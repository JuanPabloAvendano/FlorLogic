package co.florlogic.finca.ingesta;

import java.util.List;
import java.util.UUID;

/**
 * Evento público de ingesta (CT-12): una anotación quedó aceptada (aplicada o marcada). Se publica dentro de la
 * transacción de la partición; Spring Modulith lo guarda en su registro y lo entrega después del commit, así nadie
 * escucha una anotación que al final se deshizo y ninguna se pierde si el nodo cae.
 *
 * @param codigos de la marca, en minúscula; vacío si es aplicada
 * @param desfaseMs el desfase de la sesión, para corregir la hora del celular (ADR-031)
 */
public record AnotacionAceptada(UUID anotacionUuid, UUID dispositivoId, String tipo, int versionTipo,
        String contenidoJson, List<String> codigos, long desfaseMs) {

    public AnotacionAceptada {
        codigos = List.copyOf(codigos);
    }
}
