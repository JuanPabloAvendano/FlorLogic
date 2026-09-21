# FlorLogic — Episodio único · guion de podcast entre Juan y Jerónimo

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 18-sep-2026, a pedido de Juan
> Estado: **SIN REVISAR**
> Manda sobre esto: `ADR-PoC-Spikes.xlsx` (36 ADR) · `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx` ·
> `1_VOZ_DEL_CLIENTE.md`
>
> `[!]` **Las réplicas de Jerónimo son un papel escrito, no cosas que él haya dicho.** Se le pusieron
> en la boca las objeciones que están documentadas como contradicciones abiertas. **Él tiene que leerlo
> y corregir lo que no diría.**
>
> Compañeros: **`IA_PRESENTACION-FlorLogic.md`** (guion de 30 min) y
> **`IA_PREGUNTAS-Y-RESPUESTAS-FlorLogic.md`** (tablero `X-01`..`X-20` y 12 sesiones de práctica).
> Las marcas `⚑ X-nn` de este guion apuntan a ese tablero.

---

## Ficha del episodio

| | |
|---|---|
| **Formato** | Dos voces, conversación. Sin invitado, sin público |
| **Duración objetivo** | ~30 min · **≈4.600 palabras** habladas a ritmo normal |
| **Para qué sirve** | Escucharlo hasta que el relato salga solo. Es la misma materia de la exposición, pero en forma de conversación: se memoriza mejor lo que se oye discutir que lo que se lee en viñetas |
| **Voces** | **JUAN** — lleva el hilo del problema y de las decisiones · **JERÓ** — pregunta, duda y pone las objeciones del arquitecto |
| **Cómo grabarlo** | Leerlo de corrido, sin actuar. Donde dice *(pausa)* se respira. Si alguno se sale del guion y lo dice mejor, **se anota y se corrige el texto** |

**Regla del episodio, que además es la regla del proyecto:** cada cifra dice de dónde sale. Se oirá
mucho *«eso lo escribió el cliente»*, *«ese número es nuestro»*, *«eso lo medimos»* y *«eso no lo
sabemos»*. **No quitar esas muletillas: son el contenido.**

---

## `00:00` · Entrada en frío

**JERÓ:** Juan, arranquemos por donde arranca él. Cuando le preguntaron qué haría que este sistema no
valiera la pena, ¿qué contestó?

**JUAN:** Lo escribió en mayúsculas. *«Certeza de los datos, que se ingresen los datos
correctamente.»* Eso está en la caracterización, escrito por él, no es una interpretación nuestra.

**JERÓ:** O sea que el proyecto no se trata de una app bonita para el celular.

**JUAN:** No. Se trata de que el número en el que la finca se apoya para vender flor sea verdad. Todo
lo demás cuelga de ahí.

**JERÓ:** Listo. Entonces este episodio es eso: qué problema encontramos, cómo lo levantamos, qué
decidimos, qué probamos y qué todavía no sabemos. Media hora. Y si en algún punto me estás vendiendo
humo, te paro.

**JUAN:** Eso espero.

*(pausa)*

---

## `01:30` · Qué es FlorLogic, en una frase

**JERÓ:** Dime qué es FlorLogic como si yo no supiera nada.

**JUAN:** Un sistema que captura en campo, sin conexión, la siembra y el corte de flor cama por cama;
proyecta cuánta flor va a haber, de qué variedad y en qué fecha; y mide la desviación entre lo que
proyectó y lo que realmente se cortó.

**JERÓ:** ¿Y plata?

**JUAN:** No maneja plata. Eso lo sacamos del alcance a propósito. La métrica del producto son
**tallos cortados**. Si el sistema dice ochocientos setenta, hay que cortar ochocientos setenta o lo
más cerca posible. Punto.

**JERÓ:** ¿Y quién es el cliente?

**JUAN:** Una finca de flor de corte. Quince hectáreas, veinticinco bloques, unas mil quinientas
veinticinco camas. La jerarquía es finca, bloque, nave, cama, y dentro de la cama la variedad.

**JERÓ:** Ojo ahí, que nave y bloque no sabemos si son lo mismo.

**JUAN:** Cierto, y hay que decirlo cuando toque. En el formato real de la finca la nave no aparece.
Es una de las preguntas que nunca se hicieron.

**JERÓ:** ¿Cuánta gente lo usaría?

**JUAN:** Capturan tres personas: un supervisor y dos auxiliares. Unas doce usarían el sistema, y
unas veinte más solo consultan, que son las vendedoras. Esos números los dio él en la segunda sesión.

---

## `03:30` · El problema real

**JERÓ:** Ahora cuéntame el día de hoy en esa finca. Sin sistema.

**JUAN:** El supervisor sale al invernadero con una hoja y un lápiz. Va cama por cama contando y
anotando. Eso le toma alrededor de una hora al día. Después esas planillas de papel llegan a la
oficina, y un practicante de sistemas las digita: unas cuatro horas, una vez por semana.

