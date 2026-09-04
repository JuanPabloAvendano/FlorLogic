# FlorLogic — Drivers arquitectónicos

> **v2.1 · 4-sep-2026 · Documento completo.**
> Reúne en un solo sitio **todo lo que entra en la categoría de driver arquitectónico**:
> funcionalidades significativas, restricciones de negocio, restricciones técnicas, atributos de
> calidad, votaciones, mini QAW, preguntas de caracterización priorizadas **y los 65 escenarios de
> calidad `ESC-01`..`ESC-65`**.
>
> **Qué cambió frente a la v1.0 (26-ago).** Aquella versión decía que los escenarios eran «el paso
> siguiente» y que no existía ninguno. **Ya están escritos los 65 y están en `§9`.** Con eso el
> documento deja de ser solo el insumo y pasa a ser el entregable cerrado de la fase de drivers.
>
> **Qué cambió en la v2.1 (4-sep).** Nada de fondo: se cerró la limpieza. La carpeta quedó con
> **los cuatro Excel y este documento, y nada más**; `FuncionalidadesSignificativas.xlsx` pasó a
> llevar **la misma redacción vigente que se lee en `§2`** —antes conservaba la del 13-ago y eso
> hacía que el Excel y el documento dijeran cosas distintas— y se le quitó una hoja `HU` de un
> proyecto ajeno que venía arrastrada de la plantilla de clase. **Ningún identificador cambió,
> ninguna fila se borró** y las versiones anteriores siguen en el histórico del repositorio.

---

## 0 · Cómo leer y mantener este archivo

### 0.1 Qué hay en esta carpeta

Esta carpeta contiene **los cuatro artefactos de drivers y este documento, y nada más**. Los cuatro
Excel son la fuente; este archivo explica y recoge su contenido para poder leerlo de corrido.

| Archivo | Qué contiene | Dónde se explica aquí |
|---|---|---|
| `FuncionalidadesSignificativas.xlsx` | Las 21 funcionalidades significativas `RF-nnn` | `§2` |
| `RestriccionesNegocio.xlsx` | Las 9 restricciones de negocio `CN-01`..`CN-09` | `§3` |
| `RestriccionesTecnicas.xlsx` | Las 29 restricciones técnicas `CN-10`..`CN-38` | `§4` |
| `EscenariosCalidad.xlsx` | El mini QAW completo y los 65 escenarios `ESC-01`..`ESC-65` | `§5` a `§9` |
| `DRIVERS_ARQUITECTONICOS.md` | Este documento | — |

`EscenariosCalidad.xlsx` trae cinco hojas y es autosuficiente: `1. Trade-Off-QA`,
`2. Priorización-QA`, `3. Caracterización`, `4, Lluvia-de-Escenarios` y `Top 65 - Priorizadas`.
**Sustituye a todas las copias sueltas del mini QAW**: las que estaban duplicadas y con la hoja de
escenarios vacía se eliminaron, y las versiones de trabajo anteriores quedaron guardadas en
`Documentacion/Archivo/Mini-QAW-versiones-anteriores/`. Ninguna de ellas se cita como fuente.

El material que antes acompañaba a estos archivos —entrevistas, transcripciones, recopilación de
decisiones, modelos, diagramas y notas de trabajo— está en `Documentacion/Archivo/`. **No hace falta
para leer este documento**; hace falta para verificar una afirmación concreta.

### 0.2 Qué manda sobre qué

Para el estado de una decisión manda
`Documentacion/Archivo/Recopilacion/3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md` (v7.0). Para la
voz literal del cliente manda `Documentacion/Archivo/Recopilacion/1_VOZ_DEL_CLIENTE.md`. **Este
archivo no decide nada nuevo:** recoge lo ya decidido y lo pone en forma de driver.

### 0.3 Reglas y marcas

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
| `[:PP:]` | Parcial — tiene parte de la respuesta, le falta la otra |
| `[:R:]` | Reinterpretado o transformado: sigue vivo, pero significa otra cosa |
| **PROPUESTA** | Todavía no aprobado por el equipo |

**Sobre las reescrituras.** Las funcionalidades significativas marcadas [:W:] llevan aquí su
**redacción nueva como texto vigente**, y `FuncionalidadesSignificativas.xlsx` lleva **esa misma
redacción**: el Excel y este documento dicen hoy lo mismo, palabra por palabra. La redacción
anterior, la del 13-ago-2026, sigue existiendo en el histórico del repositorio y en la copia
guardada en `_to_delete/plantilla-clase/`. `§2.5` lista qué se reescribió y por qué decisión.

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

Fuente: `FuncionalidadesSignificativas.xlsx`, hoja `FuncionalidadesSignificativas`. El archivo
partió del levantamiento del 13-ago-2026 y **quedó actualizado con las decisiones de las rondas 1 a
5 del 25-ago-2026**, con la misma redacción que se lee en este apartado. Cada fila del Excel trae
además **de qué decisión sale** y en qué estado quedó, igual que `RestriccionesTecnicas.xlsx`.

**Tipo** — clasificación del propio archivo: `Valor de negocio` · `Reto técnico` · `Ambos`.
**Estado** — `Vigente` si la redacción del 13-ago sigue en pie, `Reescrito` si cambió en las rondas,
`Renumerado` si solo se corrigió el identificador.

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
| `RF-016` | Traza atada al ciclo de producción |[:W:]| **Mientras una producción está abierta**, el sistema debe conservar todas las modificaciones de los campos de sus camas o secciones, de modo que una corrección se pueda **devolver** al valor anterior o impedirse antes de entrar. **Cuando el administrador cierra la producción** como actividad productiva terminada —sembrada, cortada, erradicada, o cualquier ciclo que haya acabado con un producto—, el estado se consolida en **el último valor conocido por campo con la fecha en que se capturó**, y las correcciones intermedias dejan de mantenerse en línea. En todo momento el sistema debe conservar, **por sesión de sincronización**, quién sincronizó, desde qué dispositivo, qué camas y qué cifras entraron. **Al capturador no se le exige motivo escrito ni autorización para corregir.**| Corregir es frecuente y no puede costarle nada al que captura, pero mientras la producción se está formando hay que poder volver atrás. Cerrada la producción, lo que importa es el resultado: es lo que el cliente pidió con **«SOLO LA CORREGIDA»**, y ata el límite de retención a un hecho del negocio en vez de a una cifra discutida.| [:W:] **Reescrito por `B8`, `A15` y `A1`, y cerrado por la decisión del 4-sep-2026** que ata la retención al ciclo de producción. `B8`: el cliente dijo **«SOLO LA CORREGIDA»**, y para él corregir sin fricción es un requisito, porque con este volumen se corrige varias veces al día. `A15`: el estado es el último valor de cada campo con su fecha — si el 24 se capturó `x, y, z` y el 25 solo `x, y`, el sistema muestra **`x, y` (25) · `z` (24)**. `A1`: la unidad de trazabilidad es la **sesión**, no el dato. `A11` aporta el cierre de periodo, que es el mismo mecanismo. `[!]` **Falta confirmarle al cliente** si las correcciones de una producción cerrada se **archivan** fuera de línea o se **purgan**: dijo que quiere responder auditorías de certificación, y eso pide archivar.|
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

Fuente: `RestriccionesNegocio.xlsx` (13-ago-2026), 9 filas.
Tipos: Tiempo · Presupuesto · Legal · Proceso · Humano.

`[!]` **El archivo no lleva los identificadores `CN` escritos**: la numeración vive en el modelo
ArchiMate y aquí. `[!]` **La columna «Plan acción» está vacía en las nueve filas** —en el archivo de
restricciones técnicas sí está llena—. **Son los dos únicos arreglos pendientes en este archivo**, y
los dos son de forma, no de contenido.

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

## 4 · Restricciones técnicas `CN-10` … `CN-38`

