# `SPK-12` · Ingreso y sincronización

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 14-sep-2026
> Estado: **SIN REVISAR** · Chat 2 de 3
> Manda sobre esto: `ADR-025`, `ADR-026`, `ADR-027`, `ADR-028`, `ADR-031`, `ADR-035`, `CT-03` · `RF-003`, `RF-022`

## Qué se explora

La tecnología y la **forma del contrato de entrada**: lo que recibe la sesión, descarta duplicados,
ordena lo que choca y lo persiste. Es la pieza que **decide si el dato es confiable**, y equivocarse
es de lo más caro del backend porque cuando se nota, el dato ya entró.

## Lo que manda, y hay que respetar literalmente

- **`CT-03` / `RF-003`** — entrega **idempotente y reanudable**, con ventanas de **15 días o más**.
- **`ADR-027`** — la identidad técnica de cada anotación es un código único que genera el dispositivo,
  **opaco**; la **clave de hecho va aparte**; hay **tres casos de idempotencia** y cuatro reglas de
  `corrige_a`. Léelos en el libro: son el corazón de este spike.
- **`ADR-028`** — la sesión de sincronización es **del transporte, no de la captura**: entidad del
  servidor, bidireccional, **y es la que mide el desvío del reloj**. No es la unidad de retención.
- **`ADR-031`** — **tres tiempos que no se mezclan nunca**: el sello crudo del dispositivo (no se toca
  jamás, sirve para auditar), el **sello normalizado por el desvío** (es el que ordena `RF-022`) y el
  tiempo de sesión, con **desempate determinista**.
- **`ADR-035`** — la **sesión de captura** es la unidad de trazabilidad y le pone tiempo a los datos.
  **No es una sola cama**: abarca varias camas o bloques.
- **`ADR-025`** — cadencia diaria; ante pérdida de dispositivo **se recaptura, no se recupera**.
  La pérdida aceptable está acotada a ≤1 jornada de un capturador, con lista de camas a rehacer.
- **`ADR-026`** — **lo asignado es el denominador** de lo que falta: qué bloques y camas le tocan a
  ese dispositivo y en qué jornada viajan en el paquete. **La ausencia de reporte es la señal.**
- **`ADR-014`** — ante hora dudosa se **marca y se pide confirmación, no se bloquea**.

## `[!]` Dos huecos conocidos que este spike debe tocar

1. **Nadie calcula el desvío del reloj.** `ADR-028` dice que la sesión lo mide y `ADR-031` dice que el
   sello normalizado ordena `RF-022` — pero en el modelo no existe ninguna pieza que lo calcule ni lo
   guarde. **Sin eso, «gana el más reciente» no significa nada.** Es el fallo que apareció el 4-sep.
2. **La bitácora cuelga de la sesión equivocada.** El modelo dice «registra por sesión de
   sincronización»; `ADR-035` la movió a la **sesión de captura**. Anótalo en «Dependencias»;
   no lo arregles en el modelo.

## Qué se mide

1. **Idempotencia real.** Reenviar la misma sesión N veces, cortarla a la mitad y reanudarla,
   reenviarla desordenada → **0 duplicados, 0 pérdidas**, y los **tres casos de `ADR-027`** cubiertos.
2. **El desvío del reloj.** Que el servidor lo calcule y que el sello normalizado **ordene igual** que
   el reloj del servidor, incluso con un dispositivo adelantado. Es la prueba central.
3. **Pico de ingesta de una jornada.** Tres capturadores volviendo a la oficina a la vez con la cola
   de todo el día. Mide latencia y comportamiento bajo concurrencia.
4. **Ventana larga.** Una cola de 15 días o más que entra completa y reanudable.
5. **Choque real.** Dos dispositivos con la misma cama el mismo día → qué hace el desempate
   determinista. *(`BR-N4` sigue sin respuesta del cliente: mide el mecanismo, no decidas la política.)*

## `[!]` Restricción de este chat

**No asumas un lenguaje.** `SPK-09` está explorando caminos **sin elegir**. Si una medición depende
del lenguaje, hazla en dos lenguajes distintos o declárala dependiente y di de qué. Lo que sí se puede
fijar aquí es **la forma del contrato**: qué viaja, cómo se lotea, cómo se reanuda, cómo se acusa.

## Qué NO se decide aquí

El lenguaje del backend (`SPK-09`) · el motor de base de datos (aplazado esta ronda; trabaja contra
el esquema como contrato) · **qué es «la jornada»** ni la granularidad campo por campo (`D1`, del
cliente) · `BR-N4` · la política de correcciones (`ADR-032`).
