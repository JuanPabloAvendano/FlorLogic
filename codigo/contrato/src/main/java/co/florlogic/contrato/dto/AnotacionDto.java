package co.florlogic.contrato.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import co.florlogic.contrato.lectura.IdDeCatalogoInterfaz;

import tools.jackson.databind.JsonNode;

/**
 * Sobre genérico de una anotación (A-15): lo común a todo tipo, más los {@code datos} propios
 * del tipo. Es un <b>Envelope Wrapper</b>: el sobre lleva los datos de control y envuelve el contenido
 * (<em>POSA 4</em> · p. 267, que cita a Hohpe y Woolf). Hoy el único tipo es {@code captura.campo} ({@link co.florlogic.contrato.dto.tipos.CapturaCampoDto}).
 *
 * <p>{@code datos} queda como árbol JSON y no como un tipo de Java: cada tipo valida sus datos con
 * su propio esquema (A-15) y un tipo nuevo no obliga a cambiar este record. Tampoco se usa
 * herencia polimórfica de Jackson, que A-9 prohíbe.
 *
 * <p>La huella no se calcula sobre este record sino sobre los bytes recibidos (A-33): leerlo y
 * volverlo a escribir puede cambiar el orden o los números.
 *
 * @param consecutivo orden en el almacén de salida del celular; los huecos se marcan, no se impiden (A-27)
 * @param versionTipo versión de la forma de {@code datos} para ese {@code tipo}
 * @param versionConfig versión del paquete de configuración con que juzgó el celular
 */
public record AnotacionDto(
        @JsonProperty("anotacion_uuid") UUID anotacionUuid,
        @JsonProperty("consecutivo") long consecutivo,
        @JsonProperty("tipo") String tipo,
        @JsonProperty("version_tipo") int versionTipo,
        @JsonProperty("sesion_captura_uuid") UUID sesionCapturaUuid,
        @JsonProperty("tiempo") TiempoDto tiempo,
        @JsonProperty("version_config") int versionConfig,
        @JsonProperty("veredicto_local") VeredictoLocal veredictoLocal,
        @JsonProperty("datos") JsonNode datos) {

    /**
     * Lo que el celular rechaza no viaja (A-21), así que solo hay dos veredictos.
     * {@code DESCONOCIDO}: llegó otro texto; el validador rechaza la anotación (A-32), no la partición.
     */
    public enum VeredictoLocal implements IdDeCatalogoInterfaz {
        ACEPTADA("aceptada"),
        MARCADA("marcada"),
        DESCONOCIDO(null);

        private final String id;

        VeredictoLocal(String id) {
            this.id = id;
        }

        @JsonValue
        @Override
        public String id() {
            return id;
        }

        @JsonCreator
        public static VeredictoLocal de(String id) {
            return IdDeCatalogoInterfaz.buscar(values(), id, DESCONOCIDO);
        }
    }
}
