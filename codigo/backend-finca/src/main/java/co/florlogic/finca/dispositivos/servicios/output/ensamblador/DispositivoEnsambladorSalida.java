package co.florlogic.finca.dispositivos.servicios.output.ensamblador;

import co.florlogic.finca.dispositivos.dominio.Dispositivo;
import co.florlogic.finca.dispositivos.servicios.output.dto.DispositivoDto;

/** Ensamblador de salida: agregado ↔ DTO (A-84). Sin estado. */
public final class DispositivoEnsambladorSalida {

    private DispositivoEnsambladorSalida() {
    }

    public static DispositivoDto aDto(Dispositivo d) {
        return new DispositivoDto(d.id(), d.estado().name(), d.vistoPorPrimeraVez(), d.cambiadoEn(), d.cambiadoPor());
    }

    public static Dispositivo aDominio(DispositivoDto d) {
        return Dispositivo.desde(d.id(), d.vistoPorPrimeraVez(), Dispositivo.Estado.valueOf(d.estado()), d.cambiadoEn(),
                d.cambiadoPor());
    }
}
