package co.florlogic.finca.ingesta.dominio.recepcion;

/**
 * Objeto de valor: lo que pasó al recibir una partición, contado. Hecho a la medida del caso de uso, en lenguaje de
 * dominio y no de aplicación (Vernon · Implementing Domain-Driven Design p. 560); el ensamblador de entrada lo
 * convierte en la respuesta del puerto.
 *
 * @param aplicadas nuevas, guardadas en esta petición
 * @param sinEfecto ya estaban guardadas: es un reenvío (A-2)
 * @param confirmadoHasta el mayor consecutivo del celular recibido sin huecos, tras esta partición; 0 si nunca se ha
 *        recibido nada (A-31, A-78; Segmento 1b)
 */
public record RecepcionDeParticionValor(int recibidas, int aplicadas, int sinEfecto, long confirmadoHasta) {
}
