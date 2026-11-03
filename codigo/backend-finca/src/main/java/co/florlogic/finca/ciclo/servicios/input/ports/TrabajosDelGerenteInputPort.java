package co.florlogic.finca.ciclo.servicios.input.ports;

import java.util.List;
import java.util.UUID;

import co.florlogic.finca.ciclo.servicios.input.dto.respuesta.TrabajoRespuestaDto;

/** Puerto de entrada de la web del gerente (F4): su cola de trabajo (P-S01). */
public interface TrabajosDelGerenteInputPort {

    List<TrabajoRespuestaDto> abiertos();

    /** {@code false} si no existe o ya estaba resuelto. */
    boolean resolver(UUID anotacion, String gerente);
}
