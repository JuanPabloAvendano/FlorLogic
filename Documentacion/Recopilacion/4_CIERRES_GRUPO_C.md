# 4 · Cierres propuestos — Grupo C

> **Qué es.** Las 10 contradicciones del equipo contra sí mismo, con un cierre propuesto para cada
> una. **Nada de esto está aplicado todavía**: es la hoja de ratificación. Cuando Juan (y Jerónimo)
> confirmen, cada cierre aceptado entra en `CONTEXTO.md` como `DEC-nn` nueva y se marca resuelto en
> `3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md`.
>
> **Qué queda por ratificar: tres.** `C4`, `C6` y `C8`. Las otras siete se cerraron en las rondas de
> `3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md` y su justificación completa vive allí, no aquí.
>
> `[!]` **`C4` es la más urgente y quedó sin respuesta en la ronda 4.** La extensibilidad del esquema
> hay que fijarla **antes de la primera tabla**: con `N` instalaciones ya desplegadas en casa de
> clientes, cambiarla después es carísimo.
>
> **Formato de cada entrada.** Situación · Opciones · **Cierre propuesto** · Por qué · **El
> contraargumento más fuerte contra mi propia propuesta** (regla `§16.6`) · Qué arrastra.
>
> **Versión 3.0** · 25-ago-2026 — rehecha tras **descartar el SaaS por inviable**.
>
> 🔴 **El SaaS multi-tenant queda descartado.** El modelo vigente es **local-first con servicios en
> línea**: instalación por empresa (~20.000 USD) más mensualidad de servicios gestionados. Las
> propuestas de la v2.0 para `C5`, `C7` y `C9` **se escribieron sobre el SaaS y ya no aplican** —
> las tres se resolvieron de otro modo y están cerradas en `3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md` v6.0.

---

## Estado de las 10

| ID | Tema | Estado | Cómo quedó |
|---|---|---|---|
| **C4** | **Extensibilidad del esquema y ranking** | 🔴 **ABIERTA — la más urgente** | Ranking resuelto. **Falta el esquema y el empate Interoperatividad/Escalabilidad** |
| **C6** | `RF-001`/`RF-002` | 🟠 **ABIERTA** | Propuesta abajo: capturar `#líneas` y `cantidad`, sin derivar |
| **C8** | El 2% | 🟠 **ABIERTA** | Propuesta abajo: separar los dos conceptos; el número se pregunta |
| C1 | BI | ✅ Resuelta por `B5` | BI propio **+** integración. `DEC-06` derogada. Power BI conecta contra la instalación local |
| C2 | IA | ✅ Resuelta en ronda 4 | **Vuelve como asistente de captura**, entrenada en el entorno del cliente. Falta **dónde corre el modelo** |
| C3 | Modelo de entrega | ✅ Resuelta en ronda 3 | **Local-first.** `DEC-01` derogada, SaaS descartado por inviable |
| C5 | Seguridad + `CN-28` | ✅ Resuelta por `B4` | **La llave la tiene el cliente.** `CN-28` sale de `EN DUDA` |
| C7 | Suscripción | ✅ Resuelta por `B6` | **Cobro por instalación**, no por usuario. `CN-05` resuelta en su unidad |
| C9 | Operador | ✅ Resuelta por `B6` | El rol se encoge. **Residuo:** cláusula de acceso de implantación |
| C10 | Accesibilidad | ✅ Resuelta por `A18` | Accesibilidad = **limitantes físicas**; la baja alfabetización digital va a usabilidad |

**Los cinco drivers, según `2. Priorización-QA`:** Confiabilidad (3) · Disponibilidad (11) ·
Rendimiento (17) · Capacidad para ser Auditado (17) · Capacidad (19).
**Escalabilidad queda en 25, puesto 9-10.**

---


# `C4` · Escalabilidad — **RESUELTA en cuanto al ranking**

### Situación

En S3 la pusimos casi última: *«no es un proyecto que se estime para crecer […] porque no va a ser un
software as a service.»* **Ese argumento queda retirado: sí va a ser un software as a service.**

### Resuelto

> Escalabilidad queda donde la deja la hoja **`2. Priorización-QA`**: **25 puntos, puesto 9-10.**
> Sube respecto a donde la dejó S3 y **queda fuera del top 5**, porque los cinco drivers se eligen
> por lo que **el usuario final puede medir**, y la escalabilidad se nota cuando falta y nunca
> cuando está.
>
> Los cinco drivers son: **Confiabilidad · Disponibilidad · Rendimiento · Capacidad para ser
> Auditado · Capacidad.**

### Lo que sigue abierto de `C4`, y no es el ranking

**1 · El empate `Interoperatividad / Escalabilidad` (25 y 25) sigue sin romper.**

| Criterio | Gana |
|---|---|
| Patrón de respuestas, como manda `§9.1` | **Escalabilidad** — 11 SÍ de 11, contra 10 SÍ y 3 NO |
| «Medible por el usuario final», el criterio que acaba de fijarse | **Interoperatividad** — el usuario toca Excel, PDF y Power BI; la escalabilidad no la ve |

