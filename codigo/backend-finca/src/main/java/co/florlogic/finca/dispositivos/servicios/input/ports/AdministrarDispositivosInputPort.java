package co.florlogic.finca.dispositivos.servicios.input.ports;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import co.florlogic.finca.dispositivos.servicios.input.dto.respuesta.AsignacionRespuestaDto;
import co.florlogic.finca.dispositivos.servicios.input.dto.respuesta.DispositivoRespuestaDto;
import co.florlogic.finca.dispositivos.servicios.input.dto.solicitud.AsignacionSolicitudDto;

/** Puerto de entrada de la web del administrador (F4, CT-10, CT-13): celulares y asignación del día. */
public interface AdministrarDispositivosInputPort {

    /** La jornada de hoy en la zona de la finca: la que la web muestra si no se pide otra. */
    LocalDate jornadaDeHoy();

    List<DispositivoRespuestaDto> todos();

    DispositivoRespuestaDto aprobar(UUID dispositivo, String administrador);

    DispositivoRespuestaDto bloquear(UUID dispositivo, String administrador);

    List<AsignacionRespuestaDto> asignacionDe(LocalDate jornada);

    /** @throws AsignacionRechazada con el motivo para el administrador */
    AsignacionRespuestaDto asignar(AsignacionSolicitudDto solicitud);

    /** La asignación no se puede hacer (celular no aprobado, ids que no sirven, demasiadas celdas). */
    final class AsignacionRechazada extends RuntimeException {
        public AsignacionRechazada(String motivo) {
            super(motivo);
        }
    }
}
