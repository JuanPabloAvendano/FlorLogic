---
plantilla: adaptador-persistencia
aplica_a: adaptador de salida que guarda y carga un agregado en una base relacional (repositorio), su modelo de tablas, sus migraciones y sus pruebas; también la proyección de lectura que vive en el mismo módulo
entra_desde: caso-de-uso (firma del puerto de salida) · entidad-agregado (identidad, invariantes, estados, versión)
sale_hacia: ninguna (es una hoja del flujo); el resultado pasa a la codificación por segmentos y a la verificación
---
# Plantilla · Adaptador de persistencia

## 0 · Instrucciones para el agente

1. Úsala cuando la etapa de consulta identifique «guardar o leer un agregado en la base de datos».
   Si lo que falta es la firma del puerto, vuelve a `caso-de-uso`. Si faltan invariantes, estados o
   la frontera del agregado, vuelve a `entidad-agregado`. Esta plantilla **no** decide reglas de
   negocio: las traduce a filas, restricciones y sentencias.
2. Antes de preguntar, lee lo que ya existe: ADR de tecnología, `pom.xml`, migraciones en
   `db/migration`, el puerto ya escrito. Lo que esté fijado se confirma en una línea (regla 7 de
   `investigacion/04` §1.2) y libera cupo.
3. Muestra primero el esqueleto de §1 con los ★ como valores provisionales. Luego haz las 20
   preguntas de §5, una variable por pregunta, en orden. Aplica la poda de §6 en cuanto una
   respuesta la active.
4. Cada respuesta produce una afirmación (`A-n`), una variable (`V-n`) o un pendiente (`Q-n`), según
   `investigacion/04` §4. Toda regla de concurrencia o de idempotencia lleva un caso que la cumple y
   otro que la viola.
5. Ofrece código solo desde §10. Si copias de §9, respeta la licencia indicada.
6. No inventes nombres de tablas ni columnas: salen del lenguaje ubicuo del agregado.
7. Al terminar, marca §7 y calcula la cobertura. Las prácticas de §4 que no apliquen se marcan N/A
   con motivo y no penalizan.

## 1 · Qué se define primero

Orden de prioridad (es el esqueleto que se ofrece al empezar). Base: `investigacion/04` §3.6,
ampliada.

```
Agregado:      ⟦1 qué raíz guarda⟧            → Pedido (con sus LineaPedido)
Puerto:        ⟦2 operaciones⟧                → Pedidos { porId, guardar, pendientesDe }
Tecnología:    ⟦3 cómo habla con la BD⟧       → Spring JDBC (JdbcClient) + mapeador a mano
Tablas:        ⟦4 modelo y mapeo⟧             → pedido(id, …, version) · linea_pedido(pedido_id, sku, …)
Identidad:     ⟦5 id y unicidad⟧              → UUIDv7 desde la app · UNIQUE(numero) → PedidoDuplicado
Escritura:     ⟦6 transacción y concurrencia⟧ → tx en el caso de uso · version optimista
Repetición:    ⟦7 idempotencia y eventos⟧     → ON CONFLICT · outbox en la misma tx
Lectura:       ⟦8 consultas e índices⟧        → pendientesDe → INDEX(cliente_id, estado)
Evolución:     ⟦9 migraciones⟧                → Flyway V__ · expand/contract
Fin de vida:   ⟦10 borrado⟧                   → estado terminal, sin DELETE
Pruebas:       ⟦11 dobles y contrato⟧         → en memoria + PostgreSQL (Testcontainers), misma prueba
```

| # | Concepto | Por qué va aquí | Fuente |
|---|---|---|---|
| 1 | Agregado que se persiste | El repositorio existe por raíz de agregado, no por tabla. Todo lo demás depende de esa frontera | Evans, *DDD* (Repositories); Vernon, *IDDD* cap. Repositories; Spring Data JDBC, «Domain Driven Design» |
| 2 | Operaciones del puerto | Fijan qué sentencias hacen falta. El puerto es una «colección en memoria» de raíces | Fowler, PoEAA *Repository* |
| 3 | Tecnología de acceso | Cambia cómo se mapea, cómo se detecta lo nuevo y cómo se cargan las hijas | jOOQ manual «jOOQ and JPA»; Spring Data JDBC «Why» |
| 4 | Modelo de tablas y mapeo | Separa el modelo de persistencia del de dominio | Fowler, PoEAA *Data Mapper*; Hombergs, cap. «Implementing a Persistence Adapter» y «Mapping Between Boundaries» |
| 5 | Identidad y unicidad | La unicidad es una invariante que solo la base garantiza bajo concurrencia | `investigacion/03` regla 12; PostgreSQL, *Constraints* |
| 6 | Transacción y concurrencia | Sin versión, «gana el último» (lost update) | Fowler, PoEAA *Optimistic Offline Lock*; Kleppmann, *DDIA* cap. 7; Mihalcea |
| 7 | Idempotencia y eventos | Reintentos y entrega «al menos una vez» | PostgreSQL `INSERT … ON CONFLICT`; microservices.io *Transactional Outbox* |
| 8 | Consultas e índices | Cada consulta frecuente necesita su índice; las FK no lo crean solas | PostgreSQL, *Constraints* |
| 9 | Migraciones | El esquema evoluciona con el código, en pasos pequeños | Sadalage y Fowler, *Evolutionary Database Design*; Flyway, *Versioned migrations* |
| 10 | Borrado | Decide restricciones, cascadas y retención | `investigacion/04` §3.6 P9 |
| 11 | Pruebas | La implementación en memoria solo sirve si se comporta igual que la real | Rainsberger, *Contract tests*; Testcontainers |

## 2 · Funcionamiento apropiado

**Responsabilidades del adaptador** (Hombergs, cap. «Implementing a Persistence Adapter»): recibe
tipos del dominio, los mapea al formato de la base, envía las sentencias, mapea la respuesta de
vuelta al dominio y la devuelve. **No** decide reglas, **no** abre la transacción y **no** publica
eventos a un broker.

```
 caso de uso (@Transactional)                       NÚCLEO
   │  pedidos.porId(id) / pedidos.guardar(p)
   ▼
 «puerto de salida» Pedidos  ── tipos del dominio: Pedido, PedidoId, ClienteId
   ▲ implementa                     ▲ implementa
   │                                │
 PedidosJdbc  (infraestructura)   PedidosEnMemoria (pruebas y demo)
   │ PedidoMapper: Pedido ↔ PedidoFila + LineaFila
   │ JdbcClient / NamedParameterJdbcTemplate (lotes)
   ▼
 PostgreSQL ── pedido · linea_pedido · event_publication (outbox de Spring Modulith)
   ▲
 Flyway: V1__pedido.sql, V2__…  (se aplican al arrancar y en las pruebas)

 PedidosContratoTest (abstracta) ◄── PedidosEnMemoriaContratoTest
                                 ◄── PedidosJdbcContratoIT (Testcontainers PostgreSQL)
```

**Flujo de `guardar`** [opinión, combinando PoEAA *Optimistic Offline Lock* y la doc. de
PostgreSQL]: (1) mapear la raíz a una fila; (2) un solo `INSERT … ON CONFLICT (id) DO UPDATE … WHERE
version = :leida` que inserta o actualiza y comprueba la versión; (3) si afecta 0 filas, lanzar
`ConflictoDeVersion`; (4) reemplazar las hijas en lote; (5) traducir `DuplicateKeyException` a la
excepción de dominio de la unicidad. Todo dentro de la transacción del caso de uso.

**Flujo de `porId`**: una consulta para la raíz y una por cada tabla hija, filtradas por el id. Así
el número de sentencias es fijo (1 + número de tablas hijas), no proporcional a las filas (sin N+1).

**Dónde vive cada pieza** (empaquetado del módulo; ajústalo al del proyecto):

| Pieza | Paquete | Conoce |
|---|---|---|
| Puerto `Pedidos` y sus excepciones | `pedidos.servicios.puertos` | dominio |
| `PedidosJdbc`, `PedidoMapper`, `PedidoFila` | `pedidos.infraestructura.salida.persistencia` | puerto, dominio, Spring JDBC |
| `PedidosEnMemoria` | `src/test` o `pedidos.infraestructura.salida.memoria` | puerto, dominio |
| Migraciones | `src/main/resources/db/migration` | — |
| `PedidosContratoTest` | `src/test/.../servicios/puertos` | puerto, dominio, JUnit |

## 3 · Patrones de diseño

