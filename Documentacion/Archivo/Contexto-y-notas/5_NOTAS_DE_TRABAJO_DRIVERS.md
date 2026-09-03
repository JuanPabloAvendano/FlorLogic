# Notas de trabajo — versión anotada de los drivers

> **No es un entregable.** Es la copia de trabajo de `Documentacion/DRIVERS_ARQUITECTONICOS.md`
> anterior a dejarlo presentable: conserva las marcas `[!]`, los estados EN DUDA, las notas de qué
> se reescribió y por qué decisión, y los apartados de lo que sigue abierto.
> Guardada el 26-ago-2026. El documento presentable no lleva nada de esto.

---

# FlorLogic — Drivers arquitectónicos

> **v1.0 · 26-ago-2026 · Documento acumulador.**
> Reúne en un solo sitio **todo lo que entra en la categoría de driver arquitectónico**:
> funcionalidades significativas, restricciones de negocio, restricciones técnicas, atributos de
> calidad, votaciones, mini QAW y las preguntas de caracterización priorizadas.
>
> **Sobre este documento se escriben los escenarios de calidad `ESC-nnn`.** Es el insumo, no el
> resultado: aquí está el material; los escenarios son el paso siguiente.
****
---

## 0 · Cómo leer y mantener este archivo

**Qué manda sobre qué.** Para el contexto general del proyecto manda
`CONTEXTO.md`. Para el estado de una decisión manda
`Documentacion/Recopilacion/3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md` (v7.0). **Este archivo no
decide nada nuevo:** recoge lo ya decidido y lo pone en forma de driver.

**Reglas de mantenimiento — las mismas del documento de decisiones.**

1. Los identificadores **nunca se renumeran** — con una sola excepción documentada en `§2.5`.
2. **No se borra nada**: lo que cambia se marca y se explica de dónde viene el cambio.
3. Cada entrada dice **de qué decisión sale**, para poder rastrearla hacia atrás.

**Marcas.**

| Marca | Significado |
|---|---|
| `[!]` | Problema abierto, contradicción o cabo suelto |
| `[:W:]` | Texto **reescrito** en esta versión respecto al archivo de origen |
| `[:V:]` | Vigente |
| `[:DD:]` | Derogado, descartado o en rojo |
| `[:OK:]` | Resuelto o cerrado |
| `[:P:]` | Parcial — tiene parte de la respuesta, le falta la otra |
| `[:R:]` | Reinterpretado o transformado: sigue vivo, pero significa otra cosa |
| **PROPUESTA** | Todavía no aprobado por el equipo |

**Sobre las reescrituras.** Las funcionalidades significativas marcadas [:W:] llevan aquí su
**redacción nueva como texto vigente**. La redacción anterior sigue existiendo en
`Documentacion/FuncionalidadesSignificativas.xlsx` (13-ago-2026) y en el histórico del repositorio;
`§2.5` lista qué se reescribió y por qué decisión.

---

## 1 · El marco: qué se está construyendo y bajo qué modelo

**FlorLogic** captura en campo la siembra y el corte de flor por cama, proyecta la producción y
mide la desviación entre lo proyectado y lo cortado.

**El dolor, cuantificado.** Hoy el supervisor llena formatos a lápiz **1 hora al día**, un
practicante rotativo digita **4 horas por semana**, y el dato tarda **8 días** en llegar a
planeación y gerencia. El error declarado de captura es del **2%** — que `C8` identificó como
**error de transcripción de papel a sistema**, es decir, el que **desaparece por diseño** en cuanto
no hay transcripción manual.

**La escala.** 15 hectáreas · 25 bloques · ~1.525 camas · **3 personas capturan** (1 supervisor +
2 auxiliares) · ~12 usarían el sistema · ~20 más solo consultan.

### 1.1 Modelo de entrega — [:V:] VIGENTE: local-first con servicios en línea

> **Instalación en la infraestructura de cada empresa — ~20.000 USD por compañía — más una
> mensualidad por nube, copias, mantenimiento, soporte e IA.** Cifra de partida de la mensualidad:
> **100–200 USD/mes** (`E2`).
>
> **No se le llama SaaS.** Es un servicio recurrente sobre una instalación que el cliente compró y
> que es suya. Habrá **actualización en línea para quien pague la mensualidad**.

> [:DD:] **SaaS multi-tenant DESCARTADO por inviable. `DEC-01` queda derogada.**
> Motivos: sin estudio de mercado · los números no cierran (10 USD/usuario/mes en una finca con 3
> capturadores ≈ 230 USD/mes) · el umbral de «20 empresas» nunca se validó · la carga operativa 24×7
> no está costeada y son dos ingenieros · **y el cliente necesita operar sin internet**.

**Por qué importa para la arquitectura, en una frase:** el sistema se despliega **N veces, dentro de
casa de N clientes**, y el equipo no controla esas instalaciones. Todo lo que sea caro de cambiar
después —el esquema de datos, sobre todo— hay que fijarlo antes de la primera tabla.

**La frase del cliente que ordena las prioridades**, respuesta a *«¿qué haría que no valga la pena
usar el sistema?»*:

> ### **«CERTEZA DE LOS DATOS, QUE SE INGRESEN LOS DATOS CORRECTAMENTE»**

Confirma **Confiabilidad** como atributo #1 y la reduce a una sola cosa: que el dato entre correcto.

### 1.2 Alcance de la fase 1

**Entra:** captura offline de siembra, corte, bajas y erradicaciones · sincronización sin perder ni
duplicar · validación de reglas en el dispositivo · motor de proyección por cama, variedad y fecha ·
regeneración semanal conservando versiones · control de desviación proyectado contra cortado ·
parametrización por empresa · trazabilidad hasta la cama · aislamiento entre empresas · exportación
a Excel y PDF · tableros propios · **integración de lectura con herramientas de BI externas** (`B5`).

**No entra:** precios, ventas y rendimiento económico (`DEC-07`) · cruce con pedidos y clientes ·
Florverde y certificaciones automatizadas (`A13`) · gestión de personal e insumos · poscosecha —el
alcance termina en el corte— · registro obligatorio de actividades culturales · datos climáticos ·
consumo o reemplazo de la app de plagas · almacenamiento de fotografías y documentos escaneados.

---

## 2 · Funcionalidades significativas `RF-nn`

Fuente: `Documentacion/FuncionalidadesSignificativas.xlsx`, hoja `FuncionalidadesSignificativas`
(13-ago-2026), **actualizada aquí** con las decisiones de las rondas 1 a 5 del 25-ago-2026.

**Tipo** — clasificación del propio archivo: `Valor de negocio` · `Reto técnico` · `Ambos`.

### 2.1 Cuadro de mando

| ID | Enunciado en una línea | Tipo | Estado |
|---|---|---|---|
| `RF-001` | Registrar siembra por cama o sección, sin conexión | Valor de negocio | [:W:] reescrito |
| `RF-002` | Registrar corte por cama o sección, sin conexión | Valor de negocio | [:W:] reescrito |
| `RF-003` | Sincronizar sin perder ni duplicar, con ventana larga | Ambos | [:W:] reescrito |
| `RF-004` | Rechazar el evento imposible y explicar el motivo, sin conexión | Valor de negocio | intacto |
| `RF-005` | Rechazar más tallos que plantas sembradas | Valor de negocio | intacto |
| `RF-006` | Calcular tallos proyectados | Valor de negocio | [:W:] reescrito |
| `RF-008` | Regenerar la proyección semanalmente conservando versiones | Valor de negocio | intacto |
| `RF-009` | Registrar baja y erradicación, parcial o total, con motivo | Valor de negocio | [:W:] reescrito |
| `RF-011` | Mostrar la desviación real contra proyectado | Valor de negocio | intacto |
| `RF-012` | Ningún usuario accede a datos de otra empresa, por ningún canal | Valor de negocio | [:W:] reescrito |
| `RF-013` | Parametrización por el administrador de la empresa | Valor de negocio | [:W:] reescrito |
| `RF-014` | Autenticar y aplicar permisos sin conexión | Valor de negocio | intacto |
| `RF-016` | Último valor conocido por campo + traza por sesión de sincronización | Valor de negocio | [:W:] **reescrito a fondo** |
| `RF-017` | Solo el administrador de la empresa corrige lo ya sincronizado | Valor de negocio | [:W:] reescrito |
| `RF-018` | Proyectado y real agregados por día, semana y mes | Valor de negocio | intacto |
| `RF-019` | Exportar a Excel y PDF con las restricciones de pantalla | Valor de negocio | intacto |
| `RF-020` | Catálogo y parametrización descargados antes de capturar | Valor de negocio | [:W:] reescrito |
| `RF-021` | Fecha de negocio confiable y bloqueo ante reloj alterado | Valor de negocio | [:W:] reescrito |
| `RF-022` | Conflictos automáticos: gana el más reciente, con aviso | Ambos | [:W:] reescrito |
| `RF-023` | Versionado inmutable de parámetros de cálculo | Ambos | [:W:] renumerado |
| `RF-024` | Explicar la causa de una caída en la proyección | Valor de negocio | [:W:] renumerado |

**21 funcionalidades significativas: 7 intactas y 14 tocadas** por las decisiones del 25-ago-2026.

### 2.2 Captura en campo

Las seis funcionalidades de la captura en campo. Se sostienen todas sobre lo mismo —**`CN-13`,
offline-first**—: en el invernadero no hay red, así que la validación, la identidad y la fecha se
resuelven en el dispositivo o no se resuelven.

