package co.florlogic.finca.ingesta.servicios.input.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.servicios.input.dto.request.RecibirParticionRequest;
import co.florlogic.finca.ingesta.servicios.input.dto.response.RecibirParticionResponse;

/**
 * El adaptador solo orquesta: request → ensamblador → caso de uso → ensamblador → response. El caso de uso es un doble
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
        adaptador.ejecutar(new RecibirParticionRequest(SESION,
                List.of(new RecibirParticionRequest.Anotacion(D001, 1, "{\"a\":1}"))));

        assertThat(sesiones).containsExactly(SESION);
        assertThat(recibidas).containsExactly(new AnotacionRecibida(D001, 1, SESION, "{\"a\":1}"));
    }

    @Test
    void el_response_sale_de_lo_que_devuelve_el_caso_de_uso() {
        RecibirParticionResponse response = adaptador.ejecutar(new RecibirParticionRequest(SESION, List.of()));

        assertThat(response).isEqualTo(new RecibirParticionResponse(3, 2, 1, 7));
    }
}
