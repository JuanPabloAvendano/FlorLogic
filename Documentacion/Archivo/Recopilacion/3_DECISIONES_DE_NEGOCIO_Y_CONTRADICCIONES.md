# 3 · Decisiones de negocio y solución de contradicciones
## Guía principal de decisiones — FlorLogic

> **v7.0 · 25-ago-2026 — quinta ronda: cerrado todo lo que no depende del cliente.**
>
> **Este documento dejó de ser un inventario de brechas y pasó a ser la guía de decisiones del
> proyecto.** De 71 entradas, **53 están decididas**. Las 18 que quedan son todas del grupo `D` y
> **son la sesión con el cliente**.
>
> Cada entrada conserva **su situación original y todas sus rondas**, sin borrar ninguna: se puede
> leer qué se decidió, **por qué**, qué se descartó y qué había antes.
>
> `[!]` **Si buscas el porqué de una decisión, está dentro de su propia entrada**, en el bloque de la
> ronda que la cerró. Los identificadores (`A1`, `B6`, `C4`, `D3`, `E2`) **nunca se renumeran**.
>
> **Reglas de este archivo, fijadas por Juan:**
> 1. **No se añaden brechas nuevas.** Se trabaja solo sobre las que ya están y sobre lo que ellas
>    mismas abran. Ningún identificador nuevo.
> 2. **No se borra nada.** Lo resuelto se marca resuelto y **se queda a la vista**, con la decisión
>    que lo cerró.
> 3. **Los identificadores nunca se renumeran.**
> 4. **No se pasa a otro archivo hasta que absolutamente todo aquí esté resuelto.**
>
> **Cómo está escrita cada entrada.** Primero la **situación**. Después **lo que dice el cliente**,
> con su cita o su respuesta literal. Después **lo que decimos Juan y Jerónimo**. El cliente va
> primero porque es la fuente de verificación.
>
> **Este documento sigue sin resolver nada del cliente.** Señala y muestra la evidencia de cada lado.

---

## 0 · Lo que cambió en esta versión

### 0.1 · La decisión de modelo — **cambiada en la ronda 3**

> ## 🟢 **VIGENTE · Local-first con servicios en línea**
>
> **Instalación en la infraestructura de cada empresa —~20.000 USD por compañía— más una mensualidad
> por nube, copias, mantenimiento y servicios.**
>
> Lo decidió `B6`. Motivos: resuelve a una cantidad definida de clientes, que es lo realista sin
> estudio de mercado · no hay que gestionar el gasto de todo lo que vive en la nube · **permite seguir
> operando sin internet**, que es valor agregado real · y permite **empezar pequeño e ir creciendo**.
>
> **La mensualidad es una suscripción de servicios gestionados** —nube, copias, mantenimiento,
> soporte y servicios en línea—. **No se le llama SaaS**, porque el SaaS como modelo de negocio quedó
> descartado; es un servicio recurrente sobre una instalación que el cliente ya compró y que es suya.

> ## 🔴 **DESCARTADA · SaaS multi-tenant — inviable para el negocio**
>
> **No es que haya quedado superada: se descarta por inviable**, y de forma total.
>
> **Por qué era inviable, con lo que este archivo fue reuniendo:**
>
> | Motivo | Dónde se demostró |
> |---|---|
> | **No hay estudio de mercado** que respalde un producto para todas las fincas | Q&A del 12-ago, y `DEC-01` se lo dejó escrito a sí misma como riesgo |
> | **Los números no cierran.** 10 USD/usuario/mes en una finca con **3 capturadores** son ~230 USD al mes | `E4`, con datos del propio cliente |
> | **El umbral de «20 empresas» para ser rentable nunca lo validó nadie** | `E4` |
> | **La carga operativa de una plataforma 24×7 no está costeada** y son dos ingenieros | `E2`, `CN-06` |
> | **El cliente necesita operar sin internet**, y una plataforma en la nube no se lo da | `A10`, `A20` |
> | **La objeción comercial que él mismo predijo apunta justo al SaaS** | `E7`, cita de S1 |
>
> **`DEC-01` queda derogada.** No se borra nada: la decisión, su razonamiento y el hecho de que se tomó
> **antes de entender por qué el cliente pedía el servidor propio** siguen a la vista en `C3`.
> El motivo real apareció en `A20`: **no era desconfianza, era continuidad sin internet.**

**Cómo ha ido cambiando cada entrada afectada, sin borrar ninguna versión:**

| Entrada | v1.0 | v2.0 (SaaS) | **v5.0 (local-first)** |
|---|---|---|---|
| **`C3`** Modelo | «La piedra angular», abierta | Cerrada como SaaS | ✅ **Cerrada al revés: local-first** |
| **`B6`** On-premise | Cliente contra equipo | Resuelta **en contra** del cliente | ✅ **Resuelta A FAVOR del cliente** |
| **`C5`** Seguridad + `CN-28` | Clave podía quedar en la finca | La custodia la asume el operador | ✅ **La llave la tiene el cliente** (`B4`) |
| **`C7`** Suscripción | `CN-11` fuera de alcance | `CN-11` vuelve, `CN-05` bloqueante | ✅ **Cobro por instalación + mensualidad** |
| **`C9`** Operador | No existía en fase 1 | El rol existe, cláusula obligatoria | ✅ **Se encoge; queda residuo de implantación** |
| **`C4`** Escalabilidad | Partirla y subirla | Sube, no es driver | Igual — y `B9` quita el punto ciego |
| **Grupo E** | No existía | 7 consecuencias del SaaS | ✅ **5 resueltas · 2 transformadas** |

### 0.2 · El ranking vigente — hoja `2. Priorización-QA`

Es la versión hecha pensando en SaaS y **es la que manda**. Cada actor reparte 1..13; menor es más
prioritario.

| # | Atributo | Sup. campo | Ger. producción | Adm. sistema | Total | % |
|---|---|:--:|:--:|:--:|:--:|:--:|
| **1** | **Confiabilidad** | 1 | 1 | 1 | **3** | 1,10% |
| **2** | **Disponibilidad** | 2 | 2 | 7 | **11** | 4,03% |
| **3** | **Rendimiento** | 3 | 5 | 9 | **17** | 6,23% |
| **3** | **Capacidad para ser Auditado** | 9 | 4 | 4 | **17** | 6,23% |
| **5** | **Capacidad** | 11 | 3 | 5 | **19** | 6,96% |
| 6 | Capacidad para ser Administrado | 13 | 7 | 3 | 23 | 8,42% |
| 7 | Experiencia de Usuario | 6 | 6 | 12 | 24 | 8,79% |
| 7 | Seguridad | 10 | 8 | 6 | 24 | 8,79% |
| 9 | Interoperatividad | 5 | 10 | 10 | 25 | 9,16% |
| 9 | **Escalabilidad** | 8 | 9 | 8 | **25** | 9,16% |
| 11 | Capacidad para ser Soportado | 12 | 13 | 2 | 27 | 9,89% |
| 12 | Portabilidad | 4 | 11 | 13 | 28 | 10,26% |
| 13 | Accesibilidad | 7 | 12 | 11 | 30 | 10,99% |

**Los cinco drivers son:** Confiabilidad · Disponibilidad · Rendimiento · Capacidad para ser Auditado
· Capacidad.
**Confiabilidad es 1 para los tres actores: único consenso total del proyecto.**
`[!]` **Tres empates sin romper:** Rendimiento / Auditado (17) · UX / Seguridad (24) ·
**Interoperatividad / Escalabilidad (25)**.

### 0.3 · Escalabilidad: subió, pero no es driver

**Decidido.** Escalabilidad queda en 25 puntos, puesto 9-10 — por encima de donde la había dejado el
equipo en S3, pero **fuera del top 5**.

**El motivo, y conviene que quede escrito porque es un criterio de diseño, no una preferencia:**
*se quieren ofrecer atributos de calidad más medibles para los usuarios finales.* Escalabilidad es un
atributo que el usuario final **no percibe**: la nota cuando falta, nunca cuando está. Los cinco
drivers son los que el supervisor de campo y el gerente de producción **sí** notan todos los días.

`[!]` **Esto crea una tensión que hay que asumir a conciencia y está registrada como `E6`:** se ha
decidido construir una plataforma multi-tenant, y la escalabilidad no es un driver de calidad. No es
incoherente —el multi-tenant es una decisión de negocio y la escalabilidad un atributo de calidad—
pero **no puede olvidarse al diseñar**.

### 0.4 · El hallazgo que sale de anclar el ranking

> **Los cinco drivers tienen, hoy, un problema de medida. Los cinco.**

| # | Driver | Estado de su medida | Entrada |
|---|---|---|---|
| 1 | **Confiabilidad** | Tiene meta —*«NO PUEDE HABER ERRORES»*, es decir 0— pero el cliente **rechazó todos los instrumentos para medirla** | `A14` |
| 2 | **Disponibilidad** | Tiene tres medidas duras (1 hora · 24×7 · +15 días offline) pero **dos son incompatibles entre sí** | `A2` |
| 3 | **Rendimiento** | **No tiene ninguna medida.** El cliente dijo NO a los cinco escalones de velocidad | `B2` |
| 3 | **Cap. para ser Auditado** | Lo que pide **no es realizable** con lo que rechazó | `A1` |
| 5 | **Capacidad** | Dos respuestas **mutuamente excluyentes** | `A3` |

**Si el objetivo declarado es ofrecer atributos de calidad medibles al usuario final, este es el
trabajo:** hoy ninguno de los cinco drivers se puede medir limpiamente. **Y las cinco entradas que lo
bloquean son del grupo A: solo el cliente puede desbloquearlas.**

### 0.5 · La regla de decisión, enunciada en esta sesión

> **«Cumplir lo más que se pueda de lo que pide el cliente. Pero si una cosa depende de otra,
> entonces se ignora la contradicción del cliente.»**

Es el criterio con el que se resolvió todo el grupo A, y conviene tenerlo escrito porque explica por
qué en `A5`, `A8`, `A10` y `A11` **se decidió en contra de una respuesta suya**. El caso típico que
enuncia la regla: *quiere información verificada, pero sin decir cómo verificarla — entonces hace
falta una verificación, aunque sea poco intrusiva.*

`[!]` **Las decisiones tomadas en contra del cliente hay que llevarlas a la sesión con él**, no
aplicarlas en silencio. Hoy son cinco: `A5` (cifrado local), `A8` (todo visible para todos),
`A10` (la sincronización puede degradarse), `A11` (sí hay cierre de periodo) y `B6` (no hay servidor
propio).

### 0.6 · Lo que la sesión de interpretación abrió

**Las 20 respuestas cierran el grupo A y, de paso, dos brechas del grupo D. Pero abren cinco choques
que no existían o que estaban dormidos.** Ninguno es una brecha nueva: los cinco viven dentro de
entradas que ya estaban.

| # | El choque | Estado tras la ronda 2 |
|---|---|---|
| **1** | **El sistema no puede medir su propio éxito** — la métrica estaba en dinero y `DEC-07` sacó el dinero del dominio | ✅ **CERRADO por `A19`.** La medición se hace **en tallos cortados**, no en ventas. `DEC-07` queda intacta |
| **2** | **Pérdida cero, quince días offline, un dispositivo y cifrado local no caben juntos** | ⚠️ **PARCIAL.** `A4` cierra el caso de la contraseña (recuperación en línea). **El dispositivo destruido sigue sin salida** — y `A10` lo agrava |
| **3** | **`A1` «sin marcas de tiempo» contra `A11` «sí hay fechas»** | ✅ **CERRADO por `A1` ronda 2.** Son tres niveles distintos: **por dato** (no) · **por sesión de sincronización** (sí) · **cierre de periodo** (sí) |
| **4** | **`A6` puede haber sobredimensionado `B1`** | 🔄 **REINTERPRETADO.** Los quince días son **captura retroactiva**, no ventana offline. **El veredicto contra la PWA queda en duda** — ver `B1` |
| **5** | **`A14` reabre `DEC-16`** | ⚠️ **SIGUE ABIERTO**, aunque más barato: la plantilla es común y el tenant **activa columnas**, no las diseña |

**La corrección de `CN-30` queda en suspenso:** `A10` la deja mal calculada, pero **el número final
depende de dónde caiga la carga**, y eso lo decide el modelo de entrega que `B6` reabre.

### 0.7 · Lo que abrió la ronda 2

**Tres cierres grandes, y una pregunta que vuelve a ponerlo todo en juego.**

**Lo que se cerró:**

| Qué | Cómo |
|---|---|
| **El proyecto recupera su métrica de éxito** | `A19`: se mide en **tallos cortados**, no en dinero. Si el sistema dice 870, lo que se corte debe ser 870 o lo más cerca posible |
| **Rendimiento —driver #3— gana medida por fin** | `B2`: **no se mide en segundos por cama, sino en latencia de captura a proyección.** La línea base es del cliente: **hoy son 8 días** |
| **Capacidad —driver #5— gana su número** | `A3`: **2 años de búsqueda rápida**, después demora escalonada por antigüedad |

**Y la pregunta que vuelve:**

> `[!]` **`B6` reabre el modelo de entrega, y esta vez con más dependencias que nunca.**
> `A10` y `A20` describen **local-first con la nube como respaldo y como proveedor de servicios**, no
> el SaaS multi-tenant puro que `C3` cerró. Juan lo plantea explícitamente y lo deja para la ronda
> siguiente. **Ocho entradas dependen de cómo se resuelva:** `C3`, `C5`, `C7`, `C9`, `E2`, `E3`, `E4`,
> `E5` — y con ellas `CN-28`, `CN-30` y `A4`.
>
> **Mientras `B6` no se resuelva, ninguna entrada del grupo E se puede cerrar.**

**Las decisiones tomadas en contra de una respuesta escrita del cliente eran ocho tras la ronda 2.
La ronda 3 las deja en seis:**

| Sigue en contra — **siete** | Ya no lo está |
|---|---|
| `A5` cifrado local · `A8` todo visible para todos · `A10` la sincronización puede degradarse · `A11` sí hay cierre de periodo · `B12` tableros · `B13` habrá manual corto · **`C2` asistente de captura por IA** | ~~`B6`~~ **se le concede el servidor propio** · ~~`B7`~~ **era un error de lectura nuestro** · ~~`B11`~~ **hablaba de otro contexto** |

`[!]` En `B12` hay con qué defenderse: **la idea fue suya en S1**, y los dos tableros instrumentan la
medida de Rendimiento de `B2`.

### 0.8 · Lo que resolvió la ronda 3

**El cambio de modelo desbloqueó de golpe lo que llevaba trabado desde la mañana.** `B6` era el
bloqueante nº 1 y su resolución arrastra doce entradas.

| Entrada | Cómo la resuelve local-first |
|---|---|
| `C3` | Cerrada al revés: **`DEC-01` derogada** |
| `C5` · `CN-28` | ✅ **Sale de `EN DUDA`.** La llave la tiene el cliente (`B4`) — la opción que bajo SaaS era imposible |
| `C7` · `E4` | ✅ **La unidad de cobro es la instalación, no el usuario.** El umbral de «20 empresas» deja de ser condición de viabilidad |
| `C9` · `E5` | ✅ El operador se encoge; **los 3 roles del cliente vuelven a bastar** y `2. Priorización-QA` no necesita cuarta columna |
| `E1` | ✅ **La pregunta se disuelve:** el on-premise ES el producto |
| `E3` | 🔄 El aislamiento pasa a ser **físico** en los datos operativos; sigue vivo solo en la capa de servicios |
| `E6` · `B9` | ✅ La tensión desaparece: **el crecimiento es comercial, no arquitectónico** |
| `E7` | ✅ **La objeción de S1 deja de tener objeto** — y se convierte en el argumento diferencial del producto |
| `A4` | ✅ **El nodo local es la segunda copia.** El dispositivo destruido deja de ser pérdida total |
| `C1` | ✅ Resuelta por `B5`, y **más barata**: Power BI conecta contra la instalación local |
| `A10` · `CN-30` | ✅ **10 concurrentes por instalación**, no ~200 sobre una plataforma compartida |
| `B1` | ✅ **`PLAN_DEMO_CAPTURA §4.4` retirado por premisa equivocada.** La PWA sigue en pie |

**Y dos correcciones de lectura que quitan decisiones contra el cliente:**

- **`B7`** — *«NO DEJA INGRESAR EL ULTIMO REGISTRO»* significaba **«no deja entrar el más viejo»**. Lo
  habíamos leído al revés. **La decisión y el cliente coinciden.**
- **`B11`** — el NO a la vista geométrica era **en la captura en campo**. En la **consulta del
  computador principal** nunca dijo que no.

> `[!]` **Aviso de método que sale de las dos:** una nota de tres palabras en mayúsculas estuvo a punto
> de convertirse en una decisión contra el cliente, y un NO fuera de contexto en otra. **Las notas
> literales de la caracterización hay que confirmarlas con él, no interpretarlas.**

### 0.9 · Lo que la ronda 3 deja abierto

1. **`E2` — el coste de operar el nuevo modelo.** Baja mucho respecto al SaaS, pero **aparecen costes
   nuevos sin costear**: instalar y mantener `N` instalaciones ajenas, y **`CN-29` empeora** porque
   migrar el esquema en `N` sitios con versiones distintas es más difícil que en `N` bases propias.
   **Y ahora hay una mensualidad que fijar sin saber su coste.**
2. **`C9` deja residuo:** durante desarrollo, instalación y soporte seguimos tocando datos reales de
   producción. **La cláusula de acceso de implantación sigue haciendo falta**, más pequeña que antes.
3. **`E1` deja residuo:** falta **plazo y formato** de la salida de datos, que ahora es obligación
   contractual del servicio de nube y no una contraoferta.
4. **`C4` sigue sin decidirse y ahora urge más:** la **extensibilidad del esquema** hay que fijarla
   **antes de la primera tabla**, porque con `N` instalaciones desplegadas cambiarla después es mucho
   más caro.
5. **`B13`** — sin registro de problemas, el compromiso de *«menos de una hora»* sigue sin instrumento.

### 0.10 · Lo que cerró la ronda 4

**`C2` — la IA vuelve como asistente de captura**, y con una razón de negocio que antes no existía:
bajo `B6` **el valor inicial es lo que se le entrega a la empresa para que se quede con ello**, y el
asistente es exactamente eso. **La IA se entrena en el entorno de trabajo del propio cliente.**

| Qué cierra | Cómo |
|---|---|
| `DEC-16` | Se reabre y **se resuelve**. `RFP-05` y `CN-31` salen de suspenso |
| La advertencia de aislamiento de `DEC-16` | ✅ **Desaparece.** Con modelo entrenado por instalación, **el aislamiento es físico** |
| `app-captura/` | ✅ Recupera justificación de producto — **por calidad del dato, no por velocidad** (`B2`) |

**Y lo que abre, que hay que decidir:**

> `[!]` **Dónde corre el modelo.** *«Se entrena dentro de su propio entorno de trabajo»* admite dos
> lecturas con costes muy distintos: **en el nodo local de la finca** —hace falta hardware y encarece
> la instalación— o **en el dispositivo** —hace falta un modelo pequeño y limita lo que puede hacer
> sin conexión—. **Sin decidir, y afecta al precio de instalación de `B6`.**

> `[!]` **`C4` quedó sin respuesta en esta ronda.** Es la única entrada que se abordó y quedó en
> blanco. Sigue abierta y **urge**: la extensibilidad del esquema hay que fijarla antes de la primera
> tabla, porque con `N` instalaciones desplegadas cambiarla después es carísimo.

### 0.11 · Lo que cerró la ronda 5 — **y con ella todo lo que no depende del cliente**

| Entrada | Decisión |
|---|---|
| **`C4`** | **Gana Interoperatividad** el empate (puesto 9; Escalabilidad 10) · **la extensibilidad es obligatoria** · **la votación por rol ya estaba hecha** con los tres roles de la hoja |
| **`C6`** | **Las líneas son una forma de agrupación.** Densidad por metro, cantidad por líneas y unidades por tallo **están todas contempladas** |
| **`C8`** | **El 2% del cliente es el error de transcripción de papel a sistema**, y **desaparece por diseño**: sin transcripción manual no hay error de transcripción |
| **`E2`** | **100–200 USD/mes** para soporte, copias e IA inicial. El resto del costeo se difiere. Y **habrá un sistema de actualización en línea** para quien pague la mensualidad |
| **`E3`** | **Discriminador de empresa desde el día uno**, con prueba automatizada que falle si falta |

**Tres cosas que salen de cruzar estas cinco:**

1. `[!]` **`C4` y `C6` piden lo mismo desde dos sitios distintos: que los campos capturados sean
   DATOS y no COLUMNAS.** Si añadir una labor exigiera una columna, exigiría migrar el esquema **en
   `N` instalaciones dentro de casa de clientes** — lo más caro que le puede pasar al modelo de `B6`.
   **Es la restricción de arquitectura más importante que sale de todo este documento.**
2. ✅ **`E2` tapa el agujero que yo había abierto.** Había marcado `CN-29` como *«empeora»*; **el
   sistema de actualización en línea es el mecanismo que faltaba** — y de paso convierte la
   mensualidad en el canal por el que el producto sigue vivo: *pagas, te actualizas*.
3. ✅ **`C8` cierra `A14` del todo.** El cliente rechazó todos los instrumentos para medir el error
   **porque para él no es algo que vigilar, sino algo que el sistema hace desaparecer.**

**Y dos riesgos que quedan escritos aunque las entradas estén cerradas:** los 100–200 USD/mes hay que
**recalcularlos antes de firmar** con quince instalaciones, y hay que **decidir qué pasa con una
instalación que deja de pagar** — porque si se queda sin actualizaciones, diverge de versión, que es
justo lo que `CN-29` teme.

---

## Índice y resumen

| Grupo | Qué contiene | Abiertas | Decididas | Estado |
|---|---|:--:|:--:|---|
| **A** | El cliente se contradice a sí mismo | **0** | **20** | ✅ Cerrado — rondas 1 y 2 |
| **B** | El cliente contradice al equipo | **0** | **13** | ✅ Cerrado — ronda 3 |
| **C** | El equipo se contradice a sí mismo | **0** | **10** | ✅ Cerrado — rondas 3, 4 y 5 |
| **E** | Consecuencias del modelo de entrega | **0** | **7** | ✅ Cerrado — rondas 3 y 5 |
| **D** | **Nunca se preguntó al cliente** | **18** | 3 | 🔴 **Lo único que queda** |
| | **Total — 71 entradas** | **18** | **53** | |

> ## 🎯 **Todo lo que se podía decidir sin el cliente está decidido.**
> **Las 18 que quedan son del grupo `D` y todas necesitan una sesión con él.**

