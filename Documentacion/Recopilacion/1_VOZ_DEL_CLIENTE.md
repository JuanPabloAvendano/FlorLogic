# 1 · Voz del cliente — recopilación completa

> **Qué es.** Todo lo que el cliente ha dicho, en sus palabras, reunido en un solo archivo y ordenado
> por sesión y por tema. **Es la fuente principal de verificación del proyecto.** Cuando algo en
> `CONTEXTO.md`, en las decisiones `DEC-nn`, en los requisitos o en el modelo contradiga este
> archivo, manda este archivo.
>
> **Qué NO es.** No es un análisis, no es una lista de requisitos y no resuelve nada. No hay ni una
> interpretación del equipo en las secciones de cita: lo que el equipo piensa está en
> `2_VOZ_DEL_EQUIPO.md`, y los choques entre ambos están en `3_BRECHAS_Y_CONTRADICCIONES.md`.
>
> **Versión** 1.0 · 25-ago-2026 · construido leyendo las cuatro transcripciones completas, el
> cuestionario S1-Q diligenciado y las 262 respuestas de caracterización.

---

## 0 · Advertencias sobre las fuentes — leer antes de citar

**0.1 · Las transcripciones no distinguen quién habla.** Los cuatro `.vtt` de Teams tienen **un solo
identificador de hablante para todo el archivo** (se verificó: un único GUID por fichero). Teams
además destroza el vocabulario técnico —*desbotone*, *esqueje*, *botón color*, *pompón*— y mete
fragmentos en inglés que no dijo nadie.

En consecuencia, en este archivo:

- `CIT` = **cita textual verificable.** Está en el `.vtt`, en una celda del cuestionario o en una nota
  del Excel de caracterización, y por el contenido **solo puede haberla dicho el cliente** (habla del
  negocio, de su finca, de sus cifras).
- `INF` = **atribución inferida.** Está en el `.vtt` y encaja con el cliente, pero un lector estricto
  podría discutir quién la dijo. **No usar `INF` como respaldo de una decisión sin confirmarla.**
- `ESC` = **respuesta escrita del cliente**, sin ambigüedad de hablante: cuestionario S1-Q o columna
  «Respuesta Cliente» / «Nota» de la caracterización. **Es la evidencia más fuerte que hay.**

**0.2 · Todo descansa en una sola persona.** El informante es el **director de producción** de la
finca. Él mismo cerró la puerta a entrevistar a otras áreas. **Planeación —que es donde nace la
proyección— nunca se exploró.**

**0.3 · El cliente calificó su propio contexto en 5/10.** `CIT` *«de uno sobre 10 es un 5. 5 está muy
mal, está muy mal. Por eso hay que volverla a hacer y por eso saqué estas preguntas y posiblemente
toca volverlas a hacer hasta conseguir un 9 con algo.»* (S2). **Lo dijo él, no nosotros.**

### Inventario de fuentes de voz del cliente

| Fuente | Fecha | Naturaleza | Volumen |
|---|---|---|---|
| **S1** `.vtt` | 27-jul-2026 | Grabada, cliente + equipo | 451 intervenciones |
| **S1-Q** `2_ENTREVISTA_S1_Diligenciada_y_Vacios.xlsx` | tras S1 | **Escrita**, 46 preguntas | 40 con respuesta, 6 en blanco |
| **S2** `.vtt` | 4-ago-2026 | Grabada, cliente + equipo | 828 intervenciones |
| **Caracterización** `MINI QAW PLANTILLA NO TERMINADA.xlsx` | ~23-ago-2026 | **Escrita**, sí/no | **262/262 + 28 notas** |
| Formato real de captura | — | Documento físico suyo | 3 hojas con datos reales |

**S3 (11-ago) y S4 (17-ago) NO son voz del cliente:** son reuniones internas del equipo. Van en
`2_VOZ_DEL_EQUIPO.md`.

---

## 1 · Lo que el cliente dijo en S1 — 27-jul-2026, la propuesta

Esta sesión es el cliente **vendiendo la idea**. Casi todo lo que dice es sobre qué quiere y por qué,
no sobre cómo funciona su finca en detalle.

### 1.1 El problema y el objetivo

- `CIT` *«Más ágil el ingreso de los datos.»* — es lo primero que dice en toda la grabación.
- `CIT` *«cómo tomar los datos de tal manera que sean confiables y que sean de un ingreso de una sola
  vez en cualquier dispositivo, que les sirva para todos, que no tengan que hacer un formato aquí, un
  formulario primero en una hojita y es una persona [que] digita, que hay un riesgo de error y una
  demora más alta en esa cuestión.»*
- `CIT` *«que aprovechen los usuarios a saber qué flor van a vender […] yo tengo una proyección de
  una venta de 5000 tallos, según mis tendencias no van a ser 5000, sino 4000 tallos. Y según los
  4000 tallos van a ser 2000 de [una] variedad, 1000 de otra y 500 de otra.»*
- `CIT` *«mejorar una toma de decisiones casi que al final del proceso del ciclo.»*
- `ESC` **Motivo real del proyecto, escrito:** *«mejorar el motor de base de datos […] llevar los
  datos que tiene actualmente a una base robusta, y tener una aplicación enfocada a celulares y
  web.»*
- `ESC` (S1-Q P05) *«Ahora las fincas se encuentran más organizadas y sistematizadas; al estar más
  organizadas necesitan datos más certeros, porque la producción demora 3-4 meses.»*

### 1.2 Conectividad — el hecho que fija la arquitectura

- `CIT` *«obviamente están en vereda […] solamente está la conexión en la oficina, no más.»*
- `CIT` *«el cultivo de flores puede tener la equivalente a […] cancha de fútbol.»*
- `CIT` *«tener el software que trabaje offline o fuera de línea; en el momento que tenga internet se
  suba, como hacen los sistemas de rutas de los camiones del correo.»*
- `CIT` *«el programa que usted puede estar fuera de línea y tomar datos tranquilamente sin ningún
  problema. Y hay internet aquí, se suben los datos.»*
- `ESC` (S1-Q P29) A *«¿Hay señal de celular o wifi dentro del cultivo?»* respondió literalmente:
  **«NO».**

### 1.3 Errores de captura y validación — lo pidió él

