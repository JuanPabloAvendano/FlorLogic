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
| TEC-02 | Armazón | Spring Boot **4.1.2** · Spring Framework 7.0.10 (desde el 31-oct-2026; antes 4.1.1 · 7.0.9: Trivy halló 4 CRITICAL de Tomcat 11.0.24 y 5 HIGH de Jackson 3.1.5, corregidos en 4.1.2 con Tomcat 11.0.27 y Jackson 3.1.7, 0 avisos en OSV) | ACEPTADO 13-oct-2026 · parche subido el 31-oct (§4: basta el paso 4) | SÍ |
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
| TEC-14 | Seguridad web | Spring Security 7 (**7.1.2**, de Spring Boot 4.1.2: `spring-boot-starter-security-oauth2-resource-server`, con Nimbus JOSE 10.9.1; OSV sin avisos el 31-oct-2026; desde el 1-nov-2026 también `spring-boot-starter-security-oauth2-client` para el inicio de sesión de la web y `spring-security-test` en pruebas, OSV sin avisos) | ACEPTADO 13-oct-2026 | SÍ (`backend-finca/…/infraestructura/SeguridadDelNodo.java` desde el 31-oct; `WebDelNodo.java` desde el 1-nov) |
| TEC-15 | Gateway | Nginx 1.30.6 (imagen `nginx:1.30.6-alpine`, estable del 14-oct-2026; amd64 y arm64) | ACEPTADO 13-oct-2026 | SÍ (`despliegue/gateway/`, `compose.yaml`) |
| TEC-16 | TLS local | Autoridad certificadora de la finca: raíz fuera de línea (`despliegue/pki/Ceremonia.java`, solo JDK y `keytool`) + intermedia en el motor PKI de OpenBao 2.7.2 (TEC-33) | ACEPTADO 13-oct-2026 | **SÍ, 1-nov-2026** (ESPEC-esqueleto A-44…A-60) |
| TEC-17 | Observabilidad local | Actuator · Micrometer 1.17.2 (`micrometer-registry-prometheus`, versión de Boot; trae `io.prometheus:prometheus-metrics-*` 1.7.0; 0 avisos en OSV el 31-oct-2026) · logs JSON ECS (`logging.structured`, propio de Boot) | ACEPTADO 13-oct-2026 | SÍ (31-oct-2026: Actuator con `health,info,prometheus`, logs JSON ECS en `application.yaml`) |
| TEC-18 | Telemetría | Servicio propio con esquema cerrado | ACEPTADO 13-oct-2026 | NO |
| TEC-19 | Volcado | `pg_dump` formato custom | ACEPTADO 13-oct-2026 | NO |
| TEC-20 | Deduplicar y cifrar | restic | ACEPTADO 13-oct-2026 | NO |
| TEC-21 | Almacén de objetos | S3 con bloqueo de retención · AWS SDK v2 · MinIO en pruebas | ACEPTADO 13-oct-2026 | NO |
| TEC-22 | Servicios en línea | Spring Boot 4.1.1 · Thymeleaf | ACEPTADO 13-oct-2026 | SÍ (sin Thymeleaf) |
| TEC-23 | Finca → nube | TLS mutuo | ACEPTADO 13-oct-2026 | NO |
| TEC-24 | Web de TX-1 y TX-2 | Thymeleaf **3.1.5** (de Spring Boot 4.1.2: `spring-boot-starter-thymeleaf`) con htmx **2.0.11** servido desde el nodo (`backend-finca/src/main/resources/static/web/estatico/htmx-2.0.11.min.js`, idéntico al de `htmx.org-2.0.11.tgz` de npm, sha512 `Thx/WtpeOQqSrqBCw/A1cwGJGg4UrVa3+sW0GmrM3p4gJgO89ecH4qtbnyzDDWFvBTqjnIMCgELTNt636dtamA==`; sin CDN: el nodo trabaja sin internet). OSV sin avisos para los dos el 1-nov-2026 | ACEPTADO 13-oct-2026 | SÍ (`backend-finca`: web del gerente y del administrador, `templates/web/`, desde el 1-nov-2026) |
| TEC-25 | Simulador | picocli 4.7.7 · SQLite (`sqlite-jdbc` 3.50.3.0) | ACEPTADO 13-oct-2026 | SÍ |
| TEC-26 | Pruebas | JUnit 6.0.3 · AssertJ 3.27.7 · Testcontainers 2.0.5 · Toxiproxy | ACEPTADO 13-oct-2026 · ver A-3 | SÍ (sin Toxiproxy) |
| TEC-27 | Reglas en el servidor | JSON Logic en Java | ACEPTADO 13-oct-2026 | NO |
| TEC-28 | Empaquetado | Jib (OCI, amd64 y arm64) · Docker Compose | AJUSTADO 28-oct-2026 · la imagen del backend la construye un Dockerfile multietapa escrito por el equipo, no Jib (`ESPEC-esqueleto-tx1` A-9) | SÍ (Compose y `backend-finca/Dockerfile`, 29-oct-2026) |
| TEC-29 | Sistema del nodo | Debian 13 o Ubuntu 24.04 LTS | ACEPTADO 13-oct-2026 | — (fuera del código) |
| TEC-30 | Integración continua | GitHub Actions. Acciones fijadas por SHA (31-oct-2026): `actions/checkout` v7.0.1, `actions/setup-java` v6.1.0, `actions/upload-artifact` v7.0.2, `docker/setup-buildx-action` v4.4.1, `docker/build-push-action` v7.4.0, `aquasecurity/trivy-action` v0.36.0 (commit `ed142fd…`, posterior al compromiso de etiquetas de marzo de 2026) | ACEPTADO 13-oct-2026 | SÍ (`.github/workflows/ci.yml`, 31-oct-2026, sin correr todavía en GitHub; `actionlint` 1.7.12 sin errores) |
| TEC-31 | WAF de la finca | ModSecurity v3 (libmodsecurity 3.0.17) · OWASP CRS 4.30.0 · imagen `owasp/modsecurity-crs:4.30.0-nginx-alpine-202610180210` (trae Nginx 1.30.6, la de TEC-15; amd64 y arm64). Lo eligió el C4 (`IA_TECNOLOGIAS-C4.md`, consulta Q1); motivo y choques en `codigo/despliegue/ESPEC-waf-finca.md` | PROPUESTA 21-oct-2026 · con visto de Juan (Jeronimo, 21-oct: «ya se habló», va a nivel del gateway; K-1 y K-2 de la ESPEC). Falta que Juan la pase a `ACEPTADO` y ajuste `BB-08` | SÍ (`despliegue/compose.yaml`, `despliegue/gateway/`) |
| TEC-32 | Proveedor de identidad | Keycloak **26.8.1** autohospedado en el nodo: imagen `quay.io/keycloak/keycloak:26.8.1-2@sha256:3f3c5f8e03c614774b4bd9173b88391fefa4153bfc010d8012d59ad825377332` (reconstrucción del 31-oct-2026), base `keycloak` en el PostgreSQL de TEC-06, realm en `codigo/despliegue/keycloak/realm-florlogic.json`. El backend lo usa como servidor de recursos OAuth2 con Spring Security (TEC-14). Motivo: Juan, 28-oct-2026, «el IdP es un servicio, no algo que construimos desde cero». Choca con ADR-007 y toca TEC-13 (ver `IA_GUIA-IS2-checklist.md` §8). **CVE, 31-oct-2026:** OSV sin avisos para `org.keycloak:keycloak-services` y `keycloak-core` 26.8.1; Trivy (0.76.1) sobre la imagen: 0 CRITICAL y 4 HIGH, ninguno con arreglo en una imagen publicada: CVE-2026-119373 y CVE-2026-119586 (`sqlite-libs` de RHEL 9.8), CVE-2026-91403 (el OpenJDK 21 de la imagen) y CVE-2026-102554 (Guava 33.6.0, arreglado en 33.7.2: falta que Keycloak lo suba). Ninguno es alcanzable desde fuera del nodo sin pasar por el gateway; revisar en la siguiente reconstrucción | PROPUESTA 28-oct-2026 · decidido por Juan; **usada desde el 31-oct-2026** en la construcción completa (CT-2); falta ajustar ADR-007 y pasarla a `ACEPTADO` | SÍ (`despliegue/compose.yaml`, `despliegue/keycloak/`, `despliegue/gateway/`) |
| TEC-33 | Vault de secretos del nodo | OpenBao **2.7.2** (imagen `openbao/openbao:2.7.2@sha256:5046911075e137e154ce49ab051502f4ec7cf367d484ae4a4dce665d9b90561f`, Alpine 3.24.3, 26-oct-2026). Imagen propia encima: `despliegue/openbao/Dockerfile`, etapas `servidor` e `init` (`ESPEC-esqueleto-tx1` A-27, A-32). Solo almacenamiento `raft`; sin `mlock` (comprobado). **CVE (29-oct):** la 2.7.1 propuesta primero está afectada por cuatro avisos del 26-oct (tres altos; uno es **ACL bypass en AppRole**, GHSA-736f-82w7-hj22, que usamos); los corrige la 2.7.2. Los avisos anteriores están corregidos en 2.7.x (sus commits son ancestros de la etiqueta) | ACEPTADO 29-oct-2026 · en sesión (Juan), tras la revisión de CVE; falta reflejarlo en `IA_00-LEEME-transacciones.md` | SÍ (Segmento 1a, 29-oct) |
| TEC-34 | Cliente del vault en el backend | Spring Cloud Vault **5.0.3** (`spring-cloud-starter-vault-config`, tren Spring Cloud **2025.1.4**; trae `spring-vault-core` 4.0.4 y `spring-cloud-commons` 5.0.4). **Excepción anotada (A-43):** el tren es para Boot 4.0 y el proyecto usa Boot 4.1.2 (desde el 2-nov; antes 4.1.1); el tren 2026.0.0 para Boot 4.1 está en RC1 (22-oct). `NodoCompletoTest` es la prueba de que funciona; se sube a 2026.0.0 cuando sea estable. **CVE (29-oct):** 0 en OSV para las tres piezas | ACEPTADO 29-oct-2026 · en sesión (Juan), tras la revisión de CVE, con la excepción; falta el LEEME | SÍ (Segmento 1a, 29-oct) |
| TEC-35 | Sello `pkcs11` de OpenBao | `openbao-plugin-kms-pkcs11` **v0.2.1** (`openbao/openbao-plugins`, 23-sep-2026), fijado por `sha256` (la firma `.sig` se comprueba en el Segmento 7, con su herramienta y su fila TEC; Juan, 29-oct): amd64 `e88632ee52600513bd0e77242823ef57641f1eaac019d8c66515c8bca81e007d`, arm64 `eeeea22ac0cc25f8ebafd1cb8b00ee27a6b2e166e67f29e13f2fe4436efc0df3` (binarios, de `checksums-kms-pkcs11.txt`). En Alpine pide `gcompat`. Versión 0.x; OpenBao solo lista HSM de red como probados: un modelo de token USB se verifica antes de la primera finca (K-23). **CVE (29-oct):** el único aviso del repositorio es del método AWS, que no usamos. Cambiar de versión = pasar otra vez por esta fila | ACEPTADO 29-oct-2026 · en sesión (Juan); falta el LEEME | SÍ (Segmento 1a, 29-oct) |
| TEC-36 | Paquetes Alpine de las imágenes de OpenBao | Alpine 3.24 (la de TEC-33), **fijados a la menor con `~`** para recibir parches (Alpine solo guarda la última versión de cada paquete; un pin exacto rompe la construcción con cada parche): `softhsm~2.7` (hoy 2.7.0-r0; se instala en todas las imágenes, la misma en la finca por ADR-016, pero solo se usa fuera de la finca, A-29), `opensc~0.26` (0.26.1-r0), `gcompat~1.1` (1.1.0-r4); en la etapa `init`: `postgresql16-client~16` (16.15-r0) y `jq~1.8` (1.8.2-r0). **CVE (29-oct):** 0 en OSV (`Alpine:v3.24`) para las cinco versiones; Trivy vigila el resto en el Segmento 7 | ACEPTADO 29-oct-2026 · en sesión (Juan); falta el LEEME | SÍ (Segmento 1a, 29-oct) |
| TEC-37 | Oráculo de prueba de la forma canónica (RFC 8785) | **Solo `scope test`, en `contrato`:** `com.apicatalog:titanium-jcs` **2.0.0** (Apache-2.0, 27-sep-2025; trae `tree-io-api` 0.8.0) + `com.apicatalog:tree-io-jakarta` **0.8.0** + `org.eclipse.parsson:parsson` **1.1.9** (Jakarta JSON-P). Por qué: la prueba de `Jcs`/`HuellaCanonica` (en `contrato`, paquete `interno`) compara nuestra salida con una implementación independiente de RFC 8785 (A-86). No entra al nodo: Titanium no tiene adaptador de Jackson 3 (`tree-io-jackson2` violaría A-5). **CVE, consulta del 30-oct-2026 en OSV:** `titanium-jcs`, `tree-io-api`, `tree-io-jakarta` sin avisos; `parsson` tuvo GHSA-2rwm-xv5j-777p / CVE-2023-7272 (desbordamiento de pila con anidamiento profundo), corregido en 1.1.3: la 1.1.9 no está afectada. Descartada `io.github.erdtman:java-json-canonicalization` 1.1 (2018, borrador previo al RFC) | **PROPUESTA 30-oct-2026** | NO |
| TEC-38 | Cobertura de pruebas | JaCoCo **0.8.16** (`org.jacoco:jacoco-maven-plugin`, en el `pom.xml` padre: `prepare-agent` y `report` en `verify`). La 0.8.14 no lee clases de Java 27 (versión 71); la 0.8.16 sí (comprobado: informes en `target/site/jacoco/`). **CVE, OSV 31-oct-2026:** 0 para `jacoco-maven-plugin`, `org.jacoco.agent`, `org.jacoco.core` 0.8.16 y `asm` 9.9 | **PROPUESTA 31-oct-2026** · usada en la construcción completa (`IA_CONSTRUCCION-TX1.md`) | SÍ (`codigo/pom.xml`) |
| TEC-39 | Análisis estático | SonarQube Community Build **26.10.0.133078** (imagen `sonarqube:26.10.0.133078-community@sha256:f95aaa9016dd4e92c7d70691167a6d96dbb5d8acffd61eba1cb4335d364a7840`, perfil `calidad` de `compose.yaml`, base H2 solo para desarrollo) · `org.sonarsource.scanner.maven:sonar-maven-plugin` **5.8.0.7211**. En CI, SonarQube o SonarQube Cloud si existe el secreto `SONAR_TOKEN`. **CVE, 31-oct-2026:** el complemento, 0 en OSV; la imagen, Trivy: 3 CRITICAL (`netty-handler` 4.1.135, CVE-2026-75595, corregida en 4.1.137) y 36 HIGH. No entra al nodo de finca: solo desarrollo | **PROPUESTA 31-oct-2026** · usada en la construcción completa | SÍ (`codigo/pom.xml`, `despliegue/compose.yaml`) |
| TEC-40 | Escáner de vulnerabilidades | Trivy **0.76.1** (imagen `aquasec/trivy:0.76.1@sha256:790fd66d1697395d8693d1acaa1ea702406a20f109b6fc7bac8db623eca6c06c`; en CI, la misma versión por `aquasecurity/trivy-action`). Script `codigo/scripts/IA_escanear-trivy.sh`. **CVE, avisos del repositorio 31-oct-2026:** la 0.69.4 fue una entrega maliciosa (GHSA-69fq-xp46-6x23, marzo de 2026) y los demás avisos se corrigen hasta la 0.72.0: la 0.76.1 no está afectada; nunca una etiqueta móvil | **PROPUESTA 31-oct-2026** · usada en la construcción completa | SÍ (`scripts/IA_escanear-trivy.sh`, `.github/workflows/ci.yml`) |
| TEC-41 | Observabilidad del nodo (perfil `observabilidad`) | Prometheus **3.15.1** (`prom/prometheus:v3.15.1@sha256:fa3302279710be2f21a8301ec17a8b4f2301fd829bd113839b3b6b02b6f191a2`) · Loki **3.7.9** (`grafana/loki:3.7.9@sha256:0db216b630ae1f348a1584c67f3cead8b4c1fc839169a39501db664744158e28`) · Grafana Alloy **1.21.3** (`grafana/alloy:v1.21.3@sha256:8ab871206bd2d8f89277f53abffee5f452a293980788d52f85fa183a87a505a4`) · Grafana **13.2.5** (`grafana/grafana:13.2.5@sha256:9f7aa1e1e3246aade5792d35a738049f7f32f89cd51d1956cf426a4e86268a84`). Lo pide TEC-17 y el C4 N3 («Prometheus/Grafana (propuesta)»). Configuración en `despliegue/observabilidad/`. **CVE, 31-oct-2026:** avisos de Prometheus corregidos hasta la 3.11.3 (no afectan a la 3.15.1); Loki y Alloy sin avisos en su repositorio; Trivy sobre las imágenes: Prometheus, Loki y Alloy 0 HIGH/CRITICAL, Grafana 0 CRITICAL y 35 HIGH (bibliotecas de la imagen) | **PROPUESTA 31-oct-2026** · usada en la construcción completa | SÍ (`despliegue/compose.yaml`, `despliegue/observabilidad/`) |
| TEC-42 | Pruebas del JS de la app del celular (PWA, F8) | **Node.js LTS** (22 o más nuevo) con su corredor propio `node:test` y `node:assert`, **sin npm ni paquetes**: `node --test "backend-finca/src/test/js/*.test.mjs"`. La app en sí no trae dependencias (HTML/JS sin framework, CT-4; PKCE con WebCrypto). Probado con Node 23.11.1, que ya está fuera de soporte: usar una LTS. **CVE: sin revisar todavía** (OSV de la versión de Node elegida, antes de aceptar). `./mvnw verify` no lo corre: entrarlo al build (p. ej. `exec-maven-plugin`) sería otra fila | **PROPUESTA 3-nov-2026** (Claude, a pedido de Jeronimo) | SÍ, fuera de Maven (`backend-finca/src/test/js/`, `scripts/IA_probar-app.mjs`) |

