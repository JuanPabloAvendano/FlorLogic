# FlorLogic — Preguntas de caracterización por atributo de calidad

**Versión 2 · 21-ago-2026** · reemplaza la v1 del mismo día
**Precede a:** los escenarios de calidad (`ESC-nnn`, formato de dos columnas ya acordado)

## Qué cambió frente a la v1

- **Seguridad de funcionamiento desaparece como atributo propio.** Queda absorbida por **Confiabilidad**, que ahora cubre también la pérdida y el daño de información ante fallos (bloque 1.6). Sus 15 preguntas se conservan íntegras, renumeradas como `CNF-43`..`CNF-57`.
- **Trazabilidad pasa a llamarse Capacidad para ser Auditado**, entendida como la mezcla de **trazabilidad + cumplimiento** con la que quedó definida en el nuevo mini QAW. Las 18 preguntas anteriores se conservan como el bloque de trazabilidad y se suman **14 preguntas nuevas** de cumplimiento y evidencia ante terceros (certificaciones, autoridad fitosanitaria, auditor externo, retención normativa, datos personales).
- **Los atributos van en el orden del nuevo mini QAW** (menor puntuación = mayor prioridad), no en el orden de la plantilla anterior.
- **El prefijo de Escalabilidad cambia de `ESC-` a `ESL-`**, para no colisionar con la numeración `ESC-nnn` de los escenarios de calidad.
- Total: **262 preguntas** sobre **13 atributos**, más 3 de cierre.

## Ranking del nuevo mini QAW

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

- Todas se responden **Sí / No**. Si una respuesta necesita matiz, va en la columna *Nota*, no en la pregunta.
- Están **escalonadas a propósito**: dentro de cada bloque las preguntas recorren la misma idea de menor a mayor exigencia (dato → sección → cama → bloque → finca; en el momento → al sincronizar → después). El punto donde el cliente pasa de «Sí» a «No» **es la medida de respuesta** del escenario que se va a escribir. Por eso el orden de las filas no se altera.
- **No mencionan roles.** Se pregunta por la necesidad, no por quién la tiene. Si al responder el cliente atribuye la necesidad a alguien en particular, eso se anota: es un hallazgo, no parte de la pregunta.
- Un «Sí» no obliga a escribir un escenario; obliga a decidir. Un «No» **también sirve**: cierra alcance y justifica descartes.

---

## 1. Confiabilidad

*Que la información sea correcta, que el sistema no entregue resultados equivocados y que nada se pierda cuando algo falla. Absorbe lo que antes se llamaba Seguridad de funcionamiento.*  
*Puntuación mini QAW: 3 · 57 preguntas*

### 1.1 Verificación en el momento de la captura

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-01 | ¿Se necesita verificar que la información sea correcta de manera individual con cada dato tomado? | | |
| CNF-02 | ¿Se necesita verificar que la información sea correcta de manera que se agrupe la información por variedad? | | |
| CNF-03 | ¿Se necesita verificar que la información sea correcta de manera que se agrupe la información por sección de la cama? | | |
| CNF-04 | ¿Se necesita verificar que la información sea correcta de manera que se agrupe la información por cama? | | |
| CNF-05 | ¿Se necesita verificar que la información sea correcta de manera que se agrupe la información por bloque? | | |
| CNF-06 | ¿Se necesita verificar que la información sea correcta de manera que se agrupe la información por finca y por jornada completa? | | |
| CNF-07 | ¿Se necesita una confirmación final que muestre todo lo capturado antes de darlo por guardado? | | |
| CNF-08 | ¿Se necesita que el sistema impida cerrar la captura de una cama si quedaron campos sin diligenciar? | | |
| CNF-09 | ¿Se necesita que el sistema permita guardar una captura incompleta y marcarla como pendiente? | | |

### 1.2 Reglas de validación

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-10 | ¿Se necesita que el sistema rechace un dato cuando está fuera del rango posible (por ejemplo, más tallos que plantas sembradas)? | | |
| CNF-11 | ¿Se necesita que el sistema advierta, sin bloquear, cuando un dato se aleja mucho de lo que se ha capturado históricamente en esa misma cama? | | |
| CNF-12 | ¿Se necesita que el sistema muestre siempre el motivo por el cual rechazó un dato? | | |
| CNF-13 | ¿Se necesita que los rangos y las reglas de validación se puedan cambiar sin depender de una nueva versión de la aplicación? | | |
| CNF-14 | ¿Se necesita que las reglas de validación funcionen igual estando sin conexión que estando conectado? | | |
| CNF-15 | ¿Se necesita que el sistema avise cuando se intenta capturar dos veces la misma cama el mismo día? | | |
| CNF-16 | ¿Se necesita que el sistema avise cuando una cama lleva más días de lo normal sin ser capturada? | | |
| CNF-17 | ¿Se necesita que el sistema impida registrar una labor sobre una cama que ya fue erradicada? | | |
| CNF-18 | ¿Se necesita que el sistema bloquee la captura cuando detecta que la fecha y la hora del dispositivo fueron alteradas? | | |

