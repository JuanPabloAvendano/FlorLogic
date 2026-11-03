package co.florlogic.finca.reglas;

/**
 * API pública del motor de reglas del servidor (ESPEC-dominio-ingesta A-96, A-99): juzga el contenido de una anotación
 * con la forma de su tipo y versión (ADR-029: la versión con que se capturó). Ingesta la llama por su puerto.
 */
public interface EvaluarAnotacion {

    ResultadoDeEvaluacion evaluar(SolicitudDeEvaluacion solicitud);
}
