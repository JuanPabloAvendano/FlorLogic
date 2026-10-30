package co.florlogic.finca.ingesta.servicios.output.assembler;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDTO;

/**
 * Ensamblador de salida: convierte el dominio en el DTO que cruza el puerto de salida (Fowler · PoEAA secc. 100,
 * Assembler; A-84). Sin estado, por eso es un método estático y no un bean [sin fuente].
 */
public final class AnotacionRecibidaOutputAssembler {

    private AnotacionRecibidaOutputAssembler() {
    }

    public static AnotacionRecibidaDTO aSalida(AnotacionRecibida anotacion) {
        return new AnotacionRecibidaDTO(
                anotacion.anotacionUuid(), anotacion.consecutivo(), anotacion.sesionId(), anotacion.contenidoJson());
    }
}
