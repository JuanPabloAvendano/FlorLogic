REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 (iteración 2) y el 3-nov-2026 (iteración 3, final) por el orquestador
Estado de revisión: SIN REVISAR
Manda por encima: `README.md` de `documentacion-desarrollo` (sus diagramas mandan) · `codigo/IA_LEEME.md` · la palabra de Juan

# 09 · Romper y reparar en vivo

Es la columna «Lo rompe y lo repara» de la matriz §6 de la guía IS II. Cada ejercicio: qué se daña, qué síntoma se
ve y con qué orden, cómo se repara y cómo se comprueba. Se hace con el nodo arriba y la terminal preparada
(`IA_CHULETA.md`, bloque «Preparar la terminal»: `G`, `C`, `T`, `DORADA` y un `SES` **abierto**; tras borrar la base o
cerrar la sesión, el `SES` viejo da 404: abrir otro). Toda reparación con `up` lleva `--wait` (regla de la hoja 01).

**Antes de tocar un archivo, una copia:** `cp <archivo> $T/` · reparar: `cp $T/<nombre> <archivo>` y comprobar con
`cmp $T/<nombre> <archivo> && echo igual`. En el repo también sirve `git restore <archivo>` (hoja 07), pero pisa
cambios sin commit: lo decide Juan.

| Ejercicio | Fila de la matriz §6 | Área de la guía |
|---|---|---|
| Apagar el backend | `backend-finca` · Gateway | 1.7 (resiliencia, el gateway sigue) |
| Apagar la base | PostgreSQL + Flyway | 1.7 |
| Romper una regla de capas | `backend-finca` · `ingesta` (hexágono) | 1.4 Clean Architecture |
| Gateway con configuración inválida | Gateway Nginx | 1.3 |
| Variable del secreto cero faltante | OpenBao + SoftHSM + `openbao-init` · `compose.yaml` | 1.3 secretos · 1.5 |
| Desajustar la clave de la base y el vault | OpenBao + `openbao-init` · PostgreSQL | 1.3 · 1.5 |
| Cambiar código Java en vivo | Imagen del backend (`Dockerfile`) | 1.6 · 1.9 |
| Borrar la imagen y reconstruirla | Imagen del backend | 1.7 (hoja 01) |
| Borrar una fila y recuperarla reenviando | `ingesta` · PostgreSQL | 1.7 (idempotencia y reenvío, hoja 03) |

### Apagar el backend: el gateway sigue arriba y responde 502
- Daño: `DC stop backend`
- Síntoma: `$C -o /dev/null -w '%{http_code}\n' --data-binary @$DORADA $G/ingesta/v1/sesiones/$SES/particiones` → `502` (el gateway no encuentra a quién pasarle la petición; él sigue sano)
- Reparar: `DC start backend` y `DC up -d --wait backend` (espera a que esté sano; `start` solo no espera)
- Comprobar: la misma petición → `200`
- Riesgo: ninguno · Necesidad: ESENCIAL
- Mostrar que el gateway sigue: `$C -o /dev/null -w '%{http_code}\n' $G/actuator/health` → `404` (lo contesta el gateway) y `DC ps -a` → `backend Exited (143)`, `gateway Up (healthy)`
- Probado: 2-nov-2026 · iteración 1 · funcionó: 502, luego `Healthy` y 200 (filas 87-91) · 2-nov-2026 · iteración 2 · funcionó: 502, 404 del gateway, `Exited (143)`; `up -d --wait backend` en 10 s y 200 (filas 117-119)

### Apagar la base con el backend arriba
- Daño: `DC stop base`
- Síntoma: `DC ps -a` la muestra `Exited (0)` (sin `-a` **no aparece**) · la partición (`time $C -i --data-binary @$DORADA $G/ingesta/v1/sesiones/$SES/particiones`) tarda **~22 s** y devuelve `HTTP/2 504` del **gateway**: `"type":"…/gateway/no-disponible","title":"El nodo de finca no responde"` (el backend espera la base y el gateway se cansa de esperar). Avisar al público antes: son 22 s de silencio · en el backend: `DC logs --tail=40 backend | grep -E 'ERROR|WARN'` → `HikariPool-1 - Failed to validate connection` (el grupo de conexiones a la base las pierde)
- Reparar: `DC up -d --wait base` (arranca y espera sana, ~6 s). `DC start base` solo no espera: la petición siguiente puede fallar unos segundos
- Comprobar: enseguida la misma partición → `200`. El backend se recupera solo: no hay que reiniciarlo
- Riesgo: ninguno · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · funcionó: 504, aviso de Hikari, 200 tras `start` y unos segundos (filas 92-97); `DC ps` sin `-a` no la mostró (fila 93) · 2-nov-2026 · iteración 2 · funcionó: 504 a los 22,0 s, Hikari; `up -d --wait base` 5,8 s y 200 en el acto (filas 88-93) · 3-nov-2026 · iteración 3 · funcionó: `Exited (0)`, `HTTP/2 504` a los 22,06 s, Hikari; `up -d --wait base` 6,0 s y 200 en el acto (filas 64, 65)

