# `SPK-16` · Salida de datos hacia la herramienta de BI del cliente — resultado

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 15-sep-2026
> Estado: **SIN REVISAR** · Chat 6
> Manda sobre esto: `ADR-013`, `ADR-010` · `CN-10`, `CN-16`, `CN-17` · `CT-05`, `ESC-29`, `RF-012`

Las tablas completas y los matices de cada corrida están en
`labs/spk-16/salidas/EVIDENCIA-detallada.md`; aquí va lo que cabe en dos páginas.

---

## Medición

PostgreSQL 16.13, máquina de 2 núcleos y 7 GB. Un año sembrado con las cifras del dimensionamiento
del 4-sep: **4.800.000 hechos** (8.000 producciones × 600) más 480.000 de una segunda empresa que
solo existe para probar el acotamiento. Con índices, 1.710 MB para 5.280.000 hechos: **340 bytes por
hecho**, así que la estimación de ~300 aguanta. El modelo derivado agregado: 2.580.960 filas.
`ESC-29` da **600 s** de presupuesto.

| Camino | Qué extrae | Filas | **Tiempo** | Volumen |
|---|---|---|---|---|
| **A** rol de solo lectura, cursor de servidor | derivado | 2.341.760 | **6,92 s** | 80 MB |
| **A** rol de solo lectura, `COPY` | derivado | 2.341.760 | **1,99 s** | 86 MB |
| **A** rol de solo lectura, cursor de servidor | grano fino | 4.800.000 | **33,84 s** | 491 MB |
| **B** endpoint REST propio · **python** | derivado | 2.341.760 | **15,41 s** | 396 MB |
| **B** endpoint REST propio · **node** | derivado | 2.341.760 | **15,00 s** | 366 MB |
| **B** endpoint REST propio · python | grano fino | 5.000.000 | **68,44 s** | 1.206 MB |
| **C** conector OData v4 · **python** | derivado | 2.341.760 | **16,25 s** | 407 MB |
| **C** conector OData v4 · **node** | derivado | 2.341.760 | **16,94 s** | 378 MB |

**El lenguaje no aparece en el resultado:** python contra node difiere 2,7 % en REST y 4,2 % en
OData, en direcciones opuestas — por debajo del ruido. Lo que cuesta es el JSON: el mismo año sale
en 6,92 s por el protocolo de PostgreSQL y en ~15 s por HTTP, **2,2× más caro por el formato**, en
cualquiera de los dos lenguajes. **El peor caso medido usa 68 s de los 600.**

### Punto 2 · dónde queda abierto un camino directo a la base

Con la credencial del camino A y la RLS ya puesta, doce sondas. Las tres que importan:

- Leer filas de la empresa 2 por la vista expuesta: **0**. La RLS cumple en las tablas.
- Leer **la tabla base** saltándose la vista: **entra** (4.800.000 filas propias, 0 ajenas).
- Leer **la vista materializada del modelo derivado**: **2.580.960 filas, con 239.200 de la
  empresa 2 dentro.**

**El agujero:** `ALTER MATERIALIZED VIEW ... ENABLE ROW LEVEL SECURITY` **no existe en PostgreSQL**.
La RLS protege tablas y no protege el modelo derivado — que es justo lo que `ADR-010` quiere exponer.
Se rompió el 100 % de `CT-05` por una vía que parecía cubierta.

**El arreglo, probado:** una vista encima de la materializada que se ejecuta con los permisos de su
dueño (el comportamiento por defecto, **no** `security_invoker`) y filtra por `current_user`, sin
conceder `SELECT` sobre la materializada. Con `security_invoker` no funciona: el rol necesitaría
permiso sobre la materializada y con él se la salta. Tras el arreglo, alfa ve 2.341.760 y beta
239.200 —suman el total exacto—, 0 filas ajenas por cualquier vía, y suplantar el rol, reescribir el
mapeo rol→empresa o crearse una vista propia quedan denegados.

**Aun arreglado, el camino A no da el cero de `ESC-29`:** la credencial *es* de base de datos y el
cliente *tiene que* alcanzar el puerto de PostgreSQL. El acceso directo no se cierra, se acota. Y
queda lo que no es leer datos: con esa misma credencial una consulta arbitraria costó **9,1 s**
contra los **0,2 s** de su trabajo legítimo (**45×**), sin límite de fábrica de conexiones (`-1`) ni
de tiempo por consulta (`0`). En B y C el cliente tiene un token, no credencial de base; sin
credencial responde **401**; el puerto de la base no sale de la máquina. **Cero por construcción.**

### Punto 4 · refresco concurrente

Escribir hechos nuevos no molesta al que lee: una sincronización de 200.000 hechos durante la
extracción la degradó **1,07×**. **El choque real va al revés del esperado:** regenerar el modelo
derivado pide un bloqueo exclusivo, espera detrás del cliente que ya estaba extrayendo, y **todo el
que llega mientras espera se encola detrás aunque solo quiera leer** — el segundo cliente vio su
**primera fila a los 19,92 s**, sin ningún error, solo un tablero en blanco. Con
`REFRESH ... CONCURRENTLY`: **0,03 s**, a cambio de un 20 % más de refresco y lectores 1,8× más
lentos.

### Punto 5 · sin internet

