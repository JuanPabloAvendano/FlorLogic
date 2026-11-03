REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 (iteración 2) y el 3-nov-2026 (iteración 3, final) por el orquestador
Estado de revisión: SIN REVISAR
Manda por encima: `codigo/IA_LEEME.md` · `codigo/despliegue/compose.yaml` · la palabra de Juan

# 06 · El simulador (el celular) y el script del canal

## El simulador contra tu nodo

### Construir el simulador
`./mvnw -q -DskipTests -pl simulador -am package`
- Partes: `-pl simulador -am` el simulador y lo que necesita (`contrato`) · `-DskipTests` sin correr pruebas · `-q` silencioso
- Espera: nada impreso (~2 s si `verify` ya compiló); queda `simulador/target/simulador-0.1.0-SNAPSHOT.jar`. Su fecha en `ls -l` es `Oct 12 19:00` aunque se acabe de construir: el `pom.xml` fija `project.build.outputTimestamp` en `2026-10-13T00:00:00Z` (construcción reproducible: el mismo código da el mismo jar) y 19:00 es esa hora en Colombia
- Riesgo: ninguno · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 39) · 2-nov-2026 · iteración 2 · funcionó en 2,3 s (fila 6); la fecha se explicó leyendo el `pom.xml` · 3-nov-2026 · iteración 3 · funcionó en 2,3 s (fila 7)

### Ayuda del simulador
`java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar --help` · `… sincronizar --help`
- Espera: `Usage: simulador [-hV] [COMMAND]` con el comando `sincronizar`; este acepta `--archivo`, `--dispositivo` y `--url` (por defecto `https://localhost:${FLORLOGIC_GATEWAY_PUERTO:-443}`)
- Riesgo: ninguno · Necesidad: ÚTIL
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 40, 41)

### Sincronizar una partición (abre la sesión, entrega y cierra)
```bash
export FLORLOGIC_CA_RAIZ=$FLORLOGIC_PKI/raiz.crt
java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar sincronizar --archivo contrato/src/test/resources/dorados/peticion-particion.json
```
- Partes: `FLORLOGIC_CA_RAIZ` la raíz en la que confía el simulador (como `--cacert` en curl); exportarla **después** de la ceremonia · `sincronizar` hace lo mismo que la hoja 04 a mano: abre sesión, manda la partición y cierra · `--dispositivo <uuid>` simula otro celular · `--url` otro gateway
- Espera: la 1ª vez `Recibida · 2 anotaciones · 2 nuevas · 0 ya estaban · confirmado hasta 2`; al repetirla `0 nuevas · 2 ya estaban` (idempotencia: reenviar no duplica)
- Si falla: `NoSuchFileException …raiz.crt` = `FLORLOGIC_CA_RAIZ` mal puesta; `Connection refused` = gateway apagado u otro puerto
- Riesgo: ninguno · Necesidad: ESENCIAL (el canal de TX-1 de punta a punta)
- Tras borrar una fila: `2 anotaciones · 1 nuevas · 1 ya estaban` y la fila vuelve (hoja 09, «Borrar una fila y recuperarla reenviando»)
- Probado: 2-nov-2026 · iteración 1 · funcionó: 2 nuevas, luego 2 ya estaban, y tras desmontar y remontar el nodo los datos seguían (filas 42, 43, 148) · 2-nov-2026 · iteración 2 · funcionó: 2 nuevas en 3,2 s, 2 ya estaban en 2,4 s; con la base nueva otra vez 2 nuevas; tras borrar una fila, 1 nuevas · 1 ya estaban (filas 13, 14, 86, 101) · 3-nov-2026 · iteración 3 · funcionó: 2 nuevas en 2,3 s, luego 2 ya estaban en 2,2 s; tras borrar una fila, 1 nuevas · 1 ya estaban (filas 22, 23, 67)

## El script del canal (proyecto aparte)