**Bloqueantes de primer orden, actualizados tras la sesión:**

**Lo único abierto: las 18 del grupo `D`.** Las tres que mandan:

| ID | Qué falta |
|---|---|
| **`D1`** | **El proceso de captura nunca se trabajó con el cliente.** Pide una sesión, no un dato |
| **`D2`** | **Los documentos prometidos no han llegado:** plan de siembra, presupuestos, formatos llenos, tabla de grados, histórico |
| **`D3`** | **De dónde sale el % de productividad por variedad**, y **cómo se reparten los tallos en los ~7 días de corte** |

**Y tres decisiones nuestras que quedaron con un cabo suelto**, aunque su entrada esté cerrada:
**dónde corre el modelo de IA** (`C2`: nodo local o dispositivo — afecta al precio de instalación) ·
**recalcular los 100–200 USD/mes** antes de firmar (`E2`) · **qué pasa con una instalación que deja de
pagar** (`E2`).

---

# GRUPO A · El cliente se contradice a sí mismo

> ## ✅ **20 entradas, las 20 resueltas** — sesión de interpretación del 25-ago-2026
>
> Juan interpretó las respuestas del cliente una por una. **Cada entrada conserva su situación
> original íntegra**, y debajo lleva un bloque con la decisión tomada y con **lo que esa decisión
> abre** sobre entradas que ya existían.
>
> **Cinco se decidieron en contra de una respuesta escrita del cliente** (`A5`, `A8`, `A10`, `A11`,
> y `A18` frente a la definición que se le dio en S2), aplicando la regla de `§0.5`. **Esas hay que
> llevarlas a la sesión con él, no aplicarlas en silencio.**

---

### `A1` · Auditoría partida por la mitad — **la más grave del proyecto**

**Situación.** Pide poder demostrar ante un auditor externo el origen de cualquier cifra, y a la vez
niega todos los mecanismos con los que eso se demuestra. **No es una tensión de prioridades: es
técnicamente irrealizable tal como está respondido.**

**Lo que dice el cliente.** Respondió **SÍ** a: *«demostrar ante una auditoría externa el origen de
cualquier cifra del sistema»* · *«responder auditorías de certificación (Florverde, Rainforest
Alliance o GLOBALG.A.P.)»* · *«conservar la evidencia de las labores que exige la autoridad
fitosanitaria»* · *«demostrar que la información no fue alterada después de haberse cerrado el
periodo»* · *«conservar la trazabilidad de manera indefinida»*, con nota **«DE POR VIDA»**.
Y respondió **NO** a: *«saber la fecha y la hora exactas en que se capturó cada dato»* · *«distinguir
la hora en que se capturó de la hora en que se sincronizó»* · *«conservar todas las versiones
anteriores de un dato modificado»* · *«saber quién hizo cada modificación y por qué motivo»* ·
*«poder exportar ese registro para entregarlo a un tercero»* · *«conservar tanto la información tal
como se capturó como la información ya corregida»*, con nota **«SOLO LA CORREGIDA»**.

**Lo que decimos Juan y Jerónimo.** Ambos respondimos **SÍ** a fecha y hora exactas y **SÍ** a
distinguir captura de sincronización. `RF-016` y `RF-021` están escritos sobre esa base: marca de
tiempo inmutable por evento y conservación del valor anterior. `CN-25` la hace restricción adoptada.
En S1 el propio cliente había dicho *«si pasó algo, por favor que lo firme alguien, algún auditor»*.

**Qué bloquea.** `ESC-012`, `ESC-014`, `ESC-006`, `RF-016`, `RF-021`, `CN-25`, `DEC-08` y toda la
mitad de «Capacidad para ser Auditado».

---

> ### ✅ DECIDIDO · sesión de interpretación del 25-ago-2026
>
> **La trazabilidad es interna, indefinida y no compartible.** La información del sistema **se queda
> en el sistema**. **No se guardan marcas de tiempo de captura ni de sincronización.**
>
> **El *compliance* no se automatiza.** El sistema conserva la información y funciona como se le
> indica; **la verificación ante certificaciones y autoridad la hace la empresa a mano**, con otro
> trabajador. Por eso **no se construye ninguna vía de auditoría para terceros distinta de la que ya
> tienen los administradores**. Lo que sí se exige: que la información sea **suficiente y válida**
> para que una persona pueda hacer esa verificación.

**Lo que abre — y hay que resolverlo dentro de este archivo:**

1. **Sin marca de tiempo, ¿qué ordena la historia de una cama?** `RF-016` y `ESC-013` necesitan
   orden. La salida natural es que **la fecha del dato sea un campo de negocio capturado**, no un
   timestamp del sistema: el formato real ya tiene una columna `FECHA`. **Falta escribirlo así.**
2. **Choca con `A11`.** `A11` decide que **sí** hay que dar fechas por lo legal. O `A1` significa «no
   hay timestamps técnicos por dato» y `A11` «sí hay fecha de cierre de periodo» —que es
   compatible— o se contradicen. **Hay que decirlo explícitamente en el texto de las dos.**
3. **`RF-021` y `CN-25` quedan sin sustento.** Exigen marca de tiempo inmutable y bloqueo si el reloj
   fue alterado, y el cliente respondió **SÍ** a lo del reloj. Si no hay timestamp, ¿por qué importa
   el reloj? Respuesta probable: **porque de él se propone la `FECHA` del dato**. Sin confirmar.
4. **Bajo SaaS deja un hueco que la auditoría manual no puede tapar.** Sin registro de acceso técnico
   (`B4`) y con el operador teniendo acceso (`C9`, `E7`), **una manipulación desde la plataforma sería
   invisible para la verificación manual del cliente.** El cliente no lo pidió; `CN-03` sí lo exige.

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026
>
> **La unidad de trazabilidad es la SESIÓN DE SINCRONIZACIÓN, no el dato.**
>
> No hace falta *«fecha y hora de captura de la cama X»*. Hace falta **de quién, desde dónde y cuándo
> salió cierta información sincronizada**: qué persona, qué día, qué sincronización. La pregunta de la
> caracterización se interpretó como *cada dato* y por eso el cliente la rechazó.
>
> **No se busca la historia minuciosa de una cama.** Para el negocio no es productivo perseguir el
> detalle exacto de cada dato ni su hora. Lo valioso es identificar **la persona, las camas y los
> números que salieron de una sesión completa.**

**Lo que cierra:**

1. **Cierra el choque `A1` ↔ `A11`** de `§0.6`. Ya no hay contradicción: `A1` habla de **timestamps
   por dato** —que no van— y `A11` de **fecha de cierre de periodo** —que sí va—. Entre medias queda
   ahora un tercer nivel que sí existe: **la marca de la sesión de sincronización.** Escrito.
2. **Da la medida de `A12`.** El driver *Capacidad para ser Auditado* se mide así: **reconstruir quién
   sincronizó qué camas y qué números, en qué sesión y qué día, de por vida.** Es medible, es barato y
   no exige nada de lo que el cliente rechazó.
3. **Sostiene `RF-021` y `CN-25` con otro alcance:** el reloj importa **por sesión**, no por dato.

### `A2` · Disponibilidad 24×7 contra mantenimiento con parada

**Situación.** Quiere el sistema disponible las 24 horas y los 7 días, pero no exige que el
mantenimiento se haga sin sacarlo de servicio. Ambas no pueden sostenerse a la vez sin una ventana
acordada.

**Lo que dice el cliente.** **SÍ** a *«que el sistema esté disponible las veinticuatro horas del
día»*, **SÍ** a *«los siete días de la semana»*, **SÍ** a *«que exista un horario del día en el que
el sistema no pueda estar fuera de servicio bajo ninguna circunstancia»*. **NO** a *«que las labores
de mantenimiento se hagan sin sacar el sistema de servicio»*.
En S1 ya había dado la salida sin que nadie se la pidiera: *«cuando está programado de 2 a 4 de la
mañana […] ustedes deben hacer la programación en la madrugada.»*

**Lo que decimos Juan y Jerónimo.** Ambos respondimos **NO** a 24 horas y **NO** a 7 días, y **SÍ** a
mantenimiento en caliente — exactamente al revés que él en las tres. `DEC-12`/`CN-15` fija fallo de
funcionamiento en 1 hora, que sí coincide con lo que dijo en S2: *«cuatro horas es mucho, mucho.
Entonces 1 hora.»*

---

`[!]` **Bajo SaaS esto se endurece.** La ventana de mantenimiento deja de ser un acuerdo con
una finca y pasa a ser una ventana **común a todas las empresas** — y `CN-30` dice que sus picos de
temporada coinciden. Puede no existir ninguna hora segura para todas a la vez. Ver `E2`.

> ### ✅ DECIDIDO · sesión de interpretación del 25-ago-2026
>
> **El cliente no estaba puntuando disponibilidad en el sentido técnico.** Su «24×7» significa
> **captura y sincronización activas durante todo el día**, no un objetivo de 99,999% anual.
>
> **El sistema puede estar caído un tiempo, pero no en los horarios definidos como intocables**, y
> **toda indisponibilidad prevista debe avisarse con antelación**. La regla es: mantener el 24×7 tanto
> como se pueda, con ventana de mantenimiento acordada y anunciada.

**Lo que abre:**

1. **Alivia `E2` de forma importante.** Renunciar a 99,999% baja la carga operativa del SaaS de
   «guardia permanente» a «ventana anunciada». **Es la mejor noticia que ha recibido `E2`**, aunque no
   lo cierra: los respaldos diarios, la custodia de claves y las migraciones sobre `N` bases siguen.
2. **`D20` se estrecha pero sigue abierta.** Ya no es «¿qué ventana?» sino **«¿cuál es el horario
   intocable?»** — y en S1 el propio cliente sugirió *«de 2 a 4 de la mañana»*. Falta confirmarlo.
3. **Bajo SaaS la ventana es común a todas las empresas**, y `CN-30` dice que sus picos coinciden.
   **El horario intocable de una empresa puede ser la única ventana posible de otra.** Ver `E6`.
4. **El offline-first es el mitigante y hay que decirlo en voz alta:** con la captura funcionando sin
   conexión (`B1`), una caída de plataforma es un **retraso**, no una parada. Es exactamente lo que
   `DEC-12` ya sostenía.

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026
>
> **El horario intocable es negociable si se mira el año completo.**
>
> - **Fallo no planificado, en producción normal:** el sistema no debe tardar **más de 1 hora** en
>   recuperarse.
> - **Mantenimiento y cese programado de actividades:** **sí o sí en temporada baja — mayo de cada
>   año.** Con eso, una parada en horario «intocable» (12-2 am, por ejemplo) es perfectamente válida.

**Lo que cierra y lo que abre:**

1. **Cierra `D20`.** La ventana de mantenimiento existe y tiene fecha: **mayo**, en baja temporada.
2. **Y baja `E6` de golpe.** El problema de *«el horario intocable de una empresa es la única ventana
   de otra»* se disuelve: **la temporada baja es la misma para todas las empresas del sector.** La
   ventana es común porque el calendario agrícola es común.
3. `[!]` **Coincidencia que conviene mirar:** `CN-01` fija la entrega e implementación en **mayo de
   2027**. El mes de la puesta en marcha es el mismo que el de la ventana de mantenimiento anual.

### `A3` · La información antigua: igual de rápida y a la vez puede tardar más

**Situación.** Dos respuestas mutuamente excluyentes dentro del mismo bloque.

**Lo que dice el cliente.** **SÍ** a *«que la información antigua siga estando disponible con la
misma rapidez que la reciente»*. Y **SÍ** a *«¿Se aceptaría que la información de más de cierta
antigüedad tarde más en consultarse?»*.

**Lo que decimos Juan y Jerónimo.** Ambos respondimos **NO** a la primera y **SÍ** a la segunda —
posición coherente, y la barata. En S3 argumentamos: *«sin capacidad no hay trazabilidad»* y *«hay
que llevar el registro posiblemente hasta 5 años»*, subiendo Capacidad al puesto 3.

**Qué bloquea.** `ESC-015` y cualquier medida de rendimiento sobre el histórico.

---

> ### ✅ RESUELTA · sesión de interpretación del 25-ago-2026
>
> **Se adopta la posición del equipo: se acepta que la información de más de cierta antigüedad tarde
> más en consultarse.** De las dos respuestas excluyentes del cliente, gana la segunda.

**Lo que abre:** **falta el umbral.** «Cierta antigüedad» no es una medida. Y Capacidad es el
**driver #5**, así que su escenario (`ESC-015`) necesita un número: *hasta N años igual de rápido, más
allá se acepta espera.* **`N` no lo decide el cliente: lo decidimos nosotros y se lo contamos.**

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026 — **con número**
>
> **Dos años de búsqueda rápida. A partir de ahí, demora escalonada y proporcional a la antigüedad:**
> buscar algo de 10 años tarda más que buscar algo de 3.

**Lo que cierra:** **`A3` queda cerrada del todo y `ESC-015` gana su medida.** Era el único número que
le faltaba a **Capacidad**, que es el **driver #5**. El escenario ya se puede escribir sin `PENDIENTE`.

### `A4` · Cambiar de dispositivo: no, en Disponibilidad; sí, en Portabilidad

**Situación.** La misma capacidad, preguntada en dos bloques distintos, con respuestas opuestas.

**Lo que dice el cliente.** **NO** a *«poder continuar la captura en otro dispositivo cuando el que se
estaba usando deja de funcionar»* (Disponibilidad). **SÍ** a *«poder cambiar de dispositivo
conservando la información que aún no se ha sincronizado»* (Portabilidad). Y **SÍ** a *«que la
información capturada en un dispositivo que se dañó o se perdió pueda recuperarse»*.

**Lo que decimos Juan y Jerónimo.** Juan respondió **SÍ** a continuar en otro dispositivo y **NO** a
cambiar conservando; Jerónimo **SÍ** y **SÍ**. Es una de las 15 preguntas donde diferimos entre
nosotros.

**Qué bloquea.** `ESC-004` y el diseño de la persistencia local.

---

> ### ✅ RESUELTA · sesión de interpretación del 25-ago-2026
>
> **Se deja SÍ y SÍ.** Las dos preguntas se hicieron con diferencia de tiempo y la opinión cambió,
> pero la respuesta escrita antigua se quedó. Vale la más reciente: **se puede continuar la captura en
> otro dispositivo y se conserva lo que aún no se ha sincronizado.**

**Lo que abre — y es el choque más serio de esta pasada:**

> **`A4` + `A5` + `B1` + `DEC-12` no se sostienen a la vez.**
>
> - `A4` y el cliente exigen **recuperar la información de un dispositivo que se dañó o se perdió**.
> - `A5` decide que esa información esté **cifrada e ilegible en el dispositivo**.
> - `B1` acepta ventanas de **más de quince días sin sincronizar**.
> - `DEC-12` fija **pérdida de información CERO**, como objetivo duro y sin excepción.
>
> Un dispositivo con quince días de captura cifrada que se cae a un canal de riego **no se recupera de
> ninguna manera**. No hay copia en ningún otro sitio.

**Las salidas posibles, ninguna elegida todavía:** sincronización oportunista siempre que aparezca
señal —lo que `A6` hace viable— · una segunda copia local en otro medio · o **aceptar por escrito que
la pérdida cero tiene una excepción**, y decírselo al cliente. **Hay que elegir una.**

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026 — **cierre parcial**
>
> **La recuperación se hace con internet.** La información se cifra, y **si se olvida una contraseña,
> se recupera en línea.**

**Lo que cierra y lo que NO:**

1. ✅ **Cierra el caso de la contraseña olvidada**, que era una de las dos mitades del choque nº 2 de
   `§0.6`.
2. ❌ **No cierra el caso del dispositivo destruido.** Si el equipo se rompe, se moja o se pierde
   **con captura sin sincronizar dentro**, no hay contraseña que recuperar: **no hay copia en ninguna
   parte.** `DEC-12` sigue prometiendo **pérdida CERO**.
3. `[!]` **Y `A10` lo empeora.** Si es probable perder la conexión **por completo en toda la finca,
   oficinas incluidas**, la sincronización oportunista de `A6` —que era la salida barata— deja de
   estar garantizada. **Las tres salidas que quedan siguen sin elegir:** copia local en otro medio ·
   sincronización a un nodo local en la finca (ver `A10`) · o aceptar por escrito que la pérdida cero
   tiene una excepción y decírselo al cliente.

### `A5` · Borrado remoto sí, cifrado local no

**Situación.** Pide poder borrar a distancia un dispositivo perdido, pero no pide que lo que hay
dentro sea ilegible. Sin cifrado, el borrado remoto solo funciona **si el dispositivo vuelve a
conectarse**; hasta entonces la información es legible para quien lo tenga. Y él mismo aceptó
ventanas offline de más de quince días.

**Lo que dice el cliente.** **SÍ** a *«poder borrar de manera remota la información de un dispositivo
perdido»*, con nota **«OPCIONAL»**. **NO** a *«que la información guardada en el dispositivo quede
ilegible si el dispositivo se pierde o lo roban»*, **NO** a cifrado en tránsito, **NO** a respaldos
cifrados, **NO** a llave distinta por empresa.
Pero en S1 dijo: *«las empresas de flores son muy celosas en su información […] usted cómo me va a
controlar a saber que usted no le da la información mía a otro cualquiera de flores.»*

**Lo que decimos Juan y Jerónimo.** Ambos respondimos **SÍ** a las cuatro de cifrado y **NO** al
borrado remoto — al revés que él en las cinco. `CN-28` (cifrado en tránsito y en reposo, custodia de
la clave sin decidir) sigue **EN DUDA** y es de las dos que hay que cerrar primero.

**Ojo.** Esto **no cierra `CN-28`**: el cifrado también responde a `CN-03` (secreto empresarial, art.
260 Decisión 486 CAN) y al contrato de servicio, no solo a lo que el cliente pida.

---

`[!]` **Bajo SaaS esto empeora.** La información ya no viaja solo del dispositivo a un servidor de
la finca: atraviesa y reposa en **nuestra infraestructura**, junto a la de empresas competidoras. El
NO al cifrado del cliente deja de ser solo suyo. Ver `C5` y `E7`.

> ### ✅ DECIDIDO · sesión de interpretación del 25-ago-2026
>
> **Se cifra la información en el dispositivo, contra el NO del cliente.** Motivo: **es la opción más
> barata** hacer la información ilegible desde el propio dispositivo, sin depender de conexión. Se
> decide en contra de su respuesta porque el coste es casi nulo y `CN-03` lo respalda.

**Lo que abre:**

1. **De dónde sale la clave local.** El cliente pidió **entrar a la app sin conexión**. Lo estándar es
   derivar la clave de su credencial — pero entonces **si olvida la contraseña, lo no sincronizado se
   pierde**, y eso vuelve a chocar con la pérdida cero de `DEC-12`. Ver `A4`.
2. **No cierra `C5` ni `E7`.** El cifrado *local* no dice nada del cifrado en tránsito, de los
   respaldos ni de la custodia de la clave en la nube, que es donde está la objeción real del cliente.
3. **Hace coherente el borrado remoto** que él sí pidió: con cifrado local, el borrado remoto deja de
   ser la única defensa y pasa a ser una capa más.

### `A6` · «No hay señal» contra la app de plagas que ya sube a la nube

**Situación.** La marcó como contradictoria el propio análisis del cuestionario. Se explica como
arquitectura offline-first, pero sigue sin saberse **dónde sí hay señal**: oficina, poscosecha,
entrada.

**Lo que dice el cliente.** A *«¿Hay señal de celular o wifi dentro del cultivo?»* respondió
literalmente **«NO»** (S1-Q P29). A *«¿Qué registran de plagas y enfermedades?»*: *«es un formato que
se encuentra en un celular, y va directamente en un sistema»* (P30). En S2 lo aclaró: *«es totalmente
celular local […] y luego se sube a la nube»*, y que la app *«es la empresa propia»*.

**Lo que decimos Juan y Jerónimo.** No es contradicción real: es **el precedente de offline-first
funcionando en la finca** (`H-34`), y es el argumento más fuerte a favor de `CN-13`. Lo que falta es
el mapa de cobertura.

---

> ### ✅ RESUELTA · sesión de interpretación del 25-ago-2026
>
> **No era una contradicción: es offline-first, y el mapa de cobertura queda cerrado.**
> **No hay señal en ninguna parte de la finca; sí la hay en las oficinas.**

**Lo que cierra y lo que abre:**

1. **Cierra `D21`** (dónde sí hay señal). Es la única brecha del grupo D que esta sesión resuelve.
2. **Valida `ESC-011`:** la sincronización ocurre al volver al casco, en bloque y al final de la
   jornada. Es exactamente el escenario de los dispositivos sincronizando a la vez.
3. **Tensiona `B1`, y con fuerza.** Si hay señal en las oficinas y la gente vuelve cada día, **el caso
   real de captura sin sincronizar es de una jornada, no de quince días.** `B1` dimensionó la
   arquitectura entera —y tumbó la PWA— sobre los quince días. **Falta saber qué escenario tenía él en
   la cabeza al responder SÍ a los cuatro escalones:** ¿bloques a los que no se vuelve? ¿personal que
   no pasa por oficina? Sin eso, `B1` puede estar sobredimensionada.
4. **Da la salida a `A4`:** con señal diaria en oficinas, la sincronización oportunista al volver
   reduce muchísimo la ventana de pérdida.

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026 — **y reinterpreta los quince días**
>
> **Los quince días no son quince días de dispositivo apagado: son papeles que aparecen tarde.**
>
> El cliente está describiendo su proceso actual: *puede encontrar información en hojas incluso quince
> días después y aun así tiene que registrarla, por un motivo u otro.* **No es el deber ser** y es
> justo lo que el sistema quiere evitar — pero **se espera conservar esa flexibilidad**: mantener
> vigente la información más actualizada y **guardar también la antigua**.

**Lo que cambia — y toca la decisión arquitectónica más grande del proyecto:**

1. **Lo que se necesita no es una ventana offline de quince días: es captura retroactiva.** Poder
   registrar hoy un dato con fecha de hace quince días. Son dos requisitos **completamente distintos**
   y con costes muy distintos.
2. `[!]` **Y eso pone en duda el veredicto de `B1`.** `PLAN_DEMO_CAPTURA §4.4` tumbó la PWA porque la
   ventana offline era de días. **Si la ventana real es de una jornada y lo de quince días es
   retro-captura, la premisa que mató a la PWA se cae.** Ver `B1`.
3. **Encaja con `A15`:** «mantener lo último y guardar lo antiguo» es exactamente el modelo de último
   valor por campo con su fecha.

### `A7` · «Todo está sistematizado» contra 1 hora de papel al día

