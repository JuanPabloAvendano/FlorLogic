#!/bin/sh
# IA_ · openbao-init: el orquestador de inicio del nodo. Inicia el vault, guarda la clave de la base y entrega al
# backend su credencial AppRole; después revoca el raíz. En las corridas siguientes solo comprueba
# Quién: IA (Claude), 29-oct-2026, a pedido de Juan (Segmento 1a: camino feliz; 1b: marcas, reanudación y
#        comprobaciones); 1-nov-2026: la CA intermedia y los certificados del nodo (HTTPS, A-44) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-12, A-15, A-24, A-25, A-35, A-36, A-40, A-41, A-42,
#                   A-44…A-51, A-52…A-60
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

# HTTPS (A-44): la carpeta de la ceremonia (FLORLOGIC_PKI del anfitrión) y un volumen de certificados por servicio.
CEREMONIA=/bao/ceremonia      # raiz.crt; aquí deja el CSR de la intermedia y aquí vuelve firmada (A-46, A-56)
RAIZ_CRT=/run/secrets/ca-raiz
EMISOR=emisor-certificados    # el AppRole de las corridas siguientes: solo emite (K-47, A-53)
ROL_PKI=servidor-finca
RENOVAR_ANTES=$((30 * 86400)) # se emite otra vez si falta o vence en menos de 30 días (COH-45, A-55)

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
    renovar
    dice "todo coincide (subred, AppRole, clave de KV, base y anfitrión); nada que cambiar"
}

# ── HTTPS: la firma de la intermedia y los certificados de cada servicio (A-44…A-51, A-52…A-60) ──────────────

# El paso 2 de la ceremonia: el operador firma el CSR fuera de línea y deja intermedia.crt en la misma carpeta. En la
# finca no se espera (FLORLOGIC_CEREMONIA_ESPERA=0): sale ≠0 con lo que hay que hacer. Las pruebas y la corrida de
# desarrollo firman mientras esperan (A-56).
esperar_firma() {
    espera="${FLORLOGIC_CEREMONIA_ESPERA:-0}"
    while [ ! -s "$CEREMONIA/intermedia.crt" ] && [ "$espera" -gt 0 ]; do
        sleep 1
        espera=$((espera - 1))
    done
    [ -s "$CEREMONIA/intermedia.crt" ] ||
        falla "falta la firma de la intermedia: firma intermedia.csr de FLORLOGIC_PKI con la raíz (java despliegue/pki/Ceremonia.java firmar <custodia> <FLORLOGIC_PKI>) y vuelve a hacer «docker compose up». El raíz sigue guardado hasta entonces"
}

# Emite el certificado de un servicio si falta, si vence en menos de 30 días o si cambiaron sus nombres (COH-45,
# A-55). Deja servidor.crt (hoja + intermedia), servidor.key (PKCS#8), raiz.crt y lo que vence, con el dueño del
# servicio, y suma el servicio a $emitidos. Sin subshell: un falla aquí tiene que parar el script.
emitir() { # servicio, nombres separados por comas, uid del dueño
    destino="/tls/$1"
    if [ -s "$destino/servidor.crt" ] && [ -s "$destino/vence" ] && [ "$(cat "$destino/nombres" 2>/dev/null)" = "$2" ] &&
        [ "$(cat "$destino/vence")" -gt "$(($(date +%s) + RENOVAR_ANTES))" ]; then
        return 0
    fi
    dns=""
    ips=""
    primero=""
    for nombre in $(printf '%s' "$2" | tr ',' ' '); do
        [ -n "$primero" ] || primero="$nombre"
        if printf '%s' "$nombre" | grep -Eq '^[0-9]{1,3}(\.[0-9]{1,3}){3}$'; then
            ips="${ips:+$ips,}$nombre"
        else
            dns="${dns:+$dns,}$nombre"
        fi
    done
    emitido="$(bao write -format=json "pki_int/issue/$ROL_PKI" common_name="$primero" alt_names="$dns" ip_sans="$ips" \
        ttl=8760h private_key_format=pkcs8)" ||
        falla "pki_int no emitió el certificado de $1: ¿la intermedia vence antes de un año? Fírmala de nuevo (COH-50)"
    escribir "$destino/servidor.key" "$(printf '%s' "$emitido" | jq -r .data.private_key)" "$3"
    escribir "$destino/servidor.crt" "$(printf '%s' "$emitido" | jq -r '.data.certificate + "\n" + .data.issuing_ca')" "$3"
    escribir "$destino/raiz.crt" "$(cat "$RAIZ_CRT")" "$3"
    escribir "$destino/vence" "$(printf '%s' "$emitido" | jq -r .data.expiration)" root
    escribir "$destino/nombres" "$2" root
    emitidos="${emitidos:+$emitidos }$1"
}

# Los tres certificados del nodo. El gateway lleva siempre localhost y 127.0.0.1 (su salud) y los nombres e IP por
# los que la finca llega al nodo (A-50).
emitir_todos() {
    emitidos=""
    emitir gateway "${FLORLOGIC_NODO_DIRECCIONES:+$FLORLOGIC_NODO_DIRECCIONES,}localhost,127.0.0.1" 101
    emitir backend backend,localhost,127.0.0.1 10001
    emitir keycloak keycloak,localhost,127.0.0.1 1000
    [ -z "$emitidos" ] ||
        dice "certificados emitidos: $emitidos. Si ya corrían: docker compose up -d --force-recreate $emitidos"
}

