REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 por el orquestador (iteración 2)
Estado de revisión: SIN REVISAR
Manda por encima: `codigo/IA_LEEME.md` · `codigo/despliegue/compose.yaml` · la palabra de Juan

# 05 · El secreto cero, la CA de la finca, OpenBao y TLS

`<custodia>` (la llave de la raíz; en la finca, una máquina sin red) y `<pki>` (lo que lee el nodo, `FLORLOGIC_PKI`)
van **fuera del repo**, p. ej. `~/florlogic-ca/custodia` y `~/florlogic-ca/pki`. La clave de la raíz va en la variable
`FLORLOGIC_RAIZ_CLAVE`, nunca escrita en la orden.

## Antes del primer `up`

### El secreto cero: `despliegue/.env`
```
FLORLOGIC_PG_ADMIN=<openssl rand -hex 16>      # clave de administrador de PostgreSQL; openbao-init la copia al vault
FLORLOGIC_HSM_PIN=<openssl rand -hex 16>       # PIN del token PKCS#11 que abre (desella) el vault
FLORLOGIC_SOFTHSM_SO_PIN=<openssl rand -hex 16> # solo fuera de la finca: crea el token SoftHSM
FLORLOGIC_SOFTHSM_CREAR=si                     # solo fuera de la finca
BAO_HSM_LIB=/usr/lib/softhsm/libsofthsm2.so
FLORLOGIC_PKI=/home/<usuario>/florlogic-ca/pki # ruta absoluta de <pki>
```
- Partes: es «lo que existe antes de `up`»: Compose lo lee solo de `despliegue/.env`, lo monta como archivo en `/run/secrets/` del contenedor que lo pide y no lo pone en su entorno · `openssl rand -hex 16` inventa 32 caracteres al azar (se corre aparte y se pega el resultado) · el `.env` está en `.gitignore`: **nunca** entra a git
- Espera: nada visible; `DC config --services` lista los 5 servicios sin quejarse de `FLORLOGIC_PKI`
- Si falla: `falta FLORLOGIC_PKI` / `falta BAO_HSM_LIB` = la línea no está; una variable de secreto faltante solo se nota al **crear** el contenedor (hoja 09)
- Riesgo: el archivo tiene claves: no proyectarlo · Necesidad: ESENCIAL (sin él no arranca nada)
- Probado: sin probar escribirlo (en la iteración 1 venía hecho); funcionó usándolo (fila 21) y quitando una línea (filas 125-144, hoja 09)

### Preparar la terminal para la ceremonia
`export FLORLOGIC_PKI=~/florlogic-ca/pki FLORLOGIC_RAIZ_CLAVE=$(openssl rand -hex 16)`
- Partes: `FLORLOGIC_PKI` la misma ruta del `.env` (la usan `curl`, `openssl` y esta hoja) · `FLORLOGIC_RAIZ_CLAVE` la clave que protege `raiz.p12`: **anotarla en lugar seguro** (con otra clave ya no se puede firmar) · **no** exportar todavía `FLORLOGIC_CA_RAIZ`: si apunta a un archivo que aún no existe, `./mvnw verify` falla (hoja 02)
- Riesgo: la clave queda en la terminal mientras esté abierta · Necesidad: ESENCIAL
- Probado: sin probar escrito así (el laboratorio lo exportaba en `entorno.sh`)

### Paso 1 de la ceremonia: la raíz y el certificado de OpenBao (una vez por finca)
`java despliegue/pki/Ceremonia.java raiz ~/florlogic-ca/custodia $FLORLOGIC_PKI`
- Partes: `java Archivo.java` corre un programa de un solo archivo sin compilarlo aparte · `raiz` crea la CA raíz (autofirmada, 10 años) y el certificado del servidor de OpenBao firmado por ella · primer argumento la custodia, segundo el `<pki>`
- Espera: `raíz creada en …/custodia/raiz.p12; raiz.crt, openbao.crt y openbao.key en …/pki`; `ls -l` muestra `raiz.p12` y `openbao.key` con permisos `0600` (solo el dueño)
- Si falla: `… raiz.p12 ya existe: la raíz se crea una sola vez por finca` (rc=1) = ya se hizo; para empezar de cero, borrar las dos carpetas (paso 6 de la Secuencia del índice)
- Tiempo: ~2 s
- Riesgo: ninguno · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 16, 17) · 2-nov-2026 · iteración 2 · funcionó en 2,1 s; con la CA vieja sin borrar dio `raiz.p12 ya existe` (filas 1, 7)

