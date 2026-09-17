# FlorLogic — Exposición de 30 minutos · primera muestra

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 16-sep-2026, a pedido de Juan
> Estado: **SIN REVISAR** — ni por Juan ni por Jerónimo
> Manda sobre esto: `Documentacion/Drivers-Arquitectonicos/ADR-PoC-Spikes.xlsx` (36 ADR) ·
> `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx` · `Documentacion/Archivo/Recopilacion/1_VOZ_DEL_CLIENTE.md`
>
> **Está en la raíz del repositorio por instrucción explícita de Juan** (16-sep-2026), para que
> cualquier modelo o persona lo encuentre primero. Es una excepción a la regla de la zona IA
> (`docs/03-arquitectura/spikes/`), por eso lleva el prefijo `IA_` y esta cabecera.
> Su pareja es **`IA_PREGUNTAS-Y-RESPUESTAS-FlorLogic.md`**, el banco de práctica por sesiones.

---

## Para qué es esto

**Audiencia:** un ingeniero de sistemas, líder de arquitectura de software.
**Objetivo:** que compre la idea — que salga convencido de que **el problema es real, el método fue
serio y las decisiones están justificadas**, y que vea con claridad qué falta.
**Quién expone:** Juan y Jerónimo. **Los dos tienen que poder dar la charla entera solos.**

**Tres reglas para exponer ante un arquitecto:**

1. **Cada cifra con su origen.** «El cliente lo escribió» ≠ «lo decidimos nosotros» ≠ «es un
   supuesto». Un arquitecto pregunta *«¿de dónde sale ese número?»* y la respuesta tiene que ser
   inmediata. En este guion cada cifra lleva la marca: **[cliente]**, **[equipo]**, **[medido]** o
   **[supuesto]**.
2. **Lo que no sabemos se dice antes de que lo pregunten.** Hay un bloque entero (§6) para eso. Un
   hueco declarado da credibilidad; un hueco descubierto por el oyente la quita.
3. **Se citan los ADR con la numeración del `.xlsx`** (36 ADR). El `.md` largo
   `FlorLogic-alternativa-de-solucion-y-ADR.md` usa **otra numeración** (su `ADR-001` es «monolito
   modular»; en el `.xlsx` `ADR-001` es «tipo de producto a entregar»). Mezclarlas en vivo es el error
   más fácil de cometer. Ver la trampa T1 al final.

---

## Mapa de tiempos

| # | Bloque | Min | Acumulado | Idea que tiene que quedar |
|---|---|:--:|:--:|---|
| 0 | Apertura | 1 | 1 | «Certeza de los datos» |
| 1 | El problema | 6 | 7 | El dolor no es capturar: es que el dato tarda días en servir |
| 2 | Cómo lo levantamos | 4 | 11 | Método trazable, con sus límites a la vista |
| 3 | La decisión grande: cómo se entrega | 5 | 16 | Local-first, no SaaS, y por qué |
| 4 | Qué hace el sistema: el viaje de un dato | 7 | 23 | Cada paso del recorrido tiene un ADR detrás |
| 5 | Qué ya está probado | 3 | 26 | Hay prototipo y hay mediciones; el rendimiento no es la restricción |
| 6 | Lo que no sabemos | 2 | 28 | Los huecos tienen dueño y tienen nombre |
| 7 | Cierre y pedido | 2 | 30 | Qué le pedimos al arquitecto |

> Si el tiempo aprieta, **se recorta el §5**, nunca el §6. Las preguntas van al final o al banco.

---

## 0 · Apertura — 1 min

**Qué decir:**

> «Le preguntamos al director de producción de la finca qué haría que un sistema así no valiera la
> pena. Escribió, en mayúsculas: **CERTEZA DE LOS DATOS, QUE SE INGRESEN LOS DATOS
> CORRECTAMENTE**. Todo lo que les vamos a mostrar se ordena alrededor de esa frase.» **[cliente]**

