package co.florlogic.contrato.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Respuesta {@code 201} de {@code POST /ingesta/v1/sesiones} (CT-1; recorrido de TX-1, paso 6).
 *
 * @param desfaseMs positivo si el reloj del celular va adelantado frente al del nodo de finca
 * @param confirmadoHasta el punto de reanudación: hasta dónde el nodo ya tiene todo sin huecos (A-31)
 * @param versionConfig la versión de configuración vigente en el nodo
 * @param marcada {@code true} si el celular no está registrado: la sesión entra marcada (ES-S13)
 */
public record SesionAbiertaDto(
        @JsonProperty("sesion_uuid") UUID sesionUuid,
        @JsonProperty("desfase_ms") long desfaseMs,
        @JsonProperty("confirmado_hasta") long confirmadoHasta,
        @JsonProperty("version_config") int versionConfig,
        @JsonProperty("marcada") boolean marcada) {
}
