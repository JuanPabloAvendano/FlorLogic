# `SPK-17` · Java y C# — resultado

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 15-sep-2026
> Estado: **SIN REVISAR** · Chat 7
> Manda sobre esto: `ADR-PoC-Spikes.xlsx` · `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx` ·
> `IA_CONTEXTO-3-BACKEND.md`

**Banco:** 2 núcleos, 8 GB, Ubuntu 24.04, PostgreSQL **16.13** (la de `SPK-15`), OpenJDK **21.0.10**,
.NET SDK **8.0.131**, Node 22.22.2, CPython 3.11.15, Go 1.24.7. Código y datos en `labs/spk-17/`;
las tablas completas en `labs/spk-17/salidas/EVIDENCIA-detallada.md`.
**Este spike no elige lenguaje y no escribe ADR.**

---

## Medición

**0 · La compuerta: esta vez sí hubo cadena de herramientas — a medias.** `java`, `javac`, `mvn` y
`gradle` ya estaban. `dotnet` no; se instaló del archivo de Ubuntu (`apt-get install dotnet-sdk-8.0`
→ 8.0.131). Jackson **2.14.0** y el driver JDBC **42.7.2** salieron de apt. **Maven Central y NuGet
están bloqueados** (403 en el CONNECT), y con ellos nueve espejos y CDN más. Se quedaron sin medir:
**Npgsql · EF Core · DbUp · Flyway · Liquibase · GraalVM `native-image` · Blazor WASM · TeaVM ·
CheerpJ**. Nada de eso se estima.

**1 · La huella canónica del JSON. Java y C# reproducen el veredicto sellado de `labs/spk-14`:
`36dea84eb83198c1` para v1 y `1fdc4a79dea175cb` para v2..v6, exactos.** Con la forma fijada —claves
ordenadas, sin espacios, no-ASCII crudo, texto original del número— **11 de 12 casos dan la misma
huella en los cuatro lenguajes; con el serializador por defecto, 6 de 12.** Lo que hay que fijar:

| | qué hace de más si no se le dice nada |
|---|---|
| **Jackson** | conserva el orden del fuente. No escapa nada de más |
| **`System.Text.Json`** | conserva el orden **y escapa lo no-ASCII**: `"sección"` → `"sección"`. También escapa `<`, `>`, `&`, `'` |
| **los cuatro** | `118.0` sobrevive en Python, Java y C#; **en JavaScript sale `118`** |

`[!]` **El caso `K07` no lo cierra ningún lenguaje: JavaScript no distingue `118.0` de `118`.** Si
un valor entero llega escrito con decimal, la huella se parte entre Node y los otros tres. **El
hallazgo es sobre el contrato, no sobre el lenguaje**, y es material directo para `AB-01`.

**2 · El camino A2: NO SE MIDIÓ.** Ni en Java ni en C#. `dotnet workload install wasm-tools` y
`dotnet restore` de un `blazorwasm` fallan contra `api.nuget.org`; TeaVM vive en Maven Central y
CheerpJ en un CDN, los dos rechazados. Se revisaron las dos fuentes que sí responden
(`registry.npmjs.org`, `pypi.org`) y ninguna trae un compilador de Java a wasm ni el runtime wasm
de .NET. **El rango de 4.000× de `SPK-09` sigue abierto, y ahora por los cinco candidatos.**

**3 · La batería de 1.232 camas contra `app-captura/src/reglas.ts`.**

| motor | **veredicto** | bloqueo | **motivo** |
|---|---|---|---|
| Java · Jackson · número formateado a la forma de JS | **0 %** | 0 % | **0 %** |
| Java · número pegado tal cual (`String.valueOf(double)`) | **0 %** | 0 % | **52,27 %** (644) |
| C# · `System.Text.Json` · forma de JS + cultura invariante | **0 %** | 0 % | **0 %** |
| C# · formato natural, cultura `en-US` | **0 %** | 0 % | **0,33 %** (4) |
| C# · formato natural, cultura **`es-CO`** | **0 %** | 0 % | **36,61 %** (451) |

