# 2 · Voz del equipo — lo que Juan y Jerónimo decidimos y hablamos

> **Qué es.** Todo lo que el equipo ha decidido, supuesto o discutido, reunido y separado de la voz
> del cliente. Cubre las dos reuniones internas grabadas (S3 y S4), la Q&A del 12-ago, las decisiones
> `DEC-01`..`DEC-16` y el patrón de las columnas «Respuesta Juan» y «Respuesta Jerónimo» de la
> caracterización.
>
> **Regla que gobierna este archivo.** Nada de aquí es requisito. **Ninguna de las 16 decisiones está
> validada con el cliente** y las columnas de la caracterización son hipótesis de dos ingenieros que
> `CN-06` declara **sin experiencia medible en el sector**. Ya pasó una vez que un supuesto del
> equipo se leyera como dicho del cliente (`DEC-16`); este documento existe para que no vuelva a
> pasar.
>
> **Versión** 1.0 · 25-ago-2026.

---

## 0 · Marcas

- `EQ` — supuesto o decisión del equipo, sin validar con el cliente. **Es el estado por defecto de
  todo este archivo.**
- `EQ-CONF` — coincide con algo que el cliente dijo o respondió. Se indica dónde.
- `EQ-CONTRA` — **el cliente ya respondió lo contrario.** Marcado en rojo lógico: hay que reescribirlo.

---

## 1 · Quiénes somos y qué pesa sobre nuestras decisiones

**Juan Pablo Avendaño Duque** y **Jerónimo**, dos estudiantes de ingeniería. El informante del
negocio es el padre de uno de los dos, director de producción de la finca — dato que explica el
acceso privilegiado y también por qué el levantamiento descansa en una sola voz.

Restricciones que el propio equipo se reconoció:

| ID | Qué dice |
|---|---|
| **CN-06** | Decisiones de arquitectura tomadas por **ingenieros sin experiencia medible en el sector** |
| **CN-04** | El proceso a apalancar **apenas se está definiendo**; se implementa lo que se vaya identificando |
| **CN-09** | Disponibilidad limitada antes de la implementación para pruebas |

En S4 quedó grabado el reconocimiento más honesto del equipo sobre su propio método:

> *«esto es una cosa de confusión que no hemos clarificado y que no le hemos dejado bien claro a la
> IA lo que nosotros pensamos al respecto, pero también **hay que meter al cliente en este
> asunto**.»*

Y sobre el 90%, en la misma sesión:

> *«lo de los porcentajes **no lo entiendo ni yo todavía**.»*

---

## 2 · S3 — 11-ago-2026, reunión interna

**Solo el equipo.** Confianza `EQ` en todo lo que salga de aquí, nunca `CONF`.

### 2.1 Los tres roles quedaron argumentados

- *«3 roles principales que toca justificar muy bien porque es la base de todo: supervisor de campo
  […] no es un trabajador cualquiera, sino un supervisor que directamente supervisa cómo va el
  proceso.»*
- Supervisor y auxiliar se unifican: *«los 2 entran en las mismas funcionalidades […] sea un
  supervisor en la nómina o sea un auxiliar de siembra en la nómina […] ese es el rol de la recolecta
  de información.»*
- Se renombra el segundo rol: *«yo diría únicamente **administrador de producción y proyección**»* —
  y se aclara que no es una persona sino varias.
- Se resuelve por qué no se abren más roles: *«no genera valor agregado en el proyecto […] todos
  tienen acceso a la mayoría de información […] pero cada uno puede filtrar mediante el panel»* y
  *«tienen acceso a los mismos tableros»*. `EQ-CONF` — coincide con *«hay información muy compartida
  en todo el sistema»* del cliente en S2, **pero el cliente lo revirtió después** en la
  caracterización.

### 2.2 La IA embebida se descartó como requisito del cliente

Registro literal de S3, y es la cita que más pesa de esta sesión:

