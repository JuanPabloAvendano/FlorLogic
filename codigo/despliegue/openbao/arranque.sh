#!/bin/sh
# IA_ · Arranque del contenedor openbao: el PIN del token y, solo fuera de la finca, el token SoftHSM
# Quién: IA (Claude), 29-oct-2026, a pedido de Juan (Segmento 1a) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-29, A-37, A-38, A-44, A-46
# 1-nov-2026 (Claude, a pedido de Juan): el certificado del listener (HTTPS, A-44)
# Corre como root (lo necesitan softhsm2-util y el chown) y termina en el docker-entrypoint.sh de la imagen oficial,
# que baja a openbao y ajusta permisos (A-37).
set -eu

TOKEN="florlogic"           # = token_label de openbao.hcl
LLAVE="bao-sello-1"         # = key_label de openbao.hcl
TOKENS="/var/lib/softhsm/tokens"
DATOS="/openbao/data"

leer_secreto() {
    [ -s "/run/secrets/$1" ] || { echo "openbao: falta el Compose secret $1" >&2; exit 1; }
    cat "/run/secrets/$1"
}

: "${BAO_HSM_LIB:?openbao: falta BAO_HSM_LIB (la biblioteca PKCS#11 del token)}"

# El PIN viaja como archivo y se vuelve variable en cada arranque: la documentación avisa que BAO_HSM_PIN hay que
# ponerla otra vez tras reiniciar (A-38; OpenBao 2 · Docs · secc. 376).
BAO_HSM_PIN="$(leer_secreto hsm-pin)"
export BAO_HSM_PIN

# SoftHSM solo con la bandera, que la finca nunca pone: sin ella, aquí no se crea ninguna llave (A-29).
if [ "${FLORLOGIC_SOFTHSM_CREAR:-}" = "si" ] && ! softhsm2-util --show-slots 2>/dev/null | grep -q "Label: *$TOKEN *$"; then
    # Una llave nueva sobre un vault con datos no lo abre nunca: mejor parar y decir qué hacer (A-37, COH-18).
    if [ -e "$DATOS/raft" ] || [ -e "$DATOS/vault.db" ]; then
        echo "openbao: token SoftHSM ausente sobre un vault iniciado: docker compose down -v" >&2
        exit 1
    fi
    SO_PIN="$(leer_secreto softhsm-so-pin)"
    softhsm2-util --init-token --free --label "$TOKEN" --so-pin "$SO_PIN" --pin "$BAO_HSM_PIN" >/dev/null
    pkcs11-tool --module "$BAO_HSM_LIB" --token-label "$TOKEN" --login --pin "$BAO_HSM_PIN" \
        --keygen --key-type aes:32 --label "$LLAVE" >/dev/null
    chown -R openbao:openbao "$TOKENS"
    echo "openbao: token SoftHSM «$TOKEN» y llave «$LLAVE» creados (solo desarrollo, CI y pruebas)"
fi

# El volumen nuevo llega con dueño root; raft escribe como openbao.
chown openbao:openbao "$DATOS"

# HTTPS (A-44, A-46 paso 1): el certificado y la llave del listener llegan como Compose secrets del dueño del
# anfitrión; aquí quedan con dueño openbao y 0400, que es quien los lee.
TLS=/openbao/tls
[ -s /run/secrets/bao-tls-crt ] && [ -s /run/secrets/bao-tls-key ] ||
    { echo "openbao: falta el certificado de OpenBao (paso 1 de la ceremonia: Ceremonia.java raiz)" >&2; exit 1; }
mkdir -p "$TLS"
cp /run/secrets/bao-tls-crt "$TLS/servidor.crt"
cp /run/secrets/bao-tls-key "$TLS/servidor.key"
chown -R openbao:openbao "$TLS"
chmod 0400 "$TLS/servidor.crt" "$TLS/servidor.key"

exec docker-entrypoint.sh server

# ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
# Propósito: prepara el contenedor openbao antes de arrancar el vault: exporta el PIN del token, crea el token
# SoftHSM solo fuera de la finca, ajusta dueños y copia el certificado HTTPS del listener.
# Uso: es el ENTRYPOINT de la etapa «servidor» del Dockerfile; corre solo en cada arranque del contenedor openbao.
# Nadie lo ejecuta a mano.
# Origen: la cabecera de arriba (ESPEC-esqueleto-tx1; HTTPS interno añadido el 1-nov).
# Línea por línea (lo no obvio):
# - #!/bin/sh: shell POSIX de Alpine (no bash): por eso no hay arreglos ni [[ ]].
# - set -eu: -e corta al primer error, -u falla si se lee una variable sin definir (no hay pipefail en sh POSIX).
# - [ -s "/run/secrets/$1" ]: -s es «existe y no está vacío»; un secret vacío cuenta como faltante.
# - cmd || { …; exit 1; }: si la prueba falla, se ejecuta el bloque entre llaves (mensaje a stderr y salida).
# - >&2: manda el mensaje a la salida de errores, que es la que muestra «docker compose logs».
# - : "${BAO_HSM_LIB:?mensaje}": «:» es la orden que no hace nada; la expansión :? aborta con el mensaje si la
#   variable falta o está vacía. Es la forma corta de exigir una variable de entorno.
# - export BAO_HSM_PIN: la deja visible al proceso hijo (el servidor y su complemento pkcs11 la leen del entorno).
# - "${FLORLOGIC_SOFTHSM_CREAR:-}": :- da cadena vacía si no existe, para que -u no aborte cuando falta la bandera.
# - softhsm2-util --show-slots | grep -q "Label: *florlogic *$": comprueba en silencio (-q) si el token ya existe;
#   el «!» de delante niega: solo se crea si NO existe.
# - [ -e "$DATOS/raft" ]: -e es «existe»; si hay datos de un vault previo y falta el token, se para en vez de crear
#   una llave nueva que nunca abriría esos datos.
# - softhsm2-util --init-token --free: inicia el token en el primer espacio libre, con su etiqueta, el PIN del oficial
#   de seguridad (--so-pin) y el PIN de uso (--pin).
# - pkcs11-tool --module … --login --keygen --key-type aes:32 --label: entra al token y genera dentro una llave AES
#   de 32 bytes (256 bits); la llave nunca sale del token.
# - >/dev/null: descarta la salida normal para no ensuciar el registro.
# - chown -R openbao:openbao: el proceso del vault corre como openbao y necesita leer y escribir tokens y datos.
# - [ -s a ] && [ -s b ] || { …; }: falla si cualquiera de los dos archivos falta o está vacío.
# - chmod 0400: solo el dueño (openbao) puede leer el certificado y su llave; nadie puede escribirlos.
# - exec docker-entrypoint.sh server: exec reemplaza este shell por el entrypoint oficial (mismo PID 1), que baja a
#   openbao y arranca «bao server»; así las señales de «docker stop» llegan directo al vault.