### 1.3 Forma de capturar

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-19 | ¿Se necesita realizar la captura de datos de manera progresiva y guiada pantalla por pantalla (primero se selecciona bloque, luego cama, luego se captura lote y fecha de siembra, después las variedades plantadas)? | | |
| CNF-20 | ¿Se necesita realizar la captura de datos con una plantilla predefinida que use una estructura similar a las plantillas de papel utilizadas previamente? | | |
| CNF-21 | ¿Se necesita que la estructura de captura sea siempre la misma para todos los bloques de la finca? | | |
| CNF-22 | ¿Se necesita que la estructura de captura pueda cambiar según la variedad que se esté trabajando? | | |
| CNF-23 | ¿Se necesita que el sistema proponga automáticamente el valor capturado la última vez para esa misma cama? | | |
| CNF-24 | ¿Se necesita que el orden de captura siga el recorrido físico que se hace dentro del bloque? | | |
| CNF-25 | ¿Se necesita que el sistema indique en todo momento cuántas camas faltan por capturar en la jornada? | | |

### 1.4 Corrección y conflicto

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-26 | ¿Se necesita poder corregir un dato ya capturado antes de que se sincronice? | | |
| CNF-27 | ¿Se necesita poder corregir un dato después de que se sincronizó? | | |
| CNF-28 | ¿Se necesita que al corregir un dato se conserve visible el valor original? | | |
| CNF-29 | ¿Se necesita que toda corrección exija escribir un motivo? | | |
| CNF-30 | ¿Se necesita que exista una fecha a partir de la cual la información ya no se pueda corregir? | | |
| CNF-31 | ¿Se necesita que, cuando dos capturas de la misma cama entran en conflicto, el sistema resuelva solo tomando la más reciente? | | |
| CNF-32 | ¿Se necesita que, cuando dos capturas de la misma cama entran en conflicto, el sistema deje ambas y pida que una persona decida? | | |
| CNF-33 | ¿Se necesita que quien capturó el dato se entere cuando su captura fue descartada o modificada por un conflicto? | | |

### 1.5 Confianza en el resultado

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-34 | ¿Se necesita saber, sobre cada proyección, con qué datos y con qué parámetros fue calculada? | | |
| CNF-35 | ¿Se necesita que una misma proyección, recalculada meses después, dé exactamente el mismo resultado? | | |
| CNF-36 | ¿Se necesita que el sistema muestre cuánto se desvió la proyección anterior frente a lo que realmente se cortó? | | |
| CNF-37 | ¿Se necesita que el sistema muestre un indicador del porcentaje de datos con error o pendientes de verificación? | | |
| CNF-38 | ¿Se necesita fijar una meta explícita de porcentaje máximo de error aceptable en la información capturada? | | |
| CNF-39 | ¿Se necesita que el sistema distinga la información verificada de la que todavía no lo ha sido? | | |
| CNF-40 | ¿Se necesita que la información no verificada quede excluida de las proyecciones hasta que se verifique? | | |
| CNF-41 | ¿Se necesita seguir llenando el formato en papel en paralelo durante los primeros meses de uso? | | |
| CNF-42 | ¿Se necesita que el sistema permita comparar lo capturado en la aplicación contra el formato en papel del mismo día? | | |

