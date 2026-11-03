REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# Línea base técnica: los building blocks que se decidió NO adoptar

La guía pide evidencia de WAF, Identity Provider, API Gateway, **Message Broker**, **Service Mesh**, **Data Mesh**,
Observabilidad y CI/CD. Tres de ellos FlorLogic los **evaluó y no los adopta**. Juan, 28-oct-2026: se sustentan con
su ADR, sin demostración. Cada sección dice qué se eligió en su lugar, el trade-off y el disparador para reconsiderar.

Citas: el README de `documentacion-desarrollo` §4 (resumen de cada ADR, que manda) y, como antecedente,
`docs/03-arquitectura/FlorLogic-alternativa-de-solucion-y-ADR.md` (tabla de building blocks BB-nn y el texto largo de
cada ADR; si choca con el README, manda el README).

Los otros cinco **sí** se adoptan: WAF (TEC-31, hay), API Gateway (TEC-15, hay), Identity Provider (Keycloak, TEC-32,
F3), Observabilidad (TEC-17, parcial, F6) y CI/CD (TEC-30, F6). Ver `IA_00-INDICE-sustentacion.md`.

---

## 1 · Message Broker → cola sobre PostgreSQL (ADR-009, BB-09)

**Lo que dice el ADR (README §4, ADR-009, estado Propuesta):** «La fila de trabajos pendientes vive dentro de la misma
base de datos, sin instalar una pieza aparte para eso. Se adopta una herramienta dedicada únicamente si la medición
demuestra que la base no sostiene el pico de una …» (el README corta ahí).

**BB-09 (antecedente):** «Cola sobre PostgreSQL (`SELECT … FOR UPDATE SKIP LOCK`) · Broker dedicado solo si `SPK-04`
lo exige · Con 3 dispositivos y una jornada de registros, un broker es infraestructura que hay que operar sin carga
que lo justifique».

**Qué se usa en su lugar.**
- Entre el celular y el nodo: el **almacén de salida del celular** (outbox, ADR-002) y particiones idempotentes. Ese
  es el «mensaje» de TX-1 y ya funciona (`CanalParticionesTest`).
- Dentro del nodo: **db-scheduler** (TEC-09, aceptado), una cola de trabajos en una tabla de PostgreSQL, y los
  **eventos de Spring Modulith** con su tabla `event_publication`, que garantiza que un evento no se pierde si el
  proceso muere después del commit (`IA_PRIORIDAD-componentes-TX1.md` §2). Entra en F5 con `notificacion` y `trabajos`.

**Trade-off.**

| Gana | Pierde |
|---|---|
| Una pieza menos que instalar, respaldar, monitorear y actualizar **en cada finca** | Throughput máximo menor que Kafka o RabbitMQ |
| El trabajo y el dato se guardan en la **misma transacción** (no hace falta outbox entre base y broker) | Sin fan-out a muchos consumidores ni reproducción de flujos |
| El respaldo de la base (TX-2) respalda también la cola | La cola compite por la misma base en el pico |

**Disparador para reconsiderar (SPK-04):** «Si la sincronización de jornada supera 30 min o la proyección supera 1 h
bajo carga sintética de +60 % → entra BB-09 como broker dedicado, o sube el hardware del nodo». Alternativa a escala:
RabbitMQ 4 (cola de trabajo) antes que Kafka (registro de eventos).

**Evidencia:** decisión en ADR-009; cola en código pendiente (F5); medición pendiente (prueba de carga, F5/F6).

---

## 2 · Service Mesh → monolito modular (ADR-001, ADR-017)

**Lo que dicen los ADR (README §4).**
- ADR-001, Aceptada: «Evitar la tendencia de desarrollo que presenta al modelo de negocio SaaS como la tendencia
  necesaria para el éxito, se utilizará una forma de entrega que permita el Offline-First por restricciones técnicas
  del lugar de …».
- ADR-017, Aceptada, «Lo que deliberadamente no se construye»: «Queda fuera de la primera entrega, cada cosa con la
  condición escrita que la reabre: separar el sistema en servicios independientes, una bodega de datos aparte, una
  pieza dedicada para el trabajo en segundo plano, una …».
- Tabla de ADR-017 (antecedente): «Microservicios, service mesh · Se reabre cuando haya más de un equipo de dominio».

