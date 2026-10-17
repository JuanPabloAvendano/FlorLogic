REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 16-oct-2026, a pedido de Juan (skill detallar-para-codificar)
Estado de revisión: SIN REVISAR
Manda por encima: los ADR (`ADR.xlsx`) y la documentación de `documentacion-desarrollo`; si chocan, manda la fuente y esta entrada se corrige

# Diccionario de FlorLogic

Palabras con un significado específico en FlorLogic, de **negocio** y **técnicas**. Lo mantiene la skill
`detallar-para-codificar`: cada explicación copia de aquí las definiciones de las palabras que usa. Si
una palabra tiene dos lecturas, son dos entradas y cada una nombra a la otra en «No es».

Estados: `PROPUESTO` (escrito por la IA, sin confirmar) · `ACORDADO <fecha>` (Juan dijo «Es lo mismo»)
· `REEMPLAZADO <fecha> · ver <término>`.

## Pares que se confunden

| Término | Término | La diferencia |
|---|---|---|
| Lote | Partición | El lote es una plantación; la partición es un trozo de lo que sube un celular |
| Sesión de captura | Sesión de sincronización | La primera es el trabajo de una persona en campo; la segunda, una subida al nodo de finca |
| `confirmado_hasta` | `respaldado_hasta` | Lo que el nodo de finca recibió frente a lo que ya está respaldado en dos copias |
| Nodo de finca (nodo principal) | Nube | El nodo de finca tiene el dato vivo y opera sin internet; la nube solo guarda respaldos y versiones |
| Acuse | Marca de agua | El acuse es por anotación; la marca de agua es un número para todo el celular |
| Anotación vieja | Anotación obsoleta | La vieja se mide por edad; la obsoleta, porque otra captura ya cumplió su orden de captura |
| Divergencia | Fallo técnico | La divergencia es un fallo del motor y entra marcada; el fallo técnico se descarta y se recaptura |
| Captura incompleta | Fallo técnico | La incompleta es un hecho del campo y entra; el fallo técnico es un error y se descarta |
| Anotación marcada | Marca de agua | La marcada resalta una anotación para el gerente; la marca de agua es un número de avance |
| `corrige_a` | Corrección del gerente | `corrige_a` es una anotación que corrige otra; la del gerente es una revisión en el tablero |
| Corrección | Edición local | La corrección la hace el gerente sobre lo registrado; la edición local, el operario antes de sincronizar |
| Desviación | Divergencia | La desviación es un cambio brusco del producto; la divergencia, un desacuerdo entre motores |
| Hueco | Dato ausente | El hueco es un consecutivo que no llegó; el dato ausente llegó sin cifra |

## Entradas

### Acuse · técnico
- **Es:** lo que el celular recuerda de cada anotación según la respuesta del nodo de finca: su resultado (`aplicada`, `marcada`, `rechazada` o `sin_efecto`) y sus códigos.
- **No es / se confunde con:** `confirmado_hasta`; el acuse es por anotación. Para borrar no basta cualquier acuse: la regla está en A-48. Tras restaurar el nodo de finca, un acuse viejo puede ser falso.
- **Fuente:** ESPEC-contrato-ingesta A-39, A-48, COH-13
- **Estado:** PROPUESTO

### Anomalía · técnico
- **Es:** una situación que por diseño no debería ocurrir (por ejemplo, un celular con una versión que el nodo de finca no creó); si aparece, se alerta al administrador; con `contrato-incompatible` el celular retiene todo.
- **No es / se confunde con:** fallo técnico, que sí se espera y se resuelve borrando y recapturando.
- **Fuente:** ESPEC-contrato-ingesta A-50
- **Estado:** PROPUESTO

### Anotación · técnico
- **Es:** un registro que el celular guarda y entrega al nodo de finca, con identidad propia (UUID v7), un número consecutivo y un tipo; hoy solo existe `captura.campo`.
- **No es / se confunde con:** el dato de negocio que se captura; la anotación es el sobre que lo lleva.
- **Fuente:** ESPEC-contrato-ingesta A-1, A-15
- **Estado:** PROPUESTO

