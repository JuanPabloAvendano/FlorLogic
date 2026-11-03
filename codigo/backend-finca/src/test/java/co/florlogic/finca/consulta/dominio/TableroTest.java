package co.florlogic.finca.consulta.dominio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/** Esperado, recibido y falta del tablero (CT-13). */
class TableroTest {

    @Test
    void falta_y_porcentaje_hacia_abajo() {
        Avance a = new Avance(3, 2);
        assertThat(a.falta()).isEqualTo(1);
        assertThat(a.porcentaje()).isEqualTo(66);
        assertThat(a.completo()).isFalse();
        assertThat(new Avance(1000, 999).porcentaje()).as("99,9 % no es completo").isEqualTo(99);
        assertThat(new Avance(0, 0).porcentaje()).isEqualTo(100);
        assertThat(new Avance(0, 0).completo()).isTrue();
    }

    @Test
    void no_se_recibe_mas_de_lo_esperado_ni_negativo() {
        assertThatThrownBy(() -> new Avance(1, 2)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Avance(-1, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void el_total_suma_los_celulares_y_la_atencion_se_ve_por_fila() {
        Tablero.PorCelular alDia = new Tablero.PorCelular(UUID.randomUUID(), new Avance(4, 4), 0, 0, 0);
        Tablero.PorCelular conMarcas = new Tablero.PorCelular(UUID.randomUUID(), new Avance(6, 1), 2, 0, 0);
        Tablero.PorCelular conParcial = new Tablero.PorCelular(UUID.randomUUID(), new Avance(0, 0), 0, 0, 1);

        Tablero t = new Tablero(LocalDate.parse("2026-11-01"), List.of(alDia, conMarcas, conParcial), List.of());

        assertThat(t.total()).isEqualTo(new Avance(10, 5));
        assertThat(alDia.pideAtencion()).isFalse();
        assertThat(conMarcas.pideAtencion()).isTrue();
        assertThat(conParcial.pideAtencion()).isTrue();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Test (JUnit 5): marca el método como una prueba; JUnit lo corre en ./mvnw verify. La clase y los métodos no
 *   necesitan ser public.
 * - import static ...Assertions.*: permite escribir assertThat y assertThatThrownBy sin el nombre de la clase.
 * - assertThat(x).isEqualTo(y) / isTrue() / isFalse() (AssertJ): comprueba x y, si falla, explica qué esperaba.
 * - .as("99,9 % no es completo"): descripción que aparece en el mensaje si esa comprobación falla.
 * - assertThatThrownBy(() -> new Avance(1, 2)).isInstanceOf(...): ejecuta la lambda y comprueba que lance
 *   IllegalArgumentException; si no lanza, la prueba falla.
 * - UUID.randomUUID(): un identificador cualquiera para cada celular; la prueba no depende de su valor.
 */
