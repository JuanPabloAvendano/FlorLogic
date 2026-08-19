# FlorLogic — Bitácora de decisiones cerradas

Registro incremental de las decisiones que se cierran en cada tanda de depuración del contexto.
Cada decisión tiene ID estable `DEC-nn`, fecha, quién decidió y qué desbloqueó.
**Los IDs no se reutilizan.** Si una decisión se revierte, se marca `REVERTIDA` y se conserva la fila.

Este archivo es la fuente de la que se refundirá `0_CONTEXTO v4` cuando terminen las tandas.
El modelo `docs/03-arquitectura/FlorLogic-modelo.archimate` refleja cada decisión en la
documentación de los elementos afectados.

---

## Tanda 1 — 15-ago-2026 · decisiones internas del equipo

Decididas por Juan Pablo Avendaño y Jerónimo Montoya. Confianza `EQ` salvo donde se indique:
**ninguna de estas seis está validada con el cliente.**

### `DEC-01` · FlorLogic es un SaaS multi-tenant

Se descartan on-premise, PaaS y desarrollo a medida. Motivos declarados: hacerlo más accesible
para el cliente y ganar flexibilidad para adaptarse a otras fincas.

La plataforma la **opera el equipo FlorLogic** cuando el producto se lance de forma completa.
El conocimiento de negocio sobre el que se construye proviene de **dos empresas del sector, de
mediano y gran tamaño**.

**Cierra:** la contradicción SaaS vs. PaaS que arrastraba desde el 12-ago.

**Desbloqueó:**

| Elemento | Antes | Ahora |
|---|---|---|
| `Empresa / Tenant` | EN DUDA | **Raíz de la jerarquía.** Empresa → Finca(s) → Bloques → Naves → Camas → Variedades |
| `RF-012` aislamiento entre empresas | EN DUDA | DENTRO, estructural. Base legal: `CN-03` |
| `RF-013` parametrización por empresa | condicionado | DENTRO, corazón del modelo de datos |
| `Plataforma de despliegue` | bloqueada | **Plataforma SaaS multi-tenant**, modelable |
| Rol `Administrador del sistema` | uno solo | **se parte en dos** (ver abajo) |
| `CN-11` PayU | sin sentido | tiene sentido: hace falta cobrar suscripción |
| `CN-12` RBAC | pendiente | DENTRO: cada permiso se evalúa contra el par (rol, empresa) |

**Partición del rol de administrador:**

- **Administrador de la empresa (tenant)** — vive dentro de la finca cliente. Parametriza,
  otorga permisos a los suyos, autoriza correcciones, elige la política de conflictos.
- **Operador de la plataforma (equipo FlorLogic)** — altas de empresas, disponibilidad,
  despliegues, soporte.

> `[!]` **Problema abierto que esta decisión creó.** `RF-017` dice que solo el administrador puede
> modificar un registro ya sincronizado. Si eso incluyera al operador de la plataforma, el equipo
> FlorLogic estaría tocando datos productivos de un cliente. Es un problema contractual, no técnico.
> Hay que decidir si el operador tiene acceso a datos de tenant, y con qué mecanismo auditado.

> `[!]` **Riesgo que esta decisión agravó.** Construir para varias empresas sobre la evidencia
> disponible es un salto mayor que construir a medida. Pendiente de precisar si las dos empresas
> son dos fuentes entrevistadas o un mismo entrevistado hablando de dos empresas que conoce.

---

### `DEC-02` · Primero una finca piloto, después el producto multi-tenant

El objetivo de la primera entrega es que el software **funcione en al menos una empresa concreta**.
El multi-tenant se construye desde el diseño, pero la validación en campo es de una sola finca.

**Consecuencias registradas:**

- `CN-01` (mayo 2027, después de la temporada alta de marzo-abril) aplica al **piloto**, no al
  lanzamiento del producto. Igual `CN-07` (el despliegue no puede retrasar la operación más de 7 días).
- Se crean dos paquetes de trabajo en el modelo: **Fase 1 · Piloto en una finca** (DENTRO) y
  **Fase 2 · Lanzamiento SaaS multi-tenant** (EN DUDA, sin fecha).
