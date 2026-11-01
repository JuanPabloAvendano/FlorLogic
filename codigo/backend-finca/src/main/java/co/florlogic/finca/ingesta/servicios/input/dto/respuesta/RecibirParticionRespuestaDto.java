package co.florlogic.finca.ingesta.servicios.input.dto.respuesta;

/**
 * Lo que sale por el puerto de entrada: los conteos de la partición.
 *
 * @param aplicadas nuevas, guardadas en esta petición
 * @param sinEfecto ya estaban guardadas: es un reenvío (A-2, solo por identidad en el camino mínimo)
 * @param confirmadoHasta el mayor consecutivo del celular recibido sin huecos; 0 si nunca se ha recibido nada
 */
public record RecibirParticionRespuestaDto(int recibidas, int aplicadas, int sinEfecto, long confirmadoHasta) {
}
