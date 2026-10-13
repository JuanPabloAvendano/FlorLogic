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
class ServiciosEnLineaTest {

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
