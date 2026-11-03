package co.florlogic.finca.ingesta.dominio.comun;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * La identidad de una anotación (ESPEC-dominio-ingesta A-64). La asigna el celular, nunca el dominio (E-14), y es
 * UUID v7, igual que {@code anotacion_uuid} en el contrato (A-38). Identidad asignada por otro contexto:
 * <i>Implementing Domain-Driven Design · p. 216-217</i>.
 *
 * @param valor el uuid v7 que puso el celular
 */
public record AnotacionId(UUID valor) {

    private static final int VERSION_EXIGIDA = 7;

    /**
     * Valida y lanza: solo atrapa errores de programación (DI1-7). Java exige que el constructor de un {@code record}
     * público sea público, así que la frontera es {@link #de(UUID)}.
     */
    public AnotacionId {
        Objects.requireNonNull(valor, "valor");
        if (valor.version() != VERSION_EXIGIDA) {
            // Solo la llave y el valor (OBS-1-6, VER-3).
            throw new IllegalArgumentException("anotacion_id.no_es_uuid_v7: " + valor);
        }
    }

    /**
     * La frontera: {@code null} o un uuid que no es v7 dan vacío, nunca lanza (A-57). Un vacío aquí solo dice «el
     * contrato no filtró esto»: la partición entera es {@code 422 sobre-invalido} (A-38, COH-113).
     */
    public static Optional<AnotacionId> de(UUID valor) {
        return valor != null && valor.version() == VERSION_EXIGIDA ? Optional.of(new AnotacionId(valor))
                : Optional.empty();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record con constructor compacto (public AnotacionId { … }): valida antes de guardar el campo y lanza si el uuid
 *   no sirve.
 * - valor.version(): el número de versión que trae el propio UUID; 7 es el uuid ordenado por tiempo que exige el
 *   contrato.
 * - IllegalArgumentException: la excepción estándar de Java para «argumento inválido»; aquí solo sale ante un error
 *   de programación.
 * - Optional.of(...) / Optional.empty() con el operador ternario «condición ? a : b»: de(...) devuelve vacío en vez
 *   de lanzar.
 */
