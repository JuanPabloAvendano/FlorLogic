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
