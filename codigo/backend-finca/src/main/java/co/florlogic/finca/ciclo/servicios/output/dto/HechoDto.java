package co.florlogic.finca.ciclo.servicios.output.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Una fila de {@code hecho_produccion}, sin dominio. */
public record HechoDto(String produccionId, String seccionId, String campoId, LocalDate jornada, String valor,
        String motivoAusencia, Instant capturadoEn, UUID anotacion, UUID dispositivo) {
}
