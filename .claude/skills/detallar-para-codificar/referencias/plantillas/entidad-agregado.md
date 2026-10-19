---
plantilla: entidad-agregado
aplica_a: raíz de agregado, entidad interna, objeto de valor, id tipado, estados, errores y eventos de dominio de un agregado
entra_desde: caso-de-uso
sale_hacia: adaptador-persistencia, evento-mensaje
---
# Plantilla · Entidad o agregado (modelo de dominio)

## 0 · Instrucciones para el agente

1. Usa esta plantilla cuando el caso de uso ya nombró el agregado que modifica y la firma del puerto de
   entrada está cerrada. Si aún no lo está, vuelve a `caso-de-uso`.
2. Antes de la P1, muestra el esqueleto de §1 con los huecos ⟦1⟧…⟦12⟧ y la opción ★ como valor provisional
   (`investigacion/04` §2.3).
3. Pregunta un agregado por ronda. Si aparece un segundo agregado, regístralo como pendiente y sigue con
   el primero (`investigacion/01` regla 1).
4. Haz las preguntas en orden. Respeta la poda de §6. Una variable por pregunta.
5. No inventes invariantes, cifras ni nombres. Si el humano no sabe, anota un *hotspot*, asume la ★ y
   sigue (`investigacion/03` regla 18).
6. Cada invariante sale con dos casos dado/cuando/entonces, uno que la cumple y otro que la viola (Evans,
   *Assertions*).
7. Quedan fuera de esta plantilla, y se pasan a otra:
   - las tablas, el mapeo, los índices y la columna de versión van a `adaptador-persistencia`;
   - el sobre, el outbox, el orden y la deduplicación de eventos van a `evento-mensaje`;
   - la transacción y la orquestación se quedan en `caso-de-uso`.
   De aquí solo sale el contrato que esas plantillas consumen: la fábrica `reconstituir`, la `version` y
   los records de eventos.
8. Si P3 queda vacía (no hay invariantes), propone el patrón *active record* o *transaction script*
   (Khononov, cap. 5) y marca N/A las prácticas E-02, E-06 y E-10 sin penalizarlas.

## 1 · Qué se define primero

El orden sigue la dependencia: cada concepto necesita los anteriores. Los tres primeros son esenciales
(regla dura de cobertura, `investigacion/04` §5.1).

| ⟦k⟧ | Concepto | Por qué va aquí | Fuente |
|---|---|---|---|
| 1 | **Tipo y nombre**: raíz, entidad interna u objeto de valor, con su término del negocio | De él depende si tiene identidad, repositorio y ciclo de vida. | Evans, *DDD Reference* (Entities, Value Objects) |
| 2 | **Invariantes verdaderas** | Una invariante es una regla de consistencia transaccional y es lo que fija la frontera. | Vernon, EAD I, «Model True Invariants» |
| 3 | **Frontera**: qué contiene | La frontera del agregado delimita también la transacción. | Evans, *DDD Reference* (Aggregates); Fowler, *DDD_Aggregate* |
| 4 | **Identidad** | La identidad debe distinguir al objeto con independencia de su forma o su historia. | Evans (Entities); RFC 9562 |
| 5 | **Estados y transiciones** | Indican qué comandos valen en cada momento. | Aggregate Design Canvas §3; Wlaschin |
| 6 | **Comandos** (métodos con nombre de negocio) | Son la entrada del agregado y lo que evita el modelo anémico. | Fowler, *AnemicDomainModel*; Canvas §6 |
| 7 | **Errores de negocio** | Ponen nombre a cada rechazo para que el caso de uso lo traduzca. | `investigacion/02` #12; Bloch, ítem 73 |
| 8 | **Objetos de valor** | Sacan las validaciones de la raíz y eliminan la obsesión por los primitivos. | Evans (Value Objects); Fowler, *ValueObject* |
| 9 | **Referencias a otros agregados** | Van por id, para que el agregado se mantenga pequeño y distribuible. | Vernon, EAD II, «Reference Other Aggregates By Identity» |
| 10 | **Eventos de dominio** | Comunican el cambio fuera de la frontera, con consistencia eventual. | Evans, *DDD Reference* (Domain Events); Vernon EAD II |
| 11 | **Creación y reconstitución** | El agregado nace válido entero, y la persistencia lo rehace sin volver a ejecutar las reglas de creación. | Evans (Factories); Bloch, ítem 1 |
| 12 | **Concurrencia y tamaño** | Miden la probabilidad de conflictos de versión y cuánto se carga en memoria. | Canvas §8-9; Vernon EAD I |

Esqueleto que se muestra antes de la P1:

```
Agregado:      ⟦1 tipo + nombre⟧       → raíz «Pedido» (pedidos.domain)
Invariantes:   ⟦2⟧                      → I1 sin líneas no se confirma · I2 ≤ 50 líneas
Frontera:      ⟦3⟧                      → Pedido + LineaPedido (valor) · Cliente fuera
Identidad:     ⟦4⟧                      → record PedidoId(UUID v7), la genera la aplicación
Estados:       ⟦5⟧                      → Borrador → Confirmado → Despachado; Borrador|Confirmado → Cancelado
Comandos:      ⟦6⟧                      → agregarLinea · confirmar · despachar · cancelar
Errores:       ⟦7⟧                      → PedidoVacio · LimiteDeLineasExcedido · TransicionInvalida
Valores:       ⟦8⟧                      → Dinero · Cantidad · Sku
Referencias:   ⟦9⟧                      → ClienteId, EnvioId
Eventos:       ⟦10⟧                     → PedidoConfirmado · PedidoDespachado · PedidoCancelado
Creación:      ⟦11⟧                     → Pedido.abrir(...) · Pedido.reconstituir(...)
Concurrencia:  ⟦12⟧                     → version optimista · 1 cliente por pedido
```

## 2 · Funcionamiento apropiado

