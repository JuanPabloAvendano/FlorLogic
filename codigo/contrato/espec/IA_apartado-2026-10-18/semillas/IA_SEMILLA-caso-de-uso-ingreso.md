# SEMILLA · caso de uso · ingreso de una partición en el nodo de finca
Qué es: lo que decide el nodo de finca con cada anotación recibida, apartado de la ESPEC del contrato de ingesta porque no cambia la forma de los mensajes.
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md` (rondas 1-1h), su HISTORIA y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `ADR.xlsx` · `docs/03-arquitectura/decisiones/` · `IA_TX1-sincronizacion.md` · la palabra de Juan.

## Ficha
| Campo | Valor |
|---|---|
| Elemento | Caso de uso «ingresar partición» del `backend-finca` (capa `servicios`) |
| Plantilla | `caso-de-uso` |
| Definición propuesta | Toma una partición que el contrato ya validó y decide qué pasa con cada anotación: si entra, si ya estaba, si llegó tarde, si la supera otra, qué marcas lleva y cómo se mueven las marcas de agua. **No** define la forma de los mensajes (eso es el contrato) ni cómo se guarda (adaptador). *Sin convergencia con Juan todavía* |
| Dominio | Entra: partición validada, catálogo de la versión, asignaciones · Sale: resultado por anotación, conteos, marcas de agua, constancias, alertas |


## Punto de partida: el canal ya implementado (traspaso D-19, 20-oct-2026)

Esta semilla **no arranca desde cero**. El Segmento 2 de `ESPEC-contrato-ingesta.md` (el canal de TX-1, camino mínimo
D-17) ya escribió, probó y verificó una versión mínima de este elemento. Su ESPEC parte de ese código y lo hace crecer.
Detalle: ESPEC-contrato-ingesta §12 (nota de traspaso) y §10-§11 (observaciones y verificación del Segmento 2).

| Archivo ya escrito | Qué hace hoy |
|---|---|
| `backend-finca/…/ingesta/servicios/puertos/entrada/RecibirParticionPuertoEntrada`, `RecibirParticionComando`, `ParticionRecibidaResultado` | Puerto de entrada, comando y resultado |
| `…/ingesta/servicios/casosuso/RecibirParticionCasoUso` | Guarda cada anotación una vez y cuenta aplicadas y sin efecto, en una transacción. **Sin juicio**: nada se marca ni se rechaza por anotación |
| `…/ingesta/dominio/AnotacionRecibida` | `record` sin conducta (OBS-2-17: todavía no es entidad ni agregado) |
| `…/ingesta/infraestructura/entrada/web/ParticionesAdaptadorWeb` | Lee con el lector estricto, `422` «rechazado» para todo fallo de forma, `confirmado_hasta` = mayor consecutivo (simplificación de A-31) |
| Pruebas: `RecibirParticionCasoUsoTest`, `CanalParticionesTest` | Unitaria con doble en memoria e integración amplia |

Viajan con el traspaso: **A-59** (se guarda el JSON equivalente, no los bytes), **OBS-2-17** (record → entidad/agregado,
id tipado E-08), **COH-107** (falta `consecutivo` → se guarda 0), OBS-2-7 y COH-108 (tipo de problema «rechazado» y
vocabulario «rechazada»). Lo que el canal **no** hace y esta ESPEC decide: el juicio de cada anotación (aplicada,
marcada, rechazada), las marcas de agua y `confirmado_hasta` real (A-31), la sesión `{id}` verificada, y los avisos a
quien corresponda (módulo `notificacion`).

## Lo que ya se sabe (decidido en el detallamiento del contrato)
| Origen | Regla | Fuente |
|---|---|---|
| A-2 | Tres casos por anotación: mismo id y misma huella → `sin_efecto`; mismo id y otra huella → corrupta (`rechazada`, alerta, cuarentena de los bytes, nunca se sobrescribe); otro id → lo juzgan las reglas | ronda 1g P6, elección de Juan |
| A-2 | Orden de juicio: ⟦1⟧ ¿el id existe, como anotación o constancia? ⟦2⟧ forma ⟦3⟧ ¿la orden ya se cumplió?, solo si `version_config` es conocida ⟦4⟧ reglas | ronda 1g P10, 1h P9 |
| A-3, A-11 | La clave del hecho la declara cada campo en el catálogo (por defecto producción · sección · campo · jornada) y la calcula el ingreso | ronda 1d P2 |
| A-45 ⟦1⟧ | **Obsoleta y superada.** Si la orden de captura (cama + jornada, ADR-026) ya la cumplió otra captura posterior (sello normalizado) y verificada (sin ningún código de marca), por el mismo (sección, campo): la tardía cuenta `sin_efecto` + `obsoleta`, no se guarda y queda una constancia; si la vieja ya estaba guardada pasa a **superada** y su constancia conserva los bytes | rondas 1e P3, 1f P2-P4, 1g P2, 1h P9 |
| A-45 ⟦1⟧ | Solo aplica cuando la orden se **reasignó** a otro dispositivo o persona; en cualquier otro caso decide RF-022 (gana la más reciente; con el mismo sello, el `anotacion_uuid` mayor; se avisa a quien perdió) | ronda 1g P1 (K-52) |
| A-45 ⟦1⟧ | La orden la deduce el nodo de finca: sección → cama por el catálogo, la jornada, y «el dispositivo o el usuario al que se le asignó» (Juan, 17-oct 03:17). Sin asignación → RF-022 y `fuera_de_asignacion` | ronda 1h P8 (K-72) |
| A-45 ⟦1⟧ | Si la posterior trae cualquier marca o alguna tiene hora dudosa: entran las dos `marcadas` con `orden_duplicada`; el estado lo decide RF-022 y el caso queda en el tablero | ronda 1h P7 (K-71) |
| A-45 ⟦2⟧ | `vieja`: «primera llegada − sello normalizado» > `edad_vieja` (3 días) | rondas 1d P3, 1f P8 |
| A-17 | `hora_dudosa`: dentro de un arranque, `|Δcrudo_ms − Δmono_ms|` pasa el umbral (umbral pendiente de Juan) | SPK-12 M2b · IA_TX1 paso 11 |
| A-6 | Sello normalizado = crudo + desfase declarado | ADR-031 §2 |
| A-26 | El sello del evento es el de la primera sesión que lo aplicó; tras restaurar, la primera que lo aplica después (propuesta para ADR-028, K-28) | ADR-028 §3 |
| A-27, A-31 | `confirmado_hasta` = mayor consecutivo sin huecos; rechazadas y constancias cuentan como recibidas. Un hueco se cierra con un aviso de descarte o por el administrador, nunca por plazo. Si se restaura por debajo de lo anunciado, se calcula la posible pérdida por celular y se alerta (lista de camas a rehacer) | rondas 1b-1g |
| A-32, A-35 | Lo que se juzga con el catálogo o las reglas (rango, motivo fuera del catálogo, desviación) entra `marcada` con el id de la regla; se agrega `divergencia` **solo si el nodo de finca juzga distinto que `veredicto_local`** (el celular dijo `aceptada` y el nodo de finca la marca); el producto faltante lo decide una regla del nodo de finca | rondas 1b-1e · 1k P9 (★) |
| A-39 | Mismo (dispositivo, consecutivo) con otro id → `consecutivo_repetido`, alerta, `registrar_de_nuevo` una sola vez por dispositivo | rondas 1c, 1d |
| A-42 | Una sola alerta por (`anotacion_uuid`, huella) | ronda 1c P16 |
| A-41 (1i P7, 1k P4-P5) | Contar los `422 sobre-invalido` por `particion_uuid` guardando la huella SHA-256 de cada cuerpo, y alertar `sobre_invalido_repetido` **una vez**, cuando aparece el **segundo cuerpo distinto** (un reintento con el mismo cuerpo no cuenta), con su `regla` y `pointer`. Es la **única** fuente de la alerta (el campo `rearmado` salió del contrato). `cuerpo_incompleto` no cuenta (A-54). La cuenta sobrevive a reinicios. Riesgo anotado: un rearmado idéntico no alerta (K-93) | ronda 1i P7 (COH-69) · ronda 1k P4-P5 (Juan) · segunda poda |
| A-56 | Quien ve la anomalía la avisa una vez: el nodo de finca alerta una vez por clave (`particion_uuid` o, si no se lee, la huella del cuerpo) | segunda poda 18-oct (regla general, Juan) |
| A-45 ⟦3⟧ (1j P9) | Tras una restauración, un aviso `descartada_por_gerente` cuya orden se perdió se acepta y deja la constancia `descarte_sin_orden` en la bitácora; no viaja en la respuesta (solo `descartes_acusados`) | ronda 1f P10 · ronda 1j P9 (K-86) |
| A-49 ⟦4⟧ | Desviación detectada sin `motivo_desviacion` → `marcada` con el id de la regla y `desviacion_sin_motivo` | ronda 1e P5 |

## Casos que ya existen
- Apartados aquí (texto en `ESPEC-contrato-ingesta-HISTORIA-1g-1h.md` §4): C-5, C-33, C-37, C-38, C-60, C-61, C-70, C-71, C-72, C-81.
- Siguen en la ESPEC del contrato, porque muestran la respuesta: C-21, C-29, C-30, C-32, C-53, C-54, C-55, C-59, C-64, C-65, C-75.

## Abierto (sale de ESPEC §6)
- **COH-61 (bloqueante)** · ¿manda la persona o el dispositivo cuando un celular compartido reasigna la cama entre dos personas? Juan dijo «el dispositivo o el usuario al que se le asignó». Propuesta del agente: manda la persona de la sesión de captura.
- **COH-64 (decide Juan)** · D-099 sin asignación captura después que D-022 asignada: ¿gana la asignada aunque sea más vieja (excepción a RF-022)?
- **COH-62** · toda constancia debería conservar los bytes, no solo la de una superada.
- **COH-52** · qué estado sobrevive a una restauración: deduplicación de alertas, `registrar_de_nuevo` ya pedido.
- **COH-26** · ¿se avisa a quien quedó `obsoleta`, como RF-022 avisa a quien perdió?
- **K-104** (segunda poda) · celular más estricto: `veredicto_local: marcada` y ninguna regla del nodo de finca la marca. Regla general propuesta (inferido, confirmar): los motores difieren en cualquier sentido → `divergencia`, sin id de regla.
- **COH-92** (segunda poda) · `sin_minimo` con `veredicto_local: marcada` lleva `divergencia` aunque los veredictos coincidan: propuesta, excepción explícita (siempre es fallo del motor).
- **K-105** (segunda poda) · clave de la memoria cuando no se lee el `particion_uuid`: A-56 propone la huella del cuerpo.
- **COH-93** (segunda poda) · alertar cuando un hueco de consecutivos dura más de 1 h (cubre el rearmado idéntico sin alerta; falsos positivos con celulares aún entregando).
- **K-82** · ¿de quién son las anotaciones que reenvía otro (buzón del gerente, nodo de contingencia)?
- **K-40** · obsoleta y superada chocan con ADR-027 §2, ESC-34, CN-24, ADR-024 §1 y ESC-58 (ver `documentacion/docs/contenido-pendiente/`).

## Qué no es
No es el contrato (forma, versión, errores) ni la persistencia (`ON CONFLICT`, índices: ver la semilla del adaptador).