### Paso 2: firmar la intermedia que pidió OpenBao
`java despliegue/pki/Ceremonia.java firmar ~/florlogic-ca/custodia $FLORLOGIC_PKI` y otra vez `DC up -d --build --wait`
- Partes: el primer `up` sale ≠0 a propósito y deja `<pki>/intermedia.csr` (la petición de OpenBao) · `firmar` la firma con la raíz y deja `intermedia.crt` · el segundo `up` la carga y emite los certificados del gateway y el backend
- Espera: `intermedia firmada: …/pki/intermedia.crt`; después, todo `Healthy`
- Si falla: `DC logs openbao-init` dice qué falta, p. ej. `openbao-init: falta la firma de la intermedia: firma intermedia.csr de FLORLOGIC_PKI con la raíz (java despliegue/pki/Ceremonia.java firmar …) y vuelve a hacer «docker compose up»`
- Riesgo: ninguno · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 18-21) · 2-nov-2026 · iteración 2 · funcionó: el log pidió la firma, `firmar` 0,8 s, luego `Healthy` (filas 8-11)

## Mirar el vault

### Qué hizo el inicio del vault
`DC logs openbao-init`
- Espera: 1ª vez `certificados emitidos: gateway backend` · `configuración terminada; raíz revocado`; las siguientes, `todo coincide (subred, AppRole, clave de KV, base y anfitrión); nada que cambiar`
- Riesgo: ninguno · Necesidad: ESENCIAL (gestión de secretos, área 1.3)
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 19, 25) · 2-nov-2026 · iteración 2 · funcionó (filas 76, 84)

### Estado del vault (sellado, iniciado)
`DC exec -T openbao bao status`
- Partes: la imagen trae `BAO_ADDR` y `BAO_CACERT`, no hace falta pasarlos · sale 2 si está sellado o sin iniciar
- Espera: `Initialized true`, `Sealed false`, `Version 2.7.2`, `Storage Type raft`, `HA Mode active`. Tras recrear `openbao` se abre solo con el token del HSM
- Riesgo: ninguno · Necesidad: ESENCIAL
- Probado: 2-nov-2026 · iteración 1 · funcionó (filas 85, 147) · 2-nov-2026 · iteración 2 · funcionó (filas 65, 122)

### Dónde vive el secreto cero dentro del contenedor
`DC exec -T openbao sh -c 'ls -l /run/secrets/'`
- Partes: `/run/secrets/` es donde Compose monta los `secrets:`; no aparecen en `env`
- Espera: `hsm-pin`, `softhsm-so-pin`, `ca-raiz`, `bao-tls-crt`, `bao-tls-key`. **La clave de la base (`pg-admin`) no está aquí** (entrada siguiente)
- Riesgo: no hacer `cat` de esos archivos en la demo · Necesidad: ÚTIL (pregunta «¿dónde está la clave?»: área 1.5 y factor III de Twelve-Factor, la configuración en el entorno)
- Probado: 2-nov-2026 · iteración 1 · funcionó: `hsm-pin` montado, 32 bytes (fila 132) · 2-nov-2026 · iteración 2 · funcionó; `pg-admin` no aparece (fila 111)

