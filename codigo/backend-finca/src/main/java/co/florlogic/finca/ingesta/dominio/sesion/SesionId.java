package co.florlogic.finca.ingesta.dominio.sesion;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Identidad de una sesión de sincronización: UUID v7 que genera el nodo al abrirla. */
public record SesionId(UUID valor) {

    public SesionId {
        Objects.requireNonNull(valor, "valor");
    }

    /** La frontera: {@code null} da vacío, nunca lanza (A-57). */
    public static Optional<SesionId> de(UUID valor) {
        return Optional.ofNullable(valor).map(SesionId::new);
    }
}
