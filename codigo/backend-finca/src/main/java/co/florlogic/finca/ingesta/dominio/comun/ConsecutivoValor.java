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
            throw new IllegalArgumentException("Un consecutivo vale 1 o más: " + valor);
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
