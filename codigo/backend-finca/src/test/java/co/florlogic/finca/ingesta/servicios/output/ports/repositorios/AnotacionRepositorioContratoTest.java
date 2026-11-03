package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRechazadaDto;
import co.florlogic.finca.ingesta.servicios.output.dto.AnotacionRecibidaDto;
import co.florlogic.finca.ingesta.servicios.output.dto.GuardadaDto;

/**
 * Lo que toda implementación de {@link AnotacionRepositorio} cumple (U-20, PR-17): el doble en memoria y el adaptador
 * JDBC corren las mismas pruebas.
 */
public abstract class AnotacionRepositorioContratoTest {

    protected static final UUID SESION = UUID.fromString("0192f0a1-7c00-7000-8000-00000000a001");
    protected static final UUID D001 = UUID.fromString("0192f0a0-2000-7000-8000-00000000d001");
    protected static final UUID D002 = UUID.fromString("0192f0a0-2000-7000-8000-00000000d002");
    protected static final String H1 = "a".repeat(64);
    protected static final String H2 = "b".repeat(64);

    private AnotacionRepositorio anotaciones;

    /** Cada hija entrega su implementación, vacía. */
    protected abstract AnotacionRepositorio crear();

    @BeforeEach
    void preparar() {
        anotaciones = crear();
    }

    @Test
    protected void un_id_que_nunca_llego_no_esta_guardado() {
        assertThat(anotaciones.guardadasCon(List.of(D001))).isEmpty();
    }

    @Test
    protected void una_aceptada_se_lee_con_su_huella_resultado_y_codigos() {
        anotaciones.guardarAceptada(aceptada(D001, H1, "MARCADA", List.of("incompleta")));

        assertThat(anotaciones.guardadasCon(List.of(D001, D002)))
                .containsOnlyKeys(D001)
                .containsEntry(D001, new GuardadaDto(D001, H1, "MARCADA", List.of("incompleta")));
    }

    @Test
    protected void la_misma_aceptada_dos_veces_choca() {
        // A-98: si otra partición guardó el mismo id entre la lectura y la escritura, toda la partición se deshace.
        anotaciones.guardarAceptada(aceptada(D001, H1, "APLICADA", List.of()));

        assertThatThrownBy(() -> anotaciones.guardarAceptada(aceptada(D001, H1, "APLICADA", List.of())))
                .isInstanceOf(ChoqueConcurrente.class);
    }

    @Test
    protected void una_rechazada_se_lee_con_su_motivo_y_repetirla_no_choca() {
        anotaciones.guardarRechazada(rechazada(D002, H2, "tipo_desconocido"));
        anotaciones.guardarRechazada(rechazada(D002, H2, "tipo_desconocido"));

        assertThat(anotaciones.guardadasCon(List.of(D002)))
                .containsEntry(D002, new GuardadaDto(D002, H2, "RECHAZADA", List.of("tipo_desconocido")));
    }

    @Test
    protected void si_un_id_tiene_aceptada_y_rechazada_manda_la_aceptada() {
        // La primera llegada fue aceptada; una segunda con otra huella quedó rechazada como corrupta.
        anotaciones.guardarAceptada(aceptada(D001, H1, "APLICADA", List.of()));
        anotaciones.guardarRechazada(rechazada(D001, H2, "corrupta"));

        assertThat(anotaciones.guardadasCon(List.of(D001)).get(D001).huella()).isEqualTo(H1);
    }

    protected static AnotacionRecibidaDto aceptada(UUID id, String huella, String resultado, List<String> codigos) {
        return new AnotacionRecibidaDto(id, 1, SESION, "captura.campo", 1, huella, resultado, codigos,
                "{\"anotacion_uuid\":\"" + id + "\"}");
    }

    protected static AnotacionRechazadaDto rechazada(UUID id, String huella, String motivo) {
        return new AnotacionRechazadaDto(id, huella, SESION, 2, motivo, "{\"anotacion_uuid\":\"" + id + "\"}");
    }
}
