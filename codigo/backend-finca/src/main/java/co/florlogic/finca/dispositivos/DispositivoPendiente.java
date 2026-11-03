package co.florlogic.finca.dispositivos;

import java.util.UUID;

/**
 * Evento: un celular desconocido abrió sesión y espera aprobación (CT-10). Lo escuchará {@code notificacion} para
 * avisar al administrador; ese oyente es de la fase F5 y todavía no existe: hoy el evento se publica y nadie lo usa.
 */
public record DispositivoPendiente(UUID dispositivo) {
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - record: clase de datos inmutable de un solo componente; Java genera el constructor, dispositivo(), equals,
 *   hashCode y toString. Como evento basta con eso: lo publica GestionarDispositivosCasoUsoImpl con
 *   ApplicationEventPublisher. Hoy ningún oyente lo escucha: el de notificacion llega en la fase F5.
 */
