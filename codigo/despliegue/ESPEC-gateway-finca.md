# ESPEC · gateway de la finca (TX-1, camino mínimo)

REGISTRO DE IA · NO ES FUENTE — lo genera la skill DetallarParaCodificar, 20-oct-2026, a pedido del equipo con visto de Juan.
Estado de revisión: SIN REVISAR
Manda por encima: `IA_00-LEEME-transacciones.md` (TX-1, TEC-15, TEC-28) · `codigo/contrato/ESPEC-contrato-ingesta.md` (la ruta, los tiempos y los topes) · `ADR.xlsx`.

## Ficha
| Campo | Valor |
|---|---|
| Elemento | El gateway de la finca (Nginx, TEC-15) delante de `POST /ingesta/v1/sesiones/{id}/particiones`, con su lugar en `despliegue/compose.yaml` |
| **Definición acordada** | El gateway de la finca es la única puerta de red del nodo de finca. Recibe la partición que manda el simulador, la reenvía sin cambiarla al backend y devuelve su respuesta tal cual. Rechaza todo lo que no sea la ruta de ingesta. No decide si la partición vale, no verifica la credencial todavía, no cifra (TLS va después) y no sirve la aplicación web (acordada 20-oct, 1.ª vuelta) |
| Diccionario | `.claude/contexto/IA_DICCIONARIO.md` |
| Plantilla | `endpoint`, adaptada: solo ⟦2⟧ ruta, ⟦5⟧ errores, ⟦9⟧ límites y tiempos y ⟦12⟧ correlación; ⟦11⟧ (frontera hexagonal) no aplica porque el gateway no tiene código Java · `pruebas` para la prueba de punta a punta |
| Dominio | Entra: el puerto del nodo de finca, la ruta de ingesta, el reenvío al backend y los errores que genera el propio gateway · Sale: la configuración de Nginx, su servicio en Compose y su prueba · Fuera: lo que el backend hace por dentro, la aplicación web, la herramienta de análisis |
| Generalidad | Configuración casi línea por línea (Nginx y Compose) y su prueba de punta a punta |
| Para qué | Especificar para programar |
| Estado | **LISTA PARA CÓDIGO** (20-oct) |
| **Etapa actual** | 6 Verificación |
| **Dónde retomar** | Verificación del Segmento 1, paso 6: la explicación del usuario (plantilla, prueba y servicio de Compose). Lo demás de la verificación pasó (§11) |
| Precisión | **85,3 %** (verificador nuevo, 20-oct: cobertura 71,0 · contradicciones 100 · prácticas 92,0 · trazabilidad 83,3). Antes de los arreglos: 78,6 %. Coherencia: COH-1 (bloqueante) cerrado; ningún bloqueante abierto |
| Completitud | **100 %** (20-oct, §11) · Explicación: pendiente |
| Avance por etapa | Tamaño ✔ · Consulta ✔ (mapa confirmado 20-oct) · Preguntas ✔ (ronda 1, 3 preguntas, 85,3 %) · Generación: Segmento 1 de 1, escrito · Revisión ✔ (cerrada 20-oct, 5 OBS) · Verificación: en curso · Aceptación — |
| Ambiente | JDK 27 en `C:\Estudio\Software_2\jdk-27` (OpenJDK 27+35) · Docker Desktop 29.9.1 con Compose v5.6.0 (CLI fuera del PATH: `…\AppData\Local\Programs\DockerDesktop\resources\bin`) |

## 1 · Propósito
Poner el gateway de la finca en el camino mínimo de TX-1: que la partición del simulador entre al nodo de finca
por una sola puerta, como en el paso 4 de `IA_TX1-sincronizacion.md` («el gateway termina el TLS, limita la tasa
y enruta»), empezando solo por enrutar. Fuente: TEC-15, TEC-28, `IA_FUNCIONAMIENTO-ESPERADO.md` (API Gateway de la
finca: «único punto de entrada a la finca»).

## Camino mínimo funcional (D-17)
1. El simulador hace `POST /ingesta/v1/sesiones/{id}/particiones` al gateway, no al backend.
2. El gateway deja pasar solo esa ruta y solo con `POST`; lo demás lo rechaza ahí mismo.
3. Reenvía los bytes tal cual al backend-finca por la red interna.
4. El backend guarda y responde; el gateway devuelve la respuesta sin cambiarla.

**Después (fuera a propósito):** TLS con la autoridad certificadora de la finca (TEC-16) · tasa por dispositivo
(`429`) · tamaño con su `413` propio · credencial con `auth_request` (Identidad y permisos) · aplicación web y
herramienta de análisis · WAF · logs JSON · imagen del backend con Jib.

