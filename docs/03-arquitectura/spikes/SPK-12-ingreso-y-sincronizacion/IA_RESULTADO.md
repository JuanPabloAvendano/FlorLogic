# `SPK-12` · Ingreso y sincronización — resultado

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 15-sep-2026 · Chat 2 de 3 (Nivel 3, Backend de la finca)
> Estado: **APLAZADO 15-sep-2026 · medido, pero con demasiadas contradicciones abiertas
> para decidir nada. Se retoma cuando se cierren los bloqueos del §0**
> Manda sobre esto: `ADR-PoC-Spikes.xlsx` (`ADR-025`…`ADR-028`, `ADR-031`, `ADR-035`, `ADR-014`) ·
> `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx` (`RF-003`, `RF-022`)

---

## §0 · Para retomar — leer esto primero

**Por qué está aparcado.** Lo medido sirve y no depende de nada que esté abierto (ver «lo que no hay
que volver a medir»). Lo que no se puede hacer todavía es **decidir**: la clave de hecho de
`ADR-027` incluye «la jornada», y qué es la jornada no está definido. Todo lo demás cuelga de ahí.

### Los bloqueos, en el orden en que se desbloquean

**1 · Del cliente. Es la raíz: sin esto no se decide nada aquí.**

| | Qué falta | Por qué bloquea |
|---|---|---|
| `D1` | **Qué es «la jornada»** — ¿el día natural, el turno, la visita? | Va **dentro de la clave de hecho** (`ADR-027`). Sin ella no hay clave de hecho; sin clave de hecho no hay idempotencia, ni choque, ni «gana el más reciente». **Todo lo medido descansa sobre una definición ausente** |
| `BR-N4` | La **política** del choque (el mecanismo ya está medido) | M5 mide que el desempate es estable, no qué debería ganar |
| `ADR-025` | El **umbral de antigüedad** a partir del cual se estorba o se bloquea | Cambia qué tan larga puede ser de verdad la ventana de `RF-003` |

**2 · Del equipo. Papeles que hoy se contradicen o no existen.**

| | Qué pasa | Dónde |
|---|---|---|
| `ADR-024` | Sigue en **`Propuesta`**, y `ADR-035` la cambia. **El esquema de ingreso se apoya en ella** | Libro de ADR |
| `CT-03` | **No existe** en ningún documento humano. Lo sostiene `RF-003` solo | `IA_PREGUNTA.md` de este spike |
| `corrige_a` | Las «**cuatro reglas**» que cita la pregunta **no están escritas en ninguna parte** | `IA_PREGUNTA.md` · `ADR-027` |
| Cierre de producción | **No tiene ADR**, y `ADR-022` lo señala como frontera semántica | — |
| `ESC-06` · `RF-017` | Dicen «rechazar la modificación»; `ADR-032` ya decidió que manda la **magnitud** del cambio | Escenarios y funcionalidades |
| Bitácora | El modelo C4 la cuelga de la **sesión de sincronización**; `ADR-035` la movió a la **sesión de captura** | Modelo C4 (no tocar: ver contexto 3 §5) |

**3 · De otros spikes. No bloquean el contrato, sí las cifras.**

- **`SPK-10`** (motor de base de datos) está aparcado → **la concurrencia real no está medida**.
  SQLite serializa donde PostgreSQL no lo haría.
- **`SPK-09`** no fija lenguaje → las dos implementaciones se conservan a propósito.
- **El reloj monótono** —lo único que este spike propone **añadir** al contrato— es una petición al
  cliente de captura, y **`ADR-008` aplaza deliberadamente** esa tecnología. Hoy no se puede pedir.

### `[!]` Lo que NO hay que volver a medir

Es independiente del lenguaje, del motor y de todo lo de arriba. Si alguien lo reabre, pierde la ronda:

1. **Resolver al leer converge.** 12 entregas barajadas, reenviadas y cortadas → un solo hash.
2. **Ordenar por el sello crudo entrega el choque al reloj adelantado.** 100 % de 1.200 choques, en silencio.
3. **Medir el desvío una sola vez no basta.** Con un reloj que salta a mitad de jornada, falla 1 de cada 5 hechos.
4. **El pico de ingesta no es un problema de rendimiento.** Una jornada entera en ~1 s; el +60 % no mueve el p95.

### Qué medir primero cuando se retome

1. **Cadenas de `corrige_a`** (A→B→C) y qué pasa si `corrige_a` apunta a algo que aún no ha llegado.
   Es el hueco más grande que dejé, y **no depende de ningún bloqueo de arriba**: se puede hacer ya.
