# FlorLogic — Plan de construcción

> **v1.0 · 31-ago-2026 · PROPUESTA DEL EQUIPO.** Primer documento del proyecto que habla de
> **solución** y no de levantamiento. Nada de lo que hay aquí está validado con el cliente.
>
> **Qué es:** la descomposición del sistema en **bloques de construcción**, qué corre en cada
> dispositivo, y el **contrato** entre bloques. Es lo que permite repartir trabajo entre Juan y
> Jerónimo sin que cada uno construya un sistema distinto.
>
> **Qué NO es:** no decide tecnologías (§6 dice cuáles son las tres decisiones tecnológicas reales y
> qué las dispara), no reemplaza a `DRIVERS_ARQUITECTONICOS.md` y no cierra ninguna brecha.

**Qué manda sobre qué.** Contexto general: `CONTEXTO.md`. Estado de una decisión:
`Recopilacion/3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md` (v7.0). Drivers y medidas:
`DRIVERS_ARQUITECTONICOS.md` (v1.0, 26-ago). Escenarios: `Mini QAW FlorLogic - Escenarios de
Calidad.xlsx`. **Este archivo no decide nada de negocio: traduce lo ya decidido a estructura.**

`[!]` **`CONTEXTO.md` v4.0 es del 25-ago y quedó por detrás.** Sigue describiendo el SaaS
multi-tenant de `DEC-01` como vigente y dice que no existe ningún `ESC-nnn`. Las dos cosas son
falsas: `DEC-01` está derogada desde la ronda 3 y hay 65 escenarios escritos. **Mientras no se
actualice, para el modelo de entrega manda `DRIVERS_ARQUITECTONICOS.md §1.1`.**

**Marcas.** `[!]` problema abierto o contradicción · **PROP** supuesto del equipo, no validado ·
**DURO** respaldado por evidencia o por decisión cerrada · **BLOQUEADO** no se puede construir hasta
que se cierre lo que se indica.

---

## 1 · Las seis reglas de partición

**No se reparte el sistema por pantallas ni por módulos de negocio: se reparte por estas seis
reglas.** Cada una sale de una restricción ya cerrada, y entre las seis generan toda la estructura
de §2 y §3. Si alguna se cae, la descomposición hay que rehacerla.

### `R1` · Lo que decide si un dato entra, se decide en el dispositivo — **DURO**

En el invernadero no hay red (`CN-17`). Validar al sincronizar convierte un error de diez segundos en
un error de ocho días. Por eso `CN-13` es la restricción rectora y `CN-22` exige que **todas** las
reglas duras sean evaluables en el dispositivo, con el motivo del rechazo en el momento
(`RF-004`, `RF-005`, `ESC-02`, `ESC-56`).

> **Consecuencia estructural:** el motor de validación es **un solo bloque desplegado en dos sitios**,
> y las reglas son **un artefacto de datos versionado**, no código. `ESC-57` pide **0% de divergencia
> entre la validación local y la del servidor con la misma versión de reglas** — eso es imposible de
> sostener con dos implementaciones escritas por separado.

### `R2` · La autoridad del dato es el nodo de finca; la nube no es autoridad de nada operativo — **DURO**

`CN-37` (local-first) y `B6`: el sistema se instala en la infraestructura de cada empresa y opera sin
internet sobre su información activa. `RF-003` ya no dice «repositorio central»: dice **la instalación
de la empresa**. La nube presta respaldo, actualización, soporte e IA (`CN-32`: la IA **jamás** es
dependencia de la captura).

> **Consecuencia estructural:** ningún camino crítico —capturar, validar, sincronizar, proyectar,
> consultar— puede pasar por internet. Si un bloque necesita la nube para funcionar, está mal ubicado.

### `R3` · Un cambio de negocio no puede ser un cambio de esquema — **DURO**

`CN-36`, que sale de cruzar `C4` y `C6`: **los campos capturados son DATOS, no COLUMNAS.** Añadir un
tipo de labor, de medición o de agrupación no puede obligar a migrar el esquema **en N instalaciones
dentro de casa de clientes**, que es lo más caro que le puede pasar al modelo de entrega.

> **Consecuencia estructural:** plantilla común amplia de la que cada empresa **activa** el
> subconjunto que usa (`A14`, `RF-013`); las tres agrupaciones —densidad por m², número de líneas,
> unidades por tallo— conviven como parámetros y no como tres modelos (`C6`, `RF-006`).
> **Esto hay que fijarlo antes de la primera tabla.**

### `R4` · Una proyección publicada es inmutable y arrastra los parámetros con que se calculó — **DURO**

`CN-27` y `RF-023`, respaldados por el cliente («si no se modifica sí»). Una proyección emitida es la
base sobre la que ya se comprometió flor: cambiarle los parámetros por debajo es alterar el pasado
(`ESC-09`, `ESC-24`, `ESC-45`). `ESC-44` lo extiende al catálogo: cambiar la tabla de grados **no
puede reinterpretar la historia**.

> **Consecuencia estructural:** el motor de proyección **no lee la tabla viva de parámetros**: lee un
> snapshot versionado. Y `RF-011` (desviación real contra proyectado) solo significa algo si la
> proyección contra la que se mide no se movió.

