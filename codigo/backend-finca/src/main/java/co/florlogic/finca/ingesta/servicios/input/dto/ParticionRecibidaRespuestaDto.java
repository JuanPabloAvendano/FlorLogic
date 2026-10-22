package co.florlogic.finca.ingesta.servicios.input.dto;

/**
 * Lo que sale por el puerto de entrada: los conteos de la partición.
 *
 * @param aplicadas nuevas, guardadas en esta petición
 * @param sinEfecto ya estaban guardadas: es un reenvío (A-2, solo por identidad en el camino mínimo)
 * @param mayorConsecutivo el mayor consecutivo de la partición; 0 si no traía anotaciones
 */
public record ParticionRecibidaRespuestaDto(int recibidas, int aplicadas, int sinEfecto, long mayorConsecutivo) {
}
