package co.florlogic.finca.reglas.infraestructura.output.adapter;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import co.florlogic.finca.reglas.dominio.CamposLeidos;
import co.florlogic.finca.reglas.dominio.Presencia;
import co.florlogic.finca.reglas.servicios.output.dto.FormaDto;
import co.florlogic.finca.reglas.servicios.output.ports.serviciosexternos.LectorDeContenido;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Lee {@code datos} de la anotación con Jackson 3 (TEC-10) y dice cómo viene cada campo de la forma (A-99). Null ≡
 * ausente (contrato A-33). Un motivo de ausencia en blanco no cuenta como motivo. Nunca lanza: lo ilegible es
 * {@link CamposLeidos#invalidos()}.
 */
@Component
class LectorDeContenidoJackson implements LectorDeContenido {

    private static final ObjectMapper JSON = JsonMapper.builder().build();

    @Override
    public CamposLeidos leer(String contenidoJson, FormaDto forma) {
        JsonNode datos;
        try {
            datos = JSON.readTree(contenidoJson).path("datos");
        } catch (JacksonException e) {
            return CamposLeidos.invalidos();
        }
        if (!datos.isObject()) {
            return CamposLeidos.invalidos();
        }
        Map<String, Presencia> campos = new HashMap<>();
        for (String campo : forma.campos()) {
            JsonNode valor = datos.path(campo);
            campos.put(campo, valor.isMissingNode() || valor.isNull() ? Presencia.FALTA : Presencia.HAY);
        }
        JsonNode motivo = datos.path(forma.campoMotivoAusencia());
        boolean hayMotivo = motivo.isString() && !motivo.asString().isBlank();
        return new CamposLeidos(true, campos, hayMotivo);
    }
}
