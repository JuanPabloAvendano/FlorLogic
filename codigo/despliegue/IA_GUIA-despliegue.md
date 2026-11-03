REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 3-nov-2026, a pedido de Juan (guía corta: montar desde cero, verificar, tumbar y volver a montar con la misma firma)
Estado de revisión: SIN REVISAR
Manda por encima: `codigo/IA_LEEME.md` · `despliegue/espec/ESPEC-esqueleto-tx1.md` (A-10, A-24, A-25, A-44…A-60) · `despliegue/openbao/init.sh`

# Guía corta del despliegue del nodo de finca

Todas las órdenes van desde `codigo/`. El detalle y el porqué están en `IA_LEEME.md`; aquí solo el camino.

```
C="docker compose -f despliegue/compose.yaml"
```

## 0 · Lo que existe antes de empezar (una vez por máquina)

| Qué | Dónde | Se pierde si… |
|---|---|---|
| **Custodia**: la llave de la raíz (`raiz.p12`) | una carpeta **fuera del repo**, p. ej. `~/florlogic-ca/custodia` (en la finca: máquina sin red) | se borra la carpeta: **sin ella no hay misma firma nunca más** |
| **PKI del nodo** (`FLORLOGIC_PKI`): `raiz.crt`, `openbao.crt`, `openbao.key`, CSR e intermedia | fuera del repo, p. ej. `~/florlogic-ca/pki` | se borra la carpeta (la raíz se puede volver a exportar de la custodia, el resto se rehace) |
| **Clave de la raíz** (`FLORLOGIC_RAIZ_CLAVE`) | en tu cabeza o tu gestor de claves; **nunca** en `.env` ni en la línea de órdenes | se olvida: la custodia queda inservible |
| **Secreto cero** (`despliegue/.env`) | `despliegue/.env`, fuera de git (lista de variables en `IA_LEEME.md`, «El secreto cero») | se borra: la base y el vault ya no abren con otra clave (A-25) |

Pide la clave de la raíz sin que quede en el historial:

```
read -rs FLORLOGIC_RAIZ_CLAVE && export FLORLOGIC_RAIZ_CLAVE
set -a; . despliegue/.env; set +a        # deja FLORLOGIC_PKI y el resto en la terminal
```

## 1 · Montar desde cero

```
java despliegue/pki/Ceremonia.java raiz ~/florlogic-ca/custodia "$FLORLOGIC_PKI"   # paso 1, UNA vez por finca
$C up -d --build --wait            # falla a propósito: openbao-init deja $FLORLOGIC_PKI/intermedia.csr y sale ≠0
java despliegue/pki/Ceremonia.java firmar ~/florlogic-ca/custodia "$FLORLOGIC_PKI" # paso 2: intermedia.crt
$C up -d --build --wait            # ahora sí: emite los certificados y arranca backend, Keycloak y gateway
```

Lo que se espera de cada paso:
- **Paso 1:** dice «raíz creada en …» y deja `raiz.crt`, `openbao.crt` y `openbao.key` en `$FLORLOGIC_PKI`.
- **Primer `up`:** `$C logs openbao-init` termina con «falta la firma de la intermedia».
- **Segundo `up`:** termina sin error.

Una sola vez, para que el simulador pueda entregar (solo desarrollo; en la finca este cliente no existe):

```
export FLORLOGIC_SIMULADOR_SECRETO="$(openssl rand -hex 16)"   # guárdalo junto al .env si lo vas a reusar
$C exec -T -e SECRETO_SIMULADOR="$FLORLOGIC_SIMULADOR_SECRETO" keycloak bash -s < despliegue/keycloak/IA_crear-cliente-simulador.sh
```

## 2 · Verificar que el despliegue está bien

```
G=https://localhost:${FLORLOGIC_GATEWAY_PUERTO:-443}
CURL="curl -s --cacert $FLORLOGIC_PKI/raiz.crt"
```

