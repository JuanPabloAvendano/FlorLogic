package co.florlogic.finca.ingesta.dominio.comun;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * El contenido de negocio de una anotación (ESPEC-dominio-ingesta A-65): su JSON equivalente (A-59), opaco para el
 * dominio, y la presencia de cada campo, que es lo único que juzga la regla del dato ausente (A-69). Sin Jackson en
 * el dominio: la infraestructura llena las presencias desde el {@code ValorDto} del contrato.
 *
 * @param json la anotación como JSON equivalente; el dominio nunca lo lee
 * @param campos la presencia de cada campo, por nombre; copia no modificable (E-16)
 */
public record CapturaValor(String json, Map<String, Presencia> campos) {

    /**
     * Valida y lanza: solo atrapa errores de programación (DI1-7). La copia hace que cambiar el mapa de origen
     * después no cambie la captura.
     */
    public CapturaValor {
        Objects.requireNonNull(json, "json");
        campos = Map.copyOf(Objects.requireNonNull(campos, "campos"));
    }

    /**
     * La frontera: {@code null} en el JSON, en el mapa, en una clave o en una presencia da vacío, nunca lanza (A-57).
     * Se revisa antes de copiar porque {@code Map.copyOf} lanza con claves o valores nulos (DI1-5).
     */
    public static Optional<CapturaValor> de(String json, Map<String, Presencia> campos) {
        if (json == null || campos == null) {
            return Optional.empty();
        }
        // Por las entradas y no con containsKey(null): un Map.of lanza si se le pregunta por null.
        for (Map.Entry<String, Presencia> campo : campos.entrySet()) {
            if (campo.getKey() == null || campo.getValue() == null) {
                return Optional.empty();
            }
        }
        return Optional.of(new CapturaValor(json, campos));
    }
}
