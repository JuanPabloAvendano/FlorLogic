REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 por el orquestador (iteración 2)
Estado de revisión: SIN REVISAR
Manda por encima: `codigo/IA_LEEME.md` · `codigo/despliegue/compose.yaml` · la palabra de Juan

# 01 · Docker y Docker Compose

`DC` abrevia `docker compose -f despliegue/compose.yaml` (desde `codigo/`). Para no escribirlo, una vez por terminal:
`alias DC='docker compose -f despliegue/compose.yaml'`. Servicios: `base`, `openbao`, `openbao-init`, `backend`, `gateway`.
El proyecto se llama `despliegue` (el nombre de la carpeta): por eso las imágenes, volúmenes y la red empiezan por
`despliegue-` o `despliegue_`. En el laboratorio aparte de las pruebas el proyecto era `florlogic-manual` (puertos 9443 y 55432);
lo que dice «Probado» se comprobó allí, con la misma `compose.yaml`.

Antes de la primera vez hace falta el secreto cero (`despliegue/.env`) y la ceremonia de la CA: hoja 05.

## Regla del wait y qué relee cada orden

**Regla de todo el manual: cada `up -d` que va seguido de una comprobación lleva `--wait`.** Sin él, `up` devuelve la
terminal antes de que el servicio escuche y la comprobación de la línea siguiente miente (`000` con curl rc 35, logs
vacíos, `Up` en vez de `Exited (1)`): probado el 2-nov-2026, iteración 2 (filas 39, 67-69). Con `--wait`, un servicio
que muere sale ≠0 en el acto (fila 77). La única excepción es dañar el gateway a propósito (hoja 09): ahí se espera
unos segundos y se mira `DC ps -a`.

**Qué relee cada orden** (lo que más confunde):

| Cambié… | Basta con | Por qué | Probado |
|---|---|---|---|
| la plantilla montada del gateway (`default.conf.template`) | `DC restart gateway` (o `--force-recreate`) | la imagen convierte la plantilla en `default.conf` **en cada arranque** | iteración 2, filas 53-56 |
| una variable de entorno (`FLORLOGIC_WAF_MODO`, …) | `DC up -d --wait <servicio>` | Compose ve que cambió la configuración y **recrea**; `restart` no la aplica (sigue con la del contenedor creado) | `up`: iteración 2, filas 37, 41; `restart`: sin probar (lo dice la documentación de Compose en docs.docker.com; `--help` no lo menciona) |
| un secreto del `.env` (`FLORLOGIC_HSM_PIN`, `FLORLOGIC_PG_ADMIN`) | `DC up -d --force-recreate --no-deps --wait <servicio>` o `DC rm -sf` + `up` | el secreto se monta al **crear** el contenedor; `up -d` sin cambios no lo nota | iteración 1, filas 125-147; iteración 2, filas 59-60, 75-77 |
| código Java del backend | `DC up -d --build --no-deps --wait backend` | hay que rehacer la imagen | iteración 1, filas 106-111 |

Tocar `gateway` o `backend` con `up` **re-corre `openbao-init`** (sale `Started` y luego `Exited` en la salida): es
normal, comprueba y no cambia nada (iteración 2, filas 37, 41, 119).

## Levantar y apagar

### Levantar el nodo entero, reconstruyendo y esperando a que esté sano
`DC up -d --build --wait`
- Partes: `up` crea lo que falta y arranca · `-d` en segundo plano (devuelve la terminal) · `--build` reconstruye las imágenes propias (`openbao`, `openbao-init`, `backend`) · `--wait` no devuelve la terminal hasta que los healthcheck digan sano (sale ≠0 si alguno no)
- Espera: `✔ Container … Healthy` por servicio; `openbao-init` queda `Exited` (es de una sola vez)
- Si falla: la **primera vez** sale ≠0 a propósito con `service "openbao-init" didn't complete successfully: exit 1`: falta firmar la intermedia (paso 2 de la ceremonia, hoja 05). Si no es eso: `DC logs openbao-init backend`
- Riesgo: ninguno · Necesidad: ESENCIAL
- Tiempos: 1ª vez (para a propósito) ~17 s; 2ª vez ~10 s con las imágenes hechas
- Probado: 2-nov-2026 · iteración 1 · funcionó: 1ª vez falla esperada de `openbao-init` (fila 18); tras firmar, todo `Healthy` (fila 21) · 2-nov-2026 · iteración 2 · funcionó igual: rc=1 en 17 s, luego `Healthy` en 9,7 s (filas 8, 11)

