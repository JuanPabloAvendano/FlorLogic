REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 8-oct-2026, a pedido de Juan
Estado de revisión: POR ACEPTAR — cada fila TEC-nn se acepta, se ajusta o se rechaza por separado en `IA_00-LEEME-transacciones.md`
Manda por encima: `ADR.xlsx` (con `ADR-037`, Java) · `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx` · la decisión de Juan del 8-oct

# Tecnologías, módulos y patrones para TX-1 y TX-2

## 0 · Lo que ya decidió Juan

> **Juan, 8-oct-2026:** «también es el momento de terminar de definir tecnologías, pero vamos con lo
> rápido para nosotros, java spring boot maven para los principales servicios».

- **Lenguaje:** Java, que ya fija `ADR-037`, aceptada.
- **Armazón:** **Spring Boot.** **Decisión de Juan del 8-oct, pendiente de ADR.** Con esto
  `SPK-18`, que comparaba Spring Boot, Quarkus y Micronaut y nunca se midió, **deja de hacer falta**.
  `ADR-037` dice «Esta decisión no elige el armazón sobre el que se construye el servicio» y «Se decide aparte»: este es ese «aparte». El ADR lo escribes tú.
- **Construcción:** **Maven.**
- **«Los principales servicios»** se lee así: el backend de la finca, los servicios en línea y el
  simulador. El gateway y la base son infraestructura y se eligen abajo.

Lo de abajo es **todo propuesta**. El criterio es el tuyo: **lo rápido para nosotros**, que significa
pocas piezas, todo del mismo ecosistema y lo ya medido por encima de lo nuevo.

---

## 1 · Tecnologías propuestas

