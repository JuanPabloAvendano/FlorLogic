REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 28-oct-2026, a pedido de Juan («lista de los componentes que faltan de TX-01, con tecnología ya construida; catálogo de mensajes, pasarela de notificación, todo lo del C4 nivel 3 y la secuencia de GitHub»)
Estado de revisión: SIN REVISAR
Manda por encima: `README.md` de `documentacion-desarrollo` (commit `f8e5bb4`) y sus imágenes (copia en `.claude/contexto/referencias/arquitectura-readme/`) · `IA_TECNOLOGIAS-vigentes.md` · la palabra de Juan

# ESPEC · Esqueleto de componentes de TX-1

## Ficha
| Campo | Valor |
|---|---|
| Elemento | Flujo entre componentes: todos los componentes del C4 nivel 3 que toca TX-1, levantados y conectados |
| **Definición acordada** | El esqueleto de comunicación del nodo de la finca: cada componente del C4 nivel 3 existe y corre (como contenedor o como módulo de Spring Modulith) con tecnología ya construida, y cada conexión entre ellos funciona con una llamada mínima. **No** lleva reglas de negocio: ni evaluación de reglas, ni proyección, ni tableros reales; eso viene después sobre este esqueleto. **ACORDADA 28-oct-2026** («Es lo mismo») |
| Diccionario | `.claude/contexto/IA_DICCIONARIO.md` (`CMP-n`, `MSG-n`, `CH-n`) |
| Plantilla | `evento-mensaje` (entre módulos) y `configuracion` (contenedores); una por ronda |
| Dominio | Entra: cada caja y cada flecha del C4 N3 del backend, N2, despliegue, N3 web y la secuencia de §8 · Sale: Compose con los contenedores, módulos con su API pública y sus eventos, una prueba de humo por conexión · Fuera: reglas de negocio, app de captura real, servicios en línea de TX-2 (§7) |
| Generalidad | flujo |
| Para qué | especificar para programar, y que el equipo entienda y reconstruya cada pieza |
| Estado | BORRADOR |
| **Etapa actual** | 3 Preguntas · Segmento 1 (CMP-3), ronda 1, plantilla `configuracion` |
| **Dónde retomar** | **PAUSADA 28-oct-2026 por Juan** tras la ronda 1 (52 %, coherencia no pasa). Al volver: pedir permiso para los arreglos de redacción (COH-3/K-19, COH-4, COH-5, COH-11, K-17, K-21) y la ronda 2, en este orden: K-20/K-10, COH-1, K-15/COH-6, K-14, K-16, K-13, COH-2, K-18. Juan tiene pendientes 3 preguntas de estudio (AppRole, resolver perezoso, idempotencia) |
| Precisión | **≈52 %** (cobertura 62,5 · contradicciones 15 · prácticas ≈77 · trazabilidad 50), verificador, 28-oct-2026, ronda 1 · coherencia **no pasa** |
| Completitud | — · Explicación — |
| Avance por etapa | Tamaño ✔ · Consulta ✔ · Preguntas: ronda 1 ✔ (52 %), ronda 2 por pedir |

## 1 · Propósito
Que TX-1 use la mayor cantidad posible de componentes (Juan, 8-oct y 28-oct) y que cada uno exista y se comunique
antes de escribir su lógica. Tecnología ya construida como opción principal (Juan, 28-oct: «el IdP es un servicio, no
lo construimos desde cero»). Base: C4 nivel 3 del backend (README §6) y secuencia de una partición (README §8).

