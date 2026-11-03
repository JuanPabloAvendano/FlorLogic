REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 31-oct-2026, a pedido de Juan («go» a la identidad con Keycloak, CT-2)
Estado de revisión: SIN REVISAR
Manda por encima: la palabra de Juan · `codigo/IA_CONSTRUCCION-TX1.md` (CT-2) · `ESPEC-contrato-ingesta.md` A-22, A-41, A-51 · TEC-32 en `.claude/contexto/IA_TECNOLOGIAS-vigentes.md`

# Identidad del nodo: Keycloak

## Qué hay

| Pieza | Dónde | Qué hace |
|---|---|---|
| `keycloak` (Compose) | `despliegue/compose.yaml` | Keycloak 26.8.1 por digest, sin puertos publicados, `/auth` como ruta, base `keycloak` en el mismo PostgreSQL, caché local (un solo nodo) |
| `base-init` (Compose) | `despliegue/compose.yaml` | Crea la base `keycloak` si falta, en cada arranque (también sobre un volumen `datos` viejo) |
| Realm `florlogic` | `despliegue/keycloak/realm-florlogic.json` | Roles `operador`, `gerente`, `administrador`; clientes `celular` (público, PKCE S256) y `web` (confidencial, flujo `navegador-con-totp`: contraseña y TOTP siempre). Sin usuarios ni secretos. Se importa **solo si el realm no existe** |
| Cliente `simulador` | `despliegue/keycloak/IA_crear-cliente-simulador.sh` | **Solo desarrollo y CI**: credenciales de cliente con el rol `operador`. No está en el realm de la finca |
| Gateway | `despliegue/gateway/default.conf.template` | `/auth/realms/florlogic/…` y `/auth/resources/…` van a Keycloak; `master` y la consola, 404 |
| Backend | `backend-finca/…/infraestructura/SeguridadDelNodo.java` | Servidor de recursos: firma (JWKS interno), `exp`, `iss`, `aud = backend-finca`; `/ingesta/**` pide `operador`; salud y métricas sin token; lo demás, nadie |
| Web (F4) | `backend-finca/…/infraestructura/WebDelNodo.java` | `/web/**` con sesión en cookie y CSRF: cliente `web` (código + PKCE, canje por la red interna), roles del token de acceso validado igual que la API; tablero para gerente y administrador, trabajos para gerente, celulares y asignación para administrador; salir cierra también la sesión de Keycloak |

## Decisiones de esta pasada (Claude, por el «go» de Juan del 31-oct)

- **El emisor es fijo:** `KC_HOSTNAME = ${FLORLOGIC_NODO_URL}/auth` (por defecto `https://localhost/auth`). El `iss` de
  todo token es `${FLORLOGIC_NODO_URL}/auth/realms/florlogic`, entre por donde entre la petición, y el backend lo
  compara (`FLORLOGIC_IDENTIDAD_EMISOR`). En la finca, `FLORLOGIC_NODO_URL` es la dirección del nodo en la red de la
  finca (la que abre el celular). Las llaves las pide el backend por la red interna (`https://keycloak:8443`, A-44).
- **La persona es el `sub`** del token (contrato A-51). El celular (`dispositivo_uuid`) sigue en el cuerpo y lo juzga
  el registro de dispositivos (CT-10): el token dice quién, no con qué celular.
- **Errores del contrato (A-22):** `401 credencial-invalida` (sin token, vencido, otra firma, otro emisor, otra
  audiencia) antes que `403 sin-permiso` con `regla: ingesta_entregar` (token sin el rol `operador`).
- **El cliente `simulador` no vive en el realm:** lo crea un script con `kcadm` dentro del contenedor, con un secreto
  de la corrida. Así la finca no trae una puerta de pruebas.
- **El realm se importa una vez.** Cambiar `realm-florlogic.json` no cambia un realm ya creado: en desarrollo,
  `down -v`; en la finca, por `kcadm` (pendiente: decidir cómo se migran cambios del realm).

## Cómo se usa en desarrollo

```
# despliegue/.env necesita también:
FLORLOGIC_KC_ADMIN=…        # el administrador del realm master (solo se aplica en el primer arranque)

docker compose -f despliegue/compose.yaml up -d --build --wait
# el cliente del simulador, con un secreto tuyo:
docker compose -f despliegue/compose.yaml exec -T -e SECRETO_SIMULADOR="$s" keycloak bash -s \
  < despliegue/keycloak/IA_crear-cliente-simulador.sh
FLORLOGIC_SIMULADOR_SECRETO="$s" java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar sincronizar --archivo …
```

