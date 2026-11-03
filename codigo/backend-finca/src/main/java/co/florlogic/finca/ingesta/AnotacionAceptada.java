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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, los accesores (anotacionUuid(), codigos()…),
 *   equals, hashCode y toString. Aquí es el evento que ingesta publica y que escuchan otros módulos.
 * - public AnotacionAceptada { … } (constructor compacto): corre antes de guardar los campos y puede reasignar los
 *   parámetros; aquí cambia codigos por su copia.
 * - List.copyOf(codigos): copia no modificable; quien publicó el evento no puede cambiar la lista después, y un
 *   null dentro de la lista lanza NullPointerException.
 */
