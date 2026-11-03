package co.florlogic.finca.ingesta.servicios.input.dto.respuesta;

/** Lo que devuelve el puerto de apertura, con nombre (A-57). */
public sealed interface AbrirSesionResultadoDto {

    record Abierta(SesionAbiertaRespuestaDto respuesta) implements AbrirSesionResultadoDto {
    }

    /** El id del celular no es válido: {@code 422}. */
    record DispositivoInvalido() implements AbrirSesionResultadoDto {
    }

    /** {@code 403 sin-permiso} con {@code dispositivo_bloqueado} (CT-10). */
    record DispositivoBloqueado() implements AbrirSesionResultadoDto {
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - sealed interface con records anidados: los únicos resultados del puerto de apertura; el controlador los recorre
 *   con un switch sin default.
 */
