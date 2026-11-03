package co.florlogic.contrato.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cuándo vio el nodo de finca por primera vez una anotación que se reenvía tras una restauración
 * (A-45 ⟦2⟧b). Va fuera de la anotación porque la anotación es inmutable (A-49 ⟦3⟧).
 *
 * @param primerAcuseMs el {@code recibido_ms} de aquella respuesta, copiado tal cual: reloj del
 *     nodo de finca, nunca del celular
 */
public record PrimerAcuseDto(
        @JsonProperty("anotacion_uuid") UUID anotacionUuid,
        @JsonProperty("primer_acuse_ms") long primerAcuseMs) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase inmutable de solo datos; Java genera constructor y lectores.
 * - @JsonProperty("primer_acuse_ms"): nombre del miembro en el JSON del contrato.
 * - long primitivo: no admite null; con el lector estricto, un null aquí es un fallo de lectura.
 */
