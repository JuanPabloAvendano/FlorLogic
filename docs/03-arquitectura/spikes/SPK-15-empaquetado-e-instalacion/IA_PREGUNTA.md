# `SPK-15` · Empaquetado, instalación y actualización del nodo

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 15-sep-2026
> Estado: **SIN REVISAR** · Chat 5
> Manda sobre esto: `ADR-016`, `ADR-003`, `ADR-029`, `ADR-009` · `CT-06`, `CN-02`, `CN-29`, `CN-37`, `CN-15` · `ESC-14`

## Qué se explora

Cómo se empaqueta el sistema, **cuánto tarda instalarlo en una finca**, cómo se actualiza y qué pasa
cuando una actualización sale mal.

Es **el eje «código»** de `ADR-029`. El eje «dato» es `SPK-14`.

## Lo que manda

- **`ADR-016`** (Aceptada) — **un solo artefacto empaquetado** y una definición de infraestructura
  escrita como archivo. Todo lo propio de cada entorno vive en **configuración, nunca en código**.
  Instalación en la infraestructura de cada empresa, sin cambios de código, con puesta en marcha corta.
- **`CT-06` / `CN-29`** — **el mismo paquete en nube y en sitio**, y la versión nueva llega solo a
  instalaciones vigentes. Es el mecanismo de «pagas, te actualizas».
- **`ADR-003`** — una instalación por empresa; migraciones sobre **N bases** con discriminador de
  empresa desde el día uno.
- **`ESC-14`** — administración **sin línea de comandos ni acceso directo a los datos**, y sin
  instalación adicional en el computador.
- **`CN-02` / `CN-37`** — el nodo **es el producto que se compra**. Lo que mida este spike alimenta
  directamente el precio.

## Qué se mide

1. **Tiempo de instalación en limpio**, de cero a sistema utilizable. **Es un número que hoy nadie
   tiene**, y condiciona el costo de cada venta.
2. **Migraciones sobre N bases.** Aplicar una migración a varias empresas: idempotente, y reanudable
   si falla a la mitad. Qué pasa si una base queda a medias.
3. **Actualizar sin perder nada.** Actualizar con trabajo pendiente en la cola (`ADR-009` la pone
   dentro de la misma base) y con dispositivos que aún no han sincronizado.
4. **Volver atrás.** Qué ocurre si la versión nueva falla en la finca un domingo.
5. **Paridad nube/sitio.** Demostrar que es **el mismo artefacto**, no dos compilaciones parecidas.
6. **Requisitos reales del nodo:** CPU, memoria y disco **medidos, no estimados**. Alimenta el precio
   y también `ADR-036`, cuyo modelo ligero pedía del orden de 16 GB.

## `[!]` Dos cosas que hay que nombrar

1. **`ADR-016` no dice nada de rollback**, y `CN-15` exige restauración en **un día o menos**. Si no
   hay vuelta atrás, la única red de seguridad es el respaldo de `SPK-13`. **Anótalo; no lo resuelvas.**
2. **`CN-29` tiene un riesgo escrito:** la instalación que deja de pagar **diverge de versión**. Mide
   qué tan lejos puede divergir antes de que una migración deje de aplicar.

## Qué NO se decide aquí

El motor de base de datos (aplazado esta ronda) · el lenguaje del backend (`SPK-09`) · si `N4` existe
o se difiere · el precio.
