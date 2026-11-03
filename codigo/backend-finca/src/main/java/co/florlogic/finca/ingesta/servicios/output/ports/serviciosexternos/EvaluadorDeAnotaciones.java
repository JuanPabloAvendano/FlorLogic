package co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos;

import co.florlogic.finca.ingesta.servicios.output.dto.ContenidoAEvaluarDto;
import co.florlogic.finca.ingesta.servicios.output.dto.VeredictoDto;

/**
 * Puerto de salida hacia el módulo {@code reglas} (A-96): juzga el contenido de una anotación nueva con la forma de su
 * tipo y versión. Ingesta no sabe cómo se juzga.
 */
public interface EvaluadorDeAnotaciones {

    VeredictoDto evaluar(ContenidoAEvaluarDto contenido);
}
