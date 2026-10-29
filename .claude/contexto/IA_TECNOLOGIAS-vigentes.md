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
| A-4 | §3 · dentro de cada módulo: `dominio/`, `aplicacion/`, `adaptadores/` | `dominio/`, `servicios/{input/{port,dto,adapter}, casosuso/impl, output/{adapter,dto,port}}`, `infraestructura/{input/controlador, output/adapter}`, `transversal/` (ver `codigo/IA_LEEME.md`) | Juan, 14-oct-2026 (commit `fa244b4`); reempaquetado de Juan 22-oct-2026 (OBS-1-7) | Actualizar §3 de `IA_TECNOLOGIAS-y-estructura.md` |
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
| TEC-06 | Base de la finca | PostgreSQL 16 (imagen `postgres:16`; en `compose.yaml`, 16.15 por digest `sha256:1beaa3084eee2676f20a9cd9928f0bf2c5bf674f8e49f81017912183d377c61c`, 29-oct-2026) · controlador 42.7.13 | ACEPTADO 13-oct-2026 | SÍ |
| TEC-07 | Acceso a datos | Spring JDBC (`JdbcClient`) | ACEPTADO 13-oct-2026 | SÍ (`AnotacionesRecibidasAdaptadorJdbc`) |
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
| TEC-28 | Empaquetado | Jib (OCI, amd64 y arm64) · Docker Compose | AJUSTADO 28-oct-2026 · la imagen del backend la construye un Dockerfile multietapa escrito por el equipo, no Jib (`ESPEC-esqueleto-tx1` A-9) | SÍ (solo Compose) |
| TEC-29 | Sistema del nodo | Debian 13 o Ubuntu 24.04 LTS | ACEPTADO 13-oct-2026 | — (fuera del código) |
| TEC-30 | Integración continua | GitHub Actions | ACEPTADO 13-oct-2026 | NO |
| TEC-31 | WAF de la finca | ModSecurity v3 (libmodsecurity 3.0.17) · OWASP CRS 4.30.0 · imagen `owasp/modsecurity-crs:4.30.0-nginx-alpine-202610180210` (trae Nginx 1.30.6, la de TEC-15; amd64 y arm64). Lo eligió el C4 (`IA_TECNOLOGIAS-C4.md`, consulta Q1); motivo y choques en `codigo/despliegue/ESPEC-waf-finca.md` | PROPUESTA 21-oct-2026 · con visto de Juan (Jeronimo, 21-oct: «ya se habló», va a nivel del gateway; K-1 y K-2 de la ESPEC). Falta que Juan la pase a `ACEPTADO` y ajuste `BB-08` | SÍ (`despliegue/compose.yaml`, `despliegue/gateway/`) |
| TEC-32 | Proveedor de identidad | Keycloak 26 autohospedado en el nodo (imagen `quay.io/keycloak/keycloak`; versión exacta y parche por fijar al entrar). El backend lo usa como servidor de recursos OAuth2 con Spring Security (TEC-14). Motivo: Juan, 28-oct-2026, «el IdP es un servicio, no algo que construimos desde cero». Choca con ADR-007 y toca TEC-13 (ver `IA_GUIA-IS2-checklist.md` §8) | PROPUESTA 28-oct-2026 · decidido por Juan; falta ajustar ADR-007 y pasarla a `ACEPTADO` | NO |
| TEC-33 | Vault de secretos del nodo | OpenBao **2.7.2** (imagen `openbao/openbao:2.7.2@sha256:5046911075e137e154ce49ab051502f4ec7cf367d484ae4a4dce665d9b90561f`, Alpine 3.24.3, 26-oct-2026). Imagen propia encima: `despliegue/openbao/Dockerfile`, etapas `servidor` e `init` (`ESPEC-esqueleto-tx1` A-27, A-32). Solo almacenamiento `raft`; sin `mlock` (comprobado). **CVE (29-oct):** la 2.7.1 propuesta primero está afectada por cuatro avisos del 26-oct (tres altos; uno es **ACL bypass en AppRole**, GHSA-736f-82w7-hj22, que usamos); los corrige la 2.7.2. Los avisos anteriores están corregidos en 2.7.x (sus commits son ancestros de la etiqueta) | ACEPTADO 29-oct-2026 · en sesión (Juan), tras la revisión de CVE; falta reflejarlo en `IA_00-LEEME-transacciones.md` | SÍ (Segmento 1a, 29-oct) |
| TEC-34 | Cliente del vault en el backend | Spring Cloud Vault **5.0.3** (`spring-cloud-starter-vault-config`, tren Spring Cloud **2025.1.4**; trae `spring-vault-core` 4.0.4 y `spring-cloud-commons` 5.0.4). **Excepción anotada (A-43):** el tren es para Boot 4.0 y el proyecto usa Boot 4.1.1; el tren 2026.0.0 para Boot 4.1 está en RC1 (22-oct). `NodoCompletoTest` es la prueba de que funciona; se sube a 2026.0.0 cuando sea estable. **CVE (29-oct):** 0 en OSV para las tres piezas | ACEPTADO 29-oct-2026 · en sesión (Juan), tras la revisión de CVE, con la excepción; falta el LEEME | SÍ (Segmento 1a, 29-oct) |
| TEC-35 | Sello `pkcs11` de OpenBao | `openbao-plugin-kms-pkcs11` **v0.2.1** (`openbao/openbao-plugins`, 23-sep-2026), fijado por `sha256` (la firma `.sig` se comprueba en el Segmento 7, con su herramienta y su fila TEC; Juan, 29-oct): amd64 `e88632ee52600513bd0e77242823ef57641f1eaac019d8c66515c8bca81e007d`, arm64 `eeeea22ac0cc25f8ebafd1cb8b00ee27a6b2e166e67f29e13f2fe4436efc0df3` (binarios, de `checksums-kms-pkcs11.txt`). En Alpine pide `gcompat`. Versión 0.x; OpenBao solo lista HSM de red como probados: un modelo de token USB se verifica antes de la primera finca (K-23). **CVE (29-oct):** el único aviso del repositorio es del método AWS, que no usamos. Cambiar de versión = pasar otra vez por esta fila | ACEPTADO 29-oct-2026 · en sesión (Juan); falta el LEEME | SÍ (Segmento 1a, 29-oct) |
| TEC-36 | Paquetes Alpine de las imágenes de OpenBao | Alpine 3.24 (la de TEC-33), **fijados a la menor con `~`** para recibir parches (Alpine solo guarda la última versión de cada paquete; un pin exacto rompe la construcción con cada parche): `softhsm~2.7` (hoy 2.7.0-r0; se instala en todas las imágenes, la misma en la finca por ADR-016, pero solo se usa fuera de la finca, A-29), `opensc~0.26` (0.26.1-r0), `gcompat~1.1` (1.1.0-r4); en la etapa `init`: `postgresql16-client~16` (16.15-r0) y `jq~1.8` (1.8.2-r0). **CVE (29-oct):** 0 en OSV (`Alpine:v3.24`) para las cinco versiones; Trivy vigila el resto en el Segmento 7 | ACEPTADO 29-oct-2026 · en sesión (Juan); falta el LEEME | SÍ (Segmento 1a, 29-oct) |

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