### Anotación aislada · técnico
- **Es:** la anotación que la anotación culpable que el celular separa de su partición (por un `413` o por un `422` con `pointer` a ella); nunca entró al nodo de finca.
- **No es / se confunde con:** anotación rechazada, que sí entró y quedó registrada.
- **Fuente:** ESPEC-contrato-ingesta A-41
- **Estado:** PROPUESTO

### Anotación corrupta · técnico
- **Es:** una anotación que llega con el mismo `anotacion_uuid` que otra ya guardada o que una constancia, pero con otro contenido (otra huella); es fallo técnico: se rechaza, se alerta, y el celular la borra con lápida y pide recapturar.
- **No es / se confunde con:** un reenvío, que trae el mismo contenido y cuenta `sin_efecto`.
- **Fuente:** ESPEC-contrato-ingesta A-2, C-4 (antes ADR-027 §2, deprecado, K-41)
- **Estado:** PROPUESTO

### Anotación marcada · técnico
- **Es:** una anotación que entra y cuenta para el estado, pero lleva uno o más códigos que la **resaltan para que el gerente de producción haga las correcciones**.
- **No es / se confunde con:** anotación rechazada, que no cuenta; ni la marca de agua.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · ESPEC-contrato-ingesta A-23
- **Estado:** ACORDADO 16-oct-2026

### Anotación obsoleta · técnico
- **Es:** una captura que llega cuando su orden de captura **fue reasignada a otro dispositivo o persona** y ésa ya la cumplió con una captura posterior y verificada, en el mismo (sección, campo); no entra al estado ni se guarda el dato, solo una constancia. Sin reasignación, decide RF-022 y se guardan las dos.
- **Variante:** «superada»: la vieja que llegó antes y ya estaba guardada; al llegar la que cumple la orden, sale del estado y de la historia y queda su constancia.
- **No es / se confunde con:** anotación vieja, que se mide por su edad; ni el registro que pierde en RF-022, que sí se guarda.
- **Ejemplo:** la cama 12 la capturó primero el turno de la tarde; la del turno de la mañana llega después y queda obsoleta.
- **Fuente:** aclaración de Juan 16-oct (ronda 1e P3) · ADR-026
- **Estado:** ACORDADO 16-oct-2026

### Anotación rechazada · técnico
- **Es:** una anotación que llegó al nodo de finca, quedó registrada, pero no cuenta para el estado porque su forma no cumple el contrato o es corrupta.
- **No es / se confunde con:** anotación marcada, que sí cuenta; ni anotación aislada, que nunca entró.
- **Fuente:** ESPEC-contrato-ingesta A-32, A-31
- **Estado:** PROPUESTO

### Anotación vieja · técnico
- **Es:** una anotación que lleva más de 3 días capturada sin llegar al nodo de finca; todavía se entrega, pero queda marcada para que el gerente decida si se recaptura.
- **No es / se confunde con:** anotación obsoleta.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · ADR-025 §2 (el umbral es decisión de negocio)
- **Estado:** ACORDADO 16-oct-2026

### Asignación (orden de captura) · negocio
- **Es:** la orden de capturar una cama, dada a un dispositivo o a un usuario para una jornada; si nadie la sincronizó, se puede volver a dar ese mismo día a otro trabajador de otro turno.
- **No es / se confunde con:** la captura misma; la asignación es lo esperado, la captura es lo recibido.
- **Ejemplo:** la cama 12 le toca a D-014 (turno de 6 a 2) el 16-oct; como no llegó, se reasigna a D-022 (turno de 2 a 10) el mismo día.
- **Fuente:** ADR-026 («la asignación va acotada en el tiempo») · aclaración de Juan 16-oct (ronda 1e)
- **Estado:** ACORDADO 16-oct-2026

### Aviso de descarte · técnico
- **Es:** la línea que el celular envía en su siguiente partición para decir qué consecutivo borró sin entregar y por qué; el nodo de finca cierra ese hueco y lo deja en la bitácora.
- **No es / se confunde con:** una anotación: no lleva datos del campo.
- **Ejemplo:** `{ "consecutivo": 137, "motivo": "fallo_tecnico" }`.
- **Fuente:** ESPEC-contrato-ingesta A-47
- **Estado:** ACORDADO 16-oct-2026

