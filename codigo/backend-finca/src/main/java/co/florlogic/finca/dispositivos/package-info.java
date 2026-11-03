/**
 * Gestión de dispositivos y asignación · TX-1
 */
@org.springframework.modulith.ApplicationModule
package co.florlogic.finca.dispositivos;

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @org.springframework.modulith.ApplicationModule: anotación de Spring Modulith puesta sobre el paquete (por eso vive
 *   en package-info.java, el único archivo donde Java deja anotar un paquete). Declara que co.florlogic.finca.dispositivos
 *   y sus subpaquetes forman un módulo: los demás solo pueden usar los tipos públicos de este paquete raíz
 *   (RegistroDeDispositivos, DispositivoPendiente, DispositivoRegistrado, EstadoDeDispositivo), y la prueba de
 *   módulos (ModulosTest) falla si otro módulo entra a un subpaquete de este.
 */
