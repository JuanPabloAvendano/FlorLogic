package co.florlogic.finca.ingesta.infraestructura.salida.persistencia;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.servicios.puertos.salida.AnotacionesRecibidas;

/**
 * Adaptador de salida con {@code JdbcClient} (TEC-07; plantilla adaptador-persistencia 10.3).
 * {@code ON CONFLICT DO NOTHING} hace el reenvío inofensivo en una sola sentencia, sin leer antes: si dos
 * peticiones llegan a la vez con la misma anotación, PostgreSQL deja entrar solo una.
 */
@Repository
class AnotacionesRecibidasJdbc implements AnotacionesRecibidas {

    private final JdbcClient jdbc;

    AnotacionesRecibidasJdbc(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean guardarSiNueva(AnotacionRecibida anotacion) {
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