### Blindaje · técnico
- **Es:** guardar un campo desconocido como texto inerte, con topes, sin convertirlo en objetos ni evaluarlo nunca.
- **No es / se confunde con:** aceptar el campo; ya no se usa: ningún campo desconocido entra (A-25 retirada).
- **Fuente:** ESPEC-contrato-ingesta A-25
- **Estado:** REEMPLAZADO 16-oct-2026 · ver Fallo técnico (ronda 1e P8)

### Captura incompleta · negocio
- **Es:** una captura válida a la que le falta producto o un dato no obligatorio, con motivo o sin él; sí llega al sistema y queda para el gerente.
- **No es / se confunde con:** captura sin el mínimo requerido, que nunca llega; ni fallo técnico.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · P-S01 · ESPEC-contrato-ingesta A-35
- **Estado:** ACORDADO 16-oct-2026

### Captura válida · negocio
- **Es:** la captura de una cama que tiene completo el mínimo requerido de su actividad; solo así se vuelve anotación y puede llegar al sistema.
- **No es / se confunde con:** captura incompleta, que tiene el mínimo pero le falta producto o algún dato no obligatorio.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · ADR-024 (la validación va antes del evento)
- **Estado:** ACORDADO 16-oct-2026

### Clave del hecho · técnico
- **Es:** la tupla que identifica un hecho, declarada por campo en el catálogo; por defecto producción · sección · campo · jornada. Dos anotaciones con la misma clave hablan del mismo hecho.
- **No es / se confunde con:** el `anotacion_uuid`, que identifica la anotación, no el hecho.
- **Fuente:** ESPEC-contrato-ingesta A-3 (antes ADR-027 §3, deprecado, K-41)
- **Estado:** PROPUESTO

### `confirmado_hasta` · técnico
- **Es:** el mayor consecutivo que el nodo de finca ya recibió sin huecos por debajo; le dice al celular desde dónde seguir enviando.
- **No es / se confunde con:** `respaldado_hasta`. Recibido no es respaldado: el celular no borra hasta aquí.
- **Ejemplo:** recibidas 1-200 y 401-600 → `confirmado_hasta = 200`.
- **Fuente:** ESPEC-contrato-ingesta A-26, A-27
- **Estado:** PROPUESTO

### Consecutivo · técnico
- **Es:** el número que cada celular pone a sus anotaciones, 1, 2, 3…, en el orden en que las guarda; solo tiene sentido dentro de un mismo celular.
- **No es / se confunde con:** el `anotacion_uuid`, que identifica la anotación. El 812 del celular A y el 812 del celular B son anotaciones distintas.
- **Fuente:** ESPEC-contrato-ingesta A-7, A-27 (antes ADR-027 §5, deprecado, K-41)
- **Estado:** PROPUESTO

### Consolidación · técnico
- **Es:** reescribir la ESPEC con las reglas vigentes, sin la historia de rondas, para que no se contradigan entre sí; la historia queda en un anexo.
- **No es / se confunde con:** una ronda de preguntas: no decide nada nuevo.
- **Fuente:** decisión de Juan 16-oct (tras la ronda 1f)
- **Estado:** ACORDADO 16-oct-2026

### Constancia · técnico
- **Es:** la línea que el nodo de finca deja en la bitácora de una captura obsoleta o superada: quién, qué cama, qué jornada y qué la superó. La de una obsoleta tardía no guarda el dato; la de una **superada** conserva sus bytes.
- **No es / se confunde con:** la lápida, que guarda el celular.
- **Fuente:** ESPEC-contrato-ingesta A-45 ⟦1⟧
- **Estado:** PROPUESTO

### Corrección · negocio
- **Es:** el cambio que hace el gerente de producción sobre información ya registrada, al revisar lo marcado y las desviaciones; no la hace el operario de campo.
- **No es / se confunde con:** la edición local del operario antes de sincronizar, ni la recaptura de una cama; tampoco `corrige_a`.
- **Fuente:** aclaración de Juan 16-oct (ronda 1e P4)
- **Estado:** ACORDADO 16-oct-2026

