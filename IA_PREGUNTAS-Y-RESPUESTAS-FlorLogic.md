# FlorLogic — Banco de preguntas y respuestas para exponer sin inventar

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 16-sep-2026, a pedido de Juan
> Estado: **SIN REVISAR** — ni por Juan ni por Jerónimo
> Manda sobre esto: `ADR-PoC-Spikes.xlsx` (36 ADR) · `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx` ·
> `1_VOZ_DEL_CLIENTE.md` · `3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md`
>
> Pareja de **`IA_PRESENTACION-FlorLogic.md`** (raíz del repo). Aquel es el guion; este es el
> entrenamiento. **Está en la raíz por instrucción explícita de Juan.**

---

## Cómo se usa

**Objetivo:** que Juan y Jerónimo puedan explicar **cada detalle** de FlorLogic ante un líder de
arquitectura **sin pararse y sin echar carreta** sobre lo que no se sabe.

**Dos partes, en este orden:**

1. **Parte I · Tablero de contradicciones `X-01`..`X-20`.** Antes de practicar hay que **definirlas**:
   una contradicción sin resolver es una pregunta que se va a responder mal en vivo. Cada una se cierra
   con **una** de cuatro salidas:

   | Salida | Qué significa |
   |---|---|
   | ☐ **Consenso** | Los dos acuerdan qué se dice y se escribe la frase exacta |
   | ☐ **Se la queda Juan** | Juan la resuelve (normalmente: reescribir ADR, RF o ESC) y la responde en la charla |
   | ☐ **Se la queda Jerónimo** | Jerónimo la resuelve y la responde en la charla |
   | ☐ **Va al cliente** | No la decide el equipo; en la charla se declara como hueco, con nombre |

   **Achacarle la pregunta al otro es válido y es parte del método**: lo que no vale es que nadie la
   tenga. La columna *Sugerencia* es solo eso — **no está decidida**.

2. **Parte II · Sesiones `S1`..`S12`, paso por paso.** Cada sesión cubre un tramo del sistema, de
   afuera hacia adentro, y en conjunto dan la idea completa de lo que hará el sistema al final.
   **Uno pregunta, el otro responde sin mirar**, luego se compara con la respuesta de aquí y se marca:
   `✔` la dijo bien · `~` la dijo a medias · `✘` no la supo o inventó.

**Marcas de certeza en cada respuesta** — decirlas en voz alta cuando pregunten:

| Marca | Significa |
|---|---|
| **[cliente]** | Lo dijo o escribió el cliente. Fuente de verificación |
| **[decidido]** | Decisión del equipo registrada en ADR o en la guía de decisiones |
| **[propuesta]** | ADR en estado Propuesta, o decisión sin ADR todavía |
| **[medido]** | Salió de un spike o del prototipo. **Registro de IA sin revisar** |
| **[supuesto]** | Número o afirmación del equipo que nadie confirmó |
| **[no sabemos]** | Hueco declarado. Se responde: *«no lo sabemos; depende de X; lo resuelve Y»* |

**Plantilla de respuesta a una pregunta que no sabemos:**

> «Eso no está decidido. Depende de ___, que es del ___ (cliente/equipo). Lo registramos como ___.»

---

# PARTE I · Tablero de contradicciones

> Cada fila: qué dice cada lado, con su fuente literal · por qué importa en la charla · la salida.
> **Rellenar la línea `Resolución` y la fecha.** Mientras siga vacía, esa pregunta **no se responde en
> vivo como si estuviera cerrada**.

### `X-01` · Las cifras del dolor: ¿4 h por semana o 4 h por tanda? ¿8 días hasta planeación o 8 días de proyección?

- **Lado A — cliente, escrito (S1-Q P28):** *«el practicante de sistemas digitando esta información
  puede tardar 4 horas 1 vez a la semana»*; y el dato tarda **8 días** en llegar a planeación
  (`DRIVERS §1`, `B2`, `H-25`/`H-26`).
- **Lado B — relato de Juan, 8-sep-2026:** *«el traslado de una tanda de plantillas puede tomar 4
  horas»*; *«la proyección confirmada y hecha a mano puede tardar hasta 8 días»* por correcciones.
- **Y un tercer dato:** `RF-008` dice que *«rehacerla a mano puede llevar hasta un mes»*; en S2 dijo
  que la proyección se ajusta **mensual**.
- **Por qué importa:** es **el caso de negocio**. Si el arquitecto pregunta *«¿8 días de qué?»* y los
  dos expositores dan versiones distintas, se cae la credibilidad del resto.
- **Sugerencia:** Consenso para la charla (usar la versión escrita del cliente) + Va al cliente
  (confirmar la versión de Juan en la sesión `D1`).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-02` · Dos capturas de la misma cama el mismo día: ¿gana la más reciente o se pide resolución?

- **`RF-022`:** automático, **gana el más reciente, sin mediación humana**, con aviso a quien capturó.
- **`ADR-027`:** entran las dos; **el sistema decide** después, sin que nadie medie.
- **`ESC-34`:** *«conserva ambas versiones **y pide resolución antes de consolidar**»*, con *«0 registros
  descartados automáticamente»*.
- **Y no sabemos si ocurre:** `D12`/`BR-N4` — ¿dos capturadores en la misma cama el mismo día? **Nunca se preguntó.**
- **Por qué importa:** es la primera pregunta de cualquier arquitecto sobre sistemas offline.
- **Sugerencia:** Juan (reescribir `ESC-34`, ya listado como pendiente de `ADR-020`).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-03` · Reloj alterado: ¿bloquear o marcar?

- **`RF-021` y `CN-25`:** **bloquear** la captura ante alteración manual del reloj.
- **`ADR-014` (Aceptada):** **marcar y pedir confirmación, no bloquear.** `ESC-17` sigue `EN CONFLICTO`.
- **`ADR-031`:** tres horas; la corregida por desfase decide los choques.
- **Sugerencia:** Juan (reescribir `RF-021` y `CN-25`; el propio `ADR-014` lo pide).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-04` · ¿Quién puede corregir un dato ya sincronizado, y con qué requisitos?

- **`RF-017`:** solo el **administrador de la empresa**; impide corrección libre de un periodo cerrado.
- **`ESC-58`:** *«con autorización del administrador»* y *«100 % de las correcciones con autor, **motivo y autorización**»*.
- **`ESC-08`:** conserva *«el valor original **y el motivo**»*.
- **`ADR-020`:** al capturador **ni motivo escrito ni autorización**.
- **`ADR-032`:** se controla por **magnitud del cambio, no por rol**; lo drástico pide un segundo administrador **como aviso**.
- **Cliente, S2:** *«el ingeniero de sistemas puede corregir, aprobado por el área de producción»*.
- **Abierto dentro de `ADR-032`:** qué es «drástico» y qué pasa si el segundo administrador no confirma.
- **Sugerencia:** Juan (reescribir `RF-017`, `ESC-08`, `ESC-58`) + Consenso sobre la lista de acciones drásticas.
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-05` · La unidad de trazabilidad: ¿sesión de sincronización o sesión de captura?

