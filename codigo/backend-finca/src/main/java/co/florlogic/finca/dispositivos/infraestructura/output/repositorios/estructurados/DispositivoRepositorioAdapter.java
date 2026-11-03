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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Repository (Spring): como @Component, pero marca la clase como acceso a datos; Spring traduce los errores de
 *   JDBC a DataAccessException.
 * - JdbcClient: sql(…).param(…) arma la sentencia; query((fila, n) → fila(fila)) convierte cada fila con el método
 *   fila(…); optional() da cero o una, list() todas, update() ejecuta el cambio.
 * - ResultSet / SQLException (java.sql): la fila que entrega JDBC; sus getters pueden lanzar SQLException, por eso el
 *   método fila(…) la declara con throws.
 * - Optional<DispositivoDto>: vacío si el celular nunca se ha visto.
 * - INSERT … ON CONFLICT (dispositivo_id) DO UPDATE SET … = EXCLUDED.…: upsert de PostgreSQL; si el celular ya existe
 *   se actualizan estado y quién/cuándo con los valores que se intentaban insertar (EXCLUDED).
 * - WHERE EXCLUDED.cambiado_por <> 'nodo': la actualización solo ocurre si la trae un administrador. Cuando dos
 *   aperturas de un celular nuevo llegan a la vez, la segunda intenta insertar otra vez «pendiente, por nodo» y se
 *   descarta sin error, en vez de pisar lo que haya decidido un administrador.
 * - .param("estado", d.estado()): el estado viaja como texto; la tabla solo acepta PENDIENTE, APROBADO o BLOQUEADO.
 */
