package co.florlogic.finca.ingesta.servicios.input.ports;

import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.RecibirParticionSolicitudDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionResultadoDto;

/**
 * Puerto de entrada: recibir una partición que mandó un celular (plantilla endpoint T4: un puerto por
 * caso de uso, nombrado por su intención). Por aquí solo cruzan DTO, nunca dominio (Martin · Clean Architecture
 * p. 185): el controlador no sabe que existe {@code AnotacionRecibida}.
 */
public interface RecibirParticionInputPort {

    /**
     * Guarda cada anotación una sola vez, mueve el avance del celular y dice cuántas eran nuevas y cuántas ya estaban.
     * Si el dominio rechaza alguna anotación, la partición entera es {@code 422} y nada se guarda (A-108); si la sesión
     * no está abierta o es de otra persona, tampoco (CT-1).
     */
    RecibirParticionResultadoDto ejecutar(RecibirParticionSolicitudDto solicitud);
}
