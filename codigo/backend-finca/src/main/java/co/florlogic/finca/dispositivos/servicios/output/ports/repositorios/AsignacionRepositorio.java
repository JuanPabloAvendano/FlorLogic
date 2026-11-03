package co.florlogic.finca.dispositivos.servicios.output.ports.repositorios;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import co.florlogic.finca.dispositivos.servicios.output.dto.AsignacionDto;

/** Puerto de salida: dónde queda la asignación de cada jornada (CT-13). */
public interface AsignacionRepositorio {

    /** Borra lo que el celular tenía ese día y deja estas celdas, en una sola transacción. */
    void reemplazar(LocalDate jornada, UUID dispositivo, List<AsignacionDto> celdas);

    List<AsignacionDto> de(LocalDate jornada);
}
