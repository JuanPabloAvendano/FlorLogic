#!/usr/bin/env bash
# REGISTRO DE IA · NO ES FUENTE
# Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
# Estado de revisión: SIN REVISAR
# Manda por encima: IA_TECNOLOGIAS-vigentes.md · la palabra de Juan
#
# Escaneo de vulnerabilidades con Trivy (TEC-40; guía IS II §1.6 y OWASP A06 «componentes vulnerables»).
#   1. «trivy fs» sobre codigo/: dependencias Maven (pom.xml y los .jar de target/ si ya se construyó), secretos
#      escritos por error y malas configuraciones de los Dockerfile y de compose.yaml.
#   2. «trivy image» sobre las imágenes del nodo: la del backend (backend-finca/Dockerfile), las dos de OpenBao
#      (despliegue/openbao/Dockerfile, etapas servidor e init) y todas las «image:» de despliegue/compose.yaml
#      (base, gateway y las de los perfiles calidad y observabilidad).
# Severidad por defecto: HIGH,CRITICAL. Salida: codigo/target-seguridad/ (fuera de git): un .txt legible y un .json
# por objetivo, y resumen.txt con los conteos. Usa el binario «trivy» si está instalado; si no, la imagen oficial
# fijada abajo por versión y digest. Necesita Docker para las imágenes.
#
# Uso:  scripts/IA_escanear-trivy.sh                 # todo
#       scripts/IA_escanear-trivy.sh fs              # solo el sistema de archivos (rápido, sin construir imágenes)
#       FLORLOGIC_TRIVY_ESTRICTO=si scripts/…        # sale con 1 si hay algún CRITICAL (como en CI)
set -euo pipefail

CODIGO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
SALIDA="$CODIGO/target-seguridad"
SEVERIDAD="${FLORLOGIC_TRIVY_SEVERIDAD:-HIGH,CRITICAL}"
# TEC-40 · Trivy 0.76.1 por digest (la 0.69.4 fue una entrega maliciosa, GHSA-69fq-xp46-6x23: nunca una etiqueta móvil)
TRIVY_IMAGEN="aquasec/trivy:0.76.1@sha256:790fd66d1697395d8693d1acaa1ea702406a20f109b6fc7bac8db623eca6c06c"
ALCANCE="${1:-todo}"

mkdir -p "$SALIDA"

# trivy <argumentos…>: el binario local o el contenedor, con la carpeta codigo/ en /src (solo lectura), la salida en
# /salida y el socket de Docker para leer las imágenes locales sin subirlas a ningún registro.
trivy_() {
  if command -v trivy >/dev/null 2>&1; then
    (cd "$CODIGO" && trivy "$@")
  else
    docker run --rm \
      -v /var/run/docker.sock:/var/run/docker.sock \
      -v florlogic-trivy-cache:/root/.cache/trivy \
      -v "$HOME/.m2":/root/.m2:ro \
      -v "$CODIGO":/src:ro \
      -v "$SALIDA":/salida \
      -w /src \
      "$TRIVY_IMAGEN" "$@"
  fi
}

# Ruta dentro de trivy_(): /salida en el contenedor, la carpeta real con el binario local.
destino() { if command -v trivy >/dev/null 2>&1; then echo "$SALIDA/$1"; else echo "/salida/$1"; fi; }

# escanear <nombre> <subcomando y objetivo…>: un .json (para contar) y un .txt (para leer) por objetivo.
escanear() {
  local nombre="$1"; shift
  echo "· Trivy: $nombre"
  trivy_ "$@" --severity "$SEVERIDAD" --format json --output "$(destino "$nombre.json")" --quiet
  trivy_ convert --format table --output "$(destino "$nombre.txt")" "$(destino "$nombre.json")"
}

# --offline-scan: las dependencias Maven se leen del ~/.m2 local (lleno tras «./mvnw verify»), sin pedirle a Maven
# Central cada pom.xml; Central bloquea la IP 30 minutos con un 429 si se le pide demasiado (pasó el 31-oct-2026).
escanear sistema-de-archivos fs --scanners vuln,secret,misconfig --offline-scan --skip-dirs target-seguridad .

if [ "$ALCANCE" != "fs" ]; then
  # Las imágenes que construye el proyecto, con una etiqueta local que no choca con las de Compose.
  echo "· Construyendo las imágenes propias (backend y OpenBao)"
  docker build -q -f "$CODIGO/backend-finca/Dockerfile" -t florlogic-escaneo/backend-finca:local "$CODIGO" >/dev/null
  docker build -q --target servidor -t florlogic-escaneo/openbao:local "$CODIGO/despliegue/openbao" >/dev/null
  docker build -q --target init -t florlogic-escaneo/openbao-init:local "$CODIGO/despliegue/openbao" >/dev/null
  escanear imagen-backend-finca image florlogic-escaneo/backend-finca:local
  escanear imagen-openbao image florlogic-escaneo/openbao:local
  escanear imagen-openbao-init image florlogic-escaneo/openbao-init:local

  # Las imágenes de terceros que nombra compose.yaml, tal cual (con su digest cuando lo trae).
  grep -E '^[[:space:]]+image:' "$CODIGO/despliegue/compose.yaml" | awk '{print $2}' | sort -u | while read -r imagen; do
    nombre="imagen-$(echo "${imagen%%[:@]*}" | tr '/' '-')"
    escanear "$nombre" image "$imagen"
  done
fi

# El contenedor escribe como root: los informes vuelven a ser de quien corrió el script.
if ! command -v trivy >/dev/null 2>&1; then
  docker run --rm -v "$SALIDA":/salida --entrypoint chown "$TRIVY_IMAGEN" -R "$(id -u):$(id -g)" /salida
fi

# Resumen: cuántos hallazgos por severidad en cada objetivo (vulnerabilidades, secretos y malas configuraciones).
python3 - "$SALIDA" > "$SALIDA/resumen.txt" <<'EOF'
import json, pathlib, sys
salida = pathlib.Path(sys.argv[1])
total = {}
print(f"{'objetivo':40} {'CRITICAL':>8} {'HIGH':>6}")
for f in sorted(salida.glob("*.json")):
    cuenta = {}
    for r in json.loads(f.read_text()).get("Results") or []:
        for clave in ("Vulnerabilities", "Secrets", "Misconfigurations"):
            for h in r.get(clave) or []:
                cuenta[h["Severity"]] = cuenta.get(h["Severity"], 0) + 1
    for s, n in cuenta.items():
        total[s] = total.get(s, 0) + n
    print(f"{f.stem:40} {cuenta.get('CRITICAL', 0):>8} {cuenta.get('HIGH', 0):>6}")
print(f"{'TOTAL':40} {total.get('CRITICAL', 0):>8} {total.get('HIGH', 0):>6}")
EOF
cat "$SALIDA/resumen.txt"
echo "Informes: $SALIDA"

if [ "${FLORLOGIC_TRIVY_ESTRICTO:-}" = "si" ] && grep -E '^TOTAL' "$SALIDA/resumen.txt" | awk '{exit !($2 > 0)}'; then
  echo "Hay hallazgos CRITICAL (FLORLOGIC_TRIVY_ESTRICTO=si)." >&2
  exit 1
fi
