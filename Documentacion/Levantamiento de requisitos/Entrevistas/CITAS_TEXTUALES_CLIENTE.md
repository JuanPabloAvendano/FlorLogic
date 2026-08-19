# FlorLogic — Citas textuales del cliente

Rescatado el 15-ago-2026 de la hoja `Trazabilidad` de `5_RF_CRITICOS_v1.xlsx`, antes de archivar ese archivo.

**Por qué existe este archivo.** Es la única parte de aquel catálogo que era evidencia y no andamiaje: lo que el cliente dijo, con sus palabras, y qué se dedujo de ahí. La numeración `RF-Cnn` está **obsoleta** (ver `DECISIONES.md`, DEC-04); lo que vale es la columna de citas.

La fuente última siguen siendo las transcripciones `.vtt` y `2_ENTREVISTA_S1_Diligenciada_y_Vacios.xlsx`. Este archivo es un atajo, no un sustituto.

---

### RF-C01 *(ID obsoleto)*

> "El formato de plagas y enfermedades se llena en una app totalmente local en el celular y sube a la nube despues." / "Que sea facil, que a veces las aplicaciones ponemos tanta arandela que es muy complicado."

**Qué significa:** La finca ya opera una app movil offline para monitoreo fitosanitario. Existe precedente tecnico y cultural de captura movil sin red; la exigencia real no es la conexion sino la simplicidad.

**Regla derivada:** RN-01: la captura en campo no puede depender de conectividad. El registro se persiste en el dispositivo y se sincroniza despues.

*Origen: S2 - Director de Produccion*


### RF-C02 *(ID obsoleto)*

> "El supervisor llena formatos a lapiz y papel una hora al dia" y el practicante digita 4 horas por semana.

**Qué significa:** Hoy la produccion se captura en papel y se redigita. Es el origen del 2% de error de captura y de los 8 dias de latencia.

**Regla derivada:** RN-02: el corte de una cama se registra por dia; una cama corta durante ~7 dias, no en un solo evento.

*Origen: S1 y S2 - Director de Produccion*


### RF-C03 *(ID obsoleto)*

> "Sube a la nube despues." / "La informacion tarda 8 dias en llegar a planeacion y gerencia."

**Qué significa:** La sincronizacion diferida es la pieza que convierte la captura offline en dato utilizable. Es tambien el punto donde el sistema puede perder informacion sin que nadie se entere.

**Regla derivada:** RN-03: cada registro capturado se entrega al repositorio central exactamente una vez. Un registro no confirmado permanece pendiente en el dispositivo.

*Origen: S2 - Director de Produccion / decision de arquitectura*


### RF-C04 *(ID obsoleto)*

> "Solo 3 personas ingresan informacion: 1 supervisor y 2 auxiliares."

**Qué significa:** Tres capturadores sobre 1.525 camas hacen improbable pero no imposible el solapamiento. En un esquema offline los dos registros son validos localmente y solo chocan al sincronizar.

**Regla derivada:** RN-04: ante dos registros en conflicto, ninguno se descarta automaticamente. Prevalece la decision de un usuario con rol administrador.

*Origen: Inferido de S2 - pendiente de validar con el cliente*


### RF-C05 *(ID obsoleto)*

> "No se puede registrar corte en una cama sembrada hace una semana."

**Qué significa:** El cliente pidio validaciones duras de forma explicita. Como la captura es offline, la validacion debe ejecutarse en el dispositivo: validar solo en el servidor llegaria 8 dias tarde.

**Regla derivada:** RN-05: entre siembra y primer corte deben transcurrir al menos los dias a corte minimos de la variedad, menos el margen de corrimiento admitido (hasta 15 dias de adelanto).

*Origen: S1 y S2 - Director de Produccion*


### RF-C06 *(ID obsoleto)*

> "Nunca puede superar el 100%: si el sistema arroja mas tallos que plantas sembradas, hay un dato malo."

**Qué significa:** El cliente entrego una regla de consistencia absoluta y verificable. Es la unica cota dura que dio para el motor de proyeccion.

**Regla derivada:** RN-06: tallos registrados o proyectados por cama <= plantas sembradas en esa cama. Sin excepcion declarada.

