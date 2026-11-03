# IA_ · Imagen de OpenBao del nodo de la finca: etapa «servidor» (el vault) y etapa «init» (openbao-init)
# Quién: IA (Claude), 29-oct-2026, a pedido de Juan (Segmento 1a) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-27, A-32, A-36, A-37 · IA_TECNOLOGIAS-vigentes TEC-33…36
# La misma imagen en la finca, en desarrollo y en CI (ADR-016): solo cambia la configuración (BAO_HSM_LIB y la bandera
# de SoftHSM). Contexto de construcción: despliegue/openbao/.

# ── servidor ─────────────────────────────────────────────────────────────────────────────────────────────────────
# TEC-33: OpenBao 2.7.2 por digest, nunca una etiqueta móvil (la 2.7.1 tiene el ACL bypass de AppRole).
FROM openbao/openbao:2.7.2@sha256:5046911075e137e154ce49ab051502f4ec7cf367d484ae4a4dce665d9b90561f AS servidor

USER root

# Capas estables primero, para que cambiar un .sh no repita apk ni la descarga (COH-22).
# TEC-36: fijados a la menor; Alpine solo guarda la última versión de cada paquete.
RUN apk add --no-cache gcompat~1.1 softhsm~2.7 opensc~0.26

# TEC-35: el complemento pkcs11 v0.2.1, comprobado por el sha256 oficial del binario (checksums-kms-pkcs11.txt).
# openbao.hcl repite el sha256 y OpenBao lo comprueba otra vez antes de ejecutarlo. La firma, en el Segmento 7.
ARG TARGETARCH
RUN set -eu; \
    case "$TARGETARCH" in \
      amd64) archivo=linux_amd64_v1; suma=e88632ee52600513bd0e77242823ef57641f1eaac019d8c66515c8bca81e007d ;; \
      arm64) archivo=linux_arm64_v8; suma=eeeea22ac0cc25f8ebafd1cb8b00ee27a6b2e166e67f29e13f2fe4436efc0df3 ;; \
      *) echo "arquitectura sin complemento: $TARGETARCH" >&2; exit 1 ;; \
    esac; \
    mkdir -p /openbao/plugins /tmp/complemento; \
    wget -qO- "https://github.com/openbao/openbao-plugins/releases/download/kms-pkcs11-v0.2.1/openbao-plugin-kms-pkcs11_${archivo}.tar.gz" \
      | tar -xz -C /tmp/complemento; \
    echo "$suma  /tmp/complemento/openbao-plugin-kms-pkcs11_${archivo}" | sha256sum -c -; \
    install -m 0755 "/tmp/complemento/openbao-plugin-kms-pkcs11_${archivo}" /openbao/plugins/openbao-plugin-kms-pkcs11; \
    rm -rf /tmp/complemento; \
    mkdir -p /openbao/data /var/lib/softhsm/tokens; \
    chown openbao:openbao /openbao/data /var/lib/softhsm/tokens

COPY softhsm2.conf /etc/softhsm2.conf
COPY openbao.hcl /openbao/config/openbao.hcl
COPY --chmod=0755 arranque.sh /usr/local/bin/arranque.sh

# HTTPS (A-44): el CLI confía en la raíz de la finca, que llega como Compose secret (A-46).
ENV SOFTHSM2_CONF=/etc/softhsm2.conf \
    BAO_ADDR=https://127.0.0.1:8200 \
    BAO_CACERT=/run/secrets/ca-raiz

# Root solo hasta arranque.sh: él crea SoftHSM si toca y docker-entrypoint.sh baja a openbao (A-37).
ENTRYPOINT ["arranque.sh"]
CMD []

# ── init ─────────────────────────────────────────────────────────────────────────────────────────────────────────
# openbao-init: el servidor más psql (la comprobación de la clave, A-25) y jq. Corre como root, sin USER, para dejar
# las credenciales con dueño 10001 y 0400 y releerlas después (A-15, A-36, COH-32).
FROM servidor AS init

RUN apk add --no-cache postgresql16-client~16 jq~1.8

COPY politica-backend-finca.hcl politica-emisor-certificados.hcl /openbao/
COPY --chmod=0755 init.sh /usr/local/bin/init.sh

ENTRYPOINT ["init.sh"]

# ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
# Propósito: construye las dos imágenes de OpenBao del nodo: «servidor» (el vault que guarda la clave del backend) y
# «init» (openbao-init, que lo inicia, crea políticas y AppRoles y emite certificados).
# Uso: no se construye a mano; lo hace compose.yaml (build: con target: servidor o target: init) en «docker compose up»
# o «docker compose build». El contexto es esta carpeta, por eso los COPY usan nombres sin ruta.
# Origen: la cabecera de arriba (ESPEC-esqueleto-tx1, misma imagen en finca, desarrollo y CI según ADR-016).
# Línea por línea (lo no obvio):
# - FROM … AS servidor / FROM servidor AS init: construcción en varias etapas; «init» parte de «servidor» y solo le
#   suma psql, jq, las políticas e init.sh. Compose elige la etapa con target:.
# - @sha256:…: fija la imagen por su huella; aunque alguien mueva la etiqueta 2.7.2, se baja exactamente la misma.
# - USER root: la imagen oficial corre como openbao; aquí se vuelve a root porque apk y chown lo necesitan, y
#   arranque.sh baja a openbao al final (por el docker-entrypoint.sh oficial).
# - apk add --no-cache: instala paquetes de Alpine sin guardar el índice en la imagen (imagen más liviana).
# - gcompat~1.1 softhsm~2.7 opensc~0.26: «~» fija la versión menor y deja entrar parches. gcompat permite correr el
#   complemento compilado para glibc sobre Alpine (musl); softhsm es el token PKCS#11 en software; opensc trae
#   pkcs11-tool, que arranque.sh usa para crear la llave AES.
# - ARG TARGETARCH: variable que Docker BuildKit llena sola con la arquitectura destino (amd64 o arm64); el case
#   elige el binario y su sha256 según ella.
# - set -eu dentro del RUN: -e corta al primer error, -u falla si se usa una variable sin definir.
# - wget -qO- … | tar -xz -C: descarga en silencio a la salida estándar y descomprime directo, sin archivo intermedio.
# - sha256sum -c -: lee «suma  ruta» desde la entrada estándar y falla si el binario no coincide; así un binario
#   alterado rompe la construcción.
# - install -m 0755: copia y deja permisos de ejecución en un solo paso.
# - chown openbao:openbao /openbao/data …: el usuario openbao (no root) es quien escribe datos raft y tokens.
# - COPY --chmod=0755: copia el script ya ejecutable, sin un RUN chmod aparte.
# - ENV SOFTHSM2_CONF: le dice a SoftHSM dónde está softhsm2.conf.
# - ENV BAO_ADDR=https://127.0.0.1:8200: a dónde habla el CLI «bao» dentro del contenedor (init.sh lo usa).
# - ENV BAO_CACERT=/run/secrets/ca-raiz: el CLI confía solo en la raíz de la finca; Compose monta los secrets en
#   /run/secrets/<nombre>.
# - ENTRYPOINT ["arranque.sh"] + CMD []: el contenedor siempre arranca por arranque.sh; CMD vacío evita heredar el
#   «server» de la imagen base como argumento (arranque.sh ya llama «docker-entrypoint.sh server»).
# - postgresql16-client~16: trae psql, con el que init.sh comprueba que la clave guardada sí abre PostgreSQL.
# - Sin USER en la etapa init: corre como root para dejar las credenciales con dueño 10001 (el usuario del backend)
#   y permisos 0400 (solo lectura del dueño).
