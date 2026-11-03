package co.florlogic.finca.reglas;

/** Lo que se evalúa de una anotación: su tipo y versión, el contenido como llegó y si el celular la había marcado. */
public record SolicitudDeEvaluacion(String tipo, int versionTipo, boolean marcadaEnElCelular, String contenidoJson) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, tipo(), versionTipo(), marcadaEnElCelular(),
 *   contenidoJson(), equals, hashCode y toString. Es lo que ingesta le entrega al motor de reglas.
 */
