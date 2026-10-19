# C4 nivel 4 (Code): de componente a código

> Investigación para la skill `DetallarParaCodificar`. Lo marcado con **[opinión]** es interpretación propia y no viene de una fuente. Todo lo demás se apoya en las fuentes de la sección 6.

---

## 1. Qué es

- El nivel 4 del modelo C4 es el **diagrama de código**: se acerca a **un solo componente** del nivel 3 y muestra cómo está implementado. Sus elementos son los del lenguaje: clases, interfaces, enums, funciones, objetos y tablas de base de datos (c4model.com, *Code diagram* y *abstractions/code*).
- La notación es libre. Lo habitual es un diagrama de clases UML, un diagrama entidad-relación o algo parecido. Se muestran «solo los atributos y métodos que cuentan la historia».
- La fuente oficial lo declara **opcional y no recomendado** para documentación de larga vida, porque el IDE lo genera bajo demanda. Se reserva para los componentes **más importantes o complejos**.
- La relación clave es con el nivel 3. Un **componente** es «una agrupación de funcionalidad relacionada encapsulada tras una interfaz bien definida». En Java o C# eso equivale a «un conjunto de clases de implementación detrás de una interfaz». Un componente **no** es un deployable: todos los componentes de un contenedor corren en el mismo proceso.
- Los paquetes, JARs, namespaces y carpetas **no son automáticamente componentes**. El empaquetado es «una preocupación ortogonal», aunque en algunas arquitecturas el mapeo sea 1:1.
- El nivel 3 «debe mapear a abstracciones reales (p. ej. una agrupación de código) en tu código base» (InfoQ, Brown).
- Si el código no hace visibles esas abstracciones, aparece el **model-code gap** (Fairbanks): los arquitectos razonan en componentes y capas, mientras que el código solo tiene clases y paquetes.
- Brown propone cerrarlo con un **estilo de codificación arquitectónicamente evidente** y con **package by component**, encapsulando con modificadores de acceso para que el compilador haga cumplir la arquitectura (capítulo «The Missing Chapter» en *Clean Architecture*).
- **[opinión]** Para esta skill, el valor del nivel 4 no es el diagrama. Es la **decisión explícita**, antes de codificar, de qué paquete, qué tipos públicos, qué dependencias y qué tipos internos tendrá cada componente. Con esas decisiones se puede escribir código revisable por segmentos.

---

## 2. Reglas operativas para un agente

**Alcance y momento**
1. Detalla a nivel 4 **un componente a la vez**. Nunca mezcles varios componentes en el mismo diagrama o especificación de nivel 4.
2. No bajes a nivel 4 sin un componente de nivel 3 nombrado y con responsabilidad escrita. Un nivel 4 sin padre es un «diagrama huérfano».
3. Pregunta si el componente justifica nivel 4 detallado. Lo justifica si es complejo, crítico, cambia mucho o introduce un patrón nuevo. Si es CRUD simple, basta con la convención de paquetes y pasa directo a código.

**Mapeo nivel 3 → código**
4. Cada componente del nivel 3 debe mapear a **un paquete raíz identificable** (p. ej. `com.empresa.app.pedidos`). Escribe el mapeo en una tabla `componente → paquete`.
5. El nombre del paquete y el del tipo de entrada deben usar el **mismo término** que el diagrama de nivel 3 (lenguaje ubicuo).
6. Cada componente debe tener **un punto de entrada público explícito**: una interfaz, una fachada o un servicio de API. Nómbralo antes de nombrar cualquier clase interna.
7. Declara el estilo de empaquetado del contenedor (layer, feature, ports & adapters o component) y **úsalo igual en todos los componentes**.

**Encapsulamiento y dependencias**
8. Marca `public` solo los tipos de la API del componente. Todo lo demás es package-private o va en un subpaquete `internal`.
9. Lista las **dependencias permitidas** de cada componente hacia otros componentes. Cada flecha del nivel 3 debe tener su import correspondiente, y no debe haber imports sin flecha.
10. Prohíbe los ciclos entre componentes. Si aparece uno, vuelve al nivel 3 y repártelo.
11. Haz cumplir las reglas con una prueba automática (Spring Modulith `verify()`, ArchUnit o módulos Java). No te fíes solo de la disciplina ni de la revisión.
12. Un controlador **nunca** accede directamente al repositorio de otro componente. Solo usa la API pública del componente.

