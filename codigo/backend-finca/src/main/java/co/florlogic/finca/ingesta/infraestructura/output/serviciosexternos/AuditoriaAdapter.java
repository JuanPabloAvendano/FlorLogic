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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component: Spring crea una instancia y la entrega a quien pida el puerto Auditoria.
 * - Constructor con Bitacora: inyección por constructor; Spring pasa el bean público del módulo bitacora.
 */
