package co.florlogic.finca.ingesta.dominio.recepcion;

import java.util.Objects;
import java.util.UUID;

import co.florlogic.finca.ingesta.dominio.comun.AnotacionId;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;

/**
 * Una anotación que llegó del celular, como JSON equivalente (A-59): mismo contenido, no los mismos bytes. En el camino mínimo (D-17) el nodo de finca solo
 * la guarda; juzgarla con el catálogo y las reglas llega después.
 *
 * <p>Es dominio: Java puro, sin Spring y sin el módulo {@code contrato} (A-44). El ensamblador del controlador traduce el JSON a la solicitud
 * del puerto (A-103) y el ensamblador de entrada traduce la solicitud a este record (A-84).
 *
 * @param anotacionId el id del celular, siempre un UUID v7 (lo garantiza {@link AnotacionId})
 * @param consecutivo siempre 1 o más (lo garantiza {@link ConsecutivoValor}; A-108, C-105)
 * @param sesionId la sesión de sincronización de la ruta; todavía no se verifica que exista
 * @param contenidoJson la anotación completa en JSON, para no perder campos mientras no hay reglas; el orden de
 *        las claves y la forma de los números pueden cambiar (A-59)
 */
public record AnotacionRecibida(AnotacionId anotacionId, ConsecutivoValor consecutivo, UUID sesionId, String contenidoJson) {

    public AnotacionRecibida {
        // El dominio sí exige sus invariantes (investigacion/03 regla 8): el ensamblador del controlador ya rechazó con 422
        // lo que no las cumple, así que aquí solo atrapan errores de programación.
        Objects.requireNonNull(anotacionId, "anotacionId");
        Objects.requireNonNull(consecutivo, "consecutivo");
        Objects.requireNonNull(sesionId, "sesionId");
        Objects.requireNonNull(contenidoJson, "contenidoJson");
    }
}
