package co.florlogic.finca.dispositivos.servicios.output.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Una fila de {@code asignacion}, sin dominio. */
public record AsignacionDto(LocalDate jornada, UUID dispositivo, String seccion, String campo, Instant asignadoEn,
        String asignadoPor) {
}
