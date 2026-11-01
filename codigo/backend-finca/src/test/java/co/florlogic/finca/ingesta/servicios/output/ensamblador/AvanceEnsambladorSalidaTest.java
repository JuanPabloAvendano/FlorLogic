package co.florlogic.finca.ingesta.servicios.output.ensamblador;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.avance.AvanceDelDispositivoAgregado;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.servicios.output.dto.AvanceDto;

/** El ensamblador del avance solo traduce, en los dos sentidos; lo guardado que no es un avance válido sale vacío. */
class AvanceEnsambladorSalidaTest {

    private static final UUID CELULAR = UUID.fromString("0192f0a0-0000-7000-8000-0000000000e1");

    @Test
    void ida_y_vuelta_conserva_el_avance() {
        AvanceDto guardado = new AvanceDto(CELULAR, 2, List.of(4L, 6L), 3);

        AvanceDelDispositivoAgregado avance = AvanceEnsambladorSalida.aDominio(guardado).orElseThrow();

        assertThat(avance.confirmadoHasta()).contains(new ConsecutivoValor(2));
        assertThat(AvanceEnsambladorSalida.aDto(avance)).isEqualTo(guardado);
    }

    @Test
    void cero_es_nada_recibido() {
        AvanceDelDispositivoAgregado avance = AvanceEnsambladorSalida.aDominio(new AvanceDto(CELULAR, 0, List.of(), 1))
                .orElseThrow();

        assertThat(avance.confirmadoHasta()).isEmpty();
    }

    @Test
    void lo_guardado_que_no_es_un_avance_valido_sale_vacio() {
        // Negativo, un número por encima que ya debió subir confirmado_hasta (INV-4), y un 0 por encima.
        assertThat(AvanceEnsambladorSalida.aDominio(new AvanceDto(CELULAR, -1, List.of(), 1))).isEmpty();
        assertThat(AvanceEnsambladorSalida.aDominio(new AvanceDto(CELULAR, 2, List.of(3L), 1))).isEmpty();
        assertThat(AvanceEnsambladorSalida.aDominio(new AvanceDto(CELULAR, 2, List.of(0L), 1))).isEmpty();
    }
}