*Origen: S2 - Director de Produccion*


### RF-C07 *(ID obsoleto)*

> "1.000 plantas sembradas, 90% de productividad, da 900 tallos proyectados."

**Qué significa:** Es la formula nuclear del producto y la unica que el cliente verbalizo completa. Todo lo demas del sistema existe para alimentarla o para consumirla.

**Regla derivada:** RN-07: plantas_sembradas = area_cama_m2 x densidad_siembra(variedad); tallos_proyectados = plantas_sembradas x %productividad_esperada(variedad), con %productividad entre 80% y 100%.

*Origen: S2 - Director de Produccion*


### RF-C08 *(ID obsoleto)*

> "Una cama no corta todo en un dia. El corte dura alrededor de 7 dias, segun variedad."

**Qué significa:** Sin curva de distribucion, la proyeccion diaria que necesita el gerente de produccion es inutilizable, aunque el total semanal sea correcto.

**Regla derivada:** RN-08: la produccion de una cama se reparte sobre la duracion de corte de su variedad segun una curva de distribucion parametrizable.

*Origen: S2 - Director de Produccion*


### RF-C09 *(ID obsoleto)*

> "Se debe digitar para que el sistema reste esa produccion proyectada." / "Lo que ya estaba vendido de ahi se cancela o se compra a terceros."

**Qué significa:** Es la instruccion mas concreta que dio el cliente sobre comportamiento del sistema. Conecta directo con el 8% de las ventas que hoy se cubre comprando a terceros o cancelando ordenes.

**Regla derivada:** RN-09: al erradicar una cama, el ciclo vuelve a cero y la produccion proyectada pendiente de esa cama se resta del total. La fecha de resiembra no es fija.

*Origen: S2 - Director de Produccion*


### RF-C10 *(ID obsoleto)*

> "Baja de produccion: cuando algo falla y parte del producto ya no sirve para venta, se descuenta un porcentaje de la produccion estimada. La idea es que sea de un dia para otro, hoy pasa cada semana."

**Qué significa:** El cliente nombro explicitamente la latencia actual (una semana) y la deseada (un dia). Esa brecha es requisito, no aspiracion.

**Regla derivada:** RN-10: la baja se expresa en porcentaje o en tallos sobre la produccion estimada de una cama y una fecha. Se registra por formato en campo.

*Origen: S1 y S2 - Director de Produccion*


### RF-C11 *(ID obsoleto)*

> "Hoy se ajusta mensualmente o cuando es necesario. Se quiere que sea semanal."

**Qué significa:** El ajuste semanal es una brecha declarada entre el estado actual y el deseado. Conservar versiones no lo pidio el cliente, pero sin ello es imposible medir si la proyeccion mejoro.

**Regla derivada:** RN-11: la proyeccion se recalcula semanalmente como minimo, y bajo demanda cuando ocurre una erradicacion o una baja relevante. Cada version queda fechada e identificable.

*Origen: S2 - Director de Produccion*


### RF-C12 *(ID obsoleto)*

> "El corte real debe quedar dentro del +/-10% de lo que el sistema proyecto." / "Hoy el presupuesto contra el corte real esta en -6%."

**Qué significa:** Esta es la metrica de exito del proyecto entero. Si el sistema no la muestra, nadie puede saber si FlorLogic sirvio. El cliente aclaro que la desviacion puede ser por encima o por debajo.

**Regla derivada:** RN-12: banda de aceptacion +/-10% sobre lo proyectado, parametrizable. El objetivo declarado es reducir la brecha, no solo mantenerse dentro de la banda.

*Origen: S2 - Director de Produccion*


### RF-C13 *(ID obsoleto)*

> "Parametrizacion por parte del administrador: ciclos, dias a corte, densidades, margenes." / "El ingeniero agronomo define la densidad de siembra segun la variedad."

**Qué significa:** Todos los numeros del motor los define un humano y cambian por variedad. Quemarlos en codigo convierte cada ajuste agronomico en un despliegue.

**Regla derivada:** RN-13: los parametros del motor son datos, no codigo. Cambiarlos no debe requerir un despliegue ni intervencion tecnica externa.

