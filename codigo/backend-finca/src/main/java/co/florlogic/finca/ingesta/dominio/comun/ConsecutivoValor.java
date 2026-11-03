package co.florlogic.finca.ingesta.dominio.comun;

import java.util.Optional;

/**
 * El número que el celular da, en orden, a cada anotación que guardó (ESPEC-dominio-ingesta A-65). Sobre él se
 * calcula {@code confirmado_hasta}, que es lo que el celular usa para borrar (A-31).
 *
 * <p>Siempre vale 1 o más (INV-5): 0 o ausente no es un consecutivo, y «nada recibido todavía» es
 * {@code Optional.empty()}, nunca {@code ConsecutivoValor(0)} (A-78). El contrato lo acota a 2⁵³ − 1; el dominio acepta
 * hasta {@code Long.MAX_VALUE} (ESPEC §3).
 *
 * @param valor el número, 1 o más
 */
public record ConsecutivoValor(long valor) implements Comparable<ConsecutivoValor> {

    /** Valida y lanza: solo atrapa errores de programación. La frontera es {@link #de(long)} (DI1-7). */
    public ConsecutivoValor {
        if (valor < 1) {
            // Solo la llave y el valor: el texto para personas vive en el catálogo de mensajes (OBS-1-6, VER-3).
            throw new IllegalArgumentException("consecutivo.menor_que_uno: " + valor);
        }
    }

    /**
     * La frontera: 0 o negativo da vacío, nunca lanza (A-57, A-70). Con vacío la partición entera es
     * {@code 422 sobre-invalido}: nunca llega al juicio ni al avance.
     */
    public static Optional<ConsecutivoValor> de(long valor) {
        return valor >= 1 ? Optional.of(new ConsecutivoValor(valor)) : Optional.empty();
    }

    /**
     * El que sigue a un {@code confirmadoHasta}: vacío (nada recibido) → 1 (A-78, COH-123). Concentra esa regla en un
     * solo lugar (DI1-3); la usa {@code Anotacion.recibir} para decidir si hay hueco (A-71, Segmento 2).
     */
    public static ConsecutivoValor siguienteDe(Optional<ConsecutivoValor> confirmadoHasta) {
        return confirmadoHasta.map(ConsecutivoValor::siguiente).orElse(new ConsecutivoValor(1));
    }

    /**
     * El número siguiente. Con {@code Long.MAX_VALUE} lanza {@link ArithmeticException}: está fuera del rango del
     * contrato (2⁵³ − 1), así que solo pasa por un error de programación (DI1-9).
     */
    public ConsecutivoValor siguiente() {
        return new ConsecutivoValor(Math.addExact(valor, 1));
    }

    @Override
    public int compareTo(ConsecutivoValor otro) {
        return Long.compare(valor, otro.valor);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - implements Comparable<ConsecutivoValor> + compareTo: le da a los consecutivos un orden natural; Long.compare
 *   devuelve negativo, cero o positivo. Lo usan el TreeSet del avance y el sorted del caso de uso.
 * - Optional.map(ConsecutivoValor::siguiente).orElse(new ConsecutivoValor(1)): si hay confirmado, su siguiente; si
 *   no, 1. Ojo: el argumento de orElse se construye siempre, aunque no se use (aquí es barato).
 * - Math.addExact(valor, 1): suma que lanza ArithmeticException si se pasa de Long.MAX_VALUE, en vez de dar la
 *   vuelta a un número negativo.
 * - record con constructor compacto: valida antes de guardar el campo.
 */
