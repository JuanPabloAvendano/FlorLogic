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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - ConcurrentHashMap: mapa seguro entre hilos.
 * - aceptadas.putIfAbsent(id, anotacion) != null: si ya había algo con ese id, putIfAbsent no lo cambia y devuelve
 *   lo anterior; eso imita la llave repetida de la base y se lanza ChoqueConcurrente.
 * - rechazadas con llave id + "/" + huella: imita el ON CONFLICT (anotacion_uuid, huella) DO NOTHING del adaptador.
 */