**Antes de aceptar, se buscan sus CVE** (regla de Juan, 29-oct-2026: «in every tech must search for CVE's first»): la versión exacta y sus dependencias directas en OSV (`api.osv.dev`) y en los avisos de seguridad de su repositorio, con fecha de consulta. La fila dice qué se encontró; si la versión está afectada, se propone la que lo corrige.

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
| 21-oct-2026 | Claude | Propone **TEC-31** (WAF: ModSecurity v3 + OWASP CRS 4.30.0, imagen `owasp/modsecurity-crs:4.30.0-nginx-alpine-202610180210`), a pedido de Jeronimo (`despliegue/ESPEC-waf-finca.md`). Probada solo en borrador, fuera del repo. No entra al código hasta que Juan la acepte |
| 21-oct-2026 | Claude, por decisión de Jeronimo | Corrige la línea anterior: con el visto de Juan («ya se habló»), TEC-31 entra al código antes de que Juan la pase a `ACEPTADO`, como excepción anotada a §4 (`ESPEC-waf-finca.md` A-1 y COH-1, ronda 2 P1). Si Juan no la acepta, el segmento se revierte |
| 21-oct-2026 | Claude | TEC-31 entra al código: el servicio `gateway` de `despliegue/compose.yaml` pasa a `owasp/modsecurity-crs:4.30.0-nginx-alpine-202610180210` (la misma Nginx 1.30.6 de TEC-15), con `despliegue/gateway/modsecurity-override.conf.template` nuevo (`despliegue/ESPEC-waf-finca.md`, Segmento 1). `CanalPorGatewayTest` lee la imagen de ahí. Sin dependencias Maven nuevas: `@ParameterizedTest` viene con JUnit (TEC-26) |
| 28-oct-2026 | Claude, por decisión de Juan | Propone **TEC-32**: Keycloak 26 como proveedor de identidad (servicio, no construido). Sin código todavía |
| 28-oct-2026 | Juan | Ajusta **TEC-28**: Dockerfile multietapa en lugar de Jib para la imagen del backend (`ESPEC-esqueleto-tx1` A-9) |
| 29-oct-2026 | Claude, por decisión de Juan | Propone **TEC-33** (OpenBao 2.7.1), **TEC-34** (Spring Cloud Vault; la versión estable no es la de Boot 4.1: decide Juan), **TEC-35** (complemento `pkcs11` v0.2.1) y **TEC-36** (paquetes Alpine fijos). Cierran la parte de propuesta de K-7 de `ESPEC-esqueleto-tx1`; falta el `ACEPTADO` de Juan. Sin código todavía |
| 29-oct-2026 | Juan (en sesión) | Acepta **TEC-33…TEC-36** tras la revisión de CVE. TEC-33 sube de 2.7.1 a **2.7.2** (la 2.7.1 está afectada por GHSA-736f-82w7-hj22, ACL bypass en AppRole, y otros tres avisos del 26-oct). TEC-36 se fija a la menor con `~`. Nueva regla en §4: buscar CVE antes de aceptar. Falta reflejarlo en `IA_00-LEEME-transacciones.md` |
| 29-oct-2026 | Claude, Segmento 1a de `ESPEC-esqueleto-tx1` (con el «sí» de Juan) | Entran al código **TEC-33…TEC-36** (`despliegue/openbao/`, `compose.yaml`, BOM de Spring Cloud 2025.1.4 en `codigo/pom.xml`). Fija por digest (COH-42; 0 avisos en OSV): `postgres:16` → 16.15 (TEC-06) y `eclipse-temurin:27-jdk`/`27-jre` → jdk-27+35 `sha256:473d02d477a8152a05dc42fde10b965754f1863d45e586a97763ef298965c48b` / `sha256:0dd0465f90d398fa3226b56a3973f5d3c47ac323934dceebdab45ac8910bee6e` (imágenes del Dockerfile del backend, TEC-28) |
