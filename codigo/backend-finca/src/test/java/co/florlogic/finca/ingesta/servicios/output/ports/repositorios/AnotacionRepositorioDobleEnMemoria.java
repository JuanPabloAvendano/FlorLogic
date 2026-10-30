package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDTO;

/**
 * Doble en memoria del puerto (Fake Object: «implements the same or a subset of the functionality of the real
 * depended-on component but in a much simpler way», Meszaros, citado en Applying Domain-Driven Design and
 * Patterns · p. 187; plantilla caso-de-uso T8). {@code putIfAbsent} hace lo mismo que el
 * {@code ON CONFLICT DO NOTHING} del adaptador JDBC; lo garantiza {@link AnotacionRepositorioContratoTest}.
 */
public final class AnotacionRepositorioDobleEnMemoria implements AnotacionRepositorio {

    private final Map<UUID, AnotacionRecibidaDTO> guardadas = new ConcurrentHashMap<>();

    @Override
    public boolean guardarSiNueva(AnotacionRecibidaDTO anotacion) {
        return guardadas.putIfAbsent(anotacion.anotacionUuid(), anotacion) == null;
    }

    /** Cuántas hay guardadas, para que la prueba compruebe el efecto y no solo el conteo que devuelve el servicio. */
    public int cuantas() {
        return guardadas.size();
    }
}
