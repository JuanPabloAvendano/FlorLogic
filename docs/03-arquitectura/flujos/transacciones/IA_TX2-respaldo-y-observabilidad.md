REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 8-oct-2026, a pedido de Juan
Estado de revisión: POR ACEPTAR — nada de este archivo vale para programar hasta que Juan lo marque ACEPTADO en `IA_00-LEEME-transacciones.md`
Manda por encima: `ADR.xlsx` (hoja ADR) · `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx` · `docs/03-arquitectura/decisiones/`

# TX-2 · Respaldar la finca en la nube, con observabilidad

**En una frase.** A la hora fijada, el nodo respalda su base y lo verifica. Lo cifra con la llave de
la empresa, guarda una copia local y sube otra a la nube, que la guarda sin poder abrirla. Durante
todo el camino cada paso deja logs, métricas y telemetría sin datos de negocio. Esa telemetría la ven
el administrador de la finca y el operador de la plataforma, cada uno en su propio plano.

Es la transacción que más contenedores toca, **7 de 9**, y casi toda la nube: **5 de 6 componentes**.

Las marcas son las mismas de `IA_TX1-sincronizacion.md`: [fuente], [modelo], [medido], [propuesta] y [PENDIENTE].

---

## 1 · Alcance

| Entra | Queda fuera, a propósito |
|---|---|
| El disparo automático y el manual («respaldar ahora») | La distribución de versiones (es la actualización) |
| El volcado, la verificación local, el cifrado, la copia local y la subida reanudable | El procedimiento físico de la copia de custodia de la llave: es papel y doble control, no software (`ADR-012`) |
| El acuse de la nube y el registro en la bitácora | La restauración completa en un nodo nuevo entra como caso de uso y tarjeta, **no** como el camino principal |
| La prueba de restauración del mes, **dentro de la finca** | El aviso por correo o push: `ADR-012` lo exige, pero el canal no está decidido |
| La observabilidad de los dos planos: tablero del administrador, panel del operador y telemetría con esquema cerrado | — |

**Juan, 8-oct:** «me gustaría que respaldo fuera acompañado de observabilidad». Por eso la
observabilidad no se trata como un efecto lateral: **es la segunda mitad de esta transacción**.

---

## 2 · El recorrido, paso a paso

