package co.florlogic.finca.dispositivos.servicios.output.dto;

import java.time.Instant;
import java.util.UUID;

/** Una fila de {@code dispositivo}, sin dominio. */
public record DispositivoDto(UUID id, String estado, Instant vistoPorPrimeraVez, Instant cambiadoEn, String cambiadoPor) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor y un método de lectura por componente (id(),
 *   estado()…), además de equals, hashCode y toString. El estado va como texto, igual que en la columna.
 */
