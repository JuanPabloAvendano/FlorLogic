package co.florlogic.finca.consulta.servicios.casosuso.impl;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.consulta.dominio.Avance;
import co.florlogic.finca.consulta.dominio.Tablero;
import co.florlogic.finca.consulta.servicios.casosuso.ConsultarTableroCasoUso;
import co.florlogic.finca.consulta.servicios.output.ports.repositorios.TableroRepositorio;

/** Arma el tablero de la jornada con los conteos de la base (CT-13); las dos vistas salen de la misma foto. */
@Service
public class ConsultarTableroCasoUsoImpl implements ConsultarTableroCasoUso {

    private final TableroRepositorio conteos;
    private final Clock reloj;
    private final ZoneId zona;

    public ConsultarTableroCasoUsoImpl(TableroRepositorio conteos, Clock reloj, ZoneId zonaDeLaFinca) {
        this.conteos = conteos;
        this.reloj = reloj;
        this.zona = zonaDeLaFinca;
    }

    @Override
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public Tablero de(LocalDate jornada) {
        return new Tablero(jornada,
                conteos.porCelular(jornada, zona).stream().map(c -> new Tablero.PorCelular(c.dispositivo(),
                        new Avance(c.esperado(), c.recibido()), c.marcadas(), c.rechazadas(), c.parciales())).toList(),
                conteos.porSeccion(jornada).stream().map(s -> new Tablero.PorSeccion(s.seccion(),
                        new Avance(s.esperado(), s.recibido()), s.marcadas())).toList());
    }

    @Override
    public LocalDate hoy() {
        return LocalDate.now(reloj.withZone(zona));
    }
}
