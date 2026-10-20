package co.florlogic.contrato.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import co.florlogic.contrato.lectura.IdDeCatalogo;

/**
 * Una anotación con algo que mostrar en la respuesta (A-23). No es una excepción de Java: es el
 * nombre del contrato para «lo que salió distinto de aplicada sin códigos».
 *
 * @param codigos ids cortos y sin texto: id de regla del catálogo ({@code R-…}) o código del
 *     ingreso ({@link co.florlogic.contrato.codigos.CodigoIngreso}). Son texto y no un enum porque
 *     los ids de regla los crea el catálogo, no el contrato
 */
public record Excepcion(
        @JsonProperty("anotacion_uuid") UUID anotacionUuid,
        @JsonProperty("resultado") Resultado resultado,
        @JsonProperty("codigos") List<String> codigos) {

    public Excepcion {
        codigos = Listas.copia(codigos);
    }

    /**
     * {@code aplicada} no aparece aquí: una aplicada con códigos sale como {@code marcada}.
     * {@code DESCONOCIDO}: un nodo de finca más nuevo mandó otro resultado; el celular sigue leyendo
     * la respuesta y no borra esa anotación (A-57).
     */
    public enum Resultado implements IdDeCatalogo {
        MARCADA("marcada"),
        RECHAZADA("rechazada"),
        /** Ya estaba guardada, o es {@code obsoleta} (A-45 ⟦1⟧). */
        SIN_EFECTO("sin_efecto"),
        DESCONOCIDO(null);

        private final String id;

        Resultado(String id) {
            this.id = id;
        }

        @JsonValue
        @Override
        public String id() {
            return id;
        }

        @JsonCreator
        public static Resultado de(String id) {
            return IdDeCatalogo.buscar(values(), id, DESCONOCIDO);
        }
    }
}
