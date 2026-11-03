package co.florlogic.finca.ingesta;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import co.florlogic.finca.EmisorDePrueba;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

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

    @DynamicPropertySource
    static void confiaEnElEmisorDePrueba(DynamicPropertyRegistry propiedades) {
        EmisorDePrueba.registrar(propiedades);   // CT-2: los tokens de EmisorDePrueba
    }

    @Autowired
    JdbcTemplate jdbc;

    private final HttpClient http = HttpClient.newHttpClient();

    @BeforeEach
    void baseVacia() {
        jdbc.update("DELETE FROM anotacion_recibida");
        jdbc.update("DELETE FROM anotacion_rechazada");
        jdbc.update("DELETE FROM avance_dispositivo");
        jdbc.update("DELETE FROM sesion_sincronizacion");
        // Una sesión ya abierta (CT-1): estas pruebas son del canal de particiones; abrir y cerrar tienen la suya.
        jdbc.update("INSERT INTO sesion_sincronizacion VALUES (?::uuid, ?::uuid, ?, now(), 0, 12, true, 'ABIERTA', null)",
                SESION, "0192f0a0-0000-7000-8000-0000000000e1", EmisorDePrueba.PERSONA);
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
    void confirmado_hasta_es_el_avance_real_del_celular() throws Exception {
        // C-104 (Segmento 1b): 1 y 2, después 4, después 3, después 4 otra vez → 2, 2, 4, 4, y la tabla igual.
        long[][] particiones = {{1, 2}, {4}, {3}, {4}};
        long[] esperado = {2, 2, 4, 4};
        for (int i = 0; i < particiones.length; i++) {
            HttpResponse<String> r = post(SESION, particionCon(particiones[i]));

            assertThat(r.statusCode()).isEqualTo(200);
            assertThat(JsonMapper.builder().build().readTree(r.body()).get("confirmado_hasta").asLong())
                    .as("partición %d", i + 1).isEqualTo(esperado[i]);
            assertThat(jdbc.queryForObject("SELECT confirmado_hasta FROM avance_dispositivo", Long.class))
                    .as("tabla tras la partición %d", i + 1).isEqualTo(esperado[i]);
        }
        assertThat(filas()).isEqualTo(4);
    }

    @Test
    void una_anotacion_que_el_dominio_rechaza_es_422_y_no_guarda_ni_mueve_el_avance() throws Exception {
        // C-105, A-108: un id que no es UUID v7, o un consecutivo 0; la otra anotación de la partición tampoco entra.
        String noV7 = particionCon(1, 2).replace("0192f0a0-2000-7000-8000-000000000001", "0192f0a0-2000-4000-8000-000000000001");
        String consecutivoCero = particionCon(1, 2).replace("\"consecutivo\":2", "\"consecutivo\":0");
        for (String mala : new String[] {noV7, consecutivoCero}) {
            HttpResponse<String> r = post(SESION, mala);

            assertThat(r.statusCode()).isEqualTo(422);
            assertThat(r.body()).contains("ingesta/rechazado");
        }
        assertThat(filas()).isZero();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM avance_dispositivo", Integer.class)).isZero();
    }

    @Test
    void dos_particiones_del_mismo_celular_a_la_vez_la_que_pierde_es_409_y_al_reintentar_entra() throws Exception {
        // C-112, A-98. La partición A (consecutivo 1) se demora 2 s al guardar su anotación; mientras, B (consecutivo 2)
        // lee el mismo avance vacío, lo guarda primero y gana. A choca al guardar el avance y se deshace entera.
        jdbc.execute("""
                CREATE FUNCTION particion_lenta() RETURNS trigger AS $$
                BEGIN
                    IF NEW.consecutivo = 1 THEN PERFORM pg_sleep(2); END IF;
                    RETURN NEW;
                END $$ LANGUAGE plpgsql""");
        jdbc.execute("CREATE TRIGGER particion_lenta BEFORE INSERT ON anotacion_recibida "
                + "FOR EACH ROW EXECUTE FUNCTION particion_lenta()");
        HttpResponse<String> a;
        try {
            String particionA = particionCon(1);
            CompletableFuture<HttpResponse<String>> enCurso = CompletableFuture.supplyAsync(() -> postSinLanzar(particionA));
            Thread.sleep(500);
            HttpResponse<String> b = post(SESION, particionCon(2));
            a = enCurso.get(15, TimeUnit.SECONDS);

            assertThat(b.statusCode()).isEqualTo(200);
            assertThat(a.statusCode()).isEqualTo(409);
            assertThat(a.headers().firstValue("Retry-After")).hasValue("1");
            assertThat(a.headers().firstValue("Cache-Control")).hasValue("no-store");
            assertThat(a.headers().firstValue("Content-Type")).hasValueSatisfying(t -> assertThat(t).startsWith("application/problem+json"));
            assertThat(a.body()).contains("https://florlogic.co/problemas/ingesta/particion-en-curso");
            assertThat(filas()).as("solo la de B; la anotación de A se deshizo").isEqualTo(1);
        } finally {
            jdbc.execute("DROP TRIGGER particion_lenta ON anotacion_recibida");
            jdbc.execute("DROP FUNCTION particion_lenta()");
        }

        HttpResponse<String> reintento = post(SESION, particionCon(1));

        assertThat(reintento.statusCode()).isEqualTo(200);
        assertThat(JsonMapper.builder().build().readTree(reintento.body()).get("confirmado_hasta").asLong()).isEqualTo(2);
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
                .header("Authorization", "Bearer " + EmisorDePrueba.operador())
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    private static String dorada() throws IOException {
        try (var in = CanalParticionesTest.class.getResourceAsStream("/canal/peticion-particion.json")) {
            return new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }

    private HttpResponse<String> postSinLanzar(String cuerpo) {
        try {
            return post(SESION, cuerpo);
        } catch (IOException | InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    /** La partición dorada con una anotación por consecutivo: la primera del contrato, con otro uuid v7. */
    private static String particionCon(long... consecutivos) throws IOException {
        JsonMapper json = JsonMapper.builder().build();
        ObjectNode particion = (ObjectNode) json.readTree(dorada());
        ObjectNode modelo = (ObjectNode) particion.get("anotaciones").get(0);
        ArrayNode anotaciones = particion.putArray("anotaciones");
        for (long consecutivo : consecutivos) {
            ObjectNode anotacion = modelo.deepCopy();
            anotacion.put("anotacion_uuid", "0192f0a0-2000-7000-8000-%012d".formatted(consecutivo));
            anotacion.put("consecutivo", consecutivo);
            anotaciones.add(anotacion);
        }
        return particion.toString();
    }

    private int filas() {
        return jdbc.queryForObject("SELECT count(*) FROM anotacion_recibida", Integer.class);
    }
}
