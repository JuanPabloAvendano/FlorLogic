package co.florlogic.finca.ciclo.servicios.output.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Un trabajo del gerente: una anotación marcada que tiene que revisar (P-S01). */
public record TrabajoDto(UUID anotacion, UUID dispositivo, String seccionId, LocalDate jornada, List<String> codigos,
        Instant abiertoEn) {

    public TrabajoDto {
        codigos = List.copyOf(codigos);
    }
}
