---
plantilla: trabajo-programado
aplica_a: trabajo recurrente por calendario (cron o intervalo), trabajo único diferido y cola de trabajos persistida en la base (respaldos, recálculos, vencimientos, purgas, reintentos, reenvío de publicaciones fallidas)
entra_desde: caso-de-uso (el disparador «temporizador» de su P2) · evento-mensaje (reenvío de fallidos, purga de publicaciones)
sale_hacia: caso-de-uso (el trabajo invoca un puerto de entrada) · adaptador-persistencia (tabla de la cola, tabla de bloqueos, índices) · parametro-configuracion (cron, zona, lote, topes, interruptor)
---
# Plantilla · Trabajo programado o de cola

## 0 · Instrucciones para el agente

1. Usa esta plantilla cuando lo que se detalla **se ejecuta sin que nadie lo pida en ese momento**: por
   reloj (cron, intervalo) o porque hay ítems pendientes en una cola persistida. En el hexágono es un
   **adaptador primario** (de entrada): traduce «sonó el reloj» o «hay un ítem» en un comando al puerto de
   entrada de un caso de uso (`investigacion/02` §2; Cockburn, *Hexagonal Architecture*).
2. Antes de la P1, muestra el esqueleto de §1 con los huecos ⟦1⟧…⟦13⟧ y la opción ★ como valor provisional
   (`investigacion/04` §2.3).
3. Haz primero la P2 (tipo de disparo) y la P8 (instancias). Son las que más podan (§6).
4. Un trabajo por ronda. Si el humano describe dos («cancela vencidos y además manda el resumen»), registra
   el segundo como pendiente: son dos adaptadores y dos casos de uso (`investigacion/01` regla 1).
5. Las reglas de negocio del trabajo (qué es «vencido», qué se hace con cada ítem) **no** se detallan aquí:
   se confirman en una línea y van a `caso-de-uso`. Aquí solo se decide cuándo, cuántos, cuántas veces, con
   qué tope, qué pasa si falla y cómo se observa.
6. La tabla de la cola, la de bloqueos y sus índices se nombran aquí; su DDL fino, el mapeo y las
   migraciones van a `adaptador-persistencia`. Los valores (cron, lote, topes) se nombran aquí y su
   validación va a `parametro-configuracion`.
7. No inventes cifras. Si el humano no sabe la duración esperada, el volumen o la ventana, anota un
   *hotspot* («Q-4: ¿cuántos pedidos vencen por noche? supuesto: < 10 000»), asume la ★ y sigue
   (`investigacion/03` regla 18).
8. Cada garantía (ejecución única, idempotencia, fallo definitivo) sale con un caso dado/cuando/entonces que
   la prueba. Ejemplo: «dado un pedido ya cancelado, cuando el trabajo corre otra vez, entonces no cambia
   nada y `procesados=0`».
9. Si el trabajo es un ETL grande con pasos, reinicio por *chunk* y particiones, dilo y propone Spring Batch
   como motor, con un ADR (§2, tabla de motores). Esta plantilla cubre el adaptador y sus garantías, no el
   diseño de un pipeline por pasos.
10. Si queda un cupo libre al final, úsalo en la metapregunta «¿hay algo que debí preguntarte sobre este
    trabajo?» (`investigacion/04` regla 18).

## 1 · Qué se define primero

El orden sigue la dependencia: sin resultado de negocio no hay criterio de selección, y sin criterio no se
puede decidir la idempotencia. Los tres primeros son esenciales (regla dura de cobertura,
`investigacion/04` §5.1).

| ⟦k⟧ | Concepto | Por qué va aquí | Fuente |
|---|---|---|---|
| 1 | **Resultado de negocio y caso de uso** que dispara | Un trabajo que «procesa cosas» no se puede probar ni alertar. | `investigacion/04` §3.7 P1 |
| 2 | **Disparo**: recurrente (cron/intervalo), único diferido o cola persistida; zona horaria | Decide el motor y la forma del adaptador. | Spring, *Task Execution and Scheduling*; db-scheduler README |
| 3 | **Criterio de selección** autocurativo y **unidad de trabajo** | Un criterio por estado y umbral recupera solo lo que no se hizo; un ítem por transacción aísla los fallos. | SRE cap. 24; [opinión] |
| 4 | **Motor**: `@Scheduled`, `@Scheduled`+ShedLock, db-scheduler, Quartz | Cada uno resuelve un problema distinto (bloqueo, persistencia, calendario). | READMEs de ShedLock y db-scheduler; Quartz *Clustering* |
| 5 | **Ejecución única y solapamiento** | Con más de una réplica, cada una dispara su propio `@Scheduled`. | ShedLock README; Spring *Scheduling* |
| 6 | **Idempotencia y garantía de entrega** | Los bloqueos por tiempo y los reintentos producen repeticiones: la entrega real es «al menos una vez». | ShedLock *Caveats*; AWS Builders' Library |
| 7 | **Reintentos**: transitorio frente a permanente, backoff exponencial con *jitter* y tope, una sola capa | Los reintentos son «egoístas» y se amplifican capa sobre capa. | Brooker, AWS Builders' Library; AWS *Exponential Backoff and Jitter* |
| 8 | **Fallo definitivo**: apartar (estado `FALLIDO` o *dead letter*) con causa y alerta | Un ítem envenenado no debe bloquear la cola ni perderse. | EIP, *Dead Letter Channel* |
| 9 | **Tiempos y aislamiento**: timeout por llamada, duración máxima, apagado ordenado, pool propio | Sin timeout un hilo se cuelga para siempre; el planificador por defecto tiene un hilo. | Nygard, *Release It!* (Timeouts, Bulkheads); Spring Boot |
| 10 | **Ejecuciones perdidas y muertas** | El servidor puede estar caído a la hora del cron o morir a mitad. | Quartz (misfire); db-scheduler (*dead executions*) |
| 11 | **Reloj inyectable** | El umbral «más de 48 h» se prueba con un `Clock` fijo, no esperando 48 h. | `investigacion/02` #21; `java.time.Clock` |
| 12 | **Observabilidad y alertas por edad** | «La métrica clave de un batch es la última vez que tuvo éxito». | Prometheus, *Instrumentation*; SRE cap. 6 |
| 13 | **Estado estable**: qué acumula el trabajo (o su cola) y quién lo purga | Todo mecanismo que acumula un recurso necesita otro que lo recicle. | Nygard, *Release It!* (Steady State) |

Esqueleto que se muestra antes de la P1:

```
Trabajo:        ⟦1⟧  → «cancela los pedidos CREADO con más de 48 h» · CancelarPedidosVencidos (puerto in)
Disparo:        ⟦2⟧  → recurrente · cron "0 15 2 * * *" · zona America/Bogota
Selección:      ⟦3⟧  → estado = CREADO y creado_en < ahora − 48 h · lotes de 100 · 1 pedido por transacción
Motor:          ⟦4⟧  → @Scheduled + ShedLock (JdbcTemplateLockProvider, usingDbTime)
Ejecución única:⟦5⟧  → @SchedulerLock(name="cancelar-pedidos-vencidos", lockAtMostFor="30m")
Idempotencia:   ⟦6⟧  → por estado: solo cancela si sigue CREADO · al menos una vez
Reintentos:     ⟦7⟧  → no reintenta dentro de la pasada; la siguiente pasada recoge lo pendiente
Fallo definitivo:⟦8⟧ → tras 5 pasadas fallidas, PedidoMarcadoParaRevision + alerta
Tiempos:        ⟦9⟧  → tope 20 min, corta al terminar el lote en curso · pool "jobs-" de 2 hilos
Perdidas:       ⟦10⟧ → criterio autocurativo: la siguiente pasada procesa lo atrasado
Reloj:          ⟦11⟧ → Clock inyectado; pruebas con Clock.fixed
Observabilidad: ⟦12⟧ → trabajo_ultimo_exito_segundos · procesados/fallidos · alerta si > 26 h sin éxito
Estado estable: ⟦13⟧ → no acumula (la tabla shedlock tiene 1 fila por trabajo)
```

## 2 · Funcionamiento apropiado

**Responsabilidad.** El adaptador de trabajo **dispara y no decide**. Calcula los parámetros del disparo (el
instante, el tamaño de lote), llama a un puerto de entrada y registra el resultado. El caso de uso
selecciona, aplica las reglas y guarda. Así el mismo caso de uso se puede lanzar desde un endpoint de
administración o desde una prueba sin el planificador (`investigacion/02` §2; §9.4 HalcyonTech).

**Flujo de un trabajo recurrente (★):**

```
planificador (Spring)          adaptador in (scheduling)              aplicación                       salida
─────────────────────          ─────────────────────────              ──────────                       ──────
cron 02:15 Bogotá ──▶ CancelarVencidosJob.ejecutar()
                      1 ShedLock: ¿tengo el bloqueo? ── no ──▶ se salta (otra réplica lo tiene)
                      2 sí ──▶ caso.ejecutar(new Comando(lote=100, tope=20m))
                                                        3 umbral = clock.instant() − 48 h
                                                        4 ids = pedidos.vencidos(umbral, 100) ─▶ SELECT … LIMIT 100
                                                        5 por cada id, en su propia transacción:
                                                          cargar → pedido.cancelarPorVencimiento(ahora)
                                                          (si ya no está CREADO: no hace nada) ─▶ UPDATE … version
                                                        6 repetir 4-5 hasta vacío o tope
                      7 ◀── Resultado(procesados, omitidos, fallidos)
                      8 métricas + log estructurado; si fallidos > 0, no lanza: la alerta va por métrica
```

**Flujo de una cola persistida** (db-scheduler o tabla propia con `SKIP LOCKED`):

```
caso de uso A (en su transacción)          planificador / sondeo                  caso de uso B
──────────────────────────────────         ──────────────────────                 ─────────────
guarda Pedido + encola                      cada 10 s: reclama N ítems con
"notificar-despacho:p-01" ── commit ──▶     FOR UPDATE SKIP LOCKED (o versión)
(si hay rollback, el ítem no existe)        por ítem: B.ejecutar(cmd) ─────────▶ llama a la transportadora
                                            ok      → borra/HECHO                 (timeout 2 s)
                                            falla   → reprograma ahora + backoff con jitter
                                            n fallos→ FALLIDO + causa + alerta
                                            nodo muere → latido viejo → se revive
```

El encolado en la misma transacción del cambio de negocio garantiza que «se encola si y solo si hay
commit» (§9.2). Es el mismo razonamiento del outbox (`evento-mensaje`).

**Dónde vive cada pieza** (`investigacion/02` §2):

| Pieza | Paquete | Depende de |
|---|---|---|
| Clase con `@Scheduled`/`@SchedulerLock`, o `@Bean Task<…>` de db-scheduler | `pedidos.adapter.in.scheduling` | puerto de entrada, Micrometer, Spring |
| Puerto de entrada y comando (`CancelarPedidosVencidos`, `record Comando`) | `pedidos.application.port.in` | tipos de dominio |
| Caso de uso (selección por lotes, transacción por ítem, `Clock`) | `pedidos.application` | puertos de salida, `Clock` |
| Puerto para encolar (`ProgramadorDeTareas`) si un caso de uso encola | `pedidos.application.port.out` | — |
| Implementación del puerto con `SchedulerClient` o con SQL `SKIP LOCKED` | `pedidos.adapter.out.scheduling` | db-scheduler / `JdbcClient` |
| `LockProvider`, pool del planificador, `Clock`, propiedades | `pedidos.config` (o `shared.config`) | Spring |

**Elegir el motor.** Es la decisión de la P3.

