package co.florlogic.finca.reglas.servicios.casosuso;

import co.florlogic.finca.reglas.ResultadoDeEvaluacion;
import co.florlogic.finca.reglas.SolicitudDeEvaluacion;

/** Caso de uso: juzgar el contenido de una anotación con la forma de su tipo y versión. */
public interface EvaluarAnotacionCasoUso {

    ResultadoDeEvaluacion ejecutar(SolicitudDeEvaluacion solicitud);
}
