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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - Optional<HechoDto>: tipo de Java para «puede que haya, puede que no»; vacío cuando la celda todavía no tiene hecho
 *   vigente, en lugar de devolver null.
 * - «ConTurno» en vigenteConTurno: la implementación en PostgreSQL lo cumple con SELECT … FOR UPDATE, que bloquea la
 *   fila hasta el fin de la transacción.
 */
