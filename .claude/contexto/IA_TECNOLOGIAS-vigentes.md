REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 15-oct-2026, a pedido de Juan
Estado de revisión: SIN REVISAR
Manda por encima: `IA_00-LEEME-transacciones.md` (aceptación) · `IA_TECNOLOGIAS-y-estructura.md` (motivos) · `ADR.xlsx` — los tres en `documentacion-desarrollo`

# Tecnologías vigentes del código

Registro vivo de **qué tecnología usa el código, con qué versión exacta y en qué estado está**. Se
actualiza en cada sesión que cambie una versión o meta una pieza nueva. Los motivos de cada decisión
no se repiten aquí: están en `IA_TECNOLOGIAS-y-estructura.md`.

**Estados:**
- `ACEPTADO <fecha>`: Juan lo aceptó en `IA_00-LEEME-transacciones.md`. Se puede usar.
- `PROPUESTA <fecha>`: lo sugiere una sesión. **No se usa** hasta que Juan lo acepte.
- `AJUSTADO <fecha> · <qué>`: Juan lo cambió después de aceptarlo.
- `RECHAZADO <fecha> · <por qué>`.

**En el código:** `SÍ` (está en un `pom.xml` o en `despliegue/`) · `NO` (aceptado pero nadie lo ha
necesitado todavía).

## 1 · Aclaraciones: el código frente a lo aceptado el 13-oct

Lo aceptado el 13-oct y el código ya no coinciden en estos puntos. **Manda el código**, que es lo
último que Juan fijó. Falta llevar cada aclaración al documento de motivos.

| # | Qué decía lo aceptado | Qué hay en el código | De dónde sale | Falta |
|---|---|---|---|---|
| A-1 | TEC-01 · **Java 21 LTS** | **Java 27** (`maven.compiler.release 27`), JDK **Temurin 27.0.0+35** | Juan, 14-oct-2026 (comentario en `pom.xml`, commit `fa244b4`) | Juan cambia TEC-01 a `AJUSTADO 14-oct-2026 · Java 27` en el LEEME de transacciones. Java 27 **no es LTS** (la siguiente es la 29): queda anotado |
| A-2 | Java 27 sin efectos en el resto | **ArchUnit 1.5.1** forzado: el 1.4.2 que trae Modulith 2.1.1 no lee clases de Java 27 | `pom.xml` | Quitar el forzado cuando Modulith traiga un ArchUnit que lea Java 27 |
| A-3 | TEC-26 · **JUnit 5** | **JUnit 6.0.3**, el que trae Spring Boot 4.1.1 | `spring-boot-starter-parent` | Solo nombre: decir «JUnit» sin número en los documentos |
| A-4 | §3 · dentro de cada módulo: `dominio/`, `aplicacion/`, `adaptadores/` | `dominio/`, `servicios/casosuso/`, `servicios/puertos/`, `infraestructura/entrada/`, `infraestructura/salida/` | Juan, 14-oct-2026 (commit `fa244b4`) | Actualizar §3 de `IA_TECNOLOGIAS-y-estructura.md` |
| A-5 | TEC-10 · Jackson | **Jackson 3** (`tools.jackson`), no Jackson 2 (`com.fasterxml`) | Spring Boot 4.1 | Ninguna; al buscar ejemplos, usar los paquetes de Jackson 3 |
| A-6 | El simulador como «programa de línea de órdenes» | Corre **sobre Spring Boot sin servidor web**; picocli sigue siendo la línea de órdenes | `simulador/pom.xml` | Ninguna; queda dicho |

## 2 · Lo aceptado, con su versión en el código

Versiones comprobadas con `./mvnw dependency:list` el 15-oct-2026.

