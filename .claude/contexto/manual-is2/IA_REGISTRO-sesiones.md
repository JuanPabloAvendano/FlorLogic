REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 (iteración 2) y el 3-nov-2026 (iteración 3, final, y cierre del proceso) por el orquestador
Estado de revisión: SIN REVISAR
Manda por encima: `README.md` de `documentacion-desarrollo` (sus diagramas mandan) · `codigo/IA_LEEME.md` · la palabra de Juan

# Registro de las sesiones de prueba

El agente de contexto limitado (solo conoce este manual y una copia del código en un laboratorio) anota cada orden
que corrió. El orquestador cuenta y sube a «Acceso rápido» las más repetidas y las imprescindibles. El registro
crudo de cada iteración queda en el scratchpad de la sesión (no en el repo).

## Conteo acumulado

Por forma general: las variantes cuentan juntas (todos los `DC logs …` en una fila). Cada fila del registro crudo
cuenta una vez, en la forma de su orden principal. «Funcionó» cuenta las que dieron lo esperado, incluidas las fallas
esperadas (los síntomas de la hoja 09); las que no, o las «parciales», son errores del manual ya corregidos. Total de
las tres iteraciones: **380 órdenes en 52 formas** (iteración 1: 159 en 42; iteración 2: 138, 8 formas nuevas;
iteración 3: 83, 2 formas nuevas: `DC wait` y `docker inspect`). Funcionaron 351 de 380.

