package co.florlogic.finca.consulta.servicios.output.dto;

/** Los conteos de una sección en una jornada, tal como salen de la base. */
public record ConteoPorSeccionDto(String seccion, int esperado, int recibido, int marcadas) {
}
