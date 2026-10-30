# ESPEC · modelo de dominio de la ingesta (TX-1)

REGISTRO DE IA · NO ES FUENTE — lo genera la skill DetallarParaCodificar a pedido de Juan, 20-oct-2026.
Estado de revisión: SIN REVISAR
Manda por encima: `ADR.xlsx` · `docs/03-arquitectura/decisiones/` · `IA_00-LEEME-transacciones.md` · `IA_TX1-sincronizacion.md` · la palabra de Juan.

## Ficha
| Campo | Valor |
|---|---|
| Elemento | Modelo de dominio del paquete `co.florlogic.finca.ingesta.dominio` (backend-finca) |
| **Definición acordada** | Las clases Java puras de `ingesta/dominio` que deciden, con reglas precisas y nombradas, qué pasa con cada anotación que un celular entrega al nodo de finca (`aplicada`, `marcada`, `rechazada` o `sin_efecto`) y cuánto se considera ya recibido de cada celular (`confirmado_hasta`). Lo hacen con un agregado que protege sus invariantes, objetos de valor en vez de primitivos y eventos de dominio que anuncian lo marcado y lo rechazado. **No es** el caso de uso, el controlador, la base de datos ni el aviso real al gerente: esas piezas solo consumen lo que este modelo decide. (Juan, «Es lo mismo», 20-oct-2026) |
| Diccionario | `.claude/contexto/IA_DICCIONARIO.md` |
| Plantilla | `entidad-agregado` (sale hacia: `caso-de-uso` y `evento-mensaje`, en sus semillas) |
| Dominio | Entra: la anotación recibida y lo que ya está guardado con su id · Sale: juicio por anotación, `confirmado_hasta`, eventos de dominio · Fuera: servicio, controlador, JDBC, aviso real al gerente, catálogo versionado completo |
| Generalidad | Casi línea por línea: invariantes y reglas exactas |
| Para qué | Especificar para programar |
| Camino mínimo (D-17) | a) se construye la anotación con ids y valores tipados · b) la raíz recibe un comando con nombre de negocio · c) aplica las reglas en orden (A-2 de la semilla) y devuelve su juicio · d) registra un evento si queda marcada o rechazada · e) el celular avanza su `confirmado_hasta` sin huecos |
| Escenarios del camino mínimo | 1 nueva y sin problema → `aplicada`, sin evento · 2 mismo id, misma huella → `sin_efecto` · 3 mismo id, otra huella → `rechazada` + `AnotacionRechazada` · 4 una regla mínima la marca → `marcada` + `AnotacionMarcada` · 5 consecutivos 1, 2, 4 → `confirmado_hasta` = 2 · 6 dato imprevisto → valor con nombre, nunca excepción. Cada uno es una prueba unitaria del dominio |
| Después | obsoleta/superada (A-45 ⟦1⟧), vieja (A-45 ⟦2⟧), `hora_dudosa` (A-17), `consecutivo_repetido` (A-39), divergencia (A-32, A-35), sesión verificada, restauración (A-31), deduplicación fina de alertas (A-42, A-56) |
| Antes del código (CLAUDE.md) | TX-1 está `AJUSTAR`: Juan acepta el alcance del dominio mínimo. Dominio en Java puro, sin jMolecules (no tiene fila en `IA_TECNOLOGIAS-vigentes.md`) |
| Estado | **LISTA PARA CÓDIGO** (20-oct-2026: ≈88 %, sin contradicción directa ni bloqueante abierto) |
| **Etapa actual** | 5 Revisión · Segmento 1 (escrito el 21-oct-2026; `./mvnw verify` 104/104 con Docker tras COH-140 y COH-142) · 6 Verificación abierta el 21-oct |
| **Dónde retomar** | **PAUSADA otra vez el 21-oct-2026 (tarde) por Juan**, a mitad del paso 6: `ConsecutivoValor` en curso (§10 «Paso 6»; falta: por qué dos formas de crear, constructor que lanza frente a `de` con `Optional`, y qué se cuela con un `long` suelto); luego las clases 2 a 7. En la pausa se decidió OBS-1-6 (catálogo de mensajes, módulo `mensajes`; destino por confirmar). **Antes:** PAUSADA el 21-oct-2026 por Juan. Leer primero `.claude/contexto/IA_LEER-PRIMERO.md`. Etapa 6, Segmento 1: falta el **paso 6, la explicación de Juan** (≈25 %, hace falta ≥80 %) y la aceptación. Después, por OBS-1-5, el siguiente segmento es el **avance conectado de punta a punta** (puerto, adaptador JDBC, V3, caso de uso, casos fijos); `Anotacion` y el traslado pasan después. Código y ESPEC en la copia visible, sin commit (PR-05) |
| Precisión | **≈88 %** (cobertura 84 · contradicciones 90 · prácticas 90 · trazabilidad 93), verificador, 20-oct-2026, tras la redacción final de la ronda 3. Coherencia ronda 3: pasa, 0 bloqueantes. Antes de la redacción final: ≈82 % (76 · 80 · 89 · 88). Ronda 2: **≈75 %** (cobertura 73 · contradicciones 60 · prácticas 89 · trazabilidad 81). Con los 8 arreglos de redacción ≈85 %; con los ejemplos «viola», §9 completa y la confirmación de A-78, ≈89-90 %. Coherencia ronda 2: **pasa, 0 bloqueantes** (COH-121 y COH-122 mayores, antes de la etapa 4). Ronda 1: ≈66 % (69 · 70 · 76 · 35). Arreglos de redacción aplicados el 20-oct con permiso de Juan (D-11): COH-112, 113, 115-118; §3, §5, §8, §9; pares cumple/viola; prácticas |
| Completitud | — · Explicación — |
| Avance por etapa | Tamaño ✔ · Consulta ✔ · Preguntas ✔ 88 % · ronda 3 (3, cerrada) · ronda 2 (9, cerrada) · ronda 1 (10/10, cerrada; P4 «¿hay daño si se rompe un momento?» de la plantilla se poda: ya la cerró P1, consistencia inmediata) · Generación: Segmento 1 de 3 escrito (21-oct) · Revisión: Segmento 1 abierta · Verificación — · Aceptación — |
| Parte de | Semilla `codigo/contrato/espec/IA_apartado-2026-10-18/semillas/IA_SEMILLA-caso-de-uso-ingreso.md` (sus A-n conservan el id) · traspaso D-19 del canal (OBS-2-17) |

## Mapa de la consulta (etapa 2, 20-oct-2026)
Consultado: semilla del caso de uso de ingreso · `IA_TX1-sincronizacion.md` §7 (ES-S03, ES-S04, ES-S05, ES-S09) · `IA_00-LEEME-transacciones.md` (TX-1 `AJUSTAR`) · código de `ingesta/` y `contrato/dto/ParticionDto` · MCP `biblioteca`: *Domain-Driven Design Distilled · p. 86, 89*, *Implementing Domain-Driven Design · p. 397*.

| Qué | Hallazgo |
|---|---|
| Decidido | A-2: tres casos por id (misma huella → `sin_efecto`; otra huella → `rechazada` y nunca se sobrescribe; id nuevo → reglas) y orden del juicio · A-31: `confirmado_hasta` sin huecos, rechazadas cuentan como recibidas; puede bajar tras una restauración · A-27: un hueco no se impide, se marca `hueco_consecutivo` · COH-107: si falta el consecutivo, el canal guarda 0 · P-S01/ES-S09: la incompleta entra marcada · A-42: una alerta por (id, huella) |
| Existe | `AnotacionRecibida` (record sin conducta) · puerto `guardarSiNueva` sin huella · el canal calcula `confirmado_hasta` como el mayor consecutivo |
| Choca 1 | Vernon (*IDDD · p. 397*): se modifica una sola instancia de agregado por transacción (regla práctica). El canal y ES-S03 guardan la partición entera en una transacción. Hay que decidir qué es el agregado |
| Choca 2 | La huella pide JSON canónico (RFC 8785) y no hay biblioteca aceptada: el dominio solo puede compararla como objeto de valor, y la calcula la infraestructura |
| Falta | La regla mínima que marca · dónde vive `confirmado_hasta` (por dispositivo; el dispositivo viaja en la partición, no en el record) · nombres de los eventos |
| Guía | *DDD Distilled · p. 89*: cada agregado empieza con una sola entidad raíz; se juntan dos solo si una invariante pide consistencia inmediata · *p. 86*: sin setters públicos, conducta en métodos (contra el modelo anémico) |
| Plantilla | `entidad-agregado`, ronda 1 = P1–P10 del árbol (tipo, invariantes, frontera, identidad, estados, comandos, errores, valores, referencias, eventos) |

## 1 · Propósito
Dar al ingreso de TX-1 un modelo de dominio con conducta, que hoy es un `record` sin reglas (`AnotacionRecibida`, OBS-2-17), para que el juicio de cada anotación y el avance de `confirmado_hasta` vivan en el dominio y se prueben sin Spring ni base de datos. Fuente: encargo de Juan 20-oct (`IA_RETOMAR-AQUI.md`) · plantilla `entidad-agregado`.

## 2 · Decisiones
Numeración: las decisiones nuevas empiezan en A-60 para no chocar con A-1…A-59 del contrato, que la semilla conserva.

