# `SPK-18` · El armazón del backend en Java — definir cuál

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 23-sep-2026, reescrito el mismo día para que el spike **defina**
> Estado: **SIN REVISAR** · Chat 8
> Manda sobre esto: `ADR-PoC-Spikes.xlsx` (con `ADR-037`) · `DRIVERS_ARQUITECTONICOS.md` y sus
> cuatro `.xlsx` · `IA_CONTEXTO-3-BACKEND.md`

## `[!]` El encargo

**Definir el armazón del backend.** El spike termina con **una recomendación**: el armazón que sale
de aplicar, a lo medido, los criterios de la sección siguiente. Esos criterios se fijan **antes** de
medir.

`ADR-037` fijó Java y dejó escrito lo que falta: *«Esta decisión no elige el armazón sobre el que se
construye el servicio. Esa elección cambia la memoria que consume el nodo casi a la mitad, y no está
medido. Se decide aparte.»* Este spike es ese «aparte».

`[!]` **Esto es distinto de los spikes anteriores**, que tenían prohibido elegir. Aquí se pidió
expresamente que el spike defina. Lo que no cambia: **el ADR lo redacta Juan en el `.xlsx`**, a
partir de la recomendación, y el spike no edita el libro.

`[!]` **`IA_CONTEXTO-3-BACKEND.md` §3, regla 3, está desactualizado:** dice que ningún chat fija el
lenguaje. `ADR-037` ya lo fijó. Manda el ADR. Este spike **no reabre el lenguaje**.

| Candidato | Versión | Por qué esa |
|---|---|---|
| **Spring Boot** | **4.1.x** | La línea con soporte vigente (hasta jul-2027) |
| **Quarkus** | **3.33 LTS** | La de soporte largo vigente (hasta mar-2027). **No** la 3.39 corriente ni la 4 en beta |
| Micronaut *(entra si se gana el lugar)* | 4.10.x | Ver la regla de ascenso abajo |

JDK: **OpenJDK 21**, el de `SPK-17`. PostgreSQL **16**, el de `SPK-15` y `SPK-17`.

**Regla de ascenso de Micronaut.** Su serializador por defecto (Micronaut Serde) **no es Jackson**, y
todo lo que `SPK-17` midió de la huella fue con Jackson. Se le corren solo las mediciones 1 y 2. Si
pasa el eliminatorio `E1` **y** su memoria con carga sale al menos un 20 % por debajo del mejor de
los otros dos, **pasa a candidato completo** y se le corre todo. Si no, queda fuera **con sus cifras
escritas**, no por falta de datos.

## `[!]` Cómo se decide — fijado antes de medir

**Paso 1 · Eliminatorios.** Un candidato que falla uno de estos queda fuera, sin importar el puntaje:

| | Qué tiene que cumplir | Medición |
|---|---|---|
| `E1` | Reproduce `36dea84eb83198c1` y `1fdc4a79dea175cb`, llega a 11 de 12 casos con la forma fijada, y da **0 % de veredicto y 0 % de motivo** en las 1.232 camas **por HTTP**, también con cultura `es-CO` | 1 · 6 |
| `E2` | Lee el heredado a la escala real **con memoria plana** (no crece con el tamaño del archivo) y **se reanuda sin duplicar ni perder filas** | 4a |
| `E3` | Un solo artefacto (`ADR-016`) y dos construcciones con **digest idéntico** (`CT-06`) | 3 · 7 |
| `E4` | Trabajos guardados en la misma base (`ADR-009`), sin doble ejecución después de un `kill -9` | 5 |
| `E5` | Flyway sin residuo con un corte a mitad de las 40 migraciones | 5 |

**Paso 2 · Puntaje, entre los que pasan.** En cada criterio, el mejor se lleva todos los puntos y el
otro cero. **Si la diferencia queda por debajo del umbral, es empate y los dos se los llevan.** El
umbral existe para que un 3 % de ruido no decida un armazón.

