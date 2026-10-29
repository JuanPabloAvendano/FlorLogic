REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 28-oct-2026, a pedido de Juan («la guía de evaluación es el foco principal; checklist de todo lo que se pide, emparejado con TX-01»)
Estado de revisión: SIN REVISAR
Manda por encima: `Guia_Oficial_Evaluacion_Ingeniería Software II.docx` (la guía del curso) · **`README.md` de `documentacion-desarrollo` y sus imágenes** (humano, manda desde el 28-oct-2026) · `ADR.xlsx` · la palabra de Juan

# Guía de evaluación de IS II · checklist emparejado con FlorLogic y TX-1

## 0 · Para qué sirve y cómo se usa

La guía pide **evidencia y sustentación** de 9 áreas, más el detalle de OWASP Top 10, el Manifiesto Reactivo,
Twelve-Factor y Well-Architected. Esta lista empareja cada ítem con **dónde cae en FlorLogic / TX-1**, **qué evidencia
hay hoy** (comprobada el 28-oct-2026 en `codigo-desarrollo` y `origin/documentacion-desarrollo`) y **qué fuente de la
biblioteca** se estudia para entenderlo (`mcp__biblioteca__buscar(…, libro="<slug>")`).

**La meta no es que la IA lo construya.** Es que cada integrante pueda **entender, modificar, construir, explicar,
apagar y reconstruir desde cero** cada componente de TX-1 sin ayuda de un agente. La IA explica, señala la fuente y
revisa; los integrantes construyen, fallan y preguntan (modo docente). Lo mínimo que se espera de cada ítem:
emparejarlo con FlorLogic, aunque no se domine a la perfección.

**Lo que manda (Juan, 28-oct-2026): todo lo que está en GitHub es humano y manda.** La fuente de arquitectura y
modelado es el `README.md` de `documentacion-desarrollo` (commit `f8e5bb4`): §1 trade-off de atributos, §3 los 65
ESC, §4 la tabla de ADR-001…037, §5 C4 nivel 2 y despliegue, §6 C4 nivel 3, §7 paquetes, §8 secuencia. Sus imágenes
están copiadas en `referencias/arquitectura-readme/`. Los documentos `IA_*` (incluido
`FlorLogic-alternativa-de-solucion-y-ADR.md`) quedan como antecedente: si chocan con el README, manda el README.

**Estados:** `HAY` evidencia · `PARCIAL` · `FALTA` · `DEFENDER`: FlorLogic decidió **no adoptarlo** (ADR) y se sustenta con
ese ADR, sin demostración (Juan, 28-oct-2026): qué se eligió en su lugar, el trade-off y el disparador para
reconsiderarlo · `VERIFICAR`: no se comprobó todavía.

## 1 · Las 9 áreas

### 1.1 Gobierno arquitectónico y ADR

| Ítem | En FlorLogic | Evidencia hoy | Estado | Estudiar |
|---|---|---|---|---|
| ADR documentados | ADR-001…037 con estado (aceptada, propuesta, depreciada, rechazada) | README §4 · `ADR.xlsx` (fuera de git) | HAY | `adr-nygard-2011`, `adr-github-io` |
| Trade-offs | Ranking de 13 atributos por tres actores; confiabilidad primero | README §1 (y ALT-1…4 en el documento IA, antecedente) | HAY | *Software Architecture in Practice* (ATAM) |
| Trazabilidad | CN/ESC/RF → ADR → TEC → código (comentarios `TEC-nn`, ESPEC con A-n) | ADR «Deriva de», `IA_TECNOLOGIAS-vigentes.md` | PARCIAL: falta una matriz ESC → ADR → clase/prueba que se pueda mostrar | `adr-github-io` |
| Riesgos | ESC «parciales o en choque», CN `EN DUDA` | dispersos | VERIFICAR: no se encontró un registro de riesgos único | *Software Architecture in Practice* |

### 1.2 Arquitectura y modelado

| Ítem | En FlorLogic | Evidencia hoy | Estado | Estudiar |
|---|---|---|---|---|
| C4 nivel 1 | Contexto: finca, celular, nube | **no está en el README** (solo en `FlorLogic-C4.archimate`) | FALTA en lo que manda | `c4model` |
| C4 nivel 2 | Contenedores y despliegue en 4 nodos | README §5 (`01 - Nivel 2`, `06 - Despliegue`) | HAY | `c4model` |
| Paquetes | El hexágono de `ingesta` | README §7 (`diagrama de paquetes - 01`), coincide con el código; `ModulosTest` | HAY | `uml-2.5.1`, *Clean Architecture* |
| Componentes | C4 nivel 3 de backend, app, web y servicios en línea | README §6 (`02`…`05`) | HAY | `c4model`, `spring-modulith-2.1` |
| Secuencia | Recibir una partición: camino feliz (20 pasos) y fallos | README §8 (`diagrama de secuencia - 01` y `02`), es lo que corre hoy | HAY | `uml-2.5.1` |

