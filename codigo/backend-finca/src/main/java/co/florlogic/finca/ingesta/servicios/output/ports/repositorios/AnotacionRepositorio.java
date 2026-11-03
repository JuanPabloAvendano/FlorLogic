package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRechazadaDto;
import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDto;
import co.florlogic.finca.ingesta.servicios.output.dto.GuardadaDto;

/**
 * Puerto de salida: dónde quedan las anotaciones juzgadas (ESPEC-dominio-ingesta §5 «Puertos de ingesta»). Se lee en
 * lote lo ya guardado de los ids de la partición y después se guarda cada una; sin {@code ON CONFLICT}: si otra
 * partición guardó el mismo id entre la lectura y la escritura, choca (A-98).
 */
public interface AnotacionRepositorio {

    /** Lo ya guardado de esos ids, aceptadas y rechazadas; un id que no aparece es nuevo. */
    Map<UUID, GuardadaDto> guardadasCon(Collection<UUID> ids);

    /** @throws ChoqueConcurrente si el id ya existe (otra partición lo guardó primero) */
    void guardarAceptada(AnotacionRecibidaDto anotacion);

    /** Guarda el rechazo; el mismo id con la misma huella ya rechazado no se repite. */
    void guardarRechazada(AnotacionRechazadaDto anotacion);
}
