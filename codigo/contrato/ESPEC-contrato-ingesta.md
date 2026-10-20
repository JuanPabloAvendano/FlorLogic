# ESPEC · contrato de ingesta (TX-1.K)

REGISTRO DE IA · NO ES FUENTE — lo genera la skill DetallarParaCodificar a pedido de Juan, 16-oct-2026. Podada el 18-oct-2026 con la skill information-pairment.
Manda por encima: `ADR.xlsx` · `docs/03-arquitectura/decisiones/` · `IA_00-LEEME-transacciones.md`.

## Ficha
| Campo | Valor |
|---|---|
| Definición acordada | Acuerdo entre el celular y el servidor de la finca para entregar lo capturado en una jornada: qué lleva cada mensaje, qué responde el servidor, cómo se reconoce un envío repetido y cómo cambia de versión sin romper a nadie. No es la lógica que decide si una anotación vale, ni la base de datos, ni el código que recibe la petición (acordada 16-oct, 2.ª vuelta) |
| Diccionario | `codigo/IA_DICCIONARIO.md` (compartido por todos los detallamientos) |
| Historia | `ESPEC-contrato-ingesta-HISTORIA.md` (rondas 1 a 1f) · `ESPEC-contrato-ingesta-HISTORIA-1g-1h.md` (rondas 1g y 1h, copia íntegra antes de la primera poda) · `ESPEC-contrato-ingesta-HISTORIA-1i-1k.md` (rondas 1i a 1k, copia íntegra antes de la segunda poda) |
| Apartado | `IA_apartado-2026-10-18/IA_00-INDICE.md`: lo que no es del contrato, con su destino. Primera poda (§1-§4) y **segunda poda (§5)**, las dos aprobadas por Juan el 18-oct |
| Elemento | Módulo `codigo/contrato/`: los mensajes de los 4 POST de ingesta de TX-1; **parte 1** (esta): `POST …/particiones` · **parte 2** (ronda 2, en esta misma ESPEC): `POST /sesiones`, `…/cierre` y `…/archivos` (ronda 1i P8, resuelve COH-71) |
| Plantilla | `endpoint` (solo §1 ⟦1⟧-⟦10⟧; ⟦11⟧ frontera y controladores quedan fuera) |
| Dominio | Entra: forma de petición y respuesta de los 4 POST, versión, errores, huella · Sale: DTO como records, JSON Schema, `HuellaCanonica` · Fuera: controladores, puertos, reglas, base de datos |
| Generalidad | Contrato campo por campo |
| Para qué | Especificar para programar |
| Estado | **LISTA PARA CÓDIGO (forzada)** · decisión de Juan del 18-oct: «definir ya de manera forzada el 85 %», generalizando los escenarios específicos en reglas amplias (A-53 ⟦3⟧, A-54, A-55, A-56). **Desbloqueada el 19-oct:** Juan aceptó TX-1.K (esta forma), P-S04 y P-S05; K-87 cerrada |
| **Etapa actual** | 6 Verificación · Segmento 2 verificado (65/65, coherencia pasa, completitud 100 %); **aceptación pendiente de la explicación de Juan**. Canal traspasado a sus semillas (§12, D-19) |
| **Dónde retomar** | 20-oct: ver `codigo/IA_RETOMAR-AQUI.md`. Juan sigue en un chat nuevo con el **dominio de ingesta** (semilla caso-de-uso-ingreso, que arranca desde el canal, §12). Pendiente aquí: la explicación de Juan del Segmento 2 y, después, los Segmentos 3 a 7 y la ronda 2 del contrato |
| Precisión | **85 % forzado** por decisión de Juan (18-oct), sobre el último índice oficial de **75,2 %** (verificador, ronda 1k: cobertura 89,5 · contradicciones 30 · prácticas 92,2 · trazabilidad 88,9). Las contradicciones N1-N5 del verificador se repararon como redacción en la segunda poda; su proyección era ≈89 %. **Coherencia (D-04):** el bloqueante COH-88 se cerró con la regla general A-54; tras la segunda poda se lanzó una pasada acotada de coherencia (D-10), que solo busca contradicciones literales y no casos nuevos, por el pedido de Juan de no dar más vueltas. Encontró 5 contradicciones y un roce, ninguno bloqueante, y se repararon como redacción (A-29, A-56, A-23, A-2, A-22 ⟦2⟧ y el par de A-12); la revisión completa sigue siendo obligatoria en la etapa 6. Historia: 52 · 59 · 68 · 60 · 60 · 62 · 62 · 63 · 64,5 · poda · 74,8 · 78,0 · 80,4 · 75,2 · poda · 85 forzado |
| Completitud | Segmento 1: — · Explicación — |
| Avance por etapa | Tamaño ✔ · Consulta ✔ · Preguntas ✔ (rondas 1 a 1k; dos podas el 18-oct; 85 % forzado) · Generación: Segmento 1 de 6 · Revisión, Verificación y Aceptación: Segmento 1 (forzada, 19-oct) · Verificación — · Aceptación — |

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
| A-2 | Resultado de la idempotencia por anotación: mismo id y misma huella → `sin_efecto`; mismo id y otra huella → **corrupta**: `rechazada` con `datos_invalidos`, nunca se sobrescribe; es fallo técnico (A-48, semilla celular); otro id → entra y su resultado lo decide el ingreso. El id es único entre anotaciones y constancias. *Orden de juicio, cuarentena y RF-022: semilla ingreso* | rondas 1, 1g P6 (elección de Juan), 1g P10 | decidida |
| A-3 | El contrato garantiza que viajan los datos de la clave del hecho (por defecto producción · sección · campo · jornada); la clave la declara el catálogo y la calcula el ingreso. No incluye autor, dispositivo ni versión de configuración | ronda 1d P2 | decidida |
| A-19 | La huella es `SHA-256` de la anotación entera en forma canónica; el nodo de finca la recalcula siempre y nunca usa una que mande el celular | ronda 1 P7 | decidida |
| A-33 | `huella = SHA-256(JCS(quitarNulls(árbol JSON recibido)))`, con versión de algoritmo (`{"alg": "h1", "sha256": …}`); solo se comparan huellas de la misma versión. `quitarNulls` quita solo miembros de objeto que valen `null`, nunca elementos de arreglo, **en cualquier nivel, también dentro de `valor`**. Un miembro `null` vale lo mismo que uno ausente. Se calcula sobre el JSON recibido, nunca sobre el DTO. **El esquema valida el mismo árbol sin nulls que se usa para la huella** (recibido → `quitarNulls` → huella y esquema → juicio), en la anotación y en el sobre de la partición: ningún esquema declara `"type": "null"` ni exige un miembro que puede valer `null`; `datos.valor` ausente es el dato ausente (A-35). Misma huella, mismo resultado (ronda 1k P10, ★, resuelve K-98). **Todo reenviador** (celular, buzón del gerente, nodo de contingencia) reenvía los bytes originales, sin volver a serializar | rondas 1b, 1c, 1d | decidida |
| A-38 | Los ids que genera el celular son UUID v7 en minúsculas (`^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$`). Si falla uno que identifica (`anotacion_uuid`, `particion_uuid`, `sesiones_captura[].uuid`) → `422 sobre-invalido`; si `sesion_captura_uuid` está mal escrito → solo esa anotación, `rechazada` `datos_invalidos` (el defecto está en sus bytes, que son inmutables). Si está **bien escrito pero no está declarado en `sesiones_captura[]`**, el defecto es del sobre: `422 sobre-invalido` con `pointer: /sesiones_captura`, `regla: sesion_no_declarada` y `faltan[]` con los uuid que faltan; el celular rearma el sobre desde su almacén (A-41) y no borra nada (ronda 1i P5, ★, resuelve COH-63; reemplaza la ronda 1h P4) | rondas 1b, 1d | decidida |
| A-42 | El mismo id dos veces en una partición se procesa en el orden del arreglo: la segunda copia es `sin_efecto` si es igual, o corrupta si es distinta. *Deduplicar alertas: semilla ingreso* | ronda 1c P16 | decidida |
| A-49 | ⟦3⟧ **Inmutable desde que sale:** lo que ya viajó, aunque no tenga respuesta, no se edita; cambiarlo es una **recaptura** con id nuevo, así que un mismo id con otra huella siempre es corrupción. ⟦4⟧ `datos.motivo_desviacion` (id del catálogo) cuando una regla pide motivo por una desviación brusca; **siempre que la regla detecta la desviación, la anotación entra `marcada` con el id de la regla**, traiga motivo o no; si no hay motivo, se agrega `desviacion_sin_motivo` (ronda 1j P8, ★, resuelve K-85). *⟦1⟧ ⟦2⟧ ⟦5⟧ ⟦6⟧: decisiones «corrige solo el gerente» y «ADR-027 deprecado», semilla celular* | rondas 1e P4-P5, 1f P1 | decidida |

### La petición
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-12 | `POST /ingesta/v1/sesiones/{id}/particiones`: el celular entrega una partición de su almacén de salida; la llama una persona con credencial JWS. Las otras rutas (parte 2): `POST /ingesta/v1/sesiones`, `…/{id}/cierre` y `POST /ingesta/v1/archivos`, fuera de la sesión | IA_TX1 pasos 7-14 · ADR-007 · P-S05 (ACEPTADO 19-oct, cierra K-87) | decidida |
| A-29 | Cabeceras: `Authorization: Bearer <JWS>`, `Content-Type: application/json`, `traceparent` opcional. No hay `Idempotency-Key`: `particion_uuid` solo correlaciona; se conserva al rearmar el sobre o al reintentar, y solo cambia si cambian las anotaciones que lleva: al partir por un `413`, cada mitad lleva uno nuevo; rearmar el sobre lo conserva aunque cambien los bytes (A-41; segunda poda 18-oct, resuelve COH-95). Los logs llevan `sesion_id`, `particion_uuid` y `traceId`, sin datos de negocio | ronda 1 P19 · ADR-026 | decidida |
| A-30 | *Partición*: un trozo, de hasta `max_anotaciones_particion`, de lo pendiente de **un** celular, que sube en un POST; nunca mezcla celulares. Todos los celulares sincronizan en paralelo. (*Lote* es negocio: diccionario y semilla entidad) | rondas 1b, 1c | decidida |
| A-15 | La anotación es un sobre genérico: `anotacion_uuid`, `consecutivo`, `tipo`, `version_tipo`, `sesion_captura_uuid`, `tiempo`, `version_config`, `veredicto_local` y `datos`. Hoy solo existe `captura.campo`; cada tipo valida sus `datos` con su esquema | ronda 1 P2 | decidida |
| A-16 | `tiempo`: `crudo_ms` (reloj de pared, nunca se toca), `mono_ms` (reloj monótono), `arranque` y `desfase_declarado_ms` (el de la última sincronización; `null` o ausente si nunca sincronizó, que valen lo mismo (A-33) → la respuesta trae `desfase_desconocido`; ronda 1j P7) | ADR-031 §2 · SPK-12 | decidida |
| A-17 | `arranque` sube en cada encendido; `mono_ms` solo es comparable dentro del mismo arranque. El contrato garantiza los datos; el cálculo de `hora_dudosa` es del ingreso | SPK-12 M2b · IA_TX1 paso 11 | decidida |
| A-18 | La partición declara sus `sesiones_captura[]`: `uuid`, `persona_id` (quien capturó), `credencial_jti` vigente al capturar, `inicio`, `version_app` y `version_motor` (las de al capturar; actualizar la app abre una sesión de captura nueva). Cada anotación la referencia por `sesion_captura_uuid`. El dispositivo va una vez en la sesión de sincronización (ronda 2). La credencial de la petición es **quien entrega** | rondas 1, 1b, 1c · ADR-029 · ES-S12 | decidida |
| A-20 | `datos` de `captura.campo`: `produccion_id`, `seccion_id`, `campo_id`, `jornada` (`AAAA-MM-DD`, día natural en la zona horaria de la finca; P-S04 ACEPTADO 19-oct), `valor` (JSON libre o `null`, validado por el catálogo de su versión), `motivo_ausencia` si falta el valor y `motivo_desviacion` si la regla lo pide. En v1 solo se anota a nivel de **sección**; otro nivel exige una versión mayor (A-24) | rondas 1, 1c, 1d · ADR-024 · P-S04 (19-oct) | decidida |
| A-21 | `veredicto_local` es `aceptada` o `marcada`; lo que el celular rechaza no viaja (ADR-024, ESC-02) | ronda 1c P4 | decidida |
| A-24 | La versión mayor va en la ruta (`/v1/`), la menor en la partición (`"contrato": "1.2"`). Una menor solo agrega campos opcionales que no cambian el sentido de los demás. El nodo de finca solo acepta versiones que conoce; lo demás es `422 contrato-incompatible`. **Orden de validación:** primero la lectura del documento (A-22 ⟦0⟧), después la versión de `contrato`, después el esquema. Un `contrato` **mal escrito** (p. ej. `"1.x"`) no es una versión: `422 sobre-invalido` con `pointer: /contrato` (ronda 1h P5, resuelve K-63) | rondas 1, 1c, 1d, 1e | decidida |
| A-27 | El orden de consecutivos no se impide, se marca: tras un hueco las anotaciones llevan `hueco_consecutivo`; los solapes los resuelve la idempotencia | ronda 1 P17 | decidida |
| A-47 | **Aviso de descarte:** el celular avisa en `descartes[]` (opcional; mismo tope que `anotaciones`) cada consecutivo que borró sin entregar, **con el número de su almacén**: `{consecutivo, motivo}`, `motivo` = `fallo_tecnico` o `descartada_por_gerente`. El nodo de finca cierra el hueco y lo deja en la bitácora. Idempotente: un aviso repetido, o de algo que ya llegó, no tiene efecto. **Todo aviso vuelve en `descartes_acusados`.** Una partición puede traer solo descartes | rondas 1d, 1e, 1f | decidida |
| A-45 ⟦2⟧b | `primeros_acuses[]` (opcional): `{anotacion_uuid, primer_acuse_ms}` de lo que se reenvía tras una restauración; va **fuera** de la anotación, que es inmutable. **`primer_acuse_ms` es el reloj del nodo de finca**: cada respuesta trae `recibido_ms` y el celular lo devuelve tal cual. Una entrada imposible (futura, anterior al sello, o de un uuid que no viene en la partición) se ignora y va a la bitácora. Tope: `max_anotaciones_particion` (ronda 1h P3, resuelve COH-39 y K-60) | ronda 1f P8 | decidida |

