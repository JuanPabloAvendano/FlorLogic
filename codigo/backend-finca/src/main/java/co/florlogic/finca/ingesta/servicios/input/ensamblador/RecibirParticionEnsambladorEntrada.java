package co.florlogic.finca.ingesta.servicios.input.ensamblador;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import co.florlogic.finca.ingesta.dominio.comun.AnotacionId;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.JuicioDeAnotacion;
import co.florlogic.finca.ingesta.dominio.recepcion.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.dominio.recepcion.ResultadoDeRecepcion;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.RecibirParticionSolicitudDto;

/**
 * Ensamblador de entrada: solo traduce, solicitud → dominio y dominio → respuesta (Fowler · PoEAA secc. 100, Assembler;
 * A-84). No orquesta ni decide. Sin estado, por eso son métodos estáticos y no un bean.
 */
public final class RecibirParticionEnsambladorEntrada {

    private RecibirParticionEnsambladorEntrada() {
    }

    /**
     * Cada anotación de la solicitud ya como dominio. Vacío, sin lanzar, si alguna trae un id que no es UUID v7 o un
     * consecutivo menor que 1: el dominio la rechaza y la partición entera es {@code 422} (A-108, C-105).
     */
    public static Optional<List<AnotacionRecibida>> aDominio(RecibirParticionSolicitudDto solicitud) {
        List<AnotacionRecibida> anotaciones = new ArrayList<>();
        for (RecibirParticionSolicitudDto.Anotacion a : solicitud.anotaciones()) {
            Optional<AnotacionId> id = AnotacionId.de(a.anotacionUuid());
            Optional<ConsecutivoValor> consecutivo = ConsecutivoValor.de(a.consecutivo());
            if (id.isEmpty() || consecutivo.isEmpty() || a.tipo() == null) {
                return Optional.empty();
            }
            anotaciones.add(new AnotacionRecibida(id.get(), consecutivo.get(), a.tipo(), a.versionTipo(),
                    a.marcadaEnElCelular(), a.contenidoJson()));
        }
        return Optional.of(List.copyOf(anotaciones));
    }

    public static RecibirParticionResultadoDto aResultado(ResultadoDeRecepcion resultado) {
        return switch (resultado) {
            case ResultadoDeRecepcion.Recibida r -> new RecibirParticionResultadoDto.Recibida(aRespuesta(r.recepcion()));
            case ResultadoDeRecepcion.SesionDesconocida s -> new RecibirParticionResultadoDto.SesionDesconocida();
            case ResultadoDeRecepcion.SesionAjena s -> new RecibirParticionResultadoDto.SesionAjena();
        };
    }

    public static RecibirParticionRespuestaDto aRespuesta(RecepcionDeParticionValor recepcion) {
        return new RecibirParticionRespuestaDto(recepcion.recibidas(), recepcion.aplicadas(), recepcion.marcadas(),
                recepcion.rechazadas(), recepcion.sinEfecto(), recepcion.confirmadoHasta(),
                recepcion.excepciones().stream().map(RecibirParticionEnsambladorEntrada::aExcepcion).toList());
    }

    private static RecibirParticionRespuestaDto.Excepcion aExcepcion(JuicioDeAnotacion juicio) {
        List<String> codigos = juicio.motivo().map(m -> List.of(minuscula(m.name())))
                .orElseGet(() -> juicio.codigos().stream().map(c -> minuscula(c.name())).sorted().toList());
        return new RecibirParticionRespuestaDto.Excepcion(juicio.anotacion().valor(), minuscula(juicio.resultado().name()),
                codigos);
    }

    private static String minuscula(String nombre) {
        return nombre.toLowerCase(Locale.ROOT);
    }
}