### `R5` · El estado es el último valor conocido por campo; la unidad de traza es la sesión — **DURO, y con una amputación consciente**

`RF-016` reescrito por `B8`, `A15` y `A1`: para cada campo de una cama o sección se conserva **su
último valor conocido con la fecha en que se capturó**; por **sesión de sincronización** se conserva
quién sincronizó, desde qué dispositivo y qué entró. **No se conserva el valor anterior a una
corrección, ni las versiones intermedias, ni se exige motivo escrito.** Y `RF-022` encaja con eso:
gana el registro más reciente, sin mediación humana, porque **el estado *es* el último valor
conocido**.

> **Consecuencia estructural:** ni event sourcing completo ni sobrescritura ciega. El almacén guarda
> por campo la terna `(valor · fecha de captura · sesión)`. La resolución de conflictos deja de ser
> un algoritmo y pasa a ser una propiedad del modelo de datos.
>
> `[!]` **Lo que se pierde a conciencia, y hay que decirlo en voz alta:** no se podrá reconstruir qué
> decía un dato antes de una corrección. Es la mitad amputada de *Capacidad para ser Auditado*, que es
> el driver #4. **Seis escenarios ya escritos contradicen esta regla — ver §9.**

### `R6` · Lo que sale de la finca sale cifrado con la llave del cliente — **DURO**

`CN-28`, cerrada por `B4`: cifrado en tránsito y en reposo, respaldos incluidos, **con la llave del
lado del cliente**. `CN-34`: el operador de la plataforma opera la capa de servicios en línea sobre
datos que llegan cifrados, y quien administra la instalación es el ingeniero de sistemas de la finca
(`ESC-50`: 0 accesos a datos de negocio en operación normal).

> **Consecuencia estructural:** los bloques de nube se diseñan **sin poder leer** lo que custodian.
> Eso descarta de entrada cualquier servicio en línea que necesite entender el contenido — y obliga a
> pensar dos veces la IA analítica (`BC-24`).
>
> `[!]` `A5` decidió **cifrar el dispositivo aunque el cliente dijo que no**. Es una de las siete
> decisiones tomadas en contra suya y hay que llevarla a la sesión, no aplicarla en silencio.

---

## 2 · Topología: cuatro lugares, tres de ellos con estado

```
   ┌─────────────────────────┐        ┌──────────────────────────────────┐
   │  N1 · DISPOSITIVO       │        │  N4 · SERVICIOS EN LÍNEA (nube)  │
   │  3 capturadores         │        │  la mensualidad · 100-200 USD    │
   │  ESTADO: sí (autoridad  │        │  ESTADO: sí, pero CIFRADO y      │
   │  temporal de lo no      │        │  OPACO (R6). Nunca autoridad.    │
   │  sincronizado)          │        └──────────────┬───────────────────┘
   └───────────┬─────────────┘                       │
               │  CT-03 sincronización               │  CT-06 versiones
               │  CT-01 catálogo                     │  respaldo · soporte · IA
               ▼                                     ▼
   ┌───────────────────────────────────────────────────────────────────┐
   │  N2 · NODO DE FINCA — LA INSTALACIÓN. ES EL PRODUCTO.             │
   │  Autoridad de todo el dato operativo (R2). Opera sin internet.    │
   │  Una instalación por empresa · una base por empresa (CN-16)       │
   └───────────────────────────────┬───────────────────────────────────┘
                                   │  CT-05 salida de datos
                                   ▼
   ┌───────────────────────────────────────────────────────────────────┐
   │  N3 · PUESTO DE CONSULTA — navegador. ESTADO: NINGUNO.            │
   │  ~12 usuarios + ~20 que solo consultan · CN-18 doble canal        │
   └───────────────────────────────────────────────────────────────────┘
```

**Por qué N3 no es un nodo con estado.** `CN-18` obliga a doble canal —app de captura y web de
consulta— pero la web **no captura**: `RFP-03` (vista geométrica) entra solo en la consulta, nunca en
la captura (`B11`). Un puesto de consulta que guardara estado propio sería una tercera copia del dato
sin ninguna necesidad que la justifique.

**Por qué el dispositivo es autoridad temporal y no una caché.** Entre la captura y la sincronización
el único sitio donde ese dato existe es el teléfono, y `CN-15` exige **pérdida CERO**. Mientras un
registro esté en la cola de salida, el dispositivo **es** la fuente de verdad de ese registro
(`ESC-01`, `ESC-11`, `ESC-18`, `ESC-59`).

`[!]` **El agujero que esto deja abierto, y que sigue sin salida:** si el dispositivo se destruye con
registros sin sincronizar, hay pérdida — y pérdida cero es objetivo duro. `A4` lo cerró a medias (el
nodo local es la segunda copia **una vez sincronizado**). **El dispositivo destruido antes de
sincronizar sigue siendo pérdida total.** Es `BR-N5` y no lo resuelve la arquitectura sola: lo
resuelve una política de sincronización mínima obligatoria, y eso se le pregunta al cliente.

---

## 3 · Los bloques de construcción

