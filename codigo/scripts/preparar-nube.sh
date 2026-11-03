#!/usr/bin/env bash
# Prepara una sesión de Claude Code en la nube: JDK Temurin 27 y dependencias de Maven.
# Lo llama el gancho SessionStart de .claude/settings.json. En la máquina local no hace nada.
set -euo pipefail

[ "${CLAUDE_CODE_REMOTE:-}" = "true" ] || exit 0

RAIZ="$(cd "$(dirname "$0")/../.." && pwd)"
VERSION_JAVA=27
DESTINO="$HOME/.jdks/temurin-$VERSION_JAVA"

version_actual() { java -version 2>&1 | awk -F'"' '/version/ {print $2}' | cut -d. -f1; }

if [ "$(version_actual 2>/dev/null || true)" != "$VERSION_JAVA" ] && [ ! -x "$DESTINO/bin/java" ]; then
  case "$(uname -m)" in
    x86_64) ARQ=x64 ;;
    aarch64|arm64) ARQ=aarch64 ;;
    *) echo "preparar-nube: arquitectura $(uname -m) sin soporte" >&2; exit 1 ;;
  esac
  echo "preparar-nube: descargando Temurin $VERSION_JAVA ($ARQ)…" >&2
  mkdir -p "$DESTINO"
  curl -fsSL "https://api.adoptium.net/v3/binary/latest/$VERSION_JAVA/ga/linux/$ARQ/jdk/hotspot/normal/eclipse" \
    | tar -xz -C "$DESTINO" --strip-components=1
fi

if [ -x "$DESTINO/bin/java" ]; then
  export JAVA_HOME="$DESTINO"
  export PATH="$JAVA_HOME/bin:$PATH"
  # Las órdenes de Bash de la sesión heredan lo que se escriba aquí
  if [ -n "${CLAUDE_ENV_FILE:-}" ]; then
    echo "export JAVA_HOME=\"$DESTINO\"" >> "$CLAUDE_ENV_FILE"
    echo "export PATH=\"$DESTINO/bin:\$PATH\"" >> "$CLAUDE_ENV_FILE"
  fi
fi

# Baja Maven y las dependencias una vez, para que la primera construcción no gaste la sesión
cd "$RAIZ/codigo"
./mvnw -q -DskipTests -Dmaven.test.skip=true install >&2 || echo "preparar-nube: la descarga de dependencias falló; revisa la red del entorno" >&2

echo "preparar-nube: $(java -version 2>&1 | head -1) · Docker: $(docker info >/dev/null 2>&1 && echo sí || echo no, Testcontainers se salta)" >&2

# ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
# Propósito: deja lista una sesión de Claude Code en la nube: JDK Temurin 27 y las dependencias de Maven.
# Uso: lo ejecuta solo el gancho SessionStart de .claude/settings.json al abrir una sesión; en la máquina local sale
# enseguida sin hacer nada.
# Origen: la cabecera de arriba.
# Línea por línea (lo no obvio):
# - set -euo pipefail: -e corta al primer error, -u falla con variables sin definir, -o pipefail hace fallar una
#   tubería si falla cualquiera de sus partes.
# - [ "${CLAUDE_CODE_REMOTE:-}" = "true" ] || exit 0: la variable la pone Claude Code solo en la nube; :- evita que
#   -u aborte si falta.
# - RAIZ="$(cd "$(dirname "$0")/../.." && pwd)": la raíz del repositorio (dos carpetas arriba del script).
# - java -version 2>&1 | awk -F'"' '/version/ {print $2}' | cut -d. -f1: java escribe la versión en la salida de
#   errores; awk parte por comillas y toma lo de dentro, cut se queda con el número mayor (27).
# - [ ! -x "$DESTINO/bin/java" ]: -x es «existe y es ejecutable»; solo descarga si aún no está.
# - uname -m: la arquitectura de la máquina (x86_64 o aarch64), para pedir el JDK correcto.
# - curl -fsSL: -f falla con códigos HTTP de error, -s silencioso, -S igual muestra errores, -L sigue redirecciones.
#   La URL es la API de Adoptium para la última versión general (ga) del JDK 27.
# - tar -xz -C "$DESTINO" --strip-components=1: descomprime ahí quitando la carpeta de primer nivel del archivo.
# - CLAUDE_ENV_FILE: archivo que Claude Code relee para que las siguientes órdenes Bash de la sesión hereden
#   JAVA_HOME y PATH. En el segundo echo, \$PATH queda literal para expandirse después, no ahora.
# - ./mvnw -q -DskipTests -Dmaven.test.skip=true install: baja dependencias e instala sin compilar ni correr pruebas;
#   >&2 manda todo a la salida de errores para no ensuciar el contexto de la sesión.
# - docker info >/dev/null 2>&1 && echo sí || echo no: informa si hay Docker (sin él, Testcontainers se salta).
