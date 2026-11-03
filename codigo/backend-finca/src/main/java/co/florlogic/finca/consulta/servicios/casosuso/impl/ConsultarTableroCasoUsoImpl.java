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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): registra la clase como bean; Spring la crea una sola vez y le pasa por el constructor lo que pide (inyección por constructor, sin @Autowired). El Clock y el ZoneId zonaDeLaFinca son beans de RelojDelNodo.
 * - @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ): las dos consultas (por celular y por
 *   sección) corren en una sola transacción de lectura repetible; PostgreSQL les muestra la misma foto de la base,
 *   así una sincronización que llegue en medio no descuadra una vista contra la otra (la «misma foto» del Javadoc).
 * - stream().map(c -> new Tablero.PorCelular(...)).toList(): convierte cada conteo en una fila del dominio.
 * - LocalDate.now(reloj.withZone(zona)): la fecha de hoy según el reloj inyectado, pero en la zona de la finca y no
 *   en la del servidor.
 * - @Override: le pide al compilador comprobar que el método sí implementa uno de la interfaz; si la firma cambia, no compila.
 */