### Levantar (o volver a montar) sin reconstruir
`DC up -d --wait`
- Partes: igual que la anterior sin `--build`: usa las imágenes que ya hay; crea los contenedores que falten
- Espera: los cuatro `Healthy`; si `openbao` se recreó, `openbao-init` vuelve a correr y sale 0
- Si falla: si nada cambió en la configuración, no recrea nada: no nota un archivo montado editado ni un secreto cambiado (tabla «Qué relee cada orden», arriba)
- Riesgo: ninguno · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · funcionó: tras desmontar `openbao` y `backend` los volvió a montar (fila 145) · 2-nov-2026 · iteración 2 · funcionó: tras restaurar el `.env` (7,5 s), tras borrar el volumen de la base (18 s) (filas 64, 83)

### Apagar sin perder datos
`DC down`
- Partes: para y borra los contenedores y la red; **conserva** los volúmenes (base, vault, certificados)
- Espera: `Container … Removed` y `Network despliegue_default Removed`
- Si falla: si dice que la red está en uso, otro contenedor quedó conectado: `DC ps -a`
- Riesgo: ninguno · Necesidad: ESENCIAL (los servicios tienen `restart: unless-stopped`: sin `down` vuelven en cada encendido de la máquina)
- Probado: 2-nov-2026 · iteración 2 · funcionó: 2,4 s, contenedores y red `Removed`, volúmenes conservados; `DC down` + `DC up -d --wait` en 21 s y los datos seguían (filas 81, 131)

### Apagar y borrar todo lo del proyecto (vuelta a cero)
`DC down -v --rmi local`
- Partes: `-v` borra también los volúmenes con nombre del proyecto (base, vault, token SoftHSM, certificados) · `--rmi local` borra las imágenes que construyó el proyecto (las que no tienen `image:` propia); `--rmi all` borraría también las bajadas (postgres, gateway)
- Espera: 8 volúmenes, la red y 3 imágenes `Removed`
- Si falla: si un volumen «is in use», queda un contenedor de otro `-p` montándolo: `docker ps -a --filter volume=<volumen>`
- Riesgo: **borra datos** de este proyecto (base y vault de desarrollo). No toca otros proyectos · Necesidad: ESENCIAL para «reconstruir desde cero»
- Comprobar que quedó en cero (cambiar `despliegue` por el nombre del proyecto): `docker images | grep -c despliegue; docker volume ls --filter label=com.docker.compose.project=despliegue -q | wc -l; docker ps -aq --filter label=com.docker.compose.project=despliegue | wc -l` → `0 0 0`
- Probado: 2-nov-2026 · iteración 1 · funcionó: 8 volúmenes, red y 3 imágenes borrados; luego 0 imágenes y 0 volúmenes del proyecto (filas 155-157) · 2-nov-2026 · iteración 2 · funcionó: 2,3 s, 5 contenedores, 8 volúmenes, red y 3 imágenes; recuento 0/0/0/0 (filas 135, 137)

### Parar o arrancar un solo servicio
`DC stop backend` · `DC start backend`
- Partes: `stop` para el contenedor sin borrarlo · `start` lo arranca otra vez. `start` vuelve enseguida, **sin esperar** a que esté sano
- Espera: `Container … Stopped` / `Started`
- Si falla: para esperar sano después de `start`, `DC up -d --wait backend` (siguiente entrada)
- Riesgo: ninguno · Necesidad: ESENCIAL para la demo «apagar uno» (hoja 09)
- Probado: 2-nov-2026 · iteración 1 · funcionó: `stop`/`start` de backend, base y gateway (filas 87, 89, 92, 96, 150, 154) · 2-nov-2026 · iteración 2 · funcionó con base y backend (filas 88, 117, 119)

