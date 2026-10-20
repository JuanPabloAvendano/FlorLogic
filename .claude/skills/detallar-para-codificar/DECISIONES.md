# Decisiones de la skill detallar-para-codificar

Registro de los cambios a la skill y su porqué, para perfeccionarla poco a poco. Lo pide y decide Juan;
el agente lo anota. Cada decisión dice qué cambió, por qué y en qué archivos. Las más recientes van
abajo. Si una decisión reemplaza a otra, la anterior queda marcada «REEMPLAZADA POR D-xx».

El historial de los archivos está en git, en esta misma carpeta (`git log`).

---

## D-01 · Límite de la pregunta: de 20 a 50 palabras — REEMPLAZADA POR D-02

- **Fecha:** 16-oct-2026
- **Qué:** la pregunta pasaba de ≤20 palabras a ≤50 palabras.
- **Por qué:** con 20 palabras, cada pregunta daba muy poco contexto para decidir.
- **Dónde:** `SKILL.md` («Cómo preguntas»), las plantillas de `referencias/plantillas/` y
  `referencias/investigacion/04-tecnicas-de-preguntas.md`.

## D-02 · Forma de la pregunta: título y contexto, con tamaños de texto

- **Fecha:** 16-oct-2026
- **Qué:** cada pregunta es un **título** de una línea (la pregunta) y debajo un **contexto** corto: qué
  se sabe, qué está en juego y por qué se pregunta. Se definen tres tamaños de texto:

  | Tamaño | Palabras | Lectura | Referencia |
  |---|---|---|---|
  | Pequeño | ~50 | ~15 s | Una lista de 3 viñetas con lo esencial |
  | Mediano | ~120 | ~30 s | Un párrafo de apertura, 3 viñetas y una línea de cierre |
  | Grande | ~240 | ~1 min | El doble del mediano |

  El contexto de una pregunta es pequeño o mediano. El grande solo se usa cuando el usuario pide
  ampliar.
- **Cómo se calibró:** Juan dio como ejemplo una respuesta del agente. Las tres viñetas medían
  ~52 palabras (pequeño) y la respuesta completa ~117 (mediano).
- **Por qué:** «preguntas cortas» tenía que querer decir algo que se lee rápido (30 s a 1 min) pero con
  suficiente contexto, no un número arbitrario de palabras.
- **Cómo se muestra:** el contexto va en el texto del turno justo antes de la herramienta de preguntas,
  con el título en negrita; la herramienta repite el título. Se hizo así porque no está comprobado que
  la herramienta muestre bien un texto de varias líneas. **Por verificar en uso.**
- **Dónde:** `SKILL.md` (descripción y «Cómo preguntas»), las plantillas de
  `referencias/plantillas/` (línea «Formato») y `referencias/investigacion/04-tecnicas-de-preguntas.md`
  (longitud y plantilla de la pregunta).

## D-03 · Tolerancia sobre el tamaño mediano

- **Fecha:** 16-oct-2026
- **Qué:** el mediano es una guía, no un corte. El contexto puede pasarlo hasta un 50 % (~180 palabras,
  ~45 s) sin que la pregunta se considere mal formulada. Por encima de ~180 palabras, el agente revisa
  si la pregunta mezcla dos decisiones: si las mezcla, la divide; si no, la deja así y lo dice en una
  línea.
- **Por qué:** un corte rígido obligaba al agente a recortar ideas que se entienden bien solo por pasar
  las 120 palabras.
- **Dónde:** `SKILL.md` («Cómo preguntas»).

## D-04 · Revisión obligatoria de coherencia, cohesión y atributos de calidad

- **Fecha:** 16-oct-2026
- **Qué:** un agente nuevo, aparte del verificador del índice, que revisa la coherencia de cada decisión
  con sus fuentes y con las demás, hace una **pasada extra por la cohesión entre decisiones** (cómo se
  combinan, recorridos de punta a punta, efectos en cascada) y busca cómo mejorar los atributos de
  calidad en este orden: **confiabilidad** (la principal), disponibilidad, rendimiento y eficiencia.
  **No se salta.** Corre al cerrar cada ronda (etapa 3, en paralelo con el verificador) y en la etapa 6
  (paso 4 de 7). Un hallazgo bloqueante abierto impide `LISTA PARA CÓDIGO` y la aceptación.
