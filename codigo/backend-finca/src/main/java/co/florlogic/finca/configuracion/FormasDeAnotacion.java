package co.florlogic.finca.configuracion;

import java.util.Optional;

/**
 * API pública del catálogo de parámetros (ESPEC-dominio-ingesta A-88, A-95): la forma de cada tipo de anotación en
 * cada versión. Vacío si el tipo o la versión no existen; quien pregunta decide qué hacer (A-101: tipo desconocido).
 */
public interface FormasDeAnotacion {

    Optional<FormaDeAnotacion> de(String tipo, int version);
}
