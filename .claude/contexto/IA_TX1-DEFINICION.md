REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 20-oct-2026, a pedido de Juan («define TX-01 con toda la información que existe tratando este tema»)
Estado de revisión: SIN REVISAR
Manda por encima: `IA_00-LEEME-transacciones.md` (aceptación) · `IA_TX1-sincronizacion.md` (rama `documentacion-desarrollo`) · `ESPEC-contrato-ingesta.md` · `ESPEC-dominio-ingesta.md` · `ADR.xlsx` · la palabra de Juan

# TX-1 · definición consolidada

Este documento **no decide nada**. Reúne en un solo lugar lo que dicen sobre TX-1 los once documentos que la
tratan, con el estado de cada pieza al 20-oct-2026. Si algo de aquí choca con la fuente citada, manda la fuente.

## 0 · Definición en un párrafo

**TX-1 es «Sincronizar una jornada ya capturada».** Un celular, que en esta transacción es el **simulador de
contrato**, llega a la oficina de la finca con la jornada guardada sin red. Por el Wi-Fi local entrega lo capturado
al **nodo de finca**: abre una sesión de sincronización, manda las anotaciones en **particiones** y cierra la sesión.
Cada anotación atraviesa el gateway, la identidad, la idempotencia, las reglas, la bitácora y la base, y recibe un
juicio: `aplicada`, `marcada`, `rechazada` o `sin_efecto`. El celular se lleva de vuelta el acuse, el desfase de su
reloj, la credencial renovada, la versión de configuración vigente y los avisos. El gerente ve en la web lo esperado,
lo recibido y lo que falta. Es la transacción que más componentes del backend toca (16 de 20).

Origen: Juan, 8-oct-2026: «2 transacciones diferentes completas que ocupen la mayor cantidad de componentes». La
otra es TX-2, respaldo con observabilidad. Fuente: `IA_00-LEEME-transacciones.md` e `IA_TX1-sincronizacion.md`.

## 1 · Estado de aceptación (lo que autoriza código)

| Pieza | Estado | Fuente |
|---|---|---|
| **TX-1** (fila) | `AJUSTAR 19-oct-2026` · solo el **camino mínimo funcional**: POST de una partición del simulador al backend-finca, que guarda cada anotación y responde `200`; cualquier fallo de forma es `422` sin motivo; la sesión `{id}` no se verifica. **20-oct:** Juan aceptó en la sesión «el alcance del dominio mínimo de TX-1»; la fila aún no lo refleja | `IA_00-LEEME` · `ESPEC-dominio-ingesta.md` Ficha |
| TX-1.K · forma del contrato | `ACEPTADO 19-oct` (la de `ESPEC-contrato-ingesta.md` §2 y §4b) | `IA_00-LEEME` |
| TX-1.C · canales | `POR ACEPTAR` | `IA_TX1` §3 |
| TX-1.T · criterio de terminado | `POR ACEPTAR` | `IA_TX1` §10 |
| ES-S · 17 tarjetas · UC-S · 8 casos de uso | `POR ACEPTAR` | `IA_TX1` §7, §8 |
| P-S01, P-S02, P-S03 | `ACEPTADO 13-oct` | ver §6 |
| P-S04, P-S05 | `ACEPTADO 19-oct` | ver §6 |
| P-S06 … P-S10 | `POR ACEPTAR` | ver §6 |
| TEC-01 … TEC-30 | `ACEPTADO 13-oct` (con las versiones que fija `IA_TECNOLOGIAS-vigentes.md`) | `IA_00-LEEME` |

`[!]` `.claude/CLAUDE.md` dice «TX-1 y TX-2 siguen `POR ACEPTAR`»: está desactualizado frente a la tabla.

## 2 · Alcance

| Entra | Queda fuera, a propósito |
|---|---|
| El celular **simulado**: carga una jornada ya hecha y la entrega | La captura en pantalla, el QR de la cama y las reglas en el celular |
| Gateway → backend → base, con todos los componentes del recorrido | Preparar la jornada desde la web (aquí es dato de prueba) |
| La vuelta: acuse, desfase, credencial, versión de configuración, avisos | La aplicación real del celular |
| Canales C1 y C2, más C3 por archivo | Corregir lo cerrado y cerrar la producción |
| El tablero de lo esperado, lo recibido y lo que falta (si P-S08 lo acepta) | La proyección real (bloqueada por `BR-23`; aquí es simulada) |

