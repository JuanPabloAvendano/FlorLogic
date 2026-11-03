package co.florlogic.finca.ingesta.servicios.casosuso.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.ingesta.AnotacionAceptada;
import co.florlogic.finca.ingesta.dominio.avance.AvanceDelDispositivoAgregado;
import co.florlogic.finca.ingesta.dominio.avance.LlegadaAlAvance;
import co.florlogic.finca.ingesta.dominio.comun.CodigoMarca;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;
import co.florlogic.finca.ingesta.dominio.comun.HuellaValor;
import co.florlogic.finca.ingesta.dominio.comun.MotivoRechazo;
import co.florlogic.finca.ingesta.dominio.comun.Resultado;
import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.IdYaRecibido;
import co.florlogic.finca.ingesta.dominio.recepcion.JuicioDeAnotacion;
import co.florlogic.finca.ingesta.dominio.recepcion.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.dominio.recepcion.ResultadoDeRecepcion;
import co.florlogic.finca.ingesta.dominio.recepcion.Veredicto;
import co.florlogic.finca.ingesta.dominio.sesion.SesionDeSincronizacion;
import co.florlogic.finca.ingesta.servicios.casosuso.RecibirParticionCasoUso;
import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDto;
import co.florlogic.finca.ingesta.servicios.output.dto.GuardadaDto;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.AnotacionRecibidaEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.AvanceEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.SesionEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.VeredictoEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AnotacionRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AvanceRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.SesionRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.Auditoria;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.CalculadorDeHuella;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.EvaluadorDeAnotaciones;

/**
 * Recibe una partición de una sesión abierta y juzga cada anotación (ESPEC-dominio-ingesta §5 «Flujo del caso de
 * uso»; recorrido de TX-1, pasos 8 a 14). Todo en una transacción: si la base falla a mitad o otra partición del
 * mismo celular ganó primero ({@code ChoqueConcurrente}, A-98), no queda nada a medias y el celular reenvía entera.
 *
 * <p>Por anotación, en orden de consecutivo (A-71): huella (A-106) → ¿el id ya llegó? (misma huella = reenvío, otra =
 * corrupta, ADR-027) → si es nueva, las reglas (A-99) → el avance se mueve con toda llegada (A-73) y una que deja hueco
 * queda marcada (A-72). Solo conoce puertos y ensambladores (Martin · Clean Architecture p. 195).
 */
@Service
public class RecibirParticionCasoUsoImpl implements RecibirParticionCasoUso {

    private final SesionRepositorio sesiones;
    private final AnotacionRepositorio anotaciones;
    private final AvanceRepositorio avances;
    private final CalculadorDeHuella huellas;
    private final EvaluadorDeAnotaciones reglas;
    private final Auditoria auditoria;
    private final ApplicationEventPublisher eventos;

    public RecibirParticionCasoUsoImpl(SesionRepositorio sesiones, AnotacionRepositorio anotaciones,
            AvanceRepositorio avances, CalculadorDeHuella huellas, EvaluadorDeAnotaciones reglas, Auditoria auditoria,
            ApplicationEventPublisher eventos) {
        this.sesiones = sesiones;
        this.anotaciones = anotaciones;
        this.avances = avances;
        this.huellas = huellas;
        this.reglas = reglas;
        this.auditoria = auditoria;
        this.eventos = eventos;
    }

