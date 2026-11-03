package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import co.florlogic.finca.ingesta.servicios.output.dto.AvanceDto;

/** Doble en memoria de {@link AvanceRepositorio}: cumple el mismo contrato que el adaptador JDBC. */
public final class AvanceRepositorioDobleEnMemoria implements AvanceRepositorio {

    private final Map<UUID, AvanceDto> guardados = new ConcurrentHashMap<>();

    @Override
    public Optional<AvanceDto> deDispositivo(UUID dispositivoId) {
        return Optional.ofNullable(guardados.get(dispositivoId));
    }

    @Override
    public synchronized void guardar(AvanceDto avance) {
        AvanceDto actual = guardados.get(avance.dispositivoId());
        long versionEnLaBase = actual == null ? 0 : actual.version();
        if (versionEnLaBase != avance.version()) {
            throw new ChoqueConcurrente("El avance del dispositivo " + avance.dispositivoId() + " cambió de versión");
        }
        guardados.put(avance.dispositivoId(), new AvanceDto(avance.dispositivoId(), avance.confirmadoHasta(),
                avance.porEncima(), versionEnLaBase + 1));
    }

    /** Pone un avance guardado tal cual, para probar lo que el caso de uso hace con uno que no se puede rehacer. */
    public void sembrar(AvanceDto avance) {
        guardados.put(avance.dispositivoId(), avance);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - ConcurrentHashMap: mapa seguro entre hilos.
 * - synchronized en guardar: un solo hilo a la vez compara la versión y escribe; imita el UPDATE … WHERE version
 *   del adaptador.
 * - Optional.ofNullable(guardados.get(id)): vacío si nunca se guardó ese celular.
 */