## 2 · Decisiones
| Id | Afirmación | Fuente | Estado |
|---|---|---|---|
| A-1 | Primero el esqueleto de todos los componentes y su comunicación; la lógica de negocio va después | Juan, 28-oct | decidida |
| A-2 | Tecnología ya construida como opción principal; un componente sin tecnología en el C4 recibe una por su caso de uso (§5, columna «Propuesta») | Juan, 28-oct | decidida |
| A-3 | Alcance: nivel 1 y nivel 2 de §5 | Juan, 28-oct (selector) | decidida |
| A-4 | Lo que está en GitHub manda; los `IA_*` son antecedente | Juan, 28-oct | decidida |
| A-5 | Identidad = Keycloak (TEC-32), no construido | Juan, 28-oct | decidida |
| A-6 | Sin broker ni malla de servicios: los mensajes entre módulos son eventos de Spring Modulith guardados en la misma base (ADR-009, ADR-017) | Juan, 28-oct · README ADR-009 | decidida |
| A-8 | La lista de §5.1, el catálogo de §5.3 y el orden de §8 son el alcance | Juan, 28-oct («Es lo mismo») | decidida |
| A-9 | La imagen del backend se construye con un **Dockerfile multietapa** que escribe el equipo; Jib sale del plan. TEC-28 pasa a `AJUSTADO 28-oct-2026` | Juan, ronda 1 P1 (★) · cierra K-6 | decidida |
| A-10 | Los secretos de arranque (clave de administrador de PostgreSQL, credencial del backend ante OpenBao) son **Compose secrets**: archivos fuera de git montados en `/run/secrets/` | Juan, ronda 1 P2 | decidida |
| A-11 | **El vault es obligatorio ya** (Juan: «secrets vault use is necessary here»): OpenBao (CMP-20) entra al Segmento 1. El backend lee la clave de la base del motor **KV estático** de OpenBao al arrancar, con Spring Cloud Vault; los usuarios dinámicos del motor de bases van después, sobre el mismo OpenBao | Juan, ronda 1 P3 (★) | decidida |
| A-12 | OpenBao corre como **servidor persistente** (almacenamiento en archivo, volumen propio). Un contenedor de una sola vez, `openbao-init`, hace siempre lo mismo y es idempotente: si no está iniciado, `init`; en cada arranque, `unseal`; habilita KV v2, escribe `kv/florlogic/backend-finca`, crea una política de solo lectura de esa ruta y un **AppRole** `backend-finca`. El backend entra con AppRole (`role_id` + `secret_id` como Compose secrets) y espera a que `openbao-init` termine bien | Juan, ronda 1 P4 (★) | decidida |
| A-13 | `application.yaml`: la URL y el usuario conservan su defecto de desarrollo (Compose cambia la URL a `jdbc:postgresql://base:5432/florlogic`); **la clave no tiene defecto**: en Compose sale de OpenBao (`spring.config.import: optional:vault://`). Para correr desde Eclipse, perfil `local` con el vault apagado y `FLORLOGIC_DB_CLAVE`. Las pruebas con Testcontainers corren con el vault apagado | Juan, ronda 1 P5 (★) | decidida |
| A-14 | La prueba del Segmento 1 es `NodoCompletoTest` en `pruebas-extremo-a-extremo`: levanta el `compose.yaml` real con Testcontainers (`ComposeContainer`, construye la imagen), espera al backend sano, manda la partición dorada por el gateway y espera `200`; comprueba también que el backend no publica puerto en el anfitrión. Se salta sin Docker | Juan, ronda 1 P6 (★) | decidida |
| A-15 | La credencial AppRole (`role_id`, `secret_id`) viaja por un **volumen con nombre** `bao-credenciales`: `openbao-init` la escribe y el backend lo monta **solo lectura**, con `depends_on: openbao-init: service_completed_successfully`. A-10 queda acotada: Compose secret solo para lo que existe antes de `up` (clave de administrador de PostgreSQL) | Juan, ronda 1 P7 (★) · cierra K-8 | decidida |
| A-16 | Un contenedor de una sola vez, `secretos-init`, crea la clave de administrador de PostgreSQL en un volumen `secretos` **solo si falta** (idempotente). `base` la lee con `POSTGRES_PASSWORD_FILE` y `openbao-init` la copia a KV. Sin paso a mano en el anfitrión: igual en una máquina nueva, en `NodoCompletoTest` y en el CI | Juan, ronda 1 P8 · cierra K-9 | decidida |
| A-17 | El gateway resuelve el backend **por petición**: `resolver` con el DNS de Docker y una variable en `proxy_pass`; sale el bloque `upstream` (sus ajustes pasan a la `location`). El gateway arranca solo, sin `depends_on` del backend, y responde `502` mientras el backend no contesta (README §8). Toca `despliegue/gateway/default.conf.template`, que es de `ESPEC-gateway-finca.md`: se le avisa allí | Juan, ronda 1 P9 (★) · cierra K-11 | decidida |
| A-18 | El `resolver` del gateway sale de una variable, `FLORLOGIC_DNS` (en Compose, `127.0.0.11`, el DNS de Docker). `CanalPorGatewayTest` pasa a `FLORLOGIC_BACKEND` una **dirección IP** del anfitrión, que no necesita búsqueda de nombre; la prueba conserva todos sus casos del WAF | Juan, ronda 1 P10 (★) · cierra K-12 | decidida |
| A-19 | El Dockerfile vive en `codigo/backend-finca/Dockerfile` y el contexto de construcción es `codigo/` (necesita el `pom.xml` padre, `contrato` y `backend-finca`); la clave en OpenBao es `kv/florlogic/backend-finca` → `spring.datasource.password` | plantilla `configuracion` §10 · mapa del Segmento 1 | supuesto |
| A-7 | Cada mensaje es **llamada síncrona** (API pública del otro módulo) o **evento** (asíncrono, guardado); lo dice la flecha del C4: «Llamada en proceso» → llamada, «Trabajo encolado en la misma base» → evento o trabajo | C4 N3 | supuesto |

