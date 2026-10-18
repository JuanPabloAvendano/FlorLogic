# ESPEC · contrato de ingesta (TX-1.K)

REGISTRO DE IA · NO ES FUENTE — lo genera la skill DetallarParaCodificar a pedido de Juan, 16-oct-2026. Podada el 18-oct-2026 con la skill information-pairment.
Manda por encima: `ADR.xlsx` · `docs/03-arquitectura/decisiones/` · `IA_00-LEEME-transacciones.md`.

## Ficha
| Campo | Valor |
|---|---|
| Definición acordada | Acuerdo entre el celular y el servidor de la finca para entregar lo capturado en una jornada: qué lleva cada mensaje, qué responde el servidor, cómo se reconoce un envío repetido y cómo cambia de versión sin romper a nadie. No es la lógica que decide si una anotación vale, ni la base de datos, ni el código que recibe la petición (acordada 16-oct, 2.ª vuelta) |
| Diccionario | `codigo/IA_DICCIONARIO.md` (compartido por todos los detallamientos) |
| Historia | `ESPEC-contrato-ingesta-HISTORIA.md` (rondas 1 a 1f) · `ESPEC-contrato-ingesta-HISTORIA-1g-1h.md` (rondas 1g y 1h, copia íntegra antes de la poda) |
| Apartado | `IA_apartado-2026-10-18/IA_00-INDICE.md`: lo que no es del contrato, con su destino (7 semillas y 8 documentos en revisión). Poda aprobada por Juan el 18-oct |
| Elemento | Módulo `codigo/contrato/`: los mensajes de los 4 POST de ingesta de TX-1; **parte 1** (esta): `POST …/particiones` · **parte 2** (ronda 2, en esta misma ESPEC): `POST /sesiones`, `…/cierre` y `…/archivos` (ronda 1i P8, resuelve COH-71) |
| Plantilla | `endpoint` (solo §1 ⟦1⟧-⟦10⟧; ⟦11⟧ frontera y controladores quedan fuera) |
| Dominio | Entra: forma de petición y respuesta de los 4 POST, versión, errores, huella · Sale: DTO como records, JSON Schema, `HuellaCanonica` · Fuera: controladores, puertos, reglas, base de datos |
| Generalidad | Contrato campo por campo |
| Para qué | Especificar para programar |
| Estado | BORRADOR |
| **Etapa actual** | 3 Preguntas · ronda 1j |
| **Dónde retomar** | Ronda 1j en curso (18-oct). Ya cerradas: redacción (K-88, COH-79, COH-80) · P1 COH-74/K-92 · P2 COH-75 · P3 COH-76 · P4 COH-77/K-89 · P5 COH-78/K-90 · P6 K-91 · P7 K-84 · P8 K-85, todas ★. **Estimado:** ≈85,6 % (7 K cerradas: K-84, K-85, K-88 a K-92; K-93 nueva resta; lo confirma el verificador). Faltan: P9 K-86 · P10 K-93 (rearmado idéntico sin alerta; entró a la ronda por D-12) · P11 extra, con permiso de Juan: par de A-52. Va en P9 |
| Precisión | **78,0 % oficial** (verificador, ronda 1i, 18-oct): cobertura 85,4 · contradicciones 50 · prácticas 88,0 · trazabilidad 91. Falta el par de A-52 (−3). **Coherencia (D-04): pasa**, sin bloqueantes; 5 mayores nuevos (COH-74 a COH-78). Proyección: ≈85,3 % con el par de A-52 y 5 K cerradas; ≈91,5 % con las 10 K. Historia: 52 · 59 · 68 · 60 · 60 · 62 · 62 · 63 · 64,5 · poda · 74,8 · 78,0 |
| Completitud | — · Explicación — |
| Avance por etapa | Tamaño ✔ · Consulta ✔ · Preguntas (rondas 1, 1b–1h; consolidación tras la 1f; poda y verificación el 18-oct) · Generación — · Revisión — · Verificación — · Aceptación — |

## 1 · Propósito
Fijar el contrato que comparten el simulador del celular y el backend de la finca para entregar una
jornada ya capturada (TX-1): qué mensajes viajan en cada uno de los 4 POST, con qué campos, cómo se
identifica un duplicado y cómo evoluciona sin romper a nadie. Fuente: `IA_TX1-sincronizacion.md` §4,
TEC-10, TEC-11.

## 2 · Decisiones vigentes
Cada fila dice la **regla vigente del contrato**, sin historia. Los ids no cambian. Lo que una fila decía
sobre otro elemento salió en la poda del 18-oct; el texto anterior está en `ESPEC-contrato-ingesta-HISTORIA-1g-1h.md`
y su destino en `IA_apartado-2026-10-18/IA_00-INDICE.md` §3.

**Retiradas:** A-4 (`corrige_a`) y A-28 (corrección colgante): el operario no corrige (A-49). A-25
(blindaje): ningún campo desconocido entra (A-32). A-43: va con despliegue y actualización.
**Apartadas enteras (18-oct):** A-5 (sesión de sincronización → semilla sesión), A-6, A-8 y A-11 (→ semilla ingreso), A-7 y A-48 (→ semilla celular), A-45 ⟦3⟧ (→ semilla celular, `RF-P02`).

### Identidad, huella e idempotencia
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-1 | La identidad de una anotación es su `anotacion_uuid`, un UUID v7 generado en el celular. Es opaco: nada se ordena ni se filtra por su contenido; el único uso de su valor es **desempatar** de forma estable y sin significado cuando dos capturas tienen el mismo sello (ADR-031 §4) | ronda 1 · antes ADR-027 §1 (deprecado, K-41: preguntas para Juan del apartado) | decidida |
| A-2 | Resultado de la idempotencia por anotación: mismo id y misma huella → `sin_efecto`; mismo id y otra huella → **corrupta**: `rechazada` con `datos_invalidos`, nunca se sobrescribe; es fallo técnico (A-41); otro id → entra y su resultado lo decide el ingreso. El id es único entre anotaciones y constancias. *Orden de juicio, cuarentena y RF-022: semilla ingreso* | rondas 1, 1g P6 (elección de Juan), 1g P10 | decidida |
| A-3 | El contrato garantiza que viajan los datos de la clave del hecho (por defecto producción · sección · campo · jornada); la clave la declara el catálogo y la calcula el ingreso. No incluye autor, dispositivo ni versión de configuración | ronda 1d P2 | decidida |
| A-19 | La huella es `SHA-256` de la anotación entera en forma canónica; el nodo de finca la recalcula siempre y nunca usa una que mande el celular | ronda 1 P7 | decidida |
| A-33 | `huella = SHA-256(JCS(quitarNulls(árbol JSON recibido)))`, con versión de algoritmo (`{"alg": "h1", "sha256": …}`); solo se comparan huellas de la misma versión. `quitarNulls` quita solo miembros de objeto que valen `null`, nunca elementos de arreglo, **en cualquier nivel, también dentro de `valor`**. Un miembro `null` vale lo mismo que uno ausente. Se calcula sobre el JSON recibido, nunca sobre el DTO. **Todo reenviador** (celular, buzón del gerente, nodo de contingencia) reenvía los bytes originales, sin volver a serializar | rondas 1b, 1c, 1d | decidida |
| A-38 | Los ids que genera el celular son UUID v7 en minúsculas (`^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$`). Si falla uno que identifica (`anotacion_uuid`, `particion_uuid`, `sesiones_captura[].uuid`) → `422 sobre-invalido`; si `sesion_captura_uuid` está mal escrito → solo esa anotación, `rechazada` `datos_invalidos` (el defecto está en sus bytes, que son inmutables). Si está **bien escrito pero no está declarado en `sesiones_captura[]`**, el defecto es del sobre: `422 sobre-invalido` con `pointer: /sesiones_captura`, `regla: sesion_no_declarada` y `faltan[]` con los uuid que faltan; el celular rearma el sobre desde su almacén (A-41) y no borra nada (ronda 1i P5, ★, resuelve COH-63; reemplaza la ronda 1h P4) | rondas 1b, 1d | decidida |
| A-42 | El mismo id dos veces en una partición se procesa en el orden del arreglo: la segunda copia es `sin_efecto` si es igual, o corrupta si es distinta. *Deduplicar alertas: semilla ingreso* | ronda 1c P16 | decidida |
| A-49 | ⟦3⟧ **Inmutable desde que sale:** lo que ya viajó, aunque no tenga respuesta, no se edita; cambiarlo es una **recaptura** con id nuevo, así que un mismo id con otra huella siempre es corrupción. ⟦4⟧ `datos.motivo_desviacion` (id del catálogo) cuando una regla pide motivo por una desviación brusca; **siempre que la regla detecta la desviación, la anotación entra `marcada` con el id de la regla**, traiga motivo o no; si no hay motivo, se agrega `desviacion_sin_motivo` (ronda 1j P8, ★, resuelve K-85). *⟦1⟧ ⟦2⟧ ⟦5⟧ ⟦6⟧: decisiones «corrige solo el gerente» y «ADR-027 deprecado», semilla celular* | rondas 1e P4-P5, 1f P1 | decidida |