**Qué es FlorLogic en una frase:** un sistema que **captura en campo, sin conexión, la siembra y el
corte de flor por cama, proyecta cuánta flor habrá —de qué variedad y en qué fecha— y mide la
desviación entre lo proyectado y lo cortado**. No maneja dinero: la métrica es **tallos cortados**
(`DEC-07`, `A19`).

Fuente: `DRIVERS §1`.

---

## 1 · El problema — 6 min

### 1.1 El escenario físico (1 min)

- Una finca de **15 hectáreas**, **25 bloques**, **~1.525 camas**. Jerarquía: finca → bloque → nave →
  cama → variedad. **[cliente, S1-Q P04 y S2]**
- **3 personas capturan** (un supervisor y dos auxiliares); ~12 usarían el sistema; **~20 solo
  consultan** (vendedoras). **[cliente, S2]**
- **9 variedades activas** en una etapa de producción; ~300 variedades/subvariedades en total, que
  él mismo sacó del alcance. **[cliente]**
- Ciclo de siembra a corte: **3 a 5 meses**; el corte dura **~7 días**; puede adelantarse **hasta 15
  días**. **[cliente, S2]**
- **No hay señal en el cultivo.** A *«¿hay señal de celular o wifi dentro del cultivo?»* respondió:
  **«NO»**. Solo hay conexión en la oficina. **[cliente, S1-Q P29]** — *Este es el hecho que fija la
  arquitectura.*

### 1.2 Cómo se trabaja hoy (2 min)

1. El supervisor recorre camas y anota **a lápiz** en un formato corto: **~1 hora al día**. **[cliente, P28]**
2. Un practicante —puesto rotativo, *«es otra falla»*— digita las planillas: **~4 horas, una vez por
   semana**. **[cliente, P28 y S2]**
3. El dato tarda **8 días** en llegar a planeación y gerencia. **[cliente, `B2`, `H-25`/`H-26`]**
4. La proyección se ajusta **a mano**, restando porcentajes tras ir a mirar la cama. Hoy se ajusta
   **mensual**; lo deseado es **semanal**. **[cliente, P18 y S2]**

### 1.3 Lo que eso cuesta (2 min)

| Síntoma | Cifra | Origen |
|---|---|---|
| Error de captura/digitación que llega a la proyección | **2 %** | [cliente, S2] |
| Brecha presupuesto de ventas contra lo cortado | **~6 % por debajo** | [cliente, P22 y S2] |
| Ventas que hubo que cubrir comprando a terceros | **~8 %**, mensual, más órdenes canceladas | [cliente, S2] |
| Tolerancia que él quiere en la proyección | **±10 % en tallos** (900 → entre 810 y 990) | [cliente, S2] |

**La lectura que hay que transmitir:** el 2 % es **error de transcripción** —papel a sistema— y
**desaparece por diseño** si no hay transcripción (`C8`). Y como la proyección se hace con datos de
hace una semana, **se vende sobre un número viejo**; cuando falla, se compra a terceros o se cancela.

### 1.4 El reencuadre que hicimos (1 min)

> «Al principio optimizamos la **velocidad de captura**, porque era lo único que podíamos medir.
> El dolor que paga el cliente está dos pasos más abajo: **el traslado papel→digital y la
> reconciliación hasta que la proyección es confiable**.»

Consecuencia: la velocidad de captura pasa a ser **un piso de adopción**, no el objetivo.
**Lo que no se relaja es la corrección del dato en la cama**, porque es lo que evita que la
reconciliación simplemente cambie de sitio. Fuente: el reencuadre de Juan del 8-sep-2026.

> `[!]` **No mezclar dos versiones de las cifras.** El cliente escribió *«4 horas una vez a la
> semana»* y *«8 días hasta planeación»*. El relato de Juan del 8-sep dice *«4 horas por tanda»* y
> *«hasta 8 días la proyección, por las correcciones»*. **En la charla se usan las del cliente**; la
> versión de Juan es contradicción `X-01` del banco y hay que resolverla antes de exponer.

---

## 2 · Cómo lo levantamos — 4 min

### 2.1 Las fuentes (1,5 min)

