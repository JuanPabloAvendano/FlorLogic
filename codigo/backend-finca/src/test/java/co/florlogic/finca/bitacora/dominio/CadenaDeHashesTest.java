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
