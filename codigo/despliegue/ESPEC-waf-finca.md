# ESPEC · WAF de la finca (TX-1, recibir la información)

REGISTRO DE IA · NO ES FUENTE — lo genera la skill DetallarParaCodificar, 21-oct-2026, a pedido de Jeronimo (equipo), con visto de Juan (ronda 1 P1).
Estado de revisión: SIN REVISAR
Manda por encima: `IA_00-LEEME-transacciones.md` (TX-1, TEC-15; TEC-31 propuesta) · `despliegue/ESPEC-gateway-finca.md` (el gateway sobre el que corre) · `codigo/contrato/espec/ESPEC-contrato-ingesta.md` (códigos y `type` de error) · `ADR.xlsx`.

## Ficha
| Campo | Valor |
|---|---|
| Elemento | El WAF de la finca (ModSecurity v3 + OWASP CRS 4) dentro del gateway, delante de todo lo que entra; hoy, `POST /ingesta/v1/sesiones/{id}/particiones` |
| **Definición acordada** | El WAF de la finca es un filtro que corre dentro del mismo Nginx del gateway y revisa **todo lo que entra por el gateway** (ruta, cabeceras salvo `Authorization`, y cuerpo) antes de que llegue al backend. **«Limpiar» es filtrar:** lo que parece ataque se detiene (`403`); lo que no puede revisar entero también se detiene (`400` si no es UTF-8 simple, `413` si trae demasiados campos), salvo un JSON roto en UTF-8 simple, que llega al backend y a su `422` (A-4); lo limpio sigue **sin cambios**. El WAF nunca modifica la petición. Hoy solo pasa la ruta de ingesta, así que es lo que se prueba; cuando el gateway abra la web o el análisis, el WAF ya las cubre. No valida el contrato ni la credencial (eso es del backend), no cifra (TLS va después), no limita la tasa y no es el WAF de la nube (acordada 21-oct, 3.ª vuelta, Jeronimo, tras la ronda 2; la 2.ª vuelta fue tras la ronda 1 P1) |
| Diccionario | `.claude/contexto/IA_DICCIONARIO.md` |
| Plantilla | `endpoint`, adaptada como en `ESPEC-gateway-finca` (sin código Java): ⟦5⟧ errores, ⟦9⟧ límites y ⟦12⟧ correlación · `pruebas` para la prueba de punta a punta |
| Dominio | Entra: el WAF en **todo el servidor del gateway** (toda petición que llega), probado sobre `POST /ingesta/v1/sesiones/{id}/particiones`, la única ruta que pasa: imagen, reglas, modo, ajustes, su error propio y su registro · Sale: `despliegue/compose.yaml`, la plantilla del gateway y su prueba de punta a punta · Fuera: el backend, abrir rutas nuevas (web, análisis), TLS, tasa, credencial y el WAF de la nube (`BB-08`) |
| Generalidad | Configuración casi línea por línea (Nginx, ModSecurity, Compose) y su prueba de punta a punta |
| Para qué | Especificar para programar (con visto de Juan sobre TEC-31, K-1) |
| Estado | **LISTA PARA CÓDIGO** (21-oct): precisión 87,6 % y coherencia **pasa**, sin `blocking` abiertos · Segmento 1 escrito y verificado, **sin aceptar**: la explicación se omitió (22-oct, decisión de Jeronimo) |
| **Etapa actual** | 6 Verificación |
| **Dónde retomar** | Verificación del Segmento 1, paso 6: la explicación, que Jeronimo decidió omitir el 22-oct para dejar el código listo para subir (dio el archivo 1: 50 %, §11). Sin ella el segmento no se acepta ni la ESPEC pasa a `IMPLEMENTADA`; se retoma completando el archivo 1 y explicando del 2 al 4. Lo demás de la verificación pasó |
| Precisión | **87,6 %** (verificador, 21-oct, tras los arreglos de redacción: cobertura 80,0 · contradicciones 90 · prácticas 92,0 · trazabilidad 93,8). Antes: ronda 1, 76,6 %; ronda 2, 81,9 % (verificador, 21-oct: cobertura 77,3 · contradicciones 70 · prácticas 93,2 · trazabilidad 93,8), antes de los arreglos de redacción; el verificador estimó ~91 % con ellos. Coherencia ronda 2: **pasa** (COH-11 a COH-16, ninguno rompe el CMF) |
| Completitud | **100 %** (21-oct, §11) · Mutaciones 11/11 · Explicación: omitida 22-oct por decisión de Jeronimo (archivo 1: 50 %; del 2 al 4 sin dar) |
| Avance por etapa | Tamaño ✔ (encuadre acordado 21-oct; 2.ª vuelta tras P1) · Consulta ✔ (mapa confirmado 21-oct) · Preguntas ✔ (ronda 1: 9 desde el encuadre; ronda 2: 4, con permiso; 3.ª vuelta de la definición; 87,6 %) · Generación: Segmento 1 de 1, escrito (21-oct, con el «sí» de Jeronimo) · Revisión ✔ (cerrada 21-oct por Jeronimo: 4 OBS, 2 cumplidas y 2 descartadas) · Verificación: pasos 1 a 5 ✔ (coherencia pasa, 90/90; 122/122 integrado con el refactor, 22-oct), el 6 (explicación) omitido por decisión de Jeronimo (22-oct) · Revisión — · Verificación — · Aceptación — |
| Ambiente | JDK 27 en `C:\Estudio\Software_2\jdk-27` · Docker Desktop 29.9.1 (CLI en `…\AppData\Local\Programs\DockerDesktop\resources\bin`) |

## 1 · Propósito
Poner el WAF de la finca en el camino de la partición de TX-1: el componente «WAF de la finca» del C4 (`N3-GW`,
consulta Q1 de `IA_TECNOLOGIAS-C4.md`), nivel 2 #13 de `IA_PRIORIDAD-componentes-TX1.md`. Por palabra de Juan
(ronda 1 P1), el WAF queda a nivel del gateway para filtrar todo lo que entra por él.

## Camino mínimo funcional (D-17)
1. El simulador manda la partición al gateway, como hoy.
2. El WAF revisa ruta, cabeceras y cuerpo con las reglas OWASP CRS 4.
3. Si no ve un ataque y pudo revisarla entera, la petición sigue al backend sin cambios y responde como hoy (`200` o
   `422`). Si no la puede revisar entera, la detiene: `400` si no es UTF-8 simple, `413` si trae demasiados campos; un JSON
   roto en UTF-8 simple sigue al backend y a su `422` (A-4).
4. Si ve un ataque, en modo bloquear responde `403 gateway/bloqueado` y el backend no se entera.
5. El registro del WAF dice qué regla saltó, con la traza del gateway y sin cabeceras ni cuerpos.

**Después (fuera a propósito):** TLS · tasa · otras rutas (web, análisis) · niveles de paranoia mayores · exclusiones
finas por campo · métricas del WAF · qué hace el celular con `gateway/bloqueado` (§7).

