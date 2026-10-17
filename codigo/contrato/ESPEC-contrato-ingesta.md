# ESPEC · contrato de ingesta (TX-1.K)

REGISTRO DE IA · NO ES FUENTE — lo genera la skill DetallarParaCodificar a pedido de Juan, 16-oct-2026.
Manda por encima: `ADR.xlsx` · `docs/03-arquitectura/decisiones/` · `IA_00-LEEME-transacciones.md`.

## Ficha
| Campo | Valor |
|---|---|
| Definición acordada | Acuerdo entre el celular y el servidor de la finca para entregar lo capturado en una jornada: qué lleva cada mensaje, qué responde el servidor, cómo se reconoce un envío repetido y cómo cambia de versión sin romper a nadie. No es la lógica que decide si una anotación vale, ni la base de datos, ni el código que recibe la petición (acordada 16-oct, 2.ª vuelta) |
| Diccionario | `codigo/IA_DICCIONARIO.md` (compartido por todos los detallamientos) |
| Historia | `ESPEC-contrato-ingesta-HISTORIA.md` (rondas 1 a 1f, íntegra; las rondas 1g y 1h están en esta ESPEC) |
| Elemento | Módulo `codigo/contrato/`: los mensajes de los 4 POST de ingesta de TX-1; **esta parte 1 detalla solo `POST …/particiones`** (los otros 3, ronda 2) |
| Plantilla | `endpoint` (solo §1 ⟦1⟧-⟦10⟧; ⟦11⟧ frontera y controladores quedan fuera) |
| Dominio | Entra: forma de petición y respuesta de los 4 POST, versión, errores, huella · Sale: DTO como records, JSON Schema, `HuellaCanonica` · Fuera: controladores, puertos, reglas, base de datos |
| Generalidad | Contrato campo por campo |
| Para qué | Especificar para programar |
| Estado | BORRADOR |
| **Etapa actual** | 3 Preguntas · ronda 1h |
| **Dónde retomar** | Ronda 1h cerrada: **≈64,5 %**, coherencia no pasa (COH-61). Siguiente: pedir permiso para la ronda 1i, que empieza por K-75 (P-N01, de Juan) y COH-61; después COH-62, COH-63, COH-64, COH-66, COH-67, K-80, K-81, K-82 y los pares de A-2, A-13, A-19, A-20, A-22, A-44, A-49 ⟦4⟧ |
| Precisión | **≈64,5 % oficial** (verificador, ronda 1h, 16-oct): cobertura 77,9 · contradicciones 5 · prácticas 93 · trazabilidad 85. **Coherencia (D-04): no pasa** (COH-61 bloqueante). Proyección del verificador: ≈88 % si se cierran K-75 (de Juan), K-76..K-82 y los pares que faltan. Corregidos ya en el texto: K-76, K-77, K-78, K-79. Historia: 52 · 59 · 68 · 60 · 60 · 62 · 62 · 63 · 64,5 |
| Completitud | — · Explicación — |
| Avance por etapa | Tamaño ✔ · Consulta ✔ · Preguntas (rondas 1, 1b–1g; consolidación tras la 1f) · Generación — · Revisión — · Verificación — · Aceptación — |

## 1 · Propósito
Fijar el contrato que comparten el simulador del celular y el backend de la finca para entregar una
jornada ya capturada (TX-1): qué mensajes viajan en cada uno de los 4 POST, con qué campos, cómo se
identifica un duplicado y cómo evoluciona sin romper a nadie. Fuente: `IA_TX1-sincronizacion.md` §4,
TEC-10, TEC-11.

## 2 · Decisiones vigentes
Estado consolidado al 16-oct-2026 (rondas 1 a 1f), con los cambios de las rondas 1g y 1h. Cada fila dice la **regla vigente**,
sin historia; la historia de cada id está en `ESPEC-contrato-ingesta-HISTORIA.md`. Los ids no cambian,
para no romper las referencias del diccionario y de §4-§6.

**Retiradas:** A-4 (`corrige_a`) y A-28 (corrección colgante): el operario no corrige (A-49). A-25
(blindaje): ningún campo desconocido entra (A-32). A-43: va con despliegue y actualización (§7).

### Identidad, huella e idempotencia
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-1 | La identidad de una anotación es su `anotacion_uuid`, un UUID v7 generado en el celular. Es opaco: nada se ordena ni se filtra por su contenido; el único uso de su valor es **desempatar** de forma estable y sin significado cuando dos capturas tienen el mismo sello (A-2, A-45; ADR-031 §4) | ronda 1 · antes ADR-027 §1 (deprecado, K-41) | decidida |
| A-2 | Idempotencia por anotación: mismo id y misma huella → `sin_efecto`; mismo id y otra huella → **corrupta**: `rechazada` con `datos_invalidos`, se registra y se alerta, nunca se sobrescribe; **es fallo técnico**: el celular borra su copia con lápida y pide recapturar; el nodo de finca guarda en **cuarentena** los bytes de la versión rechazada, fuera del estado, y la alerta lleva las dos huellas (ronda 1h P9, COH-57; no reabre la decisión) (ronda 1g P6, elección de Juan, no la ★: se acepta el riesgo de perder la copia buena si un intermediario alteró los bytes; resuelve COH-50); otro id → entra y lo juzgan las reglas: si su orden de captura fue **reasignada** a otro dispositivo y ése la cumplió, aplica obsoleta/superada (A-45 ⟦1⟧); en cualquier otro caso (recaptura, dos capturas de la misma sección) se guardan las dos y gana la más reciente (RF-022, P-S02; con el mismo sello, el `anotacion_uuid` mayor; se avisa a quien perdió) — ronda 1g P1, resuelve K-52. **Orden por anotación** (ronda 1g P10, resuelve COH-37): ⟦1⟧ ¿el id existe, como anotación **o como constancia**? misma huella → `sin_efecto`; otra → corrupta; ⟦2⟧ forma por anotación (A-32: fallo técnico); ⟦3⟧ ¿su orden ya se cumplió? → obsoleta, **solo si su `version_config` es conocida**; ⟦4⟧ reglas (A-32) — orden ajustado en la ronda 1h P9 (COH-53). El id es único entre anotaciones y constancias | ronda 1 · antes ADR-027 §2 | decidida |
| A-3 | La clave del hecho la declara cada campo en el catálogo (por defecto producción · sección · campo · jornada) y la calcula el ingreso; el contrato solo garantiza que viajan sus datos. No incluye autor, dispositivo ni versión de configuración | ronda 1d P2 | decidida |
| A-19 | La huella es `SHA-256` de la anotación entera en forma canónica; el nodo de finca la recalcula siempre y nunca usa una que mande el celular | ronda 1 P7 | decidida |
| A-33 | `huella = SHA-256(JCS(quitarNulls(árbol JSON recibido)))`, con versión de algoritmo (`{"alg": "h1", "sha256": …}`); solo se comparan huellas de la misma versión. `quitarNulls` quita solo miembros de objeto que valen `null`, nunca elementos de arreglo, **en cualquier nivel, también dentro de `valor`**. Un miembro `null` vale lo mismo que uno ausente. Se calcula sobre el JSON recibido, nunca sobre el DTO. **Todo reenviador** (celular, buzón del gerente, nodo de contingencia) reenvía los bytes originales, sin volver a serializar | rondas 1b, 1c, 1d | decidida |
| A-38 | Los ids que genera el celular son UUID v7 en minúsculas (`^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$`). Si falla uno que identifica (`anotacion_uuid`, `particion_uuid`, `sesiones_captura[].uuid`) → `422 sobre-invalido`; si falla `sesion_captura_uuid`, **o está bien escrito pero no está declarado en `sesiones_captura[]`** (ronda 1h P4, resuelve K-50) → solo esa anotación, `rechazada` `datos_invalidos` (fallo técnico: sin autoría no entra ningún dato) | rondas 1b, 1d | decidida |
| A-42 | Una sola alerta por (`anotacion_uuid`, huella). El mismo id dos veces en una partición se procesa en el orden del arreglo: la segunda copia es `sin_efecto` si es igual, o corrupta si es distinta | ronda 1c P16 | decidida |
| A-49 | ⟦1⟧ **Corrige solo el gerente de producción**, en el tablero; no viaja aquí. ⟦2⟧ **Edición local:** el operario cambia en su lugar lo que todavía no salió en ninguna partición (mismo id, solo viaja la versión final). ⟦3⟧ **Inmutable desde que sale:** lo que ya viajó, aunque no tenga respuesta, no se edita; cambiarlo es una **recaptura** (id nuevo) y decide RF-022. ⟦4⟧ El único motivo obligatorio es el de una **desviación** brusca según las reglas: `datos.motivo_desviacion` (id del catálogo); si la regla la detecta y no hay motivo, entra `marcada` con el id de la regla y `desviacion_sin_motivo`. ⟦5⟧ La trazabilidad es contra el catálogo y los cambios bruscos, no contra las ediciones locales. ⟦6⟧ Juan depreca ADR-027 (K-41) | rondas 1e P4-P5, 1f P1 | decidida |

