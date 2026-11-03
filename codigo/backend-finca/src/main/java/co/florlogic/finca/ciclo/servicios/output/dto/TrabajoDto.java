package co.florlogic.finca.ciclo.servicios.output.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Un trabajo del gerente: una anotación marcada que tiene que revisar (P-S01). */
public record TrabajoDto(UUID anotacion, UUID dispositivo, String seccionId, LocalDate jornada, List<String> codigos,
        Instant abiertoEn) {

    public TrabajoDto {
        codigos = List.copyOf(codigos);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera constructor, métodos de lectura, equals, hashCode y toString.
 * - public TrabajoDto { codigos = List.copyOf(codigos); }: constructor compacto; guarda una copia inmutable de los
 *   códigos para que la lista no cambie después de creado el DTO.
 */