## 2 · Decisiones
| Id | Afirmación | Fuente | Estado |
|---|---|---|---|
| A-1 | **El WAF entra con visto de Juan y va a nivel del gateway de la finca**, sobre TEC-15 y la TEC-31 propuesta. Juan pasa TEC-31 a `ACEPTADO` y ajusta `BB-08` (K-1, K-2) | ronda 1 P1 (Jeronimo: «ya se habló, el WAF va a quedar a nivel del gateway para "limpiar" todo lo que ingresa por el gateway») | decidida |
| A-2 | **El WAF bloquea desde el inicio, con el modo en una variable.** Compose arranca con `FLORLOGIC_WAF_MODO=On`; la plantilla lo aplica con `SecRuleEngine ${FLORLOGIC_WAF_MODO}`. Pasar a solo detectar (`DetectionOnly`) es cambiar la variable, sin tocar la plantilla. Un falso positivo no pierde datos: el celular retiene lo que no entregó (ADR-002). La prueba corre en `On`, salvo el tercer gateway, el de C-7, que prueba `DetectionOnly` | ronda 1 P2 (★) | decidida |
| A-3 | **Un bloqueo del WAF es `403` en `problem+json` con su propio `type`:** `https://florlogic.co/problemas/gateway/bloqueado`, `title` «El WAF de la finca bloqueó la petición», `status` 403 y `traceId` (la traza de A-11 del gateway); sin `detail` ni la regla que saltó (A-36 del contrato: nada interno). Sale por `error_page 403` hacia una ubicación interna `/_gateway/bloqueado`, como los demás errores propios (A-3 y A-9 del gateway). No se confunde con el `403 …/sin-permiso` del backend: el cliente decide por `type` (COH-14 del gateway) | ronda 1 P3 (★) · comprobado 21-oct | decidida |
| A-4 | **El WAF no juzga la forma del JSON: un cuerpo que no puede leer pasa al backend**, que responde su `422 ingesta/rechazado` (A-22 del contrato) como hoy (C-2 del gateway). Se quita la regla `200002` con `SecRuleRemoveById 200002`. Vale **solo para un cuerpo en UTF-8 simple** (sin BOM, sin bytes nulos, UTF-8 válido): ese el backend lo rechaza sin usar nada de él. Lo que no es UTF-8 simple lo para el WAF (A-14), porque el backend sí lo podría leer (K-8). Un cuerpo cortado sigue siendo corte de conexión del gateway (A-9 del gateway), antes del WAF. **Invariante** (COH-13, con permiso): todo lo que el WAF no puede leer en UTF-8 simple también lo rechaza el backend. Hoy se cumple porque el WAF lee hasta 512 niveles de anidación y Jackson 3.1.5, hasta 500 (`DEFAULT_MAX_DEPTH`), y porque Jackson 3 rechaza lo que sobra tras el documento (`FAIL_ON_TRAILING_TOKENS`). Para no depender del valor de la imagen, `SecRequestBodyJsonDepthLimit 512` va fijo en el archivo de ajustes (A-6); C-9 lo prueba con 520 niveles | ronda 1 P4 (★) · acotada en la ronda 2 P2 · comprobado 21-oct | decidida |
| A-5 | **Regla general: en bytes y en anotaciones, los topes del WAF son los del contrato; en campos, el WAF tiene su propio tope y responde `413` para que el celular parta** (reescrita 21-oct con permiso, D-11, tras la ronda 2 P3: el contrato no limita los campos). Partir converge: una anotación válida trae como mucho ~2.000 campos (`max_bytes_valor` de 4 KiB del contrato), menos que el tope. Campos: `SecArgumentsLimit 10000`, unas 3 veces una partición de `max_anotaciones_particion` = 200 (~17 campos por anotación ≈ 3.400); la regla `200007` (tope de 1000) se quita y una regla propia (`id:10001`, del rango 1–99.999 que ModSecurity deja para reglas locales) responde con 10.000 campos o más (`@ge`), porque pasado el tope ModSecurity deja de leer campos y el resto entraría sin revisar. **Responde `413`** (`gateway/rechazado`), «demasiado grande», como el MiB del gateway: el celular parte la partición en dos (A-41 del contrato) y cada mitad pasa; un `400` la dejaba atascada (COH-5, ronda 2 P3). No se sube más el tope: a ~0,44 ms por campo, lo medido con campos como los de la dorada, 100.000 campos pasarían los 30 s del celular. **Tamaño:** lo corta Nginx en 1 MiB (`413`) antes que el WAF, y el WAF revisa el cuerpo entero hasta ese MiB: `SecRequestBodyNoFilesLimit 1048576`, porque la imagen trae 128 KiB y pasado ese tamaño no revisaba el resto (K-9; corregido 21-oct con permiso, D-11: antes decía que el tope de la imagen era el `SecRequestBodyLimit` de 12,5 MiB, que no aplica a un JSON). Si el contrato sube sus topes (ESC-38), estos se revisan (§7) | ronda 1 P5 (★) · ronda 2 P3 (★, `413`) · comprobado 21-oct | decidida |
| A-6 | **El registro del WAF guarda la regla y el valor del campo que la disparó, nunca cabeceras ni cuerpos.** Auditoría solo con las partes `A` (fecha, IP, traza), `H` (reglas, mensajes y el valor que coincidió) y `Z`: `SecAuditLogParts AHZ`, sin `B` (cabeceras, con `Authorization`), sin `C`/`I`/`J` (cuerpo) y sin `E`/`F` (respuesta). **Va en `gateway/modsecurity-override.conf.template`**, el archivo de ajustes que la imagen incluye después de su base: dentro del `server` no tiene efecto, porque la auditoría es global (comprobado 21-oct, COH-3). La traza del WAF es la del gateway (`modsecurity_transaction_id "$traza"`, comprobado). Salidas: la auditoría, una línea JSON por stdout; el registro de errores (stderr) solo trae la línea de la regla que bloqueó, sin datos (comprobado). **Amplía A-11 del gateway:** el registro del WAF sí lleva el valor que disparó la regla (lo que parecía ataque, de la ruta, de una cabecera o del cuerpo), porque sin él no se puede ajustar un falso positivo; nunca de `Authorization`, que el WAF no revisa (A-15). **Es el valor entero del campo, sin recorte** (comprobado en la verificación, OBS-1-10: una nota de 4.000 caracteres dejó un `data` de 4.085). Riesgo anotado: en un falso positivo, el registro local guarda un dato legítimo de hasta 4 KiB (§7). No sale del nodo (no es telemetría, BB-13) | ronda 1 P6 (★) · comprobado 21-oct | decidida |
| A-7 | **Las pruebas del WAF viven en `CanalPorGatewayTest`** (backend-finca, paquete `ingesta`, A-4 del gateway): el mismo arranque (backend con `@SpringBootTest`, PostgreSQL de Testcontainers y el gateway con la imagen y la plantilla de `despliegue/`), ahora con el WAF en `On`. De sus 9 casos de hoy, la dorada pasa a probar C-1 y los otros 8, C-3; entran los casos de C-2 y C-4 a C-13. La imagen se sigue leyendo de `compose.yaml` (ahora la línea `image: owasp/modsecurity-crs:`), y el gateway de la prueba monta los mismos dos archivos que Compose. `java.net.http` no deja poner la cabecera `Host`: para C-1 y C-5 la URL va por IP (`http://127.0.0.1:<puerto>`), que da el mismo `Host` por IP que en la finca. Para C-7 arranca un tercer gateway en `DetectionOnly` que apunta al backend vivo, así que el ataque llega y se guarda (COH-10, con permiso) | ronda 1 P7 (★) · COH-10 | decidida |
| A-8 | **La imagen y su montaje.** El servicio `gateway` usa `owasp/modsecurity-crs:4.30.0-nginx-alpine-202610180210` (TEC-31; trae la Nginx 1.30.6 de TEC-15). La imagen corre sin privilegios: Nginx escucha en el **8080** dentro del contenedor y Compose publica `${FLORLOGIC_GATEWAY_PUERTO:-80}:8080` (el puerto de fuera no cambia, A-2 del gateway). Se montan **solo dos archivos nuestros**, uno por uno: la plantilla, como `/etc/nginx/templates/conf.d/default.conf.template` (pisa el servidor de ejemplo de la imagen), y los ajustes globales, como `/etc/nginx/templates/modsecurity.d/modsecurity-override.conf.template` (A-6); así siguen vivas las demás plantillas de la imagen (`nginx.conf` con los módulos, `conf.d/modsecurity.conf` con `modsecurity on`, `conf.d/logging.conf` con el registro de errores en nivel `warn`, `modsecurity.d/modsecurity.conf` y `setup.conf` con CRS). El archivo de ajustes de la imagen que el nuestro pisa trae solo un comentario (comprobado). El `nginx.conf` de la imagen carga también `headers-more`: la premisa de A-7 del gateway («no hay directiva para quitar `Server`») ya no vale, pero `Server: nginx` se queda como está (§7 del gateway). Montar la carpeta `gateway/` entera, como hasta hoy (OBS-1-11 del gateway), taparía esas plantillas. **`healthcheck` propio en Compose:** `curl -s -o /dev/null http://localhost:8080/` (cualquier respuesta HTTP vale, también el `404` de `/`), porque el de la imagen consulta `https://localhost:8443/healthz`, que nuestro servidor no tiene, y deja el contenedor `unhealthy` (K-10, comprobado). **Se quita `NGINX_ENVSUBST_FILTER`** (ajusta A-12 del gateway): con `^FLORLOGIC_` las plantillas de la imagen quedan sin sustituir. Sin filtro no hay choque: las variables del entorno van en mayúscula y las de Nginx de la plantilla (`$traza`, `$status`…) en minúscula. **Se conserva `client_body_timeout 60s`** del gateway (§3 del gateway), que la imagen bajaría a 10 s | supuesto técnico · comprobado 21-oct | supuesto |
| A-9 | **El WAF revisa lo que entra, nunca las respuestas** (la definición acordada: «revisa todo lo que entra»). Las tres ubicaciones internas de error (`/_gateway/rechazado`, `/_gateway/bloqueado`, `/_gateway/no-disponible`) llevan `modsecurity off;`: sin eso, un `400` que Nginx genera al leer la línea sale como respuesta vacía (K-7). Y para las respuestas del backend: `SecResponseBodyAccess Off` y `SecRuleRemoveById 950000-959999` (las reglas de salida de CRS), porque si una saltara ahí el dato ya estaría guardado y el celular recibiría un error de algo que sí entró (COH-8) | K-7 · COH-8 (con permiso, 21-oct) · comprobado 21-oct | supuesto |
| A-10 | **Alcance: todo el servidor.** `modsecurity on` vale para todo el gateway (lo pone la imagen en `http`). Lo que el gateway ya rechaza por ruta, método, tamaño o forma de la línea (`404`, `405`, `413`, `414`, `400`) sale antes que el WAF y nunca llega al backend; un ataque en una ruta ajena es `404 gateway/rechazado`, no `403` (comprobado). Cuando el gateway abra otra ruta con `proxy_pass`, el WAF ya la revisa | ronda 1 P1 (definición, 2.ª vuelta) · comprobado 21-oct | supuesto |
| A-11 | **Reglas: OWASP CRS 4.30.0 completas, con los valores de la imagen**: paranoia 1 (`BLOCKING_PARANOIA=1`) y umbral de entrada 5 (`ANOMALY_INBOUND=5`). Los ajustes son los de A-4, A-5 y A-9 y una exclusión. **Regla general de exclusiones:** una exclusión de CRS entra solo con un falso positivo comprobado y la prueba que lo reproduce. *Cumple:* quitar `920350` («Host header is a numeric IP address»), que salta en **cada** partición legítima cuando el celular llama al nodo por su IP en la red de la finca y la deja a 2 puntos del umbral (comprobado, COH-6); su prueba son C-1 y C-5 con `Host` por IP. *Viola:* quitar `942100` porque «estorba», sin una partición legítima que la dispare. Hoy es la única exclusión | encuadre (paranoia 1) · COH-6 (con permiso, 21-oct) · comprobado 21-oct · D-17 (regla por defecto) | supuesto |
| A-12 | **Versión: la última estable de CRS con fecha fija**, `4.30.0-nginx-alpine-202610180210`, como TEC-15 tomó la estable de Nginx. Nunca una etiqueta móvil (`nginx-alpine`, `4-nginx-alpine`): la imagen de la prueba y la del nodo tienen que ser la misma. amd64 y arm64 comprobados en Docker Hub. La línea LTS (`4.25.x-…-lts`) queda para Juan (§7) | supuesto · comprobado 21-oct | supuesto |
| A-13 | **Guía ante un falso positivo, en `IA_LEEME.md`** (COH-2, COH-16): buscar la traza que vio el celular en el registro del WAF (`unique_id`), o, si el cliente no la muestra (hoy el simulador solo dice `http_403`), la IP del celular y la hora → si la partición era legítima, pasar el nodo a `DetectionOnly` con `FLORLOGIC_WAF_MODO` y **recrear** el gateway (`docker compose … up -d gateway`; reiniciar no relee la variable) → que el celular vuelva a entregar → escribir la exclusión con su prueba (A-11) y volver a `On` cuanto antes: en `DetectionOnly` el WAF no detiene nada, tampoco lo de A-5 y A-14. Qué hace el celular solo, sin el administrador: semilla del celular (§7) | COH-2 (con permiso, 21-oct) | supuesto |
| A-14 | **Lo que el WAF no puede leer pasa solo si es UTF-8 simple.** Dos reglas propias, que solo actúan cuando el WAF no pudo leer el cuerpo (`REQBODY_ERROR`), responden `400 gateway/rechazado`: `10002` si el cuerpo empieza con BOM (`EF BB BF`) o trae un byte nulo (así se ve UTF-16 o UTF-32, con o sin BOM), y `10003` si no es UTF-8 válido (`@validateUtf8Encoding`, que coincide cuando el texto **no** lo es). El JSON roto en UTF-8 sigue llegando al backend y a su `422` (A-4). Razón: el contrato es JSON y JSON entre sistemas va en UTF-8 sin BOM (RFC 8259 §8.1); Jackson 3 lee las otras codificaciones y el WAF no (K-8) | ronda 2 P2 (★) · comprobado 21-oct | decidida |
| A-15 | **El WAF no revisa la cabecera `Authorization`:** `SecRuleUpdateTargetByTag "OWASP_CRS" "!REQUEST_HEADERS:Authorization"`. Así ninguna regla que revise todas las cabeceras (la `941110`, XSS) puede bloquear por la credencial ni poner un pedazo de ella en el registro (A-6). La credencial la verifica el backend (identidad, TEC-13); hoy todavía no la lee. **Motivo corregido en la revisión (OBS-1-3):** la ronda 2 P4 decía que un `--` del token podía parecer comentario SQL; no es así, la `942100` no revisa `Authorization`, y un token JWS real (base64url y puntos) no dispara ninguna regla. La contraparte: un ataque escondido en `Authorization` llega al backend | ronda 2 P4 (★) · K-11 · mantenida al cerrar la revisión (OBS-1-3, Jeronimo) · comprobado 21-oct | decidida |
| A-16 | **Un cuerpo que no es JSON se para en el WAF:** con `Content-Type` `text/plain` (u otro fuera de los que CRS permite) o sin `Content-Type`, `403 gateway/bloqueado` (CRS `920420`, `920340`), antes del backend. El celular siempre manda `application/json` (con `charset` también pasa); un cliente que manda otra cosa no es el celular. Antes del WAF, ese cuerpo llegaba al backend, que lo rechazaba por no ser JSON | cierre de la revisión, OBS-1-1 (★, Jeronimo) · comprobado 21-oct | decidida |

