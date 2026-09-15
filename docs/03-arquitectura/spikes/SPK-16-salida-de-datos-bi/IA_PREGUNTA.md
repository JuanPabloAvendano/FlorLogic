# `SPK-16` · Salida de datos hacia la herramienta de BI del cliente

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 15-sep-2026
> Estado: **SIN REVISAR** · Chat 6
> Manda sobre esto: `ADR-013`, `ADR-010` · `CN-10`, `CN-16`, `CN-17` · `CT-05`, `ESC-29`, `RF-012`

## Qué se explora

Cómo sale la información de la instalación hacia la herramienta de análisis del cliente:
**autenticada, acotada a su empresa y sin abrir la base**.

Es de lo poco que el cliente pidió con nombre propio. `CN-10` lleva su nota literal: **«POWER BI»**.

## Lo que manda

- **`ADR-013`** (Aceptada) — dos caminos, **y ninguno es una interfaz pública**: exportación
  autenticada a hoja de cálculo y PDF, generada en segundo plano; y lectura directa. Bajo local-first
  la herramienta del cliente conecta **contra su propia instalación**, no contra una nube nuestra.
  *(`DEC-06` quedó derogada: sí se integra con terceros.)*
- **`CT-05`** — entrega datos acotados a la empresa.
- **`ESC-29`** — **0 accesos directos a la base** · **100%** de las consultas limitadas a la empresa
  del solicitante · **extracción de un año de historia en 10 minutos o menos**.
- **`ADR-010`** (Propuesta) — las tablas de consulta derivadas **son las mismas que se le exponen al
  cliente**: se construyen una vez y sirven para los dos usos.
- **`CN-17`** — hay red en las oficinas, pero el sistema **no depende de internet**.

## Qué se mide

1. **Un año de historia en ≤10 minutos**, que es el número de `ESC-29`. Con Power BI real, o con un
   cliente que hable el mismo protocolo.
2. **Que no se pueda saltar la interfaz.** Comparar las tres formas —rol de solo lectura sobre la
   base, endpoint REST propio, conector OData— y **demostrar** en cuáles queda un camino directo a la
   base abierto. `ESC-29` pide cero.
3. **Acotamiento por empresa.** Que una credencial no pueda ver otra empresa. 100%, sin excepciones.
4. **Refresco concurrente.** Qué ocurre cuando el cliente refresca su tablero justo mientras entra
   una sincronización. Es el choque real entre `SPK-12` y este camino.
5. **Sin internet.** Comprobar que todo el camino funciona dentro de la red de la finca.

## `[!]` Una dependencia y una corrección pendiente

**Dependencia:** `ADR-010` dice que estas tablas son las mismas de los tableros internos, y la
Ronda 0 —si `ADR-010`, `ADR-023` y `ADR-034` son tres mecanismos o uno— todavía no está cerrada. Eso
cambia **qué** se expone, no **cómo**. **Mide el cómo** y anota la dependencia; no esperes a la Ronda 0.

**Corrección pendiente del levantamiento:** `ESC-29` arrastra una observación —*«la decisión vigente
excluye Power BI»*— que **hay que borrar**, porque `B5` revirtió esa decisión. Es una de las siete
correcciones de escenarios que siguen sin aplicar. **Anótalo en tu resultado; no toques el `.xlsx`.**

## Qué NO se decide aquí

Qué tablas se exponen (depende de la Ronda 0) · el motor de base de datos (aplazado) · el lenguaje
del backend (`SPK-09`) · la generación de Excel y PDF, que es otro spike (`RF-019`, `ESC-51`).
