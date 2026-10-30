package co.florlogic.finca.ingesta.servicios.input.assembler;

import java.util.List;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.servicios.input.dto.request.RecibirParticionRequest;
import co.florlogic.finca.ingesta.servicios.input.dto.response.RecibirParticionResponse;

/**
 * Ensamblador de entrada: solo traduce, request → dominio y dominio → response (Fowler · PoEAA secc. 100, Assembler;
 * A-84). Así ni el DTO depende del dominio ni el dominio del DTO. No orquesta ni decide: eso es del adaptador, del caso
 * de uso y del dominio. Sin estado, por eso son métodos estáticos y no un bean [sin fuente].
 */
public final class RecibirParticionInputAssembler {

    private RecibirParticionInputAssembler() {
    }

    /** Cada anotación del request, con la sesión de la ruta (OBS-2-10). */
    public static List<AnotacionRecibida> aDominio(RecibirParticionRequest request) {
        return request.anotaciones().stream()
                .map(a -> new AnotacionRecibida(a.anotacionUuid(), a.consecutivo(), request.sesionId(), a.contenidoJson()))
                .toList();
    }

    public static RecibirParticionResponse aResponse(RecepcionDeParticionValor recepcion) {
        return new RecibirParticionResponse(
                recepcion.recibidas(), recepcion.aplicadas(), recepcion.sinEfecto(), recepcion.mayorConsecutivo());
    }
}
