package co.florlogic.finca.ingesta.dominio.comun;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/**
 * Los ids y objetos de valor de {@code dominio.comun} (ESPEC-dominio-ingesta A-64, A-65, A-70): sus pares cumple/viola
 * de §4b y el escenario 6 («dato imprevisto → valor con nombre, nunca excepción»). Java puro: sin Spring ni base.
 */
class ValoresTest {

    private static final UUID V7 = UUID.fromString("0192f0a1-7d10-7000-8000-00000000b001");
    private static final UUID V4 = UUID.fromString("0192f0a1-7d10-4000-8000-00000000b001");
    private static final String HEX = "a".repeat(64);

    // ── ConsecutivoValor (A-65, A-70, INV-5) ──────────────────────────────────────────────────────────

    @Test
    void consecutivo_de_1_esta_presente() {
        assertThat(ConsecutivoValor.de(1)).contains(new ConsecutivoValor(1));
    }

    @Test
    void consecutivo_de_0_o_negativo_es_vacio_y_no_lanza() { // C-92
        assertThat(ConsecutivoValor.de(0)).isEmpty();
        assertThat(ConsecutivoValor.de(-5)).isEmpty();
        assertThat(ConsecutivoValor.de(Long.MIN_VALUE)).isEmpty();
    }

    @Test
    void el_constructor_de_consecutivo_lanza_ante_un_error_de_programacion() { // DI1-7
        assertThatThrownBy(() -> new ConsecutivoValor(0)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void el_siguiente_de_nada_es_1_y_el_de_un_numero_es_el_que_sigue() { // A-78, COH-123
        assertThat(ConsecutivoValor.siguienteDe(Optional.empty())).isEqualTo(new ConsecutivoValor(1));
        assertThat(ConsecutivoValor.siguienteDe(ConsecutivoValor.de(200))).isEqualTo(new ConsecutivoValor(201));
    }

    @Test
    void los_consecutivos_se_ordenan_por_su_numero() {
        assertThat(new ConsecutivoValor(2)).isLessThan(new ConsecutivoValor(10)).isEqualByComparingTo(new ConsecutivoValor(2));
    }

    // ── AnotacionId y DispositivoId (A-64) ───────────────────────────────────────────────────────

    @Test
    void anotacion_id_de_un_uuid_v7_esta_presente() {
        assertThat(AnotacionId.de(V7)).contains(new AnotacionId(V7));
    }

    @Test
    void anotacion_id_de_un_uuid_v4_o_null_es_vacio_y_no_lanza() {
        assertThat(AnotacionId.de(V4)).isEmpty();
        assertThat(AnotacionId.de(null)).isEmpty();
    }

    @Test
    void el_constructor_de_anotacion_id_lanza_ante_un_error_de_programacion() { // DI1-7
        assertThatThrownBy(() -> new AnotacionId(V4)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AnotacionId(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void dispositivo_id_acepta_cualquier_version_y_null_es_vacio() { // DI1-2
        assertThat(DispositivoId.de(V4)).contains(new DispositivoId(V4));
        assertThat(DispositivoId.de(null)).isEmpty();
    }

    // ── HuellaValor (A-65) ────────────────────────────────────────────────────────────────────────────

    @Test
    void huella_de_64_hex_en_minusculas_y_version_1_esta_presente() {
        assertThat(HuellaValor.de(1, HEX)).contains(new HuellaValor(1, HEX));
    }

    @Test
    void huella_mal_formada_es_vacia_y_no_lanza() { // par 4b de A-65
        assertThat(HuellaValor.de(1, "a".repeat(63))).as("63 caracteres").isEmpty();
        assertThat(HuellaValor.de(1, "A".repeat(64))).as("mayúsculas, DI1-6").isEmpty();
        assertThat(HuellaValor.de(1, "g".repeat(64))).as("fuera de hex").isEmpty();
        assertThat(HuellaValor.de(1, null)).as("null").isEmpty();
        assertThat(HuellaValor.de(0, HEX)).as("versión 0").isEmpty();
    }

    @Test
    void dos_huellas_son_iguales_solo_con_la_misma_version_y_el_mismo_hex() {
        assertThat(new HuellaValor(1, HEX)).isEqualTo(new HuellaValor(1, HEX))
                .isNotEqualTo(new HuellaValor(2, HEX))
                .isNotEqualTo(new HuellaValor(1, "b".repeat(64)));
    }

    // ── CapturaValor (A-65, E-16) ─────────────────────────────────────────────────────────────────────

    @Test
    void la_captura_no_cambia_si_cambia_el_mapa_de_origen() {
        Map<String, Presencia> origen = new HashMap<>(Map.of("tallos", Presencia.CON_VALOR));
        CapturaValor captura = CapturaValor.de("{\"tallos\":12}", origen).orElseThrow();

        origen.put("seccion_id", Presencia.AUSENTE_SIN_MOTIVO);

        assertThat(captura.campos()).containsOnlyKeys("tallos");
    }

    @Test
    void los_campos_de_la_captura_no_se_pueden_modificar() {
        CapturaValor captura = CapturaValor.de("{}", Map.of("tallos", Presencia.CON_VALOR)).orElseThrow();

        assertThatThrownBy(() -> captura.campos().put("x", Presencia.CON_VALOR))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void captura_con_null_en_cualquier_parte_es_vacia_y_no_lanza() { // escenario 6, DI1-5
        Map<String, Presencia> claveNula = new HashMap<>();
        claveNula.put(null, Presencia.CON_VALOR);
        Map<String, Presencia> valorNulo = new HashMap<>();
        valorNulo.put("tallos", null);

        assertThat(CapturaValor.de(null, Map.of())).as("json null").isEmpty();
        assertThat(CapturaValor.de("{}", null)).as("mapa null").isEmpty();
        assertThat(CapturaValor.de("{}", claveNula)).as("clave null").isEmpty();
        assertThat(CapturaValor.de("{}", valorNulo)).as("presencia null").isEmpty();
    }
}
