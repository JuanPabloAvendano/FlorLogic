package co.florlogic.finca.ingesta.infraestructura.input.controladores;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import co.florlogic.contrato.dto.ResultadoParticionDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionResultadoDto;

/** {@code recibido_ms} sale del reloj del nodo que recibe el controlador, no de uno propio (A-45). */
class ParticionesControladorTest {

    private static final Instant AHORA = Instant.parse("2026-11-03T15:00:00Z");

    @Test
    void recibido_ms_es_la_hora_del_reloj_del_nodo() throws IOException {
        ParticionesControlador controlador = new ParticionesControlador(
                solicitud -> new RecibirParticionResultadoDto.Recibida(
                        new RecibirParticionRespuestaDto(2, 2, 0, 0, 0, 2, List.of())),
                Clock.fixed(AHORA, ZoneOffset.UTC));

        ResponseEntity<?> respuesta = controlador.recibir("0192f0a0-1000-7000-8000-00000000c001", dorada(),
                () -> "op-07");

        assertThat(respuesta.getStatusCode().value()).isEqualTo(200);
        assertThat(((ResultadoParticionDto) respuesta.getBody()).recibidoMs()).isEqualTo(AHORA.toEpochMilli());
    }

    private static byte[] dorada() throws IOException {
        try (InputStream entrada = ParticionesControladorTest.class.getResourceAsStream("/canal/peticion-particion.json")) {
            return entrada.readAllBytes();
        }
    }
}
