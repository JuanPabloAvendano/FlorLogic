#!/usr/bin/env bash
# IA_ · Prueba real del canal de TX-1 (camino mínimo, D-17): simulador → gateway (Nginx con su WAF) → backend-finca → PostgreSQL.
# Quién: IA (Claude), 19-oct-2026, a pedido de Juan; 20-oct-2026: pasa por el gateway; 21-oct-2026: el WAF, a pedido
#        de Jeronimo; 29-oct-2026: el nodo completo en Compose, con el vault (ESPEC-esqueleto-tx1, Segmento 1b, K-17);
#        31-oct-2026: con Keycloak y token de operador (IA_CONSTRUCCION-TX1 CT-2); 1-nov-2026: solo HTTPS, con la
#        ceremonia de la CA de la finca (A-44) · Estado: SIN REVISAR
# Manda por encima: codigo/contrato/espec/ESPEC-contrato-ingesta.md §8 (Segmento 2) · despliegue/ESPEC-gateway-finca.md · despliegue/ESPEC-waf-finca.md · despliegue/espec/ESPEC-esqueleto-tx1.md · IA_LEEME.md
# Uso: scripts/IA_probar-canal.sh   (desde codigo/; necesita Docker y el JDK; el gateway usa el
#      FLORLOGIC_GATEWAY_PUERTO, por defecto 443; la primera vez construye las imágenes y tarda unos minutos)
set -euo pipefail
cd "$(dirname "$0")/.."

export FLORLOGIC_GATEWAY_PUERTO="${FLORLOGIC_GATEWAY_PUERTO:-443}"
# Sin «:443»: Keycloak quita el puerto por defecto del emisor de los tokens (iss https://localhost/auth/…) y el backend
# compara el emisor letra por letra con FLORLOGIC_NODO_URL: con «:443», todo token salía 401 (3-nov-2026).
GATEWAY="https://localhost"
[[ "$FLORLOGIC_GATEWAY_PUERTO" == 443 ]] || GATEWAY="$GATEWAY:$FLORLOGIC_GATEWAY_PUERTO"
SESION=0192f0a1-7c00-7000-8000-00000000a001   # solo para la petición de ataque, que el WAF frena antes
CELULAR=0192f0a0-0000-7000-8000-0000000000e1
DORADA=contrato/src/test/resources/dorados/peticion-particion.json
# Proyecto de compose propio (OBS-2-15): sus volúmenes no son los del nodo de desarrollo, así que borrarlos al
# terminar no toca tus datos. Subred y puerto de la base propios: no choca con el nodo de desarrollo encendido (A-20, A-35).
COMPOSE="docker compose -p florlogic-canal -f despliegue/compose.yaml"
export FLORLOGIC_RED=172.30.9.0/24 FLORLOGIC_BASE_PUERTO=0

# El secreto cero de esta corrida (A-10): inventado aquí, vive lo que dura el script y se tira con sus volúmenes.
alazar() { head -c 16 /dev/urandom | od -An -tx1 | tr -d ' \n'; }
export FLORLOGIC_PG_ADMIN="$(alazar)" FLORLOGIC_HSM_PIN="$(alazar)" FLORLOGIC_SOFTHSM_SO_PIN="$(alazar)"
export FLORLOGIC_KC_ADMIN="$(alazar)" FLORLOGIC_WEB_SECRETO="$(alazar)"
# El secreto del cliente simulador (CT-2): solo vive en esta corrida; el simulador lo lee de esta variable.
export FLORLOGIC_SIMULADOR_SECRETO="$(alazar)"
export FLORLOGIC_SOFTHSM_CREAR=si BAO_HSM_LIB=/usr/lib/softhsm/libsofthsm2.so   # SoftHSM: fuera de la finca (A-29)
# Git Bash (Windows) convierte toda variable que parezca ruta POSIX al lanzar docker: BAO_HSM_LIB llegaría a OpenBao
# como C:/Program Files/Git/usr/lib/… y el vault no arrancaría. FLORLOGIC_PKI sí se convierte: es carpeta del anfitrión.
export MSYS2_ENV_CONV_EXCL=BAO_HSM_LIB