| ID | Qué | Pieza y versión | Estado | En el código |
|---|---|---|---|---|
| TEC-01 | Lenguaje | Java 27 · Temurin 27.0.0+35 | AJUSTADO 14-oct-2026 · ver A-1 | SÍ |
| TEC-02 | Armazón | Spring Boot 4.1.1 · Spring Framework 7.0.9 | ACEPTADO 13-oct-2026 | SÍ |
| TEC-03 | Construcción | Maven 3.9.16 por Maven Wrapper 3.3.4 · `outputTimestamp` fijo | ACEPTADO 13-oct-2026 | SÍ |
| TEC-04 | Servicio web | Spring Web MVC con hilos virtuales | ACEPTADO 13-oct-2026 | SÍ |
| TEC-05 | Monolito modular | Spring Modulith 2.1.1 · ArchUnit 1.5.1 | ACEPTADO 13-oct-2026 | SÍ |
| TEC-06 | Base de la finca | PostgreSQL 16 (imagen `postgres:16`) · controlador 42.7.13 | ACEPTADO 13-oct-2026 | SÍ |
| TEC-07 | Acceso a datos | Spring JDBC (`JdbcClient`) | ACEPTADO 13-oct-2026 | SÍ (`AnotacionesRecibidasJdbc`) |
| TEC-08 | Migraciones | Flyway 12.4.0 | ACEPTADO 13-oct-2026 | SÍ (`V1__base.sql`, `V2__anotacion_recibida.sql`) |
| TEC-09 | Cola de trabajos | db-scheduler | ACEPTADO 13-oct-2026 | NO |
| TEC-10 | JSON y huella | Jackson 3.1.5 · forma canónica propia | ACEPTADO 13-oct-2026 | SÍ (solo Jackson) |
| TEC-11 | Contrato | Records + JSON Schema versionado | ACEPTADO 13-oct-2026 | PARCIAL (records del Segmento 1; esquemas sin biblioteca, ver §4 TEC-11) |
| TEC-12 | Identificadores | UUID v7 · `uuid` nativo | ACEPTADO 13-oct-2026 | NO |
| TEC-13 | Credencial | JWS Ed25519 con Nimbus JOSE+JWT | ACEPTADO 13-oct-2026 | NO |
| TEC-14 | Seguridad web | Spring Security 7 | ACEPTADO 13-oct-2026 | NO |
| TEC-15 | Gateway | Nginx 1.30.6 (imagen `nginx:1.30.6-alpine`, estable del 14-oct-2026; amd64 y arm64) | ACEPTADO 13-oct-2026 | SÍ (`despliegue/gateway/`, `compose.yaml`) |
| TEC-16 | TLS local | Autoridad certificadora de la finca | ACEPTADO 13-oct-2026 | NO |
| TEC-17 | Observabilidad local | Actuator · Micrometer 1.17.1 · logs JSON | ACEPTADO 13-oct-2026 | SÍ (Actuator) |
| TEC-18 | Telemetría | Servicio propio con esquema cerrado | ACEPTADO 13-oct-2026 | NO |
| TEC-19 | Volcado | `pg_dump` formato custom | ACEPTADO 13-oct-2026 | NO |
| TEC-20 | Deduplicar y cifrar | restic | ACEPTADO 13-oct-2026 | NO |
| TEC-21 | Almacén de objetos | S3 con bloqueo de retención · AWS SDK v2 · MinIO en pruebas | ACEPTADO 13-oct-2026 | NO |
| TEC-22 | Servicios en línea | Spring Boot 4.1.1 · Thymeleaf | ACEPTADO 13-oct-2026 | SÍ (sin Thymeleaf) |
| TEC-23 | Finca → nube | TLS mutuo | ACEPTADO 13-oct-2026 | NO |
| TEC-24 | Web de TX-1 y TX-2 | Thymeleaf con htmx | ACEPTADO 13-oct-2026 | NO |
| TEC-25 | Simulador | picocli 4.7.7 · SQLite (`sqlite-jdbc` 3.50.3.0) | ACEPTADO 13-oct-2026 | SÍ |
| TEC-26 | Pruebas | JUnit 6.0.3 · AssertJ 3.27.7 · Testcontainers 2.0.5 · Toxiproxy | ACEPTADO 13-oct-2026 · ver A-3 | SÍ (sin Toxiproxy) |
| TEC-27 | Reglas en el servidor | JSON Logic en Java | ACEPTADO 13-oct-2026 | NO |
| TEC-28 | Empaquetado | Jib (OCI, amd64 y arm64) · Docker Compose | ACEPTADO 13-oct-2026 | SÍ (solo Compose) |
| TEC-29 | Sistema del nodo | Debian 13 o Ubuntu 24.04 LTS | ACEPTADO 13-oct-2026 | — (fuera del código) |
| TEC-30 | Integración continua | GitHub Actions | ACEPTADO 13-oct-2026 | NO |

## 3 · Lo que falta definir

### 3.1 Piezas aceptadas sin biblioteca concreta

La TEC está aceptada pero no dice qué biblioteca. **La primera sesión que la necesite propone la
biblioteca aquí, como `PROPUESTA`, y para.** No la mete al `pom.xml` sin el visto de Juan.

| TEC | Falta elegir | Estado |
|---|---|---|
| TEC-10 | Forma canónica para la huella: ¿RFC 8785 (JCS) con una biblioteca, o propia? | POR DEFINIR |
| TEC-11 | Validador de JSON Schema en Java, y qué versión del borrador de JSON Schema | POR DEFINIR |
| TEC-12 | Generador de UUID v7: ¿lo trae el JDK 27 o hace falta biblioteca? | POR DEFINIR |
| TEC-27 | Implementación de JSON Logic en Java | POR DEFINIR |
| TEC-26 | Plantilla de datos de prueba y casos dorados | POR DEFINIR |