> *«la decisión de utilizar una inteligencia artificial embebida dentro de un celular es una decisión
> **de los desarrolladores**, no es en sí una funcionalidad crítica que necesite el usuario. **Fue
> como una idea de venta** que se pudo haber utilizado para presentar la idea más atractiva.»*
> *«el problema principal sigue siendo la recopilación de información de forma eficiente.»*

Y sobre la premisa de velocidad:

> *«**Esto no es una cosa de la entrevista, sino una propuesta.** La idea es ser más rápido que lápiz
> y papel. Y por eso se piensa en la propuesta del modelo IA.»*

> `[!]` **Esta es la declaración de origen más importante de todo el proyecto.** Tanto la IA de
> captura como la premisa de velocidad quedaron registradas por el propio equipo como **propuesta
> del equipo**, no como necesidad del cliente. `DEC-16` las volvió a meter cuatro días después.

### 2.3 El ranking de atributos del equipo, con sus argumentos

Ejercicio hecho en vivo, deliberadamente distinto al del cliente: *«este se hizo con lo del usuario
[…] hacer uno con nuestros criterios».*

| Puesto | Atributo | Argumento textual del equipo |
|---|---|---|
| 1 | **Confiabilidad** | *«confiabilidad, yo creo que sigue siendo la número uno»* — coincidencia con el cliente |
| 2 | Disponibilidad | sin discusión |
| 3 | Capacidad | *«sin capacidad no hay trazabilidad»* · *«hay que llevar el registro posiblemente hasta 5 años»* |
| 4 | Rendimiento | *«hay que hacer más rápidos que el papel y el lápiz»* |
| 5 | Trazabilidad | *«la trazabilidad siempre en el top 5»* |
| 6 | Cap. para ser Administrado | *«la capacidad de ser administrado sigue siendo muy importante»* |
| 7 | **Experiencia de Usuario** | ver abajo |
| 8 | Interoperatividad | *«cubre la parte de la comunicación entre celular y computador»* |
| 9 | Seguridad | *«no es el foco principal […] el foco principal es facilitar la recolección»* |
| 10 | Portabilidad | *«portabilidad es una idea con la que se empezó a vender, sin embargo…»* |
| 11+ | Escalabilidad, Soportado, Accesibilidad | ver abajo |

**El argumento con el que el equipo bajó Experiencia de Usuario del 2 al 7** — el cliente la había
puesto en 2:

> *«aunque sean usuarios a los que se les brinda una experiencia de usuario sencilla, **siguen siendo
> usuarios que conocen el negocio**, no usuarios que necesiten una guía de la mano para poder
> utilizar[lo]. Y además pueden ser entrenados en una o 2 semanas.»*
> *«la idea del sistema es que se adapte al papel y lápiz lo más rápido. Entonces es lo que ya están
> haciendo, lo pueden seguir haciendo, pero ahora desde el celular.»*

**El argumento con el que el equipo bajó Seguridad:**

> *«yo no creo que si uno hace este proyecto para un entorno cerrado […] tenga que preocuparse de
> ataques constantes, como si fuera un Instagram, un GitHub […] entonces la parte de seguridad se
> puede reducir o bajar.»*

> `[!]` Se dijo en la misma sesión el contraargumento y no se resolvió: *«acuérdese que algo que nos
> dio el cliente es que los datos son muy celosos.»*

**El argumento con el que el equipo bajó Escalabilidad al fondo:**

> *«no es un proyecto que se estime para crecer.»*
> *«estamos buscando un desarrollo a medida para un uso muy primordial, con un presupuesto limitado,
> sin la necesidad de crecer […] porque no va a ser un software as a service.»*

> `[!]` **Esto se revirtió dos veces.** `DEC-01` lo devolvió a SaaS multi-tenant, y el cliente
> respondió **11 SÍ de 11** en Escalabilidad, incluido *«que el costo no crezca proporcionalmente por
> finca»* y *«que un cambio se aplique a todas las empresas sin intervención manual»*.

