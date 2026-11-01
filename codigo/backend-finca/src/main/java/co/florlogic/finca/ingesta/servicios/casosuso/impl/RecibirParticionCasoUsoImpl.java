package co.florlogic.finca.ingesta.servicios.casosuso.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.ingesta.dominio.avance.AvanceDelDispositivoAgregado;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;
import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.servicios.casosuso.RecibirParticionCasoUso;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.AnotacionRecibidaEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ensamblador.AvanceEnsambladorSalida;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AnotacionRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AvanceRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.DispositivoDelNodo;

/**
 * Guarda cada anotación una vez, mueve el avance del celular y cuenta (Segmento 1b; ESPEC-dominio-ingesta §5 «Flujo
 * del caso de uso», pasos 2, 4 y 5). Todo en una transacción: si la base falla a mitad o otra partición del mismo
 * celular ganó primero ({@code ChoqueConcurrente}, A-98), no queda nada a medias y el celular puede reenviar la
 * partición entera.
 *
 * <p>Solo conoce interfaces y los ensambladores de salida: el dominio se vuelve DTO antes de cruzar un puerto
 * (Martin · Clean Architecture p. 195). El juicio de cada anotación (huella, reglas) llega en el Segmento 2.
 */
@Service
public class RecibirParticionCasoUsoImpl implements RecibirParticionCasoUso {

    private final AnotacionRepositorio anotaciones;
    private final AvanceRepositorio avances;
    private final DispositivoDelNodo nodo;

    public RecibirParticionCasoUsoImpl(AnotacionRepositorio anotaciones, AvanceRepositorio avances,
            DispositivoDelNodo nodo) {
        this.anotaciones = anotaciones;
        this.avances = avances;
        this.nodo = nodo;
    }

    @Override
    @Transactional
    public RecepcionDeParticionValor ejecutar(UUID sesionId, List<AnotacionRecibida> recibidas) {
        AvanceDelDispositivoAgregado avance = avanceDelCelular();
        int aplicadas = 0;
        int sinEfecto = 0;
        for (AnotacionRecibida anotacion : recibidas) {
            if (anotaciones.guardarSiNueva(AnotacionRecibidaEnsambladorSalida.aSalida(anotacion))) {
                aplicadas++;
            } else {
                sinEfecto++;
            }
            // Toda llegada mueve el avance, también un reenvío (A-73); registrar es idempotente. El consecutivo ya es
            // válido: el dominio rechazó antes lo que no lo era (A-108).
            avance.registrar(anotacion.consecutivo());
        }
        avances.guardar(AvanceEnsambladorSalida.aDto(avance));
        long confirmadoHasta = avance.confirmadoHasta().map(ConsecutivoValor::valor).orElse(0L);
        return new RecepcionDeParticionValor(recibidas.size(), aplicadas, sinEfecto, confirmadoHasta);
    }

    /**
     * El avance guardado del celular, o uno nuevo si nunca se recibió nada. Si lo guardado no se puede rehacer, no se
     * crea otro encima: la partición se deshace y sale el {@code 500} genérico, con el dispositivo en el log (COH-131).
     */
    private AvanceDelDispositivoAgregado avanceDelCelular() {
        UUID dispositivo = nodo.dispositivoId();
        return avances.deDispositivo(dispositivo)
                .map(guardado -> AvanceEnsambladorSalida.aDominio(guardado).orElseThrow(() -> new IllegalStateException(
                        "El avance guardado del dispositivo " + dispositivo + " no se puede reconstruir")))
                .orElseGet(() -> AvanceDelDispositivoAgregado.nuevo(new DispositivoId(dispositivo)));
    }
}
