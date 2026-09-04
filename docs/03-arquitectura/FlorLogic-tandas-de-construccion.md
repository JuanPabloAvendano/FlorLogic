# FlorLogic — Tandas de construcción

**Versión 1.1 · 4-sep-2026 · Juan Pablo Avendaño y Jerónimo Montoya**

**Este documento no decide nada.** Ordena lo que decidieron los 31 ADR de
`FlorLogic-alternativa-de-solucion-y-ADR.md`, que es y sigue siendo **la entrada única a la
solución**. Si algo de aquí contradice un ADR, manda el ADR y este documento está mal.

**Para qué existe.** El sistema se construye **por partes**, y hacía falta un sitio donde estuviera
escrito, para cada parte: **qué construye · qué ADR la sostiene · qué requisito y escenario cubre ·
qué tiene que estar resuelto antes de empezarla · y cuándo se da por terminada.** Sin eso, cada vez
que se retoma el trabajo hay que reconstruir el contexto entero, y es donde se cuela una premisa
derogada.

**Cómo se usa.** Se pide una tanda por su identificador —*«arranquemos `T1`»*— y este documento dice
qué hay que leer antes y contra qué se verifica al terminar. **Ninguna tanda arranca con una
precondición sin cumplir**; si una lo hace, lo que se construye hay que rehacerlo.

---

## 0. Las tres reglas que gobiernan el orden

**1 · Nada de la lista de §6.5 del documento de ADR se construye antes de la compuerta 3.** Está
repetido allí a propósito, porque es donde se fuga el presupuesto.

**2 · Una tanda con precondición del cliente no se «empieza a medias».** `BR-23` y `D1` no se
sustituyen por un supuesto plausible: es la regla de honestidad del proyecto. Lo que sí se puede
construir es **la maquinaria que los recibirá cuando lleguen**, y eso está dicho tanda por tanda.

**3 · Lo que se decida tarde se paga migrando `N` sedes, no refactorizando.** Es el razonamiento
entero de `ADR-021`, y es la razón de que `T0` y `T1` vayan primero aunque no se vean.

---

## 1. Las tandas, de un vistazo

| # | Tanda | Qué produce | ¿Puede arrancar hoy? |
|---|---|---|:--|
| **`T0`** | **Andamiaje e instalación** | Una instalación vacía que se levanta y se migra sola | ✅ **Sí** |
| **`T1`** | **El dominio: la primera tabla** | Jerarquía, catálogo de campos y el evento de `ADR-024` | ✅ **Sí** — las cinco decisiones previas están cerradas |
| **`T2`** | **Validación** | Motor de reglas en los dos lados, con casos dorados | ✅ Sí, tras `T1` |
| **`T3`** | **Captura en campo** | La cama completa de una sentada, sobre `PoC-0` | ✅ Sí, tras `T1`+`T2` |
| **`T4`** | **Sincronización e ingesta** | Sesión, idempotencia, asignación y distribución | ✅ Sí, tras `T1` |
| **`T5`** | **Identidad y permisos** | Credencial offline y separación de deberes | 🟠 Falta reescribir `RF-017` (redacción, `ADR-020` §2) |
| **`T6`** | **Cierre de producción** | La frontera semántica y la consolidación | 🟠 Falta **definir el evento de cierre** — es barato y cuelga mucho |
| **`T7`** | **Consulta e histórico** | Agregados del cierre y carga progresiva | ✅ Sí, tras `T6` |
| **`T8`** | **Observabilidad y operación** | Los dos planos, actualización y restauración | ✅ Sí, tras `T4` |
| **`T9`** | **Interoperabilidad y exportación** | Exportación y lectura desde la BI del cliente | ✅ Sí, tras `T7` |
| **`T10`** | **Proyección** | El motor y la desviación | 🔴 **BLOQUEADA por `BR-23`** — del cliente |
| **`T11`** | **Asistente de captura** | La IA en el dispositivo | 🔴 **BLOQUEADA por la compuerta 1** (`SPK-01`) |

**Lo que esta tabla dice y conviene leer despacio:** hay **siete tandas que pueden arrancar ya**, y
las dos bloqueadas lo están por cosas que **no se destraban trabajando más**. `T10` espera un número
del agrónomo y `T11` espera una medición de tres días.

---

## 2. Tanda por tanda

### `T0` · Andamiaje e instalación

