package co.florlogic.finca.ingesta.servicios.output.adapter;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaSalidaDto;

/**
 * Ensamblador de salida: convierte el dominio en el DTO que cruza el puerto de salida (Fowler · PoEAA secc. 100,
 * Assembler). Sin estado, por eso es un método estático y no un bean [sin fuente].
 */
public final class AnotacionRecibidaEnsambladorSalida {

    private AnotacionRecibidaEnsambladorSalida() {
    }

    public static AnotacionRecibidaSalidaDto aSalida(AnotacionRecibida anotacion) {
        return new AnotacionRecibidaSalidaDto(
                anotacion.anotacionUuid(), anotacion.consecutivo(), anotacion.sesionId(), anotacion.contenidoJson());
    }
}