# La CA de la finca de esta corrida (A-44…A-46): la raíz se inventa aquí y se tira al terminar. openbao-init espera la
# intermedia firmada (A-56) mientras el firmante de abajo la firma, como lo haría el operador fuera de línea.
PKI="$(mktemp -d)"
export FLORLOGIC_RAIZ_CLAVE="$(alazar)" FLORLOGIC_PKI="$PKI/nodo" FLORLOGIC_CEREMONIA_ESPERA=300
export FLORLOGIC_NODO_URL="$GATEWAY" FLORLOGIC_CA_RAIZ="$PKI/nodo/raiz.crt"
CEREMONIA="java despliegue/pki/Ceremonia.java"
# --ssl-no-revoke: el curl de Windows (Schannel) exige revocación y la CA de la finca no publica CRL ni OCSP; con
# OpenSSL (Linux, macOS) no cambia nada.
CURL="curl -s --ssl-no-revoke --cacert $PKI/nodo/raiz.crt"

limpiar() { $COMPOSE down -v >/dev/null 2>&1 || true; kill "${FIRMANTE:-0}" 2>/dev/null || true; rm -rf "$PKI";
    rm -f /tmp/IA_canal-rota.json /tmp/IA_canal-ataque.json /tmp/IA_canal-ataque.out; }
trap limpiar EXIT

echo "1 · Compilando el simulador (sin pruebas)…"
./mvnw -q -DskipTests -pl simulador -am package

echo "2 · La ceremonia (paso 1: raíz y certificado de OpenBao) y el nodo completo por HTTPS: base, OpenBao,"
echo "    openbao-init (paso 2: firma de la intermedia), Keycloak, backend y gateway (espera a que estén sanos)…"
$CEREMONIA raiz "$PKI/custodia" "$PKI/nodo" | sed 's/^/   /'
( until [ -s "$PKI/nodo/intermedia.csr" ]; do sleep 1; done; $CEREMONIA firmar "$PKI/custodia" "$PKI/nodo" >/dev/null ) &
FIRMANTE=$!
$COMPOSE up -d --build --wait >/dev/null 2>&1 || { $COMPOSE logs openbao openbao-init keycloak backend gateway | tail -40; echo "El nodo no arrancó."; exit 1; }
echo "   Cliente simulador en Keycloak (solo desarrollo y CI):"
$COMPOSE exec -T -e SECRETO_SIMULADOR="$FLORLOGIC_SIMULADOR_SECRETO" keycloak bash -s \
  < despliegue/keycloak/IA_crear-cliente-simulador.sh | sed 's/^/   /'

# Desde aquí todo entra por el gateway, la única puerta del nodo de finca (C-2).
SIMULADOR="java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar"
echo "3 · Primera sincronización por el gateway (espera: 2 nuevas):"
$SIMULADOR sincronizar --url "$GATEWAY" --dispositivo "$CELULAR" --archivo "$DORADA"
echo "4 · Reenvío de la misma partición (espera: 2 ya estaban):"
$SIMULADOR sincronizar --url "$GATEWAY" --dispositivo "$CELULAR" --archivo "$DORADA"
echo "5 · Partición rota (espera: rechazada, código 1):"
echo '{ esto no es json' > /tmp/IA_canal-rota.json
$SIMULADOR sincronizar --url "$GATEWAY" --dispositivo "$CELULAR" --archivo /tmp/IA_canal-rota.json || echo "   código de salida: $?"
echo "5b · Sin token (espera: 401 credencial-invalida del backend; nada se guarda):"
$CURL -H "Content-Type: application/json" --data '{"contrato":"1.0"}' "$GATEWAY/ingesta/v1/sesiones"; echo
echo "5c · El realm master por el gateway (espera: 404 gateway/rechazado; la administración no sale del nodo):"
$CURL "$GATEWAY/auth/realms/master/.well-known/openid-configuration"; echo
echo "6 · Ruta ajena por el gateway (espera: 404 gateway/rechazado; el backend no se entera):"
$CURL "$GATEWAY/actuator/health"; echo
echo "7 · Ataque por el gateway (espera: 403 gateway/bloqueado del WAF; el backend no se entera):"
sed "s/\"persona_id\": \"op-07\"/\"persona_id\": \"op-07' OR 1=1 --\"/" "$DORADA" > /tmp/IA_canal-ataque.json
CODIGO=$($CURL -o /tmp/IA_canal-ataque.out -w '%{http_code}' -H "Content-Type: application/json" \
  --data-binary @/tmp/IA_canal-ataque.json "$GATEWAY/ingesta/v1/sesiones/$SESION/particiones")