**El veredicto es 0 % en los cinco casos: `ESC-57` se sigue cumpliendo, ahora en siete caminos.**
El motivo es otra cosa. En Java, `1.0` en vez de `1` cambia **más de la mitad** de los mensajes. En
C# la coma decimal de `es-CO` —la cultura del nodo de una finca colombiana— cambia **el 36,6 %**.
Los dos se van a 0 % con un helper de seis líneas y `InvariantCulture`.

`[!]` **Y hay una divergencia que no es de formato.** Seis camas dan una razón que cae justo en la
mitad. **node, Java y Go redondean hacia arriba; Python y C# redondean al par.** En 4 de esas 6
camas los dos bandos dan **números distintos** (`12.13` contra `12.12`), y ese número va dentro del
texto que lee la persona en el campo.

`[!]` **Esto corrige una lectura de `SPK-09`:** de sus 24 camas de divergencia de motivo Python↔JS,
**21 son formato, pero 3 —`C0091`, `C1190`, `C1225`— son un número distinto por el modo de
redondeo.** No se arreglan sacando el texto al catálogo.

**Tiempo por cama.** Calientes (200.000 vueltas): **Java 0,27–0,28 µs · C# 0,19–0,25 µs** — al nivel
de los 0,20 µs de Go. Fríos, con las 2.000 vueltas que calentó `SPK-09`: **Java 0,95–1,13 µs · C#
1,39–1,58 µs**, los dos **más lentos que los 0,43 µs de node**. C# tarda ≈6× en llegar a su
velocidad; Java, ≈3,4×. Sigue siendo tres órdenes por debajo de lo perceptible.

**4 · Instalación, artefacto, memoria.** `[!]` **`labs/spk-15/` no existe** en `Claude outputs/labs/`,
así que la cadena de 40 migraciones se **reconstruyó** y **los totales no se comparan con los
2,474 / 2,535 / 3,897 s de `SPK-15`**. Para tener una comparación válida se volvieron a medir node,
python y go **en este banco**, con la misma cadena:

| mediana de 5 | java | csharp | node | python | go |
|---|---|---|---|---|---|
| extraer + preparar runtime + primer `/salud` | **0,276 s** | **0,080 s** | 0,134 s | 0,128 s | 0,058 s |
| memoria en reposo | **60,2 MB** | **38,9 MB** | 67,3 MB | 35,9 MB | 7,3 MB |

**Java es el más lento en llegar al primer `/salud` de los cinco, y C# el segundo más rápido.**
Ninguno llega a un tercio de segundo. En memoria, los dos caen **dentro** del rango que ya había
(`SPK-15`: 65,8–116,4 MB). `[!]` La fase de migraciones de csharp y go la corre `psql` —no hay
driver alcanzable— así que **su total de instalación no es una cifra del lenguaje.**

`[!]` **Bajar el montón no baja la huella:** de `-Xmx64m` a `-Xmx32m` con `SerialGC` el RSS de la
JVM no se mueve (62,7 → 63,6 → 62,8 MB); en C#, limitar el montón tampoco (45,9 → 45,7 MB).

**Artefacto:**

| | dependiente del runtime | autocontenido |
|---|---|---|
| **java** | JAR + driver **0,95 MB** (solo código, 0,005 MB) | imagen `jlink` **35,6 MB** |
| **csharp** | `publish` **0,039 MB** | `self-contained` **28,4 MB** · un solo archivo **26,1 MB** |

Referencia `SPK-15`: go 2,73 · node 0,12 · python 3,00 MB. **Sin medir: `PublishTrimmed`,
`PublishAot` y `native-image` de GraalVM** — justo las tres que bajarían las cifras autocontenidas.

