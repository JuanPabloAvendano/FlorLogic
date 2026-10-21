package co.florlogic.finca.ingesta.dominio.comun;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * La identidad de un celular (ESPEC-dominio-ingesta A-64). La asigna el registro de dispositivos, así que el dominio
 * solo exige que exista; no exige versión de uuid.
 *
 * @param valor el uuid del dispositivo
 */
public record DispositivoId(UUID valor) {

    /** Valida y lanza: solo atrapa errores de programación (DI1-7). */
    public DispositivoId {
        Objects.requireNonNull(valor, "valor");
    }

    /** La frontera: {@code null} da vacío, nunca lanza (A-57, DI1-2: toda fábrica cumple el escenario 6 igual). */
    public static Optional<DispositivoId> de(UUID valor) {
        return valor != null ? Optional.of(new DispositivoId(valor)) : Optional.empty();
    }
}
