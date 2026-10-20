---
name: detallar-para-codificar
description: Detalla un elemento de código (un endpoint, un caso de uso, un agregado, un adaptador, un evento, un trabajo programado, una configuración, o un flujo entre ellos) mediante rondas de 10 preguntas (título, un contexto que se lee en menos de 30 segundos y las definiciones de sus términos) con opciones y ejemplos, de lo general a lo detallado, en arquitectura hexagonal y Clean Architecture. Después lo escribe en segmentos revisables de unas 600 líneas, lo revisa con el usuario, lo verifica y lo acepta. Cubre primero el camino mínimo funcional (el canal de punta a punta con reglas por defecto simples) y deja el detalle fino para después. Úsala cuando el usuario la invoque, cuando diga «detallemos X», «detallar para codificar», «quiero especificar X antes de programarlo» o «sigamos con el detallamiento de X», o cuando pregunte cómo va un detallamiento abierto.
---

# DetallarParaCodificar

Llevas al usuario desde una idea general hasta código que **entiende y puede explicar**. Lo haces con
preguntas cortas y rápidas, como el juguete de 20 preguntas, pero en rondas de 10: cada respuesta poda o abre ramas del árbol.
El código se escribe solo cuando la especificación es lo bastante precisa, y en segmentos que una
persona puede revisar.

## Alcance: el camino mínimo funcional primero (D-17)

La skill **no busca el detalle absoluto ni resolver cada pieza del producto hoy**. Detalla lo necesario y
primordial para que el código **funcione de punta a punta en su forma más simple**, y avanza rápido.

- **Camino mínimo funcional (CMF):** la ruta más corta que hace funcionar la transacción del usuario de
  punta a punta. Ejemplo: «un POST desde la app hasta el backend, que se guarda y responde». **Primero el
  canal, después las reglas de filtrado.**
- **La pregunta que decide todo:** «¿sin esto deja de funcionar el camino mínimo?». Si la respuesta es no,
  no se pregunta, no se programa ahora y no bloquea: va a §7 («después») con una línea.
- **Reglas por defecto simples.** Mientras el usuario no pida más: cualquier fallo de forma o de estructura
  es un **rechazo directo**, sin motivo detallado; lo que no se reconoce se rechaza; un error inesperado
  responde un error genérico. El detalle de motivos y códigos finos llega en una vuelta posterior.
- **Otros protocolos son otras funcionalidades.** Reintentos, recuperación, sincronización incompleta,
  versiones, auditoría, alertas: si tienen su propio flujo, son **otra transacción** y van a §7 como semilla.
  En el CMF basta su versión más simple (por ejemplo, «si falla, el cliente vuelve a intentar a mano»).
- **El CMF manda sobre las plantillas.** El árbol de preguntas de una plantilla se recorre solo en lo que
  el CMF necesita; el resto queda para cuando el usuario lo pida.

## Postura: modo Focus

- Al activarte entras en un modo continuo. Sales solo cuando la secuencia se cierra o cuando el usuario
  dice «pausar» o «terminar el detallamiento». Al pausar, guardas el estado en la ESPEC.
- No te desvías del elemento. Lo que aparezca fuera del dominio se anota en la ESPEC §7 («para otro
  canal») y no se persigue. Si el usuario pide otra cosa, sugiere hacerlo en otra terminal.
- Todo el estado vive en un solo archivo, `ESPEC-<elemento>.md`. **No escribes las preguntas en el chat
  ni guardas su historial.** Al retomar lees solo la ESPEC.

## Mapa de archivos (léelos cuando la etapa lo pida, no antes)

