package co.florlogic.finca.ingesta.servicios.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.comun.AnotacionId;
import co.florlogic.finca.ingesta.dominio.comun.ConsecutivoValor;
import co.florlogic.finca.ingesta.dominio.recepcion.AnotacionRecibida;
import co.florlogic.finca.ingesta.dominio.recepcion.RecepcionDeParticionValor;
import co.florlogic.finca.ingesta.dominio.recepcion.ResultadoDeRecepcion;
import co.florlogic.finca.ingesta.servicios.output.dto.AvanceDto;
import co.florlogic.finca.ingesta.servicios.output.dto.SesionDto;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AnotacionRepositorioDobleEnMemoria;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.AvanceRepositorioDobleEnMemoria;
import co.florlogic.finca.ingesta.servicios.output.ports.repositorios.SesionRepositorioDobleEnMemoria;

/**
 * El caso de uso con los dobles en memoria: guarda una vez, cuenta y mueve el avance real del celular (Segmento 1b), solo
 * en una sesión abierta de quien la abrió; el celular es el de la sesión (CT-1, CT-7).
 */
class RecibirParticionCasoUsoImplTest {

    private static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");
    private static final UUID CELULAR = UUID.fromString("0192f0a0-0000-7000-8000-0000000000e1");
    private static final String PERSONA = "operario-1";

    private final SesionRepositorioDobleEnMemoria sesiones = new SesionRepositorioDobleEnMemoria();
    private final AnotacionRepositorioDobleEnMemoria anotaciones = new AnotacionRepositorioDobleEnMemoria();
    private final AvanceRepositorioDobleEnMemoria avances = new AvanceRepositorioDobleEnMemoria();
    private final RecibirParticionCasoUsoImpl servicio = new RecibirParticionCasoUsoImpl(sesiones, anotaciones, avances);

    @BeforeEach
    void sesionAbierta() {
        sesiones.guardar(new SesionDto(SESION, CELULAR, PERSONA, Instant.EPOCH, 0, 12, "ABIERTA", null));
    }

    @Test
    void sin_sesion_guardada_es_sesion_desconocida_y_no_guarda_nada() {
        UUID otra = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a0ff");

        assertThat(servicio.ejecutar(otra, PERSONA, particion(anotacion("d001", 1))))
                .isInstanceOf(ResultadoDeRecepcion.SesionDesconocida.class);
        assertThat(anotaciones.cuantas()).isZero();
    }

    @Test
    void una_sesion_cerrada_ya_no_recibe() {
        sesiones.guardar(new SesionDto(SESION, CELULAR, PERSONA, Instant.EPOCH, 0, 12, "CERRADA", Instant.EPOCH));

        assertThat(servicio.ejecutar(SESION, PERSONA, particion(anotacion("d001", 1))))
                .isInstanceOf(ResultadoDeRecepcion.SesionDesconocida.class);
        assertThat(anotaciones.cuantas()).isZero();
    }

    @Test
    void la_sesion_de_otra_persona_es_ajena_y_no_mueve_el_avance() {
        assertThat(servicio.ejecutar(SESION, "operario-2", particion(anotacion("d001", 1))))
                .isInstanceOf(ResultadoDeRecepcion.SesionAjena.class);
        assertThat(avances.deDispositivo(CELULAR)).isEmpty();
    }

    @Test
    void las_nuevas_se_cuentan_como_aplicadas_y_se_guardan() {
        RecepcionDeParticionValor r = recibir(particion(anotacion("d001", 1), anotacion("d002", 2)));

        assertThat(r).isEqualTo(new RecepcionDeParticionValor(2, 2, 0, 2));
        assertThat(anotaciones.cuantas()).isEqualTo(2);
    }

    @Test
    void un_reenvio_cuenta_sin_efecto_y_no_guarda_otra_vez() {
        recibir(particion(anotacion("d001", 1), anotacion("d002", 2)));

        RecepcionDeParticionValor r = recibir(particion(anotacion("d001", 1), anotacion("d002", 2)));

        assertThat(r).isEqualTo(new RecepcionDeParticionValor(2, 0, 2, 2));
        assertThat(anotaciones.cuantas()).isEqualTo(2);
    }

    @Test
    void la_misma_anotacion_dos_veces_en_una_particion_entra_una_sola_vez() {
        RecepcionDeParticionValor r = recibir(particion(anotacion("d001", 1), anotacion("d001", 1)));

        assertThat(r).isEqualTo(new RecepcionDeParticionValor(2, 1, 1, 1));
    }

    @Test
    void confirmado_hasta_se_queda_en_el_hueco_y_salta_cuando_llega_lo_que_faltaba() {
        // C-104: 1 y 2, después 4, después 3, después 4 otra vez → 2, 2, 4, 4; y queda guardado así.
        assertThat(recibir(particion(anotacion("d001", 1), anotacion("d002", 2))).confirmadoHasta())
                .isEqualTo(2);
        assertThat(recibir(particion(anotacion("d004", 4))).confirmadoHasta()).isEqualTo(2);
        assertThat(recibir(particion(anotacion("d003", 3))).confirmadoHasta()).isEqualTo(4);
        assertThat(recibir(particion(anotacion("d004", 4))).confirmadoHasta()).isEqualTo(4);
        assertThat(avances.deDispositivo(CELULAR)).hasValueSatisfying(a -> {
            assertThat(a.confirmadoHasta()).isEqualTo(4);
            assertThat(a.porEncima()).isEmpty();
        });
    }

    @Test
    void el_orden_dentro_de_la_particion_no_cambia_el_avance() {
        assertThat(recibir(particion(anotacion("d002", 2), anotacion("d001", 1))).confirmadoHasta())
                .isEqualTo(2);
    }

    @Test
    void una_particion_sin_anotaciones_da_todo_en_cero() {
        assertThat(recibir(particion())).isEqualTo(new RecepcionDeParticionValor(0, 0, 0, 0));
    }

    @Test
    void un_avance_guardado_que_no_se_puede_rehacer_no_se_tapa_con_uno_nuevo() {
        // COH-131: 3 por encima de confirmado 2 rompe INV-4; la partición se deshace (500) y el avance queda igual.
        avances.sembrar(new AvanceDto(CELULAR, 2, List.of(3L), 1));

        assertThatThrownBy(() -> recibir(particion(anotacion("d005", 5))))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(CELULAR.toString());
        assertThat(avances.deDispositivo(CELULAR)).contains(new AvanceDto(CELULAR, 2, List.of(3L), 1));
    }

    /** Recibe en la sesión abierta y saca la recepción; cualquier otro resultado hace fallar la prueba. */
    private RecepcionDeParticionValor recibir(List<AnotacionRecibida> particion) {
        ResultadoDeRecepcion resultado = servicio.ejecutar(SESION, PERSONA, particion);
        assertThat(resultado).isInstanceOf(ResultadoDeRecepcion.Recibida.class);
        return ((ResultadoDeRecepcion.Recibida) resultado).recepcion();
    }

    private static List<AnotacionRecibida> particion(AnotacionRecibida... anotaciones) {
        return List.of(anotaciones);
    }

    private static AnotacionRecibida anotacion(String sufijo, long consecutivo) {
        UUID uuid = UUID.fromString("0192f0a0-2000-7000-8000-00000000" + sufijo);
        return new AnotacionRecibida(new AnotacionId(uuid), new ConsecutivoValor(consecutivo), SESION, "{}");
    }
}
