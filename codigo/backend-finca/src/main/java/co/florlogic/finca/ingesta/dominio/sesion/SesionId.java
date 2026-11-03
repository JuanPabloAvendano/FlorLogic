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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record con constructor compacto: Objects.requireNonNull lanza si el uuid es null.
 * - Optional.ofNullable(valor).map(SesionId::new): vacío si valor es null; si no, construye el id. SesionId::new es
 *   una referencia al constructor.
 */
