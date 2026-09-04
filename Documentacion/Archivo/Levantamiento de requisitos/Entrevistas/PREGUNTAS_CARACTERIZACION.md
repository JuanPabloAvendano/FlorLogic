# FlorLogic — Preguntas de caracterización por atributo de calidad

**Versión 3 · 27-ago-2026** · reemplaza la v2 del 21-ago-2026
**Precede a:** los escenarios de calidad `ESC-01`..`ESC-65`, hoy en `Documentacion/Drivers-Arquitectonicos/EscenariosCalidad.xlsx` y explicados en `DRIVERS_ARQUITECTONICOS.md §9`

## Qué cambió frente a la v2

- **Cada pregunta se escribe ahora como escenario, no como funcionalidad.** Las 262 preguntas conservan su identificador, su orden, su bloque y su objetivo; lo que cambia es la forma: cada una contiene los **seis elementos** del modelo de Bass/Clements/Kazman — **entorno, fuente del estímulo, estímulo, artefacto, respuesta y medida de respuesta** — en ese orden y dentro de una sola frase interrogativa, igual que el párrafo narrativo acordado para los `ESC-nnn`.
- **No se agregó ni se quitó ninguna pregunta.** Siguen siendo 262 sobre 13 atributos, más 3 de cierre. Ninguna cambió de intención: lo que antes se preguntaba como *«¿se necesita que el sistema haga X?»* ahora se pregunta como *«en tal situación, ante tal estímulo, ¿se necesita que tal artefacto responda X con tal medida?»*.
- **La medida de respuesta sale del propio escalón de la fila**, no de una cifra nueva. Donde la fila ya traía un umbral (menos de treinta segundos, tres días sin sincronizar, cinco años) ese umbral es la medida. Donde el escalón es cualitativo (por sección, por cama, por bloque, por finca), la medida es ese nivel de agregación. **No se inventó ningún número.**
- **Sobre roles:** la regla de la v2 sigue vigente — no se pregunta *quién* tiene la necesidad. Pero un escenario exige una **fuente del estímulo**, así que se nombra de forma genérica y estructural (*quien captura*, *quien consulta*, *un auditor externo*, *el dispositivo*, *el servicio en la nube*), nunca con los tres roles del mini QAW. Si al responder el cliente atribuye la necesidad a alguien en particular, eso se anota: es un hallazgo, no parte de la pregunta.
- El ranking, los prefijos y el escalonamiento **no se tocaron**. `ESL-` sigue siendo Escalabilidad; `ESC-` sigue reservado para los escenarios.

## Ranking del mini QAW

| # | Atributo | Supervisor de campo | Gerente de producción | Administrador del sistema | Total |
|---|---|:--:|:--:|:--:|:--:|
| 1 | Confiabilidad | 1 | 1 | 1 | **3** |
| 2 | Disponibilidad | 2 | 2 | 7 | **11** |
| 3 | Rendimiento | 3 | 5 | 9 | **17** |
| 4 | Capacidad para ser Auditado | 9 | 4 | 4 | **17** |
| 5 | Capacidad | 11 | 3 | 5 | **19** |
| 6 | Capacidad para ser Administrado | 13 | 7 | 3 | **23** |
| 7 | Experiencia de Usuario | 6 | 6 | 12 | **24** |
| 8 | Seguridad | 10 | 8 | 6 | **24** |
| 9 | Interoperatividad | 5 | 10 | 10 | **25** |
| 10 | Escalabilidad | 8 | 9 | 8 | **25** |
| 11 | Capacidad para ser Soportado | 12 | 13 | 2 | **27** |
| 12 | Portabilidad | 4 | 11 | 13 | **28** |
| 13 | Accesibilidad | 7 | 12 | 11 | **30** |

Menor puntuación = mayor prioridad. Hay empates reales: Rendimiento y Capacidad para ser Auditado (17), Experiencia de Usuario y Seguridad (24), Interoperatividad y Escalabilidad (25). El empate no se rompe aquí — **lo rompe el patrón de respuestas de estas preguntas**.

## Cómo se usan

- Todas se responden **Sí / No**. Si una respuesta necesita matiz, va en la columna *Nota*, nunca dentro de la pregunta.
- Están **escalonadas a propósito**: dentro de cada bloque las preguntas recorren la misma idea de menor a mayor exigencia (dato → sección → cama → bloque → finca; en el momento → al sincronizar → después). El punto donde el cliente pasa de «Sí» a «No» **es la medida de respuesta** del escenario que se va a escribir. Por eso el orden de las filas no se altera.
- Cada pregunta ya trae su propia medida dentro del texto. Al responder «Sí», esa medida queda ratificada y el escenario `ESC-nnn` se puede escribir directamente desde la fila; al responder «No», la fila anterior de la escalera es la que fija la medida.
- Un «Sí» no obliga a escribir un escenario; obliga a decidir. Un «No» **también sirve**: cierra alcance y justifica descartes.

---

## 1. Confiabilidad

*Que la información sea correcta, que el sistema no entregue resultados equivocados y que nada se pierda cuando algo falla. Absorbe lo que antes se llamaba Seguridad de funcionamiento.*  
*Puntuación mini QAW: 3 · 57 preguntas*

### 1.1 Verificación en el momento de la captura

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-01 | En plena jornada de captura dentro del bloque y sin conexión de datos, cuando quien captura ingresa un dato en la aplicación de captura, ¿se necesita que la aplicación verifique que ese dato sea correcto de manera individual y se lo confirme en el momento, dato por dato, antes de admitir el siguiente? | | |
| CNF-02 | En plena jornada de captura dentro del bloque y sin conexión de datos, cuando quien captura termina de registrar lo correspondiente a una variedad, ¿se necesita que la aplicación de captura verifique que la información sea correcta agrupándola por variedad y se lo confirme antes de pasar a la variedad siguiente? | | |
| CNF-03 | En plena jornada de captura dentro del bloque y sin conexión de datos, cuando quien captura termina de registrar una sección de la cama, ¿se necesita que la aplicación de captura verifique que la información sea correcta agrupándola por sección y se lo confirme antes de pasar a la sección siguiente? | | |
| CNF-04 | En plena jornada de captura dentro del bloque y sin conexión de datos, cuando quien captura termina de registrar una cama completa, ¿se necesita que la aplicación de captura verifique que la información sea correcta agrupándola por cama y se lo confirme antes de pasar a la cama siguiente? | | |
| CNF-05 | En plena jornada de captura dentro del bloque y sin conexión de datos, cuando quien captura termina de registrar un bloque completo, ¿se necesita que la aplicación de captura verifique que la información sea correcta agrupándola por bloque y se lo confirme antes de pasar al bloque siguiente? | | |
| CNF-06 | Al terminar el recorrido de la finca y sin conexión de datos, cuando quien captura cierra la jornada, ¿se necesita que la aplicación de captura verifique que la información sea correcta agrupándola por finca y por jornada completa y se lo confirme antes de dar la jornada por terminada? | | |
| CNF-07 | En plena jornada de captura dentro del bloque, cuando quien captura da por terminado un registro, ¿se necesita que la aplicación de captura muestre una confirmación final con todo lo capturado y espere una aprobación explícita, de modo que nada quede guardado sin haber pasado por esa confirmación? | | |
| CNF-08 | En plena jornada de captura dentro del bloque, cuando quien captura intenta cerrar una cama con campos sin diligenciar, ¿se necesita que la aplicación de captura se lo impida y señale los campos faltantes, de modo que ninguna cama se pueda cerrar incompleta? | | |
| CNF-09 | En plena jornada de captura dentro del bloque, cuando quien captura debe interrumpir el registro de una cama sin haberlo terminado, ¿se necesita que la aplicación de captura le permita guardarlo tal como va y lo deje señalado como pendiente hasta que se complete? | | |

