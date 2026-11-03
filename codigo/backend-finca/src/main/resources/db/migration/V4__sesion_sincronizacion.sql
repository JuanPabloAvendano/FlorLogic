-- TX-1 · paso P1(a): la sesión de sincronización (IA_CONSTRUCCION-TX1 CT-1, CT-7). Se abre, recibe particiones y se
-- cierra, completa o parcial. La huella, las rechazadas (P1 b, c) y el celular registrado (P4) llegan en sus pasos.
CREATE TABLE sesion_sincronizacion (
    sesion_id      uuid        PRIMARY KEY,
    dispositivo_id uuid        NOT NULL,
    persona        text        NOT NULL,
    abierta_en     timestamptz NOT NULL,
    desfase_ms     bigint      NOT NULL,
    version_config integer     NOT NULL,
    estado         text        NOT NULL CHECK (estado IN ('ABIERTA', 'CERRADA', 'PARCIAL')),
    cerrada_en     timestamptz,
    CHECK ((estado = 'ABIERTA') = (cerrada_en IS NULL))
);
CREATE INDEX sesion_por_dispositivo ON sesion_sincronizacion (dispositivo_id, abierta_en);
