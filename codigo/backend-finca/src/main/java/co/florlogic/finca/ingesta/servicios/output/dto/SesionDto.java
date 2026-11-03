package co.florlogic.finca.ingesta.servicios.output.dto;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Lo que la persistencia guarda de una sesión de sincronización (CT-1), sin el agregado.
 *
 * @param estado {@code ABIERTA}, {@code CERRADA} o {@code PARCIAL}
 * @param cerradaEn {@code null} mientras está abierta
 */
public record SesionDto(UUID sesionId, UUID dispositivoId, String persona, Instant abiertaEn, long desfaseMs,
        int versionConfig, boolean dispositivoRegistrado, String estado, Instant cerradaEn) {

    public SesionDto {
        Objects.requireNonNull(sesionId, "sesionId");
    }
}
