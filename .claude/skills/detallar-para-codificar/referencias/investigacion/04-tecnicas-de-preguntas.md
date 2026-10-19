# 04 · Técnicas de preguntas para especificar software

> Frente de investigación de la skill **DetallarParaCodificar** (estilo 20Q/Akinator).
> Se buscó cómo preguntar para especificar software, cómo elegir la siguiente pregunta y qué preguntar según el tipo de elemento.
> Convención: **★** marca la opción recomendada (va primero). **[opinión]** marca lo que es interpretación mía y no viene de una fuente.

---

## 1. Cómo elegir la siguiente pregunta

### 1.1 De dónde salen las reglas

- **20 preguntas y ganancia de información.** Cada respuesta sí/no aporta como máximo 1 bit. Con 20 preguntas que parten el espacio por la mitad se distinguen unos 2^20 ≈ 1 millón de candidatos. La pregunta ideal es la que más reduce la entropía de lo que aún no se sabe, es decir, la que maximiza la ganancia de información esperada: `IG = H(antes) − E[H(después)]`. El trabajo *Uncertainty of Thoughts* (Hu et al., NeurIPS 2024) aplica esto a un LLM. Simula respuestas posibles, premia la ganancia de información y elige la pregunta con más recompensa esperada. Así sube la tasa de éxito y bajan las preguntas necesarias, también en el juego de 20 preguntas.
- **Embudo de preguntas.** Se empieza con preguntas abiertas (qué, quién, cuándo, cómo), se sigue con preguntas de sondeo que se apoyan en la respuesta anterior y se cierra con una pregunta cerrada que resume y confirma. Si la primera pregunta no es lo bastante amplia, el embudo no funciona.
- **Preguntas libres de contexto** (Gause y Weinberg, *Exploring Requirements*, 1989; las recoge la guía de entrevistas de RUP). Son preguntas que sirven en cualquier proyecto y que no presuponen una solución. Se agrupan en actores, proceso o problema, producto y metapreguntas.
- **Cuestionamiento socrático** (Paul y Elder). Distingue seis tipos de pregunta: aclaración, supuestos, razones y evidencia, puntos de vista, implicaciones y consecuencias, y preguntas sobre la propia pregunta.
- **Example Mapping** (Matt Wynne). Usa tarjetas de cuatro colores: historia, reglas, ejemplos y preguntas abiertas (rojas). La sesión dura unos 25 minutos. Si salen muchas tarjetas rojas, la historia no está lista. Si salen muchas reglas, conviene partirla.
- **Three Amigos** (George Dinwiddie). Reúne tres miradas que no se solapan. Negocio pregunta por el objetivo, quien programa por el cómo y quien prueba por lo que puede salir mal.

### 1.2 Reglas operativas para el agente

1. **Mantén un espacio de hipótesis explícito.** Por cada elemento, guarda una lista de variables abiertas, cada una con sus valores candidatos. Ejemplo: `idempotencia ∈ {clave de cabecera, clave natural, no aplica}`. La siguiente pregunta siempre resuelve una de esas variables. [opinión]
2. **Prioriza por ganancia × impacto.** Puntúa cada variable abierta como `incertidumbre (0–1) × impacto si se adivina mal (1–3) × número de variables que dependen de ella`. Pregunta primero por la de mayor puntuación. [opinión, adaptación de la ganancia de información esperada]
3. **Pregunta antes lo que poda ramas.** Una respuesta como "no publica eventos" elimina de golpe el bloque entero de mensajería. Las preguntas que habilitan o descartan subárboles van antes que las de detalle. Es la lógica de 20Q.
4. **Haz el embudo por elemento.** El orden es identidad y propósito, luego contrato, luego reglas, luego errores y bordes, y al final calidades no funcionales. No bajes a detalle mientras el nivel de arriba tenga variables de alto impacto sin resolver.
5. **Ofrece opciones que se repartan la probabilidad.** Un selector con una opción casi segura y otras improbables aporta poco. Elige 2–4 opciones plausibles y mutuamente excluyentes; la opción libre cubre el resto. [opinión basada en IG]
6. **La opción recomendada va primero y debe ser la práctica de referencia,** salvo que el contexto del proyecto diga otra cosa. Ejemplos: Problem Details para errores, Idempotency-Key en POST con efectos y un consumidor tolerante para eventos.
7. **No preguntes lo que puedes deducir.** Si el código, los ADR o la documentación ya fijan un valor, regístralo como *supuesto verificado*, cita la fuente y no gastes una pregunta. Equivale a no preguntar lo que ya se sabe en 20Q.
8. **Confirma lo que es barato confirmar.** Si un valor deducido tiene alto impacto y confianza menor de 0,8, haz una pregunta cerrada de confirmación ("¿Asumo X?"). Esa pregunta cuenta como pregunta. [opinión]
9. **Una variable por pregunta.** Nada de preguntas dobles ("¿pagina y ordena?"). Si salen dos, divídelas.
10. **Usa contraejemplos para cerrar reglas.** Cuando una regla parece clara, pregunta por un borde: "¿qué pasa si la cantidad es 0?". Es el uso de las tarjetas verdes de Example Mapping para hacer aflorar reglas ocultas.
11. **Rota las tres miradas.** Cada 5–6 preguntas, comprueba que hubo al menos una de negocio (qué y para qué), una de implementación (cómo) y una de pruebas (qué falla). [opinión, inspirada en Three Amigos]
12. **Aparca en vez de bloquear.** Si el usuario no sabe la respuesta, convierte la pregunta en un *pendiente* (tarjeta roja), asume la opción ★ como provisional y sigue.
13. **Detecta contradicciones enseguida.** Si una respuesta choca con otra anterior, la siguiente pregunta es la que deshace el conflicto. Las contradicciones pesan 25 en el índice, así que tienen prioridad máxima.
14. **Ve de lo libre de contexto a lo específico del tipo.** Las dos o tres primeras preguntas de una ronda establecen quién lo usa, qué problema resuelve y cómo se ve el éxito. Después viene el banco del tipo de elemento (sección 3).
15. **Limita la profundidad por ronda.** La ronda es de 10 preguntas (D-08): en la primera de una plantilla, 2 libres de contexto, 6–7 del banco y 1–2 de bordes; en la siguiente, todo del banco y de bordes. [opinión]
16. **Usa una pregunta socrática cuando una respuesta sea vaga.** Si el usuario dice "rápido" o "seguro", aclara con una pregunta del tipo "¿cuánto es rápido? p95 < 200 ms | < 1 s | sin requisito". Todo requisito necesita un criterio de ajuste medible (*fit criterion*, Volere).
17. **Corta cuando la ganancia marginal es baja.** Si las variables que quedan tienen impacto 1 o ya tienen ★ verificada, termina la ronda antes de llegar a 10 y calcula el índice. [opinión]
18. **Cierra con una metapregunta.** "¿Hay algo que debería haberte preguntado?" Viene de las metapreguntas de Gause y Weinberg. Cuesta una sola pregunta y suele destapar una rama olvidada.

