-- TX-1 · construcción completa, fase F3 (IA_CONSTRUCCION-TX1 CT-11; ESC-40: registro de solo inserción).
-- Cada entrada guarda el hash de la anterior: cambiar una vieja rompe la cadena y se nota (verificación diaria).
CREATE TABLE bitacora (
    id            bigint      GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    momento       timestamptz NOT NULL,
    actor         text        NOT NULL,
    evento        text        NOT NULL,
    objeto        text        NOT NULL,
    detalle       text        NOT NULL,
    hash_anterior text        NOT NULL,
    hash          text        NOT NULL UNIQUE
);

-- Solo inserción: ni el backend ni nadie con su rol puede cambiar o borrar una entrada.
CREATE FUNCTION bitacora_solo_insercion() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'bitacora: solo inserción (ESC-40)';
END $$ LANGUAGE plpgsql;
CREATE TRIGGER bitacora_solo_insercion BEFORE UPDATE OR DELETE OR TRUNCATE ON bitacora
    FOR EACH STATEMENT EXECUTE FUNCTION bitacora_solo_insercion();