## 3 · Variables
| Nombre | Valor | Tipo | Dueño |
|---|---|---|---|
| Versiones de cada imagen | por fijar al entrar, nunca etiqueta móvil | fija | quien la mete, con su fila TEC |

## 4 · Comportamiento
- **C-1** Dado el Compose arriba · cuando el simulador manda la partición dorada al gateway · entonces responde `200` y las anotaciones quedan en `base` (cubre A-9, A-11, A-12, A-13)
- **C-2** Dado el Compose arriba · cuando se pide el 18080 o el 8080 del backend en el anfitrión · entonces no hay nada escuchando (cubre ESC-29)
- **C-3** Dado OpenBao reiniciado · cuando `openbao-init` vuelve a correr · entonces solo hace `unseal` y deja KV y AppRole como estaban (cubre A-12)

## 5 · Forma técnica

### 5.1 · Componentes (C4 nivel 3 del backend, más lo que lo rodea)

Estado: `HAY` · `PARCIAL` · `FALTA`. «C4» es lo que dice el diagrama; «Propuesta» es la tecnología ya construida que
propongo cuando el C4 no elige o deja dos opciones. Ninguna propuesta entra al código sin su fila TEC (`PROPUESTA` →
Juan la acepta).

**Nivel 1 · TX-1 lo recorre**

| Id | Componente (C4) | Dónde | C4 dice | Propuesta | Estado |
|---|---|---|---|---|---|
| CMP-1 | API Gateway de la finca | contenedor `gateway` | nginx o Caddy | Nginx + ModSecurity/CRS (TEC-15, TEC-31) | HAY |
| CMP-2 | Base de datos de la finca | contenedor `base` | PostgreSQL | PostgreSQL 16 + Flyway (TEC-06, TEC-08) | HAY |
| CMP-3 | Backend de la finca como contenedor | contenedor `backend` | servicio Java | Dockerfile multietapa (ajusta TEC-28, que dice Jib) | FALTA |
| CMP-4 | Ingreso de sincronización | módulo `ingesta` | servicio del backend | Spring MVC (existe) | PARCIAL: el canal |
| CMP-5 | Identidad y permisos | contenedor `keycloak` + módulo `identidad` | propia o Keycloak autoalojado | Keycloak + Spring Security OAuth2 Resource Server (TEC-32, TEC-14) | FALTA |
| CMP-6 | Emisión de credencial | módulo `llaves` | firma de token | Keycloak emite el token; la credencial offline del paquete se firma con Nimbus JWS (TEC-13) | FALTA · ver K-1 |
| CMP-7 | Motor de reglas · instancia servidor | módulo `reglas` | mismo artefacto que el dispositivo | JSON Logic (TEC-27) con `json-logic-java` | FALTA |
| CMP-8 | Datos maestros y parametrización versionada | módulo `configuracion` | tablas versionadas + paquete firmado | PostgreSQL + Flyway; paquete firmado con Nimbus JWS (TEC-13) | FALTA |
| CMP-9 | Bitácora de auditoría | módulo `bitacora` | tabla append-only con encadenamiento por hash | tabla PostgreSQL + disparador que impide `UPDATE`/`DELETE` + SHA-256 del JDK | FALTA |
| CMP-10 | Servicio de notificación (pasarela) | módulo `notificacion` + contenedor `mailpit` | SMTP local + aviso en la app | Spring Boot Mail; Mailpit como SMTP de desarrollo; el aviso viaja en la respuesta de la siguiente sincronización | FALTA |
| CMP-11 | Planificador de tareas | módulo `trabajos` | Quartz o el planificador del SO | db-scheduler (TEC-09) **o** Quartz: K-2 | FALTA |
| CMP-12 | Mensajería entre módulos (catálogo §5.3) | todos los módulos | «trabajo encolado en la misma base» | Spring Modulith: eventos + registro de publicación JDBC (TEC-05) | FALTA |
| CMP-13 | Consulta y tableros | módulo `consulta` | endpoints propios | Spring MVC + `JdbcClient` sobre tablas de consulta (ADR-010) | FALTA |
| CMP-14 | Gestión de dispositivos | módulo `dispositivos` | capacidad propia de la consola | Spring + PostgreSQL | FALTA |
| CMP-15 | Motor de proyección | módulo `proyeccion` | cálculo en el backend · BLOQUEADO por D3 | en TX-1, simulado (devuelve un valor fijo) | FALTA |
| CMP-16 | Observabilidad | módulo `observabilidad` + contenedores | registros estructurados + Prometheus/Grafana | Micrometer + `micrometer-registry-prometheus`, logs ECS en stdout, Prometheus, Grafana, Loki, Alloy (TEC-17) | PARCIAL: Actuator |
| CMP-17 | Empaquetado y despliegue | `despliegue/` + CI | Docker Compose + Flyway/Liquibase | Compose + Flyway; GitHub Actions (TEC-30, C4 servicios en línea) | PARCIAL: Compose |
| CMP-18 | Aplicación web: tableros | módulo `consulta` (vistas) | fetch + TanStack, ECharts o Chart.js | Thymeleaf + htmx (TEC-24) · K-3 | FALTA |

