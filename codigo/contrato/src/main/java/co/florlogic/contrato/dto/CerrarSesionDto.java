package co.florlogic.contrato.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cuerpo de {@code POST /ingesta/v1/sesiones/{id}/cierre} (CT-1; recorrido de TX-1, paso 15). No lanza ni valida (A-57).
 *
 * @param completa {@code true} si el celular entregó todo lo pendiente; {@code false} deja la sesión parcial y el
 *        reintento la completa sin duplicar (TX-1.T 2)
 */
public record CerrarSesionDto(
        @JsonProperty("contrato") String contrato,
        @JsonProperty("completa") Boolean completa) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase inmutable de solo datos; Java genera constructor y lectores (contrato(), completa()).
 * - @JsonProperty("completa"): nombre del miembro en el JSON del contrato.
 * - Boolean (objeto) y no boolean: admite null; si el miembro falta, se lee igual y lo juzga el ingreso.
 */
