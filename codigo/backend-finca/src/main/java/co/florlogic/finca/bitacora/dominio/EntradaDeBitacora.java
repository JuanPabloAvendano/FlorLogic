package co.florlogic.finca.bitacora.dominio;

import java.time.Instant;
import java.util.Objects;

/** Una entrada de la bitácora, ya encadenada: su hash cubre todos sus campos y el hash de la anterior. */
public record EntradaDeBitacora(Instant momento, String actor, String evento, String objeto, String detalle,
        String hashAnterior, String hash) {

    public EntradaDeBitacora {
        Objects.requireNonNull(momento, "momento");
        Objects.requireNonNull(hash, "hash");
    }
}
