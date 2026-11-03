REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 (iteración 2) y el 3-nov-2026 (iteración 3, final) por el orquestador
Estado de revisión: SIN REVISAR
Manda por encima: `codigo/IA_LEEME.md` · `codigo/despliegue/compose.yaml` · la palabra de Juan

# 04 · HTTP por el gateway, a mano con curl

El gateway es la única puerta (`https://localhost:443`, o el puerto de `FLORLOGIC_GATEWAY_PUERTO`; en el laboratorio
aparte, 9443). Solo HTTPS, verificando con la raíz de la CA de la finca. Rutas: `POST /ingesta/v1/sesiones` (abrir),
`POST /ingesta/v1/sesiones/{id}/particiones` (entregar), `POST /ingesta/v1/sesiones/{id}/cierre` (cerrar).

## Preparar la terminal (una vez, desde `codigo/`)
```bash
export FLORLOGIC_PKI=<pki>                      # la misma carpeta que FLORLOGIC_PKI en despliegue/.env
G=https://localhost:${FLORLOGIC_GATEWAY_PUERTO:-443}
C="curl -s --cacert $FLORLOGIC_PKI/raiz.crt -H Content-Type:application/json"
T=~/florlogic-demo; mkdir -p $T                 # archivos de trabajo, fuera del repo
DORADA=contrato/src/test/resources/dorados/peticion-particion.json
```
- Partes: `-s` sin barra de progreso · `--cacert` confía solo en la raíz de la finca (no en las del sistema) · `-H Content-Type:…` dice que el cuerpo es JSON · `G` y `C` son variables de la terminal: se pierden al cerrarla · `DORADA` es la partición de ejemplo del contrato (2 anotaciones)
- Necesidad: ESENCIAL (todas las entradas de abajo las usan)
- Probado: 2-nov-2026 · iteración 1 · funcionó con las rutas escritas completas, sin las variables (filas 44-50) · 2-nov-2026 · iteración 2 · funcionó con las variables (`T` era la carpeta de trabajo del laboratorio) (filas 19-21) · 3-nov-2026 · iteración 3 · funcionó el bloque de la chuleta (filas 25, 63). El mismo bloque, con el `alias` y la apertura de sesión, está en `IA_CHULETA.md`

## El canal a mano: abrir, entregar, cerrar

### Abrir una sesión de sincronización y guardar su id
```bash
printf '{"contrato":"1.0","dispositivo_uuid":"0192f0a1-0000-7000-8000-0000000000aa","reloj_ms":%s,"version_config":12}' "$(date +%s%3N)" > $T/abrir.json
SES=$($C -d @$T/abrir.json $G/ingesta/v1/sesiones | jq -r .sesion_uuid); echo $SES
```
- Partes: el cuerpo es `AbrirSesionDto` (`contrato/src/main/java/co/florlogic/contrato/dto/AbrirSesionDto.java`): `contrato` versión del contrato · `dispositivo_uuid` el celular · `reloj_ms` la hora del celular en milisegundos (`date +%s%3N` = segundos desde 1970 con 3 cifras de milisegundos; con ella el nodo calcula el desfase del reloj) · `version_config` la configuración que tiene el celular · `printf … %s` mete la hora en el JSON · `-d @archivo` manda el archivo como cuerpo (y hace POST) · `jq -r .sesion_uuid` saca solo ese campo, sin comillas · `SES=$(…)` lo guarda en la variable
- Espera: un uuid. La respuesta completa (quitar `| jq -r …` o poner `| jq .`): `{"sesion_uuid":"…","desfase_ms":-25,"confirmado_hasta":0,"version_config":12,"marcada":false}`
- Si falla: `null` = la respuesta no traía `sesion_uuid` (mirar la respuesta completa); vacío = no conectó (`echo $?` de curl: 7 sin gateway, 60 sin la raíz)
- Riesgo: ninguno (crea una sesión ABIERTA) · Necesidad: ESENCIAL para mostrar el canal sin simulador
- Cuándo repetirla: tras cerrar la sesión (una partición a una sesión cerrada da 404) o tras borrar la base (el `SES` viejo ya no existe)
- Probado: 2-nov-2026 · iteración 1 · funcionó la apertura y su respuesta (filas 44, 69) · 2-nov-2026 · iteración 2 · funcionó la captura con `jq -r .sesion_uuid`, cuatro veces (filas 20, 26, 33, 87) · 3-nov-2026 · iteración 3 · funcionó dos veces; un `SES` de 11 h antes seguía abierto (filas 25, 42, 63)

