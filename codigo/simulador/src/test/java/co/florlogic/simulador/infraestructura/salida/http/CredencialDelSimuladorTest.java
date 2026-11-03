package co.florlogic.simulador.infraestructura.salida.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

import co.florlogic.contrato.lectura.LecturaResultado;

/**
 * El simulador con token (CT-2), contra un nodo falso que hace de gateway: {@code /auth/…/token} da el token y las
 * rutas de ingesta lo reciben en {@code Authorization}. Nunca lanza.
 */
class CredencialDelSimuladorTest {

    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");
    private static final Clock RELOJ = Clock.fixed(Instant.parse("2026-10-31T12:00:00Z"), ZoneOffset.UTC);

    private HttpServer servidor;
    private final List<String> autorizaciones = new CopyOnWriteArrayList<>();
    private final List<String> pedidosDeToken = new CopyOnWriteArrayList<>();
    private final AtomicInteger estadoDelToken = new AtomicInteger(200);

    @AfterEach
    void apagar() {
        if (servidor != null) {
            servidor.stop(0);
        }
    }

    @Test
    void con_secreto_pide_el_token_una_vez_y_lo_manda_en_cada_peticion() throws IOException {
        URI nodo = nodo();
        HttpClient http = HttpClient.newHttpClient();
        NodoDeFincaAdaptadorHttp adaptador = new NodoDeFincaAdaptadorHttp(http,
                new CredencialDelSimulador(http, "s3cr3t&raro", RELOJ));

        adaptador.enviar(nodo, SESION, "{}".getBytes());
        adaptador.cerrar(nodo, SESION, true);

        assertThat(autorizaciones).containsExactly("Bearer token-1", "Bearer token-1");
        assertThat(pedidosDeToken).hasSize(1);
        // Credenciales de cliente, con el secreto escapado: el & no parte el formulario.
        assertThat(pedidosDeToken.get(0)).isEqualTo("grant_type=client_credentials&client_id=simulador&client_secret=s3cr3t%26raro");
    }

    @Test
    void sin_secreto_va_sin_token_y_el_nodo_decide() throws IOException {
        URI nodo = nodo();
        HttpClient http = HttpClient.newHttpClient();

        new NodoDeFincaAdaptadorHttp(http, new CredencialDelSimulador(http, null, RELOJ)).enviar(nodo, SESION, "{}".getBytes());

        assertThat(autorizaciones).containsExactly("(sin)");
        assertThat(pedidosDeToken).isEmpty();
    }

    @Test
    void si_keycloak_no_da_token_no_manda_nada_y_dice_sin_credencial() throws IOException {
        URI nodo = nodo();
        estadoDelToken.set(401);
        HttpClient http = HttpClient.newHttpClient();

        LecturaResultado<?> r = new NodoDeFincaAdaptadorHttp(http, new CredencialDelSimulador(http, "otro", RELOJ))
                .abrir(nodo, UUID.fromString("0192f0a0-0000-7000-8000-0000000000e1"), 0, 12);

        assertThat(r).isEqualTo(new LecturaResultado.Fallida<>("", "sin_credencial"));
        assertThat(autorizaciones).isEmpty();
    }

    private URI nodo() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext(CredencialDelSimulador.RUTA_TOKEN, intercambio -> {
            pedidosDeToken.add(new String(intercambio.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] bytes = "{\"access_token\":\"token-%d\",\"expires_in\":300}".formatted(pedidosDeToken.size())
                    .getBytes(StandardCharsets.UTF_8);
            intercambio.sendResponseHeaders(estadoDelToken.get(), bytes.length);
            intercambio.getResponseBody().write(bytes);
            intercambio.close();
        });
        servidor.createContext("/ingesta/", intercambio -> {
            autorizaciones.add(intercambio.getRequestHeaders().getOrDefault("Authorization", List.of("(sin)")).get(0));
            intercambio.getRequestBody().readAllBytes();
            intercambio.sendResponseHeaders(503, -1);
            intercambio.close();
        });
        servidor.start();
        return URI.create("http://localhost:" + servidor.getAddress().getPort());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - Clock.fixed(Instant.parse(…), ZoneOffset.UTC): un reloj que siempre da la misma hora; el vencimiento del
 *   token no depende de cuándo corra la prueba.
 * - HttpServer.create(new InetSocketAddress("localhost", 0), 0) (JDK, com.sun.net.httpserver): un servidor HTTP
 *   mínimo en un puerto libre que hace de gateway: una ruta para el token y otra para la ingesta.
 * - createContext(ruta, intercambio -> { … }): lo que responde cada ruta; getRequestBody() lee el cuerpo,
 *   getRequestHeaders().getOrDefault("Authorization", …) la cabecera, sendResponseHeaders(estado, largo) responde.
 *   Un largo de -1 es «sin cuerpo».
 * - CopyOnWriteArrayList / AtomicInteger: lista y número seguros entre hilos; el servidor responde en otro hilo.
 * - "s3cr3t&raro" → "s3cr3t%26raro": el & del secreto llega escapado en el formulario (%26).
 * - @AfterEach: apaga el servidor después de cada caso.
 * - @Test y assertThat(...).containsExactly / hasSize / isEmpty / isEqualTo (AssertJ): los casos y sus
 *   comprobaciones.
 * - LecturaResultado<?>: el ? es «de cualquier tipo»; a la prueba solo le importa que sea la Fallida esperada.
 */
