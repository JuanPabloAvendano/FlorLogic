package co.florlogic.contrato.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Los relojes de una anotación (A-16, A-17). El contrato solo garantiza que viajan; calcular
 * {@code hora_dudosa} o {@code reloj_movido} es del ingreso.
 *
 * @param crudoMs reloj de pared del celular al capturar; nadie lo corrige nunca
 * @param monoMs reloj monótono; solo se compara dentro del mismo {@code arranque}
 * @param arranque sube en cada encendido del celular
 * @param desfaseDeclarado el desfase medido en la última sincronización, o desconocido
 */
public record TiempoDto(
        @JsonProperty("crudo_ms") long crudoMs,
        @JsonProperty("mono_ms") long monoMs,
        @JsonProperty("arranque") long arranque,
        @JsonProperty("desfase_declarado_ms")
        @JsonInclude(value = JsonInclude.Include.CUSTOM, valueFilter = DesfaseDto.FiltroDesconocido.class)
        DesfaseDto desfaseDeclarado) {

    public TiempoDto {
        // Ausente y null del JSON llegan aquí como null y se vuelven el mismo objeto nulo (A-34).
        if (desfaseDeclarado == null) {
            desfaseDeclarado = DesfaseDto.DESCONOCIDO;
        }
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record con @JsonProperty: cada reloj con su nombre del contrato.
 * - @JsonInclude(value = Include.CUSTOM, valueFilter = DesfaseDto.FiltroDesconocido.class): al escribir,
 *   Jackson pregunta al filtro si omite desfase_declarado_ms; lo omite cuando el desfase es Desconocido.
 * - public TiempoDto { … }: constructor compacto; un desfase null (ausente o null en el JSON) se cambia por
 *   DesfaseDto.DESCONOCIDO antes de guardarlo.
 */
