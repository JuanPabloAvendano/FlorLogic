package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRechazadaDto;
import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDto;
import co.florlogic.finca.ingesta.servicios.output.dto.GuardadaDto;

/** Doble en memoria de {@link AnotacionRepositorio}: cumple el mismo contrato que el adaptador JDBC. */
public final class AnotacionRepositorioDobleEnMemoria implements AnotacionRepositorio {

    private final Map<UUID, AnotacionRecibidaDto> aceptadas = new ConcurrentHashMap<>();
    private final Map<String, AnotacionRechazadaDto> rechazadas = new ConcurrentHashMap<>();

    @Override
    public Map<UUID, GuardadaDto> guardadasCon(Collection<UUID> ids) {
        Map<UUID, GuardadaDto> guardadas = new HashMap<>();
        for (AnotacionRechazadaDto r : rechazadas.values()) {
            if (ids.contains(r.anotacionUuid())) {
                guardadas.putIfAbsent(r.anotacionUuid(),
                        new GuardadaDto(r.anotacionUuid(), r.huella(), "RECHAZADA", List.of(r.motivo())));
            }
        }
        for (UUID id : ids) {
            AnotacionRecibidaDto a = aceptadas.get(id);
            if (a != null) {
                guardadas.put(id, new GuardadaDto(id, a.huella(), a.resultado(), a.codigos()));
            }
        }
        return guardadas;
    }

    @Override
    public void guardarAceptada(AnotacionRecibidaDto anotacion) {
        if (aceptadas.putIfAbsent(anotacion.anotacionUuid(), anotacion) != null) {
            throw new ChoqueConcurrente("La anotación " + anotacion.anotacionUuid() + " ya estaba");
        }
    }

    @Override
    public void guardarRechazada(AnotacionRechazadaDto anotacion) {
        rechazadas.putIfAbsent(anotacion.anotacionUuid() + "/" + anotacion.huella(), anotacion);
    }

    /** Cuántas aceptadas hay, para que la prueba compruebe el efecto y no solo el conteo. */
    public int cuantas() {
        return aceptadas.size();
    }

    /** Cuántas rechazadas distintas hay. */
    public int rechazadas() {
        return rechazadas.size();
    }
}