**Qué construye.** El esqueleto que hace verdad la frase *«una instalación por empresa con esquema
común»*: imagen de contenedor única, `Compose`, herramienta de migración con orquestador sobre `N`
bases **desde el primer día**, y el **discriminador de empresa en toda consulta con una prueba
automatizada que falle si falta**.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-001` (monolito modular) · `ADR-003` (aislamiento en tres capas) · `ADR-016` (despliegue portable) |
| **Bloques** | `BB-14` contenedores · `BB-15` migraciones · `BB-03` base de datos · `BB-06` pasarela |
| **Requisitos** | `RF-012` |
| **Escenarios** | `ESC-16`, `ESC-21`, `ESC-50`, `ESC-52`, `ESC-59`, `ESC-64` |
| **Precondiciones** | Ninguna |
| **Se cruza con** | `SPK-03` mide exactamente esto. **Conviene ejecutarlo sobre lo que salga de `T0`**, no antes ni aparte |

**Criterio de terminado.** Una instalación vacía se levanta desde cero y se migra **por
automatización, sin presencia física** (`CN-29`), y existe una prueba que **falla** si una consulta
olvida el discriminador de empresa (`E3`).

**Qué NO entra.** Autenticación real, RBAC completo, aprovisionamiento de `N` sedes.

---

### `T1` · El dominio: la primera tabla

**Qué construye.** El modelo de datos entero de `ADR-024`, que es lo que llevaba bloqueado desde
`CN-36`:

1. **La estructura relacional** — finca, bloque, nave, cama, sección; variedad y subvariedad; grado,
   labor, motivo y unidad. **El lote no es un nivel del árbol**: es una agrupación transversal de
   secciones sembradas juntas.
2. **El catálogo de campos**, con su tabla de activación por empresa —*«la plantilla tiene 20
   columnas; yo solo uso 5»*—, y las dos propiedades por campo: **niveles admisibles** y
   **propagación** (`se_hereda` / `no_se_reparte`).
3. **El evento**, con la tupla completa: `(producción · sección · campo · valor · autor · sello de
   captura · dispositivo · sesión de sincronización · versión de configuración · corrige_a)`. **El
   valor en `JSONB`, y solo el valor.**
4. **La identidad**: UUID v7 del dispositivo, **opaco** (nada se ordena por su contenido), guardado
   como `uuid` nativo; el **consecutivo por dispositivo**, monótono y persistente; y la **clave de
   hecho declarada en el catálogo** —`(producción · sección · campo · jornada)`, **sin autor ni
   dispositivo**, que es cosa distinta de la identidad.
5. **Los tres tiempos de `ADR-031`**: sello crudo (nunca se toca), sello normalizado por el desfase
   (es el que ordena `RF-022`) y tiempo de sesión. Más el **desempate determinista**.
5. **El paquete de configuración de la empresa**, con su número de versión único y su manifiesto.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-024` (modelo) · `ADR-027` (identidad) · `ADR-028` (sesión, la columna) · `ADR-029` (versionado) · `ADR-031` (los tres tiempos) · `ADR-004` (append-only con horizonte) |
| **Bloques** | `BB-03` base de datos |
| **Requisitos** | `RF-013`, `RF-016`, `RF-022`, y el sustrato de todos los demás |
| **Escenarios** | `ESC-07`, `ESC-23`, `ESC-24`, `ESC-44`, `ESC-48`, `ESC-62`, `ESC-65` |
| **Precondiciones** | **Ninguna. Las cinco decisiones de `ADR-021` están cerradas** (`ADR-024`, `ADR-027`, `ADR-028`, `ADR-029`, `ADR-030`) |

**Criterio de terminado.** Tres pruebas, y las tres son el punto entero de la tanda:

- **Añadir un tipo de labor o de medición nuevo es insertar filas**, y no toca el esquema ni obliga a
  rehacer captura existente. Es `CN-36` verificado, no prometido.
- **Se puede reconstruir el estado consolidado por campo** de una producción a partir de sus eventos,
  y **con los nombres que la jerarquía tenía cuando ocurrieron** — desde la versión de configuración
  que el evento referencia, no desde la actual.
- **Dos eventos con la misma clave de hecho conviven** y `RF-022` decide cuál es el estado, sin
  mediación humana (`ESC-34`) — **y reconstruir ese estado dos veces da el mismo resultado**, que es
  el desempate determinista de `ADR-031` §4.
