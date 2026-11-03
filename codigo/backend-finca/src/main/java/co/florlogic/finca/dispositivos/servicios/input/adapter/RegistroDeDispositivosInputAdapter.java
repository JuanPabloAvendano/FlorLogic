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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Service (Spring): Spring crea la instancia y la entrega a cualquier módulo que pida RegistroDeDispositivos (la
 *   API pública del módulo).
 * - EstadoDeDispositivo.valueOf(….estado().name()): pasa del enum del dominio (Dispositivo.Estado) al de la API
 *   pública por el nombre de la constante; funciona porque los dos enums tienen exactamente las mismas tres. Si un día
 *   difieren, valueOf lanza IllegalArgumentException.
 * - casoUso.todos().stream().map(d → new DispositivoRegistrado(…)).toList(): Stream; copia cada agregado en el record
 *   público.
 */
