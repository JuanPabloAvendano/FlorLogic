# FlorLogic — Plan de la demo de captura

**21-ago-2026 · Juan Pablo y Jerónimo · rama `codigo-desarrollo`**

> Supuestos con los que está escrito (no quedaron confirmados): la demo es para la **sesión con el
> cliente dentro de dos semanas**, y es un **prototipo desechable**, no el esqueleto del producto.
> Si alguno de los dos cambia, cambia el plan de la §5, no el resto.

---

## 1 · La estrategia: esto no es un adelanto del producto

`BR-N6` dice que **el proceso de captura nunca se trabajó con el cliente**. `PUNTOS_CLAVE.md` lo
pone como el punto 1 de 6 y añade la mitigación del riesgo de fuente única:

> «la mitigación no es preguntarle más, sino **contrastar contra documentos reales y validar con un
> prototipo**».

La demo **es** esa mitigación. No se construye para enseñar avance: se construye para que el cliente
capture una cama real delante de nosotros y falle. Cada punto donde el prototipo no le sirve es un
requisito que hoy no tenemos.

De ahí salen tres reglas que gobiernan todo lo demás:

**R1 · Solo se codifica lo que ya tiene evidencia dura.** Evidencia dura hoy = la plantilla
digitalizada + las decisiones `DEC-01..DEC-16`. Todo lo que dependa de una respuesta pendiente entra
como **configuración en un archivo**, nunca como código. Así el cambio de opinión del cliente cuesta
editar un JSON, no reescribir pantallas.

**R2 · Se construye para botarlo.** Se debe poder tirar el 100% de la interfaz sin perder nada,
porque lo que sobrevive a la sesión es otra cosa: el **modelo de datos**, el **catálogo de reglas**,
el **contrato de sincronización** y el **registro de lo que pasó en la mesa**.

**R3 · La demo tiene que producir un dato que hoy no existe.** Los escenarios de calidad están
bloqueados porque no hay **medida de respuesta** (`DEC-03`). Un prototipo instrumentado la produce
midiendo: segundos por cama, toques por cama, rechazos, correcciones. Eso no se saca de una encuesta.

---

## 2 · La plantilla ya contesta cuatro preguntas que teníamos abiertas

`PLANTILLAS DOCUMENTOS DE EMPRESA/Plantilla digitalizada de excel de información capturada.xlsx`
—tres hojas: `Cremón`, `Matsomoto` (novedades de siembra de Buenavista) y `Programa Siembras`—
es el único documento del proyecto que muestra **datos reales de captura**. Leerlo con cuidado ya
mueve el modelo:

### 2.1 · Las camas divididas son reales y frecuentes — `PUNTOS_CLAVE §5` se puede cerrar

En el bloque 12 de la hoja `Cremón`, tres camas aparecen dos veces el mismo día con variedades
distintas:

| Cama | Fila 1 | Fila 2 |
|---|---|---|
| 37 | Lineth · 148 líneas · 2812 | Cooper · 21 líneas · 399 |
| 39 | Astroi · 153 líneas · 1907 | Astroi · 97 líneas · 1843 |
| 40 | Lineth · 49 líneas · 931 | Petroska · 5 líneas · 95 |

Tres de dieciocho filas, **17%**. `DEC-14` y `DEC-15` dejan de ser una apuesta: el formato en papel
ya trabaja por sección. No hace falta preguntarlo en la sesión, hace falta **confirmar la lectura**.

### 2.2 · La unidad real de captura no es el área — son las líneas

`DEC-14` dice `plantas = área × densidad por sección`. El formato real no tiene ninguna columna de
área ni de densidad. Tiene **`#LINEAS`** y **`CANTIDAD`**. Nadie mide metros cuadrados en el campo:
**cuentan líneas**.

Consecuencia directa: cuando se reescriban `RF-001` y `RF-002` (que siguen sobre el modelo de
esquejes que `DEC-14` invalidó), la fórmula de captura es

```
cantidad de plantas (sección) = #líneas × plantas por línea (de esa variedad)
```

