package co.florlogic.finca.ingesta.servicios.output.dto;

/**
 * Lo que ingesta manda a {@code reglas} para juzgar una anotación nueva (A-96, A-99): el tipo y su versión, el
 * contenido tal como llegó y si el celular la había marcado (para la divergencia, contrato A-32).
 */
public record ContenidoAEvaluarDto(String tipo, int versionTipo, boolean marcadaEnElCelular, String contenidoJson) {
}
