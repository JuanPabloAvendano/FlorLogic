package co.florlogic.finca.ingesta.dominio.avance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;

/**
 * La raíz {@link AvanceDelDispositivoAgregado} (ESPEC-dominio-ingesta A-60, A-72, A-73, A-78, A-81): INV-4, «{@code
 * confirmadoHasta} es el mayor contiguo y nunca baja», con un caso dado/cuando/entonces por escenario (C-90, C-91,
 * C-98, C-100, C-101, C-103). Java puro: sin Spring ni base.
 */
class AvanceDelDispositivoAgregadoTest {

    private static final DispositivoId CELULAR = new DispositivoId(
            UUID.fromString("0192f0a1-7c00-7000-8000-0000000000d1"));

    @Test
    void un_avance_nuevo_no_ha_recibido_nada_y_empieza_en_version_0() { // A-78, A-81
        AvanceDelDispositivoAgregado avance = AvanceDelDispositivoAgregado.nuevo(CELULAR);

        assertThat(avance.confirmadoHasta()).isEmpty();
        assertThat(avance.porEncima()).isEmpty();
        assertThat(avance.version()).isZero();
    }

    @Test
    void con_1_2_y_4_confirma_hasta_2_y_con_el_3_salta_a_4() { // C-90
        AvanceDelDispositivoAgregado avance = AvanceDelDispositivoAgregado.nuevo(CELULAR);

        registrar(avance, 1, 2, 4);
        assertThat(confirmado(avance)).isEqualTo(2);
        assertThat(avance.porEncima()).containsExactly(c(4));

        avance.registrar(c(3));
        assertThat(confirmado(avance)).isEqualTo(4);
        assertThat(avance.porEncima()).isEmpty();
    }

    @Test
    void registrar_otra_vez_un_numero_ya_confirmado_no_cambia_nada() { // C-91, C-100 (lado del avance)
        AvanceDelDispositivoAgregado avance = AvanceDelDispositivoAgregado.nuevo(CELULAR);
        registrar(avance, 1, 2, 3, 4);

        avance.registrar(c(2));
        avance.registrar(c(4));

        assertThat(confirmado(avance)).isEqualTo(4);
        assertThat(avance.porEncima()).isEmpty();
    }

    @Test
    void registrar_otra_vez_un_numero_por_encima_del_hueco_no_cambia_nada() { // C-91, DI1-8
        AvanceDelDispositivoAgregado avance = AvanceDelDispositivoAgregado.nuevo(CELULAR);
        registrar(avance, 1, 5, 5);

        assertThat(confirmado(avance)).isEqualTo(1);
        assertThat(avance.porEncima()).containsExactly(c(5));
    }

    @Test
    void despues_de_un_hueco_no_avanza_hasta_que_llega_lo_que_faltaba() { // C-98 (avance), A-72
        AvanceDelDispositivoAgregado avance = AvanceDelDispositivoAgregado.nuevo(CELULAR);
        LongStream.rangeClosed(1, 200).forEach(n -> avance.registrar(c(n)));

        LongStream.rangeClosed(401, 600).forEach(n -> avance.registrar(c(n)));
        assertThat(confirmado(avance)).isEqualTo(200);

        LongStream.rangeClosed(201, 400).forEach(n -> avance.registrar(c(n)));
        assertThat(confirmado(avance)).isEqualTo(600);
        assertThat(avance.porEncima()).isEmpty();
    }

    @Test
    void si_lo_primero_que_llega_es_el_3_sigue_sin_confirmar_hasta_el_1_y_el_2() { // C-101, A-78
        AvanceDelDispositivoAgregado avance = AvanceDelDispositivoAgregado.nuevo(CELULAR);

        avance.registrar(c(3));
        assertThat(avance.confirmadoHasta()).as("0 en el cable").isEmpty();

        registrar(avance, 1, 2);
        assertThat(confirmado(avance)).isEqualTo(3);
    }