- `CIT` *«¿Cómo uno se puede asegurar que un tomador de datos no puede tomar la información 2 veces?
  Eso es lo que ustedes tienen que hacer, porque el sistema tiene que permitir […] evitar esos
  errores.»*
- `CIT` *«en esta cama yo sembré la semana pasada X variedad, entonces ustedes ahí con los datos
  decir: no, qué pena, ustedes no me pueden tomar esta cama porque es imposible que una flor salga en
  una semana.»*
- `CIT` *«Si salió, o si pasó algo, por favor que lo firme alguien, algún auditor.»*
- `ESC` (S1-Q P33) *«El sistema normalmente no permite ingresar información errónea, pero si esta
  información errónea llega a otras etapas se debe poder realizar una **trazabilidad completa hasta
  la cama** de dónde salió esta información.»*

### 1.4 Facilidad de uso — su propia formulación

- `CIT` *«hay unos usuarios que no son muy [duchos] en esta parte de sistemas.»*
- `CIT` *«que sea fácil y que estén los campos necesarios, grandes.»*
- `CIT` *«Deben buscar un software […] de tal manera que sea fácil.»*
- `CIT` *«que no tengamos que llenar formatos ni nada de esas cosas.»*

### 1.5 Notificaciones y mapa de calor — idea suya, no del equipo

- `CIT` *«unas notificaciones bacanas ahí […] "qué pena, esta semana no se siembran 10 camas y apenas
  llevan 8 camas y faltan 2 días de trabajo".»*
- `CIT` *«ya terminaron el corte en alguna área, entonces decir: cama 100 bloque 5 está vacía, o
  hacer un mapa de las camas que están vacías.»*
- `CIT` *«dónde va el corte, con mapas […] cómo va el corte, un 50, 60%, con colores. Mapa de
  calor.»*

### 1.6 Roles y permisos

- `CIT` *«no es necesario [autenticación] muy pesada, pero sí con buenas restricciones.»*
- `CIT` *«Jerónimo es el supervisor de siembra: que tenga acceso nomás a cierto ingreso de datos y a
  ciertos reportes. Usted, digamos, que es el gerente o el de ventas, que tenga acceso a cuestiones
  más grandes, sobre todo de informes.»*

### 1.7 Power BI e IA — lo que realmente dijo

- `CIT` *«nosotros […] siempre ya utilizan Power BI. Power BI es interesante […] pero ya la
  inteligencia artificial no sé cómo está.»*
- `CIT` *«la idea es que ustedes le suban una capa más y le coloquen inteligencia artificial.»*

> **Ojo con esta última.** Es lo más cerca que existe de que el cliente pida IA, y habla de una
> **capa analítica sobre Power BI**, no de un asistente de captura en el celular. Ver `BR-IA` en
> `3_BRECHAS_Y_CONTRADICCIONES.md`.

### 1.8 Modelo de negocio, precio y celo por los datos

- `CIT` *«las empresas de flores son muy celosas en su información. Entonces una de las primeras
  cosas que le van a decir: "bueno, puede hacer un plan de membresía, pero usted cómo me va a
  controlar a saber que usted no le da la información mía a otro cualquiera de flores".»*
- `CIT` *«ustedes ofrecerán 10 USD por mes por usuario.»*
- `CIT` *«la idea es que ellos migren a la nube […] porque el servidor en cierto punto es bueno, pero
  también necesita un ingeniero de sistemas ahí, necesita una cantidad de cosas.»*
- `CIT` *«los cultivos de flores en cierto punto no tienen mucho dinero, pero requieren mucha
  información.»*
- `CIT` *«primero debe ser estándar […] Y se lo replicamos a todas las [fincas].»*
- `INF` *«a partir de 20 empresas […] obtenemos la rentabilidad.»*

### 1.9 Caídas del sistema

- `CIT` *«sabemos que el sistema se cae, pero acordate, Juan Pablo, que los bancos dicen "se cayó el
  sistema" y todo el mundo se escandaliza.»*
- `CIT` *«cuando está programado de 2 a 4 de la mañana […] ustedes deben hacer la programación en la
  madrugada.»*
- `CIT` *«si usted lo hace offline, casi que usted no va a tener ese problema […] usted puede seguir
  ingresando otros datos mientras está en mantenimiento.»*
- `ESC` (S1-Q P40) *«El sistema perdería todo su valor si en fechas importantes falla.»*

### 1.10 Alcance — lo que él mismo sacó

- `CIT` *«no nos metamos con personal, porque […] nos va a tocar en algún momento.»*
- `CIT` (sobre presupuestos con insumos y mano de obra) *«se vuelve muy grande para ejecutar ese
  [semestre].»*

---

## 2 · Lo que el cliente respondió por escrito en S1-Q — 46 preguntas

Fuente sin ambigüedad de hablante. **Resultado del propio análisis del archivo: 10 completas ·
21 parciales · 3 vagas · 2 mal interpretadas · 4 contradictorias · 6 sin responder.** Nota global
**5,48/10**.

### 2.1 Su rol, en sus palabras

`ESC` (P02) *«director de producción. Responsabilidades: verifica que la información recolectada
[sea] correcta · ejecuta presupuesto de producción · realiza el presupuesto de proyección y ventas ·
realiza el presupuesto de gastos de la empresa · toma decisiones con base en la producción.»*

> **Primera responsabilidad que se nombra a sí mismo: verificar que la información sea correcta.**
> Es la misma frase que cerraría la caracterización un mes después.

### 2.2 Estructura y escala

- `ESC` (P04) *«15 hectáreas […] finca o predio con 15 hectáreas que contiene bloques (que no son
  necesariamente las mismas áreas). Estos bloques están compuestos [de] naves, y las naves están
  compuestas por camas, y dentro de las camas están las variedades.»*
- `ESC` (P10) *«Normalmente hay una variedad por cama; esa variedad puede tener varios colores; se
  puede llegar a dar el caso de **mezclar dos variedades por cama**; en una misma cama pueden haber
  distintas subvariedades; **en una etapa de producción hay 9 variedades activas** constantemente.»*
- `ESC` (P03) *«9 variedades de flor con diferentes morfologías.»*

### 2.3 Ciclo, corrimiento y erradicación

