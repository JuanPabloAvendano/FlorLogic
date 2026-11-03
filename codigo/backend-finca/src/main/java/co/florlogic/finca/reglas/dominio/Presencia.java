package co.florlogic.finca.reglas.dominio;

/** Cómo viene un campo esperado en {@code datos} (A-99, contrato A-35). */
public enum Presencia {
    /** Trae un valor. */
    HAY,
    /** No está, o es {@code null}: null ≡ ausente (contrato A-33). */
    FALTA
}
