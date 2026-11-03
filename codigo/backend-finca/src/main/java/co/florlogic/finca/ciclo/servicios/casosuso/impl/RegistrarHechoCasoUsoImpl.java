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
