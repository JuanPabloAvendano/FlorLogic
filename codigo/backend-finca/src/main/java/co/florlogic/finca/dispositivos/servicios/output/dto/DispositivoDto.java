package co.florlogic.finca.dispositivos.servicios.output.dto;

import java.time.Instant;
import java.util.UUID;

/** Una fila de {@code dispositivo}, sin dominio. */
public record DispositivoDto(UUID id, String estado, Instant vistoPorPrimeraVez, Instant cambiadoEn, String cambiadoPor) {
}
