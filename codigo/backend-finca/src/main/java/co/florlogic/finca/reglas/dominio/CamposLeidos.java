package co.florlogic.finca.reglas.dominio;

import java.util.Map;

/**
 * Lo que el lector sacó del contenido de una anotación, ya sin JSON: cómo viene cada campo esperado y si trae motivo
 * de ausencia.
 *
 * @param datosValidos {@code false} si no hay un objeto {@code datos} que leer: es un rechazo, no una marca (A-101)
 */
public record CamposLeidos(boolean datosValidos, Map<String, Presencia> campos, boolean hayMotivo) {

    public CamposLeidos {
        campos = Map.copyOf(campos);
    }

    public static CamposLeidos invalidos() {
        return new CamposLeidos(false, Map.of(), false);
    }
}