### Esperar a que un servicio esté sano
`DC up -d --wait backend`
- Partes: `--wait` espera el healthcheck de ese servicio; sin `--build` no reconstruye nada
- Espera: `Container …-backend-1 Healthy`
- Si falla: `DC logs --tail=50 backend`
- Riesgo: ninguno · Necesidad: ÚTIL (sin esto, la primera petición tras `start` puede dar 502)
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 90) · 2-nov-2026 · iteración 2 · funcionó: base 5,8 s, backend 10 s, gateway 8 s (filas 41, 92, 119)

### Reiniciar un servicio (y releer la plantilla del gateway)
`DC restart gateway`
- Partes: para y arranca el **mismo** contenedor, sin crear uno nuevo. Al arrancar, la imagen del gateway vuelve a convertir `default.conf.template` en `default.conf`: **sí relee la plantilla montada**. No aplica variables nuevas del `.env` (eso necesita `up -d`, que recrea)
- Espera: `Restarting` · `Started`; a los ~6 s `DC ps -a gateway` dice `Up … (healthy)`. `restart` no espera: comprobar con `sleep 6; DC ps -a gateway`
- Si falla: `Restarting (1)` = la plantilla tiene un error (hoja 09, «Romper el gateway»)
- Riesgo: el gateway no atiende unos segundos · Necesidad: ÚTIL · Motivo de necesidad: la forma más corta de aplicar un cambio de la plantilla del gateway, o de arrancar un proceso colgado
- Probado: 2-nov-2026 · iteración 2 · funcionó: con la plantilla dañada dio `Restarting (1)` y el `[emerg]`; con la plantilla restaurada reparó (`healthy`, 404) (filas 53-56). Que no aplique una variable nueva: sin probar

### Reconstruir y recrear solo un servicio (tras cambiar código Java)
`DC up -d --build --no-deps --wait backend`
- Partes: `--build` reconstruye la imagen del backend (compila con Maven dentro del `Dockerfile`) · `--no-deps` no toca a sus dependencias (base, vault) · `--wait` espera sano
- Espera: `Recreated`, `Started`, `Healthy`
- Si falla: un error de compilación sale en la salida del `build`; si arranca y no queda sano, `DC logs --tail=50 backend`
- Riesgo: ninguno · Necesidad: ESENCIAL para «cambiar en vivo»
- Probado: 2-nov-2026 · iteración 1 · funcionó: el título nuevo se vio por curl (filas 106-111)

### Recrear aunque Compose no vea cambios (archivos montados, secretos)
`DC up -d --force-recreate --no-deps --wait <servicio>`
- Partes: `--force-recreate` borra y crea el contenedor aunque su configuración no haya cambiado · `--no-deps` no recrea sus dependencias · `--wait` espera sano y sale ≠0 si el servicio muere (sin él, la comprobación siguiente miente)
- Por qué: un secreto del `.env` (`FLORLOGIC_PG_ADMIN`, `FLORLOGIC_HSM_PIN`) solo se monta al crear; `openbao-init` es de una sola vez y solo vuelve a correr si se recrea. Para la plantilla del gateway también sirve, aunque basta `DC restart gateway` (arriba)
- Espera: `Recreated` … `Healthy` (en `openbao-init`, después `Exited (0)`); ~6 s el gateway, ~4 s `openbao-init`
- Si falla: `container …-<servicio>-1 exited (1)` al instante = el servicio murió: `DC logs --tail=5 <servicio>` (en `openbao-init` el log puede tardar 1-2 s en salir: repetir). En el gateway: `Restarting (1)` y `[emerg]` con la línea
- Riesgo: ninguno · Necesidad: ESENCIAL para los ejercicios de la hoja 09
- Probado: 2-nov-2026 · iteración 1 · funcionó con el gateway (filas 119, 124); `DC up -d gateway` sin la bandera **no** recreó (fila 113) · 2-nov-2026 · iteración 2 · funcionó con gateway y `openbao-init`; sin `--wait` salió 0 y dejó `openbao-init` en `Up` aunque iba a morir (filas 50, 67-68, 75-79)

