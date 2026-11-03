package co.florlogic.finca.configuracion;

import java.util.List;
import java.util.Objects;

/**
 * La forma de un tipo de anotación (A-88; forma {@code captura.campo} v1, COH-164): qué campos de {@code datos} se
 * esperan y cuál trae el motivo cuando falta un valor.
 */
public record FormaDeAnotacion(String tipo, int version, List<String> campos, String campoMotivoAusencia) {

    public FormaDeAnotacion {
        Objects.requireNonNull(tipo, "tipo");
        campos = List.copyOf(campos);
        Objects.requireNonNull(campoMotivoAusencia, "campoMotivoAusencia");
    }
}
