/**
 * Ingreso de sincronización · TX-1
 */
@org.springframework.modulith.ApplicationModule
package co.florlogic.finca.ingesta;

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @org.springframework.modulith.ApplicationModule: marca el paquete co.florlogic.finca.ingesta como un módulo de
 *   Spring Modulith. Desde aquí, solo lo que está en este paquete raíz (por ejemplo el evento AnotacionAceptada) es
 *   API pública; los subpaquetes (dominio, servicios, infraestructura) son internos y ModulosTest falla si otro
 *   módulo los usa.
 */