## 3 · Lo que falta definir

### 3.1 Piezas aceptadas sin biblioteca concreta

La TEC está aceptada pero no dice qué biblioteca. **La primera sesión que la necesite propone la
biblioteca aquí, como `PROPUESTA`, y para.** No la mete al `pom.xml` sin el visto de Juan.

| TEC | Falta elegir | Estado |
|---|---|---|
| TEC-10 | Forma canónica para la huella: ¿RFC 8785 (JCS) con una biblioteca, o propia? | **PROPUESTA 30-oct-2026** (ESPEC-dominio-ingesta A-86, Juan): **propia** sobre Jackson 3 en `contrato`, verificada contra TEC-37 solo en pruebas. Sin dependencia de producción nueva |
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
| 29-oct-2026 | Claude, Segmento 1b de `ESPEC-esqueleto-tx1` (con el «sí» de Juan) | **TEC-28** queda en el código entero: Compose levanta el nodo completo y la imagen del backend sale de su Dockerfile multietapa. Sin versiones nuevas |
| 30-oct-2026 | Claude, ronda 4 de `ESPEC-dominio-ingesta` (decisión A-86 de Juan) | Propone **TEC-37** (Titanium JCS 2.0.0 + tree-io-jakarta 0.8.0 + Parsson 1.1.9, solo pruebas) y la resolución de **TEC-10** (forma canónica propia). Revisión de CVE en OSV el mismo día. Falta el `ACEPTADO` de Juan; no se usan todavía |
| 31-oct-2026 | Claude (subagente de plataforma, construcción completa de TX-1) | Propone y usa **TEC-38** (JaCoCo 0.8.16), **TEC-39** (SonarQube Community Build 26.10 + `sonar-maven-plugin` 5.8.0.7211), **TEC-40** (Trivy 0.76.1) y **TEC-41** (Prometheus 3.15.1, Loki 3.7.9, Alloy 1.21.3, Grafana 13.2.5), con revisión de CVE (OSV, avisos de los repositorios y Trivy). TEC-17 entra entera (`micrometer-registry-prometheus` de Boot, logs JSON ECS) y TEC-30 (`.github/workflows/ci.yml`). **TEC-02 sube de parche, 4.1.1 → 4.1.2**, por los CRITICAL de Tomcat que halló Trivy. Falta el `ACEPTADO` de Juan para TEC-38…41 |
| 31-oct-2026 | Claude, por el «go» de Juan (identidad, CT-2) | **TEC-32 entra al código** con versión fija: Keycloak 26.8.1 (`26.8.1-2` por digest), CVE revisados (OSV y Trivy, ver la fila). **TEC-14 entra al código**: `spring-boot-starter-security-oauth2-resource-server` de Boot 4.1.2 (Spring Security 7.1.2, Nimbus JOSE 10.9.1; OSV sin avisos). Sin dependencias fuera de las versiones de Spring Boot. TEC-32 sigue `PROPUESTA` hasta que Juan la acepte |
| 1-nov-2026 | Claude, construcción completa de TX-1 (F4, web del gerente) | **TEC-24 entra al código**: Thymeleaf 3.1.5 (de Boot 4.1.2) y htmx 2.0.11 como archivo estático, verificado contra el sha512 del paquete de npm. **TEC-14 suma** `spring-boot-starter-security-oauth2-client` y `spring-security-test` (Spring Security 7.1.2, de Boot). OSV sin avisos para los cuatro. Ninguna versión fuera del BOM de Spring Boot |
| 1-nov-2026 | Claude, a pedido de Juan (HTTPS en todo el nodo, A-44) | **TEC-16 entra al código**: raíz de la finca fuera de línea con `keytool` del JDK (sin dependencia nueva) e intermedia en el motor PKI de OpenBao (TEC-33, sin versión nueva). Sin tecnología nueva ni cambios de versión |
| 2-nov-2026 | Claude, a pedido de Juan (paso P0; traído de `florlogic-tx1-completa`, CT-9) | **TEC-02 sube de parche**: Spring Boot 4.1.1 → **4.1.2** (Spring Framework 7.0.10, Tomcat 11.0.27, Jackson 3.1.7; JUnit sigue en 6.0.3) por los 4 CRITICAL de Tomcat 11.0.24 y los 5 HIGH de Jackson 3.1.5 que halló Trivy en la copia; OSV sin avisos para la 4.1.2. Solo `codigo/pom.xml`; sin dependencias nuevas. TEC-22 y A-3 siguen la misma versión |
| 3-nov-2026 | Claude, a pedido de Juan («trae todo a este proyecto») | Se trae entera la construcción completa de `~/Work/florlogic-tx1-completa` a este repo: entran al código TEC-38 (JaCoCo), TEC-39 (SonarQube, perfil `calidad`), TEC-40 (Trivy, `scripts/IA_escanear-trivy.sh` y `.github/workflows/ci.yml`), TEC-41 (observabilidad, perfil `observabilidad`), TEC-32 (Keycloak) y TEC-24 (Thymeleaf). **Sus estados no cambian:** las que dicen `PROPUESTA` siguen esperando el `ACEPTADO` de Juan. Respaldo del estado anterior en `~/Work/respaldos/florlogic-codigo-antes-de-traer-tx1-completa-20261103-1125.tar.gz` |
| 3-nov-2026 | Claude, a pedido de Juan (arreglos tras la verificación de los agentes) | Solo texto, sin cambio de versión: TEC-34 dice Boot 4.1.2 (no 4.1.1); TEC-37 nombra las clases que existen (`Jcs`, `HuellaCanonica`; `CanonizadorJson` no existe). `pruebas-extremo-a-extremo` deja `spring-boot-testcontainers` y `testcontainers-postgresql`, que no usaba (TEC-26 sigue por `testcontainers-junit-jupiter`). El wrapper de Maven fija `distributionSha256Sum` de Maven 3.9.16 |
| 3-nov-2026 | Claude, a pedido de Jeronimo (la PWA de la demo servida por el nodo, F8) | **TEC-42 PROPUESTA**: Node LTS con `node:test`, sin npm, para las pruebas del JS de `static/app/` (fuera de `./mvnw verify`). La app no suma dependencias: es el `dist/` de `app-captura` (JS sin framework, CT-4). Para desarrollo, `scripts/IA_probar-app.sh` usa nombres `*.sslip.io` (DNS público que resuelve la IP escrita en el nombre): no es dependencia del código, solo de la prueba (ID-8). |