- **`RF-016`** y la guía de decisiones (`A1`): trazabilidad **por sesión de sincronización**.
- **`ADR-028`:** la captura se identifica al llegar a la base (registro del intercambio).
- **`ADR-035`:** la unidad es **la sesión de captura** —quién, cuándo, qué camas—; y queda abierto **cómo se cierra una sesión de captura**.
- **El modelo C4** dice «registra por sesión de sincronización»; «sesión de captura» aparece 0 veces.
- **Por qué importa:** son dos cosas distintas que comparten la palabra «sesión». `ADR-028` avisa que por ahí se cuela la contradicción.
- **Sugerencia:** Consenso (frase: *«la captura lleva el quién y el cuándo; la sincronización lleva el cómo llegó»*) + Juan (reescribir `RF-016`).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-06` · Retención: ¿2 años, 5 años, «de por vida»? ¿Y qué pasa con las correcciones al cerrar?

- **Cliente, `A3`:** búsqueda rápida **2 años**; más atrás, demora escalonada. Retención **«DE POR VIDA»** (`A12`).
- **`ESC-12`/`ESC-41`/`ESC-62`:** **5 años en línea** — `DRIVERS §11.1` lo marca como **supuesto del equipo**. `ESC-12` además pide «5 segundos para 5 años», un número que no sale de ninguna fuente.
- **`RF-016`:** al cerrar la producción *«las correcciones intermedias **dejan de mantenerse en línea**»*.
- **`ADR-022` (Aceptada):** se conserva **absolutamente todo** —cada modificación de cada campo, **abiertas y cerradas por igual**— 5 años.
- **Por qué importa:** un arquitecto va a preguntar cuánto ocupa y cuánto tarda. `RF-016` y `ADR-022` responden distinto.
- **Sugerencia:** Juan (alinear `RF-016` con `ADR-022`) + Cliente (2 contra 5 años y la obligación real de auditoría).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-07` · ¿Pérdida cero o recaptura?

- **`CN-15`, `ESC-01`, `ESC-54`:** pérdida de información **CERO**; «0 registros perdidos».
- **`ADR-002`:** *«si la información se pierde antes de una sincronización entonces se pierde para siempre»*; para el negocio **sale más fácil repetir la jornada**.
- **`ADR-025`:** no se recuperan teléfonos; se genera **la lista de camas a volver a caminar**. Y *«un corte no se recaptura, porque se fue con los tallos»*.
- **Por qué importa:** es la trampa T2 de la charla. Decir «cero» y que el arquitecto pregunte por un teléfono que cae al agua.
- **Sugerencia:** Consenso (frase: *«cero pérdida por fallo del sistema; pérdida acotada a lo no sincronizado de un teléfono destruido, con lista exacta de qué rehacer»*) + Juan (reescribir `ESC-54`).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-08` · ¿La proyección se regenera semanal o con cada sincronización? ¿Qué dispara una publicación?

- **`RF-008`:** regenerar **al menos una vez por semana**, conservando la versión anterior.
- **Modelo C4:** el planificador *«dispara la regeneración semanal»*.
- **`ADR-005`:** **cálculo vivo con cada sincronización** + **versión publicada** congelada. *«Semanal es un piso, no una cadencia.»* **Falta decidir qué dispara la publicación**: cadencia, cierre, cambio de parámetros o acción manual — *«decisión de negocio»*.
- **Sugerencia:** Cliente (qué dispara la publicación) + Consenso sobre la frase del piso.
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-09` · ¿Dónde está la IA y para qué?

- **`CN-31`:** asistente de captura **local**, vocabulario restringido. **`CN-32`:** IA analítica **en la nube**.
- **Guía de decisiones, `C2`:** *«la IA vuelve como asistente de captura»*; y marca el cabo como cerrado **por `ADR-030`** (infiere en el teléfono, entrena en el nodo).
- **Tandas:** `T11` asistente de captura, bloqueada por `SPK-01`.
- **`ADR-030` y `ADR-018`: Depreciadas.** **`ADR-036` (Aceptada):** modelo local ligero **en la finca** para revisar; generativo **en línea** solo documentos; **nada en el teléfono**; **opcional**. Abierto: qué información puede salir hacia el servicio en línea.
- **Cliente, S1:** habló de *«una capa más»* sobre Power BI. **S3:** la IA fue **idea de venta del equipo**.
- **Sugerencia:** Juan (reescribir `CN-31`, `CN-32`, `RFP-05`, nota de `C2`, `T11`) + Consenso sobre qué se dice si preguntan.
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-10` · ¿Una regla dura frena la captura o no?

- **`RF-004`, `RF-005`:** **impedir** el registro imposible. **`ESC-02`:** *«no permite guardar hasta corregirlo»*.
- **Decisión de Juan, 15-sep-2026 (pendiente de ADR):** un **obligatorio sin valor no frena la captura ni la sincronización**; va a la cola del gerente de producción. Medido: **872 de 1.232 camas** (70,8 %) de la batería dejan de bloquearse.
- **`ADR-006`:** *«no se puede parar el proceso por algunos datos erróneos»*.
- **El hueco:** ¿dónde está la frontera? *Faltar un dato* no frena; ¿*un dato imposible* (más tallos que plantas) sí frena? La decisión del 15-sep incluye `RG-01` (variedad) entre las que dejan de bloquear.
- **Por qué importa:** es la promesa central —«certeza de los datos»— contra «nunca parar la jornada».
- **Sugerencia:** Consenso (definir las dos clases: *imposible* vs *incompleto*) + Juan (el ADR).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-11` · ¿Dos motores de reglas o el mismo evaluador? (la «pregunta del motivo»)

- **`ADR-006`:** cliente y servidor interpretan la misma especificación; si divergen, se evalúa en ambos y **se avisa al administrador**. Consecuencia escrita: *«dos motores independientes pueden divergir fácilmente»*.
- **`CT-02`** (contrato de spikes): **el mismo evaluador**. **`ESC-57`:** **0 %** de divergencia con la misma versión de reglas.
- **Medido (`SPK-09`, `SPK-17`):** veredicto 0 % en todos los caminos; **motivo** 1,95 % (37,5 % si el texto vive en el código); el **modo de redondeo** parte los lenguajes en dos bandos y cambia **el número** que ve la persona.
- **La pregunta que decide el lenguaje:** ¿el motivo debe ser idéntico en los dos lados o basta el veredicto? Si idéntico → TypeScript es el único medido que lo permite.
- **Sugerencia:** Consenso (es de los dos y cae el lenguaje con ella).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-12` · Dos libros de ADR con numeración distinta

- **`ADR-PoC-Spikes.xlsx`:** **36 ADR**, reescritos por Juan. `ADR-001` = tipo de producto.
- **`FlorLogic-alternativa-de-solucion-y-ADR.md`:** **31 ADR**, redactados por IA. `ADR-001` = monolito modular; `ADR-008` dice «se decide con `SPK-02`»; `ADR-021` sigue viva ahí.
- **Por qué importa:** en la charla alguien dice «ADR-001» y los dos piensan cosas distintas.
- **Sugerencia:** Consenso (en la charla manda el `.xlsx`) + Juan (decidir si el `.md` se erradica o se renumera).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-13` · Credencial offline de 24 h contra una ventana de 15 días sin sincronizar

- **`ADR-007`:** credencial con vigencia **24 h**; observación: subir a **7–10 días**.
- **`RF-003`:** soportar **al menos 15 días** sin sincronizar (papeles que aparecen tarde).
- **`ADR-025`:** la sincronización apunta a **diaria** y el recordatorio escala hasta impedir.
- **`BR-N5`** (ventana de sesión offline): **nunca se preguntó**.
- **Por qué importa:** con 24 h, un capturador que no sincroniza un día **no puede entrar**; con 15 días, la seguridad se afloja. Choque directo Seguridad–Disponibilidad.
- **Sugerencia:** Cliente (`BR-N5`) + Juan (fijar el umbral en `ADR-007`/`ADR-025`).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-14` · Tablas de consulta, totales precalculados y caché: ¿tres mecanismos o uno?

- **`ADR-010`:** tablas de consulta derivadas (y son las mismas que lee el BI).
- **`ADR-023`:** totales ya calculados + carga progresiva.
- **`ADR-034` (Propuesta):** caché delante de la base con las producciones activas.
- **Auditoría del C4 (14-sep):** *«puede que sobren dos»*. Además `SPK-16` encontró que la seguridad por fila **no protege las vistas materializadas**.
- **Sugerencia:** Juan o Jerónimo (es de arquitectura pura) — decidir antes de la charla quién la defiende.
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-15` · El modelo de datos está en Propuesta, pero «T1 puede arrancar hoy»

