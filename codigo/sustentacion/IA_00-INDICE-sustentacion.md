REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# Índice de la sustentación de IS II · FlorLogic · TX-1

Mapa de **cada ítem de la guía oficial** al documento de esta carpeta y a la evidencia en código o pruebas.

**Estados:** `HAY` (evidencia con prueba o documento) · `PARCIAL` · `PENDIENTE-Fx` (lo entrega la fase x de
`codigo/IA_CONSTRUCCION-TX1.md`: F1 ingesta completa, F2 configuración/reglas/ciclo, F3 identidad/dispositivos/bitácora,
F4 consulta + web, F5 notificación + trabajos, F6 plataforma: TLS, observabilidad, CI/CD, perfiles; F7 estos
documentos; F8 PWA) · `DEFENDER` (se decidió no adoptarlo; se sustenta con su ADR).

**Advertencias de esta pasada.**
- No se ejecutó `./mvnw verify`. Los nombres de clases y pruebas se comprobaron leyendo el código el 31-oct-2026; la
  fase F1 lo estaba cambiando al mismo tiempo. Una pasada posterior debe repetir la comprobación.
- Rutas de prueba: `CanalParticionesTest`, `CanalPorGatewayTest`, `CapasTest`, `ModulosTest` están en
  `codigo/backend-finca/src/test/java/co/florlogic/finca/…`; `NodoCompletoTest` y `NodoArranqueSeguroTest` en
  `codigo/pruebas-extremo-a-extremo/src/test/java/co/florlogic/pruebas/`. Las que usan Docker **se saltan sin Docker**.

## Documentos de esta carpeta

| Archivo | Para qué |
|---|---|
| `IA_C4-nivel1-contexto.md` / `.drawio` / `.png` | C4 nivel 1 (el README no lo tiene) |
| `IA_riesgos.md` | Registro de riesgos |
| `IA_trazabilidad.md` | ESC → ADR → TEC → clase → prueba |
| `IA_STRIDE-TX1.md` | Modelo de amenazas |
| `IA_OWASP-top10.md` | A01…A10 (2021) y diferencias con 2025 |
| `IA_zero-trust.md` | NIST SP 800-207 |
| `IA_12-factores.md`, `IA_well-architected.md`, `IA_manifiesto-reactivo.md`, `IA_CAF.md` | Cloud native |
| `IA_linea-base-DEFENDER.md` | Message Broker, Service Mesh, Data Mesh |
| `IA_calidad-de-codigo.md` | Clean Code, Clean Architecture, SOLID, KISS, YAGNI, DRY |
| `IA_ia-responsable.md` | IA responsable y validación humana |
| `IA_defensa-y-escenarios-futuros.md` | Guion por componente y escenarios futuros |

## 1 · Gobierno arquitectónico y ADR

| Ítem | Documento | Evidencia | Estado |
|---|---|---|---|
| ADR documentados | README §4 (ADR-001…037 con estado) | `ADR.xlsx` fuera de git | HAY |
| Trade-offs | README §1 (ranking de 13 atributos, 3 actores); `IA_linea-base-DEFENDER.md` | — | HAY |
| Trazabilidad | `IA_trazabilidad.md` | Comentarios `TEC-nn` y `A-n` en el código | PARCIAL: §2 de la matriz espera F1–F8 |
| Riesgos | `IA_riesgos.md` | — | HAY (documento) |

## 2 · Arquitectura y modelado

| Ítem | Documento | Evidencia | Estado |
|---|---|---|---|
| C4 nivel 1 | `IA_C4-nivel1-contexto.md` | `.drawio` + `.png` | HAY · falta llevarlo al README |
| C4 nivel 2 | README §5 (contenedores y despliegue) | `codigo/despliegue/compose.yaml` | HAY · Keycloak y ntfy no están dibujados (llegan en F3, F5) |
| Paquetes | README §7 | `CapasTest`, `ModulosTest`; `codigo/IA_DIAGRAMA-paquetes.drawio` | PARCIAL: el diagrama del README usa nombres anteriores al Segmento R (`RecibirParticionPuertoEntrada`, `AnotacionesRecibidasAdaptadorJdbc`) — CH-7 |
| Componentes | README §6 (C4 nivel 3) | — | HAY |
| Secuencia | README §8 (feliz y errores) | `CanalParticionesTest`, `CanalPorGatewayTest` | HAY · redibujar con la sesión cuando cierre F1 |

## 3 · Línea base técnica