- **Los tres casos de idempotencia se distinguen** (`ADR-027` §2): mismo id y mismo contenido es un
  reenvío sin efecto; **mismo id y contenido distinto se rechaza y alerta**, porque es corrupción y no
  un reintento; id nuevo con la misma clave de hecho entran los dos.

> `[!]` **Dos supuestos que hay que confirmar y que no bloquean.** *(a)* `ADR-024` asume
> **producción = un ciclo sobre una sección**; la aritmética cuadra pero es una coincidencia
> numérica, no una confirmación del cliente. *(b)* La **jornada** de la clave de hecho se construye
> como día natural y se cambia por dato del catálogo — depende de `D1`. **Ninguno de los dos cambia
> el esquema**, que es precisamente lo que se compró con `ADR-024`.

> `[!]` **Lo que sigue faltando y no bloquea:** el **contenido** del catálogo de campos —las ~20
> columnas comunes de `A14`— depende de `D7`, `D8` y `D9`. Se construye la maquinaria; las columnas
> entran después **como filas**.

---

### `T2` · Validación

**Qué construye.** El motor de reglas dirigido por datos, **la misma especificación en dispositivo y
servidor**, con las dos clases que `ADR-024` exige: **límites estáticos** (tipo, unidad, rango,
obligatoriedad) y **reglas de coherencia**, que leen otros campos de la misma producción —`tallos ≤
plantas sembradas`, un corte anterior a la siembra, una erradicación sobre algo ya erradicado—. Y la
**suite de casos dorados** que corre en integración continua contra **ambas** implementaciones.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-006` (motor de reglas) · `ADR-024` (la validación va **antes** del evento) · `ADR-029` (una sola versión) |
| **Bloques** | `BB-02` almacén local · `BB-16` distribución |
| **Requisitos** | `RF-004`, `RF-005`, `RF-013`, `RF-020` |
| **Escenarios** | `ESC-02`, `ESC-07`, `ESC-23`, `ESC-56`, `ESC-57`, `ESC-63`, `ESC-65` |
| **Precondiciones** | `T1` |
| **Se cruza con** | `SPK-05`. **Sin la suite de casos dorados, `ESC-57` es una promesa, no un mecanismo** |

**Criterio de terminado.** `ESC-02` literal: **100% de lo fuera de rango rechazado en el dispositivo,
en menos de 1 segundo, sin red, y cero llega al servidor.** Y **0% de divergencia** entre los dos
motores sobre la suite (`ESC-57`); una sola divergencia sin causa identificada dispara el criterio de
muerte de `SPK-05` y se pasa a un motor único.

> **La consecuencia que se paga aquí, dicha ahora:** las reglas de coherencia obligan a que **el
> estado consolidado por campo esté materializado también en el dispositivo**. Sin él, el motor local
> no puede comprobar nada que dependa de otro campo, y `CN-13` exige que valide sin red. **Ese almacén
> local es parte de esta tanda, no de `T3`.**

---

### `T3` · Captura en campo

**Qué construye.** El flujo real de `ADR-024`: **el capturador abre una cama y la anota completa de
una sentada**, viendo **solo el conjunto de campos que toca mirar** —no una lista de veinte columnas—,
y ese único acto produce datos para las secciones de esa cama. Más el **«visitada sin novedad»** de
`ADR-026`, que es un toque y **cambia el flujo**. Todo sobre `PoC-0`, que ya tiene almacén local,
motor de reglas, escaneo y outbox.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-002` (el dispositivo es el sistema de registro) · `ADR-024` §4 y §5 · `ADR-026` (visitada sin novedad) · `ADR-008` (la tecnología la decide `SPK-02`) |
| **Bloques** | `BB-01` cliente de captura · `BB-02` almacén local |
| **Requisitos** | `RF-001`, `RF-002`, `RF-003` |
| **Escenarios** | `ESC-04`, `ESC-15`, `ESC-26`, `ESC-27`, `ESC-33`, `ESC-36`, `ESC-37`, `ESC-55` |
| **Precondiciones** | `T1`, `T2`. **Y la reescritura de `RF-001`/`RF-002`** sobre secciones de cama: hoy siguen redactados sobre «cantidad de esquejes por cama», modelo que `DEC-14` invalidó (`ADR-020` §2) |
| **Se cruza con** | `SPK-01`, que **tiene que cronometrar este flujo completo**: una cama entera de una sentada, con su conjunto de captura y con el «visitada sin novedad» |