### 1.2 Reglas de validación

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-10 | En plena jornada de captura dentro del bloque, cuando quien captura ingresa un valor fuera del rango posible (por ejemplo, más tallos que plantas sembradas), ¿se necesita que la aplicación de captura lo rechace en el momento, sin permitir que ese valor quede guardado? | | |
| CNF-11 | En plena jornada de captura dentro del bloque, cuando quien captura ingresa un valor que se aleja mucho de lo capturado históricamente en esa misma cama, ¿se necesita que la aplicación de captura se lo advierta en el momento sin bloquearlo, dejándolo continuar y guardar el valor si lo confirma? | | |
| CNF-12 | En plena jornada de captura dentro del bloque, cuando la aplicación de captura rechaza un dato, ¿se necesita que la aplicación muestre el motivo del rechazo en la misma pantalla, siempre y sin excepción, y no solo el aviso de que fue rechazado? | | |
| CNF-13 | Con la operación en marcha, cuando desde la finca se cambia un rango o una regla de validación, ¿se necesita que la aplicación de captura empiece a aplicar la regla nueva sin que se deba publicar ni instalar una versión distinta de la aplicación? | | |
| CNF-14 | En el área de cultivo y sin ninguna conexión de datos, cuando quien captura ingresa un dato que infringe una regla de validación, ¿se necesita que la aplicación de captura reaccione exactamente igual que estando conectada, con el mismo rechazo o la misma advertencia? | | |
| CNF-15 | En plena jornada de captura dentro del bloque, cuando quien captura empieza a registrar una cama que ya fue capturada ese mismo día, ¿se necesita que la aplicación de captura se lo advierta antes de que ingrese el primer dato del duplicado? | | |
| CNF-16 | En plena jornada de captura dentro del bloque, cuando quien captura abre una cama que lleva más días de lo normal sin ser capturada, ¿se necesita que la aplicación de captura se lo advierta en ese momento, indicando el rezago? | | |
| CNF-17 | En plena jornada de captura dentro del bloque, cuando quien captura intenta registrar una labor sobre una cama que ya fue erradicada, ¿se necesita que la aplicación de captura se lo impida, sin dejar que ese registro se guarde? | | |
| CNF-18 | En plena jornada de captura dentro del bloque, cuando el dispositivo de captura tiene la fecha o la hora alteradas, ¿se necesita que la aplicación de captura detecte la alteración y bloquee la captura hasta que se corrija, sin dejar registrar ningún dato entre tanto? | | |

### 1.3 Forma de capturar

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-19 | En plena jornada de captura dentro del bloque, cuando quien captura inicia el registro de una cama, ¿se necesita que la aplicación de captura lo lleve de manera progresiva y guiada pantalla por pantalla —primero bloque, luego cama, luego lote y fecha de siembra, después las variedades plantadas—, sin permitir avanzar a la pantalla siguiente antes de resolver la anterior? | | |
| CNF-20 | En plena jornada de captura dentro del bloque, cuando quien captura inicia el registro de una cama, ¿se necesita que la aplicación de captura le presente una plantilla predefinida con una estructura equivalente a la de las plantillas de papel que se usaban antes, campo por campo? | | |
| CNF-21 | En plena jornada de captura dentro del bloque, cuando quien captura pasa de un bloque a otro de la finca, ¿se necesita que la aplicación de captura le presente siempre la misma estructura de captura, sin variaciones entre bloques? | | |
| CNF-22 | En plena jornada de captura dentro del bloque, cuando quien captura selecciona una variedad distinta, ¿se necesita que la aplicación de captura ajuste la estructura de captura a esa variedad, mostrando los campos que le correspondan? | | |
| CNF-23 | En plena jornada de captura dentro del bloque, cuando quien captura abre una cama ya capturada anteriormente, ¿se necesita que la aplicación de captura proponga automáticamente el valor registrado la última vez en esa misma cama, dejándolo aceptar o cambiar? | | |
| CNF-24 | En plena jornada de captura dentro del bloque, cuando quien captura avanza de una cama a la siguiente, ¿se necesita que la aplicación de captura le proponga las camas en el mismo orden del recorrido físico que se hace dentro del bloque, sin obligarlo a buscarlas? | | |
| CNF-25 | En plena jornada de captura dentro del bloque, mientras quien captura avanza en el recorrido, ¿se necesita que la aplicación de captura le muestre en todo momento cuántas camas faltan por capturar en la jornada, actualizado con cada cama cerrada? | | |

### 1.4 Corrección y conflicto

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-26 | En plena jornada de captura y antes de que la información se haya sincronizado, cuando quien captura advierte que un dato quedó mal registrado, ¿se necesita que la aplicación de captura le permita corregirlo en ese momento, sin esperar a la sincronización? | | |
| CNF-27 | Una vez sincronizada la jornada, cuando quien captura o quien consulta advierte que un dato quedó mal registrado, ¿se necesita que el sistema le permita corregirlo después de sincronizado, dejando el valor corregido como vigente? | | |
| CNF-28 | Al revisar la información ya corregida, cuando quien consulta abre un dato que fue modificado, ¿se necesita que el sistema le muestre también el valor original junto al corregido, de modo que ambos queden visibles? | | |
| CNF-29 | En el momento de corregir, cuando quien corrige guarda el nuevo valor de un dato, ¿se necesita que el sistema le exija escribir un motivo y no acepte la corrección sin él, en todas las correcciones sin excepción? | | |
| CNF-30 | Pasado el cierre de un periodo, cuando quien corrige intenta modificar un dato de una fecha ya cerrada, ¿se necesita que el sistema se lo impida a partir de esa fecha, sin admitir ninguna corrección posterior sobre ese periodo? | | |
| CNF-31 | Al sincronizar al final de la jornada, cuando llegan al servicio en la nube dos capturas distintas de la misma cama, ¿se necesita que el sistema resuelva el conflicto por sí solo conservando la más reciente, sin intervención de nadie? | | |
| CNF-32 | Al sincronizar al final de la jornada, cuando llegan al servicio en la nube dos capturas distintas de la misma cama, ¿se necesita que el sistema conserve ambas y deje el dato en espera hasta que una persona decida cuál queda? | | |
| CNF-33 | Después de la sincronización, cuando una captura es descartada o modificada por un conflicto, ¿se necesita que el sistema le avise a quien la había capturado, indicándole qué pasó con su registro? | | |

### 1.5 Confianza en el resultado

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-34 | En la consulta de resultados, cuando quien consulta abre una proyección, ¿se necesita que el sistema le muestre con qué datos y con qué parámetros fue calculada esa proyección, sin tener que pedirlo aparte? | | |
| CNF-35 | Meses después de haber sido emitida, cuando se vuelve a calcular una proyección con la misma información y los mismos parámetros, ¿se necesita que el sistema entregue exactamente el mismo resultado, sin diferencia alguna frente al original? | | |
| CNF-36 | En la consulta de resultados, cuando quien consulta abre una proyección cuyo periodo ya se cortó, ¿se necesita que el sistema le muestre cuánto se desvió esa proyección frente a lo que realmente se cortó? | | |
| CNF-37 | En la consulta de resultados, cuando quien consulta abre la información de un periodo, ¿se necesita que el sistema le muestre un indicador del porcentaje de datos con error o pendientes de verificación de ese periodo? | | |
| CNF-38 | Con la operación en marcha, cuando el porcentaje de error de la información capturada supera un límite acordado, ¿se necesita que el sistema lo señale contra una meta explícita de error máximo aceptable fijada de antemano? | | |
| CNF-39 | En la consulta de resultados, cuando quien consulta abre información capturada, ¿se necesita que el sistema distinga a la vista la información ya verificada de la que todavía no lo ha sido? | | |
| CNF-40 | Al calcular una proyección, cuando parte de la información involucrada aún no ha sido verificada, ¿se necesita que el sistema la excluya del cálculo y solo la incorpore una vez verificada? | | |
| CNF-41 | Durante los primeros meses de uso, cuando quien captura registra una cama en la aplicación, ¿se necesita que siga llenando además el formato en papel de esa misma cama, en paralelo y sin excepción, durante ese periodo de marcha blanca? | | |
| CNF-42 | Al cierre de la jornada, cuando quien consulta quiere contrastar lo registrado, ¿se necesita que el sistema le permita comparar lo capturado en la aplicación contra el formato en papel del mismo día, cama por cama? | | |

