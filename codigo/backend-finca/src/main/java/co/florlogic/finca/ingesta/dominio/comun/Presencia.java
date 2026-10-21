package co.florlogic.finca.ingesta.dominio.comun;

/**
 * Cómo viene un campo de la captura (ESPEC-dominio-ingesta A-69). La infraestructura lo traduce del Null Object
 * {@code ValorDto} del contrato (A-34): {@code Hay} → {@link #CON_VALOR}, {@code Falta} con motivo →
 * {@link #AUSENTE_CON_MOTIVO}, {@code Falta} sin motivo → {@link #AUSENTE_SIN_MOTIVO}, {@code Contradictorio} →
 * {@link #VALOR_CON_MOTIVO_AUSENCIA}.
 */
public enum Presencia {

    /** Trae su valor: no marca. */
    CON_VALOR,

    /** Falta y el operario dijo por qué ({@code motivo_ausencia}): marca {@code INCOMPLETA}. */
    AUSENTE_CON_MOTIVO,

    /** Falta y nadie dijo por qué: marca {@code INCOMPLETA} y {@code MOTIVO_AUSENTE}. */
    AUSENTE_SIN_MOTIVO,

    /** Trae valor y a la vez motivo de ausencia: se contradice, es un fallo de forma y se rechaza (A-2 ⟦2⟧). */
    VALOR_CON_MOTIVO_AUSENCIA
}
