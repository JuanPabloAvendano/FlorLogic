package co.florlogic.contrato.interno;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

/**
 * La huella de una anotación (contrato A-19, A-33; ESPEC-dominio-ingesta A-86): SHA-256 en hexadecimal minúsculo de su
 * forma canónica, después de quitar los {@code null} de los objetos a cualquier nivel (null ≡ ausente; los arreglos
 * conservan sus null). Versión {@code h1}. Fachada sin estado: el nodo la recalcula siempre y nunca usa una que mande
 * el celular.
 */
public final class HuellaCanonica {

    /** La versión del cálculo: {@code "alg": "h1"}. */
    public static final int VERSION = 1;

    private HuellaCanonica() {
    }

    public static String de(JsonNode anotacion) {
        try {
            byte[] bytes = Jcs.canonica(sinNulls(anotacion)).getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible en esta JVM", e);   // la exige toda JVM
        }
    }

    /** Copia sin los miembros {@code null} de los objetos, a cualquier profundidad. */
    static JsonNode sinNulls(JsonNode nodo) {
        if (nodo == null) {
            return JsonNodeFactory.instance.nullNode();
        }
        if (nodo.isObject()) {
            ObjectNode copia = JsonNodeFactory.instance.objectNode();
            for (String nombre : nodo.propertyNames()) {
                JsonNode valor = nodo.get(nombre);
                if (valor != null && !valor.isNull()) {
                    copia.set(nombre, sinNulls(valor));
                }
            }
            return copia;
        }
        if (nodo.isArray()) {
            ArrayNode copia = JsonNodeFactory.instance.arrayNode();
            for (JsonNode elemento : nodo) {
                copia.add(sinNulls(elemento));
            }
            return copia;
        }
        return nodo;
    }
}
