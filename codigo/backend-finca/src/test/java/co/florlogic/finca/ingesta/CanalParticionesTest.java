package co.florlogic.finca.ingesta;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.BeforeEach;
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
 * El canal de TX-1 de punta a punta en el backend (camino mínimo, D-17): HTTP real, PostgreSQL real.
 * La petición es la dorada del contrato (copiada de contrato/src/test/resources/dorados/). Se salta sin Docker.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class CanalParticionesTest {

    private static final String SESION = "0192f0a1-7c00-7000-8000-00000000a001";

    @Container
    @ServiceConnection
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    @LocalServerPort
    int puerto;

    @Autowired
    JdbcTemplate jdbc;

    private final HttpClient http = HttpClient.newHttpClient();

    @BeforeEach
    void baseVacia() {
        jdbc.update("DELETE FROM anotacion_recibida");
    }

    @Test
    void la_particion_dorada_se_guarda_y_responde_200() throws Exception {
        HttpResponse<String> r = post(SESION, dorada());

        assertThat(r.statusCode()).isEqualTo(200);
        assertThat(r.headers().firstValue("Cache-Control")).hasValue("no-store");
        JsonNode cuerpo = JsonMapper.builder().build().readTree(r.body());
        assertThat(cuerpo.get("particion_uuid").asString()).isEqualTo("0192f0a1-7d10-7000-8000-00000000b001");
        assertThat(cuerpo.at("/conteo/recibidas").asInt()).isEqualTo(2);
        assertThat(cuerpo.at("/conteo/aplicadas").asInt()).isEqualTo(2);
        assertThat(cuerpo.get("confirmado_hasta").asLong()).isEqualTo(2);
        assertThat(filas()).isEqualTo(2);
        // La fila lleva la sesión de la ruta, no otra (OBS-2-10).
        assertThat(jdbc.queryForList("SELECT DISTINCT sesion_id::text FROM anotacion_recibida", String.class))
                .containsExactly(SESION);
        // Se guarda el árbol, no el DTO: el "valor": null de la segunda anotación sigue ahí (A-59).
        assertThat(jdbc.queryForObject(
                "SELECT contenido -> 'datos' ? 'valor' FROM anotacion_recibida WHERE consecutivo = 2", Boolean.class))
                .isTrue();
    }

    @Test
    void reenviar_la_misma_particion_no_duplica() throws Exception {
        post(SESION, dorada());

        HttpResponse<String> r = post(SESION, dorada());

        assertThat(r.statusCode()).isEqualTo(200);
        JsonNode cuerpo = JsonMapper.builder().build().readTree(r.body());
        assertThat(cuerpo.at("/conteo/aplicadas").asInt()).isZero();
        assertThat(cuerpo.at("/conteo/sin_efecto").asInt()).isEqualTo(2);
        assertThat(filas()).isEqualTo(2);
    }

    @Test
    void cualquier_fallo_de_forma_es_422_rechazado_y_no_guarda_nada() throws Exception {
        String[] malas = {
            "{ esto no es json",
            "",
            "[]",
            "{\"contrato\":\"1.0\",\"particion_uuid\":\"0192f0a1-7d10-7000-8000-00000000b001\",\"sesiones_captura\":[]}",
            dorada().replace("\"contrato\": \"1.0\",", "\"contrato\": \"1.0\", \"cama_id\": \"B3-C17\","),
            dorada().replace("\"consecutivo\": 1,", "\"consecutivo\": \"uno\","),
            // Un null dentro del arreglo desalineaba el DTO y el JSON crudo (OBS-2-1).
            dorada().replace("\"anotaciones\": [", "\"anotaciones\": [null, "),
            dorada().replace("\"anotaciones\": [", "\"anotaciones\": [5, "),
            // JSON válido que PostgreSQL no puede guardar en jsonb: daba 500 en cada intento (COH-105).
            dorada().replace("\"B3-C17-S1\"", "\"B3-C17\\u0000-S1\""),
            dorada().replace("\"seccion_id\": \"B3-C17-S1\"", "\"secc\\u0000ion_id\": \"B3-C17-S1\""),
        };
        for (String mala : malas) {
            HttpResponse<String> r = post(SESION, mala);
            assertThat(r.statusCode()).as(mala).isEqualTo(422);
            assertThat(r.headers().firstValue("Content-Type")).hasValueSatisfying(t -> assertThat(t).startsWith("application/problem+json"));
            assertThat(r.body()).contains("ingesta/rechazado");
        }
        assertThat(filas()).isZero();
    }

    @Test
    void una_sesion_que_no_es_uuid_es_422() throws Exception {
        assertThat(post("no-es-un-uuid", dorada()).statusCode()).isEqualTo(422);
        assertThat(filas()).isZero();
    }

    @Test
    void si_la_base_falla_a_mitad_es_500_generico_y_no_queda_nada_a_medias() throws Exception {
        // C-116: la segunda anotación hace fallar el INSERT; la primera ya había entrado y el rollback la quita.
        jdbc.execute("""
                CREATE FUNCTION falla_a_mitad() RETURNS trigger AS $$
                BEGIN
                    IF NEW.consecutivo = 2 THEN RAISE EXCEPTION 'la base cae a mitad'; END IF;
                    RETURN NEW;
                END $$ LANGUAGE plpgsql""");
        jdbc.execute("CREATE TRIGGER falla_a_mitad BEFORE INSERT ON anotacion_recibida "
                + "FOR EACH ROW EXECUTE FUNCTION falla_a_mitad()");
        try {
            HttpResponse<String> r = post(SESION, dorada());

            assertThat(r.statusCode()).isEqualTo(500);
            assertThat(r.headers().firstValue("Content-Type")).hasValueSatisfying(t -> assertThat(t).startsWith("application/problem+json"));
            assertThat(r.headers().firstValue("Cache-Control")).hasValue("no-store");
            assertThat(r.body()).contains("https://florlogic.co/problemas/ingesta/error-interno")
                    .doesNotContain("la base cae a mitad");
            assertThat(filas()).isZero();
        } finally {
            jdbc.execute("DROP TRIGGER falla_a_mitad ON anotacion_recibida");
            jdbc.execute("DROP FUNCTION falla_a_mitad()");
        }
    }

    private HttpResponse<String> post(String sesion, String cuerpo) throws IOException, InterruptedException {
        return http.send(HttpRequest.newBuilder(
                        URI.create("http://localhost:" + puerto + "/ingesta/v1/sesiones/" + sesion + "/particiones"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    private static String dorada() throws IOException {
        try (var in = CanalParticionesTest.class.getResourceAsStream("/canal/peticion-particion.json")) {
            return new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    private int filas() {
        return jdbc.queryForObject("SELECT count(*) FROM anotacion_recibida", Integer.class);
    }
}
