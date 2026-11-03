-- TX-1 · construcción completa, fase F2/F3 (IA_CONSTRUCCION-TX1 CT-12; P-S01, RF-022).
-- El valor vigente de cada campo de una sección en una jornada: gana la captura más reciente (hora del celular corregida
-- con el desfase de su sesión).
CREATE TABLE hecho_produccion (
    produccion_id  text        NOT NULL,
    seccion_id     text        NOT NULL,
    campo_id       text        NOT NULL,
    jornada        date        NOT NULL,
    valor          text,
    motivo_ausencia text,
    capturado_en   timestamptz NOT NULL,
    anotacion_uuid uuid        NOT NULL,
    dispositivo_id uuid        NOT NULL,
    PRIMARY KEY (produccion_id, seccion_id, campo_id, jornada)
);

-- Lo que el gerente tiene que revisar: cada anotación marcada abre un trabajo (incompleta, divergencia, hueco…).
CREATE TABLE trabajo_gerente (
    anotacion_uuid uuid        PRIMARY KEY,
    dispositivo_id uuid        NOT NULL,
    seccion_id     text,
    jornada        date,
    codigos        text[]      NOT NULL,
    abierto_en     timestamptz NOT NULL,
    resuelto_en    timestamptz,
    resuelto_por   text
);

-- ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
-- Propósito: los hechos de producción (el valor vigente de cada campo de una sección por jornada) y la cola de trabajos
-- del gerente (anotaciones marcadas por revisar).
-- Origen: IA_CONSTRUCCION-TX1 CT-12 (hechos y trabajos por evento de Spring Modulith), P-S01 y el requisito RF-022.
-- Lo que no está en español:
-- - date: solo la fecha, sin hora (la jornada).
-- - PRIMARY KEY (produccion_id, seccion_id, campo_id, jornada): un solo valor vigente por celda y jornada; el código
--   reemplaza la fila cuando llega una captura más reciente (hora del celular corregida con el desfase de su sesión).
-- - valor y motivo_ausencia sin NOT NULL: llega uno de los dos (el valor capturado o por qué no se capturó).
-- - text[] NOT NULL (codigos): los códigos de marca que abrieron el trabajo (incompleta, divergencia, hueco…).
-- - resuelto_en / resuelto_por nulos = trabajo abierto; con valor = el gerente lo resolvió.
-- Uso: Flyway 12.4.0 (TEC-08, la herramienta de migraciones aceptada) la aplica una sola vez, en orden de versión,
-- al arrancar backend-finca (Spring Boot la llama sola porque Flyway está en el classpath). El nombre
-- V<número>__<descripción>.sql es la regla de Flyway: V = migración versionada, dos guiones bajos separan la versión.
-- Flyway guarda en la tabla flyway_schema_history una suma de comprobación (checksum) de cada archivo: cambiar un
-- archivo ya aplicado, aunque sea un comentario, hace que el arranque falle en esa base hasta recrearla
-- (`docker compose down -v`) o repararla (`flyway repair`). Las pruebas no lo notan: usan bases nuevas.