### 1.3 Línea base técnica

| Ítem | En FlorLogic | Evidencia hoy | Estado | Estudiar |
|---|---|---|---|---|
| WAF | TEC-31: ModSecurity v3 + OWASP CRS 4.30.0 en el gateway | `compose.yaml`, `gateway/modsecurity-override.conf.template`, `CanalPorGatewayTest`, `ESPEC-waf-finca.md` | HAY · TEC-31 sigue `PROPUESTA` | `modsecurity-v3`, `owasp-crs-4` |
| Identity Provider | BB-05 / ADR-007: identidad **propia** con credencial offline (JWS Ed25519, TEC-13); Keycloak solo «si crece» | módulo `identidad` vacío | FALTA · **se adopta un IdP como servicio, no se construye** (Juan, 28-oct-2026): Keycloak 26 autohospedado en el nodo (TEC-32 `PROPUESTA`); `identidad` solo lo integra (Spring Security como servidor de recursos). Ajusta ADR-007 | `openid-connect-core`, `rfc-6749-oauth2`, `rfc-9700-oauth-bcp`, `keycloak-26-*`, `nimbus-jose-jwt` |
| API Gateway | BB-06 / TEC-15: Nginx con TLS y límite de tasa | `despliegue/gateway/`, `ESPEC-gateway-finca.md` | HAY · TLS de la CA (TEC-16) falta | `nginx`, `microservices-io-patrones` |
| Message Broker | BB-09 / ADR-009: **cola sobre PostgreSQL** (db-scheduler) y eventos de Spring Modulith, no broker | nada en código | DEFENDER con ADR-009 y BB-09 · la cola en PostgreSQL sigue FALTA | `rabbitmq-4`, `db-scheduler`, `spring-modulith-2.1`, `microservices-io-patrones` |
| Service Mesh | ADR-001/017: monolito modular, sin malla | nada | DEFENDER con ADR-001 y ADR-017 | `istio-conceptos`, `linkerd-que-es-service-mesh` |
| Data Mesh | BB-17: **NO SE ADOPTA** (un dominio, dos personas); BI sobre modelos de lectura (ADR-010) | sustentado en el documento de ADR | DEFENDER con BB-17 y ADR-010 · ya hay sustentación escrita | `data-mesh-dehghani` |
| Observabilidad | C4 N3: «Registros estructurados + Prometheus/Grafana (propuesta)»; TEC-17: Actuator, Micrometer, logs JSON | Actuator | PARCIAL | `micrometer-1.17`, `prometheus-docs`, `loki-get-started`, `opentelemetry-conceptos` |
| CI/CD | TEC-30: GitHub Actions (aceptado) | no hay `.github/workflows` | FALTA | `github-actions`, `fowler-ci` |

### 1.4 Calidad de código

| Ítem | En FlorLogic | Evidencia hoy | Estado | Estudiar |
|---|---|---|---|---|
| Clean Code | Nombres autoexplicativos (D-09), dominio en español | código de `ingesta`, `contrato` | PARCIAL: falta la sustentación con ejemplos del propio código | *Clean Code* / *Código limpio* |
| Clean Architecture | Hexágono por módulo; `dominio` sin Spring | `ModulosTest`, paquetes `servicios/{input,output}/port` | HAY | *Clean Architecture* |
| SOLID | Puertos (DIP), un caso de uso por clase (SRP) | código de `ingesta` | PARCIAL: falta señalar un ejemplo por principio | `solid-martin-*`, `solid-liskov-wing-1994` |
| KISS | Camino mínimo (D-17), cola en PG en vez de broker | ADR-009, ADR-017 | PARCIAL | `kiss-wikipedia`, `fowler-beck-design-rules` |
| YAGNI | ADR-017 «lo que deliberadamente no se construye»; OBS-1-5 (código anticipado) | ADR-017 | HAY como decisión; ejemplo de código pendiente | `fowler-yagni` |
| DRY | Un solo versionado (ADR-029); misma especificación de reglas cliente y servidor (ADR-006) | ADR | PARCIAL | *The Pragmatic Programmer* |