- `RF-012` hay que construirlo aunque durante el piloto no se pueda probar con dos tenants reales.
  Necesita pruebas sintéticas.
- El módulo de **gestión de empresas y suscripciones** no hace falta para el piloto, sí para la fase 2.

**Presupuesto.** `CN-02` (~20.000 USD ≈ 61 M COP) proviene de la partida que la empresa destina a
producción y recolección de datos antes de iniciar un cultivo. Es el presupuesto de **construcción y
puesta en marcha**; el pitch de ~10 USD/usuario/mes es el ingreso recurrente posterior. No compiten:
son dos momentos distintos. Queda registrado que `CN-05` —qué se cobra, por qué unidad y qué
incluye— sigue sin definir.

---

### `DEC-03` · Los rankings de atributos de calidad previos quedan descartados

Los cuatro ejercicios existentes —`0_CONTEXTO_v3.md` §6, mini QAW, ranking de arquitectos y mapa
de empatía— se hicieron bajo dudas iniciales de arquitectura y **no se usan como fuente**. Quedan
como histórico. Se elaborará una versión nueva enfocada al modelo SaaS.

**Efecto en el modelo:** los 14 atributos pasan de `DENTRO` a `EN DUDA`. Su documentación conserva
las cuatro posiciones antiguas marcadas explícitamente como histórico. **Hasta que exista el
ranking nuevo, ningún atributo tiene prioridad válida y no se pueden calcular trade-offs.**

**Insumos que la re-elaboración debería incorporar y que las listas viejas no tenían:**

- Aislamiento entre empresas competidoras (`RF-012`).
- Pico de temporada **simultáneo** para todos los tenants: en floricultura es de calendario
  (San Valentín, Día de la Madre), así que el multi-tenant apila la carga en vez de suavizarla (`H-41`).
- Operación de la plataforma por el propio equipo FlorLogic, no por un ingeniero de sistemas en planta.
- `DEC-06`: BI propio en lugar de integración, que **baja Interoperatividad**.

**Escenarios de calidad.** Confirmado: no están planteados. Lo que existe son sugerencias y ejemplos
de referencia para conversar con el cliente, no información validada. Quedan bloqueados en cascada
hasta que exista el ranking nuevo.

---

### `DEC-04` · El catálogo vigente es `FuncionalidadesSignificativas.xlsx`

«Funcionalidades significativas» = las funcionalidades **críticas para el funcionamiento mínimo del
negocio**, acotadas al tiempo disponible de desarrollo. Es el catálogo sobre el que se trabaja todo
el proyecto. **No existen más requisitos validados.**

`5_RF_CRITICOS_v1.xlsx` (10-ago) y `6_FUNCIONALIDADES_CRITICAS_v1.xlsx` (12-ago) quedan como histórico.

**Pendiente de forma:** `FR-023` y `FR-024` deberían escribirse `RF-`.

> `[!]` **Cuatro requisitos quedaron fuera del catálogo.** Se marcan `FUERA-CATALOGO` en el modelo,
> que no es lo mismo que fuera de alcance: son requisitos reales cuya ausencia tiene consecuencias.
> Ver la pregunta 2 de la tanda 1-bis.

---

### `DEC-05` · Conflictos de sincronización: automático por defecto, mediación como opción

Resolución por **orden cronológico estricto** como comportamiento por defecto, con registro de
cambios consultable. La **mediación humana existe como opción configurable por empresa**, no como
el camino principal. La automatización es lo que se construye primero.

**Cierra** la contradicción entre `RF-C04` (resolución humana obligatoria) y `RF-022` (orden
cronológico automático): conviven como una opción de configuración.

**Consecuencia:** `RF-013` deja de ser solo parámetros del motor y pasa a incluir también
**políticas de comportamiento** por empresa.

**Principio nuevo `PR-09`:** *automatizar primero, permitir mediación humana como opción.* Aplica a
casos análogos, no solo a la sincronización.

> `[!]` No confundir con `PR-01` («el asistente propone, el sistema valida, el usuario confirma»).
> `PR-01` manda sobre la **interpretación** de un dato que se está capturando; `PR-09` manda sobre la
> **resolución de conflictos** entre datos ya capturados. No se solapan.