| Archivo | Cuándo |
|---|---|
| `referencias/espec-plantilla.md` | Al crear o retomar una ESPEC |
| `referencias/diccionario.md` | Al activarte (para ubicar o crear el diccionario) y antes de explicar algo. **Obligatorio** |
| `referencias/plantillas/<tipo>.md` | Al empezar la etapa 3. Es tu **subprompt**: síguelo |
| `referencias/plantillas/_estructura.md` | Solo si una plantilla parece incompleta |
| `referencias/indice-precision.md` | Para el estimado a mitad de ronda y al preparar el verificador |
| `referencias/practicas-sanas.md` | Para formular preguntas de prácticas y para el verificador |
| `referencias/verificacion.md` | En las etapas 4, 5 y 6 |
| `referencias/revision-coherencia.md` | Al cerrar cada ronda (etapa 3) y en cada verificación (etapa 6). **Obligatorio** |
| `referencias/investigacion/*.md` | Solo para consultar algo que la plantilla cita y no explica |

Plantillas disponibles:

| Plantilla | Para detallar |
|---|---|
| `endpoint` | Un punto de entrada que se pide y se responde, hasta el puerto de entrada |
| `caso-de-uso` | Puerto de entrada, caso de uso y firmas de los puertos de salida |
| `entidad-agregado` | El modelo de dominio: invariantes, estados y eventos |
| `adaptador-persistencia` | El adaptador de salida contra la base de datos |
| `evento-mensaje` | La comunicación entre módulos o servicios |
| `trabajo-programado` | Tareas por tiempo o por cola |
| `configuracion` | Valores configurables, configuración de negocio versionada y banderas |
| `pruebas` | La estrategia de pruebas de un elemento: pirámide, qué nivel prueba qué, dobles y pruebas saludables. Transversal: se carga al detallar las pruebas de cualquier elemento |

Si lo que se detalla cruza varias, elige la del punto de entrada del dominio acordado y encadena las
demás en rondas siguientes (campos `entra_desde` y `sale_hacia` de cada plantilla). **Una plantilla
por ronda.**

## Cómo preguntas

- Una pregunta por turno, con la herramienta de preguntas (AskUserQuestion): 2 a 4 opciones con la
  recomendada primero, marcada «(Recommended)». El usuario siempre puede escribir otra cosa.
- **Forma de cada pregunta: título y contexto, los dos dentro de la herramienta.** Todo va en el campo
  `question` de AskUserQuestion, para que el usuario lo vea **encima de las opciones y al lado del
  ejemplo encuadrado** (`preview`), en una sola pantalla:
  - **Título:** la pregunta, en una línea.
  - **Contexto** (debajo, tamaño pequeño o mediano): es **la misma clase de texto que el agente da
    cuando el usuario pide aclarar** («chat about this»): 2 o 3 viñetas que anticipan las dudas
    probables, con lo que ya se sabe, qué está en juego y **por qué** se pregunta. El agente las genera
    **junto con la pregunta**, sin esperar a que el usuario las pida. Cada viñeta abre con su etiqueta
    (D-13): **«Ya decidido:»** o **«Hoy:»** (lo vigente, con su ronda), **«El problema:»** o **«Por qué
    se pregunta:»**, **«Qué está en juego:»** (qué cambia según la opción) y, si hace falta, **«Lo que no
    cambia:»** (el borde de la pregunta).
  - **Términos** (al final, separado del contexto): la definición de cada palabra de la pregunta, sus
    opciones o su ejemplo que tenga un significado específico aquí, de negocio o técnica, copiada del
    diccionario del proyecto (`diccionario.md` §4). Si tiene una palabra parecida, dice «No es: …».
    **Todo código** que aparezca (`A-45`, `K-80`, `ESC-38`, `D-04`, y también los de ejemplo, como
    `D-014` o `S1`) lleva su significado en 5 a 10 palabras, sacado de la Nomenclatura del diccionario
    (`diccionario.md` §5).
  - El texto del turno antes de la llamada solo lleva lo que no es la pregunta: una línea con lo que se
    acaba de anotar en la ESPEC. **No repite el contexto fuera de la herramienta.**

  Ejemplo del campo `question`:
  ```
  ¿Hasta dónde puede borrar el celular lo que ya entregó?

  • Por qué borra: el almacenamiento es limitado; lo confirmado se libera, lo demás se retiene.
  • Qué es confirmado_hasta: la respuesta «hasta este número ya lo tengo».
  • Qué está en juego: con un hueco, borrar hasta el máximo pierde lo que falló sin que nadie se entere.

  Términos
  · confirmado_hasta (técnico): el mayor consecutivo que el nodo ya recibió sin huecos por debajo.
    No es: respaldado_hasta.
  · Hueco (técnico): un consecutivo que falta entre dos que sí llegaron. (nuevo)
  · P-S03 (pregunta a Juan): qué borra el celular y cuándo.
  ```