| Fuente | Fecha | Qué es |
|---|---|---|
| **S1** grabada | 27-jul-2026 | El cliente presenta la idea |
| **S1-Q** escrita | tras S1 | 46 preguntas, 40 respondidas |
| **S2** grabada | 4-ago-2026 | La sesión con casi todas las cifras |
| **S3** y **S4** | 11 y 17-ago | Reuniones internas del equipo — **no son voz del cliente** |
| **Caracterización** | ~23-ago | **262 preguntas sí/no** respondidas por el cliente, Juan y Jerónimo |
| **Formato real de captura** | — | La única planilla con datos reales de la finca |

Todo quedó en `1_VOZ_DEL_CLIENTE.md`, con cada cita marcada `CIT` (verificable), `INF` (inferida) o
`ESC` (escrita, la más fuerte).

### 2.2 Del levantamiento a los drivers (1,5 min)

Se dice como embudo:

1. **Mini QAW** con **13 atributos de calidad**, priorizados por tres actores (supervisor de campo,
   gerente de producción, administrador del sistema). **Confiabilidad quedó 1 para los tres** —el
   único consenso total del proyecto—; luego Disponibilidad, Rendimiento y Capacidad para ser Auditado.
2. De las 262 preguntas se votó un **Top 65** → se escribieron **65 escenarios de calidad**
   `ESC-01`..`ESC-65` con los seis elementos de Bass, Clements y Kazman. Confiabilidad se lleva 18.
3. **21 funcionalidades significativas** (`RF-001`..`RF-024`) y **38 restricciones** (`CN-01`..`CN-38`),
   de las cuales la rectora es **`CN-13`: offline-first obligatorio**.
4. **71 contradicciones** reunidas y trabajadas en rondas: del cliente consigo mismo (A), del cliente
   contra el equipo (B), del equipo consigo mismo (C), consecuencias del modelo (E) y lo nunca
   preguntado (D). **53 decididas; las 18 abiertas son todas del grupo D y necesitan al cliente.**
5. **36 ADR** en formato Nygard, reescritos a mano por Juan.

Fuente: `DRIVERS §5–§9` y `3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md`.

### 2.3 Los límites del método, dichos por nosotros (1 min)

- **Todo descansa en una sola persona**, el director de producción. **Planeación —donde nace la
  proyección— nunca se exploró**: él cerró esa puerta (*«no, eso no»*).
- Las transcripciones de Teams **no distinguen quién habla**.
- **Ya nos equivocamos leyendo al cliente dos veces** (`B7`: «no deja ingresar el último registro»
  quería decir *el más viejo*). Por eso las notas literales se confirman, no se interpretan.
- **Tomamos siete decisiones en contra de respuestas escritas del cliente** —por ejemplo cifrar,
  aunque dijo que no hacía falta— y están listadas para llevárselas, no aplicadas en silencio (`DRIVERS §11.2`).

> El arquitecto va a valorar esto más que cualquier diagrama: **sabemos dónde es débil nuestra evidencia.**

---

## 3 · La decisión grande: cómo se entrega — 5 min

### 3.1 Descartamos SaaS (2 min)

Empezamos pensando SaaS multi-tenant (`DEC-01`) y **lo derogamos por inviable** (`ADR-001`):

- **El cliente necesita operar sin internet.** El motivo real de pedir servidor propio no era
  desconfianza, **era continuidad sin conexión** (`A20`).
- Los números no cerraban: 10 USD/usuario/mes en una finca con 3 capturadores ≈ 230 USD/mes. **[equipo, `E4`]**
- El umbral de «20 empresas para ser rentable» no lo validó nadie, y no hubo estudio de mercado.
- Operar una plataforma 24×7 con dos ingenieros no estaba costeado.
- Y la objeción que él mismo predijo — *«¿cómo me garantiza que no le da mi información a otra
  finca?»* — apunta justo al SaaS. **[cliente, S1]**

> **Lección que conviene confesar:** decidimos el modelo **antes** de entender por qué el cliente
> pedía servidor propio. Rehacerlo costó una ronda entera.