### 1.6 Comportamiento ante fallos y pérdida de información

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CNF-43 | ¿Se necesita que no se pierda ningún dato capturado bajo ninguna circunstancia? | | |
| CNF-44 | ¿Se necesita que la información se conserve cuando el dispositivo se apaga por batería agotada a mitad de una captura? | | |
| CNF-45 | ¿Se necesita que la información se conserve cuando la aplicación se cierra de manera inesperada? | | |
| CNF-46 | ¿Se necesita que la información se conserve cuando el dispositivo se moja o se golpea y hay que cambiarlo? | | |
| CNF-47 | ¿Se necesita que una sincronización interrumpida a la mitad pueda retomarse sin duplicar ni perder información? | | |
| CNF-48 | ¿Se necesita que el sistema haga copias de respaldo de manera automática, sin que nadie lo solicite? | | |
| CNF-49 | ¿Se necesita que las copias de respaldo se hagan al menos una vez al día? | | |
| CNF-50 | ¿Se necesita poder devolver la información a como estaba en una fecha anterior? | | |
| CNF-51 | ¿Se necesita poder devolver la información de una sola finca sin tocar la de las demás? | | |
| CNF-52 | ¿Se necesita que la información nunca se elimine, ni siquiera cuando se erradica una cama? | | |
| CNF-53 | ¿Se necesita conservar tanto la información tal como se capturó como la información ya corregida? | | |
| CNF-54 | ¿Se necesita que el sistema avise cuando una proyección se calculó con información incompleta? | | |
| CNF-55 | ¿Se necesita que el sistema impida publicar una proyección cuando faltan datos de bloques enteros? | | |
| CNF-56 | ¿Se necesita que exista una prueba periódica de que las copias de respaldo efectivamente sirven? | | |
| CNF-57 | ¿Se necesita que la aplicación avise cuando el almacenamiento del dispositivo está por llenarse? | | |

---

## 2. Disponibilidad

*Que se pueda usar cuando se necesita, incluso sin conexión o con fallos.*  
*Puntuación mini QAW: 11 · 20 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| DSP-01 | ¿Se necesita capturar información en el área de cultivo sin ninguna conexión de datos? | | |
| DSP-02 | ¿Se necesita capturar durante una jornada completa sin sincronizar en ningún momento? | | |
| DSP-03 | ¿Se necesita capturar durante tres días seguidos sin sincronizar? | | |
| DSP-04 | ¿Se necesita capturar durante una semana seguida sin sincronizar? | | |
| DSP-05 | ¿Se necesita capturar durante más de quince días sin sincronizar? | | |
| DSP-06 | ¿Se necesita poder consultar la información ya sincronizada aunque en ese momento no haya conexión? | | |
| DSP-07 | ¿Se necesita poder consultar la información desde la web aunque la aplicación de captura esté fuera de servicio? | | |
| DSP-08 | ¿Se necesita que la captura siga funcionando aunque el servicio en la nube esté caído? | | |
| DSP-09 | ¿Se necesita que exista un horario del día en el que el sistema no pueda estar fuera de servicio bajo ninguna circunstancia? | | |
| DSP-10 | ¿Se necesita que el sistema esté disponible los siete días de la semana? | | |
| DSP-11 | ¿Se necesita que el sistema esté disponible las veinticuatro horas del día? | | |
| DSP-12 | ¿Se necesita que exista un periodo del año (temporada alta) con una exigencia de disponibilidad mayor que el resto? | | |
| DSP-13 | ¿Se necesita que una interrupción del servicio se resuelva en menos de una hora? | | |
| DSP-14 | ¿Se necesita que una interrupción del servicio se resuelva dentro del mismo día? | | |
| DSP-15 | ¿Se necesita que las labores de mantenimiento se avisen con anticipación? | | |
| DSP-16 | ¿Se necesita que las labores de mantenimiento se hagan sin sacar el sistema de servicio? | | |
| DSP-17 | ¿Se necesita poder continuar la captura en otro dispositivo cuando el que se estaba usando deja de funcionar? | | |
| DSP-18 | ¿Se necesita que la información capturada en un dispositivo que se dañó o se perdió pueda recuperarse? | | |
| DSP-19 | ¿Se necesita que una falla que afecte a una finca no afecte a las demás fincas o empresas? | | |
| DSP-20 | ¿Se necesita que el sistema avise cuando un dispositivo lleva demasiado tiempo sin sincronizar? | | |

---

## 3. Rendimiento