y `área × densidad` queda como **derivación de oficina**, no como dato de campo. Esto abarata la
app: se captura un entero, no dos decimales.

### 2.3 · La regla de validación ya existe en los datos, y ya encuentra errores

`plantas por línea` es prácticamente constante por hoja: **19 en Cremón, 15 en Matsomoto**. Aplicando
esa razón al histórico real aparecen las filas que no cuadran:

| Hoja | Variedad | Razones observadas | Lectura |
|---|---|---|---|
| Cremón | Petroska | 19,0 · 18,93 · 19,0 · 19,0 · 19,0 · 19,0 | 18,93 = redondeo |
| Cremón | Cooper | 19,0 ×5 · **17,78** | una fila fuera |
| Cremón | Lineth | 19,0 · 19,0 | limpia |
| Cremón | **Astroi** | **11,19 · 12,46 · 19,0** | tres razones distintas, misma variedad, mismo día |
| Matsomoto | Rose | **13,8** · 15,0 · **16,0** · 15,0 · 15,0 | dos fuera |
| Matsomoto | Blue | 15,0 ×3 · **20,56** | una fuera |
| Matsomoto | Scarlett | **14,76** · **24,68** · 15,0 | dos fuera |
| Matsomoto | Pink | 15,0 · 15,0 · **13,96** | una fuera |

**Nueve de treinta y dos filas** no cuadran con la razón dominante de su hoja. Ese es exactamente el
error de captura del que habla el proyecto, y por primera vez está **medido sobre datos del cliente**.

Esto vale doble: es la primera regla dura de la app (`CN-22`) y es el argumento de venta de la
sesión. Se le muestra al cliente su propio formato con las nueve filas marcadas y se le pregunta cuál
de ellas es un error y cuál es una razón legítima que no conocemos. Cualquiera de las dos respuestas
es un requisito.

### 2.4 · Hay una columna que no sabemos leer, y dos formatos, no uno

- La columna **`OBSE`** alterna entre `325` y `425` sin patrón evidente, incluso dentro de la misma
  variedad. No es una observación en texto libre: es un código. **Pregunta directa de la sesión.**
- `Novedades de siembra` y `Programa de siembras` **no son el mismo formato**. El segundo trae
  `fecha salida cf`, `long prebrotado`, `lote`, `calibre`, `proveedor`, `contenedor`. La app necesita
  **dos plantillas de captura**, no una — y eso confirma la respuesta de Juan a `CNF` («la estructura
  de captura puede cambiar según la variedad / no es la misma para todos los bloques»).

---

## 3 · Las 262 respuestas de caracterización: qué son y qué no son

### 3.1 · Estado real, hoy

| Columna | Diligenciadas |
|---|---|
| **Respuesta Juan** | **262 / 262** |
| Respuesta Jerónimo | 0 |
| Respuesta Cliente | 0 |

Y hay un problema de higiene que conviene arreglar antes de seguir: las respuestas viven en
`Documentacion/Mini QAW FlorLogic.xlsx`, hoja `3. Caracterización`, mientras que
`PREGUNTAS_CARACTERIZACION.xlsx` —el archivo que la memoria del proyecto declara como hoja de
diligenciamiento— sigue en **0% respondido**. Son dos fuentes para lo mismo. **Una de las dos tiene
que morir esta semana**, o dentro de un mes nadie va a saber cuál se llevó a la sesión.

### 3.2 · Su estatus epistémico: hipótesis del arquitecto

Esto hay que escribirlo en el archivo, no solo saberlo. Las 262 respuestas son **hipótesis de un
ingeniero sin experiencia en el sector** (`CN-06`), no requisitos. Sirven para tres cosas:

1. **Fijar el alcance de la demo** — sin ellas no habría con qué empezar a codificar hoy.
2. **Ser contradichas.** Una hipótesis escrita se puede refutar en cinco segundos frente al cliente;
   una pregunta abierta se responde con «depende» y no avanza nada.