- **`ADR-024`:** **Propuesta** — *«hay que analizar más a fondo la forma de almacenar»*; `ADR-035` le cambia la tupla (autor y fecha suben a la sesión de captura).
- **Tandas (`T1`):** *«el dominio: la primera tabla»* marcado **✅ arranca hoy**, sosteniéndose en `ADR-024`.
- **Por qué importa:** el arquitecto va a preguntar *«¿qué construyen primero?»*.
- **Sugerencia:** Juan (llevar `ADR-024` a Aceptada o marcar `T1` bloqueada).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-16` · El modelo C4 dice cosas que los ADR ya cambiaron

- Dispositivo **«Android · Flutter o Kotlin»** contra **`ADR-008`** (aplazado, sigue la web).
- Planificador **Quartz/Hangfire** contra **`ADR-009`** (cola dentro de la misma base).
- **Archivo histórico separado** contra **`ADR-033` Rechazada**.
- IA **«ubicación sin decidir»** contra **`ADR-036`**.
- Sin representar: asignación (`ADR-026`), magnitud (`ADR-032`), desfase (`ADR-031`), custodia fuera de línea (`ADR-012`), **cierre de producción** (0 apariciones), **carga desde Access**, **QR de cama**.
- **Sugerencia:** Juan o Jerónimo — **no mostrar el C4 en la charla sin avisar**.
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-17` · ¿«Cierre de periodo» y «cierre de producción» son lo mismo?

- **`A11`** (decidido **en contra del cliente**, por motivo legal) y **`RF-017`:** existe un **cierre de periodo** desde el cual no se corrige libremente.
- **`ADR-020`, `ADR-022`, `ADR-023`:** la frontera es el **cierre de producción** —un hecho del negocio, lo declara el administrador—.
- **`RF-016`** dice que `A11` *«es el mismo mecanismo»*, pero un periodo es calendario y una producción es un ciclo biológico.
- **Y el evento de cierre no está definido** (quién lo dispara, qué habilita, qué consolida) — `T6`, *«la pieza con más cosas colgando»*.
- **Sugerencia:** Consenso + Juan (definir el evento de cierre).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-18` · El sistema heredado: ¿Access o no sabemos?

- **`CN-20`:** ~300 tablas (45 de producción) y *«un sistema de productividades que el cliente no supo nombrar»*. **EN DUDA · BLOQUEANTE.**
- **Juan, 11-sep-2026:** *«posiblemente y casi que seguro»* **Microsoft Access**. **No es confirmación del cliente**; «Access» no aparece en ninguna transcripción.
- **Cliente, S1:** motivo real del proyecto *«llevar los datos que tiene actualmente a una base robusta»*.
- **Por qué importa:** si es Access, `BR-23` (el % de productividad) **puede estar dentro del archivo**, y la carga inicial es un script de instalación, no un componente.
- **Sugerencia:** Cliente (pedir **una copia del archivo**, no un diccionario de datos).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-19` · Cifrado y llave por empresa: el cliente dijo que no

- **Cliente (`B4`):** dijo **NO** a respaldos cifrados, a llave por empresa y a registrar todo acceso técnico. `A5`: dijo que no a cifrar el dispositivo.
- **`ADR-012`:** llave por empresa **+ copia de custodia fuera de línea**, doble control, *«se decide en contra de esa respuesta porque es lo que sostiene el argumento comercial»*.
- **Cliente, S1:** *«¿cómo me va a controlar que usted no le da la información mía a otro?»* — la objeción que él mismo predijo.
- **Abierto:** cómo se avisa al administrador cada uso de la copia de custodia (**la notificación no está definida**); y es cláusula de contrato.
- **Sugerencia:** Consenso (defenderlo con su propia cita de S1) + Cliente (ratificar).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

### `X-20` · El cliente habló de «migrar a la nube», y entregamos local-first

- **Cliente, S1:** *«la idea es que ellos migren a la nube […] porque el servidor […] necesita un ingeniero de sistemas ahí»*; *«ustedes ofrecerán 10 USD por mes por usuario»*.
- **Cliente, S1 y S2:** offline *«como los sistemas de rutas de los camiones del correo»*; *«sí o sí hay que seguir trabajando»*.
- **`A20`, `B6`, `ADR-001`:** local-first; el motivo del servidor propio era **continuidad sin internet**.
- **Y la consecuencia que él mismo nombró:** local-first **sí necesita un ingeniero de sistemas en la finca** (`C9`: el administrador es empleado del cliente).
- **Por qué importa:** un arquitecto que lea S1 va a preguntar *«¿no pidió nube?»*.
- **Sugerencia:** Consenso + Cliente (confirmar que la finca tiene o tendrá ese ingeniero).
- **Resolución:** ☐ Consenso ☐ Juan ☐ Jerónimo ☐ Cliente · Fecha: ____ · Frase acordada: ______________________

---

# PARTE II · Sesiones de preguntas, paso por paso

> **Orden recomendado:** una sesión por encuentro, en orden. No pasar a la siguiente con más de dos `✘`.
> Las preguntas marcadas **⚑** tocan una contradicción del tablero: **no se dan por respondidas hasta
> cerrar la `X-nn` correspondiente.**

| Sesión | Tramo | Preguntas |
|---|---|:--:|
| `S1` | El negocio y el problema | 12 |
| `S2` | El levantamiento y la calidad de la evidencia | 10 |
| `S3` | Los drivers: atributos, restricciones, requisitos, escenarios | 11 |
| `S4` | Modelo de entrega y alternativa de arquitectura | 10 |
| `S5` | Antes y durante la captura en campo | 11 |
| `S6` | Sincronización, identidad del dato y tiempo | 11 |
| `S7` | Modelo de datos, correcciones, cierre y retención | 11 |
| `S8` | Reglas y paquete de configuración | 9 |
| `S9` | Proyección, consulta y tableros | 10 |
| `S10` | Seguridad, aislamiento, cifrado y respaldo | 10 |
| `S11` | Operación: instalación, actualización, interoperabilidad, IA | 10 |
| `S12` | Tecnología, pruebas, plan de construcción y lo que falta | 12 |

**Registro de práctica** (copiar por sesión):

| Fecha | Sesión | Preguntó | Respondió | ✔ | ~ | ✘ | Preguntas a repasar |
|---|---|---|---|:--:|:--:|:--:|---|
| | | | | | | | |

---

## `S1` · El negocio y el problema

**1.01 · ¿Qué es FlorLogic en una frase?**
Un sistema que captura en campo, sin conexión, la siembra y el corte de flor por cama; proyecta
cuánta flor habrá, de qué variedad y en qué fecha; y mide la desviación entre lo proyectado y lo
cortado. **[decidido]** · `DRIVERS §1`

**1.02 · ¿Quién es el cliente y cuántas personas hablaron por él?**
Una finca de flores de corte; **una sola persona**: el director de producción, con ~30 años en el
proceso. **[cliente]** · `VOZ §0.2`

**1.03 · ¿Qué tamaño tiene la finca?**
15 ha · 25 bloques · ~1.525 camas · jerarquía finca → bloque → nave → cama → variedad · 9 variedades
activas en una etapa. **[cliente, S1-Q P04, P10 y S2]** — `[!]` si nave y bloque son lo mismo no se sabe (`D11`).

**1.04 · ¿Cuántos usuarios?**
3 capturan (supervisor + 2 auxiliares), ~12 usarían el sistema, ~20 solo consultan (vendedoras).
**[cliente, S2]** — el propio cliente corrigió «12 para ingreso» a 3 en la frase siguiente.

**1.05 · ¿Cómo se trabaja hoy?**
Lápiz y papel ~1 h/día en campo; un practicante rotativo digita ~4 h una vez por semana; el dato
tarda ~8 días en llegar a planeación; la proyección se ajusta a mano, restando porcentajes tras ir a
mirar la cama. **[cliente, P18, P28, S2]** ⚑ `X-01`

**1.06 · ¿Qué le cuesta eso al negocio?**
2 % de error que llega a la proyección; ventas ~6 % por debajo del presupuesto contra lo cortado; ~8 %
de las ventas cubiertas comprando a terceros, más órdenes canceladas. **[cliente, S2]**

