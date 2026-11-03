package co.florlogic.finca.ingesta.infraestructura.output.repositorios.estructurados;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import co.florlogic.finca.ingesta.servicios.output.dto.SesionDto;

/** El adaptador JDBC de la sesión contra PostgreSQL real: crear, leer y cerrar. Se salta sin Docker. */
@Testcontainers(disabledWithoutDocker = true)
class SesionRepositorioAdapterTest {

    @Container
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    private static SesionRepositorioAdapter sesiones;

    @BeforeAll
    static void migrar() {
        DriverManagerDataSource fuente = new DriverManagerDataSource(base.getJdbcUrl(), base.getUsername(), base.getPassword());
        Flyway.configure().dataSource(fuente).locations("classpath:db/migration").load().migrate();
        sesiones = new SesionRepositorioAdapter(JdbcClient.create(fuente));
    }

    @Test
    void una_sesion_se_guarda_se_lee_y_se_cierra() {
        Instant ahora = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        SesionDto abierta = new SesionDto(UUID.randomUUID(), UUID.randomUUID(), "op-07", ahora, -250, 12, false,
                "ABIERTA", null);

        sesiones.guardar(abierta);
        assertThat(sesiones.de(abierta.sesionId())).contains(abierta);

        SesionDto cerrada = new SesionDto(abierta.sesionId(), abierta.dispositivoId(), "op-07", ahora, -250, 12, false,
                "PARCIAL", ahora.plusSeconds(30));
        sesiones.guardar(cerrada);
        assertThat(sesiones.de(abierta.sesionId())).contains(cerrada);
    }

    @Test
    void una_sesion_que_no_existe_da_vacio() {
        assertThat(sesiones.de(UUID.randomUUID())).isEmpty();
    }
}
