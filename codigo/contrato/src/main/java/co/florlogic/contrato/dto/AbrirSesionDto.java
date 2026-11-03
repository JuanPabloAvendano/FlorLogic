package co.florlogic.contrato.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cuerpo de {@code POST /ingesta/v1/sesiones}: el celular abre su sesión de sincronización (contrato parte 2;
 * IA_CONSTRUCCION-TX1 CT-1; recorrido de TX-1, paso 3). No lanza ni valida (A-57): lo decide el ingreso.
 *
 * @param contrato versión menor, p. ej. {@code "1.0"}
 * @param dispositivoUuid el celular que entrega; la persona sale de la credencial
 * @param relojMs el reloj de pared del celular al abrir; con él se mide el desfase (ADR-031)
 * @param versionConfig la versión de configuración con que capturó (ADR-029)
 */
public record AbrirSesionDto(
        @JsonProperty("contrato") String contrato,
        @JsonProperty("dispositivo_uuid") UUID dispositivoUuid,
        @JsonProperty("reloj_ms") Long relojMs,
        @JsonProperty("version_config") Integer versionConfig) {
}
