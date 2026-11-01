package co.florlogic.finca.ingesta.servicios.input.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.comun.AnotacionId;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.RecibirParticionSolicitudDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionRespuestaDto;

/**
 * El adaptador solo orquesta: solicitud → ensamblador → caso de uso → ensamblador → respuesta. El caso de uso es un doble
 * que anota lo que recibe (Spy) y devuelve un valor fijo. Sin Spring, sin base.
 */
class RecibirParticionInputAdapterTest {

    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");
    private static final UUID D001 = UUID.fromString("0192f0a0-2000-7000-8000-00000000d001");

    private final List<UUID> sesiones = new ArrayList<>();
    private final List<AnotacionRecibida> recibidas = new ArrayList<>();
    private final RecibirParticionInputAdapter adaptador = new RecibirParticionInputAdapter(
            (sesion, anotaciones) -> {
                sesiones.add(sesion);
                recibidas.addAll(anotaciones);
                return new RecepcionDeParticionValor(3, 2, 1, 7);
            });

    @Test
    void el_caso_de_uso_recibe_la_sesion_y_las_anotaciones_ya_como_dominio() {
        adaptador.ejecutar(new RecibirParticionSolicitudDto(SESION,
                List.of(new RecibirParticionSolicitudDto.Anotacion(D001, 1, "{\"a\":1}"))));

        assertThat(sesiones).containsExactly(SESION);
        assertThat(recibidas).containsExactly(
                new AnotacionRecibida(new AnotacionId(D001), new ConsecutivoValor(1), SESION, "{\"a\":1}"));
    }

    @Test
    void el_response_sale_de_lo_que_devuelve_el_caso_de_uso() {
        assertThat(adaptador.ejecutar(new RecibirParticionSolicitudDto(SESION, List.of())))
                .contains(new RecibirParticionRespuestaDto(3, 2, 1, 7));
    }

    @Test
    void si_el_dominio_rechaza_una_anotacion_el_caso_de_uso_ni_se_llama() {
        // C-105: consecutivo 0 → vacío (422) y nada llega al caso de uso, así nada se guarda ni se mueve el avance.
        assertThat(adaptador.ejecutar(new RecibirParticionSolicitudDto(SESION,
                List.of(new RecibirParticionSolicitudDto.Anotacion(D001, 0, "{}"))))).isEmpty();
        assertThat(sesiones).isEmpty();
    }
}