| Orden (forma general) | It. 1 | It. 2 | It. 3 | Acumulado | Funcionó | Necesidad |
|---|---|---|---|---|---|---|
| `curl --cacert …` (abrir sesión, partición, cierre, códigos, errores, WAF; `-i`, `-sS`) | 19 | 29 | 7 | **55** | 54 | ESENCIAL |
| `DC exec -T base psql\|pg_dump …` (también `-x`, `-At`, restaurar con `<`) | 17 | 15 | 9 | **41** | 40 | ESENCIAL |
| `DC logs [--tail=N] <servicio> [\| grep]` | 12 | 10 | 4 | **26** | 22 | ESENCIAL |
| `DC ps [-a] [--format …]` | 6 | 9 | 4 | **19** | 16 | ESENCIAL |
| `cp` / `cmp` / `diff -rq` (copia y restauración de lo dañado) | 8 | 8 | 2 | **18** | 18 | ÚTIL (hoja 09) |
| `DC up -d [--wait] [<servicio>]` (sin `--build`) | 10 | 5 | 3 | **18** | 15 | ESENCIAL |
| `sed -i` / `echo >>` (dañar o preparar un archivo) | 8 | 5 | 2 | **15** | 15 | ÚTIL (hoja 09) |
| `DC up -d --force-recreate --no-deps [--wait] <servicio>` | 2 | 6 | 5 | **13** | 11 | ESENCIAL (con `openbao-init`, `DC wait` detrás) |
| `docker images \| grep <proyecto>` (o `DC images`, `--filter dangling=true`, `--tree`) | 6 | 1 | 6 | **13** | 11 | ÚTIL |
| `DC exec -T <servicio> <shell> -c …` | 6 | 4 | 2 | **12** | 11 | ÚTIL |
| `java -jar simulador… sincronizar` | 3 | 4 | 3 | **10** | 10 | ESENCIAL |
| `DC up -d --build [--no-deps] [--wait] [<servicio>]` | 5 | 2 | 2 | 9 | 9 | ESENCIAL |
| `./mvnw [-pl <módulo> -am] verify` (con o sin `env -u`) | 4 | 3 | 1 | 8 | 7 | ESENCIAL |
| `./mvnw … -Dtest=<Clase> verify` | 3 | 3 | 2 | 8 | 8 | ESENCIAL (`CapasTest,ModulosTest`) |
| `DC config` (`--services`, `-q`, `grep -m1 '^name'`) | 3 | 2 | 2 | 7 | 6 | ÚTIL |
| `DC stop <servicio>` | 3 | 2 | 1 | 6 | 6 | ESENCIAL |
| `docker compose ls [-a]` · `docker ps -a --filter label=…` | 2 | 1 | 2 | 5 | 5 | ÚTIL |
| `openssl s_client -connect … -CAfile … [2>&1]` | 1 | 2 | 2 | 5 | 3 | ESENCIAL |
| preparar la terminal (`export …`, `DORADA=…`, `G`, `C`, `T`, abrir `SES`) | 0 | 2 | 3 | 5 | 5 | ESENCIAL |
| `DC start <servicio>` | 3 | 1 | 0 | 4 | 4 | ESENCIAL |
| `docker volume ls\|inspect\|rm` | 3 | 1 | 0 | 4 | 4 | ÚTIL |
| `DC exec -T openbao bao status` | 2 | 1 | 1 | 4 | 4 | ESENCIAL |
| `java despliegue/pki/Ceremonia.java raiz` | 1 | 2 | 1 | 4 | 4 | ESENCIAL |
| `./mvnw -q -DskipTests [-pl simulador -am] package` | 1 | 1 | 2 | 4 | 4 | ESENCIAL (simulador) · RARA (todo) |
| `docker inspect` · `docker image inspect` · `docker info` · nueva | 0 | 0 | 4 | 4 | 4 | RARA (solo para explicar el `sha256:…`) |
| `java -version` · `./mvnw -v` · `mise current` | 3 | 0 | 0 | 3 | 3 | ÚTIL |
| `openssl x509 -in <crt> -noout …` | 3 | 0 | 0 | 3 | 3 | ÚTIL |
| `grep` en código o script | 3 | 0 | 0 | 3 | 2 | ÚTIL |
| `./mvnw … dependency:tree` | 2 | 1 | 0 | 3 | 2 | ÚTIL |
| `docker network ls\|inspect` | 2 | 1 | 0 | 3 | 3 | ÚTIL |
| `DC rm -sf <servicio>` | 2 | 1 | 0 | 3 | 3 | ÚTIL |
| `scripts/IA_probar-canal.sh` (con o sin `FLORLOGIC_GATEWAY_PUERTO`) | 1 | 2 | 0 | 3 | 2 | ESENCIAL |
| `java despliegue/pki/Ceremonia.java firmar` | 1 | 1 | 1 | 3 | 3 | ESENCIAL |
| `DC down -v --rmi local` | 1 | 1 | 1 | 3 | 3 | ESENCIAL |
| `FLORLOGIC_WAF_MODO=DetectionOnly DC up -d [--wait] gateway` | 1 | 1 | 1 | 3 | 3 | ÚTIL |
| `docker stats --no-stream [$(DC ps -q)]` | 1 | 1 | 1 | 3 | 3 | ÚTIL |
| `rm -r <custodia> <pki>` (borrar la CA) | 0 | 2 | 1 | 3 | 3 | ESENCIAL (vuelta a cero) |
| `DC restart <servicio>` | 0 | 2 | 1 | 3 | 3 | ÚTIL |
| `head -4\|-5\|-6` · `sed -n '2,/^[^#]/p'` (cabecera `IA_`) | 0 | 1 | 2 | 3 | 1 | ÚTIL (en `.sh`, el `sed`) |
| `docker version` · `docker compose version` | 2 | 0 | 0 | 2 | 2 | ÚTIL |
| `java -jar simulador… [sincronizar] --help` | 2 | 0 | 0 | 2 | 2 | ÚTIL |
| `docker rmi <imagen>` | 2 | 0 | 0 | 2 | 1 | ÚTIL |
| `DC down` (sin `-v`) | 0 | 2 | 0 | 2 | 2 | ESENCIAL |
| `DC wait <servicio>` · nueva | 0 | 0 | 2 | 2 | 2 | ESENCIAL (para `openbao-init`, hoja 09) |
| recuento de cero (`docker images\|volume\|ps\|network` con `grep -c`/`wc -l`) | 0 | 1 | 1 | 2 | 2 | ÚTIL |
| `DC port <servicio> <puerto>` | 0 | 1 | 1 | 2 | 1 | PRESCINDIBLE (`:0` críptico en el backend; en el gateway sí dice `0.0.0.0:<puerto>`) |
| `ls -l <pki> <custodia>` · `ls $LAB` | 1 | 0 | 1 | 2 | 2 | ÚTIL |
| `ls …/surefire-reports` | 1 | 0 | 0 | 1 | 1 | ÚTIL |
| `psql -h 127.0.0.1 -p <puerto>` (anfitrión) | 1 | 0 | 0 | 1 | 1 | RARA |
| `lazydocker --help` | 0 | 1 | 0 | 1 | 0 | RARA (no tiene modo sin pantalla) |
| `which psql` | 1 | 0 | 0 | 1 | 1 | PRESCINDIBLE (solo comprobó que psql está instalado) |
| `find … -newer` (archivos tocados) | 1 | 0 | 0 | 1 | 1 | PRESCINDIBLE (control del laboratorio) |