| ID | Título |Estado| Requisito|Necesidad|Nota|
|---|---|---|---|---|---|
| `RF-001` | Registro de siembra |[:W:]| El sistema debe permitir al supervisor de campo registrar la siembra de una **cama o de una sección de cama** —bloque, nave, cama, sección, variedad o subvariedad, fecha y cantidad sembrada, expresada en **la unidad y la agrupación que la empresa tenga configuradas** (densidad por metro, número de líneas, unidades por tallo)— **sin conexión a internet**, admitiendo registros deliberadamente incompletos y confirmando en pantalla que el registro quedó guardado en el dispositivo.| Sin esta información no hay proyección: es el dato sobre el que descansa el resto del producto.|[:W:] **Reescrito por `C6` (ronda 5), `A14` y `DEC-14`.** Sale *«cantidad de esquejes»* —`DEC-14` dice que **nada se cuenta por esqueje**— y entran la **sección** como unidad de inventario, la **agrupación configurable** (el formato real de la finca usa `#LÍNEAS` y `CANTIDAD`, y no tiene columna de área ni de densidad) y la **captura incremental**.|
| `RF-002` | Registro de corte |[:W:]| El sistema debe permitir al supervisor de campo registrar el corte de una **cama o sección** por variedad, grado y fecha **sin conexión a internet**, en la unidad y agrupación configuradas por la empresa, aceptando **varios registros de corte para la misma cama en días distintos** de su ciclo y admitiendo registros parciales.| Es la otra mitad del dato. La siembra dice lo que debería haber; el corte dice lo que hubo. Sin ese contraste, `RF-011` no tiene nada que medir.|[:W:] **Reescrito por el mismo motivo que `RF-001`:** fuera los esquejes, entran la sección y la agrupación configurable.|
| `RF-004` | Rechazo del evento imposible |intacto| El sistema debe impedir el registro de un evento que sea imposible dentro del ciclo fenológico de la cama y **explicar al usuario el motivo del rechazo**, aun sin conexión a internet.| Validar solo al sincronizar convierte un error de diez segundos en un error de ocho días. Es el mecanismo con el que desaparece el 2% de `C8`.| El motivo del rechazo pesa tanto como el rechazo: un «dato inválido» a secas deja al supervisor sin saber qué corregir, en mitad del invernadero y sin red para preguntar.|
| `RF-005` | Cota dura de producción |intacto| El sistema debe rechazar todo dato de producción que implique una cantidad de tallos superior a la cantidad de plantas sembradas en esa cama.| Es el dato imposible más frecuente y el más barato de atajar: nadie corta más tallos de los que sembró.| `[!]` **Es la única regla dura documentada**, junto con el ciclo fenológico de `RF-004`. El catálogo completo sigue sin levantarse — es `D1` y `BR-N6`, y lo responde el cliente.|
| `RF-020` | Catálogo local antes de capturar |[:W:]| El sistema debe exigir y garantizar la descarga de **la parametrización vigente completa de la empresa** —variedades y subvariedades, camas y secciones, agrupaciones, unidades, grados y reglas— en el dispositivo antes de permitir la captura, y el dispositivo debe saber **qué versión del catálogo tiene**.| Sin catálogo local no se valida nada ni se identifica una cama: el supervisor terminaría registrando contra camas que no existen.|[:W:] **Precisado por `A9` (ronda 2):** el catálogo va completo, la idea de bajar «solo lo que le incumbe» se revirtió. **Propuesta de arquitecto, marcada como tal:** bajar solo lo más actualizado y las camas activas con sus procesos — un ciclo ya terminado no hace falta consultarlo sin conexión.|
| `RF-021` | Fecha de negocio confiable |[:W:]| El sistema debe **proponer la fecha de negocio de cada evento a partir del reloj del dispositivo** y **bloquear la captura cuando detecte alteración manual del reloj**, permitiendo al capturador fijar explícitamente una fecha anterior cuando esté registrando información retroactiva.| Los ciclos sobre los que se calcula la proyección son sensibles al desfase de fechas: un reloj corrido contamina la proyección sin dejar ningún rastro visible.|[:W:] **Reescrito por `A1`**, que decidió que no se guardan marcas de tiempo técnicas por dato y dejó este requisito sin sustento. El que queda es otro: del reloj sale la `FECHA`, que sí es campo de negocio y está en el formato real. `[!]` **Sin confirmar con el cliente**, y falta decidir qué hace quien se queda bloqueado en pleno campo: un bloqueo sin salida es peor que el desfase.|

### 2.3 Sincronización y datos

Qué pasa con el dato una vez capturado. Aquí está **el cambio más grande de todo el catálogo**
—`RF-016`— y la regla que lo hace posible: gana el registro más reciente.

| ID | Título |Estado| Requisito|Necesidad|Nota|
|---|---|---|---|---|---|
| `RF-003` | Sincronización sin pérdida ni duplicación |[:W:]| El sistema debe sincronizar los registros capturados en el dispositivo con **la instalación de la empresa** en cuanto haya red disponible, garantizando que **ningún registro se pierda y que ninguno se aplique más de una vez**, soportando ventanas sin sincronizar de **al menos quince días** y **la captura retroactiva** de información con fecha anterior.| Es el tramo donde se juega la promesa del producto: hoy el dato tarda **8 días** en llegar a planeación (`B2`). Y es el único punto en el que un registro puede perderse sin que nadie se entere.|[:W:] **Reescrito por `B6` y `A6`.** «Repositorio central» pasa a ser **la instalación local de la empresa**; la nube es respaldo y servicios. Y los quince días quedan bien entendidos: **no son quince días de dispositivo apagado, son papeles que aparecen tarde** y que igual hay que registrar.|
| `RF-022` | Resolución de conflictos |[:W:]| El sistema debe resolver los conflictos de información sincronizada **de forma automática, por orden cronológico estricto: gana el registro capturado más recientemente**. No hay mediación humana. El sistema debe **avisar a quien capturó** cuando su registro fue descartado o modificado, y el administrador de la empresa debe poder consultar la bitácora de esos casos.| Dos supervisores pueden tocar la misma cama con minutos de diferencia, y la información no puede quedarse esperando a que alguien decida cuál vale: tiene que entrar en la proyección.|[:W:] **Reescrito por `B7` (ronda 3).** La nota del cliente *«NO DEJA INGRESAR EL ÚLTIMO REGISTRO»* significaba **«no deja entrar el más viejo»** — la habíamos leído al revés, así que la decisión y el cliente coinciden. Deja de ser una regla de conflicto para ser el **modelo de datos** de `RF-016`: gana el más reciente porque el estado *es* el último valor conocido. [:DD:] **`DEC-05` derogada; `CN-24` se reescribe.**|
| `RF-016` | Traza mínima: último valor conocido y sesión de sincronización |[:W:]| El sistema debe conservar, **para cada campo de una cama o sección, su último valor conocido con la fecha en que se capturó**, y debe conservar, **por sesión de sincronización**, quién sincronizó, desde qué dispositivo, qué camas y qué cifras entraron en esa sesión. **No se conserva el valor anterior a una corrección, ni las versiones intermedias, ni se exige motivo escrito para corregir.**| Es la traza mínima del sistema: saber **de quién, desde dónde y cuándo** salió cierta información, sin perseguir el detalle de cada dato — que es justo lo que el cliente rechazó.|[:W:] **Reescrito a fondo por `B8`, `A15` y `A1`; es el cambio más grande del catálogo.** `B8`: el cliente dijo **«SOLO LA CORREGIDA»**, y para él corregir sin trazabilidad exhaustiva **no es una concesión, es un requisito**, porque con este volumen se corrige varias veces al día. `A15`: no se busca completitud, sino el último valor de cada campo con su fecha — si el 24 se capturó `x, y, z` y el 25 solo `x, y`, el sistema muestra **`x, y` (25) · `z` (24)**. `A1`: la unidad de trazabilidad es la **sesión**, no el dato. `[!]` **Lo que se pierde a conciencia:** no se podrá reconstruir qué decía un dato antes de una corrección. El cliente lo pidió así, pero que quede escrito que **se pierde**, no que no existía — es la mitad que se le amputa a *Capacidad para ser Auditado*, el driver #4.|
| `RF-017` | Quién puede corregir lo ya sincronizado |[:W:]| El sistema debe impedir que un registro ya sincronizado sea modificado o eliminado por un rol distinto del **administrador de la empresa** —el ingeniero de sistemas de la finca—, debe dejar constancia en la bitácora de la sesión de quién hizo la corrección, y debe impedir la corrección libre de un periodo ya cerrado.| Corregir es una operación normal y frecuente, pero no puede serlo para cualquiera ni sobre un periodo del que ya se rindieron cuentas.|[:W:] **Reescrito por `C9`, `B8` y `A11`.** Cae la exigencia de *«aprobación registrada»* y de motivo escrito (`B8`); se precisa que el administrador es **de la empresa** y empleado del cliente (`C9`, bajo local-first); y entra el **cierre de periodo**, que `A11` decidió **en contra del cliente** por motivos legales.|

### 2.4 Proyección, consulta y frontera de la empresa

Los once restantes: el motor que convierte lo sembrado en tallos esperados, lo que se enseña
después, y las dos fronteras —la de la empresa y la del usuario sin conexión—.

