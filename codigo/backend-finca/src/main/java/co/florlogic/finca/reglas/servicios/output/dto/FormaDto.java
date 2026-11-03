package co.florlogic.finca.reglas.servicios.output.dto;

import java.util.List;

/** La forma de un tipo, como la necesita {@code reglas}. */
public record FormaDto(List<String> campos, String campoMotivoAusencia) {

    public FormaDto {
        campos = List.copyOf(campos);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, campos(), campoMotivoAusencia(), equals, hashCode y
 *   toString.
 * - public FormaDto { campos = List.copyOf(campos); }: constructor compacto; guarda una copia que no se puede
 *   modificar, como los demás DTO, para que quien la creó no la cambie después (y falla si llega null).
 */
