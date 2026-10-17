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