| ID | Título |Estado| Requisito|Necesidad|Nota|
|---|---|---|---|---|---|
| `RF-006` | Cálculo de tallos proyectados |[:W:]| El sistema debe calcular los tallos proyectados de una cama o sección a partir de **la agrupación y la unidad configuradas** —área y densidad de siembra, número de líneas, o unidades por tallo, según lo que use esa empresa— y del **porcentaje de productividad esperada** de esa variedad.| Es el motor del producto: convierte lo sembrado en tallos esperados, y sobre esos tallos se vende.|[:W:] **Reescrito por `C6` (ronda 5):** densidad por metro, cantidad por líneas y unidades por tallo **están todas contempladas**, y cada finca usa la suya. `[!]` **De dónde sale el % de productividad por variedad sigue sin preguntarse: es `D3`.**|
| `RF-008` | Regeneración de la proyección |intacto| El sistema debe regenerar la proyección con la información acumulada **al menos una vez por semana**, conservando la versión anterior de cada proyección generada.| Una proyección que no se rehace envejece, y hoy rehacerla a mano puede llevar hasta un mes.| Conservar la versión anterior es lo que hace posible `RF-011`: sin la foto previa no hay contra qué medir la desviación. Va de la mano de `RF-023` y de `CN-27`.|
| `RF-009` | Bajas y erradicaciones |[:W:]| El sistema debe permitir registrar la **baja parcial o la erradicación total** de una cama o sección —en porcentaje o en tallos— **con el motivo de la disminución, incluida la enfermedad**, descontándola de la producción que quedaba proyectada para esa cama.| Casi ninguna cama produce lo que prometía. Si la merma no entra, la proyección miente hacia arriba — y sobre esa cifra se compromete flor con el comprador.|[:W:] **Absorbe `RF-010` por `DEC-08`** y recoge `RFP-02`. Conserva la diferencia operativa: la baja parcial se registra en campo y es frecuente; la erradicación total la decide el ingeniero de producción o el gerente de ventas. `[!]` La enfermedad se captura **a mano** aquí aunque la app de plagas ya la registre por su lado (`CN-19`): verificar con el cliente que no se duplica trabajo.|
| `RF-011` | Desviación real contra proyectado |intacto| El sistema debe mostrar la desviación entre el corte real y el corte proyectado por cama, variedad y periodo, señalando los casos que salen de la banda de tolerancia parametrizada.| **Es lo que instrumenta la métrica de éxito del proyecto** (`A19`): la banda es de **±10% en tallos** y lo que se mide es la distancia entre los tallos proyectados y los cortados.| Sin este requisito el proyecto no puede demostrar que mejora nada. Depende de `RF-008` y `RF-023`: si los parámetros cambian por debajo de una proyección ya emitida, la desviación medida deja de significar algo.|
| `RF-024` | Explicar la caída de la proyección |[:W:]| El sistema debe identificar e informar de manera visual y clara **la causa** de una caída en la producción proyectada, especificando el motivo.| Si la proyección baja y nadie sabe por qué, el usuario no puede hacer nada al respecto — y fue él quien lo pidió.|[:W:] **Renumerado**: en el archivo de origen figura como `FR-024`.|
| `RF-023` | Versionado inmutable de parámetros |[:W:]| El sistema debe conservar una **captura estática en el tiempo** de los parámetros de cálculo y proyección, de modo que una proyección ya emitida **no se recalcule sola** ni cambie de resultado cuando cambian los parámetros.| Una proyección emitida es la base sobre la que ya se vendió. Cambiarle los parámetros por debajo es alterar el pasado.|[:W:] **Renumerado** (era `FR-023`). **Respaldado por el cliente**: respondió SÍ a que una proyección recalculada meses después dé el mismo resultado, con la nota *«si no se modifica sí»*. Es la base de `CN-27`.|
| `RF-018` | Agregación por día, semana y mes |intacto| El sistema debe presentar la producción proyectada y la producción real agregadas por día, por semana y por mes, calculadas sobre el mismo conjunto de datos.| Gerencia general lo mira mensual, ventas semanal y producción diario. Son tres horizontes del mismo negocio.| Lo significativo no son las tres vistas, sino que salgan **del mismo cálculo**: en cuanto se separan, cada área discute con un número distinto.|
| `RF-019` | Exportación a Excel y PDF |intacto| El sistema debe permitir exportar a Excel y a PDF la información que el usuario tiene autorizado consultar, aplicando las mismas restricciones que rigen en pantalla.| Es como la información sale hacia fuera para las ~20 personas que solo consultan, y en el formato que el negocio ya usa.| **Y es la única vía de auditoría que se construye:** `A13` decidió **no** construir ningún formato ni exportación específica para certificaciones — la verificación manual se hace con estos mismos Excel y PDF. `[!]` Verificar que la frontera de empresa se respeta también **dentro** del archivo exportado.|
| `RF-013` | Parametrización por la empresa |[:W:]| El sistema debe permitir al **administrador de la empresa** parametrizar sus variedades y subvariedades, agrupaciones y unidades, densidades de siembra, porcentajes de productividad esperada, grados de calidad, días a corte por etapa, duración del corte, bandas de tolerancia **y qué columnas de la plantilla de captura usa**, sin intervención del equipo de desarrollo.| Sin parametrización propia, cada finca nueva es una intervención del equipo de desarrollo. Con N instalaciones en casa de clientes, eso no se sostiene.|[:W:] **Reescrito por `A14` (ronda 2) y `B6`.** Sale la justificación *«debido al enfoque SaaS»* —derogado— y entra el modelo de plantilla de `A14`: **una plantilla predefinida amplia y común, de la que cada empresa activa el subconjunto que necesita.** El ejemplo es del cliente: *«la plantilla tiene 20 columnas; yo solo uso 5»*. No es un diseñador libre de formatos, y por eso es barato.|
| `RF-012` | Frontera de la empresa |[:W:]| El sistema debe garantizar que ningún usuario acceda, **por ningún canal**, a datos de una empresa distinta de aquella a la que pertenece su cuenta. En los datos operativos la frontera es **física** —una instalación y una base de datos por empresa—; en la **capa de servicios en línea** —copias, sincronización, IA analítica, BI— se sostiene con el **cifrado con la llave del cliente** y con el **discriminador de empresa en toda consulta desde el día uno, con una prueba automatizada que falle si falta**.| Los clientes son fincas que compiten entre sí, y `CN-03` obliga a tratar su información como secreto empresarial. Es además la objeción que el propio cliente predijo en S1.|[:W:] **Reescrito por `E3` (rondas 3 y 5) y `B4`.** Bajo local-first el aislamiento operativo deja de ser un problema de diseño de consultas y pasa a ser consecuencia del despliegue. Lo que sigue vivo es la capa compartida, y ahí *«por ningún canal»* incluye el BI, los exports de `RF-019` y **la IA, que no puede arrastrar contexto de una empresa a otra**. El discriminador cuesta lo mismo hoy y convierte un futuro multi-tenant en un despliegue y no en una reescritura.|
| `RF-014` | Identidad y permisos sin conexión |intacto| El sistema debe autenticar al usuario y aplicar los permisos de su rol en el dispositivo móvil aun cuando no haya conexión a internet.| Sin identidad local no se sabe quién capturó qué, y toda la traza de `RF-016` descansa sobre eso.| `[!]` **Sigue sin definirse la duración de la ventana de sesión offline** ni qué pasa si se pierde el dispositivo. Es `BR-N5` y sigue sin preguntarse. Es el punto donde chocan Seguridad y Disponibilidad: una sesión larga es cómoda y riesgosa a la vez.|

### 2.5 Qué se reescribió, qué se renumeró y qué quedó fuera

| ID | Qué cambió | Decisión que lo obliga |
|---|---|---|
| `RF-001`·`RF-002` | Fuera los esquejes; entran sección, agrupación configurable y captura incremental | `C6`, `A14`, `DEC-14` |
| `RF-003` | Destino = instalación local; ventana de 15 días y captura retroactiva | `B6`, `A6`, `B1` |
| `RF-006` | Unidad y agrupación configurables | `C6` |
| `RF-009` | Absorbe `RF-010`; entra el motivo de la disminución | `DEC-08`, `RFP-02` |
| `RF-012` | Aislamiento físico + discriminador + capa de servicios | `E3`, `B4`, `B6` |
| `RF-013` | Fuera la justificación SaaS; entra la activación de columnas | `A14`, `B6` |
| `RF-016` | **Deja de exigir el valor anterior**; último valor por campo + sesión de sincronización | `B8`, `A15`, `A1` |
| `RF-017` | «De la empresa»; sin motivo escrito; entra el cierre de periodo | `C9`, `B8`, `A11` |
| `RF-020` | Catálogo completo; propuesta de bajar solo lo activo | `A9` |
| `RF-021` | De marca de tiempo inmutable a **fecha de negocio** propuesta por el reloj | `A1` |
| `RF-022` | Automático, gana el más reciente, sin mediación humana | `B7` |
| `FR-023`→`RF-023` · `FR-024`→`RF-024` | **Renumerados**: el archivo de origen escribe «FR-» | corrección de forma |

**Requisitos que existen pero NO son funcionalidades significativas** (`DEC-08`):

| ID | Destino |
|---|---|
| `RF-007` repartir los tallos sobre los días de corte | **Entra al producto, no al catálogo de significativas.** Depende de `D3` |
| `RF-010` baja de producción | **Absorbido por `RF-009`** |
| `RF-015` ocultar el precio de venta | **Sin objeto** — `DEC-07` sacó el dinero del dominio |
| `RF-C19` vista de calidad de datos | **Absorbido por `RF-016`** — `[!]` y `B8` le quitó la mitad |

**Candidatos `RFP-nn` — sin validar con el cliente**, y cómo quedaron tras las cinco rondas:

| ID | Qué | Estado |
|---|---|---|
| `RFP-01` | Siembra por sección de cama | [:OK:] **Absorbido por `RF-001`** |
| `RFP-02` | Motivo de la disminución, incluida enfermedad | [:OK:] **Absorbido por `RF-009`** |
| `RFP-03` | Vista geométrica de camas con % de producción | [:V:] **Se mantiene** (`B11`), pero **solo en la consulta**, nunca en la captura |
| `RFP-04` | Doble lectura: tallos aproximados y % de plantas reales | Abierto (`DEC-15`) |
| `RFP-05` | Asistente de captura offline | [:V:] **Confirmado por `C2`** — justificado **por calidad del dato, no por velocidad** |
| `RFP-06` | Consultas y análisis asistidos por IA en la nube | [:V:] Vigente. `[!]` Alcance sin acotar |
| `RFP-07` | Plantillas de captura configurables | [:R:] **Vuelve al alcance en forma reducida:** activar columnas de una plantilla común (`A14`) |
| `RFP-08` | Restaurar los datos de una empresa sin afectar a las demás | [:OK:] Trivial bajo local-first |

---

## 3 · Restricciones de negocio `CN-01` … `CN-09`

Fuente: `Documentacion/RestriccionesNegocio.xlsx` (13-ago-2026), 9 filas.
Tipos: Tiempo · Presupuesto · Legal · Proceso · Humano.

`[!]` **El archivo no lleva los identificadores `CN` escritos**: la numeración vive en el modelo
ArchiMate, en `CONTEXTO.md` y aquí. `[!]` **La columna «Plan acción» está vacía en las nueve filas**
—en el archivo de restricciones técnicas sí está llena—.