## 2 · Decisiones
| Id | Afirmación | Fuente | Estado |
|---|---|---|---|
| A-1 | El gateway entra al camino mínimo de TX-1 (paso 4 de `IA_TX1`), sobre TEC-15 y TEC-28. Juan ajusta la fila TX-1 del LEEME de transacciones (carpeta humana; K-1) | visto de Juan, 20-oct-2026 (encuadre) | decidida |
| A-2 | **El backend corre en el anfitrión; el gateway, en Compose.** Compose levanta `base` y `gateway`; el backend sigue con `java -jar … --server.port=18080`. El gateway lo alcanza por la variable `FLORLOGIC_BACKEND` (Compose la fija en `host.docker.internal:18080`, con `extra_hosts: host-gateway` para Linux) y publica `${FLORLOGIC_GATEWAY_PUERTO:-80}:80`. La configuración de Nginx no cambia con el lugar del backend: solo esa variable | ronda 1 P1 (★) | decidida |
| A-3 | **Todo error que genera el propio gateway sale en `application/problem+json`**, con `Cache-Control: no-store`, el código HTTP del caso y `traceId` (la traza de A-11). Dos `type`: `https://florlogic.co/problemas/gateway/rechazado` (lo que el gateway no deja pasar) y `https://florlogic.co/problemas/gateway/no-disponible` (el backend no está). Campos: `type`, `title`, `status` y `traceId`; sin `detail` ni datos internos (A-36 del contrato). **Los errores del backend pasan tal cual**: el gateway nunca los reemplaza (C-2). Qué código va a qué `type`: A-9 | ronda 1 P2 (★) · contrato A-22, A-36 | decidida |
| A-4 | **La prueba de punta a punta vive en `backend-finca`**: `ingesta/CanalPorGatewayTest`, junto a `CanalParticionesTest`. Arranca el backend con `@SpringBootTest` (puerto al azar) y PostgreSQL de Testcontainers, y un contenedor de Nginx con la plantilla real de `../despliegue/gateway/` y la **misma imagen que `compose.yaml`**, que la prueba lee de ahí. **Orden:** primero el contexto de Spring (da el puerto), después `Testcontainers.exposeHostPorts(puerto)`, después arranca Nginx con `FLORLOGIC_BACKEND=host.testcontainers.internal:<puerto>`; por eso Nginx no es un `@Container` estático. Para C-4, un segundo Nginx apunta a `127.0.0.1:9`, donde nadie escucha. Sin cambios de pom. Se salta sin Docker, como las demás | ronda 1 P3 (★) · PR-04 · COH-13 | decidida |
| A-5 | **Solo pasa una ruta y un método.** Pasa `POST` a `"^/ingesta/v1/sesiones/[^/;]+/particiones$"` (sin `;`, para que `..;` no se cuele como segmento; entre comillas porque el `;` cerraría la directiva; OBS-1-18); el mismo camino con otro método es `405` con `Allow: POST` (por `if ($request_method != POST) { return 405; }`; `limit_except` daría `403`); toda otra ruta es `404`, incluida `/actuator/…`, que nunca se expone por el gateway. El `{id}` no se valida en el gateway: es regla del backend (su `422`) | encuadre (recorrido, paso 2) · contrato A-12 | supuesto |
| A-6 | **Reenvía sin tocar lo que es de punta a punta.** El cuerpo y las cabeceras de punta a punta del cliente (`Content-Type`, `Authorization`, `traceparent`…) llegan iguales al backend; Nginx no pasa las de salto (`Connection`, `Upgrade`, `Keep-Alive`…), conserva el nombre del `Host` del cliente, sin el puerto (`proxy_set_header Host $host`) y agrega `X-Forwarded-For` (la IP que ve el gateway, `$remote_addr`: es el primer salto y no se fía del que mande el cliente; OBS-1-17), `X-Forwarded-Proto` y `X-Request-Id` (= la traza de A-11). Lee el cuerpo entero antes de reenviar (`proxy_request_buffering`, por defecto), así que el backend nunca recibe una partición a medias. La respuesta del backend vuelve con su código, su cuerpo y sus cabeceras de punta a punta (`Cache-Control: no-store`, contrato A-13); Nginx pone su propio `Date` y `Server` | encuadre (pasos 3 y 4) · COH-10 | supuesto |
| A-7 | **El gateway no dice qué versión corre** (`server_tokens off`), y sus errores no llevan páginas de Nginx. La cabecera `Server: nginx` se queda: la Nginx abierta no tiene directiva para quitarla (haría falta el módulo `headers-more`, que no está en la imagen oficial) | contrato A-36 («nunca detalles internos») · `plantillas/endpoint.md` EP-07 · comprobado 20-oct | supuesto |
| A-8 | **El simulador apunta al gateway por defecto**: `sincronizar --url` pasa de `http://localhost:18080` (el backend) a `http://localhost:${env:FLORLOGIC_GATEWAY_PUERTO:-80}` (el gateway; picocli lee la variable de entorno). `scripts/IA_probar-canal.sh` levanta `base` y `gateway` con Compose y sincroniza por el gateway | encuadre (paso 1) · COH-15 | supuesto |
| A-9 | **Regla general de los errores propios (amplía A-3):** lo que el cliente pidió mal va a `gateway/rechazado` (`400` petición ilegible, `404` ruta ajena, `405` método ajeno, `413` cuerpo mayor que el tope, `414` ruta demasiado larga, `501` `Transfer-Encoding` desconocido, `505` versión HTTP; el `494` de cabeceras enormes sale como `400`); lo que falla del lado del nodo va a `gateway/no-disponible` (`500`, `502` el backend no contesta, `503`, `504` se pasó la espera). **Si el cuerpo deja de llegar, Nginx registra `408` y corta la conexión sin responder** (comprobado; OBS-1-9): el celular lo ve como transporte y reintenta (contrato A-54). Se escriben en **ubicaciones internas por URI** (`/_gateway/rechazado`, `/_gateway/no-disponible`, marcadas `internal`, que desde fuera dan `404`), no en ubicaciones con nombre: con una ubicación con nombre, una línea de petición ilegible o demasiado larga se vuelve `500` en HTML («empty URI in redirect to named location», comprobado el 20-oct) | COH-8 · ronda 1 P2 (regla general) | supuesto |
| A-10 | **Confiabilidad del reenvío.** Conectar al backend tiene 2 s (peor caso con dos direcciones: 2 + 2 + 25 < 30 s del celular; OBS-1-12); el backend se declara con `max_fails=0`, para que un fallo no lo marque caído 10 s cuando ya volvió (`host.docker.internal` resuelve a IPv4 y a IPv6: Nginx ve dos destinos, comprobado); `proxy_next_upstream error timeout` solo prueba la otra dirección si la conexión falló, y **nunca reenvía un `POST` que ya llegó al backend** (sin `non_idempotent`: el reintento es del celular, contrato A-41); `proxy_ignore_client_abort on`: si el celular corta, el gateway igual espera la respuesta del backend y la deja en el registro; búfer del cuerpo de 1 MiB, para que una partición no pase por un archivo temporal; **sin `keepalive`** hacia el backend en el camino mínimo, porque una conexión que Tomcat ya cerró daría `502` a un `POST` que no se reintenta | COH-2, COH-3, COH-4, COH-12 | supuesto |
| A-11 | **La traza.** `traceId` es el trace-id del `traceparent` de la petición si viene bien formado (W3C: `00-<32 hex>-<16 hex>-<2 hex>`), y si no, el `$request_id` de Nginx (32 hex al azar). La misma traza va en el JSON de los errores (A-3), en `X-Request-Id` hacia el backend (A-6) y en cada línea del registro de acceso, junto con `$upstream_status` y `$upstream_response_time`; el registro nunca lleva `Authorization` ni el cuerpo | COH-4, COH-5 · contrato A-29, A-36 | supuesto |
| A-12 | **Compose del nodo.** `gateway` y `base` llevan `restart: unless-stopped` (vuelven solos tras un corte de luz, ES-S03); `gateway` usa `NGINX_ENVSUBST_FILTER: "^FLORLOGIC_"`, para que la plantilla solo sustituya variables propias; `base` publica `127.0.0.1:5432:5432`, para que la base no sea otra puerta hacia la red | COH-11, COH-15 | supuesto |

