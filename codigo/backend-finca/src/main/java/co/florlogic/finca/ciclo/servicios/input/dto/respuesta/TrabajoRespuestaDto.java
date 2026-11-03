package co.florlogic.finca.ciclo.servicios.input.dto.respuesta;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Un trabajo del gerente como se ve en la web: qué anotación, de qué celular y por qué quedó marcada. */
public record TrabajoRespuestaDto(UUID anotacion, UUID dispositivo, String seccion, LocalDate jornada,
        List<String> codigos, Instant abiertoEn) {

    public TrabajoRespuestaDto {
        codigos = List.copyOf(codigos);
    }
}
