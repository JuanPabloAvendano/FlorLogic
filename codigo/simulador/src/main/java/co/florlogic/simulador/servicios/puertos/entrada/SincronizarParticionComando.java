package co.florlogic.simulador.servicios.puertos.entrada;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

/**
 * Lo que necesita el caso de uso: a qué nodo de finca, en qué sesión de sincronización y qué partición.
 *
 * @param nodo la dirección del nodo de finca; llega por llamada porque la da {@code --url} (decisión VS2-1 de la
 *        verificación del Segmento 2)
 * @param particionJson la partición tal como se enviará; el comando guarda su propia copia
 */
public record SincronizarParticionComando(URI nodo, UUID sesion, byte[] particionJson) {

    public SincronizarParticionComando {
        // Solo atrapan errores de programación: la orden ya validó sus opciones (investigacion/03 regla 8).
        Objects.requireNonNull(nodo, "nodo");
        Objects.requireNonNull(sesion, "sesion");
        particionJson = Objects.requireNonNull(particionJson, "particionJson").clone();
    }

    /** Copia: un arreglo es mutable y el record no debe dejar que se cambie por fuera. */
    @Override
    public byte[] particionJson() {
        return particionJson.clone();
    }
}
