package co.florlogic.contrato.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import co.florlogic.contrato.lectura.IdDeCatalogoInterfaz;

/**
 * Aviso de que el celular borró un consecutivo sin entregarlo (A-47). Con el aviso, el nodo de
 * finca cierra el hueco en vez de esperar algo que nunca va a llegar.
 *
 * @param consecutivo el número del almacén de salida del celular
 */
public record DescarteDto(
        @JsonProperty("consecutivo") long consecutivo,
        @JsonProperty("motivo") Motivo motivo) {

    /** {@code DESCONOCIDO}: un descarte mal formado se ignora y va a la bitácora (A-55). */
    public enum Motivo implements IdDeCatalogoInterfaz {
        /** La anotación no se podía leer o no cumplía el contrato (A-53 ⟦1⟧). */
        FALLO_TECNICO("fallo_tecnico"),
        DESCARTADA_POR_GERENTE("descartada_por_gerente"),
        DESCONOCIDO(null);

        private final String id;

        Motivo(String id) {
            this.id = id;
        }

        @JsonValue
        @Override
        public String id() {
            return id;
        }

        @JsonCreator
        public static Motivo de(String id) {
            return IdDeCatalogoInterfaz.buscar(values(), id, DESCONOCIDO);
        }
    }
}
