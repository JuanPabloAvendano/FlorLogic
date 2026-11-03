-- TX-1 · camino mínimo funcional (D-17, LEEME: TX-1 AJUSTAR 19-oct-2026).
-- Cada anotación recibida se guarda una vez, tal como llegó. La llave es anotacion_uuid: un reenvío no
-- inserta nada (ON CONFLICT DO NOTHING) y cuenta como sin_efecto. Huella, corrupción, reglas y marcas
-- de agua llegan después (ESPEC-contrato-ingesta §8).
CREATE TABLE anotacion_recibida (
    anotacion_uuid uuid        PRIMARY KEY,
    consecutivo    bigint      NOT NULL,
    sesion_id      uuid        NOT NULL,
    contenido      jsonb       NOT NULL,
    recibido_en    timestamptz NOT NULL DEFAULT now()
);

-- ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
-- Propósito: la tabla donde se guarda cada anotación aceptada, tal como llegó del celular.
-- Origen: camino mínimo funcional de TX-1 (D-17, el canal de punta a punta antes que las reglas finas).
-- Lo que no está en español:
-- - CREATE TABLE … ( … ): crea la tabla; cada línea es columna, tipo y restricciones.
-- - uuid: identificador de 128 bits; aquí lo genera el celular (anotacion_uuid) y por eso sirve de llave para reenvíos.
-- - PRIMARY KEY: la columna no se repite ni es nula; insertar el mismo anotacion_uuid otra vez choca, y el adaptador
--   usa ON CONFLICT DO NOTHING para que el reenvío no inserte y cuente como sin_efecto.
-- - bigint: entero de 64 bits (el consecutivo por celular no se queda corto).
-- - NOT NULL: la columna es obligatoria.
-- - jsonb: JSON guardado en forma binaria de PostgreSQL; se puede consultar por campos con operadores como ->>.
-- - timestamptz: instante con zona horaria (se guarda en UTC). DEFAULT now(): si el INSERT no lo trae, la base pone la
--   hora en que se insertó.
-- Uso: Flyway 12.4.0 (TEC-08, la herramienta de migraciones aceptada) la aplica una sola vez, en orden de versión,
-- al arrancar backend-finca (Spring Boot la llama sola porque Flyway está en el classpath). El nombre
-- V<número>__<descripción>.sql es la regla de Flyway: V = migración versionada, dos guiones bajos separan la versión.
-- Flyway guarda en la tabla flyway_schema_history una suma de comprobación (checksum) de cada archivo: cambiar un
-- archivo ya aplicado, aunque sea un comentario, hace que el arranque falle en esa base hasta recrearla
-- (`docker compose down -v`) o repararla (`flyway repair`). Las pruebas no lo notan: usan bases nuevas.
