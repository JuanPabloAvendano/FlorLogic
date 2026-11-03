package co.florlogic.finca.ingesta.servicios.input.ports;

import co.florlogic.finca.ingesta.servicios.input.dto.respuesta.AbrirSesionResultadoDto;
import co.florlogic.finca.ingesta.servicios.input.dto.solicitud.AbrirSesionSolicitudDto;

/** Puerto de entrada: abrir una sesión de sincronización (CT-1, CT-10). */
public interface AbrirSesionInputPort {

    AbrirSesionResultadoDto ejecutar(AbrirSesionSolicitudDto solicitud);
}
