package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

import java.util.Optional;
import java.util.UUID;

import co.florlogic.finca.ingesta.servicios.output.dto.SesionDto;

/** Puerto de salida: dónde quedan las sesiones de sincronización (CT-1). */
public interface SesionRepositorio {

    Optional<SesionDto> de(UUID sesionId);

    /** Crea la sesión o actualiza su estado y su cierre. */
    void guardar(SesionDto sesion);
}
