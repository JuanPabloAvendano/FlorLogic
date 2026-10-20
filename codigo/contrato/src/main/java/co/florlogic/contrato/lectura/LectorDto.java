package co.florlogic.contrato.lectura;

import java.util.List;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Barrera de lectura del contrato (A-57): convierte un árbol JSON ya leído en un DTO sin lanzar
 * nunca. Todo lo imprevisto, también un error de programación dentro de un constructor, sale como
 * {@link Lectura.Fallida} con su {@code pointer}.
 *
 * <p>Hay dos lectores porque cada lado del cable lee distinto (OBS-1-5):
 * <ul>
 *   <li>{@link #estricto()}: el nodo de finca leyendo peticiones. Un campo que el contrato no conoce es
 *       {@code campo_desconocido} (A-9, A-32): nada desconocido entra (A-50). Es una desviación
 *       consciente de la ley de Postel, que pide ser liberal con lo que se acepta
 *       (<em>Principles and Patterns for Distributed Application Architecture</em> · p. 123): aquí el
 *       nodo de finca crea todas las versiones, así que un campo desconocido es una anomalía.</li>
 *   <li>{@link #tolerante()}: el celular leyendo respuestas. Ignora los campos de más, como pide el
 *       principio de robustez: «clients should ignore any extra response attributes»
 *       (<em>Microservices Patterns</em> · p. 100). Así un nodo de finca más nuevo no detiene al celular.</li>
 * </ul>
 *
 * <p>El árbol sigue siendo la fuente (A-33): la huella y el esquema se calculan sobre él, no sobre el
 * DTO. No atrapa {@link Error} (memoria agotada, pila desbordada): eso no es una entrada mala sino el
 * proceso en peligro, y lo tiene que ver quien supervisa el proceso.
 */
public final class LectorDto {

    // Jackson 3: ObjectMapper es inmutable y seguro entre hilos; se comparte.
    private static final LectorDto ESTRICTO = new LectorDto(JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build());

    private static final LectorDto TOLERANTE = new LectorDto(JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build());

    private final ObjectMapper mapper;

    private LectorDto(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /** Para el nodo de finca: lee peticiones y rechaza lo que el contrato no conoce. */
    public static LectorDto estricto() {
        return ESTRICTO;
    }

    /** Para el celular: lee respuestas e ignora lo que trae de más una versión más nueva. */
    public static LectorDto tolerante() {
        return TOLERANTE;
    }

    public <T> Lectura<T> leer(JsonNode arbol, Class<T> tipo) {
        if (arbol == null || arbol.isMissingNode() || arbol.isNull()) {
            return new Lectura.Fallida<>("", "documento_ausente");
        }
        try {
            T valor = mapper.treeToValue(arbol, tipo);
            return valor == null ? new Lectura.Fallida<>("", "documento_ausente") : new Lectura.Leida<>(valor);
        } catch (JacksonException e) {
            return new Lectura.Fallida<>(pointer(e.getPath()), clase(e));
        } catch (RuntimeException e) {
            // Barrera de fallos: un defecto de este módulo no debe parar a quien lo usa.
            return new Lectura.Fallida<>("", "fallo_inesperado");
        }
    }

    private static String clase(JacksonException e) {
        // El orden importa: las dos primeras son subclases de MismatchedInputException.
        if (e instanceof UnrecognizedPropertyException) {
            return "campo_desconocido";
        }
        if (e instanceof InvalidFormatException) {
            return "formato_invalido";
        }
        if (e instanceof MismatchedInputException) {
            return "tipo_incorrecto";
        }
        return "json_ilegible";
    }

    /** Arma el JSON Pointer (RFC 6901) desde la ruta que reporta Jackson. */
    static String pointer(List<JacksonException.Reference> ruta) {
        StringBuilder sb = new StringBuilder();
        for (JacksonException.Reference paso : ruta) {
            sb.append('/');
            if (paso.getPropertyName() != null) {
                sb.append(paso.getPropertyName().replace("~", "~0").replace("/", "~1"));
            } else if (paso.getIndex() >= 0) {
                sb.append(paso.getIndex());
            }
        }
        return sb.toString();
    }
}
