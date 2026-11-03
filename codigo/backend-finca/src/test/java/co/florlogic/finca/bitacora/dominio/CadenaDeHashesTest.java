package co.florlogic.finca.bitacora.dominio;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/** La cadena de la bitácora: encadena desde el origen y cambiar una entrada vieja la rompe (CT-11, ESC-40). */
class CadenaDeHashesTest {

    private static final Instant T = Instant.parse("2026-10-31T12:00:00Z");

    @Test
    void una_cadena_bien_hecha_es_integra_y_una_tocada_no() {
        EntradaDeBitacora a = CadenaDeHashes.encadenar(CadenaDeHashes.ORIGEN, T, "op-07", "sesion_abierta", "s1", Map.of("k", "v"));
        EntradaDeBitacora b = CadenaDeHashes.encadenar(a.hash(), T.plusSeconds(1), "op-07", "sesion_cerrada", "s1", Map.of());
        assertThat(CadenaDeHashes.integra(List.of(a, b))).isTrue();

        EntradaDeBitacora aTocada = new EntradaDeBitacora(a.momento(), "otro-actor", a.evento(), a.objeto(), a.detalle(),
                a.hashAnterior(), a.hash());
        assertThat(CadenaDeHashes.integra(List.of(aTocada, b))).isFalse();
        assertThat(CadenaDeHashes.integra(List.of(b))).as("falta la primera").isFalse();
    }

    @Test
    void el_detalle_no_depende_del_orden_de_las_claves() {
        assertThat(CadenaDeHashes.detalle(Map.of("b", "2", "a", "1"))).isEqualTo("a=1\nb=2\n");
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Test (JUnit 5): marca el método como una prueba; JUnit lo corre en ./mvnw verify. La clase y los métodos no
 *   necesitan ser public.
 * - import static ...Assertions.assertThat: permite escribir assertThat(...) sin el nombre de la clase.
 * - assertThat(x).isTrue() / isFalse() / isEqualTo(y) (AssertJ): comprueba x y, si falla, explica qué esperaba.
 * - .as("falta la primera"): descripción que aparece en el mensaje si esa comprobación falla.
 * - Instant.parse("2026-10-31T12:00:00Z"): momento fijo en UTC (la Z) para que la prueba dé siempre lo mismo.
 * - Map.of(...) y Map.of(): mapas inmutables, con datos o vacío.
 * - CadenaDeHashes.detalle(...) no es public: la prueba lo alcanza porque está en el mismo paquete.
 */
