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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component (Spring): Spring crea una instancia y la entrega a quien pida LectorDeContenido (el caso de uso).
 * - JsonMapper.builder().build() (Jackson 3, paquetes tools.jackson): el lector de JSON, propio y estático; ya
 *   construido se comparte entre hilos sin problema.
 * - JSON.readTree(contenidoJson): lee el texto como árbol de JsonNode; un JSON roto lanza JacksonException, que aquí se
 *   convierte en CamposLeidos.invalidos() (por eso el lector nunca lanza).
 * - path("datos"): baja al hijo datos; si no existe devuelve un MissingNode en vez de null.
 * - datos.isObject(): exige que datos sea un objeto JSON { … }; un número, un texto o la ausencia son inválidos.
 * - valor.isMissingNode() || valor.isNull(): el campo no vino o vino como null → FALTA; cualquier otro valor, incluso
 *   0 o un texto vacío, cuenta como HAY.
 * - motivo.isString() && !motivo.asString().isBlank(): el motivo solo cuenta si es texto y no son solo espacios
 *   (asString es el nombre en Jackson 3 de lo que antes era asText).
 * - HashMap: el mapa campo → Presencia; el orden no importa porque CamposLeidos hace su propia copia.
 */
