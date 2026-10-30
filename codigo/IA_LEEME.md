REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 13-oct-2026, a pedido de Juan
Estado de revisión: SIN REVISAR
Manda por encima: `docs/03-arquitectura/flujos/transacciones/IA_00-LEEME-transacciones.md` (rama `documentacion-desarrollo`) · `ADR.xlsx`

# Código de FlorLogic — esqueleto

Armazón para TX-1 (sincronización) y TX-2 (respaldo con observabilidad). **Desde el 19-oct-2026 funciona
el canal de TX-1** (camino mínimo funcional, D-17): el simulador manda una partición, el backend la guarda en
PostgreSQL y responde. **Desde el 20-oct-2026 entra por el gateway** (Nginx, TEC-15; `despliegue/ESPEC-gateway-finca.md`),
con visto de Juan; falta que la fila TX-1 del LEEME de transacciones lo diga. **Desde el 21-oct-2026 el gateway lleva
un WAF** (ModSecurity v3 + OWASP CRS 4, TEC-31 propuesta con visto de Juan; `despliegue/ESPEC-waf-finca.md`): falta que
Juan pase TEC-31 a `ACEPTADO`. En el LEEME de transacciones, TX-1 está `AJUSTAR 19-oct` (aceptado solo el canal) y
TX-1.K `ACEPTADO`; TX-2 sigue `POR ACEPTAR`. Lo que sí está aceptado (13-oct-2026):
TEC-01..TEC-30, P-T01..P-T07, P-S01, P-S02, P-S03 y P-N01. Las versiones exactas y las diferencias con
lo aceptado (Java 27 en vez de 21, entre otras) están en `../.claude/contexto/IA_TECNOLOGIAS-vigentes.md`.

## Cómo se usa

```
./mvnw verify                                   # compila todo y corre las pruebas (con Docker, también el nodo entero)
docker compose -f despliegue/compose.yaml up -d --build --wait   # el nodo: base, OpenBao, openbao-init, backend y gateway (puerto 80)
docker compose -f despliegue/compose.yaml logs openbao-init       # qué hizo el inicio del vault en esta corrida
scripts/IA_probar-canal.sh                      # el canal de TX-1 de punta a punta, por el gateway (necesita Docker)
java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar sincronizar \
     --sesion <uuid> --archivo <particion.json> # --url por defecto: el gateway, http://localhost:80
```

**El nodo de la finca corre entero en Compose** (`despliegue/espec/ESPEC-esqueleto-tx1.md`, Segmento 1). El gateway es
la única puerta: ni el backend ni OpenBao publican puertos, y `/actuator` por el gateway es 404. La salud del backend
la dice Compose (`docker compose -f despliegue/compose.yaml ps`). Orden de arranque: `base` sana → `openbao` sano →
`openbao-init` termina con 0 → `backend`; el gateway arranca solo y responde 502 mientras el backend no contesta.

### El secreto cero: un `.env` fuera de git

`compose up` para si falta alguna de estas variables (A-10). En desarrollo van en `despliegue/.env`, que Compose lee
solo y que **nunca entra a git** (`.gitignore`, PR-06); en CI, secretos de GitHub Actions; en las pruebas, los inventa
`NodoCompose`.

```
# despliegue/.env  — valores inventados por ti, largos y al azar (p. ej. «openssl rand -hex 16»)
FLORLOGIC_PG_ADMIN=…                 # clave de administrador de PostgreSQL; openbao-init la copia al vault
FLORLOGIC_HSM_PIN=…                  # PIN del token PKCS#11 que abre el vault
FLORLOGIC_SOFTHSM_SO_PIN=…           # solo fuera de la finca: crea el token SoftHSM
FLORLOGIC_SOFTHSM_CREAR=si           # solo fuera de la finca; en la finca, vacía (A-29)
BAO_HSM_LIB=/usr/lib/softhsm/libsofthsm2.so
```

