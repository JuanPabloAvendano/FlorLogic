package co.florlogic.finca.ingesta.servicios.puertos.entrada;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;

/** Lo que el caso de uso necesita, ya traducido del cable: la sesión y sus anotaciones (T4). */
public record RecibirParticionComando(UUID sesionId, List<AnotacionRecibida> anotaciones) {

    public RecibirParticionComando {
        Objects.requireNonNull(sesionId, "sesionId");
        anotaciones = List.copyOf(anotaciones);
    }
}