**Accesibilidad, tal como el equipo la ubicó:** de las últimas. La definición que se manejó fue
*«conforme a las discapacidades visuales y auditivas»*. `[!]` En S2 al cliente se le explicó distinto
—*«falta de digitalización, analfabetismo»*— que es justamente la premisa del asistente de captura.

### 2.4 El equipo pensando en el rol de administrador del sistema

> *«yo como un practicante en sistemas […] contratado para mantener un sistema prendido en una
> floristería: yo quiero que eso no se dañe y que si se daña, al menos tenga fácil poder volver a
> revisar información y arreglarlo.»*
> *«como cuando una empresa compra una volqueta: usted me tiene que asegurar que esa volqueta yo le
> pueda dar duro sin importar el conductor.»*

Y una observación fina que no llegó a ningún documento:

> *«la trazabilidad para el supervisor de campo es "yo fui a la cama 25 en la mañana, necesito ver si
> subí bien esa información". Para el administrador del sistema sería "necesito ver quién me hizo
> esto". […] **Dentro de la misma trazabilidad puede haber diferentes interpretaciones.**»*

### 2.5 El modelo de entrega quedó abierto en S3

Discusión sin cerrar, textual:

> *«no va a ser un software as a service, va a ser como infraestructura as a service»* → *«no,
> infraestructura no, es un PaaS»* → *«no, eso es un SaaS, es un SaaS»* → *«plataforma no sería,
> sería el de plataforma como servicio, yo diría que es ese»* → *«no nos pongamos con eso ahora
> mismo».*

**Se cerró sin acuerdo.** En la Q&A del 12-ago se inclinó a **PaaS**; el 15-ago `DEC-01` lo fijó en
**SaaS multi-tenant**; el cliente respondió **SÍ** a instalar en servidores propios.

### 2.6 Cómo se decidió el formato de las preguntas de caracterización

> *«**las preguntas deben ser sí o no.**»*
> *«el sistema debe estar preparado para la subida de información de más de 30 usuarios en un mismo
> día […] esa es una pregunta que se le haría […] al owner para saber si este escenario de calidad
> entra o no entra.»*

Y el escenario ilustrativo que **se descartó después**:

> *«digamos 2 variedades muy parecidas, pero en realidad las voy a tratar como la misma […] puedo
> simplemente omitir la variedad específica porque no necesito sí o sí esa rigurosidad en ese
> momento.»* — **era solo un ejemplo, no un caso real. No usarlo.**

---

## 3 · Q&A del 12-ago-2026 — decisiones del equipo

- **Formato de escenarios de calidad:** dos columnas `ID | Escenario`, párrafo narrativo continuo con
  los seis elementos de Bass/Clements/Kazman, IDs `ESC-001`… agrupados por atributo. Se descartó el
  esqueleto `ESC-01..ESC-08` de `5_RF_CRITICOS_v1.xlsx`.
- **Los 3 roles quedan validados.** Cerrado.
- **Modelo de entrega: se inclina a PaaS**, no SaaS. Razón registrada: el conocimiento de negocio
  viene de dos empresas concretas que el informante conoce a fondo; **no hay investigación de mercado
  que respalde un producto para todos**. Y el despliegue asume un ingeniero de sistemas en planta.
- **Capacidad se sostiene en el puesto 3.** Fundamento que no quedó en ninguna transcripción: la
  información no se borra nunca porque se reutiliza a lo largo del flujo (producción cruzada con
  tierras), y ese es el motivo por el que estas empresas son celosas con sus datos. Se planeaba
  guardar **copias sin modificar + copias modificadas** más trazabilidad completa.
  `EQ-CONTRA` — el cliente respondió **NO** a conservar ambas copias: *«SOLO LA CORREGIDA»*.

---

## 4 · S4 — 17-ago-2026, reunión interna

**Solo el equipo.** Sesión dedicada a leer el formato real de captura y a adoptar ArchiMate.

