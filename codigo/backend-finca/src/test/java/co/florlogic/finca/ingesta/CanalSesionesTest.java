package co.florlogic.finca.ingesta;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import co.florlogic.finca.EmisorDePrueba;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import co.florlogic.finca.bitacora.Bitacora;
import co.florlogic.finca.dispositivos.RegistroDeDispositivos;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * La sesión de sincronización de punta a punta (CT-1; recorrido de TX-1, pasos 3, 6, 14 y 15): abrir, entregar,
 * cerrar; después de cerrar, la misma sesión ya no recibe. Se salta sin Docker.
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

    @DynamicPropertySource
    static void confiaEnElEmisorDePrueba(DynamicPropertyRegistry propiedades) {
        EmisorDePrueba.registrar(propiedades);   // CT-2: los tokens de EmisorDePrueba
    }

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    RegistroDeDispositivos dispositivos;

    @Autowired
    Bitacora bitacora;

    private final HttpClient http = HttpClient.newHttpClient();

    @Test
    void abrir_entregar_y_cerrar() throws Exception {
        long relojDelCelular = System.currentTimeMillis() + 5_000;
        HttpResponse<String> abierta = post("/ingesta/v1/sesiones", """
                {"contrato":"1.0","dispositivo_uuid":"0192f0a0-0000-7000-8000-0000000000e2","reloj_ms":%d,"version_config":12}"""
                .formatted(relojDelCelular));

        assertThat(abierta.statusCode()).isEqualTo(201);
        JsonNode sesion = JSON.readTree(abierta.body());
        String id = sesion.get("sesion_uuid").asString();
        assertThat(java.util.UUID.fromString(id).version()).isEqualTo(7);
        assertThat(sesion.get("desfase_ms").asLong()).isBetween(3_000L, 6_000L);
        assertThat(sesion.get("confirmado_hasta").asLong()).isZero();
        assertThat(sesion.get("version_config").asInt()).isEqualTo(12);

        HttpResponse<String> particion = post("/ingesta/v1/sesiones/" + id + "/particiones", dorada());
        assertThat(particion.statusCode()).isEqualTo(200);
        assertThat(JSON.readTree(particion.body()).get("confirmado_hasta").asLong()).isEqualTo(2);

        HttpResponse<String> cerrada = post("/ingesta/v1/sesiones/" + id + "/cierre", "{\"contrato\":\"1.0\",\"completa\":true}");
        assertThat(cerrada.statusCode()).isEqualTo(200);
        assertThat(JSON.readTree(cerrada.body()).get("estado").asString()).isEqualTo("cerrada");
        assertThat(JSON.readTree(cerrada.body()).get("confirmado_hasta").asLong()).isEqualTo(2);

        HttpResponse<String> tarde = post("/ingesta/v1/sesiones/" + id + "/particiones", dorada());
        assertThat(tarde.statusCode()).isEqualTo(404);
        assertThat(tarde.body()).contains("ingesta/sesion-desconocida");
    }

    @Test
    void un_celular_nuevo_queda_pendiente_y_lo_suyo_entra_marcado_y_va_al_ciclo() throws Exception {
        String id = abrir("0192f0a0-0000-7000-8000-0000000000e3");
        // Otros ids que la dorada, que ya entró en otra prueba con esta misma base (sería sin_efecto).
        HttpResponse<String> r = post("/ingesta/v1/sesiones/" + id + "/particiones",
                dorada().replace("00000000d001", "00000000e301").replace("00000000d002", "00000000e302"));

        JsonNode cuerpo = JSON.readTree(r.body());
        assertThat(cuerpo.at("/conteo/marcadas").asInt()).as("CT-10: dispositivo_no_registrado").isEqualTo(2);
        assertThat(cuerpo.toString()).contains("dispositivo_no_registrado");
        // CT-12: el ciclo recibió los hechos y abrió los trabajos del gerente.
        assertThat(jdbc.queryForObject("SELECT count(*) FROM hecho_produccion WHERE seccion_id = 'B3-C17-S1'", Integer.class))
                .isPositive();
        assertThat(jdbc.queryForObject("SELECT count(*) FROM trabajo_gerente", Integer.class)).isPositive();
        // CT-11: la bitácora tiene la sesión y la partición, y la cadena está íntegra.
        assertThat(jdbc.queryForList("SELECT evento FROM bitacora WHERE objeto = ?", String.class, id))
                .contains("sesion_abierta", "particion_recibida");
        assertThat(bitacora.cadenaIntegra()).isTrue();
    }

    @Test
    void un_celular_bloqueado_no_abre_sesion() throws Exception {
        java.util.UUID celular = java.util.UUID.fromString("0192f0a0-0000-7000-8000-0000000000e4");
        dispositivos.bloquear(celular, "admin-1");

        HttpResponse<String> r = post("/ingesta/v1/sesiones", """
                {"contrato":"1.0","dispositivo_uuid":"%s","reloj_ms":%d,"version_config":12}"""
                .formatted(celular, System.currentTimeMillis()));

        assertThat(r.statusCode()).isEqualTo(403);
        assertThat(r.body()).contains("dispositivo_bloqueado");
    }

    @Test
    void la_bitacora_no_deja_cambiar_ni_borrar() {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> jdbc.update("DELETE FROM bitacora"))
                .hasMessageContaining("solo inserción");
    }

    private String abrir(String celular) throws Exception {
        HttpResponse<String> abierta = post("/ingesta/v1/sesiones", """
                {"contrato":"1.0","dispositivo_uuid":"%s","reloj_ms":%d,"version_config":12}"""
                .formatted(celular, System.currentTimeMillis()));
        assertThat(abierta.statusCode()).isEqualTo(201);
        return JSON.readTree(abierta.body()).get("sesion_uuid").asString();
    }

    @Test
    void una_sesion_que_no_existe_es_404_y_un_cuerpo_incompleto_es_422() throws Exception {
        assertThat(post("/ingesta/v1/sesiones/0192f0a1-7c00-7000-8000-0000000000ff/cierre", "{\"contrato\":\"1.0\"}")
                .statusCode()).isEqualTo(404);
        assertThat(post("/ingesta/v1/sesiones/0192f0a1-7c00-7000-8000-0000000000ff/particiones", dorada())
                .statusCode()).isEqualTo(404);
        assertThat(post("/ingesta/v1/sesiones", "{\"contrato\":\"1.0\"}").statusCode()).isEqualTo(422);
        assertThat(post("/ingesta/v1/sesiones", "{\"contrato\":\"1.0\",\"campo_raro\":1}").statusCode()).isEqualTo(422);
    }

    private HttpResponse<String> post(String ruta, String cuerpo) throws IOException, InterruptedException {
        return http.send(HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + EmisorDePrueba.operador())
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    private static String dorada() throws IOException {
        try (var in = CanalSesionesTest.class.getResourceAsStream("/canal/peticion-particion.json")) {
            return new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @SpringBootTest(webEnvironment = RANDOM_PORT): arranca la aplicación completa con un servidor HTTP real en un
 *   puerto libre al azar.
 * - @Testcontainers(disabledWithoutDocker = true): maneja los contenedores Docker de la prueba; sin Docker la clase
 *   se salta, no falla.
 * - @Container + @ServiceConnection sobre PostgreSQLContainer("postgres:16"): un PostgreSQL real en Docker;
 *   @ServiceConnection hace que Spring se conecte a él sin escribir la URL.
 * - @LocalServerPort: inyecta el puerto al azar donde quedó escuchando el backend.
 * - @DynamicPropertySource: agrega propiedades de Spring calculadas al arrancar; aquí EmisorDePrueba registra el
 *   emisor de tokens de prueba.
 * - @Autowired JdbcTemplate: Spring entrega su cliente SQL para preparar y revisar la base.
 * - @Test: cada método así es un caso que JUnit 5 corre por separado.
 * - assertThat(...).isEqualTo / hasValue / contains (AssertJ): comprobaciones; .as("…") pone el texto que sale si
 *   falla.
 * - HttpClient / HttpRequest / HttpResponse (java.net.http): cliente HTTP del JDK; BodyPublishers.ofString pone el
 *   cuerpo y BodyHandlers.ofString lee la respuesta como texto.
 * - Authorization: Bearer <token>: la cabecera con el token del operador de prueba.
 * - JsonMapper / JsonNode (Jackson 3): leen la respuesta; at("/conteo/recibidas") navega el JSON con un JSON
 *   Pointer.
 * - @Autowired RegistroDeDispositivos / Bitacora: beans públicos de otros módulos, para bloquear un celular y
 *   revisar la cadena de la bitácora.
 * - """ … """.formatted(...): bloque de texto con %d y %s que se llenan con los valores.
 * - UUID.fromString(id).version(): comprueba que la sesión nueva sea UUID v7.
 * - isBetween(3_000L, 6_000L): el desfase debe caer en ese rango (el celular va 5 s adelantado, con margen).
 * - assertThatThrownBy(() -> …).hasMessageContaining("solo inserción"): espera que la base rechace el DELETE.
 */