### Dónde está la clave de la base, sin imprimirla
`DC exec -T base sh -c 'ls -l /run/secrets/; wc -c < /run/secrets/pg-admin'` · que no está en el entorno: `DC exec -T base sh -c 'env | grep -c PG_ADMIN'`
- Partes: en `despliegue/compose.yaml` el secreto `pg-admin` sale de la variable `FLORLOGIC_PG_ADMIN` del `.env` (`secrets: pg-admin: environment: FLORLOGIC_PG_ADMIN`) y se monta **solo** en `base` (donde `POSTGRES_PASSWORD_FILE: /run/secrets/pg-admin` lo lee al crear la base) y en `openbao-init` (que lo copia al vault). El backend no lo ve: pide su clave al vault · `wc -c` cuenta bytes sin mostrar el contenido · `grep -c` cuenta líneas
- Espera: `pg-admin` de 32 bytes; `0` en el entorno (el `grep -c` sale rc=1 cuando cuenta 0: es normal). `openbao-init` ya terminó: no se puede entrar a mirarlo, basta leer `compose.yaml`
- Riesgo: no hacer `cat` · Necesidad: ÚTIL (pregunta «¿dónde está la clave de la base?»)
- Probado: 2-nov-2026 · iteración 2 · funcionó: 32 B y 0 (fila 112); el montaje en `openbao-init`, leído en `compose.yaml` (fila 111)

## Certificados y TLS

### Leer un certificado
`openssl x509 -in $FLORLOGIC_PKI/raiz.crt -noout -subject -issuer -dates -ext subjectAltName` (también `openbao.crt`, `intermedia.crt`)
- Partes: `x509` lee un certificado · `-noout` no repite el certificado en base64 · `-subject` a quién se emitió · `-issuer` quién lo firmó · `-dates` vigencia · `-ext subjectAltName` los nombres para los que vale
- Espera: raíz `CN=FlorLogic raiz de la finca`, autofirmada, hasta 2036, «No extensions» en SAN · intermedia firmada por la raíz, hasta 2031 · `openbao.crt` `CN=openbao`, firmado por la raíz, hasta 2028, SAN `openbao, localhost, 127.0.0.1`
- Riesgo: ninguno · Necesidad: ÚTIL
- Probado: 2-nov-2026 · iteración 1 · funcionó con los tres (filas 80-82)

### Ver la cadena que presenta el gateway
`openssl s_client -connect localhost:443 -CAfile $FLORLOGIC_PKI/raiz.crt -servername localhost -showcerts </dev/null 2>&1 | grep -E 'depth|s:|i:|Verify return'`
- Partes: `s_client` hace el apretón TLS como un cliente · `-CAfile` confía en la raíz · `-servername` el nombre pedido (SNI) · `-showcerts` imprime todos los certificados que manda el servidor · `</dev/null` cierra la conexión al terminar · `2>&1` junta la salida de errores con la normal: `depth=…`, `verify return:1`, `DONE` y `Connecting to …` salen por la de errores y **sin** `2>&1` pasan sin filtrar · el `grep` deja solo la cadena y el veredicto
- Espera: depth 2 raíz → depth 1 intermedia → depth 0 `CN=localhost`; el gateway manda hoja + intermedia; `Verify return code: 0 (ok)`
- Riesgo: ninguno · Necesidad: ESENCIAL (TLS de punta a punta, área 1.5)
- Probado: 2-nov-2026 · iteración 1 · funcionó en el puerto del laboratorio (filas 83, 84) · 2-nov-2026 · iteración 2 · sin `2>&1` el `grep` no filtró (fila 114); con `2>&1` funcionó (fila 116)

### Comprobar que el backend exige certificado de cliente (mTLS)
`DC exec -T gateway sh -c 'curl -sk https://backend:8443/ -o /dev/null -w "%{http_code}\n"; echo rc=$?'`
- Partes: desde **dentro** del gateway (el backend no tiene puertos publicados) · `-k` acepta cualquier certificado del servidor, para que lo único que falte sea el del cliente · sin `--cert`, curl no presenta certificado
- Espera: `000` y `rc=56`: el backend corta el apretón porque no hay certificado de cliente; solo entra el gateway, que sí lo tiene
- Riesgo: ninguno · Necesidad: ÚTIL (Zero Trust: el backend no confía en la red, área 1.5)
- Probado: 2-nov-2026 · iteración 1 · funcionó (fila 86) · 2-nov-2026 · iteración 2 · funcionó (fila 124)
