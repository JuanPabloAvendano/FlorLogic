package co.florlogic.finca.consulta.infraestructura.output.repositorios.estructurados;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import co.florlogic.finca.consulta.servicios.output.dto.ConteoPorCelularDto;
import co.florlogic.finca.consulta.servicios.output.dto.ConteoPorSeccionDto;
import co.florlogic.finca.consulta.servicios.output.ports.repositorios.TableroRepositorio;

/**
 * Los conteos del tablero en PostgreSQL con {@code JdbcClient} (TEC-07), una consulta por vista. Una celda asignada
 * cuenta como recibida si ya tiene hecho de producción ese día, lo haya capturado ese celular u otro: al gerente le
 * importa que el dato exista. Las marcadas cuentan también las ya resueltas: es lo que pasó en el día.
 */
@Repository
class TableroRepositorioAdapter implements TableroRepositorio {

    private final JdbcClient jdbc;

    TableroRepositorioAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<ConteoPorCelularDto> porCelular(LocalDate jornada, ZoneId zona) {
        return jdbc.sql("""
                        WITH esperado AS (
                            SELECT a.dispositivo_id, count(*) AS esperado,
                                   count(*) FILTER (WHERE EXISTS (SELECT 1 FROM hecho_produccion h
                                       WHERE h.seccion_id = a.seccion_id AND h.campo_id = a.campo_id
                                         AND h.jornada = a.jornada)) AS recibido
                            FROM asignacion a WHERE a.jornada = :j GROUP BY a.dispositivo_id),
                        marcadas AS (
                            SELECT dispositivo_id, count(*) AS n FROM trabajo_gerente WHERE jornada = :j
                            GROUP BY dispositivo_id),
                        rechazadas AS (
                            SELECT s.dispositivo_id, count(*) AS n
                            FROM anotacion_rechazada r JOIN sesion_sincronizacion s USING (sesion_id)
                            WHERE (r.recibido_en AT TIME ZONE :zona)::date = :j GROUP BY s.dispositivo_id),
                        parciales AS (
                            SELECT dispositivo_id, count(*) AS n FROM sesion_sincronizacion
                            WHERE estado = 'PARCIAL' AND (abierta_en AT TIME ZONE :zona)::date = :j
                            GROUP BY dispositivo_id),
                        celulares AS (
                            SELECT dispositivo_id FROM esperado UNION SELECT dispositivo_id FROM marcadas
                            UNION SELECT dispositivo_id FROM rechazadas UNION SELECT dispositivo_id FROM parciales)
                        SELECT c.dispositivo_id, coalesce(e.esperado, 0) AS esperado, coalesce(e.recibido, 0) AS recibido,
                               coalesce(m.n, 0) AS marcadas, coalesce(r.n, 0) AS rechazadas, coalesce(p.n, 0) AS parciales
                        FROM celulares c
                        LEFT JOIN esperado e USING (dispositivo_id) LEFT JOIN marcadas m USING (dispositivo_id)
                        LEFT JOIN rechazadas r USING (dispositivo_id) LEFT JOIN parciales p USING (dispositivo_id)
                        ORDER BY c.dispositivo_id""")
                .param("j", Date.valueOf(jornada)).param("zona", zona.getId())
                .query((f, n) -> new ConteoPorCelularDto(f.getObject("dispositivo_id", UUID.class), f.getInt("esperado"),
                        f.getInt("recibido"), f.getInt("marcadas"), f.getInt("rechazadas"), f.getInt("parciales")))
                .list();
    }

    @Override
    public List<ConteoPorSeccionDto> porSeccion(LocalDate jornada) {
        // Dos celulares con la misma celda la esperan una sola vez: DISTINCT.
        return jdbc.sql("""
                        WITH celdas AS (SELECT DISTINCT seccion_id, campo_id, jornada FROM asignacion WHERE jornada = :j),
                        esperado AS (
                            SELECT c.seccion_id, count(*) AS esperado,
                                   count(*) FILTER (WHERE EXISTS (SELECT 1 FROM hecho_produccion h
                                       WHERE h.seccion_id = c.seccion_id AND h.campo_id = c.campo_id
                                         AND h.jornada = c.jornada)) AS recibido
                            FROM celdas c GROUP BY c.seccion_id),
                        marcadas AS (
                            SELECT seccion_id, count(*) AS n FROM trabajo_gerente
                            WHERE jornada = :j AND seccion_id IS NOT NULL GROUP BY seccion_id),
                        secciones AS (SELECT seccion_id FROM esperado UNION SELECT seccion_id FROM marcadas)
                        SELECT s.seccion_id, coalesce(e.esperado, 0) AS esperado, coalesce(e.recibido, 0) AS recibido,
                               coalesce(m.n, 0) AS marcadas
                        FROM secciones s LEFT JOIN esperado e USING (seccion_id) LEFT JOIN marcadas m USING (seccion_id)
                        ORDER BY s.seccion_id""")
                .param("j", Date.valueOf(jornada))
                .query((f, n) -> new ConteoPorSeccionDto(f.getString("seccion_id"), f.getInt("esperado"),
                        f.getInt("recibido"), f.getInt("marcadas")))
                .list();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Repository (Spring): registra la clase como bean y traduce los errores de SQL a las excepciones de Spring (DataAccessException). La clase y su constructor no son public: nadie fuera del paquete la nombra; Spring la entrega a quien pide el puerto TableroRepositorio.
 * - JdbcClient (Spring JDBC): API encadenada para SQL: sql(...) recibe el texto, param("nombre", valor) llena cada :nombre (parámetros con nombre, sin concatenar texto, así no hay inyección SQL), y query(...) o update() ejecuta.
 * - """ ... """: bloques de texto (String de varias líneas) para escribir el SQL legible.
 * - WITH nombre AS ( ... ): subconsultas con nombre (CTE); cada una cuenta una cosa (esperado, marcadas, rechazadas,
 *   parciales) y la consulta final las une.
 * - count(*) FILTER (WHERE EXISTS (SELECT 1 ...)): de las celdas asignadas, cuenta solo las que ya tienen al menos un
 *   hecho_produccion con la misma sección, campo y jornada: ese es el «recibido».
 * - UNION (en celulares y secciones): junta sin repetir los celulares (o secciones) que aparecen en cualquier conteo;
 *   así sale también un celular que hoy solo tiene rechazadas o una sesión parcial.
 * - LEFT JOIN ... USING (dispositivo_id): une por la columna con ese nombre y conserva la fila aunque ese conteo no la
 *   tenga; coalesce(x, 0) cambia el NULL resultante por 0.
 * - (r.recibido_en AT TIME ZONE :zona)::date: pasa la hora guardada a la hora local de la finca y se queda con la
 *   fecha (::date es un cast de PostgreSQL); así se sabe de qué jornada es una rechazada o una sesión.
 * - Date.valueOf(jornada): convierte el LocalDate al java.sql.Date que espera el parámetro :j.
 * - zona.getId(): el nombre de la zona horaria (por ejemplo America/Bogota) como texto para :zona.
 * - f.getObject("dispositivo_id", UUID.class): lee la columna uuid directamente como UUID de Java.
 * - query((f, n) -> new ...Dto(...)).list(): lambda que convierte cada fila en un DTO; list() las junta en orden.
 * - @Override: le pide al compilador comprobar que el método sí implementa uno de la interfaz; si la firma cambia, no compila.
 */
