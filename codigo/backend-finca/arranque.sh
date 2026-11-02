#!/bin/bash
# IA_ · Arranque del backend en el nodo: confía en la raíz de la finca y arranca Spring Boot
# Quién: IA (Claude), 1-nov-2026, a pedido de Juan (HTTPS en todo el nodo, A-44) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-44, A-48, A-54
# El backend habla HTTPS con OpenBao (Spring Cloud Vault) y con Keycloak (llaves y canje del código de la web), y los
# dos presentan certificados de la CA de la finca. La raíz llega en /tls/raiz.crt (de openbao-init) y aquí se vuelve el
# almacén de confianza de la JVM: uno solo para todos los clientes, sin desactivar nunca la verificación. Solo trae
# certificados públicos: su clave no es un secreto.
set -euo pipefail

RAIZ=/tls/raiz.crt
CONFIANZA=/tmp/confianza-finca.p12

[ -s "$RAIZ" ] || { echo "backend: falta $RAIZ (lo emite openbao-init; sin él no hay HTTPS, A-44)" >&2; exit 1; }
rm -f "$CONFIANZA"
keytool -importcert -noprompt -alias raiz-finca -file "$RAIZ" -keystore "$CONFIANZA" -storetype PKCS12 \
    -storepass confianza >/dev/null

exec java -Djavax.net.ssl.trustStore="$CONFIANZA" -Djavax.net.ssl.trustStoreType=PKCS12 \
    -Djavax.net.ssl.trustStorePassword=confianza -jar /app/backend-finca.jar
