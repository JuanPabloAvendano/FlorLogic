package co.florlogic.finca.ingesta.servicios.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.comun.AnotacionId;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.dominio.recepcion.ResultadoDeRecepcion;
import co.florlogic.finca.ingesta.servicios.output.dto.SesionDto;
import co.florlogic.finca.ingesta.servicios.output.dto.VeredictoDto;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AnotacionRepositorioDobleEnMemoria;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AvanceRepositorioDobleEnMemoria;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.SesionRepositorio;

/**
 * El caso de uso con dobles en memoria: sesión abierta y de quien entrega, idempotencia por huella (ADR-027), juicio de
 * las reglas, avance real y marca de hueco (Segmento 1b + F1).
 */
class RecibirParticionCasoUsoImplTest {

    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");
    private static final UUID CELULAR = UUID.fromString("0192f0a0-0000-7000-8000-0000000000e1");
    private static final String PERSONA = "op-07";

    private final AnotacionRepositorioDobleEnMemoria anotaciones = new AnotacionRepositorioDobleEnMemoria();
    private final AvanceRepositorioDobleEnMemoria avances = new AvanceRepositorioDobleEnMemoria();
    private final Map<UUID, SesionDto> sesionesGuardadas = new ConcurrentHashMap<>();
    private final SesionRepositorio sesiones = new SesionRepositorio() {
        @Override
        public Optional<SesionDto> de(UUID id) {
            return Optional.ofNullable(sesionesGuardadas.get(id));
        }

        @Override
        public void guardar(SesionDto sesion) {
            sesionesGuardadas.put(sesion.sesionId(), sesion);
        }
    };
    // Huella de prueba: el contenido en hexadecimal, repetido hasta 64; basta para que igual = igual y distinto = distinto.
    private final RecibirParticionCasoUsoImpl servicio = new RecibirParticionCasoUsoImpl(sesiones, anotaciones, avances,
            json -> String.format("%064x", new java.math.BigInteger(1, json.getBytes())).substring(0, 64),
            contenido -> contenido.contenidoJson().contains("\"tipo_raro\"")
                    ? new VeredictoDto(List.of(), Optional.of("tipo_desconocido"))
                    : contenido.contenidoJson().contains("\"sin_valor\"")
                            ? new VeredictoDto(List.of("incompleta", "motivo_ausente"), Optional.empty())
                            : new VeredictoDto(List.of(), Optional.empty()),
            (actor, evento, objeto, detalle) -> { }, evento -> { });

    @BeforeEach
    void sesionAbierta() {
        sesiones.guardar(new SesionDto(SESION, CELULAR, PERSONA, Instant.EPOCH, 0, 12, true, "ABIERTA", null));
    }

    @Test
    void las_nuevas_se_aplican_y_se_guardan() {
        RecepcionDeParticionValor r = recibir(anotacion("d001", 1, "a"), anotacion("d002", 2, "b"));

        assertThat(r).isEqualTo(new RecepcionDeParticionValor(2, 2, 0, 0, 0, 2, List.of()));
        assertThat(anotaciones.cuantas()).isEqualTo(2);
    }

    @Test
    void el_mismo_id_con_la_misma_huella_es_sin_efecto() {
        recibir(anotacion("d001", 1, "a"));

        assertThat(recibir(anotacion("d001", 1, "a")).sinEfecto()).isEqualTo(1);
        assertThat(anotaciones.cuantas()).isEqualTo(1);
    }

    @Test
    void el_mismo_id_con_otra_huella_es_corrupta_y_se_guarda_aparte() {
        recibir(anotacion("d001", 1, "a"));

        RecepcionDeParticionValor r = recibir(anotacion("d001", 1, "OTRO"));

        assertThat(r.rechazadas()).isEqualTo(1);
        assertThat(r.excepciones()).singleElement().satisfies(e -> assertThat(e.motivo()).isPresent());
        assertThat(anotaciones.cuantas()).isEqualTo(1);
        assertThat(anotaciones.rechazadas()).isEqualTo(1);
    }

    @Test
    void lo_que_las_reglas_marcan_entra_marcado_y_lo_que_rechazan_va_aparte() {
        RecepcionDeParticionValor r = recibir(anotacion("d001", 1, "sin_valor"), anotacion("d002", 2, "tipo_raro"));

        assertThat(r.aplicadas()).isEqualTo(1);
        assertThat(r.marcadas()).isEqualTo(1);
        assertThat(r.rechazadas()).isEqualTo(1);
        assertThat(r.excepciones()).hasSize(2);
    }

    @Test
    void un_rechazo_reenviado_se_vuelve_a_ver_como_rechazo() {
        recibir(anotacion("d002", 1, "tipo_raro"));

        assertThat(recibir(anotacion("d002", 1, "tipo_raro")).rechazadas()).isEqualTo(1);
        assertThat(anotaciones.rechazadas()).isEqualTo(1);
    }

    @Test
    void lo_que_llega_despues_de_un_hueco_queda_marcado_y_el_avance_espera() {
        // C-104 y A-72: 1 y 2, después 4 (marcada por hueco), después 3 → confirmado 4.
        assertThat(recibir(anotacion("d001", 1, "a"), anotacion("d002", 2, "b")).confirmadoHasta()).isEqualTo(2);
        RecepcionDeParticionValor conHueco = recibir(anotacion("d004", 4, "d"));
        assertThat(conHueco.confirmadoHasta()).isEqualTo(2);
        assertThat(conHueco.marcadas()).isEqualTo(1);
        assertThat(recibir(anotacion("d003", 3, "c")).confirmadoHasta()).isEqualTo(4);
    }

    @Test
    void el_orden_dentro_de_la_particion_no_cambia_el_juicio() {
        RecepcionDeParticionValor r = recibir(anotacion("d002", 2, "b"), anotacion("d001", 1, "a"));

        assertThat(r.confirmadoHasta()).isEqualTo(2);
        assertThat(r.marcadas()).isZero();
    }

    @Test
    void sin_sesion_abierta_o_de_otra_persona_no_se_recibe_nada() {
        assertThat(servicio.ejecutar(UUID.randomUUID(), PERSONA, List.of(anotacion("d001", 1, "a"))))
                .isInstanceOf(ResultadoDeRecepcion.SesionDesconocida.class);
        assertThat(servicio.ejecutar(SESION, "otra-persona", List.of(anotacion("d001", 1, "a"))))
                .isInstanceOf(ResultadoDeRecepcion.SesionAjena.class);
        assertThat(anotaciones.cuantas()).isZero();
    }

    private RecepcionDeParticionValor recibir(AnotacionRecibida... recibidas) {
        return ((ResultadoDeRecepcion.Recibida) servicio.ejecutar(SESION, PERSONA, List.of(recibidas))).recepcion();
    }

    private static AnotacionRecibida anotacion(String sufijo, long consecutivo, String contenido) {
        UUID uuid = UUID.fromString("0192f0a0-2000-7000-8000-00000000" + sufijo);
        return new AnotacionRecibida(new AnotacionId(uuid), new ConsecutivoValor(consecutivo), "captura.campo", 1, false,
                "{\"c\":\"" + contenido + "\"}");
    }
}