### 1.6 Comportamiento ante fallos y pérdida de información

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-43 | En cualquier punto de la operación, cuando ocurre una falla de cualquier naturaleza —dispositivo, aplicación, red o servicio—, ¿se necesita que el sistema conserve la información ya capturada sin perder ni un solo dato, bajo ninguna circunstancia? | | |
| CNF-44 | En plena jornada de captura dentro del bloque, cuando el dispositivo se apaga por batería agotada a mitad de una captura, ¿se necesita que la aplicación de captura conserve lo que ya se había registrado y lo devuelva íntegro al volver a encender? | | |
| CNF-45 | En plena jornada de captura dentro del bloque, cuando la aplicación de captura se cierra de manera inesperada, ¿se necesita que conserve lo que ya se había registrado y lo devuelva íntegro al volver a abrirla? | | |
| CNF-46 | En plena jornada de captura dentro del bloque, cuando el dispositivo se moja o se golpea y hay que reemplazarlo, ¿se necesita que el sistema conserve la información que ese dispositivo tenía capturada y la deje disponible en el dispositivo de reemplazo? | | |
| CNF-47 | Al sincronizar al final de la jornada, cuando la sincronización se interrumpe a la mitad, ¿se necesita que el sistema la pueda retomar desde donde quedó, sin duplicar ni perder ningún registro? | | |
| CNF-48 | Con la operación en marcha, cuando transcurre el intervalo previsto de respaldo, ¿se necesita que el servicio en la nube haga la copia de respaldo por sí solo, sin que nadie tenga que solicitarla? | | |
| CNF-49 | Con la operación en marcha, cuando transcurre un día de operación, ¿se necesita que el servicio en la nube haya hecho al menos una copia de respaldo de la información en ese día? | | |
| CNF-50 | Después de un daño o un error masivo en la información, cuando se solicita restablecerla, ¿se necesita que el sistema pueda devolver la información al estado exacto que tenía en una fecha anterior determinada? | | |
| CNF-51 | Después de un daño o un error masivo en la información de una finca, cuando se solicita restablecerla, ¿se necesita que el sistema pueda devolver únicamente la información de esa finca, sin alterar la de las demás fincas ni la de las demás empresas? | | |
| CNF-52 | Con la operación en marcha, cuando una cama se erradica o deja de estar en uso, ¿se necesita que el sistema conserve toda su información sin eliminar nada, ni en ese momento ni después? | | |
| CNF-53 | Con la operación en marcha, cuando un dato capturado es corregido, ¿se necesita que el sistema conserve a la vez la información tal como se capturó y la información ya corregida, sin que la corregida reemplace a la original? | | |
| CNF-54 | En la consulta de resultados, cuando una proyección se calculó sobre información incompleta, ¿se necesita que el sistema lo advierta al mostrarla, indicando qué faltaba? | | |
| CNF-55 | Al momento de publicar una proyección, cuando faltan los datos de bloques enteros, ¿se necesita que el sistema impida publicarla mientras esos bloques sigan sin capturar? | | |
| CNF-56 | Con la operación en marcha, cuando transcurre el periodo establecido de prueba, ¿se necesita que el servicio en la nube verifique que las copias de respaldo efectivamente permiten restablecer la información, y deje constancia del resultado? | | |
| CNF-57 | En plena jornada de captura dentro del bloque, cuando el almacenamiento del dispositivo está por llenarse, ¿se necesita que la aplicación de captura lo avise antes de que se agote, con margen para actuar durante la jornada? | | |

---

## 2. Disponibilidad

*Que se pueda usar cuando se necesita, incluso sin conexión o con fallos.*  
*Puntuación mini QAW: 11 · 20 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| DSP-01 | En el área de cultivo y sin ninguna conexión de datos, cuando quien captura inicia el registro de una cama, ¿se necesita que la aplicación de captura funcione y reciba la información completa, sin depender en ningún momento de la red? | | |
| DSP-02 | En el área de cultivo y sin ninguna conexión de datos, cuando quien captura recorre una jornada completa, ¿se necesita que la aplicación de captura conserve y siga admitiendo información durante toda la jornada sin sincronizar ni una sola vez? | | |
| DSP-03 | En el área de cultivo y sin ninguna conexión de datos, cuando quien captura acumula tres días seguidos de trabajo, ¿se necesita que la aplicación de captura conserve y siga admitiendo información durante esos tres días sin sincronizar? | | |
| DSP-04 | En el área de cultivo y sin ninguna conexión de datos, cuando quien captura acumula una semana seguida de trabajo, ¿se necesita que la aplicación de captura conserve y siga admitiendo información durante esa semana sin sincronizar? | | |
| DSP-05 | En el área de cultivo y sin ninguna conexión de datos, cuando quien captura acumula más de quince días de trabajo, ¿se necesita que la aplicación de captura conserve y siga admitiendo información durante más de quince días sin sincronizar? | | |
| DSP-06 | En el área de cultivo y sin conexión en ese momento, cuando quien consulta necesita ver información que ya fue sincronizada, ¿se necesita que la aplicación se la muestre igualmente, estando sin conexión? | | |
| DSP-07 | Con la aplicación de captura fuera de servicio, cuando quien consulta entra a la consulta web, ¿se necesita que la consulta web siga funcionando y mostrando la información, sin verse afectada por la caída de la aplicación de captura? | | |
| DSP-08 | Con el servicio en la nube caído, cuando quien captura inicia el registro de una cama, ¿se necesita que la aplicación de captura siga funcionando y admitiendo información durante toda la caída? | | |
| DSP-09 | Dentro del horario crítico de la operación, cuando llega ese tramo del día, ¿se necesita que el sistema no pueda estar fuera de servicio bajo ninguna circunstancia durante ese horario, ni siquiera por mantenimiento? | | |
| DSP-10 | A lo largo de la semana, cuando alguien necesita usar el sistema en sábado, domingo o festivo, ¿se necesita que esté disponible los siete días de la semana, sin días de cierre programado? | | |
| DSP-11 | A lo largo del día, cuando alguien necesita usar el sistema fuera del horario habitual de la finca, ¿se necesita que esté disponible las veinticuatro horas del día? | | |
| DSP-12 | Durante la temporada alta, cuando llega ese periodo del año, ¿se necesita que el sistema cumpla una exigencia de disponibilidad mayor que la del resto del año? | | |
| DSP-13 | Con la operación en marcha, cuando el servicio se interrumpe, ¿se necesita que el sistema quede restablecido en menos de una hora desde el momento de la interrupción? | | |
| DSP-14 | Con la operación en marcha, cuando el servicio se interrumpe, ¿se necesita que el sistema quede restablecido dentro del mismo día de la interrupción? | | |
| DSP-15 | Con la operación en marcha, cuando se va a realizar una labor de mantenimiento, ¿se necesita que el sistema lo avise con anticipación a quienes lo usan, antes de que la labor comience? | | |
| DSP-16 | Con la operación en marcha, cuando se realiza una labor de mantenimiento, ¿se necesita que el sistema siga en servicio durante toda la labor, sin ninguna interrupción para quienes lo usan? | | |
| DSP-17 | En plena jornada de captura dentro del bloque, cuando el dispositivo que se estaba usando deja de funcionar, ¿se necesita que el sistema permita continuar la captura en otro dispositivo dentro de la misma jornada? | | |
| DSP-18 | Después de que un dispositivo se dañó o se perdió, cuando se solicita la información que tenía capturada, ¿se necesita que el sistema pueda recuperarla, incluida la que no alcanzó a sincronizar? | | |
| DSP-19 | Con varias fincas y empresas operando a la vez, cuando ocurre una falla que afecta a una finca, ¿se necesita que el sistema mantenga a las demás fincas y empresas funcionando con normalidad, sin propagarles la falla? | | |
| DSP-20 | Con la operación en marcha, cuando un dispositivo lleva demasiado tiempo sin sincronizar, ¿se necesita que el sistema lo avise, identificando el dispositivo y el tiempo transcurrido? | | |

