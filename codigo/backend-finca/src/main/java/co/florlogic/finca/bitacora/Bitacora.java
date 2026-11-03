package co.florlogic.finca.bitacora;

import java.util.Map;

/**
 * API pública de la bitácora (IA_CONSTRUCCION-TX1 CT-11; ESC-40): registro de solo inserción con cadena de hashes. Se
 * llama dentro de la transacción de quien registra: si esa transacción se deshace, la entrada también.
 */
public interface Bitacora {

    /** @param detalle sin datos de negocio (ADR-026): ids, conteos, códigos */
    void registrar(String actor, String evento, String objeto, Map<String, String> detalle);

    /** {@code true} si cada entrada encadena con la anterior y su hash es el que le corresponde. */
    boolean cadenaIntegra();
}
