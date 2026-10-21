# Contexto del proyecto FlorLogic — traspaso a una nueva conversación

Pega este archivo completo al inicio de un chat nuevo. Contiene todo lo necesario para retomar el trabajo sin repetir la investigación.

**Versión 2.0 — actualizada el 5 de agosto de 2026, después de la sesión 2 del 4 de agosto.**
La versión 1.0 solo contenía la sesión 1. Todo lo que cambió está marcado con `[S2]`.

---

## 1. Quién es el usuario y qué está haciendo

Ruben, analista/estudiante de ingeniería de software. Está haciendo el **levantamiento de requisitos** de un sistema para una **finca de flores de corte**. No es el cliente: es quien entrevista al cliente y traduce lo que dice a requisitos.

`[S2]` **El equipo son tres:** Ruben, Juan Pablo Avendaño y Jerónimo Montoya. Es un proyecto de semestre de ingeniería de software, con un profesor que aprueba el alcance. El producto se llama **FlorLogic**.

`[S2]` **El entrevistado** es el Director de Producción de la finca. En la transcripción de la sesión 2 se le llama **Gustavo**. Es la única persona entrevistada y el cliente descartó explícitamente entrevistar a otras áreas por ahora.

**Preferencias de trabajo:** respuestas concisas y directas, sin relleno. Español. Entrega archivos reales (Excel, Word), no texto en el chat. Valora que se le señalen errores propios sin suavizarlos.

---

## 2. El proyecto del cliente

**Sistema de proyección de producción y ventas con registro continuo del estado del cultivo**, para una finca de flores de corte.

### Escala y estructura física

| Dato | Valor | Fuente |
|---|---|---|
| Área en producción | 15 hectáreas | S1 |
| Bloques | 25 | `[S2]` |
| Camas | ~1.525 | `[S2]` |
| Operarios en campo | 156 + ~11 administrativos | `[S2]` |
| Personal en poscosecha | 57 + 4 administrativos | `[S2]` |
| Jerarquía | Finca → Bloques (de área distinta entre sí) → Naves → Camas → Variedades | S1 |

Dentro de una cama puede haber subvariedades y varios colores. Normalmente una variedad por cama, pero se puede dar el caso de dos mezcladas.

`[S2]` **Cómo se calcula la capacidad de una cama:** cada cama tiene largo × ancho → área en m². El **ingeniero agrónomo** define la **densidad de siembra** en plantas por m² según la variedad (ejemplos que dio: 90 plantas/m², 60 plantas/m²). Planeación debe respetar esa densidad.

### Ciclo productivo

- **Siembra a corte:** 3 a 5 meses según variedad.
- `[S2]` **Corrimiento del ciclo:** hasta **15 días** de adelanto o atraso, que sobre un ciclo de ~90 días es un **6% de margen**.
- `[S2]` **Duración del corte:** una cama no corta todo en un día. El corte dura alrededor de **7 días**, según variedad.
- `[S2]` **Etapas y días a corte:** desbotone → corte ≈ **3 semanas**. Botón color → corte ≈ **12 días**. Depende de la variedad. El cliente no quiso añadir más etapas por ahora.
- `[S2]` **Qué corre el ciclo — aclaración importante:** en la sesión 1 el cliente dijo "dentro del ciclo hay una serie de **variedades** que pueden o no afectar la producción". Confirmado en la sesión 2: **quiso decir ACTIVIDADES**, no variedades ni variables. Los ejemplos que dio son desbotone, corte o retirada de luces, profundidad de siembra y fumigaciones. Esas actividades son justo lo que habría que registrar.
- **Erradicación:** se puede erradicar una cama a mitad de ciclo por problemas fitosanitarios, enfermedad o falta de ventas. Decide el ingeniero de producción o el gerente de ventas.
  - `[S2]` Puede ocurrir **semanalmente**; hay unas más graves que otras.
  - `[S2]` Se debe digitar para que el sistema **reste** esa producción proyectada.
  - `[S2]` Lo que ya estaba vendido de ahí se **cancela o se compra a terceros**.
  - `[S2]` El ciclo vuelve a cero. El tiempo hasta resembrar depende de la necesidad, no hay una regla fija.

### El motor de proyección — lo que ya se sabe

`[S2]` La sesión 2 destrabó la estructura del cálculo, que en la sesión 1 solo existía como juicio experto:

```
plantas_sembradas  = área_cama_m² × densidad_siembra(variedad)
tallos_proyectados = plantas_sembradas × %productividad_esperada(variedad)
```