**Nivel 2 · mismo patrón, después del nivel 1**

| Id | Componente (C4) | Dónde | C4 dice | Propuesta | Estado |
|---|---|---|---|---|---|
| CMP-19 | Caché de producciones activas | contenedor `cache` | **contenedor** · SIN DECIDIR (ADR-034 propuesta) | Valkey + Spring Cache; alternativa Caffeine en proceso · K-4 | FALTA |
| CMP-20 | Key Vault · **sube al Segmento 1 (A-11)** | contenedor `openbao` | HashiCorp Vault u OpenBao | OpenBao + Spring Cloud Vault (también saca la clave de la base de `compose.yaml`) | FALTA |
| CMP-21 | Servicio de cifrado (TLS) | contenedor `ca` + gateway | TLS propio + cifrado de volumen | CA de la finca (TEC-16) con step-ca; el cifrado de volumen es del sistema operativo, fuera de Compose | FALTA |
| CMP-22 | Servicio de respaldo | contenedor `respaldo` + módulo `respaldo` | pgBackRest/Barman o Restic/Borg | `pg_dump` + restic (TEC-19, TEC-20) | FALTA · puente a TX-2 |
| CMP-23 | Custodia de respaldos | contenedor `minio` | MinIO o S3 | MinIO (TEC-21) | FALTA · puente a TX-2 |

### 5.2 · Comunicación entre contenedores

| De → A | Cómo | Fuente |
|---|---|---|
| Simulador → `gateway` | HTTPS/JSON en red local (hoy HTTP) | C4 N2, CT-03 |
| `gateway` → `backend` | HTTP interno por nombre de servicio, `backend:8080`; el backend sin `ports:` | C4 N3, ESC-29 |
| `backend` → `base` | SQL por JDBC | C4 N3 |
| Simulador → `keycloak` (por el gateway) | OIDC: pide el token | TEC-32 |
| `backend` → `keycloak` | trae las llaves públicas (JWKS) y verifica cada token localmente | TEC-32 |
| `prometheus` → `backend` | lee `/actuator/prometheus` | C4 N3 Observabilidad |
| `backend` → `alloy` → `loki` → `grafana` | logs JSON por stdout | C4 N3 Observabilidad |
| `backend` → `mailpit` | SMTP | C4 N3 Notificación |
| `backend` → `cache` · `vault` · `minio` | protocolo de cada uno (nivel 2) | C4 N3 |

### 5.3 · Catálogo de mensajes entre módulos (de las flechas del C4 N3)