La clave de la base se escribe **una sola vez**, en el primer arranque, en la base y en el vault a la vez (A-25). Si
tu máquina ya tenía el volumen `datos` de antes del vault (otra clave), haz **una vez**
`docker compose -f despliegue/compose.yaml down -v`: borra la base de desarrollo y el vault, y el siguiente `up` los
crea juntos. Después, cambiar `FLORLOGIC_PG_ADMIN` **no** cambia la clave: `openbao-init` compara la del `.env`, la del
vault y la de la base, y si no coinciden para con un mensaje y el backend no arranca. Cambiar la clave es una rotación
con `generate-root` (K-22). Lo mismo con `FLORLOGIC_RED` (la subred del nodo, por defecto `172.30.0.0/24`): el AppRole
del backend solo sirve desde la subred con la que se creó.

El primer arranque deja **tres partes de recuperación** del vault en el volumen `bao-entrega`; en la finca el operador
las saca y las borra (A-24). En desarrollo no hacen falta: se van con el `down -v`.

Si el puerto 80 está ocupado, `FLORLOGIC_GATEWAY_PUERTO=8081` cambia a la vez el puerto que publica Compose y el que
usa el simulador por defecto. Si otro PostgreSQL ya tiene el 5432 del anfitrión, `FLORLOGIC_BASE_PUERTO` lo cambia.

`base`, `openbao`, `backend` y `gateway` llevan `restart: unless-stopped`, porque en el nodo de finca tienen que volver
solos tras un corte de luz. En un computador de desarrollo eso significa que, después de un `compose up`, vuelven a
arrancar con Docker en cada encendido y ocupan el 80 y el 5432. Para apagarlos:
`docker compose -f despliegue/compose.yaml down` (sin `-v`: la base y el vault se conservan).
`scripts/IA_probar-canal.sh` usa su propio proyecto (`florlogic-canal`), su subred y un secreto cero inventado en
cada corrida; al terminar hace `down -v` de ese proyecto, no del tuyo.

El WAF del gateway arranca bloqueando (`FLORLOGIC_WAF_MODO=On`); con `FLORLOGIC_WAF_MODO=DetectionOnly` solo anota. El
cambio se aplica recreando el contenedor (`docker compose -f despliegue/compose.yaml up -d gateway`); reiniciarlo no
relee la variable. Su registro (una línea JSON por petición bloqueada o con error) sale por
`docker compose -f despliegue/compose.yaml logs gateway`: trae la traza, la regla y el valor entero del campo que la disparó, nunca
cabeceras, cuerpos ni `Authorization`.

### Ante un falso positivo del WAF

Una partición legítima que el WAF bloquea recibe `403` con `type` `…/gateway/bloqueado`; el celular la retiene y no
se pierde nada (ADR-002). Para destrabarla (`despliegue/ESPEC-waf-finca.md` A-13):

1. Buscar en el registro del WAF la entrada con `"unique_id"` igual al `traceId` que vio el celular. Si el cliente no
   lo muestra (hoy el simulador solo dice `http_403`), buscar por la IP del celular y la hora. La entrada dice qué
   regla saltó (`ruleId`) y con qué valor (`data`, el campo entero).
2. Si la partición era legítima, pasar el nodo a `DetectionOnly` y recrear el gateway. Mientras tanto el WAF **no
   detiene nada**, tampoco lo que no puede revisar entero: volver a `On` cuanto antes.
3. Que el celular vuelva a entregar.
4. Escribir la exclusión en `despliegue/gateway/default.conf.template` con su prueba en `CanalPorGatewayTest` (una
   exclusión entra solo con un falso positivo comprobado, A-11) y volver a `On`.

Solo hace falta el JDK **Temurin 27** (`mise.toml` en la raíz lo fija; con mise basta `mise install`);
`mvnw` baja Maven 3.9.16. En la nube, `scripts/preparar-nube.sh` instala el JDK y las dependencias.

## Qué hay