### Resultados y códigos que devuelve el nodo de finca
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-14 | Una anotación mala no tumba la partición: hay un resultado por anotación | IA_TX1 paso 14 | confirmada |
| A-32 | **Frontera.** `422 sobre-invalido` (toda la partición) solo si no se puede leer o identificar, o trae algo que no está en el contrato. **Si el fallo de lectura cae dentro de `/anotaciones/n`** (JSON ilegible o profundidad mayor que `max_profundidad_particion`), el `422` lleva `pointer: /anotaciones/n` y `regla` `json_ilegible` o `profundidad_excedida`, y el `pointer` le sirve al administrador: el celular no quita nada, rearma (A-41, ronda 1k P3); sin `pointer` solo queda lo que no se puede ubicar (ronda 1i P6, ★, resuelve COH-68). Casos: JSON roto, **campo desconocido en el sobre de la partición**, profundidad de toda la partición mayor que `max_profundidad_particion` (límite duro; es anomalía), falta `contrato`/`particion_uuid`/`anotaciones[]`, una anotación sin `anotacion_uuid` o `consecutivo` válidos, o un `sesion_captura_uuid` que no está declarado en `sesiones_captura[]` (A-38). **Por anotación:** forma inválida, campo desconocido, topes de anotación pasados (incluida la profundidad dentro de una anotación, `max_profundidad_anotacion` — ronda 1h P1, resuelve K-73), motivo mal escrito → **fallo técnico**: `rechazada` `datos_invalidos`. **`tipo` o `version_tipo` bien escritos pero desconocidos** son un eje de versión, no un dato malo: `422 contrato-incompatible` con `pointer` a la anotación y `regla: tipo_desconocido` (ronda 1i P9, ★, resuelve K-80). Un `tipo` mal escrito (no cumple §3b) o ausente, o un `version_tipo` que no es entero o falta, es forma inválida: `rechazada` `datos_invalidos` (ronda 1j P6, ★, resuelve K-91). Lo que se juzga con el catálogo o las reglas (valor fuera de rango, motivo que no está en el catálogo, desviación) → `marcada` con **el id de la regla** (`R-…`); y, si los motores del celular y del nodo de finca juzgan distinto, también `divergencia` (ronda 1k P9, ★, resuelve K-97; cuándo exactamente, `sin_minimo` y el celular más estricto: semilla ingreso). `version_config` desconocida → `marcada` `version_desconocida`. *Cómo se juzga y qué se recaptura: semilla ingreso* | rondas 1b, 1c, 1d, 1e, 1h · ADR-014 · ES-S10, ES-S11 | decidida |
| A-35 | **Dato ausente:** `valor: null` con `motivo_ausencia` → `marcada` `incompleta`; sin motivo → `marcada` `incompleta` + `motivo_ausente`; valor **con** motivo → fallo técnico. **Producto faltante:** hay cifra pero menor a lo esperado; lo decide una regla del nodo de finca y vuelve como id de regla en los códigos. Los motivos viven en el catálogo de configuración | rondas 1b, 1c · P-S01 · CN-36 | decidida |
| A-46 | **`sin_minimo`:** una captura sin el mínimo requerido de su actividad que llega (falló el motor del celular) vuelve `marcada` con `divergencia` y `sin_minimo`. *El flujo de la jornada y el destino en el celular: semilla celular* | rondas 1d P5, 1e P6, 1f P6 · ADR-024 · P-S01 | decidida |
| A-45 ⟦1⟧ | **`obsoleta`:** cuando el ingreso decide que una captura llegó tarde para una orden de captura ya cumplida, la anotación cuenta `sin_efecto` con `obsoleta`; queda una **constancia** con su uuid y su huella, que cuenta como recibida para las marcas y en un reenvío vuelve igual en `excepciones[]`. Códigos relacionados que puede devolver: `orden_duplicada` y `fuera_de_asignacion`. *Cuándo aplica, cómo se deduce la orden y la «superada»: semilla ingreso* | rondas 1e P3, 1f P2-P4, 1g, 1h · ADR-026 | decidida |
| A-45 ⟦2⟧ | **`vieja`:** código que el ingreso pone según la edad medida hasta la primera llegada (`primeros_acuses[]`, A-45 ⟦2⟧b). *Umbral y regla: semillas ingreso y configuración* | rondas 1d P3, 1f P8 | decidida |
| A-23 | `excepciones[]` lleva cada anotación con resultado o códigos que mostrar: `rechazada`, `marcada` y `sin_efecto` con `obsoleta`. Cada una: `anotacion_uuid`, `resultado`, `codigos` (ids cortos, sin textos: id de regla del catálogo `R-…` o código del ingreso en `snake_case`, §3b). La marca sirve para que el gerente de producción haga las correcciones. En un reenvío repite el resultado y los códigos guardados, o `sin_efecto` con `obsoleta` si después la superó otra captura (A-45 ⟦1⟧, C-77), y los conteos la cuentan en `sin_efecto`. Códigos del ingreso: §5; entre ellos `capturada_sin_credencial` (el `crudo_ms` es posterior al vencimiento de la credencial de la sesión de captura, ES-S12), `reloj_movido` (el desfase declarado al capturar y el medido al entregar difieren más que el umbral, ADR-031 §3) y `dispositivo_no_registrado` (ES-S13) | rondas 1, 1c, 1d, 1f | decidida |
| A-39 | **Consecutivo reiniciado.** El mismo (dispositivo, consecutivo) con otro id → `marcada` `consecutivo_repetido` y `"accion": "registrar_de_nuevo"` (una sola vez por dispositivo). Cuando el dispositivo nuevo la reenvía, vuelve `consecutivo_resuelto`. *El registro del dispositivo: semilla sesión* | rondas 1c, 1d | decidida |
| A-50 | **Versiones desconocidas son anomalía.** `422 contrato-incompatible` y `version_desconocida` no ocurren en operación normal, porque el nodo de finca crea todas las versiones (decisión «nada desconocido entra a la finca»); si aparecen, se alerta al administrador. Una `version_app` o `version_motor` desconocida también: la anotación entra `marcada` `version_desconocida` (ronda 1h P9, COH-60). Un `tipo` o `version_tipo` desconocido → `422 contrato-incompatible` con `pointer` (A-32; ronda 1i P9) | ronda 1f P5, 1g P7-P8 · nota de Juan | decidida |

### La respuesta y los errores
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-13 | Éxito: `200` (no hay recurso nuevo con URL propia), síncrono, con `Cache-Control: no-store` | ronda 1b P14 | confirmada |
| A-26 | Respuesta `200`: `particion_uuid`, `confirmado_hasta`, `respaldado_hasta`, `conteo` {`recibidas`, `aplicadas`, `marcadas` ⊆ `aplicadas`, `rechazadas`, `sin_efecto` (ya guardadas u obsoletas)} con `recibidas = aplicadas + rechazadas + sin_efecto`, `excepciones[]`, `recibido_ms` (reloj del nodo de finca, A-45 ⟦2⟧b), `descartes_acusados[]` (**opcional**: solo si hubo descartes) y, solo si hace falta, `accion`. **Los conteos son la foto del momento** de esa respuesta y no se reescriben después (ronda 1h P2, resuelve COH-38). No se guardan respuestas: un reenvío se procesa de nuevo (el de una superada devuelve `sin_efecto` + `obsoleta`). *El sello de la primera sesión: semilla ingreso* | rondas 1, 1b | decidida |
| A-22 | Errores de toda la partición en `application/problem+json` con `regla`: `401 credencial-invalida`, `403 sin-permiso` (`ingesta_entregar`, `empresa_distinta`, `sesion_ajena`; se comprueba antes de leer anotaciones), `404 sesion-desconocida`, `409 sesion-cerrada`, `409 particion-en-curso` (una a la vez **por dispositivo**), `413 particion-muy-grande`, `422 sobre-invalido` con `errores[].pointer`, `422 contrato-incompatible`, `429 demasiadas-peticiones`, `500 error-interno` y `503 nodo-ocupado` (también cuando vence el plazo de procesamiento, A-40). Agregar un `type` es compatible. **Orden de comprobación** (responde la primera que falla): 401 → 403 `ingesta_entregar` / `empresa_distinta` → 404 / 409 `sesion-cerrada` → 403 `sesion_ajena` (solo se evalúa con la sesión ya encontrada; ronda 1j P4) → 429 → 409 `particion-en-curso` → 413 por bytes (`max_bytes_particion`, antes de leer el JSON) → 422; `500` y `503` pueden salir en cualquier momento (ronda 1i P10, ★, resuelve K-83 y COH-72). El `413` por cantidad de anotaciones (`max_anotaciones_particion`) exige leer el arreglo, así que se comprueba en la fase ⟦2⟧ (redacción del 18-oct, COH-86). **Los `422`, en una lectura y tres fases** (ronda 1j P1, ★, resuelve COH-74 y K-92): ⟦0⟧ **lectura completa** del documento con el lector en flujo, sintaxis y `max_profundidad_particion`: si falla → `sobre-invalido` `json_ilegible` o `profundidad_excedida`, con `pointer: /anotaciones/n` si el error cae dentro de una anotación y sin `pointer` si cae fuera (una `}` de más en el sobre); un cuerpo que termina antes de tiempo es `cuerpo_incompleto` (A-54); las fases siguientes solo corren sobre un documento ya leído (ronda 1k P8, ★, resuelve K-94). ⟦1⟧ el campo `contrato`: falta → `sobre-invalido` `contrato_obligatorio`; mal escrito → `sobre-invalido` `contrato_mal_escrito`, los dos con `pointer: /contrato`; desconocido → `contrato-incompatible`. ⟦2⟧ esquema del sobre de la partición (las entradas de `descartes[]` y `primeros_acuses[]` se juzgan una por una, A-55, no con este esquema; campos obligatorios, forma de `sesiones_captura[]`, cantidad de anotaciones → `413`) → `sobre-invalido`. ⟦3⟧ las anotaciones en el orden del arreglo: la primera que falla de forma ubicable decide el `type`, y `errores[]` lleva todas las del mismo `type` que se alcanzaron a leer (ronda 1j P3) (`anotacion_uuid` o `consecutivo` ausentes o inválidos → `sobre-invalido` con `anotacion_uuid_obligatorio`, `anotacion_uuid_invalido`, `consecutivo_obligatorio` o `consecutivo_invalido` (C-14, C-25, C-63); `tipo` desconocido → `contrato-incompatible`; `sesion_captura_uuid` no declarado → `sobre-invalido` `sesion_no_declarada`). Un fallo de forma o un campo desconocido en una anotación no corta: es `rechazada` y se sigue con las demás | rondas 1, 1b, 1d | decidida |
| A-36 | Todo error lleva `traceId`; `429`, `503` y `409 particion-en-curso` traen `Retry-After`; `500` nunca expone detalles internos; ningún error lleva datos de negocio | ronda 1b P13 | decidida |
| A-41 | **Qué hace el celular según la respuesta** (la otra mitad de cada error; el cómo por dentro: semilla celular). `413` → partir; cada mitad lleva `particion_uuid` nuevo (A-29). `422 sobre-invalido` `cuerpo_incompleto` → reintentar, como un `5xx` (A-54). Cualquier otro `422 sobre-invalido` es anomalía (A-53): **nunca se quita ni se borra nada por el `pointer`**, que solo le sirve al administrador; se rearma el sobre una vez, con el mismo `particion_uuid`, y se reenvía; si vuelve a fallar, se retiene **solo esa partición** y se sigue con las demás, y el nodo de finca alerta `sobre_invalido_repetido` (A-56; cuándo exactamente: semilla ingreso). **Riesgo anotado** (elecciones de Juan en la 1k P4-P6, CN-15): el rearmado sale normalmente idéntico, así que la alerta del nodo de finca casi nunca salta; el aviso de esa retención es el local del celular, y mientras tanto el hueco congela las marcas de agua y el borrado (ES-N10). `422 contrato-incompatible` → no partir ni borrar; por el `contrato` se detiene todo el celular y avisa qué lado actualizar; por `tipo_desconocido` se retiene solo esa partición y se sigue con las demás. `401` → renovar la credencial y reintentar. `403 ingesta_entregar` o `empresa_distinta` → se detiene la pareja (persona, credencial), no el celular; se reanuda con otra credencial de esa persona o cuando entrega otra persona (A-51). `403 sesion_ajena` → abrir otra sesión a su nombre. `404` y `409 sesion-cerrada` → abrir otra sesión. `5xx`, `429`, `409 particion-en-curso` → reintentar; la espera es la mayor entre `Retry-After` y la espera creciente. Nada se borra por estos errores | rondas 1c-1j · 1k P3 (★), P4-P6 (Juan), P7 (★) · segunda poda 18-oct | decidida |
| A-40 | ⟦2⟧ Plazos: procesar ≤ 20 s (si no, `503`) < candado por dispositivo 25 s < espera del celular 30 s. ⟦3⟧ `409 particion-en-curso` trae `Retry-After`. ⟦4⟧ Espera creciente `min(2^n · 1 s, 5 min) + azar(0..1 s)`. *⟦1⟧ una sesión por celular: semilla sesión · ⟦5⟧ inserción atómica: semilla persistencia* | rondas 1c P8, 1d P8 · ESC-38 | decidida |

### Marcas de agua
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-31 | `confirmado_hasta`: mayor consecutivo recibido sin huecos (rechazadas y constancias cuentan como recibidas). `respaldado_hasta`: mayor consecutivo dentro de un respaldo verificado y presente en las dos copias; lo calcula TX-2. Invariante `respaldado_hasta ≤ confirmado_hasta`. Las marcas pueden **bajar** tras una restauración: el celular anula los acuses por encima y reenvía desde ahí. Viajan en cada respuesta de partición (y al abrir la sesión: ronda 2, COH-5 en la semilla de sesión). *Cálculo, alerta de pérdida, cierre de huecos y retención: semillas ingreso, respaldo y celular* | rondas 1b-1g · P-S03 · TX-2 · ES3 | decidida |

