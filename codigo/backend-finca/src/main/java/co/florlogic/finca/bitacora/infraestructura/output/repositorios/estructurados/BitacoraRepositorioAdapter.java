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