Los tres caminos se levantaron en un espacio de red con solo la interfaz local. Control: `https`
externo → 000; TCP externo → sin salida; resolución de nombres → falla. Ahí dentro el camino A
responde, el REST devuelve datos y el `$metadata` de OData responde **200** y sirve páginas.
**El camino entero vive dentro de la finca.**

### Hallazgo suelto

Paginar por la clave de hecho **sin índice** hace que cada página vuelva a recorrer y ordenar los
4,8 M de filas: la extracción no terminó. Con índice sobre `(empresa_id, clave_hecho)` —10 s,
**222 MB**— pasa a *Index Only Scan*. Sale a la luz porque `ADR-027` mantiene la identidad técnica
fuera de lo expuesto: si no se pagina por el identificador interno, hay que pagar el índice de la
clave por la que sí se pagina.

---

## Lectura

*(interpretación; se puede tirar entera sin perder la medición)*

**`ESC-29` no es la restricción.** El peor caso usa el 11 % del presupuesto y el caso realista, el
2,5 %. Hay entre 9× y 40× de margen. Elegir el camino por rendimiento es elegirlo por la variable que
sobra: **lo que separa a los tres es quién puede hacer qué, y qué pasa cuando dos cosas coinciden.**

**El camino A y el cero de `ESC-29` son incompatibles**, y no por configuración mejorable: dar una
credencial de base es dar acceso a la base. Se acota bien —la RLS cumple— pero el escenario pide cero
y ahí el cero no existe. B y C lo dan por construcción.

**El agujero de la vista materializada es lo que más pesa de este spike**, porque es del tipo que no
se ve: la RLS estaba puesta y aun así una credencial de la empresa 1 leía 239.200 filas de la
empresa 2. La protección de las tablas **no se hereda** al modelo derivado. Cualquier camino que
exponga un modelo materializado —los tres— necesita el arreglo, y necesita una prueba automática que
compruebe el cero en cada cambio: nadie ve un mensaje de error, simplemente hay filas de más.

**Entre B y C la diferencia no es técnica.** Rinden igual y ambos cierran la base. OData tiene
conector nativo en Power BI: el cliente pega una dirección. El REST propio obliga al cliente a
escribir su consulta. Como `CN-10` lleva la nota literal **«POWER BI»**, el que menos le pide al
cliente es OData, a cambio de sostener un formato ajeno.

**La concurrencia es lo que hay que diseñar, no el rendimiento.** Con cadencia diaria, el momento en
que el derivado se regenera y el momento en que alguien abre su tablero se van a cruzar.
`CONCURRENTLY` cuesta un 20 % que, con estos márgenes, no se nota.

---

## Dependencias

- **`ADR-010` y la Ronda 0.** Todo se midió sobre *un* modelo derivado agregado. Si la Ronda 0
  concluye que son tres mecanismos, cambia **qué** se expone, no el cómo: los tiempos escalan con las
  filas y los hallazgos de seguridad y concurrencia son independientes del contenido. **No se esperó
  a la Ronda 0.**
- **El motor de base de datos.** Todo sobre PostgreSQL, que en `ADR-024` sigue como **propuesta**.
  Dos resultados dependen del motor: que la RLS no alcance las vistas materializadas y el bloqueo del
  refresco. Los tiempos por HTTP casi no, porque el costo está en el JSON.
- **El lenguaje del backend.** No se asumió ninguno. Solo el grano fino por HTTP se midió en un
  lenguaje, **después** de comprobar la paridad.
- **La sincronización.** Se asumió solo su forma: hechos que llegan en tandas a la misma base.
- **El API Gateway de la finca.** Se asumió que puede publicar un puerto HTTP hacia las oficinas y
  no publicar el de la base. B y C dependen de eso.

## `[!]` Corrección pendiente del levantamiento

`ESC-29` arrastra la observación **«la decisión vigente excluye Power BI»**, que hay que **borrar**:
`B5` revirtió esa decisión y `ADR-013` la da por integrada. **No se tocó el `.xlsx`.**

## Qué NO probé

- **Power BI de verdad.** Se midió con clientes que hablan los mismos protocolos, no con Power BI
  Desktop: queda sin medir su compresión al importar y si su conector OData respeta esta paginación.
- **`DirectQuery` contra importación.** Todo lo medido es una extracción completa. `DirectQuery`
  lanza muchas consultas pequeñas en horario de trabajo: otro perfil de carga.
- **El servicio de Power BI en la nube y su puerta de enlace**, que sale del alcance de `CN-17`.
- **TLS, rotación y caducidad de credenciales.** Los tokens del prototipo son fijos y en claro.
- **Datos reales y más de dos empresas.** Los datos son sintéticos y uniformes; un año real tiene
  picos de temporada.
- **El hardware del nodo.** Si el nodo real es más modesto los tiempos suben proporcionalmente y aun
  así sobra presupuesto, pero conviene confirmarlo.
- **La generación de Excel y PDF** (`RF-019`, `ESC-51`) y **la lectura del heredado** (`SPK-11`).
- **Una prueba automática del cero de `CT-05`.** Se comprobó a mano, doce veces; no quedó montada la
  prueba que lo comprobaría en cada cambio, que es lo que el hallazgo pide.

---

*Código, datos y evidencia larga: `Claude outputs/labs/spk-16/`. Se repite con `bin/correr.sh`.*
