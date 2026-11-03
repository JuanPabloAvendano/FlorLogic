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
public sealed interface DesfaseDto permits DesfaseDto.Conocido, DesfaseDto.Desconocido {

    DesfaseDto DESCONOCIDO = new Desconocido();

    /** Jackson llama aquí con el entero del JSON; el null y el ausente no pasan por aquí. */
    @JsonCreator
    static DesfaseDto de(long ms) {
        return new Conocido(ms);
    }

    /** @param ms positivo si el reloj del celular va adelantado frente al del nodo de finca */
    record Conocido(long ms) implements DesfaseDto {
        @JsonValue
        long valor() {
            return ms;
        }
    }

    /** El celular nunca sincronizó: la respuesta traerá {@code desfase_desconocido} (A-16). */
    record Desconocido() implements DesfaseDto {
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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - sealed interface ... permits Conocido, Desconocido: solo esas dos clases pueden implementarla; por eso un
 *   switch sobre DesfaseDto que cubra las dos compila sin default, y si mañana hubiera una tercera, no compilaría.
 * - Campo DESCONOCIDO en la interfaz: en una interfaz todo campo es public static final; es la instancia única
 *   del objeto nulo.
 * - @JsonCreator sobre el static de(long): al leer, Jackson construye el desfase desde el número del JSON.
 * - record Conocido(long ms) con @JsonValue en valor(): al escribir, el objeto sale como el número suelto
 *   (-1200), no como {"ms": -1200}.
 * - final class FiltroDesconocido: la usa TiempoDto en @JsonInclude(CUSTOM, valueFilter = …). Jackson crea un
 *   objeto filtro y llama filtro.equals(valor): si da true, omite el miembro. Por eso equals devuelve true para
 *   null y para Desconocido; hashCode se redefine para no romper el par equals/hashCode.
 * - instanceof Desconocido: pregunta si el objeto es de esa clase.
 */