Cada bloque dice **qué hace**, **qué explícitamente no hace**, de dónde sale y qué lo bloquea. Los
artefactos de los 65 escenarios ya escritos se usaron como punto de partida: **si un escenario nombra
un artefacto, ese artefacto tiene que ser un bloque o vivir dentro de uno.**

### 3.1 · N1 — Dispositivo de campo

| ID | Bloque | Qué hace | Qué NO hace |
|---|---|---|---|
| **`BC-01`** | **Almacén local y cola de salida** | Guarda cada captura en el dispositivo en el momento en que se confirma, y mantiene la cola de lo pendiente de entregar con su estado y sus reintentos | No decide si el dato es válido (eso es `BC-02`) ni resuelve conflictos (eso es `BC-10`) |
| **`BC-02`** | **Motor de reglas — instancia dispositivo** | Evalúa el catálogo de reglas contra lo capturado y devuelve rechazo o aviso **con el motivo en lenguaje del negocio** | No trae las reglas: las recibe de `BC-03`. **No tiene reglas escritas en el código** |
| **`BC-03`** | **Catálogo local versionado** | Custodia la parametrización vigente de la empresa —camas, secciones, variedades, grados, agrupaciones, unidades y reglas— y **sabe qué versión tiene** | No la edita. El catálogo se edita en `BC-13` y baja por `CT-01` |
| **`BC-04`** | **Captura** | Presenta la plantilla activa de la empresa y recoge el dato. Dos plantillas desde el principio: `novedad_siembra` y `programa_siembra` | No valida (llama a `BC-02`) ni guarda (llama a `BC-01`) |
| **`BC-05`** | **Identidad y permisos sin conexión** | Autentica y aplica los permisos del rol en el dispositivo durante toda la jornada, sin red | No administra usuarios: eso es `BC-18` |
| **`BC-06`** | **Fecha de negocio** | Propone la `FECHA` del evento desde el reloj del dispositivo, detecta alteración y permite fijar explícitamente una fecha anterior para captura retroactiva | No guarda marcas de tiempo técnicas por dato — `A1` las eliminó |
| **`BC-07`** | **Cliente de sincronización** | Entrega la cola a `BC-10` en segundo plano, de forma idempotente y reanudable, y refleja el estado de cada registro | No decide quién gana un conflicto: lo hace `BC-10` y aquí solo se muestra el resultado |
| **`BC-08`** | **Asistente de captura** · **PROP** | Propone valores con vocabulario restringido al catálogo de la finca. **Propone, el sistema valida, el usuario confirma. Nunca escritura silenciosa** | No captura por él. No es un asistente abierto |
| **`BC-09`** | **Actualización de app y catálogo** | Aplica versiones nuevas de aplicación, catálogo y reglas sin recoger los dispositivos uno por uno | No actualiza si hay registros pendientes sin entregar |

**De dónde sale cada uno.** `BC-01` ← `RF-003`, `CN-15`, `ESC-01/11/18/35`. `BC-02` ← `RF-004`,
`RF-005`, `CN-22`, `ESC-02/56/57`. `BC-03` ← `RF-020`, `CN-26`, `ESC-65/07`. `BC-04` ← `RF-001`,
`RF-002`, `RF-009`, §4.3 de `CONTEXTO.md`. `BC-05` ← `RF-014`, `CN-23`, `ESC-49`. `BC-06` ← `RF-021`,
`CN-25`, `ESC-17`. `BC-07` ← `RF-003`, `CN-24`, `ESC-38/47/59`. `BC-08` ← `RFP-05`, `CN-31`, `C2`.
`BC-09` ← `ESC-25`, `CN-29`.

**Estado real hoy:** `BC-01`, `BC-02`, `BC-03`, `BC-04` y `BC-07` **ya existen en versión demo** en
`app-captura/` (`almacen.ts`, `reglas.ts`, `catalogo.ts`, `ui/`, `sincronizacion.ts`), con 9 reglas
en `configuracion/reglas.v1.json` y seis suites de prueba en verde. `BC-05`, `BC-06`, `BC-08` y
`BC-09` no existen.

`[!]` **Advertencia sobre la demo.** Se construyó contra las hipótesis de Juan, y el cliente las
contradijo en 94 de 262 preguntas. Lo que sobrevive por decisión explícita (`§11.1 R2`) es **el
modelo de datos, el catálogo de reglas y el contrato de sincronización** — que es exactamente lo que
este plan reutiliza. **La variante «guiada», el cronómetro y el escaneo QR respondían a exigencias que
el cliente no pidió** (`B2`, `B3`); el escaneo vuelve a entrar solo si `ESC-27` sobrevive a la sesión.

### 3.2 · N2 — Nodo de finca

**Es el producto.** Lo que la empresa compra por ~20.000 USD es esto instalado y funcionando.

