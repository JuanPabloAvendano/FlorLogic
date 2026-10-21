package co.florlogic.finca.ingesta.servicios.casosuso;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.AnotacionRecibida;
import co.florlogic.finca.ingesta.servicios.puertos.entrada.ParticionRecibidaResultado;
import co.florlogic.finca.ingesta.servicios.puertos.entrada.RecibirParticionComando;
import co.florlogic.finca.ingesta.servicios.puertos.salida.AnotacionesRecibidasDobleEnMemoria;

/**
 * El caso de uso solo, con el doble en memoria: base de la pirámide de ingesta (OBS-2-11; plantilla pruebas §0.7,
 * «cada regla en el nivel más bajo que pueda detectarla»). Sin Spring, sin base, sin HTTP.
 */
class RecibirParticionCasoUsoTest {

    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");

    private final AnotacionesRecibidasDobleEnMemoria anotaciones = new AnotacionesRecibidasDobleEnMemoria();
    private final RecibirParticionCasoUso servicio = new RecibirParticionCasoUso(anotaciones);

    @Test
    void las_nuevas_se_cuentan_como_aplicadas_y_se_guardan() {
        ParticionRecibidaResultado r = servicio.ejecutar(particion(anotacion("d001", 1), anotacion("d002", 2)));

        assertThat(r).isEqualTo(new ParticionRecibidaResultado(2, 2, 0, 2));
        assertThat(anotaciones.cuantas()).isEqualTo(2);
    }

    @Test
    void un_reenvio_cuenta_sin_efecto_y_no_guarda_otra_vez() {
        servicio.ejecutar(particion(anotacion("d001", 1), anotacion("d002", 2)));

        ParticionRecibidaResultado r = servicio.ejecutar(particion(anotacion("d001", 1), anotacion("d002", 2)));

        assertThat(r).isEqualTo(new ParticionRecibidaResultado(2, 0, 2, 2));
        assertThat(anotaciones.cuantas()).isEqualTo(2);
    }

    @Test
    void la_misma_anotacion_dos_veces_en_una_particion_entra_una_sola_vez() {
        ParticionRecibidaResultado r = servicio.ejecutar(particion(anotacion("d001", 1), anotacion("d001", 1)));

        assertThat(r).isEqualTo(new ParticionRecibidaResultado(2, 1, 1, 1));
    }

    @Test
    void el_mayor_consecutivo_no_depende_del_orden() {
        assertThat(servicio.ejecutar(particion(anotacion("d007", 7), anotacion("d003", 3))).mayorConsecutivo())
                .isEqualTo(7);
    }

    @Test
    void una_particion_sin_anotaciones_da_todo_en_cero() {
        assertThat(servicio.ejecutar(particion())).isEqualTo(new ParticionRecibidaResultado(0, 0, 0, 0));
    }

    private static RecibirParticionComando particion(AnotacionRecibida... anotaciones) {
        return new RecibirParticionComando(SESION, List.of(anotaciones));
    }

    private static AnotacionRecibida anotacion(String sufijo, long consecutivo) {
        UUID uuid = UUID.fromString("0192f0a0-2000-7000-8000-00000000" + sufijo);
        return new AnotacionRecibida(uuid, consecutivo, SESION, "{}");
    }
}
