package co.florlogic.finca.dispositivos.infraestructura.output.repositorios.estructurados;

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

import co.florlogic.finca.dispositivos.servicios.output.dto.DispositivoDto;

/**
 * El adaptador JDBC de los celulares contra PostgreSQL real: una decisión del administrador siempre queda, y volver a
 * ver un celular conocido no la pisa. Se salta sin Docker.
 */
@Testcontainers(disabledWithoutDocker = true)
class DispositivoRepositorioAdapterTest {

    @Container
    static PostgreSQLContainer base = new PostgreSQLContainer("postgres:16");

    private static DispositivoRepositorioAdapter dispositivos;

    private final Instant ahora = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @BeforeAll
    static void migrar() {
        DriverManagerDataSource fuente = new DriverManagerDataSource(base.getJdbcUrl(), base.getUsername(), base.getPassword());
        Flyway.configure().dataSource(fuente).locations("classpath:db/migration").load().migrate();
        dispositivos = new DispositivoRepositorioAdapter(JdbcClient.create(fuente));
    }

    @Test
    void verlo_otra_vez_no_pisa_la_decision_del_administrador() {
        UUID id = UUID.randomUUID();
        dispositivos.guardar(new DispositivoDto(id, "PENDIENTE", ahora, ahora, "nodo"));
        DispositivoDto aprobado = new DispositivoDto(id, "APROBADO", ahora, ahora.plusSeconds(60), "admin-1");
        dispositivos.guardar(aprobado);

        dispositivos.guardar(new DispositivoDto(id, "PENDIENTE", ahora.plusSeconds(120), ahora.plusSeconds(120), "nodo"));

        assertThat(dispositivos.de(id)).contains(aprobado);
    }

    @Test
    void un_administrador_que_se_llama_nodo_tambien_decide() {
        UUID id = UUID.randomUUID();
        dispositivos.guardar(new DispositivoDto(id, "PENDIENTE", ahora, ahora, "nodo"));
        DispositivoDto bloqueado = new DispositivoDto(id, "BLOQUEADO", ahora, ahora.plusSeconds(60), "nodo");

        dispositivos.guardar(bloqueado);

        assertThat(dispositivos.de(id)).contains(bloqueado);
    }
}
