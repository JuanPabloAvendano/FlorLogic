package co.florlogic.finca.ciclo.servicios.input.ensamblador;

import co.florlogic.finca.ciclo.dominio.HechoDeProduccion;
import co.florlogic.finca.ciclo.servicios.input.dto.solicitud.AnotacionAceptadaSolicitudDto;

/** Ensamblador de entrada del ciclo: solicitud → hecho (A-84). Sin estado. */
public final class HechoEnsambladorEntrada {

    private HechoEnsambladorEntrada() {
    }

    public static HechoDeProduccion aDominio(AnotacionAceptadaSolicitudDto a) {
        return new HechoDeProduccion(a.produccionId(), a.seccionId(), a.campoId(), a.jornada(), a.valor(),
                a.motivoAusencia(), a.capturadoEn(), a.anotacionUuid(), a.dispositivoId());
    }
}
