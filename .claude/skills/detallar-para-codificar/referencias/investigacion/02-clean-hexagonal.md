# 02 · Clean Architecture, Hexagonal (Ports & Adapters), Onion y código sano

> Insumo para la skill **DetallarParaCodificar**. Alimenta el componente del índice de precisión
> llamado "adherencia a Clean Architecture y prácticas de código sano" (25 %) y la etapa de verificación.
> Lo marcado con **[opinión]** es interpretación del investigador, no de las fuentes.

---

## 1. Qué es

- **Hexagonal / Ports & Adapters (Cockburn, 2005).** El objetivo es que la aplicación pueda "ser manejada
  igualmente por usuarios, programas, pruebas automatizadas o scripts batch, y desarrollarse y probarse
  aislada de sus bases de datos y dispositivos". Un **puerto** es una conversación con propósito, definida
  como API y nombrada por su intención, no por la tecnología (Cockburn propone `For[Hacer][Algo]`:
  `ForPlacingOrders`, `ForStoringUsers`). Un **adaptador** traduce una tecnología concreta (HTTP, SQL, cola)
  a esa API. Hay dos lados:
  - **Primario / driving**: el actor inicia la conversación (UI, REST, test, batch).
  - **Secundario / driven**: la aplicación inicia la conversación (BD, correo, API externa).
  - **Configurable dependency**: la aplicación depende de una abstracción. Un configurador (en Spring, el
    contenedor de inyección) decide qué adaptador real o de prueba se conecta.
- **Clean Architecture (Martin, 2012/2017).** Hay círculos concéntricos: Entities, Use Cases, Interface
  Adapters y Frameworks & Drivers. Su regla central es la **regla de dependencias**: "las dependencias de
  código fuente solo apuntan hacia adentro" y "ningún nombre de un círculo externo puede mencionarse en uno
  interno". Por las fronteras cruzan **estructuras de datos simples (DTOs)** con la forma más cómoda para el
  círculo interno, nunca entidades ni filas de BD. "La web es un detalle. La base de datos es un detalle."
- **Onion (Palermo, 2008).** El modelo de dominio va en el centro, rodeado de servicios de dominio y
  servicios de aplicación. La infraestructura, la UI y las pruebas van en el anillo externo. Las interfaces
  (repositorios) se declaran en el núcleo y se implementan en el borde. Palermo la recomienda para
  aplicaciones de negocio longevas y complejas, no para sitios pequeños.
- **Explicit Architecture (Graça, 2017)** une las tres con DDD y CQRS. Los puertos *pertenecen al núcleo* y
  se diseñan según sus necesidades, no copiando la API del proveedor. Distingue **servicios de aplicación**
  (orquestan casos de uso) de **servicios de dominio** (lógica que abarca varias entidades). Empaqueta
  **por componente** (capacidad de negocio) cortando las capas, y desacopla componentes con eventos.
- **En qué coinciden**: (1) el dominio y los casos de uso no conocen frameworks, BD ni HTTP; (2) se aplica
  inversión de dependencias, de modo que el núcleo define interfaces y el borde las implementa; (3) se
  traduce en las fronteras; (4) el núcleo se prueba sin infraestructura. **[opinión]** Son la misma idea con
  distinto vocabulario. Hexagonal habla de simetría entrada/salida, Clean de capas y regla de dependencias,
  y Onion de anillos alrededor del modelo.

## 2. Flujo de entrada a salida