- **Por qué:** el verificador del índice mide cuánto está especificado, pero no si las decisiones forman
  un todo que funciona. En el contrato de ingesta, una redacción («la partición es de un solo celular»)
  se leyó como algo que no era, y el verificador encontró un choque directo entre dos decisiones
  (A-20 y A-35) que nadie había cruzado.
- **Interpretación por confirmar:** Juan pidió que no se salte «en la etapa de verificación». El agente
  lo aplicó en la etapa 6 y también al cerrar cada ronda, porque ahí es donde hoy se verifica la ESPEC.
- **Dónde:** `referencias/revision-coherencia.md` (nuevo), `SKILL.md` (mapa de archivos, etapas 3 y 6,
  reglas que no se rompen) y `referencias/verificacion.md` §4.

## D-05 · El contexto de la pregunta va dentro de la herramienta

- **Fecha:** 16-oct-2026
- **Qué:** el título y el contexto van juntos en el campo `question` de AskUserQuestion: encima de las
  opciones y al lado del ejemplo encuadrado. El contexto son 2 o 3 viñetas que anticipan las dudas
  probables, con la misma forma que el agente usa cuando el usuario pide aclarar («chat about this»), y
  se generan junto con la pregunta. Fuera de la herramienta solo queda una línea con lo anotado en la
  ESPEC. **Reemplaza el apartado «Cómo se muestra» de D-02.**
- **Por qué:** con el contexto en el texto del turno, el usuario veía el selector sin el contexto al
  lado. Rechazó varias preguntas para pedir aclaraciones, y solo las aclaraciones tenían la forma que
  servía para decidir.
- **Dónde:** `SKILL.md` («Cómo preguntas»).

## D-06 · Diccionario del proyecto y bloque «Términos» en cada explicación

- **Fecha:** 16-oct-2026
- **Qué:** un diccionario por proyecto (no por ESPEC) con términos de **negocio** y **técnicos**. Cada
  entrada tiene tipo, «Es», «No es / se confunde con», ejemplo, fuente y estado (`PROPUESTO`,
  `ACORDADO`, `REEMPLAZADO`). Toda explicación de la skill (contexto de pregunta, mapa de la etapa 2,
  definición de la etapa 1, respuestas de la revisión) termina con un bloque **Términos**: las palabras
  de esa explicación que tienen un significado específico, copiadas del diccionario sin parafrasear.
  **El bloque no cuenta para el tamaño pequeño o mediano**: el contexto se mide solo. El diccionario se
  actualiza en el mismo turno en que aparece un término nuevo o el usuario corrige uno; un uso con otro
  sentido es una contradicción que se resuelve antes de seguir. El agente de coherencia cruza la ESPEC
  con el diccionario.
- **Por qué:** se hablaba de varias cosas con palabras parecidas y objetivos distintos, y el usuario se
  confundía. El caso más reciente: «lote» se usó para el grupo de anotaciones que sube un celular, cuando
  en el negocio es una plantación (A-30 del contrato de ingesta, que separó lote y partición).
- **Dónde:** `referencias/diccionario.md` (nuevo), `SKILL.md` (mapa, «Cómo preguntas», etapa 1, reglas),
  `referencias/espec-plantilla.md` (campo «Diccionario» en la ficha), `referencias/revision-coherencia.md`
  §2.3. En FlorLogic el diccionario es `codigo/IA_DICCIONARIO.md`, sembrado con lote/partición,
  sesión de captura/sesión de sincronización y `confirmado_hasta`/`respaldado_hasta`.

## D-07 · El destino de la lógica fuera del borde es su ronda, no un archivo

- **Fecha:** 16-oct-2026
- **Qué:** cuando una decisión pide lógica que vive fuera del elemento acordado, su archivo existe pero
  no se define en este detallamiento. En la ESPEC §5 se anota en la tabla «Fuera del borde», con la
  ronda y la plantilla donde se detallará, y con eso cuenta como trazada para el índice.
