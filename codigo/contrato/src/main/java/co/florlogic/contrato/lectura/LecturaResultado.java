package co.florlogic.contrato.lectura;

/**
 * Resultado de leer un mensaje del contrato: o se leyó, o falló con el lugar y la clase de fallo.
 *
 * <p>Es la forma de A-57: leer nunca lanza. Quien llama está obligado por el {@code switch} a tratar
 * los dos casos, y un fallo de lectura queda confinado a lo suyo (A-55) en vez de tumbar el proceso.
 */
public sealed interface LecturaResultado<T> permits LecturaResultado.Leida, LecturaResultado.Fallida {

    record Leida<T>(T valor) implements LecturaResultado<T> {
    }

    /**
     * @param pointer JSON Pointer (RFC 6901) al lugar del fallo; vacío si no se puede ubicar
     * @param clase la clase técnica del fallo, sin datos de negocio (A-36), p. ej. {@code tipo_incorrecto}
     */
    record Fallida<T>(String pointer, String clase) implements LecturaResultado<T> {
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - sealed interface LecturaResultado<T> permits Leida, Fallida: solo esas dos variantes; un switch que las
 *   cubra compila sin default. <T> es el tipo de lo leído (p. ej. ParticionDto).
 * - record Leida<T>(T valor) / record Fallida<T>(String pointer, String clase): cada variante es un record
 *   inmutable y genérico; dos Fallida con los mismos textos son iguales (equals de record), y las pruebas lo usan.
 */