## 3 · Variables
| Nombre | Valor | Tipo | Dueño |
|---|---|---|---|
| Modo del WAF | `FLORLOGIC_WAF_MODO`: `On` por defecto en Compose (`${FLORLOGIC_WAF_MODO:-On}`); `DetectionOnly` para solo detectar. Son los dos valores admitidos: sin la variable, Nginx no arranca (comprobado); `Off` apagaría el WAF (§7) | configurable por entorno | A-2 · ronda 1 P2 · COH-10 |
| Tope de campos | 10.000 (`SecArgumentsLimit` y regla `10001`, que responde `413`); la dorada trae ~17 por anotación, ~3.400 en 200 | fija en la plantilla · se revisa con `max_anotaciones_particion` | A-5 · ronda 1 P5 |
| Tope de tamaño | 1 MiB: el de Nginx (`client_max_body_size`, gateway §3) y el que revisa el WAF (`SecRequestBodyNoFilesLimit 1048576`; la imagen trae 128 KiB) | fija en la plantilla | A-5 · K-9 |
| Paranoia y umbral | paranoia 1 · umbral de entrada 5 | fija · por defecto de la imagen | A-11 |
| Partes del registro | `AHZ`, en `gateway/modsecurity-override.conf.template` | fija | A-6 · COH-3 |
| Imagen | `owasp/modsecurity-crs:4.30.0-nginx-alpine-202610180210` | fija; se sube con cada estable | TEC-31 · A-12 |
| Puerto dentro del contenedor | 8080 (la imagen corre sin privilegios); fuera sigue `${FLORLOGIC_GATEWAY_PUERTO:-80}` | fija | A-8 |
| Espera del cuerpo del cliente | 60 s entre dos lecturas (`client_body_timeout`), la del gateway; la imagen trae 10 s | fija · se conserva | A-8 · gateway §3 |

## 4 · Comportamiento
Todos con el WAF en `On` (A-2, A-7), salvo C-7.
- **C-1** Dado el backend arriba · cuando el simulador manda la partición dorada por el gateway, con `Host` por IP como en la red de la finca · entonces recibe `200` con el conteo, como sin WAF, y se guardan 2 filas: cero falsos positivos (cubre A-2, A-8, A-11).
- **C-2** Dada la partición dorada con `' OR 1=1 --` en `persona_id` · cuando llega al gateway · entonces responde `403` en `problem+json` con `type` `…/gateway/bloqueado`, `Cache-Control: no-store` y `traceId`, y el backend no guarda nada (cubre A-2, A-3, A-9).
- **C-3** Lo de antes no cambia: el `422` del backend (también ante un JSON ilegible), la ruta ajena `404`, el método ajeno `405` con `Allow: POST`, el cuerpo de más de 1 MiB `413`, la ruta larga `414`, el backend apagado `502` y la traza del `traceparent` salen igual que con el gateway sin WAF (cubre A-4, A-8, A-9, A-10; 8 de los 9 casos de antes de `CanalPorGatewayTest`; el noveno, la dorada, es C-1).
- **C-4** Dado un ataque con `traceparent` y `Authorization: Bearer <secreto>` · cuando el WAF lo bloquea · entonces el registro del WAF (stdout) trae una entrada JSON con `unique_id` = la traza de ese `traceparent`, la regla `949110` y la regla que detectó el ataque, y ni stdout ni stderr del contenedor contienen el secreto (cubre A-6).
- **C-5** Dada una partición válida de 200 anotaciones (`max_anotaciones_particion`) que pesa casi 1 MiB, con `Host` por IP · cuando llega · entonces `200` y se guardan las 200 (cubre A-5, A-11, K-4, K-9).
- **C-6** Dada una ruta que Nginx rechaza al leer la línea (`/../../etc/passwd`) · cuando llega · entonces `400` en `problem+json` con `type` `…/gateway/rechazado`, y no una respuesta vacía (cubre A-9, K-7).
- **C-7** Dado un gateway con `FLORLOGIC_WAF_MODO=DetectionOnly` · cuando llega el ataque de C-2 · entonces llega al backend (no hay `403`) y el registro del WAF lo anota (cubre A-2, COH-10).
- **C-8** Dada la partición de C-5 llevada hasta ~1.048.000 bytes, con `' OR 1=1 --` en el **último** campo de la última anotación · cuando llega · entonces `403 gateway/bloqueado` y no se guarda nada: el WAF revisa el cuerpo entero hasta el MiB (cubre A-5, K-9, OBS-1-7).
- **C-9** Dado el ataque de C-2 escrito en UTF-16 con BOM, en UTF-16LE sin BOM o en UTF-8 con BOM · cuando llega · entonces `400 gateway/rechazado` y no se guarda nada; y `{ esto no es json` sigue dando el `422` del backend; un JSON de 520 niveles con el ataque al final, que el WAF no puede leer, llega al backend y también da `422`, sin guardar nada (cubre A-4 y su invariante, A-14, K-8, COH-13).
- **C-10** Dada una partición de forma válida con 10.000 campos o más (por ejemplo, 200 anotaciones con `datos.valor` como lista de 60 números) · cuando llega · entonces `413` en `problem+json` con `type` `…/gateway/rechazado`, y no se guarda nada; partida en dos, cada mitad da `200` (cubre A-5, COH-5).
- **C-11** Dada la partición dorada con `Authorization: Bearer …x<script>alert(1)</script>` (sin A-15, la `941110` daría `403`, comprobado) · cuando llega · entonces `200`: el WAF no revisa esa cabecera, y su valor no aparece en el registro (cubre A-15, A-6; corregido en la revisión, OBS-1-3).
- **C-12** Dado el gateway con el backend apagado (su `502` sí se anota) · cuando llega la dorada con `Host` por IP · entonces la entrada del registro del WAF no trae la regla `920350`: la exclusión de A-11 está activa (I-5).
- **C-13** Dada la partición dorada con `Content-Type: text/plain`, o sin `Content-Type` · cuando llega · entonces `403` en `problem+json` con `type` `…/gateway/bloqueado`, y no se guarda nada (cubre A-16, OBS-1-1).

## 4b · Ejemplo dorado del bloqueo
`POST /ingesta/v1/sesiones/0192f0a1-7c00-7000-8000-00000000a001/particiones` con `"persona_id": "op-07' OR 1=1 --"` y
`traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01`:

```
HTTP/1.1 403 Forbidden
Server: nginx
Content-Type: application/problem+json
Cache-Control: no-store
```
```json
{"type":"https://florlogic.co/problemas/gateway/bloqueado","title":"El WAF de la finca bloqueó la petición","status":403,"traceId":"4bf92f3577b34da6a3ce929d0e0e4736"}
```

Y en el registro del WAF (una línea JSON; recortada y en forma ilustrativa: los campos exactos los fija la prueba de C-4):
```json
{"transaction":{"client_ip":"…","time_stamp":"…","unique_id":"4bf92f3577b34da6a3ce929d0e0e4736",
 "messages":[{"message":"SQL Injection Attack Detected via libinjection","details":{"ruleId":"942100","data":"Matched Data: s&1c found within ARGS:json.sesiones_captura.array_0.persona_id: op-07' OR 1=1 --"}},
             {"message":"Inbound Anomaly Score Exceeded (Total Score: 5)","details":{"ruleId":"949110"}}]}}
```

## 5 · Forma técnica
De la entrada a la salida. Como en el gateway, no hay controlador, DTO ni puerto: el WAF no tiene código Java.

| Pieza | Archivo destino | Qué cambia |
|---|---|---|
| Configuración del gateway y del WAF | `despliegue/gateway/default.conf.template` | `listen 8080` y `client_body_timeout 60s` (A-8); `modsecurity_transaction_id "$traza"` y un bloque `modsecurity_rules` a nivel de `server` (todo el servidor, A-10) con el modo (A-2), la regla `200002` quitada (A-4), los topes de cuerpo y de campos con la regla `10001` (A-5), las respuestas sin revisar (A-9), la exclusión de `920350` (A-11), las reglas `10002` y `10003` (A-14) y `Authorization` fuera de la revisión (A-15); `error_page 403 /_gateway/bloqueado` y su ubicación interna (A-3); `modsecurity off;` en las tres ubicaciones internas (A-9). La cabecera de comentarios cita esta ESPEC |
| Ajustes globales del WAF | `despliegue/gateway/modsecurity-override.conf.template` (nuevo) | `SecAuditLogParts AHZ` (A-6) y `SecRequestBodyJsonDepthLimit 512` (invariante de A-4) |
| Servicio en Compose | `despliegue/compose.yaml` | `gateway`: imagen de TEC-31 (A-12), `ports` hacia el 8080, `FLORLOGIC_WAF_MODO`, `healthcheck` propio, montaje de los dos archivos y sin `NGINX_ENVSUBST_FILTER` (A-8) |
| Prueba | `backend-finca/src/test/java/co/florlogic/finca/ingesta/CanalPorGatewayTest.java` | Arranque: imagen de `compose.yaml` (ahora la de TEC-31), los dos archivos montados como en Compose, puerto 8080, `FLORLOGIC_WAF_MODO=On`, sin filtro (A-7, A-8). Casos nuevos: C-2 y C-4 a C-13 |
| Corrida real | `scripts/IA_probar-canal.sh` | Un paso más: un ataque por el gateway da `403 gateway/bloqueado` |
| Registro de tecnologías | `.claude/contexto/IA_TECNOLOGIAS-vigentes.md` | TEC-31 «En el código: SÍ» y una línea en §5; el estado lo cambia Juan (A-1, K-1) |
| Guía del código | `IA_LEEME.md` | El gateway con WAF, el modo, la prueba, el conteo de pruebas y la guía ante un falso positivo (A-13) |
| ESPEC del gateway | `despliegue/ESPEC-gateway-finca.md` §7 | Un puntero a esta ESPEC con la lista «Lo que este segmento cambia de la ESPEC del gateway» (más abajo) |
| Esta ESPEC y el diccionario | `despliegue/ESPEC-waf-finca.md` · `.claude/contexto/IA_DICCIONARIO.md` | Visto de Juan y pendientes (A-1); el alcance de todo el servidor se comprueba en C-3 y C-6 (A-10) |

**Cómo encaja en la imagen (comprobado).** La imagen saca sus plantillas a `/etc/nginx` (`NGINX_ENVSUBST_OUTPUT_DIR`);
nuestra plantilla, montada como `templates/conf.d/default.conf.template`, sale como `conf.d/default.conf` y reemplaza
el servidor de ejemplo de la imagen (su `location /` hacia `BACKEND`, `/healthz`, `/metrics/nginx` y el servidor TLS de
prueba): solo queda el servidor del gateway. `nginx.conf` de la imagen carga `ngx_http_modsecurity_module` y su
`conf.d/modsecurity.conf` deja `modsecurity on` y `modsecurity_rules_file /etc/modsecurity.d/setup.conf` (configuración
base y CRS) a nivel `http`. El bloque `modsecurity_rules` de nuestro `server` se suma a esa base: por eso `SecRuleRemoveById`
puede quitar `200002` y `200007`, que la base definió antes.