- El `%productividad_esperada` es del orden de **80–90%** según variedad. **Nunca puede superar el 100%**: si el sistema arroja más tallos que plantas sembradas, hay un dato malo y debe rechazarse.
- Ejemplo textual del cliente: 1.000 plantas sembradas, 90% de productividad → 900 tallos proyectados.

### La meta de exactitud — formulación correcta

`[S2]` **El corte real debe quedar dentro del ±10% de lo que el sistema proyectó.** Sobre una proyección de 900 tallos, la banda aceptable es 810 a 990.

Ese ±10% es el **umbral de aceptación**, no la meta. **El objetivo del proyecto es reducir esa brecha lo más posible**, para que la diferencia entre el corte real y lo proyectado sea la mínima. La desviación puede ser por encima o por debajo: el cliente insistió en que no solo se daña flor, también puede salir mejor de lo esperado.

> **Ambigüedad abierta y crítica (BR-21).** Hoy el presupuesto contra el corte real está en **−6%**. Si la proyección —que se ajusta continuamente y con más información que el presupuesto anual— se propone como meta un ±10%, la meta es *más floja* que lo que ya logran. La explicación probable es el **nivel de agregación**: el 6% sería finca/año y el ±10% sería cama o variedad por semana. Hay que confirmarlo antes de fijar la línea base del proyecto, porque de ahí depende la métrica de éxito.

### Horizonte y granularidad

- **Horizonte de proyección:** 1 a 1.5 años. La unidad de tiempo base es el **día**.
- `[S2]` **El presupuesto se arma en días, semanas o meses según quién lo consume:**
  - Gerencia general → **mensual** (le interesa cuánta plata entra y cuánta sale).
  - Gerente de ventas → **semanal**.
  - Gerente de producción → **diario**.
- `[S2]` **Frecuencia de ajuste de la proyección:** hoy se ajusta **mensualmente** o cuando es necesario. **Se quiere que sea semanal.** Esa es una brecha entre el estado actual y el deseado, y es requisito.

### Clasificación comercial

Grados, que agrupan calidades (longitud de tallo, tamaño de flor, número de flores por tallo, grosor, follaje). Cada finca define las suyas. **El catálogo de grados de esta finca sigue sin entregarse** (BR-11).

### Captura de datos hoy — el dolor cuantificado

| Métrica | Valor |
|---|---|
| Supervisor llenando formatos a lápiz y papel | 1 hora/día |
| Practicante digitando | 4 horas/semana |
| Latencia hasta que planeación y gerencia ven el dato | 8 días |
| Error de captura declarado | 2% |
| `[S2]` Horas semanales del director revisando siembra | ~4 horas |

`[S2]` **Datos nuevos sobre la captura:**
- **Solo 3 personas ingresan información:** 1 supervisor + 2 auxiliares.
- **~12 personas** usarían el sistema en total; **~20 más** solo consultan (las vendedoras).
- El puesto del digitador es **rotativo** (practicante de ingeniería de sistemas). Al principio se demora más y se equivoca más. El cliente lo señaló como una falla del proceso actual.
- **Corrección de datos:** un dato mal registrado solo lo puede corregir el **ingeniero de sistemas**, con aprobación del gerente o ingeniero de producción.
- El 2% de error lo revisa un **auditor** (normalmente el ingeniero de sistemas) y debe llevarlo a 0%. Ese 2% no está visualizado en ninguna parte.

### Conectividad — contradicción resuelta

`[S2]` **No es contradicción, es arquitectura offline-first.** El formato de plagas y enfermedades se llena en una app **totalmente local en el celular**, y **sube a la nube después**, cuando hay red. La app es propia de la empresa. Se llama *formato de monitoreo de plagas y enfermedades*.

Esto confirma que hay precedente de captura móvil offline funcionando en la finca, y sirve de modelo.

### Sistemas existentes

- Hay **un sistema en operación** que arroja "productividades" en tallos. `[S2]` El cliente **no recordó el nombre** en la sesión 2. Existe y se puede sacar un pantallazo, pero aún no se ha visto.
- **PowerBI** produce reportes semanales: siembra, producción, plagas y enfermedades, inventario de material vegetal, pérdida de flor, estimados de flor.
- `[S2]` Sobre PowerBI: *"está y se utiliza, pero no es la forma más óptima. No es que no sirva, se puede mejorar cambiando el enfoque."* Sigue sin decidirse si se queda o se reemplaza.
- Existen sistemas de **nómina y contabilidad**, sin explorar.
- `[S2]` **El motivo real del proyecto**, en boca del cliente: *"mejorar el motor de base de datos, llevar los datos que tiene actualmente a una base robusta, y tener una aplicación enfocada a celulares y web."*