Fuente: `RestriccionesTecnicas.xlsx`, 29 filas.
**Serie única y continua con las de negocio.** No existe serie `RT-nn`. Los IDs no se reciclan.

Tipos: **impuesta** (9) — viene de fuera y no se negocia — y **adoptada** (20) — la elige el equipo.

`[:W:]` **El archivo se reescribió el 2-sep-2026.** Se le quitó el sufijo `-IA` del nombre y se
redactó de nuevo en el mismo tono llano del archivo de restricciones de negocio. Además se pusieron
al día los estados que las rondas 1 a 5 habían movido —`CN-10` revertida, `CN-33` cerrada, `CN-28`
resuelta— y se registraron las tres restricciones nuevas `CN-36`, `CN-37` y `CN-38`, que hasta ahora
solo vivían en este documento. **Ningún identificador cambió y ninguna fila se borró.**

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
| **`CN-28`** | [:W:] **CERRADA:** cifrado en tránsito y en reposo, respaldos incluidos, **con la llave del lado del cliente**. El equipo conserva además una **copia de custodia de cada llave, fuera de línea y en soporte físico**, que solo se abre ante una excepción declarada —pérdida del nodo de la finca— con doble control, registro y aviso al administrador de la empresa. Sin ella, perder el servidor de la finca significaría perder también la capacidad de abrir el respaldo | [:OK:] **sale de EN DUDA** por `B4`; la custodia la cierra Juan el 4-sep |
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

### 4.3 Restricciones nuevas — `CN-36` … `CN-38` · **PROPUESTA**

Salen de las rondas 3 a 5. **Desde el 2-sep-2026 están registradas en `RestriccionesTecnicas.xlsx`**,
con estado `EN DUDA` porque siguen sin aprobarse formalmente. Llevan IDs nuevos de la serie continua,
sin reciclar ninguno.

| ID | Restricción propuesta | Tipo | De dónde sale |
|---|---|---|---|
| **`CN-36`** | **Los campos capturados son DATOS, no COLUMNAS.** Añadir un tipo de labor, de medición o de agrupación **no puede modificar lo previo ni obligar a rehacer la captura existente**, y no puede exigir una migración de esquema | adoptada | `C4` + `C6` (ronda 5) |
| **`CN-37`** | **Entrega local-first:** el sistema se instala en la infraestructura de cada empresa y opera **sin internet** sobre su información activa; la nube presta respaldo, sincronización, actualización e IA | impuesta | `B6`, `A10`, `A20` |
| **`CN-38`** | **La verificación de cumplimiento no se automatiza.** No se construye ninguna vía de auditoría para terceros distinta de la que ya tienen los administradores; lo que se exige es que la información exportable sea **suficiente y válida** para que una persona haga esa verificación a mano | impuesta | `A1`, `A13` |

> `[!]` **`CN-36` es la restricción de arquitectura más importante que sale de todo el trabajo de
> depuración.** Con N instalaciones desplegadas dentro de casa de clientes, un cambio de esquema es
> lo más caro que le puede pasar al modelo de entrega. **Hay que fijarla antes de la primera tabla.**

### 4.4 Estado de las restricciones, de un vistazo

Sobre las **29** restricciones técnicas del archivo:

| Estado | Cuántas | Cuáles |
|---|:--:|---|
| **DENTRO** | 22 | Las 18 adoptadas que siguen en pie —`CN-28` incluida— más `CN-10`, `CN-17`, `CN-18` y `CN-19` |
| **EN DUDA** | 6 | `CN-11` · `CN-20` `[!]` **bloqueante, la responde el cliente** · `CN-21` · y las tres nuevas `CN-36`, `CN-37`, `CN-38`, que están sin aprobar |
| **CERRADO** | 1 | `CN-33`, derogada por `B5` y conservada para que no se reabra |
| **Reescritas** | 8 | `CN-10`, `CN-11`, `CN-14`, `CN-24`, `CN-25`, `CN-28`, `CN-30`, `CN-34` |

**`CN-28` salió de EN DUDA**: era una de las dos que había que cerrar primero, y la cerró `B4` con
la opción que bajo SaaS era imposible — **la llave la tiene el dispositivo del cliente**. La otra,
`CN-20`, sigue abierta y solo el cliente la cierra.

`[!]` **Pendiente de siempre:** sincronizar `CN-17`..`CN-38` al modelo
`docs/03-arquitectura/FlorLogic-C4.archimate` como elementos `Constraint`. *(La copia que había en
`Archivo/Modelo-y-construccion/` se erradicó el 4-sep-2026; el ejemplar bueno es el de `docs/`.)*
Hoy viven solo en el xlsx y en este documento.

---

## 5 · Atributos de calidad — los 13 vigentes

Fuente: `EscenariosCalidad.xlsx`, hojas `1. Trade-Off-QA` y `2. Priorización-QA`.
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

**Este ranking sustituye a los dos anteriores** (el ranking del contexto anterior y el mini QAW viejo), que eran
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

El mini QAW es el instrumento con el que se produjo todo lo del `§6`, el `§8` y el `§9`. Vive en
`EscenariosCalidad.xlsx` — **esa es la única copia, y está completa**.

| Hoja | Para qué sirve | Estado |
|---|---|---|
| `1. Trade-Off-QA` | Tres ordenamientos de los 13 atributos: cliente, arquitectos y «promediado» | [:OK:] Completa · `[!]` la tercera columna sin justificar |
| `2. Priorización-QA` | Reparto 1..13 por cada uno de los tres actores. **Es el ranking vigente** | [:OK:] Completa y cerrada |
| `3. Caracterización` | **262 preguntas** de necesidad, respondidas por Juan (260), Jerónimo (260) y **el Cliente (262)**, con 29 notas literales | [:OK:] Completa |
| `Top 65 - Priorizadas` | Las 65 preguntas con más puntaje, con la votación de los tres roles. **Es el insumo directo de los escenarios** | [:OK:] Completa |
| `4, Lluvia-de-Escenarios` | **Los 65 escenarios de calidad `ESC-01`..`ESC-65`**, cada uno con sus seis partes en columna y el párrafo narrativo armado | [:OK:] **Completa — ver `§9`** |

[:OK:] **La higiene del mini QAW quedó resuelta el 2-sep-2026.** Antes había cuatro libros dando
vueltas: `Mini QAW FlorLogic.xlsx`, `MINI QAW PLANTILLA NO TERMINADA.xlsx` (dos copias) y este. **Los
tres primeros tenían la hoja de escenarios vacía y se eliminaron**; sus otras cuatro hojas están
íntegras dentro de `EscenariosCalidad.xlsx`, que es autosuficiente. Las dos versiones de trabajo que
sí aportaban contenido —la lluvia de escenarios v1 y los escenarios documentados en su versión
larga— **no se borraron**: están en `Documentacion/Archivo/Mini-QAW-versiones-anteriores/`.

[:DD:] **Cerrado el 4-sep-2026.** El pendiente era portar los identificadores `CNF-nn` y la columna
`Bloque`: **ya estaban en el xlsx**, con las 262 filas y el texto de la v3. Así que la copia `.md` no
conservaba nada propio y se erradicó —`_to_delete/obsoletos-2026-09-04-b/`—, dejando el método de uso
en `Levantamiento de requisitos/Entrevistas/LEEME-caracterizacion.md`. **La fuente es el xlsx**, y
sigue al 0% respondido: es material para la sesión con el cliente.

[:DD:] **La lista de este apartado ya NO es fuente del Top 65 (decisión del 4-sep-2026).** El libro
`EscenariosCalidad.xlsx` rehízo su Top 65 después del 26-ago añadiendo como criterio *«evidencia
textual directa del cliente»*, y **21 de las 65 preguntas difieren**. Manda la del libro, por ser la
versión más actualizada guardada en el repositorio, y es contra la que están escritos los 65
escenarios. Lo que sigue se conserva porque explica **cómo se construyó** la priorización, no porque
la tabla sea la vigente.

