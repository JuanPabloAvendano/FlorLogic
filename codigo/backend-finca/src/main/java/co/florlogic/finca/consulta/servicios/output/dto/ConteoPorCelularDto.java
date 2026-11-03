package co.florlogic.finca.consulta.servicios.output.dto;

import java.util.UUID;

/** Los conteos de un celular en una jornada, tal como salen de la base. */
public record ConteoPorCelularDto(UUID dispositivo, int esperado, int recibido, int marcadas, int rechazadas,
        int parciales) {
}
