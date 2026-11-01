package co.florlogic.finca.ingesta.servicios.output.ensamblador;

import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDto;

/**
 * Ensamblador de salida: convierte el dominio en el DTO que cruza el puerto de salida (Fowler · PoEAA secc. 100,
 * Assembler; A-84). Sin estado, por eso es un método estático y no un bean [sin fuente].
 */
public final class AnotacionRecibidaEnsambladorSalida {

    private AnotacionRecibidaEnsambladorSalida() {
    }

    public static AnotacionRecibidaDto aSalida(AnotacionRecibida anotacion) {
        return new AnotacionRecibidaDto(
                anotacion.anotacionId().valor(), anotacion.consecutivo().valor(), anotacion.sesionId(), anotacion.contenidoJson());
    }
}