**Elementos de código**
13. Nombra cada clase con un sufijo de rol arquitectónico (`*Controller`, `*Service`, `*Repository`, `*Adapter`, `*Listener`, `*Config`).
14. Para cada componente, enumera los tipos significativos: unos 5 a 15. Si pasan de ~20, divide el componente en el nivel 3.
15. Omite del detalle los getters/setters, DTOs triviales, utilidades, tests y código generado, salvo que sean el tema.
16. Si el componente persiste datos, define sus **tablas o entidades** y su dueño. Ningún otro componente escribe esas tablas.
17. Si el componente publica o consume eventos, nombra cada evento (tipo y campos) y quién lo escucha.
18. Para las interacciones con varios pasos, usa un **diagrama dinámico** (secuencia) y no un diagrama de clases.

**Mantenimiento**
19. Prefiere especificaciones verificables (tabla, reglas, prueba de arquitectura) a diagramas de clase dibujados a mano. Si hay diagrama, que se genere desde el código.
20. Guarda la especificación de nivel 4 junto al código y cámbiala en el mismo PR que el código.
21. **[opinión]** Parte la implementación de un componente en segmentos de ~600 líneas que compilen y se prueben por sí solos. Orden sugerido: API y tipos → dominio → persistencia → adaptadores de entrada → pruebas de componente.

---

## 3. Elementos prioritarios del nivel 4 (de mayor a menor)

Usar como «plantilla de ejemplo» que se ofrece en cada pregunta.

| # | Elemento | Por qué va en esta posición |
|---|----------|----------------------------|
| 1 | **Mapeo componente → paquete raíz** | Sin él no hay trazabilidad, y el model-code gap empieza aquí. Brown e InfoQ piden que los componentes mapeen a agrupaciones reales de código. |
| 2 | **API pública del componente** (interfaz o fachada y sus firmas) | Es la definición misma de componente («detrás de una interfaz bien definida»). Fija el contrato que consumen los demás. |
| 3 | **Visibilidad / encapsulamiento** (qué es `public`, qué es `internal`) | En el *Missing Chapter*, sin modificadores de acceso los cuatro estilos de empaquetado colapsan en el mismo grafo de dependencias. |
| 4 | **Dependencias permitidas y regla verificable** | Hace cumplir las flechas del nivel 3 y evita la erosión (controlador → repositorio saltándose el servicio). |
| 5 | **Modelo de datos propio** (entidades, VOs, tablas/ERD, dueño) | El ERD es el artefacto de nivel 4 más estable y difícil de inferir del código (blogs de la comunidad). El acoplamiento por datos rompe los componentes. |
| 6 | **Tipos internos con su rol** (Service, Repository, Adapter...) y naming | Es el estilo arquitectónicamente evidente: permite reconocer el diagrama en el código y extraerlo con herramientas (Structurizr). |
| 7 | **Eventos publicados y consumidos** | Son parte de la interfaz provista o requerida de un módulo (Spring Modulith *Module Canvas*). |
| 8 | **Contratos de error y transacción** (excepciones, límites `@Transactional`) **[opinión]** | Son decisiones de código que no salen en el nivel 3 y que cambian las firmas. |
| 9 | **Flujo dinámico de los casos complejos** (secuencia) | C4 lo recomienda solo para interacciones complicadas o patrones recurrentes. |
| 10 | **Pruebas alineadas a la arquitectura** (test de componente vía API + test de arquitectura) | El trabajo de Brown sobre «architecturally-aligned testing» propone probar el componente por su interfaz. |
| 11 | **Plan de segmentos (~600 líneas)** **[opinión]** | Convierte la especificación en entregas revisables. |

### Plantilla mínima (ejemplo para ofrecer)

