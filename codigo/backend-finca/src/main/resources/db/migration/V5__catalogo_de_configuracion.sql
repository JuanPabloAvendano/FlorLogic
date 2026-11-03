-- TX-1 · construcción completa, fase F2 (ESPEC-dominio-ingesta A-88: el catálogo en tablas versionadas de PostgreSQL).

-- La forma de cada tipo de anotación en cada versión: qué campos de datos se esperan y cuál trae el motivo de ausencia.
CREATE TABLE forma_tipo (
    tipo            text    NOT NULL,
    version         integer NOT NULL CHECK (version >= 1),
    campos          text[]  NOT NULL,
    motivo_ausencia text    NOT NULL,
    PRIMARY KEY (tipo, version)
);
-- captura.campo v1 (COH-164): un solo valor por anotación; si falta, el motivo va en motivo_ausencia.
INSERT INTO forma_tipo (tipo, version, campos, motivo_ausencia)
VALUES ('captura.campo', 1, ARRAY['valor'], 'motivo_ausencia');

-- Las versiones de configuración que el nodo publicó; una es la vigente (ADR-029).
CREATE TABLE version_configuracion (
    version      integer     PRIMARY KEY CHECK (version >= 1),
    publicada_en timestamptz NOT NULL DEFAULT now(),
    vigente      boolean     NOT NULL DEFAULT false
);
CREATE UNIQUE INDEX una_sola_vigente ON version_configuracion (vigente) WHERE vigente;
-- La dorada del contrato captura con la versión 12.
INSERT INTO version_configuracion (version, vigente) VALUES (12, true);

-- ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
-- Propósito: el catálogo de formas de anotación por versión y las versiones de configuración publicadas por el nodo.
-- Origen: ESPEC-dominio-ingesta A-88 (el catálogo en tablas versionadas de PostgreSQL); ADR-029 (una versión vigente).
-- Lo que no está en español:
-- - INSERT INTO … VALUES (…): deja datos iniciales dentro de la migración, para que el nodo arranque ya con un tipo.
-- - ARRAY['valor']: arreglo de text escrito como literal de PostgreSQL.
-- - CREATE UNIQUE INDEX una_sola_vigente … (vigente) WHERE vigente: índice parcial; solo indexa las filas con
--   vigente = true y como es único, no puede haber dos versiones vigentes a la vez. Las filas con false no cuentan.
-- - DEFAULT false / DEFAULT now(): valores que pone la base si el INSERT no los trae.
-- - La versión 12 vigente coincide con la que trae la petición dorada del contrato (el ejemplo fijo de las pruebas).
-- Uso: Flyway 12.4.0 (TEC-08, la herramienta de migraciones aceptada) la aplica una sola vez, en orden de versión,
-- al arrancar backend-finca (Spring Boot la llama sola porque Flyway está en el classpath). El nombre
-- V<número>__<descripción>.sql es la regla de Flyway: V = migración versionada, dos guiones bajos separan la versión.
-- Flyway guarda en la tabla flyway_schema_history una suma de comprobación (checksum) de cada archivo: cambiar un
-- archivo ya aplicado, aunque sea un comentario, hace que el arranque falle en esa base hasta recrearla
-- (`docker compose down -v`) o repararla (`flyway repair`). Las pruebas no lo notan: usan bases nuevas.