| Módulo | Qué es | Tecnología |
|---|---|---|
| `contrato/` | La forma del cable celular ↔ nodo de finca: DTO como records, códigos, cabeceras y la barrera de lectura (`LectorDto` estricto y tolerante, nunca lanza). Huella y esquema: después. ESPEC, diagramas y plan en `contrato/espec/ESPEC-contrato-ingesta.md` y `contrato/espec/diagramas/` | Jackson 3 |
| `backend-finca/` | Monolito modular: los 15 módulos de §3 de `IA_TECNOLOGIAS`, cada uno con su `package-info` | Spring Boot 4.1.1 · Modulith 2.1.1 · JDBC · Flyway · PostgreSQL |
| `servicios-en-linea/` | Recepción de respaldos, telemetría y panel (TX-2) | Spring Boot 4.1.1 · puerto 8090 |
| `simulador/` | El celular simulado, sin órdenes todavía | Spring Boot 4.1.1 sin web · picocli · SQLite |
| `pruebas-extremo-a-extremo/` | Vacío; aquí van las pruebas del §10 de cada TX | Testcontainers |
| `despliegue/` | `compose.yaml` del nodo entero: la base, el vault (`openbao/`: imagen propia con SoftHSM y el sello `pkcs11`, y `init.sh`, el inicio idempotente), el backend (imagen de `backend-finca/Dockerfile`) y el gateway, la única puerta del nodo, con su WAF (`gateway/default.conf.template` y `gateway/modsecurity-override.conf.template`). ESPEC y diagramas: `despliegue/espec/ESPEC-esqueleto-tx1.md`, `despliegue/ESPEC-gateway-finca.md`, `despliegue/ESPEC-waf-finca.md`, `despliegue/espec/diagramas/` y `despliegue/diagramas/` | Docker Compose · PostgreSQL 16.15 · OpenBao 2.7.2 + SoftHSM · Nginx 1.30.6 · ModSecurity v3 + OWASP CRS 4.30.0 (imagen `owasp/modsecurity-crs`) |

Todo lo ejecutable es Spring Boot. `contrato` es una biblioteca: no arranca sola, la usan el backend y
el simulador.

## Dentro de cada módulo del backend

Primero se corta por módulo y después por capa. **Desde el 30-oct-2026 (Segmento R, A-83, A-84, A-97) los paquetes
llevan la forma y los nombres de ucotestapi** (`~/eclipse-workspace/ucotestapi`), con un hexágono por módulo. Antes,
desde el 22-oct-2026, seguían el **diagrama de
paquetes hexagonal de Juan** (`.claude/contexto/referencias/empaquetado-hexagonal-por-modulo.png`, la referencia principal; puertos y adaptadores; reemplaza la forma del 20-oct, que dejaba sueltos puertos,
adaptadores y DTO). D-18: el empaquetado no se recorta ni en el camino mínimo, así que los 13 módulos sin código
ya tienen todo el árbol, solo con `package-info.java`. Diagrama: `backend-finca/espec/diagramas/IA_reempaquetado-hexagonal.drawio`.

```
<modulo>/                                                      API pública: lo único que otros módulos pueden usar
<modulo>/dominio/                                              entidades, valores y reglas, Java puro: sin Spring, sin SQL, sin contrato
<modulo>/servicios/input/ports/                                puerto de entrada (…InputPort): recibe un request y devuelve un response
<modulo>/servicios/input/dto/request/ · dto/response/          DTO de petición y de respuesta del puerto, sin dominio
<modulo>/servicios/input/adapter/                              …InputAdapter: implementa el puerto y solo orquesta (ensamblador → caso de uso → ensamblador)
<modulo>/servicios/input/assembler/                            …InputAssembler: solo traduce request → dominio y dominio → response
<modulo>/servicios/casosuso/                                   caso de uso: interfaz en lenguaje de dominio
<modulo>/servicios/casosuso/impl/                              su implementación: abre la transacción y orquesta dominio y puertos de salida
<modulo>/servicios/output/ports/repositorios/                  puertos de persistencia (…Repositorio) y piezas técnicas (reloj, ids)
<modulo>/servicios/output/ports/serviciosexternos/             puertos hacia otro módulo o un servicio externo
<modulo>/servicios/output/adapter/                             …OutputAdapter: implementa un puerto de serviciosexternos con la API pública de otro módulo
<modulo>/servicios/output/assembler/                           …OutputAssembler: solo traduce dominio ↔ DTO de salida
<modulo>/servicios/output/dto/                                 DTO de salida (…DTO): lo que cruza el puerto de salida, distinto al de entrada
<modulo>/infraestructura/input/controladores/                  controladores HTTP (y oyentes o trabajos) y su ManejadorDeErrores
<modulo>/infraestructura/output/adapter/                       adaptadores técnicos (GeneradorDeIdsAdapter, RelojUtc)
<modulo>/infraestructura/output/repositorios/estructurados/    …RepositorioAdapter con JdbcClient; implementan los puertos de repositorios
<modulo>/infraestructura/output/repositorios/estructurados/mapper/   fila ↔ DTO de salida
<modulo>/infraestructura/output/serviciosexternos/             clientes de servicios externos propios del módulo
<modulo>/transversal/                                          lo que cruza las capas del módulo (registro); los mensajes son el módulo propio `mensajes` (VER-9)
```

