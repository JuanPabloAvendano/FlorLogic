package co.florlogic.finca.ingesta.servicios.input.dto.solicitud;

import java.util.UUID;

/** Lo que entra para abrir una sesión: el celular, quién entrega (de la credencial), su reloj y su versión de configuración. */
public record AbrirSesionSolicitudDto(UUID dispositivoId, String persona, long relojMs, int versionConfig) {
}
