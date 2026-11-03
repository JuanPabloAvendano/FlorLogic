package co.florlogic.finca.reglas.servicios.output.dto;

import java.util.List;

/** La forma de un tipo, como la necesita {@code reglas}. */
public record FormaDto(List<String> campos, String campoMotivoAusencia) {
}
