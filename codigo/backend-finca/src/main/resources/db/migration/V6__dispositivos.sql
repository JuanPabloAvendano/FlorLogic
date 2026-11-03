-- TX-1 · construcción completa, fase F3 (IA_CONSTRUCCION-TX1 CT-10; ES-S13).
-- Cada celular que alguna vez abrió sesión: PENDIENTE hasta que el administrador lo aprueba o lo bloquea.
CREATE TABLE dispositivo (
    dispositivo_id        uuid        PRIMARY KEY,
    estado                text        NOT NULL CHECK (estado IN ('PENDIENTE', 'APROBADO', 'BLOQUEADO')),
    visto_por_primera_vez timestamptz NOT NULL,
    cambiado_en           timestamptz NOT NULL,
    cambiado_por          text        NOT NULL
);
