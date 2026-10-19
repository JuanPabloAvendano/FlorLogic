---
plantilla: caso-de-uso
aplica_a: la capa de aplicación de un comportamiento — puerto de entrada (interfaz del caso de uso), comando o consulta, resultado, servicio de aplicación que orquesta, frontera transaccional, catálogo de errores que salen hacia el adaptador, y la firma (solo la firma) de cada puerto de salida con su doble en memoria y su prueba de contrato
entra_desde: endpoint (su P20 fija la firma del puerto de entrada) · evento-mensaje (el listener invoca un puerto de entrada) · trabajo programado
sale_hacia: entidad-agregado (el interior del agregado que el caso de uso carga y llama) · adaptador-persistencia (la implementación del puerto de repositorio) · evento-mensaje (los eventos que el caso de uso publica y su outbox)
---
# Plantilla · Puerto y caso de uso (capa de aplicación)

## 0 · Instrucciones para el agente

1. Usa esta plantilla cuando haya que detallar **qué hace la aplicación** ante una petición: quién la
   pide, qué pasos orquesta, qué reglas aplica (y quién es dueño de cada una), qué devuelve, cómo falla,
   dónde empieza y termina la transacción y qué necesita del exterior. Es **una sola plantilla
   combinada**: puerto de entrada + caso de uso + firmas de los puertos de salida. [decisión del usuario]
2. **Empieza donde termina `endpoint`.** Si la ronda de endpoint ya fijó la firma del puerto de entrada
   (su P20), confírmala en una línea y no la vuelvas a preguntar (`investigacion/04` §1.2 regla 7). Si
   el disparador es un evento, confirma con `evento-mensaje` qué datos trae.
3. **No bajes al interior del agregado ni a los adaptadores.** Aquí solo se decide *qué* método del
   agregado se invoca y *qué* error puede lanzar. Invariantes, estados, fábricas y eventos por dentro:
   `entidad-agregado`. Tablas, SQL, versión e implementación del repositorio: `adaptador-persistencia`.
   Sobre del evento, outbox y deduplicación: `evento-mensaje`. Esta plantilla entrega a esas tres las
   **firmas** que consumen.
4. Antes de la P1, lee lo que exista: ADR, tarjetas de event storming, la ronda de endpoint, el código
   del módulo (`application/port/in`, `port/out`). Muestra el esqueleto de §1 con los huecos ⟦1⟧…⟦11⟧ y
   la opción ★ como valor provisional (`investigacion/04` §2.3).
5. Haz las preguntas de §5 en orden, 10 por ronda: P1–P10 en la primera, P11–P20 en la siguiente (SKILL.md, etapa 3), **una variable por pregunta**, cada una con su línea de «por
   qué», sus opciones (la ★ primero) y un ejemplo en Java 21 + Spring por opción. Aplica la poda de §6 en
   cuanto una respuesta la active y usa los cupos libres como dice §6.
6. Cada regla de negocio sale con **dos casos** dado/cuando/entonces: uno que la cumple y otro que la
   viola (`investigacion/04` §4.1 regla 2). Cada fallo de la P8 y de la P18 sale con un caso.
7. **Defectos fijados por el usuario** (no se preguntan como abiertos; se confirman):
   - el éxito se devuelve como un `record` de resultado propio del caso de uso;
   - el fallo de negocio es una **excepción de dominio con nombre**; el adaptador de entrada la traduce a
     RFC 9457 (`ProblemDetail`) en un solo lugar;
   - **siempre** hay dobles de prueba: cada puerto de salida tiene una implementación en memoria y una
     prueba de contrato abstracta que heredan la real y la de memoria.
   Si el humano quiere apartarse de un defecto, regístralo como decisión con su motivo (ADR corto).
8. No inventes reglas, cifras, nombres ni actores. Si el humano no sabe, crea un pendiente `Q-n` con la ★
   como supuesto y sigue (`investigacion/04` §1.2 regla 12).
9. Si la P6 deja **todas** las reglas fuera del dominio y no hay invariantes (solo guardar y leer),
   propone capas simples (Transaction Script o CRUD con Spring Data) y marca N/A U-05, U-06 y U-15 sin
   penalizar (`investigacion/02` §6; Fowler, PoEAA, *Transaction Script*).
10. Ofrece código solo desde §10. Si copias de §9, respeta la licencia indicada (buckpal no declara
    licencia: solo se lee).
11. Al terminar, marca §7, calcula la cobertura y lista los pendientes. Cierra con la metapregunta «¿hay
    algo que debí preguntarte sobre este caso de uso?» si queda cupo (`investigacion/04` regla 18).

## 1 · Qué se define primero

El orden sigue la dependencia: cada concepto necesita los anteriores. Los tres primeros son esenciales:
si alguno queda sin responder, la cobertura se limita a 70 (`investigacion/04` §5.1, regla dura).

| ⟦k⟧ | Concepto | Por qué va aquí | Fuente |
|---|---|---|---|
| 1 | **Actor y objetivo** (con disparador y fuente) | Un caso de uso es el contrato de comportamiento entre un actor y el sistema para lograr un objetivo; sin objetivo no hay nombre ni límite. | Cockburn, *Writing Effective Use Cases* (WEUC), nivel «user goal»; `investigacion/04` §3.4 |
| 2 | **Firma del puerto de entrada** (comando → resultado) | Es el contrato interno que el adaptador invoca; fija el mapeo «full» en la entrada. | Hombergs, cap. «Implementing a Use Case»; Martin, *Clean Architecture* (input boundary, request model) |
| 3 | **Reglas de negocio con ejemplos** y dueño de cada una | Decide si el servicio queda delgado (reglas en el dominio) o se vuelve un Transaction Script. | Fowler, PoEAA (*Domain Model* frente a *Transaction Script*); Fowler, *AnemicDomainModel*; Example Mapping |
| 4 | **Precondiciones y flujo principal** (3 a 9 pasos) | El escenario de éxito es la orquestación que escribirá el servicio. | Cockburn, WEUC, «Main Success Scenario» |
| 5 | **Fallos y transacción** | Cada extensión de Cockburn es un error con nombre; la transacción decide qué queda si falla a mitad. | Cockburn, WEUC, «Extensions» y «Minimal Guarantees»; Fowler, PoEAA (*Service Layer*, *Unit of Work*); Vernon, EAD II |
| 6 | **Puertos de salida** (operaciones, tipos, errores) | Toda E/S pasa por un puerto declarado en el núcleo y diseñado según su necesidad. | Cockburn, *Hexagonal*; Hombergs («the shape of the output port interfaces is dictated by the use case»); `investigacion/02` #5 |
| 7 | **Postcondiciones y eventos** | Lo que queda distinto al terminar: es lo que verifican las pruebas y lo que consumen otros módulos. | Cockburn, WEUC, «Success Guarantees»; Evans, *DDD Reference* (Domain Events) |
| 8 | **Idempotencia** | Las peticiones por red y los mensajes llegan repetidos. | `investigacion/03` regla 14; microservices.io, *Transactional Outbox* |
| 9 | **Autorización** | Quién puede ejecutarlo es una regla de la aplicación, igual para todos los disparadores. | Vernon, IDDD cap. 14; [opinión] |
| 10 | **Dobles de prueba** | El núcleo se prueba sin infraestructura; la memoria solo sirve si cumple el mismo contrato que la real. | Cockburn (adaptadores de prueba); Meszaros, *xUnit Test Patterns* (Fake Object); Rainsberger, *Contract tests* |
| 11 | **Variables y criterio medible** | Sin medida, «funciona» es ambiguo; cada valor necesita dueño. | Volere, *fit criterion*; `investigacion/04` regla 16 |

Esqueleto que se muestra antes de la P1 (los valores son la ★ provisional; dominio genérico de pedidos):

```
Caso de uso:  ⟦1 actor + objetivo⟧    → el vendedor confirma un pedido para que se reserve el stock
Disparador:   ⟦1⟧                     → POST /pedidos/{id}:confirmar (endpoint) · fuente: ESC-07
Entrada:      ⟦2 puerto + comando⟧    → ConfirmarPedido.ejecutar(ConfirmarPedidoComando(pedidoId, solicitante))
Salida:       ⟦2 resultado⟧           → record ResultadoConfirmacion(pedidoId, total, confirmadoEn)
Reglas:       ⟦3⟧                     → R1 sin líneas no se confirma (Pedido) · R2 crédito ≥ total (caso de uso + puerto)
Pasos:        ⟦4⟧                     → cargar pedido · autorizar · consultar crédito · pedido.confirmar(ahora) · guardar · publicar
Fallos:       ⟦5⟧                     → PedidoNoExiste 404 · PedidoVacio 422 · CreditoInsuficiente 422 · ConflictoDeVersion 409
Transacción:  ⟦5⟧                     → @Transactional en ConfirmarPedidoServicio · un agregado (Pedido)
Puertos out:  ⟦6⟧                     → Pedidos{porId,guardar} · CreditoDeClientes{disponible} · EventosDePedido{publicar}
Después:      ⟦7⟧                     → Pedido CONFIRMADO · evento PedidoConfirmado (outbox de Modulith)
Repetición:   ⟦8⟧                     → confirmar dos veces = un solo efecto (el agregado lo ignora)
Permiso:      ⟦9⟧                     → solo el vendedor dueño del pedido o un supervisor
Dobles:       ⟦10⟧                    → PedidosEnMemoria · CreditoDeClientesEnMemoria · EventosCapturados · Clock.fixed
Medida:       ⟦11⟧                    → 0 pedidos confirmados sin crédito por semana; p95 < 300 ms
```

## 2 · Funcionamiento apropiado

**Qué es un caso de uso bien hecho.** Martin lo llama *interactor*: los casos de uso «orquestan el flujo
de datos hacia y desde las entidades» y contienen las reglas *de la aplicación* (Martin, *The Clean
Architecture*, 2012). Vernon lo llama *Application Service*: es delgado, coordina tareas sobre el
modelo, controla la transacción y la seguridad, y no contiene lógica de dominio (Vernon, IDDD, cap. 14).
Fowler lo llama *Service Layer*: «define la frontera de una aplicación con una capa de servicios que
establece un conjunto de operaciones disponibles» y controla las transacciones (PoEAA). Cockburn pide
escribir los casos de uso «en la frontera de la aplicación (el hexágono interior)», sin mencionar
tecnología (*Hexagonal Architecture*).

**Responsabilidades del servicio de aplicación** (síntesis de las cuatro fuentes; el orden es [opinión]):

1. Recibe un comando ya válido en su forma (el comando se autovalida; Hombergs).
2. Carga **un** agregado por su puerto de salida (Vernon, EAD II: una transacción, un agregado).
3. Comprueba la autorización con la identidad que trae el comando.
4. Reúne del exterior lo que el agregado necesita para decidir (otro puerto, el reloj, el generador de id).
5. **Invoca un método con nombre de negocio del agregado**: la decisión es del dominio, no del servicio.
6. Guarda el agregado.
7. Publica los eventos que el agregado registró, dentro de la misma transacción (outbox).
8. Arma y devuelve el `record` de resultado. Nunca devuelve el agregado.

