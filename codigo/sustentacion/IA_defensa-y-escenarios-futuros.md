REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# Guion de defensa por componente y escenarios futuros

## Parte A · Guion de defensa

Formato por componente: **qué es** (una frase) · **por qué así** (la decisión) · **cómo se prueba** · **qué se
demuestra en vivo** · **pregunta difícil probable**. Integrantes: Juan Pablo Avendaño Duque (J) y Jeronimo Montoya
Londoño (JM); el reparto lo deciden ellos.

### A.0 · Apertura (1 minuto)

> FlorLogic reemplaza el formulario a lápiz del operador de campo (2–4 horas al día) y los 8 días que tarda el dato en
> llegar. Lo que hace inútil al sistema es que los datos no sean correctos, así que **la confiabilidad es el atributo
> nº 1** (README §1). TX-1, «sincronizar una jornada ya capturada», es la transacción que lo pone a prueba: un celular
> que trabajó sin red entrega su jornada al nodo de la finca, sin perder ni duplicar nada.

Mostrar: `IA_C4-nivel1-contexto.png`, luego README §5 (nivel 2 y despliegue) y §8 (secuencia).

### A.1 · `contrato`

- **Qué:** biblioteca Java con los mensajes entre celular y nodo (records, `LectorDto`).
- **Por qué:** una sola definición para ambos lados (DRY); solo la lee el anillo de afuera (A-103: «`contrato` es el
  lenguaje publicado del cable … leída solo en el anillo de afuera»).
- **Prueba:** `DtoJsonTest` contra JSON dorados, `LectorDtoTest`.
- **En vivo:** cambiar un campo del JSON dorado y ver fallar `DtoJsonTest`.
- **Pregunta:** «¿Por qué no OpenAPI?» → TEC-11: records + JSON Schema versionado; el validador de esquema sigue
  `POR DEFINIR`.

### A.2 · `backend-finca` · módulo `ingesta`

- **Qué:** el hexágono que recibe sesiones y particiones.
- **Por qué:** Clean Architecture con nombres de ucotestapi (A-83, A-97); adaptador ≠ ensamblador (A-84).
- **Prueba:** `CapasTest` (7 reglas), `ModulosTest`, `CanalParticionesTest`, pruebas de contrato de los puertos.
- **En vivo:** importar `JdbcClient` en una clase de `dominio` y ver fallar `CapasTest`; reenviar la misma partición
  y ver que no duplica.
- **Pregunta:** «¿Qué pasa si dos particiones del mismo celular llegan a la vez?» → versión optimista; la que pierde
  recibe `409` con `Retry-After` y todo se deshace (A-98).

### A.3 · PostgreSQL + Flyway

- **Qué:** la base de la finca, una por empresa (ADR-003), migrada al arrancar.
- **Prueba:** `ArranqueBackendFincaTest`, `AnotacionRepositorioAdapterTest`, `AvanceRepositorioAdapterTest`.
- **En vivo:** `docker compose up`, `psql` desde el anfitrión (solo `127.0.0.1`).
- **Pregunta:** «¿Por qué `jsonb`?» → la anotación es un hecho con forma variable según el catálogo (ADR-024).

### A.4 · Gateway Nginx + WAF ModSecurity / OWASP CRS

- **Qué:** la única puerta de red del nodo.
- **Por qué:** BB-06 y TEC-31; en modo `On`, con exclusiones justificadas (920350 porque el celular llama por IP;
  `Authorization` fuera de las reglas).
- **Prueba:** `CanalPorGatewayTest` (25).
- **En vivo:** `curl` con `' OR 1=1 --` → `403` `gateway/bloqueado`; cambiar `FLORLOGIC_WAF_MODO=DetectionOnly` y ver
  que pasa pero queda anotado.
- **Pregunta:** «¿Un WAF en una red local cerrada?» → el Wi-Fi de la finca no es de confianza (Zero Trust); y el
  WAF da `403` antes del backend, que el celular trata como «no entregado, conservar».

### A.5 · OpenBao + SoftHSM / token USB + `openbao-init`

- **Qué:** vault de secretos con la llave en hardware.
- **Por qué:** A-22: «La llave del vault es de la máquina»; robar el disco no da la clave de la base.
- **Prueba:** `NodoCompletoTest`, `NodoArranqueSeguroTest` (6).
- **En vivo:** apagar `openbao` y reiniciar el backend: no queda escuchando, el gateway da `502`.
- **Pregunta:** «¿Y si se pierde el token USB?» → segundo token bajo doble control (ADR-012).

### A.6 · Imagen del backend (`Dockerfile`)

- **Qué:** construcción multietapa: JDK para compilar, JRE para correr, usuario 10001.
- **Por qué:** TEC-28 ajustado por Juan (el equipo lo entiende mejor que Jib); bases por `sha256`.
- **En vivo:** `docker compose up --build backend`.

### A.7 · Simulador (picocli)