**Qué se usa en su lugar.** Un **monolito modular** con Spring Modulith (TEC-05): 17 módulos en un proceso, cada uno
un hexágono, con límites verificados por pruebas:
- `ModulosTest.losModulosRespetanSusLimites` (un módulo solo usa la API pública de otro).
- `CapasTest` (7 reglas ArchUnit dentro de cada hexágono).

Las funciones que da una malla se cubren sin ella:

| Lo que da un service mesh | Cómo lo cubre FlorLogic |
|---|---|
| mTLS entre servicios | Los módulos se llaman **dentro del proceso**, no hay red entre ellos. Entre contenedores: TLS en todo el nodo (A-44, F6) |
| Reintentos, timeouts | El gateway: `proxy_connect_timeout 2s`, `proxy_read_timeout 25s`, nunca reintenta un POST |
| Trazas distribuidas | Traza W3C desde el gateway (`CanalPorGatewayTest.el_trace_id_sale_del_traceparent`) |
| Políticas de tráfico | Una sola ruta en el gateway |

**Trade-off.**

| Gana | Pierde |
|---|---|
| Sin sidecars, sin plano de control, sin Kubernetes: cabe en un servidor de oficina | No se puede desplegar ni escalar un módulo por separado |
| Llamadas entre módulos en memoria y transacciones locales | Un fallo grave del proceso tumba todos los módulos (mitigado con `restart`) |
| Dos personas pueden entender y operar todo | — |

**Disparador para reconsiderar:** más de un equipo de dominio, o que un módulo (p. ej. `proyeccion`) necesite escalar
aparte (ESC-61). Como los límites ya están verificados, extraer un módulo a un servicio es un cambio acotado. Solo con
varios servicios en Kubernetes tendría sentido Linkerd o Istio.

**Evidencia:** `ModulosTest`, `CapasTest`; `codigo/backend-finca/src/main/java/co/florlogic/finca/` (17 paquetes).

---

## 3 · Data Mesh → modelos de lectura en la misma base (BB-17, ADR-010)

**Lo que dice el ADR (README §4, ADR-010, Propuesta):** «Construir tablas de consulta derivadas de esos hechos, dentro
de la misma base de la empresa, que se actualizan después de cada sincronización. Sin bodega de datos aparte. Esas
mismas tablas son las que se le exponen a …».

**BB-17 (antecedente):** «NO SE ADOPTA · Reevaluar solo si aparecen equipos de dominio separados · Data mesh es una
respuesta organizacional a muchos dominios y muchos equipos. Aquí hay **un** dominio y **dos** personas. Adoptarlo
sería el desperdicio arquetípico que este documento evita».

**Qué se usa en su lugar.** Tablas de consulta (modelos de lectura) en PostgreSQL, refrescadas tras cada
sincronización; las mismas que se exponen a herramientas externas por ADR-013 («Interoperabilidad por exportación y
por lectura directa»). En TX-1 lo construye `consulta` (F4): lo esperado, lo recibido y lo que falta.

**Trade-off.**

| Gana | Pierde |
|---|---|
| Un solo lugar de verdad y un solo aislamiento por empresa (cada empresa su base, ADR-003) | No hay productos de datos con dueño propio ni catálogo federado |
| Sin ETL ni bodega que operar | Consultas analíticas pesadas compiten con la operación (ADR-034 propone caché si hace falta) |

**Disparador para reconsiderar:** «Data mesh, almacén analítico separado (BB-17) · Se reabre cuando el BI de una
empresa no quepa en su base con índices» (tabla de ADR-017, antecedente), o cuando aparezcan equipos de dominio
separados (BB-17).

**Evidencia:** decisión en ADR-010 y BB-17; código pendiente (F4).

---

## 4 · Preguntas que puede hacer el jurado

| Pregunta | Respuesta corta |
|---|---|
| «¿No es un monolito un antipatrón?» | Un monolito **sin** límites lo es. Este tiene 17 módulos con límites que una prueba hace cumplir; separarlo en servicios no daría nada a una finca con un servidor y dos desarrolladores |
| «¿Y si mañana tienen 100 fincas?» | Cada finca es su propia instalación (ADR-003): 100 fincas son 100 nodos pequeños, no un sistema 100 veces más grande. Lo que se comparte (servicios en línea) sí puede crecer aparte |
| «¿Cómo saben cuándo cambiar?» | Cada no-adopción tiene un disparador medible escrito (SPK-04, más de un equipo, BI que no cabe) |