## 3 · Variables
| Nombre | Valor | Tipo | Dueño |
|---|---|---|---|
| Imagen de Nginx | `nginx:1.30.6-alpine` (estable del 14-oct-2026; amd64 y arm64) | fija; se sube con cada estable | TEC-15 · `IA_TECNOLOGIAS-vigentes.md` §3.2 |
| Espera de lectura y de envío al backend | 25 s (`proxy_read_timeout`, `proxy_send_timeout`): más que el plazo de procesar del contrato (20 s) y menos que la espera del celular (30 s). Ojo: Nginx mide **el tiempo entre dos lecturas**, no el total; con una respuesta de una sola vez, como la de una partición, coinciden. El `503` propio del backend a los 20 s todavía no existe (contrato A-40, «después») | fija · supuesto | contrato A-40 · mapa confirmado 20-oct |
| Espera para conectar al backend | 2 s (`proxy_connect_timeout`); con dos direcciones, 2 + 2 + 25 < 30 | fija · supuesto | A-10 · OBS-1-12 |
| Tope del cuerpo | 1 MiB (`client_max_body_size`), igual que `max_bytes_particion` hoy. Es un **techo** del gateway: cuando el contrato anuncie su máximo por sesión (COH-6), el techo queda por encima | fija · supuesto | contrato §3 · mapa confirmado 20-oct |
| Búfer del cuerpo | 1 MiB (`client_body_buffer_size`): hasta 1 MiB de memoria por petición en curso | fija · supuesto | A-10 |
| Puerto publicado | `${FLORLOGIC_GATEWAY_PUERTO:-80}` | configurable por entorno | A-2 |
| Línea de petición y cada cabecera | 8 KiB (`large_client_header_buffers 4 8k`, valor por defecto de Nginx): más larga → `414` (ruta) o `400` (cabecera, el `494` interno) | fija · por defecto de Nginx | A-9, C-6 |
| Espera del cuerpo del cliente | 60 s entre dos lecturas (`client_body_timeout`, por defecto): si el cuerpo deja de llegar, Nginx registra `408` y **corta la conexión sin responder** (comprobado). Es más que los 30 s del celular, que se rinde antes; se ajusta con los plazos de A-40 | fija · por defecto de Nginx | A-9 · §7 |
| Dirección del backend | `FLORLOGIC_BACKEND`; Compose: `host.docker.internal:18080` | configurable por entorno | A-2 |

## 4 · Comportamiento
- **C-1** Dado el backend arriba · cuando el simulador manda una partición válida al gateway · entonces recibe `200` con el conteo y `Cache-Control: no-store`, igual que sin gateway (cubre A-1, A-6).
- **C-2** Dado el backend arriba · cuando la partición tiene un fallo de forma · entonces recibe el `422` «rechazado» del backend tal cual, con su `type` `…/ingesta/rechazado` (cubre A-3, A-6).
- **C-3** Dada una ruta distinta de la de ingesta (por ejemplo `/actuator/health`), o la ruta de ingesta con un método distinto de `POST` · cuando llega al gateway · entonces el gateway la rechaza y el backend no se entera: `404`, o `405` con `Allow: POST`, en `problem+json` con `type` `…/gateway/rechazado` (cubre A-3, A-5, A-9).
- **C-4** Dado el backend apagado (la conexión se rechaza) · cuando llega una partición · entonces el gateway responde `502` en `problem+json`, `type` `…/gateway/no-disponible`, con `traceId`; el simulador reintenta a mano (cubre A-2, A-3, A-10). El backend colgado (`504`) está en §7.
- **C-5** Dada una petición que declara con `Content-Length` un cuerpo mayor que 1 MiB, **en cualquier ruta** · cuando llega al gateway · entonces responde `413` en `problem+json`, `type` `…/gateway/rechazado`, sin reenviarla: Nginx mira el tamaño declarado antes que la ruta. Un cuerpo `chunked` solo se mide al leerlo: en la ruta de ingesta da `413`; en una ruta ajena, `404` (comprobado; OBS-1-19). Cubre A-3, A-9; el `413 particion-muy-grande` propio del contrato es «después».
- **C-6** Dada una ruta de más de 8 KiB · cuando llega al gateway · entonces responde `414` en `problem+json`, `type` `…/gateway/rechazado`, y no un `500` en HTML (cubre A-9).
- **C-7** Dada una petición con `traceparent` bien formado · cuando el gateway genera un error · entonces el `traceId` es el trace-id de esa cabecera (cubre A-11).

## 4b · Ejemplo dorado de un error del gateway
`POST /ingesta/v1/sesiones/0192f0a1-7c00-7000-8000-00000000a001/particiones` con el backend apagado y
`traceparent: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01`:

```
HTTP/1.1 502 Bad Gateway
Server: nginx
Content-Type: application/problem+json
Cache-Control: no-store
```
```json
{"type":"https://florlogic.co/problemas/gateway/no-disponible","title":"El nodo de finca no responde","status":502,"traceId":"4bf92f3577b34da6a3ce929d0e0e4736"}
```

`GET` a la misma ruta, sin `traceparent` (la traza es el `$request_id`):

```
HTTP/1.1 405 Not Allowed
Content-Type: application/problem+json
Cache-Control: no-store
Allow: POST
```
```json
{"type":"https://florlogic.co/problemas/gateway/rechazado","title":"El gateway no deja pasar la petición","status":405,"traceId":"c39ef4402071afbfb2d4977deb46b623"}
```

Nginx escribe el texto de `return` byte a byte desde la plantilla, que está en UTF-8; JSON siempre va en UTF-8
(RFC 8259 §8.1), así que `application/problem+json` no necesita `charset`.

## 5 · Forma técnica
De la entrada a la salida. La plantilla `endpoint` se aplica solo en ⟦2⟧, ⟦5⟧, ⟦9⟧ y ⟦12⟧: aquí no hay
controlador, DTO ni puerto, porque el gateway no tiene código Java.

