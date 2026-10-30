package co.florlogic.finca.ingesta.servicios.input.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.servicios.input.dto.request.RecibirParticionRequest;
import co.florlogic.finca.ingesta.servicios.input.dto.response.RecibirParticionResponse;

/** El ensamblador solo traduce: request → dominio y dominio → response. Sin Spring, sin base. */
class RecibirParticionInputAssemblerTest {

    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");
    private static final UUID D001 = UUID.fromString("0192f0a0-2000-7000-8000-00000000d001");

    @Test
    void cada_anotacion_del_request_se_vuelve_dominio_con_la_sesion_del_request() {
        List<AnotacionRecibida> dominio = RecibirParticionInputAssembler.aDominio(new RecibirParticionRequest(SESION,
                List.of(new RecibirParticionRequest.Anotacion(D001, 1, "{\"a\":1}"))));

        assertThat(dominio).containsExactly(new AnotacionRecibida(D001, 1, SESION, "{\"a\":1}"));
    }

    @Test
    void el_response_copia_los_conteos_de_la_recepcion() {
        RecibirParticionResponse response = RecibirParticionInputAssembler.aResponse(new RecepcionDeParticionValor(3, 2, 1, 7));

        assertThat(response).isEqualTo(new RecibirParticionResponse(3, 2, 1, 7));
    }
}
