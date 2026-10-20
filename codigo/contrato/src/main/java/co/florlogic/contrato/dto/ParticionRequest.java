package co.florlogic.contrato.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cuerpo de {@code POST /ingesta/v1/sesiones/{id}/particiones}: un trozo de lo pendiente de un
 * solo celular (A-30). El dispositivo no viaja aquí: es el de la sesión de la ruta (A-52).
 *
 * <p>Ningún DTO lanza en su constructor (A-57), y los de la petición tampoco validan. Una anotación mala tiene que
 * terminar {@code rechazada} y no tumbar la partición (A-14), y eso lo decide el validador
 * (Segmento 6), no una excepción al leer el JSON.
 *
 * @param contrato versión menor, p. ej. {@code "1.0"}; la mayor va en la ruta (A-24)
 * @param particionUuid solo correlaciona; no es llave de idempotencia (A-29)
 * @param descartes consecutivos que el celular borró sin entregar (A-47); opcional
 * @param primerosAcuses solo tras una restauración (A-45 ⟦2⟧b); opcional
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ParticionRequest(
        @JsonProperty("contrato") String contrato,
        @JsonProperty("particion_uuid") UUID particionUuid,
        @JsonProperty("sesiones_captura") @JsonInclude(JsonInclude.Include.ALWAYS) List<SesionCaptura> sesionesCaptura,
        // Siempre se escribe, aunque vaya vacío: si falta, es 422 sobre-invalido (A-32).
        @JsonProperty("anotaciones") @JsonInclude(JsonInclude.Include.ALWAYS) List<Anotacion> anotaciones,
        @JsonProperty("descartes") List<Descarte> descartes,
        @JsonProperty("primeros_acuses") List<PrimerAcuse> primerosAcuses) {

    public ParticionRequest {
        // Ausente o null se guardan como lista vacía para que nadie pregunte por null (A-34). No dice que
        // ausente sea válido: que falte anotaciones[] es 422, y lo decide el validador (A-32, A-58). Nunca lanza (A-57).
        sesionesCaptura = Listas.copia(sesionesCaptura);
        anotaciones = Listas.copia(anotaciones);
        descartes = Listas.copia(descartes);
        primerosAcuses = Listas.copia(primerosAcuses);
    }
}