### La petición
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-12 | `POST /ingesta/v1/sesiones/{id}/particiones`: el celular entrega una partición de su almacén de salida; la llama una persona con credencial JWS | IA_TX1 pasos 7-14 · ADR-007 | confirmada |
| A-29 | Cabeceras: `Authorization: Bearer <JWS>`, `Content-Type: application/json`, `traceparent` opcional. No hay `Idempotency-Key`: `particion_uuid` solo correlaciona. Los logs llevan `sesion_id`, `particion_uuid` y `traceId`, sin datos de negocio | ronda 1 P19 · ADR-026 | decidida |
| A-30 | *Lote* (negocio): plantación definida de una variedad, en un periodo con fechas reales, que puede ocupar varias secciones; no se captura el lote, se capturan las camas. *Partición* (técnico): un trozo, de hasta `max_anotaciones_particion`, de lo pendiente de **un** celular, que sube en un POST; nunca mezcla celulares. Todos los celulares sincronizan en paralelo | rondas 1b, 1c | decidida |
| A-15 | La anotación es un sobre genérico: `anotacion_uuid`, `consecutivo`, `tipo`, `version_tipo`, `sesion_captura_uuid`, `tiempo`, `version_config`, `veredicto_local` y `datos`. Hoy solo existe `captura.campo`; cada tipo valida sus `datos` con su esquema | ronda 1 P2 | decidida |
| A-16 | `tiempo`: `crudo_ms` (reloj de pared, nunca se toca), `mono_ms` (reloj monótono), `arranque` y `desfase_declarado_ms` (el de la última sincronización; `null` si nunca sincronizó → desfase cero y marca `desfase_desconocido`) | ADR-031 §2 · SPK-12 | decidida |
| A-17 | `arranque` sube en cada encendido; `mono_ms` solo se compara dentro del mismo arranque. Si entre dos anotaciones seguidas del mismo arranque `|Δcrudo_ms − Δmono_ms|` pasa el umbral, la segunda se marca `hora_dudosa` (lo calcula el ingreso) | SPK-12 M2b · IA_TX1 paso 11 | decidida |
| A-18 | La partición declara sus `sesiones_captura[]`: `uuid`, `persona_id` (quien capturó), `credencial_jti` vigente al capturar, `inicio`, `version_app` y `version_motor` (las de al capturar; actualizar la app abre una sesión de captura nueva). Cada anotación la referencia por `sesion_captura_uuid`. El dispositivo va una vez en la sesión de sincronización (A-5). La credencial de la petición es **quien entrega** | rondas 1, 1b, 1c · ADR-029 · ES-S12 | decidida |
| A-20 | `datos` de `captura.campo`: `produccion_id`, `seccion_id`, `campo_id`, `jornada` (`AAAA-MM-DD`, provisional por P-S04), `valor` (JSON libre o `null`, validado por el catálogo de su versión), `motivo_ausencia` si falta el valor y `motivo_desviacion` si la regla lo pide. En v1 solo se anota a nivel de **sección**; cama, bloque o lote llegarán en una versión mayor cuando D1 decida la granularidad | rondas 1, 1c, 1d · ADR-024 | decidida (jornada: P-S04) |
| A-21 | `veredicto_local` es `aceptada` o `marcada`; lo que el celular rechaza no viaja (ADR-024, ESC-02) | ronda 1c P4 | decidida |
| A-24 | La versión mayor va en la ruta (`/v1/`), la menor en la partición (`"contrato": "1.2"`). Una menor solo agrega campos opcionales que no cambian el sentido de los demás. El nodo de finca solo acepta versiones que conoce; lo demás es `422 contrato-incompatible`. **Orden de validación:** primero la versión de `contrato`, después el esquema. Un `contrato` **mal escrito** (p. ej. `"1.x"`) no es una versión: `422 sobre-invalido` con `pointer: /contrato` (ronda 1h P5, resuelve K-63) | rondas 1, 1c, 1d, 1e | decidida |
| A-27 | El orden de consecutivos no se impide, se marca: tras un hueco las anotaciones llevan `hueco_consecutivo`; los solapes los resuelve la idempotencia | ronda 1 P17 | decidida |
| A-7 | El celular entrega en el orden de su consecutivo | ADR-031 §5 | supuesto por fuente |
| A-47 | **Aviso de descarte:** el celular avisa en `descartes[]` (opcional; mismo tope que `anotaciones`) cada consecutivo que borró sin entregar, **con el número de su almacén**: `{consecutivo, motivo}`, `motivo` = `fallo_tecnico` o `descartada_por_gerente`. El nodo de finca cierra el hueco y lo deja en la bitácora. Idempotente: un aviso repetido, o de algo que ya llegó, no tiene efecto. **Todo aviso vuelve en `descartes_acusados`.** Una partición puede traer solo descartes | rondas 1d, 1e, 1f | decidida |
| A-45 ⟦2⟧b | `primeros_acuses[]` (opcional): `{anotacion_uuid, primer_acuse_ms}` de lo que se reenvía tras una restauración; va **fuera** de la anotación, que es inmutable. **`primer_acuse_ms` es el reloj del nodo de finca**: cada respuesta trae `recibido_ms` y el celular lo devuelve tal cual. Una entrada imposible (futura, anterior al sello, o de un uuid que no viene en la partición) se ignora y va a la bitácora. Tope: `max_anotaciones_particion` (ronda 1h P3, resuelve COH-39 y K-60) | ronda 1f P8 | decidida |

### Qué juzga el nodo de finca
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-14 | Una anotación mala no tumba la partición: hay un resultado por anotación | IA_TX1 paso 14 | confirmada |
| A-32 | **Frontera.** `422 sobre-invalido` (toda la partición) solo si no se puede leer o identificar, o trae algo que no está en el contrato: JSON roto, **campo desconocido en el sobre de la partición**, profundidad de toda la partición mayor que `max_profundidad_particion` (límite duro; es anomalía), falta `contrato`/`particion_uuid`/`anotaciones[]`, o una anotación sin `anotacion_uuid` o `consecutivo` válidos. **Por anotación:** forma inválida, campo desconocido, `tipo` o `version_tipo` desconocidos, topes de anotación pasados (incluida la profundidad dentro de una anotación, `max_profundidad_anotacion` — ronda 1h P1, resuelve K-73), motivo mal escrito → **fallo técnico**: `rechazada` (`datos_invalidos` o `tipo_desconocido`) y se registra. Lo que se juzga con el catálogo o las reglas (valor fuera de rango, motivo que no está en el catálogo) → `marcada` `divergencia` (fallo del motor, no del operario; **no se recaptura, salvo con `sin_minimo`**, porque ahí falta un dato que solo el campo puede dar — ronda 1h P5, resuelve K-65). `version_config` desconocida → `marcada` `version_desconocida` | rondas 1b, 1c, 1d, 1e · ADR-014 · ES-S10, ES-S11 | decidida |
| A-35 | **Dato ausente:** `valor: null` con `motivo_ausencia` → `marcada` `incompleta`; sin motivo → `marcada` `incompleta` + `motivo_ausente`; valor **con** motivo → fallo técnico. **Producto faltante:** hay cifra pero menor a lo esperado; lo decide una regla del nodo de finca (id de regla en los códigos). Los motivos viven en el catálogo de configuración | rondas 1b, 1c · P-S01 · CN-36 | decidida |
| A-46 | **Flujo de la jornada.** Sin el mínimo requerido de su actividad, el celular no deja guardar la captura. Si llega una así (falló el motor del celular) → `marcada` `divergencia` + `sin_minimo`, y se pide recapturar. Captura válida o incompleta → entra. Fallo técnico → el celular borra su contenido, guarda lápida y pide recapturar (A-48). Una anotación **aislada** nunca se registra | rondas 1d P5, 1e P6, 1f P6 · ADR-024 · P-S01 | decidida |
| A-45 ⟦1⟧ | **Obsoleta y superada.** Si la orden de captura (asignación ADR-026: cama + jornada) ya la cumplió otra captura **posterior** (por sello normalizado) y **verificada** (aplicada **sin ningún código de marca**: ni de hora, ni de motor, ni incompleta, ni de dispositivo — ronda 1g P2, resuelve COH-36), **por el mismo (sección, campo)**: lo que la captura posterior no trae no se supera, la tardía cuenta `sin_efecto` con `obsoleta`, no se guarda el dato y queda una **constancia** (quién, cama, jornada, qué la superó), guarda el uuid y la huella (el uuid es único entre anotaciones y constancias, A-2) y cuenta como recibida para las marcas. **Cómo se sabe la orden** (ronda 1h P8, resuelve K-72; nota de Juan): la deduce el nodo de finca, que crea las asignaciones: sección → cama por el catálogo, más la jornada, y **el dispositivo o el usuario al que se asignó**, que es quien debería devolverla (el dispositivo de la sesión de sincronización o la `persona_id` de la sesión de captura). El contrato no cambia. Si la cama no estaba asignada a ese dispositivo ni a esa persona, no hay orden: decide RF-022 y se marca `fuera_de_asignacion`. **Solo aplica cuando la orden se reasignó a otro dispositivo o persona** y ésa la cumplió; si no, decide RF-022 (A-2, K-52). Si la vieja ya estaba guardada, pasa a **superada**: sale del estado y de la historia y queda su constancia, que en este caso **conserva sus bytes** (ronda 1h P9, COH-58), por si el sello de la otra se recalcula o una restauración reordena las llegadas. Si la posterior trae cualquier marca, o alguna tiene hora dudosa, no se declara obsoleta: entran las dos `marcadas` con `orden_duplicada` (y sus otros códigos); **el estado lo decide RF-022 al instante** (gana la más reciente; con el mismo sello, el `anotacion_uuid` mayor), se avisa a quien perdió, y la marca deja el caso en el tablero para que el gerente corrija si hace falta (ronda 1h P7, resuelve K-71 · P-S02, ES-S06) | rondas 1e P3, 1f P2-P4 · ADR-026 | decidida |
| A-45 ⟦2⟧ | **Vieja:** la edad es «primera llegada − sello normalizado»; con más de `edad_vieja` (3 días) entra `marcada` `vieja` | rondas 1d P3, 1f P8 | decidida (ver COH-39) |
| A-23 | `excepciones[]` lleva cada anotación con resultado o códigos que mostrar: `rechazada`, `marcada` y `sin_efecto` con `obsoleta`. Cada una: `anotacion_uuid`, `resultado`, `codigos` (ids cortos, sin textos: id de regla del catálogo `R-…` o código del ingreso en `snake_case`, §3b). La marca sirve para que el gerente de producción haga las correcciones. En un reenvío repite el resultado y los códigos guardados, y los conteos la cuentan en `sin_efecto`. Códigos del ingreso: §5; entre ellos `capturada_sin_credencial` (el `crudo_ms` es posterior al vencimiento de la credencial de la sesión de captura, ES-S12), `reloj_movido` (el desfase declarado al capturar y el medido al entregar difieren más que `umbral_reloj_movido`, ADR-031 §3) y `dispositivo_no_registrado` (ES-S13) | rondas 1, 1c, 1d, 1f | decidida |
| A-39 | **Consecutivo reiniciado.** El mismo (dispositivo, consecutivo) con otro id → `marcada` `consecutivo_repetido`, alerta y `"accion": "registrar_de_nuevo"` (una sola vez por dispositivo). El celular se registra de nuevo y reenvía; al llegar por el dispositivo nuevo cuenta para sus marcas y pasa a `consecutivo_resuelto` | rondas 1c, 1d | decidida |
| A-50 | **El nodo de finca crea todo lo que llega al celular.** Hay **cuatro ejes de versión independientes**: despliegue/app, motor, **configuración** (catálogo y reglas juntos en un solo paquete, ADR-029) y contrato (ronda 1g P7, resuelve K-53), que el nodo de finca crea, distribuye y guarda identificadas, para poder reevaluar lo que haga falta. Invariante: un celular nunca tiene una versión que el nodo de finca no tenga. Por eso `422 contrato-incompatible` y `version_desconocida` no ocurren en operación normal: si aparecen son una anomalía y se alerta al administrador. Con `contrato-incompatible` el celular **retiene todo** y la ventana no corre. Una `version_app` o `version_motor` desconocida también es anomalía: la anotación entra `marcada` (`version_desconocida`) y se alerta (ronda 1h P9, COH-60). Con `version_desconocida` la anotación entra `marcada` y el celular la suelta según A-48, porque el dato ya está en el nodo de finca (ronda 1g P8, resuelve K-54). Juan: «es imposible que el backend no tenga la configuración: nace, se crea y se distribuye desde el nodo de finca». Que la configuración sobreviva a una restauración es responsabilidad de TX-2 (COH-40, §7) | ronda 1f P5 · nota de Juan | decidida |
| A-8 | Una captura incompleta viaja y entra; queda como trabajo del gerente | P-S01 | decidida |
| A-6 | El nodo de finca calcula el sello normalizado con el desfase declarado | ADR-031 §2 | supuesto por fuente |
| A-11 | Todo campo del que dependa un orden o una decisión del nodo de finca entra en la huella | IA_TX1 §4 | supuesto por fuente |

