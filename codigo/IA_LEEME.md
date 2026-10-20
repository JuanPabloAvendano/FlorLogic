REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 13-oct-2026, a pedido de Juan
Estado de revisión: SIN REVISAR
Manda por encima: `docs/03-arquitectura/flujos/transacciones/IA_00-LEEME-transacciones.md` (rama `documentacion-desarrollo`) · `ADR.xlsx`

# Código de FlorLogic — esqueleto

Armazón para TX-1 (sincronización) y TX-2 (respaldo con observabilidad). **Desde el 19-oct-2026 funciona
el canal de TX-1** (camino mínimo funcional, D-17): el simulador manda una partición, el backend la guarda en
PostgreSQL y responde. En el LEEME de transacciones, TX-1 está `AJUSTAR 19-oct` (aceptado solo el canal) y
TX-1.K `ACEPTADO`; TX-2 sigue `POR ACEPTAR`. Lo que sí está aceptado (13-oct-2026):
TEC-01..TEC-30, P-T01..P-T07, P-S01, P-S02, P-S03 y P-N01. Las versiones exactas y las diferencias con
lo aceptado (Java 27 en vez de 21, entre otras) están en `../.claude/contexto/IA_TECNOLOGIAS-vigentes.md`.

## Cómo se usa

```
./mvnw verify                                   # compila todo y corre las pruebas
docker compose -f despliegue/compose.yaml up -d # PostgreSQL 16 de desarrollo
java -jar backend-finca/target/backend-finca-0.1.0-SNAPSHOT.jar --server.port=18080   # el 8080 suele estar ocupado
curl localhost:18080/actuator/health
scripts/IA_probar-canal.sh                      # el canal de TX-1 de punta a punta (necesita Docker)
java -jar simulador/target/simulador-0.1.0-SNAPSHOT.jar sincronizar \
     --url http://localhost:18080 --sesion <uuid> --archivo <particion.json>
```

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
| `despliegue/` | `compose.yaml` del nodo, hoy solo con la base | Docker Compose |

Todo lo ejecutable es Spring Boot. `contrato` es una biblioteca: no arranca sola, la usan el backend y
el simulador.

## Dentro de cada módulo del backend

Primero se corta por módulo y después por capa. Los paquetes siguen la tabla «Dónde vive cada pieza» de
las plantillas de la skill (endpoint, caso-de-uso, adaptador-persistencia), con nombres en español
(decisión de Juan, 20-oct-2026; D-18: el empaquetado no se recorta ni en el camino mínimo). Los módulos sin
código tienen solo `package-info.java` y toman esta forma cuando reciben su primera clase; no se crean
paquetes vacíos de relleno. `ingesta` ya la tiene completa:

```
<modulo>/                                   API pública: lo único que otros módulos pueden usar
<modulo>/dominio/                           entidades y reglas, Java puro: sin Spring, sin SQL, sin contrato
<modulo>/servicios/puertos/entrada/         un puerto de entrada por caso de uso, con su comando y su resultado
<modulo>/servicios/puertos/salida/          interfaces que el caso de uso necesita (solo las que existan)
<modulo>/servicios/casosuso/                un servicio por caso de uso (package-private); abre la transacción
<modulo>/infraestructura/entrada/web/       controladores HTTP (otros adaptadores de entrada: oyentes, trabajos)
<modulo>/infraestructura/salida/persistencia/  adaptadores JDBC (otros de salida: cliente HTTP, archivos)
```

Las dependencias apuntan hacia adentro: `infraestructura` → `servicios` → `dominio`. Modulith ya
trata estos subpaquetes como internos del módulo; la regla hacia adentro **todavía no se verifica**
(falta jMolecules + ArchUnit en `ModulosTest`).

## Dentro del simulador

El simulador sigue el mismo molde hexagonal (decisión de Juan del 20-oct-2026, OBS-2-12). Todavía no tiene
`dominio/`: llega con el almacén de salida (ADR-002), sin paquetes vacíos de relleno (D-18).

```
co.florlogic.simulador/                          SimuladorAplicacion (arranque de Spring Boot y picocli)
├── servicios/puertos/entrada/                   SincronizarParticion · SincronizarParticionComando
├── servicios/puertos/salida/                    NodoDeFinca
├── servicios/casosuso/                          SincronizarParticionServicio (package-private)
├── infraestructura/entrada/cli/                 Simulador · Sincronizar · FabricaDeOrdenes (picocli)
└── infraestructura/salida/http/                 ClienteIngesta (implementa NodoDeFinca)
```

## Pruebas