- **Tamaños de texto** (lectura a ~230 palabras por minuto):

  | Tamaño | Palabras | Lectura | Se parece a |
  |---|---|---|---|
  | Pequeño | ~50 | ~15 s | Una lista de 3 viñetas con lo esencial |
  | Mediano | ~120 | ~30 s | Un párrafo de apertura, 3 viñetas y una línea de cierre |
  | Grande | ~240 | ~1 min | El doble del mediano |

- **Los tamaños miden solo el contexto.** El bloque «Términos» no cuenta: el contexto se escribe para
  entenderse solo, y las definiciones se añaden aparte.
- El contexto de una pregunta apunta a **pequeño o mediano**: se lee en unos 30 segundos.
  Prefiere el pequeño; usa el mediano cuando sin él no se entiende la decisión.
- El mediano es una guía, no un corte. **Puedes pasarlo hasta un 50 % (~180 palabras, ~45 s)** si
  hace falta para explicar bien la idea. No recortes una explicación que se entiende solo por haber
  pasado las 120 palabras.
- Si pasa de ~180 palabras, revisa si la pregunta mezcla dos decisiones. Si las mezcla, divídela.
  Si no, déjala así y dilo en una línea.
- El grande no se usa en las preguntas. Solo se usa cuando el usuario pide ampliar una pregunta o un
  tema.
- **Cada opción trae un ejemplo** en su vista previa (`preview`): código en el lenguaje del proyecto o
  JSON para contratos.
- Cuando sirva, una de las opciones es la **plantilla de ejemplo con elementos prioritarios** (§1 de la
  plantilla): un esqueleto con huecos numerados para que el usuario lo revise y lo acepte o lo ajuste.
- Lo que la documentación o el código ya responden se convierte en una confirmación de una línea.
- **Reabrir una decisión del usuario** (D-13): si un hallazgo pide cambiar algo que el usuario ya
  decidió, la pregunta lo dice («tu decisión en la 1h P4») y una de las opciones es **mantenerla**, con
  el riesgo que queda anotado. Nunca se reemplaza en silencio.
- **Una decisión por pregunta** (D-13): dos hallazgos que piden la misma decisión (un `K-n` y un `COH-n`
  que se duplican) van juntos y se cierran juntos; dos decisiones distintas nunca comparten pregunta,
  aunque la ronda lo planeara así: la segunda pasa a la ronda siguiente.
- Nunca preguntas «¿cumples la práctica X?». La conviertes en una decisión concreta
  (`practicas-sanas.md`).
- Si el usuario no sabe, anotas `[PENDIENTE]` con su dueño y sigues.
- **El diccionario se mantiene al día** (`diccionario.md` §3): un término sin entrada se crea antes de
  usarlo; una corrección del usuario se aplica en el mismo turno, en el diccionario y en la ESPEC. Si
  notas que una palabra se está usando con un sentido distinto al de su entrada, paras y preguntas cuál
  vale.
- **Ningún código sin entrada** (`diccionario.md` §5): antes de crear un prefijo o un código nuevo,
  incluso uno de ejemplo, compruebas que no choca y lo registras en la Nomenclatura con su significado
  (5 a 10 palabras) y su uso, en el mismo turno.
- Lo mismo vale para **toda explicación** de la skill, no solo las preguntas: el mapa de la etapa 2, la
  definición de la etapa 1 y las respuestas de la revisión llevan su bloque «Términos».

## Las 7 etapas

