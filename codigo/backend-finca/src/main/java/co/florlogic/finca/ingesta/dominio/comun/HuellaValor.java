package co.florlogic.finca.ingesta.dominio.comun;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * El resumen SHA-256 del contenido de una anotación (ESPEC-dominio-ingesta A-65). Dice si dos llegadas del mismo id
 * traen el mismo contenido: misma huella → reenvío; otra → corrupta (A-2).
 *
 * <p>La calcula la infraestructura con la forma canónica (K-115); el dominio solo la compara con {@code equals}.
 * Solo tiene sentido comparar huellas de la misma versión: recalcular con la versión de la guardada es de la
 * infraestructura (COH-132, después).
 *
 * @param version la versión del cálculo: {@code "alg": "h1"} en el cable → 1 (A-33)
 * @param hex los 64 caracteres del SHA-256 en hexadecimal, en minúsculas
 */
public record HuellaValor(int version, String hex) {

    // Minúsculas, como los ids del contrato (A-38, DI1-6).
    private static final Pattern HEX_SHA256 = Pattern.compile("[0-9a-f]{64}");

    /** Valida y lanza: solo atrapa errores de programación (DI1-7). */
    public HuellaValor {
        Objects.requireNonNull(hex, "hex");
        if (!valida(version, hex)) {
            // Solo la llave y los valores (OBS-1-6, VER-3).
            throw new IllegalArgumentException("huella.invalida: version=" + version + ", hex=" + hex);
        }
    }

    /** La frontera: versión menor que 1, {@code null} o un hex que no son 64 {@code [0-9a-f]} dan vacío (A-57). */
    public static Optional<HuellaValor> de(int version, String hex) {
        return hex != null && valida(version, hex) ? Optional.of(new HuellaValor(version, hex)) : Optional.empty();
    }

    private static boolean valida(int version, String hex) {
        return version >= 1 && HEX_SHA256.matcher(hex).matches();
    }
}