| ID | Bloque | Qué hace | Qué NO hace |
|---|---|---|---|
| **`BC-10`** | **Ingesta y consolidación** | Recibe sesiones de sincronización, descarta duplicados por identidad de registro, aplica **el más reciente gana** y avisa a quien capturó cuando su registro fue descartado o modificado | No pide a nadie que medie. `B7` cerró que no hay mediación humana |
| **`BC-11`** | **Almacén de la empresa** | Guarda la estructura Empresa-Finca-Bloque-Nave-Cama-Sección y, por campo, la terna último valor / fecha / sesión (`R5`) | No guarda versiones intermedias ni el valor anterior a una corrección |
| **`BC-12`** | **Motor de reglas — instancia servidor** | **El mismo bloque que `BC-02`**, con la misma versión de reglas, ejecutado del lado del nodo | No tiene reglas propias ni «reglas adicionales de servidor». Si diverge, `ESC-57` falla |
| **`BC-13`** | **Catálogo y parametrización versionados** | Donde el administrador de la empresa define variedades, densidades, % de productividad, grados, días a corte, duración del corte, bandas y **qué columnas de la plantilla usa** | No permite diseñar formatos libres. Se **activan** columnas de una plantilla común (`A14`) |
| **`BC-14`** | **Bitácora de sesiones y eventos** | Registro append-only de sesiones de sincronización, correcciones, cambios de parámetros y accesos excepcionales | No guarda el detalle por dato: la unidad es la **sesión** (`A1`) |
| **`BC-15`** | **Motor de proyección** | Calcula tallos proyectados por sección, agrega a cama, reparte sobre los días de corte y **publica versiones inmutables** con su snapshot de parámetros | No recalcula proyecciones ya publicadas (`R4`) |
| **`BC-16`** | **Consulta y tableros** | Proyectado contra real por día, semana y mes desde el mismo cálculo; desviación contra la banda; vista geométrica de camas; qué está sin sincronizar; avance del día por bloque | No es un diseñador de reportes |
| **`BC-17`** | **Salida de datos** | Exporta a Excel y PDF y expone una interfaz de lectura para herramientas externas, autenticada y limitada a esa empresa | **No abre la base de datos.** `ESC-29`: 0 accesos directos |
| **`BC-18`** | **Consola de administración** | Altas y bajas de usuario, permisos, parámetros, estado del sistema — desde un computador de la finca y sin herramientas técnicas | No permite al administrador modificar información de producción (`ESC-06`) |
| **`BC-19`** | **Respaldo y restauración** | Respalda automáticamente, cifra con la llave del cliente y **prueba la restauración** | No manda las copias a la nube en claro (`R6`) |
| **`BC-20`** | **Estado de dispositivos** | Qué dispositivo sincronizó, cuándo, cuánto lleva sin hacerlo, y avisa al superar el umbral | No fuerza la sincronización: eso es una orden que sale de `BC-18` |
| **`BC-26`** | **Carga inicial e histórico** · **BLOQUEADO por `CN-20`** | Trae al sistema lo que la empresa ya tiene, para que `CN-07` (≤7 días de puesta en marcha) sea alcanzable | — |

**De dónde sale cada uno.** `BC-10` ← `RF-003`, `RF-022`, `CN-24`, `ESC-34/38`. `BC-11` ← `RF-016`,
`DEC-14`, `ESC-64`. `BC-12` ← `CN-22`, `ESC-57`. `BC-13` ← `RF-013`, `RF-023`, `CN-26`,
`ESC-23/24/44`. `BC-14` ← `RF-016`, `RF-017`, `ESC-40/12`. `BC-15` ← `RF-006`, `RF-007`, `RF-008`,
`ESC-05/09/45`. `BC-16` ← `RF-011`, `RF-018`, `RF-024`, `RFP-03`, `B12`, `ESC-39/60`. `BC-17` ←
`RF-019`, `CN-14`, `B5`, `ESC-29/51`. `BC-18` ← `RF-013`, `RF-017`, `ESC-13/14/22/53`. `BC-19` ←
`CN-15`, `CN-28`, `ESC-03/19`. `BC-20` ← `ESC-20/31`. `BC-26` ← `CN-20`, `CN-07`.

### 3.3 · N4 — Servicios en línea

**Es la mensualidad.** Todo lo de aquí puede caerse una semana sin que la finca deje de operar — y si
eso no es cierto de un bloque, el bloque está mal ubicado (`R2`).

| ID | Bloque | Qué hace |
|---|---|---|
| **`BC-21`** | **Custodia de respaldos** | Guarda las copias que sube `BC-19`, **sin poder leerlas** |
| **`BC-22`** | **Distribución de versiones** | Publica el paquete de versión que `BC-09` y el nodo aplican. Es el mecanismo que hace viable `CN-29` con N instalaciones ajenas |
| **`BC-23`** | **Soporte remoto** | Diagnóstico sin desplazarse a la finca: registros de operación, telemetría y estado de sincronización |
| **`BC-24`** | **IA analítica** · **PROP** | Consultas y análisis sobre la información de esa empresa. **Jamás dependencia de la captura** (`CN-32`) |
| **`BC-25`** | **Recuperación de acceso** | Restablecer credenciales cuando el dispositivo no puede resolverlo solo (`A4`) |

`[!]` **`BC-24` choca de frente con `R6`.** Un servicio de nube que no puede leer lo que custodia no
puede analizarlo. O la IA analítica corre **en el nodo de finca** —y entonces no es un servicio de
nube, es parte del producto y encarece la instalación—, o la empresa entrega su información en claro
a la nube —y entonces `CN-28` y `CN-03` se debilitan—. **Esta contradicción no está registrada en
ningún archivo del proyecto todavía.** No la resuelve este documento.

