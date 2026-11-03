#!/usr/bin/env bash
# IA_ · La app del celular (PWA, F8) de punta a punta: el nodo completo por HTTPS con un NOMBRE (no localhost: ID-8),
# un operador de prueba en Keycloak y scripts/IA_probar-app.mjs, que entra y sincroniza con el mismo JS de la app.
# Quién: IA (Claude), 3-nov-2026, a pedido de Jeronimo · Estado: SIN REVISAR
# Manda por encima: codigo/IA_CONSTRUCCION-TX1.md (CT-4, F8) · despliegue/keycloak/IA_LEEME-identidad.md (ID-5, ID-8)
#
# Necesita Docker, el JDK 27 (la ceremonia) y Node 22 o más nuevo. Como IA_probar-canal.sh, usa su propio proyecto
# (florlogic-app), su subred y un secreto cero inventado; al terminar hace down -v de ese proyecto, no del tuyo.
#
#   scripts/IA_probar-app.sh            # prueba y lo tumba todo
#   scripts/IA_probar-app.sh --dejar    # prueba y deja el nodo arriba para abrir /app/ desde un navegador o el celular
#
# El nombre por defecto es 127-0-0-1.sslip.io, que el DNS público de sslip.io resuelve a 127.0.0.1 (necesita salida a
# internet para resolverlo). Para el celular, FLORLOGIC_APP_NOMBRE=192-168-1-10.sslip.io con la IP de este equipo.
set -euo pipefail
cd "$(dirname "$0")/.."

DEJAR=no
[[ "${1:-}" == "--dejar" ]] && DEJAR=si
NOMBRE="${FLORLOGIC_APP_NOMBRE:-127-0-0-1.sslip.io}"
export FLORLOGIC_GATEWAY_PUERTO="${FLORLOGIC_GATEWAY_PUERTO:-8443}"
# Sin «:443»: Keycloak quita el puerto por defecto del emisor y el backend lo compara letra por letra (si no, 401).
NODO="https://$NOMBRE"
[[ "$FLORLOGIC_GATEWAY_PUERTO" == 443 ]] || NODO="$NODO:$FLORLOGIC_GATEWAY_PUERTO"
COMPOSE="docker compose -p florlogic-app -f despliegue/compose.yaml"
export FLORLOGIC_RED=172.30.10.0/24 FLORLOGIC_BASE_PUERTO=0

alazar() { head -c 16 /dev/urandom | od -An -tx1 | tr -d ' \n'; }
export FLORLOGIC_PG_ADMIN="$(alazar)" FLORLOGIC_HSM_PIN="$(alazar)" FLORLOGIC_SOFTHSM_SO_PIN="$(alazar)"
export FLORLOGIC_KC_ADMIN="$(alazar)" FLORLOGIC_WEB_SECRETO="$(alazar)"
export FLORLOGIC_SOFTHSM_CREAR=si BAO_HSM_LIB=/usr/lib/softhsm/libsofthsm2.so   # SoftHSM: fuera de la finca (A-29)
# Git Bash (Windows) convierte toda variable que parezca ruta POSIX al lanzar docker: BAO_HSM_LIB llegaría a OpenBao
# como C:/Program Files/Git/usr/lib/… y el vault no arrancaría. FLORLOGIC_PKI sí se convierte: es carpeta del anfitrión.
export MSYS2_ENV_CONV_EXCL=BAO_HSM_LIB
PKI="$(mktemp -d)"
export FLORLOGIC_RAIZ_CLAVE="$(alazar)" FLORLOGIC_PKI="$PKI/nodo" FLORLOGIC_CEREMONIA_ESPERA=300
export FLORLOGIC_NODO_URL="$NODO" FLORLOGIC_NODO_DIRECCIONES="$NOMBRE"
OPERADOR=operador-prueba
CLAVE_OPERADOR="$(alazar)"
CEREMONIA="java despliegue/pki/Ceremonia.java"

limpiar() {
    if [[ "$DEJAR" == si ]]; then
        kill "${FIRMANTE:-0}" 2>/dev/null || true
        return
    fi
    $COMPOSE down -v >/dev/null 2>&1 || true
    kill "${FIRMANTE:-0}" 2>/dev/null || true
    rm -rf "$PKI"
}
trap limpiar EXIT