```
  [Cliente HTTP]
       │ JSON
       ▼
┌─ ADAPTADOR PRIMARIO (infra/entrada) ───────────────────────────────┐
│ PedidoController (@RestController)                                 │
│   recibe CrearPedidoRequest (DTO web: @Valid, @JsonProperty)       │
│   MAPEA Request → CrearPedidoCommand  (mapeo #1, en el adaptador)  │
│   MAPEA Resultado → PedidoResponse    (mapeo #6, en el adaptador)  │
└───────────────┬────────────────────────────────────────────────────┘
                │ llama a la interfaz
                ▼
┌─ NÚCLEO: APLICACIÓN ───────────────────────────────────────────────┐
│ «puerto de entrada» CrearPedidoUseCase  (interface)                │
│   CrearPedidoCommand (record inmutable, se autovalida)             │
│ CrearPedidoService implements CrearPedidoUseCase                   │
│   orquesta: carga, invoca dominio, persiste, publica evento        │
│   transacción (@Transactional aquí o en un decorador)              │
│ «puerto de salida» CargarPedidoPort / GuardarPedidoPort (interface)│
└───────┬───────────────────────────────────────────▲────────────────┘
        │ usa                                       │ implementa
        ▼                                           │
┌─ NÚCLEO: DOMINIO ────────────────┐   ┌─ ADAPTADOR SECUNDARIO (infra/salida) ─┐
│ Pedido (agregado, invariantes)   │   │ PedidoPersistenceAdapter             │
│ LineaPedido, Dinero (VO)         │   │   implements GuardarPedidoPort       │
│ PedidoCreado (evento de dominio) │   │   MAPEA Pedido ↔ PedidoJpaEntity     │
│ reglas y excepciones de negocio  │   │   (mapeos #3 y #4, en el adaptador)  │
│ SIN Spring, JPA, Jackson         │   │   usa PedidoJpaRepository (Spring)   │
└──────────────────────────────────┘   └───────────────┬───────────────────────┘
                                                       ▼
                                                  [PostgreSQL]
```

**Dónde vive cada pieza y quién mapea.** Ejemplo de paquete por módulo: `pedidos/`.

| Pieza | Paquete sugerido | Conoce a | Mapea |
|---|---|---|---|
| Controlador / listener / job (adaptador de entrada) | `pedidos.adapter.in.web` | puerto de entrada, DTO web, Command | Request→Command, Resultado→Response |
| DTO web de entrada y salida | `pedidos.adapter.in.web` | nada del dominio (solo primitivos y Strings) | — |
| Puerto de entrada (caso de uso) | `pedidos.application.port.in` | Command, tipos de dominio o de resultado | — |
| Command / Query (modelo de entrada) | `pedidos.application.port.in` | VO de dominio, nada de web | valida en constructor |
| Servicio de aplicación | `pedidos.application.service` | puertos in/out, dominio | — (puede armar el resultado) |
| Dominio (agregados, VO, eventos, servicios de dominio) | `pedidos.domain` | solo dominio y JDK | — |
| Puerto de salida | `pedidos.application.port.out` | tipos de dominio | — |
| Adaptador de salida | `pedidos.adapter.out.persistence` | puerto out, dominio, JPA | Dominio↔Entidad JPA |
| Entidad JPA / DTO de cliente HTTP externo | `pedidos.adapter.out.*` | framework | — |
| Configuración (wiring) | `pedidos.config` o `@Configuration` | todo | — |

**Estrategias de mapeo (Hombergs, cap. "Mapping Between Boundaries")**:
- *No mapping*: un solo modelo en todas las capas. Es barato, pero el dominio se llena de anotaciones JPA y
  JSON. Solo sirve para CRUD puro.
- *Two-way*: cada capa tiene su modelo y el adaptador mapea hacia y desde el dominio. Es el estándar
  recomendado.
- *Full*: un modelo de entrada por operación (`CrearPedidoCommand`, `CancelarPedidoCommand`). Se recomienda
  en la frontera web→aplicación, porque deja explícito qué pide cada caso de uso.
- *One-way*: todos los modelos implementan una interfaz común de solo lectura. Se usa poco.
- **[opinión]** Por defecto: *Full* en la entrada (Command por caso de uso), *Two-way* en persistencia y
  *No mapping* en consultas de solo lectura, que proyectan directo a un DTO de lectura.

## 3. Lista de verificación de prácticas sanas

Severidad y peso: **C** = crítico (peso 3), **M** = mayor (peso 2), **m** = menor (peso 1).
Comprobación: **L** = lectura del detalle o código, **A** = regla ArchUnit, jMolecules o Spring Modulith,
**P** = prueba.

