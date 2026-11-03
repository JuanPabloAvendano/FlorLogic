REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 31-oct-2026, a pedido de Juan («construir toda la transacción de una vez, después aprenderla»)
Estado de revisión: SIN REVISAR
Manda por encima: la palabra de Juan · `Guia_Oficial_Evaluacion_Ingeniería Software II.docx` · `codigo/backend-finca/espec/ESPEC-dominio-ingesta.md` · `codigo/contrato/espec/ESPEC-contrato-ingesta.md` · el README de `documentacion-desarrollo`

# Construcción completa de TX-1 · plan y decisiones

Esta carpeta (`~/Work/florlogic-tx1-completa`) es una **copia sin git** de `florlogic-codigo` tomada el 31-oct-2026
con el Segmento 1b terminado (171/171 con Docker). Aquí se construye **toda TX-1** de una vez, cumpliendo la guía de
IS II; se estudia después. Reglas de esta copia:

- Arquitectura: un hexágono por módulo con los nombres de A-97 y A-105 (`servicios/{input,output}/{ports,adapter,ensamblador,dto}`,
  `infraestructura/{input,output}`, `dominio` puro), `CapasTest` y `ModulosTest` en verde siempre.
- Tecnologías nuevas: Claude revisa CVE y avisos (OSV, Trivy) y las usa; la fila va a `IA_TECNOLOGIAS-vigentes.md`
  como **PROPUESTA** con la evidencia. Juan las acepta después.
- Pruebas: cada componente con su prueba automática; `./mvnw verify` (sin `clean` si Eclipse está abierto).
- Sin commits (copia sin git).

## Fases

| Fase | Qué | Estado |
|---|---|---|
| F1 | Ingesta completa: sesión abrir/cerrar, huella, idempotencia de 3 casos, rechazadas, `excepciones[]`, simulador con sesiones | **hecha 31-oct** · `./mvnw verify` 215/215 con Docker |
| F2 | `configuracion` (catálogo, versiones, parámetros, `type` de problemas A-104) · `reglas` · `ciclo` | **catálogo, reglas y ciclo hechos 31-oct** (hechos de producción y trabajo del gerente por evento de Modulith con registro en PostgreSQL); faltan los `type` en el catálogo (A-104) |
| F3 | `identidad` (Keycloak OIDC, roles, MFA) · `dispositivos` · `bitacora` | **hecha 31-oct.** `dispositivos` y `bitacora` (pendiente → aprobado/bloqueado, 403; solo inserción con cadena SHA-256). **Identidad:** Keycloak 26.8.1 en Compose (realm `florlogic`, roles, `celular` PKCE, `web` con TOTP obligatorio), backend como servidor de recursos (401 `credencial-invalida` / 403 `ingesta_entregar`, persona = `sub`), gateway con `/auth/` solo del realm florlogic, simulador con token. `./mvnw verify` **250/250** con Docker; `IA_probar-canal.sh` «Canal OK». Pendientes ID-1…ID-7 en `despliegue/keycloak/IA_LEEME-identidad.md` |
| F4 | `consulta` + web del gerente (Thymeleaf + htmx) | **hecha 1-nov** (CT-14…CT-17): `/web/**` en el backend con sesión de Keycloak (cliente `web`, PKCE, TOTP), CSRF y CSP estricta; **tablero** por celular y por sección (esperado, recibido, falta, marcadas, rechazadas, parciales; htmx cada 30 s); **celulares** (aprobar/bloquear con htmx, a la bitácora); **asignación del día** (V9); **trabajos del gerente** (resolver una vez, a la bitácora); gateway con `/web`. Sin pantalla de usuarios (ID-4: `kcadm`) ni corrección de valores por el gerente |
| F5 | `notificacion` (ntfy) + `trabajos` (db-scheduler) | |
| F6 | Plataforma: TLS en todo el nodo, WAF, observabilidad, CI/CD (GitHub Actions, JaCoCo, Trivy, SonarQube), perfiles de Compose | **hechos 31-oct (subagente):** JaCoCo, SonarQube (perfil `calidad`), Trivy, CI, observabilidad (perfil `observabilidad`), Boot 4.1.2 por CVE de Tomcat; **TLS hecho 1-nov** (Claude, a pedido de Juan): HTTPS en todo enlace del nodo, mTLS gateway → backend, CA de la finca (raíz fuera de línea + intermedia en OpenBao), ESPEC-esqueleto A-52…A-60; falta el aviso de vencimiento y las alertas |
| F7 | Documentos de la guía (C4 N1, riesgos, trazabilidad, STRIDE, OWASP, Zero Trust, 12F, WA, reactivo, CAF, IA responsable, defensa) | **primera pasada hecha 31-oct** en `codigo/sustentacion/` (subagente); falta la pasada final con la evidencia de F3–F8 |
| F8 | PWA de captura sin red (HTML/JS sin framework, IndexedDB, service worker) | |

## Decisiones de la construcción (31-oct-2026, Juan)