`[!]` **`BC-23` tiene el mismo problema, más pequeño.** Diagnosticar sin desplazarse exige ver algo.
Hay que definir qué telemetría sale de la finca y comprobar que no contiene información de negocio.

---

## 4 · Los contratos

**Un contrato es lo que un bloque promete a otro y no puede romper sin avisar.** Son seis y son lo
único que Juan y Jerónimo tienen que acordar antes de escribir código por separado.

| ID | Entre | Qué fija | Por qué existe |
|---|---|---|---|
| **`CT-01`** | `BC-13` → `BC-03` | El catálogo baja **completo**, identificado por versión, y el dispositivo sabe cuál tiene. Sin catálogo vigente **no se captura** | `RF-020`, `CN-26`, `A9`. `ESC-65`: descarga completa en 5 minutos o menos |
| **`CT-02`** | `BC-02` ≡ `BC-12` | **El mismo artefacto de reglas versionado**, evaluado por la misma implementación en los dos lados. Una regla nueva es un archivo nuevo, **nunca un despliegue** | `CN-22`, `ESC-07` (0 despliegues), `ESC-57` (0% de divergencia) |
| **`CT-03`** | `BC-07` → `BC-10` | Sesión de sincronización: identidad de registro estable, entrega **idempotente**, reanudable a la mitad, el más reciente gana, y aviso de vuelta a quien capturó | `RF-003`, `RF-022`, `CN-24`, `ESC-09/34/38` |
| **`CT-04`** | `BC-13` → `BC-15` | El motor lee un **snapshot inmutable** de parámetros, no la tabla viva. Toda proyección publicada queda amarrada a la versión con la que se calculó | `CN-27`, `RF-023`, `R4`, `ESC-45` |
| **`CT-05`** | `BC-17` → exterior | Salida autenticada, limitada a la empresa del solicitante, **sin acceso directo a la base**. Es la única puerta hacia afuera, exportes incluidos | `RF-012`, `RF-019`, `CN-14`, `ESC-29` |
| **`CT-06`** | `BC-22` → nodo y dispositivos | Paquete de versión: **el mismo paquete se instala en nube y en sitio, sin cambios de código** | `ESC-16`, `CN-29`, `CN-07` |

> **`CT-02` es el contrato que más barato es de fijar hoy y más caro de arreglar después.** Ya existe
> en la demo (`reglas.v1.json` + `reglas.ts`) y es lo único del prototipo que `§11.1 R2` mandó
> conservar. Si `BC-12` se escribe de cero en otro lenguaje, `ESC-57` es inalcanzable desde el día uno.

---

## 5 · Las cinco decisiones que hay que tomar antes de la primera tabla

**No son preguntas al cliente: son decisiones del equipo, y las cinco encarecen enormemente si se
toman después de tener N instalaciones desplegadas.**

| # | Decisión | Por qué ahora | Estado |
|---|---|---|---|
| **1** | **Cómo se representa «campo capturado» para que sea DATO y no COLUMNA** | `CN-36`. Es la restricción de arquitectura más importante de todo el trabajo de depuración, y `C4` la dejó explícitamente sin respuesta | **Abierta. La más urgente** |
| **2** | **Cómo se identifica un registro** para que `CT-03` sea idempotente entre dispositivos sin conexión | Sin identidad estable no hay «entregar exactamente una vez». La demo usa UUID v7; **funciona, pero nadie lo ha ratificado** | Propuesta viva en `app-captura/id.ts` |
| **3** | **Qué es exactamente una sesión de sincronización** | Es la **unidad de traza** de todo el sistema (`A1`, `R5`). Si se define mal, `RF-016` y `RF-017` quedan sin sujeto | Abierta |
| **4** | **Cómo se versionan catálogo, reglas y parámetros** — ¿tres versionados o uno? | `CT-01`, `CT-02` y `CT-04` los tratan por separado, pero `ESC-44` (un grado nuevo no reinterpreta la historia) sugiere que **el catálogo necesita el mismo tratamiento inmutable que los parámetros** | Abierta |
| **5** | **Dónde corre el modelo de IA** — nodo de finca o dispositivo | `C2` lo dejó abierto y **afecta al precio de instalación** de `B6`, que es el número que se le pone al cliente | Abierta |

---

## 6 · Los tres puntos donde la tecnología sí es decisión de arquitectura

Todo lo demás es implementación. Estos tres cambian la forma del sistema, así que van con su criterio
y con **el disparador que los decide** — no con una elección tomada hoy.

### `T1` · La app de campo: PWA o nativa

| | PWA | Nativa |
|---|---|---|
| A favor | Ya existe y funciona, cero dependencias, actualización trivial (`ESC-25`), un solo código para `CN-18` | Cifrado en reposo demostrable, control real del almacenamiento, iOS sin sorpresas |
| En contra | El cifrado en reposo con WebCrypto **deja la llave en IndexedDB y no se puede demostrar**; iOS desaloja IndexedDB tras ~7 días sin abrir la app; la detección de reloj alterado es heurística | Hay que construirla; Juan exploró Flutter pero no lo domina; dos canales que mantener |

