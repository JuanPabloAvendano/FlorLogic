REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# Registro de riesgos de FlorLogic (vista de TX-1)

La guía pide, en «Gobierno arquitectónico», que los riesgos estén identificados. Hasta hoy estaban dispersos en los
ESC «parciales», las CN `EN DUDA` y los «riesgo aceptado» de las ESPEC (`IA_GUIA-IS2-checklist.md` §1.1 los marcaba
`VERIFICAR`). Este registro los junta.

**Escalas.** Probabilidad e impacto: **A** alta · **M** media · **B** baja. Exposición = la peor de las dos cuando una es
A. **Dueño:** J = Juan Pablo Avendaño Duque · JM = Jeronimo Montoya Londoño · Eq = los dos.
**Estado:** `ABIERTO` · `MITIGADO` (hay prueba) · `ACEPTADO` (decisión escrita) · `EN CURSO (Fx)`.

## 1 · Riesgos de confiabilidad (atributo nº 1 del README §1)

| ID | Riesgo | P | I | Mitigación | Evidencia | Dueño | ESC / ADR | Estado |
|---|---|---|---|---|---|---|---|---|
| R-01 | Una partición entra a medias (la base falla en mitad de la transacción) y el celular cree que quedó guardada | M | A | Toda la partición en una transacción; si falla, `500` genérico y no queda nada | `CanalParticionesTest.si_la_base_falla_a_mitad_es_500_generico_y_no_queda_nada_a_medias` | J | ESC-01, ESC-38 · ADR-002 | MITIGADO |
| R-02 | El celular reenvía la misma partición y se duplican anotaciones | A | A | Identificador único del teléfono y llave única en la base; F1 lee en lote lo ya guardado (`AnotacionRepositorio.guardadasCon`) y juzga por huella: misma huella = `sin_efecto`, otra = rechazada (ADR-027) | `CanalParticionesTest.reenviar_la_misma_particion_no_duplica` | J | ESC-01, ESC-34 · ADR-027 | MITIGADO |
| R-03 | Dos particiones del mismo celular llegan a la vez y el avance se corrompe | M | A | Versión optimista del avance; la que pierde recibe `409` con `Retry-After` y reintenta | `CanalParticionesTest.dos_particiones_del_mismo_celular_a_la_vez_la_que_pierde_es_409_y_al_reintentar_entra`, `AvanceRepositorioAdapterTest` | J | ESC-38 · ADR-027 | MITIGADO |
| R-04 | Mismo identificador de anotación con contenido distinto (huella distinta) pasa como reenvío | M | A | Huella canónica y los tres casos de idempotencia (ADR-027); puerto `CalculadorDeHuella` (CT-6) | Pendiente (F1) | J | ESC-34 · ADR-027 | EN CURSO (F1) |
| R-05 | Reloj del celular desviado ordena mal los hechos | A | M | Tres horas que no se mezclan (ADR-031) y marca de hora dudosa (ADR-014); `desfase_ms` al abrir sesión (CT-1) | Pendiente (F1) | J | ESC-17 · ADR-014, ADR-031 | EN CURSO (F1) |
| R-06 | La validación del servidor rechaza lo que el celular aceptó con la misma versión de reglas | M | A | Mismo motor guiado por datos en cliente y servidor (ADR-006), validar con la versión con que se capturó | Pendiente (F2) | J | ESC-57 · ADR-006, ADR-029 | ABIERTO |
| R-07 | Se pierde el teléfono con la jornada sin sincronizar | M | M | Riesgo aceptado: «si se pierde un teléfono se recaptura» (ADR-025) | Decisión ADR-025 | Eq | ESC-54 · ADR-025 | ACEPTADO |

## 2 · Riesgos de seguridad

