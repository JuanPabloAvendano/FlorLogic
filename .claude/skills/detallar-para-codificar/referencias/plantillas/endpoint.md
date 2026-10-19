---
plantilla: endpoint
aplica_a: cualquier punto de entrada que se pide y se responde (POST, PUT, PATCH, DELETE, GET); controlador, DTO de petición y respuesta, mapeo, traducción de errores, firma del puerto de entrada y contrato JSON
entra_desde: ninguna
sale_hacia: caso-de-uso
---
# Plantilla · Endpoint (adaptador primario)

## 0 · Instrucciones para el agente

1. Usa esta plantilla cuando lo que se detalla es algo que **alguien pide y que responde**: un endpoint
   HTTP, una operación RPC o un comando expuesto por la red. Un evento que solo se escucha va a
   `evento-mensaje`.
2. **Baja solo hasta el puerto de entrada** (decisión del usuario). Aquí se cierran el controlador, los
   DTO, el mapeo DTO → comando, la traducción de errores a HTTP y la **firma** del puerto. Qué hace el
   caso de uso por dentro, la transacción y el dominio los detalla `caso-de-uso` y las plantillas que
   siguen. Si la conversación se va hacia «cómo se guarda», anótalo como pendiente para `caso-de-uso` y
   vuelve.
3. **Los contratos se escriben en JSON** (decisión del usuario): ejemplos de petición y respuesta, el
   `application/problem+json` de cada error y, si hay OpenAPI o JSON Schema, también en JSON.
4. Antes de la P1, muestra el esqueleto de §1 con los huecos ⟦1⟧…⟦12⟧ y la opción ★ como valor
   provisional (`investigacion/04` §2.3).
5. Un endpoint por ronda. Si aparece otro (por ejemplo, el `GET` de la operación asíncrona), regístralo
   como pendiente y termina el primero.
6. Haz las preguntas en orden, 10 por ronda: P1–P10 en la primera, P11–P20 en la siguiente (SKILL.md, etapa 3), respeta la poda de §6 y pregunta una variable por pregunta
   (`investigacion/04` regla 9).
7. No inventes códigos de error, límites ni cifras. Si el humano no sabe, abre un pendiente, asume la ★
   y sigue (`investigacion/04` regla 12).
8. Cada fallo de la P7 sale con un caso *dado / cuando / entonces* y con su JSON de `problem+json`.
9. Lo que el código, el OpenAPI existente o un ADR ya fijan, confírmalo en una línea y no gastes la
   pregunta (`investigacion/04` regla 7).
10. Detecta la pila del proyecto antes de dar ejemplos. Por defecto son Java 21 y Spring Boot 3 o 4
    (Spring Framework 6.x o 7.x). En Spring Boot 4, Jackson es la versión 3 (`tools.jackson`).

## 1 · Qué se define primero

El orden sigue la dependencia: sin el hecho del negocio no hay nombre, y sin el contrato no hay errores
ni idempotencia. Los tres primeros son esenciales (regla dura de cobertura, `investigacion/04` §5.1).

| ⟦k⟧ | Concepto | Por qué va aquí | Fuente |
|---|---|---|---|
| 1 | **Hecho del negocio y actor**: qué logra quien llama | Da el nombre al puerto y fija el límite del endpoint. La API se diseña desde el objetivo del consumidor, no desde la base de datos. | Lauret, *The Design of Web APIs* (API goals canvas); Cockburn (puerto con nombre de intención) |
| 2 | **Recurso, método y ruta** | El método decide la seguridad y la idempotencia por defecto; la ruta es contrato visible. | RFC 9110 §9.2.1-9.2.2; Google AIP-121, AIP-136; Zalando #134, #141 |
| 3 | **Contrato de petición** (cabeceras y cuerpo JSON) | Es lo que el cliente programa. Cambiarlo después rompe clientes. | Zalando #100, #101; OpenAPI 3.1 |
| 4 | **Respuesta de éxito** (código, cabeceras y cuerpo) | Define el efecto observable y cómo encuentra el cliente lo creado. | RFC 9110 §15.3.2 (201 + `Location`), §15.3.5 (204) |
| 5 | **Catálogo de errores** | Cada fallo es un caso de prueba y una rama del cliente. | RFC 9457; Zalando #176; Azure *Errors* |
| 6 | **Idempotencia y reintentos** | En la red, el reintento es normal. Sin idempotencia, un corte duplica efectos. | Borrador IETF *Idempotency-Key* -07; Stripe; Google AIP-155; Zalando #230-#231 |
| 7 | **Síncrono o asíncrono** | Si tarda más que el tiempo de espera del cliente, hace falta 202 y un recurso de operación. | RFC 9110 §15.3.3; Azure *Long-running operations*; Google AIP-151 |
| 8 | **Autenticación y permisos** | Decide la credencial, el 401 frente al 403 y dónde se comprueba. | RFC 9110 §15.5.2, §15.5.4; Hombergs, *Implementing a Web Adapter* (paso 2) |
| 9 | **Límites y tiempos** (tamaño, cantidad, tasa, espera) | Protegen el nodo y son parte del contrato. | RFC 6585 §4 (429); RFC 9110 §15.5.14 (413), §10.2.3 (`Retry-After`); Zalando #153 |
| 10 | **Versión y evolución** | Fija qué cambios son compatibles y cómo se anuncia una ruptura. | Zalando #106-#108, #114-#115; Google AIP-180, AIP-185; Azure *Versioning* |
| 11 | **Frontera hexagonal**: DTO → comando → puerto | Mantiene HTTP y JSON fuera del núcleo. Es lo que esta plantilla entrega a `caso-de-uso`. | Cockburn (adaptador primario); Hombergs, *Mapping Between Boundaries*; Martin (regla de dependencias) |
| 12 | **Observabilidad y correlación** | Permite seguir una petición y relacionar un error con su traza. | Zalando #233 (`X-Flow-ID`); W3C Trace Context (`traceparent`) |

Esqueleto que se muestra antes de la P1:

```
Hecho y actor:  ⟦1⟧   → «la tienda registra un pedido confirmado por el cliente» · sistema «tienda-web»
Recurso:        ⟦2⟧   → POST /v1/pedidos
Petición:       ⟦3⟧   → {"cliente_id":"c-9","lineas":[{"sku":"ROSA-R","cantidad":24}]}
Éxito:          ⟦4⟧   → 201 Created · Location: /v1/pedidos/0192… · {"id":"0192…","estado":"CONFIRMADO"}
Errores:        ⟦5⟧   → 400 validación · 422 stock-insuficiente · 409 pedido-duplicado (problem+json)
Reintento:      ⟦6⟧   → Idempotency-Key obligatoria · 24 h · misma clave y otro cuerpo → 422
Tiempo:         ⟦7⟧   → síncrono, p99 < 1 s
Seguridad:      ⟦8⟧   → Bearer con scope pedidos:escribir · 401 sin token · 403 sin scope
Límites:        ⟦9⟧   → cuerpo ≤ 256 KiB · ≤ 100 líneas · 60 pet/min por cliente → 429
Versión:        ⟦10⟧  → v1 en la ruta; solo cambios aditivos; enums extensibles
Frontera:       ⟦11⟧  → PedidoRequest → RegistrarPedidoComando → RegistrarPedido.ejecutar(...)
Correlación:    ⟦12⟧  → traceparent de entrada · traceId en cada problem+json
```

## 2 · Funcionamiento apropiado