**Criterio de terminado.** Una cama con dos secciones se captura entera en una sentada, produce
eventos para las dos, y `SPK-01` tiene **el número de toques y de segundos medido**. Ese número
decide la compuerta 1 y con ella `T11`.

`[!]` **`PoC-0` contradice hoy cuatro decisiones vigentes, y hay que arreglarlo antes de medir sobre
él** — la tabla completa está en las consecuencias de `ADR-027`. En corto: ordena la bandeja **por el
contenido del UUID**; tiene la clave de hecho **quemada en código y a nivel de cama**; resuelve el
choque **con mediación humana** (`DEC-05`, derogada por `B7`); y la identidad del dispositivo vive en
`localStorage`, que en modo privado genera una nueva cada vez. **Lo que sí queda ratificado** es la
generación de UUID v7 en el dispositivo —con su contador para las colisiones dentro del mismo
milisegundo, que es lo que pasa al guardar una cama de un tirón— y el outbox que no borra hasta que el
servidor confirma.

> `[!]` **La compuerta 2 manda sobre esta tanda.** Antes de que `SPK-02` cierre, **no se escribe una
> línea de código de producto para móvil**: lo de `T3` se hace sobre el prototipo, y lo que sobrevive
> de él es el modelo, el catálogo de reglas y el contrato de sincronización.

> `[!]` **`ESC-26` no se le compromete al cliente.** `B2` cerró que el cliente **no** pide velocidad
> de captura. `SPK-01` mide los segundos porque son lo que decide si el asistente entra, pero el
> número no se convierte en compromiso hasta que él lo pida.

---

### `T4` · Sincronización e ingesta

**Qué construye.** La **sesión de sincronización de `ADR-028`** como entidad del servidor —con reloj
de servidor, versión de configuración, conteos y resultado, y sin sesiones abiertas para siempre—; la
ingesta idempotente por identificador; la cola sobre PostgreSQL; la **bajada del paquete de
configuración y de la asignación** en el mismo intercambio; y la **asignación acotada en el tiempo**
que es el denominador de `ADR-026`.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-028` (qué es una sesión) · `ADR-027` (idempotencia) · **`ADR-031` (el desfase medido y el orden)** · `ADR-025` (cadencia diaria, recapturar) · `ADR-026` (la asignación como denominador) · `ADR-009` (cola sobre la BD) · `ADR-029` (el paquete) |
| **Bloques** | `BB-09` cola · `BB-16` distribución · `BB-07` CDN · `BB-06` pasarela |
| **Requisitos** | `RF-003`, `RF-020`, `RF-022` |
| **Escenarios** | `ESC-01`, `ESC-05`, `ESC-11`, `ESC-18`, `ESC-34`, `ESC-38`, `ESC-46`, `ESC-47`, `ESC-59`, `ESC-60`, `ESC-61` |
| **Precondiciones** | `T1`. **Y renegociar `ESC-46` y `ESC-54`** (`ADR-020` §3): fuera el «≤5 min», y «0 registros perdidos» pasa a **pérdida acotada a ≤1 jornada de un capturador más la lista de camas a rehacer** |
| **Se cruza con** | `SPK-04`, sobre **el hardware que se le va a vender al cliente**: si la sincronización de jornada supera 30 min bajo carga sintética de +60%, entra `BB-09` como broker dedicado o sube el nodo — **y en ese caso cambia el precio de instalación** |

**Criterio de terminado.** Reenviar el mismo evento mil veces no cambia el estado ni la procedencia.
Un intercambio cortado a la mitad deja una sesión `parcial` con lo que alcanzó a aplicar, y el
reintento la completa sin duplicar. **Y la lista de camas a rehacer se puede pedir para un dispositivo
concreto y sale bien** — que es lo que convierte la pérdida de un teléfono en una orden de trabajo en
vez de en un agujero.

**Y una prueba que no es obvia y es la que más vale:** **dos dispositivos con los relojes desfasados a
propósito, anotando la misma sección, tienen que dar el mismo estado que si estuvieran en hora.** Si
gana el del reloj adelantado, `ADR-031` no está implementado y el sesgo ya está dentro de los datos.
El outbox se drena **por el consecutivo**, no por el sello ni por el UUID.

> `[!]` **El recordatorio no es un adorno: es lo que sostiene la estrategia entera de `ADR-025`.**
> *«Sale más barato recapturar»* **solo es cierto si el dato es reciente**: a un día se recuenta la
> cama; a quince nadie recuenta unos tallos ya despachados. El escalado del aviso —de aviso, a
> estorbo, a impedir continuar— **es parte de esta tanda**, y su umbral es una decisión de negocio
> que va al cliente con `BR-N5`.

---

### `T5` · Identidad y permisos

**Qué construye.** Identidad propia con **credencial firmada de vigencia acotada** que se evalúa en el
dispositivo sin conexión; RBAC contra el par **(rol, empresa)**, nunca contra el rol solo; identidad
individual en dispositivos compartidos, que es como trabaja la finca (`B10`); y **el alta del
dispositivo contra el servidor** (`ADR-027` §5) — sin ella, la asignación de `ADR-026` no tiene sujeto
y el consecutivo no tiene dueño estable.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-007` (credencial offline) · `ADR-019` (separación de deberes) · `ADR-003` (tercera capa del aislamiento) |
| **Bloques** | `BB-05` proveedor de identidad propio |
| **Requisitos** | `RF-014`, `RF-017`, `RF-012` |
| **Escenarios** | `ESC-06`, `ESC-13`, `ESC-22`, `ESC-28`, `ESC-49` |
| **Precondiciones** | **Reescribir `RF-017`** partiendo administración técnica (usuarios, parámetros, catálogo) de autorización de correcciones de producción. **`ESC-06` está EN CONFLICTO por esto** y la matriz de permisos no se puede construir mientras no se sepa qué debe decir. Es redacción, no decisión: `ADR-020` §2 |