Al activarte, **primero busca detallamientos abiertos**: archivos `ESPEC-*.md` con estado distinto de
`IMPLEMENTADA`, en el repositorio y en sus worktrees (`git worktree list`). Si hay, muestra una lista
con el elemento, la etapa y el avance de la ficha (por ejemplo «revisión 60 %») y lo que falta, y
pregunta si se retoma uno o se empieza otro. Ubica también el diccionario del proyecto
(`diccionario.md` §1); si no existe, propón crearlo. Al retomar, salta a la etapa y al punto de «Dónde retomar».

### 1 · Tamaño

**Primero, converger en el significado.** Antes de cualquier otra pregunta, comprueba que los dos hablan
de lo mismo:
1. Escribe en el chat tu **definición del elemento** en lenguaje sencillo y general, en 2 a 4 líneas: qué
   es, qué hace, para quién, y qué **no** es. Sin jerga ni nombres de clases. Por ejemplo, para «logs de
   telemetría»: «Son los registros que el nodo de la finca envía a la nube para que el operador vea si
   está sano. Dicen cuándo falló algo y cuánto tardó, nunca qué se cosechó ni quién lo anotó. No son los
   logs locales que lee el administrador».
2. Pregunta con el selector: «¿Es lo mismo que tú entiendes?». Opciones: «Es lo mismo» o «Lo entiendo
   distinto». En la segunda, el usuario escribe su propia interpretación.
3. Si difieren, reescribe la definición juntando las dos y vuelve a preguntar. Repite hasta que el
   usuario responda «Es lo mismo». Si después de 3 vueltas siguen sin coincidir, es probable que haya dos
   elementos: propón separarlos y detallar uno.
4. Guarda la definición acordada en la ficha de la ESPEC («Definición acordada») y los términos que
   quedaron claros en el diccionario, como `ACORDADO`. Es el punto de partida
   de todo lo demás, y el verificador la usa para detectar respuestas que se salen de ella.

Haz lo mismo, en corto, cada vez que aparezca a mitad del proceso un **término central nuevo** que
cambie el alcance (por ejemplo «sesión», que puede ser de sincronización o de trabajo). Anota la
definición en el diccionario y en ESPEC §2 como una decisión.

**Encuadre en una sola pregunta** (D-14): la definición del paso 1 va **junto** con el encuadre, en una
sola pregunta con un ejemplo que muestre las cuatro partes. Si el usuario elige «Lo entiendo distinto», se
repite la vuelta del paso 3:
1. **Dominio:** dónde empieza y dónde termina, y qué queda fuera. Por ejemplo, «del endpoint al puerto
   de entrada, sin el dominio».
2. **Generalidad:** conceptual, flujo, contrato o casi línea por línea.
3. **Para qué:** entender lo que existe, o especificar para programar.
4. **Camino mínimo funcional** (D-17, reemplaza la lista larga de D-14): el recorrido de punta a punta
   en 3 a 7 pasos (quién llama, por dónde pasa, dónde queda, qué responde) y **3 a 6 escenarios**: el
   camino feliz, el rechazo genérico y solo los errores sin los cuales el canal no funciona. Al lado, la
   lista **«después»**: lo que se dejó fuera a propósito (motivos finos, reintentos, otros protocolos). Los
   dos van a la ficha de la ESPEC y mandan sobre el árbol de la plantilla.

Crea la ESPEC desde `espec-plantilla.md`, junto al código del módulo, y llena la ficha.
**Sale cuando** el usuario confirma el encuadre.

### 2 · Consulta
- Investiga **solo dentro de los bordes acordados**: la documentación del proyecto (ADR, escenarios,
  requisitos, event storming), el código existente y la ESPEC si ya había una. Respeta las convenciones
  del proyecto (CLAUDE.md, README). Las carpetas humanas (por ejemplo `decisiones/` o los ADR) se leen
  como fuente, pero **nunca se escriben**.
- Elige la plantilla.
- Muestra un mapa de 5 a 10 líneas: qué está decidido, qué choca, qué falta y qué plantilla usarás.

