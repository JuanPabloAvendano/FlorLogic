/**
 * Núcleo compartido del backend de finca (Evans · Domain Driven Design secc. 112, Shared Kernel): los valores
 * que usan todos los módulos. Pequeño y acordado; Java puro. Vacío hasta que un módulo lo pida.
 */
@org.springframework.modulith.ApplicationModule
package co.florlogic.finca.dominio;

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - package-info.java: archivo especial de Java sin clase; solo lleva el Javadoc y las anotaciones del paquete
 *   entero. Aquí declara que este paquete es la raíz (la API pública) de un módulo del backend.
 * - @org.springframework.modulith.ApplicationModule (Spring Modulith): marca el paquete como módulo de la
 *   aplicación. Va sin atributos: con la detección por defecto, Modulith ya toma cada subpaquete directo de
 *   co.florlogic.finca como módulo; la anotación lo deja explícito y es el sitio donde irían, si hicieran falta,
 *   un nombre visible (displayName) o la lista de módulos de los que puede depender (allowedDependencies).
 *   ModulosTest verifica que los demás módulos solo usen las clases de este paquete raíz y nunca sus
 *   subpaquetes (servicios, infraestructura, dominio internos).
 *   Aunque hoy está vacío, ModulosTest exige que exista entre los diecisiete módulos.
 */
