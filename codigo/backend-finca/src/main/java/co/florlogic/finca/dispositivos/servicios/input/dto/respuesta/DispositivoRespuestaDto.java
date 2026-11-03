package co.florlogic.finca.dispositivos.servicios.input.dto.respuesta;

import java.time.Instant;
import java.util.UUID;

/** Un celular como lo ve el administrador en la web (CT-10). */
public record DispositivoRespuestaDto(UUID id, String estado, Instant vistoPorPrimeraVez, Instant cambiadoEn,
        String cambiadoPor) {

    public boolean pendiente() {
        return "PENDIENTE".equals(estado);
    }
}