### 3.2 Lo que quedó: local-first con servicios en línea (1,5 min)

- **Una instalación por empresa**, en un nodo dentro de la finca, que opera **sin internet** sobre la
  información activa. La nube presta **respaldo, actualización y servicios**, nunca en camino
  crítico (`ADR-001`, `CN-37`).
- Modelo comercial: **~20.000 USD por instalación** + mensualidad de **100–200 USD** **[supuesto, sin
  costear — `E2`]**. Entrega: **mayo de 2027**, después de la temporada alta **[cliente, `CN-01`]**.

**Idea rectora, para decirla textual:**

> «**El teléfono es el sistema de registro mientras hay jornada; el servidor de la finca lo es
> cuando hay red.** Una caída se convierte en retraso, no en parada.»

### 3.3 Las cuatro alternativas (1,5 min)

| | Qué es | Por qué no / por qué sí |
|---|---|---|
| **ALT-1** ✅ | **Monolito modular** en contenedor, una instalación por empresa, cliente offline pesado | Cabe en 2 personas y ~20.000 USD, entregable a mayo 2027, aislamiento demostrable |
| ALT-2 | Microservicios con broker y orquestador | Sobre-ingeniería con dos personas sin experiencia operativa (`CN-06`); multiplica costo fijo (`CN-35`) |
| ALT-3 | Backend-as-a-Service (Firebase/Supabase) | **No corre en la finca** (`CN-37`, descalificatorio); precio por usuario; la llave no es nuestra; su sincronización no es la nuestra |
| ALT-4 | Código propio por finca | La novena finca es el noveno proyecto; parametrizar se vuelve desarrollar |

**La frase que cierra el bloque:** *«lo que varía entre fincas son **datos** —catálogo, reglas,
parámetros—, **no código**»* (`CN-36`). Con N instalaciones dentro de casa de clientes, un cambio de
esquema es lo más caro que puede pasar.

Fuente: `FlorLogic-alternativa-de-solucion-y-ADR.md §2` (numeración propia del `.md`).

---

## 4 · Qué hace el sistema: el viaje de un dato — 7 min

> **La forma de contarlo: seguir UNA cama de principio a fin.** Cada parada tiene su ADR. Aquí se
> gana o se pierde la charla, porque es donde el arquitecto ve que la solución existe entera.

### Los cuatro lugares

| Zona | Nodo | Qué vive ahí |
|---|---|---|
| **A · Invernadero, sin red** | Teléfono del capturador | Aplicación de captura, almacén local, motor de reglas local |
| **B · Oficina de la finca, red local** | Servidor de la finca | Backend, base de datos de la empresa, caché de producciones activas |
| **B · Oficina** | Computador de oficina | Aplicación web de consulta (navegador) |
| **C · Internet, asíncrono** | Nube | Respaldo cifrado, distribución de versiones, servicios |

### Las once paradas

**① Antes de salir al cultivo — el paquete.** El teléfono descarga **un solo paquete de
configuración de la empresa** (catálogo, reglas y parámetros, una única versión inmutable) y **lo
que le toca hoy**: qué camas y bloques. Si falta algo, **se avisa antes de salir, nunca en medio del
campo**. → `ADR-029`, `ADR-015`, `ADR-026`

**② La persona entra.** Identidad propia por usuario, en teléfonos posiblemente compartidos. Al
sincronizar recibe una **credencial con vigencia acotada**; sin red se desbloquea con PIN o
biometría. Cierre de sesión a los 15 minutos. → `ADR-007` *(ventana abierta: 24 h o 7–10 días)*

**③ Captura de la cama.** Se captura **una cama completa de una sentada**, aunque el dato aterrice
en la **sección** (una cama puede estar dividida: pasa en el 17 % de las filas de la planilla real).
**Cada campo capturado es un hecho, no una columna**: agregar un tipo de labor es agregar una fila
al catálogo, no migrar N bases. → `ADR-024` *(en Propuesta)*, `CN-36`

