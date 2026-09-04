# 3 · Decisiones de negocio y solución de contradicciones
## Guía principal de decisiones — FlorLogic

> **v8.0 · 4-sep-2026 — partido en guía y anexo; el detalle de las 50 entradas cerradas vive ahora en
> `3_ANEXO_RONDAS_DE_DECISION.md`.**
> *(v7.0 · 25-ago-2026 — quinta ronda: cerrado todo lo que no depende del cliente.)*
>
> **Este documento dejó de ser un inventario de brechas y pasó a ser la guía de decisiones del
> proyecto.** De 71 entradas, **53 están decididas**. Las 18 que quedan son todas del grupo `D` y
> **son la sesión con el cliente**.
>
> Cada entrada conserva **su situación original y todas sus rondas**, sin borrar ninguna: se puede
> leer qué se decidió, **por qué**, qué se descartó y qué había antes.
>
> `[!]` **Si buscas el porqué de una decisión, está dentro de su propia entrada**, en el bloque de la
> ronda que la cerró — y **esas entradas están en `3_ANEXO_RONDAS_DE_DECISION.md`** desde el
> 4-sep-2026. Los identificadores (`A1`, `B6`, `C4`, `D3`, `E2`) **nunca se renumeran**.
>
> **Reglas de este archivo, fijadas por Juan:**
> 1. **No se añaden brechas nuevas.** Se trabaja solo sobre las que ya están y sobre lo que ellas
>    mismas abran. Ningún identificador nuevo.
> 2. **No se borra nada.** Lo resuelto se marca resuelto y **se queda a la vista**, con la decisión
>    que lo cerró.
> 3. **Los identificadores nunca se renumeran.**
> 4. **No se abren archivos nuevos para lo que este cubre.** Lo que sí se hizo el 4-sep fue partir
>    este en **guía + anexo**: mismo alcance, mismas entradas, mismos identificadores, repartidos
>    entre dos archivos porque uno solo de 168 KB no se leía. **Una contradicción nueva no abre
>    archivo: se acumula en un ADR** (`docs/03-arquitectura/`), que es el método acordado ese día.
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

**Y los cabos sueltos nuestros, actualizados al 4-sep-2026:**

| Cabo | Estado |
|---|---|
| **Dónde corre el modelo de IA** (`C2`) | ✅ **CERRADO** por `ADR-030`: **infiere en el dispositivo, se entrena en el nodo.** El nodo no lleva hardware de IA, así que **el precio de instalación no se mueve** |
| **Recalcular los 100–200 USD/mes** antes de firmar (`E2`) | 🔴 Abierto |
| **Qué pasa con una instalación que deja de pagar** (`E2`) | 🔴 Abierto — si se queda sin actualizaciones diverge de versión, que es lo que `CN-29` teme |
| **Qué es físicamente el nodo de la finca** | 🔴 Abierto, **y ya solo lo bloquea `CN-20`** (el sistema heredado de ~300 tablas). Es del cliente, y va con `D5` |

**Lo que ya no es un cabo suelto:** las cinco decisiones previas a la primera tabla del dominio están
cerradas en `ADR-024`, `ADR-027`, `ADR-028`, `ADR-029` y `ADR-030`. **La construcción del dominio ya
no está bloqueada por decisión propia** — solo por `BR-23` y por `D1`, que son del cliente.

---


---

## Dónde está el detalle de cada entrada — **partido el 4-sep-2026**

Este archivo era de 168 KB y **2.362 de sus 2.823 líneas eran el historial de rondas ya cerradas**.
Eso lo volvía imposible de leer para ponerse al día, que es justo para lo que existe.

**Se partió en dos, y no se borró ni una línea:**

| Archivo | Qué tiene | Cuándo se abre |
|---|---|---|
| **Este** (`3_DECISIONES...md`) | El estado vigente: qué cambió en cada ronda (`§0`), el índice, **las 18 abiertas del grupo `D`** y la guía de entrega | **Siempre.** Es la entrada |
| `3_ANEXO_RONDAS_DE_DECISION.md` | Las 50 entradas de los grupos `A`, `B`, `C` y `E` **íntegras**, con su situación original y todas sus rondas | Cuando haga falta **el porqué** de una decisión concreta |

