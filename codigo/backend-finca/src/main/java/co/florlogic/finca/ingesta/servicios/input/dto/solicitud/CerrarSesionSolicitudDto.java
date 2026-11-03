package co.florlogic.finca.ingesta.servicios.input.dto.solicitud;

import java.util.UUID;

/** Lo que entra para cerrar una sesión. */
public record CerrarSesionSolicitudDto(UUID sesionId, String persona, boolean completa) {
}