### Mandar una partición a la sesión
`$C --data-binary @$DORADA $G/ingesta/v1/sesiones/$SES/particiones | jq .`
- Partes: `--data-binary` manda el archivo tal cual (`-d` quitaría los saltos de línea) · `| jq .` lo imprime ordenado
- Espera: `200` con `confirmado_hasta: 2` y `conteo` (`recibidas: 2`; `sin_efecto: 2` si ya se habían mandado antes: idempotencia)
- Si falla: `404` «Sesión desconocida» = `$SES` vacío o de otra base; `403` = el WAF (ver abajo); `502`/`504` = backend o base caídos (hoja 09)
- Riesgo: ninguno · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · funcionó: `confirmado_hasta:2`, `recibidas:2, sin_efecto:2` (fila 45) · 2-nov-2026 · iteración 2 · funcionó igual (fila 21) · 3-nov-2026 · iteración 3 · funcionó (filas 47, 64-65)

### Cerrar la sesión
`$C -d '{"contrato":"1.0","completa":true}' $G/ingesta/v1/sesiones/$SES/cierre | jq .`
- Partes: el cuerpo es `CerrarSesionDto`: `completa: true` → la sesión queda `CERRADA`; `false` → `PARCIAL` (el celular no alcanzó a mandar todo)
- Espera: `{"estado":"cerrada","confirmado_hasta":2,…}` · con `false`, `{"estado":"parcial",…}` (en la base: `PARCIAL`, hoja 03 «El estado de una sesión concreta»). Cerrarla otra vez también da `200` (idempotente) y **el primer cierre manda**: una sesión `parcial` cerrada luego con `true` sigue `parcial`
- Después del cierre: una partición a esa sesión da `404` «Sesión desconocida» (el nodo solo conoce sesiones abiertas para entregar): abrir otra
- Riesgo: ninguno · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · funcionó con `completa:true` y doble cierre (filas 46, 47) · 2-nov-2026 · iteración 2 · funcionó `completa:false` → `parcial`/`PARCIAL`; `true` después sigue `parcial`; `true` en otra → `cerrada`; partición a la cerrada → 404 «Sesión desconocida» (filas 22-27, 32, 36) · 3-nov-2026 · iteración 3 · funcionó `completa:false` → `"estado": "parcial"`, `confirmado_hasta 2` (fila 59)

## Códigos y errores

### Ver código de estado y cabeceras
`$C -i …` (cabeceras + cuerpo) · `$C -o /dev/null -w '%{http_code}\n' …` (solo el código) · `-v` todo el diálogo TLS
- Partes: `-i` incluye las cabeceras de la respuesta · `-o /dev/null` tira el cuerpo · `-w '%{http_code}\n'` escribe el código
- Espera: `HTTP/2 200` … · o solo `200`
- Riesgo: ninguno · Necesidad: ESENCIAL (casi todos los ejercicios de la hoja 09 se leen así)
- Códigos de salida de curl (`echo rc=$?`) cuando sale `000`: `7` nadie escucha (gateway apagado o en bucle) · `35` error en el apretón TLS (el gateway aún arranca: faltó `--wait`) · `56` el servidor cortó (mTLS sin certificado) · `60` certificado no confiable (falta `--cacert`)
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 46-48, 88, 94) · 2-nov-2026 · iteración 2 · funcionó; los rc 7, 35, 56 y 60 se vieron en las filas 48, 39, 124 y 123 · 3-nov-2026 · iteración 3 · funcionó; rc 7 y 60 otra vez (filas 29, 31, 37)