*Origen: S2 - Director de Produccion*


### RF-C14 *(ID obsoleto)*

> "Gerencia general mensual, gerente de ventas semanal, gerente de produccion diario."

**Qué significa:** Tres consumidores distintos piden tres granularidades del mismo numero. No son tres reportes: es una agregacion del mismo dato base, que es diario.

**Regla derivada:** RN-14: la unidad de tiempo base del sistema es el dia. Semana y mes son agregaciones derivadas y deben conciliar con el diario.

*Origen: S2 - Director de Produccion*


### RF-C15 *(ID obsoleto)*

> "Hay informacion muy compartida en todo el sistema, no hay restriccion." La unica excepcion declarada: los supervisores y auxiliares no ven precio de venta.

**Qué significa:** El cliente dio un modelo de seguridad casi abierto con una sola restriccion dura. Eso simplifica el diseno, pero convierte esa unica regla en algo que no puede fallar en ningun canal.

**Regla derivada:** RN-15: precio de venta es el unico dato restringido por rol. El costo de produccion si es visible para supervisores.

*Origen: S2 - Director de Produccion*


### RF-C16 *(ID obsoleto)*

> "App totalmente local en el celular" combinado con los tres roles definidos y la restriccion de precio de venta.

**Qué significa:** Choque directo entre dos requisitos del cliente: la captura funciona sin red, pero la autorizacion normalmente vive en el servidor. Resolverlo es una decision de arquitectura, no un detalle de implementacion.

**Regla derivada:** RN-16: la sesion de un usuario en campo sigue siendo valida sin conectividad durante una ventana definida, tras la cual el dispositivo exige reautenticacion.

*Origen: Inferido de S2 - decision de arquitectura pendiente de validar*


### RF-C17 *(ID obsoleto)*

> "Un dato mal registrado solo lo puede corregir el ingeniero de sistemas, con aprobacion del gerente o ingeniero de produccion."

**Qué significa:** El cliente describio un control que hoy existe como acuerdo verbal. Llevarlo al sistema es lo que convierte la trazabilidad en algo defendible.

**Regla derivada:** RN-17: toda correccion de un dato sincronizado requiere (a) rol Administrador del sistema y (b) referencia a la aprobacion del gerente o ingeniero de produccion.

*Origen: S2 - Director de Produccion*


### RF-C18 *(ID obsoleto)*

> "Trazabilidad completa hasta la cama de origen." / "El 2% de error lo revisa un auditor y debe llevarlo a 0%."

**Qué significa:** Sin bitacora no hay auditoria posible y no se puede reconstruir por que una proyeccion dio lo que dio. Tambien es lo que permite atribuir errores al proceso, no a la persona.

**Regla derivada:** RN-18: ningun dato se sobrescribe sin dejar rastro del valor anterior y de quien lo cambio.

*Origen: S1 y S2 - Director de Produccion*


### RF-C19 *(ID obsoleto)*

> "Ese 2% no esta visualizado en ninguna parte."

**Qué significa:** El cliente identifico el problema y su ausencia de solucion en la misma frase. Es una funcionalidad barata con impacto directo sobre el atributo que puso en primer lugar.

**Regla derivada:** RN-19: la meta declarada es llevar el error de captura del 2% al 0%. Lo que no se ve no se corrige.

*Origen: S2 - Director de Produccion*


### RF-C20 *(ID obsoleto)*

> "Que sea facil, que a veces las aplicaciones ponemos tanta arandela que es muy complicado." El requisito de facilidad es del cliente; la captura por lenguaje natural NO lo es.

**Qué significa:** Este RF no salio de la entrevista: es una propuesta de diseno del equipo para atender la exigencia de rapidez y baja digitalizacion. Debe documentarse como tal y sostenerse con una medicion, no con intuicion.

**Regla derivada:** RN-20: el asistente propone, el sistema valida, el usuario confirma. Nunca escritura silenciosa. Las validaciones RF-C05 y RF-C06 corren sobre el resultado interpretado.

*Origen: Propuesta del equipo - no validada con el cliente*