### 4.1 El encuadre con el que se abrió

> *«dijimos: SaaS multitenant con piloto para mayo de 2027, sin manejar dinero, precios, solamente
> unidades de tallos. Cada sección no se va a contar por esqueje por unidad directamente de plantas,
> sino por un área por densidad. Una base de datos por empresa pero con un mismo esquema para todos.
> Un BI propio, **pero con la necesidad obligatoria de hacer integraciones con otras herramientas de
> BI**. La IA vuelve como captura de datos para garantizar la eficiencia y la velocidad.»*

> `[!]` **Esta frase contradice `DEC-06` cuatro veces en una sola oración.** `DEC-06`, tomada dos
> días antes, dice «BI propio y cerrado, antes que integración con terceros». Y más adelante en la
> misma sesión: *«yo le dije [a la IA] que no vamos a hacer Power[BI], sino que lo vamos a ofrecer
> nosotros, pero entonces ahora toca volver a cambiar esa pregunta, porque **al parecer sí vamos a
> integrar, pero a la vez ofrecer**.»*

### 4.2 La lectura del formato real — hallazgos del equipo

El equipo dictó la estructura a la transcripción para dejarla registrada:

> *«es una hoja del tamaño de oficina normal que arriba, en lapicero escrito a mano, tiene Matsomoto,
> que es la flor. Tiene la semana […] y abajo hay una tabla completa con "Buenavista, novedades de
> siembra" […] 7 columnas: la primera fecha, la segunda bloque, la tercera cama, la cuarta variedad,
> la quinta numerar líneas —**que esa me la tienes que explicar**—, la sexta cantidad y la séptima
> observación.»*

Hallazgo que cambió el modelo mental:

> *«lo que hacen para revisar camas es poner esta flor como segmento principal. **No es como que vaya
> a ir a una cama y a buscar la información, sino que voy a buscar la flor.** Eso es muy importante
> porque yo estaba confundido con eso.»*

Sobre los errores resaltados en el formato: `CIT` del cliente dentro de S4 — *«las resaltadas son los
errores cometidos por el que tomó los datos»* y la observación *«repetido»* significa que *«hay varias
plantillas de la misma flor y la misma fecha con información repetida».*

Y lo que quedó explícitamente pendiente de preguntar:

> *«lote, calibre, proveedor, contenedor […] **queda pendiente de preguntarle al cliente exactamente
> qué significa cada uno.**»*
> *«[calibre] debe ser como una de esas categorizaciones de calidad»* — **especulación del equipo,
> marcada como tal en la propia sesión.**

### 4.3 La adopción de ArchiMate y su motivo real

> *«el problema que he tenido con esta IA es que se me ha acumulado mucha información en muchos lados
> y es tan larga que hasta el contexto de la IA se amplía demasiado y pierde precisión. Entonces toca
> purgar.»*
> *«montar todo lo que ya tenemos construido en una herramienta llamada ArchiMate […] y solamente
> mover ese ArchiMate como único contexto real y principal.»*

### 4.4 La discusión del 90%, sin cerrar

El equipo intentó reconstruir la fórmula y no llegó a un acuerdo limpio:

> *«se hace una proyección de 900, pero entonces para asegurar esas 900 se hace una siembra total de
> 1000. De esa siembra total de 1000 se espera cierta cantidad de pérdidas y esa cantidad de pérdidas
> se espera que esté un 10% por arriba o por abajo de la proyección, o sea de las 900: 990 u 810.»*

Y la delimitación de alcance más clara que dio el equipo:

> *«nosotros no buscamos reducir que sean 10% al 5%, porque en realidad no podemos hacer eso, porque
> **no podemos garantizar que la planta crezca**. Ese es un proceso de la empresa propio: nosotros no
> lo tocamos, simplemente lo facilitamos para que sea más visual, más cómodo. Y llevamos la
> contabilidad.»*

### 4.5 Preguntas que el equipo se hizo en S4 y nunca llevó al cliente

