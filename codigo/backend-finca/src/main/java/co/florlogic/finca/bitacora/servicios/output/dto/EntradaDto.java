package co.florlogic.finca.bitacora.servicios.output.dto;

import java.time.Instant;

/** Una fila de {@code bitacora}, sin dominio. */
public record EntradaDto(Instant momento, String actor, String evento, String objeto, String detalle,
        String hashAnterior, String hash) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, los accesores (sin «get»: momento(), hashAnterior()…), equals, hashCode y toString a partir de la lista de componentes.
 */