**La web (F4).** Entra por `${FLORLOGIC_NODO_URL}/web/`: sin sesión, el backend manda a Keycloak (cliente `web`, PKCE),
que pide contraseña y TOTP (el primer ingreso obliga a configurar el TOTP). Un gerente de prueba:

```
docker compose -f despliegue/compose.yaml exec keycloak bash    # y adentro:
export HOME=/tmp; K=/opt/keycloak/bin/kcadm.sh
# Solo HTTPS (A-44): kcadm confía en la raíz de la finca que openbao-init deja en /tls
keytool -importcert -noprompt -alias raiz-finca -file /tls/raiz.crt -keystore /tmp/confianza.p12 -storetype PKCS12 -storepass confianza
$K config truststore --trustpass confianza /tmp/confianza.p12
$K config credentials --server https://localhost:8443/auth --realm master --user admin --password "$(cat /run/secrets/kc-admin)"
$K create users -r florlogic -s username=gerente1 -s enabled=true
$K set-password -r florlogic --username gerente1 --new-password '…' --temporary
$K add-roles -r florlogic --uusername gerente1 --rolename gerente      # o administrador
```

Un realm creado antes del 1-nov-2026 no tiene el secreto del cliente `web` que espera el backend: en desarrollo,
`down -v`; si no, `kcadm.sh update clients/<id> -r florlogic -s secret=…` con el mismo valor.

Administrar (crear usuarios, ver sesiones) se hace desde el nodo, nunca por el gateway:
`docker compose -f despliegue/compose.yaml exec keycloak /opt/keycloak/bin/kcadm.sh …`.

## Pendiente (no hecho en esta pasada)

| # | Qué | Por qué queda |
|---|---|---|
| ID-1 | Keycloak entra a la base como `florlogic` (el administrador), como el backend | Un rol propio para `keycloak` necesita su secreto: pasa por OpenBao o por otro secreto cero. Decidir con K-22 |
| ~~ID-2~~ | ~~Sin TLS~~ | **Hecho 1-nov-2026:** Keycloak solo en HTTPS (8443) con el certificado que emite `openbao-init`; el gateway y el backend le hablan por TLS verificado; `FLORLOGIC_NODO_URL` es `https://…`. La salud, en claro solo en el loopback del contenedor (A-57 del esqueleto, por revisar con Juan) |
| ID-8 | El WAF bloquea el inicio de sesión de la web si el nodo se llama por `localhost` o por IP: la `redirect_uri` dispara las reglas 934110 (SSRF) y 931100 (RFI) de CRS. Con un nombre (`nodo.finca`) pasa | Visto el 1-nov-2026. Decide Juan: nombre siempre (DNS local o mDNS) o una excepción angosta para `ARGS:redirect_uri` en `/auth/realms/florlogic/protocol/openid-connect/auth`. Para desarrollo y el celular (F8, 3-nov): un nombre `<ip-con-guiones>.sslip.io`, que el DNS público resuelve a esa IP; lo usa `scripts/IA_probar-app.sh` |
| ~~ID-3~~ | ~~El cliente `web` y su secreto~~ | **Hecho 1-nov-2026 (F4):** sale del secreto cero `FLORLOGIC_WEB_SECRETO` (secreto de Compose `web-secreto`): Keycloak lo pone al cliente al importar el realm y el backend lo lee por `configtree:/run/secrets/`. Pendiente: pasarlo por OpenBao como la clave de la base, y rotarlo |
| ID-4 | Usuarios reales y cómo los da de alta el administrador | F4 hizo la web sin pantalla de usuarios: por ahora `kcadm` (abajo). Una pantalla propia necesitaría la API de administración de Keycloak desde el backend (otro cliente con permisos de `realm-management`) |
| ~~ID-5~~ | ~~El celular de verdad (PKCE + refresh offline)~~ | **Hecho 3-nov-2026 (F8, Claude, a pedido de Jeronimo):** la PWA servida en `/app/` usa el cliente `celular` con código + PKCE S256 y alcance `openid offline_access` (`backend-finca/src/main/resources/static/app/js/auth.js`); el refresh token es offline (30 días sin uso). Lo prueba `scripts/IA_probar-app.sh` (crea un operador con `IA_crear-operador-prueba.sh`). Pendiente: probarlo en un celular de verdad |
| ID-6 | Migración de cambios del realm en un nodo ya instalado | El import solo crea; decidir herramienta (¿`keycloak-config-cli`?) antes de la primera finca |
| ID-7 | `kc.sh start` construye la imagen en cada arranque (~15 s) | Se quita con una imagen propia `kc.sh build` + `--optimized`; no urge |
