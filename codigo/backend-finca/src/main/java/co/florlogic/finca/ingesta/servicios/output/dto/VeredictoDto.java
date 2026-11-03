package co.florlogic.finca.ingesta.servicios.output.dto;

import java.util.List;
import java.util.Optional;

/**
 * Lo que responde {@code reglas} (A-99): los códigos de marca (vacío si no hay nada que marcar) o el motivo de rechazo.
 * Los nombres van en minúscula, como en el contrato.
 */
public record VeredictoDto(List<String> marcas, Optional<String> motivoRechazo) {

    public VeredictoDto {
        marcas = List.copyOf(marcas);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - Optional<String> motivoRechazo como componente del record: vacío si las reglas no rechazan.
 * - record con constructor compacto: List.copyOf(marcas) deja una copia no modificable.
 */
