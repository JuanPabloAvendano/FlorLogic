package co.florlogic.simulador.servicios.casosuso;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.contrato.lectura.LecturaResultado;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticionComando;
import co.florlogic.simulador.servicios.puertos.salida.NodoDeFincaPuertoSalida;

/**
 * El caso de uso del celular con un nodo de finca en memoria (Fake Object): sin red ni Spring. Hoy el servicio
 * solo delega (camino mínimo); esta prueba fija que entrega exactamente lo que recibió y devuelve lo del nodo, y
 * es donde crecerán las pruebas del almacén de salida (ADR-002).
 */
class SincronizarParticionCasoUsoTest {

    private static final URI NODO = URI.create("http://finca:18080");
    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");

    /** Guarda lo que le entregaron y responde lo que se le diga. */
    private static final class NodoEnMemoria implements NodoDeFincaPuertoSalida {
        final List<String> entregas = new ArrayList<>();
        LecturaResultado<ResultadoParticionDto> respuesta = new LecturaResultado.Fallida<>("", "rechazada");

        @Override
        public LecturaResultado<ResultadoParticionDto> enviar(URI nodo, UUID sesion, byte[] particionJson) {
            entregas.add(nodo + " " + sesion + " " + new String(particionJson));
            return respuesta;
        }
    }

    private final NodoEnMemoria nodo = new NodoEnMemoria();
    private final SincronizarParticionCasoUso servicio = new SincronizarParticionCasoUso(nodo);

    @Test
    void entrega_al_nodo_la_particion_tal_cual_una_sola_vez() {
        servicio.ejecutar(new SincronizarParticionComando(NODO, SESION, "{\"contrato\":\"1.0\"}".getBytes()));

        assertThat(nodo.entregas).containsExactly(NODO + " " + SESION + " {\"contrato\":\"1.0\"}");
    }

    @Test
    void devuelve_lo_que_respondio_el_nodo_sin_cambiarlo() {
        nodo.respuesta = new LecturaResultado.Fallida<>("", "sin_conexion");

        assertThat(servicio.ejecutar(new SincronizarParticionComando(NODO, SESION, new byte[0])))
                .isEqualTo(new LecturaResultado.Fallida<>("", "sin_conexion"));
    }
}
