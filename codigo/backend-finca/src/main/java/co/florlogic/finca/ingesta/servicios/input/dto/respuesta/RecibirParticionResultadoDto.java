package co.florlogic.finca.ingesta.servicios.input.dto.respuesta;

/** Lo que devuelve el puerto de entrada, con nombre (A-57): la respuesta, o por qué no se recibió nada. */
public sealed interface RecibirParticionResultadoDto {

    record Recibida(RecibirParticionRespuestaDto respuesta) implements RecibirParticionResultadoDto {
    }

    /** El dominio rechazó una anotación (id no v7, consecutivo menor que 1): {@code 422} de toda la partición (A-108). */
    record AnotacionInvalida() implements RecibirParticionResultadoDto {
    }

    /** {@code 404 sesion-desconocida} (contrato A-22). */
    record SesionDesconocida() implements RecibirParticionResultadoDto {
    }

    /** {@code 403 sin-permiso} con {@code sesion_ajena} (contrato A-51). */
    record SesionAjena() implements RecibirParticionResultadoDto {
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - sealed interface con records anidados: los únicos resultados del puerto de particiones; el controlador los
 *   recorre con un switch sin default y el compilador avisa si falta uno.
 */
