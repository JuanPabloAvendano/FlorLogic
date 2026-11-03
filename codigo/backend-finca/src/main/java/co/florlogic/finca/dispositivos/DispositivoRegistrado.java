package co.florlogic.finca.dispositivos;

import java.time.Instant;
import java.util.UUID;

/** Un celular registrado, para la web del administrador. */
public record DispositivoRegistrado(UUID dispositivo, EstadoDeDispositivo estado, Instant vistoPorPrimeraVez,
        Instant cambiadoEn, String cambiadoPor) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable; Java genera el constructor, un método de lectura por componente (estado(),
 *   cambiadoPor()…), equals, hashCode y toString. Es lo que la API pública entrega a otros módulos, sin el agregado.
 */
