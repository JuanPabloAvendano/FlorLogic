# `SPK-11` · ¿Se puede leer el sistema heredado en Access?

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 14-sep-2026
> Estado: **APLAZADO 14-sep-2026** · Fuera de la ronda del 14-sep, que es solo Backend de la finca. Se retoma cuando llegue una copia del archivo del cliente.
> *(El contenido de abajo se conserva intacto para cuando se retome.)*
> Manda sobre esto: `CN-20` en `DRIVERS_ARQUITECTONICOS.md` · `1_VOZ_DEL_CLIENTE.md §7`

## Qué se decide

Si el archivo del sistema actual se puede leer y con qué herramienta, y qué hay dentro.

Es la prueba más barata de las tres y **destraba a las otras dos**: da datos reales para `SPK-10` en
vez de sintéticos, y abre la posibilidad de sacar `BR-23` del histórico.

## `[!]` Atribución

Que el sistema heredado sea Access lo dijo **Juan** el 11-sep-2026, con «posiblemente y casi que
seguro». **No es una confirmación del cliente.** «Access» no aparece en ninguna transcripción ni
documento del repo — se verificó. `CN-20` **no se marca cerrada** hasta ver el archivo o hasta que el
cliente lo diga.

## Por qué importa más de lo que parece

`CN-20` describe *«un sistema de productividades que el cliente no supo nombrar»*, y
`1_VOZ_DEL_CLIENTE §7.6` lo lista entre lo que el cliente nunca dijo. El sistema que no supo nombrar
es **el de productividades** — y `BR-23` (% de productividad por variedad + curva de reparto del
corte) es exactamente eso: el bloqueo real del producto, sin fecha, que detiene `T10`.

Si esos números están en el archivo, **no se piden: se leen o se derivan**. Y `§7.11` —si los códigos
de cama se repiten entre bloques y si cambian al renovar, *«la llave primaria de todo el modelo»*—
se responde mirando, no preguntando.

## Qué se prueba

1. Abrir un `.mdb` / `.accdb` con `mdbtools` y con `pyodbc`. Cuál funciona y con qué límites.
2. Listar el esquema: cuántas tablas, cuáles son de producción (`CN-20` dice ~300 y 45).
3. Buscar dentro: productividades por variedad · reparto del corte por día · nomenclatura de camas ·
   cuántos años de histórico.
4. Calidad del dato: integridad referencial, fechas como texto, tablas por año, variantes de escritura
   del mismo nombre (el caso `Cortona`/`Cartona` del papel).

## Lo que hay que pedirle al cliente

**Una copia del archivo, no un diccionario de datos** — el diccionario no existe; si existiera, el
cliente sabría nombrar el sistema. Y seis preguntas: ¿`.mdb` o `.accdb`? · ¿un archivo o uno por año
y por finca? · ¿solo base de datos o tiene formularios y macros? · ¿contraseña? · ¿cuántos años? ·
¿queda alguien que conozca el esquema?

## Qué NO se decide aquí

Si FlorLogic **reemplaza, alimenta o convive** con el heredado — eso es `CN-20` y lo cierra Juan con
el cliente · cómo se construye el importador · qué se migra.

## `[!]` Y un límite de alcance

«Cargar la información» son **tres cosas distintas** y conviene no mezclarlas: **datos maestros**
(camas, bloques, variedades — precondición para arrancar) · **histórico de producción** (alimenta
`BR-23`, no bloquea capturar) · **operación en paralelo** (seguir alimentando Access: hay que
evitarlo, la doble captura mata la adopción).