**Propuesta: romperlo a favor de Interoperatividad**, por coherencia con el criterio que se acaba de
adoptar para elegir drivers, y porque el cliente lo respaldó con una nota literal (**«POWER BI»**).

⚠ **Contraargumento:** `§9.1` dice explícitamente que los empates *«se rompen con el patrón de
respuestas de la caracterización, no con otra encuesta de ordenamiento»*. Romperlo con un criterio
distinto es cambiar la regla después de ver el resultado — exactamente lo que llevó a `DEC-03` a
descartar los cuatro rankings anteriores.

**2 · La extensibilidad del esquema no es negociable aunque el atributo no sea driver.** El cliente
dijo **SÍ** a *«que agregar un tipo de labor o de medición nueva no exija rehacer la captura
existente»*. Eso es diseño de esquema y hay que decidirlo **antes de la primera tabla**.

**3 · La «Votación por rol» sigue con una sola celda diligenciada.** Decidir si se completa o se
descarta — y ahora hay un motivo nuevo para completarla: falta el cuarto actor (`E5`).

**RATIFICACIÓN:** ☐ Romper el empate a favor de Interoperatividad ☐ A favor de Escalabilidad (patrón) ☐ Dejar el empate ☐ Prefiero

---

> ### 🔄 ESTADO tras las rondas 1-4
>
> **El ranking ya está resuelto:** Escalabilidad queda en 25, puesto 9-10, fuera del top 5 —
> los drivers se eligen por lo que el usuario final puede medir. Y **`B9` quitó el punto ciego**:
> con local-first el crecimiento es comercial, no arquitectónico.
>
> **Lo que sigue abierto de `C4`, y es lo que hay que ratificar:**
>
> 1. 🔴 **La extensibilidad del esquema.** El cliente dijo **SÍ** a *«que agregar un tipo de labor o de
>    medición nueva no exija rehacer la captura existente»*. Eso es **diseño de datos y hay que
>    decidirlo antes de la primera tabla** — con `N` instalaciones ya en casa de clientes, cambiarlo
>    después es carísimo. **Quedó sin respuesta en la ronda 4.**
> 2. **El empate `Interoperatividad / Escalabilidad` (25 y 25).** Por patrón de respuestas gana
>    Escalabilidad (11 SÍ de 11 contra 10 SÍ y 3 NO); por «medible por el usuario final» gana
>    Interoperatividad. Sin decidir.
> 3. **La «Votación por rol»** sigue con una sola celda diligenciada. Con `E5`, ya sabemos que **no
>    hace falta una cuarta columna** para el operador.


---

# `C6` · `RF-001` y `RF-002` están escritos sobre un modelo que ya invalidamos

### Situación

Ambos hablan de *«cantidad de esquejes»*. `DEC-14` dice **«nada se cuenta por esqueje»**. Y `§4.3`,
leyendo el formato real, encontró que la unidad de campo no es el área ni el esqueje: **son las
líneas.** El formato **no tiene ninguna columna de área ni de densidad** — tiene `#LÍNEAS` y
`CANTIDAD`, las dos.

### Opciones

- (a) Reescribirlos sobre `plantas_sección = #líneas × plantas_por_línea(variedad)`.
- (b) Reescribirlos capturando **los dos campos que el formato ya tiene**, sin derivar nada.

### Cierre propuesto — **(b)**

> **`RF-001`** — *Registrar la siembra de una **sección de cama** (bloque, cama, sección, variedad,
> fecha, **número de líneas** y **cantidad**) sin conexión, confirmando en pantalla que quedó
> guardado.* **El sistema almacena los dos valores tal como se capturan y no deriva ninguno del
> otro.**
>
> **`RF-002`** — *Registrar el corte por **sección de cama**, variedad, grado y fecha sin conexión,
> aceptando varios registros para la misma cama en días distintos.*
>
> La razón `cantidad / #líneas` se calcula **solo para validar** (`RG` de la demo), nunca para
> sustituir un dato capturado.

### Por qué

(a) es tentador y es lo que `§5.1` propone, pero **cambia un error conocido por otro**:
`plantas_por_línea` es un parámetro que no tenemos: `D8` dice que **no sabemos qué es una línea**, y
en S4 quedó grabado *«esa me la tienes que explicar»*. Escribir un requisito sobre una fórmula cuyo
término central está sin definir es repetir exactamente lo que hicimos con los esquejes.

(b) tiene una virtud que (a) no: **es lo que el papel ya hace.** El supervisor escribe las dos cosas.
Capturar las dos y validar su razón es más barato, no pierde información y **hace que la app sea una
transcripción fiel del formato**, que es justo lo que el cliente pidió (*SÍ* a *«una plantilla
predefinida que use una estructura similar a las plantillas de papel»*).

### ⚠ El contraargumento más fuerte contra esta propuesta

