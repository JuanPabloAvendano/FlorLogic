REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 por el orquestador (iteración 2)
Estado de revisión: SIN REVISAR
Manda por encima: `codigo/IA_LEEME.md` · `codigo/despliegue/compose.yaml` · la palabra de Juan

# 03 · PostgreSQL y datos

Tablas de hoy (Flyway, `backend-finca/src/main/resources/db/migration/V1…V4`): `anotacion_recibida`,
`avance_dispositivo`, `sesion_sincronizacion` y `flyway_schema_history`. Diagrama: `diagramas/IA_entidad-relacion.drawio`
(PNG al lado). Para no repetir: `Q='docker compose -f despliegue/compose.yaml exec -T base psql -U florlogic -d florlogic -c'`
y luego `$Q "<SQL>"` (o con el alias de la hoja 01: `DC exec -T base psql -U florlogic -d florlogic -c "<SQL>"`).
Varias consultas en una orden: repetir `-c` (`… -c 'SELECT 1;' -c 'SELECT 2;'`). Archivos de trabajo en `$T` (hoja 04).

**Nombres de columnas que confunden:** en la tabla la sesión es `sesion_id` (en el JSON de la API se llama
`sesion_uuid`); el celular es `dispositivo_id` (en la API, `dispositivo_uuid`). `anotacion_uuid` sí se llama igual.

### Entrar a la base dentro del contenedor (sin clave)
`DC exec base psql -U florlogic -d florlogic`
- Partes: `exec base` dentro del servicio `base` (allí la conexión local no pide clave) · `-U` usuario · `-d` base de datos · salir: `\q`
- Espera: el indicador `florlogic=#`
- Si falla: «service base is not running»: `DC start base`
- Riesgo: ninguno al entrar · Necesidad: ESENCIAL
- Probado: sin probar en forma interactiva (el probador no tiene terminal); la forma `-T … -c` funcionó (abajo)

### Una consulta sin entrar
`DC exec -T base psql -U florlogic -d florlogic -c 'SELECT count(*) FROM anotacion_recibida;'`
- Partes: `-T` sin terminal (para scripts y tuberías) · `-c '<SQL>'` corre una sola orden y sale. Comillas simples por fuera si el SQL lleva `->`; dobles si el SQL lleva comillas simples
- Espera: `count` = 2 tras una sincronización con la partición dorada
- Riesgo: ninguno · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 54) · 2-nov-2026 · iteración 2 · funcionó, también con dos `-c` (filas 15, 80, 85)

### El estado de una sesión concreta
`DC exec -T base psql -U florlogic -d florlogic -c "SELECT sesion_id, estado FROM sesion_sincronizacion WHERE sesion_id='$SES';"`
- Partes: comillas **dobles** por fuera para que la terminal ponga el valor de `$SES` dentro de las comillas simples del SQL · la columna es `sesion_id`
- Espera: `<uuid> | ABIERTA`, `CERRADA` o `PARCIAL`
- Si falla: `ERROR: column "sesion_uuid" does not exist` · `HINT: Perhaps you meant … "sesion_sincronizacion.sesion_id"` = se usó el nombre del JSON (psql mismo da la pista); `(0 rows)` = `$SES` vacío o de antes de borrar la base
- Riesgo: ninguno · Necesidad: ÚTIL (mostrar `PARCIAL` tras `completa:false`, hoja 04)
- Probado: 2-nov-2026 · iteración 2 · funcionó con `sesion_id` (`PARCIAL`); con `sesion_uuid` falló con el HINT (filas 23, 24)

### Órdenes de psql (con `-c '\dt'` o dentro de psql)
| Orden | Qué hace | Probado |
|---|---|---|
| `\dt` | lista tablas: las 4 de arriba | funcionó (fila 51) |
| `\d+ sesion_sincronizacion` | columnas, tipos, índices y restricciones de una tabla | funcionó (fila 52): 8 columnas, PK, índice `sesion_por_dispositivo`, CHECK de estado |
| `\di` | índices (5) | funcionó (fila 53) |
| `\x` | alterna salida vertical (útil con `jsonb`) | sin probar |
| `\?` / `\h DELETE` | ayuda de psql / ayuda de una orden SQL | funcionó (filas 63, 68) |
| `\q` | salir | sin probar |
- Riesgo: ninguno · Necesidad: ESENCIAL (`\dt`, `\d+`) · ÚTIL el resto
- Probado: 2-nov-2026 · iteración 1 · ver columna

