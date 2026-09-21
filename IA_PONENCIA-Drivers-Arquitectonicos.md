# FlorLogic — Ponencia de drivers arquitectónicos · guion diapositiva por diapositiva

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 21-sep-2026, a pedido de Juan
> Estado: **SIN REVISAR** — ni por Juan ni por Jerónimo
> Manda sobre esto: `Documentacion/Drivers-Arquitectonicos/DRIVERS_ARQUITECTONICOS.md` (v2.2) y sus
> cuatro `.xlsx` · `ADR-PoC-Spikes.xlsx` (36 ADR) · `Documentacion/Archivo/Recopilacion/1_VOZ_DEL_CLIENTE.md`
>
> **El mazo de diapositivas vive en Claude** («FlorLogic — Drivers arquitectónicos», 24 láminas,
> editable y descargable como PowerPoint o PDF). Este archivo es su guion: sirve para ensayar sin
> abrir el mazo y para que quien no lo tenga a mano sepa qué va en cada lámina.
>
> Compañeros en la raíz del repo: **`IA_PRESENTACION-FlorLogic.md`** (la versión anterior, centrada en
> el producto), **`IA_PREGUNTAS-Y-RESPUESTAS-FlorLogic.md`** (tablero `X-01`..`X-20` y 12 sesiones de
> práctica) y **`IA_PODCAST-FlorLogic.md`**.

---

## 1 · Qué es esta ponencia y para quién

**Objetivo:** exponer **lo trabajado en `Documentacion/Drivers-Arquitectonicos/`** —la carpeta que
manda sobre el levantamiento— de forma que se entienda el problema, se vea el método y se compre el
proyecto.

**Sirve para dos audiencias sin cambiar las láminas**, que es como se pidió:

| Audiencia | Qué le importa | Dónde se detiene la charla |
|---|---|---|
| **Ingeniero de sistemas · líder de arquitectura** | Que las decisiones estén justificadas y que los riesgos estén nombrados | Láminas 5, 15, 17, 18, 19, 23 |
| **Sustentación académica** | El método: mini QAW, atributos, escenarios de Bass-Clements-Kazman, trazabilidad | Láminas 7, 8, 9, 10, 11, 12, 13, 20 |

**Duración:** 30 minutos · 24 láminas · ~1,2 min por lámina.
**Expositores:** Juan y Jerónimo. **Los dos tienen que poder darla completa.**

**La regla que se anuncia en la lámina 2 y se cumple todo el tiempo:** cada cifra dice de dónde sale
— **[cliente]**, **[equipo]**, **[medido]**, **[supuesto]** o **[no sabemos]**.

---

## 2 · Estructura, en cinco bloques

| Bloque | Láminas | Min | Pregunta que responde |
|---|---|:--:|---|
| **0 · Apertura** | 1–3 | 2 | ¿De qué se trata y por qué debería importarme? |
| **1 · El problema** | 4–6 | 6 | ¿Existe el problema y cuánto cuesta? |
| **2 · El método de drivers** | 7–16 | 11 | ¿Cómo sabemos que eso es lo que hay que construir? |
| **3 · Lo que los drivers decidieron** | 17–20 | 6 | ¿En qué se convirtió todo eso? |
| **4 · Cómo se vende** | 21–22 | 3 | ¿Por qué vale la pena apoyarlo? |
| **5 · Cierre** | 23–24 | 2 | ¿Qué falta y qué les pedimos? |

**Si el tiempo aprieta**, se recorta en este orden: lámina 22 (objeciones, pasa a respaldo de
preguntas), 18 (alternativas), 10 (trade-off). **Nunca se recorta la 23 (los huecos).**

---

## 3 · Guion lámina por lámina

> Cada entrada: **qué muestra** la lámina · **qué se dice** encima · **la trampa** o el dato que hay
> que tener a mano. Las notas del orador ya están dentro del mazo; esto es la versión larga.

### 1 · Portada
De pie, sin leer. «Vamos a mostrar el trabajo de drivers arquitectónicos de FlorLogic: qué problema
encontramos, cómo lo levantamos, en qué decisiones se convirtió y qué todavía no sabemos.» Decir que
el equipo son dos personas.

### 2 · Cómo va a ir la media hora
No leer la tabla. Anunciar los cinco bloques y **la regla de las cifras**. Si preguntan por qué tanta
insistencia en el origen: porque todo el levantamiento descansa en una sola voz.

### 3 · «Certeza de los datos, que se ingresen los datos correctamente»
Leer la frase y callarse un segundo. Es **respuesta escrita** del cliente a *«¿qué haría que el
sistema no valga la pena?»*. Enlazar: esa frase, traducida a atributo, es **Confiabilidad**.

