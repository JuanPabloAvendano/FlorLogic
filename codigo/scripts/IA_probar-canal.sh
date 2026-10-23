#!/usr/bin/env bash
# IA_ · Prueba real del canal de TX-1 (camino mínimo, D-17): simulador → gateway (Nginx con su WAF) → backend-finca → PostgreSQL.
# Quién: IA (Claude), 19-oct-2026, a pedido de Juan; 20-oct-2026: pasa por el gateway; 21-oct-2026: el WAF, a pedido
#        de Jeronimo · Estado: SIN REVISAR
# Manda por encima: codigo/contrato/espec/ESPEC-contrato-ingesta.md §8 (Segmento 2) · despliegue/ESPEC-gateway-finca.md · despliegue/ESPEC-waf-finca.md · IA_LEEME.md
# Uso: scripts/IA_probar-canal.sh   (desde codigo/; necesita Docker; el backend usa el 18080 y el gateway el
#      FLORLOGIC_GATEWAY_PUERTO, por defecto 80)
set -euo pipefail
cd "$(dirname "$0")/.."

PUERTO=18080
export FLORLOGIC_GATEWAY_PUERTO="${FLORLOGIC_GATEWAY_PUERTO:-80}"
GATEWAY="http://localhost:$FLORLOGIC_GATEWAY_PUERTO"
SESION=0192f0a1-7c00-7000-8000-00000000a001
DORADA=contrato/src/test/resources/dorados/peticion-particion.json
BACKEND_PID=""
# Proyecto de compose propio (OBS-2-15): su volumen no es el de la base de desarrollo, así que borrarlo al
# terminar no toca tus datos. Si la base de desarrollo está encendida en el 5432, este script choca con ella.
COMPOSE="docker compose -p florlogic-canal -f despliegue/compose.yaml"

limpiar() {
  [[ -n "$BACKEND_PID" ]] && kill "$BACKEND_PID" 2>/dev/null || true
  $COMPOSE down -v >/dev/null 2>&1 || true
}
trap limpiar EXIT

echo "1 · Compilando (sin pruebas)…"
./mvnw -q -DskipTests package

echo "2 · Levantando PostgreSQL y el gateway…"
$COMPOSE up -d --wait >/dev/null

echo "3 · Arrancando backend-finca en el puerto $PUERTO…"
java -jar backend-finca/target/backend-finca-0.1.0-SNAPSHOT.jar --server.port=$PUERTO >/tmp/IA_canal-backend.log 2>&1 &
BACKEND_PID=$!
for _ in $(seq 1 60); do
  curl -sf "http://localhost:$PUERTO/actuator/health" >/dev/null && break
  sleep 1
done
curl -sf "http://localhost:$PUERTO/actuator/health" >/dev/null || { echo "El backend no arrancó; ver /tmp/IA_canal-backend.log"; exit 1; }

# Desde aquí todo entra por el gateway, la puerta del nodo de finca (la única cuando el backend entre a Compose; hoy el
# 18080 de arriba sigue abierto en el anfitrión).
SIMULADOR="java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar"
echo "4 · Primera sincronización por el gateway (espera: 2 nuevas):"
$SIMULADOR sincronizar --url "$GATEWAY" --sesion "$SESION" --archivo "$DORADA"
echo "5 · Reenvío de la misma partición (espera: 2 ya estaban):"
$SIMULADOR sincronizar --url "$GATEWAY" --sesion "$SESION" --archivo "$DORADA"
echo "6 · Partición rota (espera: rechazada, código 1):"
echo '{ esto no es json' > /tmp/IA_canal-rota.json
$SIMULADOR sincronizar --url "$GATEWAY" --sesion "$SESION" --archivo /tmp/IA_canal-rota.json || echo "   código de salida: $?"
echo "7 · Ruta ajena por el gateway (espera: 404 gateway/rechazado; el backend no se entera):"
curl -s "$GATEWAY/actuator/health"; echo
echo "8 · Ataque por el gateway (espera: 403 gateway/bloqueado del WAF; el backend no se entera):"
sed "s/\"persona_id\": \"op-07\"/\"persona_id\": \"op-07' OR 1=1 --\"/" "$DORADA" > /tmp/IA_canal-ataque.json
CODIGO=$(curl -s -o /tmp/IA_canal-ataque.out -w '%{http_code}' -H "Content-Type: application/json" \
  --data-binary @/tmp/IA_canal-ataque.json "$GATEWAY/ingesta/v1/sesiones/$SESION/particiones")
cat /tmp/IA_canal-ataque.out; echo
# Si llegó al backend (por ejemplo, con FLORLOGIC_WAF_MODO=DetectionOnly en el entorno), el canal no está bien (OBS-1-9).
[[ "$CODIGO" == 403 ]] || { echo "El WAF no bloqueó el ataque (código $CODIGO)."; exit 1; }
echo "9 · Lo que quedó en la base:"
$COMPOSE exec -T base psql -U florlogic -d florlogic -c \
  "SELECT anotacion_uuid, consecutivo, contenido->'datos'->>'seccion_id' AS seccion FROM anotacion_recibida ORDER BY consecutivo;"
echo "Canal OK."
