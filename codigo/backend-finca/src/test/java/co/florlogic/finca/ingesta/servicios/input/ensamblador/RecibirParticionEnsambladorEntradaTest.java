package co.florlogic.finca.ingesta.servicios.input.ensamblador;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.comun.AnotacionId;
import co.florlogic.finca.ingesta.dominio.comun.CodigoMarca;
import co.florlogic.finca.ingesta.dominio.comun.MotivoRechazo;
import co.florlogic.finca.ingesta.dominio.recepcion.JuicioDeAnotacion;
import co.florlogic.finca.ingesta.dominio.recepcion.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.RecibirParticionSolicitudDto;

/** El ensamblador de entrada solo traduce: solicitud → dominio (o vacío si el dominio la rechaza) y dominio → respuesta. */
class RecibirParticionEnsambladorEntradaTest {

    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");
    private static final UUID D001 = UUID.fromString("0192f0a0-2000-7000-8000-00000000d001");

    @Test
    void cada_anotacion_de_la_solicitud_se_vuelve_dominio() {
        assertThat(RecibirParticionEnsambladorEntrada.aDominio(solicitud(D001, 1))).hasValueSatisfying(dominio ->
                assertThat(dominio).singleElement().satisfies(a -> {
                    assertThat(a.anotacionId().valor()).isEqualTo(D001);
                    assertThat(a.consecutivo().valor()).isEqualTo(1);
                    assertThat(a.tipo()).isEqualTo("captura.campo");
                }));
    }

    @Test
    void un_id_que_no_es_uuid_v7_o_un_consecutivo_menor_que_1_rechaza_toda_la_solicitud() {
        // C-105, A-108: la regla vive en el dominio (AnotacionId, ConsecutivoValor) y aquí sale como vacío.
        assertThat(RecibirParticionEnsambladorEntrada.aDominio(solicitud(UUID.fromString("0192f0a0-2000-4000-8000-00000000d001"), 1))).isEmpty();
        assertThat(RecibirParticionEnsambladorEntrada.aDominio(solicitud(D001, 0))).isEmpty();
    }

    @Test
    void la_respuesta_lleva_los_conteos_y_las_excepciones_en_minuscula() {
        RecepcionDeParticionValor recepcion = new RecepcionDeParticionValor(2, 1, 1, 1, 0, 2, List.of(
                new JuicioDeAnotacion(new AnotacionId(D001), co.florlogic.finca.ingesta.dominio.comun.Resultado.MARCADA,
                        Set.of(CodigoMarca.INCOMPLETA), Optional.empty()),
                JuicioDeAnotacion.rechazada(new AnotacionId(D001), MotivoRechazo.TIPO_DESCONOCIDO)));

        RecibirParticionRespuestaDto r = RecibirParticionEnsambladorEntrada.aRespuesta(recepcion);

        assertThat(r.excepciones()).containsExactly(
                new RecibirParticionRespuestaDto.Excepcion(D001, "marcada", List.of("incompleta")),
                new RecibirParticionRespuestaDto.Excepcion(D001, "rechazada", List.of("tipo_desconocido")));
    }

    private static RecibirParticionSolicitudDto solicitud(UUID id, long consecutivo) {
        return new RecibirParticionSolicitudDto(SESION, "op-07",
                List.of(new RecibirParticionSolicitudDto.Anotacion(id, consecutivo, "captura.campo", 1, false, "{}")));
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Test: cada método así es un caso que JUnit 5 corre por separado.
 * - assertThat(Optional).hasValueSatisfying(...) / isEmpty() (AssertJ): revisa lo que trae el Optional.
 * - singleElement().satisfies(...) y containsExactly(...): la lista tiene un solo elemento que cumple, o esos
 *   elementos en ese orden.
 */