El paquete `assembler` es una desviación consciente de ucotestapi, donde el `InputAdapter` traduce en su propio
método (A-84). El flujo de `ingesta`: `ParticionesControlador` → `RecibirParticionInputPort` (lo implementa
`RecibirParticionInputAdapter`, que usa `RecibirParticionInputAssembler`) → `RecibirParticionCasoUso` (lo implementa
`RecibirParticionCasoUsoImpl`) → `AnotacionRecibidaOutputAssembler` → `AnotacionRepositorio` (lo implementa
`AnotacionRepositorioAdapter`). Lo inesperado lo responde `ManejadorDeErrores` con un `500` genérico (C-116). Fuentes: Martin · Clean Architecture p. 185 y p. 195 (por los puertos cruzan
estructuras simples, nunca entidades), Fowler · PoEAA secc. 100 (el ensamblador, para que DTO y dominio no dependan
uno del otro), Vernon · Implementing DDD p. 170 (el caso de uso es el cliente directo del dominio).

**El envoltorio:** el backend completo tiene su propio `dominio/` (núcleo compartido, Evans · Shared Kernel),
`infraestructura/` (seguridad web, migraciones, configuración: lo que antes era el módulo `plataforma`) y
`transversal/` directo bajo `co.florlogic.finca`. Spring Modulith los cuenta como tres módulos más (17 en total).
Solo el monolito modular vive aquí: el simulador, el contrato y `servicios-en-linea` no entran en `backend-finca`.

Las dependencias apuntan hacia adentro: `infraestructura` → `servicios` → `dominio`. Modulith trata estos
subpaquetes como internos del módulo (`ModulosTest`), y `CapasTest` (ArchUnit) verifica la regla hacia adentro, que el
controlador solo conoce `input/ports` e `input/dto`, y que ningún `adapter` de `servicios` construye dominio (C-115).

## Nombres de las clases: el papel va al final (OBS-1-2)

Decisión de Juan del 21-oct-2026: cada archivo dice en su nombre qué papel cumple, para leerlo en el IDE sin
mirar la carpeta. Primero el nombre de negocio y al final el papel, en español (PR-01). El paquete sigue mandando
dónde vive (D-18); el sufijo lo repite a propósito.

