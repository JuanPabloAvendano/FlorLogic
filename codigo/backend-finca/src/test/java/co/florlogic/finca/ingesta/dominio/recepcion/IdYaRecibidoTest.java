package co.florlogic.finca.ingesta.dominio.recepcion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.comun.HuellaValor;

/** Los casos de idempotencia por id (ADR-027): nuevo, reenvío (misma huella) y corrupta (otra huella). */
class IdYaRecibidoTest {

    private static final HuellaValor A = new HuellaValor(1, "a".repeat(64));
    private static final HuellaValor B = new HuellaValor(1, "b".repeat(64));

    @Test
    void sin_huella_anterior_es_nueva_misma_es_reenvio_otra_es_corrupta() {
        assertThat(IdYaRecibido.ante(Optional.empty(), A)).isEmpty();
        assertThat(IdYaRecibido.ante(Optional.of(A), A)).contains(IdYaRecibido.Caso.REENVIO);
        assertThat(IdYaRecibido.ante(Optional.of(A), B)).contains(IdYaRecibido.Caso.CORRUPTA);
    }
}
