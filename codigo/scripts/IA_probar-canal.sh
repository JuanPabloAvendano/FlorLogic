#!/usr/bin/env bash
# IA_ · Prueba real del canal de TX-1 (camino mínimo, D-17): simulador → gateway (Nginx con su WAF) → backend-finca → PostgreSQL.
# Quién: IA (Claude), 19-oct-2026, a pedido de Juan; 20-oct-2026: pasa por el gateway; 21-oct-2026: el WAF, a pedido
#        de Jeronimo; 29-oct-2026: el nodo completo en Compose, con el vault (ESPEC-esqueleto-tx1, Segmento 1b, K-17)
#        · Estado: SIN REVISAR
# Manda por encima: codigo/contrato/espec/ESPEC-contrato-ingesta.md §8 (Segmento 2) · despliegue/ESPEC-gateway-finca.md · despliegue/ESPEC-waf-finca.md · despliegue/espec/ESPEC-esqueleto-tx1.md · IA_LEEME.md
# Uso: scripts/IA_probar-canal.sh   (desde codigo/; necesita Docker; el gateway usa el FLORLOGIC_GATEWAY_PUERTO,
#      por defecto 80; la primera vez construye las imágenes y tarda unos minutos)
set -euo pipefail
cd "$(dirname "$0")/.."

export FLORLOGIC_GATEWAY_PUERTO="${FLORLOGIC_GATEWAY_PUERTO:-80}"
GATEWAY="http://localhost:$FLORLOGIC_GATEWAY_PUERTO"
SESION=0192f0a1-7c00-7000-8000-00000000a001
DORADA=contrato/src/test/resources/dorados/peticion-particion.json
# Proyecto de compose propio (OBS-2-15): sus volúmenes no son los del nodo de desarrollo, así que borrarlos al
# terminar no toca tus datos. Subred y puerto de la base propios: no choca con el nodo de desarrollo encendido (A-20, A-35).
COMPOSE="docker compose -p florlogic-canal -f despliegue/compose.yaml"
export FLORLOGIC_RED=172.30.9.0/24 FLORLOGIC_BASE_PUERTO=0

# El secreto cero de esta corrida (A-10): inventado aquí, vive lo que dura el script y se tira con sus volúmenes.
alazar() { head -c 16 /dev/urandom | od -An -tx1 | tr -d ' \n'; }
export FLORLOGIC_PG_ADMIN="$(alazar)" FLORLOGIC_HSM_PIN="$(alazar)" FLORLOGIC_SOFTHSM_SO_PIN="$(alazar)"
export FLORLOGIC_SOFTHSM_CREAR=si BAO_HSM_LIB=/usr/lib/softhsm/libsofthsm2.so   # SoftHSM: fuera de la finca (A-29)

limpiar() { $COMPOSE down -v >/dev/null 2>&1 || true; }
trap limpiar EXIT

echo "1 · Compilando el simulador (sin pruebas)…"
./mvnw -q -DskipTests -pl simulador -am package

echo "2 · Levantando el nodo completo: base, OpenBao, openbao-init, backend y gateway (espera a que estén sanos)…"
$COMPOSE up -d --build --wait >/dev/null 2>&1 || { $COMPOSE logs openbao-init backend | tail -40; echo "El nodo no arrancó."; exit 1; }

# Desde aquí todo entra por el gateway, la única puerta del nodo de finca (C-2).
SIMULADOR="java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar"
echo "3 · Primera sincronización por el gateway (espera: 2 nuevas):"
$SIMULADOR sincronizar --url "$GATEWAY" --sesion "$SESION" --archivo "$DORADA"
echo "4 · Reenvío de la misma partición (espera: 2 ya estaban):"
$SIMULADOR sincronizar --url "$GATEWAY" --sesion "$SESION" --archivo "$DORADA"
echo "5 · Partición rota (espera: rechazada, código 1):"
echo '{ esto no es json' > /tmp/IA_canal-rota.json
$SIMULADOR sincronizar --url "$GATEWAY" --sesion "$SESION" --archivo /tmp/IA_canal-rota.json || echo "   código de salida: $?"
echo "6 · Ruta ajena por el gateway (espera: 404 gateway/rechazado; el backend no se entera):"
curl -s "$GATEWAY/actuator/health"; echo
echo "7 · Ataque por el gateway (espera: 403 gateway/bloqueado del WAF; el backend no se entera):"
sed "s/\"persona_id\": \"op-07\"/\"persona_id\": \"op-07' OR 1=1 --\"/" "$DORADA" > /tmp/IA_canal-ataque.json
CODIGO=$(curl -s -o /tmp/IA_canal-ataque.out -w '%{http_code}' -H "Content-Type: application/json" \
  --data-binary @/tmp/IA_canal-ataque.json "$GATEWAY/ingesta/v1/sesiones/$SESION/particiones")
cat /tmp/IA_canal-ataque.out; echo
# Si llegó al backend (por ejemplo, con FLORLOGIC_WAF_MODO=DetectionOnly en el entorno), el canal no está bien (OBS-1-9).
[[ "$CODIGO" == 403 ]] || { echo "El WAF no bloqueó el ataque (código $CODIGO)."; exit 1; }
echo "8 · Lo que quedó en la base:"
$COMPOSE exec -T base psql -U florlogic -d florlogic -c \
  "SELECT anotacion_uuid, consecutivo, contenido->'datos'->>'seccion_id' AS seccion FROM anotacion_recibida ORDER BY consecutivo;"
echo "Canal OK."
