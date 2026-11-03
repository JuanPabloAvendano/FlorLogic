package co.florlogic.finca.ingesta;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * La sesión de sincronización de punta a punta (CT-1; recorrido de TX-1, pasos 3, 6, 14 y 15): abrir, entregar,
 * cerrar; después de cerrar, la misma sesión ya no recibe. El celular de la partición es el de su sesión (CT-7). Se
 * salta sin Docker. Cada caso usa su propio celular, para que el avance de uno no toque el de otro.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class CanalSesionesTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    @Container
    @ServiceConnection
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    @LocalServerPort
    int puerto;

    @Autowired
    JdbcTemplate jdbc;

    private final HttpClient http = HttpClient.newHttpClient();

    @Test
    void abrir_entregar_y_cerrar() throws Exception {
        long relojDelCelular = System.currentTimeMillis() + 5_000;
        HttpResponse<String> abierta = post("/ingesta/v1/sesiones", """
                {"contrato":"1.0","dispositivo_uuid":"0192f0a0-0000-7000-8000-0000000000e2","reloj_ms":%d,"version_config":12}"""
                .formatted(relojDelCelular));

        assertThat(abierta.statusCode()).isEqualTo(201);
        assertThat(abierta.headers().firstValue("Cache-Control")).hasValue("no-store");
        JsonNode sesion = JSON.readTree(abierta.body());
        String id = sesion.get("sesion_uuid").asString();
        assertThat(UUID.fromString(id).version()).isEqualTo(7);
        assertThat(sesion.get("desfase_ms").asLong()).isBetween(3_000L, 6_000L);
        assertThat(sesion.get("confirmado_hasta").asLong()).isZero();
        assertThat(sesion.get("version_config").asInt()).isEqualTo(12);

        HttpResponse<String> particion = post("/ingesta/v1/sesiones/" + id + "/particiones", dorada());
        assertThat(particion.statusCode()).isEqualTo(200);
        assertThat(JSON.readTree(particion.body()).get("confirmado_hasta").asLong()).isEqualTo(2);
        // CT-7: el avance quedó en el celular de la sesión, no en uno configurado en el nodo.
        assertThat(jdbc.queryForObject("SELECT confirmado_hasta FROM avance_dispositivo WHERE dispositivo_id = ?::uuid",
                Long.class, "0192f0a0-0000-7000-8000-0000000000e2")).isEqualTo(2);

        HttpResponse<String> cerrada = post("/ingesta/v1/sesiones/" + id + "/cierre", "{\"contrato\":\"1.0\",\"completa\":true}");
        assertThat(cerrada.statusCode()).isEqualTo(200);
        assertThat(JSON.readTree(cerrada.body()).get("estado").asString()).isEqualTo("cerrada");
        assertThat(JSON.readTree(cerrada.body()).get("confirmado_hasta").asLong()).isEqualTo(2);

        HttpResponse<String> tarde = post("/ingesta/v1/sesiones/" + id + "/particiones", dorada());
        assertThat(tarde.statusCode()).isEqualTo(404);
        assertThat(tarde.body()).contains("ingesta/sesion-desconocida");
    }

    @Test
    void una_sesion_parcial_se_completa_al_reabrir_sin_duplicar() throws Exception {
        // TX-1.T 2: el celular cierra parcial; la sesión siguiente arranca en confirmado_hasta y el reenvío no duplica.
        String celular = "0192f0a0-0000-7000-8000-0000000000e3";
        String propia = dorada().replace("00000000d001", "00000000e301").replace("00000000d002", "00000000e302");
        String primera = abrir(celular);
        assertThat(post("/ingesta/v1/sesiones/" + primera + "/particiones", propia).statusCode()).isEqualTo(200);

        HttpResponse<String> parcial = post("/ingesta/v1/sesiones/" + primera + "/cierre",
                "{\"contrato\":\"1.0\",\"completa\":false}");
        assertThat(JSON.readTree(parcial.body()).get("estado").asString()).isEqualTo("parcial");
        // Cerrar otra vez responde lo mismo: el celular puede reintentar el cierre sin miedo.
        assertThat(JSON.readTree(post("/ingesta/v1/sesiones/" + primera + "/cierre",
                "{\"contrato\":\"1.0\",\"completa\":true}").body()).get("estado").asString()).isEqualTo("parcial");

        HttpResponse<String> segunda = post("/ingesta/v1/sesiones", """
                {"contrato":"1.0","dispositivo_uuid":"%s","reloj_ms":%d,"version_config":12}"""
                .formatted(celular, System.currentTimeMillis()));
        assertThat(JSON.readTree(segunda.body()).get("confirmado_hasta").asLong()).isEqualTo(2);
        String id = JSON.readTree(segunda.body()).get("sesion_uuid").asString();
        JsonNode reenvio = JSON.readTree(post("/ingesta/v1/sesiones/" + id + "/particiones", propia).body());
        assertThat(reenvio.at("/conteo/sin_efecto").asInt()).isEqualTo(2);
        assertThat(jdbc.queryForObject(
                "SELECT count(*) FROM anotacion_recibida WHERE anotacion_uuid::text LIKE '%00000000e30_'", Integer.class))
                .isEqualTo(2);
    }

    @Test
    void una_sesion_que_no_existe_es_404_y_un_cuerpo_incompleto_es_422() throws Exception {
        assertThat(post("/ingesta/v1/sesiones/0192f0a1-7c00-7000-8000-0000000000ff/cierre", "{\"contrato\":\"1.0\"}")
                .statusCode()).isEqualTo(404);
        assertThat(post("/ingesta/v1/sesiones/0192f0a1-7c00-7000-8000-0000000000ff/particiones", dorada())
                .statusCode()).isEqualTo(404);
        assertThat(post("/ingesta/v1/sesiones/no-es-uuid/cierre", "{\"contrato\":\"1.0\"}").statusCode()).isEqualTo(422);
        assertThat(post("/ingesta/v1/sesiones", "{\"contrato\":\"1.0\"}").statusCode()).isEqualTo(422);
        assertThat(post("/ingesta/v1/sesiones", "{\"contrato\":\"1.0\",\"campo_raro\":1}").statusCode()).isEqualTo(422);
        assertThat(post("/ingesta/v1/sesiones", "{ roto").statusCode()).isEqualTo(422);
    }

    private String abrir(String celular) throws Exception {
        HttpResponse<String> abierta = post("/ingesta/v1/sesiones", """
                {"contrato":"1.0","dispositivo_uuid":"%s","reloj_ms":%d,"version_config":12}"""
                .formatted(celular, System.currentTimeMillis()));
        assertThat(abierta.statusCode()).isEqualTo(201);
        return JSON.readTree(abierta.body()).get("sesion_uuid").asString();
    }

    private HttpResponse<String> post(String ruta, String cuerpo) throws IOException, InterruptedException {
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    private static String dorada() throws IOException {
        try (var in = CanalSesionesTest.class.getResourceAsStream("/canal/peticion-particion.json")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
