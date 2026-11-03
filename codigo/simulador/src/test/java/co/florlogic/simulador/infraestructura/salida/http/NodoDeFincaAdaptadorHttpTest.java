package co.florlogic.simulador.infraestructura.salida.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.contrato.lectura.LecturaResultado;

/**
 * El adaptador HTTP del celular contra un nodo de finca falso (servidor HTTP del JDK): integración estrecha, red
 * real sin backend. Nunca lanza.
 */
class NodoDeFincaAdaptadorHttpTest {

    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");
    private HttpServer servidor;
    private final AtomicReference<String> rutaPedida = new AtomicReference<>();

    @AfterEach
    void apagar() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    @Test
    void un_200_se_lee_con_el_lector_tolerante() throws IOException {
        String respuesta = dorada().replace("\"particion_uuid\"", "\"campo_de_una_version_nueva\": 1, \"particion_uuid\"");
        URI base = nodoQueResponde(200, respuesta);

        LecturaResultado<ResultadoParticionDto> r = new NodoDeFincaAdaptadorHttp(HttpClient.newHttpClient()).enviar(base, SESION, "{}".getBytes());

        assertThat(r).isInstanceOfSatisfying(LecturaResultado.Leida.class,
                l -> assertThat(((ResultadoParticionDto) l.valor()).conteo().aplicadas()).isEqualTo(2));
        assertThat(rutaPedida.get()).isEqualTo("/ingesta/v1/sesiones/" + SESION + "/particiones");
    }

    @Test
    void un_422_es_rechazada() throws IOException {
        URI base = nodoQueResponde(422, "{\"type\":\"https://florlogic.co/problemas/ingesta/rechazado\"}");

        assertThat(new NodoDeFincaAdaptadorHttp(HttpClient.newHttpClient()).enviar(base, SESION, "{}".getBytes()))
                .isEqualTo(new LecturaResultado.Fallida<>("", "rechazada"));
    }

    @Test
    void otro_codigo_o_un_200_ilegible_no_lanzan() throws IOException {
        URI caido = nodoQueResponde(503, "");
        assertThat(new NodoDeFincaAdaptadorHttp(HttpClient.newHttpClient()).enviar(caido, SESION, "{}".getBytes()))
                .isEqualTo(new LecturaResultado.Fallida<>("", "http_503"));
        servidor.stop(0);

        URI raro = nodoQueResponde(200, "esto no es json");
        assertThat(new NodoDeFincaAdaptadorHttp(HttpClient.newHttpClient()).enviar(raro, SESION, "{}".getBytes()))
                .isInstanceOf(LecturaResultado.Fallida.class);
    }

    @Test
    void sin_nodo_es_sin_conexion() throws IOException {
        int puertoLibre;
        try (ServerSocket s = new ServerSocket(0)) {
            puertoLibre = s.getLocalPort();
        }
        URI nadie = URI.create("http://localhost:" + puertoLibre);

        assertThat(new NodoDeFincaAdaptadorHttp(HttpClient.newHttpClient()).enviar(nadie, SESION, "{}".getBytes()))
                .isEqualTo(new LecturaResultado.Fallida<>("", "sin_conexion"));
    }

    private URI nodoQueResponde(int estado, String cuerpo) throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext("/", intercambio -> {
            rutaPedida.set(intercambio.getRequestURI().getPath());
            intercambio.getRequestBody().readAllBytes();
            byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
            intercambio.sendResponseHeaders(estado, bytes.length == 0 ? -1 : bytes.length);
            if (bytes.length > 0) {
                intercambio.getResponseBody().write(bytes);
            }
            intercambio.close();
        });
        servidor.start();
        return URI.create("http://localhost:" + servidor.getAddress().getPort());
    }

    private static String dorada() throws IOException {
        try (var in = NodoDeFincaAdaptadorHttpTest.class.getResourceAsStream("/canal/respuesta-particion.json")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - HttpServer.create(new InetSocketAddress("localhost", 0), 0) (JDK, com.sun.net.httpserver): un nodo de finca
 *   falso en un puerto libre; createContext("/", …) guarda la ruta pedida y responde el estado y el cuerpo dados.
 * - sendResponseHeaders(estado, -1): -1 es «respuesta sin cuerpo».
 * - new ServerSocket(0) + getLocalPort(): pide al sistema un puerto libre y lo suelta; así se arma una dirección
 *   donde nadie escucha (caso sin conexión).
 * - AtomicReference<String>: guarda la ruta que vio el servidor, que corre en otro hilo.
 * - getResourceAsStream("/canal/respuesta-particion.json"): la respuesta dorada de src/test/resources.
 * - @AfterEach: apaga el servidor tras cada caso; @Test: cada caso.
 * - isInstanceOfSatisfying(LecturaResultado.Leida.class, l -> …) (AssertJ): comprueba la variante y revisa su
 *   valor; el cast (ResultadoParticionDto) hace falta porque la clase se pasa sin su tipo genérico.
 */