### 4 · Cómo se trabaja hoy
Recorrer las cuatro tarjetas: 1 h/día de lápiz y papel · 4 h/semana de digitación por un practicante
rotativo · 8 días hasta planeación · proyección mensual hecha a mano.
**Trampa `X-01`:** esas son las cifras **escritas por el cliente**. La versión interna de Juan
(«4 h por tanda», «8 días de proyección por correcciones») **no se usa en la charla** hasta
confirmarla con él.

### 5 · Qué le cuesta al negocio
2 % de error · −6 % contra lo cortado · ~8 % comprado a terceros · banda ±10 %.
La frase: *«se vende sobre un número con una semana de viejo y un 2 % de error encima»*.
**Honestidad obligatoria:** solo desaparece por diseño la mitad del 2 % —la de transcripción—; la
otra mitad la ataca la validación en la cama.

### 6 · El reencuadre
Contarlo como error propio corregido: optimizábamos velocidad porque era lo único medible. Hoy la
velocidad es **piso de adopción**. Cerrar con el riesgo real: **el costo lo paga el campo y el
beneficio lo cobra la oficina.**

### 7 · De una entrevista a un escenario *(arranca el bloque de método)*
Señalar el embudo de izquierda a derecha. Insistir en que **ninguna flecha es automática**: entre
caracterización y Top 65 hay una votación; entre Top 65 y escenarios, una reescritura. Aquí se
nombra a **Bass, Clements y Kazman**.

### 8 · La evidencia, y lo que le falta
Las cinco fuentes y las marcas **CIT / INF / ESC**. Luego los tres límites: una sola voz ·
planeación nunca se exploró · él calificó su contexto en 5/10 · ya lo leímos mal dos veces.
**Esta lámina es la que compra credibilidad para todo lo demás.**

### 9 · Los cinco drivers
Señalar la fila 1: **1, 1, 1**. Confiabilidad es el único consenso total. Nombrar las dos
redefiniciones (seguridad de funcionamiento absorbida; trazabilidad → **Capacidad para ser
Auditado** = trazabilidad + cumplimiento). Si preguntan por Escalabilidad en el puesto 10: el
crecimiento es comercial, no arquitectónico.

### 10 · Cliente y arquitectos no ordenan igual
Las dos divergencias con respuesta: **UX** (él 2, nosotros 8) y **auditabilidad** (él 11, luego pidió
trazabilidad «de por vida»). El criterio con el que se rompió un empate: **se prioriza lo que el
usuario final puede medir.**
**Aviso honesto:** la tercera columna «promediada» del Excel no es la media de las otras dos y está
marcada para verificar; dos empates siguen sin romper.

### 11 · Cómo se eligieron las 65
La mecánica: 65 puntos por rol entre 29 preguntas, 5 como máximo por pregunta; desempate por atributo
y por consenso. Y el desajuste que hay que mirar: **«Capacidad para ser Administrado» es segundo por
puntos y sexto del ranking**, porque su presupuesto cae en preguntas que solo vota un actor.

### 12 · Un escenario desarmado (`ESC-02`)
Leer el escenario entero una vez y señalar los seis elementos. La frase: **sin medida de respuesta no
hay escenario**. Formato acordado: dos columnas, ID y párrafo narrativo; el Excel guarda cada
elemento en su propia columna.

### 13 · 65 escenarios por atributo
No leer las doce filas. Señalar la barra de Confiabilidad (18) y decir que **los cinco drivers
concentran 38 de 65**. Si objetan el desbalance: un reparto parejo sería la alarma.

### 14 · Números firmes contra números propuestos
Columna verde: lo que dijo el cliente. Columna naranja: **lo que nos inventamos nosotros** y va
marcado como propuesta. Terminar con los dos casos de traducción: *«no puede haber errores»* no es un
número, y *«24×7»* no es 99,99 %.

### 15 · Dos preguntas de un punto *(la lámina que levanta la charla)*
Las dos que quedaron **fuera** del Top 65 y cambiaron el proyecto: la de instalar en servidores
propios —derogó el SaaS— y la del **Power BI** —derogó tres decisiones de interoperabilidad—.
Moraleja: **la ponderación por puntos no es importancia arquitectónica.**

### 16 · Las restricciones que cierran el diseño
Tres oscuras (offline-first · datos no columnas · el sistema heredado bloqueante) y tres claras
(tiempo y plata · secreto empresarial, art. 260 Decisión 486 CAN · **arquitectos sin experiencia en el
sector**, escrita por nosotros mismos). De las 29 técnicas, **seis siguen en duda**.

