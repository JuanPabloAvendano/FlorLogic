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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, los accesores (sin «get»: momento(), hash()…), equals, hashCode y toString a partir de la lista de componentes.
 * - public EntradaDeBitacora { ... }: constructor compacto del record; corre antes de guardar los campos. Aquí solo
 *   exige que momento y hash existan.
 * - Objects.requireNonNull(x, "nombre"): lanza NullPointerException con ese nombre si x es null.
 */
