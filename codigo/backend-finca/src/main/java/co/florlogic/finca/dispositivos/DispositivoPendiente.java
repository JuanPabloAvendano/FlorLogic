package co.florlogic.finca.dispositivos;

import java.util.UUID;

/** Evento: un celular desconocido abrió sesión y espera aprobación (CT-10). Lo escucha {@code notificacion}. */
public record DispositivoPendiente(UUID dispositivo) {
}