*Que responda con la rapidez que exige el ritmo del campo.*  
*Puntuación mini QAW: 17 · 21 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| RND-01 | ¿Se necesita que registrar una cama completa tome menos tiempo del que hoy toma llenarla en papel? | | |
| RND-02 | ¿Se necesita que registrar una cama completa tome menos de un minuto? | | |
| RND-03 | ¿Se necesita que registrar una cama completa tome menos de treinta segundos? | | |
| RND-04 | ¿Se necesita poder capturar un bloque completo dentro de una sola jornada? | | |
| RND-05 | ¿Se necesita poder capturar la finca completa dentro de una sola jornada? | | |
| RND-06 | ¿Se necesita que cada dato quede guardado de inmediato, sin espera perceptible? | | |
| RND-07 | ¿Se necesita que el paso de una pantalla a la siguiente sea inmediato aun sin conexión? | | |
| RND-08 | ¿Se necesita que buscar una cama por su código dé resultado de inmediato? | | |
| RND-09 | ¿Se necesita que la sincronización de una jornada completa termine en menos de cinco minutos? | | |
| RND-10 | ¿Se necesita que la sincronización ocurra en segundo plano, sin impedir seguir capturando? | | |
| RND-11 | ¿Se necesita que muchos dispositivos puedan sincronizar al mismo tiempo al terminar la jornada sin que el sistema se degrade? | | |
| RND-12 | ¿Se necesita que la información sincronizada se vea reflejada en la consulta web de inmediato? | | |
| RND-13 | ¿Se necesita que la información sincronizada se vea reflejada en la consulta web dentro de la misma hora? | | |
| RND-14 | ¿Se necesita que la información sincronizada se vea reflejada al día siguiente? | | |
| RND-15 | ¿Se necesita que un reporte de un mes se genere en menos de diez segundos? | | |
| RND-16 | ¿Se necesita que un reporte que abarque varios años se genere sin espera significativa? | | |
| RND-17 | ¿Se necesita que la proyección se recalcule en el momento en que llega información nueva? | | |
| RND-18 | ¿Se necesita que la proyección se recalcule una vez al día en un horario fijo? | | |
| RND-19 | ¿Se necesita que la aplicación funcione con fluidez en dispositivos de gama baja o de varios años de antigüedad? | | |
| RND-20 | ¿Se necesita que la aplicación no agote la batería del dispositivo antes de terminar la jornada? | | |
| RND-21 | ¿Se necesita que el rendimiento se mantenga igual en temporada alta que en el resto del año? | | |

---

## 4. Capacidad para ser Auditado

*Que se pueda reconstruir quién hizo qué y con qué información, y demostrarlo ante quien lo exija. Mezcla trazabilidad y cumplimiento.*  
*Puntuación mini QAW: 17 · 32 preguntas*

### 4.1 Trazabilidad de la información

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| AUD-01 | ¿Se necesita saber quién capturó cada dato? | | |
| AUD-02 | ¿Se necesita saber la fecha y la hora exactas en que se capturó cada dato? | | |
| AUD-03 | ¿Se necesita distinguir la hora en que se capturó de la hora en que se sincronizó? | | |
| AUD-04 | ¿Se necesita saber desde qué dispositivo se capturó cada dato? | | |
| AUD-05 | ¿Se necesita saber en qué lugar físico se encontraba el dispositivo al capturar? | | |
| AUD-06 | ¿Se necesita conservar todas las versiones anteriores de un dato modificado, y no solo la última? | | |
| AUD-07 | ¿Se necesita saber quién hizo cada modificación y por qué motivo? | | |
| AUD-08 | ¿Se necesita poder ver la historia completa de una cama desde la siembra hasta la erradicación? | | |
| AUD-09 | ¿Se necesita poder ver la historia completa de un lote a través de varias camas? | | |
| AUD-10 | ¿Se necesita poder ver la historia completa de una variedad a través de varios ciclos? | | |
| AUD-11 | ¿Se necesita poder llegar, desde una cifra de un reporte, hasta los datos individuales que la componen? | | |
| AUD-12 | ¿Se necesita registrar también quién consultó o exportó la información, y no solo quién la modificó? | | |
| AUD-13 | ¿Se necesita que el registro de lo ocurrido no pueda ser alterado ni borrado por nadie? | | |
| AUD-14 | ¿Se necesita poder exportar ese registro para entregarlo a un tercero? | | |
| AUD-15 | ¿Se necesita conservar la trazabilidad durante al menos cinco años? | | |
| AUD-16 | ¿Se necesita conservar la trazabilidad de manera indefinida? | | |
| AUD-17 | ¿Se necesita registrar el motivo cada vez que disminuye la cantidad de plantas o de tallos esperados? | | |
| AUD-18 | ¿Se necesita poder demostrar ante una auditoría externa el origen de cualquier cifra del sistema? | | |