### La respuesta y los errores
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-13 | Éxito: `200` (no hay recurso nuevo con URL propia), síncrono, con `Cache-Control: no-store` | ronda 1b P14 | confirmada |
| A-26 | Respuesta `200`: `particion_uuid`, `confirmado_hasta`, `respaldado_hasta`, `conteo` {`recibidas`, `aplicadas`, `marcadas` ⊆ `aplicadas`, `rechazadas`, `sin_efecto` (ya guardadas u obsoletas)} con `recibidas = aplicadas + rechazadas + sin_efecto`, `excepciones[]`, `recibido_ms` (reloj del nodo de finca, A-45 ⟦2⟧b), `descartes_acusados[]` (**opcional**: solo si hubo descartes) y, solo si hace falta, `accion`. **Los conteos son la foto del momento** de esa respuesta y no se reescriben si después una captura pasa a superada (ronda 1h P2, resuelve COH-38). No se guardan respuestas: un reenvío se procesa de nuevo (el de una superada devuelve `sin_efecto` + `obsoleta` y el celular actualiza su acuse); la sesión que lo recibe lo cuenta como «reenviado sin efecto» y **el sello del evento sigue siendo el de la primera sesión que lo aplicó** (ADR-028 §3; excepción tras restaurar en A-31) | rondas 1, 1b | decidida |
| A-22 | Errores de toda la partición en `application/problem+json` con `regla`: `401 credencial-invalida`, `403 sin-permiso` (`ingesta_entregar`, `empresa_distinta`, `sesion_ajena`; se comprueba antes de leer anotaciones), `404 sesion-desconocida`, `409 sesion-cerrada`, `409 particion-en-curso` (una a la vez **por dispositivo**), `413 particion-muy-grande`, `422 sobre-invalido` con `errores[].pointer`, `422 contrato-incompatible`. Agregar un `type` es compatible | rondas 1, 1b, 1d | decidida |
| A-36 | Todo error lleva `traceId`; `429`, `503` y `409 particion-en-curso` traen `Retry-After`; `500` nunca expone detalles internos; ningún error lleva datos de negocio | ronda 1b P13 | decidida |
| A-41 | **Qué hace el celular según el error:** `413` → partir en dos; `422 sobre-invalido` con `pointer` → quitar esa anotación (fallo técnico) y reenviar el resto; **sin `pointer` a una anotación** (defecto del sobre) → **no se parte**: el celular rearma **solo el sobre** una vez desde su almacén, con los bytes originales de cada anotación y sin regenerar ids ya enviados, y reenvía; si vuelve a fallar, retiene todo como anomalía, la ventana de retención no corre, se alerta al administrador y la salida es actualizar la app (rondas 1g P3 y 1h P9, COH-48 y COH-56); `422 contrato-incompatible` → no partir ni borrar, retener y avisar qué lado actualizar; `401` → renovar credencial (A-51) y reintentar; `403` con `ingesta_entregar` o `empresa_distinta` → no reintentar, retener y avisar al administrador; `403 sesion_ajena` (entrega otra persona) → abrir otra sesión a su nombre, que cierra la anterior como `parcial` (A-51); `404` y `409 sesion-cerrada` → abrir otra sesión; `5xx`, `429`, `409 particion-en-curso` → reintentar. La espera es la mayor entre `Retry-After` y la espera creciente. Nada se borra por estos errores | rondas 1c, 1d, 1f | decidida |
| A-40 | ⟦1⟧ Una sesión activa por celular (supuesto, ronda 2). ⟦2⟧ Plazos: procesar ≤ 20 s (si no, `503`) < candado 25 s < espera del celular 30 s. ⟦3⟧ `409 particion-en-curso` trae `Retry-After`. ⟦4⟧ Espera creciente `min(2^n · 1 s, 5 min) + azar(0..1 s)`; tras 8 intentos avisa al operario y sigue cada 5 min. ⟦5⟧ Inserción idempotente y atómica | rondas 1c P8, 1d P8 · ESC-38 | decidida |

### Marcas de agua, retención y borrado
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-31 | `confirmado_hasta`: mayor consecutivo recibido sin huecos (rechazadas y constancias cuentan como recibidas). `respaldado_hasta`: mayor consecutivo dentro de un respaldo **verificado y que pasó la prueba de restauración** (TX-2 paso 17), presente en la copia local y en la nube (el mínimo de las dos); lo calcula TX-2. Si alguna vez se restaura por debajo de lo ya anunciado, se calcula la posible pérdida por celular y se alerta (lista de camas a rehacer) — ronda 1g P4, resuelve COH-49. Para poder calcularla, **el celular declara al abrir la sesión el último `respaldado_hasta` que recibió** (ronda 2; ronda 1h P9, COH-54). Invariante `respaldado_hasta ≤ confirmado_hasta`. Las marcas pueden **bajar** tras una restauración: el celular anula los acuses por encima y reenvía desde ahí; si baja `respaldado_hasta`, alerta. Un hueco se cierra con un aviso de descarte o por el administrador, nunca por plazo. Lo confirmado y aún no respaldado se retiene y queda inaccesible para los operarios. Las marcas viajan en cada respuesta de partición y **también al abrir la sesión** (ronda 2, COH-5). Tras restaurar, un evento reenviado se sella con la primera sesión que lo aplica después de la restauración (propuesta para ADR-028, K-28) | rondas 1b-1e · P-S03 · TX-2 · ES3 | decidida |
| A-48 | **Única regla de borrado en el celular.** ⟦1⟧ Lo acusado (`aplicada`, `marcada`, `sin_efecto`, `consecutivo_resuelto`; nunca `consecutivo_repetido`) se borra cuando su consecutivo ≤ `respaldado_hasta`. ⟦2⟧ Un fallo técnico o un descarte del gerente borra el contenido ya y deja una **lápida** `{consecutivo, motivo}` hasta que el respaldo la cubra. ⟦3⟧ Si una marca baja, se reenvía lo retenido y las lápidas. ⟦4⟧ Nada más se borra | ronda 1e P2 | decidida |
| A-45 ⟦3⟧ | **Lo no entregado nunca se borra solo.** A los 3 días escala el aviso al operario; pasada `ventana_retencion` (7 días), la cama sale en el tablero del gerente, que decide recapturar o descartar. Los días los cuenta el celular tras cada respuesta normal del nodo de finca, y el tablero muestra la edad que el celular declara (campo: COH-43) (no corren con el nodo de finca caído ni con `contrato-incompatible`). Una lápida `descartada_por_gerente` cuya orden se perdió al restaurar se acepta con constancia `descarte_sin_orden` | rondas 1e P1, 1f P10 · P-S03 · ADR-025 §2 | decidida |

### Sesión, identidad y transversales
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-5 | La sesión de sincronización la crea el nodo de finca; lleva dispositivo (`uuid`, `registro_id`), usuario, versiones de configuración, conteos y resultado; ninguna queda abierta | ADR-028 | supuesto por fuente (ronda 2) |
| A-51 | ⟦1⟧ Login local en el celular: mantiene la identidad al capturar sin red. ⟦2⟧ El acceso en campo nunca se pierde por vencimiento (si captura después, marca `capturada_sin_credencial`). ⟦3⟧ En la oficina se renueva la credencial antes de sincronizar, y la persona verificada con esa credencial renovada es la dueña de la sincronización. **La sesión de sincronización es de la persona**, no de la credencial: renovar no la rompe (ronda 1g P9, resuelve COH-41). **Celular compartido** (ronda 1h P6, resuelve K-69 y COH-55; aclaración de Juan: «cada operario ingresa con su cuenta única, esté o no sincronizando»): entrar con otra cuenta **cierra la sesión anterior como `parcial`** y abre la nueva; la nueva sube **todo lo pendiente del almacén** del celular (los autores no cambian, A-18; quien entrega es la persona nueva) y después baja el catálogo y la asignación de esa persona. El candado de «una partición a la vez» es **por dispositivo**. ⟦4⟧ Ante `401`, el celular pide la renovación y reintenta; nunca borra nada | ronda 1f P9 · ES-S12 | decidida (ver K-59, COH-41) |
| A-9 | DTO como `record`, JSON Schema 2020-12 versionado, prueba que falla si el contrato cambia sin subir versión. Jackson 3 con `FAIL_ON_UNKNOWN_PROPERTIES` y sin tipos polimórficos | TEC-10, TEC-11 · ronda 1e P8 | decidida |
| A-10 | Forma canónica RFC 8785 propia sobre Jackson, probada con los vectores del RFC | TEC-10 · ronda 1b P17 | decidida |
| A-34 | Null Object en Java: interfaces selladas con variante «hay» y «no hay» (`Desfase`, `Valor` con `Motivo`); ausente y `null` del JSON dan el mismo objeto nulo | ronda 1c P18 | decidida (confirmar en revisión) |
| A-37 | `contrato` es Java puro, sin Spring; `module-info` exporta la fachada, `.dto`, `.codigos` y `.problemas`, y oculta `.interno`; una prueba de arquitectura en cada consumidor lo hace cumplir | ronda 1b P15 | decidida |
| A-44 | `dominio` no importa `contrato` | CLAUDE.md | confirmada |

## 3 · Variables
| Nombre | Valor | Tipo | Dueño |
|---|---|---|---|
| Forma canónica de la huella | RFC 8785 (JCS), **implementación propia sobre el árbol de Jackson** (`interno/Jcs.java`), probada con los vectores del RFC, sobre todo los números | fija | ronda 1 P8 · **ronda 1b P17 (★, resuelve K-14)** · TEC-10 «forma canónica propia» · cierra la pregunta de TEC-10 en `IA_TECNOLOGIAS-vigentes.md` (actualizarla, con confirmación) |
| Validador y versión de JSON Schema | **Biblioteca en ejecución, borrador 2020-12**; cuál, se elige al generar, con compatibilidad con Jackson 3 comprobada y **fila nueva en el registro con aceptación de Juan** antes de usarla | fija (biblioteca por elegir) | ronda 1b P20 (★) · TEC-11 · A-9 · A-15 |
| `edad_vieja` | 3 días (provisional) | configuración de negocio | ronda 1d P3 · A-45 · ADR-025 §2 |
| `ventana_retencion` | 7 días (provisional) | configuración de negocio | ronda 1d P3 · A-45 · ADR-025 §3-4 |
| Umbral de `hora_dudosa` | — | pendiente | Juan · IA_TX1 paso 11 · A-17 |
| `umbral_reloj_movido` | — | pendiente | Juan · ADR-031 §3 · A-23 |
| Granularidad de captura (D1) | — | pendiente | Juan · ADR-024 «niveles admisibles» · sesión D1 con el cliente |
| Generador de UUID v7 | JDK o biblioteca | pendiente | Juan · TEC-12 |
| `max_anotaciones_particion` | 200 (provisional) | configurable; el servidor lo anuncia al abrir la sesión | ronda 1 P13 (★) · se ajusta con la prueba de ESC-38 |
| p95 de respuesta por partición | ≤ 2 s con 13 celulares a la vez y +60 % de carga (provisional) | fija; se mide en la prueba de ESC-38 | ronda 1b P16 (★) · ESC-38 · se ajusta con SPK-04 |
| Espera del celular por respuesta | 30 s; al vencer, reenvía la misma partición con la espera de A-40 | configurable en el celular | ronda 1b P16 (★) |
| Espera entre reintentos | `min(2^n · 1 s, 5 min) + azar(0..1 s)`; tras 8 intentos, aviso al operario y sigue cada 5 min | configurable en el celular | ronda 1c P8 (★) · A-40 |
| Plazo máximo de procesamiento | 20 s; al pasarlo, aborta con `503` | fija | ronda 1d P8 (★) · A-40 |
| Vencimiento del candado por dispositivo | 25 s | fija | ronda 1c P8 · ronda 1d P8 (★) · A-40 |
| `max_profundidad_anotacion` | 16 (provisional) | configurable; anunciado al abrir. Pasarlo dentro de una anotación → esa anotación `rechazada` `datos_invalidos`; las demás entran | ronda 1c P11 · ronda 1h P1 (★) · A-32 |
| `max_profundidad_particion` | 64 (provisional) | fija; límite duro contra JSON malicioso. Pasarlo → `422 sobre-invalido` sin `pointer` (anomalía) | ronda 1h P1 (★) · A-32 |
| `max_bytes_valor` | 4 KiB por anotación (provisional) | configurable; anunciado al abrir. Pasarlo → `rechazada` `datos_invalidos` | ronda 1c P11 (★) · A-20 |
| `max_largo_cadena` | 1 KiB (provisional) | configurable; anunciado al abrir. Pasarlo → `rechazada` `datos_invalidos` | ronda 1c P11 (★) |
| `tasa_por_dispositivo` | 60 peticiones/min (provisional) → `429` con `Retry-After` | configurable | ronda 1c P11 (★) · A-36 · se ajusta con ESC-38 |
| `max_bytes_particion` | 1 MiB (provisional) | configurable; anunciado al abrir | ronda 1 P13 (★) |

