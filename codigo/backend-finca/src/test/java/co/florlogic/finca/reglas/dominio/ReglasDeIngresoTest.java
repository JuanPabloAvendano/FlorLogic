package co.florlogic.finca.reglas.dominio;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

/** Dato ausente y divergencia (A-99; contrato A-32, A-35). */
class ReglasDeIngresoTest {

    @Test
    void con_todo_y_sin_marca_local_no_hay_nada_que_marcar() {
        assertThat(ReglasDeIngreso.marcas(new CamposLeidos(true, Map.of("valor", Presencia.HAY), false), false)).isEmpty();
    }

    @Test
    void falta_un_valor_con_motivo_es_incompleta_y_sin_motivo_tambien_motivo_ausente() {
        assertThat(ReglasDeIngreso.marcas(new CamposLeidos(true, Map.of("valor", Presencia.FALTA), true), true))
                .containsExactly("incompleta");
        assertThat(ReglasDeIngreso.marcas(new CamposLeidos(true, Map.of("valor", Presencia.FALTA), false), true))
                .containsExactly("incompleta", "motivo_ausente");
    }

    @Test
    void si_el_celular_y_el_nodo_juzgan_distinto_es_divergencia() {
        assertThat(ReglasDeIngreso.marcas(new CamposLeidos(true, Map.of("valor", Presencia.HAY), false), true))
                .containsExactly("divergencia");
        assertThat(ReglasDeIngreso.marcas(new CamposLeidos(true, Map.of("valor", Presencia.FALTA), true), false))
                .containsExactly("divergencia", "incompleta");
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Test (JUnit 5): marca cada método como una prueba que corre ./mvnw verify; el nombre dice la regla.
 * - assertThat(…).isEmpty() y containsExactly(…) (AssertJ): containsExactly exige esos códigos y en ese orden; el
 *   orden alfabético viene del TreeSet de ReglasDeIngreso.
 * - Map.of("valor", Presencia.HAY): mapa inmutable de un solo campo, suficiente para cada caso.
 */