---

## 2. Cómo escribir una pregunta rápida

### 2.1 Forma

- **Longitud:** título de una línea; debajo, un contexto pequeño (~50 palabras) o mediano (~120 palabras) como máximo, en segunda persona, en presente y sin jerga innecesaria.
- **Estructura:** `[contexto mínimo] + ¿[decisión concreta]?` Por ejemplo: "POST /pedidos: ¿cómo evitas pedidos duplicados por reintento?".
- **Opciones:** de 2 a 4, mutuamente excluyentes, con la ★ primero. Cada opción tiene una etiqueta corta (≤6 palabras) y un ejemplo en una línea. Siempre se puede responder libremente.
- **Ejemplo por opción:** debe ser concreto y ejecutable mentalmente, con un valor real, una cabecera real o un JSON mínimo. Nada de "depende".
- **Evita:** el "¿por qué?" a secas, que pone a la defensiva (guía RUP), las preguntas dobles, las opciones solapadas y las negaciones dobles.
- **Indica el coste de equivocarse** cuando sea alto. Por ejemplo: "(cambiarlo luego rompe clientes)". [opinión]

### 2.2 Plantilla de pregunta

```
P{n} · {elemento} · {variable}            [impacto: alto|medio|bajo]
¿{título: la pregunta en una línea}?
{contexto pequeño o mediano: qué se sabe, qué está en juego y por qué se pregunta}
  1. ★ {opción recomendada} — ej: {ejemplo de una línea}
  2. {alternativa} — ej: {ejemplo}
  3. {alternativa} — ej: {ejemplo}
  (o responde libre)
```

### 2.3 Plantilla de ejemplo con elementos prioritarios

Antes de la primera pregunta sobre un elemento, el agente muestra un **ejemplo esqueleto** del elemento. En él, los elementos prioritarios aparecen como huecos numerados por orden de importancia, y cada pregunta rellena un hueco. Así el usuario ve qué falta y por qué se le pregunta. [opinión, inspirada en las tarjetas de Example Mapping y en la plantilla Volere]

```
Endpoint: ⟦1 método+ruta⟧            → POST /pedidos
Propósito: ⟦2 qué logra el cliente⟧  → registrar un pedido de flores
Entrada: ⟦3 campos obligatorios⟧     → {cliente_id, lineas[]}
Éxito: ⟦4 código + cuerpo⟧           → 201 + Location
Errores: ⟦5 casos⟧                   → 422 stock insuficiente (problem+json)
Reintento: ⟦6 idempotencia⟧          → Idempotency-Key
Lo demás: ⟦7..n⟧ paginación, versión, auth, límites
```

Reglas de la plantilla:
- Los huecos ⟦k⟧ siguen el orden de los **elementos prioritarios** del tipo (sección 3).
- El valor de la derecha es la opción ★ provisional. Si el usuario acepta el esqueleto entero, cada ★ pasa a ser un *supuesto aceptado*, con menos peso que una respuesta explícita (sección 5).
- Un hueco que queda sin responder al final de la ronda pasa a ser un *pendiente*.

---

## 3. Bancos de preguntas por tipo de elemento

Formato de cada pregunta: **pregunta**, luego las opciones (★ primero) y luego `ej:` con el mini ejemplo de la opción ★. Las preguntas van de general a detallado.

### 3.1 Endpoint HTTP

**Elementos prioritarios:** 1 recurso y método · 2 propósito y actor · 3 contrato de entrada · 4 respuesta de éxito · 5 errores · 6 idempotencia y reintentos · 7 autorización · 8 concurrencia (ETag) · 9 paginación y filtrado · 10 versionado y compatibilidad · 11 límites y operaciones largas.

1. **¿Qué recurso expone y con qué método?** ★ sustantivo plural + método estándar · acción personalizada `:verbo` · RPC.
   ej: `POST /pedidos` (crear) · `GET /pedidos/{id}` (leer). Las URL no llevan verbos y los recursos van en plural (Zalando, Google AIP-121).
2. **¿Quién lo llama y para qué?** ★ otra app interna · cliente externo · usuario final vía front.
   ej: "El front de ventas registra un pedido confirmado por el cliente."
3. **¿Qué campos son obligatorios en la entrada?** ★ mínimos de negocio · todo el recurso · sin cuerpo.
   ej: `{"cliente_id":"c-9","lineas":[{"sku":"ROSA-R","cant":24}]}`
4. **¿Qué devuelve si sale bien?** ★ 201 + Location + recurso · 202 + seguimiento · 204 sin cuerpo.
   ej: `201 Created` · `Location: /pedidos/p-77` · cuerpo con `id` y `estado`.
5. **¿Qué formato tienen los errores?** ★ RFC 9457 `application/problem+json` · formato propio del dominio · solo el código HTTP.
   ej: `{"type":"/problemas/stock","title":"Stock insuficiente","status":422,"detail":"ROSA-R: faltan 4"}`
6. **¿Qué errores de negocio distingue el cliente?** ★ lista cerrada con `type` documentado · genérico 400/422 · ninguno.
   ej: `stock-insuficiente` (422), `cliente-bloqueado` (403), `pedido-duplicado` (409).