**Qué la dispara, y en este orden:**

1. **`A5` — se decidió cifrar el dispositivo.** `[!]` **Ese solo hecho tensiona la PWA**, porque el
   límite conocido es justo ese. Es una decisión del equipo tomada en contra del cliente: si en la
   sesión se cae, la PWA recupera terreno.
2. **`CN-21` — qué dispositivo se usa.** `B10` dice que lo pone la empresa, pero **nadie sabe qué
   celulares tienen hoy los tres capturadores.** EN DUDA.
3. **iOS.** `ESC-32` fija Android, y **Apple no aparece entre los 65 escenarios del libro vigente**
   —aunque `DRIVERS §8.1` sí lista la pregunta de Apple con 2 puntos (ver §9.2)—. Si Apple es real,
   los límites de la PWA en iOS dejan de ser teóricos; si no lo es, este disparador se apaga.

`[!]` **La condición que antes mataba a la PWA ya no aplica.** `PLAN_DEMO_CAPTURA §4.4` decía que una
ventana offline de días la descartaba; `B1` retiró ese veredicto al entender que **los quince días son
captura retroactiva de papeles que aparecen tarde, no dispositivo apagado quince días.** Quien lea
`CONTEXTO.md §15.2` va a encontrar lo contrario: **está desactualizado.**

### `T2` · Qué es, físicamente, el nodo de finca

Servidor que la empresa ya tiene · equipo que se entrega dentro de los 20.000 USD · máquina virtual
en la infraestructura del cliente. **Lo dispara `CN-20`** —hasta no ver el sistema heredado de ~300
tablas no se sabe con qué convive— y **`CN-02`**, porque si el nodo es hardware que entregamos, sale
del presupuesto de construcción. La decisión 5 de §5 lo condiciona: una IA que corra en la finca
necesita otro tipo de máquina.

### `T3` · La forma del almacén

No es «qué motor de base de datos»: es si el modelo de `R3` y `R5` —campos como datos, último valor
por campo con su sesión— se representa de forma relacional estricta, mixta o documental. **Lo dispara
la decisión 1 de §5**, y solo esa. Elegir motor antes de resolver esa forma es elegir al revés.

---

## 7 · Orden de construcción

**El criterio no es la prioridad de negocio: es cuánto cuesta equivocarse.** Primero lo que es caro
de cambiar y ya tiene evidencia dura; al final lo que es barato de cambiar o depende del cliente.

### Tanda 0 — antes de escribir la primera tabla · **se puede empezar hoy**

Las cinco decisiones de §5 y los seis contratos de §4, escritos. **No es código: es un documento y un
esquema.** Sale casi entero de lo que la demo ya probó (`modelo.ts`, `reglas.v1.json`,
`sincronizacion.ts`) más `CN-36`. Sin esta tanda, las tres siguientes se hacen dos veces.

### Tanda 1 — la línea «papel → dato confiable en la finca» · **se puede empezar hoy**

`BC-01` · `BC-02` · `BC-03` · `BC-04` · `BC-07` · `BC-10` · `BC-11` · `BC-12` · `BC-13` · `BC-14`

Es Confiabilidad y Disponibilidad, los drivers #1 y #2, y es **exactamente la frase del cliente**:
*«CERTEZA DE LOS DATOS, QUE SE INGRESEN LOS DATOS CORRECTAMENTE»*. Cinco de estos diez ya existen en
versión demo. **Ninguno depende de una respuesta pendiente del cliente**, con una salvedad: el
catálogo de reglas duras que `CN-22` exige **no está levantado** (`D1`), así que `BC-02`/`BC-12`
arrancan con las nueve reglas de `reglas.v1.json` y crecen — que es precisamente para lo que sirve
`CT-02`.

### Tanda 2 — el motor y lo que se enseña · **parcialmente bloqueada**

`BC-15` · `BC-16` · `BC-17` · `BC-18` · `BC-20`

`[!]` **`BC-15` está BLOQUEADO por `D3`:** de dónde sale el % de productividad por variedad, y cómo se
reparten los tallos sobre los ~7 días de corte. Sin eso, `RF-006` calcula un total y `RF-007` no
existe — y el gerente de producción consume proyección **diaria**. **Es el último hueco del motor y
es una pregunta de sesión, no un dato que se derive.**

`BC-16`, `BC-17`, `BC-18` y `BC-20` **no están bloqueados** y pueden ir en paralelo con la tanda 1.
`BC-16` incluye los dos tableros de `B12` —qué está sin sincronizar y avance del día por bloque—, que
son además **el instrumento con el que se mide Rendimiento** (`B2`): latencia de captura a proyección,
contra la línea base de 8 días.

### Tanda 3 — el servicio

`BC-19` · `BC-21` · `BC-22` · `BC-23` · `BC-25` · `BC-09`

Es lo que justifica la mensualidad. `BC-22` es la pieza que hace viable `CN-29` con N instalaciones
ajenas: *pagas, te actualizas*. `[!]` **Y el riesgo escrito de `E2` sigue en pie:** la instalación que
deja de pagar **diverge de versión**, que es justo lo que `CN-29` teme.

