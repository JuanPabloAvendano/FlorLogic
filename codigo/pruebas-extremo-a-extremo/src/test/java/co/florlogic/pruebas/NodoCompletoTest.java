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

    private static final String RUTA = "/ingesta/v1/sesiones/0192f0a1-7c00-7000-8000-00000000a001/particiones";
    // La misma partición dorada del canal (backend-finca): dos anotaciones.
    private static final Path DORADA = Path.of("..", "backend-finca", "src", "test", "resources", "canal",
            "peticion-particion.json");

    private static NodoCompose nodo;

    private final HttpClient http = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();

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
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(nodo.urlGateway() + RUTA))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofFile(DORADA))
                .build(), HttpResponse.BodyHandlers.ofString());

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

    private static long filasRecibidas() throws Exception {
        try (var conexion = DriverManager.getConnection(nodo.urlJdbcBase(), "florlogic", nodo.clavePg());
                Statement consulta = conexion.createStatement();
                ResultSet filas = consulta.executeQuery("SELECT count(*) FROM anotacion_recibida")) {
            filas.next();
            return filas.getLong(1);
        }
    }
}
