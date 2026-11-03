package co.florlogic.finca.ingesta.servicios.input.dto.solicitud;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Lo que entra por el puerto: la sesión, quién entrega y sus anotaciones, ya traducidas del JSON del contrato.
 * Estructura simple, sin dominio (Martin · Clean Architecture p. 185).
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
     * @param marcadaEnElCelular el {@code veredicto_local} fue {@code marcada}
     * @param contenidoJson la anotación completa en JSON equivalente (A-59)
     */
    public record Anotacion(UUID anotacionUuid, long consecutivo, String tipo, int versionTipo,
            boolean marcadaEnElCelular, String contenidoJson) {
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record con constructor compacto: Objects.requireNonNull exige sesión y persona, y List.copyOf deja la lista de
 *   anotaciones no modificable.
 * - public record Anotacion(...) anidado: una anotación ya traducida del JSON, todavía sin validar como dominio.
 */
