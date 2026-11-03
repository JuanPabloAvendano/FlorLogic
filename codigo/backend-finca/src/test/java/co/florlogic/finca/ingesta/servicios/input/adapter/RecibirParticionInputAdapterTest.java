package co.florlogic.finca.ingesta.servicios.input.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.dominio.recepcion.ResultadoDeRecepcion;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.RecibirParticionSolicitudDto;

/** El adaptador solo orquesta: solicitud → ensamblador → caso de uso → ensamblador → resultado. Sin Spring. */
class RecibirParticionInputAdapterTest {

    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");
    private static final UUID D001 = UUID.fromString("0192f0a0-2000-7000-8000-00000000d001");

    private final List<AnotacionRecibida> recibidas = new ArrayList<>();
    private final RecibirParticionInputAdapter adaptador = new RecibirParticionInputAdapter((sesion, persona, anotaciones) -> {
        recibidas.addAll(anotaciones);
        return new ResultadoDeRecepcion.Recibida(new RecepcionDeParticionValor(3, 2, 0, 1, 0, 7, List.of()));
    });

    @Test
    void el_caso_de_uso_recibe_las_anotaciones_ya_como_dominio_y_el_resultado_sale_traducido() {
        RecibirParticionResultadoDto r = adaptador.ejecutar(solicitud(D001, 1));

        assertThat(recibidas).singleElement().satisfies(a -> assertThat(a.anotacionId().valor()).isEqualTo(D001));
        assertThat(r).isEqualTo(new RecibirParticionResultadoDto.Recibida(
                new RecibirParticionRespuestaDto(3, 2, 0, 1, 0, 7, List.of())));
    }

    @Test
    void si_el_dominio_rechaza_una_anotacion_el_caso_de_uso_ni_se_llama() {
        // C-105: consecutivo 0 → 422 y nada llega al caso de uso, así nada se guarda ni se mueve el avance.
        assertThat(adaptador.ejecutar(solicitud(D001, 0))).isInstanceOf(RecibirParticionResultadoDto.AnotacionInvalida.class);
        assertThat(recibidas).isEmpty();
    }

    private static RecibirParticionSolicitudDto solicitud(UUID id, long consecutivo) {
        return new RecibirParticionSolicitudDto(SESION, "op-07",
                List.of(new RecibirParticionSolicitudDto.Anotacion(id, consecutivo, "captura.campo", 1, false, "{}")));
    }
}