| Sufijo | Qué es | Ejemplo |
|---|---|---|
| `Aplicacion` | La clase que arranca Spring Boot | `BackendFincaAplicacion` |
| `InputPort` | Interfaz que el controlador llama; la implementa un `InputAdapter` | `RecibirParticionInputPort` |
| `Request` · `Response` | Record que entra a un puerto de entrada y el que sale de él, sin dominio | `RecibirParticionRequest` · `RecibirParticionResponse` |
| `InputAdapter` | Clase que implementa el puerto de entrada y solo orquesta: ensamblador → caso de uso → ensamblador | `RecibirParticionInputAdapter` |
| `InputAssembler` | Clase que solo traduce request → dominio y dominio → response | `RecibirParticionInputAssembler` |
| `CasoUso` · `CasoUsoImpl` | Interfaz del caso de uso, en dominio, y la clase que la implementa (en `impl`) | `RecibirParticionCasoUso` · `RecibirParticionCasoUsoImpl` |
| `OutputAssembler` · `DTO` | Lo que convierte el dominio en el DTO de salida, y ese DTO | `AnotacionRecibidaOutputAssembler` · `AnotacionRecibidaDTO` |
| `Repositorio` | Puerto de salida de persistencia que el caso de uso necesita; lo implementa un `RepositorioAdapter` | `AnotacionRepositorio` |
| `OutputAdapter` | Implementa un puerto de `serviciosexternos` con la API pública de otro módulo | `EvaluadorDeAnotacionesOutputAdapter` (Segmento 2) |
| `Controlador` | Infraestructura de entrada HTTP que llama a un puerto de entrada | `ParticionesControlador` |
| `RepositorioAdapter` | Infraestructura que implementa un puerto de repositorio contra PostgreSQL | `AnotacionRepositorioAdapter` |
| `Adaptador<Técnica>` | Infraestructura del simulador que traduce entre afuera y un puerto (`Http`, `Cli`); el simulador conserva su forma (A-97) | `NodoDeFincaAdaptadorHttp` |
| `Dto` | Record del contrato que solo viaja por el cable, sin reglas | `ParticionDto`, `AnotacionDto` |
| `Valor` | Objeto de valor del dominio: record inmutable con reglas, sin identidad | `ConsecutivoValor`, `HuellaValor` |
| `Id` | Identidad tipada del dominio | `AnotacionId`, `DispositivoId` |
| `Agregado` | Raíz de agregado del dominio: clase con identidad que cambia por verbos de negocio, sin setters | `AvanceDelDispositivoAgregado` |
| `Constantes` · `Utilidad` · `Interfaz` | Clase de constantes, ayudante interno, interfaz que no es puerto | `CabecerasConstantes`, `ListasUtilidad`, `IdDeCatalogoInterfaz` |
| `…DobleEnMemoria` | Implementación falsa de un puerto, solo en pruebas | `AnotacionRepositorioDobleEnMemoria` |
| (sin sufijo) | `enum` (su forma ya lo dice) y clases cuyo nombre ya es su papel (`LectorDto`, `FabricaDeOrdenes`) | `CodigoMarca`, `CodigoIngreso` |

Las pruebas siguen a su clase (`RecibirParticionCasoUsoImplTest`). `AnotacionRecibida` sigue en el dominio: el
reempaquetado del 22-oct choca con A-74 (sacarla a la entrada como `AnotacionEntrante`); lo decide Juan (OBS-1-7).

Renombrado del 30-oct-2026 (Segmento R, nombres de ucotestapi; detalle en `ESPEC-dominio-ingesta.md` §5):

| Antes | Ahora |
|---|---|
| `servicios/input/port/RecibirParticionPuertoEntrada` | `servicios/input/ports/RecibirParticionInputPort` |
| `RecibirParticionEntradaDto` · `ParticionRecibidaRespuestaDto` (`servicios/input/dto`) | `RecibirParticionRequest` (`dto/request`) · `RecibirParticionResponse` (`dto/response`) |
| `servicios/input/adapter/RecibirParticionEnsambladorEntrada` | `servicios/input/adapter/RecibirParticionInputAdapter` + `servicios/input/assembler/RecibirParticionInputAssembler` |
| `servicios/output/port/AnotacionesRecibidasPuertoSalida` | `servicios/output/ports/repositorios/AnotacionRepositorio` |
| `AnotacionRecibidaSalidaDto` · `servicios/output/adapter/AnotacionRecibidaEnsambladorSalida` | `AnotacionRecibidaDTO` · `servicios/output/assembler/AnotacionRecibidaOutputAssembler` |
| `infraestructura/input/controlador/` | `infraestructura/input/controladores/` (+ `ManejadorDeErrores`) |
| `infraestructura/output/adapter/AnotacionesRecibidasAdaptadorJdbc` | `infraestructura/output/repositorios/estructurados/AnotacionRepositorioAdapter` |

Renombrado del 22-oct-2026 (reempaquetado hexagonal):

