# Las reglas se mantienen en JSON · y la captura incompleta se sincroniza

> **DECISIÓN DE JUAN · 15-sep-2026**
> Estado: **DECIDIDA · PENDIENTE DE ADR**
> Va al ADR: ______________
> Manda sobre esto: `ADR-PoC-Spikes.xlsx` y `DRIVERS_ARQUITECTONICOS.md`
> Toca: `ADR-006` (motor de reglas guiado por datos) · `ADR-029` (un paquete, una versión) ·
> `CT-02`, `ESC-56`, `ESC-57`

---

## 1 · Lo decidido

Son **dos decisiones separadas** que salieron en la misma conversación. Se escriben aparte porque
no van al mismo ADR y una puede cambiar sin la otra.

**Decisión A — el formato.** Las reglas se mantienen en **JSON**, como hoy en
`app-captura/configuracion/reglas.v1.json`. Es el formato más ligero, rápido y portable de los
explorados, y el que mejor se adapta a mantener reglas.

**Decisión B — qué pasa cuando falta un dato obligatorio.** Un campo obligatorio sin valor
**no frena la captura en la cama ni impide sincronizar**. La información incompleta viaja al
servidor y queda ahí **como trabajo para el gerente de producción**, que es quien depura y decide
si esa información entra como válida aunque esté incompleta.

---

## 2 · Los motivos de Juan

**Perder información es peor que recibirla incompleta.** Que un valor obligatorio omitido se
resuelva con un valor genérico es peligroso: la información se pierde así, sin que nadie lo note.

**Un obligatorio no siempre aplica de verdad.** Si al registrar una cama era obligatorio poner el
número de líneas, pero **esa cama en concreto no trabaja con líneas**, ese es un motivo real. No
puede frenarme la producción por «incompleto».

**Un obligatorio puede faltar por un fallo que no es del capturador.** Por ejemplo el número de
lote: porque no se ha registrado, porque no lo hay en el dispositivo por X o Y motivo, o porque
falló la sincronización que lo traía. Información obligatoria que **no me impide sincronizar** el
número de tallos y lo demás, y depurar eso más adelante.

**Quien decide es el gerente de producción.** Él depura la información y hace cambios según las
cosas raras identificadas. Es él quien toma la decisión de si la información entra aunque esté
incompleta.

---

## 3 · Lo que esta decisión toca, en cifras

*Medido sobre la batería de 1.232 camas de `SPK-09` (`labs/spk-09/`).*

**La Decisión B mueve la mayoría de la batería.** Hoy **872 camas de 1.232 (70,8 %)** las bloquea
una regla dura en el dispositivo. Bajo la Decisión B, esas 872 dejan de bloquearse en campo y pasan
a la cola del gerente. No es un ajuste de borde: es el caso mayoritario.

Reparto de las seis reglas duras, en camas de la batería que toca cada una:

| Regla | Qué exige | Camas |
|---|---|---|
| `RG-01` | la sección tiene variedad | 285 |
| `RG-02` | hay número de líneas | 235 |
| `RG-03` | hay cantidad | 231 |
| `RG-04` | las líneas están dentro del rango posible | 196 |
| `RG-05` | la cantidad está dentro del rango posible | 236 |
| `RG-08` | la cama tiene al menos una sección | 147 |

**Lo que NO cambia.** `CT-02` y `ESC-57` (cero divergencia) quedan intactos: los dos lados siguen
calculando **el mismo veredicto**; lo que cambia es qué hace la aplicación después del veredicto.
El contrato del evaluador sobrevive entero, y con él el 0 % medido en los cinco caminos.

**Lo que se vuelve más importante.** `ESC-56` — el motivo en lenguaje de negocio. Antes el motivo
lo leía el capturador y se acababa ahí. Ahora **el motivo viaja con el registro hasta el gerente**,
y es lo único que le va a permitir separar un montón de huecos en una cola triable.

---

## 4 · Motivos de la IA — por qué JSON, y sus contras

> **REGISTRO DE IA · NO ES FUENTE · SIN REVISAR**
> Generado por Claude · 15-sep-2026 · derivado de `spikes/SPK-09-motor-de-reglas/IA_RESULTADO.md`
> **Este apartado se puede tirar entero sin tocar la decisión de arriba.**

### A favor de JSON

1. **Peso.** El catálogo pesa 2 823 B (879 B comprimido) y el evaluador que lo interpreta, 1 911 B
   (895 B comprimido). Menos de 2 KB comprimidos en el dispositivo, reglas incluidas. La alternativa
   más cara medida (CEL como lenguaje de expresiones) añade 276 KB de evaluador — 67 KB comprimido.
2. **Portabilidad comprobada, no prometida.** El mismo `reglas.v1.json` se leyó desde node, bun,
   Go nativo, Go compilado a wasm, CPython y wasmtime: **0 % de divergencia de veredicto en todos**.
   Ningún lenguaje de servidor queda descartado por el formato.
3. **Versionar sin publicar la aplicación**, que es requisito escrito del cliente en `ADR-006`.
   Se probó de verdad: cambiar un rango movió 505 camas, añadir una regla de un tipo ya conocido
   movió 691, cambiar un texto movió 0 — **los tres sin tocar una línea de código**.
4. **Encaja con `ADR-029` y con git.** Un paquete, un número de versión, y un cambio de regla que
   se revisa línea por línea en un diff. Un binario o una tabla en base de datos no dan eso.
5. **Parser en la biblioteca estándar** de todos los lenguajes candidatos. Cero dependencias nuevas.

### En contra de JSON

