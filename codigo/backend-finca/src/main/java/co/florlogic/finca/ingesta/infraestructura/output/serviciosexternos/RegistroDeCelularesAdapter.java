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