**1.07 · ¿De dónde viene el 2 % de error?**
De la captura y de la digitación — el cliente dijo que *«ocurre cuando se digitaliza y también cuando
la persona […] captura la primera vez»*. `C8` lo identificó como error de transcripción, que
**desaparece por diseño** sin digitación. **[cliente + decidido]** — `[!]` ojo: el error en la captura
no desaparece solo; lo ataca la validación en la cama.

**1.08 · ¿Cuál es la métrica de éxito?**
Tallos cortados contra tallos proyectados, con banda **±10 %** sobre la proyección (900 → 810 a 990).
El sistema no maneja dinero. **[cliente, S2 · decidido, `A19`, `DEC-07`]**

**1.09 · ¿Qué dijo el cliente que haría inútil el sistema?**
*«CERTEZA DE LOS DATOS, QUE SE INGRESEN LOS DATOS CORRECTAMENTE»* — y *«el sistema perdería todo su
valor si en fechas importantes falla»*. **[cliente, caracterización y S1-Q P40]**

**1.10 · ¿Por qué no basta con capturar más rápido?**
Porque el dolor está en el traslado papel→digital y en la reconciliación hasta que la proyección sirve.
La velocidad de captura queda como **piso de adopción**. **[decidido, reencuadre del 8-sep]**

**1.11 · ¿Qué hay fuera del alcance?**
Precios y ventas, cruce con pedidos, Florverde y certificaciones automatizadas, personal e insumos,
poscosecha, clima, **la app de plagas** (sigue viva y no se toca), fotos. **[cliente + decidido]** · `DRIVERS §1.2`, `CN-19`

**1.12 · ¿Por qué ahora y para cuándo?**
Porque *«las fincas están más organizadas y necesitan datos más certeros»*; entrega **mayo de 2027**,
tras la temporada alta de marzo-abril; despliegue sin retrasar la operación más de **7 días**.
**[cliente, P05, `CN-01`, `CN-07`]**

---

## `S2` · El levantamiento y la calidad de la evidencia

**2.01 · ¿Qué fuentes tienen del cliente?**
S1 grabada (27-jul), S1-Q escrita (46 preguntas, 40 con respuesta), S2 grabada (4-ago), caracterización
de 262 preguntas y una planilla real de captura. S3 y S4 son internas del equipo. **[hecho]** · `VOZ §0`

**2.02 · ¿Cómo distinguen lo que dijo el cliente de lo que interpretaron?**
Marcas `CIT` (cita verificable), `INF` (inferida), `ESC` (escrita, la más fuerte). Las transcripciones
de Teams no separan hablantes. **[hecho]** · `VOZ §0.1`

**2.03 · ¿Cuál es la mayor debilidad del levantamiento?**
Una sola voz, y **planeación —donde nace la proyección— nunca se exploró** porque el cliente cerró la
puerta. Él mismo calificó su contexto en 5/10. **[cliente]** · `VOZ §0.2–0.3`, `D19`

**2.04 · ¿Se han equivocado leyendo al cliente?**
Sí, dos veces: `B7` (*«no deja ingresar el último registro»* significaba el más viejo) y `B11` (un NO
que era sobre la captura, no sobre la consulta). Desde entonces las notas literales se confirman.
**[decidido]**

**2.05 · ¿Qué es el mini QAW y qué produjo?**
El taller de atributos: 13 atributos, trade-off, priorización por tres actores, 262 preguntas de
caracterización, Top 65 y 65 escenarios. Todo en `EscenariosCalidad.xlsx`. **[hecho]** · `DRIVERS §7`

**2.06 · ¿Cómo pasaron de 262 preguntas a 65 escenarios?**
Cada rol repartió 65 puntos entre 29 preguntas según la importancia de cada atributo para ese rol y el
consenso de SÍ; las 65 con más puntaje se volvieron `ESC-01`..`ESC-65`, conservando el número del
puesto. **[hecho]** · `DRIVERS §7–§9` — `[!]` hay dos Top 65 y 21 preguntas difieren; manda el del libro.

**2.07 · ¿Cuántas contradicciones reunieron y cómo las trabajaron?**
71 entradas en cinco grupos (A cliente/cliente, B cliente/equipo, C equipo/equipo, D nunca preguntado,
E consecuencias del modelo), decididas en cinco rondas. **53 decididas, 18 abiertas — todas del grupo
D, del cliente.** **[hecho]** · `3_DECISIONES §Índice`

**2.08 · ¿Qué decisiones tomaron en contra del cliente?**
Siete: cifrar en el dispositivo (`A5`), todo visible dentro de la empresa con filtros (`A8`),
sincronización degradable (`A10`), cierre de periodo (`A11`), dos tableros de avance (`B12`), una guía
corta (`B13`), y la IA (`C2`, ya superada por `ADR-036`). Además `B4`: respaldos cifrados y llave propia.
**[decidido]** · `DRIVERS §11.2` ⚑ `X-09`, `X-19`

**2.09 · ¿Qué le falta preguntar al cliente — lo más importante?**
`D1` el proceso de captura a detalle (una sesión), `D2` los documentos prometidos, `D3` el % de
productividad por variedad y el reparto del corte. **[no sabemos]**

**2.10 · ¿Cómo piensan hacer esa sesión?**
Siguiendo **una cama real** de principio a fin con los formatos físicos sobre la mesa, y repetir en diez
minutos con una cama erradicada; y pedir **el archivo con el que hizo la última proyección** en vez de
«danos el porcentaje». **[propuesta]** · `3_DECISIONES §Instrucciones 4`

---

## `S3` · Los drivers

**3.01 · ¿Cuáles son los atributos de calidad y cuál manda?**
13. **Confiabilidad es 1 para los tres actores** —único consenso total—; siguen Disponibilidad,
Rendimiento y Capacidad para ser Auditado (empatados), Capacidad. **[decidido]** · `DRIVERS §6.1`

**3.02 · ¿Qué significa aquí «Confiabilidad»?**
Que el dato entre correcto y no se pierda ni se dañe ante fallos (absorbió «seguridad de
funcionamiento»). Se mide con pérdida cero, rechazo en el dispositivo y desviación. **No** como «error
menor a X %», porque el cliente respondió *«NO PUEDE HABER ERRORES»*. **[decidido]** · `DRIVERS §5, §10.2`

**3.03 · ¿Por qué Escalabilidad no es driver?**
Porque cada instalación carga una finca, nunca la suma de todas; el pico de temporada se reparte sobre
~10 personas por instalación. **[decidido]** · `DRIVERS §6.2`, `CN-30`

**3.04 · ¿Cuál es la restricción rectora?**
`CN-13`: offline-first — captura, validación, autenticación y fecha funcionan íntegramente en el
dispositivo. Nace de `CN-17`: no hay red en el cultivo. **[decidido + cliente]**

**3.05 · ¿Y la restricción de arquitectura más importante?**
`CN-36`: los campos capturados son **datos, no columnas** — agregar una labor no puede exigir migración
de esquema. Con N instalaciones en casa de clientes, migrar es lo más caro. **[propuesta, EN DUDA formalmente]**

**3.06 · ¿Cuáles son los números firmes del proyecto?**
Pérdida cero · fallo máx. 1 h · restauración máx. 1 día · mantenimiento en mayo · ≤10 personas
sincronizando · pico +60 % · ventana ≥15 días · ±10 % · 2 % de error · entrega mayo 2027.
**[cliente]** · `DRIVERS §10.1` ⚑ `X-07`

**3.07 · ¿Qué números de los escenarios son inventados?**
Los que no están en `§10.1`: por ejemplo la latencia objetivo de 1 hora (`ESC-05`, `ESC-60`), los
5 años en línea, los 5 segundos de `ESC-12`. `ADR-020` §4 los manda marcar `PENDIENTE`. **[supuesto]** ⚑ `X-06`