| # | Qué pasa | Componente · contenedor | Marca |
|---|---|---|---|
| 1 | Se cumple el intervalo. El planificador toma el trabajo «respaldo» de la cola **guardada en la base**. Si el nodo se reinicia, el trabajo no se pierde y no corre dos veces. | Planificador de tareas · Backend | [fuente] `ESC-03` («sin intervención humana»), `ADR-009` · intervalo [PENDIENTE] |
| 1b | O el administrador pulsa «respaldar ahora» en la web. Identidad y permisos lo autoriza. | Aplicación web → Gateway → Identidad y permisos → Servicio de respaldo | [propuesta] `ESC-14`: sin consola técnica |
| 2 | Se toma el volcado de la base **sin detener la captura**. El volcado lee una foto fija de la base y no bloquea a quien escribe. | Servicio de respaldo → Base de datos de la finca | [medido] `SPK-13`: 64 s para los 5,3 GB de cinco años; sale un archivo de 478 MB |
| 3 | **Verificación local antes de cifrar:** suma de control, se lista el contenido del volcado y se comprueba la **cadena de integridad de la bitácora**. | Servicio de respaldo · Bitácora de auditoría | [fuente] `ESC-40`: «integridad de la bitácora verificable en cada respaldo» · [propuesta] la cadena |
| 4 | **Deduplicar y comprimir antes de cifrar.** El orden importa: al revés no se deduplica nada. | Servicio de respaldo | [medido] `SPK-13`: 1,62 GB a cinco años contra 813 GB de copias completas |
| 5 | Se cifra con la llave de la empresa, en sobre: una llave por respaldo, envuelta con la de la empresa. | Servicio de respaldo → Custodia de llaves | [fuente] `ADR-012`, `CN-28` · [medido] `SPK-13`: cifrar cuesta 0,68 s |
| 6 | **Copia local cifrada**, en un disco o carpeta aparte del disco de la base. | Servicio de respaldo | **[propuesta]**: sin ella, restaurar sin internet es imposible. Ver ES3 |
| 7 | Subida a la nube por HTTPS con TLS mutuo, **reanudable por partes**. Si la nube no responde, se reintenta más tarde y la finca sigue. | Servicio de respaldo → Servicios en línea | [modelo] «reintenta si la nube no responde» · [fuente] `CN-37` |
| 8 | La nube verifica quién llama: certificado de la finca y suscripción vigente. | Control de borde → Identidad y suscripción → BD de los servicios en línea | [modelo] |
| 9 | La nube guarda el archivo **sin poder abrirlo** en un almacén con bloqueo contra borrado. En su base anota solo tamaño, fecha y huella. | Recepción y entrega de respaldos → Custodia de respaldos · BD de los servicios en línea | [fuente] `ADR-003` · [propuesta] el bloqueo contra borrado · [medido] `SPK-13`: la nube **sí ve** el tamaño, la fecha y la cadencia |
| 10 | La nube devuelve la huella que recibió. El nodo compara: si coincide, el respaldo queda «en la nube». | Servicio de respaldo | [propuesta] |
| 11 | La bitácora anota el resultado: cuándo, cuánto pesó, dónde quedó y si se verificó. | Bitácora de auditoría → BD | [fuente] `ESC-03`: «registra el resultado» |
| 12 | **Observabilidad local:** métricas como la edad del último respaldo bueno, la duración, el tamaño, el disco libre y el resultado de la última prueba. Logs estructurados por paso. | Observabilidad | [propuesta] |
| 13 | **Telemetría a la nube** con un **esquema cerrado**: solo los campos de una lista blanca. Si algo trae un campo de más, se rechaza en el origen y en el destino. | Observabilidad → Control de borde → Recepción de telemetría → BD de los servicios en línea | [fuente] `CN-34`, `ESC-30`, `ADR-026` (dos planos) |
| 14 | **Plano de la empresa:** el administrador ve en la web el estado del respaldo y las alertas. | Aplicación web → Gateway → Consulta y tableros | [fuente] `ADR-026` |
| 15 | **Plano de la plataforma:** el operador ve la salud de cada finca, sin datos de negocio. | Panel de soporte remoto → Recepción de telemetría | [modelo] · [fuente] `ESC-30` |
| 16 | **Alertas:** si el último respaldo bueno pasa de cierta edad, el disco baja de cierto umbral o la prueba de restauración falla, se avisa al administrador. | Servicio de notificación · Observabilidad | canal **[PENDIENTE]** (`ADR-012` y `ADR-026` lo exigen sin decir cómo) |
| 17 | **Una vez al mes, prueba de restauración dentro de la finca:** se descifra el último respaldo, se restaura en una base aparte, se compara y se reporta **solo el resultado** por telemetría. | Planificador → Servicio de respaldo → Custodia de llaves · Actualización y migraciones (versión de esquema) | [propuesta]: resuelve el choque de `ESC-19` con `ADR-012`, ver §6 |

---

## 3 · Qué componentes toca

**Contenedores: 7 de 9.** API Gateway, Backend, Base de datos de la finca, Aplicación web, Servicios en
línea, Custodia de respaldos y Base de datos de los servicios en línea.

**Componentes del backend: 9 de 20.** Planificador, Servicio de respaldo, Custodia de llaves, Bitácora,
Observabilidad, Servicio de notificación, Identidad y permisos y Consulta y tableros, más
Actualización y migraciones a medias, porque la prueba verifica la versión del esquema.

**Componentes de la nube: 5 de 6.** Control de borde, Identidad y suscripción, Recepción y entrega de
respaldos, Recepción de telemetría y Panel de soporte remoto. Solo falta Distribución de versiones.

**Juntas, TX-1 y TX-2 tocan 9 de 9 contenedores y 17 de 20 componentes del backend**, uno de ellos
simulado. No tocan el Generador de documentos, la Interfaz de salida de datos ni la Lectura del
sistema heredado.

---

## 4 · La observabilidad, definida

Hay dos planos y **no se pueden mezclar** (`ADR-026`). Construir uno solo rompería `ESC-30` sin que
nadie lo note.