| | Criterio | Peso | Umbral de empate | Por qué pesa eso |
|---|---|---|---|---|
| `C1` | **Costo para dos personas:** líneas del esqueleto, dependencias, piezas que no son del armazón oficial, construcción sin internet, tiempo de construcción | **30** | Gana el que sea mejor en al menos 3 de las 5; si no, empate | El equipo son dos personas y ya sostienen la aplicación de captura |
| `C2` | **Memoria del nodo:** RSS con carga (medición 2) | **25** | < 20 % | Es la cifra que `ADR-037` dejó sin medir |
| `C3` | **Carga del heredado:** ritmo de lectura, reanudación y código del trabajo de lote | **20** | < 20 % en ritmo | Con 250 GB, la carga inicial es un trabajo de horas o días (medición 4a) |
| `C4` | **Vida de la versión:** soporte que queda al salir a producción, y si hay una versión mayor encima | **15** | Empate si los dos tienen ≥ 12 meses | Quarkus 4 sale en nov-2026: migrar al poco tiempo de empezar es costo |
| `C5` | **Artefacto y aprovisionamiento** (medición 3) | **10** | < 20 % | Pesa en la instalación, pero `jlink` ya lo acota |
| — | Arranque | **0** | — | Un nodo encendido todo el día. Se mide solo para la columna |

**Desempate.** Si los totales quedan a 10 puntos o menos, gana **el armazón que el equipo ya
conoce**. Por eso, **antes de medir**, Juan y Jerónimo anotan su experiencia con cada uno (ninguna ·
cursos · proyecto real), y se escribe en el resultado.

**Los pesos son una propuesta.** Se pueden cambiar **antes de la primera medición**. Después quedan
congelados, con fecha, para que el resultado no se acomode a lo que salió.

## `[!]` Compuerta 0 · ¿Qué son los 250 GB del Access?

**Un archivo `.accdb` o `.mdb` no puede pasar de 2 GB**: es el límite del propio Access. Así que
«el Access pesa 250 GB» tiene que ser otra cosa, y cada respuesta cambia la medición 4a:

| Si los 250 GB son… | Qué cambia |
|---|---|
| **Muchos archivos** de hasta 2 GB (≥ 125) | Es lo que este spike supone por defecto |
| **Tablas vinculadas** a otro motor (SQL Server, un ODBC) | Los datos no están en Access. Se leen con el driver de ese motor y UCanAccess sobra |
| **Adjuntos u objetos OLE** (fotos, documentos) | Lo que hace falta del histórico probablemente pesa mucho menos. Hay que decidir si los adjuntos migran |
| **Copias de respaldo** en la misma carpeta | Solo cuenta el archivo vigente |

**Antes de medir, Juan confirma:** cuántos archivos son, cuánto pesa cada uno, si hay tablas
vinculadas y si hay adjuntos. Sin eso el spike corre con el supuesto por defecto y lo dice en la
primera línea del resultado.

## Compuerta 1 · ¿Llega Maven Central?

`SPK-17` pudo medir Java a pelo porque `javac`, `jar` y `jlink` vienen en el JDK. **Un armazón no:
sin Maven Central no hay Spring Boot ni Quarkus.** Antes que nada:

`java -version` · `mvn -v` · `mvn -q dependency:get -Dartifact=org.springframework.boot:spring-boot:4.1.1`
· lo mismo con `io.quarkus:quarkus-core` de la 3.33.

**Si está bloqueado, el spike no se corre en ese entorno.** Se deja `labs/spk-18/` listo para una
máquina del equipo: los proyectos, un `docker compose` de PostgreSQL y un solo guion que produzca
las tablas. **Un número que no se midió no se estima**, y aquí la tentación es grande porque hay
benchmarks de blogs para todo. **Ninguna cifra de un blog entra al resultado.**

## El esqueleto común

El mismo esqueleto en cada candidato, **tan pequeño como se pueda pero que toque todo lo que el
armazón pone**. Lo que es Java puro —evaluador de reglas, proyección— se reutiliza de `labs/spk-17/`
sin cambios, para que la diferencia medida sea del armazón y no del código.

| Pieza del esqueleto | Componente del backend | Spring Boot | Quarkus |
|---|---|---|---|
| `POST /sincronizacion` que recibe una sesión y la guarda | Ingreso de sincronización | Spring MVC | Quarkus REST |
| Tabla de hechos con valor en `JSONB` (`ADR-024`) | Datos maestros · Ciclo de producción | Spring Data JPA (Hibernate) | Hibernate ORM con Panache |
| Validación con `reglas.v1.json` en el servidor | Motor de reglas, instancia servidor | evaluador de `labs/spk-17/` | el mismo |
| `GET /consulta/...` de solo lectura | Consulta y tableros · Salida de datos | Spring MVC | Quarkus REST |
| Las 40 migraciones de `labs/spk-17/` | Actualización y migraciones | Flyway | Flyway |
| Un trabajo programado **guardado en la base** (`ADR-009`) | Planificador de tareas | Quartz con almacén JDBC | `quarkus-quartz` con almacén JDBC |
| Emitir y verificar un token firmado | Emisión de credenciales · Identidad | Spring Security + Nimbus JOSE | `smallrye-jwt` |
| Caché de producciones activas | Caché de producciones activas | Spring Cache + Caffeine | Quarkus Cache |
| `/salud` y métricas | Observabilidad | Actuator + Micrometer | SmallRye Health + Micrometer |
| Un Excel y un PDF | Generador de documentos | Apache POI · OpenPDF | los mismos |
| **Carga del heredado por lotes, reanudable** | Lectura del sistema heredado | **Spring Batch** | **`quarkus-jberet`** (Jakarta Batch, de Quarkiverse) |