## 3b · Tipos y rangos (ronda 1f P10)
Los formatos de los ids de negocio son **supuesto** hasta la ronda de `configuracion`.

| Campo | Tipo | Rango o formato |
|---|---|---|
| `contrato` | texto | `^\d+\.\d{1,3}$`; se valida **antes** que el esquema: mayor distinta de la ruta o menor desconocida → `422 contrato-incompatible` (A-24) |
| `particion_uuid`, `anotacion_uuid`, `sesiones_captura[].uuid`, `sesion_captura_uuid` | texto | UUID v7 en minúsculas (A-38) |
| `sesiones_captura[]` | arreglo | 0 a `max_anotaciones_particion`; al menos 1 si hay anotaciones; cada `sesion_captura_uuid` de una anotación debe estar declarado aquí |
| `persona_id` | texto | `[A-Za-z0-9_-]{1,40}` (supuesto) |
| `credencial_jti` | texto | 1 a 64 caracteres |
| `inicio` | texto | RFC 3339 en UTC |
| `version_app`, `version_motor` | texto | `^\d+\.\d+\.\d+$` |
| `anotaciones[]` | arreglo | 0 a `max_anotaciones_particion`; 0 solo si hay `descartes` |
| `consecutivo` | entero | 1 a 2⁵³ − 1 |
| `tipo` | texto | `^[a-z]+(\.[a-z_]+)+$`, hasta 40 |
| `version_tipo`, `version_config` | entero | ≥ 1 (supuesto) |
| `tiempo.crudo_ms`, `tiempo.mono_ms` | entero | ≥ 0, milisegundos |
| `tiempo.arranque` | entero | ≥ 0 |
| `tiempo.desfase_declarado_ms` | entero o `null` | entero con signo |
| `veredicto_local` | texto | `aceptada` o `marcada` |
| `datos.produccion_id`, `seccion_id`, `campo_id` | texto | `[A-Za-z0-9_-]{1,40}` (supuesto) |
| `datos.jornada` | texto | fecha `AAAA-MM-DD` (P-S04) |
| `datos.valor` | JSON o `null` | hasta `max_bytes_valor` |
| `datos.motivo_ausencia`, `datos.motivo_desviacion` | texto | `[a-z_]{1,40}`, del catálogo |
| `descartes[]` | arreglo | 0 a `max_anotaciones_particion`; `consecutivo` entero; `motivo` = `fallo_tecnico` o `descartada_por_gerente` |
| `primeros_acuses[]` | arreglo | 0 a `max_anotaciones_particion`; `anotacion_uuid` y `primer_acuse_ms` (entero ≥ 0, copiado de `recibido_ms`) |
| `recibido_ms` (respuesta) | entero | ≥ 0, reloj del nodo de finca |
| **Respuesta** `particion_uuid` | texto | UUID v7 en minúsculas, el de la petición |
| `confirmado_hasta`, `respaldado_hasta` | entero | ≥ 0; `respaldado_hasta ≤ confirmado_hasta` (A-31) |
| `conteo.recibidas`, `aplicadas`, `marcadas`, `rechazadas`, `sin_efecto` | entero | ≥ 0; `recibidas = aplicadas + rechazadas + sin_efecto`; `marcadas ≤ aplicadas` |
| `excepciones[]` | arreglo | `anotacion_uuid`; `resultado` = `marcada`, `rechazada` o `sin_efecto`; `codigos[]` |
| `codigos[]` | arreglo de texto | id de regla del catálogo `^R-[A-Z]+-\d{2,4}$` o código del ingreso `^[a-z_]{1,40}$` (ronda 1h P10) |
| `descartes_acusados[]` | arreglo de enteros | opcional; consecutivos acusados |
| `accion` | texto | opcional; hoy solo `registrar_de_nuevo` |