`[!]` **`dotnet publish -p:PublishTrimmed=true` y `-p:PublishAot=true` NO fallan cuando falta el
paquete: terminan con éxito y producen, en silencio, el artefacto sin recortar**, byte a byte igual
al `self-contained`. Solo se nota comparando tamaños.

`[!]` **La imagen `jlink` que propone `jdeps` no funciona.** `jdeps` sobre las clases da 42,6 MB;
esa imagen arranca, abre el puerto y **devuelve una respuesta vacía** —al driver JDBC le falta
`java.lang.management`. Corriendo `jdeps` también sobre el jar del driver, la imagen correcta pesa
55,2 MB y sí funciona. **13 MB y un fallo mudo de diferencia.**

**`[!]` El aprovisionamiento, que es lo que `SPK-15` dejó fuera del cronómetro:**

| | qué hay que instalar en el nodo antes | descarga | en disco |
|---|---|---|---|
| **java** | `openjdk-21-jre-headless` | **44,4 MB** | **193,6 MB** |
| **csharp** | `dotnet-runtime-8.0` | **22,8 MB** | **66,6 MB** |
| go | nada (binario estático) | 0 | 0 |

Las dos tienen salida: `jlink` (**35,6 MB**, y viene en el JDK, sin red) y `self-contained`
(**28,4 MB**). Con ellas el nodo **no necesita ni JRE ni .NET**.

**5 · Las tres banderas de paridad. Los dos dan digest idéntico con disciplina y distinto sin ella.**
En Java la fecha va dentro del JAR entrada por entrada y `jar` no tiene bandera para fijarla: hay
que reescribir el zip con fecha constante y orden alfabético. En C# `Deterministic` **ya viene en
`true` y no basta** — el PDB lleva un identificador nuevo en cada compilación y la ruta absoluta
entra en el ensamblado; hacen falta `DebugType=none` y `PathMap`. **`CT-06` es demostrable en los
dos, al mismo precio que en los otros tres.**

**6 · El migrador.** **Flyway, Liquibase, EF Core y DbUp no se midieron.** Lo que se midió es el
sustrato en JDBC con la disciplina que describe `SPK-15` —una conexión por migración, registro de
versión dentro de la transacción—: corte a mitad de cadena → **0 residuo, 0 índices inválidos,
reanuda en 0,57 s hasta v40**; dos migradores a la vez → uno aplica las 40, **el otro se detiene en
la primera colisión**, estado final correcto y sin duplicadas; escalonados no chocan; una migración
que revienta **no anota su versión y deshace su DDL**.

`[!]` **Hallazgo nuevo, que no está en `SPK-15`: matar el migrador no lo mata.** Con `kill -9` al
proceso cliente, el backend de PostgreSQL **termina la sentencia y hace `commit`** —el índice no
existía a los 0,9 s y existía a los 9 s. Lo que aborta de verdad es matar el backend. Y ahí se
reproduce lo de `SPK-15`: transaccional → sin residuo y se reintenta limpio; `CREATE INDEX
CONCURRENTLY` → **1 índice inválido ocupando disco y `relation "ixc" already exists` al reintentar**.

**7 · La cadena de conexión.** `SPK-15` encontró que la forma de palabras clave de libpq funciona en
Go y Python y no en Node. **Con Java, el resultado es peor de lo que parecía:**

| forma | psycopg3 | node-pg | **pgjdbc** |
|---|---|---|---|
| URI `postgresql://…` | **sí** | **sí** | **no** — «No suitable driver found» |
| libpq `host=… port=… dbname=…` | **sí** | no | **no** — «No suitable driver found» |
| `jdbc:postgresql://…` | — | — | **sí** |

**`pgjdbc` no acepta ninguna de las dos formas portables: exige su propio prefijo `jdbc:`.**
`jdbc:postgresql://` + palabras clave tampoco («Unable to parse URL»). **Npgsql: no medido.**