- **Por qué:** en el contrato de ingesta, el verificador bajó la trazabilidad porque decisiones de
  idempotencia y de orden no tenían archivo. Juan: «estamos definiendo documentación de contrato; el
  archivo de destino por lógica debe existir, pero está fuera del contexto de este detallamiento,
  entonces queda por fuera, y es una regla de la skill».
- **Dónde:** `referencias/indice-precision.md` (Trazabilidad) y `referencias/espec-plantilla.md` (§5).

## D-08 · Rondas de 10 preguntas en lugar de 20

- **Fecha:** 16-oct-2026
- **Qué:** cada ronda de la etapa 3 tiene 10 preguntas. El árbol §5 de cada plantilla sigue siendo de
  20 y se recorre en dos rondas: P1–P10 (lo general) y P11–P20 (lo detallado); en las plantillas
  revisadas, P11 es donde empiezan las variables y la forma técnica. El estimado del índice pasa de
  «cada 5 preguntas» a «a mitad de ronda». Después de la primera ronda de una plantilla, lo normal es no
  llegar al 85 %, y se pide permiso para la segunda. El umbral no cambia.
- **Por qué:** con el contexto más largo (D-02, D-03, D-05) y el bloque de términos (D-06), cada
  pregunta pide más lectura; 20 seguidas eran demasiadas.
- **Dónde:** `SKILL.md` (descripción, introducción, mapa, etapa 3), `referencias/indice-precision.md`,
  `referencias/espec-plantilla.md` (ejemplo de «Dónde retomar»), `referencias/plantillas/_estructura.md`
  §5, las instrucciones de `endpoint`, `caso-de-uso`, `pruebas` y `configuracion`, y las reglas 15 y 17
  de `referencias/investigacion/04-tecnicas-de-preguntas.md`.

## D-09 · Nomenclatura de códigos en el diccionario y en «Términos»

- **Fecha:** 18-oct-2026
- **Qué:** el diccionario tiene una sección **Nomenclatura** con todos los códigos: las familias
  (prefijos), con su significado, dónde viven, a qué llegan y quién los crea; y los códigos sueltos de
  fuentes, de la skill y de ejemplo. Cada uno lleva su significado en 5 a 10 palabras. El bloque
  «Términos» explica todo código que aparezca, incluso los de ejemplo. Antes de crear un prefijo o un
  código se comprueba que no choque y se registra en el mismo turno. Los ids propios de la ESPEC
  (`A-n`, `C-n`, `K-n`, `COH-n`, `OBS-n`) solo tienen entrada de familia: su resumen es la primera
  frase de su fila. **Los ids ya usados se mantienen**: no se renombran aunque choquen, y el choque se
  documenta. Los códigos de una misma familia se explican con un patrón parecido (misma etiqueta
  inicial), aunque no siempre igual. El que se aparta tiene su propia fila.
- **Por qué:** el detallamiento de TX-1.K llenó la ESPEC de letras (A, C, K, COH, D, ESC, CN, SPK,
  EP, S, H…) y algunas chocaban (`P` y `D` tenían tres sentidos cada una). Juan: «cualquier tipo de
  código deberá ser autoexplicativo» y «mantener los IDs ya utilizados pero misma familia o categoría,
  se especifican similar aunque puede que no siempre igual».
- **Dónde:** `referencias/diccionario.md` §4 y §5 (nueva), `SKILL.md` («Cómo preguntas», reglas). En
  FlorLogic: `codigo/IA_DICCIONARIO.md`, sección «Nomenclatura».

## D-10 · Verificar otra vez después de podar

- **Fecha:** 18-oct-2026
- **Qué:** si la ESPEC se poda con la skill `information-pairment`, antes de seguir preguntando se
  lanzan de nuevo el verificador y el agente de coherencia sobre la ESPEC podada. En sus instrucciones
  va que lo que tiene destino en una semilla cuenta como trazado (D-07) y que los `K-n` y `COH-n`
  movidos a semillas no son contradicciones del elemento.
