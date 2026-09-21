# Ponencia de drivers — decisiones de Juan y lo que queda por rehacer

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 21-sep-2026, dictado por Juan mientras revisaba el mazo lámina por lámina
> Estado: **SIN REVISAR**
> Acompaña a `IA_PONENCIA-Drivers-Arquitectonicos.md` (el guion) y al mazo «FlorLogic — Drivers
> arquitectónicos» en Claude.

---

## 1 · La estructura que pidió Juan — **manda sobre el guion actual**

El mazo se hizo con cinco bloques genéricos. **Juan fijó otra secuencia**, con el trabajo de drivers
como columna vertebral:

| # | Sección | Qué entra |
|---|---|---|
| 1 | **Problema** | El proceso en la finca. `[!]` **Sin entrar en el detalle cuantificado**: no se hace referencia tan precisa a las cifras |
| 2 | **Solución** | Qué hace FlorLogic y qué queda fuera |
| 3 | **Drivers arquitectónicos** | **Es lo principal de la ponencia.** Se llama así, no «el método de drivers» |
| 3.1 | **Mini QAW** | Presentado **con las tablas humanas** del libro, no con versiones reinterpretadas |
| 3.2 | **Preguntas de caracterización** | **Solo los ejemplos más relevantes**, y relevante = aquello sobre lo que más se ha decidido y aclarado (más ADR, más información acumulada). Ejemplo que Juan dio: **el uso de la IA**, que casi no aparece en las preguntas pero es de lo que más se ha trabajado |
| 3.3 | **Escenarios de calidad** | Su mejor versión, y **uno por cada atributo principal**, en orden de peso del atributo; dentro de cada uno, el criterio de relevancia de 3.2 |
| 3.4 | **Primeras etapas de ADR** | Cómo nacieron las decisiones registradas |
| 4 | **Modelos** | Primero los **arquetipos previos al C4**, resumidos en concepto y **con imágenes de esos modelos** como referencia; después los **modelos C4** |
| 5 | **Tecnología y despliegue** | Las decisiones de tecnología y un resumen de **cómo se implementa y se despliega dentro de la finca** |

`[!]` **Observación de Juan, sin verificar:** *«creo que estos modelos C4 están incompletos frente a
las decisiones de hoy»*. Coincide con la auditoría del 14-sep, que encontró ADR sin representar y
afirmaciones del modelo que un ADR ya cambió. **Antes de proyectar el C4 hay que revisarlo o avisarlo
en voz alta.**

---

## 2 · Cambios ya aplicados al mazo

| Lámina | Qué se hizo |
|---|---|
| **1 · Portada** | Le gusta como está. Se le añadió, al lado del título, **el icono de la aplicación de captura** (`app-captura/public/icono-512.png`, la flor verde de la pestaña del navegador) |
| **2 · Agenda** | Rehecha entera. Ya no es «cómo va a ir la media hora» ni lleva la columna de «qué se lleva de ahí»: es **una agenda de cinco secciones en banda de flechas**, al estilo de la gráfica de PowerPoint que pasó Juan, con la sección activa en verde oscuro y las demás en ámbar. Bajo cada flecha, una línea de lo que contiene |

**La banda de flechas está pensada para repetirse** al abrir cada sección, moviendo el bloque oscuro
una posición a la derecha. Falta hacer esas láminas de sección.

---

## 3 · Apartado — cosas que se sacaron pero hay que tener en cuenta

- **La «regla de la charla»** —que cada cifra diga si viene del cliente, del equipo, de una medición o
  de un supuesto— **sale de la agenda**, pero **no se abandona**: sigue siendo la disciplina al
  responder preguntas. Queda pendiente decidir dónde se enuncia, si es que se enuncia.
- **El detalle cuantificado del problema** (las 4 h, los 8 días, el 2 %, el −6 %, el ~8 %) deja de ser
  el centro de la sección 1. Sigue disponible para la sesión de preguntas y para la lámina de venta.
- **Las contradicciones `X-01`, `X-07` y `X-12`** del banco de preguntas siguen abiertas y pueden
  aparecer en vivo. Ver `IA_PREGUNTAS-Y-RESPUESTAS-FlorLogic.md`.

---

## 4 · Estado de la revisión lámina por lámina

Juan va recorriendo el mazo y diciendo qué se queda y qué se va.

| Láminas | Estado |
|---|---|
| 1 Portada · 2 Agenda | ✅ **Revisadas y rehechas** |
| 3 a 24 | ⏳ **Pendientes de revisión.** Además hay que rearmarlas según la estructura del `§1`, que cambia el orden y el peso de casi todo |
