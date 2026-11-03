package co.florlogic.contrato.dto;

import java.time.Instant;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * El trabajo de una persona en campo, declarado una vez por partición (A-18). Cada anotación
 * la nombra con {@code sesion_captura_uuid}. No es la sesión de sincronización.
 *
 * @param personaId quien capturó; no necesariamente quien entrega (A-51)
 * @param credencialJti la credencial vigente al capturar; sirve para {@code capturada_sin_credencial}
 * @param versionApp las versiones de al capturar: actualizar la app abre otra sesión de captura
 */
public record SesionCapturaDto(
        @JsonProperty("uuid") UUID uuid,
        @JsonProperty("persona_id") String personaId,
        @JsonProperty("credencial_jti") String credencialJti,
        @JsonProperty("inicio") Instant inicio,
        @JsonProperty("version_app") String versionApp,
        @JsonProperty("version_motor") String versionMotor) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record con @JsonProperty: cada miembro con su nombre del contrato en snake_case.
 * - Instant (java.time): un instante en UTC; en el JSON viaja como texto ISO-8601 («2026-10-16T06:02:11Z»).
 */
