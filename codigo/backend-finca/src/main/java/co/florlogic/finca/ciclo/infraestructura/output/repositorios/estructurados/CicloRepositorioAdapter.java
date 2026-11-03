package co.florlogic.finca.ciclo.infraestructura.output.repositorios.estructurados;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import co.florlogic.finca.ciclo.servicios.output.dto.HechoDto;
import co.florlogic.finca.ciclo.servicios.output.dto.TrabajoDto;
import co.florlogic.finca.ciclo.servicios.output.ports.repositorios.HechoRepositorio;
import co.florlogic.finca.ciclo.servicios.output.ports.repositorios.TrabajoRepositorio;

/** Hechos y trabajos del gerente en PostgreSQL con {@code JdbcClient} (TEC-07). */
@Repository
class CicloRepositorioAdapter implements HechoRepositorio, TrabajoRepositorio {

    private final JdbcClient jdbc;

    CicloRepositorioAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<HechoDto> vigenteConTurno(String produccionId, String seccionId, String campoId, LocalDate jornada) {
        return jdbc.sql("""
                        SELECT * FROM hecho_produccion
                        WHERE produccion_id = :p AND seccion_id = :s AND campo_id = :c AND jornada = :j FOR UPDATE""")
                .param("p", produccionId).param("s", seccionId).param("c", campoId).param("j", Date.valueOf(jornada))
                .query((f, n) -> new HechoDto(f.getString("produccion_id"), f.getString("seccion_id"), f.getString("campo_id"),
                        f.getDate("jornada").toLocalDate(), f.getString("valor"), f.getString("motivo_ausencia"),
                        f.getTimestamp("capturado_en").toInstant(), f.getObject("anotacion_uuid", UUID.class),
                        f.getObject("dispositivo_id", UUID.class)))
                .optional();
    }

    @Override
    public void guardar(HechoDto h) {
        jdbc.sql("""
                        INSERT INTO hecho_produccion (produccion_id, seccion_id, campo_id, jornada, valor, motivo_ausencia,
                            capturado_en, anotacion_uuid, dispositivo_id)
                        VALUES (:p, :s, :c, :j, :valor, :motivo, :capturado, :anotacion, :dispositivo)
                        ON CONFLICT (produccion_id, seccion_id, campo_id, jornada) DO UPDATE
                        SET valor = EXCLUDED.valor, motivo_ausencia = EXCLUDED.motivo_ausencia,
                            capturado_en = EXCLUDED.capturado_en, anotacion_uuid = EXCLUDED.anotacion_uuid,
                            dispositivo_id = EXCLUDED.dispositivo_id""")
                .param("p", h.produccionId()).param("s", h.seccionId()).param("c", h.campoId())
                .param("j", Date.valueOf(h.jornada())).param("valor", h.valor()).param("motivo", h.motivoAusencia())
                .param("capturado", Timestamp.from(h.capturadoEn())).param("anotacion", h.anotacion())
                .param("dispositivo", h.dispositivo())
                .update();
    }

    @Override
    public void abrir(TrabajoDto t) {
        jdbc.sql("""
                        INSERT INTO trabajo_gerente (anotacion_uuid, dispositivo_id, seccion_id, jornada, codigos, abierto_en)
                        VALUES (:anotacion, :dispositivo, :seccion, :jornada, :codigos, :abierto)
                        ON CONFLICT (anotacion_uuid) DO NOTHING""")
                .param("anotacion", t.anotacion()).param("dispositivo", t.dispositivo()).param("seccion", t.seccionId())
                .param("jornada", t.jornada() == null ? null : Date.valueOf(t.jornada()))
                .param("codigos", t.codigos().toArray(String[]::new)).param("abierto", Timestamp.from(t.abiertoEn()))
                .update();
    }

    @Override
    public List<TrabajoDto> abiertos(int maximo) {
        return jdbc.sql("""
                        SELECT * FROM trabajo_gerente WHERE resuelto_en IS NULL
                        ORDER BY abierto_en, anotacion_uuid LIMIT :maximo""")
                .param("maximo", maximo)
                .query((f, n) -> new TrabajoDto(f.getObject("anotacion_uuid", UUID.class),
                        f.getObject("dispositivo_id", UUID.class), f.getString("seccion_id"),
                        f.getDate("jornada") == null ? null : f.getDate("jornada").toLocalDate(),
                        List.of((String[]) f.getArray("codigos").getArray()), f.getTimestamp("abierto_en").toInstant()))
                .list();
    }

    @Override
    public boolean resolver(UUID anotacion, String gerente, Instant cuando) {
        return jdbc.sql("""
                        UPDATE trabajo_gerente SET resuelto_en = :cuando, resuelto_por = :gerente
                        WHERE anotacion_uuid = :anotacion AND resuelto_en IS NULL""")
                .param("cuando", Timestamp.from(cuando)).param("gerente", gerente).param("anotacion", anotacion)
                .update() == 1;
    }
}
