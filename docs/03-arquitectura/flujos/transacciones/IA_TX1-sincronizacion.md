REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 8-oct-2026, a pedido de Juan
Estado de revisión: POR ACEPTAR — nada de este archivo vale para programar hasta que Juan lo marque ACEPTADO en `IA_00-LEEME-transacciones.md`
Manda por encima: `ADR.xlsx` (hoja ADR) · `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx` · `docs/03-arquitectura/decisiones/`

# TX-1 · Sincronizar una jornada ya capturada

**En una frase.** Un celular llega a la oficina con la jornada guardada. La entrega por la red local y
cada anotación atraviesa el gateway, la identidad, las reglas, la bitácora y la base. El celular se
lleva de vuelta la confirmación, la hora corregida y la credencial nueva. Esta transacción es la que
más componentes del backend toca.

**Cómo leer las marcas.**
- **[fuente]**: lo dice un ADR aceptado, DRIVERS o una decisión de Juan.
- **[modelo]**: lo dibuja el C4 vigente.
- **[medido]**: lo midió un spike. Es registro de IA.
- **[propuesta]**: lo propone este documento y espera tu aceptación.
- **[PENDIENTE]**: nadie lo ha decidido.

---

## 1 · Alcance

| Entra | Queda fuera, a propósito |
|---|---|
| El celular **simulado** por el simulador de contrato: carga una jornada ya hecha y la entrega | La captura en pantalla, el QR de la cama y las reglas en el celular (Juan, 8-oct: «ignorando la carga de información ya hecha sin hacer todo el proceso de captura») |
| Gateway → backend → base, con todos los componentes del recorrido | Preparar la jornada desde la web (es la ida; aquí entra como dato de prueba) |
| La vuelta: acuse, desfase, credencial renovada, versión de configuración vigente y avisos | La aplicación nueva para el celular (eso es la actualización de versión) |
| Dos canales de entrega más uno alterno | Corregir lo cerrado y cerrar la producción (T6 sin regla) |
| El tablero de lo que llegó y lo que falta, en la aplicación web | La proyección real: está bloqueada por `BR-23` y aquí es un recálculo **simulado** |

**Precondiciones.** Un guion de datos de prueba las crea; no son funcionalidades de esta transacción.
- Una finca, una producción activa y sus secciones.
- El paquete de configuración `v1` publicado y firmado.
- Un celular registrado y una persona con credencial vigente.
- La asignación de camas de la jornada.

---

## 2 · El recorrido, paso a paso

Cada paso dice qué componente actúa y en qué contenedor. Los diagramas están en el mazo, lámina «TX-1 · secuencia».

