package co.florlogic.finca.consulta.servicios.output.ports.repositorios;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import co.florlogic.finca.consulta.servicios.output.dto.ConteoPorCelularDto;
import co.florlogic.finca.consulta.servicios.output.dto.ConteoPorSeccionDto;

/**
 * Puerto de salida de solo lectura: los conteos del tablero (CT-13). Lee lo que escriben otros módulos (asignación,
 * hechos, trabajos, rechazadas, sesiones) sin pasar por su código: es una consulta, no cambia nada.
 */
public interface TableroRepositorio {

    /** @param zona en qué día cae una rechazada o una sesión, que solo tienen hora */
    List<ConteoPorCelularDto> porCelular(LocalDate jornada, ZoneId zona);

    List<ConteoPorSeccionDto> porSeccion(LocalDate jornada);
}
