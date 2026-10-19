# Revisión y verificación de un segmento

Etapas 5 (revisión) y 6 (verificación). Fuente: `investigacion/05`. Los umbrales y las fórmulas están en
`indice-precision.md` §2 y §3; este archivo dice **cómo se trabaja**.

## 1 · Tamaño del segmento

- Unas 600 líneas en promedio; el tope es flexible.
- **Un archivo nunca se corta.** Si el segmento pasa de 600 y al archivo le faltan 300 líneas o menos,
  se termina en el mismo segmento.
- Un archivo de más de 900 líneas va solo, como su propio segmento.
- La revisión se hace en **pasadas de 300 líneas o menos**. Se cuentan líneas de lógica: las pruebas
  valen 0,5 y el código declarativo 0,25 (`05` §1.2).
- Si el segmento tiene más de 12 archivos, se muestra primero el mapa de dependencias.

## 2 · Presentar el segmento

Antes de la primera pasada, el agente muestra la ficha del segmento (`05` §2.5):

```
## Segmento N/M — <nombre>
Cubre: <ids de la ESPEC>          No cubre: <ids → en qué segmento van>
Tamaño: <brutas> / ~<lógica> de lógica → <k> pasadas
Entrada: <archivo:símbolo>        Orden de lectura: 1) … 2) … 3) …
Decisiones implícitas: D1 … (con porqué)
Puntos sensibles: <archivo:línea — atributo de calidad>
Matriz requisito → código → prueba
```

**Orden de lectura, de afuera hacia adentro:**
1. Contrato externo
2. Pruebas de aceptación
3. Núcleo (caso de uso y dominio)
4. Adaptadores
5. Resto de pruebas y soporte

Si el segmento es solo dominio, se lee de adentro hacia afuera.

## 3 · Modo revisión

- El foco queda **bloqueado** en el segmento y en lo que se usó para generarlo.
- **Solo el usuario cierra la revisión.**
- Reglas para el agente (`05` §3.1):
  - Ancla cada respuesta a `archivo:línea`.
  - Responde primero en 1 o 2 frases y después da el detalle.
  - Antes de afirmar algo, relee el código. Si no estás seguro, dilo.
  - **No reescribas código durante la revisión: anota.**
  - Si la pregunta es de «por qué», explica el concepto y el patrón antes que el código.
- Mientras responde, el agente se hace las preguntas internas de `05` §3.2: ¿hay un caso sin manejar?
  ¿una decisión que no está en la ESPEC? ¿una afirmación sin prueba? ¿un nombre engañoso? ¿una API que
  no verifiqué? Cada «sí» se convierte en una observación.

**Formato de una observación** (Conventional Comments extendido, `05` §3.3):

```
OBS-<seg>-<n> · <etiqueta> (<severidad>[, <categoría>]): <asunto>
  Ubicación: <archivo:línea|símbolo>
  Requisito: <id de la ESPEC | ninguno>
  Detalle: <qué falta o falla y por qué importa>
  Criterio de cumplido: <condición binaria y comprobable>
  Origen: <pregunta del usuario | agente | lista #k>
  Estado: abierta | cumplida | descartada (motivo, aprobó)
```

- Etiquetas: `issue`, `todo`, `suggestion`, `question`, `nitpick`, `chore`. Las etiquetas `note`,
  `thought` y `praise` no cuentan.
- Severidad y peso: `blocking` 3, `mayor` 2, `menor` 1, `nit` 0,5.
- Las observaciones se guardan en la ESPEC (§10), no en el chat.

## 4 · Verificación

1. Se aplican las observaciones que el usuario eligió **sobre los mismos archivos**: se refinan, no se
   borran ni se duplican.
2. Se recorre la lista de 22 puntos de `05` §4.1. Cada «No» abre una observación.
3. Se compila y se corren las pruebas. Las prácticas se comprueban con ArchUnit o con una prueba
   (`indice-precision.md` §1, Prácticas).
4. **Se lanza la revisión de coherencia, cohesión y atributos de calidad** (`revision-coherencia.md`)
   con un agente aparte. **Obligatoria: no se salta.** Cada hallazgo abre una observación en §10.
5. Se calcula la **completitud ≥95 %** (`indice-precision.md` §2).
6. Se hace la **explicación ≥80 %**: el usuario explica clase por clase qué hace cada una y qué patrón
   usa (`indice-precision.md` §3).
7. Si todo pasa y no queda ningún hallazgo bloqueante, se ofrece la aceptación. Si no, se vuelve al modo
   revisión con lo que falta.

Informe de verificación, que queda en la ESPEC:

```
| OBS | peso | estado | evidencia (archivo:línea o prueba) |
Coherencia: pasa | no pasa (COH bloqueantes: n)
Completitud = … %   Explicación = … %   Build: ok   Pruebas: n/n
```