### Transversales
| Id | Regla vigente | Fuente | Estado |
|---|---|---|---|
| A-51 | La credencial de la petición es **quien entrega** (A-18). Ante `401` el celular renueva y reintenta, sin borrar nada; `403 sesion_ajena` cuando entrega una persona distinta de la que abrió la sesión (el dispositivo: parte 2, A-52). *Login local, acceso en campo, renovación y celular compartido: semilla sesión* | ronda 1f P9, 1g P9, 1h P6 · ES-S12 | decidida |
| A-9 | DTO como `record`, JSON Schema 2020-12 versionado, prueba que falla si el contrato cambia sin subir versión. Jackson 3 sin tipos polimórficos. **Dos lectores** (19-oct, OBS-1-5): `LectorDto.estricto()` para el nodo de finca que lee peticiones, con `FAIL_ON_UNKNOWN_PROPERTIES` y `FAIL_ON_NULL_FOR_PRIMITIVES` (un campo desconocido → `Fallida` `campo_desconocido` con `pointer`); `LectorDto.tolerante()` para el celular que lee respuestas, que ignora los campos de más (*Microservices Patterns* · p. 100). Jackson 3 trae `FAIL_ON_UNKNOWN_PROPERTIES` apagado: nunca se lee una petición con un mapper sin configurar | TEC-10, TEC-11 · ronda 1e P8 · Juan 19-oct | decidida |
| A-10 | Forma canónica RFC 8785 propia sobre Jackson, probada con los vectores del RFC | TEC-10 · ronda 1b P17 | decidida |
| A-34 | Null Object en Java: interfaces selladas con variante «hay» y «no hay» (`Desfase`; `Valor` con `Hay`, `Falta` con su `Motivo` y, desde el 19-oct, `Contradictorio`, A-57); ausente y `null` del JSON dan el mismo objeto nulo | ronda 1c P18 · A-57 | decidida |
| A-37 | `contrato` es Java puro, sin Spring. **Sin `module-info`** (decisión de Juan, 19-oct: con Java 27 exigía forzar ASM en el compilador): `.interno` se oculta solo con la prueba de arquitectura de cada consumidor, que prohíbe depender de `co.florlogic.contrato.interno..`; la API pública es la fachada, `.lectura` (barrera de lectura, A-57), `.dto`, `.dto.tipos`, `.codigos` y `.problemas`. Paquetes por tipo técnico, sin versión (Juan, 19-oct: se mantiene la forma de §5; solo la mecánica de lectura sale de la raíz) | ronda 1b P15 · Juan 19-oct (Segmento 1) | decidida |
| A-44 | `dominio` no importa `contrato` | CLAUDE.md | confirmada |
| A-53 | **Un solo validador, una sola medida.** El módulo `contrato` ofrece `ValidadorContrato`, el mismo para el simulador y el nodo de finca (A-37), con `FAIL_ON_TRAILING_TOKENS` activado de forma explícita. ⟦1⟧ El celular no envía nada que ese validador rechace: una anotación que no se lee, o cuyo id, consecutivo o forma no valen, no viaja (fallo técnico; se avisa en `descartes[]`, A-47); un defecto del sobre se corrige antes de enviar. ⟦2⟧ Por eso todo `422 sobre-invalido` que no sea `cuerpo_incompleto` (A-54) es anomalía; el nodo de finca sigue validando todo. ⟦3⟧ Los dos lados validan el árbol sin nulls (A-33), con los topes que anunció la sesión vigente (A-52), y cada tope se mide igual en los dos: la profundidad de una anotación desde la anotación (`max_profundidad_anotacion`), la de la partición desde la raíz (`max_profundidad_particion`). *Cómo arma el celular (generador, bytes crudos, lápidas, retención): semilla celular* | ronda 1k P1-P2 (★) · ⟦3⟧ segunda poda 18-oct (regla general, Juan; resuelve COH-91, K-107, K-108) | decidida |
| A-54 | **Lo que puede ser transporte se reintenta.** Un cuerpo incompleto (el documento termina antes de tiempo, o llegan menos bytes que `Content-Length`) es `422 sobre-invalido` con `regla: cuerpo_incompleto`, sin `pointer`. No es anomalía: el celular lo trata como un `5xx` (reintenta la misma partición y no gasta el rearmado) y no cuenta para la alerta. Solo un documento completo y mal formado es `json_ilegible` | segunda poda 18-oct (regla general, Juan; resuelve COH-88 y K-106) | decidida |
| A-55 | **Una entrada mala solo afecta a lo suyo** (frontera general). Lo que impide leer o identificar la partición → `422`. Una entrada identificable mala solo cae ella: la anotación queda `rechazada`; un descarte o un primer acuse mal formado o contradictorio se ignora y va a la bitácora (como A-45 ⟦2⟧b). Una entrada mal formada de `sesiones_captura[]`, o un uuid repetido allí con otro contenido, es defecto del sobre → `422 sobre-invalido`. Pasar el tope de cualquier arreglo es `413`. Si un descarte y una anotación traen el mismo consecutivo, gana la anotación (A-47: un descarte de algo que ya llegó no tiene efecto) | segunda poda 18-oct (regla general, Juan; resuelve K-81) | decidida |
| A-56 | **Quien ve la anomalía la avisa, una vez, y solo retiene lo suyo.** El nodo de finca alerta lo que ve, una vez por clave (`particion_uuid` o, si no se lee, la huella del cuerpo); el celular avisa en pantalla lo que solo él ve; ninguno repite un aviso ya dado. Ninguna anomalía retiene más que su partición, salvo un `contrato` desconocido (todo el celular) o un `403` (la pareja persona-credencial, A-41). *Claves y deduplicación: semilla ingreso · avisos y reintentos del celular: semilla celular* | segunda poda 18-oct (regla general, Juan; resuelve COH-96, base de K-105, K-110 y COH-94 en sus semillas) | decidida |
| A-52 | **El dispositivo de una partición es el de la sesión `{id}` de la ruta**; la partición no lo declara. Los topes de §3 son los que esa sesión anunció al abrir. *Cómo se abre la sesión y cómo se ata al celular (si otro dispositivo puede usarla y cómo se detecta): parte 2* (ronda 1j P4, ★, resuelve COH-77 y K-89) | ronda 1i P8 (★, resuelve COH-71) | decidida |
| A-57 | **Ninguna pieza del contrato lanza ante una entrada; lo imprevisto es un valor con nombre.** Leer pasa por una sola barrera (`LectorDto` → `Lectura` = `Leida` o `Fallida` con `pointer` y clase, sin datos de negocio, A-36), que atrapa también los defectos de programación (no los `Error` de la JVM). Los catálogos que viajan como texto leen un valor nuevo como `DESCONOCIDO` (lector tolerante, enums extensibles: EP-16). Los constructores de los DTO no lanzan: las listas descartan `null`, y las invariantes de la respuesta se nombran en `incoherencias()` (patrón Notification). Un dato contradictorio es `Valor.Contradictorio` (Special Case). **No contradice A-50:** tolerar al leer no es aceptar. **Desviación consciente de la ley de Postel** (*Principles and Patterns…* · p. 123) en la petición: el nodo de finca no es liberal con lo que acepta, porque él crea todas las versiones y un campo desconocido es anomalía (A-9: lector estricto); el celular sí lo es con las respuestas (lector tolerante); el validador sigue rechazando lo desconocido, pero solo lo suyo (A-55). El nodo de finca no envía una respuesta con incoherencias; el celular que recibe una no borra nada por ella y sigue | Juan, 19-oct (revisión del Segmento 1, OBS-1-4) · *PoEAA* secc. 118 (Special Case) · *Principles and Patterns for Distributed Application Architecture* p. 123 (Postel, tolerant reader) · *.NET Microservices Architecture* p. 240 (Notification) · *POSA 4* p. 495 (Null Object) | decidida |
| A-58 | **El nodo de finca valida y lee por piezas** (COH-99): nunca lee la partición entera como `ParticionRequest`. Primero valida y lee el sobre sin anotaciones (falla → `422 sobre-invalido`, A-32); después cada `anotaciones[n]` por separado, con su esquema y `LectorDto.estricto()`: `Leida` va al ingreso, `Fallida` deja solo esa anotación `rechazada` `datos_invalidos` (A-55). El celular sigue armando el `ParticionRequest` completo. Lo programa el Segmento 6 | Juan, 19-oct (verificación del Segmento 1) | decidida |
| A-59 | **El nodo de finca guarda el JSON equivalente de cada anotación, no sus bytes** (`jsonb`). Juan, 20-oct, al revisar COH-104: «borremos la necesidad de guardar tal como llegó, no me hace sentido». La huella no lo necesita: A-33 la calcula sobre la forma canónica del árbol (JCS), no sobre los bytes; reenviar los bytes originales sigue siendo obligación de los **reenviadores** (celular, buzón, contingencia), no del nodo. Consecuencia aceptada: el orden de las claves, los espacios y la forma de los números no se conservan. Lo que `jsonb` no puede guardar se rechaza con `422` antes de la base (COH-105) | verificación del Segmento 2, 20-oct | decidida |

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
| `contrato` | texto | obligatorio (`"contrato": null` = ausente → `contrato_obligatorio`, A-33); `^\d+\.\d{1,3}$`; se valida **antes** que el esquema: mayor distinta de la ruta o menor desconocida → `422 contrato-incompatible` (A-24) |
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
| `tiempo.desfase_declarado_ms` | entero | entero con signo; ausente o `null` = desconocido (A-16, A-33) |
| `veredicto_local` | texto | `aceptada` o `marcada` |
| `datos.produccion_id`, `seccion_id`, `campo_id` | texto | `[A-Za-z0-9_-]{1,40}` (supuesto) |
| `datos.jornada` | texto | fecha `AAAA-MM-DD` (P-S04, aceptado 19-oct; la zona horaria es de la configuración, no del contrato) |
| `datos.valor` | JSON | hasta `max_bytes_valor`; ausente o `null` = dato ausente (A-33, A-35) |
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
Casos vigentes del contrato, en orden. C-9 y C-51 se retiraron con A-28 y A-43. **Apartados el 18-oct** (texto en `ESPEC-contrato-ingesta-HISTORIA-1g-1h.md` §4): C-5, C-33, C-37, C-38, C-60, C-61, C-70, C-71, C-72 y C-81 → semilla ingreso; C-39, C-39b, C-41, C-42, C-58 y C-62 → semilla celular; C-67 y C-80 → semilla sesión. **Segunda poda (18-oct):** C-83 y C-85 → semilla celular (cómo arma y valida el celular; texto en `ESPEC-contrato-ingesta-HISTORIA-1i-1k.md` §4).
- **C-73** Dada una partición que responde `422 sobre-invalido` con `pointer: /sesiones_captura/0/uuid` · cuando el celular la procesa · entonces no la parte: rearma el sobre desde su almacén y reenvía una vez; si falla otra vez con el mismo `particion_uuid`, retiene solo esa partición y sigue con las demás; el nodo de finca alerta `sobre_invalido_repetido` solo si el segundo cuerpo es distinto, y si no queda el aviso local del celular; no se borra nada (A-41, A-56; rondas 1i P7, 1k P5).
- **C-74** Dada la sesión S1 abierta por op-07 · cuando op-07 renueva su credencial (jti nuevo) y sigue entregando · entonces sigue en S1; si en el mismo celular entrega op-12, recibe `403 sesion_ajena` y abre S2 a su nombre (A-51, A-41).
- **C-75** Dada la 77 obsoleta con su constancia (huella H1) · cuando llega el mismo id con huella H2 · entonces es corrupta: `rechazada` `datos_invalidos`, alerta, y no se crea una segunda constancia (A-2, A-45).
- **C-76** Dada una partición de 200 anotaciones donde la 38 trae un `valor` con profundidad 20 · cuando llega · entonces la 38 es `rechazada` `datos_invalidos` (fallo técnico) y las otras 199 entran (A-32).
- **C-82** Dada una partición de 200 donde la n.º 38 trae un `valor` con profundidad 70 · cuando el celular la arma · entonces su lectura previa falla, la 38 no viaja y se avisa en `descartes` con `fallo_tecnico` (A-53). Si aun así llega al nodo de finca (anomalía): `422 sobre-invalido` con `pointer: /anotaciones/37` y `regla: profundidad_excedida`; el celular no la quita, rearma una vez y, si vuelve a fallar, retiene esa partición y el nodo de finca alerta solo si el segundo cuerpo es distinto (A-32, A-41, A-56; rondas 1i P6, 1k P3).
- **C-77** Dada D-014 aplicada a las 14:05 (respuesta con `aplicadas: 1`) y superada a las 15:10 por D-022 · cuando D-014 reenvía · entonces recibe `sin_efecto` con `obsoleta`, el celular actualiza su acuse, y los conteos de la respuesta de las 14:05 no se reescriben (A-26, A-45).
- **C-78** Dado un reenvío con `primeros_acuses: [{uuid, primer_acuse_ms}]` donde `primer_acuse_ms` es posterior a ahora · cuando llega · entonces esa entrada se ignora y va a la bitácora; la anotación se juzga sin ella (A-45 ⟦2⟧b).
- **C-79** Dada una partición con `sesiones_captura: [S1]` y la anotación 12 con `sesion_captura_uuid: S9` · cuando llega · entonces `422 sobre-invalido` con `pointer: /sesiones_captura`, `regla: sesion_no_declarada` y `faltan: [S9]`; no entra ninguna anotación, el celular agrega S9 desde su almacén, reenvía y entran todas; nada se borra (A-38, A-41; ronda 1i P5).
- **C-84** Dada una partición armada con anotaciones validadas · cuando el nodo de finca responde `422 sobre-invalido` `json_ilegible` con `pointer: /anotaciones/38` · entonces el celular no borra nada: rearma el sobre una vez, con el mismo `particion_uuid`, y reenvía (A-41, A-53).
- **C-86** Dado un cuerpo cortado a la mitad (se cayó el Wi-Fi durante el envío) que nunca llegó a `"contrato"` · cuando el nodo de finca lo lee · entonces `422 sobre-invalido` `cuerpo_incompleto` sin `pointer`, nunca `contrato_obligatorio` ni `json_ilegible`; el celular reintenta la misma partición como ante un `5xx`, sin gastar el rearmado, y no cuenta para la alerta (A-22 ⟦0⟧, A-54).
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
- **C-29** Dada una anotación con `valor: 9999` y `veredicto_local: aceptada`, donde el campo admite 0-500 según la regla R-RANGO-01 de la versión 12 · cuando llega · entonces entra `marcada` con `["R-RANGO-01", "divergencia"]`; no se rechaza (A-32, ADR-014, ES-S11; ronda 1k P9).
- **C-30** Dada una anotación con `version_config: 99`, que el nodo de finca no conoce · cuando llega · entonces entra `marcada` con `version_desconocida` (A-32, ES-S10).
- **C-31** Dada una copia local verificada hasta el 600 y la nube hasta el 800 para D-014 · cuando D-014 recibe una respuesta · entonces `respaldado_hasta = 600` y retiene del 601 al 800 (A-31, ES3).
- **C-32** Dadas 1-200 aplicadas, 201 `rechazada` y 202-400 aplicadas · cuando se calcula la marca · entonces `confirmado_hasta = 400` (A-27, A-31).
- **C-34** Dada una partición con `"contrato": "1.9"` y un nodo de finca que conoce la 1.2 · cuando llega · entonces `422 contrato-incompatible` por el `contrato`: el celular deja de enviar del todo, sin partir ni borrar, y avisa que hay que actualizar el nodo de finca (A-24, A-41).
- **C-35** Dada una partición con `"contrato": "2.0"` en `/v1/` · cuando llega · entonces `422 contrato-incompatible` (A-24).
- **C-36** Dada una partición 1-200 que responde `422 sobre-invalido` con `pointer: /anotaciones/136` (la n.º 137) · cuando el celular la procesa · entonces **no** quita la 137: rearma el sobre una vez, mismo `particion_uuid`, y reenvía; si vuelve el `422`, retiene solo esa partición, sigue con las demás y el nodo de finca alerta solo si el segundo cuerpo es distinto (A-41, A-53, A-56; ronda 1k P3, reemplaza la versión que borraba la 137).
- **C-40** Dado un nodo de finca restaurado al 600, con un respaldo hasta el 800 que pasó la prueba de restauración pero cuya copia no se pudo usar · cuando responde · entonces `confirmado_hasta = 600` y `respaldado_hasta = 600` (invariante); el celular anula sus acuses del 601 al 800 y reenvía lo que todavía retiene (el cálculo de la posible pérdida: semilla ingreso) (A-31).
- **C-43** Dada la anotación 137 aislada y borrada por fallo técnico · cuando el celular envía su siguiente partición con `descartes: [{consecutivo: 137, motivo: fallo_tecnico}]` · entonces el nodo de finca cierra el hueco 137, lo deja en la bitácora y las marcas avanzan (A-47).
- **C-44** Dada una partición que responde `422 contrato-incompatible` · cuando el celular la procesa · entonces no la parte ni la borra; si la causa es el `contrato`, deja de enviar del todo y avisa qué lado actualizar; si es `tipo_desconocido`, retiene solo esa partición y sigue con las demás (A-24, A-41; redacción de la segunda poda, COH-85).
- **C-45** Dada una anotación con `sesion_captura_uuid: "x"` · cuando llega en una partición válida · entonces esa anotación es `rechazada` con `datos_invalidos` (fallo técnico), y las demás entran (A-32, A-38).
- **C-46** Dado op-12 con credencial válida de otra empresa · cuando entrega una partición · entonces `403 sin-permiso` con `regla: empresa_distinta` y no se lee ninguna anotación (A-22).
- **C-47** Dada la anotación 412 que el gerente ordenó descartar · cuando el celular la borra · entonces la avisa con `descartes: [{consecutivo: 412, motivo: descartada_por_gerente}]`, el nodo de finca cierra el hueco y las marcas siguen (A-47).
- **C-48** Dado un celular que pasa `tasa_por_dispositivo` · cuando entrega otra partición · entonces `429 demasiadas-peticiones` con `Retry-After` y `traceId`; el celular espera esos segundos (A-36, §3).
- **C-49** Dado un error interno del nodo de finca · cuando procesa una partición · entonces `500 error-interno` en `problem+json` con `traceId`, sin stack ni mensaje interno; el celular reintenta con la espera de A-40 (A-36).
- **C-50** Dada una partición con el mismo `anotacion_uuid` dos veces e igual contenido · cuando llega · entonces la segunda copia cuenta `sin_efecto`; si el contenido difiere, la segunda es `rechazada` con una sola alerta (A-42).
- **C-52** Dada la rechazada 137 (fallo técnico) borrada con lápida y un nodo de finca restaurado a un respaldo anterior a ella · cuando la marca baja · entonces el celular reenvía lo retenido y `descartes: [{consecutivo: 137, motivo: fallo_tecnico}]`; la respuesta trae `descartes_acusados: [137]` y el hueco no reaparece (A-47, A-31; las lápidas: semilla celular).
- **C-53** Dada una captura de 12 tallos con `veredicto_local: marcada`, donde la regla R-DESV-03 espera unos 60, con `motivo_desviacion: "helada"` · cuando llega · entonces entra `marcada` con `R-DESV-03`, cuenta en `conteo.marcadas` y sale en `excepciones[]`; el gerente ve el motivo (A-49; ronda 1j P8).
- **C-54** Dada la misma captura sin `motivo_desviacion` · cuando llega · entonces entra `marcada` con `R-DESV-03` y `desviacion_sin_motivo` (A-49).
- **C-55** Dada una captura que llega sin el mínimo requerido de su actividad · cuando el nodo de finca la juzga · entonces entra `marcada` con `divergencia` y `sin_minimo`, cuenta como fallo del motor del celular y el tablero pide recapturar esa cama (A-46, A-32).
- **C-56** Dada la 412 ya aplicada (la respuesta se perdió) · cuando llega `descartes: [{412, fallo_tecnico}]` · entonces no tiene efecto, queda en la bitácora, vuelve en `descartes_acusados` y la cama no sale en la lista de camas a rehacer (A-47).
- **C-57** Dada una partición con `anotaciones: []` y `descartes: [{137, fallo_tecnico}]` · cuando llega · entonces `200`, el hueco 137 se cierra y la respuesta trae las marcas actualizadas (A-47).
- **C-59** Dada la 77 obsoleta con su constancia · cuando el celular la reenvía · entonces la constancia no se duplica, la 77 cuenta para `confirmado_hasta` y vuelve en `excepciones[]` con `sin_efecto` y `obsoleta` (A-45, A-26).
- **C-63** Dada la 137 guardada con `"consecutivo": "13x"` (roto) · cuando el celular la valida al armar · entonces no viaja; avisa `descartes: [{consecutivo: 137, motivo: fallo_tecnico}]` con el número de su almacén, y el hueco 137 se cierra (A-47, A-53).
- **C-64** Dado un celular sin `registro_id` y una persona con credencial válida · cuando entrega · entonces sus anotaciones entran `marcadas` con `dispositivo_no_registrado` y el tablero lo muestra (A-23, ES-S13).
- **C-65** Dada una anotación con `desfase_declarado_ms: -1200` y un desfase medido al entregar de +2.200.000 ms · cuando llega · entonces entra `marcada` con `reloj_movido` (A-23, ADR-031 §3).
- **C-66** Dado un nodo de finca restaurado 4 días después · cuando el celular reenvía la 600, que tuvo su primer acuse 2 h después de capturarla, con `primeros_acuses: [{uuid de la 600, primer_acuse_ms}]` · entonces no se marca `vieja` (A-45 ⟦2⟧).
- **C-68** Dado un `403 sin-permiso` con `regla: empresa_distinta` para op-12 (`jti-9a1c`) · cuando el celular lo recibe · entonces no reintenta con esa credencial; el celular lo muestra y el nodo de finca alerta al administrador (A-56); nada se borra. Si op-07 entrega después en el mismo celular, en su propia sesión (C-74), se envía todo lo pendiente, también lo capturado por op-12; si op-12 renueva su credencial (`jti-77f0`), se intenta una vez más (A-41, A-51; ronda 1k P7).
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

