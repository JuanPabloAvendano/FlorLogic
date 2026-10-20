package co.florlogic.contrato.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Cuántas anotaciones de la partición terminaron en cada resultado (A-26).
 * {@code recibidas = aplicadas + rechazadas + sin_efecto}, y {@code marcadas} es parte de
 * {@code aplicadas}: una marcada sí entró, solo que resaltada para el gerente.
 *
 *
 * <p>No lanza si los números no cuadran (A-57): {@link #incoherencias()} las nombra, al estilo del
 * patrón <em>Notification</em> (Fowler). El nodo de finca la revisa antes de responder; el celular,
 * al leer, la trata como anomalía y no borra nada por esa respuesta.
 *
 * @param sinEfecto ya guardadas (reenvío) u obsoletas (A-45 ⟦1⟧)
 */
public record Conteo(
        @JsonProperty("recibidas") int recibidas,
        @JsonProperty("aplicadas") int aplicadas,
        @JsonProperty("marcadas") int marcadas,
        @JsonProperty("rechazadas") int rechazadas,
        @JsonProperty("sin_efecto") int sinEfecto) {

    /** Las reglas de A-26 y §3b que este conteo rompe; vacía si cuadra. */
    @JsonIgnore
    public List<String> incoherencias() {
        List<String> encontradas = new ArrayList<>();
        if (recibidas < 0 || aplicadas < 0 || marcadas < 0 || rechazadas < 0 || sinEfecto < 0) {
            encontradas.add("conteo_negativo");
        }
        if (recibidas != aplicadas + rechazadas + sinEfecto) {
            encontradas.add("conteo_no_suma");
        }
        if (marcadas > aplicadas) {
            encontradas.add("marcadas_mayor_que_aplicadas");
        }
        return List.copyOf(encontradas);
    }
}
