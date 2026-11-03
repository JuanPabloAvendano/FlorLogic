package co.florlogic.finca.ciclo.servicios.output.ports.repositorios;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import co.florlogic.finca.ciclo.servicios.output.dto.TrabajoDto;

/** Puerto de salida: la cola de trabajo del gerente. */
public interface TrabajoRepositorio {

    /** Abre el trabajo; si ya estaba abierto para esa anotación, no hace nada (el evento puede repetirse). */
    void abrir(TrabajoDto trabajo);

    /** Los que nadie ha resuelto, los más viejos primero, hasta {@code maximo}. */
    List<TrabajoDto> abiertos(int maximo);

    /** Lo cierra a nombre del gerente; {@code false} si no existe o ya estaba resuelto. */
    boolean resolver(UUID anotacion, String gerente, Instant cuando);
}
