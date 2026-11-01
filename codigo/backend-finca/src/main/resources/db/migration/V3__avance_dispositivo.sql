-- TX-1 · Segmento 1b (ESPEC-dominio-ingesta A-31, A-60, A-72, A-81, A-98).
-- El avance de cada celular: hasta dónde se confirma sin huecos y qué llegó por encima de un hueco. Una fila por
-- dispositivo. version es la versión optimista: cada guardado la sube en 1 y solo gana quien traía la de la base;
-- el que llega con una vieja choca (A-98) y su partición entera se deshace.
CREATE TABLE avance_dispositivo (
    dispositivo_id   uuid     PRIMARY KEY,
    confirmado_hasta bigint   NOT NULL CHECK (confirmado_hasta >= 0),   -- 0 = nada recibido (A-78)
    por_encima       bigint[] NOT NULL DEFAULT '{}',
    version          bigint   NOT NULL CHECK (version >= 1)
);
