package co.florlogic.finca.ingesta.infraestructura.output.repositorios.estructurados;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDTO;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AnotacionRepositorio;

/**
 * Adaptador de salida con {@code JdbcClient} (TEC-07; plantilla adaptador-persistencia 10.3).
 * {@code ON CONFLICT DO NOTHING} hace el reenvío inofensivo en una sola sentencia, sin leer antes: si dos
 * peticiones llegan a la vez con la misma anotación, PostgreSQL deja entrar solo una. Solo ve el DTO de salida, nunca
 * el dominio (Martin · Clean Architecture p. 195).
 */
@Repository
class AnotacionRepositorioAdapter implements AnotacionRepositorio {

    private final JdbcClient jdbc;

    AnotacionRepositorioAdapter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean guardarSiNueva(AnotacionRecibidaDTO anotacion) {
        int insertadas = jdbc.sql("""
                        INSERT INTO anotacion_recibida (anotacion_uuid, consecutivo, sesion_id, contenido)
                        VALUES (:uuid, :consecutivo, :sesion, CAST(:contenido AS jsonb))
                        ON CONFLICT (anotacion_uuid) DO NOTHING""")
                .param("uuid", anotacion.anotacionUuid())
                .param("consecutivo", anotacion.consecutivo())
                .param("sesion", anotacion.sesionId())
                .param("contenido", anotacion.contenidoJson())
                .update();
        return insertadas == 1;
    }
}