### Romper una regla de capas y ver a ArchUnit atraparla
- Daño: en `SesionDeSincronizacion.java` (dominio puro) añadir `import org.springframework.stereotype.Component;` y la anotación `@Component` sobre la clase. Sin editor:
  ```bash
  F=backend-finca/src/main/java/co/florlogic/finca/ingesta/dominio/sesion/SesionDeSincronizacion.java
  cp $F $T/                                                                    # copia
  sed -i '0,/^package .*;/s//&\nimport org.springframework.stereotype.Component;/' $F   # import tras el package
  sed -i 's/^public final class SesionDeSincronizacion/@Component\n&/' $F               # anotación sobre la clase
  grep -n Component $F                                                         # 2:import … y 16:@Component
  ```
  Partes: `0,/^package .*;/` limita el primer `sed` a la primera línea `package` · `&` es el texto encontrado y `\n` un salto de línea: escribe la línea encontrada y debajo la nueva
- Síntoma: `./mvnw -pl backend-finca -am -Dtest='CapasTest,ModulosTest' -Dsurefire.failIfNoSpecifiedTests=false verify` → `BUILD FAILURE`; `CapasTest.el_dominio_solo_depende_de_java_y_de_otro_dominio` dice `Class <…SesionDeSincronizacion> is annotated with <org.springframework.stereotype.Component>`; `ModulosTest` sigue verde (la regla rota es de capas, no de módulos)
- Reparar: `cp $T/SesionDeSincronizacion.java $F` y `cmp $T/SesionDeSincronizacion.java $F && echo igual`
- Comprobar: la misma orden → `Tests run: 9, Failures: 0`, `BUILD SUCCESS` (~7-13 s cada corrida)
- Riesgo: ninguno si se restaura · Necesidad: ESENCIAL (Clean Architecture verificada por una prueba, no por palabra)
- Probado: 2-nov-2026 · iteración 1 · funcionó en rojo y en verde (filas 98-101) · 2-nov-2026 · iteración 2 · funcionó igual, ~7 s cada corrida (filas 126-129) · 3-nov-2026 · iteración 3 · funcionó con los dos `sed` de arriba (líneas 2 y 16): rojo en 12,6 s con el mismo mensaje, verde en 12,8 s tras restaurar (filas 34-36)

### Romper el gateway con una configuración inválida
- Daño: `cp despliegue/gateway/default.conf.template $T/` (copia) · `echo 'esto_no_es_nginx;' >> despliegue/gateway/default.conf.template` · aplicarlo: `DC restart gateway` (la imagen relee la plantilla en cada arranque) o `DC up -d --force-recreate --no-deps gateway`. Con `DC up -d gateway` a secas **no** pasa nada: la plantilla es un archivo montado y Compose no ve cambio en la configuración. Aquí **sin** `--wait`: se quiere ver el bucle
- Síntoma: `sleep 4; DC ps -a gateway` → `Restarting (1)` (muere al arrancar, y Docker lo reintenta en bucle) · `DC logs --tail=30 gateway | grep -E 'emerg|unknown directive'` → `[emerg] 1#1: unknown directive "esto_no_es_nginx" in /etc/nginx/conf.d/default.conf:195` · `$C -o /dev/null -w '%{http_code}\n' $G/` → `000` (curl rc 7: nadie escucha)
- Reparar: `cp $T/default.conf.template despliegue/gateway/` y `DC up -d --force-recreate --no-deps --wait gateway` (~6 s, `Healthy`). También repara `DC restart gateway` (luego `sleep 6`)
- Comprobar: `cmp $T/default.conf.template despliegue/gateway/default.conf.template && echo igual`; `$C -o /dev/null -w '%{http_code}\n' $G/actuator/health` → `404`
- Riesgo: el nodo queda sin puerta mientras dure · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · funcionó con `--force-recreate` (filas 118-124); con `DC up -d gateway` no hubo síntoma (filas 112-115) · 2-nov-2026 · iteración 2 · funcionó con `--force-recreate` (filas 43-51) y con `DC restart gateway`, que **sí** relee la plantilla: rompe y repara (filas 52-56) · 3-nov-2026 · iteración 3 · funcionó: `DC restart gateway` → `Restarting (1)`, `[emerg] … default.conf:195`, `000` rc 7; reparado con `--force-recreate --wait` en 6,3 s, `igual` y `404` (filas 30-33)