**3.08 · ¿Cuántos requisitos funcionales significativos y cuáles son los críticos?**
21. Críticos: siembra y corte sin conexión (`RF-001`/`002`), sincronizar sin perder ni duplicar
(`RF-003`), rechazo del evento imposible con motivo (`RF-004`/`005`), cálculo de tallos (`RF-006`),
desviación (`RF-011`), frontera de empresa (`RF-012`), parametrización (`RF-013`). **[decidido]** · `DRIVERS §2`

**3.09 · ¿Por qué no hay requisitos por «esquejes» o por área?**
Porque la planilla real de la finca no tiene área ni densidad: usa `#LÍNEAS` y `CANTIDAD`. `DEC-14`:
nada se cuenta por esqueje; la unidad y la agrupación son configurables por empresa. **[decidido + planilla real]**

**3.10 · ¿Hay escenarios que contradicen decisiones vigentes?**
Sí: `ESC-08`, `ESC-58` (motivo y autorización), `ESC-34` (pide resolución), `ESC-46` (5 min),
`ESC-54` (0 perdidos), `ESC-29`, `ESC-12`. Siete correcciones pendientes, tres ya aplicadas. **[hecho]** ⚑ `X-02`, `X-04`, `X-07`

**3.11 · ¿Por qué Accesibilidad no tiene escenarios?**
Quedó 13 y ninguna de sus preguntas entró al Top 65. Y el cliente respondió SÍ a casi todo lo que
preguntaba por ella: probablemente no entendió el término. La facilidad de uso entra como Experiencia
de Usuario. **[decidido]** · `DRIVERS §5, §9.1`

---

## `S4` · Modelo de entrega y alternativa de arquitectura

**4.01 · ¿Por qué no SaaS?**
El cliente necesita operar sin internet (`A20`); 10 USD/usuario/mes con 3 capturadores ≈ 230 USD/mes;
el umbral de 20 empresas nunca se validó; no hubo estudio de mercado; operar 24×7 con dos ingenieros no
estaba costeado. `DEC-01` derogada por `ADR-001`. **[decidido]** ⚑ `X-20`

**4.02 · Entonces, ¿qué se entrega?**
Local-first: una instalación por empresa en un nodo de la finca, que opera sin internet; la nube presta
respaldo, actualización y servicios, fuera del camino crítico. **[decidido, `ADR-001`, `CN-37`]**

**4.03 · ¿Cuánto cuesta?**
~20.000 USD por instalación **[cliente, `CN-02`]** + 100–200 USD/mes de servicios **[supuesto, sin
costear — `E2`]**. Actualización en línea para quien paga la mensualidad. **[no sabemos]** qué pasa con
quien deja de pagar (diverge de versión).

**4.04 · ¿Qué alternativas evaluaron?**
ALT-1 monolito modular por instalación · ALT-2 microservicios · ALT-3 Backend-as-a-Service · ALT-4
código propio por finca. Se eligió ALT-1. **[decidido]** · `alternativa §2`

**4.05 · ¿Por qué no microservicios?**
Dos personas sin experiencia operativa (`CN-06`): es gastar el presupuesto en infraestructura en vez de
en el dominio; y `CN-35` prohíbe costos fijos por servicio. **[decidido]**

**4.06 · ¿Por qué no Firebase/Supabase, si resuelve la sincronización?**
Descalificatorio: **no corre en la finca** (`CN-37`). Además cobra por usuario activo, la llave no sería
nuestra, y su resolución de conflictos no es la que exigimos. La nube de servicios sí podría usar algo
gestionado. **[decidido]**

**4.07 · ¿En qué se diferencia ALT-1 de ALT-4, si las dos instalan por finca?**
En cuántos productos hay: ALT-1 es uno solo instalado N veces; lo que varía entre fincas son datos
(catálogo, reglas, parámetros). Con ALT-4 la novena finca es el noveno proyecto. **[decidido]**

**4.08 · ¿Cuál es la idea rectora de la arquitectura?**
*«El teléfono es el sistema de registro mientras hay jornada; el servidor lo es cuando hay red.»* Una
caída se vuelve retraso, no parada. **[decidido]** · `alternativa §3.1`

**4.09 · ¿Cuáles son los contenedores?**
Aplicación de captura + almacén del dispositivo (teléfono) · API Gateway, Backend, Base de datos de la
empresa y caché (servidor de la finca) · Aplicación web de consulta (computador de oficina) · Servicios
en línea y custodia de respaldos (nube). Externos: BI del cliente, sistema heredado, app de plagas.
**[propuesta — modelo C4]** ⚑ `X-16`

**4.10 · ¿Qué administra el cliente y qué el equipo FlorLogic?**
La instalación la administra el **ingeniero de sistemas de la finca**, empleado del cliente (`C9`). El
equipo solo opera la capa de servicios en línea, sobre datos cifrados (`CN-34`), y no toca datos de
producción. **[decidido]**

---

## `S5` · Antes y durante la captura en campo

**5.01 · ¿Qué hace el teléfono antes de salir al cultivo?**
Descarga el paquete de configuración de la empresa y la asignación del día; si falta algo, avisa
**antes de salir**, nunca en medio del campo. **[decidido, `ADR-015`, `ADR-029`, `ADR-026`]**

**5.02 · ¿Qué es la asignación y para qué sirve?**
Qué camas y bloques le tocan a ese dispositivo y en qué jornada. Es el **denominador**: lo esperado menos
lo recibido es lo que falta. Sirve para el avance, para lo que lleva mucho sin sincronizar y para la
lista de camas a rehacer si se pierde un teléfono. **[decidido, `ADR-026`]** — `[!]` hay que poder
cerrar una cama como «visitada sin novedad» o el tablero avisa en falso.

**5.03 · ¿Qué teléfono y qué tecnología?**
**Aplazado a propósito** (`ADR-008`): se sigue con la aplicación web. Se pasa a app instalada solo si la
ventana sin abrir la app llega a una jornada, si el cliente exige demostrar el cifrado, o si hay iPhone
reales. No se sabe qué celulares tienen los capturadores (`CN-21`). **[decidido + no sabemos]** ⚑ `X-16`

**5.04 · ¿Qué se captura?**
Siembra, corte, bajas y erradicaciones por cama o sección: variedad, fecha, cantidad en la unidad y
agrupación de la empresa (líneas, densidad, unidades). Se admiten registros incompletos. **[decidido, `RF-001`/`002`/`009`]**

**5.05 · ¿Cuál es la unidad de captura?**
La **cama completa de una sentada**, aunque el dato aterrice en la sección (hay camas divididas: 3 de 18
filas en la planilla real). **[propuesta, `ADR-024`]** ⚑ `X-15`

**5.06 · ¿Cómo sabe el teléfono qué cama es?**
Por el catálogo local; la demo usa escaneo de la cama. `[!]` **Generar, imprimir y reemplazar los QR de
cama no tiene ADR**, y la nomenclatura de camas (`D13`) —«la llave primaria de todo»— no se preguntó.
**[no sabemos]**

**5.07 · ¿Cómo entra la persona sin red?**
Identidad individual; al sincronizar recibe credencial con permisos y vigencia acotada; sin red se
desbloquea con PIN, biometría u otro método rápido; cierre a los 15 min de inactividad. **[decidido, `ADR-007`]** ⚑ `X-13`

**5.08 · ¿Qué pasa si ingresa un dato imposible?**
Se rechaza en el teléfono, sin red, **con el motivo en lenguaje de negocio** (*nadie corta más tallos
de los que sembró*). **[decidido, `RF-004`/`005`, `ADR-006`]** ⚑ `X-10`

**5.09 · ¿Y si falta un dato obligatorio?**
No frena la captura ni la sincronización: viaja incompleto y queda como trabajo para el gerente de
producción, que decide si entra. Perder información es peor que recibirla incompleta.
**[decidido por Juan 15-sep, pendiente de ADR]** ⚑ `X-10`

**5.10 · ¿Qué pasa si el teléfono se apaga a media captura?**
Lo confirmado está en la bandeja persistente; lo que iba a medias se restaura al abrir, marcado
pendiente y fuera de la proyección. **[decidido, `ADR-002`; escenarios `ESC-11`, `ESC-18`, `ESC-55`]**

