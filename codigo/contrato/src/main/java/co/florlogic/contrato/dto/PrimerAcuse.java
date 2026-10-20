package co.florlogic.contrato.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cuándo vio el nodo de finca por primera vez una anotación que se reenvía tras una restauración
 * (A-45 ⟦2⟧b). Va fuera de la anotación porque la anotación es inmutable (A-49 ⟦3⟧).
 *
 * @param primerAcuseMs el {@code recibido_ms} de aquella respuesta, copiado tal cual: reloj del
 *     nodo de finca, nunca del celular
 */
public record PrimerAcuse(
        @JsonProperty("anotacion_uuid") UUID anotacionUuid,
        @JsonProperty("primer_acuse_ms") long primerAcuseMs) {
}
