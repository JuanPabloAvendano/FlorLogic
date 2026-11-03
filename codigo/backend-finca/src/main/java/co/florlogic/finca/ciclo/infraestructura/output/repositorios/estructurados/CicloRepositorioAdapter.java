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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Repository (Spring): como @Component, pero marca la clase como acceso a datos; Spring además traduce los errores
 *   de JDBC a su familia DataAccessException.
 * - JdbcClient (Spring JDBC): cliente de SQL directo. sql(…) recibe la sentencia, param("p", x) llena el marcador :p,
 *   query(…) convierte cada fila con la lambda (f, n) → … (f es el ResultSet de la fila, n su número), y optional(),
 *   list() o update() ejecutan y devuelven cero o una fila, la lista o el número de filas cambiadas.
 * - """ … """: bloque de texto de Java; permite escribir el SQL en varios renglones sin concatenar.
 * - java.sql.Date.valueOf / Timestamp.from: pasan LocalDate e Instant a los tipos que entiende el controlador JDBC;
 *   getDate(…).toLocalDate() y getTimestamp(…).toInstant() hacen el camino de vuelta. getObject(…, UUID.class) lee una
 *   columna uuid de PostgreSQL directamente como UUID.
 * - SELECT … FOR UPDATE: bloquea la fila leída hasta el fin de la transacción del caso de uso; una segunda captura del
 *   mismo hecho espera su turno en lugar de comparar contra un vigente viejo. Si la fila aún no existe no hay nada
 *   que bloquear, y dos primeras capturas a la vez se resuelven en el INSERT de abajo.
 * - Optional<HechoDto>: «puede que haya, puede que no»; vacío cuando todavía no hay hecho vigente.
 * - INSERT … ON CONFLICT (llave) DO UPDATE SET … = EXCLUDED.…: «upsert» de PostgreSQL; si ya existe la fila con esa
 *   llave primaria la actualiza con los valores que se intentaban insertar (EXCLUDED es esa fila rechazada).
 * - ON CONFLICT (anotacion_uuid) DO NOTHING: abrir dos veces el mismo trabajo no falla ni duplica (el evento puede
 *   repetirse).
 * - t.codigos().toArray(String[]::new) y (String[]) f.getArray("codigos").getArray(): la columna codigos es text[]
 *   (arreglo de PostgreSQL); se envía como arreglo Java y se lee con getArray, que devuelve Object y hay que convertir.
 * - ORDER BY abierto_en, anotacion_uuid LIMIT :maximo: los más viejos primero, con un orden estable en empates, y no
 *   más de lo que cabe en pantalla.
 * - UPDATE … WHERE … AND resuelto_en IS NULL y update() == 1: solo cierra un trabajo abierto; si otro gerente ya lo
 *   cerró cambia 0 filas y el método devuelve false.
 */