### Desmontar un solo servicio
`DC rm -sf backend`
- Partes: `rm` borra el contenedor · `-s` lo para antes · `-f` no pregunta. Volúmenes e imagen se quedan
- Espera: `Stopped`, `Removed`. Volver a montarlo: `DC up -d --wait`
- Riesgo: ninguno · Necesidad: ÚTIL (para borrar su imagen, o para que un secreto se vuelva a leer al crear)
- Probado: 2-nov-2026 · iteración 1 · funcionó con `backend` y `openbao` (filas 135, 140) · 2-nov-2026 · iteración 2 · funcionó con `openbao` (fila 59)

## Mirar

### Estado y salud de cada servicio
`DC ps -a`
- Partes: sin `-a` **solo lista los que corren**: un servicio parado (p. ej. la base tras `stop`) no aparece · `-a` incluye los terminados (`openbao-init Exited (0)`) y los parados · `--format '{{.Service}} {{.Status}}'` deja solo nombre y estado
- Espera: 4 `Up … (healthy)` y `openbao-init` `Exited (0)`. La columna PORTS muestra que **solo** `gateway` (`0.0.0.0:443->8443`) y `base` (`127.0.0.1:5432->5432`) publican puertos: backend y vault no (evidencia de «una sola puerta»)
- Si falla: `Restarting (1)` = el proceso muere al arrancar → `DC logs`; `Created` = Compose no pudo arrancarlo (mire el mensaje del `up`)
- Riesgo: ninguno · Necesidad: ESENCIAL
- Ojo: tras el segundo `up --build`, la columna IMAGE de `backend`, `openbao` y `openbao-init` sale `sha256:…` en vez del nombre. No es un fallo (duda sin resolver: el `--build` deja una imagen nueva con otro id)
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 22, 146); sin `-a` no mostró la base parada (fila 93) · 2-nov-2026 · iteración 2 · funcionó (filas 12, 89, 107); IMAGE `sha256:…` otra vez (fila 12)

### Mostrar que el backend no publica puertos
`DC ps -a --format '{{.Service}}: {{.Ports}}'` y `curl -sk https://localhost:8443/ -o /dev/null -w '%{http_code}\n'; echo rc=$?`
- Partes: `{{.Ports}}` deja solo la columna de puertos: un puerto **con** `->` está publicado en el anfitrión; **sin** `->` solo existe dentro de la red de Compose · el `curl` al 8443 del anfitrión demuestra que nadie escucha ahí (`-k` aquí no importa: ni siquiera conecta)
- Espera: `backend: 8081/tcp, 8443/tcp` (sin `->`) · `base: 127.0.0.1:5432->5432/tcp` · `gateway: 0.0.0.0:443->8443/tcp`; el curl da `000` y `rc=7` (conexión rechazada)
- Riesgo: ninguno · Necesidad: ÚTIL (pregunta típica: «una sola puerta», áreas 1.2 y 1.5)
- Probado: 2-nov-2026 · iteración 2 · funcionó con 55432 y 9443 del laboratorio (filas 108, 110)

### Registros (logs)
`DC logs --tail=50 backend` · en vivo: `DC logs -f --tail=50 backend`
- Partes: `--tail=50` solo las últimas 50 líneas · `-f` sigue en vivo (Ctrl+C sale) · sin servicio: todos mezclados · se le puede añadir `| grep -E 'ERROR|WARN'`
- Espera por servicio: `base` «database system is ready to accept connections» · `openbao-init` «certificados emitidos: gateway backend» y «configuración terminada» o «todo coincide» · `backend` «Initializing Spring DispatcherServlet» · `gateway` líneas `"GET / HTTP/2.0" 404` (el healthcheck)
- Si falla: no hay nada que mirar si el contenedor nunca arrancó (`Created`): el error está en la salida de `up`
- Riesgo: ninguno · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · funcionó en los cinco servicios (filas 19, 23-27, 95, 121) · 2-nov-2026 · iteración 2 · funcionó (filas 9, 47, 72, 84, 91); justo después de un `up` sin `--wait`, `openbao-init` salió vacío (fila 69)

