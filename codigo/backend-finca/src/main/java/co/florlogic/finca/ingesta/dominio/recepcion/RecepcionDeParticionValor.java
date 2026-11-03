package co.florlogic.finca.ingesta.dominio.recepcion;

import java.util.List;

/**
 * Objeto de valor: lo que pasó al recibir una partición (contrato A-26): los conteos, el avance real del celular y
 * las anotaciones que el celular tiene que ver una por una (las marcadas y las rechazadas, A-23).
 *
 * @param marcadas incluidas también en {@code aplicadas} (marcadas ⊆ aplicadas)
 * @param confirmadoHasta el mayor consecutivo del celular recibido sin huecos; 0 si nunca se ha recibido nada (A-31)
 */
public record RecepcionDeParticionValor(int recibidas, int aplicadas, int marcadas, int rechazadas, int sinEfecto,
        long confirmadoHasta, List<JuicioDeAnotacion> excepciones) {

    public RecepcionDeParticionValor {
        excepciones = List.copyOf(excepciones);
    }
}