echo "1 · La ceremonia y el nodo completo por HTTPS en $NODO (espera a que esté sano)…"
$CEREMONIA raiz "$PKI/custodia" "$PKI/nodo" | sed 's/^/   /'
( until [ -s "$PKI/nodo/intermedia.csr" ]; do sleep 1; done; $CEREMONIA firmar "$PKI/custodia" "$PKI/nodo" >/dev/null ) &
FIRMANTE=$!
if ! $COMPOSE up -d --build --wait >"$PKI/up.log" 2>&1; then
    tail -20 "$PKI/up.log"
    $COMPOSE logs openbao openbao-init keycloak backend gateway 2>&1 | tail -40
    echo "El nodo no arrancó."
    exit 1
fi

echo "2 · Operador de prueba en Keycloak ($OPERADOR, clave al azar de esta corrida):"
$COMPOSE exec -T -e USUARIO_OPERADOR="$OPERADOR" -e CLAVE_OPERADOR="$CLAVE_OPERADOR" keycloak bash -s \
    < despliegue/keycloak/IA_crear-operador-prueba.sh | sed 's/^/   /'

echo "3 · La app, el inicio de sesión y la sincronización, como el celular:"
SALIDA="$PKI/app.log"
NODE_EXTRA_CA_CERTS="$PKI/nodo/raiz.crt" FLORLOGIC_NODO_URL="$NODO" FLORLOGIC_OPERADOR="$OPERADOR" \
    FLORLOGIC_OPERADOR_CLAVE="$CLAVE_OPERADOR" node scripts/IA_probar-app.mjs | tee "$SALIDA" | sed 's/^/   /'
CELULAR="$(sed -n 's/^CELULAR=//p' "$SALIDA")"

echo "4 · Lo que quedó en la base (el celular, pendiente; las celdas, con su sección B12-C…):"
$COMPOSE exec -T base psql -U florlogic -d florlogic -At -c \
    "SELECT 'celular ' || estado FROM dispositivo WHERE dispositivo_id = '$CELULAR';"
$COMPOSE exec -T base psql -U florlogic -d florlogic -c \
    "SELECT seccion_id, campo_id, valor FROM hecho_produccion WHERE dispositivo_id = '$CELULAR' ORDER BY 1, 2;"
echo "App OK."

if [[ "$DEJAR" == si ]]; then
    cat <<FIN

El nodo queda arriba (proyecto florlogic-app). Para abrir la app:
  · La raíz de la finca, para importarla en el navegador o el celular: $PKI/nodo/raiz.crt
  · La app: $NODO/app/   · La web del administrador: $NODO/web/
  · El operador de prueba es $OPERADOR; su clave se generó al azar y no se guardó. Para ponerle una tuya:
      $COMPOSE exec -T -e USUARIO_OPERADOR=$OPERADOR -e CLAVE_OPERADOR=<tu-clave> keycloak bash -s < despliegue/keycloak/IA_crear-operador-prueba.sh
  · Para tumbarlo: $COMPOSE down -v   (y borrar $PKI)
FIN
fi

# ── Guía de lectura · IA (Claude), 3-nov-2026 · SIN REVISAR ──
# - set -euo pipefail: para en el primer error, en una variable sin definir o en una tubería que falla.
# - mktemp -d: carpeta temporal para la ceremonia (fuera del repo, PR-06); trap limpiar EXIT la borra al salir.
# - head -c 16 /dev/urandom | od -An -tx1: 16 bytes al azar en hexadecimal (el secreto cero de esta corrida).
# - FLORLOGIC_NODO_URL y FLORLOGIC_NODO_DIRECCIONES: la dirección que ve el celular (emisor de los tokens y regreso de
#   Keycloak, /app/*) y el nombre que lleva el certificado del gateway.
# - ( until … ; done; … ) &: en segundo plano, espera la solicitud de la intermedia y la firma (paso 2 de la ceremonia).
# - docker compose … up -d --build --wait: construye, arranca y espera a que todo esté sano; su salida va a up.log.
# - exec -T … bash -s < script: corre el script dentro del contenedor por la entrada estándar (en Windows, bash -c
#   parte la orden en sus comillas).
# - NODE_EXTRA_CA_CERTS: Node confía además en la raíz de la finca para el HTTPS del nodo.
# - tee: muestra la salida y la guarda; sed -n 's/^CELULAR=//p' saca el id del celular que usó la prueba.
# - psql -At: sin encabezados ni alineación, para una línea suelta.