---

## 3. Rendimiento

*Que responda con la rapidez que exige el ritmo del campo.*  
*Puntuación mini QAW: 17 · 21 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| RND-01 | En plena jornada de captura dentro del bloque, cuando quien captura registra una cama completa, ¿se necesita que la aplicación de captura le permita terminarla en menos tiempo del que hoy toma llenar esa misma cama en papel? | | |
| RND-02 | En plena jornada de captura dentro del bloque, cuando quien captura registra una cama completa, ¿se necesita que la aplicación de captura le permita terminarla en menos de un minuto? | | |
| RND-03 | En plena jornada de captura dentro del bloque, cuando quien captura registra una cama completa, ¿se necesita que la aplicación de captura le permita terminarla en menos de treinta segundos? | | |
| RND-04 | En el recorrido normal del cultivo, cuando quien captura trabaja un bloque completo, ¿se necesita que la aplicación de captura le permita terminar ese bloque dentro de una sola jornada? | | |
| RND-05 | En el recorrido normal del cultivo, cuando quien captura trabaja la finca completa, ¿se necesita que la aplicación de captura le permita terminar toda la finca dentro de una sola jornada? | | |
| RND-06 | En plena jornada de captura dentro del bloque, cuando quien captura confirma un dato, ¿se necesita que la aplicación de captura lo deje guardado de inmediato, sin espera perceptible antes de admitir el siguiente? | | |
| RND-07 | En el área de cultivo y sin ninguna conexión de datos, cuando quien captura pasa de una pantalla a la siguiente, ¿se necesita que la aplicación de captura responda de inmediato, sin espera perceptible aun estando sin conexión? | | |
| RND-08 | En plena jornada de captura dentro del bloque, cuando quien captura busca una cama por su código, ¿se necesita que la aplicación de captura entregue el resultado de inmediato, sin espera perceptible? | | |
| RND-09 | Al cierre de la jornada y con conexión disponible, cuando el dispositivo empieza a sincronizar lo capturado en el día, ¿se necesita que el sistema termine la sincronización de la jornada completa en menos de cinco minutos? | | |
| RND-10 | En plena jornada de captura dentro del bloque, cuando el dispositivo recupera conexión y empieza a sincronizar, ¿se necesita que la aplicación de captura haga la sincronización en segundo plano, sin impedir que se siga capturando mientras tanto? | | |
| RND-11 | Al cierre de la jornada, cuando muchos dispositivos sincronizan al mismo tiempo, ¿se necesita que el servicio en la nube atienda todas esas sincronizaciones sin degradación perceptible del tiempo de respuesta? | | |
| RND-12 | Terminada la sincronización, cuando quien consulta entra a la consulta web, ¿se necesita que el sistema le muestre la información recién sincronizada de inmediato? | | |
| RND-13 | Terminada la sincronización, cuando quien consulta entra a la consulta web, ¿se necesita que el sistema le muestre la información recién sincronizada dentro de la misma hora? | | |
| RND-14 | Terminada la sincronización, cuando quien consulta entra a la consulta web, ¿se necesita que el sistema le muestre la información recién sincronizada al día siguiente? | | |
| RND-15 | En la consulta de resultados, cuando quien consulta solicita un reporte de un mes, ¿se necesita que el sistema lo genere en menos de diez segundos? | | |
| RND-16 | En la consulta de resultados, cuando quien consulta solicita un reporte que abarca varios años, ¿se necesita que el sistema lo genere sin espera significativa? | | |
| RND-17 | Con la operación en marcha, cuando llega información nueva al servicio en la nube, ¿se necesita que el sistema recalcule la proyección en ese mismo momento, incorporando de inmediato lo que acaba de llegar? | | |
| RND-18 | Con la operación en marcha, cuando llega la hora fija establecida, ¿se necesita que el sistema recalcule la proyección una vez al día en ese horario? | | |
| RND-19 | En plena jornada de captura dentro del bloque y sobre un dispositivo de gama baja o de varios años de antigüedad, cuando quien captura registra una cama, ¿se necesita que la aplicación de captura funcione con la misma fluidez que en un dispositivo reciente? | | |
| RND-20 | En plena jornada de captura dentro del bloque, cuando quien captura usa la aplicación durante toda la jornada, ¿se necesita que el dispositivo llegue al final de la jornada con batería, sin agotarse por causa de la aplicación? | | |
| RND-21 | Durante la temporada alta, cuando el volumen de captura y de consulta alcanza su pico, ¿se necesita que el sistema mantenga los mismos tiempos de respuesta que en el resto del año? | | |

---

## 4. Capacidad para ser Auditado

*Que se pueda reconstruir quién hizo qué y con qué información, y demostrarlo ante quien lo exija. Mezcla trazabilidad y cumplimiento.*  
*Puntuación mini QAW: 17 · 32 preguntas*

### 4.1 Trazabilidad de la información

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| AUD-01 | Tiempo después de la captura, cuando quien revisa abre un dato cualquiera, ¿se necesita que el sistema le muestre quién lo capturó, para ese dato y para todos los demás? | | |
| AUD-02 | Tiempo después de la captura, cuando quien revisa abre un dato cualquiera, ¿se necesita que el sistema le muestre la fecha y la hora exactas en que fue capturado? | | |
| AUD-03 | Tiempo después de la captura, cuando quien revisa abre un dato cualquiera, ¿se necesita que el sistema le muestre por separado la hora en que se capturó y la hora en que se sincronizó, distinguiéndolas? | | |
| AUD-04 | Tiempo después de la captura, cuando quien revisa abre un dato cualquiera, ¿se necesita que el sistema le muestre desde qué dispositivo fue capturado? | | |
| AUD-05 | Tiempo después de la captura, cuando quien revisa abre un dato cualquiera, ¿se necesita que el sistema le muestre en qué lugar físico se encontraba el dispositivo en el momento de capturarlo? | | |
| AUD-06 | Tiempo después de la captura, cuando quien revisa abre un dato que fue modificado varias veces, ¿se necesita que el sistema le muestre todas las versiones anteriores de ese dato y no solamente la última? | | |
| AUD-07 | Tiempo después de la captura, cuando quien revisa abre una modificación registrada sobre un dato, ¿se necesita que el sistema le muestre quién la hizo y por qué motivo, en cada modificación? | | |
| AUD-08 | En una revisión posterior, cuando quien revisa abre una cama, ¿se necesita que el sistema le muestre su historia completa desde la siembra hasta la erradicación, sin vacíos? | | |
| AUD-09 | En una revisión posterior, cuando quien revisa abre un lote, ¿se necesita que el sistema le muestre su historia completa a través de todas las camas por las que pasó? | | |
| AUD-10 | En una revisión posterior, cuando quien revisa abre una variedad, ¿se necesita que el sistema le muestre su historia completa a través de todos los ciclos en que se ha sembrado? | | |
| AUD-11 | En la consulta de resultados, cuando quien revisa señala una cifra de un reporte, ¿se necesita que el sistema lo lleve hasta los datos individuales que la componen, hasta el nivel del dato capturado? | | |
| AUD-12 | En una revisión posterior, cuando alguien consulta o exporta información, ¿se necesita que el sistema registre también ese acceso, y no únicamente las modificaciones? | | |
| AUD-13 | En cualquier momento y ante cualquier intento, cuando alguien —incluido quien administra el sistema— trata de alterar o borrar el registro de lo ocurrido, ¿se necesita que el sistema se lo impida, sin excepción para nadie? | | |
| AUD-14 | Ante un requerimiento externo, cuando se solicita el registro de lo ocurrido, ¿se necesita que el sistema lo exporte en un archivo entregable a un tercero, completo y legible fuera del sistema? | | |
| AUD-15 | Cinco años después de la captura, cuando quien revisa abre un dato de esa antigüedad, ¿se necesita que el sistema conserve su trazabilidad completa durante al menos esos cinco años? | | |
| AUD-16 | En cualquier momento futuro, cuando quien revisa abre un dato de cualquier antigüedad, ¿se necesita que el sistema conserve su trazabilidad de manera indefinida, sin fecha de caducidad? | | |
| AUD-17 | En plena jornada de captura dentro del bloque, cuando quien captura registra una disminución en la cantidad de plantas o de tallos esperados, ¿se necesita que la aplicación de captura le exija el motivo y lo registre junto al dato, cada vez que ocurra? | | |
| AUD-18 | Durante una auditoría externa, cuando el auditor señala cualquier cifra del sistema, ¿se necesita que el sistema permita demostrar su origen hasta los datos que la componen, para cualquier cifra que se señale? | | |