### 1.5 Seguridad

| Ítem | En FlorLogic | Evidencia hoy | Estado | Estudiar |
|---|---|---|---|---|
| OWASP Top 10 | ver §2 | CRS, `JdbcClient` | PARCIAL | `owasp-top10-2021`, `owasp-top10-2025`, `owasp-cheatsheets` |
| Threat Modeling | STRIDE sobre el diagrama de contenedores de TX-1 | no hay | FALTA | `owasp-threat-modeling`, `threat-modeling-manifesto`, `owasp-cheatsheets` |
| Zero Trust | El backend verifica la credencial aunque venga del gateway (no confía en la red de la finca) | decisión en `IA_PRIORIDAD-componentes-TX1.md` §2 | FALTA: documento y prueba | `nist-sp-800-207` |

### 1.6 DevSecOps

| Ítem | En FlorLogic | Evidencia hoy | Estado | Estudiar |
|---|---|---|---|---|
| Pruebas | JUnit 6, AssertJ, Testcontainers, ArchUnit | 20 clases `*Test.java`; `./mvnw verify` | HAY · sin Docker no prueban PostgreSQL | `junit-6.0`, `testcontainers-java`, `fowler-test-pyramid` |
| Cobertura | — | sin JaCoCo en el `pom.xml` | FALTA · necesita fila TEC | `jacoco` |
| SonarQube | — | nada | FALTA · necesita fila TEC (Community Build en contenedor o SonarQube Cloud) | `sonarqube-community` |
| Trivy | escaneo de imágenes y dependencias | nada | FALTA · necesita fila TEC | `trivy-docs`, `owasp-dependency-check-maven` |

### 1.7 Cloud native

| Ítem | En FlorLogic | Evidencia hoy | Estado | Estudiar |
|---|---|---|---|---|
| Reactive Manifesto | ver §3 | nada escrito | FALTA | `reactive-manifesto` |
| 12 Factor App | ver §4 | nada escrito | FALTA | `twelve-factor` |
| CAF | Local-first: la «adopción de nube» es solo TX-2 (respaldo) y los servicios en línea | nada escrito | FALTA | `aws-caf`, `azure-caf` |
| Well-Architected | ver §5 | nada escrito | FALTA | `aws-well-architected`, `azure-well-architected` |

### 1.8 IA responsable

| Ítem | En FlorLogic | Evidencia hoy | Estado | Estudiar |
|---|---|---|---|---|
| Uso responsable | Prefijo `IA_`, cabecera de cuatro líneas, «NO ES FUENTE», ninguna llave en el repo | todo `.claude/` y `IA_*` | PARCIAL: falta una política escrita de una página | `nist-ai-rmf-1.0`, `nist-ai-600-1` |
| Validación humana | Estados `SIN REVISAR` / `REVISADO POR JUAN`, aceptación por fila, etapa 6 «explicación» de la skill, commits solo de Juan (PR-05) | `IA_00-LEEME-transacciones.md`, `IA_LEER-PRIMERO.md` | HAY | `nist-ai-rmf-1.0` |

### 1.9 Sustentación

| Ítem | En FlorLogic | Evidencia hoy | Estado | Estudiar |
|---|---|---|---|---|
| Defensa técnica | Cada integrante explica y reconstruye cada componente (§6) | matriz §6 vacía | FALTA | — |
| Escenarios futuros | «Alternativa a escala» de cada BB; disparadores de ADR-008, ADR-009, BB-17 | documento de ADR §4 | PARCIAL | *Software Architecture in Practice* |

## 2 · OWASP Top 10 (edición 2021, la que nombra la guía)

La edición vigente es la 2025 (`owasp-top10-2025`), que reordena y renombra; se sustenta con la de la guía y se
menciona la diferencia.

