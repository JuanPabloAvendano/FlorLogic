/**
 * Planificador, cola en la base · TX-1 · TX-2
 */
@org.springframework.modulith.ApplicationModule
package co.florlogic.finca.trabajos;

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - @org.springframework.modulith.ApplicationModule (Spring Modulith): declara este paquete como la raíz de un módulo
 *   de la aplicación. Lo que vive directamente aquí es la API pública del módulo; los subpaquetes (dominio, servicios,
 *   infraestructura) quedan internos, y ModulosTest (ApplicationModules.verify) falla si otro módulo los usa.
 * - package-info.java: archivo especial de Java que no declara clases; solo existe para poner el Javadoc y las
 *   anotaciones que valen para el paquete entero.
 */
