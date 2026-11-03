package co.florlogic.finca.dispositivos.servicios.input.ensamblador;

import co.florlogic.finca.dispositivos.dominio.AsignacionDelDia;
import co.florlogic.finca.dispositivos.dominio.Dispositivo;
import co.florlogic.finca.dispositivos.servicios.input.dto.respuesta.AsignacionRespuestaDto;
import co.florlogic.finca.dispositivos.servicios.input.dto.respuesta.DispositivoRespuestaDto;

/** Ensamblador de salida de la web del administrador: dominio → DTO de respuesta (A-84). Sin estado. */
public final class AdministrarDispositivosEnsambladorSalida {

    private AdministrarDispositivosEnsambladorSalida() {
    }

    public static DispositivoRespuestaDto aDto(Dispositivo d) {
        return new DispositivoRespuestaDto(d.id(), d.estado().name(), d.vistoPorPrimeraVez(), d.cambiadoEn(),
                d.cambiadoPor());
    }

    public static AsignacionRespuestaDto aDto(AsignacionDelDia a) {
        return new AsignacionRespuestaDto(a.jornada(), a.dispositivo(), a.secciones(), a.campos(), a.celdas().size());
    }
}