### La petición
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-12 | `POST /ingesta/v1/sesiones/{id}/particiones`: el celular entrega una partición de su almacén de salida; la llama una persona con credencial JWS | IA_TX1 pasos 7-14 · ADR-007 | confirmada |
| A-29 | Cabeceras: `Authorization: Bearer <JWS>`, `Content-Type: application/json`, `traceparent` opcional. No hay `Idempotency-Key`: `particion_uuid` solo correlaciona; se conserva al rearmar el sobre y cambia al quitar anotaciones (A-41). Los logs llevan `sesion_id`, `particion_uuid` y `traceId`, sin datos de negocio | ronda 1 P19 · ADR-026 | decidida |
| A-30 | *Partición*: un trozo, de hasta `max_anotaciones_particion`, de lo pendiente de **un** celular, que sube en un POST; nunca mezcla celulares. Todos los celulares sincronizan en paralelo. (*Lote* es negocio: diccionario y semilla entidad) | rondas 1b, 1c | decidida |
| A-15 | La anotación es un sobre genérico: `anotacion_uuid`, `consecutivo`, `tipo`, `version_tipo`, `sesion_captura_uuid`, `tiempo`, `version_config`, `veredicto_local` y `datos`. Hoy solo existe `captura.campo`; cada tipo valida sus `datos` con su esquema | ronda 1 P2 | decidida |
| A-16 | `tiempo`: `crudo_ms` (reloj de pared, nunca se toca), `mono_ms` (reloj monótono), `arranque` y `desfase_declarado_ms` (el de la última sincronización; `null` o ausente si nunca sincronizó, que valen lo mismo (A-33) → la respuesta trae `desfase_desconocido`; ronda 1j P7) | ADR-031 §2 · SPK-12 | decidida |
| A-17 | `arranque` sube en cada encendido; `mono_ms` solo es comparable dentro del mismo arranque. El contrato garantiza los datos; el cálculo de `hora_dudosa` es del ingreso | SPK-12 M2b · IA_TX1 paso 11 | decidida |
| A-18 | La partición declara sus `sesiones_captura[]`: `uuid`, `persona_id` (quien capturó), `credencial_jti` vigente al capturar, `inicio`, `version_app` y `version_motor` (las de al capturar; actualizar la app abre una sesión de captura nueva). Cada anotación la referencia por `sesion_captura_uuid`. El dispositivo va una vez en la sesión de sincronización (ronda 2). La credencial de la petición es **quien entrega** | rondas 1, 1b, 1c · ADR-029 · ES-S12 | decidida |
| A-20 | `datos` de `captura.campo`: `produccion_id`, `seccion_id`, `campo_id`, `jornada` (`AAAA-MM-DD`, provisional por P-S04), `valor` (JSON libre o `null`, validado por el catálogo de su versión), `motivo_ausencia` si falta el valor y `motivo_desviacion` si la regla lo pide. En v1 solo se anota a nivel de **sección**; otro nivel exige una versión mayor (A-24) | rondas 1, 1c, 1d · ADR-024 | decidida (jornada: P-S04) |
| A-21 | `veredicto_local` es `aceptada` o `marcada`; lo que el celular rechaza no viaja (ADR-024, ESC-02) | ronda 1c P4 | decidida |
| A-24 | La versión mayor va en la ruta (`/v1/`), la menor en la partición (`"contrato": "1.2"`). Una menor solo agrega campos opcionales que no cambian el sentido de los demás. El nodo de finca solo acepta versiones que conoce; lo demás es `422 contrato-incompatible`. **Orden de validación:** primero la versión de `contrato`, después el esquema. Un `contrato` **mal escrito** (p. ej. `"1.x"`) no es una versión: `422 sobre-invalido` con `pointer: /contrato` (ronda 1h P5, resuelve K-63) | rondas 1, 1c, 1d, 1e | decidida |
| A-27 | El orden de consecutivos no se impide, se marca: tras un hueco las anotaciones llevan `hueco_consecutivo`; los solapes los resuelve la idempotencia | ronda 1 P17 | decidida |
| A-47 | **Aviso de descarte:** el celular avisa en `descartes[]` (opcional; mismo tope que `anotaciones`) cada consecutivo que borró sin entregar, **con el número de su almacén**: `{consecutivo, motivo}`, `motivo` = `fallo_tecnico` o `descartada_por_gerente`. El nodo de finca cierra el hueco y lo deja en la bitácora. Idempotente: un aviso repetido, o de algo que ya llegó, no tiene efecto. **Todo aviso vuelve en `descartes_acusados`.** Una partición puede traer solo descartes | rondas 1d, 1e, 1f | decidida |
| A-45 ⟦2⟧b | `primeros_acuses[]` (opcional): `{anotacion_uuid, primer_acuse_ms}` de lo que se reenvía tras una restauración; va **fuera** de la anotación, que es inmutable. **`primer_acuse_ms` es el reloj del nodo de finca**: cada respuesta trae `recibido_ms` y el celular lo devuelve tal cual. Una entrada imposible (futura, anterior al sello, o de un uuid que no viene en la partición) se ignora y va a la bitácora. Tope: `max_anotaciones_particion` (ronda 1h P3, resuelve COH-39 y K-60) | ronda 1f P8 | decidida |

### Resultados y códigos que devuelve el nodo de finca
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-14 | Una anotación mala no tumba la partición: hay un resultado por anotación | IA_TX1 paso 14 | confirmada |
| A-32 | **Frontera.** `422 sobre-invalido` (toda la partición) solo si no se puede leer o identificar, o trae algo que no está en el contrato. **Si el fallo de lectura cae dentro de `/anotaciones/n`** (JSON ilegible o profundidad mayor que `max_profundidad_particion`), el `422` lleva `pointer: /anotaciones/n` y `regla` `json_ilegible` o `profundidad_excedida`, y el celular aísla esa anotación (A-41); sin `pointer` solo queda lo que no se puede ubicar (ronda 1i P6, ★, resuelve COH-68). Casos: JSON roto, **campo desconocido en el sobre de la partición**, profundidad de toda la partición mayor que `max_profundidad_particion` (límite duro; es anomalía), falta `contrato`/`particion_uuid`/`anotaciones[]`, una anotación sin `anotacion_uuid` o `consecutivo` válidos, o un `sesion_captura_uuid` que no está declarado en `sesiones_captura[]` (A-38). **Por anotación:** forma inválida, campo desconocido, topes de anotación pasados (incluida la profundidad dentro de una anotación, `max_profundidad_anotacion` — ronda 1h P1, resuelve K-73), motivo mal escrito → **fallo técnico**: `rechazada` `datos_invalidos`. **`tipo` o `version_tipo` bien escritos pero desconocidos** son un eje de versión, no un dato malo: `422 contrato-incompatible` con `pointer` a la anotación y `regla: tipo_desconocido` (ronda 1i P9, ★, resuelve K-80). Un `tipo` mal escrito (no cumple §3b) o ausente, o un `version_tipo` que no es entero o falta, es forma inválida: `rechazada` `datos_invalidos` (ronda 1j P6, ★, resuelve K-91). Lo que se juzga con el catálogo o las reglas (valor fuera de rango, motivo que no está en el catálogo) → `marcada` `divergencia`. `version_config` desconocida → `marcada` `version_desconocida`. *Cómo se juzga y qué se recaptura: semilla ingreso* | rondas 1b, 1c, 1d, 1e, 1h · ADR-014 · ES-S10, ES-S11 | decidida |
| A-35 | **Dato ausente:** `valor: null` con `motivo_ausencia` → `marcada` `incompleta`; sin motivo → `marcada` `incompleta` + `motivo_ausente`; valor **con** motivo → fallo técnico. **Producto faltante:** hay cifra pero menor a lo esperado; lo decide una regla del nodo de finca y vuelve como id de regla en los códigos. Los motivos viven en el catálogo de configuración | rondas 1b, 1c · P-S01 · CN-36 | decidida |
| A-46 | **`sin_minimo`:** una captura sin el mínimo requerido de su actividad que llega (falló el motor del celular) vuelve `marcada` con `divergencia` y `sin_minimo`. *El flujo de la jornada y el destino en el celular: semilla celular* | rondas 1d P5, 1e P6, 1f P6 · ADR-024 · P-S01 | decidida |
| A-45 ⟦1⟧ | **`obsoleta`:** cuando el ingreso decide que una captura llegó tarde para una orden de captura ya cumplida, la anotación cuenta `sin_efecto` con `obsoleta`; queda una **constancia** con su uuid y su huella, que cuenta como recibida para las marcas y en un reenvío vuelve igual en `excepciones[]`. Códigos relacionados que puede devolver: `orden_duplicada` y `fuera_de_asignacion`. *Cuándo aplica, cómo se deduce la orden y la «superada»: semilla ingreso* | rondas 1e P3, 1f P2-P4, 1g, 1h · ADR-026 | decidida |
| A-45 ⟦2⟧ | **`vieja`:** código que el ingreso pone según la edad medida hasta la primera llegada (`primeros_acuses[]`, A-45 ⟦2⟧b). *Umbral y regla: semillas ingreso y configuración* | rondas 1d P3, 1f P8 | decidida |
| A-23 | `excepciones[]` lleva cada anotación con resultado o códigos que mostrar: `rechazada`, `marcada` y `sin_efecto` con `obsoleta`. Cada una: `anotacion_uuid`, `resultado`, `codigos` (ids cortos, sin textos: id de regla del catálogo `R-…` o código del ingreso en `snake_case`, §3b). La marca sirve para que el gerente de producción haga las correcciones. En un reenvío repite el resultado y los códigos guardados, y los conteos la cuentan en `sin_efecto`. Códigos del ingreso: §5; entre ellos `capturada_sin_credencial` (el `crudo_ms` es posterior al vencimiento de la credencial de la sesión de captura, ES-S12), `reloj_movido` (el desfase declarado al capturar y el medido al entregar difieren más que el umbral, ADR-031 §3) y `dispositivo_no_registrado` (ES-S13) | rondas 1, 1c, 1d, 1f | decidida |
| A-39 | **Consecutivo reiniciado.** El mismo (dispositivo, consecutivo) con otro id → `marcada` `consecutivo_repetido` y `"accion": "registrar_de_nuevo"` (una sola vez por dispositivo). Cuando el dispositivo nuevo la reenvía, vuelve `consecutivo_resuelto`. *El registro del dispositivo: semilla sesión* | rondas 1c, 1d | decidida |
| A-50 | **Versiones desconocidas son anomalía.** `422 contrato-incompatible` y `version_desconocida` no ocurren en operación normal, porque el nodo de finca crea todas las versiones (decisión «nada desconocido entra a la finca»); si aparecen, se alerta al administrador. Una `version_app` o `version_motor` desconocida también: la anotación entra `marcada` `version_desconocida` (ronda 1h P9, COH-60). Un `tipo` o `version_tipo` desconocido → `422 contrato-incompatible` con `pointer` (A-32; ronda 1i P9) | ronda 1f P5, 1g P7-P8 · nota de Juan | decidida |