**④ Validación en la cama, sin red.** Las reglas viven en **JSON**, no en código. *«Nadie corta más
tallos de los que sembró»* se rechaza ahí mismo **con el motivo en lenguaje de negocio**. **Un
obligatorio incompleto no frena la captura**: viaja y queda como trabajo para el gerente de
producción. → `ADR-006`, `RF-004`/`RF-005`, decisión de Juan del 15-sep

**⑤ Guardado local.** Cada captura se confirma localmente con un **identificador único generado en
el teléfono**, entra a una **bandeja de salida persistente** y **no se borra hasta que el servidor
confirma**. Sincronizar es transporte, no condición para capturar. → `ADR-002`

**⑥ Sincronización.** Apunta a **diaria**, apenas hay red, y forzable. El recordatorio se endurece
con la antigüedad de lo pendiente: avisa → estorba → impide. **Recibir el mismo envío dos veces no
hace nada.** La captura adquiere trazabilidad al llegar a la base: qué dispositivo, qué persona,
reloj del servidor, desfase, versión de configuración. → `ADR-025`, `ADR-027`, `ADR-028`

**⑦ Choques y tiempo.** Si dos anotaciones hablan del mismo hecho, **gana la más reciente**,
automático y sin mediador. Pero el reloj del teléfono miente, así que se guardan **tres horas**: la
del teléfono (intacta, para auditar), **una corregida con el desfase medido** (la única que decide
choques) y la del servidor. Ante una hora dudosa **se marca, no se bloquea**. → `ADR-031`, `ADR-014`,
`RF-022`

**⑧ Proyección.** Dos cosas separadas: el **cálculo vivo**, que se rehace con cada sincronización, y
la **versión publicada**, una foto congelada con su corte de datos y sus parámetros, que es la vara
para medir la desviación. Cambiar un parámetro crea versión nueva, no reescribe el pasado.
→ `ADR-005`, `RF-006`, `RF-011`, `RF-023` *(bloqueada: falta el % de productividad por variedad, `BR-23`)*

**⑨ Consulta y tableros.** Nunca sobre los hechos crudos: sobre **tablas de consulta derivadas**,
totales ya calculados por finca/bloque/cama/periodo y **carga progresiva** en el orden en que la
persona navega; un **caché delante de la base** con las producciones activas. Tablero de **qué falta
por capturar**: lo asignado menos lo recibido. → `ADR-010`, `ADR-023`, `ADR-034` *(Propuesta)*, `ADR-026`

**⑩ Corregir y cerrar.** Mientras la producción está abierta **se guarda cada modificación** y se
puede devolver. Al capturador **no se le pide motivo ni autorización**. Un cambio puntual se aplica y
queda en bitácora; **un movimiento drástico** (erradicar cinco bloques) **pide confirmación de un
segundo administrador, como aviso, no como bloqueo**. Al **cerrar la producción** el estado se
consolida. **Se conserva toda la información cinco años.** → `ADR-020`, `ADR-032`, `ADR-035`, `ADR-022`

**⑪ Salir de la finca.** Exportación a Excel/PDF con los mismos permisos que la pantalla, y
**lectura directa desde el Power BI del cliente** con un usuario de solo lectura sobre las vistas —
sin exponer nada a internet. **Respaldo cifrado con la llave de cada empresa**, más una **copia de
custodia fuera de línea** con doble control. → `ADR-013`, `ADR-012`, `ADR-003`

### Y lo que deliberadamente no se construye (30 s)

Microservicios, bodega de datos aparte, broker, interfaz pública de escritura, alta disponibilidad
duplicada, **recuperación de teléfonos rotos** (*sale más barato volver a capturar*), plantillas de
captura libres. **Cada una con su condición de reapertura escrita.** → `ADR-017`

### IA, en una línea (si preguntan)

**Opcional.** Un modelo local ligero en la finca para **señalar lo raro, lo contradictorio y lo
desactualizado**; uno generativo en línea solo para documentos largos; **nada en el teléfono**.
→ `ADR-036` (sustituye a `ADR-018` y `ADR-030`).

