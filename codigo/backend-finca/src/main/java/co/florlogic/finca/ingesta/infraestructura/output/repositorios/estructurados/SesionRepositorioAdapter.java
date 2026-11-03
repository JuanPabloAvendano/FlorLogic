package co.florlogic.finca.ingesta.infraestructura.output.repositorios.estructurados;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import co.florlogic.finca.ingesta.servicios.output.dto.SesionDto;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.SesionRepositorio;

/** Adaptador de salida de las sesiones con {@code JdbcClient} (CT-1). Crear o actualizar en una sola sentencia. */
@Repository
class SesionRepositorioAdapter implements SesionRepositorio {

    private final JdbcClient jdbc;

    SesionRepositorioAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<SesionDto> de(UUID sesionId) {
        return jdbc.sql("""
                        SELECT sesion_id, dispositivo_id, persona, abierta_en, desfase_ms, version_config,
                               dispositivo_registrado, estado, cerrada_en
                        FROM sesion_sincronizacion WHERE sesion_id = :id""")
                .param("id", sesionId)
                .query((fila, n) -> new SesionDto(fila.getObject("sesion_id", UUID.class),
                        fila.getObject("dispositivo_id", UUID.class), fila.getString("persona"),
                        fila.getTimestamp("abierta_en").toInstant(), fila.getLong("desfase_ms"),
                        fila.getInt("version_config"), fila.getBoolean("dispositivo_registrado"), fila.getString("estado"),
                        Optional.ofNullable(fila.getTimestamp("cerrada_en")).map(Timestamp::toInstant).orElse(null)))
                .optional();
    }

    @Override
    public void guardar(SesionDto sesion) {
        jdbc.sql("""
                        INSERT INTO sesion_sincronizacion (sesion_id, dispositivo_id, persona, abierta_en, desfase_ms,
                            version_config, dispositivo_registrado, estado, cerrada_en)
                        VALUES (:id, :dispositivo, :persona, :abierta, :desfase, :version, :registrado, :estado, :cerrada)
                        ON CONFLICT (sesion_id) DO UPDATE SET estado = EXCLUDED.estado, cerrada_en = EXCLUDED.cerrada_en""")
                .param("id", sesion.sesionId())
                .param("dispositivo", sesion.dispositivoId())
                .param("persona", sesion.persona())
                .param("abierta", Timestamp.from(sesion.abiertaEn()))
                .param("desfase", sesion.desfaseMs())
                .param("version", sesion.versionConfig())
                .param("registrado", sesion.dispositivoRegistrado())
                .param("estado", sesion.estado())
                .param("cerrada", sesion.cerradaEn() == null ? null : Timestamp.from(sesion.cerradaEn()))
                .update();
    }
}
