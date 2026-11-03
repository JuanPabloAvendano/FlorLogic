package co.florlogic.finca.ciclo.dominio;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/** Gana la captura más reciente (RF-022); en empate, el id mayor, para no depender del orden de llegada. */
class HechoDeProduccionTest {

    private static final Instant T = Instant.parse("2026-10-16T06:00:00Z");

    @Test
    void la_mas_reciente_reemplaza_y_la_vieja_no() {
        HechoDeProduccion vieja = hecho("40", T, "0192f0a0-2000-7000-8000-000000000001");
        HechoDeProduccion nueva = hecho("41", T.plusSeconds(60), "0192f0a0-2000-7000-8000-000000000002");

        assertThat(nueva.reemplazaA(vieja)).isTrue();
        assertThat(vieja.reemplazaA(nueva)).isFalse();
    }

    @Test
    void en_empate_gana_el_id_mayor() {
        HechoDeProduccion a = hecho("40", T, "0192f0a0-2000-7000-8000-000000000001");
        HechoDeProduccion b = hecho("41", T, "0192f0a0-2000-7000-8000-000000000002");

        assertThat(b.reemplazaA(a)).isTrue();
        assertThat(a.reemplazaA(b)).isFalse();
    }

    private static HechoDeProduccion hecho(String valor, Instant en, String anotacion) {
        return new HechoDeProduccion("P-2026-031", "B3-C17-S1", "tallos_cortados", LocalDate.parse("2026-10-16"), valor, null,
                en, UUID.fromString(anotacion), UUID.randomUUID());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Test (JUnit 5): marca cada método como una prueba que corre ./mvnw verify; el nombre del método dice la regla.
 * - import static …Assertions.assertThat (AssertJ): assertThat(x).isTrue() o .isFalse() comprueba el resultado y,
 *   si falla, el mensaje muestra lo esperado y lo obtenido.
 * - Instant.parse("2026-10-16T06:00:00Z") y T.plusSeconds(60): un instante fijo en UTC (la Z) y otro un minuto
 *   después; así la prueba no depende de la hora en que se corre.
 * - UUID.fromString(…): ids escritos a mano que solo difieren en el último dígito, para saber cuál es el mayor en el
 *   empate. UUID.randomUUID() para el celular, que aquí no importa.
 */
