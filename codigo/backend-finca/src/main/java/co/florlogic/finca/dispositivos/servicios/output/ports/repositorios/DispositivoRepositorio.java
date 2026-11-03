package co.florlogic.finca.dispositivos.servicios.output.ports.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import co.florlogic.finca.dispositivos.servicios.output.dto.DispositivoDto;

/** Puerto de salida: dónde quedan los celulares conocidos. */
public interface DispositivoRepositorio {

    Optional<DispositivoDto> de(UUID id);

    /** Crea o actualiza; dos aperturas a la vez de un celular nuevo no fallan: la segunda no cambia nada. */
    void guardar(DispositivoDto dispositivo);

    List<DispositivoDto> todos();
}