**JERÓ:** Un practicante.

**JUAN:** Un practicante rotativo. Y eso él lo nombró como una falla del proceso, con esas palabras:
que es un puesto cambiante, que hay que volver a explicarle a cada uno, que al principio se demora
mucho y se equivoca más de lo normal.

**JERÓ:** ¿Y cuánto tarda el dato en servirle a alguien?

**JUAN:** Ocho días en llegar a planeación y a gerencia.

**JERÓ:** Ocho días. ¿Y la proyección?

**JUAN:** A mano. La persona encargada va físicamente a mirar la cama y estima restando porcentajes:
que esta va bajando, que a esta hay que correrle días. Hoy eso se ajusta mensual. Lo que él quisiera
es semanal.

**JERÓ:** Juan, aquí hay que tener cuidado, porque tú y yo hemos contado esas cifras de dos maneras
distintas. Tú en septiembre dijiste «cuatro horas por tanda de plantillas» y «ocho días de
proyección por las correcciones». Lo que él escribió es «cuatro horas una vez por semana» y «ocho
días hasta planeación».

**JUAN:** Tienes razón, y es de las primeras cosas que hay que cerrar. **En la exposición se usan las
del cliente**, porque están escritas por él. Mi versión es una interpretación mía y hay que
confirmarla con él antes de usarla como argumento comercial. ⚑ `X-01`

**JERÓ:** Bien. ¿Y cuánto le cuesta eso al negocio? Porque «ocho días» a un arquitecto no le dice
nada por sí solo.

**JUAN:** Tres números, todos de él. Uno: la información que se captura y se digita llega a la
proyección **con un dos por ciento de error**. Dos: el presupuesto de ventas queda **alrededor de un
seis por ciento por debajo** de lo que efectivamente se cortó. Tres: **cerca de un ocho por ciento**
de las ventas hubo que cubrirlo comprando flor a terceros, y ahí también están las órdenes que
tocó cancelar.

**JERÓ:** Ahí está el costo.

**JUAN:** Ahí está. Se vende sobre un número que tiene una semana de viejo y un dos por ciento de
error encima. Cuando no cuadra, o se compra afuera o se cancela una venta.

**JERÓ:** ¿Y ese dos por ciento de dónde sale exactamente? Porque nosotros decimos que desaparece
solo.

**JUAN:** Decimos que desaparece **la mitad**. Él dijo que el error ocurre cuando se digitaliza y
también cuando la persona toma el dato la primera vez. La parte de transcripción —papel a
sistema— desaparece por diseño: si no hay transcripción, no hay error de transcripción. La parte de
la captura no desaparece sola: **esa la ataca la validación en la cama**. Si digo que desaparece
todo, estoy mintiendo.

**JERÓ:** Ese matiz sí me gusta para la charla.

*(pausa)*

---

## `07:00` · El reencuadre: no es velocidad

**JERÓ:** Hubo un momento en que cambiamos de idea sobre cuál era el problema. Cuéntalo, porque un
arquitecto valora más eso que un diagrama.

**JUAN:** Durante meses el documento optimizaba **la velocidad de captura**: cuántos toques, cuántos
segundos por cama. Y era porque la velocidad era lo único que sabíamos medir.

**JERÓ:** Y no era el problema.

**JUAN:** No. La captura tiene que durar lo que tiene que durar. El dolor que paga el cliente está
dos pasos más abajo: el traslado del papel a lo digital, y el ciclo de correcciones hasta que la
proyección es confiable. Cuando entendimos eso, la velocidad de captura bajó de categoría: pasó a ser
un **piso de adopción**, no un objetivo de rendimiento.

**JERÓ:** Explícame «piso de adopción», que suena a excusa.

**JUAN:** Es que si capturar en el celular es más incómodo que el papel, el supervisor no lo usa, y se
acabó el proyecto. Pero hacerlo tres segundos más rápido no le devuelve nada al negocio. Entonces
tiene que ser cómodo, no récord.

**JERÓ:** Y ahí hay un riesgo que a mí me preocupa más que cualquier decisión técnica.

**JUAN:** Dilo.

**JERÓ:** Que el costo lo paga el capturador y el beneficio lo cobra la oficina. El man que está en el
invernadero con guantes es el que tiene que sacar el celular; las cuatro horas que desaparecen son
de la oficina. Si al capturador no le devolvemos algo en la cama, la app se muere en el campo aunque
el negocio cierre.

**JUAN:** Estoy de acuerdo, y no está resuelto. Lo que tenemos es que la validación le evita rehacer
trabajo: si mete un dato imposible, se entera ahí mismo y no dos semanas después. Eso es lo poco que
hoy le devolvemos.

---

## `09:30` · De dónde sacamos todo esto

**JERÓ:** Hablemos del levantamiento, que es donde nos van a apretar. ¿Qué fuentes tenemos?

