#!/usr/bin/env bash
# IA_ · Crea (o deja igual) el cliente «simulador» del realm florlogic: confidencial, solo credenciales de cliente, y su
#      cuenta de servicio con el rol operador. SOLO desarrollo, CI y pruebas: en la finca no existe (no está en
#      realm-florlogic.json a propósito), porque un secreto que entrega particiones sin persona es una puerta más.
# Quién: IA (Claude), 31-oct-2026, a pedido de Juan (identidad, CT-2); 1-nov-2026: por HTTPS (A-44) · Estado: SIN REVISAR
# Manda por encima: codigo/IA_CONSTRUCCION-TX1.md (CT-2) · despliegue/keycloak/IA_LEEME-identidad.md
# Uso: corre DENTRO del contenedor keycloak, con el secreto en la variable SECRETO_SIMULADOR:
#   docker compose … exec -T -e SECRETO_SIMULADOR="$s" keycloak bash -s < despliegue/keycloak/IA_crear-cliente-simulador.sh
# El administrador entra con la clave del secreto cero (/run/secrets/kc-admin); nunca pasa por la línea de órdenes.
set -euo pipefail
: "${SECRETO_SIMULADOR:?falta SECRETO_SIMULADOR}"
KCADM=/opt/keycloak/bin/kcadm.sh
export HOME=/tmp   # kcadm guarda su sesión en ~/.keycloak

# Solo HTTPS (A-44): kcadm confía en la raíz de la finca, que openbao-init deja junto al certificado de Keycloak. El
# almacén solo trae la raíz pública: su clave no es un secreto.
rm -f /tmp/confianza.p12
keytool -importcert -noprompt -alias raiz-finca -file /tls/raiz.crt -keystore /tmp/confianza.p12 -storetype PKCS12 \
    -storepass confianza >/dev/null 2>&1
"$KCADM" config truststore --trustpass confianza /tmp/confianza.p12
"$KCADM" config credentials --server https://localhost:8443/auth --realm master --user admin \
    --password "$(cat /run/secrets/kc-admin)" >/dev/null

id=$("$KCADM" get clients -r florlogic -q clientId=simulador --fields id --format csv --noquotes)
if [ -z "$id" ]; then
    "$KCADM" create clients -r florlogic -f - >/dev/null <<JSON
{
  "clientId": "simulador",
  "name": "Simulador del celular (solo desarrollo y CI)",
  "enabled": true,
  "publicClient": false,
  "clientAuthenticatorType": "client-secret",
  "secret": "$SECRETO_SIMULADOR",
  "standardFlowEnabled": false,
  "directAccessGrantsEnabled": false,
  "serviceAccountsEnabled": true,
  "protocolMappers": [{
    "name": "audiencia-backend-finca", "protocol": "openid-connect", "protocolMapper": "oidc-audience-mapper",
    "config": {"included.custom.audience": "backend-finca", "access.token.claim": "true", "id.token.claim": "false"}
  }]
}
JSON
    "$KCADM" add-roles -r florlogic --uusername service-account-simulador --rolename operador
    echo "cliente simulador creado"
else
    # Ya existe: solo se alinea el secreto con el de esta corrida.
    "$KCADM" update "clients/$id" -r florlogic -s "secret=$SECRETO_SIMULADOR"
    echo "cliente simulador ya existía; secreto actualizado"
fi

# ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
# Propósito: deja en Keycloak el cliente «simulador» (máquina, sin persona) con su secreto y el rol operador, para
# que el simulador del celular pida tokens en desarrollo y CI.
# Uso: lo ejecuta scripts/IA_probar-canal.sh (o una persona) dentro del contenedor keycloak con «docker compose exec
# -T … bash -s < este archivo» (ver la cabecera). Se puede correr las veces que se quiera.
# Origen: la cabecera de arriba (IA_CONSTRUCCION-TX1, tarjeta CT-2: identidad con Keycloak).
# Línea por línea (lo no obvio):
# - #!/usr/bin/env bash: busca bash en el PATH; la imagen de Keycloak trae bash.
# - set -euo pipefail: -e corta al primer error, -u falla con variables sin definir, -o pipefail hace que una tubería
#   falle si falla cualquiera de sus partes (no solo la última).
# - : "${SECRETO_SIMULADOR:?…}": aborta con el mensaje si no llega el secreto.
# - export HOME=/tmp: kcadm guarda su sesión en la carpeta personal; el usuario de la imagen no tiene una escribible.
# - keytool -importcert -noprompt -alias raiz-finca -file /tls/raiz.crt -keystore /tmp/confianza.p12 -storetype
#   PKCS12 -storepass confianza: crea un almacén de confianza con solo la raíz de la finca. -importcert importa un
#   certificado, -noprompt no pregunta «¿confía en este certificado?», -alias es el nombre de la entrada, -storetype
#   PKCS12 el formato estándar, -storepass la clave del almacén (no protege nada secreto: solo hay una raíz pública).
# - kcadm.sh config truststore --trustpass: kcadm (el CLI de administración de Keycloak) confiará solo en ese almacén.
# - kcadm.sh config credentials --server https://localhost:8443/auth --realm master --user admin --password …: entra
#   como administrador del realm master; la clave sale del Compose secret con $(cat …).
# - get clients -q clientId=simulador --fields id --format csv --noquotes: busca el cliente y devuelve solo su id,
#   sin comillas; vacío si no existe.
# - [ -z "$id" ]: -z es «cadena vacía»; decide entre crear y actualizar.
# - create clients -f - <<JSON: -f - lee el JSON desde la entrada estándar; el heredoc sin comillas deja que el shell
#   ponga el secreto dentro.
# - "publicClient": false + "clientAuthenticatorType": "client-secret": cliente confidencial que se autentica con
#   secreto. "standardFlowEnabled": false y "directAccessGrantsEnabled": false: sin inicio de sesión de persona ni
#   usuario y clave. "serviceAccountsEnabled": true: activa el flujo «credenciales de cliente» (client_credentials).
# - protocolMapper "oidc-audience-mapper" con included.custom.audience = backend-finca: agrega «backend-finca» al
#   campo aud del token de acceso, que el backend exige; no se pone en el id token.
# - add-roles --uusername service-account-simulador --rolename operador: da el rol de realm operador al usuario técnico
#   que Keycloak crea para la cuenta de servicio.
# - update "clients/$id" -s "secret=…": -s cambia un solo campo; aquí, el secreto.
