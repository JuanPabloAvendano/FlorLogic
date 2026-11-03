-- TX-1 · construcción completa, fase F3 (IA_CONSTRUCCION-TX1 CT-10; ES-S13).
-- Cada celular que alguna vez abrió sesión: PENDIENTE hasta que el administrador lo aprueba o lo bloquea.
CREATE TABLE dispositivo (
    dispositivo_id        uuid        PRIMARY KEY,
    estado                text        NOT NULL CHECK (estado IN ('PENDIENTE', 'APROBADO', 'BLOQUEADO')),
    visto_por_primera_vez timestamptz NOT NULL,
    cambiado_en           timestamptz NOT NULL,
    cambiado_por          text        NOT NULL
);

-- ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
-- Propósito: el registro de cada celular que alguna vez abrió sesión y su estado de aprobación.
-- Origen: IA_CONSTRUCCION-TX1 CT-10 (dispositivos pendiente, aprobado o bloqueado) y el escenario ES-S13.
-- Lo que no está en español:
-- - CHECK (estado IN ('PENDIENTE', 'APROBADO', 'BLOQUEADO')): la base solo acepta los tres estados del dominio.
-- - timestamptz: instantes en UTC; cambiado_por guarda quién aprobó o bloqueó (el usuario del administrador).
-- Uso: Flyway 12.4.0 (TEC-08, la herramienta de migraciones aceptada) la aplica una sola vez, en orden de versión,
-- al arrancar backend-finca (Spring Boot la llama sola porque Flyway está en el classpath). El nombre
-- V<número>__<descripción>.sql es la regla de Flyway: V = migración versionada, dos guiones bajos separan la versión.
-- Flyway guarda en la tabla flyway_schema_history una suma de comprobación (checksum) de cada archivo: cambiar un
-- archivo ya aplicado, aunque sea un comentario, hace que el arranque falle en esa base hasta recrearla
-- (`docker compose down -v`) o repararla (`flyway repair`). Las pruebas no lo notan: usan bases nuevas.
