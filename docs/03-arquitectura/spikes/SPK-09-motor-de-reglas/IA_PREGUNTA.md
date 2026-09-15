# `SPK-09` · Motor de reglas — explorar lenguajes **sin fijar ninguno**

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 14-sep-2026
> Estado: **SIN REVISAR** · Chat 1 de 3
> Manda sobre esto: `ADR-006`, `ADR-029`, `CT-02`, `ESC-56`, `ESC-57` en `ADR-PoC-Spikes.xlsx` y `DRIVERS_ARQUITECTONICOS.md`

## `[!]` El encargo, y su límite

**Levantar el mapa de caminos posibles y lo que cuesta cada uno. NO elegir.**

Este chat entrega una **matriz de caminos × lenguajes con evidencia medida**. Si termina con un
ganador, incumplió el encargo. La elección es de Juan, y va después.

## Por qué esta es la primera

Es la única pieza del backend que **puede fijar el lenguaje de todo lo demás**. Por eso se explora
antes de que nadie elija: para que la decisión de stack se tome sabiendo qué la restringe, y no al
revés.

Y no es solo técnica: la validación en la cama —rechazar el dato malo donde se captura— es
**el mecanismo que tumba los 8 días** de reconciliación. Si la captura digital produce los mismos
errores del papel, la reconciliación solo se muda de sitio.

## `[!]` La tensión que hay que medir, no resolver

- **`ADR-006`** (Aceptada) dice reglas duras y blandas en **un artefacto versionable**, con el motivo
  del rechazo en lenguaje de negocio, **no código de aplicación** — y en sus consecuencias contempla
  **dos motores independientes**.
- **`CT-02`** exige el **mismo evaluador** en los dos lados, y **`ESC-57`** pide **0% de divergencia**.

Dos motores con cero divergencia es posible, pero es **la restricción técnica más cara del proyecto**:
arrincona el stack y deja un impuesto de mantenimiento permanente. **Medir cuánto cuesta es el trabajo
de este spike. Decidir si se relaja, no.**

## Los cuatro caminos a explorar

Son arquetipos, no productos. Para cada uno: qué lenguajes deja vivos, qué cuesta, qué rompe.

**A · Un solo artefacto compartido.** El evaluador es una biblioteca que corre literalmente en los
dos lados. Implica lenguaje común. Hoy realistas: TypeScript/JavaScript (la captura es web por
`ADR-008`), o algo que compile a WebAssembly y se consuma desde el navegador y desde el servidor
(Rust, Go, C#). **Explora ambos sub-caminos.**

**B · Reglas como datos + evaluadores estándar.** El artefacto versionado es la regla; los evaluadores
son dos implementaciones de una misma especificación. A mirar: JSON Logic, CEL, Rego/OPA, JSONata.
Aquí el lenguaje del servidor queda casi libre.

**C · Dos implementaciones propias + batería de casos dorados como contrato.** Máxima libertad de
lenguaje, máximo impuesto de mantenimiento. Es lo que `ADR-006` parece contemplar.

**D · Solo servidor.** Bajar la promesa a «las reglas duras se evalúan en el dispositivo desde el
paquete versionado y el servidor es la autoridad que revalida todo». **Nómbralo y cuantifícalo**
—es el camino que abarata el proyecto— pero **no lo propongas como decisión**: cambiaría `ESC-57`,
y eso es de Juan.

## Qué se mide

Punto de partida: **`reglas.v1.json` ya existe** en `app-captura/` (`PoC-0`). De ahí sale la batería.

1. **Divergencia.** Batería de casos dorados corriendo en las dos instancias de cada camino. El
   objetivo escrito es 0%. **Reporta el número, no la impresión.**
2. **Tiempo de evaluación en el dispositivo.** No puede hacer esperar a quien captura: el piso de
   adopción es que capturar una cama no se sienta más lento que el papel.
3. **Tamaño del artefacto que baja al dispositivo.** `ADR-029`: el paquete es uno solo, con un número
   de versión. `ADR-015`: se verifica **antes de salir al cultivo**, nunca en medio del campo.
4. **Versionar una regla.** Cambiar una regla no puede exigir publicar una versión nueva de la
   aplicación — es requisito escrito del cliente en `ADR-006`.
5. **El motivo del rechazo en lenguaje de negocio** (`ESC-56`), sin escribir código por regla.
6. **Qué lenguajes de servidor deja vivos cada camino.** Esta es la columna que le importa a Juan.

## Entregable

`IA_RESULTADO.md` con la matriz **caminos × (divergencia · tiempo · tamaño · versionado · motivo ·
lenguajes vivos)**, más el costo de mantenimiento de cada uno en una frase. Sin ganador.

## Qué NO se decide aquí

El lenguaje del backend · la tecnología del dispositivo (`ADR-008` la aplazó) · **el contenido de las
reglas** (es del cliente, `D1`, y nunca se trabajó el proceso de captura) · si se relaja `ESC-57`.
