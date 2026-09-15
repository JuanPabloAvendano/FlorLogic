# `SPK-09` · Motor de reglas — matriz de caminos, con evidencia medida

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 14-sep-2026 · Chat 1 de 3 (Nivel 3, Backend de la finca)
> Estado: **SIN REVISAR**
> Manda sobre esto: `ADR-006`, `ADR-029`, `CT-02`, `ESC-56`, `ESC-57` en `ADR-PoC-Spikes.xlsx` y `DRIVERS_ARQUITECTONICOS.md`

**Sin ganador, a propósito.** Lo que sigue es el mapa de lo que cuesta cada camino.
La elección es de Juan y va después.

---

## Medición

**Batería:** 1.232 camas generadas con semilla fija (32 casos de borde escritos a mano + 1.200 de
barrido), 2.189 secciones. Las nueve reglas disparan. El «correcto» no se inventó: lo define
`app-captura/src/reglas.ts` (`PoC-0`) corriendo la batería, copiado sin tocar una línea.

**Repetir todo:** `bash labs/spk-09/correr.sh` (node ≥22, bun, python ≥3.11, go ≥1.24).
Datos y salidas: `labs/spk-09/bateria/` y `labs/spk-09/resultados/`.

### La matriz

| | **A1** · un JS compartido | **A2** · un fuente → wasm | **B** · reglas como datos | **C** · dos implementaciones | **D** · solo servidor |
|---|---|---|---|---|---|
| **Divergencia de veredicto** | **0 %** (node y bun) | **0 %** (anfitrión node y anfitrión Python: salida idéntica) | **0 %** en las 4 combinaciones | **0 %** en las 3 implementaciones | no aplica: un solo lado |
| **Divergencia de motivo** (`ESC-56`) | **0 %** | **0 %** | **0 %** JS↔JS · **1,95 %** JS↔Python | **37,5 %** tal cual salió · **1,95 %** con los textos idénticos | no aplica |
| **µs por cama** (mediana / p95) | 0,43 / 0,57 node · 0,68 / 1,13 bun | 0,98 / 1,30 | 21 / 34 JSON Logic-JS · 47 / 75 CEL-JS · 132 / 178 JSON Logic-Py · **5 267 / 6 161 CEL-Py** | 0,20 / 0,27 Go · 3,2 / 4,7 Python | — |
| **Artefacto al dispositivo** | 1 911 B (**895 B** gz) | 3 396 545 B (**953 KB** gz) | +5 540 B (1 871 gz) JSON Logic · +275 897 B (**67 KB** gz) CEL · reglas 8 401 B | igual que A1 | 69 % del paquete de reglas |
| **Versionar una regla** | rango, texto y regla nueva de tipo conocido: **sin código**. Tipo **nuevo**: código en cada implementación + versión nueva de la app | igual que A1 | **los cuatro cambios sin código**, incluido el tipo nuevo | igual que A1, pero multiplicado por cada implementación | igual que A1 |
| **Quién arma el motivo** | el evaluador | el evaluador | **el anfitrión, en los dos lados** | **el anfitrión, en los dos lados** | el servidor |
| **Lenguajes de servidor vivos** | **solo JavaScript** | cualquiera con runtime wasm — **medido: JS y Python** | casi cualquiera con evaluador — **medido: JS y Python**, en los dos lenguajes de expresión | **cualquiera** | **cualquiera** |
| **Costo de mantenimiento, en una frase** | uno solo: el servidor queda atado al lenguaje del navegador | uno solo, más una cadena de compilación a wasm y 1 000× el peso del artefacto | el artefacto de reglas es único, pero el anfitrión que le da forma a los datos y arma el motivo existe dos veces | tres bases de código para nueve reglas, y ninguna herramienta avisa cuando se separan | una sola base, pagada con lo que el capturador deja de ver en la cama |

### Lo que la matriz no muestra

**1 · La divergencia de veredicto salió 0 % en todos los caminos. La que no se va es la del motivo.**
`ESC-57` se cumple hoy en los cinco caminos. La grieta está en `ESC-56`: el motivo es texto armado
con números calculados, y ahí sí divergen. En B y en C el motivo no lo produce la regla: lo produce
el anfitrión. Por eso B y C dan **la misma cifra de 1,95 %**.

**2 · Qué cuesta cada decisión que el catálogo no especifica** (camino C; sobre 1.232 camas):