Ordenadas por la pirámide, de abajo arriba (plantilla de pruebas de la skill, §0.7: cada regla en el nivel más
bajo que pueda detectarla). 65 pruebas; con Docker no se salta ninguna.

| Nivel | Clase | Qué comprueba |
|---|---|---|
| Unitaria | `contrato` · `DtoJsonTest` (14) | La petición y la respuesta doradas se leen y se escriben igual; null ≡ ausente; enums tolerantes; incoherencias |
| Unitaria | `contrato` · `LectorDtoTest` (10) | La barrera de lectura nunca lanza; el estricto rechaza campos desconocidos y el tolerante los ignora |
| Unitaria | `backend-finca` · `RecibirParticionServicioTest` (5) | El caso de uso de ingesta con el doble en memoria: aplicadas, sin efecto, mayor consecutivo |
| Unitaria | `backend-finca` · `AnotacionesRecibidasEnMemoriaTest` (4) | El doble en memoria cumple el contrato del puerto (`AnotacionesRecibidasContratoTest`) |
| Unitaria | `simulador` · `SincronizarParticionServicioTest` (2) | El caso de uso del celular entrega la partición tal cual y devuelve lo del nodo |
| Unitaria | `simulador` · `SincronizarTest` (4) | La orden `sincronizar` con un puerto falso: códigos de salida 0, 1 y 2 |
| Unitaria | `simulador` · `SimuladorTest` (4), `FabricaDeOrdenesTest` (2) | Uso, `--version`, `--help`, opción desconocida; las órdenes salen de Spring si son beans |
| Arquitectura | `backend-finca` · `ModulosTest` (2) | Ningún módulo toca la parte interna de otro (TEC-05) y están los 15 |
| Integración estrecha | `backend-finca` · `AnotacionesRecibidasJdbcTest` (4) | El adaptador JDBC contra PostgreSQL real cumple el mismo contrato que el doble. **Se salta sin Docker** |
| Integración estrecha | `simulador` · `ClienteIngestaTest` (4) | El adaptador HTTP del celular contra un nodo falso: 200, 422, otro código y sin conexión, sin lanzar |
| Integración amplia | `backend-finca` · `CanalParticionesTest` (4) | **El canal de TX-1** con HTTP y PostgreSQL reales: 200 y guarda, el reenvío no duplica, todo JSON malo (también `[null, …]`) es 422 «rechazado». **Se salta sin Docker** |
| Humo | `backend-finca` · `ArranqueBackendFincaTest` (2) | Arranca contra PostgreSQL 16, Flyway aplica V1 y la salud da `UP`. **Se salta sin Docker** |
| Humo | `servicios-en-linea` · `ServiciosEnLineaTest` (2), `simulador` · `SimuladorAplicacionTest` (2) | Arrancan; salud `UP`; el simulador sin servidor web |

`pruebas-extremo-a-extremo` no tiene pruebas todavía: la corrida real de punta a punta es `scripts/IA_probar-canal.sh` (usa su propio proyecto de compose, `florlogic-canal`, y no toca la base de desarrollo).

## Abrir en IntelliJ IDEA o Eclipse

Es un proyecto Maven multi-módulo normal; los dos IDE lo leen sin complementos extra.

- **IntelliJ IDEA**: *File → Open* y elegir `codigo/pom.xml` → *Open as Project*. Fijar el SDK del
  proyecto en un JDK 27 o superior.
- **Eclipse** (IDE for Java Developers, trae m2e): *File → Import → Maven → Existing Maven Projects*
  y elegir la carpeta `codigo/`. Aparecen el padre y los cinco módulos. JDK 27 o superior en
  *Installed JREs*.
- Para arrancar desde el IDE: `BackendFinca` (con la base de `compose.yaml` arriba),
  `ServiciosEnLinea` y `SimuladorAplicacion`. Las pruebas corren con JUnit desde el mismo IDE.
- Los archivos que generan los IDE (`.idea/`, `*.iml`, `.classpath`, `.project`, `.settings/`) no se
  suben.

## Dónde va TX-1

- **Hecho (19-oct-2026):** `contrato/` Segmento 1 (forma de los mensajes) y Segmento 2 (el canal). Detalle y
  plan en `contrato/espec/ESPEC-contrato-ingesta.md` §8; punto de reentrada en `../.claude/contexto/IA_RETOMAR-AQUI.md`.
- **Después**, en el orden en que Juan los pida: sesión `{id}` y credencial, huella y corrupción, esquema JSON,
  códigos finos y rechazo por anotación (A-58), reglas y marcas, marcas de agua reales, reintentos.

TX-2 necesita P-R01..P-R10; P-R10 (proveedor de nube) bloquea la subida real.