- **Por qué:** en TX-1.K la poda subió el índice de 64,5 a 74,8 % sin una sola pregunta, y la nueva
  verificación encontró restos de la poda (citas rotas) que habrían restado en la ronda siguiente.
- **Dónde:** `SKILL.md` (etapa 3).

## D-11 · Arreglos de redacción con permiso y sin pregunta

- **Fecha:** 18-oct-2026
- **Qué:** los hallazgos que no cambian ninguna regla (citas rotas, vocabulario, ejemplos dorados que
  faltan, entradas del diccionario, catálogos de códigos ya decididos) se agrupan y se ofrecen en el
  mismo permiso que pide la ronda siguiente. Con el «sí» se aplican antes de la ronda, no ocupan
  pregunta y se cierran como «redacción, con permiso». Lo que sí cambia una regla sigue siendo pregunta.
- **Por qué:** en TX-1.K, COH-70, COH-73, K-88, COH-79 y COH-80 eran redacción. Gastar preguntas en
  ellos dejaba sin cupo las decisiones reales, y no hacerlos los mantenía restando.
- **Dónde:** `SKILL.md` (etapa 3, «Por debajo de 85 %»).

## D-12 · Efecto en cascada en el mismo turno y estimado honesto

- **Fecha:** 18-oct-2026
- **Qué:** si una respuesta deja un caso límite sin cubrir, se registra en ese mismo turno como `K-n`,
  aunque reste. El estimado a mitad de ronda suma lo cerrado y resta los `K-n` nuevos. Un `K-n` que
  nace a mitad de ronda entra al orden de la ronda en curso; si no cabe en las 10, va como pregunta
  extra solo con el permiso del usuario.
- **Por qué:** en la ronda 1i cada decisión abrió ambigüedades pequeñas (K-88 a K-92) que solo vio el
  verificador, y el índice subió menos de lo esperado (74,8 → 78,0). En la 1j se registró K-93 en el
  turno de la P3. Juan pidió el 18-oct «añadir lo más nuevo a las preguntas».
- **Dónde:** `SKILL.md` (etapa 3).

## D-13 · Etiquetas del contexto, reabrir decisiones y una decisión por pregunta

- **Fecha:** 18-oct-2026
- **Qué:** cada viñeta del contexto abre con su etiqueta: «Ya decidido:» o «Hoy:», «El problema:» o
  «Por qué se pregunta:», «Qué está en juego:» y, si hace falta, «Lo que no cambia:». Si un hallazgo
  pide cambiar algo que el usuario ya decidió, la pregunta lo cita con su ronda y una opción es
  mantenerlo, con el riesgo anotado. Dos hallazgos que piden la misma decisión (un `K-n` y un `COH-n`
  duplicados) van juntos; dos decisiones distintas nunca comparten pregunta.
- **Por qué:** es la forma que se usó en las rondas 1i y 1j de TX-1.K, y Juan pidió fijarla (18-oct). La
  opción «mantener» evita reabrir en silencio una decisión suya (COH-63, ronda 1h P4). La P10 de la 1i
  separó K-84 de K-83 porque eran dos decisiones.
- **Dónde:** `SKILL.md` («Cómo preguntas»).

---

## D-14 · Lo mínimo viable primero: escenarios, filtro, generalizar, presupuesto

- **Fecha:** 18-oct-2026
- **Qué:** seis genes de la variante v2 del laboratorio, que pasaron a la skill por pedido de Juan:
  1. El encuadre de la etapa 1 va en una sola pregunta, junto con la definición.
  2. En el encuadre se arma una lista de 8 a 15 escenarios primordiales del MVP, en la que cada grupo de
     campos que viaja es un escenario.
  3. Antes de cada pregunta, un filtro de borde: solo se pregunta lo que cambia lo que el elemento recibe,
     devuelve o garantiza en el MVP.
  4. Una pregunta cierra un escenario, y los casos límite se generalizan en una regla.
  5. Los campos se preguntan temprano, con la regla de cada uno.
  6. Hay un presupuesto de 15 preguntas, con parada al cubrir la lista y sin metapreguntas.
  
  La cascada de D-12 solo abre un `K-n` si bloquea el MVP; lo demás va a §7 como riesgo y no resta al
  índice.
