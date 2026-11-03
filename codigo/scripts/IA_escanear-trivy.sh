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

# ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
# Propósito: busca vulnerabilidades conocidas, secretos escritos por error y malas configuraciones con Trivy, en el
# código y en todas las imágenes del nodo, y deja un resumen con los conteos.
# Uso: una persona desde codigo/ (ver la cabecera), mejor después de «./mvnw verify»; con
# FLORLOGIC_TRIVY_ESTRICTO=si sale con 1 si hay algún CRITICAL, como en CI.
# Origen: la cabecera de arriba (TEC-40 es Trivy; guía IS II y OWASP A06, componentes vulnerables).
# Línea por línea (lo no obvio):
# - set -euo pipefail: -e corta al primer error, -u falla con variables sin definir, -o pipefail hace fallar una
#   tubería si falla cualquiera de sus partes.
# - CODIGO="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)": la ruta absoluta de codigo/ a partir de dónde está
#   este script, sin importar desde dónde se llame. BASH_SOURCE[0] es la ruta del propio script.
# - "${1:-todo}": el primer argumento, o «todo» si no hay.
# - command -v trivy >/dev/null 2>&1: comprueba en silencio si el binario existe en el PATH.
# - docker run --rm -v origen:destino -w /src: contenedor de un solo uso que se borra al salir (--rm), con carpetas
#   montadas (-v; :ro es solo lectura) y carpeta de trabajo /src (-w). florlogic-trivy-cache es un volumen con nombre
#   para no bajar la base de vulnerabilidades cada vez.
# - "$@": todos los argumentos tal cual, cada uno como palabra aparte.
# - local nombre="$1"; shift: variable solo de la función; shift quita el primer argumento y deja el resto en "$@".
# - trivy fs --scanners vuln,secret,misconfig --offline-scan --skip-dirs: revisa la carpeta buscando las tres
#   clases de hallazgo, sin consultar Maven Central, y sin mirar la carpeta de salida.
# - --severity --format json --output --quiet: solo esas severidades, salida JSON a un archivo, sin barra de progreso.
# - trivy convert --format table: convierte el JSON ya hecho en una tabla legible, sin volver a escanear.
# - docker build -q -f … --target … -t …: construye en silencio, con ese Dockerfile, esa etapa y esa etiqueta local.
# - grep -E '^[[:space:]]+image:' | awk '{print $2}' | sort -u | while read -r imagen: saca cada image: de
#   compose.yaml, sin repetidos, y la recorre; read -r no interpreta barras invertidas.
# - "${imagen%%[:@]*}": quita desde el primer «:» o «@» hasta el final (deja el nombre sin etiqueta ni digest).
# - --entrypoint chown … -R "$(id -u):$(id -g)": cambia el dueño de los informes al usuario que corrió el script.
# - python3 - "$SALIDA" <<'EOF' … EOF: corre Python leído de la entrada estándar («-»), con la carpeta como argumento;
#   las comillas en 'EOF' impiden que el shell sustituya nada dentro del heredoc.
# - awk '{exit !($2 > 0)}': sale con 0 (verdadero para el if) si la columna CRITICAL del TOTAL es mayor que 0.