| Id | Afirmación | Fuente | Estado |
|---|---|---|---|
| A-60 | **Dos raíces de agregado pequeñas.** `Anotacion` (identidad `AnotacionId`) protege, dentro de cada instancia, «su contenido no cambia y su juicio se decide una vez» (INV-1, INV-2); entre dos llegadas del mismo id, la unicidad la dan `IdYaRecibido.ante` sobre las huellas y la unicidad del puerto de persistencia (A-2, A-81; COH-133). `AvanceDelDispositivoAgregado` (identidad `DispositivoId`) protege «`confirmado_hasta` es el mayor consecutivo sin huecos y nunca retrocede» (A-31; la cita a A-27 en las preguntas P1 era un error de número, corregido como redacción) y lleva versión optimista. Se referencian por id. **Excepción anotada a la regla de Vernon** (una instancia por transacción): cada partición modifica en una transacción N `Anotacion` nuevas y un `AvanceDelDispositivoAgregado`, porque deben ser consistentes al instante (el celular borra lo que se le confirma; mismo usuario, mismo acto) y lo justifican la razón uno (crear un lote no cambia nada frente a crearlos uno por uno) y la afinidad usuario-agregado (cada avance lo toca solo su celular) | ronda 1 P1 (★) · *Implementing Domain-Driven Design · p. 397, 410-412* · *Domain-Driven Design Distilled · p. 89* · ES-S03 | decidida |
| A-61 | **La raíz 1 se llama `Anotacion`**: «decide una sola vez su juicio y nunca cambia de contenido». Coincide con el contrato (`anotacion_uuid`) y el código; ojo: `ParticionesAdaptadorWeb` ya importa el DTO `co.florlogic.contrato.dto.Anotacion`, así que donde se usen los dos se escribe uno con su nombre calificado (K-119). «Captura» queda como el contenido de negocio que lleva. Sus eventos se nombran `Anotacion<Hecho>` (`AnotacionMarcada`, `AnotacionRechazada`) | ronda 1 P2 (★) · *Domain-Driven Design Distilled · p. 87* (todo el agregado en el lenguaje ubicuo) | decidida |
| A-62 | **Cinco invariantes.** `Anotacion`: **INV-1** contenido fijo (id y huella nunca cambian: campos `final`, sin setters) · **INV-2** juicio único (se decide al crearse; ningún método lo cambia) · **INV-3** juicio coherente (`aplicada` sin códigos · `marcada` con al menos un código · `rechazada` con su motivo · **solo `rechazada` lleva motivo**, COH-116). `AvanceDelDispositivoAgregado`: **INV-4** `confirmadoHasta` = mayor c con 1..c recibidos, y nunca baja (la bajada por restauración, A-31, va después) · **INV-5** solo un consecutivo ≥ 1 mueve el avance; 0 o ausente no es consecutivo (cierra COH-107 en el dominio) | ronda 1 P3 (★) · *Implementing Domain-Driven Design · p. 397* · *Domain-Driven Design Distilled · p. 86* | decidida |
| A-63 | Una anotación sin consecutivo válido (0 o ausente) queda `rechazada` con motivo genérico y no mueve el avance. Es la regla por defecto del camino mínimo (D-17: lo que no se reconoce se rechaza); el motivo fino va después | efecto en cascada de A-62 INV-5 (D-12) · D-17 | **reemplazada por A-70** (ronda 2 P1) |
| A-70 | **Sin consecutivo válido no hay juicio: es `422 sobre-invalido` de la partición entera** (A-22 ⟦3⟧ del contrato: `consecutivo_obligatorio` o `consecutivo_invalido`), igual que el uuid no v7 (A-64). `ConsecutivoValor.de(long)` devuelve vacío con 0 o negativo y nunca lanza; si el canal dejara pasar uno, el servicio responde el 422 genérico (semilla caso de uso). `recibir` recibe un `ConsecutivoValor`, no un `Optional`. Reemplaza A-63 y cierra K-111 y K-113. **Justificación (Juan, ronda 2 P1): si un consecutivo falla, la integridad de toda la partición queda en duda.** El consecutivo es la numeración que el celular da a lo que guardó, en orden; uno ausente o inválido dice que esa numeración no es confiable, y sobre ella se calcula `confirmado_hasta` (A-31), que es lo que el celular usa para borrar. Aceptar las demás y rechazar solo esa podría confirmar números de una secuencia rota y hacer que el celular borre lo que el nodo de finca no tiene. Con el 422 no entra nada (todo o nada, ES-S03), el celular conserva la partición y nada se pierde | ronda 2 P1 (★) · A-22 ⟦3⟧ · K-111 = COH-110 · A-31 · ES-S03 | decidida |
| A-71 | **El hueco de consecutivos entra al camino mínimo** (A-27 del contrato; cierra K-112). `recibir` recibe además el `confirmadoHasta` del avance **como valor** (no el agregado: referencia por id, A-60); si `c > confirmadoHasta.siguiente()`, la anotación lleva `HUECO_CONSECUTIVO` (con `confirmadoHasta` vacío, `siguiente()` vale 1, A-78). El servicio procesa la partición **en orden de consecutivo** y registra uno a uno, así que el hueco de cada anotación se calcula **después** de registrar las anteriores de la misma partición. Si una regla rechaza la anotación, `HUECO_CONSECUTIVO` también se descarta, porque una rechazada no lleva códigos (INV-3) (COH-123). `CodigoMarca { INCOMPLETA, MOTIVO_AUSENTE, HUECO_CONSECUTIVO }`. **Qué es y qué no es un hueco (Juan):** el consecutivo solo numera lo que el celular guardó; una cama asignada que no se capturó (sin captura ni cambio frente al catálogo) no produce anotación ni número, así que **no es hueco**: es «asignado y no capturado» (semilla de asignación). Un hueco es un número que falta, por ejemplo por una partición perdida en el camino. Qué se hace con lo que llega después del hueco («cortar y reanudar»): A-72 | ronda 2 P2 (Juan, no ★, con aclaración) · A-27 · A-31 | decidida |
| A-72 | **Cortar y reanudar = guardar, marcar y reanudar** (lectura ⟦a⟧ de K-114). Lo que llega después de un hueco se guarda con `HUECO_CONSECUTIVO`; `confirmadoHasta` se queda en el último número antes del hueco (INV-4), así que el celular no borra nada por encima y reenvía desde ahí. Cuando llega lo que faltaba, `confirmadoHasta` salta hasta el último contiguo. Nada se pierde y A-27 del contrato no cambia | ronda 2 P3 (★) · K-114 · A-27 · A-31 | decidida |
| A-73 | **Toda llegada con consecutivo válido se registra en el avance**: nueva, `SIN_EFECTO` o corrupta (A-31: las rechazadas cuentan como recibidas). `registrar` es idempotente (C-91), así que repetir un número no cambia nada. **Justificación (Juan, misma regla que A-70): si un consecutivo falla, la integridad de toda la partición peligra.** En el caso normal, una corrupta repite un id ya guardado, y ese id ya registró su consecutivo al guardarse: registrarlo otra vez no cambia nada. La regla protege el caso raro en que la corrupta trae **otro** consecutivo que la original: si no se registrara, ese número quedaría como hueco falso que nunca se cierra, con `confirmadoHasta` trabado, todo lo posterior marcado `HUECO_CONSECUTIVO` (A-72) y el celular reenviando sin fin. Detectar y marcar ese desajuste es `consecutivo_repetido` (A-39) → después. (El ejemplo anterior, «1 y 2 guardados, corrupta con el 3», no podía ocurrir tal cual: corregido por K-118) | ronda 2 P4 (★) · COH-111 · A-31 · A-70 | decidida |
| A-74 | **AJUSTADA 29-oct-2026 por el README §7, que manda (VER-5; Juan eligió aplicarlo):** `AnotacionRecibida` y `RecepcionDeParticionValor` **se quedan en `ingesta/dominio`**, como dibuja el README; el DTO de entrada no lleva dominio y es el **ensamblador de entrada** quien construye los valores con `de(...)` y devuelve `422` si alguno sale vacío (eso es el segmento «avance conectado», VER-1). Lo de abajo es el antecedente. · ~~**`AnotacionRecibida` sale de `dominio`**~~ (COH-119 ⟦1⟧, D-18): se mueve a `servicios/puertos/entrada` y se renombra **`AnotacionEntrante(AnotacionId id, ConsecutivoValor consecutivo, HuellaValor huella, CapturaValor captura)`**: la entrada del caso de uso, ya traducida del cable y sin juicio. `RecibirParticionComando(UUID sesionId, List<AnotacionEntrante> anotaciones)`. El controlador construye los valores con las fábricas `de(...)` y responde `422` si alguno sale vacío (A-64, A-70). En `dominio` queda solo `Anotacion`. **Efecto fuera del borde:** el traslado obliga a adaptar `RecibirParticionComando`, `RecibirParticionCasoUso`, el controlador, el puerto de salida `AnotacionesRecibidasPuertoSalida`, `AnotacionesRecibidasAdaptadorJdbc`, dos pruebas (`RecibirParticionCasoUsoTest`, `AnotacionesRecibidasPuertoSalidaContratoTest`) y un doble (`AnotacionesRecibidasDobleEnMemoria`); se hace de forma mecánica en el segmento que mueve el archivo (el empaquetado no se recorta, D-18), y el rediseño del puerto para guardar `Anotacion` es de la semilla caso de uso | ronda 2 P5 (★) · COH-119 · D-18 · plantilla «Dónde vive cada pieza» | decidida |
| A-75 | **El dominio de ingesta va en subpaquetes por concepto** dentro de `co.florlogic.finca.ingesta.dominio` (COH-119 ⟦2⟧; elección de Juan frente al paquete único). Consecuencias aceptadas: lo que cruza subpaquetes pasa a `public` y hay que actualizar `codigo/IA_LEEME.md` (convención `<modulo>/dominio/`). El reparto exacto, sin ciclos, lo fija K-116. Se añade a #25 una regla ArchUnit: sin ciclos entre los subpaquetes de `ingesta.dominio` | ronda 2 P6 (Juan, no ★) · COH-119 · D-18 | decidida · reparto en A-76 |
| A-76 | **Ajustada 29-oct-2026 (VER-5):** en la raíz de `ingesta/dominio` quedan también `AnotacionRecibida` y `RecepcionDeParticionValor` (README §7), y `dominio.avance` suma `LlegadaAlAvance` (VER-6). · **Reparto sin ciclos en cinco subpaquetes** (cierra K-116 y COH-119): `dominio.comun` (`AnotacionId`, `DispositivoId`, `ConsecutivoValor`, `HuellaValor`, `CapturaValor`, `Presencia`, `Resultado`, `CodigoMarca`, `MotivoRechazo`, `ReglaDeIngreso`, `ReglasDeIngreso`, `Veredicto`) no depende de nadie · `dominio.eventos` (`EventoDeIngreso`, `AnotacionMarcada`, `AnotacionRechazada`) → `comun` · `dominio.anotacion` (`Anotacion`, `IdYaRecibido`) → `comun`, `eventos` · `dominio.avance` (`AvanceDelDispositivoAgregado`) → `comun` · `dominio.reglas` (`ReglaDatoAusente`) → `comun`. La interfaz de la regla va con lo común y solo sus implementaciones van a `reglas` (Strategy). ArchUnit verifica que no haya ciclos entre estos subpaquetes y que ninguno dependa de Spring, SQL ni `contrato` (#25) | ronda 2 P7 (Juan, no ★) · K-116 · D-18 | decidida |
| A-77 | **El resultado de `ante(HuellaValor)` se llama `IdYaRecibido(Resultado resultado, Optional<EventoDeIngreso> evento)`** (antes `Reenvio`): nombra lo común a los dos casos (el id ya existía) y deja que el `Resultado` diga qué pasó: `SIN_EFECTO` es un reenvío y `RECHAZADA` es una corrupta. Vive en `dominio.anotacion` | ronda 2 P8 (★) · COH-114 · diccionario «Anotación corrupta» | decidida |
| A-78 | **«Nada recibido todavía» es `Optional.empty()`** en `AvanceDelDispositivoAgregado.confirmadoHasta()` y en el argumento de `recibir` (A-71 pasa a recibir `Optional<ConsecutivoValor> confirmadoHasta`): como el consecutivo exige ≥ 1 (INV-5), un avance nuevo no tiene número. Con vacío, la primera anotación es hueco si su consecutivo es mayor que 1. En el cable vale 0 (la infraestructura traduce) | efecto en cascada de A-71 e INV-5 (K-117) · confirmada en R3 P1 (★) | decidida |
| A-79 | **Reconstituir sale del borde** (Juan: «se sale del objetivo que busco de la transacción»). Rehacer `Anotacion` y `AvanceDelDispositivoAgregado` desde la base, con la versión optimista del avance, es del **adaptador de persistencia** → semilla `IA_SEMILLA-adaptador-persistencia-ingreso.md`. En el dominio las raíces nacen solo con `Anotacion.recibir(...)` y `AvanceDelDispositivoAgregado.nuevo(DispositivoId)`; las pruebas del camino mínimo no necesitan más. Poda la P14 de la plantilla. **Ajustada por A-81:** se mantiene para `Anotacion` (nunca se rehace); el avance sí tiene una fábrica mínima `desde(...)` | ronda 2 P9 (Juan) · D-17 · A-81 | decidida |
| A-80 | **El rechazo de negocio es un valor, nunca una excepción** (P16 de la plantilla, cerrada sin pregunta porque ya la responden otras decisiones): `Resultado.RECHAZADA` con su `MotivoRechazo` y el evento `AnotacionRechazada` (A-66, A-68); las fábricas `de(...)` devuelven `Optional` (A-64, COH-113); los constructores lanzan solo ante errores de programación. Ningún método del dominio lanza ante una entrada externa (A-57) | ronda 2 (poda de la P16) · A-57, A-64, A-66, A-68 | decidida |
| A-81 | **Lo guardado se usa sin rehacer la `Anotacion`** (cierra COH-121; A-79 se mantiene para `Anotacion`). ⟦1⟧ A-2 se decide con la huella guardada: `IdYaRecibido.ante(AnotacionId, DispositivoId, HuellaValor guardada, HuellaValor llegada)`, estático, en `dominio.anotacion`; misma huella → `(SIN_EFECTO, vacío)`, otra → `(RECHAZADA, AnotacionRechazada(…, CORRUPTA))`. Como la anotación guardada nunca cambia (INV-1, INV-2), nunca hay que rehacerla. ⟦2⟧ El avance sí se carga en cada partición: fábrica mínima `AvanceDelDispositivoAgregado.desde(DispositivoId, Optional<ConsecutivoValor> confirmadoHasta, SortedSet<ConsecutivoValor> porEncima, long version)` → `Optional`, vacío si lo guardado rompe INV-4 (algo en `porEncima` ≤ `confirmadoHasta` + 1; con `confirmadoHasta` vacío, todo en `porEncima` debe ser ≥ 2, COH-130) o si algún argumento es `null`; no juzga ni crea eventos. `nuevo(d)` empieza con `version` = 0 (COH-131). El puerto que da la huella guardada y carga el avance es de la semilla caso de uso | ronda 3 P2 (★, reabre en parte A-79 con permiso) · COH-121 · INV-1, INV-2, INV-4 | decidida |
| A-82 | **`Veredicto` sellado de tres variantes** (cierra COH-122), en `dominio.comun`: `SinMarca()`, `Marca(Set<CodigoMarca> codigos)` (no vacío, copia no modificable) y `Rechazo(MotivoRechazo motivo)`. Las reglas lo crean con `Veredicto.marcar(Set)`, que devuelve `SinMarca` si el conjunto viene vacío y nunca lanza; el constructor canónico de `Marca` rechaza el vacío solo como error de programación, y una regla ArchUnit prohíbe llamarlo fuera de `dominio.comun`. `recibir` junta los veredictos con un `switch` exhaustivo: si hay algún `Rechazo`, `RECHAZADA` con el primer motivo según el orden de la lista de reglas (gana y descarta las marcas, INV-3); si no, la unión de las `Marca` más `HUECO_CONSECUTIVO` si toca (A-71) da `MARCADA`, y sin códigos da `APLICADA`. Así `recibir` cumple INV-3 por construcción y no lanza (A-80) | ronda 3 P3 (★) · COH-122 · A-66 · A-80 | decidida |
| A-64 | **Ids tipados, puestos por otros sistemas.** `record AnotacionId(UUID valor)` lo asigna el celular y **exige UUID v7**, igual que A-38 del contrato. `record DispositivoId(UUID valor)` lo asigna el registro de dispositivos y solo exige no ser nulo. El dominio nunca genera ids (no hay generador). Frontera: la fábrica `AnotacionId.de(UUID)` devuelve `Optional.empty()` si el uuid es nulo o no es v7, y nunca lanza (A-57); el constructor canónico (público, como exige Java en un `record` público) valida lo mismo y lanza, para atrapar errores de programación. Un uuid no v7 **nunca llega al juicio**: es `422 sobre-invalido` de la partición entera (A-38); si el canal lo dejara pasar, el servicio responde el 422 genérico (semilla caso de uso). **Por qué `Optional` y no un resultado con nombre** (COH-113): la clase del fallo ya la nombró el lector del contrato en la frontera (A-57); en el dominio, un vacío solo significa «el contrato no filtró esto» y el servicio responde el error genérico. La P16 se cerró sin cambiar esto (A-80) | ronda 1 P4 y P5 (desempate de K-110, ★) · *Implementing Domain-Driven Design · p. 216-217* (identidad asignada por otro contexto) · A-38 | decidida |
| A-65 | **Tres objetos de valor**, `record` inmutables con fábrica `de(...)` que devuelve `Optional` y nunca lanza (A-57): `HuellaValor(int version, String hex)` (64 caracteres `[0-9a-f]`, `version` ≥ 1; en el cable es `{"alg": "h<n>", "sha256": …}` de A-33, y `version` = n: `h1` → 1; solo se comparan huellas de la misma versión; la calcula la infraestructura y el dominio solo la compara con `equals`) · `ConsecutivoValor(long valor)` (≥ 1, comparable, con `siguiente()`; 0 → vacío → A-70) · `CapturaValor(String json, Map<String, Presencia> campos)` (el JSON equivalente de A-59, opaco, más la presencia de cada campo que fija A-69; sin Jackson en el dominio) | ronda 1 P6 (★) · Bloch, ítem 17 · *Domain-Driven Design Distilled · p. 85* | decidida |
| A-66 | **Un solo `enum Resultado { APLICADA, MARCADA, RECHAZADA, SIN_EFECTO }`**, con `Set<CodigoMarca>` y `MotivoRechazo` aparte en la `Anotacion` (elección de Juan frente a los tipos sellados). INV-3 se protege con **guardas**, no con el tipo. Las guardas viven en el **constructor privado** de `Anotacion`, que lanza si se rompen (solo atrapa errores de programación: el único que lo llama es `recibir`); `recibir` las cumple por construcción (decide el juicio él mismo), así que nunca da vacío ni lanza (COH-112; `reconstituir` salió del borde por A-79). Las guardas: `MARCADA` exige al menos un código; `APLICADA` y `RECHAZADA` no llevan códigos; `RECHAZADA` exige motivo; **una `Anotacion` nunca se guarda con `SIN_EFECTO`** (ese valor solo describe lo que pasó al recibir un id que ya existía). Cómo se protege INV-3 al leer de la base es del adaptador de persistencia (A-79). Una corrupta se informa como `RECHAZADA` sin crear otra `Anotacion` (A-60: un id, una raíz). `CodigoMarca` y `MotivoRechazo` son enums propios del dominio (el `CodigoIngreso` del contrato no entra a `dominio`) | ronda 1 P7 (Juan, no ★) · A-62 INV-3 · CLAUDE.md (dominio sin `contrato`) | decidida · riesgo anotado: las combinaciones imposibles se cuidan con pruebas, no con el compilador |
| A-67 | **Los comandos viven en las raíces**, sin servicio de dominio. `Anotacion.recibir(AnotacionId, DispositivoId, ConsecutivoValor, Optional<ConsecutivoValor> confirmadoHasta, HuellaValor, CapturaValor, ReglasDeIngreso)` crea la nueva y decide su juicio; devuelve siempre una `Anotacion`, nunca vacío ni excepción (firma ajustada por A-70: el consecutivo ya llega válido; y por A-71 y A-78: recibe el `confirmadoHasta` vigente como valor). `IdYaRecibido.ante(AnotacionId, DispositivoId, HuellaValor guardada, HuellaValor llegada)` (estático, firma ajustada por A-81) decide el reenvío de A-2 sin cambiar nada: misma huella → `SIN_EFECTO`, otra → `RECHAZADA` (corrupta); devuelve un `IdYaRecibido` (firma ajustada por A-68, ronda 1 P9). `avance.registrar(ConsecutivoValor)` aplica INV-4; `AvanceDelDispositivoAgregado.nuevo(DispositivoId)` crea uno vacío y `AvanceDelDispositivoAgregado.desde(...)` carga uno guardado (A-81); rehacer una `Anotacion` salió del borde (A-79). Orden en el servicio de aplicación: ¿existe el id? → `ante`; si no → `recibir`; después `registrar` | ronda 1 P8 (★) · *Domain-Driven Design Distilled · p. 86* · *Domain Driven Design · secc. 42* · plantilla §1 ⟦6⟧, ⟦11⟧ | decidida |
| A-68 | **Eventos de dominio en una lista de la raíz**, en Java puro (sin `AbstractAggregateRoot`: dominio sin Spring). `sealed interface EventoDeIngreso` con `AnotacionMarcada(AnotacionId, DispositivoId, Set<CodigoMarca>)` y `AnotacionRechazada(AnotacionId, DispositivoId, MotivoRechazo)`. Llevan ids y códigos, **nunca el dato de la captura** (sin datos de negocio fuera del dominio: `IA_RETOMAR-AQUI.md`, encargo del 20-oct). `Anotacion.recibir` deja en la lista el evento de su juicio (ninguno si queda `APLICADA`); `extraerEventos()` devuelve una copia y vacía la lista, y lo llama el caso de uso. Para la corrupta, `IdYaRecibido.ante(...)` (A-81) devuelve `IdYaRecibido(Resultado resultado, Optional<EventoDeIngreso> evento)`: misma huella → `(SIN_EFECTO, vacío)`; otra → `(RECHAZADA, AnotacionRechazada(…, CORRUPTA))`. `MotivoRechazo { DATOS_INVALIDOS, CORRUPTA }`. **El servicio junta los dos canales** (COH-115): los eventos de `extraerEventos()` de cada nueva y el evento opcional de cada `IdYaRecibido`, en el orden de la partición. **En el cable** (COH-116), `CORRUPTA` se traduce a `rechazada` con `datos_invalidos` (A-2); la traducción vive en la infraestructura, fuera del borde | ronda 1 P9 (★; ajusta la firma de `ante` de la P8, con permiso de Juan) · *Domain Driven Design Reference* (Domain Events) · escenario 3 del encuadre | decidida |
| A-69 | **La regla mínima que marca es el dato ausente de A-35, completa**, como `ReglaDatoAusente implements ReglaDeIngreso` (Strategy; `ReglasDeIngreso` es la lista de reglas que recibe `Anotacion.recibir`). Por cada campo, `enum Presencia { CON_VALOR, AUSENTE_CON_MOTIVO, AUSENTE_SIN_MOTIVO, VALOR_CON_MOTIVO_AUSENCIA }`, que llena la infraestructura desde el Null Object `ValorDto` del contrato (A-34): `AUSENTE_CON_MOTIVO` → `MARCADA` `INCOMPLETA` · `AUSENTE_SIN_MOTIVO` → `MARCADA` `INCOMPLETA` + `MOTIVO_AUSENTE` · `VALOR_CON_MOTIVO_AUSENCIA` → `RECHAZADA` `DATOS_INVALIDOS` (se contradice: error del programa) · `CON_VALOR` → no marca. **Solo usa `motivo_ausencia`.** `motivo_desviacion` viaja en la `CapturaValor` y se guarda, pero no se juzga: detectar la desviación necesita el valor esperado del catálogo (A-49 ⟦4⟧, R-DESV-03) → después. Si una regla rechaza, el rechazo gana sobre las marcas y **descarta los códigos de los otros campos** (una rechazada no lleva códigos, INV-3); si varias marcan, se juntan los códigos como conjunto, así que el orden de las reglas no cambia el resultado (COH-118). `VALOR_CON_MOTIVO_AUSENCIA` es en el fondo un fallo de forma (semilla A-2 ⟦2⟧) que se aplica como regla porque el lector del contrato lo deja pasar como `Contradictorio` (A-34, A-57). **Traducción `ValorDto` → `Presencia`** (infraestructura, `contrato/dto/tipos/ValorDto.java`): `Hay` → `CON_VALOR` · `Falta` con motivo declarado → `AUSENTE_CON_MOTIVO` · `Falta` sin motivo → `AUSENTE_SIN_MOTIVO` · `Contradictorio` → `VALOR_CON_MOTIVO_AUSENCIA`. `CodigoMarca { INCOMPLETA, MOTIVO_AUSENTE }` en esta decisión; A-71 agrega `HUECO_CONSECUTIVO` (tres valores en total) | ronda 1 P10 (★, tras la aclaración de Juan sobre los dos motivos) · A-35 · P-S01 · ES-S09 | decidida |

## 3 · Variables
| Nombre | Valor | Tipo | Dueño |
|---|---|---|---|
| Versión de `AnotacionId` | UUID v7 (`uuid.version() == 7`) | fija (A-38, A-64) | contrato |
| Formato de `HuellaValor.hex` | 64 caracteres `[0-9a-f]` (SHA-256 en hexadecimal) | fija (A-65) | contrato (A-19) |
| `HuellaValor.version` | ≥ 1; `alg: "h<n>"` del cable → n (`h1` → 1) | fija (A-65, A-33) | contrato |
| Rango de `ConsecutivoValor` | 1 … 2⁵³ − 1 (el contrato lo fija; el dominio exige ≥ 1 y acepta hasta `Long.MAX_VALUE`) | fija (A-62 INV-5) | contrato |
| Máximo de códigos por anotación | sin cifra: es un `Set` de un `enum` (hoy 3 valores: `INCOMPLETA`, `MOTIVO_AUSENTE`, `HUECO_CONSECUTIVO`) | fija (A-69, A-71) | — |

## 4 · Comportamiento
Numeración desde C-90, para no chocar con los C-n del contrato que conserva la semilla.

- **C-90** Dado un `AvanceDelDispositivoAgregado` vacío · cuando registra los consecutivos 1, 2 y 4 · entonces `confirmadoHasta` = 2; y cuando después registra el 3 · entonces `confirmadoHasta` = 4 (INV-4, A-31).
- **C-91** Dado un avance con `confirmadoHasta` = 4 · cuando registra otra vez el 2 · entonces `confirmadoHasta` sigue en 4 (INV-4: nunca baja; un reenvío no cambia nada).
- **C-92** Dado el consecutivo 0 o negativo · cuando se pide `ConsecutivoValor.de` · entonces devuelve vacío y no lanza; nunca llega al juicio ni al avance (INV-5, A-70).
- **C-94** Dada una anotación nueva con todos sus campos `CON_VALOR` · cuando se recibe · entonces queda `APLICADA`, sin códigos y sin evento (escenario 1).
- **C-95** Dado un id ya guardado · cuando llega con la misma huella · entonces `ante` da `(SIN_EFECTO, vacío)`; y con otra huella da `(RECHAZADA, AnotacionRechazada(CORRUPTA))`, y la guardada no cambia (escenarios 2 y 3, A-2, A-68).
- **C-96** Dada una anotación nueva con un campo `AUSENTE_CON_MOTIVO` · cuando se recibe · entonces queda `MARCADA` `[INCOMPLETA]` con `AnotacionMarcada`; con `AUSENTE_SIN_MOTIVO`, `[INCOMPLETA, MOTIVO_AUSENTE]`; con `VALOR_CON_MOTIVO_AUSENCIA`, `RECHAZADA` `DATOS_INVALIDOS` con `AnotacionRechazada` (escenario 4, A-69).
- **C-97** Dada una anotación con 12 tallos y `motivo_desviacion: "helada"` · cuando se recibe en el camino mínimo · entonces queda `APLICADA` y el motivo se guarda en la `CapturaValor` (A-69: la desviación se juzga después).
- **C-98** Dado un avance con `confirmadoHasta` = 200 · cuando llegan los consecutivos 401 a 600 · entonces entran `MARCADA` `[HUECO_CONSECUTIVO]` y `confirmadoHasta` sigue en 200; y cuando llegan 201 a 400 · entonces entran sin esa marca y `confirmadoHasta` = 600 (A-71, A-72).
- **C-100** Dados los consecutivos 1, 2 y 3 guardados (el 3 es el id X) · cuando llega X otra vez con otra huella y el consecutivo 3 · entonces `ante` da `RECHAZADA` (corrupta), el avance registra el 3 sin cambiar nada y `confirmadoHasta` sigue en 3 (A-73, C-91; K-118 corrigió el ejemplo anterior).
- **C-102** Dada una regla que devuelve `Veredicto.marcar(Set.of())` · cuando `recibir` junta los veredictos · entonces esa regla cuenta como `SinMarca` y no lanza; y dada una que devuelve `Rechazo` junto a otra con `Marca` · entonces queda `RECHAZADA` sin códigos (A-82, INV-3).
- **C-103** Dado un avance guardado con `confirmadoHasta` = 5 y `porEncima` = {6} · cuando se pide `AvanceDelDispositivoAgregado.desde` · entonces devuelve vacío (6 debería haber subido el confirmado, INV-4); con `porEncima` = {8} devuelve el avance; y con `confirmadoHasta` vacío y `porEncima` = {1} devuelve vacío (A-81, COH-130).
- **C-101** Dado un `AvanceDelDispositivoAgregado.nuevo(d)` (`confirmadoHasta` vacío) · cuando llega primero el consecutivo 3 · entonces entra `MARCADA` `[HUECO_CONSECUTIVO]` y `confirmadoHasta` sigue vacío (0 en el cable); y cuando llegan 1 y 2 · entonces `confirmadoHasta` = 3 (A-71, A-78).
- **C-99** Dada una cama asignada que el operario no visitó · cuando el celular sincroniza · entonces no hay anotación ni consecutivo para ella y no se abre ningún hueco (A-71).
- **C-93** Dada cualquier combinación de presencias de campos (las cuatro de `Presencia`, en uno o varios campos) · cuando se llama `Anotacion.recibir` · entonces nunca resulta `MARCADA` sin códigos, `APLICADA` o `MARCADA` con motivo, `RECHAZADA` sin motivo, ni `SIN_EFECTO` (INV-3, A-66, COH-112).

### 4b · Pares cumple/viola
| Decisión | Cumple | Viola |
|---|---|---|
| INV-1 contenido fijo | `Anotacion` no expone setters; todos sus campos son `final` | un método `cambiarHuella(...)` o un campo sin `final` (lo prueba ArchUnit o reflexión) |
| INV-2 juicio único | `recibir` decide el juicio y ningún método público lo cambia | `anotacion.marcar(...)` después de creada |
| A-64 `AnotacionId.de` | uuid v7 → `Optional.of(...)` | uuid v4 o `null` → `Optional.empty()`, sin excepción |
| A-65 `HuellaValor.de` | 64 caracteres `[0-9a-f]`, versión 1 → presente | 63 caracteres, mayúsculas, `null` o versión 0 → vacío, sin excepción |
| A-65 `ConsecutivoValor.de` | 1 → presente | 0 o negativo → vacío, sin excepción |
| A-65 `CapturaValor` | `json` no nulo y mapa de presencias no modificable | mapa modificable desde fuera después de crearla |
| A-61 nombre | eventos `AnotacionMarcada`, `AnotacionRechazada` | `EventoMarcado`, `CapturaRechazada` |
| Escenario 6 (frontera total) | toda fábrica `de(...)` y `AvanceDelDispositivoAgregado.desde(...)` con un argumento `null` devuelven vacío; `Veredicto.marcar(null)` devuelve `SinMarca` | `NullPointerException` desde una fábrica (COH-134) |
| A-68 sin dato de negocio | `AnotacionMarcada(id, dispositivo, [INCOMPLETA])` | un evento que lleva la `CapturaValor`, el JSON o el valor capturado |
| A-78 nada recibido | `nuevo(d).confirmadoHasta()` es `Optional.empty()`; en el cable, 0 | `ConsecutivoValor.de(0)` presente para representar «nada», o `confirmadoHasta` = 1 sin haber recibido el 1 |
| A-60 excepción a Vernon | una partición de 3 anotaciones nuevas crea 3 `Anotacion` y cambia 1 `AvanceDelDispositivoAgregado`, todo en la misma transacción | un `AvanceDelDispositivoAgregado` que contiene sus anotaciones (agregado que crece sin límite) |
| A-69 rechazo gana | un campo `VALOR_CON_MOTIVO_AUSENCIA` y otro `AUSENTE_CON_MOTIVO` → `RECHAZADA` `DATOS_INVALIDOS`, sin códigos | `RECHAZADA` que conserva `[INCOMPLETA]` |
| A-71 / A-72 hueco | `confirmadoHasta` 200, llegan 401-600 → marcadas `HUECO_CONSECUTIVO` y `confirmadoHasta` = 200 | `confirmadoHasta` = 600 sin que hayan llegado 201-400, o 401-600 `APLICADA` sin marca |
| A-73 registrar siempre | la corrupta registra su consecutivo y `confirmadoHasta` no se traba | el avance ignora el consecutivo de una `RECHAZADA` y deja un hueco falso |

## 5 · Forma técnica
Todo vive bajo `co.florlogic.finca.ingesta.dominio`, en Java puro (CLAUDE.md; plantilla «Dónde vive cada pieza»), con los subpaquetes de A-76. `AnotacionRecibida` sale a `servicios/puertos/entrada` como `AnotacionEntrante` (A-74).

| Pieza | Subpaquete | Tipo | Decisiones |
|---|---|---|---|
| `AnotacionId`, `DispositivoId` | `comun` | ids tipados (`record`) | A-64 |
| `HuellaValor`, `ConsecutivoValor`, `CapturaValor` | `comun` | objetos de valor (`record`) | A-65, A-70 |
| `Presencia`, `Resultado`, `CodigoMarca`, `MotivoRechazo` | `comun` | `enum` | A-66, A-68, A-69, A-71 |
| `ReglaDeIngreso`, `ReglasDeIngreso`, `Veredicto` (sellado: `SinMarca`, `Marca`, `Rechazo`) | `comun` | Strategy: interfaz, lista de reglas que recibe `recibir`, y lo que devuelve una regla | A-69, A-76, A-82 |
| `IdYaRecibido.ante(...)` estático, `AvanceDelDispositivoAgregado.desde(...)` | `anotacion`, `avance` | uso de lo guardado sin rehacer la `Anotacion` | A-81 |
| `EventoDeIngreso`, `AnotacionMarcada`, `AnotacionRechazada` | `eventos` | `sealed interface` + `record` | A-68 |
| `Anotacion` | `anotacion` | raíz (clase `final`, constructor privado) | A-60, A-61, A-62, A-66, A-67, A-68, A-71 |
| `IdYaRecibido` (antes `Reenvio`, A-77) | `anotacion` | `record` de resultado | A-68, A-77 |
| `AvanceDelDispositivoAgregado` | `avance` | raíz (clase `final`, con `version`) | A-60, A-62, A-67, A-72, A-73 |
| `ReglaDatoAusente` | `reglas` | implementación de la Strategy | A-69 |
| `AnotacionEntrante` | `servicios/puertos/entrada` (fuera de `dominio`) | `record` de entrada del caso de uso | A-74 |

**Prácticas** (una línea cada una, para el verificador):
- E-09 igualdad: las raíces son iguales por su id (`equals` y `hashCode` solo con `AnotacionId` o `DispositivoId`); los `record` lo son por valor.
- E-13 creación: constructor privado en las raíces; `Anotacion` se crea solo con `recibir` (nunca se rehace, A-79); el avance se crea con `nuevo` y se carga con `desde` (A-81).
- E-16 colecciones: `Set` de códigos y mapa de presencias no modificables (`Set.copyOf`, `Map.copyOf`); `extraerEventos()` devuelve una copia.
- E-14 sin reloj ni azar: el dominio no lee la hora ni genera ids; todo llega como argumento.
- E-12 sin instante en los eventos: el instante lo pone quien publica (registro de publicación de Spring Modulith, semilla evento-mensaje), fuera del borde.
- #24 sin logging en el dominio: la observabilidad la hace el caso de uso con los eventos y los conteos.
- #25 regla ArchUnit: `ingesta.dominio` no depende de `org.springframework..`, `java.sql..`, `jakarta..`, `tools.jackson..`, `com.fasterxml..` (Jackson 3 deja ahí sus anotaciones, como usa el contrato) ni `co.florlogic.contrato..`; no hay ciclos entre sus subpaquetes (A-76); y el constructor de `Veredicto.Marca` no se llama fuera de `dominio.comun` (A-82) (ArchUnit 1.5.1, TEC-05 `ACEPTADO`).
- #26 Strategy con una sola regla: se justifica porque las reglas siguientes (desviación, versión, hora dudosa) ya están en «Después» y cada una será una implementación.

**Fuera del borde.** Traducción del contrato al dominio (`ValorDto` → `Presencia`, cálculo de la `HuellaValor`, `CORRUPTA` → `datos_invalidos`) → semilla caso de uso / adaptador. Orden del servicio, conflicto de versión optimista, mismo id dos veces en una partición, (COH-120) → semilla caso de uso. Avance guardado inválido (`desde` vacío) → error genérico y alerta al administrador (COH-131) → semilla caso de uso. Recalcular la huella que llega con la versión de la guardada antes de `ante` (COH-132) → semilla adaptador / caso de uso. Rehacer `Anotacion` desde la base y proteger INV-3 al leer (A-79) → semilla adaptador de persistencia. **Cuarentena de los bytes de la corrupta** (semilla A-2): el dominio solo da `RECHAZADA` `CORRUPTA` y su evento; guardar los bytes aparte → semilla adaptador de persistencia. Publicación de eventos → semilla evento-mensaje (`notificacion`).

## 6 · Contradicciones y pendientes
Numeración desde K-110, para no chocar con los K-n del contrato.

| Id | Qué | Tipo | Dueño | Estado |
|---|---|---|---|---|
| K-110 | P4 (Juan): `AnotacionId` exige UUID v7 y «la anotación se rechaza». Pero A-38 del contrato (TX-1.K `ACEPTADO` 19-oct) dice que un `anotacion_uuid` que no es v7 da `422 sobre-invalido` a la partición entera: nunca llega al juicio por anotación | contradicción | Juan | cerrada en P5: v7 y sigue A-38 (A-64) |
| K-112 | A-27 del contrato (decidida) y C-8: tras un hueco las anotaciones llevan `hueco_consecutivo`. El Mapa lo da como decidido, pero A-69 deja `CodigoMarca { INCOMPLETA, MOTIVO_AUSENTE }` y `recibir` no ve el avance: en el escenario 5 la anotación 4 queda `APLICADA`. No está en «Después» ni en §7 (verificador, −15) | contradicción con el contrato | Juan | cerrada en ronda 2 P2: entra ahora (A-71) |
| K-114 | «Si hay huecos por pérdida de paquetes, toca cortar y reanudar para asegurar que la información no se pierda» (Juan, R2 P2). Dos lecturas: ⟦a⟧ se acepta y se marca `hueco_consecutivo` (A-27) y el celular reanuda desde `confirmado_hasta` para mandar lo que falta; ⟦b⟧ se corta: lo que viene después del hueco no se guarda hasta que llegue lo que falta, lo que cambia A-27 del contrato | ambigüedad | Juan | cerrada en R2 P3: lectura ⟦a⟧ (A-72) |
| K-113 | A-63 dice «motivo genérico» sin nombrar el valor de `MotivoRechazo` (verificador, −5). Queda absorbida si K-111 se cierra igualando a A-64 | ambigüedad | Juan | cerrada con K-111 (A-70: ya no hay rechazo por consecutivo) |
| K-111 = COH-110 | A-63 y C-92 rechazan por anotación la que no trae consecutivo válido, pero A-22 ⟦3⟧ del contrato (aceptado) da `422 sobre-invalido` a la partición con `consecutivo_obligatorio` o `consecutivo_invalido`. Es el mismo choque de K-110. Propuesta: igualar a A-64 (`ConsecutivoValor.de` vacío nunca llega al juicio; `recibir` recibe `ConsecutivoValor`, no `Optional`; C-92 prueba `ConsecutivoValor.de(0)` vacío). Reabre A-63 | contradicción · cambia firma | Juan | cerrada en ronda 2 P1: igualar a A-64 (A-70) |
| COH-111 | La corrupta no registra su consecutivo (§7), pero A-31 dice que las rechazadas cuentan como recibidas: con 1-2 guardadas, corrupta 3 y luego 4, `confirmado_hasta` queda en 2 para siempre. Propuesta: `registrar` siempre (es idempotente, C-91) | confiabilidad (mayor) | Juan | cerrada en R2 P4: registrar siempre (A-73) |
| COH-112 | A-66 dice que «la fábrica» devuelve vacío y A-67 que `recibir` nunca da vacío. Propuesta: las guardas de INV-3 viven en `reconstituir` (vacío) y en el constructor privado; `recibir` las cumple por construcción; falta qué hace el servicio si `reconstituir` da vacío | coherencia · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-113 | El escenario 6 pide un valor con nombre; `Optional.empty()` no dice qué falló. Propuesta: aceptarlo en el dominio porque la clase del fallo ya la dio el lector del contrato (A-57), y escribirlo | coherencia (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-114 | El resultado de `ante` se llamaba `Reenvio` y cubría también la corrupta, y el diccionario dice que la corrupta «no es un reenvío». Propuesta: renombrar a `IdYaRecibido` (o `LlegadaRepetida`) con su fila en el diccionario | vocabulario (menor) · nombre | Juan | cerrada en R2 P8: `IdYaRecibido` (A-77) |
| COH-115 | Dos canales de eventos: la lista de la raíz (A-68) y `IdYaRecibido` para la corrupta. Propuesta: escribir que el servicio junta los dos | cohesión (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-116 | Falta la traducción `MotivoRechazo.CORRUPTA` → `datos_invalidos` en el cable (A-2), y falta decir «solo `RECHAZADA` lleva motivo» en INV-3 | coherencia (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-117 | C-97 deja `APLICADA` la desviación con motivo, que el contrato (A-49 ⟦4⟧) da como contraejemplo. Propuesta: anotarlo en §7 como desviación provisional de A-49 ⟦4⟧, a reescribir cuando entre el catálogo | coherencia (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-118 | A-69: decir que el rechazo descarta las marcas de los otros campos; nombrar `VALOR_CON_MOTIVO_AUSENCIA` como fallo de forma (A-2 ⟦2⟧) aplicado como regla; escribir la traducción `ValorDto` → `Presencia` (Hay → `CON_VALOR`, Falta declarado → `AUSENTE_CON_MOTIVO`, Falta sin motivo → `AUSENTE_SIN_MOTIVO`, Contradictorio → `VALOR_CON_MOTIVO_AUSENCIA`) | cohesión (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-119 | **BLOQUEANTE (D-18).** `dominio/AnotacionRecibida` (record sin conducta que usa `RecibirParticionComando`) quedaría junto a `Anotacion`: dos modelos del mismo concepto en `dominio`, y el primero es en realidad entrada del puerto. Falta decir su destino (moverlo a `servicios/puertos/entrada` o retirarlo) y fijar el paquete de `ReglaDeIngreso`, los eventos y los enums | empaquetado | Juan | cerrada en R2 P5-P7 (A-74, A-75, A-76) |
| K-115 | Efecto de A-74: el controlador tiene que construir la `HuellaValor`. **Corregido por COH-126:** TEC-10 está `ACEPTADO` («Jackson 3.1.5 · forma canónica propia»), y queda `POR DEFINIR` si la forma canónica es RFC 8785 con una biblioteca o propia (`IA_TECNOLOGIAS-vigentes.md`, filas 50 y 81). No bloquea el dominio (sus pruebas reciben la huella hecha). Decide Juan antes de la etapa 4: forma propia sobre Jackson 3 (ya aceptada) o proponer una biblioteca RFC 8785 como `PROPUESTA` según el procedimiento de TEC | decisión de tecnología | Juan | abierta → antes de la etapa 4 |
| COH-121 | A-79 frente a A-60, A-67 y E-13: `ante` se llama sobre una `Anotacion` existente y el avance lleva versión, pero sin `reconstituir` y con constructores privados el adaptador no puede rehacerlas sin reflexión; la semilla del adaptador no lo menciona. No rompe los escenarios del dominio. Propuestas: ⟦a⟧ declarar en el dominio las firmas `reconstituir` (sin detallarlas); ⟦b⟧ `ante` estático sobre `(AnotacionId, DispositivoId, HuellaValor guardada, HuellaValor llegada)`, para que el servicio solo necesite la huella guardada | cohesión (mayor) | Juan | cerrada en R3 P2: huella guardada + `avance.desde` (A-81) |
| COH-122 | `recibir` cumple INV-3 «por construcción» solo si `Veredicto` no puede representar combinaciones imposibles, y `Veredicto` no tiene forma decidida; una regla futura con fallos haría lanzar a `recibir`. Propuesta: `Veredicto` sellado (`SinMarca`, `Marca(Set no vacío)`, `Rechazo(MotivoRechazo)`) con fábrica que no lanza; y regla ArchUnit que prohíba llamar los constructores canónicos de los `record` fuera de `dominio` | coherencia (mayor) | Juan | cerrada en R3 P3: `Veredicto` sellado (A-82) |
| COH-123 | Escribir: `siguiente()` de vacío vale 1; el hueco se calcula después de registrar las anteriores de la misma partición; si una regla rechaza, `HUECO_CONSECUTIVO` también se descarta (INV-3) | cohesión (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct, en A-71) |
| COH-124 | Restos de redacción: firma de `recibir` en A-67 sin `confirmadoHasta`; «2 valores» de `CodigoMarca` en A-69 y §3 (son 3); A-65 cita A-63 (es A-70); §5 «antes `IdYaRecibido`» (es `Reenvio`); §8 segmento 2 «retiro o traslado» (es traslado, con la adaptación de controlador, puerto, JDBC y tres pruebas); §9 sin filas de A-70…A-78 y C-98…C-100 | coherencia (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-125 | Diccionario: «Juicio de la anotación» cuenta `sin_efecto` como juicio (es un `Resultado` de llegada, A-66); «Reconstituir» cita A-67 (ahora A-79); faltan `Veredicto` (con «No es: `veredicto_local`») y `ReglasDeIngreso` | vocabulario (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| K-118 | Verificador ronda 2: el ejemplo de COH-111 y de C-100 («1 y 2 guardados, llega una corrupta con el 3») no puede ocurrir tal cual. Una corrupta repite un id ya guardado, y ese id ya registró su consecutivo al guardarse. Que su consecutivo no coincida con el de la guardada es justo `consecutivo_repetido` (A-39, después). A-73 sigue siendo correcta e inofensiva (`registrar` es idempotente), pero su justificación de «avance trabado» solo aplica a ese caso de desajuste. Reescribir C-100 y la nota de A-73 | contradicción en un ejemplo | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| K-119 | Verificador ronda 2: ocho restos de redacción (firma de `recibir` en A-67; `CodigoMarca` con 3 valores en A-69 y §3; A-65 cita A-63; §5 «antes `IdYaRecibido`»; §8 «retiro o traslado»; `Veredicto` sin definir; C-100 = K-118; `HuellaValor.version` frente a `"alg": "h1"` de A-33), más: A-74 dice «tres pruebas» (son dos pruebas y un doble), A-61 choca de nombre con el DTO `co.florlogic.contrato.dto.Anotacion` que importa `ParticionesAdaptadorWeb`, la regla #25 sin `jakarta..` ni `tools.jackson..`, faltan los ejemplos «viola» de A-69, A-71/72 y A-73 y las filas de §9 | redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| K-120 | Verificador ronda 3: ficha «Etapa actual» dice ronda 2; A-74 no nombra `RecibirParticionCasoUso` ni `RecibirParticionComando` entre los usos; faltan ejemplos «viola» de A-68 (dato de la captura en un evento) y A-78; regla #25 sin `com.fasterxml..` (anotaciones de Jackson); A-80 sin fila en §9; A-60 (excepción a Vernon) sin caso; la «cuarentena de los bytes» de la corrupta (semilla A-2) no está trazada | redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-128 | §9 (fila A-67, A-68) y el diccionario («`IdYaRecibido`») todavía dicen `Anotacion.ante`; con A-81 es `IdYaRecibido.ante(AnotacionId, DispositivoId, HuellaValor guardada, HuellaValor llegada)` | coherencia (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-129 | Textos sin A-81/A-82: §5 E-13 sin `desde`; §5 «Fuera del borde» dice «rehacer las raíces» (es rehacer `Anotacion`); §8 Segmento 1 sin `desde`, C-101 ni C-103; Segmento 2 sin A-81 ⟦1⟧, A-82 ni C-102; regla #25 sin la regla ArchUnit del constructor de `Marca` | coherencia (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-130 | `desde` con `confirmadoHasta` vacío: `porEncima` debe ser ≥ 2, y {1} da vacío; añadir el caso a C-103 | cohesión (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-131 | Qué hace el servicio cuando `desde` da vacío (avance guardado corrupto): hoy no hay recuperación ni aviso; y `nuevo` no dice que `version` = 0. Propuesta: «avance guardado inválido → error genérico y alerta al administrador» en §5 «Fuera del borde»; `nuevo` con `version` = 0 | confiabilidad (mayor) · después | semilla caso de uso | `version` = 0 escrito en A-81 (prueba en `AvanceDelDispositivoAgregadoTest`, C-101); lo demás abierto → semilla caso de uso |
| COH-132 | A-65 dice que solo se comparan huellas de la misma versión, pero `IdYaRecibido.ante` compara con `equals`: h2 contra h1 guardada daría `CORRUPTA` aunque el contenido sea igual. Hoy solo existe h1. Propuesta: en §7, la infraestructura recalcula la huella que llega con la versión de la guardada antes de llamar a `ante` | confiabilidad (mayor) · después | semilla caso de uso / adaptador | abierta |
| COH-133 | A-60 dice que `Anotacion` protege «un id, un contenido»; con A-81, entre dos llegadas lo protegen `ante` y la unicidad del puerto, e INV-1 rige dentro de la instancia. Reescribir A-60 | coherencia (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-134 | El par «Escenario 6» de §4b no cubre `desde` ni `Veredicto.marcar(null)`: añadir `marcar(null)` → `SinMarca` y `desde` con `null` → vacío | coherencia (menor) · redacción | IA, con permiso | cerrada · redacción, con permiso (20-oct) |
| COH-127 | En C-98, un hueco marca 200 anotaciones y produce 200 `AnotacionMarcada` por una sola causa → semilla evento-mensaje, con A-42 y A-56 | eficiencia (menor) · después | semilla evento-mensaje | abierta |
| K-116 | Efecto de A-75 (D-12): el reparto del ejemplo de la R2 P6 deja dos ciclos. `anotacion` ↔ `reglas`: `Anotacion.recibir` usa `ReglasDeIngreso` y las reglas usan `CapturaValor` y `CodigoMarca`. `anotacion` ↔ `eventos`: `Anotacion` crea los eventos y los eventos usan `AnotacionId` y `CodigoMarca`. Paquetes con ciclos mutuos = empaquetado mal (D-18) | empaquetado · bloqueante | Juan | cerrada en R2 P7: cinco subpaquetes con `comun` (A-76) |
| K-117 | Efecto de A-71: `recibir` recibía `ConsecutivoValor confirmadoHasta`, pero un avance nuevo no tiene número (INV-5: ≥ 1). Resuelto con el supuesto A-78 (`Optional`) | cascada | IA · se revisa con el código | cerrada con supuesto A-78 |
| COH-120 | Para el caso de uso: conflicto de versión optimista (se deshace todo y el celular reenvía, seguro por A-2); mismo id dos veces en una partición (A-42: «¿existe?» ve lo creado en la transacción); `registrar` corre también tras `SIN_EFECTO` | confiabilidad (menor) · después | semilla caso de uso | abierta |

## 7 · Fuera del dominio (para otro canal)
- Semilla (OBS-1-6, Juan 21-oct): módulo `mensajes` del backend, catálogo de mensajes sobre Spring `MessageSource`; entra con el primer texto que lee una persona.
- Conectar `RecibirParticionCasoUso` con el modelo → semilla caso de uso de ingreso.
- Riesgo de segunda vuelta (A-60): `AvanceDelDispositivoAgregado` guarda los consecutivos por encima de un hueco; si el hueco nunca se cierra, el conjunto crece. Se acota con el cierre de huecos (aviso de descarte o administrador, A-31) y la alerta de COH-93. Propuesta de COH-144: guardarlo por rangos (`[401-600]`) en vez de número por número; costo: más lógica en `registrar` y `desde`.
- «Nunca retrocede» (A-60) tiene una excepción por restauración (A-31) → después.
- Riesgo (A-67): una corrupta trae su propio consecutivo, que puede no coincidir con el de la anotación guardada. Se registra siempre (A-73); si no coincide con el de la guardada, `consecutivo_repetido` (A-39) → después. **Y el riesgo contrario** (COH-143): si la corrupta X (guardada con el 3) llega con el 5 y la Y real del 5 nunca llegó, `confirmado_hasta` pasa el 5 sin tener Y y el celular no la reenvía (rompe A-31) → se decide con A-39 en la semilla caso de uso.
- Riesgo de segunda vuelta (A-72): cuando el hueco se cierra, las anotaciones que llegaron después siguen `MARCADA` `[HUECO_CONSECUTIVO]` porque el juicio no cambia (INV-2). El gerente las verá marcadas aunque ya no falte nada. Se resuelve en la vista del tablero o con un evento «hueco cerrado» → semilla evento-mensaje.
- «Asignado y no capturado» (A-71, C-99): comparar lo asignado con lo capturado es otra regla → semilla `IA_SEMILLA-entidad-agregado-produccion-y-asignacion.md`.
- **Desviación provisional de A-49 ⟦4⟧** (COH-117): mientras no haya catálogo, C-97 deja `APLICADA` una desviación con motivo, que el contrato da como contraejemplo. Cuando entre la regla R-DESV-03 (semilla configuración), C-97 se reescribe como C-53 del contrato (`marcada` con el id de la regla).
- **Quién calcula la `HuellaValor`** (A-65): necesita forma canónica y SHA-256. TEC-10 ya acepta «forma canónica propia» sobre Jackson 3; falta definir si es RFC 8785 con biblioteca o propia (K-115) → semilla adaptador/caso de uso. El dominio solo la compara.
- **De dónde sale el `DispositivoId`** (para el caso de uso): el dispositivo no viaja en la partición; es el de la sesión de sincronización de la ruta (A-52), que hoy no se verifica. El dominio solo recibe un `DispositivoId`; quién lo resuelve mientras no hay sesión verificada → semilla caso de uso de ingreso y semilla sesión y credencial.
- Al escribir el primer segmento: actualizar `codigo/IA_LEEME.md` con los subpaquetes de `ingesta/dominio` (A-75, A-76), que se apartan de la convención `<modulo>/dominio/` plano.
- Aviso real al gerente (correo, push; sin tecnología aceptada) → módulo `notificacion`, plantilla `evento-mensaje`.

## 8 · Plan de segmentos
Borrador (se confirma en la etapa 4). Subpaquetes de A-76 bajo `ingesta/dominio`; pruebas en los mismos subpaquetes de `src/test/java`. Ajustes de la ronda 2: `HUECO_CONSECUTIVO` y `confirmadoHasta` en `recibir` (A-71, A-78), `AvanceDelDispositivoAgregado.nuevo` en lugar de `reconstituir` (A-79), `IdYaRecibido` (A-77) y traslado de `AnotacionRecibida` a `servicios/puertos/entrada` como `AnotacionEntrante` con la adaptación mecánica de sus usos (A-74, pendiente de K-115 para la `HuellaValor`). C-98, C-99 y C-100 entran en el Segmento 1 (avance) y el 2 (anotación).

**Reordenado el 21-oct (OBS-1-5):** después del 1 va el **avance conectado** (fila «1b»); el 2 y el 3 se corren después de él. La PWA, en su propio segmento.

| Seg. | Archivos (líneas estimadas) | Cubre | Estado |
|---|---|---|---|
| 1 | `AnotacionId` (35), `DispositivoId` (20), `HuellaValor` (40), `ConsecutivoValor` (40), `CapturaValor` (35), `Presencia` (15), `Resultado` (15), `CodigoMarca` (10), `MotivoRechazo` (10), `AvanceDelDispositivoAgregado` (90), pruebas de valores (130) y de avance (110) | A-62 INV-4/5, A-64, A-65, A-70, A-72, A-73, A-78, A-81 ⟦2⟧ (`nuevo`, `desde`) · C-90, C-91, C-92, C-98 (avance), C-101, C-103 · pares 4b de A-64, A-65, A-71/72, A-78, escenario 6 | **escrito 21-oct** (720 líneas; `ValoresTest` 15 y `AvanceDelDispositivoAgregadoTest` 13 en verde) · en revisión |
| 1b | `AvancesPuertoSalida`, `AvancesAdaptadorJdbc`, `V3__avance_dispositivo.sql`, cambio en `RecibirParticionCasoUso` y en la respuesta, casos fijos en `codigo/simulador/casos/`, `scripts/IA_probar-avance.sh`, pruebas (contrato del puerto, JDBC con PostgreSQL, caso de uso) | OBS-1-5 · A-31, A-60, A-72, A-81 · usa `desde` y `version` · semillas caso de uso y adaptador de persistencia | por detallar (ronda corta) · supuestos en `IA_LEER-PRIMERO.md` §2 |
| 2 | `Anotacion` (160), `IdYaRecibido` (20), `EventoDeIngreso` + 2 eventos (40), `ReglaDeIngreso` (15), `Veredicto` (30), `ReglaDatoAusente` (50), `ReglasDeIngreso` (35), `AnotacionTest` (240), `ReglaDatoAusenteTest` (90), regla ArchUnit del dominio (40) | A-60, A-61, A-62 INV-1/2/3, A-66, A-67, A-68, A-69, A-71, A-77, A-80, A-81 ⟦1⟧ (`IdYaRecibido.ante`), A-82 (`Veredicto`) · C-93…C-97, C-98 (marca), C-100, C-102 · pares 4b de INV-1, INV-2, A-60, A-61, A-68, A-69, A-73 · COH-141 (el hueco con `valor − 1 > base`, sin `siguienteDe`) | por escribir |
| 3 | Traslado de `AnotacionRecibida` a `servicios/puertos/entrada/AnotacionEntrante` (25) y adaptación mecánica de `RecibirParticionComando`, `ParticionesAdaptadorWeb`, `AnotacionesRecibidasPuertoSalida`, `AnotacionesRecibidasAdaptadorJdbc`, `RecibirParticionCasoUso`, dos pruebas y un doble; actualizar `codigo/IA_LEEME.md` | A-74, A-75 · depende de K-115 (huella) | por escribir |

### Ficha del Segmento 1/3 — valores y avance (por confirmar)
- **Cubre:** A-62 INV-4/5, A-64, A-65, A-70, A-72, A-73, A-78, A-81 ⟦2⟧ · C-90, C-91, C-92, C-98 (avance), C-101, C-103 · pares 4b de A-64, A-65, A-71/72 (avance), A-78, escenario 6 · C-100 (lado del avance). **No cubre:** `Anotacion`, reglas, eventos, `Veredicto`, `IdYaRecibido` (→ Segmento 2); traslado de `AnotacionRecibida` (→ Segmento 3). `AnotacionRecibida` sigue en `dominio` hasta el Segmento 3.
- **Archivos** (paquete `co.florlogic.finca.ingesta.dominio.…`, en `src/main/java`): `comun/package-info` (8), `comun/AnotacionId` (40), `comun/DispositivoId` (30), `comun/HuellaValor` (50), `comun/ConsecutivoValor` (60), `comun/CapturaValor` (45), `comun/Presencia` (25), `comun/Resultado` (20), `comun/CodigoMarca` (15), `comun/MotivoRechazo` (12), `avance/package-info` (8), `avance/AvanceDelDispositivoAgregado` (120). Pruebas (en `src/test/java`): `comun/ValoresTest` (170), `avance/AvanceDelDispositivoAgregadoTest` (150).
- **Árbol de archivos (D-20):**
  ```
  backend-finca/src/main/java/co/florlogic/finca/ingesta/
  └── dominio/
      ├── comun/                        (no depende de nadie · Java puro)
      │   ├── + package-info            [A-76]
      │   ├── + ConsecutivoValor           [A-65, A-70, A-78, INV-5 · C-92]
      │   ├── + AnotacionId             [A-64]
      │   ├── + DispositivoId           [A-64, DI1-2]
      │   ├── + HuellaValor             [A-65, DI1-6]
      │   ├── + CapturaValor            [A-65, A-69, DI1-5]
      │   ├── + Presencia               [A-69]
      │   ├── + Resultado               [A-66]
      │   ├── + CodigoMarca             [A-69, A-71]
      │   └── + MotivoRechazo           [A-68]
      └── avance/                       (→ comun)
          ├── + package-info            [A-76]
          └── + AvanceDelDispositivoAgregado [A-60, A-72, A-73, A-78, A-81, INV-4 · C-90, C-91, C-98, C-101, C-103]

  backend-finca/src/test/java/co/florlogic/finca/ingesta/
  └── dominio/
      ├── comun/
      │   └── + ValoresTest             [pares 4b de A-64, A-65 · C-92 · escenario 6]
      └── avance/
          └── + AvanceDelDispositivoAgregadoTest [C-90, C-91, C-98, C-100, C-101, C-103 · E-09, E-16]
  ```
- **Qué es cada archivo, para qué sirve y qué patrón sigue (D-20, agregado el 21-oct):**
  - `comun/package-info`: documentación del subpaquete; dice que `comun` no depende de nadie y es Java puro (A-76, D-18).
  - `comun/ConsecutivoValor`: `record`, el número que el celular da a cada anotación; lo usan el avance y la anotación para saber qué falta. Objeto de valor (DDD) con fábrica estática `de` que no lanza (fronteras totales, A-57).
  - `comun/AnotacionId`: `record`, la identidad de una anotación; la usará la raíz `Anotacion` (Segmento 2). Id tipado (DDD: identidad asignada por otro contexto, el celular), con fábrica que exige UUID v7.
  - `comun/DispositivoId`: `record`, la identidad de un celular; es la identidad del avance. Id tipado (DDD), con fábrica que no lanza.
  - `comun/HuellaValor`: `record`, el resumen SHA-256 del contenido; el dominio la compara para distinguir un reenvío de una corrupta (Segmento 2). Objeto de valor (DDD); la calcula la infraestructura (hexagonal: el dominio no conoce Jackson).
  - `comun/CapturaValor`: `record`, el contenido de negocio (JSON opaco y presencia de cada campo); lo leerá la regla del dato ausente (Segmento 2). Objeto de valor inmutable con copia defensiva.
  - `comun/Presencia`: `enum`, cómo viene un campo (con valor, ausente con o sin motivo, contradictorio). Lenguaje ubicuo (DDD) traducido desde el `ValorDto` del contrato por la infraestructura.
  - `comun/Resultado`: `enum`, qué pasó con una anotación al llegar. Estado con guardas (A-66: `enum` en lugar de tipos sellados).
  - `comun/CodigoMarca`: `enum`, por qué una anotación queda marcada. Lenguaje ubicuo propio del dominio, sin el código del contrato.
  - `comun/MotivoRechazo`: `enum`, por qué una anotación queda rechazada. Lenguaje ubicuo; la traducción al cable es de la infraestructura.
  - `avance/package-info`: documentación del subpaquete; dice que `avance` solo depende de `comun` (A-76).
  - `avance/AvanceDelDispositivoAgregado`: clase `final`, cuánto se ha recibido de un celular (`confirmadoHasta`); la usará el caso de uso en cada partición. Raíz de agregado (DDD) que protege INV-4, con fábricas `nuevo` y `desde`, igualdad por id y versión para la concurrencia optimista.
  - `comun/ValoresTest`: prueba unitaria de los valores; comprueba los pares cumple/viola y que ninguna fábrica lanza. Pirámide de pruebas: base, sin Spring ni base de datos.
  - `avance/AvanceDelDispositivoAgregadoTest`: prueba unitaria de la raíz; un caso dado/cuando/entonces por escenario (C-90, C-91, C-98, C-101, C-103). Una prueba por invariante (plantilla E-03).
- **Tamaño:** ≈ 750 brutas; ≈ 330 de lógica (pruebas al 0,5, enums al 0,25) → 2 pasadas de revisión.
- **Diagrama:** `diagramas/IA_segmento-1.drawio` (página 1). **Orden de lectura** (solo dominio, de adentro hacia afuera): `ConsecutivoValor` → `AvanceDelDispositivoAgregado` → ids, `HuellaValor`, `CapturaValor` → enums → pruebas.
- **Consultado (D-15):** plantilla entidad-agregado §1, §4 (E-03, E-06, E-07, E-09, E-13, E-14, E-15, E-16), §9.5, §9.7, §10 T1-T2, §11 · *Implementing Domain-Driven Design · p. 216-217* (identidad asignada por otro contexto), *p. 286-287* (pruebas con sentido de dominio; inmutabilidad probada con una copia igual) · *Domain-Driven Design Distilled · p. 85-86* (ids y atributos como valores inmutables; sin setters) · *Applying Domain-Driven Design and Patterns · p. 95* (fábricas estáticas en vez de constructores). Bloch, ítem 17, solo por la cita de la plantilla: *Effective Java* no está en la biblioteca.
- **Decisiones implícitas:**
  - **DI1-1** El dominio no sube `version`; la sube el adaptador al guardar y la usa para el conflicto (plantilla §4 E-15: «P en persistencia»).
  - **DI1-2** `DispositivoId` también tiene `de(UUID)` → `Optional`, para que toda fábrica cumpla el escenario 6 igual (ESPEC escenario 6).
  - **DI1-3** `ConsecutivoValor.siguienteDe(Optional<ConsecutivoValor>)` (vacío → 1) concentra la regla de A-78 y COH-123 en un solo lugar.
  - **DI1-4** `porEncima` es un `TreeSet` interno; hacia fuera solo una vista no modificable (E-16).
  - **DI1-5** `CapturaValor.de` revisa que no haya `null` antes de `Map.copyOf`, porque `Map.copyOf` lanza con claves o valores nulos [sin fuente: documentación del JDK].
  - **DI1-6** `HuellaValor.hex` exige minúsculas, como el resto de ids del contrato (A-38) y el `[0-9a-f]` de A-65.
  - **DI1-7** Los constructores canónicos validan y lanzan (`IllegalArgumentException`, `NullPointerException`) solo ante errores de programación; las fábricas `de`/`desde` son la frontera y no lanzan (A-57, A-80).
  - **DI1-8** `registrar` con un consecutivo ≤ `confirmadoHasta` o ya presente en `porEncima` no cambia nada (idempotente, C-91); con uno contiguo, sube `confirmadoHasta` y absorbe los de `porEncima` que queden contiguos.
  - **DI1-9** (al escribir, 21-oct) `ConsecutivoValor.siguiente()` usa `Math.addExact` y lanza `ArithmeticException` con `Long.MAX_VALUE`: el contrato acota a 2⁵³ − 1, así que solo pasa por un error de programación. `registrar` y `desde` no usan `siguiente()` (comparan con `valor − 1`) y no se desbordan [sin fuente]. Roce con A-80 si algún día el dominio recibe un número fuera del rango del contrato: se mira en la revisión.
  - **DI1-10** (verificación, 21-oct, COH-147) `desde` acepta cualquier `version`, también negativa: el dominio no la usa; su sentido y su rango los decide el adaptador que la sube (DI1-1) [sin fuente].
  - **DI1-11** (verificación, 21-oct, COH-147) `nuevo(null)` lanza `NullPointerException`: lo llama solo el caso de uso con un `DispositivoId` ya construido, igual que `registrar(null)` (DI1-7).
  - **DI1-12** (verificación, 21-oct, COH-142) la copia de `porEncima` usa orden natural (`new TreeSet<>()` + `addAll`), no el comparador de quien llama.
- **Lo imprevisto:** toda fábrica (`de`, `desde`) ante `null` o un valor fuera de rango devuelve vacío. `registrar(null)` lanza `NullPointerException`: lo llama solo el caso de uso con un `ConsecutivoValor` ya construido.
- **Puntos sensibles:** `AvanceDelDispositivoAgregado.registrar` (confiabilidad: ahí vive A-31, que decide qué borra el celular) · `porEncima` sin tope (eficiencia, riesgo de §7, E-06 parcial).

## 9 · Matriz requisito → código → prueba
| Requisito | Archivo:símbolo | Prueba | Estado |
|---|---|---|---|
| A-60, A-62 INV-4 | `AvanceDelDispositivoAgregado:registrar` | `AvanceDelDispositivoAgregadoTest` (C-90, C-91) | en verde (21-oct) · en revisión |
| A-62 INV-5, A-70 | `ConsecutivoValor:de` | `ValoresTest` (C-92, par 4b) | en verde (21-oct) · en revisión |
| A-64 | `AnotacionId:de` | `ValoresTest` (par 4b) | en verde (21-oct) · en revisión |
| A-65 | `HuellaValor:de`, `ConsecutivoValor:de`, `CapturaValor` | `ValoresTest` (pares 4b) | en verde (21-oct) · en revisión |
| A-62 INV-1/2/3, A-66 | `Anotacion` (constructor privado, `recibir`) | `AnotacionTest` (C-93, pares 4b) | por escribir |
| A-67, A-68 | `Anotacion:recibir`, `IdYaRecibido.ante`, `Anotacion:extraerEventos` | `AnotacionTest` (C-94, C-95, par 4b A-68) | por escribir |
| A-80 | ningún método del dominio lanza ante entrada externa | `ValoresTest` y `AvanceDelDispositivoAgregadoTest` (escenario 6) en verde; `AnotacionTest` (C-102) por escribir | en verde la parte del Segmento 1 (21-oct) · Segmento 2 lo demás · COH-141 |
| A-60 | una partición = N `Anotacion` + 1 `AvanceDelDispositivoAgregado` | par 4b A-60 (lectura; la transacción la prueba el caso de uso) | trazado |
| A-71, A-78 | `Anotacion:recibir` (con `confirmadoHasta`) | `AnotacionTest` (C-98 marca, par 4b A-71/72) | por escribir |
| A-72 | `AvanceDelDispositivoAgregado:registrar`, `confirmadoHasta` | `AvanceDelDispositivoAgregadoTest` (C-98 avance) | en verde (21-oct) · en revisión |
| A-73 | `AvanceDelDispositivoAgregado:registrar` tras `ante` | `AvanceDelDispositivoAgregadoTest` (C-100, lado del avance) en verde; `AnotacionTest` (par 4b A-73) por escribir | en verde la parte del Segmento 1 (21-oct) · Segmento 2 lo demás |
| A-75, A-76 | subpaquetes de `ingesta.dominio` | regla ArchUnit «sin ciclos» y dependencias (#25) | por escribir |
| A-77, A-81 ⟦1⟧ | `IdYaRecibido.ante` | `AnotacionTest` (C-95, C-100) | por escribir |
| A-81 ⟦2⟧ | `AvanceDelDispositivoAgregado.desde` | `AvanceDelDispositivoAgregadoTest` (C-103) | en verde (21-oct) · en revisión |
| A-82 | `Veredicto`, `Anotacion:recibir` | `AnotacionTest` (C-102) + regla ArchUnit del constructor de `Marca` | por escribir |
| A-78, A-81 (`version` = 0) | `AvanceDelDispositivoAgregado.nuevo`, `confirmadoHasta`, `version` | `AvanceDelDispositivoAgregadoTest` (C-101) | en verde (21-oct) · en revisión |
| A-71 (no es hueco) | — (sin anotación no hay consecutivo) | C-99 queda como caso de lectura: no tiene código que probar en el dominio | trazado |
| A-74 | `servicios/puertos/entrada/AnotacionEntrante` | pruebas existentes del canal adaptadas (Segmento 3) | por escribir |
| A-69 | `ReglaDatoAusente:evaluar` | `ReglaDatoAusenteTest` (C-96, C-97) | por escribir |
| A-61, #25 | nombres y dependencias de `ingesta.dominio` | regla ArchUnit del dominio | por escribir |

## 10 · Observaciones de revisión

### Segmento 1 · revisión abierta el 21-oct-2026
Pasada 1: `ConsecutivoValor`, `AvanceDelDispositivoAgregado` y `AvanceDelDispositivoAgregadoTest` (~380 líneas). Pasada 2: ids, `HuellaValor`, `CapturaValor`, enums y `ValoresTest` (~340). Filtro D-17: lo que no rompe el camino mínimo no reabre el segmento; el empaquetado sí (D-18).

```
OBS-1-1 · issue (menor, vocabulario): la entrada «Record» del diccionario dice que un record «solo lleva datos»
  Ubicación: .claude/contexto/IA_DICCIONARIO.md «Record · técnico» (PROPUESTO)
  Requisito: A-64, A-65 (ids y objetos de valor como record con reglas)
  Detalle: la entrada se escribió para los DTO del contrato y dice que un record solo lleva datos y no decide nada.
    En el dominio, Consecutivo, Huella, AnotacionId y Captura son record con reglas (validan al crearse, tienen
    fábrica de(...) y operaciones como siguiente()). Juan leyó los record del Segmento 1 como «objetos de traslado de
    información», que es lo que dice la entrada. Record es la forma del lenguaje; DTO y objeto de valor son dos usos.
  Criterio de cumplido: la entrada dice que record es una forma de Java (inmutable, igualdad por contenido) con dos
    usos en el proyecto: DTO en contrato (solo viaja) y objeto de valor o id tipado en dominio (con reglas); y
    «No es: una entidad» se mantiene.
  Origen: pregunta de Juan en la pasada 1 (21-oct): «¿por qué todos son record?»
  Estado: cumplida 21-oct · redacción, con permiso (D-11): la entrada «Record» dice sus dos usos (DTO y objeto de valor o id tipado) y que una entidad no es record
```

```
OBS-1-2 · issue (convención, decide Juan): el nombre de una clase no dice su papel en la arquitectura
  Ubicación: todo el código (backend-finca, contrato, simulador), no solo el Segmento 1
  Requisito: PR-01 (dominio en español, lo técnico en el idioma del armazón) · D-09 (nomenclatura autoexplicativa)
  Detalle: hoy el papel lo dice el paquete (dominio/comun, servicios/puertos/entrada, infraestructura/salida/...) y
    solo algunas clases lo llevan en el nombre (RecibirParticionComando, RecibirParticionServicio,
    AnotacionesRecibidasJdbc, ParticionRequest). Juan quiere que el nombre del archivo cuente su papel (interfaz de
    puerto, DTO, entidad, dominio...). Lo pide también porque «tanto record» no le dice qué representa cada uno: en
    el código hay tres usos (DTO del contrato, comando o resultado de un puerto, objeto de valor o id del dominio).
  Criterio de cumplido: convención de sufijos elegida por Juan, escrita en codigo/IA_LEEME.md y en el diccionario,
    y aplicada a todos los archivos con `./mvnw verify` en verde.
  Origen: pregunta de Juan en Eclipse (21-oct): «¿por qué la nomenclatura de las clases no incluye su tipo?»
  Estado: cumplida 21-oct · Juan eligió «sufijo en todo», en español; 44 archivos renombrados (código y pruebas),
    convención y tabla «antes → ahora» en codigo/IA_LEEME.md; `./mvnw verify` 102/102 con Docker
```

Hallazgos del agente de coherencia (etapa 6, 21-oct-2026). Veredicto: **pasa**, sin bloqueantes y sin nada que rompa
el camino mínimo. COH-140 y COH-142 los comprobó el agente principal en el código.

```
COH-140 · issue (mayor, confiabilidad): las pruebas no atrapan que registrar salte un hueco al absorber
  Ubicación: AvanceDelDispositivoAgregadoTest C-101 (:84-92)
  Detalle: con el mutante «absorber todo porEncima», al llegar el 1 tras el 3 confirma 3 de golpe y las 13 pruebas
    pasan: C-101 solo mira el final. Rompe la promesa de A-31 sin que ninguna prueba lo diga.
  Criterio de cumplido: C-101 comprueba confirmado = 1 justo después del 1; caso nuevo {1, 3, 5} + 2 → confirmado 3 y
    porEncima {5}.
  Estado: cumplida 21-oct · C-101 mira confirmado = 1 tras el 1; prueba nueva {1, 3, 5} + 2 → 3 y {5}; el mutante «absorber todo» ya falla 3 pruebas

COH-141 · issue (menor, confiabilidad): DI1-9 se apoya en un tope del contrato que el código no aplica
  Detalle: AnotacionDto lleva el consecutivo como long sin validar 2⁵³ − 1, y desde acepta Long.MAX_VALUE; si
    Anotacion.recibir usa siguienteDe, lanzaría ArithmeticException (roce con A-80).
  Criterio de cumplido: en el Segmento 2 el hueco se calcula con valor − 1 > base, como registrar, y DI1-9 cita su fuente.
  Estado: abierta · se cierra en el Segmento 2 (anotado en su fila de §8); no rompe el camino mínimo del Segmento 1

COH-142 · issue (menor, confiabilidad): desde conserva el comparador del conjunto que recibe
  Ubicación: AvanceDelDispositivoAgregado.java:37 (new TreeSet<>(porEncima))
  Detalle: TreeSet(SortedSet) hereda el orden del original; con un orden inverso first() da el mayor, nunca absorbe y
    confirmadoHasta se traba.
  Criterio de cumplido: copia con orden natural (new TreeSet<>() + addAll) y una prueba con un conjunto en orden inverso.
  Estado: cumplida 21-oct · copia con orden natural (DI1-12) y prueba con un conjunto en orden inverso; el mutante falla 1 prueba

COH-143 · note (mayor, cohesión): una corrupta con otro consecutivo hace subir confirmado_hasta por encima de lo que falta
  Detalle: A-73 registra toda llegada con consecutivo válido, también la corrupta. Si la corrupta X (guardada con el 3)
    llega con el 5 y la Y real del 5 nunca llegó, confirmado pasa el 5 y el celular no reenvía Y. §7 solo anota el
    riesgo contrario (avance trabado).
  Criterio de cumplido: el riesgo queda en §7 junto a A-39, para la semilla caso de uso; no cambia código ahora.
  Estado: cumplida 21-oct · redacción, con permiso: riesgo en §7 junto a A-39

COH-144 · note (menor, eficiencia): porEncima crece sin tope ante un hueco que nunca se cierra
  Criterio de cumplido: §7 ya lo tiene como riesgo; se agrega la propuesta de guardarlo por rangos (costo: más lógica).
  Estado: cumplida 21-oct · redacción, con permiso: propuesta por rangos en §7

COH-145 · nitpick (menor, vocabulario): el diccionario dice «candidato a agregado» (A-60 ya lo decidió) y porEncima no
  tiene entrada (falta su «No es: hueco»).
  Estado: cumplida 21-oct · redacción, con permiso: «Avance del dispositivo» y entrada nueva `porEncima`

COH-146 · nitpick (menor, coherencia): la ficha dice 93/93 y 102/102; §8 «Cubre» no nombra C-100; en §9, A-80 y A-73
  siguen «por escribir» aunque ya tienen pruebas en verde.
  Estado: cumplida 21-oct · redacción, con permiso: ficha, §8 y §9 al día

COH-147 · nitpick (menor, coherencia): desde acepta cualquier version (también negativa) y nuevo(null) lanza
  NullPointerException; son decisiones implícitas sin escribir.
  Criterio de cumplido: quedan como DI1-10 y DI1-11 en la ficha del Segmento 1.
  Estado: cumplida 21-oct · redacción, con permiso: DI1-10 y DI1-11
```

```
OBS-1-4 · issue (bloqueante, empaquetado D-18): `comun` mezcla categorías distintas
  Ubicación: ingesta/dominio/comun (A-76)
  Requisito: A-75, A-76 (reparto sin ciclos), D-18
  Detalle: en la explicación (paso 6), Juan ve que `comun` junta ids, objetos de valor y cuatro enums con nombres
    que no se parecen (Presencia, Resultado, CodigoMarca, MotivoRechazo), y que lo que es «otra herramienta»
    (respuestas, marcas, enums, ayudantes) debería vivir aparte, quizá como transversal. Los cuatro enums son una
    sola categoría: Presencia es lo que entra al juicio de una anotación (cómo viene cada campo); Resultado,
    CodigoMarca y MotivoRechazo son lo que sale de él (el veredicto y sus porqués). Reabre la decisión A-76 de Juan.
  Criterio de cumplido: reparto elegido por Juan, aplicado a los archivos y a A-76, sin ciclos, con 104/104.
  Origen: explicación de Juan, 21-oct
  Estado: cumplida 21-oct · Juan mantiene A-76; el package-info de `comun` explica las tres clases de piezas
    (identidad, valores, vocabulario del juicio: entra / sale). Riesgo anotado: se lee peor que separado.
```

```
OBS-1-5 · issue (mayor, punto 4 · D-17): el Segmento 1 prepara un repositorio que no existe
  Ubicación: AvanceDelDispositivoAgregado (desde, version), constructor (COH-142)
  Requisito: D-17 (canal de punta a punta primero) · «el dominio tira de la infraestructura»
  Detalle: ninguna clase fuera de ingesta/dominio usa el Segmento 1; el canal sigue con AnotacionRecibida. No hay
    puerto, adaptador ni tabla del avance (migraciones hasta V2__anotacion_recibida). desde y version se escribieron
    para un adaptador futuro, y COH-142 protege a un llamador hipotético. Lo señaló Juan en la explicación.
  Criterio de cumplido: Juan decide si el siguiente segmento conecta el avance de punta a punta (puerto, adaptador
    JDBC, migración y caso de uso) o si desde y version salen hasta que exista el repositorio.
  Origen: Juan, 21-oct: «you are doing this safe for something that's not there yet»
  Estado: decidida 21-oct · Juan: conectar el avance ya (nodo + casos fijos primero, PWA después). Se cumple al
    escribir ese segmento; supuestos por confirmar en IA_LEER-PRIMERO.md §2
```

## 11 · Informe de verificación

### Segmento 1 · verificación abierta el 21-oct-2026
Pasos de `verificacion.md` §4: (1) observaciones aplicadas: OBS-1-1, OBS-1-2, OBS-1-3, COH-140, COH-142…COH-147 cumplidas · (3) build y pruebas:
`./mvnw verify` 104/104 con Docker (21-oct, tras COH-140 y COH-142; los dos mutantes fallan) · (4) coherencia: **pasa** (COH-140…COH-147, sin bloqueantes) ·
(5) completitud y (6) explicación: pendientes.

**Lista de 22 puntos** (`investigacion/05` §4.1):

| # | Punto | Marca | Evidencia o motivo |
|---|---|---|---|
| 1 | Requisito cubierto → código y prueba | Sí | §9: las filas del Segmento 1 tienen archivo y prueba en verde |
| 2 | Ningún código sin padre | Sí | cada clase y método cita A-n, E-n o DI1-n |
| 3 | Lo que queda fuera, con su segmento | Sí | ficha del Segmento 1 «No cubre» → Segmentos 2 y 3 |
| 4 | Sin API «por si acaso» | **No** (corregido 21-oct) | ninguna clase fuera del dominio usa el Segmento 1; `desde`, `version` y la copia de `porEncima` (COH-142) preparan un repositorio de avance que no existe → OBS-1-5 |
| 5 | Camino feliz probado | Sí | `AvanceDelDispositivoAgregadoTest` C-90, C-101, C-103 |
| 6 | Casos borde | Sí | null, 0, negativos, `Long.MIN_VALUE`/`MAX_VALUE`, duplicados (C-91), estados rotos en `desde` |
| 7 | Fallos externos | N. A. | dominio puro: sin red, base ni reloj |
| 8 | Concurrencia y transacción | N. A. | `version` la sube el adaptador (DI1-1); se prueba en la semilla adaptador-persistencia |
| 9 | Las pruebas fallarían si el código se rompe | Sí | aserciones sobre `confirmadoHasta`, `porEncima` y vacíos, no sobre la implementación |
| 10 | Sin pruebas debilitadas | Sí | sin `@Disabled`, sin dobles de lo que se prueba |
| 11 | Build y pruebas en verde | Sí | 102/102 con Docker |
| 12 | Arquitectura y fronteras | Sí | imports de `comun` y `avance`: solo `java.util` y `comun`; sin Spring, SQL ni contrato. La regla ArchUnit propia llega en el Segmento 2 |
| 13 | Sin sobreingeniería | Sí | — |
| 14 | Reutilización | Sí | la regla «siguiente de nada es 1» vive solo en `ConsecutivoValor.siguienteDe` (DI1-3) |
| 15 | Entradas validadas | Sí | toda fábrica `de`/`desde` valida y devuelve vacío |
| 16 | Autorización | N. A. | sin frontera de usuario en el dominio |
| 17 | Sin secretos ni datos sensibles en registros | Sí | no hay registros; `toString` solo muestra el id del celular y números |
| 18 | Dependencias nuevas | Sí | ninguna |
| 19 | Nombres del glosario | Sí | convención OBS-1-2 aplicada; entrada «Sufijo de papel» |
| 20 | Comentarios del porqué, sin TODO | Sí | sin TODO; DI1-n citadas en el código |
| 21 | Explicaciones de la revisión quedaron escritas | Sí (tras OBS-1-3) | la explicación de por qué el dominio no usa setters (record frente a clase con verbos de negocio) solo está en el chat → OBS-1-3 |
| 22 | Juan resume el flujo con sus palabras | pendiente | paso 6 (explicación) |

```
OBS-1-3 · todo (mayor, punto 21): la explicación «por qué no hay setters» no está escrita
  Ubicación: .claude/contexto/IA_DICCIONARIO.md «Objeto de valor» y «Raíz de agregado»
  Requisito: A-62 (invariantes), E-13 (constructor privado, fábricas)
  Detalle: en la revisión se explicó que un setter permite romper INV-4 o INV-5 desde cualquier lado, que el
    objeto de valor cambia creando otro y que la raíz cambia por verbos de negocio (registrar), no por setters
    (modelo anémico). Solo quedó en el chat.
  Criterio de cumplido: las dos entradas lo dicen en una línea, con su fuente (Implementing Domain-Driven Design ·
    p. 56, 271; Domain-Driven Design Distilled · p. 83, 86).
  Origen: punto 21 de la lista, 21-oct
  Estado: cumplida 21-oct · redacción, con permiso: «Sin setters» en «Objeto de valor» y «Raíz de agregado»
```

**Completitud (paso 5, `indice-precision.md` §2, D-17):**

| OBS | peso | estado | evidencia |
|---|---|---|---|
| OBS-1-1 | 1 | cumplida | diccionario «Record» |
| OBS-1-2 | 2 | cumplida | 44 archivos renombrados; `codigo/IA_LEEME.md` «Nombres de las clases» |
| OBS-1-3 (punto 21) | 2 | cumplida | diccionario «Objeto de valor» y «Raíz de agregado», «Sin setters» |
| COH-140 | 2 | cumplida | `AvanceDelDispositivoAgregadoTest` C-101 y `al_cerrar_un_hueco_absorbe_solo_lo_contiguo…` |
| COH-142 | 1 | cumplida | `AvanceDelDispositivoAgregado.java` constructor · `desde_ordena_lo_guardado_por_numero…` |
| COH-145 · COH-146 · COH-147 | 1 + 1 + 1 | cumplidas | diccionario; ficha, §8, §9; DI1-10, DI1-11 |
| COH-143 · COH-144 | — | cumplidas (`note`: fuera de U) | §7 |
| COH-141 | — | abierta, Segmento 2 (no rompe el camino mínimo: fuera de U por D-17) | fila del Segmento 2 en §8 |

```
Coherencia: pasa (COH bloqueantes: 0)
Completitud = 100 × 11 / 11 = 100 %  (contando COH-141 dentro: 11 / 12 = 92 %)   Explicación = pendiente
Build: ok   Pruebas: 104/104 con Docker
```

#### Paso 6 · Explicación de Juan, clase por clase (retomada el 21-oct)

| # | Clase | Lo bien explicado | Lo que falta o se corrige | Avance |
|---|---|---|---|---|
| 1 | `ConsecutivoValor` | objeto de dominio con sus reglas de existencia; revisa el mínimo (≥ 1, INV-5) | es el número de **una** anotación, no el progreso (el progreso es `confirmadoHasta`, en el agregado); no es «vivo», es inmutable y `siguiente()` crea otro; `Comparable` sirve para ordenar (`TreeSet porEncima`); el tope es a propósito (`Long.MAX_VALUE` en el dominio, 2⁵³ − 1 en el contrato, `addExact` en `siguiente`); el tipo lo garantiza el compilador y el texto no numérico lo frena Jackson en `AnotacionDto`; falta explicar por qué hay dos caminos (constructor que lanza / `de` con `Optional`) | en curso |

**Paso 6, ronda del 29-oct-2026** (Juan explicó 1-4; puntuación por clase, práctica y teoría al 50 %): (1) `ConsecutivoValor` ½: volvió «es el progreso» (es el número de **una** anotación; el progreso es `confirmadoHasta`); siguen sin responder las dos formas de crear. (2) `AvanceDelDispositivoAgregado` ½: bien que trabaja con los consecutivos y que guarda una regla (INV-4); mal «el estado de todo objeto del dominio» (solo los números de un celular, nunca anotaciones); faltan `confirmadoHasta`, `porEncima`, `registrar` y el nombre del patrón. Propuso sacar `equals`/`hashCode`/`toString` a lo transversal: respondido (son decisión del dominio —identidad por dispositivo—; Java no deja `default equals` en una interfaz; la forma conocida es Layer Supertype dentro de `dominio`, YAGNI con una sola entidad). (3) `AnotacionId`/`DispositivoId` ¼: los juntó en uno; faltan quién asigna cada uno, v7 y por qué dos tipos. (4) `HuellaValor` ¼: no es composición ni trazabilidad: es el SHA-256 del contenido; misma huella = reenvío, otra = corrupta. **Explicación ≈21 %.** Siguiente: el flujo 1, 2, 4, 3 sobre `registrar`. **Repuntuado el mismo día** tras las aclaraciones de Juan: (1) ½ («el número asignado a la anotación» ✓; nueva confusión: «las anotaciones son las particiones», corregida: la partición es el paquete de un POST y cada anotación dentro lleva su consecutivo); (2) ¾ (teoría ✓: frontera de consistencia propia, sin acceso a las anotaciones; práctica: no es paralelo ni disparado, el caso de uso llama a `registrar` en la misma transacción, y hoy nada lo llama); (3) ½ (dos identidades para dos propósitos ✓; falta por qué dos tipos y no dos `UUID`); (4) ¼. **Explicación ≈29 %.** (4) otra vez: «un id cifrado del contenido, el último paso cuando termina la verificación» → sigue ¼: confunde hash con cifrado (sin llave, sin vuelta atrás), con identidad (esa es `AnotacionId`) y con el momento (es la **primera** comparación del juicio, A-2, no la última). Luego: `confirmadoHasta` = «el mayor guardado» (✗, es el mayor **sin huecos debajo**; contraejemplo 1, 2, 4 → 2, no 4); `porEncima` «ordena» (✓ a medias: guarda lo llegado por encima de un hueco); `AnotacionId` objeto con regla propia que envuelve el valor (✓; el porqué de dos tipos se le mostró con su propia confusión de PRA-1: dos `UUID` seguidos se cruzan y compilan). (3) sube a ¾. **Explicación ≈32 %.** Flujo 1, 2, 4, 3 (selector): eligió la opción con `confirmadoHasta` = 3 al llegar el 3 → tres de cuatro líneas bien (vio que el 4 espera en `porEncima`); le faltó el `while` que absorbe el 4 (correcto: 4 y `porEncima` vacío). (7) ½. **Explicación ≈39 %.** Dos formas de crear (selector): eligió bien `de` para lo que viene de afuera (vacío → 422, nunca lanza) y el constructor para nuestro código. (1) completa: 1. **Explicación ≈46 %.** Reenvío con la misma huella (selector): bien, `sin_efecto` y `confirmadoHasta` no cambia (primer `if` de `registrar`). (4) sube a ¾ (falta decir con sus palabras que es hash, no cifrado). **Explicación ≈54 %.** `CapturaValor` (selector): eligió «el JSON viene cifrado» (✗; segunda vez que «opaco» se vuelve «cifrado»: es texto plano que el dominio decide no leer porque solo juzga la presencia; sin Jackson en el dominio). (5) 0, se repregunta al final. Enums (selector): faltante sin motivo → eligió RECHAZADA `DATOS_INVALIDOS` (✗: es MARCADA `INCOMPLETA` + `MOTIVO_AUSENTE`; solo lo contradictorio o corrupto se rechaza). (6) 0, se repregunta. Patrón: ante lo que falta tiende a rechazar. **Explicación ≈54 %.** Repregunta de enums (tres anotaciones): bien las tres (contradictoria → RECHAZADA, falta con motivo → MARCADA, completa → APLICADA). (6) sube a ¾. **Explicación ≈64 %.** Repregunta de `CapturaValor` (renombrar «tallos»): eligió cambiar `CapturaValor` (✗: el dominio no nombra ningún campo, `grep tallos` en el dominio no da nada; cambian el contrato y la traducción). (5) sigue en 0 tras dos intentos. **Pendiente para aceptar (≥80 %): una frase propia de Juan sobre (5): qué guarda, por qué el dominio no lee el `json`, quién llena `campos`.** Frase de Juan: «guarda el json como texto, tal cual, para trazabilidad y sin cambios; y mapea los valores del json para el dominio» (✓ la primera mitad; ✗ «valores»: son presencias). Selector: bien, `campos` = presencias, lo llena la infraestructura desde el DTO. (5) completa: 1. **Explicación ≈75 %.** Flujo 3, 1, 2 (selector): otra vez sin el `while` (dejó el 3 en `porEncima`; correcto: `confirmadoHasta` 3 y vacío). Se le explicó con la analogía de las páginas que llegan por correo. (7) sigue ½. **Pendiente: Juan explica con sus palabras qué hace el `while` con `porEncima` y cuándo para → ≈82 %.** Frase de Juan: mezcló el segundo `if` (hueco → `porEncima`) con el `while`; vio que el `while` vacía `porEncima` con lo que llega, sin la condición de parada. Selector {4, 6} + 3: eligió «solo sube a 3» (✗; correcto: 4 y {6}). Cuarto intento sin aplicar el `while`: se pasa a práctica (tramo 3 de la ruta): Juan escribe su prueba con su predicción en `AvanceDelDispositivoAgregadoTest`, la corre y la recorre con el depurador en el `while`. (7) ½. **Explicación ≈75 %.** **Corrección de cuenta (Claude, mismo día):** las sumas desde (5) estaban mal divididas. Puntos: (1) 1 · (2) ¾ · (3) ¾ · (4) ¾ · (5) 1 · (6) ¾ · (7) ½ = 5,5 / 7 = **≈79 %** (no 75 %). Con (7) en ¾ → ≈82 %; en 1 → ≈86 %. Regla: `indice-precision.md` §3 (práctica y teoría al 50 %, cada mitad 1 / ½ / 0). **Cierre del paso 6:** Juan leyó la condición del `while` («mientras `porEncima` no esté vacío y su primero sea el siguiente del último confirmado»), que es la regla de parada; el cuerpo (`pollFirst` → nuevo `ultimo`) lo completó Claude. (7) ¾. **Explicación ≈82 % (≥80 %): paso 6 cumplido.** Punto 22 de la lista, explicado. Aceptación (paso 7): la decide Juan. **Juan (29-oct): «todavía no aceptado»**: antes, una verificación contra la documentación aceptada y otra de arquitectura limpia y de todo lo que pide la guía de IS II (Clean Code, SOLID, OWASP, Twelve-Factor, Well-Architected), con subagentes; ejemplo suyo: la URI expuesta en el controlador debería ir a un catálogo de parámetros, que parece no existir.

**Verificación para la aceptación (29-oct-2026, dos subagentes a pedido de Juan: documentación aceptada · arquitectura limpia y guía IS II; 78 pruebas en verde con Docker).** Comprobado por Claude contra el código: nada fuera de `ingesta/dominio` usa `comun` ni `avance`; `AnotacionRecibida` lleva `UUID` y `long` crudos; `V2` sin `CHECK`; sin `@RestControllerAdvice`; ArchUnit 1.5.1 en el `pom` sin reglas. Hallazgos (`DOC-n` documentación, `CAL-n` calidad):

| Id | Qué | Alcance | Severidad |
|---|---|---|---|
| VER-1 (CAL-1, DOC-5) | El Segmento 1 no está conectado: el canal acepta `consecutivo` ≤ 0 y uuid no v7 con `200`; `AnotacionRecibida` usa tipos crudos | S1 + canal | mayor; era el siguiente segmento por OBS-1-5 |
| VER-2 (CAL-5, DOC-12) | Ninguna prueba hace cumplir «dominio Java puro» ni las capas (ArchUnit sin reglas) | S1 | mayor |
| VER-3 (DOC-4) | Textos en excepciones del dominio (`AnotacionId:25`, `ConsecutivoValor:21`, `HuellaValor:27`) contra OBS-1-6 | S1 | menor |
| VER-4 (DOC-13) | Diccionario sin `CodigoMarca`, `MotivoRechazo`, `CapturaValor`, `DispositivoId`, `RecepcionDeParticionValor`, INV-4, INV-5, E-14, COH-105; filas con `ParticionesAdaptadorWeb` | S1 + resto | menor |
| VER-5 (DOC-11) | A-74 y A-76 chocan con el README §7 (que manda): `AnotacionRecibida` y `RecepcionDeParticionValor` en `dominio` | dominio | mayor (coherencia) |
| VER-6 (CAL-6) | `porEncima` crece sin tope | S1 | mayor |
| VER-7 (CAL-10, CAL-11) | Sin pruebas de `siguiente()` en `Long.MAX_VALUE` ni de `desde` con versión negativa; `Optional` como campo | S1 | menor |
| VER-8 (DOC-1, DOC-2, CAL-3) | URI `RECHAZADO` y título escritos en el controlador; sin `traceId`. La URI va al **catálogo de códigos** (`contrato/problemas/TipoProblema`, A-22, OBS-2-7), no a parámetros; el título al catálogo de mensajes | canal | mayor |
| VER-9 (DOC-3) | El catálogo de mensajes en tres lugares: diccionario «módulo `mensajes`» (ACORDADO 21-oct), entrada «Transversal» del diccionario y `IA_LEEME.md:127` | documentos | bloquea programar el catálogo |
| VER-10 (CAL-2) | Un fallo de la base sale como `500` sin `problem+json` | canal | mayor |
| VER-11 (CAL-7) | `ingesta` no registra nada: un `422` no deja rastro | canal | mayor |
| VER-12 (DOC-7, DOC-8) | No se revisa la versión de `contrato` (A-24); sin tope de bytes ni de profundidad del contrato | canal | mayor |
| VER-13 (CAL-4) | Controlador con cuatro trabajos (= OBS-1-8; el filtro U+0000 al adaptador JDBC) | canal | mayor |
| VER-14 (DOC-9, DOC-10, CAL-8, CAL-9) | Ruta repetida con el simulador; `Clock` creado en el controlador y `now()` en la base; `…ONull` devuelven `null` | canal | menor |

Qué ya cumple y se puede mostrar (CAL): fronteras totales con `de(...)`, invariante del agregado, inmutabilidad y copia defensiva, dominio Java puro (a mano), SOLID con ejemplos, Assembler, consulta parametrizada (OWASP A03), errores sin `detail`, idempotencia con `ON CONFLICT`, pirámide de pruebas. «Catálogo de parámetros» del README (ESC-24, ESC-45) = **catálogo de configuración** del diccionario (módulo `configuracion`, vacío). Estado: **presentado a Juan; la aceptación espera su decisión**.

**Decisión de Juan (29-oct): «arreglar lo del segmento y aceptar»**; conectar (VER-1) sigue siendo el segmento siguiente (OBS-1-5). Tope de `porEncima`: **ventana** (no se recuerda, resultado con nombre). Escribe Claude, pero «se debe mejorar la verificación y entendimiento». **Aplicado el mismo día:** VER-2 `CapasTest` (ArchUnit: dominio solo `java..` y dominio, sin contar `package-info`, que declara el módulo; servicios sin infraestructura; la primera corrida falló justo por el `@ApplicationModule` del `package-info`, prueba de que la regla detecta) · VER-3 excepciones con llave y valor · VER-4 diccionario (`CapturaValor`, `CodigoMarca` y `MotivoRechazo`, `DispositivoId`, `LlegadaAlAvance` y ventana, `RecepcionDeParticionValor`; `E-n`, `VER-n`, `DOC-n`, `CAL-n`; nombre viejo del controlador) · VER-5 A-74 y A-76 ajustadas al README · VER-6 `LlegadaAlAvance` y `TOPE_POR_ENCIMA = 10 000`, también en `desde` · VER-7 `long` interno en vez de `Optional` como campo, `desde` rechaza versión negativa, prueba de `siguiente()` en `Long.MAX_VALUE` · VER-9 diccionario e `IA_LEEME.md` apuntan al módulo `mensajes`. VER-8, VER-10…VER-14 son del canal (contrato y tramo 1). **Falta para aceptar:** las preguntas de entendimiento de Juan sobre estos cambios (ventana y `LlegadaAlAvance`, `CapasTest`, el `long` interno, las llaves) y su «sí».

**Preguntas de entendimiento de los cambios, 29-oct-2026 (noche).** Claude hizo 13 preguntas en cinco bloques: A ventana y `LlegadaAlAvance` (1-5), B `long` interno (6-7), C `CapasTest` (8-10), D llaves en las excepciones (11-12), E empaquetado según el README (13). Juan respondió **A1** (por qué un resultado con nombre y no `void` ni excepción): «la mejor forma de dar una reacción específica al sistema que se comunicó con él, diciéndole qué hacer después, con un alcance más preciso y escalable; también puede disparar en pasos anteriores de la arquitectura un registro específico para la salud y la observabilidad; `registrar` funciona como interpretación de una regla de negocio». Puntuación **¾**: práctica ✓ (quien llama, el caso de uso, sabe qué pasó y decide; puede registrarlo o medirlo, pero eso lo hace el servicio, no el dominio, que no registra nada); teoría ½: falta el porqué frente a la excepción y frente a `void`, ligado a A-57 (un reenvío o un número fuera de la ventana es una situación **esperada**, no un error; la excepción queda para errores de programación, y `void` no deja distinguir los cuatro casos). Pista para retomar: «¿un reenvío del celular es un error? ¿qué le llegaría al celular si `registrar` lanzara?». **Faltan A2…E13** (12 preguntas) y el «sí» de Juan.

**OBS-1-6 · Los mensajes no viven en código crudo** (Juan, 21-oct, durante la explicación de `ConsecutivoValor`). Ningún texto para una persona va escrito en el código: va en un **catálogo de mensajes** y el código solo nombra su llave. Hoy hay tres textos crudos en el dominio, todos en excepciones de error de programación: `AnotacionId:25`, `ConsecutivoValor:20` y `HuellaValor:27`. **Alcance (Juan, 21-oct): todo, pero las excepciones solo con llave**: una excepción lleva la llave y el valor (`"consecutivo.menor_que_uno: " + valor`), sin texto; el texto vive en el catálogo de mensajes (`mensajes*.properties`) y el dominio no lee ningún archivo. Antes de preguntar dónde vive el catálogo, Juan pidió investigarlo (Spring `MessageSource`, convención `ResourceBundle`). **Dónde vive (Juan, 21-oct): módulo propio `mensajes`** de Spring Modulith (`co.florlogic.finca.mensajes`, `@ApplicationModule`), dueño de todos los textos; API pública `MensajesPuerto.texto(llave, Locale, Object...)`, adaptador sobre Spring `MessageSource` en su `infraestructura`, `mensajes.properties` con todas las llaves (prefijo por módulo: `ingesta.…`). Sin dependencia nueva (Spring Framework ya aceptado). Fuentes: Spring Framework «Additional Capabilities of the ApplicationContext» · Spring Boot «Internationalization» · *Application Architecture Guide v2 · p. 522*. **Destino propuesto, por confirmar con Juan:** las tres excepciones pasan a «solo llave» en el segmento del avance conectado (cambio mecánico); el módulo `mensajes` entra cuando aparezca el primer texto que lee una persona (el dominio tira de la infraestructura), hasta entonces semilla en §7. Estado: **decidida en forma; destino por confirmar**. No rompe el camino mínimo (D-17): va al segmento que Juan elija, no reabre el Segmento 1. Diccionario: «Catálogo de configuración», «Catálogo de códigos», «Catálogo de mensajes» (21-oct).

**OBS-1-7 · Reempaquetado hexagonal** (Juan, 22-oct-2026, durante la verificación). La forma de paquetes del 20-oct
dejaba sueltos puertos, adaptadores y DTO; Juan dio su diagrama de paquetes y se aplicó a todo `backend-finca`
(D-18): `servicios/{input/{port,dto,adapter}, casosuso/impl, output/{adapter,dto,port}}`,
`infraestructura/{input/controlador, output/adapter}`, `transversal`, y en la raíz `dominio` (núcleo compartido),
`infraestructura` (antes `plataforma`) y `transversal`. Decisiones de Juan: el caso de uso es interfaz + `impl` y
habla en dominio (devuelve `RecepcionDeParticionValor`); la traducción DTO ↔ dominio la hacen **ensambladores**
en `servicios/*/adapter` (Fowler · PoEAA secc. 100); el puerto de salida recibe un DTO de salida (Martin · Clean
Architecture p. 195). Diagrama: `diagramas/IA_reempaquetado-hexagonal.drawio`. `./mvnw verify` 106/106 con Docker.
No toca el Segmento 1 (valores y agregado). **Choca con A-74, por decidir con Juan (D-13):** A-74 saca
`AnotacionRecibida` del dominio a la entrada como `AnotacionEntrante` con valores del dominio; con la forma nueva el
DTO de entrada no lleva dominio y el ensamblador arma el dominio, así que `AnotacionRecibida` (o su sucesora) se
queda en `dominio`. Opciones: mantener A-74 (y aceptar dominio en el DTO de entrada) o reescribirla. Afecta también
al Segmento 3 de §8 y a la entrada «`AnotacionEntrante`» del diccionario. Estado: **aplicada; A-74 por decidir**.

**OBS-1-8 · El controlador hace más que adaptar** (Juan, 29-oct-2026, durante la explicación de `ConsecutivoValor`:
«no veo cómo se cumple la arquitectura hexagonal si hay funciones de null object en el mismo controlador»).
`ParticionesControlador` (`infraestructura/input/controlador`, del canal, contrato Segmento 2) **respeta la dirección
de dependencias**: solo conoce el puerto de entrada y sus DTO, nunca el dominio. Pero mezcla tres papeles en 183
líneas: (1) adaptar HTTP ↔ puerto (lo suyo); (2) validar la forma del sobre (`formaMinima`, `soloObjetos`), que A-58
y el Segmento 6 del contrato ponen en un `ValidadorContrato` único; (3) **una restricción de la base** (`guardable`:
`jsonb` no guarda U+0000, COH-105): el adaptador de entrada sabe un límite del adaptador de salida, y eso sí rompe la
idea del hexágono (cada adaptador ignora a los demás). Además `uuidONull` y `arbolONull` **devuelven `null`**: no son
Null Object (el único Null Object real es `path()` de Jackson, `MissingNode`), y contradicen la regla del proyecto
para fronteras (A-57, D-15: resultado con nombre o `Optional`, Special Case). No rompe el camino mínimo (D-17).
Destino propuesto: el Segmento 6 del contrato (validador único) se lleva (2) y los `…ONull`; (3) pasa a una regla
del contrato («ningún texto con U+0000») validada en el mismo validador, o a la persistencia con su prueba. Estado:
**anotada; Juan (29-oct): separarlo antes de seguir, escrito por él**, como tramo 1 de `.claude/contexto/IA_RUTA-ESTUDIO-TX1.md`; dónde vive `guardable` lo decide en ese tramo. No toca el Segmento 1 del dominio; la explicación de `ConsecutivoValor` sigue como tramo 2.

*Ajuste del 22-oct (tarde), en conversación con Juan, con su proyecto de referencia `uco-test-api`:* **un hexágono por
módulo**; los 14 módulos siguen siendo de negocio. La raíz **no es hexágono** (sin `servicios/casosuso`): es el
**afuera compartido**, con `inicializador`, `dominio` (núcleo compartido), `transversal` (= crosscutting: helpers y
herramientas; corrección de Juan) e `infraestructura/output/serviciosexternos/{messagecatalog, parametercatalog,
telemetry, …}`. **Variante B (Juan):** el servicio externo de la raíz publica su propio puerto y su adaptador; el
módulo lo inyecta (dependencia módulo → raíz). Para Modulith, cada `serviciosexternos.<x>` necesita `@NamedInterface`,
porque es subpaquete del módulo `infraestructura`. Fuentes: Brown en Martin · Clean Architecture cap. 34 p. 271-279
(package by component), Vernon · IDDD p. 39 (un hexágono por Bounded Context). **Todavía sin código**: falta el plan,
su diagrama y el «sí».