**JUAN:** Cinco. Una primera sesión grabada, del veintisiete de julio, donde él presenta la idea. Un
cuestionario escrito después de esa sesión, cuarenta y seis preguntas, cuarenta contestadas. Una
segunda sesión grabada el cuatro de agosto, que es donde están casi todas las cifras. Una
caracterización de **doscientas sesenta y dos preguntas de sí y no**, contestadas por él, por ti y por
mí. Y la planilla real de captura de la finca.

**JERÓ:** Y dos reuniones nuestras, que no son voz del cliente.

**JUAN:** Exacto, y están separadas a propósito en otro archivo. Lo que dijimos nosotros nunca se
mezcla con lo que dijo él.

**JERÓ:** ¿Cómo distinguimos una cita buena de una dudosa?

**JUAN:** Cada cita en el archivo de voz del cliente va marcada. Si es verificable en la grabación va
como cita; si encaja con él pero un lector estricto podría discutir quién la dijo, va como inferida;
y si es respuesta escrita, que es la evidencia más fuerte, va marcada como escrita. Esto es porque
las transcripciones de Teams **tienen un solo identificador de hablante para todo el archivo**.

**JERÓ:** O sea que la grabación no sabe quién habló.

**JUAN:** No lo sabe. Por eso el archivo lo dice de frente en la primera página.

**JERÓ:** Ahora la parte incómoda. ¿Cuál es la debilidad grande del levantamiento?

**JUAN:** Que todo descansa en **una sola persona**: el director de producción. Y que **planeación,
que es donde nace la proyección, nunca se exploró**, porque él cerró la puerta. Le preguntamos con
quién más podíamos hablar y dijo *«no, eso no»*.

**JERÓ:** Y él mismo calificó su contexto.

**JUAN:** En cinco sobre diez. Lo dijo él: *«de uno a diez es un cinco, y cinco está muy mal»*. Eso
también va en la charla, porque es honesto y porque explica por qué hay tantas preguntas abiertas.

**JERÓ:** Agrega que nos hemos equivocado leyéndolo.

**JUAN:** Dos veces. La más fea: él escribió *«no deja ingresar el último registro»* y nosotros lo
entendimos como que el sistema rechazaba el registro más nuevo. Lo que quería decir era **el más
viejo**. Estuvimos a punto de construir un desacuerdo que no existía. Desde ahí la regla es: las
notas literales se confirman, no se interpretan.

---

## `12:30` · Del levantamiento a los drivers

**JERÓ:** Y de todo eso, ¿qué salió en limpio?

**JUAN:** Un embudo. Primero, un mini QAW con **trece atributos de calidad**, priorizados por tres
actores: supervisor de campo, gerente de producción y administrador del sistema. **Confiabilidad
quedó en el puesto uno para los tres.** Es el único consenso total de todo el proyecto.

**JERÓ:** Que además es coherente con la frase de él.

**JUAN:** Es la misma frase, en otro idioma. Después vienen Disponibilidad, Rendimiento, Capacidad
para ser Auditado, Capacidad. Segundo: de las doscientas sesenta y dos preguntas se votó un **Top
sesenta y cinco**, y cada una de esas se convirtió en un **escenario de calidad**. Sesenta y cinco
escenarios, con los seis elementos del modelo de Bass, Clements y Kazman: entorno, fuente, estímulo,
artefacto, respuesta y medida de respuesta.

**JERÓ:** ¿Y el catálogo de requisitos?

**JUAN:** Veintiún funcionalidades significativas y treinta y ocho restricciones, entre negocio y
técnicas. La restricción rectora es que **todo tiene que funcionar sin conexión**, porque **en el
cultivo no hay señal**. Eso lo respondió él con una sola palabra: «NO».

**JERÓ:** Y la lista de contradicciones, que es lo que más me gusta enseñar.

**JUAN:** Setenta y una entradas, en cinco grupos: donde el cliente se contradice a sí mismo, donde
el cliente nos contradice a nosotros, donde nosotros nos contradecimos, las consecuencias del modelo
de negocio, y **las que nunca le preguntamos**. Cincuenta y tres están decididas. **Las dieciocho que
quedan son todas del último grupo y todas necesitan una sesión con él.**

**JERÓ:** Y treinta y seis decisiones de arquitectura escritas.

**JUAN:** En formato Nygard, sí. Contexto, decisión, consecuencias y observaciones. Y ahí hay una
trampa que hay que evitar en vivo: **existen dos libros de ADR con numeración distinta**. El que
manda es el Excel. En el otro, el `ADR-001` es otra cosa completamente.

**JERÓ:** Si en la exposición decimos «ADR-001» y pensamos en cosas distintas, quedamos mal los dos.

**JUAN:** Por eso está escrito como trampa número uno. ⚑ `X-12`

*(pausa)*

---

## `15:30` · La decisión grande: cómo se entrega

**JERÓ:** Vamos a la decisión que a un líder de arquitectura le va a interesar más. Nosotros
empezamos pensando en SaaS.

**JUAN:** Empezamos pensando SaaS multi-tenant, sí. Y lo descartamos entero.