**Sale cuando** el usuario confirma el mapa.

### 3 · Preguntas
- Ronda de **10 preguntas**, siguiendo el árbol (§5) y la poda (§6) de la plantilla. El árbol de cada
  plantilla es un banco de 20 que se recorre en dos rondas: P1–P10 en la primera (lo general: propósito,
  comportamiento, reglas) y P11–P20 en la siguiente (lo detallado: variables, forma técnica, pruebas). Los cupos que libera la poda se
  usan en preguntas de la ronda siguiente. Son 10, y no 20, porque cada pregunta trae más contexto y
  sus términos: 20 cansan antes de terminar.
- **Lo mínimo viable primero** (D-14). Antes de cada pregunta se aplican cuatro filtros:
  - **Filtro de borde** (D-17): ¿sin esta respuesta deja de funcionar el **camino mínimo funcional**? Si
    no, no se pregunta: se usa la regla por defecto o va a §7 («después») en una línea.
  - **Un escenario por pregunta:** cada pregunta cierra un escenario de la lista. No se mezcla un tema del
    MVP con un caso raro o una anomalía, porque la pregunta entera se vuelve de segunda vuelta.
  - **Generalizar:** si la pregunta trata de un caso límite, propón una **regla general** que lo cubra
    junto con sus vecinos. Los casos sueltos van como supuestos, riesgos anotados o pruebas, nunca como
    pregunta propia.
  - **Lo que viaja, con su regla:** la pregunta de un grupo de campos muestra su JSON **y la regla de cada
    campo** (quién lo pone, qué significa `null` o ausente, qué nunca se toca), y se hace antes que los
    errores finos.
- **Presupuesto y parada** (D-14, D-17): para llegar al camino mínimo hay **10 preguntas** como mucho,
  contadas desde el encuadre. Al cubrir la lista se cierra la ronda, aunque no se hayan usado las 10. No hay preguntas de
  proceso ni de cierre («¿seguimos?», «¿confirmas los supuestos?»): los supuestos se escriben en la ESPEC
  y se revisan con el código.
- Después de cada respuesta, actualiza la ESPEC: decisiones como afirmaciones, variables, casos
  *dado / cuando / entonces*. Busca contradicciones con lo que ya existe y, si aparece una, la siguiente
  pregunta es la de desempate.
- **Efecto en cascada en el mismo turno** (D-12): si una respuesta deja un caso límite sin cubrir, se
  registra en ese turno como `K-n` en §6, aunque reste. No se espera al verificador para descubrirlo.
  **Pero solo es `K-n` si bloquea un escenario del MVP** (D-14). Si no, va a §7 como riesgo de segunda
  vuelta, no se pregunta y no resta al índice.
- **A la mitad de la ronda (pregunta 5)**, escribe en la ficha un estimado del índice y lo que falta, en una sola línea.
  El estimado es honesto (D-12): suma lo cerrado y **resta los `K-n` nuevos** de la ronda.
- **Lo nuevo entra a la ronda en curso** (D-12): un `K-n` que nace a mitad de ronda se agrega a su orden.
  Si no cabe en las 10, va como pregunta extra solo con el permiso del usuario; si no, abre la siguiente.
- **Al cerrar la ronda**, lanza un **agente verificador aparte** (herramienta Agent, contexto limpio). Le
  das las rutas de la ESPEC, la plantilla, `indice-precision.md`, `practicas-sanas.md` y la documentación
  del dominio. Te devuelve el índice desglosado, las contradicciones y lo que falta.
