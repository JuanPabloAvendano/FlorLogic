package co.florlogic.finca.ciclo.servicios.input.adapter;

import org.springframework.stereotype.Service;

import co.florlogic.finca.ciclo.dominio.HechoDeProduccion;
import co.florlogic.finca.ciclo.servicios.casosuso.RegistrarHechoCasoUso;
import co.florlogic.finca.ciclo.servicios.input.dto.solicitud.AnotacionAceptadaSolicitudDto;
import co.florlogic.finca.ciclo.servicios.input.ensamblador.HechoEnsambladorEntrada;
import co.florlogic.finca.ciclo.servicios.input.ports.RegistrarHechoInputPort;

/** Adaptador de entrada: solo orquesta ensamblador → caso de uso (A-84). */
@Service
public class RegistrarHechoInputAdapter implements RegistrarHechoInputPort {

    private final RegistrarHechoCasoUso casoUso;

    public RegistrarHechoInputAdapter(RegistrarHechoCasoUso casoUso) {
        this.casoUso = casoUso;
    }

    @Override
    public void ejecutar(AnotacionAceptadaSolicitudDto anotacion) {
        HechoDeProduccion hecho = HechoEnsambladorEntrada.aDominio(anotacion);
        casoUso.ejecutar(hecho, anotacion.codigos());
    }
}