**Top 10 acumulado (tres iteraciones):** `curl` 55 · `psql`/`pg_dump` 41 · `DC logs` 26 · `DC ps -a` 19 · `cp`/`cmp` 18 ·
`DC up -d [--wait]` 18 · `sed`/`echo` 15 · `--force-recreate` 13 · `docker images` 13 · `DC exec` (otro servicio) 12.
Les siguen el simulador (10) y `up --build` (9).

## Iteraciones

### Iteración 1 · 2-nov-2026

- **Quién y dónde:** agente probador de contexto limitado, en una copia sin git del código (`lab/codigo`), proyecto
  Compose `florlogic-manual`, gateway en 9443, base en 55432, red 172.30.42.0/24. Docker 29.9.2, Compose 5.6.1,
  Temurin 27, Maven 3.9.16.
- **Qué se probó (12 tareas, 159 órdenes):** herramientas; `./mvnw verify` completo (194 pruebas con Testcontainers,
  0 saltadas), una clase, un módulo, árbol; la ceremonia y el arranque en dos pasos; estado, logs, exec, imágenes,
  red, volúmenes, consumo; el simulador (2 nuevas · 2 ya estaban) y el canal a mano con curl (abrir, partición,
  cerrar, códigos); la base (tablas, CHECK, inserciones, borrado, respaldo, Flyway, psql del anfitrión); WAF On y
  DetectionOnly; certificados, cadena TLS, vault y mTLS; seis ejercicios de romper y reparar; borrar y reconstruir
  la imagen del backend; el script del canal; vuelta a cero.
- **Qué falló (errores del manual):**
  1. `./mvnw verify` cae en `SimuladorAplicacionTest` si `FLORLOGIC_CA_RAIZ` apunta a un archivo que no existe.
  2. `DC up -d gateway` no recrea tras editar la plantilla montada: no hubo síntoma.
  3. Secreto cero: `DC up` no para si los contenedores ya existen; `DC config -q` tampoco avisa.
  4. Base apagada: `DC ps` sin `-a` no la muestra; el síntoma es `504` del gateway, no un error del backend.
  5. Los títulos que se disparan con curl están en `Problemas.java`, no en `ManejadorDeErrores`.
  6. El mensaje real de `docker rmi` es `conflict: unable to delete … (must be forced) - container … is using its referenced image …`.
  7. El log del WAF no deja leer `ruleId` sin filtrar el JSON.
  8. `dependency:tree` con `-q` no imprime nada.
  9. `IA_probar-canal.sh` usa el mismo puerto que el nodo propio y no borra sus imágenes.
  10. `DC images` muestra `<none>` para las imágenes construidas.
- **Qué se corrigió (orquestador):** las diez, con la orden comprobada, en las hojas 01, 02, 04, 05, 06 y 09; además
  se reescribió «Desajustar la clave de la base y el vault» (borrar solo el volumen `datos` no produce el choque:
  la base se recrea con la misma clave; el choque sale al cambiar `FLORLOGIC_PG_ADMIN`, leído en `openbao/init.sh`,
  sin probar) y la restauración del respaldo (con `--clean --if-exists`, sin probar).