**Situación.** La contradicción con peor nota de todo el cuestionario (1/5). No está mintiendo:
describe la **intención** de la finca, no su realidad.

**Lo que dice el cliente.** A *«¿Qué se hace hoy por fuera del sistema o de las planillas
oficiales?»*: **«Todo está sistematizado.»** (P35). Pero también: *«el supervisor llena a lápiz y
papel […] se tarda 1 hora al día […] el practicante digitando puede tardar 4 horas 1 vez a la
semana»* (P28) · *«el clima se registra manual»* (P31) · *«se registra 1 cosa: la siembra […] está
digitalizado pero no lo hace la finca»* (P26).

**Lo que decimos Juan y Jerónimo.** Esta respuesta no se discute en reunión: **se verifica contra
documentos**. Y ya se verificó: el formato real de captura es una hoja de oficina escrita a mano.

---

> ### ✅ RESUELTA · sesión de interpretación del 25-ago-2026
>
> **No se contradecía: hablaba de otra capa.** Las **etapas superiores** de la finca —venta,
> facturación y demás— **sí están sistematizadas**. Lo que no tiene sistema es **la captura de estos
> datos**, que es justo el hueco que FlorLogic viene a llenar.

**Lo que abre:** **`D5` sube de prioridad en lugar de cerrarse.** Ahora sabemos que el sistema
existe y que cubre venta y facturación — con toda probabilidad **es el modelo de ~300 tablas de
`CN-20`**, hoy `EN DUDA` y marcado como bloqueante. Y es **exactamente el sistema con el que `B5` y
`C1` deciden interoperar.** Seguimos sin su nombre, sin saber qué guarda y sin haberlo visto.

### `A8` · Quién ve qué: tres respuestas distintas en tres momentos

**Situación.** La visibilidad de la información cambió de sentido tres veces a lo largo del
levantamiento.

**Lo que dice el cliente.**
1. S1-Q P08: *«hay distintas áreas: Planeación, Producción, Ventas, Compras y el área administrativa
   y/o **gerencial (con privilegios)**.»*
2. S1-Q P38 y S2: *«Toda la información puede ser observada por todos siempre y cuando sea bajo el
   contexto de producción»* · *«Hay información muy compartida en todo el sistema. Sí, no hay
   restricción»* · *«para supervisores y auxiliares sí hay restricciones, sobre todo en el precio de
   la venta de la flor.»*
3. Caracterización: **NO aceptó** *«que dentro de una misma empresa toda la información sea visible
   para todos sus usuarios»*.

**Lo que decimos Juan y Jerónimo.** Ambos respondimos **SÍ** a que todo fuera visible para todos —
al revés que él. En S3 lo argumentamos: *«todos tienen acceso a la mayoría de información […] pero
cada uno puede filtrar mediante el panel»* y *«no genera valor agregado abrir más roles»*. Y `DEC-07`
lo llevó al extremo: *«dentro de una empresa no hay ningún dato restringido por rol»*, porque los
precios salieron del dominio.

**Qué bloquea.** `DEC-07`, `CN-12` (RBAC) y el diseño entero de permisos.

---

`[!]` **Bajo SaaS hay dos fronteras, no una.** La de **empresa** (`RF-012`) es estructural e
innegociable. La de **rol dentro de la empresa** es la que él no aceptó ceder — y `DEC-07` la había
declarado inexistente al sacar los precios del dominio. Siguen chocando. Ver `E3`.

> ### ✅ DECIDIDO · sesión de interpretación del 25-ago-2026
>
> **Dentro de una empresa, la información es visible por todos.** No se añade capa de seguridad ni de
> verificación por rol para la visibilidad. **Lo que sí es necesario son los filtros mediante
> paneles**: cada quien llega a lo suyo filtrando, no porque el sistema le oculte nada.

**Lo que abre:**

1. **Se decide contra la respuesta escrita del cliente**, igual que `B6`. Él **no aceptó** que toda la
   información fuera visible para todos dentro de la empresa. Queda registrado como decisión en
   contra, no como acuerdo.
2. **Coherente con `DEC-07`** —*dentro de una empresa no hay ningún dato restringido por rol*— y con
   la solución que S3 ya había razonado: *«tienen acceso a los mismos tableros»*. **`CN-12` (RBAC)
   queda reducido a controlar capacidades —quién captura, quién administra— y no visibilidad.**
3. **Deja una arista legal sin mirar.** Si todo es visible para todos, **los datos personales de quien
   captura también lo son**. El cliente respondió **NO** a darles tratamiento distinto. En Colombia
   eso toca habeas data, y `CN-03` es la única restricción legal que el proyecto tiene escrita.
4. **La frontera de empresa (`RF-012`) no se toca:** sigue siendo absoluta y estructural. Ver `E3`.

### `A9` · ¿9 variedades o ~300?

**Situación.** Diferencia de dos órdenes de magnitud en el dimensionamiento del catálogo. Es
`BR-22`.

**Lo que dice el cliente.** S1-Q P10: *«en una etapa de producción hay **9 variedades activas**
constantemente»* y P03 *«9 variedades de flor con diferentes morfologías»*. S2: *«ahí en estos
momentos, más o menos **300 variedades, 300 subvariedades**.»*

**Lo que decimos Juan y Jerónimo.** No lo hemos resuelto. La lectura probable es que 9 son las
**activas** y ~300 el **histórico o el catálogo completo con subvariedades**, pero es una
interpretación nuestra, no algo que él haya dicho. Afecta a `CN-26` (catálogo descargado antes de
capturar) y al peso del paquete que baja al dispositivo.

---

> ### ✅ RESUELTA · sesión de interpretación del 25-ago-2026
>
> **No son 9 ni 300: son las dos cosas.** Puede haber muchas variedades y muchas subvariedades, y
> **hay que estar preparados para muchas**. Lo que cambia es el paquete que baja al dispositivo:
> **el dispositivo descarga solo lo que le incumbe.**

**Lo que abre:**

1. **Contradice una respuesta escrita del cliente y a `CN-26`.** Él respondió **SÍ** a *«que el
   dispositivo tenga descargado **todo** el catálogo de la finca antes de salir a capturar»*, y
   `CN-26` lo convirtió en restricción adoptada. **«Solo lo que le incumbe» es lo contrario.**
2. **Falta definir qué es «lo que le incumbe».** Lo natural son los bloques asignados. Pero entonces:
   **si a alguien le reasignan un bloque estando en campo y sin señal, no puede capturarlo** — y `A6`
   confirma que en el cultivo no hay señal en ninguna parte.
3. **Cierra el dimensionamiento de `BR-22`** sin necesidad de preguntar más: 9 activas, catálogo
   grande.

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026 — **se revierte lo de «solo lo que le incumbe»**
>
> **Sí hace falta el catálogo completo en el dispositivo.** Se respeta la respuesta escrita del cliente
> y `CN-26` queda intacta.
>
> **Y una propuesta de arquitecto, marcada como tal:** para no cargar demasiada información, al
> dispositivo se sincroniza **solo lo más actualizado y las camas activas con sus procesos**. **No hace
> falta poder consultar sin conexión un proceso de producción ya terminado.**

**Lo que cierra y lo que abre:**

1. ✅ **Cierra la contradicción con `CN-26`** que abrió la ronda 1, y con ella el riesgo de que alguien
   no pudiera capturar un bloque reasignado en campo.
2. **Queda una definición por escribir: qué es «activo».** ¿Una cama sembrada y sin erradicar? ¿Con
   corte abierto? De esa definición depende el tamaño del paquete que baja al dispositivo.
3. **Separa limpiamente dos cosas:** *catálogo* (variedades, densidades, reglas → completo, offline) y
   *datos de producción* (solo lo activo, offline; lo cerrado, en línea). Es coherente con `A10`.

### `A10` · ¿12 personas capturan o 3?

**Situación.** Se corrigió a sí mismo en la misma sesión, y las dos cifras quedaron en la
transcripción.

**Lo que dice el cliente.** *«¿Cuántas personas pueden utilizar el sistema? Para alrededor de **12
personas** para solo el ingreso de información.»* Y a continuación: *«No, de ingreso de información
serían **3 personas**: el supervisor y otros auxiliares […] Son un supervisor y 2 auxiliares.»* Más:
*«las que consultan son las vendedoras, alrededor de 20 personas.»*
Y en la caracterización, **SÍ** a *«prever el uso simultáneo de más de treinta personas subiendo
información el mismo día»*.

**Lo que decimos Juan y Jerónimo.** Ambos respondimos **SÍ** a las 30 personas simultáneas y sobre
esa base está `CN-30` (pico simultáneo entre tenants). **3 capturadores y 30 capturadores no dan la
misma arquitectura de sincronización.** Nota: las 30 pueden ser en multi-tenant, no en una finca —
pero eso es una lectura nuestra.

---

`[!]` **Bajo SaaS la cifra de 30 se explica mejor**, si se lee como *30 capturadores simultáneos en
toda la plataforma* y no en una finca (donde él dijo 3). **Pero es una lectura nuestra, no algo que
él haya dicho**, y `CN-30` está dimensionado sobre ella. Sigue habiendo que preguntarlo.

> ### ✅ DECIDIDO · sesión de interpretación del 25-ago-2026
>
> **Hasta 10 personas por empresa sincronizando**, y **ese número no va a crecer con facilidad**.
> Y una expectativa que el cliente asume como realista: **si muchos intentan sincronizar a la vez, se
> va a demorar**. La degradación bajo carga es aceptable.

**Lo que abre:**

1. **`CN-30` está mal dimensionada.** Estaba construida sobre *«más de treinta personas subiendo
   información el mismo día»*. Son **10 por empresa** — pero bajo SaaS son 10 × `N` empresas: con las
   ~20 del pitch, **~200 concurrentes en el pico**, y `CN-30` ya advierte que los picos de temporada
   **coinciden**. El número sube, no baja. **Hay que recalcularlo.**
2. **Da la medida que le faltaba a `ESC-011`.** «Se va a demorar y es aceptable» convierte un
   `PENDIENTE` en una medida real: **la sincronización masiva puede degradarse sin romper el
   compromiso.** Es el primer escenario de esta pasada que gana medida.
3. **Choca con la respuesta escrita del cliente**, que dijo **SÍ** a *«que muchos dispositivos puedan
   sincronizar al mismo tiempo **sin que el sistema se degrade**»*. La interpretación dice lo
   contrario. Queda como decisión en contra.

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026 — **y toca el modelo de entrega**
>
> **Hay que prepararse para mucha concurrencia y mucha información.** Pero lo que el cliente imagina es
> **hacerlo todo de manera local, en su computador o servidor propio**, y usar la nube **sobre todo
> para copias de seguridad y conexión con servicios**.
>
> **El motivo es concreto: es muy probable perder la conexión por completo en una finca, oficinas
> incluidas.** Y la producción tiene que poder seguir trabajando sobre **sus camas activas y toda la
> información activa del momento**, con la posibilidad de consultar **—con internet—** registros
> pasados y producciones ya cerradas.

**Lo que abre — y es lo más importante de esta ronda:**

1. `[!]` **Esto cuestiona `C3`.** Lo descrito no es un SaaS multi-tenant puro: es **local-first con la
   nube como respaldo y como proveedor de servicios**. Coincide con `A20` y con lo que `B6` plantea
   abiertamente. **No se reabre aquí por decisión propia — se deja marcado para la siguiente ronda**,
   tal como pide `B6`.
2. **Da la salida que le faltaba a `A4`.** Un nodo local en la finca al que sincronizar **sin
   internet** resuelve el dispositivo destruido: deja de haber una única copia.
3. **Corrige el sentido de la corrección de `CN-30`.** Si la concurrencia se resuelve contra un nodo
   local por empresa, **los ~200 concurrentes de la ronda 1 dejan de caer sobre la misma
   infraestructura.** El dimensionamiento depende de `C3`.
4. **Coherente con `A9`:** activo offline, cerrado en línea. Es la misma frontera.

### `A11` · Demostrar que nada se alteró tras el cierre, sin que exista cierre

**Situación.** Pide la prueba de un evento que, según otra respuesta suya, no ocurre nunca.

**Lo que dice el cliente.** **SÍ** a *«poder demostrar que la información no fue alterada después de
haberse cerrado el periodo»*. **NO** a *«que exista una fecha a partir de la cual la información ya
no se pueda corregir»*. Y **SÍ** a *«poder corregir un dato después de que se sincronizó»*.

**Lo que decimos Juan y Jerónimo.** Ambos respondimos igual que él en las tres. La contradicción es
suya, pero **nosotros la heredamos sin verla**.

---

> ### ✅ DECIDIDO · sesión de interpretación del 25-ago-2026
>
> **Sí existen esas fechas. Se ignora la posición del cliente**, porque la suya podría no ser la
> adecuada en la parte legal. Habrá cierre de periodo y fecha a partir de la cual la información deja
> de ser corregible libremente.

**Lo que abre:**

1. **Choca de frente con `A1`, y hay que resolverlo aquí.** `A1` decide que **no** se guardan marcas
   de tiempo de captura ni de sincronización; `A11` decide que **sí** hay fechas. La lectura que las
   hace compatibles es: **`A1` habla de timestamps técnicos por dato; `A11` de la fecha de cierre de
   periodo, que es un hecho de negocio.** Son cosas distintas — **pero eso no está escrito en ninguna
   parte y sin escribirlo el par se lee como una contradicción.**
2. **Desbloquea `ESC-014`**, que estaba en rojo precisamente porque *no existía el concepto de cierre*
   que el escenario necesitaba.
3. **Pero `ESC-014` sigue sin poder demostrar la no alteración**, porque eso depende de `A1` y de las
   versiones anteriores que el cliente rechazó. **La mitad del escenario se desbloquea; la otra no.**

### `A12` · Trazabilidad: la bajó al puesto 8 y luego la pidió «de por vida»

**Situación.** El mismo concepto, valorado de dos formas opuestas con 19 días de diferencia.

**Lo que dice el cliente.** En S2, primero: *«trazabilidad importantísima, trazabilidad es muy
importante […] para mí sería un dos o tres»*. Y en la misma conversación, tras acotarla: *«ya lo
puede dejar en **ocho, no es tan importante** […] no es necesariamente un histórico sobre los mismos
datos, sino directamente un histórico sobre las modificaciones.»*
En la caracterización: **SÍ** a conservar la trazabilidad **de manera indefinida**, nota **«DE POR
VIDA»**, y **SÍ** a responder auditorías de certificación.

**Lo que decimos Juan y Jerónimo.** En S3 la pusimos en el puesto 5: *«la trazabilidad siempre en el
top 5»*. Y renombramos el atributo: «Trazabilidad» ya no existe, se llama **Capacidad para ser
Auditado** = trazabilidad + cumplimiento. **Es posible que el 8 que dio en S2 fuera para el concepto
estrecho y el "de por vida" para el amplio, pero eso es una hipótesis nuestra.**

---

> ### ✅ RESUELTA · sesión de interpretación del 25-ago-2026
>
> **La trazabilidad ya no compite con nada: es parte de Capacidad para ser Auditado**, que es
> **atributo prioritario** — puesto 3-4 del ranking vigente y uno de los cinco drivers. El 8 que el
> cliente le dio en S2 era al concepto estrecho; el atributo actual es el amplio.

**Lo que abre:** **tensión directa con `A1`, y es la que más pesa.** Auditado es un **driver**, y
`A1` acaba de quitarle los timestamps, las versiones anteriores y el valor original. **La medida que
le queda al driver es reconstruir la historia completa de cualquier cama, lote o variedad, de por
vida, sobre la fecha de negocio capturada.** Eso sí es medible y sí es defendible — **pero hay que
escribirlo como la medida oficial del driver**, porque hoy `§0.4` lo sigue dando por irrealizable.

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026
>
> **Hoy el negocio no lleva registro de tiempos, así que el cliente no lo espera.** Pero **si es
> necesario para dar mejor servicio sin subir demasiado el costo de operación, los tiempos vuelven a
> entrar.** Es un sí condicionado al coste, no un no.

**Lo que cierra:** con `A1` refinada, la condición **se cumple**: la marca por **sesión de
sincronización** es barata —una fila por sincronización, no una por dato— y da servicio real. **Los
tiempos entran, al nivel de sesión.** El driver *Capacidad para ser Auditado* queda con medida.

### `A13` · Florverde: «no nos metamos» contra «sí, responder auditorías Florverde»

**Situación.** Reversión limpia y con consecuencias de alcance grandes.

**Lo que dice el cliente.** En S2: *«no, pero nosotros no nos podemos meter en eso. **No nos metamos
en Florverde**, porque es otra cuestión adicional.»* En S1-Q P37 lo había descrito: *«FlorVerde es un
sello donde se piden datos de producción para mostrar que se está cultivando de manera ecosostenible
y amigable socialmente.»*
En la caracterización: **SÍ** a *«responder auditorías de certificación (por ejemplo Florverde,
Rainforest Alliance o GLOBALG.A.P.) con información sacada del sistema»*.

**Lo que decimos Juan y Jerónimo.** Ambos respondimos **NO** a esa pregunta, coherentes con `H-44` y
`D-08`, que sacaron Florverde del alcance **por decisión suya**. Nunca se preguntó qué registros
exige ni en qué formato: por eso `BR` lo marcaba como *«descubrirlo tarde obliga a rehacer
formularios y reportes»*.

---

> ### ✅ RESUELTA · sesión de interpretación del 25-ago-2026
>
> **Se mantiene el NO.** La verificación de certificaciones **no la maneja FlorLogic**. La información
> del sistema será usada por otros medios o por otras personas, que harán esa verificación **a mano**.
> Es la aplicación directa de la regla de `A1`.

**Lo que abre:** aunque no construyamos la auditoría, **la información tiene que salir del sistema en
un estado utilizable por esa persona.** Y ahí hay un cabo suelto: el cliente respondió **NO** a
*«poder exportar ese registro para entregarlo a un tercero»*. **Si nadie puede exportar la evidencia,
la verificación manual que `A13` da por hecha no tiene con qué hacerse.** `H-44` y `D-08` (Florverde
fuera del alcance) se mantienen.

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026
>
> **No se construye ningún formato ni vía de exportación específica para auditoría.** El sistema ya
> tiene sus propias exportaciones; **para auditar se usa la misma información ya considerada
> exportable.**

**Lo que cierra:** ✅ **el cabo suelto que la ronda 1 dejó abierto.** Ya no importa que el cliente
respondiera **NO** a *«exportar ese registro para entregarlo a un tercero»*: **no hay un registro de
auditoría aparte que exportar.** La verificación manual de `A13` se hace con los Excel y PDF de
`RF-019`, que él sí pidió.

### `A14` · «No puede haber errores» contra no querer ver el error

**Situación.** Fija la meta más dura posible y rechaza todos los instrumentos para medirla.

**Lo que dice el cliente.** **NO** a *«fijar una meta explícita de porcentaje máximo de error
aceptable»*, con nota **«NO PUEDE HABER ERRORES»**. Pero también **NO** a *«un indicador del
porcentaje de datos con error o pendientes de verificación»*, **NO** a *«distinguir la información
verificada de la que todavía no lo ha sido»* y **NO** a *«que la información no verificada quede
excluida de las proyecciones»*.
En S2 había dicho lo contrario sobre lo mismo: *«[la información] tiene un error del 2% […] ese 2%
lo tiene que dar el 0% […] lo tiene que revisar un auditor»* y **«¿Está visualizada en algún lado?
No.»**

**Lo que decimos Juan y Jerónimo.** Juan respondió **SÍ** a fijar meta de error, Jerónimo **NO**.
Ambos respondimos **SÍ** a excluir lo no verificado de las proyecciones. `DEC-08` convirtió `RF-016`
en «conservar **y mostrar**» precisamente para exponer al auditor los registros erróneos, en
conflicto o pendientes: **eso es justo lo que él acaba de rechazar.**

**Y hay una frase suya de S2 que lo resume:** *«ese 2% no está visualizado en ninguna parte»* —
*lo que no se ve no se corrige.*

---

> ### ✅ DECIDIDO · sesión de interpretación del 25-ago-2026
>
> **Lo del cliente es sobreautomatización.** La verificación **se mantiene**, y se deja por escrito que
> **debe ser rápida**. Y su nota *«PUEDEN HABER DATOS EN BLANCO»* se interpreta así:
>
> - Lo que él imagina es **una plantilla con mucha información**, donde es normal que parte quede sin
>   diligenciar.
> - **Rechazó cambiar el formato según la variedad** porque añadiría complejidad al sistema y a su uso
>   — por eso pidió **la misma estructura para toda la finca y todos los bloques**.
> - La salida es **un formato único, personalizable por tenant**, que ofrezca la mayor cantidad de
>   información relevante capturable.
> - **Poder cerrar con campos vacíos viene de un caso real de campo:** quien captura puede estar
>   registrando algo muy específico —por ejemplo **una sola subvariedad de una cama**— y por tanto
>   sube deliberadamente información incompleta de esa cama.

**Lo que abre:**

1. **Contradice `DEC-16` de frente.** `RFP-07` (plantillas de captura configurables) está declarado
   **explícitamente fuera de la primera entrega**. «Personalizable por tenant» es exactamente eso —
   y bajo SaaS deja de ser un lujo: **cada empresa tendrá su formato**. `DEC-16` hay que reabrirla.
2. **Resuelve `A15` y buena parte de `A16` y `C6`.** La captura es **incremental por sección**, no
   por cama completa: por eso se guarda incompleto, por eso no se bloquea, y por eso el formato es
   único. Encaja con `DEC-14` y con `RFP-01`.
3. **«Debe ser rápida» sigue sin número.** Es la tercera vez que la velocidad aparece como necesidad
   sin medida — ver `B2` y `D16`. **El driver Rendimiento sigue sin medida.**

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026 — **el modelo de plantilla, explícito**
>
> **Una plantilla predefinida amplia, de la que cada empresa usa el subconjunto que necesita.**
>
> > *La plantilla tiene, por ejemplo, **20 columnas** de información muy específica. Como tenant,
> > **yo solo uso 5**.*
>
> No es «cada empresa diseña su formato»: es **una lista amplia y común, y cada tenant activa las
> columnas que le sirven.**

**Lo que cierra y lo que sigue abierto:**

1. **Baja mucho el coste frente a lo que temía la ronda 1.** Activar y desactivar columnas de una
   plantilla común **no es lo mismo** que un diseñador de plantillas libre. Es mucho más barato.
2. `[!]` **Pero `RFP-07` sigue estando declarado fuera de la primera entrega por `DEC-16`.** Aunque sea
   la versión barata, **`DEC-16` hay que reabrirla igual.** El choque nº 5 de `§0.6` sigue vivo.