### 4.2 Cumplimiento y evidencia ante terceros

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| AUD-19 | ¿Se necesita responder auditorías de certificación (por ejemplo Florverde, Rainforest Alliance o GLOBALG.A.P.) con información sacada del sistema? | | |
| AUD-20 | ¿Se necesita conservar dentro del sistema la evidencia de las labores aplicadas a un cultivo que exige la autoridad fitosanitaria? | | |
| AUD-21 | ¿Se necesita poder entregar a un comprador la historia completa del lote que adquirió? | | |
| AUD-22 | ¿Se necesita que un auditor externo pueda consultar el sistema directamente, sin poder modificar nada? | | |
| AUD-23 | ¿Se necesita que ese acceso de consulta externa tenga fecha de vencimiento automática? | | |
| AUD-24 | ¿Se necesita generar el informe para una auditoría desde el propio sistema, sin ayuda técnica? | | |
| AUD-25 | ¿Se necesita poder demostrar que la información no fue alterada después de haberse cerrado el periodo? | | |
| AUD-26 | ¿Se necesita que quede constancia de quién ha visto la información que está protegida como secreto empresarial? | | |
| AUD-27 | ¿Se necesita conservar la información durante un periodo mínimo exigido por norma, aunque ya no se use en la operación? | | |
| AUD-28 | ¿Se necesita que exista un momento en que la información se deba eliminar por obligación normativa? | | |
| AUD-29 | ¿Se necesita que los datos personales de quienes capturan reciban un tratamiento distinto al de los datos de producción? | | |
| AUD-30 | ¿Se necesita registrar quién aprobó una proyección antes de que se comunique fuera del área? | | |
| AUD-31 | ¿Se necesita que las revisiones de calidad de la información queden registradas como evidencia, y no solo su resultado? | | |
| AUD-32 | ¿Se necesita poder demostrar con información del propio sistema el cumplimiento de un compromiso adquirido con un tercero? | | |

---

## 5. Capacidad

*Cuánta información tiene que soportar y por cuánto tiempo.*  
*Puntuación mini QAW: 19 · 15 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| CAP-01 | ¿Se necesita que el sistema soporte toda la información de una finca completa? | | |
| CAP-02 | ¿Se necesita que el sistema soporte varias fincas de la misma empresa? | | |
| CAP-03 | ¿Se necesita que el sistema soporte varias empresas distintas al mismo tiempo? | | |
| CAP-04 | ¿Se necesita conservar en línea la información del último año? | | |
| CAP-05 | ¿Se necesita conservar en línea la información de los últimos cinco años? | | |
| CAP-06 | ¿Se necesita conservar en línea toda la información desde el primer día, sin límite? | | |
| CAP-07 | ¿Se necesita que la información antigua siga estando disponible con la misma rapidez que la reciente? | | |
| CAP-08 | ¿Se aceptaría que la información de más de cierta antigüedad tarde más en consultarse? | | |
| CAP-09 | ¿Se necesita almacenar fotografías tomadas en campo junto con los datos? | | |
| CAP-10 | ¿Se necesita almacenar documentos o formatos escaneados asociados a una cama o a un lote? | | |
| CAP-11 | ¿Se necesita conservar a la vez la copia sin modificar y la copia corregida de cada dato? | | |
| CAP-12 | ¿Se necesita que el dispositivo pueda guardar la información de varios días sin sincronizar sin quedarse sin espacio? | | |
| CAP-13 | ¿Se necesita que el dispositivo tenga descargado todo el catálogo de la finca antes de salir a capturar? | | |
| CAP-14 | ¿Se necesita prever un crecimiento del volumen de información año tras año sin cambiar de sistema? | | |
| CAP-15 | ¿Se necesita que el costo de almacenamiento por finca se mantenga acotado a medida que crece la historia? | | |

---

## 6. Capacidad para ser Administrado

*Que el día a día del sistema se pueda manejar desde adentro, sin desarrollo.*  
*Puntuación mini QAW: 23 · 16 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| ADM-01 | ¿Se necesita poder crear y dar de baja usuarios sin solicitarlo a quien desarrolló el sistema? | | |
| ADM-02 | ¿Se necesita poder cambiar los permisos de un usuario de manera inmediata? | | |
| ADM-03 | ¿Se necesita poder retirar el acceso de un usuario el mismo día en que deja de trabajar en la finca? | | |
| ADM-04 | ¿Se necesita poder crear bloques, camas y secciones nuevas sin desarrollo adicional? | | |
| ADM-05 | ¿Se necesita poder crear variedades nuevas sin desarrollo adicional? | | |
| ADM-06 | ¿Se necesita poder cambiar los grados de calidad y sus definiciones sin desarrollo adicional? | | |
| ADM-07 | ¿Se necesita poder cambiar la densidad de siembra o los parámetros de cálculo sin desarrollo adicional? | | |
| ADM-08 | ¿Se necesita que un cambio de parámetros no altere las proyecciones ya emitidas? | | |
| ADM-09 | ¿Se necesita ver en una sola pantalla qué dispositivos tienen información pendiente de sincronizar? | | |
| ADM-10 | ¿Se necesita ver en una sola pantalla el avance de captura del día por bloque? | | |
| ADM-11 | ¿Se necesita poder forzar la sincronización de un dispositivo de manera remota? | | |
| ADM-12 | ¿Se necesita poder actualizar la aplicación en todos los dispositivos sin recogerlos uno por uno? | | |
| ADM-13 | ¿Se necesita que una actualización nunca obligue a suspender la captura del día? | | |
| ADM-14 | ¿Se necesita poder registrar por adelantado el calendario de temporadas y días no laborables? | | |
| ADM-15 | ¿Se necesita que la administración del sistema se pueda hacer desde un computador de la finca, sin herramientas técnicas especiales? | | |
| ADM-16 | ¿Se necesita que quien administra el sistema en la finca no pueda modificar la información de producción? | | |

