package co.florlogic.finca.ciclo.servicios.casosuso;

import java.util.List;
import java.util.UUID;

import co.florlogic.finca.ciclo.servicios.output.dto.TrabajoDto;

/** Casos de uso de la cola del gerente (F4, P-S01): ver lo pendiente y darlo por revisado. */
public interface ResolverTrabajoCasoUso {

    List<TrabajoDto> abiertos();

    /** {@code false} si el trabajo no existe o otro gerente ya lo resolvió. */
    boolean resolver(UUID anotacion, String gerente);
}