- **Faltantes añadidos:** los cuerpos JSON de abrir y cerrar sesión, `date +%s%3N`, capturar el id con `jq`, las
  variables `G`, `C`, `T`, `DORADA`; `--wait`, `--force-recreate`, `DC rm -sf`, `docker compose ls -a`,
  `DC config --services`, `docker network inspect --format`; psql del anfitrión con `PGPASSWORD`; inserciones
  válidas y su borrado; `-showcerts` e intermedia; `/run/secrets`; el `.env` del secreto cero; los errores 404/422
  disparables con curl; el modo del WAF dentro del contenedor; la tabla «dónde se ve cada área» en el índice.
- **Diagramas:** etiquetas del entidad-relación separadas; nuevo modelo de dominio de `ingesta`; mapa de órdenes
  al día. PNG reexportados.
- **Sin resolver, para la iteración 2:** `DC restart` con plantilla rota; choque de puerto del script; desajuste
  de claves; restaurar el respaldo; captura con `jq`; `grep SecRuleEngine` en el contenedor; borrar la CA y la
  secuencia de la demo de un tirón; imágenes `florlogic-canal-*` (~1,2 GB) aún en la máquina.

### Iteración 2 · 2-nov-2026

- **Quién y dónde:** el mismo probador de contexto limitado y el mismo laboratorio (`florlogic-manual`, 9443, 55432,
  172.30.42.0/24), con Docker encendido (pruebas de Testcontainers de verdad, 0 saltadas). Variables de la hoja 04 en
  un archivo `vars.sh` del laboratorio.
- **Qué se probó (14 tareas, 138 órdenes):** la Secuencia de la demostración de un tirón **solo con el índice**
  (funcionó; ~40 s sin `verify`, 3 min 27 s el `verify`); la trampa de `FLORLOGIC_CA_RAIZ` y su corrección; el canal
  a mano con captura de `SES` por `jq`, `completa:false` → `PARCIAL`, los errores 404/422; WAF On/DetectionOnly con el
  `grep` de `SecRuleEngine`; gateway roto con `--force-recreate` y con `DC restart`; secreto cero; clave de la base
  desajustada y reparada; borrar el volumen de la base; base apagada con `time`; respaldo y restauración; borrar una
  fila y recuperarla reenviando; el script del canal con el puerto ocupado y con 9444; seis variantes del evaluador
  (puertos del backend, clave de la base, consumo, cadena TLS, migraciones, backend apagado); la tabla de áreas;
  vuelta a cero verificada (0 imágenes, volúmenes, contenedores y redes; `diff -rq` del laboratorio sin diferencias).
- **Qué falló (errores del manual) y cómo se corrigió:**
  1. `FLORLOGIC_WAF_MODO=DetectionOnly DC up -d gateway` y el ataque enseguida dio `000` (curl rc 35): faltaba `--wait`
     (hoja 04). Regla general nueva en el índice y la hoja 01: todo `up -d` seguido de una comprobación lleva `--wait`.
  2. «`DC restart gateway` no relee la plantilla» era falso: **sí la relee** (rompe y repara). Hojas 01 y 09, con la
     tabla «qué relee cada orden» (plantilla montada ≠ variable de entorno ≠ secreto).
  3. «Desajustar la clave»: sin `--wait` la orden sale 0 y `openbao-init` parece `Up`; con `--wait` sale 1 en el acto.
     Hoja 09 reescrita y marcada como probada.
  4. El `grep` de `openssl s_client` no filtraba: `depth`, `verify return` y `DONE` van por la salida de errores.
     Corrección `2>&1` (hoja 05, chuleta).
  5. Script del canal con el puerto ocupado: no dice «port is already allocated»; muestra logs sanos de `openbao-init`
     y `El nodo no arrancó.`. Hoja 06: síntoma engañoso explicado y `FLORLOGIC_GATEWAY_PUERTO=9444` como forma principal.
  6. Base apagada: el 504 tarda ~22 s; con `DC up -d --wait base` el 200 es inmediato (hoja 09).
  7. `head -4` en un `.sh` corta la cabecera (la línea 1 es el `#!`): `head -5` (índice, hoja 07).
  8. El script reconstruye sus imágenes en cada corrida y deja colgadas las anteriores (hoja 06 y «Avisos» del índice).