---

## 5 · Qué ya está probado — 3 min

### 5.1 El prototipo `PoC-0` (1 min)

`app-captura/`: aplicación web offline en el celular, **desechable a propósito**. Captura una cama
dividida, choca contra una regla dura, sincroniza, provoca y resuelve un conflicto, y funciona con la
red apagada. Seis suites de prueba en verde. **Se construyó para botarlo**: lo que sobrevive es el
modelo de datos, las reglas en JSON y el contrato de sincronización.

Y ya dio un hallazgo real: **pasando las reglas por la planilla real de la finca aparecieron 9 filas
sospechosas**, entre ellas `Cortona`/`Cartona` (una variedad escrita de dos formas). *El error del 2 %
existe y se ve.*

> `[!]` `PoC-0` **contradice cuatro decisiones vigentes** (identidad y tiempo). No se presenta como
> «la arquitectura funcionando», se presenta como el prototipo que la desafió.

### 5.2 Los spikes del backend (2 min)

Siete spikes medidos el 15-sep-2026 (`SPK-09`, `12`..`17`), cada uno sobre un componente que hay que
hacer nosotros. **Registros de IA, sin revisar**, resumidos en `docs/03-arquitectura/spikes/IA_SPIKES-resumen.xlsx`.

**El mensaje para un arquitecto:**

> «**El rendimiento no es la restricción en ninguno.** Evaluar una cama cuesta décimas de
> microsegundo; una jornada de ingesta entra en ~1 s; la salida al BI usa 68 s de un presupuesto de
> 600. **Lo que decide es quién puede hacer qué y qué pasa cuando dos cosas coinciden.**» **[medido]**

**Los tres fallos silenciosos que encontramos — ninguno da error:**

1. El teléfono con la hora adelantada **gana el 100 % de los choques** → de ahí salió `ADR-031`.
2. Una regla con un tipo desconocido **cierra la cama como si no existiera**.
3. Con seguridad por fila puesta, **una credencial de la empresa 1 leyó 239.200 filas de la empresa
   2**, porque la protección no se hereda a las vistas materializadas. El arreglo está probado.

