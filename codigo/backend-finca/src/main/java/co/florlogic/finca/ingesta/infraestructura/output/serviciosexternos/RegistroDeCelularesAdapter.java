package co.florlogic.finca.ingesta.infraestructura.output.serviciosexternos;

import java.util.UUID;

import org.springframework.stereotype.Component;

import co.florlogic.finca.dispositivos.RegistroDeDispositivos;
import co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos.RegistroDeCelulares;

/** Llega a {@code dispositivos} por su API pública (Modulith: solo el paquete raíz). */
@Component
class RegistroDeCelularesAdapter implements RegistroDeCelulares {

    private final RegistroDeDispositivos dispositivos;

    RegistroDeCelularesAdapter(RegistroDeDispositivos dispositivos) {
        this.dispositivos = dispositivos;
    }

    @Override
    public Estado reconocer(UUID dispositivo) {
        // switch sin default: si dispositivos agrega un estado, esto deja de compilar en vez de fallar en marcha.
        return switch (dispositivos.reconocer(dispositivo)) {
            case PENDIENTE -> Estado.PENDIENTE;
            case APROBADO -> Estado.APROBADO;
            case BLOQUEADO -> Estado.BLOQUEADO;
        };
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component: Spring crea una instancia y la entrega a quien pida el puerto RegistroDeCelulares.
 * - switch (…) { case PENDIENTE -> … }: switch de expresión sobre el enum EstadoDeDispositivo; traduce cada estado
 *   del módulo dispositivos al de ingesta. Sin default, el compilador exige cubrir todos los valores: si
 *   dispositivos agrega un estado, el build falla aquí en lugar de lanzar un error en marcha.
 */