| Patrón | Problema que resuelve | Cuándo usarlo | Cuándo no | Fuente |
|---|---|---|---|---|
| Repository | El dominio necesita objetos persistidos sin saber de SQL | Siempre, uno por raíz de agregado | Para listados de pantalla: usa una proyección de lectura | Fowler PoEAA; Evans; Vernon |
| Data Mapper | Objetos y tablas con formas distintas | Dominio rico, sin anotaciones de persistencia | CRUD sin reglas (`investigacion/02` §6) | Fowler PoEAA |
| Active Record | Fila y lógica en el mismo objeto | Lógica simple, casi CRUD | Con invariantes ricas o en hexagonal: mete la BD en el dominio | Fowler PoEAA |
| Unit of Work | Registrar cambios y escribirlos juntos | Ya lo da JPA (contexto de persistencia); a mano solo si hace falta | Con JDBC y un agregado por transacción: `guardar(raiz)` basta [opinión] | Fowler PoEAA |
| Identity Map | Cargar un objeto una sola vez por transacción | Lo da JPA; útil si el caso de uso carga la misma raíz varias veces | Con un agregado por transacción casi nunca hace falta [opinión] | Fowler PoEAA |
| Lazy Load | No cargar datos que no se usan | Colecciones grandes **fuera** del agregado | Dentro del agregado: se carga entero; el lazy causa N+1 | Fowler PoEAA; Mihalcea N+1 |
| Optimistic Offline Lock | Lost update entre transacciones | Conflictos raros; lectura y escritura en peticiones distintas | Contención alta o reintento caro | Fowler PoEAA; Mihalcea |
| Pessimistic Offline Lock (`SELECT … FOR UPDATE`) | Evitar el conflicto bloqueando | Contención alta en la misma transacción | Entre peticiones HTTP: no puede cubrir ese hueco | Fowler PoEAA; Mihalcea |
| UPSERT idempotente | Insertar o actualizar de forma atómica, aun en concurrencia | Clave natural o de idempotencia conocida | Si el negocio exige distinguir «nuevo» de «repetido»: lee el conteo | PostgreSQL `INSERT` |
| Transactional Outbox | Publicar si y solo si hay commit | El agregado emite eventos para otros módulos | Sin consumidores | microservices.io; Spring Modulith |
| Proyección de lectura | Consultas que no necesitan el agregado | Listados, reportes, búsquedas | Para validar invariantes | `investigacion/02` §6 (Jovanović) |
| Fake en memoria + prueba de contrato | Probar el núcleo sin BD con garantías | Todo puerto de salida (convención) | — | Rainsberger |
| Parallel Change (expand/contract) | Cambio de esquema que rompe a quien lee | Renombrar o partir columnas con datos | Tabla nueva sin consumidores | Fowler, *ParallelChange*; Ambler y Sadalage |

**Decisiones de patrón que la ronda tiene que tomar:** tecnología (P3), mapeo separado o no (P4),
insertar frente a actualizar (P9), concurrencia optimista o pesimista (P10), outbox sí o no (P15).

## 4 · Prácticas profesionales verificables

Severidad: 3 crítica · 2 mayor · 1 menor. Verificación: A = ArchUnit/Modulith · P = prueba · L = lectura.

| id | Práctica | Sev. | Al detallar | Al verificar | Fuente |
|---|---|---|---|---|---|
| PR-01 | Un puerto de persistencia por raíz de agregado; nada de repositorios de tablas hijas | 3 | P1 responde una raíz | L: ningún repositorio de hija | Evans; Vernon; Spring Data JDBC |
| PR-02 | El puerto vive en el núcleo, se nombra por intención y solo usa tipos del dominio | 3 | P2 firma escrita | A: `..servicios.puertos..` no depende de `org.springframework.jdbc..`, `jakarta.persistence..`, `org.jooq..` | Cockburn; `investigacion/02` #6 y #7 |
| PR-03 | Ninguna fila, entidad JPA ni `Record` de jOOQ sale del adaptador | 3 | P4 decide el mapeo | A: solo `..persistencia..` usa esas clases | Hombergs; Martin |
| PR-04 | El mapeo dominio↔fila es explícito y reconstituye el agregado por una fábrica | 2 | P4, P8 | P: ida y vuelta por estado | Fowler *Data Mapper* |
| PR-05 | Cada unicidad de negocio es `UNIQUE` y su violación se traduce a una excepción de dominio | 2 | P7 | P: insertar duplicado espera la excepción | `investigacion/04` §3.6; PostgreSQL |
| PR-06 | Concurrencia optimista: `version` + escritura condicionada; 0 filas = conflicto con nombre | 3 | P10 | P: guardar con versión vieja lanza `ConflictoDeVersion` | Fowler *Optimistic Offline Lock*; ddd-by-examples/library |
| PR-07 | La transacción la abre el caso de uso; el adaptador no la abre ni hace commit | 2 | P12 | A: `@Transactional` solo en `..servicios..` (o `MANDATORY` en el adaptador) | `investigacion/02` #20; Hombergs |
| PR-08 | Un agregado modificado por transacción | 2 | P12 | L: el caso de uso guarda una raíz | Vernon, *Effective Aggregate Design* |
| PR-09 | Las escrituras repetibles usan `ON CONFLICT`, no «SELECT y luego INSERT» ni capturar el error dentro de la transacción | 2 | P9, P14 | P: dos guardados del mismo comando dejan una fila | PostgreSQL `INSERT`; spring-ai (comentario del dialecto) |
| PR-10 | Los eventos salen por outbox en la misma transacción; los consumidores son idempotentes | 3 si aplica | P15 | P: rollback del caso de uso ⇒ ningún evento publicado | microservices.io; Spring Modulith |
| PR-11 | Número de sentencias por operación acotado (sin N+1) | 2 | P17 | P: contador de sentencias en la prueba [opinión: p. ej. datasource-proxy] | Mihalcea, *N+1* |
| PR-12 | Cada consulta frecuente tiene índice; las FK tienen índice en la columna que referencia | 2 | P16 | L: tabla consulta→índice en la migración | PostgreSQL, *Constraints* |
| PR-13 | Ningún listado sin límite (`findAll` prohibido) | 2 | P16 | L/A: no hay `findAll` ni `SELECT` sin `LIMIT` en listados | Mihalcea, *Best Spring Data JpaRepository* |
| PR-14 | El esquema cambia solo por migraciones versionadas; una migración aplicada no se edita; sin `ddl-auto` | 3 | P19 | P: Flyway `validate` en la prueba; L: sin `ddl-auto` | Flyway; Sadalage y Fowler |
| PR-15 | Los cambios destructivos van en dos pasos (expand/contract) | 2 | P19 | L: la migración no borra ni renombra en el mismo despliegue | Ambler y Sadalage; Fowler *ParallelChange* |
| PR-16 | Índices nuevos sobre tablas con tráfico: `CREATE INDEX CONCURRENTLY`, fuera de una transacción | 1 | P16, P19 | L | PostgreSQL `CREATE INDEX` |
| PR-17 | El puerto tiene implementación en memoria y una prueba de contrato que heredan la memoria y la real | 3 | P20 | P: existen las dos subclases | Rainsberger; spring-projects/spring-ai |
| PR-18 | La implementación real se prueba contra el mismo motor y versión que producción, no H2 | 2 | P20 | P: Testcontainers con la imagen de producción | Testcontainers, *Replace H2* |
| PR-19 | Prueba de ida y vuelta (guardar → cargar = igual) para cada estado del agregado | 2 | P8 | P | thombergs/buckpal (prueba del adaptador); [opinión] por estado |
| PR-20 | Con JPA: `open-in-view` apagado, asociaciones `LAZY` con carga explícita, `SEQUENCE` en vez de `IDENTITY` si hay lotes, `Persistable` con id asignado | 2 (N/A sin JPA) | P3, P9 | L + P | Mihalcea; Spring Data JPA, *Entity persistence* |
| PR-21 | Conflictos y fallos de serialización (SQLSTATE 40001) se reintentan desde el caso de uso completo, con tope | 1 | P11 | P | PostgreSQL, *Transaction Isolation* |
| PR-22 | SQL siempre con parámetros enlazados, nunca concatenado | 3 | — (se asume) | L: no hay `+` en cadenas SQL | OWASP, *SQL Injection Prevention* |

## 5 · Árbol de preguntas

Formato: pregunta · por qué · opciones (★ primero) con ejemplo · qué cierra.

### Propósito y frontera

**P1 · ¿Qué agregado persiste este adaptador?**
Por qué: el repositorio existe por raíz; fija tablas y transacción.
1. ★ Una raíz con sus hijas — ej: `Pedidos` guarda `Pedido` y sus `LineaPedido`.
2. Una tabla por repositorio — ej: `LineaPedidoRepository` (antipatrón, PR-01).
3. Solo una proyección de lectura — ej: `ResumenPedidos.porCliente(c)`; activa la poda de §6.
Cierra: §1.1 · PR-01.

**P2 · ¿Qué operaciones necesita el caso de uso, con nombre de negocio?**
Por qué: cada operación es una sentencia que probar; ISP.
1. ★ 2-5 operaciones — ej: `Optional<Pedido> porId(PedidoId)`, `void guardar(Pedido)`.
2. Puertos finos por operación — ej: `CargarPedido` y `GuardarPedido` (buckpal: `LoadAccountPort`).
3. CRUD genérico — ej: `extends CrudRepository<Pedido, UUID>` en el núcleo (rompe PR-02).
Cierra: §1.2 · PR-02.