**Cómo se construyó el Top 65** — nota metodológica del propio archivo:

> Cada rol —Tomador de datos, Producción, Administrador— distribuyó **65 puntos entre 29 preguntas**
> (2×5 + 3×4 + 6×3 + 7×2 + 11×1), priorizando según la importancia de cada atributo de calidad para
> ese rol (hoja `2. Priorización-QA`) y el consenso de necesidad (SÍ) en la hoja `3. Caracterización`.
> Estas son las 65 preguntas con mayor puntaje total sumando los tres roles; **en caso de empate se
> prefirió el atributo de calidad de mayor prioridad promedio** entre los tres roles y, en segundo
> lugar, el mayor consenso (SÍ) entre Juan, Jerónimo y el Cliente.

---

## 8 · Las preguntas priorizadas — Top 65

Fuente: hoja `Top 65 - Priorizadas` de `EscenariosCalidad.xlsx`. **Son la base directa de los
escenarios de calidad**: cada una es una necesidad ya consensuada, ya atribuida a un atributo y ya
ponderada por los tres roles, y **cada una se convirtió en uno de los 65 `ESC-nnn` del `§9`**.

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

## 9 · Los escenarios de calidad `ESC-01` … `ESC-65`

Fuente: `EscenariosCalidad.xlsx`, hoja `4, Lluvia-de-Escenarios`. **Los 65 escenarios están escritos
y esta sección los recoge completos.** No se redactó ninguno aquí: se transcriben de la hoja, que es
donde se trabajaron y donde cada uno conserva sus seis partes en columnas separadas —fuente,
estímulo, artefacto, entorno, respuesta y medida de respuesta— además del párrafo armado que es lo
que se reproduce abajo.

**Cómo se construyeron.** Cada fila del `Top 65 - Priorizadas` del `§8` se convirtió en un escenario,
conservando el orden de puntaje: el `ESC-nn` lleva el mismo número que el puesto de su pregunta. La
conversión siguió tres reglas:

1. **Lo que estaba escrito como funcionalidad se reescribió como propiedad de calidad observable.**
   Una pregunta del tipo *«¿se necesita que el sistema haga X?»* no es un escenario; lo que se
   pregunta en un escenario es cuánto, en cuánto tiempo y bajo qué condición.
2. **Todo escenario recibió un estímulo concreto y una medida de respuesta verificable**, tomada de
   los números ya medidos con el cliente que están reunidos en el `§10`: pérdida de datos cero, una
   hora de tolerancia a fallo, un día para restaurar, los 8 días de latencia actual, el 2% de error
   de captura, el pico de temporada del +60%.
3. **Se agruparon por atributo de calidad, no por requisito**, como fija el `§11.4`.

`[!]` **Qué son y qué no son.** Son ejemplos de uso de la vida real y del comportamiento esperado,
no el contrato final de aceptación. Los números que llevan **no están todos validados con el
cliente**: los que sí lo están son los del `§10.1`; el resto son propuesta del equipo y se distinguen
por eso, no por cómo están escritos. [:DD:] **El esqueleto `ESC-01`..`ESC-08` del viejo
`5_RF_CRITICOS_v1.xlsx` quedó descartado**: no se usa ni su contenido ni su numeración, y la
numeración actual no tiene nada que ver con aquella.

### 9.1 Reparto por atributo

| Atributo | Puesto en el ranking | Escenarios | Cuáles |
|---|:--:|:--:|---|
| Confiabilidad | 1 | 18 | ESC-01, ESC-02, ESC-03, ESC-07, ESC-08, ESC-09, ESC-10, ESC-11, ESC-17, ESC-18, ESC-19, ESC-33, ESC-34, ESC-35, ESC-55, ESC-56, ESC-57, ESC-58 |
| Disponibilidad | 2 | 3 | ESC-04, ESC-20, ESC-59 |
| Rendimiento | 3 | 6 | ESC-05, ESC-36, ESC-37, ESC-38, ESC-60, ESC-61 |
| Capacidad para ser Auditado | 4 | 5 | ESC-12, ESC-39, ESC-40, ESC-62, ESC-63 |
| Capacidad | 5 | 6 | ESC-21, ESC-41, ESC-42, ESC-43, ESC-64, ESC-65 |
| Capacidad para ser Administrado | 6 | 10 | ESC-06, ESC-13, ESC-14, ESC-22, ESC-23, ESC-24, ESC-25, ESC-44, ESC-45, ESC-46 |
| Experiencia de Usuario | 7 | 5 | ESC-15, ESC-26, ESC-27, ESC-47, ESC-48 |
| Seguridad | 8 | 3 | ESC-28, ESC-49, ESC-50 |
| Interoperatividad | 9 | 2 | ESC-29, ESC-51 |
| Escalabilidad | 10 | 1 | ESC-52 |
| Capacidad para ser Soportado | 11 | 3 | ESC-30, ESC-31, ESC-53 |
| Portabilidad | 12 | 3 | ESC-16, ESC-32, ESC-54 |

**Los cinco drivers concentran 38 de los 65 escenarios**, y solo Confiabilidad se lleva 18. Es
coherente con el `§6.1`: Confiabilidad es el único atributo que los tres actores pusieron en el
puesto 1, y con la frase de cierre del cliente —*«certeza de los datos, que se ingresen los datos
correctamente»*—.

`[!]` **Accesibilidad se quedó con cero escenarios.** Es el atributo 13 del ranking y ninguna de sus
preguntas entró en el Top 65, así que ninguna se convirtió en escenario. **No es un olvido, es una
consecuencia del corte**, pero conviene mirarlo junto con lo que dice el `§5`: el cliente puso
Accesibilidad de última y después respondió que sí a casi todo lo que preguntaba por ella, de modo
que es probable que no entendiera el término. Las dos preguntas de Accesibilidad que se quedaron a un
punto del corte están en el `§8.2`.

### 9.2 Los 65 escenarios

Formato de dos columnas, tal como lo fija el `§11.4`. Cada párrafo contiene los seis elementos del
modelo de Bass, Clements y Kazman.

#### Confiabilidad — 18 escenarios