**5.11 · ¿Por qué el capturador usaría la app, si el beneficio es de la oficina?**
Es el riesgo real de adopción: quien paga el costo captura en el invernadero y quien cobra el beneficio
es la oficina. Se aborda por método (demos, feedback, cambios, `A17`) y con validación que le evita
rehacer trabajo. **Hay que devolverle algo en la cama.** **[decidido + no resuelto]** · `CN-08`

---

## `S6` · Sincronización, identidad del dato y tiempo

**6.01 · ¿Cada cuánto se sincroniza?**
Apunta a **diaria**, apenas hay red y forzable. El recordatorio escala: avisa → estorba → impide. El
umbral lo decide el negocio. **[decidido, `ADR-025`; umbral no sabemos]**

**6.02 · ¿Por dónde se sincroniza?**
Por la **red local de la finca** contra el servidor de la finca, no por internet. En las oficinas hay
señal. **[decidido, `CN-17`, `alternativa §3.2`]**

**6.03 · ¿Cómo garantizan que no se duplica?**
Cada anotación nace con un identificador único generado en el teléfono; el servidor aplica por
identificador; reenviar el mismo envío no hace nada. **[decidido, `ADR-002`, `ADR-027`]**

**6.04 · ¿Y si llega el mismo identificador con contenido distinto?**
No es reenvío: es corrupción o un error de programa. Se rechaza, se registra y se avisa; nunca se
sobrescribe. **[decidido, `ADR-027`]**

**6.05 · ¿Qué es la «clave del hecho»?**
Producción, sección, campo y jornada: responde si dos anotaciones hablan del mismo dato del mundo. **No
incluye autor ni dispositivo**, para que un choque no se vuelva invisible. **[decidido, `ADR-027`]** —
`[!]` qué es «la jornada» (día, turno, visita) **no se sabe** (`D1`); por eso `SPK-12` quedó aplazado.

**6.06 · ¿Qué pasa cuando dos personas anotan lo mismo?**
Entran las dos y gana la más reciente, automático, con aviso a quien capturó. **[decidido, `RF-022`]** ⚑ `X-02`

**6.07 · ¿«Más reciente» según qué reloj?**
Según una **hora corregida** con el desfase medido de ese teléfono. Se guardan tres: la del teléfono
(intacta), la corregida (decide choques) y la del servidor. Empate → por identificador. **[decidido, `ADR-031`]**

**6.08 · ¿Por qué no usar simplemente la hora del teléfono?**
Porque medimos que **el teléfono con la hora adelantada gana el 100 % de los choques** y los demás
datos desaparecen sin error. **[medido, `SPK-12`; decidido, `ADR-031`]**

**6.09 · ¿Y si el reloj está alterado?**
Se marca con las dos horas y se pide confirmación; **no se bloquea**, porque un bloqueo sin salida en
pleno cultivo es una parada de jornada. **[decidido, `ADR-014`]** ⚑ `X-03`

**6.10 · ¿Qué queda registrado de cada sincronización?**
Dispositivo, persona autenticada, inicio y fin según el servidor, desfase, versión de configuración con
que venía y la que se le entregó, anotaciones entradas y rechazadas, y cómo terminó. Si se interrumpe,
se cierra como parcial. **[decidido, `ADR-028`]** ⚑ `X-05`

**6.11 · ¿Qué pasa si se pierde un teléfono con capturas?**
No se recupera. El servidor sabe qué esperaba de ese dispositivo y genera **la lista exacta de camas a
volver a caminar**. Funciona si el dato es reciente: un corte no se recaptura. **[decidido, `ADR-025`, `ADR-017`]** ⚑ `X-07`

---

## `S7` · Modelo de datos, correcciones, cierre y retención

**7.01 · ¿Cómo se guarda un dato capturado?**
Cada anotación es una fila: producción, sección, campo, valor, momento de entrada, versión del paquete
con que se validó y a cuál corrige si corrige. Jerarquía y catálogo son tablas normales; **solo el valor
va flexible**. **[propuesta, `ADR-024`]** ⚑ `X-15`

**7.02 · ¿Qué cuesta ese modelo?**
La base deja de validar tipos (los valida el motor de reglas); una pantalla de 20 campos es un giro
sobre 20 filas; las filas se multiplican. A cambio: dos capturadores no chocan, sincronizar es insertar,
y agregar un campo no migra N bases. **Ninguna pantalla se construye sobre esas filas.** **[propuesta]**

**7.03 · ¿Qué ve el usuario: el historial o el último valor?**
El último valor conocido por campo con su fecha: si el 24 se capturó x, y, z y el 25 solo x, y →
**x, y (25) · z (24)**. No se busca completitud. **[decidido, `A15`, `RF-016`]**

**7.04 · ¿Quién lleva la trazabilidad?**
La **sesión de captura**: en este momento, esta persona, estas camas. Los datos cuelgan de ella en
grupos; de una proyección que falla se baja a la cama y se sube a la sesión y la persona. **[decidido, `ADR-035`]** ⚑ `X-05`

**7.05 · ¿Se puede corregir? ¿Cuesta algo al capturador?**
Sí; mientras la producción está abierta se guarda cada modificación y se puede devolver. Al capturador
no se le pide motivo ni autorización: la autoría ya viaja. **[decidido, `ADR-020`; cliente: «SOLO LA CORREGIDA»]** ⚑ `X-04`

**7.06 · ¿Y los cambios grandes?**
Se controla por magnitud, no por rol: lo puntual se aplica y queda en bitácora; lo drástico (erradicar
bloques) pide confirmación de un segundo administrador, como aviso, sin detener la operación. **[decidido, `ADR-032`]**
— **[no sabemos]** la lista de acciones drásticas ni qué pasa si nadie confirma.

**7.07 · ¿Qué es el cierre de producción?**
El momento en que el administrador declara terminado un ciclo; ahí el estado se consolida y los totales
se calculan una vez para siempre. **[decidido como concepto]** — **[no sabemos]** quién lo dispara, qué
habilita y qué consolida (`T6`). ⚑ `X-17`

**7.08 · ¿Cuánto tiempo se guarda?**
Todo, cinco años, sin degradar detalle; los cinco años son el plazo para decidir con volumen real, no
para borrar. **[decidido, `ADR-022`]** ⚑ `X-06`

**7.09 · ¿Cuánto ocupa?**
Del orden de **decenas de GB por finca en cinco años** (estimación ~24 millones de eventos, 20–25 GB con
índices). Como cada empresa tiene su instalación, no se suma. **[supuesto]** — `[!]` el dimensionamiento
que se apoyaba en «conjunto activo acotado» hay que rehacerlo (`ADR-033` Rechazada). Lo único que lo
rompería: **fotos**.

**7.10 · Entonces, ¿cuál es el riesgo del almacenamiento?**
No el espacio: **el tiempo de consulta** sobre la acumulación. **[decidido, `ADR-022`, `ADR-023`]**

**7.11 · ¿Por qué rechazaron separar lo activo de lo histórico?**
Porque la premisa era falsa: una finca puede tener más producciones que camas ocupadas (planeadas,
camas nuevas, producciones que crecen). La rapidez de lo activo la da un mecanismo, no la forma del
negocio. **[decidido, `ADR-033` Rechazada → `ADR-034`]**

---

## `S8` · Reglas y paquete de configuración

**8.01 · ¿Dónde viven las reglas?**
En un artefacto versionado en **JSON**, no en código; el mismo lo interpretan teléfono y servidor.
Cambiar una regla no exige publicar la aplicación. **[decidido, `ADR-006`; formato JSON, decisión del 15-sep]**

**8.02 · ¿Qué es el paquete de configuración?**
Catálogo + reglas + parámetros publicados juntos, inmutables, con **un solo número de versión por
empresa**, en un solo archivo. La versión de la app va aparte; la asignación diaria tampoco entra.
**[decidido, `ADR-029`]**

