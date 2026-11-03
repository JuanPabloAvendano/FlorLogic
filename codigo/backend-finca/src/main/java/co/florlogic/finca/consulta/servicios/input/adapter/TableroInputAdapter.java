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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): registra la clase como bean; Spring la crea una sola vez y le pasa por el constructor lo que pide (inyección por constructor, sin @Autowired). Es el bean que recibe TableroControlador cuando pide TableroInputPort.
 * - @Override: le pide al compilador comprobar que el método sí implementa uno de la interfaz; si la firma cambia, no compila.
 */
