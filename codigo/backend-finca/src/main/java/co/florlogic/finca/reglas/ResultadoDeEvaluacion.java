package co.florlogic.finca.reglas;

import java.util.List;
import java.util.Optional;

/**
 * El juicio de las reglas (A-99): los códigos de marca, en minúscula como en el contrato (vacío si no hay nada que
 * marcar), o el motivo del rechazo. Un rechazo no lleva marcas.
 */
public record ResultadoDeEvaluacion(List<String> marcas, Optional<String> motivoRechazo) {

    public ResultadoDeEvaluacion {
        marcas = List.copyOf(marcas);
    }

    public static ResultadoDeEvaluacion rechazo(String motivo) {
        return new ResultadoDeEvaluacion(List.of(), Optional.of(motivo));
    }
}