1. *«¿cómo se reparten los tallos en los 7 días que dura el corte? […] si una cama saca 900 tallos,
   en 7 días saldrían 130 un día, 400 otro día, 300 otro día?»* — **el propio equipo la llamó *«esto
   es importante, esto es muy importante»* y sigue abierta.**
2. *«¿a las camas también toca dividirlas por sección de manera opcional? […] puedo tener una cama
   completa con una única flor y varias variedades, o **una cama completa con varias flores**?»* —
   El equipo se respondió a sí mismo con una especulación: *«en cuanto a su variedad sí […] y en
   cuanto a [flor] creería que no»*, y dijo *«ya enseguida pregunto»*. **No consta que se preguntara.**
3. *«¿una nave, creo que es lo mismo que un bloque, no?»* — **sin resolver.** En el formato real no
   aparece la nave.

---

## 5 · Las 16 decisiones del equipo — `DEC-01` a `DEC-16`

Todas del **15-ago-2026**. Todas `EQ`. Se listan con su estado frente a la voz del cliente.

| ID | Qué decide | Estado frente al cliente |
|---|---|---|
| **DEC-01** | FlorLogic es un **SaaS multi-tenant**; se descartan on-premise, PaaS y desarrollo a medida | `EQ-CONTRA` — el cliente respondió **SÍ** a instalar en servidores propios y **SÍ** a llevarse toda la información |
| **DEC-02** | Primero una finca piloto, después el multi-tenant. `CN-01` (mayo 2027) aplica al piloto | `EQ` |
| **DEC-03** | Los rankings de atributos previos quedan descartados; se rehace | `EQ` — ya ejecutada |
| **DEC-04** | El catálogo vigente es `FuncionalidadesSignificativas.xlsx` | `EQ` |
| **DEC-05** | Conflictos: cronológico por defecto, **mediación humana como opción** | `EQ-CONTRA` — *«NO DEJA INGRESAR EL ULTIMO REGISTRO»* |
| **DEC-06** | **BI propio y cerrado**, antes que integración con terceros | `EQ-CONTRA` — *«POWER BI»*, y contradicha por el propio equipo en S4 |
| **DEC-07** | El dominio excluye **precios y rendimiento económico** | `EQ-CONF` — *«solamente va a mostrar qué flor se va a producir»* |
| **DEC-08** | Destino de los cuatro requisitos huérfanos (RF-007, RF-010, RF-015, RF-C19) | `EQ` |
| **DEC-09** | El operador de la plataforma tiene acceso de **infraestructura, no funcional** | `EQ-CONF` parcial — el cliente respondió SÍ a que el operador no lea producción |
| **DEC-10** | Alcance inicial del BI: **los seis reportes actuales** | `EQ-CONF` — salen de S1-Q P32, la respuesta 5/5 |
| **DEC-11** | **Una base de datos por empresa**, esquema común. No se acepta tabla compartida | `EQ` |
| **DEC-12** | Continuidad: pérdida de información **cero** · fallo **1 hora** · restauración **1 día** | `EQ-CONF` — la hora sale de S2 |
| **DEC-13** | La app de plagas **no se toca**; la enfermedad entra como motivo | `EQ-CONF` parcial — el cliente respondió SÍ a convivir, **NO** a consumirla |
| **DEC-14** | **La cama se divide en secciones**; nada se cuenta por esqueje | `EQ` confirmado **documentalmente** (17% de camas divididas en el formato real), no por el cliente |
| **DEC-15** | Dos lecturas de la producción + **vista geométrica** de camas | `EQ-CONTRA` parcial — el cliente respondió **NO** a la vista gráfica del bloque |
| **DEC-16** | **La IA vuelve, partida en dos**: asistente de captura local + IA analítica en la nube | `EQ` en disputa — S3 la había registrado como idea de venta del equipo; el cliente respondió NO a voz y NO a sugerencias |