**La regla 2 de este archivo —*no se borra nada*— se mantiene entera:** el anexo conserva cada
entrada tal cual estaba, y los identificadores siguen sin renumerarse. Lo único que cambió es en qué
archivo vive cada cosa.

`[!]` **Y una advertencia que ahora importa más:** las entradas del anexo conservan el enunciado
original, escrito **cuando el modelo era SaaS**. Una mención al SaaS dentro de una entrada del anexo
es **registro histórico, no estado vigente**. El estado vigente es local-first (`B6`, `CN-37`), y
manda `§0` de este archivo.


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


## Anexo · Lo que este documento NO cubre

- **Las respuestas pregunta por pregunta de Juan y de Jerónimo** en las 94 divergencias con el
  cliente. Aquí están agrupadas por tema; el detalle fila a fila vive en el Excel.
- **`FuncionalidadesSignificativas.xlsx`, `RestriccionesNegocio.xlsx` y `RestriccionesTecnicas.xlsx`
  leídos celda por celda.** Se usaron a través de un resumen, no del original.
- **El modelo ArchiMate.** No se abrió — y ahora arrastra el rol de Operador de `E5`.
- **`FlorLogic_Mapa_de_Impacto.xlsx` y el Elevator Pitch.** No se abrieron.
- **Las grabaciones de audio.** Solo se leyeron las transcripciones, con las limitaciones de `§0.1`
  del documento 1.
- **El coste de operar el SaaS.** `E2` lo señala; nadie lo ha calculado.

---

**Fin del inventario, v8.0.**

**53 de 71 decididas.** Los grupos **A**, **B**, **C** y **E** completos.
**18 siguen abiertas, todas del grupo `D`, y ninguna es nueva.**

**Lo que queda:**

1. **Las 18 del grupo `D` — la sesión con el cliente.** Es todo lo que falta, y le corresponde a
   **Jerónimo**. Ver la sección final de este documento.
2. **Dos cabos sueltos nuestros**, con la entrada ya cerrada: **recalcular los 100–200 USD/mes** con
   quince instalaciones (`E2`) · **qué pasa con una instalación que deja de pagar** (`E2`).
   *(El tercero —dónde corre la IA— lo cerró `ADR-030` el 4-sep-2026.)*
3. **Las 7 decisiones tomadas en contra del cliente** (`§0.7`), que hay que llevarle.

[:OK:] **Todo lo que se decidió sobre el modelo —`DEC-01`, `DEC-06`, `DEC-09`, `DEC-16`, `CN-10`,
`CN-11`, `CN-14`, `CN-28`, `CN-29`, `CN-30`, `CN-31`, `CN-33`, `CN-35`, `RFP-05` y
`PLAN_DEMO_CAPTURA §4.4`— ya está propagado a `DRIVERS_ARQUITECTONICOS.md`,** que es hoy la entrada
única al levantamiento.

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

**Las entradas de los grupos `A`, `B`, `C` y `E` están en `3_ANEXO_RONDAS_DE_DECISION.md`**, en la
misma carpeta. Este archivo tiene el estado, el índice y el grupo `D`.

**Y hay un tercer documento que manda sobre la solución técnica**, posterior a este y que cierra
cosas que aquí quedaron abiertas: `docs/03-arquitectura/FlorLogic-alternativa-de-solucion-y-ADR.md`,
con 30 decisiones de arquitectura registradas. **Si una decisión técnica aparece abierta aquí, mira
allí antes de darla por abierta.**

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
  honestidad del proyecto.

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

**Las otras quince, en la tabla del grupo `D`.** La técnica acordada para la sesión: **seguir UNA
cama real de principio a fin**, con los formatos físicos sobre la mesa, y repetir en diez minutos con
una cama que se erradicó.

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
4. **Lo que este documento cierra hay que propagarlo a los drivers**, que es de donde lo toma la
   arquitectura. No se propaga solo. Y **lo que decide la arquitectura vive en los ADR**, no aquí:
   `docs/03-arquitectura/FlorLogic-alternativa-de-solucion-y-ADR.md`.

---

**Fin de las instrucciones.**
