package co.florlogic.finca.dispositivos.infraestructura.output.repositorios.estructurados;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import co.florlogic.finca.dispositivos.servicios.output.dto.DispositivoDto;
import co.florlogic.finca.dispositivos.servicios.output.ports.repositorios.DispositivoRepositorio;

/** Los celulares en PostgreSQL con {@code JdbcClient} (TEC-07). */
@Repository
class DispositivoRepositorioAdapter implements DispositivoRepositorio {

    private final JdbcClient jdbc;

    DispositivoRepositorioAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<DispositivoDto> de(UUID id) {
        return jdbc.sql("SELECT * FROM dispositivo WHERE dispositivo_id = :id").param("id", id)
                .query((fila, n) -> fila(fila)).optional();
    }

    @Override
    public void guardar(DispositivoDto d) {
        jdbc.sql("""
                        INSERT INTO dispositivo (dispositivo_id, estado, visto_por_primera_vez, cambiado_en, cambiado_por)
                        VALUES (:id, :estado, :visto, :cambiado, :por)
                        ON CONFLICT (dispositivo_id) DO UPDATE
                        SET estado = EXCLUDED.estado, cambiado_en = EXCLUDED.cambiado_en, cambiado_por = EXCLUDED.cambiado_por
                        WHERE EXCLUDED.cambiado_por <> 'nodo'""")
                .param("id", d.id())
                .param("estado", d.estado())
                .param("visto", Timestamp.from(d.vistoPorPrimeraVez()))
                .param("cambiado", Timestamp.from(d.cambiadoEn()))
                .param("por", d.cambiadoPor())
                .update();
    }

    @Override
    public List<DispositivoDto> todos() {
        return jdbc.sql("SELECT * FROM dispositivo ORDER BY visto_por_primera_vez").query((fila, n) -> fila(fila)).list();
    }

    private static DispositivoDto fila(ResultSet fila) throws SQLException {
        return new DispositivoDto(fila.getObject("dispositivo_id", UUID.class), fila.getString("estado"),
                fila.getTimestamp("visto_por_primera_vez").toInstant(), fila.getTimestamp("cambiado_en").toInstant(),
                fila.getString("cambiado_por"));
    }
}
