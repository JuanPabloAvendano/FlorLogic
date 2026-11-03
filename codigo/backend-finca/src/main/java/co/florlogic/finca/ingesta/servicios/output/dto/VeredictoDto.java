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
