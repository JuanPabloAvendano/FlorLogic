package co.florlogic.finca.reglas.servicios.output.ports.serviciosexternos;

import java.util.Optional;

import co.florlogic.finca.reglas.servicios.output.dto.FormaDto;

/** Puerto de salida hacia el catálogo de parámetros del módulo {@code configuracion} (A-96, como en ucotestapi). */
public interface CatalogoDeParametros {

    Optional<FormaDto> forma(String tipo, int version);
}
