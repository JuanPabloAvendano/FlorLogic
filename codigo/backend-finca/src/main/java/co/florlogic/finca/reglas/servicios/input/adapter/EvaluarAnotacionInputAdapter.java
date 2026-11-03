package co.florlogic.finca.reglas.servicios.input.adapter;

import org.springframework.stereotype.Service;

import co.florlogic.finca.reglas.EvaluarAnotacion;
import co.florlogic.finca.reglas.ResultadoDeEvaluacion;
import co.florlogic.finca.reglas.SolicitudDeEvaluacion;
import co.florlogic.finca.reglas.servicios.casosuso.EvaluarAnotacionCasoUso;

/** Implementa la API pública del módulo y solo delega en el caso de uso (A-84). */
@Service
public class EvaluarAnotacionInputAdapter implements EvaluarAnotacion {

    private final EvaluarAnotacionCasoUso casoUso;

    public EvaluarAnotacionInputAdapter(EvaluarAnotacionCasoUso casoUso) {
        this.casoUso = casoUso;
    }

    @Override
    public ResultadoDeEvaluacion evaluar(SolicitudDeEvaluacion solicitud) {
        return casoUso.ejecutar(solicitud);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): Spring crea la instancia y la entrega a cualquier módulo que pida EvaluarAnotacion (la API
 *   pública de reglas; hoy la pide ingesta).
 */