**Comprobado el 21-oct (consulta)** con `owasp/modsecurity-crs:4.30.0-nginx-alpine-202610180210` (Nginx 1.30.6,
ModSecurity-nginx 1.0.4, libmodsecurity 3.0.17, CRS 4.30.0, 855 reglas) y una copia de borrador de
`gateway/default.conf.template` (fuera del repo, en el scratchpad de la sesión):
- La imagen trae **la misma Nginx 1.30.6** que TEC-15. Corre **sin privilegios** (usuario `nginx`) y escucha en el
  **8080** (`PORT`), no en el 80. Basta montar **solo** `conf.d/default.conf.template`. Con
  `NGINX_ENVSUBST_FILTER: "^FLORLOGIC_"` (A-12 del gateway) sus plantillas quedarían sin sustituir.
- Valores por defecto: `MODSEC_RULE_ENGINE=on` (bloquea), `BLOCKING_PARANOIA=1`, `ANOMALY_INBOUND=5`,
  `MODSEC_REQ_BODY_LIMIT` 12,5 MiB, `MODSEC_ARGUMENTS_LIMIT=1000`, `CLIENT_BODY_TIMEOUT=10s`, auditoría `RelevantOnly`
  en JSON por stdout con las partes `ABIJDEFHZ` (incluyen cabeceras y cuerpo).
- La partición dorada pasa (también con `Host: 192.168.1.10`, como la usará el celular en la red de la finca). Un
  `' OR 1=1 --` en `persona_id` da `403` por la regla `949110` (suma 5), en **HTML**. Con `error_page 403` hacia una
  ubicación interna sale en `problem+json`.
- `{ esto no es json` da **`400` del gateway** (regla `200002`), no el `422` del backend (K-3).
- Una partición válida de **200 anotaciones** (90 KB) da **`400`** (regla `200007`, más de 1000 campos; K-4).
- El registro de auditoría del ataque guardó `Authorization: Bearer …` (K-6). Con `modsecurity_transaction_id "$traza"`
  el `unique_id` del WAF es la misma traza del gateway.

**Comprobado el 21-oct (segunda vuelta del borrador, tras la ronda 1 P1):**
- Las directivas caben en nuestra plantilla, a nivel de `server`, con `modsecurity_rules '…'`: el modo sale de
  `SecRuleEngine ${FLORLOGIC_WAF_MODO}`, y `SecRuleRemoveById 200002` y `SecArgumentsLimit 10000` con una regla propia
  que reemplaza a la `200007` funcionan. La configuración que prueba la prueba es la misma que despliega Compose, sin
  copiar variables `MODSEC_*` a dos lados.
- Con esas líneas, en `On`: la dorada y la de 200 anotaciones pasan; `{ esto no es json` llega al backend; el ataque
  da `403`. En `DetectionOnly` el ataque llega al backend.
- Un ataque en una ruta ajena (`GET /x?id=1' OR 1=1 --`) da `404 gateway/rechazado` (A-10).
- **Fallo hallado (K-7):** `/../../etc/passwd` (Nginx responde `400` al leer la línea) terminaba en respuesta vacía
  (`500`, «header already sent»): el WAF evaluaba la respuesta de la ubicación de error con la regla `959100` sin
  umbral, porque la fase 1 nunca corrió. Con `modsecurity off;` en las tres ubicaciones internas, `400`, `404`, `405`,
  `413` (en las dos rutas), `414`, la línea ilegible y el `403` del WAF salen en `problem+json`.
- ~~Sin comprobar todavía~~ (resuelto en la tercera y la cuarta vuelta): `SecAuditLogParts AHZ` dentro del `server`
  **no** quita las cabeceras (va en el archivo de ajustes, A-6); `client_body_timeout 60s` en el `server` **sí** pisa
  los 10 s de la imagen (`nginx -T`: 10 s en `http`, 60 s en el `server`).

**Comprobado el 21-oct (tercera vuelta, tras el verificador y el agente de coherencia)**, con todo junto en el borrador:
- **K-8:** Jackson 3 (`readTree(byte[])`, como `ParticionesControlador`) lee el ataque en UTF-16 con BOM, UTF-16LE sin
  BOM y UTF-8 con BOM; el WAF, sin la `200002`, los dejaba pasar. Con las reglas propias `10002` (BOM o byte nulo) y
  `10003` (`@validateUtf8Encoding`, que coincide cuando el texto **no** es UTF-8 válido), las dos solo cuando el WAF no
  pudo leer el cuerpo (`REQBODY_ERROR`), los tres dan `400`, un Latin-1 roto también, y `{ esto no es json` sigue
  llegando al backend.
- **K-9:** con el `SecRequestBodyNoFilesLimit` de la imagen (128 KiB), un ataque al final de una partición válida de
  870 KiB pasaba sin revisar; con `SecRequestBodyNoFilesLimit 1048576` en el `server`, da `403`, y la misma partición
  sin ataque pasa.
- **K-10:** el `HEALTHCHECK` de la imagen (`curl https://localhost:8443/healthz`) deja el contenedor `unhealthy`
  (código 7); `--health-cmd "curl -s -o /dev/null http://localhost:8080/"` lo deja `healthy`.
- **Registro (COH-3, A-6):** `SecAuditLogParts AHZ` dentro de `modsecurity_rules` del `server` **no tiene efecto** (la
  auditoría es global): el registro siguió guardando `Authorization`. En
  `modsecurity.d/modsecurity-override.conf.template` (el archivo que la imagen deja para eso, incluido justo después
  de su configuración base) sí: la entrada del ataque trae `unique_id` = la traza, `response.http_code` 403 y
  `messages` (942100 con `Matched Data … persona_id: op-07' OR 1=1 --` y 949110), sin `headers` ni cuerpo; el
  secreto no aparece en ninguna salida. La auditoría del `403` se escribe aunque `/_gateway/bloqueado` lleve
  `modsecurity off`. El registro de errores (stderr) solo trae la línea de `949110` con `data` vacío.
- **COH-6:** con `Host: 192.168.1.10` salta la `920350` («Host header is a numeric IP address», 3 puntos) en cada
  partición legítima: queda a 2 del umbral. Con `SecRuleRemoveById 920350` deja de saltar.
- `SecRuleUpdateTargetByTag "OWASP_CRS" "!REQUEST_HEADERS:Authorization"`: un `' OR 1=1 --` en `Authorization` ya no
  se revisa (pasa). `SecResponseBodyAccess Off` y `SecRuleRemoveById 950000-959999` (sin revisión de respuestas)
  arrancan bien y la ruta `../` sigue en `400 problem+json`.
- **Rendimiento (COH-9):** en este equipo (Docker Desktop en Windows), sin backend: dorada 0,03 s; 200 anotaciones
  (90 KB, ~3.400 campos) **1,5 s**; 200 anotaciones de 870 KiB **2,2 s**. Es tiempo del WAF antes del `proxy_pass`.

**Comprobado el 21-oct (cuarta vuelta, tras la ronda 2)**, con todos los ajustes de A-2 a A-15 juntos:
- `client_body_timeout 60s` del `server` manda sobre los 10 s del `http` de la imagen (`nginx -T`).
- **C-10:** 200 anotaciones con `datos.valor` como lista de 60 números (111 KiB, más de 12.000 campos) → `413` en
  `problem+json` `gateway/rechazado`, en 0,14 s. El orden de las reglas del `server` frente a CRS **no está comprobado** (OBS-1-6, §7): si corren antes, la `10001`
  corta sin la revisión completa, y eso explicaría los 0,14 s; el costo por campo también depende del contenido.
- **C-11:** `Authorization: Bearer eyJ…ab--cd' OR 1=1 --` con la dorada → pasa (sin `403`), y el valor no aparece en
  ninguna salida del contenedor.
- **C-7:** con `FLORLOGIC_WAF_MODO=DetectionOnly`, el ataque de C-2 llega al backend (`502` en el borrador, sin
  backend) y la auditoría lo anota: `unique_id` = la traza, `942100` y `949110`.

**Lo que este segmento cambia de la ESPEC del gateway** (completado con COH-7 y el verificador, con permiso). A-2 del
gateway: el puerto dentro del contenedor pasa de 80 a 8080 (el de fuera no cambia). A-3 y A-9 del gateway: hay un tercer
`type` propio, `gateway/bloqueado` (`403`), y `gateway/rechazado` deja de ser «cualquier 4xx». A-11 del gateway: el
registro del WAF sí lleva el valor que disparó una regla (A-6). A-12 del gateway: se quita `NGINX_ENVSUBST_FILTER`.
A-4 y OBS-1-11 del gateway: la prueba lee otra imagen y monta dos archivos sueltos en vez de la carpeta entera (la
carpeta taparía las plantillas de la imagen). Y el comentario de la prueba que dice que este Nginx no tiene HTTP/2 deja
de ser cierto (el `nginx.conf` de la imagen trae `http2 on`; la prueba sigue en HTTP/1.1). A-7 del gateway: la imagen
trae `headers-more`, así que ya se podría quitar `Server` (no se hace). §3 del gateway: la imagen es otra y
`client_body_timeout` se fija en 60 s en la plantilla, en vez de quedar por defecto. A-10 del gateway: el tiempo del
WAF va antes del `proxy_pass` y no entra en su suma 2 + 2 + 25 < 30 s (§7). **Comprobado:** el cliente de
`java.net.http` (HTTP/2 por defecto, con `Upgrade: h2c`, como `NodoDeFincaAdaptadorHttp`) cruza el WAF en HTTP/1.1: la dorada
pasa y el ataque da `403`. Todo esto quedó anotado en el §7 de `ESPEC-gateway-finca.md`, con un puntero a esta ESPEC (Segmento 1).

**Fuera del borde.** La forma de la partición y su `422`: el backend (`ParticionesControlador`, contrato Segmento 2).
Qué hace el celular ante `gateway/bloqueado`: semilla del celular (§7). La imagen del backend: TEC-28 con Jib.

