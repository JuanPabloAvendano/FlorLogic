package co.florlogic.finca.consulta.servicios.input.ports;

import java.time.LocalDate;

import co.florlogic.finca.consulta.servicios.input.dto.respuesta.TableroRespuestaDto;

/** Puerto de entrada del tablero del gerente (F4, CT-13). */
public interface TableroInputPort {

    TableroRespuestaDto de(LocalDate jornada);

    /** La jornada de hoy en la zona de la finca. */
    LocalDate hoy();
}