| Id | De → A | C4 dice | Forma | Nombre propuesto |
|---|---|---|---|---|
| MSG-1 | `ingesta` → `reglas` | Solicita evaluación · llamada en proceso | llamada | `EvaluarAnotacion` → veredicto y motivo |
| MSG-2 | `ingesta` → `bitacora` | Registra la sesión de sincronización · llamada en proceso | llamada, en la misma transacción | `RegistrarSesion` |
| MSG-3 | `ingesta` → `notificacion` | Avisa cuando un registro fue descartado · trabajo encolado en la misma base | evento | `AnotacionDescartada` |
| MSG-4 | `ingesta` → caché | Actualiza la producción activa (ADR-034) | evento | `AnotacionesAplicadas` |
| MSG-5 | `identidad` → `llaves` | Solicita la credencial · llamada en proceso | llamada | `EmitirCredencial` |
| MSG-6 | `llaves` → `configuracion` | Deposita la credencial en el paquete | llamada | `DepositarCredencial` |
| MSG-7 | `reglas` → `configuracion` | Lee reglas y rangos · lectura en memoria del paquete vigente | llamada | `PaqueteVigente` |
| MSG-8 | `configuracion` → `proyeccion` | CT-04 · snapshot inmutable de parámetros | llamada | `SnapshotParametros` |
| MSG-9 | `trabajos` → `proyeccion` | Dispara la regeneración semanal · trabajo encolado | trabajo | `RegenerarProyeccion` |
| MSG-10 | `trabajos` → `respaldo` | Dispara el respaldo y su prueba de restauración · trabajo encolado | trabajo | `RespaldarYProbar` (nivel 2) |
| MSG-11 | `notificacion` → celular | Entrega el aviso en la siguiente sincronización | dentro de la respuesta HTTP | campo `avisos` |
| MSG-12 | `dispositivos` → celular | Ordena sincronizar o actualizar, en la siguiente sincronización | dentro de la respuesta HTTP | campo `ordenes` |
| MSG-13 | `consulta` → celular | Entrega el paquete versionado · [!] el C4 duda si lo publica `dispositivos` | HTTP | K-5 |
| MSG-14 | `identidad` → todos | Autoriza contra el par (rol, empresa) · llamada en proceso | filtro de Spring Security | — |