## 6 · Contradicciones y pendientes
| Id | Qué | Tipo | Dueño | Estado |
|---|---|---|---|---|
| K-1 | El WAF no tiene fila TEC: TEC-15 acepta solo Nginx, y CLAUDE.md pide la TEC `ACEPTADO` antes de usarla | contradicción con el documento que manda | Juan | **cerrada 21-oct** con visto de Juan (ronda 1 P1, Jeronimo: «ya se habló»), como K-1 del gateway. **Pendiente de Juan:** TEC-31 → `ACEPTADO` en el LEEME de transacciones y en `IA_TECNOLOGIAS-vigentes.md` |
| K-2 | `BB-08` pone el WAF en la nube («el plano de datos no está expuesto; un WAF gestionado basta»); el C4 (consulta Q1) lo pone en el gateway de la finca | contradicción entre fuentes | Juan | **cerrada 21-oct** (ronda 1 P1): el WAF queda a nivel del gateway de la finca, para filtrar todo lo que entra por él. **Pendiente de Juan:** ajustar `BB-08` en su documento (carpeta humana) |
| K-3 | Un JSON ilegible da `400 gateway/rechazado` (regla `200002`) en vez del `422` del backend: rompe C-2 del gateway, su prueba `el_422_del_backend_pasa_tal_cual` y el contrato (A-22, A-54 `cuerpo_incompleto` → reintentar) | contradicción con lo decidido · comprobada | usuario | **cerrada 21-oct** por A-4 (ronda 1 P4) |
| K-4 | Una partición válida de 200 anotaciones (`max_anotaciones_particion`) da `400` (regla `200007`, 1000 campos) | falso positivo comprobado | usuario | **cerrada 21-oct** por A-5 (ronda 1 P5) |
| K-5 | El WAF bloquea con `403`, que en el contrato es `sin-permiso` (A-22) y hace que el celular deje de usar esa credencial (A-41); además sale en HTML (rompe A-3 del gateway) | contradicción con lo decidido · comprobada | usuario | **cerrada 21-oct** por A-3 (ronda 1 P3). Qué hace el celular con `gateway/bloqueado`: §7 (semilla del celular) |
| K-6 | El registro del WAF guarda `Authorization` y el valor que disparó la regla; A-11 del gateway dice que el registro nunca lleva `Authorization` ni el cuerpo | contradicción con lo decidido · comprobada | usuario | **cerrada 21-oct** por A-6 (ronda 1 P6) |
| K-7 | Con el WAF, un `400` de línea ilegible o ruta con `..` sale como respuesta vacía (el WAF revisa la respuesta de error del gateway): rompe A-9 del gateway y C-3 | fallo comprobado · bloquea C-3 | IA | **cerrada 21-oct** por A-9 (supuesto técnico, comprobado) |
| K-8 | **Salto del WAF por codificación** (hallado por el verificador, comprobado 21-oct): la premisa de A-4 («el backend lo rechaza sin usar nada de él») es falsa para UTF-16 con o sin BOM y UTF-8 con BOM. El WAF no los puede leer (sin la `200002`, los deja pasar sin revisar) y Jackson 3, el lector del backend, sí: lee `persona_id = op-07' OR 1=1 --`. Un ataque así llega al backend y se guarda | contradicción con el código · rompe C-2 | usuario (reabre A-4, ronda 1 P4) | **cerrada 21-oct** por A-14 y C-9 (ronda 2 P2). Comprobado en borrador: dos reglas propias (`10002`: BOM o byte nulo; `10003`: UTF-8 inválido), que solo actúan si el WAF no pudo leer el cuerpo, dan `400` a esos tres y dejan pasar el JSON roto en UTF-8 (sigue su `422`) |
| K-9 | **Cuerpos grandes sin revisar** (hallado por el verificador, comprobado 21-oct): la imagen trae `SecRequestBodyNoFilesLimit` de 128 KiB, y pasado ese tamaño el WAF no revisa el cuerpo. Un ataque al final de una partición válida de 870 KiB pasó (`502` en el borrador, sin backend). Con `SecRequestBodyNoFilesLimit 1048576` (= el MiB del gateway) da `403`. A-5 decía, por error, que el tope de la imagen era 12,5 MiB | contradicción con A-5 (su regla general) · rompe C-2 | IA | **cerrada 21-oct** por A-5 corregida (con permiso, D-11) y C-8 |
| K-10 | **El contenedor queda «no sano»** (hallado por el verificador, comprobado 21-oct): el `HEALTHCHECK` de la imagen consulta `https://localhost:8443/healthz`, que nuestra plantilla no tiene (sale con código 7). `scripts/IA_probar-canal.sh` hace `compose up --wait` y fallaría | fallo comprobado · rompe la corrida real | IA | **cerrada 21-oct** por A-8 (`healthcheck` propio, comprobado; con permiso) |
| COH-1 | TEC-31 sigue `PROPUESTA` y el visto de Juan llegó por Jeronimo; CLAUDE.md: «si no está `ACEPTADO`, no se usa». `IA_PRIORIDAD` §3.2 pedía además aceptar el `403` en el contrato | coherencia · **blocking** · rompe el CMF | usuario / Juan (reabre A-1) | **cerrada 21-oct** (ronda 2 P1): se mantiene la ronda 1 P1, como K-1 del gateway. **Riesgo anotado:** si Juan no acepta TEC-31, el segmento se revierte entero: la imagen `nginx:1.30.6-alpine`, el puerto 80 interno, el filtro de la plantilla, el `healthcheck`, el montaje de la carpeta, la prueba y el `type` `gateway/bloqueado`. Mientras tanto quedan contradichos, como excepción anotada: CLAUDE.md («si no está `ACEPTADO`, no se usa»), §4 de `IA_TECNOLOGIAS-vigentes.md` («no la usa todavía»; tiene una línea nueva en su §5 que lo explica) y la fila `TEC-31` del diccionario (corregida). El precedente no es idéntico: K-1 del gateway iba sobre la fila TX-1, con TEC-15 ya `ACEPTADO`. Pendiente de Juan: TEC-31 → `ACEPTADO` y `BB-08`. El `403` no entra a A-22 del contrato: los `type` del gateway viven en su ESPEC y en el diccionario |
| COH-2 | Un falso positivo no tiene camino de recuperación: «no reintentar igual» no dice cuándo se reenvía, y si el celular aplica A-56 por código frenaría a toda la persona | confiabilidad · mayor · después (semilla del celular) | IA | **incorporado 21-oct** (con permiso): A-13 y §7 (semilla del celular) |
| COH-3 | El registro: ¿se escribe la auditoría del `403` con `modsecurity off` en la ubicación de error?, el `error_log` con `[data]`, stdout sin rotación | confiabilidad · mayor | IA | **incorporado 21-oct** (con permiso): A-6 (archivo de ajustes, salidas), C-4 (stdout y stderr); rotación de los registros de Docker: §7 |
| COH-4 = K-8 | Salto por codificación | coherencia · mayor | usuario | ver K-8 · ronda 2 |
| COH-5 | Con `datos.valor` como lista, 200 anotaciones válidas pasan de 10.000 campos y reciben `400`, que el celular no sabe resolver; la `10001` no tiene caso | confiabilidad · mayor | usuario (reabre A-5) | **cerrada 21-oct** por A-5 (`413`) y C-10 (ronda 2 P3) |
| COH-6 | `920350` deja cada partición con `Host` por IP a 2 puntos del bloqueo | confiabilidad · mayor | IA | **incorporado 21-oct** (con permiso): A-11, C-1 y C-5 |
| COH-7 | Faltan en la lista de cambios al gateway A-3 y A-9 (sus `type`) y OBS-1-11 (montaje), y el diccionario define `gateway/rechazado` como «cualquier 4xx» | coherencia · menor | IA | **incorporado 21-oct** (redacción, con permiso): §5 «Lo que este segmento cambia de la ESPEC del gateway» y diccionario |
| COH-8 | CRS también revisa respuestas; si saltara ahí, el dato ya estaría guardado | cohesión · menor | IA | **incorporado 21-oct** (con permiso): A-9 |
| COH-9 | El tiempo del WAF no está en la suma 2 + 2 + 25 < 30 s del gateway ni en el p95 del contrato | rendimiento · menor · después | IA | **medido**: 1,5 s (200 anotaciones) y 2,2 s (870 KiB) en desarrollo · §7 (ESC-38 y plazos de A-40) |
| COH-10 | `DetectionOnly` no tiene prueba; un valor mal escrito de `FLORLOGIC_WAF_MODO` impide arrancar | disponibilidad · menor | IA | **incorporado 21-oct** (con permiso): A-7, C-7 y §3; `Off`, en §7 |
| K-11 | Verificador: «nunca cabeceras» frente al fragmento que puede salir de una cabecera, también de `Authorization` | coherencia · −5 | usuario | **cerrada 21-oct** por A-15 y C-11 (ronda 2 P4) |
| K-12 | Verificador: A-1, A-10 y A-11 sin destino en §5; la regla de A-11 sin su par cumple/viola; la `10001` sin caso | trazabilidad · cobertura | IA | **incorporado 21-oct** (redacción, con permiso): §5 (destinos de A-1, A-10 y A-11), par cumple/viola en A-11, C-8; el caso de la `10001` es C-10 (ronda 2 P3) |
| COH-11 | Un `413 gateway/rechazado` por campos no lleva al celular a partir si decide solo por `type`; el `400` de A-14 no tiene conducta en A-41 | cohesión · mayor · después | IA | §7 (semilla del celular), con permiso 21-oct |
| COH-12 | Concurrencia: el WAF ocupa el proceso de Nginx; con 13 celulares y pocos núcleos las esperas pueden pasar de 30 s | rendimiento · mayor · después | IA | §7 (ESC-38), con permiso 21-oct |
| COH-13 | A-4 depende de que la profundidad del WAF (512) sea mayor o igual que la de Jackson (500) | confiabilidad · menor | IA | **incorporado 21-oct** (con permiso): invariante en A-4, `SecRequestBodyJsonDepthLimit 512` fijo, caso en C-9 |
| COH-14 | Evidencias que faltaban en §5 (`413`, `client_body_timeout`, archivo de ajustes de la imagen) y detalles de la prueba (`Host`, `imagenDeCompose`, C-7) | coherencia · menor | IA | **incorporado 21-oct**: §5 cuarta vuelta, A-7, A-8 |
| COH-15 | La definición, el paso 3 del CMF y el diccionario no decían lo de la ronda 2; A-4 frente a A-14; cambios al gateway (§3, A-7) | coherencia · menor | IA | **incorporado 21-oct**: definición (3.ª vuelta), CMF, A-4, §5, diccionario; UTF-8 al contrato en §7 |
| COH-16 | La guía de A-13 empieza por la traza, que el simulador no muestra; `DetectionOnly` abre todo; el registro y TEC-17 | confiabilidad · menor | IA | **incorporado 21-oct**: A-13 y §7 |
| K-13 = I-1 | Verificador: la definición y el paso 3 dicen «lo que no puede revisar entero se detiene», pero A-4, C-3 y C-9 dejan pasar al `422` el JSON roto en UTF-8 | ambigüedad · −5 | IA | **aplicada 21-oct** (con el «sí» al plan): definición, paso 3 del camino mínimo y diccionario |
| K-14 = I-2 | Verificador: §5 dice que la `10001` «corta antes de revisar» y §7 que corre al final de la fase 2; los 0,44 ms por campo dependen del contenido | coherencia · −5 | IA | **aplicada 21-oct**: §5 cuarta vuelta y A-5 |
| I-3 · I-4 · I-7 · I-8 | C-3 dice «9 casos» (son 8); «desde» y «más de» 10.000 (es `@ge`: 10.000 o más); §7 no nombra `max_bytes_valor`; la lista de cambios al gateway no nombra A-10 | redacción · detalle | IA | **aplicadas 21-oct**: C-3, A-5, C-10, diccionario, §7 y la lista de cambios al gateway |
| I-5 | C-1 y C-5 pasarían aunque `920350` siguiera activa (3 < 5): no prueban la exclusión | pruebas · menor | IA | Segmento 1: un caso con el gateway del backend apagado (`502`, que la auditoría anota) y `Host` por IP, que afirma que `920350` no aparece en el registro (C-12) · **escrito**: `con_host_por_ip_el_registro_no_trae_la_920350` |
| I-6 | C-4 y C-7 leen el registro después de la respuesta, y ModSecurity lo escribe al final | pruebas · menor | IA | **escrito**: `entradaDelRegistro` espera con `WaitingConsumer` de Testcontainers, sin `sleep` (PB-10) |