| Pieza | Archivo destino | Qué hace |
|---|---|---|
| Configuración del gateway | `despliegue/gateway/default.conf.template` | Plantilla que la imagen oficial de Nginx convierte en `/etc/nginx/conf.d/default.conf` al arrancar (sustituye solo `${FLORLOGIC_…}`, A-12; las variables propias de Nginx, como `$request_id`, no se tocan). Reemplaza la página de bienvenida de la imagen. Lleva el `upstream` del backend (A-10), los dos `map` (traza, A-11; `Allow`, A-5), el formato del registro (A-11), la ruta de ingesta (A-5, A-6, A-10), la ruta de todo lo demás (`404`) y las dos ubicaciones internas de error (A-3, A-9) |
| Servicio en Compose | `despliegue/compose.yaml` | Agrega `gateway` (A-2, A-12) y ajusta `base` (A-12) |
| Prueba | `backend-finca/src/test/java/co/florlogic/finca/ingesta/CanalPorGatewayTest.java` | C-1 a C-7 con HTTP, Nginx y PostgreSQL reales (A-4) |
| Cliente | `simulador/src/main/java/co/florlogic/simulador/Sincronizar.java` | Solo el valor por defecto de `--url` (A-8) |
| Corrida real | `scripts/IA_probar-canal.sh` | Sincroniza por el gateway (A-8) |
| Registro de tecnologías | `IA_TECNOLOGIAS-vigentes.md` | TEC-15 pasa a «En el código: SÍ» con la imagen y su versión; línea en §5 |
| Guía del código | `IA_LEEME.md` | Cómo levantar el gateway, la prueba nueva y el puerto |

**Cómo se ubica cada error (A-3, A-9).** `error_page` sin `=` conserva el código original y hace una redirección
interna a `/_gateway/rechazado` o `/_gateway/no-disponible`, que escriben el JSON con `return 200 '…'`; el código que
sale es el original, y el del cuerpo sale de `$status` (los dos leen el código del error, no el del `return`). El
`494` lleva `=400`. Con `proxy_intercept_errors` apagado (el valor por defecto), `error_page` solo toca los errores
que genera Nginx, nunca las respuestas del backend (C-2). En esas ubicaciones `types {}` vacío obliga a
`application/problem+json` aunque la ruta pedida termine en `.html`, y `add_header … always` hace que
`Cache-Control` y `Allow` salgan también en 4xx y 5xx. El `Allow` sale de un `map $status` que solo vale `POST`
para el `405` (una cabecera vacía no se envía).

**Comprobado el 20-oct** con `nginx:1.30.6-alpine` y una plantilla de borrador (fuera del repo, en el scratchpad
de la sesión): la plantilla se sustituye al arrancar; `POST` con el backend caído da `502` `gateway/no-disponible`
(Nginx prueba IPv4 y IPv6 antes de rendirse); `GET` a la ruta da `405` con `Allow: POST`; `/actuator/health` y
`/x.html` dan `404` `gateway/rechazado` con `application/problem+json`; un cuerpo de 1 MiB + 1 byte da `413`, también
en una ruta ajena; una ruta de 9.000 caracteres da `414`, una línea de petición ilegible da `400` y una cabecera de
9.000 caracteres da `400`, todos en `problem+json`; con `traceparent` válido, `traceId` es su trace-id, y con uno
malo, el `$request_id`; `/_gateway/rechazado` pedido desde fuera da `404`. Un `501` que genera el backend pasa sin
tocar, con su propio `Content-Type`. `host.docker.internal` alcanza al anfitrión en Docker Desktop.

**El nombre de la plantilla manda.** Se llama `default.conf.template`, sin prefijo `IA_`, como las migraciones
`V2__…sql`: la imagen la convierte en `conf.d/default.conf` y así **pisa** el `default.conf` de la imagen (su página
de bienvenida, `server_name localhost`). Con otro nombre, ese servidor seguiría vivo y atraparía las peticiones a
`localhost`: C-1 fallaría. La autoría de la IA va en su cabecera de comentarios.

**Cómo se comprueban las afirmaciones sin escenario propio.** A-7: C-1 comprueba que `Server` vale `nginx`, sin
versión. A-10 y A-12: por lectura de la plantilla y de `compose.yaml` en la revisión del segmento (sus efectos
—reintentos, reinicios— no se pueden provocar en el camino mínimo sin otra infraestructura). **Aislamiento de la
prueba (A-4):** cada caso empieza con la tabla `anotacion_recibida` vacía, como `CanalParticionesTest`; el Nginx de
la prueba recibe el mismo entorno que el de Compose (`FLORLOGIC_BACKEND` y `NGINX_ENVSUBST_FILTER`).

**Fuera del borde.** La regla del `{id}` y todo fallo de forma: el backend (`ControladorParticiones`, contrato
Segmento 2). La imagen del backend: TEC-28 con Jib, «después».

## 6 · Contradicciones y pendientes
Verificador y agente de coherencia del 20-oct (cierre de la ronda 1). Índice oficial: **78,6 %**; los arreglos se
aplicaron el mismo día con permiso (D-11) y falta volver a medir.

| Id | Qué | Tipo | Dueño | Estado |
|---|---|---|---|---|
| K-1 | A-1 mete el gateway al camino mínimo, pero la fila TX-1 del LEEME dice «aceptado solo … POST de una partición del simulador al backend-finca» y CLAUDE.md pide la fila `ACEPTADO` para programar | contradicción con el documento que manda | Juan | **cerrada 20-oct** con la decisión del encuadre («Sí, con visto de Juan»), reconfirmada al cierre de la ronda. **Pendiente de Juan:** fila TX-1 del LEEME → «AJUSTAR 20-oct · camino mínimo + gateway» |
| K-2 = COH-1 | C-4 decía «caído o que no responde → 502», pero un backend colgado es `504` | ambigüedad · bloqueante | IA | **cerrada 20-oct** (redacción, con permiso): C-4 = backend apagado → `502`; el colgado → §7 |
| COH-2, COH-3, COH-4, COH-12 | Confiabilidad del reenvío | calidad | IA | **incorporados** en A-10 |
| COH-5 | Traza desde `traceparent` | coherencia | IA | **incorporado** en A-11 |
| COH-7 | El tamaño va antes que la ruta | cohesión | IA | **incorporado** en C-5 |
| COH-8 | Errores propios en HTML | coherencia | IA | **incorporado** en A-9 (ampliado por la prueba: ubicaciones internas por URI) |
| COH-9 | `always`, `Cache-Control` duplicado, `403` de `limit_except` | implementación | IA | **incorporado** en A-5 y §5 |
| COH-10 | «Las cabeceras llegan iguales» | redacción | IA | **incorporado** en A-6 |
| COH-11, COH-15 | `restart`, filtro de la plantilla, base y simulador | confiabilidad · coherencia | IA | **incorporados** en A-8 y A-12 |
| COH-13 | Orden de arranque de la prueba e imagen única | cohesión | IA | **incorporado** en A-4 |
| COH-6, COH-14 | El `413` del contrato; el celular decide por `type` | — | — | en §7 |

