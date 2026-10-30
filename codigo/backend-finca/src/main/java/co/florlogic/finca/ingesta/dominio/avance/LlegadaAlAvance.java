package co.florlogic.finca.ingesta.dominio.avance;

/**
 * Qué hizo {@link AvanceDelDispositivoAgregado#registrar} con un consecutivo (ESPEC-dominio-ingesta VER-6, 29-oct): un
 * resultado con nombre en vez de un {@code void} o una excepción (A-57). Ninguno pierde la anotación: se guarda aparte,
 * y lo que el avance no recuerda el celular lo vuelve a mandar desde {@code confirmadoHasta} (A-72).
 */
public enum LlegadaAlAvance {
    /** Ya estaba cubierto por {@code confirmadoHasta}: un reenvío; nada cambia (C-91). */
    YA_CUBIERTO,
    /** Era el siguiente: {@code confirmadoHasta} subió, con lo que estuviera esperando contiguo en {@code porEncima}. */
    AVANZA,
    /** Dejó un hueco: queda en {@code porEncima} hasta que llegue lo que falta (A-72). También si ya esperaba ahí. */
    EN_ESPERA,
    /**
     * Dejó un hueco y {@code porEncima} está lleno ({@link AvanceDelDispositivoAgregado#TOPE_POR_ENCIMA}): no se
     * recuerda. No se pierde: el celular reanuda desde {@code confirmadoHasta} y lo reenvía cuando el hueco se llene.
     */
    FUERA_DE_VENTANA
}