```
Componente (N3): Pedidos            Contenedor: api-backend
Paquete raíz:    com.acme.tienda.pedidos       Estilo: package by component
API pública:     PedidosApi { PedidoId crear(NuevoPedido); Pedido obtener(PedidoId) }
Internos:        PedidosService, PedidoRepository (JPA), PedidoEntity, PedidosController
Depende de:      inventario (API), pagos::spi       Prohibido: cualquier *.internal ajeno
Datos propios:   tablas pedido, linea_pedido       Eventos: PedidoCreado -> inventario
Errores/Tx:      PedidoNoEncontrado (404); @Transactional en PedidosService.crear
Verificación:    ApplicationModules.of(App.class).verify()
Segmentos:       S1 API+dominio · S2 persistencia · S3 controller · S4 pruebas
```

---

## 4. Banco de preguntas cortas (de general a detallado)

Formato: **Pregunta** · opciones típicas · mini ejemplo.

### A. ¿Merece nivel 4?

**A1. ¿Qué componente del nivel 3 vamos a detallar ahora?**
Opciones: (a) el más crítico · (b) el más complejo · (c) el que se codifica primero · (d) otro: nombrarlo.
```
Contenedor api-backend
 ├─ Pedidos      <- detallar
 ├─ Inventario
 └─ Pagos
```

**A2. ¿Qué tan complejo es este componente?**
Opciones: (a) CRUD simple → solo convención · (b) reglas de negocio medias · (c) dominio rico o algoritmo → nivel 4 completo.
```
CRUD:     Controller -> Repository
Complejo: Controller -> Service -> Politicas + Agregado -> Repository
```

**A3. ¿Qué forma tendrá el detalle del nivel 4?**
Opciones: (a) tabla/plantilla textual · (b) diagrama de clases generado · (c) ERD · (d) secuencia para un flujo.
```
@startuml
class PedidosService
interface PedidoRepository
PedidosService --> PedidoRepository
@enduml
```

### B. Empaquetado y mapeo

**B1. ¿Cómo se empaqueta el código de este contenedor?**
Opciones: (a) por capa · (b) por feature · (c) puertos y adaptadores · (d) por componente.
```
por capa:       web/ service/ data/
por componente: pedidos/ inventario/ pagos/
```

**B2. ¿Cuál es el paquete raíz del componente?**
Opciones: (a) `<base>.<componente>` · (b) `<base>.<contexto>.<componente>` · (c) módulo Maven/Gradle propio.
```java
package com.acme.tienda.pedidos;   // = componente "Pedidos" del N3
```

**B3. ¿El nombre del componente coincide con el término del negocio?**
Opciones: (a) sí, se usa tal cual · (b) se traduce o abrevia (documentar) · (c) se renombra en el N3.
```
N3: "Gestión de Pedidos"  ->  paquete: pedidos  ->  API: PedidosApi
```

**B4. ¿El componente tiene subpaquetes internos?**
Opciones: (a) no, paquete plano · (b) sí, `internal` · (c) sí, por rol (`web`, `domain`, `persistence`).
```
pedidos/
  PedidosApi.java        (public)
  internal/PedidosService.java
  internal/PedidoRepository.java
```

### C. Interfaz pública

**C1. ¿Cuál es el punto de entrada público del componente?**
Opciones: (a) interfaz `XxxApi` · (b) fachada `XxxComponent` · (c) servicio Spring público · (d) solo eventos.
```java
public interface PedidosApi {
    PedidoId crear(NuevoPedido cmd);
    Optional<PedidoView> obtener(PedidoId id);
}
```

**C2. ¿Qué operaciones expone la API? (lista de 3-7)**
Opciones: (a) comandos + consultas · (b) solo consultas · (c) solo comandos asíncronos.
```java
PedidoId crear(NuevoPedido c);
void cancelar(PedidoId id, Motivo m);
List<PedidoView> porCliente(ClienteId c);
```