### 17 · De SaaS a local-first *(arranca el bloque de decisiones)*
Las cinco razones de la caída del SaaS, empezando por la única suficiente: **el cliente necesita
operar sin internet**. Luego lo vigente: instalación por empresa, ~20.000 USD + 100–200 USD/mes
(cifra nuestra, sin costear). Confesar la lección de método.
**Si preguntan «¿pero él no pidió nube?»:** sí, en la sesión 1; también dijo que hay que seguir
trabajando sí o sí y que no hay señal. Y local-first **sí exige** un ingeniero de sistemas en la
finca: nuestro modelo lo asume.

### 18 · Cuatro alternativas
Mostrar que la evaluación se hizo contra restricciones escritas. La tentadora era el backend
gestionado —se cae porque el plano de datos viviría fuera de la finca—; la parecida era código por
finca —la novena finca sería el noveno proyecto—.

### 19 · Los cinco mecanismos
Nombrarlos, no explicarlos. Si hay tiempo, solo **M2**: la bitácora no es una tabla aparte, **es el
modelo de datos**. Si preguntan por conflictos, contar la medición del reloj adelantado.

### 20 · 71 contradicciones
Los cinco grupos, 53 cerradas y **18 abiertas, todas del grupo D**. Las reglas del archivo: no se
borra nada, los identificadores no se renumeran, se puede reconstruir el porqué de cualquier
decisión. Y las **siete decisiones tomadas contra el cliente**, que van a la sesión con él.

### 21 · Tres argumentos de venta
Dolor cuantificado por el propio cliente · decisiones rastreables hasta una frase · riesgo acotado y
dicho en voz alta. La frase de cierre comercial, tal cual: *«hoy usted vende sobre un número de hace
ocho días; nosotros le entregamos ese número el mismo día, y le decimos de qué cama salió»*.
**Lo que nunca se promete:** cero errores, cero pérdida absoluta, ni una tecnología de dispositivo
que no está decidida.

### 22 · Objeciones
Seis objeciones con su respuesta. Se puede saltar y dejar como respaldo para preguntas.

### 23 · Los huecos, con nombre y con dueño
Tres del cliente (el % de productividad · el proceso de captura y qué es «la jornada» · el sistema
heredado) y dos del equipo (el cierre de producción · la pregunta del motivo, que arrastra el
lenguaje del backend). Cerrar con **la jugada**: pedirle *«muéstranos la proyección del último ciclo
con el archivo con el que la hiciste»*.

### 24 · Cierre
Tres frases de resumen y tres cosas que se le piden a quien escucha. Terminar con la frase del
cliente y quedarse callado. **No decir «¿alguna pregunta?»**, decir **«¿por dónde quieren que
profundicemos?»**.

---

## 4 · Cómo se vende, en corto

Para no perderlo entre láminas, los tres argumentos y su orden:

1. **El problema no hay que venderlo: ya está cuantificado por quien lo sufre.** 8 días, 4 h
   semanales, 2 %, −6 %, ~8 %. Se entra por ahí siempre.
2. **Lo que se vende es el rastro de decisiones**, no la idea: 65 escenarios con medida, 38
   restricciones, 36 decisiones registradas, 71 contradicciones trabajadas. Un comprador técnico paga
   por eso, porque es lo que hace mantenible el producto en N fincas.
3. **El riesgo se enseña, no se esconde.** Prototipo desechable ya construido, siete spikes medidos,
   y una lista de huecos con dueño. Enseñar los huecos es lo que hace creíble el resto.

**Y el marco económico, cuando toque:** ~20.000 USD por instalación —el presupuesto que puso el
cliente— más 100–200 USD/mes de servicios, **cifra propia y sin costear**, contra 4 horas semanales
de digitación y una proyección que hoy llega ocho días tarde.

---

## 5 · Antes de exponer

1. **Cerrar las contradicciones `X-01`, `X-12` y `X-07`** del banco de preguntas: las cifras del
   dolor, la numeración de los ADR y «pérdida cero» contra recaptura. Son las tres que pueden
   contradecirse en vivo entre los dos expositores.
2. **Repartir las láminas**: acordar quién lleva cada bloque y quién responde qué en preguntas.
3. **Ensayar con el banco** (`IA_PREGUNTAS-Y-RESPUESTAS-FlorLogic.md`, sesiones `S1` a `S12`): uno
   pregunta, el otro responde sin mirar.
4. **Revisar el mazo y quitarle la cabecera de IA a este archivo** cuando los dos lo hayan leído.