| # | Qué pasa | Componente · contenedor | Marca |
|---|---|---|---|
| 1 | El simulador carga la jornada precargada en su almacén de salida. Cada anotación ya tiene su UUID v7, su sesión de captura, su hora cruda y su hora monótona. | Servicio de sincronización · cliente, Almacén del dispositivo · **simulados** | [fuente] `ADR-002`, `ADR-027`, `ADR-035` · [propuesta] la hora monótona viene del contrato de `SPK-12` |
| 2 | Detecta que alcanza el gateway, o la persona fuerza la sincronización. | Cliente · simulado | [fuente] `ADR-025`: «apenas hay red, y forzable» |
| 3 | **Abre la sesión de sincronización.** Lleva la credencial, el celular, la persona, el reloj de pared y el monótono, y la versión de configuración. | API Gateway → Ingreso de sincronización | [fuente] `ADR-028` · [modelo] |
| 4 | El gateway termina el TLS, limita la tasa y enruta. Identidad y permisos verifica la firma y la vigencia de la credencial. | API Gateway · Identidad y permisos | [modelo] «Verifica cada petición» |
| 5 | Gestión de dispositivos busca el celular y su asignación. Si no lo conoce, la sesión **entra marcada**: no se rechaza. | Gestión de dispositivos | [propuesta] ver ES-S13 |
| 6 | El servidor mide el desfase del reloj, guarda la sesión y responde. La respuesta trae el desfase, la hora del servidor y **los lotes ya acusados para ese celular**, que son el punto de reanudación. | Ingreso · Bitácora de auditoría · BD | [fuente] `ADR-031` · [medido] forma de `SPK-12` |
| 7 | El simulador entrega los lotes **en el orden del consecutivo del celular**. | Cliente · simulado | [propuesta] orden por consecutivo, nunca por la hora ni por el UUID |
| 8 | Por cada anotación se calcula la huella y se separan los tres casos de idempotencia: (1) mismo id y misma huella, no pasa nada; (2) mismo id y otra huella, se rechaza, se registra y se alerta; (3) otro id con la misma clave del hecho, entran las dos. | Ingreso de sincronización | [fuente] `ADR-027` |
| 9 | El motor de reglas valida cada anotación **con la versión de configuración con que se capturó**. Esa versión la lee de Datos maestros, donde los paquetes viejos siguen guardados. | Motor de reglas · servidor · Datos maestros y parametrización | [fuente] `ADR-029`, `ESC-57` · [modelo] |
| 10 | Si el servidor no está de acuerdo con el celular, la anotación **entra marcada «divergencia»**: no se descarta. Si le falta un obligatorio, entra y queda como trabajo del gerente. | Ingreso · Ciclo de producción | [fuente] `ADR-014` («nunca descarta en silencio») · decisión de Juan 15-sep · **[PENDIENTE]** choca con su respuesta del 4-oct |
| 11 | Se calcula la hora corregida. Si el reloj se movió durante la jornada, la anotación se marca como dudosa. | Ingreso | [fuente] `ADR-031`, `ADR-014` · umbral [PENDIENTE] |
| 12 | Se insertan las anotaciones y el acuse del lote **en la misma transacción**. Si se corta entre las dos escrituras, no queda un lote acusado sin datos. | Ingreso → Base de datos de la finca | [medido] `SPK-12` |
| 13 | Se actualiza el caché de producciones activas y se encolan los trabajos: recalcular la proyección viva (simulado), refrescar las tablas del tablero y guardar los avisos de descarte. | Caché · Planificador (cola en la base) · Servicio de notificación · Motor de proyección (**simulado**) | [modelo] · [fuente] `ADR-009`, `ADR-005` |
| 14 | Cada lote devuelve cuántas anotaciones se aceptaron, cuántas eran duplicadas, cuántas se rechazaron y por qué. | Ingreso → cliente | [medido] `SPK-12` |
| 15 | **Cierre.** La sesión termina completa o parcial. La respuesta lleva la credencial renovada y firmada, el desfase, la versión de configuración vigente y los avisos pendientes para esa persona. | Ingreso · Emisión de credenciales · Custodia de llaves (firma) · Notificación · Datos maestros | [fuente] `ADR-028`, `ADR-007` · [modelo] |
| 16 | El simulador marca como confirmado lo acusado. **Qué borra y cuándo lo decide la política de retención.** | Cliente · simulado | **[PENDIENTE]** P-S03 |
| 17 | Durante todo el recorrido se escriben logs estructurados y métricas, con el id de la sesión como correlación y **sin datos de negocio**. | Observabilidad | [fuente] `ADR-026` (dos planos) · [propuesta] |
| 18 | El gerente o el administrador ven en la web lo esperado, lo recibido y lo que falta por celular y por cama, más las sesiones parciales, las marcadas y las rechazadas. | Aplicación web → Gateway → Consulta y tableros → BD | [fuente] `ADR-026` |

**Lo que el modelo dibuja y las fuentes contradicen.** El modelo, entre el ingreso y Ciclo de
producción, dice: «Registra como corrección abierta la captura que cambia un dato ya registrado». `RF-022` y `ADR-027` dicen que no
media nadie y que el estado lo decide la regla de lo más reciente. **Esta transacción sigue a
`RF-022`**. El choque queda en P-S02.

---

## 3 · Los canales: varios caminos para que el dato llegue

Que haya varios caminos es seguro **gracias a la idempotencia de `ADR-027`**. Si el mismo evento
llega por dos canales, el segundo no hace nada.