| ID | Tipo | Restricción | Justificación |
|---|---|---|---|
| **`CN-01`** | Tiempo | El producto debe estar **finalizado e implementado en mayo de 2027** | Después de la temporada alta de marzo y abril; desde mayo hay tiempo para instruir y adaptar a los usuarios |
| **`CN-02`** | Presupuesto | **~20.000 USD** (~61 M COP) para el desarrollo del sistema | Es lo que el cliente puede pagar dentro de los plazos estimados |
| **`CN-03`** | Legal | Trato de la información bajo la figura de **secreto empresarial** | Art. 260 de la Decisión 486 de la Comisión de la CAN. **Base legal de `RF-012` y de `CN-28`** |
| **`CN-04`** | Proceso | **El proceso a apalancar apenas se está definiendo.** Se implementa lo que se vaya identificando | El producto no se desarrolla con participación en la operación real: se construye sobre entrevistas, y de ahí salen las brechas |
| **`CN-05`** | Presupuesto | **Se desconoce el presupuesto de mantenimiento** | Nunca se definió con el cliente qué se paga una vez implementado |
| **`CN-06`** | Humano | Decisiones de arquitectura tomadas por **ingenieros sin experiencia medible en el sector** | Es la condición real del equipo, y conviene tenerla escrita |
| **`CN-07`** | Proceso | El despliegue e instrucción **no puede retrasar la operación más de 7 días** | La ventana de baja carga posterior a la temporada alta es corta |
| **`CN-08`** | Humano | **Resistencia al cambio** y curva de adopción de los supervisores | El personal está arraigado a lápiz y papel; la adopción es crítica para el éxito |
| **`CN-09`** | Tiempo | **Disponibilidad limitada para pruebas y entregas previas** a la implementación | No hay margen para pruebas de campo previas ni retroalimentación continua |

**Lo que cambió con el modelo de entrega.**

- `CN-02` **ya no choca** con el pitch de 10 USD/usuario/mes: bajo local-first **la unidad de cobro
  es la instalación**, ~20.000 USD por compañía, y la primera empresa ya paga (`E4`). Lo que queda
  por costear es la mensualidad de **100–200 USD** (`E2`).
- `CN-05` sigue abierta y ahora tiene forma concreta: **hay que recalcular esa mensualidad antes de
  firmar**, con quince instalaciones, y **decidir qué pasa con una instalación que deja de pagar** —
  si se queda sin actualizaciones, diverge de versión, que es justo lo que teme `CN-29`.
- `CN-08` gana peso: `A17` decidió que **la usabilidad se aborda por método** —demos, feedback y
  cambios constantes— y no por umbral, precisamente porque no hay número que fijar.

---

## 4 · Restricciones técnicas `CN-10` … `CN-35`

Fuente: `Documentacion/RestriccionesTecnicas-IA.xlsx` (19-ago-2026), 26 filas.
**Serie única y continua con las de negocio.** No existe serie `RT-nn`. Los IDs no se reciclan.

Tipos: **impuesta** (7) — viene de fuera y no se negocia — y **adoptada** (19) — la elige el equipo.

### 4.1 Impuestas

| ID | Restricción | Estado |
|---|---|---|
| **`CN-10`** | [:W:] **Reescrita.** ~~PowerBI no es restricción impuesta~~ → **la lectura desde una herramienta de BI externa sí es una necesidad declarada del cliente**, con nota literal **«POWER BI»** | [:DD:] **CERRADO revertido** por `B5` |
| **`CN-11`** | El cobro exige una pasarela de pago; PayU es la opción mencionada | [:W:] **Se encoge:** con cobro por instalación, la pasarela solo hace falta para la **mensualidad**, no para el producto |
| **`CN-17`** | **No hay conectividad de datos en el área de cultivo.** La captura ocurre en invernaderos sin red; sí hay señal en las oficinas | DENTRO — es un hecho del entorno |
| **`CN-18`** | **Doble canal obligatorio:** app móvil de captura + web de consulta | DENTRO |
| **`CN-19`** | **La app de plagas sigue viva:** no se reemplaza, no se consume, no se integra. Solo entra «enfermedad» como motivo, capturado a mano | DENTRO |
| **`CN-20`** | Modelo heredado de **~300 tablas** (45 de producción) y un sistema de productividades que el cliente no supo nombrar | **EN DUDA** `[!]` **BLOQUEANTE** |
| **`CN-21`** | Dispositivo de captura sin especificar y **sin partida de hardware** | **EN DUDA** [:W:] ver abajo |

**`CN-10` — lo que decidió `B5`, y es un cambio de dirección completo.**
> **BI propio Y posibilidad de integrar otros servicios. Totalmente necesario hacerlo.**

`DEC-06` —*«BI propio y cerrado, antes que integración con terceros»*— **queda derogada**. El propio
equipo ya se había contradicho en S4, dos días después de decidirla: *«sí vamos a integrar, pero a
la vez ofrecer»*. **Y bajo local-first el coste desaparece:** Power BI conecta contra **la propia
instalación del cliente**, no contra una vista por empresa sobre N bases en nuestra nube. Es el
escenario más barato posible y deja de chocar con `RF-012`.

**`CN-21` — precisada por `B10`.** No se obliga a la empresa a comprar dispositivos si los
trabajadores ya tienen celular; pero **si un dispositivo es muy viejo, sale más barato reemplazarlo
que adaptar el sistema a él**. `[!]` Sigue sin saberse qué celulares tienen hoy los tres capturadores.

**`CN-20` sigue siendo el bloqueante técnico número uno del cliente.** Sin ver el sistema actual ni
su diccionario de datos no se puede decidir si FlorLogic reemplaza, alimenta o convive, ni estimar
la carga inicial. Está en el grupo `D`.

### 4.2 Adoptadas

| ID | Restricción | Estado |
|---|---|---|
| **`CN-12`** | **RBAC por par (rol, empresa).** Controla capacidades —quién captura, corrige, parametriza—, no visibilidad de campos | DENTRO [:W:] ver `A8` |
| **`CN-13`** | **Offline-first obligatorio:** captura, validación, autenticación y fecha funcionan íntegramente en el dispositivo | DENTRO — **es la restricción rectora** |
| **`CN-14`** | [:W:] **Reescrita:** BI propio **y además** integración de lectura con herramientas externas | [:W:] por `B5` |
| **`CN-15`** | **Continuidad:** pérdida de información **CERO** · fallo de funcionamiento **máx. 1 hora** · reparación o restauración de respaldo **máx. 1 día** · desajuste de datos con tolerancia mayor, siempre sin pérdida | DENTRO — **los mejores números del proyecto** |
| **`CN-16`** | **Una base de datos independiente por empresa, con esquema común** | DENTRO — bajo local-first es **una instalación por empresa** |
| **`CN-22`** | **Todas las reglas duras evaluables en el dispositivo**, con motivo de rechazo en el momento | DENTRO `[!]` catálogo de reglas sin levantar |
| **`CN-23`** | **Autenticación y permisos resueltos en el dispositivo** durante toda la jornada | DENTRO `[!]` `BR-N5` sin preguntar |
| **`CN-24`** | [:W:] **Reescrita:** sincronización idempotente y **resolución automática por orden cronológico — gana el más reciente. Sin mediación humana**, con aviso a quien capturó y bitácora | [:W:] por `B7` |
| **`CN-25`** | [:W:] **Reescrita:** del reloj sale **la fecha de negocio**; bloqueo ante reloj alterado | [:W:] por `A1` |
| **`CN-26`** | **Catálogo y parametrización descargados y versionados** antes de capturar | DENTRO — completo, por `A9` |
| **`CN-27`** | **Versionado inmutable** de proyecciones y parámetros de cálculo | DENTRO — respaldado por el cliente |
| **`CN-28`** | [:W:] **CERRADA:** cifrado en tránsito y en reposo, respaldos incluidos, **con la llave del lado del cliente — la tiene su dispositivo** | [:OK:] **sale de EN DUDA** por `B4` |
| **`CN-29`** | **Migraciones de esquema automatizadas y verificables** sobre las N instalaciones | DENTRO — [:W:] **empeora y se resuelve a la vez**, ver abajo |
| **`CN-30`** | [:W:] **Recalculada:** el pico de calendario sigue siendo simultáneo (+60% en registros, +30–40% en personal), pero se reparte sobre **~10 personas por instalación**, no sobre ~200 en una plataforma compartida | [:W:] por `A10` y `B6` |
| **`CN-31`** | **Asistente de captura local**, vocabulario restringido al catálogo de la finca. **Propone, el sistema valida, el usuario confirma. Nunca escritura silenciosa** | DENTRO — reconfirmada por `C2` |
| **`CN-32`** | **La IA analítica corre en la nube y jamás es dependencia de la captura** | DENTRO |
| **`CN-33`** | [:DD:] **Derogada.** Ya no es cierto que Excel y PDF sean la **única** interoperabilidad: entra la lectura desde BI externo | [:DD:] por `B5` |
| **`CN-34`** | [:W:] **Se encoge:** el operador de la plataforma solo opera la **capa de servicios en línea**, sobre datos que llegan cifrados. Quien administra la instalación es el **ingeniero de sistemas de la finca** | [:W:] por `C9` y `B6` |
| **`CN-35`** | **Costo operativo acotado:** sin licencias ni servicios cuyo precio escale por empresa o por usuario | DENTRO |

**`CN-29` — el punto que más se movió, y conviene entenderlo entero.**
Bajo local-first **empeora**: migrar el esquema en **N sitios dentro de casa de clientes, con
versiones distintas**, es más difícil que en N bases propias. **Y `E2` aporta el mecanismo que
faltaba:** el **sistema de actualización en línea** para quien paga la mensualidad — *pagas, te
actualizas*. `[!]` **El riesgo que queda escrito:** la instalación que deja de pagar **diverge de
versión**, que es exactamente lo que esta restricción teme.

