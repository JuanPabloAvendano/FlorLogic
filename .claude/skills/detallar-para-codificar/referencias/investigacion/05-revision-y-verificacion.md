# 05 · Revisión humana, comprensión y verificación de segmentos

> Investigación de apoyo para la skill **DetallarParaCodificar**. Cubre el ciclo
> *segmento de código → modo revisión → verificación (umbral 95 %)*.
> Las afirmaciones con fuente van sin marca. Lo que es interpretación mía va marcado **[opinión]**.
> Fecha de la investigación: octubre de 2026.

---

## 1. Lo que dice la evidencia sobre el tamaño y el ritmo de revisión

### 1.1 Datos

| Fuente | Hallazgo | Cifra |
|---|---|---|
| SmartBear + Cisco (MeetingPlace, 2006, 10 meses, 2 500 revisiones, muestra de 300 para defectos) | La eficacia cae con el tamaño | < 200 LOC es lo ideal; **400 LOC como máximo absoluto** |
| Mismo estudio | Ritmo de inspección | < 300 LOC/h detecta mejor; < 500 LOC/h es aceptable; los que pasan de 450 LOC/h quedan bajo el promedio en el 87 % de los casos |
| Mismo estudio | Duración | La eficacia cae después de ~60 min; **nunca más de 90 min** por sesión |
| Mismo estudio | Preparación del autor | Las revisiones que el autor **anotó antes** nunca pasaron de 30 defectos/kLOC (pudo ser autorrevisión o un revisor menos crítico; el estudio no lo separa) |
| SmartBear (guía) | Listas de verificación | Son "la forma más eficaz" de atrapar omisiones (sin cifra) |
| Google Eng. Practices | CL pequeño | ~100 líneas es razonable, ~1 000 es demasiado; también cuenta el número de archivos (200 líneas en 1 archivo ≠ 200 líneas en 50) |
| Google Eng. Practices | Contenido del CL | Un solo propósito, **con sus pruebas**, sin API nuevas que nadie use en el mismo CL, y la refactorización aparte |
| Baum, Schneider y Bacchelli (EMSE 2019, 50 participantes) | Memoria de trabajo | Los cambios pequeños se revisan con mucha más eficacia; la capacidad de memoria de trabajo se asocia con encontrar **defectos deslocalizados** (los que cruzan archivos) |
| Bacchelli y Bird (ICSE 2013, Microsoft) | Para qué sirve revisar | Se esperaba encontrar defectos, pero sale más **conocimiento compartido y comprensión**. "Entender el código y el cambio" es el reto central |

Advertencias: el estudio de Cisco es de una sola empresa, de 2006, y lo hizo un proveedor de herramientas. Hay que tomar sus cifras como orden de magnitud, no como ley.

### 1.2 Cómo encaja con segmentos de ~600 líneas

- **600 líneas está por encima del máximo de 400** de la evidencia. A 300–500 LOC/h, 600 líneas son **72–120 min**, lo que bordea o pasa el techo de 90 min. **[opinión]** No hace falta bajar el segmento. Sí hace falta **dividir la revisión del segmento en 2 o 3 pasadas** de ≤ 300 líneas cada una, con una pausa o un corte natural entre ellas.
- **[opinión]** Hay que contar **líneas de lógica**, no líneas brutas. Las importaciones, los DTO o *records* triviales, la configuración declarativa y los *getters* cuestan mucho menos. Regla práctica: las pruebas cuentan como 0,5 y el código generado o declarativo como 0,25. Un segmento de 600 líneas brutas suele tener 350–450 de lógica, que es el rango que respalda la evidencia.
- **[opinión]** Que el humano haga preguntas (modo revisión) se parece más a la "lectura con anotación del autor" que a una inspección en frío. El agente hace de autor que anota. Eso juega a favor del segmento según el estudio de Cisco, pero **también puede volver complaciente al revisor**. Por eso la verificación posterior es independiente de las respuestas.
- Ritmo recomendado que se le dice al humano: **≤ 400 LOC de lógica por hora y ≤ 60 min seguidos**. Si el segmento tiene más de 400 líneas de lógica, se parte en pasadas.
- La regla "un archivo de más de 900 líneas va solo" es coherente: ese archivo **siempre** requiere 3 o más pasadas. **[opinión]** En ese caso conviene recorrerlo por secciones (clase o método público) y no de arriba abajo.
- El número de archivos importa tanto como las líneas (Google). **[opinión]** Si un segmento tiene más de 12–15 archivos, hay que presentar primero un mapa de dependencias para no saturar la memoria de trabajo (Hermans; Baum et al.).

