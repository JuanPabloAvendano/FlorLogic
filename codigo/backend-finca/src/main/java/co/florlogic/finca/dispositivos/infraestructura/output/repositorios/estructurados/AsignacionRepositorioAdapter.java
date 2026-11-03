package co.florlogic.finca.dispositivos.infraestructura.output.repositorios.estructurados;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import co.florlogic.finca.dispositivos.servicios.output.dto.AsignacionDto;
import co.florlogic.finca.dispositivos.servicios.output.ports.repositorios.AsignacionRepositorio;

/** La asignación del día en PostgreSQL con {@code JdbcClient} (TEC-07). */
@Repository
class AsignacionRepositorioAdapter implements AsignacionRepositorio {

    private final JdbcClient jdbc;

    AsignacionRepositorioAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void reemplazar(LocalDate jornada, UUID dispositivo, List<AsignacionDto> celdas) {
        jdbc.sql("DELETE FROM asignacion WHERE jornada = :j AND dispositivo_id = :d")
                .param("j", Date.valueOf(jornada)).param("d", dispositivo).update();
        for (AsignacionDto c : celdas) {
            jdbc.sql("""
                            INSERT INTO asignacion (jornada, dispositivo_id, seccion_id, campo_id, asignado_en, asignado_por)
                            VALUES (:j, :d, :s, :c, :en, :por)""")
                    .param("j", Date.valueOf(c.jornada())).param("d", c.dispositivo()).param("s", c.seccion())
                    .param("c", c.campo()).param("en", Timestamp.from(c.asignadoEn())).param("por", c.asignadoPor())
                    .update();
        }
    }

    @Override
    public List<AsignacionDto> de(LocalDate jornada) {
        return jdbc.sql("""
                        SELECT * FROM asignacion WHERE jornada = :j
                        ORDER BY dispositivo_id, seccion_id, campo_id""")
                .param("j", Date.valueOf(jornada))
                .query((f, n) -> new AsignacionDto(f.getDate("jornada").toLocalDate(),
                        f.getObject("dispositivo_id", UUID.class), f.getString("seccion_id"), f.getString("campo_id"),
                        f.getTimestamp("asignado_en").toInstant(), f.getString("asignado_por")))
                .list();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Repository (Spring): como @Component, pero marca la clase como acceso a datos; Spring además traduce los errores
 *   de JDBC a su familia DataAccessException.
 * - JdbcClient (Spring JDBC): sql(…) recibe la sentencia, param("j", x) llena el marcador :j, update() ejecuta y
 *   query((f, n) → …).list() convierte cada fila (f es el ResultSet, n su número) en un AsignacionDto.
 * - """ … """: bloque de texto de Java para el SQL de varios renglones.
 * - Date.valueOf / Timestamp.from y getDate(…).toLocalDate() / getTimestamp(…).toInstant(): ida y vuelta entre
 *   LocalDate o Instant y los tipos de JDBC. getObject(…, UUID.class) lee la columna uuid como UUID.
 * - reemplazar: un DELETE y luego un INSERT por celda. No abre transacción propia: corre dentro de la @Transactional
 *   del caso de uso que lo llama, así borrar y volver a llenar se confirman juntos.
 */