| Motor | Qué resuelve | Úsalo cuando | No lo uses cuando | Fuente |
|---|---|---|---|---|
| `@Scheduled` solo | Disparo local por cron, `fixedDelay` o `fixedRate` | hay **una** instancia garantizada, o el trabajo es local a cada nodo (refrescar una caché) | hay varias réplicas y el efecto es global | Spring, *Scheduling* |
| `@Scheduled` + ShedLock ★ (recurrente) | «Como máximo una vez **al mismo tiempo**» entre nodos; los demás nodos **se saltan** la ejecución | trabajos recurrentes que «se pueden ejecutar repetidamente sin daño» | necesitas persistir cada ítem, reintentos por ítem o trabajos únicos diferidos: «ShedLock no es un planificador distribuido» | ShedLock README |
| db-scheduler ★ (cola o único diferido) | Ejecuciones persistidas en **una** tabla; un solo nodo toma cada una; latidos, reintentos y *dead executions* | tareas por ítem («notificar el pedido p-01 a las 10:00»), reintentos con backoff, encolar dentro de la transacción | basta un cron simple sin estado (ShedLock es más liviano) [opinión] | db-scheduler README |
| Quartz (JDBC JobStore en clúster) | Calendarios ricos, *misfire instructions*, `@DisallowConcurrentExecution` | ya está en el proyecto, o necesitas calendarios de exclusión y políticas de misfire | quieres algo simple: db-scheduler nació porque «añadir 11 tablas» era excesivo | Quartz docs; db-scheduler FAQ |
| Spring Batch | Pipeline por pasos con reinicio por *chunk* | ETL o importaciones grandes con reanudación | un trabajo de pocos minutos y una consulta | [opinión] |
| Tabla propia + `SKIP LOCKED` | Cola a medida con consumidores que compiten | necesitas un esquema propio (estados de negocio, prioridad) o ya tienes la tabla | db-scheduler ya cubre el caso: no reinventes latidos ni reintentos [opinión] | PostgreSQL, `SELECT … SKIP LOCKED` |

**Garantías que conviene decir en voz alta** (salen de las fuentes; se repiten en §4):

- ShedLock: «si una tarea ya se está ejecutando en un nodo, la ejecución en los otros no espera: se salta».
  Si la tarea dura más que `lockAtMostFor`, «se puede ejecutar más de una vez». Si la diferencia de relojes
  supera `lockAtLeastFor`, también. → el trabajo **tiene** que ser idempotente.
- db-scheduler: usa bloqueo optimista o `select for update` para que «una y solo una instancia» tome cada
  ejecución; pero si el nodo muere a mitad, la ejecución se revive (`ReviveDeadExecution`) y **vuelve a
  correr**. → idempotente otra vez.
- db-scheduler, recurrentes: «no hay garantía de que se ejecuten todos los instantes de un `Schedule`»;
  se calcula el siguiente al terminar. → el criterio de selección debe ser autocurativo.
- Google SRE (cron distribuido): ante la duda, «preferimos saltarnos lanzamientos antes que arriesgar
  lanzamientos dobles» (*fail closed*), y los dueños «pueden (¡y deben!) monitorear sus cron jobs».

## 3 · Patrones de diseño

| Patrón | Problema que resuelve | Úsalo cuando | No lo uses cuando | Fuente |
|---|---|---|---|---|
| Adaptador primario de temporizador | Separar el disparo del caso de uso | siempre | — | Cockburn; `investigacion/02` §2 |
| Bloqueo distribuido con arrendamiento (*lease*) | Una sola ejecución a la vez entre réplicas | recurrentes con varias réplicas | el trabajo es local a cada nodo | ShedLock README |
| Cola persistida / *Competing Consumers* | Repartir ítems entre nodos sin duplicar la toma | trabajos por ítem, volumen variable | un solo cron global basta | EIP, *Competing Consumers*; PostgreSQL `SKIP LOCKED` |
| *Polling Consumer* | El consumidor decide cuándo leer | la fuente es una tabla | el broker empuja mensajes (va a `evento-mensaje`) | EIP, *Polling Consumer* |
| Encolado transaccional | El trabajo existe si y solo si el cambio de negocio hizo commit | un caso de uso pide «hazlo luego» | el disparo es solo por reloj | db-scheduler (*transactionally staged*); microservices.io, *Transactional Outbox* |
| Criterio autocurativo (por estado y umbral) | Recuperar lo atrasado sin lógica de *misfire* | recurrentes | el trabajo es una foto de un instante exacto (cierre contable del día) | SRE cap. 24; [opinión] |
| Procesamiento por lotes acotados | Evitar resultados sin límite y transacciones largas | volúmenes > cientos | un puñado de ítems | Nygard, *Unbounded Result Sets* |
| Receptor idempotente | Que repetir dé el mismo efecto | siempre que la entrega sea al menos una vez | — | EIP, *Idempotent Receiver* |
| Reintento con backoff exponencial, *jitter* y tope | No martillar una dependencia caída | errores transitorios | errores permanentes (validación, 4xx) | Brooker (AWS); AWS *Exponential Backoff and Jitter* |
| *Dead Letter Channel* | Apartar lo que no se puede procesar | tras agotar reintentos o si el ítem es inválido | errores transitorios: reintenta antes | EIP |
| Timeout | No esperar para siempre | toda llamada remota | — | Nygard, *Timeouts* |
| Circuit Breaker | Dejar de llamar a una dependencia que falla | el trabajo llama a un sistema externo frágil | todo es local en la base | Nygard, *Circuit Breaker* |
| Bulkhead | Que el trabajo no robe hilos ni conexiones al resto | trabajos pesados en la misma JVM | proceso dedicado al trabajo | Nygard, *Bulkheads*; Spring 7 `@ConcurrencyLimit` |
| Steady State | Que el sistema no crezca sin límite | la cola, la DLQ o los logs acumulan | — | Nygard, *Steady State* |
| Governor | Limitar la velocidad de una automatización destructiva | purgas y borrados masivos | lecturas | Nygard, *Governor* |
| Latido (*heartbeat*) y revivir ejecuciones muertas | Detectar que el nodo murió a mitad | trabajos largos en cola persistida | trabajos de segundos con ShedLock | db-scheduler README |

**Decisiones de patrón que cierra la ronda:**
- motor (ShedLock ★ para recurrentes, db-scheduler ★ para cola): P3;
- selección (autocurativa ★, o por ventana exacta): P5;
- garantía (al menos una vez + idempotente ★): P10, P11;
- dónde reintenta (una sola capa ★): P13;
- aislamiento (pool propio ★; circuit breaker si hay dependencia externa): P16.

## 4 · Prácticas profesionales verificables

Severidad: C = crítica (3), M = mayor (2), m = menor (1). Verificación: A = ArchUnit, P = prueba,
L = lectura.

| id | Práctica | Sev. | Al detallar | Al verificar | Fuente |
|---|---|---|---|---|---|
| TP-01 | El adaptador es delgado: arma el comando, llama a **un** puerto de entrada y registra el resultado; sin repositorios ni reglas | C | P4 | A: clases de `..adapter.in.scheduling..` solo dependen de `..application.port.in..`, Spring, Micrometer y SLF4J | `investigacion/02` §2; §9.4 |
| TP-02 | `@Scheduled` no se declara en servicios de aplicación ni de dominio | M | P4 | A: métodos con `@Scheduled` solo en `..adapter.in..` | [opinión] sobre §9.8 (JHipster) |
| TP-03 | Con más de una réplica, la ejecución única la garantiza ShedLock, db-scheduler o Quartz en clúster | C | P8 | L: cada `@Scheduled` con efecto global lleva `@SchedulerLock`; P de integración con dos nodos (opcional) | ShedLock README; Spring *Scheduling* |
| TP-04 | `lockAtMostFor` es mucho mayor que la duración máxima esperada, y el método llama a `LockAssert.assertLocked()` | M | P9, P15 | L; P: la prueba del adaptador usa `LockAssert.TestHelper.makeAllAssertsPass(true)` | ShedLock README (*Behavior*, *Lock assert*) |
| TP-05 | ShedLock usa la hora de la base (`usingDbTime()`), no la del nodo | M | P8 | L: configuración del `LockProvider` | ShedLock README |
| TP-06 | Cada ítem se procesa de forma idempotente: repetir la pasada no repite efectos | C | P10 | P: ejecutar el caso de uso dos veces → un efecto | ShedLock *Caveats*; EIP *Idempotent Receiver*; AWS |
| TP-07 | Un ítem por transacción: el fallo de uno no revierte los demás | M | P6 | P: un ítem que lanza → los otros quedan hechos y `fallidos=1` | [opinión]; Nygard (*Unbounded Result Sets*) |
| TP-08 | La selección está acotada (`LIMIT`/lote) y el trabajo tiene un tope de ítems o de tiempo por pasada | M | P7, P15 | L: la consulta lleva `LIMIT`; P: con 3 lotes y tope 2, procesa 2 | Nygard, *Unbounded Result Sets* |
| TP-09 | El criterio de selección es autocurativo (estado + umbral), no «lo de ayer» | M | P5, P17 | P: con el reloj dos días adelante, procesa lo atrasado | SRE cap. 24; db-scheduler *gotchas* |
| TP-10 | El cron declara su zona (`zone = "America/Bogota"`) o se documenta que es UTC | m | P2 | L | Spring *Scheduling* (`zone`) |
| TP-11 | Los reintentos usan backoff exponencial **con jitter** y tope de espera y de intentos | M | P12 | P: la función de espera queda en `[0, min(tope, base·2^n)]` | AWS *Exponential Backoff and Jitter*; Spring 7 `@Retryable(jitter=…)` |
| TP-12 | Se reintenta en una sola capa (el trabajo **o** el cliente HTTP, no ambos) | M | P13 | L: el cliente no tiene reintentos si el trabajo los tiene | Brooker, AWS Builders' Library («243x») |
| TP-13 | Los errores permanentes no se reintentan: van directo a `FALLIDO` | M | P12, P14 | P: un ítem inválido no se reprograma | AWS (solo es seguro reintentar lo idempotente); [opinión] |
| TP-14 | Tras N intentos el ítem se aparta (`FALLIDO`/DLQ) con causa, y hay alerta; la cola no se bloquea | C | P14 | P: el ítem 6.º fallo → `FALLIDO`, la cola sigue | EIP *Dead Letter Channel*; db-scheduler `maxRetries(...).thenRemove(...)` |
| TP-15 | Toda llamada remota tiene timeout (p. ej. el p99,9 de la dependencia) | C | P15 | L: timeouts de conexión y lectura configurados; P con WireMock lento | Nygard, *Timeouts*; Brooker (p99.9) |
| TP-16 | El trabajo largo revisa si la aplicación se apaga y termina ordenadamente | m | P15 | L: `isShuttingDown()` (db-scheduler) o chequeo entre lotes | db-scheduler README (`shutdownMaxWait`) |
| TP-17 | Los trabajos no comparten el único hilo del planificador por defecto; pool con nombre o hilos virtuales | M | P16 | L: `spring.task.scheduling.pool.size` o `spring.threads.virtual.enabled` | Spring Boot, *Task Execution and Scheduling* |
| TP-18 | El tiempo viene de un `Clock` inyectado; nada de `Instant.now()` en aplicación ni dominio | M | P18 | A: `..application..` y `..domain..` no llaman a `Instant.now`/`LocalDateTime.now` | `investigacion/02` #21; §9.4, §9.6 |
| TP-19 | Se publica el instante del último éxito y hay alerta por **edad** (no por ausencia de logs) | C | P19 | L: gauge `*_ultimo_exito_*`; regla de alerta escrita | Prometheus, *Instrumentation*; SRE cap. 6 y 24 |
| TP-20 | Se miden procesados, fallidos y duración por pasada; en colas, la edad del ítem pendiente más viejo | M | P19 | L; P: el contador sube | Prometheus (*Offline processing*); Spring *Observability* (`tasks.scheduled.execution`) |
| TP-21 | Los temporizadores periódicos llevan desfase o *jitter* para no coincidir todos a las 00:00 | m | P2 | L: el cron no es `0 0 0 * * *` para todo | Brooker (AWS); SRE cap. 24 (*thundering herd*) |
| TP-22 | Lo que el trabajo acumula (cola, DLQ, completados, archivos) tiene retención y purga | M | P20 | L: hay trabajo o política de purga con su cifra | Nygard, *Steady State* |
| TP-23 | El adaptador no se traga excepciones sin métrica (`catch (Exception e) { log }` sin contador) | M | P19 | L | [opinión] sobre §9.5 (mytaxi, SocialSeed) |
| TP-24 | Si un caso de uso encola trabajo, lo hace en su misma transacción | M | P2 | P: rollback del caso de uso → no hay ítem en la cola | db-scheduler (*TransactionallyStagedJob*); microservices.io |
| TP-25 | Hay un interruptor por configuración para apagar el trabajo sin redeploy de código | m | P20 | L: `@ConditionalOnProperty` o cron `"-"` | §9.7 (Weather-alert); Spring (`"-"` desactiva el cron) |