---

## 2. Cómo ordenar y presentar un segmento para que un humano lo entienda

### 2.1 Principios de la evidencia

- Las herramientas ordenan los archivos alfabéticamente, y los revisores tienden a seguir ese orden aunque no sea el mejor. Los archivos que salen primero reciben más comentarios (Baum et al. 2017; *First Come First Served*, 2022). En una encuesta de ICSE 2026 a 1 355 desarrolladores, solo el 10,2 % considera óptimo el orden alfabético.
- Baum et al. (ICSME 2017): el orden óptimo es ante todo una **agrupación por relación**. Las partes relacionadas van juntas.
- Google, cómo navegar un CL: (1) visión general y descripción, (2) **la parte principal primero**, revisando el diseño, (3) el resto en secuencia. Leer las pruebas primero ayuda a entender la intención.
- Hermans (*The Programmer's Brain*): la memoria de trabajo maneja muy pocos *chunks*. Ayudan los nombres consistentes, las tablas de estado, los grafos de dependencias y resumir con palabras propias. La carga extrínseca (ruido de presentación) es la que se puede quitar.

### 2.2 Orden de lectura recomendado: de afuera hacia adentro, con contrato primero

**[opinión]**, sintetizado de lo anterior:

1. **Ficha del segmento** (≤ 15 líneas, ver plantilla abajo): qué parte de la especificación cubre, qué queda fuera y qué decisiones tomó el agente sin que la especificación las dijera.
2. **Mapa**: la lista de archivos agrupados por rol, más un grafo mínimo de dependencias (texto o Mermaid) con el punto de entrada marcado.
3. **Contrato externo**: la interfaz pública, el puerto, el endpoint o el evento. Es lo que el resto del sistema ve.
4. **Pruebas de aceptación** del segmento: muestran la intención en ejemplos concretos (Google: "leer pruebas primero").
5. **Núcleo de la lógica**: el caso de uso o servicio principal, que es "la parte principal" de Google. Aquí se gasta la mayor parte de la atención.
6. **Adaptadores y detalles**: persistencia, mapeos, configuración.
7. **Pruebas unitarias restantes y código de soporte.**

De adentro hacia afuera (dominio primero) solo conviene cuando el segmento es puro dominio, sin contrato externo nuevo.

### 2.3 Qué explicar primero

- El **porqué** antes del **qué**. Google pide que los comentarios expliquen el porqué; el código ya dice el qué.
- Las **decisiones implícitas**: todo lo que la especificación no fijaba y el agente decidió (valores por defecto, orden de validación, manejo de nulos, transaccionalidad). Es donde vive la "deuda de comprensión" (Osmani).
- Los **puntos de sensibilidad** (en el sentido de ATAM): las líneas donde un cambio afecta un atributo de calidad, como un *lock*, un *retry*, un *timeout* o un límite de transacción.
- **[opinión]** Como máximo **3 a 5 ideas clave** por pasada, que es lo que cabe en la memoria de trabajo.

### 2.4 Enlace con la especificación (trazabilidad)

- Cada archivo y cada método público lleva un **ID de requisito** de la especificación (por ejemplo `TX-1.R3`, `CA-07` para un criterio de aceptación, `EC-2` para un escenario de calidad).
- La trazabilidad tiene que ser **bidireccional** (ISO/IEC/IEEE 24765 vía NASA SWEHB; CMMI REQM SP1.4):
  - *Hacia adelante*: cada requisito del segmento lleva a su código **y a una prueba**. Un requisito sin prueba es una observación.
  - *Hacia atrás*: cada bloque de código lleva a un requisito. Si no lleva a ninguno, es código sin padre: alcance inflado (*scope creep*) o una decisión implícita que hay que declarar.
- Se presenta en una **mini matriz** dentro de la ficha:

```
| Requisito | Archivo:símbolo                 | Prueba                          | Estado |
|-----------|---------------------------------|---------------------------------|--------|
| TX-1.R3   | ReservaService.java:reservar    | ReservaServiceTest#reservaSinCupo | ok   |
| TX-1.R4   | —                               | —                               | fuera de segmento (seg. 3) |
```

### 2.5 Plantilla de ficha de segmento **[opinión]**

```
## Segmento N/M — <nombre>
Cubre: <IDs de requisitos>       No cubre: <IDs, y en qué segmento van>
Tamaño: <brutas> líneas / ~<lógica> de lógica → <k> pasadas sugeridas
Entrada: <archivo:símbolo>       Orden de lectura: 1) … 2) … 3) …
Decisiones implícitas: D1 …, D2 … (cada una con porqué)
Puntos sensibles: <archivo:línea — atributo de calidad>
Matriz requisito→código→prueba: (tabla)
```

---

## 3. Modo revisión: reglas para el agente

### 3.1 Responder sin salirse del segmento

1. **Ancla cada respuesta**: cita `archivo:línea` o `archivo:símbolo` del segmento. Si la respuesta depende de código fuera del segmento, dilo explícitamente ("esto lo resuelve el segmento 4, todavía no escrito" o "esto viene de `X` ya existente").
2. **Responde primero en una o dos frases**, luego da el detalle. Si el humano pide profundizar, sigue en el mismo hilo; no abras temas nuevos por tu cuenta.
3. **Concepto antes que código** cuando la pregunta es de "por qué". Usa la regla de Google: si tienes que explicar mucho, probablemente el **código** debería ser más claro. Eso es una observación, no solo una respuesta.
4. **No defiendas, verifica.** Antes de afirmar cómo se comporta el código, reléelo. Si no estás seguro, dilo y propón cómo comprobarlo (una prueba o una ejecución). **[opinión]** Afirmar con seguridad algo falso es el peor fallo posible en este modo, porque crea falsa comprensión.
5. **Fuera de alcance**: si la pregunta toca diseño general o segmentos futuros, responde en ≤ 3 líneas y anota una observación `note` o `thought` sin bloquear.
6. **No reescribas código en modo revisión.** Se anota; se corrige en la verificación. **[opinión]** Así el humano revisa un objetivo estable.
7. **Ofrece ayudas de comprensión** cuando la pregunta muestre carga alta: una tabla de estado, una traza de ejecución con un ejemplo concreto o un grafo de llamadas (Hermans).
8. **Al cerrar cada pasada**, pide al humano que resuma con sus palabras qué hace el núcleo (recuerdo activo, Hermans). Si el resumen falla, eso señala un hueco de explicación o de claridad.

### 3.2 Detectar faltantes mientras se responde

Mientras armas cada respuesta, hazte estas preguntas internas **[opinión]**:

- ¿La respuesta exige mencionar un caso que el código **no** maneja (nulo, vacío, concurrente, fallo de red, duplicado)? Entonces es `issue`.
- ¿Estoy justificando una decisión que **no está en la especificación**? Entonces es `question` o `todo` para declararla o validarla.
- ¿Hay una prueba que respalde lo que acabo de afirmar? Si no la hay, es `todo (test)`.
- ¿Mi explicación necesitó más de ~5 líneas para algo sencillo? Entonces es `suggestion` de claridad (nombres, extraer método o comentario de porqué).
- ¿La pregunta del humano revela que un nombre o un comentario engaña? Entonces es `issue` de naming.
- ¿Lo que afirmo depende de una API o librería que no verifiqué que exista con esa firma? Entonces verifícalo ya. Si falla, es `issue (blocking)`.

### 3.3 Formato de una observación (basado en Conventional Comments)

Formato de Conventional Comments: `<label> [decorations]: <subject>` + discusión. Lo extiendo con los campos que la verificación necesita:

```
OBS-<seg>-<n> · <label> (<severidad>[, <categoría>]): <asunto en una línea>
  Ubicación: <archivo>:<línea|rango|símbolo>
  Requisito: <ID de la especificación o "ninguno">
  Detalle: <qué falta o falla y por qué importa>
  Criterio de cumplido: <condición verificable, observable y binaria>
  Origen: <pregunta del humano | agente | checklist #k>
  Estado: abierta | cumplida | descartada (motivo: …, aprobó: humano)
```

- **Etiquetas** (Conventional Comments): `issue`, `todo`, `suggestion`, `question`, `nitpick`, `chore`, `note`, `thought`, `praise`. Las tres últimas nunca bloquean ni cuentan para la completitud; `praise` se usa con sinceridad.
- **Severidad** (decoración; se mapea a Google: Nit / Optional / FYI):
  - `blocking`: no se acepta el segmento sin resolverla. Peso 3.
  - `mayor`: debe resolverse; descartarla requiere justificación fuerte. Peso 2.
  - `menor` (`if-minor`, `non-blocking`): se resuelve si es barato. Peso 1.
  - `nit`: preferencia. Peso 0,5.
- **Categoría** (decoración libre, como permite el estándar): `funcional`, `diseño`, `prueba`, `seguridad`, `rendimiento`, `concurrencia`, `naming`, `doc`, `trazabilidad`, `ia` (riesgo propio del código generado).
- **Criterio de cumplido**: obligatorio y comprobable. Bien: "existe la prueba `X#y` que falla si se quita la validación de la línea 42". Mal: "mejorar la validación".

Ejemplo:

```
OBS-2-04 · issue (blocking, concurrencia): dos reservas simultáneas pueden exceder el cupo
  Ubicación: ReservaService.java:58-71 (reservar)
  Requisito: TX-1.R3
  Detalle: lectura de cupo y escritura no son atómicas; no hay lock ni versión.
  Criterio de cumplido: bloqueo optimista (@Version) o UPDATE condicional + prueba
    concurrente ReservaConcurrenciaIT que con 2 hilos deja exactamente 1 reserva.
  Origen: pregunta del humano ("¿qué pasa si dos usuarios reservan a la vez?")
  Estado: abierta
```

---

## 4. Lista de verificación de un segmento y fórmula de completitud

### 4.1 Lista (22 ítems)

Cada ítem se marca **Sí / No / N. A.** (con motivo). Un "No" genera una observación con la severidad indicada. La lista combina Google (qué buscar), OWASP (revisión segura), SmartBear (listas), trazabilidad y riesgos de IA.

**Trazabilidad y alcance**
1. Cada requisito declarado como cubierto lleva a código **y** a al menos una prueba. *(blocking)*
2. Todo bloque de código lleva a un requisito o a una decisión implícita declarada; no hay código sin padre. *(mayor)*
3. Lo que queda fuera del segmento está nombrado, con el segmento destino. *(menor)*
4. No hay API, parámetros ni ramas "por si acaso" sin uso en el segmento (Google: no agregar API sin uso). *(mayor)*

**Funcionalidad**
5. El camino feliz de cada criterio de aceptación está probado de punta a punta en el segmento. *(blocking)*
6. Los casos borde están manejados o rechazados explícitamente: nulo, vacío, límites, duplicados. *(mayor)*
7. Los errores y fallos externos (red, base de datos, tiempo) tienen comportamiento definido y no se tragan excepciones. *(mayor)*
8. La concurrencia y la transaccionalidad son correctas donde aplica: atomicidad, idempotencia, *locks*. *(blocking si aplica)*

**Pruebas**
9. Las pruebas **fallarían si el código se rompe**: aserciones sobre comportamiento, no sobre la implementación (Google). *(blocking)*
10. Las pruebas no se escribieron "a la medida" de la implementación ni se debilitaron (sin `@Disabled`, aserciones vacías ni *mocks* de lo que se prueba). *(blocking)*
11. Todas las pruebas del segmento pasan y el *build* compila. *(blocking)*

**Diseño y complejidad**
12. El código respeta la arquitectura y las fronteras de módulo previstas en la especificación (por ejemplo, módulos de Modulith y dependencias permitidas). *(mayor)*
13. Ninguna función o clase es más compleja de lo necesario y no hay sobreingeniería (Google: Complexity). *(menor)*
14. Hay reutilización en lugar de duplicación de lógica existente (riesgo IA, GitClear). *(mayor)*

**Seguridad (OWASP, revisión por diff)**
15. Las entradas se validan del lado del servidor y la salida se codifica según el contexto. *(blocking si hay frontera de confianza)*
16. La autorización se verifica en el servidor, niega por defecto y no permite IDOR. *(blocking si aplica)*
17. No hay secretos en el código; los registros no filtran datos sensibles y no son inyectables. *(blocking)*
18. Las dependencias nuevas existen, están en el manifiesto, tienen versión fijada y son las esperadas (anti-slopsquatting). *(blocking)*

**Legibilidad y documentación**
19. Los nombres son descriptivos y consistentes con el glosario de la especificación (Hermans: chunking). *(menor)*
20. Los comentarios explican el **porqué** de las decisiones implícitas y no hay TODO huérfanos. *(menor)*
21. Las explicaciones que dio el agente en modo revisión y que no son obvias quedaron en el código o en la documentación, no solo en el chat (Google: aceptar explicaciones). *(mayor)*

**Comprensión**
22. El humano resumió con sus palabras el flujo principal del segmento y el agente confirmó que el resumen es correcto. *(blocking)* **[opinión]**: es el ítem que materializa el objetivo de la skill.

### 4.2 Fórmula de completitud **[opinión]**

Universo **U** = observaciones abiertas en modo revisión con etiqueta `issue`, `todo`, `suggestion`, `question`, `chore` o `nitpick`, más los ítems de la lista marcados "No". Los ítems N. A. y las etiquetas `note`, `thought` y `praise` quedan fuera.

Cada elemento *i* tiene peso *wᵢ* (blocking 3, mayor 2, menor 1, nit 0,5) y resultado *rᵢ*:

- cumplida (criterio de cumplido verificado): *r* = 1
- descartada a propósito, con motivo escrito: *r* = 1 si es menor o nit; *r* = 1 si es mayor **y** el humano aprobó el descarte; una blocking **no se puede descartar** sin aprobación explícita del humano, y si no la hay, *r* = 0
- abierta o parcial: *r* = 0 (no hay crédito parcial; el criterio es binario)

```
Completitud = 100 × Σ(wᵢ · rᵢ) / Σ(wᵢ)
Aprobado ⇔ Completitud ≥ 95  ∧  ninguna blocking con r = 0  ∧  ítems 11 y 22 = Sí
```

- Por qué hay compuertas además del porcentaje: con muchas observaciones menores, una blocking abierta podría quedar por debajo del 5 % del peso. El umbral solo no basta.
- Si U está vacío, la completitud es 100 %, pero la lista completa debe haberse marcado.
- **Anti-trampa**: descartar no puede ser la vía fácil. Si los descartes superan el 30 % del peso, se exige que el humano los revise en bloque.
- Informe de verificación por segmento: tabla `OBS | peso | estado | evidencia (archivo:línea o prueba)` y el cálculo mostrado.

---

## 5. Elementos prioritarios de una revisión (en orden)

1. **¿Debería existir este cambio y cubre lo pedido?** Es la trazabilidad contra la especificación. Si el cambio va en la dirección equivocada, el resto no importa (Google, paso 1 de navegación).
2. **Diseño y encaje**: fronteras, dependencias y contratos. Los comentarios de diseño se mandan primero porque pueden volver inútil el resto (Google). En código de IA, el desajuste arquitectónico es frecuente por el contexto limitado.
3. **Corrección funcional**: casos borde, errores y concurrencia. CodeRabbit (2025) encontró ~1,75× más errores de lógica y corrección en los PR con IA.
4. **Seguridad**: la IA eligió la opción insegura en ~45 % de las tareas de Veracode (2025), y en Java pasó del 70 %. Es de severidad alta y difícil de ver a simple vista.
5. **Pruebas significativas**: son lo que mantiene la corrección en el tiempo. Hay que vigilar las pruebas que reflejan la implementación.
6. **Comprensión**: que el humano pueda explicar el flujo. Es la razón de ser de la skill y lo que Bacchelli y Bird identifican como el reto central.
7. **Complejidad, duplicación y reutilización**: deuda que se acumula (GitClear: 8× más bloques duplicados en 2024).
8. **Nombres y comentarios**: reducen la carga cognitiva futura (Hermans).
9. **Estilo y consistencia**: lo último, y de preferencia automatizado. Google pide marcarlo como `Nit` y no mezclarlo con cambios funcionales.

**[opinión]** El orden sigue costo de error × probabilidad de que la IA lo cometa ÷ facilidad de detección automática. El estilo lo detecta un *linter*; la seguridad y la lógica no.

---

## 6. Riesgos propios del código generado por IA y cómo los atrapa la revisión

| Riesgo | Evidencia | Cómo lo atrapa la revisión (ítem de §4.1) |
|---|---|---|
| **Paquetes o API alucinados** (*slopsquatting*) | Spracklen et al. (2025): 19,7 % de 576 000 muestras referían paquetes inexistentes; el 43 % se repite entre ejecuciones, así que un atacante puede registrarlos | Ítem 18: verificar cada dependencia contra el registro real; el agente comprueba firmas antes de afirmar (§3.2) |
| **Inseguridad plausible** | Veracode (2025): 45 % de las tareas con una vulnerabilidad del OWASP Top 10; fallos en XSS (86 %) y en inyección en logs (88 %) | Ítems 15–17 y la revisión por diff de OWASP: rastrear fuente → sumidero |
| **Lógica incorrecta con buena apariencia** | CodeRabbit (2025, de proveedor): 1,7× más hallazgos en total, 1,4× más críticos; la salida es fluida y segura aunque esté mal | Ítems 5–8; pedir una traza concreta con datos reales en modo revisión |
| **Pruebas que confirman la implementación** | Prácticas de la comunidad (2025–2026): no dejar que la misma herramienta escriba el código y las pruebas que lo aprueban | Ítems 9–10; **[opinión]** derivar las pruebas de los criterios de aceptación de la especificación, no del código |
| **Desvío de la especificación** (interpretación razonable pero no la pedida) | Checklists de la comunidad (2026) | Ítems 1–2: matriz bidireccional; decisiones implícitas declaradas en la ficha |
| **Alcance inflado o diff explosivo** | Checklists de la comunidad; GitClear | Ítems 2, 4 y 13 |
| **Duplicación en vez de reutilización** | GitClear (211 M de líneas, 2020–2024): 8× más bloques duplicados; las líneas movidas (refactorización) cayeron 39,9 % | Ítem 14: el agente busca funciones existentes antes de escribir y lo declara en la ficha |
| **Deuda de comprensión** | Osmani (2025): código limpio y pruebas en verde, pero nadie entiende el sistema. Anthropic (ene. 2026, ECA con 52 ingenieros): el grupo con IA sacó 50 % frente a 67 % en comprensión, con la mayor brecha en depuración; quienes usaron la IA para preguntas conceptuales retuvieron más | Todo el modo revisión. Ítem 22 (resumen del humano). El modo revisión **es** la "pregunta conceptual" que preserva la comprensión |
| **Falsa sensación de productividad** | METR (2025): desarrolladores expertos fueron 19 % más lentos con IA y creían haber sido 20 % más rápidos | **[opinión]** Medir la completitud real y no la velocidad; no saltarse pasadas por "ya se ve bien" |
| **Calidad debilitada a escondidas** | Checklists de la comunidad: pruebas omitidas, reglas de *lint* relajadas | Ítems 10–11; buscar `@Disabled`, `skip`, `catch` vacíos y `// TODO` nuevos |
| **Commit sin entender** | Regla de Simon Willison: "no subo código que no podría explicar exactamente a otra persona" | El ciclo completo de la skill; el ítem 22 hace operable esa regla |

---

## 7. Fuentes

- SmartBear, *Best Practices for Code Review*: https://smartbear.com/learn/code-review/best-practices-for-peer-code-review/ — 200–400 LOC, < 500 LOC/h, 60–90 min, anotación del autor, listas de verificación.
- Mike Conley, análisis del estudio Cisco/SmartBear: https://mikeconley.ca/blog/tag/loc/ — datos del estudio (2 500 revisiones, muestra de 300, umbrales de 300/450 LOC/h) y crítica metodológica.
- Google Eng. Practices, *Small CLs*: https://google.github.io/eng-practices/review/developer/small-cls.html — ~100 vs ~1 000 líneas, pruebas en el mismo CL, refactorización aparte, formas de dividir.
- Google Eng. Practices, *What to look for*: https://google.github.io/eng-practices/review/reviewer/looking-for.html — orden diseño → funcionalidad → complejidad → pruebas → nombres → comentarios → estilo; "cada línea" y contexto.
- Google Eng. Practices, *Navigating a CL*: https://google.github.io/eng-practices/review/reviewer/navigate.html — visión general, parte principal primero, el resto en secuencia, pruebas primero.
- Google Eng. Practices, *How to write comments*: https://google.github.io/eng-practices/review/reviewer/comments.html — explicar el porqué, Nit/Optional/FYI, la explicación debe quedar en el código.
- Bacchelli y Bird, *Expectations, Outcomes, and Challenges of Modern Code Review* (ICSE 2013): https://www.microsoft.com/en-us/research/publication/expectations-outcomes-and-challenges-of-modern-code-review/ — la comprensión es el reto central; se encuentran menos defectos de lo esperado.
- Baum, Schneider y Bacchelli, *Associating working memory capacity and code change ordering with code review performance* (EMSE 2019), DOI 10.1007/s10664-018-9676-8: https://www.zora.uzh.ch/entities/publication/245eb643-3ebd-476c-a613-8097ff6d9582 — cambios pequeños, memoria de trabajo y defectos deslocalizados.
- Baum et al., *On the Optimal Order of Reading Source Code Changes for Review* (ICSME 2017): https://www.merlin.uzh.ch/publication/show/15587 — orden óptimo = agrupar por relación.
- *First Come First Served: The Impact of File Position on Code Review*: https://arxiv.org/pdf/2208.04259 — los archivos que salen primero reciben más atención.
- *Breaking the Alphabet* (ICSE 2026): https://conf.researchr.org/details/icse-2026/icse-2026-research-track/310/Breaking-the-Alphabet-Rethinking-File-Ordering-in-Code-Review — solo el 10,2 % de 1 355 desarrolladores considera óptimo el orden alfabético.
- Felienne Hermans, *The Programmer's Brain* (Manning, 2021), notas de lectura: https://www.r-bloggers.com/2026/08/reading-notes-on-the-programmers-brain-by-felienne-hermans/amp/ — LTM/STM/WM, chunking, tablas de estado, roles de variables, recuerdo activo, tipos de carga cognitiva.
- Conventional Comments: https://conventionalcomments.org/ — formato `label (decorations): subject`, etiquetas y decoraciones blocking, non-blocking, if-minor.
- OWASP, *Secure Code Review Cheat Sheet*: https://cheatsheetseries.owasp.org/cheatsheets/Secure_Code_Review_Cheat_Sheet.html — áreas de revisión y los 6 pasos de la revisión por diff.
- OWASP Code Review Guide v2: https://wiki.owasp.org/images/5/53/OWASP_Code_Review_Guide_v2.pdf — guía completa con lista en el apéndice.
- SEI ATAM (resumen): https://en.wikipedia.org/wiki/Architecture_tradeoff_analysis_method — puntos de sensibilidad, compromisos y riesgos para la revisión de arquitectura.
- NASA SWEHB, trazabilidad bidireccional: https://swehb.nasa.gov/x/wAP7 — definición ISO 24765; detecta diseño sin código y código sin padre.
- CMMI REQM SP1.4: https://wibas.com/cmmi/reqmsp-14-maintain-bidirectional-traceability-of-requirements — cada requisito cubierto y cada elemento trazable a una fuente válida.
- Scrum Guide 2020: https://scrumguides.org/scrum-guide.html — la definición de terminado es la "descripción formal del estado del Incremento".
- Spracklen et al., alucinación de paquetes (resumen): https://www.endorlabs.com/learn/hallucinated-packages-how-ai-invents-dependencies-attackers-exploit — 19,7 % de paquetes inexistentes y 43 % repetidos.
- Veracode, *2025 GenAI Code Security Report*: https://www.veracode.com/press-release/ai-generated-code-poses-major-security-risks-in-nearly-half-of-all-development-tasks-veracode-research-reveals/ — 45 % de tareas inseguras; Java > 70 %.
- CodeRabbit, *State of AI vs Human Code Generation* (dic. 2025): https://coderabbit.ai/blog/state-of-ai-vs-human-code-generation-report — 1,7× más hallazgos y 1,75× más errores de lógica (fuente de proveedor).
- GitClear, calidad del código con IA (2025), resumen: https://devclass.com/2025/02/20/ai-is-eroding-code-quality-states-new-in-depth-report/ — 8× más duplicación; la refactorización cae 39,9 %.
- METR (2025), resumen: https://getdx.com/blog/metr-study-on-how-ai-affects-developer-productivity/ — 19 % más lentos con IA y percepción invertida.
- Anthropic, *AI assistance and coding skills* (ene. 2026): https://www.anthropic.com/research/AI-assistance-coding-skills — ECA: 50 % frente a 67 % en comprensión; el uso conceptual preserva el aprendizaje.
- Addy Osmani, *Comprehension Debt*: https://addyosmani.com/blog/comprehension-debt/ — la brecha entre el código que existe y el que se entiende; las pruebas en verde no bastan.
- Simon Willison, *Not all AI-assisted programming is vibe coding*: https://simonwillison.net/2025/Mar/19/vibe-coding/ — regla de oro: no subir código que no puedas explicar.
- Checklist comunitario para código de IA: https://dev.to/pyor/reviewing-ai-generated-code-a-practical-checklist-4017 — pruebas que reflejan la implementación y alucinaciones de API.