**Catálogo de `regla` (lista abierta; agregar una es compatible).** `403`: `ingesta_entregar`, `empresa_distinta`, `sesion_ajena`. `422 sobre-invalido`: `cuerpo_incompleto` (A-54, fase ⟦0⟧), `contrato_obligatorio`, `contrato_mal_escrito` (A-24, fase ⟦1⟧), `anotacion_uuid_obligatorio`, `anotacion_uuid_invalido`, `consecutivo_obligatorio`, `consecutivo_invalido`, `sesion_no_declarada` (con `faltan[]`), `json_ilegible`, `profundidad_excedida` (redacción del 18-oct, COH-86). `422 contrato-incompatible`: `tipo_desconocido`. En los `422`, `regla` y `pointer` van dentro de `errores[]`; en los `403`, `regla` va en la raíz (redacción del 18-oct, COH-80).

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
| A-51 credencial *(semilla sesión, no se prueba aquí)* | credencial vencida en campo → sigue capturando, marca `capturada_sin_credencial` | el celular bloquea la captura hasta volver a la oficina |
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
| A-32 fallo de lectura ubicable (ronda 1i P6, ★; acción del celular: 1k P3) | la n.º 38 con profundidad 70 → `422` con `pointer: /anotaciones/37`, `regla: profundidad_excedida`; el administrador ve cuál es | → `422` sin `pointer`, o el celular borra la 38 por el `pointer` |
| A-41 segundo `422` (rondas 1i P7 y 1k P3-P5) | P3 falla dos veces con el mismo `particion_uuid` → se retiene solo P3 y P4 sigue; alerta `sobre_invalido_repetido` si el segundo cuerpo es distinto; con el cuerpo idéntico, solo el aviso local del celular | → se detiene todo el celular, se rearma con un `particion_uuid` nuevo, o no avisa ni el nodo de finca ni el celular |
| A-32 tipo desconocido (ronda 1i P9, ★) | `"tipo": "labor.siembra"` desconocido → `422 contrato-incompatible`, `pointer: /anotaciones/n/tipo`, `regla: tipo_desconocido`; el celular retiene sin borrar | → `rechazada` y el celular la borra, o entra `marcada` |
| A-22 orden de comprobación (ronda 1i P10, ★) | partición de 2 MiB en una sesión cerrada → `409 sesion-cerrada` (la sesión va antes que el tamaño) | → `413`, o una respuesta distinta en cada intento |
| A-22 tres fases de los `422` (ronda 1j P1, ★) | `"contrato": "1.x"` y un `tipo` desconocido en la 5 → `422 sobre-invalido` `pointer: /contrato` (fase 1 gana) | → `422 contrato-incompatible` `tipo_desconocido`, o una respuesta distinta en cada intento |
| A-22 ⟦0⟧ lectura primero (ronda 1k P8, ★; A-54) | `{"contrato": "1.0", "anotaciones": [{…}}, …]}` completo con una `}` de más en el sobre → `422` `json_ilegible` sin `pointer`; el mismo cuerpo cortado a la mitad → `cuerpo_incompleto` | → `422` `contrato_obligatorio`, o una regla distinta según dónde quedó `contrato` en los bytes |
| A-32 códigos de regla (ronda 1k P9, ★) | `veredicto_local: marcada` + R-DESV-03 en el nodo de finca → `["R-DESV-03"]`; `aceptada` + R-DESV-03 → `["R-DESV-03", "divergencia"]` | → `["divergencia"]` sin id de regla, o `divergencia` cuando los dos motores coinciden |
| A-33 esquema sin nulls (ronda 1k P10, ★) | `{"valor": null, "motivo_ausencia": "no_disponible"}` y el mismo sin `valor` → los dos `marcada` `incompleta`, misma huella | → sin `valor` da `rechazada` por campo obligatorio y con `valor: null` da `incompleta` |
| A-41 `json_ilegible` es una pista (ronda 1k P1 y P3, ★; antes 1j P2) | `422` `json_ilegible` `pointer: /anotaciones/38` → rearma el sobre una vez, no borra nada | → el celular borra la 39 (o la 38) por el `pointer` |
| A-53 un solo validador (ronda 1k P1-P2, ★) | el simulador y el nodo de finca usan `ValidadorContrato` con `FAIL_ON_TRAILING_TOKENS`; la 38 con `…}}` no pasa en el celular y no viaja; un `422 sobre-invalido` solo sale por anomalía | → el celular valida con otra biblioteca o configuración y la 38 viaja (Jackson sin esa opción ignora la `}` sobrante) |
| A-53 ⟦3⟧ misma medida (segunda poda, Juan) | una anotación de profundidad 63 se mide en los dos lados desde la anotación, con `max_profundidad_anotacion` y los topes de la sesión vigente | → el celular la mide sola con `max_profundidad_particion` y el nodo de finca responde `422 profundidad_excedida` |
| A-54 transporte (segunda poda, Juan) | cuerpo cortado → `422 cuerpo_incompleto`; el celular reintenta igual que un `503` y conserva su rearmado | → `json_ilegible`, gastar el rearmado o contar para la alerta |
| A-55 frontera general (segunda poda, Juan) | un `primer_acuse_ms` futuro o un descarte mal formado → se ignora y va a la bitácora; las anotaciones entran | → `422` a toda la partición por una entrada identificable mala |
| A-56 un aviso (segunda poda, Juan) | el mismo `particion_uuid` falla tres veces con cuerpos distintos → una sola alerta | → una alerta por cada `422`, o ninguna |
| A-29 `particion_uuid` (segunda poda) | partir `b005` por un `413` → `b006` y `b007`; rearmar `b005` → `b005` | → las dos mitades conservan `b005` |
| A-41 un solo camino ante `sobre-invalido` (ronda 1k P3, ★; reemplaza 1j P3) | `b005` recibe `422` con `pointer` y `sesion_no_declarada` juntos → rearma `b005` con el mismo uuid, sin quitar nada | → quita la anotación señalada y crea `b006`, o rearma con un `particion_uuid` nuevo |
| A-41 alcance de retener (rondas 1j P5 y 1k P7, ★) | `contrato` desconocido → el celular deja de enviar; `403 empresa_distinta` de op-12 → no envía más con la credencial de op-12, y op-07 sí entrega; `tipo_desconocido` en P3 → retiene P3 y envía P4 | → reintenta con la credencial que recibió el `403`, frena a op-07 por el `403` de op-12, o un `tipo_desconocido` frena todo el celular |
| A-32 tipo mal escrito (ronda 1j P6, ★) | `"tipo": "Captura Campo"` o sin `tipo` → esa anotación `rechazada` `datos_invalidos`; las demás entran | → `422 contrato-incompatible` `tipo_desconocido` |
| A-49 ⟦4⟧ desviación con motivo (ronda 1j P8, ★) | 12 tallos donde se esperan 60, con `motivo_desviacion: "helada"` → `marcada` `["R-DESV-03"]`, cuenta en `marcadas` | → `aplicada` sin marca y fuera de `excepciones[]` |
| A-41 alerta por memoria del ingreso (rondas 1k P4-P5; reemplaza 1j P10) | `b005` recibe un `422` con cuerpo C1, un reintento con C1 (no cuenta) y luego un `422` con cuerpo C2 → una alerta `sobre_invalido_repetido`; un tercer cuerpo no repite la alerta | → la partición trae `"rearmado": true` (campo desconocido → `422`, A-32), o cada `422` repite la alerta |
| A-52 dispositivo y topes de la sesión (ronda 1j P11, ★) | `POST /sesiones/S1/particiones` sin `dispositivo`; la sesión de sincronización S1 anunció `max_anotaciones_particion = 150` → con 151 anotaciones, `413` | → la partición trae `"dispositivo": "…e014"` y se acepta (tiene que ser `422`: campo desconocido, A-32), o el nodo de finca aplica 200 en vez de los 150 anunciados |
| A-3 clave del hecho | el ingreso compone la clave con la tupla del catálogo de su `version_config` | una clase `ClaveDelHecho` fija en el contrato |
| A-9 contrato versionado | cambiar `particion.schema.json` sin subir `contrato` hace fallar la prueba | el esquema cambia y la prueba pasa |
| A-12 ruta (P-S05, 19-oct) | `POST /ingesta/v1/sesiones/0192f0a1-…-a001/particiones` con `Authorization: Bearer <JWS>` → `200` | → `POST /ingesta/sesiones/{id}/lotes` sin versión, o sin credencial |
| A-29 cabeceras | `Authorization: Bearer <JWS>` y `traceparent`; los logs llevan `traceId` sin datos de negocio | un log con `"valor": 40` o el nombre del operario |
| A-37 Java puro | `contrato` sin dependencias de Spring; `backend-finca` no importa `..contrato.interno..` | `import org.springframework…` en `contrato`, o un uso de `interno` desde el backend |
| A-50 versiones | el celular trae solo versiones que el nodo de finca creó; `version_motor` desconocida → anomalía marcada | aceptar en silencio una versión que el nodo de finca no tiene |
| A-45 ⟦2⟧b primer acuse | `primer_acuse_ms` copiado de `recibido_ms` | `primer_acuse_ms` tomado del reloj del celular |
| A-36 transversal | `503` con `Retry-After: 30` y `traceId` | `500` con el stack de la excepción en el cuerpo |

## 5 · Forma técnica
Solo el módulo `contrato/` (JDK 27 con `release 27` según CLAUDE.md; Jackson 3). Lo usan `simulador` y `backend-finca`. Destinos previstos:

| Pieza | Archivo destino | Cubre |
|---|---|---|
| Petición de la partición | `dto/ParticionRequest.java` (`contrato`, `particion_uuid`, `sesiones_captura[]`, `anotaciones[]`, `descartes[]`, `primeros_acuses[]` opcionales) + `dto/Descarte.java` (A-47) | A-12, A-18, A-24 |
| Sesión de captura | `dto/SesionCaptura.java` (sin dispositivo; `Dispositivo` va con la sesión de sincronización, ronda 2) | A-18 |
| Anotación (sobre genérico) | `dto/Anotacion.java`, `dto/Tiempo.java` | A-15, A-16, A-17, A-21 |
| Datos de `captura.campo` | `dto/tipos/CapturaCampo.java` (`valor` como `JsonNode`) | A-20 |
| Respuesta | `dto/ResultadoParticion.java`, `dto/Conteo.java`, `dto/Excepcion.java` | A-23, A-26 |
| Códigos | `codigos/CodigoIngreso.java` (`hora_dudosa`, `divergencia`, `hueco_consecutivo`, `datos_invalidos`, `incompleta`, `motivo_ausente`, `consecutivo_repetido`, `consecutivo_resuelto`, `version_desconocida`, `vieja`, `obsoleta`, `desfase_desconocido`, `capturada_sin_credencial`, `desviacion_sin_motivo`, `sin_minimo`, `orden_duplicada`, `dispositivo_no_registrado`, `reloj_movido`, `fuera_de_asignacion`, …) | A-23, A-27, A-32, A-45, A-46, A-49 |
| Errores de la partición | `problemas/TipoProblema.java` (catálogo de `type`) | A-22 |
| Esquemas | `src/main/resources/esquemas/ingesta/v1/particion.schema.json`, `anotacion.schema.json`, `tipos/captura.campo.v1.schema.json`, `resultado-particion.schema.json` | A-9, A-15, A-22 |
| Huella | `HuellaCanonica.java` (fachada, paquete raíz; RFC 8785 + SHA-256 sobre el árbol recibido sin nulls) | A-10, A-19, A-33 |
| Validación | `ValidadorContrato.java` (fachada, la usan el simulador y el nodo de finca: fase ⟦0⟧, frontera 422 / anotación, topes de la sesión) + `interno/ValidadorEsquema.java` (biblioteca, 2020-12) | A-9, A-30, A-32, A-38, A-53, A-54, A-55 |
| Módulo | prueba de arquitectura en cada consumidor (sin `module-info`, A-37 del 19-oct) | A-37, A-44 |
| Identidad | `dto/Anotacion.java` (`anotacionUuid` como `UUID`) + `interno/ReglasUuid.java` y el `pattern` del esquema | A-1, A-38 |
| Tiempo | `dto/Tiempo.java` (`crudo_ms`, `mono_ms`, `arranque`, `desfase`) | A-16, A-17 |
| Respuesta y cabeceras | `dto/ResultadoParticion.java` (con `recibido_ms` y `descartes_acusados`), `dto/Excepcion.java`, `Cabeceras.java` (`TRACEPARENT`, `NO_STORE`, `RETRY_AFTER`) | A-13, A-14, A-26, A-29 |
| Lectura (A-57, A-9) | `lectura/LectorDto.java` (estricto y tolerante), `lectura/Lectura.java`, `lectura/IdDeCatalogo.java` | A-9, A-57 |
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
| Memoria y deduplicación de alertas (A-56), cuándo va `divergencia`, `sin_minimo`, alerta por hueco de más de 1 h, duplicados en la partición (A-42), versiones desconocidas (A-50) · K-104, K-105, COH-92, COH-93 | `IA_SEMILLA-caso-de-uso-ingreso.md` |
| Cómo arma y valida el celular (A-53), rearmado local, aviso en pantalla, tope de intentos tras un `403` · K-107, K-108, K-110, COH-94, C-83, C-85 | `IA_SEMILLA-caso-de-uso-celular.md` |
| Lo capturado con credencial de otra empresa (K-109); el dispositivo de la partición (A-52) y un celular por partición (A-30) | `IA_SEMILLA-endpoint-sesion-y-credencial.md` (parte 2) |
| Cómo lee el celular una respuesta más nueva (A-43) | despliegue y actualización (ADR-015, ADR-016) |