3. **Y hace de `D7`, `D8` y `D9` un bloqueante duro:** para definir las 20 columnas comunes hay que
   saber qué es `OBSE`, qué es una «línea» y qué son `lote`, `calibre`, `proveedor` y `contenedor`.

### `A15` · «Todo debe estar ingresado» contra no bloquear nada

**Situación.** Exige completitud y a la vez rechaza cada mecanismo que la haría cumplir.

**Lo que dice el cliente.** **NO** a *«impedir publicar una proyección cuando faltan datos de bloques
enteros»*, con nota **«TODO DEBE ESTAR INGRESADO»**. **NO** a *«impedir cerrar la captura de una cama
si quedaron campos sin diligenciar»*, con nota *«HAY CAMPOS QUE NO SON CONCORDANTES CON OTROS»*.
**NO** a *«una confirmación final que muestre todo lo capturado antes de darlo por guardado»*. **NO**
a *«avisar cuando una proyección se calculó con información incompleta»*. Pero **SÍ** a *«guardar una
captura incompleta y marcarla como pendiente»*.

**Lo que decimos Juan y Jerónimo.** Ambos respondimos **SÍ** a la confirmación final, **SÍ** a
impedir cerrar con campos vacíos y **SÍ** a avisar de proyección incompleta. Su nota *«pueden haber
datos en blanco»* apunta a que **hay campos opcionales según el caso**, lo cual no es lo mismo que no
validar nada — pero eso lo estamos interpretando nosotros.

---

> ### ✅ RESUELTA · sesión de interpretación del 25-ago-2026
>
> **Resuelta por `A14`.** No hay contradicción: *«todo debe estar ingresado»* es el estado final
> deseado, y no bloquear al cerrar es lo que permite llegar a él **capturando por partes**. Quien
> captura una sola subvariedad de una cama deja el resto en blanco a propósito y vuelve después.

**Lo que abre:** si la captura es incremental, **hace falta saber cuándo una cama está completa** —
para poder decir *«todo está ingresado»* y para que la proyección sepa con qué cuenta. El cliente
respondió **NO** a *«un indicador del porcentaje de datos con error o pendientes de verificación»* y
**NO** a *«distinguir la información verificada de la que no»*. **Sin alguna noción de completitud,
`A15` se queda sin forma de cumplirse.** Ver `A14`.

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026 — **el modelo de completitud, resuelto**
>
> **No se busca completitud: se busca el último valor conocido de cada campo, con su fecha.**
>
> Cada sesión puede ser distinta, y muchas veces una orden de captura ni siquiera está en el sistema.
> En vez de perseguir camas completas, **se separa la información sincronizada de la última vez que
> ese dato se capturó y actualizó.** Puede haber huecos y momentos en blanco; lo que se mantiene es
> **el último valor y su última fecha**:
>
> > El 24/07/2026 se capturó `x`, `y`, `z`. El 25 solo `x`, `y`.
> > En el sistema se ve: **`x`, `y` (25) · `z` (24)**.

**Lo que cierra:** ✅ **el cabo que la ronda 1 dejó abierto.** Ya no hace falta saber «cuándo una cama
está completa» ni un indicador de pendientes —que el cliente rechazó—: **el estado de una cama es
siempre el conjunto de últimos valores conocidos, cada uno con su fecha.**

**Y encaja con tres decisiones ya tomadas:** con `A1` (la fecha viene de la sesión de sincronización) ·
con `A6` (mantener lo vigente y guardar lo antiguo) · con `B8` (corregir sin trazabilidad exhaustiva).
**Es, de hecho, la traza mínima del sistema.**

### `A16` · Una sola estructura de captura, contra dos formatos reales en la mano

**Situación.** El cliente pide plantilla única; su propio documento demuestra que usan dos.

**Lo que dice el cliente.** **SÍ** a *«que la estructura de captura sea siempre la misma para todos
los bloques de la finca»*. **NO** a *«que la estructura de captura pueda cambiar según la variedad»*,
nota *«PUEDEN HABER DATOS EN BLANCO»*. **SÍ** a *«una plantilla predefinida que use una estructura
similar a las plantillas de papel utilizadas previamente»*.

**Lo que decimos Juan y Jerónimo.** Los dos respondimos al revés en las dos primeras. Y en S4, leyendo
su formato real, encontramos que **son dos plantillas distintas**: *«Novedades de siembra»* con
7 columnas (`FECHA · BLOQUE · CAMA · VARIEDAD · #LÍNEAS · CANTIDAD · OBSE`) y *«Programa de
siembras»* con once (`fecha salida CF · fecha de siembra · long prebrotado · bloque · cama · variedad
· lote · calibre · proveedor · contenedor · observaciones`). Conclusión escrita en `§4.3`: **«la app
necesita dos plantillas de captura».**

---

> ### ✅ RESUELTA · sesión de interpretación del 25-ago-2026
>
> **Los dos formatos se fusionan**, y posiblemente se extienden, cuando se proponga la plantilla del
> sistema. No son dos plantillas de captura: es **una sola, más ancha**, coherente con el formato
> único y personalizable por tenant de `A14`.

**Lo que abre:** **para fusionar dos formatos hay que entender las columnas de los dos**, y no las
entendemos. `D7` (qué es `OBSE`, que alterna 325 y 425), `D8` (qué es exactamente una «línea») y
`D9` (`lote`, `calibre`, `proveedor`, `contenedor`) **pasan de curiosidades a bloqueantes de `A16`**.
No se puede diseñar la plantilla fusionada sin ellas.

### `A17` · Puso Experiencia de Usuario en el puesto 2 y luego dijo NO a todo lo medible

**Situación.** El atributo que él mismo colocó segundo quedó **sin una sola medida numérica**.

**Lo que dice el cliente.** En S2: *«experiencia de usuario es lo que hemos estado hablando: que sea
fácil de utilizar, muy fácil de interpretar […] es prioridad máxima, la más alta»* → puesto 2 tras
ceder el 1 a Confiabilidad. Y en S1: *«que sea fácil y que estén los campos necesarios, grandes»*.
En la caracterización: **NO** a una sola mano · **NO** a guantes · **NO** a voz · **NO** a
sugerencias · **NO** a ver el avance de la jornada · **NO** a usar la app sin capacitación formal ·
**NO** a la primera cama en menos de diez minutos · **NO** a vista gráfica del bloque · **NO** a
consultar desde el celular.
Lo que sí pidió es cualitativo: legible bajo el sol, escoger de una lista, deshacer, **los nombres que
se usan hablando en la finca**, escanear una marca física.

**Lo que decimos Juan y Jerónimo.** Nosotros la bajamos al puesto 7 con este argumento textual de S3:
*«siguen siendo usuarios que conocen el negocio, no usuarios que necesiten una guía de la mano […] y
pueden ser entrenados en una o 2 semanas»* y *«es lo que ya están haciendo, lo pueden seguir
haciendo, pero ahora desde el celular»*. **Nuestra conclusión y su respuesta coinciden en el fondo y
chocan en el ranking.**

---

> ### ✅ DECIDIDO · sesión de interpretación del 25-ago-2026
>
> **La usabilidad se aborda por método, no por umbral:** demos, feedback y cambios constantes hasta
> definir patrones de diseño. **Se toman en cuenta primero los puntos que el cliente sí respondió que
> sí:** legible bajo sol, escoger de lista en vez de escribir, mínimo de toques, deshacer, indicar qué
> se sincronizó y qué no, los nombres que se usan hablando en la finca, y escanear una marca física.

**Lo que abre:**

1. **`ESC-018` sigue sin medida, y ahora es a propósito.** Es aceptable: Experiencia de Usuario está
   en el puesto 7 y **no es driver**. Pero conviene que quede escrito que la ausencia de medida es una
   decisión y no un olvido.
2. **El método puede devolvernos a `B2`.** Si se hacen demos y se recoge feedback, es muy probable que
   la velocidad reaparezca — y el propio cliente respondió **SÍ** a *«sacrificaría alguna exigencia
   con tal de que la captura sea más rápida»* (`D15`), sin decir cuál. **La pregunta de un minuto de
   `D15` es ahora también el punto de partida de `A17`.**

### `A18` · Puso Accesibilidad de última y luego dijo SÍ a casi todo lo de accesibilidad

**Situación.** Es `BR-24`. Y hay una sospecha razonable de que **no entendió el término** cuando lo
puntuó.

**Lo que dice el cliente.** En S2, tras explicársele como *«la capacidad que tiene el sistema de
poder ser interpretado por personas con […] falta de digitalización, analfabetismo, […] algunos
ciegos»*, respondió: *«puede ser un 10»* — de 13.
En la caracterización dijo **SÍ** a ocho de once: persona con poca experiencia en celulares · persona
con dificultad para leer textos largos · imágenes o símbolos · letra aumentable · no depender del
color · daltonismo · botones grandes · ambiente ruidoso.

**Lo que decimos Juan y Jerónimo.** La pusimos aún más abajo, y con una definición distinta a la que
se le dio a él: en S3 hablamos de *«discapacidades visuales y auditivas»*. **El equipo y el cliente
no estaban puntuando lo mismo.** Y hay una consecuencia: la accesibilidad así definida —*falta de
digitalización*— **es exactamente la premisa del asistente de captura** de `RFP-05` y `CN-31`.

---

> ### ✅ RESUELTA · sesión de interpretación del 25-ago-2026
>
> **Accesibilidad = limitantes físicas.** La facilidad de uso para personas con poca alfabetización
> digital **no entra como accesibilidad: entra como usabilidad**.
>
> `[!]` **Es la definición del equipo, no la que se le dio al cliente en S2.** Es lo contrario de lo
> que proponía `C10`, y queda así por decisión.

**Lo que abre — y esta vez cuadra:** con esta definición, **el puesto 13 que el cliente le dio deja de
ser incoherente.** Sus 8 SÍ se reparten en dos sitios:

| Lo que respondió SÍ | A dónde va | Puesto |
|---|---|---|
| Botones grandes · letra aumentable · no depender del color · daltonismo · ambiente ruidoso | **Accesibilidad** (limitantes físicas) | 13 |
| Poca experiencia con celulares · dificultad para leer textos largos · símbolos además de palabras | **Experiencia de Usuario** (`A17`) | 7 |

Y sus NO —escuchar en vez de leer, estándar formal de accesibilidad— **son coherentes con el puesto
13**. La incoherencia era del instrumento, no del cliente. **`C10` queda resuelta con esto.**

### `A19` · El −6% actual contra el ±10% objetivo

**Situación.** Es `BR-21`. Si la meta del sistema es ±10% y hoy ya están en −6%, **la meta del
proyecto es más floja que lo que ya logran sin sistema.**

**Lo que dice el cliente.** *«El corte real debe quedar dentro del ±10% de lo que el sistema
proyectó»* y *«¿cuál es el 90% de 900? Son 810 unidades. Este rango es entre 810 y 990.»*
Y por otro lado: *«la desviación de presupuesto de ventas versus la proyección normalmente está 6%
debajo del presupuesto»*, aclarando que compara *«lo presupuestado contra lo que efectivamente se
cortó»*.

**Lo que decimos Juan y Jerónimo.** Nuestra hipótesis escrita es que **son niveles de agregación
distintos**: el −6% sería finca/año y el ±10% cama o variedad por semana. **Es una hipótesis
nuestra, no algo que él haya dicho.** Hasta confirmarlo, **el proyecto no tiene métrica de éxito.**

---

---

> ### ✅ RESUELTA · sesión de interpretación del 25-ago-2026 — **y es la respuesta más valiosa de la sesión**
>
> **No son el mismo número ni el mismo nivel: son dos cosas distintas.**
>
> **El ±10% es la banda de la proyección, en tallos.** Se siembran 1.000, ninguna cosecha es perfecta
> y se cuenta con ~900. Sobre esas 900 se vende, sabiendo que el resultado caerá entre **810 y 990**.
> Quien vende, vende asumiendo esa banda: unas veces vende de más y otras de menos.
>
> **El −6% es la brecha de margen, en dinero, y aparece después.** El presupuesto de venta se hizo
> con las 1.000 plantas; la venta real fue de 900 ±10%. Resultado: **un margen presupuestado del 46%
> se convirtió en un margen real del 40%.**
>
> > **Eliminar esa brecha entre margen presupuestado y margen real es la razón de ser del sistema.**
> > Cuando se vende de más hay que comprarle a la competencia o resolver como sea — **y resolver
> > cuesta más que la venta.**

**Lo que cierra y lo que abre:**

1. **Cierra `D4` / `BR-21`**, que llevaba semanas abierta y estaba marcada como *«la métrica de éxito
   del proyecto»*. No había que elegir entre los dos números: miden cosas distintas.
2. **Explica `H-42`** —el ~8% de ventas conseguido comprando a terceros o cancelando órdenes— como el
   síntoma económico de la misma brecha.
3. **Y abre una contradicción de primer orden con `DEC-07`.**
   > `DEC-07` sacó del dominio **los precios y el rendimiento económico**: *«el dominio son
   > cantidades, unidades y calidades»*. Pero **la métrica de éxito que `A19` acaba de fijar está en
   > dinero**: la distancia entre un margen del 46% y uno del 40%.
   >
   > **El sistema, tal como está delimitado, no puede medir su propio éxito.**

   Las salidas, ninguna elegida: reformular la métrica en unidades —*reducir la desviación entre
   tallos proyectados y tallos cortados, y la compra a terceros derivada*— y aceptar que el margen lo
   calcula el cliente por fuera · o **reabrir `DEC-07`** para dejar entrar lo mínimo de dinero que
   haga falta. `DEC-07` ya se dejó a sí misma la advertencia de que el **costo de producción** no es
   dato de ventas y los supervisores ya lo conocen.

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026 — **y disuelve el choque con `DEC-07`**
>
> **La medición no se hace con ventas ni con precios: se hace con tallos cortados.**
>
> > Si el sistema lleva un registro de producción de 900 y muestra que en realidad hay **870**,
> > entonces **lo que se corte debe ser 870 o lo más cercano posible.**
>
> **Eso se refleja de manera directa en los ingresos y en el margen de ganancia, sin necesidad de meter
> ventas ni precios reales en el sistema para hacer la comparación.**

**Lo que cierra:**

> ✅ **Se cierra el choque nº 1 de `§0.6`, que era el más grave del proyecto.**
> El sistema **sí puede medir su propio éxito**, y sin tocar `DEC-07`: la métrica es **la distancia
> entre los tallos que el sistema proyecta y los tallos que realmente se cortan.** El margen lo calcula
> el cliente por fuera, con la certeza que el sistema le da.

**`DEC-07` queda intacta** —el dominio sigue siendo cantidades, unidades y calidades— y el proyecto
recupera una métrica de éxito **medible dentro del alcance**. Es el mejor resultado de la ronda.

### `A20` · La nube sí, y los servidores propios también — **nueva en la v2.0**

**Situación.** Abierta al aplicar la decisión SaaS. El cliente ha defendido las dos posturas
opuestas, y la primera es un argumento **a favor** del modelo que se acaba de elegir.

**Lo que dice el cliente.** En S1, argumentando por la nube, con un motivo operativo propio:
> *«la idea es que ellos migren a la nube, migran a la nube. ¿Por qué? Porque el servidor, digamos,
> en cierto punto es bueno, pero **también necesita un ingeniero de sistemas ahí, necesita una
> cantidad de cosas**. Entonces, si uno le baja la presión a estas empresas por un precio
> razonable […] se puede masificar.»*
> Y: *«primero debe ser estándar, no para cada uno […] y se lo replicamos a todas las [fincas].»*

En la caracterización, ~27 días después: **SÍ** a *«que el sistema pueda instalarse en servidores de
la propia empresa y no solo en la nube»*.

**Lo que decimos Juan y Jerónimo.** Juan **NO** a on-premise, Jerónimo **SÍ**. Decisión final: SaaS
multi-tenant (`C3`, cerrada).

`[!]` **Por qué importa que quede escrita.** Es la única entrada del grupo A donde la contradicción
del cliente **respalda** una decisión nuestra en lugar de tensionarla. Su propio argumento de S1
—*el servidor propio exige un ingeniero de sistemas en planta*— es el mejor material que existe para
la conversación de `E1`. **Pero no lo convierte en un sí:** sigue habiendo un SÍ escrito y reciente a
los servidores propios, y lo reciente pesa más que lo antiguo.

> ### ✅ DECIDIDO · sesión de interpretación del 25-ago-2026 — **y reinterpreta `B6` entera**
>
> **El ingeniero de sistemas de la planta es real y es empleado directo de la empresa que contrata el
> servicio.**
>
> **Lo que el cliente busca con un servidor propio no es desconfianza: es continuidad.** Quiere
> **poder seguir operando aunque no haya internet**, y **acceder a la información «caliente»** —la
> activa de producción— en ese estado. La nube la quiere para **respaldar, guardar y usar la IA**.
>
> > **La frase que lo resume: no quiere tener que parar, con lo que ya tiene en producción, porque se
> > cayó internet.**

**Lo que abre:**

1. **Cambia la contraoferta de `E1` por completo, y a mejor.** No hay que negociar *«te dejamos
   llevarte los datos»*: hay que responder a **continuidad sin internet**. Y buena parte ya está
   construida — `CN-13` (offline-first) hace exactamente eso **para la captura**.
2. **Pero la consulta no está cubierta, y ahí hay una contradicción con su propia respuesta escrita.**
   El cliente respondió **NO** a *«poder consultar la información ya sincronizada aunque en ese
   momento no haya conexión»*. `A20` dice que sí quiere acceder a información caliente sin internet.
   **Las dos no pueden ser ciertas.**
3. **Y tensiona el SaaS recién decidido.** Un componente local que sirva información de producción sin
   internet **es, funcionalmente, un servidor en la finca** — que es justo lo que `C3` descartó.
   Puede resolverse con una **caché local de consulta** en vez de un servidor, pero **nadie lo ha
   decidido**, y es la diferencia entre cumplir lo que pide y reabrir `C3`.
4. **Acota el problema:** con `A6` sabemos que las oficinas **sí tienen señal**. Entonces lo que pide
   no es operar permanentemente aislado, sino **tolerar que se caiga el internet de la finca**. Es un
   problema mucho más pequeño del que parecía.

> ### 🔄 REFINADO · ronda 2 — 25-ago-2026
>
> **El sistema se enfoca a poder usarse dentro de la infraestructura del cliente, y a la vez ofrecer
> servicios de guardado de información en la nube y servicios en la nube.**

**Lo que abre:** confirma y refuerza `A10`. **Junto con `B6`, deja el modelo de entrega abierto para la
siguiente ronda:** lo descrito es **local-first con nube de servicios**, no el SaaS multi-tenant puro
que `C3` cerró. La contraoferta de `E1` ya no es una contraoferta: **es el producto.**

# GRUPO B · El cliente contradice al equipo

> **12 vivas y 1 resuelta en contra del cliente (`B6`).**
> Las 12 vivas **ya están decididas a favor del cliente**: él es la fuente. Lo que falta no es
> discutir, es **propagarlas** a `DEC-nn`, al catálogo, a las restricciones y al modelo. Cada una necesita su
> propia `DEC-nn` de reversión.

---

### `B1` · La ventana offline es de días — **cae la premisa técnica de la PWA**

**Lo que dice el cliente.** **SÍ** a los cuatro escalones: *«capturar durante una jornada completa
sin sincronizar»* · *«durante tres días seguidos»* · *«durante una semana seguida»* · *«durante más
de quince días»*. **El bloque se agotó en SÍ: la ventana real puede ser mayor.**

**Lo que decimos Juan y Jerónimo.** **NO a los cuatro.** Y `PLAN_DEMO_CAPTURA §4.4`, escrito por
nosotros, dice que si la ventana offline es de una jornada o más, **el producto deja de poder ser una
PWA**. Ese punto de decisión ya está alcanzado.

**Qué arrastra.** La tecnología del producto entera · `CN-13` · `CN-23` · `CN-26` · `ESC-008` · el
estatus de `app-captura/` como demo y no como adelanto.

---

> `[!]` **`A6` la pone en duda.** Si no hay señal en ninguna parte del cultivo **pero sí en las
> oficinas**, y la gente vuelve cada día, **el caso real es de una jornada, no de quince días.** Sobre
> los quince días se tumbó la PWA. **Falta saber qué escenario tenía él en la cabeza al responder SÍ
> a los cuatro escalones.**

> ### ✅ DECIDIDO · 25-ago-2026
>
> **No es el deber ser, pero es una posibilidad totalmente real.** Se sostiene el soporte a la ventana
> larga. Ver `A6`: **el origen real de los quince días es la captura retroactiva de papeles que
> aparecen tarde**, no un dispositivo apagado quince días.

**Lo que queda abierto, y es la decisión técnica más cara del proyecto:**

> `[!]` **¿Sobrevive el veredicto contra la PWA?**
> `PLAN_DEMO_CAPTURA §4.4` tumbó la PWA porque la ventana offline era de días. Con `A6`, esa premisa
> se parte en dos:
>
> | Lo que hace falta | Coste | ¿Tumba la PWA? |
> |---|---|---|
> | **Captura retroactiva** — registrar hoy con fecha de hace quince días | Barato. Es un campo de fecha editable | **No** |
> | **Ventana offline real de quince días** — el dispositivo acumula quince días de captura | Caro. Almacenamiento, integridad, riesgo de pérdida (`A4`) | **Sí** |
>
> **Hay que decidir cuál de los dos se construye — o los dos.** Y `A10` mete una tercera opción:
> sincronizar contra un **nodo local en la finca**, que no necesita internet y acorta la ventana del
> dispositivo a horas.

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026
>
> **No hay brecha real.** Se puede cumplir lo que pide el cliente **sin cambiar decisiones tomadas y
> sin renunciar a las facilidades de la PWA.**

**Por qué se cae el veredicto contra la PWA — con fuente primaria:**

| Premisa de `PLAN_DEMO_CAPTURA §4.4` | Realidad verificada |
|---|---|
| iOS borra el almacenamiento a los 7 días | **Las web apps de pantalla de inicio están exentas.** WebKit: *«are not part of Safari and thus have their own counter of days of use»* — y ese contador solo avanza usando la app |
| El navegador puede desalojar los datos | **Chrome concede `persist()` automáticamente a una PWA instalada**, y entonces **no desaloja** IndexedDB, Cache API ni el service worker |
| No cabe tanta información | La cuota es **~60% del disco**. Quince días de captura son **kilobytes** de texto |

> `[!]` **`PLAN_DEMO_CAPTURA §4.4` queda retirado por premisa equivocada.** No era falso que iOS
> borrara almacenamiento: era falso que aplicara al caso de uso. **La decisión PWA / nativo hay que
> tomarla por otras razones**, no por la ventana offline.