---

## 7. Experiencia de Usuario

*Que capturar sea más fácil que el papel, en las condiciones del cultivo.*  
*Puntuación mini QAW: 24 · 18 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| UXP-01 | ¿Se necesita que la captura se pueda completar con una sola mano? | | |
| UXP-02 | ¿Se necesita que la captura se pueda hacer con guantes puestos? | | |
| UXP-03 | ¿Se necesita que la pantalla sea legible bajo el sol directo? | | |
| UXP-04 | ¿Se necesita que la mayoría de los datos se capturen escogiendo de una lista y no escribiendo? | | |
| UXP-05 | ¿Se necesita reducir al mínimo la cantidad de toques necesarios para registrar una cama? | | |
| UXP-06 | ¿Se necesita poder deshacer el último dato registrado sin salir de la pantalla? | | |
| UXP-07 | ¿Se necesita ver en todo momento cuánto se lleva capturado y cuánto falta de la jornada? | | |
| UXP-08 | ¿Se necesita que la aplicación indique con claridad qué información ya se sincronizó y cuál no? | | |
| UXP-09 | ¿Se necesita que los nombres que aparecen en pantalla sean exactamente los que se usan hablando en la finca? | | |
| UXP-10 | ¿Se necesita que la aplicación se pueda usar sin capacitación formal previa? | | |
| UXP-11 | ¿Se necesita que una persona nueva pueda capturar correctamente su primera cama en menos de diez minutos de acompañamiento? | | |
| UXP-12 | ¿Se necesita poder identificar la cama escaneando una marca física en lugar de escribir el código? | | |
| UXP-13 | ¿Se necesita poder capturar dictando por voz en lugar de escribiendo? | | |
| UXP-14 | ¿Se necesita que el sistema sugiera valores mientras se captura, siempre que la sugerencia se pueda rechazar? | | |
| UXP-15 | ¿Se necesita que la consulta de resultados se pueda hacer desde un celular y no solo desde un computador? | | |
| UXP-16 | ¿Se necesita que cada quien pueda armar su propia vista de consulta sin pedirla a nadie? | | |
| UXP-17 | ¿Se necesita ver el estado de las camas sobre una representación gráfica del bloque y no solo en una lista? | | |
| UXP-18 | ¿Se necesita que los mensajes de error expliquen qué hacer y no solo qué salió mal? | | |

---

## 8. Seguridad

*Que la información no llegue a quien no debe.*  
*Puntuación mini QAW: 24 · 19 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| SEG-01 | ¿Se necesita que cada persona entre al sistema con una identificación propia y no compartida? | | |
| SEG-02 | ¿Se necesita poder entrar a la aplicación de captura estando sin conexión? | | |
| SEG-03 | ¿Se necesita que la sesión se cierre sola después de un tiempo sin uso? | | |
| SEG-04 | ¿Se necesita que la información guardada en el dispositivo quede ilegible si el dispositivo se pierde o lo roban? | | |
| SEG-05 | ¿Se necesita poder borrar de manera remota la información de un dispositivo perdido? | | |
| SEG-06 | ¿Se necesita que la información de una empresa nunca pueda ser vista desde otra empresa, bajo ninguna circunstancia? | | |
| SEG-07 | ¿Se necesita poder demostrar documentalmente ese aislamiento ante el cliente? | | |
| SEG-08 | ¿Se necesita que quien opera la plataforma no pueda leer la información de producción de la empresa? | | |
| SEG-09 | ¿Se necesita que todo acceso técnico a la información quede registrado y sea revisable? | | |
| SEG-10 | ¿Se necesita que la información viaje cifrada entre el dispositivo y la nube? | | |
| SEG-11 | ¿Se necesita que las copias de respaldo estén cifradas? | | |
| SEG-12 | ¿Se necesita que la llave para descifrar los respaldos sea distinta para cada empresa? | | |
| SEG-13 | ¿Se necesita restringir quién puede exportar información fuera del sistema? | | |
| SEG-14 | ¿Se necesita que quede registro de cada exportación realizada? | | |
| SEG-15 | ¿Se necesita marcar los archivos exportados para poder identificar su origen si se filtran? | | |
| SEG-16 | ¿Se necesita que el personal temporal tenga un acceso que caduque solo al terminar su contrato? | | |
| SEG-17 | ¿Se necesita que el sistema avise cuando alguien intenta entrar repetidamente sin lograrlo? | | |
| SEG-18 | ¿Se necesita que exista información que no pueda salir del país donde está la finca? | | |
| SEG-19 | ¿Se aceptaría que dentro de una misma empresa toda la información sea visible para todos sus usuarios? | | |