**Guardar dos campos que deberían ser uno es admitir redundancia en el modelo de datos.** Si `D8` se
responde y resulta que `plantas_por_línea` es constante y conocida, tendremos una columna derivable
almacenada, con el clásico riesgo de que las dos se desincronicen tras una corrección — y el cliente
dijo **NO** a conservar el valor original, así que no habrá con qué reconstruir cuál era la buena.

**Mitigación si se acepta (b):** declarar cuál de los dos manda ante discrepancia. Y eso hay que
preguntárselo al cliente — se suma a `D8`.

### Qué arrastra

`RF-001` y `RF-002` reescritos · `§5.1` se matiza · `RFP-01` se absorbe en `RF-001` · añade una
pregunta a `D8`.

**RATIFICACIÓN:** ☐ Acepto (b) ☐ Prefiero (a) ☐ Prefiero ☐ Dejar abierta

---

---

---

# `C8` · El umbral del 2% — un número para dos conceptos

### Situación

Estamos usando **2%** para dos cosas que no tienen nada que ver:

| | Qué es | De dónde sale |
|---|---|---|
| **El 2% del cliente** | Su **error de captura declarado**, sobre el total de la información que llega a la proyección. Meta: 0% | S2, cita textual |
| **El 2% de la demo** | Una **tolerancia sobre la razón `cantidad / #líneas`** para marcar filas sospechosas | **Decisión nuestra.** `§4.3` lo dice: *«el umbral del 2% es una decisión nuestra, no un hallazgo»* |

Con ese umbral la demo marca **9 filas** del histórico real como error.

### Cierre propuesto

> **1. Se separan y se renombran.** El de la demo deja de llamarse «2%» y pasa a ser **`tolerancia de
> razón`**, parámetro configurable por variedad, como manda `RF-013`.
> **2. El valor NO se fija ahora.** Se deja provisional y **declarado como supuesto del equipo** en
> todo sitio donde aparezca.
> **3. Se añade a las preguntas del cliente** (`D18`), junto con `D17` (si `Astroi` y `Rose` son
> errores de digitación o densidades distintas).
> **4. Regla de presentación, y es la que más importa:** si se le enseña la demo al cliente, **las 9
> filas marcadas se presentan como "filas que llaman la atención con un criterio que pusimos
> nosotros", nunca como errores detectados.**

### Por qué

Los datos reales no respaldan un 2%. Las razones limpias son 19,0 (Cremón) y 15,0 (Matsomoto), y las
desviaciones observadas van de **11,19 a 24,68** — dispersión de ±30% y más. Un umbral del 2% no está
calibrado con nada: está puesto a ojo. Y `Astroi` y `Rose` tienen **tres razones distintas cada una,
la misma variedad y el mismo día**, lo que puede significar error de captura **o** que la línea no
mide lo mismo en toda la cama. **No lo sabemos, y ese es justamente el punto.**

### ⚠ El contraargumento más fuerte contra esta propuesta

**Dejar el número abierto significa que la demo sigue funcionando con un criterio inventado.** Y la
demo ya existe, ya corre y ya marca. Un parámetro «provisional» que nadie vuelve a tocar es un
parámetro definitivo con mala conciencia. Si de verdad no sabemos calibrarlo, la opción disciplinada
no es dejarlo en 2%: es **apagar la regla de razón** hasta que `D8`, `D17` y `D18` se respondan, y
dejar solo las reglas duras que el cliente sí respaldó (`tallos ≤ plantas`, rango imposible,
desviación histórica).

### Qué arrastra

`app-captura/configuracion/reglas.v1.json` · `§4.3` · `ESC-001` y `ESC-002` · añade `D17` y `D18` a
la hoja de sesión.

**RATIFICACIÓN:** ☐ Acepto ☐ Prefiero apagar la regla de razón hasta preguntar ☐ Prefiero ☐ Dejar abierta

---

---

---

## Qué pasa cuando ratifiques

0. **Siete de las diez ya están cerradas** en `3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md` v6.0. Aquí solo quedan
   `C4`, `C6` y `C8`.
1. Cada cierre aceptado entra en `CONTEXTO.md` como **`DEC-17`, `DEC-18`…** con su justificación y su
   contraargumento — no solo la conclusión.
2. Las `DEC-nn` derogadas (`DEC-06`) o reescritas (`DEC-01`, `DEC-09`, `DEC-16`) se marcan **sin
   borrarlas**: se conserva qué decían y por qué cambiaron.
3. `3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md` marca las entradas de C como **CERRADAS**, con el número de `DEC`
   que las cierra.
4. `2_VOZ_DEL_EQUIPO.md` se actualiza.
5. Se anota lo que estos cierres **añaden** a la lista de preguntas del cliente: `D8` gana la pregunta
   de qué campo manda · `D17` y `D18` suben de prioridad · **`A18` pasa a obligatoria**.

**Ninguna de las brechas de los grupos A, B, D y E se toca en esta pasada.**
El estado actualizado de las 71 entradas está en `3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md` v2.0.