### Entrar a un contenedor
`DC exec backend bash` · `DC exec gateway sh` (Alpine no trae bash). Sin terminal (scripts): `DC exec -T backend bash -c 'whoami; ls /app'`
- Partes: `exec` corre algo dentro de un contenedor que ya corre · `-T` sin terminal (para tuberías y scripts) · `-c '<orden>'` la orden que corre el shell
- Espera: backend `florlogic` y `arranque.sh backend-finca.jar` (no corre como root); gateway `uid=101(nginx)`
- Si falla: «service … is not running»: arrancarlo antes
- Riesgo: ninguno · Necesidad: ÚTIL
- Probado: 2-nov-2026 · iteración 1 · funcionó con `-T … -c` (filas 28-30); la forma interactiva sin probar (el probador no tiene terminal)

### Los servicios y la configuración ya resuelta
`DC config --services` · todo: `DC config`
- Partes: `config` imprime el compose con el `.env` aplicado · `--services` solo los nombres · `-q` solo valida (sale 0 si está bien)
- Espera: `base openbao openbao-init backend gateway`
- Si falla: `DC config -q` **no** avisa si falta una variable de secreto (fila 131): eso se ve al crear el contenedor (hoja 09)
- Riesgo: `DC config` sin `--services` **imprime los secretos del `.env`**: no proyectarlo · Necesidad: ÚTIL (`--services`) · `DC config` completo: RARA · Motivo de necesidad: solo para depurar una variable mal sustituida, nunca delante del público
- Antes de algo destructivo (`down -v`, `volume rm`): `DC config | grep -m1 '^name'` dice a qué proyecto se le va a hacer (no imprime secretos con el `grep`)
- Probado: 2-nov-2026 · iteración 1 · funcionó `--services` (fila 38); `-q` no detectó la variable faltante (fila 131) · 2-nov-2026 · iteración 2 · funcionó `grep -m1 '^name'` (filas 2, 134); `-q` otra vez no avisó (fila 62)

### Uso de CPU y memoria
`docker stats --no-stream`
- Partes: `--no-stream` una sola foto en vez de refrescar
- Solo este proyecto, en tabla: `docker stats --no-stream $(DC ps -q) --format 'table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}'`
- Espera: backend ~475-480 MiB; gateway ~51-60, openbao ~38-43, base ~38-46
- Riesgo: ninguno · Necesidad: ÚTIL (pregunta típica de «cloud native»: cuánto consume)
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 37) · 2-nov-2026 · iteración 2 · funcionó la forma con `$(DC ps -q)` (fila 113)

### Proyectos de Compose de la máquina
`docker compose ls -a`
- Partes: `ls` lista proyectos; `-a` incluye los detenidos
- Espera: `despliegue` y, si corre el script del canal, `florlogic-canal`. Sirve para comprobar que no se toca otro proyecto. `restarting(1)` en STATUS = un servicio de ese proyecto está en bucle (mirar con `-p <proyecto> ps -a`)
- Riesgo: ninguno · Necesidad: ÚTIL
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 7, 152) · 2-nov-2026 · iteración 2 · funcionó (filas 104, 137)

## Imágenes, volúmenes y redes

### Listar las imágenes del proyecto
`docker images | grep despliegue`
- Partes: `docker images` lista todas; `grep` deja las del proyecto
- Espera: `despliegue-backend` (~559 MB), `despliegue-openbao-init` (~329 MB), `despliegue-openbao` (~315 MB)
- Si falla: `DC images` con este Docker muestra las construidas como `<none> <none> 0B N/A` (información pobre): usar la forma de arriba
- Riesgo: ninguno · Necesidad: ÚTIL
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 32, 137, 139); `DC images` dio `<none>` (fila 31) · 2-nov-2026 · iteración 2 · funcionó (filas 106, 137)

### Eliminar una imagen y reconstruirla
`DC rm -sf backend` → `docker rmi despliegue-backend` → `DC up -d --build --wait backend`
- Partes: la imagen no se puede borrar mientras un contenedor la usa: primero se desmonta solo ese servicio (no hace falta `DC down` de todo) · `rmi` borra la imagen · `up --build` la reconstruye y monta
- Espera: `Untagged: despliegue-backend:latest` · `Deleted: sha256:…`; después `Healthy` y una imagen nueva con otro id
- Si falla: `Error response from daemon: conflict: unable to delete despliegue-backend:latest (must be forced) - container <id> is using its referenced image <id>` = falta el `DC rm -sf backend`
- Riesgo: ninguno (se reconstruye) · Necesidad: ÚTIL para «reconstruir desde cero» un componente
- Probado: 2-nov-2026 · iteración 1 · funcionó la secuencia completa (filas 134-139)