### Tanda 4 — la diferenciación

`BC-08` · `BC-24`

El asistente de captura es lo que `C2` decidió que se le entrega a la empresa para que se quede con
ello, y se justifica **por calidad del dato, no por velocidad** (`B2`). `BC-24` no debería empezarse
hasta resolver el choque con `R6` que se señala en §3.3.

### Y fuera de las tandas

`BC-26` (carga inicial) **BLOQUEADO por `CN-20`**, que es el bloqueante técnico número uno y solo el
cliente lo cierra. `[!]` **Es el que puede reventar `CN-07`** —siete días de puesta en marcha— porque
migrar información de un sistema que nadie ha visto no se estima.

---

## 8 · Lo que este plan NO construye

Precios, ventas y rendimiento económico (`DEC-07`) · cruce con pedidos y clientes · Florverde y
cualquier formato específico de certificación (`A13`, `CN-38`: la verificación de cumplimiento **no
se automatiza**) · gestión de personal e insumos · poscosecha —el alcance termina en el corte— ·
registro obligatorio de actividades culturales · datos climáticos · consumo o reemplazo de la app de
plagas (`CN-19`) · almacenamiento de fotografías y documentos escaneados · plantillas de captura
libremente diseñables (solo activación de columnas, `A14`).

---

## 9 · Hallazgo: los 65 escenarios están escritos contra el estado anterior de decisiones

**Esto sale de cruzar el xlsx de hoy con `DRIVERS_ARQUITECTONICOS.md` del 26-ago, y afecta a lo que se
va a construir.** Los 65 escenarios se redactaron sobre `CONTEXTO.md` v4.0 (25-ago), que **no
incorpora las rondas 3 a 5**. Seis de ellos contradicen una funcionalidad o restricción vigente, y
dos de sus observaciones citan decisiones ya derogadas.

### 9.0 · Los seis que van contra una decisión vigente

| Escenario | Lo que dice | Contra qué va |
|---|---|---|
| **`ESC-08`** | «conserva el valor original y el motivo» · «100% de las correcciones con valor anterior y autor registrados» | **`RF-016` reescrito por `B8`/`A15`:** no se conserva el valor anterior ni se exige motivo. `R5` |
| **`ESC-58`** | «0 valores sobrescritos sin historia» · «100% de las correcciones con autor, motivo y autorización» | **`RF-016` y `RF-017`:** `B8` quitó el motivo escrito y `A11`/`C9` quitaron la aprobación registrada |
| **`ESC-34`** | «conserva ambas versiones y pide resolución antes de consolidar» · «0 registros descartados automáticamente» | **`RF-022` reescrito por `B7`:** resolución automática, gana el más reciente, **sin mediación humana** |
| **`ESC-17`** | «marca el registro y exige confirmación», explícitamente **para no bloquear** | **`RF-021` y `CN-25`:** bloqueo ante reloj alterado. La observación del escenario reconoce el cambio |
| **`ESC-16`** | Observación: «Contradice la decisión vigente de SaaS multi-tenant» | **`DEC-01` está derogada.** El escenario **ya no contradice nada**: describe el modelo vigente |
| **`ESC-29`** | Observación: «la decisión vigente excluye integrarse con Power BI» | **`B5` derogó `DEC-06`, `CN-10` y `CN-33`.** El cliente pidió Power BI por su nombre |

### 9.1 · Medidas que no salen de ninguna fuente

La necesidad de cada escenario sí está en el Top 65; **el número con el que se mide, en varios casos,
no está en las medidas firmes de `DRIVERS §9.1` ni sale de un escalón respondido.** La regla del
proyecto es explícita: *un escenario sin medida se marca `PENDIENTE`; no se completa con un número
inventado.*

| Escenario | Número puesto | De dónde debería salir |
|---|---|---|
| `ESC-26` | 10 toques · 60 s por cama · 15 min/día | De ningún sitio. La pregunta del Top 65 es «reducir al mínimo los toques», sin cifra |
| `ESC-28` | cierre de sesión a los 15 min de inactividad | La pregunta existe («que la sesión se cierre sola»), **el número no** |
| `ESC-36` · `ESC-37` | 200 ms · 300 ms | «sin espera perceptible», sin cifra |
| `ESC-15` | contraste 4,5:1 · 48 dp | «legible bajo el sol directo», sin cifra |
| `ESC-21` · `ESC-61` | 20% de degradación máxima | Sin cifra en ninguna fuente |
| `ESC-12` | 5 segundos sobre 5 años | `A3` fijó **2 años** de búsqueda rápida y demora escalonada después |

`[!]` **`ESC-26` es el más delicado:** fija un tope de segundos por cama cuando `B2` cerró que **el
cliente NO pide velocidad de captura** —dijo NO a los cinco escalones— y que Rendimiento se mide en
**latencia de captura a proyección**, no en segundos por cama. `[!]` **`ESC-28` tensiona `CN-23`**, que
exige sesión válida durante toda la jornada; quince minutos de inactividad en pleno invernadero es
justo lo contrario.

### 9.2 · El Top 65 del libro y el Top 65 de `DRIVERS §8.1` no son la misma lista