| # | Ítem verificable | Cómo se comprueba | Sev. |
|---|---|---|---|
| 1 | El dominio no importa Spring, JPA, Jackson ni `jakarta.*` | A: `noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat().resideInAnyPackage("org.springframework..","jakarta..","com.fasterxml..")` | C |
| 2 | Regla de dependencias: domain ← application ← adapter, sin flechas inversas | A: `onionArchitecture().domainModels(..).domainServices(..).applicationServices(..).adapter(..)` | C |
| 3 | Ningún adaptador depende de otro adaptador | A: incluido en `onionArchitecture()` (adapters aislados entre sí) | M |
| 4 | Cada caso de uso tiene un puerto de entrada (interface) que el controlador invoca | L + A: los controladores solo dependen de `..port.in..` | M |
| 5 | Toda E/S externa (BD, HTTP, correo, reloj, colas) pasa por un puerto de salida declarado en el núcleo | L: listar dependencias externas del caso de uso; A: `application` no depende de `..adapter..` | C |
| 6 | Los puertos se nombran por intención de negocio, no por tecnología (`GuardarPedidoPort`, no `PedidoJpaDao`) | L: buscar `Jpa`, `Sql`, `Rest`, `Kafka` en nombres de `port` | m |
| 7 | Ninguna entidad JPA ni DTO web cruza hacia el núcleo | A: `..application..` y `..domain..` no dependen de `..adapter..`; L: firmas de puertos | C |
| 8 | Hay un Command o Query inmutable por caso de uso y se autovalida | L: `record` con validación en constructor compacto; P: test de construcción inválida | M |
| 9 | La validación sintáctica va en el adaptador o Command y las invariantes de negocio en el dominio | L: reglas de negocio dentro de agregados o VO, no en el controlador | M |
| 10 | El dominio es rico: los agregados protegen sus invariantes, sin setters públicos | L; A: `noMethods().that().areDeclaredInClassesThat().resideInAPackage("..domain..").should().haveNameMatching("set.*")` (ajustable) | M |
| 11 | El servicio de aplicación orquesta y no calcula reglas (sin `if` de negocio extensos) | L: método ≤ ~20 líneas, delega en el dominio | M |
| 12 | Errores explícitos: excepciones de dominio con nombre del lenguaje ubicuo o tipo resultado; sin `throw new RuntimeException/Exception` | A: `GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS`; L: catálogo de errores | M |
| 13 | El adaptador de entrada traduce los errores de dominio a protocolo (HTTP 4xx/5xx, `ProblemDetail`) en un solo lugar | L: `@RestControllerAdvice` por módulo; P: test web con `@WebMvcTest` | M |
| 14 | Solo inyección por constructor, sin `@Autowired` en campos | A: `GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION` | m |
| 15 | El núcleo se prueba sin Spring ni BD (unit tests puros con fakes de puertos de salida) | P: tests del servicio sin `@SpringBootTest` que corren en < 1 s | C |
| 16 | Cada adaptador tiene su prueba de integración enfocada (`@DataJpaTest`, `@WebMvcTest`, Testcontainers) | P: existe un test por adaptador | M |
| 17 | Nombres del lenguaje ubicuo: clases y métodos usan términos del glosario del negocio | L: cruzar contra el glosario; evitar `Manager`, `Helper`, `Util`, `Data` | M |
| 18 | Sin ciclos entre paquetes ni módulos | A: `slices().matching("..(*)..").should().beFreeOfCycles()`; Modulith `verify()` | C |
| 19 | Los módulos solo se acceden por su API pública o *named interface* | A: `ApplicationModules.of(App.class).verify()` | C |
| 20 | Transacción en el límite del caso de uso (servicio de aplicación), nunca en controlador ni dominio | L; A: `@Transactional` solo en `..application..` o `..adapter.out..` | M |
| 21 | Se usa tiempo e identidad inyectables (`Clock`, generador de ID vía puerto), sin `LocalDateTime.now()` en el dominio | L; A: dominio no llama `now()` | m |
| 22 | SRP / ISP: los puertos son pequeños (≤ ~5 métodos) y cohesivos, sin "repositorio dios" | L: contar métodos por puerto | m |
| 23 | Los eventos entre módulos se publican desde la aplicación, sin llamadas directas a servicios internos de otro módulo | A: Modulith; L: `ApplicationEventPublisher` o `@ApplicationModuleListener` | M |
| 24 | Sin `System.out` ni `java.util.logging`; logging en adaptadores y aplicación, no en el dominio | A: `NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS`, `NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING` | m |
| 25 | Las reglas de arquitectura están en el build (falla CI si se violan) | P: existe `ArchitectureTest` o `ModularityTests` y se ejecuta en `mvn verify` | M |
| 26 | Las abstracciones están justificadas: no hay interfaces para helpers internos sin E/S | L: cada interface tiene ≥ 2 implementaciones o es un puerto real | m |

**Fórmula de puntuación (0-100) para el 25 %**:

```
ítems_aplicables = ítems de la lista que aplican al elemento (descarta los N/A justificados)
puntaje_ítem     = 1 cumple · 0,5 cumple parcial o solo declarado sin verificación automática · 0 no cumple
Adherencia       = 100 × Σ(peso_i × puntaje_i) / Σ(peso_i)        (pesos C=3, M=2, m=1)
Tope por críticos: si algún ítem C = 0  → Adherencia = min(Adherencia, 60)
                   si dos o más C = 0   → Adherencia = min(Adherencia, 40)
Contribución al índice = 0,25 × Adherencia
```

**[opinión]** El tope evita que muchos ítems menores tapen una violación de la regla de dependencias. En la
fase de *detalle* (antes de que exista código) se puntúa si el ítem **está decidido y escrito**. En la fase
de *verificación* se puntúa si **está comprobado por A o P**. Un ítem solo leído (L) en verificación vale 0,5.

## 4. Elementos prioritarios al detallar un módulo hexagonal

Orden recomendado. Cada elemento puede ser una sección de la "plantilla de ejemplo".

1. **Propósito y límite del módulo**, en lenguaje ubicuo: qué capacidad de negocio posee y qué datos le
   pertenecen. *Por qué*: sin límite no se sabe qué es núcleo y qué es externo (Graça: un componente solo
   modifica sus datos).
2. **Actores primarios y casos de uso** (puertos de entrada): quién lo dispara y con qué verbo
   (`ForRegistrandoPedidos`). *Por qué*: Cockburn y Martin centran todo en los casos de uso, y cada uno
   será una interface.
3. **Modelo de entrada por caso de uso** (Command o Query): campos, tipos, validaciones sintácticas.
   *Por qué*: es el contrato que fija el mapeo "Full".
4. **Modelo de dominio**: agregados, entidades, VO, invariantes y eventos de dominio. *Por qué*: es el
   corazón, y su forma decide si el servicio de aplicación queda delgado.
5. **Reglas de negocio e invariantes** enumeradas, con el dueño de cada una (agregado, VO o servicio de
   dominio). *Por qué*: evita el dominio anémico y la lógica en controladores.
6. **Actores secundarios y puertos de salida**: qué necesita el núcleo del exterior (persistir, notificar,
   consultar otro módulo, reloj). *Por qué*: aquí se aplica la configurable dependency y se habilitan las
   pruebas con fakes.
7. **Catálogo de errores**: errores de negocio con nombre, cómo se representan (excepción tipada o
   resultado) y su traducción a protocolo. *Por qué*: el manejo explícito de errores es verificable y suele
   olvidarse.
8. **Modelo de salida y respuesta**: qué devuelve el caso de uso y cómo lo presenta el adaptador.
9. **Adaptadores concretos**: tecnología (REST, JPA, Kafka), entidad de persistencia y estrategia de mapeo.
   *Por qué*: van al final porque son detalle (Martin), aunque fijan las pruebas de integración.
10. **Transacción, consistencia y eventos entre módulos**: dónde se abre la transacción, qué se publica y
    quién escucha.
11. **Estrategia de pruebas**: unidad del núcleo con fakes, slice por adaptador y reglas ArchUnit o
    Modulith.
12. **Reglas de arquitectura a automatizar**: qué ítems de la sección 3 entran en `ArchitectureTest`.

## 5. Banco de preguntas cortas (de general a detallado)

Formato: pregunta · opciones típicas · mini ejemplo.

**G1. ¿Qué capacidad de negocio posee este módulo?**
Opciones: (a) gestionar un agregado propio; (b) orquestar otros módulos; (c) solo consulta o reporte.
```java
@ApplicationModule(displayName = "Pedidos")
package com.flor.pedidos;
// posee: Pedido, LineaPedido; no modifica Inventario
```

**G2. ¿Qué estilo de complejidad tiene el módulo?**
Opciones: (a) reglas ricas → hexagonal completo; (b) CRUD → capas simples; (c) mixto → CQRS liviano.
```java
// (b) CRUD: el controlador llama a un repositorio Spring Data, sin puertos.
// (a) reglas: Controller → CrearPedidoUseCase → Pedido → GuardarPedidoPort
```

