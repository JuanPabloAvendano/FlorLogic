package co.florlogic.finca.ingesta.servicios.output.dto;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Lo que la persistencia guarda de una anotación aceptada (aplicada o marcada), sin el dominio
 * (Martin · Clean Architecture p. 195).
 *
 * @param huella SHA-256 en hexadecimal de la forma canónica (A-86)
 * @param resultado {@code APLICADA} o {@code MARCADA}
 * @param codigos los de la marca, en minúscula como en el contrato; vacío si es aplicada
 * @param contenidoJson la anotación en JSON equivalente (A-59); se guarda como {@code jsonb}
 */
public record AnotacionRecibidaDto(UUID anotacionUuid, long consecutivo, UUID sesionId, String tipo, int versionTipo,
        String huella, String resultado, List<String> codigos, String contenidoJson) {

    public AnotacionRecibidaDto {
        Objects.requireNonNull(anotacionUuid, "anotacionUuid");
        codigos = List.copyOf(codigos);
    }
}