**C3. ¿Qué tipos cruzan la frontera del componente?**
Opciones: (a) records/DTOs propios de la API · (b) IDs y value objects · (c) entidades JPA (antipatrón).
```java
public record NuevoPedido(ClienteId cliente, List<Linea> lineas) {}
public record PedidoView(PedidoId id, Estado estado, Dinero total) {}
```

**C4. ¿Cómo se señalan los errores de la API?**
Opciones: (a) excepciones de dominio · (b) tipo resultado (`Result`/sealed) · (c) `Optional` para "no existe".
```java
public sealed interface CrearResultado
  permits Creado, StockInsuficiente, ClienteBloqueado {}
```

### D. Encapsulamiento y dependencias

**D1. ¿Qué tipos serán `public`?**
Opciones: (a) solo la API y sus DTOs · (b) API + SPI para extensiones · (c) todo (antipatrón).
```java
public interface PedidosApi {...}
class PedidosService implements PedidosApi {...} // package-private
```

**D2. ¿De qué otros componentes depende este?**
Opciones: (a) ninguno · (b) API de X · (c) SPI/named interface de X · (d) solo eventos de X.
```java
@ApplicationModule(allowedDependencies = {"inventario", "pagos :: spi"})
package com.acme.tienda.pedidos;
```

**D3. ¿Cómo se verificará que se respeten las dependencias?**
Opciones: (a) Spring Modulith `verify()` · (b) ArchUnit · (c) módulos Maven/JPMS · (d) solo revisión (débil).
```java
@Test void modulos() {
  ApplicationModules.of(App.class).verify();
}
```

**D4. ¿Es síncrona o por eventos la comunicación con otros componentes?**
Opciones: (a) llamada directa a API · (b) evento de aplicación · (c) mixta: comando síncrono, notificación por evento.
```java
events.publishEvent(new PedidoCreado(id, lineas));
// inventario:
@ApplicationModuleListener void on(PedidoCreado e) {...}
```

### E. Tipos internos y datos

**E1. ¿Qué clases internas tendrá el componente y con qué rol?**
Opciones: (a) Controller+Service+Repository · (b) + Policy/Domain · (c) + Adapter a sistema externo.
```
PedidosController   (web, entrada)
PedidosService      (implementa PedidosApi)
Pedido / LineaPedido (dominio)
PedidoRepository    (persistencia)
```

**E2. ¿Qué convención de nombres de rol usaremos?**
Opciones: (a) sufijos (`*Service`, `*Repository`) · (b) estereotipos/anotaciones (`@Service`, jMolecules) · (c) ambos.
```java
@Service class PedidosService implements PedidosApi {}
interface PedidoRepository extends JpaRepository<PedidoEntity, UUID> {}
```

**E3. ¿Qué datos persiste el componente y quién es su dueño?**
Opciones: (a) tablas propias · (b) esquema propio · (c) no persiste · (d) comparte tabla (alerta).
```sql
CREATE TABLE pedido (id UUID PRIMARY KEY, cliente_id UUID, estado VARCHAR(20));
CREATE TABLE linea_pedido (pedido_id UUID REFERENCES pedido, sku VARCHAR, cant INT);
```

**E4. ¿Hay entidades separadas para dominio y persistencia?**
Opciones: (a) una sola entidad JPA · (b) dominio puro + entidad JPA mapeada · (c) records + JDBC.
```java
class Pedido { void agregar(Linea l) {...} }            // dominio
@Entity class PedidoEntity { @Id UUID id; String estado; } // persistencia
```

**E5. ¿Dónde están los límites transaccionales?**
Opciones: (a) en cada método público del Service · (b) solo en comandos · (c) ninguno (solo lectura).
```java
@Transactional
public PedidoId crear(NuevoPedido c) { ... repo.save(p); events.publishEvent(...); }
```

### F. Comportamiento, pruebas y entrega

**F1. ¿Qué flujo del componente necesita un diagrama de secuencia?**
Opciones: (a) ninguno · (b) el caso principal · (c) el de compensación/error.
```
Controller -> PedidosApi.crear
PedidosApi -> InventarioApi.reservar
PedidosApi -> repo.save ; publish PedidoCreado
```

