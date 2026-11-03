#!/usr/bin/env bash
# IA_ · Crea (o realinea) un operador de prueba en el realm florlogic, para entrar desde la app del celular (F8).
# Quién: IA (Claude), 3-nov-2026, a pedido de Jeronimo · Estado: SIN REVISAR
# Manda por encima: despliegue/keycloak/IA_LEEME-identidad.md (ID-4: los usuarios se crean con kcadm) · la palabra de Juan
# Solo desarrollo y CI, como IA_crear-cliente-simulador.sh: corre DENTRO del contenedor de Keycloak, por la entrada
# estándar, con la clave en la variable CLAVE_OPERADOR (nunca en la orden):
#   docker compose … exec -T -e USUARIO_OPERADOR=… -e CLAVE_OPERADOR=… keycloak bash -s < despliegue/keycloak/IA_crear-operador-prueba.sh
set -euo pipefail
: "${USUARIO_OPERADOR:?falta USUARIO_OPERADOR}"
: "${CLAVE_OPERADOR:?falta CLAVE_OPERADOR}"

KCADM=/opt/keycloak/bin/kcadm.sh
export HOME=/tmp   # kcadm guarda su sesión en ~/.keycloak
rm -f /tmp/confianza.p12
keytool -importcert -noprompt -alias raiz-finca -file /tls/raiz.crt -keystore /tmp/confianza.p12 -storetype PKCS12 \
    -storepass confianza >/dev/null 2>&1
"$KCADM" config truststore --trustpass confianza /tmp/confianza.p12
"$KCADM" config credentials --server https://localhost:8443/auth --realm master --user admin \
    --password "$(cat /run/secrets/kc-admin)" >/dev/null

id=$("$KCADM" get users -r florlogic -q "username=$USUARIO_OPERADOR" -q exact=true --fields id --format csv --noquotes)
if [ -z "$id" ]; then
    # Con nombre, apellido y correo verificado: sin ellos Keycloak pide completar el perfil al entrar.
    "$KCADM" create users -r florlogic -s "username=$USUARIO_OPERADOR" -s enabled=true -s firstName=Operador \
        -s lastName=Prueba -s "email=$USUARIO_OPERADOR@prueba.invalid" -s emailVerified=true >/dev/null
    echo "operador $USUARIO_OPERADOR creado"
else
    echo "operador $USUARIO_OPERADOR ya existía; clave realineada"
fi
"$KCADM" set-password -r florlogic --username "$USUARIO_OPERADOR" --new-password "$CLAVE_OPERADOR"
"$KCADM" add-roles -r florlogic --uusername "$USUARIO_OPERADOR" --rolename operador

# ── Guía de lectura · IA (Claude), 3-nov-2026 · SIN REVISAR ──
# - : "${VAR:?mensaje}": si la variable no existe o está vacía, el script para con ese mensaje.
# - keytool -importcert … -storetype PKCS12: arma un almacén de confianza con la raíz de la finca para que kcadm hable
#   HTTPS con este mismo Keycloak (8443) sin aceptar cualquier certificado.
# - kcadm.sh config credentials --realm master --user admin: inicia sesión de administración; la clave sale del
#   secreto de Compose kc-admin, nunca de la orden.
# - kcadm.sh get users -q username=… -q exact=true --fields id --format csv --noquotes: el id del usuario, o nada.
# - kcadm.sh create users -s campo=valor: crea el usuario; set-password --new-password: le pone la clave (no temporal);
#   add-roles --uusername … --rolename operador: le da el rol del realm que exige /ingesta.
# - @prueba.invalid: el dominio .invalid está reservado (RFC 2606): ningún correo sale de aquí.
