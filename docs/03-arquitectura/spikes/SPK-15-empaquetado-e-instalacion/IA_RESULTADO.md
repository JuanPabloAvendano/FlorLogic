# `SPK-15` · Empaquetado, instalación y actualización — resultado

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 15-sep-2026
> Estado: **SIN REVISAR** · Chat 5
> Manda sobre esto: `ADR-016`, `ADR-003`, `ADR-029`, `ADR-009` · `CT-06`, `CN-02`, `CN-29`, `CN-37`,
> `CN-15` · `ESC-14`

**Banco:** 2 núcleos, 8 GB, PostgreSQL 16.13. **Parque de 20 empresas, 250.000 eventos cada una
(1.880 MB)**, esquema fiel a `ADR-024`/`ADR-035`/`ADR-027`/`ADR-031`, cadena de **40 migraciones**
con dos destructivas dentro a propósito. **No se fijó lenguaje:** la instalación se midió **en tres**
(Go, Node, Python) y la parte que depende del lenguaje se reporta aparte. Guiones, migraciones y
salidas en `labs/spk-15/`; el detalle largo en `labs/spk-15/salidas/EVIDENCIA-detallada.md`.

---

## Medición

**1 · Instalación en limpio, de cero a `/salud` correcto.** `[!]` **Con PostgreSQL ya en la máquina**
(ver «Qué NO probé» §1).

| fase | go | node | python |
|---|---|---|---|
| verificar el artefacto + comprobar el manifiesto pieza por pieza | 0,019 | 0,008 | 0,015 |
| `initdb` + arrancar + crear la base de la empresa | 0,796 | 0,713 | 0,841 |
| **las 40 migraciones sobre base vacía** | **1,623** | **1,591** | **1,637** |
| paquete de configuración (contrato de `SPK-14`) | 0,014 | 0,014 | 0,015 |
| extraer + preparar el tiempo de ejecución + primer `/salud` | 0,084 | 0,148 | 1,389 |
| **TOTAL** | **2,535 s** | **2,474 s** | **3,897 s** |

**El lenguaje mueve entre el 3 % y el 36 % del total; el piso común es 2,33–2,51 s en los tres.** Las
migraciones son el **64 %** del total en Go y Node, y el 42 % en Python. Artefacto disciplinado: **go 2,73 MB · node 0,12 MB · python 3,00 MB**.

**2 · Migraciones sobre N bases.** Las 20 empresas de V020 a V040, secuencial: **222,77 s**; por base
**9,58–13,55 s**, media 11,14. Segunda pasada sin nada que hacer: **30,08 s = 1,50 s por base**, puro
sobrecoste del migrador (abre una conexión por migración; el SQL puro de una base es 11,17 s de los
13,41 de reloj). Dentro de una base, **el 73 % del SQL es el relleno de una columna** (8.151 ms de 11.169).

**Cortes, con testigo.** Matando el migrador a los 20 s con el parque a medias: 5 bases en V040, 5
intactas en V020, **ninguna a medias**; reanudar, 26,1 s. Matándolo **dentro** de una base, en mitad
del relleno: quedó anotada en v26, **0 filas rellenadas a medias, 0 índices inválidos**; reanudar,
9,42 s hasta v40. Dos migradores a la vez sobre la misma base: uno aplica las 35, el otro **falla en
la primera y se detiene**; estado final correcto — la red la pone la clave primaria del registro
dentro de la misma transacción, no un cerrojo.

| Una migración que revienta a la mitad | Transaccional | **Testigo: sin transacción** (`CREATE INDEX CONCURRENTLY`) |
|---|---|---|
| Residuo en la base | **ninguno** | un índice creado **y uno INVÁLIDO**, ocupando disco |
| Versión anotada | 40 (correcta) | 40 — **la base miente sobre su estado** |
| Reintentar | vuelve a fallar igual, sin acumular | **`relation already exists`: la base queda atascada** |

**3 · Actualizar sin perder nada, con cola e ingesta vivas.** El `ALTER` a solas: **32,8 ms**.

| | ALTER | Escrituras paradas | Escrituras logradas |
|---|---|---|---|
| Sin nadie leyendo | 0,033 s | — | — |
| **Detrás de una consulta de 20 s** | **18,06 s** | **18,04 s** (latencia máx **18.031 ms**) | 1.561, **0 fallos** |
| Lo mismo con `lock_timeout=2s` | 2,04 s, **se rinde** | **2,01 s** | 4.081, 0 fallos |