**8.03 · ¿Por qué uno y no tres versionados?**
Reproducir una validación pasa de acertar tres versiones —fallando en silencio— a citar una: exacto o
imposible, nunca casi. El costo: cambiar un parámetro sube toda la versión, y el paquete lleva un
resumen de qué cambió. **[decidido, `ADR-029`]**

**8.04 · ¿Cuántas reglas duras hay documentadas?**
Pocas: tallos ≤ plantas (`RF-005`) y el evento imposible en el ciclo (`RF-004`); `PoC-0` tiene 9 reglas.
**El catálogo completo no se ha levantado** — es `D1`. **[no sabemos]**

**8.05 · ¿Qué encontraron las reglas sobre datos reales?**
En la planilla real, con tolerancia del 2 % sobre plantas por línea, 9 filas marcadas, entre ellas
`Cortona`/`Cartona`; `Astroi` y `Rose` con tres razones distintas el mismo día. **El 2 % es decisión
nuestra, no del cliente** (`D18`). **[medido, `PoC-0`]**

**8.06 · ¿Qué pasa si una regla tiene un tipo que el motor no conoce?**
Medimos que **cierra la cama como si no existiera, sin error**. Por eso el validador del paquete
(`AB-01`) es imprescindible. **[medido, `SPK-09`]**

**8.07 · ¿Teléfono y servidor pueden decir cosas distintas?**
En el veredicto no divergieron (0 %). En el **motivo** sí: 1,95 %, y el modo de redondeo de cada
lenguaje puede cambiar el número mostrado. **[medido, `SPK-09`, `SPK-17`]** ⚑ `X-11`

**8.08 · ¿Cómo se sabe con qué reglas se validó un dato de hace meses?**
Cada anotación guarda la versión del paquete; el servidor también registra con qué versión operaba el
teléfono al sincronizar. **[decidido, `ADR-024`, `ADR-028`]** — `[!]` la rotación de llaves a 5 años
puede romper esa reproducibilidad (`AB-04`).

**8.09 · ¿Cada finca tiene su propia plantilla de captura?**
No es un diseñador libre: una plantilla común amplia de la que cada empresa **activa las columnas que
usa** (*«la plantilla tiene 20 columnas; yo solo uso 5»*). **[cliente + decidido, `A14`, `RF-013`]**

---

## `S9` · Proyección, consulta y tableros

**9.01 · ¿Cómo se calcula la proyección?**
Tallos proyectados = lo sembrado (en la agrupación de la empresa) × **% de productividad esperada de la
variedad**; nunca más del 100 %. Ej.: 1.000 plantas al 90 % → 900. **[cliente, S2; `RF-006`]**

**9.02 · ¿De dónde sale el % de productividad por variedad?**
**No lo sabemos.** Nunca se preguntó. Sin él, la proyección no arranca. Puede estar en el sistema
heredado. **[no sabemos — `D3`/`BR-23`]** ⚑ `X-18`

**9.03 · ¿Cómo se reparten los tallos en los días de corte?**
**No lo sabemos** (`RF-007`, `D3`). El corte dura ~7 días. **[no sabemos]**

**9.04 · ¿Cada cuánto se actualiza la proyección?**
Cálculo vivo con cada sincronización + versión publicada congelada. Semanal es el piso. **[decidido, `ADR-005`]** ⚑ `X-08`

**9.05 · ¿Por qué congelar versiones?**
Si todo se recalcula, la desviación tiende a cero porque se compara contra una proyección que ya
absorbió el dato real. Y sobre una proyección emitida ya se vendió. **[decidido, `ADR-005`, `RF-023`; cliente: «si no se modifica sí»]**

**9.06 · ¿Cómo se mide el acierto?**
Desviación entre corte real y proyectado por cama, variedad y periodo, contra la versión vigente en su
momento, marcando lo que sale de ±10 %. Agregado por día, semana y mes desde el mismo cálculo.
**[decidido, `RF-011`, `RF-018`]**

**9.07 · ¿Sobre qué se construyen los tableros?**
Nunca sobre los hechos crudos: sobre tablas de consulta derivadas, totales precalculados por nivel y
carga progresiva; y un caché con las producciones activas. **[decidido `010`/`023`; propuesta `034`]** ⚑ `X-14`

**9.08 · ¿Qué tableros se construyen?**
Los reportes que la finca ya consume (línea base), avance del día y lo que falta por capturar/sincronizar,
y la vista de camas con % de producción **solo en consulta**. Lo demás se negocia. **[decidido, `ADR-010`, `B12`, `RFP-03`]**
— el cliente propuso en S1 notificaciones y un **mapa de calor** del corte.

**9.09 · ¿Todos ven todo?**
Dentro de la empresa sí, con filtros por panel (`A8`, en contra de una respuesta escrita del cliente).
Los permisos controlan **qué se puede hacer**, no qué se ve. **[decidido, `CN-12`]**

**9.10 · ¿Qué tan rápido es consultar cinco años?**
Lo cerrado se lee de totales ya calculados; lo que nadie abre no se calcula. **El número objetivo no
existe**: los 5 s de `ESC-12` no salen de ninguna fuente. **[decidido el mecanismo · no sabemos el número]** ⚑ `X-06`

---

## `S10` · Seguridad, aislamiento, cifrado y respaldo

**10.01 · ¿Cómo se aísla una empresa de otra?**
Tres capas: instalación y base propias (física); conexión que solo abre la base de esa empresa; permisos
por par (rol, empresa). En la capa en línea: cifrado con la llave del cliente y **discriminador de
empresa en toda consulta con prueba automatizada**. **[decidido, `ADR-003`, `RF-012`]**

**10.02 · Si cada empresa tiene su base, ¿para qué el discriminador?**
Porque la capa en línea es compartida, y porque convierte un futuro despliegue compartido en un
despliegue y no en una reescritura. **[decidido, `E3`]**

**10.03 · ¿Encontraron algún agujero?**
Sí: con seguridad por fila puesta, **una credencial de la empresa 1 leyó 239.200 filas de la empresa 2**
a través de vistas materializadas. El arreglo está probado a mano; **la prueba automática no existe**
(`AB-02`). **[medido, `SPK-16`]**

**10.04 · ¿Qué base legal tiene el aislamiento?**
Secreto empresarial, art. 260 de la Decisión 486 de la CAN (`CN-03`). Y la objeción comercial del
cliente: *«¿cómo me va a controlar que no le da mi información a otro?»*. **[cliente + decidido]**

**10.05 · ¿Quién tiene la llave del respaldo?**
Cada empresa. El equipo guarda una **copia de custodia fuera de línea**, dos ejemplares en sitios
separados, doble control, registro físico; solo ante excepción declarada (se perdió el servidor), con
aviso al administrador y en el contrato. **[decidido, `ADR-012`]** ⚑ `X-19`

**10.06 · ¿Entonces el proveedor puede leer los datos?**
No es imposible técnicamente: es una **barrera física y de procedimiento**, mucho más fuerte que una
llave única del proveedor. **Eso no se le afirma al cliente como imposibilidad.** **[decidido, `ADR-012`]**

**10.07 · ¿Dónde va el cifrado en reposo?**
En el volumen, no dentro de la base: cifrar dentro de la base costó 14× en lectura y mata el índice;
cifrar el respaldo costó 0,68 s. Custodia: solo 2-de-3 resiste. **[medido, `SPK-13`]** — `[!]` falta
probar LUKS en el nodo real (`AB-03`) y la rotación de llaves (`AB-04`).

**10.08 · ¿Cuánto tarda restaurar?**
Objetivo: máximo 1 día (`CN-15`) **[cliente]**; verificación mensual por empresa en entorno aislado
(`ESC-19`) **[supuesto]**.

**10.09 · ¿Cómo se avisa al administrador cuando se usa la custodia, o al capturador cuando se descarta su dato?**
**No está definido.** Varias decisiones exigen avisar (`ADR-012`, `RF-022`, `ADR-025`, `ADR-026`) y
ninguna dice por qué canal, con qué acuse ni en qué plazo. **[no sabemos]**