## 7 · Fuera del dominio (para otro canal)
- La lista «después» del camino mínimo (arriba).
- **WAF con ModSecurity y OWASP CRS:** lo propone `IA_TECNOLOGIAS-C4.md` (consulta Q1), pero TEC-15 solo acepta Nginx. Entra solo con una TEC nueva aceptada por Juan.
  **Entró el 21-oct-2026** con su propia ESPEC, `despliegue/ESPEC-waf-finca.md` (TEC-31 propuesta, con visto de Juan; su COH-1). Ese segmento cambia, en este gateway: A-2 (el puerto dentro del contenedor pasa a 8080; fuera no cambia), A-3 y A-9 (un tercer `type`, `gateway/bloqueado`, para el `403` del WAF), A-4 y OBS-1-11 (la prueba lee otra imagen y monta dos archivos sueltos, no la carpeta), A-7 (la imagen trae `headers-more`: ya se podría quitar `Server`, no se hace), A-10 (el tiempo del WAF no entra en la suma 2 + 2 + 25 < 30 s), A-11 (el registro del WAF lleva el fragmento que disparó una regla, nunca `Authorization`), A-12 (sin `NGINX_ENVSUBST_FILTER`) y §3 (la imagen; `client_body_timeout 60s` queda fijo en la plantilla). Detalle: §5 de esa ESPEC, «Lo que este segmento cambia de la ESPEC del gateway».
- **Riesgo anotado (A-2):** mientras el backend corra en el anfitrión, su puerto 18080 sigue abierto en la máquina y el gateway todavía no es la única puerta. Se cierra cuando el backend entre a Compose con Jib (TEC-28), sin puerto publicado.
- **Verificar la credencial en el gateway** (`auth_request` contra Identidad y permisos): lo propone el mismo spike; va con la credencial (TEC-13, TEC-14).
- **Cuerpo incompleto (contrato A-54):** con el gateway delante, un cuerpo cortado nunca llega al backend; el gateway corta la conexión sin responder (registra `408`, A-9), no el `422 cuerpo_incompleto`. Se resuelve cuando se programe A-54.
- **El backend colgado (`504`) no tiene prueba automática** en el camino mínimo: exige un backend que tarde más de 25 s. Se verifica por lectura de la configuración; la prueba va con los plazos de A-40.
- **COH-6 · el `413` del contrato:** A-22 del contrato pone el `413 particion-muy-grande` después de 401…409, y `max_bytes_particion` es «configurable; anunciado al abrir». Cuando entre, el tope del gateway pasa a ser un techo contra abusos por encima del máximo configurable (o Juan ajusta A-22).
- **COH-14 · el celular decide por `type`:** el contrato A-41 dice «404 → abrir otra sesión» y «413 → partir»; el gateway da `404` y `413` con otro sentido (`gateway/rechazado`). El cliente tiene que mirar `type`, no solo el código (semilla celular). Y A-5 crece con la parte 2 del contrato (`/sesiones`, `…/cierre`, `/archivos`).
- **Cuando el backend entre a Compose:** `depends_on: {backend: {condition: service_healthy}}`; Nginx resuelve el nombre del `upstream` al arrancar y no arranca si el backend no existe (COH-11).
- **`keepalive` hacia el backend** (A-10): solo cuando su tiempo se fije por debajo del keep-alive de Tomcat (hoy sin fijar en `application.yaml`).
- **OBS-1-1 · la PWA y CORS:** si la aplicación de captura (PWA) se sirve desde otro origen que el gateway, el navegador manda un `OPTIONS` antes del `POST` y hoy recibe `405`. Se decide con la aplicación de captura: mismo origen o respuesta a `OPTIONS` en el gateway.
- **OBS-1-3 · la traza del lado del backend:** cuando el backend use `X-Request-Id` en sus registros (⟦12⟧), una prueba comprueba que la traza que devuelve el gateway es la misma que registró el backend.
- **OBS-1-10 · el 5xx del backend pasa tal cual:** cuando exista el `503` del backend (A-40), una prueba de que pasa por el gateway con su `type` y su `Retry-After` (hoy ninguna prueba vería un `proxy_intercept_errors on`).
- **OBS-1-13 · la imagen de PostgreSQL:** `postgres:16` no fija el parche, no lleva comentario `TEC-06` y `CanalPorGatewayTest` (como las otras pruebas) la repite a mano; aplicarle la regla de TEC-15 (fija en `compose.yaml`, leída por las pruebas).
- **OBS-1-21 · fin de línea (otro canal, decide Juan):** con `core.autocrlf=true` y sin `.gitattributes`, en Windows los `.sh` se bajan con CRLF y Git Bash los rompe (ya pasaba antes de este segmento). Propuesta: `.gitattributes` con `*.sh text eol=lf`.
- **Para otro canal (no es del gateway):** `SimuladorTest.sinArgumentosMuestraElUsoYTerminaBien` falla en consolas de Windows porque picocli pinta colores ANSI y la prueba busca el texto plano «Usage: simulador». Ya fallaba antes de este segmento; con `-Dpicocli.ansi=false` pasa.
- **Riesgo del entorno de desarrollo (Windows de esta sesión):** el 5432 lo tiene un PostgreSQL nativo y el 8080 un Apache. `compose up base` choca con el primero; la prueba automática no, porque usa puertos al azar.

## 8 · Plan de segmentos
Armado el 20-oct (etapa 4) con `verificacion.md` §1. Un solo segmento: el canal por el gateway de punta a punta
(D-17), con su prueba. Líneas brutas; entre paréntesis, las de lógica (pruebas ×0,5, declarativo ×0,25).

| Segmento | Archivos | Líneas (lógica) | Cubre | Estado |
|---|---|---|---|---|
| Segmento 1 · el canal por el gateway | nuevo `despliegue/gateway/default.conf.template` (~100) · cambia `despliegue/compose.yaml` (~45) · nuevo `backend-finca/…/ingesta/CanalPorGatewayTest.java` (~230) · cambia `simulador/…/Sincronizar.java` (2 líneas) · cambia `scripts/IA_probar-canal.sh` (~20 líneas) · documentos: `IA_TECNOLOGIAS-vigentes.md` (TEC-15), `IA_LEEME.md`, esta ESPEC · **diagrama:** `diagramas/IA_segmento-1.drawio` (páginas: contexto, configuración, prueba; PNG al lado) | ~400 (~180) → 1 pasada | A-1 a A-12 · C-1 a C-7 | **escrito 20-oct** (con el «sí» del usuario) · `CanalPorGatewayTest` 9/9 en verde con Docker real (OBS-1-14) |