### 5.1 Las advertencias que el propio equipo se dejó escritas

- Sobre `DEC-01`: *«si eso incluyera al operador, el equipo FlorLogic estaría tocando datos
  productivos de un cliente. Problema contractual, no técnico.»* Y: *«construir para varias empresas
  sobre esta evidencia es un salto mayor que construir a medida.»*
- Sobre `DEC-06`: *«el alcance del BI no está acotado. "Lo que el negocio considere importante" no es
  una lista.»*
- Sobre `DEC-09`: *«una copia de seguridad contiene los datos del tenant. "Acceso indirecto" solo es
  real si los respaldos van cifrados con una clave que el operador no pueda usar.»*
- Sobre `DEC-13`: *«el reporte de FlorLogic responderá "cuánta producción se perdió por enfermedad",
  no "qué plagas hay y dónde". **Decirlo así al cliente.**»*
- Sobre `DEC-16`: *«hasta que haya **cita textual** del cliente pidiéndola, esto es propuesta, no
  confirmado»* · *«"proponer estrategias" no es estimable»* · *«una IA que consulta datos de una
  empresa no puede arrastrar contexto de otra.»*

---

## 6 · Restricciones que el equipo se impuso — `CN-nn`

**De negocio (`CN-01`..`CN-09`), tomadas del cliente:** entrega mayo 2027 · ~20.000 USD · secreto
empresarial (Decisión 486 CAN, art. 260) · proceso en definición · presupuesto de mantenimiento
desconocido · ingenieros sin experiencia en el sector · despliegue que no retrase la operación más de
7 días · resistencia al cambio · disponibilidad limitada para pruebas.
`[!]` La columna «Plan acción» está **vacía en las 9 filas**.

**Técnicas impuestas (7):** `CN-10` PowerBI no es restricción `[!] revertida de hecho` · `CN-11` PayU
`EN DUDA` · `CN-17` sin conectividad en el cultivo · `CN-18` doble canal app + web · `CN-19` la app de
plagas sigue viva · `CN-20` modelo heredado de ~300 tablas **sin identificar** `[!] BLOQUEANTE` ·
`CN-21` dispositivo sin especificar y sin partida de hardware `EN DUDA`.

**Técnicas adoptadas (19), las que más pesan:**
`CN-13` **offline-first obligatorio — es la restricción rectora** · `CN-12` RBAC por (rol, empresa) ·
`CN-22` reglas duras evaluadas en el dispositivo con motivo visible · `CN-23` autenticación offline
toda la jornada · `CN-25` marca de tiempo confiable con bloqueo ante reloj alterado · `CN-26`
catálogo versionado antes de capturar · `CN-27` versionado inmutable de proyecciones · `CN-28`
cifrado en tránsito y en reposo, **custodia de la clave SIN DECIDIR** `EN DUDA` · `CN-29` migraciones
automatizadas sobre N bases · `CN-30` pico simultáneo entre tenants (+60%) · `CN-31` asistente local
con vocabulario restringido, nunca escritura silenciosa · `CN-33` Excel/PDF como única
interoperabilidad de fase 1, **sin API pública** `[!]` · `CN-35` costo operativo por tenant acotado.

**Las cuatro `EN DUDA` son `CN-11`, `CN-20`, `CN-21` y `CN-28`.** `CN-20` la responde el cliente;
`CN-28` la decide el equipo y va también al contrato.

---

## 7 · Los principios que el equipo se fijó

- **`PR-01`** — *el asistente propone, el sistema valida, el usuario confirma.* **Nunca escritura
  silenciosa.** Rige sobre la interpretación de un dato que se captura.
- **`PR-09`** — *automatizar primero, permitir mediación humana como opción.* Rige sobre la
  resolución de conflictos entre datos ya capturados. `EQ-CONTRA`.

---

## 8 · Las columnas de la caracterización — dónde estábamos flojos

Juan respondió 260/262, Jerónimo 260/262. **No son requisitos: son hipótesis.**