## 4 · Comportamiento
Casos vigentes, en orden. C-9 y C-51 se retiraron con A-28 y A-43.
- **C-70** Dado un operario que recaptura la cama 12 (sin reasignación) · cuando llegan las dos capturas · entonces se guardan las dos y gana la más reciente; la otra queda guardada (A-2, RF-022, P-S02).
- **C-71** Dada la cama 12 reasignada, con D-014 (tallos=40, plagas=no) y D-022 (tallos=42, sin marcas) · cuando llegan · entonces solo los tallos de D-014 quedan obsoletos o superados; las plagas de D-014 se guardan, porque D-022 no las capturó (A-45 ⟦1⟧).
- **C-72** Dada la misma reasignación con D-022 `tallos: null` (incompleta) · cuando llega · entonces no supera a D-014: entran las dos `marcadas` con `orden_duplicada`; el estado lo decide RF-022 (la más reciente) y el gerente puede corregir (A-45 ⟦1⟧).
- **C-73** Dada una partición que responde `422 sobre-invalido` con `pointer: /sesiones_captura/0/uuid` · cuando el celular la procesa · entonces no la parte: rearma el sobre desde su almacén y reenvía una vez; si falla otra vez, retiene todo y alerta al administrador; no se borra nada (A-41).
- **C-74** Dada la sesión S1 abierta por op-07 · cuando op-07 renueva su credencial (jti nuevo) y sigue entregando · entonces sigue en S1; si en el mismo celular entrega op-12, recibe `403 sesion_ajena` y abre S2 a su nombre (A-51, A-41).
- **C-75** Dada la 77 obsoleta con su constancia (huella H1) · cuando llega el mismo id con huella H2 · entonces es corrupta: `rechazada` `datos_invalidos`, alerta, y no se crea una segunda constancia (A-2, A-45).
- **C-76** Dada una partición de 200 anotaciones donde la 38 trae un `valor` con profundidad 20 · cuando llega · entonces la 38 es `rechazada` `datos_invalidos` (fallo técnico) y las otras 199 entran (A-32).
- **C-77** Dada D-014 aplicada a las 14:05 (respuesta con `aplicadas: 1`) y superada a las 15:10 por D-022 · cuando D-014 reenvía · entonces recibe `sin_efecto` con `obsoleta`, el celular actualiza su acuse, y los conteos de la respuesta de las 14:05 no se reescriben (A-26, A-45).
- **C-78** Dado un reenvío con `primeros_acuses: [{uuid, primer_acuse_ms}]` donde `primer_acuse_ms` es posterior a ahora · cuando llega · entonces esa entrada se ignora y va a la bitácora; la anotación se juzga sin ella (A-45 ⟦2⟧b).
- **C-79** Dada una partición con `sesiones_captura: [S1]` y la anotación 12 con `sesion_captura_uuid: S9` · cuando llega · entonces la 12 es `rechazada` `datos_invalidos` (el celular la borra con lápida y pide recapturar) y las demás entran (A-38, A-46).
- **C-80** Dada S1 de op-07 a medias en D-014 · cuando op-12 entra con su cuenta y entrega · entonces S1 se cierra `parcial`, S2 de op-12 sube todo lo pendiente del celular (lo de op-07 sigue a nombre de op-07, entregado por op-12) y después baja el catálogo y la asignación de op-12 (A-51, A-18, A-40).
- **C-81** Dada una anotación de la sección B3-C12-S1 del 16-oct entregada por D-014, con la cama 12 asignada a D-014 y reasignada a D-022 · cuando llega · entonces el nodo de finca deduce la orden por el catálogo y sus asignaciones y aplica A-45 ⟦1⟧; si la cama no estaba asignada a ese dispositivo ni a esa persona, decide RF-022 y la marca `fuera_de_asignacion` (A-45).
- **C-1** Dada una partición nueva de 200 anotaciones válidas · cuando se entrega · entonces `200`, `conteo.aplicadas = 200` y `confirmado_hasta` = su último consecutivo (A-13, A-26).
- **C-2** Dada una partición de 200 anotaciones ya entregada · cuando se reenvía, en la misma sesión o en otra · entonces `200` con `conteo.sin_efecto = 200`, nada cambia, `excepciones[]` repite las marcadas y rechazadas con sus códigos guardados, la sesión que la recibe cuenta 200 reenviados sin efecto y el sello sigue siendo el de la primera sesión (A-2, A-26, ES-S04, ADR-028 §3).
- **C-3** Dada una anotación ya guardada · cuando llega con el mismo id y la misma huella · entonces cuenta en `sin_efecto` y no cambia nada (A-2, A-26).
- **C-4** Dada una anotación ya guardada · cuando llega con el mismo id y otra huella · entonces `rechazada`, se registra y se alerta; nunca se sobrescribe (A-2, A-19, ES-S05).
- **C-5** Dadas dos anotaciones con ids distintos y la misma clave del hecho · cuando llegan · entonces entran las dos (A-2, A-3, ES-S06).
- **C-6** Dada una anotación con `valor: null` y `motivo_ausencia: "no_disponible"` · cuando llega · entonces entra marcada como incompleta y el motivo llega al gerente (A-8, A-20, A-35).
- **C-7** Dado un celular que nunca sincronizó · cuando entrega con `desfase_declarado_ms: null` · entonces entra con desfase cero, `marcada` con `desfase_desconocido` (A-6, A-16, A-23).
- **C-8** Dada una partición cuyo primer consecutivo no es `confirmado_hasta + 1`, o con un hueco · cuando llega · entonces entra completa, las anotaciones tras el hueco llevan `hueco_consecutivo` y las dos marcas de agua se quedan antes del hueco: con 1-200 y 401-600 recibidas, `confirmado_hasta = 200` (A-27, A-31).
- **C-10** Dada una anotación con un campo desconocido, en `datos` o en su sobre · cuando llega · entonces `rechazada` con `datos_invalidos`; el celular borra el contenido con lápida y pide recapturar (A-32, A-46, A-48).
- **C-11** Dada una sesión cerrada · cuando llega una partición · entonces `409 sesion-cerrada` y el celular abre otra (A-22, ES-S02).
- **C-12** Dada una sesión que no existe · cuando llega una partición · entonces `404 sesion-desconocida` (A-22).
- **C-13** Dada una partición de más de `max_anotaciones_particion` o `max_bytes_particion` · cuando llega · entonces `413 particion-muy-grande` (A-22, variables).
- **C-14** Dada una partición cuya anotación n.º 38 (índice 37; el puntero cuenta desde 0) no trae `anotacion_uuid` · cuando llega · entonces `422 sobre-invalido` con `errores[].pointer = /anotaciones/37/anotacion_uuid` y no entra ninguna (A-22, A-32).
- **C-15** Dada una credencial inválida o sin permiso · cuando llega una partición · entonces `401 credencial-invalida` o `403 sin-permiso` (A-22, A-29).
- **C-16** Dados dos arranques distintos del celular · cuando se comparan sus `mono_ms` · entonces no se interpreta como salto de reloj (A-17).
- **C-17** Dados op-07 que capturó y op-12 que entrega · cuando llega la partición · entonces las anotaciones quedan a nombre de op-07 y op-12 queda como quien entregó (A-18).
- **C-18** Dada una anotación de `tipo` `labor.siembra` que el servidor no conoce · cuando llega en una partición válida · entonces esa anotación es `rechazada` con `tipo_desconocido` y se registra; el resto entra (A-14, A-32).
- **C-19** Dada una anotación `captura.campo` cuyos `datos` no traen `seccion_id` · cuando llega · entonces `rechazada` con `datos_invalidos`; el resto entra (A-32).
- **C-20** Dada una anotación con `valor: null` sin `motivo_ausencia` · cuando llega · entonces entra `marcada` con `incompleta` y `motivo_ausente` (A-8, A-35, P-S01).
- **C-21** Dada una captura con `valor: 40` donde se esperaban 60 · cuando llega · entonces entra, y si una regla del servidor lo marca, vuelve `marcada` con el id de esa regla; el celular no declara el faltante (A-23, A-35).
- **C-22** Dada la partición P1 del celular A en proceso · cuando llega P2 del mismo celular · entonces `409 particion-en-curso`, P2 no se procesa y A la reenvía después de la respuesta de P1. Al mismo tiempo, las particiones de los celulares B y C en sus sesiones S2 y S3 se procesan sin esperar (A-7, A-22).
- **C-23** Dado un nodo de finca saturado en temporada alta · cuando llega una partición · entonces `503 nodo-ocupado` con `Retry-After` y `traceId`; el celular espera esos segundos y reenvía la misma partición (A-36, A-40, ES-S14).
- **C-24** Dados 13 celulares sincronizando a la vez con +60 % de carga · cuando entregan su jornada · entonces el p95 por partición es ≤ 2 s, la jornada termina en ≤ 30 min y no hay rechazos por carga; un `503` que después entra no cuenta como rechazo (ESC-38, A-36).
- **C-25** Dada una anotación con `anotacion_uuid` versión 4 (`…-4…`) o en mayúsculas · cuando llega · entonces `422 sobre-invalido` con el `pointer` a ese campo y no entra ninguna anotación de la partición (A-32, A-38).
- **C-26** Dada una anotación con `valor: 40` y `motivo_ausencia` · cuando llega · entonces `rechazada` con `datos_invalidos` (A-32, A-35).
- **C-27** Dado un celular con `confirmado_hasta` local = 800 y un nodo de finca restaurado a un respaldo que llega al 600 · cuando el celular entrega su siguiente partición · entonces recibe `confirmado_hasta = 600`, baja su marca local a 600 y reenvía lo retenido del 601 al 800; lo que el nodo de finca ya tenía cuenta en `sin_efecto` (A-31, ES-R14, CN-15).
- **C-28** Dado D-014 con `respaldado_hasta = 5000` · cuando, tras reinstalar la app, entrega la anotación n.º 1 con un uuid nuevo · entonces entra `marcada` con `consecutivo_repetido`, hay alerta, la respuesta trae `accion: registrar_de_nuevo` y el celular **no** la borra; se registra como D-031 y la reenvía: cuenta `sin_efecto` en las marcas de D-031, vuelve con `consecutivo_resuelto` y se borra cuando `respaldado_hasta` de D-031 la cubre; el servidor no vuelve a pedir `registrar_de_nuevo` a D-014 (A-39).
- **C-29** Dada una anotación con `valor: 9999` donde el campo admite 0-500 en la versión 12 · cuando llega · entonces entra `marcada` con `divergencia`; no se rechaza (A-32, ADR-014, ES-S11).
- **C-30** Dada una anotación con `version_config: 99`, que el servidor no conoce · cuando llega · entonces entra `marcada` con `version_desconocida` (A-32, ES-S10).
- **C-31** Dada una copia local verificada hasta el 600 y la nube hasta el 800 para D-014 · cuando D-014 recibe una respuesta · entonces `respaldado_hasta = 600` y retiene del 601 al 800 (A-31, ES3).
- **C-32** Dadas 1-200 aplicadas, 201 `rechazada` y 202-400 aplicadas · cuando se calcula la marca · entonces `confirmado_hasta = 400` (A-27, A-31).
- **C-33** Dado un hueco 401-450 de D-014 que nunca llega y del que no hay aviso de descarte · cuando pasan los días · entonces las marcas siguen en 400 y el tablero lo muestra, hasta que el administrador cierra el hueco (queda en la bitácora) y la marca salta a lo siguiente recibido (A-31, A-47, ADR-026).
- **C-34** Dada una partición con `"contrato": "1.9"` y un nodo de finca que conoce la 1.2 · cuando llega · entonces `422 contrato-incompatible`; el celular la retiene sin partirla ni borrarla y avisa que hay que actualizar el nodo de finca (A-24, A-41).
- **C-35** Dada una partición con `"contrato": "2.0"` en `/v1/` · cuando llega · entonces `422 contrato-incompatible` (A-24).
- **C-36** Dada una partición 1-200 que responde `422 sobre-invalido` con `pointer: /anotaciones/136` (la n.º 137) · cuando el celular la procesa · entonces quita la 137 (la borra, la avisa en `descartes` y pide recapturar) y reenvía las otras 199, que entran (A-41, A-46, A-47).
- **C-37** Dada la cama 12 asignada el 16-oct a D-014 (turno 6-14), que no sincronizó, y reasignada ese día a D-022 (turno 14-22), que la capturó y sincronizó · cuando llega por fin la captura de D-014 · entonces cuenta `sin_efecto` con `obsoleta`, no se guarda el dato, la bitácora registra {op-07, cama 12, 16-oct, superada por la de D-022} y el celular la borra según A-48 (A-45, A-48, ADR-026).
- **C-38** Dada una anotación capturada hace 4 días · cuando llega · entonces entra `marcada` con `vieja` (A-45).
- **C-39** Dada una anotación no entregada capturada hace 8 días, con el nodo de finca respondiendo · cuando el celular revisa su almacén · entonces **no la borra**: sigue retenida, el aviso al operario está en su nivel más alto y la cama aparece en el tablero del gerente para recapturar o descartar (A-45, ADR-025 §2).
- **C-39b** Dado el nodo de finca caído 8 días · cuando vuelve · entonces nada se descartó en el celular: la ventana no corrió mientras estuvo caído (A-45, ES3).
- **C-40** Dado un nodo de finca restaurado al 600, con un respaldo hasta el 800 que pasó la prueba de restauración pero cuya copia no se pudo usar · cuando responde · entonces `confirmado_hasta = 600` y `respaldado_hasta = 600` (invariante); el celular anula sus acuses del 601 al 800 y reenvía lo que todavía retiene; si ya había borrado algo por encima de 600, el nodo de finca calcula la posible pérdida por celular y alerta (A-31, A-48).
- **C-41** Dada una anotación que el nodo de finca rechaza con `datos_invalidos` · cuando el celular recibe la respuesta · entonces borra su contenido de inmediato, guarda una lápida hasta que el respaldo la cubra y pide al operario recapturar esa cama (A-46, A-48).
- **C-42** Dada una captura sin el mínimo requerido de su actividad · cuando el operario intenta guardarla · entonces el celular no la deja guardar y nunca se envía (A-46, ADR-024).
- **C-43** Dada la anotación 137 aislada y borrada por fallo técnico · cuando el celular envía su siguiente partición con `descartes: [{consecutivo: 137, motivo: fallo_tecnico}]` · entonces el nodo de finca cierra el hueco 137, lo deja en la bitácora y las marcas avanzan (A-47).
- **C-44** Dada una partición que responde `422 contrato-incompatible` · cuando el celular la procesa · entonces no la parte ni la borra; la retiene y avisa qué lado actualizar (aquí el nodo de finca si el celular es más nuevo) (A-24, A-41).
- **C-45** Dada una anotación con `sesion_captura_uuid: "x"` · cuando llega en una partición válida · entonces esa anotación es `rechazada` con `datos_invalidos`, el celular la borra y pide recapturar, y las demás entran (A-32, A-38, A-46).
- **C-46** Dado op-12 con credencial válida de otra empresa · cuando entrega una partición · entonces `403 sin-permiso` con `regla: empresa_distinta` y no se lee ninguna anotación (A-22).
- **C-47** Dada la anotación 412 que el gerente ordenó descartar · cuando el celular la borra · entonces la avisa con `descartes: [{consecutivo: 412, motivo: descartada_por_gerente}]`, el nodo de finca cierra el hueco y las marcas siguen (A-45, A-47).
- **C-48** Dado un celular que pasa `tasa_por_dispositivo` · cuando entrega otra partición · entonces `429 demasiadas-peticiones` con `Retry-After` y `traceId`; el celular espera esos segundos (A-36, §3).
- **C-49** Dado un error interno del nodo de finca · cuando procesa una partición · entonces `500 error-interno` en `problem+json` con `traceId`, sin stack ni mensaje interno; el celular reintenta con la espera de A-40 (A-36).
- **C-50** Dada una partición con el mismo `anotacion_uuid` dos veces e igual contenido · cuando llega · entonces la segunda copia cuenta `sin_efecto`; si el contenido difiere, la segunda es `rechazada` con una sola alerta (A-42).
- **C-52** Dada la rechazada 137 (fallo técnico) borrada con lápida y un nodo de finca restaurado a un respaldo anterior a ella · cuando la marca baja · entonces el celular reenvía lo retenido y `descartes: [{consecutivo: 137, motivo: fallo_tecnico}]`; la respuesta trae `descartes_acusados: [137]` y el hueco no reaparece (A-48).
- **C-53** Dada una captura de 12 tallos donde la regla R-DESV-03 espera unos 60, con `motivo_desviacion: "helada"` · cuando llega · entonces entra; el nodo de finca puede marcarla con `R-DESV-03` para el gerente, con el motivo a la vista (A-49).
- **C-54** Dada la misma captura sin `motivo_desviacion` · cuando llega · entonces entra `marcada` con `R-DESV-03` y `desviacion_sin_motivo` (A-49).
- **C-55** Dada una captura que llega sin el mínimo requerido de su actividad · cuando el nodo de finca la juzga · entonces entra `marcada` con `divergencia` y `sin_minimo`, cuenta como fallo del motor del celular y el tablero pide recapturar esa cama (A-46, A-32).
- **C-56** Dada la 412 ya aplicada (la respuesta se perdió) · cuando llega `descartes: [{412, fallo_tecnico}]` · entonces no tiene efecto, queda en la bitácora, vuelve en `descartes_acusados` y la cama no sale en la lista de camas a rehacer (A-47).
- **C-57** Dada una partición con `anotaciones: []` y `descartes: [{137, fallo_tecnico}]` · cuando llega · entonces `200`, el hueco 137 se cierra y la respuesta trae las marcas actualizadas (A-47).
- **C-58** Dada la cama 12 que salió en una partición cuya respuesta se perdió · cuando el operario la cambia · entonces el celular crea una captura nueva de la cama 12 con otro `anotacion_uuid`; al reenviar, la vieja cuenta `sin_efecto` y la nueva entra; no hay alerta de corrupción (A-49).
- **C-59** Dada la 77 obsoleta con su constancia · cuando el celular la reenvía · entonces la constancia no se duplica, la 77 cuenta para `confirmado_hasta` y vuelve en `excepciones[]` con `sin_efecto` y `obsoleta` (A-45, A-26).
- **C-60** Dada la cama 12 del 16-oct aplicada por D-022 con sello 14:40 confiable · cuando llega la de D-014 con `desfase_declarado_ms: null` · entonces no se declara obsoleta: entra `marcada` con `desfase_desconocido` y `orden_duplicada`; el estado lo decide RF-022 y el gerente puede corregir (A-45).
- **C-61** Dada la de D-014 (09:10, confiable) ya guardada · cuando llega la de D-022 (14:40, verificada) para la misma orden · entonces la de D-014 pasa a «superada»: sale del estado y de la historia y queda su constancia; el resultado es el mismo que si D-014 hubiera llegado después (A-45).
- **C-62** Dado un celular que recibe `422 contrato-incompatible` (anomalía: el nodo de finca crea todas las versiones) · cuando pasan los días · entonces retiene todo, la ventana de retención no corre y el administrador recibe una alerta (A-50, A-45).
- **C-63** Dada la 137 que el celular envió con `"consecutivo": "13x"` (roto) y quedó aislada · cuando la borra · entonces avisa `descartes: [{consecutivo: 137, motivo: fallo_tecnico}]` con el número de su almacén, y el hueco 137 se cierra (A-47).
- **C-64** Dado un celular sin `registro_id` y una persona con credencial válida · cuando entrega · entonces sus anotaciones entran `marcadas` con `dispositivo_no_registrado` y el tablero lo muestra (A-23, ES-S13).
- **C-65** Dada una anotación con `desfase_declarado_ms: -1200` y un desfase medido al entregar de +2.200.000 ms · cuando llega · entonces entra `marcada` con `reloj_movido` (A-23, ADR-031 §3).
- **C-66** Dado un nodo de finca restaurado 4 días después · cuando el celular reenvía la 600, que tuvo su primer acuse 2 h después de capturarla, con `primeros_acuses: [{uuid de la 600, primer_acuse_ms}]` · entonces no se marca `vieja` (A-45 ⟦2⟧).
- **C-67** Dado un operario cuya credencial venció en campo · cuando sigue capturando · entonces el celular le deja capturar con su login local y esas anotaciones llevan `capturada_sin_credencial`; en la oficina renueva la credencial y, verificada con ella, es la dueña de la sincronización (A-51, A-18).
- **C-68** Dado un `403 sin-permiso` con `regla: empresa_distinta` · cuando el celular lo recibe · entonces no reintenta, retiene todo y avisa al administrador (A-41).
- **C-69** Dado un `503` con `Retry-After: 30` y una espera creciente de 8 s · cuando el celular reintenta · entonces espera 30 s (A-41, A-40).

