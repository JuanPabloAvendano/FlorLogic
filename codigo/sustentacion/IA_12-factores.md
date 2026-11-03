REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# The Twelve-Factor App en FlorLogic

Fuente: The Twelve-Factor App (https://12factor.net). Cita clave del factor III: «twelve-factor … requires strict
separation of config from code. Config varies substantially across deploys, code does not» (The Twelve-Factor App ·
secc. 15, https://12factor.net/config).

**Matiz para la defensa.** Twelve-Factor nació para apps SaaS en la nube. FlorLogic es local-first: el «despliegue» es
el nodo de cada finca. Los factores aplican igual, porque ADR-016 los pide con otras palabras: «Un solo artefacto
empaquetado y una definición de infraestructura escrita como archivo. Todo lo propio de cada entorno vive en
configuración, nunca en el código».

**Estados:** `HAY` · `PARCIAL` · `PENDIENTE-Fx`.

| # | Factor | En FlorLogic | Evidencia | Estado |
|---|---|---|---|---|
| I | **Codebase** · un repo, muchos despliegues | Un repositorio con ramas por papel (`codigo-desarrollo`, `documentacion-desarrollo`); el mismo código para todas las fincas | `codigo/pom.xml` (padre de 5 módulos) | HAY |
| II | **Dependencies** · declaradas y aisladas | Maven Wrapper con versión fija (Maven 3.9.16 por Wrapper 3.3.4); versiones en el `pom.xml` padre con comentario `TEC-nn`; imágenes por `sha256` | `codigo/mvnw`, `codigo/pom.xml`, `compose.yaml` | HAY |
| III | **Config** · en el entorno | Variables `FLORLOGIC_*` (`FLORLOGIC_DB_URL`, `FLORLOGIC_INGESTA_DISPOSITIVO_ID`, `FLORLOGIC_WAF_MODO`…); los secretos salen de OpenBao (Spring Cloud Vault), nunca del repo; la clave de la base **no tiene defecto** | `backend-finca/src/main/resources/application.yaml`, `compose.yaml`; `NodoCompletoTest` | PARCIAL: quedan defectos de desarrollo (URL y usuario de la base, id del dispositivo en Compose) |
| IV | **Backing services** · recursos conectados | PostgreSQL por URL, OpenBao por `SPRING_CLOUD_VAULT_URI`, el backend del gateway por `FLORLOGIC_BACKEND`. Cambiar uno es cambiar una variable | `compose.yaml`; `CanalPorGatewayTest` apunta el mismo gateway a otro backend por IP | HAY |
| V | **Build, release, run** · etapas separadas | Construcción: `backend-finca/Dockerfile` multietapa (TEC-28) con `outputTimestamp` fijo; ejecución: `compose.yaml` | `backend-finca/Dockerfile` | PARCIAL · PENDIENTE-F6: no hay **release** versionada (imagen etiquetada y publicada en un registro por el pipeline) |
| VI | **Processes** · sin estado | El backend no guarda estado en memoria entre peticiones: el avance del celular y las anotaciones viven en PostgreSQL; la concurrencia se resuelve con versión optimista en la base, no con bloqueos en memoria | `AvanceRepositorioAdapterTest`, `CanalParticionesTest.dos_particiones_del_mismo_celular_a_la_vez_…` | HAY · se vuelve a comprobar cuando F1 añada la sesión (debe vivir en la base) |
| VII | **Port binding** · el servicio se exporta por un puerto | Spring Boot con Tomcat embebido en el 8080; el gateway en el 8080 del contenedor, publicado como `FLORLOGIC_GATEWAY_PUERTO` | `compose.yaml`, `Dockerfile` (`EXPOSE 8080`) | HAY |
| VIII | **Concurrency** · escalar con procesos | Hilos virtuales (TEC-04) dentro del proceso; tipos de proceso separados por contenedor (backend, gateway, vault, init). Escalar horizontal el backend no se necesita en una finca (ADR-001) | `application.yaml` (`spring.threads.virtual.enabled: true`) | PARCIAL: el trabajador de `trabajos` (db-scheduler, F5) puede ser otro tipo de proceso si SPK-04 lo pide |
| IX | **Disposability** · arranque rápido, apagado ordenado | `restart: unless-stopped`; sin vault el backend cae rápido y Docker lo reintenta (A-23); `openbao-init` es idempotente y se reanuda | `NodoArranqueSeguroTest` (6) | PARCIAL · PENDIENTE-F6: `server.shutdown=graceful` y una prueba de apagado a mitad de partición |
| X | **Dev/prod parity** | La misma imagen y el mismo `compose.yaml` en desarrollo, CI y finca; solo cambia la configuración (SoftHSM fuera de la finca, token USB en la finca, A-29) | `NodoCompletoTest` levanta el `compose.yaml` real | PARCIAL: las pruebas con Testcontainers del backend usan `postgres:16` sin `sha256` |
| XI | **Logs** · como flujo de eventos | Todo a stdout; el gateway con `log_format` propio con traza | `default.conf.template` | PARCIAL · PENDIENTE-F6: logs JSON del backend (`logging.structured.format.console=ecs`) |
| XII | **Admin processes** · tareas puntuales con el mismo código | Migraciones con Flyway al arrancar; `openbao-init` como contenedor de una sola vez (`restart: "no"`) con la misma imagen | `db/migration/V1…V3`, servicio `openbao-init` en `compose.yaml` | HAY |

## Más allá de los doce (lo que pide hoy la práctica)

- **Telemetría:** métricas y trazas (F6). La traza W3C ya cruza el gateway.
- **Seguridad:** secretos en un vault, no en variables (más estricto que el factor III).
- **API first:** el `contrato` es una biblioteca aparte con JSON dorados (`contrato/src/test/resources/dorados/`).

## Frase de defensa

> El mismo artefacto corre en la máquina de desarrollo, en CI y en la finca; lo único que cambia es la configuración,
> y los secretos ni siquiera son configuración: salen de un vault que se abre con una llave física. Lo que falta es la
> etapa de *release* versionada, que llega con el pipeline (F6).