cat /tmp/IA_canal-ataque.out; echo
# Si llegó al backend (por ejemplo, con FLORLOGIC_WAF_MODO=DetectionOnly en el entorno), el canal no está bien (OBS-1-9).
[[ "$CODIGO" == 403 ]] || { echo "El WAF no bloqueó el ataque (código $CODIGO)."; exit 1; }
echo "7b · En claro no hay nada (espera: 400, Nginx no atiende HTTP en el puerto TLS):"
curl -s -o /dev/null -w '%{http_code}\n' "http://localhost:$FLORLOGIC_GATEWAY_PUERTO/ingesta/v1/sesiones"
echo "8 · Lo que quedó en la base:"
$COMPOSE exec -T base psql -U florlogic -d florlogic -c \
  "SELECT anotacion_uuid, consecutivo, contenido->'datos'->>'seccion_id' AS seccion FROM anotacion_recibida ORDER BY consecutivo;"
echo "Canal OK."

# ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
# Propósito: prueba de punta a punta del canal de TX-1 contra el nodo real: levanta un nodo aislado en Compose con
# su propia CA, sincroniza con el simulador por el gateway, prueba rechazos (sin token, ruta ajena, ataque, HTTP en
# claro) y muestra lo guardado en PostgreSQL. Al terminar borra todo.
# Uso: una persona desde codigo/: «scripts/IA_probar-canal.sh». Necesita Docker y el JDK 27; tarda minutos la
# primera vez.
# Origen: la cabecera de arriba (ESPEC-contrato-ingesta §8, ESPEC-gateway-finca, ESPEC-waf-finca, ESPEC-esqueleto-tx1;
# D-17 es «camino mínimo funcional primero»).
# Línea por línea (lo no obvio):
# - set -euo pipefail: -e corta al primer error, -u falla con variables sin definir, -o pipefail hace fallar una
#   tubería si falla cualquiera de sus partes.
# - cd "$(dirname "$0")/..": se para en codigo/, sea cual sea la carpeta desde donde se llamó.
# - docker compose -p florlogic-canal -f …: -p da otro nombre de proyecto (volúmenes y red propios, no los del nodo
#   de desarrollo); -f elige el archivo.
# - FLORLOGIC_BASE_PUERTO=0: Docker elige un puerto libre para la base.
# - head -c 16 /dev/urandom | od -An -tx1 | tr -d ' \n': 16 bytes al azar en hexadecimal, sin direcciones (-An) ni
#   espacios: los secretos de esta corrida.
# - mktemp -d: crea una carpeta temporal única para la custodia y la carpeta del nodo de la CA.
# - trap limpiar EXIT: ejecuta limpiar al salir, sea por éxito, error o Ctrl-C: baja el proyecto con sus volúmenes
#   (down -v), mata el firmante, borra la CA y los archivos /tmp/IA_canal-* de las pruebas de rechazo.
# - ./mvnw -q -DskipTests -pl simulador -am package: arma solo el simulador (y lo que necesita), sin pruebas.
# - sed 's/^/   /': sangra la salida para leerla mejor.
# - ( until [ -s intermedia.csr ]; do sleep 1; done; … firmar ) &: un subshell en segundo plano (&) que espera a que
#   openbao-init deje el CSR y lo firma, como haría el operador; $! guarda su PID en FIRMANTE.
# - up -d --build --wait: arranca en segundo plano, reconstruye las imágenes y espera a que todo esté sano.
# - exec -T -e VAR=… keycloak bash -s < archivo: corre el script del anfitrión dentro del contenedor; -T sin terminal,
#   -e pasa la variable, bash -s lee el script de la entrada estándar.
# - curl -s --cacert: silencioso y confiando solo en la raíz de esta corrida. -o archivo -w '%{http_code}': guarda el
#   cuerpo y escribe solo el código HTTP. --data-binary @archivo: manda el archivo tal cual como cuerpo.
# - || echo "código de salida: $?": el paso 5 debe fallar; así el fallo esperado no corta el script por -e.
# - sed "s/…/…' OR 1=1 --/": inyecta un ataque SQL de manual en la partición dorada para que el WAF lo bloquee.
# - [[ "$CODIGO" == 403 ]]: comparación de bash; si no es 403, el WAF no bloqueó y la prueba falla.
# - psql … contenido->'datos'->>'seccion_id': operadores JSON de PostgreSQL; -> baja un nivel, ->> devuelve texto.