### `corrige_a` · técnico
- **Es:** el campo de una anotación que apunta al id de otra anotación del mismo hecho para corregirla; puede apuntar a una de otro celular o que todavía no ha llegado.
- **No es / se confunde con:** la corrección que hace el gerente de producción en el tablero, que es una tarea de revisión, no una anotación.
- **Ejemplo:** el operario anotó 40 tallos por error y luego anota 45 con `corrige_a` apuntando a la de 40.
- **Fuente:** ADR-027 §4 · ESPEC-contrato-ingesta A-4, A-28
- **Estado:** REEMPLAZADO 16-oct-2026 · ver Corrección, Edición local (sale del contrato, ronda 1e P4)

### Cuarentena · técnico
- **Es:** guardar aparte, fuera del estado, los bytes de una anotación rechazada por corrupta, para poder revisar cuál de las dos copias era la buena.
- **No es / se confunde con:** la constancia, que es de obsoletas y superadas.
- **Fuente:** ESPEC-contrato-ingesta A-2 (ronda 1h P9, COH-57)
- **Estado:** ACORDADO 16-oct-2026

### Dato ausente · técnico
- **Es:** no hay cifra; el celular lo declara con `valor: null` y, si lo sabe, su `motivo_ausencia`; entra como captura incompleta.
- **No es / se confunde con:** desviación, donde sí hay cifra pero rara; ni hueco, que es un consecutivo que no llegó.
- **Fuente:** ESPEC-contrato-ingesta A-20, A-35
- **Estado:** PROPUESTO

### `desfase_declarado_ms` · técnico
- **Es:** la diferencia entre el reloj del celular y el del nodo de finca, medida en la última sincronización; `null` si el celular nunca sincronizó.
- **No es / se confunde con:** el sello normalizado, que el nodo de finca calcula con este valor.
- **Fuente:** ADR-031 §2 · ESPEC-contrato-ingesta A-16
- **Estado:** PROPUESTO

### Desviación · negocio
- **Es:** un cambio brusco o raro en la cantidad de producto, según las reglas del catálogo; es el único caso en que el operario debe dar un motivo, en la primera captura o en la siguiente.
- **No es / se confunde con:** divergencia (el celular y el nodo de finca juzgan distinto) ni dato ausente.
- **Fuente:** aclaración de Juan 16-oct (ronda 1e P4) · RF-011
- **Estado:** ACORDADO 16-oct-2026

### Divergencia · técnico
- **Es:** el nodo de finca y el celular juzgan distinto la misma anotación con la misma versión de reglas; es un fallo del motor, no del operario, y entra marcada.
- **No es / se confunde con:** fallo técnico, que se descarta y se recaptura; ni producto faltante.
- **Fuente:** ADR-014 · IA_TX1 ES-S11 · ESC-57 · ESPEC-contrato-ingesta A-32
- **Estado:** ACORDADO 16-oct-2026

### Edición local · técnico
- **Es:** el cambio que el operario hace en su celular a una captura que todavía no salió en ninguna partición; reemplaza el valor sin dejar rastro y solo viaja la versión final.
- **No es / se confunde con:** corrección (la hace el gerente) ni recaptura (una captura nueva de la misma cama).
- **Ejemplo:** captura cama 2 → cama 4 → cambia los tallos de la cama 2; viaja una sola captura de la cama 2, con el valor final.
- **Fuente:** aclaración de Juan 16-oct (ronda 1e P4)
- **Estado:** ACORDADO 16-oct-2026

### Espera creciente · técnico
- **Es:** el tiempo que el celular espera antes de reintentar, que se duplica en cada intento fallido hasta un tope, con un poco de azar para que no reintenten todos a la vez.
- **No es / se confunde con:** `Retry-After`, que es la espera que pide el nodo de finca en su respuesta.
- **Ejemplo:** 1 s, 2 s, 4 s, … hasta 5 min.
- **Fuente:** ESPEC-contrato-ingesta A-40
- **Estado:** PROPUESTO