### Negocio

- **Temporada pico:** `[S2]` +60% en tallos, +60% en registros, +30–40% en personal. Es proporcional.
- `[S2]` **Costo del incumplimiento:** alrededor del **8% de las ventas** se tuvo que conseguir comprando a terceros o cancelando órdenes. La compra a terceros ocurre **mensualmente**. Falta el valor en dinero.
- **Certificación:** Florverde. `[S2]` **Excluida del alcance** por decisión del cliente.
- `[S2]` **Clima:** registran pluviometría y temperatura, y tienen una estación meteorológica, **pero no está en uso y no se hace nada con esos datos** en producción. Hueco lógico: el cliente dice que el ciclo se corre por clima, pero no usa la variable.

---

## 3. Alcance — lo que quedó decidido

`[S2]` Estas decisiones están cerradas y no hay que volver a abrirlas sin motivo:

**DENTRO del alcance**
- Captura en campo de **siembra** y **producción** por cama, offline.
- Proyección de producción por cama, variedad y fecha.
- Registro de erradicaciones y bajas de producción, con recálculo hacia adelante.
- Reportes y proyección de ventas a partir de la producción proyectada.
- Trazabilidad completa hasta la cama de origen.
- Parametrización por parte del administrador (ciclos, días a corte, densidades, márgenes).

**FUERA del alcance de la fase 1**
- **Cruce con pedidos y clientes.** El cliente eligió explícitamente la opción A: *"solamente va a mostrar qué flor se va a producir."* No muestra qué está comprometido ni con qué cliente.
- **Florverde.** *"No nos metamos en Florverde, es otra cuestión adicional."*
- **Gestión de personal e insumos.** *"No nos metamos con personal, en algún momento va a tocar."*
- **Registro de actividades culturales** como obligatorio. Se puede habilitar, pero hoy no se registran.

---

## 4. Roles del sistema

`[S2]` Quedaron definidos **tres roles**, y el cliente fue explícito en no querer más:

| Rol | Quién es | Qué puede hacer |
|---|---|---|
| **Supervisor de campo** | Supervisor y 2 auxiliares de siembra | Ingresar datos. Ver reportes sencillos de siembra y de lo que ellos mismos ingresan. **No ve precio de venta.** El costo de producción sí lo llegan a conocer. |
| **Administrador de producción y ventas** | Gerencia, planeación, ventas, producción — toda la gente que toma decisiones | Acceso amplio a proyecciones, informes y datos de todos los bloques. |
| **Administrador del sistema (superusuario)** | El ingeniero de sistemas | Configura y parametriza el sistema, otorga permisos, corrige datos erróneos. |

`[S2]` **Sobre visibilidad entre bloques:** *"hay información muy compartida en todo el sistema, no hay restricción."* La única restricción real es el **precio de venta** frente a supervisores y auxiliares.

---

## 5. Atributos de calidad (mini QAW)

`[S2]` Prioridades que dio el cliente en la sesión 2 (1 = máxima):

| # | Atributo |
|---|---|
| 1 | Confiabilidad |
| 2 | Experiencia de usuario |
| 3 | Disponibilidad |
| 3 | Interoperatividad |
| 4 | Seguridad |
| 5 | Rendimiento |
| 6 | Capacidad para ser soportado |
| 7 | Capacidad para ser administrado |
| 8 | Trazabilidad |
| 9 | Capacidad |
| 10 | Accesibilidad |
| 11 | Portabilidad |
| 12 | Escalabilidad |
| 14 | Seguridad de funcionamiento (safety) |

*Compatibilidad* se eliminó por estar duplicada con interoperatividad. Hay números repetidos y saltos que hay que limpiar antes de usar la matriz de trade-off.

**Disponibilidad, en números:** `[S2]` si el sistema se cae, pueden aguantar alrededor de **1 hora**. Cuatro horas es demasiado. Existe plan B en papel y luego se sube la información, pero el cliente insistió en que *"hay que seguir trabajando sí o sí"*.