| Antes | Ahora |
|---|---|
| `servicios/puertos/entrada/RecibirParticionPuertoEntrada` | `servicios/input/port/RecibirParticionPuertoEntrada` |
| `RecibirParticionComando` · `ParticionRecibidaResultado` | `RecibirParticionEntradaDto` · `ParticionRecibidaRespuestaDto` (`servicios/input/dto`) |
| `RecibirParticionCasoUso` (clase) | `RecibirParticionCasoUso` (interfaz) + `casosuso/impl/RecibirParticionCasoUsoImpl` |
| — | `RecibirParticionEnsambladorEntrada`, `AnotacionRecibidaEnsambladorSalida`, `AnotacionRecibidaSalidaDto`, `RecepcionDeParticionValor` (dominio) |
| `infraestructura/entrada/web/ParticionesAdaptadorWeb` | `infraestructura/input/controlador/ParticionesControlador` |
| `infraestructura/salida/persistencia/AnotacionesRecibidasAdaptadorJdbc` | `infraestructura/output/adapter/AnotacionesRecibidasAdaptadorJdbc` |
| módulo `plataforma` | `co.florlogic.finca.infraestructura` (y `dominio`, `transversal` en la raíz) |

Renombrado del 21-oct-2026 (para leer los documentos anteriores, que conservan los nombres viejos):

| Antes | Ahora |
|---|---|
| `BackendFinca` · `ServiciosEnLinea` | `BackendFincaAplicacion` · `ServiciosEnLineaAplicacion` |
| `RecibirParticion` · `ParticionRecibida` · `RecibirParticionServicio` | `RecibirParticionPuertoEntrada` · `ParticionRecibidaResultado` · `RecibirParticionCasoUso` |
| `AnotacionesRecibidas` · `AnotacionesRecibidasJdbc` · `AnotacionesRecibidasEnMemoria` | `AnotacionesRecibidasPuertoSalida` · `AnotacionesRecibidasAdaptadorJdbc` · `AnotacionesRecibidasDobleEnMemoria` |
| `ControladorParticiones` | `ParticionesAdaptadorWeb` |
| `Consecutivo` · `Huella` · `Captura` · `AvanceDelDispositivo` | `ConsecutivoValor` · `HuellaValor` · `CapturaValor` · `AvanceDelDispositivoAgregado` |
| `Cabeceras` · `Listas` · `IdDeCatalogo` · `Lectura` | `CabecerasConstantes` · `ListasUtilidad` · `IdDeCatalogoInterfaz` · `LecturaResultado` |
| `ParticionRequest` · `ResultadoParticion` | `ParticionDto` · `ResultadoParticionDto` |
| `Anotacion` · `Conteo` · `Descarte` · `Desfase` · `Excepcion` · `PrimerAcuse` · `SesionCaptura` · `Tiempo` · `CapturaCampo` · `Valor` (contrato) | el mismo nombre + `Dto` |
| `Simulador` · `Sincronizar` · `SincronizarParticion` · `SincronizarParticionServicio` | `SimuladorAdaptadorCli` · `SincronizarAdaptadorCli` · `SincronizarParticionPuertoEntrada` · `SincronizarParticionCasoUso` |
| `NodoDeFinca` · `ClienteIngesta` | `NodoDeFincaPuertoSalida` · `NodoDeFincaAdaptadorHttp` |

## Dentro del simulador

El simulador sigue el mismo molde hexagonal (decisión de Juan del 20-oct-2026, OBS-2-12). Todavía no tiene
`dominio/`: llega con el almacén de salida (ADR-002), sin paquetes vacíos de relleno (D-18).

```
co.florlogic.simulador/                          SimuladorAplicacion (arranque de Spring Boot y picocli)
├── servicios/puertos/entrada/                   SincronizarParticionPuertoEntrada · SincronizarParticionComando
├── servicios/puertos/salida/                    NodoDeFincaPuertoSalida
├── servicios/casosuso/                          SincronizarParticionCasoUso (package-private)
├── infraestructura/entrada/cli/                 SimuladorAdaptadorCli · SincronizarAdaptadorCli · FabricaDeOrdenes (picocli)
└── infraestructura/salida/http/                 NodoDeFincaAdaptadorHttp (implementa NodoDeFincaPuertoSalida)
```

