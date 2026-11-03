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