    @Test
    void desde_carga_un_avance_que_cumple_inv_4() { // C-103
        Optional<AvanceDelDispositivoAgregado> avance = AvanceDelDispositivoAgregado.desde(CELULAR, ConsecutivoValor.de(5), conjunto(8), 7);

        assertThat(avance).hasValueSatisfying(a -> {
            assertThat(confirmado(a)).isEqualTo(5);
            assertThat(a.porEncima()).containsExactly(c(8));
            assertThat(a.version()).isEqualTo(7);
        });
    }

    @Test
    void desde_da_vacio_si_lo_guardado_rompe_inv_4() { // C-103, COH-130
        assertThat(AvanceDelDispositivoAgregado.desde(CELULAR, ConsecutivoValor.de(5), conjunto(6), 1))
                .as("el 6 debió subir el confirmado").isEmpty();
        assertThat(AvanceDelDispositivoAgregado.desde(CELULAR, ConsecutivoValor.de(5), conjunto(3), 1))
                .as("el 3 ya está confirmado").isEmpty();
        assertThat(AvanceDelDispositivoAgregado.desde(CELULAR, Optional.empty(), conjunto(1), 1))
                .as("sin confirmado, el 1 debió confirmarse").isEmpty();
    }

    @Test
    void desde_con_null_da_vacio_y_no_lanza() { // escenario 6, COH-134
        assertThat(AvanceDelDispositivoAgregado.desde(null, Optional.empty(), conjunto(), 0)).isEmpty();
        assertThat(AvanceDelDispositivoAgregado.desde(CELULAR, null, conjunto(), 0)).isEmpty();
        assertThat(AvanceDelDispositivoAgregado.desde(CELULAR, Optional.empty(), null, 0)).isEmpty();
    }

    @Test
    void lo_que_se_le_pasa_a_desde_no_cambia_el_avance_despues() { // E-16
        SortedSet<ConsecutivoValor> guardado = conjunto(8);
        AvanceDelDispositivoAgregado avance = AvanceDelDispositivoAgregado.desde(CELULAR, ConsecutivoValor.de(5), guardado, 0).orElseThrow();

        guardado.add(c(9));

        assertThat(avance.porEncima()).containsExactly(c(8));
    }

    @Test
    void por_encima_no_se_puede_modificar_desde_fuera() { // E-16, DI1-4
        AvanceDelDispositivoAgregado avance = AvanceDelDispositivoAgregado.nuevo(CELULAR);
        avance.registrar(c(3));

        assertThatThrownBy(() -> avance.porEncima().add(c(1))).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void un_consecutivo_en_el_maximo_de_long_no_desborda() { // A-80: registrar no lanza ante un valor válido
        AvanceDelDispositivoAgregado avance = AvanceDelDispositivoAgregado.desde(CELULAR, ConsecutivoValor.de(Long.MAX_VALUE - 1),
                conjunto(), 0).orElseThrow();

        avance.registrar(c(Long.MAX_VALUE));

        assertThat(confirmado(avance)).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void dos_avances_del_mismo_celular_son_iguales_aunque_vayan_distinto() { // E-09
        AvanceDelDispositivoAgregado a = AvanceDelDispositivoAgregado.nuevo(CELULAR);
        AvanceDelDispositivoAgregado b = AvanceDelDispositivoAgregado.desde(CELULAR, ConsecutivoValor.de(9), conjunto(), 3).orElseThrow();

        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
    }

    private static ConsecutivoValor c(long valor) {
        return new ConsecutivoValor(valor);
    }

    private static void registrar(AvanceDelDispositivoAgregado avance, long... numeros) {
        for (long n : numeros) {
            avance.registrar(c(n));
        }
    }

    private static long confirmado(AvanceDelDispositivoAgregado avance) {
        return avance.confirmadoHasta().orElseThrow().valor();
    }

    private static SortedSet<ConsecutivoValor> conjunto(long... numeros) {
        TreeSet<ConsecutivoValor> conjunto = new TreeSet<>();
        for (long n : numeros) {
            conjunto.add(c(n));
        }
        return conjunto;
    }
}