## 6 · Contradicciones y pendientes
| Id | Qué | Tipo | Dueño | Estado |
|---|---|---|---|---|
| K-1 | Credencial offline de 24 h + 7 días (P-N01, ADR-007 aceptada) frente a Keycloak: ¿token de Keycloak verificado con su llave pública, o credencial propia firmada con Nimbus? | decisión | Juan | abierta |
| K-2 | Planificador: C4 dice Quartz; TEC-09 aceptó db-scheduler (CH-2) | decisión | Juan | abierta |
| K-3 | Web: C4 N3 web dice fetch + TanStack + ECharts; TEC-24 aceptó Thymeleaf + htmx | decisión | Juan | abierta |
| K-4 | Caché: el C4 la dibuja como contenedor; ADR-034 sigue en propuesta | decisión | Juan | abierta |
| K-5 | MSG-13: ¿el paquete versionado lo entrega `consulta` o `dispositivos`? (el C4 lo marca «[!] revisar») | decisión | Juan | abierta |
| K-6 | CMP-3 pide Dockerfile; TEC-28 aceptó Jib | decisión | Juan | cerrada por A-9 |
| K-8 | Cascada de A-10 y A-12: el `secret_id` de AppRole lo crea `openbao-init` al correr, y un Compose secret tiene que existir como archivo **antes** de `compose up`. Hay que decidir por dónde le llega al backend | decisión | Juan | cerrada por A-15 |
| K-9 | Cascada de A-10 y A-14: los archivos de los secretos de arranque están fuera de git, así que en una máquina nueva, en la prueba y en el CI no existen. Alguien los tiene que generar | decisión | Juan | cerrada por A-16 |
| K-10 | Consecuencia de A-15 y A-16: ningún secreto queda como Compose secret (todo viaja por volúmenes de los contenedores de inicio). A-10 queda sin uso; su regla de fondo, ningún secreto en git, sigue | aviso | Juan | anotado: decir si quiere conservar Compose secrets por aprendizaje |
| K-11 | El gateway resuelve `backend` al arrancar (`upstream { server ${FLORLOGIC_BACKEND}; }`): si el contenedor `backend` no existe todavía, Nginx no arranca. Choca con la prueba «con el backend apagado es 502» | decisión | Juan | cerrada por A-17 |
| K-12 | Cascada de A-17: el `resolver` de Nginx pregunta solo al DNS, **no lee `/etc/hosts`**. `CanalPorGatewayTest` llega al backend por `host.testcontainers.internal` (en `/etc/hosts`) y en una red sin DNS de Docker no hay `127.0.0.11`: esa prueba se rompería | decisión | Juan | cerrada por A-18 |
| COH-1 | Tras reiniciar el anfitrión nadie vuelve a correr `openbao-init`: Docker ignora `depends_on` al arrancar, OpenBao vuelve sellado y el backend se reinicia en bucle. Los de una sola vez necesitan `restart: "no"`. Falta decidir el desellado (a mano con `compose up -d`, o automático). `optional:vault://` de A-13 contradice A-11 (obligatorio): el backend debe fallar diciendo «vault sellado» (`fail-fast`) | confiabilidad · **bloqueante** | Juan | abierta |
| COH-2 | `POSTGRES_PASSWORD_FILE` solo se aplica al crear el volumen `datos`: en la máquina de Juan ya existe con `florlogic`, la clave nueva no entra y C-1 falla. C-3 («KV como estaba») impide resincronizar. Propuesta: `openbao-init` reescribe la clave en KV desde el archivo en cada corrida, y un `down -v` una sola vez o volumen renombrado | confiabilidad · **bloqueante** (barato) | Juan | abierta |
| COH-3 | A-18 incompleta: si `CanalPorGatewayTest` no pasa `FLORLOGIC_DNS` queda `resolver ;` y Nginx no arranca; con `exposeHostPorts` la IP útil es la del contenedor de reenvío de Testcontainers, no la del anfitrión (verificar) | cohesión · **bloqueante** | IA, redacción | abierta |
| COH-4 | `NodoCompletoTest` con el `compose.yaml` real choca en el 80 y en `127.0.0.1:5432` si el Compose de desarrollo está arriba. Propuesta: puertos del anfitrión desde variables, la prueba pone `0` | disponibilidad · **bloqueante** para A-14 | IA, redacción | abierta |
| COH-5 | El `resolver` de A-17 sin `valid=`/`resolver_timeout`: IP vieja tras recrear el backend y espera de 30 s por defecto. Propuesta `valid=10s ipv6=off`, `resolver_timeout 2s`. `max_fails=0` deja de aplicar; avisar A-2, A-10, A-12 de `ESPEC-gateway-finca` | disponibilidad · mayor | IA | abierta |
| COH-6 | Dónde quedan las llaves de `unseal` y el token raíz: nunca en `bao-credenciales` (lo monta el backend). Propuesta: volumen aparte, revocar el raíz al terminar `openbao-init`, supuesto frente a ADR-012 | confiabilidad · seguridad · mayor | Juan | abierta |
| COH-7 | Una salud de OpenBao con `bao status` da «no sano» mientras está sellado: `openbao-init` debe depender de `service_started`, no de `service_healthy` | cohesión · mayor | IA | abierta |
| COH-8 | Herramientas de salud: ¿`eclipse-temurin:27-jre` trae `curl`/`wget`? (verificar); OpenBao con su CLI. AppRole sin `secret_id_ttl`, `num_uses` ni regla de regeneración | confiabilidad · mayor | IA / Juan | abierta |
| COH-9 | = K-7: Spring Cloud Vault frente a Boot 4.1.1 / Framework 7.0.9, y filas TEC de OpenBao y Spring Cloud Vault | compatibilidad · bloquea el código | Juan / IA | abierta |
| COH-10 | `NodoCompletoTest` construye la imagen con Maven en cada corrida: `.dockerignore`, caché de BuildKit para `.m2`, limpiar imágenes y volúmenes del proyecto de prueba | eficiencia · menor | IA | abierta |
| COH-11 | C-2 cita ESC-29 (es de la interfaz de salida, no de los puertos); C-1 pide anotaciones en `base` pero A-14 solo mira el `200`; §8 fila 1 y la Precisión aún dicen «Compose secrets» | coherencia · menor · redacción | IA | abierta |
| K-13 | Salud y orden de arranque: `base` y el backend sin healthcheck; la imagen JRE puede no traer `curl`. Sin señal de «sano», A-14 no puede esperar y el backend puede arrancar antes que PostgreSQL | falta · camino mínimo | IA / Juan | abierta (con COH-8) |
| K-14 | Spring Cloud Vault: `role-id`/`secret-id` reciben valores, no archivos (¿`configtree:` sobre `bao-credenciales`?); faltan `uri`, `kv.backend`, `default-context` | falta · camino mínimo | IA / Juan | abierta |
| K-15 | Dónde viven las llaves de `unseal` y el token raíz (volumen aparte o anfitrión); un bind dentro de `codigo/` arriesga PR-06 | falta · camino mínimo | Juan | abierta (= COH-6) |
| K-16 | Archivos de OpenBao: versión fija de la imagen, `openbao.hcl` (archivo, listener sin TLS), script de `openbao-init` y su ubicación, salud que acepte «sellado» | falta · camino mínimo | IA / Juan | abierta (con COH-7) |
| K-17 | `IA_probar-canal.sh` (arranca el backend en el anfitrión con la clave por defecto) e `IA_LEEME.md` pasan al backend dentro de Compose; `up --wait` con contenedores de una sola vez sin comprobar | contradicción con el código | IA | abierta |
| K-18 | `NodoCompletoTest`: puertos fijos y cómo comprueba las filas de C-1 sin conocer la clave al azar | falta | IA / Juan | abierta (con COH-4) |
| K-19 | Red de `CanalPorGatewayTest`: puente por defecto sin `127.0.0.11`; de dónde sale la IP | falta | IA | abierta (= COH-3) |
| K-20 | Contradicción directa: A-10 sigue «decidida» y §8 dice «Compose secrets», pero A-15/A-16 ya no usan ninguno (K-10) | **contradicción directa** · redacción + respuesta de Juan a K-10 | Juan / IA | abierta |
| K-21 | `ESPEC-gateway-finca.md` A-2 y A-4 siguen con el backend en el anfitrión y el `upstream`: falta el aviso que promete A-17 | contradicción con elemento vecino | IA, redacción | abierta |
| K-7 | OpenBao y Spring Cloud Vault no tienen fila TEC; falta comprobar qué versión de Spring Cloud Vault funciona con Spring Boot 4.1.1 | decisión + verificar | Juan / IA | abierta: bloquea el código del Segmento 1, no las preguntas |