> `[!]` Sigue condicionado por `BR-N4`: nadie ha preguntado al cliente si el caso ocurre alguna vez.
> Si no ocurre, todo esto es esfuerzo evitable.

---

### `DEC-06` · BI propio y cerrado, antes que integración con terceros

Se construye un **modelo de BI específico y cerrado** sobre la información que el negocio considera
importante, en lugar de conectar herramientas externas. Razón del equipo: es más eficiente que
integrar y permite controlar qué se muestra.

Referencias de diseño declaradas: plantillas públicas de BI que hagan algo similar, y la forma en
que las empresas ya usan sus reportes actuales.

**Efecto:**

- Nuevo componente **Módulo de BI y tableros propios**, sustituto funcional de los reportes que hoy
  produce PowerBI (`H-36`): siembra, producción, plagas y enfermedades, inventario de material
  vegetal, pérdida de flor, estimados de flor.
- `CN-10` se cierra: **PowerBI no es una restricción técnica impuesta en fase 1.** Sigue existiendo
  en la finca, pero integrarse con él queda fuera. Conectar otras tecnologías se considera posible,
  no prioritario y no crítico.
- Nueva restricción adoptada `CN-14`: BI propio y cerrado antes que integración.
- **Interoperatividad baja de prioridad** en el ejercicio de atributos pendiente.
- Encaja con la decisión de la sesión 3 de no crear roles por tablero: gerencia, planeación y ventas
  ven todo el espectro y filtran con su propio panel; compartir hacia afuera es exportar (`RF-019`).

> `[!]` El alcance del BI no está acotado. «Lo que el negocio considere importante» todavía no es
> una lista, y es la clase de requisito que crece sin límite si no se cierra pronto.

---

---

## Tanda 1-bis — 15-ago-2026 · aclaraciones de la misma tanda

### `DEC-07` · El dominio excluye precios y rendimiento económico

**La decisión de alcance más grande de la tanda.** Precios, unidades comerciales y en general la
información relacionada con ventas quedan **fuera del dominio** de FlorLogic. El dominio son
**cantidades, unidades y calidades**. La proyección está enfocada al **estado resultante de
producción del cultivo**, no al rendimiento económico que esa producción supone.

**Qué cambió en el modelo:**

| Elemento | Antes | Ahora |
|---|---|---|
| `Proyectar ventas` (proceso) | proyectaba «ventas» | **`Proyectar disponibilidad de flor`** — qué flor habrá, cuánta, de qué variedad, qué grado y qué fecha |
| `Proyección de producción y ventas` (servicio) | — | **`Proyección de producción y disponibilidad`** |
| `Precio de venta` | DENTRO, único dato restringido | **FUERA-F1**, fuera del dominio |
| `RF-015` ocultar precio al supervisor | fuera del catálogo | **sin objeto**: no hay precio que ocultar |
| `CN-12` RBAC | controlaba visibilidad de campos | controla **capacidades**, no visibilidad. La única frontera de visibilidad es la de EMPRESA |
| `RF-019` exportar | punto de fuga de precios | ya no lo es |

> El nombre viejo era engañoso: «proyección de ventas» hacía pensar en dinero. Lo que el sistema
> produce es **oferta disponible**, y ese es el enunciado que hay que usar de aquí en adelante,
> también frente al cliente.

> `[!]` Punto a vigilar. El **costo de producción** no es información de ventas, y los supervisores
> ya lo llegan a conocer hoy. Si algún día entra al sistema, esta decisión hay que revisarla.

> `[!]` El driver «8% de las ventas cubiertas con compra a terceros» sigue siendo válido como
> motivación, pero su valor en dinero deja de ser algo que el sistema pueda calcular.

---

### `DEC-08` · Destino de los cuatro requisitos huérfanos