2. **Concurrencia real en PostgreSQL**, cuando `SPK-10` se desaparque.
3. **`rtt` con ruido y asimétrico**: el error de medición del desvío con red real es mayor que los
   20 ms medidos, y eso decide choques entre dos personas separadas por segundos.

El laboratorio queda **intacto y ejecutable** en `labs/spk-12/` (`python3 escenarios.py ambas`, ~35 s).
La forma del contrato está en `labs/spk-12/CONTRATO.md`.

---

## Medición

**Cómo repetirlo.** El código está en `labs/spk-12/`, sin dependencias de terceros:

```bash
cd labs/spk-12 && python3 escenarios.py ambas     # ~35 s; deja salidas/resultados.json
```

**Dos implementaciones del mismo contrato**, porque el lenguaje no está elegido: `py/ingesta.py`
(Python 3.11 + `sqlite3` de la biblioteca estándar) y `js/ingesta.mjs` (Node 22 + `node:sqlite`).
Mismo `esquema.sql`, mismos datos, mismo protocolo. **Las dos producen el mismo hash de estado en
M1, M2 y M5**, así que lo medido es el contrato y no el lenguaje. La forma del contrato está
escrita en `labs/spk-12/CONTRATO.md`.

**Volumen, derivado de cifras del proyecto** (`ADR-022`: ~24 M eventos por finca en 5 años;
`ADR-009`: 3 capturadores, pico de temporada +60%): ~13.200 anotaciones por jornada de finca,
~4.400 por capturador, 20 campos por cama, ~20 sesiones de captura por jornada.

| | Qué se probó | Qué salió |
|---|---|---|
| **M1** | 4.400 anotaciones (530 de ellas re-anotando un hecho anterior) entregadas **12 veces**: en orden, barajadas, con 35 % de lotes reenviados y una corte-y-reanudación | **1 solo hash** en las 12 corridas y en los 2 lenguajes. 4.400 almacenadas de 4.400, 0 duplicados, 0 pérdidas |
| **M1·caso 2** | 50 anotaciones reenviadas con el **mismo identificador y otro valor** | **50 rechazadas, 0 aceptadas, 0 valores sobrescritos**, 50 anotadas en `rechazo`. El estado queda idéntico al limpio |
| **M2** | 3 aparatos sobre las mismas camas; uno **+37 min adelantado**, otro −12 min. 3.600 anotaciones, 1.200 hechos en choque a tres bandas | Ordenando por **sello normalizado: 1.200/1.200 aciertos (100 %)**. Ordenando por **sello crudo: 0/1.200 (0 %)** — y el aparato adelantado **gana los 1.200**. El desvío se mide con un error de +20 ms, que es `rtt/2` |
| **M2b** | El caso que `ADR-031` se advierte a sí mismo: a `DISP-B` **le corrigen la hora a mitad de jornada** (+37 min → +2 min). La sincronización solo alcanza a medir el desvío final | Sello crudo **74,25 %**. Sello normalizado **80,75 %** — la corrección ayuda pero **falla 1 de cada 5 hechos, y nadie se entera**. Con el reloj monótono reanclado: **100 %**, y las 600 anotaciones de antes del salto quedan **marcadas** como hora dudosa |
| **M3** | Pico de jornada: los 3 capturadores vuelven a la oficina a la vez, 13.200 anotaciones entrelazadas en 27 lotes | Python **13.361 anot/s**, p95 por lote 49 ms · Node **23.445 anot/s**, p95 36 ms. Jornada completa ingerida en **≈1 s / ≈0,6 s** |
| **M3 pico** | Lo mismo con el +60 % de `ADR-009` (21.120 anotaciones) | Python **12.915 anot/s** · Node **22.098 anot/s**. El p95 por lote **no se degrada** |
| **M4** | Ventana larga de `RF-003`: 15 jornadas en cola (66.000 anotaciones, 132 lotes), transporte cortado al 40 % y reanudado en una **sesión de sincronización nueva** | 66.000/66.000, 0 rechazos. Al reabrir, el servidor devuelve **52 lotes acusados**; el aparato reenvía solo los 80 que faltan. Total 6,4 s / 4,2 s |
| **M5** | Choque real: 2 aparatos, las mismas 25 camas, el mismo día, 500 hechos en choque, **40 con sello normalizado exactamente igual**. 20 corridas con orden barajado y aparatos alternados | **1 solo hash** en las 20 corridas y en los 2 lenguajes. Los 40 empates se resuelven por el desempate de identificador, siempre igual |