### Listar e inspeccionar volúmenes
`docker volume ls --filter label=com.docker.compose.project=despliegue` · `docker volume inspect despliegue_datos --format '{{.Mountpoint}}'`
- Partes: `--filter label=…` solo los de este proyecto · `--format` saca un campo del JSON
- Espera: 8 volúmenes (`bao-credenciales`, `bao-datos`, `bao-entrega`, `bao-estado`, `datos`, `softhsm-tokens`, `tls-backend`, `tls-gateway`); el de la base vive en `/var/lib/docker/volumes/despliegue_datos/_data`
- Riesgo: ninguno · Necesidad: ÚTIL
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 35, 36)

### Borrar solo el volumen de la base
`DC down` y luego `docker volume rm despliegue_datos`
- Partes: el volumen no se borra con contenedores encima: por eso el `down` antes
Antes: `DC config | grep -m1 '^name'` (que sea tu proyecto) · después: `DC up -d --wait`
- Espera: `DC up -d --wait` en ~18 s, todo `Healthy`; la base se recrea **vacía con la misma clave** del `.env`; `DC logs openbao-init | tail -4` → `todo coincide (subred, AppRole, clave de KV, base y anfitrión); nada que cambiar`; Flyway vuelve a crear las 4 tablas (`\dt`) y los `count(*)` dan 0. El simulador vuelve a dar `2 nuevas · 0 ya estaban`. Un `$SES` de antes ya no existe: abrir otra sesión (hoja 04). El choque de claves solo aparece si además cambió `FLORLOGIC_PG_ADMIN` (hoja 09)
- Riesgo: **borra la base** · Necesidad: RARA · Motivo de necesidad: empezar con la base vacía sin rehacer el vault ni la ceremonia
- Probado: 2-nov-2026 · iteración 2 · funcionó: `down` 2,4 s, `volume rm`, `up --wait` 18 s, «todo coincide», 4 tablas, 0 y 0, simulador 2 nuevas (filas 80-86)

### Redes
`docker network ls | grep despliegue` · `docker network inspect despliegue_default --format '{{range .IPAM.Config}}{{.Subnet}}{{end}} {{range .Containers}}{{.Name}}={{.IPv4Address}} {{end}}'`
- Partes: `--format` evita leer todo el JSON: la subred (`FLORLOGIC_RED`, por defecto `172.30.0.0/24`) y la IP de cada contenedor
- Espera: `despliegue_default bridge`; una IP por contenedor que corre
- Riesgo: ninguno · Necesidad: ÚTIL (la subred importa: el AppRole del backend solo sirve desde ella)
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 33, 34) · 2-nov-2026 · iteración 2 · funcionó (fila 120)

## Fuera del camino de la demo

### Limpieza de imágenes colgadas, solo de este proyecto
`docker image prune --filter label=com.docker.compose.project=despliegue`
- Partes: `prune` borra imágenes sin etiqueta; el filtro lo limita a las de este proyecto
- Riesgo: sin el filtro afecta **otros proyectos** de la máquina. **Nunca** `docker system prune -a` aquí
- Necesidad: PRESCINDIBLE · Motivo de necesidad: tras muchas reconstrucciones quedan capas viejas que ocupan disco; `DC down --rmi local` cubre la demo
- Ver antes qué hay sin etiqueta: `docker images --filter dangling=true`
- Probado: sin probar (las pruebas no borran imágenes ajenas)

### El puerto de un servicio, con `docker compose port`
`DC port backend 8443`
- Partes: imprime el puerto del anfitrión al que está publicado ese puerto del servicio
- Espera: para el backend imprime `:0` con rc=0 (no publicado); para el gateway, `DC port gateway 8443` daría `0.0.0.0:443` (sin probar)
- Riesgo: ninguno · Necesidad: PRESCINDIBLE · Motivo de necesidad: la salida `:0` es críptica delante del evaluador; «Mostrar que el backend no publica puertos» (arriba) lo dice más claro
- Probado: 2-nov-2026 · iteración 2 · funcionó, pero críptico: `:0`, rc=0 (fila 109)
