package co.florlogic.finca.dispositivos.servicios.input.dto.respuesta;

import java.time.Instant;
import java.util.UUID;

/** Un celular como lo ve el administrador en la web (CT-10). */
public record DispositivoRespuestaDto(UUID id, String estado, Instant vistoPorPrimeraVez, Instant cambiadoEn,
        String cambiadoPor) {

    public boolean pendiente() {
        return "PENDIENTE".equals(estado);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera constructor, métodos de lectura (id(), estado()…), equals, hashCode
 *   y toString.
 * - public boolean pendiente(): método extra del record. Thymeleaf lo usa en la plantilla como ${d.pendiente} para
 *   resaltar la fila del celular que espera decisión.
 */