| ID | Escenario |
|---|---|
| `ESC-01` | un supervisor de campo termina la captura de una cama y el dispositivo falla (batería agotada, cierre inesperado o pérdida de conexión) antes de sincronizar, sobre el almacenamiento local de la app de captura y su cola de sincronización, en operación normal en campo, sin conexión de datos. El sistema conserva el registro en el dispositivo y lo entrega al servidor en la siguiente sincronización, sin duplicarlo ni alterarlo, con 0 registros perdidos sobre el total capturado, 0 duplicados tras la sincronización y 100% de los registros recuperables tras reiniciar el dispositivo. |
| `ESC-02` | un supervisor de campo digita un valor que viola una regla de rango (tallos mayores que las plantas sembradas de la sección), sobre el motor de validación embebido en la app de captura, en campo, sin conexión. El sistema rechaza el valor en pantalla, indica la regla violada y no permite guardar hasta corregirlo, con 100% de los valores fuera de rango rechazados en el propio dispositivo, respuesta visible en menos de 1 segundo y 0 registros fuera de rango llegando al servidor. |
| `ESC-03` | el programador de respaldos del sistema dispara el respaldo al cumplirse el intervalo definido, sin intervención humana, sobre la base de datos de la empresa y su repositorio de respaldos, en operación normal, incluida la temporada alta. El sistema genera, cifra y verifica el respaldo y registra el resultado sin que nadie lo pida, con 100% de respaldos ejecutados dentro de su ventana, pérdida de datos 0 confirmada y restauración completa en 1 día o menos. |
| `ESC-07` | un administrador de la empresa, con el valor aprobado por producción cambia un rango válido, un ciclo, un margen o una regla de validación y ejecuta la acción de guardar, sobre el catálogo de reglas y parámetros y su mecanismo de distribución a los dispositivos, en operación normal, con los dispositivos ya en campo y posiblemente fuera de línea. El sistema aplica la regla nueva sin publicar una versión de la aplicación y sin que nadie reinstale nada: la propaga a los dispositivos conectados y la deja en cola para los desconectados, con 0 despliegues de aplicación, vigencia en todos los dispositivos en menos de un ciclo de sincronización, 0% de rechazos por conflicto con otras reglas, 0 proyecciones históricas alteradas y 0 solicitudes al desarrollador. |
| `ESC-08` | un supervisor de campo detecta un error en un registro que todavía está en la cola local, sobre el registro local y la bitácora de cambios del dispositivo, en campo, sin conexión. El sistema permite corregir el valor, conserva el valor original y el motivo, y sincroniza ambos, con corrección posible en 3 toques o menos, 100% de las correcciones con valor anterior y autor registrados y 0 registros sincronizados sin trazabilidad. |
| `ESC-09` | un administrador de producción abre una proyección y pide su origen, sobre la versión de la proyección junto con el conjunto de datos y parámetros con que se calculó, en operación normal, desde la consulta web. El sistema muestra la versión de parámetros, el corte de datos y la fecha de cálculo, y permite recalcularla obteniendo el mismo resultado, con 100% de las proyecciones con versión de datos y de parámetros asociada, recálculo idéntico (diferencia 0) sobre la misma versión e información disponible en 3 clics o menos. |
| `ESC-10` | los datos reales de corte de un ciclo llegan al sistema al cerrarse el ciclo, sobre la proyección versionada y su indicador de desviación, en operación normal, al cierre de ciclo. El sistema calcula la desviación contra la versión de proyección que estaba vigente en ese momento y la muestra sobre la misma proyección, con desviación disponible en 1 día o menos tras el cierre del ciclo, 100% de los ciclos con desviación calculada y 0 comparaciones contra versiones recalculadas después. |
| `ESC-11` | el dispositivo del supervisor de campo se apaga por batería agotada con una captura a medio diligenciar, sobre el almacén local de la app de captura, en campo, sin conexión, con la jornada avanzada. El sistema restaura la captura en el punto donde iba al encender y la marca como pendiente, con 0 registros perdidos, 1 campo por rehacer como máximo y restauración automática al abrir la app, sin acción del usuario. |
| `ESC-17` | el dispositivo de un supervisor de campo queda con la fecha o la hora desviadas de la referencia del sistema, sobre el sello de tiempo del registro capturado, en campo, sin conexión, con el reloj del dispositivo como única referencia. El sistema detecta la desviación, marca el registro y exige confirmación o corrección antes de aceptarlo, con desviaciones mayores a 5 minutos detectadas en el 100% de los casos, 0 registros sincronizados con fecha inconsistente sin marca y aviso al usuario en el momento. |
| `ESC-18` | la aplicación de captura se cierra de manera inesperada durante una captura, sobre el almacén local y el estado de la captura en curso, en campo, sin conexión. El sistema restaura al reabrir los datos ya diligenciados y señala qué falta, con 0 registros confirmados perdidos, restauración en 5 segundos o menos al reabrir y 1 campo por rehacer como máximo. |
| `ESC-19` | el operador de la plataforma (el equipo FlorLogic) cumple el periodo de verificación de respaldos, sobre los respaldos de la base de datos de cada empresa, en operación normal, sin afectar el servicio del cliente. El sistema restaura el respaldo en un entorno aislado, verifica su integridad y deja constancia del resultado, con 1 prueba de restauración por mes y por empresa, 100% con resultado registrado y restauración completa en 1 día o menos. |
| `ESC-33` | un administrador de producción cierra la jornada una vez sincronizada toda la captura del día, sobre el consolidado de la jornada por finca, al cierre de jornada, con los 3 dispositivos sincronizados. El sistema presenta el consolidado señalando las inconsistencias (camas faltantes, valores atípicos, duplicados) antes de alimentar la proyección, con consolidado disponible en 1 hora o menos desde el cierre, 100% de las camas esperadas contrastadas contra las capturadas y 0 inconsistencias pasando sin marcar. |
| `ESC-34` | dos supervisores de campo capturando por separado registran la misma cama el mismo día en dispositivos distintos y sin conexión, sobre la detección de duplicados en la sincronización, al cierre de jornada, con las capturas hechas sin conexión en dispositivos distintos. El sistema detecta el conflicto, conserva ambas versiones y pide resolución antes de consolidar, con 100% de los duplicados detectados, 0 registros descartados automáticamente y conflicto notificado en 1 hora o menos desde la sincronización. |
| `ESC-35` | la app de captura detecta que el almacenamiento libre del dispositivo bajó del umbral definido, sobre el almacén local y la cola de sincronización, en campo, sin conexión, en una jornada larga sin sincronizar. El sistema avisa al supervisor y al administrador y protege la cola pendiente antes que cualquier otro dato, con aviso con al menos 1 jornada de captura de margen, 0 registros perdidos por falta de espacio y aviso repetido hasta que se resuelva. |
| `ESC-55` | un supervisor de campo debe interrumpir la captura de una cama antes de completarla, sobre el registro de captura y su estado (pendiente o confirmado), en campo, sin conexión. El sistema guarda lo diligenciado, lo marca como pendiente y lo excluye de la proyección hasta completarlo, con 0 registros pendientes alimentando la proyección, 100% de los pendientes visibles al supervisor y al administrador y retomable en 2 toques o menos. |
| `ESC-56` | un supervisor de campo recibe el rechazo de un valor que acaba de digitar, sobre los mensajes del motor de validación en el dispositivo, en campo, sin conexión y sin nadie a quien preguntar. El sistema indica qué regla se violó, con qué valores y qué debe hacer, sin códigos técnicos, con 100% de los rechazos con motivo en lenguaje del negocio, 0 códigos de error técnicos visibles y corrección sin ayuda externa en el 90% o más de los casos de prueba. |
| `ESC-57` | un supervisor de campo captura el mismo dato con conexión y sin ella, sobre el motor de validación del dispositivo y el del servidor, en campo sin conexión y luego al sincronizar. El sistema no vuelve a rechazar en el servidor lo que el dispositivo aceptó con la misma versión de reglas, con 0% de divergencias entre la validación local y la del servidor con la misma versión de reglas, 100% de los registros con la versión de reglas usada y discrepancias siempre reportadas, nunca descartadas en silencio. |
| `ESC-58` | un supervisor de campo que detecta el error, con autorización del administrador de la empresa solicita corregir un registro que ya está en el servidor y ya alimentó una proyección, sobre el registro sincronizado, su bitácora y las proyecciones que lo usaron, en operación normal, con proyecciones ya publicadas. El sistema aplica la corrección como un evento nuevo, conserva el valor original y marca las proyecciones afectadas, con 0 valores sobrescritos sin historia, 100% de las correcciones con autor, motivo y autorización y proyecciones afectadas identificadas en 1 hora o menos. |

#### Disponibilidad — 3 escenarios

| ID | Escenario |
|---|---|
| `ESC-04` | un supervisor de campo entra a un bloque sin cobertura y captura durante toda la jornada, sobre la app de captura en el dispositivo Android y su almacén local, sin conexión, jornada completa en campo. El sistema permite capturar, validar y guardar localmente todos los registros y los encola para sincronizar cuando vuelva la conexión, con 100% de las funciones de captura disponibles sin red, 0 registros perdidos y jornada completa (8 horas o más) sin conexión. |
| `ESC-20` | el monitor de sincronización del sistema detecta que un dispositivo superó el tiempo máximo sin sincronizar, sobre el registro de estado de dispositivos y el canal de notificación, en operación normal, al cierre de jornada. El sistema avisa al administrador de la empresa indicando el dispositivo, el usuario y cuántos registros hay en cola, con detección en 1 hora o menos desde superado el umbral, 100% de los dispositivos vencidos notificados y 0 falsos negativos en la jornada. |
| `ESC-59` | una caída del servicio en la nube deja el servicio central indisponible durante la jornada, sobre la app de captura y su cola local, con la jornada de campo en curso y los 3 supervisores capturando. El sistema mantiene la captura funcionando sin degradación y sincroniza todo lo capturado al restablecerse el servicio, con 100% de la captura disponible durante la caída, 0 registros perdidos, recuperación del servicio en 1 hora o menos y puesta al día en 1 jornada o menos. |

