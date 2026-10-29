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

ENV SOFTHSM2_CONF=/etc/softhsm2.conf \
    BAO_ADDR=http://127.0.0.1:8200

# Root solo hasta arranque.sh: él crea SoftHSM si toca y docker-entrypoint.sh baja a openbao (A-37).
ENTRYPOINT ["arranque.sh"]
CMD []

# ── init ─────────────────────────────────────────────────────────────────────────────────────────────────────────
# openbao-init: el servidor más psql (la comprobación de la clave, A-25) y jq. Corre como root, sin USER, para dejar
# las credenciales con dueño 10001 y 0400 y releerlas después (A-15, A-36, COH-32).
FROM servidor AS init

RUN apk add --no-cache postgresql16-client~16 jq~1.8

COPY politica-backend-finca.hcl /openbao/politica-backend-finca.hcl
COPY --chmod=0755 init.sh /usr/local/bin/init.sh

ENTRYPOINT ["init.sh"]
