package co.florlogic.finca.configuracion.servicios.output.ports.repositorios;

import java.util.Optional;

import co.florlogic.finca.configuracion.servicios.output.dto.FormaTipoDto;

/** Puerto de salida: dónde viven las formas y las versiones de configuración (A-88: tablas versionadas de PostgreSQL). */
public interface CatalogoRepositorio {

    Optional<FormaTipoDto> forma(String tipo, int version);

    /** La versión marcada como vigente; 0 si no hay ninguna. */
    int versionVigente();
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - Optional<FormaTipoDto>: trae la fila o viene vacío si ese tipo y versión no están en el catálogo; nunca null.
 */