**JERÓ:** Dame las razones en orden de peso, sin adornos.

**JUAN:** Primera y suficiente: **el cliente necesita operar sin internet**. Y ojo con el matiz,
porque esto es una lección de método: nosotros creíamos que él pedía servidor propio por
desconfianza. No era desconfianza. Era **continuidad**. En el cultivo no hay señal y la jornada no se
puede parar.

**JERÓ:** Segunda.

**JUAN:** Los números no cerraban. Él mismo dijo «ustedes ofrecerán diez dólares al mes por usuario».
En una finca con tres capturadores eso da del orden de doscientos treinta dólares al mes. Con eso no
se sostiene una plataforma.

**JERÓ:** Tercera.

**JUAN:** El umbral ese de «con veinte empresas somos rentables» nunca lo validó nadie. No hubo
estudio de mercado. Y operar una plataforma veinticuatro siete con dos personas no estaba costeado.

**JERÓ:** Y la cuarta me la sé: la objeción comercial la predijo él.

**JUAN:** Textual: *«las empresas de flores son muy celosas con su información; le van a decir: usted
cómo me controla que no le da mi información a otro cultivo»*. Esa objeción apunta justo al SaaS.

**JERÓ:** Ahora la pregunta fea, y te la va a hacer el arquitecto si lee la primera sesión: él también
dijo que **la idea era migrar a la nube**.

**JUAN:** Lo dijo. Y dijo por qué: que un servidor en la finca necesita un ingeniero de sistemas ahí.
Nuestra respuesta tiene dos partes. La primera es que en la misma sesión pidió que el software
funcionara fuera de línea, *«como los sistemas de rutas de los camiones del correo»*, esas fueron sus
palabras; y en la segunda sesión dijo *«sí o sí hay que seguir trabajando»*. La nube no le da eso.

**JERÓ:** ¿Y la segunda parte?

**JUAN:** Que él tenía razón en la consecuencia: local-first **sí necesita un ingeniero de sistemas en
la finca**. Y de hecho nuestro modelo lo asume: el administrador del sistema es un empleado del
cliente, el ingeniero de sistemas de la finca. Nosotros no tocamos datos de producción. Eso hay que
confirmarlo con él, no esconderlo. ⚑ `X-20`

**JERÓ:** Entonces, en una frase, ¿qué entregamos?

**JUAN:** Una instalación por empresa, en un nodo dentro de la finca, que opera sin internet sobre su
información activa. La nube presta respaldo, distribución de versiones y servicios, y **nunca está en
el camino crítico**. Comercialmente: unos veinte mil dólares por instalación, que es el presupuesto
que él puso, más una mensualidad de cien a doscientos dólares.

**JERÓ:** Esa mensualidad es nuestra y no está costeada.

**JUAN:** Es una cifra de partida, sin costear, y hay que decirlo así. Y queda un cabo suelto que hay
que nombrar antes de que lo pregunten: **qué pasa con una finca que deja de pagar**. Si se queda sin
actualizaciones, diverge de versión, que es justo lo que nos da miedo.

---

## `19:00` · Las alternativas que miramos

**JERÓ:** Cuatro alternativas, ¿cierto? Resúmelas rápido, porque esto sí lo va a preguntar.

**JUAN:** La primera, la elegida: **monolito modular en contenedor**, una instalación por empresa, con
un cliente móvil pesado que funciona offline. La segunda: microservicios con broker y orquestador. La
tercera: un backend gestionado, tipo Firebase o Supabase. La cuarta: código propio por finca.

**JERÓ:** ¿Por qué no microservicios?

**JUAN:** Porque somos dos personas **sin experiencia medible en el sector**, y eso está escrito como
restricción de negocio, no es modestia. Microservicios con dos personas es la forma más rápida
conocida de gastarse el presupuesto en infraestructura en vez de en el dominio.

**JERÓ:** ¿Y el backend gestionado? Porque ese nos compraba media arquitectura.

**JUAN:** Ese es el que hay que argumentar bien, porque es tentador. Nos regalaba sincronización,
identidad y base de datos. Se cae por tres cosas. La descalificatoria: **no corre en el nodo de la
finca**. El plano de datos viviría en la nube del proveedor, y con eso se acaba la operación sin
internet, que es la razón del proyecto.

**JERÓ:** Las otras dos.

**JUAN:** El precio por usuario activo se come la mensualidad justo cuando el negocio crece. Y su
modelo de sincronización no es el nuestro: la mayoría resuelve conflictos con «el último que escribe
gana» a secas, y nosotros necesitamos idempotencia, orden y bitácora consultable.

**JERÓ:** Una aclaración justa: la puerta no queda cerrada del todo.

**JUAN:** No. Para los servicios **en la nube** —respaldo, distribución de versiones— sí puede salir
más barato algo gestionado. Lo que no vuelve a la mesa es el plano de datos de la finca.

**JERÓ:** Y la cuarta, código por finca. Esa se parece peligrosamente a la nuestra.

