package co.florlogic.finca.configuracion.servicios.output.dto;

import java.util.List;

/** Una fila de {@code forma_tipo}, sin dominio. */
public record FormaTipoDto(String tipo, int version, List<String> campos, String campoMotivoAusencia) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, los accesores (sin «get»: tipo(), campos()…), equals, hashCode y toString a partir de la lista de componentes.
 */
