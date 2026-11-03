package co.florlogic.finca.dispositivos.servicios.input.dto.respuesta;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Lo que le toca a un celular en una jornada: sus secciones por sus campos (CT-13). */
public record AsignacionRespuestaDto(LocalDate jornada, UUID dispositivo, List<String> secciones, List<String> campos,
        int celdas) {

    public AsignacionRespuestaDto {
        secciones = List.copyOf(secciones);
        campos = List.copyOf(campos);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera constructor, métodos de lectura (jornada(), secciones()…), equals,
 *   hashCode y toString. La plantilla web/asignacion lee esos métodos.
 * - public AsignacionRespuestaDto { … List.copyOf(…) }: constructor compacto; guarda copias inmutables de las listas.
 */
