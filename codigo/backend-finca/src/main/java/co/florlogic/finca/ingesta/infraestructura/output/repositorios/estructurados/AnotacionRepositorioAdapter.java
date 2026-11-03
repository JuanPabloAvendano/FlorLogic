package co.florlogic.finca.ingesta.infraestructura.output.repositorios.estructurados;

import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRechazadaDto;
import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDto;
import co.florlogic.finca.ingesta.servicios.output.dto.GuardadaDto;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AnotacionRepositorio;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.ChoqueConcurrente;

/**
 * Adaptador de salida de las anotaciones con {@code JdbcClient} (TEC-07; plantilla adaptador-persistencia 10.3). Las
 * aceptadas y las rechazadas van en tablas aparte (§5 paso 5). Todo valor va con {@code .param(...)}: nunca se pega
 * texto en el SQL (OWASP A03).
 */
@Repository
class AnotacionRepositorioAdapter implements AnotacionRepositorio {

    private final JdbcClient jdbc;

    AnotacionRepositorioAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Map<UUID, GuardadaDto> guardadasCon(Collection<UUID> ids) {
        Map<UUID, GuardadaDto> guardadas = new HashMap<>();
        if (ids.isEmpty()) {
            return guardadas;
        }
        UUID[] arreglo = ids.toArray(UUID[]::new);
        // Primero las rechazadas y después las aceptadas: si un id tiene las dos, manda la aceptada (la primera llegada).
        jdbc.sql("""
                        SELECT anotacion_uuid, huella, motivo FROM anotacion_rechazada
                        WHERE anotacion_uuid = ANY(:ids) ORDER BY recibido_en""")
                .param("ids", arreglo)
                .query((fila, n) -> new GuardadaDto(fila.getObject("anotacion_uuid", UUID.class), fila.getString("huella"),
                        "RECHAZADA", List.of(fila.getString("motivo"))))
                .list().forEach(g -> guardadas.putIfAbsent(g.anotacionUuid(), g));
        jdbc.sql("""
                        SELECT anotacion_uuid, huella, resultado, codigos FROM anotacion_recibida
                        WHERE anotacion_uuid = ANY(:ids)""")
                .param("ids", arreglo)
                .query((fila, n) -> new GuardadaDto(fila.getObject("anotacion_uuid", UUID.class), fila.getString("huella"),
                        fila.getString("resultado"), textos(fila.getArray("codigos"))))
                .list().forEach(g -> guardadas.put(g.anotacionUuid(), g));
        return guardadas;
    }

    @Override
    public void guardarAceptada(AnotacionRecibidaDto anotacion) {
        try {
            jdbc.sql("""
                            INSERT INTO anotacion_recibida
                                (anotacion_uuid, consecutivo, sesion_id, tipo, version_tipo, huella, resultado, codigos, contenido)
                            VALUES (:uuid, :consecutivo, :sesion, :tipo, :versionTipo, :huella, :resultado, :codigos,
                                    CAST(:contenido AS jsonb))""")
                    .param("uuid", anotacion.anotacionUuid())
                    .param("consecutivo", anotacion.consecutivo())
                    .param("sesion", anotacion.sesionId())
                    .param("tipo", anotacion.tipo())
                    .param("versionTipo", anotacion.versionTipo())
                    .param("huella", anotacion.huella())
                    .param("resultado", anotacion.resultado())
                    .param("codigos", anotacion.codigos().toArray(String[]::new))
                    .param("contenido", anotacion.contenidoJson())
                    .update();
        } catch (DuplicateKeyException e) {
            throw new ChoqueConcurrente("La anotación " + anotacion.anotacionUuid() + " la guardó otra partición", e);
        }
    }

    @Override
    public void guardarRechazada(AnotacionRechazadaDto anotacion) {
        jdbc.sql("""
                        INSERT INTO anotacion_rechazada (anotacion_uuid, huella, sesion_id, consecutivo, motivo, contenido)
                        VALUES (:uuid, :huella, :sesion, :consecutivo, :motivo, CAST(:contenido AS jsonb))
                        ON CONFLICT (anotacion_uuid, huella) DO NOTHING""")
                .param("uuid", anotacion.anotacionUuid())
                .param("huella", anotacion.huella())
                .param("sesion", anotacion.sesionId())
                .param("consecutivo", anotacion.consecutivo())
                .param("motivo", anotacion.motivo())
                .param("contenido", anotacion.contenidoJson())
                .update();
    }

    private static List<String> textos(Array arreglo) throws SQLException {
        return arreglo == null ? List.of() : Arrays.asList((String[]) arreglo.getArray());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Repository: como @Component, Spring lo crea; además traduce los errores del driver JDBC a excepciones de
 *   Spring como DuplicateKeyException.
 * - JdbcClient: cliente SQL de Spring. jdbc.sql("…") da la sentencia, .param("nombre", valor) llena :nombre sin
 *   pegar texto en el SQL, .query(...).list() lee filas y .update() ejecuta y devuelve cuántas filas cambió.
 * - """ … """ (bloque de texto): cadena de varias líneas de Java; aquí guarda el SQL tal como se lee.
 * - (fila, n) -> new GuardadaDto(...): lambda que convierte cada fila (ResultSet) en un DTO; n es el número de
 *   fila. fila.getObject("anotacion_uuid", UUID.class) lee la columna uuid de PostgreSQL como UUID.
 * - anotacion_uuid = ANY(:ids) con ids.toArray(UUID[]::new): pasa los ids como un arreglo de PostgreSQL y busca los
 *   que estén en él, en una sola consulta.
 * - putIfAbsent y put: las rechazadas se cargan primero sin pisarse (la más antigua por ORDER BY recibido_en) y las
 *   aceptadas después pisan, por eso manda la aceptada.
 * - CAST(:contenido AS jsonb): el texto JSON se guarda como jsonb, el tipo JSON binario de PostgreSQL.
 * - ON CONFLICT (anotacion_uuid, huella) DO NOTHING: si ese rechazo ya está, no hace nada y no falla.
 * - catch (DuplicateKeyException e) → ChoqueConcurrente: la llave primaria repetida significa que otra partición
 *   guardó el mismo id primero.
 * - java.sql.Array + (String[]) arreglo.getArray(): la columna codigos es un arreglo de texto de PostgreSQL; se
 *   convierte a String[] y Arrays.asList lo vuelve lista. throws SQLException es válido porque el RowMapper lo
 *   admite.
 */
