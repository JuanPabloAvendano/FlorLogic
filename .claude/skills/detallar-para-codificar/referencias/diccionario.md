# Diccionario del proyecto

Un solo archivo por proyecto con los términos que la skill usa al explicar, de **negocio** y **técnicos**.
Existe porque en un mismo detallamiento aparecen palabras parecidas con objetivos distintos, y una
palabra mal entendida produce decisiones que parecen acordadas sin estarlo. Por ejemplo, «lote» se usó
para el grupo de anotaciones que sube un celular, cuando en el negocio es una plantación.

## 1 · Dónde vive

- Uno por proyecto, **no** uno por ESPEC: lo comparten todos los detallamientos.
- Antes de crearlo, busca uno que ya exista (`glosario`, `diccionario`, `lenguaje ubicuo`) en todas las
  ramas y worktrees. Si existe en una carpeta humana, se lee como fuente y los términos nuevos van al
  archivo de la skill, citándolo.
- Si no existe, propón crearlo en la raíz del código con las convenciones del proyecto para archivos de
  la IA. En FlorLogic: `.claude/contexto/IA_DICCIONARIO.md`, con la cabecera de cuatro líneas.
- La ficha de cada ESPEC enlaza al diccionario.

## 2 · Forma de cada entrada

```markdown
### Lote · negocio
- **Es:** una plantación de una variedad, en un periodo con fechas reales, que puede ocupar varias secciones.
- **No es / se confunde con:** [Partición](#particion--tecnico), que es un trozo de lo que sube un celular.
- **Ejemplo:** «el lote de rosa Freedom sembrado en marzo, en las secciones 3 y 4».
- **Fuente:** ADR-024 §2 · ESPEC-contrato-ingesta A-30
- **Estado:** ACORDADO 16-oct-2026
```

- **Tipo:** `negocio` (lo que dice la gente de la finca o del negocio) o `técnico` (lo que existe por cómo
  se construye el software). Si la misma palabra tiene las dos lecturas, son **dos entradas**, una por
  tipo, y cada una nombra a la otra en «No es».
- **Es:** una frase, de 15 a 30 palabras, sin jerga que no esté también en el diccionario.
- **No es / se confunde con:** obligatorio cuando hay una palabra parecida. Es el campo que evita la
  confusión; nunca se omite por ser obvio.
- **Estados:** `PROPUESTO` (lo escribió el agente y el usuario no lo ha confirmado) · `ACORDADO <fecha>`
  (el usuario respondió «Es lo mismo») · `REEMPLAZADO <fecha> · ver <término>` (la entrada vieja queda,
  para entender ESPEC viejas).
- Orden alfabético. Al final, una tabla corta de **pares que se confunden** (término · término · la
  diferencia en una línea).

## 3 · Cuándo se actualiza

- **Al explicar algo con un término sin entrada:** se crea la entrada `PROPUESTO` antes de usarla.
- **Cuando el usuario corrige un significado** o una respuesta cambia lo que una palabra quiere decir:
  se actualiza la entrada en el mismo turno, se busca la palabra en la ESPEC y se corrigen los usos
  viejos.
- **Si un término central cambia el alcance**, se hace la convergencia de la etapa 1 en corto y la
  entrada pasa a `ACORDADO`.
- **Si una explicación usa una palabra con un sentido distinto al de su entrada**, es una
  contradicción: se para, se pregunta cuál vale y se anota en la ESPEC §6. No se sigue con las dos
  lecturas abiertas.

## 4 · Términos que acompañan una explicación

Toda explicación de la skill (el contexto de una pregunta, el mapa de la etapa 2, las respuestas de la
revisión, la definición de la etapa 1) lleva al final un bloque **Términos**.

- **Qué entra:** las palabras que aparecen en esa explicación, en sus opciones o en su ejemplo, y que
  tienen un significado específico dentro de ella. Entran:
  - los términos del negocio del proyecto (lote, cama, sección);
  - los términos técnicos que nombran algo de este diseño (partición, `confirmado_hasta`, puerto de
    salida);
  - toda palabra que tenga una pareja parecida en el diccionario.

  No entran las palabras de uso general que significan lo de siempre.
- **De dónde sale:** se copia de «Es» en el diccionario, sin parafrasear, para que la misma palabra se
  defina siempre igual. Si tiene pareja, se añade «No es: <pareja>». Un término recién creado se marca
  `(nuevo)`.