**P3 · ¿Con qué tecnología habla con la base?**
Por qué: decide mapeo, detección de lo nuevo y carga de hijas.
1. ★ Spring JDBC `JdbcClient` + mapeo a mano — ej: `jdbc.sql("SELECT …").param("id", id).query(PedidoFila.class).optional()`. Conviene si el agregado es pequeño y quieres SQL explícito, sin lazy load ni dirty checking [opinión, apoyada en Spring Data JDBC «Why»].
2. JPA/Hibernate — ej: `@Entity class PedidoJpa` + `JpaRepository`. Conviene con grafos de objetos que se cargan, cambian y se guardan (jOOQ manual: «Whenever you're persisting an object graph, JPA is a good fit»). Activa PR-20.
3. jOOQ — ej: `dsl.insertInto(PEDIDO).set(…).onConflict(PEDIDO.ID).doUpdate()`. Conviene si hay mucho SQL específico o reportes (jOOQ manual: «Whenever SQL is a good fit, jOOQ is a good fit»).
Cierra: §1.3.

**P4 · ¿El modelo de persistencia va separado del dominio?**
Por qué: sin separación, las anotaciones de la BD dictan la forma del dominio.
1. ★ Separado, mapeo en dos sentidos — ej: `PedidoFila` (record) ↔ `Pedido` en `PedidoMapper`.
2. Dominio anotado (sin mapeo) — ej: `@Entity` en `Pedido`; solo para CRUD sin invariantes.
3. Proyección directa a DTO de lectura — ej: `query(ResumenPedido.class).list()`; solo lectura.
Cierra: §1.4 · PR-03, PR-04.

### Modelo de datos

**P5 · ¿Cómo se reparte el agregado en tablas?**
Por qué: fija claves, cascadas y número de sentencias por carga.
1. ★ Tabla raíz + una tabla por colección hija, con FK y `ON DELETE CASCADE` — ej: `linea_pedido(pedido_id → pedido.id)`.
2. Hijas o valores como `jsonb` en la raíz — ej: `pedido.lineas jsonb`; si nunca se consultan por dentro.
3. Una tabla ancha con columnas por estado — ej: `book_database_entity` de library; con muchos `null`.
Cierra: §1.4.

**P6 · ¿Quién genera la identidad y de qué tipo?**
Por qué: decide si el adaptador sabe qué es nuevo y si hay lotes.
1. ★ UUIDv7 generado en la aplicación — ej: `PedidoId.nuevo()`; columna `uuid` (RFC 9562, `investigacion/03` regla 12).
2. Secuencia de la base — ej: `nextval('pedido_seq')` antes de crear la raíz (domainlifecycles: `dslContext.nextval(…)`).
3. `IDENTITY`/`bigserial` — ej: el id llega tras el `INSERT`; con Hibernate impide lotes (Mihalcea).
Cierra: §1.5.

**P7 · ¿Qué unicidades de negocio garantiza la base y cómo se traducen?**
Por qué: un `SELECT` previo no evita la carrera; la restricción sí.
1. ★ `UNIQUE` + traducción a excepción de dominio — ej: `UNIQUE(numero)`; `DuplicateKeyException` → `PedidoDuplicado`.
2. Comprobar antes con `SELECT` — ej: `existePorNumero(n)`; dos transacciones pasan a la vez.
3. Ninguna — ej: solo la PK.
Cierra: §1.5 · PR-05.

**P8 · ¿Cómo se guardan los objetos de valor y el estado?**
Por qué: la base debe rechazar lo que el dominio no admite.
1. ★ Columnas tipadas + `CHECK`; estado como texto con `CHECK` — ej: `estado text CHECK (estado IN ('CREADO','CONFIRMADO','CANCELADO'))`.
2. Enum ordinal — ej: `estado smallint`; reordenar el enum corrompe datos.
3. Todo en `jsonb` — ej: `datos jsonb`; sin restricciones.
Cierra: §1.4 · PR-04, PR-19.

### Escritura y concurrencia

**P9 · ¿Cómo distingue insertar de actualizar?**
Por qué: con id asignado, el «save» genérico adivina mal.
1. ★ Un `INSERT … ON CONFLICT (id) DO UPDATE` — ej: §10.3; atómico aun en concurrencia (PostgreSQL).
2. `save()` de Spring Data JPA — ej: con id asignado necesita `Persistable.isNew()` o hace `merge` con `SELECT` extra (Spring Data JPA docs; Mihalcea).
3. Buscar y luego `INSERT` o `UPDATE` — ej: `findBy(id).map(update).onEmpty(insert)` (library); dos sentencias.
Cierra: §1.6 · PR-09, PR-20.

**P10 · ¿Cómo detecta dos escrituras concurrentes sobre el mismo agregado?**
Por qué: sin control, la segunda borra la primera (lost update).
1. ★ Versión optimista — ej: `UPDATE … SET version = version + 1 WHERE id = ? AND version = ?`; 0 filas → `ConflictoDeVersion`.
2. Bloqueo pesimista — ej: `SELECT … FOR UPDATE` al cargar; si la contención es alta.
3. Ninguno — ej: «gana el último» (`investigacion/03` §6).
Cierra: §1.6 · PR-06.

**P11 · ¿Qué aislamiento usa y quién reintenta?**
Por qué: Read Committed deja pasar write skew; niveles altos exigen reintento.
1. ★ Read Committed + versión; el caso de uso reintenta conflictos con tope — ej: 3 intentos ante `ConflictoDeVersion`.
2. Serializable (SSI) con reintento ante SQLSTATE 40001 — ej: reglas entre varias filas (write skew, Kleppmann cap. 7).
3. Sin reintento — ej: el conflicto llega al cliente como 409.
Cierra: §1.6 · PR-21.

**P12 · ¿Dónde se abre la transacción?**
Por qué: el adaptador no sabe qué más hace el caso de uso.
1. ★ En el caso de uso; el adaptador exige una abierta — ej: `@Transactional(propagation = MANDATORY)` en el adaptador.
2. En el adaptador — ej: `@Transactional` en `guardar`; dos puertos no quedan atómicos.
3. Autocommit — ej: cada sentencia por su lado.
Cierra: §1.6 · PR-07, PR-08.

**P13 · ¿Cómo guarda las colecciones hijas?**
Por qué: decide sentencias por guardado y si se pierden filas.
1. ★ Borrar y reinsertar en lote, si la colección es acotada — ej: `DELETE … WHERE pedido_id = ?` + `batchUpdate`; es lo que hace Spring Data JDBC.
2. Diferencia por clave (insertar, actualizar, borrar) — ej: colecciones grandes o hijas con id propio referenciado.
3. Solo insertar las nuevas — ej: buckpal `updateActivities` guarda las que no tienen id; para listas que solo crecen.
Cierra: §1.4.

### Repetición y eventos

**P14 · Si llega dos veces la misma escritura, ¿qué pasa?**
Por qué: los reintentos de red y de mensajes son la norma.
1. ★ Clave de idempotencia con `ON CONFLICT DO NOTHING` y se lee el resultado previo — ej: `comando_procesado(clave PK)` en la misma transacción.
2. `UPSERT` sobre la clave natural — ej: `ON CONFLICT (numero) DO UPDATE SET …`; la segunda deja el mismo estado.
3. No aplica — ej: la versión optimista rechaza la segunda (lo resuelve el caso de uso).
Cierra: §1.7 · PR-09.

**P15 · ¿Cómo salen los eventos de dominio junto con el cambio?**
Por qué: publicar fuera de la transacción pierde o inventa eventos.
1. ★ Outbox en la misma transacción — ej: Spring Modulith guarda en `event_publication` y entrega después.
2. Publicar tras el commit — ej: `@TransactionalEventListener(phase = AFTER_COMMIT)`; si el proceso cae, se pierde.
3. No emite eventos.
Cierra: §1.7 · PR-10.

### Lectura

**P16 · ¿Qué consultas son frecuentes y qué índice tiene cada una?**
Por qué: cada consulta sin índice es un barrido de tabla.
1. ★ Tabla consulta → índice, con límite — ej: `pendientesDe(cliente, 50)` → `INDEX (cliente_id, estado)`.
2. Solo `porId` — ej: basta la PK.
3. Decidir después — queda pendiente `Q-n`.
Cierra: §1.8 · PR-12, PR-13, PR-16.

**P17 · ¿Cómo carga el agregado sin N+1?**
Por qué: N+1 no aparece en el log de lentas, pero suma.
1. ★ Una consulta por tabla, filtrada por id o por `IN (…)` — ej: raíz + `SELECT … FROM linea_pedido WHERE pedido_id = ?`.
2. Una sola consulta con `multiset` (jOOQ) o `JOIN FETCH` (JPA) — ej: §9.6.
3. Carga perezosa — ej: `@OneToMany(fetch = LAZY)` recorrido en un bucle (antipatrón).
Cierra: §1.8 · PR-11.

**P18 · ¿Cómo termina la vida de un agregado?**
Por qué: decide cascadas, retención y si `porId` devuelve terminados.
1. ★ Estado terminal, sin `DELETE` — ej: `CANCELADO` sigue en la tabla.
2. Borrado físico con cascada a las hijas — ej: `ON DELETE CASCADE`; datos sin valor histórico.
3. Archivado tras un plazo — ej: trabajo nocturno que mueve a `pedido_archivo` (plantilla de trabajo programado).
Cierra: §1.10.

### Evolución y pruebas

**P19 · ¿Cómo evoluciona el esquema?**
Por qué: una migración editada o destructiva rompe despliegues.
1. ★ Flyway versionado, nunca editar lo aplicado, cambios destructivos en dos pasos — ej: `V3__pedido_add_canal.sql` (nullable), relleno y luego `NOT NULL`.
2. `ddl-auto` / generar desde entidades — ej: `spring.jpa.hibernate.ddl-auto=update`; sin historial.
3. Scripts a mano — ej: DBA aplica `cambio.sql`.
Cierra: §1.9 · PR-14, PR-15.

**P20 · ¿Qué implementaciones y pruebas tendrá el puerto?**
Por qué: el fake en memoria solo vale si cumple el mismo contrato.
1. ★ Real + en memoria + prueba de contrato abstracta que heredan ambas; la real con PostgreSQL en Testcontainers — ej: §10.6.
2. Real probada con H2 — ej: `jdbc:h2:mem`; difiere en `ON CONFLICT`, tipos y bloqueos.
3. Solo mocks del puerto — ej: `when(pedidos.porId(id))…`; nadie prueba el SQL.
Cierra: §1.11 · PR-17, PR-18, PR-19.

## 6 · Poda y ramas nuevas

- **Proyección de lectura (P1 = 3):** se saltan P9, P10, P11, P12, P13, P14, P15 y P18. Los cupos van a:
  orden y paginación por cursor, forma del DTO de lectura y frescura (¿lee del primario o de una
  réplica?).
- **Agregado sin hijas:** P13 y P17 se vuelven una confirmación. Cupo: ¿hay columnas calculadas o
  históricas?
- **Solo se añade (registro, bitácora):** P10 cambia a «¿qué clave garantiza el orden y la
  unicidad?» y P13 se salta.
- **Tecnología fijada por ADR:** P3 y P19 se confirman en una línea, citando el ADR.
- **P3 = JPA:** P9 pasa a «¿`Persistable` o versión no primitiva?», P17 a «¿`JOIN FETCH` o
  `@EntityGraph` por caso de uso?» y se añade «¿`spring.jpa.open-in-view=false` y
  `hibernate.jdbc.batch_size`?». PR-20 aplica.
- **P3 = jOOQ:** se añade «¿generación de código desde las migraciones (Flyway) en el build?».
- **Sin eventos (P15 = 3):** PR-10 queda N/A.
- **P10 = pesimista:** se añade «¿qué tiempo de espera del bloqueo (`lock_timeout`)?».
- **Módulo CRUD sin invariantes:** se sugiere el modelo anotado (P4 = 2) y PR-03/PR-04 quedan N/A con
  motivo (`investigacion/02` §6).
- **Cupos libres:** primero la metapregunta «¿qué debería haberte preguntado?», luego bordes de
  concurrencia («¿y si dos operarios confirman el mismo pedido?»).

## 7 · Cobertura

- [ ] Agregado persistido y frontera (raíz + hijas)
- [ ] Firma del puerto: operaciones, tipos del dominio, «no encontrado» y errores
- [ ] Tecnología de acceso con su motivo
- [ ] Modelo de tablas, mapeo y reconstitución
- [ ] Identidad: tipo y quién la genera
- [ ] Unicidades de negocio con su restricción y su traducción
- [ ] Objetos de valor y estado con sus `CHECK`
- [ ] Insertar frente a actualizar
- [ ] Concurrencia: versión o bloqueo, con caso que cumple y que viola
- [ ] Aislamiento y política de reintento
- [ ] Frontera transaccional
- [ ] Guardado de colecciones hijas
- [ ] Idempotencia de escrituras repetidas
- [ ] Eventos y outbox (o N/A)
- [ ] Consultas con su índice y su límite
- [ ] Carga sin N+1 (sentencias por operación)
- [ ] Borrado y retención
- [ ] Migraciones y estrategia de cambio destructivo
- [ ] Implementación en memoria + prueba de contrato + prueba con el motor real

## 8 · Antipatrones

| Antipatrón | Señal detectable | Fuente |
|---|---|---|
| Repositorio por tabla | `LineaPedidoRepository`; el caso de uso guarda hijas por separado | Evans; `investigacion/03` §6 |
| Entidad de persistencia en el núcleo | `import jakarta.persistence` u `org.jooq` en `..dominio..` o `..puertos..` | Martin; `investigacion/02` §6 |
| Puerto con nombre de tecnología | `PedidoJpaPort`, `PedidoDao` | Graça; `investigacion/02` #6 |
| «Gana el último» | tabla sin `version`; `UPDATE … WHERE id = ?` sin más condición | Fowler *Optimistic Offline Lock* |
| Comprobar y luego insertar | `if (!existe(n)) insertar(…)` sin `UNIQUE` | PostgreSQL `INSERT` (UPSERT atómico) |
| Capturar la violación dentro de la transacción y seguir | `catch (DuplicateKeyException e) { … }` y más sentencias después, en PostgreSQL | spring-ai, `PostgresJdbcSessionRepositoryDialect` (comentario) |
| N+1 | consulta dentro de un bucle; `getLineas()` perezoso en un `for` | Mihalcea, *N+1* |
| Open Session in View | `spring.jpa.open-in-view` sin apagar; consultas al serializar la respuesta | Mihalcea, *OSIV* |
| `save()` innecesario o `merge` con `SELECT` extra | `repo.save(e)` sobre una entidad ya gestionada; id asignado sin `Persistable` | Mihalcea; Spring Data JPA docs |
| `findAll` sin límite | `findAll()` o `SELECT` sin `LIMIT` en un listado | Mihalcea |
| Migración editada | cambia el checksum en `flyway_schema_history`; `validate` falla | Flyway |
| Renombrar o borrar columna en un paso | `ALTER TABLE … RENAME COLUMN` en la misma versión que el código nuevo | Ambler y Sadalage; Sadalage y Fowler |
| Publicar eventos a mano tras guardar | `kafka.send(…)` dentro o fuera del `@Transactional` sin outbox | microservices.io |
| Probar con H2 lo que corre en PostgreSQL | `jdbc:h2:mem` en pruebas; SQL con `ON CONFLICT` o `jsonb` | Testcontainers, *Replace H2* |
| Fake en memoria sin contrato | `PedidosEnMemoria` que nunca corre la prueba de la real | Rainsberger |
| SQL concatenado | `"… WHERE id = '" + id + "'"` | OWASP |

## 9 · Código de referencia real

Todos los archivos se leyeron el 15-oct-2026 en el commit enlazado. Los fragmentos son literales;
`// …` marca lo omitido.

### 9.1 · Adaptador de persistencia hexagonal con mapeo separado (buckpal)

- **Muestra:** adaptador que implementa dos puertos finos y delega el mapeo.
- **Repositorio:** `thombergs/buckpal`, `src/main/java/io/reflectoring/buckpal/adapter/out/persistence/AccountPersistenceAdapter.java`
- **URL:** https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/main/java/io/reflectoring/buckpal/adapter/out/persistence/AccountPersistenceAdapter.java#L15-L66
- **Licencia:** el repositorio **no declara licencia** (la API de GitHub devuelve `license: null`).
  Úsalo para leer y aprender; no copies el código literal.

```java
@RequiredArgsConstructor
@PersistenceAdapter
class AccountPersistenceAdapter implements
		LoadAccountPort,
		UpdateAccountStatePort {

	private final SpringDataAccountRepository accountRepository;
	private final ActivityRepository activityRepository;
	private final AccountMapper accountMapper;

	@Override
	public Account loadAccount(
					AccountId accountId,
					LocalDateTime baselineDate) {

		AccountJpaEntity account =
				accountRepository.findById(accountId.getValue())
						.orElseThrow(EntityNotFoundException::new);
	// …
		return accountMapper.mapToDomainEntity(
				account,
				activities,
				withdrawalBalance,
				depositBalance);
	}

	@Override
	public void updateActivities(Account account) {
		for (Activity activity : account.getActivityWindow().getActivities()) {
			if (activity.getId() == null) {
				activityRepository.save(accountMapper.mapToJpaEntity(activity));
			}
		}
	}
}
```

Del mismo repositorio, `AccountMapper.java`
(https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/main/java/io/reflectoring/buckpal/adapter/out/persistence/AccountMapper.java):

```java
	ActivityJpaEntity mapToJpaEntity(Activity activity) {
		return new ActivityJpaEntity(
				activity.getId() == null ? null : activity.getId().getValue(),
				activity.getTimestamp(),
				activity.getOwnerAccountId().getValue(),
				activity.getSourceAccountId().getValue(),
				activity.getTargetAccountId().getValue(),
				activity.getMoney().getAmount().longValue());
	}
```

- **Copia:** clase package-private, puertos del núcleo, entidades JPA y repositorios Spring Data
  encerrados en el paquete del adaptador, mapeador aparte.
- **No copies:** `EntityNotFoundException` de JPA sale hacia el núcleo (el puerto debería devolver
  `Optional` o una excepción propia); no hay control de versión (`NoOpAccountLock` en el mismo
  paquete); el dominio usa ids `Long` de base de datos.

### 9.2 · Repositorio de agregado con versión optimista en JDBC (ddd-by-examples/library)

- **Muestra:** puerto en el modelo, adaptador JDBC con `UPDATE … WHERE version = ?` y excepción con nombre.
- **Repositorio:** `ddd-by-examples/library`. Puerto: `src/main/java/io/pillopl/library/lending/book/model/BookRepository.java`; adaptador: `…/lending/book/infrastructure/BookDatabaseRepository.java`
- **URL:** https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/main/java/io/pillopl/library/lending/book/infrastructure/BookDatabaseRepository.java#L27-L70
- **Licencia:** MIT.

```java
public interface BookRepository {

    Option<Book> findBy(BookId bookId);

    void save(Book book);
}
```

```java
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class BookDatabaseRepository implements BookRepository, FindAvailableBook, FindBookOnHold {

    private final JdbcTemplate jdbcTemplate;
    // …
    @Override
    public void save(Book book) {
        findBy(book.bookId())
                .map(entity -> updateOptimistically(book))
                .onEmpty(() -> insertNew(book));
    }

    private int updateOptimistically(Book book) {
        int result = Match(book).of(
                Case($(instanceOf(AvailableBook.class)), this::update),
                Case($(instanceOf(BookOnHold.class)), this::update),
                Case($(instanceOf(CheckedOutBook.class)), this::update)
        );
        if (result == 0) {
            throw new AggregateRootIsStale("Someone has updated book in the meantime, book: " + book);
        }
        return result;
    }

    private int update(AvailableBook availableBook) {
        return jdbcTemplate.update("UPDATE book_database_entity b SET b.book_state = ?, b.available_at_branch = ?, b.version = ? WHERE book_id = ? AND version = ?",
                Available.toString(),
                availableBook.getLibraryBranch().getLibraryBranchId(),
                availableBook.getVersion().getVersion() + 1,
                availableBook.getBookId().getBookId(),
                availableBook.getVersion().getVersion());
    }
```

- **Copia:** el puerto vive en `model` con dos operaciones; la versión viaja en el agregado; 0 filas
  actualizadas ⇒ excepción del dominio; un `update` por estado de la máquina de estados.
- **No copies:** «buscar y luego insertar» son dos sentencias y dos transacciones pueden insertar a la
  vez (usa `ON CONFLICT`, §9.4); en el mismo repositorio, `PatronsDatabaseRepository` publica eventos
  desde el adaptador (`domainEvents.publish(…)`), algo que en esta plantilla hace el caso de uso con
  outbox.

### 9.3 · Repositorio con `JdbcClient` (making/oauth2-sso-demo)

- **Muestra:** `JdbcClient` con parámetros posicionales, `optional()`, mapeo a mano y upsert.
- **Repositorio:** `making/oauth2-sso-demo`, `todo-api/src/main/java/lol/maki/dev/todo/todo/TodoRepository.java`
- **URL:** https://github.com/making/oauth2-sso-demo/blob/42780eae3d386250fa1e432e3d6210a06b873a54/todo-api/src/main/java/lol/maki/dev/todo/todo/TodoRepository.java#L11-L47
- **Licencia:** Apache-2.0.

```java
@Component
public class TodoRepository {

	private final JdbcClient jdbcClient;

	public TodoRepository(JdbcClient jdbcClient) {
		this.jdbcClient = jdbcClient;
	}

	public Optional<Todo> findById(UUID todoId) {
		return this.jdbcClient.sql(
				"SELECT todo_id, todo_title, finished, created_at, created_by, updated_at, updated_by FROM todo WHERE todo_id = ?")
			.param(todoId.toString())
			.query(this::mapRow)
			.optional();
	}
	// …
	public Todo save(Todo todo) {
		this.jdbcClient.sql(
				"INSERT OR REPLACE INTO todo (todo_id, todo_title, finished, created_at, created_by, updated_at, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?)")
			.param(todo.todoId().toString())
			.param(todo.todoTitle())
			// …
			.update();
		return todo;
	}
```

- **Copia:** la forma fluida `sql → param → query → optional/list`, id asignado por la aplicación,
  columnas explícitas (sin `SELECT *`).
- **No copies:** `INSERT OR REPLACE` es de SQLite (en PostgreSQL es `ON CONFLICT`); no es un puerto
  (clase pública sin interfaz); `findAll()` sin límite; sin versión.

### 9.4 · UPSERT idempotente en PostgreSQL (spring-projects/spring-ai)

- **Muestra:** `ON CONFLICT … DO UPDATE` con `EXCLUDED` y `DO NOTHING` con el motivo de no capturar
  la excepción.
- **Repositorio:** `spring-projects/spring-ai`, `session-repositories/spring-ai-session-jdbc/src/main/java/org/springframework/ai/session/jdbc/PostgresJdbcSessionRepositoryDialect.java`
- **URL:** https://github.com/spring-projects/spring-ai/blob/d8ee22fc43c878e046d5a5471fa8c87c808e6dac/session-repositories/spring-ai-session-jdbc/src/main/java/org/springframework/ai/session/jdbc/PostgresJdbcSessionRepositoryDialect.java#L27-L47
- **Licencia:** Apache-2.0.

```java
	@Override
	public String getUpsertSessionSql() {
		return """
				INSERT INTO AI_SESSION (id, created_at, expires_at, metadata)
				VALUES (?, ?, ?, ?)
				ON CONFLICT (id) DO UPDATE
					SET expires_at        = EXCLUDED.expires_at,
						metadata          = EXCLUDED.metadata
				""";
	}

	@Override
	public String getInsertSessionIfAbsentSql() {
		// A failed INSERT would abort an enclosing PostgreSQL transaction; ignore the
		// conflict instead and report it through the update count.
		return """
				INSERT INTO AI_SESSION (id, created_at, expires_at, metadata)
				VALUES (?, ?, ?, ?)
				ON CONFLICT (id) DO NOTHING
				""";
	}
```

- **Copia:** el SQL del motor aislado en un «dialecto»; `DO NOTHING` + conteo de filas para saber si
  era nuevo; `created_at` no se pisa en el `DO UPDATE`.
- **No copies:** nada en particular; falta solo la condición de versión, que aquí no aplica.

Del mismo repositorio, `MIT` aparte: `testcontainers/tc-guide-replace-h2-with-real-database-for-testing`,
`src/main/java/com/testcontainers/demo/ProductRepository.java`
(https://github.com/testcontainers/tc-guide-replace-h2-with-real-database-for-testing/blob/c8bfe20f502a973ac5f9e70cf3993217ea1d552d/src/main/java/com/testcontainers/demo/ProductRepository.java#L8-L19, licencia MIT) usa
`ON CONFLICT DO NOTHING` desde Spring Data JPA con `@Modifying @Query(nativeQuery = true)`.

### 9.5 · Puerto implementado con jOOQ, upsert en lote (Marthym/baywatch)

- **Muestra:** adaptador hexagonal (`…PersistenceAdapter implements …PersistencePort`) con
  `onConflict().doUpdate()` y `dsl.batch(…)`.
- **Repositorio:** `Marthym/baywatch`, `sandside/src/main/java/fr/ght1pc9kc/baywatch/teams/infra/adapters/MembersPersistenceAdapter.java`
- **URL:** https://github.com/Marthym/baywatch/blob/145794e72cc84ba1b20173a93dc7dcba38c9d823/sandside/src/main/java/fr/ght1pc9kc/baywatch/teams/infra/adapters/MembersPersistenceAdapter.java#L29-L76
- **Licencia:** GPL-3.0 (no copies el código a un proyecto con otra licencia).

```java
@Repository
@RequiredArgsConstructor
@SuppressWarnings("BlockingMethodInNonBlockingContext")
public class MembersPersistenceAdapter implements TeamMemberPersistencePort {
    // …
    private final @DatabaseQualifier Scheduler databaseScheduler;
    private final DSLContext dsl;
    private final TeamsMapper teamsMapper;
    // …
    @Override
    public Mono<Void> add(Collection<Entity<TeamMember>> requests) {
        Query[] queries = requests.stream()
                .map(teamsMapper::getTeamsMemberRecord)
                .map(r -> dsl.insertInto(TEAMS_MEMBERS).set(r)
                        .onConflict().doUpdate()
                        .set(TEAMS_MEMBERS.TEME_PENDING_FOR, TEAMS_MEMBERS.TEME_PENDING_FOR.bitOr(r.getTemePendingFor()))
                ).toArray(Query[]::new);
        return Mono.fromCallable(() -> dsl.batch(queries).execute())
                .subscribeOn(databaseScheduler).then();
    }
```

- **Copia:** el puerto en `domain.ports`, el `Record` de jOOQ sin salir del adaptador (lo arma el
  mapeador), varias sentencias en un solo viaje con `batch`.
- **No copies:** `onConflict()` sin columnas confía en la PK implícita (nombra la clave); el envoltorio
  reactivo sobre JDBC bloqueante no aplica a un servicio con hilos virtuales.

### 9.6 · jOOQ sin N+1 con `multiset`, y prueba con Testcontainers (testcontainers/tc-guide-working-with-jooq-flyway-using-testcontainers)

- **Muestra:** una consulta que trae la raíz, una referencia y una colección hija; prueba contra
  PostgreSQL real con `@ServiceConnection`; esquema por Flyway.
- **Repositorio:** `testcontainers/tc-guide-working-with-jooq-flyway-using-testcontainers`
- **URL:** https://github.com/testcontainers/tc-guide-working-with-jooq-flyway-using-testcontainers/blob/a7a68fe4f177b6809a4ad1ec3c99236ceec337fd/src/main/java/com/testcontainers/demo/domain/PostRepository.java#L23-L50 y https://github.com/testcontainers/tc-guide-working-with-jooq-flyway-using-testcontainers/blob/a7a68fe4f177b6809a4ad1ec3c99236ceec337fd/src/test/java/com/testcontainers/demo/domain/UserRepositoryTest.java#L14-L37
- **Licencia:** MIT.

```java
  public Optional<Post> getPostById(Long id) {
    return this.dsl.select(
        POSTS.ID,
        POSTS.TITLE,
        POSTS.CONTENT,
        row(POSTS.users().ID, POSTS.users().NAME, POSTS.users().EMAIL)
          .mapping(User::new)
          .as("createdBy"),
        multiset(
          select(COMMENTS.ID, COMMENTS.NAME, COMMENTS.CONTENT, COMMENTS.CREATED_AT, COMMENTS.UPDATED_AT)
            .from(COMMENTS)
            .where(POSTS.ID.eq(COMMENTS.POST_ID))
        )
          .as("comments")
          .convertFrom(r -> r.map(mapping(Comment::new))),
        POSTS.CREATED_AT,
        POSTS.UPDATED_AT
      )
      .from(POSTS)
      .where(POSTS.ID.eq(id))
      .fetchOptional(mapping(Post::new));
  }
```

(La lista de columnas del `select` interno va en una línea; en el original ocupa siete.)

```java
@SpringBootTest
@Sql("/test-data.sql")
@Testcontainers
class UserRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );

  @Autowired
  UserRepository repository;

  @Test
  void shouldCreateUserSuccessfully() {
    User user = new User(null, "John", "john@gmail.com");
    User savedUser = repository.createUser(user);
    assertThat(savedUser.id()).isNotNull();
    // …
  }
```

- **Copia:** `multiset` para cargar el agregado en un viaje; mapeo directo a records; contenedor
  estático con `@ServiceConnection`; la misma imagen de PostgreSQL que producción; en el mismo
  repositorio, `UserRepositoryJooqTest` usa el slice `@JooqTest` con la URL
  `jdbc:tc:postgresql:16-alpine:///db`.
- **No copies:** `LocalDateTime.now()` dentro del repositorio (inyecta `Clock`); `bigserial` como
  identidad; con Testcontainers 2 la clase es `org.testcontainers.postgresql.PostgreSQLContainer` sin
  genérico (el ejemplo usa la de 1.x).

### 9.7 · Prueba de contrato compartida: memoria + PostgreSQL (spring-projects/spring-ai)

- **Muestra:** exactamente la convención del usuario: una prueba abstracta que corre contra la
  implementación en memoria y contra la JDBC en PostgreSQL con Testcontainers.
- **Repositorio:** `spring-projects/spring-ai`.
  Abstracta: `spring-ai-test/src/main/java/org/springframework/ai/test/session/AbstractSessionRepositoryContractTests.java`;
  memoria: `spring-ai-test/src/test/java/org/springframework/ai/test/session/InMemorySessionRepositoryContractTests.java`;
  PostgreSQL: `session-repositories/spring-ai-session-jdbc/src/test/java/org/springframework/ai/session/jdbc/JdbcSessionRepositoryPostgresqlIT.java`
- **URL:** https://github.com/spring-projects/spring-ai/blob/d8ee22fc43c878e046d5a5471fa8c87c808e6dac/spring-ai-test/src/main/java/org/springframework/ai/test/session/AbstractSessionRepositoryContractTests.java#L88-L118 ·
  https://github.com/spring-projects/spring-ai/blob/d8ee22fc43c878e046d5a5471fa8c87c808e6dac/session-repositories/spring-ai-session-jdbc/src/test/java/org/springframework/ai/session/jdbc/JdbcSessionRepositoryPostgresqlIT.java#L59-L87
- **Licencia:** Apache-2.0.

```java
public abstract class AbstractSessionRepositoryContractTests {

	protected SessionRepository repository;

	protected abstract SessionRepository createRepository();

	@BeforeEach
	void setUpRepository() {
		this.repository = createRepository();
	}
	// …
	@Test
	void findByIdReturnsNullForUnknownSession() {
		assertThat(this.repository.findById(UUID.randomUUID().toString())).isNull();
	}
	// …
	@Test
	void appendOfSameEventIdIsIdempotentReplay() {
		// …
		this.repository.appendEvent(event);
		long versionAfterFirst = this.repository.getEventVersion(session.id());
		this.repository.appendEvent(event);

		assertThat(texts(session.id(), EventFilter.all())).containsExactly("once");
		assertThat(this.repository.getEventVersion(session.id())).isEqualTo(versionAfterFirst);
	}
```

```java
class InMemorySessionRepositoryContractTests extends AbstractSessionRepositoryContractTests {

	@Override
	protected SessionRepository createRepository() {
		return InMemorySessionRepository.builder().build();
	}

}
```

```java
@Testcontainers
class JdbcSessionRepositoryPostgresqlIT {

	@Container
	static final PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");
	// …
	@Nested
	class Contract extends AbstractSessionRepositoryContractTests {

		@Override
		protected SessionRepository createRepository() {
			return JdbcSessionRepository.builder().dataSource(dataSource).build();
		}

	}
}
```

- **Copia:** la fábrica abstracta `createRepository()`; datos con ids aleatorios para compartir el
  almacén; la prueba de idempotencia; el `@Nested` que reutiliza el contenedor y el esquema de la
  clase externa; las pruebas que solo el motor real reproduce (bloqueos) quedan fuera del contrato.
- **No copies:** devolver `null` para «no encontrado» (en esta plantilla, `Optional`); la abstracta
  vive en `src/main` porque es una biblioteca para terceros; en un proyecto va en `src/test`.

### 9.8 · Tabla de outbox de Spring Modulith (spring-projects/spring-modulith)

- **Muestra:** el esquema del *Event Publication Registry* que hace de outbox transaccional.
- **Repositorio:** `spring-projects/spring-modulith`, `spring-modulith-events/spring-modulith-events-jdbc/src/main/resources/org/springframework/modulith/events/jdbc/schemas/v2/schema-postgresql.sql`
- **URL:** https://github.com/spring-projects/spring-modulith/blob/1fe549999aaa2401b675eba4a562ae374f4f96ba/spring-modulith-events/spring-modulith-events-jdbc/src/main/resources/org/springframework/modulith/events/jdbc/schemas/v2/schema-postgresql.sql
- **Licencia:** Apache-2.0.

```sql
CREATE TABLE IF NOT EXISTS event_publication
(
  id                     UUID NOT NULL,
  listener_id            TEXT NOT NULL,
  event_type             TEXT NOT NULL,
  serialized_event       TEXT NOT NULL,
  publication_date       TIMESTAMP WITH TIME ZONE NOT NULL,
  completion_date        TIMESTAMP WITH TIME ZONE,
  status                 TEXT,
  completion_attempts    INT,
  last_resubmission_date TIMESTAMP WITH TIME ZONE,
  PRIMARY KEY (id)
);
CREATE INDEX IF NOT EXISTS event_publication_serialized_event_hash_idx ON event_publication USING hash(serialized_event);
CREATE INDEX IF NOT EXISTS event_publication_by_completion_date_idx ON event_publication (completion_date);
```

- **Copia:** no escribas tu propio outbox si usas Spring Modulith; lleva este esquema a una migración
  Flyway versionada en vez de dejar que la biblioteca lo cree al arrancar.
- **No copies:** el `IF NOT EXISTS` dentro de una migración versionada sobra (Flyway ya controla qué se
  aplicó).

## 10 · Plantillas de código

Java 21 + Spring Boot. Dominio de ejemplo: `Pedido` (raíz) con `LineaPedido`, `version` y estado.
Supone que `entidad-agregado` dio `Pedido.reconstituir(…)`, `version()` y los objetos de valor.

### 10.1 · Puerto de salida (núcleo)

Basado en `BookRepository` (§9.2) y en `investigacion/03` regla 15.

```java
package com.acme.pedidos.servicios.puertos;

public interface Pedidos {                                    // TODO P1-P2: un puerto por raíz
    Optional<Pedido> porId(PedidoId id);
    /** Inserta o actualiza. Lanza ConflictoDeVersion o PedidoDuplicado. */
    void guardar(Pedido pedido);
    List<Pedido> pendientesDe(ClienteId cliente, int limite); // TODO P16: consultas con nombre y límite
}

public final class ConflictoDeVersion extends RuntimeException {      // TODO P10
    public ConflictoDeVersion(PedidoId id, long esperada) {
        super("El pedido %s cambió; versión leída %d".formatted(id.valor(), esperada));
    }
}
public final class PedidoDuplicado extends RuntimeException {         // TODO P7
    public PedidoDuplicado(NumeroPedido numero) { super("Ya existe el pedido " + numero.valor()); }
}
```

### 10.2 · Filas y mapeador (adaptador)

Basado en `AccountMapper` (§9.1): el mapeo vive en el adaptador y es package-private.

```java
package com.acme.pedidos.infraestructura.salida.persistencia;

record PedidoFila(UUID id, String numero, UUID clienteId, String estado, long version) {}
record LineaFila(UUID pedidoId, String sku, int cantidad) {}

final class PedidoMapper {
    private PedidoMapper() {}

    static PedidoFila aFila(Pedido p) {                              // TODO P8: tipos y estado
        return new PedidoFila(p.id().valor(), p.numero().valor(), p.cliente().valor(),
                p.estado().name(), p.version());
    }
    static List<LineaFila> aFilas(Pedido p) {
        return p.lineas().stream()
                .map(l -> new LineaFila(p.id().valor(), l.sku().valor(), l.cantidad().valor()))
                .toList();
    }
    static Pedido aDominio(PedidoFila f, List<LineaFila> lineas) {  // reconstituye, no «crea»
        return Pedido.reconstituir(new PedidoId(f.id()), new NumeroPedido(f.numero()),
                new ClienteId(f.clienteId()), EstadoPedido.valueOf(f.estado()),
                lineas.stream().map(l -> new LineaPedido(new Sku(l.sku()), new Cantidad(l.cantidad()))).toList(),
                f.version());
    }
}
```

### 10.3 · Adaptador con `JdbcClient`: upsert + versión + hijas en lote (★)

Basado en §9.3 (`JdbcClient`), §9.4 (`ON CONFLICT`) y §9.2 (0 filas ⇒ conflicto). Unir el upsert y
la condición de versión en una sola sentencia es [opinión]; se apoya en la doc. de PostgreSQL: si el
`WHERE` del `DO UPDATE` no se cumple, la fila no se actualiza y no cuenta.

```java
package com.acme.pedidos.infraestructura.salida.persistencia;

@Repository
@Transactional(propagation = Propagation.MANDATORY)                  // TODO P12
class PedidosJdbc implements Pedidos {

    private final JdbcClient jdbc;
    private final NamedParameterJdbcTemplate lotes;                   // JdbcClient no hace lotes

    PedidosJdbc(JdbcClient jdbc, NamedParameterJdbcTemplate lotes) { this.jdbc = jdbc; this.lotes = lotes; }

    @Override
    public Optional<Pedido> porId(PedidoId id) {                      // TODO P17: 1 + hijas sentencias
        return jdbc.sql("SELECT id, numero, cliente_id, estado, version FROM pedido WHERE id = :id")
                .param("id", id.valor())
                .query(PedidoFila.class).optional()
                .map(f -> PedidoMapper.aDominio(f, lineasDe(List.of(f.id()))));
    }

    @Override
    public List<Pedido> pendientesDe(ClienteId cliente, int limite) { // TODO P16: índice (cliente_id, estado)
        var filas = jdbc.sql("""
                SELECT id, numero, cliente_id, estado, version FROM pedido
                WHERE cliente_id = :cliente AND estado = 'CREADO' ORDER BY id LIMIT :limite""")
                .param("cliente", cliente.valor()).param("limite", limite)
                .query(PedidoFila.class).list();
        var lineas = lineasDe(filas.stream().map(PedidoFila::id).toList())   // una consulta, no N
                .stream().collect(Collectors.groupingBy(LineaFila::pedidoId));
        return filas.stream().map(f -> PedidoMapper.aDominio(f, lineas.getOrDefault(f.id(), List.of()))).toList();
    }

    @Override
    public void guardar(Pedido p) {
        var f = PedidoMapper.aFila(p);
        try {
            int filas = jdbc.sql("""
                    INSERT INTO pedido (id, numero, cliente_id, estado, version)
                    VALUES (:id, :numero, :cliente, :estado, :version + 1)
                    ON CONFLICT (id) DO UPDATE
                       SET estado = EXCLUDED.estado, version = EXCLUDED.version
                     WHERE pedido.version = :version""")                 // TODO P9-P10
                    .param("id", f.id()).param("numero", f.numero()).param("cliente", f.clienteId())
                    .param("estado", f.estado()).param("version", f.version())
                    .update();
            if (filas == 0) throw new ConflictoDeVersion(p.id(), f.version());
        } catch (DuplicateKeyException e) {                           // UNIQUE(numero); TODO P7
            throw new PedidoDuplicado(p.numero());                    // la tx ya está abortada: no sigas
        }
        reemplazarLineas(p);                                           // TODO P13
    }

    private void reemplazarLineas(Pedido p) {
        jdbc.sql("DELETE FROM linea_pedido WHERE pedido_id = :id").param("id", p.id().valor()).update();
        var params = PedidoMapper.aFilas(p).stream()
                .map(l -> new MapSqlParameterSource().addValue("pedido", l.pedidoId())
                        .addValue("sku", l.sku()).addValue("cantidad", l.cantidad()))
                .toArray(SqlParameterSource[]::new);
        lotes.batchUpdate("INSERT INTO linea_pedido (pedido_id, sku, cantidad) VALUES (:pedido, :sku, :cantidad)", params);
    }

    private List<LineaFila> lineasDe(List<UUID> ids) {
        if (ids.isEmpty()) return List.of();
        return jdbc.sql("SELECT pedido_id, sku, cantidad FROM linea_pedido WHERE pedido_id IN (:ids) ORDER BY sku")
                .param("ids", ids).query(LineaFila.class).list();
    }
}
```

### 10.4 · Variante jOOQ de `guardar`

Basado en §9.5 y §9.6. Requiere las clases generadas (`PEDIDO`) desde las migraciones.

```java
int filas = dsl.insertInto(PEDIDO)
        .set(PEDIDO.ID, f.id()).set(PEDIDO.NUMERO, f.numero()).set(PEDIDO.CLIENTE_ID, f.clienteId())
        .set(PEDIDO.ESTADO, f.estado()).set(PEDIDO.VERSION, f.version() + 1)
        .onConflict(PEDIDO.ID).doUpdate()                            // nombra la clave (no copies §9.5)
        .set(PEDIDO.ESTADO, f.estado()).set(PEDIDO.VERSION, f.version() + 1)
        .where(PEDIDO.VERSION.eq(f.version()))                       // TODO P10
        .execute();
if (filas == 0) throw new ConflictoDeVersion(p.id(), f.version());
```

### 10.5 · Variante JPA (si P3 = JPA)

Basado en §9.1 y en Spring Data JPA, *Entity persistence* (versión no primitiva ⇒ `save` sabe qué es
nuevo).

```java
@Entity @Table(name = "pedido")
class PedidoJpa {
    @Id UUID id;                                      // TODO P6: asignado por la app
    @Version Long version;                            // no primitivo: null = nuevo (Spring Data JPA)
    String numero; UUID clienteId; String estado;
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<LineaJpa> lineas = new ArrayList<>();
    protected PedidoJpa() {}
}
interface PedidoJpaRepository extends Repository<PedidoJpa, UUID> {   // sin findAll heredado
    @Query("select p from PedidoJpa p left join fetch p.lineas where p.id = :id")  // TODO P17
    Optional<PedidoJpa> conLineas(UUID id);
    PedidoJpa save(PedidoJpa p);
}
// application.yml: spring.jpa.open-in-view: false · hibernate.jdbc.batch_size: 30   TODO PR-20
// ObjectOptimisticLockingFailureException → ConflictoDeVersion en el adaptador
```

### 10.6 · Implementación en memoria y prueba de contrato

Basado en §9.7. La memoria emula la misma regla de versión que el SQL de §10.3.

```java
public final class PedidosEnMemoria implements Pedidos {
    private final Map<PedidoId, Pedido> datos = new ConcurrentHashMap<>();

    public Optional<Pedido> porId(PedidoId id) { return Optional.ofNullable(datos.get(id)).map(PedidosEnMemoria::copia); }

    public void guardar(Pedido p) {
        datos.compute(p.id(), (id, actual) -> {
            long enBase = actual == null ? 0 : actual.version();
            if (enBase != p.version()) throw new ConflictoDeVersion(id, p.version());
            boolean numeroUsado = datos.values().stream()
                    .anyMatch(o -> !o.id().equals(id) && o.numero().equals(p.numero()));
            if (numeroUsado) throw new PedidoDuplicado(p.numero());
            return copia(p, enBase + 1);
        });
    }

    public List<Pedido> pendientesDe(ClienteId c, int limite) {
        return datos.values().stream()
                .filter(p -> p.cliente().equals(c) && p.estado() == EstadoPedido.CREADO)
                .sorted(Comparator.comparing(p -> p.id().valor())).limit(limite)
                .map(PedidosEnMemoria::copia).toList();
    }

    private static Pedido copia(Pedido p) { return copia(p, p.version()); }
    private static Pedido copia(Pedido p, long v) {                   // TODO P4: copia profunda
        return Pedido.reconstituir(p.id(), p.numero(), p.cliente(), p.estado(), List.copyOf(p.lineas()), v);
    }
}

public abstract class PedidosContratoTest {
    protected Pedidos pedidos;
    protected abstract Pedidos crear();
    @BeforeEach void preparar() { pedidos = crear(); }

    @Test void guardaYRecuperaIgual() {                               // PR-19, uno por estado
        var p = PedidoMadre.creado();
        pedidos.guardar(p);
        assertThat(pedidos.porId(p.id())).get().usingRecursiveComparison()
                .ignoringFields("version").isEqualTo(p);
    }
    @Test void idDesconocidoDevuelveVacio() { assertThat(pedidos.porId(PedidoId.nuevo())).isEmpty(); }
    @Test void versionViejaLanzaConflicto() {                         // PR-06: viola
        var p = PedidoMadre.creado();
        pedidos.guardar(p);
        assertThatThrownBy(() -> pedidos.guardar(p)).isInstanceOf(ConflictoDeVersion.class);
    }
    @Test void versionLeidaActualiza() {                              // PR-06: cumple
        var p = PedidoMadre.creado();
        pedidos.guardar(p);
        var leido = pedidos.porId(p.id()).orElseThrow();
        leido.confirmar();
        pedidos.guardar(leido);
        assertThat(pedidos.porId(p.id()).orElseThrow().estado()).isEqualTo(EstadoPedido.CONFIRMADO);
    }
    @Test void numeroRepetidoLanzaDuplicado() {                       // PR-05
        pedidos.guardar(PedidoMadre.conNumero("P-1"));
        assertThatThrownBy(() -> pedidos.guardar(PedidoMadre.conNumero("P-1")))
                .isInstanceOf(PedidoDuplicado.class);
    }
}

class PedidosEnMemoriaContratoTest extends PedidosContratoTest {
    protected Pedidos crear() { return new PedidosEnMemoria(); }
}

@JdbcTest                                   // Boot 4: org.springframework.boot.jdbc.test.autoconfigure
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(PedidosJdbc.class)
@Testcontainers
class PedidosJdbcContratoIT extends PedidosContratoTest {
    @Container @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");  // TODO: imagen de producción
    @Autowired PedidosJdbc real;
    protected Pedidos crear() { return real; }   // Flyway corre al arrancar el contexto; TODO: confirmar en tu Boot
}
```

`@JdbcTest` hace rollback de cada prueba y deja abierta la transacción que pide `MANDATORY`. Las
pruebas de concurrencia (dos hilos) necesitan commits reales: van aparte, sin `@JdbcTest`, como en
`JdbcSessionRepositoryPostgresqlIT` (§9.7).

### 10.7 · Migración Flyway

Basado en §9.6 (`V1__create_tables.sql`) y §9.8. Nombre: `V<versión>__<descripción>.sql`.

```sql
-- V1__pedido.sql                              TODO P5, P7, P8, P16
CREATE TABLE pedido (
    id          uuid PRIMARY KEY,
    numero      text   NOT NULL,
    cliente_id  uuid   NOT NULL,
    estado      text   NOT NULL CHECK (estado IN ('CREADO', 'CONFIRMADO', 'CANCELADO')),
    version     bigint NOT NULL,
    CONSTRAINT pedido_numero_unico UNIQUE (numero)
);
CREATE TABLE linea_pedido (
    pedido_id uuid    NOT NULL REFERENCES pedido (id) ON DELETE CASCADE,
    sku       text    NOT NULL,
    cantidad  integer NOT NULL CHECK (cantidad > 0),
    PRIMARY KEY (pedido_id, sku)               -- la PK empieza por la FK: sirve de índice
);
CREATE INDEX pedido_cliente_estado_idx ON pedido (cliente_id, estado);

-- TODO P14 (si hay clave de idempotencia)
CREATE TABLE comando_procesado (
    clave        text        PRIMARY KEY,
    resultado    jsonb       NOT NULL,
    procesado_en timestamptz NOT NULL
);
-- TODO P15: el esquema de event_publication (§9.8) va en su propia migración.
```

Cambio destructivo en dos versiones (Parallel Change): `V4__pedido_add_canal.sql` añade la columna
nullable; el código escribe en ambas; `V5__…` rellena y pone `NOT NULL`; la columna vieja se borra en
un despliegue posterior.

### 10.8 · Regla de arquitectura

```java
@ArchTest
static final ArchRule persistenciaEncerrada = noClasses()
        .that().resideOutsideOfPackage("..infraestructura.salida.persistencia..")
        .should().dependOnClassesThat().resideInAnyPackage(
                "org.springframework.jdbc..", "jakarta.persistence..", "org.jooq..");   // PR-02, PR-03
```

## 11 · Qué se genera

Orden dentro del segmento de persistencia (`investigacion/01` regla 21: dominio antes que
persistencia):

1. `V<n>__<agregado>.sql` (migración). Se valida al arrancar la prueba.
2. Puerto `Pedidos` y sus excepciones, si `caso-de-uso` no los dejó escritos.
3. `PedidosEnMemoria` + `PedidosContratoTest` + `PedidosEnMemoriaContratoTest` (corren en < 1 s).
4. `PedidoFila`, `LineaFila`, `PedidoMapper`.
5. `PedidosJdbc` (o la variante elegida en P3).
6. `PedidosJdbcContratoIT` (Testcontainers) y, si P10 o P11 lo piden, una prueba de concurrencia aparte.
7. La regla ArchUnit de §10.8 en la prueba de arquitectura del proyecto.

Tamaño típico: 350-500 líneas brutas; las pruebas cuentan 0,5 y la migración 0,25
(`investigacion/05` §1.2).

## 12 · Referencias

- Martin Fowler, *Patterns of Enterprise Application Architecture* (2002). Catálogo: *Repository* https://martinfowler.com/eaaCatalog/repository.html · *Data Mapper* https://martinfowler.com/eaaCatalog/dataMapper.html · *Unit of Work* https://martinfowler.com/eaaCatalog/unitOfWork.html · *Identity Map* https://martinfowler.com/eaaCatalog/identityMap.html · *Active Record* https://martinfowler.com/eaaCatalog/activeRecord.html · *Lazy Load* https://martinfowler.com/eaaCatalog/lazyLoad.html · *Optimistic Offline Lock* https://martinfowler.com/eaaCatalog/optimisticOfflineLock.html · *Pessimistic Offline Lock* https://martinfowler.com/eaaCatalog/pessimisticOfflineLock.html — definiciones de los patrones de §3.
- Eric Evans, *Domain-Driven Design* (2003) y *DDD Reference* https://www.domainlanguage.com/ddd/reference/ — repositorios solo para raíces de agregado.
- Vaughn Vernon, *Implementing Domain-Driven Design* (2013), cap. «Repositories» — repositorios orientados a colección frente a orientados a persistencia; *Effective Aggregate Design* https://www.dddcommunity.org/library/vernon_2011/ — un agregado por transacción.
- Tom Hombergs, *Get Your Hands Dirty on Clean Architecture*, 2.ª ed. https://leanpub.com/get-your-hands-dirty-on-clean-architecture — responsabilidades del adaptador de persistencia, puertos partidos, transacción en el servicio, estrategias de mapeo; artículo https://reflectoring.io/spring-hexagonal/.
- Spring Data JDBC, «Domain Driven Design» https://docs.spring.io/spring-data/relational/reference/jdbc/domain-driven-design.html y «Why Spring Data JDBC» https://docs.spring.io/spring-data/relational/reference/jdbc/why.html — un repositorio por raíz; sin lazy load, caché ni dirty tracking; hijas borradas y recreadas.
- Spring Framework, «Using the JDBC Core Classes» (`JdbcClient`) https://docs.spring.io/spring-framework/reference/data-access/jdbc/core.html — API fluida, `optional()`, `list()`; lotes con `JdbcTemplate`.
- Spring Data JPA, «Persisting Entities» https://docs.spring.io/spring-data/jpa/reference/jpa/entity-persistence.html — `persist` frente a `merge`, `Persistable` con id asignado.
- jOOQ manual, «jOOQ and JPA» https://www.jooq.org/doc/latest/manual/getting-started/jooq-and-jpa/ — criterio para elegir JPA o jOOQ.
- Vlad Mihalcea: *N+1 query problem* https://vladmihalcea.com/n-plus-1-query-problem/ · *Batch insert and update* https://vladmihalcea.com/how-to-batch-insert-and-update-statements-with-hibernate/ · *IDENTITY y SEQUENCE* https://vladmihalcea.com/hibernate-identity-sequence-and-table-sequence-generator/ · *Optimistic vs pessimistic locking* https://vladmihalcea.com/optimistic-vs-pessimistic-locking/ · *Best Spring Data JpaRepository* https://vladmihalcea.com/best-spring-data-jparepository/ · *Open Session in View* https://vladmihalcea.com/the-open-session-in-view-anti-pattern/ — N+1, lotes, bloqueos y trampas de JPA.
- Martin Kleppmann, *Designing Data-Intensive Applications* (2017), cap. 7 «Transactions» — read committed, snapshot isolation, lost updates, write skew, serializabilidad.
- PostgreSQL 16+: *Transaction Isolation* https://www.postgresql.org/docs/current/transaction-iso.html (Read Committed por defecto, reintento ante 40001) · *INSERT* https://www.postgresql.org/docs/current/sql-insert.html (`ON CONFLICT`, `EXCLUDED`) · *Constraints* https://www.postgresql.org/docs/current/ddl-constraints.html (índice en columnas que referencian) · *CREATE INDEX* https://www.postgresql.org/docs/current/sql-createindex.html (`CONCURRENTLY`).
- Scott Ambler y Pramod Sadalage, *Refactoring Databases* (2006) — refactorizaciones de BD con periodo de transición.
- Pramod Sadalage y Martin Fowler, *Evolutionary Database Design* https://martinfowler.com/articles/evodb.html — cada cambio es una migración, pasos pequeños, fase de transición; Fowler, *ParallelChange* https://martinfowler.com/bliki/ParallelChange.html.
- Redgate Flyway, *Versioned migrations* https://documentation.red-gate.com/fd/versioned-migrations-273973333.html — versión única, aplicada una vez, checksum, no editar lo aplicado.
- microservices.io, *Transactional Outbox* https://microservices.io/patterns/data/transactional-outbox.html; Spring Modulith, *Events* https://docs.spring.io/spring-modulith/reference/events.html — outbox y entrega al menos una vez.
- Spring Boot, *Testcontainers* https://docs.spring.io/spring-boot/reference/testing/testcontainers.html — `@ServiceConnection`, ciclo de vida de contenedores estáticos; Testcontainers, guía *Replace H2 with a real database* https://github.com/testcontainers/tc-guide-replace-h2-with-real-database-for-testing.
- J. B. Rainsberger, *Getting Started with Contract Tests* https://blog.thecodewhisperer.com/permalink/getting-started-with-contract-tests — prueba abstracta con fábrica que corre contra cada implementación.
- OWASP, *SQL Injection Prevention Cheat Sheet* https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html — consultas parametrizadas.
- `investigacion/02` (reglas ArchUnit, mapeo), `investigacion/03` (agregados, versión, idempotencia), `investigacion/04` §3.6 (banco base de esta plantilla), `investigacion/05` (tamaño del segmento).