**JUAN:** Se parece en dónde se instala y se diferencia en cuántos productos hay. Con código por
finca, la novena finca es el noveno proyecto: cada corrección se aplica nueve veces y parametrizar
deja de ser configuración para volverse desarrollo. Nosotros tenemos **un solo producto instalado N
veces**, y lo que cambia entre fincas son **datos** —catálogo, reglas, parámetros—, no código.

**JERÓ:** Esa es la frase del proyecto, en serio.

**JUAN:** Es la restricción más importante que salió de toda la depuración. Si agregar un tipo de
labor obligara a agregar una columna, obligaría a migrar el esquema **dentro de la casa de cada
cliente**. Eso no se sostiene.

*(pausa)*

---

## `22:00` · El viaje de una cama

**JERÓ:** Hagamos lo que más se entiende: sigamos una cama, de principio a fin. Arranca.

**JUAN:** Antes de salir al cultivo, el teléfono baja dos cosas: **el paquete de configuración de la
empresa** —catálogo, reglas y parámetros publicados juntos, una sola versión inmutable— y **la
asignación del día**: qué bloques y qué camas le tocan. Si falta algo, se avisa **antes de salir**,
nunca en medio del campo.

**JERÓ:** ¿Por qué un solo paquete y no tres versionados?

**JUAN:** Porque si son tres, reproducir una validación vieja exige acertar tres versiones, y si
aciertas mal **falla en silencio**. Con uno solo, citas un número y o es exacto o es imposible. Nunca
«casi».

**JERÓ:** Sigue. La persona entra a la app.

**JUAN:** Con identidad propia, aunque el teléfono sea compartido. Al sincronizar recibe una
credencial con sus permisos y una vigencia acotada; sin red se desbloquea con PIN o huella. Cierre de
sesión a los quince minutos de inactividad, que eso lo pidió él.

**JERÓ:** Ahí te paro. La credencial dura veinticuatro horas y al mismo tiempo el requisito dice que
hay que aguantar quince días sin sincronizar.

**JUAN:** Y tienes razón: así como está, un capturador que no sincronizó ayer no puede entrar hoy. En
el propio ADR quedó la observación de subirla a siete o diez días. Es una contradicción abierta, y el
número real depende de una pregunta que **nunca le hicimos** al cliente. ⚑ `X-13`

**JERÓ:** Bien. Captura.

**JUAN:** Se captura **la cama completa de una sentada**, aunque el dato aterrice en la sección,
porque una cama puede estar dividida en dos variedades. Y eso no es teoría: en la planilla real, tres
de dieciocho filas son camas divididas.

**JERÓ:** ¿Y cómo se guarda?

**JUAN:** Cada campo capturado es **un hecho**, una fila: producción, sección, campo, valor, cuándo
entró, con qué versión del paquete se validó y, si corrige a otra, a cuál corrige. Agregar un tipo de
anotación es agregar una fila al catálogo, no cambiar la estructura.

**JERÓ:** Eso se paga caro y hay que decirlo.

**JUAN:** Se paga: la base deja de validar tipos —los valida el motor de reglas—, una pantalla de
veinte campos es un giro sobre veinte filas, y las filas se multiplican. A cambio, dos personas
capturando a la vez no chocan, sincronizar es insertar, y ninguna pantalla se construye sobre esas
filas. Y falta decir una cosa: **ese ADR todavía está en estado de propuesta.** ⚑ `X-15`

**JERÓ:** Validación.

**JUAN:** En la cama y sin red. Las reglas viven en un archivo JSON, no en el código. «Nadie corta más
tallos de los que sembró» se rechaza ahí mismo, **con el motivo en lenguaje de negocio**, no con un
código de error. Si el supervisor no entiende qué hizo mal, no puede corregirlo: está en el
invernadero y no tiene a quién preguntarle.

**JERÓ:** ¿Y si le falta un dato obligatorio?

**JUAN:** Ahí decidimos que **no se frena la captura ni la sincronización**. La información viaja
incompleta y queda como trabajo para el gerente de producción, que es quien depura. El razonamiento es
que **perder información es peor que recibirla incompleta**, y que un obligatorio puede faltar por
razones legítimas: que esa cama no trabaja con líneas, que el número de lote no bajó al dispositivo.

**JERÓ:** Pero entonces, ¿qué frena y qué no? Porque un escenario nuestro dice literalmente que no se
puede guardar hasta corregir.

**JUAN:** Esa frontera no está escrita todavía y es la contradicción más gorda que tenemos. Hay que
separar dos cosas: **dato imposible** —más tallos que plantas— y **dato incompleto**. Lo medimos y el
efecto no es de borde: bajo esa decisión, el setenta por ciento de la batería de pruebas deja de
bloquearse en campo. ⚑ `X-10`

**JERÓ:** Ok. El dato ya está en el teléfono.

**JUAN:** Cada captura nace con un identificador único generado en el propio teléfono, sin red, y
entra a una bandeja de salida persistente. **No se borra hasta que el servidor confirme.** Sincronizar
es transporte, no una condición para poder capturar.