**Criterio de terminado.** Un capturador trabaja **toda la jornada sin red** con sus permisos
evaluados localmente (`CN-23`), y la matriz distingue quién administra de quién autoriza una
corrección de producción.

> `[!]` **`ESC-28` y `CN-23` se contradicen y hay que llevarlo al cliente.** Cierre de sesión a los 15
> minutos de inactividad contra sesión válida toda la jornada: quince minutos en pleno invernadero es
> lo contrario de lo que la finca necesita. Va en `BR-N5`. Y el número **no sale de ninguna fuente**:
> está marcado como medida inventada en `ADR-020` §4.

---

### `T6` · Cierre de producción

**Qué construye.** El **evento de dominio del cierre**: quién lo dispara —el administrador,
declarando la actividad productiva terminada—, con autor y fecha, qué habilita y qué consolida. Y lo
que ese cierre produce: **el documento consolidado por producción**, con el estado final por campo,
sus secciones, fechas y lote, que **no vuelve a cambiar nunca**.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-020` §1 (la frontera) · `ADR-024` §6 (el documento consolidado) · `ADR-022` (conservar 5 años, escalada diferida) · `ADR-004` (append-only con horizonte) |
| **Requisitos** | `RF-016` |
| **Escenarios** | `ESC-08`, `ESC-12`, `ESC-40`, `ESC-41`, `ESC-42`, `ESC-43`, `ESC-58`, `ESC-62` |
| **Precondiciones** | `T1`. **Y definir el evento**, que es lo único que falta y es pequeño |

**Por qué esta tanda importa más de lo que parece.** El cierre de producción **es la pieza pequeña con
más cosas colgando de todo el proyecto**: es la frontera semántica de la retención, la frontera entre
el conjunto vivo y la acumulación, el punto donde materializar los agregados cuesta cero, el momento
de consolidar el documento por producción, y **el disparador de la escalada de `ADR-022` cuando
llegue**. Hoy **no mueve un solo byte** — y se construye igual, porque inventarla después sobre datos
ya escritos es mucho más caro.

**Las otras dos costuras de `ADR-022`, que van en esta misma tanda porque son baratas hoy y caras
dentro de cinco años:** la **política de retención como dato del catálogo** y no como código, y la
**medición de volumen y de uso desde el primer día**.

**Criterio de terminado.** Cerrada una producción, su estado es **el último valor conocido por campo**
y la corrección deja de ser una operación normal; **las correcciones intermedias siguen ahí**, sin
archivar ni purgar (`ADR-022`); y existe una serie de volumen y uso desde el día uno con la que
decidir, dentro de unos años, si la escalada se activa.

---

### `T7` · Consulta e histórico

**Qué construye.** Los **modelos de lectura** —que bajo el modelo de eventos de `ADR-024` **no son
opcionales**: una pantalla de veinte campos es un pivote de veinte filas, y **ninguna pantalla se
construye sobre la tabla de eventos**—, la **agregación materializada en el cierre**, la **carga
progresiva por categoría**, y la BI propia sobre la misma base de la empresa.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-023` (agregación y carga progresiva) · `ADR-010` (BI propio sobre modelos de lectura) · `ADR-022` |
| **Bloques** | `BB-04` archivo e histórico · `BB-03` |
| **Requisitos** | `RF-018`, `RF-016` |
| **Escenarios** | `ESC-12`, `ESC-39`, `ESC-40`, `ESC-41`, `ESC-51`, `ESC-60`, `ESC-62` |
| **Precondiciones** | `T6` — sin cierre no hay dónde materializar barato |
| **Se cruza con** | `SPK-06`: con volumen realista y **sin degradar el dato**, la historia de una cama en ≤5 s y la de un lote en ≤10 s. **Si ni particionando responde, es el disparador de reapertura de `ADR-022`** |