| # | Decisión |
|---|---|
| CT-1 | La sesión de sincronización se abre y se cierra: `POST /ingesta/v1/sesiones` → `201 {sesion_id, desfase_ms, confirmado_hasta}`; `POST …/sesiones/{id}/particiones` exige una sesión abierta (si no, `404 sesion-desconocida`); `POST …/sesiones/{id}/cierre` → `200 {estado, version_config, avisos[]}` |
| CT-2 | Identidad con Keycloak 26 en el nodo (TEC-32): realm `florlogic`, roles `operador` (entrega), `gerente` (ve y corrige), `administrador` (dispositivos y usuarios). Celular: cliente público con PKCE y refresh token. Web: cliente confidencial con **MFA TOTP obligatorio** para gerente y administrador. El nodo es servidor de recursos (JWT validado con el JWKS local) |
| CT-3 | Aviso al administrador por **ntfy** en el nodo: puerto `AvisoAlAdministrador`, adaptador HTTP al tema; el aviso se guarda y `trabajos` (db-scheduler) lo reintenta |
| CT-4 | PWA en HTML/JS sin framework: módulos ES, IndexedDB, service worker; la sirve el gateway |
| CT-5 | Construcción en copia sin git; Claude no hace commits |
| CT-6 | La huella la calcula un puerto `CalculadorDeHuella` que pide el caso de uso (A-106); forma canónica propia en `contrato` probada con los vectores del RFC 8785 |
| CT-7 | El celular de una partición es el de su sesión (contrato A-52): se retira `DispositivoDelNodo` y `FLORLOGIC_INGESTA_DISPOSITIVO_ID` (A-107 superada) |
| CT-8 | El gateway deja pasar abrir, particiones y cierre (`^/ingesta/v1/sesiones(/{id}/(particiones|cierre))?$`); todo lo demás sigue en 404 |
| CT-9 | Spring Boot 4.1.2 (Tomcat 11.0.27, Jackson 3.1.7) por 4 CVE críticos de Tomcat 11.0.24 que encontró Trivy (subagente de plataforma) |
| CT-10 | Dispositivos: el primer celular desconocido queda **PENDIENTE**; sus anotaciones entran marcadas `dispositivo_no_registrado` y se avisa al administrador; el administrador lo **APRUEBA** o lo **BLOQUEA** en la web; uno bloqueado recibe `403 dispositivo_bloqueado` |
| CT-11 | Bitácora: eventos clave (sesión abierta/cerrada, anotación corrupta y rechazada, celular aprobado/bloqueado, correcciones del gerente, ingresos); tabla de solo inserción (trigger que impide UPDATE/DELETE) con **cadena SHA-256**, verificada por una prueba y por un trabajo diario |
| CT-12 | Ciclo: cada aplicada actualiza el **hecho de producción** (producción, sección, campo, jornada; gana la captura más reciente, RF-022) y cada marcada abre un **trabajo del gerente**; lo alimenta un evento de Modulith después del commit |
| CT-13 | Consulta: **asignación** del día (celular → secciones × campos) que define el administrador; tablero por celular y por sección: esperado, recibido, falta, marcadas, rechazadas, sesiones parciales, con htmx |
| CT-14 | La web vive en el mismo backend, en `/web/**`, con una segunda cadena de Spring Security (`WebDelNodo`): sesión en cookie (`FLORLOGIC_WEB`, 30 min, `SameSite=Lax`), CSRF de la sesión (htmx lo manda en `X-CSRF-TOKEN`), CSP sin scripts ni estilos en línea. Los roles salen del **token de acceso** validado con el mismo `JwtDecoder` de la API. El registro del cliente se arma en código (sin discovery: el backend arranca sin Keycloak). Detrás del gateway, `forward-headers-strategy: framework` con `X-Forwarded-Host` (Claude, 1-nov, F4) |
| CT-15 | Quién ve qué: **tablero** gerente y administrador; **trabajos** solo gerente; **celulares** y **asignación** solo administrador; toda otra ruta de `/web`, nadie (Claude, 1-nov, F4) |
| CT-16 | **Asignación:** por celular y jornada, todas sus secciones × todos sus campos; solo a un celular **aprobado**; reasignar reemplaza lo del día; sin secciones o sin campos la deja vacía; máximo 500 celdas; queda en la bitácora (`asignacion_cambiada`) (Claude, 1-nov, F4) |
| CT-17 | **Tablero:** una celda asignada cuenta como **recibida** si ya tiene hecho de producción ese día, la haya capturado ese celular u otro; las **marcadas** cuentan también las ya resueltas; rechazadas y sesiones parciales se ubican en el día con la zona de la finca (`FLORLOGIC_FINCA_ZONA`, por defecto `America/Bogota`); lo guardado sigue en UTC (Claude, 1-nov, F4) |

## Riesgos vistos en la construcción

- En `backend-finca`, Surefire mata su JVM 30 s después de `System.exit` (Hikari valida conexiones ya cerradas al
  apagar); las pruebas pasan igual. Visto antes y después de la identidad (31-oct). Vigilar.
- `IA_probar-canal.sh` no arrancó una vez con el Compose de desarrollo (`despliegue-gateway-1`) reiniciándose en bucle
  sobre el puerto 80; pasó al repetir con ese contenedor parado.

- `NodoArranqueSeguroTest.c5` falló una vez con `docker compose up --wait` = 255 al devolver el nodo sano; pasó al repetir. Intermitente; vigilar.
- `NodoArranqueSeguroTest.c5` dio 504 en vez de 502 con HTTPS (la petición cae mientras el backend se reinicia y la IP vieja no contesta); desde el 1-nov acepta 502 o 504 con `no-disponible`.