- **Qué:** reemplaza al celular en TX-1; también es un hexágono.
- **Prueba:** `SincronizarParticionCasoUsoTest`, `NodoDeFincaAdaptadorHttpTest`.
- **En vivo:** `scripts/IA_probar-canal.sh`.

### A.8 · Lo que llega con la construcción

| Componente | Fase | Qué demostrar cuando exista |
|---|---|---|
| Sesión abrir / partición / cerrar, huella, rechazadas | F1 | `POST …/sesiones` → `201` con `desfase_ms`; partición sin sesión → `404 sesion-desconocida` |
| `configuracion`, `reglas`, `ciclo` | F2 | Una regla del catálogo rechaza en servidor lo mismo que en cliente |
| Keycloak, `dispositivos`, `bitacora` | F3 | Sin token → 401; rol equivocado → 403; MFA del gerente; `DELETE` en bitácora falla |
| `consulta` + web | F4 | Esperado / recibido / faltante tras sincronizar |
| `notificacion` (ntfy) + `trabajos` | F5 | Aviso en el teléfono; sin red, el aviso se reintenta |
| TLS, observabilidad, CI/CD, perfiles | F6 | Pipeline verde en GitHub; Grafana con métricas de `/ingesta`; `curl http://` falla |
| PWA sin red | F8 | Modo avión: capturar, volver a la red, sincronizar |

## Parte B · Escenarios futuros

Cada building block tiene una **alternativa a escala** y un **disparador** medible. Fuente principal: la tabla BB-nn y
ADR-017 de `FlorLogic-alternativa-de-solucion-y-ADR.md` (antecedente) y el README §4 (manda).

| Building block | Hoy | Alternativa a escala | Disparador |
|---|---|---|---|
| Cola / broker (BB-09) | Cola sobre PostgreSQL (db-scheduler) | RabbitMQ 4 | SPK-04: jornada > 30 min o proyección > 1 h con +60 % |
| Arquitectura (ADR-017) | Monolito modular, 17 módulos | Extraer `proyeccion` o `consulta` como servicio; luego malla | «Haya más de un equipo de dominio», o ESC-61 no se cumple subiendo hardware |
| Datos analíticos (BB-17, ADR-010) | Modelos de lectura en la misma base | Almacén analítico aparte; data mesh | «El BI de una empresa no quepa en su base con índices» |
| Caché (ADR-034) | Ninguna | Caché delante de la base con producciones activas | Consultas del gerente por encima de ESC-60 (≤ 1 h) o ESC-12 (≤ 5 s) |
| Identidad (BB-05 → TEC-32) | Keycloak en cada nodo | Keycloak central en los servicios en línea, federado con cada nodo | Muchas instalaciones que comparten operadores; resolver CH-6 |
| Gateway / WAF | Nginx + CRS en el nodo | CDN + WAF gestionado para los servicios en línea (BB-07, BB-08) | Exponer servicios en línea al público |
| Contenedores (BB-14) | Compose | k3s o Kubernetes ligero | Un nodo con varias réplicas o alta disponibilidad (ADR-017: «La tolerancia de fallo baje de la hora que fijó DEC-12») |
| Celular (ADR-008) | PWA | App nativa Android | ADR-008: «que la ventana sin abrir la aplicación llegue a una jornada o …» (disparadores de SPK-02) |
| Observabilidad (BB-13) | Actuator + Micrometer + logs JSON | Prometheus + Loki + Grafana en el nodo, OpenTelemetry | Soporte remoto que no logra la causa en ≤ 4 h (ESC-30) |
| Histórico (ADR-011, depreciada) | Todo en línea 5 años | Histórico por capas y almacenamiento frío | «Lo que ocurra primero» de ADR-022: SPK-06, SPK-03 o 5 años |
| IA (ADR-036) | Opcional | Modelo local ligero en el nodo | ADR-036: «ninguno exige un servidor dedicado a inteligencia artificial profesional» |
| Java (ADR-037) | Java 27 (no LTS) | Java 29 LTS | Salida de Java 29 |

### Escenarios «qué pasa si…» para preguntas abiertas

| Si… | Respuesta |
|---|---|
| …la finca triplica personal en temporada | Prueba de carga con 13 simuladores (TX-1.T 6); si no alcanza: subir hardware antes que separar servicios (SPK-04) |
| …se va internet una semana | Nada de TX-1 se detiene: todo está en el nodo. Solo se retrasan el respaldo (TX-2) y los avisos fuera de la red |
| …roban el servidor | El vault no abre sin el token USB; los respaldos en la nube están cifrados con la llave de la empresa |
| …llega una segunda finca de la misma empresa | ESC-52 y ESC-64: una sola instalación por empresa con estructura Empresa → Fincas; sin reinstalar |
| …el profesor pide quitar el WAF | `FLORLOGIC_WAF_MODO=DetectionOnly`, o un perfil de Compose sin WAF (F6) |
