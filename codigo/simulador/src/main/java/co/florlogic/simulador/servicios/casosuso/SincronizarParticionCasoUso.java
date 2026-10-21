package co.florlogic.simulador.servicios.casosuso;

import org.springframework.stereotype.Service;

import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.contrato.lectura.LecturaResultado;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticionPuertoEntrada;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticionComando;
import co.florlogic.simulador.servicios.puertos.salida.NodoDeFincaPuertoSalida;

/**
 * Camino mínimo (D-17): entrega la partición tal como llega. Aquí vivirá el almacén de salida (ADR-002): armar
 * la partición con lo pendiente y no borrar nada hasta tener acuse. Hoy solo delega, y por eso no tiene más
 * lógica que probar que delega.
 */
@Service
class SincronizarParticionCasoUso implements SincronizarParticionPuertoEntrada {

    private final NodoDeFincaPuertoSalida nodo;

    SincronizarParticionCasoUso(NodoDeFincaPuertoSalida nodo) {
        this.nodo = nodo;
    }

    @Override
    public LecturaResultado<ResultadoParticionDto> ejecutar(SincronizarParticionComando comando) {
        return nodo.enviar(comando.nodo(), comando.sesion(), comando.particionJson());
    }
}