| Riesgo | Qué revisa la guía | En TX-1 | Estado |
|---|---|---|---|
| A01 Broken Access Control | roles y autorización en el backend | `identidad` (ESC-22 permiso retirado) | FALTA |
| A02 Cryptographic Failures | TLS, cifrado, secretos | TEC-16 CA de la finca; contraseña de la base por variable de entorno | PARCIAL: TLS falta; `compose.yaml` trae `florlogic` como clave de desarrollo |
| A03 Injection | consultas parametrizadas | `JdbcClient` en `AnotacionesRecibidasAdaptadorJdbc` | VERIFICAR con una prueba de inyección |
| A04 Insecure Design | STRIDE, diseño seguro | — | FALTA (ver 1.5) |
| A05 Security Misconfiguration | configuraciones seguras | base solo en `127.0.0.1`, WAF en `On`, Nginx sin privilegios | PARCIAL |
| A06 Vulnerable Components | Trivy, Snyk, Dependency-Check | — | FALTA |
| A07 Authentication Failures | OAuth2, OIDC, MFA | OAuth2/OIDC con Keycloak (TEC-32) | FALTA · Keycloak trae MFA y OIDC; queda por resolver la gracia offline de 7 días (P-N01) |
| A08 Integrity Failures | integridad de artefactos | `outputTimestamp` fijo (construcción reproducible); paquete de configuración firmado (ADR-029) | PARCIAL · `slsa-1.0` |
| A09 Logging and Monitoring | logs, alertas, trazabilidad | bitácora (ESC-40), logs sin datos de negocio | FALTA |
| A10 SSRF | validación de destinos | el nodo no hace peticiones salientes en TX-1 | PARCIAL: sustentarlo |

## 3 · Manifiesto reactivo

| Propiedad | Pregunta para TX-1 | Estado |
|---|---|---|
| Responsive | ¿Responde a tiempo cada partición? (ESC-38, ESC-61) | FALTA |
| Resilient | Corte de Wi-Fi o de luz sin perder datos (ES-S02, ES-S03), Toxiproxy | FALTA |
| Elastic | Temporada alta, 13 simuladores (TX-1.T 6) | FALTA |
| Message Driven | Outbox del celular, eventos de Modulith, cola en PostgreSQL | FALTA |

## 4 · Twelve-Factor (lectura rápida del 28-oct; cada fila se sustenta con evidencia)

| Factor | Hoy | Estado |
|---|---|---|
| Codebase | un repo, ramas por papel | HAY |
| Dependencies | Maven Wrapper, versiones fijas en el padre | HAY |
| Config | `FLORLOGIC_*` por entorno con valor de desarrollo | PARCIAL |
| Backing Services | PostgreSQL por URL | HAY |
| Build, Release, Run | sin imagen del backend (Jib, TEC-28, no está) | FALTA |
| Processes | backend sin estado en memoria | VERIFICAR |
| Port Binding | Spring Boot embebido | HAY |
| Concurrency | hilos virtuales | PARCIAL |
| Disposability | `restart: unless-stopped`; apagado ordenado sin probar | PARCIAL |
| Dev/Prod Parity | Testcontainers usa las mismas imágenes de `compose.yaml` | PARCIAL (`postgres:16` sin parche fijo) |
| Logs | stdout; JSON pendiente | PARCIAL |
| Admin Processes | Flyway | HAY |

## 5 · Well-Architected (los seis pilares de AWS que nombra la guía)

| Pilar | Pregunta para FlorLogic | Estado |
|---|---|---|
| Operational Excellence | ¿Se despliega y observa sin el agente? CI/CD, runbooks | FALTA |
| Security | §1.5 y §2 | PARCIAL |
| Reliability | local-first, idempotencia (ADR-027), respaldo (TX-2) | PARCIAL |
| Performance Efficiency | carga de temporada (ESC-38) | FALTA |
| Cost Optimization | CN-35, una instalación por finca, sin broker ni malla | PARCIAL (sustentado en ADR) |
| Sustainability | nodo pequeño, sin hardware de IA (ADR-030) | PARCIAL |

## 6 · Matriz de dominio por integrante (la meta del pedido)

Cada integrante marca su fecha cuando lo hizo **sin agente**. Una fila por componente que corre en TX-1 hoy; las
demás se añaden cuando existan. Integrantes (README): **Juan Pablo Avendaño Duque** (J) y **Jeronimo Montoya Londoño** (JM); cada celda lleva `J <fecha>` o `JM <fecha>`.

| Componente | Cómo se apaga y reconstruye (lo escribe el equipo) | Explica (teoría y qué hace aquí) | Construye desde cero | Lo rompe y lo repara |
|---|---|---|---|---|
| `contrato` (records, Maven) | | | | |
| `backend-finca` · `ingesta` (hexágono) | | | | |
| PostgreSQL + Flyway | | | | |
| Gateway Nginx | | | | |
| WAF ModSecurity + CRS | | | | |
| `simulador` (picocli) | | | | |
| `pruebas-extremo-a-extremo` | | | | |
| `compose.yaml` completo | | | | |
| Pipeline CI/CD (cuando exista) | | | | |
| Observabilidad (cuando exista) | | | | |

