package co.florlogic.finca.ingesta.servicios.input.ensamblador;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import co.florlogic.finca.ingesta.dominio.comun.AnotacionId;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.dominio.recepcion.ResultadoDeRecepcion;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.RecibirParticionSolicitudDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionResultadoDto;

/**
 * Ensamblador de entrada: solo traduce, solicitud → dominio y dominio → respuesta (Fowler · PoEAA secc. 100, Assembler;
 * A-84). Así ni el DTO depende del dominio ni el dominio del DTO. No orquesta ni decide: eso es del adaptador, del caso
 * de uso y del dominio. Sin estado, por eso son métodos estáticos y no un bean [sin fuente].
 */
public final class RecibirParticionEnsambladorEntrada {

    private RecibirParticionEnsambladorEntrada() {
    }

    /**
     * Cada anotación de la solicitud, con la sesión de la ruta (OBS-2-10), ya como dominio. Vacío, sin lanzar, si
     * alguna trae un id que no es UUID v7 o un consecutivo menor que 1: el dominio la rechaza y la partición entera es
     * {@code 422} (A-108, C-105). La regla vive una sola vez, en {@code AnotacionId} y {@code ConsecutivoValor}.
     */
    public static Optional<List<AnotacionRecibida>> aDominio(RecibirParticionSolicitudDto solicitud) {
        List<AnotacionRecibida> anotaciones = new ArrayList<>();
        for (RecibirParticionSolicitudDto.Anotacion a : solicitud.anotaciones()) {
            Optional<AnotacionId> id = AnotacionId.de(a.anotacionUuid());
            Optional<ConsecutivoValor> consecutivo = ConsecutivoValor.de(a.consecutivo());
            if (id.isEmpty() || consecutivo.isEmpty()) {
                return Optional.empty();
            }
            anotaciones.add(new AnotacionRecibida(id.get(), consecutivo.get(), solicitud.sesionId(), a.contenidoJson()));
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
        return new RecibirParticionRespuestaDto(
                recepcion.recibidas(), recepcion.aplicadas(), recepcion.sinEfecto(), recepcion.confirmadoHasta());
    }
}
