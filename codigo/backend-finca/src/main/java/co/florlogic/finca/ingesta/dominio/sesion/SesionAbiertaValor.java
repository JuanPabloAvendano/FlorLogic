package co.florlogic.finca.ingesta.dominio.sesion;

/**
 * Objeto de valor: la sesión recién abierta y el punto de reanudación del celular (recorrido de TX-1, paso 6).
 *
 * @param confirmadoHasta lo que el nodo ya tiene sin huecos de ese celular; 0 si nada
 */
public record SesionAbiertaValor(SesionDeSincronizacion sesion, long confirmadoHasta) {
}