- `ESC` (P11) *«El ciclo de una variedad del sembrado al corte es entre 3 a 5 meses dependiendo la
  variedad; dentro del ciclo hay una serie de variedades que pueden o no afectar la producción.»*
  → **corregido por él mismo en S2:** son **actividades**, no variedades.
- `ESC` (P12) *«La producción se puede adelantar por clima, ambiente, actividades humanas o
  químicamente.»* — sin ningún número.
- `ESC` (P13) *«si la cama tiene muchos problemas puede erradicar la plantación […] la decisión la
  toma el ingeniero de producción o [el] gerente de ventas.»*
- `ESC` (P14) *«Durante temporada se busca que el presupuesto de ventas y producción permita realizar
  una **siembra escalonada** para abastecer estas fechas.»*

### 2.4 Proyección — el bloque peor resuelto (2,82/5)

- `ESC` (P15) *«Al sembrar el producto tengo un estimado de cuánto se va a producir dentro del ciclo
  de esa variedad […] voy **físicamente** a ver cómo va su desarrollo.»*
- `ESC` (P18) *«la persona encargada de la proyección realiza una revisión presencial de la cama y
  estima **restando porcentajes** si la producción va bajando, y además agrega o quita días a la
  fecha de corte.»*
- `ESC` (P20) *«Siempre se proyecta en **días**; se pueden utilizar semanas, pero al final el
  principal son días.»*
- `ESC` (P21) *«**1 año** de anticipación […] y en ocasiones hasta 1 año y medio dependiendo de la
  variedad.»*
- `ESC` (P22) *«La desviación de presupuesto de ventas vs la proyección normalmente está **6% debajo
  del presupuesto**.»*
- `ESC` (P24) *«si de X variedad requiero que proyecte el 90%, pero la realidad es 80%, […]
  planeación dice "estamos produciendo menos de lo que estamos proyectando o más de lo que estamos
  proyectando", siendo **a mano**.»*
- `ESC` (P23) *«Cuando se incumple la proyección el gerente de ventas debe suplir la necesidad del
  producto, comprando a terceros, o cancelando ventas.»*
- `ESC` (P25) sobre cruzar con pedidos: *«el usuario sugiere que **se sale del dominio del sistema**
  que se busca.»*

### 2.5 Grados y calidades

`ESC` (P19) *«La flor se clasifica en grados o calidades. GRADOS: el grado es una forma de indicar un
conjunto de CALIDADES de un producto. CALIDADES: la longitud del tallo, tamaños de la flor, número de
flores por tallo y grosor del tallo, y follaje. **Cada finca define sus calidades y grados.**»*

### 2.6 El dolor cuantificado — sus propios números

- `ESC` (P28) *«el supervisor llena a lápiz y papel […] se tarda **1 hora al día** tomando
  información; el practicante de sistemas digitando esta información puede tardar **4 horas 1 vez a
  la semana**.»*
- `ESC` (P26) *«Se registra 1 cosa: la siembra. Lo ideal es registrar más actividades; está
  digitalizado pero no lo hace la finca; **es necesario un sistema amigable y rápido de ingresar**.»*
- `ESC` (P27) *«Un digitador practicante de sistemas […] lo puede hacer cualquier persona.»*
- `ESC` (P34) *«**La revisión de la siembra es lo que más tiempo consume**, realizar la captura de
  datos, el foco del proyecto.»* → **el foco del proyecto declarado por el propio cliente.**
- `ESC` (P32) *«Se sacan reportes de siembra, de producción, de plagas y enfermedades, de inventarios
  de material vegetal, de pérdida de flor, de estimados de flor […] generalmente se realizan de
  manera semanal, estos reportes son digitales a través de PowerBI.»* — **única respuesta con nota
  5/5 de toda la entrevista.**

### 2.7 Las cuatro respuestas que el propio análisis marcó como contradictorias

| # | Pregunta | Lo que respondió | Choca con |
|---|---|---|---|
| P29 | ¿Hay señal en el cultivo? | **«NO»** | P30 |
| P30 | ¿Qué registran de plagas? | *«es un formato que se encuentra en un celular, y va directamente en un sistema»* | P29 |
| P35 | ¿Qué se hace por fuera del sistema? | **«Todo está sistematizado.»** | P28 (papel y lápiz, 4 h de digitación) y P31 (clima manual) |
| P38 | ¿Quién NO debería ver cierta información? | *«Toda la información puede ser observada por todos siempre y cuando sea bajo el contexto de producción.»* | P08 (*«administrativa y/o gerencial con privilegios»*) |

### 2.8 Lo que quedó en blanco

**Seis preguntas sin responder, y las cuatro últimas son el bloque de cierre completo:**
P09 (dibujo de la estructura) · P39 (qué NO debe cambiar el sistema) · **P42** (validar el
entendimiento) · **P43** (con quién más hablar) · **P44** (documentos de muestra) ·
**P45** (agendar segunda sesión).

---

## 3 · Lo que el cliente dijo en S2 — 4-ago-2026

La sesión más productiva. Aquí están casi todas las cifras del proyecto.

### 3.1 Escala física

- `CIT` *«Hay 25 bloques.»* · `CIT` *«camas hay […] 1525.»*
- `CIT` *«En el campo aproximadamente 156 personas […] y más o menos 10 administradores, 11
  administradores.»* · `CIT` *«[en poscosecha] creo que son 57 personas […] y cuatro
  administradores.»*
- `CIT` *«las camas tienen un largo […] y un ancho. Multiplicando esas dos dimensiones en metros nos
  arroja un área […] en metros cuadrados.»*
- `CIT` *«los ingenieros dicen cuál es la densidad de siembra […] número de plantas o unidades por
  metro cuadrado […] "para yo sacar una buena flor, esta variedad la voy a sembrar a 90 plantas [por]
  metro cuadrado". Otra puede decir sembrar a 60. **Es de acuerdo al ingeniero de producción.**»*
- `CIT` *«**el de planeación debe respetar la densidad de siembra.**»*

### 3.2 La fórmula del motor — la única que verbalizó completa

- `CIT` *«tenemos una cama de 1000 plantas […] y al término de 3 a 5 meses, de acuerdo a las
  productividades de esa variedad […] se puede sacar 900.»*
