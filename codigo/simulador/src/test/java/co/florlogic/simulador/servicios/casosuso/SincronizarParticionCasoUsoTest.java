package co.florlogic.simulador.servicios.casosuso;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.contrato.dto.SesionAbiertaDto;
import co.florlogic.contrato.dto.SesionCerradaDto;
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
    private static final UUID CELULAR = UUID.fromString("0192f0a0-0000-7000-8000-0000000000e1");
    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");

    /** Guarda lo que le pidieron y responde lo que se le diga. */
    private static final class NodoEnMemoria implements NodoDeFincaPuertoSalida {
        final List<String> pedidos = new ArrayList<>();
        LecturaResultado<SesionAbiertaDto> apertura = new LecturaResultado.Leida<>(new SesionAbiertaDto(SESION, 0, 0, 12, false));
        LecturaResultado<ResultadoParticionDto> respuesta = new LecturaResultado.Fallida<>("", "rechazada");

        @Override
        public LecturaResultado<SesionAbiertaDto> abrir(URI nodo, UUID dispositivo, long relojMs, int versionConfig) {
            pedidos.add("abrir " + dispositivo);
            return apertura;
        }

        @Override
        public LecturaResultado<SesionCerradaDto> cerrar(URI nodo, UUID sesion, boolean completa) {
            pedidos.add("cerrar " + sesion + " " + completa);
            return new LecturaResultado.Fallida<>("", "sin_importancia");
        }

        @Override
        public LecturaResultado<ResultadoParticionDto> enviar(URI nodo, UUID sesion, byte[] particionJson) {
            pedidos.add("enviar " + sesion + " " + new String(particionJson));
            return respuesta;
        }
    }

    private final NodoEnMemoria nodo = new NodoEnMemoria();
    private final SincronizarParticionCasoUso servicio = new SincronizarParticionCasoUso(nodo);

    @Test
    void abre_la_sesion_entrega_la_particion_tal_cual_en_ella_y_la_cierra() {
        servicio.ejecutar(new SincronizarParticionComando(NODO, CELULAR, "{\"contrato\":\"1.0\"}".getBytes()));

        assertThat(nodo.pedidos).containsExactly("abrir " + CELULAR, "enviar " + SESION + " {\"contrato\":\"1.0\"}",
                "cerrar " + SESION + " false");
    }

    @Test
    void devuelve_lo_que_respondio_el_nodo_sin_cambiarlo() {
        nodo.respuesta = new LecturaResultado.Fallida<>("", "sin_conexion");

        assertThat(servicio.ejecutar(new SincronizarParticionComando(NODO, CELULAR, new byte[0])))
                .isEqualTo(new LecturaResultado.Fallida<>("", "sin_conexion"));
    }

    @Test
    void si_no_se_puede_abrir_la_sesion_no_se_entrega_nada() {
        nodo.apertura = new LecturaResultado.Fallida<>("", "http_502");

        assertThat(servicio.ejecutar(new SincronizarParticionComando(NODO, CELULAR, new byte[0])))
                .isEqualTo(new LecturaResultado.Fallida<>("", "http_502"));
        assertThat(nodo.pedidos).containsExactly("abrir " + CELULAR);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - private static final class NodoEnMemoria implements NodoDeFincaPuertoSalida: el puerto de salida escrito a
 *   mano en la prueba (Fake Object); guarda lo que le piden y devuelve lo que se le diga.
 * - @Override: cada método cumple la firma del puerto.
 * - @Test: cada caso de prueba.
 * - assertThat(nodo.pedidos).containsExactly(…) (AssertJ): la lista tiene esos elementos, en ese orden, y nada
 *   más; fija abrir, enviar y cerrar en secuencia.
 * - new byte[0]: una partición vacía; al caso de uso no le importa el contenido.
 */