## 6 · Contradicciones y pendientes abiertos
Solo lo abierto **del contrato**. Lo resuelto vive en las HISTORIAS (`…-HISTORIA.md`, `…-HISTORIA-1g-1h.md` y `…-HISTORIA-1i-1k.md`, esta con todas las filas K y COH hasta la ronda 1k). **Segunda poda del 18-oct** (destino en `IA_apartado-2026-10-18/IA_00-INDICE.md` §5): K-104, K-105, COH-92 y COH-93 → semilla ingreso; K-107, K-108, K-110 y COH-94 → semilla celular (la regla general queda en A-53 y A-56); K-109 → semilla sesión. Por reglas generales (decisión de Juan): COH-88/K-106 → A-54; COH-91 → A-53 ⟦3⟧; K-81 → A-55; COH-95 → A-29; COH-96 → A-56. Por redacción, con permiso: COH-85, COH-89, COH-90 y COH-97. K-93 quedó como riesgo anotado en A-41 (1k P5-P6).

| Id | Qué | Tipo | Dueño | Estado |
|---|---|---|---|---|
| K-87 | A-12 está «confirmada» con fuente IA_TX1 pasos 7-14, pero IA_TX1 §4 dice `/ingesta/sesiones/{id}/lotes`, sin `v1` y con «lotes»; P-S05 sigue `POR ACEPTAR`; además IA_TX1 pone `POST /ingesta/archivos` fuera de `sesiones/{id}` | cita · A-12, A-24, A-30 | Juan | **cerrada 19-oct** · P-S05 aceptado con `v1` y «particiones»; `/archivos` fuera de la sesión; `IA_TX1` §4 lleva nota de superado |

**Pares cumple/viola:** todas las A-n tienen par (A-54, A-55, A-56 y el ajuste de A-29 desde la segunda poda; A-12 desde la pasada acotada del 18-oct, con la ruta provisional hasta K-87).

## 7 · Fuera del dominio (para otro canal)
Todo lo de otro elemento está en `IA_apartado-2026-10-18/IA_00-INDICE.md`, con su semilla o su documento en revisión.
- Seguridad del entorno (verificación del Segmento 2, punto 17, 20-oct): `backend-finca/src/main/resources/application.yaml` trae la clave de desarrollo por defecto `${FLORLOGIC_DB_CLAVE:florlogic}`; con PR-06, Juan decide si se quita el valor por defecto.
- TX-1.K, P-S04 y P-S05: `ACEPTADO 19-oct-2026`. La zona horaria de la jornada va a `IA_SEMILLA-configuracion-catalogo-y-versiones.md`.

## 8 · Plan de segmentos
Armado el 19-oct-2026 (etapa 4) con `verificacion.md` §1: ~600 líneas por segmento, ningún archivo cortado. Las
líneas son brutas; entre paréntesis, las de lógica (pruebas ×0,5, declarativo ×0,25). Solo cubre la **parte 1**
(`…/particiones`); la parte 2 (`/sesiones`, `/cierre`, `/archivos`) necesita su ronda (ronda 2) y tendrá su propio plan.
Cada segmento trae sus pruebas (PR-04) y deja `./mvnw verify` en verde. Las reglas que son lógica del ingreso o del
celular (A-2, A-27, A-31, A-39, A-40, A-41, A-42, A-46, A-49, A-51, A-56) **no se programan aquí**: en `contrato/`
solo viajan sus códigos y campos; su lógica está en las semillas (§5, «fuera del borde»).

| Segmento | Nombre | Archivos | Líneas (lógica) | Cubre | Depende de | Estado |
|---|---|---|---|---|---|---|
| Segmento 1 | Forma de los mensajes y barrera de lectura | `package-info.java`, `Cabeceras` · `lectura/`: `IdDeCatalogo`, `Lectura`, `LectorDto`, `package-info` · `dto/`: `ParticionRequest`, `SesionCaptura`, `Descarte`, `PrimerAcuse`, `Anotacion`, `Tiempo`, `Desfase`, `ResultadoParticion`, `Conteo`, `Excepcion`, `Listas` · `dto/tipos/`: `CapturaCampo`, `Valor` · `codigos/CodigoIngreso` · pruebas `DtoJsonTest` (14) y `LectorDtoTest` (10) · `src/test/resources/dorados/peticion-particion.json` y `respuesta-particion.json` | ~1.240 (~450) | A-9 (records), A-13, A-15, A-16, A-17, A-18, A-20, A-21, A-23, A-26, A-29, A-34, A-35, A-45 ⟦2⟧b, A-47, **A-57** · códigos de A-27, A-39, A-45, A-46, A-49, A-50 | — | **ACEPTADO (forzado) 19-oct** · refactorizado con A-57 (`./mvnw verify` en verde, 24/24; OBS-1-1 a 1-7 cerradas el 19-oct) · **diagrama:** `diagramas/IA_segmento-1.drawio` (1 · módulos, 2 · clases; PNG al lado), D-16 |
| Segmento 2 | **El canal: camino mínimo funcional de TX-1** (D-17) · diagrama `diagramas/IA_segmento-2.drawio` | backend-finca/ingesta: `V2__anotacion_recibida.sql`, `dominio/AnotacionRecibida`, `servicios/puertos/entrada/`: `RecibirParticion`, `RecibirParticionComando`, `ParticionRecibida` · `servicios/puertos/salida/AnotacionesRecibidas` · `servicios/casosuso/RecibirParticionServicio` · `infraestructura/entrada/web/ControladorParticiones` · `infraestructura/salida/persistencia/AnotacionesRecibidasJdbc` (empaquetado D-18, 20-oct), prueba `CanalParticionesTest` (PostgreSQL real) · simulador: `ClienteIngesta`, orden `Sincronizar`, prueba `ClienteIngestaTest` · `scripts/IA_probar-canal.sh` | ~700 (~380) | POST de punta a punta: leer con `LectorDto.estricto()`, guardar cada anotación (JSON tal cual, llave `anotacion_uuid`), responder `200` con el conteo; reenvío → `sin_efecto`; **cualquier fallo de forma → `422` «rechazado» sin motivo**; el celular lee con `LectorDto.tolerante()` y nunca lanza. **Después:** sesión `{id}` y credencial, esquema, huella y corrupta, reglas y marcas, códigos finos, A-58 por piezas, reintentos | Segmento 1 | **verificación aplicada 20-oct** (OBS-2-1, 2-9, 2-10, 2-11, 2-12, 2-15): `clean verify` 65/65 con Docker, mutación de OBS-2-1 detectada, `scripts/IA_probar-canal.sh` OK. Simulador hexagonal (`servicios/puertos/entrada` y `salida`, `servicios/casosuso`, `infraestructura/entrada/cli`, `infraestructura/salida/http`); pirámide de ingesta con contrato del puerto, doble en memoria y prueba JDBC. **Consultado** (D-15, OBS-2-6): 19-oct, plantillas endpoint T4 y T6, adaptador-persistencia 10.1 y 10.3, `investigacion/03` regla 8; 20-oct, caso-de-uso T8 y T9, adaptador-persistencia §9.7, §10.6 y tabla de paquetes, pruebas §0.3 y §0.7, *Applying Domain-Driven Design and Patterns* · p. 187 (Fake Object, Meszaros), *Implementing Domain-Driven Design* · p. 166 (DIP). Falta: coherencia, completitud y explicación de Juan |
| Segmento 3 | Huella | `interno/Jcs`, `interno/QuitarNulls`, `HuellaCanonica`, `Huella` · pruebas `JcsVectoresTest` (vectores RFC 8785, sobre todo números) y `HuellaCanonicaTest` (null ≡ ausente, a cualquier nivel; los arreglos conservan sus null) | ~650 (~380) | A-10, A-19, A-33, K-14 · base de A-2 y A-49 ⟦3⟧ | Segmento 1 | pendiente |
| Segmento 4 | Errores y fronteras de lectura (sobre `lectura/`, A-57; incluye OBS-1-8, COH-100, COH-101 y COH-102) | `problemas/Problema`, `ErrorCampo`, `TipoProblema` · `interno/LimitesJson` (tamaño, profundidad, `FAIL_ON_UNKNOWN_PROPERTIES`, sin tipos polimórficos) · `interno/ReglasUuid` · pruebas | ~500 (~300) | A-22, A-36, A-38, A-32 (lectura), A-54 (cuerpo incompleto) · variables `max_*` de §3 | Segmento 1 | pendiente |
| Segmento 5 | Esquemas JSON 2020-12 | `esquemas/ingesta/v1/particion.schema.json`, `anotacion.schema.json`, `tipos/captura.campo.v1.schema.json`, `resultado-particion.schema.json` · `interno/ValidadorEsquema` · `ContratoVersionTest` | ~700 (~260) | A-9, A-15, A-20, A-24, A-33 (esquema sin `null`) | Segmento 1 y 4 · **bloqueo:** biblioteca de JSON Schema sin fila en `IA_TECNOLOGIAS-vigentes.md` (TEC-11 «POR DEFINIR»); se propone con su fila y Juan la acepta antes de escribir el Segmento 5 | pendiente |
| Segmento 6 | Validador único (incluye A-58 por piezas y COH-98) | `ValidadorContrato` (fachada que nunca lanza, A-57: orden ⟦0⟧ lectura → versión → esquema; frontera `422` contra rechazo por anotación; topes de la sesión), `ResultadoValidacion` · pruebas de los pares cumple/viola (§4b) | ~650 (~400) | A-24 (orden), A-30, A-32, A-38, A-52 (topes), A-53, A-55 | Segmentos 3, 4 y 5 | pendiente |
| Segmento 7 | Ejemplos dorados y arquitectura | `src/test/resources/dorados/*.json` (petición, respuesta, reenvío, errores de §4b) · `EjemplosDoradosTest` · **OBS-1-9 y OBS-1-10** (descartes, primeros acuses, acción, partición solo con descartes) · **prueba de contrato dirigida por el consumidor**: peticiones armadas por el simulador pasan por `ValidadorContrato` (OBS-1-7; *Microservices Patterns* · p. 332) · prueba de arquitectura en `backend-finca` y `simulador` (A-44: `dominio` no importa `contrato`) | ~500 (~220) | §4b completa, A-37, A-44 · casos C-n de forma y frontera | Segmentos 1 y 3 a 6 | pendiente |

**Orden desde el 19-oct (D-17):** primero el canal (Segmento 2); los segmentos 3 a 7 del contrato quedan para «después», en el orden en que Juan los pida.

**Lo que este plan no programa:** los controladores y el puerto de entrada (plantilla `endpoint` ⟦11⟧, fuera del
dominio acordado); la generación de UUID v7 (TEC-12, es del simulador). La tarjeta ES que cierra PR-04 para TX-1
(ES-S01) necesita también el ingreso: el contrato deja en verde ES-S04 y ES-S05 en lo que le toca (huella y forma).

## 9 · Matriz requisito → código → prueba
| Requisito | Código | Prueba (`DtoJsonTest`) | Segmento |
|---|---|---|---|
| A-15, A-18, A-21, A-29 forma de la petición | `ParticionRequest`, `SesionCaptura`, `Anotacion` | `lee_la_peticion_dorada`, `la_peticion_dorada_vuelve_igual` | 1 |
| A-20, A-35, A-34 (`Valor`) | `CapturaCampo`, `Valor` | `los_datos_de_captura_campo_dan_el_valor_o_su_falta`, `valor_ausente_y_valor_null_son_lo_mismo`, `valor_con_motivo_de_ausencia_es_contradictorio_y_no_lanza`, `captura_campo_escrita_omite_el_valor_ausente` | 1 |
| A-16, A-33, A-34 (`Desfase`) | `Tiempo`, `Desfase` | `desfase_ausente_null_y_numero` | 1 |
| A-13, A-23, A-26, A-31 (invariante), A-47 forma de la respuesta | `ResultadoParticion`, `Conteo`, `Excepcion` | `la_respuesta_dorada_vuelve_igual`, `la_respuesta_que_rompe_sus_invariantes_se_arma_y_dice_que_rompe`, `sin_excepciones_igual_escribe_el_arreglo_vacio` | 1 |
| A-23, §3b códigos del ingreso | `CodigoIngreso` | `cada_codigo_del_ingreso_cumple_la_forma_de_3b` | 1 |
| A-57 lector tolerante | `IdDeCatalogo`, enums con `DESCONOCIDO` | `una_respuesta_mas_nueva_se_sigue_leyendo`, `un_veredicto_o_motivo_desconocido_no_tumba_la_lectura` | 1 |
| A-57 barrera de lectura · A-9 dos lectores | `Lectura`, `LectorDto.estricto()`, `LectorDto.tolerante()` | `LectorDtoTest` (10): `el_estricto_rechaza_un_campo_desconocido_en_la_peticion`, `el_tolerante_lee_una_respuesta_con_campos_de_una_version_mas_nueva`, `el_estricto_acepta_la_peticion_dorada_y_su_captura_campo`, … | 1 |
| A-29 cabeceras | `Cabeceras.AUTHORIZATION`, `ESQUEMA_BEARER` | (constantes; las usa el Segmento 7) | 1 |
| A-57 constructores totales y Notification | `Listas`, `Conteo.incoherencias`, `ResultadoParticion.incoherencias`, `Valor.Contradictorio` | `la_respuesta_que_rompe_sus_invariantes_se_arma_y_dice_que_rompe`, `una_lista_con_null_no_lanza`, `valor_con_motivo_de_ausencia_es_contradictorio_y_no_lanza` | 1 |
| A-37 encapsulamiento | (sin `module-info`) | prueba de arquitectura en cada consumidor, Segmento 7 | 7 |
| Canal de TX-1 (D-17): POST → guardar → 200; reenvío sin duplicar; fallo de forma → 422 | `ControladorParticiones`, `RecibirParticionServicio`, `AnotacionesRecibidasJdbc`, `V2__anotacion_recibida.sql`, `ClienteIngesta`, `Sincronizar` | `CanalParticionesTest` (4), `ClienteIngestaTest` (4), `scripts/IA_probar-canal.sh` | 2 |
| OBS-2-1: un elemento de `anotaciones[]` que no es objeto → 422 | `ControladorParticiones.soloObjetos` | `CanalParticionesTest.cualquier_fallo_de_forma_es_422_rechazado_y_no_guarda_nada` (`[null, …]`, `[5, …]`; mutación detectada) | 2 |
| Reenvío por identidad, en el núcleo (OBS-2-11) | `RecibirParticionServicio`, `AnotacionesRecibidas` | `RecibirParticionServicioTest` (5), `AnotacionesRecibidasContratoTest` heredada por `…EnMemoriaTest` y `…JdbcTest` (4 + 4) | 2 |
| Simulador hexagonal (OBS-2-12): la orden solo ve el puerto; códigos de salida 0 · 1 · 2 | `Sincronizar`, `SincronizarParticionServicio`, `NodoDeFinca`, `ClienteIngesta` | `SincronizarTest` (4), `SincronizarParticionServicioTest` (2), `ClienteIngestaTest` (4) | 2 |

