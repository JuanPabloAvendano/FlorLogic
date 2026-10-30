package co.florlogic.finca.ingesta.servicios.input.dto.request;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Lo que entra por el puerto: la sesión y sus anotaciones, ya traducidas del cable (T4). Estructura simple, sin dominio
 * (Martin · Clean Architecture p. 185): el ensamblador de entrada la convierte en {@code AnotacionRecibida}.
 */
public record RecibirParticionRequest(UUID sesionId, List<Anotacion> anotaciones) {

    public RecibirParticionRequest {
        Objects.requireNonNull(sesionId, "sesionId");
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
