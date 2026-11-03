# syntax=docker/dockerfile:1
# IA_ · Imagen del backend de la finca (CMP-3): Dockerfile multietapa escrito por el equipo, no Jib (TEC-28 AJUSTADO)
# Quién: IA (Claude), 29-oct-2026, a pedido de Juan (Segmento 1a) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-9, A-19, A-30, A-34, A-44, A-54
# Contexto de construcción: codigo/ (el pom.xml padre, contrato y backend-finca). Basta Docker: no hace falta correr
# Maven antes (A-34). Sin secretos: la clave de la base llega de OpenBao al arrancar (A-11).

# ── construccion ─────────────────────────────────────────────────────────────────────────────────────────────────
# JDK 27 (jdk-27+35, Ubuntu 26.04) por digest, nunca una etiqueta móvil (COH-42; 0 avisos en OSV el 29-oct-2026).
FROM eclipse-temurin:27-jdk@sha256:473d02d477a8152a05dc42fde10b965754f1863d45e586a97763ef298965c48b AS construccion

WORKDIR /fuente

# El reactor lee los pom.xml de los cinco módulos aunque solo construya dos: primero ellos, que cambian poco.
COPY mvnw pom.xml ./
COPY .mvn .mvn
COPY contrato/pom.xml contrato/
COPY backend-finca/pom.xml backend-finca/
COPY servicios-en-linea/pom.xml servicios-en-linea/
COPY simulador/pom.xml simulador/
COPY pruebas-extremo-a-extremo/pom.xml pruebas-extremo-a-extremo/
COPY contrato/src contrato/src
COPY backend-finca/src backend-finca/src

# La caché de BuildKit guarda ~/.m2 entre construcciones: la prueba no baja Maven ni las dependencias cada vez (COH-10).
# Las pruebas ya corrieron en «./mvnw verify»; aquí solo se empaqueta.
RUN --mount=type=cache,target=/root/.m2 \
    ./mvnw -B -q -pl backend-finca -am package -DskipTests \
 && cp backend-finca/target/backend-finca-*.jar /backend-finca.jar

# ── final ────────────────────────────────────────────────────────────────────────────────────────────────────────
# JRE 27 (jdk-27+35) por digest. No trae curl ni wget; sí openssl, con el que la salud de Compose habla HTTPS (A-49).
FROM eclipse-temurin:27-jre@sha256:0dd0465f90d398fa3226b56a3973f5d3c47ac323934dceebdab45ac8910bee6e

# Usuario de sistema sin privilegios; openbao-init le deja la credencial AppRole con este uid (A-15, A-34).
RUN groupadd --system --gid 10001 florlogic \
 && useradd --system --uid 10001 --gid florlogic --no-create-home --shell /usr/sbin/nologin florlogic

WORKDIR /app
COPY --from=construccion --chown=root:root --chmod=0444 /backend-finca.jar backend-finca.jar
# HTTPS (A-44): confía en la raíz de la finca antes de arrancar (A-54).
COPY --chown=root:root --chmod=0555 backend-finca/arranque.sh arranque.sh

USER 10001
EXPOSE 8443 8081
ENTRYPOINT ["/app/arranque.sh"]

# ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
# Propósito: construye la imagen del backend de la finca en dos etapas: compila con el JDK y deja solo el .jar sobre
# un JRE, con un usuario sin privilegios.
# Uso: lo usa compose.yaml (servicio backend, build con context: .. y dockerfile: backend-finca/Dockerfile) en
# «docker compose up --build» o «docker compose build backend»; a mano: «docker build -f backend-finca/Dockerfile .»
# desde codigo/.
# Origen: la cabecera de arriba (ESPEC-esqueleto-tx1; TEC-28 AJUSTADO: Dockerfile propio en vez de Jib).
# Línea por línea (lo no obvio):
# - # syntax=docker/dockerfile:1: primera línea especial; pide la sintaxis moderna de BuildKit (la necesita
#   RUN --mount y COPY --chmod).
# - FROM imagen@sha256:… AS construccion: etapa de compilación con nombre; el digest fija la imagen exacta.
# - WORKDIR /fuente: carpeta de trabajo (la crea si falta); los COPY y RUN siguientes son relativos a ella.
# - COPY de los pom.xml antes que src: Docker guarda cada paso en caché; si solo cambia el código, no se repiten los
#   pasos de arriba.
# - RUN --mount=type=cache,target=/root/.m2: monta una caché de BuildKit para el repositorio local de Maven que
#   sobrevive entre construcciones pero no queda dentro de la imagen.
# - ./mvnw -B -q -pl backend-finca -am package -DskipTests: -B modo por lotes (sin colores ni preguntas), -q
#   silencioso, -pl construye solo ese módulo, -am también los que necesita (contrato), package arma el .jar,
#   -DskipTests no corre pruebas.
# - FROM … (sin AS): la etapa final; lo de la etapa anterior no queda en la imagen salvo lo copiado con --from.
# - groupadd/useradd --system --uid 10001 --no-create-home --shell /usr/sbin/nologin: usuario de sistema sin carpeta
#   personal ni shell para entrar; el uid 10001 es el que espera openbao-init para las credenciales.
# - COPY --from=construccion --chown=root:root --chmod=0444: copia el .jar de la otra etapa con dueño root y solo
#   lectura para todos; el proceso (uid 10001) no puede modificarlo. 0555 en arranque.sh: leer y ejecutar.
# - USER 10001: desde aquí todo corre sin privilegios.
# - EXPOSE 8443 8081: documenta los puertos (negocio y gestión); no los publica (eso lo haría ports: en Compose).
# - ENTRYPOINT ["/app/arranque.sh"]: forma de arreglo JSON, sin shell intermedio; el script arranca la JVM.