    @Override
    @Transactional
    public ResultadoDeRecepcion ejecutar(UUID sesionId, String persona, List<AnotacionRecibida> recibidas) {
        Optional<SesionDeSincronizacion> sesion = sesiones.de(sesionId).flatMap(SesionEnsambladorSalida::aDominio)
                .filter(SesionDeSincronizacion::abierta);
        if (sesion.isEmpty()) {
            return new ResultadoDeRecepcion.SesionDesconocida();
        }
        if (!sesion.get().esDe(persona)) {
            return new ResultadoDeRecepcion.SesionAjena();
        }
        AvanceDelDispositivoAgregado avance = avanceDe(sesion.get().dispositivo());
        Set<CodigoMarca> deLaSesion = sesion.get().dispositivoRegistrado()
                ? Set.of()
                : Set.of(CodigoMarca.DISPOSITIVO_NO_REGISTRADO);
        Map<UUID, GuardadaDto> guardadas = anotaciones.guardadasCon(
                recibidas.stream().map(a -> a.anotacionId().valor()).toList());
        Map<UUID, String> vistas = new HashMap<>();
        List<JuicioDeAnotacion> juicios = new ArrayList<>();

        // Orden estable por consecutivo (A-71): con [2, 1] el juicio es el mismo que con [1, 2].
        List<AnotacionRecibida> enOrden = recibidas.stream()
                .sorted(Comparator.comparing(AnotacionRecibida::consecutivo)).toList();
        for (AnotacionRecibida anotacion : enOrden) {
            UUID id = anotacion.anotacionId().valor();
            String huella = huellas.de(anotacion.contenidoJson());
            Optional<GuardadaDto> guardada = Optional.ofNullable(guardadas.get(id));
            Optional<HuellaValor> anterior = Optional.ofNullable(vistas.get(id))
                    .or(() -> guardada.map(GuardadaDto::huella))
                    .map(RecibirParticionCasoUsoImpl::huella);
            LlegadaAlAvance llegada = avance.registrar(anotacion.consecutivo());

            JuicioDeAnotacion juicio = switch (IdYaRecibido.ante(anterior, huella(huella)).orElse(null)) {
                case REENVIO -> reenvio(anotacion, guardada, vistas.containsKey(id));
                case CORRUPTA -> {
                    auditoria.registrar(persona, "anotacion_corrupta", id.toString(), Map.of("sesion", sesionId.toString()));
                    yield rechazar(anotacion, sesionId, huella, MotivoRechazo.CORRUPTA);
                }
                case null -> juzgarNueva(anotacion, sesion.get(), huella, llegada, deLaSesion);
            };
            vistas.put(id, huella);
            juicios.add(juicio);
        }
        avances.guardar(AvanceEnsambladorSalida.aDto(avance));
        RecepcionDeParticionValor recepcion = contar(recibidas.size(), juicios, avance);
        auditoria.registrar(persona, "particion_recibida", sesionId.toString(), Map.of(
                "recibidas", Integer.toString(recepcion.recibidas()), "aplicadas", Integer.toString(recepcion.aplicadas()),
                "marcadas", Integer.toString(recepcion.marcadas()), "rechazadas", Integer.toString(recepcion.rechazadas()),
                "sin_efecto", Integer.toString(recepcion.sinEfecto())));
        return new ResultadoDeRecepcion.Recibida(recepcion);
    }

    private JuicioDeAnotacion juzgarNueva(AnotacionRecibida anotacion, SesionDeSincronizacion sesion, String huella,
            LlegadaAlAvance llegada, Set<CodigoMarca> deLaSesion) {
        UUID sesionId = sesion.id().valor();
        Veredicto veredicto = VeredictoEnsambladorSalida.aDominio(reglas.evaluar(VeredictoEnsambladorSalida.aDto(anotacion)));
        if (veredicto instanceof Veredicto.Rechazo rechazo) {
            return rechazar(anotacion, sesionId, huella, rechazo.motivo());
        }
        Set<CodigoMarca> marcas = EnumSet.noneOf(CodigoMarca.class);
        if (veredicto instanceof Veredicto.Marca marca) {
            marcas.addAll(marca.codigos());
        }
        marcas.addAll(deLaSesion);
        if (llegada == LlegadaAlAvance.EN_ESPERA || llegada == LlegadaAlAvance.FUERA_DE_VENTANA) {
            marcas.add(CodigoMarca.HUECO_CONSECUTIVO);   // A-72: se guarda, marcada; nada se pierde
        }
        JuicioDeAnotacion juicio = JuicioDeAnotacion.aplicada(anotacion.anotacionId(), marcas);
        AnotacionRecibidaDto aceptada = AnotacionRecibidaEnsambladorSalida.aAceptada(anotacion, sesionId, huella, juicio);
        anotaciones.guardarAceptada(aceptada);
        // Se entrega después del commit (CT-12): si la partición se deshace, nadie la ve.
        eventos.publishEvent(new AnotacionAceptada(anotacion.anotacionId().valor(), sesion.dispositivo().valor(),
                anotacion.tipo(), anotacion.versionTipo(), anotacion.contenidoJson(),
                aceptada.codigos(), sesion.desfaseMs()));
        return juicio;
    }

    private JuicioDeAnotacion rechazar(AnotacionRecibida anotacion, UUID sesionId, String huella, MotivoRechazo motivo) {
        JuicioDeAnotacion juicio = JuicioDeAnotacion.rechazada(anotacion.anotacionId(), motivo);
        anotaciones.guardarRechazada(AnotacionRecibidaEnsambladorSalida.aRechazada(anotacion, sesionId, huella, juicio));
        return juicio;
    }

