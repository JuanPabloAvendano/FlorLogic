package co.florlogic.finca.ciclo.infraestructura.input.controladores;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ciclo.servicios.input.dto.solicitud.AnotacionAceptadaSolicitudDto;
import co.florlogic.finca.ingesta.AnotacionAceptada;

/** Cómo el oyente lee el JSON de una captura: el valor tal como lo anotaron y la hora corregida, o nada si falta. */
class AnotacionAceptadaOyenteTest {

    private static final UUID ANOTACION = UUID.fromString("0192f0a0-2000-7000-8000-00000000d001");
    private static final UUID CELULAR = UUID.fromString("0192f0a0-1000-7000-8000-0000000000c1");

    private final List<AnotacionAceptadaSolicitudDto> registradas = new ArrayList<>();
    private final AnotacionAceptadaOyente oyente = new AnotacionAceptadaOyente(registradas::add);

    @Test
    void un_valor_de_texto_se_guarda_sin_comillas() {
        oyente.al(evento("\"rojo\"", "\"crudo_ms\": 1792130531000"));

        assertThat(registradas).singleElement().extracting(AnotacionAceptadaSolicitudDto::valor).isEqualTo("rojo");
    }

    @Test
    void un_valor_numerico_se_guarda_como_su_numero() {
        oyente.al(evento("40", "\"crudo_ms\": 1792130531000"));

        assertThat(registradas).singleElement().extracting(AnotacionAceptadaSolicitudDto::valor).isEqualTo("40");
    }

    @Test
    void la_hora_es_la_del_celular_menos_el_desfase_de_su_sesion() {
        oyente.al(evento("40", "\"crudo_ms\": 1792130531000"));

        assertThat(registradas).singleElement().extracting(AnotacionAceptadaSolicitudDto::capturadoEn)
                .isEqualTo(Instant.ofEpochMilli(1792130531000L + 1200));
    }

    @Test
    void sin_hora_del_celular_no_se_registra_nada() {
        oyente.al(evento("40", "\"mono_ms\": 8123400"));

        assertThat(registradas).isEmpty();
    }

    private static AnotacionAceptada evento(String valor, String tiempo) {
        String json = """
                {"tiempo": {%s},
                 "datos": {"produccion_id": "P-2026-031", "seccion_id": "B3-C17-S1", "campo_id": "color",
                           "jornada": "2026-10-16", "valor": %s}}""".formatted(tiempo, valor);
        return new AnotacionAceptada(ANOTACION, CELULAR, "captura.campo", 1, json, List.of(), -1200);
    }
}