**Lo que sí sigue siendo diferencia real de la PWA, para no sobrecorregir:** no hay **Background Sync
en iOS** —la sincronización ocurre al abrir la app, no sola— y `showSaveFilePicker` es de Chromium de
escritorio, así que un respaldo a archivo en móvil va por descarga o Web Share.

**Y el problema de `A4` nunca fue de la PWA:** si el dispositivo se destruye, una app nativa pierde
los datos exactamente igual. Lo que resuelve `A4` es **una segunda copia en otro sitio**, y eso llega
con el modelo local-first de `B6`.

### `B2` · El cliente NO pide velocidad de captura

**Lo que dice el cliente.** **NO** a que registrar una cama tome menos que en papel · **NO** a menos
de un minuto · **NO** a menos de treinta segundos · **NO** a capturar un bloque completo en una
jornada, nota **«SOBRA»** · **NO** a la finca completa, nota **«SOBRA»** · **NO** a que buscar una
cama dé resultado inmediato, nota **«LIGERO, NO DE INMEDIATO»**.

**Lo que decimos Juan y Jerónimo.** **SÍ a los cinco.** Y en S3 lo dijimos con todas las letras:
*«**esto no es una cosa de la entrevista, sino una propuesta.** La idea es ser más rápido que lápiz y
papel»*. En S2 lo habíamos formulado como meta del proyecto: *«eliminar las 4 horas semanales de
digitación»*.

**Ojo con no sobrecorregir.** Que no pida un umbral **no significa que la velocidad no importe**: él
mismo abrió S1 con *«más ágil el ingreso de los datos»* y declaró que *«la revisión de la siembra es
lo que más tiempo consume»*. Lo que cae es **el número**, no el objetivo.

---

> `[!]` **Vuelve por dos sitios distintos tras la sesión.** `A14` exige que la verificación **«sea
> rápida»** y `A17` aborda la usabilidad por demos y feedback, de donde la velocidad reaparecerá.
> **Rendimiento es driver (puesto 3) y sigue sin una sola medida.** La llave sigue siendo `D15`.

> ### ✅ DECIDIDO · 25-ago-2026 — **y le da a Rendimiento la medida que le faltaba**
>
> **El ahorro de tiempo no está en capturar más rápido que el papel: está en sincronizar más rápido y
> reflejarlo antes en las proyecciones.**
>
> > *Ahí está el dolor real: poder trabajar con información fresca más rápidamente, aunque la captura
> > tome el mismo tiempo o un poco más que a lápiz y papel. Porque aunque el sistema tarde más que el
> > papel al capturar, **sería imposible que su sincronización tarde lo que tarda una persona pasando
> > el papel a mano.***

**Lo que cierra:**

> ✅ **El driver #3 sale de `§0.4` con medida propia.**
> **Rendimiento ya no se mide en segundos por cama: se mide en latencia desde la captura hasta que el
> dato está en la proyección.** La línea base es del propio cliente y está en `1_VOZ_DEL_CLIENTE.md`:
> hoy son **8 días** hasta que planeación y gerencia ven el dato, con **4 horas semanales** de
> digitación por medio.

**Y explica por qué el cliente dijo NO a los cinco escalones de velocidad:** no le estábamos
preguntando por su dolor. **`B2` deja de ser una brecha y pasa a ser el enunciado del beneficio.**

`[!]` Falta el número objetivo de latencia. Pero ahora la pregunta tiene sentido —*«¿cuánto puede
tardar un dato en llegar a la proyección?»*— y sustituye a la de `D16`, que preguntaba lo que no era.

### `B3` · El cliente NO pide ergonomía de campo

**Lo que dice el cliente.** **NO** a capturar con una sola mano · con guantes puestos · dictando por
voz · con sugerencias mientras se captura · viendo el avance de la jornada · sin capacitación formal ·
con vista gráfica del bloque en vez de lista. **SÍ** a *«que la mayoría de los datos se capturen
escogiendo de una lista y no escribiendo»*.

**Lo que decimos Juan y Jerónimo.** Juan dijo **SÍ** a una mano, guantes, voz, sugerencias y avance
de jornada; Jerónimo **SÍ** a varias. Ambos dijimos **NO** a lo de la lista, que es justo lo único
que él pidió. Esto desarma buena parte de `RFP-05`, de `CN-31` y de la variante «guiada» de la demo.

---

> ### ✅ DECIDIDO · 25-ago-2026
>
> **Se ofrecen como valor agregado, con prioridad baja.** Que sea posible **vende mejor el producto**,
> aunque las funciones acaben sin usarse. Nota específica sobre la voz: **no se guarda; solo serviría
> para transcribir.**

**Lo que abre:** toca `C2`. La voz vuelve a entrar, pero **sin las dos cosas que la hacían cara**: no
hay almacenamiento de audio y no hay interpretación de lenguaje natural, solo transcripción a un campo.
`PR-01` sigue mandando —*propone, valida, confirma; nunca escritura silenciosa*—. **Prioridad baja:
no compite con los drivers.**

### `B4` · El cliente NO pide cifrado ni aislamiento demostrable

**Lo que dice el cliente.** **NO** a información ilegible si se pierde el dispositivo · **NO** a
demostrar documentalmente el aislamiento · **NO** a registrar todo acceso técnico · **NO** a cifrado
en tránsito · **NO** a respaldos cifrados · **NO** a llave distinta por empresa · **NO** a registrar
cada exportación.

**Lo que decimos Juan y Jerónimo.** **SÍ a las siete.** `CN-28` está `EN DUDA` con la custodia de la
clave sin decidir, y `DEC-09` advierte: *«una copia de seguridad contiene los datos del tenant.
"Acceso indirecto" solo es real si los respaldos van cifrados con una clave que el operador no pueda
usar.»*

**Esto no cierra `CN-28`** — ver `A5`.

---

> `[!]` **Parcialmente decidido en contra del cliente por `A5`:** el cifrado **local en el
> dispositivo sí se hace**, por ser la opción más barata. Lo demás —tránsito, respaldos, llave por
> empresa, registro de acceso— sigue abierto en `C5` y `E7`. Y `A1` deja un hueco: **sin registro de
> acceso técnico, la verificación manual del cliente no puede detectar una manipulación desde la
> plataforma.**

> ### ✅ DECIDIDO · 25-ago-2026
>
> **El cliente no comprende la necesidad de esta seguridad**, y por eso respondió NO. Las necesidades
> reales ya quedaron abordadas en `A4` (recuperación en línea), `A5` (cifrado en el dispositivo) y la
> sincronización.

**Lo que sigue abierto:** `A5` resolvió **el cifrado local**. **Sigue sin decidirse el lado de la
nube**: cifrado en tránsito, cifrado de respaldos y **custodia de la clave**. Eso vive en `C5`, `E7` y
`CN-28`, que sigue `EN DUDA`. `[!]` Y con `A10`/`A20` empujando hacia local-first, **el reparto de
responsabilidad de las claves cambia otra vez** — no se puede cerrar `CN-28` antes que el modelo de
entrega.

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 — **y cierra `CN-28`**
>
> **Se elige la opción más rápida y barata, y el modelo la hace natural.**
>
> Como **cada tenant tiene su propia base de datos con el mismo esquema**, interceptar esa información
> es difícil si **se envía cifrada y se queda cifrada**.
>
> > **Y la pieza que faltaba desde el principio: la llave de acceso la tiene el dispositivo del
> > cliente.**

**Lo que cierra:**

1. ✅ **`CN-28` sale de `EN DUDA`.** La custodia de la clave —la pregunta que llevaba abierta desde el
   principio y que era una de las dos `EN DUDA` prioritarias— **queda del lado del cliente**. Ya no
   hace falta elegir entre las opciones (a), (c) o (e) de `C5`: **con local-first, la (b) —la que se
   descartó por imposible bajo SaaS— es la que aplica.**
2. ✅ **Y responde la objeción de S1 sin necesidad de contrato.** *«¿usted cómo me va a controlar a
   saber que usted no le da la información mía a otro cualquiera de flores?»* — **no podemos darla
   aunque quisiéramos: no tenemos la llave.** Ver `E7`.
3. **Los respaldos en la nube van cifrados con la llave del cliente.** Eso convierte a `DEC-09`
   —*«acceso indirecto solo es real si los respaldos van cifrados con una clave que el operador no
   pueda usar»*— de advertencia imposible en **descripción de lo que efectivamente ocurre**.

### `B5` · El cliente SÍ quiere interoperar — **caen `DEC-06`, `CN-10`, `CN-14`, `CN-33`**

**Lo que dice el cliente.** **SÍ** a *«que la información se pueda leer desde una herramienta de
análisis externa»*, nota literal **«POWER BI»** · **SÍ** a *«tomar información del sistema
administrativo que la empresa ya tiene»* · **SÍ** a *«convivir con la aplicación de plagas sin
reemplazarla»* · **SÍ** a *«una manera automática de conectar el sistema con otros programas, sin
intervención manual»* · **NO** a nómina, pero con nota **«PRODUCTIVIDADES SI»**.
Y en S2 sobre Power BI: *«no sirve, pero se puede mejorar […] cambiando el enfoque.»*

**Lo que decimos Juan y Jerónimo.** `DEC-06` decidió **«BI propio y cerrado, antes que integración
con terceros»**, `CN-10` cerró PowerBI como no-restricción, `CN-14` lo hizo restricción adoptada y
`CN-33` dejó Excel/PDF como **única** interoperabilidad de fase 1, **sin API pública**.
**Y nosotros mismos ya lo habíamos contradicho en S4**, dos días después de decidirlo: *«un BI propio,
pero con la necesidad obligatoria de hacer integraciones con otras herramientas de BI»* y *«al
parecer sí vamos a integrar, pero a la vez ofrecer.»*

**El único matiz que sí sostiene la fase 1:** él aceptó *«que en la primera entrega la única forma de
intercambio sea la exportación manual a Excel y PDF»*.

---

> ### ✅ DECIDIDO · 25-ago-2026
>
> **BI propio Y posibilidad de integrar otros servicios. Totalmente necesario hacerlo.**

**Lo que cierra:** ✅ **ratifica la dirección que `4_CIERRES_GRUPO_C.md` proponía para `C1`.**
`DEC-06` —*«BI propio y cerrado, antes que integración con terceros»*— **queda derogada de hecho**;
`CN-10`, `CN-14` y `CN-33` hay que reescribirlas. Es además lo que el propio equipo había dicho en S4:
*«sí vamos a integrar, pero a la vez ofrecer»*. **Falta aplicar el cambio, no decidirlo.**

### `B6` · El cliente SÍ quiere on-premise — **RESUELTA EN CONTRA DEL CLIENTE**

> **Estado v2.0.** Ya no es una duda: es una decisión tomada contra una respuesta explícita suya.
> Se cerró con `C3` a favor del **SaaS multi-tenant**. Lo que queda no es decidir, es **negociar** —
> y eso vive en `E1`.

**Lo que dice el cliente.** **SÍ** a *«que el sistema pueda instalarse en servidores de la propia
empresa y no solo en la nube»* · **SÍ** a *«llevarse toda la información en un formato utilizable si
se termina la relación con el proveedor»* · **NO** a operar en una finca de otro país.
Pero en S1 había dicho lo contrario, y con argumento propio: *«la idea es que ellos migren a la nube
[…] porque el servidor también necesita un ingeniero de sistemas ahí.»* Ver `A20`.

**Lo que decimos Juan y Jerónimo.** Juan **NO** a ambas, Jerónimo **SÍ** a on-premise. La Q&A del
12-ago se había inclinado a **PaaS** por una razón que sigue en pie y que no desaparece por haber
decidido lo contrario: *no hay investigación de mercado que respalde un producto para todos*, y *el
despliegue asume un ingeniero de sistemas en planta*.

**Cómo queda partida la petición.**

| Lo que pidió | Decisión | Dónde se trata |
|---|---|---|
| Instalar en servidores propios | **Denegado.** Es SaaS | `E1` — hay que decírselo y sostenerlo |
| Llevarse toda su información al terminar | **Concedido**, y conviene concederlo en voz alta | `E1` — falta plazo y formato |

`[!]` **El riesgo que se asume:** el cliente puede sostener su SÍ. Y su objeción de S1 —*«usted cómo
me va a controlar a saber que usted no le da la información mía a otro cualquiera de flores»*— es
precisamente la que el SaaS activa. Ver `E7`.

---

> ### 🔄 REABIERTA POR DECISIÓN · 25-ago-2026 — **pasa a la siguiente ronda**
>
> > **«¿Sigue siendo un SaaS… o no? El servicio de nube, IA y servicios online sería la parte SaaS.
> > Si hay que discutir más sobre esto, lo volveremos a abordar en la siguiente ronda con este
> > archivo.»**
>
> **Queda planteado, no resuelto.** El modelo que describen `A10` y `A20` es **local-first con la nube
> como respaldo y como proveedor de servicios** — y eso no es el SaaS multi-tenant puro que `C3`
> cerró.

**Lo que arrastra, para tenerlo junto cuando se aborde:**

| Entrada | Qué depende del modelo de entrega |
|---|---|
| `C3` | Está **cerrada** como SaaS multi-tenant. Es la que habría que reabrir |
| `C5` · `E7` · `CN-28` | Quién custodia la clave cambia por completo según dónde viva el dato |
| `C7` · `E4` | Si la parte SaaS es solo la nube de servicios, **la unidad de cobro es otra** |
| `C9` · `E5` | El rol de Operador **existe o no existe** según el modelo |
| `E2` | La carga operativa cambia de orden de magnitud |
| `E3` | `RF-012` sigue haciendo falta, pero contra un reparto de datos distinto |
| `A4` | Un nodo local resuelve el dispositivo destruido |
| `A10` · `CN-30` | El dimensionamiento de concurrencia depende de dónde caiga la carga |

`[!]` **Ocho entradas dependen de esta.** Es la decisión con más dependencias de todo el archivo — más
que cuando era `C3` en la v1.0.

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 — **cambia el modelo de negocio del proyecto**
>
> **Local-first con servicios en línea.** Es la opción más viable para el negocio y para un equipo que
> empieza:
>
> - **Resuelve a una cantidad definida de clientes**, que es lo realista sin conocimiento de mercado.
> - **Se cobra por instalación** de la plataforma y del aplicativo.
> - **No hay que gestionar el gasto de todo lo que vive en la nube.**
> - **Permite seguir operando sin conexión a internet**, que es valor agregado real.
> - Y sobre eso **se ofrece por suscripción: nube, copias, mantenimiento y servicios.**
> - **Permite empezar pequeño e ir creciendo.**
>
> > **El modelo concreto: ~20.000 USD por cada compañía donde se instale, más una mensualidad por
> > servicios, copias y soporte.**
>
> *«Aunque no sea SaaS, sigue siendo un modelo de negocio válido y aproximado.»*

**Lo que cierra — y es la entrada con más efecto de todo el archivo:**

1. ✅ **Desbloquea el grupo E entero.** Las 7 entradas estaban esperando esta decisión. **Cinco se
   resuelven y dos cambian de naturaleza.** Ver `§0.8`.
2. ✅ **Cierra `C3` en sentido contrario** al del 25-ago por la mañana, **`C5`/`CN-28` vía `B4`**,
   **`C7`/`E4`** con el modelo de cobro, **`C9`/`E5`** porque el operador de plataforma se encoge, y
   **`A4`** porque aparece el nodo local que es la segunda copia.
3. ✅ **Y deja de ser una decisión contra el cliente.** Él respondió **SÍ** a instalar en servidores
   propios; ahora se le da. `B6` sale de la lista de `§0.7`.

`[!]` **Precisión de nombre, para el documento académico:** lo decidido no es «no-SaaS». Es un
**híbrido**: licencia e instalación por empresa (*on-premise*) **más** una capa de servicios en línea
que **sí es SaaS** —copias, sincronización, IA analítica, soporte—. Conviene nombrarlo así y no como
«dejamos el SaaS», porque la mitad recurrente del negocio sigue siéndolo.

### `B7` · El cliente NO quiere mediación humana — **cae `DEC-05`**

**Lo que dice el cliente.** **NO** a *«que el sistema deje ambas [capturas] y pida que una persona
decida»*, nota **«NO DEJA INGRESAR EL ULTIMO REGISTRO»**. **NO** a resolver tomando la más reciente.
**SÍ** a *«que quien capturó el dato se entere cuando su captura fue descartada o modificada»*.

**Lo que decimos Juan y Jerónimo.** Ambos **SÍ** a la mediación humana y **NO** al aviso. `DEC-05` y
`CN-24` fijan «automático por defecto, mediación humana como opción configurable por empresa», y el
principio `PR-09` dice *«automatizar primero, permitir mediación humana como opción»*.
La cita de S1 sobre la que se construyó todo eso —`RN-04`— era: *«solo 3 personas ingresan
información: 1 supervisor y 2 auxiliares.»*

---

> ### ✅ DECIDIDO · 25-ago-2026 — **contra la nota literal del cliente**
>
> **Se automatiza, y en caso de información repetida se almacena la capturada más recientemente.**
> Motivo: es información necesaria que **no puede esperar a ser tratada** para entrar en la proyección.

**Lo que abre:**

1. `[!]` **Contradice la nota literal del cliente**, que es de las más explícitas que dio:
   **«NO DEJA INGRESAR EL ULTIMO REGISTRO»**. Él quería rechazar el segundo; se decide conservar el
   segundo. **Es una decisión en contra y hay que llevársela.**
2. ✅ **Pero encaja perfectamente con `A15`.** Si el estado de una cama es *el último valor conocido de
   cada campo con su fecha*, entonces «gana el más reciente» **no es una regla de conflicto: es el
   modelo de datos**. La contradicción con el cliente es real; la incoherencia interna desaparece.
3. **`DEC-05` y `CN-24` se reescriben** hacia orden cronológico estricto sin mediación humana —que es
   donde `PR-09` ya apuntaba—. **Y el aviso a quien capturó sigue en pie:** el cliente respondió **SÍ**
   a enterarse cuando su captura fue descartada o modificada.

> ### 🔄 CORREGIDA · ronda 3 — 25-ago-2026 — **no era una decisión contra el cliente**
>
> **«ULTIMO» no significaba «el más reciente»: significaba «el más viejo».**
>
> Lo que el cliente quería decir con *«NO DEJA INGRESAR EL ULTIMO REGISTRO»* es que **no se deja entrar
> el registro viejo**. Lo que se busca conservar es **lo último llegado**. Él lo expresó de esa forma y
> se leyó al revés.

**Lo que corrige:**

1. ✅ **`B7` sale de la lista de decisiones tomadas en contra del cliente.** Era un error de lectura
   nuestro, no un desacuerdo. **La decisión y el cliente coinciden: gana el más reciente.**
2. ✅ **Y refuerza `A15` en lugar de tensionarlo.** «Gana el más reciente» deja de ser una regla de
   conflicto impuesta: **es exactamente el modelo de último valor por campo con su fecha** que el
   propio cliente describe.
3. `[!]` **Vale como aviso de método:** una nota de tres palabras en mayúsculas se leyó al revés y
   estuvo a punto de convertirse en una decisión contra el cliente. **Las notas literales de la
   caracterización hay que confirmarlas con él, no interpretarlas.**

### `B8` · «Solo la corregida» — choca con `RF-016`

**Lo que dice el cliente.** **NO** a *«conservar tanto la información tal como se capturó como la
información ya corregida»*, nota **«SOLO LA CORREGIDA»** · **NO** a *«que al corregir un dato se
conserve visible el valor original»* · **NO** a *«que toda corrección exija escribir un motivo»* ·
**NO** a *«conservar todas las versiones anteriores»*.

**Lo que decimos Juan y Jerónimo.** `RF-016` exige conservar y mostrar *«valor anterior en caso de
corrección»*, y `DEC-08` lo amplió a exponer al auditor los registros erróneos. En la Q&A del 12-ago
llegamos a planear *«copias directas sin modificar + copias modificadas más trazabilidad completa»*,
justificándolo con el volumen. **En las cuatro preguntas los dos respondimos igual que él** (NO) —
pero nuestros documentos dicen lo contrario. **La contradicción está entre nuestras respuestas y
nuestros propios requisitos.**

---

> ### ✅ DECIDIDO · 25-ago-2026
>
> **Se puede corregir sin mantener registro exhaustivo, y es una necesidad de primer orden.**
>
> Viene del volumen: es tanta información y se esperan tantos errores que **corregir es una operación
> normal que puede ocurrir varias veces al día**. Para el cliente, poder corregir sin trazabilidad
> exhaustiva **no es una concesión: es un requisito.**

**Lo que cierra:** ✅ **coherente con `A1` y con `A15`.** La traza que queda no es ninguna —es la de la
**sesión de sincronización** más el **último valor con su fecha**—, y eso basta para saber quién y
cuándo, sin guardar cada versión intermedia. **`RF-016` se reescribe: deja de exigir el valor
anterior.**

`[!]` Lo que se pierde a conciencia: **no se podrá reconstruir qué decía un dato antes de una
corrección.** El cliente lo pidió así (*«SOLO LA CORREGIDA»*) y es coherente con `A13` —la verificación
es manual— pero conviene que quede escrito que se pierde, no que no existía.

### `B9` · Escalabilidad: el cliente dijo SÍ 11 de 11

**Lo que dice el cliente.** Único bloque sin un solo NO. Incluye cuatro donde nos revirtió: **SÍ** a
*«que el crecimiento del sistema no implique un aumento proporcional del costo por finca»* · **SÍ** a
*«que un cambio en la estructura se aplique a todas las empresas sin intervención manual una por
una»* · **SÍ** a *«varios años de historia acumulada sin que las consultas se vuelvan lentas»* ·
**SÍ** a *«que agregar un tipo de labor o de medición nueva no exija rehacer la captura existente»*.

**Lo que decimos Juan y Jerónimo.** **NO a las cuatro.** Y en S3 pusimos Escalabilidad casi al final
con este argumento: *«no es un proyecto que se estime para crecer […] estamos buscando un desarrollo
a medida […] sin la necesidad de crecer, porque no va a ser un software as a service.»*
`CN-29` (migraciones automatizadas sobre N bases) y `CN-35` (costo por tenant acotado) sí van en la
dirección del cliente.

---

> ### ✅ DECIDIDO · 25-ago-2026
>
> **Va a crecer, y mucho, si el negocio va bien.** Pero **como no hay estudio de mercado, el SaaS se
> limita a lo ya acordado.** Crecer es la expectativa; dimensionar para ello ahora, no.