**Criterio de terminado.** `ESC-62` completo: la historia de un lote *«sembrado en varias camas y
secciones»* se recorre y responde en tiempo, **con el dato entero**, no con una versión degradada.

> `[!]` **La retención sigue con dos respuestas y la contesta el cliente.** `A3` dijo **2 años** de
> búsqueda rápida; los escenarios dicen **5 en línea**. Y falta saber **hasta dónde llega su
> obligación de auditoría de certificación**. `ESC-12` está marcado como medida sin fuente.

---

### `T8` · Observabilidad y operación

**Qué construye.** Los **dos planos que no se pueden mezclar**: el de la empresa —el administrador ve
su finca con identificadores: qué camas faltan, de qué dispositivo, desde cuándo— y el de la
plataforma —el operador ve **solo salud y conteos**—. Más la actualización de aplicación y catálogo
sin recoger dispositivos, y el procedimiento de restauración desde la copia de custodia.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-026` (los dos planos) · `ADR-015` (actualizar sin recoger dispositivos) · `ADR-012` (clave por empresa + custodia fuera de línea) · `ADR-014` (marcar, no bloquear) · **`ADR-031` (el desfase medido, y su umbral `PENDIENTE`)** |
| **Bloques** | `BB-13` observabilidad · `BB-07` CDN · `BB-12` custodia de claves · `BB-10` notificación |
| **Requisitos** | `RF-020`, `RF-021` |
| **Escenarios** | `ESC-03`, `ESC-17`, `ESC-19`, `ESC-20`, `ESC-25`, `ESC-30`, `ESC-31`, `ESC-32`, `ESC-53` |
| **Precondiciones** | `T4` — la señal de `ADR-026` es la ausencia de sesión, y las sesiones nacen en `T4` |
| **Se cruza con** | `SPK-07` (restaurar una empresa desde cero con su nodo perdido) y `SPK-08` (detectar desviación de reloj >5 min **sin bloquear** al usuario en pleno campo) |

**Criterio de terminado.** **Construir un solo plano rompería `ESC-30` sin que nadie se diera
cuenta**: el criterio es que la telemetría de plataforma **no contenga un solo dato de negocio**
(`CN-34`) y que aun así el administrador de la finca vea su lista de camas pendientes con nombre y
apellido.

**Precondición de redacción, de `ADR-020` §2:** `RF-021` y `CN-25` pasan de **bloquear** ante reloj
alterado a **marcar y exigir confirmación**. Un bloqueo sin salida en pleno campo es peor que el
desfase, y el propio `CN-25` ya lo advertía. **`ESC-17` está EN CONFLICTO hasta que se reescriban.**

---

### `T9` · Interoperabilidad y exportación

**Qué construye.** La exportación a Excel y PDF **con las restricciones de pantalla aplicadas**, y la
lectura directa desde la BI del cliente contra su propia instalación — que es lo que el cliente pidió
**por su nombre**, Power BI.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-013` (exportación + lectura desde la BI del cliente) · `ADR-010` |
| **Requisitos** | `RF-019` |
| **Escenarios** | `ESC-29`, `ESC-51` |
| **Precondiciones** | `T7` |

**Criterio de terminado.** Un usuario exporta solo lo que su rol ve en pantalla, y una herramienta de
BI del cliente lee contra su instalación **sin abrir una puerta que cruce la frontera de empresa**
(`RF-012`, `M5`).