1. **`[!]` JSON no valida nada por sí solo.** Se metió una regla con un `tipo` que el motor no
   conoce: **no falló, no avisó, y la cama se cerró como si la regla no existiera.** Una regla puede
   viajar al dispositivo y no aplicarse nunca sin que nada lo diga. Hace falta un validador del
   paquete, y `ADR-015` (se verifica antes de salir al cultivo) ya es el sitio donde ponerlo.
2. **`[!]` JSON no define qué es «vacío».** `null`, la clave ausente, `""` y `0` son cuatro cosas
   distintas y el formato no dice cuál significa «falta el dato». Medido: tratar el `0` como vacío
   cambia el veredicto en 96 camas (7,8 %); que un obligatorio vacío cuente además como fuera de
   rango, en 404 (32,8 %).
3. **JSON no tiene expresiones.** Hoy solo **2 de los 6 tipos de regla son genéricos**; los otros
   cuatro son código escrito a mano para esa regla y solo esa. Una regla con una *forma* nueva sigue
   exigiendo versión nueva de la aplicación. Hoy «reglas guiadas por datos» significa, en la
   práctica, «parámetros como datos y formas de regla como código».
4. **Los números son coma flotante IEEE.** De ahí sale una divergencia de redondeo del 0,24 % que no
   se ve mirando las dos salidas: las dos parecen correctas.
5. **No tiene comentarios.** El campo `nota` es una convención, no una característica del formato.
   El *porqué* de una regla —lo que `ESC-56` pide en lenguaje de negocio— no tiene un sitio natural.
6. **El texto tiene que estar dentro.** Si el mensaje sale del JSON, la divergencia entre los dos
   lados es 0 %. Si vive en el código, sube a 37,5 %. El formato ayuda solo si el mensaje está dentro.

### `[!]` Lo que JSON no arregla, y por qué no es la respuesta a la preocupación de Juan

La pérdida de información que preocupa a Juan **no la causa el formato**. La causa que el catálogo
no diga qué significa un hueco. Cambiar de formato no la arregla; **escribirlo en el catálogo, sí.**

Y hay un segundo filo, más concreto. Hoy, en `src/reglas.ts`, la función que decide si un campo está
vacío trata `null`, `undefined`, `""` y `NaN` **como la misma cosa**. Es decir: hoy el sistema **ya
no distingue** entre los tres casos que Juan describe —

- «todavía no lo capturé»,
- «esta cama no trabaja con líneas»,
- «el lote no estaba en el dispositivo» —

y los tres llegan al servidor como el mismo hueco. **La ausencia no puede ser el dato.** Si el
gerente recibe 802 camas con huecos y ningún motivo pegado a cada hueco, la depuración no se elimina:
se muda de la finca a la oficina, que es exactamente lo que `IA_PREGUNTA` advierte cuando dice que
«si la captura digital produce los mismos errores del papel, la reconciliación solo se muda de sitio».

La Decisión B **con un motivo tipificado** no tiene ese problema: convierte un bloqueo en una cola
que se puede filtrar. La Decisión B **sin motivo** sí lo tiene. Los dos ejemplos de Juan ya son dos
motivos distintos, y piden tratamientos distintos:

| Motivo | Ejemplo de Juan | Qué es en realidad | Qué debería pasar después |
|---|---|---|---|
| `no-aplica` | «esta cama no trabaja con líneas» | un hecho del cultivo, estable | si se repite, **modelarlo en el catálogo**; si no, el gerente lo depura una vez |
| `no-disponible` | «el lote no estaba en el dispositivo» | un fallo de catálogo o de sincronización | **arreglar el fallo**; cada caso es un defecto, no una excepción normal |

Si los dos llegan como `null`, ni el gerente ni ningún informe pueden separarlos — y el primero se
depura 365 veces al año en vez de modelarse una.

---

## 5 · Lo que queda abierto

No son objeciones a la decisión; son huecos que la decisión deja y que hay que cerrar antes de que
esto sea un ADR.

1. **¿Omitir es lo mismo que equivocarse?** Los tres motivos de Juan hablan de **omisiones**. Pero
   `RG-04` y `RG-05` no son omisiones: son **valores imposibles** (900 líneas, 45.000 tallos). Una
   cama sin líneas es un hecho legítimo; una cama con 900 líneas es un error de digitación. El
   argumento de «no frenar la producción» no se extiende solo a ese caso. **432 camas** de la
   batería están ahí.
2. **¿`RG-08` también?** Una cama sin ninguna sección no lleva información que depurar: no hay nada
   que el gerente pueda rescatar. Es la única de las seis duras donde sincronizar no aporta nada.
   **147 camas.**
3. **¿Quién puede marcar el motivo, y cuál?** Si el capturador elige el motivo de una lista, esa
   lista es catálogo y versiona con el paquete (`ADR-029`). Si lo escribe libre, vuelve a ser texto
   sin filtrar. Decisión pendiente.
4. **¿Con qué autoridad el gerente vuelve válido un registro incompleto?** `ADR-032` ya trata la
   confirmación del segundo administrador según la magnitud del cambio. Esto se le parece y conviene
   mirar si es el mismo mecanismo o uno nuevo.
5. **¿Qué pasa con la proyección mientras el registro está incompleto?** Un registro pendiente de
   depurar, ¿cuenta para el cálculo vivo de `ADR-005` o se excluye hasta que el gerente lo apruebe?
   Cambia lo que ve el cliente entre la sincronización y la depuración.
6. **El formato de los datos capturados no es este.** Todo lo medido arriba es sobre el **artefacto
   de reglas**. Que los datos de captura viajen o no en JSON es territorio de `SPK-12` (ingreso y
   sincronización) y de `SPK-14` (paquete de configuración), y aquí no se toca.