**Lo que no hace:** calcular reglas de negocio con `if` largos (`investigacion/02` #11), hablar HTTP o
SQL, conocer DTO web, leer `LocalDateTime.now()` (usa un `Clock` inyectado, `investigacion/02` #21),
llamar a otro caso de uso del mismo módulo dentro de su transacción [opinión], ni capturar y tragar
excepciones.

**Flujo** (un caso de uso de comando disparado por HTTP; la frontera transaccional va en línea doble):

```
adaptador de entrada (endpoint)       NÚCLEO · application                                   NÚCLEO · domain
───────────────────────────────       ═══════════════ @Transactional ═══════════════         ───────────────
DTO → ConfirmarPedidoComando ───────▶ ConfirmarPedidoServicio.ejecutar(c)
                                        1 pedidos.porId(id) ──────────▶ «out» Pedidos ─▶ adaptador-persistencia
                                          └ vacío → throw PedidoNoExiste
                                        2 autorizar(c.solicitante(), pedido) → AccesoDenegado
                                        3 credito.disponible(cliente) ─▶ «out» CreditoDeClientes ─▶ adaptador HTTP
                                        4 pedido.confirmar(reloj.instant()) ─────────────────────▶ Pedido decide
                                          └ invariante rota → PedidoVacio (excepción de dominio) ◀┘
                                        5 pedidos.guardar(pedido) ────▶ UPDATE … WHERE version = ?
                                        6 eventos.publicar(...) ──────▶ registro de publicaciones (outbox)
                                        7 return new ResultadoConfirmacion(…)
                                      ═════════════════════ commit ══════════════════════
ResultadoConfirmacion → DTO 200 ◀─────
excepción → @RestControllerAdvice → ProblemDetail (RFC 9457)
```

**Dónde vive cada pieza** (`investigacion/02` §2; disposición de buckpal y Hombergs):

| Pieza | Paquete | Depende de | La detalla |
|---|---|---|---|
| Puerto de entrada `ConfirmarPedido` | `pedidos.application.port.in` | comando, resultado, tipos del dominio | esta plantilla |
| `ConfirmarPedidoComando`, `ResultadoConfirmacion` | `pedidos.application.port.in` | valores e ids del dominio | esta plantilla |
| `ConfirmarPedidoServicio` | `pedidos.application.service` (package-private) | puertos in/out, dominio, `Clock`, `@Transactional` | esta plantilla |
| Errores de aplicación (`PedidoNoExiste`, `AccesoDenegado`) | `pedidos.application.port.in` | — | esta plantilla |
| Errores de dominio (`PedidoVacio`, `TransicionInvalida`) | `pedidos.domain` | — | `entidad-agregado` T5 |
| Puertos de salida `Pedidos`, `CreditoDeClientes`, `EventosDePedido` | `pedidos.application.port.out` | tipos del dominio | firma aquí; implementación en `adaptador-persistencia` y `evento-mensaje` |
| Dobles en memoria y contratos | `src/test/.../application/port/out` | puerto | esta plantilla (T8, T9) |
| Traducción a `ProblemDetail` | `pedidos.adapter.in.web` | errores | `endpoint` (P16) |

**Reglas de la frontera transaccional** (Spring Framework 7, *Using @Transactional*; verificado):

- En modo proxy (el defecto) «solo se interceptan las llamadas externas que entran por el proxy»: un
  método del servicio que llama a otro método `@Transactional` del mismo objeto **no** abre transacción.
- Por defecto «cualquier `RuntimeException` o `Error` provoca rollback, y una `Exception` comprobada no».
  Por eso las excepciones de dominio y de aplicación extienden `RuntimeException`. Desde 6.2 se puede
  cambiar con `@EnableTransactionManagement(rollbackOn = ALL_EXCEPTIONS)`.
- El equipo de Spring recomienda anotar **la clase concreta**, no la interfaz del puerto.
- Desde 6.0, los métodos `protected` o package-private también son transaccionales con proxies de clase.
- Usa `org.springframework.transaction.annotation.Transactional`. buckpal usa
  `jakarta.transaction.Transactional` (JTA), que no ofrece `readOnly` ni el resto de atributos de la de
  Spring. [opinión]
- Una llamada remota lenta (HTTP) dentro de la transacción retiene la conexión de BD. Haz la consulta
  externa **antes** de abrir la transacción, o deja el efecto externo para después del commit con un
  evento (`investigacion/04` §3.3 P8: «notificar va después del commit, vía evento»).

## 3 · Patrones de diseño

| Patrón | Problema que resuelve | Úsalo cuando | No lo uses cuando | Fuente |
|---|---|---|---|---|
| Puerto de entrada (primary/driving port) | Que cualquier actor (HTTP, evento, prueba, batch) dispare el mismo comportamiento | haya más de un disparador o se quiera probar sin web | capas simples sin reglas; Hombergs admite saltarlo («Skipping Incoming Ports») como atajo consciente | Cockburn; Hombergs caps. 5 y 11; jMolecules `@PrimaryPort` |
| Interactor / Application Service | Orquestar dominio y puertos en una operación con transacción | hay un modelo de dominio con reglas | el módulo es CRUD | Martin; Vernon IDDD cap. 14; Fowler PoEAA *Service Layer* |
| Un servicio por caso de uso | Clases pequeñas con dependencias mínimas | ★ por defecto [opinión, como buckpal y library] | operaciones triviales muy parecidas (dddsample agrupa en `BookingService`) | Hombergs; ddd-by-examples/library |
| Command (objeto comando) | Empaquetar la petición en un valor inmutable que se autovalida | siempre en la entrada de un caso de uso de escritura | — | GoF *Command*; Hombergs; Young, *CQRS Documents*; jMolecules `@Command` |
| Query / modelo de lectura | Que las lecturas no carguen agregados ni abran transacciones de escritura | pantallas y listados | la lectura necesita el agregado entero para decidir | Fowler, *CommandQuerySeparation*; Hombergs («read-only use cases») |
| CQRS (modelos separados) | Simplificar o escalar lecturas con otro modelo | una parte concreta con lecturas muy distintas | casi siempre: «para la mayoría de los sistemas CQRS agrega complejidad riesgosa» | Fowler, *CQRS*; Young |
| Mediator / bus de comandos | Despachar comandos a manejadores sin que el emisor los conozca | muchos disparadores genéricos y middleware transversal | en Spring el contenedor ya inyecta el puerto; un bus oculta la dependencia [opinión] | GoF *Mediator*; jMolecules `@CommandDispatcher` |
| Transaction Script | Un procedimiento por petición, sin modelo | pocas reglas, CRUD | hay invariantes que se repiten | Fowler, PoEAA |
| Domain Model | Reglas dentro de objetos con datos y comportamiento | hay invariantes y estados | no hay reglas (sobrediseño) | Fowler, PoEAA; Evans |
| Unit of Work | Coordinar la escritura de todo lo cambiado y la concurrencia en una transacción | con JPA (el contexto de persistencia ya lo es) | con JDBC explícito, donde `guardar` escribe [opinión] | Fowler, PoEAA |
| Puerto de salida por necesidad (ISP) | Interfaces mínimas que el caso de uso dicta | ★ 1-3 operaciones por puerto | «repositorio dios» compartido por todo | Hombergs; Graça; `investigacion/02` #22 |
| Política o manejador de evento | Un caso de uso que reacciona a un hecho de otro agregado | consistencia eventual entre agregados | la regla es una invariante del mismo acto | Vernon EAD II; library `HandleDuplicateHold`; Spring Modulith |
| Saga (orquestada o coreografiada) | Una transacción de negocio que cruza agregados o servicios, con compensaciones | varios pasos remotos que pueden fallar | todo cabe en un agregado y una transacción | Richardson, *Microservices Patterns* cap. 4; microservices.io *Saga*; FTGO `CreateOrderSaga` |
| Transactional Outbox | Publicar el evento si y solo si hay commit | el caso de uso emite eventos para otros módulos | nadie consume | microservices.io; Spring Modulith *Event Publication Registry* |
| Fake Object (en memoria) | Probar el caso de uso sin infraestructura y verificando estado | ★ cada puerto de salida | — (fijo por el usuario) | Meszaros, *xUnit Test Patterns*; Fowler, *Mocks Aren't Stubs* |
| Prueba de contrato compartida | Que la memoria y la real se comporten igual | ★ siempre que exista un fake | — | Rainsberger; spring-ai `AbstractSessionRepositoryContractTests` (`adaptador-persistencia` §9.7) |
| Decorador transaccional | Sacar `@Transactional` del servicio para dejar la aplicación sin Spring | política de «aplicación sin framework» | Spring ya se acepta en `application` (`investigacion/02` #20) | `investigacion/02` D11; [opinión] |

**Decisiones de patrón que cierra la ronda:**
- un servicio por caso de uso (★) o servicio agrupado: P13;
- comando como `record` autovalidado (★) o con Bean Validation: P14;
- consistencia: todo o nada en un agregado (★), evento + política, o saga: P8 y P19;
- puertos finos por necesidad (★) o repositorio por agregado reutilizado: P16;
- dobles: fake + contrato (★, fijado por el usuario) frente a mocks: P20.

## 4 · Prácticas profesionales verificables

Severidad: C = crítica (3), M = mayor (2), m = menor (1). Verificación: A = ArchUnit o jMolecules,
P = prueba, L = lectura. Alimentan el 25 % de adherencia (`investigacion/02` §3, fórmula y topes).

| id | Práctica | Sev. | Al detallar | Al verificar | Fuente |
|---|---|---|---|---|---|
| U-01 | El controlador (o listener) depende del puerto de entrada, nunca del servicio concreto | M | P13 | A: `JMoleculesArchitectureRules.ensureHexagonal()` o regla propia sobre `..adapter.in..` | `investigacion/02` #4; Cockburn |
| U-02 | `application` no depende de `adapter`, de DTO web ni de entidades JPA | C | P17 | A: `noClasses().that().resideInAPackage("..application..").should().dependOnClassesThat().resideInAPackage("..adapter..")` | `investigacion/02` #2, #7; Martin |
| U-03 | Un comando (o consulta) inmutable por caso de uso, que valida su forma en el constructor | M | P13-P14 | P: la construcción inválida falla; L: `record` | Hombergs; `investigacion/02` #8 |
| U-04 | El resultado es un `record` del caso de uso; no se devuelve el agregado ni una entidad | M | P13 | L: firma del puerto | [decisión del usuario]; Martin (response model); Hombergs |
| U-05 | Las reglas de negocio viven en el dominio; el servicio orquesta (≤ ~20 líneas, sin `if` de negocio largos) | M | P6 | L | Fowler *AnemicDomainModel*; Vernon IDDD cap. 14; `investigacion/02` #11 |
| U-06 | Cada regla tiene un dueño y dos casos (cumple/viola) | C | P6-P7 | P: una prueba por caso | Example Mapping; Evans *Assertions* |
| U-07 | Los rechazos son excepciones con nombre (dominio o aplicación), no genéricas | M | P8, P18 | A: `GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS` | `investigacion/02` #12; Bloch ítem 73 |
| U-08 | El adaptador de entrada traduce los errores a RFC 9457 en un solo `@RestControllerAdvice` | M | P18 (catálogo) | P: `@WebMvcTest` por error | `investigacion/02` #13; RFC 9457 |
| U-09 | Toda E/S (BD, HTTP, colas, reloj, id) pasa por un puerto de salida o un `Clock` inyectado | C | P16 | L; A: `application` no llama `Instant.now()` ni `UUID.randomUUID()` | `investigacion/02` #5, #21 |
| U-10 | Los puertos de salida se nombran por intención y tienen 1-3 operaciones (máx. ~5) | m | P16 | L: contar métodos; buscar `Jpa`, `Rest`, `Kafka` en los nombres | Cockburn; Hombergs; `investigacion/02` #6, #22 |
| U-11 | Por las firmas de los puertos cruzan tipos del dominio, no DTO del proveedor ni primitivos sueltos | C | P17 | L; A: `..port..` no depende de `..adapter..` ni de SDK externos | Graça; `investigacion/04` §3.3 P4 |
| U-12 | «No encontrado» es `Optional` en el puerto; el caso de uso lo convierte en error con nombre | M | P18 | P: caso «no existe» | `investigacion/04` §3.3 P5 |
| U-13 | Los fallos técnicos de un puerto son una excepción propia del puerto; la del proveedor no cruza | M | P18 | L; P: contrato del puerto | `investigacion/04` §3.3 P6 |
| U-14 | `@Transactional` de Spring en la clase concreta del servicio de aplicación; nunca en controlador ni dominio | M | P19 | A: `@Transactional` solo en `..application.service..` (o `MANDATORY` en el adaptador de salida) | Spring Framework, *Using @Transactional*; `investigacion/02` #20 |
| U-15 | Una transacción modifica un solo agregado; los demás reaccionan a eventos | M | P19 | L: el servicio guarda una sola raíz | Vernon EAD II; `investigacion/03` regla 5 |
| U-16 | Sin llamadas remotas lentas dentro de la transacción; los efectos externos van tras el commit | M | P16, P19 | L | `investigacion/04` §3.3 P8; [opinión] |
| U-17 | Los eventos se publican en la misma transacción que guarda (outbox), no con un `send` a mano | M | P9 | P: `Scenario` de Modulith; L | microservices.io *Transactional Outbox*; Spring Modulith |
| U-18 | Idempotencia explícita: el comando repetido no duplica efectos | M | P10 | P: ejecutar dos veces → un efecto | `investigacion/03` regla 14 |
| U-19 | La autorización se comprueba en el caso de uso (o con su anotación), no solo en el transporte | M | P15 | P: caso «sin permiso» | Vernon IDDD cap. 14; [opinión] |
| U-20 | Cada puerto de salida tiene un fake en memoria y una prueba de contrato que heredan fake y real | C | P20 | P: existe `XContratoTest` con dos subclases | [decisión del usuario]; Rainsberger; Meszaros |
| U-21 | El caso de uso se prueba sin Spring ni BD, con fakes y `Clock.fixed`, en < 1 s | C | P20 | P: prueba sin `@SpringBootTest` | `investigacion/02` #15; Cockburn |
| U-22 | Las pruebas del caso de uso verifican estado y resultado, no el orden de llamadas | m | P20 | L: pocas `verify(...)` | Fowler, *Mocks Aren't Stubs*; [opinión] |
| U-23 | Nombres del lenguaje ubicuo, en imperativo para comandos y casos de uso (`ConfirmarPedido`) | M | P1, P13 | L: glosario | `investigacion/03` reglas 1-2; Cockburn (`For…`) |
| U-24 | Las reglas de arquitectura corren en el build (`ensureHexagonal`, onion o Modulith `verify()`) | M | P20 | P: existe la prueba y corre en `mvn verify` | `investigacion/02` #25; jMolecules; Modulith |
| U-25 | Inyección por constructor; el servicio es package-private y solo se expone el puerto | m | P13 | A: `NO_CLASSES_SHOULD_USE_FIELD_INJECTION`; L | `investigacion/02` #14; library `PatronConfiguration` |

## 5 · Árbol de preguntas

Formato (`investigacion/04` §2.2): título de una línea + contexto pequeño o mediano con el **por qué** (SKILL.md, «Cómo preguntas») · opciones con la ★ primero y
un ejemplo cada una · **cierra** (concepto de §1 o práctica de §4). Las 20 son fijas y siguen el orden
del borrador del usuario.

### A · Propósito

**P1. ¿Quién lo ejecuta y qué logra, en una frase del negocio?**
Por qué: nombra el caso de uso y su límite (Cockburn: actor + objetivo de nivel «user goal»).
1. ★ Actor + verbo + objetivo — «el vendedor confirma un pedido para que se reserve el stock» → `ConfirmarPedido`
2. Objetivo de sistema (subfunción) — «validar crédito» → no es caso de uso propio; es un paso o un puerto
3. Objetivo resumen (varias sesiones) — «gestionar el ciclo del pedido» → partir en casos de nivel usuario
Cierra: ⟦1⟧, U-23.

**P2. ¿Qué lo dispara?**
Por qué: decide el adaptador de entrada y si hay usuario autenticado.
1. ★ Petición de usuario (endpoint) — `POST /pedidos/{id}:confirmar` → `ConfirmarPedidoController`
2. Evento de otro módulo — `@ApplicationModuleListener void on(PagoAprobado e) { confirmar.ejecutar(...); }`
3. Temporizador o trabajo — `@Scheduled(cron = "0 0 * * * *") void caducarReservas()`
Cierra: ⟦1⟧. (Poda en §6.)

**P3. ¿Qué fuente lo justifica?**
Por qué: trazabilidad, 15 % del índice (`investigacion/04` §5.4).
1. ★ Tarjeta de event storming o escenario de calidad — «ESC-07: el vendedor confirma…»
2. ADR o requisito existente — «RF-12, ADR-004»
3. Nueva (se registra) — `Q-1: falta fuente; supuesto: pedido del negocio en la reunión del 14-oct`
Cierra: ⟦1⟧.

### B · Comportamiento

**P4. ¿Qué debe ser cierto antes de empezar?**
Por qué: las precondiciones son verificables y no se vuelven a comprobar dentro (Cockburn, WEUC).
1. ★ Lista corta y comprobable — «el solicitante está autenticado; el pedido existe»
2. Ninguna — «cualquiera puede ejecutarlo en cualquier momento»
3. Estado del agregado (va al dominio, no aquí) — «el pedido está en BORRADOR» → regla de `Pedido.confirmar`
Cierra: ⟦4⟧.

**P5. ¿Cuáles son los pasos del camino feliz (3 a 7)?**
Por qué: es la orquestación del servicio (Cockburn: escenario principal de 3 a 9 pasos).
1. ★ 3-7 pasos con verbo — «1 cargar pedido · 2 consultar crédito · 3 confirmar · 4 guardar · 5 publicar»
2. Un solo paso — «guardar el pedido» → probablemente CRUD; revisa P6
3. Más de 9 pasos — partir en dos casos de uso o usar evento + política
Cierra: ⟦4⟧, U-05.

**P6. ¿Qué reglas aplica y dónde vive cada una?**
Por qué: evita el modelo anémico (Fowler) y el caso de uso «gordo» (`investigacion/02` §6).
1. ★ Invariantes en el agregado; el caso de uso solo reúne datos externos — `pedido.confirmar(ahora)` lanza `PedidoVacio`
2. Regla con dato externo: el caso de uso lo consulta y el dominio decide — `pedido.confirmar(ahora, credito.disponible(cliente))`
3. En el caso de uso (Transaction Script, solo sin modelo) — `if (disponible.esMenorQue(total)) throw new CreditoInsuficiente(...)`
Cierra: ⟦3⟧, U-05.

**P7. Por cada regla, ¿un ejemplo que la cumple y otro que la viola?**
Por qué: cada par es una prueba del caso de uso (Example Mapping).
1. ★ Par con datos concretos — «crédito 500 000, total 480 000: confirma · crédito 400 000: `CreditoInsuficiente`»
2. Solo el camino feliz — se acepta con un pendiente `Q-n` por regla
3. Tabla (`Scenario Outline`) si solo cambian los datos — `| credito | total | resultado |`
Cierra: ⟦3⟧, U-06.

**P8. Si falla a mitad, ¿qué queda?**
Por qué: decide la consistencia y la frontera transaccional (Cockburn: «garantías mínimas»).
1. ★ Todo o nada en una transacción — «si `guardar` falla, no hay evento ni cambio»
2. Compensación (saga) — `withCompensation(kitchenService.cancel, ...)` (FTGO `CreateOrderSaga`)
3. Parcial visible con estado intermedio — `Pedido` queda `PENDIENTE_DE_CREDITO` y una política lo resuelve
Cierra: ⟦5⟧, U-15.

**P9. ¿Qué queda distinto al terminar y qué evento emite?**
Por qué: postcondiciones verificables y contrato para otros módulos (Cockburn: «garantías de éxito»).
1. ★ Estado + evento en la misma transacción — «Pedido CONFIRMADO; `PedidoConfirmado` registrado en el outbox»
2. Solo estado, sin evento — «nadie fuera necesita saberlo» (se justifica)
3. Efecto externo directo (se rechaza) — `correo.enviar(...)` dentro de la transacción
Cierra: ⟦7⟧, U-17. (El evento por dentro va a `evento-mensaje`.)

**P10. ¿Se puede ejecutar dos veces con el mismo comando?**
Por qué: los reintentos de red y la entrega «al menos una vez» repiten comandos.
1. ★ Idempotente por el estado del agregado — `case Confirmado c -> { }` en `Pedido.confirmar`; el caso de uso devuelve el mismo resultado
2. Idempotente por clave — `ComandosProcesados.registrarSiEsNuevo(c.claveIdempotencia())` (puerto)
3. La segunda falla con error con nombre — `throw new PedidoYaConfirmado(id)` → 409
Cierra: ⟦8⟧, U-18.

### C · Variables

**P11. ¿Qué parámetros lo gobiernan? ¿Fijos, configurables o pendientes?**
Por qué: dónde vive el valor y quién lo decide.
1. ★ Configurable con defecto y rango — `@ConfigurationProperties("pedidos.confirmacion") record Props(Duration esperaCredito)` (2 s; 0,5..5 s)
2. Fijo en el dominio — `static final int MAX_LINEAS = 50;` (va a `entidad-agregado`)
3. Pendiente — `Q-3: ¿límite de crédito por cliente o global? supuesto: por cliente`
Cierra: ⟦11⟧.

**P12. ¿Cómo sabrás que funciona en producción?**
Por qué: todo requisito necesita un criterio medible (Volere).
1. ★ Métrica de negocio + latencia — «0 pedidos confirmados sin crédito por semana; p95 < 300 ms»
2. Solo técnica — «tasa de errores 5xx < 0,1 %»
3. Sin criterio — se registra pendiente
Cierra: ⟦11⟧.

### D · Puerto de entrada

**P13. ¿Nombre y firma del puerto de entrada: comando → resultado?**
Por qué: es el contrato interno que invoca cualquier adaptador; el defecto es un `record` de resultado.
1. ★ Interfaz de un método + comando + `record` — `ResultadoConfirmacion ejecutar(ConfirmarPedidoComando c)`
2. Sin retorno (comando puro, CQS) — `void ejecutar(ConfirmarPedidoComando c)`; el cliente consulta después
3. Servicio agrupado con varios métodos — `BookingService { bookNewCargo; assignCargoToRoute; … }` (dddsample)
Cierra: ⟦2⟧, U-03, U-04, U-23, U-25.

**P14. ¿Dónde se valida cada cosa?**
Por qué: separa la forma de la regla de negocio (Hombergs: validación de entrada frente a reglas de negocio).
1. ★ Forma en el constructor compacto del comando; reglas en el dominio — `Objects.requireNonNull(pedidoId)` en `ConfirmarPedidoComando`
2. Bean Validation en el comando — `record SendMoneyCommand(@NotNull AccountId ..., @PositiveMoney Money ...)` + `validate(this)` (buckpal)
3. Solo en el DTO web con `@Valid` (insuficiente) — un evento o un trabajo entran sin validar
Cierra: ⟦2⟧, U-03.

**P15. ¿Dónde se comprueba la autorización?**
Por qué: la seguridad pertenece al caso de uso, igual para todos los disparadores (Vernon, IDDD cap. 14).
1. ★ En el caso de uso, con la identidad en el comando — `if (!dueno && !s.tiene(SUPERVISOR)) throw new AccesoDenegado(...)`
2. Anotación en el servicio — `@PreAuthorize("hasRole('VENDEDOR')")` (exige Spring Security en `application`)
3. Solo en el adaptador web — reglas por ruta en `SecurityFilterChain`; un listener queda sin proteger
Cierra: ⟦9⟧, U-19.

### E · Puertos de salida

**P16. ¿Qué necesita del exterior? Una frase por puerto, de 1 a 3 operaciones.**
Por qué: interfaces mínimas, dictadas por el caso de uso (Hombergs; Graça).
1. ★ Un puerto por capacidad, con nombre de intención — `CreditoDeClientes { Optional<Dinero> disponible(ClienteId) }`
2. Repositorio del agregado reutilizado — `Pedidos { porId; guardar }` (lo implementa `adaptador-persistencia`)
3. Puertos finos por operación — `LoadAccountPort`, `UpdateAccountStatePort` (buckpal)
Cierra: ⟦6⟧, U-09, U-10, U-16.

**P17. ¿Qué tipos cruzan la frontera de cada puerto?**
Por qué: el núcleo no debe conocer la infraestructura (Martin: cruzan estructuras simples, nunca filas de BD).
1. ★ Tipos del dominio — `Optional<Dinero> disponible(ClienteId cliente)`
2. Primitivos — `BigDecimal disponible(UUID cliente)` (se pierden la unidad y el tipo)
3. DTO del proveedor (se rechaza) — `CreditApiResponse consultar(String customerId)`
Cierra: ⟦6⟧, U-02, U-11.

**P18. ¿Cómo comunica «no encontrado» y los fallos técnicos?**
Por qué: errores explícitos que el caso de uso convierte en el catálogo que verá el adaptador.
1. ★ `Optional` + excepción propia del puerto — `Optional<Pedido> porId(...)`; `CreditoNoDisponible extends RuntimeException`
2. Excepción de aplicación lanzada por el puerto — `Pedido porId(...)` que lanza `PedidoNoExiste`
3. `null` o la excepción del proveedor (se rechaza) — `EntityNotFoundException` de JPA sube al núcleo (buckpal)
Cierra: ⟦5⟧, U-07, U-12, U-13.

**P19. ¿Dónde se abre la transacción y cuántos agregados modifica?**
Por qué: la frontera transaccional coincide con la del agregado (Vernon EAD II; Fowler *DDD_Aggregate*).
1. ★ `@Transactional` en el servicio de aplicación; un agregado — `@Transactional class ConfirmarPedidoServicio`
2. Varios agregados en la misma transacción, con ADR — razones de Vernon: UI, falta de mecanismo, transacción global impuesta, rendimiento
3. Sin transacción en el caso de uso (la abre el adaptador) — dos puertos dejan de ser atómicos
Cierra: ⟦5⟧, U-14, U-15, U-16.

**P20. ¿Qué implementaciones y dobles de prueba tendrá cada puerto?**
Por qué: el núcleo se prueba sin infraestructura y la memoria debe portarse igual que la real.
1. ★ Real + en memoria + prueba de contrato abstracta — `CreditoDeClientesContratoTest` heredada por `…EnMemoria…` y `…HttpContratoIT`
2. Real + mocks de Mockito por prueba — `given(loadAccountPort.loadAccount(...)).willReturn(account)` (buckpal; frágil ante refactor)
3. Solo la real con Testcontainers — el caso de uso se prueba lento y con BD
Cierra: ⟦10⟧, U-20, U-21, U-22, U-24.

## 6 · Poda y ramas nuevas

- **Caso de uso de solo lectura (consulta):** se saltan P8, P10 y P19. P13 pasa a «consulta → vista»
  (`record` de lectura). Se recomienda proyectar directo desde la BD con un puerto de lectura, sin cargar
  el agregado (`investigacion/02` §6; Hombergs, «read-only use cases»). Cupos: orden, paginación,
  filtros y límite.
- **Disparado por un evento (P2 = evento):** la P15 cambia a «¿con qué identidad corre?» (identidad
  técnica del módulo). La P10 se responde con la deduplicación de `evento-mensaje` (T2/T3) y se confirma.
  La transacción la abre el listener (`@ApplicationModuleListener` = `@Async` + `REQUIRES_NEW` +
  `@TransactionalEventListener`, Spring Modulith *Events*). Cupo: «¿qué hace si el agregado ya cambió?».
- **Disparado por un temporizador:** la P15 pasa a identidad técnica; cupo para «¿cuántos agregados por
  ejecución, y uno por transacción?» (library `ExpiringHolds`).
- **Sin dependencias externas (solo su repositorio):** P16, P17 y P18 se vuelven una confirmación de una
  línea («usa `Pedidos{porId, guardar}`, ver `adaptador-persistencia`»). Los dos cupos van a contraejemplos
  de las reglas de P7.
- **P6 = sin reglas, solo guardar y leer:** proponer capas simples o Transaction Script; marcar U-05, U-06
  y U-15 N/A con motivo (`investigacion/02` §6). Se saltan P7 y P8.
- **P8 = compensación (saga):** el cupo va a «pasos, transacción pivote y compensación de cada paso»
  (Richardson, cap. 4: transacciones compensables, pivote y reintentables). Los mensajes van a
  `evento-mensaje` (T5).
- **P19 = varios agregados:** el cupo va al ADR con la razón de Vernon que lo justifica y a «¿qué agregado
  reacciona después por evento?».
- **P13 = sin retorno:** el cupo va a «¿cómo sabe el cliente el resultado?» (Location + consulta).
- **Lo que la documentación o el código ya responde:** confirmación de una línea; libera cupo
  (`investigacion/04` regla 7).
- **Cupos libres, en este orden:** contraejemplos de reglas («¿y si el crédito es exactamente igual al
  total?»), fallos técnicos de cada puerto («¿y si el servicio de crédito no responde en 2 s?»),
  concurrencia («¿y si otro vendedor lo cancela a la vez?» → `ConflictoDeVersion`) y la metapregunta.

## 7 · Cobertura

Pesos en el orden de §1 (`w_k = 11 − k + 1`: ⟦1⟧ pesa 11 y ⟦11⟧ pesa 1). Cada casilla cuenta cuando tiene
respuesta o queda `[PENDIENTE]` con dueño (`investigacion/04` §5.1).

- [ ] ⟦1⟧ Actor, objetivo, disparador y fuente
- [ ] ⟦2⟧ Firma del puerto de entrada (comando, resultado `record`) y dónde se valida
- [ ] ⟦3⟧ Cada regla con su dueño (dominio o caso de uso) y su par de ejemplos
- [ ] ⟦4⟧ Precondiciones y flujo principal
- [ ] ⟦5⟧ Fallo a mitad, catálogo de errores y transacción (uno o varios agregados)
- [ ] ⟦6⟧ Puertos de salida: operaciones, tipos y errores
- [ ] ⟦7⟧ Postcondiciones y eventos
- [ ] ⟦8⟧ Idempotencia
- [ ] ⟦9⟧ Autorización (o identidad técnica)
- [ ] ⟦10⟧ Dobles de prueba y prueba de contrato por puerto de salida
- [ ] ⟦11⟧ Variables con su tipo (fija, configurable o pendiente) y criterio medible

## 8 · Antipatrones

| Antipatrón | Señal detectable | Fuente |
|---|---|---|
| Caso de uso «gordo» (Transaction Script disfrazado) | el servicio calcula totales, compara estados y llama setters; método de > 40 líneas | `investigacion/02` §6; Fowler *AnemicDomainModel* |
| Resultado `boolean` | `boolean sendMoney(...)` devuelve `false` y el motivo se pierde | [opinión] sobre buckpal `SendMoneyUseCase` |
| Devolver el agregado | `Order createOrder(...)` sube la entidad JPA al controlador | FTGO `OrderService`; Hombergs |
| DTO web como comando | `ejecutar(CrearPedidoRequest r)` con anotaciones Jackson | `investigacion/02` §6 |
| Validación solo en el controlador | el listener o el trabajo entran con datos sin validar | Hombergs |
| Puerto con nombre de tecnología o del proveedor | `PedidoJpaPort`, `CreditApiClient` con tipos del SDK | Cockburn; Graça |
| Excepción del proveedor en el núcleo | `EntityNotFoundException`, `HttpClientErrorException` en `application` | buckpal `AccountPersistenceAdapter`; U-13 |
| `IllegalArgumentException` como «no encontrado» | `getOrElseThrow(() -> new IllegalArgumentException("Cannot find ..."))` | library `PlacingOnHold`; U-07 |
| Errores tragados en un `Try` | `Try.of(...).onFailure(log::error)`; el llamante recibe un valor genérico | [opinión] sobre library |
| Reloj oculto | `LocalDateTime.now()` en el servicio; pruebas no deterministas | buckpal `SendMoneyService`; `investigacion/02` #21 |
| `@Transactional` en el controlador o en la interfaz del puerto | anotación sobre un `@RestController` o en `interface ConfirmarPedido` | Spring Framework docs; U-14 |
| Autoinvocación transaccional | `this.otroMetodoTransaccional()` dentro del mismo servicio | Spring Framework docs (modo proxy) |
| Excepción comprobada sin rollback | `throws XException` comprobada sin `rollbackFor` (dddsample lo pone a mano) | Spring Framework docs |
| Dos agregados en una transacción sin ADR | guardar `Pedido` e `Inventario` en el mismo método | Vernon EAD II |
| HTTP lento dentro de la transacción | `restClient.post()` entre `porId` y `guardar` | U-16; [opinión] |
| Publicar a mano sin outbox | `kafka.send(...)`, o `@TransactionalEventListener @Async` sin registro de publicaciones: si el proceso cae, el evento se pierde | Spring Modulith *Events*; ttulka `OrderPlacedListener` |
| La aplicación importa de la capa de interfaces | `ApplicationEvents` importa `interfaces.handling.HandlingEventRegistrationAttempt` | dddsample; U-02 |
| Mocks que fijan el orden de llamadas | `then(accountLock).should().lockAccount(...)` en cada prueba; un refactor rompe pruebas sin romper el comportamiento | Fowler *Mocks Aren't Stubs*; buckpal `SendMoneyServiceTest` |
| Fake sin contrato | `InMemoryBookRepo` acepta lo que la BD rechazaría (unicidad, versión) | Rainsberger; U-20 |
| Mediator innecesario | un `CommandBus` genérico con un solo manejador por comando; la dependencia real queda oculta | [opinión] |

## 9 · Código de referencia real

Todos los archivos se clonaron y leyeron el **15-oct-2026** en el commit enlazado. Los fragmentos son
literales (indentación normalizada a espacios) y `// …` marca lo omitido. Las licencias se leyeron del
archivo `LICENSE` de cada repositorio, salvo que se indique otra cosa.

### 9.1 · Puerto de entrada, comando autovalidado y servicio por caso de uso (buckpal)

- **Muestra:** el puerto de entrada de un método, el comando `record` que se valida al construirse, los
  puertos de salida finos y el servicio que orquesta con `@Transactional`.
- **Caso de uso real:** *Send Money*: un titular transfiere dinero de su cuenta a otra. Resuelve que la
  transferencia no supere un umbral configurado y que retiro y depósito queden registrados juntos.
- **Repositorio:** `thombergs/buckpal` (acompaña a Hombergs, *Get Your Hands Dirty on Clean Architecture*,
  2.ª ed.). **Sin licencia declarada** (la API de GitHub devuelve `license: null`): úsalo para leer; no
  copies el código literal.
- **Archivos:**
  [`SendMoneyUseCase.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/main/java/io/reflectoring/buckpal/application/port/in/SendMoneyUseCase.java) ·
  [`SendMoneyCommand.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/main/java/io/reflectoring/buckpal/application/port/in/SendMoneyCommand.java#L9-L25) ·
  [`LoadAccountPort.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/main/java/io/reflectoring/buckpal/application/port/out/LoadAccountPort.java) ·
  [`SendMoneyService.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/main/java/io/reflectoring/buckpal/application/domain/service/SendMoneyService.java#L16-L71)

```java
public interface SendMoneyUseCase {
    boolean sendMoney(SendMoneyCommand command);
}
// … SendMoneyCommand.java
public record SendMoneyCommand(
        @NotNull AccountId sourceAccountId,
        @NotNull AccountId targetAccountId,
        @NotNull @PositiveMoney Money money
) {
    public SendMoneyCommand(
            AccountId sourceAccountId,
            AccountId targetAccountId,
            Money money) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.money = money;
        validate(this);
    }
}
// … LoadAccountPort.java
public interface LoadAccountPort {
    Account loadAccount(AccountId accountId, LocalDateTime baselineDate);
}
// … SendMoneyService.java
@RequiredArgsConstructor
@UseCase
@Transactional
public class SendMoneyService implements SendMoneyUseCase {

    private final LoadAccountPort loadAccountPort;
    private final AccountLock accountLock;
    private final UpdateAccountStatePort updateAccountStatePort;
    private final MoneyTransferProperties moneyTransferProperties;

    @Override
    public boolean sendMoney(SendMoneyCommand command) {
        checkThreshold(command);
        LocalDateTime baselineDate = LocalDateTime.now().minusDays(10);
        Account sourceAccount = loadAccountPort.loadAccount(
                command.sourceAccountId(),
                baselineDate);
        // …
        accountLock.lockAccount(sourceAccountId);
        if (!sourceAccount.withdraw(command.money(), targetAccountId)) {
            accountLock.releaseAccount(sourceAccountId);
            return false;
        }
        // …
        updateAccountStatePort.updateActivities(sourceAccount);
        updateAccountStatePort.updateActivities(targetAccount);
        // …
        return true;
    }
```

- **Qué copiar:** el puerto de un método con el nombre del caso de uso; el comando como `record` que se
  valida en su constructor (P14); los puertos de salida de una operación, dictados por el caso de uso
  (P16-3); el umbral leído de propiedades tipadas (P11); la decisión `withdraw` delegada en el agregado;
  el estereotipo propio `@UseCase` (meta-anotado con `@Component`).
- **Qué no copiar:** el resultado `boolean` (pierde el motivo; el defecto aquí es `record` + excepción);
  `LocalDateTime.now()` en el servicio (U-09); **dos agregados modificados en una transacción** sin ADR
  (U-15); `jakarta.transaction.Transactional` en vez de la de Spring; `IllegalStateException` para ids
  vacíos; un servicio público cuando el puerto ya es la API (hazlo package-private, U-25).

### 9.2 · Prueba del servicio con mocks y regla hexagonal en el build (buckpal)

- **Muestra:** la prueba unitaria del caso de uso sin Spring, con verificación de comportamiento
  (Mockito), y la prueba ArchUnit que fija las capas del hexágono.
- **Caso de uso real:** el mismo *Send Money*: comprueba que, si el retiro falla, solo se bloquea y se
  libera la cuenta de origen.
- **Repositorio:** `thombergs/buckpal` · sin licencia declarada (solo lectura).
- **Archivos:**
  [`SendMoneyServiceTest.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/test/java/io/reflectoring/buckpal/application/domain/service/SendMoneyServiceTest.java#L22-L60) ·
  [`DependencyRuleTests.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/test/java/io/reflectoring/buckpal/DependencyRuleTests.java#L11-L30)

```java
class SendMoneyServiceTest {

    private final LoadAccountPort loadAccountPort =
            Mockito.mock(LoadAccountPort.class);
    // …
    private final SendMoneyService sendMoneyService =
            new SendMoneyService(loadAccountPort, accountLock, updateAccountStatePort, moneyTransferProperties());

    @Test
    void givenWithdrawalFails_thenOnlySourceAccountIsLockedAndReleased() {
        // …
        givenWithdrawalWillFail(sourceAccount);
        givenDepositWillSucceed(targetAccount);
        // …
        boolean success = sendMoneyService.sendMoney(command);

        assertThat(success).isFalse();

        then(accountLock).should().lockAccount(eq(sourceAccountId));
        then(accountLock).should().releaseAccount(eq(sourceAccountId));
        then(accountLock).should(times(0)).lockAccount(eq(targetAccountId));
    }
// … DependencyRuleTests.java
    @Test
    void validateRegistrationContextArchitecture() {
        HexagonalArchitecture.basePackage("io.reflectoring.buckpal")
                .withDomainLayer("application.domain")
                .withAdaptersLayer("adapter")
                .incoming("in.web")
                .outgoing("out.persistence")
                .and()
                .withApplicationLayer("application")
                .incomingPorts("port.in")
                .outgoingPorts("port.out")
                .and()
                .withConfiguration("configuration")
                .check(new ClassFileImporter()
                        .importPackages("io.reflectoring.buckpal.."));
    }
```

- **Qué copiar:** el servicio se construye con `new` y sus puertos, sin contexto de Spring (U-21); la
  regla de arquitectura corre en el build (U-24).
- **Qué no copiar:** mockear el **agregado** (`Mockito.mock(Account.class)`): la prueba deja de ejercitar
  las reglas reales; verificar el orden de llamadas (U-22). Aquí el defecto es fake + verificación de
  estado (T10). `HexagonalArchitecture` es un DSL propio del repositorio; con jMolecules usa
  `ensureHexagonal()` (§9.10).

### 9.3 · Servicio que deja decidir al agregado y doble como lambda (ddd-by-examples/library)

- **Muestra:** un caso de uso por clase, sin anotaciones de Spring (se cablea en una `@Configuration`),
  que carga dos cosas por puertos, deja que el agregado decida y publica los eventos de éxito o de rechazo.
  En la prueba, el puerto funcional se sustituye por una lambda (un *stub* trivial).
- **Caso de uso real:** *Placing on hold*: un usuario de la biblioteca reserva un libro disponible en una
  sucursal, por N días o sin límite. Resuelve las políticas (máximo de reservas, libros restringidos solo
  para investigadores) sin que el servicio las conozca.
- **Repositorio:** `ddd-by-examples/library` · licencia MIT.
- **Archivos:**
  [`PlacingOnHold.java`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/main/java/io/pillopl/library/lending/patron/application/hold/PlacingOnHold.java#L21-L60) ·
  [`FindAvailableBook.java`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/main/java/io/pillopl/library/lending/patron/application/hold/FindAvailableBook.java) ·
  [`PlacingBookOnHoldTest.groovy`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/test/groovy/io/pillopl/library/lending/patron/application/hold/PlacingBookOnHoldTest.groovy#L20-L63)

```java
@AllArgsConstructor
@Slf4j
public class PlacingOnHold {

    private final FindAvailableBook findAvailableBook;
    private final Patrons patronRepository;

    public Try<Result> placeOnHold(@NonNull PlaceOnHoldCommand command) {
        return Try.of(() -> {
            AvailableBook availableBook = find(command.getBookId());
            Patron patron = find(command.getPatronId());
            Either<BookHoldFailed, BookPlacedOnHoldEvents> result = patron.placeOnHold(availableBook, command.getHoldDuration());
            return Match(result).of(
                    Case($Left($()), this::publishEvents),
                    Case($Right($()), this::publishEvents)
            );
        }).onFailure(t -> log.error("Failed to place a hold", t));
    }
    // …
    private AvailableBook find(BookId id) {
        return findAvailableBook
                .findAvailableBookBy(id)
                .getOrElseThrow(() -> new IllegalArgumentException("Cannot find available book with Id: " + id.getBookId()));
    }
// … FindAvailableBook.java
@FunctionalInterface
public interface FindAvailableBook {

    Option<AvailableBook> findAvailableBookBy(BookId bookId);
}
```

```groovy
class PlacingBookOnHoldTest extends Specification {

    FindAvailableBook willFindBook = { id -> Option.of(circulatingBook()) }
    FindAvailableBook willNotFindBook = { id -> Option.none() }
    Patrons repository = Stub()
    // …
    def 'should reject placing on hold book if one of the domain rules is broken (but should not fail!)'() {
        given:
            PlacingOnHold holding = new PlacingOnHold(willFindBook, repository)
        and:
            PatronId patron = persistedRegularPatronWithManyHolds()
        when:
            Try<Result> result = holding.placeOnHold(for3days(patron))
        then:
            result.isSuccess()
            result.get() == Result.Rejection
    }
```

- **Qué copiar:** el servicio solo orquesta: cargar, `patron.placeOnHold(...)`, publicar; un puerto de
  lectura funcional con nombre de intención (`FindAvailableBook`), separado del repositorio del agregado
  (P16-1); el cableado explícito en `PatronConfiguration`, que deja el servicio sin Spring; las pruebas
  que distinguen «rechazo de negocio» de «fallo».
- **Qué no copiar:** `IllegalArgumentException` para «no encontrado» (U-07, U-12); envolver todo en `Try`
  y devolver un `enum Result` genérico (se pierde el tipo del error que el adaptador traduciría a RFC
  9457); Vavr y Lombok en firmas públicas; `Instant.now()` en las fábricas del comando
  (`PlaceOnHoldCommand.closeEnded`). El servicio no lleva `@Transactional`: la consistencia queda en el
  repositorio, que guarda y publica dentro de `publish(...)`.

### 9.4 · Política: caso de uso disparado por un evento, con `Clock` inyectado (ddd-by-examples/library)

- **Muestra:** un manejador de evento que traduce el hecho a un comando y llama a otro caso de uso, con el
  reloj inyectado por constructor (P2-2; consistencia eventual entre agregados).
- **Caso de uso real:** *Handle duplicate hold*: si dos usuarios reservan el mismo ejemplar a la vez, el
  agregado `Book` emite `BookDuplicateHoldFound` y esta política cancela la reserva del segundo.
- **Repositorio:** `ddd-by-examples/library` · MIT.
- **Archivo:** [`HandleDuplicateHold.java`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/main/java/io/pillopl/library/lending/patron/application/hold/HandleDuplicateHold.java#L14-L40)

```java
public class HandleDuplicateHold {

    private final CancelingHold cancelingHold;
    private final Clock clock;

    public HandleDuplicateHold(CancelingHold cancelingHold) {
        this(cancelingHold, Clock.systemUTC());
    }

    public HandleDuplicateHold(CancelingHold cancelingHold, Clock clock) {
        this.cancelingHold = cancelingHold;
        this.clock = clock;
    }

    @EventListener
    public Try<Result> handle(BookDuplicateHoldFound event) {
        return cancelingHold.cancelHold(cancelHoldCommandFrom(event));
    }

    private CancelHoldCommand cancelHoldCommandFrom(BookDuplicateHoldFound event) {
        return new CancelHoldCommand(
                now(clock),
                new PatronId(event.getSecondPatronId()),
                new BookId(event.getBookId()));
    }
}
```

- **Qué copiar:** la política no decide: construye el comando y delega en el caso de uso `CancelingHold`;
  el `Clock` inyectable (U-09); la compensación como un caso de uso normal.
- **Qué no copiar:** `@EventListener` síncrono: corre dentro de la transacción del emisor y une los dos
  agregados. En Spring Modulith usa `@ApplicationModuleListener` (transacción propia, tras el commit) y
  deduplica (`evento-mensaje` T2).

### 9.5 · Servicio de aplicación agrupado, una transacción por operación (citerus/dddsample-core)

- **Muestra:** el servicio de aplicación «clásico» del libro de Evans: una interfaz con varias
  operaciones, cada una `@Transactional`, que carga un agregado, invoca un método del dominio y lo guarda.
- **Caso de uso real:** *Booking*: el cliente reserva el transporte de una carga entre dos puertos con
  fecha límite, pide rutas posibles, asigna una ruta y cambia el destino. La carga recalcula su estado de
  entrega sin que el servicio lo calcule.
- **Repositorio:** `citerus/dddsample-core` · licencia MIT.
- **Archivos:**
  [`BookingService.java`](https://github.com/citerus/dddsample-core/blob/d8b896254ed4e18ba8117dcd5bf9a54708bdeaaf/src/main/java/se/citerus/dddsample/application/BookingService.java) ·
  [`BookingServiceImpl.java`](https://github.com/citerus/dddsample-core/blob/d8b896254ed4e18ba8117dcd5bf9a54708bdeaaf/src/main/java/se/citerus/dddsample/application/impl/BookingServiceImpl.java#L18-L90)

```java
public class BookingServiceImpl implements BookingService {

  private final CargoRepository cargoRepository;
  private final LocationRepository locationRepository;
  private final RoutingService routingService;
  private final CargoFactory cargoFactory;
  // …
  @Override
  @Transactional
  public TrackingId bookNewCargo(final UnLocode originUnLocode,
                                 final UnLocode destinationUnLocode,
                                 final Instant arrivalDeadline) {
    Cargo cargo = cargoFactory.createCargo(originUnLocode, destinationUnLocode, arrivalDeadline);

    cargoRepository.store(cargo);
    logger.info("Booked new cargo with tracking id {}", cargo.trackingId().idString());

    return cargo.trackingId();
  }
  // …
  @Override
  @Transactional
  public void assignCargoToRoute(final Itinerary itinerary, final TrackingId trackingId) {
    final Cargo cargo = cargoRepository.find(trackingId);
    if (cargo == null) {
      throw new IllegalArgumentException("Can't assign itinerary to non-existing cargo " + trackingId);
    }

    cargo.assignToRoute(itinerary);
    cargoRepository.store(cargo);

    logger.info("Assigned cargo {} to new route", trackingId);
  }
```

- **Qué copiar:** el patrón cargar → método de negocio → guardar, en una transacción y un agregado
  (U-15); la fábrica de dominio para crear (`CargoFactory`); devolver la identidad (`TrackingId`), no el
  agregado; `@Transactional` de Spring en la clase concreta (U-14).
- **Qué no copiar:** `null` como «no encontrado» y `IllegalArgumentException` (U-12); la consulta
  `requestPossibleRoutesForCargo` dentro de una transacción de escritura y con un servicio remoto
  (`RoutingService`) en medio (U-16); parámetros sueltos en vez de un comando (U-03).

### 9.6 · Casos de uso reales probados con repositorios en memoria (citerus/dddsample-core)

- **Muestra:** una prueba de escenario que usa los **servicios de aplicación reales** cableados con
  repositorios en memoria y un stub síncrono de eventos; y un servicio que guarda un agregado y deja el
  otro para la vía asíncrona.
- **Caso de uso real:** *Handling*: se registra una manipulación (carga, descarga, aduana) de una carga en
  un puerto; la carga se reinspecciona después, de forma asíncrona.
- **Repositorio:** `citerus/dddsample-core` · MIT.
- **Archivos:**
  [`CargoLifecycleScenarioTest.java`](https://github.com/citerus/dddsample-core/blob/d8b896254ed4e18ba8117dcd5bf9a54708bdeaaf/src/test/java/se/citerus/dddsample/scenario/CargoLifecycleScenarioTest.java#L302-L345) ·
  [`CargoRepositoryInMem.java`](https://github.com/citerus/dddsample-core/blob/d8b896254ed4e18ba8117dcd5bf9a54708bdeaaf/src/test/java/se/citerus/dddsample/infrastructure/persistence/inmemory/CargoRepositoryInMem.java#L24-L49) ·
  [`HandlingEventServiceImpl.java`](https://github.com/citerus/dddsample-core/blob/d8b896254ed4e18ba8117dcd5bf9a54708bdeaaf/src/main/java/se/citerus/dddsample/application/impl/HandlingEventServiceImpl.java#L19-L58)

```java
    applicationEvents = new SynchronousApplicationEventsStub();

    // In-memory implementations of the repositories
    handlingEventRepository = new HandlingEventRepositoryInMem();
    cargoRepository = new CargoRepositoryInMem();
    locationRepository = new LocationRepositoryInMem();
    voyageRepository = new VoyageRepositoryInMem();

    // Actual factories and application services, wired with stubbed or in-memory infrastructure
    handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);

    cargoInspectionService = new CargoInspectionServiceImpl(applicationEvents, cargoRepository, handlingEventRepository);
    handlingEventService = new HandlingEventServiceImpl(handlingEventRepository, applicationEvents, handlingEventFactory);
    CargoFactory cargoFactory = new CargoFactory(locationRepository, cargoRepository);
    bookingService = new BookingServiceImpl(cargoRepository, locationRepository, routingService, cargoFactory);
// … CargoRepositoryInMem.java
public class CargoRepositoryInMem implements CargoRepository {

    private final Map<String, Cargo> cargoDb;
    // …
    public Cargo find(final TrackingId trackingId) {
        return cargoDb.get(trackingId.idString());
    }

    public void store(final Cargo cargo) {
        cargoDb.put(cargo.trackingId().idString(), cargo);
    }
// … HandlingEventServiceImpl.java
  @Override
  @Transactional(rollbackFor = CannotCreateHandlingEventException.class)
  public void registerHandlingEvent(final Instant completionTime,
                                    // …
                                    final HandlingEvent.Type type) throws CannotCreateHandlingEventException {
    final Instant registrationTime = Instant.now();
    // …
    /* Store the new handling event, which updates the persistent
       state of the handling event aggregate (but not the cargo aggregate -
       that happens asynchronously!)
     */
    handlingEventRepository.store(event);

    /* Publish an event stating that a cargo has been handled. */
    applicationEvents.cargoWasHandled(event);
```

- **Qué copiar:** probar los casos de uso reales con fakes en memoria de cada puerto (U-21), que es la
  idea de Cockburn de los «adaptadores de prueba»; el comentario que justifica un agregado por
  transacción; `rollbackFor` cuando la excepción es comprobada (Spring no hace rollback por defecto).
- **Qué no copiar:** los fakes no tienen prueba de contrato (U-20): `store` no comprueba versión ni
  unicidad; el stub de eventos escribe en `System.out`; `ApplicationEvents` importa un tipo de la capa
  `interfaces` (U-02); `Instant.now()` en el servicio (U-09).

### 9.7 · Caso de uso con nombre de intención y reacciones tras el commit (ttulka/ddd-example-ecommerce)

- **Muestra:** el puerto de entrada como interfaz con el nombre del caso de uso (`PlaceOrder`), un caso de
  uso de orquestación que lo llama y lanza una excepción de negocio con nombre, y un módulo que reacciona
  al evento tras el commit.
- **Caso de uso real:** *Place order from cart*: el cliente convierte su carrito en un pedido. El carrito
  vacío se rechaza; con el pedido colocado, *billing* cobra y *warehouse* aparta la mercancía.
- **Repositorio:** `ttulka/ddd-example-ecommerce` · licencia MIT.
- **Archivos:**
  [`PlaceOrder.java`](https://github.com/ttulka/ddd-example-ecommerce/blob/b48268eacfb2940d4d1237cfae09acea3bcc779e/src/main/java/com/ttulka/ecommerce/sales/order/PlaceOrder.java) ·
  [`PlaceOrderFromCart.java`](https://github.com/ttulka/ddd-example-ecommerce/blob/b48268eacfb2940d4d1237cfae09acea3bcc779e/src/main/java/com/ttulka/ecommerce/portal/PlaceOrderFromCart.java#L23-L57) ·
  [`billing/.../OrderPlacedListener.java`](https://github.com/ttulka/ddd-example-ecommerce/blob/b48268eacfb2940d4d1237cfae09acea3bcc779e/src/main/java/com/ttulka/ecommerce/billing/payment/listeners/OrderPlacedListener.java#L18-L31)

```java
/**
 * Place Order use-case.
 */
public interface PlaceOrder {
    // …
    void place(OrderId orderId, Collection<OrderItem> items, Money total);
}
// … PlaceOrderFromCart.java
@RequiredArgsConstructor
public class PlaceOrderFromCart {

    private final @NonNull PlaceOrder placeOrder;
    // …
    public void placeOrder(@NonNull UUID orderId, @NonNull Cart cart) {
        if (!cart.hasItems()) {
            throw new PlaceOrderFromCart.NoItemsToOrderException();
        }
        // here a command message PlaceOrder could be sent for lower coupling
        placeOrder.place(new OrderId(orderId),
                         cart.items().stream()
                                 .map(this::toOrderItem)
                                 .collect(Collectors.toList()),
                         // …
// … billing/payment/listeners/OrderPlacedListener.java
@Component("payment-orderPlacedListener") // a custom name to avoid collision
@RequiredArgsConstructor
class OrderPlacedListener {

    private final @NonNull CollectPayment collectPayment;

    @TransactionalEventListener
    @Async
    public void on(OrderPlaced event) {
        collectPayment.collect(
                new ReferenceId(event.orderId),
                new Money(event.total));
    }
}
```

- **Qué copiar:** el nombre del caso de uso en imperativo de negocio (U-23); el id lo genera quien llama
  (`UUID orderId`), lo que permite idempotencia por id (P10); la excepción de negocio con nombre
  (`NoItemsToOrderException`); el listener package-private que traduce el evento a los tipos del módulo y
  llama a un puerto de entrada.
- **Qué no copiar:** `@TransactionalEventListener` + `@Async` sin registro de publicaciones: si el proceso
  cae tras el commit, el cobro no ocurre (Spring Modulith *Events*; U-17). La implementación
  `PlaceOrderJdbc` abre `REQUIRES_NEW` en el adaptador y separa su transacción de la del llamante (U-14).

### 9.8 · Caso de uso que inicia una saga orquestada (microservices-patterns/ftgo-application)

- **Muestra:** un servicio de aplicación que, en su transacción local, crea el agregado, publica sus
  eventos y arranca una saga; y la definición de la saga con pasos y compensaciones.
- **Caso de uso real:** *Create order* de FTGO (reparto de comida): el consumidor pide platos a un
  restaurante. La saga valida al consumidor, crea el ticket en cocina, autoriza la tarjeta y aprueba el
  pedido, o compensa (rechaza el pedido y cancela el ticket).
- **Repositorio:** `microservices-patterns/ftgo-application` (Richardson, *Microservices Patterns*) ·
  licencia Apache-2.0.
- **Archivos:**
  [`OrderService.java`](https://github.com/microservices-patterns/ftgo-application/blob/558dfc53b11d30a5f1d995c0c6d58d5106c28189/ftgo-order-service/src/main/java/net/chrisrichardson/ftgo/orderservice/domain/OrderService.java#L68-L92) ·
  [`CreateOrderSaga.java`](https://github.com/microservices-patterns/ftgo-application/blob/558dfc53b11d30a5f1d995c0c6d58d5106c28189/ftgo-order-service/src/main/java/net/chrisrichardson/ftgo/orderservice/sagas/createorder/CreateOrderSaga.java#L10-L36)

```java
  @Transactional
  public Order createOrder(long consumerId, long restaurantId, DeliveryInformation deliveryInformation,
                           List<MenuItemIdAndQuantity> lineItems) {
    Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId));

    List<OrderLineItem> orderLineItems = makeOrderLineItems(lineItems, restaurant);

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
// … CreateOrderSaga.java
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
            // …
            .build();
```

- **Qué copiar:** el agregado devuelve sus eventos (`ResultWithDomainEvents`) y el servicio los publica
  en la misma transacción que guarda (Eventuate Tram los escribe en una tabla outbox); la saga arranca
  dentro de esa transacción; «no encontrado» como excepción con nombre (`RestaurantNotFoundException`);
  cada paso con su compensación (P8-2).
- **Qué no copiar:** devolver la entidad `Order` (U-04); parámetros sueltos y un DTO del paquete `web`
  (`MenuItemIdAndQuantity`) en el servicio de dominio (U-02); un servicio con nueve dependencias que
  agrupa todos los casos de uso del pedido. En un monolito modular no hace falta saga distribuida:
  evento + política (§9.4) suele bastar. [opinión]

### 9.9 · Servicio transaccional que publica, y prueba de módulo con `Scenario` (spring-projects/spring-modulith)

- **Muestra:** la forma mínima de un caso de uso que publica un evento dentro de su transacción (el
  *Event Publication Registry* lo registra como outbox) y la prueba que espera el evento.
- **Caso de uso real:** *Complete order* del ejemplo oficial: al completar un pedido, *inventory*
  reacciona de forma asíncrona.
- **Repositorio:** `spring-projects/spring-modulith`, carpeta `spring-modulith-examples` · Apache-2.0
  (cabecera del archivo).
- **Archivos:**
  [`OrderManagement.java`](https://github.com/spring-projects/spring-modulith/blob/1fe549999aaa2401b675eba4a562ae374f4f96ba/spring-modulith-examples/spring-modulith-example-full/src/main/java/example/order/OrderManagement.java#L29-L40) ·
  [`OrderIntegrationTests.java`](https://github.com/spring-projects/spring-modulith/blob/1fe549999aaa2401b675eba4a562ae374f4f96ba/spring-modulith-examples/spring-modulith-example-full/src/test/java/example/order/OrderIntegrationTests.java#L117-L133)

```java
@Service
@RequiredArgsConstructor
public class OrderManagement {

    private final @NonNull ApplicationEventPublisher events;
    private final @NonNull OrderInternal dependency;

    @Transactional
    public void complete(Order order) {
        events.publishEvent(new OrderCompleted(order.getId()));
    }
}
// … OrderIntegrationTests.java
@ApplicationModuleTest
@RequiredArgsConstructor
class OrderIntegrationTests {

    private final OrderManagement orders;

    @Test
    void publishesOrderCompletion(Scenario scenario) {
        // this is a change
        var reference = new Order();

        scenario.stimulate(() -> orders.complete(reference))
                .andWaitForEventOfType(OrderCompleted.class)
                .matchingMappedValue(OrderCompleted::orderId, reference.getId())
                .toArrive();
    }
}
```

- **Qué copiar:** la publicación dentro del método `@Transactional` (U-17); `@ApplicationModuleTest` +
  `Scenario` para probar la postcondición «se publicó el evento» del módulo entero (complementa la prueba
  unitaria con fakes).
- **Qué no copiar:** recibir la entidad `Order` como parámetro en vez de un comando con su id; la falta de
  un puerto de salida para los eventos (el ejemplo es mínimo a propósito). En esta plantilla el servicio
  publica por el puerto `EventosDePedido` para poder usar un fake (como `evento-mensaje` T1).

### 9.10 · Estereotipos de puertos y regla hexagonal de jMolecules (xmolecules)

- **Muestra:** la anotación `@PrimaryPort` (y sus hermanas `@SecondaryPort`, `@PrimaryAdapter` y
  `@SecondaryAdapter`) y la regla ArchUnit que verifica el hexágono a partir de ellas.
- **Caso de uso real:** ninguno; es la biblioteca que usan los ejemplos de Spring Modulith y de
  jmolecules-examples para declarar los roles de la arquitectura.
- **Repositorios:** `xmolecules/jmolecules` y `xmolecules/jmolecules-integrations` · Apache-2.0.
- **Archivos:**
  [`PrimaryPort.java`](https://github.com/xmolecules/jmolecules/blob/20d2636beba2e3cb8f0a9b62c07e3606c49da84f/jmolecules-architecture/jmolecules-hexagonal-architecture/src/main/java/org/jmolecules/architecture/hexagonal/PrimaryPort.java) ·
  [`JMoleculesArchitectureRules.java`](https://github.com/xmolecules/jmolecules-integrations/blob/6e6282f72bcf891c69a63706cebacd4ba61c1ba0/jmolecules-archunit/src/main/java/org/jmolecules/archunit/JMoleculesArchitectureRules.java#L251-L310)

```java
/**
 * In Hexagonal Architecture an {@link PrimaryPort} describes an interface into an application's core that is exposed to
 * the outside to drive the application. A {@link PrimaryAdapter} would refer to those ports in its implementation.
 // …
 */
@Port
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
@Documented
@Stereotype(priority = 100, inherited = false)
public @interface PrimaryPort {
// … JMoleculesArchitectureRules.java
    public static ArchRule ensureHexagonal() {
        return ensureHexagonal(VerificationDepth.STRICT);
    }
    // …
    public static ArchRule ensureHexagonal(VerificationDepth depth, StereotypeLookup lookup) {
        return hexagonalArchitecture(lookup)

                // Adapters

                .whereLayer(HEXAGONAL_ADAPTER_UNQUALIFIED)
                // …
                .whereLayer(HEXAGONAL_PRIMARY_ADAPTER)
                .mayOnlyBeAccessedByLayers(HEXAGONAL_ADAPTER_UNQUALIFIED, UNANNOTATED)
```

- **Qué copiar:** anotar el paquete `port.in` con `@PrimaryPort` y `port.out` con `@SecondaryPort` en
  `package-info.java` (el `@Target` admite `PACKAGE`), y correr `JMoleculesArchitectureRules.ensureHexagonal()`
  en el build (U-01, U-24). Para CQRS, jMolecules trae además `@Command`, `@CommandHandler` y
  `@QueryModel` (`jmolecules-cqrs-architecture`).
- **Qué no copiar:** la rama clonada es `2.1.0-SNAPSHOT`; usa la última versión publicada en Maven Central
  y comprueba que trae `@PrimaryPort` (el javadoc dice «since 1.5»).

**Límite de lo verificado:** en estos repositorios no apareció una prueba de contrato compartida para un
puerto de salida **que no sea un repositorio**. La de repositorio está en `adaptador-persistencia` §9.7
(spring-ai), y T9 la adapta, como se indica allí.

## 10 · Plantillas de código

Esqueletos en Java 21 + Spring Boot 4 (Spring Framework 7). Cada `TODO Pn` lo decide la pregunta n.
Paquete de ejemplo: `com.ejemplo.pedidos`. Suponen lo que entregan las plantillas vecinas, que no se
repite aquí: `entidad-agregado` T5-T6 (`Pedido.confirmar(Instant)`, `extraerEventos()`, `PedidoVacio`,
`TransicionInvalida`), `adaptador-persistencia` §10.1 y §10.6 (`Pedidos`, `ConflictoDeVersion`,
`PedidosEnMemoria`, `PedidosContratoTest`) y `evento-mensaje` T1 (`EventosDePedido`).

### T1 · Comando que valida su forma (basado en §9.1, sin Bean Validation)

```java
package com.ejemplo.pedidos.application.port.in;

public record ConfirmarPedidoComando(PedidoId pedidoId, Solicitante solicitante) {  // TODO P13: nombre en imperativo
  public ConfirmarPedidoComando {                                                  // TODO P14: solo forma, sin reglas
    Objects.requireNonNull(pedidoId, "pedidoId");
    Objects.requireNonNull(solicitante, "solicitante");
  }
}

/** Identidad de quien pide; la arma el adaptador de entrada desde el token o desde el evento. */
public record Solicitante(UsuarioId usuario, Set<Rol> roles) {                     // TODO P15
  public Solicitante { Objects.requireNonNull(usuario, "usuario"); roles = Set.copyOf(roles); }
  public boolean tiene(Rol rol) { return roles.contains(rol); }
  public static Solicitante sistema(String modulo) { return new Solicitante(UsuarioId.tecnico(modulo), Set.of(Rol.SISTEMA)); }
}
```

Variante P14-2 (Bean Validation, como buckpal): anota los componentes con `@NotNull` y llama a un
`Validator` en el constructor. Exige `jakarta.validation` en `application`. [opinión: preferir la ★]

### T2 · Puerto de entrada y resultado (basado en §9.1 y §9.7)

```java
package com.ejemplo.pedidos.application.port.in;

@PrimaryPort                                                                        // org.jmolecules.architecture.hexagonal
public interface ConfirmarPedido {                                                  // TODO P1/P13: verbo + sustantivo
  /**
   * Confirma el pedido para que se reserve el stock.
   * @throws PedidoNoExiste       si no hay pedido con ese id            → 404
   * @throws AccesoDenegado       si el solicitante no puede confirmarlo → 403
   * @throws PedidoVacio          regla R1 (dominio)                      → 422
   * @throws CreditoInsuficiente  regla R2                                → 422
   * @throws ConflictoDeVersion   otro cambio ganó                        → 409
   */
  ResultadoConfirmacion ejecutar(ConfirmarPedidoComando comando);
}

public record ResultadoConfirmacion(PedidoId pedidoId, Dinero total, Instant confirmadoEn) {}  // TODO P13: solo lo que usa el cliente
```

Variante consulta (poda «solo lectura»):

```java
public interface ConsultarPedidosPendientes {
  List<VistaPedidoPendiente> ejecutar(ConsultaPendientes consulta);
  record ConsultaPendientes(ClienteId cliente, int limite) {
    public ConsultaPendientes {
      Objects.requireNonNull(cliente, "cliente");
      if (limite < 1 || limite > 100) throw new IllegalArgumentException("limite fuera de 1..100");  // forma, no regla
    }
  }
}
public record VistaPedidoPendiente(PedidoId id, Dinero total, Instant creadoEn) {}
```

### T3 · Errores de la aplicación (U-07; el adaptador web los traduce a RFC 9457)

Los errores **de dominio** (`PedidoVacio`, `TransicionInvalida`) vienen de `entidad-agregado` T5. Aquí van
los que nacen en la capa de aplicación.

```java
package com.ejemplo.pedidos.application.port.in;

public abstract sealed class ErrorDeAplicacion extends RuntimeException           // RuntimeException → rollback por defecto
    permits PedidoNoExiste, AccesoDenegado, CreditoInsuficiente {                    // TODO P18: catálogo cerrado
  protected ErrorDeAplicacion(String mensaje) { super(mensaje); }
}
public final class PedidoNoExiste extends ErrorDeAplicacion {
  public PedidoNoExiste(PedidoId id) { super("No existe el pedido %s".formatted(id.valor())); }
}
public final class AccesoDenegado extends ErrorDeAplicacion {
  public AccesoDenegado(UsuarioId u, String accion) { super("%s no puede %s".formatted(u.valor(), accion)); }
}
public final class CreditoInsuficiente extends ErrorDeAplicacion {                  // TODO P6: ¿regla del dominio?
  public CreditoInsuficiente(ClienteId c, Dinero disponible, Dinero total) {
    super("Crédito de %s: %s < %s".formatted(c.valor(), disponible, total));
  }
}
```

Catálogo que se entrega a `endpoint` (su P16), con un `type` estable [opinión sobre la forma de la URI]:

| Excepción | HTTP | `type` |
|---|---|---|
| `PedidoNoExiste` | 404 | `/problemas/pedido-no-existe` |
| `AccesoDenegado` | 403 | `/problemas/acceso-denegado` |
| `PedidoVacio` | 422 | `/problemas/pedido-vacio` |
| `CreditoInsuficiente` | 422 | `/problemas/credito-insuficiente` |
| `ConflictoDeVersion` | 409 | `/problemas/conflicto-de-version` |
| `CreditoNoDisponible` (técnico) | 503 | `/problemas/servicio-no-disponible` |

### T4 · Puertos de salida: solo las firmas (basado en §9.1 y §9.3; Cockburn: nombre por intención)

```java
package com.ejemplo.pedidos.application.port.out;

// Repositorio del agregado: firma completa e implementación en adaptador-persistencia §10.1.
// Aquí solo se confirma qué operaciones usa este caso de uso: porId y guardar.

@SecondaryPort
public interface CreditoDeClientes {                                                // TODO P16: una frase de intención
  /** Crédito disponible ahora; vacío si el cliente no tiene línea de crédito. */
  Optional<Dinero> disponible(ClienteId cliente);                                   // TODO P17: tipos del dominio
}
public final class CreditoNoDisponible extends RuntimeException {                   // TODO P18: fallo técnico propio
  public CreditoNoDisponible(ClienteId c, Throwable causa) { super("Crédito no disponible para " + c.valor(), causa); }
}

// Eventos: puerto EventosDePedido de evento-mensaje T1, tal cual.
// Reloj: java.time.Clock inyectado (no hace falta un puerto propio).
```

### T5 · Servicio de aplicación (★; basado en §9.5, §9.8 y §9.9)

```java
package com.ejemplo.pedidos.application.service;

@Service                                                                            // o @Bean en una @Configuration, como library
@Transactional                                                                      // org.springframework… · TODO P19 · en la clase concreta (U-14)
class ConfirmarPedidoServicio implements ConfirmarPedido {                          // package-private (U-25)

  private final Pedidos pedidos;
  private final CreditoDeClientes credito;
  private final EventosDePedido eventos;
  private final Clock reloj;

  ConfirmarPedidoServicio(Pedidos pedidos, CreditoDeClientes credito, EventosDePedido eventos, Clock reloj) {
    this.pedidos = pedidos; this.credito = credito; this.eventos = eventos; this.reloj = reloj;
  }

  @Override
  public ResultadoConfirmacion ejecutar(ConfirmarPedidoComando c) {
    var pedido = pedidos.porId(c.pedidoId())                                        // 1 cargar UN agregado
        .orElseThrow(() -> new PedidoNoExiste(c.pedidoId()));                       // TODO P18
    autorizar(c.solicitante(), pedido);                                             // 2 TODO P15

    var disponible = credito.disponible(pedido.cliente())                           // 3 dato externo · TODO P16
        .orElse(Dinero.cero(pedido.total().moneda()));
    if (disponible.esMenorQue(pedido.total())) {                                    // TODO P6: si es invariante, pásala al dominio
      throw new CreditoInsuficiente(pedido.cliente(), disponible, pedido.total());
    }

    var ahora = reloj.instant();
    pedido.confirmar(ahora);                                                        // 4 decide el dominio (PedidoVacio…)
    pedidos.guardar(pedido);                                                        // 5 ConflictoDeVersion si otro ganó
    pedido.extraerEventos().forEach(eventos::publicar);                             // 6 outbox, misma transacción · TODO P9
    return new ResultadoConfirmacion(pedido.getId(), pedido.total(), ahora);        // 7 record, nunca el agregado
  }

  private void autorizar(Solicitante s, Pedido p) {                                 // TODO P15: regla real de permisos
    boolean dueno = p.vendedor().equals(s.usuario());
    if (!dueno && !s.tiene(Rol.SUPERVISOR)) throw new AccesoDenegado(s.usuario(), "confirmar " + p.getId().valor());
  }
}
```

Notas de la frontera (Spring Framework 7):
- No llames a otro método `@Transactional` de esta misma clase: el proxy no lo intercepta.
- Si `CreditoDeClientes` es HTTP y puede tardar, consúltalo en un componente que corre **antes** de abrir
  la transacción, o acepta el riesgo con un tiempo de espera corto (P11). [opinión]
- Reintento ante `ConflictoDeVersion`, si la P10 lo pide: `@Retryable(includes = ConflictoDeVersion.class,
  maxRetries = 2)` (Spring Framework 7, `org.springframework.resilience.annotation`, con
  `@EnableResilientMethods`) en un bean **exterior** a la transacción (el adaptador de entrada o un
  decorador), para que cada intento abra una transacción nueva. [opinión sobre dónde ponerlo]

### T6 · Caso de uso disparado por un evento (basado en §9.4; detalle en `evento-mensaje` T2)

```java
package com.ejemplo.pedidos.adapter.in.eventos;

@Component
class ConfirmarAlAprobarPago {                                                      // política: traduce y delega
  private final ConfirmarPedido confirmar;
  ConfirmarAlAprobarPago(ConfirmarPedido confirmar) { this.confirmar = confirmar; }

  @ApplicationModuleListener                                                        // async + REQUIRES_NEW + tras el commit
  void on(PagoAprobado evento) {                                                    // TODO P2 · dedupe: evento-mensaje T2/T3
    confirmar.ejecutar(new ConfirmarPedidoComando(evento.pedidoId(), Solicitante.sistema("pagos")));  // TODO P15: identidad técnica
  }
}
```

### T7 · Consulta de solo lectura (poda «solo lectura»; Hombergs, «read-only use cases»)

```java
package com.ejemplo.pedidos.application.service;

@Service
@Transactional(readOnly = true)
class ConsultarPedidosPendientesServicio implements ConsultarPedidosPendientes {
  private final PedidosPendientesLectura lectura;                                    // puerto de proyección, no el repositorio
  ConsultarPedidosPendientesServicio(PedidosPendientesLectura lectura) { this.lectura = lectura; }
  @Override public List<VistaPedidoPendiente> ejecutar(ConsultaPendientes q) { return lectura.de(q.cliente(), q.limite()); }
}

// application.port.out
public interface PedidosPendientesLectura {
  List<VistaPedidoPendiente> de(ClienteId cliente, int limite);                      // SQL directo en el adaptador (adaptador-persistencia, lectura)
}
```

### T8 · Dobles en memoria (Fake Object, Meszaros; basado en §9.6)

`PedidosEnMemoria` ya está en `adaptador-persistencia` §10.6, con la regla de versión. Aquí van los de los
demás puertos de este caso de uso:

```java
// src/test/java/com/ejemplo/pedidos/application/port/out/
public final class CreditoDeClientesEnMemoria implements CreditoDeClientes {
  private final Map<ClienteId, Dinero> lineas = new ConcurrentHashMap<>();
  private final Set<ClienteId> caidos = ConcurrentHashMap.newKeySet();

  public CreditoDeClientesEnMemoria con(ClienteId c, Dinero disponible) { lineas.put(c, disponible); return this; }
  public CreditoDeClientesEnMemoria caido(ClienteId c) { caidos.add(c); return this; }   // simula el fallo técnico

  @Override public Optional<Dinero> disponible(ClienteId c) {
    if (caidos.contains(c)) throw new CreditoNoDisponible(c, new IllegalStateException("simulado"));
    return Optional.ofNullable(lineas.get(c));
  }
}

public final class EventosCapturados implements EventosDePedido {                   // fake que además guarda lo publicado
  private final List<Object> publicados = new CopyOnWriteArrayList<>();
  @Override public void publicar(Object evento) { publicados.add(evento); }
  public List<Object> publicados() { return List.copyOf(publicados); }
}
```

### T9 · Prueba de contrato del puerto de salida (Rainsberger; adapta `adaptador-persistencia` §9.7 a un puerto que no es repositorio)

```java
public abstract class CreditoDeClientesContratoTest {
  protected abstract CreditoDeClientes crear();
  /** Prepara el dato en el sistema real (servidor simulado, BD de prueba…) o en la memoria. */
  protected abstract void dado(ClienteId c, Dinero disponible);
  protected abstract void dadoCaido(ClienteId c);

  static final ClienteId CON_CREDITO = ClienteId.de("01928c1e-0000-7000-8000-0000000000c1");
  static final ClienteId SIN_LINEA   = ClienteId.de("01928c1e-0000-7000-8000-0000000000c2");

  @Test void devuelveElDisponible() {
    dado(CON_CREDITO, Dinero.cop(500_000));
    assertThat(crear().disponible(CON_CREDITO)).contains(Dinero.cop(500_000));
  }
  @Test void sinLineaDevuelveVacio() { assertThat(crear().disponible(SIN_LINEA)).isEmpty(); }   // U-12
  @Test void fallaTecnicaEsCreditoNoDisponible() {                                               // U-13
    dadoCaido(CON_CREDITO);
    assertThatThrownBy(() -> crear().disponible(CON_CREDITO)).isInstanceOf(CreditoNoDisponible.class);
  }
}

class CreditoDeClientesEnMemoriaContratoTest extends CreditoDeClientesContratoTest {
  private final CreditoDeClientesEnMemoria memoria = new CreditoDeClientesEnMemoria();
  @Override protected CreditoDeClientes crear() { return memoria; }
  @Override protected void dado(ClienteId c, Dinero d) { memoria.con(c, d); }
  @Override protected void dadoCaido(ClienteId c) { memoria.caido(c); }
}

class CreditoDeClientesHttpContratoIT extends CreditoDeClientesContratoTest {        // TODO P20: servidor simulado (p. ej. WireMock)
  // crear(): el adaptador real apuntando al servidor simulado.
  // dado(...): respuesta 200 con el disponible · dadoCaido(...): 503 o un retraso mayor que el tiempo de espera.
}
```

### T10 · Prueba del caso de uso con fakes, y reglas de arquitectura (U-06, U-21, U-24)

```java
class ConfirmarPedidoServicioTest {                                                 // sin Spring, < 1 s
  static final Instant T0 = Instant.parse("2026-01-10T12:00:00Z");
  final PedidosEnMemoria pedidos = new PedidosEnMemoria();                            // adaptador-persistencia §10.6
  final CreditoDeClientesEnMemoria credito = new CreditoDeClientesEnMemoria();
  final EventosCapturados eventos = new EventosCapturados();
  final ConfirmarPedido confirmar =
      new ConfirmarPedidoServicio(pedidos, credito, eventos, Clock.fixed(T0, ZoneOffset.UTC));

  @Test void r2Cumple_confirmaYPublica() {                                           // R2 · cumple
    var p = PedidoMadre.borradorConTotal(Dinero.cop(480_000));                        // Dado
    pedidos.guardar(p);
    credito.con(p.cliente(), Dinero.cop(500_000));
    var r = confirmar.ejecutar(new ConfirmarPedidoComando(p.getId(), SolicitanteMadre.vendedorDe(p)));  // Cuando
    assertThat(r).isEqualTo(new ResultadoConfirmacion(p.getId(), Dinero.cop(480_000), T0));             // Entonces
    assertThat(eventos.publicados()).singleElement().isInstanceOf(PedidoConfirmado.class);
  }
  @Test void r2Viola_rechazaSinEventos() {                                           // R2 · viola
    var p = PedidoMadre.borradorConTotal(Dinero.cop(480_000));
    pedidos.guardar(p);
    credito.con(p.cliente(), Dinero.cop(400_000));
    assertThatThrownBy(() -> confirmar.ejecutar(new ConfirmarPedidoComando(p.getId(), SolicitanteMadre.vendedorDe(p))))
        .isInstanceOf(CreditoInsuficiente.class);
    assertThat(eventos.publicados()).isEmpty();
  }
  @Test void pedidoInexistente() {                                                   // P18
    assertThatThrownBy(() -> confirmar.ejecutar(new ConfirmarPedidoComando(PedidoId.nuevo(), SolicitanteMadre.supervisor())))
        .isInstanceOf(PedidoNoExiste.class);
  }
  @Test void otroVendedorNoPuede() { /* TODO P15: AccesoDenegado y ningún evento */ }
  @Test void dosVecesUnSoloEvento() { /* TODO P10: la segunda ejecución no publica otro PedidoConfirmado */ }
  @Test void creditoCaido() { /* TODO P18: CreditoNoDisponible sube tal cual; el pedido sigue en borrador */ }
}

@AnalyzeClasses(packages = "com.ejemplo.pedidos")
class ReglasDeAplicacionTest {
  @ArchTest static final ArchRule hexagono = JMoleculesArchitectureRules.ensureHexagonal();          // U-01
  @ArchTest static final ArchRule sinAdaptadores = noClasses().that().resideInAPackage("..application..")
      .should().dependOnClassesThat().resideInAPackage("..adapter..");                               // U-02
  @ArchTest static final ArchRule sinErroresGenericos = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS; // U-07
  @ArchTest static final ArchRule txSoloEnServicios = noClasses()
      .that().resideOutsideOfPackages("..application.service..", "..adapter.out..")
      .should().beAnnotatedWith(org.springframework.transaction.annotation.Transactional.class);   // U-14 (clases)
  @ArchTest static final ArchRule sinInyeccionDeCampo = GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION; // U-25
}
```

La prueba del módulo con `Scenario` (postcondición «se publicó `PedidoConfirmado`») está en
`evento-mensaje` T6 y no se repite.

## 11 · Qué se genera

Orden dentro del segmento: contrato primero, de afuera hacia adentro, de modo que cada paso compile
(`investigacion/05` §2.2).

1. `ConfirmarPedidoComando`, `Solicitante` (T1) + prueba de construcción inválida.
2. `ConfirmarPedido` y `ResultadoConfirmacion` (T2); `package-info.java` de `port.in` con `@PrimaryPort`.
3. `ErrorDeAplicacion` y sus subclases (T3) + tabla de traducción para `endpoint`.
4. Puertos de salida (T4) con `@SecondaryPort`; se confirman `Pedidos` y `EventosDePedido` de las vecinas.
5. Dobles en memoria (T8) y pruebas de contrato (T9): la abstracta + la subclase de memoria. La subclase
   real (`…ContratoIT`) nace con el adaptador.
6. `ConfirmarPedidoServicio` (T5) y su prueba con fakes (T10): un par cumple/viola por regla, un caso por
   error del catálogo y uno de idempotencia.
7. Variantes si aplican: política por evento (T6) y consulta (T7).
8. `ReglasDeAplicacionTest` (T10).

Sale hacia otras plantillas: a `entidad-agregado`, el método invocado (`confirmar(Instant)`) y los errores
que se esperan; a `adaptador-persistencia`, las operaciones de repositorio que se usan y
`ConflictoDeVersion`; a `evento-mensaje`, los eventos publicados y la identidad técnica de los listeners;
a `endpoint`, el catálogo de T3.
Dependencias de prueba: JUnit 5, AssertJ, ArchUnit y `jmolecules-archunit`; un servidor HTTP simulado
solo para el contrato de los puertos HTTP.

## 12 · Referencias

- Robert C. Martin, *The Clean Architecture* (2012). Verificado: los casos de uso «orquestan el flujo de
  datos hacia y desde las entidades»; regla de dependencias; inversión en las fronteras; cruzan
  estructuras simples. https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
- Robert C. Martin, *Clean Architecture* (2017). Interactor, *input/output boundary* y *request/response
  models*. Libro; no se releyó para esta plantilla.
- Alistair Cockburn, *Hexagonal Architecture* (2005). Verificado: la intención («ser manejada igualmente
  por usuarios, programas, pruebas automatizadas o scripts»), el puerto como «conversación con
  propósito», los adaptadores de prueba y los casos de uso escritos en la frontera del hexágono.
  https://alistair.cockburn.us/hexagonal-architecture/
- Alistair Cockburn, *Writing Effective Use Cases* (2001). Niveles de objetivo, actor primario,
  precondiciones, escenario principal de 3-9 pasos, extensiones, garantías mínimas y de éxito. Libro.
- Tom Hombergs, *Get Your Hands Dirty on Clean Architecture*, 2.ª ed. (2023). Caps. «Implementing a Use
  Case» (validación de entrada frente a reglas, modelos de entrada y salida por caso de uso, casos de solo
  lectura), «Testing Architecture Elements» y «Taking Shortcuts Consciously».
  https://leanpub.com/get-your-hands-dirty-on-clean-architecture · Artículo compañero, verificado
  (puerto de entrada, comando autovalidado, puertos de salida dictados por el caso de uso):
  https://reflectoring.io/spring-hexagonal/
- Vaughn Vernon, *Implementing Domain-Driven Design* (2013), cap. 14 «Application». Servicio de
  aplicación delgado, transacción y seguridad. Libro.
- Vaughn Vernon, *Effective Aggregate Design* II (2011). Una transacción, un agregado; consistencia
  eventual; razones para romper la regla. https://www.dddcommunity.org/library/vernon_2011/
- Martin Fowler, *Patterns of Enterprise Application Architecture* (2002). *Transaction Script*,
  *Domain Model*, *Service Layer* (verificado: «define la frontera de una aplicación con una capa de
  servicios…») y *Unit of Work*. https://martinfowler.com/eaaCatalog/
- Martin Fowler, *CQRS*. Verificado: «para la mayoría de los sistemas CQRS agrega complejidad
  riesgosa»; solo en partes concretas. https://martinfowler.com/bliki/CQRS.html
- Martin Fowler, *CommandQuerySeparation*. Verificado: la definición y la excepción del `pop`.
  https://martinfowler.com/bliki/CommandQuerySeparation.html
- Greg Young, *CQRS Documents* (2010). Comandos en imperativo que el manejador puede rechazar (lo cita el
  javadoc de jMolecules `@Command`). https://cqrs.files.wordpress.com/2010/11/cqrs_documents.pdf
- Gamma et al., *Design Patterns* (1994). *Command* y *Mediator*. https://refactoring.guru/design-patterns/mediator
- Chris Richardson, *Microservices Patterns* (2018), cap. 4. Sagas, orquestación y coreografía,
  transacciones compensables, pivote y reintentables, contramedidas a la falta de aislamiento. Verificado
  en microservices.io: una saga es «una secuencia coordinada de transacciones locales».
  https://microservices.io/patterns/data/saga.html
- microservices.io, *Transactional Outbox*. Verificado: guardar el mensaje en la misma transacción;
  duplicados que exigen consumidores idempotentes. https://microservices.io/patterns/data/transactional-outbox.html
- Gerard Meszaros, *xUnit Test Patterns* (2007). *Test Double*, *Fake Object*, *Test Stub*, *Mock Object*
  y *Test Spy*. http://xunitpatterns.com/Fake%20Object.html (el sitio rechazó la conexión el
  15-oct-2026; las definiciones se verificaron en Fowler, *Mocks Aren't Stubs*: «Fake objects actually
  have working implementations, but usually take some shortcut»).
  https://martinfowler.com/articles/mocksArentStubs.html
- J. B. Rainsberger, *Getting Started with Contract Tests*. Verificado: el contrato comprueba el
  significado y no solo la sintaxis; se extrae una fábrica y la misma prueba corre contra cada
  implementación. https://blog.thecodewhisperer.com/permalink/getting-started-with-contract-tests
- Spring Framework 7, *Using @Transactional*. Verificado: autoinvocación, rollback por defecto,
  `rollbackOn=ALL_EXCEPTIONS` desde 6.2, anotar la clase concreta, visibilidad desde 6.0.
  https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/annotations.html
- Spring Framework 7, *Resilience Features*. Verificado: `@Retryable` desde 7.0, 3 reintentos y 1 s por
  defecto, atributo `includes`. https://docs.spring.io/spring-framework/reference/core/resilience.html
- Spring Modulith, *Events*. Verificado: `@ApplicationModuleListener` = `@Async` + `REQUIRES_NEW` +
  `@TransactionalEventListener`; el registro se escribe en la transacción del negocio.
  https://docs.spring.io/spring-modulith/reference/events.html
- jMolecules: `@PrimaryPort`, `@SecondaryPort`, `@Command`, `@CommandHandler`, `@QueryModel`
  (Apache-2.0). https://github.com/xmolecules/jmolecules
- jMolecules ArchUnit: `JMoleculesArchitectureRules.ensureHexagonal()` y `JMoleculesDddRules.all()`.
  https://github.com/xmolecules/jmolecules-integrations/tree/main/jmolecules-archunit
- ArchUnit User Guide: `onionArchitecture()` y `GeneralCodingRules`. https://www.archunit.org/userguide/html/000_Index.html
- RFC 9457, *Problem Details for HTTP APIs*. https://www.rfc-editor.org/rfc/rfc9457.html
- Herberto Graça, *Explicit Architecture*. Servicios de aplicación frente a servicios de dominio; los
  puertos los diseña el núcleo. https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/
- Repositorios de §9: thombergs/buckpal (sin licencia), ddd-by-examples/library (MIT),
  citerus/dddsample-core (MIT), ttulka/ddd-example-ecommerce (MIT),
  microservices-patterns/ftgo-application (Apache-2.0), spring-projects/spring-modulith (Apache-2.0),
  xmolecules/jmolecules y jmolecules-integrations (Apache-2.0).
- Internas: `investigacion/02` (§2 mapa de paquetes, §3 prácticas, §6 cuándo no), `investigacion/03`
  (reglas 5, 11 y 14), `investigacion/04` (§3.3, §3.4, §4 y §5), `investigacion/05` (§2.2); plantillas
  vecinas `endpoint`, `entidad-agregado`, `adaptador-persistencia` y `evento-mensaje`.