**Precondiciones**, que crea un guion de datos: una finca con producción activa y secciones; el paquete de
configuración `v1` firmado; un celular registrado y una persona con credencial vigente; la asignación de camas.

## 3 · El recorrido completo (18 pasos)

| # | Qué pasa | Componente | Base |
|---|---|---|---|
| 1 | El simulador carga la jornada: cada anotación con UUID v7, sesión de captura, hora cruda y monótona | Cliente y almacén (simulados) | ADR-002, 027, 035 |
| 2 | Detecta red o la persona fuerza la sincronización | Cliente | ADR-025 |
| 3 | **Abre la sesión de sincronización** con credencial, celular, persona, relojes y versión de configuración | Gateway → Ingreso | ADR-028 |
| 4 | TLS, límite de tasa y enrutamiento; se verifica la credencial | Gateway · Identidad | modelo |
| 5 | Se busca el celular; si es desconocido, la sesión **entra marcada** | Dispositivos | propuesta (ES-S13) |
| 6 | Se mide el desfase, se guarda la sesión y se responde con lo ya acusado (punto de reanudación) | Ingreso · Bitácora · BD | ADR-031, SPK-12 |
| 7 | Se entregan las particiones **en orden de consecutivo** | Cliente | propuesta |
| 8 | Huella y los tres casos de idempotencia: mismo id y huella → nada; mismo id y otra huella → rechazo y alerta; otro id con la misma clave del hecho → entran las dos | Ingreso | ADR-027 |
| 9 | Se valida con **la versión de configuración con que se capturó** | Reglas · Configuración | ADR-029, ESC-57 |
| 10 | Si el servidor discrepa, entra marcada «divergencia»; si falta un obligatorio, entra como trabajo del gerente | Ingreso · Ciclo | ADR-014, P-S01 |
| 11 | Hora corregida; si el reloj se movió, se marca hora dudosa | Ingreso | ADR-031 · umbral P-S09 |
| 12 | Anotaciones y acuse **en la misma transacción** | Ingreso → BD | SPK-12 |
| 13 | Caché, cola de trabajos (proyección simulada, tablero, avisos) | Caché · Planificador · Notificación · Proyección | ADR-009, ADR-005 |
| 14 | Cada partición devuelve cuántas se aceptaron, duplicaron y rechazaron, y por qué | Ingreso → cliente | SPK-12 |
| 15 | **Cierre** completo o parcial: credencial renovada y firmada, desfase, versión vigente, avisos | Ingreso · Credenciales · Llaves · Notificación · Configuración | ADR-028, ADR-007 |
| 16 | El simulador marca lo confirmado; qué borra lo decide la retención | Cliente | P-S03 |
| 17 | Logs y métricas con el id de sesión, **sin datos de negocio** | Observabilidad | ADR-026 |
| 18 | El gerente ve esperado, recibido y faltante por celular y cama, más lo parcial, marcado y rechazado | Web → Consulta → BD | ADR-026 |

**Choque conocido:** el C4 dice que la captura que cambia un dato registrado abre una corrección para el gerente;
`RF-022` y `ADR-027` dicen que no media nadie y gana lo más reciente. TX-1 sigue a `RF-022` (P-S02, aceptada).

## 4 · Canales

| Canal | Qué es | Estado |
|---|---|---|
| **C1** HTTPS por el Wi-Fi de la finca | Principal: particiones reanudables, reintento con espera creciente y azar | propuesta sobre ADR-025 |
| **C2** Sincronización forzada | El mismo C1, pedido por la persona | ADR-025 |
| **C3** Archivo de entrega | Exportación cifrada y firmada que sube el administrador; entra por el mismo ingreso; el desfase no se mide y la anotación entra «desfase estimado» | propuesta · P-S06 |
| C4 Punto de acceso del gerente | Otro camino de red en contingencia (`IA_ES3`) | propuesta |
| ~~C5~~ Datos móviles hasta la finca | Exigiría exponer el gateway; `CN-17`: no hay datos en el cultivo | descartada por la IA, sin confirmar |

Tener varios canales es seguro gracias a la idempotencia de `ADR-027`.

## 5 · El contrato (TX-1.K, aceptado)

Rutas (P-S05): `POST /ingesta/v1/sesiones` · `POST /ingesta/v1/sesiones/{id}/particiones` ·
`POST /ingesta/v1/sesiones/{id}/cierre` · `POST /ingesta/v1/archivos` (fuera de la sesión). Se dice
**«partición»**, no «lote»; `IA_TX1` §2 y §7 todavía dicen «lote».