| Canal | Cómo funciona | Cuándo se usa | Estado |
|---|---|---|---|
| **C1 · HTTPS por el Wi-Fi de la finca** | Entrega por lotes, reanudable, con reintento cada vez más espaciado y algo de azar. Se dispara al detectar red. | Siempre: es el principal | [propuesta] sobre [fuente] `ADR-025` |
| **C2 · Sincronización forzada** | El mismo C1, pedido por la persona. | Cuando quiere salir de dudas antes de irse | [fuente] `ADR-025` |
| **C3 · Archivo de entrega** | El celular exporta su almacén de salida a un archivo **cifrado y firmado**. Se pasa por cable o USB y el administrador lo sube en la web, o lo deja en una carpeta que el backend vigila. **Entra por el mismo ingreso.** | El Wi-Fi o el gateway no funcionan y el celular tiene que entregar igual | [propuesta] |
| C4 · Punto de acceso del celular del gerente | No es otro protocolo: es otro camino de red cuando el router de la oficina falla. | Contingencia: ver `IA_ES3-nodo-danado-sin-red.md` | [propuesta] |
| ~~C5 · Datos móviles por internet hasta la finca~~ | Exigiría exponer el gateway a internet. | **No se recomienda.** Abre la finca y `CN-17` dice que en el cultivo no hay datos | descartada por la IA, confirmar |

**El límite honesto de C3.** Por archivo no hay intercambio en vivo, así que **el desfase no se puede
medir**. Se usa el último desfase conocido de ese celular y la anotación entra marcada «desfase
estimado» (`ADR-014`: marcar, no bloquear).

---

## 4 · La forma del contrato

Sale de `SPK-12/CONTRATO.md`, que es registro de IA. `SPK-12` está **aplazado**; aquí se reutiliza solo
la forma, que no depende de nada de lo que lo aplazó. Los nombres finales se aceptan con P-S05.

```
POST /ingesta/sesiones                    abre el transporte y mide el reloj
POST /ingesta/sesiones/{id}/lotes         entrega un lote; acuse uno a uno
POST /ingesta/sesiones/{id}/cierre        cierra como completa o parcial; trae la vuelta
POST /ingesta/archivos                    canal C3: el archivo de entrega entra por el mismo ingreso
```

```jsonc
// Una anotación dentro de un lote
{ "anotacion_uuid": "uuid v7 · opaco",                // ADR-027
  "consecutivo": 1842,                                 // orden del almacén de salida
  "sesion_captura_uuid": "…",                          // ADR-035
  "produccion_id": "…", "seccion_id": "…",             // clave del hecho (ADR-027), sin autor ni celular
  "campo_id": "…", "jornada": "…",                     // «jornada» = D1, PENDIENTE del cliente
  "valor": { … },                                      // JSONB (ADR-024)
  "t_crudo_ms": …, "t_mono_ms": …,                     // ADR-031 + propuesta de SPK-12
  "corrige_a": null,
  "version_config": "cfg.v1",                          // ADR-029: una sola versión
  "veredicto_local": "aceptada" }                      // propuesta: permite medir ESC-57
```

**Regla del contrato.** Todo campo del que dependa un orden o una decisión del servidor entra en la
huella. Si un campo así se queda fuera, un reenvío con otro valor parece duplicado y el error pasa
sin que nadie lo note. [medido] `SPK-12`.

---

## 5 · Qué componentes toca

**Contenedores: 6 de 9.** Aplicación de captura (simulada), Almacén del dispositivo (simulado), API
Gateway, Backend, Base de datos de la finca y Aplicación web.

**Componentes del backend: 16 de 20, 14 completos y 2 a medias.**

| Componente | Qué hace en TX-1 | Grado |
|---|---|---|
| Ingreso de sincronización | Abre y cierra sesiones, recibe lotes, aplica la idempotencia y escribe | completo |
| Motor de reglas · instancia servidor | Revalida con la versión de la anotación | completo |
| Datos maestros y parametrización versionada | Entrega el paquete viejo para validar y el vigente para la vuelta | completo |
| Gestión de dispositivos | Celular conocido o desconocido y su asignación | completo |
| Identidad y permisos | Verifica la credencial en cada petición | completo |
| Emisión de credenciales | Renueva la credencial en la vuelta | completo |
| Custodia de llaves | Firma la credencial | completo |
| Bitácora de auditoría | Guarda la sesión y sus conteos, solo inserción | completo |
| Ciclo de producción | Recibe lo incompleto y lo marcado como trabajo del gerente | completo |
| Servicio de notificación | Guarda los avisos de descarte y los entrega en la vuelta | completo |
| Caché de producciones activas | Lo actualiza el ingreso | completo |
| Planificador de tareas | Lleva la cola de trabajos en la base | completo |
| Consulta y tableros | Muestra lo esperado, lo recibido y lo que falta | completo |
| Observabilidad | Logs y métricas locales | completo |
| Motor de proyección | Recálculo vivo **simulado** por `BR-23` | a medias |
| Actualización y migraciones | Solo el esquema con Flyway | a medias |
| Servicio de respaldo · Generador de documentos · Interfaz de salida · Lectura del heredado | — | no los toca |