## 4b · Ejemplos dorados y pares cumple/viola
Ronda 1b P18. Los valores son de ejemplo. **Supuesto por confirmar:** `version_config` y `version_tipo` son enteros. El código `incompleta` ya está
decidido (A-35, ronda 1d).

### Petición dorada · `POST /ingesta/v1/sesiones/0192f0a1-7c00-7000-8000-00000000a001/particiones`
```
Authorization: Bearer <JWS>
Content-Type: application/json
traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01
```
```json
{
  "contrato": "1.0",
  "particion_uuid": "0192f0a1-7d10-7000-8000-00000000b001",
  "sesiones_captura": [
    {
      "uuid": "0192f0a0-1000-7000-8000-00000000c001",
      "persona_id": "op-07",
      "credencial_jti": "jti-5b2e",
      "inicio": "2026-10-16T06:02:11Z",
      "version_app": "1.4.2",
      "version_motor": "3.1.0"
    }
  ],
  "anotaciones": [
    {
      "anotacion_uuid": "0192f0a0-2000-7000-8000-00000000d001",
      "consecutivo": 1,
      "tipo": "captura.campo",
      "version_tipo": 1,
      "sesion_captura_uuid": "0192f0a0-1000-7000-8000-00000000c001",
      "tiempo": { "crudo_ms": 1792130531000, "mono_ms": 8123400, "arranque": 41, "desfase_declarado_ms": -1200 },
      "version_config": 12,
      "veredicto_local": "aceptada",
      "datos": {
        "produccion_id": "P-2026-031", "seccion_id": "B3-C17-S1", "campo_id": "tallos_cortados",
        "jornada": "2026-10-16", "valor": 40
      }
    },
    {
      "anotacion_uuid": "0192f0a0-2001-7000-8000-00000000d002",
      "consecutivo": 2,
      "tipo": "captura.campo",
      "version_tipo": 1,
      "sesion_captura_uuid": "0192f0a0-1000-7000-8000-00000000c001",
      "tiempo": { "crudo_ms": 1792130590000, "mono_ms": 8182400, "arranque": 41, "desfase_declarado_ms": -1200 },
      "version_config": 12,
      "veredicto_local": "marcada",
      "datos": {
        "produccion_id": "P-2026-031", "seccion_id": "B3-C18-S1", "campo_id": "tallos_cortados",
        "jornada": "2026-10-16", "valor": null, "motivo_ausencia": "no_disponible"
      }
    }
  ]
}
```

### Respuesta dorada · `200`
```
Content-Type: application/json
Cache-Control: no-store
```
```json
{
  "particion_uuid": "0192f0a1-7d10-7000-8000-00000000b001",
  "confirmado_hasta": 2,
  "respaldado_hasta": 0,
  "recibido_ms": 1792130600000,
  "conteo": { "recibidas": 2, "aplicadas": 2, "marcadas": 1, "rechazadas": 0, "sin_efecto": 0 },
  "excepciones": [
    { "anotacion_uuid": "0192f0a0-2001-7000-8000-00000000d002", "resultado": "marcada", "codigos": ["incompleta"] }
  ]
}
```

### Reenvío de la misma partición · `200` (A-23, A-26, C-2) · las excepciones salen del estado guardado
```json
{ "particion_uuid": "0192f0a1-7d10-7000-8000-00000000b001", "confirmado_hasta": 2, "respaldado_hasta": 0,
  "recibido_ms": 1792130900000,
  "conteo": { "recibidas": 2, "aplicadas": 0, "marcadas": 0, "rechazadas": 0, "sin_efecto": 2 },
  "excepciones": [
    { "anotacion_uuid": "0192f0a0-2001-7000-8000-00000000d002", "resultado": "marcada", "codigos": ["incompleta"] }
  ] }
```

### Error dorado · `422` (A-32, C-14)
```
Content-Type: application/problem+json
Cache-Control: no-store
```
```json
{
  "type": "https://florlogic.co/problemas/ingesta/sobre-invalido",
  "title": "La partición no se puede leer o identificar",
  "status": 422,
  "traceId": "4bf92f3577b34da6a3ce929d0e0e4736",
  "errores": [ { "pointer": "/anotaciones/1/anotacion_uuid", "regla": "anotacion_uuid_obligatorio" } ]
}
```
(El dominio del `type` es un supuesto; lo fija Juan.)

### Errores dorados (uno por código, ronda 1f P10)
Todos llevan `Content-Type: application/problem+json`, `Cache-Control: no-store` y `traceId`.
```json
{ "type": ".../credencial-invalida", "status": 401, "title": "Credencial inválida o vencida", "traceId": "…" }
{ "type": ".../sin-permiso", "status": 403, "regla": "empresa_distinta", "title": "Sin permiso", "traceId": "…" }
{ "type": ".../sesion-desconocida", "status": 404, "title": "La sesión no existe", "traceId": "…" }
{ "type": ".../sesion-cerrada", "status": 409, "title": "La sesión ya se cerró", "traceId": "…" }
{ "type": ".../particion-en-curso", "status": 409, "title": "Otra partición está en curso", "traceId": "…" }   // + Retry-After
{ "type": ".../particion-muy-grande", "status": 413, "title": "La partición pasa el tope", "traceId": "…" }
{ "type": ".../contrato-incompatible", "status": 422, "title": "Versión de contrato no aceptada", "traceId": "…" }
{ "type": ".../demasiadas-peticiones", "status": 429, "title": "Demasiadas peticiones", "traceId": "…" }  // + Retry-After
{ "type": ".../error-interno", "status": 500, "title": "Error interno", "traceId": "…" }
{ "type": ".../nodo-ocupado", "status": 503, "title": "Nodo de finca ocupado", "traceId": "…" }          // + Retry-After
```