## 10 · Observaciones de revisión

### Segmento 1 · revisión abierta el 19-oct-2026
```
OBS-1-1 · pregunta (mayor, confiabilidad): el celular no tolera un resultado o una acción nuevos en la respuesta · **APLICADA 19-oct (A-57)**
  Ubicación: dto/Excepcion.java:26 (enum Resultado) · dto/ResultadoParticion.java:44 (enum Accion)
  Requisito: A-23, A-26 · A-43 (fuera del borde: despliegue y actualización) · plantilla endpoint EP-16
  Detalle: codigos[] es texto y tolera códigos nuevos, pero resultado y accion son enums cerrados: si un
    nodo de finca más nuevo manda "accion": "otra", el simulador falla al leer TODA la respuesta. La
    plantilla (EP-16, «lector tolerante y enums extensibles», Fowler TolerantReader) pide que el cliente
    tolere valores nuevos. A-43 dejó «cómo lee el celular una respuesta más nueva» fuera del contrato.
  Criterio de cumplido: Juan decide (a) dejarlo así porque A-43 lo resuelve el despliegue, o (b) una
    variante «desconocida» en esos enums con prueba de que una respuesta con un valor nuevo se lee.
OBS-1-2 · proceso (mayor): el segmento se escribió sin consultar §10 y §11 de la plantilla endpoint ni investigacion/02 y 03
  Ubicación: todo el segmento
  Requisito: SKILL etapa 4 («siguiendo §10 y §11 de la plantilla»)
  Detalle: se escribió desde la ESPEC. Contrastado después: (1) T3 usa @JsonNaming(SnakeCaseStrategy) y aquí
    cada campo lleva @JsonProperty explícito (D3); (2) investigacion/03 regla 11 dice «nunca un
    IllegalStateException genérico» para fallos de negocio, y CapturaCampo.dato() lo usa, aunque para un
    error de programación, no de negocio; (3) §11 pone el contrato en JSON y el esquema primero, y aquí el
    esquema va en el Segmento 5.
  Criterio de cumplido: Juan acepta o cambia D3 y el uso de IllegalStateException, y los segmentos 3 a 7
    se escriben leyendo antes la plantilla y la investigación que les toca (anotado en cada ficha).
OBS-1-2 **CERRADA 19-oct**. (2) aplicada: ya no hay IllegalStateException (Valor.Contradictorio). (1) D3 se mantiene, con su porqué: los nombres explícitos hacen que la forma no dependa de cómo se configure el mapper, y OBS-1-5 mostró que la configuración cambia por lado (lector estricto y tolerante); T3 usa @JsonNaming para un solo mapper. (3) el esquema queda en el Segmento 5 porque necesita la biblioteca de TEC-11; los ejemplos dorados, que son el contrato en JSON, ya están en el Segmento 1.
OBS-1-3 · sugerencia (menor): Cabeceras no nombra Authorization ni el esquema Bearer · **APLICADA 19-oct** (AUTHORIZATION, ESQUEMA_BEARER)
  Ubicación: Cabeceras.java:7
  Requisito: A-29
  Detalle: A-29 lista tres cabeceras y Cabeceras.java solo tiene traceparent; Authorization queda escrita a
    mano en el simulador y en el nodo de finca.
  Criterio de cumplido: AUTORIZACION = "Authorization" y PREFIJO_BEARER = "Bearer " en Cabeceras, o
    Juan decide que lo pone el cliente HTTP.
OBS-1-4 · pedido de Juan (crítica, confiabilidad): código que se defiende fuera de su entorno · **APLICADA 19-oct**
  Ubicación: todo el segmento
  Requisito: A-57 (nueva)
  Detalle: «si el backend está funcionando solo debería poder continuar sin pararse; cualquier segmento
    debería poder controlar lo imprevisto». Se aplicó como A-57 con cuatro patrones: lector tolerante,
    resultado sellado (Lectura), Notification y Special Case; más una barrera de fallos en LectorDto.
  Criterio de cumplido: ningún constructor ni lector del contrato lanza ante una entrada (LectorDtoTest,
    DtoJsonTest) y los segmentos 3 a 7 cumplen A-57 (cada ficha lo dice).
```

**Segunda pasada, 19-oct, con el MCP `biblioteca`** (contratos y comunicación):
```
OBS-1-5 · problema (mayor, confiabilidad): el lector ignora campos desconocidos en las dos direcciones · **APLICADA 19-oct** (Juan: dos lectores; A-9 y A-57)
  Ubicación: LectorDto.java (usa el mapper que le pasan) · todos los dto/
  Requisito: A-9 («FAIL_ON_UNKNOWN_PROPERTIES»), A-32 (campo desconocido → rechazada o 422), A-57
  Detalle: Jackson 3 trae FAIL_ON_UNKNOWN_PROPERTIES = false (comprobado con una sonda). Para la
    respuesta es lo correcto: «clients should ignore any extra response attributes» (Microservices
    Patterns · p. 100, robustness principle; Principles and Patterns… · p. 123, Postel). Para la petición
    contradice A-9: un "cama_id" de más se pierde en silencio en el DTO. Hoy solo lo atraparía el esquema
    del Segmento 5, si se valida antes de leer (A-24). Además, el mismo libro dice que el servicio «should
    provide default values for missing request attributes», y A-32 elige lo contrario a propósito («nada
    desconocido entra», A-50): es una desviación consciente que hoy no está escrita.
  Criterio de cumplido: el Segmento 4 define dos lectores, estricto (el nodo de finca lee peticiones:
    desconocido → Fallida con pointer) y tolerante (el celular lee respuestas), con una prueba de cada
    uno; A-57 nombra la desviación frente a Postel en la petición.
OBS-1-6 · sugerencia (menor, trazabilidad): nombrar en el código los patrones de los libros · **APLICADA 19-oct** (package-info, Anotacion)
  Ubicación: package-info.java · dto/Anotacion.java
  Requisito: D-15 (citar la fuente)
  Detalle: el módulo es un Published Language (DDD Reference · p. 43; Vernon, IDDD · p. 143: OHS/PL);
    Anotacion es un Envelope Wrapper (POSA 4 · p. 267, HoWo03); los dto son Data Transfer Objects «present
    on both sides» (PoEAA · secc. 100), por eso el módulo es compartido. PoEAA y POSA 4 (p. 456) dicen que
    el DTO tiene «little behavior»: incoherencias() y dato() son poca conducta y solo de lectura, lo que
    cabe en el patrón; queda escrito para que nadie le agregue reglas.
  Criterio de cumplido: package-info y Anotacion citan los patrones con su fuente.
OBS-1-7 · sugerencia (menor, pruebas): prueba de contrato dirigida por el consumidor en el Segmento 7 · **APLICADA 19-oct** (§8, Segmento 7)
  Ubicación: plan §8, Segmento 7
  Requisito: A-53 (un solo validador para los dos lados)
  Detalle: compartir el jar evita errores de compilación, no de comportamiento. Microservices Patterns ·
    p. 332 («Consumer-driven contract test: verify that a service meets the expectations of its
    clients») y p. 358: que el simulador genere sus peticiones reales y el nodo de finca las acepte con el
    mismo esquema.
  Criterio de cumplido: el Segmento 7 incluye una prueba que toma peticiones armadas por el simulador
    y las pasa por ValidadorContrato.
OBS-1-8 · problema (menor, confiabilidad): Jackson convierte en silencio tipos que el contrato no permite · **PASA AL SEGMENTO 3** (Juan, 19-oct: menor; el esquema del Segmento 5 ya lo rechaza, es defensa en profundidad de la lista permitida)
  Ubicación: LectorDto.java (configuración de ESTRICTO y TOLERANTE)
  Requisito: §3b (`consecutivo` entero), A-32 (forma inválida → rechazada), A-57
  Detalle: sonda del 19-oct sobre Descarte.consecutivo (long), en los dos lectores:
    17 → 17 · "17" (texto) → 17 · 17.9 → 17 (¡trunca!) · "uno" → formato_invalido · true → tipo_incorrecto
    · 9999999999999999999 → json_ilegible · null → tipo_incorrecto · ausente → tipo_incorrecto.
    El texto "17" y el decimal 17.9 se aceptan aunque §3b exige entero: el 17.9 cambia el dato sin aviso.
    Corrige además un punto sensible de la ficha del 19-oct, que decía que un número ausente se leía como 0:
    en Jackson 3 un primitivo ausente o null falla (FAIL_ON_NULL_FOR_PRIMITIVES viene encendido).
  Criterio de cumplido: el lector estricto rechaza texto en un número y decimal en un entero
    (ACCEPT_FLOAT_AS_INT apagado y coerción de escalares en Fail), el tolerante no trunca decimales, y una
    prueba cubre los ocho casos de la sonda; el desbordamiento sale con una clase propia (fuera_de_rango).
```