### El canal de TX-1 de punta a punta, en un proyecto aparte
`FLORLOGIC_GATEWAY_PUERTO=9444 scripts/IA_probar-canal.sh` (sin parar nada) · o `DC stop gateway` → `scripts/IA_probar-canal.sh` → `DC up -d --wait gateway`
- Partes: el script construye el simulador, hace la ceremonia en una carpeta temporal, levanta su propio proyecto `florlogic-canal` (otra subred, base sin puerto publicado), sincroniza, reenvía, manda una partición rota, prueba ruta ajena, ataque y en claro, consulta la base y al salir hace `down -v` **de su proyecto**, no del tuyo
- Por qué otro puerto: el script publica su gateway en `FLORLOGIC_GATEWAY_PUERTO` (443 por defecto), el **mismo** que tu nodo; si el tuyo está escuchando, chocan. Con un puerto libre (9444) los dos conviven; la otra forma es parar el gateway propio y volverlo a levantar al final
- Espera: pasos 3 a 8b correctos (2 nuevas, 2 ya estaban, rota rechazada con código 1, 404, 403, 400, base con 2 anotaciones, sesiones CERRADA 2 + PARCIAL 1) y al final `Canal OK.` (~40 s con las imágenes hechas; unos minutos la primera vez)
- Si falla: `El nodo no arrancó.` con las últimas líneas de `openbao-init` y `backend`
- **Síntoma engañoso del puerto ocupado:** con el puerto en uso **no** sale «port is already allocated»: el script manda la salida del `up` a `/dev/null` y solo muestra los logs de `openbao-init`, que dicen que todo salió bien («certificados emitidos», «vault iniciado»), y luego `El nodo no arrancó.` (~30 s). Parece un fallo del vault y es el puerto: repetir con `FLORLOGIC_GATEWAY_PUERTO=9444`. Comprobar quién ocupa el puerto: `DC ps -a --format '{{.Service}}: {{.Ports}}'` o `docker ps --format '{{.Names}} {{.Ports}}' | grep 443`
- Tras un fallo: el `trap` del script hace `down -v` de `florlogic-canal`; no quedan contenedores ni volúmenes suyos (`docker ps -a --filter label=com.docker.compose.project=florlogic-canal`)
- Riesgo: ninguno sobre tu proyecto; deja ~1,2 GB de imágenes y **las reconstruye en cada corrida**: las anteriores quedan sin etiqueta (entrada siguiente) · Necesidad: ESENCIAL para la demo (prueba completa sin tocar tu nodo)
- Probado: 2-nov-2026 · iteración 1 · funcionó `Canal OK.` con el gateway propio parado antes (filas 150, 151, 154) · 2-nov-2026 · iteración 2 · con el puerto ocupado (9443) falló en 30 s con el mensaje engañoso y sin dejar restos; con 9444 y el nodo propio arriba, `Canal OK.` en 40 s (filas 103-105, 107). La forma «parar y `up -d --wait gateway`» al final, sin probar con `--wait`

### Borrar las imágenes que deja el script
Ver: `docker images | grep florlogic-canal` · las viejas sin etiqueta: `docker images --filter dangling=true`
Borrar las viejas sin etiqueta del script: `docker image prune --filter label=com.docker.compose.project=florlogic-canal` · borrar también las actuales: `docker rmi florlogic-canal-backend florlogic-canal-openbao florlogic-canal-openbao-init`
- Partes: el `down -v` del script borra contenedores y volúmenes pero **no** sus imágenes (no usa `--rmi`), y cada corrida las reconstruye con otro id: la imagen anterior pierde su nombre y queda como `<none>` («colgada», *dangling*) · `prune` borra solo colgadas; el `--filter label=…` lo limita a las de `florlogic-canal` (sin filtro tocaría otros proyectos)
- Espera: `docker images | grep florlogic-canal`: `florlogic-canal-backend` ~559 MB, `-openbao-init` ~329 MB, `-openbao` ~315 MB (≈1,2 GB); tras `prune`, `Total reclaimed space`; tras `rmi`, `Untagged`/`Deleted`
- Riesgo: ninguno sobre tu nodo (se reconstruyen en la próxima corrida, que tardará más) · Necesidad: RARA · Motivo de necesidad: liberar disco; si se va a repetir el script en la demo, dejar las actuales y borrar solo las colgadas. Lo decide y lo corre Juan
- Probado: 2-nov-2026 · iteración 2 · funcionó el listado: ≈1,2 GB y los ids cambiaron tras la corrida (fila 106); borrar, sin probar (las pruebas no borran imágenes de Juan)
