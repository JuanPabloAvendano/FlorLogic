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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Repository (Spring): registra la clase como bean y traduce los errores de SQL a las excepciones de Spring (DataAccessException). La clase y su constructor no son public: nadie fuera del paquete la nombra; Spring la entrega a quien pide el puerto CatalogoRepositorio.
 * - JdbcClient (Spring JDBC): API encadenada para SQL: sql(...) recibe el texto, param("nombre", valor) llena cada :nombre (parámetros con nombre, sin concatenar texto, así no hay inyección SQL), y query(...) o update() ejecuta.
 * - query((fila, n) -> new FormaTipoDto(...)).optional(): convierte la fila en DTO; optional() da vacío si no hay fila
 *   para ese tipo y versión.
 * - (String[]) fila.getArray("campos").getArray(): la columna campos es un arreglo de PostgreSQL; getArray da un
 *   java.sql.Array, su getArray() el arreglo de Java, el cast lo trata como String[] y Arrays.asList lo vuelve lista.
 * - COALESCE(MAX(version), 0) ... WHERE vigente: MAX da NULL si ninguna versión está vigente; COALESCE lo cambia por 0,
 *   que es lo que promete el puerto.
 * - query(Integer.class).single(): lee exactamente un valor entero (la consulta siempre devuelve una fila).
 * - @Override: le pide al compilador comprobar que el método sí implementa uno de la interfaz; si la firma cambia, no compila.
 */