### Los errores del backend que se disparan con curl
| Petición | Código y `title` | Probado |
|---|---|---|
| `$C -i --data-binary @$DORADA $G/ingesta/v1/sesiones/0192f0a1-0000-7000-8000-000000000999/particiones` (sesión que no existe) | `404` «Sesión desconocida» | iteración 1 (fila 103) · iteración 2 (fila 29): funcionó |
| `$C -i --data-binary @$DORADA $G/ingesta/v1/sesiones/no-es-uuid/particiones` | `422` «Petición rechazada» | iteración 1 (fila 103) · iteración 2 (fila 30): funcionó |
| `$C -i -d '{roto' $G/ingesta/v1/sesiones/$SES/particiones` (JSON roto, con un `$SES` abierto) | `422` «Petición rechazada» | iteración 1 (fila 103) · iteración 2 (fila 31): funcionó |
| `$C -i --data-binary @$DORADA $G/ingesta/v1/sesiones/<uuid de una sesión ya cerrada>/particiones` | `404` «Sesión desconocida» (aunque exista: duda para Juan, ¿intencional?) | iteración 2 (filas 32, 36): funcionó |
- Partes: las respuestas son `application/problem+json` (un `type` por regla, sin datos de negocio); los títulos están en `backend-finca/src/main/java/co/florlogic/finca/ingesta/infraestructura/input/controladores/Problemas.java`
- Riesgo: ninguno · Necesidad: ÚTIL (código defensivo: ningún punto de entrada revienta) y base del ejercicio «cambiar Java en vivo» (hoja 09)

### Ruta ajena: el gateway responde 404 sin molestar al backend
`$C -o /dev/null -w '%{http_code}\n' $G/actuator/health`
- Espera: `404` (el gateway solo deja pasar `/ingesta/v1/…`; la salud del backend no se expone)
- Para mostrar **quién** responde (el `404` solo no lo dice): `$C -i $G/actuator/health` → cabecera `server: nginx` y cuerpo `"type":"https://florlogic.co/problemas/gateway/rechazado","title":"El gateway no deja pasar la petición"`; y que el backend ni se enteró: `DC logs --since 30s backend | grep -c actuator` → `0` (el `grep -c` sale 1 cuando cuenta 0: es lo esperado)
- Riesgo: ninguno · Necesidad: ÚTIL (API gateway, área 1.3)
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 48) · 2-nov-2026 · iteración 2 · funcionó (filas 51, 121) · 3-nov-2026 · iteración 3 · funcionó el `404` (fila 40) y la forma con `-i`: `server: nginx`, `gateway/rechazado`, 0 líneas en el backend (fila 41)

### En claro no hay nada
`curl -s -o /dev/null -w '%{http_code}\n' http://localhost:${FLORLOGIC_GATEWAY_PUERTO:-443}/`
- Espera: `400` (Nginx no atiende HTTP en el puerto TLS; tampoco hay puerto 80)
- Riesgo: ninguno · Necesidad: ÚTIL (seguridad: ningún enlace en claro)
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 49) · 2-nov-2026 · iteración 2 · funcionó (fila 121)

### Sin la raíz, curl no confía
`curl -s $G/; echo rc=$?`
- Partes: sin `--cacert`, curl usa las raíces del sistema, que no conocen la CA de la finca · `rc=$?` el código de salida de curl
- Espera: `rc=60` (certificado no confiable). `-k` lo salta: **no** usarlo en la demo de seguridad
- Con el motivo en palabras: `curl -sS $G/ -o /dev/null; echo rc=$?` (`-S` muestra el error aunque `-s` calle lo demás) → `curl: (60) SSL certificate OpenSSL verify result: unable to get local issuer certificate (20)`
- Riesgo: ninguno · Necesidad: ÚTIL
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 50) · 2-nov-2026 · iteración 2 · funcionó (fila 123) · 3-nov-2026 · iteración 3 · funcionó; con `-sS` dio el motivo (filas 37, 38)

## El WAF (ModSecurity + OWASP CRS)