`[!]` **La deduplicación del ingreso va solo por el identificador técnico** (`ADR-027`), no por clave
de hecho: `SPK-12` está aparcado porque `D1` no está respondida. Este spike **no la inventa**.

Los `.accdb` de prueba son **sintéticos**, generados con Jackcess: `SPK-11` sigue aparcado sin los
archivos del cliente. La forma de las tablas se inventa, y eso se dice en el resultado.

## Lo que se mide

**1 · La huella canónica del JSON a través del armazón.** → `E1`
`SPK-17` reprodujo las huellas con Jackson **2.14**. Los armazones traen otra cosa: **Spring Boot 4
trae Jackson 3** (paquete `tools.jackson`), **Quarkus 3.33 trae Jackson 2** y Micronaut trae
**Serde**. Reproducir las dos huellas y los 12 casos de `SPK-17` con el serializador **que inyecta
el armazón**, primero sin configurar nada y luego con la forma fijada. Reportar **qué hay que
configurar en cada uno**. Y pasar **las 1.232 camas de `SPK-09` por el endpoint HTTP**, no dentro del
proceso: en `SPK-17`, el `1.0` contra `1` de Java cambió el 52,3 % de los motivos. **Veredicto y
motivo por separado.**

**2 · La memoria del nodo.** → `C2`
**RSS, no montón**: `SPK-17` ya mostró que bajar `-Xmx` no baja la huella. Tres momentos: **en
reposo**, **después de ingerir una jornada** y **con el trabajo programado corriendo**. Modo JVM,
mismo JDK, mismas banderas, y además con `-XX:+UseSerialGC`. Referencia: Java a pelo **60,2 MB** en
reposo (`SPK-17`). **Se toma sin la carga del heredado corriendo**: esa se mide aparte, en la 4a.
`[!]` **La imagen nativa es opcional y va con condición:** solo si GraalVM `native-image` es
alcanzable, **y la imagen incluye el lector del heredado y Apache POI.** Si no compila o no funciona
en nativo, **ese es el resultado**, y la cifra nativa no cuenta para `C2`.

**3 · Arranque, artefacto y aprovisionamiento.** → `E3` · `C5`
Tiempo hasta el primer `/salud` correcto, tamaño del artefacto y qué hay que instalar en el nodo.
Referencias (`SPK-17`): primer `/salud` **0,276 s**, `jlink` **35,6 MB** (55,2 MB con el driver),
JRE **193,6 MB** en disco.
`[!]` **El artefacto por defecto de Quarkus es un directorio** (`quarkus-app/`), no un archivo. Medir
también el `uber-jar` y decir cuál de las dos formas cumple `ADR-016`.
`[!]` **La trampa de `jdeps` de `SPK-17`:** la imagen que propone arranca y devuelve respuesta vacía.
Cada imagen `jlink` vale solo si `/salud` **devuelve cuerpo**.

**4a · La carga del heredado a escala.** → `E2` · `C3`
A 250 GB, la lectura del Access **deja de ser una consulta y pasa a ser un trabajo de lote de
horas**: hay que poder cortarlo, reanudarlo y saber cuánto va. Ahí los armazones sí se diferencian:
Spring Batch es un módulo oficial de Spring; en Quarkus la opción equivalente, Jakarta Batch, llega
por una extensión de Quarkiverse (`quarkus-jberet`).

*Tres lectores*, con archivos sintéticos de **100 MB, 500 MB y 2 GB** (el máximo por archivo):

| Lector | Qué hace | Lo que se espera verificar |
|---|---|---|
| UCanAccess, copia en memoria (por defecto) | Copia el archivo entero a HSQLDB en memoria al conectarse | **Probablemente no aguanta 2 GB.** Si revienta, ese es el resultado |
| UCanAccess, copia en disco (`mirrorFolder`) | La misma copia, en disco | Cuánto disco temporal pide por archivo, y si la memoria queda plana |
| **Jackcess directo** | Recorre las filas con un cursor, sin copia | Si es el único que mantiene la memoria plana a cualquier tamaño |

