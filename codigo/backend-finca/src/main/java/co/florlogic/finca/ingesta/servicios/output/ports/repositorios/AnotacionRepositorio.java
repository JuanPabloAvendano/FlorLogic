package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDTO;

/** Puerto de salida: dónde quedan las anotaciones recibidas (plantilla adaptador-persistencia 10.1). */
public interface AnotacionRepositorio {

    /**
     * Guarda la anotación si su {@code anotacionUuid} no existe todavía.
     *
     * @return {@code true} si la guardó; {@code false} si ya estaba (reenvío)
     */
    boolean guardarSiNueva(AnotacionRecibidaDTO anotacion);
}