### Segmento 2 · revisión abierta el 20-oct-2026 (filtro del camino mínimo, D-17)
Ficha presentada a Juan el 20-oct. Solo OBS-2-1 rompe el canal; las demás quedan anotadas con su destino y no reabren el segmento.
**Revisión cerrada por Juan el 20-oct: «aplicar».** Entran en la verificación: OBS-2-1, OBS-2-6 (redacción), OBS-2-10, OBS-2-11, OBS-2-12 y OBS-2-15 (OBS-2-9 ya cumplida). Siguen con su destino: OBS-2-2, 2-3, 2-4, 2-5, 2-7, 2-13 y 2-14. Plan y diagrama: `diagramas/IA_segmento-2-verificacion.drawio`.
```
OBS-2-1 · issue (blocking, confiabilidad): un null dentro de "anotaciones" desalinea el DTO y el JSON crudo, y se guarda contenido ajeno con 200
  Ubicación: ControladorParticiones.java:74-81 (crudas.get(i)) · contrato dto/Listas.java:18 (quita los null)
  Requisito: regla por defecto de D-17 (fallo de forma → 422 rechazado) · A-33 (se guarda lo que llegó)
  Detalle: sonda del 20-oct con la petición dorada y "anotaciones": [null, A, B]: el lector estricto la da
    Leida; Listas.copia quita el null y el DTO queda [A, B], pero el árbol crudo sigue siendo [null, A, B].
    El bucle guarda A con contenido 'null' (jsonb null, pasa el NOT NULL), B con el JSON de A, pierde el
    JSON de B y responde 200 «2 nuevas». El dato queda corrupto en silencio, sin error.
  Criterio de cumplido: el controlador rechaza con 422 si algún elemento de "anotaciones" no es un objeto
    (o si el DTO y el árbol no tienen el mismo tamaño); CanalParticionesTest lo prueba con [null, …] y 0 filas.
  Origen: agente (sonda) · Estado: **cumplida 20-oct** (`ControladorParticiones.soloObjetos`; prueba con `[null, …]` y `[5, …]`; mutación comprobada: sin el arreglo la prueba falla)
OBS-2-2 · todo (menor, confiabilidad): el cuerpo se lee entero en memoria sin tope de tamaño
  Ubicación: ControladorParticiones.java:62 (byte[] cuerpo)
  Requisito: variables max_* de §3 · Segmento 4 (LimitesJson)
  Criterio de cumplido: un cuerpo mayor que el tope es 413 o 422 sin leerse entero. → Segmento 4
  Origen: agente · Estado: abierta (después)
OBS-2-3 · nitpick (menor): sin Content-Type JSON responde 415 de Spring y no 422 «rechazado»
  Ubicación: ControladorParticiones.java:61 (consumes) · el simulador sí manda la cabecera
  Criterio de cumplido: Juan decide si 415 se queda (es HTTP, no forma) o pasa a la regla por defecto. → Segmento 4
  Origen: agente · Estado: abierta (después)
OBS-2-4 · todo (menor, pruebas): CanalParticionesTest usa una copia de la petición dorada en /canal/
  Ubicación: CanalParticionesTest.java:116 · ClienteIngestaTest.java (respuesta) · el script usa la del contrato
  Detalle: si el contrato cambia su dorada, la copia no se entera y la prueba del canal sigue en verde.
  Criterio de cumplido: una sola dorada, compartida o comprobada igual a la del contrato. → Segmento 7
  Origen: agente · Estado: abierta (después)
OBS-2-5 · nitpick (nit): un 200 ilegible sale como fallo_inesperado y no con su clase propia
  Ubicación: ClienteIngesta.java:45 y 55 (además arma un JsonMapper por llamada)
  Criterio de cumplido: clase «respuesta_ilegible» y un mapper compartido. → después
  Origen: agente · Estado: abierta (después)
OBS-2-6 · chore (menor, trazabilidad): la ficha del 19-oct no dejó la línea «Consultado» (D-15) del Segmento 2
  Ubicación: ESPEC §8, fila del Segmento 2
  Detalle: el código cita endpoint T4 y T6, adaptador-persistencia 10.1 y 10.3 e investigacion/03 regla 8,
    pero no hay registro de qué libros o secciones se leyeron antes de escribir.
  Criterio de cumplido: la fila del Segmento 2 nombra lo consultado. → redacción, con permiso
  Origen: agente · Estado: **cumplida 20-oct** (redacción: §8, fila del Segmento 2)
OBS-2-7 · todo (menor, cohesión): el tipo de problema RECHAZADO vive en el controlador y no en el contrato
  Ubicación: ControladorParticiones.java:48 · ClienteIngestaTest.java:50 lo repite como texto
  Requisito: A-22 (tipos de problema en contrato problemas/TipoProblema) · regla por defecto de D-17
  Detalle: el type es parte del Published Language: el celular también lo lee. Hoy solo lo produce el
    controlador porque el canal no tiene motivos finos. Además «rechazado» no estaba en la Nomenclatura
    (agregado el 20-oct como provisional); la lista de A-22 lo llamaría sobre-invalido.
  Criterio de cumplido: el type sale de contrato problemas/TipoProblema y el simulador lo compara con esa
    constante; Juan decide si «rechazado» se queda o pasa a sobre-invalido. → Segmento 4
  Origen: pregunta de Juan (20-oct) · Estado: abierta (después)
OBS-2-8 · question (nit): ¿requireNonNull en el comando revienta si sesionId llega null?
  Ubicación: RecibirParticionComando.java:13-14 · AnotacionRecibida.java:21-23
  Requisito: A-57 (solo para fronteras con entrada externa) · investigacion/03 regla 8
  Detalle: el controlador ya rechazó con 422 todo sesionId inválido (ControladorParticiones.java:63-66),
    así que el null solo llega por un defecto de programación. Si pasara, la NullPointerException sube hasta
    Spring, que responde 500 genérico solo para esa petición; el servidor sigue. Es fail-fast a propósito.
  Criterio de cumplido: respondida en la revisión; Juan decide si se queda así. Sin cambio de código.
  Origen: pregunta de Juan (20-oct) · Estado: respondida
OBS-2-9 · issue (blocking, mantenibilidad · D-18 desde el 20-oct): el empaquetado del Segmento 2 no sigue las plantillas
  Ubicación: ingesta/infraestructura/salida/AnotacionesRecibidasJdbc.java
  Requisito: plantilla adaptador-persistencia §10 (tabla de paquetes, línea 102) y §10.8 (regla ArchUnit de PR-02 y PR-03)
  Detalle: la plantilla pone el adaptador, su mapper y su fila en <modulo>.infraestructura.salida.persistencia,
    y su regla de arquitectura encierra Spring JDBC en ese paquete. El Segmento 2 lo dejó en salida/ sin
    decirlo ([sin fuente]). Hoy hay un solo adaptador de salida y no rompe el canal; pesa cuando ingesta
    tenga otro tipo de salida (HTTP, archivos) o cuando se escriba la regla ArchUnit.
  Ampliación (20-oct, D-18): además, el puerto de entrada RecibirParticion, su comando y su resultado viven
    en servicios/casosuso junto al servicio, cuando las plantillas endpoint (tabla de paquetes, l. 107-109) y
    caso-de-uso (l. 137-139) los ponen en port.in, aparte del servicio; y el controlador está en
    infraestructura/entrada sin subpaquete por tecnología (endpoint: adapter.in.web).
  Criterio de cumplido: cada archivo del Segmento 2 vive en el paquete que Juan elija (pregunta del 20-oct),
    IA_LEEME describe la convención y ./mvnw verify sigue en verde. Se aplica en la verificación del Segmento 2.
  Decisión de Juan (20-oct): «plantilla en español». ingesta/servicios/puertos/entrada (RecibirParticion,
    RecibirParticionComando, ParticionRecibida) · servicios/puertos/salida (AnotacionesRecibidas) ·
    servicios/casosuso (RecibirParticionServicio) · infraestructura/entrada/web (ControladorParticiones) ·
    infraestructura/salida/persistencia (AnotacionesRecibidasJdbc). Es la convención del proyecto: va a
    IA_LEEME; los otros 14 módulos la toman cuando reciban código (sin paquetes vacíos de relleno, D-18).
  Origen: pregunta de Juan (20-oct) · Estado: **cumplida 20-oct** (Juan pidió aplicarla ya): archivos movidos con git mv,
    package-info en cada paquete nuevo, IA_LEEME y diagrama del Segmento 2 actualizados; ./mvnw verify en verde
    (CanalParticionesTest 4/4 con PostgreSQL real, ModulosTest 2/2)
OBS-2-10 · nitpick (nit, pruebas): dos afirmaciones que faltan en CanalParticionesTest
  Ubicación: CanalParticionesTest.java:103-105 (sesión que no es UUID) · :52-68 (camino feliz)
  Detalle: la prueba de la sesión inválida no comprueba que no se guardó nada, y el camino feliz no comprueba
    que la fila lleve el sesion_id de la ruta. El código hoy lo hace bien; la prueba no lo fijaría si cambiara.
  Criterio de cumplido: filas() == 0 tras la sesión inválida y sesion_id de la fila == SESION. → con OBS-2-1 (misma prueba)
  Origen: agente (revisión con Juan, 20-oct) · Estado: **cumplida 20-oct**
OBS-2-11 · issue (mayor, pruebas): en ingesta la pirámide está invertida: solo hay la prueba de arriba
  Ubicación: backend-finca/src/test/java/co/florlogic/finca/ingesta/ (solo CanalParticionesTest)
  Requisito: plantilla pruebas §0.3 (defecto: doble en memoria de cada puerto de salida y prueba de contrato
    abstracta, caso-de-uso T8 y T9, adaptador-persistencia §10.6) y §0.7 («cada regla en el nivel más bajo
    que pueda detectarla»; cono de helado como antipatrón)
  Detalle: RecibirParticionServicio (contar aplicadas y sin efecto, el mayor consecutivo) solo se prueba
    con Spring, Tomcat y PostgreSQL; no hay prueba unitaria con un AnotacionesRecibidas en memoria, ni prueba
    de contrato del puerto que comparta la implementación en memoria y la JDBC. Un fallo del conteo se
    descubre en la prueba más lenta y con el diagnóstico más pobre. D-17 pide una prueba que recorra el canal
    (está); la base de la pirámide la pide la plantilla de pruebas.
  Criterio de cumplido: Juan decide si entra ahora o en otro segmento: RecibirParticionServicioTest (unitaria,
    doble en memoria) y AnotacionesRecibidasContratoTest (abstracta; la heredan la de memoria y la JDBC).
  Decisión de Juan (20-oct): entra **con la verificación** del Segmento 2, junto con OBS-2-1 y OBS-2-10.
    Ubicación en src/test (plantilla adaptador-persistencia §10, tabla de paquetes): servicios/puertos/salida/
    AnotacionesRecibidasContratoTest (abstracta) y AnotacionesRecibidasEnMemoria (doble) ·
    servicios/casosuso/RecibirParticionServicioTest · infraestructura/salida/persistencia/
    AnotacionesRecibidasJdbcTest (hereda el contrato; va en ese paquete porque el adaptador es package-private).
  Origen: pregunta de Juan sobre la pirámide (20-oct) · Estado: **cumplida 20-oct** (65/65; AnotacionesRecibidasJdbcTest sin Spring, VS2-4)
OBS-2-12 · issue (blocking, D-18): el simulador es un solo paquete plano y su adaptador de entrada llama directo al de salida
  Ubicación: simulador/src/main/java/co/florlogic/simulador/ (5 clases en el mismo paquete) · Sincronizar.java:54
  Requisito: D-18 · plantilla endpoint/caso-de-uso «Dónde vive cada pieza» · A-44
  Detalle: Sincronizar (adaptador de entrada: línea de comandos) hace new ClienteIngesta(...) (adaptador de
    salida: HTTP) sin puerto ni caso de uso entre ellos; Simulador y FabricaDeOrdenes (picocli) conviven con
    el arranque. Reubicar sin más deja a la vista una dependencia entrada → salida que la hexagonal no admite.
  Decisión de Juan (20-oct): «hexagonal completa», el mismo molde que ingesta. Lectura propuesta (Juan la
    confirma al cerrar la revisión): servicios/puertos/entrada/SincronizarParticion (+ comando),
    servicios/puertos/salida/NodoDeFinca, servicios/casosuso/SincronizarParticionServicio,
    infraestructura/entrada/cli/ (Simulador, Sincronizar, FabricaDeOrdenes), infraestructura/salida/http/
    ClienteIngesta implements NodoDeFinca; dominio/ entra cuando haya una clase de dominio (el almacén de
    salida), sin paquete vacío (D-18). Base de la pirámide igual que OBS-2-11: servicio probado con un
    NodoDeFinca en memoria.
  Criterio de cumplido: esa estructura, Sincronizar solo conoce el puerto de entrada, pruebas en verde.
  Origen: agente (pasada 2 con Juan, 20-oct) · Estado: **cumplida 20-oct** (más SincronizarTest, VS2-7; canal real OK)
OBS-2-13 · nitpick (nit): un tiempo de espera agotado sale como «sin_conexion»
  Ubicación: ClienteIngesta.java:50 (HttpTimeoutException es una IOException)
  Criterio de cumplido: clase propia «sin_respuesta» para el timeout. → después, con OBS-2-5
  Origen: agente · Estado: abierta (después)
OBS-2-14 · nitpick (menor): base.resolve("/ingesta/…") descarta la ruta de la URL base
  Ubicación: ClienteIngesta.java:37
  Detalle: con --url http://finca/api, la petición va a http://finca/ingesta/… y no a /api/ingesta/…; importa
    si el gateway (TEC-15) publica el backend bajo un prefijo.
  Criterio de cumplido: la ruta se concatena a la base, con prueba. → con el gateway (después)
  Origen: agente · Estado: abierta (después)
OBS-2-15 · issue (menor, entorno): el script borra el volumen de la base de desarrollo al terminar
  Ubicación: scripts/IA_probar-canal.sh:16 (docker compose … down -v en el trap)
  Detalle: -v elimina el volumen «datos» de despliegue/compose.yaml; si Juan tenía datos de desarrollo ahí,
    cada corrida los pierde sin avisar.
  Criterio de cumplido: el script usa su propio proyecto de compose (-p florlogic-canal) o no borra volúmenes ajenos.
  Origen: agente · Estado: **cumplida 20-oct** (corrida: crea y borra solo florlogic-canal_datos)
```

## 11 · Informe de verificación

### Segmento 1 · verificación abierta el 19-oct-2026
**Lista de 22 puntos** (`investigacion/05` §4.1). Build: ok · pruebas 24/24 · Docker disponible (backend-finca probado contra PostgreSQL).

| # | Punto | Resultado |
|---|---|---|
| 1 | Cada requisito cubierto lleva a código y prueba | **No** → OBS-1-9: A-45 ⟦2⟧b (`primeros_acuses`), A-47 (`descartes`, `descartes_acusados`) y `accion` conocida no tienen prueba |
| 2 | Ningún código sin padre | Sí (`Listas`, `IdDeCatalogo` → A-57) |
| 3 | Lo de fuera nombrado con su segmento | Sí (§8) |
| 4 | Sin API «por si acaso» | Sí. `Cabeceras` y `CodigoIngreso.de` se usan desde el Segmento 7 y el simulador; son parte del Published Language |
| 5 | Camino feliz probado de punta a punta | Sí (petición y respuesta doradas, ida y vuelta) |
| 6 | Bordes: nulo, vacío, límites, duplicados | **Parcial** → OBS-1-10: no hay prueba de que una petición con `anotaciones` vacía se escriba con `[]` (A-32 exige el arreglo) |
| 7 | Fallos externos con comportamiento definido | Sí (`LectorDto` no lanza; sin E/S en el segmento) |
| 8 | Concurrencia | Sí: los dos `ObjectMapper` son inmutables y se comparten (Jackson 3) |
| 9 | Las pruebas fallarían si el código se rompe | Sí |
| 10 | Pruebas no debilitadas | Sí (sin `@Disabled`, sin mocks) |
| 11 | Build y pruebas pasan | Sí |
| 12 | Arquitectura y fronteras | Sí (Java puro; la prueba de A-44 en consumidores va en el Segmento 7) |
| 13 | Sin sobreingeniería | Sí, con nota: `Desfase.FiltroDesconocido` es un truco de Jackson, documentado |
| 14 | Reutilización | Sí, con nota: los 5 enums repiten id/`@JsonValue`/`de()` porque Java no hereda enums; la búsqueda está centralizada en `IdDeCatalogo.buscar` |
| 15 | Entradas validadas en el servidor | Parcial por diseño: lector estricto aquí; esquema y validador en los segmentos 5 y 6 |
| 16 | Autorización | N. A. (controlador, otro elemento) |
| 17 | Sin secretos; sin datos sensibles en registros | Sí (`el_fallo_no_lleva_datos_de_negocio`) |
| 18 | Dependencias nuevas existentes y fijadas | Sí: JUnit y AssertJ de prueba, versión del parent (TEC-26) |
| 19 | Nombres consistentes con el glosario | Sí (`Excepcion` documentada como «no es una excepción de Java») |
| 20 | Comentarios del porqué; sin TODO | Sí |
| 21 | Explicaciones del chat en el código | **No** → OBS-1-11: el porqué de D3 (`@JsonProperty` explícito) está en §10 pero no en el código |
| 22 | Juan resumió el flujo y se confirmó | Sí, con dos correcciones (19-oct): (1) las anotaciones van en una lista plana y cada una se juzga sola, sin cascada por bloque (A-14, A-55, A-30; en v1 se anota por sección, A-20); (2) rechazada es solo fallo técnico y se recaptura, lo de negocio entra marcada para el gerente (A-32, A-49). Si Juan quiere invalidar un bloque entero, es regla del ingreso, no del contrato |

**Resultado de la verificación (19-oct):** Coherencia: **no pasa** (COH bloqueantes: 1, COH-98) · Build: ok · Pruebas: 24/24
Completitud = 100·13/30 = **43 %** (resueltas: OBS-1-1 a 1-8 = 13; abiertas: OBS-1-9 a 1-11 y COH-98 a 103 = 17) · Explicación: pendiente.
→ Paso 7: vuelve a la etapa 5 con OBS-1-9 a 1-11 y COH-98 a 103.

**Decisión de Juan (19-oct): aceptación forzada con explicación corta.** «El contrato está lo suficientemente maduro para el primer entregable; no queremos quedarnos ahondando en lo mismo.» Lo abierto no se borra: pasa a donde se trabaja de todos modos. COH-99 → A-58, Segmento 6 · COH-98 → Segmento 6 y semilla del celular · COH-100, COH-101, COH-102 → Segmento 4 (con OBS-1-8) · OBS-1-9, OBS-1-10 → Segmento 7 · OBS-1-11, COH-103 → redacción. Resumen de Juan recibido y confirmado con dos correcciones (punto 22). OBS-1-11 y COH-103 aplicadas como redacción (dto/package-info, diccionario, A-37, comentario de ParticionRequest, leyenda del diagrama).

**SEGMENTO 1 ACEPTADO (forzado) · 19-oct-2026 · Juan.** Build ok, 24/24. Completitud de la verificación sin cumplir el umbral (riesgos repartidos a los segmentos 4, 6 y 7); explicación corta en lugar de clase por clase.

```
OBS-1-9 · issue (blocking, pruebas): campos de la petición y la respuesta sin prueba
  Ubicación: DtoJsonTest · dorados/
  Requisito: A-45 ⟦2⟧b, A-47, A-26 (accion, descartes_acusados) · lista #1
  Detalle: los dorados no traen descartes[], primeros_acuses[], descartes_acusados con valores ni una
    accion conocida; la matriz §9 los da por cubiertos solo por la forma del record.
  Criterio de cumplido: una prueba lee y escribe una petición con descartes y primeros_acuses, y una
    respuesta con descartes_acusados y "accion": "registrar_de_nuevo", y las compara con el JSON de origen.
  Origen: lista #1 · Estado: abierta
OBS-1-10 · issue (mayor, pruebas): petición sin anotaciones
  Ubicación: ParticionRequest.java (@JsonInclude ALWAYS en anotaciones)
  Requisito: A-32 (falta anotaciones[] → 422), A-47 (una partición puede traer solo descartes)
  Criterio de cumplido: prueba que escribe una partición solo con descartes y comprueba "anotaciones": [].
  Origen: lista #6 · Estado: abierta
OBS-1-11 · todo (mayor, documentación): el porqué de D3 solo está en la ESPEC
  Ubicación: paquete dto (no tiene package-info)
  Requisito: D3 · lista #21
  Criterio de cumplido: dto/package-info.java explica por qué cada campo lleva @JsonProperty explícito.
  Origen: lista #21 · Estado: abierta
```

