package co.florlogic.finca.ingesta.dominio.comun;

/**
 * Qué pasó con una anotación al llegar (ESPEC-dominio-ingesta A-66). Los tres primeros son el juicio de una
 * anotación nueva; {@link #SIN_EFECTO} solo describe la llegada de un id que ya existía y nunca se guarda.
 */
public enum Resultado {

    /** Nueva y sin problema: se guarda sin códigos ni evento. */
    APLICADA,

    /** Nueva y guardada, con al menos un código de marca para que el gerente la revise. */
    MARCADA,

    /** No se acepta: lleva su motivo y ningún código (INV-3). También la corrupta. */
    RECHAZADA,

    /** El mismo id con la misma huella: un reenvío, que no cambia nada (A-2). */
    SIN_EFECTO
}