> **Tensión que hay que resolver (BR-24).** El cliente puso **accesibilidad en 10 de 14**, casi al final. Pero accesibilidad se definió en sesión como *"capacidad de ser interpretado por personas con falta de digitalización o analfabetismo"*, que es exactamente la premisa que justifica el agente de IA de captura. Probablemente el cliente no entendió el término: puso experiencia de usuario en 2 y repitió *"que sea fácil"* varias veces. Hay que reabrir esa fila con la definición explícita antes de defender el agente.

---

## 6. La decisión del agente de IA para captura

**Esto no salió de la entrevista: es una propuesta de diseño del equipo.** Conviene tenerlo claro y documentarlo como tal.

**El requisito que atiende sí está en la documentación:** la captura tiene que ser **más rápida que lápiz y papel**, en celular, con personal de baja digitalización, sin conexión. Un formulario largo de plantilla en un celular es más lento que un formato en papel. El cliente lo dijo casi con esas palabras: *"que sea fácil, que a veces las aplicaciones ponemos tanta arandela que es muy complicado."*

**La propuesta:** un agente local en la app móvil que **interpreta lenguaje natural** y coloca la información en las plantillas definidas.

**Separar requisito de solución** (convención del propio catálogo: sin solución técnica en el enunciado):

- **Requisito:** el sistema debe permitir registrar la siembra de una cama en menos de N segundos, sin conexión. *(Falta N. El escenario de calidad bocetado en la sesión 2 usa 3 segundos para la confirmación de captura.)*
- **Decisión de arquitectura:** captura por lenguaje natural con agente local que interpreta y llena la plantilla.

**Las tres cosas que deciden su viabilidad:**

1. **Offline es la restricción que manda.** Todo debe correr en el dispositivo. Un LLM general en gama baja es pesado — pero probablemente no hace falta: los valores válidos son un **conjunto cerrado** (bloque, nave, cama, variedad, actividad, cantidad). Eso pide reconocimiento restringido contra el catálogo de la finca, no generación abierta. Mucho más liviano y mucho más exacto.
2. **El vocabulario técnico es el riesgo real.** La transcripción automática de estas mismas reuniones destrozó *desbotone*, *esqueje*, *botón color* y *pompón*. En un invernadero con ruido va a pasar lo mismo. Restringir el vocabulario al catálogo no es una optimización, es lo que hace que funcione.
3. **Confiabilidad es el atributo #1.** Un agente que interpreta mal un número de cama pega directo contra la prioridad más alta del cliente. Regla de diseño: **el agente propone, el sistema valida, el usuario confirma.** Nunca escritura silenciosa. Las validaciones duras que el cliente ya pidió (no se puede registrar corte en una cama sembrada hace una semana) corren sobre el resultado interpretado.

**Contraargumento honesto que hay que poder responder:** son solo 3 personas capturando datos, y los registros son muy repetitivos (misma secuencia de camas, misma variedad, casi los mismos campos cada día). Un formulario bien diseñado con valores por defecto, memoria del último valor y campos grandes podría dar buena parte del beneficio de velocidad a una fracción del costo. Si se defiende el agente, hay que defenderlo con una medición, no con la intuición.

---

## 7. Estado del levantamiento

**Sesión 1** (27-jul-2026, grabada): conversación de propuesta y encuadre. El cliente vende la idea, habla de trabajo offline con sincronización, notificaciones, mapa de calor de camas, control de acceso basado en roles, una capa de IA sobre el PowerBI existente, y modelo SaaS (~10 USD/usuario/mes, rentable desde ~20 empresas). Mencionó que su modelo de datos actual tiene ~300 tablas, 45 solo de producción.

**Entrevista de 46 preguntas** (no grabada): el guion completo. Resultado: 10 completas, 21 parciales, 3 vagas, 2 mal interpretadas, 4 contradictorias, 6 sin responder. Calificación 4.6/10 ponderada, 5.5/10 promedio simple.

**Sesión 2** (4-ago-2026, grabada): recorrido de las brechas del archivo 3 + taller de atributos de calidad + definición de roles. Cerró la mayoría de las brechas de alcance y roles, y destrabó parcialmente el motor de proyección.

`[S2]` **Calificación actualizada: 6.7/10 ponderada** (venía de 4.6). Eso significa: *suficiente para una propuesta de solución y un estimado con rango amplio; todavía no alcanza para construir.*

`[S2]` **El propio cliente calificó el contexto en 5/10** y dijo: *"está muy mal, por eso hay que volverla a hacer, hasta conseguir un 9 con algo."*

---

## 8. Los cuatro asuntos que mandan sobre todo lo demás

