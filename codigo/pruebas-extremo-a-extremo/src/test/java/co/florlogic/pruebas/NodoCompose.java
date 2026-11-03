package co.florlogic.pruebas;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.ContainerState;
import org.testcontainers.containers.wait.strategy.Wait;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;

/**
 * El nodo de la finca tal como lo despliega {@code despliegue/compose.yaml}, levantado por una prueba con
 * Testcontainers: construye las imágenes, espera a que el backend y el gateway estén sanos y al terminar lo borra todo
 * (contenedores, volúmenes e imágenes). Los A-n y C-n son de {@code despliegue/espec/ESPEC-esqueleto-tx1.md}.
 *
 * <p>Cada prueba trae su propia subred y Compose le da un nombre de proyecto propio, así que no choca con el Compose
 * de desarrollo ni con otra prueba; los puertos del anfitrión van en 0 y Docker elige uno libre (A-20, A-42). El
 * secreto cero se inventa en cada corrida, como lo pondría un {@code .env} fuera de git (A-10).
 *
 * <p>Solo HTTPS (A-44): antes de levantarlo, la prueba hace la ceremonia de la CA de la finca con
 * {@code despliegue/pki/Ceremonia.java}, el mismo programa que en la finca: la raíz y el certificado de OpenBao
 * (paso 1) y, mientras {@code openbao-init} espera, la firma del CSR de la intermedia (paso 2, A-56). La raíz se
 * inventa en cada corrida y se borra al terminar (A-45).
 */
final class NodoCompose implements AutoCloseable {

    /** Las pruebas corren desde pruebas-extremo-a-extremo/: lo probado es el compose.yaml que se despliega. */
    private static final File COMPOSE = new File("../despliegue/compose.yaml");
    private static final Duration ARRANQUE = Duration.ofMinutes(6);   // la primera construcción baja Maven y las dependencias

    static final String BIBLIOTECA_SOFTHSM = "/usr/lib/softhsm/libsofthsm2.so";
    static final int PUERTO_GATEWAY = 8443;   // solo HTTPS (A-44, A-51)
    static final int PUERTO_BASE = 5432;

    /** El cliente de desarrollo y CI con el rol operador (CT-2); no está en el realm de la finca. */
    private static final File CLIENTE_SIMULADOR = new File("../despliegue/keycloak/IA_crear-cliente-simulador.sh");
    private static final Path CEREMONIA = Path.of("..", "despliegue", "pki", "Ceremonia.java");

    private final ComposeContainer compose;
    private final String clavePg;
    private final String secretoSimulador = alAzar();
    private final String claveRaiz = alAzar();
    private final Path pki;
    private final Map<String, String> entorno;
    private Thread firmante;