| decisión razonable que `reglas.v1.json` no fija | cambia el veredicto | cambia el motivo |
|---|---|---|
| un campo obligatorio vacío también cuenta como fuera de rango | **404 (32,8 %)** | 404 |
| tratar el `0` como vacío (lo *falsy* del lenguaje) | **96 (7,8 %)** | 96 |
| `min`/`max` excluyentes en vez de inclusivos | **23 (1,9 %)** | 23 |
| que `0` sea un valor y no un vacío en la razón | **21 (1,7 %)** | 21 |
| usar coma decimal, como se escribe en Colombia | 0 | 462 (37,5 %) |
| truncar la razón en vez de redondearla | 0 | 238 (19,3 %) |
| imprimir siempre dos decimales (`19,00` en vez de `19`) | 0 | 79 (6,4 %) |
| redondear a la par — el `round` de Python, el `FormatFloat` de Go | 0 | **3 (0,24 %)** |
| tolerancia inclusiva (`>=` en vez de `>`) | 0 | 0 |

La última fila con cifra es la peligrosa: tres camas de mil doscientas, las dos salidas se ven
correctas, y nadie lo nota sin una batería.

**3 · Hoy, «reglas guiadas por datos» quiere decir «parámetros como datos, formas de regla como
código».** De los seis tipos del catálogo, **dos son genéricos** (`obligatorio`, `rango`) y cuatro
tienen un bloque escrito a mano para esa regla y solo esa. **Las tres reglas blandas son exactamente
las tres que tienen código propio.**

**4 · `[!]` Un tipo de regla desconocido se ignora en silencio.** Se añadió `RG-11` con un `tipo`
que el motor no conoce: no falló, no avisó, y la cama se cerró como si la regla no existiera.
Con `ADR-029` —un paquete, un número de versión— una regla puede viajar al dispositivo y no
aplicarse nunca sin que nada lo diga.

**5 · CEL no es portable tal cual.** La misma expresión revienta en `cel-python` y corre en
`@bufbuild/cel`; `has()` sobre una clave con `null` da `false` en uno y `true` en el otro; ninguna
de las dos trae `abs` ni `round`; una tercera (`cel-js`) no reconoce siquiera `string()`. Las nueve
expresiones hubo que reescribirlas y obligar al anfitrión a omitir las claves vacías. Los cuatro
incidentes están anotados en `camino-b-datos/reglas.b.json`.

**6 · Sin caché de compilación, CEL cuesta 31×** (1 445 µs por cama en vez de 47). Es una decisión
de integración que no se ve en ninguna comparativa de lenguajes de reglas.

**7 · El piso de adopción no lo decide ninguno de estos caminos.** El más lento medido en el
dispositivo (CEL-JS, 47 µs) está tres órdenes de magnitud por debajo de lo que una persona percibe.
Solo CEL-Python (5,3 ms) estaría en discusión, y ese no corre en el dispositivo.

**8 · Camino D, en cifras.** Hoy, de 1.232 camas: 97 cierran limpias (7,9 %), 872 las bloquea el
dispositivo (70,8 %), 263 solo reciben aviso (21,3 %). Bajo D dejan de verse en la cama las tres
blandas: `RG-06` la razón (462 camas, 37,5 %), `RG-09` cama repetida hoy (480, 39 %), `RG-07`
variedad repetida (184, 14,9 %). **802 camas salen hoy del campo con un aviso encima.** A cambio,
el paquete baja al 69 % y el dispositivo deja de necesitar `plantasPorLinea`: `RG-06` es la única
de las nueve que usa el catálogo de variedades. **`RG-06` es también la regla que la pregunta
señala como el mecanismo que tumba los 8 días** — la que separa un error de digitación de una
densidad distinta.

---

## Lectura

*(esto se puede tirar entero sin perder ni una medición)*

El eje que ordena la matriz no es el lenguaje: es **dónde vive el texto del motivo**. Donde el
evaluador lo produce (A1, A2), la divergencia es 0 %. Donde lo arma el anfitrión (B, C, D), aparece
el 1,95 % —y el 37,5 % cuando además los literales viven en dos archivos. El camino B mueve la
*regla* a un artefacto único, pero no mueve el *motivo*: medido contra `ESC-56`, B se parece más a C
de lo que su promesa sugiere.

La segunda cosa que cambió de forma al medirla es el precio de A2. El artefacto de 953 KB
comprimidos no es el costo del wasm: es el costo del runtime de Go. El piso sin runtime son 245 bytes.
Como `ADR-015` verifica el paquete **antes de salir al cultivo**, ese peso es de preparación y no de
campo — pero es 1 000× A1, y decidirlo con el número de Go sería decidirlo con el número equivocado.

La tabla de sensibilidad es, a mi juicio, el resultado más útil de este spike, y no habla de ningún
camino: habla del catálogo. Cuatro decisiones que `reglas.v1.json` no fija mueven el **veredicto**
entre el 1,7 % y el 32,8 % de las camas. Mientras esas cuatro no estén escritas, «0 % de divergencia»
es una propiedad del cuidado con que se escribió el código, no del camino elegido.

