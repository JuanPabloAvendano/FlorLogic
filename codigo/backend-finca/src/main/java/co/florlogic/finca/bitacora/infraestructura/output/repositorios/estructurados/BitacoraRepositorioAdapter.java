package co.florlogic.finca.bitacora.infraestructura.output.repositorios.estructurados;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import co.florlogic.finca.bitacora.servicios.output.dto.EntradaDto;
import co.florlogic.finca.bitacora.servicios.output.ports.repositorios.BitacoraRepositorio;

/**
 * La bitácora en PostgreSQL (CT-11). El turno es un candado consultivo de transacción: se suelta solo al terminar,
 * así dos registros a la vez quedan en fila y la cadena no se bifurca. El tiempo se guarda en microsegundos, que es lo
 * que PostgreSQL conserva; por eso el reloj se trunca antes de calcular el hash.
 */
@Repository
class BitacoraRepositorioAdapter implements BitacoraRepositorio {

    private static final long TURNO = 0x466C6F72426974L;   // «FlorBit»

    private final JdbcClient jdbc;

    BitacoraRepositorioAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String ultimoHashConTurno() {
        jdbc.sql("SELECT pg_advisory_xact_lock(:turno)").param("turno", TURNO).query().singleRow();
        return jdbc.sql("SELECT hash FROM bitacora ORDER BY id DESC LIMIT 1").query(String.class).optional().orElse(null);
    }

    @Override
    public void insertar(EntradaDto e) {
        jdbc.sql("""
                        INSERT INTO bitacora (momento, actor, evento, objeto, detalle, hash_anterior, hash)
                        VALUES (:momento, :actor, :evento, :objeto, :detalle, :anterior, :hash)""")
                .param("momento", Timestamp.from(e.momento()))
                .param("actor", e.actor())
                .param("evento", e.evento())
                .param("objeto", e.objeto())
                .param("detalle", e.detalle())
                .param("anterior", e.hashAnterior())
                .param("hash", e.hash())
                .update();
    }

    @Override
    public List<EntradaDto> todas() {
        return jdbc.sql("SELECT * FROM bitacora ORDER BY id").query((f, n) -> new EntradaDto(f.getTimestamp("momento").toInstant(),
                f.getString("actor"), f.getString("evento"), f.getString("objeto"), f.getString("detalle"),
                f.getString("hash_anterior"), f.getString("hash"))).list();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Repository (Spring): registra la clase como bean y traduce los errores de SQL a las excepciones de Spring (DataAccessException). La clase y su constructor no son public: nadie fuera del paquete la nombra; Spring la entrega a quien pide el puerto BitacoraRepositorio.
 * - JdbcClient (Spring JDBC): API encadenada para SQL: sql(...) recibe el texto, param("nombre", valor) llena cada :nombre (parámetros con nombre, sin concatenar texto, así no hay inyección SQL), y query(...) o update() ejecuta.
 * - 0x466C6F72426974L: número long escrito en hexadecimal (son los bytes ASCII de «FlorBit»); es la llave del candado.
 *   Otro código que use pg_advisory_xact_lock con otra llave no choca con este.
 * - SELECT pg_advisory_xact_lock(:turno) (PostgreSQL): toma el candado consultivo de transacción; si otra transacción
 *   ya lo tiene, espera. Se suelta solo con el commit o el rollback, sin código para soltarlo.
 * - .query().singleRow(): ejecuta la sentencia y lee su única fila; el valor no se usa, solo importa que corra.
 * - query(String.class).optional().orElse(null): lee una sola columna como String; optional() da un Optional vacío si
 *   la tabla no tiene filas, y orElse(null) lo vuelve null, que es lo que promete el puerto para la bitácora vacía.
 * - """ ... """: bloque de texto (String de varias líneas) para escribir el INSERT legible.
 * - Timestamp.from(Instant) y getTimestamp(...).toInstant(): pasan del Instant de Java al tipo fecha-hora de JDBC y
 *   de vuelta.
 * - query((f, n) -> new EntradaDto(...)).list(): lambda que convierte cada fila (f, con n su número) en un DTO;
 *   list() las junta en el orden del ORDER BY id.
 * - @Override: le pide al compilador comprobar que el método sí implementa uno de la interfaz; si la firma cambia, no compila.
 */
