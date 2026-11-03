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
