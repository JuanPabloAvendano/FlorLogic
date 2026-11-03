package co.florlogic.finca.infraestructura;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import co.florlogic.finca.EmisorDePrueba;

/**
 * El nodo como servidor de recursos (CT-2), con la cadena de validación de verdad y las llaves de
 * {@link EmisorDePrueba}: quién entra, con qué token y qué rol, y qué responde cuando no (contrato A-22: 401 antes que
 * 403). Se salta sin Docker (el contexto necesita la base).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class SeguridadDelNodoTest {

    private static final String ABRIR = "/ingesta/v1/sesiones";
    private static final String CREDENCIAL_INVALIDA = "https://florlogic.co/problemas/ingesta/credencial-invalida";
    private static final String SIN_PERMISO = "https://florlogic.co/problemas/ingesta/sin-permiso";

    @Container
    @ServiceConnection
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    @LocalServerPort
    int puerto;

    @DynamicPropertySource
    static void confiaEnElEmisorDePrueba(DynamicPropertyRegistry propiedades) {
        EmisorDePrueba.registrar(propiedades);
    }

    @Autowired
    JdbcTemplate jdbc;

    private final HttpClient http = HttpClient.newHttpClient();

    static Stream<Arguments> credencialesQueNoSirven() {
        Instant enCincoMinutos = Instant.now().plusSeconds(300);
        List<String> operador = List.of("operador");
        return Stream.of(
                Arguments.of("sin token", null),
                Arguments.of("no es un JWT", "esto-no-es-un-jwt"),
                Arguments.of("vencido", EmisorDePrueba.token(EmisorDePrueba.PERSONA, operador, EmisorDePrueba.EMISOR,
                        EmisorDePrueba.AUDIENCIA, Instant.now().minusSeconds(120))),
                Arguments.of("de otro emisor (el realm master)", EmisorDePrueba.token(EmisorDePrueba.PERSONA, operador,
                        "http://localhost/auth/realms/master", EmisorDePrueba.AUDIENCIA, enCincoMinutos)),
                Arguments.of("para otra audiencia", EmisorDePrueba.token(EmisorDePrueba.PERSONA, operador,
                        EmisorDePrueba.EMISOR, "account", enCincoMinutos)),
                Arguments.of("firmado con otra llave", EmisorDePrueba.firmadoConOtraLlave()));
    }

    @ParameterizedTest(name = "{0} → 401")
    @MethodSource("credencialesQueNoSirven")
    void una_credencial_que_no_sirve_es_401_credencial_invalida(String caso, String token) throws Exception {
        HttpResponse<String> r = abrir(token, "0192f0a0-0000-7000-8000-0000000000f1");

        assertThat(r.statusCode()).as(caso).isEqualTo(401);
        assertThat(r.headers().firstValue("WWW-Authenticate")).hasValue("Bearer");
        assertThat(r.headers().firstValue("Content-Type")).hasValueSatisfying(t -> assertThat(t).startsWith("application/problem+json"));
        assertThat(r.headers().firstValue("Cache-Control")).hasValue("no-store");
        assertThat(r.body()).contains(CREDENCIAL_INVALIDA).doesNotContain("Jwt").doesNotContain("expired");
        assertThat(sesionesDe(EmisorDePrueba.PERSONA, "0192f0a0-0000-7000-8000-0000000000f1")).isZero();
    }

    @Test
    void un_token_valido_sin_el_rol_operador_es_403_ingesta_entregar() throws Exception {
        HttpResponse<String> r = abrir(EmisorDePrueba.token("gerente-1", List.of("gerente", "administrador")),
                "0192f0a0-0000-7000-8000-0000000000f2");

        assertThat(r.statusCode()).isEqualTo(403);
        assertThat(r.body()).contains(SIN_PERMISO).contains("\"regla\":\"ingesta_entregar\"");
        assertThat(sesionesDe("gerente-1", "0192f0a0-0000-7000-8000-0000000000f2")).isZero();
    }

    @Test
    void la_persona_de_la_sesion_es_el_sub_del_token() throws Exception {
        HttpResponse<String> r = abrir(EmisorDePrueba.token("op-11", List.of("operador")), "0192f0a0-0000-7000-8000-0000000000f3");

        assertThat(r.statusCode()).as(r.body()).isEqualTo(201);
        assertThat(sesionesDe("op-11", "0192f0a0-0000-7000-8000-0000000000f3")).isEqualTo(1);
    }

    @Test
    void otra_persona_no_entrega_ni_cierra_en_la_sesion_ajena() throws Exception {
        // Contrato A-51: la credencial es quien entrega. Antes de Keycloak no había cómo probarlo por HTTP.
        HttpResponse<String> abierta = abrir(EmisorDePrueba.token("op-21", List.of("operador")),
                "0192f0a0-0000-7000-8000-0000000000f4");
        assertThat(abierta.statusCode()).isEqualTo(201);
        String sesion = abierta.body().replaceAll(".*\"sesion_uuid\"\\s*:\\s*\"([0-9a-f-]{36})\".*", "$1");

        HttpResponse<String> cierre = post("/ingesta/v1/sesiones/" + sesion + "/cierre",
                EmisorDePrueba.token("op-22", List.of("operador")), "{\"contrato\":\"1.0\",\"completa\":true}");

        assertThat(cierre.statusCode()).isEqualTo(403);
        assertThat(cierre.body()).contains("sesion_ajena");
    }

    @Test
    void la_salud_y_las_metricas_no_piden_token() throws Exception {
        // Solo se ven por la red interna: el gateway devuelve 404 a /actuator (C-3). Prometheus raspa sin token.
        assertThat(get("/actuator/health", null).statusCode()).isEqualTo(200);
        assertThat(get("/actuator/prometheus", null).statusCode()).isEqualTo(200);
    }

    @Test
    void una_ruta_sin_regla_no_existe_ni_con_token() throws Exception {
        assertThat(get("/actuator/env", null).statusCode()).isEqualTo(401);
        HttpResponse<String> conToken = get("/actuator/env", EmisorDePrueba.operador());
        assertThat(conToken.statusCode()).isEqualTo(403);
        assertThat(conToken.body()).contains(SIN_PERMISO).doesNotContain("propertySources").doesNotContain("regla");
    }

    @Test
    void un_metodo_ajeno_con_token_sigue_siendo_405_y_no_403() throws Exception {
        // El reenvío interno a /error no pasa otra vez por las reglas de rol (SeguridadDelNodo).
        assertThat(get(ABRIR, EmisorDePrueba.operador()).statusCode()).isEqualTo(405);
    }

    private HttpResponse<String> abrir(String token, String celular) throws IOException, InterruptedException {
        return post(ABRIR, token, """
                {"contrato":"1.0","dispositivo_uuid":"%s","reloj_ms":%d,"version_config":12}"""
                .formatted(celular, System.currentTimeMillis()));
    }

    private HttpResponse<String> post(String ruta, String token, String cuerpo) throws IOException, InterruptedException {
        HttpRequest.Builder peticion = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo));
        if (token != null) {
            peticion.header("Authorization", "Bearer " + token);
        }
        return http.send(peticion.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String ruta, String token) throws IOException, InterruptedException {
        HttpRequest.Builder peticion = HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + ruta));
        if (token != null) {
            peticion.header("Authorization", "Bearer " + token);
        }
        return http.send(peticion.build(), HttpResponse.BodyHandlers.ofString());
    }

    private int sesionesDe(String persona, String celular) {
        return jdbc.queryForObject("SELECT count(*) FROM sesion_sincronizacion WHERE persona = ? AND dispositivo_id = ?::uuid",
                Integer.class, persona, celular);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @SpringBootTest(webEnvironment = RANDOM_PORT) + @LocalServerPort: arranca el backend completo en un puerto
 *   libre y lo inyecta en puerto, para pegarle por HTTP con la cadena de seguridad real.
 * - @Testcontainers(disabledWithoutDocker = true), @Container, @ServiceConnection: un PostgreSQL 16 en Docker cuya
 *   conexión Spring toma sola; sin Docker toda la clase se salta, así que un verify en verde sin Docker no la probó.
 * - @DynamicPropertySource: método static que Spring llama antes de arrancar el contexto; aquí apunta las llaves y
 *   el emisor de los tokens a EmisorDePrueba.
 * - @ParameterizedTest(name = "{0} → 401") + @MethodSource("credencialesQueNoSirven"): la misma prueba corre una
 *   vez por cada fila que devuelve ese método; {0} pone el primer argumento (el nombre del caso) en el reporte.
 * - Stream<Arguments> / Arguments.of(…): cada fila son los argumentos de una corrida (caso, token).
 * - assertThat(…).as(caso): pone el nombre del caso en el mensaje si la comprobación falla.
 * - r.headers().firstValue("…") devuelve Optional<String>: hasValue exige ese valor exacto; hasValueSatisfying
 *   aplica otra comprobación al valor (que empiece por application/problem+json, que puede llevar ;charset).
 * - .doesNotContain("Jwt").doesNotContain("expired"): que el error no filtre detalles internos del fallo del token.
 * - body().replaceAll(regex, "$1"): saca del JSON de respuesta el sesion_uuid con una expresión regular; $1 es lo
 *   que capturó el primer paréntesis.
 * - """ … """.formatted(…): bloque de texto de Java (varias líneas sin escapar comillas) y formatted llena %s y %d
 *   como String.format.
 * - HttpRequest.newBuilder(…).header("Authorization", "Bearer " + token).POST(BodyPublishers.ofString(cuerpo)):
 *   el cliente HTTP del JDK con el token en la cabecera estándar; sin token no se pone la cabecera.
 * - jdbc.queryForObject(… "?::uuid" …, Integer.class, persona, celular): cuenta filas en la base; los ? se llenan
 *   en orden con los argumentos y ::uuid es el cast de PostgreSQL de texto a uuid.
 * - 401 / 403 / 405 / 201: credencial ausente o inválida, sin permiso, método HTTP no permitido en esa ruta, creado.
 * - /actuator/env: una ruta de Actuator que sí existe pero no tiene regla; debe quedar cerrada aun con token.
 */
