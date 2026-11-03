package co.florlogic.finca.dispositivos.dominio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

/** La asignación del día (CT-13): secciones × campos, solo a un celular aprobado, con ids limpios. */
class AsignacionDelDiaTest {

    private static final LocalDate HOY = LocalDate.parse("2026-11-01");
    private static final Instant T0 = Instant.parse("2026-11-01T12:00:00Z");

    private static Dispositivo aprobado() {
        Dispositivo d = Dispositivo.visto(UUID.randomUUID(), T0);
        d.aprobar("admin-1", T0);
        return d;
    }

    @Test
    void cada_seccion_por_cada_campo_sin_repetidos_ni_espacios() {
        AsignacionDelDia a = AsignacionDelDia.para(HOY, aprobado(), List.of(" B1 ", "B2", "B1", ""),
                List.of("tallos", "botones"));

        assertThat(a.celdas()).containsExactly(new AsignacionDelDia.Celda("B1", "tallos"),
                new AsignacionDelDia.Celda("B1", "botones"), new AsignacionDelDia.Celda("B2", "tallos"),
                new AsignacionDelDia.Celda("B2", "botones"));
        assertThat(a.secciones()).containsExactly("B1", "B2");
        assertThat(a.campos()).containsExactly("tallos", "botones");
    }

    @Test
    void sin_campos_la_asignacion_queda_vacia() {
        assertThat(AsignacionDelDia.para(HOY, aprobado(), List.of("B1"), List.of()).celdas()).isEmpty();
    }

    @Test
    void un_celular_pendiente_o_bloqueado_no_recibe_asignacion() {
        Dispositivo pendiente = Dispositivo.visto(UUID.randomUUID(), T0);
        assertThatThrownBy(() -> AsignacionDelDia.para(HOY, pendiente, List.of("B1"), List.of("tallos")))
                .isInstanceOf(AsignacionDelDia.AsignacionInvalida.class).hasMessageContaining("PENDIENTE");

        Dispositivo bloqueado = aprobado();
        bloqueado.bloquear("admin-1", T0);
        assertThatThrownBy(() -> AsignacionDelDia.para(HOY, bloqueado, List.of("B1"), List.of("tallos")))
                .isInstanceOf(AsignacionDelDia.AsignacionInvalida.class).hasMessageContaining("BLOQUEADO");
    }

    @Test
    void un_id_con_caracteres_raros_se_rechaza() {
        assertThatThrownBy(() -> AsignacionDelDia.para(HOY, aprobado(), List.of("B1<script>"), List.of("tallos")))
                .isInstanceOf(AsignacionDelDia.AsignacionInvalida.class).hasMessageContaining("sección");
    }

    @Test
    void mas_de_quinientas_celdas_se_rechaza() {
        List<String> secciones = java.util.stream.IntStream.range(0, 26).mapToObj(i -> "S" + i).toList();
        List<String> campos = java.util.stream.IntStream.range(0, 20).mapToObj(i -> "C" + i).toList();

        assertThat(AsignacionDelDia.para(HOY, aprobado(), secciones.subList(0, 25), campos).celdas()).hasSize(500);
        assertThatThrownBy(() -> AsignacionDelDia.para(HOY, aprobado(), secciones, campos))
                .isInstanceOf(AsignacionDelDia.AsignacionInvalida.class).hasMessageContaining("520");
    }

    @Test
    void nulos_cuentan_como_vacio() {
        assertThat(AsignacionDelDia.para(HOY, aprobado(), null, Collections.singletonList(null)).celdas()).isEmpty();
    }
}
