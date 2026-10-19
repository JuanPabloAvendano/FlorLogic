---
plantilla: evento-mensaje
aplica_a: eventos de dominio entre módulos de un monolito modular, eventos de integración hacia otros servicios y comandos asíncronos (con su respuesta y, si hay varios pasos, su saga)
entra_desde: entidad-agregado (el agregado registra el evento) · caso-de-uso (postcondiciones y eventos, idempotencia)
sale_hacia: caso-de-uso (el consumidor invoca un puerto de entrada) · adaptador-persistencia (tabla de procesados, registro de publicaciones) · trabajo-programado (reenvío de fallidos, purga) · parametro-configuracion (propiedades `spring.modulith.events.*`)
---
# Plantilla · Evento o mensaje

## 0 · Instrucciones para el agente

1. Usa esta plantilla cuando el elemento sea un mensaje: un evento que un agregado o caso de uso emite,
   un consumidor que reacciona a él o un comando que se envía sin esperar respuesta síncrona.
2. Antes de la P1, muestra el esqueleto de §1 con los huecos ⟦k⟧ y la opción ★ a la derecha
   (`investigacion/04` §2.3). Cada pregunta rellena un hueco.
3. Haz primero la P1 (tipo) y la P3 (alcance). Las dos podan ramas enteras (§6).
4. No preguntes lo que ya fijaron la plantilla de entidad-agregado (nombre del evento, transición que lo
   emite) o la de caso de uso (postcondiciones, idempotencia del comando). Confírmalo en una línea y
   regístralo como *supuesto verificado*.
5. Detalla un mensaje por ronda. Si el caso de uso emite tres eventos, haz tres rondas o trata los otros
   dos como confirmaciones cortas si comparten las respuestas de P7 a P12. [opinión]
6. Si el consumidor necesita lógica de negocio propia, detalla solo la frontera (listener → puerto de
   entrada) y pasa el resto a la plantilla de caso de uso.
7. La tabla de procesados y la tabla del registro de publicaciones se nombran aquí. Su mapeo, sus
   índices y sus migraciones van a la plantilla de adaptador de persistencia.
8. Si el usuario elige *event sourcing* en la P5, para la ronda. Este estilo cambia el modelo de
   persistencia del agregado y necesita su propia decisión de arquitectura (ADR) (Fowler 2017).
9. Si queda un cupo libre al final, úsalo en la metapregunta «¿hay algo que debí preguntarte sobre
   este mensaje?» (`investigacion/04` §1.2, regla 18).

## 1 · Qué se define primero

Va en orden de prioridad. Amplía los 11 elementos de `investigacion/04` §3.2 con dos que el detalle
necesita antes de codificar: el tipo de mensaje (1) y el momento de publicación (6).

| # | Elemento | Por qué va en este orden | Fuente |
|---|---|---|---|
| 1 | Tipo de mensaje: evento, comando o documento | Decide el número de receptores y quién es el dueño del contrato | EIP: *Event Message*, *Command Message*, *Document Message* |
| 2 | Hecho de negocio y nombre | El nombre en pasado fija el contrato y la semántica | Evans; Microsoft, *Domain events* |
| 3 | Alcance: interno al monolito o de integración | Abre o cierra las preguntas de broker, sobre y esquema | Microsoft, *Domain events vs integration events* |
| 4 | Productor dueño y consumidores | Sin dueño nadie decide la evolución | Zalando #207, #194 |
| 5 | Estilo y carga: notificación o transferencia de estado | Fija el acoplamiento y el tamaño del mensaje | Fowler, *What do you mean by "Event-Driven"?* |
| 6 | Momento de publicación respecto a la transacción | Si no es atómico, se pierden o se inventan eventos | Richardson, *Transactional Outbox*; Spring Modulith, EPR |
| 7 | Consumo: transacción propia y asincronía | Decide si un fallo del consumidor tumba al productor | Spring Modulith, `@ApplicationModuleListener` |
| 8 | Duplicados e idempotencia | La entrega es al menos una vez | Richardson, *Idempotent Consumer*; EIP, *Idempotent Receiver* |
| 9 | Orden y clave de partición | El orden solo se garantiza dentro de una partición | Kafka, *Introduction*; Zalando #203, #212 |
| 10 | Fallos, reintentos y mensajes muertos | Un mensaje envenenado no debe bloquear ni perderse | EIP, *Dead Letter Channel*; Modulith, `FailedEventPublications` |
| 11 | Sobre y metadatos | Sin `id` no hay deduplicación ni trazas | CloudEvents 1.0; Zalando #211, #247 |
| 12 | Evolución del esquema | Un cambio incompatible rompe a consumidores que no controlas | Kleppmann, *DDIA* cap. 4; Confluent |
| 13 | Retención, reproceso, documentación y pruebas | Cierran el contrato y lo hacen verificable | Modulith, *completion-mode*; AsyncAPI 3.0 |

Esqueleto que se muestra antes de la P1:

```
Mensaje: ⟦1 tipo⟧                     → evento
Nombre: ⟦2 hecho en pasado⟧           → PedidoConfirmado
Alcance: ⟦3 interno/integración⟧      → interno (pedidos → inventario, envíos)
Dueño y consumidores: ⟦4⟧             → pedidos; inventario reserva, envíos prepara
Carga: ⟦5 notificación/estado⟧        → ids + líneas + total
Publicación: ⟦6⟧                      → registro de publicaciones en la misma transacción
Consumo: ⟦7⟧                          → @ApplicationModuleListener (asíncrono, transacción propia)
Duplicados: ⟦8⟧                       → tabla eventos_procesados(consumidor, evento_id)
Orden: ⟦9⟧                            → por pedidoId; consumidor tolerante a desorden
Fallos: ⟦10⟧                          → FAILED + reenvío acotado + alerta
Lo demás: ⟦11..13⟧ sobre, esquema, retención, documentación, pruebas
```

## 2 · Funcionamiento apropiado

**Responsabilidades.**
- El **agregado** decide y registra el hecho. No publica, no conoce Spring ni el broker
  (`investigacion/02` §3, ítem 1).
- El **servicio de aplicación** abre la transacción, guarda el agregado y entrega sus eventos a un
  publicador. El publicador es el puerto `DomainEvents` en ddd-by-examples/library (§9 E4) o el
  `ApplicationEventPublisher` en los ejemplos de Modulith (§9 E1).
- El **registro de publicaciones** (Event Publication Registry) guarda una fila por evento y por
  listener *dentro de la misma transacción* del negocio. Marca la fila como completada cuando el
  listener termina bien (Spring Modulith, *Events*).
- El **listener** es un adaptador de entrada. Deduplica, traduce el evento a un comando y llama al puerto
  de entrada del módulo consumidor. Corre después del commit, en otro hilo y en su propia transacción.
- La **externalización** es un adaptador de salida que traduce el evento de dominio a un evento de
  integración y lo envía al broker (`@Externalized`, `EventExternalizationConfiguration`).

**Flujo dentro del monolito** (`spring.modulith.events.externalization.mode=module-listener`, el valor por defecto):

```
 [HTTP / job] → ConfirmarPedido (puerto de entrada, pedidos.application)
                 │ @Transactional ───────────────────────────────── T1 ─┐
                 │ pedido.confirmar() → registra PedidoConfirmado       │
                 │ pedidos.guardar(pedido)            (UPDATE pedido)   │
                 │ eventos.publicar(PedidoConfirmado)                   │
                 │   └─ EPR: INSERT event_publication × N listeners     │
                 └─ COMMIT ─────────────────────────────────────────────┘
                        │ AFTER_COMMIT, @Async
          ┌─────────────┴──────────────┐
          ▼                            ▼
 inventario.ReservarStockAl…   envios.PrepararEnvioAl…   (@ApplicationModuleListener, T2 y T3 propias)
   INSERT eventos_procesados     …                        si termina bien → EPR marca COMPLETED
   reservarStock.ejecutar(cmd)                            si falla        → EPR marca FAILED
```

**Flujo hacia otro servicio.** El mismo evento se selecciona con `@Externalized`. Se traduce a un
evento de integración versionado y sale al broker con una clave de partición. Con
`externalization.mode=outbox`, Modulith 2.1 delega en Namastack Outbox o JobRunr y obtiene un outbox
real (Spring Modulith, *Externalizing Events*). Del otro lado, el consumidor deduplica por
`(consumidor, id del mensaje)` en la misma transacción que su efecto (§9 E9).

**Comando asíncrono.** Tiene un solo receptor, que es el dueño del contrato. Lleva un identificador
de correlación y nombra su respuesta, que es un documento o un evento (EIP: *Command Message*,
*Correlation Identifier*, *Return Address*). Cuando hay varios pasos con efecto, los coordina una saga
orquestada o coreografiada, con una compensación por paso (Richardson, *Saga*; §9 E7).