| ID | Para qué | Propuesta | Alternativa | Por qué, en una línea |
|---|---|---|---|---|
| **TEC-01** | Versión de Java | **Java 21 LTS** | Java 25 LTS | Es la que midieron `SPK-17` y `SPK-15` |
| **TEC-02** | Versión del armazón | **Spring Boot 4.1.x** (4.1.0 salió el 10-jun-2026 y 4.1.1 el 20-ago-2026) | Spring Boot 3.5 | La línea con soporte vigente; es la que `SPK-18` había fijado |
| **TEC-03** | Construcción | **Maven multi-módulo, Maven Wrapper y construcción reproducible** (`project.build.outputTimestamp`) | — | Dos construcciones del mismo código deben dar el mismo digest (`ADR-016`; lo midió `SPK-15`) |
| **TEC-04** | Estilo de servicio web | **Spring Web MVC con hilos virtuales** | WebFlux | Código directo y fácil de depurar entre dos personas; los hilos virtuales quitan el costo de bloquear |
| **TEC-05** | Forma del backend | **Monolito modular con Spring Modulith**; una prueba falla si un módulo toca la parte interna de otro | Paquetes vigilados con ArchUnit | `ADR-017` excluye partir en servicios independientes, y Modulith hace que los límites se verifiquen |
| **TEC-06** | Base de la finca | **PostgreSQL 16** | PostgreSQL 17 o 18 | Es la medida en `SPK-13`, `SPK-15` y `SPK-17`. `SPK-10` sigue aparcado: **al aceptar esto se cierra** |
| **TEC-07** | Acceso a datos | **Spring JDBC (`JdbcClient`)** con SQL escrito a mano | Spring Data JDBC · JPA/Hibernate | La tabla de eventos es de solo inserción y lleva `JSONB` (`ADR-024`); un ORM ahí estorba más de lo que ayuda |
| **TEC-08** | Migraciones | **Flyway**, cada migración en su transacción, y en dos tiempos: primero se añade, después se quita | Liquibase | `SPK-15` midió 0 residuo con migraciones transaccionales y una base atascada sin ellas |
| **TEC-09** | Cola de trabajos en la misma base (`ADR-009`) | **db-scheduler**: una tabla en PostgreSQL; cada trabajo lo corre una sola instancia a la vez, y si el proceso muere se retoma. Verificarlo con un `kill -9`, como pedía `SPK-18` | JobRunr · tabla propia con `SKIP LOCKED` | No agrega ninguna pieza; es una tabla más |
| **TEC-10** | JSON y huella | **Jackson**, más una **forma canónica** para la huella: orden fijo, números sin `1.0`, sin cultura local | — | `SPK-17`: en Java, `1.0` en vez de `1` cambia más de la mitad de los mensajes, y la coma `es-CO` cambia el 36,6 % |
| **TEC-11** | Contrato | **Módulo `contrato`** con los DTO como `record` de Java y un **JSON Schema versionado**; una prueba falla si el contrato cambia sin subir su versión | OpenAPI escrito primero, con el código generado | El simulador y el backend comparten el mismo módulo. Al futuro cliente Android se le da el esquema |
| **TEC-12** | Identificadores | **UUID v7** generado en el cliente; tipo `uuid` nativo en PostgreSQL | — | `ADR-027` |
| **TEC-13** | Credencial firmada sin red | **JWS con Ed25519**, hecha con Nimbus JOSE+JWT, la biblioteca que usa el módulo OAuth2 de Spring Security | PASETO | `ADR-007`; Nimbus ya está en el ecosistema |
| **TEC-14** | Seguridad web | **Spring Security 7**, que viene con Boot 4.1 | — | — |
| **TEC-15** | API Gateway | **Nginx**: TLS, límite de tasa, sirve la web estática y reenvía al backend | Caddy (certificados internos automáticos) · Spring Cloud Gateway (otra JVM más) | Una pieza madura y liviana que hace las tres cosas que pide el modelo |
| **TEC-16** | TLS en la red local sin internet | **Autoridad certificadora propia de la finca**, creada en la instalación; el celular confía en ella al registrarse | — | El modelo deja abierto «el certificado del gateway sin internet»; esto lo cierra |
| **TEC-17** | Observabilidad local | **Spring Boot Actuator y Micrometer**; **logs estructurados JSON**, que Spring Boot trae; correlación por el id de la sesión o del trabajo | Prometheus y Grafana en el nodo (dos piezas más) | Cero piezas nuevas en el nodo |
| **TEC-18** | Telemetría a la nube | **Servicio propio con esquema cerrado**: lista blanca y prueba de contrato. Envío periódico encolado | OpenTelemetry con su colector | `CN-34`/`ESC-30`: lo que sale tiene que ser verificable campo por campo |
| **TEC-19** | Volcado de la base | **`pg_dump` en formato custom**, lanzado desde Java | `pgBackRest` o `WAL-G` para archivado continuo | Medido en `SPK-13`; el archivado continuo no se probó |
| **TEC-20** | Deduplicar y cifrar el respaldo | **restic**: deduplica antes de cifrar, cifra con AES-256 y habla S3 | `pg_dump` más Google Tink en Java (cifrado de sobre), sin deduplicar | `SPK-13`: deduplicar antes de cifrar da 1,62 GB contra 813 GB. **restic es una pieza externa escrita en Go: decídelo tú** |
| **TEC-21** | Almacén de objetos | **Compatible con S3** y con **bloqueo de retención**; cliente AWS SDK v2. MinIO para pruebas | — | Ningún proveedor amarra; el bloqueo protege contra el borrado (ES-R12) |
| **TEC-22** | Servicios en línea | **Spring Boot 4.1** (mismo ecosistema), PostgreSQL y el panel con **Thymeleaf** | — | El modelo pide «vistas web generadas en el servidor» |
| **TEC-23** | Finca → nube | **TLS mutuo**, con un certificado de cliente por finca emitido al registrar la instalación | — | El modelo ya lo dibuja |
| **TEC-24** | Aplicación web, solo lo de TX-1 y TX-2 | **Thymeleaf con htmx**, servido por el backend | Una aplicación de una sola página (React o Vite), que es lo que dibuja el modelo | **Todo en Java** y sin una cadena de JavaScript aparte. **Choca con el modelo**: decídelo tú |
| **TEC-25** | Simulador de contrato | **Programa de línea de comandos en Java 21** con picocli, el cliente HTTP de Java y **SQLite** como su almacén de salida | — | Reutiliza `contrato`; SQLite es lo más parecido al almacén de un celular Android |
| **TEC-26** | Pruebas | **JUnit 5, AssertJ y Testcontainers** con PostgreSQL real, MinIO y **Toxiproxy** para cortar la red a voluntad; **casos dorados** de reglas | Bases en memoria | Las tarjetas del event storming se vuelven pruebas automáticas |
| **TEC-27** | Motor de reglas en el servidor | **Reglas en JSON** (decisión A de Juan, 15-sep) evaluadas con **JSON Logic** en Java: el camino B de `SPK-09` | CEL en Java · evaluador propio | El camino B midió 0 % de divergencia en el veredicto y versiona reglas sin código. **El tipo de regla desconocido debe fallar en voz alta** (`AB-01`) |
| **TEC-28** | Empaquetado e instalación | **Imagen OCI construida con Jib** (sin Dockerfile, reproducible), para **amd64 y arm64**, y **Docker Compose** en el nodo con el backend, PostgreSQL y Nginx | `jlink` más un servicio del sistema (`SPK-17`: 55,2 MB) | Un solo archivo de infraestructura (`ADR-016`); arm64 abre OC-3 y OC-5 |
| **TEC-29** | Sistema operativo del nodo | **Debian 13 o Ubuntu 24.04 LTS** | Windows con WSL2 | El nodo físico sigue abierto (`CN-20`); Linux es lo que se ha medido |
| **TEC-30** | Integración continua | **GitHub Actions** (el repo ya está en GitHub): construir, probar con Testcontainers y publicar la imagen firmada | — | El modelo tiene «Integración continua del equipo» como sistema externo |