## 7 · Fuera del dominio (para otro canal)
- Riesgo anotado (A-12): en desarrollo las llaves de `unseal` y el token raíz quedan en un archivo del anfitrión fuera de git; en producción van a custodia separada (ADR-012). Segunda vuelta.
- Usuario de base con mínimos privilegios para el backend: llega con el motor de bases dinámico de OpenBao (después de A-11).
- Lógica de negocio de cada módulo (reglas, idempotencia de tres casos, proyección real, tableros reales): después.
- Generador de documentos (Apache POI + OpenPDF), interfaz de salida de datos (CT-05) y lectura del sistema heredado
  (UCanAccess): no los recorre TX-1.
- Servicios en línea (C4 N3 servicios en línea: control de borde, gateway de la nube, recepción de telemetría y de
  respaldos, distribución de versiones, IA analítica): TX-2.
- App de captura real: el simulador la reemplaza.

## 8 · Plan de segmentos
*(propuesta de orden; cada uno pasa por su ronda, diagrama draw.io y el «sí» de Juan)*

| Seg. | Qué | Por qué en este orden | Estado |
|---|---|---|---|
| 1 | CMP-3 + CMP-20: backend en contenedor, conectado al gateway por nombre, con su clave desde OpenBao (KV) y los secretos de arranque como Compose secrets | sin esto nada más se conecta dentro de Compose; el vault es obligatorio (A-11) | en preguntas |
| 2 | CMP-16: Prometheus, Grafana, Loki, Alloy | deja ver todo lo que viene después | por detallar |
| 3 | CMP-5, CMP-6: Keycloak y verificación del token en `ingesta` | la credencial viaja en cada partición | por detallar |
| 4 | CMP-12 + MSG-1…MSG-8: eventos y API públicas vacías entre módulos, con prueba de humo de cada mensaje | el esqueleto de mensajes | por detallar |
| 5 | CMP-9, CMP-10, CMP-11: bitácora, notificación con Mailpit, planificador | los primeros que consumen mensajes | por detallar |
| 6 | CMP-13, CMP-14, CMP-15, CMP-18 + CMP-7, CMP-8: módulos con API vacía y web mínima | completan el recorrido | por detallar |
| 7 | CMP-17: pipeline de GitHub Actions | un cambio dispara la construcción | por detallar |
| 8 | Nivel 2: CMP-19…CMP-23 | mismo patrón | por detallar |

## 9 · Matriz requisito → código → prueba
| Requisito | Archivo:símbolo | Prueba | Estado |
|---|---|---|---|

## 10 · Observaciones de revisión

## 11 · Informe de verificación