**8 · Lo que cuesta a dos personas.** El mismo motor de reglas son **193 líneas en Java y 191 en C#**
(Go 253, TypeScript de referencia 164). Compilar: **0,92 s** `javac`, **1,42 s** `dotnet build`. La
cadena en disco: **JDK 286 MB · .NET SDK 479 MB** (node, 295 MB). Sin internet, Java se construye
con `javac`+`jar`+`jlink`, que vienen en el JDK; **`dotnet build` sale a `api.nuget.org` y falla
aunque el proyecto no tenga ni una dependencia externa** — hace falta un `NuGet.config` que borre
las fuentes. Los dos son lenguajes con tipos, recolector y una biblioteca estándar que ya trae
servidor HTTP y JSON: no hicieron falta dependencias externas para nada de este spike salvo el
driver de base de datos.

---

## Lectura

*Se puede tirar entera sin perder la medición.*

**Los dos dejan de estar descartados por ausencia de datos, y ninguno aparece como ganador ni como
descarte.** Todo lo que se pudo medir los pone dentro del rango de los tres ya medidos: el veredicto
de las reglas es 0 % en los dos, el tiempo por cama caliente es el de Go, la memoria en reposo cae
dentro de 65,8–116,4 MB, el primer `/salud` no llega a un tercio de segundo y la paridad de `CT-06`
se demuestra igual. **Lo que este spike cambió no es el ranking de lenguajes: es lo que hay que
escribir en el contrato.**

**El hallazgo que más lejos llega no es de Java ni de C#: es del redondeo.** Que `Math.round` parta
los cinco candidatos en dos bandos —arriba en node/Java/Go, al par en Python/C#— significa que
**«implementaciones distintas dan el mismo veredicto» es una propiedad del cuidado, no del
lenguaje**, exactamente como avisó `SPK-09`, pero con un mecanismo nuevo: no basta con sacar los
textos al catálogo. **Hay que fijar el modo de redondeo en el mismo sitio donde se fije la forma
canónica.** `AB-01` es hoy un poco más grande de lo que decía: forma canónica **más** redondeo
**más** cómo se escribe un entero que llegó con decimal.

**Los dos costos reales de estos dos candidatos no son de rendimiento, son de aprovisionamiento.**
Java pide **193,6 MB** en el nodo y C# **66,6 MB**, contra los 0 de Go, y `SPK-15` dejó esa columna
en blanco para los tres que midió. Se puede evitar —`jlink` y `self-contained` llevan el runtime
dentro por 35,6 y 28,4 MB— pero entonces el artefacto pasa de menos de 1 MB a decenas, y **las tres
técnicas que lo bajarían (recorte, AOT, `native-image`) son justo las que este entorno no dejó
medir.** Quien quiera decidir por tamaño de artefacto necesita esa medición, y hoy no existe.

**Dos trampas que muerden en silencio, y las dos son del lado de C#/Java, no de los otros tres.** La
cultura `es-CO` cambiando el 36,6 % de los mensajes y una bandera de compilación que no hace nada
sin avisar son el mismo tipo de fallo: **nadie ve un error, simplemente el resultado es otro.** Es
la misma forma del agujero de `SPK-16` con la RLS. Si el backend acaba en uno de estos dos, la
prueba de paridad de motivos y una comprobación del tamaño del artefacto tienen que estar
automatizadas, no ser una disciplina.

**Y una corrección práctica para el día del despliegue:** con Java en la mesa, **no existe una sola
cadena de conexión que sirva para los cuatro runtimes.** `SPK-15` concluyó que la forma URI
funcionaba en los tres; `pgjdbc` no la acepta. La configuración tiene que guardar las partes —host,
puerto, base, usuario— y que cada nodo arme su forma, o guardar la cadena con el prefijo que toque.

---

## Dependencias

