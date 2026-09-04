# Documentación de FlorLogic

Esta carpeta contendrá la documentación del proyecto. El trabajo en curso se
hace en la rama `documentacion-desarrollo`; una vez revisada, se integra a
`documentacion-lista` y finalmente a `main`.

## Dónde está cada cosa

El proyecto tiene la documentación repartida en dos árboles:

- **`Documentacion/Drivers-Arquitectonicos/`** — los cuatro Excel de drivers
  (funcionalidades significativas, restricciones de negocio, restricciones
  técnicas y escenarios de calidad) más `DRIVERS_ARQUITECTONICOS.md`, que los
  explica y los recoge. **Es la entrada al levantamiento.**
- **`Documentacion/Archivo/`** — entrevistas, transcripciones, recopilación de
  decisiones, modelos y diagramas. Ver su propio `README.md`.
- **`docs/`** — esta carpeta, con la documentación de arquitectura.

**Y dentro de `docs/03-arquitectura/`, dos documentos mandan sobre la solución:**

- `FlorLogic-alternativa-de-solucion-y-ADR.md` — **la entrada única.** La alternativa
  elegida, las 31 decisiones de arquitectura registradas, los bloques, los ocho
  spikes y la cobertura escenario por escenario.
- `FlorLogic-tandas-de-construccion.md` — **el orden en que se construye.** Qué
  tanda arranca ya, qué ADR la sostiene, qué falta antes de empezarla y cuándo se
  da por terminada. No decide nada: ordena lo que los ADR decidieron.

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