**`CN-12` — matizada por `A8`.** Dentro de una empresa **la información es visible para todos**: no
se añade capa de seguridad ni de verificación por rol para la visibilidad. Lo que hace falta son
**filtros mediante paneles** — cada quien llega a lo suyo filtrando, no porque el sistema le oculte
nada. `[!]` Es una **decisión tomada en contra de una respuesta escrita del cliente**.

### 4.3 Restricciones nuevas propuestas — `CN-36` … `CN-38` · **PROPUESTA**

Salen de las rondas 3 a 5 y **no están todavía en ningún archivo**. Se proponen con IDs nuevos de la
serie continua, sin reciclar ninguno.

| ID | Restricción propuesta | Tipo | De dónde sale |
|---|---|---|---|
| **`CN-36`** | **Los campos capturados son DATOS, no COLUMNAS.** Añadir un tipo de labor, de medición o de agrupación **no puede modificar lo previo ni obligar a rehacer la captura existente**, y no puede exigir una migración de esquema | adoptada | `C4` + `C6` (ronda 5) |
| **`CN-37`** | **Entrega local-first:** el sistema se instala en la infraestructura de cada empresa y opera **sin internet** sobre su información activa; la nube presta respaldo, sincronización, actualización e IA | impuesta | `B6`, `A10`, `A20` |
| **`CN-38`** | **La verificación de cumplimiento no se automatiza.** No se construye ninguna vía de auditoría para terceros distinta de la que ya tienen los administradores; lo que se exige es que la información exportable sea **suficiente y válida** para que una persona haga esa verificación a mano | impuesta | `A1`, `A13` |

> `[!]` **`CN-36` es la restricción de arquitectura más importante que sale de todo el trabajo de
> depuración.** Con N instalaciones desplegadas dentro de casa de clientes, un cambio de esquema es
> lo más caro que le puede pasar al modelo de entrega. **Hay que fijarla antes de la primera tabla.**

### 4.4 Estado de las restricciones, de un vistazo

| Estado | Cuántas | Cuáles |
|---|---|---|
| **DENTRO** | 21 | Las 18 adoptadas que siguen en pie —`CN-28` incluida, que acaba de entrar— más `CN-17`, `CN-18` y `CN-19` |
| **CERRADO / derogado** | 2 | `CN-10` (revertida por `B5`) · `CN-33` (derogada por `B5`) |
| **EN DUDA** | 3 | `CN-11` · `CN-20` `[!]` **bloqueante, la responde el cliente** · `CN-21` |
| **Reescritas** | 8 | `CN-10`, `CN-11`, `CN-14`, `CN-24`, `CN-25`, `CN-28`, `CN-30`, `CN-34` |
| **Propuestas** | 3 | `CN-36`, `CN-37`, `CN-38` |

**`CN-28` salió de EN DUDA**: era una de las dos que había que cerrar primero, y la cerró `B4` con
la opción que bajo SaaS era imposible — **la llave la tiene el dispositivo del cliente**. La otra,
`CN-20`, sigue abierta y solo el cliente la cierra.

`[!]` **Pendiente de siempre:** sincronizar `CN-17`..`CN-38` al modelo `.archimate` como elementos
`Constraint`. Hoy viven solo en el xlsx y en este documento.

---

## 5 · Atributos de calidad — los 13 vigentes

Fuente: `Documentacion/MINI QAW PLANTILLA NO TERMINADA.xlsx`.
**Son 13, no 14.** Dos redefiniciones que hay que respetar en todo documento nuevo:

- [:DD:] **«Seguridad de funcionamiento» ya no existe como atributo propio.** Queda **absorbida por
  Confiabilidad**, que pasa a cubrir también **la pérdida y el daño de información ante fallos**.
- [:DD:] **«Trazabilidad» ya no existe con ese nombre.** Se reemplaza por **Capacidad para ser
  Auditado**, definida como **trazabilidad + cumplimiento**: certificaciones, autoridad
  fitosanitaria, auditor externo, retención normativa, datos personales y secreto empresarial
  (`CN-03`).

**Y una tercera definición fijada en sesión, que no es la que se le explicó al cliente:**

- **Accesibilidad = limitantes físicas.** La facilidad de uso para personas con poca alfabetización
  digital **no entra como accesibilidad: entra como Experiencia de Usuario** (`A18`).
  `[!]` Es la definición del equipo. El cliente puso Accesibilidad de última y luego respondió SÍ a
  casi todo lo que preguntaba por ella: **es probable que no entendiera el término**.

---

## 6 · Votaciones

### 6.1 Priorización por actor — hoja `2. Priorización-QA` · [:V:] **la que manda**

Cada actor reparte **1..13, menor = más prioritario**. Total 91 por actor, **273 global**.
Los tres actores son **Supervisor de campo · Gerente de producción · Administrador del sistema**, y
`C4` confirmó quiénes votaron de verdad: **supervisor de campo, gerente de producción e ingeniero de
sistemas de la finca** — exactamente las tres columnas de la hoja.

| # | Atributo | Sup. campo | Ger. producción | Adm. sistema | Total | % |
|---|---|:--:|:--:|:--:|:--:|:--:|
| **1** | **Confiabilidad** | 1 | 1 | 1 | **3** | 1,10% |
| **2** | **Disponibilidad** | 2 | 2 | 7 | **11** | 4,03% |
| **3** | **Rendimiento** | 3 | 5 | 9 | **17** | 6,23% |
| **4** | **Capacidad para ser Auditado** | 9 | 4 | 4 | **17** | 6,23% |
| **5** | **Capacidad** | 11 | 3 | 5 | **19** | 6,96% |
| 6 | Capacidad para ser Administrado | 13 | 7 | 3 | 23 | 8,42% |
| 7 | Experiencia de Usuario | 6 | 6 | 12 | 24 | 8,79% |
| 8 | Seguridad | 10 | 8 | 6 | 24 | 8,79% |
| 9 | Interoperatividad | 5 | 10 | 10 | 25 | 9,16% |
| 10 | Escalabilidad | 8 | 9 | 8 | 25 | 9,16% |
| 11 | Capacidad para ser Soportado | 12 | 13 | 2 | 27 | 9,89% |
| 12 | Portabilidad | 4 | 11 | 13 | 28 | 10,26% |
| 13 | Accesibilidad | 7 | 12 | 11 | 30 | 10,99% |

> ## **Los cinco drivers: Confiabilidad · Disponibilidad · Rendimiento · Capacidad para ser Auditado · Capacidad.**

**Confiabilidad es 1 para los tres actores: es el único consenso total del proyecto**, y coincide
exactamente con la frase de cierre del cliente.

**Este ranking sustituye a los dos anteriores** (`0_CONTEXTO_v3.md §6` y el mini QAW viejo), que eran
incompatibles entre sí y que `DEC-03` había dejado EN DUDA. Los viejos ya no se usan.

**Los tres empates, y cómo quedaron:**

| Empate | Puntos | Estado |
|---|:--:|---|
| Rendimiento / Capacidad para ser Auditado | 17 | `[!]` **sin romper** |
| Experiencia de Usuario / Seguridad | 24 | `[!]` **sin romper** |
| **Interoperatividad / Escalabilidad** | 25 | [:OK:] **Roto por `C4` (ronda 5): gana Interoperatividad** (puesto 9); Escalabilidad al 10 |

**Cómo se rompió el tercero, porque el criterio sirve para los otros dos:** por patrón de respuestas
ganaba Escalabilidad (**11 SÍ de 11**, el único bloque sin un solo NO) frente a Interoperatividad
(10 SÍ y 3 NO). Ganó Interoperatividad por el criterio de `§0.3` del documento de decisiones:
**se priorizan los atributos que el usuario final puede medir** — Excel, PDF, Power BI son cosas que
el usuario toca; la escalabilidad solo se nota cuando falta.

### 6.2 Por qué Escalabilidad no es driver, aunque subiera

> **Se quieren ofrecer atributos de calidad medibles por los usuarios finales.**

`[!]` **Y eso deja una tensión que hay que asumir a conciencia** (`E6`): el producto se despliega en
N instalaciones y la escalabilidad no es driver de calidad. No es incoherente —el crecimiento es
**comercial, no arquitectónico**, y bajo local-first cada instalación atiende ~10 personas— **pero
no puede olvidarse al diseñar**, y `CN-36` es justamente lo que impide que se olvide.

### 6.3 Trade-off — hoja `1. Trade-Off-QA`

Tres listas de ordenamiento, hechas antes de la priorización por actor.

| # | Propuesto por el **cliente** | Propuesto por los **arquitectos** | **«Promediado»** |
|---|---|---|---|
| 1 | Confiabilidad | Confiabilidad | Confiabilidad |
| 2 | Experiencia de Usuario | Disponibilidad | Capacidad para ser Auditado |
| 3 | Disponibilidad | Capacidad | Rendimiento |
| 4 | Seguridad | Rendimiento | Seguridad |
| 5 | Rendimiento | Capacidad para ser Auditado | Experiencia de Usuario |
| 6 | Cap. para ser Soportado | Cap. para ser Administrado | Disponibilidad |
| 7 | Cap. para ser Administrado | Interoperatividad | Escalabilidad |
| 8 | Portabilidad | Experiencia de Usuario | Cap. para ser Administrado |
| 9 | Capacidad | Seguridad | Portabilidad |
| 10 | Accesibilidad | Portabilidad | Capacidad |
| 11 | Cap. para ser Auditado | Cap. para ser Soportado | Cap. para ser Soportado |
| 12 | Escalabilidad | Accesibilidad | Interoperatividad |
| 13 | Interoperatividad | Escalabilidad | Accesibilidad |

`[!]` **La tercera columna no es la media aritmética de las dos primeras**, y la operación que la
produce no está justificada en ninguna parte. **Verificarla antes de usarla para cualquier cosa.**

**Las dos divergencias que importan, y que ya tienen respuesta:**

1. **El cliente puso Experiencia de Usuario en el puesto 2; el equipo en el 8.** El argumento del
   equipo fue *«son usuarios que ya conocen el negocio y solo pasan de papel a celular»*.
   **Y luego el cliente dijo NO a todo lo medible de UX** (`A17`) — de ahí que se aborde por método,
   no por umbral.