## 5 · Árbol de preguntas

Formato: título de una línea + contexto pequeño o mediano con el **por qué** (SKILL.md, «Cómo preguntas») · opciones con la ★ primero y un ejemplo cada una ·
**cierra**.

**A · Propósito y disparo**

**P1. ¿Qué resultado de negocio produce, en una frase, y para quién?**
Por qué: sin resultado no hay criterio de selección, ni prueba, ni alerta que tenga sentido.
1. ★ Efecto verificable — «cada noche cancela los pedidos `CREADO` con más de 48 h, para liberar stock»
2. Técnico pero acotado — «purga las publicaciones completadas con más de 7 días»
3. Vago (se reformula) — «procesa los pedidos pendientes»
Cierra: ⟦1⟧.

**P2. ¿Qué lo dispara?**
Por qué: decide el motor, la forma del adaptador y si hace falta cola.
1. ★ Calendario cron con zona — `@Scheduled(cron = "0 15 2 * * *", zone = "America/Bogota")`
2. Intervalo desde que terminó la anterior — `@Scheduled(fixedDelayString = "${pedidos.vencidos.cada:PT5M}")`
3. Un ítem encolado por un caso de uso — `programador.programar(NotificarDespacho.de(pedidoId), ahora)`
4. Un evento de otro módulo → pasa a `evento-mensaje`
Cierra: ⟦2⟧, TP-10, TP-21, TP-24.

**P3. ¿Qué motor lo ejecuta?**
Por qué: ShedLock bloquea, db-scheduler persiste ejecuciones y Quartz trae calendarios; no son intercambiables.
1. ★ Recurrente: `@Scheduled` + ShedLock — `@SchedulerLock(name = "cancelar-vencidos", lockAtMostFor = "30m")`
2. ★ Cola o diferido: db-scheduler — `Tasks.oneTime(NOTIFICAR).onFailure(...).execute(...)`
3. Quartz en clúster (ya existe en el proyecto) — `@DisallowConcurrentExecution class CancelarJob implements Job`
4. `@Scheduled` solo (una instancia garantizada) — con ADR: «el despliegue tiene `replicas: 1`»
Cierra: ⟦4⟧, TP-03.

**P4. ¿Qué puerto de entrada invoca y con qué comando?**
Por qué: el adaptador debe ser intercambiable por un endpoint de administración o una prueba.
1. ★ Un puerto con comando explícito — `Resultado ejecutar(new CancelarVencidos.Comando(100, Duration.ofMinutes(20)))`
2. Un puerto sin parámetros — `int execute()` como `ExpireOrdersUseCase` (§9.4)
3. Lógica en el adaptador (se rechaza) — `repo.findAll…().forEach(repo::delete)` (§9.8)
Cierra: ⟦1⟧, TP-01, TP-02.

**B · Selección y unidad de trabajo**

**P5. ¿Cómo decide qué ítems procesar?**
Por qué: un criterio por estado y umbral recupera solo lo atrasado tras una caída.
1. ★ Estado + umbral relativo al reloj — `estado = 'CREADO' and creado_en < :ahora - interval '48 hours'`
2. Ventana exacta del calendario — «los de ayer» → exige registrar qué ventanas se cerraron
3. Marca «procesado» en cada fila — `procesado_en is null` (útil si no hay un estado de negocio)
Cierra: ⟦3⟧, TP-09.

**P6. ¿Cuál es la unidad de trabajo y su transacción?**
Por qué: una transacción gigante bloquea filas y un fallo revierte todo.
1. ★ Un ítem por transacción, en lotes de N — `tx.executeWithoutResult(s -> cancelar(id))` por cada id
2. Un lote por transacción — 100 pedidos o ninguno (si el negocio exige atomicidad del lote)
3. Todo en una transacción (se desaconseja) — `@Transactional` sobre el método programado (§9.8)
Cierra: ⟦3⟧, TP-07.

**P7. ¿Cuántos ítems puede haber en una pasada y cuál es el tope?**
Por qué: un resultado sin límite agota memoria y conexiones (Nygard).
1. ★ Lotes de N con tope por pasada — `LIMIT 100`, máximo 50 lotes; lo demás, en la siguiente pasada
2. Volumen pequeño y acotado por negocio — «nunca más de 200 al día» + `LIMIT 1000` de seguridad
3. Sin límite (se rechaza) — `findAllByEstado(CREADO)`
Cierra: ⟦3⟧, TP-08.

**C · Concurrencia y entrega**

**P8. ¿Cuántas instancias de la aplicación corren a la vez?**
Por qué: con N réplicas, cada una dispara su `@Scheduled`; Spring no coordina entre nodos.
1. ★ Varias: bloqueo con hora de la base — `JdbcTemplateLockProvider…usingDbTime()`
2. Varias, cola por ítem — `FOR UPDATE SKIP LOCKED` o db-scheduler
3. Una garantizada — ADR + `@Scheduled` solo; revisar si eso cambia al escalar
Cierra: ⟦5⟧, TP-03, TP-05.

**P9. ¿Puede una pasada empezar antes de que termine la anterior?**
Por qué: `fixedRate` y cron no esperan; dos pasadas pueden pisarse en el mismo nodo o entre nodos.
1. ★ No: `fixedDelay` o bloqueo con `lockAtMostFor` ≫ duración máxima — `lockAtMostFor = "30m"` para 5 min típicos
2. Sí, pero las pasadas reclaman ítems distintos — `SKIP LOCKED`
3. Sin control (se rechaza) — `fixedRate = 60000` sin bloqueo para un trabajo de 3 min
Cierra: ⟦5⟧, TP-04.

**P10. ¿Qué pasa si procesa dos veces el mismo ítem?**
Por qué: los bloqueos por tiempo y las ejecuciones revividas repiten trabajo (ShedLock, db-scheduler).
1. ★ Nada: idempotente por estado — `if (!(estado instanceof Creado)) return Omitido;`
2. Nada: clave de idempotencia en tabla de procesados — `insert … on conflict (clave) do nothing`
3. Duplica (solo si es inocuo y se justifica) — «recalcular un total da el mismo número»
Cierra: ⟦6⟧, TP-06.

**P11. ¿Qué garantía de entrega necesita el negocio?**
Por qué: «exactamente una vez» no existe en red; se busca un **efecto** exacto.
1. ★ Al menos una vez + efecto idempotente — reintenta y la P10 absorbe el duplicado
2. Como máximo una vez — «si se duda, se salta» (SRE: *fail closed*), p. ej. un correo de recordatorio
3. Exactamente una vez transaccional — solo si toma, efecto y marca están en la misma base y transacción
Cierra: ⟦6⟧.

**D · Fallos**

**P12. ¿Qué fallos son transitorios y cómo se reintentan?**
Por qué: reintentar sin espera ni tope convierte una caída en una avalancha (AWS).
1. ★ Backoff exponencial con *jitter* completo y tope — `espera = random(0, min(10 min, 30 s · 2^intento))`
2. Intervalo fijo con máximo — `FailureHandler.<Void>maxRetries(3).retryEvery(Duration.ofSeconds(1))` (§9.2)
3. No reintenta en la pasada; la siguiente pasada recoge (recurrente autocurativo) — sin código extra
Cierra: ⟦7⟧, TP-11, TP-13.

**P13. ¿En qué capa se reintenta?**
Por qué: si cada capa reintenta, la carga se multiplica (AWS: 243x con cinco capas).
1. ★ Solo en el trabajo; el cliente HTTP no reintenta — `RestClient` sin reintentos + backoff en la cola
2. Solo en la llamada, dentro del ítem — `@Retryable(includes = TimeoutTransportadora.class, maxRetries = 2, jitter = 100)`
3. En las dos (se rechaza) — `@Retryable` en el cliente y `maxRetries(5)` en la tarea
Cierra: ⟦7⟧, TP-12.

**P14. ¿Qué hace con un ítem que siempre falla?**
Por qué: un ítem envenenado no debe bloquear la cola ni perderse en silencio (EIP).
1. ★ Lo aparta con causa y alerta — `estado = 'FALLIDO', ultimo_error = :causa`; métrica `fallidos_definitivos`
2. Lo borra y lo registra — `.thenRemove(c -> log.warn(...))` (db-scheduler, §9.2) + evento `NotificacionAbandonada`
3. Reintenta para siempre (se rechaza) — `onFailure` por defecto de un único: «reintentar en 5 min» sin tope
Cierra: ⟦8⟧, TP-14.

**P15. ¿Qué timeout tienen las llamadas y cuánto puede durar una pasada?**
Por qué: sin timeout un hilo se cuelga; sin tope, el trabajo choca con la siguiente pasada.
1. ★ Timeout por llamada + tope de pasada con corte entre lotes — `readTimeout 2 s`; `Duration tope = 20 min`
2. Solo el tope de pasada — si no hay llamadas remotas (todo en la base)
3. Sin topes (se rechaza) — `RestTemplate` sin timeout
Cierra: ⟦9⟧, TP-15, TP-16.

**P16. ¿Cómo evita afectar al resto de la aplicación y a la dependencia?**
Por qué: el planificador por defecto tiene un hilo y el pool de conexiones es compartido (Nygard, *Bulkheads*).
1. ★ Pool propio con nombre y límite de concurrencia — `spring.task.scheduling.pool.size=2`; `@ConcurrencyLimit(4)`
2. Además, Circuit Breaker hacia el externo — Resilience4j `@CircuitBreaker(name = "transportadora")`
3. Comparte todo (se acepta si es corto y local) — un `UPDATE` de 200 ms
Cierra: ⟦9⟧, TP-17.

**E · Tiempo, recuperación y operación**

**P17. ¿Qué pasa si la aplicación estaba caída a la hora programada?**
Por qué: Quartz lo llama *misfire*; db-scheduler no garantiza todos los instantes del `Schedule`.
1. ★ Nada especial: el criterio autocurativo recoge lo atrasado — «> 48 h» incluye lo de anoche
2. Ejecutar al arrancar — `initialDelay = 120000` (§9.7) o la política de misfire de Quartz
3. Se pierde (solo si el trabajo es prescindible) — «el resumen de ayer ya no sirve»
Cierra: ⟦10⟧, TP-09.

**P18. ¿De dónde sale «ahora» y cómo se prueba el paso del tiempo?**
Por qué: con `Instant.now()` la prueba de «más de 48 h» depende del día en que corre.
1. ★ `Clock` inyectado; prueba con `Clock.fixed` — `new ExpireOrdersUseCaseImpl(repo, Clock.fixed(T0, UTC), …)` (§9.4)
2. Reloj ajustable para pruebas de varias pasadas — `SettableClock.tick(Duration)` (§9.6)
3. `Instant.now()` directo (se rechaza) — como `removeNotActivatedUsers` (§9.8)
Cierra: ⟦11⟧, TP-18.

**P19. ¿Cómo sabrás que corrió, cómo le fue y cuándo alertar?**
Por qué: «la métrica clave de un batch es la última vez que tuvo éxito» (Prometheus).
1. ★ Gauge de último éxito + contadores + alerta por edad — `time() - trabajo_ultimo_exito_segundos > 26*3600`
2. Cola: además la edad del pendiente más viejo — `cola_pendiente_mas_viejo_segundos > 900`
3. Solo logs (se rechaza) — `log.info("Start cleanup Job")` (§9.5)
Cierra: ⟦12⟧, TP-19, TP-20, TP-23.

**P20. ¿Qué acumula el trabajo, quién lo purga y cómo se apaga?**
Por qué: todo lo que acumula necesita quien lo recicle (Nygard, *Steady State*), y operar exige un interruptor.
1. ★ Retención con cifra + interruptor — `FALLIDO` se conserva 30 días; `pedidos.vencidos.habilitado=false`
2. No acumula — la tabla `shedlock` tiene una fila por trabajo
3. Acumula sin purga (se rechaza) — `HECHO` para siempre en la tabla de la cola
Cierra: ⟦13⟧, TP-22, TP-25.

