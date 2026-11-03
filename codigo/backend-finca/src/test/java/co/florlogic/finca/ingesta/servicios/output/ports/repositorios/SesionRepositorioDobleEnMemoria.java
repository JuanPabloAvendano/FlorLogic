package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import co.florlogic.finca.ingesta.servicios.output.dto.SesionDto;

/** Doble en memoria de {@link SesionRepositorio}: crear o actualizar, como el adaptador JDBC (CT-1). */
public final class SesionRepositorioDobleEnMemoria implements SesionRepositorio {

    private final Map<UUID, SesionDto> guardadas = new ConcurrentHashMap<>();

    @Override
    public Optional<SesionDto> de(UUID sesionId) {
        return Optional.ofNullable(guardadas.get(sesionId));
    }

    @Override
    public void guardar(SesionDto sesion) {
        guardadas.put(sesion.sesionId(), sesion);
    }
}