### Fallo técnico · técnico
- **Es:** una anotación que no se puede usar por cómo está construida: JSON mal formado, datos sin sanear o cualquier error de software; se descarta y se pide recapturar.
- **No es / se confunde con:** captura incompleta, que es un hecho del campo y no un error.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d)
- **Estado:** ACORDADO 16-oct-2026

### Hueco · técnico
- **Es:** un consecutivo que falta entre dos que sí llegaron al nodo de finca, por ejemplo cuando una partición falló y la siguiente entró.
- **No es / se confunde con:** un dato ausente (`valor: null`), que sí llegó pero sin cifra.
- **Ejemplo:** llegaron 1-200 y 401-600 → el hueco es 201-400.
- **Fuente:** ESPEC-contrato-ingesta A-27
- **Estado:** PROPUESTO

### Huella · técnico
- **Es:** el SHA-256 de la anotación recibida en forma canónica (RFC 8785, sin miembros nulos), con la versión de su algoritmo; detecta si el mismo id llega con otro contenido.
- **No es / se confunde con:** el `anotacion_uuid`, que identifica la anotación pero no su contenido.
- **Fuente:** ESPEC-contrato-ingesta A-19, A-33
- **Estado:** PROPUESTO

### Invariante · técnico
- **Es:** una regla que el sistema cumple siempre, en cualquier estado, y que una prueba puede comprobar.
- **No es / se confunde con:** una validación de entrada, que se revisa una vez al recibir un dato.
- **Ejemplo:** `respaldado_hasta ≤ confirmado_hasta`.
- **Fuente:** COH-13
- **Estado:** PROPUESTO

### Jornada de captura · negocio
- **Es:** la salida de un operario a revisar lo que le asignaron: va sin internet, captura las camas y vuelve a la oficina a sincronizar.
- **No es / se confunde con:** la jornada de la clave del hecho (el día al que pertenece un dato); ni la sesión de sincronización.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · ADR-026 (asignación)
- **Estado:** ACORDADO 16-oct-2026

### Lápida · técnico
- **Es:** el registro mínimo (consecutivo y motivo) que el celular guarda de algo que borró sin respaldo, para volver a avisarlo si una marca de agua baja.
- **No es / se confunde con:** el aviso de descarte, que es el envío; la lápida es lo que el celular conserva.
- **Fuente:** revisión de coherencia ronda 1d, COH-19 · ESPEC-contrato-ingesta A-48
- **Estado:** ACORDADO 16-oct-2026

### Lector tolerante · técnico
- **Es:** quien lee un mensaje e ignora los campos que no conoce, en vez de fallar. En FlorLogic no aplica a lo que entra al nodo de finca; lo del celular va con despliegue y actualización.
- **No es / se confunde con:** blindaje, que guardaba lo desconocido.
- **Fuente:** ESPEC-contrato-ingesta A-43, K-42
- **Estado:** PROPUESTO

### Login local · técnico
- **Es:** la entrada del operario en su celular, sin red, que mantiene su identidad presente al capturar; sigue funcionando aunque la credencial haya vencido.
- **No es / se confunde con:** la renovación de la credencial, que se hace en la oficina contra el nodo de finca.
- **Fuente:** aclaración de Juan 16-oct (ronda 1f P9) · ADR-007
- **Estado:** ACORDADO 16-oct-2026

### Lote · negocio
- **Es:** una plantación de una variedad, en un periodo con fechas reales, que puede ocupar varias secciones (y por tanto varios bloques y camas).
- **No es / se confunde con:** partición. No se captura el lote; se capturan las camas.
- **Fuente:** ADR-024 §2 · ESPEC-contrato-ingesta A-30
- **Estado:** ACORDADO 16-oct-2026

### Marca de agua · técnico
- **Es:** un número que dice hasta dónde se llegó, sin huecos, en una secuencia; aquí son `confirmado_hasta` y `respaldado_hasta`, una pareja por celular.
- **No es / se confunde con:** el acuse, que es por anotación.
- **Fuente:** ESPEC-contrato-ingesta A-26, A-31
- **Estado:** PROPUESTO