**A. El motor de proyección sigue incompleto, pero ya no está vacío.** Se tiene área × densidad × %productividad y el tope del 100%. Faltan tres piezas:
   1. **De dónde sale el %productividad esperada por variedad.** ¿Histórico? ¿Tabla del ingeniero? ¿Ficha del proveedor de material vegetal? Sin esto no hay proyección.
   2. **Cómo se distribuyen los tallos en los ~7 días de corte.** Hoy no hay curva: una cama no corta todo el mismo día.
   3. **Con qué criterio se aplica el porcentaje de baja.** Sigue siendo juicio del director.

**B. El sistema actual sigue sin identificar.** Se sabe para qué sirve y que el objetivo es migrar su motor de base de datos, pero no se sabe cómo se llama, quién lo administra ni qué guarda. No se ha visto una pantalla ni un export. Sigue sin decidirse si FlorLogic reemplaza, alimenta o convive con lo que hay.

**C. Los documentos siguen sin entregarse.** Plan de siembra, presupuestos de producción y ventas, formatos en papel llenos, tabla de grados y calidades, histórico de siembra y producción. Todo quedó prometido en la sesión 2 y nada llegó. **Es el bloqueo real del proyecto:** un archivo real contesta veinte preguntas que nunca se hicieron.

**D. Todo descansa en una sola voz.** El cliente descartó entrevistar a otras áreas (*"me pregunta por más personas a las que se pueda entrevistar, pero no, eso no"*). Planeación, que es donde nace la proyección, nunca se exploró. Es un riesgo aceptado, no resuelto — conviene dejarlo escrito.

`[S2]` **Contradicción nueva sin resolver:** en la sesión 1 el cliente dijo **9 variedades activas**; en la sesión 2 dijo **~300 variedades y subvariedades** sumando todo. La diferencia es probablemente variedades comerciales activas vs. catálogo histórico completo, pero cambia por completo el dimensionamiento del modelo de datos y del catálogo del agente de captura.

---

## 9. Archivos del proyecto