2. **El cliente puso Capacidad para ser Auditado en el 11 y luego pidió trazabilidad «DE POR VIDA»**
   (`A12`). El 11 se lo dio al **concepto estrecho** de trazabilidad; el atributo actual es el
   amplio, y en la priorización por actor queda **puesto 3-4, driver #4**.

---

## 7 · Mini QAW

El mini QAW es el instrumento con el que se produjo todo lo del `§6` y del `§8`. Vive en
`Documentacion/MINI QAW PLANTILLA NO TERMINADA.xlsx` — **esa es la copia vigente**.

| Hoja | Para qué sirve | Estado |
|---|---|---|
| `1. Trade-Off-QA` | Tres ordenamientos de los 13 atributos: cliente, arquitectos y «promediado» | [:OK:] Completa · `[!]` la tercera columna sin justificar |
| `2. Priorización-QA` | Reparto 1..13 por cada uno de los tres actores. **Es el ranking vigente** | [:OK:] Completa y cerrada |
| `3. Caracterización` | **262 preguntas** de necesidad, respondidas por Juan (260), Jerónimo (260) y **el Cliente (262)**, con 29 notas literales | [:OK:] Completa |
| `Top 65 - Priorizadas` | Las 65 preguntas con más puntaje, con la votación de los tres roles. **Es el insumo directo de los escenarios** | [:OK:] Completa |
| `4, Lluvia-de-Escenarios` | Donde irán los escenarios | [:DD:] **Vacía — es el trabajo que sigue** |

`[!]` **Higiene pendiente.** Hay **tres copias** de este archivo (raíz de `Documentacion/`, `Formatos
de entrevista/` y OneDrive) más un `Mini QAW FlorLogic.xlsx` distinto. **La vigente es la de
`Documentacion/`.** Y `PREGUNTAS_CARACTERIZACION.md`/`.xlsx` siguen al 0% respondido, pero son lo
único que conserva los identificadores `CNF-nn` y la columna `Bloque`: **portar esos IDs antes de
archivarlas.**

**Cómo se construyó el Top 65** — nota metodológica del propio archivo:

> Cada rol —Tomador de datos, Producción, Administrador— distribuyó **65 puntos entre 29 preguntas**
> (2×5 + 3×4 + 6×3 + 7×2 + 11×1), priorizando según la importancia de cada atributo de calidad para
> ese rol (hoja `2. Priorización-QA`) y el consenso de necesidad (SÍ) en la hoja `3. Caracterización`.
> Estas son las 65 preguntas con mayor puntaje total sumando los tres roles; **en caso de empate se
> prefirió el atributo de calidad de mayor prioridad promedio** entre los tres roles y, en segundo
> lugar, el mayor consenso (SÍ) entre Juan, Jerónimo y el Cliente.

---

## 8 · Las preguntas priorizadas — Top 65

Fuente: hoja `Top 65 - Priorizadas` del mini QAW. **Son la base directa de los escenarios de
calidad**: cada una es una necesidad ya consensuada, ya atribuida a un atributo y ya ponderada por
los tres roles.

**Cómo leer la votación:** cada rol repartió 65 puntos; **5 es lo máximo que un rol podía dar a una
pregunta**. Un 5 de un solo rol significa «para este rol esto es innegociable»; un 1-1-1 significa
«a los tres les importa un poco».

**Reparto por atributo:**

| Atributo | Preguntas | Puntos | Puesto en el ranking |
|---|:--:|:--:|:--:|
| Confiabilidad | 14 | **47** | 1 |
| Capacidad para ser Administrado | 8 | **25** | 6 |
| Capacidad para ser Auditado | 7 | **22** | 4 |
| Rendimiento | 7 | **18** | 3 |
| Disponibilidad | 7 | **16** | 2 |
| Experiencia de Usuario | 7 | **16** | 7 |
| Capacidad | 5 | **15** | 5 |
| Capacidad para ser Soportado | 3 | **10** | 11 |
| Seguridad | 3 | **7** | 8 |
| Portabilidad | 2 | **6** | 12 |
| Interoperatividad | 1 | **2** | 9 |
| Escalabilidad | 1 | **2** | 10 |
| **Total** | **65** | **186** | |

**Tres cosas que hay que leer en esa tabla antes de escribir un solo escenario:**

1. **Confiabilidad se lleva 14 preguntas y 47 puntos: una cuarta parte de todo.** Es coherente con
   que sea 1 para los tres actores y con la frase de cierre del cliente. **Los escenarios van a
   estar desbalanceados hacia Confiabilidad, y está bien que lo estén.**
2. `[!]` **Capacidad para ser Administrado es el segundo atributo por puntos (25) y es el puesto 6
   del ranking.** El desajuste tiene explicación: es el atributo del **Administrador del sistema**,
   que es el rol con menos preguntas compartidas — casi todo su presupuesto de puntos cae en
   preguntas que solo él vota. **Conviene mirarlo antes de asumir que el ranking y esta tabla dicen
   lo mismo.**
3. `[!]` **Accesibilidad no aporta ni una sola pregunta al Top 65**, y es el atributo 13. Coherente
   con el ranking, pero conviene recordar `A18`: el cliente respondió SÍ a casi todo lo que
   preguntaba por accesibilidad — lo que quedó fuera es el **término**, no necesariamente la
   necesidad.

### 8.1 Las 65, en orden de puntaje

**TD** = Tomador de datos · **PR** = Producción · **AD** = Administrador.

