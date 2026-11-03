package co.florlogic.finca.reglas.servicios.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import co.florlogic.finca.reglas.ResultadoDeEvaluacion;
import co.florlogic.finca.reglas.SolicitudDeEvaluacion;
import co.florlogic.finca.reglas.dominio.CamposLeidos;
import co.florlogic.finca.reglas.dominio.Presencia;
import co.florlogic.finca.reglas.servicios.output.dto.FormaDto;

/** Tipo desconocido, datos inválidos y marcas, con dobles del catálogo y del lector. */
class EvaluarAnotacionCasoUsoImplTest {

    private static final FormaDto FORMA = new FormaDto(List.of("valor"), "motivo_ausencia");

    @Test
    void un_tipo_fuera_del_catalogo_es_rechazo_tipo_desconocido() {
        var casoUso = new EvaluarAnotacionCasoUsoImpl((t, v) -> Optional.empty(), (json, f) -> CamposLeidos.invalidos());

        assertThat(casoUso.ejecutar(new SolicitudDeEvaluacion("otro.tipo", 1, false, "{}")))
                .isEqualTo(ResultadoDeEvaluacion.rechazo("tipo_desconocido"));
    }

    @Test
    void sin_datos_que_leer_es_rechazo_datos_invalidos() {
        var casoUso = new EvaluarAnotacionCasoUsoImpl((t, v) -> Optional.of(FORMA), (json, f) -> CamposLeidos.invalidos());

        assertThat(casoUso.ejecutar(new SolicitudDeEvaluacion("captura.campo", 1, false, "{}")))
                .isEqualTo(ResultadoDeEvaluacion.rechazo("datos_invalidos"));
    }

    @Test
    void con_datos_salen_las_marcas_de_las_reglas() {
        var casoUso = new EvaluarAnotacionCasoUsoImpl((t, v) -> Optional.of(FORMA),
                (json, f) -> new CamposLeidos(true, Map.of("valor", Presencia.FALTA), false));

        assertThat(casoUso.ejecutar(new SolicitudDeEvaluacion("captura.campo", 1, true, "{}")).marcas())
                .containsExactly("incompleta", "motivo_ausente");
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Test (JUnit 5): marca cada método como una prueba que corre ./mvnw verify.
 * - var casoUso = …: var deja que Java deduzca el tipo de la variable (EvaluarAnotacionCasoUsoImpl) por lo que hay a
 *   la derecha.
 * - (t, v) → Optional.empty() y (json, f) → CamposLeidos.invalidos(): lambdas que hacen de dobles de prueba. Funcionan
 *   porque CatalogoDeParametros y LectorDeContenido son interfaces de un solo método: la lambda es su implementación,
 *   sin Mockito ni Spring.
 * - assertThat(…).isEqualTo(ResultadoDeEvaluacion.rechazo(…)) (AssertJ): compara records por valor; containsExactly
 *   exige las marcas y su orden.
 */