---

## 6 · Evaluación de los escenarios de calidad

Veredictos:
- **Se demuestra**: el simulador puede probarlo con una cifra.
- **Parcial**: TX-1 cubre una parte.
- **Choca**: el escenario dice algo que una decisión vigente ya cambió.
- **Fuera**: es de otra transacción.

| ESC | Qué pide | Cómo responde TX-1 | Veredicto |
|---|---|---|---|
| `ESC-01` | 0 perdidos, 0 duplicados, recuperable tras reiniciar | Duplicados en 0 y reanudación tras matar el simulador: se demuestra. «0 perdidos» si el celular muere **antes** de sincronizar choca con `ADR-002` y `ADR-025`, que aceptan recapturar | **Parcial · choca** en lo de 0 perdidos (corrección ya registrada) |
| `ESC-17` | Desfase de más de 5 min detectado y marcado | Desfase medido en cada sesión; marca por reloj movido | **Se demuestra** la detección. El umbral es [PENDIENTE] (`SPK-08`). «exige confirmación o corrección antes de aceptarlo» choca con `ADR-014` (marcar, no bloquear) |
| `ESC-20` | Aviso de celular vencido con cuántos registros tiene en cola | Hay último contacto por celular. **Cuántos tiene en cola no se sabe**: el celular no reporta (`ADR-026`) | **Parcial · choca** |
| `ESC-22` | Permiso retirado vale en la primera sincronización | La sesión verifica permisos al abrir | **Parcial**. Qué pasa con lo capturado antes de retirarlo: P-S07 |
| `ESC-33` | 100 % de camas esperadas contra capturadas | Paso 18 | **Se demuestra** si entra el tablero |
| `ESC-34` | Dos capturas de la misma cama el mismo día | Entran las dos; el estado se calcula al leer | **Se demuestra** «0 registros descartados automáticamente». «pide resolución antes de consolidar» choca con `RF-022`. Depende de `D1` |
| `ESC-38` | Todos sincronizan a la vez: ≤30 min, 0 rechazados, +60 % | N simuladores en paralelo con la carga de temporada | **Se demuestra**. Es la prueba de carga de la transacción |
| `ESC-40` | Bitácora inmodificable, intento registrado | Bitácora de solo inserción y permisos de base sin `UPDATE`/`DELETE` | **Se demuestra** |
| `ESC-46` | Orden de sincronizar en ≤5 min | La finca no puede despertar al celular | **Choca** con `ADR-025` (corrección ya registrada) |
| `ESC-47` | El celular muestra cuántos registros están pendientes | El simulador lo imprime; la pantalla real queda fuera | **Fuera** de la UI |
| `ESC-49` | Cada registro tiene autor identificado | La persona va en la credencial y en la sesión de captura | **Se demuestra** |
| `ESC-54` | Celular perdido: reportar lo no recuperable | Lista de camas a rehacer = asignado − recibido | **Se demuestra** la lista. «0 perdidos» choca (ya registrado) |
| `ESC-55` | Lo pendiente no alimenta la proyección | Choca con la decisión de Juan 15-sep y con su respuesta del 4-oct | **[PENDIENTE]** P-S01 |
| `ESC-57` | 0 % de divergencia local contra servidor con la misma versión | El simulador manda `veredicto_local` y el servidor cuenta las divergencias sin descartar | **Se demuestra** |
| `ESC-59` | Si la nube cae, la captura y la sincronización siguen | TX-1 no toca la nube | **Se demuestra** con la nube apagada. Su texto «sincroniza todo lo capturado al restablecerse el servicio» supone que la sincronización va a la nube: choque ya visto |
| `ESC-60` | ≤1 h entre sincronizar y verlo en la web | Tiempo del lote al tablero | **Se demuestra** si entra el tablero |
| `ESC-61` | Temporada alta sin rechazar | Igual que `ESC-38` | **Se demuestra** |
| `ESC-05` | Proyección actualizada en ≤1 h | Recálculo simulado | **Fuera**: `BR-23` |
| `ESC-07` · `ESC-23` · `ESC-44` | La configuración nueva llega en ≤1 ciclo | La vuelta entrega la versión vigente | **Parcial**: publicar la configuración es otra funcionalidad |

