package co.florlogic.enlinea;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiciosEnLineaAplicacionTest {

    @LocalServerPort
    int puerto;

    @Value("${spring.application.name}")
    String nombre;

    @Value("${spring.threads.virtual.enabled}")
    boolean hilosVirtuales;

    @Test
    void laSaludRespondeUp() throws Exception {
        HttpResponse<String> respuesta = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + "/actuator/health")).build(),
                HttpResponse.BodyHandlers.ofString());

        assertThat(respuesta.statusCode()).isEqualTo(200);
        assertThat(respuesta.body()).contains("\"status\":\"UP\"");
    }

    @Test
    void configuracionBase() {
        assertThat(nombre).isEqualTo("servicios-en-linea");
        assertThat(hilosVirtuales).isTrue();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @SpringBootTest(webEnvironment = RANDOM_PORT): arranca la aplicación completa con su servidor web real en un
 *   puerto libre al azar, no en el 8090 de application.yaml.
 * - @LocalServerPort: Spring pone en el campo el puerto que eligió.
 * - @Value("${spring.application.name}"): Spring pone en el campo el valor de esa clave de configuración.
 * - HttpClient / HttpRequest / HttpResponse (JDK): la prueba llama por HTTP de verdad a /actuator/health.
 * - /actuator/health (Spring Boot Actuator): el punto de salud; responde {"status":"UP"} si la aplicación está
 *   bien.
 * - @Test y assertThat(...).isEqualTo / contains / isTrue (AssertJ): los casos y sus comprobaciones.
 */
