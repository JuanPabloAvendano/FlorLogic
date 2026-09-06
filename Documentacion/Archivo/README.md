# Archivo

Aquí está todo el material que **no** es un driver arquitectónico. Se sacó de `Documentacion/` el
2-sep-2026 para dejar `Documentacion/Drivers-Arquitectonicos/` con los cuatro Excel de drivers y el
documento que los explica, y nada más.

**No se borró nada de lo que hay aquí.** Sigue siendo material válido y varias cosas siguen siendo
fuente primaria; simplemente no forma parte del entregable de drivers.

| Carpeta | Qué hay | Para qué sirve |
|---|---|---|
| `Levantamiento de requisitos/` | Transcripciones `.vtt` de las cuatro sesiones, cuestionarios de entrevista, plantilla de captura de la empresa, pitch y mapa de impacto | **Fuente primaria.** Es contra esto que se verifica cualquier afirmación sobre el negocio |
| `Recopilacion/` | La voz del cliente, la voz del equipo, y la guía de decisiones `DEC`/`A`/`B`/`C`/`D`/`E` partida en **guía** (estado + grupo `D`) y **anexo** (las 50 entradas cerradas, íntegras) | **Manda sobre el estado de cualquier decisión de negocio.** Sobre la solución técnica manda el documento de ADR |
| `Mini-QAW-versiones-anteriores/` | La versión larga de los escenarios documentados y la lluvia de escenarios v1 | Versiones de trabajo previas a `EscenariosCalidad.xlsx` |
| `Contexto-y-notas/` | *(vacía desde el 4-sep-2026)* | Contenía la copia de trabajo v1.0 de los drivers. Ver abajo |

## Lo que se erradicó

Todo en `_to_delete/`, y nada se perdió: sigue en git.

| Archivo | Por qué salió |
|---|---|
| **`Modelo-y-construccion/` entera** *(6-sep-2026)* | El modelo de componentes del **31-ago**, generado con IA: cinco `.md`, nueve diagramas y dos xlsx. Es anterior a `ADR-024`..`ADR-031`, traía **una segunda numeración de bloques** (31, contra los 17 `BB-nn` que manda) y se declaraba «sin validar». Está en `_to_delete/modelo-construccion-31ago-2026-09-06/` |
| `Contexto-y-notas/5_NOTAS_DE_TRABAJO_DRIVERS.md` | Copia de trabajo de la **v1.0** de los drivers, congelada el 26-ago. La v2.0 la contiene entera y además trae los escenarios. Un documento que declara estar obsoleto se sigue leyendo igual |
| `Recopilacion/4_CIERRES_GRUPO_C.md` | Hoja de ratificación del grupo `C`. **Las diez están cerradas** —`C4`, `C6` y `C8` en la ronda 5; `C2` en `ADR-030`— y el estado vive en `3_DECISIONES...md` |
| `Levantamiento .../PREGUNTAS_CARACTERIZACION.md` | El xlsx ya tenía las 262 preguntas con los `CNF-nn` y el texto de la v3. El método de uso quedó en `LEEME-caracterizacion.md` |

## Lo que sí manda hoy

**Modelo de entrega, requisitos, restricciones, atributos y escenarios:**
`Documentacion/Drivers-Arquitectonicos/DRIVERS_ARQUITECTONICOS.md` y los cuatro Excel de esa carpeta.
**Estado de cualquier decisión:** `Recopilacion/3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md`.
**Voz literal del cliente, por encima de todo lo demás:** `Recopilacion/1_VOZ_DEL_CLIENTE.md`.
**Arquitectura, ADR, spikes y cobertura de escenarios:**
`docs/03-arquitectura/FlorLogic-alternativa-de-solucion-y-ADR.md`.
