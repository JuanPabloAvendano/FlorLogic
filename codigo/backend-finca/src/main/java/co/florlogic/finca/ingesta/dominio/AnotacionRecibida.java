package co.florlogic.finca.ingesta.dominio;

import java.util.Objects;
import java.util.UUID;

/**
 * Una anotación que llegó del celular, como JSON equivalente (A-59): mismo contenido, no los mismos bytes. En el camino mínimo (D-17) el nodo de finca solo
 * la guarda; juzgarla con el catálogo y las reglas llega después.
 *
 * <p>Es dominio: Java puro, sin Spring y sin el módulo {@code contrato} (A-44). El controlador traduce el
 * DTO del cable a este record.
 *
 * @param sesionId la sesión de sincronización de la ruta; todavía no se verifica que exista
 * @param contenidoJson la anotación completa en JSON, para no perder campos mientras no hay reglas; el orden de
 *        las claves y la forma de los números pueden cambiar (A-59)
 */
public record AnotacionRecibida(UUID anotacionUuid, long consecutivo, UUID sesionId, String contenidoJson) {

    public AnotacionRecibida {
        // El dominio sí exige sus invariantes (investigacion/03 regla 8): el controlador ya rechazó con 422
        // lo que no las cumple, así que aquí solo atrapan errores de programación.
        Objects.requireNonNull(anotacionUuid, "anotacionUuid");
        Objects.requireNonNull(sesionId, "sesionId");
        Objects.requireNonNull(contenidoJson, "contenidoJson");
    }
}
