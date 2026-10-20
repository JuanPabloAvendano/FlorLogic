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
