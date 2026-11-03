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