- **Por qué:** en TX-1.K, 14 rondas bajaron el índice de 80,4 a 75,2 % porque cada respuesta abría casos
  nuevos, y Juan tuvo que forzar el 85 % (bitácora del laboratorio, 18-oct). En la corrida
  `laboratorio-skills/corridas/c16-contrato-mvp-genetico/` hubo un juez que respondía solo con la
  referencia MVP:
  - v0, el control, cubrió 15/19 con 20 preguntas y abrió unos 6 `K` por tanda;
  - v2 cubrió **17/19 con 15 preguntas y 3 tandas**, con una sola redundante.
  
  El gen que más rindió fue «lo que viaja, con su regla»: sin él, cabeceras, autoría, tiempo y datos
  quedaban sin regla.
- **Límite:** es un solo caso y una corrida por variante. El fallo que le quedaba a v2, mezclar un tema del
  MVP con una anomalía en la misma pregunta, se corrige aquí con «un escenario por pregunta», pero esa
  regla no se probó.
- **Dónde:** `SKILL.md` (etapa 1, encuadre; etapa 3, «Lo mínimo viable primero», presupuesto y cascada) y
  `referencias/indice-precision.md` (contradicciones).

## D-15 · Consultar antes de escribir y fronteras totales

- **Fecha:** 19-oct-2026
- **Qué:** (1) en la etapa 4, antes de cada segmento se leen §3, §4, §9, §10 y §11 de la plantilla y lo
  que citan de `investigacion/`, más la documentación que el usuario haya agregado; la ficha del segmento
  lleva «Consultado» y cada decisión implícita cita su fuente o dice `[sin fuente]`. (2) Práctica nueva
  «Lo imprevisto»: fronteras totales, lector tolerante, invariantes con Notification, Special Case y
  barrera de fallos; la ficha dice qué hace cada punto de entrada ante lo imprevisto.
- **Por qué:** en TX-1.K el Segmento 1 se escribió desde la ESPEC sin abrir la plantilla ni la
  investigación (OBS-1-2): salió una forma de nombrar distinta de T3 y un `IllegalStateException` que la
  regla 11 de `investigacion/03` prohíbe. Además Juan pidió que «cualquier segmento pueda controlar lo
  imprevisto» y que el proceso siga sin pararse (OBS-1-4, A-57 de esa ESPEC).
- **Límite:** un solo caso. La documentación externa (libros) todavía no tiene un acceso formal; queda
  como pendiente un MCP o RAG de consulta.
- **Dónde:** `SKILL.md` (etapa 4), `referencias/verificacion.md` §2 (ficha), `referencias/practicas-sanas.md`
  («Lo imprevisto»).

## D-16 · Diagrama del segmento antes que el código

- **Fecha:** 19-oct-2026
- **Qué:** cada segmento se presenta primero como diagrama de draw.io (clases, paquetes, composiciones,
  dependencias, implementaciones; y contexto de módulos si cruza módulos), guardado junto a la ESPEC en
  `diagramas/IA_segmento-<n>.drawio`. Se genera con `herramientas/drawio.py` y se revisa exportado a PNG.
  La ficha del segmento lo cita. Se actualiza cuando la revisión cambia clases.
- **Por qué:** Juan pidió entender el segmento como diagrama de clases antes de cualquier cosa; la lista de
  clases de la revisión del Segmento 1 de TX-1.K «suena a un diagrama». Prefiere draw.io porque las
  flechas de Mermaid no le gustan.
- **Dónde:** `SKILL.md` (etapa 4), `referencias/verificacion.md` §2 (ficha), `herramientas/drawio.py`.

## D-17 · El camino mínimo funcional primero