### 4.2 Cumplimiento y evidencia ante terceros

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| AUD-19 | Durante una auditoría de certificación (por ejemplo Florverde, Rainforest Alliance o GLOBALG.A.P.), cuando el certificador solicita evidencia, ¿se necesita que el sistema entregue esa información sacada directamente de él, sin tener que reconstruirla por fuera? | | |
| AUD-20 | Ante una visita de la autoridad fitosanitaria, cuando esta solicita la evidencia de las labores aplicadas a un cultivo, ¿se necesita que el sistema la conserve dentro de sí y la entregue, con el alcance que la autoridad exige? | | |
| AUD-21 | Después de una venta, cuando un comprador solicita el respaldo del lote que adquirió, ¿se necesita que el sistema entregue la historia completa de ese lote? | | |
| AUD-22 | Durante una auditoría externa, cuando un auditor necesita revisar la información, ¿se necesita que el sistema le permita consultarla directamente sin poder modificar nada, en modo de solo lectura? | | |
| AUD-23 | Terminada una auditoría externa, cuando vence el plazo otorgado, ¿se necesita que el sistema retire por sí solo el acceso de consulta del auditor, sin que nadie tenga que revocarlo? | | |
| AUD-24 | Ante una auditoría, cuando desde la finca se necesita el informe correspondiente, ¿se necesita que el sistema lo genere directamente, sin requerir ayuda técnica ni intervención del proveedor? | | |
| AUD-25 | Durante una auditoría externa, cuando el auditor cuestiona la información de un periodo ya cerrado, ¿se necesita que el sistema permita demostrar que esa información no fue alterada después del cierre? | | |
| AUD-26 | En una revisión posterior, cuando alguien ha consultado información protegida como secreto empresarial, ¿se necesita que el sistema deje constancia de quién la vio y cuándo? | | |
| AUD-27 | Cumplido el periodo mínimo exigido por norma, cuando la información ya no se usa en la operación, ¿se necesita que el sistema la conserve igualmente durante todo ese periodo? | | |
| AUD-28 | Cumplido el plazo máximo de retención, cuando la norma obliga a suprimir la información, ¿se necesita que el sistema la elimine en ese momento? | | |
| AUD-29 | Con la operación en marcha, cuando el sistema guarda datos personales de quienes capturan, ¿se necesita que les dé un tratamiento distinto al de los datos de producción, con acceso y conservación propios? | | |
| AUD-30 | Antes de que una proyección se comunique fuera del área, cuando alguien la aprueba, ¿se necesita que el sistema registre quién la aprobó y cuándo, antes de permitir que salga? | | |
| AUD-31 | En una revisión posterior, cuando quien revisa abre una revisión de calidad de la información, ¿se necesita que el sistema conserve la evidencia de cómo se hizo esa revisión, y no solamente su resultado? | | |
| AUD-32 | Ante un tercero con el que se adquirió un compromiso, cuando este exige demostrar su cumplimiento, ¿se necesita que el sistema lo sustente con información propia, sin apoyarse en registros externos? | | |

---

## 5. Capacidad

*Cuánta información tiene que soportar y por cuánto tiempo.*  
*Puntuación mini QAW: 19 · 15 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CAP-01 | Con la operación en marcha, cuando una finca completa vuelca su información en el sistema, ¿se necesita que el servicio en la nube la soporte toda, sin límite que obligue a dejar parte por fuera? | | |
| CAP-02 | Con la operación en marcha, cuando varias fincas de la misma empresa vuelcan su información, ¿se necesita que el servicio en la nube las soporte todas a la vez? | | |
| CAP-03 | Con la operación en marcha, cuando varias empresas distintas vuelcan su información, ¿se necesita que el servicio en la nube las soporte todas al mismo tiempo, cada una con la suya? | | |
| CAP-04 | En la consulta de resultados, cuando quien consulta pide información del último año, ¿se necesita que el sistema la tenga en línea y la muestre sin ningún paso de recuperación previo? | | |
| CAP-05 | En la consulta de resultados, cuando quien consulta pide información de los últimos cinco años, ¿se necesita que el sistema la tenga en línea y la muestre sin ningún paso de recuperación previo? | | |
| CAP-06 | En la consulta de resultados, cuando quien consulta pide información de cualquier fecha desde el primer día de uso, ¿se necesita que el sistema la tenga en línea y la muestre sin límite de antigüedad? | | |
| CAP-07 | En la consulta de resultados, cuando quien consulta pide información antigua, ¿se necesita que el sistema se la entregue con la misma rapidez que la información reciente? | | |
| CAP-08 | En la consulta de resultados, cuando quien consulta pide información de más de cierta antigüedad, ¿se aceptaría que el sistema tarde más en entregársela que con la información reciente? | | |
| CAP-09 | En plena jornada de captura dentro del bloque, cuando quien captura toma una fotografía junto a un dato, ¿se necesita que el sistema la almacene y la conserve asociada a ese dato? | | |
| CAP-10 | Con la operación en marcha, cuando se incorpora un documento o un formato escaneado, ¿se necesita que el sistema lo almacene asociado a la cama o al lote correspondiente? | | |
| CAP-11 | Con la operación en marcha, cuando un dato es corregido, ¿se necesita que el sistema almacene a la vez la copia sin modificar y la copia corregida de ese dato? | | |
| CAP-12 | En el área de cultivo y sin conexión durante varios días, cuando quien captura acumula la información de esos días en el dispositivo, ¿se necesita que la aplicación de captura la conserve toda sin quedarse sin espacio? | | |
| CAP-13 | Antes de salir a capturar, cuando el dispositivo se prepara para la jornada, ¿se necesita que la aplicación de captura tenga descargado todo el catálogo de la finca, completo, antes de perder la conexión? | | |
| CAP-14 | Al cabo de varios años de uso, cuando el volumen de información acumulada crece año tras año, ¿se necesita que el sistema lo absorba sin obligar a cambiar de sistema ni a migrar a otro? | | |
| CAP-15 | Al cabo de varios años de uso, cuando la historia acumulada de una finca crece, ¿se necesita que el servicio en la nube mantenga acotado el costo de almacenamiento por finca, sin que crezca en la misma proporción que la historia? | | |

---

## 6. Capacidad para ser Administrado