**G3. ¿Quién dispara este caso de uso (actor primario)?**
Opciones: (a) usuario vía REST; (b) otro módulo vía evento; (c) job programado; (d) mensaje de cola.
```java
@ApplicationModuleListener
void on(PagoConfirmado e) { confirmarPedido.confirmar(new ConfirmarPedidoCommand(e.pedidoId())); }
```

**G4. ¿Cómo se llama el caso de uso en lenguaje del negocio?**
Opciones: (a) verbo + sustantivo (`RegistrarPedido`); (b) `For...` estilo Cockburn; (c) Command handler.
```java
public interface RegistrarPedidoUseCase {
    PedidoId registrar(RegistrarPedidoCommand cmd);
}
```

**D1. ¿Qué datos de entrada exige el caso de uso?**
Opciones: (a) record Command dedicado; (b) parámetros primitivos; (c) reutilizar DTO web (desaconsejado).
```java
public record RegistrarPedidoCommand(ClienteId cliente, List<LineaCmd> lineas) {
    public RegistrarPedidoCommand {
        if (lineas == null || lineas.isEmpty()) throw new IllegalArgumentException("sin líneas");
    }
}
```

**D2. ¿Dónde se valida cada regla?**
Opciones: (a) formato en DTO web con `@Valid`; (b) consistencia en Command; (c) invariante en el agregado.
```java
public void agregarLinea(Producto p, Cantidad c) {
    if (estado != Estado.BORRADOR) throw new PedidoNoEditable(id);
    lineas.add(new LineaPedido(p, c));
}
```

**D3. ¿Cuál es el agregado raíz y su identidad?**
Opciones: (a) ID tipado (VO); (b) UUID crudo; (c) Long de BD (desaconsejado en el dominio).
```java
public record PedidoId(UUID valor) {
    public static PedidoId nuevo() { return new PedidoId(UUID.randomUUID()); }
}
```

**D4. ¿Qué invariantes protege el agregado?**
Opciones: (a) totales y límites; (b) transiciones de estado; (c) unicidad (requiere puerto).
```java
public void confirmar() {
    if (estado != Estado.BORRADOR) throw new TransicionInvalida(estado, Estado.CONFIRMADO);
    estado = Estado.CONFIRMADO;
}
```

**D5. ¿Qué necesita el núcleo del exterior (puertos de salida)?**
Opciones: (a) persistir o cargar; (b) notificar; (c) consultar otro módulo; (d) reloj o ID.
```java
public interface CargarPedidoPort { Optional<Pedido> cargar(PedidoId id); }
public interface GuardarPedidoPort { void guardar(Pedido p); }
```

**D6. ¿Un puerto por operación o por agregado?**
Opciones: (a) puertos finos por intención (ISP); (b) un repositorio por agregado; (c) separar lectura y escritura.
```java
class PedidoPersistenceAdapter implements CargarPedidoPort, GuardarPedidoPort { /* ... */ }
```

**D7. ¿Cómo se representan los errores de negocio?**
Opciones: (a) excepción tipada de dominio; (b) tipo resultado sellado; (c) `Optional` para "no encontrado".
```java
public sealed interface ResultadoRegistro permits Registrado, StockInsuficiente {}
public record Registrado(PedidoId id) implements ResultadoRegistro {}
public record StockInsuficiente(ProductoId p) implements ResultadoRegistro {}
```

**D8. ¿Cómo se traduce cada error al protocolo?**
Opciones: (a) `@RestControllerAdvice` con `ProblemDetail`; (b) `switch` sobre el resultado sellado en el controlador.
```java
@ExceptionHandler(PedidoNoEditable.class)
ProblemDetail noEditable(PedidoNoEditable e) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
}
```

**D9. ¿Qué devuelve el caso de uso?**
Opciones: (a) solo ID; (b) DTO de resultado de aplicación; (c) nada (comando puro); evitar devolver el agregado.
```java
@PostMapping ResponseEntity<PedidoResponse> crear(@Valid @RequestBody CrearPedidoRequest r) {
    PedidoId id = registrar.registrar(mapper.toCommand(r));
    return ResponseEntity.created(URI.create("/pedidos/" + id.valor())).build();
}
```

**D10. ¿Qué estrategia de mapeo con persistencia?**
Opciones: (a) Two-way (entidad JPA separada); (b) No mapping (JPA en dominio); (c) proyección directa para lectura.
```java
PedidoJpaEntity toEntity(Pedido p) { return new PedidoJpaEntity(p.id().valor(), p.estado().name()); }
Pedido toDomain(PedidoJpaEntity e) { return Pedido.reconstituir(new PedidoId(e.getId()), Estado.valueOf(e.getEstado())); }
```