## 7 · Fuera del dominio (para otro canal)
- **Qué hace el celular con `gateway/bloqueado`** (semilla del celular, COH-14 del gateway): retener la partición sin
  borrar nada (ADR-002), no reintentar igual, no cambiar de credencial (no es `sin-permiso`) y avisar con la traza para
  que el administrador la busque en el registro del WAF. Hoy el simulador lo muestra como `http_403` genérico.
- **Lo que el celular hace con los demás errores del WAF** (COH-11, semilla del celular): un `413` de cualquier `type`
  → partir la partición (A-41 del contrato; si el celular decidiera solo por `type`, la de C-10 quedaría atascada); un
  `400 gateway/rechazado` (A-14) → retener solo esa partición (A-56) y avisar. **UTF-8 sin BOM al contrato:** A-14 es
  más estricta que el texto del contrato, que no lo dice; lo decide Juan en la ESPEC del contrato.
- **Línea LTS de CRS** (`4.25.x-nginx-alpine-…-lts`) frente a la última (`4.30.0`): para un nodo que se actualiza poco,
  la LTS recibe parches de seguridad más tiempo. Decide Juan. Cómo llegan reglas nuevas a un nodo sin internet: con la
  imagen (TEC-28), igual que el backend.
- **Topes de A-5 cuando cambie el contrato:** si `max_anotaciones_particion`, `max_bytes_particion` o `max_bytes_valor`
  suben (ESC-38, SPK-04), se revisan el tope de campos y las pruebas de C-5 y C-10. Partir tras un `413` converge solo
  mientras una anotación válida (con `max_bytes_valor` de 4 KiB, ~2.000 campos) quede por debajo del tope.
- **Costo de CPU del WAF por partición** en la prueba de carga (ESC-38, COH-9): medido en desarrollo, 1,5 s con 200
  anotaciones (~3.400 campos) y 2,2 s con 870 KiB, antes del `proxy_pass`. No entra en la suma 2 + 2 + 25 < 30 s de A-10
  del gateway: en el peor caso (backend colgado y dos direcciones que vencen) el celular se rinde a los 30 s antes del
  `504`, y reintenta como ante cualquier corte. Se ajusta con los plazos de A-40 del contrato y el hardware del nodo
  (CN-20). La prueba de carga corre con el WAF en `On`.
- **Concurrencia del WAF** (COH-12, ESC-38): ModSecurity revisa dentro del proceso de Nginx que atiende la petición;
  con `worker_processes auto` hay uno por núcleo. Con 13 celulares a la vez y pocos núcleos (CN-20) las esperas pueden
  pasar de 30 s. La `10001` corre al final de la fase 2, así que un `413` cuesta una revisión completa (0,14 s medidos
  con 12.000 campos). Medido en `CanalPorGatewayTest` (21-oct, en desarrollo): 100 anotaciones con listas de 60 números (~7.600
  campos, debajo del tope) tardan **6,4 s** en el WAF; 200 de casi 1 MiB, ~3 s. Se mide en la prueba de carga; la
  opción de poner las reglas propias antes de CRS se decide ahí.
- **El registro y la observabilidad** (COH-16): `unique_id` sale del `traceparent` del cliente, que puede repetirse o
  falsearse (el registro sigue sirviendo para buscar, no prueba nada). La auditoría trae el valor entero de lo que pareció
  ataque y comparte stdout con el registro de acceso: cuando TEC-17 recoja stdout, hay que filtrarla para cumplir
  BB-13 y ESC-30.
- **El orden de las reglas propias frente a CRS** (OBS-1-6, COH-18): medir 9.999 frente a 10.000 campos y corregir §5
  y la nota de concurrencia. Lo caro podría ser la partición justo por debajo del tope.
- **El valor entero en el registro** (OBS-1-10): en un falso positivo, el registro local guarda el campo legítimo
  completo (hasta 4 KiB, comprobado). Si pesa, se decide con TEC-17 y BB-13 (filtrar o recortar al recoger stdout).
- **Sin prueba:** A-9 (sin revisar respuestas) no tiene mutación, porque provocar una regla de salida exige un backend
  que filtre datos; y nadie manda un ataque por h2c con conocimiento previo (la imagen trae `http2 on`).
- **Rotación de los registros** (COH-3): Docker guarda stdout del gateway sin tope; ya pasaba con el registro de acceso.
  Se decide con la operación del nodo (`logging` con `max-size` en Compose).
- **`FLORLOGIC_WAF_MODO=Off`** apagaría el WAF sin aviso (COH-10). Validar el valor exige un guion de arranque propio;
  hoy solo lo cambia quien opera el nodo.
- Paranoia mayor que 1, exclusiones finas por campo y complementos de CRS: solo con tráfico real y la regla de A-11.
- **Métricas del WAF** (bloqueos por regla, falsos positivos ajustados): con la observabilidad (TEC-17).
- **`http2 on`** y **`keepalive_timeout 60s`** vienen del `nginx.conf` de la imagen (el gateway sin WAF no tenía HTTP/2
  y usaba 75 s). No cambian lo que ve el celular por HTTP/1.1; se revisan con TLS.
- **TLS en la imagen:** la imagen genera al arrancar un certificado de prueba que nuestro servidor no usa; el TLS real
  va con TEC-16.
- El riesgo del 18080 abierto en el anfitrión (§7 del gateway) sigue: el WAF solo filtra lo que entra por el gateway.

## 8 · Plan de segmentos
| Seg. | Archivos (líneas estimadas) | Cubre | Estado |
|---|---|---|---|
| Segmento 1 · el WAF en el canal | cambia `despliegue/gateway/default.conf.template` (107 → ~170) · nuevo `despliegue/gateway/modsecurity-override.conf.template` (~12) · cambia `despliegue/compose.yaml` (32 → ~50) · cambia `backend-finca/src/test/java/co/florlogic/finca/ingesta/CanalPorGatewayTest.java` (254 → ~470) · cambia `scripts/IA_probar-canal.sh` (+6) · documentos: `IA_LEEME.md` (gateway con WAF, guía ante un falso positivo de A-13, pruebas), `.claude/contexto/IA_TECNOLOGIAS-vigentes.md` (TEC-31 «En el código: SÍ» y su línea), `despliegue/ESPEC-gateway-finca.md` §7 (puntero a esta ESPEC), esta ESPEC (§8, §9, I-1 a I-8) y el diccionario · **diagrama:** `diagramas/IA_waf-segmento-1.drawio` (contexto, configuración, prueba; PNG al lado) | 761 brutas: configuración 223 (plantilla 166, ajustes 14, Compose 43) y prueba 538; ~325 de lógica (pruebas ×0,5, declarativo ×0,25) → 3 pasadas de revisión (configuración; prueba hasta los casos del canal de antes; casos del WAF y ayudas) | A-1 a A-16 · C-1 a C-13 | **escrito 21-oct** (con el «sí» de Jeronimo) · `CanalPorGatewayTest` 23/23 con Docker real (25/25 tras cerrar la revisión) · `./mvnw clean verify` 88/88 (90/90 tras cerrar la revisión) · corrida real con el `compose.yaml` real (gateway `healthy`) · **verificado** (pasos 1 a 5); explicación omitida 22-oct · integrado con el refactor del hexágono por módulo: 122/122 (22-oct) |

## 9 · Matriz requisito → código → prueba
Prueba = método de `CanalPorGatewayTest`. «Plantilla» = `despliegue/gateway/default.conf.template`; «ajustes» =
`despliegue/gateway/modsecurity-override.conf.template`.

| Requisito | Archivo:símbolo | Prueba | Estado |
|---|---|---|---|
| C-1 · A-8 · A-11 | plantilla: `listen 8080`, `SecRuleRemoveById 920350` · `compose.yaml`: imagen, `ports`, montajes | `la_particion_dorada_pasa_por_el_gateway_y_responde_200` (por IP) | en verde |
| C-2 · A-2 · A-3 | plantilla: `SecRuleEngine ${FLORLOGIC_WAF_MODO}`, `error_page 403`, `location = /_gateway/bloqueado` | `un_ataque_es_403_bloqueado_y_no_llega_al_backend` | en verde |
| C-3 · A-4 · A-9 · A-10 | plantilla: `SecRuleRemoveById 200002`, `modsecurity off` en las ubicaciones internas | `el_422_del_backend_pasa_tal_cual`, `una_ruta_ajena_es_404…`, `otro_metodo_es_405…`, `con_el_backend_apagado_es_502…`, `un_cuerpo_de_mas_de_1_mib_es_413…`, `una_ruta_demasiado_larga_es_414…`, `el_trace_id_sale_del_traceparent`, `un_traceparent_mal_formado…` | en verde |
| C-4 · A-6 | ajustes: `SecAuditLogParts AHZ` · plantilla: `modsecurity_transaction_id "$traza"` | `el_registro_del_waf_lleva_la_traza_y_la_regla_sin_authorization` | en verde |
| C-5 · A-5 · K-4 · K-9 | plantilla: `SecRequestBodyNoFilesLimit 1048576`, `SecArgumentsLimit 10000` | `una_particion_de_200_anotaciones_de_casi_1_mib_pasa` | en verde |
| C-6 · A-9 · K-7 | plantilla: `modsecurity off` en `/_gateway/rechazado` | `una_ruta_con_puntos_es_400_en_problem_json_y_no_una_respuesta_vacia` | en verde |
| C-7 · A-2 | plantilla: `SecRuleEngine ${FLORLOGIC_WAF_MODO}` · `compose.yaml`: `FLORLOGIC_WAF_MODO` | `en_deteccion_el_ataque_llega_al_backend_y_queda_anotado` | en verde |
| C-8 · A-5 · K-9 | plantilla: `SecRequestBodyNoFilesLimit 1048576` | `un_ataque_en_la_ultima_anotacion_de_casi_1_mib_es_403` | en verde |
| C-9 · A-4 · A-14 · K-8 | plantilla: reglas `10002`, `10003` · ajustes: `SecRequestBodyJsonDepthLimit 512` | `un_cuerpo_que_no_es_utf8_simple_es_400_y_no_llega_al_backend` (UTF-16 con BOM, UTF-16LE, UTF-8 con BOM), `un_json_de_520_niveles_que_el_waf_no_lee_llega_al_backend_y_es_422`, `el_422_del_backend_pasa_tal_cual` | en verde |
| C-10 · A-5 | plantilla: regla `10001` (`status:413`) | `mas_de_10000_campos_es_413_rechazado`, `la_mitad_de_la_particion_de_demasiados_campos_pasa` | en verde |
| C-11 · A-15 | plantilla: `SecRuleUpdateTargetByTag "OWASP_CRS" "!REQUEST_HEADERS:Authorization"` | `authorization_no_se_revisa_ni_va_al_registro` (con `<script>`, OBS-1-3) | en verde |
| C-13 · A-16 | CRS de la imagen (`920420`, `920340`), sin exclusión | `un_cuerpo_en_text_plain_es_403_bloqueado`, `un_cuerpo_sin_content_type_es_403_bloqueado` | en verde |
| C-12 · A-11 | plantilla: `SecRuleRemoveById 920350` | `con_host_por_ip_el_registro_no_trae_la_920350` | en verde |
| A-7 | `CanalPorGatewayTest`: `gatewayHacia`, `imagenDeCompose`, `entradaDelRegistro`, `basePorIp` | la clase misma | en verde |
| A-8 · K-10 | `compose.yaml`: `healthcheck`, `ports`, sin `NGINX_ENVSUBST_FILTER` · plantilla: `client_body_timeout 60s` | corrida real del 21-oct: `docker compose up -d --wait gateway` queda `healthy` en ~7 s; simulador por el gateway: 2 nuevas, 2 ya estaban, rechazada (código 1); ataque `403`; ruta ajena `404` | en verde (por partes: el 5432 del anfitrión está ocupado, como ya anotó el gateway) |
| A-12 | `compose.yaml`: `image: owasp/modsecurity-crs:4.30.0-nginx-alpine-202610180210` | `imagenDeCompose` (la prueba corre la misma imagen) | en verde |
| A-13 | `IA_LEEME.md`: «Ante un falso positivo del WAF» | lectura en la revisión | pendiente de la revisión |
| A-1 | `IA_TECNOLOGIAS-vigentes.md` (TEC-31 y su §5) · esta ESPEC (K-1, K-2, COH-1) | — (decisión de Juan) | pendiente de Juan: TEC-31 → `ACEPTADO`, `BB-08` |