## Pruebas

Ordenadas por la pirámide, de abajo arriba (plantilla de pruebas de la skill, §0.7: cada regla en el nivel más
bajo que pueda detectarla). 130 pruebas (29-oct-2026); con Docker no se salta ninguna. La tabla no lista todas.

| Nivel | Clase | Qué comprueba |
|---|---|---|
| Unitaria | `contrato` · `DtoJsonTest` (14) | La petición y la respuesta doradas se leen y se escriben igual; null ≡ ausente; enums tolerantes; incoherencias |
| Unitaria | `contrato` · `LectorDtoTest` (10) | La barrera de lectura nunca lanza; el estricto rechaza campos desconocidos y el tolerante los ignora |
| Unitaria | `backend-finca` · `RecibirParticionInputAssemblerTest` (2), `RecibirParticionInputAdapterTest` (2) | El ensamblador solo traduce (request → dominio con la sesión, conteos → response); el adaptador solo orquesta |
| Unitaria | `backend-finca` · `RecibirParticionCasoUsoImplTest` (5) | El caso de uso de ingesta con el doble en memoria: aplicadas, sin efecto, mayor consecutivo |
| Unitaria | `backend-finca` · `AnotacionRepositorioDobleEnMemoriaTest` (4) | El doble en memoria cumple el contrato del puerto (`AnotacionRepositorioContratoTest`) |
| Unitaria | `simulador` · `SincronizarParticionCasoUsoTest` (2) | El caso de uso del celular entrega la partición tal cual y devuelve lo del nodo |
| Unitaria | `simulador` · `SincronizarAdaptadorCliTest` (4) | La orden `sincronizar` con un puerto falso: códigos de salida 0, 1 y 2 |
| Unitaria | `simulador` · `SimuladorAdaptadorCliTest` (4), `FabricaDeOrdenesTest` (2) | Uso, `--version`, `--help`, opción desconocida; las órdenes salen de Spring si son beans |
| Arquitectura | `backend-finca` · `ModulosTest` (2), `CapasTest` (4) | Ningún módulo toca la parte interna de otro y están los 17 (TEC-05); dentro de cada módulo, las capas hacia adentro, el controlador solo con `input/ports` e `input/dto`, y ningún `adapter` de `servicios` construye dominio (C-115) |
| Integración estrecha | `backend-finca` · `AnotacionRepositorioAdapterTest` (4) | El adaptador JDBC contra PostgreSQL real cumple el mismo contrato que el doble. **Se salta sin Docker** |
| Integración estrecha | `simulador` · `NodoDeFincaAdaptadorHttpTest` (4) | El adaptador HTTP del celular contra un nodo falso: 200, 422, otro código y sin conexión, sin lanzar |
| Integración amplia | `backend-finca` · `CanalPorGatewayTest` (25) | **El canal por el gateway y su WAF**: el WAF real (imagen de `compose.yaml`) con los dos archivos de `despliegue/gateway/`, backend y PostgreSQL reales. 200 y 422 pasan tal cual; ruta ajena 404, otro método 405, más de 1 MiB 413, ruta larga 414, ruta con `..` 400 y backend apagado 502, todos en `problem+json`; el `traceId` sale del `traceparent`. El WAF: un ataque es 403 `gateway/bloqueado`; 200 anotaciones de casi 1 MiB pasan y un ataque en la última se bloquea; UTF-16 o BOM, 400; 10.000 campos o más, 413; un cuerpo que no es JSON, 403; `Authorization` no se revisa; el registro trae la traza y la regla, sin el secreto; `DetectionOnly` deja pasar y anota. **Se salta sin Docker** |
| Extremo a extremo | `pruebas-extremo-a-extremo` · `NodoCompletoTest` (2) | **El nodo entero con el `compose.yaml` real** (`NodoCompose`): construye las imágenes, el backend saca su clave de OpenBao, la partición dorada entra por el gateway con `200` y quedan 2 filas en la base; solo el gateway publica puerto. **Se salta sin Docker** |
| Extremo a extremo | `pruebas-extremo-a-extremo` · `NodoArranqueSeguroTest` (6) | **El arranque falla bien**, sobre un solo nodo: `openbao-init` otra vez no cambia nada; sin «listo» lo reconoce por el AppRole; otra subred guardada lo para; otra `FLORLOGIC_PG_ADMIN` para el arranque y no reescribe; sin vault el backend no queda escuchando (502); sin la bandera, la imagen de OpenBao no crea llave. Usa la orden `docker compose` del anfitrión. **Se salta sin Docker** |
| Humo | `backend-finca` · `ArranqueBackendFincaTest` (2) | Arranca contra PostgreSQL 16, Flyway aplica V1 y la salud da `UP`. **Se salta sin Docker** |
| Humo | `servicios-en-linea` · `ServiciosEnLineaAplicacionTest` (2), `simulador` · `SimuladorAplicacionTest` (2) | Arrancan; salud `UP`; el simulador sin servidor web |