| # | Atributo | Pregunta | TD | PR | AD | Total |
|:--:|---|---|:--:|:--:|:--:|:--:|
| 1 | Confiabilidad | ¿Se necesita que no se pierda ningún dato capturado bajo ninguna circunstancia? | 5 | 5 | 5 | **15** |
| 2 | Capacidad | ¿Se necesita prever un crecimiento del volumen de información año tras año sin cambiar de sistema? | 0 | 3 | 3 | **6** |
| 3 | Capacidad para ser Administrado | ¿Se necesita poder crear y dar de baja usuarios sin solicitarlo a quien desarrolló el sistema? | 0 | 1 | 5 | **6** |
| 4 | Confiabilidad | ¿Se necesita saber, sobre cada proyección, con qué datos y con qué parámetros fue calculada? | 0 | 5 | 0 | **5** |
| 5 | Disponibilidad | ¿Se necesita capturar información en el área de cultivo sin ninguna conexión de datos? | 5 | 0 | 0 | **5** |
| 6 | Capacidad para ser Auditado | ¿Se necesita saber quién capturó cada dato? | 0 | 4 | 1 | **5** |
| 7 | Confiabilidad | ¿Se necesita que el sistema muestre cuánto se desvió la proyección anterior frente a lo que realmente se cortó? | 0 | 4 | 0 | **4** |
| 8 | Confiabilidad | ¿Se necesita que la información se conserve cuando el dispositivo se moja o se dañe y hay que cambiarlo? | 4 | 0 | 0 | **4** |
| 9 | Confiabilidad | ¿Se necesita que una sincronización interrumpida a la mitad pueda retomarse sin duplicar ni perder información? | 3 | 1 | 0 | **4** |
| 10 | Rendimiento | ¿Se necesita que muchos dispositivos puedan sincronizar al mismo tiempo al terminar la jornada sin que el sistema se degrade? | 0 | 3 | 1 | **4** |
| 11 | Capacidad para ser Auditado | ¿Se necesita que el registro de lo ocurrido no pueda ser alterado ni borrado por nadie? | 0 | 0 | 4 | **4** |
| 12 | Capacidad | ¿Se necesita que el sistema soporte varias fincas de la misma empresa? | 0 | 4 | 0 | **4** |
| 13 | Capacidad para ser Administrado | ¿Se necesita poder cambiar los permisos de un usuario de manera inmediata? | 0 | 0 | 4 | **4** |
| 14 | Experiencia de Usuario | ¿Se necesita reducir al mínimo la cantidad de toques necesarios para registrar una cama? | 4 | 0 | 0 | **4** |
| 15 | Seguridad | ¿Se necesita que la información de una empresa nunca pueda ser vista desde otra empresa, bajo ninguna circunstancia? | 0 | 1 | 3 | **4** |
| 16 | Capacidad para ser Soportado | ¿Se necesita poder diagnosticar un problema sin tener que desplazarse hasta la finca? | 0 | 0 | 4 | **4** |
| 17 | Portabilidad | ¿Se necesita que la aplicación de captura funcione en dispositivos Android? | 4 | 0 | 0 | **4** |
| 18 | Confiabilidad | ¿Se necesita que la información se conserve cuando la aplicación se cierra de manera inesperada? | 3 | 0 | 0 | **3** |
| 19 | Disponibilidad | ¿Se necesita que una falla que afecte a una finca no afecte a las demás fincas o empresas? | 1 | 1 | 1 | **3** |
| 20 | Rendimiento | ¿Se necesita que cada dato quede guardado de inmediato, sin espera perceptible? | 3 | 0 | 0 | **3** |
| 21 | Rendimiento | ¿Se necesita que la sincronización ocurra en segundo plano, sin impedir seguir capturando? | 3 | 0 | 0 | **3** |
| 22 | Rendimiento | ¿Se necesita que la proyección se recalcule en el momento en que llega información nueva? | 0 | 3 | 0 | **3** |
| 23 | Capacidad para ser Auditado | ¿Se necesita poder ver la historia completa de una cama desde la siembra hasta la erradicación? | 0 | 3 | 0 | **3** |
| 24 | Capacidad para ser Auditado | ¿Se necesita registrar el motivo cada vez que disminuye la cantidad de plantas o de tallos esperados? | 0 | 3 | 0 | **3** |
| 25 | Capacidad para ser Auditado | ¿Se necesita generar el informe para una auditoría desde el propio sistema, sin ayuda técnica? | 0 | 1 | 2 | **3** |
| 26 | Capacidad | ¿Se necesita conservar en línea la información de los últimos cinco años? | 0 | 3 | 0 | **3** |
| 27 | Capacidad para ser Administrado | ¿Se necesita poder cambiar la densidad de siembra o los parámetros de cálculo sin desarrollo adicional? | 0 | 0 | 3 | **3** |
| 28 | Capacidad para ser Administrado | ¿Se necesita que un cambio de parámetros no altere las proyecciones ya emitidas? | 0 | 2 | 1 | **3** |
| 29 | Capacidad para ser Administrado | ¿Se necesita poder forzar la sincronización de un dispositivo de manera remota? | 0 | 0 | 3 | **3** |
| 30 | Experiencia de Usuario | ¿Se necesita que la pantalla sea legible bajo el sol directo? | 3 | 0 | 0 | **3** |
| 31 | Experiencia de Usuario | ¿Se necesita poder identificar la cama escaneando una marca física en lugar de escribir el código? | 3 | 0 | 0 | **3** |
| 32 | Capacidad para ser Soportado | ¿Se necesita que exista alguien dentro de la finca capaz de resolver los problemas del día a día? | 0 | 0 | 3 | **3** |
| 33 | Capacidad para ser Soportado | ¿Se necesita poder revisar el estado de un dispositivo de forma remota? | 0 | 0 | 3 | **3** |
| 34 | Confiabilidad | ¿Se necesita que el sistema avise cuando se intenta capturar dos veces la misma cama el mismo día? | 2 | 0 | 0 | **2** |
| 35 | Confiabilidad | ¿Se necesita realizar la captura de datos con una plantilla predefinida que use una estructura similar a las plantillas de papel utilizadas previamente? | 2 | 0 | 0 | **2** |
| 36 | Confiabilidad | ¿Se necesita poder corregir un dato ya capturado antes de que se sincronice? | 2 | 0 | 0 | **2** |
| 37 | Confiabilidad | ¿Se necesita que el sistema haga copias de respaldo de manera automática, sin que nadie lo solicite? | 0 | 0 | 2 | **2** |
| 38 | Disponibilidad | ¿Se necesita poder consultar la información desde la web aunque la aplicación de captura esté fuera de servicio? | 0 | 2 | 0 | **2** |
| 39 | Disponibilidad | ¿Se necesita que exista un periodo del año (temporada alta) con una exigencia de disponibilidad mayor que el resto? | 0 | 2 | 0 | **2** |
| 40 | Disponibilidad | ¿Se necesita poder continuar la captura en otro dispositivo cuando el que se estaba usando deja de funcionar? | 2 | 0 | 0 | **2** |
| 41 | Rendimiento | ¿Se necesita que la información sincronizada se vea reflejada en la consulta web dentro de la misma hora? | 0 | 2 | 0 | **2** |
| 42 | Capacidad para ser Auditado | ¿Se necesita poder ver la historia completa de una variedad a través de varios ciclos? | 0 | 2 | 0 | **2** |
| 43 | Capacidad para ser Auditado | ¿Se necesita poder llegar, desde una cifra de un reporte, hasta los datos individuales que la componen? | 0 | 2 | 0 | **2** |
| 44 | Rendimiento | ¿Se necesita que la aplicación no agote la batería del dispositivo antes de terminar la jornada? | 2 | 0 | 0 | **2** |
| 45 | Capacidad para ser Administrado | ¿Se necesita poder actualizar la aplicación en todos los dispositivos sin recogerlos uno por uno? | 0 | 0 | 2 | **2** |
| 46 | Capacidad para ser Administrado | ¿Se necesita que la administración del sistema se pueda hacer desde un computador de la finca, sin herramientas técnicas especiales? | 0 | 0 | 2 | **2** |
| 47 | Capacidad para ser Administrado | ¿Se necesita que quien administra el sistema en la finca no pueda modificar la información de producción? | 0 | 0 | 2 | **2** |
| 48 | Experiencia de Usuario | ¿Se necesita poder deshacer el último dato registrado sin salir de la pantalla? | 2 | 0 | 0 | **2** |
| 49 | Experiencia de Usuario | ¿Se necesita que la consulta de resultados se pueda hacer desde un celular y no solo desde un computador? | 0 | 2 | 0 | **2** |
| 50 | Seguridad | ¿Se necesita que quien opera la plataforma no pueda leer la información de producción de la empresa? | 0 | 0 | 2 | **2** |
| 51 | Interoperatividad | ¿Se necesita poder exportar la información a Excel? | 1 | 1 | 0 | **2** |
| 52 | Escalabilidad | ¿Se necesita poder agregar una empresa nueva sin afectar a las que ya están funcionando? | 0 | 0 | 2 | **2** |
| 53 | Portabilidad | ¿Se necesita que la aplicación de captura funcione también en dispositivos Apple? | 2 | 0 | 0 | **2** |
| 54 | Confiabilidad | ¿Se necesita verificar que la información sea correcta de manera que se agrupe la información por cama? | 1 | 0 | 0 | **1** |
| 55 | Confiabilidad | ¿Se necesita que el sistema rechace un dato cuando está fuera del rango posible (por ejemplo, más tallos que plantas sembradas)? | 1 | 0 | 0 | **1** |
| 56 | Confiabilidad | ¿Se necesita que el sistema bloquee la captura cuando detecta que la fecha y la hora del dispositivo fueron alteradas? | 1 | 0 | 0 | **1** |
| 57 | Confiabilidad | ¿Se necesita que el sistema permita comparar lo capturado en la aplicación contra el formato en papel del mismo día? | 0 | 1 | 0 | **1** |
| 58 | Disponibilidad | ¿Se necesita que la captura siga funcionando aunque el servicio en la nube esté caído? | 1 | 0 | 0 | **1** |
| 59 | Disponibilidad | ¿Se necesita que el sistema avise cuando un dispositivo lleva demasiado tiempo sin sincronizar? | 1 | 0 | 0 | **1** |
| 60 | Rendimiento | ¿Se necesita que el rendimiento se mantenga igual en temporada alta que en el resto del año? | 0 | 1 | 0 | **1** |
| 61 | Capacidad | ¿Se necesita que el dispositivo pueda guardar la información de varios días sin sincronizar sin quedarse sin espacio? | 0 | 1 | 0 | **1** |
| 62 | Capacidad | ¿Se necesita que el costo de almacenamiento por finca se mantenga acotado a medida que crece la historia? | 0 | 0 | 1 | **1** |
| 63 | Experiencia de Usuario | ¿Se necesita que los nombres que aparecen en pantalla sean exactamente los que se usan hablando en la finca? | 0 | 1 | 0 | **1** |
| 64 | Experiencia de Usuario | ¿Se necesita que los mensajes de error expliquen qué hacer y no solo qué salió mal? | 1 | 0 | 0 | **1** |
| 65 | Seguridad | ¿Se necesita restringir quién puede exportar información fuera del sistema? | 0 | 0 | 1 | **1** |

### 8.2 Las nueve que quedaron justo fuera del corte — **hay que mirarlas**

Los tres roles repartieron **195 puntos** (65 cada uno) sobre **74 preguntas**; el Top 65 recoge
**186 puntos y 65 preguntas**. Las **nueve** que quedaron fuera tenían 1 punto cada una, y **dos de
ellas son justamente las que acabaron cambiando el proyecto entero**:

| Atributo | Pregunta | Por qué importa |
|---|---|---|
| Interoperatividad | ¿Se necesita que la información se pueda leer desde una herramienta de análisis externa? | **Es la del «POWER BI».** Derogó `DEC-06`, `CN-10` y `CN-33` (`B5`) |
| Portabilidad | ¿Se necesita que el sistema pueda instalarse en servidores de la propia empresa y no solo en la nube? | **Es la que derogó `DEC-01`** y trajo el modelo local-first (`B6`, `A20`) |
| Escalabilidad | ¿Duplicar la cantidad de personas que capturan sin que el sistema se vuelva lento? | Bloque de 11 SÍ de 11 |
| Escalabilidad | ¿Soportar el aumento de la temporada alta sin degradación perceptible? | Alimenta `CN-30` |
| Cap. para ser Soportado | ¿Soporte en horario extendido durante la temporada alta? | Toca `CN-01` y la mensualidad de `E2` |
| Cap. para ser Soportado | ¿Que la puesta en marcha no detenga la operación más de una semana? | **Es `CN-07` dicha por el cliente** |
| Portabilidad | ¿Que la aplicación funcione en tabletas además de en celulares? | Toca `CN-21` |
| Accesibilidad | ¿Que lo pueda usar una persona con poca experiencia en el manejo de tecnología? | Es la premisa del asistente de captura (`C2`) |
| Accesibilidad | ¿Botones lo bastante grandes para acertarles sin precisión fina? | Ergonomía de campo (`B3`) |

> `[!]` **La ponderación por puntos no es lo mismo que la importancia arquitectónica.** Dos de las
> decisiones más grandes de todo el proyecto salieron de preguntas que sacaron **un punto** y se
> quedaron fuera del corte. **El Top 65 sirve para ordenar el trabajo de escenarios, no para decidir
> qué es un driver.** Cuando un escenario necesite una necesidad que no aparezca en el corte, se
> busca en las 262 de la hoja `3. Caracterización`.

---

## 9 · Las medidas disponibles hoy — materia prima de los escenarios

**Un escenario sin medida de respuesta no es un escenario.** Esto es todo el número que hay en el
proyecto, reunido. Lo que no está aquí, **se marca `PENDIENTE` en el escenario; no se inventa**.

### 9.1 Números firmes