7. **Si el cliente reintenta, ¿cómo evitas duplicar?** ★ cabecera `Idempotency-Key` · clave natural en el cuerpo · no aplica (GET/PUT).
   ej: `Idempotency-Key: 8e03...`. Mismo cuerpo: devuelve la respuesta guardada. Cuerpo distinto: 422. En curso: 409 (borrador IETF).
8. **¿Cuánto tiempo recuerdas una clave de idempotencia?** ★ 24 h · 5 min · para siempre (clave de negocio).
   ej: "Las claves caducan a las 24 h y la caducidad está documentada en el contrato."
9. **¿Quién puede invocarlo?** ★ rol o *scope* específico · cualquier autenticado · público.
   ej: `scope: pedidos:escribir`. Sin token: 401. Sin permiso: 403.
10. **¿Dos escrituras simultáneas al mismo recurso?** ★ ETag + `If-Match` (412 si cambió) · gana la última · bloqueo.
    ej: `PATCH /pedidos/p-77` con `If-Match: "v3"`. Si la versión actual es v4, responde `412 Precondition Failed`.
11. **¿La colección puede crecer mucho?** ★ paginación por cursor · por offset/página · no pagina (≤100 ítems fijos).
    ej: `GET /pedidos?cursor=abc&limit=50` responde `{"items":[...],"next":"/pedidos?cursor=def"}`.
