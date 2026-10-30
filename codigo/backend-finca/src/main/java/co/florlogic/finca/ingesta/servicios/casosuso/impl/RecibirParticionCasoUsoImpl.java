package co.florlogic.finca.ingesta.servicios.casosuso.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.servicios.casosuso.RecibirParticionCasoUso;
import co.florlogic.finca.ingesta.servicios.output.assembler.AnotacionRecibidaOutputAssembler;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AnotacionRepositorio;

/**
 * Camino mínimo (D-17): guarda cada anotación una vez y cuenta. Todo en una transacción: si la base falla
 * a mitad, no queda la partición a medias y el celular puede reenviarla entera sin duplicar nada.
 *
 * <p>Solo conoce interfaces y el ensamblador de salida: el dominio se vuelve DTO de salida antes de cruzar el puerto
 * (Martin · Clean Architecture p. 195).
 */
@Service
public class RecibirParticionCasoUsoImpl implements RecibirParticionCasoUso {

    private final AnotacionRepositorio anotaciones;

    public RecibirParticionCasoUsoImpl(AnotacionRepositorio anotaciones) {
        this.anotaciones = anotaciones;
    }

    @Override
    @Transactional
    public RecepcionDeParticionValor ejecutar(UUID sesionId, List<AnotacionRecibida> recibidas) {
        int aplicadas = 0;
        int sinEfecto = 0;
        long mayor = 0;
        for (AnotacionRecibida anotacion : recibidas) {
            if (anotaciones.guardarSiNueva(AnotacionRecibidaOutputAssembler.aSalida(anotacion))) {
                aplicadas++;
            } else {
                sinEfecto++;
            }
            mayor = Math.max(mayor, anotacion.consecutivo());
        }
        return new RecepcionDeParticionValor(recibidas.size(), aplicadas, sinEfecto, mayor);
    }
}
