package co.florlogic.finca.ingesta;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.WaitingConsumer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

/**
 * El canal de TX-1 pasando por el gateway de la finca y su WAF: Nginx con ModSecurity y OWASP CRS reales, con la
 * plantilla y los ajustes de {@code despliegue/gateway/}, backend real y PostgreSQL real. Se salta sin Docker.
 * Los C-n sin prefijo son de despliegue/ESPEC-gateway-finca.md; los «WAF C-n», de despliegue/ESPEC-waf-finca.md.
 *
 * <p>Orden de arranque (A-4): el puerto del backend solo existe cuando arrancó Spring, así que el gateway no puede
 * ser un {@code @Container} estático; se arranca en el primer caso, después de exponer ese puerto a los contenedores.
 *
 * <p>El WAF corre en {@code On} en todos los casos (WAF A-2), salvo el gateway de WAF C-7, que prueba
 * {@code DetectionOnly}.
 *
 * <p>El gateway busca el backend con su {@code resolver}, que pregunta solo al DNS y no lee {@code /etc/hosts}
 * (ESPEC-esqueleto-tx1 A-17, A-18): aquí recibe el backend por IP, que no necesita búsqueda, y siempre un
 * {@code FLORLOGIC_DNS}, porque sin él Nginx no arranca.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class CanalPorGatewayTest {

    private static final String SESION = "0192f0a1-7c00-7000-8000-00000000a001";
    private static final String RUTA = "/ingesta/v1/sesiones/" + SESION + "/particiones";
    private static final String RECHAZADO = "https://florlogic.co/problemas/gateway/rechazado";
    private static final String NO_DISPONIBLE = "https://florlogic.co/problemas/gateway/no-disponible";
    private static final String BLOQUEADO = "https://florlogic.co/problemas/gateway/bloqueado";
    private static final String TRAZA = "[0-9a-f]{32}";
    private static final String ATAQUE = "op-07' OR 1=1 --";
    private static final int PUERTO_NGINX = 8080;   // la imagen del WAF corre sin privilegios (WAF A-8)

    // Las pruebas corren desde backend-finca/: la configuración probada es la misma que despliega Compose.
    private static final Path DESPLIEGUE = Path.of("..", "despliegue");
    private static final JsonMapper JSON = JsonMapper.builder().build();

    @Container
    @ServiceConnection
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    private static GenericContainer<?> gateway;
    private static GenericContainer<?> sinBackend;
    private static GenericContainer<?> enDeteccion;
    private static String backendPorIp;

    @LocalServerPort
    int puerto;

    @Autowired
    JdbcTemplate jdbc;

    // HTTP/1.1, como lo que ve hoy el celular: el nginx.conf de la imagen del WAF trae http2 on, pero el cliente de
    // java.net.http cruza en HTTP/1.1 (comprobado), y así la prueba no depende de cómo se negocia h2c.
    private final HttpClient http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

    @BeforeEach
    void gatewayHaciaEsteBackendYBaseVacia() {
        if (gateway == null) {
            org.testcontainers.Testcontainers.exposeHostPorts(puerto);
            backendPorIp = ipDeHostTestcontainers() + ":" + puerto;
            GenericContainer<?> nuevo = gatewayHacia(backendPorIp, "On");
            nuevo.start();
            // Solo después de arrancar: si el arranque falla, el caso siguiente lo intenta otra vez con el error real.
            gateway = nuevo;
        }
        jdbc.update("DELETE FROM anotacion_recibida");
    }

    @AfterAll
    static void apagarGateways() {
        for (GenericContainer<?> nginx : new GenericContainer<?>[] {gateway, sinBackend, enDeteccion}) {
            if (nginx != null) {
                nginx.stop();
            }
        }
        gateway = null;
        sinBackend = null;
        enDeteccion = null;
        backendPorIp = null;
    }

    // ───────────── El canal de antes, ahora con el WAF en On (WAF C-1, C-3) ─────────────

    @Test
    void la_particion_dorada_pasa_por_el_gateway_y_responde_200() throws Exception {
        // Por IP, como llama el celular al nodo en la red de la finca: cero falsos positivos (WAF C-1).
        HttpResponse<String> r = post(basePorIp(gateway) + RUTA, dorada());

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
        // El WAF no juzga la forma del JSON: lo que no puede leer en UTF-8 simple llega al backend (WAF A-4).
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
        HttpResponse<String> r = post(base(gatewaySinBackend()) + RUTA, dorada());

        assertThat(r.statusCode()).isEqualTo(502);
        assertEsProblemaDelGateway(r, NO_DISPONIBLE, 502);
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

    // ───────────── Lo que agrega el WAF ─────────────

    @Test
    void un_ataque_es_403_bloqueado_y_no_llega_al_backend() throws Exception {
        HttpResponse<String> r = post(base(gateway) + RUTA, conAtaque());     // WAF C-2

        assertThat(r.statusCode()).isEqualTo(403);
        assertEsProblemaDelGateway(r, BLOQUEADO, 403);
        assertThat(filas()).isZero();
    }

    @Test
    void el_registro_del_waf_lleva_la_traza_y_la_regla_sin_authorization() throws Exception {
        String traza = "a1b2c3d4e5f60718293a4b5c6d7e8f90";
        String secreto = "secreto-que-no-va-al-registro";

        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gateway) + RUTA))   // WAF C-4
                .header("Content-Type", "application/json")
                .header("traceparent", "00-" + traza + "-00f067aa0ba902b7-01")
                .header("Authorization", "Bearer " + secreto)
                .POST(HttpRequest.BodyPublishers.ofString(conAtaque()))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(r.statusCode()).isEqualTo(403);
        // 942100: la inyección que vio CRS; 949110: la suma que bloqueó. jti-5b2e solo está en el cuerpo: si aparece,
        // el registro está guardando cuerpos (WAF A-6, OBS-1-8).
        assertThat(entradaDelRegistro(gateway, traza)).contains("\"942100\"").contains("\"949110\"")
                .doesNotContain("jti-5b2e");
        assertThat(gateway.getLogs()).doesNotContain(secreto);
    }

    @Test
    void una_particion_de_200_anotaciones_de_casi_1_mib_pasa() throws Exception {
        // El tope de 200 anotaciones del contrato, con una nota larga en cada una: el WAF no es más estricto que el
        // contrato y la revisa entera, más allá de los 128 KiB de la imagen (WAF C-5, A-5).
        String particion = JSON.writeValueAsString(casiUnMib());
        assertThat(particion.length()).isBetween(800_000, 1024 * 1024);

        HttpResponse<String> r = post(basePorIp(gateway) + RUTA, particion);

        assertThat(r.statusCode()).isEqualTo(200);
        assertThat(filas()).isEqualTo(200);
    }

    @Test
    void un_ataque_en_la_ultima_anotacion_de_casi_1_mib_es_403() throws Exception {
        // El ataque va al final de un cuerpo de casi 1 MiB: con los 128 KiB de la imagen pasaba sin revisar, y con
        // cualquier tope menor que el MiB también pasaría (WAF C-8, K-9, OBS-1-7).
        ObjectNode particion = casiUnMib();
        ObjectNode ultima = (ObjectNode) particion.get("anotaciones").get(199).get("datos");
        ultima.put("comentario", ATAQUE);
        int faltan = 1_048_000 - JSON.writeValueAsString(particion).length();
        ultima.put("nota", "x".repeat(4_000 + faltan));
        String cuerpo = JSON.writeValueAsString(particion);
        assertThat(cuerpo.length()).isBetween(1_047_000, 1024 * 1024);
        assertThat(cuerpo.lastIndexOf(ATAQUE)).isGreaterThan(1_047_000);

        HttpResponse<String> r = post(base(gateway) + RUTA, cuerpo);

        assertThat(r.statusCode()).isEqualTo(403);
        assertEsProblemaDelGateway(r, BLOQUEADO, 403);
        assertThat(filas()).isZero();
    }

    @Test
    void una_ruta_con_puntos_es_400_en_problem_json_y_no_una_respuesta_vacia() throws Exception {
        // Nginx la rechaza al leer la línea; con el WAF revisando su respuesta de error salía vacía (WAF C-6, K-7).
        HttpResponse<String> r = get(base(gateway) + "/../../etc/passwd");

        assertThat(r.statusCode()).isEqualTo(400);
        assertEsProblemaDelGateway(r, RECHAZADO, 400);
    }

    @Test
    void en_deteccion_el_ataque_llega_al_backend_y_queda_anotado() throws Exception {
        String traza = "0a0b0c0d0e0f10111213141516171819";

        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gatewayEnDeteccion()) + RUTA))  // WAF C-7
                .header("Content-Type", "application/json")
                .header("traceparent", "00-" + traza + "-00f067aa0ba902b7-01")
                .POST(HttpRequest.BodyPublishers.ofString(conAtaque()))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(r.statusCode()).isEqualTo(200);
        assertThat(filas()).isEqualTo(2);
        assertThat(entradaDelRegistro(enDeteccion, traza)).contains("\"942100\"");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("ataquesQueElWafNoPuedeLeer")
    void un_cuerpo_que_no_es_utf8_simple_es_400_y_no_llega_al_backend(String codificacion, byte[] cuerpo)
            throws Exception {
        // Jackson, en el backend, sí los leería: sin estas reglas el ataque se guardaba (WAF C-9, A-14, K-8).
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gateway) + RUTA))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray(cuerpo))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(r.statusCode()).as(codificacion).isEqualTo(400);
        assertEsProblemaDelGateway(r, RECHAZADO, 400);
        assertThat(filas()).isZero();
    }

    @Test
    void un_json_de_520_niveles_que_el_waf_no_lee_llega_al_backend_y_es_422() throws Exception {
        // Invariante de A-4: el WAF lee hasta 512 niveles y el backend hasta 500; lo que el WAF no lee, el backend lo
        // rechaza sin guardar nada, aunque traiga un ataque al final (WAF C-9).
        String profundo = "{\"contrato\":\"1.0\",\"x\":" + "[".repeat(520) + "1" + "]".repeat(520)
                + ",\"persona_id\":\"" + ATAQUE + "\"}";

        HttpResponse<String> r = post(base(gateway) + RUTA, profundo);

        assertThat(r.statusCode()).isEqualTo(422);
        assertThat(filas()).isZero();
    }

    @Test
    void mas_de_10000_campos_es_413_rechazado() throws Exception {
        // 200 anotaciones con una lista de 60 números en el valor: forma válida, unos 15.000 campos (WAF C-10, A-5).
        HttpResponse<String> r = post(base(gateway) + RUTA, JSON.writeValueAsString(conListas(1, 200)));

        assertThat(r.statusCode()).isEqualTo(413);
        assertEsProblemaDelGateway(r, RECHAZADO, 413);
        assertThat(filas()).isZero();
    }

    @Test
    void la_mitad_de_la_particion_de_demasiados_campos_pasa() throws Exception {
        // Lo que hace el celular ante el 413: partir; cada mitad queda por debajo del tope (WAF C-10).
        HttpResponse<String> r = post(base(gateway) + RUTA, JSON.writeValueAsString(conListas(1, 100)));

        assertThat(r.statusCode()).isEqualTo(200);
        assertThat(filas()).isEqualTo(100);
    }

    @Test
    void authorization_no_se_revisa_ni_va_al_registro() throws Exception {
        // La 941110 (XSS) revisa todas las cabeceras: sin A-15 este valor daría 403 (comprobado); con A-15 la
        // credencial la juzga el backend y nunca llega al registro (WAF C-11, OBS-1-3).
        String token = "eyJhbGciOiJFZERTQSJ9.e30.x<script>alert(1)</script>";

        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gateway) + RUTA))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(dorada()))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(r.statusCode()).isEqualTo(200);
        assertThat(gateway.getLogs()).doesNotContain("<script>alert(1)</script>");
    }

    @Test
    void con_host_por_ip_el_registro_no_trae_la_920350() throws Exception {
        // El 502 del backend apagado sí se anota, así que si la 920350 siguiera activa aparecería aquí (WAF C-12, A-11).
        String traza = "f0e1d2c3b4a5968778695a4b3c2d1e0f";

        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(basePorIp(gatewaySinBackend()) + RUTA))
                .header("Content-Type", "application/json")
                .header("traceparent", "00-" + traza + "-00f067aa0ba902b7-01")
                .POST(HttpRequest.BodyPublishers.ofString(dorada()))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(r.statusCode()).isEqualTo(502);
        assertThat(entradaDelRegistro(sinBackend, traza)).doesNotContain("920350");
    }

    @Test
    void un_cuerpo_en_text_plain_es_403_bloqueado() throws Exception {
        // El celular siempre manda JSON: otro Content-Type lo para CRS (920420) antes del backend (WAF C-13, A-16).
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gateway) + RUTA))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(dorada()))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(r.statusCode()).isEqualTo(403);
        assertEsProblemaDelGateway(r, BLOQUEADO, 403);
        assertThat(filas()).isZero();
    }

    @Test
    void un_cuerpo_sin_content_type_es_403_bloqueado() throws Exception {
        // java.net.http no pone Content-Type si no se le pide: lo para CRS (920340) (WAF C-13, A-16).
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gateway) + RUTA))
                .POST(HttpRequest.BodyPublishers.ofString(dorada()))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(r.statusCode()).isEqualTo(403);
        assertEsProblemaDelGateway(r, BLOQUEADO, 403);
        assertThat(filas()).isZero();
    }

    // ───────────── Ayudas ─────────────

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
     * El mismo entorno que el servicio gateway de compose.yaml, con otra dirección del backend y otro modo (A-4, WAF A-7):
     * los mismos dos archivos, montados en el mismo sitio que en Compose, para que lo probado sea lo desplegado.
     */
    private static GenericContainer<?> gatewayHacia(String backend, String modo) {
        Path gatewayDir = DESPLIEGUE.resolve("gateway");
        return new GenericContainer<>(DockerImageName.parse(imagenDeCompose()))
                .withCopyFileToContainer(MountableFile.forHostPath(gatewayDir.resolve("default.conf.template")),
                        "/etc/nginx/templates/conf.d/default.conf.template")
                .withCopyFileToContainer(MountableFile.forHostPath(gatewayDir.resolve("modsecurity-override.conf.template")),
                        "/etc/nginx/templates/modsecurity.d/modsecurity-override.conf.template")
                .withEnv("FLORLOGIC_BACKEND", backend)
                .withEnv("FLORLOGIC_DNS", "127.0.0.11")   // con una IP en FLORLOGIC_BACKEND, Nginx no le pregunta (A-18)
                .withEnv("FLORLOGIC_WAF_MODO", modo)
                .withExposedPorts(PUERTO_NGINX)
                .waitingFor(Wait.forHttp("/").forStatusCode(404));
    }

    /** Un segundo gateway hacia un puerto donde nadie escucha (C-4, WAF C-12); se arranca una vez. */
    private static GenericContainer<?> gatewaySinBackend() {
        if (sinBackend == null) {
            GenericContainer<?> nuevo = gatewayHacia("127.0.0.1:9", "On");
            nuevo.start();
            sinBackend = nuevo;
        }
        return sinBackend;
    }

    /** Un tercer gateway, hacia este backend, en DetectionOnly (WAF C-7); se arranca una vez. */
    private GenericContainer<?> gatewayEnDeteccion() {
        if (enDeteccion == null) {
            GenericContainer<?> nuevo = gatewayHacia(backendPorIp, "DetectionOnly");
            nuevo.start();
            enDeteccion = nuevo;
        }
        return enDeteccion;
    }

    /**
     * La IP de {@code host.testcontainers.internal}: la del contenedor de reenvío por el que Testcontainers lleva al
     * puerto expuesto del anfitrión, no la del anfitrión (ESPEC-esqueleto-tx1 A-18, A-21). Se lee del
     * {@code /etc/hosts} de un contenedor auxiliar con acceso al anfitrión, con la misma imagen del gateway.
     */
    private static String ipDeHostTestcontainers() {
        try (GenericContainer<?> auxiliar = new GenericContainer<>(DockerImageName.parse(imagenDeCompose()))
                .withAccessToHost(true)
                .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("cat").withCmd("/etc/hosts"))
                .withStartupCheckStrategy(new OneShotStartupCheckStrategy())) {
            auxiliar.start();
            return auxiliar.getLogs().lines()
                    .map(linea -> linea.strip().split("\\s+"))
                    .filter(campos -> campos.length > 1 && campos[1].equals("host.testcontainers.internal"))
                    .map(campos -> campos[0])
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("sin host.testcontainers.internal en /etc/hosts"));
        }
    }

    /** La imagen del servicio gateway de compose.yaml: así la prueba nunca corre otra versión del WAF (WAF A-12). */
    private static String imagenDeCompose() {
        try {
            return Files.readAllLines(DESPLIEGUE.resolve("compose.yaml")).stream()
                    .map(String::strip)
                    .filter(linea -> linea.startsWith("image: owasp/modsecurity-crs:"))
                    .map(linea -> linea.substring("image: ".length()).split("\\s")[0])
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("compose.yaml no declara la imagen del WAF"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Espera, sin sleep, a que el WAF escriba en stdout su entrada de auditoría con esa traza, y la devuelve
     * (WAF A-6; PB-10). La escribe al terminar la petición, así que puede llegar después de la respuesta.
     */
    private static String entradaDelRegistro(GenericContainer<?> nginx, String traza) throws TimeoutException {
        String marca = "\"unique_id\":\"" + traza + "\"";
        WaitingConsumer espera = new WaitingConsumer();
        nginx.followOutput(espera, OutputFrame.OutputType.STDOUT);
        espera.waitUntil(linea -> linea.getUtf8String().contains(marca), 10, TimeUnit.SECONDS);
        return nginx.getLogs(OutputFrame.OutputType.STDOUT).lines()
                .filter(linea -> linea.contains(marca))
                .findFirst()
                .orElseThrow();
    }

    private static String base(GenericContainer<?> nginx) {
        return "http://" + nginx.getHost() + ":" + nginx.getMappedPort(PUERTO_NGINX);
    }

    /** Por IP, como llama el celular al nodo en la red de la finca: el Host es una IP (WAF A-11). */
    private static String basePorIp(GenericContainer<?> nginx) throws UnknownHostException {
        String ip = InetAddress.getByName(nginx.getHost()).getHostAddress();
        return "http://" + (ip.contains(":") ? "[" + ip + "]" : ip) + ":" + nginx.getMappedPort(PUERTO_NGINX);
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
        return JSON.readTree(r.body());
    }

    private static String dorada() throws IOException {
        try (var in = CanalPorGatewayTest.class.getResourceAsStream("/canal/peticion-particion.json")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    /** La partición dorada con un ataque evidente en un campo de texto (WAF C-2). */
    private static String conAtaque() throws IOException {
        ObjectNode particion = (ObjectNode) JSON.readTree(dorada());
        ((ObjectNode) particion.get("sesiones_captura").get(0)).put("persona_id", ATAQUE);
        return JSON.writeValueAsString(particion);
    }

    /** El ataque de WAF C-2 en tres codificaciones que el backend lee y el WAF no (WAF C-9). */
    static Stream<Arguments> ataquesQueElWafNoPuedeLeer() throws IOException {
        String ataque = conAtaque();
        ByteArrayOutputStream conBom = new ByteArrayOutputStream();
        conBom.writeBytes(new byte[] {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        conBom.writeBytes(ataque.getBytes(StandardCharsets.UTF_8));
        return Stream.of(
                Arguments.of("UTF-16 con BOM", ataque.getBytes(StandardCharsets.UTF_16)),
                Arguments.of("UTF-16LE sin BOM", ataque.getBytes(StandardCharsets.UTF_16LE)),
                Arguments.of("UTF-8 con BOM", conBom.toByteArray()));
    }

    /** 200 anotaciones (max_anotaciones_particion) con una nota de 4.000 letras en cada una: casi 1 MiB (WAF C-5). */
    private static ObjectNode casiUnMib() throws IOException {
        return particionDe(1, 200, datos -> datos.put("nota", "x".repeat(4_000)));
    }

    /** Anotaciones con una lista de 60 números en el valor: muchos campos en poco tamaño (WAF C-10). */
    private static ObjectNode conListas(int desde, int hasta) throws IOException {
        return particionDe(desde, hasta, datos -> {
            ArrayNode valor = datos.putArray("valor");
            for (int numero = 0; numero < 60; numero++) {
                valor.add(numero);
            }
        });
    }

    /**
     * La partición dorada con las anotaciones {@code desde}..{@code hasta}, copias de su primera anotación con su propio
     * uuid y consecutivo, y los datos que pida cada caso.
     */
    private static ObjectNode particionDe(int desde, int hasta, Consumer<ObjectNode> datos) throws IOException {
        ObjectNode particion = (ObjectNode) JSON.readTree(dorada());
        JsonNode modelo = particion.get("anotaciones").get(0);
        ArrayNode anotaciones = particion.putArray("anotaciones");
        for (int consecutivo = desde; consecutivo <= hasta; consecutivo++) {
            ObjectNode anotacion = (ObjectNode) modelo.deepCopy();
            anotacion.put("anotacion_uuid", "0192f0a0-2000-7000-8000-%012d".formatted(consecutivo));
            anotacion.put("consecutivo", consecutivo);
            datos.accept((ObjectNode) anotacion.get("datos"));
            anotaciones.add(anotacion);
        }
        return particion;
    }

    private int filas() {
        return jdbc.queryForObject("SELECT count(*) FROM anotacion_recibida", Integer.class);
    }
}
