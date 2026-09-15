# `SPK-17` · Java y C# — cerrar el único hueco de lenguaje que queda

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 15-sep-2026
> Estado: **SIN REVISAR** · Chat 7
> Manda sobre esto: `ADR-PoC-Spikes.xlsx` · `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx` ·
> `IA_CONTEXTO-3-BACKEND.md`

## `[!]` El encargo, y su límite

**Medir Java y C# lo suficiente para que dejen de estar descartados por ausencia de datos.
NO elegir.**

De los cinco lenguajes candidatos, tres están medidos (JavaScript/Node, Python, Go) y **dos no
tienen ni una cifra**: `SPK-09` los dejó fuera por falta de cadena de herramientas en el entorno y
`SPK-15` midió la instalación solo en los otros tres. Hoy se descartarían porque nadie los probó,
que es la peor razón posible para descartar algo.

Este spike **no entrega un ganador.** Entrega dos columnas nuevas que quepan en las mismas filas
que ya existen en `IA_SPIKES-resumen.xlsx`. La elección es de Juan y va después.

## `[!]` La compuerta: primero, ¿existe el toolchain?

`java -version` · `javac -version` · `mvn -v` o `gradle -v` · `dotnet --version`. Si no están,
intentar instalarlos y **reportar si la red del entorno lo impide**.

**Este es exactamente el motivo por el que estos dos lenguajes nunca se midieron.** Si vuelve a
pasar, se dice en una línea y se sigue con lo que sí se pueda. **Un número que no se midió no se
estima**: el hueco es un resultado, y una cifra plausible inventada es el peor daño que este spike
puede hacer, porque va a viajar a un ADR.

## Lo que se mide, en orden de lo que más decide

**1 · La huella canónica del JSON.** Reproducir el veredicto sellado de `labs/spk-14` en Java
(Jackson) y C# (`System.Text.Json`). ¿Dan la **misma** huella que Python y Node con claves ordenadas
y sin espacios? `SPK-14` encontró que los dos lenguajes ya medidos daban huellas **distintas** del
mismo veredicto hasta fijar la serialización. Esto cierra el spike abierto `AB-01` en dos lenguajes
más; si alguno no engancha, **el hallazgo es sobre el contrato, no sobre el lenguaje.**

**2 · El camino A2 — ¿pueden compartir el evaluador con el navegador?** C# por Blazor WebAssembly
o .NET wasm AOT; Java por TeaVM, CheerpJ o nada. **Medir el artefacto comprimido y el arranque en
frío.** `SPK-09` dejó A2 con un rango de 4.000× —245 B de piso contra 953 KB del runtime de Go—
porque Rust y TinyGo estaban bloqueados. Esta medición convierte A2 en un camino real o lo mata, y
con ello decide si «el servidor atado al lenguaje del navegador» sigue siendo una objeción.

**3 · La batería de reglas de `SPK-09`.** Las 1.232 camas de `labs/spk-09/bateria/`, contra el
oráculo de `app-captura/src/reglas.ts`. **Divergencia de veredicto y divergencia de motivo por
separado**, y µs por cama. Referencias: 0 % de veredicto en los cinco caminos · 1,95 % de motivo
JS↔Python · 0,43 µs node, 0,20 µs Go, 3,2 µs Python.

**4 · Instalación y huella, comparables con `SPK-15`.** Fases hasta un `/salud` correcto, tamaño del
artefacto (autocontenido contra dependiente de runtime; `native-image` de GraalVM y AOT de .NET
contra JAR/dll) y memoria en reposo. Referencias: 2,474 s node · 2,535 go · 3,897 python ·
artefacto 0,12 / 2,73 / 3,00 MB · reposo 82,3 / 65,8 / 116,4 MB.
`[!]` **Y la pregunta honesta que los otros tres no tenían: ¿el nodo de la finca necesita un JRE o
un runtime de .NET instalado?** `SPK-15` dejó el aprovisionamiento fuera del cronómetro y lo señaló
como «casi seguro el grueso del tiempo real». Esto lo engorda, y hay que decir cuánto.

**5 · Las tres banderas de paridad.** Dos construcciones separadas del mismo código: ¿digest
idéntico? Los JAR llevan fecha dentro y C# tiene `Deterministic`. `SPK-15` demostró que la paridad
es una disciplina de tres banderas y que sin ella **`CT-06` es indemostrable**; hay que saber si en
estos dos es posible y a qué precio.

**6 · El migrador.** Flyway o Liquibase, y EF Core o DbUp, contra la cadena de 40 migraciones de
`labs/spk-15`: ¿un migrador muerto a mitad deja residuo? ¿dos a la vez se pisan? ¿reanuda? Las
garantías que midió `SPK-15` venían del **DDL transaccional de PostgreSQL**, pero el comportamiento
del migrador —una conexión por migración, el registro de versión dentro de la transacción— **es de
la herramienta**, y estas son otras herramientas.

**7 · La cadena de conexión.** `SPK-15` encontró que la forma de palabras clave de libpq **no**
funciona en Node y sí en Go y Python. Comprobar las dos formas en JDBC y en Npgsql, porque es el
tipo de detalle que aparece el día del despliegue.

**8 · Lo que cuesta a dos personas.** Una frase honesta, no una medición: peso de la cadena de
herramientas y qué hace falta saber para mantenerlo. El equipo son **dos personas** y ya sostienen
una aplicación de captura web.

## Qué NO se hace aquí

- **No se vuelve a medir el rendimiento de ingesta.** `SPK-12` ya mostró que el riesgo de esa pieza
  es de corrección, no de velocidad, y que una jornada entera entra en ~1 s en dos lenguajes.
- **No se mide cifrado ni respaldo.** `SPK-13` es independiente del lenguaje y lo dice.
- **No se toca el modelo C4.** Está congelado: manda la versión en la nube de Juan.
- **No se reabre la tecnología del dispositivo de captura.** `ADR-008` la aplaza a propósito.
- **No se escribe ADR** y **no se editan** los cuatro `.xlsx` de drivers ni el libro de ADR. Si hay
  que cambiar algo, se lista el cambio exacto —texto de hoy, texto propuesto, dónde— y lo aplica
  una persona.
- **No se elige lenguaje.** Ni el de este spike ni el del backend.

## Entregable

`IA_RESULTADO.md` en esta carpeta, con la estructura fija de la zona:
**Medición · Lectura · Dependencias · Qué NO probé**, cabecera de cuatro líneas y estado
`SIN REVISAR`. Las cifras tienen que caber en las mismas columnas de las hojas `Spikes` y `Cifras`
de `IA_SPIKES-resumen.xlsx`; si no caben, se dice por qué.

Código y datos a `labs/spk-17/`, **fuera del repo de documentación**. Si el registro pasa de dos
páginas, se parte: la evidencia larga va a `labs/`.

Y **una línea** al final de `IA_BITACORA.md`, sin editar lo anterior.

## Por qué este spike y no otro

`SPK-09` midió lo que cuesta cada camino del motor de reglas y tenía prohibido elegir; lo hizo bien.
Lo que dejó abierto es que **la decisión del lenguaje del backend está lista para tomarse salvo por
dos candidatos sin datos**. Cerrar eso es barato y quita la última excusa para no decidir. Después
de este, la campaña de tecnología no necesita más spikes: necesita las cuatro mediciones abiertas
en rojo de `IA_SPIKES-resumen.xlsx` y las decisiones de la hoja `Para-ADR`.
