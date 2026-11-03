package co.florlogic.finca.dispositivos.servicios.input.adapter;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import co.florlogic.finca.dispositivos.DispositivoRegistrado;
import co.florlogic.finca.dispositivos.EstadoDeDispositivo;
import co.florlogic.finca.dispositivos.RegistroDeDispositivos;
import co.florlogic.finca.dispositivos.servicios.casosuso.GestionarDispositivosCasoUso;

/** Implementa la API pública del módulo y solo delega en el caso de uso (A-84). */
@Service
public class RegistroDeDispositivosInputAdapter implements RegistroDeDispositivos {

    private final GestionarDispositivosCasoUso casoUso;

    public RegistroDeDispositivosInputAdapter(GestionarDispositivosCasoUso casoUso) {
        this.casoUso = casoUso;
    }

    @Override
    public EstadoDeDispositivo reconocer(UUID dispositivo) {
        return EstadoDeDispositivo.valueOf(casoUso.reconocer(dispositivo).estado().name());
    }

    @Override
    public void aprobar(UUID dispositivo, String administrador) {
        casoUso.aprobar(dispositivo, administrador);
    }

    @Override
    public void bloquear(UUID dispositivo, String administrador) {
        casoUso.bloquear(dispositivo, administrador);
    }

    @Override
    public List<DispositivoRegistrado> todos() {
        return casoUso.todos().stream().map(d -> new DispositivoRegistrado(d.id(), EstadoDeDispositivo.valueOf(d.estado().name()),
                d.vistoPorPrimeraVez(), d.cambiadoEn(), d.cambiadoPor())).toList();
    }
}
