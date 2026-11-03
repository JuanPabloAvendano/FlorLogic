REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan («lista de notas y diagramas de fácil acceso para la demostración de la guía IS II, sin IA») · actualizado el 2-nov-2026 por el orquestador (iteración 2)
Estado de revisión: SIN REVISAR
Manda por encima: `.claude/contexto/IA_GUIA-IS2-checklist.md` · `codigo/IA_LEEME.md` · la palabra de Juan

# Manual de la demostración IS II · índice

Para usar **sin agente** durante la demostración: cada orden lleva qué hace cada parte, qué se espera ver, qué hacer
si falla y si es necesaria. Todas se corren **desde `codigo/`** salvo que se diga otra cosa. `DC` abrevia
`docker compose -f despliegue/compose.yaml` (`alias DC='docker compose -f despliegue/compose.yaml'`, hoja 01).

**¿Con prisa? → [`IA_CHULETA.md`](IA_CHULETA.md)**: una página con el bloque «Preparar la terminal», las 16 órdenes
que se escriben de memoria, los tiempos medidos y «Si sale…».

## Dos reglas para todo el manual

1. **Todo `up -d` seguido de una comprobación lleva `--wait`.** Sin él, `up` devuelve la terminal antes de que el
   servicio escuche y la comprobación siguiente miente: `000` con curl rc 35, logs vacíos, `Up` en vez de `Exited (1)`
   (iteración 2, filas 39, 67-69). La única excepción es dañar el gateway a propósito (hoja 09). Detalle y tabla
   «qué relee cada orden»: [hoja 01](IA_01-compose-docker.md#regla-del-wait-y-qué-relee-cada-orden).
2. **Antes de algo que borra** (`down -v`, `docker volume rm`, `DC rm -sf`): `DC config | grep -m1 '^name'` dice a qué
   proyecto se le va a hacer.

## Acceso rápido

Ordenadas por: primero lo imprescindible para que el nodo funcione y se vea el canal, después lo que más se usó en las
pruebas (veces acumuladas en las iteraciones 1 y 2, `IA_REGISTRO-sesiones.md`). Todas probadas salvo donde se dice.

| # | Orden | Para qué | Veces | Entrada |
|---|---|---|---|---|
| 1 | el bloque «Preparar la terminal» de la chuleta (`alias DC`, `FLORLOGIC_PKI`, `G`, `C`, `T`, `DORADA`, abrir `SES` con `jq`) | todo lo de abajo lo usa | 2 | [chuleta](IA_CHULETA.md#preparar-la-terminal) |
| 2 | `java despliegue/pki/Ceremonia.java raiz ~/florlogic-ca/custodia $FLORLOGIC_PKI` | crear la CA de la finca (una vez; antes, `despliegue/.env` y `FLORLOGIC_RAIZ_CLAVE`) | 3 | [05](IA_05-openbao-pki-tls.md#paso-1-de-la-ceremonia-la-raíz-y-el-certificado-de-openbao-una-vez-por-finca) |
| 3 | `DC up -d --build --wait` | levantar el nodo entero (la 1ª vez para pidiendo la firma) | 7 | [01](IA_01-compose-docker.md#levantar-el-nodo-entero-reconstruyendo-y-esperando-a-que-esté-sano) |
| 4 | `java despliegue/pki/Ceremonia.java firmar ~/florlogic-ca/custodia $FLORLOGIC_PKI` | firmar la intermedia y repetir la 3 | 2 | [05](IA_05-openbao-pki-tls.md#paso-2-firmar-la-intermedia-que-pidió-openbao) |
| 5 | `java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar sincronizar --archivo $DORADA` | el canal de TX-1 (2 nuevas; al repetir, 2 ya estaban). Antes: `export FLORLOGIC_CA_RAIZ=$FLORLOGIC_PKI/raiz.crt` | 7 | [06](IA_06-simulador.md#sincronizar-una-partición-abre-la-sesión-entrega-y-cierra) |
| 6 | `$C --data-binary @$DORADA $G/ingesta/v1/sesiones/$SES/particiones` | el canal a mano; `-o /dev/null -w '%{http_code}\n'` para ver solo el código | 48 | [04](IA_04-http-gateway-waf.md#mandar-una-partición-a-la-sesión) |
| 7 | `DC exec -T base psql -U florlogic -d florlogic -c '<SQL>'` | ver lo que llegó (`\dt`, `SELECT …`; la sesión es `sesion_id`) | 32 | [03](IA_03-postgres-datos.md#ver-lo-que-llegó-consultas-de-la-demo) |
| 8 | `DC logs --tail=50 <servicio>` | por qué algo falla (`-f` en vivo) | 22 | [01](IA_01-compose-docker.md#registros-logs) |
| 9 | `cp <archivo> $T/` · reparar `cp $T/<nombre> <archivo>` · `cmp … && echo igual` | copia antes de dañar y restauración (hoja 09) | 16 | [09](IA_09-romper-y-reparar.md) |
| 10 | `DC ps -a` | qué está sano, qué paró (sin `-a` no salen los parados) | 15 | [01](IA_01-compose-docker.md#estado-y-salud-de-cada-servicio) |
| 11 | `DC up -d --wait <servicio>` | volver a montar o esperar sano tras `start`/reparar | 15 | [01](IA_01-compose-docker.md#esperar-a-que-un-servicio-esté-sano) |
| 12 | `DC stop <servicio>` · `DC start <servicio>` | apagar y prender una pieza en vivo | 9 | [09](IA_09-romper-y-reparar.md#apagar-el-backend-el-gateway-sigue-arriba-y-responde-502) |
| 13 | `DC up -d --force-recreate --no-deps --wait <servicio>` | recrear para que relea un secreto del `.env`; re-correr `openbao-init` | 8 | [01](IA_01-compose-docker.md#recrear-aunque-compose-no-vea-cambios-archivos-montados-secretos) |
| 14 | `env -u FLORLOGIC_CA_RAIZ ./mvnw verify` | compilar y probar todo (194 pruebas, ~3,5 min con Docker) | 7 | [02](IA_02-maven-java-pruebas.md#compilar-los-cinco-módulos-y-correr-todas-las-pruebas) |
| 15 | `DC up -d --build --no-deps --wait backend` | reconstruir solo el backend tras cambiar Java | 7 | [09](IA_09-romper-y-reparar.md#cambiar-código-java-y-verlo-en-vivo) |
| 16 | `./mvnw -pl backend-finca -am -Dtest='CapasTest,ModulosTest' -Dsurefire.failIfNoSpecifiedTests=false verify` | las pruebas de arquitectura (Clean Architecture), ~7 s | 6 | [02](IA_02-maven-java-pruebas.md#las-pruebas-de-arquitectura-capas-y-módulos) |
| 17 | `DC down` · cero: `DC down -v --rmi local` | apagar conservando datos · borrar base, vault e imágenes del proyecto | 4 | [01](IA_01-compose-docker.md#apagar-y-borrar-todo-lo-del-proyecto-vuelta-a-cero) |
| 18 | `FLORLOGIC_GATEWAY_PUERTO=9444 scripts/IA_probar-canal.sh` | prueba completa del canal en un proyecto aparte, sin parar nada (`Canal OK.`, ~40 s) | 3 | [06](IA_06-simulador.md#el-canal-de-tx-1-de-punta-a-punta-en-un-proyecto-aparte) |
| 19 | `DC restart gateway` | aplicar un cambio de la plantilla del gateway (sí la relee; no aplica variables del `.env`) | 2 | [01](IA_01-compose-docker.md#reiniciar-un-servicio-y-releer-la-plantilla-del-gateway) |

## Secuencia de la demostración: de cero al canal y de vuelta a cero

Requisitos: Docker encendido, `despliegue/.env` escrito (hoja 05), una terminal en `codigo/` y **ninguna CA anterior**
en `~/florlogic-ca` (si quedó de otra corrida, hacer antes el paso 6: si no, el paso 3 para con `raiz.p12 ya existe`).
Tiempo: ~40 s sin el paso 1; ~4 min 10 s con él.

```bash
# 0 · preparar la terminal: el bloque de IA_CHULETA.md y la clave de la raíz
alias DC='docker compose -f despliegue/compose.yaml'
export FLORLOGIC_PKI=~/florlogic-ca/pki FLORLOGIC_RAIZ_CLAVE=<la clave de la raíz>
DORADA=contrato/src/test/resources/dorados/peticion-particion.json
# 1 · construir y probar (opcional si hay poco tiempo: ~3,5 min)
env -u FLORLOGIC_CA_RAIZ ./mvnw verify
# 2 · el celular simulado (~2 s)
./mvnw -q -DskipTests -pl simulador -am package
# 3 · ceremonia paso 1, primer arranque (para a propósito), paso 2, arranque completo
java despliegue/pki/Ceremonia.java raiz ~/florlogic-ca/custodia $FLORLOGIC_PKI
DC up -d --build --wait            # sale ≠0 en ~17 s: openbao-init pide la firma de la intermedia
DC logs --tail=10 openbao-init     # confirma que es esa: «falta la firma de la intermedia: …»
java despliegue/pki/Ceremonia.java firmar ~/florlogic-ca/custodia $FLORLOGIC_PKI
DC up -d --build --wait            # todo Healthy en ~10 s
DC ps -a                           # 4 healthy y openbao-init Exited (0); IMAGE «sha256:…» es normal
# 4 · el canal: dos veces la misma partición (2 nuevas · 2 ya estaban)
export FLORLOGIC_CA_RAIZ=$FLORLOGIC_PKI/raiz.crt
java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar sincronizar --archivo $DORADA
java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar sincronizar --archivo $DORADA
# 5 · lo que quedó en la base
DC exec -T base psql -U florlogic -d florlogic -c 'SELECT estado, count(*) FROM sesion_sincronizacion GROUP BY estado;'
# 6 · vuelta a cero: contenedores, red, volúmenes e imágenes del proyecto; y la CA (si no, «raiz.p12 ya existe»)
DC config | grep -m1 '^name'       # que sea tu proyecto
DC down -v --rmi local
rm -r ~/florlogic-ca/custodia ~/florlogic-ca/pki
```
- Probado: 2-nov-2026 · iteración 1 · funcionaron los pasos 1 a 6 por separado (filas 9, 16-22, 39, 42-43, 56, 155-157) · 2-nov-2026 · iteración 2 · funcionó **de un tirón, solo con el índice**, con las rutas del laboratorio: sin borrar la CA, `raiz.p12 ya existe` (fila 1); `verify` 3 min 27 s con la trampa de `FLORLOGIC_CA_RAIZ` armada; primer `up` rc=1 en 17 s y el log pidió la firma; segundo `up` 9,7 s; 2 nuevas · 2 ya estaban; `CERRADA 2`; vuelta a cero en 2,3 s y recuento 0 (filas 1-15, 134-137). El `grep -m1 '^name'` del paso 6, filas 2 y 134

## Si el evaluador pide… (dónde se ve cada área de la guía en vivo)

Solo lo que se demuestra con una orden; lo que la guía marca `DEFENDER` (broker, service mesh, data mesh) se sustenta
con su ADR, sin orden. Cada enlace lleva a la entrada exacta.

| Área (guía §1) | Qué mostrar | Entrada | Probado |
|---|---|---|---|
| 1.1 Gobierno y ADR | La tabla de ADR-001…037 del README | `git show origin/documentacion-desarrollo:README.md \| less` → [07](IA_07-git-lectura.md) | sin probar (las pruebas no usan git) |
| 1.2 Arquitectura y modelado | Quién publica puertos (solo gateway y base) y la red | [01 · backend sin puertos](IA_01-compose-docker.md#mostrar-que-el-backend-no-publica-puertos) · [01 · Redes](IA_01-compose-docker.md#redes) · diagramas [08](IA_08-diagramas.md) | iteración 2 (filas 108, 110, 120) |
| 1.3 Línea base: API gateway | Única puerta: ruta ajena 404, en claro 400 | [04 · ruta ajena](IA_04-http-gateway-waf.md#ruta-ajena-el-gateway-responde-404-sin-molestar-al-backend) · [04 · en claro](IA_04-http-gateway-waf.md#en-claro-no-hay-nada) | iteración 2 (fila 121) |
| 1.3 Línea base: WAF | Inyección SQL → 403 y la regla 942100; «apaga el WAF» | [04 · disparar](IA_04-http-gateway-waf.md#disparar-el-waf-con-una-inyección-sql-de-ejemplo) · [04 · regla](IA_04-http-gateway-waf.md#ver-qué-regla-bloqueó) · [04 · DetectionOnly](IA_04-http-gateway-waf.md#pasar-el-waf-a-solo-anotar-comprobarlo-y-volver) | iteración 2 (filas 34-42) |
| 1.3 Línea base: secretos | Vault abierto por el HSM; secreto cero en `/run/secrets`; «¿dónde está la clave de la base?» | [05 · estado](IA_05-openbao-pki-tls.md#estado-del-vault-sellado-iniciado) · [05 · /run/secrets](IA_05-openbao-pki-tls.md#dónde-vive-el-secreto-cero-dentro-del-contenedor) · [05 · clave de la base](IA_05-openbao-pki-tls.md#dónde-está-la-clave-de-la-base-sin-imprimirla) | iteración 2 (filas 111, 112, 122) |
| 1.4 Calidad: Clean Architecture | Romper una capa y ver la prueba en rojo | [02 · arquitectura](IA_02-maven-java-pruebas.md#las-pruebas-de-arquitectura-capas-y-módulos) · [09 · romper una capa](IA_09-romper-y-reparar.md#romper-una-regla-de-capas-y-ver-a-archunit-atraparla) | iteración 2 (filas 125-129) |
| 1.5 Seguridad | Cadena TLS verificada; mTLS al backend; sin raíz rc 60 | [05 · cadena](IA_05-openbao-pki-tls.md#ver-la-cadena-que-presenta-el-gateway) (con `2>&1`) · [05 · mTLS](IA_05-openbao-pki-tls.md#comprobar-que-el-backend-exige-certificado-de-cliente-mtls) · [04 · sin raíz](IA_04-http-gateway-waf.md#sin-la-raíz-curl-no-confía) | iteración 2 (filas 116, 123, 124) |
| 1.6 DevSecOps: pruebas | 194 pruebas con Testcontainers. Cobertura, SonarQube y Trivy: FALTA en la guía | [02 · verify](IA_02-maven-java-pruebas.md#compilar-los-cinco-módulos-y-correr-todas-las-pruebas) | iteración 2 (fila 5) |
| 1.7 Cloud native | Reinicio sin perder datos; healthchecks; respaldo y restauración; reenvío idempotente; consumo | [01 · apagar sin perder datos](IA_01-compose-docker.md#apagar-sin-perder-datos) · [09 · base apagada](IA_09-romper-y-reparar.md#apagar-la-base-con-el-backend-arriba) · [03 · respaldo](IA_03-postgres-datos.md#respaldo-y-restauración-manual) · [09 · borrar una fila](IA_09-romper-y-reparar.md#borrar-una-fila-y-recuperarla-reenviando-la-misma-partición) · [01 · consumo](IA_01-compose-docker.md#uso-de-cpu-y-memoria) | iteración 2 (filas 88-102, 113, 131) |
| 1.8 IA responsable | Cabecera de cuatro líneas en todo `IA_*`; commits humanos | `head -4 <archivo IA_….md>`, en scripts `head -5` (la línea 1 es el `#!`) · `git log --format=…` → [07](IA_07-git-lectura.md) | `head -4` en un `.sh` cortó la cabecera (fila 132); git sin probar |
| 1.9 Sustentación | Apagar, romper y reparar cada pieza (matriz §6) | [09](IA_09-romper-y-reparar.md) | iteración 2 (filas 43-102, 117-119) |

## Hojas

| Hoja | Tema |
|---|---|
| [`IA_CHULETA.md`](IA_CHULETA.md) | Una página: preparar la terminal, 16 órdenes, tiempos, «Si sale…» |
| [`IA_01-compose-docker.md`](IA_01-compose-docker.md) | La regla del `--wait` y qué relee cada orden; levantar, apagar, recrear, reiniciar, reconstruir un servicio, logs, puertos publicados, entrar a un contenedor, imágenes, volúmenes, redes |
| [`IA_02-maven-java-pruebas.md`](IA_02-maven-java-pruebas.md) | Construir, una sola prueba, pruebas de arquitectura, un solo módulo, árbol de dependencias |
| [`IA_03-postgres-datos.md`](IA_03-postgres-datos.md) | psql, ver tablas, el estado de una sesión, consultas de la demo, restricción CHECK, borrar, respaldo y restauración, Flyway, entidad-relación |
| [`IA_04-http-gateway-waf.md`](IA_04-http-gateway-waf.md) | curl por HTTPS: abrir sesión, partición y cierre a mano con sus JSON, errores y códigos de curl, WAF On/DetectionOnly |
| [`IA_05-openbao-pki-tls.md`](IA_05-openbao-pki-tls.md) | El `.env` del secreto cero, la ceremonia de la CA, estado del vault, dónde están las claves, certificados, mTLS |
| [`IA_06-simulador.md`](IA_06-simulador.md) | El celular simulado y el script del canal (con otro puerto, sin parar nada) |
| [`IA_07-git-lectura.md`](IA_07-git-lectura.md) | Mirar historia, diferencias y el README de la otra rama (commits los hace Juan) |
| [`IA_08-diagramas.md`](IA_08-diagramas.md) | Qué diagrama existe, dónde, qué muestra, cómo abrirlo o generarlo sin IA |
| [`IA_09-romper-y-reparar.md`](IA_09-romper-y-reparar.md) | Ejercicios: dañar una pieza en vivo, ver el síntoma, repararla |
| [`IA_10-herramientas-sin-ia.md`](IA_10-herramientas-sin-ia.md) | Dónde consultar sin IA: `--help`, `man`, `psql \?`, jq, docs oficiales, draw.io, lazydocker |
| [`IA_REGISTRO-sesiones.md`](IA_REGISTRO-sesiones.md) | Lo que probó el agente de contexto limitado, iteración por iteración, y el conteo de uso |

## Convención de cada entrada

```
### <para qué sirve, en palabras>
`<la orden>`
- Partes: qué hace cada palabra y bandera
- Espera: lo que se ve cuando sale bien
- Si falla: el síntoma típico y qué mirar
- Riesgo: ninguno · borra datos · afecta otros proyectos
- Necesidad: ESENCIAL (sin ella el nodo no funciona o no se demuestra) · ÚTIL · RARA o PRESCINDIBLE, con el «Motivo de necesidad» (por qué existe y cuándo sí haría falta)
- Probado: <fecha · iteración · funcionó|falló: qué se vio (filas del registro)> o «sin probar»
```
Los ejercicios de la hoja 09 cambian «Partes/Espera» por «Daño/Síntoma/Reparar/Comprobar». Lo que se aparta del camino
de la demo no se borra: va a «Fuera del camino de la demo» de su hoja, con su motivo. Si una iteración contradice a
otra, manda la más reciente.

## Advertencias que valen para todo

- **El nombre del proyecto de Compose sale de la carpeta** (`despliegue`). Dos copias del repo con la misma carpeta
  se pisan. Para un laboratorio aparte: `COMPOSE_PROJECT_NAME=<otro>` en `despliegue/.env` o `-p <otro>`, y otros
  `FLORLOGIC_GATEWAY_PUERTO`, `FLORLOGIC_BASE_PUERTO` y `FLORLOGIC_RED` (las pruebas usaron `florlogic-manual`, 9443,
  55432 y 172.30.42.0/24).
- **Nada de `docker system prune` ni `docker image prune -a` sin filtro:** borran imágenes y volúmenes de otros
  proyectos de la máquina (SonarQube, Keycloak, glubia…). Siempre con `--filter label=com.docker.compose.project=<p>`.
- **`FLORLOGIC_CA_RAIZ` exportada antes de la ceremonia** hace fallar `./mvnw verify` en `SimuladorAplicacionTest`:
  usar `env -u FLORLOGIC_CA_RAIZ ./mvnw verify` o exportarla después (hoja 02).
- **Qué relee cada orden:** la plantilla montada del gateway la relee `DC restart gateway`; una variable de entorno
  nueva necesita `DC up -d --wait <servicio>` (recrea); un secreto del `.env` solo se lee al **crear** el contenedor:
  `--force-recreate` o `DC rm -sf <servicio>` antes del `up`. Tabla completa en la
  [hoja 01](IA_01-compose-docker.md#regla-del-wait-y-qué-relee-cada-orden).
- **`./mvnw clean` con Eclipse abierto** deja `contrato` sin resolver: usar `verify` sin `clean`, o F5 + Project › Clean.
- **Sin Docker**, `./mvnw verify` sale verde pero **no prueba nada contra PostgreSQL**.
- **Archivos de trabajo** (`abrir.json`, `ataque.json`, respaldos, copias de lo que se daña) van en `$T`
  (`T=~/florlogic-demo`, chuleta), nunca dentro del repo. Todo el manual escribe `$T/…`.

## Avisos fuera del manual (para Juan; la IA no actuó)

- **El proyecto `despliegue` aparece `restarting(1), running(1)`** en `docker compose ls -a` (iteración 2, filas 104
  y 137): un servicio de tu nodo real está en bucle de reinicio, probablemente el gateway. Las pruebas no lo tocaron.
  Para mirarlo tú: `docker compose -p despliegue ps -a` y `docker compose -p despliegue logs --tail=30 gateway`
  (si es una plantilla o un certificado: hoja 09, «Romper el gateway»).
- **Las imágenes `florlogic-canal-*` (~1,2 GB) se reconstruyen en cada corrida de `scripts/IA_probar-canal.sh`** y las
  anteriores quedan sin etiqueta ocupando disco (los ids cambiaron tras la corrida de la iteración 2, fila 106). Para
  verlas y borrarlas tú mismo:
  - ver las actuales: `docker images | grep florlogic-canal` · ver las colgadas (sin etiqueta): `docker images --filter dangling=true`
  - borrar solo las colgadas del script: `docker image prune --filter label=com.docker.compose.project=florlogic-canal`
  - borrar también las actuales (la próxima corrida tardará más): `docker rmi florlogic-canal-backend florlogic-canal-openbao florlogic-canal-openbao-init`
  - detalle en la [hoja 06](IA_06-simulador.md#borrar-las-imágenes-que-deja-el-script). Sin probar (las pruebas no borran imágenes tuyas).
- **Dudas de comportamiento para decidir tú:** una sesión `parcial` cerrada luego con `completa:true` sigue `parcial`
  (¿el primer cierre manda a propósito?); una partición a una sesión ya cerrada responde 404 «Sesión desconocida» en
  vez de algo como «Sesión cerrada» (¿intencional?); el simulador dice «1 nuevas» (sin concordancia de número).
