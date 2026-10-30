#!/bin/sh
# IA_ · openbao-init: el orquestador de inicio del nodo. Inicia el vault, guarda la clave de la base y entrega al
# backend su credencial AppRole; después revoca el raíz. En las corridas siguientes solo comprueba
# Quién: IA (Claude), 29-oct-2026, a pedido de Juan (Segmento 1a: camino feliz; 1b: marcas, reanudación y
#        comprobaciones) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-12, A-15, A-24, A-25, A-35, A-36, A-40, A-41, A-42
# Corre en cada «compose up» (restart: "no") y tiene que poder repetirse. Tres caminos:
#   · vault sin iniciar        → init y configuración, paso por paso, con una marca por paso (A-36)
#   · iniciado, con raíz vivo  → la configuración se cortó: sigue desde la última marca (A-36)
#   · iniciado, sin raíz       → comprueba subred, AppRole y clave, y no reescribe nada (A-25, A-40, A-41)
# Cualquier diferencia para con un mensaje que dice qué hacer; con salida ≠0 el backend no arranca (A-23).
set -eu

: "${BAO_ADDR:?falta BAO_ADDR}"
: "${FLORLOGIC_RED:?falta FLORLOGIC_RED (la subred de Docker del nodo)}"

ESTADO=/bao/estado            # solo lo monta openbao-init: el raíz mientras dura la configuración, marcas, listo y red (A-40)
ENTREGA=/bao/entrega          # solo las partes de recuperación; el operador las saca y las borra (A-24)
CREDENCIALES=/bao/credenciales  # el backend lo monta solo lectura y lo lee con configtree: (A-15, A-26)
ROLE_ID="$CREDENCIALES/spring.cloud.vault.app-role.role-id"
SECRET_ID="$CREDENCIALES/spring.cloud.vault.app-role.secret-id"
USUARIO_BACKEND=10001         # el uid del backend (A-34)
ROL=backend-finca
CLAVE_KV=spring.datasource.password
RUTA_KV=florlogic/backend-finca

falla() { echo "openbao-init: $*" >&2; exit 1; }
dice() { echo "openbao-init: $*"; }

# Escribe un archivo que solo lee su dueño, sin salto de línea al final. Primero a un temporal y después mv: un corte
# a mitad nunca deja un archivo a medias con el nombre bueno.
escribir() { # archivo, valor, dueño
    ( umask 077; printf '%s' "$2" > "$1.tmp" )
    chown "$3" "$1.tmp"
    chmod 0400 "$1.tmp"
    mv -f "$1.tmp" "$1"
}

hecho() { [ -f "$ESTADO/hecho-$1" ]; }
marcar() { escribir "$ESTADO/hecho-$1" "" root; }

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

leer_pg_admin() {
    [ -s /run/secrets/pg-admin ] || falla "falta el Compose secret pg-admin (FLORLOGIC_PG_ADMIN)"
    cat /run/secrets/pg-admin
}

# ── La subred se guarda y se compara antes que nada: el AppRole solo sirve desde ella (A-41, COH-37).
comprobar_red() {
    if [ ! -f "$ESTADO/red" ]; then
        escribir "$ESTADO/red" "$FLORLOGIC_RED" root
        return
    fi
    guardada="$(cat "$ESTADO/red")"
    [ "$guardada" = "$FLORLOGIC_RED" ] ||
        falla "la subred del nodo cambió de $guardada a $FLORLOGIC_RED: vuelve a la anterior (FLORLOGIC_RED) o reconfigura el AppRole con generate-root (2 de 3 partes)"
}

# Entra como el backend: con la credencial de solo lectura que este mismo script dejó (A-25). Imprime el token.
entrar_como_backend() {
    [ -s "$ROLE_ID" ] && [ -s "$SECRET_ID" ] || return 1
    bao write -field=token auth/approle/login role_id=@"$ROLE_ID" secret_id=@"$SECRET_ID" 2>/dev/null
}