**Lo que el pedido exige y hoy no existe:**
- **Saltar componentes:** perfiles de Compose (p. ej. sin WAF, sin gateway) para probar cada pieza sola.
- **Apagar uno y reconstruirlo:** el backend no tiene imagen (corre con `java -jar` en el anfitrión); sin imagen
  (Jib, TEC-28) no se puede reconstruir dentro de Compose.
- **Un cambio dispara el pipeline:** no hay `.github/workflows` (TEC-30).

## 7 · Orden sugerido (propuesta; lo decide Juan)

1. Todos construyen lo que ya hay: `./mvnw verify` con Docker, `docker compose up`, `IA_probar-canal.sh`.
2. Cada uno apaga, rompe y reconstruye los componentes de §6 que ya corren, y escribe su columna «cómo».
3. Imagen del backend (Jib) y perfiles de Compose: sin esto no hay «saltar» ni «reconstruir».
4. Pipeline de GitHub Actions que construye y prueba en cada empuje.
5. DevSecOps dentro del pipeline: JaCoCo, Trivy, SonarQube (cada uno con su fila TEC antes).
6. Documentos de sustentación: una página por cada `DEFENDER` de §1.3 (broker, service mesh, data mesh), STRIDE, mapa OWASP, Zero Trust, 12 factores, Well-Architected, reactivo, CAF.
7. Los módulos de TX-1 que faltan, en el orden de `IA_PRIORIDAD-componentes-TX1.md`.

## 8 · Lo que decide Juan

1. ~~IdP, broker, service mesh y data mesh: ¿ADR o demostración?~~ **Decidido 28-oct-2026: se sustentan con
   los ADR** (ADR-009, ADR-001/017, BB-17), sin demostración. **El IdP no:** «should be a service, not something we
   build from scratch; the objective is to implement the techs there are» (Juan, 28-oct-2026) → Keycloak (TEC-32).
2b. **Para Juan:** cambiar ADR-007 (identidad propia → Keycloak autohospedado; su propia «alternativa a escala») y
   decidir si TEC-13 (credencial JWS propia con Nimbus) y el módulo `llaves` siguen, quedan solo para el paquete de
   configuración firmado, o se retiran. Pregunta abierta: cómo cumple Keycloak la gracia offline de P-N01 y CN-23.
2. ~~¿§7 reemplaza `IA_LEER-PRIMERO.md`?~~ **Decidido 28-oct-2026: va en paralelo.** La explicación del Segmento 1
   y el avance conectado siguen vigentes.
3. ~~Nombres de §6~~: Juan y Jeronimo (README).
4. Herramientas de §1.6 que necesitan fila TEC: JaCoCo, SonarQube (Community Build o Cloud), Trivy.

## 9 · Choques entre lo que manda (README) y los documentos IA o el código (28-oct-2026)

Lo vi en el README y en 5 de sus 9 imágenes (N2, despliegue, N3 backend, paquetes, secuencia feliz). Falta leer N3
app, N3 web, N3 servicios en línea y la secuencia de errores.

| # | Qué dice el README | Qué dice lo otro | Quién lo resuelve |
|---|---|---|---|
| CH-1 | ADR-007 `Aceptada`: identidad propia con credencial offline de 24 h. El N3 del backend dice «Identidad propia del nodo **o Keycloak autohospedado** (propuesta)» | Juan, 28-oct: el IdP es un servicio (TEC-32 Keycloak) | Juan: actualizar el texto de ADR-007 en el README; el N3 ya lo permite |
| CH-2 | N3: Planificador de tareas «Quartz o el planificador del sistema operativo» | TEC-09: db-scheduler (aceptado 13-oct) | Juan |
| CH-3 | ADR-001…037 con títulos y estados propios (p. ej. ADR-019, ADR-021, ADR-030 depreciadas; ADR-032…037 nuevas) | `FlorLogic-alternativa-de-solucion-y-ADR.md` llega a ADR-031 con otros estados | Manda el README; el documento IA queda como antecedente |
| CH-4 | N3 y despliegue: gateway «nginx o Caddy (propuesta)», PostgreSQL «(propuesta)», respaldo «pgBackRest/Barman o Restic/Borg», llaves «HashiCorp Vault u OpenBao», migraciones «Flyway/Liquibase» | TEC-15 Nginx, TEC-06 PostgreSQL 16, TEC-20 restic, TEC-08 Flyway: ya elegidos y en el código | Juan: el README puede cerrar la elección o dejarla abierta |
| CH-5 | No hay C4 nivel 1 | La guía lo pide | El equipo lo dibuja en el README |
