package co.florlogic.finca.bitacora.dominio;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * La cadena de la bitácora (CT-11): cada hash es SHA-256 de la entrada y del hash anterior; la primera encadena con
 * {@link #ORIGEN}. Cambiar una entrada vieja cambia su hash y rompe a todas las que siguen. Java puro.
 */
public final class CadenaDeHashes {

    public static final String ORIGEN = "0".repeat(64);

    private CadenaDeHashes() {
    }

    public static EntradaDeBitacora encadenar(String hashAnterior, Instant momento, String actor, String evento,
            String objeto, Map<String, String> detalle) {
        String texto = detalle(detalle);
        return new EntradaDeBitacora(momento, actor, evento, objeto, texto, hashAnterior,
                hash(hashAnterior, momento, actor, evento, objeto, texto));
    }

    /** {@code true} si la lista, en orden, encadena desde {@link #ORIGEN} y cada hash es el correcto. */
    public static boolean integra(List<EntradaDeBitacora> entradas) {
        String anterior = ORIGEN;
        for (EntradaDeBitacora e : entradas) {
            if (!e.hashAnterior().equals(anterior)
                    || !e.hash().equals(hash(anterior, e.momento(), e.actor(), e.evento(), e.objeto(), e.detalle()))) {
                return false;
            }
            anterior = e.hash();
        }
        return true;
    }

    /** El detalle como texto estable: claves en orden, una por línea. */
    static String detalle(Map<String, String> detalle) {
        StringBuilder texto = new StringBuilder();
        new TreeMap<>(detalle).forEach((k, v) -> texto.append(k).append('=').append(v).append('\n'));
        return texto.toString();
    }

    private static String hash(String anterior, Instant momento, String actor, String evento, String objeto,
            String detalle) {
        String linea = String.join("\u001f", anterior, momento.toString(), actor, evento, objeto, detalle);
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(linea.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible en esta JVM", e);
        }
    }
}
