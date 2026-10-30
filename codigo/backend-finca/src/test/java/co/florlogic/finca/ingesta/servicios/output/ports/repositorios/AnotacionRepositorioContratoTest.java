package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDTO;

/**
 * Prueba de contrato del puerto de salida {@link AnotacionRepositorio} (plantilla caso-de-uso T9 y
 * adaptador-persistencia §9.7 y §10.6). La heredan el doble en memoria y el adaptador JDBC: si los dos pasan las
 * mismas pruebas, el doble se comporta como la base real y las pruebas rápidas que lo usan dicen la verdad.
 */
public abstract class AnotacionRepositorioContratoTest {

    protected static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");

    private AnotacionRepositorio anotaciones;

    /** Cada hija entrega su implementación, vacía. */
    protected abstract AnotacionRepositorio crear();

    @BeforeEach
    void preparar() {
        anotaciones = crear();
    }

    @Test
    protected void una_anotacion_nueva_se_guarda() {
        assertThat(anotaciones.guardarSiNueva(anotacion("0192f0a0-2000-7000-8000-00000000d001", 1))).isTrue();
    }

    @Test
    protected void la_misma_anotacion_otra_vez_no_se_guarda() {
        AnotacionRecibidaDTO a = anotacion("0192f0a0-2000-7000-8000-00000000d001", 1);
        anotaciones.guardarSiNueva(a);

        assertThat(anotaciones.guardarSiNueva(a)).isFalse();
    }

    @Test
    protected void el_mismo_uuid_con_otro_contenido_tampoco_se_guarda() {
        // En el camino mínimo la identidad es solo el uuid; la anotación corrupta (huella) llega en el Segmento 3.
        anotaciones.guardarSiNueva(anotacion("0192f0a0-2000-7000-8000-00000000d001", 1));

        assertThat(anotaciones.guardarSiNueva(anotacion("0192f0a0-2000-7000-8000-00000000d001", 9))).isFalse();
    }

    @Test
    protected void dos_anotaciones_distintas_se_guardan_las_dos() {
        assertThat(anotaciones.guardarSiNueva(anotacion("0192f0a0-2000-7000-8000-00000000d001", 1))).isTrue();
        assertThat(anotaciones.guardarSiNueva(anotacion("0192f0a0-2001-7000-8000-00000000d002", 2))).isTrue();
    }

    protected static AnotacionRecibidaDTO anotacion(String uuid, long consecutivo) {
        return new AnotacionRecibidaDTO(UUID.fromString(uuid), consecutivo, SESION,
                "{\"anotacion_uuid\":\"" + uuid + "\",\"consecutivo\":" + consecutivo + "}");
    }
}