| | Plano de la empresa | Plano de la plataforma |
|---|---|---|
| Quién lo ve | Gerente y administrador de la finca | Operador de la plataforma, el equipo FlorLogic |
| Dónde | Aplicación web, en la red local | Panel de soporte remoto, en la nube |
| Qué ve | Respaldos con fecha, tamaño y resultado · camas y celulares con nombre · alertas | **Solo salud y conteos:** edad del último respaldo, resultado de la prueba, disco libre, versión instalada, número de sesiones y de rechazos del día y errores por código |
| Qué no puede ver | — | Ningún nombre de cama, cifra de tallos, persona ni valor capturado (`CN-34`) |

**Las métricas propuestas para TX-2**, todas sin datos de negocio:

| Métrica | Para qué |
|---|---|
| `respaldo.ultimo_bueno.edad_horas` | La alerta principal: cuánto se perdería si hoy muere el disco |
| `respaldo.duracion_s`, `respaldo.tamano_bytes`, `respaldo.dedup_ratio` | Ver el crecimiento contra los cinco años de `ADR-022` |
| `respaldo.subida.pendientes` | Respaldos locales todavía sin subir: la nube cayó o no hay internet |
| `restauracion.prueba.resultado`, `restauracion.prueba.duracion_s` | `ESC-19` y `CN-15` (≤1 día) |
| `disco.libre_pct` en datos y en respaldo local | Anticipar ES-R06 |
| `bitacora.cadena.ok` | `ESC-40` |
| `telemetria.rechazos_esquema` | Que nadie intente sacar datos por la puerta de la telemetría |

**Los logs:** JSON, un evento por línea, con el id de correlación del trabajo de respaldo. El
contenido de la base nunca va en un log.

---

## 5 · Lo que no queda resuelto y hay que saberlo

1. **La pérdida cero de `CN-15` no se cumple con volcados periódicos.** Entre dos respaldos, lo que
   entró al nodo vive solo en el nodo. Hay dos salidas:
   - **archivado continuo** de la base: `pgBackRest` o `WAL-G`, que no se probaron;
   - **que el celular retenga** hasta que lo suyo esté dentro de un respaldo que salió del nodo
     (P-S03 de TX-1).

   La IA recomienda la segunda: no agrega piezas y reutiliza lo que ya hay. Ver ES3.
2. **`AB-03` · el cifrado del disco del nodo no se pudo medir** (LUKS no abrió en el entorno de
   `SPK-13`). Está abierto en rojo.
3. **`AB-04` · la rotación de llaves contra los cinco años de `ADR-022`.** Si la llave de la empresa
   rota, los respaldos viejos siguen cifrados con la anterior. Abierto en rojo.
4. **El reparto de la llave de custodia.** `ADR-012` dice dos ejemplares en sitios separados.
   `SPK-13` midió que con la llave entera en cada uno, robar un sitio basta, y que con 2-de-2 perder
   un sitio basta. **Solo 2-de-3 resiste las dos cosas.**

---

## 6 · Evaluación de los escenarios de calidad

