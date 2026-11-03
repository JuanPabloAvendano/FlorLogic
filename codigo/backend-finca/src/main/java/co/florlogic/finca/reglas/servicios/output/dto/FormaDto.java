package co.florlogic.finca.reglas.servicios.output.dto;

import java.util.List;

/** La forma de un tipo, como la necesita {@code reglas}. */
public record FormaDto(List<String> campos, String campoMotivoAusencia) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, campos(), campoMotivoAusencia(), equals, hashCode y
 *   toString. A diferencia de los demás DTO, no copia la lista en un constructor compacto: guarda la que le pasen.
 */
