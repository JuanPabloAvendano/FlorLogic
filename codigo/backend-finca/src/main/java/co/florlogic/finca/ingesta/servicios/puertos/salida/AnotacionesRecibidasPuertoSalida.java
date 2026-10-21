package co.florlogic.finca.ingesta.servicios.puertos.salida;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;

/** Puerto de salida: dónde quedan las anotaciones recibidas (plantilla adaptador-persistencia 10.1). */
public interface AnotacionesRecibidasPuertoSalida {

    /**
     * Guarda la anotación si su {@code anotacionUuid} no existe todavía.
     *
     * @return {@code true} si la guardó; {@code false} si ya estaba (reenvío)
     */
    boolean guardarSiNueva(AnotacionRecibida anotacion);
}
