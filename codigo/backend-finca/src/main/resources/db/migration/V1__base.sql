-- Migración vacía: marca la versión base del esquema (TEC-08).
-- Las tablas de TX-1 y TX-2 entran cuando esas transacciones estén aceptadas.
SELECT 1;

-- ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
-- Propósito: dejar la versión 1 del esquema sin tablas, para que la historia de Flyway empiece en una base conocida.
-- Origen: TEC-08 (Flyway como herramienta de migraciones).
-- Lo que no está en español:
-- - SELECT 1: consulta que no hace nada; Flyway necesita al menos una sentencia válida en el archivo.
-- Uso: Flyway 12.4.0 (TEC-08, la herramienta de migraciones aceptada) la aplica una sola vez, en orden de versión,
-- al arrancar backend-finca (Spring Boot la llama sola porque Flyway está en el classpath). El nombre
-- V<número>__<descripción>.sql es la regla de Flyway: V = migración versionada, dos guiones bajos separan la versión.
-- Flyway guarda en la tabla flyway_schema_history una suma de comprobación (checksum) de cada archivo: cambiar un
-- archivo ya aplicado, aunque sea un comentario, hace que el arranque falle en esa base hasta recrearla
-- (`docker compose down -v`) o repararla (`flyway repair`). Las pruebas no lo notan: usan bases nuevas.