**Lo que abre:** `[!]` **tensión declarada con `E6`.** Se espera crecimiento fuerte y a la vez
Escalabilidad queda **fuera del top 5** de drivers. No es incoherente —una cosa es la expectativa de
negocio y otra el atributo de calidad que se instrumenta— **pero es exactamente el punto ciego que
`E6` señala**: ningún escenario de calidad va a ejercitar el crecimiento. La decisión de `C4` sobre
la **extensibilidad del esquema** es lo único que protege esta expectativa, y sigue sin tomarse.

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026
>
> **Resuelta por `B6`: se piensa una forma de negocio diferente y más segura.** Crecer por
> instalaciones —cada una con su cobro y su mensualidad— en lugar de crecer por usuarios de una
> plataforma compartida.

**Lo que cierra:** ✅ **la tensión con `E6` desaparece.** Ya no hay una plataforma multi-tenant cuya
escalabilidad nadie instrumenta: **el crecimiento es comercial —más instalaciones— y no
arquitectónico.** Escalabilidad puede quedarse fuera del top 5 sin punto ciego.

`[!]` **Lo que sí queda, y empeora:** `CN-29` —migraciones de esquema automatizadas sobre `N` bases—
pasa a ser sobre **`N` instalaciones en infraestructura ajena y posiblemente en versiones distintas**.
Es más difícil que sobre `N` bases en nuestra nube. Ver `E2`.

### `B10` · El dispositivo lo pone la empresa, no cada persona — `CN-21`

**Lo que dice el cliente.** **NO** a *«que el dispositivo de captura lo ponga cada persona y no la
empresa»*, con nota **«IDEAL DE LA EMPRESA»**.

**Lo que decimos Juan y Jerónimo.** Ambos **SÍ**. `CN-21` está `EN DUDA`: *«dispositivo de captura
sin especificar y sin partida de hardware»*, y el supuesto que arrastrábamos era BYOD de gama baja.
Nota relacionada: el cliente respondió **NO** a *«que la aplicación funcione con fluidez en
dispositivos de gama baja o de varios años de antigüedad»*.

---

> ### ✅ DECIDIDO · 25-ago-2026
>
> **La flexibilidad que se ofrece es no obligar a invertir en dispositivos nuevos si los trabajadores
> ya tienen celular propio.** Pero **si un dispositivo es muy viejo, es más barato reemplazarlo que
> adaptar el sistema a él.**

**Lo que cierra:** ✅ **coherente con la respuesta del cliente**, que dijo **NO** a *«que la aplicación
funcione con fluidez en dispositivos de gama baja o de varios años de antigüedad»*. Queda una política
clara: **se soporta BYOD, no se soporta gama muy baja**, y `CN-21` sale de `EN DUDA` en cuanto se fije
el mínimo de versión de sistema operativo. **Ese mínimo lo decidimos nosotros.**

### `B11` · La vista geométrica de camas — `DEC-15`

**Lo que dice el cliente.** **NO** a *«ver el estado de las camas sobre una representación gráfica del
bloque y no solo en una lista»*.
Pero en S1 la idea fue **suya**: *«hacer un mapa de las camas que están vacías»* · *«dónde va el
corte, con mapas […] un 50, 60%, con colores. **Mapa de calor.**»*

**Lo que decimos Juan y Jerónimo.** Ambos **SÍ**. `DEC-15` construyó sobre eso la *«vista geométrica:
las camas como rectángulos que muestran cuánta producción sigue viva»*, y `RFP-03` la recoge. La
justificación escrita fue precisamente *«recupera con justificación la idea de mapa de calor del pitch
de S1»*.

---

> ### ✅ DECIDIDO · 25-ago-2026 — **contra la respuesta del cliente**
>
> **La vista geométrica se mantiene, aunque contradiga la última respuesta del cliente.** Se considera
> necesaria.

**Lo que abre:** `[!]` **decisión en contra, la tercera de esta ronda.** Él respondió **NO** a *«ver el
estado de las camas sobre una representación gráfica del bloque»*. **Pero la idea fue suya en S1** —
*«hacer un mapa de las camas que están vacías»*, *«cómo va el corte, un 50, 60%, con colores. Mapa de
calor»*— así que hay con qué defenderla. `DEC-15` y `RFP-03` se mantienen.

> ### 🔄 CORREGIDA · ronda 3 — 25-ago-2026 — **tampoco era una contradicción**
>
> **La vista geométrica se mantiene, y hay que diferenciar dónde vive:**
>
> > **No es durante la captura de información.** Es **dentro de las consultas, en el computador
> > principal donde los dispositivos sincronizan.**

**Lo que corrige:**

1. ✅ **`B11` sale de la lista de decisiones contra el cliente.** Él respondió NO a *«ver el estado de
   las camas sobre una representación gráfica del bloque»* **en el contexto de la captura en campo** —
   y ahí sigue sin haberla. **En la consulta de escritorio nunca dijo que no.**
2. ✅ **Y encaja con la idea que fue suya en S1:** *«hacer un mapa de las camas que están vacías»*,
   *«cómo va el corte, un 50, 60%, con colores. Mapa de calor»* — que era, en efecto, sobre lo que él
   consulta, no sobre lo que el supervisor captura.
3. **`DEC-15` y `RFP-03` se mantienen, con el alcance precisado:** vista geométrica **en consulta**,
   no en captura. Y con `B6`, ese computador principal es el **nodo local de la finca**.

### `B12` · El tablero de supervisión de la jornada

**Lo que dice el cliente.** **NO** a *«ver en una sola pantalla qué dispositivos tienen información
pendiente de sincronizar»* · **NO** a *«ver en una sola pantalla el avance de captura del día por
bloque»* · **NO** a *«que el sistema indique en todo momento cuántas camas faltan por capturar»*.
Pero en S1 la idea también fue suya: *«qué pena, esta semana no se siembran 10 camas y apenas llevan
8 camas y faltan 2 días de trabajo.»*

**Lo que decimos Juan y Jerónimo.** Ambos **SÍ** a las dos primeras.

---

> ### ✅ DECIDIDO · 25-ago-2026 — **las dos primeras, contra la respuesta del cliente**
>
> **Se construyen las dos primeras:** ver en una sola pantalla **qué dispositivos tienen información
> pendiente de sincronizar**, y **el avance de captura del día por bloque**.
> La tercera —indicar en todo momento cuántas camas faltan en la jornada— **no**.

**Lo que abre:**

1. `[!]` **Decisión en contra:** él respondió **NO** a las dos. **Pero la idea también fue suya en
   S1**: *«qué pena, esta semana no se siembran 10 camas y apenas llevan 8 camas y faltan 2 días de
   trabajo»*.
2. ✅ **Y las dos sirven a `B2`.** Un tablero de qué está sin sincronizar es **el instrumento de la
   nueva medida de Rendimiento**: la latencia entre captura y proyección se ve ahí. **Dejan de ser un
   capricho del equipo y pasan a instrumentar el driver #3.**

### `B13` · Soporte sin manual, sin videos y sin registro de problemas

**Lo que dice el cliente.** **NO** a reportar el problema desde la propia app · **NO** a que el
reporte incluya el contexto automático · **NO** a *«que exista un manual de uso escrito»* · **NO** a
*«videos cortos de apoyo dentro de la aplicación»* · **NO** a *«llevar un registro de los problemas
reportados y de cómo se resolvieron»*. **SÍ** a resolver en menos de una hora.

**Lo que decimos Juan y Jerónimo.** **SÍ** a manual, a videos y a reportar desde la app.
`[!]` Consecuencia que ninguno de los dos lados ha mirado: **sin registro de problemas no hay forma de
verificar el compromiso de «menos de una hora» que él mismo pidió.**

---

> ### ✅ DECIDIDO · 25-ago-2026
>
> **La decisión del cliente se toma como correcta.** Sin reporte desde la app, sin contexto automático,
> sin manual escrito, sin videos en la aplicación y **sin registro de problemas**. El canal de soporte
> es la persona en planta.

**Lo que queda abierto:** `[!]` **el cabo que señaló la ronda 1 no se cierra con esto.** Sin registro
de problemas **no hay forma de verificar el compromiso de «menos de una hora»** que el propio cliente
pidió. Con `A1`, ahora existe al menos la traza de **sesiones de sincronización**, que permite ver
*cuándo un dispositivo dejó de sincronizar* — pero no por qué ni cuánto tardó en resolverse.
**El compromiso de la hora sigue sin instrumento.**

> ### 🔄 REFINADA · ronda 3 — 25-ago-2026
>
> **Dependerá de la persona de planta, pero se hará en contra del cliente:** existirá **alguna forma de
> manual o guía corta** — algo con lo que el ingeniero de sistemas de planta pueda interpretar lo
> suficiente **para que el sistema no se caiga**. No un manual de usuario completo: lo mínimo para
> sostener la operación.

**Lo que abre:**

1. `[!]` **Decisión en contra del cliente**, que respondió **NO** a *«que exista un manual de uso
   escrito»*. Se asume a conciencia.
2. ✅ **Y con `B6` deja de ser opcional: pasa a ser crítica.** Bajo local-first **el ingeniero de
   sistemas de la finca opera la instalación**, no solo la usa. Sin una guía mínima, cada incidencia
   vuelve a nosotros — y eso es exactamente el coste que `E2` intenta acotar.
3. **Sigue sin cerrarse el cabo de la ronda 1:** sin registro de problemas, el compromiso de *«menos de
   una hora»* sigue sin instrumento de verificación.

# GRUPO C · El equipo se contradice a sí mismo

> ## ✅ **10 entradas, las 10 decididas** — rondas 3, 4 y 5
>
> Ninguna requería al cliente. **La restricción de arquitectura más importante de todo el documento
> sale de aquí:** `C4` y `C6` juntas obligan a que **los campos capturados sean datos y no columnas**.

---

### `C3` · ~~SaaS, PaaS o IaaS~~ — **CERRADA**

> **CERRADA el 25-ago-2026. FlorLogic es un SaaS multi-tenant, y sale de discusión.**
>
> 🔄 `[!]` **CUESTIONADA en la ronda 2 del mismo día.** `A10` y `A20` describen **local-first con la
> nube como respaldo y servicios**, y `B6` lo plantea de frente: *«¿sigue siendo un SaaS… o no?»*.
> **Se deja marcada, no reabierta**: la decisión de reabrirla es de la ronda siguiente. Ver `B6`.

**Lo que había.** Cuatro posiciones sobre la misma pregunta: S3 (11-ago) sin acuerdo — *«no va a ser
un software as a service, va a ser como infraestructura as a service»* → *«no, eso es un SaaS»* →
*«no nos pongamos con eso ahora mismo»*; Q&A (12-ago) inclinándose a **PaaS**; `DEC-01` (15-ago)
fijando **SaaS multi-tenant**; y el cliente respondiendo **SÍ** a servidores propios.

**Cómo se cerró.** Decisión del equipo: **SaaS multi-tenant, sin despliegue dedicado.** `DEC-01` se
confirma sin cambios. La opción de «fase 1 dedicada, multi-tenant en fase 2» queda descartada.

**Lo que NO cierra.** El **SÍ** del cliente a servidores propios sigue existiendo: no desaparece
porque hayamos decidido lo contrario. Pasa a `B6` como decisión tomada en contra suya, y su
tratamiento comercial está en `E1`. Y las consecuencias operativas de sostener un SaaS están en el
**grupo E completo**.

---

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 — **en sentido contrario al de la mañana**
>
> **El giro está tomado: local-first con servicios en línea.** `DEC-01` (SaaS multi-tenant puro) queda
> **derogada**, no matizada. Lo que la sustituye está en `B6`: **instalación por empresa (~20.000 USD)
> más mensualidad por nube, copias, mantenimiento y servicios.**
>
> **Las cuatro posiciones históricas quedan así:** S3 no tenía acuerdo · la **Q&A del 12-ago acertó**
> al inclinarse a PaaS y por la razón correcta (*no hay investigación de mercado que respalde un
> producto para todos*) · `DEC-01` se equivocó · **y el cliente tenía razón** al pedir servidores
> propios.

`[!]` **Queda escrito que esta entrada se cerró dos veces en sentidos opuestos el mismo día.**
No se borra ninguna de las dos: la primera decisión y su razonamiento siguen arriba. **La lección de
método es que el modelo de entrega se decidió antes de entender por qué el cliente pedía el servidor
propio** — y el motivo real, que apareció en `A20`, no era desconfianza sino **continuidad sin
internet**.

### `C1` · `DEC-06` contra S4, con dos días de diferencia

`DEC-06` (15-ago): *«BI propio y cerrado, antes que integración con terceros. Interoperatividad baja
de prioridad.»* S4 (17-ago): *«un BI propio, **pero con la necesidad obligatoria de hacer
integraciones con otras herramientas de BI**»* y *«al parecer sí vamos a integrar, pero a la vez
ofrecer»*. **Nos contradijimos solos antes de que el cliente respondiera.**

`[!]` **Bajo SaaS esto se endurece, no se ablanda.** Una superficie de lectura externa —el *«POWER
BI»* que pidió el cliente— deja de ser «una vista» y pasa a ser **una vista por empresa, con
credenciales por empresa, sobre `N` bases de datos**. Choca de frente con `RF-012`, que bajo SaaS es
estructural. El coste de `C1` lo paga `E3`.

---

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 (por `B5`)
>
> **BI propio Y posibilidad de integrar otros servicios. Totalmente necesario hacerlo.**
> `DEC-06` queda **derogada**; `CN-10`, `CN-14` y `CN-33` hay que reescribirlas.
>
> `[!]` **Y con `B6` el coste que la ronda 2 temía desaparece.** La superficie de lectura externa
> —el *«POWER BI»* del cliente— **ya no es una vista por empresa sobre `N` bases en nuestra nube**:
> es **una conexión contra su propia instalación local**. Es el escenario más barato posible y deja de
> chocar con `RF-012`.

### `C2` · `DEC-16` contra el registro de S3

S3 (11-ago): *«la decisión de utilizar una inteligencia artificial embebida dentro de un celular es
una decisión **de los desarrolladores**, no es en sí una funcionalidad crítica que necesite el
usuario. **Fue como una idea de venta.**»*
`DEC-16` (15-ago) la devolvió partida en dos: asistente de captura local + IA analítica en la nube.
El propio texto de la decisión se deja la advertencia: *«hasta que haya cita textual del cliente
pidiéndola, esto es propuesta, no confirmado»*.
**Lo único parecido a una cita del cliente es de S1:** *«la idea es que ustedes le suban una capa más
[a Power BI] y le coloquen inteligencia artificial»* — que es **capa analítica, no captura**.
Y el cliente respondió **NO** a voz y **NO** a sugerencias mientras captura.

`[!]` **Bajo SaaS hay una restricción nueva** que `DEC-16` ya anticipaba: *«una IA que consulta datos
de una empresa no puede arrastrar contexto de otra. El aislamiento aplica al modelo, a sus prompts y
a cualquier índice o caché.»* Con multi-tenant confirmado, eso deja de ser una advertencia y pasa a
ser un requisito de `RF-012`.

---

> ### ✅ RESUELTA · ronda 4 — 25-ago-2026 — **la IA vuelve, y ahora con razón de negocio**
>
> **El asistente de captura por IA entra.** El razonamiento cambia por completo respecto a S3:
>
> - **Es una instalación de valor.** Nuestro valor inicial es **lo que le entregamos a la empresa para
>   que se quede con ello** — y con `B6` eso es literalmente el producto que se cobra.
> - **Agrega mucho valor de negocio y no sale caro.**
> - **La IA se entrena dentro de su propio entorno de trabajo.**

**Lo que cierra:**

1. ✅ **`DEC-16` se reabre y se resuelve.** `RFP-05` (asistente de captura offline) y `CN-31`
   (vocabulario restringido, nunca escritura silenciosa) **salen de suspenso**. `PR-01` sigue
   mandando: *propone, el sistema valida, el usuario confirma.*
2. ✅ **Y desaparece el choque que `DEC-16` se dejó escrito.** Su advertencia era *«una IA que consulta
   datos de una empresa no puede arrastrar contexto de otra; el aislamiento aplica al modelo, a sus
   prompts y a cualquier índice o caché»*. **Con entrenamiento en el entorno del propio cliente, el
   aislamiento es físico:** cada instalación tiene su modelo y sus datos. Refuerza `E3`.
3. ✅ **`app-captura/` recupera justificación de producto** — pero no la que tenía. **No se justifica
   por velocidad**: `B2` demostró que el valor está en la sincronización, no en capturar más rápido.
   **Se justifica por calidad del dato y por accesibilidad.**
4. **Y da respuesta técnica a lo que el cliente sí pidió en `A18`:** que lo pueda usar alguien con poca
   experiencia en celulares o con dificultad para leer textos largos. Eso vive en **usabilidad**
   (puesto 7), no en accesibilidad.

**Lo que abre:**

1. `[!]` **Es una decisión en contra del cliente — la séptima.** Él respondió **NO** a dictar por voz,
   **NO** a sugerencias mientras captura, **NO** a captura guiada pantalla por pantalla y **NO** a
   proponer el valor de la última vez. **Hay que llevársela.**
   **Matiz que ayuda:** lo único que él **sí** pidió —*que la mayoría de los datos se escojan de una
   lista y no se escriban*— es exactamente lo que un asistente con vocabulario cerrado hace bien.
   Y `B3` ya decidió que la voz entra como valor agregado de prioridad baja, solo para transcribir.
2. `[!]` **Falta decidir dónde corre el modelo, y no es menor.** *«Se entrena dentro de su propio
   entorno de trabajo»* admite dos lecturas con costes muy distintos:
   **en el nodo local de la finca** —hace falta hardware, y eso encarece la instalación— **o en el
   dispositivo** —hace falta un modelo pequeño y limita lo que puede hacer sin conexión—.
   **Sin decidir.**
3. **`E2` gana un componente más.** Si la instalación incluye una IA que se entrena en casa del
   cliente, el coste de instalar y de sostener sube. `E2` sigue sin número.

### `C4` · Escalabilidad: al fondo en S3, central en `DEC-01` — **RESUELTA en cuanto al ranking**

En S3 la pusimos casi última: *«no es un proyecto que se estime para crecer […] porque no va a ser un
software as a service.»* **Ese argumento queda retirado: sí va a ser un software as a service.**

**Resuelto.** Escalabilidad queda donde la deja la hoja `2. Priorización-QA`: **25 puntos, puesto
9-10** — por encima de donde la dejó S3, y **fuera del top 5**, porque los drivers se eligieron por
lo que el usuario final puede medir (`§0.3`).

**Lo que sigue vivo de `C4`, y no es el ranking:**

1. **El empate `Interoperatividad / Escalabilidad` (25 y 25) sigue sin romper.** `§9.1` manda
   romperlo con el patrón de respuestas: Escalabilidad **11 SÍ de 11**; Interoperatividad **10 SÍ y
   3 NO**. Por patrón gana Escalabilidad; por el criterio de `§0.3` —medible por el usuario final—
   gana Interoperatividad, que es la que el usuario toca (Excel, PDF, Power BI). **Sin decidir.**
2. **La extensibilidad del modelo de datos no es negociable aunque el atributo no sea driver.** El
   cliente dijo **SÍ** a *«que agregar un tipo de labor o de medición nueva no exija rehacer la
   captura existente»*. Eso no es infraestructura: es diseño de esquema, y hay que decidirlo **antes
   de la primera tabla**.
3. **La «Votación por rol» sigue con una sola celda diligenciada.** Decidir si se completa o se
   descarta.

---

> ### ✅ RESUELTA · ronda 5 — 25-ago-2026 — **los tres puntos**
>
> **1 · El empate se rompe: gana Interoperatividad.** Queda en el puesto 9 y Escalabilidad en el 10.
> **El ranking de `2. Priorización-QA` queda cerrado del todo.**
>
> **2 · La extensibilidad es obligatoria.** Agregar un tipo de labor o información de captura nueva
> **no puede modificar lo previo ni obligar a rehacer la captura existente** — con el matiz de que
> *depende de la información solicitada*.
>
> **3 · La votación por rol ya está hecha.** Se realizó con **supervisor de campo, gerente de
> producción e ingeniero en sistemas de la finca** — que son exactamente las tres columnas de
> `2. Priorización-QA`.

**Lo que cierra:**

1. ✅ **El empate era el último cabo del ranking**, y se rompe con coherencia: Interoperatividad es lo
   que el usuario final toca —Excel, PDF, la nota literal **«POWER BI»**— y ese fue el criterio con el
   que se eligieron los drivers.
2. ✅ **La «Votación por rol» deja de ser un pendiente.** La celda solitaria de la hoja era un resto,
   no un ejercicio a medias: **el ejercicio es la propia hoja de priorización.** Y con `E5` ya sabemos
   que **no hace falta una cuarta columna** para el operador.

**Lo que fija — y es una restricción de arquitectura, no una preferencia:**

> `[!]` **Junto con `C6`, esto obliga a que los campos capturados sean DATOS y no COLUMNAS.**
> Si añadir una labor nueva exigiera una columna nueva, exigiría una migración de esquema **en `N`
> instalaciones dentro de casa de clientes**. Con el modelo de `B6` eso es lo más caro que puede
> pasarle al proyecto. **El esquema tiene que absorber campos nuevos sin migrar, y hay que decidirlo
> antes de la primera tabla.**
>
> El mitigante existe y lo trae `E2`: **el sistema de actualización en línea**. Pero un mecanismo de
> actualización no sustituye a un esquema que no necesite migrar.

### `C5` · Seguridad bajada por «entorno cerrado» — y `CN-28` sin cerrar

S3: *«no creo que si uno hace este proyecto para un entorno cerrado tenga que preocuparse de ataques
constantes, como si fuera un Instagram, un GitHub […] entonces la parte de seguridad se puede reducir
o bajar»*, con el alcance definido como *«instalado dentro de empresas para el uso de empresas»*.
En la misma sesión se dijo el contraargumento y no se resolvió: *«acuérdese que algo que nos dio el
cliente es que los datos son muy celosos.»*

> `[!]` **Con SaaS confirmado, el argumento de «entorno cerrado» queda muerto sin discusión
> posible.** No hay entorno cerrado: hay una plataforma que **nosotros operamos** para empresas que
> **son competidoras entre sí**. `RF-012` es estructural y de fase 1, y `CN-03` (secreto empresarial,
> art. 260 Decisión 486 CAN) es su base legal.

**Y `CN-28` pierde su salida fácil.** En un despliegue dedicado la clave de cifrado habría quedado en
la finca de forma natural. **Bajo SaaS la custodia la asume el operador, es decir, nosotros.** Esa es
exactamente la situación que el cliente describió en S1 como la primera objeción que le pondrían:

