package co.florlogic.finca.reglas.servicios.casosuso.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.florlogic.finca.reglas.ResultadoDeEvaluacion;
import co.florlogic.finca.reglas.SolicitudDeEvaluacion;
import co.florlogic.finca.reglas.dominio.CamposLeidos;
import co.florlogic.finca.reglas.dominio.ReglasDeIngreso;
import co.florlogic.finca.reglas.servicios.casosuso.EvaluarAnotacionCasoUso;
import co.florlogic.finca.reglas.servicios.output.dto.FormaDto;
import co.florlogic.finca.reglas.servicios.output.ports.serviciosexternos.CatalogoDeParametros;
import co.florlogic.finca.reglas.servicios.output.ports.serviciosexternos.LectorDeContenido;

/**
 * Juzga una anotación (A-99, A-101): tipo o versión fuera del catálogo → {@code tipo_desconocido}; sin un objeto
 * {@code datos} → {@code datos_invalidos}; si no, las marcas de {@link ReglasDeIngreso}.
 */
@Service
public class EvaluarAnotacionCasoUsoImpl implements EvaluarAnotacionCasoUso {

    static final String TIPO_DESCONOCIDO = "tipo_desconocido";
    static final String DATOS_INVALIDOS = "datos_invalidos";

    private final CatalogoDeParametros catalogo;
    private final LectorDeContenido lector;

    public EvaluarAnotacionCasoUsoImpl(CatalogoDeParametros catalogo, LectorDeContenido lector) {
        this.catalogo = catalogo;
        this.lector = lector;
    }

    @Override
    public ResultadoDeEvaluacion ejecutar(SolicitudDeEvaluacion solicitud) {
        Optional<FormaDto> forma = catalogo.forma(solicitud.tipo(), solicitud.versionTipo());
        if (forma.isEmpty()) {
            return ResultadoDeEvaluacion.rechazo(TIPO_DESCONOCIDO);
        }
        CamposLeidos campos = lector.leer(solicitud.contenidoJson(), forma.get());
        if (!campos.datosValidos()) {
            return ResultadoDeEvaluacion.rechazo(DATOS_INVALIDOS);
        }
        return new ResultadoDeEvaluacion(List.copyOf(ReglasDeIngreso.marcas(campos, solicitud.marcadaEnElCelular())),
                Optional.empty());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): Spring crea la instancia y la entrega a quien pida EvaluarAnotacionCasoUso. No lleva
 *   @Transactional porque no escribe nada: lee el catálogo y juzga.
 * - static final String TIPO_DESCONOCIDO / DATOS_INVALIDOS (sin public): los códigos de rechazo, visibles solo en el
 *   paquete.
 * - Optional<FormaDto> forma, forma.isEmpty() y forma.get(): si el catálogo no tiene ese tipo y versión, rechazo;
 *   get() solo se llama después de comprobar que no está vacío.
 * - List.copyOf(ReglasDeIngreso.marcas(…)): pasa el conjunto ordenado de marcas a una lista inmutable, conservando el
 *   orden alfabético.
 */
