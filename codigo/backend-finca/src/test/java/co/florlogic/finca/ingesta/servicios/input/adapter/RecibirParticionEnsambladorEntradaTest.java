package co.florlogic.finca.ingesta.servicios.input.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.servicios.input.dto.ParticionRecibidaRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.RecibirParticionEntradaDto;

/**
 * El ensamblador solo traduce: DTO de entrada → dominio → caso de uso → DTO de respuesta. El caso de uso es un doble
 * que anota lo que recibe (Spy) y devuelve un valor fijo. Sin Spring, sin base.
 */
class RecibirParticionEnsambladorEntradaTest {

    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");
    private static final UUID D001 = UUID.fromString("0192f0a0-2000-7000-8000-00000000d001");

    private final List<AnotacionRecibida> recibidas = new ArrayList<>();
    private final RecibirParticionEnsambladorEntrada ensamblador = new RecibirParticionEnsambladorEntrada(
            (sesion, anotaciones) -> {
                recibidas.addAll(anotaciones);
                return new RecepcionDeParticionValor(3, 2, 1, 7);
            });

    @Test
    void cada_anotacion_llega_al_caso_de_uso_como_dominio_con_la_sesion_de_la_entrada() {
        ensamblador.ejecutar(new RecibirParticionEntradaDto(SESION,
                List.of(new RecibirParticionEntradaDto.Anotacion(D001, 1, "{\"a\":1}"))));

        assertThat(recibidas).containsExactly(new AnotacionRecibida(D001, 1, SESION, "{\"a\":1}"));
    }

    @Test
    void la_respuesta_copia_los_conteos_del_caso_de_uso() {
        ParticionRecibidaRespuestaDto respuesta = ensamblador.ejecutar(new RecibirParticionEntradaDto(SESION, List.of()));

        assertThat(respuesta).isEqualTo(new ParticionRecibidaRespuestaDto(3, 2, 1, 7));
    }
}