3. **Medir la distancia** entre lo que el equipo asume y lo que el negocio necesita. Esa distancia,
   por bloque, es el mapa de dónde estamos flojos.

El riesgo concreto de no marcarlas: dentro de un mes esas columnas se van a leer como si el cliente
las hubiera dicho. Ya pasó una vez en este proyecto — `DEC-16` tiene la marca `[!]` porque la IA
apareció atribuida al cliente sin cita que lo respalde.

### 3.3 · Triage: quién debe responder qué (para no esperar al cliente en todo)

De los 13 bloques, **solo ocho son del cliente**. Los otros cinco los cierran ustedes dos y no
dependen de ninguna reunión:

| Responde el **cliente** (necesidad del negocio) | Responden **Juan + Jero** (decisión de arquitectura) |
|---|---|
| `CNF` Confiabilidad (57) | `SEG` Seguridad (19) — salvo el aislamiento, que es del contrato |
| `DSP` Disponibilidad (20) | `ESL` Escalabilidad (11) |
| `RND` Rendimiento (21) | `SOP` Capacidad para ser Soportado (13) |
| `AUD` Capacidad para ser Auditado (32) | `ADM` Capacidad para ser Administrado (16) |
| `CAP` Capacidad (15) | `INT` Interoperatividad (13) — ya acotada por `CN-33` |
| `UXP` Experiencia de Usuario (18) | |
| `ACC` Accesibilidad (11) | |
| `POR` Portabilidad (13) — parcial | |

**Jero puede diligenciar su columna completa esta semana**, sin esperar al cliente. Y donde Juan y
Jero difieran hay algo mucho más valioso que un acuerdo: un supuesto que ninguno de los dos había
hecho explícito. Ese desacuerdo es la lista de preguntas prioritarias para la sesión.

### 3.4 · Lo que las respuestas de Juan ya fijan para la demo

| Respuesta | Decisión de diseño de la demo |
|---|---|
| `CNF` **NO** a guiado pantalla por pantalla · **SÍ** a plantilla con estructura similar al papel | La pantalla principal es una **rejilla igual al formato**, no un asistente |
| `CNF` verificar por variedad **SÍ** · sección **SÍ** · cama **SÍ** · bloque **NO** · finca+jornada **SÍ** | La verificación llega hasta cama, **salta bloque** y vuelve en el cierre de jornada |
| `CNF` **SÍ** rechazar fuera de rango · **SÍ** mostrar siempre el motivo | Motor de reglas con motivo visible, nunca un rechazo mudo |
| `CNF` **SÍ** cambiar reglas sin nueva versión de la app | Las reglas van en `reglas.v1.json`, versionado y descargable (`CN-26`) |
| `CNF` **SÍ** guardar incompleto como pendiente · **SÍ** impedir cerrar con campos vacíos | Dos estados de cierre distintos, no uno |
| `CNF` **SÍ** dejar ambas capturas en conflicto y que decida una persona | Coincide con `DEC-05` y `CN-24`. Nada de «gana la más reciente» |
| `CNF` **SÍ** seguir llenando papel en paralelo · **SÍ** comparar app contra papel del mismo día | **Es una funcionalidad**, no un periodo de transición. Entra en la demo |
| `RND` **SÍ** menos de 30 s por cama · **SÍ** guardado inmediato · **SÍ** sync en segundo plano | Objetivo medible desde el día 1. Se instrumenta, no se estima |
| `UXP` **SÍ** una mano · guantes · sol directo · mínimos toques · escanear marca física | Botones ≥ 56 px, alto contraste, escaneo QR de la cama |
| `POR` **SÍ** Android · **SÍ** Apple · **SÍ** tabletas · **SÍ** navegador sin instalar | Multiplataforma obligatoria — ver §4 |
| `SEG` **SÍ** entrar sin conexión · **SÍ** identificación propia no compartida | Login local con PIN, sin backend en la demo |

### 3.5 · Las seis contradicciones que la demo debe llevar a la mesa