| ESC | Qué pide | Cómo responde TX-2 | Veredicto |
|---|---|---|---|
| `ESC-03` | Respaldo automático, cifrado, verificado y registrado; 100 % en ventana; pérdida 0; restaurar en ≤1 día | Pasos 1 a 11 | **Se demuestra**, salvo la «pérdida 0», que necesita la retención del celular o el archivado continuo (§5.1) |
| `ESC-19` | El **operador** restaura una vez al mes en un entorno aislado | Choca con `ADR-012`: la llave del equipo solo se usa en una excepción declarada, y con `ESC-50`: 0 accesos en operación normal | **Choca.** **Propuesta:** la prueba corre **en la finca** (paso 17) y al operador le llega solo el resultado. Así se cumple la «1 prueba de restauración por mes» sin que el equipo vea nada |
| `ESC-50` | 0 accesos del operador a datos de negocio y respaldos cifrados al 100 % | La nube nunca tiene la llave. Prueba con el método de `SPK-13`: buscar palabras del esquema en el archivo subido da 0 coincidencias; en el volcado sin cifrar daba 56 | **Se demuestra** |
| `ESC-30` | Causa de un fallo en ≤4 h, sin ver datos de negocio | Telemetría con esquema cerrado y panel del operador | **Se demuestra** el «sin acceder al contenido de producción del cliente». Las 4 h se miden en un simulacro |
| `ESC-31` | Último estado conocido de un celular, con su antigüedad | La última sesión de cada celular en el tablero, y el conteo en el panel | **Parcial**: comparte el dato con TX-1 |
| `ESC-40` | La bitácora es íntegra y se verifica en cada respaldo | Paso 3 | **Se demuestra** |
| `ESC-53` | El administrador resuelve el 80 % de los incidentes sin escalar | Alertas con la acción correctiva: «el respaldo no sube: revise internet» | **Parcial**: hay que escribir las acciones |
| `ESC-59` | Si la nube cae, la finca sigue | Respaldo local y subidas pendientes que se reintentan | **Se demuestra** apagando la nube |
| `ESC-14` | La administración corriente sin consola técnica | «Respaldar ahora» y el estado en la web | **Se demuestra** |
| `ESC-21` · `ESC-41` | Crecimiento a cinco años | `dedup_ratio` y tamaño | **Parcial**: se observa, no se resuelve |
| `CN-15` | Pérdida cero · fallo ≤1 h · restaurar ≤1 día | Restaurar ≤1 día: medido en 2,01 min sobre 24,5 M de filas (`SPK-13`). Pérdida cero: §5.1 | **Parcial** |
| `CN-28` | Cifrado en tránsito y en reposo con la llave del cliente | TLS mutuo, cifrado del respaldo y cifrado del disco (`AB-03` sin medir) | **Parcial** |

---

## 7 · Event storming · lo que puede pasar al respaldar

### ES-R01 · Respaldo programado normal
*El camino feliz. Es el diagrama de flujo del mazo.*
- **Estímulo:** se cumple el intervalo, por ejemplo a las 02:00.
- **Entorno:** nodo sano, con internet y fuera del pico de sincronización.
- **Qué pasó:** volcado, verificación, cifrado, copia local, subida, acuse, bitácora y métrica en 0 h.
- **Cómo lo abordamos:** es la línea base.
- **Prueba:** Testcontainers con PostgreSQL y MinIO; el trabajo se dispara con el reloj adelantado.

### ES-R02 · No hay internet a la hora del respaldo
*Lo normal en una finca.*
- **Estímulo:** la subida falla al conectar.
- **Entorno:** el internet de la oficina está caído.
- **Qué pasó:** el respaldo existe cifrado en local, pero no en la nube.
- **Cómo lo abordamos:**
  - Queda en «subidas pendientes» y se reintenta cada vez más espaciado.
  - La métrica de pendientes sube.
  - Pasado el umbral, se alerta al administrador.
  - **La finca no se entera de nada más** (`CN-37`).
- **Prueba:** Toxiproxy cortando la salida hacia la nube.

### ES-R03 · El internet se corta a mitad de la subida
*Un enlace rural lento.*
- **Estímulo:** la conexión cae con el 60 % subido.
- **Entorno:** enlace inestable y respaldo grande.
- **Qué pasó:** la subida quedó a medias.
- **Cómo lo abordamos:**
  - **Subida por partes**: se reanuda desde la última parte aceptada.
  - La nube solo da por bueno el archivo cuando la huella completa coincide.
- **Prueba:** Toxiproxy con corte a los N bytes.

### ES-R04 · La suscripción de la finca está vencida
*La finca dejó de pagar (`E2`).*
- **Estímulo:** Identidad y suscripción responde «vencida».
- **Entorno:** mes impago.
- **Qué pasó:** el respaldo no tiene dónde quedar en la nube.
- **Cómo lo abordamos:** **es una decisión de negocio** (P-R04). La IA propone que el respaldo **se siga aceptando** durante un periodo de gracia, porque es la protección del dato del cliente y no un servicio de lujo. Lo que se corta primero son las versiones nuevas. Mientras tanto, la copia local sigue.

### ES-R05 · El respaldo sale corrupto
*La verificación local falla.*
- **Estímulo:** la suma de control o el listado del volcado no cuadran.
- **Entorno:** disco con errores o un proceso que murió.
- **Qué pasó:** hay un archivo que no sirve para restaurar.
- **Cómo lo abordamos:**
  - **No se sube.** Se reintenta una vez y se alerta.
  - El último respaldo bueno no se toca.
  - La métrica de edad sigue subiendo, así que la alerta crece sola.
