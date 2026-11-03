package co.florlogic.finca.ingesta.servicios.input.dto.respuesta;

/** Lo que devuelve el puerto de cierre, con nombre (A-57). */
public sealed interface CerrarSesionResultadoDto {

    record Cerrada(SesionCerradaRespuestaDto respuesta) implements CerrarSesionResultadoDto {
    }

    record SesionDesconocida() implements CerrarSesionResultadoDto {
    }

    record SesionAjena() implements CerrarSesionResultadoDto {
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - sealed interface con records anidados: los únicos resultados del puerto de cierre; el controlador los recorre
 *   con un switch sin default.
 */