### Pares cumple / viola
| Decisión | Cumple | Viola |
|---|---|---|
| A-10 / A-33 forma canónica | `{"b":1,"a":1.0}` y `{"a":1,"b":1}` dan la misma huella | `1.0` y `1` dan huellas distintas |
| A-33 arreglos | `{"a":null,"b":[1,null]}` se canoniza como `{"b":[1,null]}` | quitar el `null` del arreglo y dejar `[1]` |
| A-33 null = ausente | `{"motivo_ausencia":null,…}` y el mismo JSON sin ese miembro dan la misma huella | una app nueva que agrega `null` provoca `rechazada` por corrupción |
| A-33 reenviador | el buzón reenvía los bytes guardados y la huella coincide | el buzón lee a DTO, vuelve a escribir y cambia el orden o los números → falsa corrupción |
| A-33 sobre lo recibido | la huella se calcula sobre el árbol JSON recibido | la huella se calcula sobre el DTO vuelto a serializar |
| A-14 / A-32 | 1 anotación con `datos` sin `seccion_id` → esa `rechazada`, las otras 199 entran | la partición entera responde `422` |
| A-32 frontera | anotación sin `anotacion_uuid` → `422` y no entra ninguna | se descarta en silencio y entran las demás |
| A-15 sobre genérico | `"tipo":"captura.campo"` con sus `datos` validados por su esquema | campos de `captura.campo` puestos fuera de `datos` |
| A-17 arranque | `arranque` 41 → 42 con `mono_ms` que vuelve a 0: no hay salto de reloj | se marca `hora_dudosa` por el reinicio del contador |
| A-21 veredicto | `"veredicto_local":"marcada"` | `"veredicto_local":"rechazada"` (no viaja), `{"motivo":"…"}` u otro texto |
| A-24 versión | ruta `/v1/` con `"contrato":"1.2"` que el nodo de finca conoce; `nivel` llega en 2.0 | `"contrato":"1.9"` con un nodo de finca 1.2 entrando, o `"2.0"` en `/v1/` sin `422 contrato-incompatible` |
| A-32 sin desconocidos | un campo `"extra": 1` en una anotación → `rechazada` `datos_invalidos` | se guarda blindado o se ignora |
| A-31 invariante | `confirmado 600 · respaldado 600` | `confirmado 600 · respaldado 800` |
| A-27 / A-31 marcas | recibidas 1-200 y 401-600 → `confirmado_hasta: 200` | `confirmado_hasta: 600` |
| A-31 retención | el celular borra hasta `respaldado_hasta` (0 en el ejemplo dorado: no borra nada) | borra hasta `confirmado_hasta` |
| A-35 motivo | `"valor":null,"motivo_ausencia":"no_disponible"` entra marcada `incompleta`; sin motivo entra marcada `incompleta` + `motivo_ausente` | `"valor":40` con motivo → `datos_invalidos`; rechazar un `null` sin motivo |
| A-23 excepciones | una `marcada` vuelve con `codigos: ["incompleta"]`, solo ids cortos | `codigos: ["Falta el valor de tallos"]` (texto) |
| A-23 reenvío | el reenvío repite en `excepciones[]` la marcada con su código guardado | reenvío con `excepciones: []` |
| A-26 invariante | `recibidas 200 = aplicadas 190 + rechazadas 3 + sin_efecto 7`, con `marcadas 12` dentro de `aplicadas` | `aplicadas 178 + marcadas 12 + …` sumadas por separado |
| A-34 Null Object | `switch (desfase) { case Conocido c -> …; case Desconocido d -> … }` | `if (desfase == null)` o un campo `Long` nulo en el DTO |
| A-38 UUID | `0192f0a0-2000-7000-8000-00000000d001` | `6f1c…-4…` (v4) o en mayúsculas → `422` |
| A-39 registro nuevo | D-031 reenvía la n.º 1 → `sin_efecto` + `consecutivo_resuelto` → se borra al quedar respaldada | queda retenida para siempre, o el nodo de finca vuelve a pedir `registrar_de_nuevo` |
| A-40 plazos | procesamiento 20 s < candado 25 s < espera del celular 30 s | candado 30 s = espera 30 s |
| A-42 duplicado | `[{uuid:a1}, {uuid:a1 igual}]` → la segunda cuenta `sin_efecto`; una sola alerta por (uuid, huella) | dos alertas iguales por el mismo reenvío |
| A-43 | fuera del borde (despliegue y actualización) | — |
| A-45 obsoleta | la captura tardía de una orden ya cumplida cuenta `sin_efecto` + `obsoleta` y deja una línea en la bitácora | se guarda como un segundo hecho, o desaparece sin rastro |
| A-45 antigüedad | 4 días → entra `marcada` `vieja`; 8 días sin entregar → sigue retenida y sale en el tablero del gerente | 8 días → el celular la borra solo; 4 días → rechazada |
| A-49 inmutable | editar la cama 12 ya enviada crea una anotación nueva (uuid nuevo) | editar la cama 12 enviada con el mismo uuid → corrupción falsa |
| A-46/A-48 fallo técnico | `rechazada` `datos_invalidos` → borra el contenido, guarda lápida `{137, fallo_tecnico}` y pide recapturar | la retiene completa para siempre, o la borra sin lápida |
| A-47 descarte | `descartes: [{consecutivo: 137, motivo: "fallo_tecnico"}]` sin datos del campo | el hueco 137 congela las marcas hasta que alguien lo cierre a mano |
| A-16 tiempo | `{"crudo_ms": 1792130531000, "mono_ms": 8123400, "arranque": 41, "desfase_declarado_ms": -1200}` | `desfase_declarado_ms` ausente sin ser `null`, o `crudo_ms` corregido por el celular |
| A-18 sesión de captura | `{"uuid", "persona_id": "op-07", "credencial_jti", "inicio", "version_app", "version_motor"}` sin dispositivo | la sesión de captura con `dispositivo`, o sin `persona_id` |
| A-41 según el error | `403` → no reintenta y avisa; `413` → parte en dos | reintenta un `403` cada 5 min; parte un `contrato-incompatible` |
| A-51 credencial | credencial vencida en campo → sigue capturando, marca `capturada_sin_credencial` | el celular bloquea la captura hasta volver a la oficina |
| A-1 identidad | `anotacion_uuid` UUID v7 generado en el celular; nada se ordena por él | ordenar o filtrar por el tiempo que lleva dentro el UUID |
| A-3 clave del hecho | el ingreso compone la clave con la tupla del catálogo de su `version_config` | una clase `ClaveDelHecho` fija en el contrato |
| A-9 contrato versionado | cambiar `particion.schema.json` sin subir `contrato` hace fallar la prueba | el esquema cambia y la prueba pasa |
| A-29 cabeceras | `Authorization: Bearer <JWS>` y `traceparent`; los logs llevan `traceId` sin datos de negocio | un log con `"valor": 40` o el nombre del operario |
| A-37 Java puro | `contrato` sin dependencias de Spring; `backend-finca` no importa `..contrato.interno..` | `import org.springframework…` en `contrato`, o un uso de `interno` desde el backend |
| A-50 versiones | el celular trae solo versiones que el nodo de finca creó; `version_motor` desconocida → anomalía marcada | aceptar en silencio una versión que el nodo de finca no tiene |
| A-45 ⟦2⟧b primer acuse | `primer_acuse_ms` copiado de `recibido_ms` | `primer_acuse_ms` tomado del reloj del celular |
| A-36 transversal | `503` con `Retry-After: 30` y `traceId` | `500` con el stack de la excepción en el cuerpo |

## 5 · Forma técnica
Solo el módulo `contrato/` (JDK 27 con `release 27` según CLAUDE.md; Jackson 3). Lo usan `simulador` y `backend-finca`. Destinos previstos:

| Pieza | Archivo destino | Cubre |
|---|---|---|
| Petición de la partición | `dto/ParticionRequest.java` (`contrato`, `particion_uuid`, `sesiones_captura[]`, `anotaciones[]`, `descartes[]`, `primeros_acuses[]` opcional) + `dto/Descarte.java` (A-47) | A-12, A-18, A-24 |
| Sesión de captura | `dto/SesionCaptura.java` (sin dispositivo; `Dispositivo` va con la sesión de sincronización, ronda 2) | A-18 |
| Anotación (sobre genérico) | `dto/Anotacion.java`, `dto/Tiempo.java` | A-15, A-16, A-17, A-21 |
| Datos de `captura.campo` | `dto/tipos/CapturaCampo.java` (`valor` como `JsonNode`) | A-20 |
| Respuesta | `dto/ResultadoParticion.java`, `dto/Conteo.java`, `dto/Excepcion.java` | A-23, A-26 |
| Códigos | `codigos/CodigoIngreso.java` (`hora_dudosa`, `divergencia`, `hueco_consecutivo`, `datos_invalidos`, `tipo_desconocido`, `incompleta`, `motivo_ausente`, `consecutivo_repetido`, `consecutivo_resuelto`, `version_desconocida`, `vieja`, `obsoleta`, `desfase_desconocido`, `capturada_sin_credencial`, `desviacion_sin_motivo`, `sin_minimo`, `orden_duplicada`, `dispositivo_no_registrado`, `reloj_movido`, `descarte_sin_orden`, `fuera_de_asignacion`, …) | A-23, A-27, A-32, A-45, A-46, A-49 |
| Errores de la partición | `problemas/TipoProblema.java` (catálogo de `type`) | A-22 |
| Esquemas | `src/main/resources/esquemas/ingesta/v1/particion.schema.json`, `anotacion.schema.json`, `tipos/captura.campo.v1.schema.json`, `resultado-particion.schema.json` | A-9, A-15, A-22 |
| Huella | `HuellaCanonica.java` (fachada, paquete raíz; RFC 8785 + SHA-256 sobre el árbol recibido sin nulls) | A-10, A-19, A-33 |
| Validación | `ValidadorContrato.java` (fachada: frontera 422 / anotación) + `interno/ValidadorEsquema.java` (biblioteca, 2020-12) | A-9, A-32, A-38 |
| Módulo | `src/main/java/module-info.java` + prueba de arquitectura en cada consumidor | A-37, A-44 |
| Identidad | `dto/Anotacion.java` (`anotacionUuid` como `UUID`) + `interno/ReglasUuid.java` y el `pattern` del esquema | A-1, A-38 |
| Tiempo | `dto/Tiempo.java` (`crudo_ms`, `mono_ms`, `arranque`, `desfase`) | A-6, A-16, A-17 |
| Respuesta y cabeceras | `dto/ResultadoParticion.java` (con `recibido_ms` y `descartes_acusados`), `dto/Excepcion.java`, `Cabeceras.java` (`TRACEPARENT`, `NO_STORE`, `RETRY_AFTER`) | A-13, A-14, A-26, A-29 |
| Problema | `problemas/Problema.java` (`type`, `title`, `status`, `traceId`, `regla`, `errores[]` con `pointer` y `regla`); los ids de regla van en `snake_case` | A-22, A-36 |
| Null Object | `dto/Desfase.java`, `dto/Valor.java` (con `Motivo`): interfaces selladas | A-16, A-34, A-35 |
| Forma canónica | `interno/Jcs.java` (RFC 8785 propia) + `JcsVectoresTest` | A-10, A-33, K-14 |
| Límites | `interno/LimitesJson.java` (topes de tamaño y profundidad, `FAIL_ON_UNKNOWN_PROPERTIES`, sin tipos polimórficos) | A-9, A-32 |
| Pruebas | `ContratoVersionTest` (falla si el esquema cambia sin subir versión), `HuellaCanonicaTest`, pruebas de ejemplos dorados | A-9, A-19 |

**Fuera del borde** (regla D-07 de la skill: el archivo existe, pero no se define en este detallamiento; cuenta como trazado):

| Decisión | Qué lógica | Ronda y plantilla |
|---|---|---|
| A-2, A-42 | Idempotencia de tres casos, deduplicar alertas | `caso-de-uso` (ingreso) + `adaptador-persistencia` (A-40 ⟦5⟧) |
| A-5, A-40 ⟦1⟧ | Sesión de sincronización, una activa por celular | ronda 2 (`endpoint` de abrir y cerrar sesión) |
| A-7, A-27, A-31 | Orden, huecos, marcas de agua, retroceso, cierre administrativo | `caso-de-uso` (ingreso) |
| A-3, A-11 | Componer la clave del hecho desde el catálogo de la versión de configuración | `caso-de-uso` (ingreso) |
| A-8, A-35 (faltante por regla), A-32 (`divergencia`) | Juicio con el catálogo y las reglas | `caso-de-uso` (ingreso) |
| A-17 (`hora_dudosa`) | Comparar `Δcrudo` con `Δmono` | `caso-de-uso` (ingreso) |
| A-26 (sello) | Sello de la primera sesión | `caso-de-uso` (ingreso) |
| A-30 | Términos de negocio y técnicos | `codigo/IA_DICCIONARIO.md` (no es código) |
| A-40 ⟦2⟧ | Candado por dispositivo y plazo de procesamiento | `caso-de-uso` (ingreso) + adaptador web |
| A-43 | Cómo lee el celular una respuesta más nueva | despliegue y actualización (ADR-015, ADR-016) |
| A-51 ⟦3⟧ | Renovación de la credencial en la oficina | ronda 2 (sesión y credencial) |
| A-39 | Registro de un dispositivo nuevo | ronda aparte (`endpoint` de dispositivos) |
| A-45 ⟦1⟧ | Saber qué orden de captura cumplió cada anotación, registrar la constancia y pasar a «superada» la vieja ya guardada (cómo se retira su dato: `adaptador-persistencia`) | `caso-de-uso` (ingreso) + ADR-026 |
| A-45 ⟦3⟧ | Orden del gerente (recapturar o descartar) desde el tablero hasta el celular | ronda 2 (sesión) + tablero ADR-026 |
| A-46, A-48, A-49 ⟦2⟧ | Comportamiento del celular: no guardar sin mínimo, borrar con lápida, edición local | simulador (ronda aparte) |
| A-31 (retención), A-40 ⟦4⟧, A-41 | Comportamiento del celular: retener, ocultar, reintentar, partir | simulador (ronda aparte) |
| A-31 (`respaldado_hasta`) | Calcular el mínimo verificado entre copias | TX-2 |

## 6 · Contradicciones y pendientes abiertos
Solo lo abierto. Lo resuelto, con su historia, está en `ESPEC-contrato-ingesta-HISTORIA.md`. En la
consolidación se resolvieron K-55 (§3b), K-56 (A-24, §3b) y K-57 (A-23). **La consolidación cambió por error el sentido de A-2, A-50 y otras cinco frases; se restituyeron el 16-oct (K-61..K-66).**

