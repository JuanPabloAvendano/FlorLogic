package co.florlogic.finca.ingesta.servicios.output.dto;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Lo que la persistencia necesita del avance de un celular, sin el agregado (Martin · Clean Architecture p. 195).
 *
 * @param confirmadoHasta 0 si no se ha recibido nada (A-78)
 * @param porEncima los consecutivos que llegaron por encima de un hueco, en orden
 * @param version la versión leída; 0 si el avance es nuevo
 */
public record AvanceDto(UUID dispositivoId, long confirmadoHasta, List<Long> porEncima, long version) {

    public AvanceDto {
        Objects.requireNonNull(dispositivoId, "dispositivoId");
        porEncima = List.copyOf(porEncima);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record con constructor compacto: exige el dispositivo con Objects.requireNonNull y copia porEncima con
 *   List.copyOf (no modificable, sin null).
 */
