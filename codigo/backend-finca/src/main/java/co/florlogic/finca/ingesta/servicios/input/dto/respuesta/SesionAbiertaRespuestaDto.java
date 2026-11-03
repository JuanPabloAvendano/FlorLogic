package co.florlogic.finca.ingesta.servicios.input.dto.respuesta;

import java.util.UUID;

/** Lo que sale al abrir una sesión (recorrido de TX-1, paso 6). */
public record SesionAbiertaRespuestaDto(UUID sesionId, long desfaseMs, long confirmadoHasta, int versionConfig) {
}