---

## 9. Interoperatividad

*Qué tanto tiene que hablar con lo que ya existe.*  
*Puntuación mini QAW: 25 · 13 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| INT-01 | ¿Se necesita poder exportar la información a Excel? | | |
| INT-02 | ¿Se necesita poder exportar los reportes a PDF? | | |
| INT-03 | ¿Se necesita que la exportación conserve exactamente el formato de los archivos que hoy se comparten? | | |
| INT-04 | ¿Se necesita cargar la información inicial de la finca desde los archivos que ya existen? | | |
| INT-05 | ¿Se necesita que el sistema entregue información al sistema administrativo que la empresa ya tiene? | | |
| INT-06 | ¿Se necesita que el sistema tome información del sistema administrativo que la empresa ya tiene? | | |
| INT-07 | ¿Se necesita que el sistema conviva con la aplicación de plagas sin reemplazarla? | | |
| INT-08 | ¿Se necesita que el sistema consuma la información que produce la aplicación de plagas? | | |
| INT-09 | ¿Se necesita que el sistema entregue información al proceso de nómina o de productividades? | | |
| INT-10 | ¿Se necesita que la información se pueda leer desde una herramienta de análisis externa? | | |
| INT-11 | ¿Se necesita que exista una manera automática de conectar el sistema con otros programas, sin intervención manual? | | |
| INT-12 | ¿Se necesita que la información se pueda entregar a un cliente o a un comercializador externo? | | |
| INT-13 | ¿Se aceptaría que, en la primera entrega, la única forma de intercambio sea la exportación manual a Excel y PDF? | | |

---

## 10. Escalabilidad

*Que crecer no obligue a rehacerlo.*  
*Puntuación mini QAW: 25 · 11 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| ESL-01 | ¿Se necesita poder agregar bloques y camas nuevas en plena temporada sin interrumpir la operación? | | |
| ESL-02 | ¿Se necesita poder duplicar la cantidad de personas que capturan sin que el sistema se vuelva más lento? | | |
| ESL-03 | ¿Se necesita poder agregar una finca nueva sin volver a instalar ni reconfigurar todo? | | |
| ESL-04 | ¿Se necesita poder agregar una empresa nueva sin afectar a las que ya están funcionando? | | |
| ESL-05 | ¿Se necesita soportar el aumento de actividad de la temporada alta sin degradación perceptible? | | |
| ESL-06 | ¿Se necesita soportar que varias empresas tengan su pico de temporada exactamente en las mismas fechas? | | |
| ESL-07 | ¿Se necesita que el crecimiento del sistema no implique un aumento proporcional del costo por finca? | | |
| ESL-08 | ¿Se necesita que un cambio en la estructura del sistema se aplique a todas las empresas sin intervención manual una por una? | | |
| ESL-09 | ¿Se necesita que el sistema soporte varios años de historia acumulada sin que las consultas se vuelvan lentas? | | |
| ESL-10 | ¿Se necesita prever el uso simultáneo de más de treinta personas subiendo información el mismo día? | | |
| ESL-11 | ¿Se necesita que agregar un tipo de labor o de medición nueva no exija rehacer la captura existente? | | |

---

## 11. Capacidad para ser Soportado

