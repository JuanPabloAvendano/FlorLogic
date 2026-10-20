package co.florlogic.finca.ingesta.servicios.puertos.entrada;

/**
 * El resultado de recibir una partición.
 *
 * @param aplicadas nuevas, guardadas en esta petición
 * @param sinEfecto ya estaban guardadas: es un reenvío (A-2, solo por identidad en el camino mínimo)
 * @param mayorConsecutivo el mayor consecutivo de la partición; 0 si no traía anotaciones
 */
public record ParticionRecibida(int recibidas, int aplicadas, int sinEfecto, long mayorConsecutivo) {
}