- **`SPK-14`, paquete de configuración.** Se tomó como contrato su veredicto sellado y su forma de
  huella (`sha256` de la serialización canónica, 16 hex). Las huellas `36dea84eb83198c1` y
  `1fdc4a79dea175cb` se reprodujeron **desde su propio dato**, no se recalcularon.
- **`SPK-09`, motor de reglas.** Se reutilizaron sus 1.232 camas, su catálogo `reglas.v1.json` y su
  `comparar.py` sin tocarlos, y su oráculo sigue siendo `app-captura/src/reglas.ts`.
- **`SPK-15`, empaquetado.** Se tomó su **estructura de fases** y su disciplina de paridad como
  contrato. Sus **cifras** no se reutilizaron como base de comparación: su laboratorio no está.
- **`ADR-024`/`035`/`027`/`031`** dieron la forma del esquema de las 40 migraciones reconstruidas.
- **`ADR-008`** se respetó: nada se midió sobre el dispositivo de captura ni sobre un navegador.
- **Motor de base de datos:** PostgreSQL, porque `ADR-024` lo propone y `SPK-10` está aparcado. Las
  garantías de §6 dependen de su DDL transaccional, no del lenguaje ni del migrador.

---

## Qué NO probé

1. **`[!]` El camino A2, en los dos lenguajes.** Blazor WebAssembly, .NET wasm AOT, TeaVM y CheerpJ:
   **cero mediciones**, por red. Es la medición que el encargo ponía en segundo lugar y **la que
   habría convertido A2 en camino real o lo habría matado**. Sigue sin decidirse.
2. **`[!]` Los cuatro migradores de verdad** —Flyway, Liquibase, EF Core, DbUp—. Lo medido es el
   sustrato JDBC, que acota **qué parte de la garantía la pone PostgreSQL**; el comportamiento
   propio de cada herramienta (su tabla de historial, su cerrojo, su `baseline`, su `repair`)
   **no se tocó**, y es de la herramienta, como decía el encargo.
3. **`[!]` Npgsql.** No se probó ni la conexión, ni las migraciones, ni el rendimiento del driver.
   **La columna de base de datos de C# está vacía.**
4. **`[!]` `PublishTrimmed`, `PublishAot` y `native-image` de GraalVM.** Las tres cifras que decidirían
   si el artefacto autocontenido de estos lenguajes es de 28 MB o de 3. **No se estiman.**
5. **`[!]` La cadena de 40 migraciones es una reconstrucción**, no la de `SPK-15`. Los totales de
   instalación de este spike **no son comparables con los suyos**; sí lo son entre los cinco
   lenguajes medidos aquí, que corrieron la misma cadena en el mismo banco.
6. **Maven y Gradle no se usaron.** Todo se compiló con `javac` directo porque Maven Central está
   bloqueado. **No se midió qué cuesta una construcción real con Maven**, que es como se haría.
7. **Ni un navegador ni un dispositivo de campo**, igual que `SPK-09`. Todo en x86 en la nube.
8. **El nodo real de la finca.** Dos núcleos, `shared_buffers` a 256 MB. La memoria en reposo y los
   tiempos de arranque escalan con eso y hay que volver a medirlos en el equipo que se venda.
9. **La actualización en caliente, la vuelta atrás y `CN-29`** —§3, §4 y §6 de `SPK-15`— no se
   repitieron en estos lenguajes. Nada indica que dependan del lenguaje, pero no está medido.
10. **Cifrado y respaldo** (`SPK-13`) e **ingesta** (`SPK-12`): fuera por encargo, no por falta de
    medios.
11. **El motor de reglas de este spike no es el del backend.** Son implementaciones de laboratorio
    para la batería, escritas desde el catálogo. No prejuzgan cómo se escribiría el componente real.
12. **La divergencia entre implementaciones está subestimada, igual que en `SPK-09`:** las escribió
    el mismo agente en la misma sesión, a partir del mismo catálogo.