# Corridas siguientes, sin raíz: entra con el AppRole emisor y renueva lo que haga falta (K-47).
renovar() {
    [ -s "$ESTADO/emisor-role-id" ] && [ -s "$ESTADO/emisor-secret-id" ] ||
        falla "falta la credencial del emisor de certificados en bao-estado: reconfigúrala con generate-root (2 de 3 partes)"
    BAO_TOKEN="$(bao write -field=token auth/approle/login role_id=@"$ESTADO/emisor-role-id" \
        secret_id=@"$ESTADO/emisor-secret-id" 2>/dev/null)" ||
        falla "el AppRole $EMISOR no entra: reconfigúralo con generate-root (2 de 3 partes)"
    export BAO_TOKEN
    emitir_todos
    bao token revoke -self >/dev/null 2>&1 || true
    unset BAO_TOKEN
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

    # 5 · La intermedia de la finca: el motor pki_int con su llave dentro del vault y su CSR en la carpeta de la
    # ceremonia; el CSR no se regenera, o el certificado firmado ya no le serviría (A-45, A-46, COH-46).
    if ! hecho csr; then
        bao secrets list -format=json | jq -e 'has("pki_int/")' >/dev/null || bao secrets enable -path=pki_int pki >/dev/null
        bao secrets tune -max-lease-ttl=43800h pki_int >/dev/null     # 5 años, la vida de la intermedia (COH-50)
        bao write -format=json pki_int/intermediate/generate/internal \
            common_name="FlorLogic intermedia de la finca" key_type=ec key_bits=256 | jq -r .data.csr > "$CEREMONIA/intermedia.csr.tmp"
        chmod 0644 "$CEREMONIA/intermedia.csr.tmp"
        mv -f "$CEREMONIA/intermedia.csr.tmp" "$CEREMONIA/intermedia.csr"
        marcar csr
    fi

    # 6 · El certificado firmado por la raíz, fuera de línea. Hasta tenerlo el raíz sigue en bao-estado (COH-43, A-52)
    # y el nodo no atiende: salida ≠0, Compose no arranca lo demás (A-46).
    if ! hecho intermedia; then
        esperar_firma
        cat "$CEREMONIA/intermedia.crt" "$RAIZ_CRT" > /tmp/cadena.pem
        bao write pki_int/intermediate/set-signed certificate=@/tmp/cadena.pem >/dev/null ||
            falla "pki_int no acepta $CEREMONIA/intermedia.crt: ¿la firmó la raíz de esta finca y es del CSR que dejé?"
        rm -f /tmp/cadena.pem
        marcar intermedia
    fi

    # 7 · El rol que emite los certificados de servidor del nodo: un año, servidor y cliente (el gateway presenta el
    # suyo al backend, A-48), sin guardarlos en raft (COH-45).
    if ! hecho rol_pki; then
        bao write "pki_int/roles/$ROL_PKI" allow_any_name=true allow_ip_sans=true allow_localhost=true \
            enforce_hostnames=false server_flag=true client_flag=true key_type=ec key_bits=256 \
            ttl=8760h max_ttl=8760h no_store=true >/dev/null
        marcar rol_pki
    fi

    # 8 · El AppRole que emite en las corridas siguientes, con el raíz ya revocado: solo pki_int/issue (K-47). Su
    # credencial vive en bao-estado, que solo monta openbao-init; un solo secret_id vivo (A-42).
    if ! hecho emisor; then
        bao policy write "$EMISOR" /openbao/politica-emisor-certificados.hcl >/dev/null
        bao write "auth/approle/role/$EMISOR" token_policies="$EMISOR" secret_id_ttl=0 secret_id_num_uses=0 \
            secret_id_bound_cidrs="$FLORLOGIC_RED" token_bound_cidrs="$FLORLOGIC_RED" token_ttl=10m token_max_ttl=10m >/dev/null
        escribir "$ESTADO/emisor-role-id" "$(bao read -field=role_id "auth/approle/role/$EMISOR/role-id")" root
        for accesor in $(bao list -format=json "auth/approle/role/$EMISOR/secret-id" 2>/dev/null | jq -r '.[]'); do
            bao write "auth/approle/role/$EMISOR/secret-id-accessor/destroy" secret_id_accessor="$accesor" >/dev/null
        done
        escribir "$ESTADO/emisor-secret-id" "$(bao write -f -field=secret_id "auth/approle/role/$EMISOR/secret-id")" root
        marcar emisor
    fi

    # 9 · Los certificados del gateway, el backend y Keycloak (A-47, A-50).
    emitir_todos

    # 10 · Sin raíz vivo: un cambio posterior pide generate-root con 2 de 3 partes (A-24). Si el corte cae entre revocar
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
    rm -f "$ESTADO"/hecho-* "$ESTADO/raiz" "$ESTADO/red" "$ESTADO"/emisor-* "$ROLE_ID" "$SECRET_ID" /tls/*/vence
    # Un CSR de un vault anterior no sirve: su llave ya no existe (A-46).
    rm -f "$CEREMONIA/intermedia.csr" "$CEREMONIA/intermedia.crt"

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
