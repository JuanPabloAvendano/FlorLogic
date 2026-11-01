package co.florlogic.finca.ingesta.servicios.input.adapter;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.florlogic.finca.ingesta.servicios.casosuso.RecibirParticionCasoUso;
import co.florlogic.finca.ingesta.servicios.input.ensamblador.RecibirParticionEnsambladorEntrada;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.RecibirParticionSolicitudDto;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.RecibirParticionRespuestaDto;
import co.florlogic.finca.ingesta.servicios.input.ports.RecibirParticionInputPort;

/**
 * Adaptador de entrada: implementa el puerto y solo orquesta (A-84): pide el dominio al ensamblador, llama al caso de
 * uso y pide la respuesta. No construye ningún valor del dominio; eso lo hace {@link RecibirParticionEnsambladorEntrada}, y
 * {@code CapasTest} lo vigila.
 */
@Service
public class RecibirParticionInputAdapter implements RecibirParticionInputPort {

    private final RecibirParticionCasoUso casoUso;

    public RecibirParticionInputAdapter(RecibirParticionCasoUso casoUso) {
        this.casoUso = Objects.requireNonNull(casoUso, "casoUso");
    }

    @Override
    public Optional<RecibirParticionRespuestaDto> ejecutar(RecibirParticionSolicitudDto solicitud) {
        // Si el dominio rechaza una anotación, el caso de uso ni se llama: nada se guarda ni se mueve el avance (C-105).
        return RecibirParticionEnsambladorEntrada.aDominio(solicitud)
                .map(anotaciones -> casoUso.ejecutar(solicitud.sesionId(), anotaciones))
                .map(RecibirParticionEnsambladorEntrada::aRespuesta);
    }
}