Estas salen de las respuestas de Juan contra sí mismas o contra las restricciones. No son errores
suyos: son justamente lo que un prototipo resuelve mejor que una entrevista.

1. **La ventana offline no existe.** `DSP-01` **SÍ** capturar sin ninguna conexión, pero `DSP-02..05`
   **NO** a jornada completa, tres días, semana o quince días sin sincronizar. Y `CN-17` dice que
   **no hay conectividad en el área de cultivo**. Entonces, ¿cuándo sincroniza? La demo asume
   **jornada completa** y en la sesión se pregunta dónde está el punto real con señal.
2. **Rejilla contra una sola mano.** `CNF` pide estructura tipo papel; `UXP` pide una mano, con
   guantes, bajo el sol; `RND` pide menos de 30 s por cama. Una rejilla de siete columnas y una
   captura de una mano **no son la misma aplicación**. → se construyen las **dos variantes** (§5, D3
   y D7) y el cliente elige capturando, no opinando.
3. **Corrección sin rastro.** `CNF` **SÍ** corregir después de sincronizar, pero **NO** conservar
   visible el valor original y **NO** exigir motivo — mientras `AUD` es el atributo #4 y `CNF` dice
   **SÍ** a «la información nunca se elimine». Auditoría sin valor original no es auditoría. Hay que
   preguntarlo derecho.
4. **Gama baja contra BYOD.** `RND` **NO** a que funcione con fluidez en gama baja, pero `POR` **SÍ**
   a que **el dispositivo lo ponga cada persona** y `CN-21` dice que no hay partida de hardware. Si
   el celular lo pone el operario, es de gama baja. Una de las tres tiene que ceder, y es una
   decisión de presupuesto, no técnica.
5. **Portabilidad de datos.** `POR` **NO** a poder llevarse toda la información si se termina la
   relación con el proveedor — contra `CN-33`/`RF-019`, que hacen de la exportación la única
   interoperabilidad de fase 1. En un SaaS multi-tenant (`DEC-01`) esto además va al contrato.
6. **Verificado contra proyección.** `CNF` **NO** a distinguir en pantalla lo verificado de lo no
   verificado, pero **SÍ** a que lo no verificado quede **excluido de las proyecciones**. Si se
   excluye pero no se ve, nadie va a entender por qué la proyección le dio bajo.

---

## 4 · Tecnología

### 4.1 · Recomendación

**Aplicación web progresiva (PWA) offline-first: React + TypeScript + Vite + Dexie (IndexedDB).**

| Pieza | Elección | Por qué |
|---|---|---|
| App | React 18 + TypeScript + Vite | Es el stack que ya manejan. Cero curva |
| Offline | `vite-plugin-pwa` (Workbox) + **Dexie 4** sobre IndexedDB | Persistencia local real, transaccional, sin backend |
| IDs | **UUID v7 generados en el dispositivo** | Idempotencia de sincronización (`CN-24`) desde el día 1 |
| Reglas | `reglas.v1.json` interpretado en runtime | `CNF`: cambiar reglas sin nueva versión de la app (`CN-22`, `CN-26`) |
| Escaneo | `@zxing/browser` o `BarcodeDetector` | `UXP`: identificar la cama escaneando en vez de escribir |
| Voz | Web Speech API | `UXP` **SÍ** a dictar. En la demo, solo Android/Chrome. Suficiente |
| Estilos | Tailwind, paleta de alto contraste | Sol directo, y `ACC`: nunca depender solo del color |
| Semilla | script **Python + openpyxl**: plantilla `.xlsx` → `seed.json` | Los bloques y camas de la demo son los reales de Buenavista |
| Backend (fase 2) | **FastAPI + SQLAlchemy + PostgreSQL** | Python, que ya manejan. Una BD por empresa (`DEC-11`) con *connection factory* por tenant |
| Despliegue | Cloudflare Pages o Netlify | HTTPS gratis — requisito del *service worker* y de la cámara. Se actualiza en segundos |

