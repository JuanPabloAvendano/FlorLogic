REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# AWS Well-Architected: los seis pilares aplicados a FlorLogic

Fuente: «The AWS Well-Architected Framework is based on six pillars — operational excellence, security, reliability,
performance efficiency, cost optimization, and sustainability» (AWS Well-Architected Framework (seis pilares) · p. 7,
https://docs.aws.amazon.com/pdfs/wellarchitected/latest/framework/wellarchitected-framework.pdf#page=7).

**Matiz.** El marco está escrito para cargas en AWS. FlorLogic corre en un servidor de la finca; sus preguntas valen
igual porque son de diseño, no de proveedor. Se evalúa el nodo de finca con TX-1.

**Estados:** `HAY` · `PARCIAL` · `PENDIENTE-Fx`.

## Resumen

| Pilar | Estado | Puntuación del README §1 que lo mueve |
|---|---|---|
| Excelencia operativa | PARCIAL · PENDIENTE-F6 | Capacidad para ser administrado (23), soportado (27) |
| Seguridad | PARCIAL · PENDIENTE-F3/F6 | Seguridad (24) |
| Confiabilidad | HAY en TX-1 | **Confiabilidad (3), el nº 1**; disponibilidad (11) |
| Eficiencia de rendimiento | PARCIAL | Rendimiento (17) |
| Optimización de costos | HAY (sustentado en ADR) | — (restricción CN-35) |
| Sostenibilidad | PARCIAL | — |

## 1 · Excelencia operativa

| Pregunta | En FlorLogic | Evidencia | Estado |
|---|---|---|---|
| ¿La infraestructura es código? | `compose.yaml`, `Dockerfile`, plantillas del gateway, `openbao.hcl` | `codigo/despliegue/` | HAY |
| ¿Se despliega con cambios pequeños y reversibles? | Pipeline que construye y prueba en cada empuje | No hay `.github/workflows` | PENDIENTE-F6 |
| ¿Se entiende el estado en operación? | Salud de cada contenedor (`healthcheck`), traza W3C | `compose.yaml`; `CanalPorGatewayTest.el_trace_id_sale_del_traceparent` | PARCIAL · métricas y logs JSON en F6 |
| ¿Hay procedimientos (runbooks)? | `codigo/IA_LEEME.md` (construir, levantar, apagar), `scripts/IA_probar-canal.sh` | archivos | PARCIAL: falta runbook de la finca (token USB, restaurar) |
| ¿El equipo puede operar sin el agente? | Matriz de dominio por integrante | `IA_GUIA-IS2-checklist.md` §6 (vacía) | FALTA |

## 2 · Seguridad

Detalle en `IA_STRIDE-TX1.md`, `IA_OWASP-top10.md` e `IA_zero-trust.md`.

| Pregunta | En FlorLogic | Estado |
|---|---|---|
| Identidad fuerte, mínimo privilegio | Keycloak + MFA (F3); política de solo lectura en OpenBao (hay) | PARCIAL |
| Detección | Registro del WAF con traza (hay); alertas (F5, F6) | PARCIAL |
| Protección de la infraestructura | Una sola puerta, WAF `On`, contenedores sin root (hay) | HAY |
| Protección de datos | Secretos con sello HSM (hay); TLS (F6); respaldo cifrado (TX-2) | PARCIAL |
| Respuesta a incidentes | — | PENDIENTE |

## 3 · Confiabilidad (la prioridad del proyecto)

| Pregunta | En FlorLogic | Evidencia | Estado |
|---|---|---|---|
| ¿Se recupera solo de fallos? | `restart: unless-stopped`; backend `fail-fast` sin vault; gateway arranca sin backend y da `502` | `NodoArranqueSeguroTest`, `CanalPorGatewayTest.con_el_backend_apagado_es_502_no_disponible` | HAY |
| ¿Los cambios son atómicos e idempotentes? | Partición en una transacción; llave única por identificador del teléfono; `409` con reintento | `CanalParticionesTest` (8) | HAY |
| ¿Funciona si se cae la red externa? | Local-first: la nube nunca está en el camino crítico (README §5, N4) | Diseño (ADR-001) | HAY |
| ¿Funciona si se cae el Wi-Fi a mitad? | El celular conserva hasta que el servidor confirma (ADR-002); `confirmado_hasta` | `CanalParticionesTest.confirmado_hasta_es_el_avance_real_del_celular`; Toxiproxy pendiente (F6) | PARCIAL |
| ¿Hay respaldo probado? | TX-2 (pg_dump + restic + S3) | — | Fuera de TX-1 |

## 4 · Eficiencia de rendimiento

| Pregunta | En FlorLogic | Estado |
|---|---|---|
| ¿Se eligió el recurso adecuado? | Hilos virtuales (TEC-04) para E/S; PostgreSQL con `jsonb`; sin broker ni caché mientras no se mida | HAY (decisión) |
| ¿Se mide? | ESC-38 (jornada ≤ 30 min), ESC-61 (degradación ≤ 20 %) sin medir | PENDIENTE-F5/F6: prueba de carga con 13 simuladores (TX-1.T 6) y SPK-04 |
| ¿Se sabe cuándo cambiar? | Disparadores escritos: SPK-04 para el broker, ADR-034 para la caché | HAY |

## 5 · Optimización de costos

- Una instalación por empresa, sin broker, sin malla, sin Kubernetes (BB-14: «Kubernetes con dos personas es costo de
  operación sin beneficio medible»).
- Sin IdP que cobre por usuario (BB-05: «un IdP gestionado que cobra por usuario activo rompe el margen»): Keycloak
  autohospedado (TEC-32).
- La nube solo para respaldo y versiones; almacenamiento con bloqueo de retención y deduplicación (restic).
- **Estado:** HAY, sustentado en ADR-001, ADR-009, ADR-017.

## 6 · Sostenibilidad

- Un nodo pequeño por finca, sin hardware de IA (ADR-036: «ninguno exige un servidor dedicado a inteligencia
  artificial profesional»).
- Sin réplicas ociosas: un proceso de backend con hilos virtuales.
- Imágenes Alpine y JRE (no JDK) en ejecución.
- **Falta:** medir consumo del nodo en reposo y en pico (F6).

## Acciones que cierra la construcción

| Acción | Pilar | Fase |
|---|---|---|
| Pipeline GitHub Actions con `./mvnw verify`, JaCoCo, Trivy, SonarQube | Excelencia operativa, Seguridad | F6 |
| Logs JSON, Micrometer + Prometheus, perfil de Compose `observabilidad` | Excelencia operativa | F6 |
| TLS en todo el nodo | Seguridad | F6 |
| Keycloak con MFA | Seguridad | F3 |
| Prueba de carga y Toxiproxy | Rendimiento, Confiabilidad | F5/F6 |