**JERÓ:** Y sincroniza contra la finca, no contra internet.

**JUAN:** Contra el servidor de la finca, por la red local de las oficinas. Apunta a diaria, apenas
haya red, y se puede forzar. El recordatorio se endurece con el tiempo: primero avisa, después
estorba, después impide trabajar con normalidad.

**JERÓ:** Dos supervisores anotaron la misma cama el mismo día. ¿Qué pasa?

**JUAN:** Entran las dos anotaciones y **gana la más reciente**, automático, sin que nadie medie, con
aviso a quien capturó.

**JERÓ:** ¿Más reciente según qué reloj? Porque el reloj del celular miente.

**JUAN:** Esa es la pregunta buena, y la respuesta la sacamos de una medición, no de una intuición.
**El teléfono con la hora adelantada ganaba el cien por ciento de los choques**, siempre y en
silencio, y lo que quedaba tenía cara de dato bueno. Entonces guardamos tres tiempos: la hora que
puso el teléfono, que no se toca nunca y sirve para auditar; una hora **corregida con el desfase
medido** de ese teléfono, que es la única que decide quién gana; y la hora del servidor. Si empatan,
desempata el identificador.

**JERÓ:** ¿Y si el reloj está manipulado?

**JUAN:** Se marca y se pide confirmación. **No se bloquea.** Un bloqueo sin salida en pleno cultivo
es una parada de jornada, y proteger la jornada es lo más importante del sistema. Ojo: hay dos
documentos nuestros que todavía dicen «bloquear» y hay que reescribirlos. ⚑ `X-03`

**JERÓ:** Proyección.

**JUAN:** Dos cosas separadas que antes confundíamos. El **cálculo vivo**, que se rehace con cada
sincronización y siempre muestra el estado actual. Y la **versión publicada**, que es una foto
congelada, con su corte de datos y su versión de parámetros, y que no vuelve a cambiar. Esa foto es la
vara contra la que se mide la desviación.

**JERÓ:** ¿Por qué no recalcular todo siempre?

**JUAN:** Porque si todo se recalcula, la desviación tiende a cero: estarías comparando contra una
proyección que ya absorbió el dato real. Y porque sobre una proyección emitida **ya se vendió**.
Cambiarle los parámetros por debajo es alterar el pasado.

**JERÓ:** Consulta.

**JUAN:** Nunca sobre los hechos crudos. Sobre tablas derivadas, con totales ya calculados por finca,
bloque, cama, periodo; y la pantalla se sirve en el orden en que la persona navega. Lo que nadie abre,
no se calcula.

**JERÓ:** Ahí tenemos tres mecanismos para el mismo problema, Juan: tablas derivadas, totales
precalculados y un caché. La auditoría del modelo decía que **puede que sobren dos**.

**JUAN:** Sí, y es de las pocas decisiones pendientes que pueden **quitar** trabajo en vez de
agregarlo. Hay que sentarse a decidir si es uno, dos o tres. ⚑ `X-14`

**JERÓ:** Corregir y cerrar.

**JUAN:** Mientras la producción está abierta se guarda cada modificación y se puede devolver al valor
anterior. **Al capturador no se le pide ni motivo escrito ni autorización**, porque corregir es
frecuente y no puede costarle nada; la autoría ya viaja sola. Para los cambios grandes el control no
es por rol, es **por magnitud**: corregir un número se aplica y queda en bitácora; erradicar cinco
bloques pide la confirmación de un segundo administrador, como aviso, no como bloqueo.

**JERÓ:** ¿Y quién define qué es «drástico»?

**JUAN:** Falta escribirlo, y va como dato del catálogo, no como código, porque cada empresa puede
tener un umbral distinto. Tampoco está decidido qué pasa si el segundo administrador no confirma.

**JERÓ:** Y el cierre de producción.

**JUAN:** Ese es el hueco más grande del proyecto y hay que decirlo así. **Es la pieza con más cosas
colgando** —retención, consulta del histórico, consolidación— y todavía no está definido quién lo
dispara ni qué consolida exactamente. ⚑ `X-17`

**JERÓ:** Última parada: sacar información de la finca.

**JUAN:** Dos caminos y ninguno es una interfaz pública. Exportar a Excel y PDF con las mismas
restricciones que rigen en pantalla. Y **lectura directa desde el Power BI del cliente**, con un
usuario de solo lectura contra las vistas publicadas, nunca contra los hechos crudos. Como el sistema
vive en la red de la finca y la herramienta del cliente también, no hay que exponer nada a internet.

**JERÓ:** Y el respaldo.

**JUAN:** Cifrado, con **la llave de cada empresa**. Y nosotros conservamos una copia de custodia
**fuera de línea**, en soporte físico, dos ejemplares en sitios separados, con doble control y
registro de cada acceso. Solo se usa ante una excepción declarada, se le avisa al administrador de la
empresa, y la existencia de esa copia va en el contrato.

