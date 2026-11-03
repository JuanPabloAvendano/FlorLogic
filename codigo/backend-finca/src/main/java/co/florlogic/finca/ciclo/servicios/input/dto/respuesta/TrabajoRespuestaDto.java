package co.florlogic.finca.ciclo.servicios.input.dto.respuesta;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Un trabajo del gerente como se ve en la web: qué anotación, de qué celular y por qué quedó marcada. */
public record TrabajoRespuestaDto(UUID anotacion, UUID dispositivo, String seccion, LocalDate jornada,
        List<String> codigos, Instant abiertoEn) {

    public TrabajoRespuestaDto {
        codigos = List.copyOf(codigos);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera constructor, métodos de lectura (anotacion(), codigos()…), equals,
 *   hashCode y toString. La plantilla de la web lee esos métodos.
 * - public TrabajoRespuestaDto { codigos = List.copyOf(codigos); }: constructor compacto; reemplaza la lista recibida
 *   por una copia inmutable, así nadie puede cambiar los códigos después de creado el DTO. List.copyOf lanza
 *   NullPointerException si la lista es null o trae un null.
 */