Lo esencial del contrato vive en `codigo/contrato/espec/ESPEC-contrato-ingesta.md`:
- **Identidad e idempotencia:** `anotacion_uuid` (UUID v7, del celular) más una huella canónica versionada
  (`{"alg": "h<n>", "sha256": …}`). Todo campo del que dependa una decisión del servidor entra en la huella.
- **Juicio por anotación:** `aplicada`, `marcada` (con códigos), `rechazada` (con motivo) o `sin_efecto` (reenvío).
- **Respuesta:** conteo y excepciones, salidas del estado guardado; `confirmado_hasta` por celular.
- **Frontera:** un fallo de forma de la partición es `422` (A-32). Ningún campo desconocido entra. Ningún punto de
  entrada lanza ante una entrada externa (A-57): lector estricto en el nodo, tolerante en el celular.
- **Retiradas:** `corrige_a` y la corrección colgante (A-4, A-28): el operario no corrige (A-49). Por eso ES-S16 de
  `IA_TX1` ya no aplica tal cual.
- **Pendiente:** la ronda 2 (`/sesiones`, `/cierre`, `/archivos`) y los Segmentos 3 a 7 (huella, errores, esquemas,
  validador único, dorados). El Segmento 5 está bloqueado: falta fila TEC para la biblioteca de JSON Schema.

## 6 · Preguntas P-S

| ID | Pregunta | Estado / respuesta |
|---|---|---|
| P-S01 | Captura incompleta | **(a) 15-sep:** entra y es trabajo del gerente |
| P-S02 | Choque de dos capturas | **(a) `RF-022`:** gana la más reciente, sin mediación, con aviso |
| P-S03 | Qué borra el celular y cuándo | **(a)** retiene hasta que la anotación esté dentro de un respaldo que salió del nodo (enlaza con TX-2) |
| P-S04 | Qué es «la jornada» | Fecha `AAAA-MM-DD` del día natural en la zona de la finca; parámetro del paquete de configuración (por defecto `America/Bogota`) |
| P-S05 | Nombres del contrato | Las rutas `v1` de §5, con «partición» |
| P-S06 | ¿C3 por archivo en la primera versión? | POR ACEPTAR · recomendada: sí |
| P-S07 | Permiso retirado y lo capturado antes | POR ACEPTAR · recomendada: lo anterior entra, lo posterior marcado |
| P-S08 | ¿Entra el tablero? | POR ACEPTAR · recomendada: sí (suma un contenedor) |
| P-S09 | Umbral de hora dudosa | POR ACEPTAR · propuesta: 60 s de salto monótono |
| P-S10 | Sesión parcial por inactividad | POR ACEPTAR · propuesta: 10 min |

Ligadas: **P-N01** (`ACEPTADO 13-oct`): credencial de 24 h más 7 días de gracia solo para capturar (`IA_ES3`).

## 7 · Componentes y módulos

**Contenedores: 6 de 9** (captura y almacén simulados, gateway, backend, base, web). **Backend: 16 de 20**.

| Módulo (`backend-finca`) | Papel en TX-1 | Prioridad propuesta | Código hoy |
|---|---|---|---|
| `ingesta` | Sesiones, particiones, idempotencia, escritura | Nivel 0 · 1 | Canal hecho; dominio en curso |
| `configuracion` | Catálogo, versiones, zona horaria | Nivel 0 · 2 | solo `package-info` |
| `reglas` | Juicio de cada anotación con su versión | Nivel 0 · 3 | solo `package-info` |
| `ciclo` | Lo incompleto y marcado como trabajo del gerente | Nivel 0 · 4 | solo `package-info` |
| `consulta` | Esperado, recibido, faltante (y caché) | Nivel 0 · 5 | solo `package-info` |
| `identidad` · `llaves` | Verificar, renovar y firmar la credencial | Nivel 1 · 6-7 | solo `package-info` |
| `dispositivos` | Celular conocido o no, y su asignación | Nivel 1 · 8 | solo `package-info` |
| `bitacora` | Sesiones y conteos, solo inserción | Nivel 1 · 9 | solo `package-info` |
| Gateway Nginx + CA de la finca | TLS y límite de tasa | Nivel 1 · 10 | — |
| `notificacion` · `trabajos` | Avisos (al gerente y en la vuelta) y su cola | Nivel 2 · 11-12 | solo `package-info` |
| `proyeccion` | Recálculo **simulado** | Nivel 3 | solo `package-info` |
| `observabilidad` | Logs y métricas locales, transversal | desde ya | Actuator |