| Requisito | Destino | Por qué |
|---|---|---|
| **RF-007** repartir tallos sobre los días de corte | **Entra, pero NO como funcionalidad significativa** | Es una dependencia del negocio, no algo indispensable para que el producto exista. Se construye; no compite por prioridad con las 19 significativas |
| **RF-010** baja de producción | **Absorbido por RF-009** | «Erradicación parcial o total» ya cubre el descuento parcial de una cama |
| **RF-015** ocultar precio de venta | **Sin objeto** | Ver `DEC-07`: no hay precios en el sistema |
| **RF-C19** vista de calidad de datos | **Absorbido por RF-016** | RF-016 ya conserva la información; ahora también la muestra |

**Consecuencias de redacción que hay que aplicar al catálogo:**

- **RF-009** pasa a cubrir explícitamente el descuento parcial expresado en porcentaje o en tallos.
  Al fusionar, conservar la diferencia operativa: la baja parcial se registra en campo por formato y
  es mucho más frecuente; la erradicación total la decide el ingeniero de producción o el gerente de
  ventas (`H-19`).
- **RF-016** pasa de «conservar» a **«conservar y mostrar»**: debe exponer al auditor los registros
  marcados como erróneos, en conflicto o pendientes, con su antigüedad y filtrables por bloque y por
  capturador. Si no se redacta así, la meta de llevar el error del 2% al 0% vuelve a quedarse sin
  pantalla.

Las cuatro fichas se conservan en el modelo por trazabilidad, marcadas con su destino. **No se
reutilizan sus identificadores.**

---

### `DEC-09` · El operador de la plataforma tiene acceso de infraestructura, no funcional

Los administradores de la plataforma SaaS acceden a la información **de manera indirecta**: pueden
operar sobre los datos en la nube para dar soporte —copias de seguridad, restauración,
disponibilidad, integridad— pero **no como usuarios funcionales del negocio de un tenant**.
`RF-017` se refiere siempre al administrador de la **empresa**, nunca al operador.

> `[!]` **Tarea que esta decisión deja abierta, y no es menor.** Una copia de seguridad *contiene*
> los datos del tenant. «Acceso indirecto» solo es real si los respaldos van cifrados con una clave
> que el operador no pueda usar para leer contenido de negocio, y si toda operación sobre ellos
> queda auditada. Contra `CN-03` (secreto empresarial) esto es también una cláusula de contrato, no
> solo una decisión técnica.

---

### `DEC-10` · Alcance inicial del BI: los seis reportes actuales

El módulo de BI cubre, como línea base, los seis reportes que la finca ya consume hoy vía PowerBI
(`H-36`):

1. Siembra
2. Producción
3. Plagas y enfermedades
4. Inventario de material vegetal
5. Pérdida de flor
6. Estimados de flor

Se expandirá cuando se defina con el cliente qué información es realmente relevante.

Tomar los seis existentes como línea base es defendible: es lo que el negocio ya usa, no una
hipótesis del equipo.

> `[!]` **Dos de los seis no tienen de dónde alimentarse.** *Plagas y enfermedades* tiene su propia
> app offline-first en la finca (`H-34`): hay que decidir si FlorLogic la reemplaza, la consume, o
> el reporte se queda sin datos. *Inventario de material vegetal* no tiene ningún requisito del
> catálogo que lo alimente — los esquejes se registran al sembrar (`RF-001`), pero eso no es un
> inventario. Queda como Assessment abierto.

---

### Precisión sobre el riesgo de la fuente única

Confirmado: es **una única persona entrevistada**, con **30 años de experiencia en el proceso sobre
el que se enfoca el proyecto**, y con conocimiento directo de **dos empresas** cuyas prácticas son
las que sigue el resto del sector.

Eso no elimina el riesgo `§9.2`, lo reencuadra:

- **Lo que mitiga:** la profundidad y la representatividad del informante son mucho mayores de lo que
  sugiere «una sola voz». Las prácticas descritas no son idiosincrásicas de una finca, lo que
  sostiene mejor la apuesta multi-tenant de `DEC-01`.
- **Lo que no mitiga:** sigue habiendo un solo punto de vista. Planeación —donde nace la
  proyección— nunca se exploró. Y todo lo que el informante da por obvio es invisible para el
  equipo: un experto de treinta años es exactamente el perfil que omite lo que para él es evidente.

