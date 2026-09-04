# Caracterización por atributo de calidad — cómo se usa

**La fuente es `PREGUNTAS_CARACTERIZACION.xlsx`**, hoja de preguntas: 262 filas sobre 13 atributos,
con las columnas `Identificador · Atributo · Bloque · Pregunta · Respuesta · Nota · Escenario`.
Este archivo solo guarda el **método**, que no cabe en una celda.

> La versión `.md` de estas mismas preguntas se erradicó el 4-sep-2026: el xlsx ya tenía las 262 con
> el texto de la v3, y dos copias del mismo cuestionario es exactamente cómo se responde la
> equivocada. Está en `_to_delete/obsoletos-2026-09-04-b/`.

## Qué es la v3 (27-ago-2026)

**Cada pregunta está escrita como escenario, no como funcionalidad.** Contiene los **seis elementos**
del modelo de Bass/Clements/Kazman —entorno, fuente del estímulo, estímulo, artefacto, respuesta y
medida de respuesta— en ese orden y dentro de una sola frase interrogativa, igual que el párrafo
narrativo de los `ESC-nnn`.

Frente a la v2 **no se agregó ni se quitó ninguna pregunta**, ninguna cambió de intención y **no se
inventó ningún número**: la medida sale del propio escalón de la fila.

## Las cuatro reglas de uso

1. **Se responden Sí / No.** El matiz va en *Nota*, nunca dentro de la pregunta.
2. **El orden de las filas no se altera.** Dentro de cada bloque las preguntas escalan la misma idea
   de menor a mayor exigencia —dato → sección → cama → bloque → finca; en el momento → al sincronizar
   → después—. **El punto donde el cliente pasa de «Sí» a «No» es la medida de respuesta** del
   escenario que se va a escribir.
3. **Al responder «Sí», el escenario se escribe directo desde la fila**; al responder «No», la fila
   anterior de la escalera es la que fija la medida. Un «Sí» no obliga a escribir escenario: obliga a
   decidir. **Un «No» también sirve**: cierra alcance y justifica descartes.
4. **No se pregunta *quién* tiene la necesidad.** Un escenario exige fuente del estímulo, así que se
   nombra de forma genérica y estructural —*quien captura*, *quien consulta*, *un auditor externo*,
   *el dispositivo*— y nunca con los tres roles del mini QAW. Si al responder el cliente atribuye la
   necesidad a alguien en particular, **eso se anota como hallazgo**, no como parte de la pregunta.

## Prefijos

`ESL-` es Escalabilidad. **`ESC-` está reservado para los escenarios de calidad** y no se usa aquí.

## Dónde está el resto

- El ranking del mini QAW y los empates: `Documentacion/Drivers-Arquitectonicos/DRIVERS_ARQUITECTONICOS.md` §6 y §7.
- Los escenarios que salieron de esto: `EscenariosCalidad.xlsx`, explicados en `DRIVERS §9`.
- El estado de cualquier decisión: `Documentacion/Archivo/Recopilacion/3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md`.