| # | Orden | Lo esperado | Qué prueba |
|---|---|---|---|
| 1 | `$C ps -a` | `base`, `openbao`, `keycloak`, `backend`, `gateway` en `healthy`; `openbao-init` y `base-init` en `Exited (0)` | todo arrancó y pasó su chequeo de salud |
| 2 | `$C logs openbao-init \| tail -5` | sin «falla»; en un arranque repetido, solo comprobaciones | el vault está iniciado, abierto y la clave de la base coincide (A-25) |
| 3 | `openssl s_client -connect localhost:${FLORLOGIC_GATEWAY_PUERTO:-443} -CAfile $FLORLOGIC_PKI/raiz.crt </dev/null 2>/dev/null \| grep -E 'Verify return code\|issuer='` | `Verify return code: 0 (ok)` y emisor `FlorLogic intermedia de la finca` | la cadena del gateway llega a **tu** raíz (la firma) |
| 4 | `$CURL $G/auth/realms/florlogic/.well-known/openid-configuration -o /dev/null -w '%{http_code}\n'` | `200` | Keycloak responde por el gateway |
| 5 | `$CURL -H 'Content-Type: application/json' --data '{"contrato":"1.0"}' $G/ingesta/v1/sesiones` | `401` con `credencial-invalida` | el backend responde y exige token |
| 6 | `$CURL $G/actuator/health` | `404` `gateway/rechazado` | el gateway es la única puerta (C-2) |
| 7 | `FLORLOGIC_CA_RAIZ=$FLORLOGIC_PKI/raiz.crt java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar sincronizar --url $G --archivo contrato/src/test/resources/dorados/peticion-particion.json` | la primera vez «2 nuevas»; repetida, «2 ya estaban» | el canal de TX-1 de punta a punta (necesita `./mvnw -pl simulador -am package -DskipTests` y el cliente del paso 1) |

**La prueba completa sin tocar tu despliegue:** `scripts/IA_probar-canal.sh`. Monta un nodo aparte (proyecto
`florlogic-canal`, su propia CA y subred), corre todo lo de arriba y más (WAF, HTTP en claro) y al final lo borra.
Si el 443 está ocupado por tu nodo, córrela con `FLORLOGIC_GATEWAY_PUERTO=8443`.

## 3 · Tumbar y volver a montar con la misma firma

«Misma firma» = los certificados siguen saliendo de **la misma raíz**, así que los celulares, el simulador y el
navegador que ya confían en `raiz.crt` siguen confiando sin tocar nada. Hay dos formas de tumbar:

### 3a · Tumbar conservando todo (lo normal)

```
openssl x509 -in "$FLORLOGIC_PKI/raiz.crt" -noout -fingerprint -sha256   # anótala
$C down                          # SIN -v: la base, el vault, la intermedia y los certificados quedan en los volúmenes
$C up -d --wait                  # vuelve igual: no hay ceremonia; openbao-init solo comprueba
```

Vuelve todo: los mismos certificados (solo se reemiten los que vencen en menos de 30 días), los datos, el cliente
simulador y el secreto cero. Verifica con la tabla del paso 2.

### 3b · Tumbar borrando todo, con la misma raíz

```
$C down -v                       # borra la base, el vault (y con él la llave de la intermedia), los certificados
                                 # y las partes de recuperación
$C up -d --build --wait          # falla a propósito: el vault nuevo genera otra intermedia y deja su CSR
java despliegue/pki/Ceremonia.java firmar ~/florlogic-ca/custodia "$FLORLOGIC_PKI"   # la MISMA raíz la firma
$C up -d --build --wait
```

- **No se repite el paso 1** (`raiz`): la custodia ya la tiene. La orden se niega si `raiz.p12` existe, a propósito.
- La intermedia es **nueva**, porque su llave vivía en el vault borrado. La firma la hace la misma raíz.
  `openbao-init` borra solo el CSR y el `intermedia.crt` viejos, que ya no sirven (A-46).
- Se pierden los datos de la base y el cliente simulador de Keycloak: repite el bloque del cliente del paso 1.
- El `.env` puede quedar igual: la clave de la base se vuelve a escribir en la base y en el vault a la vez.

**Comprobar que es la misma firma:** la huella de `raiz.crt` es igual a la anotada, y la fila 3 del paso 2 da
`Verify return code: 0 (ok)` con la **misma** `raiz.crt` de antes.

## 4 · Lo que no se hace

- `down -v` en la finca: borra el vault y las partes de recuperación. Solo en desarrollo y CI.
- Borrar la custodia o volver a crear la raíz: cambia la firma, y todo cliente tendría que confiar en la raíz nueva.
- Cambiar `FLORLOGIC_PG_ADMIN` o `FLORLOGIC_RED` en el `.env` y hacer `up` sobre volúmenes viejos: `openbao-init` para
  con un error y el backend no arranca (A-25). Cambiar la clave es una rotación con `generate-root` (K-22).
- Poner `FLORLOGIC_RAIZ_CLAVE` en `.env` o en un archivo del repo (PR-06).
