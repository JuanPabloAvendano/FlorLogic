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
        return Estado.valueOf(dispositivos.reconocer(dispositivo).name());
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @Component: Spring crea una instancia y la entrega a quien pida el puerto RegistroDeCelulares.
 * - Estado.valueOf(dispositivos.reconocer(dispositivo).name()): traduce el estado del módulo dispositivos al de
 *   ingesta por su nombre; si dispositivos agregara un estado que ingesta no tiene, valueOf lanzaría
 *   IllegalArgumentException.
 */
