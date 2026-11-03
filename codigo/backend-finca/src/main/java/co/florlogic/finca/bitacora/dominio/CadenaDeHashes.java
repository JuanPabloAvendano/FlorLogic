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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - final class con constructor private: nadie hereda de ella ni crea instancias; solo se usan sus métodos static.
 * - "0".repeat(64): 64 ceros, el «hash anterior» de la primera entrada; son 64 porque un SHA-256 escrito en
 *   hexadecimal ocupa 64 caracteres, igual que cualquier otro hash de la cadena.
 * - new TreeMap<>(detalle): copia el detalle a un mapa que ordena sus claves; así el mismo detalle da siempre el mismo
 *   texto (y el mismo hash) aunque las claves lleguen en otro orden. Lo prueba CadenaDeHashesTest.
 * - forEach((k, v) -> ...): lambda que recorre cada par clave-valor; StringBuilder arma el texto pedazo a pedazo.
 * - static String detalle(...) sin public: visible solo en el paquete; lo usan este archivo y su prueba.
 * - String.join con el carácter barra-u 001f: une los campos con el «separador de unidad» de ASCII (código 31), que no
 *   aparece en texto normal; así actor «ab» + evento «c» no da la misma línea que «a» + «bc».
 * - momento.toString(): el Instant entra al hash como texto ISO-8601; por eso quien registra lo trunca antes a
 *   microsegundos: si no, el texto releído de la base no coincidiría y la cadena se vería rota.
 * - getBytes(StandardCharsets.UTF_8): texto a bytes con una codificación fija; el hash no depende de la máquina.
 * - MessageDigest.getInstance("SHA-256").digest(bytes) (JDK): calcula el resumen SHA-256, 32 bytes.
 * - HexFormat.of().formatHex(...) (JDK): escribe esos 32 bytes como 64 caracteres hexadecimales, como se guardan.
 * - catch NoSuchAlgorithmException → IllegalStateException: el compilador obliga a tratar esa excepción comprobada;
 *   toda JVM trae SHA-256, así que si falta es un error del entorno y se relanza sin comprobación.
 */
