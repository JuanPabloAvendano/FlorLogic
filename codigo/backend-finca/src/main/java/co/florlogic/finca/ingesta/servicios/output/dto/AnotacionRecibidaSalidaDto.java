package co.florlogic.finca.ingesta.servicios.output.dto;

import java.util.UUID;

/**
 * Lo que la persistencia necesita de una anotación recibida, distinto al DTO de entrada: no cruza el dominio hacia
 * afuera (Martin · Clean Architecture p. 195, «we don't want to cheat and pass Entity objects»).
 *
 * @param contenidoJson la anotación completa en JSON equivalente (A-59); se guarda como {@code jsonb}
 */
public record AnotacionRecibidaSalidaDto(UUID anotacionUuid, long consecutivo, UUID sesionId, String contenidoJson) {
}
