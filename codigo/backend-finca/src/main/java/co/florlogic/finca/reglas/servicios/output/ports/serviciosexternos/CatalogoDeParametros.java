package co.florlogic.finca.reglas.servicios.output.ports.serviciosexternos;

import java.util.Optional;

import co.florlogic.finca.reglas.servicios.output.dto.FormaDto;

/** Puerto de salida hacia el catálogo de parámetros del módulo {@code configuracion} (A-96, como en ucotestapi). */
public interface CatalogoDeParametros {

    Optional<FormaDto> forma(String tipo, int version);
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - Optional<FormaDto>: tipo de Java para «puede que haya, puede que no»; vacío si el tipo o la versión no están en el
 *   catálogo, en lugar de devolver null.
 */
