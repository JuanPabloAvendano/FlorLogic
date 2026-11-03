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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - Optional<DispositivoDto>: tipo de Java para «puede que haya, puede que no»; vacío si el celular nunca se ha visto,
 *   en lugar de devolver null.
 */
