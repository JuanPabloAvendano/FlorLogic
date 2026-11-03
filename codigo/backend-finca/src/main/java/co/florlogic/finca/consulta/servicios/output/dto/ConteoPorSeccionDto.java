package co.florlogic.finca.consulta.servicios.output.dto;

/** Los conteos de una sección en una jornada, tal como salen de la base. */
public record ConteoPorSeccionDto(String seccion, int esperado, int recibido, int marcadas) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, los accesores (sin «get»: seccion(), esperado()…), equals, hashCode y toString a partir de la lista de componentes.
 */