Y `ESC-56` pide el motivo **en lenguaje de negocio**. Ninguna de las expresiones de B lo es. Lo que
B convierte en dato es la condición, no la explicación.

---

## Dependencias

Todo lo de abajo se tomó **como contrato**, nunca como implementación de otro componente.

- **Cliente de captura** (`ADR-008`, aplazado: sigue siendo aplicación web) — el lado dispositivo
  corre en un navegador. Se asumió un motor de JavaScript y capacidad de `WebAssembly`.
- **Paquete de configuración** (`ADR-029`) — un paquete, un número de versión, que trae **catálogo
  de variedades y reglas juntos**. `RG-06` no existe sin `plantasPorLinea` y `confianza`.
- **Verificación del paquete** (`ADR-015`) — se verifica antes de salir al cultivo. Por eso el tamaño
  del artefacto se leyó como costo de preparación, no de campo.
- **Ingreso y sincronización** (`SPK-12`, chat 2) — solo la **forma**: que al servidor le llega la
  captura completa, con sus secciones, para poder revalidar. Nada sobre el transporte.
- **Base de la finca** — `RG-09` necesita cuántas capturas de esa cama hay hoy. En el dispositivo es
  local; en el servidor es una consulta. Contrato asumido: un contador por (cama, fecha).
- **`ADR-035`** (la sesión de captura es la unidad de trazabilidad) — **no se usó**: la batería
  trabaja a nivel de cama. Si la unidad manda también para la evaluación, la batería queda corta.

---

## Qué NO probé

1. **Un navegador. Ni uno.** Todo corrió en node, bun, CPython y Go sobre x86 en la nube. **No hay
   una sola medición sobre un dispositivo de campo**, y el piso de adopción se juzga ahí.
2. **`[!]` Las tres implementaciones del camino C las escribió el mismo agente, en la misma sesión.**
   Eso **subestima la divergencia de C de forma sistemática**: el 0 % de Go no es evidencia de que C
   sea seguro. La cifra honesta de C es la tabla de sensibilidad, no el 0 %.
3. **Rust y TinyGo a wasm** — bloqueados por la red del entorno (`static.rust-lang.org` fuera de la
   lista). El tamaño real de A2 con un lenguaje sin runtime pesado **no se midió**: quedó acotado por
   arriba (Go, 953 KB gz) y por abajo (AssemblyScript, 208 B gz), que es un rango de 4 000×.
4. **Rego/OPA y JSONata** — no se pudieron traer (`proxy.golang.org` bloqueado; JSONata no tiene una
   segunda implementación que pudiera correr). **B se midió con dos de los cuatro candidatos.**
5. **C# / .NET y Java** — sin toolchain en el entorno. No se sabe qué les pasa en ningún camino.
6. **Arranque en frío del wasm en un móvil.** Medido: 13,5 ms en V8, 1 389 ms en wasmtime. Dos
   órdenes de diferencia según el anfitrión, y el del dispositivo real no se midió.
7. **Un catálogo grande.** Nueve reglas, sin dependencias entre ellas. Con cincuenta, o con reglas
   que dependan de otras, la expresividad de B y los tiempos pueden cambiar de forma.
8. **Si una persona puede leer y mantener las expresiones de B.** El costo de traducir las nueve lo
   pagó la IA en una sesión; el de mantenerlas, no se midió. `ESC-56` pide lenguaje de negocio.
9. **El evaluador bajo carga en el servidor** — ni en la ruta de ingesta, ni contra la base.
10. **Identidad y tiempos** (`ADR-027`, `ADR-031`) — la batería no toca sellos de tiempo ni
    idempotencia. Si la reevaluación en el servidor tiene que ser reproducible en el tiempo, eso no
    se midió.
11. **El contenido de las reglas.** Es del cliente (`D1`) y no se tocó.

---

### Lo que haría falta para poder comparar de verdad

No es una recomendación; es lo que hoy lo impide:

- **Cerrar las cuatro decisiones de la tabla de sensibilidad dentro del catálogo.** Mientras no estén
  escritas, ningún camino puede prometer 0 %.
- **Una medición en un dispositivo real**, aunque sea uno.
- **Saber si el motivo en lenguaje de negocio se compone o se escribe.** Si se compone a partir de
  números calculados, el motivo es código y ningún camino lo vuelve dato.
- **`ADR-006` contempla dos motores independientes; `CT-02` exige el mismo evaluador.** La matriz
  mide lo que cuesta cada lectura, pero **la contradicción sigue abierta y es de Juan.**