**Responsabilidad.** El agregado decide. Recibe los datos del comando, comprueba sus invariantes con lo
que ya tiene dentro, cambia de estado y registra uno o más eventos. No consulta repositorios, no lee el
reloj, no publica nada y no abre transacciones (`investigacion/02` §1 y #21). [opinión] El estilo por
defecto es este: el método muta la raíz y registra eventos, y el servicio de aplicación guarda y publica
(`investigacion/03` §4.8).

**Flujo de un comando:**

```
caso-de-uso (application)                      dominio (domain, sin Spring/JPA)
─────────────────────────                      ────────────────────────────────
1 carga  ── Pedidos.porId(id) ───────────────▶ adaptador → Pedido.reconstituir(..., version)
2 invoca ── pedido.confirmar(reloj.instant()) ▶ Pedido: switch(estado)
                                                 ├─ invariante rota → throw PedidoVacio
                                                 └─ ok → estado = Confirmado; eventos += PedidoConfirmado
3 guarda ── Pedidos.guardar(pedido) ─────────▶ adaptador: UPDATE ... WHERE version = ?
4 publica ─ pedido.extraerEventos() ─────────▶ ApplicationEventPublisher → evento-mensaje
```

**Dónde vive cada pieza** (`investigacion/02` §2):

| Pieza | Paquete | Depende de |
|---|---|---|
| Raíz, entidades internas, valores, ids, estados, eventos, errores, especificaciones | `pedidos.domain` | solo el JDK (y jMolecules, que son anotaciones e interfaces sin framework) |
| Interfaz del repositorio (`Pedidos`) | `pedidos.application.port.out`, o `domain` si usas el repositorio de Evans | tipos del dominio |
| Generador de ids y reloj | puerto de salida o `java.time.Clock` inyectado | — |
| Mapeo a tablas y entidad JPA | `pedidos.adapter.out.persistence` (otra plantilla) | dominio |

**Reglas de consistencia.** Dentro de la frontera, las reglas se aplican de forma síncrona; entre
fronteras, de forma asíncrona (Evans, *DDD Reference*, p. 15). Una transacción modifica un solo agregado
(Fowler: «Transactions should not cross aggregate boundaries»). Para cada regla, Vernon pregunta «¿de
quién es el trabajo?»: si lo hace el mismo usuario en el mismo acto, la consistencia es transaccional; si
lo hace otro, es eventual (EAD II).

## 3 · Patrones de diseño

| Patrón | Problema que resuelve | Úsalo cuando | No lo uses cuando | Fuente |
|---|---|---|---|---|
| Agregado (raíz) | Mantener la consistencia en un grafo de objetos | hay invariantes que abarcan varios objetos | no hay invariantes (usa active record) | Evans; Vernon EAD |
| Entidad | Seguir algo que cambia y conserva su identidad | importa su historia y no sus atributos | dos instancias con iguales datos son lo mismo | Evans (Entities) |
| Objeto de valor | Dar significado y reglas a un dato | hay formato, unidad, rango u operaciones | el dato no tiene reglas (un nombre libre) | Evans; Fowler; Bloch ítem 17 |
| Id tipado | Evitar mezclar los ids de agregados distintos | siempre en raíces y referencias | — | jMolecules `Identifier`; Vernon IDDD |
| Fábrica estática | Crear válido y con un nombre de negocio | la creación tiene reglas o varias formas | basta un constructor de un solo campo | Bloch ítem 1; Evans (Factories) |
| Fábrica aparte | Crear con datos de varias fuentes | la creación cruza agregados o puertos | los datos ya llegan en el comando | Evans (Factories); `CargoFactory` |
| Estados como tipos (sealed) | Que los estados ilegales no se puedan representar | cada estado tiene datos propios | los estados no tienen datos (basta `enum` + guardas) | Wlaschin; library `BookOnHold` |
| State (GoF) | Delegar el comportamiento en un objeto por estado | cada estado tiene mucha lógica distinta | en Java 21, cuando `switch` sobre sealed queda más claro [opinión] | GoF; refactoring.guru/state |
| Strategy / Policy | Reglas intercambiables o configurables | la regla cambia por tipo de cliente, país o versión | hay una sola regla fija | Evans cap. 12; library `PlacingOnHoldPolicy` |
| Specification | Una regla de selección con nombre y combinable | la regla se usa para validar, para filtrar y para construir | se usa una sola vez (basta un método privado) | Evans y Fowler, *Specifications*; dddsample |
| Composite | Combinar especificaciones o políticas | se arman reglas con y/o/no | hay una sola regla | Evans cap. 12; `AbstractSpecification` |
| Evento de dominio | Avisar de un hecho fuera de la frontera | otro agregado o módulo reacciona | nadie lo escucha ni lo necesita el negocio | Evans *DDD Reference*; Vernon EAD II |
| Decisión funcional (comando → eventos) | Separar la decisión de la mutación | la máquina de estados es rica o hay event sourcing | el equipo trabaja con OO mutable y JPA [opinión] | Wlaschin DMMF; library `Patron` |
| Concurrencia optimista | Detectar escrituras que se pisan | hay más de un cliente por instancia | hay un solo escritor garantizado | Vernon EAD I |

**Decisiones de patrón que cierra la ronda:**
- estilo (OO mutable con eventos registrados ★, o decisión funcional): P15;
- estados (sealed ★, o `enum` con guardas): P10;
- errores (excepción con nombre ★, o `Resultado` sellado): P16;
- regla reutilizable (método privado ★, Specification o Strategy): P17;
- creación (fábrica estática ★, o fábrica aparte): P13.

## 4 · Prácticas profesionales verificables

Severidad: C = crítica (3), M = mayor (2), m = menor (1). Verificación: A = ArchUnit, P = prueba,
L = lectura.

| id | Práctica | Sev. | Al detallar | Al verificar | Fuente |
|---|---|---|---|---|---|
| E-01 | El dominio no importa Spring, JPA, Jackson ni `jakarta.*` | C | P14 decidió el mapeo aparte | A: `noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat().resideInAnyPackage("org.springframework..","jakarta..","com.fasterxml..")` | `investigacion/02` #1 |
| E-02 | Sin setters públicos: cada cambio es un método con nombre de negocio | M | P15 lista los comandos | A: no hay métodos `set*` públicos en `..domain..` | Fowler, *AnemicDomainModel* |
| E-03 | Cada invariante tiene un solo dueño y una prueba que la viola | C | P3-P4 con casos | P: una prueba por invariante que espera el rechazo | Evans, *Assertions* |
| E-04 | Las referencias a otros agregados van por id o `Association` | C | P9 | A: `JMoleculesDddRules.aggregateReferencesShouldBeViaIdOrAssociation()` | Vernon EAD II; jMolecules |
| E-05 | Un comando modifica un solo agregado | M | P5 sin «dos agregados» | L: el caso de uso guarda una sola raíz | Fowler; Vernon EAD II |
| E-06 | Agregado pequeño; toda colección interna tiene un límite explícito | M | P6 con cifra | P: una prueba que excede el límite | Vernon EAD I; Canvas §9 |
| E-07 | Los objetos de valor son `record` inmutables que se validan en el constructor compacto | M | P8 | P: la construcción inválida falla | Evans; Fowler; Bloch ítem 17 |
| E-08 | Los ids son tipados (`record PedidoId(UUID)`), sin `UUID`/`Long` crudos en las firmas | M | P7 | L o A: las firmas públicas de `..domain..` no usan `java.util.UUID` | jMolecules `Identifier` |
| E-09 | Igualdad: la entidad por id, el valor por todos sus campos, y `hashCode` coherente | M | P1 y P7 | P: dos `Pedido` con el mismo id son iguales; EqualsVerifier para valores | Bloch ítems 10-11; Hibernate UG §3.4.7 |
| E-10 | Los estados con datos distintos son variantes sealed, sin flags `boolean` ni campos `null` según el estado | M | P10 | L: no hay `boolean` de estado; `switch` exhaustivo sin `default` | Wlaschin (frase de Y. Minsky) |
| E-11 | Los rechazos de negocio son errores con nombre, no `IllegalStateException` ni `RuntimeException` | M | P16 | A: `GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS` | `investigacion/02` #12; Bloch ítem 73 |
| E-12 | Los eventos están en pasado, son inmutables y llevan el instante y los ids implicados | M | P18 | L: `record` en pasado; P: el comando registra el evento | Evans, *DDD Reference* p. 12 |
| E-13 | El agregado nace válido por una fábrica con nombre, sin constructor vacío público | M | P13 | A o L: constructores privados en la raíz | Evans (Factories); Bloch ítem 1 |
| E-14 | El dominio no lee el reloj ni el azar: el instante y el id llegan como parámetro | m | P7 y P15 | A: `..domain..` no llama a `Instant.now`, `LocalDateTime.now` ni `UUID.randomUUID` | `investigacion/02` #21 |
| E-15 | La raíz lleva `version` para la concurrencia optimista | M | P19 | P (en persistencia): el conflicto se traduce a un error de dominio | Vernon EAD I; `investigacion/03` regla 13 |
| E-16 | Las colecciones internas no se exponen mutables | M | P5 | P: modificar `pedido.lineas()` lanza `UnsupportedOperationException` | Bloch ítem 50 |
| E-17 | Los nombres salen del lenguaje ubicuo (sin `Manager`, `Data` ni `Helper`) | M | P2 | L: se cruzan contra el glosario | Evans, *Intention-Revealing Interfaces* |
| E-18 | Los ids de BD usan UUIDv7; si el id da acceso, UUIDv4 | m | P7 | L | RFC 9562 §5.7 y §8 |
| E-19 | Fuera de la raíz solo se ve la raíz; las entidades internas no salen | M | P5 | A: `JMoleculesDddRules.entitiesShouldBeDeclaredForUseInSameAggregate()` | Evans (Aggregates) |
| E-20 | Los valores e ids no referencian entidades | m | P8 | A: `JMoleculesDddRules.valueObjectsMustNotReferToIdentifiables()` | jMolecules |

## 5 · Árbol de preguntas

Formato: título de una línea + contexto pequeño o mediano con el **por qué** (SKILL.md, «Cómo preguntas») · opciones con la ★ primero y un ejemplo cada una ·
**cierra**.

**A · Tipo, frontera e invariantes**

**P1. ¿Qué es este elemento: raíz de agregado, entidad interna u objeto de valor?**
Por qué: decide si tiene identidad, repositorio y ciclo de vida.
1. ★ Raíz de agregado — `public final class Pedido implements AggregateRoot<Pedido, PedidoId>`
2. Entidad interna — `final class LineaPedido implements Entity<Pedido, LineaId>`, sin repositorio
3. Objeto de valor — `public record Dinero(BigDecimal monto, Currency moneda) implements ValueObject`
Cierra: ⟦1⟧, E-09.

**P2. ¿Cómo lo llama el negocio y qué decide, en una frase?**
Por qué: el nombre pasa a ser tipo, paquete y evento. Es lo más barato de corregir hoy.
1. ★ Término del glosario más su decisión — «Pedido: decide si se puede confirmar, despachar o cancelar»
2. Nombre técnico (se rechaza) — `PedidoManager`, `PedidoData`
3. Dos conceptos en uno (se parte) — «pedido» y «orden de carga» → dos agregados
Cierra: ⟦1⟧, E-17.

**P3. ¿Qué reglas nunca pueden romperse, ni un instante?**
Por qué: las invariantes verdaderas fijan la frontera (Vernon).
1. ★ De 1 a 5 invariantes con nombre — «I1 sin líneas no se confirma; I2 como máximo 50 líneas»
2. Ninguna — `Pedido` solo guarda y lee → propone active record (Khononov, cap. 5)
3. Más de 7 — complejidad local alta → partir el agregado (Canvas §4)
Cierra: ⟦2⟧, E-03.

**P4. Si esa regla se rompe un momento y luego se corrige, ¿hay daño real?**
Por qué: separa la invariante de la política correctiva (Canvas §5; Vernon, «¿de quién es el trabajo?»).
1. ★ Sí, hay daño → invariante dentro del agregado — `if (lineas.isEmpty()) throw new PedidoVacio(id);`
2. No → política con evento — `@ApplicationModuleListener void on(PedidoConfirmado e) { inventario.reservar(e.pedido()); }`
3. No se sabe → hotspot — `Q-3: ¿se puede confirmar sin stock? supuesto: invariante`
Cierra: ⟦2⟧, ⟦3⟧.

**P5. ¿Qué objetos tienen que cambiar juntos para cumplir las invariantes?**
Por qué: la frontera mínima evita cargas pesadas y conflictos.
1. ★ Raíz más valores — `Pedido` con `List<LineaPedido>` donde `LineaPedido` es un `record`
2. Raíz más entidades internas acotadas — `LineaPedido` con `LineaId` porque se edita una a una
3. Otro agregado (se rediseña) — `Pedido` e `Inventario` → evento + política
Cierra: ⟦3⟧, E-05, E-16, E-19.

**P6. ¿Cuántos hijos puede tener una instancia como máximo?**
Por qué: una colección sin límite rompe el rendimiento y la idea de agregado pequeño (Vernon EAD I).
1. ★ Acotado con una cifra — `static final int MAX_LINEAS = 50;`
2. Cientos o miles → sacarlos a su propio agregado — `LineaDeCatalogo` con `CatalogoId`
3. Sin límite y por periodo → partir por ventana — `CuentaDelMes(CuentaId, YearMonth)`
Cierra: ⟦12⟧, E-06.

**B · Identidad y valores**

**P7. ¿Cómo se identifica una instancia y quién genera el id?**
Por qué: el id debe existir antes de guardar y ordenar bien en el índice (RFC 9562 §6.13).
1. ★ `record PedidoId(UUID)` v7, generado por la aplicación — `generador.nuevoPedidoId()`
2. Clave natural de negocio — `record NumeroGuia(String valor)` con formato validado
3. Secuencia de BD (se desaconseja) — `Long id` nulo hasta guardar, como `Account.withoutId` en buckpal
Cierra: ⟦4⟧, E-08, E-14, E-18.

**P8. ¿Qué datos son valores con reglas propias, no texto ni números sueltos?**
Por qué: sacan validaciones de la raíz y evitan la obsesión por los primitivos.
1. ★ Los que tienen formato, unidad o rango — `Dinero`, `Cantidad(int)`, `Sku(String)`
2. Solo los ids — `PedidoId`, `ClienteId`, y el resto como primitivos
3. Ninguno — `double precio, int cantidad` (antipatrón)
Cierra: ⟦8⟧, E-07, E-20.

**P9. ¿Cómo referencia a otros agregados?**
Por qué: la referencia por id mantiene el agregado pequeño y sin cascadas (Vernon EAD II).
1. ★ Por id tipado — `private final ClienteId cliente;`
2. `Association` de jMolecules — `Association<Cliente, ClienteId> cliente` (jmolecules-examples)
3. Por objeto, si se justifica una de las 4 razones de Vernon — `Cliente cliente` + ADR
Cierra: ⟦9⟧, E-04.

**C · Ciclo de vida**

**P10. ¿En qué estados puede estar y qué datos existen solo en cada uno?**
Por qué: los estados como tipos impiden las combinaciones imposibles (Wlaschin).
1. ★ Sealed con un record por estado — `record Despachado(Instant confirmadoEn, EnvioId envio) implements EstadoPedido`
2. `enum` más guardas, si los estados no tienen datos — `enum Estado { BORRADOR, CONFIRMADO }`
3. Sin estados — el agregado solo valida al crear (revisa P3: ¿es anémico?)
Cierra: ⟦5⟧, E-10.

**P11. Si llega un comando en un estado que no lo admite, ¿qué pasa?**
Por qué: cada transición prohibida es un caso de prueba y un error con nombre.
1. ★ Se rechaza con un error de dominio — `case Cancelado c -> throw new TransicionInvalida(id, estado, "confirmar");`
2. Se ignora si es el mismo comando repetido (idempotente) — `case Confirmado c -> { }`
3. Se acepta y se corrige luego (política) — `Cancelado` + evento `CancelacionTardiaRegistrada`
Cierra: ⟦5⟧, ⟦7⟧.

**P12. ¿Hay estados terminales que no se pueden deshacer?**
Por qué: lo terminal no se edita; se compensa con otro hecho.
1. ★ Sí, y se compensa con un evento — `Cancelado` es terminal; `PedidoReabierto` crea uno nuevo
2. Se reabre con permiso — `reabrir(SupervisorId)` desde `Cancelado` a `Borrador`
3. No hay terminales — el ciclo vuelve a empezar (revisa el tamaño en P6)
Cierra: ⟦5⟧.

**P13. ¿Cómo nace válido?**
Por qué: un agregado debe crearse entero cumpliendo sus invariantes (Evans, *Factories*).
1. ★ Fábrica estática con nombre de negocio — `Pedido.abrir(PedidoId id, ClienteId cliente)`
2. Fábrica aparte, si necesita datos de varias fuentes — `FabricaDePedidos.desdeCotizacion(cot, tarifas)`
3. Constructor público con validación — `new Pedido(id, cliente)` (aceptable si solo hay una forma de crear)
Cierra: ⟦11⟧, E-13.

**P14. ¿Cómo lo reconstruye el adaptador de persistencia?**
Por qué: reconstituir no debe disparar reglas de creación ni eventos. Lo consume `adaptador-persistencia`.
1. ★ Fábrica `reconstituir(...)` que recibe el estado y la versión — `Pedido.reconstituir(id, cliente, lineas, estado, 7)`
2. Dominio anotado con JPA y constructor protegido — `protected Pedido() {}` como en dddsample (E-01 pasa a N/A, con ADR)
3. Constructor vacío y setters (se rechaza) — `new Pedido(); p.setEstado(...)`
Cierra: ⟦11⟧, E-01.

**D · Comportamiento**

**P15. ¿Qué forma tiene cada comando?**
Por qué: fija cómo el caso de uso invoca, guarda y publica.
1. ★ Muta la raíz, registra eventos y no devuelve nada — `void confirmar(Instant ahora)`
2. Decisión funcional — `static Resultado<EventoPedido> decidir(EstadoPedido e, ComandoPedido c)`
3. Devuelve `boolean` (se rechaza) — `boolean withdraw(...)`, como buckpal: pierde el motivo
Cierra: ⟦6⟧, E-02, E-14.

**P16. ¿Cómo comunica el rechazo de negocio?**
Por qué: el caso de uso y el adaptador web traducen cada rechazo a RFC 9457.
1. ★ Excepción de dominio con nombre — `throw new PedidoVacio(id)`; el adaptador web la convierte en 422
2. `Resultado` sellado o `Either` — `Rechazado(new PedidoVacio(id))`, como library
3. `IllegalStateException` genérica (se rechaza) — como `markPaid()` en spring-restbucks
Cierra: ⟦7⟧, E-11.

**P17. ¿Hay una regla de elegibilidad que se repita o que cambie según el contexto?**
Por qué: elige entre método privado, Specification o Strategy sin sobrediseñar.
1. ★ No, es un método privado — `private boolean admiteLineas() { return lineas.size() < MAX_LINEAS; }`
2. Specification con nombre y combinable — `DespachableEnZona.y(SinBloqueoDeCliente)`
3. Políticas intercambiables (Strategy) — `List<PoliticaDeConfirmacion>`, como `PlacingOnHoldPolicy`
Cierra: §3 (decisión de patrón).

**P18. ¿Qué evento emite cada comando y qué datos lleva?**
Por qué: es el contrato que consume `evento-mensaje` y los demás módulos.
1. ★ Uno por transición relevante, en pasado, con ids, instante y lo que necesita el consumidor — `PedidoConfirmado(pedido, total, ocurrioEn)`
2. Ninguno — el cambio no le interesa a nadie fuera (se justifica en una línea)
3. Evento CRUD (se rechaza) — `PedidoActualizado(pedido)`
Cierra: ⟦10⟧, E-12.

**E · Concurrencia y verificación**

**P19. ¿Pueden dos actores cambiar la misma instancia a la vez?**
Por qué: decide si basta la versión optimista o hay que partir el agregado (Canvas §8).
1. ★ A veces → `version` optimista y conflicto como error o reintento — `long version` en la raíz
2. Casi nunca (un solo cliente) → `version` igualmente, barata — mismo campo
3. A menudo, con muchos clientes → partir el agregado — `CupoDeEnvio` por franja y no un solo `Envio`
Cierra: ⟦12⟧, E-15.

**P20. ¿Cómo se verifica el modelo?**
Por qué: Evans pide escribir las aserciones como pruebas automáticas.
1. ★ Dado/cuando/entonces por invariante + ArchUnit con `JMoleculesDddRules.all()` — §10 T9
2. Solo pruebas unitarias del camino feliz — `confirmaPedido()`
3. Ninguna (se rechaza)
Cierra: E-03, E-01, E-04, E-19.

## 6 · Poda y ramas nuevas

- **P1 = objeto de valor:** se saltan P5, P6, P9-P12, P14, P15 y P18, P19. Los cupos se usan en: rango
  válido, normalización (escala de `BigDecimal`), operaciones sin efectos y formato textual.
- **P1 = entidad interna:** se saltan P7 (el id es local a la raíz), P14 y P19. Se confirma qué métodos
  de la raíz la modifican.
- **P3 = ninguna:** se propone active record. Se saltan P4-P6, P10-P12 y P17. E-02, E-06 y E-10 quedan N/A.
- **P10 = sin estados:** se saltan P11 y P12. El cupo va a P3 («¿seguro que no hay ciclo de vida?»).
- **P18 = ninguno:** el cupo va a «¿quién necesita saber que cambió?». Si nadie, se cierra.
- **P19 = a menudo:** el cupo va a rediseñar la frontera (vuelve a P5).
- **Lo que el código o un ADR ya fija** se confirma en una línea y libera su cupo
  (`investigacion/04` regla 7).
- **Cupos libres:** primero los contraejemplos de las invariantes («¿y con 0 líneas?») y al final la
  metapregunta «¿hay algo que debí preguntarte sobre este agregado?» (`investigacion/04` regla 18).

## 7 · Cobertura

Pesos en el orden de §1 (⟦1⟧ pesa 12, ⟦12⟧ pesa 1).

- [ ] ⟦1⟧ Tipo y nombre del glosario, con su responsabilidad en una frase
- [ ] ⟦2⟧ Invariantes con nombre, cada una con su par de casos (cumple/viola)
- [ ] ⟦3⟧ Frontera: qué contiene, qué queda fuera y las políticas correctivas
- [ ] ⟦4⟧ Identidad: tipo de id, quién lo genera y la versión de UUID
- [ ] ⟦5⟧ Estados, transiciones permitidas y prohibidas, y estados terminales
- [ ] ⟦6⟧ Comandos con su firma
- [ ] ⟦7⟧ Catálogo de errores de negocio
- [ ] ⟦8⟧ Objetos de valor con sus reglas
- [ ] ⟦9⟧ Referencias a otros agregados
- [ ] ⟦10⟧ Eventos por comando, con su carga útil
- [ ] ⟦11⟧ Fábricas de creación y de reconstitución
- [ ] ⟦12⟧ Límite de tamaño y política de concurrencia

## 8 · Antipatrones

| Antipatrón | Señal detectable | Fuente |
|---|---|---|
| Modelo anémico | Solo getters/setters; un `PedidoService` hace `if (p.getEstado()==…) p.setEstado(…)` | Fowler, *AnemicDomainModel* |
| Agregado gigante | Colecciones sin límite; cargar la raíz trae cientos de filas; conflictos de versión frecuentes | Vernon EAD I |
| Referencia por objeto entre agregados | `Cliente cliente` dentro de `Pedido`; `@ManyToOne` hacia otra raíz | Vernon EAD II; jMolecules ArchUnit |
| Obsesión por primitivos | `String sku`, `double precio`, `int cantidad` validados en varios sitios | `investigacion/03` §6 |
| Estado por banderas | `boolean confirmado, cancelado`; campos que valen `null` según el estado | Wlaschin |
| Comando con `boolean` | `boolean withdraw(...)` devuelve `false` y el motivo se pierde | [opinión] sobre buckpal `Account` |
| Error genérico | `throw new IllegalStateException("...")` como rechazo de negocio | `investigacion/02` #12 |
| Reloj o azar ocultos | `LocalDateTime.now()` o `UUID.randomUUID()` dentro del agregado; pruebas no deterministas | `investigacion/02` #21 |
| Constructor vacío público | `public Pedido() {}` para el ORM; el objeto puede existir inválido | Evans (Factories) |
| Evento CRUD o técnico | `PedidoActualizado`, `RegistroGuardado` | `investigacion/03` §6 |
| Colección expuesta | `getLineas()` devuelve la lista interna mutable | Bloch ítem 50 |
| Igualdad mal definida | Entidad con `equals` por todos los campos; VO con `BigDecimal` sin normalizar la escala | Bloch ítems 10-11 |
| Dominio atado al ORM | `@Entity` y `@ManyToOne` dictan la forma del agregado (dddsample `Cargo`) | `investigacion/02` §6 |

## 9 · Código de referencia real

Todos los archivos se leyeron el 15-oct-2026 en el commit indicado. Los fragmentos son literales
(indentación normalizada a espacios) y `// …` marca lo omitido.

### 9.1 Agregado que decide y devuelve eventos, con políticas (Strategy)

- **Muestra:** una raíz cuyo comando devuelve `Either<fallo, eventos>` y unas políticas combinables. Es el
  estilo funcional de P15-2, P16-2 y P17-3.
- **Repositorio:** `ddd-by-examples/library` · licencia MIT.
- **Archivos:**
  [`Patron.java`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/main/java/io/pillopl/library/lending/patron/model/Patron.java#L28-L80)
  y [`PlacingOnHoldPolicy.java`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/main/java/io/pillopl/library/lending/patron/model/PlacingOnHoldPolicy.java#L13-L49)

```java
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "patron")
public class Patron {
    // …
    @NonNull
    private final List<PlacingOnHoldPolicy> placingOnHoldPolicies;
    // …
    public Either<BookHoldFailed, BookPlacedOnHoldEvents> placeOnHold(AvailableBook aBook, HoldDuration duration) {
        Option<Rejection> rejection = patronCanHold(aBook, duration);
        if (rejection.isEmpty()) {
            BookPlacedOnHold bookPlacedOnHold = bookPlacedOnHoldNow(aBook.getBookId(), aBook.type(), aBook.getLibraryBranch(), patron.getPatronId(), duration);
            if (patronHolds.maximumHoldsAfterHolding(aBook)) {
                return announceSuccess(events(bookPlacedOnHold, MaximumNumberOhHoldsReached.now(patron, MAX_NUMBER_OF_HOLDS)));
            }
            return announceSuccess(events(bookPlacedOnHold));
        }
        return announceFailure(bookHoldFailedNow(rejection.get(), aBook.getBookId(), aBook.getLibraryBranch(), patron));
    }
    // …
    private Option<Rejection> patronCanHold(AvailableBook aBook, HoldDuration forDuration) {
        return placingOnHoldPolicies
                .toStream()
                .map(policy -> policy.apply(aBook, this, forDuration))
                .find(Either::isLeft)
                .map(Either::getLeft);
    }
// … PlacingOnHoldPolicy.java
interface PlacingOnHoldPolicy extends Function3<AvailableBook, Patron, HoldDuration, Either<Rejection, Allowance>> {

    PlacingOnHoldPolicy onlyResearcherPatronsCanHoldRestrictedBooksPolicy = (AvailableBook toHold, Patron patron, HoldDuration holdDuration) -> {
        if (toHold.isRestricted() && patron.isRegular()) {
            return left(Rejection.withReason("Regular patrons cannot hold restricted books"));
        }
        return right(new Allowance());
    };
    // …
```

- **Qué copiar:** la decisión sin efectos, que devuelve el resultado; las políticas con nombre de negocio
  inyectadas en la raíz; la igualdad solo por la identidad (`of = "patron"`).
- **Qué no copiar:** la dependencia de Vavr y Lombok en el dominio (con Java 21 se usa un `Resultado`
  sellado); `Instant.now()` dentro de las fábricas de eventos (`PatronEvent.java`), que va contra E-14.

### 9.2 Estados como tipos distintos

- **Muestra:** cada estado del libro es su propia clase (`AvailableBook`, `BookOnHold`, `CheckedOutBook`)
  bajo la interfaz `Book`. Cada transición devuelve el tipo del estado siguiente (P10-1) y la clase lleva
  su `Version` (P19).
- **Repositorio:** `ddd-by-examples/library` · licencia MIT.
- **Archivo:** [`BookOnHold.java`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/main/java/io/pillopl/library/lending/book/model/BookOnHold.java#L20-L69)

```java
@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "bookInformation")
public class BookOnHold implements Book {

    @NonNull
    BookInformation bookInformation;

    @NonNull
    LibraryBranchId holdPlacedAt;

    @NonNull
    PatronId byPatron;

    @NonNull
    Instant holdTill;

    @NonNull
    Version version;
    // …
    public CheckedOutBook handle(BookCheckedOut bookCheckedOut) {
        return new CheckedOutBook(
                bookInformation,
                new LibraryBranchId(bookCheckedOut.getLibraryBranchId()),
                new PatronId(bookCheckedOut.getPatronId()),
                version);
    }
```

- **Qué copiar:** los datos que solo existen en ese estado (`holdTill`, `byPatron`) viven solo en ese tipo,
  y la transición es inmutable y devuelve un nuevo valor.
- **Qué no copiar:** `Book` no es `sealed` (en 2019 no existía); en Java 21 se declara
  `sealed interface ... permits`.

### 9.3 Raíz con identidad de negocio y consistencia síncrona dentro de la frontera

- **Muestra:** la entidad igualada por `TrackingId` (E-09). Recalcula el valor `Delivery` dentro de la
  frontera y deja el historial de manejo, que es otro agregado, para la vía asíncrona.
- **Repositorio:** `citerus/dddsample-core` · licencia MIT.
- **Archivo:** [`Cargo.java`](https://github.com/citerus/dddsample-core/blob/d8b896254ed4e18ba8117dcd5bf9a54708bdeaaf/src/main/java/se/citerus/dddsample/domain/model/cargo/Cargo.java#L72-L221)

```java
  public Cargo(final TrackingId trackingId, final RouteSpecification routeSpecification) {
    Objects.requireNonNull(trackingId, "Tracking ID is required");
    Objects.requireNonNull(routeSpecification, "Route specification is required");

    this.trackingId = trackingId.idString();
    // Cargo origin never changes, even if the route specification changes.
    // However, at creation, cargo origin can be derived from the initial route specification.
    this.origin = routeSpecification.origin();
    this.routeSpecification = routeSpecification;
    // …
  }
  // …
  public void specifyNewRoute(final RouteSpecification routeSpecification) {
    Objects.requireNonNull(routeSpecification, "Route specification is required");

    this.routeSpecification = routeSpecification;
    Itinerary itineraryForRouting = this.itinerary != null && !this.itinerary.isEmpty() ? new Itinerary(this.itinerary) : null;
    // Handling consistency within the Cargo aggregate synchronously
    this.delivery = delivery.updateOnRouting(this.routeSpecification, itineraryForRouting);
  }
  // …
  @Override
  public boolean sameIdentityAs(final Cargo other) {
    return other != null && trackingId.equals(other.trackingId);
  }
  // …
  @Override
  public int hashCode() {
    return trackingId.hashCode();
  }
```

- **Qué copiar:** los comandos con nombre de negocio (`specifyNewRoute`, `assignToRoute`); el valor
  `Delivery` se sustituye entero; `equals` y `hashCode` van solo por la identidad.
- **Qué no copiar:** `@Entity`, `@ManyToOne` y `@GeneratedValue long id` en el dominio, y el constructor
  `protected Cargo()` «Needed by Hibernate». Es la opción P14-2 y deja E-01 en N/A.

### 9.4 Specification como objeto de valor, y su base Composite

- **Muestra:** `RouteSpecification` es a la vez objeto de valor y regla `isSatisfiedBy(Itinerary)`
  (Evans y Fowler: validar, seleccionar y construir a pedido).
- **Repositorio:** `citerus/dddsample-core` · licencia MIT.
- **Archivos:**
  [`RouteSpecification.java`](https://github.com/citerus/dddsample-core/blob/d8b896254ed4e18ba8117dcd5bf9a54708bdeaaf/src/main/java/se/citerus/dddsample/domain/model/cargo/RouteSpecification.java#L23-L79)
  y [`AbstractSpecification.java`](https://github.com/citerus/dddsample-core/blob/d8b896254ed4e18ba8117dcd5bf9a54708bdeaaf/src/main/java/se/citerus/dddsample/domain/shared/AbstractSpecification.java#L8-L35)

```java
public class RouteSpecification extends AbstractSpecification<Itinerary> implements ValueObject<RouteSpecification> {
  // …
  public RouteSpecification(final Location origin, final Location destination, final Instant arrivalDeadline) {
    Objects.requireNonNull(origin, "Origin is required");
    Objects.requireNonNull(destination, "Destination is required");
    Objects.requireNonNull(arrivalDeadline, "Arrival deadline is required");
    Validate.isTrue(!origin.sameIdentityAs(destination), "Origin and destination can't be the same: " + origin);
    // …
  }
  // …
  @Override
  public boolean isSatisfiedBy(final Itinerary itinerary) {
    return itinerary != null &&
           origin().sameIdentityAs(itinerary.initialDepartureLocation()) &&
           destination().sameIdentityAs(itinerary.finalArrivalLocation()) &&
           arrivalDeadline().isAfter(itinerary.finalArrivalDate());
  }
// … AbstractSpecification.java
  public Specification<T> and(final Specification<T> specification) {
    return new AndSpecification<T>(this, specification);
  }
  // …
  public Specification<T> not(final Specification<T> specification) {
    return new NotSpecification<T>(specification);
  }
```

- **Qué copiar:** la validación completa en el constructor; la regla expresada con el lenguaje del dominio;
  la composición con `and` y `or`.
- **Qué no copiar:** `not(spec)` niega el argumento e ignora `this`. En la plantilla T7 es `no()` sin
  argumento. Tampoco las anotaciones JPA en el valor.

### 9.5 Tipos de jMolecules: raíz, id tipado como record y `Association`

- **Muestra:** `AggregateRoot<T, ID>`, un `Identifier` como `record` y la referencia a otro agregado por
  `Association` (P9-2). Con eso se pueden aplicar las reglas `JMoleculesDddRules`.
- **Repositorio:** `xmolecules/jmolecules-examples` · licencia Apache-2.0.
- **Archivos:**
  [`jpa/order/Order.java`](https://github.com/xmolecules/jmolecules-examples/blob/54035665cb14df6619c2a6dc9ef56a68889f7973/jmolecules-example-spring-data-jpa/src/main/java/org/jmolecules/examples/jpa/order/Order.java#L34-L50)
  y [`jpa/customer/Customer.java`](https://github.com/xmolecules/jmolecules-examples/blob/54035665cb14df6619c2a6dc9ef56a68889f7973/jmolecules-example-spring-data-jpa/src/main/java/org/jmolecules/examples/jpa/customer/Customer.java#L33-L54)

```java
@Table(name = "SAMPLE_ORDER")
@Getter
@RequiredArgsConstructor
public class Order implements AggregateRoot<Order, Order.OrderId> {

    private final OrderId id;
    private List<LineItem> lineItems;
    private Association<Customer, CustomerId> customer;

    public Order(Customer customer) {

        this.id = new OrderId(UUID.randomUUID());
        this.customer = Association.forAggregate(customer);
    }

    public record OrderId(UUID orderId) implements Identifier {}
}
// … Customer.java
    public record Name(String firstname, String lastname) implements ValueObject {}

    public record CustomerId(UUID id) implements Identifier {}
```

- **Qué copiar:** el id como `record ... implements Identifier`, el valor como `record ... implements
  ValueObject` y la referencia por `Association` o por id.
- **Qué no copiar:** `UUID.randomUUID()` dentro del constructor (va contra E-14 y es v4, no v7) y
  `@Getter` sobre una lista mutable (E-16). Es un ejemplo de la integración con el ORM, no de reglas de
  negocio.

### 9.6 Raíz que registra eventos y lleva versión (Spring Data `AbstractAggregateRoot`)

- **Muestra:** `registerEvent(...)` en cada transición (P15-1, P18-1) y `@Version` para la concurrencia
  optimista (P19).
- **Repositorio:** `odrotbohm/spring-restbucks`. No tiene archivo de licencia en la raíz; la cabecera del
  archivo declara Apache-2.0.
- **Archivo:** [`Order.java`](https://github.com/odrotbohm/spring-restbucks/blob/ad97ad03c367a69ff1de70c660be2731cbe7bc3d/server/src/main/java/de/odrotbohm/restbucks/order/Order.java#L49-L137)

```java
public class Order extends AbstractAggregateRoot<Order> implements AggregateRoot<Order, OrderIdentifier> {

    private final OrderIdentifier id;
    private final Location location;
    private final LocalDateTime orderedDate;
    private Status status;
    private @Version Long version;
    // …
    public Order(Collection<LineItem> lineItems, Location location) {

        this.id = new OrderIdentifier(UUID.randomUUID());
        this.location = location == null ? Location.TAKE_AWAY : location;
        this.status = Status.PAYMENT_EXPECTED;
        this.lineItems.addAll(lineItems);
        this.orderedDate = LocalDateTime.now();
        registerEvent(new OrderCreated(id, location));
    }
    // …
    Order markPaid() {

        if (isPaid()) {
            throw new IllegalStateException("Already paid order cannot be paid again!");
        }

        this.status = Status.PAID;

        registerEvent(new OrderPaid(id, getPrice()));

        return this;
    }
```

- **Qué copiar:** el evento se registra en la misma transición que cambia el estado y lo publica quien
  guarda (Spring Data publica los `@DomainEvents` en `save(…)`); la guarda de estado va antes de mutar.
- **Qué no copiar:** `IllegalStateException` como rechazo de negocio (E-11), `LocalDateTime.now()` y
  `UUID.randomUUID()` en el dominio (E-14) y la herencia de una clase de Spring en el dominio (E-01).

### 9.7 Fábricas estáticas de creación y reconstitución, con objeto de valor `Money`

- **Muestra:** `withoutId` (crear) frente a `withId` (reconstituir), con un constructor privado (P13, P14).
- **Repositorio:** `thombergs/buckpal`. **No declara licencia** (la API de GitHub devuelve
  `license: null`). Se cita solo como referencia breve; no copies el código literal.
- **Archivo:** [`Account.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/main/java/io/reflectoring/buckpal/application/domain/model/Account.java#L17-L95)

```java
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {
    // …
    public static Account withoutId(
                    Money baselineBalance,
                    ActivityWindow activityWindow) {
        return new Account(null, baselineBalance, activityWindow);
    }
    // …
    public static Account withId(
                    AccountId accountId,
                    Money baselineBalance,
                    ActivityWindow activityWindow) {
        return new Account(accountId, baselineBalance, activityWindow);
    }
    // …
    public boolean withdraw(Money money, AccountId targetAccountId) {

        if (!mayWithdraw(money)) {
            return false;
        }
        // …
```

- **Qué copiar:** las dos fábricas con nombre y la ventana acotada de actividades (`ActivityWindow`), que
  mantiene el agregado pequeño (E-06).
- **Qué no copiar:** el id `Long` nulo hasta guardar (P7-3), el `boolean` como resultado del comando
  (P15-3) y `LocalDateTime.now()` dentro de `withdraw`.

## 10 · Plantillas de código

Son esqueletos en Java 21. El dominio usa solo el JDK y `jmolecules-ddd`/`jmolecules-events`. Cada `TODO Pn`
lo resuelve la pregunta n. Paquete de ejemplo: `com.ejemplo.pedidos.domain`.

**T1 · Id tipado y generador** (basado en 9.5; v7 según RFC 9562 §5.7)

```java
public record PedidoId(UUID valor) implements Identifier {           // TODO P7: ¿UUID v7 o clave natural?
  public PedidoId { Objects.requireNonNull(valor, "valor"); }
}
public interface GeneradorDeIds {                                      // puerto: el dominio no genera ids (E-14)
  PedidoId nuevoPedidoId();   // impl. en el adaptador, p. ej. UuidCreator.getTimeOrderedEpoch() (uuid-creator, MIT)
}
```

**T2 · Objetos de valor** (basado en 9.4 y 9.5; Bloch ítem 17)

```java
public record Dinero(BigDecimal monto, Currency moneda) implements ValueObject {
  public Dinero {
    Objects.requireNonNull(monto, "monto");
    Objects.requireNonNull(moneda, "moneda");
    if (monto.signum() < 0) throw new MontoNegativo(monto);                 // TODO P8: rango válido
    monto = monto.setScale(moneda.getDefaultFractionDigits(), RoundingMode.HALF_EVEN); // equals estable (E-09)
  }
  public static Dinero cero(Currency moneda) { return new Dinero(BigDecimal.ZERO, moneda); }
  public Dinero mas(Dinero otro) { exigirMisma(otro); return new Dinero(monto.add(otro.monto), moneda); }
  public Dinero por(Cantidad c) { return new Dinero(monto.multiply(BigDecimal.valueOf(c.valor())), moneda); }
  private void exigirMisma(Dinero otro) { if (!moneda.equals(otro.moneda)) throw new MonedasDistintas(moneda, otro.moneda); }
}
public record Cantidad(int valor) implements ValueObject {
  public Cantidad { if (valor <= 0) throw new CantidadInvalida(valor); }
}
public record LineaPedido(Sku sku, Cantidad cantidad, Dinero precioUnitario) implements ValueObject {
  public Dinero subtotal() { return precioUnitario.por(cantidad); }
}
```

**T3 · Estados sellados** (basado en 9.2; Wlaschin)

```java
public sealed interface EstadoPedido {                                  // TODO P10: ¿datos por estado?
  record Borrador() implements EstadoPedido {}
  record Confirmado(Instant confirmadoEn) implements EstadoPedido {}
  record Despachado(Instant confirmadoEn, EnvioId envio) implements EstadoPedido {}
  record Cancelado(MotivoCancelacion motivo, Instant canceladoEn) implements EstadoPedido {} // TODO P12: terminal
}
```

**T4 · Eventos de dominio** (Evans, *DDD Reference* p. 12: inmutables, con instante e ids)

```java
public sealed interface EventoPedido extends DomainEvent {               // org.jmolecules.event.types
  PedidoId pedido();
  Instant ocurrioEn();
}
public record PedidoConfirmado(PedidoId pedido, Dinero total, Instant ocurrioEn) implements EventoPedido {}
public record PedidoCancelado(PedidoId pedido, MotivoCancelacion motivo, Instant ocurrioEn) implements EventoPedido {}
// TODO P18: carga útil. El id del sobre y la deduplicación los define evento-mensaje.
```

**T5 · Errores de negocio** (E-11; el adaptador web los traduce a RFC 9457)

```java
public abstract sealed class ErrorDePedido extends RuntimeException
    permits PedidoVacio, LimiteDeLineasExcedido, TransicionInvalida {     // TODO P16: ¿excepción o Resultado?
  protected ErrorDePedido(String mensaje) { super(mensaje); }
}
public final class PedidoVacio extends ErrorDePedido {
  public PedidoVacio(PedidoId id) { super("El pedido %s no tiene líneas".formatted(id.valor())); }
}
public final class TransicionInvalida extends ErrorDePedido {
  public TransicionInvalida(PedidoId id, EstadoPedido actual, String comando) {
    super("El pedido %s en %s no admite %s".formatted(id.valor(), actual.getClass().getSimpleName(), comando));
  }
}
```

**T6 · Raíz de agregado** (basado en 9.3, 9.6 y 9.7; estilo P15-1)

```java
public final class Pedido implements AggregateRoot<Pedido, PedidoId> {
  static final int MAX_LINEAS = 50;                                      // TODO P6
  private final PedidoId id;
  private final ClienteId cliente;                                       // por id (E-04) · TODO P9
  private final List<LineaPedido> lineas;
  private EstadoPedido estado;
  private final long version;                                            // la usa el adaptador (E-15) · TODO P19
  private final List<EventoPedido> eventos = new ArrayList<>();

  private Pedido(PedidoId id, ClienteId cliente, List<LineaPedido> lineas, EstadoPedido estado, long version) {
    this.id = Objects.requireNonNull(id); this.cliente = Objects.requireNonNull(cliente);
    this.lineas = new ArrayList<>(lineas); this.estado = Objects.requireNonNull(estado); this.version = version;
  }
  public static Pedido abrir(PedidoId id, ClienteId cliente) {           // TODO P13
    return new Pedido(id, cliente, List.of(), new EstadoPedido.Borrador(), 0L);
  }
  public static Pedido reconstituir(PedidoId id, ClienteId cliente, List<LineaPedido> lineas,
                                    EstadoPedido estado, long version) {  // TODO P14: sin reglas ni eventos
    return new Pedido(id, cliente, lineas, estado, version);
  }

  public void agregarLinea(LineaPedido linea) {
    if (!(estado instanceof EstadoPedido.Borrador)) throw new TransicionInvalida(id, estado, "agregarLinea");
    if (lineas.size() == MAX_LINEAS) throw new LimiteDeLineasExcedido(id, MAX_LINEAS);   // I2
    lineas.add(linea);
  }
  public void confirmar(Instant ahora) {                                 // TODO P11: transiciones prohibidas
    switch (estado) {
      case EstadoPedido.Borrador b when lineas.isEmpty() -> throw new PedidoVacio(id);   // I1
      case EstadoPedido.Borrador b -> {
        estado = new EstadoPedido.Confirmado(ahora);
        eventos.add(new PedidoConfirmado(id, total(), ahora));
      }
      case EstadoPedido.Confirmado c -> { }                              // repetido: idempotente
      case EstadoPedido.Despachado d -> throw new TransicionInvalida(id, estado, "confirmar");
      case EstadoPedido.Cancelado x -> throw new TransicionInvalida(id, estado, "confirmar");
    }
  }
  public Dinero total() {
    return lineas.stream().map(LineaPedido::subtotal).reduce(Dinero::mas)
        .orElseGet(() -> Dinero.cero(Currency.getInstance("COP")));     // TODO P8: moneda del pedido
  }
  public List<EventoPedido> extraerEventos() {                          // el caso de uso publica tras guardar
    var copia = List.copyOf(eventos); eventos.clear(); return copia;
  }
  @Override public PedidoId getId() { return id; }
  public ClienteId cliente() { return cliente; }
  public EstadoPedido estado() { return estado; }
  public List<LineaPedido> lineas() { return List.copyOf(lineas); }      // E-16
  public long version() { return version; }
  @Override public boolean equals(Object o) { return o instanceof Pedido p && id.equals(p.id); }   // E-09
  @Override public int hashCode() { return id.hashCode(); }
}
```

**T7 · Especificación con Composite** (basado en 9.4, con `no()` corregido)

```java
@FunctionalInterface
public interface Especificacion<T> {                                    // TODO P17: ¿hace falta?
  boolean esSatisfechaPor(T candidato);
  default Especificacion<T> y(Especificacion<T> otra) { return c -> esSatisfechaPor(c) && otra.esSatisfechaPor(c); }
  default Especificacion<T> o(Especificacion<T> otra) { return c -> esSatisfechaPor(c) || otra.esSatisfechaPor(c); }
  default Especificacion<T> no() { return c -> !esSatisfechaPor(c); }
}
public record DespachableEnZona(Set<CodigoZona> zonasCubiertas) implements Especificacion<DireccionEntrega>, ValueObject {
  public DespachableEnZona { zonasCubiertas = Set.copyOf(zonasCubiertas); }
  public boolean esSatisfechaPor(DireccionEntrega d) { return zonasCubiertas.contains(d.zona()); }
}
```

**T8 · Políticas intercambiables y decisión funcional** (basado en 9.1; alternativa P15-2 y P16-2)

```java
@FunctionalInterface
public interface PoliticaDeConfirmacion {                               // Strategy (Evans cap. 12)
  Optional<MotivoRechazo> evaluar(Pedido pedido, PerfilDeCliente perfil);
}
public sealed interface Resultado<E> {
  record Aceptado<E>(List<E> eventos) implements Resultado<E> {}
  record Rechazado<E>(MotivoRechazo motivo) implements Resultado<E> {}
}
// En la raíz, si se elige el estilo funcional:
public Resultado<EventoPedido> decidirConfirmacion(PerfilDeCliente perfil, List<PoliticaDeConfirmacion> politicas,
                                                   Instant ahora) {
  return politicas.stream().map(p -> p.evaluar(this, perfil)).flatMap(Optional::stream).findFirst()
      .<Resultado<EventoPedido>>map(Resultado.Rechazado::new)
      .orElseGet(() -> new Resultado.Aceptado<>(List.of(new PedidoConfirmado(id, total(), ahora))));
}
```

**T9 · Pruebas del modelo y reglas de arquitectura** (Evans, *Assertions*; jMolecules ArchUnit)

```java
class PedidoTest {
  static final Instant T0 = Instant.parse("2026-01-10T12:00:00Z");
  static final PedidoId ID = new PedidoId(UUID.fromString("01928c1e-0000-7000-8000-000000000001"));
  static final ClienteId CLIENTE = new ClienteId(UUID.fromString("01928c1e-0000-7000-8000-0000000000c1"));

  @Test void noConfirmaSinLineas() {                                     // I1 · viola
    var pedido = Pedido.abrir(ID, CLIENTE);                                          // Dado
    assertThatThrownBy(() -> pedido.confirmar(T0)).isInstanceOf(PedidoVacio.class); // Cuando / Entonces
    assertThat(pedido.extraerEventos()).isEmpty();
  }
  @Test void confirmaConLineasYRegistraEvento() {                        // I1 · cumple
    var pedido = Pedido.abrir(ID, CLIENTE);
    pedido.agregarLinea(new LineaPedido(new Sku("ROSA-R"), new Cantidad(24), Dinero.cero(Currency.getInstance("COP"))));
    pedido.confirmar(T0);
    assertThat(pedido.extraerEventos()).singleElement().isInstanceOf(PedidoConfirmado.class);
  }
  @Test void igualdadPorId() {                                           // E-09
    var a = Pedido.abrir(ID, CLIENTE);
    var b = Pedido.reconstituir(ID, CLIENTE, List.of(), new EstadoPedido.Cancelado(null, T0), 3);
    assertThat(a).isEqualTo(b).hasSameHashCodeAs(b);
  }
}

@AnalyzeClasses(packages = "com.ejemplo.pedidos")
class ReglasDelDominioTest {
  @ArchTest static final ArchRule ddd = JMoleculesDddRules.all();        // E-04, E-19, E-20
  @ArchTest static final ArchRule dominioPuro = noClasses().that().resideInAPackage("..domain..")
      .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta..", "com.fasterxml.."); // E-01
  @ArchTest static final ArchRule sinErroresGenericos = GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS; // E-11
}
```

## 11 · Qué se genera

Orden dentro del segmento (de las hojas a la raíz; cada paso compila solo):

1. Ids: `PedidoId`, `ClienteId`, `EnvioId` (T1).
2. Valores: `Dinero`, `Cantidad`, `Sku`, `LineaPedido` (T2), con sus pruebas de construcción inválida.
3. Errores: `ErrorDePedido` y sus subclases (T5).
4. Estados: `EstadoPedido` (T3).
5. Eventos: `EventoPedido` y sus records (T4). Pasa a `evento-mensaje`.
6. Raíz: `Pedido` (T6), con una prueba dado/cuando/entonces por invariante y por transición prohibida.
7. Opcionales: `Especificacion` y sus implementaciones (T7), y las políticas (T8).
8. Reglas: `ReglasDelDominioTest` (T9).

Salen hacia otras plantillas: la firma de `reconstituir(...)` y `version()` para `adaptador-persistencia`;
los records de `EventoPedido` para `evento-mensaje`; el catálogo de errores para la traducción a
RFC 9457 del adaptador de entrada.
Dependencias de prueba: JUnit 5, AssertJ, ArchUnit y `jmolecules-archunit`. EqualsVerifier es opcional
para los valores.

## 12 · Referencias

- Evans, *Domain-Driven Design* (2003). Cap. 5: Entities, Value Objects, Services. Cap. 6: Aggregates,
  Factories, Repositories. Cap. 9: Specification. Cap. 10: Supple Design. Cap. 12: Strategy y Composite
  en el dominio. https://www.domainlanguage.com/ddd/
- Evans, *DDD Reference* (2015). Los «Therefore» de Entities (p. 10), Value Objects (p. 11), Domain Events
  (p. 12), Aggregates (p. 15), Factories (p. 17) y Assertions (p. 21).
  https://www.domainlanguage.com/ddd/reference/
- Vernon, *Effective Aggregate Design* I-III (2011). Las cuatro reglas, «¿de quién es el trabajo?», las
  razones para romperlas y la versión optimista. https://www.dddcommunity.org/library/vernon_2011/
- Vernon, *Implementing Domain-Driven Design* (2013). Identidad generada por la aplicación, eventos y
  repositorios. https://www.oreilly.com/library/view/implementing-domain-driven-design/9780133039900/
- Wlaschin, *Domain Modeling Made Functional* (2018). Los flujos como funciones y los estados como tipos.
  https://pragprog.com/titles/swdddf/domain-modeling-made-functional/
- Wlaschin, *Making illegal states unrepresentable* (frase de Yaron Minsky).
  https://fsharpforfunandprofit.com/posts/designing-with-types-making-illegal-states-unrepresentable/
- Fowler, *ValueObject*: inmutabilidad y bugs por aliasing. https://martinfowler.com/bliki/ValueObject.html
- Fowler, *AnemicDomainModel*: los síntomas del modelo anémico. https://martinfowler.com/bliki/AnemicDomainModel.html
- Fowler, *DDD_Aggregate*: las referencias solo a la raíz y la transacción dentro del agregado.
  https://martinfowler.com/bliki/DDD_Aggregate.html
- Evans y Fowler, *Specifications*: validación, selección, construcción a pedido y subsunción.
  https://martinfowler.com/apsupp/spec.pdf
- DDD Crew (Kacper Gunia), *Aggregate Design Canvas* v1.1 (CC BY 4.0), con sus nueve secciones,
  throughput y tamaño. https://github.com/ddd-crew/aggregate-design-canvas ·
  https://domaincentric.net/blog/modelling-business-rules-invariants-vs-corrective-policies
- Khononov, *Learning Domain-Driven Design* (2021). Cap. 5: transaction script y active record. Cap. 6:
  domain model. Cap. 7: event sourcing. Cap. 10: heurísticas de diseño. Solo se verificó el índice.
  https://www.oreilly.com/library/view/learning-domain-driven-design/9781098100124/
- Gamma et al., *Design Patterns* (GoF, 1994): State, Strategy, Factory Method y Composite.
  https://refactoring.guru/design-patterns/state
- Bloch, *Effective Java* (3.ª ed., 2018). Ítem 1 (fábricas estáticas), 10-11 (equals y hashCode),
  17 (minimizar la mutabilidad), 50 (copias defensivas), 73 (excepciones según la abstracción).
- IETF RFC 9562. §5.7: UUIDv7. §6.13: localidad en índices. §8: usar v4 cuando el UUID tiene uso de
  seguridad. https://www.rfc-editor.org/rfc/rfc9562.html
- jMolecules: `@AggregateRoot`, `@ValueObject`, `@Identity`, `AggregateRoot<T,ID>`, `Identifier`,
  `Association`, `DomainEvent` (Apache-2.0). https://github.com/xmolecules/jmolecules
- jMolecules ArchUnit, `JMoleculesDddRules`.
  https://github.com/xmolecules/jmolecules-integrations/tree/main/jmolecules-archunit
- Spring Data, *Publishing Events from Aggregate Roots*: `@DomainEvents`, `AbstractAggregateRoot`.
  https://docs.spring.io/spring-data/commons/reference/repositories/core-domain-events.html
- Spring Modulith, *Events*: `@ApplicationModuleListener` y el Event Publication Registry.
  https://docs.spring.io/spring-modulith/reference/events.html
- Hibernate ORM 6.6 User Guide, §3.4.7: *Implementing equals() and hashCode()*.
  https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/Hibernate_User_Guide.html
- uuid-creator (MIT), `UuidCreator.getTimeOrderedEpoch()` para v7. https://github.com/f4b6a3/uuid-creator
- EqualsVerifier, para probar el contrato de equals y hashCode. https://jqno.nl/equalsverifier/