*Que el día a día del sistema se pueda manejar desde adentro, sin desarrollo.*  
*Puntuación mini QAW: 23 · 16 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| ADM-01 | Con la operación en marcha, cuando desde la finca se necesita crear o dar de baja un usuario, ¿se necesita que el sistema lo permita hacer allí mismo, sin solicitárselo a quien desarrolló el sistema? | | |
| ADM-02 | Con la operación en marcha, cuando desde la finca se cambian los permisos de un usuario, ¿se necesita que el sistema los aplique de manera inmediata, sin esperar a que la persona vuelva a entrar? | | |
| ADM-03 | El mismo día en que una persona deja de trabajar en la finca, cuando desde la finca se retira su acceso, ¿se necesita que el sistema lo deje sin acceso ese mismo día? | | |
| ADM-04 | Con la operación en marcha, cuando desde la finca se necesita registrar bloques, camas o secciones nuevas, ¿se necesita que el sistema lo permita hacer allí mismo, sin desarrollo adicional? | | |
| ADM-05 | Con la operación en marcha, cuando desde la finca se necesita registrar una variedad nueva, ¿se necesita que el sistema lo permita hacer allí mismo, sin desarrollo adicional? | | |
| ADM-06 | Con la operación en marcha, cuando desde la finca se cambian los grados de calidad o sus definiciones, ¿se necesita que el sistema lo permita hacer allí mismo, sin desarrollo adicional? | | |
| ADM-07 | Con la operación en marcha, cuando desde la finca se cambia la densidad de siembra o algún parámetro de cálculo, ¿se necesita que el sistema lo permita hacer allí mismo, sin desarrollo adicional? | | |
| ADM-08 | Después de un cambio de parámetros, cuando quien consulta abre una proyección emitida antes de ese cambio, ¿se necesita que el sistema la muestre tal como fue emitida, sin que el cambio la altere? | | |
| ADM-09 | Con la operación en marcha, cuando desde la finca se revisa el estado de los dispositivos, ¿se necesita que el sistema muestre en una sola pantalla cuáles tienen información pendiente de sincronizar? | | |
| ADM-10 | Durante la jornada, cuando desde la finca se revisa cómo va la captura del día, ¿se necesita que el sistema muestre en una sola pantalla el avance por bloque? | | |
| ADM-11 | Con la operación en marcha, cuando un dispositivo tiene información pendiente y no la ha enviado, ¿se necesita que el sistema permita forzar su sincronización de manera remota, sin tener que recoger el dispositivo? | | |
| ADM-12 | Cuando hay una versión nueva de la aplicación, al momento de desplegarla, ¿se necesita que el sistema la aplique a todos los dispositivos de manera remota, sin recogerlos uno por uno? | | |
| ADM-13 | En plena jornada de captura, cuando se aplica una actualización de la aplicación, ¿se necesita que la captura del día continúe sin suspenderse en ningún momento? | | |
| ADM-14 | Antes del inicio de una temporada, cuando desde la finca se prepara el calendario, ¿se necesita que el sistema permita registrar por adelantado las temporadas y los días no laborables? | | |
| ADM-15 | Con la operación en marcha, cuando desde un computador corriente de la finca se administra el sistema, ¿se necesita que todas esas tareas se puedan hacer allí, sin herramientas técnicas especiales? | | |
| ADM-16 | Con la operación en marcha, cuando quien administra el sistema en la finca intenta modificar información de producción, ¿se necesita que el sistema se lo impida, sin excepción? | | |

---

## 7. Experiencia de Usuario

*Que capturar sea más fácil que el papel, en las condiciones del cultivo.*  
*Puntuación mini QAW: 24 · 18 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| UXP-01 | En el cultivo y con la otra mano ocupada, cuando quien captura registra una cama, ¿se necesita que la aplicación de captura le permita completarla usando una sola mano, de principio a fin? | | |
| UXP-02 | En el cultivo y con guantes de labor puestos, cuando quien captura registra una cama, ¿se necesita que la aplicación de captura responda a todos sus toques sin necesidad de quitarse los guantes? | | |
| UXP-03 | En el cultivo y bajo sol directo, cuando quien captura mira la pantalla, ¿se necesita que la aplicación de captura se mantenga legible en esas condiciones, sin buscar sombra? | | |
| UXP-04 | En plena jornada de captura dentro del bloque, cuando quien captura ingresa los datos de una cama, ¿se necesita que la aplicación de captura le permita resolver la mayoría escogiendo de una lista, sin tener que escribirlos? | | |
| UXP-05 | En plena jornada de captura dentro del bloque, cuando quien captura registra una cama completa, ¿se necesita que la aplicación de captura la resuelva con la menor cantidad posible de toques? | | |
| UXP-06 | En plena jornada de captura dentro del bloque, cuando quien captura se equivoca en el último dato registrado, ¿se necesita que la aplicación de captura le permita deshacerlo sin salir de la pantalla en que está? | | |
| UXP-07 | En plena jornada de captura dentro del bloque, mientras quien captura avanza, ¿se necesita que la aplicación de captura le muestre en todo momento cuánto lleva capturado y cuánto le falta de la jornada? | | |
| UXP-08 | En plena jornada de captura dentro del bloque, cuando quien captura revisa lo que lleva registrado, ¿se necesita que la aplicación de captura le indique con claridad qué información ya se sincronizó y cuál no? | | |
| UXP-09 | En plena jornada de captura dentro del bloque, cuando quien captura lee los nombres que aparecen en pantalla, ¿se necesita que la aplicación de captura use exactamente los mismos términos que se usan hablando en la finca? | | |
| UXP-10 | La primera vez que alguien usa la aplicación, cuando esa persona intenta capturar sin haber recibido capacitación formal, ¿se necesita que la aplicación de captura le permita completar el registro por sí sola? | | |
| UXP-11 | Durante el acompañamiento inicial de una persona nueva, cuando esa persona captura su primera cama, ¿se necesita que la aplicación de captura le permita hacerlo correctamente en menos de diez minutos de acompañamiento? | | |
| UXP-12 | En plena jornada de captura dentro del bloque, cuando quien captura llega a una cama, ¿se necesita que la aplicación de captura la identifique escaneando una marca física, sin tener que escribir el código? | | |
| UXP-13 | En plena jornada de captura dentro del bloque, cuando quien captura tiene que ingresar un dato, ¿se necesita que la aplicación de captura le permita dictarlo por voz en lugar de escribirlo? | | |
| UXP-14 | En plena jornada de captura dentro del bloque, mientras quien captura ingresa un dato, ¿se necesita que la aplicación de captura le sugiera un valor, siempre dejándole rechazar la sugerencia? | | |
| UXP-15 | Fuera del computador, cuando quien consulta necesita ver resultados desde un celular, ¿se necesita que la consulta web funcione completa en el celular, con las mismas funciones que en el computador? | | |
| UXP-16 | En la consulta de resultados, cuando quien consulta necesita una vista que no existe, ¿se necesita que el sistema le permita armarla por sí mismo, sin pedírsela a nadie? | | |
| UXP-17 | En la consulta de resultados, cuando quien consulta revisa el estado de las camas de un bloque, ¿se necesita que el sistema se lo muestre sobre una representación gráfica del bloque y no solamente en una lista? | | |
| UXP-18 | En plena jornada de captura dentro del bloque, cuando la aplicación de captura rechaza o advierte algo, ¿se necesita que el mensaje explique qué hacer para resolverlo, y no solo qué salió mal? | | |

---

## 8. Seguridad