### 3.2 Piezas aceptadas con biblioteca nombrada

La biblioteca ya está nombrada. La sesión que la necesite **puede meterla** con la última versión
estable, siempre que funcione con Java 27. Deja la versión en el `pom.xml` padre con un comentario
`<!-- TEC-nn -->`, la anota en la tabla del §2 y escribe una línea en el §5.

db-scheduler (TEC-09) · Nimbus JOSE+JWT (TEC-13) · Spring Security (TEC-14, ya en el BOM de Boot) ·
Thymeleaf (TEC-22, TEC-24) · htmx (TEC-24) · AWS SDK v2 y MinIO (TEC-21) · Toxiproxy de
Testcontainers (TEC-26) · Jib (TEC-28). Nginx (TEC-15) ya entró el 20-oct-2026.

Desde TEC-15, para una imagen de contenedor (como la de Nginx) la versión no va al `pom.xml`: va fija en
`despliegue/compose.yaml` con su comentario `TEC-nn`, y la prueba que la usa la lee de ahí. `postgres:16` es anterior
a esta regla: no fija el parche y la prueba lo repite a mano (pendiente en `despliegue/ESPEC-gateway-finca.md` §7).

### 3.3 Decisiones abiertas que bloquean código

| ID | Qué bloquea |
|---|---|
| TX-1, TX-1.C, TX-1.K, TX-1.T | Toda la lógica de TX-1. **TX-1.K bloquea `contrato/`**, que es lo primero que se programa |
| P-S04..P-S10 | Jornada, nombres del contrato, canal por archivo, permiso retirado, tablero, umbrales |
| TX-2, TX-2.O, P-R01..P-R09 | Toda la lógica de TX-2 |
| P-R10 | **Proveedor de nube.** Bloquea la subida real del respaldo; hasta entonces, solo MinIO |
| A-1 | Que Juan pase TEC-01 a `AJUSTADO` |

## 4 · Cómo entra una tecnología nueva

1. **La sesión que la necesita** añade una fila al §3.1 (o una nueva `TEC-31`, `TEC-32`… al final
   del §2) con estado `PROPUESTA <fecha>`, la pieza, la versión y una línea de por qué. **No la usa
   todavía.**
2. **Juan decide** y cambia el estado a `ACEPTADO`, `AJUSTADO` o `RECHAZADO`. Si es una TEC nueva,
   también la añade a la tabla de `IA_00-LEEME-transacciones.md`.
3. **La siguiente sesión** la mete: versión en `<properties>` y `<dependencyManagement>` del
   `pom.xml` padre, con el comentario `<!-- TEC-nn -->`; los módulos la usan sin versión.
4. Se pone `SÍ` en la columna «En el código» y se escribe una línea en el §5.

Lo mismo vale para **subir una versión** de algo que ya está: si cambia la versión mayor, pasa por el
paso 1; si solo cambia la menor o el parche, basta con el paso 4.

## 5 · Registro de cambios (solo se añade)

| Fecha | Quién | Qué |
|---|---|---|
| 13-oct-2026 | Juan | Acepta TEC-01..TEC-30 y P-T01..P-T07 |
| 13-oct-2026 | Claude | Esqueleto Maven con Spring Boot 4.1.1 y Modulith 2.1.1, compilando para Java 21 |
| 14-oct-2026 | Juan | Pasa a Java 27 y fuerza ArchUnit 1.5.1. Cambia los paquetes internos (A-4) |
| 15-oct-2026 | Claude | Este registro. `mise.toml` con Temurin 27.0.0+35. Arranque de la nube en `.claude/` |
| 19-oct-2026 | Claude | `contrato/`: records del Segmento 1 y JUnit + AssertJ de prueba (TEC-26). Sin `module-info`: con Java 27 obligaba a forzar ASM en el compilador, y Juan lo descartó (A-37 de la ESPEC) |
| 19-oct-2026 | Claude | Canal de TX-1 (camino mínimo, D-17): `V2__anotacion_recibida.sql`, módulo `ingesta` del backend con `JdbcClient`, y en el simulador la orden `sincronizar` con `java.net.http` (del JDK). Sin dependencias nuevas |
| 20-oct-2026 | Claude | TEC-15 entra al código: gateway Nginx 1.30.6 (`nginx:1.30.6-alpine`) en `despliegue/compose.yaml` con la plantilla `despliegue/gateway/default.conf.template` (`despliegue/ESPEC-gateway-finca.md`). La prueba `CanalPorGatewayTest` usa `GenericContainer` de Testcontainers (TEC-26, ya en el BOM). Sin dependencias Maven nuevas |