*Qué pasa cuando algo falla y hay que resolverlo.*  
*Puntuación mini QAW: 27 · 13 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| SOP-01 | ¿Se necesita que exista alguien dentro de la finca capaz de resolver los problemas del día a día? | | |
| SOP-02 | ¿Se necesita que un problema en plena jornada de captura se resuelva el mismo día? | | |
| SOP-03 | ¿Se necesita que un problema en plena jornada de captura se resuelva en menos de una hora? | | |
| SOP-04 | ¿Se necesita poder reportar un problema desde la misma aplicación, sin llamar por teléfono? | | |
| SOP-05 | ¿Se necesita que el reporte de un problema incluya automáticamente lo que estaba haciendo la persona cuando ocurrió? | | |
| SOP-06 | ¿Se necesita poder diagnosticar un problema sin tener que desplazarse hasta la finca? | | |
| SOP-07 | ¿Se necesita poder revisar el estado de un dispositivo de forma remota? | | |
| SOP-08 | ¿Se necesita que el soporte esté disponible en horario extendido durante la temporada alta? | | |
| SOP-09 | ¿Se necesita que exista un manual de uso escrito? | | |
| SOP-10 | ¿Se necesita que existan videos cortos de apoyo dentro de la propia aplicación? | | |
| SOP-11 | ¿Se necesita que la persona que capturó pueda seguir trabajando mientras su problema se resuelve? | | |
| SOP-12 | ¿Se necesita llevar un registro de los problemas reportados y de cómo se resolvieron? | | |
| SOP-13 | ¿Se necesita que la puesta en marcha del sistema no detenga la operación por más de una semana? | | |

---

## 12. Portabilidad

*Dónde y sobre qué tiene que poder funcionar.*  
*Puntuación mini QAW: 28 · 13 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| POR-01 | ¿Se necesita que la aplicación de captura funcione en dispositivos Android? | | |
| POR-02 | ¿Se necesita que la aplicación de captura funcione también en dispositivos Apple? | | |
| POR-03 | ¿Se necesita que la aplicación funcione en tabletas además de en celulares? | | |
| POR-04 | ¿Se necesita que la consulta funcione en cualquier navegador, sin instalar nada? | | |
| POR-05 | ¿Se necesita que el dispositivo de captura lo ponga cada persona y no la empresa? | | |
| POR-06 | ¿Se necesita poder cambiar de dispositivo conservando la información que aún no se ha sincronizado? | | |
| POR-07 | ¿Se necesita que la misma persona pueda usar dos dispositivos distintos el mismo día? | | |
| POR-08 | ¿Se necesita que el sistema pueda operar en una finca ubicada en otro país? | | |
| POR-09 | ¿Se necesita que el sistema maneje otro idioma además del español? | | |
| POR-10 | ¿Se necesita que el sistema maneje unidades de medida distintas según la finca? | | |
| POR-11 | ¿Se necesita poder llevarse toda la información en un formato utilizable si se termina la relación con el proveedor? | | |
| POR-12 | ¿Se necesita que el sistema pueda instalarse en servidores de la propia empresa y no solo en la nube? | | |
| POR-13 | ¿Se necesita que el sistema pueda funcionar con un tipo de flor distinto al que se maneja hoy? | | |

---

## 13. Accesibilidad

*Que todo el que deba usarlo pueda usarlo.*  
*Puntuación mini QAW: 30 · 11 preguntas*

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| ACC-01 | ¿Se necesita que el sistema lo pueda usar una persona con poca experiencia en el manejo de celulares? | | |
| ACC-02 | ¿Se necesita que el sistema lo pueda usar una persona con dificultad para leer textos largos? | | |
| ACC-03 | ¿Se necesita que las opciones se identifiquen con imágenes o símbolos además de con palabras? | | |
| ACC-04 | ¿Se necesita que el tamaño de la letra se pueda aumentar sin que la pantalla deje de funcionar? | | |
| ACC-05 | ¿Se necesita que la información nunca dependa solo del color para entenderse? | | |
| ACC-06 | ¿Se necesita que el sistema sea usable por una persona que no distingue ciertos colores? | | |
| ACC-07 | ¿Se necesita que los botones sean lo bastante grandes para acertarles sin precisión fina? | | |
| ACC-08 | ¿Se necesita que la aplicación se pueda escuchar en lugar de leer? | | |
| ACC-09 | ¿Se necesita que la aplicación funcione en un ambiente ruidoso, sin depender del sonido? | | |
| ACC-10 | ¿Se necesita que la consulta web cumpla algún estándar formal de accesibilidad? | | |
| ACC-11 | ¿Se necesita que exista una manera alternativa de reportar la información cuando alguien no puede usar el dispositivo? | | |

---

## Cierre de la sesión

Tres preguntas que ordenan todo lo anterior y rompen los empates del ranking.

| ID | Pregunta | Sí/No | Nota |
|---|---|:--:|---|
| FIN-01 | De todo lo que respondió «Sí», ¿hay algo que, si el sistema no lo cumple, haría que no valga la pena usarlo? | | |
| FIN-02 | ¿Hay alguna de estas exigencias que esté dispuesto a sacrificar con tal de que la captura en campo sea más rápida? | | |
| FIN-03 | ¿Hay algo que necesite el sistema y que ninguna de estas preguntas haya tocado? | | |