- **Fecha:** 19-oct-2026
- **Qué:** la skill detalla y programa primero el **camino mínimo funcional** (CMF): la ruta más corta que
  hace funcionar la transacción de punta a punta, con reglas por defecto simples (todo fallo de forma o de
  estructura es un rechazo directo, sin motivo detallado). La pregunta que decide es «¿sin esto deja de
  funcionar el camino mínimo?». Los otros protocolos (reintentos, sincronización incompleta, recuperación)
  son otras funcionalidades y van a §7. Cambia: el encuadre (CMF de 3 a 7 pasos y 3 a 6 escenarios, en vez
  de 8 a 15), el filtro de borde, el presupuesto (10 preguntas), el plan (el primer segmento es el canal
  de punta a punta con su prueba), la revisión y la verificación (lo que no rompe el CMF no reabre ni
  bloquea) y la forma de medir los umbrales.
- **Por qué:** Juan, 19-oct: «la skill no busca entrar en el detalle absoluto y solucionar cada pedazo del
  producto hoy mismo… mi objetivo ahora mismo es poder realizar un POST desde la app hasta el backend… lo
  primero que hay que construir es el canal y luego las reglas de filtrado… cualquier fallo en el JSON y su
  estructura es un rechazo directo… esto es importantísimo para el alcance definido al inicio». En TX-1.K,
  14 rondas y un segmento con 17 observaciones se fueron al detalle sin que existiera todavía el canal.
- **Reemplaza en parte a D-14:** la lista de 8 a 15 escenarios del MVP y el presupuesto de 15 preguntas.
- **Dónde:** `SKILL.md` (alcance, etapas 1, 3, 4, 5, 6 y reglas), `referencias/revision-coherencia.md` §1.

---

## Pendientes y observaciones para próximas iteraciones

- **Filtro del MVP también en las etapas 5 y 6 (Juan, 19-oct):** en la verificación del Segmento 1 de TX-1.K, la lista de 22 puntos y el agente de coherencia abrieron 9 observaciones, y Juan aceptó de forma forzada para no «quedarse ahondando». **Aplicada en D-17.**
- **Documentación de consulta (Juan, 19-oct):** hecho como MCP `biblioteca` (`~/Work/biblioteca`, búsqueda FTS5, 58 libros). Falta: los 3 CHM (PoEAA incluido), OCR de 2 escaneados y búsqueda por significado. Probar en las rondas (etapa 3) además de la etapa 4.

- **Espiral de escenarios (Juan, 18-oct, uso real en TX-1.K):** identificar primero los escenarios primordiales para el MVP. Generalizar antes de crear un proceso por cada caso límite. Si una ronda abre tantos hallazgos como cierra, o el índice baja dos rondas seguidas, ofrecer generalizar, podar o pasar a código con riesgos anotados. Detalle en `laboratorio-skills/BITACORA-LABORATORIO.md` (18-oct). **Convertido en D-14** (18-oct) tras la corrida c16.

- Verificar en una ronda real que el campo `question` muestra bien el título y las viñetas en varias
  líneas (D-05).
- La memoria del proyecto (`skill-detallar.md`) guarda un resumen de D-02 y D-03.
- Revisar con Juan las entradas `PROPUESTO` de `codigo/IA_DICCIONARIO.md` (D-06).
- Comprobar en uso si el corte P10/P11 cae bien en todas las plantillas; en `pruebas` el corte es menos natural (D-08).
- **Para más adelante (pedido por Juan el 2026-10-16): análisis multiagente para afinar la skill.** Versión pulida, en 3 partes: `mejora/01-prompt-investigacion.md`.
  Hacer un análisis con varios agentes, tomando como ejemplos skills oficiales de Anthropic y de GitHub,
  para mejorar el enfoque y la precisión de la skill con retroalimentación y consultas constantes, y
  definir por completo cada aspecto. Hoy eso ya se hace, pero a mano y para detalles muy puntuales.
  Antes de empezar se recomienda investigar con agentes y ejemplos que tengan un resultado esperado.
  Por ejemplo: tomar una plantilla de código con ciertas especificaciones y su documentación, y que un
  agente responda de forma vaga al agente que usa la skill. El objetivo es que, solo con esas
  respuestas, la skill logre construir un producto parecido a la plantilla.
