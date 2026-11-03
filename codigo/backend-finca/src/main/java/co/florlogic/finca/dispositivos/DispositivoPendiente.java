package co.florlogic.finca.dispositivos;

import java.util.UUID;

/** Evento: un celular desconocido abrió sesión y espera aprobación (CT-10). Lo escucha {@code notificacion}. */
public record DispositivoPendiente(UUID dispositivo) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable de un solo componente; Java genera el constructor, dispositivo(), equals,
 *   hashCode y toString. Como evento basta con eso: lo publica GestionarDispositivosCasoUsoImpl con
 *   ApplicationEventPublisher. Ojo: hoy ningún oyente lo escucha (no hay uno en notificacion, aunque el Javadoc lo
 *   anuncie).
 */