La cadena entera con ingesta viva tardó **9,64 s** y **no perdió nada**: los 1.788 eventos escritos
durante la ventana están los 1.788, la cola de `ADR-009` pasó de 50 a 1.838 pendientes intacta, y la
mayor parada por cerrojo fue de **0,08 s**. **Lo que se cayó fue la aplicación:** 4.074 escrituras
fallaron y **el escritor murió a los 12,1 s de una ventana de 40 y no se recuperó** — `V021` renombró
una columna y `V036` borró otra. Y **el relleno se escapó**: de las 1.788 filas nuevas solo **683**
quedaron rellenadas; **1.105 se escribieron después de que la pasada de relleno ya había terminado**.

**4 · Volver atrás.** El código solo —parar, cambiar el artefacto, arrancar, primer `/salud`—:
**0,334 s**. El esquema, de V040 a V020, una por una: **0,68 s**, **19 de 20 reversibles**. La que no:
`V036` devuelve la columna **vacía en las 250.000 filas**. Y ni el código ni el esquema se pueden
mover solos:

| | contra esquema V040 | contra esquema V020 |
|---|---|---|
| Código v20 (`campo`, `tiempo_sesion_ms`) | **3 de 4 consultas rompen** | funciona |
| Código v40 (`codigo_campo`, `tallos`, `prioridad`) | funciona | **3 de 3 rompen** |

**5 · Paridad: demostrar que es el mismo artefacto.** Dos construcciones separadas del mismo código:

| | Construcción disciplinada | **Testigo: construcción ingenua** |
|---|---|---|
| go | **digest idéntico** | distinto — y **cambia hasta el tamaño** (5.426.069 vs 5.426.063 B) |
| node | **digest idéntico** | distinto |
| python | **digest idéntico** | distinto |

Hacen falta tres cosas a la vez: `-trimpath -buildvcs=false -ldflags "-s -w -buildid="` en la
compilación, `tar --sort=name --mtime --owner=0 --numeric-owner` y `gzip -n`. El mismo artefacto
(`f074c9d7…`) instalado como «sitio» y como «nube» da **el mismo sha256 del binario desplegado** y
manifiesto correcto en los dos; lo único distinto es lo que responden: `paquete 7` contra `paquete 9`.

**6 · `CN-29` — cuánto puede divergir la instalación que dejó de pagar.** Congelada en v1, v5, v10,
v20, v30 y v39: **las seis llegan a v40**, y su esquema queda **idéntico línea por línea** al de una
instalación nueva (430 líneas normalizadas, misma suma; la comprobación sí detecta un índice puesto a
mano). **La divergencia no la fija la deuda de versiones. La fija aplanar la cadena:**

| Al aplanar `V001..V025` en una línea base | |
|---|---|
| Instalación nueva | llega a v40 |
| Congelada **exactamente** en v25 | se bloquea por deriva de suma (arreglable a mano) |
| **Congeladas en v10 y v20** | **`relation "asignacion" already exists`: no se pueden actualizar nunca** |

**7 · Requisitos reales del nodo, medidos.**

| | |
|---|---|
| En reposo (una empresa, base vacía) | **72 MB** de memoria real · 2 % de dos núcleos |
| Bajo ingesta continua | **99 MB** · **31 %** de dos núcleos · **1.542 ev/s** desde un cliente (p50 0,52 ms) |
| **Durante la actualización de una empresa** | **pico de 519 MB** · 43 % de dos núcleos |
| Disco: nodo recién instalado | 48 MB (42 de datos + 7 del artefacto) + 48 MB de PostgreSQL en el sistema |
| Disco: una empresa de 250.000 eventos | **94 MB en V020 → 166 MB tras actualizar (+77 %)**; con `VACUUM FULL`, 91 MB |
| Corte de luz a media faena | **116 s** antes de aceptar la primera conexión (1,98 GB de WAL por rehacer) |

---

## Lectura

*Se puede tirar entera sin perder la medición.*

**El número que nadie tenía resultó pequeño, y por eso no es el número que importa.** Instalar el
software son **2,5 s**; actualizar veinte empresas, **3,7 minutos**. Lo que cuesta una venta no está
aquí: está en aprovisionar el equipo y PostgreSQL —que **no medí**— y en cargar el heredado, que es de
`SPK-11` y está aparcado. **Antes de meter esta cifra en un precio hay que medir esas dos.**