- **Prueba:** truncar el archivo del volcado.

### ES-R06 · El disco del nodo se llena
*Cinco años de acumulación más las copias locales.*
- **Estímulo:** el disco libre baja del umbral.
- **Entorno:** temporada alta.
- **Qué pasó:** no cabe la copia local, y quizá tampoco la base.
- **Cómo lo abordamos:**
  - Alerta **antes**, a partir del umbral de `disco.libre_pct`.
  - Las copias locales rotan: se guardan las N últimas.
  - **La base tiene prioridad sobre la copia local.**

### ES-R07 · El respaldo lleva días atrasado y nadie mira
*La alerta existe, pero nadie la ve.*
- **Estímulo:** `edad_horas` pasa de 72.
- **Entorno:** administrador de vacaciones.
- **Qué pasó:** si hoy muere el disco, se pierden tres días.
- **Cómo lo abordamos:**
  - Escalada: aviso en la web, luego notificación y luego bandera en el panel del operador, solo como conteo.
  - **El operador no ve datos, pero sí que esa finca lleva 72 h sin respaldo**, y puede llamar.
  - El canal de la notificación está [PENDIENTE].

### ES-R08 · La prueba de restauración del mes falla
*El respaldo que creíamos bueno no restaura.*
- **Estímulo:** restaurar en la base aparte termina con error o con conteos distintos.
- **Entorno:** madrugada del primer día del mes.
- **Qué pasó:** hay un respaldo que no sirve, y no se sabe desde cuándo.
- **Cómo lo abordamos:**
  - Alerta al administrador y bandera al operador con el **código** del error, sin datos.
  - Se prueba el respaldo anterior.
  - Se fuerza un respaldo nuevo y se prueba.

### ES-R09 · La telemetría intenta llevar un dato de negocio
*Un programador agrega «nombre de la cama» a un log que viaja.*
- **Estímulo:** el envío trae un campo fuera de la lista blanca.
- **Entorno:** después de un cambio de código.
- **Qué pasó:** casi sale información de la finca.
- **Cómo lo abordamos:**
  - **Esquema cerrado en los dos lados.** El backend no serializa campos desconocidos y la nube rechaza el mensaje.
  - Una prueba automática falla si alguien agrega un campo sin aprobarlo.
- **Prueba:** prueba de contrato de la telemetría.

### ES-R10 · El operador tiene que diagnosticar un fallo a distancia
*`ESC-30`: la finca está a horas.*
- **Estímulo:** el administrador llama: «no sincroniza nadie».
- **Entorno:** soporte remoto.
- **Qué pasó:** hay que encontrar la causa sin entrar a la base.
- **Cómo lo abordamos:**
  - El panel muestra versión, salud del backend y de la base, sesiones del día, rechazos por código, disco y último respaldo.
  - Con eso se separa un problema de red, uno de credenciales o uno de versión.
  - **Ningún acceso a datos de negocio.**

### ES-R11 · La nube está caída varios días
*`ESC-59`.*
- **Estímulo:** la nube no responde desde hace 3 días.
- **Entorno:** incidente del proveedor.
- **Qué pasó:** no hay copias fuera de la finca.
- **Cómo lo abordamos:**
  - Las copias locales siguen, una por intervalo, y las subidas se encolan.
  - Cuando vuelve, sube **solo lo que falta**, porque lo deduplicado ya estaba.
  - La finca nunca se detuvo.

### ES-R12 · Alguien intenta borrar los respaldos de la nube
*Un secuestro de datos, o una credencial de la finca robada.*
- **Estímulo:** pedido de borrado o de sobrescritura de un respaldo.
- **Entorno:** un atacante con el certificado de la finca.
- **Qué pasó:** podría dejar a la finca sin copias.
- **Cómo lo abordamos:**
  - **La finca solo puede escribir, nunca borrar**, y el almacén tiene bloqueo de retención.
  - Los respaldos viejos los borra solo la política de retención de la nube.

