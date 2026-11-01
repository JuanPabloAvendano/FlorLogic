package co.florlogic.finca.ingesta.infraestructura.output.repositorios.estructurados;

import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import co.florlogic.finca.ingesta.servicios.output.dto.AvanceDto;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AvanceRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.ChoqueConcurrente;

/**
 * Adaptador de salida del avance con {@code JdbcClient} (TEC-07; plantilla adaptador-persistencia 10.3). Versión
 * optimista (A-98): el nuevo entra con {@code INSERT} y versión 1; el que ya existía, con
 * {@code UPDATE … WHERE version = ?}. Si otra partición ganó antes, la llave repetida o las 0 filas se vuelven
 * {@link ChoqueConcurrente}.
 */
@Repository
class AvanceRepositorioAdapter implements AvanceRepositorio {

    private final JdbcClient jdbc;

    AvanceRepositorioAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<AvanceDto> deDispositivo(UUID dispositivoId) {
        return jdbc.sql("""
                        SELECT dispositivo_id, confirmado_hasta, por_encima, version
                        FROM avance_dispositivo WHERE dispositivo_id = :id""")
                .param("id", dispositivoId)
                .query((fila, n) -> new AvanceDto(
                        fila.getObject("dispositivo_id", UUID.class),
                        fila.getLong("confirmado_hasta"),
                        numeros(fila.getArray("por_encima")),
                        fila.getLong("version")))
                .optional();
    }

    @Override
    public void guardar(AvanceDto avance) {
        Long[] porEncima = avance.porEncima().toArray(Long[]::new);
        if (avance.version() == 0) {
            try {
                jdbc.sql("""
                                INSERT INTO avance_dispositivo (dispositivo_id, confirmado_hasta, por_encima, version)
                                VALUES (:id, :confirmado, :porEncima, 1)""")
                        .param("id", avance.dispositivoId())
                        .param("confirmado", avance.confirmadoHasta())
                        .param("porEncima", porEncima)
                        .update();
            } catch (DuplicateKeyException e) {
                throw new ChoqueConcurrente("El avance del dispositivo " + avance.dispositivoId() + " ya existía", e);
            }
            return;
        }
        int actualizadas = jdbc.sql("""
                        UPDATE avance_dispositivo
                        SET confirmado_hasta = :confirmado, por_encima = :porEncima, version = version + 1
                        WHERE dispositivo_id = :id AND version = :version""")
                .param("id", avance.dispositivoId())
                .param("confirmado", avance.confirmadoHasta())
                .param("porEncima", porEncima)
                .param("version", avance.version())
                .update();
        if (actualizadas != 1) {
            throw new ChoqueConcurrente("El avance del dispositivo " + avance.dispositivoId() + " cambió de versión");
        }
    }

    private static List<Long> numeros(Array arreglo) throws SQLException {
        return arreglo == null ? List.of() : Arrays.asList((Long[]) arreglo.getArray());
    }
}
