package co.florlogic.finca.ciclo.servicios.output.ports.repositorios;

import java.time.LocalDate;
import java.util.Optional;

import co.florlogic.finca.ciclo.servicios.output.dto.HechoDto;

/** Puerto de salida: los hechos de producción vigentes. */
public interface HechoRepositorio {

    /** El vigente, tomando su fila hasta el fin de la transacción (dos capturas del mismo hecho a la vez quedan en fila). */
    Optional<HechoDto> vigenteConTurno(String produccionId, String seccionId, String campoId, LocalDate jornada);

    void guardar(HechoDto hecho);
}
