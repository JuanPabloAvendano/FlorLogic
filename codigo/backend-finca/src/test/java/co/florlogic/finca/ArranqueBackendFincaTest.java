package co.florlogic.finca;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/** El backend arranca contra PostgreSQL 16 y Flyway aplica V1. Se salta si no hay Docker. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class ArranqueBackendFincaTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    @LocalServerPort
    int puerto;

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void flywayAplicoLaVersionBase() {
        Integer aplicadas = jdbc.queryForObject(
                "select count(*) from flyway_schema_history where version = '1' and success", Integer.class);
        assertThat(aplicadas).isEqualTo(1);
    }

    @Test
    void laSaludRespondeUp() throws Exception {
        HttpResponse<String> respuesta = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + puerto + "/actuator/health")).build(),
                HttpResponse.BodyHandlers.ofString());

        assertThat(respuesta.statusCode()).isEqualTo(200);
        assertThat(respuesta.body()).contains("\"status\":\"UP\"");
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @SpringBootTest(webEnvironment = RANDOM_PORT): arranca la aplicación completa con su servidor web en un
 *   puerto libre al azar, para hablarle por HTTP de verdad.
 * - @Testcontainers(disabledWithoutDocker = true): JUnit maneja los contenedores de la clase; si no hay Docker, la
 *   clase entera se salta (no falla). Un verify en verde sin Docker no probó esto.
 * - @Container static PostgreSQLContainer("postgres:16"): un PostgreSQL 16 en Docker; al ser static se levanta una
 *   vez para toda la clase.
 * - @ServiceConnection: Spring Boot toma la URL, usuario y clave de ese contenedor y los usa como datasource, sin
 *   escribirlos en propiedades.
 * - @LocalServerPort int puerto: Spring inyecta el puerto al azar que tomó el servidor.
 * - @Autowired JdbcTemplate jdbc: inyecta la herramienta de Spring para lanzar SQL directo contra la base.
 * - @Test: método que JUnit 5 ejecuta como prueba.
 * - jdbc.queryForObject(sql, Integer.class): corre la consulta y devuelve el único valor como Integer.
 * - flyway_schema_history: la tabla donde Flyway anota cada migración aplicada; se busca la versión 1 con éxito.
 * - HttpClient / HttpRequest / HttpResponse.BodyHandlers.ofString(): el cliente HTTP del JDK; envía un GET a
 *   /actuator/health y entrega el cuerpo como texto.
 * - assertThat(…).isEqualTo / contains (AssertJ): comprobaciones que, si no se cumplen, hacen fallar la prueba.
 * - "\"status\":\"UP\"": el JSON de salud de Spring Actuator cuando todo responde (base incluida).
 */