De cada lector, en cada armazón: **RSS pico**, **ritmo** (MB leídos por segundo, con las filas ya
escritas en PostgreSQL), **disco temporal** y si el RSS **vuelve** al de reposo al terminar.

*A escala.* Un lote de **10 archivos de 2 GB** (20 GB) para comprobar que el ritmo se mantiene. Si
el ritmo del archivo 10 queda a menos de un 10 % del ritmo del archivo 1, se entrega la
**proyección a 250 GB**, marcada como proyección y no como medición. Si no se mantiene, no se
proyecta.

*Reanudación.* `kill -9` a mitad del archivo 5 de 10. Al reanudar: ¿sigue desde el último punto
confirmado? ¿Sin filas duplicadas ni perdidas? (Se cuentan contra el origen.) ¿Dónde guarda el
armazón el estado del lote? Tiene que ser la misma base (`ADR-009`).

*Dónde corre.* Dos formas:

- **Forma A · dentro del backend encendido.** Con 250 GB, la copia en memoria queda descartada de
  entrada. Solo cuenta con copia en disco o Jackcess.
- **Forma B · orden aparte, desde el mismo artefacto.** El mismo JAR arrancado **sin servidor web**,
  que lee, escribe en PostgreSQL por el mismo esquema y termina. `ADR-016` pide un solo artefacto:
  **no vale un segundo programa**. Spring Boot con `spring.main.web-application-type=none`; Quarkus
  en modo orden (`@QuarkusMain`, o la extensión de Picocli). Medir si el backend encendido se queda
  en su RSS de la medición 2 mientras la orden corre al lado.

`[!]` **La recomendación de la forma sí entra en el resultado**, porque `E2` depende de ella. Si sale
la B, el componente «Lectura del sistema heredado» saldría del contenedor del backend en el C4. Eso
**no se edita**: se lista como cambio —texto de hoy, texto propuesto, dónde— y lo aplica Juan.

*El transporte, que no se mide aquí pero manda.* Los archivos viven en una carpeta compartida del
equipo Windows. Solo por aritmética, mover 250 GB toma **≈ 5,6 h a 100 Mb/s** y **≈ 33 min a
1 Gb/s**, antes de leer una sola fila. Eso es un piso, no una medición.

**4b · Documentos.** POI y OpenPDF dentro del armazón. Un Excel del tamaño de `ESC-51` contra sus
**30 s**. Si Quarkus necesita una extensión de Quarkiverse para que funcione, se dice cuál.

**5 · El planificador y el migrador de verdad.** → `E4` · `E5`
Los puntos 2 y 6 de «Qué NO probé» de `SPK-17`, ahora alcanzables:
- **Quartz con almacén JDBC.** `kill -9` a mitad de un trabajo y rearranque: ¿se recupera? ¿corre
  dos veces? ¿las tablas viven en la misma base?
- **Flyway contra las 40 migraciones.** Corte a mitad de cadena, dos migradores a la vez, reanudar.
  `SPK-17` midió el sustrato JDBC; esto mide **la herramienta**: su tabla de historial, su cerrojo y
  su `repair`. Y el hallazgo de `SPK-17` —matar el cliente **no** mata la sentencia en PostgreSQL—:
  ¿Flyway lo resuelve o lo hereda?
- ¿Las migraciones corren **al arrancar** o como **orden aparte**? `SPK-15` mostró que la aplicación
  vieja muere durante la actualización. Decir qué permite cada armazón.

**6 · Configuración de afuera y la cultura del nodo.** → `E1`
- `[!]` **La cadena de conexión** (`SPK-17` §7): `pgjdbc` solo acepta `jdbc:postgresql://`. ¿Cada
  armazón deja armarla desde las partes —host, puerto, base— con variables de entorno?
- `[!]` **La batería de la medición 1 con cultura `es-CO`** (`LANG=es_CO.UTF-8`,
  `-Duser.language=es -Duser.country=CO`). En C# la coma decimal cambió el 36,6 % de los motivos. En
  Java `String.format` también usa la cultura por defecto. Es un fallo que no da error.

**7 · La paridad del artefacto.** → `E3`
Dos construcciones con Maven del mismo código: ¿digest idéntico? Maven tiene
`project.build.outputTimestamp`. Hay que ver si el reempaquetado de Spring Boot y el de Quarkus lo
respetan. `SPK-17` tuvo que reescribir el zip a mano.