**El argumento de fondo:** la demo se lleva a una mesa y se pasa de mano en mano. Una PWA se abre con
un enlace en el celular del cliente — sin tienda, sin APK, sin permisos, sin que él instale nada. Y
cuando en mitad de la sesión él diga «eso va al revés», ustedes corrigen y republican **mientras
siguen sentados ahí**. Ningún otro stack da eso.

### 4.2 · Por qué no Flutter todavía

Flutter es probablemente la respuesta correcta **para el producto**. No para esta demo: lo exploraste
pero no lo dominas, y aprenderlo cuesta más o menos las dos semanas que faltan para la sesión. Gastar
el plazo en la herramienta en vez de en las preguntas es exactamente el error que el proyecto no se
puede permitir.

Android nativo (Java/Kotlin + Room) se descarta por otra razón: cierra iOS y web de un tajo, y `POR`
pide Android **y** Apple **y** tabletas **y** navegador sin instalar.

### 4.3 · Los límites de la PWA — dicho ahora, no cuando duela

1. **iOS/Safari desaloja IndexedDB** tras ~7 días sin abrir la app si no está instalada en pantalla
   de inicio. Contra `DEC-12`, que exige **pérdida CERO**.
2. **`Background Sync` no existe en iOS.** La sincronización en segundo plano que pide `RND` solo
   funciona con la app abierta.
3. **Cifrado en reposo** (`SEG` **SÍ**, `CN-28`): en web es WebCrypto con clave derivada del PIN, pero
   la clave termina viviendo en IndexedDB. Es más débil que SQLCipher y **no se puede demostrar
   documentalmente** ante el cliente, que es justo lo que `SEG` pide.
4. **Reloj alterado** (`CN-25`): en web solo se detecta por deriva contra el último sello del
   servidor. Bloquear la captura es una heurística, no una garantía.

### 4.4 · El punto de decisión (y esto contesta tu pregunta de fondo)

**La elección de tecnología del producto está deliberadamente aplazada, y las respuestas que faltan
son justo las que la deciden.** No es indecisión: es que decidir hoy sería adivinar.

El producto **deja de poder ser PWA** si se cumple cualquiera de estas tres:

- `DSP-02..05` → la ventana offline es **de una jornada o más** (y `CN-17` sugiere que sí lo es);
- `SEG` → el cifrado en reposo tiene que ser **demostrable ante el cliente**, no declarativo;
- `POR` → iOS es **real** y no una respuesta de cortesía.

Si se cumple alguna, el producto es **Flutter** (o Kotlin + Room si iOS se cae). Y la demo no se
pierde: **el modelo de datos, el catálogo de reglas y el contrato de sincronización se llevan tal
cual**. Lo único que se bota es la piel — que es lo que dice `R2`.

---

## 5 · Plan de desarrollo — 10 días hábiles

Rama `codigo-desarrollo`, que ya existe en el repo y está vacía.

| Día | Qué | Resultado visible al final del día |
|---|---|---|
| **D1** | Scaffold Vite + TS + PWA. Script Python `plantilla.xlsx → seed.json` | La app abre en el celular y **lista los bloques y camas reales de Buenavista** |
| **D2** | Modelo local en Dexie: empresa · finca · bloque · cama · sección · captura · outbox. UUID v7 | Se captura una fila y sobrevive a cerrar la app y apagar el teléfono |
| **D3** | **Variante A** — rejilla igual al formato de papel, con fila añadible para cama dividida | Se puede reproducir el bloque 12 completo, camas 37/39/40 incluidas |
| **D4** | Motor de reglas leyendo `reglas.v1.json`. Duras y blandas. Motivo de rechazo visible | La regla `cantidad ≈ #líneas × plantas por línea` **marca las nueve filas de §2.3** |
| **D5** | Cierre de cama, confirmación final, estado de sincronización visible, deshacer último dato | Cerrar una cama exige lo obligatorio; lo incompleto queda «pendiente» |
| **D6** | Outbox + sincronización simulada: cola, reintento, idempotencia, conflicto → decide una persona | Se captura la misma cama dos veces y **quedan las dos**, no se pisan |
| **D7** | **Variante B** — una cama a la vez, botones grandes, una mano, escaneo QR | Misma base de datos, otra piel. Listas para comparar |
| **D8** | Instrumentación: segundos por cama, toques, rechazos, correcciones. Panel oculto + export CSV | La demo **mide** en vez de que nosotros estimemos |
| **D9** | Modo papel-contra-app: la fila del formato al lado de la captura del mismo día | `CNF` «seguir llenando el papel en paralelo» deja de ser un parche |
| **D10** | Prueba en campo simulado (sol, guantes, modo avión, batería). Congelar, desplegar, ensayar | Jero hace de supervisor y Juan cronometra. Se corrige lo que salga |

