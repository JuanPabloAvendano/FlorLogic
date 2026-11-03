-- TX-1 · construcción completa, fase F3 (IA_CONSTRUCCION-TX1 CT-11; ESC-40: registro de solo inserción).
-- Cada entrada guarda el hash de la anterior: cambiar una vieja rompe la cadena y se nota (verificación diaria).
CREATE TABLE bitacora (
    id            bigint      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    momento       timestamptz NOT NULL,
    actor         text        NOT NULL,
    evento        text        NOT NULL,
    objeto        text        NOT NULL,
    detalle       text        NOT NULL,
    hash_anterior text        NOT NULL,
    hash          text        NOT NULL UNIQUE
);

-- Solo inserción: ni el backend ni nadie con su rol puede cambiar o borrar una entrada.
CREATE FUNCTION bitacora_solo_insercion() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'bitacora: solo inserción (ESC-40)';
END $$ LANGUAGE plpgsql;
CREATE TRIGGER bitacora_solo_insercion BEFORE UPDATE OR DELETE OR TRUNCATE ON bitacora
    FOR EACH STATEMENT EXECUTE FUNCTION bitacora_solo_insercion();

-- ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
-- Propósito: la bitácora de solo inserción, encadenada por hashes para que cualquier cambio a una entrada vieja se note.
-- Origen: IA_CONSTRUCCION-TX1 CT-11 y el escenario de calidad ESC-40 (registro que no se puede reescribir).
-- Lo que no está en español:
-- - bigint GENERATED ALWAYS AS IDENTITY: la base numera cada fila sola y no deja que el INSERT ponga el número.
-- - UNIQUE: hash no se repite en toda la tabla.
-- - CREATE FUNCTION … RETURNS trigger AS $$ … $$ LANGUAGE plpgsql: función en el lenguaje procedimental de PostgreSQL
--   (plpgsql); lo que va entre $$ y $$ es su cuerpo, sin tener que escapar las comillas.
-- - RAISE EXCEPTION '…': aborta la sentencia con ese error; aquí cualquier intento de cambiar la bitácora falla.
-- - CREATE TRIGGER … BEFORE UPDATE OR DELETE OR TRUNCATE ON bitacora: antes de modificar, borrar o vaciar la tabla,
--   PostgreSQL llama la función de arriba, que siempre falla. TRUNCATE (vaciar de golpe) no dispara triggers de fila,
--   por eso el trigger es de sentencia.
-- - FOR EACH STATEMENT EXECUTE FUNCTION: se ejecuta una vez por sentencia, no una por fila.
-- - Ojo: el dueño de la tabla o un superusuario puede quitar el trigger (DROP TRIGGER); la cadena de hashes es la que
--   delata ese caso en la verificación diaria.
-- Uso: Flyway 12.4.0 (TEC-08, la herramienta de migraciones aceptada) la aplica una sola vez, en orden de versión,
-- al arrancar backend-finca (Spring Boot la llama sola porque Flyway está en el classpath). El nombre
-- V<número>__<descripción>.sql es la regla de Flyway: V = migración versionada, dos guiones bajos separan la versión.
-- Flyway guarda en la tabla flyway_schema_history una suma de comprobación (checksum) de cada archivo: cambiar un
-- archivo ya aplicado, aunque sea un comentario, hace que el arranque falle en esa base hasta recrearla
-- (`docker compose down -v`) o repararla (`flyway repair`). Las pruebas no lo notan: usan bases nuevas.
