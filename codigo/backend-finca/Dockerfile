# syntax=docker/dockerfile:1
# IA_ · Imagen del backend de la finca (CMP-3): Dockerfile multietapa escrito por el equipo, no Jib (TEC-28 AJUSTADO)
# Quién: IA (Claude), 29-oct-2026, a pedido de Juan (Segmento 1a) · Estado: SIN REVISAR
# Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-9, A-19, A-30, A-34
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
# JRE 27 (jdk-27+35) por digest. No trae curl ni wget: la salud de Compose usa bash y /dev/tcp (A-30).
FROM eclipse-temurin:27-jre@sha256:0dd0465f90d398fa3226b56a3973f5d3c47ac323934dceebdab45ac8910bee6e

# Usuario de sistema sin privilegios; openbao-init le deja la credencial AppRole con este uid (A-15, A-34).
RUN groupadd --system --gid 10001 florlogic \
 && useradd --system --uid 10001 --gid florlogic --no-create-home --shell /usr/sbin/nologin florlogic

WORKDIR /app
COPY --from=construccion --chown=root:root --chmod=0444 /backend-finca.jar backend-finca.jar

USER 10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/backend-finca.jar"]