- **Faltantes añadidos:** el estado de una sesión concreta con la columna `sesion_id` (hoja 03); dónde está la clave de
  la base (`/run/secrets/pg-admin` en `base`, no en `openbao`; hoja 05); mostrar que el backend no publica puertos con
  `DC ps -a --format` y `curl` al 8443 (hoja 01; `DC port` queda PRESCINDIBLE); borrar una fila y recuperarla
  reenviando, «1 nuevas · 1 ya estaban» (hoja 09); una sesión `parcial` no pasa a cerrada y una partición a una sesión
  cerrada da 404 (hoja 04); restaurar con el backend arriba no rompe nada (hoja 03); los códigos de salida de curl
  7/35/56/60 (hoja 04); la fecha fija del jar del simulador por `project.build.outputTimestamp` (hoja 06);
  `lazydocker -f … -p …` (hoja 10, sin probar: interactiva); `openbao-init` se re-corre al tocar gateway o backend, y
  `sha256:…` en IMAGE es normal (hoja 01).
- **Acceso (de la «Fricción»):** nueva `IA_CHULETA.md` (una página: preparar la terminal con apertura de `SES`,
  16 órdenes, tiempos medidos, «Si sale…»); índice con enlaces a la entrada exacta en «Acceso rápido» y en «Si el
  evaluador pide…»; «Acceso rápido» reordenado por imprescindible y frecuencia acumulada; rutas de trabajo unificadas
  en `$T`; la Secuencia confirma la falla del primer `up` con `DC logs --tail=10 openbao-init` y avisa de borrar la CA
  antes.
- **Sin resolver, para la iteración 3:** por qué el segundo `up --build` deja `sha256:…` en IMAGE; el `--wait` en la
  primera línea del WAF tal cual; `DC restart` con una variable nueva; `head -5`; las tres dudas de comportamiento para
  Juan (índice, «Avisos»); lazydocker y draw.io (necesitan pantalla); las órdenes de git (el laboratorio no tiene git).

### Iteración 3 · 2 y 3-nov-2026 (final)

- **Quién y dónde:** el mismo probador de contexto limitado y el mismo laboratorio (`florlogic-manual`, 9443, 55432,
  172.30.42.0/24), con Docker encendido. Esta vez como **estudiante sin IA**: solo la chuleta, «Acceso rápido» y
  «Si el evaluador pide…» del índice, y las hojas a las que llevan (cada salto anotado). Hubo un corte de ~11 h por
  límite de uso entre las filas 31 y 32: el nodo quedó con el gateway en bucle y se reparó con la orden del manual.
- **Qué se probó (83 órdenes):** la Secuencia de cero al canal con la chuleta (funcionó de un tirón; `verify` 20 min
  33 s); la duda del `sha256:…`; WAF `DetectionOnly` con `--wait` en la primera línea y `DC restart` con una variable
  nueva; la clave de la base desajustada; `/run/secrets`; cierre parcial; `head` en un script; base apagada; borrar
  una fila, respaldo, borrar por `sesion_id`, restaurar y `TRUNCATE`; `psql -x`; `DC port gateway`; vuelta a cero
  con recuento de redes; y el examen en frío.
- **Examen en frío (10 peticiones sorteadas de 20: 8, 10, 1, 20, 9, 11, 3, 4, 5, 15):** **10 de 10 resueltas, todas con
  ≤ 2 saltos** (meta: ≥ 9 de 10). Media **1 salto y 1,8 órdenes** por petición. Solo con la chuleta, sin abrir hoja:
  **P8** (vault sellado, chuleta #13) y **P10** (cadena TLS, chuleta #14). Dos necesitaron algo de fuera: P4 (`curl -i`
  para ver quién responde) y P9 (el `sed` del daño de capas, que la hoja daba en prosa). P3 (migraciones) se encontró
  por la palabra «Flyway» en la tabla de hojas, sin enlace directo. «Si el evaluador pide…» fue el enlace más usado
  (7 de 10).