**Responsabilidad.** El endpoint es un **adaptador primario** (*driving*): traduce un protocolo a la
conversación que el núcleo ofrece por su puerto de entrada (Cockburn). No decide reglas de negocio, no
abre transacciones y no habla con la base de datos (`investigacion/02` #4, #13 y #20). Hombergs le
asigna siete tareas, en este orden (*Implementing a Web Adapter*):

1. mapear la petición HTTP a objetos;
2. comprobar la autorización;
3. validar la entrada (forma: tipos, obligatorios, tamaños);
4. mapear la entrada al modelo de entrada del caso de uso (el comando);
5. llamar al caso de uso por su puerto;
6. mapear la salida del caso de uso a HTTP;
7. devolver la respuesta HTTP.

**Flujo de una petición:**

```
cliente HTTP                 adaptador primario (adapter.in.web)              núcleo (application)
────────────                 ───────────────────────────────────              ────────────────────
POST /v1/pedidos ──JSON──▶ 1 Jackson → PedidoRequest (record, @Valid)
Idempotency-Key            2 Spring Security: scope pedidos:escribir ─ 401/403
                           3 Bean Validation de forma ───────────── 400 problem+json
                           4 PedidoWebMapper → RegistrarPedidoComando ── el comando se autovalida
                           5 registrarPedido.ejecutar(comando) ───────────────▶ «puerto de entrada»
                                                                                 caso-de-uso (otra plantilla)
                           6 PedidoRegistrado → PedidoResponse ◀──────────────── resultado o excepción
                           7 201 + Location + cuerpo
                             o @RestControllerAdvice: excepción de dominio → ProblemDetail (422/409…)
```

**Dónde vive cada pieza** (`investigacion/02` §2):

| Pieza | Paquete | Depende de |
|---|---|---|
| Controlador, DTO web (`*Request`, `*Response`), mapper web, `@RestControllerAdvice` del módulo | `pedidos.adapter.in.web` | puerto de entrada, comando, Spring Web, Jackson |
| Puerto de entrada (`interface RegistrarPedido`) y comando (`record RegistrarPedidoComando`) | `pedidos.application.port.in` | tipos del dominio; nada de web |
| Resultado del caso de uso (`record PedidoRegistrado`) | `pedidos.application.port.in` | tipos del dominio |
| Contrato publicado (OpenAPI o JSON Schema, en JSON) | `contrato/` o `src/main/resources/openapi/` | — |

**Validación en el borde frente a reglas en el dominio.** La forma (tipo, obligatorio, longitud, rango
sintáctico, formato) se valida en el borde y en el constructor del comando. Las reglas que necesitan
estado («hay stock», «el pedido está abierto») van en el dominio o en el caso de uso
(Hombergs, *Implementing a Use Case*: validación de entrada frente a validación de reglas de negocio;
`investigacion/02` #9). Que el comando sea un tipo ya validado aplica la idea *Parse, don't validate*
(King, 2019): después del borde nadie vuelve a comprobar la forma.

**Mapeo.** Por defecto se usa el mapeo *Full* en la entrada: un comando por caso de uso, distinto del DTO
web (Hombergs, *Mapping Between Boundaries*). El DTO web puede cambiar por razones del protocolo
(nombres en `snake_case`, versión, campos de enlace) sin tocar el núcleo. [opinión] En una consulta de
solo lectura sin reglas, el DTO de respuesta puede proyectarse directamente desde el resultado.

**Síncrono frente a asíncrono.** Si la operación cabe en el tiempo de espera del cliente, se responde
200, 201 o 204. Si no, se responde **202 Accepted** con la dirección de un recurso de operación que el
cliente consulta con `GET` hasta un estado terminal, guiado por `Retry-After` (RFC 9110 §15.3.3; Azure
*LRO*: estados `NotStarted`, `Running`, `Succeeded`, `Failed`, `Canceled`).

## 3 · Patrones de diseño

| Patrón | Problema que resuelve | Úsalo cuando | No lo uses cuando | Fuente |
|---|---|---|---|---|
| Adaptador primario (Ports & Adapters) | Que HTTP no contamine el núcleo y que el caso de uso se pueda manejar desde pruebas | siempre en hexagonal | prototipo CRUD sin reglas (capas simples) | Cockburn; `investigacion/02` §6 |
| Controlador por caso de uso | Controladores gordos que mezclan operaciones | cada operación tiene su comando | consultas triviales de un mismo recurso [opinión] | Hombergs, *Slicing Controllers*; buckpal `SendMoneyController` |
| DTO (Data Transfer Object) | Separar la forma del cable del modelo interno | siempre en la frontera web | — | Fowler, PoEAA (*Data Transfer Object*); Martin (DTO en las fronteras) |
| Command (modelo de entrada) | Un contrato explícito e inmutable para cada caso de uso | el caso de uso tiene entrada | lectura sin parámetros | Hombergs (*input model*); GoF Command (en sentido amplio) [opinión] |
| Mapper en el borde | Traducir DTO ↔ comando y resultado ↔ DTO | mapeo *Full* | el mapeo es 1:1 y trivial (puede ir en el controlador) | Hombergs, *Mapping Between Boundaries* |
| Problem Details | Errores legibles por máquina y uniformes | APIs HTTP con JSON | errores genéricos donde basta el código (RFC 9457 §4) | RFC 9457; Spring `ProblemDetail` |
| Traductor central de errores | Un solo lugar para pasar excepciones a HTTP | más de un endpoint | — | `investigacion/02` #13; Spring `@RestControllerAdvice` |
| Idempotency Key | Reintentos de POST o PATCH sin duplicar efectos | POST o PATCH con efectos y clientes que reintentan | GET, PUT y DELETE, ya idempotentes (Stripe; RFC 9110 §9.2.2) | Borrador IETF -07; Stripe; Zalando #230 |
| Clave secundaria de negocio | Deduplicar sin cabecera, por un dato natural | existe un id del cliente único (número de orden externo) | no hay dato natural único | Zalando #231; Geewax, *Request deduplication* |
| Request-Reply asíncrono (202 + operación) | Operaciones largas sin bloquear al cliente | tarda más que el tiempo de espera o depende de terceros | cabe con holgura en el tiempo síncrono | Azure *LRO*; Google AIP-151; Geewax, *Long-running operations* |
| Versionado | Introducir rupturas sin romper a los clientes actuales | hay clientes que no controlas | API interna desplegada a la vez que su único cliente [opinión] | Google AIP-185; Zalando #114-#115; Spring 7 *API Versioning* |
| Lector tolerante y enums extensibles | Evolucionar sin versión nueva | siempre en el cliente; enums que pueden crecer | conjunto cerrado por definición | Fowler, *TolerantReader*; Zalando #108, #112; Azure *Extensible enums* |
| Petición condicional (ETag + `If-Match`) | Escrituras que se pisan | PUT o PATCH sobre recursos editables por varios | creación o recursos inmutables | RFC 9110 §8.8.3, §13.1.1; Zalando #182 |
| Paginación por cursor | Colecciones grandes y estables entre páginas | GET de colección que crece | ≤ 100 ítems fijos | Zalando #160; Google AIP-158; Azure `nextLink` |
| Contract-first | Que el contrato se acuerde antes del código | API pública o con varios consumidores | prueba de concepto [opinión] | Zalando #100-#101; OpenAPI |
| Contrato dirigido por el consumidor | Detectar rupturas antes de desplegar | hay consumidores conocidos | API pública abierta (se usan pruebas del proveedor) | Pact; Spring Cloud Contract |

**Decisiones de patrón que cierra la ronda:**
- síncrono ★ o asíncrono con 202: P6;
- idempotencia por `Idempotency-Key` ★, por clave de negocio o por PUT con id del cliente: P5 y P17;
- formato de error RFC 9457 ★ o propio: P16;
- versión en la ruta ★, en cabecera o en el media type: P12;
- mapeo *Full* ★ o sin mapeo: P20.

## 4 · Prácticas profesionales verificables

Severidad: C = crítica (3), M = mayor (2), m = menor (1). Verificación: A = ArchUnit, P = prueba,
L = lectura.

| id | Práctica | Sev. | Al detallar | Al verificar | Fuente |
|---|---|---|---|---|---|
| EP-01 | El controlador solo depende del puerto de entrada, no de servicios, repositorios ni de otro adaptador | C | P20 | A: `classes().that().resideInAPackage("..adapter.in.web..").should().onlyDependOnClassesThat().resideOutsideOfPackages("..adapter.out..","..application.service..")` | `investigacion/02` #3, #4 |
| EP-02 | Ningún DTO web cruza al núcleo: el puerto recibe un comando y no un `*Request` | C | P20 | A: `noClasses().that().resideInAPackage("..application..").should().dependOnClassesThat().resideInAPackage("..adapter..")` | `investigacion/02` #7 |
| EP-03 | El controlador no contiene reglas de negocio, `@Transactional` ni acceso a datos | M | P18, P20 | A: no hay `@Transactional` en `..adapter.in..`; L: sin `if` de negocio | `investigacion/02` #11, #20 |
| EP-04 | Un comando inmutable por caso de uso, que se autovalida al construirse | M | P14, P20 | P: construir el comando inválido falla | Hombergs (*input model*); `investigacion/02` #8 |
| EP-05 | La forma se valida en el borde y las reglas con estado en el dominio | M | P7, P14 | L: anotaciones de Bean Validation solo de forma; P: la regla de negocio tiene su prueba en el núcleo | Hombergs; `investigacion/02` #9 |
| EP-06 | Todos los errores salen como `application/problem+json`, con `type` estable y documentado | C | P16 | P: `@WebMvcTest` comprueba `Content-Type` y `type` para cada error del catálogo | RFC 9457 §3.1.1; Zalando #176 |
| EP-07 | Los errores no filtran detalles internos (pila, SQL, nombres de clase) | C | P16 | P: un error inesperado devuelve 500 con `detail` genérico | RFC 9457 §5; Lauret (errores informativos sin exponer la implementación) |
| EP-08 | La traducción excepción → HTTP vive en un solo `@RestControllerAdvice` por módulo | M | P16 | L: no hay `try/catch` de negocio en controladores | `investigacion/02` #13 |
| EP-09 | Métodos con su semántica: GET seguro, PUT y DELETE idempotentes, POST para crear o para acciones | C | P12 | L: contra RFC 9110 §9.2 | RFC 9110 §9.2.1-9.2.2 |
| EP-10 | Toda creación responde 201 con `Location`; un 202 trae la dirección de la operación | M | P15, P6 | P: comprueba la cabecera | RFC 9110 §15.3.2-15.3.3 |
| EP-11 | POST y PATCH con efectos tienen una estrategia de idempotencia documentada (clave, ventana y respuesta ante otro cuerpo) | C | P5, P17 | P: misma clave y mismo cuerpo → misma respuesta; otro cuerpo → 422; en curso → 409 | Borrador IETF -07 §2.7; Stripe |
| EP-12 | La clave de idempotencia se valida (longitud y caracteres) y nunca lleva datos personales | m | P17 | P: clave vacía o de 256 caracteres → 4xx | Stripe (≤ 255, sin datos sensibles); GOV.UK Pay |
| EP-13 | 401 cuando falta o falla la credencial, 403 cuando no alcanza el permiso | M | P13 | P: un caso por cada uno | RFC 9110 §15.5.2, §15.5.4 |
| EP-14 | Límites explícitos: tamaño de cuerpo (413), cantidad de ítems (400 o 422) y tasa (429 + `Retry-After`) | M | P9 | P: prueba que excede cada límite | RFC 9110 §15.5.14; RFC 6585 §4; Zalando #153 |
| EP-15 | Solo cambios compatibles dentro de una versión; los clientes ignoran campos desconocidos | C | P19 | P: prueba de contrato del consumidor; L: diferencia del OpenAPI | Zalando #106-#108; Fowler, *TolerantReader* |
| EP-16 | Los enums del contrato son extensibles y el cliente tolera valores nuevos | m | P19 | L: el OpenAPI lo dice; P: el cliente no falla con un valor nuevo | Zalando #112; Azure *Extensible enums* |
| EP-17 | Nombres y formatos estándar: recursos en plural, sin verbos en la ruta, fechas ISO 8601, dinero como objeto | m | P12, P14 | L contra la guía | Zalando #134, #141, #169, #173 |
| EP-18 | El contrato existe en JSON (OpenAPI o JSON Schema) y la prueba del controlador lo cumple | M | P14-P16 | P: prueba de contrato (Pact o Spring Cloud Contract) o validación contra el esquema | Zalando #100-#101 |
| EP-19 | Cada respuesta de error lleva un id de correlación o de traza | m | P13 | P: `traceId` presente en el `problem+json` | Zalando #233; W3C Trace Context |
| EP-20 | La prueba del adaptador usa `@WebMvcTest` con el puerto simulado y verifica el comando recibido | M | P20 | P: existe y corre sin base de datos | Hombergs, *Testing Architecture Elements*; buckpal `SendMoneyControllerTest` |
| EP-21 | Las operaciones asíncronas publican el recurso de operación con estado terminal y su tiempo de retención | M | P6, P10 | P: el `GET` de la operación llega a `Succeeded` o `Failed` | Azure *LRO*; Google AIP-151 |
| EP-22 | Las respuestas con datos sensibles no se guardan en caché | m | P15 | P: `Cache-Control: no-store` | RFC 9111 §5.2.2.5; GOV.UK Pay `createNewPayment` |

## 5 · Árbol de preguntas

Formato: título de una línea + contexto pequeño o mediano con el **por qué** (SKILL.md, «Cómo preguntas») · opciones con la ★ primero y un ejemplo cada una ·
**cierra**.

**A · Propósito**

**P1. ¿Qué hecho del negocio dispara esta llamada?**
Por qué: fija el nombre del puerto y el límite del endpoint.
1. ★ Un verbo de negocio sobre un objeto — «la tienda registra un pedido» → `interface RegistrarPedido`
2. Una acción sobre un recurso existente — «cancelar el pedido» → `CancelarPedido`, `POST /v1/pedidos/{id}:cancelar`
3. Una consulta — «ver el estado del pedido» → `ConsultarPedido`, `GET /v1/pedidos/{id}`
Cierra: ⟦1⟧.

**P2. ¿Quién la llama?**
Por qué: decide la credencial, el canal y quién reintenta.
1. ★ Otro sistema, con credencial de servicio — `tienda-web` con `client_credentials`, scope `pedidos:escribir`
2. Una persona, a través de un front — token de usuario con el rol `vendedor`
3. Un trabajo programado interno — identidad técnica `cierre-nocturno`, sin exposición pública
Cierra: ⟦1⟧, ⟦8⟧.

**P3. ¿Qué documento la justifica?**
Por qué: trazabilidad (15 % del índice). Sin fuente no hay criterio para aceptar.
1. ★ Un requisito o escenario con id — `RF-12 «registrar pedido confirmado»`
2. Un ADR — `ADR-007: los clientes reintentan con Idempotency-Key`
3. Ninguno → se crea un pendiente — `Q-1: falta la fuente; dueño: producto`
Cierra: trazabilidad.

**B · Comportamiento**

**P4. En el camino feliz, ¿qué queda distinto después?**
Por qué: define el efecto observable y el código de éxito.
1. ★ Se crea un recurso — `201 Created`, `Location: /v1/pedidos/0192f…`
2. Cambia el estado de uno existente — `200 OK` con el recurso o `204 No Content`
3. Nada cambia (lectura) — `GET` con `200` y el recurso
Cierra: ⟦4⟧.

**P5. Si llega dos veces, ¿qué pasa?**
Por qué: decide si un reenvío por corte duplica efectos.
1. ★ Devuelve la respuesta de la primera vez — `201` la primera, el mismo `201` al reintento (Stripe, borrador IETF)
2. Responde que ya existe — `409` con `type …/pedido-duplicado` y enlace al existente
3. Se procesa otra vez, porque es inocuo — `PUT /v1/pedidos/{id}/nota` reemplaza el mismo valor
Cierra: ⟦6⟧, EP-11.

**P6. ¿Responde al instante o después?**
Por qué: separa la respuesta síncrona del patrón 202 con operación.
1. ★ Al instante — `201` en p99 < 1 s, por debajo del tiempo de espera del cliente
2. Después, con operación consultable — `202` + `Location: /v1/operaciones/op-5` + `Retry-After: 5`
3. Después, con aviso — `202` y un evento o *callback* al terminar (se pasa a `evento-mensaje`)
Cierra: ⟦7⟧, EP-21.

**P7. ¿Qué fallos puede ver quien llama?**
Por qué: cada fallo es una rama del cliente y un caso de prueba.
1. ★ Catálogo cerrado, cada uno con `type` — `stock-insuficiente` 422 · `cliente-bloqueado` 403 · `pedido-duplicado` 409
2. Solo genéricos — `400` validación y `422` «no procesable», sin distinguir
3. Los que ya trae el sistema heredado — se mapean uno a uno a `type` propios
Cierra: ⟦5⟧, EP-05.

**P8. ¿Una parte mala tumba todo o solo esa parte?**
Por qué: fija la granularidad del error en peticiones con varios ítems.
1. ★ Todo o nada — `422` con `"errors":[{"pointer":"/lineas/3/cantidad","detail":"debe ser > 0"}]`
2. Por ítem — `200` con `{"resultados":[{"indice":3,"estado":"RECHAZADO","type":"…"}]}`
3. Lote con estado múltiple — `207 Multi-Status` (Zalando #152)
Cierra: ⟦5⟧.

**C · Variables**

**P9. ¿Qué límites tiene?**
Por qué: protegen el nodo y el cliente debe conocerlos.
1. ★ Tamaño, cantidad y tasa — cuerpo ≤ 256 KiB (413) · ≤ 100 líneas (422) · 60/min por cliente (429)
2. Solo tamaño y cantidad; la tasa la pone el gateway — límite del servidor + regla del gateway
3. Ninguno (se rechaza) — permite agotar la memoria con un lote gigante
Cierra: ⟦9⟧, EP-14.

**P10. ¿Qué tiempos lo gobiernan?**
Por qué: ante un corte, el cliente debe saber cuándo reintentar y hasta cuándo.
1. ★ Espera del servidor menor que la del cliente, más la ventana de idempotencia — servidor 10 s, cliente 30 s, claves 24 h
2. Además, la retención de la operación asíncrona — `op-5` consultable 7 días
3. Sin tiempos explícitos — se marca pendiente con dueño
Cierra: ⟦9⟧, ⟦6⟧.

**P11. Cada variable, ¿fija, configurable o pendiente?**
Por qué: decide dónde vive el valor y quién lo cambia.
1. ★ Configurable y validada al arrancar — `@ConfigurationProperties("pedidos.api") @Validated record LimitesApi(@Max(1000) int maxLineas)`
2. Fija en el contrato — `"maxItems": 100` en el JSON Schema
3. Pendiente con dueño — `V-3: tasa por cliente = ? · dueño: operación`
Cierra: ⟦9⟧.

**D · Forma técnica**

**P12. ¿Qué método, ruta y versión?**
Por qué: es el contrato más visible; cambiarlo rompe a todos.
1. ★ Sustantivo plural, método estándar y versión mayor en la ruta — `POST /v1/pedidos` (AIP-185)
2. Método personalizado o subrecurso — `POST /v1/pedidos/{id}:cancelar` (AIP-136) o `POST /v1/pedidos/{id}/cancelacion`
3. Versión fuera de la ruta — cabecera `API-Version: 1.1` (Spring 7) o `Accept: application/vnd.tienda.pedido+json;version=2` (Zalando #114)
Cierra: ⟦2⟧, ⟦10⟧, EP-09, EP-17.

**P13. ¿Qué cabeceras y qué credencial lleva?**
Por qué: la seguridad y la correlación se fijan en el contrato, no después.
1. ★ `Authorization: Bearer` con scope, más `traceparent` — `scope=pedidos:escribir`; el `traceId` vuelve en los errores
2. Clave de API por cliente — `X-Api-Key: …` (solo de servicio a servicio en red interna)
3. mTLS entre servicios — certificado del cliente, sin token
Cierra: ⟦8⟧, ⟦12⟧, EP-13, EP-19.

**P14. ¿Qué campos lleva la petición, con su tipo y obligatoriedad?**
Por qué: es el esquema de entrada; lo que es obligatorio hoy no se puede relajar sin riesgo.
1. ★ Los mínimos de negocio, con tipos estándar — `{"cliente_id":"c-9","lineas":[{"sku":"ROSA-R","cantidad":24}],"entrega_el":"2026-11-02"}`
2. El recurso completo — incluye `estado`, `total` y `creado_en` (se desaconseja: son de solo salida, AIP-203)
3. Sin cuerpo — todo va en la ruta: `POST /v1/pedidos/{id}:confirmar`
Cierra: ⟦3⟧, EP-04, EP-17.

**P15. ¿Qué devuelve si sale bien?**
Por qué: es el esquema de salida y lo que el cliente guarda.
1. ★ `201` + `Location` + representación — `{"id":"0192f…","estado":"CONFIRMADO","total":{"monto":"48000","moneda":"COP"}}`
2. `200` + cuerpo — para acciones sobre un recurso existente
3. `204` sin cuerpo — el cliente no necesita nada más
Cierra: ⟦4⟧, EP-10, EP-22.

**P16. ¿En qué formato salen los errores?**
Por qué: errores explícitos y uniformes; el cliente programa contra `type`, no contra `detail`.
1. ★ RFC 9457 con `type` propios y extensiones — `{"type":"https://api.tienda.example/problemas/stock-insuficiente","title":"Stock insuficiente","status":422,"detail":"ROSA-R: faltan 4","sku":"ROSA-R"}`
2. Objeto de error propio — `{"error":{"code":"StockInsuficiente","message":"…","details":[…]}}` (estilo Azure)
3. Solo el código HTTP — aceptable solo para errores genéricos (RFC 9457 §4)
Cierra: ⟦5⟧, EP-06, EP-07, EP-08.

**P17. ¿Qué identifica un duplicado?**
Por qué: sin una clave definida, la idempotencia de la P5 no se puede implementar.
1. ★ Cabecera `Idempotency-Key` más la huella del cuerpo — `Idempotency-Key: 8e03978e-40d5-43e8-bc93-6894a57f9324`
2. Clave de negocio en el cuerpo — `"numero_orden_externa":"TW-2026-000123"`, única por cliente (Zalando #231)
3. El cliente elige el id y usa PUT — `PUT /v1/pedidos/0192f…` crea o devuelve el mismo
Cierra: ⟦6⟧, EP-11, EP-12.

**P18. ¿Qué efectos promete el contrato y quién los garantiza?**
Por qué: el endpoint promete y el caso de uso cumple. Aquí solo se fija la promesa.
1. ★ Postcondición del puerto; la transacción la detalla `caso-de-uso` — «con el 201, el pedido existe y se publicó `PedidoRegistrado`»
2. Efecto parcial visible — «con el 202, el lote quedó recibido; el procesamiento se informa en la operación»
3. Sin efectos (lectura) — se salta la pregunta
Cierra: ⟦11⟧ (salida hacia `caso-de-uso`).

**P19. ¿Qué cambio sería compatible?**
Por qué: la evolución sin romper clientes es una regla MUST (Zalando #106).
1. ★ Solo aditivos, lector tolerante y enums extensibles — agregar `"nota"` opcional sí; renombrar `cantidad` no
2. Versión mayor nueva para cada ruptura, con fecha de retiro — `/v2/pedidos` + `Deprecation` y `Sunset` en `/v1` (RFC 9745, RFC 8594)
3. Sin política (se rechaza) — cada despliegue puede romper a un cliente
Cierra: ⟦10⟧, EP-15, EP-16.

**P20. ¿Cómo cruza la frontera: controlador → DTO → comando → puerto?**
Por qué: mantiene HTTP y JSON fuera del núcleo (regla de dependencias).
1. ★ DTO web propio, mapeo *Full* a un comando y puerto `interface` — `registrarPedido.ejecutar(mapper.aComando(req, clave))`
2. Sin mapeo: el DTO es el comando (solo CRUD sin reglas) — `ejecutar(PedidoRequest r)` (EP-02 pasa a N/A, con ADR)
3. El controlador llama al servicio concreto (se rechaza) — `@Autowired PedidoServiceImpl service`
Cierra: ⟦11⟧, EP-01, EP-02, EP-03, EP-20.

## 6 · Poda y ramas nuevas

- **GET (P4 = lectura):** se saltan P5, P17 y P18, porque leer es seguro e idempotente (RFC 9110 §9.2).
  Los cupos van a la paginación (cursor ★, AIP-158), al filtrado, a la caché (`ETag` + `If-None-Match`) y
  a la proyección de campos.
- **PUT o DELETE:** P17 se convierte en confirmación («el id lo pone la ruta»). El cupo va a la
  concurrencia: `If-Match` con 412 (RFC 9110 §13.1.1).
- **Síncrono (P6 = 1):** desaparecen la retención de la operación (P10-2) y EP-21.
- **Asíncrono (P6 = 2):** nace un segundo endpoint, `GET /v1/operaciones/{id}`, que se registra como
  pendiente para la ronda siguiente. El cupo libre va a «¿qué devuelve la operación al terminar?».
- **P2 = trabajo interno sin red pública:** P13 y P9 se vuelven confirmaciones de una línea.
- **Sin ítems múltiples:** se salta la P8.
- **Sin efectos (P18 = 3):** se salta la P18.
- **Lo que la documentación ya responde** se confirma en una línea (`investigacion/04` regla 7).
- **Cupos liberados:** primero, una pregunta por cada fallo de la P7 sin caso *dado/cuando/entonces*;
  después, una por cada contradicción detectada; al final, la metapregunta «¿hay algo que debí
  preguntarte sobre este endpoint?» (`investigacion/04` regla 18).

## 7 · Cobertura

Pesos en el orden de §1 (⟦1⟧ pesa 12, ⟦12⟧ pesa 1). Un punto está cubierto cuando tiene respuesta o queda
`[PENDIENTE]` con su dueño.

- [ ] ⟦1⟧ Hecho del negocio, actor y fuente
- [ ] ⟦2⟧ Método, ruta y semántica del método
- [ ] ⟦3⟧ Petición: cabeceras y cuerpo, con un ejemplo JSON y su esquema
- [ ] ⟦4⟧ Respuesta de éxito: código, cabeceras y cuerpo JSON
- [ ] ⟦5⟧ Catálogo de errores con `type`, `status`, ejemplo `problem+json` y un caso *dado/cuando/entonces* por fallo
- [ ] ⟦6⟧ Idempotencia: clave, huella, ventana y respuesta ante reintento, otro cuerpo y petición en curso
- [ ] ⟦7⟧ Síncrono o asíncrono, con el tiempo esperado
- [ ] ⟦8⟧ Credencial, permiso y dónde se comprueba
- [ ] ⟦9⟧ Límites y tiempos, cada uno fijo, configurable o pendiente
- [ ] ⟦10⟧ Versión y reglas de evolución compatible
- [ ] ⟦11⟧ Frontera: DTO, mapeo, comando y firma del puerto de entrada, más la postcondición
- [ ] ⟦12⟧ Correlación y trazas

## 8 · Antipatrones

| Antipatrón | Señal detectable | Fuente |
|---|---|---|
| Controlador con lógica | `if` de reglas, cálculos o acceso a repositorio dentro de `@RestController` | `investigacion/02` §6 |
| Transacción en el controlador | `@Transactional` sobre métodos de `..adapter.in.web..` (MOLGENIS, 9.11) | `investigacion/02` #20 |
| Entidad o DTO web en el núcleo | `UseCase.ejecutar(PedidoRequest)`; el controlador devuelve una `@Entity` | `investigacion/02` #7 |
| Mapeo al dominio dentro del DTO | `LocationAndDrinks.toOrder()` construye el agregado en el adaptador (9.4) | [opinión] sobre spring-restbucks |
| Verbos y datos de negocio en la ruta | `POST /accounts/send/{origen}/{destino}/{monto}` (buckpal, 9.1) | Zalando #141 |
| Éxito sin señal | `void` y `200` aunque el caso de uso devuelva `false` (buckpal, 9.1) | [opinión] |
| `404` para todo | «no existe» y «ya pagado» responden igual (restbucks, 9.3) | RFC 9110 §15.5; Lauret |
| Error genérico que filtra detalles | `ProblemDetail.forStatusAndDetail(500, e.getMessage())` | RFC 9457 §5 |
| Formato de error mixto | unos endpoints devuelven `problem+json` y otros `{"message":…}` | Zalando #176 |
| POST no idempotente con reintentos | el cliente reintenta tras un corte y se crean dos pedidos | Borrador IETF -07 §1 |
| Clave de idempotencia sin huella | misma clave y otro cuerpo devuelven la respuesta vieja en silencio | Borrador IETF -07 §2.4; Stripe |
| Operación larga síncrona | peticiones que superan el tiempo de espera del gateway; 504 intermitentes | Azure *LRO* |
| Romper en caliente | renombrar o quitar un campo, o volver obligatorio uno opcional, sin versión nueva | Zalando #106 |
| Enum cerrado en el cliente | el cliente falla al recibir un valor nuevo | Zalando #112; Azure |
| Paginación por offset en colecciones vivas | `?page=7` repite o salta ítems cuando se inserta | Zalando #160 |
| Sin límites | lote sin `maxItems` ni tamaño máximo; sin 429 | RFC 6585 §4 |

## 9 · Código de referencia real

Todos los archivos se leyeron el 15-oct-2026 en el commit indicado (descargados de
`raw.githubusercontent.com` y comprobados con `gh api`). Los fragmentos son literales, con la indentación
normalizada a espacios; `// …` marca lo omitido.

### 9.1 Adaptador web por caso de uso, comando autovalidado y puerto de entrada

- **Muestra:** el controlador solo conoce el puerto `SendMoneyUseCase` y construye un `SendMoneyCommand`
  que se valida al crearse (P20-1, EP-01, EP-04). Hay un controlador por caso de uso.
- **Caso de uso real:** BuckPal es la aplicación de ejemplo del libro de Hombergs. Representa una cuenta
  de pagos en línea; este endpoint **transfiere dinero de una cuenta a otra**.
- **Repositorio:** `thombergs/buckpal`. **Sin licencia declarada** (`license: null` en la API de
  GitHub), así que todos los derechos quedan reservados. Se cita solo como referencia breve; no copies el
  código literal.
- **Archivos:**
  [`SendMoneyController.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/main/java/io/reflectoring/buckpal/adapter/in/web/SendMoneyController.java#L13-L32),
  [`SendMoneyCommand.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/main/java/io/reflectoring/buckpal/application/port/in/SendMoneyCommand.java#L9-L25)
  y [`SendMoneyUseCase.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/main/java/io/reflectoring/buckpal/application/port/in/SendMoneyUseCase.java#L3-L7)

```java
@WebAdapter
@RestController
@RequiredArgsConstructor
class SendMoneyController {

    private final SendMoneyUseCase sendMoneyUseCase;

    @PostMapping(path = "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}")
    void sendMoney(
            @PathVariable("sourceAccountId") Long sourceAccountId,
            @PathVariable("targetAccountId") Long targetAccountId,
            @PathVariable("amount") Long amount) {

        SendMoneyCommand command = new SendMoneyCommand(
                new AccountId(sourceAccountId),
                new AccountId(targetAccountId),
                Money.of(amount));

        sendMoneyUseCase.sendMoney(command);
    }
}
// … SendMoneyCommand.java (application/port/in)
public record SendMoneyCommand(
        @NotNull AccountId sourceAccountId,
        @NotNull AccountId targetAccountId,
        @NotNull @PositiveMoney Money money
) {
    // … constructor que asigna los campos y llama a validate(this);
}
// … SendMoneyUseCase.java
public interface SendMoneyUseCase {
    boolean sendMoney(SendMoneyCommand command);
}
```

- **Qué copiar:** el paquete `adapter.in.web` frente a `application.port.in`; un controlador por caso de
  uso; el comando como `record` inmutable que se valida al construirse; la clase del controlador con
  visibilidad de paquete.
- **Qué no copiar:** los datos de negocio en la ruta y el verbo `send` (antipatrón de §8; con P12-1 sería
  `POST /v1/transferencias` con un cuerpo JSON). La respuesta `void` ignora el `boolean` del puerto, así
  que una transferencia rechazada también responde 200. Un movimiento de dinero sin `Idempotency-Key`.

### 9.2 Prueba del adaptador web con el puerto simulado

- **Muestra:** `@WebMvcTest` del controlador con el puerto simulado. Se comprueba el comando exacto que
  recibe el caso de uso (EP-20), sin base de datos ni servicio real.
- **Caso de uso real:** el mismo de 9.1, la transferencia entre cuentas.
- **Repositorio:** `thombergs/buckpal` · sin licencia declarada (solo referencia).
- **Archivo:** [`SendMoneyControllerTest.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/test/java/io/reflectoring/buckpal/adapter/in/web/SendMoneyControllerTest.java#L16-L38)

```java
@WebMvcTest(controllers = SendMoneyController.class)
class SendMoneyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SendMoneyUseCase sendMoneyUseCase;

    @Test
    void testSendMoney() throws Exception {

        mockMvc.perform(post("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
                41L, 42L, 500)
                .header("Content-Type", "application/json"))
                .andExpect(status().isOk());

        then(sendMoneyUseCase).should()
                .sendMoney(eq(new SendMoneyCommand(
                        new AccountId(41L),
                        new AccountId(42L),
                        Money.of(500L))));
    }
}
```

- **Qué copiar:** probar el adaptador aislado y verificar el mapeo HTTP → comando con `eq(...)`, que
  funciona porque el comando es un `record`.
- **Qué no copiar:** `@MockBean`, obsoleto desde Spring Boot 3.4 y retirado en Spring Boot 4; se usa
  `@MockitoBean`. Falta un caso por cada error del catálogo.

### 9.3 Creación con `201 Created` + `Location`, y error de negocio con `ErrorResponse`

- **Muestra:** un `PUT` que crea el pago y responde `ResponseEntity.created(uri)` (EP-10). Un
  `@ExceptionHandler` traduce `PaymentFailed` con `ErrorResponse.builder`, la API de Problem Details de
  Spring 6 (EP-06).
- **Caso de uso real:** Spring RESTBucks es la cafetería del libro *REST in Practice*. El cliente pide un
  café y después **paga el pedido con tarjeta**; mientras no esté pagado, el pago se puede enviar.
- **Repositorio:** `odrotbohm/spring-restbucks`. No tiene archivo de licencia en la raíz; la cabecera del
  archivo declara Apache-2.0.
- **Archivo:** [`PaymentController.java`](https://github.com/odrotbohm/spring-restbucks/blob/ad97ad03c367a69ff1de70c660be2731cbe7bc3d/server/src/main/java/de/odrotbohm/restbucks/payment/web/PaymentController.java#L56-L169)

```java
@Controller
@RequestMapping("/orders/{id}")
@ExposesResourceFor(Payment.class)
@RequiredArgsConstructor
class PaymentController {
    // …
    @PutMapping(path = PaymentLinks.PAYMENT)
    ResponseEntity<?> submitPayment(@PathVariable("id") Order order, @RequestBody PaymentForm form) {

        if (order == null || order.isPaid()) {
            return ResponseEntity.notFound().build();
        }

        var payment = paymentService.pay(order, form.number());
        var model = new PaymentModel(order.getPrice(), payment.getCreditCardNumber().getId()) //
                .add(paymentLinks.getOrderLinks().linkToItemResource(order));

        var paymentUri = paymentLinks.getPaymentLink(order).toUri();

        return ResponseEntity.created(paymentUri).body(model);
    }
    // …
    @ExceptionHandler
    ErrorResponse handle(PaymentFailed exception) {
        return ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST, exception.getMessage()).build();
    }
    // …
    record PaymentForm(CreditCardNumber number) implements DTO {}
}
```

- **Qué copiar:** el `201` con `Location` hacia el recurso creado; el DTO de entrada como `record`
  anidado y marcado (`implements DTO`); `ErrorResponse.builder` para emitir `problem+json` sin escribir
  el JSON a mano.
- **Qué no copiar:** «ya pagado» responde `404` igual que «no existe» (habría que usar 409 con su
  `type`). El controlador recibe el agregado `Order` desde la ruta y lo pasa al servicio, sin comando ni
  puerto. [opinión] Un pago rechazado con 400 sería mejor un 422 con su `type`.

### 9.4 DTO de entrada validado y mapeo en el borde

- **Muestra:** un DTO con `@NotNull` validado por `@Validated`, y un `Errors` que, si hay fallos, se
  convierte en la respuesta. Después se mapea al dominio y se responde `201` con el enlace `self`.
- **Caso de uso real:** en RESTBucks, **el cliente hace un pedido**: dice si lo toma en el local o para
  llevar, y qué bebidas quiere.
- **Repositorio:** `odrotbohm/spring-restbucks` · cabecera Apache-2.0.
- **Archivos:**
  [`OrderController.java`](https://github.com/odrotbohm/spring-restbucks/blob/ad97ad03c367a69ff1de70c660be2731cbe7bc3d/server/src/main/java/de/odrotbohm/restbucks/order/web/OrderController.java#L35-L58)
  y [`LocationAndDrinks.java`](https://github.com/odrotbohm/spring-restbucks/blob/ad97ad03c367a69ff1de70c660be2731cbe7bc3d/server/src/main/java/de/odrotbohm/restbucks/order/web/LocationAndDrinks.java#L33-L47)

```java
@BasePathAwareController
@RequiredArgsConstructor
class OrderController {

    private final Orders orders;
    // …
    @PostMapping(path = "/orders")
    public HttpEntity<?> placeOrder(@RequestBody @Validated LocationAndDrinks payload, Errors errors,
            PersistentEntityResourceAssembler assembler) {

        return MappedPayloads.of(payload, errors)
                .mapIfValid(LocationAndDrinks::toOrder)
                .mapIfValid(orders::save)
                .mapIfValid(assembler::toFullResource)
                .concludeIfValid(it -> ResponseEntity.created(it.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(it));
    }
}
// … LocationAndDrinks.java
@Data
public class LocationAndDrinks {

    @NotNull //
    private Location location;
    @NotNull //
    private List<Drink> drinks = new ArrayList<>();

    public Order toOrder() {

        Order order = new Order(Collections.emptyList(), location);
        drinks.forEach(order::add);

        return order;
    }
}
```

- **Qué copiar:** la validación de forma en el DTO y la cadena «validar → mapear → ejecutar → 201».
- **Qué no copiar:** el controlador guarda directamente con el repositorio `Orders`, sin puerto de
  entrada (EP-01). El DTO construye el agregado (`toOrder`); en hexagonal, el DTO se mapea a un comando y
  el caso de uso crea el agregado. Además es mutable (`@Data`); en Java 21 se usa un `record`.

### 9.5 `@RestControllerAdvice` con `ProblemDetail` y el id de traza

- **Muestra:** un traductor central (EP-08) que devuelve `ProblemDetail` (Spring 6+) para cada familia de
  excepción, agrega `traceId` como extensión (EP-19) y no filtra el mensaje de los errores inesperados
  (EP-07).
- **Caso de uso real:** la API del blog de Toshiaki Maki (`ik.am`). Publica y sirve las entradas, y
  recibe webhooks firmados para actualizar el contenido.
- **Repositorio:** `categolj/blog-api` · licencia Apache-2.0.
- **Archivo:** [`ProblemControllerAdvice.java`](https://github.com/categolj/blog-api/blob/23ae16bab3f5a50fac932cb51efad5d9d9c41959/src/main/java/am/ik/blog/problem/ProblemControllerAdvice.java#L21-L105)

```java
@RestControllerAdvice
public class ProblemControllerAdvice {
    // …
    @ExceptionHandler(ConstraintViolationsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleConstraintViolationsException(ConstraintViolationsException e) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Constraint violations found!");
        problemDetail.setProperty("violations", e.violations().details());
        return setTraceId(problemDetail);
    }
    // …
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ProblemDetail handleDataAccessException(DataAccessException e) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "There is a problem with database access.");
        log.error("There is a problem with database access.", e);
        return setTraceId(problemDetail);
    }
    // …
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleException(Exception e) {
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error occurred!");
        log.error("Unexpected error occurred!", e);
        return setTraceId(problemDetail);
    }

    private ProblemDetail setTraceId(ProblemDetail problemDetail) {
        final Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            problemDetail.setProperty("traceId", currentSpan.context().traceId());
        }
        return problemDetail;
    }
}
```

- **Qué copiar:** el retorno directo de `ProblemDetail`; las violaciones como extensión con nombre
  (RFC 9457 §3.2); el `detail` genérico con el error completo en el log; el `traceId`.
- **Qué no copiar:** no fija `type`, así que todos los errores quedan `about:blank` y el cliente solo
  puede distinguirlos por `status`. En la plantilla T8 cada error de dominio tiene su `type` estable.

### 9.6 Catálogo de `type` como constantes

- **Muestra:** las URI de `type`, estables y documentadas, una por error de negocio (EP-06). Las usa el
  `ExceptionTranslator` de JHipster, que extiende `ResponseEntityExceptionHandler`.
- **Caso de uso real:** la aplicación de muestra que genera JHipster gestiona usuarios y cuentas
  bancarias con operaciones y etiquetas. Los errores cubren, por ejemplo, el correo ya registrado al
  crear un usuario.
- **Repositorio:** `jhipster/jhipster-sample-app` · licencia Apache-2.0.
- **Archivo:** [`ErrorConstants.java`](https://github.com/jhipster/jhipster-sample-app/blob/6b000b5d23a36c45e01472471b84a44fa2464044/src/main/java/io/github/jhipster/sample/web/rest/errors/ErrorConstants.java#L5-L17)

```java
public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.jhipster.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");

    private ErrorConstants() {}
}
```

- **Qué copiar:** una base de URI propia y absoluta (RFC 9457 §3.1.1) y un `type` por error de negocio.
- **Qué no copiar:** el `ExceptionTranslator` completo, de más de 200 líneas con `instanceof`
  encadenados. En un módulo hexagonal basta un `@ExceptionHandler` por familia de excepción de dominio.

### 9.7 `Idempotency-Key` en el endpoint público: 201 si es nuevo, 200 si ya existía

- **Muestra:** la cabecera opcional `Idempotency-Key` se valida en el borde (de 1 a 255 caracteres, solo
  alfanuméricos y guiones; EP-12). La respuesta distingue `BRAND_NEW` (201 + `Location`) de `EXISTING`
  (200), y las respuestas con datos de pago llevan `Cache-Control: no-store` (EP-22).
- **Caso de uso real:** GOV.UK Pay es la plataforma de pagos del gobierno británico. Un servicio público
  (por ejemplo, el cobro de una tasa) **crea un pago** y recibe la URL a la que envía al ciudadano para
  pagar con tarjeta.
- **Repositorio:** `govuk-pay/pay-publicapi` · licencia MIT. Es JAX-RS sobre Dropwizard, no Spring; el
  patrón se traslada igual.
- **Archivo:** [`PaymentsResource.java`](https://github.com/govuk-pay/pay-publicapi/blob/e4e80a9cade39e6386e1d22388ecca29f6d56df4/src/main/java/uk/gov/pay/api/resources/PaymentsResource.java#L263-L317)

```java
    @POST
    @Timed
    @Path("/v1/payments")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    // … @Operation con las respuestas 201, 400, 401, 422, 429 y 500 documentadas
    public Response createNewPayment(@Parameter(hidden = true) @Auth Account account,
                                     @Parameter(required = true, description = "requestPayload")
                                     @Valid CreateCardPaymentRequest createCardPaymentRequest,
                                     @Nullable
                                     @Length(min = 1, max = 255, message = "Header [Idempotency-Key] can have a size between 1 and 255")
                                     @Pattern(regexp = "^$|^[a-zA-Z0-9-]+$", message = "Header [Idempotency-Key] can only contain alphanumeric characters and hyphens")
                                     @HeaderParam("Idempotency-Key")
                                     String idempotencyKey) {
        // …
        CreatedPaymentWithAllLinks createdPayment = createPaymentService.create(account, createCardPaymentRequest, idempotencyKey);

        PaymentWithAllLinks paymentWithAllLinks = createdPayment.getPayment();
        Response.ResponseBuilder response;

        switch (createdPayment.getWhenCreated()) {
            case BRAND_NEW -> response = Response
                    .created(publicApiUriGenerator.getPaymentURI(paymentWithAllLinks.getPaymentId()));
            case EXISTING -> response = Response.ok();
            default ->
                    throw new IllegalArgumentException(format("Unrecognised WhenCreated enum: %s", createdPayment.getWhenCreated()));
        }

        response.entity(paymentWithAllLinks)
                .header(PRAGMA, "no-cache")
                .header(CACHE_CONTROL, "no-store");
        // …
        return response.build();
    }
```

- **Qué copiar:** la versión mayor en la ruta (`/v1`); validar la clave en el borde; que el resultado del
  servicio diga si el recurso es nuevo o ya existía, y que el adaptador elija el código; documentar todas
  las respuestas en el OpenAPI.
- **Qué no copiar:** el `switch` con `default` que lanza `IllegalArgumentException`; con un enum o un
  `sealed` en Java 21, el `switch` exhaustivo no necesita `default`. Responder 200 al reintento es una
  decisión válida, pero Stripe y el borrador IETF devuelven la **misma** respuesta de la primera vez
  (201). Hay que elegir y documentarlo (P5).

### 9.8 Idempotencia del lado del proveedor: huella del cuerpo y carrera de peticiones

- **Muestra:** la clave se guarda con el cuerpo de la petición y el id del recurso creado, **en la misma
  transacción**. Al reintentar, se compara el cuerpo nuevo con el guardado: si es igual, devuelve el
  recurso existente; si es distinto, lanza `IdempotencyKeyUsedException` (409). Si dos peticiones con la
  misma clave chocan, la restricción única provoca un `RollbackException`, que también se traduce a 409.
- **Caso de uso real:** `pay-connector` es el servicio interno de GOV.UK Pay que **crea el cobro** frente
  a la pasarela de pago. Aquí se aplica a los pagos recurrentes con acuerdo
  (`AuthorisationMode.AGREEMENT`).
- **Repositorio:** `govuk-pay/pay-connector` · licencia MIT.
- **Archivos:**
  [`ChargesApiResource.java`](https://github.com/govuk-pay/pay-connector/blob/69d6519fe0842fc18f6f01d31cf8d6bea7770299/src/main/java/uk/gov/pay/connector/charge/resource/ChargesApiResource.java#L151-L190)
  y [`ChargeService.java`](https://github.com/govuk-pay/pay-connector/blob/69d6519fe0842fc18f6f01d31cf8d6bea7770299/src/main/java/uk/gov/pay/connector/charge/service/ChargeService.java#L310-L497)

```java
// ChargesApiResource.createNewCharge(...)
        if (idempotencyKey != null && chargeRequest.getAuthorisationMode() == AuthorisationMode.AGREEMENT) {
            Optional<ChargeResponse> maybeExistingChargeResponse = chargeService.checkForChargeCreatedWithIdempotencyKey(
                    chargeRequest, accountId, idempotencyKey, uriInfo);
            if (maybeExistingChargeResponse.isPresent()) {
                ChargeResponse existingChargeResponse = maybeExistingChargeResponse.get();
                return ok(existingChargeResponse.getLink("self")).entity(existingChargeResponse).build();
            }
        }
// … ChargeService.create(...)
        } catch (RollbackException e) {
            if (idempotencyKey != null && idempotencyDao.findByGatewayAccountIdAndKey(accountId, idempotencyKey).isPresent()) {
                LOGGER.info("Race condition between two requests to create a charge with the same Idempotency Key handled");
                throw new IdempotencyKeyUsedException();
            }
            throw e;
        }
// … ChargeService.checkForChargeCreatedWithIdempotencyKey(...)
        return idempotencyDao.findByGatewayAccountIdAndKey(gatewayAccountId, idempotencyKey).map(idempotencyEntity -> {
            // … convierte la petición nueva y la guardada a Map
            if (chargeRequestMap.equals(previousChargeRequestMap)) {
                LOGGER.info("Idempotency-Key was already used to create a request with matching values {}", idempotencyKey);
                ChargeEntity existingCharge = findChargeByExternalId(idempotencyEntity.getResourceExternalId());
                return populateResponseBuilderWith(aChargeResponseBuilder(), uriInfo, existingCharge).build();
            }
            // … registra ambos cuerpos sin datos personales
            throw new IdempotencyKeyUsedException();
        });
```

- **Qué copiar:** el alcance de la clave por cliente (`gatewayAccountId` + `key`); la huella del cuerpo;
  el registro de la clave en la misma transacción que el recurso; la restricción única como defensa
  ante carreras; la caducidad de las claves (`IDEMPOTENCY_KEY_EXPIRY_WINDOW_SECONDS`, en el mismo
  recurso).
- **Qué no copiar:** la comprobación vive en el recurso y en un servicio con JPA. En hexagonal, el
  controlador pone la clave en el comando y el caso de uso la resuelve con un puerto de salida
  (`caso-de-uso`). El cuerpo distinto responde 409, cuando el borrador IETF recomienda 422 (§2.7).

**Pruebas del mismo comportamiento** ([`PaymentsResourceCreateWithIdempotencyKeyIT.java`](https://github.com/govuk-pay/pay-publicapi/blob/e4e80a9cade39e6386e1d22388ecca29f6d56df4/src/test/java/uk/gov/pay/api/it/PaymentsResourceCreateWithIdempotencyKeyIT.java#L106-L140),
MIT): cada respuesta de la política es un caso con su código y su mensaje.

```java
        postPaymentResponseWithIdempotencyKey(paymentPayload(createChargeRequestParams), IDEMPOTENCY_KEY)
                .statusCode(409)
                .contentType(JSON)
                .body("code", is("P0191"))
                .body("description", is("The `Idempotency-Key` you sent in the request header has already been used to create a payment."));
        // …
    @Test
    void createPayment_responseWith422_whenIdempotencyKeyIsEmpty() {
        // …
        postPaymentResponseWithIdempotencyKey(SUCCESS_PAYLOAD, "")
                .statusCode(422)
                .contentType(JSON)
                .body("code", is("P0102"))
                .body("header", is("Idempotency-Key"))
                .body("description", is("Header [Idempotency-Key] can have a size between 1 and 255"));
    }
```

### 9.9 Prueba de contrato del consumidor con Pact

- **Muestra:** el cliente (`publicapi`) prueba su llamada contra un servidor simulado por Pact que
  reproduce el contrato acordado con `connector`. El contrato queda en un archivo **JSON**
  (`src/test/resources/pacts/*.json`) que el proveedor verifica en su propio build.
- **Caso de uso real:** la API pública de GOV.UK Pay **crea un pago** pidiéndoselo al conector; el
  contrato fija qué campos manda y qué espera de vuelta.
- **Repositorio:** `govuk-pay/pay-publicapi` · licencia MIT.
- **Archivo:** [`CreatePaymentServicePactTest.java`](https://github.com/govuk-pay/pay-publicapi/blob/e4e80a9cade39e6386e1d22388ecca29f6d56df4/src/test/java/uk/gov/pay/api/service/CreatePaymentServicePactTest.java#L46-L102)

```java
@RunWith(MockitoJUnitRunner.class)
public class CreatePaymentServicePactTest {
    // …
    @Rule
    public PayPactProviderRule connectorRule = new PayPactProviderRule("connector", this);
    // …
    @Test
    @PactVerification({"connector"})
    @Pacts(pacts = {"publicapi-connector-create-payment-with-minimum-fields"})
    public void testCreatePaymentWithMinimumFields() {
        Account account = new Account("123456", TokenPaymentType.CARD, "a-token-link");
        var requestPayload = CreateCardPaymentRequestBuilder.builder()
                .amount(100)
                .returnUrl("https://somewhere.gov.uk/rainbow/1")
                .reference("a reference")
                .description("a description")
                .build();

        CreatedPaymentWithAllLinks createdPaymentWithAllLinks = createPaymentService.create(account, requestPayload, null);
        PaymentWithAllLinks paymentResponse = createdPaymentWithAllLinks.getPayment();

        assertThat(paymentResponse.getPaymentId(), is("ch_ab2341da231434l"));
        assertThat(paymentResponse.getAmount(), is(100L));
        // …
    }
```

- **Qué copiar:** un contrato por escenario («con campos mínimos», «con todos los campos»), guardado
  como JSON y versionado; la prueba usa el cliente real contra el simulador.
- **Qué no copiar:** JUnit 4 (`@Rule`, `@RunWith`). Con Pact JVM 4.x y JUnit 5 se usan
  `@ExtendWith(PactConsumerTestExt.class)` y `@PactTestFor`.

### 9.10 Contrato del proveedor con Spring Cloud Contract y el puerto como lambda

- **Muestra:** el contrato (aquí en YAML) describe la petición y la respuesta; el plugin genera las
  pruebas del proveedor, que heredan de `BeerRestBase`. La base levanta el controlador **en modo
  standalone, con el puerto implementado por una lambda**: el adaptador primario se prueba sin el núcleo.
- **Caso de uso real:** la muestra oficial de Spring Cloud Contract. Un bar **comprueba si una persona
  tiene edad para que le sirvan una cerveza**: `POST /check` con la edad, y la respuesta `OK` o
  `NOT_OK`.
- **Repositorio:** `spring-cloud-samples/spring-cloud-contract-samples` · licencia Apache-2.0.
- **Archivos:**
  [`shouldRejectABeerIfTooYoung.yml`](https://github.com/spring-cloud-samples/spring-cloud-contract-samples/blob/0df8bfb616fa0bc3ff667dd2c3c1ea6a528b71be/producer_yaml/src/test/resources/contracts/beer/rest/shouldRejectABeerIfTooYoung.yml#L12-L29)
  y [`BeerRestBase.java`](https://github.com/spring-cloud-samples/spring-cloud-contract-samples/blob/0df8bfb616fa0bc3ff667dd2c3c1ea6a528b71be/producer_yaml/src/test/java/com/example/BeerRestBase.java#L12-L31)

```yaml
request:
  method: POST
  url: /check
  body:
    age: 10
  headers:
    Content-Type: application/json
  matchers:
    body:
      - path: "$.age"
        type: by_regex
        value: "[0-1][0-9]"
response:
  status: 200
  body: |
    {
      "status": "NOT_OK"
    }
```

```java
public abstract class BeerRestBase {
    ProducerController producerController = new ProducerController(oldEnough());
    StatsController statsController = new StatsController(statsService());

    @BeforeEach
    public void setup() {
        // …
        RestAssuredMockMvc.standaloneSetup(this.producerController, this.statsController);
    }

    private PersonCheckingService oldEnough() {
        return argument -> argument.age >= 20;
    }
    // …
}
```

- **Qué copiar:** un contrato por caso, con el dado/cuando/entonces en la descripción; el puerto
  sustituido por una implementación de prueba; las pruebas del proveedor generadas desde el contrato, y
  los *stubs* que el proveedor publica para que los consumidores prueben con ellos.
- **Qué no copiar:** responder `200` con `NOT_OK` a un rechazo de negocio; con el catálogo de la P16
  sería `422` con su `type`. [opinión] Para cumplir «contratos en JSON», escribe el contrato en JSON
  (Pact) o trata el OpenAPI en JSON como fuente y el contrato como derivado.

### 9.11 Request-Reply asíncrono: `202 Accepted` + `Location` hacia el trabajo

- **Muestra:** las operaciones que cambian el esquema se ejecutan como un trabajo en segundo plano. El
  endpoint responde `202` con `Location` hacia el recurso del trabajo (`JobExecution`), que el cliente
  consulta (P6-2, EP-10).
- **Caso de uso real:** MOLGENIS es una plataforma de datos para biobancos e investigación biomédica.
  Aquí, **agregar o cambiar un atributo de un tipo de entidad** (una columna de un conjunto de datos)
  puede tardar, porque reescribe los datos existentes.
- **Repositorio:** `molgenis/molgenis` · licencia LGPL-3.0.
- **Archivo:** [`MetadataApiController.java`](https://github.com/molgenis/molgenis/blob/b2b0016b22dcddca3d1666a1b186a4404535897d/molgenis-api-metadata/src/main/java/org/molgenis/api/metadata/v3/MetadataApiController.java#L132-L263)

```java
  @Transactional
  @PostMapping("/{entityTypeId}/attributes")
  public ResponseEntity<Void> createAttribute(
      @PathVariable("entityTypeId") String entityTypeId,
      @Valid @RequestBody CreateAttributeRequest createAttributeRequest) {
    EntityType entityType = metadataApiService.findEntityType(entityTypeId);
    Attribute attribute = attributeRequestMapper.toAttribute(createAttributeRequest, entityType);
    entityType.addAttribute(attribute);

    JobExecution jobExecution = metadataApiService.updateEntityTypeAsync(entityType);
    return toLocationResponse(jobExecution);
  }
  // …
  private ResponseEntity<Void> toLocationResponse(JobExecution jobExecution) {
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequestUri()
            .replacePath(EntityController.API_ENTITY_PATH)
            .pathSegment(jobExecution.getEntityType().getId(), jobExecution.getIdentifier())
            .build()
            .toUri();
    return ResponseEntity.accepted().location(location).build();
  }
```

- **Qué copiar:** un único método que arma el `202` + `Location` para todas las operaciones largas; el
  `Location` construido desde la petición actual.
- **Qué no copiar:** `@Transactional` en el controlador y la mutación del modelo (`entityType.addAttribute`)
  en el adaptador (EP-03). Falta `Retry-After`, y el estado terminal del trabajo no está en el contrato
  (EP-21).

## 10 · Plantillas de código

Esqueletos en Java 21 y Spring Boot 3.x o 4.x. Cada `TODO Pn` lo resuelve la pregunta n. Paquetes de
ejemplo: `com.ejemplo.pedidos.adapter.in.web` y `com.ejemplo.pedidos.application.port.in`. Con Spring
Boot 4 (Jackson 3), las anotaciones siguen en `com.fasterxml.jackson.annotation`; la vinculación está en
`tools.jackson.databind`.

**T1 · Contrato en JSON: ejemplo de petición, respuesta y error** (decisión del usuario; RFC 9457;
Zalando #118 para `snake_case`)

```json
{
  "peticion": {
    "metodo": "POST", "ruta": "/v1/pedidos",
    "cabeceras": { "Authorization": "Bearer …", "Idempotency-Key": "8e03978e-40d5-43e8-bc93-6894a57f9324",
                   "Content-Type": "application/json" },
    "cuerpo": { "cliente_id": "c-9", "lineas": [ { "sku": "ROSA-R", "cantidad": 24 } ], "entrega_el": "2026-11-02" }
  },
  "exito": {
    "status": 201, "cabeceras": { "Location": "/v1/pedidos/0192f6c4-7d3e-7b1a-9c2e-5f1d2a3b4c5d" },
    "cuerpo": { "id": "0192f6c4-7d3e-7b1a-9c2e-5f1d2a3b4c5d", "estado": "CONFIRMADO",
                "total": { "monto": "48000", "moneda": "COP" } }
  },
  "errores": [
    { "status": 422, "cuerpo": { "type": "https://api.tienda.example/problemas/stock-insuficiente",
      "title": "Stock insuficiente", "status": 422, "detail": "ROSA-R: faltan 4",
      "instance": "/v1/pedidos", "sku": "ROSA-R", "faltante": 4, "traceId": "4bf92f35…" } }
  ]
}
```

**T2 · Esquema JSON de la petición** (JSON Schema 2020-12, el que usa OpenAPI 3.1)

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://api.tienda.example/esquemas/v1/registrar-pedido.json",
  "type": "object",
  "required": ["cliente_id", "lineas"],
  "properties": {
    "cliente_id": { "type": "string", "minLength": 1, "maxLength": 64 },
    "lineas": { "type": "array", "minItems": 1, "maxItems": 100,
      "items": { "type": "object", "required": ["sku", "cantidad"],
        "properties": { "sku": { "type": "string", "pattern": "^[A-Z0-9-]{3,32}$" },
                        "cantidad": { "type": "integer", "minimum": 1 } } } },
    "entrega_el": { "type": "string", "format": "date" }
  }
}
```

`TODO P14`: campos y obligatoriedad. `TODO P9`: `maxItems`. `TODO P19`: [opinión] no pongas
`additionalProperties: false` en los esquemas de respuesta, para que los clientes que validan toleren
campos nuevos (Fowler, *TolerantReader*).

**T3 · DTO web** (basado en 9.3 y 9.4, con `record` en lugar de `@Data`)

```java
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)          // TODO P14: snake_case o camelCase
record PedidoRequest(
    @NotBlank @Size(max = 64) String clienteId,
    @NotEmpty @Size(max = 100) List<@Valid LineaRequest> lineas,        // TODO P9: límite de ítems
    LocalDate entregaEl) {

  record LineaRequest(@NotBlank @Pattern(regexp = "^[A-Z0-9-]{3,32}$") String sku,
                      @Positive int cantidad) {}
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
record PedidoResponse(String id, String estado, DineroJson total) {     // TODO P15
  record DineroJson(String monto, String moneda) {}                     // dinero como objeto (Zalando #173)
}
```

**T4 · Comando y puerto de entrada** (basado en 9.1; Hombergs, *input model*)

```java
public record RegistrarPedidoComando(ClienteId cliente, List<LineaSolicitada> lineas,
                                     Optional<LocalDate> entregaEl, ClaveIdempotencia clave) {
  public RegistrarPedidoComando {                                       // se autovalida (EP-04)
    Objects.requireNonNull(cliente, "cliente");
    Objects.requireNonNull(clave, "clave");                             // TODO P17: ¿obligatoria?
    if (lineas == null || lineas.isEmpty()) throw new LineasVacias();
    lineas = List.copyOf(lineas);
  }
}
public record ClaveIdempotencia(String valor) {
  private static final Pattern FORMA = Pattern.compile("^[A-Za-z0-9-]{1,255}$");  // Stripe ≤ 255; GOV.UK Pay
  public ClaveIdempotencia {
    if (valor == null || !FORMA.matcher(valor).matches()) throw new ClaveIdempotenciaInvalida();
  }
}

public interface RegistrarPedido {                                      // puerto de entrada (nombre de intención)
  PedidoRegistrado ejecutar(RegistrarPedidoComando comando);            // TODO P18: postcondición documentada
}
public record PedidoRegistrado(PedidoId id, EstadoPedido estado, Dinero total, boolean yaExistia) {}
// yaExistia permite al adaptador decidir la respuesta del reintento (TODO P5), como WhenCreated en 9.7
```

**T5 · Mapper web** (mapeo *Full*, Hombergs)

```java
@Component
class PedidoWebMapper {
  RegistrarPedidoComando aComando(PedidoRequest r, String idempotencyKey) {
    var lineas = r.lineas().stream()
        .map(l -> new LineaSolicitada(new Sku(l.sku()), new Cantidad(l.cantidad()))).toList();
    return new RegistrarPedidoComando(new ClienteId(r.clienteId()), lineas,
        Optional.ofNullable(r.entregaEl()), new ClaveIdempotencia(idempotencyKey));
  }
  PedidoResponse aRespuesta(PedidoRegistrado p) {
    return new PedidoResponse(p.id().valor().toString(), p.estado().name(),
        new PedidoResponse.DineroJson(p.total().monto().toPlainString(), p.total().moneda().getCurrencyCode()));
  }
}
```

**T6 · Controlador síncrono con `201` + `Location`** (basado en 9.1, 9.3 y 9.7)

```java
@RestController
@RequestMapping("/v1/pedidos")                                          // TODO P12: ruta y versión
class RegistrarPedidoController {                                       // uno por caso de uso
  private final RegistrarPedido registrarPedido;                        // solo el puerto (EP-01)
  private final PedidoWebMapper mapper;

  RegistrarPedidoController(RegistrarPedido registrarPedido, PedidoWebMapper mapper) {
    this.registrarPedido = registrarPedido;
    this.mapper = mapper;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAuthority('SCOPE_pedidos:escribir')")              // TODO P13
  ResponseEntity<PedidoResponse> registrar(@Valid @RequestBody PedidoRequest peticion,
                                           @RequestHeader("Idempotency-Key") String clave) { // TODO P17
    var resultado = registrarPedido.ejecutar(mapper.aComando(peticion, clave));
    var uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
        .path("/{id}").buildAndExpand(resultado.id().valor()).toUri();
    return ResponseEntity.created(uri)                                  // TODO P5: el mismo 201 al reintentar
        .cacheControl(CacheControl.noStore())                           // TODO P15: solo si hay datos sensibles
        .body(mapper.aRespuesta(resultado));
  }
}
```

Sin la cabecera, Spring responde 400 por `MissingRequestHeaderException`, que ya sale como
`ProblemDetail` si están activados (T8). El borrador IETF pide 400 en ese caso (§2.7).

**T7 · Variante asíncrona: `202` + operación** (basado en 9.11; Azure *LRO*)

```java
@PostMapping("/v1/lotes")
ResponseEntity<OperacionResponse> recibir(@Valid @RequestBody LoteRequest lote,
                                          @RequestHeader("Idempotency-Key") String clave) {
  OperacionId op = recibirLote.ejecutar(mapper.aComando(lote, clave));  // el puerto solo registra y encola
  URI uri = URI.create("/v1/operaciones/" + op.valor());
  return ResponseEntity.accepted().location(uri)
      .header(HttpHeaders.RETRY_AFTER, "5")                              // TODO P10
      .body(new OperacionResponse(op.valor().toString(), "NotStarted"));
}

@GetMapping("/v1/operaciones/{id}")                                     // pendiente: su propia ronda
OperacionResponse estado(@PathVariable UUID id) {
  return mapper.aRespuesta(consultarOperacion.ejecutar(new OperacionId(id)));
}

@JsonInclude(JsonInclude.Include.NON_NULL)
record OperacionResponse(String id, String status, Object resultado, ProblemDetail error) {
  OperacionResponse(String id, String status) { this(id, status, null, null); }
}  // status: NotStarted | Running | Succeeded | Failed | Canceled (enum extensible)
```

**T8 · Traductor de errores con `ProblemDetail`** (basado en 9.5 y 9.6; RFC 9457)

```java
@RestControllerAdvice(basePackageClasses = RegistrarPedidoController.class)   // uno por módulo (EP-08)
class PedidosProblemas {
  private static final String BASE = "https://api.tienda.example/problemas/"; // TODO P16

  @ExceptionHandler(StockInsuficiente.class)                            // excepción de dominio con nombre
  ProblemDetail stock(StockInsuficiente e) {
    var p = problema(HttpStatus.UNPROCESSABLE_ENTITY, "stock-insuficiente", "Stock insuficiente",
        "%s: faltan %d".formatted(e.sku().valor(), e.faltante()));
    p.setProperty("sku", e.sku().valor());                               // extensión legible por máquina
    p.setProperty("faltante", e.faltante());
    return p;
  }

  @ExceptionHandler(ClaveIdempotenciaReutilizada.class)                 // misma clave, otro cuerpo
  ProblemDetail claveReutilizada(ClaveIdempotenciaReutilizada e) {
    return problema(HttpStatus.UNPROCESSABLE_ENTITY, "idempotency-key-reutilizada",
        "Idempotency-Key is already used", "La clave ya se usó con otro cuerpo.");   // borrador IETF §2.7
  }

  @ExceptionHandler(PeticionEnCurso.class)                              // misma clave, aún en proceso
  ProblemDetail enCurso(PeticionEnCurso e) {
    return problema(HttpStatus.CONFLICT, "peticion-en-curso",
        "A request is outstanding for this Idempotency-Key", "Reintenta más tarde.");
  }

  private ProblemDetail problema(HttpStatus status, String tipo, String titulo, String detalle) {
    var p = ProblemDetail.forStatusAndDetail(status, detalle);
    p.setType(URI.create(BASE + tipo));
    p.setTitle(titulo);
    return p;                                                            // traceId: como setTraceId en 9.5 (EP-19)
  }
}
```

Los errores de forma (`MethodArgumentNotValidException`, `MissingRequestHeaderException`) y los genéricos
de Spring MVC los cubre el propio Spring con `spring.mvc.problemdetails.enabled=true`, o extendiendo
`ResponseEntityExceptionHandler`, y ya salen como `ProblemDetail`. Para `Exception`, usa un `detail` sin
el mensaje interno (EP-07). Las versiones recientes de Spring agregan la constante
`HttpStatus.UNPROCESSABLE_CONTENT` (el nombre de RFC 9110); usa la que exista en tu versión.

**T9 · Versión por cabecera con Spring Framework 7** (solo si P12 = 3; documentación de Spring,
*API Versioning*)

```java
@Configuration
class VersionadoWeb implements WebMvcConfigurer {
  @Override
  public void configureApiVersioning(ApiVersionConfigurer configurer) {
    configurer.useRequestHeader("API-Version");                         // TODO P12
  }
}

@GetMapping(path = "/pedidos/{id}", version = "1.2+")                   // la 1.2 y las soportadas superiores
PedidoResponseV12 consultar(@PathVariable UUID id) { /* … */ }
```

Una versión ausente o no soportada responde 400. El `StandardApiVersionDeprecationHandler` puede emitir
`Deprecation`, `Sunset` y `Link` (RFC 9745 y RFC 8594). Con la versión en la ruta (P12 = 1) no hace falta
nada de esto.

**T10 · Pruebas del adaptador y reglas de arquitectura** (basado en 9.2 y 9.10)

```java
@WebMvcTest(RegistrarPedidoController.class)
@Import(PedidoWebMapper.class)                                          // el mapper real
class RegistrarPedidoControllerTest {
  @Autowired MockMvc mvc;
  @MockitoBean RegistrarPedido registrarPedido;                          // el puerto, simulado (EP-20)

  @Test void stockInsuficienteSaleComoProblemDetail() throws Exception {  // dado/cuando/entonces de la P7
    given(registrarPedido.ejecutar(any())).willThrow(new StockInsuficiente(new Sku("ROSA-R"), 4));
    mvc.perform(post("/v1/pedidos").header("Idempotency-Key", "k-1")
            .contentType(MediaType.APPLICATION_JSON).content("""
              {"cliente_id":"c-9","lineas":[{"sku":"ROSA-R","cantidad":24}]}"""))
        .andExpect(status().is(422))
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.type").value("https://api.tienda.example/problemas/stock-insuficiente"));
  }
  // TODO P7: una prueba por cada error del catálogo · TODO P5: misma clave → misma respuesta
  // TODO P13: 401 sin token y 403 sin scope (con spring-security-test)
}

@AnalyzeClasses(packages = "com.ejemplo.pedidos")
class ReglasDelAdaptadorWebTest {
  @ArchTest static final ArchRule soloPuertos = classes().that().resideInAPackage("..adapter.in.web..")
      .should().onlyDependOnClassesThat().resideOutsideOfPackages("..adapter.out..", "..application.service.."); // EP-01
  @ArchTest static final ArchRule nucleoSinWeb = noClasses().that().resideInAPackage("..application..")
      .should().dependOnClassesThat().resideInAPackage("..adapter.."); // EP-02
  @ArchTest static final ArchRule sinTransaccionEnWeb = noMethods().that().areDeclaredInClassesThat()
      .resideInAPackage("..adapter.in.web..").should().beAnnotatedWith(Transactional.class); // EP-03
}
```

En Spring Boot 4, `@WebMvcTest` cambió de paquete (va en un módulo de prueba propio de Web MVC);
comprueba el import con la versión del proyecto.

## 11 · Qué se genera

Orden dentro del segmento (de afuera hacia adentro, con el contrato primero; `investigacion/05` §2.2):

1. Contrato en JSON: ejemplos (T1), esquema de la petición (T2) y, si el proyecto lo usa, el fragmento
   OpenAPI en JSON con todas las respuestas, como en 9.7.
2. Puerto de entrada, comando, clave de idempotencia y resultado (T4), con la prueba de construcción
   inválida del comando.
3. DTO web (T3) y mapper (T5).
4. Controlador (T6, o T7 si P6 = 2).
5. Traductor de errores (T8), con una entrada por cada `type` del catálogo.
6. Configuración de versión (T9), solo si P12 = 3, y las `@ConfigurationProperties` de los límites
   (P11).
7. Pruebas: `@WebMvcTest` con un caso por fallo de la P7 y por cada respuesta de la política de
   idempotencia; reglas ArchUnit (T10); prueba de contrato (Pact o Spring Cloud Contract, 9.9 y 9.10) si
   hay consumidores conocidos.

Sale hacia `caso-de-uso`: la firma del puerto de entrada, el comando, la clave de idempotencia con su
política (ventana, huella y respuestas), la postcondición de la P18 y el catálogo de excepciones de
dominio que el traductor espera.
Dependencias: `spring-boot-starter-web`, `spring-boot-starter-validation`, `spring-boot-starter-security`
(si P13 = 1), JUnit, AssertJ, ArchUnit y, opcionalmente, Pact JVM o Spring Cloud Contract.

## 12 · Referencias

- Cockburn, *Hexagonal Architecture* (2005): puertos primarios (*driving*), adaptadores y adaptadores de
  prueba. https://alistair.cockburn.us/hexagonal-architecture/
- Hombergs, *Get Your Hands Dirty on Clean Architecture* (2.ª ed., 2023): las siete tareas del adaptador
  web, *Slicing Controllers*, el modelo de entrada por caso de uso, la validación de entrada frente a las
  reglas de negocio, las pruebas por elemento de la arquitectura y las cuatro estrategias de mapeo.
  https://leanpub.com/get-your-hands-dirty-on-clean-architecture
- Martin, *The Clean Architecture* (2012): regla de dependencias y estructuras de datos simples en las
  fronteras. https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
- Lauret, *The Design of Web APIs* (Manning, 2019): diseño desde la perspectiva del consumidor, el *API
  goals canvas* y errores informativos que no exponen la implementación. No se leyó el libro; se cita
  por su temario. https://www.manning.com/books/the-design-of-web-apis
- Geewax, *API Design Patterns* (Manning, 2021): métodos estándar y personalizados, operaciones largas,
  paginación, validación de peticiones, deduplicación de peticiones, versionado y compatibilidad. No se
  leyó el libro; se cita por su temario. https://www.manning.com/books/api-design-patterns
- Google AIP: 121 (recursos), 136 (métodos personalizados), 151 (operaciones largas), 155 (`request_id`),
  158 (paginación), 180 (compatibilidad), 185 (versionado), 193 (errores), 203 (comportamiento de campos).
  https://google.aip.dev/
- Zalando RESTful API and Event Guidelines: #100-#101 (API first y OpenAPI), #106-#108 (compatibilidad),
  #112 (enums abiertos), #114-#115 (versionado por media type, no por URL), #118 (`snake_case`), #134 y
  #141 (nombres), #152 (207), #153 (429), #160 (cursor), #169 y #173 (formatos), #176 (Problem JSON),
  #182 (ETag), #230-#231 (idempotencia), #233 (`X-Flow-ID`), #253 (asíncrono).
  https://opensource.zalando.com/restful-api-guidelines/
- Microsoft Azure REST API Guidelines: errores (`x-ms-error-code`), LRO (202, `operation-location`,
  `retry-after` y estados), repetibilidad (`Repeatability-Request-ID`, ventana ≥ 5 min), `api-version`,
  enums extensibles, `nextLink` y ETag. https://github.com/microsoft/api-guidelines/blob/vNext/azure/Guidelines.md
- IETF RFC 9110, *HTTP Semantics*: §9.2 (métodos seguros e idempotentes), §15.3.2-15.3.5 (201, 202, 204),
  §15.5 (4xx), §10.2.3 (`Retry-After`), §8.8.3 y §13.1.1 (ETag e `If-Match`).
  https://www.rfc-editor.org/rfc/rfc9110.html
- IETF RFC 9457, *Problem Details for HTTP APIs* (reemplaza al RFC 7807): miembros, `about:blank`,
  extensiones, no parsear `detail`, cuándo no usarlo y seguridad. https://www.rfc-editor.org/rfc/rfc9457.html
- IETF RFC 6585 §4 (429 Too Many Requests); RFC 9745 (`Deprecation`); RFC 8594 (`Sunset`); RFC 9111
  §5.2.2.5 (`no-store`).
- IETF, *The Idempotency-Key HTTP Header Field*, draft-ietf-httpapi-idempotency-key-header-07 (oct-2025,
  hoy expirado): unicidad, caducidad, huella y los errores 400, 422 y 409 (§2.7), con la lista de
  implementaciones. https://datatracker.ietf.org/doc/draft-ietf-httpapi-idempotency-key-header/
- Stripe, *Idempotent requests*: guarda el código y el cuerpo de la primera respuesta, incluidos los 500;
  claves de hasta 255 caracteres; poda a partir de 24 h; compara parámetros; solo en POST.
  https://docs.stripe.com/api/idempotent_requests
- OpenAPI Specification 3.1, alineada con JSON Schema 2020-12. https://spec.openapis.org/oas/v3.1.0
- Fowler, *TolerantReader* y *Data Transfer Object* (PoEAA). https://martinfowler.com/bliki/TolerantReader.html
- King, *Parse, don't validate* (2019). https://lexi-lambda.github.io/blog/2019/11/05/parse-don-t-validate/
- Spring Framework, *Error Responses*: `ProblemDetail`, `ErrorResponse` y `ResponseEntityExceptionHandler`.
  https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html
- Spring Framework 7, *API Versioning*: `ApiVersionConfigurer`, el atributo `version`, el 400 ante una
  versión no soportada y las cabeceras de obsolescencia.
  https://docs.spring.io/spring-framework/reference/web/webmvc-versioning.html
- Pact, *Consumer-driven contract testing*, y Pact JVM. https://docs.pact.io/
- Spring Cloud Contract: contratos del proveedor y *stubs*. https://spring.io/projects/spring-cloud-contract
- W3C Trace Context (`traceparent`). https://www.w3.org/TR/trace-context/
