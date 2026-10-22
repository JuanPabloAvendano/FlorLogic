package co.florlogic.finca.ingesta.servicios.input.port;

import co.florlogic.finca.ingesta.servicios.input.dto.ParticionRecibidaRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.RecibirParticionEntradaDto;

/**
 * Puerto de entrada: recibir una partición que mandó un celular (plantilla endpoint T4: un puerto por
 * caso de uso, nombrado por su intención). Por aquí solo cruzan DTO, nunca dominio (Martin · Clean Architecture
 * p. 185): el controlador no sabe que existe {@code AnotacionRecibida}.
 */
public interface RecibirParticionPuertoEntrada {

    /** Guarda cada anotación una sola vez y dice cuántas eran nuevas y cuántas ya estaban. */
    ParticionRecibidaRespuestaDto ejecutar(RecibirParticionEntradaDto entrada);
}