### ES-R13 · Se pierde la llave de la empresa junto con el nodo
*El peor fallo del modelo de entrega (`ADR-012`).*
- **Estímulo:** el disco muerto tenía la única copia en línea de la llave.
- **Entorno:** nodo perdido.
- **Qué pasó:** los respaldos existen, pero nadie los abre.
- **Cómo lo abordamos:**
  - **Primero, la copia propia del cliente**. Propuesta: un sobre sellado y una memoria USB en la caja fuerte de la finca, entregados en la instalación.
  - **Después, la copia de custodia del equipo**, como excepción declarada, con doble control, registro y aviso (`ADR-012`).
  - Sin internet, la copia del equipo tarda en llegar, y por eso la del cliente va primero.

### ES-R14 · Restaurar la finca entera en un nodo nuevo
*Con internet. El caso sin internet está en ES3.*
- **Estímulo:** el administrador declara el nodo perdido.
- **Entorno:** hardware nuevo y misma imagen (`ADR-016`).
- **Qué pasó:** hay que volver a operar en ≤1 día (`CN-15`).
- **Cómo lo abordamos:**
  1. Instalar la imagen.
  2. Traer el último respaldo, de la copia local o de la nube.
  3. Descifrar con la llave del cliente.
  4. Restaurar y verificar.
  5. **Los celulares reenvían lo que tenían retenido** y la idempotencia rellena el hueco (P-S03).
- **Prueba:** simulacro completo con cronómetro.

---

## 8 · Casos de uso propuestos

Los diagramas están en el mazo, lámina «TX-2 · casos de uso».

| ID | Caso de uso | Actor |
|---|---|---|
| UC-R1 | Respaldar automáticamente | El tiempo, a través del planificador |
| UC-R2 | Respaldar ahora | Administrador del sistema en la finca |
| UC-R3 | Verificar el respaldo antes de subirlo | Incluido en UC-R1 y UC-R2 |
| UC-R4 | Probar la restauración una vez al mes, en la finca | El tiempo |
| UC-R5 | Ver el estado de los respaldos y las alertas | Administrador del sistema en la finca · Gerente |
| UC-R6 | Reportar telemetría con esquema cerrado | Backend de la finca, como sistema |
| UC-R7 | Diagnosticar la salud de la finca | Operador de la plataforma |
| UC-R8 | Restaurar la finca en un nodo nuevo | Administrador · Operador, solo si hace falta la custodia |
| UC-R9 | Usar la copia de custodia de la llave | Equipo FlorLogic con doble control. Excepción declarada; **no es software**, pero deja registro y aviso |

---

## 9 · Lo que tienes que decidir antes de programar TX-2

| ID | Pregunta | Opciones (la recomendada primero) |
|---|---|---|
| **P-R01** | Cada cuánto se respalda | (a) **cada noche más uno después del cierre de jornada** · (b) cada noche · (c) cada hora |
| **P-R02** | Volcado más deduplicación, o archivado continuo | (a) **volcado nocturno deduplicado más la retención del celular para la pérdida cero** · (b) archivado continuo con `pgBackRest` o `WAL-G`, sin medir · (c) volcados completos, 813 GB a cinco años |
| **P-R03** | ¿Hay copia local además de la nube? | (a) **sí: disco o USB en la finca, cifrada** · (b) solo la nube |
| **P-R04** | Suscripción vencida: ¿se sigue aceptando el respaldo? | (a) **sí, con gracia de N días** · (b) no · decisión de negocio, `E2` |
| **P-R05** | Cuántas copias guarda la nube y por cuánto tiempo | Propuesta: **30 diarias, 12 mensuales y 5 anuales**, por los cinco años de `ADR-022` |
| **P-R06** | ¿La prueba de restauración corre en la finca? | (a) **sí, y solo sube el resultado** · (b) la hace el operador: choca con `ADR-012` |
| **P-R07** | ¿Quién guarda la copia de la llave del cliente? | (a) **el cliente: sobre sellado más USB en la caja fuerte** · (b) solo el equipo, por custodia |
| **P-R08** | ¿Cómo se reparte la copia de custodia? | (a) **2-de-3**, lo único que resiste el robo y la pérdida según `SPK-13` · (b) lo que dice `ADR-012`: dos ejemplares enteros |
| **P-R09** | Canal de las alertas | (a) **la web más el correo transaccional** · (b) más push · decisión con `BB-10` |
| **P-R10** | Proveedor de nube y de almacén de objetos | [PENDIENTE]: requisitos mínimos, compatible con S3 y con bloqueo de retención |
