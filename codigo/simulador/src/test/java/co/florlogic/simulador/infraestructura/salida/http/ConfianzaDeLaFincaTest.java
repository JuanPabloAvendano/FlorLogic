package co.florlogic.simulador.infraestructura.salida.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

/**
 * El simulador habla HTTPS con un nodo cuyo certificado firma una raíz propia (A-44, A-51 b): con esa raíz entra, sin
 * ella el apretón de manos falla. La raíz y el nodo falso los arma keytool, el del mismo JDK.
 */
class ConfianzaDeLaFincaTest {

    private static final String CLAVE = "prueba-confianza";

    @TempDir
    Path carpeta;

    private HttpsServer nodo;
    private Path raiz;

    @BeforeEach
    void nodoConCertificadoPropio() throws Exception {
        Path almacen = carpeta.resolve("nodo.p12");
        raiz = carpeta.resolve("raiz.crt");
        keytool("-genkeypair", "-alias", "nodo", "-keyalg", "EC", "-groupname", "secp256r1", "-dname", "CN=localhost",
                "-ext", "san=dns:localhost,ip:127.0.0.1", "-validity", "1", "-keystore", almacen.toString(),
                "-storetype", "PKCS12", "-storepass", CLAVE);
        keytool("-exportcert", "-rfc", "-alias", "nodo", "-keystore", almacen.toString(), "-storepass", CLAVE,
                "-file", raiz.toString());

        KeyStore llaves = KeyStore.getInstance("PKCS12");
        try (InputStream entrada = Files.newInputStream(almacen)) {
            llaves.load(entrada, CLAVE.toCharArray());
        }
        KeyManagerFactory gestores = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        gestores.init(llaves, CLAVE.toCharArray());
        SSLContext tls = SSLContext.getInstance("TLS");
        tls.init(gestores.getKeyManagers(), null, null);

        nodo = HttpsServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        nodo.setHttpsConfigurator(new HttpsConfigurator(tls));
        nodo.createContext("/", intercambio -> {
            byte[] cuerpo = "nodo".getBytes(StandardCharsets.UTF_8);
            intercambio.sendResponseHeaders(200, cuerpo.length);
            intercambio.getResponseBody().write(cuerpo);
            intercambio.close();
        });
        nodo.start();
    }

    @AfterEach
    void apagar() {
        nodo.stop(0);
    }

    @Test
    void con_la_raiz_de_la_finca_habla_https_con_el_nodo() throws Exception {
        HttpResponse<String> r = ConfianzaDeLaFinca.cliente(raiz).send(pedido(), HttpResponse.BodyHandlers.ofString());

        assertThat(r.statusCode()).isEqualTo(200);
        assertThat(r.body()).isEqualTo("nodo");
    }

    @Test
    void sin_la_raiz_el_almacen_de_java_no_confia_en_el_nodo() {
        assertThatThrownBy(() -> HttpClient.newHttpClient().send(pedido(), HttpResponse.BodyHandlers.ofString()))
                .isInstanceOf(SSLHandshakeException.class);
    }

    @Test
    void un_archivo_que_no_es_certificado_para_el_arranque() throws Exception {
        Path basura = Files.writeString(carpeta.resolve("basura.crt"), "esto no es un certificado");

        assertThatThrownBy(() -> ConfianzaDeLaFinca.cliente(basura))
                .hasMessageContaining(ConfianzaDeLaFinca.VARIABLE);
    }

    private HttpRequest pedido() {
        return HttpRequest.newBuilder(URI.create("https://localhost:" + nodo.getAddress().getPort() + "/")).build();
    }

    private static void keytool(String... argumentos) throws Exception {
        var orden = new java.util.ArrayList<String>();
        orden.add(Path.of(System.getProperty("java.home"), "bin", "keytool").toString());
        orden.addAll(java.util.List.of(argumentos));
        Process proceso = new ProcessBuilder(orden).redirectErrorStream(true).start();
        String salida = new String(proceso.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertThat(proceso.waitFor()).as(salida).isZero();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @TempDir (JUnit): carpeta temporal por prueba para el almacén y la raíz.
 * - @BeforeEach / @AfterEach: arman el nodo falso antes de cada caso y lo apagan después.
 * - keytool -genkeypair -alias nodo -keyalg EC -groupname secp256r1: crea un par de llaves de curva elíptica
 *   P-256 con su certificado autofirmado. -dname "CN=localhost": el nombre del sujeto. -ext
 *   san=dns:localhost,ip:127.0.0.1: los nombres alternativos que el cliente compara con la dirección; sin ellos
 *   HTTPS falla aunque confíe en la raíz. -validity 1: dura un día. -keystore/-storetype PKCS12/-storepass: dónde y
 *   con qué clave se guarda.
 * - keytool -exportcert -rfc: saca el certificado en texto PEM (el que lee ConfianzaDeLaFinca). Como es
 *   autofirmado, hace de «raíz» de la prueba.
 * - System.getProperty("java.home") + bin/keytool: el keytool del mismo JDK que corre la prueba.
 * - ProcessBuilder(...).redirectErrorStream(true).start(): corre el programa y junta errores con la salida;
 *   waitFor() espera y devuelve su código.
 * - KeyStore "PKCS12" + KeyManagerFactory: cargan la llave del nodo falso; SSLContext.init(gestores, null, null)
 *   le da al servidor su certificado propio.
 * - HttpsServer.create(new InetSocketAddress("127.0.0.1", 0), 0) (JDK, com.sun.net.httpserver): servidor HTTPS
 *   mínimo; el puerto 0 deja que el sistema elija uno libre. setHttpsConfigurator pone el contexto TLS;
 *   createContext("/", …) responde «nodo» a todo; stop(0) lo apaga sin esperar.
 * - sendResponseHeaders(200, largo): el estado y el largo del cuerpo antes de escribirlo.
 * - assertThatThrownBy(() -> …).isInstanceOf(SSLHandshakeException.class) (AssertJ): comprueba que la lambda
 *   lanza esa excepción: el apretón de manos TLS falla sin la raíz. hasMessageContaining revisa el mensaje.
 */
