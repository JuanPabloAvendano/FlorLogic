package co.florlogic.finca.dispositivos.infraestructura.output.serviciosexternos;

import java.util.Map;

import org.springframework.stereotype.Component;

import co.florlogic.finca.bitacora.Bitacora;
import co.florlogic.finca.dispositivos.servicios.output.ports.serviciosexternos.Auditoria;

/** Llega a la bitácora por su API pública (Modulith: solo el paquete raíz). */
@Component("auditoriaDeDispositivos")
class AuditoriaAdapter implements Auditoria {

    private final Bitacora bitacora;

    AuditoriaAdapter(Bitacora bitacora) {
        this.bitacora = bitacora;
    }

    @Override
    public void registrar(String actor, String evento, String objeto, Map<String, String> detalle) {
        bitacora.registrar(actor, evento, objeto, detalle);
    }
}
