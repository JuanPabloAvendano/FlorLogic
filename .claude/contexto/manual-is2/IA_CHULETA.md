REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (orquestador del manual) · 2-nov-2026, iteración 2, a partir de la «Fricción» del registro de pruebas · actualizado el 3-nov-2026 (iteración 3, final)
Estado de revisión: SIN REVISAR
Manda por encima: `IA_00-INDICE.md` y las hojas (cada orden tiene allí su entrada completa) · la palabra de Juan

# Chuleta de una página · lo que se escribe de memoria

Desde `codigo/`. Cada orden enlaza a su entrada completa (Partes, Espera, Si falla, Probado). Todas probadas el
2 y 3-nov-2026 en el laboratorio (iteraciones 1 a 3) salvo donde se dice. Las órdenes con tubería (`|`) van en
bloques de código, nunca dentro de una tabla: copiadas del archivo sin renderizar, el `\|` de la tabla las rompe.

## Dos reglas

1. **Todo `up -d` seguido de una comprobación lleva `--wait`.** Sin él, la comprobación miente (`000`, logs vacíos,
   `Up` en vez de `Exited (1)`). Excepciones: dañar el gateway a propósito, y `openbao-init` (de una sola vez:
   `--wait` sale 0 en cuanto arranca), que va con `DC wait openbao-init` (orden 8).
   → [hoja 01, «Regla del wait»](IA_01-compose-docker.md#regla-del-wait-y-qué-relee-cada-orden)
2. **Antes de algo que borra** (`down -v`, `volume rm`, `rm -sf`): `DC config | grep -m1 '^name'` dice a qué proyecto.

## Preparar la terminal

Una vez por terminal; las dos últimas líneas, **después** de levantar el nodo (paso 3 de la Secuencia: si el gateway
aún no responde, `SES` queda `null`) y cada vez que haga falta una sesión abierta.

```bash
alias DC='docker compose -f despliegue/compose.yaml'
export FLORLOGIC_PKI=~/florlogic-ca/pki            # la misma ruta que FLORLOGIC_PKI en despliegue/.env
G=https://localhost:${FLORLOGIC_GATEWAY_PUERTO:-443}
C="curl -s --cacert $FLORLOGIC_PKI/raiz.crt -H Content-Type:application/json"
T=~/florlogic-demo; mkdir -p $T                     # archivos de trabajo y copias, fuera del repo
DORADA=contrato/src/test/resources/dorados/peticion-particion.json
# abrir una sesión (con el nodo arriba): repetir tras cerrarla o tras borrar la base (el SES viejo da 404)
printf '{"contrato":"1.0","dispositivo_uuid":"0192f0a1-0000-7000-8000-0000000000aa","reloj_ms":%s,"version_config":12}' "$(date +%s%3N)" > $T/abrir.json
SES=$($C -d @$T/abrir.json $G/ingesta/v1/sesiones | jq -r .sesion_uuid); echo $SES
```
Después de la ceremonia (no antes: rompe `./mvnw verify`): `export FLORLOGIC_CA_RAIZ=$FLORLOGIC_PKI/raiz.crt`.
→ [hoja 04, «Preparar la terminal»](IA_04-http-gateway-waf.md#preparar-la-terminal-una-vez-desde-codigo)

## Las órdenes

Ordenadas por: primero lo imprescindible para levantar el nodo y ver el canal, después lo más usado en las tres
iteraciones (veces acumuladas, `IA_REGISTRO-sesiones.md`).

| # | Orden | Para qué · tiempo | Veces | Entrada |
|---|---|---|---|---|
| 1 | `DC up -d --build --wait` | levantar todo (~10 s con imágenes; la 1ª vez para pidiendo la firma, ~17-18 s) | 9 | [01](IA_01-compose-docker.md#levantar-el-nodo-entero-reconstruyendo-y-esperando-a-que-esté-sano) |
| 2 | `java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar sincronizar --archivo $DORADA` | el canal de punta a punta (~2-3 s): 2 nuevas, al repetir 2 ya estaban | 10 | [06](IA_06-simulador.md#sincronizar-una-partición-abre-la-sesión-entrega-y-cierra) |
| 3 | `$C -o /dev/null -w '%{http_code}\n' --data-binary @$DORADA $G/ingesta/v1/sesiones/$SES/particiones` | el canal a mano, solo el código (200, 403, 404, 502, 504); `-i` en vez de `-o … -w …` para ver quién responde (`server: nginx`) | 55 | [04](IA_04-http-gateway-waf.md#mandar-una-partición-a-la-sesión) |
| 4 | `DC exec -T base psql -U florlogic -d florlogic -c '<SQL>'` | ver la base (`\dt`; la sesión es `sesion_id`; `-x` en vertical). Migraciones: [03 · Flyway](IA_03-postgres-datos.md#migraciones-aplicadas-por-flyway) | 41 | [03](IA_03-postgres-datos.md#ver-lo-que-llegó-consultas-de-la-demo) |
| 5 | `DC logs --tail=50 <servicio>` | por qué falla algo (`--tail=1 openbao-init` da la línea que importa) | 26 | [01](IA_01-compose-docker.md#registros-logs) |
| 6 | `DC ps -a` | quién está sano, quién paró (sin `-a` no salen los parados) | 19 | [01](IA_01-compose-docker.md#estado-y-salud-de-cada-servicio) |
| 7 | `DC stop <servicio>` · reparar: `DC up -d --wait <servicio>` | apagar y prender una pieza (6-10 s) | 6 + 18 | [09](IA_09-romper-y-reparar.md#apagar-el-backend-el-gateway-sigue-arriba-y-responde-502) |
| 8 | `DC up -d --force-recreate --no-deps --wait <servicio>` · para `openbao-init`: `DC up -d --force-recreate --no-deps openbao-init; DC wait openbao-init; echo rc=$?` | recrear para que relea un secreto del `.env`; `DC wait` sale con el código de `openbao-init` | 13 + 2 | [01](IA_01-compose-docker.md#recrear-aunque-compose-no-vea-cambios-archivos-montados-secretos) · [01 · wait](IA_01-compose-docker.md#esperar-a-que-termine-un-servicio-de-una-sola-vez-openbao-init) |
| 9 | `DC up -d --build --no-deps --wait backend` | tras cambiar Java | 9 | [09](IA_09-romper-y-reparar.md#cambiar-código-java-y-verlo-en-vivo) |
| 10 | `env -u FLORLOGIC_CA_RAIZ ./mvnw verify` | todo compilado y probado: 194 pruebas, **3,5 a 20,5 min** (correrlo antes de la demo; cómo saltar la clase lenta: hoja 02) | 8 | [02](IA_02-maven-java-pruebas.md#compilar-los-cinco-módulos-y-correr-todas-las-pruebas) |
| 11 | `./mvnw -pl backend-finca -am -Dtest='CapasTest,ModulosTest' -Dsurefire.failIfNoSpecifiedTests=false verify` | Clean Architecture: 9 pruebas, ~7-13 s | 8 | [02](IA_02-maven-java-pruebas.md#las-pruebas-de-arquitectura-capas-y-módulos) |
| 12 | `DC down` · cero: `DC down -v --rmi local` y `rm -r ~/florlogic-ca/custodia ~/florlogic-ca/pki` | apagar conservando datos · borrar todo (~2-3 s) | 5 | [01](IA_01-compose-docker.md#apagar-y-borrar-todo-lo-del-proyecto-vuelta-a-cero) |
| 13 | la cadena TLS: **bloque de abajo** | ver que el gateway presenta hoja + intermedia y verifica contra la raíz | 5 | [05](IA_05-openbao-pki-tls.md#ver-la-cadena-que-presenta-el-gateway) |
| 14 | `DC exec -T openbao bao status` | vault abierto por el HSM (`Seal Type pkcs11`, `Sealed false`) | 4 | [05](IA_05-openbao-pki-tls.md#estado-del-vault-sellado-iniciado) |
| 15 | `DC restart gateway` | aplicar un cambio de la plantilla del gateway (no aplica variables del `.env`) | 3 | [01](IA_01-compose-docker.md#reiniciar-un-servicio-y-releer-la-plantilla-del-gateway) |
| 16 | `FLORLOGIC_GATEWAY_PUERTO=9444 scripts/IA_probar-canal.sh` | prueba completa en un proyecto aparte, sin parar nada: `Canal OK.` en ~40 s | 3 | [06](IA_06-simulador.md#el-canal-de-tx-1-de-punta-a-punta-en-un-proyecto-aparte) |

La cadena TLS (orden 13; sin `2>&1` el `grep` no filtra):

```bash
openssl s_client -connect localhost:${FLORLOGIC_GATEWAY_PUERTO:-443} -CAfile $FLORLOGIC_PKI/raiz.crt -servername localhost -showcerts </dev/null 2>&1 | grep -E 'depth|s:|i:|Verify return'
```

## Tiempos medidos (para no ponerse nervioso)

| Qué | Cuánto |
|---|---|
| De cero al canal, sin `verify` (ceremonia, dos `up`, dos sincronizaciones) | ~35-40 s |
| `./mvnw verify` completo con Docker | 3,5 min en dos corridas, **20,5 min** en otra (un caso de `NodoArranqueSeguroTest`, 1060 s) |
| `504` con la base apagada | ~22 s de silencio |
| `up -d --wait` de un servicio · del nodo entero tras borrar la base | 6-10 s · ~18 s |
| `up` + `DC wait openbao-init` | ~2 s |
| pruebas de arquitectura (`CapasTest,ModulosTest`) | 7-13 s |
| `scripts/IA_probar-canal.sh` con las imágenes hechas · con el puerto ocupado (falla engañosa) | ~40 s · ~30 s |

## Si sale…

| Sale | Es | Entrada |
|---|---|---|
| `000` y curl rc 35 | el gateway aún arranca: faltó `--wait` | [04](IA_04-http-gateway-waf.md#ver-código-de-estado-y-cabeceras) |
| `000` y rc 7 | nadie escucha: gateway apagado o en bucle (`Restarting (1)`) | [09](IA_09-romper-y-reparar.md#romper-el-gateway-con-una-configuración-inválida) |
| `rc=60` | curl no confía: falta `--cacert` (`-sS` dice el motivo) | [04](IA_04-http-gateway-waf.md#sin-la-raíz-curl-no-confía) |
| `404` «Sesión desconocida» | `$SES` vacío, cerrado o de una base borrada: abrir otra | [04](IA_04-http-gateway-waf.md#cerrar-la-sesión) |
| `SES` sale `null` | se abrió antes de que el gateway respondiera: repetir las dos líneas con el nodo arriba | [04](IA_04-http-gateway-waf.md#abrir-una-sesión-de-sincronización-y-guardar-su-id) |
| `column "sesion_uuid" does not exist` | en la tabla es `sesion_id` | [03](IA_03-postgres-datos.md#el-estado-de-una-sesión-concreta) |
| `raiz.p12 ya existe` | no se borró la CA anterior | [05](IA_05-openbao-pki-tls.md#paso-1-de-la-ceremonia-la-raíz-y-el-certificado-de-openbao-una-vez-por-finca) |
| `NoSuchFileException …raiz.crt` en `verify` | `FLORLOGIC_CA_RAIZ` exportada antes de la ceremonia: `env -u` | [02](IA_02-maven-java-pruebas.md#compilar-los-cinco-módulos-y-correr-todas-las-pruebas) |
| `openbao-init-1 Healthy` y logs vacíos tras re-correrlo | `--wait` no espera a un servicio de una sola vez: `DC wait openbao-init` | [01](IA_01-compose-docker.md#esperar-a-que-termine-un-servicio-de-una-sola-vez-openbao-init) |
| `s_client: Extra option: "\|"` | se copió una orden con tubería desde una tabla sin renderizar: usar el bloque de código | [05](IA_05-openbao-pki-tls.md#ver-la-cadena-que-presenta-el-gateway) |
| `El nodo no arrancó.` del script con logs de `openbao-init` sanos | el puerto está ocupado: `FLORLOGIC_GATEWAY_PUERTO=9444` | [06](IA_06-simulador.md#el-canal-de-tx-1-de-punta-a-punta-en-un-proyecto-aparte) |
| `sha256:…` en la columna IMAGE de `DC ps -a` | normal: el almacén containerd de Docker; el contenedor corre la imagen nueva | [01](IA_01-compose-docker.md#estado-y-salud-de-cada-servicio) |
| `openbao-init Started`/`Exited` al tocar gateway o backend | normal: comprueba y no cambia nada | [01](IA_01-compose-docker.md#regla-del-wait-y-qué-relee-cada-orden) |
