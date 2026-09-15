# `SPK-10` · Motor de base de datos y forma del dato

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 14-sep-2026
> Estado: **APLAZADO 14-sep-2026** · Fuera de la ronda del 14-sep, que es solo Backend de la finca. La base de datos es un contenedor aparte y depende de la Ronda 0 (`ADR-010`/`023`/`034`).
> *(El contenido de abajo se conserva intacto para cuando se retome.)*
> Manda sobre esto: `ADR-024`, `ADR-023`, `ADR-010`, `ADR-034` en `ADR-PoC-Spikes.xlsx`

## Qué se decide

Qué motor sostiene la base de cada empresa, y si la forma del dato de `ADR-024` —relacional con
`JSONB` solo para el valor— aguanta la ingesta y la consulta.

**Y de paso responde si `ADR-034` hace falta.** El caché se decidió por razonamiento después de
rechazar `ADR-033`; nunca se midió. Un caché puesto antes de tener un problema de latencia medido
regala un problema de invalidación gratis.

## `[!]` Precondición: la Ronda 0

**No empezar sin que Juan cierre si `ADR-010`, `ADR-023` y `ADR-034` son tres mecanismos o uno.**
Tablas de consulta derivadas, totales precalculados en el cierre y caché delante de la base atacan el
mismo problema. Si son tres, hay que medir tres. Si es uno, esta prueba es la mitad de larga.

## Por qué existe la pregunta

- `ADR-024` (Propuesta): cada anotación es una fila con producción, sección, campo, valor, momento y
  versión del catálogo. Juan la dejó en Propuesta **precisamente para analizar más a fondo la forma
  de almacenar**, y `ADR-035` le cambió la tupla (el autor sube a la sesión de captura).
- `ADR-023`: el histórico se consulta por totales ya calculados y carga progresiva.
- **`ADR-033` fue Rechazada** porque su premisa era falsa: el conjunto activo **no** está acotado por
  la geografía. Eso invalidó el dimensionamiento de `ADR-022`. **Hay que rehacer el cálculo, y con
  números.**

## Qué se mide

1. **Pico de ingesta de una jornada.** Varios dispositivos sincronizando a la vez, con idempotencia
   y «gana el más reciente» (`ADR-027`, `ADR-031`).
2. **Consulta del histórico** de `ADR-023` sobre un volumen de varios años.
3. **La misma consulta con y sin caché**, para que `ADR-034` se decida con un número.
4. **Crecimiento real** del almacenamiento por ciclo de producción, para rehacer el dimensionamiento
   que `ADR-033` dejó sin suelo.

## Los datos

Preferible **el histórico real del Access** (ver `SPK-11`); sintético solo si ese camino falla.
Medir sobre datos inventados el volumen de una finca de ocho años es el error que ya se cometió una
vez al razonar `ADR-033`.

## Candidatos

PostgreSQL es la propuesta escrita. La prueba debe decir si hay motivo para mirar otra cosa, y sobre
todo **cómo se modela**: dónde acaba lo relacional y dónde empieza el `JSONB`.

## Qué NO se decide aquí

El modelo de datos en sí (`ADR-024` es de Juan) · la retención (`ADR-022`) · el cierre de producción,
que **todavía no tiene ADR** y del que depende cuándo se materializan los totales.