Carpeta de documentación (sin git): `OneDrive - UCO\FlorLogic\Documentacion\`

| Archivo | Qué contiene |
|---|---|
| `0_CONTEXTO_para_nuevo_chat.md` | Este archivo. Traspaso a un chat nuevo. |
| `1_PLANTILLA_Levantamiento_Requisitos.xlsx` | Maestro reutilizable sin datos. 8 hojas: instrucciones, guion de 46 preguntas con sondeos, ejemplo de trazabilidad, catálogo de requisitos, tablero, reglas y excepciones, glosario de floricultura, pendientes y riesgos. Los ejemplos usan un caso de rosa que **no** corresponde a esta finca. |
| `2_ENTREVISTA_S1_Diligenciada_y_Vacios.xlsx` | Las 46 preguntas con respuesta textual sin editar, estado, nota /5, qué falta, y el texto listo para volver a preguntar. |
| `3_DIAGNOSTICO_Brechas_y_Plan_de_Accion.xlsx` | Diagnóstico, 24 brechas priorizadas, plan de acercamiento y mensajes listos. **Actualizado con la sesión 2**: incluye hoja `Estado tras Sesion 2` y columna con lo que respondió cada brecha. |
| `4_DOCUMENTOS_Requeridos_al_Cliente.docx` | 38 documentos a pedir en 4 bloques de prioridad. |
| `4b_Lista_para_WhatsApp.txt` | La misma lista en texto plano. |
| `MINI QAW PLANTILLA NO TERMINADA.xlsx` | Matriz de trade-off y mapa de empatía. **Sin terminar**: falta cargar las prioridades de la sección 5 y los 3 roles. |
| `Propuesta de Idea\FlorLogic_Elevator_Pitch.pptx` | Presentación de la idea. No revisado. |
| `Propuesta de Idea\FlorLogic_Mapa_de_Impacto.xlsx` | Mapa de impacto. No revisado. |

**Grabaciones y transcripciones:** `Documentacion\Levantamiento de requisitos\Entrevistas\Grabaciones y Transcripciones por sesión\`. Las transcripciones son `.vtt` de Teams y **atribuyen todo a un solo hablante**, así que hay que inferir quién dice qué por el contenido.

**Repositorio de código** (`GitHub\FlorLogic`): solo tiene README con la estrategia de ramas. El foco está en documentación hasta nuevo aviso.

---

## 10. Convenciones acordadas

- **Trazabilidad obligatoria:** cita textual del cliente → hallazgo → regla de negocio → requisito → criterio de aceptación en formato Dado/Cuando/Entonces. La cita y la interpretación nunca se mezclan en la misma columna.
- **Requisitos:** una sola capacidad por requisito, verbo observable (registrar, calcular, mostrar, notificar, impedir — nunca "gestionar" ni "optimizar"), **sin solución técnica en el enunciado**, con origen apuntando a la pregunta y persona.
- **Funcionales y no funcionales van separados.** Prioridad MoSCoW.
- **Texto para WhatsApp:** prohibido usar asteriscos, guiones bajos, virgulillas y acentos graves; prohibidas las líneas que empiecen con guion, asterisco, ">" o número seguido de punto (WhatsApp las convierte en listas y renumera). Se usa "1)" en vez de "1.". Las tildes y mayúsculas sí son seguras.
- **Estilo de los Excel:** Arial, fórmulas reales nunca valores quemados, verde = ejemplo o cerrado, amarillo = para llenar o parcial, naranja/rojo = alerta o crítico.

---

## 11. Limitación de la herramienta

Claude **no puede escuchar audio ni ver video**; no puede transcribir. Sí puede procesar transcripciones de texto largo sin problema (una entrevista de 90 minutos son 12–15 mil palabras).

Riesgo advertido y ya confirmado en la práctica: **los errores del transcriptor con términos técnicos se propagan**. En estas transcripciones aparecen destrozados *desbotone*, *esqueje*, *botón color* y *pompón*, y hay fragmentos en inglés sin sentido donde el transcriptor perdió el audio. Cuando un dato de la transcripción parezca raro, hay que verificarlo contra la grabación, no darlo por bueno.

---

## 12. Qué sigue

**Bloqueo principal: los documentos.** Nada avanza de verdad hasta que lleguen. En orden:

1. **Perseguir el bloque 1 de documentos** (12 ítems de `4b_Lista_para_WhatsApp.txt`), especialmente: plan de siembra, presupuesto de producción, formatos en papel llenos, tabla de grados, y el detalle completo de una sola cama real.
2. **Cerrar BR-21:** a qué nivel de agregación se miden el 6% y el ±10%. Define la métrica de éxito del proyecto.
3. **Cerrar BR-23:** de dónde sale el %productividad esperada por variedad, y cómo se reparten los tallos en los 7 días de corte. Es el último hueco del motor.
4. **Ver el sistema actual.** Un pantallazo y un export. 20 minutos que pueden cambiar el proyecto entero.
5. **Resolver BR-22:** 9 variedades o 300.
6. **Terminar el mini QAW** con los 3 roles y reabrir la fila de accesibilidad con la definición explícita.
7. **Empezar el catálogo de requisitos** con lo que ya está firme: captura offline, validaciones duras, los 3 roles y sus permisos, proyección, trazabilidad hasta la cama.
8. **Prototipo con datos simulados** y sesión de validación de 45 minutos. Recién ahí se puede estimar esfuerzo.

Sobre el agente de IA: antes de comprometerlo, medir. Un prototipo de formulario optimizado contra un prototipo de captura por voz, cronometrados con una persona real de campo, resuelven la discusión mejor que cualquier documento.

---

## 13. Vocabulario de esta finca

Los términos que usa el cliente, que no coinciden con el vocabulario estándar de rosa:

- **Siembra:** poner el esqueje en la cama; ahí arranca el ciclo fenológico.
- **Esqueje:** material vegetal de propagación.
- **Desbotone:** quitar ciertas partes de la planta para que el producto salga de mejor calidad. Faltan ~3 semanas para el corte.
- **Botón color:** el momento previo al corte. Faltan ~12 días.
- **Baja de producción:** cuando algo falla y parte del producto ya no sirve para venta, se descuenta un porcentaje de la producción estimada. Se mide en porcentajes o en tallos. Se registra por formato y se ingresa al sistema; la idea es que sea de un día para otro, hoy pasa cada semana.
- **Grado / calidad:** el grado agrupa un conjunto de calidades (longitud de tallo, tamaño de flor, número de flores por tallo, grosor, follaje).
- **Densidad de siembra:** plantas por m² que define el ingeniero agrónomo según variedad.
- **Cuarto frío / cava:** nevera grande donde se preserva la flor cortada, 4–5 días.
- **Erradicación:** eliminar la plantación de una cama antes de terminar el ciclo. Reinicia el ciclo a cero.
- **Pompón:** una de las flores que producen. Tiene subvariedades y colores (hielo, verde, etc.).