**JERÓ:** Aquí hay que ser exactos, Juan, porque es fácil pasarse. Nosotros no podemos decirle al
cliente «es imposible que accedamos a su información».

**JUAN:** No podemos, y el ADR lo dice con esas palabras: es una **barrera física y de
procedimiento**, mucho más fuerte que una llave única nuestra, pero no es una imposibilidad técnica.
Y hay otra cosa que hay que confesar: **el cliente dijo que no** a los respaldos cifrados y a la llave
propia. Decidimos en contra de su respuesta.

**JERÓ:** ¿Con qué argumento?

**JUAN:** Con el de él mismo. Es lo único que responde la objeción comercial que él predijo. Pero no
se aplica en silencio: va en la lista de las siete decisiones que hay que llevarle a la sesión. ⚑ `X-19`

*(pausa)*

---

## `28:00` · Qué está probado

**JERÓ:** Tenemos algo construido. Cuéntalo sin venderlo de más.

**JUAN:** Hay un prototipo de captura, `PoC-0`: aplicación web que funciona con el celular sin red, sin
una sola dependencia de npm, **hecha para botarla**. Recorre una cama dividida, choca contra una regla
dura, sincroniza, provoca y resuelve un conflicto, y corre con la red apagada. Seis suites de prueba
en verde.

**JERÓ:** Y lo que sobrevive del prototipo no es el código.

**JUAN:** Es el modelo de datos, el catálogo de reglas y el contrato de sincronización. La interfaz se
bota.

**JERÓ:** Cuenta lo de la planilla, que eso convence.

**JUAN:** Le pasamos las reglas a la planilla real de la finca, con datos verdaderos. Salieron nueve
filas sospechosas. Entre ellas, la misma variedad escrita `Cortona` y `Cartona` —a una letra de
distancia— y dos variedades con tres densidades distintas el mismo día. **El dos por ciento de error
existe, y se ve.**

**JERÓ:** Ahora sé honesto con el prototipo.

**JUAN:** El prototipo **contradice cuatro decisiones vigentes**, de identidad y de tiempo, y hay que
arreglarlo antes de medir encima de él. No lo presentamos como «la arquitectura funcionando».

**JERÓ:** Y los spikes.

**JUAN:** Siete mediciones sobre los componentes que nos toca hacer a nosotros: el motor de reglas, el
ingreso y la sincronización, el cifrado y el respaldo, el paquete de configuración, el empaquetado y
la instalación, la salida al BI, y los dos lenguajes que nos faltaban. Y la conclusión que a un
arquitecto le va a gustar es una sola: **el rendimiento no es la restricción en ninguno**.

**JERÓ:** Dame un número que lo pruebe.

**JUAN:** Evaluar una cama contra todas las reglas cuesta décimas de microsegundo. Una jornada entera
de ingesta entra en cosa de un segundo. La extracción de un año para el BI usa sesenta y ocho segundos
de un presupuesto de seiscientos. Lo que decide **no es velocidad**: es quién puede hacer qué, y qué
pasa cuando dos cosas coinciden.

**JERÓ:** Y encontramos fallos que no dan error, que eso es lo valioso.

**JUAN:** Tres que hay que contar. El del reloj adelantado, que ya conté. Una regla con un tipo que el
motor no conoce **cierra la cama como si no existiera**, sin avisar. Y el peor: con la seguridad por
fila puesta, **una credencial de una empresa leyó doscientas treinta y nueve mil filas de otra
empresa**, porque la protección de las tablas no se hereda a las vistas materializadas. El arreglo
está probado a mano; la prueba automática que lo vigile en cada cambio **todavía no existe**.

**JERÓ:** ¿Y el lenguaje del backend? Porque eso lo van a preguntar.

**JUAN:** No está elegido, y ya no depende de medir más. Los cinco candidatos quedan dentro del cuatro
por ciento en casi todo. Lo que decide es **una pregunta que no es técnica**: si el motivo del rechazo
tiene que ser idéntico en el teléfono y en el servidor, o si basta con que lo sea el veredicto. Si
tiene que ser idéntico, el camino se estrecha a uno solo. Si un uno coma nueve por ciento de
divergencia es aceptable, se abren los demás.

**JERÓ:** Esa pregunta es tuya y mía. No es del cliente.

**JUAN:** Es nuestra, y es la que hay que contestar primero. ⚑ `X-11`

---

## `31:00` · Lo que no sabemos

**JERÓ:** Cierre. Dime los huecos, sin adornos, porque prefiero que salgan de nuestra boca.

**JUAN:** Tres del cliente y tres nuestros. Del cliente: uno, **el porcentaje de productividad
esperada por variedad y cómo se reparten los tallos en los siete días que dura el corte**. Sin eso la
proyección no arranca: es el motor del producto.

**JERÓ:** Y ahí hay una jugada que cambia la petición.

