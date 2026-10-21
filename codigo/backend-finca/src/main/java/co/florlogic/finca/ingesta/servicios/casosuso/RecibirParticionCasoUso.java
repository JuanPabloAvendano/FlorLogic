package co.florlogic.finca.ingesta.servicios.casosuso;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.servicios.puertos.entrada.ParticionRecibidaResultado;
import co.florlogic.finca.ingesta.servicios.puertos.entrada.RecibirParticionPuertoEntrada;
import co.florlogic.finca.ingesta.servicios.puertos.entrada.RecibirParticionComando;
import co.florlogic.finca.ingesta.servicios.puertos.salida.AnotacionesRecibidasPuertoSalida;

/**
 * Camino mínimo (D-17): guarda cada anotación una vez y cuenta. Todo en una transacción: si la base falla
 * a mitad, no queda la partición a medias y el celular puede reenviarla entera sin duplicar nada.
 */
@Service
class RecibirParticionCasoUso implements RecibirParticionPuertoEntrada {

    private final AnotacionesRecibidasPuertoSalida anotaciones;

    RecibirParticionCasoUso(AnotacionesRecibidasPuertoSalida anotaciones) {
        this.anotaciones = anotaciones;
    }

    @Override
    @Transactional
    public ParticionRecibidaResultado ejecutar(RecibirParticionComando comando) {
        int aplicadas = 0;
        int sinEfecto = 0;
        long mayor = 0;
        for (AnotacionRecibida anotacion : comando.anotaciones()) {
            if (anotaciones.guardarSiNueva(anotacion)) {
                aplicadas++;
            } else {
                sinEfecto++;
            }
            mayor = Math.max(mayor, anotacion.consecutivo());
        }
        return new ParticionRecibidaResultado(comando.anotaciones().size(), aplicadas, sinEfecto, mayor);
    }
}
