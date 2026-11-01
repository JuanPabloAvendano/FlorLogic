package co.florlogic.finca.ingesta.servicios.input.ensamblador;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.comun.AnotacionId;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.RecibirParticionSolicitudDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionRespuestaDto;

/** El ensamblador solo traduce: solicitud → dominio y dominio → respuesta. Sin Spring, sin base. */
class RecibirParticionEnsambladorEntradaTest {

    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");
    private static final UUID D001 = UUID.fromString("0192f0a0-2000-7000-8000-00000000d001");

    @Test
    void cada_anotacion_de_la_solicitud_se_vuelve_dominio_con_la_sesion_de_la_solicitud() {
        assertThat(RecibirParticionEnsambladorEntrada.aDominio(solicitud(D001, 1))).hasValueSatisfying(dominio ->
                assertThat(dominio).containsExactly(
                        new AnotacionRecibida(new AnotacionId(D001), new ConsecutivoValor(1), SESION, "{\"a\":1}")));
    }

    @Test
    void un_id_que_no_es_uuid_v7_o_un_consecutivo_menor_que_1_rechaza_toda_la_solicitud() {
        // C-105, A-108: la regla vive en el dominio (AnotacionId, ConsecutivoValor) y aquí sale como vacío.
        UUID v4 = UUID.fromString("0192f0a0-2000-4000-8000-00000000d001");
        assertThat(RecibirParticionEnsambladorEntrada.aDominio(solicitud(v4, 1))).isEmpty();
        assertThat(RecibirParticionEnsambladorEntrada.aDominio(solicitud(D001, 0))).isEmpty();
        assertThat(RecibirParticionEnsambladorEntrada.aDominio(solicitud(D001, -3))).isEmpty();
    }

    private static RecibirParticionSolicitudDto solicitud(UUID id, long consecutivo) {
        return new RecibirParticionSolicitudDto(SESION, List.of(new RecibirParticionSolicitudDto.Anotacion(id, consecutivo, "{\"a\":1}")));
    }

    @Test
    void el_response_copia_los_conteos_de_la_recepcion() {
        RecibirParticionRespuestaDto respuesta = RecibirParticionEnsambladorEntrada.aRespuesta(new RecepcionDeParticionValor(3, 2, 1, 7));

        assertThat(respuesta).isEqualTo(new RecibirParticionRespuestaDto(3, 2, 1, 7));
    }
}
