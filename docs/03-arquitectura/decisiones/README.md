# `decisiones/` — lo que Juan ya decidió y todavía no es ADR

> **DOCUMENTO HUMANO.** Lo que hay aquí lo decidió Juan.
> Manda sobre esto: `Documentacion/Drivers-Arquitectonicos/ADR-PoC-Spikes.xlsx` (el libro maestro)
> y `DRIVERS_ARQUITECTONICOS.md` con sus cuatro `.xlsx`.

## Qué es esta carpeta

El hueco entre «Juan ya decidió esto» y «está escrito como ADR en el `.xlsx`». Una decisión tomada
en una conversación se pierde si no se escribe, y el `.xlsx` se llena por tandas, no al momento.
Aquí queda la decisión con sus motivos, fechada, hasta que entre al libro.

## Qué NO es

**No es un libro de ADR paralelo.** El libro maestro es `ADR-PoC-Spikes.xlsx` y no tiene competencia.
Un archivo de aquí **no se cita como si fuera un ADR** y no numera decisiones con el espacio de
nombres `ADR-xxx` ni `DEC-xx` (ese ya lo usa la recopilación). Se cita por su nombre de archivo.

Tampoco es la zona de registros de IA: eso es `spikes/`, con sus reglas propias
(ver `spikes/LEEME-ZONA-IA.md`).

## Cómo se cierra un archivo de aquí

Cada archivo lleva arriba una línea **`Va al ADR:`** en blanco. Cuando la decisión entra al `.xlsx`,
Juan escribe ahí el número. A partir de ese momento **manda el ADR** y este archivo queda como
registro de por qué se decidió así, no como fuente.

## Convención de nombre

`aaaa-mm-dd-tema-en-minusculas.md`. La fecha es la de la decisión, no la de la última edición.

## Cuando un archivo de aquí incluye material de IA

Algunos archivos traen un apartado escrito por la IA (por ejemplo, el resumen de lo que se midió en
un spike). Ese apartado va **encerrado y marcado** con su propio estado de revisión, y se puede
tirar entero sin tocar la decisión de Juan. La decisión es humana; el apartado es insumo.

## Qué hay hoy

| Archivo | Decisión | Va al ADR |
|---|---|---|
| `2026-09-15-reglas-en-json-y-captura-incompleta.md` | Las reglas se mantienen en JSON · un obligatorio incumplido no frena la captura ni la sincronización | *pendiente* |
