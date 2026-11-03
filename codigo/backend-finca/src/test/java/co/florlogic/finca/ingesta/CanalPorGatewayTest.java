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
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import co.florlogic.finca.EmisorDePrueba;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.containers.output.WaitingConsumer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.DockerClientFactory;
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
 *
 * <p>Solo HTTPS (A-44): la prueba hace su propia raíz con {@code despliegue/pki/Ceremonia.java}, el mismo programa de
 * la finca, y con ella firma el certificado del gateway y el del backend. El backend de la prueba exige el certificado
 * del gateway (mTLS, A-48) y el cliente de la prueba solo confía en esa raíz (COH-47).
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
    private static final int PUERTO_NGINX = 8443;   // solo HTTPS; la imagen del WAF corre sin privilegios (WAF A-8)

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

    // La raíz de esta prueba y los certificados que firma; se hacen una vez, antes de arrancar Spring (A-44).
    private static Path pki;

    @DynamicPropertySource
    static void confiaEnElEmisorDePrueba(DynamicPropertyRegistry propiedades) {
        EmisorDePrueba.registrar(propiedades);   // CT-2: los tokens de EmisorDePrueba
        // El backend, como en Compose: HTTPS con mTLS, solo entra quien presenta un certificado de la finca (A-48).
        Path certificados = certificados();
        propiedades.add("spring.ssl.bundle.pem.servidor.keystore.certificate", () -> "file:" + certificados.resolve("backend.crt"));
        propiedades.add("spring.ssl.bundle.pem.servidor.keystore.private-key", () -> "file:" + certificados.resolve("backend.key"));
        propiedades.add("spring.ssl.bundle.pem.servidor.truststore.certificate", () -> "file:" + certificados.resolve("raiz.crt"));
        propiedades.add("server.ssl.bundle", () -> "servidor");
        propiedades.add("server.ssl.client-auth", () -> "need");
    }

    @Autowired
    JdbcTemplate jdbc;

    // HTTP/1.1, como lo que ve hoy el celular: el nginx.conf de la imagen del WAF trae http2 on, pero así la prueba no
    // depende de cómo se negocia h2. Solo confía en la raíz de la prueba: nunca se desactiva la verificación (A-51 b).
    private final HttpClient http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
            .sslContext(confianzaEnLaRaiz()).build();

    @BeforeEach
    void gatewayHaciaEsteBackendYBaseVacia() {
        if (gateway == null) {
            org.testcontainers.Testcontainers.exposeHostPorts(puerto);
            backendPorIp = ipDeHostTestcontainers() + ":" + puerto;
            GenericContainer<?> nuevo = gatewayHacia(backendPorIp, "On");
            nuevo.start();
            calentar(nuevo);
            // Solo después de arrancar: si el arranque falla, el caso siguiente lo intenta otra vez con el error real.
            gateway = nuevo;
        }
        jdbc.update("DELETE FROM anotacion_recibida");
        jdbc.update("DELETE FROM anotacion_rechazada");
        jdbc.update("DELETE FROM avance_dispositivo");
        jdbc.update("DELETE FROM sesion_sincronizacion");
        // Una sesión ya abierta (CT-1): estas pruebas son del canal de particiones; abrir y cerrar tienen la suya.
        jdbc.update("INSERT INTO sesion_sincronizacion VALUES (?::uuid, ?::uuid, ?, now(), 0, 12, true, 'ABIERTA', null)",
                SESION, "0192f0a0-0000-7000-8000-0000000000e1", EmisorDePrueba.PERSONA);
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
        if (pki != null) {
            try (var archivos = Files.walk(pki)) {
                archivos.sorted(java.util.Comparator.reverseOrder()).forEach(archivo -> archivo.toFile().delete());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
            pki = null;
        }
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
                    .header("Authorization", "Bearer " + EmisorDePrueba.operador())
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
                .header("Authorization", "Bearer " + EmisorDePrueba.operador())
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
                .header("Authorization", "Bearer " + EmisorDePrueba.operador())
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
    void sin_token_el_backend_responde_401_por_el_gateway() throws Exception {
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gateway) + RUTA))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(dorada()))
                .build(), HttpResponse.BodyHandlers.ofString());

        assertThat(r.statusCode()).isEqualTo(401);
        assertThat(r.headers().firstValue("WWW-Authenticate")).hasValue("Bearer");
        assertThat(r.body()).contains("https://florlogic.co/problemas/ingesta/credencial-invalida");
        assertThat(filas()).isZero();
    }

    @ParameterizedTest
    @MethodSource("rutasDeIdentidadQueNoPasan")
    void del_proveedor_de_identidad_solo_pasa_el_realm_florlogic(String ruta) throws Exception {
        // CT-2: el realm master y la consola de administración se quedan en el 404 del gateway; nunca llegan a Keycloak.
        HttpResponse<String> r = get(base(gateway) + ruta);

        assertEsProblemaDelGateway(r, RECHAZADO, 404);
    }

    static Stream<String> rutasDeIdentidadQueNoPasan() {
        return Stream.of("/auth/", "/auth/admin/master/console/", "/auth/realms/master/protocol/openid-connect/certs",
                "/auth/admin/realms/florlogic/users", "/auth/realms/florlogica/", "/auth/realms/florlogic");
    }

    @Test
    void el_realm_florlogic_va_a_keycloak() throws Exception {
        // Esta prueba no levanta Keycloak (FLORLOGIC_IDENTIDAD apunta a un puerto cerrado): 502 dice que el gateway sí
        // lo mandó allá, en vez de dejarlo en el 404. El de verdad lo prueban las de extremo a extremo.
        HttpResponse<String> r = get(base(gateway) + "/auth/realms/florlogic/protocol/openid-connect/certs");

        assertEsProblemaDelGateway(r, NO_DISPONIBLE, 502);
    }

    @Test
    void la_web_llega_al_backend_y_vuelve_al_mismo_host_y_puerto() throws Exception {
        // F4: sin sesión, el backend manda a iniciarla; la redirect_uri que va a Keycloak lleva el host y el puerto que
        // usó el navegador (X-Forwarded-Host del gateway), no los del contenedor del backend.
        HttpResponse<String> r = get(base(gateway) + "/web/tablero");
        assertThat(r.statusCode()).isEqualTo(302);
        assertThat(r.headers().firstValue("Location")).hasValueSatisfying(
                l -> assertThat(l).endsWith("/web/oauth2/authorization/keycloak"));

        HttpResponse<String> inicio = get(base(gateway) + "/web/oauth2/authorization/keycloak");
        assertThat(inicio.statusCode()).isEqualTo(302);
        assertThat(inicio.headers().firstValue("Location")).hasValueSatisfying(l -> assertThat(l)
                .contains("/auth/realms/florlogic/protocol/openid-connect/auth?", "client_id=web")
                .contains("redirect_uri=" + base(gateway) + "/web/login/oauth2/code/keycloak"));
    }

    @Test
    void en_la_web_un_metodo_ajeno_es_405_con_get_y_post() throws Exception {
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gateway) + "/web/tablero"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());

        assertEsProblemaDelGateway(r, RECHAZADO, 405);
        assertThat(r.headers().firstValue("Allow")).hasValue("GET, POST");
    }

    @Test
    void la_app_del_celular_llega_al_backend_y_vuelve_al_mismo_host_y_puerto() throws Exception {
        // F8: /app sin barra vuelve a /app/ con el host y el puerto que usó el celular; la app se baja sin token, con la
        // CSP del backend.
        HttpResponse<String> r = get(base(gateway) + "/app");
        assertThat(r.statusCode()).isEqualTo(302);
        assertThat(r.headers().firstValue("Location")).hasValueSatisfying(
                l -> assertThat(l).isIn("/app/", base(gateway) + "/app/"));

        HttpResponse<String> index = get(base(gateway) + "/app/");
        assertThat(index.statusCode()).isEqualTo(200);
        assertThat(index.body()).contains("js/main.js");
        assertThat(index.headers().firstValue("Content-Security-Policy")).hasValueSatisfying(
                c -> assertThat(c).contains("connect-src 'self'"));
        assertThat(get(base(gateway) + "/app/sw.js").statusCode()).isEqualTo(200);
    }

    @Test
    void en_la_app_un_metodo_ajeno_es_405_con_get() throws Exception {
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base(gateway) + "/app/"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());

        assertEsProblemaDelGateway(r, RECHAZADO, 405);
        assertThat(r.headers().firstValue("Allow")).hasValue("GET");
    }

    @Test
    void en_identidad_un_metodo_ajeno_es_405_con_get_y_post() throws Exception {
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(
                        URI.create(base(gateway) + "/auth/realms/florlogic/protocol/openid-connect/token"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofString());

        // OPTIONS y no DELETE: CRS (911100) frena DELETE con su 403 antes de que Nginx responda 405.
        assertEsProblemaDelGateway(r, RECHAZADO, 405);
        assertThat(r.headers().firstValue("Allow")).hasValue("GET, POST");
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

        // El WAF la deja pasar y el backend la juzga: no es un token suyo, así que 401 (CT-2), no el 403 del WAF.
        assertThat(r.statusCode()).isEqualTo(401);
        assertThat(r.body()).contains("https://florlogic.co/problemas/ingesta/credencial-invalida");
        assertThat(gateway.getLogs()).doesNotContain("<script>alert(1)</script>");
    }

    @Test
    void con_host_por_ip_el_registro_no_trae_la_920350() throws Exception {
        // El 502 del backend apagado sí se anota, así que si la 920350 siguiera activa aparecería aquí (WAF C-12, A-11).
        String traza = "f0e1d2c3b4a5968778695a4b3c2d1e0f";

        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(basePorIp(gatewaySinBackend()) + RUTA))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + EmisorDePrueba.operador())
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
        Path certificados = certificados();
        return new GenericContainer<>(DockerImageName.parse(imagenDeCompose()))
                // Los certificados en /tls, como los deja openbao-init en el volumen del gateway (A-47). 0444: la
                // imagen corre como nginx (101) y la copia queda de root.
                .withCopyFileToContainer(MountableFile.forHostPath(certificados.resolve("gateway.crt"), 0444), "/tls/servidor.crt")
                .withCopyFileToContainer(MountableFile.forHostPath(certificados.resolve("gateway.key"), 0444), "/tls/servidor.key")
                .withCopyFileToContainer(MountableFile.forHostPath(certificados.resolve("raiz.crt"), 0444), "/tls/raiz.crt")
                .withCopyFileToContainer(MountableFile.forHostPath(gatewayDir.resolve("default.conf.template")),
                        "/etc/nginx/templates/conf.d/default.conf.template")
                .withCopyFileToContainer(MountableFile.forHostPath(gatewayDir.resolve("modsecurity-override.conf.template")),
                        "/etc/nginx/templates/modsecurity.d/modsecurity-override.conf.template")
                .withEnv("FLORLOGIC_BACKEND", backend)
                .withEnv("FLORLOGIC_DNS", "127.0.0.11")   // con una IP en FLORLOGIC_BACKEND, Nginx no le pregunta (A-18)
                .withEnv("FLORLOGIC_WAF_MODO", modo)
                .withEnv("FLORLOGIC_IDENTIDAD", "127.0.0.1:9")   // sin Keycloak en esta prueba: /auth/ da 502 (CT-2)
                .withExposedPorts(PUERTO_NGINX)
                .waitingFor(Wait.forListeningPort());
    }

    /**
     * La ceremonia de la prueba (A-45, A-46 sin intermedia: la raíz firma directo): la raíz, el certificado del backend
     * (nombre «backend», el que verifica el gateway, COH-47) y el del gateway, con los nombres por los que lo llama la
     * prueba. Una sola vez por corrida.
     */
    private static synchronized Path certificados() {
        if (pki != null) {
            return pki.resolve("nodo");
        }
        try {
            Path carpeta = Files.createTempDirectory("florlogic-pki");
            byte[] azar = new byte[16];
            new SecureRandom().nextBytes(azar);
            String clave = HexFormat.of().formatHex(azar);
            String custodia = carpeta.resolve("custodia").toString();
            String nodo = carpeta.resolve("nodo").toString();
            String anfitrion = InetAddress.getByName(DockerClientFactory.instance().dockerHostIpAddress()).getHostAddress();
            ceremonia(clave, "raiz", custodia, nodo);
            ceremonia(clave, "hoja", custodia, nodo, "backend", "backend,localhost,127.0.0.1", "1");
            ceremonia(clave, "hoja", custodia, nodo, "gateway", "localhost,127.0.0.1," + anfitrion, "1");
            pki = carpeta;
            return pki.resolve("nodo");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void ceremonia(String clave, String... argumentos) throws IOException {
        List<String> linea = new ArrayList<>(List.of(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                DESPLIEGUE.resolve("pki").resolve("Ceremonia.java").toString()));
        linea.addAll(List.of(argumentos));
        ProcessBuilder proceso = new ProcessBuilder(linea).redirectErrorStream(true);
        proceso.environment().put("FLORLOGIC_RAIZ_CLAVE", clave);
        Process corriendo = proceso.start();
        String salida = new String(corriendo.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        try {
            if (corriendo.waitFor() != 0) {
                throw new IllegalStateException("la ceremonia " + argumentos[0] + " falló: " + salida);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    /** El contexto TLS del cliente de la prueba: solo la raíz de la prueba. */
    private static SSLContext confianzaEnLaRaiz() {
        try (var entrada = Files.newInputStream(certificados().resolve("raiz.crt"))) {
            KeyStore confianza = KeyStore.getInstance(KeyStore.getDefaultType());
            confianza.load(null, null);
            confianza.setCertificateEntry("raiz", CertificateFactory.getInstance("X.509").generateCertificate(entrada));
            TrustManagerFactory gestores = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            gestores.init(confianza);
            SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, gestores.getTrustManagers(), null);
            return tls;
        } catch (Exception e) {
            throw new IllegalStateException("sin la raíz de la prueba no hay HTTPS", e);
        }
    }

    /**
     * El primer apretón de manos TLS de una JVM recién arrancada, por el túnel de Testcontainers, pasó de los 5 s de
     * proxy_connect_timeout (504, A-58). Una petición que llega al backend y no guarda nada (422) lo deja hecho antes de
     * los casos; en la finca, el celular reintenta.
     */
    private static void calentar(GenericContainer<?> nginx) {
        try {
            HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).sslContext(confianzaEnLaRaiz()).build()
                    .send(HttpRequest.newBuilder(URI.create(base(nginx) + RUTA))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString("{}"))
                            .build(), HttpResponse.BodyHandlers.discarding());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
        return "https://" + nginx.getHost() + ":" + nginx.getMappedPort(PUERTO_NGINX);
    }

    /** Por IP, como llama el celular al nodo en la red de la finca: el Host es una IP (WAF A-11). */
    private static String basePorIp(GenericContainer<?> nginx) throws UnknownHostException {
        String ip = InetAddress.getByName(nginx.getHost()).getHostAddress();
        return "https://" + (ip.contains(":") ? "[" + ip + "]" : ip) + ":" + nginx.getMappedPort(PUERTO_NGINX);
    }

    private HttpResponse<String> post(String url, String cuerpo) throws IOException, InterruptedException {
        return http.send(HttpRequest.newBuilder(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + EmisorDePrueba.operador())
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
 * - @BeforeEach: corre antes de cada caso; el primero arranca el gateway (no puede ser @Container porque necesita
 *   el puerto del backend) y todos limpian la base.
 * - @AfterAll: corre una vez al final; apaga los gateways y borra la carpeta de certificados (Files.walk ordenado
 *   al revés borra primero los archivos y después las carpetas).
 * - spring.ssl.bundle.pem.servidor.keystore.certificate / private-key / truststore.certificate: el certificado y la
 *   llave PEM del backend y la raíz en la que confía, en un paquete SSL llamado servidor.
 * - server.ssl.bundle = servidor y server.ssl.client-auth = need: el backend sirve HTTPS con ese paquete y exige
 *   certificado de cliente (mTLS); solo entra el gateway.
 * - HttpClient.newBuilder().version(HTTP_1_1).sslContext(...): cliente HTTP/1.1 que solo confía en la raíz de la
 *   prueba.
 * - KeyStore + CertificateFactory.getInstance("X.509") + TrustManagerFactory + SSLContext.getInstance("TLS"): arman
 *   a mano ese almacén de confianza con un único certificado, raiz.crt.
 * - org.testcontainers.Testcontainers.exposeHostPorts(puerto): deja que los contenedores lleguen al puerto del
 *   backend que corre en el anfitrión, por host.testcontainers.internal.
 * - GenericContainer + withCopyFileToContainer(MountableFile.forHostPath(ruta, 0444), destino): copia al contenedor
 *   los certificados y las plantillas de despliegue; 0444 (octal) es solo lectura para todos.
 * - withEnv("FLORLOGIC_BACKEND" / "FLORLOGIC_DNS" / "FLORLOGIC_WAF_MODO" / "FLORLOGIC_IDENTIDAD", …): las variables
 *   que la plantilla de Nginx usa. 127.0.0.11 es el DNS interno de Docker; 127.0.0.1:9 es un puerto donde nadie
 *   escucha.
 * - withExposedPorts(8443) + Wait.forListeningPort() + getMappedPort(8443): publica el puerto HTTPS del contenedor,
 *   espera a que escuche y averigua en qué puerto del anfitrión quedó.
 * - "On" y "DetectionOnly": modos del motor de ModSecurity; On bloquea, DetectionOnly solo anota en el registro.
 * - withAccessToHost(true) + withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("cat").withCmd("/etc/hosts"))
 *   + OneShotStartupCheckStrategy: un contenedor de un solo uso que imprime su /etc/hosts para leer la IP de
 *   host.testcontainers.internal.
 * - DockerClientFactory.instance().dockerHostIpAddress(): la dirección del anfitrión de Docker; entra en los
 *   nombres del certificado del gateway.
 * - ProcessBuilder + redirectErrorStream(true) + environment().put("FLORLOGIC_RAIZ_CLAVE", clave): ejecuta
 *   Ceremonia.java con el java del mismo JDK (System.getProperty("java.home")), juntando salida y errores.
 * - SecureRandom + HexFormat.of().formatHex(...): una clave de raíz al azar, en hexadecimal, solo para esta
 *   corrida.
 * - WaitingConsumer + followOutput + waitUntil(..., 10, SECONDS): espera, sin dormir, a que la línea de auditoría
 *   del WAF con esa traza aparezca en la salida del contenedor.
 * - traceparent: 00-<traza de 32 hex>-<tramo de 16 hex>-01: cabecera W3C Trace Context; el gateway saca de ahí el
 *   traceId del error.
 * - expectContinue(true): manda Expect: 100-continue; Nginx puede rechazar por Content-Length antes de recibir el
 *   cuerpo.
 * - @ParameterizedTest + @MethodSource("…") + Arguments.of(...): el mismo caso con varias entradas que da un método
 *   estático que devuelve Stream.
 * - StandardCharsets.UTF_16 (Java le pone BOM), UTF_16LE (sin BOM) y los bytes EF BB BF (BOM de UTF-8):
 *   codificaciones que el WAF no lee.
 * - Reglas de OWASP CRS nombradas: 942100 detecta inyección SQL, 949110 bloquea por puntaje de anomalía, 920350
 *   avisa Host numérico, 920420 tipo de contenido no permitido, 920340 cuerpo sin Content-Type, 941110 XSS con
 *   etiqueta script, 911100 método no permitido.
 * - Server: nginx (sin versión), Allow, WWW-Authenticate: Bearer, Location: cabeceras que cada caso revisa.
 * - [ip] con corchetes en basePorIp: una IPv6 dentro de una URL va entre corchetes.
 * - Consumer<ObjectNode> datos: función que recibe el objeto datos de cada anotación y lo modifica.
 */