**Lenguaje del backend:** los cinco candidatos (TypeScript, Python, Go, Java, C#) quedan **dentro del
4 %** en casi todo. **No se ha elegido**, y la decisión ya no depende de medir más — ver §6.

---

## 6 · Lo que no sabemos — 2 min

**Decirlo así, sin suavizar:**

| Hueco | De quién | Qué bloquea |
|---|---|---|
| **`BR-23` / `D3`** — el % de productividad por variedad y cómo se reparten los tallos en los ~7 días de corte | **Cliente** | **La proyección entera** (`RF-006`, `RF-011`) |
| **`D1`** — el proceso de captura a detalle nunca se trabajó con el cliente; incluye **qué es «la jornada»** | **Cliente** | Clave del hecho, reglas, umbral del recordatorio |
| **`CN-20`** — el sistema heredado (~300 tablas). Creemos que es **Microsoft Access**, **sin confirmar** | **Cliente** | Qué es el nodo, carga inicial, quizá `BR-23` está dentro |
| **El evento de cierre de producción** — quién lo dispara y qué consolida | **Equipo** | Retención, consulta del histórico, correcciones |
| **La pregunta del motivo** — ¿el motivo del rechazo debe ser idéntico en teléfono y servidor, o basta el veredicto? | **Equipo** | **El lenguaje del backend** cae solo al responderla |
| Si `ADR-010`, `ADR-023` y `ADR-034` son tres mecanismos o uno | **Equipo** | Sobre-construir la lectura |

> «Nada de esto se destraba escribiendo código. Los tres primeros se resuelven con **una sesión con
> el cliente siguiendo una cama real** y pidiendo **el archivo con el que hizo la última proyección**.»

---

## 7 · Cierre y pedido — 2 min

**Resumen en tres frases:**

1. El problema es **que el dato tarda una semana en servir y llega con errores de transcripción**; por
   eso se vende sobre números viejos.
2. La solución es **un solo producto instalado en cada finca, que captura sin red, valida en la cama y
   proyecta con cada sincronización**, con lo variable como datos y no como código.
3. Tenemos **36 decisiones registradas, 65 escenarios, un prototipo y siete spikes medidos**, y
   sabemos exactamente qué nos falta.

**Qué le pedimos al líder de arquitectura** *(ajustar antes de exponer)*:

- Que ataque **las tres decisiones más caras de revertir**: el modelo de datos por hechos (`ADR-024`),
  la resolución automática por la hora corregida (`ADR-031`) y el monolito por instalación (`ALT-1`).
- Su criterio sobre **la pregunta del motivo** y, con ella, el lenguaje del backend.
- Si ve un **hueco de operación** que dos personas no estén viendo: actualizar N instalaciones en casa
  de clientes (`CN-29`).

---

## Trampas — lo que NO se dice

| # | No decir | Decir | Por qué |
|---|---|---|---|
| T1 | «`ADR-001`, el monolito» | Citar por el `.xlsx` (36 ADR) | Dos libros con numeración distinta |
| T2 | «No se pierde ningún dato» | «Nada se borra del teléfono hasta que el servidor confirma; si el teléfono se destruye antes de sincronizar, **se recaptura** y el sistema dice qué camas» | `CN-15` dice pérdida cero; `ADR-002`/`ADR-025` aceptan recaptura. Contradicción `X-07` |
| T3 | «Es SaaS» / «es multi-tenant» | «Local-first, una instalación por empresa» | `ADR-001` |
| T4 | «La IA ayuda a capturar en el celular» | «IA opcional, en la finca, para revisar» | `ADR-036`; `CN-31`, `C2` y `T11` siguen diciendo lo viejo |
| T5 | «La app es una PWA» como definitivo | «Aplazado a propósito, con tres disparadores» | `ADR-008` |
| T6 | «Ya elegimos TypeScript» | «No hay lenguaje elegido» | Solo lo sugiere un registro de IA sin revisar |
| T7 | «El cliente pidió la IA» | «Fue idea nuestra; él habló de una capa sobre Power BI» | `S3`, `VOZ §1.7` |
| T8 | «El conjunto activo está acotado» | No usarlo | `ADR-033` **Rechazada** |
| T9 | «Se bloquea ante reloj alterado» | «Se marca y se confirma» | `ADR-014` contra `RF-021`/`CN-25`. Contradicción `X-03` |
| T10 | «Los modelos C4 están al día» | «El C4 no refleja todavía los ADR 024 a 036» | Auditoría del 14-sep |
| T11 | Citar las 4 h / 8 días del relato de Juan | Citar las del cliente | Contradicción `X-01` |

---

## Material de apoyo sugerido

- **Un diagrama:** la vista de contenedores de tres zonas (`FlorLogic-alternativa... §3.2`, mermaid) o
  `C4 · Nivel 2 — Contenedores` del `.drawio` — **avisando** que el `.drawio` no refleja los ADR 024–036.
- **Una foto o captura de la planilla real** con `Cortona`/`Cartona` marcado.
- **La demo de `PoC-0`** por túnel (`python compartir.py`), solo si sobra tiempo: dos minutos, red apagada.

---

## Cómo se prepara esta charla

**Esta es la tarea principal de las próximas sesiones, con Juan y con Jerónimo.**
El criterio de terminado: **cualquiera de los dos da los 30 minutos sin notas y responde el banco
entero sin inventar.**

1. Cerrar las contradicciones del **tablero `X-nn`** de `IA_PREGUNTAS-Y-RESPUESTAS-FlorLogic.md`:
   cada una queda en **consenso**, **asignada a Juan**, **asignada a Jerónimo** o **al cliente**.
2. Correr el banco **por sesiones, en orden** (S1 a S12): uno pregunta, el otro responde sin mirar.
3. Reescribir este guion con lo que salga — **y quitarle la cabecera de IA cuando lo revisen**.
