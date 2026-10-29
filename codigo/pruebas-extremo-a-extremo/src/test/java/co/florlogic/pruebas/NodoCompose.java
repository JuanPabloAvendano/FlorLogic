package co.florlogic.pruebas;

import java.io.File;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
 */
final class NodoCompose implements AutoCloseable {

    /** Las pruebas corren desde pruebas-extremo-a-extremo/: lo probado es el compose.yaml que se despliega. */
    private static final File COMPOSE = new File("../despliegue/compose.yaml");
    private static final Duration ARRANQUE = Duration.ofMinutes(6);   // la primera construcción baja Maven y las dependencias

    static final int PUERTO_GATEWAY = 8080;
    static final int PUERTO_BASE = 5432;

    private final ComposeContainer compose;
    private final String clavePg;

    /** @param subred la subred de Docker de esta prueba, distinta de la de desarrollo y de las otras pruebas (A-42) */
    NodoCompose(String subred) {
        this.clavePg = alAzar();
        this.compose = new ComposeContainer("florlogic-prueba", COMPOSE)
                .withBuild(true)
                .withEnv(Map.of(
                        "FLORLOGIC_PG_ADMIN", clavePg,
                        "FLORLOGIC_HSM_PIN", alAzar(),
                        "FLORLOGIC_SOFTHSM_SO_PIN", alAzar(),
                        "FLORLOGIC_SOFTHSM_CREAR", "si",                       // fuera de la finca (A-29)
                        "BAO_HSM_LIB", "/usr/lib/softhsm/libsofthsm2.so",
                        "FLORLOGIC_RED", subred,
                        "FLORLOGIC_GATEWAY_PUERTO", "0",
                        "FLORLOGIC_BASE_PUERTO", "0"))
                // El healthcheck de compose.yaml es la señal de «sano» (A-30): la prueba espera lo mismo que Compose.
                .waitingFor("backend", Wait.forHealthcheck().withStartupTimeout(ARRANQUE))
                .waitingFor("gateway", Wait.forHealthcheck().withStartupTimeout(ARRANQUE))
                .withRemoveVolumes(true)
                .withRemoveImages(ComposeContainer.RemoveImages.LOCAL);
    }

    NodoCompose arrancar() {
        compose.start();
        return this;
    }

    /** El contenedor de un servicio de compose.yaml. */
    ContainerState servicio(String nombre) {
        return compose.getContainerByServiceName(nombre)
                .orElseThrow(() -> new IllegalStateException("compose.yaml no levantó el servicio " + nombre));
    }

    /** La dirección del gateway en el anfitrión, el único que publica un puerto hacia la red (C-2). */
    String urlGateway() {
        return "http://127.0.0.1:" + puertoEnElAnfitrion("gateway", PUERTO_GATEWAY);
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

    /** La clave de administrador que esta prueba puso en FLORLOGIC_PG_ADMIN (A-10, A-31). */
    String clavePg() {
        return clavePg;
    }

    /** Hace «down -v» y borra las imágenes construidas para esta prueba (A-42). */
    @Override
    public void close() {
        compose.stop();
    }

    private static String alAzar() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
