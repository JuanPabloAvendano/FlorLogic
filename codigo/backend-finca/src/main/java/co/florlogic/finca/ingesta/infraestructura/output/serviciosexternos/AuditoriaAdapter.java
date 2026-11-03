package co.florlogic.finca.ingesta.infraestructura.output.serviciosexternos;

import java.util.Map;

import org.springframework.stereotype.Component;

import co.florlogic.finca.bitacora.Bitacora;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.Auditoria;

/** Llega a la bitácora por su API pública (Modulith: solo el paquete raíz). */
@Component
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
