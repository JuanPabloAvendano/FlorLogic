package co.florlogic.finca.ingesta.servicios.output.ports.repositorios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.servicios.output.dto.AvanceDto;

/**
 * Lo que toda implementación de {@link AvanceRepositorio} cumple (U-20, PR-17): el doble en memoria y el adaptador
 * JDBC corren las mismas pruebas, así el doble no miente. La versión optimista es la de A-98.
 */
public abstract class AvanceRepositorioContratoTest {

    protected static final UUID CELULAR = UUID.fromString("0192f0a0-0000-7000-8000-0000000000e1");

    private AvanceRepositorio avances;

    /** Cada hija entrega su implementación, vacía. */
    protected abstract AvanceRepositorio crear();

    @BeforeEach
    void preparar() {
        avances = crear();
    }

    @Test
    protected void un_celular_sin_avance_da_vacio() {
        assertThat(avances.deDispositivo(CELULAR)).isEmpty();
    }

    @Test
    protected void un_avance_nuevo_se_guarda_con_version_1() {
        avances.guardar(new AvanceDto(CELULAR, 2, List.of(4L, 5L), 0));

        assertThat(avances.deDispositivo(CELULAR)).contains(new AvanceDto(CELULAR, 2, List.of(4L, 5L), 1));
    }

    @Test
    protected void guardar_con_la_version_leida_sube_la_version() {
        avances.guardar(new AvanceDto(CELULAR, 2, List.of(), 0));

        avances.guardar(new AvanceDto(CELULAR, 4, List.of(), 1));

        assertThat(avances.deDispositivo(CELULAR)).contains(new AvanceDto(CELULAR, 4, List.of(), 2));
    }

    @Test
    protected void guardar_con_una_version_vieja_choca_y_no_cambia_nada() {
        avances.guardar(new AvanceDto(CELULAR, 2, List.of(), 0));
        avances.guardar(new AvanceDto(CELULAR, 3, List.of(), 1));

        assertThatThrownBy(() -> avances.guardar(new AvanceDto(CELULAR, 9, List.of(), 1)))
                .isInstanceOf(ChoqueConcurrente.class);
        assertThat(avances.deDispositivo(CELULAR)).contains(new AvanceDto(CELULAR, 3, List.of(), 2));
    }

    @Test
    protected void dos_avances_nuevos_del_mismo_celular_chocan() {
        // Dos particiones del primer día, leídas las dos como «nuevo» (versión 0): solo una gana.
        avances.guardar(new AvanceDto(CELULAR, 1, List.of(), 0));

        assertThatThrownBy(() -> avances.guardar(new AvanceDto(CELULAR, 2, List.of(), 0)))
                .isInstanceOf(ChoqueConcurrente.class);
        assertThat(avances.deDispositivo(CELULAR)).contains(new AvanceDto(CELULAR, 1, List.of(), 1));
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - public abstract class …ContratoTest: prueba de contrato; JUnit no corre la clase abstracta, corre sus hijas (el
 *   doble en memoria y el adaptador JDBC), que heredan todos estos @Test.
 * - protected abstract AvanceRepositorio crear(): cada hija entrega su implementación.
 * - @BeforeEach: antes de cada caso pide una implementación nueva y vacía.
 * - assertThat(Optional).isEmpty() / contains(...) (AssertJ) y assertThatThrownBy(...).isInstanceOf(...): revisan
 *   lo leído y el choque.
 */
