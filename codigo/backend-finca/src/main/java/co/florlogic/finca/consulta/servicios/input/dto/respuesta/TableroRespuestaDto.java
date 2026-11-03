package co.florlogic.finca.consulta.servicios.input.dto.respuesta;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** El tablero de la jornada como lo pinta la web (CT-13). */
public record TableroRespuestaDto(LocalDate jornada, Fila total, List<FilaDeCelular> porCelular,
        List<FilaDeSeccion> porSeccion) {

    /** Esperado, recibido, falta y porcentaje: lo que se repite en cada fila. */
    public record Fila(int esperado, int recibido, int falta, int porcentaje, boolean completo) {
    }

    public record FilaDeCelular(UUID dispositivo, Fila avance, int marcadas, int rechazadas, int parciales,
            boolean pideAtencion) {
    }

    public record FilaDeSeccion(String seccion, Fila avance, int marcadas) {
    }

    public TableroRespuestaDto {
        porCelular = List.copyOf(porCelular);
        porSeccion = List.copyOf(porSeccion);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, los accesores (sin «get»: jornada(), total()…), equals, hashCode y toString a partir de la lista de componentes.
 * - Fila, FilaDeCelular y FilaDeSeccion: records anidados; se nombran TableroRespuestaDto.Fila y así.
 * - public TableroRespuestaDto { ... }: constructor compacto; List.copyOf guarda copias inmutables de las listas.
 */