#### Rendimiento — 6 escenarios

| ID | Escenario |
|---|---|
| `ESC-05` | los dispositivos de captura al cierre de jornada sincronizan la información de la jornada, sobre el motor de proyección y sus versiones publicadas, al cierre de jornada, con los 3 dispositivos sincronizando, también en temporada alta (+60% de registros). El sistema recalcula la proyección afectada y publica una versión nueva conservando la anterior, con proyección actualizada en 1 hora o menos desde la sincronización, degradación máxima del 20% en temporada alta y 0 versiones anteriores sobrescritas. |
| `ESC-36` | un supervisor de campo confirma un dato de la captura, sobre el almacén local de la app, en campo, sin conexión, con la cola acumulada de una jornada. El sistema persiste el dato localmente y responde de inmediato en la interfaz, con 200 ms o menos percibidos en el 95% de las confirmaciones, 1 segundo o menos en el 99% y sin degradación con la cola de una jornada completa. |
| `ESC-37` | un supervisor de campo pasa de una pantalla de captura a la siguiente, sobre la interfaz de la app de captura y su catálogo local, en campo, sin conexión. El sistema muestra la pantalla siguiente con los datos del catálogo local, sin ninguna llamada a la red, con 300 ms o menos en el 95% de las transiciones, 0 llamadas a red en el flujo de captura y comportamiento idéntico con y sin conexión. |
| `ESC-38` | los dispositivos de captura de la finca sincronizan todos a la vez al terminar la jornada, sobre el servicio de sincronización y la base de datos de cada empresa, al cierre de jornada en temporada alta (+60% de registros y +30 a 40% de personal). El sistema procesa todas las colas sin rechazar registros ni perder el orden, con 0 registros rechazados o perdidos, sincronización completa de la jornada en 30 minutos o menos y degradación máxima del 20% frente a temporada normal. |
| `ESC-60` | un administrador de producción consulta la web después de que los dispositivos sincronizaron, sobre la consulta web y los tableros, al cierre de jornada, incluida la temporada alta. El sistema muestra la información de la jornada ya consolidada, con 1 hora o menos entre la sincronización y la visibilidad en el 95% de los casos, frente a los 8 días actuales, con degradación máxima del 20% en temporada alta. |
| `ESC-61` | la carga de la temporada alta sube el volumen un 60% en tallos y registros y el personal entre 30 y 40%, sobre la sincronización, el motor de proyección y la consulta web, en la temporada alta de marzo y abril. El sistema sostiene los tiempos de respuesta comprometidos sin rechazar operaciones, con degradación máxima del 20% en los tiempos comprometidos, 0 operaciones rechazadas por carga y sin intervención manual para escalar. |

#### Capacidad para ser Auditado — 5 escenarios

| ID | Escenario |
|---|---|
| `ESC-12` | un administrador de producción selecciona una cama y pide su historia, sobre la bitácora de eventos de la cama y de sus secciones, en operación normal, sobre la historia en línea (5 años). El sistema entrega la secuencia completa de siembra, cortes, bajas y erradicaciones con fecha, autor y motivo, con 100% de los eventos presentes y ordenados, consulta en 5 segundos o menos para los últimos 5 años y 0 solicitudes al equipo de desarrollo. |
| `ESC-39` | un administrador de producción selecciona una cifra de un tablero o reporte y pide su composición, sobre el tablero, el reporte y los registros de captura que lo alimentan, en operación normal, sobre la historia en línea. El sistema desglosa la cifra hasta el registro de cama y sección, con autor y fecha, con trazabilidad completa en 3 niveles de navegación o menos, 100% de las cifras descomponibles y desglose en 5 segundos o menos. |
| `ESC-40` | cualquier usuario, incluido el operador de la plataforma intenta modificar o borrar una entrada de la bitácora, sobre la bitácora de eventos del sistema, en operación normal y también durante mantenimiento o soporte. El sistema impide la operación y registra el intento, con 0 entradas modificadas o borradas, 100% de los intentos registrados e integridad de la bitácora verificable en cada respaldo. |
| `ESC-62` | un administrador de producción pide la historia de un lote que se sembró en varias camas y secciones, sobre la relación lote-cama-sección y la bitácora de eventos, en operación normal, sobre la historia en línea de 5 años. El sistema entrega la historia consolidada del lote, con el detalle por cama y por sección, con 100% de las camas y secciones del lote incluidas, consulta en 10 segundos o menos y 0 eventos huérfanos sin lote asociado. |
| `ESC-63` | un supervisor de campo registra una baja o una erradicación que reduce lo esperado, sobre el registro de bajas y su catálogo de motivos, incluida la enfermedad, en campo, sin conexión. El sistema exige un motivo del catálogo antes de aceptar la disminución y lo propaga a la proyección, con 100% de las disminuciones con motivo, 0 bajas aceptadas sin motivo y motivo disponible en el análisis de desviación. |

#### Capacidad — 6 escenarios

| ID | Escenario |
|---|---|
| `ESC-21` | el crecimiento acumulado de la operación suma un año más de registros, incluidos los picos de temporada (+60%), sobre la base de datos de la empresa y las consultas de los tableros, en operación normal, con 5 años de historia en línea. El sistema sigue respondiendo dentro de los mismos tiempos, sin migración ni reinstalación, con degradación máxima del 20% en el tiempo de consulta por cada año adicional, 0 migraciones de plataforma en 5 años y crecimiento absorbido sin detener el servicio. |
| `ESC-41` | un administrador de producción consulta información de hasta cinco años atrás, sobre el almacenamiento en línea de la empresa, en operación normal, desde la consulta web. El sistema responde con los datos históricos sin pedir restauración ni intervención del operador, con 5 años de historia consultables en línea, 0 solicitudes de restauración para consultas dentro de ese rango y respuesta en 10 segundos o menos. |
| `ESC-42` | un administrador de producción consulta información anterior al rango en línea, sobre el almacenamiento histórico frío de la empresa, en operación normal, en una consulta esporádica. El sistema acepta la consulta, avisa que tomará más tiempo y entrega el resultado cuando esté disponible, con entrega en 1 día o menos, 0 datos perdidos por antigüedad y usuario siempre informado del tiempo estimado. |
| `ESC-43` | el operador de la plataforma observa que la historia de una empresa creció un año más, sobre el almacenamiento en línea e histórico de esa empresa, en la operación del SaaS con varias empresas, bajo el presupuesto de ~20.000 USD de construcción y puesta en marcha. El sistema mueve automáticamente lo más antiguo a almacenamiento de menor costo, conservándolo obtenible, con crecimiento del costo por finca sublineal frente al crecimiento de datos, 0 datos eliminados y movimiento automático sin intervención manual. |
| `ESC-64` | un administrador de producción consulta y proyecta sobre varias fincas de su empresa, sobre la estructura Empresa - Fincas - Bloques - Naves - Camas - Secciones, en operación normal, con fincas que capturan en paralelo. El sistema entrega la vista consolidada y la vista por finca sin duplicar catálogos ni instalaciones, con 1 sola instalación por empresa, 100% de los reportes disponibles consolidados y por finca y sin degradación al agregar fincas dentro del rango previsto. |
| `ESC-65` | un supervisor de campo prepara el dispositivo antes de salir al cultivo, sobre el catálogo local (camas, secciones, variedades, grados y reglas), en la finca, con conexión, antes de iniciar la jornada. El sistema descarga y verifica el catálogo completo y avisa si está incompleto o desactualizado, con descarga completa en 5 minutos o menos, 100% del catálogo de la finca disponible localmente y aviso bloqueante si falta algo antes de salir. |