Orden de prioridad: `IA_PRIORIDAD-componentes-TX1.md` (SIN REVISAR). Patrones: receptor idempotente con huella en
`ingesta`; eventos internos con registro de publicación de Spring Modulith de `ingesta` hacia `notificacion`,
`proyeccion` y `consulta` (`IA_TECNOLOGIAS-y-estructura.md`). Principio vigente: **el dominio tira de la
infraestructura**; un componente entra cuando una regla lo pide.

## 8 · Escenarios de calidad

- **Se demuestran:** ESC-17 (detección del desfase), ESC-33 y ESC-60 (si entra el tablero), ESC-38 y ESC-61
  (carga de temporada), ESC-40 (bitácora inmodificable), ESC-49 (autor), ESC-54 (lista de camas a rehacer),
  ESC-57 (0 % de divergencia), ESC-59 (con la nube apagada).
- **Parciales o en choque:** ESC-01 («0 perdidos» si el celular muere antes de sincronizar), ESC-20, ESC-22,
  ESC-34, ESC-46 (la finca no despierta al celular), ESC-07/23/44.
- **Pendiente:** ESC-55 (resuelto en dirección por P-S01). **Fuera:** ESC-05, ESC-47.

## 9 · Event storming ES-S (17 tarjetas, POR ACEPTAR)

| Tarjeta | Situación | Cómo se aborda |
|---|---|---|
| S01 | Camino feliz: 380 anotaciones, 4 particiones | Línea base |
| S02 | Se corta el Wi-Fi a mitad de partición | Sesión parcial por inactividad; reanudación con lo acusado; reenvío = duplicado |
| S03 | Se va la luz en el nodo | Partición y acuse en una transacción; al arrancar, sesiones abiertas → parciales |
| S04 | Se pierde la respuesta y se reenvía | `sin_efecto`; estado idéntico tras 1000 reenvíos |
| S05 | Mismo id con otro contenido | Rechazo, registro y alerta; nunca se sobrescribe |
| S06 | Dos operarios, misma sección y día | Entran las dos; gana la de hora corregida más reciente; aviso (P-S02) |
| S07 | Reloj adelantado 3 h | Se ordena por hora corregida; el sello crudo no se toca |
| S08 | Cambiaron la hora a mitad de jornada | Hora monótona detecta el salto; se marca (P-S09) |
| S09 | Captura incompleta | Entra, trabajo del gerente (P-S01) |
| S10 | Configuración vieja | Se valida con la vieja; la vuelta entrega la vigente; versión desconocida → marcada |
| S11 | Servidor discrepa del celular | Marcada «divergencia»; se cuenta sin el dato |
| S12 | Credencial vencida | Separar quién capturó de quién entrega; reautenticación en la oficina |
| S13 | Celular desconocido | Si la persona es válida, entra marcado; si no, se rechaza la sesión |
| S14 | Temporada alta | Particiones acotadas y cola en la base; si no aguanta, cambia el hardware y el precio |
| S15 | Sin Wi-Fi: archivo | C3, mismo ingreso, desfase estimado |
| S16 | Corrección antes que lo corregido | **Superada por el contrato** (A-4 y A-28 retiradas, A-49) |
| S17 | Se pierde un celular | Lista de camas a rehacer = asignado − recibido |

Complemento: `IA_TX1-jornada-event-storming.md` (tarjetas `ES-P`, en el apartado del 18-oct, SIN REVISAR) describe la
jornada desde el negocio: asignación → captura sin red → sincronización → retención hasta el respaldo → borrado.

## 10 · Casos de uso UC-S (POR ACEPTAR)

UC-S1 Sincronizar la jornada (abrir, entregar, cerrar) · UC-S2 Forzar la sincronización · UC-S3 Entregar por archivo ·
UC-S4 Ver el avance de la jornada · UC-S5 Lista de camas a rehacer · UC-S6 Revisar lo marcado · UC-S7 Recibir avisos
en la vuelta · UC-S8 Registrar un celular y su asignación (precondición).

## 11 · Cuándo TX-1 está terminada (TX-1.T, POR ACEPTAR)

