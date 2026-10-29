#!/bin/sh
# IA_ · openbao-init: el orquestador de inicio del nodo. Inicia el vault, guarda la clave de la base y entrega al
# backend su credencial AppRole; después revoca el raíz
# Quién: IA (Claude), 29-oct-2026, a pedido de Juan (Segmento 1a: camino feliz) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-12, A-15, A-24, A-35, A-36, A-40
# Corre en cada «compose up» (restart: "no") y tiene que poder repetirse. El Segmento 1b agrega las marcas por paso,
# la reanudación y las comprobaciones de las corridas siguientes (A-25, A-36, A-40…A-42).
set -eu

: "${BAO_ADDR:?falta BAO_ADDR}"
: "${FLORLOGIC_RED:?falta FLORLOGIC_RED (la subred de Docker del nodo)}"

ESTADO=/bao/estado            # solo lo monta openbao-init: el raíz mientras dura la configuración, listo y la red (A-40)
ENTREGA=/bao/entrega          # solo las partes de recuperación; el operador las saca y las borra (A-24)
CREDENCIALES=/bao/credenciales  # el backend lo monta solo lectura y lo lee con configtree: (A-15, A-26)
USUARIO_BACKEND=10001         # el uid del backend (A-34)
ROL=backend-finca

falla() { echo "openbao-init: $*" >&2; exit 1; }

# Escribe un archivo que solo lee su dueño, sin salto de línea al final.
escribir() { # archivo, valor, dueño
    ( umask 077; printf '%s' "$2" > "$1" )
    chown "$3" "$1"
    chmod 0400 "$1"
}

# La salud de openbao ya es 200, pero raft puede tardar un momento en tener líder: la primera escritura se reintenta
# con tope (A-28).
reintentar() {
    intentos=0
    until "$@" >/dev/null 2>&1; do
        intentos=$((intentos + 1))
        [ "$intentos" -lt 30 ] || { "$@"; falla "sin respuesta del vault tras 30 intentos: $*"; }
        sleep 1
    done
}

iniciado="$(bao status -format=json | jq -r .initialized)" || true

if [ "$iniciado" = "true" ]; then
    # Corridas siguientes. 1b: comprobar subred, AppRole y clave (A-25, A-41); aquí solo se reconoce lo ya hecho.
    [ -f "$ESTADO/listo" ] || falla "vault iniciado sin «listo»: la reanudación llega en el Segmento 1b; con raíz revocado, generate-root (2 de 3)"
    echo "openbao-init: el vault ya estaba configurado; nada que hacer"
    exit 0
fi

[ -s /run/secrets/pg-admin ] || falla "falta el Compose secret pg-admin (FLORLOGIC_PG_ADMIN)"

# ── 1 · init: tres partes de recuperación, dos abren (A-24). Con el sello pkcs11 no hay llaves de unseal (A-22).
salida="$(bao operator init -recovery-shares=3 -recovery-threshold=2 -format=json)"
i=1
for parte in $(printf '%s' "$salida" | jq -r '.recovery_keys_b64[]'); do
    escribir "$ENTREGA/parte-$i" "$parte" root
    i=$((i + 1))
done
escribir "$ESTADO/raiz" "$(printf '%s' "$salida" | jq -r .root_token)" root
unset salida
BAO_TOKEN="$(cat "$ESTADO/raiz")"
export BAO_TOKEN

# ── 2 · KV v2 con la clave de la base, con el nombre de la propiedad de Spring (A-19).
reintentar bao secrets enable -path=kv kv-v2
bao kv put -mount=kv florlogic/backend-finca spring.datasource.password=@/run/secrets/pg-admin >/dev/null

# ── 3 · Política de solo lectura y AppRole atado a la red del nodo, sin caducidad del secret_id (A-12, A-35).
bao policy write "$ROL" /openbao/politica-backend-finca.hcl >/dev/null
bao auth enable approle >/dev/null
bao write "auth/approle/role/$ROL" \
    token_policies="$ROL" \
    secret_id_ttl=0 secret_id_num_uses=0 \
    secret_id_bound_cidrs="$FLORLOGIC_RED" token_bound_cidrs="$FLORLOGIC_RED" \
    token_ttl=1h token_max_ttl=24h >/dev/null

# ── 4 · La credencial, con el nombre de la propiedad que la lee: configtree: convierte cada archivo en una
# propiedad (A-26). Dueño 10001 y 0400: si no, el backend no la lee y no arranca (A-15, COH-24).
escribir "$CREDENCIALES/spring.cloud.vault.app-role.role-id" \
    "$(bao read -field=role_id "auth/approle/role/$ROL/role-id")" "$USUARIO_BACKEND"
escribir "$CREDENCIALES/spring.cloud.vault.app-role.secret-id" \
    "$(bao write -f -field=secret_id "auth/approle/role/$ROL/secret-id")" "$USUARIO_BACKEND"

# ── 5 · Sin raíz vivo: un cambio posterior pide generate-root con 2 de 3 partes (A-24).
bao token revoke -self >/dev/null
unset BAO_TOKEN
rm -f "$ESTADO/raiz"
escribir "$ESTADO/red" "$FLORLOGIC_RED" root
escribir "$ESTADO/listo" "" root

echo "openbao-init: vault iniciado y configurado; raíz revocado. Saca las tres partes de recuperación del volumen bao-entrega"