### Nodo de finca (nodo principal) · técnico
- **Es:** la máquina en las oficinas de cada empresa donde corre FlorLogic con su base de datos; los celulares le entregan las anotaciones por la red local, sin internet.
- **No es / se confunde con:** la nube (servicios en línea), que solo guarda respaldos y distribuye versiones; el celular, que es el registro durante la jornada; ni el nodo de contingencia (OC-2). **Se dice «nodo de finca» o «nodo principal», nunca «nodo» a secas.**
- **Ejemplo:** el computador de la oficina de la finca al que se sincronizan los celulares al volver del campo.
- **Fuente:** documento de alternativa de solución (§ «Un solo producto, instalado N veces») · CN-37 · ADR-016
- **Estado:** ACORDADO 16-oct-2026

### Nube (servicios en línea) · técnico
- **Es:** la capa compartida fuera de la finca que guarda respaldos cifrados, distribuye versiones y ofrece IA; la finca no depende de ella para operar.
- **No es / se confunde con:** el nodo de finca, donde vive el dato vivo.
- **Fuente:** documento de alternativa de solución · CN-17 · ESC-59
- **Estado:** PROPUESTO

### Paquete de configuración · técnico
- **Es:** el conjunto versionado de catálogo y reglas, en un solo paquete, que el nodo de finca crea y entrega al celular; cada anotación dice con cuál se capturó (`version_config`).
- **No es / se confunde con:** la versión de la app, la del motor o la del contrato, que son ejes aparte.
- **Fuente:** ADR-029 · ESPEC-contrato-ingesta A-50 (ronda 1g P7)
- **Estado:** ACORDADO 16-oct-2026

### Par cumple/viola · técnico
- **Es:** para una decisión de la ESPEC, un ejemplo que la cumple y otro que no; después se convierte en prueba.
- **No es / se confunde con:** un caso *dado / cuando / entonces*, que describe un comportamiento completo.
- **Fuente:** skill detallar-para-codificar (`indice-precision.md`)
- **Estado:** PROPUESTO

### Partición · técnico
- **Es:** un trozo, de hasta `max_anotaciones_particion`, de lo que un celular tiene pendiente por sincronizar; sube en un solo POST.
- **No es / se confunde con:** lote. Puede traer anotaciones de varias camas y varios lotes, pero nunca de dos celulares.
- **Ejemplo:** 1.000 anotaciones pendientes salen en 5 particiones de 200.
- **Fuente:** ESPEC-contrato-ingesta A-30
- **Estado:** ACORDADO 16-oct-2026

### Primer acuse · técnico
- **Es:** el momento, medido con el reloj del nodo de finca (`recibido_ms`), en que el celular recibió por primera vez la respuesta para una anotación; el celular lo devuelve tal cual tras una restauración para medir su edad.
- **No es / se confunde con:** el acuse en general, que puede repetirse en cada reenvío.
- **Fuente:** ESPEC-contrato-ingesta A-45 ⟦2⟧
- **Estado:** PROPUESTO

### Prueba de restauración · técnico
- **Es:** restaurar un respaldo en una base aparte y compararlo, para saber que de verdad sirve; `respaldado_hasta` solo cuenta respaldos que la pasaron.
- **No es / se confunde con:** la verificación del respaldo (suma de control y lista del contenido), que no prueba que restaure.
- **Fuente:** TX-2 paso 17 · ESPEC-contrato-ingesta A-31 (frecuencia: TX-2, K-67)
- **Estado:** PROPUESTO

### Recaptura · negocio
- **Es:** una captura nueva de la misma cama, con su propio id, para cambiar algo que ya salió del celular o que se perdió; la más reciente decide el estado.
- **No es / se confunde con:** edición local, que cambia en su lugar lo que todavía no salió; ni corrección, que la hace el gerente.
- **Fuente:** aclaración de Juan 16-oct (ronda 1e P4, ronda 1f P1) · ESPEC-contrato-ingesta A-49
- **Estado:** ACORDADO 16-oct-2026

### Renovación de credencial · técnico
- **Es:** volver a verificar al operario contra el nodo de finca, en la oficina y antes de sincronizar; la persona verificada así es la dueña de la sincronización (la sesión es de la persona, no de la credencial).
- **No es / se confunde con:** el login local, que no necesita red.
- **Fuente:** aclaración de Juan 16-oct (ronda 1f P9) · ES-S12
- **Estado:** ACORDADO 16-oct-2026

