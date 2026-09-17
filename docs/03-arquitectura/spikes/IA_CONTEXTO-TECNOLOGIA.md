# Contexto para las pruebas de tecnología

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 14-sep-2026
> Estado: **SIN REVISAR**
> Manda sobre esto: `ADR-PoC-Spikes.xlsx` (36 ADR) · `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx`

> ## `[!]` TAREA PRINCIPAL DE LAS PRÓXIMAS SESIONES — desde el 16-sep-2026
>
> **Preparar la exposición de 30 minutos de FlorLogic** ante un ingeniero de sistemas y líder de
> arquitectura de software. Vale para **toda** sesión, con Juan **o con Jerónimo**, y va **antes** que
> cualquier spike nuevo.
>
> - Guion: **`IA_PRESENTACION-FlorLogic.md`** (raíz del repo)
> - Entrenamiento: **`IA_PREGUNTAS-Y-RESPUESTAS-FlorLogic.md`** (raíz del repo) — primero el tablero
>   de contradicciones `X-01`..`X-20` (cada una: consenso · Juan · Jerónimo · cliente), luego las
>   sesiones `S1`..`S12` en orden.
> - **Terminado cuando** cualquiera de los dos expone los 30 minutos sin notas y responde el banco sin
>   inventar: lo que no se sabe se dice con nombre y dueño.
> - Al empezar cada sesión: preguntar qué `X-nn` se cerraron y qué sesión `S` toca.

**Esto es lo primero que lee cada chat de pruebas.** Una página. Si algo de aquí contradice al
`.xlsx` de ADR o a los drivers, **manda el documento humano** y este archivo está desactualizado.

## El problema que se resuelve

FlorLogic proyecta producción de flor de corte a partir de captura en campo sin conexión. El dolor
que paga el cliente **no es la velocidad de captura**: es el traslado de papel a digital (~4 h por
tanda) y la proyección manual (hasta 8 días, por el ciclo de corrección). **Toda prioridad se juzga
contra esas dos cifras.**

Equipo: dos personas. Entrega: **local-first**, una instalación por empresa (`ADR-001`, `CN-37`).

## Decidido y no se reabre en estas pruebas

| | |
|---|---|
| `ADR-001` | Local-first, una instalación por empresa. **No SaaS.** |
| `ADR-024` | Cada campo capturado es un hecho; la cama es la unidad de captura. Relacional + `JSONB` solo para el valor |
| `ADR-006` | Motor de reglas guiado por datos: reglas en artefacto versionable, no en código |
| `ADR-016` | Un solo artefacto empaquetado; lo del entorno vive en configuración, nunca en código |
| `ADR-013` | Interoperabilidad por exportación **y** lectura directa. Ninguna interfaz pública |
| `ADR-009` | La cola de trabajos vive **dentro de la misma base**. Pieza aparte solo si la medición obliga |
| `ADR-010` | Los tableros se construyen sobre tablas de consulta derivadas, no sobre hechos crudos |

## `[!]` Lo que NO se toca

**El cliente de captura.** `ADR-008` aplaza deliberadamente la tecnología del dispositivo: el
prototipo y las mediciones **siguen sobre la aplicación web**, y solo se pasa a aplicación instalada
si se cumple uno de tres disparadores (ventana sin abrir ≥ una jornada · el cliente exige demostrar
el cifrado · hay iPhone reales). Abrir esa discusión es gastar una ronda en algo ya decidido.

## Pendiente de Juan antes de medir (Ronda 0)

1. **Si `ADR-010`, `ADR-023` y `ADR-034` son tres mecanismos o uno.** Tablas de consulta derivadas,
   totales precalculados y caché atacan el mismo problema de lectura. Sin esto, `SPK-10` mide mal.
2. **La ventana de la credencial offline** (24 h → 7–10 días). `ADR-007` lo pide *antes* de comparar
   proveedores de identidad.
3. **Qué versión del modelo C4 manda.** Hoy: la de la nube de Juan, todavía no compartida. El
   `.drawio` de la carpeta queda congelado.

## Lo que se está midiendo ahora

`SPK-09` motor de reglas · `SPK-10` motor de base de datos · `SPK-11` lectura de Access.
`SPK-09` va primero en importancia: **decide el lenguaje del backend**.

## Cómo cierra un chat

1. Escribe `IA_RESULTADO.md` en la carpeta de su spike, partido en **Medición** y **Lectura**, con
   el comando para repetir y la sección «Qué NO probé».
2. Añade **una línea** a `IA_BITACORA.md`. No edita lo anterior.
3. **No escribe ADR.** Entrega el insumo; el ADR lo redacta Juan en el `.xlsx`.
