package co.florlogic.finca.ingesta.dominio;

/**
 * Objeto de valor: lo que pasó al recibir una partición, contado. Hecho a la medida del caso de uso, en lenguaje de
 * dominio y no de aplicación (Vernon · Implementing Domain-Driven Design p. 560); el ensamblador de entrada lo
 * convierte en la respuesta del puerto.
 *
 * @param aplicadas nuevas, guardadas en esta petición
 * @param sinEfecto ya estaban guardadas: es un reenvío (A-2)
 * @param mayorConsecutivo el mayor consecutivo de la partición; 0 si no traía anotaciones (atajo del camino mínimo:
 *        no es la marca de agua real, A-31)
 */
public record RecepcionDeParticionValor(int recibidas, int aplicadas, int sinEfecto, long mayorConsecutivo) {
}
