package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

import java.util.Optional;
import java.util.UUID;

import co.florlogic.finca.ingesta.servicios.output.dto.AvanceDto;

/**
 * Puerto de salida: dónde queda el avance de cada celular (plantilla adaptador-persistencia 10.1; ESPEC-dominio-ingesta
 * §5 «Puertos de ingesta»). Por aquí cruza un DTO, nunca el agregado (Martin · Clean Architecture p. 195).
 */
public interface AvanceRepositorio {

    /** El avance guardado de ese celular, o vacío si todavía no se ha recibido nada de él. */
    Optional<AvanceDto> deDispositivo(UUID dispositivoId);

    /**
     * Guarda el avance con versión optimista: {@code avance.version()} es la que se leyó (0 si era nuevo). Si en la
     * base ya hay otra, otra partición del mismo celular ganó primero.
     *
     * @throws ChoqueConcurrente si la versión no coincide o el avance «nuevo» ya existe (A-98)
     */
    void guardar(AvanceDto avance);
}
