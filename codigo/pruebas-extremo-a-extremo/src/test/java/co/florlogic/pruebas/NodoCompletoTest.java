package co.florlogic.pruebas;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * El nodo de la finca completo, con el {@code compose.yaml} real: base, OpenBao con SoftHSM, openbao-init, backend
 * construido con su Dockerfile y gateway con su WAF. Es el camino feliz del Segmento 1 del esqueleto: si el backend no
 * saca su clave del vault, no arranca y la prueba falla; también es la evidencia de que Spring Cloud Vault 5.0.3
 * funciona con Boot 4.1.1 (A-43). Los A-n y C-n son de {@code despliegue/espec/ESPEC-esqueleto-tx1.md}.
 *
 * <p>Se salta sin Docker; sin Docker no prueba nada contra PostgreSQL ni OpenBao.
 */
@Testcontainers(disabledWithoutDocker = true)
class NodoCompletoTest {

    // Para las comprobaciones de transporte (en claro, mTLS): no hace falta que la sesión exista.
    private static final String RUTA = "/ingesta/v1/sesiones/0192f0a1-7c00-7000-8000-00000000a001/particiones";
    // La misma partición dorada del canal (backend-finca): dos anotaciones.
    private static final Path DORADA = Path.of("..", "backend-finca", "src", "test", "resources", "canal",
            "peticion-particion.json");

    private static NodoCompose nodo;

    private final HttpClient http = nodo.http();   // solo confía en la raíz de la finca de la prueba (A-44)

    @BeforeAll
    static void levantarElNodo() {
        nodo = new NodoCompose("172.31.201.0/24").arrancar();
    }

    @AfterAll
    static void borrarElNodo() {
        if (nodo != null) {
            nodo.close();
        }
    }

    @Test
    void la_particion_dorada_entra_por_el_gateway_y_queda_en_la_base() throws Exception {
        // CT-1: abre la sesión y entrega en ella, como el celular.
        HttpResponse<String> r = EntregaDeLaDorada.entregar(http, nodo.urlGateway(), DORADA);

        assertThat(r.statusCode()).as(r.body()).isEqualTo(200);
        // Se cuentan en la base, sin fiarse de lo que dice el backend (A-31, C-1).
        assertThat(filasRecibidas()).isEqualTo(2);
    }

    @Test
    void el_gateway_es_la_unica_puerta_del_nodo() {
        // Ni el backend (18080 de antes, 8080 de ahora) ni el vault (8200) publican puertos en el anfitrión (C-2).
        assertThat(nodo.puertosPublicados("backend")).isEmpty();
        assertThat(nodo.puertosPublicados("openbao")).isEmpty();
        assertThat(nodo.puertosPublicados("gateway")).containsExactly(NodoCompose.PUERTO_GATEWAY);
    }

    @Test
    void ningun_enlace_del_nodo_va_en_claro() throws Exception {
        // A-44: HTTP en claro al puerto del gateway no llega a nada (Nginx responde 400 a HTTP en un puerto TLS).
        HttpResponse<String> enClaro = HttpClient.newHttpClient().send(HttpRequest.newBuilder(
                URI.create(nodo.urlGateway().replace("https://", "http://") + RUTA)).build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(enClaro.statusCode()).isEqualTo(400);

        // Dentro del nodo, desde el gateway: OpenBao no atiende en claro, y el backend no deja entrar a quien no
        // presenta un certificado de la finca, aunque confíe en la raíz (mTLS, A-48).
        assertThat(desdeElGateway("curl -s -m 5 http://openbao:8200/v1/sys/health").salida())
                .contains("Client sent an HTTP request to an HTTPS server");
        assertThat(desdeElGateway("curl -s -m 5 --cacert /tls/raiz.crt -o /dev/null https://backend:8443" + RUTA).codigo())
                .isNotZero();
        // Con el certificado del gateway sí entra y el backend responde: el canal es el del gateway.
        assertThat(desdeElGateway("curl -s -m 5 --cacert /tls/raiz.crt --cert /tls/servidor.crt --key /tls/servidor.key"
                + " -o /dev/null -w '%{http_code}' -X POST -H 'Content-Type: application/json' -d '{}'"
                + " https://backend:8443" + RUTA).salida()).matches("[1-5][0-9][0-9]");
    }

    private static NodoCompose.Corrida desdeElGateway(String orden) {
        return nodo.compose("exec", "-T", "gateway", "sh", "-c", orden);
    }

    private static long filasRecibidas() throws Exception {
        try (var conexion = DriverManager.getConnection(nodo.urlJdbcBase(), "florlogic", nodo.clavePg());
                Statement consulta = conexion.createStatement();
                ResultSet filas = consulta.executeQuery("SELECT count(*) FROM anotacion_recibida")) {
            filas.next();
            return filas.getLong(1);
        }
    }
}