**«Actualizar sin perder nada» se cumple con el dato y se rompe con la aplicación.** Cero eventos
perdidos, cola intacta, 80 ms de parada por cerrojo — y aun así la finca estuvo **28 de 40 segundos
sin poder escribir**, porque el código viejo seguía hablándole a columnas que la migración acababa de
renombrar y borrar. **El riesgo de la actualización no es el dato: es el orden.** Expandir y contraer
en dos despliegues separados es lo que convierte esos 28 segundos en cero, y el precio es que
`V036` no puede ir en la misma entrega que `V035`. La fuga del relleno dice lo mismo desde otro lado:
**una pasada de relleno no alcanza a lo que se escribe después de ella**, y 1.105 filas lo demuestran.

**`lock_timeout` es la diferencia entre 18 segundos y 2.** Un `ALTER` de 33 ms se convierte en una
parada de 18 s **por una consulta de lectura que ya estaba corriendo** — no por la migración. Con
`lock_timeout` la migración se rinde y la finca sigue escribiendo; sin él, la finca espera a que
termine la consulta más larga que haya abierta. **La elección es qué prefieres que falle.**

**`[!]` La vuelta atrás del esquema es barata y mentirosa.** 0,68 s para deshacer veinte migraciones,
y 250.000 filas vuelven con la columna vacía. **`ADR-016` no dice nada de rollback y `CN-15` pide
restauración en un día o menos: la única red de seguridad real es el respaldo de `SPK-13`.** Lo anoto
como pedía la pregunta y no lo resuelvo. Lo que sí queda medido es que **código y esquema no se pueden
mover por separado** mientras la entrega lleve migraciones destructivas dentro.

**`[!]` `CN-29` no se defiende sola: la defensa es no aplanar.** Treinta y nueve versiones de atraso
se recuperan sin un rasguño y dejan la base **idéntica** a una instalación nueva. Pero el día que la
cadena crezca y se aplanen las primeras, **toda instalación por debajo del corte deja de ser
actualizable para siempre**. El riesgo escrito en `CN-29` es real, pero la palanca es una decisión del
equipo, no la morosidad del cliente: **aplanar fija un suelo de versión, y ese suelo hay que
publicarlo antes de crearlo.**

**La paridad nube/sitio no es una propiedad: es una disciplina de tres banderas.** Sin ellas el mismo
código da un artefacto distinto cada vez —en go cambia hasta el tamaño— y `CT-06` se vuelve
indemostrable. Con ellas, el digest es la prueba. **Y el artefacto ingenuo pesaba el doble** (5,43 MB
contra 2,73): la mitad eran símbolos de depuración que iban a viajar a cada finca.

**El disco sube un 77 % al actualizar y no baja solo.** Una empresa pasa de 94 a 166 MB, y hace falta
un `VACUUM FULL` —que reescribe y bloquea— para recuperar los 75. **Dimensionar el nodo con el tamaño
en reposo es dimensionarlo mal:** hay que dejar sitio para la actualización, no para el estado
estable. Y el pico de memoria de la actualización (519 MB) es **cinco veces** el de la operación
normal (99 MB): **el momento más exigente del nodo es el que ocurre una vez cada versión.**

**PostgreSQL está sosteniendo la mitad de estas conclusiones, y conviene nombrarlo.** Que una
migración que revienta no deje residuo, que dos migradores no se pisen, que el corte a mitad del
relleno no deje filas a medias: **eso es DDL transaccional, y no todos los motores lo tienen.** Si
`SPK-10` se abre y cambia el motor, esta sección se vuelve a medir entera.

---

## Dependencias

*Asumido como contrato, nunca como implementación.*

- **`SPK-09`, lenguaje.** No se fijó ninguno. El contrato que este spike necesita es mínimo: *«un
  proceso que lee su configuración de un archivo indicado por una variable de entorno, se conecta a la
  base y expone un chequeo de salud»*. Los tres lo cumplen; **lo único que cambia es entre el 3 % y el
  36 % del tiempo de instalación** y la huella en reposo (65,8 · 82,3 · 116,4 MB).