| Medida | Valor | De dónde sale |
|---|---|---|
| **Pérdida de información** | **CERO** | `CN-15` · `DEC-12` |
| **Fallo de funcionamiento no planificado** | **máx. 1 hora** | `CN-15` · `A2` · el cliente en S2: aguantan ~1 hora, 4 horas es demasiado |
| **Reparación o restauración de respaldo** | **máx. 1 día** | `CN-15` |
| **Ventana de mantenimiento con parada** | **mayo de cada año** — temporada baja | `A2` ronda 2 |
| **Búsqueda rápida de información** | **2 años**; más allá, demora escalonada y proporcional a la antigüedad | `A3` ronda 2 |
| **Latencia de captura a proyección — línea base actual** | **8 días**, con 4 h/semana de digitación por medio | `B2` · `H-25` · `H-26` |
| **Concurrencia de sincronización** | **hasta 10 personas por instalación**, y no crece con facilidad. **La degradación bajo carga es aceptable** | `A10` |
| **Pico de temporada** | **+60%** en tallos y registros · **+30–40%** en personal | `CN-30` · `H-41` |
| **Ventana sin sincronizar** | **≥ 15 días**, incluida captura retroactiva de papeles que aparecen tarde | `B1` · `A6` |
| **Banda de la proyección** | **±10% en tallos** — se siembran 1.000, se cuenta con ~900, el resultado cae entre 810 y 990 | `A19` |
| **Error de captura actual** | **2%**, y es **error de transcripción de papel a sistema** | `C8` · `H-27` |
| **Regeneración de la proyección** | **al menos semanal**, conservando la versión anterior | `RF-008` |
| **Entrega e implementación** | **mayo de 2027** | `CN-01` |
| **Despliegue e instrucción** | **≤ 7 días** sin retrasar la operación | `CN-07` |
| **Presupuesto de construcción** | **~20.000 USD** por instalación | `CN-02` · `E4` |
| **Mensualidad de servicios** | **100–200 USD/mes** — cifra de partida, sin costear | `E2` |
| **Escala física** | 15 ha · 25 bloques · ~1.525 camas · 3 capturadores · ~12 usuarios · ~20 consultores | `H-01`..`H-03`, `H-29`, `H-30` |
| **Densidad de siembra** | ejemplos reales: **90 y 60 plantas/m²**, definidas por el agrónomo según variedad | `H-10` |

### 9.2 La medida que el cliente dio y que hay que traducir con cuidado

> **«NO PUEDE HABER ERRORES»** — respuesta a la pregunta de fijar un porcentaje máximo de error.

No es un número: es un rechazo a la pregunta. **Y `C8` explica por qué**: para el cliente el 2% no
es algo que vigilar, sino algo que **el sistema hace desaparecer** — sin transcripción manual no hay
error de transcripción. Por eso rechazó todos los instrumentos para medirlo. **El escenario de
Confiabilidad no debe escribirse como «el error se mantiene por debajo de X%»**, sino sobre lo que
sí tiene medida: **pérdida cero**, rechazo en el dispositivo (`RF-004`, `RF-005`) y la desviación de
`RF-011`.

Y el **«24×7»** del cliente tampoco es un objetivo de disponibilidad anual: `A2` lo tradujo a
**«captura y sincronización activas durante todo el día»**, con parada anunciada y concentrada en
mayo. **Escribirlo como 99,99% sería inventarse el requisito.**

---

## 10 · Lo que todavía bloquea los escenarios

### 10.1 Estado de medida de los cinco drivers

Cuando se ancló el ranking, **los cinco drivers tenían un problema de medida**. Las rondas 2 a 5
resolvieron tres:

| # | Driver | Estado de su medida | Entrada |
|:--:|---|---|---|
| 1 | **Confiabilidad** | [:P:] **Parcial.** Meta = 0 errores, pero el cliente rechazó los instrumentos. `C8` explica por qué y `CN-15` aporta la medida utilizable: **pérdida cero** | `A14`, `C8`, `CN-15` |
| 2 | **Disponibilidad** | [:OK:] **Resuelta.** 1 hora de fallo no planificado · mantenimiento en mayo · «24×7» = jornada completa activa | `A2` |
| 3 | **Rendimiento** | [:P:] **Tiene métrica, le falta el objetivo.** Ya no se mide en segundos por cama: **se mide en latencia de captura a proyección**, con línea base de 8 días. `[!]` **Falta el número objetivo** | `B2` |
| 4 | **Capacidad para ser Auditado** | [:DD:] **Sin medida, y con la mitad amputada.** La unidad es la **sesión de sincronización**; no hay marcas de tiempo por dato ni valor anterior. Retención **«DE POR VIDA»** | `A1`, `B8`, `A12` |
| 5 | **Capacidad** | [:OK:] **Resuelta.** 2 años de búsqueda rápida, después demora escalonada | `A3` |

> **Los dos que faltan son el número de latencia de `B2` y la forma de medir la auditabilidad
> de `A1`.** Los dos se preguntan al cliente; ninguno lo decide el equipo solo.

### 10.2 Las decisiones tomadas **en contra** del cliente

**Hay que llevárselas a la sesión, no aplicarlas en silencio.** Y varias de ellas van a aparecer
dentro de escenarios, así que conviene tenerlas identificadas antes de escribirlos.

| ID | Qué se decidió contra su respuesta |
|---|---|
| `A5` | **Se cifra la información en el dispositivo**, aunque dijo que no. Motivo: es casi gratis y `CN-03` lo respalda |
| `A8` | **Dentro de una empresa todo es visible para todos**, con filtros por panel |
| `A10` | **La sincronización puede degradarse** bajo carga — él lo asume como realista |
| `A11` | **Sí hay cierre de periodo** y fecha desde la cual no se corrige libremente. Motivo legal |
| `B12` | **Se construyen dos tableros** —qué está sin sincronizar y avance del día por bloque— aunque dijo que no. **La idea fue suya en S1** y son el instrumento de la medida de `B2` |
| `B13` | **Habrá una guía corta**, aunque pidió no tener manual. Lo mínimo para que la operación no se caiga |
| `C2` | **Entra el asistente de captura por IA**, entrenado en el entorno del cliente |

`[!]` **Dos que estaban en esta lista y salieron, y valen como aviso de método:** `B7` —*«NO DEJA
INGRESAR EL ÚLTIMO REGISTRO»* significaba *«el más viejo»*, lo leímos al revés— y `B11` —el NO a la
vista geométrica era **sobre la captura**, no sobre la consulta—.
**Las notas literales de la caracterización hay que confirmarlas con el cliente, no interpretarlas.**

### 10.3 Lo que sigue abierto y puede tumbar un escenario

**Del cliente — las 18 entradas del grupo `D`.** Todas necesitan sesión con él; le corresponden a
Jerónimo. Las tres que mandan:

| ID | Qué falta |
|---|---|
| **`D1`** | **El proceso de captura nunca se trabajó con el cliente.** Pide una sesión, no un dato. Bloquea el catálogo de reglas duras de `CN-22` |
| **`D2`** | **Los documentos prometidos no han llegado:** plan de siembra, presupuestos, formatos llenos, tabla de grados, histórico |
| **`D3`** | **De dónde sale el % de productividad por variedad** y **cómo se reparten los tallos en los ~7 días de corte**. Bloquea `RF-006` y `RF-007` |

**Del equipo — tres cabos sueltos con la entrada ya cerrada:**

1. **Dónde corre el modelo de IA** (`C2`): nodo local de la finca —hace falta hardware y encarece la
   instalación— o el propio dispositivo —modelo pequeño y menos capaz—. **Afecta al precio de
   instalación.**
2. **Recalcular los 100–200 USD/mes** antes de firmar, con quince instalaciones (`E2`).
3. **Qué pasa con una instalación que deja de pagar** (`E2`): sin actualizaciones **diverge de
   versión**, que es lo que teme `CN-29`.

**Y dos restricciones técnicas todavía EN DUDA que pueden mover escenarios enteros:** `CN-20` —el
sistema heredado de ~300 tablas, **bloqueante y solo el cliente lo cierra**— y `CN-21` —qué
dispositivo se usa, que fija el piso técnico de la captura offline—.

### 10.4 Formato acordado para los escenarios

**Dos columnas y nada más: `ID | Escenario`.** El escenario se escribe como **párrafo narrativo
continuo** que contiene los seis elementos de Bass, Clements y Kazman: **entorno · fuente del
estímulo · estímulo · artefacto · respuesta · medida de respuesta**. IDs `ESC-001`, `ESC-002`, …,
**agrupados por atributo de calidad, no por requisito**.

Son **ejemplos de uso de la vida real** y del comportamiento esperado, no el contrato final de
aceptación. [:DD:] **El esqueleto `ESC-01`..`ESC-08` de `5_RF_CRITICOS_v1.xlsx` queda descartado**: no se
usa ni su contenido ni su numeración.

**Estado: no existe ningún escenario todavía.** La hoja `4, Lluvia-de-Escenarios` está vacía.

---

## 11 · De dónde sale cada cosa

| Sección | Fuente primaria |
|---|---|
| `§1` marco y modelo de entrega | `Recopilacion/3_DECISIONES...md` §0.1 · `CONTEXTO.md` §1, §3 |
| `§2` funcionalidades significativas | `Documentacion/FuncionalidadesSignificativas.xlsx`, hoja `FuncionalidadesSignificativas` + rondas 1-5 del documento de decisiones |
| `§3` restricciones de negocio | `Documentacion/RestriccionesNegocio.xlsx` |
| `§4` restricciones técnicas | `Documentacion/RestriccionesTecnicas-IA.xlsx` |
| `§5` `§6` `§7` atributos, votaciones y mini QAW | `Documentacion/MINI QAW PLANTILLA NO TERMINADA.xlsx`, hojas `1. Trade-Off-QA` y `2. Priorización-QA` |
| `§8` Top 65 | misma hoja `Top 65 - Priorizadas` — **tabla generada desde el archivo, no transcrita a mano** |
| `§9` medidas | `CN-15`, y las entradas `A2`, `A3`, `A10`, `A19`, `B1`, `B2`, `C8` del documento de decisiones |
| `§10` lo abierto | `Recopilacion/3_DECISIONES...md` grupos `D` y `E` · `CONTEXTO.md` §9.3, §13 |

`[!]` **Advertencia de método que aplica a todo el documento.** Las transcripciones de las sesiones
**no distinguen quién habla** —un único GUID por `.vtt`— y todo el conocimiento del negocio descansa
en **una sola voz**, la del director de producción. **Planeación nunca se exploró.**

---

*Documento acumulador de drivers. Última actualización: 26-ago-2026.*