1. Reenviar el mismo evento mil veces no cambia el estado ni la procedencia.
2. Un intercambio cortado deja una sesión parcial y el reintento la completa sin duplicar.
3. La lista de camas a rehacer sale bien para un celular concreto.
4. Dos celulares desfasados a propósito dan el mismo estado que en hora.
5. *(propuesta)* Cortar la base a mitad de partición no deja ninguna partición acusada sin datos.
6. *(propuesta)* Trece simuladores con +60 % de carga terminan en ≤ 30 min sin rechazos por carga.
7. *(propuesta)* La misma jornada por C1 y por C3 da el mismo estado.

Por PR-04, sin la prueba automática de la tarjeta ES (ES-S01 para TX-1) no está terminada.

## 12 · Lo que ya existe en código (rama `codigo-desarrollo`, sin commit)

| Trabajo | Estado | Dónde |
|---|---|---|
| Contrato, Segmento 1: DTO y barrera de lectura | ACEPTADO (forzado) 19-oct | `codigo/contrato/` |
| **Canal**, Segmento 2: POST de partición simulador → nodo → PostgreSQL, `200` con conteo, reenvío `sin_efecto`, `422` sin motivo | Verificado 20-oct (65/65 con Docker); falta la explicación de Juan para aceptarlo. Traspasado a sus semillas (D-19) | `backend-finca/ingesta`, `simulador/`, `scripts/IA_probar-canal.sh` |
| **Dominio de ingesta**: `Anotacion` y `AvanceDelDispositivoAgregado`, juicio por anotación, `confirmado_hasta` sin huecos, eventos `AnotacionMarcada` y `AnotacionRechazada`, regla mínima `ReglaDatoAusente` (A-60…A-82) | ESPEC `LISTA PARA CÓDIGO` (≈88 %); Segmento 1 de 3 presentado, **espera el «sí» de Juan**; sin commit | copia visible, sin commit: `codigo/backend-finca/espec/ESPEC-dominio-ingesta.md` |

Semillas que continúan TX-1 (en `codigo/contrato/espec/IA_apartado-2026-10-18/semillas/`): caso de uso de ingreso,
adaptador de persistencia de ingreso, caso de uso del celular, endpoint de sesión y credencial, configuración
(catálogo y versiones), entidad producción y asignación.

## 13 · Tensiones y huecos que este documento encontró

1. **Estado de la fila TX-1:** la tabla dice `AJUSTAR 19-oct`; Juan aceptó el dominio mínimo el 20-oct en sesión;
   `CLAUDE.md` dice `POR ACEPTAR`. Hay que alinear los tres.
2. **«Lote» contra «partición»:** `IA_TX1` §2, §3 y §7 siguen diciendo «lote» (P-S05 lo cambió).
3. **ES-S16 y `corrige_a`:** el contrato los retiró (A-49: el operario no corrige); la tarjeta sigue en `IA_TX1`.
4. **Notificación sin red:** el encargo pide avisar al gerente por celular y correo, pero el nodo es local-first y
   correo y push no tienen fila TEC.
5. **Sesión `{id}` sin verificar:** el `DispositivoId` del dominio sale de la sesión, que el camino mínimo no verifica.
6. **K-115:** forma canónica de la huella (propia sobre Jackson 3 o biblioteca RFC 8785); bloquea el Segmento 3 del
   dominio y el 3 del contrato.
7. **WAF y Grafana:** el C4 y `IA_PRIORIDAD-componentes-TX1.md` los mencionan, pero no tienen fila TEC; el WAF
   añadiría un `403` que el contrato no conoce.

## 14 · Fuentes

| Documento | Rama / ruta |
|---|---|
| `IA_00-LEEME-transacciones.md` · `IA_TX1-sincronizacion.md` · `IA_TECNOLOGIAS-y-estructura.md` · `IA_ES3-nodo-danado-sin-red.md` · `IA_TX2-respaldo-y-observabilidad.md` · `IA_TRANSACCIONES-mazo.html` (diagramas) | `documentacion-desarrollo`: `docs/03-arquitectura/flujos/transacciones/` |
| `ESPEC-contrato-ingesta.md` y sus HISTORIAS · apartado del 18-oct y semillas | `codigo/contrato/espec/` |
| `ESPEC-dominio-ingesta.md` | copia visible, sin commit: `codigo/backend-finca/espec/` |
| `IA_RETOMAR-AQUI.md` · `IA_PRIORIDAD-componentes-TX1.md` · `IA_DICCIONARIO.md` · `IA_TECNOLOGIAS-vigentes.md` | `.claude/contexto/` |