## 10 · Observaciones de revisión

### Segmento 1 · revisión abierta el 21-oct-2026

```
## Segmento 1/1 — el WAF en el canal
Cubre: A-1 a A-16 · C-1 a C-13            No cubre: §7 (semilla del celular, ESC-38, línea LTS, TEC-17) → otros canales
Tamaño: 761 brutas / ~325 de lógica → 3 pasadas
Entrada: despliegue/compose.yaml:gateway
Orden de lectura: 1) compose.yaml (cómo se despliega y qué publica) 2) gateway/default.conf.template (el WAF dentro
  del server y los errores propios) 3) gateway/modsecurity-override.conf.template 4) CanalPorGatewayTest (arranque y
  casos del canal de antes) 5) CanalPorGatewayTest (casos del WAF y ayudas) 6) script y documentos
Diagrama: diagramas/IA_waf-segmento-1.drawio (páginas: contexto, configuración, prueba; PNG al lado)   (D-16)
Consultado: plantilla endpoint §1, §4 (EP-06, EP-07, EP-14, EP-19, EP-22), §11 · plantilla pruebas §2.1, §4 (PB-01,
  PB-02, PB-03, PB-10, PB-15, PB-16, PB-18), §8 · la imagen misma, en contenedores de borrador (cuatro vueltas, §5).
  El MCP «biblioteca» no está en esta sesión: no hay citas de libros.
Decisiones implícitas:
  · La prueba reutiliza tres gateways estáticos (el principal, el del backend apagado y el de DetectionOnly): arrancar
    el WAF cuesta ~6 s por contenedor [sin fuente; mismo patrón que el gateway principal, A-4 del gateway].
  · entradaDelRegistro espera 10 s como máximo (WaitingConsumer) [sin fuente; PB-10 pide esperar por condición].
  · healthcheck cada 5 s, 3 s de espera, 3 intentos [sin fuente; con --wait el gateway queda sano en ~7 s, medido].
  · Los msg de las reglas propias van sin espacios (Demasiados_campos, Cuerpo_ilegible_no_UTF8): una comilla simple
    cerraría la cadena de modsecurity_rules en Nginx [comprobado con nginx -t].
  · Los comentarios van dentro de modsecurity_rules, junto a cada regla: ModSecurity acepta líneas con # [comprobado].
  · @ParameterizedTest para las tres codificaciones de C-9: viene con JUnit (TEC-26), evita un bucle en la prueba (PB-18).
  · En la plantilla, las citas a esta ESPEC dicen «WAF A-n»; las que no tienen prefijo siguen siendo del gateway.
  · modsecurity-override.conf.template va sin el prefijo IA_: la imagen lo incluye por ese nombre exacto, igual que
    default.conf.template; la autoría va en su cabecera (OBS-1-11).
Lo imprevisto: el gateway nunca lanza: todo lo que no espera sale en problem+json (rechazado, bloqueado o
  no-disponible). Comprobado el 21-oct con la configuración real: Content-Type text/plain o ausente → 403 bloqueado
  (CRS 920420, 920340), que antes llegaba al backend (OBS-1-1); application/json con charset, cuerpo vacío y chunked
  pasan al backend.
Puntos sensibles: default.conf.template, bloque modsecurity_rules — confiabilidad (lo que se quita o se deja sin
  revisar: 200002, 200007, 920350, Authorization, respuestas) · reglas 10002/10003 — seguridad (K-8) ·
  SecRequestBodyNoFilesLimit — seguridad (K-9) · compose.yaml healthcheck — disponibilidad · CanalPorGatewayTest
  entradaDelRegistro — determinismo de la prueba
Matriz requisito → código → prueba: §9
```

Pasada 1 de 3: configuración (`compose.yaml`, `gateway/default.conf.template`, `gateway/modsecurity-override.conf.template`).

```
OBS-1-1 · question (menor, lo imprevisto): Content-Type distinto de JSON
  Ubicación: despliegue/gateway/default.conf.template (CRS completo, A-11)
  Requisito: A-11, C-3
  Detalle: con el WAF, un POST con Content-Type text/plain o sin Content-Type recibe 403 gateway/bloqueado (CRS
    920420 «content type not allowed», 920340 «content but missing Content-Type»). Antes llegaba al backend, que lo
    rechazaba porque solo consume application/json. El celular siempre manda application/json (con o sin charset,
    que pasa), así que el camino mínimo no se rompe; pero el sentido cambia: «parece ataque» en vez de «mal pedido».
  Criterio de cumplido: la ESPEC dice si se queda así (403) o si esas dos reglas se excluyen con su prueba (A-11).
  Origen: agente (lo imprevisto de la ficha, comprobado en contenedor)
  Estado: cumplida 21-oct al cerrar la revisión (Jeronimo: se queda el 403): A-16, C-13 y las pruebas
    un_cuerpo_en_text_plain_es_403_bloqueado y un_cuerpo_sin_content_type_es_403_bloqueado

OBS-1-2 · suggestion (menor, legibilidad): demasiados comentarios en la configuración del WAF
  Ubicación: despliegue/gateway/default.conf.template (bloque modsecurity_rules y ubicaciones internas) ·
    despliegue/gateway/modsecurity-override.conf.template
  Requisito: ninguno (estilo; skill: «los comentarios explican el porqué»; codigo-limpio: el porqué, no el qué)
  Detalle: la plantilla pasó de 36 a 57 líneas de comentario (0,64 → 0,67 por línea de configuración) y el archivo de
    ajustes tiene 10 de comentario para 2 directivas. Unas 10 de las 21 nuevas cuentan historia que ya está en la
    ESPEC (K-7) o dicen el qué («Bloquea (On) o solo anota»). La cabecera de cuatro líneas (CLAUDE.md) y el porqué de
    cada regla quitada o sin revisar (200002, 200007, 920350, Authorization, respuestas) se quedan.
  Criterio de cumplido: una línea de porqué por decisión, con su «WAF A-n»; plantilla ~46 líneas de comentario, ajustes
    ~7; ninguna directiva cambia y CanalPorGatewayTest sigue 23/23.
  Origen: usuario (Jeronimo, pasada 1: «están llenos de líneas con comentarios, ¿eso está bien?»)
  Estado: descartada 21-oct, aprobó el usuario (Jeronimo) · motivo: mantienen la proporción de comentarios que ya
    tenía el archivo (0,64 antes, 0,67 ahora, por línea de configuración)
```

Pasada 2 de 3 (`CanalPorGatewayTest`: arranque y los 9 casos de antes): sin observaciones nuevas.

Pasada 3 de 3 (`CanalPorGatewayTest`: casos del WAF y ayudas; script y documentos):

```
OBS-1-3 · issue (menor, pruebas y redacción): C-11 no prueba A-15, y el motivo de A-15 estaba mal dicho
  Ubicación: CanalPorGatewayTest.authorization_no_se_revisa_ni_va_al_registro · ESPEC A-15 · ronda 2 P4
  Requisito: A-15, C-11 (PB-03: la prueba falla si se rompe el comportamiento)
  Detalle: comprobado el 21-oct con la configuración real y con la misma sin la línea de A-15: el token de C-11
    («…ab--cd' OR 1=1 --») y un «() { :; }; …» pasan en los dos casos, porque la 942100 (inyección SQL) no revisa
    Authorization; así que C-11 pasaría aunque se quitara A-15. En cambio «…x<script>alert(1)</script>» en
    Authorization da 403 sin A-15 (regla 941110, XSS, que sí revisa todas las cabeceras) y pasa con ella. El contexto
    de la ronda 2 P4 decía que un «--» del token podía parecer comentario SQL: con CRS 4.30.0 a paranoia 1 no es así.
    Lo que A-15 sí asegura: ninguna regla que revise todas las cabeceras puede poner un pedazo de la credencial en el
    registro ni bloquear por ella; la contraparte es que un ataque escondido en Authorization pasa al backend.
  Criterio de cumplido: C-11 usa un valor que la 941110 bloquearía (comprobado: 403 sin A-15, 200 con ella), y A-15 y
    el diccionario dicen el motivo comprobado. O, si el usuario reabre la ronda 2 P4, se quita A-15 y C-11 afirma
    el 403.
  Origen: agente (pasada 3, al releer el caso antes de explicarlo; comprobado en contenedor)
  Estado: cumplida 21-oct al cerrar la revisión (Jeronimo: se mantiene A-15): A-15 con el motivo corregido, el
    comentario de la plantilla y C-11 con «<script>», que la 941110 bloquearía sin A-15

OBS-1-4 · nitpick (nit, recursos): entradaDelRegistro deja un seguidor del registro por llamada
  Ubicación: CanalPorGatewayTest.entradaDelRegistro
  Requisito: PB-10
  Detalle: cada llamada engancha un WaitingConsumer con followOutput, que no se suelta hasta que el contenedor se para
    en @AfterAll. Son tres llamadas por corrida y Testcontainers los cierra al parar; no afecta el resultado.
  Criterio de cumplido: o se descarta con este motivo, o la ayuda reutiliza un solo seguidor por contenedor.
  Propuesta del agente: descartar (tres hilos de corta vida; cerrarlos a mano agrega código sin efecto observable)
  Estado: descartada 21-oct, aprobó el usuario (Jeronimo) · motivo: tres seguidores por corrida, que Testcontainers
    cierra al parar el contenedor; cerrarlos a mano agrega código sin efecto observable
```