### La respuesta y los errores
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-13 | Éxito: `200` (no hay recurso nuevo con URL propia), síncrono, con `Cache-Control: no-store` | ronda 1b P14 | confirmada |
| A-26 | Respuesta `200`: `particion_uuid`, `confirmado_hasta`, `respaldado_hasta`, `conteo` {`recibidas`, `aplicadas`, `marcadas` ⊆ `aplicadas`, `rechazadas`, `sin_efecto` (ya guardadas u obsoletas)} con `recibidas = aplicadas + rechazadas + sin_efecto`, `excepciones[]`, `recibido_ms` (reloj del nodo de finca, A-45 ⟦2⟧b), `descartes_acusados[]` (**opcional**: solo si hubo descartes) y, solo si hace falta, `accion`. **Los conteos son la foto del momento** de esa respuesta y no se reescriben después (ronda 1h P2, resuelve COH-38). No se guardan respuestas: un reenvío se procesa de nuevo (el de una superada devuelve `sin_efecto` + `obsoleta`). *El sello de la primera sesión: semilla ingreso* | rondas 1, 1b | decidida |
| A-22 | Errores de toda la partición en `application/problem+json` con `regla`: `401 credencial-invalida`, `403 sin-permiso` (`ingesta_entregar`, `empresa_distinta`, `sesion_ajena`; se comprueba antes de leer anotaciones), `404 sesion-desconocida`, `409 sesion-cerrada`, `409 particion-en-curso` (una a la vez **por dispositivo**), `413 particion-muy-grande`, `422 sobre-invalido` con `errores[].pointer`, `422 contrato-incompatible`, `429 demasiadas-peticiones`, `500 error-interno` y `503 nodo-ocupado` (también cuando vence el plazo de procesamiento, A-40). Agregar un `type` es compatible. **Orden de comprobación** (responde la primera que falla): 401 → 403 `ingesta_entregar` / `empresa_distinta` → 404 / 409 `sesion-cerrada` → 403 `sesion_ajena` (solo se evalúa con la sesión ya encontrada; ronda 1j P4) → 429 → 409 `particion-en-curso` → 413 → 422; `500` y `503` pueden salir en cualquier momento (ronda 1i P10, ★, resuelve K-83 y COH-72). **Los `422`, en tres fases** (ronda 1j P1, ★, resuelve COH-74 y K-92): ⟦1⟧ el campo `contrato`: falta o está mal escrito → `sobre-invalido` con `pointer: /contrato`; desconocido → `contrato-incompatible`. ⟦2⟧ lectura y esquema del sobre de la partición (JSON del sobre, profundidad, campos obligatorios, forma de `sesiones_captura[]`) → `sobre-invalido`. ⟦3⟧ las anotaciones en el orden del arreglo: la primera que falla de forma ubicable decide el `type`, y `errores[]` lleva todas las del mismo `type` que se alcanzaron a leer (ronda 1j P3) (ilegible o profundidad → `sobre-invalido`; `tipo` desconocido → `contrato-incompatible`; `sesion_captura_uuid` no declarado → `sobre-invalido` `sesion_no_declarada`). Un fallo de forma o un campo desconocido en una anotación no corta: es `rechazada` y se sigue con las demás | rondas 1, 1b, 1d | decidida |
| A-36 | Todo error lleva `traceId`; `429`, `503` y `409 particion-en-curso` traen `Retry-After`; `500` nunca expone detalles internos; ningún error lleva datos de negocio | ronda 1b P13 | decidida |
| A-41 | **Qué hace el celular según el error:** `413` → partir en dos; `422 sobre-invalido` con `pointer` a `/anotaciones/n` → quitar esa anotación (fallo técnico) y reenviar el resto. Con `regla: json_ilegible` el `pointer` es solo una pista: el celular lee esa anotación con sus propios bytes; si no se lee, la quita; si se lee, lo trata como defecto del sobre y rearma (ronda 1j P2, ★, resuelve COH-75). Con `profundidad_excedida` el `pointer` es fiable y se quita sin confirmar; **sin `pointer` a una anotación** (defecto del sobre) → **no se parte**: el celular rearma **solo el sobre** una vez desde su almacén, con los bytes originales de cada anotación y sin regenerar ids ya enviados, y reenvía; el rearmado **conserva su `particion_uuid`**, y quitar anotaciones crea otra partición con `particion_uuid` **nuevo** (ronda 1j P3, ★, resuelve COH-76); el celular quita de una vez todas las de `errores[]`; si vuelve a fallar, el celular retiene **solo esa partición** como anomalía y sigue con las demás (el hueco congela las marcas de agua hasta resolverlo), el nodo de finca alerta al administrador cuando el mismo `particion_uuid` recibe un segundo `422` sin `pointer` a una anotación **con otro cuerpo** (`sobre_invalido_repetido`; un reenvío idéntico es un reintento y no cuenta; cómo lo recuerda: semilla ingreso) y la salida es actualizar la app (rondas 1g P3, 1h P9 y 1i P7, COH-48, COH-56 y COH-69); `422 contrato-incompatible` → no partir ni borrar; **por el `contrato` (fase 1) se detiene todo el celular** y avisa qué lado actualizar; **por `tipo_desconocido` (fase 3, con `pointer`) se retiene solo esa partición y se sigue con las demás**, y el nodo de finca alerta al administrador con el `pointer` (ronda 1j P5, ★, resuelve COH-78 y K-90); `401` → renovar credencial y reintentar; `403` con `ingesta_entregar` o `empresa_distinta` → no reintentar, **se detiene todo el celular** y avisa al administrador; `403 sesion_ajena` (entrega otra persona) → abrir otra sesión a su nombre; `404` y `409 sesion-cerrada` → abrir otra sesión; `5xx`, `429`, `409 particion-en-curso` → reintentar. La espera es la mayor entre `Retry-After` y la espera creciente. Nada se borra por estos errores | rondas 1c, 1d, 1f | decidida |
| A-40 | ⟦2⟧ Plazos: procesar ≤ 20 s (si no, `503`) < candado por dispositivo 25 s < espera del celular 30 s. ⟦3⟧ `409 particion-en-curso` trae `Retry-After`. ⟦4⟧ Espera creciente `min(2^n · 1 s, 5 min) + azar(0..1 s)`. *⟦1⟧ una sesión por celular: semilla sesión · ⟦5⟧ inserción atómica: semilla persistencia* | rondas 1c P8, 1d P8 · ESC-38 | decidida |

### Marcas de agua
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-31 | `confirmado_hasta`: mayor consecutivo recibido sin huecos (rechazadas y constancias cuentan como recibidas). `respaldado_hasta`: mayor consecutivo dentro de un respaldo verificado y presente en las dos copias; lo calcula TX-2. Invariante `respaldado_hasta ≤ confirmado_hasta`. Las marcas pueden **bajar** tras una restauración: el celular anula los acuses por encima y reenvía desde ahí. Viajan en cada respuesta de partición (y al abrir la sesión: ronda 2, COH-5 en la semilla de sesión). *Cálculo, alerta de pérdida, cierre de huecos y retención: semillas ingreso, respaldo y celular* | rondas 1b-1g · P-S03 · TX-2 · ES3 | decidida |

### Transversales
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-51 | La credencial de la petición es **quien entrega** (A-18). Ante `401` el celular renueva y reintenta, sin borrar nada; `403 sesion_ajena` cuando entrega una persona distinta de la que abrió la sesión (el dispositivo: parte 2, A-52). *Login local, acceso en campo, renovación y celular compartido: semilla sesión* | ronda 1f P9, 1g P9, 1h P6 · ES-S12 | decidida |
| A-9 | DTO como `record`, JSON Schema 2020-12 versionado, prueba que falla si el contrato cambia sin subir versión. Jackson 3 con `FAIL_ON_UNKNOWN_PROPERTIES` y sin tipos polimórficos | TEC-10, TEC-11 · ronda 1e P8 | decidida |
| A-10 | Forma canónica RFC 8785 propia sobre Jackson, probada con los vectores del RFC | TEC-10 · ronda 1b P17 | decidida |
| A-34 | Null Object en Java: interfaces selladas con variante «hay» y «no hay» (`Desfase`, `Valor` con `Motivo`); ausente y `null` del JSON dan el mismo objeto nulo | ronda 1c P18 | decidida (confirmar en revisión) |
| A-37 | `contrato` es Java puro, sin Spring; `module-info` exporta la fachada, `.dto`, `.codigos` y `.problemas`, y oculta `.interno`; una prueba de arquitectura en cada consumidor lo hace cumplir | ronda 1b P15 | decidida |
| A-44 | `dominio` no importa `contrato` | CLAUDE.md | confirmada |
| A-52 | **El dispositivo de una partición es el de la sesión `{id}` de la ruta**; la partición no lo declara. Los topes de §3 son los que esa sesión anunció al abrir. *Cómo se abre la sesión y cómo se ata al celular (si otro dispositivo puede usarla y cómo se detecta): parte 2* (ronda 1j P4, ★, resuelve COH-77 y K-89) | ronda 1i P8 (★, resuelve COH-71) | decidida |

## 3 · Variables
Apartadas el 18-oct a la semilla de configuración: `edad_vieja`, `ventana_retencion`, umbral de `hora_dudosa`, `umbral_reloj_movido` y la granularidad D1.

