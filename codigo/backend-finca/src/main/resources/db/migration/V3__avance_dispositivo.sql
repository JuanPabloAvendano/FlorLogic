-- TX-1 · Segmento 1b (ESPEC-dominio-ingesta A-31, A-60, A-72, A-81, A-98).
-- El avance de cada celular: hasta dónde se confirma sin huecos y qué llegó por encima de un hueco. Una fila por
-- dispositivo. version es la versión optimista: cada guardado la sube en 1 y solo gana quien traía la de la base;
-- el que llega con una vieja choca (A-98) y su partición entera se deshace.
CREATE TABLE avance_dispositivo (
    dispositivo_id   uuid     PRIMARY KEY,
    confirmado_hasta bigint   NOT NULL CHECK (confirmado_hasta >= 0),   -- 0 = nada recibido (A-78)
    por_encima       bigint[] NOT NULL DEFAULT '{}',
    version          bigint   NOT NULL CHECK (version >= 1)
);

-- ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
-- Propósito: el avance de cada celular (hasta qué consecutivo se recibió sin huecos y qué llegó por encima de un hueco).
-- Origen: ESPEC-dominio-ingesta, Segmento 1b (A-98: concurrencia optimista con columna de versión).
-- Lo que no está en español:
-- - CHECK (confirmado_hasta >= 0): la base rechaza cualquier fila que no cumpla la condición, aunque el código falle.
-- - bigint[]: arreglo de enteros de PostgreSQL; '{}' es el arreglo vacío escrito como texto.
-- - version … CHECK (version >= 1): columna de bloqueo optimista; el adaptador hace UPDATE … WHERE version = la leída y
--   la sube en 1: si cambiaron 0 filas, otro guardó primero y la partición se deshace (choque, A-98).
-- Uso: Flyway 12.4.0 (TEC-08, la herramienta de migraciones aceptada) la aplica una sola vez, en orden de versión,
-- al arrancar backend-finca (Spring Boot la llama sola porque Flyway está en el classpath). El nombre
-- V<número>__<descripción>.sql es la regla de Flyway: V = migración versionada, dos guiones bajos separan la versión.
-- Flyway guarda en la tabla flyway_schema_history una suma de comprobación (checksum) de cada archivo: cambiar un
-- archivo ya aplicado, aunque sea un comentario, hace que el arranque falle en esa base hasta recrearla
-- (`docker compose down -v`) o repararla (`flyway repair`). Las pruebas no lo notan: usan bases nuevas.