#### Capacidad para ser Administrado — 10 escenarios

| ID | Escenario |
|---|---|
| `ESC-06` | un administrador de la empresa (el ingeniero de sistemas de la finca) intenta modificar un registro de siembra, corte o baja ya capturado, sobre el control de acceso y el registro de producción, en operación normal. El sistema rechaza la modificación, la deja registrada en la bitácora y ofrece la vía de corrección autorizada, con 100% de los intentos rechazados y registrados, 0 registros de producción modificados por ese rol y separación de deberes verificable en la matriz de permisos. |
| `ESC-13` | un administrador de la empresa crea un usuario nuevo o da de baja a uno que salió de la finca, sobre el módulo de usuarios y permisos de la empresa, en operación normal, especialmente en temporada alta (+30 a 40% de personal). El sistema aplica y audita el cambio sin intervención del equipo FlorLogic, con alta o baja completada en 5 minutos o menos, 0 solicitudes al desarrollador y baja efectiva (sesión cerrada) en 1 minuto o menos. |
| `ESC-14` | un administrador de la empresa necesita parametrizar, otorgar permisos o revisar el estado del sistema, sobre la consola de administración web, en operación normal, desde la red de la finca y con un navegador. El sistema expone todas las tareas de administración corrientes por interfaz, sin línea de comandos ni acceso directo a los datos, con 100% de las tareas de administración definidas cubiertas por la consola, 0 tareas que exijan consola técnica o SQL y ninguna instalación adicional en el computador. |
| `ESC-22` | un administrador de la empresa retira o cambia un permiso a un usuario que está usando el sistema, sobre el control de acceso y las sesiones activas, en operación normal, con dispositivos en campo posiblemente sin conexión. El sistema aplica el permiso nuevo en la siguiente acción del usuario conectado y en la primera sincronización del desconectado, con efecto en 1 minuto o menos en sesiones conectadas, efecto en la primera sincronización en las desconectadas y 0 acciones aceptadas con el permiso ya retirado. |
| `ESC-23` | un administrador de la empresa, con los datos aprobados por producción registra una variedad nueva con su densidad, su productividad esperada y sus días a corte, sobre el catálogo de variedades de la empresa, en operación normal, con los dispositivos ya en campo. El sistema deja la variedad disponible para captura y proyección tras la siguiente sincronización, sin tocar el código, con 0 líneas de código y 0 despliegues, disponible en los dispositivos en un ciclo de sincronización o menos y 0 proyecciones históricas alteradas. |
| `ESC-24` | un administrador de la empresa, con el valor aprobado por producción cambia la densidad de siembra o un parámetro del motor de proyección, sobre el catálogo de parámetros versionado y el motor de proyección, en operación normal, con proyecciones ya publicadas. El sistema aplica el cambio a los cálculos nuevos como una versión nueva de parámetros, sin modificar las proyecciones ya emitidas, con 0 despliegues, 0 proyecciones históricas alteradas y versión de parámetros registrada en el 100% de los cálculos posteriores. |
| `ESC-25` | el operador de la plataforma publica una versión nueva de la app de captura, sobre los dispositivos Android en campo y su mecanismo de actualización, en operación normal, con dispositivos en campo y algunos sin conexión. El sistema actualiza los dispositivos al reconectarse, conservando la información pendiente de sincronizar, con 100% de los dispositivos actualizados en una jornada o menos tras reconectar, 0 registros pendientes perdidos en la actualización y 0 dispositivos recogidos físicamente. |
| `ESC-44` | un administrador de la empresa, con la definición aprobada por producción agrega o redefine un grado de calidad, sobre el catálogo de grados versionado, en operación normal, con registros históricos tomados con la definición anterior. El sistema deja el grado nuevo disponible para la captura siguiente y conserva en los registros anteriores la definición con que se tomaron, con 0 despliegues, disponible en los dispositivos en un ciclo de sincronización o menos y 100% de los registros históricos conservando su versión de grado. |
| `ESC-45` | un administrador de la empresa cambia un parámetro del motor de proyección, sobre las proyecciones ya publicadas y el catálogo de parámetros versionado, en operación normal, con proyecciones semanales publicadas. El sistema aplica el parámetro solo a los cálculos nuevos y conserva intactas las versiones publicadas, con 0 proyecciones publicadas modificadas, 100% de las proyecciones con su versión de parámetros y comparación de desviación siempre contra la versión vigente. |
| `ESC-46` | un administrador de la empresa ordena la sincronización de un dispositivo con registros pendientes, sobre el dispositivo de captura y el servicio de sincronización, en operación normal, con conexión intermitente en el dispositivo. El sistema sincroniza el dispositivo en cuanto tenga conexión, sin que el supervisor haga nada, con orden ejecutada en 5 minutos o menos desde que el dispositivo tiene conexión, 100% de los registros pendientes entregados y 0 desplazamientos al campo. |

#### Experiencia de Usuario — 5 escenarios

| ID | Escenario |
|---|---|
| `ESC-15` | un supervisor de campo captura al mediodía, con sol directo sobre la pantalla, sobre la interfaz de la app de captura (contraste, tamaño de texto y controles), en campo, a sol directo, con guantes y una sola mano libre. El sistema mantiene la información y los controles legibles y accionables sin cambiar de posición ni de ajustes, con contraste de 4.5:1 o mayor en todo el texto, objetivos táctiles de 48 dp o más y 100% de las tareas de captura completadas por los 3 supervisores en prueba a sol directo sin asistencia. |
| `ESC-26` | un supervisor de campo registra una cama completa (siembra o corte) durante el recorrido, sobre el formulario de captura de la app, en campo, de pie, con una sola mano y sin conexión. El sistema completa por defecto lo que ya conoce (cama, sección, variedad y fecha) y solo pide lo que cambia, con 10 toques o menos por cama, 60 segundos o menos por cama y tiempo total de captura del supervisor de 15 minutos al día o menos, frente a la hora diaria actual. |
| `ESC-27` | un supervisor de campo llega a una cama e inicia la captura, sobre el identificador físico de la cama y el lector de la app, en campo, a sol directo y sin conexión. El sistema identifica la cama al escanear la marca física y deja la digitación solo como alternativa, con identificación en 3 segundos o menos, 0% de error de identificación frente al 2% de error de captura actual y funcionamiento sin conexión en el 100% de los casos. |
| `ESC-47` | un supervisor de campo revisa lo capturado durante la jornada o al terminarla, sobre el indicador de estado de sincronización de la app, en campo, con conexión intermitente. El sistema muestra por registro y en total cuántos están sincronizados y cuántos pendientes, y desde cuándo, con estado visible sin navegar (1 toque o menos), 100% de los registros con estado correcto y 0 casos de registro mostrado como sincronizado sin estarlo. |
| `ESC-48` | un supervisor de campo busca una cama, un bloque, una variedad o un grado en la app, sobre el catálogo de nombres de la empresa (bloques, naves, camas, variedades y grados), en campo, sin conexión, y también en la consulta web. El sistema muestra la nomenclatura propia de esa empresa, parametrizable, sin nombres técnicos ni códigos internos, con 100% de los términos validados contra el glosario de la finca, 0 términos técnicos visibles al supervisor y nomenclatura configurable por empresa sin desarrollo. |

#### Seguridad — 3 escenarios