---

## 7 · Event storming · lo que puede pasar al sincronizar

Formato de Juan:
- título y descripción breve;
- **estímulo** · **entorno** · **qué pasó**;
- **cómo lo abordamos**, que es el camino alterno;
- en las tarjetas que lo permiten, una línea **Prueba** con cómo lo reproduce el simulador.

### ES-S01 · Sincronización completa al volver a la oficina
*El camino feliz. Es el diagrama de secuencia del mazo.*
- **Estímulo:** el operario entra al Wi-Fi de la oficina con 2 sesiones de captura y 380 anotaciones.
- **Entorno:** jornada normal, nodo sano y nube indiferente.
- **Qué pasó:** sesión abierta, desfase medido, 4 lotes acusados, cierre completo, credencial renovada y tablero al día.
- **Cómo lo abordamos:** es la línea base. Todas las demás tarjetas se miden contra esta.
- **Prueba:** `simulador entregar --jornada demo-380.json`.

### ES-S02 · Se corta el Wi-Fi a mitad de un lote
*El operario se aleja del punto de acceso mientras sincroniza.*
- **Estímulo:** la red cae después de que el servidor guardó el lote 3 y antes de que el celular reciba el acuse.
- **Entorno:** oficina con Wi-Fi intermitente y 6 lotes pendientes.
- **Qué pasó:** el servidor tiene el lote 3 y el celular cree que no. La sesión quedó abierta en el servidor.
- **Cómo lo abordamos:**
  - Pasado un tiempo sin actividad, el servidor cierra la sesión como **parcial** (`ADR-028`).
  - Al volver la red, el celular abre una sesión nueva. La respuesta trae los lotes acusados **por celular**, y el celular sigue desde el 4.
  - Si reenvía el 3, cuenta como duplicado y no pasa nada.
- **Prueba:** `--cortar-tras-lote 3`.

### ES-S03 · Se va la luz en el nodo a mitad de un lote
*El servidor muere mientras escribe.*
- **Estímulo:** corte de energía con la transacción del lote 5 a medias.
- **Entorno:** nodo sin UPS o con la UPS agotada.
- **Qué pasó:** la base, al volver, deshace lo que no alcanzó a confirmar. El lote 5 no quedó ni guardado ni acusado.
- **Cómo lo abordamos:**
  - El lote y su acuse van en **la misma transacción**, así que no hay estado intermedio.
  - Al arrancar, el backend cierra como parciales las sesiones que quedaron abiertas.
  - El celular, que no recibió acuse, reenvía.
- **Prueba:** Testcontainers matando PostgreSQL a mitad de una inserción.

### ES-S04 · La respuesta se pierde y el celular reenvía
*El caso más común de todos.*
- **Estímulo:** llega el mismo lote dos veces.
- **Entorno:** cualquier red real.
- **Qué pasó:** las mismas anotaciones, con la misma huella.
- **Cómo lo abordamos:** caso 1 de `ADR-027`. No pasa nada, se cuenta como duplicada y **no se reescribe la sesión que la trajo**.
- **Prueba:** `--repetir-lotes 1000`. El estado tiene que quedar idéntico (criterio de T4).

### ES-S05 · El mismo identificador llega con otro contenido
*Un error de programa o datos corruptos en el celular.*
- **Estímulo:** un `anotacion_uuid` ya guardado llega con otro valor.
- **Entorno:** después de una actualización de la aplicación, o con el almacén dañado.
- **Qué pasó:** no es un reenvío: es corrupción.
- **Cómo lo abordamos:**
  - Se rechaza, se registra y se alerta. **Nunca se sobrescribe** (`ADR-027`).
  - **Propuesta:** el rechazo vuelve al celular en la respuesta del lote y queda allí en una bandeja de «rechazadas», visible y sin reintento automático.
  - En el tablero del administrador aparece con el celular y la sesión.
- **Prueba:** `--corromper-anotacion`.

