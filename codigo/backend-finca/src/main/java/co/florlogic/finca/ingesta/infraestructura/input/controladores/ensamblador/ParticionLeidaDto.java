package co.florlogic.finca.ingesta.infraestructura.input.controladores.ensamblador;

import java.util.Objects;
import java.util.UUID;

import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.RecibirParticionSolicitudDto;

/**
 * Una partición que pasó la barrera del contrato: la solicitud para el puerto y el id de la partición, que el puerto no
 * necesita pero la respuesta del contrato sí devuelve.
 */
public record ParticionLeidaDto(UUID particionUuid, RecibirParticionSolicitudDto solicitud) {

    public ParticionLeidaDto {
        Objects.requireNonNull(particionUuid, "particionUuid");
        Objects.requireNonNull(solicitud, "solicitud");
    }
}