### Ver lo que llegó (consultas de la demo)
```sql
SELECT anotacion_uuid, consecutivo, contenido->'datos'->>'seccion_id' AS seccion FROM anotacion_recibida ORDER BY consecutivo;
SELECT estado, count(*) FROM sesion_sincronizacion GROUP BY estado;
SELECT * FROM avance_dispositivo;
```
- Partes: `->` saca un campo `jsonb` como json · `->>` lo saca como texto · `GROUP BY estado` cuenta sesiones por estado (ABIERTA, CERRADA, PARCIAL)
- Espera: 2 filas `B3-C17-S1`, `B3-C18-S1`; sesiones `CERRADA n`; `confirmado_hasta` = 2 por dispositivo
- Riesgo: ninguno · Necesidad: ESENCIAL (muestra que el canal escribió y que el reenvío no duplicó)
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 55-57) · 2-nov-2026 · iteración 2 · funcionó: tras `completa:false` salió `CERRADA 3 · PARCIAL 1` (filas 28, 94)

### Migraciones aplicadas por Flyway
`SELECT installed_rank, version, description, success FROM flyway_schema_history;`
- Espera: V1 base … V4 sesion sincronizacion, todas `success = t`
- Riesgo: ninguno · Necesidad: ÚTIL (el esquema lo crea el backend al arrancar, no un script a mano)
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 58) · 2-nov-2026 · iteración 2 · funcionó: V1-V4 `t`, también tras restaurar (filas 98, 115)

### Demostrar una restricción: un INSERT que falla y uno que entra
```sql
-- falla: una sesión CERRADA sin cerrada_en
INSERT INTO sesion_sincronizacion (sesion_id, dispositivo_id, persona, abierta_en, desfase_ms, version_config, estado)
VALUES (gen_random_uuid(), gen_random_uuid(), 'op-demo', now(), 0, 1, 'CERRADA');
-- entra: ABIERTA sin cerrada_en
INSERT INTO sesion_sincronizacion (sesion_id, dispositivo_id, persona, abierta_en, desfase_ms, version_config, estado)
VALUES (gen_random_uuid(), gen_random_uuid(), 'op-demo', now(), 0, 1, 'ABIERTA');
-- entra: CERRADA con cerrada_en
INSERT INTO sesion_sincronizacion (sesion_id, dispositivo_id, persona, abierta_en, desfase_ms, version_config, estado, cerrada_en)
VALUES (gen_random_uuid(), gen_random_uuid(), 'op-demo', now(), 0, 1, 'CERRADA', now());
```
- Partes: `gen_random_uuid()` inventa un id · la regla es `CHECK ((estado = 'ABIERTA') = (cerrada_en IS NULL))`: abierta ⇔ sin fecha de cierre (la misma regla vive en el dominio, `SesionDeSincronizacion`)
- Espera: el primero `ERROR: new row for relation "sesion_sincronizacion" violates check constraint "sesion_sincronizacion_check"`; los otros `INSERT 0 1`
- Riesgo: ensucia la tabla: limpiar con la entrada siguiente · Necesidad: ÚTIL (integridad en la base además del código)
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 59-61)

### Borrar datos de prueba
`DELETE FROM sesion_sincronizacion WHERE persona = 'op-demo';` · una fila: `DELETE FROM anotacion_recibida WHERE consecutivo = 2;` · una partición entera: `DELETE FROM anotacion_recibida WHERE sesion_id = '<uuid>';` · todo: `TRUNCATE anotacion_recibida, avance_dispositivo, sesion_sincronizacion;`
- Espera: `DELETE 2` (las dos filas válidas de arriba) · `DELETE 1` (una fila). Que el reenvío recupera la fila borrada: hoja 09, «Borrar una fila y recuperarla reenviando»
- Riesgo: **borra datos** (solo desarrollo). `TRUNCATE` vacía las tres tablas
- Necesidad: ÚTIL (`DELETE … op-demo`, una fila) · `TRUNCATE` y por `sesion_id`: RARA · Motivo de necesidad: dejar la base vacía para repetir «2 nuevas» sin rehacer el nodo (también lo logra borrar el volumen, hoja 01)
- Probado: 2-nov-2026 · iteración 1 · funcionó el `DELETE … op-demo` (fila 62) · 2-nov-2026 · iteración 2 · funcionó `WHERE consecutivo = 2` (filas 96, 100); `TRUNCATE` y por `sesion_id` sin probar

