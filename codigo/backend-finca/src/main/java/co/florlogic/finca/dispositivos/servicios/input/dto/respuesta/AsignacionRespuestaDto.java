package co.florlogic.finca.dispositivos.servicios.input.dto.respuesta;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Lo que le toca a un celular en una jornada: sus secciones por sus campos (CT-13). */
public record AsignacionRespuestaDto(LocalDate jornada, UUID dispositivo, List<String> secciones, List<String> campos,
        int celdas) {

    public AsignacionRespuestaDto {
        secciones = List.copyOf(secciones);
        campos = List.copyOf(campos);
    }
}
