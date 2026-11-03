package co.florlogic.finca.ingesta.servicios.input.adapter;

import org.springframework.stereotype.Service;

import co.florlogic.finca.ingesta.servicios.casosuso.CerrarSesionCasoUso;
import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.CerrarSesionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.CerrarSesionSolicitudDto;
import co.florlogic.finca.ingesta.servicios.input.ensamblador.SesionEnsambladorEntrada;
import co.florlogic.finca.ingesta.servicios.input.ports.CerrarSesionInputPort;

/** Adaptador de entrada del cierre: solo orquesta (A-84). */
@Service
public class CerrarSesionInputAdapter implements CerrarSesionInputPort {

    private final CerrarSesionCasoUso casoUso;

    public CerrarSesionInputAdapter(CerrarSesionCasoUso casoUso) {
        this.casoUso = casoUso;
    }

    @Override
    public CerrarSesionResultadoDto ejecutar(CerrarSesionSolicitudDto solicitud) {
        return SesionEnsambladorEntrada.aResultado(
                casoUso.ejecutar(solicitud.sesionId(), solicitud.persona(), solicitud.completa()));
    }
}
