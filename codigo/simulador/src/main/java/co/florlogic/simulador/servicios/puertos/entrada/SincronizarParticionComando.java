package co.florlogic.simulador.servicios.puertos.entrada;

import java.net.URI;
import java.util.Objects;
import java.util.UUID;

/**
 * Lo que necesita el caso de uso: a qué nodo de finca, desde qué celular y qué partición (la sesión la abre el caso de
 * uso, CT-1).
 *
 * @param nodo la dirección del nodo de finca; llega por llamada porque la da {@code --url} (decisión VS2-1 de la
 *        verificación del Segmento 2)
 * @param particionJson la partición tal como se enviará; el comando guarda su propia copia
 */
public record SincronizarParticionComando(URI nodo, UUID dispositivo, byte[] particionJson) {

    public SincronizarParticionComando {
        // Solo atrapan errores de programación: la orden ya validó sus opciones (investigacion/03 regla 8).
        Objects.requireNonNull(nodo, "nodo");
        Objects.requireNonNull(dispositivo, "dispositivo");
        particionJson = Objects.requireNonNull(particionJson, "particionJson").clone();
    }

    /** Copia: un arreglo es mutable y el record no debe dejar que se cambie por fuera. */
    @Override
    public byte[] particionJson() {
        return particionJson.clone();
    }
}