- `CIT` *«quiere decir que depende de la variedad […] con base en la siembra voy a recuperar en
  producción el 90%, o sea 1000 por [0,]9. Otra puede ser el 80%. **Nunca puede ser mayor a 100.**
  Uno lo que siembra luego no puede salir más.»*
- `CIT` *«**yo me puedo exceder hasta el 100%; más del 100% hay un dato malo.** O sea, yo puedo sacar
  de 800 hasta 990 [de] las 1000 plantas. Si ya [saca] 1020, ahí pasó algo que no funcionó.»*
- `CIT` *«porque quiere decir que ahí hubo una mala siembra y [se] sembraron más de 1000 plantas.»*

### 3.3 La banda del ±10% — explicada por él con números

- `CIT` *«tener las proyecciones de ventas que se acerquen del 90% al 110%.»*
- `CIT` *«el sistema me dijo "usted va a tener 900 unidades para la semana 25" […] ¿cuál es el 90% de
  900? Son 810 unidades. Este rango es entre 810 unidades y 990 unidades […] 990 es el 110 de 900.»*
- `CIT` *«no es como que van a haber 1000 y van a salir 1000, porque no es solamente que vayan a
  dañarse, sino que también pueden salir muy bien.»*
- `CIT` (corrección importante) *«el 10 o el 110 es […] de acuerdo a la proyección, que es
  diferente.»* → **la banda se aplica sobre la proyección, no sobre la siembra.**

### 3.4 Corrimiento del ciclo y duración del corte

- `CIT` *«en el peor de los casos se puede adelantar […] hasta 15 días.»*
- `INF` *«15 dividido […] 90 [es] el 6% de margen de error.»*
- `CIT` *«el ciclo […] es siembra, producción, de tres a cinco meses dependiendo de la variedad.
  Vuelve el ciclo a cero cuando se erradica.»*
- `CIT` *«[el corte] dura alrededor de **7 días**.»* — respuesta a *«cuando se empieza a cortar,
  ¿cuántos días dura?»*
- `CIT` *«normalmente es 3 semanas después del desbotone.»* · `CIT` *«[botón color] más o menos
  **12 días**.»*
- `CIT` (corrigiéndose a sí mismo) *«no, no, para nada […] dentro del ciclo hay una serie de
  variedades que pueden o no afectar la producción: **no variedades, sino actividades**. […] Por
  ejemplo […] un corte de luces, retirada de luces […] profundidad de la siembra.»*

### 3.5 Erradicación y bajas

- `CIT` *«esa erradicación puede ocurrir **semanal**. Hay unas más graves que otras, pero
  semanalmente se puede presentar un problema.»*
- `CIT` *«Tiene uno que digitar esa información […] para que el sistema **reste** de esas
  [unidades].»*
- `CIT` *«con lo vendido es o cancelar o comprar la flor […] para cumplir esa orden.»*
- `CIT` sobre la baja de producción: *«se hace por formato, y se debe ingresar al sistema. […] **La
  idea es que sea de un día para otro**, [pero] hoy en día […] pasa cada semana.»*
- `CIT` *«el ciclo vuelve a cero […] eso depende de la necesidad»* (cuánto tarda en resembrarse).

### 3.6 Frecuencia de proyección y consumo por rol

- `CIT` *«al gerente le interesa cuánta plata va a ingresar, cuánta plata va a gastar. **Al gerente de
  ventas le va a interesar el semanal. Y al gerente de producción […] es diario.**»*
- `CIT` *«La proyección, la idea es que se ajuste **semanal**.»* · `CIT` *«¿Se ajusta actualmente?
  No, actualmente se hace **mensual**, mensual o cada que sea necesario.»*

### 3.7 El 6% y el 2% — los dos números de exactitud

- `CIT` *«este 6%, la desviación de presupuesto de ventas versus la proyección, normalmente está 6%
  debajo del presupuesto.»* Y al preguntársele si compara presupuesto contra proyectado o contra
  cortado: `CIT` *«no, lo presupuestado contra lo que efectivamente […] **se cortó**.»*
- `CIT` *«[la información que se captura y digita] al final para la proyección tiene un error del
  2%.»*
- `CIT` *«¿Esta información está visualizada en algún lado? **No.** […] La idea es que en la
  proyección ya esté al 100%.»*
- `CIT` *«hay una tercera persona que lo revisa y ese 2% **lo tiene que dar el 0%** […] lo tiene que
  revisar un auditor. **El auditor normalmente es el ingeniero del sistema.**»*
- `CIT` *«ocurre cuando se digitaliza y también cuando […] la persona que está tomando los datos
  [captura] la primera vez.»* → **el error existe en los dos pasos, no solo al digitar.**

### 3.8 Compras a terceros

- `CIT` *«tal vez un 8% […] del total de las ventas»* se tuvo que conseguir por terceros.
- `CIT` *«ahí también están las órdenes que se tuvieron que cancelar.»*
- `CIT` *«[la compra a terceros ocurre] mensualmente.»*

### 3.9 Quién captura y quién consulta

- `CIT` *«de ingreso de información serían **3 personas**: el supervisor y otros [dos] auxiliares.»*
- `CIT` *«alrededor de 12 personas para solo el ingreso de información»* → **cifra que él mismo
  corrigió a 3 en la frase siguiente.**
- `CIT` *«[las que consultan] son las vendedoras. Alrededor de 20 personas.»*
- `CIT` *«El puesto de la persona que digita, [el] ingeniero en sistemas, **es cambiante, no es un
  puesto fijo: es otra falla**. Porque le tienes que explicar […] al principio se demora mucho, se
  equivocan más de lo normal.»*

### 3.10 Corrección de datos

`CIT` *«No la puede corregir [cualquiera]: el ingeniero de sistemas puede corregir, o el área de
sistemas, **aprobado por el área de producción, por [el] gerente de producción, por el
ingeniero**.»*

### 3.11 La app de plagas — el precedente offline que ya funciona

- `CIT` *«es totalmente celular local […] y luego se sube a la nube.»*
- `CIT` *«se llama formato [de] monitoreo de plagas y enfermedades.»*
- `CIT` *«¿Es un aplicativo con [otra] empresa por detrás? No, no, no. **La empresa propia**.»*
- `CIT` *«se registra la pluviometría […] **pero no se hace nada con esos datos** […] no en la
  producción.»*