| ID | Escenario |
|---|---|
| `ESC-28` | un usuario que deja el dispositivo o el computador sin uso supera el tiempo de inactividad definido, sobre la sesión de la app de captura y de la consola web, en operación normal; en campo el dispositivo se comparte entre supervisores. El sistema cierra la sesión y exige autenticarse de nuevo, conservando la captura en curso como pendiente, con cierre en 15 minutos de inactividad o menos, 0 capturas perdidas por el cierre y 100% de las sesiones afectadas, también sin conexión. |
| `ESC-49` | un supervisor de campo o cualquier otro usuario entra al sistema y captura o modifica información, sobre las identidades de usuario y la bitácora de acciones, en campo, con dispositivos que se comparten entre supervisores. El sistema exige identificación individual, también sin conexión, y atribuye cada registro a su autor, con 0 usuarios compartidos activos, 100% de los registros con autor identificado y autenticación posible sin conexión. |
| `ESC-50` | el operador de la plataforma (el equipo FlorLogic) ejecuta una tarea de operación (respaldo, restauración, despliegue o diagnóstico), sobre la base de datos de la empresa y sus respaldos cifrados, en la operación normal del SaaS, con clientes que compiten entre sí. El sistema permite la tarea sin exponer el contenido de negocio y registra todo acceso, con 0 accesos a datos de negocio en operación normal, 100% de los accesos excepcionales registrados y autorizados y respaldos cifrados en el 100% de los casos. |

#### Interoperatividad — 2 escenarios

| ID | Escenario |
|---|---|
| `ESC-29` | un administrador de producción o un analista de la empresa solicita conectar una herramienta de análisis externa a los datos de su empresa, sobre la interfaz de salida de datos de la empresa (exportación o servicio de consulta), en operación normal, dentro de los límites de aislamiento por empresa. El sistema entrega los datos de esa empresa en un formato consumible, autenticado y limitado a su ámbito, con 0 accesos directos a la base de datos, 100% de las consultas limitadas a la empresa del solicitante y extracción de un año de historia en 10 minutos o menos. |
| `ESC-51` | un administrador de producción exporta el resultado de una consulta o un reporte, sobre los reportes y tableros del sistema, en operación normal, desde la consulta web. El sistema entrega el archivo con los mismos datos y el mismo detalle que muestra en pantalla, con exportación disponible en el 100% de los reportes, archivo generado en 30 segundos o menos para un año de datos y 0 diferencias entre lo mostrado y lo exportado. |

#### Escalabilidad — 1 escenario

| ID | Escenario |
|---|---|
| `ESC-52` | un administrador de la empresa o el operador de la plataforma da de alta una finca nueva de una empresa ya existente, sobre la estructura de la empresa (fincas, bloques, naves, camas y secciones), en operación normal, sin detener a las fincas existentes. El sistema deja la finca operativa con su catálogo y sus usuarios, sin reinstalar ni reconfigurar el sistema, con alta completa en 1 día o menos, 0 reinstalaciones y 0 minutos de interrupción para las fincas existentes. |

#### Capacidad para ser Soportado — 3 escenarios

| ID | Escenario |
|---|---|
| `ESC-30` | el operador de la plataforma recibe el reporte de un fallo en un dispositivo o en la sincronización, sobre los registros de operación, la telemetría y el estado de sincronización, en soporte remoto, con la finca a horas de distancia. El sistema permite identificar la causa con la información de operación disponible, sin acceder al contenido de producción del cliente, con causa identificada en 4 horas o menos en el 80% de los casos, 0 desplazamientos para diagnóstico y 0 accesos a datos de negocio del cliente. |
| `ESC-31` | un administrador de la empresa o el operador de la plataforma consulta el estado de un dispositivo, sobre el panel de estado de dispositivos, en operación normal, con el dispositivo posiblemente en campo y sin conexión. El sistema muestra el último estado conocido junto con su antigüedad, sin requerir que el dispositivo esté conectado, con estado disponible para el 100% de los dispositivos registrados, antigüedad del dato siempre visible y consulta en 5 segundos o menos. |
| `ESC-53` | un administrador de la empresa enfrenta un problema corriente (usuario bloqueado, dispositivo sin sincronizar, parámetro mal cargado), sobre la consola de administración y su documentación de operación, en operación normal, sin soporte del proveedor disponible de inmediato. El sistema le entrega el diagnóstico y la acción correctiva para resolverlo sin escalar, con 80% o más de los incidentes corrientes resueltos dentro de la finca, resolución en 1 hora o menos y escalamiento solo en el 20% restante. |

#### Portabilidad — 3 escenarios

| ID | Escenario |
|---|---|
| `ESC-16` | un administrador de la empresa, con el operador de la plataforma exige que los datos de la empresa permanezcan en su propia infraestructura, sobre el paquete de despliegue del sistema y la base de datos de esa empresa, en la puesta en marcha de un cliente nuevo. El sistema se instala en la infraestructura indicada usando el mismo paquete, sin cambios en el código, con 0 cambios de código entre el despliegue en nube y en sitio, puesta en marcha en 7 días o menos (tope de retraso operativo) y 100% de las funciones disponibles en ambos modos. |
| `ESC-32` | un supervisor de campo instala y usa la app en un dispositivo Android existente de la finca, sobre la aplicación de captura, en dispositivos de gama de entrada ya en uso en la finca, sin conexión. El sistema se instala y opera con todas sus funciones de captura en las versiones de Android soportadas, con soporte declarado desde una versión mínima de Android definida, 100% de las funciones de captura operativas en el dispositivo de menor gama del parque y 0 dispositivos nuevos exigidos para el piloto. |
| `ESC-54` | un supervisor de campo cuyo dispositivo se dañó o se perdió pasa a un dispositivo nuevo con registros pendientes en el anterior, sobre la cola local de sincronización y la identidad del usuario, en campo o al cierre de jornada, con el dispositivo anterior posiblemente inaccesible. El sistema recupera o reconstruye la información pendiente en el dispositivo nuevo, o deja constancia explícita de lo que no pudo recuperarse, con 0 registros perdidos si el dispositivo anterior es accesible, reposición operativa en 1 hora o menos y 100% de los registros no recuperables reportados de forma explícita. |

---

## 10 · Las medidas disponibles hoy — materia prima de los escenarios

**Un escenario sin medida de respuesta no es un escenario.** Esto es todo el número que hay en el
proyecto, reunido. Lo que no está aquí, **se marca `PENDIENTE` en el escenario; no se inventa**.

### 10.1 Números firmes

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

### 10.2 La medida que el cliente dio y que hay que traducir con cuidado

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

## 11 · Lo que todavía bloquea o matiza los escenarios

**Que los 65 escenarios estén escritos no quiere decir que estén cerrados.** Esta sección es la lista
de lo que puede obligar a reescribir alguno, y hay que leerla pegada al `§9`: un escenario cuya
medida salió de un supuesto del equipo y no de un número del cliente **cambia en cuanto el cliente
responda**.

### 11.1 Estado de medida de los cinco drivers

Cuando se ancló el ranking, **los cinco drivers tenían un problema de medida**. Las rondas 2 a 5
resolvieron tres, y los escenarios del `§9` taparon los otros dos con cifras propuestas por el
equipo, que es distinto de tenerlos resueltos:

| # | Driver | Estado de su medida | Entrada |
|:--:|---|---|---|
| 1 | **Confiabilidad** | [:PP:] **Parcial.** Meta = 0 errores, pero el cliente rechazó los instrumentos. `C8` explica por qué y `CN-15` aporta la medida utilizable: **pérdida cero** | `A14`, `C8`, `CN-15` |
| 2 | **Disponibilidad** | [:OK:] **Resuelta.** 1 hora de fallo no planificado · mantenimiento en mayo · «24×7» = jornada completa activa | `A2` |
| 3 | **Rendimiento** | [:PP:] **Tiene métrica, y el objetivo es propuesta nuestra.** Ya no se mide en segundos por cama: **se mide en latencia de captura a proyección**, con línea base de 8 días. `ESC-60` propone **1 hora o menos** y `ESC-05` lo mismo para la proyección. `[!]` **El cliente nunca dio ese número** | `B2`, `ESC-05`, `ESC-60` |
| 4 | **Capacidad para ser Auditado** | [:PP:] **Con medida propuesta, y con la mitad amputada.** La unidad es la **sesión de sincronización**; no hay marcas de tiempo por dato ni valor anterior. Retención **«DE POR VIDA»**, que `ESC-12`, `ESC-41` y `ESC-62` acotaron a **5 años en línea** por su cuenta. `[!]` **Los 5 años son supuesto del equipo** | `A1`, `B8`, `A12` |
| 5 | **Capacidad** | [:OK:] **Resuelta.** 2 años de búsqueda rápida, después demora escalonada | `A3` |

