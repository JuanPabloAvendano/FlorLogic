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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record con constructor compacto: Objects.requireNonNull lanza NullPointerException si falta el id de la
 *   partición o la solicitud.
 */
