package co.florlogic.simulador.servicios.casosuso;

import org.springframework.stereotype.Service;

import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.contrato.dto.SesionAbiertaDto;
import co.florlogic.contrato.lectura.LecturaResultado;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticionPuertoEntrada;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticionComando;
import co.florlogic.simulador.servicios.puertos.salida.NodoDeFincaPuertoSalida;

/**
 * Lo que hace el celular al sincronizar (CT-1; recorrido de TX-1, pasos 3, 7 y 15): abre la sesión, entrega la
 * partición en ella y la cierra: completa si la partición entró, parcial si no. Aquí vivirá el almacén de salida
 * (ADR-002): armar la partición con lo pendiente y no borrar nada hasta tener acuse.
 */
@Service
class SincronizarParticionCasoUso implements SincronizarParticionPuertoEntrada {

    /** La versión de configuración con que captura el simulador: la de la partición dorada del contrato. */
    static final int VERSION_CONFIG = 12;

    private final NodoDeFincaPuertoSalida nodo;

    SincronizarParticionCasoUso(NodoDeFincaPuertoSalida nodo) {
        this.nodo = nodo;
    }

    @Override
    public LecturaResultado<ResultadoParticionDto> ejecutar(SincronizarParticionComando comando) {
        return switch (nodo.abrir(comando.nodo(), comando.dispositivo(), System.currentTimeMillis(), VERSION_CONFIG)) {
            case LecturaResultado.Fallida<SesionAbiertaDto> fallo -> new LecturaResultado.Fallida<>(fallo.pointer(), fallo.clase());
            case LecturaResultado.Leida<SesionAbiertaDto> abierta -> {
                LecturaResultado<ResultadoParticionDto> entrega =
                        nodo.enviar(comando.nodo(), abierta.valor().sesionUuid(), comando.particionJson());
                nodo.cerrar(comando.nodo(), abierta.valor().sesionUuid(), entrega instanceof LecturaResultado.Leida);
                yield entrega;
            }
        };
    }
}