*Que la información no llegue a quien no debe.*  
*Puntuación mini QAW: 24 · 19 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| SEG-01 | Al iniciar la jornada, cuando una persona entra al sistema, ¿se necesita que el sistema le exija una identificación propia y no compartida, distinta de la de cualquier otra persona? | | |
| SEG-02 | En el área de cultivo y sin ninguna conexión de datos, cuando quien captura intenta entrar a la aplicación, ¿se necesita que la aplicación de captura le permita autenticarse y trabajar estando sin conexión? | | |
| SEG-03 | Con la sesión abierta y el dispositivo sin uso, cuando transcurre el tiempo de inactividad establecido, ¿se necesita que el sistema cierre la sesión por sí solo, sin que nadie la cierre? | | |
| SEG-04 | Después de que un dispositivo se pierde o es robado, cuando alguien intenta leer la información que tiene guardada, ¿se necesita que el sistema la mantenga ilegible fuera de la aplicación? | | |
| SEG-05 | Después de que un dispositivo se pierde o es robado, cuando desde la finca se ordena borrarlo, ¿se necesita que el sistema elimine de manera remota la información que ese dispositivo contiene? | | |
| SEG-06 | Con varias empresas operando en la misma plataforma, cuando alguien de una empresa intenta ver información de otra, ¿se necesita que el sistema se lo impida, bajo ninguna circunstancia y sin excepción? | | |
| SEG-07 | Ante el cliente, cuando este pide garantías del aislamiento entre empresas, ¿se necesita que el sistema permita demostrarlo documentalmente, con evidencia sacada de él? | | |
| SEG-08 | Con la operación en marcha, cuando quien opera la plataforma accede a la infraestructura, ¿se necesita que el sistema le impida leer la información de producción de la empresa? | | |
| SEG-09 | En una revisión posterior, cuando alguien del proveedor accedió técnicamente a la información, ¿se necesita que el sistema lo tenga registrado y lo deje revisable por la empresa? | | |
| SEG-10 | Al sincronizar, cuando la información viaja entre el dispositivo y la nube, ¿se necesita que el sistema la transmita cifrada durante todo el trayecto? | | |
| SEG-11 | Con la operación en marcha, cuando el servicio en la nube genera una copia de respaldo, ¿se necesita que la almacene cifrada? | | |
| SEG-12 | Con varias empresas operando en la misma plataforma, cuando se descifra la copia de respaldo de una empresa, ¿se necesita que el sistema use una llave distinta para cada empresa, de modo que esa llave no sirva para ninguna otra? | | |
| SEG-13 | En la consulta de resultados, cuando alguien intenta exportar información fuera del sistema, ¿se necesita que el sistema lo permita solo a quien esté expresamente autorizado y se lo impida al resto? | | |
| SEG-14 | En una revisión posterior, cuando se quiere saber qué información salió del sistema, ¿se necesita que el sistema tenga registrada cada exportación realizada, sin excepción? | | |
| SEG-15 | Después de una filtración, cuando aparece por fuera un archivo exportado del sistema, ¿se necesita que el sistema lo haya marcado de modo que se pueda identificar su origen? | | |
| SEG-16 | Al terminar el contrato de una persona temporal, cuando llega esa fecha, ¿se necesita que el sistema le retire el acceso por sí solo, sin que nadie tenga que revocarlo? | | |
| SEG-17 | Con la operación en marcha, cuando alguien intenta entrar repetidamente sin lograrlo, ¿se necesita que el sistema lo avise, identificando la cuenta y los intentos? | | |
| SEG-18 | Con la finca operando en un país determinado, cuando cierta información se va a almacenar o transmitir, ¿se necesita que el sistema la mantenga siempre dentro de ese país, sin que salga en ningún momento? | | |
| SEG-19 | Dentro de una misma empresa, cuando cualquiera de sus usuarios consulta el sistema, ¿se aceptaría que el sistema le muestre toda la información de la empresa, sin restricciones entre áreas? | | |

---

## 9. Interoperatividad

*Qué tanto tiene que hablar con lo que ya existe.*  
*Puntuación mini QAW: 25 · 13 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| INT-01 | En la consulta de resultados, cuando quien consulta necesita llevarse la información fuera del sistema, ¿se necesita que el sistema la exporte a Excel, en un archivo utilizable sin retoques? | | |
| INT-02 | En la consulta de resultados, cuando quien consulta necesita compartir un reporte, ¿se necesita que el sistema lo exporte a PDF, listo para enviar? | | |
| INT-03 | Al exportar, cuando quien consulta genera un archivo para compartir, ¿se necesita que el sistema conserve exactamente el formato de los archivos que hoy se comparten, sin que nadie tenga que reacomodarlo? | | |
| INT-04 | En la puesta en marcha, cuando se carga la información inicial de la finca, ¿se necesita que el sistema la tome de los archivos que ya existen, sin volver a digitarla? | | |
| INT-05 | Con la operación en marcha, cuando se produce información que el sistema administrativo de la empresa necesita, ¿se necesita que el sistema se la entregue a ese sistema administrativo? | | |
| INT-06 | Con la operación en marcha, cuando el sistema administrativo de la empresa tiene información que hace falta, ¿se necesita que el sistema la tome de allí, sin volver a digitarla? | | |
| INT-07 | Con la aplicación de plagas en uso, cuando esta sigue operando en la finca, ¿se necesita que el sistema conviva con ella sin reemplazarla ni obligar a retirarla? | | |
| INT-08 | Con la aplicación de plagas en uso, cuando esta produce información, ¿se necesita que el sistema la consuma e incorpore a la suya? | | |
| INT-09 | Al cierre del periodo de nómina o de productividades, cuando ese proceso requiere la información de campo, ¿se necesita que el sistema se la entregue directamente? | | |
| INT-10 | Desde una herramienta de análisis externa, cuando alguien necesita analizar la información del sistema, ¿se necesita que el sistema la deje leer desde esa herramienta, sin exportaciones manuales de por medio? | | |
| INT-11 | Con la operación en marcha, cuando otro programa necesita intercambiar información con el sistema, ¿se necesita que el sistema ofrezca una manera automática de conectarse, sin ninguna intervención manual? | | |
| INT-12 | Ante un cliente o un comercializador externo, cuando este solicita información, ¿se necesita que el sistema se la entregue en un formato que pueda usar? | | |
| INT-13 | En la primera entrega del sistema, cuando alguien necesita intercambiar información con otro programa, ¿se aceptaría que la única forma disponible sea la exportación manual a Excel y PDF? | | |

---

## 10. Escalabilidad

*Que crecer no obligue a rehacerlo.*  
*Puntuación mini QAW: 25 · 11 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| ESL-01 | En plena temporada, cuando desde la finca se agregan bloques y camas nuevas, ¿se necesita que el sistema los incorpore sin interrumpir la operación en curso? | | |
| ESL-02 | Con la operación en marcha, cuando se duplica la cantidad de personas que capturan, ¿se necesita que el sistema mantenga los mismos tiempos de respuesta, sin volverse más lento? | | |
| ESL-03 | Con la operación en marcha, cuando se incorpora una finca nueva, ¿se necesita que el sistema la reciba sin volver a instalarlo ni reconfigurarlo todo? | | |
| ESL-04 | Con la operación en marcha, cuando se incorpora una empresa nueva, ¿se necesita que el sistema la reciba sin afectar en nada a las empresas que ya están funcionando? | | |
| ESL-05 | Durante la temporada alta, cuando la actividad aumenta respecto del resto del año, ¿se necesita que el sistema la absorba sin degradación perceptible para quienes lo usan? | | |
| ESL-06 | Durante la temporada alta, cuando varias empresas alcanzan su pico exactamente en las mismas fechas, ¿se necesita que el sistema las atienda a todas a la vez sin degradación perceptible? | | |
| ESL-07 | Al cabo de varios años de uso, cuando el sistema crece en fincas y en volumen, ¿se necesita que el costo por finca no aumente en la misma proporción que ese crecimiento? | | |
| ESL-08 | Con varias empresas operando en la plataforma, cuando se aplica un cambio en la estructura del sistema, ¿se necesita que quede aplicado en todas las empresas sin intervención manual una por una? | | |
| ESL-09 | Al cabo de varios años de historia acumulada, cuando quien consulta hace una consulta corriente, ¿se necesita que el sistema le responda sin que el volumen acumulado la haya vuelto más lenta? | | |
| ESL-10 | Al cierre de la jornada, cuando más de treinta personas suben información el mismo día, ¿se necesita que el sistema las atienda a todas simultáneamente sin degradación perceptible? | | |
| ESL-11 | Con la operación en marcha, cuando se incorpora un tipo de labor o de medición nueva, ¿se necesita que el sistema la admita sin rehacer la captura que ya está funcionando? | | |

---

## 11. Capacidad para ser Soportado