### ES-S06 · Dos operarios anotan la misma sección el mismo día
*Es `BR-N4`: un problema del negocio que parecía de sincronización.*
- **Estímulo:** dos anotaciones distintas con la misma clave del hecho.
- **Entorno:** dos celulares sin red que pasaron por la misma cama.
- **Qué pasó:** las dos son válidas y dicen cosas distintas.
- **Cómo lo abordamos:**
  - **Entran las dos** (`ADR-027`).
  - El estado es **una vista**: gana la de hora corregida más reciente y el empate se resuelve por id (`ADR-031`).
  - Se avisa a quien perdió (`RF-022`).
  - **El modelo la manda al gerente como corrección abierta y eso choca.** Ver P-S02.
- **Prueba:** `--dos-celulares --misma-seccion`.

### ES-S07 · El celular tiene el reloj adelantado tres horas
*El sesgo que nadie había nombrado.*
- **Estímulo:** sello crudo +3 h.
- **Entorno:** un celular mal configurado desde hace semanas.
- **Qué pasó:** sin corrección, ese celular ganaría todos los choques, siempre y en silencio.
- **Cómo lo abordamos:**
  - Se mide el desfase al abrir la sesión y se ordena por la **hora corregida**. El sello crudo no se toca nunca (`ADR-031`).
  - El desfase se le devuelve al celular.
- **Prueba:** la prueba de T4: dos celulares desfasados a propósito tienen que dar el mismo estado que si estuvieran en hora.

### ES-S08 · Alguien cambió la hora del celular a mitad de la jornada
*El desfase medido al final ya no vale para lo capturado antes.*
- **Estímulo:** el reloj de pared salta una hora entre dos capturas.
- **Entorno:** celular compartido; alguien lo «arregló».
- **Qué pasó:** la corrección aplicada es una estimación equivocada para parte de la jornada.
- **Cómo lo abordamos:**
  - **Propuesta de `SPK-12`:** con la hora monótona se detecta el salto y la anotación se **marca** (`ADR-014`). Medido: sin la hora monótona, la estimación falla en 1 de cada 5 hechos.
  - El umbral está [PENDIENTE].
- **Prueba:** `--salto-de-reloj 3600`.

### ES-S09 · La captura llega incompleta
*Falta un obligatorio, por ejemplo el número de líneas.*
- **Estímulo:** anotación con un campo obligatorio vacío.
- **Entorno:** la cama no trabaja con líneas, o falta el lote en el celular.
- **Qué pasó:** el dato es útil a medias.
- **Cómo lo abordamos:** **hay dos respuestas tuyas que no coinciden:**
  - **15-sep:** entra y queda como trabajo del gerente.
  - **4-oct:** lo incompleto se descarta y lo raro pide confirmación. Tú mismo dijiste que estabas confundido y que se revisa luego.
  - **Hasta que decidas, TX-1 implementa la del 15-sep**, que es la única escrita como decisión. P-S01.
- **Prueba:** `--vaciar-obligatorio lineas`.

### ES-S10 · El celular trae una configuración vieja
*Lleva días sin pasar por la oficina.*
- **Estímulo:** anotaciones con `cfg.v1` cuando la vigente es `cfg.v3`.
- **Entorno:** operario que no sincronizó en una semana.
- **Qué pasó:** se capturó con reglas viejas.
- **Cómo lo abordamos:**
  - Se valida **con `cfg.v1`**, que sigue guardada porque los paquetes son inmutables (`ADR-029`).
  - La vuelta le entrega la `v3`.
  - **Propuesta:** si llega una versión que el servidor no conoce, la anotación entra marcada «versión desconocida». No se rechaza.
- **Prueba:** `--version-config cfg.v1`.

### ES-S11 · El servidor rechaza lo que el celular aceptó
*Las dos implementaciones de las reglas no piensan igual.*
- **Estímulo:** `veredicto_local=aceptada` y el servidor la rechaza con la misma versión.
- **Entorno:** cualquier día.
- **Qué pasó:** divergencia. `ESC-57` pide 0 %.
- **Cómo lo abordamos:**
  - **No se descarta**: entra marcada «divergencia».
  - La observabilidad cuenta las divergencias **sin el dato**.
  - Cada divergencia es un fallo del motor, no del operario.
  - `SPK-09` midió **0 % en el veredicto y 1,95 % en el motivo** entre dos lenguajes.