## 9 · Matriz requisito → código → prueba
| Requisito | Archivo:símbolo | Prueba | Estado |
|---|---|---|---|
| C-1 · A-6 · A-7 | `gateway/default.conf.template`: `location ~ ^/ingesta/…`, `server_tokens off` | `CanalPorGatewayTest.la_particion_dorada_pasa_por_el_gateway_y_responde_200` | en verde |
| C-2 · A-3 (lo del backend pasa) | ídem, sin `proxy_intercept_errors` | `el_422_del_backend_pasa_tal_cual` | en verde |
| C-3 · A-5 · A-9 | `location /`, `if ($request_method != POST)`, `map $status $permitido`, `/_gateway/rechazado` | `una_ruta_ajena_es_404_rechazado_y_no_llega_al_backend`, `otro_metodo_es_405_con_allow_post` | en verde |
| C-4 · A-2 · A-10 | `upstream backend`, `/_gateway/no-disponible` | `con_el_backend_apagado_es_502_no_disponible` | en verde |
| C-5 · A-9 | `client_max_body_size 1m` (con `Content-Length`; la prueba manda la partición dorada rellenada) | `un_cuerpo_de_mas_de_1_mib_es_413_en_cualquier_ruta` | en verde |
| C-6 · A-9 | `error_page … 414 /_gateway/rechazado` (ubicación interna por URI) | `una_ruta_demasiado_larga_es_414_en_problem_json` | en verde |
| C-7 · A-11 | `map $http_traceparent $traza` | `el_trace_id_sale_del_traceparent`, `un_traceparent_mal_formado_no_entra_al_error` (OBS-1-5) | en verde |
| A-4 | `CanalPorGatewayTest` (orden de arranque, imagen leída de `compose.yaml`) | la clase misma | en verde |
| A-8 · A-12 (Compose) | `Sincronizar.url`, `compose.yaml`, `IA_probar-canal.sh` | corrida real del 20-oct: el servicio `gateway` de Compose + backend en el anfitrión; el simulador con su URL por defecto da 2 nuevas, 2 ya estaban y rechazada; `/actuator/health` por el gateway da 404; el registro trae `traza` y `backend=`; `restart=unless-stopped` | en verde (la base fue temporal en el 15432; el script entero no corre aquí porque el 5432 está ocupado) |
| A-10 · A-12 | plantilla (`max_fails=0`, `proxy_next_upstream`, tiempos, búfer) · `compose.yaml` (`restart`, filtro, `127.0.0.1`) | lectura en la revisión | pendiente de la revisión |

## 10 · Observaciones de revisión

### Segmento 1 · revisión abierta el 20-oct-2026
Pasada 1 de 1 (~400 brutas, ~180 de lógica). Orden: plantilla de Nginx → `CanalPorGatewayTest` → `compose.yaml` →
`Sincronizar` y el script → documentos. Filtro D-17: lo que no rompe el camino mínimo no reabre el segmento.

```
OBS-1-1 · question (menor, alcance): la aplicación de captura real (PWA en el navegador) y CORS
  Ubicación: despliegue/gateway/default.conf.template:50 (if $request_method != POST → 405)
  Requisito: A-5
  Detalle: si la PWA de app-captura se sirve desde otro origen que el gateway, el navegador manda antes del POST
    un OPTIONS de verificación (CORS, porque Content-Type es application/json); hoy el gateway lo responde 405 y la
    PWA no podría sincronizar. El simulador no pasa por CORS, así que el camino mínimo no se rompe.
  Criterio de cumplido: la ESPEC dice si la PWA comparte origen con el gateway o qué responde el gateway a OPTIONS.
  Origen: agente (pregunta interna «¿hay un caso sin manejar?»)
  Destino: §7 «después» (con la aplicación de captura); no reabre el segmento (D-17)
  Estado: movida a §7 (cierre de la revisión, 20-oct, decisión del usuario)

OBS-1-2 · nitpick (nit, redacción): el comentario promete un filtro que pone otro archivo
  Ubicación: despliegue/gateway/default.conf.template:4-5
  Requisito: A-12
  Detalle: dice que la imagen «solo sustituye las variables FLORLOGIC_», pero eso depende de que quien arranque el
    contenedor pase NGINX_ENVSUBST_FILTER (compose.yaml y la prueba lo hacen; la plantilla sola no lo garantiza).
  Criterio de cumplido: el comentario dice «si se arranca con NGINX_ENVSUBST_FILTER, como en compose.yaml».
  Origen: agente (pregunta interna «¿un nombre o comentario engañoso?»)
  Estado: cumplida 20-oct: default.conf.template:4-5 dice «si se arranca con NGINX_ENVSUBST_FILTER, como en compose.yaml y en CanalPorGatewayTest»

OBS-1-3 · todo (menor, pruebas): lo que el gateway agrega hacia el backend no se comprueba del lado del backend
  Ubicación: backend-finca/src/test/java/co/florlogic/finca/ingesta/CanalPorGatewayTest.java:88-98
  Requisito: A-6, A-11
  Detalle: C-1 prueba que la respuesta pasa por Nginx (Server: nginx) y que se guardan 2 filas, pero no que al
    backend le lleguen X-Request-Id (= la traza) ni X-Forwarded-For. Hoy el backend no los lee, así que ninguna
    prueba puede verlos sin agregar código al backend. No rompe el camino mínimo.
  Criterio de cumplido: cuando el backend use X-Request-Id en sus registros (⟦12⟧), una prueba comprueba que la
    traza que devuelve el gateway es la misma que registró el backend.
  Origen: agente (pregunta interna «¿una afirmación sin prueba?»; ya lo marcó el verificador como PB-02)
  Destino: §7 «después» (correlación en el backend); no reabre el segmento (D-17)
  Estado: movida a §7 (cierre de la revisión, 20-oct, decisión del usuario)

OBS-1-4 · question (menor, operación): restart: unless-stopped también en desarrollo
  Ubicación: despliegue/compose.yaml:7 y :20
  Requisito: A-12
  Detalle: en el nodo de finca es lo que se quiere (vuelve solo tras un corte de luz). En un computador de
    desarrollo, después de un «compose up» la base y el gateway vuelven a arrancar con Docker Desktop en cada
    encendido y ocupan el 5432 y el 80, hasta un «compose down». El script del canal ya hace «down» al terminar.
  Criterio de cumplido: IA_LEEME dice que se apagan con «docker compose -f despliegue/compose.yaml down»
    (recomendado), o el reinicio pasa a un archivo de Compose solo para el nodo de finca.
  Origen: agente (pregunta interna «¿hay un caso sin manejar?»)
  Estado: cumplida 20-oct: IA_LEEME «Cómo se usa» explica el reinicio y cómo apagar con compose down (se mantiene restart, decisión del usuario)

OBS-1-5 · nitpick (nit, pruebas): C-7 no prueba un traceparent mal formado
  Ubicación: CanalPorGatewayTest.java:164-171
  Requisito: A-11
  Detalle: la otra rama del map (traceparent malo → $request_id, no el texto que mandó el cliente) solo se
    comprobó a mano en el contenedor de borrador. Importa porque es la que impide que un cliente meta texto
    arbitrario en el JSON del error y en el registro.
  Criterio de cumplido: un caso manda «traceparent: basura» y el traceId es de 32 hex y distinto de «basura».
  Origen: agente (pregunta interna «¿una afirmación sin prueba?»)
  Estado: cumplida 20-oct: CanalPorGatewayTest.un_traceparent_mal_formado_no_entra_al_error (con un intento de inyección en el JSON)
```

