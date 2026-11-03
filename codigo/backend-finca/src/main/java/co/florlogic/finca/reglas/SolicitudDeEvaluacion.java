package co.florlogic.finca.reglas;

/** Lo que se evalúa de una anotación: su tipo y versión, el contenido como llegó y si el celular la había marcado. */
public record SolicitudDeEvaluacion(String tipo, int versionTipo, boolean marcadaEnElCelular, String contenidoJson) {
}
