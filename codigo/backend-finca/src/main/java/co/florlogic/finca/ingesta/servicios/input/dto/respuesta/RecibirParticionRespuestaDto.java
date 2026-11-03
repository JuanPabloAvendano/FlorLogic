package co.florlogic.finca.ingesta.servicios.input.dto.respuesta;

import java.util.List;
import java.util.UUID;

/**
 * Lo que sale por el puerto de entrada: los conteos de la partición (contrato A-26) y las anotaciones que el celular
 * tiene que ver una por una (A-23).
 *
 * @param confirmadoHasta el mayor consecutivo del celular recibido sin huecos; 0 si nunca se ha recibido nada
 */
public record RecibirParticionRespuestaDto(int recibidas, int aplicadas, int marcadas, int rechazadas, int sinEfecto,
        long confirmadoHasta, List<Excepcion> excepciones) {

    public RecibirParticionRespuestaDto {
        excepciones = List.copyOf(excepciones);
    }

    /** @param resultado {@code marcada} o {@code rechazada}; @param codigos en minúscula, como en el contrato */
    public record Excepcion(UUID anotacionUuid, String resultado, List<String> codigos) {

        public Excepcion {
            codigos = List.copyOf(codigos);
        }
    }
}
