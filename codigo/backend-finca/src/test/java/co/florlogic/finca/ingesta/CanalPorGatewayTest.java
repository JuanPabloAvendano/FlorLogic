package co.florlogic.finca.ingesta;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * El canal de TX-1 pasando por el gateway de la finca (despliegue/ESPEC-gateway-finca.md, C-1 a C-7): Nginx real con
 * la plantilla de {@code despliegue/gateway/}, backend real y PostgreSQL real. Se salta sin Docker.
 *
 * <p>Orden de arranque (A-4): el puerto del backend solo existe cuando arrancó Spring, así que el gateway no puede
 * ser un {@code @Container} estático; se arranca en el primer caso, después de exponer ese puerto a los contenedores.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class CanalPorGatewayTest {

    private static final String SESION = "0192f0a1-7c00-7000-8000-00000000a001";
    private static final String RUTA = "/ingesta/v1/sesiones/" + SESION + "/particiones";
    private static final String RECHAZADO = "https://florlogic.co/problemas/gateway/rechazado";
    private static final String NO_DISPONIBLE = "https://florlogic.co/problemas/gateway/no-disponible";
    private static final String TRAZA = "[0-9a-f]{32}";

    // Las pruebas corren desde backend-finca/: la configuración probada es la misma que despliega Compose.
    private static final Path DESPLIEGUE = Path.of("..", "despliegue");

    @Container
    @ServiceConnection
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    private static GenericContainer<?> gateway;

    @LocalServerPort
    int puerto;

    @Autowired
    JdbcTemplate jdbc;

    // HTTP/1.1: este Nginx no tiene HTTP/2 activado, y así la prueba no depende de cómo el cliente negocia h2c.
    private final HttpClient http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    @BeforeEach
    void gatewayHaciaEsteBackendYBaseVacia() {
        if (gateway == null) {
            org.testcontainers.Testcontainers.exposeHostPorts(puerto);
            GenericContainer<?> nuevo = gatewayHacia("host.testcontainers.internal:" + puerto);
            nuevo.start();
            // Solo después de arrancar: si el arranque falla, el caso siguiente lo intenta otra vez con el error real.
            gateway = nuevo;
        }
        jdbc.update("DELETE FROM anotacion_recibida");
    }

    @AfterAll
    static void apagarGateway() {
        if (gateway != null) {
            gateway.stop();
            gateway = null;
        }
    }

    @Test
    void la_particion_dorada_pasa_por_el_gateway_y_responde_200() throws Exception {
        HttpResponse<String> r = post(base(gateway) + RUTA, dorada());

        assertThat(r.statusCode()).isEqualTo(200);
        assertThat(r.headers().firstValue("Cache-Control")).hasValue("no-store");
        assertThat(r.headers().firstValue("Server")).hasValue("nginx");     // sin versión (A-7)
        JsonNode cuerpo = json(r);
        assertThat(cuerpo.get("particion_uuid").asString()).isEqualTo("0192f0a1-7d10-7000-8000-00000000b001");
        assertThat(cuerpo.at("/conteo/aplicadas").asInt()).isEqualTo(2);
        assertThat(filas()).isEqualTo(2);
    }

    @Test
    void el_422_del_backend_pasa_tal_cual() throws Exception {
        HttpResponse<String> r = post(base(gateway) + RUTA, "{ esto no es json");

        assertThat(r.statusCode()).isEqualTo(422);
        assertThat(r.body()).contains("https://florlogic.co/problemas/ingesta/rechazado");
        assertThat(filas()).isZero();
    }

    @Test
    void una_ruta_ajena_es_404_rechazado_y_no_llega_al_backend() throws Exception {
        HttpResponse<String> r = get(base(gateway) + "/actuator/health");

        assertThat(r.statusCode()).isEqualTo(404);
        assertEsProblemaDelGateway(r, RECHAZADO, 404);
    }

    @Test
    void otro_metodo_es_405_con_allow_post() throws Exception {
        HttpResponse<String> r = get(base(gateway) + RUTA);

        assertThat(r.statusCode()).isEqualTo(405);
        assertThat(r.headers().firstValue("Allow")).hasValue("POST");
        assertEsProblemaDelGateway(r, RECHAZADO, 405);
    }

    @Test
    void con_el_backend_apagado_es_502_no_disponible() throws Exception {
        // Un segundo gateway apunta a un puerto donde nadie escucha: la conexión se rechaza (C-4).
        try (GenericContainer<?> sinBackend = gatewayHacia("127.0.0.1:9")) {
            sinBackend.start();

            HttpResponse<String> r = post(base(sinBackend) + RUTA, dorada());

            assertThat(r.statusCode()).isEqualTo(502);
            assertEsProblemaDelGateway(r, NO_DISPONIBLE, 502);
        }
    }

    @Test
    void un_cuerpo_de_mas_de_1_mib_es_413_en_cualquier_ruta() throws Exception {
        // La partición dorada, válida, rellenada con espacios: si el gateway la dejara pasar, el backend guardaría 2
        // filas. Así el «no llegó» de abajo prueba algo.
        String dorada = dorada();
        String grande = dorada + " ".repeat(1024 * 1024 + 1 - dorada.length());
        for (String ruta : new String[] {RUTA, "/otra"}) {
            // Con Content-Length y Expect: 100-continue, Nginx rechaza por el tamaño declarado, sin recibir el MiB.
            HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gateway) + ruta))
                    .header("Content-Type", "application/json")
                    .expectContinue(true)
                    .POST(HttpRequest.BodyPublishers.ofString(grande))
                    .build(), HttpResponse.BodyHandlers.ofString());

            assertThat(r.statusCode()).as(ruta).isEqualTo(413);
            assertEsProblemaDelGateway(r, RECHAZADO, 413);
        }
        assertThat(filas()).isZero();
    }

    @Test
    void una_ruta_demasiado_larga_es_414_en_problem_json() throws Exception {
        HttpResponse<String> r = get(base(gateway) + "/" + "a".repeat(9_000));

        assertThat(r.statusCode()).isEqualTo(414);
        assertEsProblemaDelGateway(r, RECHAZADO, 414);
    }

    @Test
    void el_trace_id_sale_del_traceparent() throws Exception {
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gateway) + "/actuator/health"))
                .header("traceparent", "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01")
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(json(r).get("traceId").asString()).isEqualTo("4bf92f3577b34da6a3ce929d0e0e4736");
    }

    @Test
    void un_traceparent_mal_formado_no_entra_al_error() throws Exception {
        // Si se copiara tal cual, un cliente podría meter su propio texto en el JSON y en el registro (A-11).
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gateway) + "/actuator/health"))
                .header("traceparent", "basura\",\"inyectado\":\"si")
                .build(), HttpResponse.BodyHandlers.ofString());

        assertEsProblemaDelGateway(r, RECHAZADO, 404);
        assertThat(r.body()).doesNotContain("basura").doesNotContain("inyectado");
    }

    /** Lo que todo error propio del gateway cumple (A-3): problem+json, sin caché, su type y una traza. */
    private static void assertEsProblemaDelGateway(HttpResponse<String> r, String type, int status) {
        assertThat(r.headers().firstValue("Content-Type")).hasValue("application/problem+json");
        assertThat(r.headers().firstValue("Cache-Control")).hasValue("no-store");
        JsonNode cuerpo = json(r);
        assertThat(cuerpo.get("type").asString()).isEqualTo(type);
        assertThat(cuerpo.get("status").asInt()).isEqualTo(status);
        assertThat(cuerpo.get("traceId").asString()).matches(TRAZA);
    }

    /**
     * El mismo entorno que el servicio gateway de compose.yaml, con otra dirección del backend (A-4): la carpeta
     * gateway/ entera, como la monta Compose, para que una plantilla nueva entre igual en los dos.
     */
    private static GenericContainer<?> gatewayHacia(String backend) {
        return new GenericContainer<>(DockerImageName.parse(imagenDeCompose()))
                .withCopyFileToContainer(MountableFile.forHostPath(DESPLIEGUE.resolve("gateway")), "/etc/nginx/templates")
                .withEnv("FLORLOGIC_BACKEND", backend)
                .withEnv("NGINX_ENVSUBST_FILTER", "^FLORLOGIC_")
                .withExposedPorts(80)
                .waitingFor(Wait.forHttp("/").forStatusCode(404));
    }

    /** La imagen del servicio gateway de compose.yaml: así la prueba nunca corre otra versión de Nginx. */
    private static String imagenDeCompose() {
        try {
            return Files.readAllLines(DESPLIEGUE.resolve("compose.yaml")).stream()
                    .map(String::strip)
                    .filter(linea -> linea.startsWith("image: nginx:"))
                    .map(linea -> linea.substring("image: ".length()).split("\\s")[0])
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("compose.yaml no declara la imagen de Nginx"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String base(GenericContainer<?> nginx) {
        return "http://" + nginx.getHost() + ":" + nginx.getMappedPort(80);
    }

    private HttpResponse<String> post(String url, String cuerpo) throws IOException, InterruptedException {
        return http.send(HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> get(String url) throws IOException, InterruptedException {
        return http.send(HttpRequest.newBuilder(URI.create(url)).build(), HttpResponse.BodyHandlers.ofString());
    }

    private static JsonNode json(HttpResponse<String> r) {
        return JsonMapper.builder().build().readTree(r.body());
    }

    private static String dorada() throws IOException {
        try (var in = CanalPorGatewayTest.class.getResourceAsStream("/canal/peticion-particion.json")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private int filas() {
        return jdbc.queryForObject("SELECT count(*) FROM anotacion_recibida", Integer.class);
    }
}
