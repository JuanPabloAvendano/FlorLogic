package co.florlogic.finca.dispositivos;

import java.time.Instant;
import java.util.UUID;

/** Un celular registrado, para la web del administrador. */
public record DispositivoRegistrado(UUID dispositivo, EstadoDeDispositivo estado, Instant vistoPorPrimeraVez,
        Instant cambiadoEn, String cambiadoPor) {
}
