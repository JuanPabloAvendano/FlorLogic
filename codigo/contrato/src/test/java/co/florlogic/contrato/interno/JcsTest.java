package co.florlogic.contrato.interno;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import tools.jackson.databind.json.JsonMapper;

/**
 * La forma canónica contra los vectores del RFC 8785 (apéndice B para los números, secc. 3.2.3 para el orden de las
 * claves) y la huella: mismo contenido → misma huella; null ≡ ausente.
 */
class JcsTest {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    @ParameterizedTest(name = "{0} → {1}")
    @CsvSource(delimiter = '|', value = {
        "0.0|0", "-0.0|0", "1.0|1", "-1.0|-1", "4.5|4.5", "0.002|0.002", "0.000001|0.000001", "1.0E-7|1e-7",
        "1.0E21|1e+21", "1.0E30|1e+30", "9.007199254740992E15|9007199254740992", "333333333.3333333|333333333.3333333",
        "1.0E20|100000000000000000000", "9.999999999999997E22|9.999999999999997e+22", "5.0E-324|5e-324",
        "1.7976931348623157E308|1.7976931348623157e+308", "295147905179352830000.0|295147905179352830000"
    })
    void los_numeros_se_escriben_como_ecmascript(double valor, String esperado) {
        assertThat(Jcs.numero(valor)).isEqualTo(esperado);
    }

    @Test
    void las_claves_van_en_orden_utf16_sin_espacios() {
        String json = "{\"b\": [1, 2.0, \"x\"], \"a\": {\"z\": true, \"\\u20ac\": null}, \"\\r\": \"\\u0001\"}";

        assertThat(Jcs.canonica(JSON.readTree(json)))
                .isEqualTo("{\"\\r\":\"\\u0001\",\"a\":{\"z\":true,\"€\":null},\"b\":[1,2,\"x\"]}");
    }

    @Test
    void mismo_contenido_con_otro_orden_y_otros_espacios_da_la_misma_huella() {
        String a = HuellaCanonica.de(JSON.readTree("{\"b\":1,\"a\":1.0}"));
        String b = HuellaCanonica.de(JSON.readTree("{ \"a\" : 1 , \"b\" : 1 }"));

        assertThat(a).isEqualTo(b).hasSize(64).matches("[0-9a-f]{64}");
    }

    @Test
    void null_es_lo_mismo_que_ausente_pero_no_dentro_de_un_arreglo() {
        assertThat(HuellaCanonica.de(JSON.readTree("{\"a\":1,\"b\":null}")))
                .isEqualTo(HuellaCanonica.de(JSON.readTree("{\"a\":1}")));
        assertThat(HuellaCanonica.de(JSON.readTree("{\"a\":[1,null]}")))
                .isNotEqualTo(HuellaCanonica.de(JSON.readTree("{\"a\":[1]}")));
    }

    @Test
    void otro_contenido_da_otra_huella() {
        assertThat(HuellaCanonica.de(JSON.readTree("{\"valor\":40}")))
                .isNotEqualTo(HuellaCanonica.de(JSON.readTree("{\"valor\":41}")));
    }

    @Test
    void la_huella_es_el_sha256_de_la_forma_canonica() {
        // sha256("{\"a\":1}") calculado aparte con sha256sum.
        assertThat(HuellaCanonica.de(JSON.readTree("{\"a\":1}")))
                .isEqualTo("015abd7f5cc57a2dd94b7590f04ad8084273905ee33ec5cebeae62276a97f862");
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @ParameterizedTest(name = "{0} → {1}") (JUnit): el método corre una vez por cada fila de datos; el name
 *   arma el título de cada corrida con los argumentos.
 * - @CsvSource(delimiter = '|', value = {…}): las filas de datos; cada texto «entrada|esperado» se parte por la
 *   barra y JUnit convierte cada parte al tipo del parámetro (double y String).
 * - @Test: un caso de prueba normal.
 * - assertThat(x).isEqualTo(y) (AssertJ): la comprobación; isNotEqualTo, hasSize y matches("[0-9a-f]{64}")
 *   (expresión regular: 64 cifras hexadecimales) van en la misma cadena.
 * - JsonMapper.builder().build(): el ObjectMapper de Jackson 3 para leer los JSON de prueba con readTree.
 * - Los vectores numéricos son los del apéndice B de RFC 8785; la huella esperada se calculó aparte con sha256sum.
 */