| Ítem | Documento | Evidencia | Estado |
|---|---|---|---|
| WAF | `IA_STRIDE-TX1.md` §2.3, `IA_OWASP-top10.md` A03 | `despliegue/gateway/modsecurity-override.conf.template`, `CanalPorGatewayTest` (25) | HAY · TEC-31 `PROPUESTA` |
| Identity Provider | `IA_zero-trust.md` | Keycloak 26 (TEC-32, CT-2) | PENDIENTE-F3 |
| API Gateway | `IA_STRIDE-TX1.md` §2.3 | `despliegue/gateway/default.conf.template`, `NodoCompletoTest.el_gateway_es_la_unica_puerta_del_nodo` | HAY · TLS y `limit_req` PENDIENTE-F6 |
| Message Broker | `IA_linea-base-DEFENDER.md` §1 | ADR-009, BB-09; db-scheduler en F5 | DEFENDER |
| Service Mesh | `IA_linea-base-DEFENDER.md` §2 | ADR-001, ADR-017; `ModulosTest` | DEFENDER |
| Data Mesh | `IA_linea-base-DEFENDER.md` §3 | BB-17, ADR-010 | DEFENDER |
| Observabilidad | `IA_well-architected.md` §1 | Actuator; traza W3C (`CanalPorGatewayTest.el_trace_id_sale_del_traceparent`) | PARCIAL · PENDIENTE-F6 (logs JSON, Micrometer/Prometheus, Grafana) |
| CI/CD | `IA_12-factores.md` V | Sin `.github/workflows` | PENDIENTE-F6 |
| (extra) Gestión de secretos | `IA_zero-trust.md` §4 | `despliegue/openbao/`, `NodoArranqueSeguroTest` (6) | HAY |

## 4 · Calidad de código

| Ítem | Documento | Evidencia | Estado |
|---|---|---|---|
| Clean Code | `IA_calidad-de-codigo.md` §3 | Nombres, valores, `Optional` | HAY · SonarQube PENDIENTE-F6 |
| Clean Architecture | §1 | `CapasTest` (7 reglas), `ModulosTest` | HAY |
| SOLID | §2 (un ejemplo por principio) | `RecibirParticionInputAdapter` / `RecibirParticionEnsambladorEntrada`; `AnotacionRepositorioContratoTest` | HAY |
| KISS | §4 | ADR-009, ADR-017, TEC-04, TEC-07 | HAY |
| YAGNI | §5 | ADR-017; 13 módulos solo con `package-info` | HAY |
| DRY | §6 | `contrato/`, `CanalPorGatewayTest` lee la imagen de `compose.yaml` | HAY |

## 5 · Seguridad

| Ítem | Documento | Estado |
|---|---|---|
| OWASP Top 10 | `IA_OWASP-top10.md` | PARCIAL |
| Threat Modeling (STRIDE) | `IA_STRIDE-TX1.md` | HAY (documento) · mitigaciones F3/F6 |
| Zero Trust | `IA_zero-trust.md` | PARCIAL · PENDIENTE-F3/F6 |

### 5.1 · OWASP A01…A10 (2021)

| Riesgo | Evidencia principal | Estado |
|---|---|---|
| A01 Broken Access Control | Una sola ruta en el gateway; `CanalPorGatewayTest.una_ruta_ajena_es_404_rechazado_y_no_llega_al_backend`; roles en F3 | PARCIAL · PENDIENTE-F3 |
| A02 Cryptographic Failures | OpenBao con sello `pkcs11`; TLS en F6 | PARCIAL · PENDIENTE-F6 |
| A03 Injection | CRS `On` + `JdbcClient` parametrizado; `CanalPorGatewayTest.un_ataque_es_403_bloqueado_y_no_llega_al_backend` | HAY |
| A04 Insecure Design | `IA_STRIDE-TX1.md`; atomicidad e idempotencia (`CanalParticionesTest`) | PARCIAL |
| A05 Security Misconfiguration | Puertos cerrados, `500` genérico, `fail-fast` | HAY |
| A06 Vulnerable Components | Revisión de CVE manual, digests; Trivy en F6 | PARCIAL · PENDIENTE-F6 |
| A07 Authentication Failures | Keycloak + MFA | PENDIENTE-F3 |
| A08 Integrity Failures | Construcción reproducible, `sha256`; firma de configuración en F2 | PARCIAL |
| A09 Logging & Monitoring | Traza y registro del WAF; logs JSON y alertas en F5/F6 | PARCIAL · PENDIENTE-F6 |
| A10 SSRF | Destinos solo de configuración | HAY (por diseño) |

## 6 · DevSecOps

