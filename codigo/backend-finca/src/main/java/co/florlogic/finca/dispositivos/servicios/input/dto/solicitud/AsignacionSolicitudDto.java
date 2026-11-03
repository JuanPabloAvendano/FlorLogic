package co.florlogic.finca.dispositivos.servicios.input.dto.solicitud;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Lo que el administrador pide asignar: un celular, una jornada, sus secciones y sus campos (CT-13). */
public record AsignacionSolicitudDto(LocalDate jornada, UUID dispositivo, List<String> secciones, List<String> campos,
        String administrador) {

    public AsignacionSolicitudDto {
        secciones = secciones == null ? List.of() : List.copyOf(secciones);
        campos = campos == null ? List.of() : List.copyOf(campos);
    }
}