12. **¿Cómo evoluciona sin romper clientes?** ★ solo cambios aditivos + clientes tolerantes · versión en media type · versión en la URL.
    ej: añadir el campo opcional `nota` no rompe nada. Renombrar `cant` sí rompe (Zalando #106, #108).
13. **¿Algún enum puede crecer?** ★ enum extensible documentado · enum cerrado.
    ej: `estado: CREADO|CONFIRMADO|...`. El cliente debe tolerar valores nuevos (Azure: extensible enums).
14. **¿Tarda más de unos segundos?** ★ no, síncrono · sí, 202 + recurso de operación · sí, *callback* o evento.
    ej: `202 Accepted` + `Operation-Location: /operaciones/o-5` + `Retry-After: 5`.
15. **¿Límite de uso por cliente?** ★ 429 + `Retry-After` · sin límite · lo gestiona el gateway.
    ej: "100 peticiones/min por cliente. Al superarlo, `429` con `Retry-After: 30`."

### 3.2 Evento o mensaje

**Elementos prioritarios:** 1 hecho de negocio y nombre · 2 productor y dueño · 3 consumidores · 4 categoría (cambio de datos o paso de proceso) · 5 sobre (metadatos) · 6 carga útil · 7 garantía de entrega y duplicados · 8 orden y clave de partición · 9 evolución del esquema · 10 fallos y DLQ · 11 retención y reproceso.

1. **¿Qué hecho de negocio anuncia?** ★ verbo en pasado sobre una entidad · comando (imperativo) · notificación técnica.
   ej: `pedido.confirmado` (hecho) frente a `confirmar-pedido` (comando: va a un solo destinatario y espera efecto).
2. **¿Quién es el dueño del esquema?** ★ el módulo productor · un equipo de plataforma · compartido.
   ej: "El dueño es `ventas`. El esquema vive en `ventas/eventos/pedido-confirmado.json`" (Zalando #207).
3. **¿Quién lo consume y qué hace con él?** ★ lista conocida de consumidores · desconocidos/abiertos · ninguno aún.
   ej: "`inventario` reserva stock y `facturacion` emite la proforma."
4. **¿Es cambio de datos o paso de proceso?** ★ paso de proceso (evento de negocio) · cambio de datos (CDC) · ambos.
   ej: `pedido.confirmado` frente a `pedido.actualizado {antes, despues}` (Zalando #201/#202).
5. **¿Qué metadatos lleva el sobre?** ★ CloudEvents (`id`,`source`,`type`,`specversion`,`time`) · sobre propio · ninguno.
   ej: `{"specversion":"1.0","id":"e-1","source":"/ventas","type":"pedido.confirmado","time":"..."}`
6. **¿Carga completa o solo referencia?** ★ datos que necesita el consumidor · solo el id (el consumidor consulta) · entidad completa.
   ej: `{"pedido_id":"p-77","cliente_id":"c-9","total":120000}`
7. **¿Qué pasa si llega duplicado?** ★ el consumidor deduplica por `id` de evento · al productor se le exige exactamente una vez · da igual.
   ej: tabla `eventos_procesados(id PK)` insertada en la misma transacción que el efecto. La clave es el *evento*, no la entidad.
8. **¿Cómo se publica junto al cambio en BD?** ★ outbox transaccional · publicar tras el commit (riesgo de pérdida) · Spring Modulith/registro de eventos.
   ej: `INSERT pedido` + `INSERT outbox` en una sola transacción. Un *relay* publica después y puede duplicar.
9. **¿Importa el orden?** ★ por clave (partición = `pedido_id`) · orden global · no importa.
   ej: "Los eventos del mismo pedido llegan en orden. Los de pedidos distintos, en cualquier orden."
10. **¿Cómo evoluciona el esquema?** ★ compatibilidad hacia atrás + campos nuevos opcionales · versión nueva del tipo · libre.
    ej: añadir `canal` con valor por defecto es compatible. Quitar `total` rompe (Confluent BACKWARD).
11. **¿Cómo lee el consumidor campos desconocidos?** ★ consumidor tolerante: ignora lo que no conoce · validación estricta.
    ej: "El consumidor solo enlaza `pedido_id` y `total`; los demás campos se ignoran."
12. **¿Qué hace el consumidor si falla al procesar?** ★ reintentos con backoff y luego DLQ + alerta · descartar y registrar · bloquear la cola.
    ej: "3 reintentos (1 s, 4 s, 16 s con *jitter*). Después va a `pedido.confirmado.dlq`."
13. **¿Se puede reprocesar el histórico?** ★ sí, con retención N días y consumidores idempotentes · no.
    ej: "Retención de 7 días. La ventana de deduplicación debe cubrir esos 7 días."
14. **¿Cómo se documenta el contrato?** ★ AsyncAPI (canal + operación + mensaje) · JSON Schema suelto · solo código.
    ej: `channels.pedidos.messages.PedidoConfirmado.payload: $ref ...`

### 3.3 Puerto o interfaz

**Elementos prioritarios:** 1 responsabilidad única · 2 dirección (entrada o salida) · 3 operaciones y firmas · 4 tipos del dominio en la firma · 5 errores · 6 síncrono o asíncrono · 7 transaccionalidad · 8 implementaciones previstas · 9 doble de pruebas.

1. **¿Qué capacidad abstrae, en una frase?** ★ una capacidad del dominio · un sistema externo concreto · varias cosas.
   ej: "`CatalogoDePrecios`: da el precio vigente de un SKU para un cliente."
2. **¿Es de entrada (lo llama el mundo) o de salida (lo llama el dominio)?** ★ salida · entrada.
   ej: salida: `NotificadorDeClientes`. Entrada: `RegistrarPedido` (caso de uso).
3. **¿Qué operaciones necesita como mínimo?** ★ 1–3 operaciones con nombre del dominio · CRUD genérico.
   ej: `Precio precioVigente(Sku, ClienteId)`
4. **¿Qué tipos cruzan la frontera?** ★ tipos del dominio (Value Objects) · DTO del proveedor · primitivos.
   ej: `Sku`, `Dinero` en lugar de `String`, `BigDecimal` o un `ProveedorPriceResponse`.
5. **¿Cómo comunica "no encontrado"?** ★ `Optional`/resultado vacío · excepción del dominio · null.
   ej: `Optional<Precio> precioVigente(...)`
6. **¿Cómo comunica fallos técnicos?** ★ excepción propia del puerto · se propaga la del proveedor · tipo resultado.
   ej: `CatalogoNoDisponible extends RuntimeException`. El dominio decide si reintenta.
7. **¿Síncrono o asíncrono?** ★ síncrono · `CompletableFuture`/reactivo · dispara y olvida.
   ej: "Llamada síncrona con timeout de 2 s, definido en el adaptador."
8. **¿Participa en la transacción del caso de uso?** ★ no (efecto externo tras el commit) · sí (mismo recurso transaccional).
   ej: "Notificar va después del commit, vía evento, nunca dentro de la transacción."
9. **¿Qué implementaciones habrá?** ★ una real + una falsa en memoria · varias reales (estrategia) · solo la real.
   ej: `CatalogoHttp` y `CatalogoEnMemoria` (para pruebas).
10. **¿Qué garantiza el contrato que toda implementación cumpla?** ★ prueba de contrato compartida · solo documentación.
    ej: `abstract class CatalogoDePreciosContractTest` heredada por ambas implementaciones.

### 3.4 Caso de uso

**Elementos prioritarios:** 1 actor y objetivo · 2 disparador · 3 precondiciones · 4 flujo principal · 5 reglas de negocio · 6 flujos alternativos y errores · 7 postcondiciones y efectos · 8 eventos emitidos · 9 transacción y consistencia · 10 autorización · 11 criterio medible.

1. **¿Quién lo ejecuta y qué logra?** ★ actor + objetivo de negocio · solo descripción técnica.
   ej: "El vendedor confirma un pedido para que se reserve el stock."
2. **¿Qué lo dispara?** ★ comando de usuario (HTTP) · evento de otro módulo · temporizador.
   ej: `POST /pedidos/{id}:confirmar`
3. **¿Qué debe ser cierto antes?** ★ lista de precondiciones verificables · ninguna.
   ej: "El pedido está en `CREADO` y tiene al menos una línea."
4. **¿Cuáles son los pasos del camino feliz?** ★ 3–7 pasos · un solo paso.
   ej: "1 cargar pedido · 2 validar stock · 3 reservar · 4 marcar `CONFIRMADO` · 5 emitir evento."
5. **¿Qué reglas de negocio aplica?** (una tarjeta azul por regla) ★ reglas enumeradas con ejemplo · "las de siempre".
   ej: "R1: no se confirma con stock < cantidad. R2: el cliente bloqueado no compra."
6. **Para cada regla, ¿un ejemplo que la cumpla y otro que la viole?** ★ par de ejemplos · solo el feliz.
   ej: "Stock 30, pide 24: confirma. Stock 20, pide 24: rechaza con `stock-insuficiente`."
7. **¿Qué pasa si falla a mitad?** ★ todo o nada (una transacción) · compensación (saga) · estado parcial visible.
   ej: "Si la reserva falla, el pedido sigue en `CREADO`; no queda nada a medias."
8. **¿Qué cambia en el sistema al terminar?** ★ postcondiciones + evento · solo el estado.
   ej: "Pedido en `CONFIRMADO`, stock reservado y `pedido.confirmado` publicado."
9. **¿Modifica uno o varios agregados?** ★ uno por transacción; los demás vía eventos · varios en la misma transacción.
   ej: "Se modifica `Pedido`; `Inventario` reacciona al evento" (Vernon, regla 1).
10. **¿Se puede ejecutar dos veces?** ★ idempotente (confirmar dos veces = un efecto) · la segunda vez falla · duplica.
    ej: "Confirmar un pedido ya `CONFIRMADO` devuelve OK sin repetir la reserva."
11. **¿Cómo sabrás que funciona en producción?** ★ criterio medible (*fit criterion*) · sin criterio.
    ej: "El 99 % de las confirmaciones termina en < 1 s; 0 reservas duplicadas por semana."

### 3.5 Entidad o agregado

**Elementos prioritarios:** 1 identidad · 2 límite (qué contiene) · 3 invariantes verdaderas · 4 ciclo de vida y estados · 5 comandos o métodos · 6 Value Objects · 7 referencias a otros agregados · 8 eventos de dominio · 9 concurrencia · 10 tamaño y crecimiento.

1. **¿Cómo se identifica?** ★ id generado (UUID/ULID) · clave natural de negocio · id compuesto.
   ej: `PedidoId("p-01HX...")`. La clave natural `numero_pedido` es un atributo único aparte.
2. **¿Qué invariantes deben cumplirse siempre, aunque sea en la misma transacción?** ★ lista corta de invariantes · ninguna.
   ej: "El total es la suma de las líneas; no hay líneas con cantidad ≤ 0."
3. **¿Qué contiene dentro del límite?** ★ raíz + lo mínimo para las invariantes · todo lo relacionado.
   ej: `Pedido` contiene `LineaDePedido`. `Cliente` queda fuera (Vernon: agregados pequeños).
4. **¿Cómo referencia a otros agregados?** ★ por id · por objeto (navegación directa).
   ej: `ClienteId clienteId` en lugar de `Cliente cliente`.
5. **¿Qué estados atraviesa?** ★ máquina de estados explícita · flags sueltos · sin estados.
   ej: `CREADO → CONFIRMADO → DESPACHADO`, `CREADO → CANCELADO`.
6. **¿Qué transiciones están prohibidas?** ★ lista de transiciones prohibidas con error · todas permitidas.
   ej: "`DESPACHADO → CANCELADO` lanza `TransicionInvalida`."
7. **¿Qué atributos son Value Objects?** ★ los que tienen reglas propias (dinero, cantidad, SKU) · ninguno.
   ej: `Dinero(120000, COP)` valida que no sea negativo y fija la moneda.
8. **¿Cómo se crea válido?** ★ fábrica o constructor que valida todo · constructor vacío + setters.
   ej: `Pedido.crear(clienteId, lineas)` lanza una excepción si `lineas` está vacía.
9. **¿Qué eventos de dominio registra?** ★ uno por transición relevante · ninguno.
   ej: `confirmar()` registra `PedidoConfirmado(pedidoId, total)`.
10. **¿Dos usuarios lo modifican a la vez?** ★ bloqueo optimista (versión) · pesimista · sin control.
    ej: `@Version long version`. Si hay conflicto, el caso de uso reintenta o responde 409.
11. **¿Cuánto puede crecer una colección interna?** ★ acotada (< 100) · sin límite → reconsiderar el límite del agregado.
    ej: "Máximo 50 líneas por pedido; por encima, error de negocio."

### 3.6 Adaptador de persistencia (repositorio)

**Elementos prioritarios:** 1 agregado que guarda · 2 operaciones del repositorio · 3 modelo de tablas y mapeo · 4 claves y unicidad · 5 transacciones · 6 concurrencia · 7 consultas y sus índices · 8 migraciones · 9 borrado · 10 pruebas.

1. **¿Qué agregado persiste?** ★ un repositorio por agregado raíz · uno por tabla.
   ej: `PedidoRepositorio` guarda `Pedido` con sus líneas (Fowler/Evans: Repository).
2. **¿Qué operaciones expone?** ★ `guardar`, `porId` + consultas con nombre de negocio · CRUD genérico completo.
   ej: `Optional<Pedido> porId(PedidoId)`, `List<Pedido> pendientesDe(ClienteId)`.
3. **¿Cómo se mapea el modelo?** ★ entidades JPA separadas del dominio + mapeador · el dominio anotado con JPA · JDBC/SQL a mano.
   ej: `PedidoJpa` ↔ `Pedido` mediante `PedidoMapper`.
4. **¿Qué restricciones de unicidad hay en la BD?** ★ las invariantes de unicidad como índices únicos · solo la PK.
   ej: `UNIQUE (numero_pedido)`. La violación se traduce a `PedidoDuplicado`.
5. **¿Quién abre la transacción?** ★ el caso de uso (servicio de aplicación) · el repositorio · el controlador.
   ej: `@Transactional` en `ConfirmarPedido`, no en el repositorio.
6. **¿Cómo detecta escrituras concurrentes?** ★ columna `version` (optimista) · `SELECT ... FOR UPDATE` · ninguna.
   ej: `UPDATE pedido SET ..., version=4 WHERE id=? AND version=3`. Si actualiza 0 filas, hay conflicto.
7. **¿Qué consultas son frecuentes y qué índice necesitan?** ★ lista consulta → índice · decidir luego.
   ej: `pendientesDe(cliente)` → `INDEX (cliente_id, estado)`.
8. **¿Cómo se versionan los cambios de esquema?** ★ migraciones versionadas (Flyway/Liquibase) · `ddl-auto` · a mano.
   ej: `V3__pedido_add_canal.sql`. Primero se añade como nullable y después se rellena.
9. **¿Borrado físico o lógico?** ★ lógico / estado terminal · físico · archivado.
   ej: el pedido cancelado queda en `CANCELADO`; no se hace `DELETE`.
10. **¿Cómo se prueba?** ★ prueba de integración con BD real (Testcontainers) · H2 en memoria · solo mocks.
    ej: `@DataJpaTest` + PostgreSQL en contenedor; verifica el mapeo de ida y vuelta.

### 3.7 Trabajo programado o de cola

**Elementos prioritarios:** 1 qué hace y para quién · 2 disparador y frecuencia · 3 unidad de trabajo · 4 idempotencia · 5 concurrencia y ejecución única · 6 reintentos y backoff · 7 fallo definitivo (DLQ) · 8 ejecuciones perdidas · 9 duración máxima · 10 observabilidad.

1. **¿Qué resultado de negocio produce?** ★ una frase con el efecto · solo "procesa cosas".
   ej: "Cada noche cancela los pedidos `CREADO` con más de 48 h."
2. **¿Qué lo dispara?** ★ cron con zona horaria explícita · intervalo fijo · mensaje en cola.
   ej: `0 0 2 * * *` en `America/Bogota`.
3. **¿Cuál es la unidad de trabajo?** ★ un ítem por transacción, en lotes de N · todo en una transacción.
   ej: "Lotes de 100 pedidos; cada pedido en su propia transacción."
4. **¿Qué pasa si corre dos veces sobre lo mismo?** ★ idempotente (comprueba el estado antes de actuar) · se protege con una marca · duplica.
   ej: "Solo cancela si sigue `CREADO`. La segunda pasada no hace nada."
5. **¿Puede haber dos instancias ejecutándolo a la vez?** ★ bloqueo distribuido (ShedLock/BD) · `SKIP LOCKED` por ítem · una sola instancia.
   ej: `@SchedulerLock(name="cancelarVencidos", lockAtMostFor="30m")`.
6. **¿Cómo reintenta fallos transitorios?** ★ backoff exponencial con *jitter* y tope · intervalo fijo · no reintenta.
   ej: `espera = random(0, min(60s, 1s·2^intento))`, 5 intentos (AWS *Full Jitter*).
7. **¿Qué hace con un ítem que siempre falla?** ★ lo aparta (DLQ/estado `FALLIDO`) + alerta · lo reintenta para siempre · lo ignora.
   ej: "Tras 5 intentos, marca `FALLIDO`, registra la causa y sigue con el siguiente."
8. **¿"Exactamente una vez" es requisito?** ★ al menos una vez + efecto idempotente · como máximo una vez · exactamente una vez transaccional.
   ej: "La entrega exacta no existe en red; se busca un *efecto* exacto mediante deduplicación."
9. **¿Qué pasa si el servidor estaba caído a la hora programada?** ★ recupera en la siguiente ejecución (procesa lo pendiente) · ejecuta al arrancar · se pierde.
   ej: "El criterio es '> 48 h', no 'de ayer', así que nunca se pierde nada."
10. **¿Cuánto puede durar como máximo?** ★ tope + corte ordenado · sin tope.
    ej: "Máximo 20 min. Al llegar al tope, termina el lote en curso y sale."
11. **¿Cómo sabes que corrió y cómo fue?** ★ métricas (procesados/fallidos/duración) + alerta si no corre · solo logs.
    ej: `job_cancelar_vencidos_procesados_total`; alerta si pasan 26 h sin ejecución.

### 3.8 Parámetro de configuración

**Elementos prioritarios:** 1 nombre y propósito · 2 tipo y unidad · 3 valor por defecto · 4 rango válido y validación al arrancar · 5 qué varía entre despliegues · 6 si es secreto · 7 fuente y precedencia · 8 si cambia en caliente · 9 dueño y documentación.

1. **¿Qué comportamiento controla?** ★ una decisión concreta · varias cosas a la vez.
   ej: `florlogic.pedidos.horas-vencimiento`: horas hasta cancelar un pedido sin confirmar.
2. **¿De qué tipo es y en qué unidad?** ★ tipado con unidad explícita · cadena libre.
   ej: `Duration` `48h` en lugar de `48` (¿horas? ¿minutos?).
3. **¿Cambia entre despliegues?** ★ sí: va a variable de entorno · no: es código o constante.
   ej: la URL de BD es config. La ruta de un controlador no (12-factor: si no varía por despliegue, no es config).
4. **¿Tiene valor por defecto seguro?** ★ sí, el de producción razonable · no, es obligatorio y falla al arrancar.
   ej: `horas-vencimiento: 48h` por defecto; `DB_URL` sin defecto, y si falta el arranque falla.
5. **¿Qué rango es válido?** ★ validado al arrancar (falla rápido) · validado al usarse · sin validar.
   ej: `@Min(1) @Max(720)` en `@ConfigurationProperties` + `@Validated`.
6. **¿Es secreto?** ★ sí: gestor de secretos o variable de entorno, nunca en el repo · no.
   ej: prueba 12-factor: "¿podría publicarse el repo ya mismo sin filtrar credenciales?"
7. **¿Cómo se agrupa por entorno?** ★ cada variable independiente por despliegue · perfiles con nombre (`dev`, `prod`).
   ej: `PEDIDOS_HORAS_VENCIMIENTO=24` solo en staging. 12-factor desaconseja combinatorias de entornos con nombre.
8. **¿Puede cambiar sin reiniciar?** ★ no (se lee al arrancar) · sí (refresco dinámico / *feature flag*).
   ej: "Se lee al arrancar; cambiarla exige redeploy."
9. **¿Dónde se documenta?** ★ metadatos junto al código (descripción + defecto) · wiki aparte.
   ej: Javadoc en el record `@ConfigurationProperties` → `spring-configuration-metadata.json`.

---

## 4. Cómo convertir las respuestas en una especificación

Cada respuesta genera artefactos de cuatro clases. [opinión: síntesis de Example Mapping, Specification by Example, Volere y Gherkin]

| Clase | Origen | Forma | Equivale en Example Mapping |
|---|---|---|---|
| **Afirmación** | respuesta que fija una regla o decisión | `A-{n}: {sujeto} {debe/no debe} {verbo} {objeto}. [fuente: P{k}]` | tarjeta azul (regla) |
| **Variable** | respuesta que fija un valor parametrizable | `V-{n}: {nombre} = {valor} {unidad} (rango, defecto) [P{k}]` | detalle de regla |
| **Caso dado/cuando/entonces** | ejemplo de una opción o contraejemplo | escenario Gherkin con 3–5 pasos | tarjeta verde (ejemplo) |
| **Pendiente** | "no sé", respuesta libre ambigua o supuesto ★ no confirmado | `Q-{n}: {pregunta}; supuesto provisional: {★}; bloquea: {A/V}` | tarjeta roja (pregunta) |

### 4.1 Reglas de conversión

1. **Una respuesta produce al menos una afirmación o una variable.** Si no produce ninguna, la pregunta no aportó y no cuenta para la cobertura.
2. **Cada afirmación lleva al menos un caso**, y si es una regla de negocio, dos: uno que la cumple y otro que la viola. Así lo hace Example Mapping: cada regla se ilustra con ejemplos, y cada ejemplo pasa a ser un escenario.
3. **Agrupa los casos por `Rule:`** (Gherkin 6+). Una regla equivale a un bloque `Rule` con sus `Example`.
4. **Los casos describen comportamiento observable, no implementación.** El `Entonces` debe ser visible para el cliente o el sistema externo, no el estado interno de la BD (guía de Cucumber).
5. **Usa datos concretos y con nombre:** "Floristería Rosales", `ROSA-R`, 24 tallos. Nada de "usuario A".
6. **Si los casos solo cambian en los datos, usa `Scenario Outline` + `Examples`.**
7. **Toda afirmación cualitativa necesita un criterio de ajuste medible** (Volere). Si no lo tiene, se convierte en pendiente.
8. **Traza cada artefacto a la pregunta que lo originó y al elemento del código** (clase, endpoint o tabla) que lo implementará. Eso alimenta la trazabilidad.
9. **Las decisiones de arquitectura transversales se registran como ADR** al estilo Nygard (Título, Estado, Contexto, Decisión, Consecuencias). Ejemplos: outbox frente a publicar tras el commit, o el formato de errores.

### 4.2 Ejemplo de salida

```gherkin
# A-3: Un pedido no se confirma si el stock disponible es menor que la cantidad pedida. [P5, P6]
# V-2: idempotencia.ventana = 24h (1h..72h) [P8]
Rule: No se confirma sin stock suficiente
  Example: Stock suficiente
    Given el SKU "ROSA-R" tiene 30 tallos disponibles
    And el pedido "p-77" de "Floristería Rosales" pide 24 tallos de "ROSA-R"
    When el vendedor confirma "p-77"
    Then el pedido "p-77" queda "CONFIRMADO"
    And se publica "pedido.confirmado" con pedido_id "p-77"
  Example: Stock insuficiente
    Given el SKU "ROSA-R" tiene 20 tallos disponibles
    And el pedido "p-77" pide 24 tallos de "ROSA-R"
    When el vendedor confirma "p-77"
    Then la respuesta es 422 con type "/problemas/stock-insuficiente"
    And el pedido "p-77" sigue "CREADO"
# Q-1: ¿Se permite confirmación parcial? supuesto: no. bloquea: A-3
```

---

## 5. Cómo medir que la especificación está completa

Índice de precisión = **0,35·Cobertura + 0,25·Contradicciones + 0,25·Prácticas + 0,15·Trazabilidad**. Cada componente va de 0 a 100 y el umbral es **85**.
Todo el apartado es una propuesta. [opinión]

### 5.1 Cobertura (peso 35)

Mide qué parte de los **elementos prioritarios** del tipo (sección 3) está resuelta, ponderando por orden de prioridad.

- Peso del elemento prioritario en la posición `k`, con `n` elementos: `w_k = n − k + 1` (el primero pesa más).
- Estado de cada elemento:
  - `1,0` **respondido explícitamente** y con al menos un caso dado/cuando/entonces si es una regla.
  - `0,8` respondido sin caso, o **supuesto verificado** en el código o en un ADR.
  - `0,5` **supuesto ★ aceptado** en bloque (el usuario aceptó el esqueleto sin discutir).
  - `0,25` pendiente con supuesto provisional.
  - `0` sin tocar.
  - "No aplica" justificado cuenta como `1,0` y se registra como afirmación ("A-9: no pagina porque…").
- `Cobertura = 100 · Σ(w_k · estado_k) / Σ w_k`.
- **Penalización por regla sin ejemplos:** cada afirmación de negocio con menos de 2 casos (cumple/viola) resta 3 puntos, hasta un máximo de −15.
- **Regla dura:** si alguno de los 3 primeros elementos prioritarios está en `0` o en `0,25`, la cobertura se limita a 70. Eso hace imposible llegar a 85 con lo esencial abierto. Es el equivalente a la regla de Example Mapping: si hay tarjetas rojas en lo central, la historia no está lista.

### 5.2 Contradicciones (peso 25)

Empieza en 100 y resta por cada conflicto **abierto**. Uno resuelto ya no resta.

| Tipo | Ejemplo | Resta |
|---|---|---|
| **Directa**: dos afirmaciones incompatibles | "POST idempotente" frente a "cada POST crea un pedido nuevo aunque se repita" | −25 |
| **Caso contra regla**: un ejemplo viola su propia regla | regla "stock ≥ cantidad" y caso que confirma con stock 20 / pide 24 | −20 |
| **Variable fuera de rango** o con unidades incompatibles | `timeout = 5` (sin unidad) frente a `timeout = 5s` | −10 |
| **Entre elementos**: el contrato de uno no casa con otro | el endpoint devuelve `total` en COP y el evento lo publica en USD | −15 |
| **Con el código o un ADR existente** sin ADR que lo reemplace | la spec dice "publicar tras el commit" y el ADR-004 dice outbox | −15 |
| **Ambigüedad residual**: término vago sin criterio medible | "rápido", "seguro", "muchos" | −5 |

- El mínimo es 0. **Bloqueo:** cualquier contradicción directa abierta impide superar el umbral, aunque la suma dé ≥ 85.
- **Detección:** después de cada respuesta, el agente compara la nueva afirmación con todas las del mismo elemento y con los contratos de los elementos vecinos (productor y consumidor, puerto y adaptador). Por cada par sospechoso genera una pregunta de desempate (regla 13).

### 5.3 Prácticas (peso 25), como referencia para el frente correspondiente

Porcentaje de las listas de prácticas del tipo que se cumplen o se descartan explícitamente con justificación. Ejemplos: Problem Details, Idempotency-Key, ETag, consumidor tolerante, outbox, backoff con jitter, config en el entorno, validación al arrancar.

### 5.4 Trazabilidad (peso 15)

Porcentaje de afirmaciones y variables que tienen las tres cosas: pregunta de origen (`P{k}`), al menos un caso o criterio medible y un destino en el código (clase, método, tabla, endpoint o propiedad).

### 5.5 Ejemplo rápido

Endpoint con 11 elementos prioritarios (Σw = 66). Del 1 al 7 están explícitos (w = 11..5, suma 56 · 1,0). Del 8 al 9 hay supuestos ★ (w = 4, 3 · 0,5 = 3,5). Del 10 al 11 hay pendientes (w = 2, 1 · 0,25 = 0,75).
Cobertura = 100 · 60,25/66 = **91**. Contradicciones: una ambigüedad ("rápido") = **95**. Prácticas = **80**. Trazabilidad = **85**.
Índice = 0,35·91 + 0,25·95 + 0,25·80 + 0,15·85 = 31,9 + 23,8 + 20 + 12,8 = **88,4**. Supera el umbral, así que se puede codificar, y la ambigüedad queda anotada como pendiente.

---

## 6. Fuentes

- Hu et al., *Uncertainty of Thoughts* (NeurIPS 2024) — https://arxiv.org/abs/2402.03271 — da la ganancia de información esperada para que un LLM elija preguntas, evaluada en 20 preguntas, diagnóstico y resolución de averías.
- J. S. Denker, *Twenty Questions* — https://www.av8n.com/physics/twenty-questions.pdf — explica que cada respuesta sí/no aporta 1 bit y cómo se divide el espacio.
- Artículo sobre las preguntas de Akinator (BDCC 7(1):26, 2023) — https://www.mdpi.com/2504-2289/7/1/26 — relaciona entropía y ganancia de información con el motivo de preguntar.
- RUP, *Work Guidelines: Interviews* — https://files.defcon.no/RUP/process/workguid/wg_intrv.htm — recoge la lista de preguntas libres de contexto y las metapreguntas (en la línea de Gause y Weinberg) y da consejos de orden.
- Gause y Weinberg, *Exploring Requirements* — https://www.leanpub.com/exploringrequirementsone — es la fuente original de las preguntas libres de contexto, con funciones, atributos, restricciones y preferencias.
- Revolution Learning, *The Questioning Funnel* — https://revolutionlearning.co.uk/article/the-questioning-funnel — describe el embudo abierto → sondeo → cerrado para confirmar.
- SMU Studio, *Socratic Questions* — https://studio.smu.ca/socratic-questions — presenta los seis tipos de preguntas socráticas de Paul.
- Volere Requirements Template — https://www.cs.uic.edu/~i442/VolereMaterials/templateArchive16/c%20Volere%20template16.pdf — trae la snow card y el criterio de ajuste: un requisito sin medida es ambiguo.
- Cucumber, *Example Mapping* — https://cucumber.io/docs/bdd/example-mapping — explica las tarjetas historia/regla/ejemplo/pregunta y el criterio de parada.
- Cucumber, *Gherkin Rules* (blog) — https://www.cucumber.io/blog/bdd/gherkin-rules — muestra cómo una tarjeta de ejemplo pasa a ser un escenario y una regla un bloque `Rule`.
- Cucumber, *Gherkin reference* — https://cucumber.io/docs/gherkin/reference/ — documenta Given/When/Then, Rule, Scenario Outline y la práctica de usar 3–5 pasos con comportamiento observable.
- Gojko Adzic, *Specification by Example* — https://www.oreilly.com/library/view/specification-by-example/9781617290084/ — describe los patrones: derivar el alcance de los objetivos, ilustrar con ejemplos y refinar la especificación.
- Agile Alliance, *Three Amigos* — https://agilealliance.org/glossary/three-amigos — explica las miradas de negocio, desarrollo y pruebas (Dinwiddie).
- G. Dinwiddie, *The Three Amigos Strategy* — https://www.agileconnection.com/article/three-amigos-strategy-developing-user-stories — explica por qué esas tres miradas no se solapan.
- Zalando RESTful API and Event Guidelines — https://opensource.zalando.com/restful-api-guidelines/ — trae reglas MUST de compatibilidad, Problem JSON, paginación, eventos, orden y duplicados.
- Google AIP-121 (diseño orientado a recursos) — https://google.aip.dev/121 — define recursos y métodos estándar.
- Google AIP-155 (request_id) — https://google.aip.dev/155 — trata la idempotencia por identificador de petición: el duplicado devuelve la respuesta previa.
- Microsoft Azure REST API Guidelines — https://github.com/microsoft/api-guidelines/blob/vNext/azure/Guidelines.md — cubre errores, LRO, ETag/If-Match, repetibilidad, nextLink, api-version y enums extensibles.
- RFC 9457 *Problem Details for HTTP APIs* — https://www.rfc-editor.org/rfc/rfc9457.html — define `type/title/status/detail/instance`, las extensiones y cuándo no usarlo.
- IETF draft *The Idempotency-Key HTTP Header Field* (-07) — https://datatracker.ietf.org/doc/draft-ietf-httpapi-idempotency-key-header/ — fija la unicidad, la caducidad, la huella y los códigos 400/422/409.
- CloudEvents 1.0 — https://github.com/cloudevents/spec/blob/main/cloudevents/spec.md — define los atributos `id/source/specversion/type`; `source+id` sirve para deduplicar.
- AsyncAPI 3.0 — https://www.asyncapi.com/docs/reference/specification/v3.0.0 — separa canales, operaciones y mensajes, con payload, headers y correlationId.
- Confluent, *Schema Evolution and Compatibility* — https://docs.confluent.io/platform/current/schema-registry/fundamentals/schema-evolution.html — explica la compatibilidad BACKWARD/FORWARD y los campos con valor por defecto.
- M. Fowler, *Tolerant Reader* — https://martinfowler.com/bliki/TolerantReader.html — el consumidor enlaza solo lo que necesita e ignora el resto.
- arc42 Quality, *Tolerant Reader* — https://quality.arc42.org/approaches/tolerant-reader — es un resumen operativo del patrón y de la disciplina aditiva del productor.
- microservices.io, *Transactional Outbox* — https://microservices.io/patterns/data/transactional-outbox.html — publica si y solo si hay commit; implica duplicados, consumidores idempotentes y orden.
- G. Morling, *On Idempotency Keys* — https://morling.dev/blog/on-idempotency-keys — trata el diseño de claves de idempotencia en consumidores.
- *Exactly-once delivery is a myth* — https://codemia.io/archive/exactly-once-delivery-is-a-myth — defiende al menos una vez + idempotencia = efecto exacto.
- AWS, *Exponential Backoff and Jitter* — https://aws.amazon.com/blogs/architecture/exponential-backoff-and-jitter/ — compara Full, Equal y Decorrelated Jitter frente al backoff sin jitter.
- The Twelve-Factor App, *Config* — https://12factor.net/config — config en el entorno, prueba de "repo publicable" y nada de entornos con nombre.
- M. Fowler, *Repository* (PoEAA) — https://martinfowler.com/eaaCatalog/repository.html — presenta el repositorio como colección en memoria de agregados.
- V. Vernon, *Effective Aggregate Design* — https://www.dddcommunity.org/library/vernon_2011/ — da las reglas: invariantes verdaderas, agregados pequeños, referencia por id y consistencia eventual.
- M. Nygard, *Documenting Architecture Decisions* — https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions — propone el ADR con Título, Estado, Contexto, Decisión y Consecuencias.