**21 de las 65 preguntas difieren entre las dos.** Un tercio. `DRIVERS §8.1` dice de sí mismo que es
*«tabla generada desde el archivo, no transcrita a mano»*, así que la explicación no es un error de
copia: **el Top 65 se rehizo en el libro después del 26-ago.** La nota metodológica de la hoja lo
confirma — la del libro añade como criterio *«evidencia textual directa del cliente»* citando
`CITAS_TEXTUALES_CLIENTE.md` y `HOJA_SESION_CLIENTE.md`, dos archivos que están en `_to_delete/`.

Ejemplos de lo que entró y salió: **desaparecen** la pregunta de Apple (`Portabilidad`), «¿se necesita
saber quién capturó cada dato?» (`Auditado`) y «que la información de una empresa nunca pueda ser vista
desde otra» (`Seguridad`); **entran** el aviso de captura duplicada, la corrección antes de sincronizar
y la historia de un lote.

`[!]` **Que «saber quién capturó cada dato» haya salido del Top 65 no es menor:** era la pregunta con
la que `A1` justificó la traza por sesión, y es el sustento de `RF-016`. **Hay que decidir cuál de las
dos listas manda antes de seguir**, porque `DRIVERS_ARQUITECTONICOS.md` dice que el Top 65 es *«el
insumo directo de los escenarios»* y hoy apunta a una lista que ya no existe.

> **Qué hacer con esto, y es una decisión de Juan, no de este documento.** O se corrigen los seis
> escenarios y se marcan `PENDIENTE` las medidas inventadas —la regla del proyecto es que **un
> escenario sin medida no se completa con un número inventado**—, o se acepta que los escenarios
> reabren `RF-016`, `RF-017`, `RF-021` y `RF-022`, y entonces `R5` de este plan cambia y con ella el
> modelo de datos de `BC-11`. **Lo que no se puede es construir sobre las dos versiones a la vez:
> `BC-11` y `BC-14` se diseñan distinto según cuál gane.**

---

## 10 · Lo que este plan asume y que podría tumbarlo

| # | Supuesto | Marca | Qué lo tumbaría |
|---|---|---|---|
| 1 | El modelo de entrega sigue siendo local-first con servicios en línea | **DURO** (`B6`, ronda 3) | Que el cliente rechace pagar 20.000 USD por instalación |
| 2 | El nodo de finca puede existir dentro del presupuesto | **PROP** | `CN-02` y `T2`. Nadie ha costeado el nodo |
| 3 | Las reglas duras se pueden expresar como datos | **PROP** | `D1`: el catálogo de reglas nunca se levantó con el cliente. Si aparecen reglas que exigen cálculo complejo, `CT-02` se complica |
| 4 | El dispositivo destruido antes de sincronizar es un riesgo aceptado | **PROP · en contra de `CN-15`** | Es `BR-N5`. Pérdida cero es objetivo duro y esto lo incumple |
| 5 | Dos personas no capturan la misma cama el mismo día | **PROP** | `BR-N4`, **nunca preguntada**. Si pasa, `BC-10` necesita bastante más de lo que `CT-03` promete |
| 6 | «Lote» se relaciona con cama y sección de forma estable | **PROP** | `ESC-62`: el cliente lo marcó con `*`. **Nadie sabe qué es un lote en esta finca** |
| 7 | Una cama se divide entre variedades, no entre flores distintas | **PROP** | Duda abierta de S4. Dos flores distintas tienen procesos y fechas de desbotone distintos |
| 8 | Una sola estructura de captura sirve para toda la finca | **PROP · contra la evidencia** | §4.3: hay **dos formatos reales** distintos, pero el cliente pidió estructura única. `A16` |

---

## 11 · Qué se hace ahora, en orden

1. **Ratificar o corregir las seis reglas de partición de §1.** Son media hora de lectura y condicionan
   todo lo demás.
2. **Resolver §9.** Tres cosas distintas y las tres bloquean: los **seis escenarios** que van contra
   una decisión vigente (bloquean el modelo de datos de `BC-11` y `BC-14`), las **medidas inventadas**
   de §9.1, y **cuál de los dos Top 65 manda** (§9.2).
3. **Tomar las cinco decisiones de §5.** Ninguna depende del cliente.
4. **Escribir los seis contratos de §4** con el detalle suficiente para que Juan y Jerónimo trabajen
   por separado.
5. **Actualizar `CONTEXTO.md` a v5.0.** Hoy dice que `DEC-01` está vigente y que no existe ningún
   escenario. Las dos cosas son falsas y cualquiera que lo lea empieza mal.
6. **Llevar a la sesión con el cliente lo que sigue abierto:** `D1` (proceso de captura y reglas
   duras) · `D2` (documentos) · `D3` (% de productividad y curva de reparto) · `CN-20` (sistema
   heredado) · `CN-21` (dispositivos) · `BR-N4` (dos capturadores en la misma cama) · qué es un lote ·
   **y las siete decisiones tomadas en contra suya**, que no se aplican en silencio.

---

*Plan de construcción v1.0 · propuesta del equipo, sin validar con el cliente · 31-ago-2026.*