**Entorno.** Contenedor Linux de 2 vCPU y 8 GB, **no un nodo de finca**, y **SQLite, no PostgreSQL**
(`SPK-10` está aplazado). Las cifras de M3/M4 valen como **orden de magnitud y como forma de la
curva**, no como número de producción.

---

## Lectura

*(Esta sección es interpretación y se puede tirar entera sin perder nada de lo de arriba.)*

**1 · El estado tiene que ser una vista, no una decisión de escritura. Es la pieza que sostiene
todo lo demás.** Si el ganador de `RF-022` se decide al insertar, el resultado depende del orden
de llegada — y con ventanas de 15 días y captura retroactiva, el orden de llegada no es el orden
de los hechos. Insertando siempre y resolviendo al leer, M1 da el mismo hash con las entregas
barajadas, reenviadas y cortadas. **Idempotencia y reanudación no son propiedades del transporte:
son consecuencias de no decidir al escribir.**

**2 · Sin desvío medido, «gana el más reciente» no dice nada — y es peor que eso.** M2 no da un
sesgo: da un barrido. El aparato 37 minutos adelantado gana **el 100 %** de los choques. No es que
se equivoque a veces; es que el resto de los capturadores deja de existir para el sistema, en
silencio y con cara de dato bueno. Es exactamente el fallo del 4-sep, ahora con número.

**3 · El hueco de verdad no es medir el desvío: es que se mide una sola vez.** Medirlo al
sincronizar corrige el caso fácil (reloj constantemente desviado) y **deja pasar 1 de cada 5
hechos** cuando alguien toca la hora en pleno día (M2b) — que es justo el caso que `ADR-014`
admite que va a pasar. Un campo más en el contrato —el reloj monótono del aparato, que no se puede
cambiar a mano— lo lleva al 100 % y, de paso, **convierte el salto en una marca de sospecha** en vez
de en un dato corrompido invisible. No añade un cuarto tiempo a `ADR-031`: cambia de dónde sale el
segundo, y el sello crudo se sigue conservando intacto.

**4 · El pico de ingesta no es un problema de rendimiento, y conviene decirlo antes de que alguien
lo optimice.** Una jornada de finca entera entra en ~1 s, y el pico de temporada no mueve el p95.
Eso vale en los dos lenguajes, con casi el doble de holgura en Node. **El riesgo de esta pieza es
de corrección, no de velocidad**, y el presupuesto de esfuerzo debería ir donde está el riesgo.
Esto no dice nada sobre `SPK-06` (consulta del histórico), que es otra cosa.

**5 · El prototipo de captura contradice `ADR-027` y `ADR-031` en cuatro puntos concretos**, y está
escrito así a propósito: `src/sincronizacion.ts` cita `DEC-05`, que quedó derogada. Como dice la
propia `ADR-027` («el prototipo contradice esta decisión en cuatro puntos»), aquí están localizados:

| Dónde | Qué hace hoy | Qué manda |
|---|---|---|
| `src/id.ts` | identidad **UUIDv7**, y `sincronizacion.ts` **ordena la bandeja por ella** | `ADR-027`: identidad **opaca**, «no se ordena ni se audita por él». Ordenar por el id es ordenar por el reloj del aparato — lo que `ADR-031` prohíbe |
| `sincronizacion.ts` | clave de hecho = **(cama, fecha)** | `ADR-027`: **(producción, sección, campo, jornada)**. Con la clave de hoy dos campos distintos de la misma cama chocan entre sí |
| `sincronizacion.ts` | ante choque **«se dejan las dos y decide una persona»** | `RF-022`/`ADR-031`: automático, sin mediación |
| `sincronizacion.ts` | mismo id con **otro contenido** → `guardarEnServidor` **sobrescribe** | `ADR-027` caso 2: rechazar, registrar y avisar |
| (ninguno) | **no existe el desvío del reloj** en ninguna parte | `ADR-028` lo pide en cada sesión |

**Esto no es una crítica al prototipo**: `ADR-008` lo mantiene vivo y las mediciones de ergonomía
siguen encima de él. Es que **no sirve como base para medir ingesta** hasta que se corrija, que es
lo que la propia `ADR-027` ya dejó anotado.

### `[!]` Dos referencias de la pregunta que no están en los documentos humanos

No las arreglé —la IA no edita los `.xlsx`—, van como cambio exacto para que lo aplique una persona:

| Dónde | Dice hoy | Qué se encontró | Propuesta |
|---|---|---|---|
| `SPK-12/IA_PREGUNTA.md`, §«Lo que manda» | «**`CT-03`** / `RF-003` — entrega idempotente y reanudable, con ventanas de 15 días o más» | **`CT-03` no existe** en ninguno de los cuatro `.xlsx` de drivers ni en el libro de ADR. La exigencia la sostiene `RF-003` sola, con ese texto casi literal | Quitar `CT-03` y dejar `RF-003` |
| `SPK-12/IA_PREGUNTA.md`, §`ADR-027` | «hay tres casos de idempotencia y **cuatro reglas de `corrige_a`**» | Los **tres casos sí** están (en «Consecuencias» de `ADR-027`). Las **cuatro reglas de `corrige_a` no están escritas en ninguna parte**; lo que `ADR-027` dice es que **el prototipo la contradice en cuatro puntos**, que es otra cosa | Corregir la frase, o escribir esas cuatro reglas si existían en la cabeza de alguien |

---

## Dependencias

Lo que asumí de otros componentes, **siempre como contrato y nunca como implementación**:

1. **Base de datos de la empresa** (`SPK-10` aplazado). Asumí un motor relacional con transacción
   multi-sentencia, índice compuesto y un tipo flexible para el valor (`ADR-024`). Se midió sobre
   SQLite. Lo que sí es contrato: **el acuse del lote y sus anotaciones se escriben en la misma
   transacción**; y el índice de resolución es
   `(producción, sección, campo, jornada, t_norm DESC, anotacion_uuid DESC)`.
2. **Aplicación de captura** (`ADR-008`, aplazado). Asumí que genera identificadores opacos sin red,
   que lotea su cola y que **guarda el desvío que le devolvió el servidor**. El reloj monótono
   (§ Lectura 3) es una **petición nueva al cliente de captura**: hoy no lo manda.
3. **Motor de reglas** (`SPK-09`, chat 1). El ingreso **no valida el valor**: lo persiste. La
   validación es de otro componente y `ADR-024` ya lo dice. No opiné sobre él.
4. **Bitácora de auditoría.** El modelo dice «registra por sesión de sincronización»; `ADR-035` la
   movió a la **sesión de captura**. El esquema de `labs/spk-12` sigue a `ADR-035`. **No toqué el
   modelo C4**, como manda el contexto 3.
5. **`ADR-026` (lo asignado es el denominador).** Dejé la tabla y la consulta de faltantes en el
   esquema, pero **sin medir**: depende de `D1`, «qué es la jornada», que es del cliente.
6. **Cifrado y respaldo** (`SPK-13`, chat 3). No opiné. Nada de lo de aquí presupone cifrado en
   reposo ni lo impide.

---

## Qué NO probé

- **Nada en PostgreSQL.** Concurrencia real, nivel de aislamiento y bloqueos entre los tres
  capturadores **no están medidos**: SQLite serializa donde PostgreSQL no lo haría. **La cifra de
  M3 se cae si el motor cambia el modelo de concurrencia**, aunque la holgura es grande.
- **Nada sobre hardware de finca.** 2 vCPU en la nube no son el nodo `N2`.
- **La red.** Todo fue entre procesos: no hay TLS, ni reintentos de HTTP, ni lotes que llegan a
  medias, ni un `rtt` con ruido. El `rtt/2` se asumió simétrico; **el error de medición del desvío
  con red real es mayor que los 20 ms medidos**, y eso importa cuando dos personas anotan la misma
  cama con segundos de diferencia.
- **El tamaño del lote.** Usé 500 anotaciones en todo. No busqué el óptimo ni probé qué pasa con
  un lote que no cabe en memoria.
- **`corrige_a` como cadena.** Lo guardo y lo incluyo en la huella, pero **no probé cadenas de
  corrección** (A corrige a B que corrige a C) ni qué pasa si `corrige_a` apunta a algo que aún no
  ha llegado. Es lo primero que probaría un chat siguiente.
- **La política de `BR-N4`.** Medí el **mecanismo** de desempate, no decidí la política, como pedía
  la pregunta.
- **El cierre de producción**, que no tiene ADR y que `ADR-022` señala como frontera semántica.
- **Un reloj que se va a la deriva poco a poco** (no un salto). Probé el salto, que es el caso que
  `ADR-014` describe; la deriva lenta quedaría igualmente cubierta por el monótono, pero no está medida.