**D11. ¿Dónde se abre la transacción?**
Opciones: (a) en el servicio de aplicación; (b) en el adaptador de salida; (c) decorador transaccional.
```java
@Service @Transactional
class RegistrarPedidoService implements RegistrarPedidoUseCase { /* ... */ }
```

**D12. ¿Cómo se comunica con otros módulos?**
Opciones: (a) evento de aplicación asíncrono; (b) puerto de salida hacia la API pública del otro módulo; (c) consulta de lectura.
```java
eventos.publishEvent(new PedidoRegistrado(pedido.id().valor()));
```

**D13. ¿Cómo se prueba el núcleo sin infraestructura?**
Opciones: (a) fakes en memoria de puertos; (b) Mockito; (c) ambos.
```java
var repo = new PedidosEnMemoria();
var svc = new RegistrarPedidoService(repo, repo, Clock.fixed(T0, UTC));
assertThat(svc.registrar(cmdValido())).isNotNull();
```

**D14. ¿Qué reglas de arquitectura se automatizan?**
Opciones: (a) Modulith `verify()`; (b) ArchUnit `onionArchitecture()`; (c) jMolecules `ensureHexagonal`.
```java
@Test void modulos() { ApplicationModules.of(FlorLogicApplication.class).verify(); }
@ArchTest static final ArchRule capas = onionArchitecture()
    .domainModels("..domain..").applicationServices("..application..")
    .adapter("web", "..adapter.in.web..").adapter("db", "..adapter.out.persistence..");
```

**D15. ¿Cómo se marcan los roles arquitectónicos?**
Opciones: (a) convención de paquetes; (b) anotaciones jMolecules; (c) ambas.
```java
@PrimaryPort public interface RegistrarPedidoUseCase { /* ... */ }
@SecondaryAdapter class PedidoPersistenceAdapter implements GuardarPedidoPort { /* ... */ }
```

## 6. Antipatrones y cuándo no aplicar

| Antipatrón | Señal detectable |
|---|---|
| Infraestructura filtrada al dominio | `import jakarta.persistence` o `org.springframework` en `..domain..`; `@Entity` o `@JsonProperty` en agregados |
| Dominio anémico | agregados con solo getters y setters; `XxxService.cambiarEstado()` hace `pedido.setEstado(...)` |
| Caso de uso "gordo" | el servicio de aplicación tiene muchos `if` de negocio o cálculos; métodos de > 40 líneas |
| Controlador con lógica | `if` de reglas o acceso a repositorio desde `@RestController` |
| Entidad JPA o DTO web en firmas de puertos | `GuardarPedidoPort.guardar(PedidoJpaEntity)`; `UseCase.ejecutar(CrearPedidoRequest)` |
| Puertos con nombre tecnológico o copiados del proveedor | `PedidoJpaPort`, `S3Port` con métodos de SDK (Graça: el puerto se diseña según el núcleo) |
| Sobreabstracción | interfaces con una sola implementación para helpers sin E/S (`IStringHelper`, `IOrderMapper`) |
| Mapping mania | más de 3 transformaciones por operación; mappers que copian campos 1:1 sin razón |
| Adaptadores acoplados entre sí | `adapter.in.web` importa `adapter.out.persistence` |
| Ciclos entre módulos | falla `ApplicationModules.verify()`; `slices().beFreeOfCycles()` rojo |
| "Repositorio dios" | un puerto con > 10 métodos usado por todos los casos de uso |
| Lectura cargando agregados completos | queries que reconstruyen el agregado solo para mapearlo a un DTO de vista |
| Arquitectura sin pruebas | no existe `ArchitectureTest`; las reglas solo viven en un documento |
| Errores genéricos | `throw new RuntimeException("error")`; `catch (Exception e)` que traga errores |

**Cuándo NO aplicar el hexágono completo** (Rentea, Jovanović, JDriven, Palermo):
- CRUD sin reglas de negocio, prototipos, herramientas internas pequeñas o microservicios triviales.
  *Señal*: los métodos del "dominio" solo copian campos y no hay invariantes que enumerar en D4.