Cerradas en las rondas 1g y 1h (detalle en la fila de su decisión): COH-36, COH-37, COH-40, COH-41, COH-48, COH-49, COH-50, K-67, K-52, K-53, K-54, COH-38, COH-39, K-63, K-65, COH-53, COH-54, COH-55, COH-56, COH-57, COH-58, COH-60, K-50, K-69, K-71, K-72, K-73, K-60.

| Id | Qué | Tipo | Dueño | Estado |
|---|---|---|---|---|
| COH-5 | Un celular sin nada pendiente nunca recibe `respaldado_hasta`: solo viaja en la respuesta de una partición, y no está dicho si `anotaciones: []` vale. Propuesta: las marcas también al abrir y al cerrar la sesión | cohesión (mayor) · A-31, A-12 | ronda 2 | abierta |
| COH-26 | En RF-022/P-S02 «descartado» es el registro que perdió, con aviso a quien capturó; A-47 usa «descarte» para lo que borra el celular, y A-45 ⟦1⟧ no dice si se avisa al que quedó `obsoleta`. Propuesta: «No es» entre aviso de descarte y el aviso de RF-022; decidir el aviso de `obsoleta`. (La entrada «Restauración» decía «nodo» a secas: ya corregido) | coherencia (menor) · A-47, A-45 · diccionario | ronda 1e | abierta |
| COH-27 | El orden 20 < 25 < 30 s solo vale si el aborto a los 20 s es real: cancelar la transacción y poner tiempo límite en la base. Propuesta: `statement_timeout` menor que el plazo de procesamiento, con prueba | disponibilidad (menor, inferencia) · A-40 | adaptador-persistencia | abierta |
| COH-42 | A-51 cita ADR-007, pero ADR-007 descarta la «sesión offline sin caducidad» (decisión de Juan: ajustar ADR-007 y BR-N5). `capturada_sin_credencial` compara con `crudo_ms`, no con el sello normalizado. Propuesta: usar el sello normalizado | coherencia (menor) · A-51, A-23 | Juan · ronda 1g | abierta |
| COH-43 | «El tablero muestra la edad que el celular declara», pero ningún campo la lleva; «respuesta normal» no está definida. Propuesta: el campo va en la ronda 2 (abrir sesión); respuesta normal = solo `200` | cohesión (menor) · A-45 ⟦3⟧ | ronda 1g · ronda 2 | abierta |
| COH-44 | Partir no cambia la anotación, pero falta decir que cada mitad lleva las `sesiones_captura[]` que sus anotaciones referencian, sus `primeros_acuses[]`, y a qué mitad van los `descartes` | cohesión (menor) · A-41, A-49, A-33 | ronda 1g | abierta |
| K-2 | ADR-028 nombra los resultados de la sesión `completada · parcial · fallida`; `IA_TX1` y la propuesta del chat usan `completa · parcial` | entre documentos | ronda 1 | abierta |
| COH-52 | Tras restaurar, el nodo de finca olvida la deduplicación de alertas, si ya pidió `registrar_de_nuevo` y registros hechos después del respaldo: alertas repetidas o un segundo `registrar_de_nuevo`. Propuesta: decir qué estado sobrevive a la restauración | confiabilidad (menor, inferencia) · A-42, A-39 · ES-R14 | ronda 1g | abierta |
| COH-61 | **¿Persona o dispositivo?** La cama 12 se asigna a op-07 en D-014 y se reasigna a op-12 en el mismo D-014 (celular compartido): la captura de op-07 coincide por dispositivo con las dos órdenes y por persona solo con la vieja; no se sabe si es obsoleta (no se guarda) o RF-022 (se guardan las dos). Igual tras `registrar_de_nuevo` (D-031 frente a D-014). Propuesta: manda la persona de la sesión de captura; una reasignación entre personas del mismo dispositivo cuenta como reasignación; lo reenviado por un dispositivo nuevo se compara con el original | cohesión (blocking) · A-45, A-51, A-39, A-18 · ADR-026 | ronda 1i | abierta |
| COH-62 | La constancia de una superada guarda bytes y la de una obsoleta tardía no: el resultado depende del orden de llegada (contra C-61). Propuesta: toda constancia conserva los bytes | confiabilidad (mayor) · A-45, A-48, A-31 · ADR-024 §1, ADR-031 §1 | ronda 1i | abierta |
| COH-63 | Un `sesion_captura_uuid` no declarado es un defecto del sobre (lo arma el celular), pero se trata como fallo técnico de la anotación y se borra un dato bueno. Propuesta: `422` con `pointer` a `/sesiones_captura` y rearmar el sobre (A-41), sin borrar | confiabilidad (mayor) · A-38, A-41, A-46 | ronda 1i | abierta |
| COH-64 | D-099, sin asignación, captura después que D-022 (asignada y verificada): RF-022 le da el estado a D-099 y nadie lo decidió; al revés, no se dice si D-022 la deja superada. Propuesta: con `fuera_de_asignacion`, la asignada gana el estado y la otra queda marcada (excepción a RF-022, decide Juan) | cohesión (mayor) · A-45, A-2 · P-S02 | ronda 1i · Juan | abierta |
| COH-66 | Si op-12 entra mientras P1 de S1 está en proceso, no se dice si el cierre de S1 espera al candado; P1 podría confirmar en una sesión cerrada. Propuesta: el cierre por cambio de cuenta espera a que termine la partición en curso (≤ 25 s) | cohesión (mayor) · A-51, A-40, A-26 · ADR-028 | ronda 1i | abierta |
| COH-67 | Tras una corrupta, si se restaura un respaldo anterior a la versión buena, solo queda la cuarentena, y no se dice si sobrevive a la restauración; el `descartes: fallo_tecnico` reenviado cierra el hueco sin cama a rehacer. Propuesta: la cuarentena entra en el respaldo de TX-2; un descarte de algo aplicado antes de restaurar entra en la lista de pérdida de A-31 | confiabilidad (mayor) · A-2, A-48, A-47, A-31 · CN-15, ESC-54 | ronda 1i · TX-2 | abierta |
| K-75 | **Choca con P-N01 (ACEPTADO 13-oct):** «24 h normales más 7 días de gracia **solo para capturar**». A-51 ⟦2⟧ dice que el acceso en campo «nunca se pierde por vencimiento». Hay que conciliar: ¿la gracia de 7 días es el límite, o Juan reabre P-N01? | con fuente aceptada (−15) | Juan · ronda 1i | abierta |
| K-80 | Un `tipo` o `version_tipo` desconocido da `rechazada` y el celular lo borra, pero A-50 dice que una versión desconocida es anomalía que entra `marcada` y no se pierde | interna (−5) | ronda 1i | abierta |
| K-81 | Frontera sin definir para: campos mal formados de `sesiones_captura[]` que no son el uuid (`persona_id`, `inicio`, `version_*`); entradas mal formadas o repetidas de `descartes[]` y `primeros_acuses[]`; `anotaciones: []` sin descartes | (−5) | ronda 1i | abierta |
| K-82 | A-33 deja reenviar a otros (buzón del gerente, nodo de contingencia), pero el dispositivo solo va en la sesión de sincronización y A-30, A-31 y A-39 dependen de (dispositivo, consecutivo): ¿de quién son las anotaciones reenviadas? Sin ronda destino | (−5) | ronda 1i | abierta |
| K-4 | El texto de ADR-035 (sesión de captura) no está en los `.md`; solo se cita | fuente faltante | Juan | abierta |
| K-28 | **Resuelta en la ESPEC (ronda 1d P14); falta que Juan amplíe ADR-028 §3** con el texto propuesto en A-31. **Cita infiel, corregida en el texto:** la excepción del sello tras restaurar se atribuía a ADR-028 §3, que dice que el sello no cambia. Ahora consta como decisión nueva; falta que Juan amplíe ADR-028 o la descarte | cita (−5) | Juan | abierta |
| K-40 | A-45 ⟦1⟧ (la captura tardía de una orden ya cumplida no se guarda) choca con ADR-027 §2 y ESC-34 («0 registros descartados automáticamente», se guardan ambas versiones) y con CN-24. Decisión de Juan en la ronda 1e P3, con constancia mínima en la bitácora. Propuesta: que Juan ajuste ADR-027 §2 y ESC-34 para la captura de una orden ya cumplida, **y además ADR-024 §1 y ESC-58 para la superada** (sacar de la historia un evento ya guardado; ronda 1h P2) | con un ADR (−15) | Juan | abierta |
| K-41 | **Juan depreca ADR-027** («mal conceptualizado», ronda 1e P4). Los ADR no se escriben desde la IA: Juan tiene que marcarlo en `ADR.xlsx` y en el documento de alternativa de solución, y decidir qué reemplaza a sus §1-§5, y además ajustar **ADR-024 §1**, que pone `corrige_a` en la tupla del evento (K-47) (identidad UUID v7, tres casos de idempotencia, clave del hecho, consecutivo). Mientras tanto, la ESPEC sostiene esas reglas como decisiones propias | ADR deprecado (−5) | Juan | abierta |
| K-59 | A-51 ⟦2⟧ (capturar con la credencial vencida) choca con ADR-007, que descarta la sesión offline sin caducidad (= COH-42). Decisión de Juan: ajustar ADR-007 y BR-N5 | con un ADR (−5) | Juan | abierta |

## 7 · Fuera del dominio (para otro canal)
- **Ronda 2** (`endpoint` de abrir y cerrar sesión, `/archivos`): el celular declara al abrir el último `respaldado_hasta` recibido (COH-54); cierre como `parcial` al entrar otra cuenta y bajada del catálogo y la asignación de cada persona (A-51); sesión activa por celular (A-40), marcas al abrir sesión (COH-5), renovación de credencial (A-51), edad declarada para el tablero (COH-43), resultados de la sesión (K-2).
- **`caso-de-uso` (ingreso):** idempotencia, orden y marcas, obsoleta/superada y constancias, `hora_dudosa`, `vieja`, divergencia y juicio con el catálogo, sello tras restaurar (tabla «Fuera del borde» de §5).
- **`adaptador-persistencia`:** inserción atómica (A-40), índice único del uuid entre anotaciones y constancias (A-2), `statement_timeout` (COH-27), cómo se retira el dato de una superada.
- **Simulador (el celular):** retención y ocultamiento, lápidas, edición local e inmutabilidad, reintentos y espera, partir particiones, aviso escalado, orden del gerente.
- **TX-2:** calcular `respaldado_hasta` por dispositivo; respaldar también los paquetes de configuración (COH-40); **frecuencia de la prueba de restauración** de la que depende `respaldado_hasta` (K-67; propuesta: probar cada respaldo nocturno, para que el celular no retenga hasta un mes).
- **Despliegue y actualización:** cómo lee el celular una respuesta más nueva (A-43); orden de actualización.
- **Configuración:** ¿la edad de 3 días y la ventana de 7 deben ser por tipo de campo? (ADR-025: un corte no se recaptura); formatos de ids de negocio (§3b).
- **Tablero ADR-026:** cierre administrativo de huecos, lista de camas a rehacer, decisiones del gerente.
- **Antes de programar:** TX-1.K, P-S04 y P-S05 siguen `POR ACEPTAR` (CLAUDE.md: parar y preguntar a Juan).

## 8 · Plan de segmentos
(etapa 4)

## 9 · Matriz requisito → código → prueba
(etapa 4)

## 10 · Observaciones de revisión
(etapa 5)

## 11 · Informe de verificación
(etapa 6)