**F2. ¿Cómo se probará el componente?**
Opciones: (a) test de componente por la API · (b) unitarios por clase · (c) `@ApplicationModuleTest` aislado · (d) mezcla.
```java
@ApplicationModuleTest
class PedidosTests {
  @Autowired PedidosApi api;
  @Test void creaPedido() { assertNotNull(api.crear(nuevo())); }
}
```

**F3. ¿En qué segmentos de ~600 líneas lo entregamos?**
Opciones: (a) por capa interna · (b) por operación de la API · (c) API+dominio primero, adaptadores después.
```
S1 (~250) API, records, excepciones
S2 (~400) dominio + service + tests unitarios
S3 (~350) persistencia + migración SQL
S4 (~300) controller + test de componente
```

**F4. ¿Qué queda fuera del detalle a propósito?**
Opciones: (a) getters/DTOs triviales · (b) config/boilerplate · (c) código generado · (d) todo lo anterior.
```
Fuera: PedidoMapper (MapStruct generado), getters, application.yml
```

---

## 5. Señales de alerta y antipatrones

- **Model-code gap.** El diagrama dice «componente Pedidos», pero en el código no hay ningún paquete ni tipo que se llame así. Prueba: si conviertes el modelo a código y vuelves, no coincide. Acción: renombra o reagrupa hasta que el mapeo de la regla 4 sea 1:1 o esté documentado.
- **`public` por defecto.** Todas las clases son públicas, así que los paquetes son solo carpetas y cualquier clase usa cualquier implementación (*Missing Chapter*). Señal: hay clases `*Impl` o `*Repository` importadas desde otro componente.
- **Capas relajadas.** Un controlador inyecta el repositorio y se salta el servicio, y con él la autorización o las reglas. Las flechas siguen «hacia abajo», pero la arquitectura se erosionó.
- **Antipatrón Périphérique.** Con solo dos árboles (dominio / infraestructura), un adaptador de infraestructura llama a otro adaptador sin pasar por el dominio.
- **Package by layer en un sistema que crece.** Tres «cubetas» gigantes (`controller/`, `service/`, `repository/`) que no dicen nada del negocio y suelen acabar en una *big ball of mud*.
- **Confundir paquete, JAR o módulo con componente.** El empaquetado es ortogonal. Un componente puede abarcar varios JARs, y un paquete puede no ser un componente. Hay que declarar el mapeo, no suponerlo.
- **Entidades JPA en la API pública.** Filtran el modelo de persistencia y acoplan componentes por datos. Brown advierte vigilar el acoplamiento en los modelos de datos.
- **Tablas compartidas entre componentes.** Es acoplamiento oculto que no aparece en ningún diagrama de dependencias.
- **Diagrama de clases dibujado a mano para todo.** Queda obsoleto de inmediato, porque el IDE lo genera. UML «falló» en parte por intentar reemplazar el código con diagramas (HN).
- **Diagrama generado sin curar.** Muestra cientos de clases y no se puede usar para conversar. Señal: más de ~20 tipos en un nivel 4, que entonces debe dividirse en el nivel 3.
- **Nivel 4 huérfano o multi-componente.** Un nivel 4 sin componente padre, o que cruza componentes. Las interacciones entre componentes van en un diagrama dinámico.
- **Ciclos entre componentes** (A importa B, B importa A). Indican que la frontera del nivel 3 está mal.
- **Arquitectura solo por disciplina.** No hay ninguna prueba (Modulith/ArchUnit/JPMS) que falle al violar una regla, así que la retroalimentación llega tarde o nunca.
- **Nombres sin rol.** Clases como `Manager`, `Helper` o `Utils` sin sufijo de rol: el código no es «arquitectónicamente evidente» y las herramientas no pueden extraer componentes.
- **[opinión] Segmentos que no compilan solos.** Un segmento de ~600 líneas que deja la API a medias o depende del segmento siguiente no se puede revisar ni probar aislado.

---

## 6. Fuentes