| ID | Riesgo | P | I | Mitigación | Evidencia | Dueño | ESC / ADR | Estado |
|---|---|---|---|---|---|---|---|---|
| R-10 | Credenciales y particiones viajan en claro por el Wi-Fi de la finca | A | A | A-44 de `ESPEC-esqueleto-tx1.md`: «Todo el nodo funciona con HTTPS; ningún enlace en HTTP en claro»; CA de la finca (TEC-16) | Pendiente (F6). Hoy el gateway escucha HTTP en el 8080 | J | ESC-49, ESC-50 | ABIERTO · **el más grave hoy** |
| R-11 | Cualquiera en la red de la finca entrega particiones (no hay autenticación) | A | A | Keycloak (CT-2) y el backend como servidor de recursos que valida el JWT | Pendiente (F3). Hoy el nodo atiende un solo dispositivo configurado (`DispositivoDelNodo`, A-107) | J | ESC-22, ESC-49 · ADR-007 | ABIERTO |
| R-12 | Un ataque de inyección o un cuerpo raro llega al backend | M | A | WAF ModSecurity + OWASP CRS en modo `On`; consultas parametrizadas | `CanalPorGatewayTest.un_ataque_es_403_bloqueado_y_no_llega_al_backend` y otras 24 | JM | ESC-50 | MITIGADO |
| R-13 | Robo del servidor de la finca expone la clave de la base | B | A | OpenBao con sello `pkcs11` (token HSM USB que se conecta para abrir y se guarda, A-22) | `NodoCompletoTest`, `NodoArranqueSeguroTest` (con SoftHSM, no con el token real) | J | ESC-50 · ADR-012 | MITIGADO en dev · token USB sin probar (K-23) |
| R-14 | La credencial AppRole del backend no caduca | B | M | Riesgo aceptado en A-35: «acotada por la red y por la política de solo lectura»; `secret_id_bound_cidrs` a la subred del nodo | `NodoArranqueSeguroTest.a41_si_la_subred_guardada_no_es_la_del_nodo_para_antes_de_entrar` | J | ESC-50 | ACEPTADO |
| R-15 | Componente con CVE conocido llega a producción | M | A | Regla de Juan (29-oct): «in every tech must search for CVE's first»; imágenes por `sha256` | Revisión manual en OSV (`IA_TECNOLOGIAS-vigentes.md` TEC-33…37). Trivy en CI pendiente (F6) | Eq | — | PARCIAL |
| R-16 | Se cuela un secreto al repositorio | M | A | PR-06; secretos de Compose desde variables del anfitrión, sin defecto (A-10) | `compose.yaml` sin claves; sin escáner automático de secretos | Eq | — | PARCIAL |
| R-17 | El operador de la plataforma lee datos de negocio al dar soporte | B | A | Respaldos cifrados con la llave de cada empresa (ADR-012); telemetría sin datos de negocio (BB-13) | Pendiente (TX-2, F6) | J | ESC-30, ESC-50 · ADR-003, ADR-012 | ABIERTO |

## 3 · Riesgos de rendimiento y operación

| ID | Riesgo | P | I | Mitigación | Evidencia | Dueño | ESC / ADR | Estado |
|---|---|---|---|---|---|---|---|---|
| R-20 | La cola sobre PostgreSQL no sostiene el pico de temporada (+60 %) | B | A | Disparador SPK-04: «Si la sincronización de jornada supera 30 min … entra BB-09 como broker dedicado, o sube el hardware del nodo» | Sin medir. Prueba de carga con 13 simuladores pendiente (F5/F6) | Eq | ESC-38, ESC-61 · ADR-009 | ABIERTO |
| R-21 | Un fallo en producción no se puede diagnosticar a distancia | M | M | Logs JSON, métricas Prometheus, traza W3C desde el gateway | Hoy solo la traza: `CanalPorGatewayTest.el_trace_id_sale_del_traceparent`. Logs JSON y métricas pendientes (F6) | JM | ESC-30 · BB-13 | EN CURSO (F6) |
| R-22 | Sin pipeline, un cambio roto llega a la rama sin que nadie lo note | A | M | GitHub Actions (TEC-30) con `./mvnw verify`, JaCoCo, Trivy y SonarQube | No hay `.github/workflows` | JM | — | ABIERTO (F6) |
| R-23 | El token HSM USB de la finca se pierde y el vault queda irrecuperable | B | A | Segundo token bajo la custodia de ADR-012 (doble control, dos sitios) | Procedimiento sin escribir | J | ADR-012 | ABIERTO |
| R-24 | Java 27 no es LTS (A-1 de `IA_TECNOLOGIAS-vigentes.md`) y queda sin parches | A | M | Subir a Java 29 LTS cuando salga; construcción reproducible facilita el cambio | Decisión anotada | J | ADR-037 | ACEPTADO |
| R-25 | Spring Cloud Vault 5.0.3 es del tren de Boot 4.0, no 4.1 (excepción A-43) | M | M | `NodoCompletoTest` prueba que funciona; subir al tren 2026.0.0 cuando sea estable | `NodoCompletoTest` | J | — | ACEPTADO |

## 4 · Riesgos de proyecto y de proceso

| ID | Riesgo | P | I | Mitigación | Dueño | Estado |
|---|---|---|---|---|---|---|
| R-30 | El código lo escribe la IA y el equipo no sabe explicarlo en la sustentación | A | A | Ruta de estudio por tramos (`IA_RUTA-ESTUDIO-TX1.md`), matriz de dominio por integrante (`IA_GUIA-IS2-checklist.md` §6), modo docente | Eq | ABIERTO · la matriz está vacía |
| R-31 | Documentos IA y README dicen cosas distintas (CH-1…CH-7) | M | M | Regla: «todo lo que está en GitHub es humano y manda»; tabla de choques en la guía §9 | J | ABIERTO |
| R-32 | Tecnologías en uso sin `ACEPTADO` (TEC-31 WAF, TEC-32 Keycloak, TEC-37) | A | M | Filas `PROPUESTA` con la evidencia de CVE; Juan las acepta | J | ABIERTO |
| R-33 | TX-1 sigue `AJUSTAR` en `IA_00-LEEME-transacciones.md` mientras la copia construye todo | A | B | Copia sin git (CT-5); se integra solo lo que Juan acepte | J | ACEPTADO |

## 5 · Los cinco que se defienden primero

R-10 (TLS), R-11 (autenticación), R-22 (CI/CD), R-20 (carga sin medir) y R-30 (dominio del equipo). Los cuatro
primeros tienen fase asignada (F6, F3, F6, F5/F6); el quinto depende solo del equipo.
