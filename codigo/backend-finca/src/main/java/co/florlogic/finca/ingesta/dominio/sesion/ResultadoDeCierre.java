package co.florlogic.finca.ingesta.dominio.sesion;

/** Lo que devuelve el caso de uso de cerrar una sesión, con nombre en vez de excepciones (A-57). */
public sealed interface ResultadoDeCierre {

    record Cerrada(SesionDeSincronizacion sesion, long confirmadoHasta) implements ResultadoDeCierre {
    }

    record SesionDesconocida() implements ResultadoDeCierre {
    }

    record SesionAjena() implements ResultadoDeCierre {
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - sealed interface con records anidados: los únicos resultados posibles de cerrar; SesionEnsambladorEntrada los
 *   recorre con un switch sin default.
 */