> *«las empresas de flores son muy celosas en su información. Una de las primeras cosas que le van a
> decir: "bueno, puede hacer un plan de membresía, pero **usted cómo me va a controlar a saber que
> usted no le da la información mía a otro cualquiera de flores**".»*

`CN-28` sigue `EN DUDA` y ahora es **la más urgente de las dos que quedan**. Ver `E7`.

---

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 (por `B4` y `B6`)
>
> **El argumento de «entorno cerrado» se retira igual** —nunca fue el argumento correcto: la seguridad
> de este sistema es sobre confidencialidad y sobre quién ve qué, no sobre ataques externos.
>
> **Y `CN-28` se cierra con la opción que bajo SaaS era imposible:** cifrado en tránsito y en reposo,
> **con la llave del lado del cliente** (`B4`). Los respaldos que suben a la nube van cifrados con esa
> llave. **`CN-28` sale de `EN DUDA`.**
>
> Lo que sigue sin construirse, porque el cliente dijo NO: aislamiento demostrable documentalmente,
> registro de todo acceso técnico y registro de cada exportación.

### `C6` · `RF-001` y `RF-002` están escritos sobre un modelo que ya invalidamos

Ambos hablan de *«cantidad de esquejes»*. `DEC-14` dice **«nada se cuenta por esqueje»**. Y `§4.3`,
leyendo el formato real, va más lejos: la unidad de campo no es el área ni el esqueje, **son las
líneas**: el formato tiene `#LÍNEAS` y `CANTIDAD` y **no tiene ninguna columna de área ni de
densidad**. Están pendientes de reescribir. *(Sin impacto de la decisión SaaS.)*

---

> `[!]` **`A14` y `A16` cambian el marco.** La captura es **incremental por sección** —se puede
> registrar una sola subvariedad de una cama y dejar el resto en blanco— y los dos formatos **se
> fusionan en una plantilla única personalizable por tenant**. La reescritura de `RF-001`/`RF-002`
> tiene que partir de ahí, y depende de `D7`, `D8` y `D9`.

> ### ✅ RESUELTA · ronda 5 — 25-ago-2026
>
> **Las líneas son una forma de agrupación, no la unidad.**
>
> **Densidad por metro · cantidad por líneas · unidades por tallo — todo esto debe estar contemplado
> como parte del proyecto.** El sistema soporta varias formas de agrupar y de contar, y cada finca usa
> la suya.

**Lo que cierra y lo que cambia:**

1. ✅ **`RF-001` y `RF-002` se reescriben sobre esa base**, y ya no hay que elegir entre las opciones
   (a) y (b) de la propuesta: **se capturan las dos cosas —agrupación y cantidad— porque la unidad es
   configurable.** El formato real de la finca usa `#LÍNEAS` y `CANTIDAD`, y son un caso de esto.
2. ✅ **`D7`, `D8` y `D9` bajan de bloqueante a dato de configuración.** Ya no hace falta saber qué es
   exactamente una «línea» **para diseñar el modelo** — solo para **configurar este tenant**. Es una
   mejora grande: dejaban bloqueada `A16` (la fusión de los dos formatos) y ya no.
3. **Encaja con `DEC-14`** (la sección es donde vive el dato de siembra), con `DEC-15` (dos lecturas de
   la producción) y con el hallazgo documental de `§4.3` (en el campo se cuentan líneas, no metros).
4. `[!]` **Y es la misma exigencia que `C4`:** agrupaciones y unidades configurables **son datos, no
   columnas.** Las dos entradas piden lo mismo desde dos sitios distintos.

### `C7` · `CN-02` contra el pitch de suscripción — **se invierte bajo SaaS**

`CN-02`: ~20.000 USD de desarrollo. Pitch de S1: *«10 USD por mes por usuario»* y *«a partir de 20
empresas obtenemos la rentabilidad»*. `DEC-02` los explica como dos momentos distintos.

> `[!]` **Bajo SaaS esto se invierte respecto a lo que se había propuesto.** La suscripción deja de
> ser un asunto de fase 2: **es el modelo de negocio**. En consecuencia:
>
> - **`CN-11` (pasarela de pago, PayU) vuelve a alcance** y sigue `EN DUDA`.
> - **`CN-05` (qué se cobra y por qué unidad) pasa de "abierta" a BLOQUEANTE.** No se puede diseñar
>   el modelo de datos de una plataforma de suscripción sin saber si la unidad de cobro es el
>   usuario, la finca, la cama o el tallo.
> - **`CN-05` en su otra acepción —el presupuesto de mantenimiento, desconocido— se vuelve crítica.**
>   Ver `E2`.

La tensión de fondo sigue intacta: `CN-02` son ~20.000 USD de construcción, y el ingreso recurrente
solo aparece *«a partir de 20 empresas»* — un umbral que nadie ha validado.

---

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 (por `B6`)
>
> **`CN-02` deja de ser «el presupuesto de desarrollo» y pasa a ser el precio de instalación:
> ~20.000 USD por compañía.** Más una **mensualidad por servicios, copias y soporte**.
>
> - **`CN-11` (pasarela de pago) sigue haciendo falta** — hay mensualidad — pero para un cobro
>   recurrente por empresa, no por usuario. Mucho más simple.
> - **`CN-05` queda resuelta en su acepción de unidad de cobro: la unidad es la instalación.**
>   Sigue abierta en su otra acepción —el presupuesto de mantenimiento— que es `E2`.
>
> `[!]` **Y los números por fin cierran.** El pitch de 10 USD/usuario/mes daba ~230 USD al mes por una
> finca con 3 capturadores y 20 consultores. **20.000 USD por instalación más mensualidad de servicios
> es otro orden de magnitud**, y no depende de alcanzar «20 empresas» para ser viable.

### `C8` · El umbral del 2% — un número para dos conceptos

Usamos **2%** para dos cosas distintas: el **error de captura declarado** por el cliente en S2 (con
meta 0%), y una **tolerancia sobre la razón `cantidad / #líneas`** en la demo. `§4.3` lo dice
explícitamente: *«el umbral del 2% es una decisión nuestra, no un hallazgo»*. Con ese umbral la demo
marca 9 filas del histórico real. *(Sin impacto de la decisión SaaS.)*

---

> ### ✅ RESUELTA · ronda 5 — 25-ago-2026 — **y el 2% desaparece por diseño**
>
> **El 2% del cliente es el error de transcripción de papel a sistema.**
>
> > Como el paso de papel y lápiz al sistema **no es automático**, un 2% de la información **se pierde
> > o queda errónea — y se enteran muy tarde en el proceso.**

**Lo que cierra:**

1. ✅ **Los dos «2%» quedan separados de forma definitiva**, y no eran comparables: el del cliente es
   una **tasa de error de un proceso manual**; el de la demo es una **tolerancia sobre la razón
   `cantidad / #líneas`** que nos inventamos nosotros.
2. ✅ **Y el 2% del cliente no se mide: se elimina.** Si no hay transcripción manual, no hay error de
   transcripción. **Por eso el cliente rechazó en `A14` todos los instrumentos para medirlo** —
   indicador de porcentaje de error, distinguir verificado de no verificado, excluir lo no
   verificado—: para él no es un indicador que haya que vigilar, **es un problema que el sistema hace
   desaparecer.** `A14` cierra del todo con esto.
3. ✅ **«Se enteran muy tarde» es el mismo dolor de `B2`.** El 2% y los 8 días de latencia son el mismo
   problema visto desde dos lados: **el dato malo existe y nadie se entera a tiempo.** Las dos
   entradas describen el beneficio central del producto.

**Lo que sigue abierto, y es solo nuestro:** la **tolerancia de razón** de la demo sigue siendo un
número inventado por el equipo. `D17` y `D18` se mantienen como preguntas al cliente. **Y la regla de
presentación no cambia:** si se le enseña la demo, las 9 filas marcadas se presentan como *«filas que
llaman la atención con un criterio que pusimos nosotros»*, nunca como errores detectados.

### `C9` · `RF-017` y el operador de la plataforma — **el rol existe**

`RF-017` dice que solo el administrador puede modificar un registro sincronizado. `DEC-01` advirtió:
*«si eso incluyera al operador, el equipo FlorLogic estaría tocando datos productivos de un cliente.
**Problema contractual, no técnico**»*. `DEC-09` lo acotó a acceso de infraestructura. **La cláusula
nunca se redactó.**

> `[!]` **Bajo SaaS el Operador de plataforma existe desde el día uno** y la ambigüedad de `RF-017`
> es real, no hipotética. Hay que resolver tres cosas, y ninguna es opcional:
>
> 1. **Precisar `RF-017`** para que diga «administrador **de la empresa**», sin lectura posible que
>    incluya al operador.
> 2. **Redactar la cláusula contractual** que `DEC-01` reclamó y nadie escribió.
> 3. **Resolver la contradicción que `DEC-09` se dejó abierta:** *«una copia de seguridad contiene
>    los datos del tenant. "Acceso indirecto" solo es real si los respaldos van cifrados con una
>    clave que el operador no pueda usar»* — y bajo SaaS **el operador es quien custodia la clave**.
>    Es decir: `DEC-09` se contradice a sí misma en cuanto se aplica el modelo elegido.

---

> `[!]` **`A1` lo agrava.** Sin marcas de tiempo y sin registro de acceso técnico, **la
> verificación manual que `A1` deja en manos del cliente no puede detectar nada hecho desde la
> plataforma.** El operador existe (SaaS) y no deja rastro auditable.

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 (por `B6`), **con un residuo**
>
> **Bajo local-first el Operador de plataforma se encoge hasta casi desaparecer** del lado operativo:
> **quien administra la instalación es el ingeniero de sistemas de la finca**, empleado del cliente
> (`A20`), que ya existe y que el propio cliente identificó como superusuario en S2.
>
> - **`RF-017` se precisa igual:** «administrador **de la empresa**».
> - **`DEC-09` deja de contradecirse.** Su condición —*«los respaldos cifrados con una clave que el
>   operador no pueda usar»*— **ahora se cumple de verdad** gracias a `B4`.
> - **Nosotros operamos solo la capa de servicios en línea**, y sobre datos que llegan cifrados.
>
> `[!]` **El residuo, que no desaparece:** durante **desarrollo, instalación y soporte** vamos a tener
> acceso a datos reales de producción (`CN-07`). **La cláusula de acceso de implantación sigue
> haciendo falta** — es más pequeña que antes, pero no es cero.

### `C10` · Accesibilidad se definió distinto — **RESUELTA por `A18`**

Al cliente, en S2: *«falta de digitalización, analfabetismo, algunos ciegos»*. Entre nosotros, en S3:
*«conforme a las discapacidades visuales y auditivas»*. **Puntuamos dos cosas distintas y las
comparamos como si fueran la misma.**

> ### ✅ RESUELTA · 25-ago-2026
> **Se adopta la definición del equipo: Accesibilidad = limitantes físicas.** La facilidad de uso para
> personas con poca alfabetización digital pasa a **usabilidad**, no a accesibilidad.
> **Es lo contrario de lo que proponía la v1.0 de `4_CIERRES_GRUPO_C.md`**, y queda así por decisión.
> El reparto de los 8 SÍ del cliente entre los dos atributos está en `A18`.

---

# GRUPO D · Nunca se preguntó

> **18 abiertos y 3 resueltos** (`D4`, `D20` y `D21`). Ninguno es culpa del
> cliente: son preguntas que no se hicieron. ⬆ marca las que **subieron de prioridad** en la sesión.

| ID | Qué falta | Bloquea | Estado |
|---|---|---|---|
| **`D1`** | **El proceso de captura a detalle nunca se trabajó con el cliente.** Pide una SESIÓN, no un dato | `RFP-01`..`RFP-05`; deja `RF-001`/`RF-002` sobre un modelo invalidado | `BR-N6` · **la más importante** |
| **`D2`** | **Los documentos prometidos no han llegado:** plan de siembra, presupuestos, formatos en papel llenos, tabla de grados, histórico | **Todo** | `BR-N3` · quedó sin responder ya en S1-Q P44 |
| **`D3`** | **De dónde sale el % de productividad por variedad**, y **cómo se reparten los tallos en los ~7 días de corte** | **El último hueco del motor** | `BR-23` · el equipo la llamó en S4 *«esto es muy importante»* |
| ~~**`D4`**~~ | ~~A qué nivel de agregación se miden el −6% y el ±10%~~ | — | ✅ **RESUELTA por `A19`.** Miden cosas distintas: el ±10% es la banda de proyección en tallos; el −6% es la brecha de margen en dinero |
| **`D5`** ⬆ | **El sistema actual sigue sin identificarse:** nombre, alcance, qué guarda, quién lo administra | Si FlorLogic reemplaza, alimenta o convive. `CN-20` **BLOQUEANTE** | `BR-N2` · **sube tras `A7`:** ahora sabemos que cubre venta y facturación |
| **`D6`** | **Catálogo de grados y calidades de la finca** | Con qué granularidad se registra el corte | `BR-11` |
| **`D7`** ⬆ | **Qué significa la columna `OBSE`** de su formato: alterna 325 y 425 sin patrón | El modelo de datos de la captura | Abierta desde `§4.3` |
| **`D8`** ⬆ | **Qué es exactamente una «línea»** | La fórmula de campo `#líneas × plantas_por_línea` | En S4: *«esa me la tienes que explicar»* |
| **`D9`** ⬆ | **Qué significan `lote`, `calibre`, `proveedor`, `contenedor`** en «Programa de siembras» | La segunda plantilla de captura | S4: *«queda pendiente de preguntarle al cliente»* |
| **`D10`** | **Si una cama puede dividirse entre FLORES distintas**, no solo variedades | `DEC-14`, `RFP-01` | S4: *«ya enseguida pregunto»* — no consta que se preguntara |
| **`D11`** | **Si nave y bloque son lo mismo** | La jerarquía `H-04` | En el formato real **no aparece la nave** |
| **`D12`** | **¿Puede una misma cama ser registrada por dos capturadores la misma fecha?** | Cuánta complejidad hace falta en la sincronización | `BR-N4` · **no preguntada** |
| **`D13`** | **Nomenclatura de camas:** si los códigos se repiten entre bloques y si cambian al renovar | **Es la llave primaria de todo el sistema** | S1-Q P09 quedó sin responder |
| **`D14`** | **Cuánto dura el paralelo con el papel**, que él mismo aceptó | El plan de puesta en marcha y `CN-07` | Nunca preguntado |
| **`D15`** | **Qué exigencia sacrificaría por velocidad.** *Él respondió que sí sacrificaría algo y nadie preguntó cuál* | Puede reordenar todo el ranking | **Pregunta de un minuto** · **es también el punto de partida de `A17`** |
| **`D16`** | **Tiempo máximo aceptable de confirmación de captura en campo** | Convierte *«que sea fácil»* en verificable | `BR-N1` · **reformulada por `B2`:** la pregunta ya no es cuánto tarda capturar, sino **cuánto puede tardar un dato en llegar a la proyección** |
| **`D17`** | **Si `Astroi` y `Rose` son errores de digitación o densidades distintas** (tres razones distintas cada una, mismo día) | La regla de validación de la demo | Abierta desde `§4.3` |
| **`D18`** | **Si el umbral del 2% de tolerancia sobre plantas/línea es correcto** | La regla de la razón | Ver `C8` |
| **`D19`** | **Planeación nunca se exploró** — y es donde nace la proyección | El motor entero | Él cerró la puerta: *«no, eso no»* |
| ~~**`D20`**~~ | ~~Qué ventana de mantenimiento es aceptable~~ | — | ✅ **RESUELTA por `A2` ronda 2.** Fallo no planificado: **menos de 1 hora**. Mantenimiento programado: **mayo, temporada baja**, y ahí vale parar incluso en horario intocable |
| ~~**`D21`**~~ | ~~Dónde SÍ hay señal~~ | — | ✅ **RESUELTA por `A6`.** No hay señal en ninguna parte del cultivo; **sí en las oficinas** |

---


# GRUPO E · Consecuencias abiertas de la decisión SaaS

> ## ✅ **7 entradas, las 7 decididas** — rondas 3 y 5
>
> Nacieron como el trabajo que creaba **el SaaS multi-tenant**. Al cambiar el modelo a **local-first
> con servicios en línea** (`B6`), cinco se resuelven y dos cambian de naturaleza sin cerrarse.
>
> **Se conserva íntegro el enunciado original de cada una**, escrito cuando el modelo era SaaS, para
> que se vea qué problema creaba aquel modelo y cuál crea este.

---

### `E1` · Qué se le ofrece al cliente a cambio del on-premise que pidió

**Situación.** Se ha decidido SaaS en contra de una respuesta explícita suya. Ir a la sesión sin una
contraoferta preparada es ir a perder la discusión o a ceder mal.

**Lo que dice el cliente.** **SÍ** a *«que el sistema pueda instalarse en servidores de la propia
empresa y no solo en la nube»*. **SÍ** a *«poder llevarse toda la información en un formato utilizable
si se termina la relación con el proveedor»*.
Pero en S1 argumentó **a favor de la nube**, y con un motivo operativo suyo: *«la idea es que ellos
migren a la nube […] porque el servidor en cierto punto es bueno, pero **también necesita un ingeniero
de sistemas ahí, necesita una cantidad de cosas**.»*

**Lo que decimos Juan y Jerónimo.** Juan **NO** a ambas, Jerónimo **SÍ** a on-premise. Decisión final:
SaaS.

**Lo que hay que preparar, y no está preparado.** La segunda de sus dos peticiones **sí se puede
conceder entera y conviene concederla en voz alta**: exportación completa de sus datos en formato
utilizable, en cualquier momento y al terminar la relación, con plazo y formato comprometidos. Es la
respuesta directa a *«cómo me va a controlar»*, y hoy no existe como requisito. **Falta decidir el
plazo y el formato.**

---

> `[!]` **`A20` cambia la contraoferta entera, y a mejor.** Lo que el cliente busca con el servidor
> propio **no es desconfianza: es continuidad sin internet.** La captura ya lo hace (`CN-13`). Lo que
> falta es **la consulta de información caliente sin conexión** — que él respondió NO por escrito, y
> que puede resolverse con una caché local en vez de un servidor. **Sin decidir, y es la diferencia
> entre cumplir lo que pide y reabrir `C3`.**

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 — **la pregunta se disuelve**
>
> **Ya no hay que ofrecerle nada a cambio: se le da lo que pidió.** Con `B6`, **el despliegue en su
> propia infraestructura ES el producto**, no una concesión.

**Lo que sigue haciendo falta, y ahora por otra razón:** la **salida de datos** que él también pidió
—*«llevarse toda la información en un formato utilizable si se termina la relación»*— **deja de ser
una contraoferta y pasa a ser una obligación contractual del servicio de nube**: cuando alguien deja
de pagar la mensualidad, se lleva sus copias. **Falta decidir plazo y formato**, que es lo único que
queda vivo de esta entrada.

### `E2` · La carga operativa del SaaS no está costeada — **bloqueante**

**Situación.** Operar un SaaS no es desplegarlo. Con SaaS confirmado, el equipo se compromete a:
disponibilidad **24×7** los siete días · resolución de incidencias en **menos de 1 hora** ·
**soporte en horario extendido durante temporada alta** · respaldos automáticos **diarios** ·
custodia de claves · **migraciones de esquema automatizadas sobre `N` bases** (`CN-29`) · y absorber
**picos de temporada simultáneos entre empresas** (`CN-30`, +60%).

**Lo que dice el cliente.** Pidió todo lo anterior: **SÍ** a 24 horas, **SÍ** a 7 días, **SÍ** a
resolver en menos de una hora, **SÍ** a horario extendido en temporada alta, **SÍ** a respaldos
diarios, **SÍ** a *«que una falla que afecte a una finca no afecte a las demás fincas o empresas»*.

**Lo que decimos Juan y Jerónimo.** Nada, todavía. `CN-02` son ~20.000 USD **de construcción** y
`CN-05` dice literalmente que **se desconoce el presupuesto de mantenimiento**. `CN-06` dice que somos
dos ingenieros sin experiencia en el sector. **Nadie ha puesto un número al coste de operar esto ni
ha dicho quién está de guardia a las 3 de la mañana en temporada de San Valentín.**

`[!]` Es el bloqueante nuevo más serio del proyecto y **no depende del cliente**.

---

> `[!]` **`A2` la alivia de forma importante.** El «24×7» del cliente no era un objetivo de
> 99,999%: es **captura y sincronización activas todo el día, con ventana de mantenimiento anunciada**.
> La guardia permanente deja de ser necesaria. **No la cierra:** respaldos diarios, custodia de claves
> y migraciones sobre `N` bases siguen sin costear.

> ### 🔄 TRANSFORMADA · ronda 3 — 25-ago-2026 — **sigue abierta, y sigue siendo el bloqueante nº 1 propio**
>
> **Baja mucho, pero no desaparece: cambia de forma.**

**Lo que se quita de encima con `B6`:** operar una plataforma 24×7 para `N` empresas · absorber picos
simultáneos entre tenants (`CN-30` cae a **10 concurrentes por instalación**, no ~200) · custodiar
claves ajenas (`B4` las deja del lado del cliente) · y `A2` ya había quitado el objetivo de 99,999%.

**Lo que aparece en su lugar, y no está costeado:**

| Coste nuevo | Por qué |
|---|---|
| **Instalar y mantener `N` instalaciones locales** | Cada una en infraestructura ajena, con su ingeniero de planta y su ritmo de actualización |
| **`CN-29` empeora** | Migraciones de esquema sobre `N` instalaciones **en versiones potencialmente distintas**. Es más difícil que sobre `N` bases propias |
| **Servicios en línea** | Copias, sincronización, IA analítica: menos que una plataforma completa, pero no cero |
| **Soporte con horario extendido en temporada alta** | El cliente lo pidió y sigue en pie |

`[!]` **Sigue sin haber un número.** `CN-05` en su acepción de *presupuesto de mantenimiento* sigue
declarada como desconocida, y ahora **hay una mensualidad que fijar**: no se puede poner precio a un
servicio cuyo coste nadie ha calculado. **Es lo primero que hay que hacer de lo que no depende del
cliente.**

> ### ✅ RESUELTA · ronda 5 — 25-ago-2026
>
> **No habrá SaaS, así que el grueso del costeo no aplica.** Los productos en línea y el servicio de
> copias **se costearán y medirán más adelante en el proyecto**: no hay que tenerlo en cuenta ahora
> porque **no hay SaaS de primeras**.
>
> **Lo que sí hay que costear desde ya es la copia de seguridad y el servicio de IA que se ofrece.**
> Cifra de partida: **100 a 200 USD al mes** para soporte y esos servicios iniciales.
>
> **Y más adelante hay que pensar un sistema de actualización en línea** que permita actualizar el
> sistema a las compañías que paguen la mensualidad.

