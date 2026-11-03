package co.florlogic.finca.configuracion.servicios.output.dto;

import java.util.List;

/** Una fila de {@code forma_tipo}, sin dominio. */
public record FormaTipoDto(String tipo, int version, List<String> campos, String campoMotivoAusencia) {
}