**Corte del D5:** si al terminar el D5 no está listo, se sacrifican D7 y D9 —no D4 ni D8—. Sin motor
de reglas y sin instrumentación, la demo no produce nada que sirva para escribir escenarios.

**Lo que sale de la sesión con el cliente:**

1. El CSV de instrumentación → **las medidas de respuesta** de `RND`, `CNF` y `UXP`, que hoy no
   existen y que bloquean todos los `ESC-nnn`.
2. La columna **Respuesta Cliente** diligenciada sobre los ocho bloques que le corresponden.
3. La lista de sorpresas: todo lo que hizo o dijo que el prototipo no previó. Esa lista vale más que
   las otras dos juntas.

---

## 6 · Modelo de datos mínimo de la demo

Derivado de la plantilla real, no del modelo teórico:

```
Empresa ─< Finca ─< Bloque ─< Cama ─< SeccionCama
                                        ├─ variedad
                                        ├─ lineas          ← lo que se cuenta en campo
                                        └─ plantas         ← lineas × plantasPorLinea(variedad)

Captura            id(uuidv7) · camaId · fecha · tipoPlantilla · estado(pendiente|cerrada)
                   capturadoPor · dispositivoId · selloTiempo · versionReglas
LineaCaptura       capturaId · seccionId · variedad · lineas · cantidad · obse · motivoRechazo?
Outbox             capturaId · intentos · ultimoError · estado(pendiente|enviado|conflicto)
Catalogo           variedades · plantasPorLinea · bloques · camas   ← de seed.json, versionado
Reglas             reglas.v1.json                                    ← duras y blandas, versionado
```

Dos tipos de plantilla desde el principio (`novedad_siembra`, `programa_siembra`), porque §2.4 ya
mostró que no son el mismo formato.

---

## 7 · Qué NO se construye ahora

Y la razón, en cada caso, es la misma: depende de una respuesta que todavía no existe.

| No se construye | Depende de |
|---|---|
| Autenticación real, roles, RBAC | `BR-N5`, `CN-12`, `CN-23` — sin resolver |
| Multi-tenant, una BD por empresa | `DEC-11` está decidido pero no toca la demo |
| Backend, migraciones, respaldos | `CN-29`, `CN-28` — el cifrado sigue sin decidirse |
| Motor de proyección | Faltan **los dos números** de `PUNTOS_CLAVE §2`: el % de productividad y el reparto diario de tallos |
| BI y reportes | `DEC-10` — seis reportes de línea base, ninguno especificado |
| IA de captura o analítica | `DEC-16` sigue marcada `[!]` por confianza en disputa |
| Vista geométrica del bloque | `RFP-03`, candidato sin validar |

---

## 8 · Lo primero, esta semana

1. **Matar una de las dos fuentes de caracterización** (§3.1) y marcar la columna de Juan como
   *hipótesis del arquitecto*, no como requisito.
2. **Jero diligencia su columna completa.** No depende del cliente y produce la lista de preguntas
   prioritarias donde los dos difieran.
3. **D1 y D2 del plan**, en paralelo con lo anterior.
4. Confirmar con el cliente la lectura de §2.1 y §2.2 —camas divididas y líneas en vez de área—
   antes de reescribir `RF-001` y `RF-002`.
