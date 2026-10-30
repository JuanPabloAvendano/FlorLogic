package co.florlogic.finca.ingesta.servicios.input.adapter;

import java.util.Objects;

import org.springframework.stereotype.Service;

import co.florlogic.finca.ingesta.servicios.casosuso.RecibirParticionCasoUso;
import co.florlogic.finca.ingesta.servicios.input.assembler.RecibirParticionInputAssembler;
import co.florlogic.finca.ingesta.servicios.input.dto.request.RecibirParticionRequest;
import co.florlogic.finca.ingesta.servicios.input.dto.response.RecibirParticionResponse;
import co.florlogic.finca.ingesta.servicios.input.ports.RecibirParticionInputPort;

/**
 * Adaptador de entrada: implementa el puerto y solo orquesta (A-84): pide el dominio al ensamblador, llama al caso de
 * uso y pide el response. No construye ningún valor del dominio; eso lo hace {@link RecibirParticionInputAssembler}, y
 * {@code CapasTest} lo vigila.
 */
@Service
public class RecibirParticionInputAdapter implements RecibirParticionInputPort {

    private final RecibirParticionCasoUso casoUso;

    public RecibirParticionInputAdapter(RecibirParticionCasoUso casoUso) {
        this.casoUso = Objects.requireNonNull(casoUso, "casoUso");
    }

    @Override
    public RecibirParticionResponse ejecutar(RecibirParticionRequest request) {
        return RecibirParticionInputAssembler.aResponse(
                casoUso.ejecutar(request.sesionId(), RecibirParticionInputAssembler.aDominio(request)));
    }
}
