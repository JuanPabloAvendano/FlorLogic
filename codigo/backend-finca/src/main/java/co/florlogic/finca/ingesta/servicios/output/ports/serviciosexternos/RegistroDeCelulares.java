package co.florlogic.finca.ingesta.servicios.output.ports.serviciosexternos;

import java.util.UUID;

/** Puerto de salida hacia el módulo {@code dispositivos} (CT-10): reconoce al celular que abre sesión. */
public interface RegistroDeCelulares {

    enum Estado {
        APROBADO,
        PENDIENTE,
        BLOQUEADO
    }

    /** Uno nunca visto queda pendiente (y el módulo avisa al administrador). */
    Estado reconocer(UUID dispositivo);
}
