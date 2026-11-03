package co.florlogic.finca.consulta.servicios.casosuso;

import java.time.LocalDate;

import co.florlogic.finca.consulta.dominio.Tablero;

/** Caso de uso del tablero del gerente (CT-13). */
public interface ConsultarTableroCasoUso {

    Tablero de(LocalDate jornada);

    LocalDate hoy();
}