## 6 · Poda y ramas nuevas

- **P2 = evento de otro módulo:** se cierra la ronda y se pasa a `evento-mensaje`.
- **P2 = ítem encolado:** P3 pasa a ★2 (db-scheduler) o tabla propia; P5 y P17 se confirman en una línea
  (la cola ya dice qué hay pendiente y recupera lo atrasado). Los cupos van a: ¿qué id de instancia
  identifica al ítem (`scheduleIfNotExists` deduplica por él)?, ¿qué datos lleva y cómo se serializan
  (por defecto, serialización de Java)? y ¿hay prioridad?
- **P3 = `@Scheduled` solo (una instancia):** se saltan P8 y P9. El cupo va a «¿qué pasa el día que haya dos
  réplicas?» (lo deja escrito en el ADR).
- **P3 = db-scheduler:** P9 se confirma (un solo nodo toma cada ejecución) y P12/P14 se responden con su
  `FailureHandler`. Cupo: latido y `missedHeartbeatsLimit` para trabajos largos.
- **P8 = una instancia garantizada:** igual que P3 = `@Scheduled` solo.
- **P11 = como máximo una vez:** se saltan P12 y P13 (no se reintenta). El cupo va a «¿cómo se entera
  alguien de lo que se saltó?».
- **P15 = sin llamadas remotas:** P16 se reduce a «pool propio» y el Circuit Breaker queda N/A (TP-15 N/A).
- **P10 = duplica:** se pide la justificación en una línea; si no la hay, se vuelve a P10.
- **Lo que el código o un ADR ya fija** (por ejemplo, «todos los trabajos usan ShedLock con `usingDbTime`»)
  se confirma en una línea y libera su cupo (`investigacion/04` regla 7).
- **Cupos libres:** primero los contraejemplos («¿y si un pedido cambia a `CONFIRMADO` justo mientras el
  trabajo lo cancela?» → bloqueo optimista del agregado, `entidad-agregado` P19), después la metapregunta.

## 7 · Cobertura

Pesos en el orden de §1 (⟦1⟧ pesa 13, ⟦13⟧ pesa 1).

- [ ] ⟦1⟧ Resultado de negocio en una frase y puerto de entrada que se invoca
- [ ] ⟦2⟧ Tipo de disparo, expresión cron o intervalo, zona horaria
- [ ] ⟦3⟧ Criterio de selección, tamaño de lote, tope por pasada y transacción por ítem
- [ ] ⟦4⟧ Motor elegido, con la razón
- [ ] ⟦5⟧ Ejecución única entre réplicas y política de solapamiento, con `lockAtMostFor`
- [ ] ⟦6⟧ Mecanismo de idempotencia y garantía de entrega
- [ ] ⟦7⟧ Clasificación transitorio/permanente, política de backoff con jitter y capa única
- [ ] ⟦8⟧ Destino del fallo definitivo, causa registrada y alerta
- [ ] ⟦9⟧ Timeouts por llamada, tope de pasada, apagado ordenado y pool
- [ ] ⟦10⟧ Comportamiento ante ejecuciones perdidas y muertas
- [ ] ⟦11⟧ Fuente del tiempo y estrategia de prueba
- [ ] ⟦12⟧ Métricas, instante del último éxito y reglas de alerta por edad
- [ ] ⟦13⟧ Retención y purga de lo que acumula, e interruptor

## 8 · Antipatrones

| Antipatrón | Señal detectable | Fuente |
|---|---|---|
| Lógica en el adaptador | El método `@Scheduled` usa repositorios, recorre entidades y borra (§9.8) | `investigacion/02` §2 |
| `@Scheduled` en un servicio `@Transactional` | Toda la pasada en una transacción; un fallo revierte todo y bloquea filas | [opinión] sobre §9.8 |
| Doble ejecución por réplicas | `@Scheduled` con efecto global y sin `@SchedulerLock` en un despliegue con `replicas > 1` | Spring *Scheduling*; ShedLock README |
| `lockAtMostFor` menor que la duración | El bloqueo expira a mitad y otro nodo empieza la misma pasada | ShedLock *Caveats* |
| Criterio por ventana fija sin registro | `creado_en between ayer and hoy`: si el nodo estaba caído, ese día se pierde | SRE cap. 24; [opinión] |
| Resultado sin límite | `findAll…()` sin `LIMIT`; memoria y tiempo crecen con el negocio | Nygard, *Unbounded Result Sets* |
| Reintento sin espera ni tope | `while (true) try { … } catch …`; reintento inmediato | Brooker (AWS) |
| Backoff sin jitter | Todos los ítems fallidos reintentan en el mismo segundo (el `ExponentialBackoffFailureHandler` de db-scheduler no tiene azar, §9.2) | AWS *Exponential Backoff and Jitter* |
| Reintentos en varias capas | `@Retryable` en el cliente y `maxRetries` en la tarea | Brooker (AWS) |
| Ítem envenenado | El mismo ítem falla en cada pasada y frena los siguientes; no hay estado `FALLIDO` | EIP *Dead Letter Channel* |
| Excepción tragada | `catch (Exception e) { log.error(...) }` sin métrica ni estado | [opinión] sobre §9.5 |
| Reloj oculto | `Instant.now()` en el caso de uso; prueba que depende del día | `investigacion/02` #21 |
| Trabajo en el hilo único | Varios `@Scheduled` lentos en el `ThreadPoolTaskScheduler` de un hilo: uno retrasa a los demás | Spring Boot, *Task Execution and Scheduling* |
| Estampida de medianoche | Todos los trabajos en `0 0 0 * * *` | SRE cap. 24 (*thundering herd*); Brooker |
| Alerta por logs | Nadie se entera de que el trabajo dejó de correr: el silencio no genera log | Prometheus; SRE cap. 6 |
| Cola que crece sin fin | Filas `HECHO` o `FALLIDO` sin purga | Nygard, *Steady State* |
| Estado estático en el adaptador | `static volatile boolean done` para «correr una sola vez» (§9.5): se pierde al reiniciar y no se comparte entre nodos | [opinión] |

## 9 · Código de referencia real

Todos los archivos se leyeron el 15-oct-2026 en el commit indicado (descargados con `raw.githubusercontent.com`
y la API de GitHub para la licencia). Los fragmentos son literales (indentación normalizada a espacios) y
`// …` marca lo omitido.

### 9.0 · Para qué usan sus trabajos los proyectos reales

