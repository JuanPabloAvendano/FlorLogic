package co.florlogic.finca.dispositivos;

import java.util.List;
import java.util.UUID;

/**
 * API pública del módulo de dispositivos (IA_CONSTRUCCION-TX1 CT-10; ES-S13): reconocer un celular al abrir sesión
 * (uno desconocido queda pendiente), y que el administrador lo apruebe o lo bloquee.
 */
public interface RegistroDeDispositivos {

    /** El estado del celular; si nunca se vio, lo registra como {@link EstadoDeDispositivo#PENDIENTE} y avisa. */
    EstadoDeDispositivo reconocer(UUID dispositivo);

    void aprobar(UUID dispositivo, String administrador);

    void bloquear(UUID dispositivo, String administrador);

    List<DispositivoRegistrado> todos();
}
