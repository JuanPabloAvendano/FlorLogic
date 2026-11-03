package co.florlogic.finca.ingesta.infraestructura.output.serviciosexternos;

import org.springframework.stereotype.Component;

import co.florlogic.finca.ingesta.servicios.output.dto.ContenidoAEvaluarDto;
import co.florlogic.finca.ingesta.servicios.output.dto.VeredictoDto;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.EvaluadorDeAnotaciones;
import co.florlogic.finca.reglas.EvaluarAnotacion;
import co.florlogic.finca.reglas.ResultadoDeEvaluacion;
import co.florlogic.finca.reglas.SolicitudDeEvaluacion;

/** Llega al motor de reglas por la API pública de {@code reglas} (A-96; Modulith: solo el paquete raíz). */
@Component
class EvaluadorDeAnotacionesAdapter implements EvaluadorDeAnotaciones {

    private final EvaluarAnotacion reglas;

    EvaluadorDeAnotacionesAdapter(EvaluarAnotacion reglas) {
        this.reglas = reglas;
    }

    @Override
    public VeredictoDto evaluar(ContenidoAEvaluarDto contenido) {
        ResultadoDeEvaluacion resultado = reglas.evaluar(new SolicitudDeEvaluacion(contenido.tipo(), contenido.versionTipo(),
                contenido.marcadaEnElCelular(), contenido.contenidoJson()));
        return new VeredictoDto(resultado.marcas(), resultado.motivoRechazo());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component: Spring crea una instancia y la entrega a quien pida el puerto EvaluadorDeAnotaciones.
 * - Constructor con EvaluarAnotacion: Spring inyecta el bean público del módulo reglas.
 */