- **Qué falló (errores del manual) y cómo se corrigió:**
  1. Hoja 09 «Desajustar la clave»: `DC up -d --force-recreate --no-deps --wait openbao-init` sale **0** en 1,4 s con
     `Healthy` y logs vacíos; el `Exited (1)` llega a los 9 s (filas 51-54). Con un servicio de una sola vez sin
     healthcheck, `--wait` lo da por listo al arrancar. Corrección comprobada: `DC up -d --force-recreate --no-deps
     openbao-init; DC wait openbao-init` → rc 1 con el mensaje en ~2 s; reparado, rc 0 y «todo coincide» (filas
     56-57). Hoja 09 reescrita, entrada nueva en la hoja 01, y la regla del `--wait` del índice, la hoja 01 y la
     chuleta con la excepción «servicios de una sola vez: `DC wait`».
  2. Hoja 07 / índice 1.8: `head -5` corta `# Manda por encima:` (línea 6 de `IA_probar-canal.sh`, fila 61). Ahora
     `head -6` para ese script y `sed -n '2,/^[^#]/p'` para cualquiera (fila 62).
  3. `./mvnw verify` tardó **20 min 33 s**, no ~3,5 min (fila 6): un caso de `NodoArranqueSeguroTest` (`c5_sin_vault…`)
     1060 s. Hoja 02, índice y chuleta lo dicen como rango (3,5 a 20,5 min) y la hoja 02 da cómo saltar esa clase
     (`-Dtest='!NodoArranqueSeguroTest'` con `-Dsurefire.failIfNoSpecifiedTests=false`, **sin probar**).
  4. Chuleta #14 copiada del archivo sin renderizar: el `\|` de la tabla rompe la orden (`s_client: Extra option`,
     fila 28). Las órdenes con tubería salieron de las tablas a bloques de código (chuleta, índice 1.1, hoja 07,
     hoja 10); fila nueva en «Si sale…».
  5. Menores: respaldo de 202 líneas (no ~199); conteo de `ALTER TABLE` al restaurar; «Espera» de «backend sin
     puertos» sin `8080/tcp` ni `[::]`; pruebas de capas en 12,6 s (no ~7). Corregidos en las hojas 01, 02 y 03.
- **Faltantes añadidos:** el `sed` concreto del daño de capas (hoja 09); `$C -i` para ver que responde Nginx
  (`server: nginx`, `gateway/rechazado`) y `DC logs --since 30s backend | grep -c actuator` → 0 (hoja 04, índice 1.3);
  `curl -sS` para que el rc 60 diga su motivo (hoja 04); qué significan `Seal Type pkcs11` y la recuperación shamir
  3/2 (hoja 05); `DC wait` (hoja 01); el recuento de redes al volver a cero (hoja 01); `psql -x` (hoja 03); el enlace a
  las migraciones desde el índice 1.6 y la chuleta #4; «anotar la clave de la raíz ya» (hoja 05); «abrir `SES` con el
  nodo arriba» (chuleta).
- **Duda de la iteración 2 resuelta:** el `sha256:…` de IMAGE en `DC ps -a` (filas 14-21). Docker 29.9.2 usa el almacén
  de **containerd** (`io.containerd.snapshotter.v1`): `docker images` muestra el id del índice, `--tree` el manifiesto
  `linux/amd64`, y el contenedor guarda el digest de la configuración (el `sha256:` que sale), que no coincide con
  ninguno. `ImageManifestDescriptor` del contenedor = el manifiesto de la imagen etiquetada actual: corre la imagen
  nueva, no hay colgadas. Explicado en la hoja 01 y en la chuleta.
- **Probado por primera vez:** `FLORLOGIC_RAIZ_CLAVE=$(openssl rand -hex 16)`; el bloque WAF tal cual con `--wait`;
  `DC restart` con una variable nueva (no la aplica: confirma la tabla de la hoja 01); borrar por `sesion_id` y
  `TRUNCATE`; `./mvnw -q -DskipTests package` en todo el repo; `DC port gateway 8443`.