| Ítem | Documento | Evidencia | Estado |
|---|---|---|---|
| Pruebas | `IA_trazabilidad.md` | JUnit 6, AssertJ, Testcontainers, ArchUnit; según `IA_CONSTRUCCION-TX1.md`, 171/171 con Docker al tomar la copia (no se re-ejecutó) | HAY |
| Cobertura | — | Sin JaCoCo en `codigo/pom.xml` | PENDIENTE-F6 |
| SonarQube | — | — | PENDIENTE-F6 |
| Trivy | — | Revisión manual en OSV | PENDIENTE-F6 |

## 7 · Cloud native

| Ítem | Documento | Estado |
|---|---|---|
| Reactive Manifesto | `IA_manifiesto-reactivo.md` | PARCIAL |
| 12 Factor App | `IA_12-factores.md` | PARCIAL |
| CAF | `IA_CAF.md` | HAY (documento) |
| Well-Architected | `IA_well-architected.md` | PARCIAL |

### 7.1 · Manifiesto reactivo

| Propiedad | Evidencia | Estado |
|---|---|---|
| Responsive | Presupuesto de tiempos del gateway (2 s + 2 s + 25 s < 30 s); medición en F6 | PARCIAL · PENDIENTE-F6 |
| Resilient | `NodoArranqueSeguroTest.c5_…`, `CanalPorGatewayTest.con_el_backend_apagado_es_502_no_disponible`; Toxiproxy en F6 | HAY · PENDIENTE-F6 (corte de red) |
| Elastic | Hilos virtuales; prueba de 13 simuladores | PENDIENTE-F5/F6 |
| Message Driven | Partición idempotente con acuse; `409`/`413` como contrapresión; eventos y cola en F5 | PARCIAL · PENDIENTE-F5 |

### 7.2 · Twelve-Factor

| Factor | Estado | Factor | Estado |
|---|---|---|---|
| I Codebase | HAY | VII Port binding | HAY |
| II Dependencies | HAY | VIII Concurrency | PARCIAL |
| III Config | PARCIAL | IX Disposability | PARCIAL · PENDIENTE-F6 |
| IV Backing services | HAY | X Dev/prod parity | PARCIAL |
| V Build, release, run | PARCIAL · PENDIENTE-F6 | XI Logs | PARCIAL · PENDIENTE-F6 |
| VI Processes | HAY | XII Admin processes | HAY |

### 7.3 · Well-Architected

| Pilar | Estado |
|---|---|
| Excelencia operativa | PARCIAL · PENDIENTE-F6 |
| Seguridad | PARCIAL · PENDIENTE-F3/F6 |
| Confiabilidad | HAY (TX-1) |
| Eficiencia de rendimiento | PARCIAL · PENDIENTE-F5/F6 (carga) |
| Optimización de costos | HAY (ADR) |
| Sostenibilidad | PARCIAL |

## 8 · IA responsable

| Ítem | Documento | Estado |
|---|---|---|
| Uso responsable | `IA_ia-responsable.md` §2 | HAY |
| Validación humana | `IA_ia-responsable.md` §4 | HAY |

## 9 · Sustentación

| Ítem | Documento | Estado |
|---|---|---|
| Defensa técnica | `IA_defensa-y-escenarios-futuros.md` parte A | PARCIAL: la matriz de dominio por integrante (`IA_GUIA-IS2-checklist.md` §6) está vacía |
| Escenarios futuros | `IA_defensa-y-escenarios-futuros.md` parte B | HAY |

## 10 · Checklist maestro antes de la defensa

| Ítem | Estado | Qué falta |
|---|---|---|
| ADR completos | HAY | Juan: actualizar ADR-007 (Keycloak) y BB-08 (WAF en el nodo) en el README |
| Diagramas actualizados | PARCIAL | C4 N1 al README; N2 con Keycloak, OpenBao y ntfy; paquetes tras el Segmento R (CH-7); secuencia con sesión (F1) |
| Building blocks implementados | PARCIAL | IdP (F3), TLS (F6), observabilidad (F6), CI/CD (F6); broker, malla y data mesh: DEFENDER |
| OWASP evaluado | HAY (documento) | Cerrar A01, A02, A07 con F3 y F6 |
| Pruebas ejecutadas | PARCIAL | Volver a correr `./mvnw verify` con Docker al cerrar cada fase |
| Observabilidad implementada | PENDIENTE-F6 | Logs JSON, Micrometer/Prometheus, Grafana (perfil de Compose) |
| CI/CD funcionando | PENDIENTE-F6 | `.github/workflows` con verify, JaCoCo, Trivy, SonarQube |
| Well-Architected evaluado | HAY (documento) | — |
| 12 factores revisados | HAY (documento) | — |