# ── Corridas siguientes (A-25): la clave de KV, la de la base y la del anfitrión tienen que ser la misma. No reescribe
# nada: cambiar la clave es rotación, con un raíz temporal de generate-root (K-22).
comprobar() {
    comprobar_red
    token="$(entrar_como_backend)" ||
        falla "el AppRole del backend no entra con la credencial de bao-credenciales: reconfigúralo con generate-root (2 de 3 partes)"
    en_kv="$(BAO_TOKEN="$token" bao kv get -mount=kv -field="$CLAVE_KV" "$RUTA_KV")" ||
        falla "el backend no puede leer kv/$RUTA_KV: revisa la política $ROL con generate-root (2 de 3 partes)"
    BAO_TOKEN="$token" bao token revoke -self >/dev/null 2>&1 || true
    anfitrion="$(leer_pg_admin)"
    [ "$en_kv" = "$anfitrion" ] ||
        falla "FLORLOGIC_PG_ADMIN no es la clave guardada en el vault; no se reescribe nada. Vuelve a la clave anterior o rótala con generate-root (2 de 3 partes)"
    PGPASSWORD="$en_kv" PGCONNECT_TIMEOUT=5 psql -h base -U florlogic -d florlogic -tAc 'SELECT 1' >/dev/null 2>&1 ||
        falla "la base no acepta la clave guardada en el vault; no se reescribe nada. ¿Volumen datos de otra instalación? En desarrollo, docker compose down -v una vez"
    dice "todo coincide (subred, AppRole, clave de KV, base y anfitrión); nada que cambiar"
}

# ── Configuración, paso por paso (A-36). Cada paso tolera haberse cortado a la mitad la vez anterior.
configurar() {
    BAO_TOKEN="$(cat "$ESTADO/raiz")"
    export BAO_TOKEN
    comprobar_red

    # 1 · KV v2 con la clave de la base, con el nombre de la propiedad de Spring (A-19).
    if ! hecho kv; then
        reintentar bao secrets list
        bao secrets list -format=json | jq -e 'has("kv/")' >/dev/null || bao secrets enable -path=kv kv-v2 >/dev/null
        bao kv put -mount=kv "$RUTA_KV" "$CLAVE_KV=@/run/secrets/pg-admin" >/dev/null
        marcar kv
    fi

    # 2 · Política de solo lectura (A-12).
    if ! hecho politica; then
        bao policy write "$ROL" /openbao/politica-backend-finca.hcl >/dev/null
        marcar politica
    fi

    # 3 · AppRole atado a la red del nodo, sin caducidad del secret_id (A-35).
    if ! hecho approle; then
        bao auth list -format=json | jq -e 'has("approle/")' >/dev/null || bao auth enable approle >/dev/null
        bao write "auth/approle/role/$ROL" \
            token_policies="$ROL" \
            secret_id_ttl=0 secret_id_num_uses=0 \
            secret_id_bound_cidrs="$FLORLOGIC_RED" token_bound_cidrs="$FLORLOGIC_RED" \
            token_ttl=1h token_max_ttl=24h >/dev/null
        marcar approle
    fi

    # 4 · La credencial, con el nombre de la propiedad que la lee: configtree: convierte cada archivo en una
    # propiedad (A-26). Dueño 10001 y 0400 (A-15, COH-24). Un solo secret_id vivo (A-42): si ya está en el volumen no
    # se emite otro; si hay que emitirlo, antes se destruyen los anteriores.
    if ! hecho secret_id; then
        escribir "$ROLE_ID" "$(bao read -field=role_id "auth/approle/role/$ROL/role-id")" "$USUARIO_BACKEND"
        if [ ! -s "$SECRET_ID" ]; then
            for accesor in $(bao list -format=json "auth/approle/role/$ROL/secret-id" 2>/dev/null | jq -r '.[]'); do
                bao write "auth/approle/role/$ROL/secret-id-accessor/destroy" secret_id_accessor="$accesor" >/dev/null
            done
            escribir "$SECRET_ID" "$(bao write -f -field=secret_id "auth/approle/role/$ROL/secret-id")" "$USUARIO_BACKEND"
        fi
        marcar secret_id
    fi

    # 5 · Sin raíz vivo: un cambio posterior pide generate-root con 2 de 3 partes (A-24). Si el corte cae entre revocar
    # y escribir «listo», la corrida siguiente lo reconoce por el AppRole (A-40).
    bao token revoke -self >/dev/null
    unset BAO_TOKEN
    rm -f "$ESTADO/raiz"
    escribir "$ESTADO/listo" "" root
}