**Dónde vive cada pieza.**

| Pieza | Paquete sugerido | Depende de |
|---|---|---|
| Evento de dominio (`record`) | `pedidos` (API del módulo) o `pedidos.eventos` como *named interface* | solo ids y valores |
| Registro del evento en el agregado | `pedidos.domain` | nada de Spring |
| Puerto publicador | `pedidos.application.port.out` | tipos de evento |
| Listener | `inventario.adapter.in.eventos` | API de pedidos + puerto de entrada de inventario |
| Puerto y adaptador de deduplicación | `inventario.application.port.out` y `inventario.adapter.out.persistence` | JDBC |
| Evento de integración y su mapeo | `pedidos.adapter.out.mensajeria` | broker |

## 3 · Patrones de diseño

| Patrón | Problema que resuelve | Cuándo usarlo | Cuándo no | Fuente |
|---|---|---|---|---|
| Observer | Notificar a dependientes sin acoplar al sujeto | Dentro de un proceso, en memoria | Si la notificación debe sobrevivir a una caída | GoF; Refactoring.Guru |
| Publish-Subscribe Channel | Entregar una copia del evento a cada suscriptor | Eventos con N consumidores | Comandos (un solo receptor) | EIP |
| Point-to-Point Channel + Competing Consumers | Un solo receptor procesa cada mensaje, con escalado horizontal | Comandos y colas de trabajo | Eventos que varios deben ver | EIP |
| Event Message / Command Message / Document Message | Distinguir hecho, orden y dato | Siempre: es la P1 | — | EIP |
| Domain Event | Hacer explícitos los efectos laterales entre agregados | Dentro del contexto acotado | Como contrato público estable | Evans; Fowler, *Domain Event*; Microsoft |
| Integration Event | Propagar cambios confirmados a otros contextos | Hacia otros servicios o sistemas | Entre agregados del mismo módulo | Microsoft, *.NET Microservices* |
| Event Notification | Avisar con acoplamiento bajo | El consumidor consulta poco o nada | Flujos largos difíciles de seguir | Fowler 2017 |
| Event-Carried State Transfer | El consumidor no llama al productor | Consumidores que necesitan una réplica | Datos grandes o sensibles | Fowler 2017 |
| Event Sourcing | El log de eventos es la fuente de verdad | Auditoría y reconstrucción de estados | Por defecto: pide un ADR propio | Fowler 2017 |
| Transactional Outbox (Polling Publisher, Transaction Log Tailing) | Actualizar la BD y publicar de forma atómica | Todo evento que sale del proceso | Eventos solo en memoria sin requisito de entrega | Richardson |
| Event Publication Registry | Un outbox por listener dentro del monolito | Spring Modulith con listeners transaccionales | Si ya hay un outbox externo que cubre lo mismo | Spring Modulith |
| Idempotent Consumer / Idempotent Receiver | Procesar dos veces da el mismo efecto | Siempre que la entrega sea al menos una vez | — | Richardson; EIP |
| Dead Letter Channel / Invalid Message Channel | Apartar lo que no se puede entregar o procesar | Tras agotar reintentos o si el mensaje es inválido | Para errores transitorios (reintenta antes) | EIP |
| Guaranteed Delivery / Durable Subscriber | Que el mensaje sobreviva a caídas | Integración entre servicios | Notificaciones efímeras | EIP |
| Correlation Identifier / Return Address / Request-Reply | Asociar la respuesta a su petición | Comandos asíncronos con respuesta | Eventos | EIP |
| Envelope Wrapper (CloudEvents) | Separar metadatos de carga | Eventos de integración | Eventos internos simples | EIP; CloudEvents |
| Format Indicator | Saber qué versión del esquema lleva | Esquemas que evolucionan | — | EIP; CloudEvents `dataschema` |
| Message Translator / Canonical Data Model | Traducir el modelo interno al contrato externo | En la externalización | Copiar el evento de dominio tal cual | EIP |
| Claim Check | Mensajes con cargas grandes | Adjuntos y documentos | Cargas pequeñas | EIP (*Store in Library*) |
| Resequencer | Recuperar el orden | Cuando el canal desordena | Si el consumidor tolera desorden | EIP |
| Saga (coreografía u orquestación) / Process Manager | Consistencia entre servicios sin 2PC | Varios pasos con efecto en varios dueños | Un solo agregado o módulo | Richardson; EIP |
| Tolerant Reader | Consumidores que no se rompen con campos nuevos | Todo consumidor | — | Fowler |

**Decisión de patrón que toma la ronda:** tipo (P1), alcance (P3), estilo de carga (P5), publicación
atómica (P7), modo de consumo (P8), idempotencia (P9) y, en un comando con varios pasos, saga orquestada
o coreografiada (rama de §6).

## 4 · Prácticas profesionales verificables

Severidad: crítica 3 · mayor 2 · menor 1. Se verifica con ArchUnit o Modulith (A), con una prueba (P) o leyendo (L).

| id | Práctica | Sev. | Al detallar | Al verificar | Fuente |
|---|---|---|---|---|---|
| EV-01 | Evento en pasado con lenguaje ubicuo; comando en imperativo | 2 | P2 responde con nombre del glosario | L: nombres de tipos en `..eventos..` | Evans; EIP; `investigacion/03` regla 2 |
| EV-02 | Evento inmutable (`record`) | 2 | P15 | A: `classes().that().resideInAPackage("..eventos..").should().beAssignableTo(Record.class)` | Microsoft |
| EV-03 | Cada evento lleva id único, instante y id del agregado | 3 | P14 | P: prueba de construcción; L: campos | library `DomainEvent`; CloudEvents; Zalando #211 |
| EV-04 | El evento solo lleva ids y valores, nunca entidades ni agregados | 2 | P6, P15 | A: `noClasses().that().resideInAPackage("..eventos..").should().dependOnClassesThat().areAnnotatedWith(Entity.class)` | Vernon; ejemplo Modulith (`OrderIdentifier`) |
| EV-05 | Publicación atómica con el cambio: registro u outbox en la misma transacción | 3 | P7 | P: prueba con listener que falla → publicación incompleta (§9 E3) | Richardson, *Outbox*; Modulith |
| EV-06 | Un agregado por transacción; los demás reaccionan al evento | 2 | P8 | L: un `guardar` por `@Transactional` | Vernon; Microsoft |
| EV-07 | Listener entre módulos asíncrono y en transacción propia | 2 | P8 | A: métodos con `@EventListener` solo en el módulo productor; los demás usan `@ApplicationModuleListener` | Modulith, *Application Module Listener* |
| EV-08 | Consumidor idempotente por `(consumidor, id de evento)` en la misma transacción que el efecto | 3 | P9 | P: entregar el mismo evento dos veces → un efecto | Richardson; EIP; Zalando #214 |
| EV-09 | Clave de orden = id del agregado; consumidor tolera desorden entre claves | 2 | P10 | L: `routeKey`; P: dos eventos fuera de orden | Kafka; Zalando #203, #212; CloudEvents `partitionkey` |
| EV-10 | Reintentos acotados, luego FAILED o DLQ con alerta; nada de reintento infinito | 2 | P11 | P: listener que falla queda FAILED; L: alerta | EIP; Modulith; Spring Kafka |
| EV-11 | El evento de integración es distinto del de dominio y se traduce al salir | 2 | P3, P17 | L: `mapping(...)` en la externalización | Microsoft; Modulith |
| EV-12 | Compatibilidad definida (por defecto BACKWARD) y consumidor tolerante | 3 | P18 | P: deserializar la versión anterior; registro de esquemas | Kleppmann; Confluent; Fowler; Zalando #209, #245 |
| EV-13 | Sobre CloudEvents en los eventos que salen | 1 | P14 | L: `id`, `source`, `type`, `specversion` | CloudEvents 1.0 |
| EV-14 | Contrato documentado: AsyncAPI fuera, Documenter dentro | 1 | P19 | L: archivo `asyncapi.yaml` o canvas del módulo | AsyncAPI 3.0; Modulith |
| EV-15 | Dueño del esquema explícito: el productor (en un comando, el receptor) | 2 | P4 | A: `ApplicationModules.verify()` (el consumidor solo usa la API pública) | Zalando #207 |
| EV-16 | Sin datos sensibles en la carga | 2 | P6 | L: lista de campos | Zalando #200 |
| EV-17 | Publicaciones completadas se purgan o archivan | 1 | P12 | L: `completion-mode` o trabajo de purga | Modulith, *Event Publication Completion* |
| EV-18 | `id` de listener estable y tipo de evento estable mientras haya publicaciones pendientes | 1 | P16 | L: `@ApplicationModuleListener(id = …)` | [opinión] basada en la tabla `event_publication` (`listener_id`, `event_type`) |
| EV-19 | Prueba de publicación (Scenario) y prueba de duplicado | 2 | P20 | P: existen ambas | Modulith, *Testing* |
| EV-20 | Comando asíncrono: un receptor, id de correlación, respuesta nombrada | 2 | P1, rama comando | L: campos; P: respuesta correlacionada | EIP; FTGO (§9 E8) |
| EV-21 | Saga: compensación por paso con efecto y contramedida de aislamiento | 2 | rama saga | P: falla del paso n compensa 1..n-1 | Richardson, *Saga* |
| EV-22 | `republish-outstanding-events-on-restart=false` con varias instancias | 1 | P11 | L: propiedades | Modulith, *Appendix* |
| EV-23 | Nada de «comando pasivo-agresivo»: si el emisor espera una acción concreta, es un comando | 2 | P1 | L: el evento no nombra a su receptor ni su acción | Fowler 2017 |

