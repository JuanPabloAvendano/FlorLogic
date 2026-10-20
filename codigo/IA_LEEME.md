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
lo aceptado (Java 27 en vez de 21, entre otras) están en `IA_TECNOLOGIAS-vigentes.md`.

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
| `contrato/` | La forma del cable celular ↔ nodo de finca: DTO como records, códigos, cabeceras y la barrera de lectura (`LectorDto` estricto y tolerante, nunca lanza). Huella y esquema: después. ESPEC, diagramas y plan en `contrato/ESPEC-contrato-ingesta.md` y `contrato/diagramas/` | Jackson 3 |
| `backend-finca/` | Monolito modular: los 15 módulos de §3 de `IA_TECNOLOGIAS`, cada uno con su `package-info` | Spring Boot 4.1.1 · Modulith 2.1.1 · JDBC · Flyway · PostgreSQL |
| `servicios-en-linea/` | Recepción de respaldos, telemetría y panel (TX-2) | Spring Boot 4.1.1 · puerto 8090 |
| `simulador/` | El celular simulado, sin órdenes todavía | Spring Boot 4.1.1 sin web · picocli · SQLite |
| `pruebas-extremo-a-extremo/` | Vacío; aquí van las pruebas del §10 de cada TX | Testcontainers |
| `despliegue/` | `compose.yaml` del nodo, hoy solo con la base | Docker Compose |

Todo lo ejecutable es Spring Boot. `contrato` es una biblioteca: no arranca sola, la usan el backend y
el simulador.

## Dentro de cada módulo del backend

Primero se corta por módulo y después por capa. Cada uno de los 15 módulos tiene estos cinco
paquetes, por ahora vacíos (solo `package-info.java`):

```
<modulo>/                        API pública: lo único que otros módulos pueden usar
<modulo>/dominio/                entidades y reglas, Java puro: sin Spring, sin SQL, sin contrato
<modulo>/servicios/casosuso/     un servicio por caso de uso; abre la transacción
<modulo>/servicios/puertos/      interfaces de salida que el caso de uso necesita (solo las que existan)
<modulo>/infraestructura/entrada/  controladores web, oyentes y trabajos
<modulo>/infraestructura/salida/   implementan los puertos con JDBC, HTTP o archivos
```

Las dependencias apuntan hacia adentro: `infraestructura` → `servicios` → `dominio`. Modulith ya
trata estos subpaquetes como internos del módulo; la regla hacia adentro **todavía no se verifica**
(falta jMolecules + ArchUnit en `ModulosTest`).

## Pruebas

| Clase | Qué comprueba |
|---|---|
| `backend-finca` · `ModulosTest` | Ningún módulo toca la parte interna de otro (TEC-05) y están los 15 |
| `backend-finca` · `ArranqueBackendFincaTest` | Arranca contra PostgreSQL 16 (Testcontainers), Flyway aplica V1 y la salud da `UP`. **Se salta si no hay Docker** |
| `servicios-en-linea` · `ServiciosEnLineaTest` | Arranca, la salud da `UP`, hilos virtuales encendidos |
| `contrato` · `DtoJsonTest` (14) | La petición y la respuesta doradas se leen y se escriben igual; null ≡ ausente; enums tolerantes; incoherencias |
| `contrato` · `LectorDtoTest` (10) | La barrera de lectura nunca lanza; el estricto rechaza campos desconocidos y el tolerante los ignora |
| `backend-finca` · `CanalParticionesTest` | **El canal de TX-1** con HTTP y PostgreSQL reales: 200 y guarda, el reenvío no duplica, todo JSON malo es 422 «rechazado». **Se salta si no hay Docker** |
| `simulador` · `ClienteIngestaTest` | El cliente del celular contra un nodo falso: 200, 422, otro código y sin conexión, sin lanzar |
| `simulador` · `SimuladorTest` | Uso, `--version`, `--help` y opción desconocida, sin Spring |
| `simulador` · `FabricaDeOrdenesTest` | Las órdenes salen de Spring si son beans; si no, las crea picocli |
| `simulador` · `SimuladorAplicacionTest` | Spring arranca sin servidor web y la orden termina en 0 |

`pruebas-extremo-a-extremo` no tiene pruebas todavía: la corrida real de punta a punta es `scripts/IA_probar-canal.sh`.

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
  plan en `contrato/ESPEC-contrato-ingesta.md` §8; punto de reentrada en `IA_RETOMAR-AQUI.md`.
- **Después**, en el orden en que Juan los pida: sesión `{id}` y credencial, huella y corrupción, esquema JSON,
  códigos finos y rechazo por anotación (A-58), reglas y marcas, marcas de agua reales, reintentos.

TX-2 necesita P-R01..P-R10; P-R10 (proveedor de nube) bloquea la subida real.
