package co.florlogic.finca.ingesta.servicios.input.dto.respuesta;

import java.util.List;
import java.util.UUID;

/** Lo que sale al cerrar una sesión (recorrido de TX-1, paso 15). @param estado {@code cerrada} o {@code parcial} */
public record SesionCerradaRespuestaDto(UUID sesionId, String estado, long confirmadoHasta, int versionConfig,
        List<String> avisos) {

    public SesionCerradaRespuestaDto {
        avisos = List.copyOf(avisos);
    }
}
