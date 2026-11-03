package co.florlogic.finca.ingesta.servicios.output.dto;

import java.util.Objects;
import java.util.UUID;

/**
 * Lo que la persistencia guarda de una anotación rechazada (ESPEC-dominio-ingesta §5 paso 5): aparte de las aceptadas,
 * con su huella, para que el reenvío de la misma repita el rechazo y no cuente doble.
 *
 * @param motivo en minúscula como en el contrato ({@code datos_invalidos}, {@code corrupta}, {@code tipo_desconocido})
 */
public record AnotacionRechazadaDto(UUID anotacionUuid, String huella, UUID sesionId, long consecutivo, String motivo,
        String contenidoJson) {

    public AnotacionRechazadaDto {
        Objects.requireNonNull(anotacionUuid, "anotacionUuid");
    }
}
