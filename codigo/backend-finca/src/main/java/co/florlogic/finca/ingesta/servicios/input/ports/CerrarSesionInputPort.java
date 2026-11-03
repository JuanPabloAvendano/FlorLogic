package co.florlogic.finca.ingesta.servicios.input.ports;

import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.CerrarSesionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.CerrarSesionSolicitudDto;

/** Puerto de entrada: cerrar una sesión de sincronización (CT-1). */
public interface CerrarSesionInputPort {

    CerrarSesionResultadoDto ejecutar(CerrarSesionSolicitudDto solicitud);
}