- **`SPK-14`, paquete de configuración.** Se trató como contrato: *un archivo, una versión, y el nodo
  la anuncia en su chequeo de salud*. **Hallazgo lateral que le toca a ellos:** la cadena de conexión
  tiene que estar escrita en una forma que acepte **cualquier** tiempo de ejecución — la forma de
  palabras clave de libpq funciona en Go y Python y **no** en Node; la forma URI funciona en los tres.
- **`SPK-13`, respaldo.** Es **la única vuelta atrás real del dato**, y este spike la asume sin
  volver a medirla: sus 2 min de restauración completa son lo que salva el domingo que falle la
  versión nueva.
- **`SPK-12`, ingesta.** Los 1.542 ev/s de §7 son de **un solo cliente con ida y vuelta por fila**, no
  un pico de ingesta: **no son comparables con los 84.618 ev/s de carga masiva de `SPK-13`**.
- **Motor de base de datos.** PostgreSQL, porque `ADR-024` lo propone y `SPK-10` está aparcado. §2 y
  §3 dependen de su **DDL transaccional**; §1, §5 y §6 no.
- **`ADR-009`:** la cola dentro de la misma base → la actualización la arrastra sin trabajo extra, y
  **se verificó que sobrevive**.
- **`ADR-003`:** N bases con discriminador de empresa → el migrador recorre bases, no esquemas.
- **`ADR-029`:** el eje «dato» (`SPK-14`) y el eje «código» (este) se instalan en fases separadas y
  **cada uno lleva su propio número de versión**; el chequeo de salud devuelve los dos.

---

## Qué NO probé

1. **`[!]` El aprovisionamiento del equipo.** Los 2,5 s **no incluyen** instalar el sistema operativo
   ni PostgreSQL (48 MB ya presentes en el banco), ni configurar red, disco o arranque automático.
   **Es casi seguro el grueso del tiempo real de una instalación en finca, y sigue sin medirse.**
2. **`[!]` Contenedores.** El registro público está bloqueado en este entorno: no se construyó ni se
   midió ninguna imagen OCI, así que **la paridad está demostrada sobre un paquete comprimido, no
   sobre una imagen**. Si el empaquetado acaba siendo una imagen, §5 hay que rehacerla.
3. **`[!]` `CT-06`, «la versión nueva llega solo a instalaciones vigentes».** **No se probó ningún
   mecanismo de distribución ni de comprobación de vigencia.** Solo se midió qué pasa con la base
   cuando una instalación se queda atrás, no cómo se le impide recibir la versión.
4. **`[!]` `ESC-14` es un inventario, no una medición.** Hoy **los diez pasos de la instalación, el de
   actualización y los dos de vuelta atrás son línea de comandos**. No hay interfaz de administración
   que probar, así que no hay nada que medir contra `ESC-14` — solo la constancia de que **todo lo
   medido aquí lo hace hoy alguien con una terminal.**
5. **La carga inicial del heredado** (`SPK-11`, aparcado): una instalación real arranca con datos
   dentro, y eso no está en el cronómetro.
6. **Actualizar con dispositivos sincronizando de verdad.** El escritor es un generador de carga de un
   solo hilo; no hubo reintentos de dispositivos ni reingesta por clave de hecho durante la ventana.
7. **Firma del artefacto.** El manifiesto es `sha256` **sin firmar**: prueba integridad, no
   procedencia. Nadie verificó que el paquete venga de quien dice.
8. **El aplanado de §6 es una simulación:** la línea base es un volcado del esquema, no un artefacto
   real del proveedor. El mecanismo de fallo es el mismo, las cifras no son de un caso real.
9. **`pg_upgrade` y el cambio de versión mayor de PostgreSQL.** Todo se midió dentro de 16.13.
10. **Autovacuum después de la actualización.** El `VACUUM FULL` de §7 se lanzó a mano; no se midió
    cuánto tarda el nodo en recuperar ese disco por sí solo, ni qué hace mientras tanto.
11. **Dos migradores a la vez sobre una migración sin transacción.** La red medida en §2 la da la
    transacción; **en las que no pueden ir en transacción no hay red y no se probó qué pasa.**
12. **El nodo real de la finca.** Dos núcleos aquí, `shared_buffers` a 1 GB; los picos de §7 escalan
    con esa configuración y hay que volver a medirlos en el equipo que se venda.
