package co.florlogic.finca.ciclo.servicios.casosuso.impl;

import java.time.Clock;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.ciclo.dominio.HechoDeProduccion;
import co.florlogic.finca.ciclo.servicios.casosuso.RegistrarHechoCasoUso;
import co.florlogic.finca.ciclo.servicios.output.dto.HechoDto;
import co.florlogic.finca.ciclo.servicios.output.dto.TrabajoDto;
import co.florlogic.finca.ciclo.servicios.output.ports.repositorios.HechoRepositorio;
import co.florlogic.finca.ciclo.servicios.output.ports.repositorios.TrabajoRepositorio;

/**
 * Lleva una anotación aceptada al ciclo (CT-12): el hecho vigente cambia solo si la nueva captura es más reciente
 * (RF-022); si vino marcada, el gerente tiene un trabajo nuevo (P-S01). Idempotente: el mismo evento dos veces no
 * cambia nada.
 */
@Service
public class RegistrarHechoCasoUsoImpl implements RegistrarHechoCasoUso {

    private final HechoRepositorio hechos;
    private final TrabajoRepositorio trabajos;
    private final Clock reloj;

    public RegistrarHechoCasoUsoImpl(HechoRepositorio hechos, TrabajoRepositorio trabajos, Clock reloj) {
        this.hechos = hechos;
        this.trabajos = trabajos;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public void ejecutar(HechoDeProduccion hecho, List<String> codigos) {
        boolean gana = hechos.vigenteConTurno(hecho.produccionId(), hecho.seccionId(), hecho.campoId(), hecho.jornada())
                .map(v -> hecho.reemplazaA(new HechoDeProduccion(v.produccionId(), v.seccionId(), v.campoId(), v.jornada(),
                        v.valor(), v.motivoAusencia(), v.capturadoEn(), v.anotacion(), v.dispositivo())))
                .orElse(true);
        if (gana) {
            hechos.guardar(new HechoDto(hecho.produccionId(), hecho.seccionId(), hecho.campoId(), hecho.jornada(),
                    hecho.valor(), hecho.motivoAusencia(), hecho.capturadoEn(), hecho.anotacion(), hecho.dispositivo()));
        }
        if (!codigos.isEmpty()) {
            trabajos.abrir(new TrabajoDto(hecho.anotacion(), hecho.dispositivo(), hecho.seccionId(), hecho.jornada(),
                    codigos, reloj.instant()));
        }
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): Spring crea una instancia al arrancar y la entrega a quien pida RegistrarHechoCasoUso; los
 *   repositorios y el reloj le llegan por el constructor.
 * - @Transactional (Spring): todo ejecutar(…) corre en una sola transacción de base de datos; leer el vigente con su
 *   bloqueo, guardar el hecho y abrir el trabajo se confirman juntos o se deshacen juntos si algo lanza.
 * - Clock reloj y reloj.instant(): la hora llega como dependencia (un solo reloj del nodo, en UTC) en vez de llamar a
 *   Instant.now(); así las pruebas pueden fijarla. Es la hora en que se abre el trabajo del gerente.
 * - vigenteConTurno(…).map(v → …).orElse(true): sobre el Optional que devuelve el repositorio. Si hay vigente, map lo
 *   convierte en dominio y pregunta si el nuevo lo reemplaza; si no hay, orElse(true): el primero siempre gana.
 */
