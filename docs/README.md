# Documentación de FlorLogic

Esta carpeta contendrá la documentación del proyecto. El trabajo en curso se
hace en la rama `documentacion-desarrollo`; una vez revisada, se integra a
`documentacion-lista` y finalmente a `main`.

## `[!]` Si solo vas a leer una cosa

**`docs/03-arquitectura/FlorLogic-alternativa-de-solucion-y-ADR.md`** — la entrada única a la
solución. Y si lo que buscas es **el estado de la campaña de tecnología**, es
**`docs/03-arquitectura/spikes/IA_SPIKES-resumen.xlsx`**.

## Dónde está cada cosa

El proyecto tiene la documentación repartida en dos árboles:

- **`Documentacion/Drivers-Arquitectonicos/`** — los cuatro Excel de drivers
  (funcionalidades significativas, restricciones de negocio, restricciones
  técnicas y escenarios de calidad) más `DRIVERS_ARQUITECTONICOS.md`, que los
  explica y los recoge, y **`ADR-PoC-Spikes.xlsx`, que es el libro maestro de ADR.**
  **Es la entrada al levantamiento, y la IA no edita nada de aquí.**
- **`Documentacion/Archivo/`** — entrevistas, transcripciones, recopilación de
  decisiones y material de entrevistas. Ver su propio `README.md`.
- **`docs/`** — esta carpeta, con la documentación de arquitectura.

**Y dentro de `docs/03-arquitectura/`, cuatro sitios mandan:**

- `FlorLogic-alternativa-de-solucion-y-ADR.md` — **la entrada única.** La alternativa
  elegida, las decisiones de arquitectura registradas, los bloques, los spikes y la
  cobertura escenario por escenario.
- `FlorLogic-tandas-de-construccion.md` — **el orden en que se construye.** Qué
  tanda arranca ya, qué ADR la sostiene, qué falta antes de empezarla y cuándo se
  da por terminada. No decide nada: ordena lo que los ADR decidieron.
- `decisiones/` — **lo que Juan ya decidió y todavía no es ADR.** El hueco entre la
  conversación y el libro maestro, fechado, con sus motivos. No es un libro de ADR
  paralelo. Ver su propio `README.md`.
- `spikes/` — **la zona de registros de IA de la campaña de tecnología**, con reglas
  propias en `LEEME-ZONA-IA.md`. Nada de ahí es fuente, y ningún documento humano lo
  cita mientras esté `SIN REVISAR`. **El punto de entrada es
  `spikes/IA_SPIKES-resumen.xlsx`**: los seis spikes medidos en una fila cada uno, los
  spikes abiertos imprescindibles en rojo, y lo que ya no se mide sino que se decide.
  El código y los datos de cada spike viven en `labs/`, **fuera de este repo**.

## Estructura de esta carpeta

- `contenido-pendiente/` — carpeta de recepción para el material que se irá
  adjuntando (notas, archivos, referencias) antes de organizarlo en las
  secciones definitivas de abajo.
- `01-vision-general/` — propósito del proyecto, alcance, objetivos.
- `02-requerimientos/` — requerimientos funcionales y no funcionales.
- `03-arquitectura/` — diseño técnico, decisiones de arquitectura.
- `04-manual-usuario/` — guías de uso para el usuario final.

Estas secciones son un punto de partida y se pueden renombrar o reorganizar
cuando se defina el contenido real.
