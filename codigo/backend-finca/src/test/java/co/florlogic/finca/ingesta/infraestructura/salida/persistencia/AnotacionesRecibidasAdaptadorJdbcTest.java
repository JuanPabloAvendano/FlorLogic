package co.florlogic.finca.ingesta.infraestructura.salida.persistencia;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import co.florlogic.finca.ingesta.servicios.puertos.salida.AnotacionesRecibidasPuertoSalida;
import co.florlogic.finca.ingesta.servicios.puertos.salida.AnotacionesRecibidasPuertoSalidaContratoTest;

/**
 * El adaptador JDBC contra PostgreSQL real (integración estrecha: solo el adaptador, sin Spring ni HTTP). Corre
 * las mismas pruebas que el doble en memoria. Sin {@code @JdbcTest}: en Spring Boot 4 vive en un módulo que el
 * proyecto no tiene, y agregarlo pide una fila en IA_TECNOLOGIAS-vigentes.md; Flyway y {@code JdbcClient} ya están.
 * Se salta sin Docker.
 */
@Testcontainers(disabledWithoutDocker = true)
class AnotacionesRecibidasAdaptadorJdbcTest extends AnotacionesRecibidasPuertoSalidaContratoTest {

    @Container
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    private static JdbcClient jdbc;

    @BeforeAll
    static void migrar() {
        DriverManagerDataSource fuente = new DriverManagerDataSource(base.getJdbcUrl(), base.getUsername(), base.getPassword());
        // Las mismas migraciones que en producción (V1, V2): la prueba usa la tabla real, no una copia.
        Flyway.configure().dataSource(fuente).locations("classpath:db/migration").load().migrate();
        jdbc = JdbcClient.create(fuente);
    }

    @BeforeEach
    void tablaVacia() {
        jdbc.sql("DELETE FROM anotacion_recibida").update();
    }

    @Override
    protected AnotacionesRecibidasPuertoSalida crear() {
        return new AnotacionesRecibidasAdaptadorJdbc(jdbc);
    }
}
