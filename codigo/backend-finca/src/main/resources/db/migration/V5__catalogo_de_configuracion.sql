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