# ── Qué toca en esta corrida ────────────────────────────────────────────────────────────────────────────────────
# bao status sale 2 si está sin iniciar o sellado, pero igual responde: lo que cuenta es la respuesta (A-28).
iniciado=""
for _ in $(seq 1 30); do
    iniciado="$(bao status -format=json 2>/dev/null | jq -r .initialized 2>/dev/null)" || true
    [ -n "$iniciado" ] && break
    sleep 1
done

case "$iniciado" in
false)
    # Volúmenes de dos instalaciones distintas: el estado dice «listo» pero el vault está vacío.
    [ ! -f "$ESTADO/listo" ] ||
        falla "bao-estado dice «listo» pero el vault está sin iniciar: volúmenes de otra instalación. En desarrollo, docker compose down -v"
    leer_pg_admin >/dev/null
    # Marcas o credenciales que sobrevivieron a un vault borrado mentirían sobre lo hecho: se empieza limpio.
    rm -f "$ESTADO"/hecho-* "$ESTADO/raiz" "$ESTADO/red" "$ROLE_ID" "$SECRET_ID"

    # init: tres partes de recuperación, dos abren (A-24). Con el sello pkcs11 no hay llaves de unseal (A-22). El
    # raíz se guarda primero: sin él no hay forma de seguir sin generate-root.
    salida="$(bao operator init -recovery-shares=3 -recovery-threshold=2 -format=json)"
    escribir "$ESTADO/raiz" "$(printf '%s' "$salida" | jq -r .root_token)" root
    i=1
    for parte in $(printf '%s' "$salida" | jq -r '.recovery_keys_b64[]'); do
        escribir "$ENTREGA/parte-$i" "$parte" root
        i=$((i + 1))
    done
    unset salida
    configurar
    dice "vault iniciado y configurado; raíz revocado. Saca las tres partes de recuperación del volumen bao-entrega"
    ;;
true)
    if [ -f "$ESTADO/listo" ]; then
        comprobar
    elif [ -s "$ESTADO/raiz" ] && BAO_TOKEN="$(cat "$ESTADO/raiz")" bao token lookup >/dev/null 2>&1; then
        dice "la configuración anterior se cortó; sigue desde la última marca"
        configurar
        dice "configuración terminada; raíz revocado. Saca las partes de recuperación del volumen bao-entrega"
    else
        # Sin raíz vivo y sin «listo» (A-36, A-40, COH-37): primero la subred; si el AppRole entra y lee KV, la
        # configuración había terminado y solo faltaba la marca.
        rm -f "$ESTADO/raiz"
        comprobar_red
        token="$(entrar_como_backend)" ||
            falla "vault iniciado sin raíz, sin «listo» y sin un AppRole que entre: termina la configuración con generate-root (2 de 3 partes)"
        BAO_TOKEN="$token" bao kv get -mount=kv -field="$CLAVE_KV" "$RUTA_KV" >/dev/null ||
            falla "vault iniciado sin raíz ni «listo» y el AppRole no lee kv/$RUTA_KV: termina la configuración con generate-root (2 de 3 partes)"
        BAO_TOKEN="$token" bao token revoke -self >/dev/null 2>&1 || true
        escribir "$ESTADO/listo" "" root
        dice "la configuración había terminado; se reescribió «listo»"
        comprobar
    fi
    ;;
*)
    falla "el vault no responde en $BAO_ADDR"
    ;;
esac