- **Tamaño:** el bloque **no cuenta** para el tamaño pequeño o mediano. El contexto se mide solo, y
  tiene que entenderse aunque se lean las definiciones aparte. Normalmente son de 1 a 4 términos. Si
  hacen falta más de 6, la explicación probablemente mezcla temas: divídela.
  - **todo código** (`A-45`, `K-80`, `D-04`, `ESC-38`, `D-014`, `S1`…), aunque sea solo un dato de
    ejemplo: va con su significado en 5 a 10 palabras (§5).
- **Forma:**
  ```
  Términos
  · Lote (negocio): una plantación de una variedad, en un periodo con fechas reales. No es: partición.
  · Partición (técnico): un trozo de lo que un celular tiene pendiente, que sube en un POST. (nuevo)
  · A-45 (decisión de la ESPEC): constancia de la anotación obsoleta; no se borra.
  · D-014 (ejemplo): un celular inventado para los casos C-n.
  ```

## 5 · Nomenclatura: los códigos

Todo código que aparece en una ESPEC, una semilla, un destilado o una explicación de la skill tiene
entrada en la sección **Nomenclatura** del diccionario. Sin excepción: los ids de la ESPEC, las reglas
de la skill, las citas a fuentes y también los datos de ejemplo. Nadie tiene que adivinar qué es un
código.

- **Dos niveles de entrada:**
  - **Familia** (el prefijo, p. ej. `A-n`, `ESC-n`, `D-0nn`): qué es, en 5 a 10 palabras; dónde vive
    (archivo y sección); a qué llega en el código o en la documentación; y quién lo crea (la skill, una
    fuente, un ejemplo).
  - **Código suelto** (p. ej. `D-04`, `ESC-38`, `D-014`): su significado en 5 a 10 palabras, su
    familia y su estado.
- **Los ids propios de la ESPEC** (`A-n`, `C-n`, `K-n`, `COH-n`, `OBS-n`) solo tienen entrada de
  familia. Su significado suelto es la primera frase de su fila en la ESPEC: escríbela de modo que
  sirva sola como resumen de 5 a 10 palabras. Así no se copian cientos de filas que caducan.
- **Forma de la tabla:**

  ```markdown
  | Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega | Familia · origen | Estado |
  |---|---|---|---|---|
  | D-04 | Regla de la skill: revisión de coherencia obligatoria | DECISIONES.md de la skill; bloquea el 85 % | D-0n · skill | PROPUESTO |
  ```

- **Forma compacta (la que usa FlorLogic, decidida por Juan el 18-oct):** tres columnas, Código · Significa · Uso. La familia y el origen los da el subtítulo de la tabla, y el estado se escribe una vez arriba («todas nacen PROPUESTO»). Solo una fila que se aparta de ese estado lo dice en «Uso».
- **Los ids ya usados se mantienen.** No se renombran, aunque su prefijo choque con otro (`D-04` de la
  skill y `D-014` de ejemplo siguen como están). El choque se documenta en «Prefijos que chocan», con
  la forma de distinguirlos.
- **Misma familia, especificación parecida, aunque no siempre igual.** Los códigos de una familia o
  categoría se explican con el mismo patrón: la misma etiqueta inicial («Regla de la skill:»,
  «ADR:», «Escenario:», «Ejemplo:») y el mismo tipo de dato en «Uso». El contenido cambia con cada
  código. Si uno se aparta del patrón de su familia (otro formato, otro dueño, otro sentido), tiene su
  propia fila, y esa fila dice en qué se aparta.
- **Antes de crear un código o un prefijo nuevo:**
  1. Busca en la Nomenclatura que el prefijo no exista ya con otro sentido (por ejemplo, `P` ya es
     pregunta de ronda, pregunta a Juan y producción de ejemplo). Si existe y es la misma familia, el
     código nuevo sigue su patrón. Si choca con otra familia, el código nuevo usa otro prefijo; los ya
     usados no se tocan.
  2. Crea su entrada `PROPUESTO`, con significado y uso, **en el mismo turno y antes de usarlo**.
  3. Al usarlo por primera vez en una explicación, va en el bloque Términos con su significado y la
     marca `(nuevo)`.
- **Los códigos de ejemplo** se registran igual. Su significado empieza con «Ejemplo:» y nombra la
  entidad que representan (celular, sesión, huella, sección).
- Si un código cambia de sentido, su entrada pasa a `REEMPLAZADO <fecha> · ver <código>`. Los ids
  viejos de las HISTORIAS se siguen entendiendo.