- `CIT` *«[hay estación meteorológica] pero **no está en uso**.»*

### 3.12 Disponibilidad — la cifra dura

- `CIT` *«**hay que seguir trabajando, o sea, sí o sí hay que seguir trabajando**; puede parar y lo
  hacen en papel, o se hace localmente y después se sube la información.»*
- `CIT` *«no es como que se me cae dos segundos y ya lo tengo que tener ya, ya, ya. No, no, no. Sino
  […] **dura cuatro horas: no, cuatro horas [es] mucho, mucho. Entonces 1 hora.**»*
- `CIT` *«[en pico] aumenta proporcionalmente: **60% en tallos, […] 30% [a] 40% en personal y más o
  menos 60% en registro**.»*

### 3.13 Permisos y visibilidad

- `CIT` *«El costo de producción [sí lo saben]; el precio de la venta normalmente no lo saben, pero
  sí saben [el costo de] producción en algún momento.»*
- `CIT` *«**Hay información muy compartida en todo el sistema. Sí, no hay restricción.**»*
- `CIT` *«para supervisores y auxiliares sí hay restricciones, sobre todo en el precio de la venta de
  la flor.»*

### 3.14 Resistencia al cambio

- `CIT` *«si no se explica bien, sí sería un problema, porque de todas formas hay […] una reacción al
  cambio al principio.»*
- `CIT` *«**que sea fácil** […] que a veces las aplicaciones ponemos tanta arandela que es muy
  complicado.»*

### 3.15 Alcance — lo que él sacó, con sus palabras

- `CIT` (sobre cruzar con pedidos y clientes) *«**solamente va a mostrar qué flor se va a producir.**
  Es la primera [opción]. […] Porque se alarga mucho.»*
- `CIT` (sobre Florverde) *«**no, pero nosotros no nos podemos meter en eso. No nos metamos en
  Florverde**, porque […] es otra cuestión adicional.»*
- `CIT` (sobre entrevistar a más gente) *«me pregunta por más personas a las que se puedan
  entrevistar […] **pero no, eso no.**»*
- `CIT` (sobre las ~300 variedades) *«ahí en estos momentos, más o menos **300 variedades, 300
  subvariedades**, sumando […] entonces no es algo […] por ahora interesante.»*
- `CIT` (sobre Power BI) *«**No sirve, pero se puede mejorar**»* / *«se puede mejorar y cambiar y
  funciona, pero se puede mejorar […] cambiando el enfoque […] es un enfoque diferente.»*
- `CIT` (sobre temporada alta) *«es mantener información sin necesidad de advertir. ¿Por qué? Porque
  es muy variable. […] pero **si el sistema es suficientemente inteligente, sí puede hacer unas
  advertencias** […] puede generar como sugeridos.»*
- `CIT` (sobre actividades) *«Las actividades pueden estar digitalizadas, pero no lo hace la gente
  […] **uno puede digitalizar lo que quiera** dentro de sus actividades.»*

### 3.16 Los roles — cerrados por él en esta sesión

- `CIT` *«los supervisores van a tener no más permisos para ingresar datos y ver reportes sencillos
  […] de siembra, de números, de lo que ellos ingresan.»* · *«[son] supervisores y auxiliares.
  Solamente ellos dos.»*
- `CIT` *«toda la parte de supervisor de ventas, supervisor de producción, todas las otras, o
  gerencia, todo eso se puede dejar como […] **administrador**.»* · *«en los administradores
  entrarían también gerencia. Sí, claro, toda esa gente […] la gente que toma decisiones.»*
- `CIT` *«y el ingeniero de sistemas, que es el **superusuario** […] que puede otorgar permisos.»*
- Cierre: `CIT` *«quedamos con estos 3 roles […] Así es. Y no necesitamos nada más.»*

### 3.17 El ranking de atributos que él mismo dio en S2

Ejercicio hecho en vivo. Su orden, en sus palabras:

| Puesto | Atributo | Lo que dijo |
|---|---|---|
| **1** | **Confiabilidad** | *«tienes que ser 100% confiabilidad primero»* |
| 2 | Experiencia de Usuario | *«que sea fácil de utilizar, muy fácil de interpretar […] prioridad máxima»* (la puso 1ª hasta que corrigió) |
| 3 | Disponibilidad | *«colóquele tres»* |
| 4 | Seguridad | *«puede ser un tres o un cuatro… cuatro»* |
| 5 | Rendimiento | *«me puedo demorar un poquito, pero no mucho»* |
| 6 | Cap. para ser Soportado | *«capacidad para ser soportado 6»* |
| 7 | Cap. para ser Administrado | *«administrado 7»* |
| 8 | Trazabilidad | primero dijo *«sería un dos o tres»*, luego *«ya lo puede dejar en ocho, no es tan importante»* |
| 9 | Capacidad | *«por ahí un 9»* |
| 10 | Accesibilidad | *«puede ser un 10»* |
| 13 | Interoperatividad | *«puede ser 3»* → luego lo movió |
| 14 | Seguridad de funcionamiento | *«eso lo puede dejar en 14»* |

`[!]` **Sobre trazabilidad, la definición que él aceptó fue estrecha:** *«la trazabilidad me permite
a mí como administrador poder identificar cambios: quién hizo este cambio, quién hizo tal cosa.»* Y
la bajó al 8 tras aclarar *«no es necesariamente un histórico sobre los mismos datos, sino
directamente un histórico sobre las modificaciones.»*

`[!]` **Sobre accesibilidad**, se le explicó como *«la capacidad que tiene el sistema de poder ser
interpretado por personas con […] falta de digitalización, analfabetismo, […] algunos ciegos»* y
respondió *«puede ser un 10».*

---

## 4 · Lo que el cliente respondió en la caracterización — 262/262

Fuente escrita, sin ambigüedad de hablante. **Es la evidencia más fuerte y la más reciente del
proyecto.** Regla del instrumento: las preguntas van escalonadas de menor a mayor exigencia dentro de
cada bloque, y **el punto donde el cliente pasa de SÍ a NO es la medida de respuesta**.

