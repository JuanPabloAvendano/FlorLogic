package co.florlogic.finca.ingesta.servicios.input.ports;

import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.RecibirParticionSolicitudDto;

/**
 * Puerto de entrada: recibir una partición que mandó un celular (plantilla endpoint T4: un puerto por caso de uso,
 * nombrado por su intención). Por aquí solo cruzan DTO, nunca dominio (Martin · Clean Architecture p. 185).
 */
public interface RecibirParticionInputPort {

    RecibirParticionResultadoDto ejecutar(RecibirParticionSolicitudDto solicitud);
}