La mitigación realista no es entrevistar a más gente —el cliente lo descartó— sino **contrastar
contra documentos reales (`BR-N3`) y validar con un prototipo**.

---

## Tanda 2 — 15-ago-2026 · arquitectura de datos, alcance del BI y captura

### `DEC-11` · Una base de datos por empresa, con esquema común

**Estrategia objetivo:** mismo esquema para todas las empresas, **base de datos independiente para cada
una**. Motivo declarado: es la opción más vendible y la que mejor sostiene la promesa de que la
información de cada empresa está cuidada y no se comparte.

**Ubicación de los datos: indiferente.** No hace falta que estén en el país ni cerca. El criterio es la
opción **más barata y segura** del mercado, siempre que sea accesible.

> `[!]` **Sujeta a implementación.** La elección final depende de qué opción dé el mejor resultado en un
> plazo adecuado y con costo medido, factible para la fecha de entrega. Fallback aceptable si el costo
> o el tiempo no dan: **esquema por empresa**. Fallback **no** aceptable: tabla compartida con columna
> discriminadora, que rompería la promesa de independencia.

**Lo que gana:** restaurar el respaldo de un solo cliente sin tocar a los demás es trivial — que es
justo lo que exigen `DEC-12` y el soporte de `DEC-09`.

**Lo que cuesta:** cada migración de esquema hay que aplicarla a N bases. Sin automatización desde el
primer día, los esquemas divergen entre clientes y el «esquema común» deja de ser cierto.

---

### `DEC-12` · Objetivos de continuidad, diferenciados por tipo de fallo

Cuatro números. La distinción entre ellos importa más que cada uno por separado:

| Situación | Tolerancia |
|---|---|
| **Pérdida de información** | **CERO.** Objetivo duro, sin excepción |
| **Fallo de funcionamiento** (no se puede operar, no sincroniza) | **1 hora** |
| **Reparación o carga de respaldo** | **1 día** |
| **Desajuste de datos** (las proyecciones no cuadran con lo real) | Mayor, pero sin pérdida |

La tolerancia de 1 hora coincide con lo que el cliente ya había dicho en S2: aguantan ~1 hora caídos,
4 horas es demasiado.

**El offline-first (`CN-13`) es el mitigante natural** de esa hora: si la plataforma cae, la captura en
campo sigue funcionando en el dispositivo y sincroniza después. Convierte una caída de plataforma en un
retraso, no en una parada.

> Estos son **los primeros números medidos y diferenciados del proyecto**. Sirven directamente como
> insumo de escenarios de calidad, y son mejores que cualquier cifra genérica de disponibilidad,
> porque distinguen entre "el sistema no responde" y "el sistema responde con datos que no cuadran".

Queda como `CN-15` en el modelo.

---

### `DEC-13` · La app de plagas no se toca; la enfermedad entra como motivo

En el primer lanzamiento FlorLogic **no reemplaza ni consume** la app de plagas y enfermedades que la
finca ya tiene. Razón declarada: la usan roles de la empresa que **no están contemplados** entre los
tres roles de FlorLogic, y el alcance y el tiempo no dan.

Lo que sí entra: poder indicar **enfermedad como motivo** por el que la producción disminuye. Es el
puente mínimo entre los dos mundos sin construir un módulo fitosanitario.

Esto cierra el alcance del BI (`DEC-10`), porque los dos reportes que no tenían fuente ya la tienen:

- **Plagas y enfermedades** → se alimenta de los motivos de disminución de producción.
- **Inventario de material vegetal** → se resuelve contando por cama y sección (`DEC-14`), no por esqueje.

> `[!]` **Residuo honesto.** Con motivos de disminución **no** se reconstruye el reporte fitosanitario
> completo que la finca ve hoy. El reporte de FlorLogic responderá *"cuánta producción se perdió por
> enfermedad"*, no *"qué plagas hay y dónde"*. Conviene decirlo así al cliente para que no espere lo
> segundo.

---

### `DEC-14` · La cama es la unidad de inventario, y se divide en secciones

