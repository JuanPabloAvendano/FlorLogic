package co.florlogic.finca.ingesta.servicios.input.dto.solicitud;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Lo que entra por el puerto: la sesión, quién entrega y sus anotaciones, ya traducidas del JSON del contrato (T4).
 * Estructura simple, sin dominio (Martin · Clean Architecture p. 185): el ensamblador de entrada la convierte en
 * {@code AnotacionRecibida}.
 *
 * @param persona quien entrega, sacada de la credencial (contrato A-51)
 */
public record RecibirParticionSolicitudDto(UUID sesionId, String persona, List<Anotacion> anotaciones) {

    public RecibirParticionSolicitudDto {
        Objects.requireNonNull(sesionId, "sesionId");
        Objects.requireNonNull(persona, "persona");
        anotaciones = List.copyOf(anotaciones);
    }

    /**
     * Una anotación de la partición.
     *
     * @param contenidoJson la anotación completa en JSON equivalente (A-59)
     */
    public record Anotacion(UUID anotacionUuid, long consecutivo, String contenidoJson) {
    }
}
