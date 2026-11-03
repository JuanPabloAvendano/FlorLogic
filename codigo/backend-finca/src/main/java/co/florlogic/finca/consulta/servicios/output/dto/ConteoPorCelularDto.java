package co.florlogic.finca.consulta.servicios.output.dto;

import java.util.UUID;

/** Los conteos de un celular en una jornada, tal como salen de la base. */
public record ConteoPorCelularDto(UUID dispositivo, int esperado, int recibido, int marcadas, int rechazadas,
        int parciales) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, los accesores (sin «get»: dispositivo(), esperado()…), equals, hashCode y toString a partir de la lista de componentes.
 */