**Nada se cuenta por esqueje.** No se registra un número exacto de esquejes plantados: se usa un
**aproximado calculado desde el área cultivada y la densidad por m²**. Es más sencillo y más entendible
para el usuario final, y evita pedir un conteo que nadie hace en campo.

**La cama es la unidad principal de inventario. Pero una cama se divide en SECCIONES**, y cada sección
tiene su propia área en m², su propia variedad y su propia densidad.

> Ejemplo real dado por el equipo: una cama con **30% pompón morado y 70% violeta azulada**, porque
> 6 m² se cultivaron de violeta azulada con una densidad de 6 tallos/m².

Esto **refina `H-06`** («normalmente una variedad por cama; puede haber dos mezcladas»). La mezcla deja
de ser una excepción a manejar y pasa a ser una estructura a modelar.

**Consecuencia para el motor de proyección** — la fórmula cambia de nivel:

```
plantas_sección = área_sección_m2 × densidad_siembra(variedad de esa sección)
plantas_cama    = suma de las plantas de todas sus secciones
```

La **sección** es donde vive el dato de siembra. La **cama** es el agregado y la unidad de inventario.
Es el cambio de modelo de datos más grande hasta ahora.

---

### `DEC-15` · Dos lecturas de la producción, y una vista geométrica

La información de producción se presenta de **dos formas simultáneas**:

1. **Tallos aproximados** — el resultado del motor.
2. **Porcentaje de plantas estimadas reales** frente al 100% que representa lo que se cultivó en la cama.

La segunda existe precisamente porque el número de plantas es un aproximado: **el porcentaje no exige
confiar en un conteo que nadie hizo**.

Sobre esa segunda lectura se construye una **vista geométrica**: las camas como rectángulos o formas,
cada una mostrando en porcentaje cuánta producción sigue creciendo, según el porcentaje de pérdida
acumulado que cambia con la información admitida.

> Esto **recupera con justificación** la idea de "mapa de calor de camas" que apareció en el pitch de S1
> y nunca entró a ningún catálogo. Antes era una pantalla bonita sin dato detrás; ahora tiene uno.

---

### `DEC-16` · La IA vuelve, partida en dos

| Pieza | Dónde corre | Alcance |
|---|---|---|
| **Asistente de captura** | Local, **sin conexión** | Una sola tarea: ayudar a llenar el formulario, acomodando los datos que el usuario da y recordándole qué falta |
| **IA analítica** | **En la nube** | Consultas, análisis de datos, apoyo al BI. Requiere conexión, no participa en la captura |

Justificación declarada: rendimiento, velocidad y facilidad para quien recolecta datos; más la petición
del cliente de usarla para proponer estrategias.

**No es el asistente abierto que se descartó en la sesión 3.** Aquel era lenguaje natural sin límites;
este es un ayudante de formulario con alcance cerrado, y sigue rigiendo `PR-01`: propone, el sistema
valida, el usuario confirma. Nunca escritura silenciosa.

**Plantillas configurables** (`RFP-07`): el administrador de la empresa podría quitar campos, añadir
otros y marcar preguntas como obligatorias u opcionales. Es la "capacidad de ser administrado" que el
equipo persigue, pero **explícitamente fuera de la primera entrega**: el piloto se hace con formularios
adaptados a ese entorno de producción concreto.

> `[!]` **Confianza en disputa, y conviene resolverlo antes de presentarlo.** La sesión 3 (11-ago)
> registró explícitamente que *"la IA embebida es decisión de desarrolladores, no requisito del cliente"*
> y que *"fue idea de venta"*. Ahora se afirma lo contrario. Hasta que haya una **cita textual** del
> cliente pidiéndola, esto es `PROP`, no `CONF`. Presentarlo como requisito del cliente sin cita
> repetiría el error que el propio equipo corrigió en agosto.

> `[!]` **La mitad analítica no tiene alcance.** "Proponer estrategias" puede significar cualquier cosa
> y hoy no es estimable.

> `[!]` **Choque con `RF-012`.** Una IA que consulta datos de una empresa no puede arrastrar contexto de
> otra. El aislamiento aplica también al modelo, a sus prompts y a cualquier índice o caché construido
> sobre los datos. Es el punto más fácil de romper el aislamiento sin darse cuenta.

