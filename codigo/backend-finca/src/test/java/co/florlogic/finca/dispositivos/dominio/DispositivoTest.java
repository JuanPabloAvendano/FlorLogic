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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Test (JUnit 5): marca el método como una prueba que corre ./mvnw verify.
 * - assertThat(x).isEqualTo(y) (AssertJ): compara y, si falla, muestra lo esperado y lo obtenido.
 * - Instant.parse("2026-10-31T12:00:00Z") y t0.plusSeconds(…): horas fijas en UTC para que la prueba no dependa del
 *   reloj real.
 */