- **Prueba:** batería de casos dorados de `SPK-09`.

### ES-S12 · La credencial venció antes de llegar a la oficina
*Más de 24 h sin red (`ADR-007`).*
- **Estímulo:** la sesión se abre con una credencial vencida.
- **Entorno:** turno largo o fin de semana sin pasar por la oficina.
- **Qué pasó:** la persona existe y lo capturado es válido, pero el transporte no está autenticado.
- **Cómo lo abordamos:**
  - **Propuesta:** separar dos cosas. **Quién capturó** lo prueban las sesiones de captura, hechas con la credencial vigente en su momento. **Quién entrega** se reautentica en la oficina con PIN o contraseña contra el servidor.
  - Lo capturado entra; se renueva la credencial.
  - Si `t_crudo` es posterior al vencimiento, la anotación entra marcada.
- **Prueba:** `--credencial-vencida`.

### ES-S13 · Llega un celular que el servidor no conoce
*Reinstalaron la aplicación o es un celular nuevo.*
- **Estímulo:** `dispositivo_uuid` sin registro.
- **Entorno:** después de un cambio de celular.
- **Qué pasó:** las capturas son reales, pero el origen no está registrado.
- **Cómo lo abordamos:**
  - **Propuesta:** si la **persona** tiene credencial válida, entra marcado y aparece como «no registrado» en el tablero.
  - Si ni la persona es válida, se rechaza la sesión entera.
- **Prueba:** `--celular-nuevo`.

### ES-S14 · Temporada alta: todos sincronizan a la vez
*Cierre de jornada con un 60 % más de registros.*
- **Estímulo:** N sesiones simultáneas.
- **Entorno:** marzo o abril, con un 30–40 % más de personal.
- **Qué pasó:** pico de escritura en el nodo.
- **Cómo lo abordamos:**
  - Lotes acotados y cola en la base (`ADR-009`).
  - Si no aguanta en ≤30 min, el disparador de `ADR-009` mete una pieza dedicada o un nodo más grande, **y cambia el precio** (T4).
- **Prueba:** `simulador carga --celulares 13 --factor 1.6`. Trece son las ~10 personas de una instalación (`CN-30`) con el 30 % más de temporada; el número real de celulares está [PENDIENTE].

### ES-S15 · No hay Wi-Fi en la oficina: entrega por archivo
*El canal C3.*
- **Estímulo:** el gateway no responde por red, pero el nodo está vivo.
- **Entorno:** router dañado o Wi-Fi caído.
- **Qué pasó:** el celular no puede entregar por C1.
- **Cómo lo abordamos:**
  - Exporta un archivo cifrado y firmado, que el administrador sube.
  - Entra por el mismo ingreso, con la misma idempotencia.
  - Desfase estimado y marcado.
  - Si después llega también por C1, son duplicados.
- **Prueba:** `simulador exportar` y `POST /ingesta/archivos`.

### ES-S16 · Llega una corrección antes que lo que corrige
*`corrige_a` apunta a algo que aún no está.*
- **Estímulo:** corrección de otro celular que sincronizó primero.
- **Entorno:** dos celulares, con el original todavía en el campo.
- **Qué pasó:** la referencia queda colgando.
- **Cómo lo abordamos:**
  - Se acepta marcada y se reconcilia cuando llegue el original. **No se rechaza** (`CN-24`).
  - Si nunca llega, sale en la lista de camas a rehacer.
- **Prueba:** `--corrige-a-colgante`.

### ES-S17 · Se pierde un celular con la jornada adentro
*Lo único que no se recupera: se recaptura.*
- **Estímulo:** el celular no vuelve.
- **Entorno:** fin de jornada.
- **Qué pasó:** lo no sincronizado se perdió (`ADR-002`, `ADR-017`).
- **Cómo lo abordamos:** la lista de camas a rehacer sale de restar lo recibido a lo asignado (`ADR-025`, `ADR-026`). **Solo sirve si el dato es reciente**: un corte no se recaptura.
- **Prueba:** `GET` de la lista de camas a rehacer para ese celular.

---

## 8 · Casos de uso propuestos

Los diagramas están en el mazo, lámina «TX-1 · casos de uso».

