package co.florlogic.finca.dispositivos.servicios.input.dto.solicitud;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Lo que el administrador pide asignar: un celular, una jornada, sus secciones y sus campos (CT-13). */
public record AsignacionSolicitudDto(LocalDate jornada, UUID dispositivo, List<String> secciones, List<String> campos,
        String administrador) {

    public AsignacionSolicitudDto {
        secciones = secciones == null ? List.of() : List.copyOf(secciones);
        campos = campos == null ? List.of() : List.copyOf(campos);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera constructor, métodos de lectura, equals, hashCode y toString.
 * - public AsignacionSolicitudDto { … }: constructor compacto; si secciones o campos llegan null los deja como lista
 *   vacía, y si no, guarda una copia inmutable con List.copyOf.
 */