**JUAN:** Sí, y me parece lo más útil que hemos pensado en semanas. Si él ya hace la proyección a mano,
**ya tiene esos dos números en alguna parte**: en un Excel, en el sistema viejo, o en la cabeza del
agrónomo. Entonces dejamos de pedirle «danos el porcentaje», que suena a tarea y lleva meses sin
fecha, y le pedimos **«muéstranos la proyección del último ciclo, con el archivo con el que la
hiciste»**. Eso convierte una dependencia bloqueada en una sesión y un archivo.

**JERÓ:** Dos.

**JUAN:** El proceso de captura a detalle **nunca se trabajó con él**. Eso arrastra el catálogo de
reglas duras y hasta algo tan básico como **qué es «la jornada»**: si es el día natural, el turno o la
visita. Son respuestas distintas y cambian el modelo.

**JERÓ:** Tres.

**JUAN:** El sistema viejo. Unas trescientas tablas, de las cuales cuarenta y cinco son de producción,
y un sistema de productividades que **él no supo nombrar**. Yo creo, y subrayo creo, que es un Access.
**No está confirmado por él**, y esa palabra no aparece en ninguna transcripción; salió de mí. Si es
Access, leerlo es fácil y ahí adentro puede estar el número que bloquea la proyección.

**JERÓ:** Y los nuestros.

**JUAN:** El evento de cierre de producción, que ya dijimos. Si tres mecanismos de lectura son uno o
tres. Y la pregunta del motivo, que arrastra el lenguaje.

**JERÓ:** Hay una más, y creo que es la que más me pesa a mí.

**JUAN:** Dila.

**JERÓ:** Que todo descansa en una sola voz. No es un hueco de datos, es un riesgo del proyecto.
Planeación, que es donde nace la proyección, nunca la oímos.

**JUAN:** Y no es negligencia nuestra: él cerró la puerta. Pero el riesgo existe igual, y va escrito en
las restricciones de negocio junto con la otra, que también nos incomoda: que las decisiones de
arquitectura las estamos tomando dos ingenieros **sin experiencia medible en este sector**.

**JERÓ:** Eso lo dejamos escrito nosotros mismos.

**JUAN:** Lo dejamos escrito porque conviene tenerlo a la vista. Y porque cada vez que hemos decidido
sin entender el porqué del cliente, nos ha tocado devolvernos una ronda entera.

---

## `34:00` · Cierre

**JERÓ:** Cerremos con lo que le pedimos al líder de arquitectura, que para eso es la exposición.

**JUAN:** Tres cosas. Primera: que ataque **las tres decisiones más caras de revertir**, que son el
modelo de datos por hechos, la resolución automática de choques por la hora corregida, y el monolito
por instalación. Si esas tres están mal, lo demás no importa.

**JERÓ:** Segunda.

**JUAN:** Su criterio sobre **la pregunta del motivo**, porque con ella cae el lenguaje del backend y
estamos parados ahí.

**JERÓ:** Y tercera.

**JUAN:** Que nos diga si ve un **hueco de operación** que dos personas no estemos viendo. Actualizar
N instalaciones dentro de casa de N clientes, sin acceso directo, es lo que más miedo nos da y lo que
menos hemos vivido.

**JERÓ:** Cierro yo con el resumen, a ver si me sale sin papel. El problema es que el dato tarda una
semana en servir y llega con errores de transcripción, y por eso se vende sobre un número viejo. La
solución es **un solo producto instalado en cada finca**, que captura sin red, valida en la cama y
proyecta con cada sincronización, con lo que varía entre fincas como datos y no como código. Y
tenemos treinta y seis decisiones registradas, sesenta y cinco escenarios, un prototipo y siete
spikes medidos… y una lista clara de lo que no sabemos.

**JUAN:** Yo le agregaría la frase de él, que es con la que abrimos: *certeza de los datos, que se
ingresen los datos correctamente*. Todo lo que contamos en esta media hora es eso, desarrollado.

**JERÓ:** Cerramos ahí.

*(fin)*

---

## Notas de producción

- **Cuando se grabe, no leer las marcas `⚑ X-nn`**: son para el que estudia, no para el que escucha.
- **Los tiempos son estimados** con lectura normal. Si al grabar se pasan de treinta y cinco minutos,
  el bloque que se recorta es *«Las alternativas que miramos»* (`19:00`), no *«Lo que no sabemos»*.
- **Turnos que conviene repetir hasta que salgan solos**, porque son los que deciden si el arquitecto
  compra la idea: el costo del problema (`03:30`), por qué no un backend gestionado (`19:00`), el
  reloj y las tres horas (`22:00`) y los tres huecos del cliente (`31:00`).
- **Si Jerónimo no está de acuerdo con una réplica suya**, se cambia el texto y se anota la frase
  acordada en el tablero `X-nn` del banco de preguntas. **La discusión no se deja para el aire.**
- Sugerencia de uso: grabarlo tal cual una vez, escucharlo tres o cuatro veces, y después volver a
  grabarlo **sin leer**. La segunda grabación es la que mide si ya se sabe.
