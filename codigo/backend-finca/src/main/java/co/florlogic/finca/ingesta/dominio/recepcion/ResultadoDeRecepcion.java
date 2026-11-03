package co.florlogic.finca.ingesta.dominio.recepcion;

/**
 * Lo que devuelve el caso de uso de recibir una partición, con nombre en vez de excepciones (A-57, Special Case):
 * la recepción, o por qué no se recibió nada.
 */
public sealed interface ResultadoDeRecepcion {

    record Recibida(RecepcionDeParticionValor recepcion) implements ResultadoDeRecepcion {
    }

    /** No hay sesión abierta con ese id (contrato A-22: {@code 404 sesion-desconocida}). */
    record SesionDesconocida() implements ResultadoDeRecepcion {
    }

    /** La sesión la abrió otra persona (contrato A-51: {@code 403 sesion_ajena}). */
    record SesionAjena() implements ResultadoDeRecepcion {
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - sealed interface: solo pueden implementarla los tipos que el compilador conoce; como los records están anidados
 *   aquí mismo no hace falta escribir permits. Así el switch del ensamblador de entrada cubre todos los casos sin
 *   default y el compilador avisa si se agrega uno nuevo.
 * - record Recibida(...) implements ResultadoDeRecepcion: cada resultado es un record; los vacíos
 *   (SesionDesconocida, SesionAjena) solo sirven por su tipo.
 */