Las pruebas de `pruebas-extremo-a-extremo` levantan cada una su propio proyecto de Compose, con su subred, puertos al azar y su secreto cero, y al terminar hacen `down -v` y borran sus imágenes: no tocan el nodo de desarrollo (A-20, A-42). La primera corrida construye las imágenes y tarda varios minutos. A mano, la corrida de punta a punta es `scripts/IA_probar-canal.sh`. Si `target/` trae clases de paquetes viejos (pasó tras mover `ingesta`), el contexto de Spring no arranca porque ve el controlador de particiones dos veces: `./mvnw clean verify`.

## Abrir en IntelliJ IDEA o Eclipse

Es un proyecto Maven multi-módulo normal; los dos IDE lo leen sin complementos extra.

- **IntelliJ IDEA**: *File → Open* y elegir `codigo/pom.xml` → *Open as Project*. Fijar el SDK del
  proyecto en un JDK 27 o superior.
- **Eclipse** (IDE for Java Developers, trae m2e): *File → Import → Maven → Existing Maven Projects*
  y elegir la carpeta `codigo/`. Aparecen el padre y los cinco módulos. JDK 27 o superior en
  *Installed JREs*.
- Para arrancar desde el IDE: `BackendFincaAplicacion` con el perfil `local` y `FLORLOGIC_DB_CLAVE` (la de tu `.env`)
  en la configuración de ejecución, contra la `base` de `compose.yaml` (sin vault, A-13),
  `ServiciosEnLineaAplicacion` y `SimuladorAplicacion`. Las pruebas corren con JUnit desde el mismo IDE.
- Los archivos que generan los IDE (`.idea/`, `*.iml`, `.classpath`, `.project`, `.settings/`) no se
  suben.

## Dónde va TX-1

- **Hecho (19-oct-2026):** `contrato/` Segmento 1 (forma de los mensajes) y Segmento 2 (el canal). Detalle y
  plan en `contrato/espec/ESPEC-contrato-ingesta.md` §8; punto de reentrada en `../.claude/contexto/IA_RETOMAR-AQUI.md`.
- **Hecho (20-oct-2026):** el gateway de la finca en el camino mínimo (Segmento 1 de `despliegue/ESPEC-gateway-finca.md`).
  Lo que dejó para después (TLS, tasa, credencial, imagen del backend con Jib) está en su §7.
- **Hecho (21-oct-2026):** el WAF del gateway (Segmento 1 de `despliegue/ESPEC-waf-finca.md`), verificado e integrado con el
  refactor del hexágono por módulo (22-oct); su explicación se omitió por decisión de Jeronimo, así que no está aceptado. Lo que dejó
  para después (qué hace el celular con `gateway/bloqueado`, la carga de ESC-38, la línea LTS de CRS) está en su §7.
- **Después**, en el orden en que Juan los pida: sesión `{id}` y credencial, huella y corrupción, esquema JSON,
  códigos finos y rechazo por anotación (A-58), reglas y marcas, marcas de agua reales, reintentos.

TX-2 necesita P-R01..P-R10; P-R10 (proveedor de nube) bloquea la subida real.