**Revisión de coherencia, cohesión y calidad** (agente aparte, 19-oct; COH-98, 99 y 100 comprobados con una sonda propia):
```
COH-98 · confiabilidad (blocking): una rechazada puede quedar sin excepción y nadie lo nota
  Ubicación: ResultadoParticion.incoherencias() · Listas.copia
  Requisito: A-23, A-26, A-31, A-57
  Detalle: conteo.rechazadas=1 con "excepciones": [] o [null] se lee Leida e incoherencias() = []. El
    celular no sabe cuál se rechazó y, como cuenta como recibida para las marcas (A-31), podría borrarla.
  Criterio de cumplido: incoherencias() nombra «excepciones_incompletas» si hay menos excepciones de
    cada resultado que el conteo, y nombra el null quitado; prueba con los dos casos.
COH-99 · cohesión (mayor): no hay orden decidido entre LectorDto y el validador
  Ubicación: LectorDto.estricto() sobre ParticionRequest · Listas.java (supone «el validador ya rechazó»)
  Requisito: A-32, A-55, A-14 frente a A-9, A-57
  Detalle: un campo de más o un tipo malo en UNA anotación hace Fallida toda la partición, cuando A-32 y
    A-55 piden rechazar solo esa anotación; y "anotaciones" ausente da [] cuando A-32 pide 422.
  Criterio de cumplido: la ESPEC fija el orden (decisión de Juan) y el Segmento 6 lo prueba.
  Estado: regla decidida (A-58, Juan 19-oct); se programa y prueba en el Segmento 6.
COH-100 · cohesión (mayor): null en un enum da null y no DESCONOCIDO; DESCONOCIDO se escribe como null
  Ubicación: Anotacion, Descarte, Excepcion (constructores)
  Requisito: A-34, A-57
  Criterio de cumplido: los constructores cambian null por DESCONOCIDO (salvo accion, opcional); prueba con
    null en cada enum; la ESPEC dice que un DTO con DESCONOCIDO no se reescribe (se reenvían los bytes, A-33).
COH-101 · confiabilidad (menor): "contrato": "" se omite al escribir (NON_EMPTY)
  Ubicación: ParticionRequest.java:22 · Requisito: A-24, A-22 ⟦1⟧
  Criterio de cumplido: contrato y particion_uuid con NON_NULL; prueba con "".
COH-102 · coherencia (menor): el pointer de CapturaCampo es relativo a datos
  Ubicación: LectorDto.leer · Requisito: A-32, A-22
  Criterio de cumplido: leer acepta un prefijo de pointer; prueba con /anotaciones/0/datos/cama_id.
COH-103 · coherencia (menor, vocabulario): dos entradas «Lector tolerante» en el diccionario, sin «Lector
  estricto»; A-37 no nombra .dto.tipos; la leyenda del diagrama dice que todos los enums leen DESCONOCIDO
  (CodigoIngreso devuelve Optional); la Ficha sigue en «Etapa 5».
  Criterio de cumplido: una sola entrada de cada lector; A-37, leyenda y Ficha corregidas.
```

### Segmento 2 · verificación del 20-oct-2026
**Lista de 22 puntos** (`investigacion/05` §4.1). Build: ok · `clean verify` 65/65 · Docker disponible (nada se saltó) · mutación de OBS-2-1 detectada · `scripts/IA_probar-canal.sh` OK.

| # | Punto | Resultado |
|---|---|---|
| 1 | Cada requisito cubierto lleva a código y prueba | Sí (§9, filas del Segmento 2) |
| 2 | Ningún código sin padre | Sí (OBS-2-n y VS2-1 a VS2-7 de la verificación) |
| 3 | Lo de fuera nombrado con su segmento | Sí (§10: OBS-2-2, 2-3, 2-4, 2-5, 2-7, 2-13 y 2-14 con destino) |
| 4 | Sin API «por si acaso» | Sí. `AnotacionesRecibidasEnMemoria.cuantas()` la usa la prueba del servicio |
| 5 | Camino feliz de punta a punta | Sí (`CanalParticionesTest`, script real) |
| 6 | Bordes: nulo, vacío, límites, duplicados | Sí: `[null, …]`, `[5, …]`, partición vacía, duplicada dentro de la misma partición, reenvío |
| 7 | Fallos externos con comportamiento definido | Sí: base caída → 500 genérico de Spring y rollback (VS2-3, decisión de la verificación); el celular devuelve `sin_conexion`, `http_n` o `fallo_inesperado` sin lanzar (A-57). Sin prueba de la base caída → después (Toxiproxy, TEC-26) |
| 8 | Concurrencia y transacción | Sí por diseño: `ON CONFLICT` en la base y una transacción por partición. Sin prueba concurrente → después |
| 9 | Las pruebas fallarían si el código se rompe | Sí: mutación de `soloObjetos` → `CanalParticionesTest` falla en `[null, …]` |
| 10 | Pruebas no debilitadas | Sí (sin `@Disabled`; `disabledWithoutDocker` es un salto condicionado y hoy no se saltó nada) |
| 11 | Build y pruebas pasan | Sí |
| 12 | Arquitectura y fronteras | Sí: `ModulosTest` verde; empaquetado D-18 en ingesta y simulador. La regla hacia adentro dentro del módulo no se verifica sola todavía (ArchUnit, Segmento 7) |
| 13 | Sin sobreingeniería | Sí, con nota: `SincronizarParticionServicio` solo delega hoy; existe por la decisión de Juan (hexagonal completa) y recibirá el almacén de salida |
| 14 | Reutilización | Sí, con la nota ya abierta de OBS-2-4 (copias de las doradas) |
| 15 | Entradas validadas en el servidor | Sí: `arbolONull`, `formaMinima` (+ `soloObjetos`), lector estricto, UUID de la ruta |
| 16 | Autorización | N. A. en el camino mínimo: cualquiera en la red de la finca puede hacer el POST; la credencial (TEC-13) está en «después» |
| 17 | Sin secretos; registros sin datos sensibles | Sí en el segmento. Fuera de él: `application.yaml` trae `${FLORLOGIC_DB_CLAVE:florlogic}`, clave por defecto de desarrollo (anterior al segmento) → §7 |
| 18 | Dependencias nuevas | Sí: ninguna (los `pom.xml` no cambiaron; VS2-4 evitó `spring-boot-jdbc-test`) |
| 19 | Nombres consistentes con el glosario | Sí (`NodoDeFinca` = «Nodo de finca» del diccionario) |
| 20 | Comentarios del porqué; sin TODO | Sí (sin TODO; cada clase nueva dice su porqué y su fuente) |
| 21 | Explicaciones del chat en el código | **No → OBS-2-16, aplicada en el acto**: el porqué de `formaMinima` frente al lector estricto (lo que falta y lo que sobra) y de `path` frente a `get` solo estaba en el chat; ahora está en su Javadoc |
| 22 | Juan resumió el flujo y se confirmó | Pendiente (paso 6, explicación) |

```
OBS-2-16 · todo (mayor, documentación): el porqué de formaMinima solo estaba en el chat
  Ubicación: ControladorParticiones.formaMinima · Requisito: lista #21
  Criterio de cumplido: el Javadoc explica qué atrapa frente al lector estricto y por qué path.
  Origen: lista #21 · Estado: cumplida 20-oct
```

```
OBS-2-17 · question (menor, diseño) · después → ronda de la plantilla entidad-agregado: AnotacionRecibida es un record, no una entidad
  Ubicación: ingesta/dominio/AnotacionRecibida.java
  Requisito: plantilla entidad-agregado §0.8 (sin invariantes → no hay agregado), E-07, E-08
  Detalle: pregunta de Juan (20-oct): «¿por qué record en dominio y no clases de dominio y entidad que se comunican con
    el repositorio?». En el camino mínimo la anotación no tiene ciclo de vida ni reglas: solo se guarda. Un record tiene
    igualdad por todos sus campos, y una entidad la tiene por su identidad (Evans, DDD · p. 118); hoy nada compara
    anotaciones, así que no rompe el canal. Tampoco cumple E-08 (id tipado: usa UUID crudo).
  Criterio de cumplido: cuando lleguen el juicio y las marcas, la ronda de entidad-agregado decide el agregado (anotación
    o sesión), su id tipado y su ciclo de vida, y AnotacionRecibida pasa a ser esa entidad o un valor dentro de ella.
  Origen: pregunta de Juan (20-oct) · Estado: respondida; después
```

**Revisión de coherencia, cohesión y calidad** (agente aparte, 20-oct; resultado: **pasa, 0 bloqueantes**; ningún hallazgo rompe el camino mínimo). Comprobado por la sesión principal el 20-oct: COH-105 contra PostgreSQL 16 (`ERROR: unsupported Unicode escape sequence`), COH-104 con Jackson 3.1.5 (`{"a":1.50,"x":12.345678901234567890123,"a":2}` → `{"a":2,"x":12.345678901234567}`) y `jsonb` reordena las claves (`{"b":1,"a":2.50}` → `{"a": 2.50, "b": 1}`).
```
COH-104 · confiabilidad (mayor) · después → Segmento 3: «tal como llegó» no es cierto: se guarda JsonNode.toString() en jsonb
  Ubicación: ControladorParticiones.java (bucle) · V2__anotacion_recibida.sql:2,9 · AnotacionRecibida.java:14
  Detalle: una clave repetida se pierde, un decimal largo pierde precisión y jsonb reordena; los bytes que A-33 necesita
    para la huella no se pueden recuperar.
  Criterio: ya, comentarios «JSON equivalente, no los bytes»; en el Segmento 3, guardar los bytes de cada anotación,
    USE_BIG_DECIMAL_FOR_FLOATS y STRICT_DUPLICATE_DETECTION (clave repetida → 422), con prueba.
  Estado (20-oct, Juan): guardar los bytes **descartado** (A-59); comentarios corregidos (redacción). Queda para el
    Segmento 4: la clave repetida (hoy gana la última, sin aviso) y el decimal largo, como límites del lector.
    V2__anotacion_recibida.sql:2 sigue diciendo «tal como llegó»: es una migración aplicada y no se edita (PR-14, checksum de Flyway).
COH-105 · confiabilidad (mayor) · después → Segmento 4: un \u0000 legal en el JSON da 500 en cada intento
  Ubicación: AnotacionesRecibidasJdbc.java (CAST … AS jsonb)
  Detalle: el lector estricto lo acepta y PostgreSQL lo rechaza: rollback y 500 genérico; la partición no entra nunca.
  Criterio: 422 «rechazado» (o guardar como texto), probado con 0 filas.
  Estado: **subida y cumplida 20-oct** (Juan): ControladorParticiones.guardable recorre nombres y textos de cada anotación;
    CanalParticionesTest con U+0000 en un valor y en un nombre → 422 y 0 filas; mutación: sin el filtro, 500.
COH-106 · cohesión, pruebas (mayor) · después → con COH-104 y COH-105: el contrato del puerto solo compara el boolean
  Ubicación: AnotacionesRecibidasContratoTest · AnotacionesRecibidasEnMemoria
  Detalle: no hay caso «guardar y recuperar igual» (PR-19), ni un caso que PostgreSQL rechace y la memoria acepte; el
    doble no tiene rollback, así que la atomicidad que promete RecibirParticionServicio no se prueba abajo.
  Criterio: el contrato verifica lo guardado y el caso \u0000 da lo mismo en las dos; o el Javadoc baja la promesa.
COH-107 · coherencia (menor) · después → Segmentos 5 y 6: falta consecutivo → se guarda 0 con 200 (decisión implícita sin escribir)
COH-108 · vocabulario (menor) · después → Segmento 4, con OBS-2-7: «rechazada» en el simulador (partición con 422) choca
  con «Anotación rechazada» del diccionario; el type del servidor es «rechazado».
COH-109 · vocabulario (menor) · redacción: D1-D7 de la verificación no tienen entrada en la Nomenclatura y chocan con D1
  (granularidad de la jornada) y con D3 del Segmento 1 (D-09).
  Estado: cumplida 20-oct (redacción): prefijo VS2-n registrado en la Nomenclatura y definido abajo; código y ESPEC citan VS2-n.
COH-110 · coherencia (menor) · redacción (D-16): IA_segmento-2.drawio (clases) todavía dibuja Sincronizar → ClienteIngesta
  sin puertos; el de verificación dice «4 → 5 pruebas» (son 4) y muestra 3 casos de contrato (son 4).
  Estado: cumplida 20-oct (redacción): las dos hojas iguales al código, con COH-105 y A-59; PNG regenerados.
COH-111 · confiabilidad (menor) · después → Segmento 7: el script imprime «Canal OK» sin comprobar conteos ni códigos.
COH-112 · coherencia (nit) · decide Juan: las pruebas con Docker se llaman …Test (Surefire) y la plantilla pruebas §2.5
  pide …IT (Failsafe); renombrar o escribir la desviación en IA_LEEME.
```

**Completitud (indice-precision §2, medida sobre el camino mínimo, D-17):** observaciones del alcance del segmento: OBS-2-1 (3), 2-9 (3), 2-12 (3), 2-11 (2), 2-16 (2), 2-6 (1), 2-15 (1), 2-10 (0,5), 2-8 (0,5) = 16, todas resueltas → **100 %**. Sin el filtro de D-17 (contando también las «después», 6 de OBS y 11,5 de COH) sería 16/33,5 = 47,8 %.

**Decisiones implícitas de la verificación del Segmento 2** (`VS2-n`, aprobadas por Juan con el plan el 20-oct; COH-109):
| Id | Decisión | Fuente |
|---|---|---|
| VS2-1 | `NodoDeFinca.enviar` recibe la URI del nodo en cada llamada (viene de `--url`), no de la configuración | [sin fuente] |
| VS2-2 | Los `servicios` del simulador usan tipos del contrato (`Lectura`, `ResultadoParticion`); la prohibición es solo para `dominio` | CLAUDE.md · A-44 |
| VS2-3 | Si la base falla a mitad, la partición entera se deshace (`@Transactional`) y responde el 500 genérico de Spring | `RecibirParticionServicio` · D-17 (error inesperado → genérico) |
| VS2-4 | `AnotacionesRecibidasJdbcTest` sin Spring (`JdbcClient.create` + Flyway): `@JdbcTest` vive en un módulo de Spring Boot 4 sin fila TEC | CLAUDE.md (ninguna dependencia sin fila) |
| VS2-5 | `ClienteIngesta`: constructor sin argumentos para Spring y otro con `HttpClient` para la prueba | [sin fuente] |
| VS2-6 | El script usa su propio proyecto de compose (`florlogic-canal`) | OBS-2-15 |
| VS2-7 | `SincronizarTest` entra aunque no se pidió: fija los códigos de salida que usa el script | plantilla pruebas §0.7 |

## 12 · Traspaso del canal (D-19, 20-oct-2026)

El Segmento 2 es código de otros elementos (plantillas endpoint ⟦11⟧, caso-de-uso y adaptador-persistencia). Por D-19 deja
de crecer aquí y pasa a sus semillas, que arrancan desde este código (sección «Punto de partida» de cada una):

| Archivos del canal | Elemento dueño | Semilla | Viajan con él |
|---|---|---|---|
| `ingesta/infraestructura/entrada/web/ControladorParticiones`, `servicios/puertos/entrada/*`, `servicios/casosuso/RecibirParticionServicio`, `dominio/AnotacionRecibida` y sus pruebas | Caso de uso del ingreso (y endpoint ⟦11⟧) | `IA_SEMILLA-caso-de-uso-ingreso.md` | A-59, OBS-2-17, COH-107, OBS-2-7, COH-108 |
| `V2__anotacion_recibida.sql`, `servicios/puertos/salida/AnotacionesRecibidas`, `infraestructura/salida/persistencia/AnotacionesRecibidasJdbc` y sus pruebas | Adaptador de persistencia del ingreso | `IA_SEMILLA-adaptador-persistencia-ingreso.md` | A-59, COH-104 (resto), COH-106, OBS-2-9 (ArchUnit) |
| `simulador/**` (puertos, servicio, `Sincronizar`, `ClienteIngesta`) y sus pruebas | Caso de uso del celular | `IA_SEMILLA-caso-de-uso-celular.md` | VS2-1, OBS-2-5, OBS-2-13, OBS-2-14, COH-111 |
| `scripts/IA_probar-canal.sh` | Prueba de punta a punta de TX-1 | `pruebas-extremo-a-extremo` (Segmento 7) | COH-111 |

**Estado del Segmento 2 al traspasarse:** verificado el 20-oct (65/65 con Docker, coherencia pasa, completitud 100 % sobre
el camino mínimo). **La aceptación espera la explicación de Juan** (paso 6, ≥80 %): Juan pasó el 20-oct a detallar el
dominio de ingesta en un chat nuevo; la explicación puede darse ahí, sobre las clases de la tabla de arriba.

**Lo que queda en esta ESPEC** (el contrato, hasta `IMPLEMENTADA`): Segmentos 3 a 7 y la ronda 2 (parte 2: `/sesiones`,
`/cierre`, `/archivos`).

