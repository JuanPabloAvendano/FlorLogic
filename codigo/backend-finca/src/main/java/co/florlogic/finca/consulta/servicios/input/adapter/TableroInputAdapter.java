package co.florlogic.finca.consulta.servicios.input.adapter;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import co.florlogic.finca.consulta.servicios.casosuso.ConsultarTableroCasoUso;
import co.florlogic.finca.consulta.servicios.input.dto.respuesta.TableroRespuestaDto;
import co.florlogic.finca.consulta.servicios.input.ensamblador.TableroEnsambladorSalida;
import co.florlogic.finca.consulta.servicios.input.ports.TableroInputPort;

/** Adaptador de entrada del tablero: caso de uso → ensamblador (A-84). */
@Service
public class TableroInputAdapter implements TableroInputPort {

    private final ConsultarTableroCasoUso casoUso;

    public TableroInputAdapter(ConsultarTableroCasoUso casoUso) {
        this.casoUso = casoUso;
    }

    @Override
    public TableroRespuestaDto de(LocalDate jornada) {
        return TableroEnsambladorSalida.aDto(casoUso.de(jornada));
    }

    @Override
    public LocalDate hoy() {
        return casoUso.hoy();
    }
}
