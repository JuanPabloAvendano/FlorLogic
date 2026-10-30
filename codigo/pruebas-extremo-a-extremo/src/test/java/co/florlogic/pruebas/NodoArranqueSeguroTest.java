package co.florlogic.pruebas;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.startupcheck.OneShotStartupCheckStrategy;
import org.testcontainers.images.builder.Transferable;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Los casos negativos del arranque del nodo (A-33), sobre un solo {@code compose.yaml} real: lo que pasa cuando
 * {@code openbao-init} vuelve a correr, cuando le falta «listo» o la subred guardada no es la del nodo (A-40, A-41),
 * cuando la clave del anfitrión no es la del vault, cuando el vault no está y cuando la imagen de OpenBao arranca sin
 * la bandera de SoftHSM. El camino feliz es {@link NodoCompletoTest}. Los A-n y
 * C-n son de {@code despliegue/espec/ESPEC-esqueleto-tx1.md}.
 *
 * <p>Los casos cambian el nodo y cada uno lo deja como lo encontró, por eso corren en orden. Usan la orden
 * {@code docker compose} del anfitrión, como el operador. Se salta sin Docker; sin Docker no prueba nada contra
 * PostgreSQL ni OpenBao.
 */
@Testcontainers(disabledWithoutDocker = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NodoArranqueSeguroTest {

    private static final String SECRET_ID = "/bao/spring.cloud.vault.app-role.secret-id";
    private static final String RUTA = "/ingesta/v1/sesiones/0192f0a1-7c00-7000-8000-00000000a001/particiones";
    private static final Path DORADA = Path.of("..", "backend-finca", "src", "test", "resources", "canal",
            "peticion-particion.json");
    private static final Duration ESPERA = Duration.ofMinutes(2);

    private static NodoCompose nodo;

    private final HttpClient http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(5)).build();

    @BeforeAll
    static void levantarElNodo() {
        nodo = new NodoCompose("172.31.202.0/24").arrancar();   // otra subred que NodoCompletoTest (A-42)
    }

    @AfterAll
    static void borrarElNodo() {
        if (nodo != null) {
            nodo.close();
        }
    }

    @Test
    @Order(1)
    void c3_openbao_init_otra_vez_comprueba_y_no_cambia_nada() {
        String antes = secretIdDelBackend();

        NodoCompose.Corrida r = nodo.compose("run", "--rm", "--no-deps", "-T", "openbao-init");

        assertThat(r.codigo()).as(r.salida()).isZero();
        assertThat(r.salida()).contains("nada que cambiar");
        // Un solo secret_id vivo, el mismo de antes (A-42): la corrida siguiente no emite otro.
        assertThat(secretIdDelBackend()).isEqualTo(antes);
    }

    @Test
    @Order(2)
    void a40_sin_listo_pero_con_un_approle_que_entra_lo_reconoce_y_reescribe_listo() {
        // El corte cayó entre revocar el raíz y escribir «listo»: la corrida siguiente lo reconoce por el AppRole.
        NodoCompose.Corrida r = openbaoInit("rm /bao/estado/listo && init.sh");

        assertThat(r.codigo()).as(r.salida()).isZero();
        assertThat(r.salida()).contains("se reescribió «listo»", "nada que cambiar");
        assertThat(openbaoInit("init.sh").codigo()).isZero();   // y queda como antes
    }

    @Test
    @Order(3)
    void a41_si_la_subred_guardada_no_es_la_del_nodo_para_antes_de_entrar() {
        // Lo mismo que vería openbao-init si FLORLOGIC_RED cambiara: la guardada y la actual no coinciden.
        NodoCompose.Corrida r = openbaoInit(
                "cp /bao/estado/red /tmp/red && printf 10.99.0.0/24 > /bao/estado/red && init.sh; codigo=$?;"
                        + " cp /tmp/red /bao/estado/red; exit $codigo");

        assertThat(r.codigo()).as(r.salida()).isNotZero();
        assertThat(r.salida()).contains("la subred del nodo cambió de 10.99.0.0/24", "generate-root");
        assertThat(openbaoInit("init.sh").codigo()).isZero();
    }

    @Test
    @Order(4)
    void c4_otra_clave_en_el_anfitrion_para_el_arranque_y_no_reescribe_nada() throws Exception {
        nodo.compose("stop", "backend");

        NodoCompose.Corrida r = nodo.compose(Map.of("FLORLOGIC_PG_ADMIN", "otra-clave-que-no-es-la-del-vault"),
                "up", "-d", "backend");

        // openbao-init sale ≠0 y Compose no arranca el backend que depende de él (A-25, A-23).
        assertThat(r.codigo()).as(r.salida()).isNotZero();
        assertThat(r.salida()).contains("openbao-init");
        assertThat(nodo.compose("logs", "openbao-init").salida()).contains("no se reescribe nada", "generate-root");
        assertThat(serviciosCorriendo()).doesNotContain("backend");
        // No reescribió nada: la base sigue con la clave de antes.
        try (var conexion = DriverManager.getConnection(urlJdbcBase(), "florlogic", nodo.clavePg())) {
            assertThat(conexion.isValid(5)).isTrue();
        }

        volverAlNodoSano();
    }

    @Test
    @Order(5)
    void c5_sin_vault_el_backend_reiniciado_no_queda_escuchando() throws Exception {
        assertThat(nodo.compose("stop", "openbao").codigo()).isZero();
        // restart no espera los depends_on: el backend va directo contra un vault que no está (COH-27).
        assertThat(nodo.compose("restart", "backend").codigo()).isZero();

        // Spring Cloud Vault con fail-fast lo para, y restart: unless-stopped lo vuelve a intentar (A-23).
        Instant limite = Instant.now().plus(ESPERA);
        while (reiniciosDelBackend() < 1 && Instant.now().isBefore(limite)) {
            Thread.sleep(2_000);
        }
        assertThat(reiniciosDelBackend()).as("el backend tendría que haber parado al menos una vez").isPositive();
        // Nadie atiende detrás del gateway: 502 (A-17).
        assertThat(mandarLaDorada().statusCode()).isEqualTo(502);

        assertThat(nodo.compose("start", "openbao").codigo()).isZero();
        volverAlNodoSano();
    }

    @Test
    @Order(6)
    void a29_sin_la_bandera_la_imagen_de_openbao_no_crea_llave_en_un_volumen_vacio() {
        String imagen = nodo.servicio("openbao").getContainerInfo().getConfig().getImage();
        // Sin volúmenes: el almacenamiento y los tokens están vacíos, como en una finca nueva. arranque.sh corre con un
        // tope (sin token, el servidor no abre) y después se miran los tokens que quedaron.
        try (GenericContainer<?> openbao = new GenericContainer<>(DockerImageName.parse(imagen))
                .withEnv("BAO_HSM_LIB", NodoCompose.BIBLIOTECA_SOFTHSM)
                .withCopyToContainer(Transferable.of(nodo.variable("FLORLOGIC_HSM_PIN")), "/run/secrets/hsm-pin")
                .withCreateContainerCmdModifier(cmd -> cmd.withEntrypoint("sh", "-c",
                        "timeout 30 arranque.sh; echo '--- tokens ---'; softhsm2-util --show-slots"))
                .withStartupCheckStrategy(new OneShotStartupCheckStrategy().withTimeout(Duration.ofSeconds(90)))) {
            try {
                openbao.start();
            } catch (RuntimeException esperado) {
                // La salida final es la de softhsm2-util; lo que importa está en el registro.
            }
            String registro = openbao.getLogs();
            assertThat(registro).contains("--- tokens ---");
            assertThat(registro).doesNotContain("creados");
            assertThat(registro.substring(registro.indexOf("--- tokens ---"))).doesNotContainPattern("Label: *florlogic");
        }
    }

    // ── apoyo ───────────────────────────────────────────────────────────────────────────────────────────────────

    private void volverAlNodoSano() throws Exception {
        NodoCompose.Corrida r = nodo.compose("up", "-d", "--wait");
        assertThat(r.codigo()).as(r.salida()).isZero();
        assertThat(mandarLaDorada().statusCode()).isEqualTo(200);
    }

    private HttpResponse<String> mandarLaDorada() throws Exception {
        URI destino = URI.create("http://127.0.0.1:" + nodo.puertoActual("gateway", NodoCompose.PUERTO_GATEWAY) + RUTA);
        return http.send(HttpRequest.newBuilder(destino)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofFile(DORADA))
                .build(), HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Corre una orden de shell en un openbao-init nuevo, sobre los volúmenes del nodo: así se prepara el estado que
     * dejaría un corte o un cambio (A-36, A-40, A-41) antes de llamar a {@code init.sh}, sin tocar el vault.
     */
    private NodoCompose.Corrida openbaoInit(String ordenDeShell) {
        return nodo.compose("run", "--rm", "--no-deps", "-T", "--entrypoint", "sh", "openbao-init", "-c", ordenDeShell);
    }

    private String secretIdDelBackend() {
        NodoCompose.Corrida r = nodo.compose("exec", "-T", "backend", "cat", SECRET_ID);
        assertThat(r.codigo()).as(r.salida()).isZero();
        return r.salida();
    }

    private List<String> serviciosCorriendo() {
        return nodo.compose("ps", "--status", "running", "--services").salida().lines().toList();
    }

    private int reiniciosDelBackend() {
        String id = nodo.compose("ps", "-q", "backend").salida().strip();
        Integer reinicios = DockerClientFactory.instance().client().inspectContainerCmd(id).exec().getRestartCount();
        return reinicios == null ? 0 : reinicios;
    }

    private String urlJdbcBase() {
        return "jdbc:postgresql://127.0.0.1:" + nodo.puertoActual("base", NodoCompose.PUERTO_BASE) + "/florlogic";
    }
}
