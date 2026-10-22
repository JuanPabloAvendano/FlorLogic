package co.florlogic.finca.ingesta.servicios.output.port;

import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaSalidaDto;

/** Puerto de salida: dónde quedan las anotaciones recibidas (plantilla adaptador-persistencia 10.1). */
public interface AnotacionesRecibidasPuertoSalida {

    /**
     * Guarda la anotación si su {@code anotacionUuid} no existe todavía.
     *
     * @return {@code true} si la guardó; {@code false} si ya estaba (reenvío)
     */
    boolean guardarSiNueva(AnotacionRecibidaSalidaDto anotacion);
}
