package co.florlogic.finca.bitacora.servicios.output.dto;

import java.time.Instant;

/** Una fila de {@code bitacora}, sin dominio. */
public record EntradaDto(Instant momento, String actor, String evento, String objeto, String detalle,
        String hashAnterior, String hash) {
}
