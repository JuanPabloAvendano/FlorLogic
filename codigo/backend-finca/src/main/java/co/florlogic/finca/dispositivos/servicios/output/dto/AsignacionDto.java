package co.florlogic.finca.dispositivos.servicios.output.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Una fila de {@code asignacion}, sin dominio. */
public record AsignacionDto(LocalDate jornada, UUID dispositivo, String seccion, String campo, Instant asignadoEn,
        String asignadoPor) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor y un método de lectura por componente (jornada(),
 *   dispositivo()…), además de equals, hashCode y toString. Es la fila de asignacion sin lógica.
 */