| Proyecto (licencia) | Trabajo | Disparo y motor | Archivo |
|---|---|---|---|
| HalcyonTech `ms-template-java` (MIT) | Expira pedidos `CREATED` más viejos que N minutos | `fixedDelayString` + ShedLock → `ExpireOrdersUseCase` | [`OrderExpirationJob.java`](https://github.com/HalcyonTech-LTDA/ms-template-java/blob/e06872f862929c6ae96736826b351d3cea32efe9/app/src/main/java/com/example/templatejava/order/infrastructure/job/OrderExpirationJob.java) |
| armper `Weather-alert-backend` (MIT) | Retención: borra datos viejos cada hora | `fixedDelayString` + ShedLock + interruptor `@ConditionalOnProperty` | [`DataRetentionScheduler.java`](https://github.com/armper/Weather-alert-backend/blob/cd869c74be76ebea376fd1004a730b8db34cdb11/src/main/java/com/weather/alert/infrastructure/config/DataRetentionScheduler.java) |
| Angular2Guy `MovieManager` (Apache-2.0) | Limpieza nocturna de la base, reintento horario de eventos, migración al arrancar | cron + ShedLock | [`CronJobs.java`](https://github.com/Angular2Guy/MovieManager/blob/67173c1f08fd955f2ef13cbf0b62dd57a6f91333/backend/src/main/java/ch/xxx/moviemanager/adapter/cron/CronJobs.java) |
| douglasbelinato `mytaxi-ecosystem` (Apache-2.0) | Relé de outbox: publica eventos pendientes cada 10 s | `fixedDelay` + ShedLock → `PublishEventUseCase` | [`PublishEventScheduler.java`](https://github.com/douglasbelinato/mytaxi-ecosystem/blob/5133f01983b0c0c2dbf5f8f6c8f879be3eab2d99/ms-mytaxi-ride/src/main/java/br/com/mytaxi/infrastructure/input/scheduler/PublishEventScheduler.java) |
| daironpf `SocialSeed` (Apache-2.0) | Borra tokens vencidos a medianoche | cron por propiedad → `CleanupExpiredTokens` | [`TokenCleanupJob.java`](https://github.com/daironpf/SocialSeed/blob/a3f7c51a362138c9a1df78c5326f2792350a7beb/services/auth-service/src/main/java/com/socialseed/authservice/auth/infrastructure/job/TokenCleanupJob.java) |
| ricardohsmello `g12-football-backend` (Apache-2.0) | Cierra partidos vencidos | cron con `zone = "America/Sao_Paulo"` → `CloseExpiredMatchesUseCase` | [`CloseMatchesScheduler.java`](https://github.com/ricardohsmello/g12-football-backend/blob/52b291c0738d529dd1bb5dc1918ce957fa254a7d/infrastructure/src/main/java/br/com/g12/scheduler/CloseMatchesScheduler.java) |
| jhipster `jhipster-sample-app` (Apache-2.0) | Borra usuarios no activados tras 3 días, a la 01:00 | cron en `UserService` | ver §9.8 |
| sihyunjojo `minipay` (sin licencia: solo se cita) | Reprocesa una DLQ por lotes de 50 y vigila su tamaño | `fixedDelay` y cron | `batch/…/DlqScheduler.java` |
| Spring Modulith (Apache-2.0) | Reenvía publicaciones incompletas o fallidas; el *Staleness Monitor* marca `FAILED` lo atascado «como tarea programada» | API `IncompleteEventPublications` / `FailedEventPublications` | docs *Events* |
| db-scheduler (usuarios declarados: Digipost, Wise, NAV, TOMRA…) | No publican sus trabajos; el README los lista como usuarios en producción | db-scheduler | README, *Who uses db-scheduler?* |

Lectura transversal [opinión]: los usos se reparten en **vencimientos** (pedidos, partidos, tokens),
**retención y purga** (logs, datos viejos, usuarios no activados), **reintento y reenvío** (outbox, DLQ,
eventos fallidos) y **migraciones o recálculos** puntuales. Todos se benefician del criterio autocurativo
(P5) y de la idempotencia por estado (P10).

### 9.1 · Tarea recurrente de db-scheduler declarada como bean que delega en un servicio

- **Muestra:** `Tasks.recurring(...)` con un `Schedule` y una dependencia inyectada; el arranque de Spring
  Boot la registra sola. Es la opción P3-2 para recurrentes.
- **Repositorio:** `kagkarlsson/db-scheduler` · licencia Apache-2.0.
- **Archivo:** [`BasicExamplesConfiguration.java`](https://github.com/kagkarlsson/db-scheduler/blob/466ff4b458b971470b16cfaf4fb840b60d0266fa/examples/spring-boot-example/src/main/java/com/github/kagkarlsson/examples/boot/config/BasicExamplesConfiguration.java#L31-L76)

```java
@Configuration
public class BasicExamplesConfiguration {

  public static final TaskDescriptor<Void> BASIC_ONE_TIME_TASK =
      TaskDescriptor.of("sample-one-time-task");
  public static final TaskDescriptor<Void> BASIC_RECURRING_TASK =
      TaskDescriptor.of("recurring-sample-task");
  // …
  @Bean
  Task<Void> recurringSampleTask(CounterService counter) {
    return Tasks.recurring(BASIC_RECURRING_TASK, fixedDelay(Duration.ofMinutes(1)))
        .execute(
            (instance, ctx) -> {
              log.info("Running recurring-simple-task. Instance: {}, ctx: {}", instance, ctx);
              counter.increase();
              EventLogger.logTask(
                  BASIC_RECURRING_TASK, "Ran. Run-counter current-value=" + counter.read());
            });
  }
```

- **Qué copiar:** el `TaskDescriptor` como constante con nombre estable (es la clave en la tabla
  `scheduled_tasks`); la tarea como `@Bean` que recibe el servicio y solo lo invoca.
- **Qué no copiar:** en tu código, el servicio es un **puerto de entrada** (`CancelarVencidos`), no una
  clase concreta; y el `@Configuration` vive en el adaptador de entrada.

### 9.2 · Reintentos con tope y backoff exponencial en db-scheduler (y lo que le falta)

- **Muestra:** `FailureHandler.maxRetries(n).withBackoff(inicial, multiplicador).thenRemove()` (P12-2,
  P14-2) y la implementación del backoff.
- **Repositorio:** `kagkarlsson/db-scheduler` · licencia Apache-2.0.
- **Archivos:**
  [`ExponentialBackoffWithMaxRetriesMain.java`](https://github.com/kagkarlsson/db-scheduler/blob/466ff4b458b971470b16cfaf4fb840b60d0266fa/examples/features/src/main/java/com/github/kagkarlsson/examples/ExponentialBackoffWithMaxRetriesMain.java#L38-L44)
  y [`FailureHandler.java`](https://github.com/kagkarlsson/db-scheduler/blob/466ff4b458b971470b16cfaf4fb840b60d0266fa/db-scheduler/src/main/java/com/github/kagkarlsson/scheduler/task/FailureHandler.java#L126-L141)

```java
    OneTimeTask<Void> failingTask =
        Tasks.oneTime(EXPONENTIAL_BACKOFF_TASK)
            .onFailure(FailureHandler.<Void>maxRetries(6).withBackoff(ofSeconds(1), 2).thenRemove())
            .execute(
                (taskInstance, executionContext) -> {
                  throw new RuntimeException("simulated task exception");
                });
// … FailureHandler.ExponentialBackoffFailureHandler
    @Override
    public void onFailure(
        final ExecutionComplete executionComplete,
        final ExecutionOperations<T> executionOperations) {
      long retryDurationMs =
          round(
              sleepDuration.toMillis()
                  * pow(exponentialRate, executionComplete.getExecution().consecutiveFailures));
      Instant nextTry = executionComplete.getTimeDone().plusMillis(retryDurationMs);
      // …
      executionOperations.reschedule(executionComplete, nextTry);
    }
```

- **Qué copiar:** el tope de intentos y la acción terminal explícita (`thenRemove(listener)`, donde se
  registra la causa y se emite la alerta); el uso de `consecutiveFailures` persistido en la tabla.
- **Qué no copiar:** el backoff **no tiene jitter ni tope de espera**: con muchos ítems fallando a la vez,
  todos reintentan en el mismo instante (TP-11). Usa el `FailureHandler` propio de §10 T5.

### 9.3 · Encolar dentro de la transacción del caso de uso

- **Muestra:** `scheduleIfNotExists` dentro de una transacción: si hay rollback, la tarea no existe
  (TP-24, P2-3).
- **Repositorio:** `kagkarlsson/db-scheduler` · licencia Apache-2.0.
- **Archivo:** [`TransactionallyStagedJobConfiguration.java`](https://github.com/kagkarlsson/db-scheduler/blob/466ff4b458b971470b16cfaf4fb840b60d0266fa/examples/spring-boot-example/src/main/java/com/github/kagkarlsson/examples/boot/config/TransactionallyStagedJobConfiguration.java#L42-L58)

```java
    ctx.tx.executeWithoutResult(
        (TransactionStatus status) -> {
          // Since it is scheduled in a transaction, the scheduler will not run it until the tx
          // commits
          // If the tx rolls back, the insert of the new job will also roll back, i.e. not run.
          ctx.schedulerClient.scheduleIfNotExists(
              TRANSACTIONALLY_STAGED_TASK
                  .instance(String.valueOf(ID++))
                  .scheduledTo(Instant.now()));

          // Do additional database-operations here

          if (new Random().nextBoolean()) {
            throw new RuntimeException(
                "Simulated failure happening after task was scheduled. Scheduled task will never run.");
          }
        });
```

- **Qué copiar:** la idea y `scheduleIfNotExists` con un id de instancia **de negocio** (deduplica). El
  README advierte que `SchedulerClient` usa una conexión nueva salvo que el `DataSource` sea consciente de
  la transacción (`TransactionAwareDataSourceProxy`); el *starter* de Spring Boot lo envuelve solo
  ([`DbSchedulerConfigurationSupport.java`](https://github.com/kagkarlsson/db-scheduler/blob/466ff4b458b971470b16cfaf4fb840b60d0266fa/db-scheduler-spring-boot-starter-parent/db-scheduler-spring-common/src/main/java/com/github/kagkarlsson/scheduler/boot/config/DbSchedulerConfigurationSupport.java#L213-L222)).
- **Qué no copiar:** el id secuencial estático (`ID++`) e `Instant.now()`: usa `pedidoId` y el `Clock`.

### 9.4 · Trabajo que delega en un caso de uso, y caso de uso con `Clock` probado con `Clock.fixed`

- **Muestra:** el ★ de P4 y P18 completo: adaptador delgado con ShedLock y valores por propiedad, caso de uso
  con `Clock` y `Duration` inyectados, y prueba con reloj fijo y repositorio en memoria.
- **Repositorio:** `HalcyonTech-LTDA/ms-template-java` · licencia MIT.
- **Archivos:**
  [`OrderExpirationJob.java`](https://github.com/HalcyonTech-LTDA/ms-template-java/blob/e06872f862929c6ae96736826b351d3cea32efe9/app/src/main/java/com/example/templatejava/order/infrastructure/job/OrderExpirationJob.java#L12-L35),
  [`ExpireOrdersUseCaseImpl.java`](https://github.com/HalcyonTech-LTDA/ms-template-java/blob/e06872f862929c6ae96736826b351d3cea32efe9/app/src/main/java/com/example/templatejava/order/application/usecase/impl/ExpireOrdersUseCaseImpl.java#L28-L40)
  y [`ExpireOrdersUseCaseImplTest.java`](https://github.com/HalcyonTech-LTDA/ms-template-java/blob/e06872f862929c6ae96736826b351d3cea32efe9/app/src/test/java/com/example/templatejava/order/application/usecase/impl/ExpireOrdersUseCaseImplTest.java#L27-L54)

```java
    @Scheduled(fixedDelayString = "${app.order.expiration-job.cadence}")
    @SchedulerLock(
            name = "orderExpirationJob",
            lockAtLeastFor = "${app.order.expiration-job.lock-at-least-for}",
            lockAtMostFor = "${app.order.expiration-job.lock-at-most-for}")
    public void expireStaleOrders() {
        log.info("Executing scheduled order expiration routine");
        int expired = expireOrdersUseCase.execute();
        log.info("Order expiration routine completed. Expired {} orders.", expired);
    }
// … ExpireOrdersUseCaseImpl
    public int execute() {
        Instant threshold = clock.instant().minus(expirationDuration);
        List<Order> staleOrders = orderRepository.findCreatedBefore(OrderStatus.CREATED, threshold);
        // …
        for (Order order : staleOrders) {
            order.expire();
            orderRepository.save(order);
            count++;
        }
        return count;
    }
// … ExpireOrdersUseCaseImplTest
        fixedClock = Clock.fixed(Instant.parse("2026-03-30T12:00:00Z"), ZoneId.of("UTC"));
        useCase = new ExpireOrdersUseCaseImpl(orderRepository, fixedClock, Duration.ofMinutes(15));
    // …
        Instant thirtyMinsAgo = fixedClock.instant().minus(Duration.ofMinutes(30));
        Instant fiveMinsAgo = fixedClock.instant().minus(Duration.ofMinutes(5));
        // …
        int expiredCount = useCase.execute();

        assertThat(expiredCount).isEqualTo(1);
```

- **Qué copiar:** el adaptador sin lógica; el criterio autocurativo (estado + umbral desde el reloj); la
  prueba del umbral con un pedido a cada lado; la cadencia y los bloqueos por propiedad.
- **Qué no copiar:** la selección **sin límite** (`findCreatedBefore` sin `LIMIT`, TP-08); todos los
  pedidos en el mismo bucle sin transacción por ítem (TP-07); el `int` de retorno, que no distingue omitidos
  de fallidos; `assertLocked()` ausente.

### 9.5 · Varios trabajos con ShedLock en un adaptador `adapter/cron`

- **Muestra:** `@SchedulerLock` con `lockAtLeastFor`/`lockAtMostFor` en formato ISO-8601, cron nocturno y
  reintento horario de eventos.
- **Repositorio:** `Angular2Guy/MovieManager` · licencia Apache-2.0.
- **Archivo:** [`CronJobs.java`](https://github.com/Angular2Guy/MovieManager/blob/67173c1f08fd955f2ef13cbf0b62dd57a6f91333/backend/src/main/java/ch/xxx/moviemanager/adapter/cron/CronJobs.java#L49-L79)

```java
    @Scheduled(initialDelay = 2000, fixedRate = 36000000)
    @SchedulerLock(name = "Migrations_scheduledTask", lockAtLeastFor = "PT2H", lockAtMostFor = "PT3H")
    public void startMigrations() {
        LOG.info("Start migrations.");
        if (!migrationsDone) {
            this.dataMigrationService.encryptUserKeys().thenApplyAsync(result -> {
                LOG.info("Users migrated: {}", result);
                return result;
            });
        }
        migrationsDone = true;
    }

    @Scheduled(cron = "5 0 1 * * ?")
    // …
    @SchedulerLock(name = "CleanUp_scheduledTask", lockAtLeastFor = "PT2H", lockAtMostFor = "PT3H")
    public void dbCleanup() {
        LOG.info("Start cleanup Job");
        this.movieService.cleanup();
        this.actorService.cleanup();
        this.userService.cleanup();
        LOG.info("End cleanup Job");
    }

    @Scheduled(cron = "10 0 * * * ?")
    @SchedulerLock(name = "EventRetry_scheduledTask", lockAtLeastFor = "PT45M", lockAtMostFor = "PT55M")
    public void eventRetry() {
```

- **Qué copiar:** el paquete `adapter/cron`; el desfase de segundos en el cron (`5 0 1`, `10 0 *`) para no
  coincidir con otros trabajos (TP-21); `lockAtLeastFor` cercano al periodo para que no repita en el mismo
  ciclo.
- **Qué no copiar:** tres casos de uso en un solo método (si falla el primero, los otros no corren);
  `static volatile boolean migrationsDone` como «ejecución única» (no sobrevive a un reinicio ni se
  comparte entre nodos); el trabajo asíncrono que sale del bloqueo antes de terminar (`thenApplyAsync`), con
  lo que el bloqueo no lo protege; solo logs, sin métricas.

### 9.6 · ShedLock en Spring Boot: activación, bloqueo, aserción y prueba unitaria

- **Muestra:** `@EnableSchedulerLock(defaultLockAtMostFor = …)`, `@SchedulerLock` con `lockAtMostFor` por
  propiedad, `assertLocked()` dentro del método y su desactivación en una prueba unitaria.
- **Repositorio:** `lukas-krecan/ShedLock` · licencia Apache-2.0.
- **Archivos:**
  [`Application.java`](https://github.com/lukas-krecan/ShedLock/blob/73a6bcdcb43ad60e60f419cfdc85177b19575860/spring/test/shedlock-springboot-test/src/main/java/net/javacrumbs/shedlock/test/boot/Application.java#L21-L24),
  [`ScheduledTasks.java`](https://github.com/lukas-krecan/ShedLock/blob/73a6bcdcb43ad60e60f419cfdc85177b19575860/spring/test/shedlock-springboot-test/src/main/java/net/javacrumbs/shedlock/test/boot/ScheduledTasks.java#L23-L31)
  y [`LockUnitTest.java`](https://github.com/lukas-krecan/ShedLock/blob/73a6bcdcb43ad60e60f419cfdc85177b19575860/spring/test/shedlock-springboot-test/src/test/java/net/javacrumbs/shedlock/test/boot/LockUnitTest.java#L19-L25)

```java
@SpringBootApplication
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
public class Application {
// … ScheduledTasks.java
@Component
public class ScheduledTasks {
    @Scheduled(fixedRate = 100)
    @SchedulerLock(name = "reportCurrentTime", lockAtMostFor = "${lock.at.most.for}")
    public void reportCurrentTime() {
        assertLocked();
        System.out.println(Instant.now());
    }
}
// … LockUnitTest.java
public class LockUnitTest {
    @Test
    public void shouldWork() {
        LockAssert.TestHelper.makeAllAssertsPass(true);
        new ScheduledTasks().reportCurrentTime();
    }
}
```

- **Qué copiar:** el valor por defecto global y el específico por trabajo; `assertLocked()` para detectar
  que el proxy AOP no se aplicó (el README: los métodos `final` o no públicos no se interceptan); la prueba
  unitaria que llama al método directamente.
- **Qué no copiar:** `fixedRate = 100` (es una prueba de la librería) ni el `JdbcTemplateLockProvider(dataSource,
  "shedlock")` sin `usingDbTime()` de `ShedlockConfig.java`; el README recomienda «encarecidamente»
  `usingDbTime()`.

### 9.7 · Retención con interruptor por propiedad y retraso inicial

- **Muestra:** el ★ de P20 (interruptor) y P17-2 (`initialDelayString`), con ShedLock y un único caso de
  uso.
- **Repositorio:** `armper/Weather-alert-backend` · licencia MIT.
- **Archivo:** [`DataRetentionScheduler.java`](https://github.com/armper/Weather-alert-backend/blob/cd869c74be76ebea376fd1004a730b8db34cdb11/src/main/java/com/weather/alert/infrastructure/config/DataRetentionScheduler.java#L10-L27)

```java
@Component
@ConditionalOnProperty(
        value = "app.retention.schedule-enabled",
        havingValue = "true",
        matchIfMissing = true)
@RequiredArgsConstructor
public class DataRetentionScheduler {

    private final RunDataRetentionCleanupUseCase runDataRetentionCleanupUseCase;

    @Scheduled(
            fixedDelayString = "${app.retention.cleanup-fixed-delay-ms:3600000}",
            initialDelayString = "${app.retention.cleanup-initial-delay-ms:120000}")
    @SchedulerLock(name = "dataRetentionCleanup", lockAtLeastFor = "PT5M", lockAtMostFor = "PT30M")
    public void cleanupOldData() {
        runDataRetentionCleanupUseCase.run();
    }
}
```

- **Qué copiar:** todo el esqueleto. Es el adaptador mínimo correcto.
- **Qué no copiar:** los milisegundos como número crudo en la propiedad (`3600000`); con
  `@ConfigurationProperties` y `Duration` se escribe `PT1H` (`parametro-configuracion`). Vive en un paquete
  `config` en lugar de `adapter.in`.

### 9.8 · Antiejemplo: el trabajo dentro del servicio

- **Muestra:** lo que esta plantilla evita (TP-01, TP-02, TP-07, TP-08, TP-18).
- **Repositorio:** `jhipster/jhipster-sample-app` · licencia Apache-2.0.
- **Archivo:** [`UserService.java`](https://github.com/jhipster/jhipster-sample-app/blob/6b000b5d23a36c45e01472471b84a44fa2464044/src/main/java/io/github/jhipster/sample/service/UserService.java#L285-L299)

```java
    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired every day, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                LOG.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
                this.clearUserCaches(user);
            });
    }
```

- **Qué copiar:** el criterio autocurativo («creado antes de hace 3 días», no «los de antier») y el
  Javadoc que dice el propósito y la hora.
- **Qué no copiar:** `@Scheduled` en un `@Service` anotado `@Transactional` a nivel de clase (toda la pasada en
  una transacción); `Instant.now()`; la consulta sin límite; sin bloqueo entre réplicas; sin zona; sin
  métricas.

### 9.9 · Cola en la base con `FOR UPDATE SKIP LOCKED`: dos implementaciones reales

- **Muestra:** cómo un motor reclama ejecuciones sin que dos nodos tomen la misma (db-scheduler,
  `lock-and-fetch`) y cómo Spring Integration saca un mensaje de una tabla-cola.
- **Repositorios:** `kagkarlsson/db-scheduler` y `spring-projects/spring-integration` · ambos Apache-2.0.
- **Archivos:**
  [`PostgreSqlJdbcCustomization.java`](https://github.com/kagkarlsson/db-scheduler/blob/466ff4b458b971470b16cfaf4fb840b60d0266fa/db-scheduler/src/main/java/com/github/kagkarlsson/scheduler/jdbc/PostgreSqlJdbcCustomization.java#L72-L88)
  y [`PostgresChannelMessageStoreQueryProvider.java`](https://github.com/spring-projects/spring-integration/blob/d881a5c6f53f6e8e9e13a035a538eaed845dae3e/spring-integration-jdbc/src/main/java/org/springframework/integration/jdbc/store/channel/PostgresChannelMessageStoreQueryProvider.java#L33-L46)

```java
    String selectForUpdateQuery =
        " WITH locked_executions as (UPDATE "
            + ctx.tableName
            + " st1 SET picked = ?, picked_by = ?, last_heartbeat = ?, version = version + 1 "
            + " WHERE (st1.task_name, st1.task_instance) IN "
            + "(SELECT st2.task_name, st2.task_instance FROM "
            + ctx.tableName
            + " st2 "
            + " WHERE picked = ? and execution_time <= ? "
            + unresolvedFilter.andCondition()
            + Queries.ansiSqlOrderPart(orderByPriority)
            + " FOR UPDATE SKIP LOCKED "
            + getQueryLimitPart(limit)
            + ")"
            + " RETURNING st1.*) "
            + " SELECT * FROM locked_executions "
            + Queries.ansiSqlOrderPart(orderByPriority);
// … PostgresChannelMessageStoreQueryProvider.java
    public String getPollFromGroupQuery() {
        return """
                delete
                from %PREFIX%CHANNEL_MESSAGE
                where CTID = (select CTID
                                from %PREFIX%CHANNEL_MESSAGE
                                where %PREFIX%CHANNEL_MESSAGE.GROUP_KEY = :group_key
                                and %PREFIX%CHANNEL_MESSAGE.REGION = :region
                            order by CREATED_DATE, MESSAGE_SEQUENCE
                            limit 1 for update skip locked)
                returning MESSAGE_ID, MESSAGE_CONTENT;
                """;
    }
```

- **Qué copiar:** reclamar y marcar en **una** sentencia (`UPDATE … WHERE id IN (SELECT … FOR UPDATE SKIP
  LOCKED LIMIT n) RETURNING`); marcar quién lo tomó y su latido (`picked_by`, `last_heartbeat`) para revivir
  lo que quede huérfano; ordenar por instante de ejecución.
- **Qué no copiar:** borrar al leer (`delete … returning`) si el procesamiento ocurre fuera de esa
  transacción: un fallo después del commit pierde el mensaje (como máximo una vez) [opinión]. PostgreSQL
  advierte que `SKIP LOCKED` da «una vista inconsistente de los datos» y solo sirve para tablas tipo cola.

### 9.10 · `Clock` ajustable para probar reintentos sin esperar

- **Muestra:** un reloj de prueba que avanza a mano (`tick`) y un planificador manual que ejecuta lo debido
  (`runAnyDueExecutions`). Prueba que tras 3 fallos la tarea se elimina y se llama al *listener* (P14, P18-2).
- **Repositorio:** `kagkarlsson/db-scheduler` · licencia Apache-2.0.
- **Archivos:**
  [`SettableClock.java`](https://github.com/kagkarlsson/db-scheduler/blob/466ff4b458b971470b16cfaf4fb840b60d0266fa/db-scheduler/src/main/java/com/github/kagkarlsson/scheduler/testhelper/SettableClock.java#L23-L40)
  y [`MaxRetriesFailureHandlerTest.java`](https://github.com/kagkarlsson/db-scheduler/blob/466ff4b458b971470b16cfaf4fb840b60d0266fa/db-scheduler/src/test/java/com/github/kagkarlsson/scheduler/functional/MaxRetriesFailureHandlerTest.java#L55-L130)

```java
public class SettableClock implements Clock {
  public Instant now;
  // …
  public void tick(Duration toAdd) {
    now = now.plus(toAdd);
  }
}
// … MaxRetriesFailureHandlerTest.java
  @Test
  void maxRetries_thenRemove_should_remove_after_max_failures() {
    var instance =
        setupSchedulerAndFailingTask(
            FailureHandler.<Void>maxRetries(3)
                .retryEvery(RETRY_INTERVAL)
                .thenRemove(callbackTracker));

    runTimes(3);
    tester.assertThatExecution(instance).isScheduled();
    assertThat(callbackTracker.invoked).isFalse();

    runTimes(1);
    tester.assertNoExecution(instance);
    assertThat(callbackTracker.invoked).isTrue();
  }
  // …
        TestHelper.createManualScheduler(postgres.getDataSource(), task).clock(clock).build();
  // …
  private void runTimes(int times) {
    for (int i = 0; i < times; i++) {
      scheduler.runAnyDueExecutions();
      clock.tick(TICK);
    }
  }
```

- **Qué copiar:** `TestHelper.createManualScheduler(...).clock(clock)` para probar tus tareas de db-scheduler
  sin hilos ni esperas; el patrón «ejecutar lo debido → avanzar el reloj».
- **Qué no copiar:** `SettableClock` implementa el `Clock` propio de db-scheduler, no `java.time.Clock`.
  En tu caso de uso inyecta `java.time.Clock`; para avanzar el tiempo en pruebas usa un `Clock` mutable propio
  (§10 T9) o `Clock.offset(base, d)`.

## 10 · Plantillas de código

Esqueletos en Java 21, Spring Boot 4 (Spring Framework 7), ShedLock 7 y db-scheduler 16. Cada `TODO Pn` lo
resuelve la pregunta n. Paquete de ejemplo: `com.ejemplo.pedidos`. Las versiones exactas se confirman contra
el `pom.xml` del proyecto.

**T1 · Puerto de entrada y comando** (basado en 9.4, con resultado detallado)

```java
package com.ejemplo.pedidos.application.port.in;

public interface CancelarPedidosVencidos {                                  // TODO P1: nombre de negocio
  Resultado ejecutar(Comando comando);

  record Comando(int tamanoLote, int maxLotes, Duration topePasada) {      // TODO P7, P15
    public Comando {
      if (tamanoLote < 1 || tamanoLote > 1_000) throw new IllegalArgumentException("tamanoLote");
      if (maxLotes < 1) throw new IllegalArgumentException("maxLotes");
      Objects.requireNonNull(topePasada, "topePasada");
    }
  }
  record Resultado(int procesados, int omitidos, int fallidos, boolean cortadoPorTope) {}
}
```

**T2 · Adaptador recurrente: `@Scheduled` + ShedLock** (basado en 9.4, 9.6 y 9.7)

```java
package com.ejemplo.pedidos.adapter.in.scheduling;

@Component
@ConditionalOnProperty(name = "pedidos.vencidos.habilitado", havingValue = "true", matchIfMissing = true) // TP-25 · TODO P20
public class CancelarPedidosVencidosJob {
  private static final Logger log = LoggerFactory.getLogger(CancelarPedidosVencidosJob.class);
  private final CancelarPedidosVencidos casoDeUso;
  private final PropiedadesVencidos props;
  private final MetricasDeTrabajo metricas;                                  // T7

  public CancelarPedidosVencidosJob(CancelarPedidosVencidos casoDeUso, PropiedadesVencidos props, MeterRegistry registro) {
    this.casoDeUso = casoDeUso; this.props = props;
    this.metricas = new MetricasDeTrabajo("cancelar_pedidos_vencidos", registro);
  }

  @Scheduled(cron = "${pedidos.vencidos.cron}", zone = "${pedidos.vencidos.zona}")   // TODO P2: cron con desfase (TP-21)
  @SchedulerLock(name = "cancelar-pedidos-vencidos",
                 lockAtMostFor = "${pedidos.vencidos.lock-at-most-for}",            // TODO P9: ≫ duración máxima
                 lockAtLeastFor = "${pedidos.vencidos.lock-at-least-for}")
  public void ejecutar() {                                                    // público y no final (proxy AOP)
    LockAssert.assertLocked();                                                // TP-04
    var inicio = System.nanoTime();
    try {
      var r = casoDeUso.ejecutar(new CancelarPedidosVencidos.Comando(
          props.tamanoLote(), props.maxLotes(), props.topePasada()));
      metricas.registrar(r, System.nanoTime() - inicio);
      log.info("trabajo=cancelar_pedidos_vencidos procesados={} omitidos={} fallidos={} cortado={}",
          r.procesados(), r.omitidos(), r.fallidos(), r.cortadoPorTope());
    } catch (RuntimeException e) {                                            // TP-23: no se traga sin métrica
      metricas.registrarError(e, System.nanoTime() - inicio);
      throw e;                                                                // Spring lo registra; tasks.scheduled.execution → outcome=ERROR
    }
  }
}
```

**T3 · Caso de uso por lotes, un ítem por transacción, idempotente, con `Clock`** (basado en 9.4 y 9.8)

```java
package com.ejemplo.pedidos.application;

public class CancelarPedidosVencidosServicio implements CancelarPedidosVencidos {
  private final Pedidos pedidos;                      // puerto out (adaptador-persistencia)
  private final TransactionOperations tx;             // TransactionTemplate: una transacción por ítem (TP-07)
  private final Clock reloj;                          // TP-18
  private final Duration antiguedad;                  // TODO P5: p. ej. PT48H, desde parametro-configuracion

  // … constructor con Objects.requireNonNull en todo, como ExpireOrdersUseCaseImpl (9.4)

  @Override
  public Resultado ejecutar(Comando c) {
    Instant inicio = reloj.instant();
    Instant umbral = inicio.minus(antiguedad);                                 // criterio autocurativo (TP-09)
    int procesados = 0, omitidos = 0, fallidos = 0;
    for (int lote = 0; lote < c.maxLotes(); lote++) {                          // TP-08
      if (Duration.between(inicio, reloj.instant()).compareTo(c.topePasada()) > 0)
        return new Resultado(procesados, omitidos, fallidos, true);            // corte ordenado (TP-16)
      List<PedidoId> ids = pedidos.creadosAntesDe(umbral, c.tamanoLote());     // SELECT … LIMIT n · TODO P7
      if (ids.isEmpty()) break;
      for (PedidoId id : ids) {
        try {
          boolean cambio = Boolean.TRUE.equals(tx.execute(s -> {
            Pedido p = pedidos.porId(id).orElseThrow();
            boolean hecho = p.cancelarPorVencimiento(reloj.instant());         // idempotente: false si ya no está CREADO · TODO P10
            if (hecho) pedidos.guardar(p);                                     // versión optimista (entidad-agregado P19)
            return hecho;
          }));
          if (cambio) procesados++; else omitidos++;
        } catch (RuntimeException e) {                                         // un ítem no tumba la pasada
          fallidos++;                                                          // TODO P14: ¿marcar para revisión tras N pasadas?
        }
      }
      // [opinión] si todo el lote falló, detener la pasada: probablemente la dependencia está caída
    }
    return new Resultado(procesados, omitidos, fallidos, false);
  }
}
```

**T4 · Tarea de cola con db-scheduler y puerto para encolar** (basado en 9.1, 9.2 y 9.3)

```java
// application.port.out
public interface ProgramadorDeNotificaciones {
  void notificarDespacho(PedidoId pedido, Instant cuando);                    // se llama dentro de la transacción del caso de uso (TP-24)
}

// adapter.out.scheduling
@Component
class ProgramadorDbScheduler implements ProgramadorDeNotificaciones {
  static final TaskDescriptor<NotificacionDespacho> NOTIFICAR =
      TaskDescriptor.of("notificar-despacho", NotificacionDespacho.class);   // nombre estable: es la clave en scheduled_tasks
  private final SchedulerClient cliente;
  ProgramadorDbScheduler(SchedulerClient cliente) { this.cliente = cliente; }

  @Override public void notificarDespacho(PedidoId pedido, Instant cuando) {
    cliente.scheduleIfNotExists(NOTIFICAR.instance(pedido.valor().toString())    // id de negocio: deduplica (P10)
        .data(new NotificacionDespacho(pedido.valor())).scheduledTo(cuando));
  }
}
public record NotificacionDespacho(UUID pedido) implements Serializable {}    // TODO: serializador JSON si se prefiere

// adapter.in.scheduling
@Configuration
class TareasDeDespacho {
  @Bean
  Task<NotificacionDespacho> notificarDespachoTask(NotificarDespachoATransportadora casoDeUso) {   // puerto in
    return Tasks.oneTime(ProgramadorDbScheduler.NOTIFICAR)
        .onFailure(new BackoffConJitter<>(Duration.ofSeconds(30), Duration.ofMinutes(30), 8,    // TODO P12, P14
            (completo, ops) -> { /* registrar causa, métrica fallidos_definitivos, evento */ ops.remove(); }))
        .execute((instancia, ctx) -> casoDeUso.ejecutar(new PedidoId(instancia.getData().pedido())));
  }
}
```

**T5 · Backoff exponencial con *Full Jitter* para db-scheduler** (corrige lo que falta en 9.2; fórmula de AWS)

```java
// adapter.in.scheduling — sleep = random(0, min(tope, base · 2^intento))
final class BackoffConJitter<T> implements FailureHandler<T> {
  private final Duration base, tope;
  private final int maxIntentos;
  private final BiConsumer<ExecutionComplete, ExecutionOperations<T>> alAgotar;

  BackoffConJitter(Duration base, Duration tope, int maxIntentos,
                   BiConsumer<ExecutionComplete, ExecutionOperations<T>> alAgotar) {
    this.base = base; this.tope = tope; this.maxIntentos = maxIntentos; this.alAgotar = alAgotar;
  }

  @Override
  public void onFailure(ExecutionComplete completo, ExecutionOperations<T> ops) {
    int fallos = completo.getExecution().consecutiveFailures + 1;            // incluye el fallo actual
    if (fallos >= maxIntentos || esPermanente(completo.getCause())) {         // TP-13, TP-14
      alAgotar.accept(completo, ops);
      return;
    }
    ops.reschedule(completo, completo.getTimeDone().plus(espera(fallos, ThreadLocalRandom.current())));
  }

  Duration espera(int intento, RandomGenerator azar) {                       // función pura: se prueba sola (TP-11)
    long techo = Math.min(tope.toMillis(), base.toMillis() * (1L << Math.min(intento, 20)));
    return Duration.ofMillis(azar.nextLong(techo + 1));
  }

  private static boolean esPermanente(Optional<Throwable> causa) {           // TODO P12: clasificar errores
    return causa.filter(t -> t instanceof DatosInvalidos).isPresent();
  }
}
```

Alternativa dentro del ítem (P13-2), con Spring Framework 7 (`@EnableResilientMethods` en una
`@Configuration`):

```java
@Retryable(includes = TimeoutTransportadora.class, maxRetries = 2, delay = 200, multiplier = 2,
           jitter = 100, maxDelay = 2_000)                                   // total = 1 + maxRetries intentos
@ConcurrencyLimit(4)                                                          // bulkhead del método (TP-17)
public void notificar(Despacho d) { /* llamada HTTP con timeout (TP-15) */ }
```

**T6 · Cola propia con `SKIP LOCKED` y arrendamiento** (basado en 9.9; PostgreSQL)

```sql
-- V12__cola_trabajos.sql  (detalle fino en adaptador-persistencia)
create table cola_trabajo (
  id                 uuid primary key,
  tipo               text        not null,
  clave_idempotencia text        not null unique,          -- P10
  carga              jsonb       not null,
  estado             text        not null default 'PENDIENTE',  -- PENDIENTE | EN_CURSO | HECHO | FALLIDO
  intentos           int         not null default 0,
  proximo_intento    timestamptz not null,
  tomado_por         text,
  tomado_hasta       timestamptz,                          -- arrendamiento: si vence, se revive
  ultimo_error       text,
  creado_en          timestamptz not null
);
create index ix_cola_lista on cola_trabajo (proximo_intento) where estado = 'PENDIENTE';
create index ix_cola_tomada on cola_trabajo (tomado_hasta)   where estado = 'EN_CURSO';
```

```java
// adapter.out.persistence — reclama N ítems sin que otro nodo tome los mismos
List<ItemDeCola> reclamar(String tipo, int n, String nodo, Instant ahora, Duration arriendo) {
  return jdbc.sql("""
      update cola_trabajo t
         set estado = 'EN_CURSO', tomado_por = :nodo, tomado_hasta = :hasta, intentos = intentos + 1
       where t.id in (select id from cola_trabajo
                       where tipo = :tipo and estado = 'PENDIENTE' and proximo_intento <= :ahora
                       order by proximo_intento
                       limit :n
                       for update skip locked)
      returning t.id, t.carga, t.intentos
      """)
      .param("nodo", nodo).param("hasta", Timestamp.from(ahora.plus(arriendo)))
      .param("tipo", tipo).param("ahora", Timestamp.from(ahora)).param("n", n)
      .query(ItemDeCola.class).list();
}
// Tras procesar: HECHO; si falla: PENDIENTE con proximo_intento = ahora + espera(intentos) (T5) o FALLIDO.
// Otro trabajo (o la misma pasada) revive: update … set estado='PENDIENTE' where estado='EN_CURSO' and tomado_hasta < :ahora
```

**T7 · Métricas del trabajo** (Prometheus *Instrumentation*; SRE cap. 6)

```java
final class MetricasDeTrabajo {
  private final AtomicLong ultimoExitoEpoch = new AtomicLong(0);
  private final Counter procesados, fallidos, errores;
  private final Timer duracion;

  MetricasDeTrabajo(String trabajo, MeterRegistry r) {
    Gauge.builder("trabajo.ultimo.exito", ultimoExitoEpoch, AtomicLong::get)   // → trabajo_ultimo_exito_seconds
        .baseUnit("seconds").tag("trabajo", trabajo).register(r);              // TP-19
    procesados = Counter.builder("trabajo.items.procesados").tag("trabajo", trabajo).register(r);
    fallidos   = Counter.builder("trabajo.items.fallidos").tag("trabajo", trabajo).register(r);
    errores    = Counter.builder("trabajo.pasadas.error").tag("trabajo", trabajo).register(r);
    duracion   = Timer.builder("trabajo.pasada.duracion").tag("trabajo", trabajo).register(r);
  }
  void registrar(CancelarPedidosVencidos.Resultado res, long nanos) {
    procesados.increment(res.procesados()); fallidos.increment(res.fallidos());
    duracion.record(nanos, TimeUnit.NANOSECONDS);
    if (res.fallidos() == 0) ultimoExitoEpoch.set(Instant.now().getEpochSecond()); // adaptador: aquí sí vale el reloj del sistema [opinión]
  }
  void registrarError(RuntimeException e, long nanos) { errores.increment(); duracion.record(nanos, TimeUnit.NANOSECONDS); }
}
```

```yaml
# Regla de alerta por edad (Prometheus). TODO P19: umbral = periodo + margen
- alert: TrabajoSinExito
  expr: time() - trabajo_ultimo_exito_seconds{trabajo="cancelar_pedidos_vencidos"} > 26 * 3600
  for: 15m
  annotations: { summary: "cancelar_pedidos_vencidos lleva más de 26 h sin una pasada exitosa" }
```

[opinión] Con varias réplicas, la que no tiene el bloqueo no actualiza el gauge: alerta con `max by
(trabajo)` sobre todas las instancias.

**T8 · Configuración: bloqueo, pool, reloj y propiedades** (basado en 9.6 y en el README de ShedLock)

```java
@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30M")
class ConfiguracionDeTrabajos {
  @Bean LockProvider lockProvider(DataSource ds) {
    return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
        .withJdbcTemplate(new JdbcTemplate(ds))
        .usingDbTime()                                                        // TP-05
        .build());
  }
  @Bean Clock reloj() { return Clock.systemUTC(); }                           // TP-18
}

@ConfigurationProperties("pedidos.vencidos") @Validated                       // parametro-configuracion
record PropiedadesVencidos(@NotBlank String cron, @NotNull ZoneId zona, @Min(1) @Max(1000) int tamanoLote,
                           @Min(1) int maxLotes, @NotNull Duration topePasada,
                           @NotNull Duration lockAtMostFor, @NotNull Duration lockAtLeastFor) {}
```

```yaml
spring.task.scheduling:            # TP-17: el ThreadPoolTaskScheduler usa 1 hilo por defecto
  pool.size: 2
  thread-name-prefix: "trabajos-"
pedidos.vencidos:
  habilitado: true
  cron: "0 15 2 * * *"             # 02:15, no 00:00 (TP-21)
  zona: America/Bogota
  tamano-lote: 100
  max-lotes: 50
  tope-pasada: PT20M
  lock-at-most-for: PT30M          # ≫ 20 min de tope
  lock-at-least-for: PT5M
```

**T9 · Pruebas: reloj fijo, idempotencia, adaptador y arquitectura** (basado en 9.4, 9.6 y 9.10)

```java
class CancelarPedidosVencidosServicioTest {
  static final Instant T0 = Instant.parse("2026-01-10T07:15:00Z");
  final PedidosEnMemoria pedidos = new PedidosEnMemoria();
  final CancelarPedidosVencidosServicio servicio = new CancelarPedidosVencidosServicio(
      pedidos, TransactionOperations.withoutTransaction(), Clock.fixed(T0, ZoneOffset.UTC), Duration.ofHours(48));
  final CancelarPedidosVencidos.Comando cmd = new CancelarPedidosVencidos.Comando(100, 50, Duration.ofMinutes(20));

  @Test void cancelaSoloLosMasViejosQueElUmbral() {                          // TP-09
    pedidos.guardar(creadoEn("p-viejo", T0.minus(Duration.ofHours(49))));
    pedidos.guardar(creadoEn("p-nuevo", T0.minus(Duration.ofHours(47))));
    assertThat(servicio.ejecutar(cmd).procesados()).isEqualTo(1);
  }
  @Test void segundaPasadaNoHaceNada() {                                     // TP-06
    pedidos.guardar(creadoEn("p-viejo", T0.minus(Duration.ofDays(3))));
    servicio.ejecutar(cmd);
    assertThat(servicio.ejecutar(cmd)).isEqualTo(new CancelarPedidosVencidos.Resultado(0, 0, 0, false));
  }
  @Test void unFalloNoTumbaLosDemas() { /* PedidosEnMemoria que lanza para "p-2" → procesados=2, fallidos=1 (TP-07) */ }
}

class CancelarPedidosVencidosJobTest {
  @Test void delegaEnElCasoDeUso() {                                         // 9.6: sin Spring ni bloqueo real
    LockAssert.TestHelper.makeAllAssertsPass(true);
    var falso = new CasoDeUsoFalso(new CancelarPedidosVencidos.Resultado(3, 0, 0, false));
    new CancelarPedidosVencidosJob(falso, propiedadesDePrueba(), new SimpleMeterRegistry()).ejecutar();
    assertThat(falso.invocaciones).isEqualTo(1);
  }
}

class BackoffConJitterTest {
  @Test void laEsperaQuedaEntreCeroYElTecho() {                              // TP-11
    var b = new BackoffConJitter<Void>(Duration.ofSeconds(30), Duration.ofMinutes(10), 8, (c, o) -> {});
    var azar = new SplittableRandom(42);
    for (int i = 1; i <= 8; i++)
      assertThat(b.espera(i, azar)).isBetween(Duration.ZERO, Duration.ofMinutes(10));
  }
}

@AnalyzeClasses(packages = "com.ejemplo.pedidos")
class ReglasDeTrabajosTest {
  @ArchTest static final ArchRule programadosSoloEnAdaptadores = methods()
      .that().areAnnotatedWith(Scheduled.class)
      .should().beDeclaredInClassesThat().resideInAPackage("..adapter.in..");             // TP-02
  @ArchTest static final ArchRule adaptadorDelgado = classes()
      .that().resideInAPackage("..adapter.in.scheduling..")
      .should().onlyDependOnClassesThat().resideInAnyPackage("..adapter.in.scheduling..", "..application.port.in..",
          "..domain..", "java..", "org.springframework..", "net.javacrumbs.shedlock..", "com.github.kagkarlsson..",
          "io.micrometer..", "org.slf4j..");                                                 // TP-01
  @ArchTest static final ArchRule sinRelojOculto = noClasses()
      .that().resideInAnyPackage("..application..", "..domain..")
      .should().callMethod(Instant.class, "now").orShould().callMethod(LocalDateTime.class, "now"); // TP-18
}
```

[opinión] Para avanzar el tiempo entre dos pasadas sin db-scheduler, basta un `Clock` mutable de prueba
(un `AtomicReference<Instant>` dentro de una subclase de `java.time.Clock`), con el mismo patrón
«ejecutar → avanzar» de 9.10.

## 11 · Qué se genera

Orden dentro del segmento (cada paso compila solo):

1. Puerto de entrada y comando (T1). Si no existe el caso de uso, se abre la ronda de `caso-de-uso`.
2. Propiedades `@ConfigurationProperties` y su YAML (T8). Pasa a `parametro-configuracion` para validar.
3. Caso de uso por lotes (T3), con `CancelarPedidosVencidosServicioTest` (umbral, idempotencia, fallo
   aislado, tope).
4. Puerto de salida de selección acotada (`creadosAntesDe(umbral, n)`). Pasa a `adaptador-persistencia`
   (índice `(estado, creado_en)`).
5. Si hay cola: puerto para encolar y su adaptador (T4), o la tabla y la consulta `SKIP LOCKED` (T6), con su
   `FailureHandler` (T5) y `BackoffConJitterTest`.
6. Configuración de bloqueo, pool y reloj (T8), y la migración de la tabla `shedlock` o `scheduled_tasks`
   (DDL de cada librería).
7. Adaptador de entrada (T2) con métricas (T7) y `CancelarPedidosVencidosJobTest`.
8. Regla de alerta (T7) en el repositorio de monitoreo.
9. `ReglasDeTrabajosTest` (T9).

Dependencias: `shedlock-spring` y `shedlock-provider-jdbc-template`, o `db-scheduler-spring-boot-4-starter`
(para Spring Boot 3 es `db-scheduler-spring-boot-starter`, según el README);
`micrometer-core`; de prueba, JUnit 5, AssertJ, ArchUnit y Testcontainers (PostgreSQL) para probar
`SKIP LOCKED` y los bloqueos contra la base real.

## 12 · Referencias

- **db-scheduler** (Gustav Karlsson, Apache-2.0), README: características, *polling strategy*
  (`fetch` frente a `lock-and-fetch` con `select for update … skip locked`), configuración de tareas,
  *dead executions*, *gotchas* («no hay garantía de que se ejecuten todos los instantes de un `Schedule`») y
  FAQ frente a Quartz. https://github.com/kagkarlsson/db-scheduler
- **ShedLock** (Lukáš Křečan, Apache-2.0), README: «ShedLock no es un planificador distribuido», *Behavior*
  (se salta, `lockAtMostFor`, `lockAtLeastFor`), `usingDbTime()`, *Lock assert* y *Caveats*.
  https://github.com/lukas-krecan/ShedLock
- **Quartz Scheduler 2.3**: *Configuring Clustering with JDBC-JobStore* («solo un nodo dispara cada
  ejecución», relojes sincronizados a menos de un segundo, nunca un nodo no agrupado contra las mismas tablas)
  y *Lessons 3-4* (`@DisallowConcurrentExecution`, *requests recovery*, *misfire* y *smart policy*).
  https://www.quartz-scheduler.org/documentation/quartz-2.3.0/
- **Spring Framework 7**, *Task Execution and Scheduling*: `fixedDelay` frente a `fixedRate`, `zone`,
  macros, advertencia de instancias duplicadas, `@Scheduled` repetible que puede solaparse.
  https://docs.spring.io/spring-framework/reference/integration/scheduling.html
- **Spring Framework 7**, *Resilience Features*: `@Retryable` (por defecto 3 reintentos y 1 s; `delay`,
  `jitter`, `multiplier`, `maxDelay`, `includes`), `@ConcurrencyLimit`, `@EnableResilientMethods`,
  `RetryTemplate` y `RetryPolicy`. https://docs.spring.io/spring-framework/reference/core/resilience.html
- **Spring Framework**, *Observability Support*: observación `tasks.scheduled.execution` con `code.function`,
  `code.namespace`, `error` y `outcome`; se activa con un `SchedulingConfigurer` que fija el
  `ObservationRegistry`. https://docs.spring.io/spring-framework/reference/integration/observability.html
- **Spring Boot**, *Task Execution and Scheduling*: `ThreadPoolTaskScheduler` de un hilo por defecto,
  `spring.task.scheduling.pool.size`, `SimpleAsyncTaskScheduler` con hilos virtuales.
  https://docs.spring.io/spring-boot/reference/features/task-execution-and-scheduling.html
- **Spring Modulith**, *Events*: `IncompleteEventPublications`, `FailedEventPublications`,
  `ResubmissionOptions`, *Staleness Monitor* y `completion-mode`. https://docs.spring.io/spring-modulith/reference/events.html
- **M. Brooker**, *Timeouts, retries, and backoff with jitter*, Amazon Builders' Library (2019): reintentos
  «egoístas», amplificación 243x, reintentar en un solo punto, *token bucket*, timeouts por percentil,
  «jitter en todos los temporizadores y trabajos periódicos».
  https://aws.amazon.com/builders-library/timeouts-retries-and-backoff-with-jitter/ (hoy redirige a
  https://builder.aws.com/content/3EumjoZascWd1oZiEgL8ORlv3qE/timeouts-retries-and-backoff-with-jitter)
- **AWS Architecture Blog**, *Exponential Backoff and Jitter*: Full, Equal y Decorrelated Jitter.
  https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/
- **M. Nygard**, *Release It!* 2.ª ed. (Pragmatic Bookshelf, 2018), cap. «Stability Patterns»: Timeouts,
  Circuit Breaker, Bulkheads, Steady State, Fail Fast, Shed Load, Create Back Pressure y Governor; y el
  antipatrón *Unbounded Result Sets*. https://pragprog.com/titles/mnee2/release-it-second-edition/
- **Google SRE Book**, cap. 6 *Monitoring Distributed Systems* (Rob Ewaschuk): las cuatro señales doradas,
  síntomas frente a causas, «toda página debe ser accionable». https://sre.google/sre-book/monitoring-distributed-systems/
- **Google SRE Book**, cap. 24 *Distributed Periodic Scheduling with Cron* (Štěpán Davidovič): idempotencia,
  «preferimos saltarnos lanzamientos antes que arriesgar lanzamientos dobles», *fail closed*, monitoreo por
  el dueño y estampida (*thundering herd*). https://sre.google/sre-book/distributed-periodic-scheduling/
- **Prometheus**, *Instrumentation*, secciones *Offline processing* y *Batch jobs*: «la métrica clave de un
  batch job es la última vez que tuvo éxito», duración por etapa, latido.
  https://prometheus.io/docs/practices/instrumentation/
- **PostgreSQL**, `SELECT` (*The Locking Clause*): `SKIP LOCKED` da una vista inconsistente y sirve para
  «tablas tipo cola» con varios consumidores. https://www.postgresql.org/docs/current/sql-select.html
- **Hohpe y Woolf**, *Enterprise Integration Patterns* (2003): Dead Letter Channel, Idempotent Receiver,
  Competing Consumers, Polling Consumer. https://www.enterpriseintegrationpatterns.com/
- **microservices.io**, *Transactional Outbox* y *Polling Publisher* (C. Richardson).
  https://microservices.io/patterns/data/transactional-outbox.html
- **A. Cockburn**, *Hexagonal Architecture* (2005): adaptadores primarios que conducen la aplicación.
  https://alistair.cockburn.us/hexagonal-architecture/
- **JDK 21**, `java.time.Clock` (`systemUTC`, `fixed`, `offset`).
  https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/Clock.html
- `investigacion/02` (#21 reloj inyectable, §2 dónde vive cada pieza), `investigacion/04` §3.7 (banco de
  preguntas de esta plantilla) y `investigacion/03` (idempotencia).
