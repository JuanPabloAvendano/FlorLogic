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