**Abiertas en la verificación (lista de 22 puntos, 20-oct):**

```
OBS-1-6 · suggestion (mayor, lista #14): ayudas de prueba duplicadas
  Ubicación: CanalPorGatewayTest.java:212-235 frente a CanalParticionesTest.java (post, dorada, filas)
  Requisito: ninguno (diseño)
  Detalle: dorada(), filas() y un post() casi igual están copiados de CanalParticionesTest. Son 3 ayudas de 1 a 5
    líneas. Extraerlas obliga a tocar CanalParticionesTest, que no es archivo de este segmento, y las pruebas del
    repo hoy son autocontenidas.
  Criterio de cumplido: o una clase de apoyo de prueba compartida, o el descarte aprobado por el usuario con este
    motivo.
  Propuesta del agente: descartar (convención del repo; fuera de los archivos anunciados del segmento)
  Origen: agente (lista #14)
  Estado: descartada 20-oct, aprobó el usuario · motivo: las pruebas del repo son autocontenidas; extraer toca CanalParticionesTest, fuera del segmento

OBS-1-7 · todo (mayor, lista #21): una explicación de la revisión quedó solo en el chat
  Ubicación: despliegue/gateway/default.conf.template:50-53
  Requisito: A-5
  Detalle: en la revisión se explicó que «if con return» es el único uso de if dentro de location que la
    documentación de Nginx da por seguro; el comentario solo dice por qué no se usa limit_except. Quien lea la
    plantilla sin el chat puede creer que es el «if is evil» de Nginx.
  Criterio de cumplido: el comentario de la línea 50 lo dice.
  Propuesta del agente: aplicar (solo comentario)
  Origen: agente (lista #21)
  Estado: cumplida 20-oct: default.conf.template, comentario del if (if + return es el uso seguro; el resto es el «if is evil»)
```

**Abiertas por el agente de coherencia (verificación, 20-oct; veredicto «pasa», ningún bloqueante).** Formato
corto: cada una es una OBS con etiqueta, severidad, ubicación, criterio y propuesta. «Comprobado» = probado contra
`nginx:1.30.6-alpine` en un contenedor de borrador.

| OBS | = COH | Etiqueta (sev.) | Qué | Propuesta · criterio de cumplido | Estado |
|---|---|---|---|---|---|
| OBS-1-8 | V1 | issue (menor) | `501` (Transfer-Encoding desconocido) y `505` (versión HTTP) salían en HTML: A-9 prometía «todo» | **comprobado**: con `501 505` en el `error_page` de `rechazado` salen en `problem+json`. A-9 dice: lo que pidió mal el cliente (4xx, 501, 505) → `rechazado`; lo que falla del lado del nodo (500, 502, 503, 504) → `no-disponible` | cumplida 20-oct (`error_page … 501 505 /_gateway/rechazado`; A-9) |
| OBS-1-9 | V2 | issue (menor) | Con `client_body_timeout` vencido, Nginx registra `408` y **corta sin responder** (**comprobado**); §3 y §7 decían que el cliente recibe el `408` | quitar `408` del `error_page` (no tiene efecto) y redactar §3, §7 y A-9: «corte de conexión»; encaja con A-54 del contrato (es transporte y se reintenta) | cumplida 20-oct (`408` fuera del `error_page`; A-9, §3, §7) |
| OBS-1-10 | V3 | todo (menor) | Ninguna prueba vería un `proxy_intercept_errors on` futuro: un `500` o `503` del backend se reemplazaría sin que nada falle | §7: cuando exista el `503` del backend (A-40), una prueba de que pasa con su `type` y su `Retry-After` | movida a §7 (aprobó el usuario) |
| OBS-1-11 | V4 | issue (menor) | Compose monta la carpeta `gateway/` entera; la prueba copia un solo archivo | la prueba copia la carpeta entera, igual que Compose | cumplida 20-oct (`gatewayHacia` copia la carpeta `gateway/`) |
| OBS-1-12 | V5 | suggestion (nit) | Con dos direcciones (IPv4 e IPv6) que venzan al conectar: 3 + 3 + 25 = 31 s > 30 s del celular | `proxy_connect_timeout 2s` (2 + 2 + 25 < 30) en la plantilla y en §3 | cumplida 20-oct (`proxy_connect_timeout 2s`; A-10, §3) |
| OBS-1-13 | V6 | nitpick (nit) | La regla nueva del registro («la imagen va fija en `compose.yaml` y la prueba la lee de ahí») no la cumple `postgres:16` | la regla vale desde TEC-15; `postgres:16` sin parche fijo va a §7 | cumplida 20-oct (`IA_TECNOLOGIAS-vigentes.md` §3.2; `postgres:16` en §7) |
| OBS-1-14 | V7 | nitpick (nit) | La ESPEC dice «8/8»; la clase tiene 9 casos (9/9 en verde el 20-oct) | escribir 9/9 | cumplida 20-oct (§8 dice 9/9) |
| OBS-1-15 | V8 | nitpick (nit) | El LEEME dice que el script «apaga» Compose, pero hace `down -v`, que también **borra la base** | el LEEME lo dice | cumplida 20-oct (`IA_LEEME.md`: `down -v` borra la base) |
| OBS-1-16 | V9 | nitpick (nit) | Comentarios que dicen «la única puerta» sin la salvedad del 18080 abierto (§7) | agregar «cuando el backend entre a Compose» en `Sincronizar`, el script y el LEEME | cumplida 20-oct (`Sincronizar`, script, plantilla y LEEME con la salvedad) |
| OBS-1-17 | V10 | issue (nit, seguridad) | `$proxy_add_x_forwarded_for` agrega a lo que mande el cliente: en el borde se puede falsear | `X-Forwarded-For $remote_addr` (el gateway es el primer salto) y A-6 lo dice | cumplida 20-oct (`X-Forwarded-For $remote_addr`; A-6) |
| OBS-1-18 | V11 | issue (nit, seguridad) | `/ingesta/v1/sesiones/..;/particiones` cumplía la regex (Tomcat quita el `;`) | **comprobado**: `"^/ingesta/v1/sesiones/[^/;]+/particiones$"` **entre comillas** (sin comillas Nginx no arranca: el `;` cierra la directiva) da `404` | cumplida 20-oct (regex entre comillas con `[^/;]+`; A-5) |
| OBS-1-19 | V12 | issue (nit, pruebas) | C-5 «en cualquier ruta» solo vale con `Content-Length` (**comprobado**: `chunked` grande a una ruta ajena da `404`); `filas().isZero()` no probaba nada (ceros dan `422` igual) | C-5 dice «con `Content-Length`»; el cuerpo de la prueba es la partición dorada rellenada con espacios hasta pasar 1 MiB, así que si pasara se guardarían 2 filas | cumplida 20-oct (C-5 con `Content-Length`; la prueba manda la partición dorada rellenada) |
| OBS-1-20 | V13 | nitpick (nit) | `gateway` se asigna antes de `start()`: si el arranque falla, los casos siguientes fallan con un mensaje engañoso | asignar después de arrancar | cumplida 20-oct (`gateway` se asigna después de `start()`) |
| OBS-1-21 | V14 | todo (nit, otro canal) | `core.autocrlf=true` sin `.gitattributes`: en Windows el `.sh` se baja con CRLF y Git Bash lo rompe (ya pasaba con el script antes de este segmento) | §7 «otro canal»: `.gitattributes` con `*.sh text eol=lf` (decide Juan, toca todo el repo) | movida a §7, otro canal (aprobó el usuario) |

