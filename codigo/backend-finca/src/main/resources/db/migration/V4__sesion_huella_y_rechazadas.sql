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