---

## 2 · Estructura de Maven

```
florlogic-codigo/                      ← ¿repo aparte o carpeta en la rama codigo-desarrollo? (P-T01)
├── pom.xml                            padre: versiones, plugins y construcción reproducible
├── contrato/                          DTO (records), JSON Schema versionado y huella canónica
├── backend-finca/                     Spring Boot · monolito modular · TX-1 y TX-2 del lado de la finca
├── servicios-en-linea/                Spring Boot · recepción de respaldos, telemetría, panel
├── simulador/                         línea de comandos · el celular simulado
├── pruebas-extremo-a-extremo/         Testcontainers: finca + nube + simulador juntos
└── despliegue/                        compose.yaml del nodo, Nginx y guion de datos de prueba
```

## 3 · Paquetes del backend: un módulo por componente del C4

Cada módulo es un paquete de primer nivel que Spring Modulith verifica. Por dentro, **puertos y
adaptadores**: lo de afuera entra por los adaptadores y el dominio no conoce Spring.

```
co.florlogic.finca                         ← nombre base: P-T02
├── ingesta/          Ingreso de sincronización                       TX-1
├── reglas/           Motor de reglas · instancia servidor            TX-1
├── configuracion/    Datos maestros y parametrización versionada     TX-1
├── dispositivos/     Gestión de dispositivos y asignación            TX-1
├── identidad/        Identidad y permisos + Emisión de credenciales  TX-1 · TX-2
├── llaves/           Custodia de llaves                              TX-1 (firma) · TX-2 (cifra)
├── bitacora/         Bitácora de auditoría                           TX-1 · TX-2
├── ciclo/            Ciclo de producción                             TX-1
├── notificacion/     Servicio de notificación                        TX-1 · TX-2
├── trabajos/         Planificador · cola en la base                  TX-1 · TX-2
├── proyeccion/       Motor de proyección (simulado)                  TX-1
├── consulta/         Consulta y tableros + caché                     TX-1 · TX-2
├── respaldo/         Servicio de respaldo                            TX-2
├── observabilidad/   Métricas, logs, telemetría con esquema cerrado  TX-1 · TX-2
└── plataforma/       Seguridad web, migraciones, configuración       todos

Dentro de cada módulo:
  <modulo>/              la API pública del módulo: lo único que otros pueden usar
  <modulo>/dominio/      reglas y entidades, sin Spring
  <modulo>/aplicacion/   casos de uso: un servicio por caso de uso
  <modulo>/adaptadores/  web (controladores), jdbc (repositorios), cliente http
```