## 11 · Informe de verificación

### Segmento 1 · verificación del 20-oct-2026

**Paso 1 · observaciones aplicadas** sobre los mismos archivos: OBS-1-2, 1-4, 1-5 (cierre de la revisión) y OBS-1-7,
1-8, 1-9, 1-11 a 1-20 (verificación). A §7: 1-1, 1-3, 1-10, 1-21. Descartada con aprobación: 1-6.

**Paso 2 · lista de 22 puntos** (`investigacion/05` §4.1):

| # | Punto | Resultado |
|---|---|---|
| 1 | Requisito → código → prueba | Sí para el camino mínimo (C-1 a C-7, todas con prueba). A-10 y A-12 por lectura y corrida real (§5), declarado |
| 2 | Todo bloque tiene padre | Sí: cada directiva cita su A-n |
| 3 | Lo de fuera, con destino | Sí: §7 |
| 4 | Nada «por si acaso» | Sí: los códigos del `error_page` salen de la regla general A-9 |
| 5 | Camino feliz de punta a punta | Sí: C-1 (simulador → gateway → backend → PostgreSQL) y corrida real con Compose |
| 6 | Casos borde | Sí: tamaño, ruta larga, método, ruta ajena, traza malformada |
| 7 | Fallos externos definidos | Sí: backend caído `502`; colgado `504` (prueba en §7) |
| 8 | Concurrencia e idempotencia | Sí: el gateway nunca reenvía un `POST` ya entregado (A-10) |
| 9 | Las pruebas fallarían si se rompe | Sí: lo confirmó el agente de coherencia (versión, `if`, tope, `location /`, `type`) |
| 10 | Sin pruebas debilitadas | Sí: solo `disabledWithoutDocker`, como el resto del repo |
| 11 | Build y pruebas en verde | Sí: 55/55 (`./mvnw verify -Dpicocli.ansi=false`; sin esa opción, `SimuladorTest` falla en esta consola de Windows desde antes del segmento, §7) |
| 12 | Arquitectura y módulos | Sí: la prueba vive en `ingesta` del backend; `ModulosTest` en verde |
| 13 | Sin sobreingeniería | Sí |
| 14 | Sin duplicación | No → OBS-1-6, descartada con aprobación |
| 15 | Entrada validada, salida codificada | Sí: la traza solo admite hex; el registro escapa `"` |
| 16 | Autorización | N. A.: la credencial es «después» (§7) |
| 17 | Sin secretos ni datos sensibles en registros | Sí: el registro no lleva `Authorization` ni cuerpo |
| 18 | Dependencias nuevas verificadas | Sí: `nginx:1.30.6-alpine` comprobada en Docker Hub; ninguna dependencia Maven nueva |
| 19 | Nombres del glosario | Sí |
| 20 | Comentarios con el porqué | Sí |
| 21 | Explicaciones de la revisión en el código | No → OBS-1-7, cumplida |
| 22 | El usuario resumió el flujo | pendiente: paso 6 |

**Paso 3 · build:** `./mvnw verify` 55/55 (contrato 24, backend 17 con `CanalPorGatewayTest` 9/9 contra Docker real,
servicios 2, simulador 12). Corrida real con Compose (20-oct): entrega, reenvío, rechazo y `404` por el gateway.

**Paso 4 · coherencia:** agente aparte, veredicto **pasa** (ningún bloqueante); sus 14 hallazgos son OBS-1-8 a 1-21.
Cuatro se comprobaron en un contenedor de borrador (`501`/`505`, `408`, `..;`, `chunked`), y la prueba encontró que su
regex propuesta rompía el arranque sin comillas.

**Paso 5 · completitud:**

| Grupo | Peso | Resueltas |
|---|---|---|
| Cumplidas: 1-2, 1-4, 1-5, 1-7, 1-8, 1-9, 1-11 a 1-20 | 11,5 | 11,5 |
| Descartadas o movidas con motivo: 1-1, 1-3, 1-10, 1-21 (menores y nit) · 1-6 (mayor, aprobada) | 5,5 | 5,5 |

`Completitud = 100 · 17 / 17 = 100 %` · ninguna bloqueante abierta · descartes 32 % del peso (más del 30 %): los
decidió el usuario en dos bloques (cierre de la revisión y verificación).

**Paso 6 · explicación:** pendiente (≥ 80 %).

```
Coherencia: pasa (COH bloqueantes: 0)
Completitud = 100 %   Explicación = pendiente   Build: ok   Pruebas: 55/55
```