**8 · Lo que cuesta a dos personas.** → `C1`
Líneas del esqueleto (sin contar el código Java reutilizado) · dependencias directas y transitivas
y peso del `~/.m2` · piezas que no son del armazón oficial (Quarkiverse cuenta aquí) · ¿construye
**sin internet** después de la primera vez (`mvn -o`)? · tiempo de construcción en limpio e
incremental. Más una frase honesta sobre el ciclo de trabajo diario (modo desarrollo de Quarkus
contra `devtools` de Spring).

## Qué NO se hace aquí

- **No se reabre el lenguaje.** `ADR-037` lo cerró.
- **No se escribe ADR** y **no se editan** los cuatro `.xlsx` de drivers ni el libro de ADR.
- **No se mide rendimiento de ingesta.** `SPK-12` ya mostró que el riesgo de esa pieza es de
  corrección, no de velocidad.
- **No se define la clave de hecho.** Depende de `D1`.
- **No se miden cifrado ni respaldo** (`SPK-13`), ni la Custodia de llaves con Vault u OpenBao.
- **No se mide la lectura por la carpeta compartida.** El archivo se lee en disco local. Montar la
  carpeta, los permisos y el archivo de bloqueo `.laccdb` quedan para cuando se retome `SPK-11` con
  los archivos del cliente. Correr la forma B **en el equipo Windows**, al lado de los archivos,
  queda anotada como opción y no se mide.
- **No se decide qué parte del histórico migra.** El componente dice que la lectura «lo filtra»:
  cuánto de los 250 GB termina en PostgreSQL es una decisión de negocio, no de este spike.
- **No se mide Spring Modulith.** Queda anotado para después: verifica los límites entre
  componentes y guarda sus eventos en la misma base, cerca de `ADR-009`.
- **No se mide arranque en frío tipo nube ni contenedores con límite.** Es la cancha donde Quarkus y
  Micronaut brillan en los blogs, y **no es la de este sistema**.
- **No se toca el modelo C4.** La lista de componentes salió de la página «Nivel 3 — Backend de la
  finca (copia con cambios)» del `.drawio` del 22-sep, que Juan todavía está revisando. Si contradice
  el libro de ADR, manda el ADR.
- **No se reabre la tecnología del dispositivo de captura.** `ADR-008` la aplaza a propósito.

## Entregable

`IA_RESULTADO.md` en esta carpeta, cabecera de cuatro líneas, estado `SIN REVISAR`, con la
estructura de la zona **más un bloque**:

1. **Medición** — con el comando para repetir cada cifra.
2. **Recomendación** — la tabla de eliminatorios (pasa / no pasa, por candidato), la tabla de
   puntaje (`C1`–`C5` con los umbrales aplicados), el total, el desempate si hizo falta, **el
   armazón que resulta**, la forma de carga del heredado (A o B), y **qué tendría que pasar para que
   la recomendación cambie**. Sale de aplicar los criterios, no de la Lectura.
3. **Lectura** — lo interpretado. Se puede tirar entera sin perder la Medición ni la Recomendación.
4. **Dependencias** — lo que se asumió de otros componentes, como contrato.
5. **Qué NO probé.**

Si hay que cambiar el C4 o algún `.xlsx`, se lista el cambio exacto y lo aplica una persona.

Código y datos a `labs/spk-18/`, **fuera del repo de documentación**. Si el registro pasa de dos
páginas, se parte: la evidencia larga va a `labs/`. Y **una línea** al final de `IA_BITACORA.md`.

## Dependencias que se toman como contrato

- **`SPK-17`:** el evaluador de reglas en Java, la cadena reconstruida de 40 migraciones, los 12
  casos de huella y la fila «Java a pelo» como piso.
- **`SPK-14`:** las huellas selladas `36dea84eb83198c1` y `1fdc4a79dea175cb`, desde su propio dato.
- **`SPK-09`:** las 1.232 camas y `reglas.v1.json`. El oráculo sigue siendo `app-captura/src/reglas.ts`.
- **`SPK-15`:** la estructura de fases de instalación y la disciplina de paridad.

## Por qué este spike

`ADR-037` cerró el lenguaje y dejó una sola pregunta de tecnología bloqueando el arranque del
backend: el armazón. Los 20 componentes se escriben **sobre** esa elección, y cambiarla a mitad de
camino es reescribir el cableado de cada uno. La diferencia de memoria que el ADR le atribuye puede
ser real, o puede ser una cifra de imagen nativa que este backend no puede usar. Y los 250 GB del
heredado convierten la carga inicial en el trabajo más pesado que va a correr el nodo. Es justo
donde un armazón trae herramientas de lote y el otro las toma prestadas.
