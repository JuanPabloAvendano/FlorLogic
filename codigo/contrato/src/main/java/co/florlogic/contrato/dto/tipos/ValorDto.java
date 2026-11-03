package co.florlogic.contrato.dto.tipos;

import tools.jackson.databind.JsonNode;

/**
 * El dato de una captura (A-34, A-35): o hay valor, o falta con su motivo, o llegó contradictorio.
 * Se arma con {@link CapturaCampoDto#dato()}; en el JSON son dos miembros hermanos,
 * {@code valor} y {@code motivo_ausencia}.
 */
public sealed interface ValorDto permits ValorDto.Hay, ValorDto.Falta, ValorDto.Contradictorio {

    /** @param valor JSON libre; el catálogo de su versión decide si vale (A-20) */
    record Hay(JsonNode valor) implements ValorDto {
    }

    /** Llega {@code marcada} {@code incompleta}; sin motivo, también {@code motivo_ausente} (A-35). */
    record Falta(Motivo motivo) implements ValorDto {
    }

    /**
     * Caso especial (Fowler, <em>Special Case</em>): trae valor y motivo de ausencia a la vez. Es un
     * fallo técnico (A-35) que el validador ya convirtió en {@code rechazada}; si llega aquí, se nombra
     * en vez de lanzar (A-57) o de elegir uno de los dos en silencio.
     */
    record Contradictorio(JsonNode valor, String motivoAusencia) implements ValorDto {
    }

    /** El motivo de ausencia, que viene del catálogo de configuración. */
    sealed interface Motivo permits Motivo.Declarado, Motivo.SinMotivo {

        Motivo SIN_MOTIVO = new SinMotivo();

        record Declarado(String id) implements Motivo {
        }

        record SinMotivo() implements Motivo {
        }
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - sealed interface ... permits Hay, Falta, Contradictorio: solo esas tres variantes existen; un switch sobre
 *   ValorDto que las cubra compila sin default.
 * - record Hay(JsonNode valor) implements ValorDto: cada variante es un record inmutable con sus datos.
 * - sealed interface Motivo permits Declarado, SinMotivo: el mismo recurso, anidado, para el motivo.
 * - Motivo SIN_MOTIVO = new SinMotivo(): campo de interfaz (public static final), la instancia única del
 *   objeto nulo; dos SinMotivo son iguales por ser records sin campos.
 */
