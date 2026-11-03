package co.florlogic.finca.ciclo.servicios.input.dto.solicitud;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Lo que el ciclo necesita de una anotación aceptada, ya leída de su JSON (sin dominio).
 *
 * @param capturadoEn la hora del celular corregida con el desfase de su sesión (ADR-031)
 * @param codigos de la marca; vacío si es aplicada
 */
public record AnotacionAceptadaSolicitudDto(UUID anotacionUuid, UUID dispositivoId, String produccionId, String seccionId,
        String campoId, LocalDate jornada, String valor, String motivoAusencia, Instant capturadoEn, List<String> codigos) {

    public AnotacionAceptadaSolicitudDto {
        codigos = List.copyOf(codigos);
    }
}
