package co.florlogic.contrato.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Null Object del desfase declarado (A-16, A-34): o se conoce, o el celular nunca sincronizó.
 * Un {@code switch} sobre esta interfaz sellada obliga a tratar los dos casos; con un
 * {@code Long} que puede ser null, olvidar el caso desconocido compila sin aviso.
 *
 * <p>En el JSON es un entero con signo, o no está. El desconocido nunca se escribe: un miembro
 * {@code null} y uno ausente valen lo mismo (A-33), y omitirlo es la única forma de las dos.
 */
public sealed interface Desfase permits Desfase.Conocido, Desfase.Desconocido {

    Desfase DESCONOCIDO = new Desconocido();

    /** Jackson llama aquí con el entero del JSON; el null y el ausente no pasan por aquí. */
    @JsonCreator
    static Desfase de(long ms) {
        return new Conocido(ms);
    }

    /** @param ms positivo si el reloj del celular va adelantado frente al del nodo de finca */
    record Conocido(long ms) implements Desfase {
        @JsonValue
        long valor() {
            return ms;
        }
    }

    /** El celular nunca sincronizó: la respuesta traerá {@code desfase_desconocido} (A-16). */
    record Desconocido() implements Desfase {
    }

    /**
     * Filtro de {@code @JsonInclude}: devuelve {@code true} para lo que <em>no</em> se escribe.
     * Jackson compara con {@code equals}; dos {@code Desconocido} son iguales por ser records.
     */
    final class FiltroDesconocido {
        @Override
        public boolean equals(Object otro) {
            return otro == null || otro instanceof Desconocido;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
}
