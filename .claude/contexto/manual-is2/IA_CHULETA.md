REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (orquestador del manual) · 2-nov-2026, iteración 2, a partir de la «Fricción» del registro de pruebas
Estado de revisión: SIN REVISAR
Manda por encima: `IA_00-INDICE.md` y las hojas (cada orden tiene allí su entrada completa) · la palabra de Juan

# Chuleta de una página · lo que se escribe de memoria

Desde `codigo/`. Cada orden enlaza a su entrada completa (Partes, Espera, Si falla, Probado). Todas probadas el
2-nov-2026 en el laboratorio (iteraciones 1 y 2) salvo donde se dice.

## Dos reglas

1. **Todo `up -d` seguido de una comprobación lleva `--wait`.** Sin él, la comprobación miente (`000`, logs vacíos,
   `Up` en vez de `Exited (1)`). → [hoja 01, «Regla del wait»](IA_01-compose-docker.md#regla-del-wait-y-qué-relee-cada-orden)
2. **Antes de algo que borra** (`down -v`, `volume rm`, `rm -sf`): `DC config | grep -m1 '^name'` dice a qué proyecto.

## Preparar la terminal

Una vez por terminal; las dos últimas líneas, cada vez que haga falta una sesión abierta.

```bash
alias DC='docker compose -f despliegue/compose.yaml'
export FLORLOGIC_PKI=~/florlogic-ca/pki            # la misma ruta que FLORLOGIC_PKI en despliegue/.env
G=https://localhost:${FLORLOGIC_GATEWAY_PUERTO:-443}
C="curl -s --cacert $FLORLOGIC_PKI/raiz.crt -H Content-Type:application/json"
T=~/florlogic-demo; mkdir -p $T                     # archivos de trabajo y copias, fuera del repo
DORADA=contrato/src/test/resources/dorados/peticion-particion.json
# abrir una sesión: repetir tras cerrarla o tras borrar la base (el SES viejo da 404)
printf '{"contrato":"1.0","dispositivo_uuid":"0192f0a1-0000-7000-8000-0000000000aa","reloj_ms":%s,"version_config":12}' "$(date +%s%3N)" > $T/abrir.json
SES=$($C -d @$T/abrir.json $G/ingesta/v1/sesiones | jq -r .sesion_uuid); echo $SES
```
Después de la ceremonia (no antes: rompe `./mvnw verify`): `export FLORLOGIC_CA_RAIZ=$FLORLOGIC_PKI/raiz.crt`.
→ [hoja 04, «Preparar la terminal»](IA_04-http-gateway-waf.md#preparar-la-terminal-una-vez-desde-codigo)

## Las órdenes

| # | Orden | Para qué · tiempo | Entrada |
|---|---|---|---|
| 1 | `DC up -d --build --wait` | levantar todo (~10 s con imágenes; la 1ª vez para pidiendo la firma, ~17 s) | [01](IA_01-compose-docker.md#levantar-el-nodo-entero-reconstruyendo-y-esperando-a-que-esté-sano) |
| 2 | `DC ps -a` | quién está sano, quién paró (sin `-a` no salen los parados) | [01](IA_01-compose-docker.md#estado-y-salud-de-cada-servicio) |
| 3 | `DC logs --tail=50 <servicio>` | por qué falla algo | [01](IA_01-compose-docker.md#registros-logs) |
| 4 | `java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar sincronizar --archivo $DORADA` | el canal de punta a punta (~3 s): 2 nuevas, al repetir 2 ya estaban | [06](IA_06-simulador.md#sincronizar-una-partición-abre-la-sesión-entrega-y-cierra) |
| 5 | `$C -o /dev/null -w '%{http_code}\n' --data-binary @$DORADA $G/ingesta/v1/sesiones/$SES/particiones` | el canal a mano, solo el código (200, 403, 404, 502, 504) | [04](IA_04-http-gateway-waf.md#mandar-una-partición-a-la-sesión) |
| 6 | `DC exec -T base psql -U florlogic -d florlogic -c '<SQL>'` | ver la base (`\dt`; la sesión es `sesion_id`) | [03](IA_03-postgres-datos.md#ver-lo-que-llegó-consultas-de-la-demo) |
| 7 | `DC stop <servicio>` · reparar: `DC up -d --wait <servicio>` | apagar y prender una pieza | [09](IA_09-romper-y-reparar.md#apagar-el-backend-el-gateway-sigue-arriba-y-responde-502) |
| 8 | `DC restart gateway` | aplicar un cambio de la plantilla del gateway (no aplica variables del `.env`) | [01](IA_01-compose-docker.md#reiniciar-un-servicio-y-releer-la-plantilla-del-gateway) |
| 9 | `DC up -d --force-recreate --no-deps --wait <servicio>` | recrear para que relea un secreto del `.env` (y re-correr `openbao-init`) | [01](IA_01-compose-docker.md#recrear-aunque-compose-no-vea-cambios-archivos-montados-secretos) |
| 10 | `DC up -d --build --no-deps --wait backend` | tras cambiar Java | [09](IA_09-romper-y-reparar.md#cambiar-código-java-y-verlo-en-vivo) |
| 11 | `env -u FLORLOGIC_CA_RAIZ ./mvnw verify` | todo compilado y probado: 194 pruebas, ~3,5 min | [02](IA_02-maven-java-pruebas.md#compilar-los-cinco-módulos-y-correr-todas-las-pruebas) |
| 12 | `./mvnw -pl backend-finca -am -Dtest='CapasTest,ModulosTest' -Dsurefire.failIfNoSpecifiedTests=false verify` | Clean Architecture: 9 pruebas, ~7 s | [02](IA_02-maven-java-pruebas.md#las-pruebas-de-arquitectura-capas-y-módulos) |
| 13 | `DC exec -T openbao bao status` | vault abierto por el HSM (`Sealed false`) | [05](IA_05-openbao-pki-tls.md#estado-del-vault-sellado-iniciado) |
| 14 | `openssl s_client -connect localhost:443 -CAfile $FLORLOGIC_PKI/raiz.crt -servername localhost -showcerts </dev/null 2>&1 \| grep -E 'depth\|s:\|i:\|Verify return'` | la cadena TLS (sin `2>&1` el `grep` no filtra) | [05](IA_05-openbao-pki-tls.md#ver-la-cadena-que-presenta-el-gateway) |
| 15 | `FLORLOGIC_GATEWAY_PUERTO=9444 scripts/IA_probar-canal.sh` | prueba completa en un proyecto aparte, sin parar nada: `Canal OK.` en ~40 s | [06](IA_06-simulador.md#el-canal-de-tx-1-de-punta-a-punta-en-un-proyecto-aparte) |
| 16 | `DC down` · cero: `DC down -v --rmi local` y `rm -r ~/florlogic-ca/custodia ~/florlogic-ca/pki` | apagar conservando datos · borrar todo (~2 s) | [01](IA_01-compose-docker.md#apagar-y-borrar-todo-lo-del-proyecto-vuelta-a-cero) |

## Tiempos medidos (para no ponerse nervioso)

| Qué | Cuánto |
|---|---|
| De cero al canal, sin `verify` (ceremonia, dos `up`, dos sincronizaciones) | ~40 s |
| `./mvnw verify` completo con Docker | ~3,5 min |
| `504` con la base apagada | ~22 s de silencio |
| `up -d --wait` de un servicio · del nodo entero tras borrar la base | 6-10 s · ~18 s |
| `scripts/IA_probar-canal.sh` con las imágenes hechas · con el puerto ocupado (falla engañosa) | ~40 s · ~30 s |

## Si sale…

| Sale | Es | Entrada |
|---|---|---|
| `000` y curl rc 35 | el gateway aún arranca: faltó `--wait` | [04](IA_04-http-gateway-waf.md#ver-código-de-estado-y-cabeceras) |
| `000` y rc 7 | nadie escucha: gateway apagado o en bucle (`Restarting (1)`) | [09](IA_09-romper-y-reparar.md#romper-el-gateway-con-una-configuración-inválida) |
| `404` «Sesión desconocida» | `$SES` vacío, cerrado o de una base borrada: abrir otra | [04](IA_04-http-gateway-waf.md#cerrar-la-sesión) |
| `column "sesion_uuid" does not exist` | en la tabla es `sesion_id` | [03](IA_03-postgres-datos.md#el-estado-de-una-sesión-concreta) |
| `raiz.p12 ya existe` | no se borró la CA anterior | [05](IA_05-openbao-pki-tls.md#paso-1-de-la-ceremonia-la-raíz-y-el-certificado-de-openbao-una-vez-por-finca) |
| `NoSuchFileException …raiz.crt` en `verify` | `FLORLOGIC_CA_RAIZ` exportada antes de la ceremonia: `env -u` | [02](IA_02-maven-java-pruebas.md#compilar-los-cinco-módulos-y-correr-todas-las-pruebas) |
| `El nodo no arrancó.` del script con logs de `openbao-init` sanos | el puerto está ocupado: `FLORLOGIC_GATEWAY_PUERTO=9444` | [06](IA_06-simulador.md#el-canal-de-tx-1-de-punta-a-punta-en-un-proyecto-aparte) |
| `sha256:…` en la columna IMAGE de `DC ps -a` | normal tras el segundo `up --build` | [01](IA_01-compose-docker.md#estado-y-salud-de-cada-servicio) |
| `openbao-init Started`/`Exited` al tocar gateway o backend | normal: comprueba y no cambia nada | [01](IA_01-compose-docker.md#regla-del-wait-y-qué-relee-cada-orden) |
