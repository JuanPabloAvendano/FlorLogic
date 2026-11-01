package co.florlogic.finca.ingesta.infraestructura.output.repositorios.estructurados;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AvanceRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AvanceRepositorioContratoTest;

/** El adaptador JDBC del avance contra PostgreSQL real cumple el mismo contrato que el doble. Se salta sin Docker. */
@Testcontainers(disabledWithoutDocker = true)
class AvanceRepositorioAdapterTest extends AvanceRepositorioContratoTest {

    @Container
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    private static JdbcClient jdbc;

    @BeforeAll
    static void migrar() {
        DriverManagerDataSource fuente = new DriverManagerDataSource(base.getJdbcUrl(), base.getUsername(), base.getPassword());
        // Las mismas migraciones que en producción (V1…V3): la prueba usa la tabla real, no una copia.
        Flyway.configure().dataSource(fuente).locations("classpath:db/migration").load().migrate();
        jdbc = JdbcClient.create(fuente);
    }

    @BeforeEach
    void tablaVacia() {
        jdbc.sql("DELETE FROM avance_dispositivo").update();
    }

    @Override
    protected AvanceRepositorio crear() {
        return new AvanceRepositorioAdapter(jdbc);
    }
}
