REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# OWASP Top 10 (2021) aplicado a TX-1

La guía nombra la edición **2021** y pide, por cada riesgo, «evidencia y pruebas implementadas». Se sustenta con esa
edición y al final se dice qué cambia en la **2025** (la vigente). Fuentes: OWASP Top 10:2021 · secc. 6
(https://top10.owasp.org/2021/A00_2021_Introduction/: «A01:2021-Broken Access Control moves up from the fifth
position») y OWASP Top 10:2025 · secc. 2 (https://top10.owasp.org/2025/0x00_2025-Introduction/).

**Estados:** `HAY` (prueba automática) · `PARCIAL` · `PENDIENTE-Fx` (fase de `codigo/IA_CONSTRUCCION-TX1.md`).

## Resumen

| Riesgo 2021 | Estado | Evidencia más fuerte |
|---|---|---|
| A01 Broken Access Control | PARCIAL · PENDIENTE-F3 | Una sola ruta pasa el gateway; `CapasTest` |
| A02 Cryptographic Failures | PARCIAL · PENDIENTE-F6 | OpenBao con sello HSM; falta TLS |
| A03 Injection | HAY | WAF CRS + `JdbcClient` parametrizado |
| A04 Insecure Design | PARCIAL | STRIDE, ESPEC con decisiones, idempotencia por diseño |
| A05 Security Misconfiguration | HAY | Puertos cerrados, errores sin detalle, `fail-fast` |
| A06 Vulnerable and Outdated Components | PARCIAL · PENDIENTE-F6 | Revisión de CVE manual, imágenes por `sha256`; falta Trivy |
| A07 Identification and Authentication Failures | PENDIENTE-F3 | Keycloak con MFA (CT-2) |
| A08 Software and Data Integrity Failures | PARCIAL | Construcción reproducible, digests; falta firma |
| A09 Security Logging and Monitoring Failures | PARCIAL · PENDIENTE-F6 | Traza W3C y registro del WAF |
| A10 SSRF | HAY (por diseño) | El nodo no hace peticiones a URL que venga del cliente |

## A01 · Broken Access Control

- **Qué se revisa:** que cada petición solo pueda hacer lo que su rol permite y que no haya rutas abiertas de más.
- **Hay hoy:**
  - El gateway deja pasar **una sola ruta** (`location ~ "^/ingesta/v1/sesiones/[^/;]+/particiones$"`) y solo `POST`;
    todo lo demás, incluido `/actuator`, es `404` (`codigo/despliegue/gateway/default.conf.template`).
    Pruebas: `CanalPorGatewayTest.una_ruta_ajena_es_404_rechazado_y_no_llega_al_backend`,
    `…otro_metodo_es_405_con_allow_post`.
  - El backend no publica puerto: `NodoCompletoTest.el_gateway_es_la_unica_puerta_del_nodo`.
  - Control de acceso **en el código** entre capas: `CapasTest` y `ModulosTest` (un módulo solo usa la API pública
    de otro).
- **Falta (F3):** roles `operador`, `gerente`, `administrador` en Keycloak; el backend como servidor de recursos que
  verifica JWT y rol en cada petición; prueba «rol equivocado → 403» y «permiso retirado → rechazo» (ESC-22).

## A02 · Cryptographic Failures

- **Hay hoy:**
  - Secretos fuera del código: la clave de PostgreSQL vive en OpenBao, que se abre con el sello `pkcs11` (SoftHSM
    en desarrollo y CI, token HSM USB en la finca, A-22). Pruebas: `NodoCompletoTest`, `NodoArranqueSeguroTest`.
  - El secreto cero entra como Compose secret (`/run/secrets/…`), no como variable visible (A-10).
- **Falta (F6):** TLS en todos los enlaces (A-44: «Todo el nodo funciona con HTTPS; ningún enlace en HTTP en claro»)
  con la CA de la finca (TEC-16); cifrado del almacén del celular (F8); respaldo cifrado con la llave de la empresa
  (TX-2, ADR-012).

## A03 · Injection

- **Hay hoy:**
  - SQL: `AnotacionRepositorioAdapter` y `AvanceRepositorioAdapter` usan `JdbcClient` con parámetros con nombre
    (`.param("uuid", …)`); ninguna cadena del cliente se concatena al SQL.
  - El WAF (OWASP CRS 4.30.0 en modo `On`) bloquea inyecciones antes del backend:
    `CanalPorGatewayTest.un_ataque_es_403_bloqueado_y_no_llega_al_backend`,
    `…un_ataque_en_la_ultima_anotacion_de_casi_1_mib_es_403`, y las evasiones por codificación
    `…un_cuerpo_que_no_es_utf8_simple_es_400_y_no_llega_al_backend`.
  - El contenido se guarda como `jsonb` con `CAST(:contenido AS jsonb)`: entra como dato, no como SQL.
- **Falta:** una prueba que mande `'; DROP TABLE …` **directo al backend** (sin WAF, `DetectionOnly`) y compruebe que
  queda guardado como texto. Sugerida en F6.

## A04 · Insecure Design

- **Hay hoy:** modelo de amenazas (`IA_STRIDE-TX1.md`); la confiabilidad es el atributo nº 1 (README §1) y el diseño
  la hace estructural: transacción por partición, idempotencia por identificador del teléfono (ADR-027), `409` con
  reintento ante choque (A-98). Pruebas de `CanalParticionesTest` (8). Las decisiones de diseño pasan por ESPEC con
  rondas de preguntas y verificadores.
- **Falta:** casos de abuso como pruebas (p. ej. una sesión ajena entregando a otra sesión, F1/F3).

## A05 · Security Misconfiguration

- **Hay hoy:**
  - Base solo en `127.0.0.1`; backend y vault sin puerto publicado (`compose.yaml`).
  - `server_tokens off`; todos los errores del gateway en `problem+json` sin detalles
    (`CanalPorGatewayTest.una_ruta_demasiado_larga_es_414_en_problem_json`).
  - `ManejadorDeErrores` responde `500` genérico con `Cache-Control: no-store`
    (`CanalParticionesTest.si_la_base_falla_a_mitad_es_500_generico_y_no_queda_nada_a_medias`).
  - Sin vault el backend no arranca (`NodoArranqueSeguroTest.c5_sin_vault_el_backend_reiniciado_no_queda_escuchando`).
  - Contenedores sin privilegios: WAF en el 8080 sin root, backend con usuario 10001.
- **Falta (F6):** cabeceras de seguridad (HSTS cuando haya TLS, `X-Content-Type-Options`) y escaneo de configuración
  con Trivy (`trivy config`).

## A06 · Vulnerable and Outdated Components

- **Hay hoy:** regla de Juan (29-oct): «in every tech must search for CVE's first». Ejemplo: OpenBao subió de 2.7.1
  a 2.7.2 por GHSA-736f-82w7-hj22 («ACL bypass en AppRole», que se usa). Imágenes fijadas por `sha256`
  (`postgres:16@sha256:1beaa…`, `eclipse-temurin:27-jre@sha256:0dd04…`).
- **Falta (F6):** Trivy en el pipeline (imágenes y `pom.xml`), con fallo en CVE alta o crítica; Dependabot o Renovate.

## A07 · Identification and Authentication Failures

- **Hay hoy:** nada de autenticación de usuarios; el nodo atiende un dispositivo configurado (A-107).
- **Falta (F3):** Keycloak 26 (TEC-32): PKCE para el celular, cliente confidencial para la web, **MFA TOTP obligatorio**
  para gerente y administrador (CT-2), bloqueo por fuerza bruta, cierre por inactividad (ESC-28). Pregunta abierta: la
  gracia offline (P-N01, CN-23) que ADR-007 cubría con una credencial de 24 h.

## A08 · Software and Data Integrity Failures

- **Hay hoy:** construcción reproducible (`outputTimestamp` fijo, TEC-03); imágenes y el complemento `pkcs11`
  fijados por `sha256` (TEC-35); Maven Wrapper con versión fija. Integridad del dato: idempotencia y versión optimista.
- **Falta:** firma del paquete de configuración (ADR-029, F2), comprobación de la firma `.sig` del complemento
  (Segmento 7), huella de anotación (F1), SBOM en el pipeline (F6).

## A09 · Security Logging and Monitoring Failures

- **Hay hoy:** la traza W3C del gateway llega a la respuesta y al registro del WAF, sin `Authorization`
  (`CanalPorGatewayTest.el_trace_id_sale_del_traceparent`, `…el_registro_del_waf_lleva_la_traza_y_la_regla_sin_authorization`);
  `DetectionOnly` deja anotado lo que no bloquea (`…en_deteccion_el_ataque_llega_al_backend_y_queda_anotado`).
- **Falta:** logs JSON del backend, métricas Prometheus y alertas (F6); bitácora de solo inserción (F3, ESC-40);
  aviso al administrador (F5).

## A10 · Server-Side Request Forgery

- **Sustentación:** en TX-1 el backend solo habla con destinos **fijos de configuración** (PostgreSQL, OpenBao,
  Keycloak y ntfy desde F5), nunca con una URL que mande el cliente. El gateway resuelve solo `FLORLOGIC_BACKEND`.
- **Falta:** cuando entre ntfy (F5), el tema y la URL salen de configuración; una prueba que confirme que un campo del
  cliente no cambia el destino.

## Qué cambia en la edición 2025

| 2025 | Qué pasa con lo de arriba |
|---|---|
| A01 Broken Access Control | Absorbe **SSRF** (A10:2021). Nuestra sustentación de A10 pasa aquí |
| A02 Security Misconfiguration | Sube del 5.º al 2.º puesto: refuerza lo de A05 |
| A03 **Software Supply Chain Failures** | Nueva y más amplia que A06:2021: además de componentes con CVE, pide integridad de la cadena (digests, SBOM, firma). Los `sha256` fijos y la revisión de CVE caen aquí |
| A04 Cryptographic Failures, A05 Injection, A06 Insecure Design | Bajan de puesto; mismo contenido |
| A07 Authentication Failures, A08 Software or Data Integrity Failures | Renombradas |
| A09 Security Logging **& Alerting** Failures | Pone el acento en alertar, no solo registrar: ntfy (F5) |
| A10 **Mishandling of Exceptional Conditions** | Nueva. Encaja con nuestro código defensivo: «ningún punto de entrada lanza» (lector tolerante que devuelve `Optional` vacío, `ManejadorDeErrores`, `500` genérico) |