### Quitar una variable del secreto cero
- Daño: `sed -i 's/^FLORLOGIC_HSM_PIN=/#FLORLOGIC_HSM_PIN=/' despliegue/.env` (copia antes; comenta la línea) y **desmontar** el servicio que usa ese secreto: `DC rm -sf openbao`
- Antes: `DC config | grep -m1 '^name'` (que sea tu proyecto)
- Síntoma: `DC up -d openbao` → primero `Container …-openbao-1 Created` y después `environment variable "FLORLOGIC_HSM_PIN" required by secret "despliegue_hsm-pin" is not set` (sale 1 en el acto); `DC ps -a` lo deja en `Created` y `DC logs openbao` está vacío (nunca arrancó)
- Ojo: si el contenedor **ya existe**, `DC up -d` sale 0 sin avisar (sigue usando el secreto montado al crearlo) y `DC config -q` tampoco avisa. Compose solo lo nota al crear el contenedor
- Reparar: restaurar `.env` (`cp $T/.env despliegue/.env`) y `DC up -d --wait` (~8 s)
- Comprobar: todos `Healthy`; `openbao-init` vuelve a correr y sale 0; `DC exec -T openbao bao status | grep -E 'Sealed|Seal Type|Initialized'` → `Seal Type pkcs11`, `Initialized true`, `Sealed false` (se abrió solo con el HSM)
- Riesgo: el vault queda abajo mientras dure; el backend sigue con lo que ya leyó (la partición siguió en 200) · Necesidad: ÚTIL (secreto cero, área 1.3)
- Probado: 2-nov-2026 · iteración 1 · funcionó con el contenedor por crear (filas 140-147); sin `rm` no hubo síntoma (filas 125-133) · 2-nov-2026 · iteración 2 · funcionó igual, mensaje exacto con `florlogic-manual_hsm-pin`; backend en 200 durante el daño; reparado en 7,5 s y `Sealed false` (filas 58-65)

### Desajustar la clave de la base y el vault
- Daño: `cp despliegue/.env $T/.env` (copia) · `sed -i 's/^FLORLOGIC_PG_ADMIN=.*/FLORLOGIC_PG_ADMIN=clave-distinta-de-prueba-0000/' despliegue/.env` · volver a correr el inicio y **esperar a que termine**: `DC up -d --force-recreate --no-deps openbao-init; DC wait openbao-init; echo rc=$?`
- Síntoma: `DC wait` sale en ~2 s con `container "…" exited with status code 1` y `rc=1`; `DC logs --tail=1 openbao-init` → `openbao-init: FLORLOGIC_PG_ADMIN no es la clave guardada en el vault; no se reescribe nada. Vuelve a la clave anterior o rótala con generate-root (2 de 3 partes)`. El backend que ya corre no se entera (`--no-deps`): la partición sigue en `200`
- Ojo: **`--wait` no sirve aquí.** `DC up -d --force-recreate --no-deps --wait openbao-init` sale 0 en ~1,4 s con `openbao-init-1 Healthy` y los logs vacíos: un servicio de una sola vez sin healthcheck cuenta como listo en cuanto arranca. Sin `--wait`, igual. El `Exited (1)` y el mensaje llegan ~8-10 s después y parece que no pasó nada. `DC wait <servicio>` sí espera a que el contenedor termine y sale con su código (hoja 01, «Esperar a que termine un servicio de una sola vez»)
- Reparar: `cp $T/.env despliegue/.env` y la misma pareja: `DC up -d --force-recreate --no-deps openbao-init; DC wait openbao-init; echo rc=$?` → `exited with status code 0`, `rc=0`
- Comprobar: `DC logs --tail=1 openbao-init` → `todo coincide (subred, AppRole, clave de KV, base y anfitrión); nada que cambiar`; `cmp $T/.env despliegue/.env && echo igual`
- Por qué: la clave se escribe una sola vez, en el primer arranque, en la base y en el vault a la vez; cambiarla es una rotación con un raíz temporal (`generate-root`), no editar el `.env`. **Borrar solo el volumen `datos` no produce este choque**: la base se recrea con la misma clave del `.env` (hoja 01). `DC down -v` lo «arregla» borrando base y vault: solo en desarrollo
- Riesgo: ninguno si se restaura · Necesidad: ÚTIL
- Probado: 2-nov-2026 · iteración 2 · funcionó: con `--wait` rc=1 al instante y mensaje exacto; sin `--wait` rc=0 y `Up` (el síntoma se vio a los 8 s); backend 200 todo el tiempo; reparado, «todo coincide» a los 2 s (filas 66-79) · 3-nov-2026 · iteración 3 · **falló** la forma con `--wait` que traía esta hoja: rc 0 en 1,4 s con `Healthy` y logs vacíos; `Exited (1)` a los 9 s (filas 51-54) · funcionó la forma de arriba con `DC wait openbao-init`: rc 1 en 1,9 s y el mensaje exacto; reparado rc 0 en 2,3 s, «todo coincide», `.env` igual, backend en 200 (filas 56-57)