    /** Un reenvío no cambia nada; si lo guardado era un rechazo, el celular lo vuelve a ver (COH-186). */
    private static JuicioDeAnotacion reenvio(AnotacionRecibida anotacion, Optional<GuardadaDto> guardada,
            boolean repetidaEnEstaParticion) {
        if (!repetidaEnEstaParticion && guardada.filter(g -> Resultado.RECHAZADA.name().equals(g.resultado())).isPresent()) {
            MotivoRechazo motivo = guardada.get().codigos().stream().findFirst()
                    .map(VeredictoEnsambladorSalida::motivoDe).orElse(MotivoRechazo.DATOS_INVALIDOS);
            return JuicioDeAnotacion.rechazada(anotacion.anotacionId(), motivo);
        }
        return JuicioDeAnotacion.sinEfecto(anotacion.anotacionId());
    }

    private static RecepcionDeParticionValor contar(int recibidas, List<JuicioDeAnotacion> juicios,
            AvanceDelDispositivoAgregado avance) {
        int aplicadas = 0;
        int marcadas = 0;
        int rechazadas = 0;
        int sinEfecto = 0;
        List<JuicioDeAnotacion> excepciones = new ArrayList<>();
        for (JuicioDeAnotacion juicio : juicios) {
            switch (juicio.resultado()) {
                case APLICADA -> aplicadas++;
                case MARCADA -> {
                    aplicadas++;
                    marcadas++;
                    excepciones.add(juicio);
                }
                case RECHAZADA -> {
                    rechazadas++;
                    excepciones.add(juicio);
                }
                case SIN_EFECTO -> sinEfecto++;
            }
        }
        long confirmadoHasta = avance.confirmadoHasta().map(ConsecutivoValor::valor).orElse(0L);
        return new RecepcionDeParticionValor(recibidas, aplicadas, marcadas, rechazadas, sinEfecto, confirmadoHasta,
                excepciones);
    }

    /**
     * El avance guardado del celular, o uno nuevo si nunca se recibió nada. Si lo guardado no se puede rehacer, no se
     * crea otro encima: la partición se deshace y sale el {@code 500} genérico, con el dispositivo en el log (COH-131).
     */
    private AvanceDelDispositivoAgregado avanceDe(DispositivoId dispositivo) {
        return avances.deDispositivo(dispositivo.valor())
                .map(guardado -> AvanceEnsambladorSalida.aDominio(guardado).orElseThrow(() -> new IllegalStateException(
                        "El avance guardado del dispositivo " + dispositivo.valor() + " no se puede reconstruir")))
                .orElseGet(() -> AvanceDelDispositivoAgregado.nuevo(dispositivo));
    }

    private static HuellaValor huella(String hex) {
        return new HuellaValor(1, hex);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service: Spring crea una instancia y la entrega a quien pida RecibirParticionCasoUso.
 * - @Transactional: toda la partición en una transacción; si sale una excepción no comprobada (ChoqueConcurrente,
 *   IllegalStateException, la base que cae), se deshacen anotaciones, avance y bitácora juntos.
 * - ApplicationEventPublisher.publishEvent(new AnotacionAceptada(...)): publica el evento; Spring Modulith lo anota
 *   en su registro dentro de la transacción y lo entrega a los oyentes después del commit.
 * - recibidas.stream().sorted(Comparator.comparing(AnotacionRecibida::consecutivo)).toList(): copia ordenada por
 *   consecutivo, usando el compareTo de ConsecutivoValor.
 * - Optional.ofNullable(vistas.get(id)).or(() -> guardada.map(GuardadaDto::huella)): primero la huella vista en
 *   esta misma partición; si no hay, la guardada en la base.
 * - switch (IdYaRecibido.ante(...).orElse(null)) { case REENVIO -> …; case CORRUPTA -> { …; yield …; } case null ->
 *   … }: switch sobre el enum que admite case null (id nuevo). yield devuelve el valor desde un case con bloque.
 * - EnumSet.noneOf(CodigoMarca.class): conjunto vacío y eficiente para valores de un enum.
 * - veredicto instanceof Veredicto.Rechazo rechazo: comprueba el tipo y nombra la variable en el mismo paso.
 * - Set.of() / Set.of(x) y Map.of(...): colecciones pequeñas no modificables.
 * - switch (juicio.resultado()) { case APLICADA -> aplicadas++; … }: switch con flechas, sin break, sobre el enum.
 * - .map(...).orElseThrow(() -> new IllegalStateException(...)) y .orElseGet(() -> …nuevo(...)): si hay avance
 *   guardado y no se puede rehacer, lanza (sale 500); si no hay ninguno, crea uno nuevo.
 */
