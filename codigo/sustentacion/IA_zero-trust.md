REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# Zero Trust en el nodo de finca (NIST SP 800-207)

## 1 · La idea en una frase

«Network location alone does not imply trust» (NIST SP 800-207 · Zero Trust Architecture · p. 15,
https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-207.pdf#page=15). En FlorLogic eso quiere decir:
**estar en el Wi-Fi de la finca o dentro de la red de Docker no da permiso de nada**. Cada pieza verifica a quien le
habla, aunque venga del gateway.

¿Por qué importa en una finca? Porque el Wi-Fi de la oficina lo comparten trabajadores, visitantes y equipos sin
administrar, y el servidor está en una oficina, no en un centro de datos.

## 2 · Los siete principios del NIST aplicados

| # | Principio (NIST SP 800-207 §2.1) | En el nodo de finca | Evidencia | Estado |
|---|---|---|---|---|
| 1 | Todas las fuentes de datos y servicios son recursos | Backend, PostgreSQL, OpenBao, Keycloak y ntfy son recursos que se protegen uno por uno, no «la red» | Cada uno con su credencial: AppRole para OpenBao, usuario de base desde el vault | HAY |
| 2 | Toda comunicación se asegura, sin importar dónde esté | A-44: «Todo el nodo funciona con HTTPS; ningún enlace en HTTP en claro». Vale también para gateway → backend, backend → OpenBao y `openbao-init` → OpenBao | **Pendiente (F6).** Hoy los enlaces internos van por HTTP en la red de Docker | FALTA |
| 3 | El acceso se concede por sesión | Sesión de sincronización que se abre y se cierra (CT-1); tokens de Keycloak de vida corta con refresh token (CT-2); token del backend en OpenBao de 1 h renovable, máximo 24 h (A-35) | Token de OpenBao: `NodoCompletoTest`. Sesión: pendiente (F1). Keycloak: pendiente (F3) | PARCIAL |
| 4 | El acceso lo decide una política dinámica | Roles del realm `florlogic`; un dispositivo desconocido entra marcado; el permiso retirado cuenta en la siguiente acción (ESC-22) | Pendiente (F3) | FALTA |
| 5 | Se vigila la integridad y la postura de los activos | Imágenes por `sha256`; revisión de CVE antes de aceptar; Trivy en el pipeline | Digests en `compose.yaml` y `backend-finca/Dockerfile`. Trivy: pendiente (F6) | PARCIAL |
| 6 | Autenticación y autorización dinámicas y estrictas **antes** de cada acceso | **El backend verifica el JWT aunque la petición venga del gateway** (decisión de `IA_PRIORIDAD-componentes-TX1.md` §2: «La firma de la credencial la verifica el backend … no Nginx: es una regla de dominio y tiene que poder probarse sin el gateway»). El WAF no toca `Authorization` (regla CRS excluida) | Exclusión: `CanalPorGatewayTest.authorization_no_se_revisa_ni_va_al_registro`. Verificación JWT: pendiente (F3) | PARCIAL |
| 7 | Se recoge información del estado para mejorar la seguridad | Registro del WAF con traza y regla; logs JSON y métricas; aviso al administrador | Registro del WAF: `CanalPorGatewayTest.el_registro_del_waf_lleva_la_traza_y_la_regla_sin_authorization`. Resto: pendiente (F5, F6) | PARCIAL |

## 3 · Los componentes de la arquitectura NIST en el nodo

NIST separa el **punto de decisión** (PDP: quién decide) del **punto de aplicación** (PEP: quién bloquea).

| Componente NIST | En FlorLogic |
|---|---|
| Policy Engine / Policy Administrator (PDP) | Keycloak (emite tokens con roles) y OpenBao (políticas de secretos). Pendiente F3 para Keycloak |
| Policy Enforcement Point (PEP) | **Dos capas:** el gateway con WAF (filtra forma y ataques) y el backend con Spring Security (verifica identidad y rol). El backend es el PEP que importa: el gateway puede fallar o saltarse |
| Fuentes de datos de la política | Registro de dispositivos (`dispositivos`, F3), bitácora (F3), revocaciones del realm |

## 4 · Lo que ya está hecho con mentalidad Zero Trust

1. **Secretos con mínimo privilegio.** La política de OpenBao del backend solo permite `read` sobre una ruta:
   `path "kv/data/florlogic/backend-finca" { capabilities = ["read"] }`
   (`codigo/despliegue/openbao/politica-backend-finca.hcl`).
2. **Credencial atada a la red.** El AppRole del backend solo sirve desde la subred del nodo (A-35); si la subred
   guardada no coincide, el arranque para: `NodoArranqueSeguroTest.a41_si_la_subred_guardada_no_es_la_del_nodo_para_antes_de_entrar`.
3. **La llave es de la máquina.** Sello `pkcs11` con token HSM USB que «se conecta para abrir y se guarda» (A-22):
   robar el disco no da la llave.
4. **Una sola puerta.** Solo el gateway publica puerto; la base escucha solo en `127.0.0.1`
   (`NodoCompletoTest.el_gateway_es_la_unica_puerta_del_nodo`).
5. **Falla cerrado.** Sin vault el backend no arranca (A-23,
   `NodoArranqueSeguroTest.c5_sin_vault_el_backend_reiniciado_no_queda_escuchando`).
6. **Mínimo privilegio en procesos.** Backend con usuario 10001 sin shell; WAF sin root.

## 5 · Lo que falta, por fase

| Qué | Fase | Prueba que lo cierra |
|---|---|---|
| Backend como servidor de recursos OAuth2 (JWT con el JWKS local de Keycloak) | F3 | Petición sin token → 401; token de otro rol → 403; **petición directa al backend sin pasar por el gateway → 401** |
| MFA TOTP para gerente y administrador | F3 | Prueba de configuración del realm |
| TLS en todos los enlaces (gateway, backend, OpenBao, Keycloak) con la CA de la finca | F6 | `NodoCompletoTest` sobre HTTPS; una petición HTTP en claro no recibe datos |
| Rol de base de la aplicación sin DDL ni `DELETE` sobre bitácora | F3 / F6 | Prueba que intenta `DELETE` en bitácora con el rol de la aplicación y falla |
| ¿mTLS entre contenedores? | F6 (K-46 punto 3, abierto) | Decide Juan |

## 6 · Frase de defensa

> No confiamos en la red de la finca: el gateway filtra, pero el backend vuelve a comprobar quién es cada uno y qué
> puede hacer, los secretos solo se abren con una llave física, y cada pieza tiene exactamente el permiso que
> necesita. Lo que aún no está (TLS y verificación del token) tiene fase y prueba asignadas.
