package co.florlogic.finca.ciclo.servicios.output.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Una fila de {@code hecho_produccion}, sin dominio. */
public record HechoDto(String produccionId, String seccionId, String campoId, LocalDate jornada, String valor,
        String motivoAusencia, Instant capturadoEn, UUID anotacion, UUID dispositivo) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor con los nueve componentes y un método de lectura por
 *   cada uno (produccionId(), jornada()…), además de equals, hashCode y toString. Es la forma de la fila de
 *   hecho_produccion sin lógica.
 */