### Disparar el WAF con una inyección SQL de ejemplo
```bash
sed "s/\"persona_id\": \"op-07\"/\"persona_id\": \"op-07' OR 1=1 --\"/" $DORADA > $T/ataque.json
$C -i --data-binary @$T/ataque.json $G/ingesta/v1/sesiones/$SES/particiones
```
- Partes: `sed "s/a/b/"` copia la partición dorada cambiando `op-07` por `op-07' OR 1=1 --` (inyección clásica) · `>` a un archivo fuera del repo · necesita un `$SES` abierto
- Espera: `HTTP/2 403` con `"type":"https://florlogic.co/problemas/gateway/bloqueado"`; la petición no llega al backend
- Riesgo: ninguno · Necesidad: ESENCIAL (WAF, áreas 1.3 y 1.5; OWASP A03 Inyección)
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 70, 71, 79) · 2-nov-2026 · iteración 2 · funcionó (filas 34, 42) · 3-nov-2026 · iteración 3 · funcionó: `HTTP/2 403`, `gateway/bloqueado` (fila 42)

### Ver qué regla bloqueó
`DC logs gateway | grep -oE '"ruleId":"[0-9]+"|"message":"[^"]{0,80}'`
- Partes: el log de auditoría es JSON largo; `grep -oE` saca solo el número de regla y el inicio del mensaje
- Espera: `942100` «SQL Injection Attack Detected via libinjection» y `949110` «Inbound Anomaly Score Exceeded (Total Score: 5)» (la segunda es la que decide bloquear: suma de puntos de las reglas que coincidieron)
- Riesgo: ninguno · Necesidad: ÚTIL
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 74); `grep ruleId` solo contó 1 línea ilegible (fila 73) · 2-nov-2026 · iteración 2 · funcionó igual (fila 35) · 3-nov-2026 · iteración 3 · funcionó: `942100` y `949110` (fila 43)

### Pasar el WAF a solo anotar, comprobarlo y volver
```bash
FLORLOGIC_WAF_MODO=DetectionOnly DC up -d --wait gateway
DC exec -T gateway grep -n SecRuleEngine /etc/nginx/conf.d/default.conf
$C -o /dev/null -w '%{http_code}\n' --data-binary @$T/ataque.json $G/ingesta/v1/sesiones/$SES/particiones
DC up -d --wait gateway
```
- Partes: la variable solo vale para esa orden; Compose ve que cambió y **recrea** el gateway (`restart` no aplica variables nuevas) · `--wait` espera a que Nginx escuche: **sin él**, el ataque de la línea siguiente da `000` (curl rc 35, error TLS) · la imagen convierte la plantilla en `/etc/nginx/conf.d/default.conf` sustituyendo `${FLORLOGIC_WAF_MODO}` en la línea `SecRuleEngine` · el último `up` sin la variable vuelve a `On` (el defecto de `compose.yaml`). En la salida también aparece `openbao-init` `Started`/`Exited`: es normal (hoja 01)
- Espera: `Recreated` … `Healthy` (~8 s); el `grep` dice `81: SecRuleEngine DetectionOnly`; el ataque da `200` (pasa y solo se anota); tras volver, `SecRuleEngine On` y `403`
- Riesgo: mientras está en `DetectionOnly` el nodo **no bloquea** · Necesidad: ÚTIL («apaga el WAF» que puede pedir el evaluador)
- Probado: 2-nov-2026 · iteración 1 · funcionó el cambio y la vuelta, 200 y luego 403 (filas 75, 77-79); `echo $FLORLOGIC_WAF_MODO` dentro dio `DetectionOnly` (fila 76) · 2-nov-2026 · iteración 2 · funcionó el `grep` (`81: SecRuleEngine DetectionOnly`, luego `On`) y la vuelta con `--wait` (8 s, 403); sin `--wait` el ataque dio `000` rc 35 y a los 7 s `200` (filas 37-42) · 3-nov-2026 · iteración 3 · funcionó el bloque tal cual, con `--wait` en la primera línea: 9,2 s, `81: SecRuleEngine DetectionOnly`, `200`; la vuelta 9,1 s, `On`, `403` (filas 45-48)