    /** @param subred la subred de Docker de esta prueba, distinta de la de desarrollo y de las otras pruebas (A-42) */
    NodoCompose(String subred) {
        this.clavePg = alAzar();
        try {
            this.pki = Files.createTempDirectory("florlogic-pki");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        this.entorno = Map.ofEntries(Map.entry("FLORLOGIC_PKI", pki.resolve("nodo").toAbsolutePath().toString()),
                Map.entry("FLORLOGIC_CEREMONIA_ESPERA", "300"),                // la prueba firma mientras espera (A-56)
                Map.entry("FLORLOGIC_PG_ADMIN", clavePg),
                Map.entry("FLORLOGIC_HSM_PIN", alAzar()),
                Map.entry("FLORLOGIC_SOFTHSM_SO_PIN", alAzar()),
                Map.entry("FLORLOGIC_KC_ADMIN", alAzar()),                     // CT-2: el administrador de Keycloak
                Map.entry("FLORLOGIC_WEB_SECRETO", alAzar()),                  // F4: el secreto del cliente web
                Map.entry("FLORLOGIC_SOFTHSM_CREAR", "si"),                    // fuera de la finca (A-29)
                Map.entry("BAO_HSM_LIB", BIBLIOTECA_SOFTHSM),
                Map.entry("FLORLOGIC_RED", subred),
                Map.entry("FLORLOGIC_GATEWAY_PUERTO", "0"),
                Map.entry("FLORLOGIC_BASE_PUERTO", "0"));
        this.compose = new ComposeContainer("florlogic-prueba", COMPOSE)
                .withBuild(true)
                .withEnv(entorno)
                // El healthcheck de compose.yaml es la señal de «sano» (A-30): la prueba espera lo mismo que Compose.
                .waitingFor("backend", Wait.forHealthcheck().withStartupTimeout(ARRANQUE))
                .waitingFor("gateway", Wait.forHealthcheck().withStartupTimeout(ARRANQUE))
                .waitingFor("keycloak", Wait.forHealthcheck().withStartupTimeout(ARRANQUE))
                .withRemoveVolumes(true)
                .withRemoveImages(ComposeContainer.RemoveImages.LOCAL);
    }

    NodoCompose arrancar() {
        ceremonia("raiz", pki.resolve("custodia").toString(), entorno.get("FLORLOGIC_PKI"));
        firmante = Thread.ofVirtual().start(this::firmarCuandoLleguenElCsr);
        try {
            compose.start();
        } finally {
            firmante.interrupt();
        }
        crearClienteSimulador();
        return this;
    }

    /**
     * El paso 2 de la ceremonia (A-46): cuando openbao-init deja el CSR de la intermedia, lo firma con la raíz de esta
     * prueba, como lo haría el operador fuera de línea. openbao-init lo espera (FLORLOGIC_CEREMONIA_ESPERA, A-56).
     */
    private void firmarCuandoLleguenElCsr() {
        Path nodo = Path.of(entorno.get("FLORLOGIC_PKI"));
        try {
            while (!Files.exists(nodo.resolve("intermedia.crt"))) {
                if (Files.exists(nodo.resolve("intermedia.csr"))) {
                    ceremonia("firmar", pki.resolve("custodia").toString(), nodo.toString());
                    return;
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** Corre la ceremonia de la CA de la finca, el mismo programa que en la finca, con la clave de la raíz de la prueba. */
    private void ceremonia(String... argumentos) {
        List<String> linea = new ArrayList<>(List.of(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                CEREMONIA.toString()));
        linea.addAll(List.of(argumentos));
        ProcessBuilder proceso = new ProcessBuilder(linea).redirectErrorStream(true);
        proceso.environment().put("FLORLOGIC_RAIZ_CLAVE", claveRaiz);
        try {
            Process corriendo = proceso.start();
            String salida = new String(corriendo.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (corriendo.waitFor() != 0) {
                throw new IllegalStateException("la ceremonia " + argumentos[0] + " falló: " + salida);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    /** La raíz de la finca de esta prueba: la única en la que confían sus clientes HTTPS (A-45). */
    Path raiz() {
        return Path.of(entorno.get("FLORLOGIC_PKI"), "raiz.crt");
    }

    /** Un cliente HTTP/1.1 que solo confía en la raíz de esta prueba: nunca se desactiva la verificación (A-51 b). */
    HttpClient http() {
        try (InputStream entrada = Files.newInputStream(raiz())) {
            KeyStore confianza = KeyStore.getInstance(KeyStore.getDefaultType());
            confianza.load(null, null);
            confianza.setCertificateEntry("raiz", CertificateFactory.getInstance("X.509").generateCertificate(entrada));
            TrustManagerFactory gestores = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            gestores.init(confianza);
            SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, gestores.getTrustManagers(), null);
            return HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).sslContext(tls)
                    .connectTimeout(Duration.ofSeconds(5)).build();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    /** El secreto del cliente simulador de esta corrida: con él, {@link Identidad} pide tokens de operador. */
    String secretoSimulador() {
        return secretoSimulador;
    }

    /**
     * Como en desarrollo: el script corre dentro del contenedor de Keycloak, con kcadm y la clave del secreto cero. Entra
     * por la entrada estándar ({@code bash -s < script}, como en {@code scripts/IA_probar-canal.sh}): pasado como
     * argumento de {@code bash -c}, Windows parte la línea de órdenes en sus comillas dobles.
     */
    private void crearClienteSimulador() {
        Corrida r = compose(Map.of(), ProcessBuilder.Redirect.from(CLIENTE_SIMULADOR),
                "exec", "-T", "-e", "SECRETO_SIMULADOR=" + secretoSimulador, "keycloak", "bash", "-s");
        if (r.codigo() != 0) {
            throw new IllegalStateException("no se pudo crear el cliente simulador en Keycloak: " + r.salida());
        }
    }

    /** El contenedor de un servicio de compose.yaml. */
    ContainerState servicio(String nombre) {
        return compose.getContainerByServiceName(nombre)
                .orElseThrow(() -> new IllegalStateException("compose.yaml no levantó el servicio " + nombre));
    }

    /** La dirección del gateway en el anfitrión, el único que publica un puerto hacia la red (C-2). */
    String urlGateway() {
        return "https://127.0.0.1:" + puertoEnElAnfitrion("gateway", PUERTO_GATEWAY);
    }

    /** La base solo se publica en 127.0.0.1 del anfitrión (A-20): por ahí cuenta las filas la prueba (A-31). */
    String urlJdbcBase() {
        return "jdbc:postgresql://127.0.0.1:" + puertoEnElAnfitrion("base", PUERTO_BASE) + "/florlogic";
    }

    /**
     * Los puertos del contenedor que Compose publicó en el anfitrión, leídos de Docker ({@code NetworkSettings.Ports}).
     * {@code getMappedPort} de Testcontainers no sirve aquí: solo conoce los puertos de sus contenedores intermediarios,
     * no los que publica el {@code compose.yaml}.
     */
    List<Integer> puertosPublicados(String nombre) {
        return servicio(nombre).getContainerInfo().getNetworkSettings().getPorts().getBindings().entrySet().stream()
                .filter(publicado -> publicado.getValue() != null && publicado.getValue().length > 0)
                .map(publicado -> publicado.getKey().getPort())
                .distinct()
                .toList();
    }

    private int puertoEnElAnfitrion(String nombre, int puertoInterno) {
        Ports.Binding[] enlaces = servicio(nombre).getContainerInfo().getNetworkSettings().getPorts().getBindings()
                .get(ExposedPort.tcp(puertoInterno));
        return Arrays.stream(Objects.requireNonNull(enlaces, nombre + " no publica el " + puertoInterno))
                .map(Ports.Binding::getHostPortSpec)
                .map(Integer::valueOf)
                .findFirst()
                .orElseThrow();
    }

    /** Lo que dejó una orden de {@code docker compose}: su código de salida y lo que escribió, salida y errores juntos. */
    record Corrida(int codigo, String salida) {
    }

    /**
     * Corre {@code docker compose} sobre el mismo proyecto que levantó la prueba, con su mismo secreto cero, como lo haría
     * el operador en la finca ({@code run}, {@code stop}, {@code restart}, {@code up}…). Las pruebas de los casos
     * negativos lo necesitan porque cambian el nodo a mitad de camino (A-33).
     *
     * @param cambios variables que reemplazan las de la prueba solo en esta orden (por ejemplo, otra FLORLOGIC_PG_ADMIN)
     */
    Corrida compose(Map<String, String> cambios, String... orden) {
        return compose(cambios, ProcessBuilder.Redirect.PIPE, orden);
    }

    /** Lo mismo, con la entrada estándar de la orden tomada de {@code entrada} (por ejemplo, un archivo). */
    private Corrida compose(Map<String, String> cambios, ProcessBuilder.Redirect entrada, String... orden) {
        List<String> linea = new ArrayList<>(List.of("docker", "compose", "-p", proyecto(), "-f", COMPOSE.getAbsolutePath()));
        linea.addAll(List.of(orden));
        ProcessBuilder proceso = new ProcessBuilder(linea).redirectErrorStream(true).redirectInput(entrada);
        Map<String, String> variables = new HashMap<>(entorno);
        variables.putAll(cambios);
        proceso.environment().putAll(variables);
        try {
            Process corriendo = proceso.start();
            String salida = new String(corriendo.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            if (!corriendo.waitFor(ARRANQUE.toSeconds(), TimeUnit.SECONDS)) {
                corriendo.destroyForcibly();
                throw new IllegalStateException("docker compose no terminó: " + linea);
            }
            return new Corrida(corriendo.exitValue(), salida);
        } catch (IOException e) {
            throw new UncheckedIOException("no se pudo correr docker compose", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    /** Lo mismo, con el secreto cero de la prueba sin cambios. */
    Corrida compose(String... orden) {
        return compose(Map.of(), orden);
    }

    /**
     * El puerto del anfitrión que tiene ahora el servicio. Tras un {@code up} a mitad de prueba Compose puede recrear un
     * contenedor, y con los puertos en 0 (A-20) el nuevo recibe otro: este lo pregunta a Compose cada vez.
     */
    int puertoActual(String nombre, int puertoInterno) {
        Corrida r = compose("port", nombre, String.valueOf(puertoInterno));
        if (r.codigo() != 0) {
            throw new IllegalStateException(nombre + " no publica el " + puertoInterno + ": " + r.salida());
        }
        String direccion = r.salida().strip();
        return Integer.parseInt(direccion.substring(direccion.lastIndexOf(':') + 1));
    }

    /** El nombre del proyecto que Testcontainers le dio a Compose: lo dice la etiqueta de cualquier contenedor. */
    String proyecto() {
        return servicio("gateway").getContainerInfo().getConfig().getLabels().get("com.docker.compose.project");
    }

    /** El secreto cero de esta prueba (A-10): la imagen de OpenBao suelta lo necesita para su PIN. */
    String variable(String nombre) {
        return entorno.get(nombre);
    }

    /** La clave de administrador que esta prueba puso en FLORLOGIC_PG_ADMIN (A-10, A-31). */
    String clavePg() {
        return clavePg;
    }

    /** Hace «down -v», borra las imágenes construidas para esta prueba (A-42) y la raíz que inventó (A-45). */
    @Override
    public void close() {
        compose.stop();
        try (var archivos = Files.walk(pki)) {
            archivos.sorted(java.util.Comparator.reverseOrder()).forEach(archivo -> archivo.toFile().delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String alAzar() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - implements AutoCloseable / close(): permite usarlo en un try con recursos; aquí lo cierra @AfterAll.
 * - ComposeContainer("florlogic-prueba", archivo) (Testcontainers): levanta un compose.yaml completo.
 *   withBuild(true) construye las imágenes de los servicios con build; withEnv pasa las variables del secreto
 *   cero; waitingFor("backend", Wait.forHealthcheck().withStartupTimeout(…)) espera a que el healthcheck de
 *   compose.yaml diga sano, con tope; withRemoveVolumes(true) borra los volúmenes al parar;
 *   withRemoveImages(RemoveImages.LOCAL) borra las imágenes construidas para la prueba.
 * - Map.ofEntries(Map.entry(k, v), …): mapa inmutable con las variables de entorno de la prueba.
 * - Files.createTempDirectory("florlogic-pki"): carpeta temporal para la raíz y los certificados de esta corrida.
 * - Thread.ofVirtual().start(this::firmarCuandoLleguenElCsr) (Java 21): un hilo virtual que vigila la llegada del
 *   CSR mientras compose.start() espera; interrupt() lo detiene si ya no hace falta.
 * - this::metodo: referencia a método; equivale a una lambda que lo llama.
 * - ProcessBuilder(linea).redirectErrorStream(true) + environment().put(…): corre otro programa (java con el
 *   archivo Ceremonia.java, o docker compose) con variables propias y la salida de error junta con la normal.
 *   java Archivo.java corre un programa de un solo archivo sin compilarlo antes (Java 11+).
 * - waitFor(segundos, TimeUnit.SECONDS) / destroyForcibly() / exitValue(): espera con tope, mata el proceso si
 *   no terminó y lee su código de salida.
 * - docker compose -p proyecto -f compose.yaml …: -p fija el nombre del proyecto (el que eligió Testcontainers,
 *   leído de la etiqueta com.docker.compose.project) y -f el archivo.
 * - docker compose port servicio puerto: imprime «dirección:puerto» del anfitrión; se toma lo que va tras los dos
 *   puntos.
 * - KeyStore + CertificateFactory("X.509") + TrustManagerFactory + SSLContext("TLS"): el cliente HTTPS que solo
 *   confía en la raíz de la prueba (lee el primer certificado del archivo).
 * - HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).sslContext(tls).connectTimeout(…): fuerza
 *   HTTP/1.1, usa ese contexto TLS y corta la conexión a los 5 s.
 * - getContainerInfo().getNetworkSettings().getPorts().getBindings() (docker-java): los puertos publicados de un
 *   contenedor; ExposedPort.tcp(n) es la llave del puerto interno y getHostPortSpec() el del anfitrión.
 * - stream().filter(…).map(…).distinct().toList(): recorre los enlaces, deja los publicados, toma el número de
 *   puerto, quita repetidos y arma la lista.
 * - Optional.orElseThrow(() -> …): el valor, o lanza la excepción dada si está vacío.
 * - SecureRandom + HexFormat.of().formatHex: 16 bytes al azar seguros, en 32 cifras hexadecimales, para cada
 *   secreto de la corrida.
 * - Files.walk(pki).sorted(Comparator.reverseOrder()): recorre la carpeta y ordena al revés, así los archivos se
 *   borran antes que sus carpetas.
 * - record Corrida(int codigo, String salida): el resultado de una orden, inmutable.
 * - String... orden: varargs; las palabras de la orden de docker compose.
 */