### 4.1 Confiabilidad — 57 preguntas

**Dijo SÍ a:** verificar el dato individualmente · verificar por variedad, por cama, por bloque y por
finca/jornada · rechazar lo que esté fuera de rango · mostrar siempre el motivo del rechazo · cambiar
reglas y rangos sin nueva versión de la app · que las reglas funcionen igual sin conexión · avisar de
doble captura de la misma cama el mismo día · avisar cuando una cama lleva demasiados días sin
capturarse · bloquear si el reloj del dispositivo fue alterado · plantilla parecida al papel actual ·
corregir antes y después de sincronizar · guardar captura incompleta como pendiente · saber con qué
datos y parámetros se calculó cada proyección · mostrar la desviación frente a lo cortado · **seguir
llenando el papel en paralelo los primeros meses** · comparar app contra papel del mismo día · que no
se pierda ningún dato bajo ninguna circunstancia · conservar ante batería agotada, cierre inesperado
y dispositivo mojado · retomar sincronización interrumpida · respaldos automáticos y diarios · que la
información nunca se elimine, ni al erradicar · avisar cuando el almacenamiento esté por llenarse.

**Dijo NO a:** verificar agrupando por **sección de cama** · confirmación final antes de guardar ·
impedir cerrar con campos vacíos · captura guiada pantalla por pantalla · que la estructura cambie
según la variedad · proponer el valor de la última vez · seguir el recorrido físico del bloque ·
mostrar cuántas camas faltan · conservar visible el valor original · exigir motivo en cada corrección
· fecha de cierre tras la cual no se pueda corregir · resolver conflictos por «el más reciente» ·
dejar ambas capturas para que decida una persona · indicador de % de datos con error · meta de error
· distinguir lo verificado de lo no verificado · excluir lo no verificado de las proyecciones ·
avisar si una proyección se calculó incompleta · impedir publicar con bloques faltantes · devolver la
información a una fecha anterior · prueba periódica de los respaldos.

**Reversiones donde el cliente contradijo al equipo:** verificar dato por dato (equipo NO, cliente
SÍ) · **advertir sin bloquear al alejarse del histórico** (equipo NO, cliente SÍ) · avisar a quien
capturó cuando su dato fue descartado (equipo NO, cliente SÍ) · proyección recalculada que dé el
mismo resultado (equipo NO, cliente SÍ) · estructura de captura igual para todos los bloques (equipo
NO, cliente SÍ).

**Sus notas literales en este bloque:**

> *«NO TAN IMPORTANTE»* (verificar por bloque) · *«HAY CAMPOS QUE NO SON CONCORDANTES CON OTROS»*
> (impedir cerrar con campos vacíos) · *«PUEDEN HABER DATOS EN BLANCO»* (estructura por variedad) ·
> *«DEPENDE DEL TIPO DE INGRESO DE DATO»* (impedir labor sobre cama erradicada) · **«NO DEJA INGRESAR
> EL ULTIMO REGISTRO»** (conflictos) · *«SI NO SE MODIFICA SI»* (proyección reproducible) · *«SOBRE
> LA MISMA PROYECCION»* (desviación) · **«NO PUEDE HABER ERRORES»** (meta de error) · **«SOLO LA
> CORREGIDA»** (conservar original y corregido) · *«TODO DEBE ESTAR INGRESADO»* (publicar con bloques
> faltantes).

### 4.2 Disponibilidad — 20 preguntas

**SÍ a los cinco escalones de offline:** sin conexión en el cultivo · **una jornada completa** ·
**tres días** · **una semana** · **más de quince días**. El bloque se agotó en SÍ: no hay punto de
quiebre y la ventana real puede ser mayor.

También **SÍ a:** consultar desde la web aunque la app esté fuera de servicio · que la captura siga
con la nube caída · horario intocable · **24 horas** · **7 días** · mayor exigencia en temporada alta
· resolver una interrupción en **menos de una hora** y dentro del mismo día · avisar mantenimientos ·
recuperar la información de un dispositivo dañado o perdido · que una falla de una finca no afecte a
las demás · avisar cuando un dispositivo lleva mucho sin sincronizar.

**NO a:** consultar información ya sincronizada estando sin conexión · **mantenimiento sin sacar el
sistema de servicio** · **continuar la captura en otro dispositivo cuando el que se usaba deja de
funcionar**.

### 4.3 Rendimiento — 21 preguntas

**NO a los cinco escalones de velocidad de captura:** menos que en papel · menos de un minuto · menos
de treinta segundos · un bloque completo por jornada (nota *«SOBRA»*) · la finca completa por jornada
(nota *«SOBRA»*). **NO** a buscar una cama de inmediato (nota **«LIGERO, NO DE INMEDIATO»**). **NO**
a sincronizar una jornada en menos de cinco minutos, a reportes de un mes en menos de diez segundos,
a reportes de varios años sin espera, a fluidez en gama baja, a que no se agote la batería, y a
recalcular la proyección una vez al día.

**SÍ a:** cada dato guardado de inmediato · paso de pantalla inmediato aun sin conexión ·
sincronización en segundo plano (nota *«SERIA EL IDEAL»*) · muchos dispositivos sincronizando a la
vez · **reflejo inmediato en la consulta web** (revierte al equipo) · recálculo de la proyección al
llegar información nueva · mismo rendimiento en temporada alta.

### 4.4 Capacidad para ser Auditado — 32 preguntas

**SÍ a:** saber quién capturó · desde qué dispositivo · historia completa de cama, de lote y de
variedad · llegar desde una cifra hasta los datos que la componen · registro inalterable · **conservar
la trazabilidad de manera indefinida** (nota **«DE POR VIDA»**) · registrar el motivo cuando
disminuyen plantas o tallos · **demostrar ante auditoría externa el origen de cualquier cifra** ·
**responder auditorías de certificación (Florverde, Rainforest Alliance, GLOBALG.A.P.)** · conservar
la evidencia que exige la autoridad fitosanitaria · generar el informe de auditoría desde el sistema
sin ayuda técnica · **demostrar que la información no fue alterada tras cerrar el periodo** ·
demostrar el cumplimiento de un compromiso con un tercero.

