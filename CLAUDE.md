# FlorLogic — instrucciones para Claude

Proyecto de arquitectura (sector floricultor): levantamiento de requisitos, drivers, ADR, spikes
de tecnología y un prototipo desechable de captura en campo. Arquitecto y dueño de las decisiones:
Juan Pablo. Todo el repo está en español.

## Por dónde entrar

- `docs/README.md` — mapa de la documentación. Léelo primero.
- `docs/03-arquitectura/FlorLogic-alternativa-de-solucion-y-ADR.md` — la entrada única a la solución.
- `docs/03-arquitectura/FlorLogic-tandas-de-construccion.md` — orden de construcción.
- `docs/03-arquitectura/decisiones/` — lo que Juan ya decidió y aún no es ADR.
- `Documentacion/Drivers-Arquitectonicos/` — los Excel de drivers y el libro maestro de ADR.

## Reglas que mandan

- **`Documentacion/Drivers-Arquitectonicos/` no lo edita la IA.** Es fuente humana; el ADR lo
  escribe Juan.
- Todo lo que genera la IA lleva prefijo `IA_` en el nombre y una cabecera de cuatro líneas: qué es,
  quién lo generó, estado de revisión y qué documento manda por encima. Reglas completas en
  `docs/03-arquitectura/spikes/LEEME-ZONA-IA.md`.
- Estados: `SIN REVISAR` · `REVISADO POR JUAN <fecha>` · `APLAZADO <fecha> · <motivo>` ·
  `DESCARTADO <fecha> · <motivo>`. Ningún documento humano cita uno `SIN REVISAR`.
- Un registro de IA de más de dos páginas se parte; la evidencia larga va a `labs/`, que vive fuera
  de este repo (OneDrive) y no se toca.
- `IA_BITACORA.md` es solo de añadir (append-only).

## Ramas

`documentacion-desarrollo` (rama por defecto, donde está el trabajo) → `documentacion-lista` → `main`.
Código: `codigo-desarrollo` → `codigo-testing` → `codigo-listo` → `main`.
`main` en GitHub sigue casi vacío (commit inicial).

## Prototipo `app-captura/`

PWA en TypeScript, desechable (`PoC-0`). `dist/` viene construido: `python servir.py` y listo.
Pruebas en `app-captura/pruebas/` (`python pruebas/todas.py`).

## Formatos

Mucho `.xlsx`, `.drawio`, `.archimate`, `.pptx` y mazos `.html`. Los `.xlsx` se leen y editan con
Python (openpyxl); al editar uno, conservar hojas, formatos y colores existentes.