### Cambiar código Java y verlo en vivo
- Cambio: en `backend-finca/src/main/java/co/florlogic/finca/ingesta/infraestructura/input/controladores/Problemas.java` cambiar el título `"Sesión desconocida"` (copia antes), p. ej. `sed -i 's/"Sesión desconocida"/"Sesión desconocida (cambiada en vivo)"/' <ruta>`. Ahí están los títulos que se disparan con curl (404 «Sesión desconocida», 422 «Petición rechazada»); los de `ManejadorDeErrores` (409 «Partición en curso», 500 «Error interno») no se disparan fácil
- Reconstruir: `DC up -d --build --no-deps --wait backend`
- Ver: `$C -i --data-binary @$DORADA $G/ingesta/v1/sesiones/0192f0a1-0000-7000-8000-000000000999/particiones` → `HTTP/2 404` con `"title":"Sesión desconocida (cambiada en vivo)"`
- Reparar: restaurar la copia y la misma reconstrucción; comprobar que vuelve `"title":"Sesión desconocida"`
- Riesgo: ninguno · Necesidad: ESENCIAL (cambio → imagen → contenedor, sin tocar lo demás)
- Probado: 2-nov-2026 · iteración 1 · funcionó ida y vuelta (filas 102-111)

### Borrar la imagen del backend y reconstruirla
- Ver hoja 01, «Eliminar una imagen y reconstruirla»
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 134-139)

### Borrar una fila y recuperarla reenviando la misma partición
- Daño: `DC exec -T base psql -U florlogic -d florlogic -c "DELETE FROM anotacion_recibida WHERE consecutivo = 2;"` → `DELETE 1`
- Síntoma: `-c 'SELECT consecutivo FROM anotacion_recibida;'` deja solo el 1; `avance_dispositivo` **no** cambia (`confirmado_hasta` sigue en 2)
- Reparar: reenviar con el simulador (`java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar sincronizar --archivo $DORADA`) → `Recibida · 2 anotaciones · 1 nuevas · 1 ya estaban · confirmado hasta 2` (el «1 nuevas» sin concordancia es del simulador)
- Comprobar: `SELECT consecutivo, anotacion_uuid FROM anotacion_recibida ORDER BY 1;` → vuelven las 2 filas; `avance_dispositivo` igual (2 y 2, `por_encima {}`)
- Por qué: `anotacion_uuid` es la llave primaria y el backend inserta con `ON CONFLICT DO NOTHING` (migración `V2`): lo que ya está cuenta como «ya estaban», lo que falta entra. Reenviar es seguro y recupera lo perdido; el avance del dispositivo no se toca
- Riesgo: **borra datos** hasta el reenvío · Necesidad: ÚTIL (pregunta probable: «¿y si se pierde algo?», idempotencia)
- Probado: 2-nov-2026 · iteración 2 · funcionó (filas 100-102) · 3-nov-2026 · iteración 3 · funcionó: `DELETE 1`, avance en 2; reenvío `1 nuevas · 1 ya estaban · confirmado hasta 2`; vuelven las 2 filas (filas 66, 67)