## 5 · Árbol de preguntas

Formato (`investigacion/04` §2.2). Cada opción trae su ejemplo en Java 21 + Spring. «Cierra» indica
el elemento de §1 y la práctica de §4 que resuelve.

**Propósito**

**P1 · Tipo** [alto] ¿Anuncia algo que ya pasó o pide a otro que haga algo?
*Por qué:* el evento admite N receptores y el comando uno solo, y cada caso tiene un dueño distinto.
1. ★ Evento — ej: `record PedidoConfirmado(UUID eventoId, PedidoId pedidoId, Instant ocurrioEn)`
2. Comando asíncrono — ej: `record ReservarStock(UUID correlacionId, PedidoId pedidoId, List<Linea> lineas)`
3. Respuesta o documento — ej: `record StockReservado(UUID correlacionId, ReservaId reservaId)`
Cierra: §1-1 · EV-01, EV-20, EV-23.

**P2 · Nombre** [alto] ¿Qué hecho de negocio anuncia y cómo lo nombra el negocio?
*Por qué:* el nombre es el contrato. Los nombres CRUD esconden la intención.
1. ★ Sustantivo + participio, del glosario — ej: `PedidoConfirmado`, `EnvioDespachado`
2. Cambio de datos explícito — ej: `PedidoModificado(antes, despues)` (Zalando #202)
3. Técnico (desaconsejado) — ej: `PedidoGuardado`
Cierra: §1-2 · EV-01.

**P3 · Alcance** [alto] ¿Se queda en el monolito o lo leen otros servicios?
*Por qué:* el evento interno puede cambiar con el código. El de integración es un contrato público.
1. ★ Solo entre módulos del monolito — ej: `@ApplicationModuleListener void on(PedidoConfirmado e)`
2. También sale, traducido — ej: `@Externalized("pedidos.pedido-confirmado") record PedidoConfirmado(…)`
3. Solo dentro del módulo — ej: un listener en `pedidos` que actualiza una vista de lectura
Cierra: §1-3 · EV-11.

**P4 · Dueño y consumidores** [medio] ¿Quién es el dueño del esquema y quién lo consume hoy?
*Por qué:* el dueño decide la evolución, y cada consumidor es un caso de prueba.
1. ★ El productor; consumidores listados — ej: «dueño `pedidos`; `inventario` reserva, `envios` prepara»
2. El receptor (comando) — ej: «`inventario` publica `ReservarStock` en su API»
3. Compartido (desaconsejado) — ej: `common.eventos.PedidoConfirmado`
Cierra: §1-4 · EV-15.

**Comportamiento**

**P5 · Estilo** [alto] ¿El consumidor recibe solo el aviso o los datos para actuar sin consultar?
*Por qué:* la notificación acopla menos, y la transferencia de estado evita llamadas al productor (Fowler).
1. ★ Notificación con ids + campos para decidir — ej: `PedidoConfirmado(pedidoId, clienteId, lineas, total)`
2. Solo el id; el consumidor consulta — ej: `PedidoConfirmado(pedidoId)` + `pedidos.porId(...)`
3. Transferencia de estado completa — ej: `ClienteActualizado(id, nombre, direccion, segmento)`
4. Event sourcing — ej: el agregado se reconstruye de sus eventos (para la ronda: requiere ADR)
Cierra: §1-5 · EV-23.

**P6 · Carga** [medio] ¿Qué campos exactos lleva y cuál es sensible?
*Por qué:* cada campo de más es un acoplamiento, y un dato sensible queda en logs y brokers.
1. ★ Lista cerrada de ids y valores, sin datos personales — ej: `pedidoId, clienteId, lineas[sku,cant], total`
2. Con datos personales enmascarados — ej: `correo: "a***@x.com"`
3. Entidad completa (antipatrón) — ej: `PedidoConfirmado(Pedido pedido)`
Cierra: §1-5 · EV-04, EV-16.

**P7 · Publicación** [alto] ¿Cómo queda el evento atado al commit del cambio?
*Por qué:* publicar fuera de la transacción pierde eventos o anuncia cambios que se revirtieron.
1. ★ Registro de publicaciones de Modulith en la misma transacción — ej: `spring-modulith-starter-jdbc`
2. Outbox real (Namastack o JobRunr) para lo que sale — ej: `spring.modulith.events.externalization.mode=outbox`
3. Tabla outbox propia + relay — ej: `StoreAndForwardDomainEventPublisher` (§9 E5)
4. Enviar al broker tras el commit (desaconsejado) — ej: `kafkaTemplate.send(...)` en `afterCommit`
Cierra: §1-6 · EV-05.

**P8 · Consumo** [alto] ¿El consumidor corre en la transacción del productor o en una propia?
*Por qué:* en la misma transacción, un fallo del consumidor revierte la confirmación del pedido.
1. ★ Propia, asíncrona, tras el commit — ej: `@ApplicationModuleListener`
2. Misma transacción, síncrono (solo si una invariante lo exige) — ej: `@EventListener`
3. Antes del commit — ej: `@TransactionalEventListener(phase = BEFORE_COMMIT)`
Cierra: §1-7 · EV-06, EV-07.

**P9 · Duplicados** [alto] Si el mismo evento llega dos veces, ¿cómo evitas el doble efecto?
*Por qué:* el registro y el outbox entregan al menos una vez; un reintento repite el listener.
1. ★ Tabla de procesados `(consumidor, evento_id)` en la misma transacción — ej: `INSERT … ON CONFLICT DO NOTHING`
2. Idempotencia natural por estado — ej: «si el libro ya está apartado por ese lector, no hace nada» (§9 E5)
3. Efecto idempotente por naturaleza — ej: `UPSERT` de una vista con la última versión
Cierra: §1-8 · EV-08.

**P10 · Orden** [medio] ¿Importa el orden entre eventos? ¿Respecto a qué clave?
*Por qué:* el broker ordena solo dentro de una partición (Kafka). El orden global no escala.
1. ★ Por agregado: clave = `pedidoId`; desorden tolerado entre claves — ej: `.routeKey(PedidoConfirmado.class, e -> e.pedidoId().toString())`
2. No importa — ej: «cada evento es independiente»
3. Global (evitar) — ej: una sola partición + `serialize-externalization=true`
Cierra: §1-9 · EV-09.

**Variables**

**P11 · Fallos** [alto] Si el consumidor falla, ¿cuántas veces reintenta y qué pasa después?
*Por qué:* el reintento infinito bloquea, y el descarte silencioso pierde el hecho.
1. ★ Queda FAILED; se reenvía acotado con alerta — ej: `failed.resubmit(ResubmissionOptions.defaults().withMinAge(Duration.ofMinutes(5)))`
2. Reintentos con backoff y DLT (broker) — ej: `@RetryableTopic(attempts = "4")` → `pedidos-dlt` (pierde el orden)
3. Descartar y registrar — ej: `log.warn(...)`, solo para avisos sin valor de negocio
Cierra: §1-10 · EV-10, EV-22.

**P12 · Retención** [medio] ¿Cuánto guardas publicaciones completadas y claves de deduplicación?
*Por qué:* las tablas crecen sin límite. La ventana de deduplicación debe cubrir los reenvíos.
1. ★ `completion-mode=archive` + purga de procesados > 30 días — ej: `spring.modulith.events.completion-mode=archive`
2. `delete` al completar — ej: `spring.modulith.events.completion-mode=delete`
3. `update` + trabajo de purga propio — ej: `DELETE FROM event_publication WHERE completion_date < now() - interval '7 days'`
Cierra: §1-13 · EV-17.

**P13 · Criterio medible** [medio] ¿Cómo sabrás en producción que la entrega funciona?
*Por qué:* «llega rápido» no se puede probar (Volere: criterio de ajuste).
1. ★ Cero FAILED con más de 1 h; p95 commit→consumo < 5 s — ej: métrica `eventos_fallidos{tipo}`
2. Solo cero FAILED — ej: alerta si `FailedEventPublications` no está vacío
3. Sin criterio (pasa a pendiente)
Cierra: §1-10 · `investigacion/04` regla 16.

**Forma técnica**

**P14 · Sobre** [medio] ¿Qué metadatos lleva además de la carga?
*Por qué:* el `id` deduplica, el `time` ordena y audita, y `source`+`type` enrutan (CloudEvents).
1. ★ `eventoId`, `ocurrioEn`, id del agregado; CloudEvents al salir — ej: `ce_id`, `ce_source=/pedidos`, `ce_type=…pedido-confirmado.v1`
2. CloudEvents también dentro — ej: `CloudEvent` del SDK en el listener
3. Ninguno (desaconsejado) — ej: `record PedidoConfirmado(PedidoId id)`
Cierra: §1-11 · EV-03, EV-13.

**P15 · Tipos** [medio] ¿Con qué tipos se declara el evento?
*Por qué:* el registro serializa el evento; las entidades no se serializan bien ni deben salir del agregado.
1. ★ `record` con ids tipados y valores — ej: `record PedidoConfirmado(UUID eventoId, PedidoId pedidoId, Dinero total, Instant ocurrioEn)`
2. Solo primitivos — ej: `record PedidoConfirmado(String eventoId, String pedidoId, long totalCentavos)`
3. `Map<String,Object>` (desaconsejado)
Cierra: §1-11 · EV-02, EV-04.

**P16 · Ubicación** [medio] ¿Dónde vive el tipo y cómo se identifica su listener?
*Por qué:* Modulith solo deja usar la API pública. El registro guarda `listener_id` y `event_type`.
1. ★ API del productor + `id` de listener fijo — ej: `pedidos/PedidoConfirmado.java`, `@ApplicationModuleListener(id = "inventario.reservar-stock")`
2. *Named interface* `pedidos::eventos` — ej: `@NamedInterface("eventos") package …pedidos.eventos;`
3. Módulo compartido (desaconsejado) — ej: `common/eventos`
Cierra: §1-4 · EV-15, EV-18.

**P17 · Salida** [medio] Si sale, ¿qué canal, qué clave y qué traducción usa?
*Por qué:* el contrato externo no debe cambiar cuando cambia el modelo interno.
1. ★ Evento de integración versionado vía `mapping` — ej: `.mapping(PedidoConfirmado.class, PedidoConfirmadoV1::desde)`
2. El evento de dominio tal cual — ej: `@Externalized("pedidos.pedido-confirmado::#{#this.pedidoId()}")`
3. Adaptador propio con `KafkaTemplate` + outbox — ej: `outbox.guardar(mensaje)` + relay
Cierra: §1-3 · EV-11.

**P18 · Evolución** [alto] ¿Qué cambio del esquema será compatible y cómo se comprueba?
*Por qué:* el código nuevo debe leer datos viejos y el viejo los nuevos (Kleppmann, cap. 4).
1. ★ Solo cambios aditivos con defecto; BACKWARD_TRANSITIVE; consumidor tolerante — ej: añadir `canal` opcional
2. Tipo nuevo en paralelo — ej: publicar `…v1` y `…v2` mientras migran los consumidores
3. Libre (solo eventos internos sin pendientes) — ej: renombrar campo y desplegar todo junto
Cierra: §1-12 · EV-12.

**P19 · Documentación** [bajo] ¿Dónde queda escrito el contrato?
*Por qué:* el consumidor externo no lee tu código.
1. ★ AsyncAPI 3.0 al salir; canvas de Modulith dentro — ej: `operations.publicarPedidoConfirmado.action: send`
2. JSON Schema suelto — ej: `pedido-confirmado.v1.schema.json`
3. Solo el `record` (solo interno)
Cierra: §1-13 · EV-14.

**P20 · Pruebas** [alto] ¿Qué pruebas demuestran que se publica, se consume y se deduplica?
*Por qué:* cada decisión de P7 a P11 necesita un caso dado/cuando/entonces.
1. ★ `Scenario` del productor + duplicado del consumidor + listener que falla — ej: `scenario.stimulate(...).andWaitForEventOfType(PedidoConfirmado.class).toArrive()`
2. Solo unitarias con dobles — ej: `PublicadorEnMemoria` y aserción sobre la lista
3. Solo prueba de punta a punta con broker — ej: Testcontainers Kafka
Cierra: §1-13 · EV-19.

## 6 · Poda y ramas nuevas

- **P1 = comando.** La P4 pasa a «el receptor es el dueño». La P5 cambia a «¿espera respuesta? ★ sí,
  con `correlacionId` y respuesta nombrada · no». La P10 se vuelve «¿un solo consumidor activo por clave?».
  Un cupo liberado va a la **rama saga**: «¿cuántos pasos con efecto en distintos dueños?» y, si son dos
  o más, «★ orquestada (un orquestador, §9 E7) · coreografiada (eventos encadenados)» y «compensación
  de cada paso».
- **P1 = respuesta.** Se saltan P5, P10 y P17, y la P9 se resuelve por `correlacionId`.
- **P3 = solo entre módulos.** Las P14, P17 y P19 se vuelven confirmaciones de una línea (sin CloudEvents,
  sin canal, canvas de Modulith). La P18 baja a impacto bajo si no quedan publicaciones pendientes al desplegar.
- **P3 = solo dentro del módulo.** Se saltan además P4 y P16.
- **P5 = event sourcing.** Se para la ronda y se abre un ADR (§0, regla 8).
- **P8 = misma transacción.** Se saltan P9, P11 y P12. El efecto es atómico y no hay reintento propio. Se
  pide confirmar el motivo (una de las cuatro razones de Vernon, `investigacion/03` regla 5).
- **P9 = idempotencia natural.** La P12 solo pregunta por las publicaciones, no por las claves.
- **Ya fijado por la documentación** (ADR, código existente): se confirma en una línea.
- **Cupos liberados:** uno por cada consumidor listado en la P4, para preguntar «¿qué hace si el pedido ya
  no existe o ya está cancelado?». Es la tarjeta roja típica de la consistencia eventual. [opinión]

## 7 · Cobertura

Cada casilla cuenta cubierta cuando tiene respuesta o queda `[PENDIENTE]` con dueño (`investigacion/04` §5.1).

- [ ] Tipo de mensaje y nombre del glosario
- [ ] Alcance (interno, integración o ambos)
- [ ] Dueño del esquema y lista de consumidores con su reacción
- [ ] Estilo (notificación o estado) y lista cerrada de campos, sin datos sensibles
- [ ] Publicación atómica (registro u outbox) y transacción del productor
- [ ] Modo de consumo y transacción del consumidor
- [ ] Idempotencia: mecanismo y clave
- [ ] Orden: clave o «no importa», justificado
- [ ] Fallos: reintentos, destino final y alerta
- [ ] Sobre y metadatos (`eventoId`, `ocurrioEn`, agregado; CloudEvents si sale)
- [ ] Evolución: modo de compatibilidad y regla de cambios
- [ ] Retención de publicaciones y ventana de deduplicación
- [ ] Contrato documentado (AsyncAPI o canvas)
- [ ] Pruebas: publicación, duplicado y fallo, cada una como caso dado/cuando/entonces
- [ ] Comando: correlación y respuesta · Saga: pasos y compensaciones (si aplica)

## 8 · Antipatrones

| Antipatrón | Señal detectable | Fuente |
|---|---|---|
| Comando pasivo-agresivo | El evento se llama por la acción que espera (`InventarioDebeReservar`) o tiene un solo consumidor obligatorio | Fowler 2017 |
| Evento CRUD | `PedidoActualizado` sin hecho de negocio; consumidores que comparan campos para adivinar qué pasó | `investigacion/03` §6 |
| Evento que lleva la entidad | Campo de tipo `Pedido` o `@Entity` dentro del evento | Vernon; EV-04 |
| Escritura doble | `repo.save()` y `kafkaTemplate.send()` en el mismo método sin outbox | Richardson, *Outbox* |
| Publicar tras el commit sin registro | `@Async @TransactionalEventListener` sin Event Publication Registry | Modulith, *Events* («the event publication is lost») |
| Transacción inflada | `@EventListener` síncrono que guarda otro agregado en la transacción del productor | Modulith, *Events*; Vernon |
| Consumidor no idempotente | Listener que suma, descuenta o envía correo sin comprobar duplicado | Richardson, *Idempotent Consumer* |
| Deduplicar por id de entidad | Clave única en `pedido_id` en vez de en `(consumidor, evento_id)` | `investigacion/04` §3.2 P7 |
| Orden global supuesto | Una sola partición «para que llegue en orden» o consumidor que falla con desorden | Kafka; Zalando #212 |
| Mensaje envenenado | Reintento sin límite; cola bloqueada por un mensaje | EIP, *Dead Letter Channel* |
| Evento de dominio como contrato público | El `record` interno se externaliza sin `mapping` y sin versión | Microsoft; EV-11 |
| Cambio incompatible | Renombrar o quitar un campo obligatorio; cambiar su tipo | Confluent; Zalando #209 |
| Módulo «common» de eventos | Todos los módulos dependen de un paquete compartido de eventos | Zalando #207; `investigacion/02` ítem 19 |
| Flujo invisible | Cadena de 4 o más eventos que nadie puede leer en el código | Fowler 2017 |
| Registro sin purga | `event_publication` con millones de filas completadas | Modulith, *Event Publication Completion* |
| Reenvío al arrancar en multiinstancia | `republish-outstanding-events-on-restart=true` con varias réplicas | Modulith, *Appendix* |

## 9 · Código de referencia real

Todos los archivos se leyeron el 2026-10-15 en el commit indicado. Los fragmentos están copiados sin
cambios, salvo los `import` y las líneas en blanco quitados y los recortes marcados con `// …`.

**E1 · Publicar un evento y escucharlo en otro módulo** (servicio de aplicación + adaptador de entrada).
spring-projects/spring-modulith, tag 2.1.1 · Apache-2.0.
[OrderManagement.java#L29-L40](https://github.com/spring-projects/spring-modulith/blob/83ce54cfc5229f9fe748afc332d8c728ecd0b81c/spring-modulith-examples/spring-modulith-example-full/src/main/java/example/order/OrderManagement.java#L29-L40) ·
[InventoryManagement.java#L31-L51](https://github.com/spring-projects/spring-modulith/blob/83ce54cfc5229f9fe748afc332d8c728ecd0b81c/spring-modulith-examples/spring-modulith-example-full/src/main/java/example/inventory/InventoryManagement.java#L31-L51)

```java
@Service
@RequiredArgsConstructor
public class OrderManagement {
	private final @NonNull ApplicationEventPublisher events;
	// …
	@Transactional
	public void complete(Order order) {
		events.publishEvent(new OrderCompleted(order.getId()));
	}
}

@Service
@RequiredArgsConstructor
class InventoryManagement {            // package-private: no es API del módulo inventory
	// …
	@ApplicationModuleListener
	void on(OrderCompleted event) throws InterruptedException {
		var orderId = event.orderId();
		LOG.info("Received order completion for {}.", orderId);
		// …
	}
}
```
*Copiar:* la publicación dentro de `@Transactional`, el listener package-private y el evento con un id
tipado (`OrderIdentifier`). *No copiar:* la publicación sin cambiar el agregado (es una demo) ni el
`Thread.sleep`. `@ApplicationModuleListener` equivale a `@Async` + `@Transactional(REQUIRES_NEW)` +
`@TransactionalEventListener` ([ApplicationModuleListener.java](https://github.com/spring-projects/spring-modulith/blob/83ce54cfc5229f9fe748afc332d8c728ecd0b81c/spring-modulith-events/spring-modulith-events-api/src/main/java/org/springframework/modulith/events/ApplicationModuleListener.java)).

**E2 · Marcar un evento para externalizarlo** (selección de eventos de integración).
spring-projects/spring-modulith, tag 2.1.1 · Apache-2.0.
[kafka/OrderCompleted.java#L26-L27](https://github.com/spring-projects/spring-modulith/blob/83ce54cfc5229f9fe748afc332d8c728ecd0b81c/spring-modulith-examples/spring-modulith-example-kafka/src/main/java/example/order/OrderCompleted.java#L26-L27) ·
[outbox/application.properties](https://github.com/spring-projects/spring-modulith/blob/83ce54cfc5229f9fe748afc332d8c728ecd0b81c/spring-modulith-examples/spring-modulith-example-outbox/src/main/resources/application.properties)

```java
@Externalized
public record OrderCompleted(OrderIdentifier orderId) implements DomainEvent {}
```
```properties
spring.modulith.events.externalization.mode=outbox
```
*Copiar:* el `record` inmutable con la interfaz `DomainEvent` de jMolecules y el modo `outbox` para lo que
sale. *No copiar:* el `@Externalized` sin destino ni clave, que publica en `order.OrderCompleted` sin
clave de partición. Tampoco publicar el evento de dominio sin `mapping` (P17).

**E3 · Probar que el registro conserva lo que falló** (prueba del EPR).
spring-projects/spring-modulith, tag 2.1.1 · Apache-2.0.
[EventPublicationRegistryTests.java#L38-L78](https://github.com/spring-projects/spring-modulith/blob/83ce54cfc5229f9fe748afc332d8c728ecd0b81c/spring-modulith-examples/spring-modulith-example-full/src/test/java/example/order/EventPublicationRegistryTests.java#L38-L78)

```java
@ApplicationModuleTest
@Import(FailingAsyncTransactionalEventListener.class)
@DirtiesContext
@RequiredArgsConstructor
class EventPublicationRegistryTests {
	private final OrderManagement orders;
	private final EventPublicationRegistry registry;
	private final FailingAsyncTransactionalEventListener listener;
	@Test
	void leavesPublicationIncompleteForFailingListener(Scenario scenario) throws Exception {
		var order = new Order();
		scenario.stimulate(() -> orders.complete(order))
				.andWaitForStateChange(listener::getEx)
				.andVerify(__ -> {
					assertThat(registry.findIncompletePublications()).hasSize(1);
				});
	}
	static class FailingAsyncTransactionalEventListener {
		@Getter Exception ex;
		@ApplicationModuleListener
		void foo(OrderCompleted event) {
			// … lanza IllegalStateException y la guarda en ex
		}
	}
}
```
*Copiar:* `Scenario.stimulate(...).andWaitForStateChange(...)` para esperar a un listener asíncrono y
la aserción sobre lo incompleto. *No copiar:* `@DirtiesContext` en cada prueba si no hace falta.

**E4 · Evento de dominio con id, instante y agregado** (contrato del evento).
ddd-by-examples/library, `master` en 5225ff7 · MIT.
[DomainEvent.java#L6-L13](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/main/java/io/pillopl/library/commons/events/DomainEvent.java#L6-L13) ·
[PatronEvent.java#L43-L64](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/main/java/io/pillopl/library/lending/patron/model/PatronEvent.java#L43-L64)

```java
public interface DomainEvent {
    UUID getEventId();
    UUID getAggregateId();
    Instant getWhen();
}

    @Value
    class BookPlacedOnHold implements PatronEvent {
        @NonNull UUID eventId = UUID.randomUUID();
        @NonNull Instant when;
        @NonNull UUID patronId;
        @NonNull UUID bookId;
        @NonNull BookType bookType;
        @NonNull UUID libraryBranchId;
        @NonNull Instant holdFrom;
        Instant holdTill;
        // … fábrica bookPlacedOnHoldNow(...)
    }
```
*Copiar:* los tres metadatos mínimos (id, agregado, instante) y que la carga lleve ids y valores, no
objetos `Book` ni `Patron`. *No copiar:* `UUID.randomUUID()` e `Instant.now()` dentro del evento. Son
difíciles de probar: inyecta `Clock` y un generador de ids (`investigacion/02` ítem 21).

**E5 · Outbox casero y consumidor idempotente por estado** (adaptadores de salida y de entrada).
ddd-by-examples/library, 5225ff7 · MIT.
[StoreAndForwardDomainEventPublisher.java#L11-L29](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/main/java/io/pillopl/library/commons/events/publisher/StoreAndForwardDomainEventPublisher.java#L11-L29) ·
[PatronEventsHandler.java#L24-L71](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/main/java/io/pillopl/library/lending/book/application/PatronEventsHandler.java#L24-L71)

```java
@AllArgsConstructor
public class StoreAndForwardDomainEventPublisher implements DomainEvents {
    private final DomainEvents eventsPublisher;
    private final EventsStorage eventsStorage;
    @Override
    public void publish(DomainEvent event) {
        eventsStorage.save(event);
    }
    @Scheduled(fixedRate = 3000L)
    @Transactional
    public void publishAllPeriodically() {
        List<DomainEvent> domainEvents = eventsStorage.toPublish();
        domainEvents.forEach(eventsPublisher::publish);
        eventsStorage.published(domainEvents);
    }
}

    @EventListener
    void handle(BookPlacedOnHold bookPlacedOnHold) {
        bookRepository.findBy(new BookId(bookPlacedOnHold.getBookId()))
                .map(book -> handleBookPlacedOnHold(book, bookPlacedOnHold))
                .map(this::saveBook);
    }
    // …
    private BookOnHold raiseDuplicateHoldFoundEvent(BookOnHold onHold, BookPlacedOnHold bookPlacedOnHold) {
        if(onHold.by(new PatronId(bookPlacedOnHold.getPatronId()))) {
            return onHold;      // mismo lector: entrega repetida, no hace nada
        }
        // … publica BookDuplicateHoldFound
```
*Copiar:* el puerto `DomainEvents` como decorador (guardar y reenviar) y la idempotencia por estado.
*No copiar:* el relay con `@Scheduled` sin bloqueo entre instancias, ni el `@EventListener` síncrono
para cruzar contextos (en este repositorio el publicador configurado es `JustForwardDomainEventPublisher`,
que es síncrono). Hoy lo resuelve el registro de Modulith.

**E6 · Guardar, publicar e iniciar la saga en una transacción** (outbox con Eventuate Tram).
microservices-patterns/ftgo-application, `master` en 558dfc5 · Apache-2.0 (LICENSE.md; GitHub lo marca
NOASSERTION). [OrderService.java#L68-L92](https://github.com/microservices-patterns/ftgo-application/blob/558dfc53b11d30a5f1d995c0c6d58d5106c28189/ftgo-order-service/src/main/java/net/chrisrichardson/ftgo/orderservice/domain/OrderService.java#L68-L92)

```java
  @Transactional
  public Order createOrder(long consumerId, long restaurantId, DeliveryInformation deliveryInformation,
                           List<MenuItemIdAndQuantity> lineItems) {
    // … carga el restaurante y arma las líneas
    ResultWithDomainEvents<Order, OrderDomainEvent> orderAndEvents =
            Order.createOrder(consumerId, restaurant, deliveryInformation, orderLineItems);
    Order order = orderAndEvents.result;
    orderRepository.save(order);
    orderAggregateEventPublisher.publish(order, orderAndEvents.events);
    OrderDetails orderDetails = new OrderDetails(consumerId, restaurantId, orderLineItems, order.getOrderTotal());
    CreateOrderSagaState data = new CreateOrderSagaState(order.getId(), orderDetails);
    sagaInstanceFactory.create(createOrderSaga, data);
    // …
    return order;
  }
```
*Copiar:* el agregado devuelve sus eventos (`ResultWithDomainEvents`) y el servicio los publica en la
misma transacción en que guarda. Eventuate escribe el mensaje en una tabla outbox (E10). *No copiar:* la
entidad JPA como agregado (`@Entity` en `Order`), que rompe la regla 1 de `investigacion/02`.

**E7 · Saga orquestada con compensaciones**. ftgo-application, 558dfc5 · Apache-2.0.
[CreateOrderSaga.java#L17-L36](https://github.com/microservices-patterns/ftgo-application/blob/558dfc53b11d30a5f1d995c0c6d58d5106c28189/ftgo-order-service/src/main/java/net/chrisrichardson/ftgo/orderservice/sagas/createorder/CreateOrderSaga.java#L17-L36)

```java
  public CreateOrderSaga(OrderServiceProxy orderService, ConsumerServiceProxy consumerService, KitchenServiceProxy kitchenService,
                         AccountingServiceProxy accountingService) {
    this.sagaDefinition =
             step()
              .withCompensation(orderService.reject, CreateOrderSagaState::makeRejectOrderCommand)
            .step()
              .invokeParticipant(consumerService.validateOrder, CreateOrderSagaState::makeValidateOrderByConsumerCommand)
            .step()
              .invokeParticipant(kitchenService.create, CreateOrderSagaState::makeCreateTicketCommand)
              .onReply(CreateTicketReply.class, CreateOrderSagaState::handleCreateTicketReply)
              .withCompensation(kitchenService.cancel, CreateOrderSagaState::makeCancelCreateTicketCommand)
            .step()
                .invokeParticipant(accountingService.authorize, CreateOrderSagaState::makeAuthorizeCommand)
            .step()
              .invokeParticipant(kitchenService.confirmCreate, CreateOrderSagaState::makeConfirmCreateTicketCommand)
            .step()
              .invokeParticipant(orderService.approve, CreateOrderSagaState::makeApproveOrderCommand)
            .build();
  }
```
*Copiar:* un paso por participante, la compensación junto al paso que la necesita y el último paso sin
compensación (pivote). *No copiar* dentro de un monolito modular: allí basta un *process manager* con
listeners (§10 T5). [opinión]

**E8 · Manejador de comandos asíncronos con respuesta**. ftgo-application, 558dfc5 · Apache-2.0.
[KitchenServiceCommandHandler.java#L22-L54](https://github.com/microservices-patterns/ftgo-application/blob/558dfc53b11d30a5f1d995c0c6d58d5106c28189/ftgo-kitchen-service/src/main/java/net/chrisrichardson/ftgo/kitchenservice/messagehandlers/KitchenServiceCommandHandler.java#L22-L54)

```java
  public CommandHandlers commandHandlers() {
    return SagaCommandHandlersBuilder
            .fromChannel(KitchenServiceChannels.COMMAND_CHANNEL)
            .onMessage(CreateTicket.class, this::createTicket)
            .onMessage(ConfirmCreateTicket.class, this::confirmCreateTicket)
            .onMessage(CancelCreateTicket.class, this::cancelCreateTicket)
            // … comandos de cancelar y revisar
            .build();
  }
  private Message createTicket(CommandMessage<CreateTicket>
                                                cm) {
    CreateTicket command = cm.getCommand();
    long restaurantId = command.getRestaurantId();
    Long ticketId = command.getOrderId();
    TicketDetails ticketDetails = command.getTicketDetails();
    try {
      Ticket ticket = kitchenService.createTicket(restaurantId, ticketId, ticketDetails);
      CreateTicketReply reply = new CreateTicketReply(ticket.getId());
      return withLock(Ticket.class, ticket.getId()).withSuccess(reply);
    } catch (RestaurantDetailsVerificationException e) {
      return withFailure();
    }
  }
```
*Copiar:* el canal de comandos es del receptor (`KitchenServiceChannels`), cada comando tiene una
respuesta de éxito o de fallo y el fallo de negocio se traduce en respuesta, no en excepción. *No
copiar:* el `@Autowired` en el campo (`investigacion/02` ítem 14).

**E9 · Consumidor idempotente con tabla de recibidos** (Idempotent Consumer).
eventuate-tram/eventuate-tram-core, `master` en 7e84c12 · Apache-2.0 (LICENSE.md).
[SqlTableBasedDuplicateMessageDetector.java#L31-L55](https://github.com/eventuate-tram/eventuate-tram-core/blob/7e84c12ee682244cfbfcb583eb10e99009e73566/eventuate-tram-consumer-jdbc/src/main/java/io/eventuate/tram/consumer/jdbc/SqlTableBasedDuplicateMessageDetector.java#L31-L55)

```java
  public boolean isDuplicate(String consumerId, String messageId) {
    try {
      String table = eventuateSchema.qualifyTable("received_messages");
      eventuateJdbcStatementExecutor.update("insert into %s(consumer_id, message_id, creation_time) values(?, ?, %s)".formatted(
              table,
              currentTimeInMillisecondsSql),
              consumerId,
              messageId);
      return false;
    } catch (EventuateDuplicateKeyException e) {
      logger.info("Message duplicate: consumerId = {}, messageId = {}", consumerId, messageId);
      return true;
    }
  }
  @Override
  public void doWithMessage(SubscriberIdAndMessage subscriberIdAndMessage, Runnable callback) {
    eventuateTransactionTemplate.executeInTransaction(() -> {
      if (!isDuplicate(subscriberIdAndMessage.getSubscriberId(), subscriberIdAndMessage.getMessage().getId()))
        callback.run();
      return null;
    });
  }
```
*Copiar:* la clave `(consumer_id, message_id)` y el efecto en la **misma** transacción que el `insert`.
*No copiar tal cual en PostgreSQL:* tras un error de clave duplicada la transacción queda abortada. Usa
`ON CONFLICT DO NOTHING` y revisa el número de filas (§10 T3). [opinión]

**E10 · Productor que escribe en la tabla outbox**. eventuate-tram-core, 7e84c12 · Apache-2.0.
[MessageProducerJdbcImpl.java#L27-L35](https://github.com/eventuate-tram/eventuate-tram-core/blob/7e84c12ee682244cfbfcb583eb10e99009e73566/eventuate-tram-producer-jdbc/src/main/java/io/eventuate/tram/messaging/producer/jdbc/MessageProducerJdbcImpl.java#L27-L35)

```java
  public void send(Message message) {
    String id =  eventuateCommonJdbcOperations.insertIntoMessageTable(idGenerator,
              message.getPayload(),
              message.getRequiredHeader(Message.DESTINATION),
              message.getHeaders(),
              eventuateSchema);
    message.setHeader(Message.ID, id);
  }
```
*Copiar:* «enviar» es un `INSERT` en la transacción del negocio; un relay (CDC o *polling*) lo publica
después (README de eventuate-tram-core: «inserts events into an `OUTBOX` table as part of the ACID
transaction»). *No copiar:* nada en particular; es infraestructura del framework.

## 10 · Plantillas de código

Esqueletos para Java 21 + Spring Boot 4 + Spring Modulith 2.1. Cada `TODO(Pn)` lo decide la pregunta n.

**T1 · Evento de dominio, registrado por el agregado y publicado por el caso de uso** (basado en E1, E4 y E6).

```java
package com.ejemplo.pedidos;                       // API pública del módulo · TODO(P16)

public record PedidoConfirmado(                    // TODO(P2) nombre del glosario
    UUID eventoId, PedidoId pedidoId, ClienteId clienteId,
    List<LineaConfirmada> lineas, Dinero total,    // TODO(P5, P6) lista cerrada de campos
    Instant ocurrioEn) implements DomainEvent {    // org.jmolecules.event.types.DomainEvent
  public record LineaConfirmada(Sku sku, int cantidad) {}
  public PedidoConfirmado { lineas = List.copyOf(lineas); }
}

// pedidos.domain — sin Spring
public final class Pedido {
  private final List<Object> eventos = new ArrayList<>();
  public void confirmar(Clock reloj, Supplier<UUID> ids) {
    // … invariantes y transición CREADO → CONFIRMADO
    eventos.add(new PedidoConfirmado(ids.get(), id, clienteId, lineasConfirmadas(), total(), reloj.instant()));
  }
  public List<Object> extraerEventos() { var e = List.copyOf(eventos); eventos.clear(); return e; }
}

// pedidos.application.port.out — puerto como en library (E4/E5)
public interface EventosDePedido { void publicar(Object evento); }

// pedidos.adapter.out.eventos
@Component
class EventosDePedidoSpring implements EventosDePedido {
  private final ApplicationEventPublisher publisher;
  EventosDePedidoSpring(ApplicationEventPublisher publisher) { this.publisher = publisher; }
  public void publicar(Object evento) { publisher.publishEvent(evento); }   // el EPR lo registra · TODO(P7)
}

// pedidos.application
@Service
class ConfirmarPedidoServicio implements ConfirmarPedido {
  // … constructor con Pedidos, EventosDePedido, Clock, Supplier<UUID>
  @Transactional
  public void ejecutar(ConfirmarPedidoComando c) {
    var pedido = pedidos.porId(c.pedidoId()).orElseThrow(() -> new PedidoNoExiste(c.pedidoId()));
    pedido.confirmar(reloj, ids);
    pedidos.guardar(pedido);
    pedido.extraerEventos().forEach(eventos::publicar);   // misma transacción que guardar
  }
}
```

**T2 · Listener entre módulos, idempotente** (basado en E1, E5 y E9).

```java
package com.ejemplo.inventario.adapter.in.eventos;

@Component
class ReservarStockAlConfirmarPedido {
  private static final String CONSUMIDOR = "inventario.reservar-stock";   // TODO(P16) estable
  private final EventosProcesados procesados;       // puerto de salida · TODO(P9)
  private final ReservarStock reservarStock;         // puerto de entrada de inventario
  // … constructor

  @ApplicationModuleListener(id = CONSUMIDOR)        // async + REQUIRES_NEW + after commit · TODO(P8)
  void on(PedidoConfirmado evento) {
    if (!procesados.registrarSiEsNuevo(CONSUMIDOR, evento.eventoId())) {
      return;                                        // duplicado: sin efecto
    }
    reservarStock.ejecutar(new ReservarStockComando(evento.pedidoId(), evento.lineas()));
    // TODO(P4, cupo de §6): ¿qué hace si el pedido ya se canceló?
  }
}
```

**T3 · Puerto y adaptador de deduplicación** (basado en E9; `ON CONFLICT` es [opinión] para PostgreSQL).

```java
public interface EventosProcesados {                 // inventario.application.port.out
  /** true si es la primera vez; corre en la transacción del listener. */
  boolean registrarSiEsNuevo(String consumidor, UUID eventoId);
}

@Repository
class EventosProcesadosJdbc implements EventosProcesados {
  private final JdbcClient jdbc;
  EventosProcesadosJdbc(JdbcClient jdbc) { this.jdbc = jdbc; }
  public boolean registrarSiEsNuevo(String consumidor, UUID eventoId) {
    return jdbc.sql("""
        insert into eventos_procesados (consumidor, evento_id, procesado_en)
        values (?, ?, now()) on conflict do nothing""")
      .params(consumidor, eventoId).update() == 1;
  }
}
```
```sql
-- V7__eventos_procesados.sql · TODO(P12) ventana de purga
create table eventos_procesados (
  consumidor   text        not null,
  evento_id    uuid        not null,
  procesado_en timestamptz not null,
  primary key (consumidor, evento_id)
);
```

**T4 · Evento de integración externalizado** (basado en E2 y en *Externalizing Events*).

```java
package com.ejemplo.pedidos.adapter.out.mensajeria;

public record PedidoConfirmadoV1(String pedidoId, String clienteId, long totalCentavos,
                                 String moneda, Instant ocurrioEn) {     // TODO(P17, P18) contrato público
  static PedidoConfirmadoV1 desde(PedidoConfirmado e) {
    return new PedidoConfirmadoV1(e.pedidoId().toString(), e.clienteId().toString(),
        e.total().centavos(), e.total().moneda().getCurrencyCode(), e.ocurrioEn());
  }
}

@Configuration
class ExternalizacionDePedidos {
  @Bean
  EventExternalizationConfiguration externalizacion() {
    return EventExternalizationConfiguration.externalizing()
        .select(EventExternalizationConfiguration.annotatedAsExternalized())   // PedidoConfirmado lleva @Externalized("pedidos.pedido-confirmado")
        .mapping(PedidoConfirmado.class, PedidoConfirmadoV1::desde)
        .routeKey(PedidoConfirmado.class, e -> e.pedidoId().toString())        // TODO(P10) clave de orden
        .build();
  }
}
```
```properties
spring.modulith.events.externalization.mode=outbox          # TODO(P7)
spring.modulith.events.completion-mode=archive              # TODO(P12)
spring.modulith.events.republish-outstanding-events-on-restart=false
spring.modulith.events.staleness.processing=PT15M           # TODO(P11)
```

**T5 · Comando asíncrono con respuesta y process manager** (basado en E7 y E8; usar el mecanismo de
eventos de Modulith para un comando es [opinión]: verifica en una prueba que tenga un solo listener).

```java
// inventario (dueño del contrato del comando) · TODO(P1, P4)
public record ReservarStockSolicitado(UUID correlacionId, PedidoId pedidoId, List<LineaConfirmada> lineas) {}
public sealed interface ResultadoReserva permits StockReservado, ReservaRechazada {
  UUID correlacionId();
}
public record StockReservado(UUID correlacionId, ReservaId reservaId) implements ResultadoReserva {}
public record ReservaRechazada(UUID correlacionId, String motivo) implements ResultadoReserva {}

// pedidos.adapter.in.eventos — process manager con estado persistido · rama saga de §6
@Component
class ProcesoDeConfirmacion {
  @ApplicationModuleListener(id = "pedidos.proceso-confirmacion.reserva")
  void on(ResultadoReserva r) {
    switch (r) {
      case StockReservado ok      -> avanzar.ejecutar(new SolicitarCobro(ok.correlacionId()));
      case ReservaRechazada no    -> compensar.ejecutar(new RechazarPedido(no.correlacionId(), no.motivo()));
    }
  }
  // … constructor con AvanzarProceso y CompensarProceso (puertos de entrada)
}
```

**T6 · Pruebas** (basado en E3 y en *Testing* de Modulith).

```java
@ApplicationModuleTest
class ConfirmarPedidoEventosTest {
  @Autowired ConfirmarPedido confirmar;
  @Test void publicaPedidoConfirmado(Scenario scenario) {          // TODO(P20)
    scenario.stimulate(() -> confirmar.ejecutar(comandoValido("p-77")))
        .andWaitForEventOfType(PedidoConfirmado.class)
        .matchingMappedValue(PedidoConfirmado::pedidoId, PedidoId.de("p-77"))
        .toArrive();
  }
}

class ReservarStockAlConfirmarPedidoTest {                          // sin Spring
  @Test void unDuplicadoNoReservaDosVeces() {
    var procesados = new EventosProcesadosEnMemoria();
    var reservar = new ReservarStockEspia();
    var listener = new ReservarStockAlConfirmarPedido(procesados, reservar);
    var evento = PedidoConfirmadoMadre.uno();
    listener.on(evento);
    listener.on(evento);                                             // entrega repetida
    assertThat(reservar.invocaciones()).isEqualTo(1);
  }
}
```

**T7 · Contrato AsyncAPI 3.0** (solo si P3 = sale).

```yaml
asyncapi: 3.0.0
info: { title: pedidos-eventos, version: 1.0.0 }
channels:
  pedidoConfirmado:
    address: pedidos.pedido-confirmado
    messages:
      pedidoConfirmadoV1:
        contentType: application/json
        payload: { $ref: './esquemas/pedido-confirmado.v1.json' }   # TODO(P18)
operations:
  publicarPedidoConfirmado:
    action: send
    channel: { $ref: '#/channels/pedidoConfirmado' }
```

## 11 · Qué se genera

Orden dentro del segmento (de afuera hacia adentro, `investigacion/05` §2.2):

1. El `record` del evento (o del comando y sus respuestas) en la API del módulo dueño, con su prueba de
   construcción (EV-02, EV-03).
2. El registro del evento en el agregado (si viene de entidad-agregado) y la publicación en el caso de
   uso, con la prueba `Scenario` de publicación (EV-05, EV-19).
3. El listener en el módulo consumidor, con la prueba de duplicado sin Spring (EV-08).
4. El puerto `EventosProcesados`, su adaptador JDBC, la migración y la prueba de integración con
   PostgreSQL en contenedor (sale hacia adaptador-persistencia).
5. Las propiedades `spring.modulith.events.*` y, si sale, la configuración de externalización, el
   `record` de integración versionado y el contrato AsyncAPI con su esquema (EV-11, EV-12, EV-14).
6. La prueba del listener que falla: la publicación queda FAILED o incompleta (EV-10).
7. Si hay saga: el process manager, su estado y una prueba por compensación (EV-21).
8. Las reglas ArchUnit de §4 y `ApplicationModules.of(App.class).verify()` en `ModularityTests`.

## 12 · Referencias

- Hohpe y Woolf, *Enterprise Integration Patterns* (2003), https://www.enterpriseintegrationpatterns.com/ — catálogo de canales y mensajes: Event, Command y Document Message; Publish-Subscribe y Point-to-Point; Dead Letter, Invalid Message, Idempotent Receiver, Correlation Identifier, Return Address, Envelope Wrapper, Format Indicator, Message Translator, Canonical Data Model, Claim Check, Resequencer, Process Manager, Guaranteed Delivery y Durable Subscriber (cada uno en `/patterns/messaging/<Nombre>.html`).
- C. Richardson, *Microservices Patterns* (Manning, 2018), https://www.manning.com/books/microservices-patterns — Saga, outbox e idempotencia en servicios; código en ftgo-application.
- microservices.io: [Transactional Outbox](https://microservices.io/patterns/data/transactional-outbox.html), [Idempotent Consumer](https://microservices.io/patterns/communication-style/idempotent-consumer.html), [Saga](https://microservices.io/patterns/data/saga.html), [Polling Publisher](https://microservices.io/patterns/data/polling-publisher.html), [Transaction Log Tailing](https://microservices.io/patterns/data/transaction-log-tailing.html) y [Domain Event](https://microservices.io/patterns/data/domain-event.html) — «messages are guaranteed to be sent if and only if the database transaction commits»; el relay puede duplicar; clave `(subscriberId, messageID)`.
- M. Fowler, *What do you mean by "Event-Driven"?* (2017), https://martinfowler.com/articles/201701-event-driven.html — notificación, Event-Carried State Transfer, Event Sourcing y CQRS; el riesgo del flujo invisible y del comando pasivo-agresivo.
- M. Fowler, [Domain Event](https://martinfowler.com/eaaDev/DomainEvent.html) y [Tolerant Reader](https://martinfowler.com/bliki/TolerantReader.html) — el evento como hecho del dominio; el consumidor enlaza solo lo que usa.
- Microsoft, [Domain events: design and implementation](https://learn.microsoft.com/en-us/dotnet/architecture/microservices/microservice-ddd-cqrs-patterns/domain-events-design-implementation) — evento de dominio (mismo dominio, puede ser síncrono) frente a evento de integración (siempre asíncrono, solo tras persistir); despacho antes o después del commit.
- CloudEvents 1.0, [spec](https://github.com/cloudevents/spec/blob/main/cloudevents/spec.md), [Kafka binding](https://github.com/cloudevents/spec/blob/main/cloudevents/bindings/kafka-protocol-binding.md), [partitioning](https://github.com/cloudevents/spec/blob/main/cloudevents/extensions/partitioning.md) — `id`, `source`, `specversion` y `type` obligatorios; «source + id is unique»; `partitionkey`; eventos compactos.
- AsyncAPI 3.0, https://www.asyncapi.com/docs/reference/specification/v3.0.0 — canales, operaciones (`send`/`receive`), mensajes con `payload`, `headers` y `correlationId`.
- M. Kleppmann, *Designing Data-Intensive Applications* (O'Reilly, 2017), cap. 4 «Encoding and Evolution», https://dataintensive.net/ — compatibilidad hacia atrás (el código nuevo lee datos viejos) y hacia adelante (el viejo lee datos nuevos).
- Confluent, [Schema Evolution and Compatibility](https://docs.confluent.io/platform/current/schema-registry/fundamentals/schema-evolution.html) — BACKWARD es el defecto; con BACKWARD se actualizan primero los consumidores; variantes TRANSITIVE.
- Apache Kafka, [Introduction](https://kafka.apache.org/intro) — misma clave, misma partición; el consumidor lee la partición en el orden escrito.
- Spring Modulith 2.1, [Events](https://docs.spring.io/spring-modulith/reference/events.html), [Testing](https://docs.spring.io/spring-modulith/reference/testing.html), [Appendix](https://docs.spring.io/spring-modulith/reference/appendix.html) — `@ApplicationModuleListener`, Event Publication Registry y sus estados (PUBLISHED, PROCESSING, COMPLETED, FAILED, RESUBMITTED), `FailedEventPublications`, *completion-mode*, externalización y modo `outbox`, `Scenario`.
- Spring Framework, [Transaction-bound events](https://docs.spring.io/spring-framework/reference/data-access/transaction/event.html) — `AFTER_COMMIT` por defecto; sin transacción el listener no corre salvo `fallbackExecution`.
- Spring for Apache Kafka, [Non-blocking retries](https://docs.spring.io/spring-kafka/reference/retrytopic/how-the-pattern-works.html) — tópicos de reintento y DLT; «you lose Kafka's ordering guarantees for that topic».
- Zalando, [RESTful API and Event Guidelines](https://opensource.zalando.com/restful-api-guidelines/) — #194, #200, #202, #203, #207, #209, #211, #212, #214, #245 y #247 sobre propiedad, categorías, orden, duplicados y compatibilidad.
- Debezium, [Outbox Event Router](https://debezium.io/documentation/reference/stable/transformations/outbox-event-router.html) y [Reliable Microservices Data Exchange With the Outbox Pattern](https://debezium.io/blog/2019/02/19/reliable-microservices-data-exchange-with-the-outbox-pattern/) — outbox por lectura del log de la BD.
- Gamma, Helm, Johnson y Vlissides, *Design Patterns* (1994), Observer; [Refactoring.Guru, Observer](https://refactoring.guru/design-patterns/observer) — notificación uno a muchos en memoria; base del publish-subscribe sin broker.
- V. Vernon, [Effective Aggregate Design, parte II](https://www.dddcommunity.org/wp-content/uploads/files/pdf_articles/Vernon_2011_2.pdf) — consistencia eventual entre agregados mediante eventos de dominio.
- G. Young, [Versioning in an Event Sourced System](https://leanpub.com/esversioning) — versionado de eventos y *upcasting*, para la rama de event sourcing.
- Código: [spring-projects/spring-modulith](https://github.com/spring-projects/spring-modulith/tree/83ce54cfc5229f9fe748afc332d8c728ecd0b81c/spring-modulith-examples), [ddd-by-examples/library](https://github.com/ddd-by-examples/library/tree/5225ff7a0f0c1e0751b91cdd64f925cd001d7555), [microservices-patterns/ftgo-application](https://github.com/microservices-patterns/ftgo-application/tree/558dfc53b11d30a5f1d995c0c6d58d5106c28189), [eventuate-tram/eventuate-tram-core](https://github.com/eventuate-tram/eventuate-tram-core/tree/7e84c12ee682244cfbfcb583eb10e99009e73566).
