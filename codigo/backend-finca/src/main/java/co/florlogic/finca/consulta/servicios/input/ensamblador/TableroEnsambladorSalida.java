package co.florlogic.finca.consulta.servicios.input.ensamblador;

import co.florlogic.finca.consulta.dominio.Avance;
import co.florlogic.finca.consulta.dominio.Tablero;
import co.florlogic.finca.consulta.servicios.input.dto.respuesta.TableroRespuestaDto;

/** Ensamblador de salida del tablero: dominio → DTO de respuesta (A-84). Sin estado. */
public final class TableroEnsambladorSalida {

    private TableroEnsambladorSalida() {
    }

    public static TableroRespuestaDto aDto(Tablero t) {
        return new TableroRespuestaDto(t.jornada(), fila(t.total()),
                t.porCelular().stream().map(c -> new TableroRespuestaDto.FilaDeCelular(c.dispositivo(), fila(c.avance()),
                        c.marcadas(), c.rechazadas(), c.parciales(), c.pideAtencion())).toList(),
                t.porSeccion().stream().map(s -> new TableroRespuestaDto.FilaDeSeccion(s.seccion(), fila(s.avance()),
                        s.marcadas())).toList());
    }

    private static TableroRespuestaDto.Fila fila(Avance a) {
        return new TableroRespuestaDto.Fila(a.esperado(), a.recibido(), a.falta(), a.porcentaje(), a.completo());
    }
}