---

### Lo que la tanda 2 dejó abierto

**`BR-N6` — el proceso de captura a detalle no se ha definido con el cliente.** Es la brecha nueva más
importante, y es distinta del resto: las demás piden un *dato*; esta pide una *sesión de trabajo* sobre
el proceso. Bloquea `RFP-01` a `RFP-05` y, sobre todo, deja `RF-001` y `RF-002` redactados sobre un
modelo —cantidad de esquejes por cama— que `DEC-14` ya invalidó.

**El cifrado de los respaldos.** `DEC-11` y `DEC-12` no resuelven el trade-off: clave única de
plataforma permite restaurar solo e instantáneamente pero deja `DEC-09` como promesa organizativa;
clave por empresa hace el aislamiento demostrable pero exige que el cliente participe en cada
restauración. No hay opción sin costo, y la elección va también al contrato.

---

### Requisitos candidatos abiertos por esta tanda

Ocho, con numeración propia `RFP-nn` para no colisionar con el catálogo vigente. **Ninguno está
validado con el cliente**, y casi todos dependen de `BR-N6`.

| ID | Qué |
|---|---|
| `RFP-01` | Registrar la siembra por **sección** de cama (área, variedad, densidad) |
| `RFP-02` | Registrar el **motivo** de la disminución de producción, incluida enfermedad |
| `RFP-03` | Vista **geométrica** de camas con porcentaje de producción |
| `RFP-04` | Doble lectura: tallos aproximados y % de plantas reales |
| `RFP-05` | Asistente de captura **offline** |
| `RFP-06` | Consultas y análisis asistidos por IA **en la nube** |
| `RFP-07` | Plantillas de captura configurables — **fuera de la primera entrega** |
| `RFP-08` | Restaurar los datos de una empresa sin afectar a las demás |

---

## Estado tras las tandas 1 y 2

| Alcance | Al empezar | Tras tanda 1 | Tras tanda 1-bis | Tras tanda 2 |
|---|---:|---:|---:|---:|
| `DENTRO` | 110 | 113 | 113 | 120 |
| `EN DUDA` | 52 | 51 | 51 | 51 |
| `CANDIDATO` | — | — | — | 13 |
| `CERRADO` | — | 4 | 7 | 9 |
| `FUERA-F1` | 12 | 12 | 16 | 16 |
| `FUERA-CATALOGO` | — | 4 | 0 | 0 |
| `CONTEXTO` | 30 | 32 | 32 | 32 |
| `METODO` | 11 | 12 | 12 | 12 |
| **Total** | **215** | **228** | **231** | **253** |

El total sube porque las decisiones **crean** elementos: los dos roles partidos, el módulo de BI, la
gestión de suscripciones, las dos fases de entrega y la meta del piloto.

`FUERA-CATALOGO` volvió a cero: los cuatro requisitos huérfanos tienen destino. `FUERA-F1` subió de
12 a 16 porque `DEC-07` y `DEC-08` sacaron cuatro cosas del alcance.

`EN DUDA` se quedó en 51 por un efecto que conviene entender: la tanda cerró 18 elementos, pero los
**14 atributos de calidad entraron** a esa lista al descartarse sus rankings (`DEC-03`), y se
abrieron tres Assessments nuevos. El saldo neto parece plano; el movimiento real fue grande.

**Lo que sigue abierto y de quién depende:**

- **Del cliente** (tanda 3, con preguntas listas para llevarle): `BR-N3` documentos, `BR-21` nivel de
  agregación, `BR-23` productividad y curva de reparto, `BR-N2` sistema actual, `BR-22` 9 o 300
  variedades, `BR-11` catálogo de grados, `BR-24` accesibilidad, `BR-N1` tiempo de captura,
  `BR-N4` doble captura, `BR-N5` sesión offline.
- **Del equipo** (tandas siguientes): ranking de atributos bajo SaaS · cifrado de respaldos y su
  cláusula contractual · alcance de la IA analítica · modelo de suscripción (`CN-05`) · PayU (`CN-11`) ·
  y la sesión interna de proceso de captura que exige `BR-N6`.