| Medición | Valor | Lectura |
|---|---|---|
| Juan vs. Jerónimo | difieren en **15** | el equipo está internamente alineado |
| **Juan vs. Cliente** | difieren en **94** | **36% de desacuerdo** |

**Triage de responsabilidad acordado:** del cliente son `CNF DSP RND AUD CAP UXP ACC POR`; **del
equipo** son `SEG ESL SOP ADM INT` — en estos cinco bloques la respuesta del cliente es opinión, no
requisito.

### 8.1 Los seis sitios donde el equipo estuvo más equivocado

1. **Velocidad de captura.** El equipo dijo SÍ a los cinco escalones (menos que en papel, un minuto,
   treinta segundos, un bloque por jornada, la finca por jornada). El cliente dijo **NO a los cinco**,
   y anotó *«SOBRA»* dos veces.
2. **Ergonomía de campo.** El equipo dijo SÍ a una sola mano, a guantes, a voz, a sugerencias, a ver
   el avance de jornada, a la vista gráfica del bloque. El cliente dijo **NO a todas**.
3. **La ventana offline.** El equipo dijo **NO** a jornada completa, tres días, una semana y quince
   días. El cliente dijo **SÍ a los cuatro**. Es el error de mayor consecuencia arquitectónica.
4. **Cifrado y auditoría técnica.** El equipo dijo SÍ a cifrado local, en tránsito, de respaldos,
   llave por empresa, registro de acceso técnico y de exportaciones. El cliente dijo **NO a todo**.
5. **Escalabilidad.** El equipo dijo NO a costo no proporcional, a cambios aplicados a todas las
   empresas, a años de historia sin lentitud y a añadir labores sin rehacer la captura. El cliente
   dijo **SÍ a los cuatro**.
6. **Interoperar.** El equipo dijo NO a tomar información del sistema administrativo y NO a convivir
   con la app de plagas. El cliente dijo **SÍ a ambas**.

### 8.2 Los sitios donde el equipo acertó y conviene decirlo

Coincidencia total en: rechazo de datos fuera de rango · motivo del rechazo siempre visible · reglas
parametrizables sin nueva versión · reglas iguales sin conexión · integridad ante batería, cierre
inesperado y dispositivo mojado · sincronización retomable · aislamiento absoluto entre empresas ·
que el operador no lea producción · toda la administración de usuarios, variedades, grados y
parámetros sin desarrollo adicional · exportar a Excel y PDF · el paralelo con el papel.

### 8.3 Las dos preguntas que Juan y Jerónimo dejaron en blanco

Las tres de cierre de la sesión. **Solo las respondió el cliente.**

---

## 9 · Estado del trabajo propio del equipo

- **Demo de captura** (`app-captura/`): construida el 21-ago, PWA, seis suites de prueba en verde.
  Sigue **sin commitear**. Su premisa de velocidad **no la pidió el cliente**, y su ventana offline
  de días **rompe la propia condición de PWA** que el equipo fijó en `PLAN_DEMO_CAPTURA §4.4`.
  Lo único que la caracterización **sí respaldó** es la regla de la razón: advertir sin bloquear.
- **Modelo ArchiMate**: pendiente sincronizar `CN-17`..`CN-35` como Constraints y propagar las
  reversiones.
- **Escenarios de calidad**: existe la muestra `ESC-001`..`ESC-024`
  (`FlorLogic_Lluvia_de_Escenarios_v1.xlsx`, 25-ago). 6 verdes · 8 amarillos · **10 rojos**.
- **Caracterización**: hay **tres copias** del archivo y dos gemelos al 0% (`PREGUNTAS_CARACTERIZACION
  .md/.xlsx`) que son lo único que conserva los identificadores `CNF-01`… **Portar los IDs antes de
  archivar.**

---

**Fin del documento 2.** Los choques entre este archivo y el 1 están en
`3_BRECHAS_Y_CONTRADICCIONES.md`.