**10.10 · ¿Qué ve el equipo FlorLogic de cada finca?**
Solo salud y conteos, sin nombres de camas ni cifras de negocio: una lista de camas con cifras es
información del cliente y no sale de la finca. **[decidido, `ADR-026`, `CN-34`]**

---

## `S11` · Operación: instalación, actualización, interoperabilidad, IA

**11.01 · ¿Cómo se instala en una finca?**
Un solo artefacto empaquetado + infraestructura como archivo; lo propio de cada entorno en
configuración. Lo que se instala en la novena finca es lo mismo que en la primera. **[decidido, `ADR-016`]**
— **[medido]** instalar el software ~2,5 s (sin sistema operativo ni PostgreSQL).

**11.02 · ¿Qué es físicamente el servidor de la finca?**
**No lo sabemos.** Depende del sistema heredado (`CN-20`) y de si la IA local entra (16 GB de memoria
según `ADR-036`). **[no sabemos]**

**11.03 · ¿Cómo se actualiza la app sin recoger teléfonos?**
Dos canales: paquete de configuración verificado antes de capturar; la app al reconectar, subiendo
primero lo pendiente y migrando después el almacén local. **[decidido, `ADR-015`]**

**11.04 · ¿Y cómo se migra el esquema en N fincas?**
Migraciones automatizadas y verificables desde el andamiaje inicial (`CN-29`). **[medido]**: 39 versiones
de atraso se recuperan enteras; no se pierde dato al actualizar en caliente, **pero la app vieja queda
28 de 40 s sin poder escribir**; `lock_timeout` baja una parada de 18 s a 2 s. **[no sabemos]** qué pasa
con la finca que deja de pagar.

**11.05 · ¿Cuándo se hace mantenimiento?**
Fallo no planificado máximo 1 hora; mantenimiento con parada en **mayo**, temporada baja. **[cliente, `A2`]**

**11.06 · ¿Cómo lee el cliente su información con Power BI?**
Con un usuario de base de datos de **solo lectura** contra las vistas publicadas, nunca los hechos
crudos, dentro de la red de la finca: nada expuesto a internet. **[decidido, `ADR-013`]** —
**[medido]** un año en 68 s de un presupuesto de 600.

**11.07 · ¿Hay API pública?**
No. Exportación a Excel/PDF con los mismos permisos que la pantalla, y lectura directa para el BI.
Escritura desde afuera e integración con terceros quedan fuera. **[decidido, `ADR-013`, `ADR-017`]**

**11.08 · ¿Qué se usa para trabajos en segundo plano?**
Una cola **dentro de la misma base**, sin pieza aparte; se adopta una herramienta dedicada solo si la
medición muestra que la base no aguanta el pico de una finca. **[propuesta, `ADR-009`]**

**11.09 · ¿Dónde está la IA?**
Opcional. Local ligero en la finca para señalar lo raro, contradictorio o desactualizado; generativo en
línea solo para documentos; nada en el teléfono. **[decidido, `ADR-036`]** — **[no sabemos]** qué
información puede salir al servicio en línea. ⚑ `X-09`

**11.10 · ¿Qué no se construye?**
Microservicios, bodega aparte, broker, API de escritura, pasarela de pago en fase 1, alta disponibilidad
duplicada, recuperación de teléfonos, almacenamiento por antigüedad, plantillas libres, reemplazo de la
app de plagas. Cada una con su condición de reapertura. **[decidido, `ADR-017`]**

---

## `S12` · Tecnología, pruebas, plan de construcción y lo que falta

**12.01 · ¿Qué es `PoC-0`?**
`app-captura/`: aplicación web offline, sin dependencias de npm, desechable a propósito. Recorre una
cama dividida, choca con una regla, sincroniza, resuelve un conflicto y corre con la red apagada. Seis
suites en verde. **[hecho]**

**12.02 · ¿`PoC-0` implementa la arquitectura decidida?**
No del todo: **contradice cuatro decisiones vigentes** de identidad y tiempo, y hay que corregirlo antes
de medir sobre él. **[hecho]**

**12.03 · ¿Qué spikes se midieron?**
`SPK-09` motor de reglas · `SPK-12` ingreso y sincronización (aplazado) · `SPK-13` cifrado y respaldo ·
`SPK-14` paquete de configuración · `SPK-15` empaquetado e instalación · `SPK-16` salida al BI ·
`SPK-17` Java y C#. **Registros de IA sin revisar.** **[medido]**

**12.04 · ¿Qué concluyen?**
**El rendimiento no es la restricción en ninguno.** Lo que decide es quién puede hacer qué y qué pasa
cuando dos cosas coinciden. Y seis fallos silenciosos: reloj adelantado, tipo de regla desconocido, RLS
en vistas, redondeo entre lenguajes, recorte de .NET que no falla, imagen `jlink` que responde vacío.
**[medido]**

**12.05 · ¿Qué lenguaje y qué base de datos?**
Base: **PostgreSQL es la propuesta** en el modelo y en los spikes. Lenguaje: **no elegido**; los cinco
candidatos quedan dentro del 4 % en casi todo; la instalación varía 3–36 %; Java y C# piden runtime en el
nodo (193,6 y 66,6 MB) salvo empaquetado autocontenido. **[medido · no decidido]** ⚑ `X-11`

**12.06 · ¿Qué decide el lenguaje, entonces?**
La pregunta del motivo: si el motivo de rechazo tiene que ser idéntico en teléfono y servidor, el único
camino medido es TypeScript en los dos lados; si basta el veredicto, se abren los demás. **[no decidido]** ⚑ `X-11`

**12.07 · ¿Qué spikes quedan imprescindibles?**
`AB-01` validador del paquete (forma canónica del JSON, tipo desconocido, redondeo, enteros con
decimal) · `AB-02` prueba automática del aislamiento · `AB-03` cifrado real en el nodo · `AB-04` rotación
de llaves a 5 años. **[propuesta]**

**12.08 · ¿En qué orden se construye?**
12 tandas `T0`..`T11`: andamiaje → dominio → validación → captura → sincronización → identidad → cierre
→ consulta → operación → interoperabilidad → proyección → asistente. **[propuesta, registro de IA]**
— `T5` espera reescribir `RF-017`; `T6` espera definir el cierre; `T10` espera `BR-23`; `T11` (asistente de
captura en el teléfono) choca con `ADR-036`, que no pone IA en el teléfono. ⚑ `X-15`, `X-09`

**12.09 · ¿Qué es lo primero que construirían?**
El andamiaje con migraciones sobre N bases y discriminador de empresa desde el día uno (`T0`), y el
dominio (`T1`) — **una vez `ADR-024` pase a Aceptada**. **[propuesta]** ⚑ `X-15`

**12.10 · ¿Cuál es el mayor riesgo del proyecto?**
Tres, y hay que decirlos juntos: **la proyección depende de un número que no tenemos** (`BR-23`); **todo
descansa en una sola voz** y planeación nunca se exploró; y la **adopción en campo**, porque el
beneficio lo cobra la oficina. **[no sabemos + decidido]**

**12.11 · ¿Qué documento manda sobre qué?**
Solución y ADR: `ADR-PoC-Spikes.xlsx`. Drivers: `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx`. Estado
de decisiones de negocio: `3_DECISIONES_DE_NEGOCIO_Y_CONTRADICCIONES.md`. Cita literal: `1_VOZ_DEL_CLIENTE.md`.
Lo de IA (`IA_`) nunca manda. **[hecho]** ⚑ `X-12`

**12.12 · Si mañana el cliente dice que sí hay red en el cultivo, ¿qué cambia?**
Pregunta de trampa útil para practicar. Respuesta honesta: la idea rectora sigue valiendo (red
intermitente es red ausente a efectos de jornada), pero se podría relajar la ventana de credencial,
la presión del recordatorio y la decisión del dispositivo (`ADR-007`, `ADR-025`, `ADR-008`). **No
cambian** el modelo de datos, el paquete ni la instalación por empresa, que responden a otros drivers.
**[razonamiento del equipo — confirmarlo en consenso antes de usarlo]**
