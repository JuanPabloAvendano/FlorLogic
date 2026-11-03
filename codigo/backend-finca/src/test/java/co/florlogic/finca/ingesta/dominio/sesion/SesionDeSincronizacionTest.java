package co.florlogic.finca.ingesta.dominio.sesion;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.ingesta.dominio.comun.DispositivoId;

/** La sesión mide el desfase al abrir, se cierra una sola vez y solo es de quien la abrió (CT-1). */
class SesionDeSincronizacionTest {

    private static final Instant NODO = Instant.parse("2026-10-31T12:00:00Z");

    private final SesionDeSincronizacion sesion = SesionDeSincronizacion.abrir(new SesionId(UUID.randomUUID()),
            new DispositivoId(UUID.randomUUID()), "op-07", NODO, NODO.plusMillis(1500), 12);

    @Test
    void el_desfase_es_reloj_del_celular_menos_reloj_del_nodo() {
        assertThat(sesion.desfaseMs()).isEqualTo(1500);
        assertThat(sesion.abierta()).isTrue();
        assertThat(sesion.cerradaEn()).isEmpty();
    }

    @Test
    void un_celular_atrasado_da_desfase_negativo() {
        SesionDeSincronizacion atrasado = SesionDeSincronizacion.abrir(new SesionId(UUID.randomUUID()),
                new DispositivoId(UUID.randomUUID()), "op-07", NODO, NODO.minusMillis(250), 12);

        assertThat(atrasado.desfaseMs()).isEqualTo(-250);
    }

    @Test
    void cerrar_completa_o_parcial_y_cerrar_otra_vez_no_cambia_nada() {
        sesion.cerrar(false, NODO.plusSeconds(60));
        sesion.cerrar(true, NODO.plusSeconds(120));

        assertThat(sesion.estado()).isEqualTo(EstadoSesion.PARCIAL);
        assertThat(sesion.cerradaEn()).contains(NODO.plusSeconds(60));
        assertThat(sesion.abierta()).isFalse();
    }

    @Test
    void solo_es_de_quien_la_abrio() {
        assertThat(sesion.esDe("op-07")).isTrue();
        assertThat(sesion.esDe("op-08")).isFalse();
    }

    @Test
    void lo_guardado_incoherente_no_se_rehace() {
        // INV: abierta ⇔ sin fecha de cierre; lo demás, vacío y sin lanzar (A-57).
        assertThat(SesionDeSincronizacion.desde(new SesionId(UUID.randomUUID()), new DispositivoId(UUID.randomUUID()),
                "op-07", NODO, 0, 12, EstadoSesion.CERRADA, null)).isEmpty();
        assertThat(SesionDeSincronizacion.desde(new SesionId(UUID.randomUUID()), new DispositivoId(UUID.randomUUID()),
                "op-07", NODO, 0, 12, EstadoSesion.ABIERTA, NODO)).isEmpty();
        assertThat(SesionDeSincronizacion.desde(new SesionId(UUID.randomUUID()), null, "op-07", NODO, 0, 12,
                EstadoSesion.ABIERTA, null)).isEmpty();
    }
}