| ID | Caso de uso | Actor | Incluye |
|---|---|---|---|
| UC-S1 | Sincronizar la jornada | Operario de campo, con el simulador en su lugar | abrir sesión · entregar lotes · cerrar sesión |
| UC-S2 | Forzar la sincronización | Operario de campo | UC-S1 |
| UC-S3 | Entregar la jornada por archivo | Operario y Administrador del sistema en la finca | exportar · subir · UC-S1 por el mismo ingreso |
| UC-S4 | Ver el avance de la jornada (esperado, recibido, falta) | Gerente de producción | — |
| UC-S5 | Pedir la lista de camas a rehacer de un celular | Gerente de producción | UC-S4 |
| UC-S6 | Revisar lo marcado: hora dudosa, incompleto, divergencia, rechazado | Gerente de producción | — |
| UC-S7 | Recibir los avisos de descarte en la vuelta | Operario de campo | parte de UC-S1 |
| UC-S8 | Registrar un celular y su asignación | Administrador del sistema en la finca | **precondición**; en TX-1 lo crea el guion de datos |

---

## 9 · Lo que tienes que decidir antes de programar TX-1

Cada pregunta trae la opción que recomienda la IA en primer lugar.

| ID | Pregunta | Opciones | Bloquea |
|---|---|---|---|
| **P-S01** | Captura incompleta: ¿la del 15-sep o la del 4-oct? | (a) **15-sep: entra y es trabajo del gerente** · (b) 4-oct: se descarta y lo raro pide confirmación · (c) mezcla: vacío total se descarta, parcial entra | paso 10 |
| **P-S02** | Choque de dos capturas: ¿`RF-022` o el modelo? | (a) **`RF-022`: gana la más reciente, sin mediación, con aviso** · (b) el modelo: corrección abierta para el gerente | paso 10 y ES-S06 |
| **P-S03** | ¿Qué borra el celular y cuándo? | (a) **retiene hasta que la anotación esté dentro de un respaldo que salió del nodo** (segundo acuse; ver ES3) · (b) borra al primer acuse (`ADR-002` literal) · (c) retiene N días | paso 16 y la pérdida cero |
| **P-S04** | ¿Qué es «la jornada» en la clave del hecho? | Es `D1`, del cliente. **Propuesta para no frenar:** parámetro del paquete de configuración con valor provisional «día natural de la finca» | ES-S06 |
| **P-S05** | Nombres y forma del contrato | (a) **la forma de `SPK-12` con los nombres de §4** · (b) otra | todo |
| **P-S06** | ¿Entra el canal C3 por archivo en la primera versión? | (a) **sí, porque reutiliza el ingreso** · (b) después | ES-S15 |
| **P-S07** | Permiso retirado: ¿qué pasa con lo que esa persona capturó antes? | (a) **entra lo capturado antes del retiro y lo posterior entra marcado** · (b) se rechaza todo | ESC-22 |
| **P-S08** | ¿Entra el tablero (paso 18) en TX-1? | (a) **sí: cierra la transacción a ojos del negocio y suma un contenedor** · (b) no, solo API | contenedores 6 contra 5 |
| **P-S09** | Umbral de la hora dudosa | Es `SPK-08`. **Valor provisional propuesto: 60 s de salto monótono**, como en `SPK-12` | ES-S08 |
| **P-S10** | ¿Cuánto espera el servidor antes de cerrar una sesión como parcial? | **Propuesta: 10 min sin actividad** | ES-S02 |

---

## 10 · Cuándo TX-1 está terminada

Del criterio de T4 (`FlorLogic-tandas-de-construccion.md`), más tres pruebas propias:

1. Reenviar el mismo evento mil veces no cambia el estado ni la procedencia.
2. Un intercambio cortado a la mitad deja una sesión parcial, y el reintento la completa sin duplicar.
3. La lista de camas a rehacer sale bien para un celular concreto.
4. Dos celulares con los relojes desfasados a propósito dan el mismo estado que si estuvieran en hora.
5. **[propuesta]** Se corta la base a mitad de un lote: no queda ningún lote acusado sin datos.
6. **[propuesta]** Trece simuladores con un 60 % más de carga terminan en ≤30 min con 0 rechazos por carga (`ESC-38`).
7. **[propuesta]** La misma jornada entregada por C1 y por C3 da el mismo estado.
