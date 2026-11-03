package co.florlogic.finca.reglas.infraestructura.output.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.reglas.dominio.Presencia;
import co.florlogic.finca.reglas.servicios.output.dto.FormaDto;

/** El lector de {@code datos}: null ≡ ausente, motivo en blanco no cuenta, y lo ilegible nunca lanza. */
class LectorDeContenidoJacksonTest {

    private static final FormaDto FORMA = new FormaDto(List.of("valor"), "motivo_ausencia");
    private final LectorDeContenidoJackson lector = new LectorDeContenidoJackson();

    @Test
    void un_valor_presente_es_hay_y_uno_null_o_ausente_es_falta() {
        assertThat(lector.leer("{\"datos\":{\"valor\":40}}", FORMA).campos()).containsEntry("valor", Presencia.HAY);
        assertThat(lector.leer("{\"datos\":{\"valor\":null}}", FORMA).campos()).containsEntry("valor", Presencia.FALTA);
        assertThat(lector.leer("{\"datos\":{}}", FORMA).campos()).containsEntry("valor", Presencia.FALTA);
    }

    @Test
    void el_motivo_cuenta_solo_si_no_esta_en_blanco() {
        assertThat(lector.leer("{\"datos\":{\"motivo_ausencia\":\"lluvia\"}}", FORMA).hayMotivo()).isTrue();
        assertThat(lector.leer("{\"datos\":{\"motivo_ausencia\":\"  \"}}", FORMA).hayMotivo()).isFalse();
    }

    @Test
    void sin_objeto_datos_o_con_json_roto_es_invalido() {
        assertThat(lector.leer("{\"datos\":5}", FORMA).datosValidos()).isFalse();
        assertThat(lector.leer("{roto", FORMA).datosValidos()).isFalse();
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Test (JUnit 5): marca cada método como una prueba que corre ./mvnw verify.
 * - new LectorDeContenidoJackson(): la prueba crea el adaptador a mano, sin arrancar Spring; puede hacerlo porque está
 *   en el mismo paquete que la clase (que no es public).
 * - "{\"datos\":…}": JSON escrito dentro de un texto Java; cada comilla del JSON va escapada con barra invertida.
 * - assertThat(…).containsEntry("valor", Presencia.HAY) (AssertJ): exige que el mapa tenga esa llave con ese valor;
 *   isTrue / isFalse revisan un booleano.
 */
