/**
 * Métricas, logs, telemetría con esquema cerrado · TX-1 · TX-2
 */
@org.springframework.modulith.ApplicationModule
package co.florlogic.finca.observabilidad;

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
 */
