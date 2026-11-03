/**
 * Ciclo de producción · TX-1
 */
@org.springframework.modulith.ApplicationModule
package co.florlogic.finca.ciclo;

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @org.springframework.modulith.ApplicationModule: anotación de Spring Modulith puesta sobre el paquete (por eso vive
 *   en package-info.java, el único archivo donde Java deja anotar un paquete). Declara que co.florlogic.finca.ciclo y
 *   todos sus subpaquetes forman un módulo: los demás módulos solo pueden usar los tipos públicos de este paquete raíz
 *   (hoy no hay ninguno: el ciclo solo escucha el evento de ingesta y sirve la web del gerente), y la prueba de
 *   módulos (ModulosTest) falla si otro módulo entra a un subpaquete de este.
 */
