package co.florlogic.finca.ingesta.dominio.recepcion;

import java.util.Optional;

import co.florlogic.finca.ingesta.dominio.comun.HuellaValor;

/**
 * Los dos casos de idempotencia por id (ADR-027; contrato A-19): un id que ya llegó con la <em>misma</em> huella es un
 * reenvío y no tiene efecto; con <em>otra</em> huella es una anotación corrupta. El tercer caso, otro id con la misma
 * clave del hecho, entra como nueva (RF-022: gana lo más reciente) y no pasa por aquí.
 */
public final class IdYaRecibido {

    public enum Caso {
        REENVIO,
        CORRUPTA
    }

    private IdYaRecibido() {
    }

    /** Vacío si el id es nuevo (no hay huella anterior). */
    public static Optional<Caso> ante(Optional<HuellaValor> anterior, HuellaValor llegada) {
        return anterior.map(h -> h.equals(llegada) ? Caso.REENVIO : Caso.CORRUPTA);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - public enum Caso (anidado): los dos casos posibles viven dentro de la clase que los decide; se usan como
 *   IdYaRecibido.Caso.REENVIO.
 * - private IdYaRecibido() vacío: clase de utilidades, nadie la instancia; solo se usa ante(...).
 * - anterior.map(h -> h.equals(llegada) ? Caso.REENVIO : Caso.CORRUPTA): si no hay huella anterior el Optional
 *   sigue vacío (id nuevo); si la hay, la compara. «h -> …» es una lambda.
 */
