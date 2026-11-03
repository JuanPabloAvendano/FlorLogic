package co.florlogic.finca.configuracion.infraestructura.output.repositorios.estructurados;

import java.util.Arrays;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import co.florlogic.finca.configuracion.servicios.output.dto.FormaTipoDto;
import co.florlogic.finca.configuracion.servicios.output.ports.repositorios.CatalogoRepositorio;

/** El catálogo en PostgreSQL con {@code JdbcClient} (A-88, TEC-07). */
@Repository
class CatalogoRepositorioAdapter implements CatalogoRepositorio {

    private final JdbcClient jdbc;

    CatalogoRepositorioAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<FormaTipoDto> forma(String tipo, int version) {
        return jdbc.sql("SELECT tipo, version, campos, motivo_ausencia FROM forma_tipo WHERE tipo = :tipo AND version = :version")
                .param("tipo", tipo)
                .param("version", version)
                .query((fila, n) -> new FormaTipoDto(fila.getString("tipo"), fila.getInt("version"),
                        Arrays.asList((String[]) fila.getArray("campos").getArray()), fila.getString("motivo_ausencia")))
                .optional();
    }

    @Override
    public int versionVigente() {
        return jdbc.sql("SELECT COALESCE(MAX(version), 0) FROM version_configuracion WHERE vigente")
                .query(Integer.class).single();
    }
}