## 4 · Patrones, con nombre y lugar

| Patrón | Dónde | Para qué |
|---|---|---|
| **Almacén de salida** (outbox) | Simulador / celular | No borrar hasta tener acuse (`ADR-002`) |
| **Receptor idempotente con huella** | `ingesta` | Los tres casos de `ADR-027` |
| **Registro de solo inserción** | Tabla de eventos y `bitacora` | Nada se modifica (`ESC-40`) |
| **El estado es una vista** | `consulta` | Quién gana se calcula al leer, no al escribir (`SPK-12` §3) |
| **Tablas de consulta** (CQRS ligero) | `consulta` | Ninguna pantalla sobre la tabla de eventos (`ADR-010`, propuesta) |
| **Eventos internos con registro de publicación** | `ingesta` → `notificacion`, `proyeccion`, `consulta` | Que un aviso no se pierda si el proceso muere después de guardar (lo trae Spring Modulith) |
| **Cola de trabajos en la base** | `trabajos` | `ADR-009` |
| **Reintento con espera creciente y algo de azar** | Simulador, subida del respaldo | Que trece celulares no reintenten en el mismo segundo |
| **Cifrado de sobre** | `respaldo` + `llaves` | Una llave por respaldo, envuelta con la de la empresa |
| **Esquema cerrado (lista blanca)** | `observabilidad` | Que ningún dato de negocio salga por la telemetría |
| **Puertos y adaptadores** | Todos los módulos | Probar el dominio sin base y sin red |

## 5 · Prácticas de trabajo propuestas

| ID | Práctica | Propuesta |
|---|---|---|
| PR-01 | Idioma del código | **El dominio en español** (`Cama`, `Seccion`, `SesionCaptura`), que es el lenguaje del cliente y de los ADR. Lo técnico en el idioma del armazón |
| PR-02 | Ramas | Las que ya existen: `codigo-desarrollo` → `codigo-testing` → `codigo-listo`. Una rama corta por tarjeta o caso de uso |
| PR-03 | Revisión | **Nada entra a `codigo-desarrollo` sin que lo revise el otro**; con dos personas es la única segunda mirada |
| PR-04 | Cuándo está terminado | Cada tarjeta ES con «Prueba» tiene su prueba automática en verde. La transacción está terminada cuando pasan todas las de su §10 |
| PR-05 | Commits y empujes | Los hace Juan, nunca la IA (regla del 22-sep) |
| PR-06 | Secretos | Ninguna llave en el repositorio. Las llaves del nodo viven en un almacén PKCS#12 sobre el disco cifrado |

---

## 6 · Lo que tienes que decidir

| ID | Pregunta | Recomendación |
|---|---|---|
| **P-T01** | ¿El código va en un repo aparte o en este repo, en la rama `codigo-desarrollo`? | **Este repo, carpeta `codigo/`, en `codigo-desarrollo`**: las ramas ya existen |
| **P-T02** | ¿Nombre base de los paquetes? | `co.florlogic` |
| **P-T03** | ¿Java 21 o 25? | **21**, el medido |
| **P-T04** | ¿restic (pieza externa en Go) o todo en Java con Tink, sin deduplicar? | **restic**, por los 813 GB contra 1,62 GB |
| **P-T05** | ¿La web de TX-1 y TX-2 con Thymeleaf y htmx, o la aplicación de una sola página del modelo? | **Thymeleaf y htmx para empezar**: si se acepta, el modelo cambia |
| **P-T06** | ¿Nginx como gateway? | **Sí** |
| **P-T07** | ¿Docker Compose en el nodo? | **Sí**, con la imagen multi-arquitectura |

---

Fuentes de las versiones citadas:
- [Spring Boot 4.1.0 available now — spring.io, 10-jun-2026](https://spring.io/blog/2026/06/10/spring-boot-4/)
- [Spring Boot 4.1.1 available now — spring.io, 20-ago-2026](https://spring.io/blog/2026/08/20/spring-boot-4-1-1-available-now/)
