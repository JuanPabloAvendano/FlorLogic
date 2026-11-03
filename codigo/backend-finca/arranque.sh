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

# ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
# Propósito: arma el almacén de confianza de la JVM con la raíz de la finca y arranca el backend.
# Uso: es el ENTRYPOINT de la imagen del backend (backend-finca/Dockerfile); corre en cada arranque del contenedor.
# Nadie lo ejecuta a mano.
# Origen: la cabecera de arriba (ESPEC-esqueleto-tx1, HTTPS en todo el nodo).
# Línea por línea (lo no obvio):
# - #!/bin/bash: la imagen JRE trae bash; pipefail lo necesita.
# - set -euo pipefail: -e corta al primer error, -u falla con variables sin definir, -o pipefail hace fallar una
#   tubería si falla cualquiera de sus partes.
# - [ -s "$RAIZ" ]: -s es «existe y no está vacío»; sin la raíz no hay HTTPS y se sale con 1.
# - { …; exit 1; }: agrupa las órdenes que corren si la prueba falla; >&2 manda el mensaje a la salida de errores.
# - rm -f "$CONFIANZA": borra el almacén de un arranque anterior (-f no se queja si no existe); keytool fallaría si el
#   alias ya estuviera.
# - keytool -importcert -noprompt -alias raiz-finca -file -keystore -storetype PKCS12 -storepass confianza: crea un
#   almacén .p12 con solo la raíz. -importcert importa un certificado; -noprompt no pregunta «¿confía?»; -alias es el
#   nombre de la entrada; -storepass es la clave del almacén (no protege nada secreto). Va en /tmp porque /app es
#   de root y el proceso corre como 10001.
# - exec java …: exec reemplaza el shell por la JVM (queda como PID 1), así recibe directo las señales de
#   «docker stop» y se apaga ordenadamente.
# - -Djavax.net.ssl.trustStore / trustStoreType / trustStorePassword: propiedades del sistema de la JVM que fijan el
#   almacén de confianza por defecto de todos los clientes HTTPS (Spring Cloud Vault hacia OpenBao, el cliente de
#   llaves y el canje hacia Keycloak). Reemplaza al almacén de autoridades públicas del JDK.
# - -jar /app/backend-finca.jar: arranca el .jar ejecutable de Spring Boot.