> **Nota de estado:** `DEC-06`, `CN-10`, `CN-14` y `CN-33` están **derogadas por `B5`**. Si alguien
> encuentra una observación que diga *«la decisión vigente excluye integrarse con Power BI»*, es de
> antes de la ronda 3 y hay que limpiarla (`ADR-020` §3, `ESC-29`).

---

### `T10` · Proyección — 🔴 **BLOQUEADA**

**Qué construiría.** El motor de proyección, la regeneración semanal conservando la versión anterior,
la erradicación con recálculo, la desviación real contra proyectado y la causa de una caída.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-005` (proyecciones inmutables con foto de parámetros) · `ADR-029` (una sola versión) |
| **Requisitos** | `RF-006`, `RF-008`, `RF-009`, `RF-011`, `FR-023`, `FR-024` |
| **Escenarios** | `ESC-05`, `ESC-09`, `ESC-10`, `ESC-45`, `ESC-63` |
| **Bloqueo** | **`BR-23`**: falta el **% de productividad esperada por variedad** y la **curva de reparto de tallos sobre los ~7 días de corte**. Del cliente / ingeniero agrónomo. **Sin fecha** |

**Por qué no se empieza «un poco».** `RF-006`, `RF-008` y `RF-011` **no son implementables** sin esos
dos números, y `ESC-05`, `ESC-09` y `ESC-10` solo se pueden verificar en su **mecánica** —versionado,
latencia, inmutabilidad—, nunca en su **resultado**. Poner un número plausible mientras tanto es
exactamente lo que la regla de honestidad prohíbe.

**Lo que sí se puede construir hoy sin tocar el bloqueo:** el **almacén de versiones inmutables** —una
proyección publicada guarda corte de datos, versión de configuración, versión del motor y fecha, y no
se recalcula sobre sí misma—. Eso es mecánica pura, se verifica sin los dos números, y es lo que hace
que la desviación signifique algo el día que lleguen. **Es la parte de `T10` que no está bloqueada.**

**Y esto es lo que hay que perseguir:** `BR-23` es **el bloqueo real del producto**. Sin él FlorLogic
captura muy bien y **no proyecta**, que es para lo que se compró.

---

### `T11` · Asistente de captura — 🔴 **BLOQUEADA**

**Qué construiría.** El asistente local de `CN-31`: **vocabulario restringido al catálogo de la
finca, propone, el sistema valida, el usuario confirma, nunca escritura silenciosa**. Inferencia **en
el dispositivo**; entrenamiento **en el nodo**, por lotes y fuera de jornada; y el artefacto de
personalización viajando como **una sección más del paquete de configuración**. La voz es
transcripción a un campo: sin almacenar audio y sin interpretación de lenguaje natural.

| | |
|---|---|
| **ADR que la sostienen** | `ADR-030` (dónde corre) · `ADR-018` (no se compromete hasta medirlo) · `ADR-029` (cómo se distribuye) |
| **Escenarios** | `ESC-15`, `ESC-26`, `ESC-27`, `ESC-32`, `ESC-37`, `ESC-56` |
| **Bloqueo** | **La compuerta 1.** `SPK-01` decide: si un formulario optimizado alcanza las medidas, **el asistente se cancela**; si queda >30% por encima, entra al alcance **con costo declarado** |

**Lo que `ADR-030` ya dejó resuelto aunque la tanda esté bloqueada:** **el nodo no lleva hardware de
inferencia**, así que **el precio de instalación no se mueve** y se puede cotizar sin esperar a la
compuerta. Eso era lo que había que decidir; lo demás depende de una medición de tres días.

> **Prioridad baja, y conviene que se note.** El asistente y la voz se acordaron como valor agregado
> —*que sea posible vende mejor el producto, aunque las funciones acaben sin usarse*—. **No compite
> con los drivers.**

---

## 3. Lo que hay que llevarle al cliente, y qué tanda desbloquea cada cosa

| Qué se pregunta | Desbloquea |
|---|---|
| **`BR-23`** — % de productividad por variedad y curva de reparto del corte | **`T10` entera.** Es el bloqueo real del producto |
| **`D1`** — el proceso de captura, a fondo. **Pide una sesión, no un dato.** Arrastra la granularidad de captura y el umbral del recordatorio | Afina `T3` y `T4`; confirma la clave de hecho de `T1` |
| **`BR-N4`** — ¿dos personas capturan la misma cama el mismo día? | Confirma el diseño de `T1` y `T4` (ya resuelto por diseño; falta saber si ocurre) |
| **`BR-N5`** — ventana de sesión offline y umbral del recordatorio | `T5` y el escalado de `T4` |
| **`BR-22`** — ¿9 variedades o ~300? | Dimensionamiento del catálogo de `T1` |
| **La retención** — `A3` dijo 2 años; los escenarios dicen 5. Y hasta dónde llega su obligación de auditoría de certificación | `T6` y `T7` |
| **La unidad de producción** — ¿es el ciclo sobre una sección? | Confirma el supuesto de `T1` |
| **`D5`** / **`CN-20`** — qué es el sistema heredado de ~300 tablas | **Qué es físicamente el nodo de la finca**, que es lo último que queda de `ADR-021` |
| **`D7`, `D8`, `D9`** — las ~20 columnas comunes del catálogo | Contenido de `T1`. **No bloquean la construcción**: entran como filas |
| **Las siete decisiones tomadas en su contra** (`A5`, `A8`, `A10`, `A11`, `B12`, `B13`, `C2`) | Nada técnico. **No se aplican en silencio** |

---

## 4. Lo que queda de trabajo propio, y no es construcción

1. **La reescritura de `ADR-020` §2, §3 y §4.** Es **redacción sobre decisiones ya tomadas**, se puede
   hacer en una tarde y **desbloquea `T3`, `T5` y `T8`**. Concretamente: `RF-017`, `RF-021`+`CN-25`,
   `RF-001`/`RF-002`; quitar «motivo y autorización» de `ESC-08` y `ESC-58`, «pide resolución» de
   `ESC-34`, el «≤5 min» de `ESC-46`; cambiar el «0 registros perdidos» de `ESC-54`; precisar el «0
   datos eliminados» de `ESC-43` y el «≤5 s» de `ESC-12`; limpiar las observaciones de `ESC-16` y
   `ESC-29`; y **marcar `PENDIENTE` las seis medidas inventadas**.
2. **Definir el evento de cierre de producción.** Es lo único que le falta a `T6`, y es lo que más
   cosas tiene colgando.
3. **Ejecutar `SPK-01` y `SPK-05`.** Cinco días entre los dos, **desbloquean siete escenarios** y
   deciden la compuerta 1.
4. **Reconciliar los tres rankings de atributos** antes de usarlos para negociar cualquier trade-off
   con el cliente.
5. **Poner al día el modelo C4** (`FlorLogic-C4.archimate`): **todavía no refleja `ADR-024` a
   `ADR-031`**.
6. **Arreglar `PoC-0` antes de medir sobre él**: las cuatro contradicciones de la tabla de `ADR-027`.
   Tres son de una línea; la cuarta —el alta del dispositivo contra el servidor— entra con `T5`.

---

## 5. Dónde mirar cuando haga falta el detalle

| Si necesitas… | Está en |
|---|---|
| **El porqué de una decisión de arquitectura** | `docs/03-arquitectura/FlorLogic-alternativa-de-solucion-y-ADR.md` §5 — las 31 ADR |
| **Si un escenario se cumple y qué le falta** | Ídem, §7 — la tabla completa de los 65, con veredicto |
| **Qué mide cada spike y qué lo mata** | Ídem, §6.3, y las tres compuertas en §6.4 |
| **El estado de una decisión de negocio** | `Documentacion/Archivo/Recopilacion/3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md` |
| **El porqué de una decisión de negocio y qué se descartó** | `.../3_ANEXO_RONDAS_DE_DECISION.md` — las 50 entradas cerradas, íntegras |
| **La cita literal del cliente que respalda un hecho** | `.../1_VOZ_DEL_CLIENTE.md` — `H-01`..`H-49` y las brechas `BR-nn` |
| **Requisitos, restricciones, atributos y los 65 escenarios** | `Documentacion/Drivers-Arquitectonicos/DRIVERS_ARQUITECTONICOS.md` y sus cuatro Excel |
| **Qué hay construido ya** | `app-captura/` — es `PoC-0`, y su `README.md` |

**Y la regla que evita el error más caro de este proyecto:** si un documento contradice a otro, gana
**el más reciente guardado en el repositorio**, y **la contradicción se acumula en un ADR** — no en
una lista de pendientes dentro de un acta, porque esas se pierden.
