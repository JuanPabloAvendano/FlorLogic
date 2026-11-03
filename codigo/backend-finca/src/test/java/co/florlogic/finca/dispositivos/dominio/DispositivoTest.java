package co.florlogic.finca.dispositivos.dominio;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/** Un celular nace pendiente; el administrador lo aprueba o lo bloquea y queda quién y cuándo (CT-10). */
class DispositivoTest {

    @Test
    void nace_pendiente_y_el_administrador_decide() {
        Instant t0 = Instant.parse("2026-10-31T12:00:00Z");
        Dispositivo d = Dispositivo.visto(UUID.randomUUID(), t0);
        assertThat(d.estado()).isEqualTo(Dispositivo.Estado.PENDIENTE);

        d.aprobar("admin-1", t0.plusSeconds(10));
        assertThat(d.estado()).isEqualTo(Dispositivo.Estado.APROBADO);
        assertThat(d.cambiadoPor()).isEqualTo("admin-1");

        d.bloquear("admin-2", t0.plusSeconds(20));
        assertThat(d.estado()).isEqualTo(Dispositivo.Estado.BLOQUEADO);
        assertThat(d.cambiadoEn()).isEqualTo(t0.plusSeconds(20));
        assertThat(d.vistoPorPrimeraVez()).isEqualTo(t0);
    }
}
