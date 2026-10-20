package co.florlogic.simulador.servicios.casosuso;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.contrato.dto.ResultadoParticion;
import co.florlogic.contrato.lectura.Lectura;
import co.florlogic.simulador.servicios.puertos.entrada.SincronizarParticionComando;
import co.florlogic.simulador.servicios.puertos.salida.NodoDeFinca;

/**
 * El caso de uso del celular con un nodo de finca en memoria (Fake Object): sin red ni Spring. Hoy el servicio
 * solo delega (camino mínimo); esta prueba fija que entrega exactamente lo que recibió y devuelve lo del nodo, y
 * es donde crecerán las pruebas del almacén de salida (ADR-002).
 */
class SincronizarParticionServicioTest {

    private static final URI NODO = URI.create("http://finca:18080");
    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");

    /** Guarda lo que le entregaron y responde lo que se le diga. */
    private static final class NodoEnMemoria implements NodoDeFinca {
        final List<String> entregas = new ArrayList<>();
        Lectura<ResultadoParticion> respuesta = new Lectura.Fallida<>("", "rechazada");

        @Override
        public Lectura<ResultadoParticion> enviar(URI nodo, UUID sesion, byte[] particionJson) {
            entregas.add(nodo + " " + sesion + " " + new String(particionJson));
            return respuesta;
        }
    }

    private final NodoEnMemoria nodo = new NodoEnMemoria();
    private final SincronizarParticionServicio servicio = new SincronizarParticionServicio(nodo);

    @Test
    void entrega_al_nodo_la_particion_tal_cual_una_sola_vez() {
        servicio.ejecutar(new SincronizarParticionComando(NODO, SESION, "{\"contrato\":\"1.0\"}".getBytes()));

        assertThat(nodo.entregas).containsExactly(NODO + " " + SESION + " {\"contrato\":\"1.0\"}");
    }

    @Test
    void devuelve_lo_que_respondio_el_nodo_sin_cambiarlo() {
        nodo.respuesta = new Lectura.Fallida<>("", "sin_conexion");

        assertThat(servicio.ejecutar(new SincronizarParticionComando(NODO, SESION, new byte[0])))
                .isEqualTo(new Lectura.Fallida<>("", "sin_conexion"));
    }
}
