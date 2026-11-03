package co.florlogic.finca.ingesta.dominio.sesion;

/** Lo que devuelve el caso de uso de abrir sesión (CT-10): abierta, o el celular está bloqueado. */
public sealed interface ResultadoDeApertura {

    record Abierta(SesionAbiertaValor abierta) implements ResultadoDeApertura {
    }

    /** {@code 403 sin-permiso} con {@code dispositivo_bloqueado}. */
    record DispositivoBloqueado() implements ResultadoDeApertura {
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - sealed interface con records anidados: los únicos resultados posibles de abrir; SesionEnsambladorEntrada los
 *   recorre con un switch sin default.
 */