**Lo que cierra:**

1. ✅ **`CN-29` deja de ser un agujero.** Yo lo había marcado como *«empeora»* —migrar el esquema en
   `N` instalaciones ajenas con versiones distintas—. **El sistema de actualización en línea es
   exactamente el mecanismo que faltaba.**
2. ✅ **Y crea el incentivo del modelo de negocio, que es lo elegante de la decisión:** *pagas la
   mensualidad → recibes actualizaciones.* La mensualidad deja de ser solo soporte y copias: **es el
   canal por el que el producto sigue vivo en casa del cliente.**
3. ✅ `CN-05` queda resuelta en sus dos acepciones: **unidad de cobro** (la instalación, `C7`) y
   **presupuesto de mantenimiento** (100–200 USD/mes de partida, a afinar).

**Los dos riesgos que quedan escritos, sin cerrar la entrada:**

> `[!]` **La cifra hay que revisarla antes de firmar.** 100–200 USD al mes tiene que cubrir
> infraestructura de copias, el servicio de IA, el soporte con horario extendido en temporada alta y
> el tiempo de dos personas. Con una o dos instalaciones puede cubrir; **con quince, hay que volver a
> hacer la cuenta.**
>
> `[!]` **Quien no paga se queda sin actualizaciones — y eso es justo lo que `CN-29` teme.** Las
> instalaciones que dejen de pagar **divergirán de versión**, y ahí vuelve el problema de mantener `N`
> esquemas distintos. **Hay que decidir qué pasa con una instalación que deja de pagar:** se congela,
> se degrada, o se acuerda una versión mínima soportada.

### `E3` · `RF-012` vuelve a fase 1, y hay que probarlo sin un segundo cliente

**Situación.** Bajo SaaS, el aislamiento entre empresas deja de ser diferible. Pero `DEC-02` mantiene
que la primera entrega se valida con **una sola finca**, y ya admitía que `RF-012` habría que
construirlo *«con pruebas sintéticas»*.

**Lo que dice el cliente.** **SÍ** a *«que la información de una empresa nunca pueda ser vista desde
otra empresa, bajo ninguna circunstancia»*. Pero **NO** a *«poder demostrar documentalmente ese
aislamiento ante el cliente»*.

**Lo que decimos Juan y Jerónimo.** `RF-012` es *«garantizar que ningún usuario acceda, por ningún
canal, a datos de otra empresa»*, con `CN-03` como base legal y `CN-12` (RBAC evaluado contra el par
*rol, empresa*) como mecanismo. `DEC-11` fija una base de datos por empresa.

**Lo que falta decidir.** Cómo se prueba un aislamiento que en fase 1 no tiene una segunda empresa
real que lo ejercite. La medida barata y disciplinada —**que toda consulta lleve el discriminador de
empresa desde el día uno, con una prueba automatizada que falle si falta**— sigue sin adoptarse.
`[!]` *«Por ningún canal»* incluye el BI de `C1`, los exports de `RF-019` y la IA de `C2`.

---

> ### 🔄 TRANSFORMADA · ronda 3 — 25-ago-2026 — **se parte en dos, y la mitad grande se resuelve sola**
>
> **En los datos operativos, el aislamiento pasa a ser físico.** Cada empresa tiene **su instalación y
> su base de datos, en su propia infraestructura**. `RF-012` deja de ser un problema de diseño de
> consultas y pasa a ser una consecuencia del despliegue. **Y `DEC-11` —una base por empresa, esquema
> común— encaja sin cambiar una letra.**
>
> **Lo que sigue vivo, y es la parte pequeña:** la **capa de servicios en línea** sí es compartida —
> copias, sincronización, IA analítica—. Ahí `RF-012` sigue aplicando entero, incluido *«por ningún
> canal»*: el BI de `C1`, los exports de `RF-019` y **la IA de `C2`, que no puede arrastrar contexto de
> una empresa a otra**.

**Lo que ya no hace falta:** probar el aislamiento con **tenants sintéticos**, que era el problema que
`DEC-02` dejaba sin resolver. **Con `B4`, los datos que llegan a la nube ya vienen cifrados con la
llave del cliente**, así que el aislamiento de esa capa se apoya en criptografía y no solo en código.

`[!]` **La disciplina barata sigue mereciendo la pena:** que toda consulta lleve el discriminador de
empresa aunque solo haya una. **Cuesta lo mismo ahora y protege el día que alguien pida multi-tenant.**

> ### ✅ RESUELTA · ronda 5 — 25-ago-2026
>
> **Se adopta la disciplina barata:** que **toda consulta lleve el discriminador de empresa desde el
> día uno**, aunque en cada instalación solo haya una — **con una prueba automatizada que falle si
> falta**.

**Lo que cierra:**

1. ✅ **Cuesta lo mismo hoy y protege después.** Es lo único que convierte un futuro multi-tenant —si
   alguna vez se pide— en **un despliegue y no en una reescritura**.
2. ✅ **Y protege ya, no solo en el futuro.** `RF-012` sigue vivo en la **capa de servicios en línea**,
   que sí es compartida: copias, IA analítica y sincronización. *«Por ningún canal»* incluye el BI de
   `C1`, los exports de `RF-019` y **la IA de `C2`** — que con entrenamiento por instalación ya está
   aislada físicamente, pero cuyos índices y cachés en la nube no lo están por sí solos.
3. **En los datos operativos el aislamiento ya es físico** (una instalación por empresa) y **lo que
   sube a la nube va cifrado con la llave del cliente** (`B4`). El discriminador es la tercera capa,
   la barata.

### `E4` · La unidad de cobro condiciona el modelo de datos — **bloqueante**

**Situación.** `CN-05` (qué se cobra y por qué unidad) pasa de abierta a bloqueante, porque el modelo
de datos de una plataforma de suscripción depende de ella.

**Lo que dice el cliente.** *«ustedes ofrecerán 10 USD por mes por usuario»* y *«a partir de 20
empresas obtenemos la rentabilidad»* (S1). Y el contexto que lo condiciona: *«los cultivos de flores
en cierto punto no tienen mucho dinero, pero requieren mucha información»*.
Dato incómodo del mismo cliente: en su finca **solo 3 personas capturan** y ~20 consultan. A 10
USD/usuario/mes eso son ~230 USD al mes por finca.

**Lo que decimos Juan y Jerónimo.** `CN-11` (PayU) vuelve a alcance, `EN DUDA`. Nadie ha decidido si
la unidad es el usuario, la finca, la cama o el tallo.

**Lo que hay que construir igual, decida lo que se decida:** registro de usuarios activos por empresa
con fecha de alta y baja. Es una tabla, y sin ella no se puede facturar por usuario ni reconstruir el
histórico después.

---

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 (por `B6` y `C7`)
>
> **La unidad de cobro es la instalación, no el usuario.** ~20.000 USD por compañía instalada, más
> mensualidad de servicios.
>
> ✅ **Con eso desaparece el problema que esta entrada señalaba:** que en una finca con **3
> capturadores** el cobro por usuario diera ~230 USD al mes. **El número de personas deja de ser la
> variable del negocio.**
>
> **Y el umbral de «20 empresas» deja de ser una condición de viabilidad:** con cobro por instalación,
> **la primera empresa ya paga**. Era una de las cifras que nadie había validado.
>
> **Se mantiene la tabla de usuarios activos por empresa** —alta y baja— porque cuesta una tabla y
> permite cambiar de modelo sin reconstruir el histórico.

### `E5` · El rol de Operador de plataforma entra en el modelo de roles

**Situación.** Los **tres roles** que el cliente validó en S2 —supervisor de campo, administrador de
producción, administrador del sistema— son **roles dentro de la empresa cliente**. El SaaS añade un
cuarto que no está en ese ejercicio: **el operador de la plataforma**, que somos nosotros.

**Lo que dice el cliente.** Cerró los roles: *«quedamos con estos 3 roles […] Así es. Y no
necesitamos nada más.»* Y respondió **SÍ** a *«que quien opera la plataforma no pueda leer la
información de producción de la empresa»*.

**Lo que decimos Juan y Jerónimo.** `DEC-01` partió el rol de administrador en dos: *Administrador de
la empresa (tenant)* y *Operador de la plataforma (equipo FlorLogic)*. `DEC-09` le dio acceso de
infraestructura y no funcional.

**Lo que falta.** El cuarto rol **no está en `2. Priorización-QA`** —que tiene tres columnas de
actores— ni en el mapa de empatía ni en los escenarios. Y su existencia es lo que hace que `C9` sea
urgente.

---

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 (por `B6`)
>
> **El cuarto actor casi desaparece, y los tres roles que el cliente cerró en S2 vuelven a ser
> suficientes.**
>
> Bajo local-first, **quien administra la instalación es el administrador del sistema de la finca** —
> el ingeniero de sistemas que `A20` confirma como empleado directo del cliente y que él mismo
> identificó en S2 como *«el superusuario»*. **Es exactamente el rol que ya está en
> `2. Priorización-QA`.**
>
> **Lo que queda de operador es una figura de servicios en línea**, que trabaja sobre datos cifrados
> con llave ajena (`B4`) y **no es un usuario funcional de ningún negocio.**
>
> ✅ **`2. Priorización-QA` no necesita una cuarta columna.** El ejercicio de priorización sigue
> siendo válido tal como está.

### `E6` · Tensión asumida: plataforma multi-tenant con Escalabilidad fuera del top 5

**Situación.** Se ha decidido construir un SaaS multi-tenant y, a la vez, que Escalabilidad no sea
uno de los cinco drivers de calidad (`§0.3`).

**Lo que dice el cliente.** **11 SÍ de 11** en Escalabilidad — el único bloque de la caracterización
sin un solo NO. Pero en el ejercicio de priorización de S2 la dejó en el puesto 12 de 13.
**Él también dijo las dos cosas.**

**Lo que decimos Juan y Jerónimo.** Que los drivers deben ser los que el usuario final percibe y
puede medir, y la escalabilidad no lo es: se nota cuando falta, nunca cuando está.

**Por qué queda registrada aunque sea una decisión tomada.** Porque la consecuencia práctica es que
**ningún escenario de calidad de los cinco drivers va a ejercitar el multi-tenant**, y las decisiones
de arquitectura se justifican con escenarios. `CN-29`, `CN-30` y `CN-35` quedan sin atributo que las
respalde en el ranking. **No es incoherente; es un punto ciego que hay que vigilar a conciencia.**

---

> `[!]` **`A2` y `A10` le añaden dos aristas.** El horario intocable de una empresa puede ser la
> única ventana de mantenimiento posible de otra. Y `CN-30` queda mal dimensionada: eran 30 personas,
> son **10 por empresa** — con ~20 empresas, **~200 en el pico simultáneo**.

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 (por `B6` y `B9`)
>
> **La tensión se disuelve.** Ya no hay una plataforma multi-tenant cuya escalabilidad ningún escenario
> de calidad ejercita: **el crecimiento es comercial —más instalaciones— y no arquitectónico.**
> Escalabilidad puede quedarse en el puesto 9-10 sin dejar punto ciego.
>
> **Y `A2` había disuelto ya la otra arista:** la ventana de mantenimiento es **mayo**, temporada baja
> **común a todo el sector**, así que nunca hubo conflicto de horarios entre empresas.

`[!]` **Lo único que hereda esta entrada** es lo que `B9` señala: **`CN-29` empeora**. Migrar el
esquema en `N` instalaciones ajenas es más difícil que en `N` bases propias, y **es el verdadero
riesgo de escala del nuevo modelo.** Vive en `E2`.

**Y sigue en pie lo de `C4`:** la **extensibilidad del esquema** —añadir un tipo de labor sin rehacer
la captura— es diseño de datos y hay que decidirla **antes de la primera tabla**. Con `N`
instalaciones desplegadas, cambiarla después es mucho más caro.

### `E7` · La objeción que el cliente predijo es ahora la objeción central

**Situación.** La única objeción comercial que el cliente formuló en todo el levantamiento es
exactamente la que el modelo SaaS activa.

**Lo que dice el cliente.** S1, literal:
> *«Yo le cuento, las empresas de flores son muy celosas en su información. Entonces, una de las
> primeras cosas que le van a decir: "bueno, puede hacer un plan de membresía, pero **usted cómo me
> va a controlar a saber que usted no le da la información mía a otro cualquiera de flores**", que
> eso puede ocurrir.»*

Y sin embargo, en la caracterización respondió **NO** a cifrado local, **NO** a cifrado en tránsito,
**NO** a respaldos cifrados, **NO** a llave por empresa, **NO** a registrar todo acceso técnico,
**NO** a registro de exportaciones y **NO** a *«poder demostrar documentalmente ese aislamiento»*.
**Predijo la objeción y luego rechazó todos los mecanismos que la responden.**

**Lo que decimos Juan y Jerónimo.** **SÍ a los siete.** `CN-28` sigue `EN DUDA` con la custodia de la
clave sin decidir, y bajo SaaS la custodia es nuestra.

**Lo que hay que decidir, y no lo decide el cliente.** Qué se construye y qué se promete por contrato
en materia de confidencialidad, sabiendo que el cliente **no lo ha pedido** pero que `CN-03` y el
contrato **sí lo exigen** — y que la persona que firme el contrato no va a ser el director de
producción. Ver `A5`, `B4`, `C5`, `C9`.

---

---

## Anexo · Lo que este documento NO cubre

- **Las respuestas pregunta por pregunta de Juan y de Jerónimo** en las 94 divergencias con el
  cliente. Aquí están agrupadas por tema; el detalle fila a fila vive en el Excel.
- **`FuncionalidadesSignificativas.xlsx`, `RestriccionesNegocio.xlsx` y `RestriccionesTecnicas-IA
  .xlsx` leídos celda por celda.** Se usaron a través del resumen de `CONTEXTO.md`.
- **El modelo ArchiMate.** No se abrió — y ahora arrastra el rol de Operador de `E5`.
- **`FlorLogic_Mapa_de_Impacto.xlsx` y el Elevator Pitch.** No se abrieron.
- **Las grabaciones de audio.** Solo se leyeron las transcripciones, con las limitaciones de `§0.1`
  del documento 1.
- **El coste de operar el SaaS.** `E2` lo señala; nadie lo ha calculado.

---

**Fin del inventario, v7.0.**

**53 de 71 decididas.** Los grupos **A**, **B**, **C** y **E** completos.
**18 siguen abiertas, todas del grupo `D`, y ninguna es nueva.**

**Lo que queda:**

1. **Las 18 del grupo `D` — la sesión con el cliente.** Es todo lo que falta, y le corresponde a
   **Jerónimo**. Ver la sección final de este documento.
2. **Tres cabos sueltos nuestros**, con la entrada ya cerrada: **dónde corre el modelo de IA** (`C2`) ·
   **recalcular los 100–200 USD/mes** con quince instalaciones (`E2`) · **qué pasa con una instalación
   que deja de pagar** (`E2`).
3. **Las 7 decisiones tomadas en contra del cliente** (`§0.7`), que hay que llevarle.

`[!]` **Todo lo que se decidió sobre el modelo `DEC-01`, `DEC-06`, `DEC-09`, `DEC-16`, `CN-10`,
`CN-11`, `CN-14`, `CN-28`, `CN-29`, `CN-30`, `CN-31`, `CN-33`, `CN-35`, `RFP-05` y
`PLAN_DEMO_CAPTURA §4.4` sigue sin propagarse a `CONTEXTO.md`.** Este archivo va por delante del maestro.

> ### ✅ RESUELTA · ronda 3 — 25-ago-2026 (por `B4` y `B6`) — **y se convierte en el mejor argumento comercial**
>
> **La objeción que el cliente predijo en S1 deja de tener objeto.**
>
> > *«Puede hacer un plan de membresía, pero **usted cómo me va a controlar a saber que usted no le da
> > la información mía a otro cualquiera de flores**.»*
>
> **La respuesta ya no es una promesa contractual: es una propiedad del diseño.** Sus datos operativos
> viven **en su propia infraestructura**, y lo que sube a la nube va **cifrado con una llave que tiene
> su dispositivo** (`B4`). **No podemos entregar su información aunque quisiéramos.**
>
> ✅ **Eso resuelve la incoherencia que esta entrada señalaba** —predijo la objeción y luego rechazó
> todos los mecanismos que la responden— **sin necesidad de construir ninguno de ellos**: el modelo de
> despliegue hace el trabajo que iban a hacer el cifrado por empresa y el registro de acceso técnico.
>
> `[!]` **Y conviene decirlo en la venta con esas palabras**, porque es literalmente la primera
> objeción que él dijo que le pondrían. Es el argumento diferencial del producto.

---
---

# 📌 Sección final · Instrucciones para el modelo de IA de Jerónimo

> **Esto no es parte del inventario. Es la entrega.**
> Este documento se le pasa al modelo de IA de Jerónimo como **contexto completo** de las decisiones
> tomadas. Lo que sigue son las instrucciones de qué hacer con él.

---

## 1 · Qué eres y qué tienes delante

Estás leyendo la **guía principal de decisiones de FlorLogic**, un sistema de captura de datos y
proyección de producción para fincas de flores. Contiene **71 entradas**: contradicciones del cliente
consigo mismo (`A`), del cliente contra el equipo (`B`), del equipo contra sí mismo (`C`), preguntas
que nunca se le hicieron (`D`) y consecuencias del modelo de negocio (`E`).

**53 están decididas. 18 siguen abiertas, todas del grupo `D`, y todas necesitan al cliente.**

Cada entrada conserva **su situación original y todas las rondas de decisión**, sin borrar nada. Eso
significa que **puedes reconstruir el porqué de cualquier decisión**: está dentro de su propia
entrada, en el bloque de la ronda que la cerró, junto con lo que se descartó y lo que se pensaba antes.

---

## 2 · Lo primero que tienes que hacer

**Produce un resumen de las decisiones tomadas** y, terminado el resumen, **abre con esta frase
exacta:**

> **«Aquí está el resumen, hazme preguntas por favor, o pide información más detallada sobre alguna
> decisión.»**

**No avances más allá de esa frase hasta que Jerónimo responda.** El objetivo de esta primera entrega
es **contextualizarlo y dejarle preguntar**, no cerrar nada nuevo.

---

## 3 · Cómo responder cuando pregunte

- **Si pregunta por una decisión concreta**, localiza su identificador (`A1`, `B6`, `C4`, `E2`…) y
  dale **la decisión, su porqué y qué abrió**. Todo está en la entrada.
- **Cita siempre el identificador.** Es el vocabulario común del proyecto.
- **Distingue tres cosas y no las mezcles**, porque el documento las separa a propósito:
  1. **Lo que dijo el cliente** — es la fuente de verificación.
  2. **Lo que decidimos Juan y Jerónimo** — es hipótesis del equipo, nunca requisito.
  3. **Las decisiones tomadas en contra del cliente** — son siete y están listadas en `§0.7`.
- **Si algo no está en el documento, dilo.** No lo completes con un supuesto plausible: es la regla de
  honestidad del proyecto (`§16.6` de `CONTEXTO.md`).

---

## 4 · Lo que hay que pedirle a Jerónimo

**El trabajo con el cliente le corresponde a él.** Las 18 entradas abiertas del grupo `D` son
exactamente eso: lo que hay que averiguar en la sesión con el cliente.

**Las tres que mandan:**

| ID | Qué hay que conseguir |
|---|---|
| **`D1`** | **El proceso de captura, trabajado a fondo con el cliente.** Nunca se hizo. Pide una sesión, no un dato |
| **`D2`** | **Los documentos prometidos:** plan de siembra, presupuestos, formatos en papel llenos, tabla de grados, histórico |
| **`D3`** | **De dónde sale el % de productividad por variedad**, y **cómo se reparten los tallos en los ~7 días que dura el corte** |

**Las otras quince, en la tabla del grupo `D`.** La técnica acordada para la sesión está en
`CONTEXTO.md §15.1`: **seguir UNA cama real de principio a fin**, con los formatos físicos sobre la
mesa, y repetir en diez minutos con una cama que se erradicó.

**Y hay que llevarle también las siete decisiones tomadas en contra de una respuesta escrita del
cliente** (`§0.7`). No se aplican en silencio.

---

## 5 · Las diez decisiones que más contexto dan

Si Jerónimo necesita ponerse al día rápido, estas diez explican el proyecto entero:

| ID | Decisión |
|---|---|
| `B6` · `C3` | **Local-first con servicios en línea.** Instalación por empresa (~20.000 USD) + mensualidad. **El SaaS queda descartado por inviable** |
| `A19` | **La métrica de éxito se mide en tallos cortados, no en dinero.** Si el sistema dice 870, hay que cortar 870 o lo más cerca posible |
| `B2` | **El dolor no es capturar rápido: es que el dato llegue antes a la proyección.** Hoy tarda 8 días |
| `C8` | **El 2% de error del cliente es de transcripción manual, y desaparece por diseño** |
| `A1` | **La trazabilidad es por sesión de sincronización, no por dato** |
| `A15` | **No se busca completitud: último valor conocido por campo, con su fecha** |
| `B4` · `C5` | **La llave de cifrado la tiene el dispositivo del cliente.** Responde sola la objeción comercial del cliente |
| `C2` | **La IA vuelve como asistente de captura**, entrenada en el entorno del cliente |
| `C4` · `C6` | **Los campos capturados son datos, no columnas.** Es la restricción de arquitectura más importante |
| `B1` | **La PWA sigue en pie.** `PLAN_DEMO_CAPTURA §4.4` retirado por premisa equivocada |

---

## 6 · Advertencias de método que no puedes ignorar

1. **Las transcripciones no distinguen quién habla.** Los cuatro `.vtt` de Teams tienen un único
   identificador de hablante por archivo. Por eso `1_VOZ_DEL_CLIENTE.md` marca cada cita como `CIT`
   (verificable), `INF` (inferida) o `ESC` (respuesta escrita). **`ESC` es la evidencia más fuerte.**
2. **Las notas literales de la caracterización hay que confirmarlas con el cliente, no
   interpretarlas.** Ya pasó dos veces: `B7` («ÚLTIMO» significaba «el más viejo» y lo leímos al
   revés) y `B11` (un NO que era sobre otro contexto). Las dos estuvieron a punto de convertirse en
   desacuerdos que no existían.
3. **Todo descansa en una sola voz:** el director de producción. **Planeación, que es donde nace la
   proyección, nunca se exploró.** El propio cliente cerró la puerta a entrevistar a otras áreas.
4. **`CONTEXTO.md` va por detrás de este documento.** Todavía no se ha propagado nada.

---

**Fin de las instrucciones.**