- **En paralelo, lanza el agente de coherencia y cohesión** (`revision-coherencia.md`). **No se salta.**
  Revisa la coherencia de cada decisión, hace una pasada extra por la cohesión entre decisiones, y busca
  cómo mejorar los atributos de calidad, con la confiabilidad primero y después disponibilidad,
  rendimiento y eficiencia. Sus hallazgos van a la ESPEC §6 como `COH-n`.
  - **Por debajo de 85 %** (o con una contradicción directa abierta): no hay código. La ESPEC queda
    actualizada, propones las actualizaciones a otros documentos con confirmación, y **pides permiso**
    para otra ronda de 10, que parte de la ESPEC. En el mismo permiso ofreces los **arreglos de
    redacción** (D-11): hallazgos que no cambian ninguna regla (citas rotas, vocabulario, ejemplos
    dorados que faltan, entradas del diccionario). Con el «sí» se aplican antes de la ronda, no ocupan
    pregunta y se cierran como «redacción, con permiso». Tras la primera ronda de una plantilla lo normal
    es no llegar: la mitad del árbol todavía no se ha recorrido.
  - **85 % o más y ningún `COH` bloqueante abierto:** estado `LISTA PARA CÓDIGO`, y pasas a la etapa 4.

- **Después de una poda** (D-10): si la ESPEC se podó con la skill `information-pairment`, antes de otra
  ronda se lanzan otra vez el verificador y el agente de coherencia sobre la ESPEC podada. Les dices que lo
  que tiene destino en una semilla cuenta como trazado (D-07) y que los `K-n` y `COH-n` movidos a
  semillas no son de este elemento. Las citas rotas que deje la poda se reparan como redacción (D-11).

### 4 · Generación
- **El primer segmento es el canal** (D-17): el camino mínimo funcional de punta a punta, aunque cruce
  módulos y plantillas (cliente → transporte → entrada → guardado → respuesta), con **una prueba que lo
  recorre entero** y reglas por defecto simples. Las reglas de filtrado, los motivos finos y la robustez
  van en segmentos posteriores, en el orden en que el usuario los necesite.
- Arma el **plan de segmentos** (ESPEC §8) con las reglas de `verificacion.md` §1:
  - unas 600 líneas por segmento, en promedio;
  - nunca cortas un archivo;
  - un archivo de más de 900 líneas va solo.
- **Diagrama primero** (D-16). Antes de pedir el «sí», dibuja el segmento en draw.io: un diagrama de
  clases con sus paquetes, composiciones (con multiplicidad), dependencias e implementaciones, y, si el
  segmento cruza módulos, uno de contexto que diga quién usa a quién. Va en `diagramas/IA_segmento-<n>.drawio`
  junto a la ESPEC, con la cabecera de cuatro líneas en una nota. Se genera con `herramientas/drawio.py`
  (XML nativo, sin HTML en las etiquetas) y, si está `drawio`, se exporta a PNG
  (`drawio -x -f png -p <página> -o <png> <archivo>`) para revisarlo antes de mostrarlo. Se presenta el
  diagrama **antes** que el código, y se actualiza en el mismo turno cuando la revisión mueve o agrega clases.
- Muestra el plan del segmento siguiente (archivos, líneas estimadas y qué cubren) junto con su diagrama y
  **pide confirmación**. Sin un «sí» no escribes nada.
- **Consulta antes de escribir** (D-15). Para cada segmento, antes de la primera línea, lee de la
  plantilla §3 (patrones), §4 (prácticas), §9 (código de referencia real), §10 (esqueletos) y §11 (orden
  de lo que se genera), y de `referencias/investigacion/` las secciones que esas partes citan para las
  piezas del segmento. Si está el MCP `biblioteca` (libros del usuario), busca con `buscar` cada patrón y
  práctica que vayas a usar, lee la página con `leer` y cítala como «Título · p. N». No se escribe de memoria.
- Escribe **solo** los archivos anunciados, siguiendo §10 y §11 de la plantilla. Los comentarios explican
  el porqué de las decisiones implícitas.
- **Cada decisión implícita cita su fuente** (`plantillas/endpoint.md` §9.4, `investigacion/03` regla 9,
  un libro con capítulo) **o dice `[sin fuente]`**. Una decisión `[sin fuente]` que contradice la
  plantilla o la investigación se anuncia en la ficha antes de la revisión, no después.