- **Acceso:** «Acceso rápido» y la chuleta reordenadas por imprescindible y frecuencia acumulada de las tres
  iteraciones (columna «Veces» al día).
- **Estado final:** `florlogic-manual` abajo (`DC down -v --rmi local`, 17 `Removed`), recuento 0 0 0 0, CA del
  laboratorio borrada; laboratorio igual al código real (`diff -rq` sin diferencias); foto final de la máquina igual a
  la inicial (mismas imágenes `florlogic-canal-*`, 0 colgadas, `despliegue` sin tocar).

## Cierre del proceso

Tres iteraciones del probador de contexto limitado (2 y 3-nov-2026) sobre un laboratorio aislado, más tres pasadas del
orquestador sobre el manual.

- **Órdenes:** **380 corridas** en total (159 + 138 + 83), agrupadas en 52 formas generales; funcionaron 351 (las
  otras 29 fueron errores del manual, todos corregidos o explicados en su entrada).
- **Entradas del manual:** ~100 con orden propia: 80 con encabezado en las hojas 01-06 y 09 (01: 25, 02: 8, 03: 11,
  04: 11, 05: 11, 06: 5, 09: 9), 10 filas de git y cabeceras en la hoja 07, 6 órdenes de psql en la 03 y 5
  herramientas en la 10; más la Secuencia del índice, la chuleta (16 órdenes) y los diagramas de la hoja 08.
- **Siguen sin probar ~20, por tres motivos:**
  - **Git de escritura o lectura (9 filas de la hoja 07 y el índice 1.1):** el laboratorio es una copia sin git, y la
    regla PR-05 deja los commits a Juan; `git restore` además escribe.
  - **Interactivas, necesitan pantalla y teclado (5):** psql por dentro (`\x`, `\q`), `DC exec -it … sh`, lazydocker,
    draw.io (abrir los diagramas de la hoja 08 y el paso 2 del entidad-relación).
  - **Tocan imágenes de Juan (3):** limpiar las colgadas de un proyecto (hoja 01) y borrar las colgadas y las actuales
    de `florlogic-canal-*` (hoja 06, «Avisos» del índice).
  - **Nuevas de esta pasada o variantes (3):** saltar `NodoArranqueSeguroTest` con `-Dtest='!…'` (hoja 02), escribir
    `despliegue/.env` desde cero (hoja 05; siempre venía hecho) y el cierre del script del canal con `--wait`
    (hoja 06).
- **Mejoras que quedarían para una próxima ronda:**
  1. Probar en una terminal con pantalla lo interactivo (psql por dentro, lazydocker, draw.io) y, en una copia con
     git, las órdenes de lectura de la hoja 07; Juan decide si `git restore` entra.
  2. Averiguar por qué `NodoArranqueSeguroTest.c5_sin_vault_el_backend_reiniciado_no_queda_escuchando` tardó 1060 s
     en una corrida cuando las otras dos `verify` completas duraron 3,5 min (¿un tiempo de espera fijo cuando no hay vault?) y probar la orden que la salta, para
     que `verify` vuelva a ser un paso fijo de la demo.
  3. Ponerle a `openbao-init` una forma de que `--wait` sí lo espere (p. ej. `depends_on` con
     `condition: service_completed_successfully` en un servicio testigo, o documentarlo en `compose.yaml`), para que la
     regla del `--wait` no tenga excepciones: decisión de Juan, toca código.
  4. Un examen en frío con otra persona (no un agente) y las 20 peticiones, no 10: medir saltos y tiempo real delante
     de un evaluador, y ver si la chuleta impresa basta.
  5. Un `scripts/IA_preparar-terminal.sh` (para `source`) con el bloque de la chuleta y una comprobación de que el
     gateway responde antes de abrir `SES`; y guardar la clave de la raíz en la custodia en vez de dejarla solo en la
     terminal.