> **Los dos que faltan siguen faltando: el número de latencia de `B2` y la forma de medir la
> auditabilidad de `A1`.** Los escenarios pusieron una cifra para poder escribirse, no porque la
> respuesta exista. Los dos se preguntan al cliente; ninguno lo decide el equipo solo.

`[!]` **Ojo con la retención.** El `§10.1` dice **2 años** de búsqueda rápida, que es lo que respondió
el cliente en `A3`; los escenarios `ESC-12`, `ESC-41` y `ESC-62` hablan de **5 años en línea**. **Las
dos cifras no son la misma cosa** —una es hasta dónde la consulta va rápido, la otra hasta dónde el
dato está disponible sin restaurar— pero conviene preguntarlas juntas y dejar de arrastrar dos
números parecidos.

### 11.2 Las decisiones tomadas **en contra** del cliente

**Hay que llevárselas a la sesión, no aplicarlas en silencio.** Y varias **ya están dentro de un
escenario del `§9`**, así que si el cliente se ratifica en su respuesta, ese escenario se cae con
ella. La correspondencia está en la última columna.

| ID | Qué se decidió contra su respuesta | Escenario que lo lleva dentro |
|---|---|---|
| `A5` | **Se cifra la información en el dispositivo**, aunque dijo que no. Motivo: es casi gratis y `CN-03` lo respalda | `ESC-50` |
| `A8` | **Dentro de una empresa todo es visible para todos**, con filtros por panel | `ESC-06`, `ESC-22` |
| `A10` | **La sincronización puede degradarse** bajo carga — él lo asume como realista | `ESC-38`, `ESC-61` |
| `A11` | **Sí hay cierre de periodo** y fecha desde la cual no se corrige libremente. Motivo legal | `ESC-58` |
| `B12` | **Se construyen dos tableros** —qué está sin sincronizar y avance del día por bloque— aunque dijo que no. **La idea fue suya en S1** y son el instrumento de la medida de `B2` | `ESC-20`, `ESC-31`, `ESC-47` |
| `B13` | **Habrá una guía corta**, aunque pidió no tener manual. Lo mínimo para que la operación no se caiga | `ESC-53` |
| `C2` | **Entra el asistente de captura por IA**, entrenado en el entorno del cliente | `ESC-26`, `ESC-27` |

`[!]` **Dos que estaban en esta lista y salieron, y valen como aviso de método:** `B7` —*«NO DEJA
INGRESAR EL ÚLTIMO REGISTRO»* significaba *«el más viejo»*, lo leímos al revés— y `B11` —el NO a la
vista geométrica era **sobre la captura**, no sobre la consulta—.
**Las notas literales de la caracterización hay que confirmarlas con el cliente, no interpretarlas.**

### 11.3 Lo que sigue abierto y puede tumbar un escenario

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
dispositivo se usa, que fija el piso técnico de la captura offline y del que dependen directamente
`ESC-32`, `ESC-35`, `ESC-36` y `ESC-37`—.

**Qué escenario cae con qué:**

| Si se resuelve así… | Se reescriben |
|---|---|
| `D1` trae un catálogo de reglas duras distinto del que suponemos | `ESC-02`, `ESC-07`, `ESC-56`, `ESC-57` |
| `D3` dice que el % de productividad sale de otro lado | `ESC-05`, `ESC-24`, `ESC-45` |
| `CN-20` obliga a convivir con el sistema heredado o a migrarlo | `ESC-29`, `ESC-41`, `ESC-42`, `ESC-51` |
| `CN-21` fija un piso de gama más bajo del previsto | `ESC-32`, `ESC-35`, `ESC-36`, `ESC-37` |
| `B2` da un número de latencia distinto de 1 hora | `ESC-05`, `ESC-60` |
| `A12` concreta qué significa «de por vida» | `ESC-12`, `ESC-41`, `ESC-62` |

### 11.4 Formato acordado para los escenarios

**Dos columnas y nada más: `ID | Escenario`.** El escenario se escribe como **párrafo narrativo
continuo** que contiene los seis elementos de Bass, Clements y Kazman: **entorno · fuente del
estímulo · estímulo · artefacto · respuesta · medida de respuesta**, y se agrupa **por atributo de
calidad, no por requisito**.

[:OK:] **Estado: los 65 escenarios están escritos y están en el `§9`.** La hoja
`4, Lluvia-de-Escenarios` de `EscenariosCalidad.xlsx` los guarda con las seis partes en columnas
separadas, además del párrafo armado.

[:W:] **Dos precisiones sobre el formato, respecto de lo que se había acordado.** Los IDs quedaron
como `ESC-01`..`ESC-65` y no `ESC-001`, porque son 65 y no hacían falta tres dígitos; y el número del
escenario **coincide con el puesto de su pregunta en el `Top 65`**, lo que no estaba acordado pero
resultó útil para rastrearlos. [:DD:] **El esqueleto `ESC-01`..`ESC-08` del viejo
`5_RF_CRITICOS_v1.xlsx` sigue descartado** y su numeración no tiene relación con esta.

---

## 12 · De dónde sale cada cosa

**Los cuatro Excel están en esta misma carpeta.** Todo lo demás que se cita está en
`Documentacion/Archivo/`.

| Sección | Fuente primaria |
|---|---|
| `§1` marco y modelo de entrega | `Archivo/Recopilacion/3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md` §0.1 · `Archivo/Recopilacion/1_VOZ_DEL_CLIENTE.md` |
| `§2` funcionalidades significativas | `FuncionalidadesSignificativas.xlsx`, hoja `FuncionalidadesSignificativas` + rondas 1-5 del documento de decisiones |
| `§3` restricciones de negocio | `RestriccionesNegocio.xlsx` |
| `§4` restricciones técnicas | `RestriccionesTecnicas.xlsx` |
| `§5` `§6` `§7` atributos, votaciones y mini QAW | `EscenariosCalidad.xlsx`, hojas `1. Trade-Off-QA` y `2. Priorización-QA` |
| `§8` Top 65 | `EscenariosCalidad.xlsx`, hoja `Top 65 - Priorizadas` — **tabla generada desde el archivo, no transcrita a mano** |
| `§9` escenarios de calidad | `EscenariosCalidad.xlsx`, hoja `4, Lluvia-de-Escenarios` — **transcritos desde el archivo, no redactados aquí** |
| `§10` medidas | `CN-15`, y las entradas `A2`, `A3`, `A10`, `A19`, `B1`, `B2`, `C8` del documento de decisiones |
| `§11` lo abierto | `Archivo/Recopilacion/3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md`, grupos `D` y `E` |

`[!]` **Advertencia de método que aplica a todo el documento.** Las transcripciones de las sesiones
**no distinguen quién habla** —un único GUID por `.vtt`— y todo el conocimiento del negocio descansa
en **una sola voz**, la del director de producción. **Planeación nunca se exploró.**

---

*Documento de drivers arquitectónicos de FlorLogic. Juan Pablo Avendaño y Jerónimo Montoya.*
*v2.1 · última actualización: 4-sep-2026 · 21 funcionalidades significativas · 38 restricciones ·
13 atributos de calidad · 65 escenarios.*