*Qué pasa cuando algo falla y hay que resolverlo.*  
*Puntuación mini QAW: 27 · 13 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| SOP-01 | En plena jornada de captura, cuando se presenta un problema corriente del día a día, ¿se necesita que quede resuelto dentro de la finca, con las herramientas del sistema y sin escalar al proveedor? | | |
| SOP-02 | En plena jornada de captura, cuando se presenta un problema que impide capturar, ¿se necesita que el soporte lo resuelva el mismo día en que se reportó? | | |
| SOP-03 | En plena jornada de captura, cuando se presenta un problema que impide capturar, ¿se necesita que el soporte lo resuelva en menos de una hora desde que se reportó? | | |
| SOP-04 | En plena jornada de captura dentro del bloque, cuando quien captura se topa con un problema, ¿se necesita que la aplicación de captura le permita reportarlo desde ella misma, sin tener que llamar por teléfono? | | |
| SOP-05 | Al reportar un problema, cuando quien captura lo envía desde la aplicación, ¿se necesita que la aplicación de captura adjunte por sí sola lo que la persona estaba haciendo cuando ocurrió, sin que tenga que explicarlo? | | |
| SOP-06 | Ante un problema reportado, cuando el soporte lo atiende desde fuera de la finca, ¿se necesita que el sistema le dé lo necesario para diagnosticarlo sin desplazarse hasta allá? | | |
| SOP-07 | Ante un problema reportado, cuando el soporte necesita saber cómo está un dispositivo, ¿se necesita que el sistema le permita revisar su estado de forma remota? | | |
| SOP-08 | Durante la temporada alta, cuando se presenta un problema fuera del horario habitual, ¿se necesita que el soporte esté disponible en horario extendido durante toda esa temporada? | | |
| SOP-09 | Ante una duda de uso, cuando alguien necesita consultar cómo se hace algo, ¿se necesita que exista un manual de uso escrito que lo resuelva sin preguntarle a nadie? | | |
| SOP-10 | En plena jornada de captura dentro del bloque, cuando quien captura duda de cómo hacer algo, ¿se necesita que la aplicación de captura le ofrezca videos cortos de apoyo dentro de ella misma? | | |
| SOP-11 | En plena jornada de captura, mientras el problema reportado se resuelve, ¿se necesita que la aplicación de captura le permita a quien lo reportó seguir capturando, sin quedarse detenido hasta la solución? | | |
| SOP-12 | En una revisión posterior, cuando se quiere saber qué problemas se han presentado, ¿se necesita que el sistema conserve el registro de cada problema reportado y de cómo se resolvió? | | |
| SOP-13 | En la puesta en marcha del sistema, cuando la finca empieza a usarlo, ¿se necesita que la operación no se detenga por más de una semana durante esa puesta en marcha? | | |

---

## 12. Portabilidad

*Dónde y sobre qué tiene que poder funcionar.*  
*Puntuación mini QAW: 28 · 13 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| POR-01 | En campo, cuando quien captura usa un dispositivo Android, ¿se necesita que la aplicación de captura funcione allí con todas sus funciones? | | |
| POR-02 | En campo, cuando quien captura usa un dispositivo Apple, ¿se necesita que la aplicación de captura funcione allí también, con las mismas funciones que en Android? | | |
| POR-03 | En campo, cuando quien captura usa una tableta en lugar de un celular, ¿se necesita que la aplicación de captura funcione igualmente en la tableta? | | |
| POR-04 | Desde cualquier computador, cuando quien consulta abre la consulta web en el navegador que tenga, ¿se necesita que funcione allí sin instalar nada? | | |
| POR-05 | Al iniciar la jornada, cuando quien captura usa su propio dispositivo personal en lugar de uno de la empresa, ¿se necesita que la aplicación de captura funcione igual en ese dispositivo? | | |
| POR-06 | Al cambiar de dispositivo, cuando quien captura pasa a otro equipo con información aún sin sincronizar, ¿se necesita que el sistema conserve esa información y la deje disponible en el nuevo dispositivo? | | |
| POR-07 | En una misma jornada, cuando una misma persona usa dos dispositivos distintos, ¿se necesita que el sistema admita ambos y consolide lo capturado en los dos sin duplicar ni perder nada? | | |
| POR-08 | Con una finca ubicada en otro país, cuando allí se pone en marcha el sistema, ¿se necesita que opere en esa finca con las mismas funciones? | | |
| POR-09 | Al entrar al sistema, cuando quien lo usa no trabaja en español, ¿se necesita que el sistema le presente todo en otro idioma? | | |
| POR-10 | Al capturar o consultar, cuando la finca trabaja con unidades de medida distintas, ¿se necesita que el sistema opere con las unidades de esa finca, sin obligarla a convertirlas? | | |
| POR-11 | Al terminar la relación con el proveedor, cuando la empresa solicita su información, ¿se necesita que el sistema se la entregue completa y en un formato utilizable fuera de él? | | |
| POR-12 | En la puesta en marcha, cuando la empresa quiere alojar el sistema en sus propios servidores, ¿se necesita que el sistema se pueda instalar allí y no solamente en la nube? | | |
| POR-13 | Con la operación en marcha, cuando la empresa empieza a manejar un tipo de flor distinto del actual, ¿se necesita que el sistema lo soporte sin rehacerlo? | | |

---

## 13. Accesibilidad

*Que todo el que deba usarlo pueda usarlo.*  
*Puntuación mini QAW: 30 · 11 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| ACC-01 | En plena jornada de captura dentro del bloque, cuando quien captura tiene poca experiencia en el manejo de celulares, ¿se necesita que la aplicación de captura le permita registrar una cama completa por sí solo? | | |
| ACC-02 | En plena jornada de captura dentro del bloque, cuando quien captura tiene dificultad para leer textos largos, ¿se necesita que la aplicación de captura le permita entender y completar la captura sin depender de textos extensos? | | |
| ACC-03 | En plena jornada de captura dentro del bloque, cuando quien captura debe escoger entre varias opciones, ¿se necesita que la aplicación de captura las identifique con imágenes o símbolos además de con palabras? | | |
| ACC-04 | En plena jornada de captura dentro del bloque, cuando quien captura aumenta el tamaño de la letra, ¿se necesita que la aplicación de captura siga funcionando completa, sin que se pierda ni se corte ningún elemento de la pantalla? | | |
| ACC-05 | En plena jornada de captura dentro del bloque, cuando la aplicación indica un estado con color, ¿se necesita que ese estado se entienda también sin el color, por texto o por símbolo? | | |
| ACC-06 | En plena jornada de captura dentro del bloque, cuando quien captura no distingue ciertos colores, ¿se necesita que la aplicación de captura le permita completar la captura sin equivocarse por esa causa? | | |
| ACC-07 | En el cultivo y con las manos ocupadas o enguantadas, cuando quien captura toca un botón, ¿se necesita que la aplicación de captura registre el toque sin exigir precisión fina? | | |
| ACC-08 | En plena jornada de captura dentro del bloque, cuando quien captura no puede leer la pantalla, ¿se necesita que la aplicación de captura le lea en voz alta el contenido en lugar de exigirle leerlo? | | |
| ACC-09 | En un ambiente ruidoso del cultivo, cuando la aplicación necesita avisar algo, ¿se necesita que quien captura lo perciba sin depender del sonido? | | |
| ACC-10 | En la consulta web, cuando quien consulta la usa con ayudas técnicas de accesibilidad, ¿se necesita que cumpla un estándar formal de accesibilidad verificable? | | |
| ACC-11 | En plena jornada de captura, cuando alguien no puede usar el dispositivo, ¿se necesita que exista una manera alternativa de reportar la información de esa jornada e incorporarla al sistema? | | |

---

## Cierre de la sesión

Tres preguntas que ordenan todo lo anterior y rompen los empates del ranking. **No son escenarios:** no describen una situación del sistema, sino que priorizan entre las anteriores. Por eso se dejan tal como están.

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| FIN-01 | De todo lo que respondió «Sí», ¿hay algo que, si el sistema no lo cumple, haría que no valga la pena usarlo? | | |
| FIN-02 | ¿Hay alguna de estas exigencias que esté dispuesto a sacrificar con tal de que la captura en campo sea más rápida? | | |
| FIN-03 | ¿Hay algo que necesite el sistema y que ninguna de estas preguntas haya tocado? | | |
