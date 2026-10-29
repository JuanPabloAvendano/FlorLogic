#!/bin/sh
# IA_ · Arranque del contenedor openbao: el PIN del token y, solo fuera de la finca, el token SoftHSM
# Quién: IA (Claude), 29-oct-2026, a pedido de Juan (Segmento 1a) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-29, A-37, A-38
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

exec docker-entrypoint.sh server
