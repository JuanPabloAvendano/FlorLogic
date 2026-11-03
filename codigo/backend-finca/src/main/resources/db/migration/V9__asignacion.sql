-- TX-1 · construcción completa, fase F4 (IA_CONSTRUCCION-TX1 CT-13).
-- La asignación del día: qué celdas (sección × campo) le tocan a cada celular en una jornada. La define el
-- administrador en la web; es lo «esperado» del tablero. Reasignar un celular reemplaza todas sus celdas del día.
CREATE TABLE asignacion (
    jornada        date        NOT NULL,
    dispositivo_id uuid        NOT NULL REFERENCES dispositivo (dispositivo_id),
    seccion_id     text        NOT NULL,
    campo_id       text        NOT NULL,
    asignado_en    timestamptz NOT NULL,
    asignado_por   text        NOT NULL,
    PRIMARY KEY (jornada, dispositivo_id, seccion_id, campo_id)
);

-- El tablero cruza lo asignado con lo recibido por (sección, campo, jornada) y cuenta marcadas por celular y jornada.
CREATE INDEX hecho_por_celda ON hecho_produccion (seccion_id, campo_id, jornada);
CREATE INDEX trabajo_por_jornada ON trabajo_gerente (jornada, dispositivo_id);

-- ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
-- Propósito: la asignación del día (qué celdas sección × campo le tocan a cada celular) y los índices que usa el tablero.
-- Origen: IA_CONSTRUCCION-TX1 CT-13 (fase F4: consulta y web del gerente).
-- Lo que no está en español:
-- - REFERENCES dispositivo (dispositivo_id): llave foránea; solo se puede asignar a un celular que exista en la tabla
--   dispositivo (V6), y ese celular no se puede borrar mientras tenga asignaciones.
-- - PRIMARY KEY de cuatro columnas: la misma celda no se asigna dos veces al mismo celular el mismo día.
-- - CREATE INDEX hecho_por_celda / trabajo_por_jornada: aceleran las dos consultas del tablero (cruce por sección,
--   campo y jornada; conteo de marcadas por jornada y celular). No cambian resultados, solo la velocidad.
-- Uso: Flyway 12.4.0 (TEC-08, la herramienta de migraciones aceptada) la aplica una sola vez, en orden de versión,
-- al arrancar backend-finca (Spring Boot la llama sola porque Flyway está en el classpath). El nombre
-- V<número>__<descripción>.sql es la regla de Flyway: V = migración versionada, dos guiones bajos separan la versión.
-- Flyway guarda en la tabla flyway_schema_history una suma de comprobación (checksum) de cada archivo: cambiar un
-- archivo ya aplicado, aunque sea un comentario, hace que el arranque falle en esa base hasta recrearla
-- (`docker compose down -v`) o repararla (`flyway repair`). Las pruebas no lo notan: usan bases nuevas.