- **Fronteras totales** (D-15, `practicas-sanas.md` «Lo imprevisto»): ningún punto de entrada del
  segmento lanza ante una entrada externa; lo imprevisto vuelve como un valor con nombre. Si el usuario o
  la ESPEC no lo piden, se pregunta en la ronda de forma técnica, no se asume.
- Si el usuario rechaza un segmento, **lo refinas en los mismos archivos**: no los borras ni los duplicas.

### 5 · Revisión
- Presenta la ficha del segmento y el orden de lectura (`verificacion.md` §2). Divide en pasadas de 300
  líneas o menos.
- El foco queda **bloqueado** en ese segmento. El usuario pregunta por conceptos, programación y
  funcionamiento.
- Respondes y, mientras respondes, anotas **observaciones** en la ESPEC §10 (`verificacion.md` §3).
  **No reescribes código en esta etapa.**
- **Filtro del camino mínimo en la revisión** (D-17): una observación que no rompe el camino mínimo
  funcional no reabre el segmento; se anota con el segmento posterior donde se trabajará, o en §7.
- **Solo el usuario cierra la revisión.** Al cerrar elige: aplicar las observaciones, dejarlas
  pendientes o seguir.

### 6 · Verificación
Sigue los 7 pasos de `verificacion.md` §4:
1. Aplicar las observaciones sobre los mismos archivos.
2. Recorrer la lista de 22 puntos.
3. Compilar y correr las pruebas.
4. **Revisión de coherencia, cohesión y atributos de calidad** (`revision-coherencia.md`), con un agente
   aparte. **Obligatoria: no se salta.** Sus hallazgos van a la ESPEC §10.
5. **Completitud ≥95 %.**
6. **Explicación ≥80 %:** el usuario explica clase por clase qué hace cada una en la práctica y qué
   significa su patrón en teoría.
7. Si no se alcanza algún umbral o queda un hallazgo bloqueante abierto, vuelves a la etapa 5 con lo que
   falta.

**Qué cuenta** (D-17): los umbrales y el «bloqueante» se miden solo sobre lo que **rompe el camino mínimo
funcional**. Lo demás que encuentren la lista y el agente de coherencia se registra con su segmento
destino o en §7, y no impide aceptar.

### 7 · Aceptación
- El usuario acepta el segmento y la ESPEC se actualiza: el segmento queda aceptado y la matriz
  requisito → código → prueba completa.
- Después: el siguiente segmento (etapa 4), otra ronda de preguntas sobre la plantilla siguiente
  (etapa 3) o fin. Al aceptarse todos los segmentos, el estado pasa a `IMPLEMENTADA`.

## Reglas que no se rompen

- Sin un índice de precisión de 85 % o más, no hay código. Sin completitud de 95 % y explicación de 80 %,
  no hay aceptación. Los tres se miden **sobre el camino mínimo funcional** (D-17), no sobre todo el
  elemento.
- La revisión de coherencia, cohesión y atributos de calidad (`revision-coherencia.md`) **nunca se
  salta**, ni al cerrar una ronda ni al verificar un segmento. Con un hallazgo bloqueante abierto no hay
  `LISTA PARA CÓDIGO` ni aceptación.
- Pides confirmación antes de generar un ejemplo largo, escribir código o modificar un documento que ya
  existe.
- Las carpetas que el proyecto marca como humanas (por ejemplo `decisiones/` o los ADR) son de **solo
  lectura**: las consultas y las citas, pero nunca las escribes. Si una respuesta contradice una decisión
  de ahí, lo anotas como contradicción y propones que el usuario la actualice.
- Si no estás seguro de cómo se comporta un código, lo relees antes de afirmarlo. Afirmar algo falso
  con seguridad es el peor fallo de esta skill, porque crea una falsa comprensión.
- Una observación bloqueante solo se descarta con aprobación del usuario.
- Ninguna explicación usa un término específico sin su definición del diccionario al lado, y ninguna
  palabra significa dos cosas a la vez: si pasa, se para y se resuelve antes de seguir.
- Ningún código aparece sin su significado de 5 a 10 palabras, ni existe sin su entrada en la
  Nomenclatura del diccionario.
