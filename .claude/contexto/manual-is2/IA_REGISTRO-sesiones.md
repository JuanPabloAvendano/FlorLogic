REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 por el orquestador (iteración 2)
Estado de revisión: SIN REVISAR
Manda por encima: `README.md` de `documentacion-desarrollo` (sus diagramas mandan) · `codigo/IA_LEEME.md` · la palabra de Juan

# Registro de las sesiones de prueba

El agente de contexto limitado (solo conoce este manual y una copia del código en un laboratorio) anota cada orden
que corrió. El orquestador cuenta y sube a «Acceso rápido» las más repetidas y las imprescindibles. El registro
crudo de cada iteración queda en el scratchpad de la sesión (no en el repo).

## Conteo acumulado

Por forma general: las variantes cuentan juntas (todos los `DC logs …` en una fila). Cada fila del registro crudo
cuenta una vez, en la forma de su orden principal. «Funcionó» cuenta las que dieron lo esperado, incluidas las fallas
esperadas (los síntomas de la hoja 09); las que no, o las «parciales», son errores del manual ya corregidos. Hasta la
iteración 2: **297 órdenes en 50 formas** (iteración 1: 159 en 42; iteración 2: 138, con 8 formas nuevas).

| Orden (forma general) | It. 1 | It. 2 | Acumulado | Funcionó | Necesidad |
|---|---|---|---|---|---|
| `curl --cacert …` (abrir sesión, partición, cierre, códigos, errores, WAF) | 19 | 29 | **48** | 47 | ESENCIAL |
| `DC exec -T base psql\|pg_dump …` | 17 | 15 | **32** | 31 | ESENCIAL |
| `DC logs [--tail=N] <servicio> [\| grep]` | 12 | 10 | **22** | 19 | ESENCIAL |
| `cp` / `cmp` / `diff -rq` (copia y restauración de lo dañado) | 8 | 8 | **16** | 16 | ÚTIL (hoja 09) |
| `DC up -d [--wait] [<servicio>]` (sin `--build`) | 10 | 5 | **15** | 12 | ESENCIAL |
| `DC ps [-a] [--format …]` | 6 | 9 | **15** | 12 | ESENCIAL |
| `sed -i` / `echo >>` (dañar o preparar un archivo) | 8 | 5 | **13** | 13 | ÚTIL (hoja 09) |
| `DC exec -T <servicio> <shell> -c …` | 6 | 4 | **10** | 9 | ÚTIL |
| `DC up -d --force-recreate --no-deps [--wait] <servicio>` | 2 | 6 | **8** | 7 | ESENCIAL |
| `docker images \| grep <proyecto>` (o `DC images`) | 6 | 1 | 7 | 5 | ÚTIL |
| `DC up -d --build [--no-deps] [--wait] [<servicio>]` | 5 | 2 | 7 | 7 | ESENCIAL |
| `./mvnw [-pl <módulo> -am] verify` (con o sin `env -u`) | 4 | 3 | 7 | 6 | ESENCIAL |
| `java -jar simulador… sincronizar` | 3 | 4 | 7 | 7 | ESENCIAL |
| `./mvnw … -Dtest=<Clase> verify` | 3 | 3 | 6 | 6 | ESENCIAL (`CapasTest,ModulosTest`) |
| `DC config` (`--services`, `-q`, `grep -m1 '^name'`) | 3 | 2 | 5 | 4 | ÚTIL |
| `DC stop <servicio>` | 3 | 2 | 5 | 5 | ESENCIAL |
| `DC start <servicio>` | 3 | 1 | 4 | 4 | ESENCIAL |
| `docker volume ls\|inspect\|rm` | 3 | 1 | 4 | 4 | ÚTIL |
| `java -version` · `./mvnw -v` · `mise current` | 3 | 0 | 3 | 3 | ÚTIL |
| `openssl x509 -in <crt> -noout …` | 3 | 0 | 3 | 3 | ÚTIL |
| `grep` en código o script | 3 | 0 | 3 | 2 | ÚTIL |
| `docker compose ls [-a]` · `docker ps -a --filter label=…` | 2 | 1 | 3 | 3 | ÚTIL |
| `./mvnw … dependency:tree` | 2 | 1 | 3 | 2 | ÚTIL |
| `docker network ls\|inspect` | 2 | 1 | 3 | 3 | ÚTIL |
| `DC exec -T openbao bao status` | 2 | 1 | 3 | 3 | ESENCIAL |
| `DC rm -sf <servicio>` | 2 | 1 | 3 | 3 | ÚTIL |
| `java despliegue/pki/Ceremonia.java raiz` | 1 | 2 | 3 | 3 | ESENCIAL |
| `scripts/IA_probar-canal.sh` (con o sin `FLORLOGIC_GATEWAY_PUERTO`) | 1 | 2 | 3 | 2 | ESENCIAL |
| `openssl s_client -connect … -CAfile … [2>&1]` | 1 | 2 | 3 | 2 | ESENCIAL |
| `docker version` · `docker compose version` | 2 | 0 | 2 | 2 | ÚTIL |
| `java -jar simulador… [sincronizar] --help` | 2 | 0 | 2 | 2 | ÚTIL |
| `docker rmi <imagen>` | 2 | 0 | 2 | 1 | ÚTIL |
| `java despliegue/pki/Ceremonia.java firmar` | 1 | 1 | 2 | 2 | ESENCIAL |
| `./mvnw -q -DskipTests -pl simulador -am package` | 1 | 1 | 2 | 2 | ESENCIAL |
| `DC down -v --rmi local` | 1 | 1 | 2 | 2 | ESENCIAL |
| `FLORLOGIC_WAF_MODO=DetectionOnly DC up -d [--wait] gateway` | 1 | 1 | 2 | 2 | ÚTIL |
| `docker stats --no-stream [$(DC ps -q)]` | 1 | 1 | 2 | 2 | ÚTIL |
| `rm -r <custodia> <pki>` (borrar la CA) · nueva | 0 | 2 | 2 | 2 | ESENCIAL (vuelta a cero) |
| preparar la terminal (`DORADA=…`, `G`, `C`, `T`, `echo`) · nueva | 0 | 2 | 2 | 2 | ESENCIAL |
| `DC restart <servicio>` · nueva | 0 | 2 | 2 | 2 | ÚTIL |
| `DC down` (sin `-v`) · nueva | 0 | 2 | 2 | 2 | ESENCIAL |
| `ls …/surefire-reports` | 1 | 0 | 1 | 1 | ÚTIL |
| `ls -l <pki> <custodia>` | 1 | 0 | 1 | 1 | ÚTIL |
| `psql -h 127.0.0.1 -p <puerto>` (anfitrión) | 1 | 0 | 1 | 1 | RARA |
| recuento de cero (`docker images\|volume\|ps\|network` con `grep -c`/`wc -l`) · nueva | 0 | 1 | 1 | 1 | ÚTIL |
| `DC port <servicio> <puerto>` · nueva | 0 | 1 | 1 | 0 | PRESCINDIBLE (salida `:0` críptica; mejor `DC ps -a --format`) |
| `head -4 <archivo IA_>` · nueva | 0 | 1 | 1 | 0 | ÚTIL (en `.sh`, `head -5`) |
| `lazydocker --help` · nueva | 0 | 1 | 1 | 0 | RARA (no tiene modo sin pantalla) |
| `which psql` | 1 | 0 | 1 | 1 | PRESCINDIBLE (solo comprobó que psql está instalado) |
| `find … -newer` (archivos tocados) | 1 | 0 | 1 | 1 | PRESCINDIBLE (control del laboratorio) |

**Top 10 acumulado:** `curl` 48 · `psql`/`pg_dump` 32 · `DC logs` 22 · `cp`/`cmp` 16 · `DC up -d [--wait]` 15 ·
`DC ps -a` 15 · `sed`/`echo` 13 · `DC exec` 10 · `--force-recreate` 8 · (empate a 7) `up --build`, `mvnw verify`,
`simulador sincronizar`, `docker images`.

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

