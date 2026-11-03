package co.florlogic.finca.ingesta.servicios.output.dto;

import java.util.List;
import java.util.UUID;

/**
 * Lo ya guardado de un id, para decidir reenvío o corrupta sin rehacer la anotación (A-81, A-93): su huella y su
 * resultado, con los códigos o el motivo (un reenvío repite en {@code excepciones[]} lo guardado, COH-186).
 *
 * @param resultado {@code APLICADA}, {@code MARCADA} o {@code RECHAZADA}
 */
public record GuardadaDto(UUID anotacionUuid, String huella, String resultado, List<String> codigos) {

    public GuardadaDto {
        codigos = List.copyOf(codigos);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record con constructor compacto: List.copyOf(codigos) deja una copia no modificable.
 */