**Revisión cerrada el 21-oct-2026 por Jeronimo**, con «aplicar»: OBS-1-1 y OBS-1-3 cumplidas, OBS-1-2 y OBS-1-4
descartadas con su motivo. Sigue la verificación (etapa 6).

**Abiertas en la verificación (agente de coherencia, 21-oct; veredicto «pasa», ningún bloqueante).** Formato corto.

| OBS | = COH | Etiqueta (sev.) | Qué | Propuesta · criterio de cumplido | Estado |
|---|---|---|---|---|---|
| OBS-1-5 | COH-17 | question (mayor si fallara) | Con `core.autocrlf=true`, los dos archivos llegan en CRLF a Windows: ¿los acepta ModSecurity? | **comprobado 21-oct**: con los dos archivos en CRLF el gateway arranca (3 reglas en línea, 855 de CRS), la dorada pasa, el ataque da `403` y el BOM `400` | cerrada sin cambios (comprobado) |
| OBS-1-6 | COH-18 | todo (menor) | §5 explica los 0,14 s del `413` diciendo que la `10001` corre después de CRS; con 6,4 s para ~7.600 campos no cuadra: puede que las reglas del `server` corran antes que CRS | medir 9.999 frente a 10.000 campos y corregir §5 y §7 | movida a §7 (aprobó el usuario, 21-oct) y §5 dice «orden sin comprobar» |
| OBS-1-7 | COH-19 | issue (menor) | C-8 manda ~870 KiB: con `SecRequestBodyNoFilesLimit 920000` las pruebas seguirían en verde y el tramo final del MiB pasaría sin revisar | llevar la nota de la última anotación hasta casi 1 MiB, con el ataque al final | cumplida 21-oct: C-8 manda ~1.048.000 bytes con el ataque en el último campo, y lo afirma |
| OBS-1-8 | COH-20 | issue (menor) | Con `SecAuditLogParts ACHZ` (cuerpo al registro) C-4 seguiría en verde: solo busca el secreto de `Authorization` | C-4 afirma además que la entrada no trae `jti-5b2e`, que solo está en el cuerpo | cumplida 21-oct: C-4 afirma que la entrada no trae `jti-5b2e` |
| OBS-1-9 | COH-21 | issue (menor) | El paso 8 de `IA_probar-canal.sh` no mira el código: con `DetectionOnly` en el entorno el ataque llegaría y el guion diría «Canal OK.» | `-w '%{http_code}'` y salir con error si no es `403` | cumplida 21-oct: el paso 8 sale con error si el código no es `403` |
| OBS-1-10 | COH-22 | question (menor, riesgo sobre A-6) | El «fragmento» del registro es el valor **entero** del campo: comprobado 21-oct, una nota de 4.000 caracteres que dispara una regla deja un `data` de 4.085 en el registro. En un falso positivo, eso es un dato legítimo de hasta 4 KiB, en el registro local | A-6 y el diccionario dicen «el valor entero del campo»; el riesgo queda anotado (A-6 es de la ronda 1 P6, no se reabre sin el usuario) | cumplida 21-oct (redacción): A-6, §7, diccionario, `IA_LEEME.md` y el archivo de ajustes |
| OBS-1-11 | COH-23 | nitpick (nit) | §8 dice «A-1 a A-15 · C-1 a C-12» y «88/88»; A-2 y A-7 dicen «un segundo gateway» para C-7 (es el tercero); A-7 y la fila «Prueba» de §5 no nombran C-12 ni C-13; `modsecurity-override.conf.template` va sin `IA_` porque la imagen exige ese nombre (decisión implícita sin anotar) | redacción | cumplida 21-oct (redacción): §8, A-2, A-7, §5, ficha del segmento |

Sin prueba, anotado por el agente: A-9 (`SecResponseBodyAccess Off`, sin reglas de salida) no tiene mutación, porque provocar
una regla de salida exige un backend que filtre datos; y nadie manda un ataque por h2c con conocimiento previo (la imagen
trae `http2 on`). Los dos van a §7.

## 11 · Informe de verificación

### Segmento 1 · verificación del 21-oct-2026

**Paso 1 · observaciones aplicadas** sobre los mismos archivos: OBS-1-1 (A-16, C-13 y dos pruebas), OBS-1-3 (A-15 con el
motivo corregido, C-11 con `<script>`), OBS-1-7 a OBS-1-11. Descartadas con aprobación: OBS-1-2, OBS-1-4. Movida a §7:
OBS-1-6. Cerrada por comprobación: OBS-1-5 (CRLF).

**Paso 2 · lista de 22 puntos** (`investigacion/05` §4.1):

| # | Punto | Resultado |
|---|---|---|
| 1 | Requisito → código → prueba | Sí: C-1 a C-13 con su prueba (§9). A-8 y K-10 por la corrida real con el `compose.yaml` real; A-13 por lectura en la pasada 3 |
| 2 | Todo bloque tiene padre | Sí: cada directiva del WAF cita su «WAF A-n» |
| 3 | Lo de fuera, con destino | Sí: §7 |
| 4 | Nada «por si acaso» | Sí: cada ajuste nace de un hallazgo comprobado (K-3 a K-10, OBS-1-1) o de una decisión |
| 5 | Camino feliz de punta a punta | Sí: C-1 y la corrida real (simulador → gateway con WAF → backend → PostgreSQL) |
| 6 | Casos borde | Sí: codificaciones, borde del MiB, tope de campos y su mitad, ruta con `..`, `Content-Type`, `Host` por IP, `Authorization`, 520 niveles |
| 7 | Fallos externos definidos | Sí: backend apagado `502`; `DetectionOnly`; `healthcheck`; sin la variable de modo, Nginx no arranca (fallo temprano) |
| 8 | Concurrencia e idempotencia | N. A. en el WAF (no guarda estado); la carga va a §7 (ESC-38) |
| 9 | Las pruebas fallarían si se rompe | Sí: 11 mutaciones (quitar 920350, el tope de cuerpo, bajarlo a 920.000, 10002/10003, la exclusión de `Authorization`, `AHZ`, `modsecurity off`, el `413` de la 10001, el `error_page 403`, activar 200002 y 200007): las 11 hacen fallar su caso. A-9 sin mutación (§7) |
| 10 | Sin pruebas debilitadas | Sí: solo `disabledWithoutDocker`, como el resto del repo |
| 11 | Build y pruebas en verde | Sí: `./mvnw clean verify` 90/90 (con `-Dpicocli.ansi=false`, por el `SimuladorTest` que falla en esta consola desde antes, §7 del gateway); `CanalPorGatewayTest` 25/25 contra Docker real |
| 12 | Arquitectura y módulos | Sí: ningún código Java de producción cambió; la prueba vive en `ingesta` (A-7); `ModulosTest` en verde |
| 13 | Sin sobreingeniería | Sí: dos archivos de configuración y una prueba; sin imagen propia ni guiones de arranque |
| 14 | Sin duplicación | Sí: los tres gateways salen de `gatewayHacia`; los datos, de `particionDe` |
| 15 | Entrada validada, salida codificada | Sí: la traza solo admite hex; los errores son textos fijos en `problem+json`; el registro sin cabeceras ni cuerpos |
| 16 | Autorización | N. A.: la credencial es del backend (A-15) |
| 17 | Sin secretos ni datos sensibles en registros | Sí, con riesgo anotado: `Authorization` nunca (C-4, C-11); el cuerpo nunca (C-4, OBS-1-8); en un falso positivo, el valor entero del campo (OBS-1-10, §7) |
| 18 | Dependencias nuevas verificadas | Sí: la imagen, en Docker Hub (fecha fija, amd64 y arm64), con TEC-31 `PROPUESTA` y visto de Juan (riesgo de COH-1); ninguna dependencia Maven nueva |
| 19 | Nombres del glosario | Sí: diccionario al día (WAF, modo, registro, falso positivo, puntuación de anomalía, `gateway/bloqueado`, reglas) |
| 20 | Comentarios con el porqué | Sí: OBS-1-2 descartada con aprobación (misma proporción que el archivo) |
| 21 | Explicaciones de la revisión en el código | Parcial: la puntuación de anomalía y el «chain = Y» se explicaron en la revisión y quedaron en el diccionario, no en la plantilla, por la decisión de OBS-1-2 de no crecer los comentarios. La corrección de A-15 sí está en el comentario |
| 22 | El usuario resumió el flujo | No: el paso 6 se omitió (22-oct, decisión de Jeronimo); dio el archivo 1 (50 %) |

**Paso 3 · build:** `./mvnw clean verify` 90/90 (contrato 24, backend 46 con `CanalPorGatewayTest` 25/25, servicios 2,
simulador 18), con Docker: no se saltó ninguna. Corrida real (21-oct) por partes, con el servicio `gateway` del
`compose.yaml` real; el guion entero no corre en este equipo porque el 5432 está ocupado.

**Paso 4 · coherencia:** agente aparte, veredicto **pasa** (ningún bloqueante); sus siete hallazgos son OBS-1-5 a OBS-1-11.
Dos se comprobaron en contenedor: CRLF (sin cambio) y el valor entero en el registro (redacción y riesgo).

**Paso 5 · completitud:**

| Grupo | Peso | Resueltas |
|---|---|---|
| Cumplidas o cerradas: OBS-1-1, 1-3, 1-5, 1-7, 1-8, 1-9, 1-10 (menores) · 1-11 (nit) | 7,5 | 7,5 |
| Descartadas o movidas con motivo y aprobación: OBS-1-2, 1-6 (menores) · 1-4 (nit) | 2,5 | 2,5 |

`Completitud = 100 · 10 / 10 = 100 %` · ninguna bloqueante · descartes 25 % del peso (menos del 30 %).

**Paso 6 · explicación:** omitida el 22-oct por decisión de Jeronimo, para dejar el código listo para subir. Alcanzó a
dar el archivo 1, el servicio `gateway` de `compose.yaml`: práctica incompleta (0,5) y teoría incompleta (0,5), 50 %.
Explicó el WAF en la puerta y el modo; faltaron la imagen con fecha fija, el `80:8080` sin privilegios, el
`healthcheck` propio y los dos montajes, con sus ideas (artefacto inmutable, menor privilegio, sonda de salud,
extender sin modificar). Los archivos 2 a 4 (bloque del WAF, archivo de ajustes y `CanalPorGatewayTest`) no se
explicaron. Sin el 80 % el segmento no se acepta (etapa 7).

**Integración (22-oct):** se trajo `origin/codigo-desarrollo` (refactor del hexágono por módulo, 4 commits) sin
commit. Hubo tres conflictos, solo en documentos (`IA_LEEME.md`, `IA_RETOMAR-AQUI.md`, `IA_DICCIONARIO.md`),
resueltos conservando los dos lados. `./mvnw clean verify` 122/122 con Docker, `CanalPorGatewayTest` 25/25. Aquí
se actualizaron los nombres que cambió el refactor: `ParticionesControlador` y `NodoDeFincaAdaptadorHttp`.

```
Coherencia: pasa (COH bloqueantes: 0)
Completitud = 100 %   Explicación = omitida (archivo 1: 50 %)   Build: ok   Pruebas: 90/90 (122/122 integrado)   Mutaciones: 11/11
```