**NO a:** **la fecha y hora exactas de captura** · distinguir hora de captura de hora de
sincronización · lugar físico · **conservar versiones anteriores de un dato modificado** · **saber
quién hizo cada modificación y por qué** · registrar quién consultó o exportó · **exportar el registro
de auditoría** · cinco años como mínimo · entregar al comprador la historia del lote · que un auditor
externo consulte directamente · constancia de quién vio información protegida · que exista un momento
de eliminación obligatoria · tratamiento distinto para datos personales de quien captura · registrar
quién aprobó una proyección · que las revisiones de calidad queden como evidencia.

### 4.5 Capacidad — 15 preguntas

**SÍ a:** una finca completa · varias fincas · varias empresas · último año (nota *«O DE OTROS
AÑOS»*) · cinco años · **todo desde el primer día sin límite** · **que lo antiguo esté igual de
rápido** · **y también SÍ a que lo antiguo pueda tardar más** · varios días sin sincronizar sin
quedarse sin espacio · catálogo completo descargado antes de salir · crecimiento año tras año · costo
de almacenamiento acotado.

**NO a:** **almacenar fotografías** · **almacenar documentos escaneados** · conservar a la vez la
copia sin modificar y la corregida.

### 4.6 Capacidad para ser Administrado — 16 preguntas

**SÍ a:** crear y dar de baja usuarios sin pedirlo al desarrollador · cambiar permisos de inmediato ·
retirar acceso el mismo día · crear bloques, camas y secciones · crear variedades · cambiar grados ·
cambiar densidad y parámetros de cálculo · que un cambio de parámetros no altere proyecciones ya
emitidas · forzar sincronización remota · actualizar la app en todos los dispositivos · administrar
desde un computador de la finca · que quien administra no pueda modificar producción.

**NO a:** ver en una pantalla qué dispositivos tienen pendientes · **ver el avance de captura del día
por bloque** · que una actualización nunca obligue a suspender la captura · registrar por adelantado
el calendario de temporadas (nota *«NO SE ENTIENDE»*).

### 4.7 Experiencia de Usuario — 18 preguntas

**SÍ a:** pantalla legible bajo sol directo · **que la mayoría de los datos se escojan de una lista y
no se escriban** (revierte al equipo) · minimizar toques · deshacer el último dato · indicar qué se
sincronizó y qué no · **que los nombres en pantalla sean los que se usan hablando en la finca** ·
identificar la cama escaneando una marca física · mensajes de error que expliquen qué hacer (nota
*«OPCIONAL»*).

**NO a:** una sola mano · guantes puestos · dictar por voz · ver el avance de la jornada · **usar la
app sin capacitación formal previa** · **primera cama bien capturada en menos de diez minutos de
acompañamiento** · sugerencias mientras se captura · consultar desde el celular · armar cada quien su
propia vista · **vista gráfica del bloque en vez de lista**.

### 4.8 Seguridad — 19 preguntas

**SÍ a:** identificación propia no compartida · entrar sin conexión · cierre de sesión por inactividad
· **aislamiento absoluto entre empresas** · que el operador de la plataforma no pueda leer producción
· restringir quién exporta · **borrar de manera remota un dispositivo perdido** (nota *«OPCIONAL»*).

**NO a:** **que la información del dispositivo quede ilegible si se pierde** · demostrar
documentalmente el aislamiento · registrar todo acceso técnico · **cifrado en tránsito** · **respaldos
cifrados** · **llave distinta por empresa** · registro de cada exportación · marcar los archivos
exportados · caducidad para personal temporal · avisar intentos repetidos de acceso · información que
no salga del país.
**Y NO aceptó** que dentro de una misma empresa toda la información sea visible para todos.

### 4.9 Interoperatividad — 13 preguntas

**SÍ a:** exportar a Excel · exportar a PDF · conservar el formato de los archivos actuales · cargar
la información inicial desde los archivos existentes · entregar información al sistema administrativo
· **tomar información del sistema administrativo** · **convivir con la app de plagas sin
reemplazarla** · **que la información se pueda leer desde una herramienta de análisis externa** (nota
literal **«POWER BI»**) · conexión automática con otros programas sin intervención manual · **aceptar
que en la primera entrega la única forma de intercambio sea la exportación manual a Excel y PDF**.

**NO a:** consumir los datos de la app de plagas · entregar a nómina o productividades (nota
**«PRODUCTIVIDADES SI»**) · entregar información a un cliente o comercializador externo.

### 4.10 Escalabilidad — 11 preguntas, **11 SÍ de 11**

Único bloque sin un solo NO: agregar bloques y camas en plena temporada · duplicar capturadores ·
agregar finca nueva · agregar empresa nueva sin afectar a las existentes · soportar temporada alta ·
soportar picos simultáneos de varias empresas · **que el costo no crezca proporcionalmente por finca**
· **que un cambio estructural se aplique a todas sin intervención manual** · **años de historia sin
consultas lentas** · **más de treinta personas subiendo información el mismo día** · **agregar un tipo
de labor nuevo sin rehacer la captura**.

### 4.11 Capacidad para ser Soportado — 13 preguntas

**SÍ a:** alguien dentro de la finca que resuelva el día a día · resolver el mismo día · **resolver en
menos de una hora** · diagnóstico remoto · revisar el estado de un dispositivo en remoto · soporte en
horario extendido en temporada alta · que la persona siga trabajando mientras se resuelve · que la
puesta en marcha no detenga la operación más de una semana.

**NO a:** reportar el problema desde la propia app · que el reporte incluya el contexto automático ·
**manual de uso escrito** · **videos cortos dentro de la app** · **llevar registro de los problemas y
de cómo se resolvieron**.

### 4.12 Portabilidad — 13 preguntas

**SÍ a:** Android · Apple · tabletas · cualquier navegador sin instalar nada · **cambiar de
dispositivo conservando lo no sincronizado** · que la misma persona use dos dispositivos el mismo día
· **llevarse toda la información si termina la relación con el proveedor** · **instalar en servidores
de la propia empresa y no solo en la nube** · funcionar con otro tipo de flor.

**NO a:** **que el dispositivo lo ponga cada persona** (nota **«IDEAL DE LA EMPRESA»**) · operar en
una finca de otro país · otro idioma además del español · unidades de medida distintas por finca.

