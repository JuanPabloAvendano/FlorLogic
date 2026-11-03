package co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos;

import java.time.Instant;

/** Puerto de salida: la hora del nodo de finca (ADR-031). Un puerto y no {@code Instant.now()}: las pruebas lo fijan. */
public interface Reloj {

    Instant ahora();
}
