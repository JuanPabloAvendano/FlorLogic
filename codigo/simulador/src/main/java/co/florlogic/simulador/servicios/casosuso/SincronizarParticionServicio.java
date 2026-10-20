package co.florlogic.simulador.servicios.casosuso;

import org.springframework.stereotype.Service;

import co.florlogic.contrato.dto.ResultadoParticion;
import co.florlogic.contrato.lectura.Lectura;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticion;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticionComando;
import co.florlogic.simulador.servicios.puertos.salida.NodoDeFinca;

/**
 * Camino mínimo (D-17): entrega la partición tal como llega. Aquí vivirá el almacén de salida (ADR-002): armar
 * la partición con lo pendiente y no borrar nada hasta tener acuse. Hoy solo delega, y por eso no tiene más
 * lógica que probar que delega.
 */
@Service
class SincronizarParticionServicio implements SincronizarParticion {

    private final NodoDeFinca nodo;

    SincronizarParticionServicio(NodoDeFinca nodo) {
        this.nodo = nodo;
    }

    @Override
    public Lectura<ResultadoParticion> ejecutar(SincronizarParticionComando comando) {
        return nodo.enviar(comando.nodo(), comando.sesion(), comando.particionJson());
    }
}
