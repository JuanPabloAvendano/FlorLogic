package co.florlogic.finca.ingesta.dominio.comun;

/**
 * Por qué una anotación queda {@code MARCADA} (ESPEC-dominio-ingesta A-69, A-71). Es propio del dominio: el
 * {@code CodigoIngreso} del contrato no entra aquí (A-66).
 */
public enum CodigoMarca {

    /** Le falta un dato (A-35). */
    INCOMPLETA,

    /** Le falta un dato y nadie dijo por qué. */
    MOTIVO_AUSENTE,

    /** Llegó con números sin recibir por debajo: falta algo en el camino (A-27, A-72). */
    HUECO_CONSECUTIVO
}