| Nombre | Valor | Tipo | Dueño |
|---|---|---|---|
| Forma canónica de la huella | RFC 8785 (JCS), **implementación propia sobre el árbol de Jackson** (`interno/Jcs.java`), probada con los vectores del RFC, sobre todo los números | fija | ronda 1 P8 · **ronda 1b P17 (★, resuelve K-14)** · TEC-10 «forma canónica propia» · propone cerrar la pregunta de TEC-10, que en `IA_TECNOLOGIAS-vigentes.md` sigue POR DEFINIR (actualizarla con confirmación de Juan) |
| Validador y versión de JSON Schema | **Biblioteca en ejecución, borrador 2020-12**; cuál, se elige al generar, con compatibilidad con Jackson 3 comprobada y **fila nueva en el registro con aceptación de Juan** antes de usarla | fija (biblioteca por elegir) | ronda 1b P20 (★) · TEC-11 · A-9 · A-15 |
| Generador de UUID v7 | JDK o biblioteca | pendiente | Juan · TEC-12 |
| `max_anotaciones_particion` | 200 (provisional) | configurable; el nodo de finca lo anuncia al abrir la sesión | ronda 1 P13 (★) · se ajusta con la prueba de ESC-38 |
| p95 de respuesta por partición | ≤ 2 s con 13 celulares a la vez y +60 % de carga (provisional) | fija; se mide en la prueba de ESC-38 | ronda 1b P16 (★) · ESC-38 · se ajusta con SPK-04 |
| Espera del celular por respuesta | 30 s; al vencer, reenvía la misma partición con la espera de A-40 | configurable en el celular | ronda 1b P16 (★) |
| Espera entre reintentos | `min(2^n · 1 s, 5 min) + azar(0..1 s)`; tras 8 intentos, sigue cada 5 min (el aviso al operario: semilla celular) | configurable en el celular | ronda 1c P8 (★) · A-40 |
| Plazo máximo de procesamiento | 20 s; al pasarlo, aborta con `503` | fija | ronda 1d P8 (★) · A-40 |
| Vencimiento del candado por dispositivo | 25 s | fija | ronda 1c P8 · ronda 1d P8 (★) · A-40 |
| `max_profundidad_anotacion` | 16 (provisional) | configurable; anunciado al abrir. Pasarlo dentro de una anotación → esa anotación `rechazada` `datos_invalidos`; las demás entran | ronda 1c P11 · ronda 1h P1 (★) · A-32 |
| `max_profundidad_particion` | 64 (provisional) | fija; límite duro contra JSON malicioso. Pasarlo → `422 sobre-invalido` con `pointer: /anotaciones/n` y `regla: profundidad_excedida` si el exceso está dentro de una anotación, o sin `pointer` si está fuera (anomalía) (ronda 1i P6) | ronda 1h P1 (★) · A-32 |
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
| `sesiones_captura[]` | arreglo | 0 a `max_anotaciones_particion`; al menos 1 si hay anotaciones; cada `sesion_captura_uuid` de una anotación debe estar declarado aquí; si no, `422 sobre-invalido` `sesion_no_declarada` (A-38) |
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
Casos vigentes del contrato, en orden. C-9 y C-51 se retiraron con A-28 y A-43. **Apartados el 18-oct** (texto en `ESPEC-contrato-ingesta-HISTORIA-1g-1h.md` §4): C-5, C-33, C-37, C-38, C-60, C-61, C-70, C-71, C-72 y C-81 → semilla ingreso; C-39, C-39b, C-41, C-42, C-58 y C-62 → semilla celular; C-67 y C-80 → semilla sesión.
- **C-73** Dada una partición que responde `422 sobre-invalido` con `pointer: /sesiones_captura/0/uuid` · cuando el celular la procesa · entonces no la parte: rearma el sobre desde su almacén y reenvía una vez; si falla otra vez con el mismo `particion_uuid`, retiene solo esa partición y sigue con las demás, el nodo de finca alerta al administrador (`sobre_invalido_repetido`) y no se borra nada (A-41; ronda 1i P7).
- **C-74** Dada la sesión S1 abierta por op-07 · cuando op-07 renueva su credencial (jti nuevo) y sigue entregando · entonces sigue en S1; si en el mismo celular entrega op-12, recibe `403 sesion_ajena` y abre S2 a su nombre (A-51, A-41).
- **C-75** Dada la 77 obsoleta con su constancia (huella H1) · cuando llega el mismo id con huella H2 · entonces es corrupta: `rechazada` `datos_invalidos`, alerta, y no se crea una segunda constancia (A-2, A-45).
- **C-76** Dada una partición de 200 anotaciones donde la 38 trae un `valor` con profundidad 20 · cuando llega · entonces la 38 es `rechazada` `datos_invalidos` (fallo técnico) y las otras 199 entran (A-32).
- **C-82** Dada una partición de 200 donde la n.º 38 trae un `valor` con profundidad 70 · cuando llega · entonces `422 sobre-invalido` con `pointer: /anotaciones/37` y `regla: profundidad_excedida`; el celular aísla la 38, la avisa en `descartes` con `fallo_tecnico` y reenvía las otras 199, que entran (A-32, A-41, A-47; ronda 1i P6).
- **C-77** Dada D-014 aplicada a las 14:05 (respuesta con `aplicadas: 1`) y superada a las 15:10 por D-022 · cuando D-014 reenvía · entonces recibe `sin_efecto` con `obsoleta`, el celular actualiza su acuse, y los conteos de la respuesta de las 14:05 no se reescriben (A-26, A-45).
- **C-78** Dado un reenvío con `primeros_acuses: [{uuid, primer_acuse_ms}]` donde `primer_acuse_ms` es posterior a ahora · cuando llega · entonces esa entrada se ignora y va a la bitácora; la anotación se juzga sin ella (A-45 ⟦2⟧b).
- **C-79** Dada una partición con `sesiones_captura: [S1]` y la anotación 12 con `sesion_captura_uuid: S9` · cuando llega · entonces `422 sobre-invalido` con `pointer: /sesiones_captura`, `regla: sesion_no_declarada` y `faltan: [S9]`; no entra ninguna anotación, el celular agrega S9 desde su almacén, reenvía y entran todas; nada se borra (A-38, A-41; ronda 1i P5).
- **C-1** Dada una partición nueva de 200 anotaciones válidas · cuando se entrega · entonces `200`, `conteo.aplicadas = 200` y `confirmado_hasta` = su último consecutivo (A-13, A-26).
- **C-2** Dada una partición de 200 anotaciones ya entregada · cuando se reenvía, en la misma sesión o en otra · entonces `200` con `conteo.sin_efecto = 200`, nada cambia, `excepciones[]` repite las marcadas y rechazadas con sus códigos guardados, la sesión que la recibe (la cuenta de la sesión y el sello: semilla ingreso) (A-2, A-26, ES-S04).
- **C-3** Dada una anotación ya guardada · cuando llega con el mismo id y la misma huella · entonces cuenta en `sin_efecto` y no cambia nada (A-2, A-26).
- **C-4** Dada una anotación ya guardada · cuando llega con el mismo id y otra huella · entonces `rechazada`, se registra y se alerta; nunca se sobrescribe (A-2, A-19, ES-S05).
- **C-6** Dada una anotación con `valor: null` y `motivo_ausencia: "no_disponible"` · cuando llega · entonces entra marcada como incompleta y el motivo viaja en la anotación (A-20, A-35).
- **C-7** Dado un celular que nunca sincronizó · cuando entrega con `desfase_declarado_ms: null` · entonces entra `marcada` con `desfase_desconocido` (el sello con desfase cero: semilla ingreso) (A-16, A-23).
- **C-8** Dada una partición cuyo primer consecutivo no es `confirmado_hasta + 1`, o con un hueco · cuando llega · entonces entra completa, las anotaciones tras el hueco llevan `hueco_consecutivo` y las dos marcas de agua se quedan antes del hueco: con 1-200 y 401-600 recibidas, `confirmado_hasta = 200` (A-27, A-31).
- **C-10** Dada una anotación con un campo desconocido, en `datos` o en su sobre · cuando llega · entonces `rechazada` con `datos_invalidos` (fallo técnico; qué hace el celular: semilla celular) (A-32).
- **C-11** Dada una sesión cerrada · cuando llega una partición · entonces `409 sesion-cerrada` y el celular abre otra (A-22, ES-S02).
- **C-12** Dada una sesión que no existe · cuando llega una partición · entonces `404 sesion-desconocida` (A-22).
- **C-13** Dada una partición de más de `max_anotaciones_particion` o `max_bytes_particion` · cuando llega · entonces `413 particion-muy-grande` (A-22, variables).
- **C-14** Dada una partición cuya anotación n.º 38 (índice 37; el puntero cuenta desde 0) no trae `anotacion_uuid` · cuando llega · entonces `422 sobre-invalido` con `errores[].pointer = /anotaciones/37/anotacion_uuid` y no entra ninguna (A-22, A-32).
- **C-15** Dada una credencial inválida o sin permiso · cuando llega una partición · entonces `401 credencial-invalida` o `403 sin-permiso` (A-22, A-29).
- **C-16** Dados dos arranques distintos del celular · cuando se comparan sus `mono_ms` · entonces no se interpreta como salto de reloj (A-17).
- **C-17** Dados op-07 que capturó y op-12 que entrega · cuando llega la partición · entonces las anotaciones quedan a nombre de op-07 y op-12 queda como quien entregó (A-18).
- **C-18** Dada una anotación de `tipo` `labor.siembra` que el nodo de finca no conoce · cuando llega en una partición válida · entonces `422 contrato-incompatible` con `pointer: /anotaciones/n/tipo` y `regla: tipo_desconocido`; el celular retiene esa partición sin partirla ni borrarla y sigue con las demás, y el nodo de finca alerta al administrador (ronda 1j P5) (A-32, A-41, A-50; ronda 1i P9).
- **C-19** Dada una anotación `captura.campo` cuyos `datos` no traen `seccion_id` · cuando llega · entonces `rechazada` con `datos_invalidos`; el resto entra (A-32).
- **C-20** Dada una anotación con `valor: null` sin `motivo_ausencia` · cuando llega · entonces entra `marcada` con `incompleta` y `motivo_ausente` (A-35, P-S01).
- **C-21** Dada una captura con `valor: 40` donde se esperaban 60 · cuando llega · entonces entra, y si una regla del nodo de finca lo marca, vuelve `marcada` con el id de esa regla; el celular no declara el faltante (A-23, A-35).
- **C-22** Dada la partición P1 del celular A en proceso · cuando llega P2 del mismo celular · entonces `409 particion-en-curso`, P2 no se procesa y A la reenvía después de la respuesta de P1. Al mismo tiempo, las particiones de los celulares B y C en sus sesiones S2 y S3 se procesan sin esperar (A-22).
- **C-23** Dado un nodo de finca saturado en temporada alta · cuando llega una partición · entonces `503 nodo-ocupado` con `Retry-After` y `traceId`; el celular espera esos segundos y reenvía la misma partición (A-36, A-40, ES-S14).
- **C-24** Dados 13 celulares sincronizando a la vez con +60 % de carga · cuando entregan su jornada · entonces el p95 por partición es ≤ 2 s, la jornada termina en ≤ 30 min y no hay rechazos por carga; un `503` que después entra no cuenta como rechazo (ESC-38, A-36).
- **C-25** Dada una anotación con `anotacion_uuid` versión 4 (`…-4…`) o en mayúsculas · cuando llega · entonces `422 sobre-invalido` con el `pointer` a ese campo y no entra ninguna anotación de la partición (A-32, A-38).
- **C-26** Dada una anotación con `valor: 40` y `motivo_ausencia` · cuando llega · entonces `rechazada` con `datos_invalidos` (A-32, A-35).
- **C-27** Dado un celular con `confirmado_hasta` local = 800 y un nodo de finca restaurado a un respaldo que llega al 600 · cuando el celular entrega su siguiente partición · entonces recibe `confirmado_hasta = 600`, baja su marca local a 600 y reenvía lo retenido del 601 al 800; lo que el nodo de finca ya tenía cuenta en `sin_efecto` (A-31, ES-R14, CN-15).
- **C-28** Dado D-014 con `respaldado_hasta = 5000` · cuando, tras reinstalar la app, entrega la anotación n.º 1 con un uuid nuevo · entonces entra `marcada` con `consecutivo_repetido`, hay alerta, la respuesta trae `accion: registrar_de_nuevo` y el celular **no** la borra; se registra como D-031 y la reenvía: cuenta `sin_efecto` en las marcas de D-031, vuelve con `consecutivo_resuelto` y se borra cuando `respaldado_hasta` de D-031 la cubre; el nodo de finca no vuelve a pedir `registrar_de_nuevo` a D-014 (A-39).
- **C-29** Dada una anotación con `valor: 9999` donde el campo admite 0-500 en la versión 12 · cuando llega · entonces entra `marcada` con `divergencia`; no se rechaza (A-32, ADR-014, ES-S11).
- **C-30** Dada una anotación con `version_config: 99`, que el nodo de finca no conoce · cuando llega · entonces entra `marcada` con `version_desconocida` (A-32, ES-S10).
- **C-31** Dada una copia local verificada hasta el 600 y la nube hasta el 800 para D-014 · cuando D-014 recibe una respuesta · entonces `respaldado_hasta = 600` y retiene del 601 al 800 (A-31, ES3).
- **C-32** Dadas 1-200 aplicadas, 201 `rechazada` y 202-400 aplicadas · cuando se calcula la marca · entonces `confirmado_hasta = 400` (A-27, A-31).
- **C-34** Dada una partición con `"contrato": "1.9"` y un nodo de finca que conoce la 1.2 · cuando llega · entonces `422 contrato-incompatible`; el celular la retiene sin partirla ni borrarla y avisa que hay que actualizar el nodo de finca (A-24, A-41).
- **C-35** Dada una partición con `"contrato": "2.0"` en `/v1/` · cuando llega · entonces `422 contrato-incompatible` (A-24).
- **C-36** Dada una partición 1-200 que responde `422 sobre-invalido` con `pointer: /anotaciones/136` (la n.º 137) · cuando el celular la procesa · entonces quita la 137 (la borra, la avisa en `descartes` y pide recapturar) y reenvía las otras 199, que entran (A-41, A-47).
- **C-40** Dado un nodo de finca restaurado al 600, con un respaldo hasta el 800 que pasó la prueba de restauración pero cuya copia no se pudo usar · cuando responde · entonces `confirmado_hasta = 600` y `respaldado_hasta = 600` (invariante); el celular anula sus acuses del 601 al 800 y reenvía lo que todavía retiene (el cálculo de la posible pérdida: semilla ingreso) (A-31).
- **C-43** Dada la anotación 137 aislada y borrada por fallo técnico · cuando el celular envía su siguiente partición con `descartes: [{consecutivo: 137, motivo: fallo_tecnico}]` · entonces el nodo de finca cierra el hueco 137, lo deja en la bitácora y las marcas avanzan (A-47).
- **C-44** Dada una partición que responde `422 contrato-incompatible` · cuando el celular la procesa · entonces no la parte ni la borra; la retiene y avisa qué lado actualizar (aquí el nodo de finca si el celular es más nuevo) (A-24, A-41).
- **C-45** Dada una anotación con `sesion_captura_uuid: "x"` · cuando llega en una partición válida · entonces esa anotación es `rechazada` con `datos_invalidos` (fallo técnico), y las demás entran (A-32, A-38).
- **C-46** Dado op-12 con credencial válida de otra empresa · cuando entrega una partición · entonces `403 sin-permiso` con `regla: empresa_distinta` y no se lee ninguna anotación (A-22).
- **C-47** Dada la anotación 412 que el gerente ordenó descartar · cuando el celular la borra · entonces la avisa con `descartes: [{consecutivo: 412, motivo: descartada_por_gerente}]`, el nodo de finca cierra el hueco y las marcas siguen (A-47).
- **C-48** Dado un celular que pasa `tasa_por_dispositivo` · cuando entrega otra partición · entonces `429 demasiadas-peticiones` con `Retry-After` y `traceId`; el celular espera esos segundos (A-36, §3).
- **C-49** Dado un error interno del nodo de finca · cuando procesa una partición · entonces `500 error-interno` en `problem+json` con `traceId`, sin stack ni mensaje interno; el celular reintenta con la espera de A-40 (A-36).
- **C-50** Dada una partición con el mismo `anotacion_uuid` dos veces e igual contenido · cuando llega · entonces la segunda copia cuenta `sin_efecto`; si el contenido difiere, la segunda es `rechazada` con una sola alerta (A-42).
- **C-52** Dada la rechazada 137 (fallo técnico) borrada con lápida y un nodo de finca restaurado a un respaldo anterior a ella · cuando la marca baja · entonces el celular reenvía lo retenido y `descartes: [{consecutivo: 137, motivo: fallo_tecnico}]`; la respuesta trae `descartes_acusados: [137]` y el hueco no reaparece (A-47, A-31; las lápidas: semilla celular).
- **C-53** Dada una captura de 12 tallos donde la regla R-DESV-03 espera unos 60, con `motivo_desviacion: "helada"` · cuando llega · entonces entra `marcada` con `R-DESV-03`, cuenta en `conteo.marcadas` y sale en `excepciones[]`; el gerente ve el motivo (A-49; ronda 1j P8).
- **C-54** Dada la misma captura sin `motivo_desviacion` · cuando llega · entonces entra `marcada` con `R-DESV-03` y `desviacion_sin_motivo` (A-49).
- **C-55** Dada una captura que llega sin el mínimo requerido de su actividad · cuando el nodo de finca la juzga · entonces entra `marcada` con `divergencia` y `sin_minimo`, cuenta como fallo del motor del celular y el tablero pide recapturar esa cama (A-46, A-32).
- **C-56** Dada la 412 ya aplicada (la respuesta se perdió) · cuando llega `descartes: [{412, fallo_tecnico}]` · entonces no tiene efecto, queda en la bitácora, vuelve en `descartes_acusados` y la cama no sale en la lista de camas a rehacer (A-47).
- **C-57** Dada una partición con `anotaciones: []` y `descartes: [{137, fallo_tecnico}]` · cuando llega · entonces `200`, el hueco 137 se cierra y la respuesta trae las marcas actualizadas (A-47).
- **C-59** Dada la 77 obsoleta con su constancia · cuando el celular la reenvía · entonces la constancia no se duplica, la 77 cuenta para `confirmado_hasta` y vuelve en `excepciones[]` con `sin_efecto` y `obsoleta` (A-45, A-26).
- **C-63** Dada la 137 que el celular envió con `"consecutivo": "13x"` (roto) y quedó aislada · cuando la borra · entonces avisa `descartes: [{consecutivo: 137, motivo: fallo_tecnico}]` con el número de su almacén, y el hueco 137 se cierra (A-47).
- **C-64** Dado un celular sin `registro_id` y una persona con credencial válida · cuando entrega · entonces sus anotaciones entran `marcadas` con `dispositivo_no_registrado` y el tablero lo muestra (A-23, ES-S13).
- **C-65** Dada una anotación con `desfase_declarado_ms: -1200` y un desfase medido al entregar de +2.200.000 ms · cuando llega · entonces entra `marcada` con `reloj_movido` (A-23, ADR-031 §3).
- **C-66** Dado un nodo de finca restaurado 4 días después · cuando el celular reenvía la 600, que tuvo su primer acuse 2 h después de capturarla, con `primeros_acuses: [{uuid de la 600, primer_acuse_ms}]` · entonces no se marca `vieja` (A-45 ⟦2⟧).
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
{ "type": ".../sin-permiso", "status": 403, "regla": "sesion_ajena", "title": "Sin permiso", "traceId": "…" }      // A-22: otra persona
{ "type": ".../sesion-desconocida", "status": 404, "title": "La sesión no existe", "traceId": "…" }
{ "type": ".../sesion-cerrada", "status": 409, "title": "La sesión ya se cerró", "traceId": "…" }
{ "type": ".../particion-en-curso", "status": 409, "title": "Otra partición está en curso", "traceId": "…" }   // + Retry-After
{ "type": ".../particion-muy-grande", "status": 413, "title": "La partición pasa el tope", "traceId": "…" }
{ "type": ".../contrato-incompatible", "status": 422, "title": "Versión no aceptada", "traceId": "…" }
{ "type": ".../contrato-incompatible", "status": 422, "title": "Versión no aceptada", "traceId": "…",
  "errores": [ { "pointer": "/anotaciones/12/tipo", "regla": "tipo_desconocido" } ] }   // A-32, ronda 1i P9
{ "type": ".../sobre-invalido", "status": 422, "title": "La partición no se puede leer o identificar", "traceId": "…",
  "errores": [ { "pointer": "/sesiones_captura", "regla": "sesion_no_declarada",
                 "faltan": ["0192f0a0-1000-7000-8000-00000000c009"] } ] }               // A-38, ronda 1i P5
{ "type": ".../sobre-invalido", "status": 422, "title": "La partición no se puede leer o identificar", "traceId": "…",
  "errores": [ { "pointer": "/anotaciones/37", "regla": "profundidad_excedida" } ] }    // A-32, ronda 1i P6
{ "type": ".../demasiadas-peticiones", "status": 429, "title": "Demasiadas peticiones", "traceId": "…" }  // + Retry-After
{ "type": ".../error-interno", "status": 500, "title": "Error interno", "traceId": "…" }
{ "type": ".../nodo-ocupado", "status": 503, "title": "Nodo de finca ocupado", "traceId": "…" }          // + Retry-After
```

**Catálogo de `regla` (lista abierta; agregar una es compatible).** `403`: `ingesta_entregar`, `empresa_distinta`, `sesion_ajena`. `422 sobre-invalido`: `anotacion_uuid_obligatorio`, `sesion_no_declarada` (con `faltan[]`), `json_ilegible`, `profundidad_excedida`, y la que se fije para `contrato` ausente o mal escrito (A-24). `422 contrato-incompatible`: `tipo_desconocido`. En los `422`, `regla` y `pointer` van dentro de `errores[]`; en los `403`, `regla` va en la raíz (redacción del 18-oct, COH-80).

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
| A-31 retención *(semilla celular, no se prueba aquí)* | el celular borra hasta `respaldado_hasta` (0 en el ejemplo dorado: no borra nada) | borra hasta `confirmado_hasta` |
| A-35 motivo | `"valor":null,"motivo_ausencia":"no_disponible"` entra marcada `incompleta`; sin motivo entra marcada `incompleta` + `motivo_ausente` | `"valor":40` con motivo → `datos_invalidos`; rechazar un `null` sin motivo |
| A-23 excepciones | una `marcada` vuelve con `codigos: ["incompleta"]`, solo ids cortos | `codigos: ["Falta el valor de tallos"]` (texto) |
| A-23 reenvío | el reenvío repite en `excepciones[]` la marcada con su código guardado | reenvío con `excepciones: []` |
| A-26 invariante | `recibidas 200 = aplicadas 190 + rechazadas 3 + sin_efecto 7`, con `marcadas 12` dentro de `aplicadas` | `aplicadas 178 + marcadas 12 + …` sumadas por separado |
| A-34 Null Object | `switch (desfase) { case Conocido c -> …; case Desconocido d -> … }` | `if (desfase == null)` o un campo `Long` nulo en el DTO |
| A-38 UUID | `0192f0a0-2000-7000-8000-00000000d001` | `6f1c…-4…` (v4) o en mayúsculas → `422` |
| A-39 registro nuevo | D-031 reenvía la n.º 1 → `sin_efecto` + `consecutivo_resuelto` (el borrado: semilla celular) | queda retenida para siempre, o el nodo de finca vuelve a pedir `registrar_de_nuevo` |
| A-40 plazos | procesamiento 20 s < candado 25 s < espera del celular 30 s | candado 30 s = espera 30 s |
| A-42 duplicado | `[{uuid:a1}, {uuid:a1 igual}]` → la segunda cuenta `sin_efecto`; si difiere, la segunda es `rechazada` (deduplicar alertas: semilla ingreso) | la segunda copia distinta se guarda o cuenta `sin_efecto` |
| A-45 obsoleta | la captura tardía de una orden ya cumplida cuenta `sin_efecto` + `obsoleta` y deja una línea en la bitácora | se guarda como un segundo hecho, o desaparece sin rastro |
| A-45 ⟦2⟧ `vieja` | edad hasta la primera llegada mayor que el umbral → entra `marcada` con `vieja` (umbral: semilla configuración; retención: semilla celular) | una anotación vieja → `rechazada` |
| A-49 inmutable | editar la cama 12 ya enviada crea una anotación nueva (uuid nuevo) | editar la cama 12 enviada con el mismo uuid → corrupción falsa |
| A-46/A-48 fallo técnico *(semilla celular, no se prueba aquí)* | `rechazada` `datos_invalidos` → borra el contenido, guarda lápida `{137, fallo_tecnico}` y pide recapturar | la retiene completa para siempre, o la borra sin lápida |
| A-47 descarte | `descartes: [{consecutivo: 137, motivo: "fallo_tecnico"}]` sin datos del campo | el hueco 137 congela las marcas hasta que alguien lo cierre a mano |
| A-16 tiempo (ajustado en la ronda 1j P7, ★) | `{"crudo_ms": 1792130531000, "mono_ms": 8123400, "arranque": 41, "desfase_declarado_ms": -1200}`, o sin `desfase_declarado_ms` (= `null` → `desfase_desconocido`, A-33) | `"desfase_declarado_ms": "-1200"` (texto), o `crudo_ms` corregido por el celular |
| A-18 sesión de captura | `{"uuid", "persona_id": "op-07", "credencial_jti", "inicio", "version_app", "version_motor"}` sin dispositivo | la sesión de captura con `dispositivo`, o sin `persona_id` |
| A-41 según el error | `403` → no reintenta y avisa; `413` → parte en dos | reintenta un `403` cada 5 min; parte un `contrato-incompatible` |
| A-51 credencial *(semilla sesión, no se prueba aquí; falta el par de «quien entrega»)* | credencial vencida en campo → sigue capturando, marca `capturada_sin_credencial` | el celular bloquea la captura hasta volver a la oficina |
| A-1 identidad | `anotacion_uuid` UUID v7 generado en el celular; nada se ordena por él | ordenar o filtrar por el tiempo que lleva dentro el UUID |
| A-2 mismo id, misma huella (ronda 1i P1, ★) | `d001` (H1) guardada → llega `d001` (H1) → `sin_efecto`, no cambia nada | → `aplicada` otra vez, o se guarda un segundo registro |
| A-2 mismo id, otra huella (ronda 1i P1, ★) | `d001` (H1) guardada → llega `d001` (H2) → `rechazada` `datos_invalidos`; H1 queda intacta | → se sobrescribe con H2, o cuenta `sin_efecto` |
| A-2 id nuevo (ronda 1i P1, ★) | `d001` guardada → llega `d002` con la misma clave del hecho → entra | → `sin_efecto` por «parecerse» a `d001` |
| A-13 éxito (ronda 1i P2, ★) | 200 anotaciones con 1 rechazada → `200`, `Cache-Control: no-store`, `conteo.rechazadas = 1` | → `207 Multi-Status` o `201`, o un `200` sin `Cache-Control: no-store` |
| A-22 error de partición (ronda 1i P2, ★) | credencial de otra empresa → `403` `problem+json` con `regla: empresa_distinta`; 0 anotaciones leídas ni guardadas | → `200` con todas rechazadas, un `403` después de guardar alguna, o un `403` sin `regla` |
| A-19 huella (ronda 1i P3, ★) | el nodo de finca calcula `SHA-256(JCS(…))` de la anotación recibida entera; dos envíos iguales dan la misma huella | la anotación trae `"huella": "ab12…"` y se usa (es un campo desconocido: `rechazada` `datos_invalidos`, A-32), o la huella se calcula solo sobre `datos` |
| A-20 solo sección en v1 (ronda 1i P3, ★) | `datos` = {`produccion_id`, `seccion_id: "B3-C17-S1"`, `campo_id`, `jornada`, `valor`} | `datos` con `cama_id` o `"nivel": "cama"` en `/v1/` se acepta (tiene que ser `rechazada` `datos_invalidos`: campo desconocido) |
| A-30 un celular por partición (ronda 1i P4, ★) | las 200 anotaciones de la sesión S1, todas de D-014 | la misma partición trae anotaciones de D-014 y de D-022 (cómo se sabe el dispositivo: COH-71) |
| A-44 dominio sin contrato (ronda 1i P4, ★) | `dominio/Cama.java` sin ningún `import co.florlogic.contrato..` | `import co.florlogic.contrato.dto.Anotacion` en `dominio` (lo detecta ArchUnit) |
| A-46 `sin_minimo` (ronda 1i P4, ★) | llega una captura sin el mínimo de su actividad → `marcada` con `divergencia` y `sin_minimo` | → `rechazada`, o `aplicada` sin marca |
| A-49 ⟦4⟧ motivo de desviación (ronda 1i P4, ★) | `"motivo_desviacion": "helada"` (id del catálogo) | `"motivo_desviacion": "Heló anoche"` (texto libre → `rechazada` `datos_invalidos`) |
| A-51 quien entrega (ronda 1i P4, ★) | capturada por op-07 y entregada con la credencial de op-12 → autor op-07, entrega op-12 | el autor pasa a ser op-12, o `403` por entregar lo de otro |
| A-38 sesión no declarada (ronda 1i P5, ★) | `sesiones_captura: [S1]` y una anotación con `S9` → `422` `pointer: /sesiones_captura`, `regla: sesion_no_declarada`, `faltan: [S9]`; el celular rearma y no borra | → la anotación `rechazada` y borrada, o `200` aceptándola sin autor |
| A-32 fallo de lectura ubicable (ronda 1i P6, ★) | la n.º 38 con profundidad 70 → `422` con `pointer: /anotaciones/37`, `regla: profundidad_excedida`; las otras 199 entran al reenviar | → `422` sin `pointer` y las 200 retenidas |
| A-41 segundo `422` sin pointer (ronda 1i P7, ★) | P3 falla dos veces con el mismo `particion_uuid` → se retiene solo P3, P4 sigue, alerta `sobre_invalido_repetido` | → se detiene todo el celular, se rearma con un `particion_uuid` nuevo, o no alerta nadie |
| A-32 tipo desconocido (ronda 1i P9, ★) | `"tipo": "labor.siembra"` desconocido → `422 contrato-incompatible`, `pointer: /anotaciones/n/tipo`, `regla: tipo_desconocido`; el celular retiene sin borrar | → `rechazada` y el celular la borra, o entra `marcada` |
| A-22 orden de comprobación (ronda 1i P10, ★) | partición de 2 MiB en una sesión cerrada → `409 sesion-cerrada` (la sesión va antes que el tamaño) | → `413`, o una respuesta distinta en cada intento |
| A-22 tres fases de los `422` (ronda 1j P1, ★) | `"contrato": "1.x"` y un `tipo` desconocido en la 5 → `422 sobre-invalido` `pointer: /contrato` (fase 1 gana) | → `422 contrato-incompatible` `tipo_desconocido`, o una respuesta distinta en cada intento |
| A-41 `json_ilegible` es una pista (ronda 1j P2, ★) | `422` `json_ilegible` `pointer: /anotaciones/38`; la 39 se lee bien con los bytes del celular → rearma el sobre, no borra nada | → el celular borra la 39 sin comprobarla |
| A-41 `particion_uuid` tras un `422` (ronda 1j P3, ★) | quitar la 38 → partición nueva `b004`; rearmar `b005` → mismo `b005`; reenvío idéntico de `b005` tras perder la respuesta → no alerta | → reenviar sin la 38 con el mismo `b003` y disparar `sobre_invalido_repetido` en falso |
| A-41 alcance de retener (ronda 1j P5, ★) | `contrato` desconocido o `403 empresa_distinta` → el celular deja de enviar; `tipo_desconocido` en P3 → retiene P3 y envía P4 | → sigue enviando tras un `403`, o un `tipo_desconocido` en una anotación frena todo el celular |
| A-32 tipo mal escrito (ronda 1j P6, ★) | `"tipo": "Captura Campo"` o sin `tipo` → esa anotación `rechazada` `datos_invalidos`; las demás entran | → `422 contrato-incompatible` `tipo_desconocido` |
| A-49 ⟦4⟧ desviación con motivo (ronda 1j P8, ★) | 12 tallos donde se esperan 60, con `motivo_desviacion: "helada"` → `marcada` `["R-DESV-03"]`, cuenta en `marcadas` | → `aplicada` sin marca y fuera de `excepciones[]` |
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
| Códigos | `codigos/CodigoIngreso.java` (`hora_dudosa`, `divergencia`, `hueco_consecutivo`, `datos_invalidos`, `incompleta`, `motivo_ausente`, `consecutivo_repetido`, `consecutivo_resuelto`, `version_desconocida`, `vieja`, `obsoleta`, `desfase_desconocido`, `capturada_sin_credencial`, `desviacion_sin_motivo`, `sin_minimo`, `orden_duplicada`, `dispositivo_no_registrado`, `reloj_movido`, `descarte_sin_orden`, `fuera_de_asignacion`, …) | A-23, A-27, A-32, A-45, A-46, A-49 |
| Errores de la partición | `problemas/TipoProblema.java` (catálogo de `type`) | A-22 |
| Esquemas | `src/main/resources/esquemas/ingesta/v1/particion.schema.json`, `anotacion.schema.json`, `tipos/captura.campo.v1.schema.json`, `resultado-particion.schema.json` | A-9, A-15, A-22 |
| Huella | `HuellaCanonica.java` (fachada, paquete raíz; RFC 8785 + SHA-256 sobre el árbol recibido sin nulls) | A-10, A-19, A-33 |
| Validación | `ValidadorContrato.java` (fachada: frontera 422 / anotación) + `interno/ValidadorEsquema.java` (biblioteca, 2020-12) | A-9, A-32, A-38 |
| Módulo | `src/main/java/module-info.java` + prueba de arquitectura en cada consumidor | A-37, A-44 |
| Identidad | `dto/Anotacion.java` (`anotacionUuid` como `UUID`) + `interno/ReglasUuid.java` y el `pattern` del esquema | A-1, A-38 |
| Tiempo | `dto/Tiempo.java` (`crudo_ms`, `mono_ms`, `arranque`, `desfase`) | A-16, A-17 |
| Respuesta y cabeceras | `dto/ResultadoParticion.java` (con `recibido_ms` y `descartes_acusados`), `dto/Excepcion.java`, `Cabeceras.java` (`TRACEPARENT`, `NO_STORE`, `RETRY_AFTER`) | A-13, A-14, A-26, A-29 |
| Problema | `problemas/Problema.java` (`type`, `title`, `status`, `traceId`, `regla`, `errores[]` con `pointer`, `regla` y, si aplica, `faltan[]`); los ids de regla van en `snake_case` | A-22, A-36 |
| Null Object | `dto/Desfase.java`, `dto/Valor.java` (con `Motivo`): interfaces selladas | A-16, A-34, A-35 |
| Forma canónica | `interno/Jcs.java` (RFC 8785 propia) + `JcsVectoresTest` | A-10, A-33, K-14 |
| Límites | `interno/LimitesJson.java` (topes de tamaño y profundidad, `FAIL_ON_UNKNOWN_PROPERTIES`, sin tipos polimórficos) | A-9, A-32 |
| Pruebas | `ContratoVersionTest` (falla si el esquema cambia sin subir versión), `HuellaCanonicaTest`, pruebas de ejemplos dorados | A-9, A-19 |

**Fuera del borde** (regla D-07 de la skill: cuenta como trazado). Desde la poda del 18-oct, cada destino tiene su semilla en `IA_apartado-2026-10-18/semillas/`:

| Lógica | Semilla |
|---|---|
| Idempotencia (orden de juicio, cuarentena, RF-022), clave del hecho, obsoleta y superada, `hora_dudosa`, `vieja`, juicio con el catálogo, sello, marcas de agua y huecos | `IA_SEMILLA-caso-de-uso-ingreso.md` |
| Sesión de sincronización, credencial, celular compartido, registro de dispositivo | `IA_SEMILLA-endpoint-sesion-y-credencial.md` (ronda 2) |
| Inserción atómica, índice único, cuarentena, `statement_timeout` | `IA_SEMILLA-adaptador-persistencia-ingreso.md` |
| Mínimo, edición local, retención, lápidas, reintentos, aviso escalado | `IA_SEMILLA-caso-de-uso-celular.md` (simulador) |
| Ejes de versión, mínimo por actividad, plazos y umbrales | `IA_SEMILLA-configuracion-catalogo-y-versiones.md` |
| Lote, sección, cama, turnos, orden de captura, D1 | `IA_SEMILLA-entidad-agregado-produccion-y-asignacion.md` |
| `respaldado_hasta`, prueba de restauración | `IA_SEMILLA-trabajo-programado-respaldo.md` (TX-2) |
| Cómo lee el celular una respuesta más nueva (A-43) | despliegue y actualización (ADR-015, ADR-016) |

## 6 · Contradicciones y pendientes abiertos
Solo lo abierto **del contrato**. Lo resuelto está en las dos HISTORIAS. En la poda del 18-oct salieron a sus semillas o a las preguntas para Juan: COH-5, COH-26, COH-27, COH-42, COH-43, COH-44, COH-52, COH-61, COH-62, COH-64, COH-66, COH-67, K-2, K-4, K-28, K-40, K-41, K-59, K-75 y K-82 (destino en `IA_apartado-2026-10-18/IA_00-INDICE.md` §3).

| Id | Qué | Tipo | Dueño | Estado |
|---|---|---|---|---|
| COH-63 | Un `sesion_captura_uuid` **bien escrito pero no declarado** (C-79) es un defecto del sobre, que arma el celular, pero se trata como fallo técnico de la anotación y se borra un dato bueno. Si está mal escrito (C-45), el defecto está en los bytes inmutables de la anotación y rearmar el sobre no lo arregla. El mismo defecto da dos resultados: `sesiones_captura: []` → 422 (§3b) sin pérdida, y `[S1]` con anotaciones que apuntan a S9 → 200 con rechazadas. Por A-2, un reenvío con el sobre corregido vuelve `sin_efecto` y repite `rechazada`, así que el dato no se recupera. Propuesta: `422` con `pointer` a `/sesiones_captura` y rearmar el sobre (A-41), sin borrar. Lo decidió Juan en la ronda 1h P4: no se reabre como bloqueante, se anota el riesgo frente a CN-15 (revisión del 18-oct) | confiabilidad (mayor) · A-38, A-41 · CN-15 (−15) | ronda 1i | **cerrada 18-oct** (ronda 1i P5: `422` al sobre con `sesion_no_declarada`; ver A-38) |
| K-80 | Un `tipo` o `version_tipo` desconocido da `rechazada` y el celular lo borra. A-50 dice que las versiones desconocidas son anomalía, pero distingue: `contrato` → 422 y se retiene; `version_config`, `version_app` y `version_motor` → `marcada`. `tipo` y `version_tipo` no están entre los ejes de versión. Decidir si son eje del contrato (422) o `marcada` (redacción del 18-oct) | ambigüedad (−5) · A-32, A-50 | ronda 1i | **cerrada 18-oct** (ronda 1i P9: eje de versión → `422 contrato-incompatible` con `pointer`) |
| K-81 | Frontera sin definir para: campos mal formados de `sesiones_captura[]` que no son el uuid (`persona_id`, `inicio`, `version_*`); un uuid repetido en `sesiones_captura[]` con otro contenido; entradas mal formadas de `descartes[]` y `primeros_acuses[]`; un descarte y una anotación del mismo consecutivo en la misma partición, o una anotación que llega después de su descarte; `descartes` o `primeros_acuses` que pasan su tope (¿413 o 422?); un `primer_acuse_ms` de un uuid que el nodo de finca ya tiene (debe mandar el del nodo de finca, o el celular evita `vieja`); `anotaciones: []` sin descartes. Los descartes repetidos entre particiones ya los cubre A-47 | ambigüedad (−5) · A-32, A-45 ⟦2⟧b, A-47 | ronda 1i | abierta |
| K-83 | El catálogo de A-22 no lista `429 demasiadas-peticiones`, `500 error-interno` ni `503 nodo-ocupado`, que sí usan A-36, §4b, C-23, C-48 y C-49; tampoco dice qué `type` lleva el `503` por plazo vencido (A-40). = COH-72 | ambigüedad (−5) · A-22, A-36, A-40 | ronda 1i | **cerrada 18-oct** (ronda 1i P10: catálogo completo y orden fijo en A-22) |
| K-84 | El par de A-16 pone «`desfase_declarado_ms` ausente» como violación, pero A-33 y A-34 dicen que ausente = `null`. Decidir si el campo es obligatorio y admite `null`. Si se lee como choque directo, resta −25 y bloquea | ambigüedad (−5) · A-16, A-33, A-34 | ronda 1i | **cerrada 18-oct** (ronda 1j P7: ausente = `null`; par de A-16 corregido) |
| K-85 | C-53 dice que la captura con motivo «puede» marcarse con `R-DESV-03`: no fija si se marca y si cuenta en `marcadas` | ambigüedad (−5) · A-49 ⟦4⟧, A-26 | ronda 1i | **cerrada 18-oct** (ronda 1j P8: siempre `marcada` con la regla; A-49 ⟦4⟧, C-53) |
| K-86 | §5 lista el código `descarte_sin_orden`, pero no se define en ninguna parte (resto de A-45 ⟦3⟧, apartada a la semilla celular) | ambigüedad (−5) · A-23, §5 | ronda 1i | abierta |
| K-87 | A-12 está «confirmada» con fuente IA_TX1 pasos 7-14, pero IA_TX1 §4 dice `/ingesta/sesiones/{id}/lotes`, sin `v1` y con «lotes»; P-S05 sigue `POR ACEPTAR`; además IA_TX1 pone `POST /ingesta/archivos` fuera de `sesiones/{id}` | cita (−5) · A-12, A-24, A-30 | Juan · ronda 1i | abierta |
| K-88 | A-41 dice «`422 sobre-invalido` con `pointer` → quitar esa anotación», y `/sesiones_captura` también es un `pointer`: leída al pie de la letra, manda borrar. Escribir «con `pointer` a `/anotaciones/n`». = COH-79 | ambigüedad (−5) · A-41, C-73, C-79 | redacción | **cerrada 18-oct** (redacción, con permiso de Juan) |
| K-89 | Para responder «otro dispositivo → `403 sesion_ajena`» (A-52), la petición tiene que identificar el dispositivo, pero la partición no lo declara y A-29 no tiene cabecera para eso. ¿Va en un claim del JWS o en otro sitio? = COH-77 | ambigüedad (−5) · A-52, A-29 | ronda 1j · parte 2 | **cerrada 18-oct** (ronda 1j P4: el dispositivo se ata en la parte 2; `sesion_ajena` después de encontrar la sesión) |
| K-90 | Con `contrato-incompatible`, A-41 dice «retener», pero no si es solo esa partición o todo el celular; con `tipo_desconocido` con `pointer`, una anotación retiene 199 buenas. = COH-78 | ambigüedad (−5) · A-41, A-50, C-18 | ronda 1j | **cerrada 18-oct** (ronda 1j P5: el alcance depende de la causa; A-41) |
| K-91 | Un `tipo` mal escrito (no cumple `^[a-z]+(\.[a-z_]+)+$`) o ausente: ¿forma inválida (`rechazada`) o `tipo_desconocido` (422)? Falta la distinción que A-38 hace para `sesion_captura_uuid` | ambigüedad (−5) · A-32, §3b | ronda 1j | **cerrada 18-oct** (ronda 1j P6: mal escrito o ausente → `rechazada`; A-32) |
| K-92 | El orden de A-22 pone todo `contrato-incompatible` antes de todo `sobre-invalido`, pero hay `sobre-invalido` que van antes por fuerza (JSON roto, profundidad, `contrato` ausente o mal escrito según A-24), y un tipo desconocido en la 150 no se ve si la 10 es ilegible. = COH-74 | ambigüedad (−5) · A-22, A-24, A-32 | ronda 1j | **cerrada 18-oct** (ronda 1j P1: los `422` en tres fases, A-22) |
| K-93 | Si el rearmado sale **idéntico** al envío anterior (el sobre estaba bien y el defecto persiste), el segundo `422` cuenta como reintento y el nodo de finca nunca alerta; el celular retiene esa partición sin que nadie se entere. Decidir quién alerta en ese caso | confiabilidad · ambigüedad (−5) · A-41 (ronda 1j P3) | ronda 1j o siguiente | abierta |
| COH-74 | **Orden de comprobación.** `"contrato":"1.x"` + un tipo desconocido: A-24 responde `422 sobre-invalido /contrato` y A-22 responde `422 tipo_desconocido` (contradicción literal en un caso límite). Propuesta: ⟦1⟧ paso del `contrato` (falta o mal escrito → `sobre-invalido`; desconocido → `contrato-incompatible`) ⟦2⟧ lectura y esquema del sobre ⟦3⟧ anotaciones en el orden del arreglo, responde la primera que falla · Costo: ninguno | cohesión (mayor) · A-22, A-24, A-32, A-38 | ronda 1j | **cerrada 18-oct** (ronda 1j P1: los `422` en tres fases, A-22) |
| COH-75 | **El `pointer` puede caer en la vecina.** Una `}` de más en la 38 cierra el objeto antes y el error aparece en `/anotaciones/38` (la 39, buena); el celular la borra, reenvía y cae la siguiente: pérdida en cascada. Propuesta: con `json_ilegible`, el celular comprueba con sus bytes que la señalada no se lee; si se lee, la trata como defecto del sobre · Costo: CPU en el celular | confiabilidad (mayor) · A-32, A-41, C-82 · CN-15 | ronda 1j | **cerrada 18-oct** (ronda 1j P2: el celular confirma con sus bytes; A-41) |
| COH-76 | Tras un `422` con `pointer`, no se dice si el reenvío sin la anotación conserva el `particion_uuid`; si lo conserva y sale otra mala, el nodo de finca alerta `sobre_invalido_repetido` en falso (igual si se perdió la respuesta). No se dice si `errores[]` trae todas las anotaciones ubicables o solo la primera. Propuesta: `errores[]` lleva todas y el celular las quita de una vez; la alerta solo cuenta `422` sin `pointer` a una anotación; fijar el `particion_uuid` del reenvío · Costo: memoria en el lector | cohesión (mayor) · A-41, A-29, C-36, C-82 | ronda 1j | **cerrada 18-oct** (ronda 1j P3; queda K-93) |
| COH-77 | **Dispositivo sin identificar** (= K-89). Además, A-22 pone el `403` antes del `404`: con una sesión que no existe no se sabe de quién es, así que `sesion_ajena` solo se puede evaluar después de encontrar la sesión. Propuesta: pendiente de la parte 2 (cómo se ata la sesión al dispositivo) y aclarar el orden · Costo: ninguno | cohesión (mayor) · A-52, A-51, A-29, A-22 · IA_TX1 paso 5, ES-S13 | ronda 1j · parte 2 | **cerrada 18-oct** (ronda 1j P4: el dispositivo se ata en la parte 2; `sesion_ajena` después de encontrar la sesión) |
| COH-78 | **Cuánto se retiene con `contrato-incompatible` y con `403`** (= K-90). COH-69 proponía «se detiene todo», pero la P7 solo resolvió `sobre-invalido`; el diccionario («Anomalía») y C-68 dicen «retiene todo». El `pointer` de `contrato-incompatible` no cambia lo que hace el celular: solo le sirve al administrador. Propuesta: definir el alcance y corregir el diccionario · Costo: si sigue con las demás, el hueco congela las marcas | confiabilidad (mayor) · A-41, C-18, C-68 · CN-15 | ronda 1j | **cerrada 18-oct** (ronda 1j P5: el alcance depende de la causa; A-41) |
| COH-79 | Redacción de A-41 (= K-88). «Sobre» tiene dos sentidos sin entrada en el diccionario: el de la anotación (A-15, C-10 → `rechazada`) y el de la partición (A-32, A-38 → `422`). «Anotación aislada» incluye el `413`, que parte y no aísla. Propuesta: corregir la redacción y crear entradas para «sobre de la partición», «rearmar» y «retener» · Costo: ninguno | coherencia (menor) · A-41, A-15, A-32 · diccionario | redacción | **cerrada 18-oct** (redacción, con permiso de Juan) |
| COH-80 | Faltan errores dorados para `403 sesion_ajena`, `sesion_no_declarada` con `faltan[]` y `tipo_desconocido` con `pointer`; el título de `contrato-incompatible` no sirve para un tipo desconocido; no se dice si su `pointer` va en `errores[]`; no hay catálogo de los ids de `regla` del `422` · Costo: ninguno | coherencia (menor) · §4b, A-22, §5 · EP-06, EP-18 | redacción · ronda 1j | **cerrada 18-oct** (redacción, con permiso de Juan) |
| COH-68 | **Una anotación tumba 199 buenas.** Si la 38 trae `valor` con profundidad 70 (mayor que `max_profundidad_particion`) o bytes que no parsean, la respuesta es `422` sin `pointer`; el celular rearma con los mismos bytes, falla igual y retiene las 199 hasta actualizar la app. Con profundidad 20 (C-76) solo cae la 38. Propuesta: si el exceso o el error de sintaxis cae dentro de `/anotaciones/n`, `422` con `pointer` a esa anotación (el lector en flujo conoce la ruta), y el celular valida cada anotación antes de rearmar · Costo: el lector lleva la ruta (poca CPU) | confiabilidad (mayor) · A-32, A-41, A-33 · CN-15, ESC-38 | ronda 1i | **cerrada 18-oct** (ronda 1i P6: `422` con `pointer` a la anotación; ver A-32, C-82) |
| COH-69 | Tras el segundo `422` sin `pointer`, «retiene todo y se alerta al administrador» no dice si se retiene solo esa partición o todo lo pendiente, si siguen las demás ni quién alerta. El nodo de finca no sabe que es el segundo intento, porque `particion_uuid` solo correlaciona, y no se dice si el rearmado lo conserva. Propuesta: `422 sobre-invalido` → se retiene esa partición y se sigue con las demás; `contrato-incompatible` y `403` → se detiene todo; el nodo de finca alerta cuando el mismo `particion_uuid` recibe `422` dos veces · Costo: un hueco que congela las marcas mientras tanto | cohesión (mayor) · A-41, A-29, C-73, C-68 · CN-15 | ronda 1i | **cerrada 18-oct** (ronda 1i P7: se retiene esa partición y se sigue; alerta el nodo de finca; ver A-41) |
| COH-70 | **Citas rotas por la poda.** Casos y pares citan decisiones apartadas o reglas que ya no dicen eso: C-79, C-10, C-36 y C-45 citan A-46 para borrar con lápida (A-46 ahora solo dice `sin_minimo`); C-6 y C-20 citan A-8; C-7 y la fila «Tiempo» de §5 citan A-6; C-10, C-40 y C-52 citan A-48; C-22 cita A-7; C-47 cita A-45 (⟦3⟧ apartada); C-2 afirma el sello de la primera sesión; los pares «A-46/A-48», «A-31 retención», «A-45 antigüedad», «A-39 registro nuevo», «A-42 duplicado» (alerta) y «A-51 credencial» prueban lógica apartada; A-1 cita K-41 y A-31 cita COH-5 sin decir dónde quedaron; §3 «aviso al operario» es de la semilla celular; Ficha y §6 listaban 7 pares en lugar de 10; §3 dice que cierra la pregunta de TEC-10, pero `IA_TECNOLOGIAS-vigentes.md` sigue POR DEFINIR. Propuesta: volver a citar A-41 o A-47, o marcar «(semilla X, no se prueba aquí)» · Costo: ninguno | cohesión (mayor) · poda del 18-oct | agente, con permiso de Juan | **cerrada 18-oct** (citas y vocabulario reparados con permiso de Juan; la «Definición acordada» conserva «servidor» porque es texto acordado) |
| COH-71 | El `409` por dispositivo, el par (dispositivo, consecutivo) de A-39, `tasa_por_dispositivo`, C-64 y los topes «anunciados al abrir» dependen de la sesión de sincronización, que salió entera (A-5). La Ficha dice «4 POST», pero la semilla de sesión se presenta como dos detallamientos aparte. Propuesta: fijar en la Ficha que `POST /sesiones` es la parte 2 de esta ESPEC y agregar a §2 «el dispositivo de la partición es el de la sesión `{id}`» · Costo: ninguno | cohesión (mayor) · A-18, A-22, A-39, A-40, §3 | Juan · ronda 1i | **cerrada 18-oct** (ronda 1i P8: la sesión es la parte 2 de esta ESPEC; nueva A-52) |
| COH-72 | A-22 no lista `429`, `500` ni `503` (= K-83). Tampoco hay un orden fijo entre las comprobaciones: una partición grande en una sesión cerrada puede dar `413` o `409`, y el celular reacciona distinto a cada uno. Propuesta: completar A-22 y fijar el orden 401 → 403 → 404/409 sesión → 429 → 409 en curso → 413 → 422 · Costo: ninguno | coherencia (menor) · A-22, A-24, A-36, A-41 · EP-06, EP-14 | ronda 1i | **cerrada 18-oct** (ronda 1i P10: catálogo completo y orden fijo en A-22) |
| COH-73 | **Vocabulario.** La ESPEC usa «servidor» como sinónimo de «nodo de finca», y «servidor» no tiene entrada en el diccionario. Una superada vuelve con el código `obsoleta` (no existe `superada`). En el diccionario, «Sesión de sincronización», «Consecutivo», «Lápida» y «Acuse» citan decisiones apartadas, «Anotación aislada» tiene la frase rota y «Constancia» no menciona el uuid ni la huella. Propuesta: usar siempre «nodo de finca» y volver a citar las fuentes · Costo: ninguno | coherencia (menor) · diccionario | agente, con permiso de Juan | **cerrada 18-oct** (citas y vocabulario reparados con permiso de Juan; la «Definición acordada» conserva «servidor» porque es texto acordado) |

**Pares cumple/viola:** los 10 que faltaban se cerraron en la ronda 1i P1-P4 (18-oct): A-2, A-13, A-19, A-20, A-22, A-30, A-44, A-46, A-49 ⟦4⟧ y A-51 (quien entrega).

## 7 · Fuera del dominio (para otro canal)
Todo lo de otro elemento está en `IA_apartado-2026-10-18/IA_00-INDICE.md`, con su semilla o su documento en revisión.
- **Antes de programar:** TX-1.K, P-S04 y P-S05 siguen `POR ACEPTAR` (CLAUDE.md: parar y preguntar a Juan).

## 8 · Plan de segmentos
(etapa 4)

## 9 · Matriz requisito → código → prueba
(etapa 4)

## 10 · Observaciones de revisión
(etapa 5)

## 11 · Informe de verificación
(etapa 6)