- https://c4model.com/diagrams/code — Definición oficial del nivel 4: alcance de un componente, elementos, «opcional», no recomendado salvo componentes importantes o complejos, y generarlo con el IDE.
- https://c4model.com/abstractions/component — Qué es un componente (agrupación tras una interfaz, no deployable). Paquetes y JARs no son componentes automáticamente; el empaquetado es ortogonal. Ejemplo PetClinic.
- https://c4model.com/abstractions/code — Los componentes se componen de elementos de código construidos con los bloques básicos del lenguaje.
- https://c4model.com/diagrams/component — Nivel 3: alcance de un contenedor; «créalo solo si aporta»; automatizar para documentación de larga vida.
- https://c4model.com/diagrams/dynamic — Diagrama dinámico (colaboración o secuencia) para interacciones complicadas; usar con moderación.
- https://c4model.com/faq — Origen del cambio de nombre «classes» → «code» (2015-16), objetivo de minimizar la brecha modelo-código, y complementar con UML cuando haga falta.
- https://www.infoq.com/articles/c4-architecture-model/ — Brown: los componentes «deben mapear a abstracciones reales» del código; el nivel 4 no se recomienda si el IDE lo genera.
- https://github.com/liaokongVFX/Clean-Architecture-zh/blob/master/docs/ch34.md — «The Missing Chapter» (Brown, en *Clean Architecture*): package by layer/feature/ports & adapters/component, abuso de `public`, que el compilador haga cumplir la arquitectura, y antipatrón Périphérique.
- https://dzone.com/articles/package-component-and — Brown (2015): «package by component» como híbrido y pruebas alineadas a la arquitectura (solo se pudo leer la intro; 403 en el resto).
- https://speakerdeck.com/realm/simon-brown-visualize-document-and-explore-your-software-architecture — Charla de Brown: model-code gap y estilo arquitectónicamente evidente (anotaciones, sufijos, paquetes, sistemas de módulos); extraer componentes del código y complementar.
- https://herbertograca.com/2015/11/10/software-architecture-vs-code-by-simon-brown/ — Resumen de «Software Architecture vs Code»: sufijos de rol y carpeta por componente.
- https://jasminefields.net/blag/architecture-evident-coding-style/ — Prácticas: organizar por función de dominio y límites del enfoque (mocks y pruebas).
- https://icepanel.io/blog/the-model-code-gap — Definición del model-code gap (Fairbanks), prueba de ida y vuelta, y por qué los diagramas generados no bastan.
- https://docs.structurizr.com/java/component/introduction — Component finder de Structurizr: identificar, categorizar, definir reglas (anotación, sufijo, subtipo) y codificarlas; las reglas son únicas por código base.
- https://github.com/structurizr/java-extensions/blob/master/docs/spring-component-finder-strategies.md — Estrategias Spring: reportar la interfaz (`CustomerRepository`) en vez de la implementación.
- https://docs.spring.io/spring-modulith/reference/fundamentals.html — Módulo = subpaquete directo; `internal`, `@NamedInterface`, `allowedDependencies` y `verify()`. Mapeo directo componente → paquete en Spring.
- https://docs.spring.io/spring-modulith/reference/documentation.html — `Documenter`: diagramas C4 de componentes generados desde el código y *Module Canvas* (API, agregados, eventos).
- https://www.archunit.org/use-cases — Reglas ejecutables de dependencias, capas, nombres y ciclos.
- https://uxxu.io/blog/c4-code-diagram/ — Guía práctica (comunidad): cuándo sí vale la pena, 10-20 clases, qué omitir y evitar diagramas huérfanos.
- https://www.visual-paradigm.com/guide/📘-c4-model-drilling-down-from-level-3-component-to-level-4-code-in-visual-paradigm/ — Criterios para bajar de N3 a N4: 5-15 clases, estereotipos y la regla «¿ayuda a saber dónde agregar una feature?».
- https://hn.algolia.com/api/v1/search?query=C4%20model%20code%20diagram&tags=comment — Hacker News: Brown dice que los dos primeros niveles suelen bastar; críticas a UML de bajo nivel y a los diagramas generados; pedido de versionar diagramas con el código.
