-- TX-1 · construcción completa, fase F1 (IA_CONSTRUCCION-TX1 CT-1, CT-6; ESPEC-dominio-ingesta §5 paso 5, A-86, A-98).

-- La sesión de sincronización: se abre, recibe particiones y se cierra (completa o parcial).
CREATE TABLE sesion_sincronizacion (
    sesion_id              uuid        PRIMARY KEY,
    dispositivo_id         uuid        NOT NULL,
    persona                text        NOT NULL,
    abierta_en             timestamptz NOT NULL,
    desfase_ms             bigint      NOT NULL,
    version_config         integer     NOT NULL,
    dispositivo_registrado boolean     NOT NULL,
    estado                 text        NOT NULL CHECK (estado IN ('ABIERTA', 'CERRADA', 'PARCIAL')),
    cerrada_en             timestamptz,
    CHECK ((estado = 'ABIERTA') = (cerrada_en IS NULL))
);
CREATE INDEX sesion_por_dispositivo ON sesion_sincronizacion (dispositivo_id, abierta_en);

-- El juicio de cada aceptada: su huella (para reenvío o corrupta), su resultado y sus códigos de marca.
ALTER TABLE anotacion_recibida
    ADD COLUMN tipo         text,
    ADD COLUMN version_tipo integer,
    ADD COLUMN huella       text,
    ADD COLUMN resultado    text CHECK (resultado IN ('APLICADA', 'MARCADA')),
    ADD COLUMN codigos      text[] NOT NULL DEFAULT '{}';

-- Las rechazadas, aparte: el mismo id puede llegar rechazado con varias huellas (corrupta), cada una una sola vez.
CREATE TABLE anotacion_rechazada (
    anotacion_uuid uuid        NOT NULL,
    huella         text        NOT NULL,
    sesion_id      uuid        NOT NULL,
    consecutivo    bigint      NOT NULL,
    motivo         text        NOT NULL,
    contenido      jsonb       NOT NULL,
    recibido_en    timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY (anotacion_uuid, huella)
);

-- ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
-- Propósito: la sesión de sincronización (abrir, recibir particiones, cerrar), el juicio de cada anotación aceptada y la
-- tabla aparte de las rechazadas.
-- Origen: IA_CONSTRUCCION-TX1 CT-1 (la sesión) y CT-6 (la huella), ESPEC-dominio-ingesta A-86 (forma canónica de la
-- huella) y A-98 (choques).
-- Lo que no está en español:
-- - text: cadena sin largo máximo (en PostgreSQL rinde igual que varchar).
-- - CHECK (estado IN ('ABIERTA', 'CERRADA', 'PARCIAL')): solo se aceptan esos tres valores (el enum del dominio).
-- - CHECK ((estado = 'ABIERTA') = (cerrada_en IS NULL)): compara dos verdaderos/falsos; obliga a que una sesión
--   abierta no tenga fecha de cierre y una cerrada o parcial sí la tenga.
-- - CREATE INDEX … ON sesion_sincronizacion (dispositivo_id, abierta_en): acelera buscar las sesiones de un celular en
--   orden de apertura.
-- - ALTER TABLE … ADD COLUMN: agrega columnas a una tabla que ya existe (V2) sin perder sus filas; las nuevas quedan
--   nulas en las filas viejas, salvo codigos, que tiene DEFAULT '{}'.
-- - text[]: arreglo de cadenas (los códigos de marca de una anotación MARCADA).
-- - PRIMARY KEY (anotacion_uuid, huella): llave de dos columnas; el mismo id puede llegar rechazado con huellas
--   distintas (contenido corrupto distinto) pero cada pareja una sola vez.
-- Uso: Flyway 12.4.0 (TEC-08, la herramienta de migraciones aceptada) la aplica una sola vez, en orden de versión,
-- al arrancar backend-finca (Spring Boot la llama sola porque Flyway está en el classpath). El nombre
-- V<número>__<descripción>.sql es la regla de Flyway: V = migración versionada, dos guiones bajos separan la versión.
-- Flyway guarda en la tabla flyway_schema_history una suma de comprobación (checksum) de cada archivo: cambiar un
-- archivo ya aplicado, aunque sea un comentario, hace que el arranque falle en esa base hasta recrearla
-- (`docker compose down -v`) o repararla (`flyway repair`). Las pruebas no lo notan: usan bases nuevas.