- Requisitos indefinidos o muy volátiles, que obligarían a redefinir puertos sin parar (JDriven).
- Un equipo sin disciplina sostenida ni dueño técnico a largo plazo: bajo presión se saltan capas y el
  resultado es peor que un código por capas simple (JDriven).
- Lado de lectura: las consultas pueden proyectar directo desde la BD sin puerto ni agregado (Jovanović).
- **[opinión]** Regla práctica para la skill: si al detallar un módulo D4 queda vacío y D5 solo tiene "guardar
  o leer", proponer *capas simples + Modulith* y marcar como N/A los ítems 4, 8, 10 y 11 de la sección 3, en
  lugar de penalizarlos. Una pregunta G2 bien respondida evita la sobreingeniería.

## 7. Fuentes

- Alistair Cockburn, "Hexagonal architecture" — https://alistair.cockburn.us/hexagonal-architecture/ — intención, puertos primarios y secundarios, adaptadores de prueba, configurable dependency.
- Cockburn, charla 2025 (PDF) — https://alistaircockburn.com/Talk%202025-11%20Hexagonal.pdf — nombrado `For_doing_something` y el "configurator" como elemento obligatorio.
- Xoubaman, "Understanding Hexagonal Architecture" — https://dev.to/xoubaman/understanding-hexagonal-architecture-3gk — ejemplos `ForPlacingOrders` y `ForStoringUsers`, driver y driven.
- Robert C. Martin, "The Clean Architecture" — https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html — regla de dependencias, círculos, DTOs simples en las fronteras.
- Robert C. Martin, *Clean Architecture* (2017, libro) — casos de uso como interactores, boundaries, SOLID a nivel de componentes.
- Jeffrey Palermo, "The Onion Architecture: part 1" — https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/ — anillos, interfaces en el núcleo, para qué tipo de sistemas.
- Herberto Graça, "Explicit Architecture" — https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/ — síntesis DDD + hexagonal + clean + CQRS; servicios de aplicación vs de dominio; empaquetado por componente.
- Tom Hombergs, *Get Your Hands Dirty on Clean Architecture* (2.ª ed.) — https://leanpub.com/get-your-hands-dirty-on-clean-architecture — layout `adapter/in`, `application/port/in|out`, Command autovalidado, cuatro estrategias de mapeo.
- Educative, "The No Mapping Strategy" — https://www.educative.io/courses/hexagonal-architecture-web-apps/the-no-mapping-strategy — dilema mapeo vs acoplamiento (curso basado en Hombergs).
- Hombergs, "Clean Boundaries with ArchUnit" — https://reflectoring.io/java-components-clean-boundaries/ — paquetes `api`/`internal`, `@Configuration` por componente, reglas ArchUnit a prueba de refactor.
- Vaughn Vernon, *Implementing Domain-Driven Design* (2013, libro) — hexagonal como arquitectura de referencia para DDD, agregados pequeños y referencias por ID.
- ArchUnit User Guide — https://www.archunit.org/userguide/html/000_Index.html — `onionArchitecture()`, `layeredArchitecture()`, `GeneralCodingRules`.
- jMolecules — https://github.com/xmolecules/jmolecules — anotaciones `@PrimaryPort`, `@SecondaryAdapter`, anillos Onion y bloques DDD.
- jMolecules ArchUnit — https://github.com/xmolecules/jmolecules-integrations/tree/main/jmolecules-archunit — reglas que verifican esas anotaciones.
- Spring Modulith, Verification — https://docs.spring.io/spring-modulith/reference/verification.html — `verify()` (ciclos, internals, `allowedDependencies`) y `JMoleculesArchitectureRules.ensureHexagonal`.
- Victor Rentea, "Overengineering in Onion/Hexagonal Architectures" — https://victorrentea.ro/blog/overengineering-in-onion-hexagonal-architectures/ — simplificar el blueprint al problema real.
- Milan Jovanović, "Clean Architecture Anti-Patterns" — https://milanjovanovic.tech/blog/clean-architecture-anti-patterns — diez antipatrones con síntomas.
- Milan Jovanović, "When to Use Clean Architecture" — https://milanjovanovic.tech/blog/when-to-use-clean-architecture — cuándo sí y cuándo no.
- JDriven, "Why you should NOT use Hexagonal architecture" — https://jdriven.com/blog/2022/10/Why-you-should-NOT-use-Hexagonal-architecture — riesgos con requisitos volátiles, equipos pequeños y presión de plazos.