### Respaldo y restauración manual
Respaldo: `DC exec -T base pg_dump -U florlogic --clean --if-exists florlogic > $T/respaldo.sql`
Restaurar: `DC exec -T base psql -U florlogic -d florlogic < $T/respaldo.sql`
- Partes: `pg_dump` escribe el SQL que recrea esquema y datos · `--clean --if-exists` añade los `DROP … IF EXISTS` para que restaurar sobre una base con tablas no choque · `>` lo guarda **fuera del repo** · `-T` y `<` pasan el archivo a psql por la entrada
- Espera: respaldo en ~0,1 s, ~199 líneas, 6 líneas `DROP` (`grep -cE '^DROP' $T/respaldo.sql`); al restaurar, en orden: `SET`…, `DROP INDEX` ×2, `ALTER TABLE` ×4, `DROP TABLE` ×4, `CREATE TABLE` ×4, `COPY n` ×4, `ALTER TABLE` ×4, `CREATE INDEX` ×2, sin errores. La fila borrada vuelve
- Si falla: sin `--clean`, restaurar sobre la misma base da `already exists` y llaves duplicadas
- Con el backend arriba: no hace falta pararlo; siguió respondiendo 200 después de restaurar
- Riesgo: restaurar **reemplaza** las tablas por las del respaldo · Necesidad: ÚTIL (pregunta de confiabilidad: cómo se recupera)
- Probado: 2-nov-2026 · iteración 1 · funcionó el respaldo sin `--clean` (fila 64, 190 líneas) · 2-nov-2026 · iteración 2 · funcionó respaldo con `--clean --if-exists`, `DELETE` de una fila, restauración sin errores, la fila volvió y el backend siguió en 200 (filas 95-99)

### Desde el anfitrión, sin teclear la clave
`PGPASSWORD=$(grep ^FLORLOGIC_PG_ADMIN= despliegue/.env | cut -d= -f2-) psql -h 127.0.0.1 -p 5432 -U florlogic florlogic -c 'select 1 as ok'`
- Partes: `PGPASSWORD=…` la clave solo para esta orden, leída del `.env` (no queda en el historial) · `-h 127.0.0.1` la base solo escucha en el anfitrión · `-p 5432` el puerto publicado (`FLORLOGIC_BASE_PUERTO`; en el laboratorio era 55432) · el último argumento es la base
- Espera: `ok 1`
- Si falla: `connection refused` = otro puerto o base parada; `password authentication failed` = la clave del `.env` no es la de la base (hoja 09)
- Riesgo: ninguno · Necesidad: RARA · Motivo de necesidad: conectar herramientas del anfitrión (DBeaver, draw.io no) o mostrar que la base no escucha fuera de 127.0.0.1; para la demo basta `DC exec`
- Probado: 2-nov-2026 · iteración 1 · funcionó con el puerto 55432 del laboratorio (fila 67)

### Modelo entidad-relación sin IA
1. `DC exec -T base pg_dump -U florlogic --schema-only florlogic > $T/esquema.sql`
2. draw.io › Organizar › Insertar › Avanzado › **SQL** › pegar los `CREATE TABLE` → dibuja las tablas
- Partes: `--schema-only` solo la estructura, sin datos
- Espera: 4 `CREATE TABLE`
- Riesgo: ninguno · Necesidad: ÚTIL (área 1.2 de la guía: modelado)
- Probado: 2-nov-2026 · iteración 1 · funcionó el paso 1 (fila 65); el paso 2 necesita pantalla, sin probar
