package co.florlogic.finca.ciclo.servicios.input.ports;

import co.florlogic.finca.ciclo.servicios.input.dto.solicitud.AnotacionAceptadaSolicitudDto;

/** Puerto de entrada: llevar una anotación aceptada al ciclo de producción (CT-12). */
public interface RegistrarHechoInputPort {

    void ejecutar(AnotacionAceptadaSolicitudDto anotacion);
}