### 4.13 Accesibilidad — 11 preguntas

**SÍ a:** persona con poca experiencia en celulares · persona con dificultad para leer textos largos ·
imágenes o símbolos además de palabras · letra aumentable · no depender solo del color · uso por quien
no distingue ciertos colores · botones grandes · funcionar en ambiente ruidoso sin depender del
sonido.

**NO a:** escuchar en lugar de leer · cumplir un estándar formal de accesibilidad · **que exista una
manera alternativa de reportar cuando alguien no puede usar el dispositivo**.

---

## 5 · Las tres preguntas de cierre

| Pregunta | Respuesta literal |
|---|---|
| De todo lo que respondió «Sí», ¿hay algo que, si el sistema no lo cumple, haría que no valga la pena usarlo? | **«CERTEZA DE LOS DATOS, QUE SE INGRESEN LOS DATOS CORRECTAMENTE»** |
| ¿Hay alguna de estas exigencias que esté dispuesto a sacrificar con tal de que la captura en campo sea más rápida? | **SÍ** — *sin detallar cuál* |
| ¿Hay algo que necesite el sistema y que ninguna de estas preguntas haya tocado? | **NO** |

> **La primera es la frase más importante que ha dado el cliente sobre prioridades en todo el
> proyecto.** Confirma Confiabilidad como atributo #1 y la reduce a una sola cosa: que el dato entre
> correcto.
>
> **La segunda está sin explotar.** Dijo que sí sacrificaría algo por velocidad, y nadie le preguntó
> qué. Es una pregunta de un minuto que puede reordenar todo el ranking.

---

## 6 · Índice rápido: dónde está cada cifra del cliente

| Cifra | Valor | Dónde lo dijo |
|---|---|---|
| Área en producción | 15 hectáreas | S1-Q P04 |
| Bloques | 25 | S2 §3.1 |
| Camas | ~1.525 | S2 §3.1 |
| Personal de campo | 156 + ~11 administrativos | S2 §3.1 |
| Poscosecha | 57 + 4 administrativos | S2 §3.1 |
| Personas que capturan | **3** (1 supervisor + 2 auxiliares) | S2 §3.9 |
| Personas que consultan | ~20 vendedoras | S2 §3.9 |
| Densidad de siembra | 60 y 90 plantas/m² | S2 §3.1 |
| Ciclo siembra→corte | 3 a 5 meses | S1-Q P11, S2 |
| Corrimiento del ciclo | hasta 15 días | S2 §3.4 |
| Duración del corte | ~7 días | S2 §3.4 |
| Desbotone → corte | ~3 semanas | S2 §3.4 |
| Botón color → corte | ~12 días | S2 §3.4 |
| Productividad esperada | orden de 80–90% | S2 §3.2 |
| Cota dura | tallos ≤ plantas sembradas | S2 §3.2 |
| Banda de aceptación | ±10% sobre la proyección | S2 §3.3 |
| Desviación actual | −6% presupuesto vs. cortado | S2 §3.7 |
| Error de captura | 2%, meta 0% | S2 §3.7 |
| Compra a terceros | ~8% de las ventas, mensual | S2 §3.8 |
| Papel del supervisor | 1 hora/día | S1-Q P28 |
| Digitación del practicante | 4 horas/semana | S1-Q P28 |
| Latencia del dato | 8 días | S1 |
| Revisión de siembra | ~4 horas/semana | S2 |
| Tolerancia a caída | **1 hora** (4 h es *«mucho, mucho»*) | S2 §3.12 |
| Temporada pico | +60% tallos, +60% registros, +30–40% personal | S2 §3.12 |
| Horizonte de proyección | 1 a 1,5 años, unidad base el **día** | S1-Q P20, P21 |
| Frecuencia de ajuste | hoy mensual, se quiere **semanal** | S2 §3.6 |
| Variedades | **9 activas** (S1-Q) vs. **~300** (S2) | *ver brecha* |
| Erradicación | puede ocurrir semanalmente | S2 §3.5 |
| Precio pitch | ~10 USD/usuario/mes, rentable desde ~20 empresas | S1 §1.8 |
| Calificación del contexto | **5/10, dada por él** | S2 |

---

## 7 · Lo que el cliente **nunca** ha dicho

Sección deliberada. Cada línea es algo que aparece en documentos del proyecto y que **no tiene
respaldo en ninguna cita, respuesta escrita ni nota del cliente**:

1. **Un asistente de captura por lenguaje natural o por voz.** Lo único que dijo de IA es *«súbanle
   una capa más [a Power BI] y colóquenle inteligencia artificial»* — capa analítica, no captura. Y
   en la caracterización respondió **NO** a dictar por voz y **NO** a sugerencias mientras captura.
2. **Un tiempo máximo por cama.** Respondió NO a los cinco escalones. No existe el número.
3. **De dónde sale el porcentaje de productividad por variedad.** Nunca se preguntó.
4. **Cómo se reparten los tallos en los ~7 días de corte.** Nunca se preguntó.
5. **A qué nivel de agregación se miden el −6% y el ±10%.** Nunca se aclaró.
6. **El nombre del sistema actual** que arroja «productividades». Dijo que no lo recordaba.
7. **Qué significa la columna `OBSE`** (325/425) de su propio formato, ni qué es exactamente una
   **«línea»**, ni `lote`, `calibre`, `proveedor`, `contenedor`.
8. **Si una cama puede dividirse entre flores distintas** (no solo variedades de la misma flor).
9. **Si dos personas pueden capturar la misma cama el mismo día.** Nunca se le preguntó de frente; la
   caracterización solo le preguntó qué hacer *cuando ya pasó*.
10. **Si el umbral del 2% de tolerancia sobre plantas por línea es correcto.** Es una decisión del
    equipo.
11. **Nomenclatura de camas:** si los códigos se repiten entre bloques y si cambian al renovar. Es la
    llave primaria de todo el modelo.
12. **Cuánto dura el paralelo con el papel**, que él mismo aceptó.
13. **Qué exigencia sacrificaría por velocidad**, que él mismo dijo que sacrificaría.

---

**Fin del documento 1.** Continúa en `2_VOZ_DEL_EQUIPO.md`.