### `respaldado_hasta` · técnico
- **Es:** el mayor consecutivo que ya está dentro de un respaldo verificado, en la copia local y en la nube; es el límite de borrado de lo acusado (regla completa en A-48).
- **No es / se confunde con:** `confirmado_hasta`, que solo dice qué recibió el nodo de finca.
- **Fuente:** ESPEC-contrato-ingesta A-31
- **Estado:** PROPUESTO

### Respaldo · técnico
- **Es:** una copia cifrada y verificada de la base del nodo de finca que sale de la máquina: una copia local física y otra en la nube.
- **No es / se confunde con:** la retención en el celular, que guarda las anotaciones hasta que un respaldo las cubra.
- **Fuente:** TX-2 · ESC-03 · ESPEC-contrato-ingesta A-31
- **Estado:** PROPUESTO

### Respuesta normal · técnico
- **Es:** una respuesta del nodo de finca que demuestra que la sincronización funciona; solo con ella corre la ventana de retención. Su alcance exacto está pendiente (COH-43).
- **No es / se confunde con:** cualquier respuesta: un `422 contrato-incompatible` es una anomalía.
- **Fuente:** ESPEC-contrato-ingesta A-45 ⟦3⟧
- **Estado:** PROPUESTO

### Restauración · técnico
- **Es:** volver a poner en marcha el nodo de finca desde un respaldo, por ejemplo tras un daño; el nodo de finca queda como estaba en el momento de ese respaldo.
- **No es / se confunde con:** un reenvío del celular. Después de restaurar, el celular reenvía lo que el respaldo no tenía.
- **Fuente:** TX-2 ES-R14 · ES3 · ESPEC-contrato-ingesta A-31, C-27
- **Estado:** PROPUESTO

### Sello normalizado · técnico
- **Es:** la hora de captura corregida por el nodo de finca con el desfase que declaró el celular; es la hora con la que se ordenan las capturas.
- **No es / se confunde con:** el sello crudo (`crudo_ms`), que es el reloj del celular sin corregir.
- **Fuente:** ADR-031 §2 · ESPEC-contrato-ingesta A-6, A-16, A-45
- **Estado:** PROPUESTO

### Sesión de captura · negocio
- **Es:** el trabajo de una persona en campo con una misma versión de la app; actualizar la app abre una sesión nueva.
- **No es / se confunde con:** sesión de sincronización. No lleva el dispositivo.
- **Fuente:** ESPEC-contrato-ingesta A-18
- **Estado:** PROPUESTO

### Sesión de sincronización · técnico
- **Es:** una subida de un celular al nodo de finca; la crea el servidor, es **de la persona** que entrega (no de su credencial) y lleva dispositivo, usuario, versiones de configuración, conteos y resultado.
- **No es / se confunde con:** sesión de captura, que es el trabajo en campo.
- **Fuente:** ESPEC-contrato-ingesta A-5
- **Estado:** PROPUESTO

### Ventana de retención · técnico
- **Es:** los 7 días, contados solo mientras el nodo de finca responde, tras los cuales lo no entregado pasa al tablero del gerente, que decide recapturar o descartar; el celular nunca lo borra solo.
- **No es / se confunde con:** la retención hasta `respaldado_hasta`, que aplica a lo que sí se entregó.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · ronda 1e P1 · P-S03 · ADR-025 §2-4
- **Estado:** ACORDADO 16-oct-2026

### Verificada · técnico
- **Es:** dicho de una captura: aplicada en el nodo de finca **sin ningún código de marca** (ni de hora, ni de motor, ni incompleta, ni de dispositivo); solo una captura verificada puede dejar obsoleta a otra, y solo en el mismo (sección, campo).
- **No es / se confunde con:** aceptada por el celular (`veredicto_local`).
- **Fuente:** ESPEC-contrato-ingesta A-45 ⟦1⟧ (rondas 1f P3, 1g P2)
- **Estado:** ACORDADO 16-oct-2026
