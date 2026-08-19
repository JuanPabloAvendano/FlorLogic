> ## ⚠ ESTADO DE VIGENCIA — actualizado el 15-ago-2026
>
> Este documento **sigue siendo la fuente de los hechos del dominio** (`H-01` a `H-49`) y del
> glosario de la finca. Se conserva por eso, y se cita por ID desde el modelo ArchiMate.
>
> Pero **varias de sus secciones están superadas** por las decisiones DEC-01 a DEC-16.
> Antes de usar una sección, mira esta tabla:
>
> | Sección | Estado |
> |---|---|
> | **§1 El encargo** | Parcial — el producto es SaaS multi-tenant operado por el equipo (`DEC-01`) |
> | **§2 El dominio — hechos** | **VIGENTE.** Es la razón por la que este archivo existe. Excepción: `H-06` fue refinado por `DEC-14` (secciones de cama) |
> | **§3 El motor de proyección** | Parcial — la fórmula ahora corre **por sección de cama**, no por cama (`DEC-14`). Los huecos `BR-21` y `BR-23` siguen abiertos |
> | **§4 Alcance** | **SUPERADA** — ver `DECISIONES.md` y el modelo. Además `DEC-07` sacó precios y ventas del dominio |
> | **§5 Roles** | **SUPERADA** — el «Administrador del sistema» se partió en dos (`DEC-01`). La restricción del precio de venta desapareció (`DEC-07`) |
> | **§6 Atributos de calidad** | **SUPERADA** — los rankings se descartaron (`DEC-03`). Se conserva solo como histórico |
> | **§7 Asistente de captura** | **SUPERADA** — la IA volvió partida en dos (`DEC-16`), con alcance acotado |
> | **§8 Estado e inventario de archivos** | **SUPERADA** — el inventario cambió con la limpieza del 15-ago. Ver `LIMPIEZA.md` |
> | **§9 Incógnitas abiertas** | Parcial — 10 de las brechas siguen abiertas; el resto se cerró. Ver `DECISIONES.md` |
> | **§10 Próximos pasos** | **SUPERADA** |
> | **§11 Glosario de la finca** | **VIGENTE** |
> | **Anexos A y B** | **VIGENTES** como método |
>
> **Qué leer en su lugar:** `RESUMEN_SISTEMA.md` para el estado actual, `DECISIONES.md` para el
> porqué de cada cambio, y el modelo en `docs/03-arquitectura/`.

---

# FlorLogic — Contexto del proyecto

**Versión 3.0 · 11-ago-2026 · reemplaza a `0_CONTEXTO_para_nuevo_chat.md` v2.0 (5-ago-2026).**

Documento de traspaso del levantamiento de requisitos de FlorLogic. Está escrito para ser
**independiente del modelo de IA, de la herramienta y de la conversación** en la que se use.

**Qué cambió respecto de la v2.0:** se incorporó el catálogo de requisitos críticos del 10-ago
(ausente en la v2.0) y las cinco brechas nuevas que trajo; se pusieron identificadores estables a
los hechos; se separaron origen y confianza; se movió a anexos todo lo que era método reutilizable
o propiedad de una herramienta concreta; se corrigieron las rutas de archivo. **Ningún dato del
proyecto fue modificado.** Las inconsistencias detectadas se marcan con `[!]` y se dejan sin
resolver — están inventariadas en `REVISION_CONTEXTO_v2.md`.

---

## 0. Cómo leer y mantener este documento

### 0.1 Modos de uso

| Modo | Qué se usa |
|---|---|
| Chat con cualquier asistente | Este archivo completo, pegado o adjunto al inicio |
| Agente de código en el repositorio | `AGENTS.md` del repositorio, que resume este archivo y apunta aquí |
| Entrega académica o al cliente | Secciones 1 a 9; los anexos son internos |
| Arranque de otro proyecto | Solo el **Anexo A**, que no contiene nada de FlorLogic |

### 0.2 Identificadores

Todo hecho duro tiene un ID estable. Se cita por ID, nunca por número de sección.

| Prefijo | Qué identifica |
|---|---|
| `H-nn` | Hecho del dominio |
| `D-nn` | Decisión de alcance cerrada |
| `RF-Cnn` | Requisito funcional crítico (viven en `5_RF_CRITICOS_v1.xlsx`) |
| `RN-nn` | Regla de negocio asociada a un RF |
| `ESC-nn` | Escenario de calidad |
| `BR-nn` | Brecha abierta |

Los IDs **no se reutilizan**. Si un hecho se invalida, se marca `OBSOLETO` y se conserva la fila.

### 0.3 Origen y confianza — dos ejes distintos

`ORIGEN` dice **de dónde salió**. `CONFIANZA` dice **cuánto pesa**. No se mezclan.

| Origen | Significado |
|---|---|
| `S1` | Sesión 1, 27-jul-2026, grabada |
| `S1-Q` | Cuestionario de 46 preguntas, no grabado |
| `S2` | Sesión 2, 4-ago-2026, grabada |
| `EQ` | Propuesta o inferencia del equipo, no del cliente |
| `DOC` | Documento entregado por el cliente |

| Confianza | Significado |
|---|---|
| `CONF` | El cliente lo afirmó de forma explícita |
| `INF` | Derivado por el equipo de algo que dijo el cliente; falta validar |
| `PROP` | Propuesta del equipo; el cliente no lo ha visto |
| `CONTRAD` | Hay dos versiones incompatibles en las fuentes |

### 0.4 Marcas

- `[!]` — inconsistencia detectada y **deliberadamente no corregida**. Verificar contra la fuente antes de usar.
- `[BR-nn]` — este punto depende de una brecha abierta; ver sección 9.

### 0.5 Cómo se actualiza

Después de cada sesión con el cliente: añadir filas nuevas con su ID, cambiar la `CONFIANZA` de las
que se hayan confirmado, cerrar brechas en la sección 9 y subir el número de versión. **No se
reescriben los párrafos existentes**, se marcan.

---

## 1. El encargo

### 1.1 Quiénes

| Papel | Quién |
|---|---|
| Equipo (2 personas) | Juan Pablo Avendaño, Jerónimo Montoya |
| Naturaleza | Proyecto de semestre de ingeniería de software, con profesor que aprueba el alcance |
| Cliente entrevistado | Gustavo, Director de Producción de la finca. **Única fuente humana del proyecto** |
| Producto | FlorLogic |
| Repositorio | `github.com/JuanPabloAvendano/FlorLogic` — propiedad de Juan Pablo, privado |

**Corrección 12-ago-2026.** Las versiones anteriores de este documento listaban a *Ruben* como
miembro del equipo. Es un error de origen: el proyecto se trabajó un tiempo desde esa cuenta, pero
esa persona **nunca formó parte del equipo**. El equipo son dos: Juan Pablo Avendaño y Jerónimo
Montoya. La sesión 3 fue una reunión de esos dos, sin cliente.

### 1.2 Qué se está haciendo

**Levantamiento de requisitos** de un sistema de proyección de producción y ventas con registro
continuo del estado del cultivo, para una finca de flores de corte. El equipo **no es el cliente**:
entrevista al cliente y traduce lo que dice a requisitos trazables.

### 1.3 Qué produce el trabajo — contrato de salida

Todo entregable de este proyecto es un **archivo real** (`.xlsx`, `.docx`, `.md`), no texto dentro
de una conversación. Cada afirmación sobre el dominio va acompañada de su `ORIGEN` y su
`CONFIANZA`. Un requisito nunca se da por bueno sin cita textual que lo respalde, y cuando no la
hay se marca `INF` o `PROP` en lugar de inventarla.

Si el asistente no puede verificar algo contra las fuentes que tiene delante, lo declara abierto y
lo añade a la sección 9. **No se rellenan huecos con supuestos plausibles.**

---

## 2. El dominio — hechos

### 2.1 Escala y estructura física

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-01 | Área en producción: 15 hectáreas | S1 | CONF |
| H-02 | 25 bloques | S2 | CONF |
| H-03 | ~1.525 camas | S2 | CONF |
| H-04 | Jerarquía: Finca → Bloques → Naves → Camas → Variedades | S1 | CONF |
| H-05 | Los bloques tienen área distinta entre sí | S1 | CONF |
| H-06 | Normalmente una variedad por cama; puede haber dos mezcladas, y dentro de una cama subvariedades y varios colores | S1 | CONF |
| H-07 | 156 operarios en campo + ~11 administrativos | S2 | CONF |
| H-08 | 57 personas en poscosecha + 4 administrativos | S2 | CONF |

`[!]` H-02 y H-03 dan ~61 camas por bloque, pero H-05 dice que los bloques son de área distinta. Ese
promedio es **derivado, no observado**: no usarlo para dimensionar sin confirmar la distribución real.

### 2.2 Capacidad de una cama

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-09 | Cada cama tiene largo × ancho → área en m² | S2 | CONF |
| H-10 | El **ingeniero agrónomo** define la densidad de siembra en plantas/m² según variedad. Ejemplos dados: 90 y 60 plantas/m² | S2 | CONF |
| H-11 | Planeación debe respetar esa densidad | S2 | CONF |

### 2.3 Ciclo productivo

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-12 | Siembra a corte: 3 a 5 meses según variedad | S1 | CONF |
| H-13 | El ciclo se corre hasta **15 días** de adelanto o atraso — ~6% sobre un ciclo de ~90 días | S2 | CONF |
| H-14 | El corte de una cama dura alrededor de **7 días**, según variedad. Una cama no corta todo en un día | S2 | CONF |
| H-15 | Desbotone → corte ≈ 3 semanas. Botón color → corte ≈ 12 días. Depende de la variedad | S2 | CONF |
| H-16 | El cliente no quiso añadir más etapas fenológicas por ahora | S2 | CONF |
| H-17 | Lo que corre el ciclo son **actividades**, no variedades ni variables. Ejemplos: desbotone, corte, retirada de luces, profundidad de siembra, fumigaciones | S2 | CONF |

**Sobre H-17.** En la sesión 1 el cliente dijo *«dentro del ciclo hay una serie de variedades que
pueden o no afectar la producción»*. En la sesión 2 se confirmó que quiso decir **actividades**.
Esas actividades son justamente lo que habría que registrar. La v1.0 del contexto arrastraba el
error; queda corregido en origen por el cliente, no por el equipo.

### 2.4 Erradicación

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-18 | Se puede erradicar una cama a mitad de ciclo por problema fitosanitario, enfermedad o falta de ventas | S1 | CONF |
| H-19 | Decide el ingeniero de producción o el gerente de ventas | S1 | CONF |
| H-20 | Puede ocurrir **semanalmente**; unas erradicaciones son más graves que otras | S2 | CONF |
| H-21 | Se debe digitar para que el sistema **reste** esa producción proyectada | S2 | CONF |
| H-22 | Lo ya vendido de esa cama se cancela o se compra a terceros | S2 | CONF |
| H-23 | El ciclo vuelve a cero. El tiempo hasta resembrar depende de la necesidad; no hay regla fija | S2 | CONF |

### 2.5 Captura de datos hoy — el dolor cuantificado

| ID | Métrica | Valor | Origen |
|---|---|---|---|
| H-24 | Supervisor llenando formatos a lápiz y papel | 1 hora/día | S1 |
| H-25 | Practicante digitando | 4 horas/semana | S1 |
| H-26 | Latencia hasta que planeación y gerencia ven el dato | 8 días | S1 |
| H-27 | Error de captura declarado | 2% | S1 |
| H-28 | Horas semanales del director revisando siembra | ~4 horas | S2 |

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-29 | **Solo 3 personas ingresan información:** 1 supervisor + 2 auxiliares | S2 | CONF |
| H-30 | ~12 personas usarían el sistema; ~20 más solo consultan (las vendedoras) | S2 | CONF |
| H-31 | El puesto de digitador es **rotativo** (practicante de ing. de sistemas). Al principio tarda más y se equivoca más. El cliente lo señaló como falla del proceso actual | S2 | CONF |
| H-32 | Un dato mal registrado solo lo corrige el **ingeniero de sistemas**, con aprobación del gerente o del ingeniero de producción | S2 | CONF |
| H-33 | El 2% de error lo revisa un auditor (normalmente el ing. de sistemas) y debe llevarlo a 0%. **Ese 2% no está visualizado en ninguna parte** | S2 | CONF |

### 2.6 Conectividad

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-34 | Existe un *formato de monitoreo de plagas y enfermedades* que se llena en una app **totalmente local en el celular** y sube a la nube cuando hay red. La app es propia de la empresa | S2 | CONF |

Lo que parecía contradicción en la v1.0 (*«no hay internet»* contra *«sube a la nube»*) no lo es:
es **arquitectura offline-first**. Hay precedente funcionando en la finca, y sirve de modelo.

### 2.7 Sistemas existentes

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-35 | Hay un sistema en operación que arroja «productividades» en tallos. El cliente **no recordó el nombre**. Se puede sacar un pantallazo pero aún no se ha visto | S1, S2 | CONF |
| H-36 | PowerBI produce reportes semanales: siembra, producción, plagas y enfermedades, inventario de material vegetal, pérdida de flor, estimados de flor | S1 | CONF |
| H-37 | Sobre PowerBI: *«está y se utiliza, pero no es la forma más óptima. No es que no sirva, se puede mejorar cambiando el enfoque.»* Sin decidir si se queda o se reemplaza | S2 | CONF |
| H-38 | Existen sistemas de nómina y contabilidad, sin explorar | S1 | CONF |
| H-39 | El modelo de datos actual tiene ~300 tablas, 45 solo de producción | S1 | CONF |
| H-40 | **Motivo real del proyecto, en boca del cliente:** *«mejorar el motor de base de datos, llevar los datos que tiene actualmente a una base robusta, y tener una aplicación enfocada a celulares y web.»* | S2 | CONF |

### 2.8 Negocio

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-41 | Temporada pico: +60% en tallos, +60% en registros, +30–40% en personal. Es proporcional | S2 | CONF |
| H-42 | ~**8% de las ventas** se tuvo que conseguir comprando a terceros o cancelando órdenes. Falta el valor en dinero | S2 | CONF |
| H-43 | La compra a terceros ocurre **mensualmente** | S2 | CONF |
| H-44 | Certificación Florverde: **excluida del alcance** por decisión del cliente | S2 | CONF |
| H-45 | Registran pluviometría y temperatura y tienen estación meteorológica, **pero no está en uso y no se hace nada con esos datos** en producción | S2 | CONF |
| H-46 | Clasificación comercial por **grados**, que agrupan calidades: longitud de tallo, tamaño de flor, número de flores por tallo, grosor, follaje. Cada finca define las suyas | S1 | CONF |

`[!]` **Hueco lógico en H-45:** el cliente dice que el ciclo se corre por clima (H-13) pero no usa la
variable climática. O el clima no es la causa, o hay una fuente de datos sin explotar.
El catálogo de grados de esta finca sigue sin entregarse — ver `BR-11`.

### 2.9 Horizonte y granularidad

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-47 | Horizonte de proyección: 1 a 1,5 años. Unidad de tiempo base: el **día** | S1 | CONF |
| H-48 | El presupuesto se arma en días, semanas o meses según quién lo consume: gerencia general → **mensual**; gerente de ventas → **semanal**; gerente de producción → **diario** | S2 | CONF |
| H-49 | Hoy la proyección se ajusta **mensualmente** o cuando es necesario. **Se quiere semanal.** Es una brecha declarada entre estado actual y deseado, y es requisito | S2 | CONF |

---

## 3. El motor de proyección

### 3.1 La fórmula

```
plantas_sembradas   = area_cama_m2 × densidad_siembra(variedad)
tallos_proyectados  = plantas_sembradas × productividad_esperada(variedad)
```

| Variable | Unidad | Dominio | De dónde sale |
|---|---|---|---|
| `area_cama_m2` | m² | > 0 | Largo × ancho de la cama (H-09) |
| `densidad_siembra` | plantas/m² | > 0; ejemplos 60 y 90 | La define el ingeniero agrónomo por variedad (H-10) |
| `plantas_sembradas` | plantas | entero ≥ 0 | Calculado |
| `productividad_esperada` | fracción | **(0 , 1]** — del orden de 0,80–0,90 | **`BR-23` — no se sabe de dónde sale** |
| `tallos_proyectados` | tallos | entero ≥ 0 | Calculado |

**Cota dura, verificable, dada por el cliente:** `tallos ≤ plantas_sembradas` siempre.
Si el sistema arroja más tallos que plantas sembradas, el dato es malo y debe rechazarse.
Ejemplo textual del cliente: 1.000 plantas × 90% → 900 tallos proyectados.

### 3.2 Lo que la fórmula todavía no da

La fórmula produce un **total por cama por ciclo**. El gerente de producción consume **proyección
diaria** (H-48). Entre una cosa y otra falta la **curva de reparto** de los tallos sobre los ~7 días
de corte (H-14). Esa curva **no existe hoy** — es `BR-23` y es el último hueco del motor.

### 3.3 La meta de exactitud

**El corte real debe quedar dentro del ±10% de lo proyectado.** Sobre 900 tallos, la banda es
810–990. La desviación puede ser por encima o por debajo: el cliente insistió en que no solo se daña
flor, también puede salir mejor de lo esperado.

Ese ±10% es el **umbral de aceptación, no la meta**. El objetivo del proyecto es **reducir la brecha
lo más posible**.

> **`BR-21` — abierta y crítica.** Hoy el presupuesto contra el corte real está en **−6%**. Si la
> proyección —que se ajusta continuamente y con más información que el presupuesto anual— se pone
> como meta un ±10%, la meta es **más floja que lo que ya logran**. La explicación probable es el
> **nivel de agregación**: el 6% sería finca/año y el ±10% sería cama o variedad por semana. Hay que
> confirmarlo antes de fijar la línea base, porque de ahí depende la métrica de éxito del proyecto.

---

## 4. Alcance — decisiones cerradas

Estas decisiones están cerradas. No se reabren sin motivo nuevo.

### 4.1 Dentro del alcance

| ID | Decisión | Origen |
|---|---|---|
| D-01 | Captura en campo de **siembra** y **producción** por cama, offline | S2 |
| D-02 | Proyección de producción por cama, variedad y fecha | S2 |
| D-03 | Registro de erradicaciones y bajas de producción, con recálculo hacia adelante | S2 |
| D-04 | Reportes y proyección de ventas a partir de la producción proyectada | S2 |
| D-05 | Trazabilidad completa hasta la cama de origen | S2 |
| D-06 | Parametrización por el administrador: ciclos, días a corte, densidades, márgenes | S2 |

### 4.2 Fuera del alcance de la fase 1

| ID | Decisión | Cita del cliente |
|---|---|---|
| D-07 | **Cruce con pedidos y clientes.** No muestra qué está comprometido ni con qué cliente | *«solamente va a mostrar qué flor se va a producir»* (eligió explícitamente la opción A) |
| D-08 | **Florverde** | *«No nos metamos en Florverde, es otra cuestión adicional»* |
| D-09 | **Gestión de personal e insumos** | *«No nos metamos con personal, en algún momento va a tocar»* |
| D-10 | **Registro de actividades culturales como obligatorio.** Se puede habilitar, pero hoy no se registran | S2 |

---

## 5. Roles del sistema

Tres roles. El cliente fue explícito en no querer más.

| Rol | Quién es | Qué puede hacer |
|---|---|---|
| **Supervisor de campo** | El supervisor y los 2 auxiliares de siembra | Ingresar datos. Ver reportes sencillos de siembra y de lo que ellos mismos ingresan. **No ve precio de venta.** El costo de producción sí lo llegan a conocer |
| **Administrador de producción y ventas** | Gerencia, planeación, ventas, producción — quienes toman decisiones | Acceso amplio a proyecciones, informes y datos de todos los bloques |
| **Administrador del sistema (superusuario)** | El ingeniero de sistemas | Configura y parametriza, otorga permisos, corrige datos erróneos |

**Visibilidad entre bloques:** *«hay información muy compartida en todo el sistema, no hay
restricción.»* La **única restricción real es el precio de venta** frente a supervisores y
auxiliares (origen S2, CONF).

---

## 6. Atributos de calidad

Prioridades que dio el cliente en la sesión 2, tal como quedaron. `1` = máxima.

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

`[!]` **Esta tabla está mal numerada y se deja tal cual a propósito.** El 3 aparece dos veces y no
hay 13. *Compatibilidad* se eliminó por duplicarse con interoperatividad, lo que explica un salto
pero no el otro. **No calcular trade-offs sobre esta lista hasta renumerarla con el cliente.**

**Disponibilidad, en números:** si el sistema se cae pueden aguantar alrededor de **1 hora**. Cuatro
horas es demasiado. Existe plan B en papel y luego se sube la información, pero el cliente insistió
en que *«hay que seguir trabajando sí o sí»* (S2, CONF).

> **`BR-24` — tensión sin resolver.** El cliente puso **accesibilidad en el puesto 10 de 14**, casi
> al final. Pero en la sesión accesibilidad se definió como *«capacidad de ser interpretado por
> personas con falta de digitalización o analfabetismo»*, que es exactamente la premisa que justifica
> el asistente de captura. Es probable que el cliente no entendiera el término: puso experiencia de
> usuario en 2 y repitió *«que sea fácil»* varias veces. Hay que reabrir esa fila con la definición
> explícita antes de defender el asistente.

---

## 7. El asistente de captura por lenguaje natural

**Esto no salió de la entrevista: es una propuesta de diseño del equipo.** Está documentada como tal
(`RF-C20`, confianza `PROP`, MoSCoW `Could`). Conviene no presentarla nunca como requisito del cliente.

### 7.1 El requisito sí está documentado; la solución no

- **Requisito:** el sistema debe permitir registrar la siembra de una cama en menos de N segundos,
  sin conexión. **Falta N** — es `BR-N1`. El escenario bocetado en S2 usó 3 segundos para la
  confirmación de captura, sin validar.
- **Decisión de arquitectura (separada del requisito):** captura por lenguaje natural con asistente
  local que interpreta y llena la plantilla.

El cliente lo dijo casi con esas palabras: *«que sea fácil, que a veces las aplicaciones ponemos
tanta arandela que es muy complicado.»* Un formulario largo en un celular es más lento que un
formato en papel, y la captura tiene que ser más rápida que lápiz y papel, en celular, con personal
de baja digitalización, sin conexión.

### 7.2 Las tres cosas que deciden su viabilidad

1. **Offline es la restricción que manda.** Todo corre en el dispositivo. Un LLM general en gama
   baja es pesado — pero probablemente no hace falta: los valores válidos son un **conjunto cerrado**
   (bloque, nave, cama, variedad, actividad, cantidad). Eso pide **reconocimiento restringido contra
   el catálogo de la finca**, no generación abierta. Más liviano y más exacto.
2. **El vocabulario técnico es el riesgo real.** La transcripción automática de estas mismas
   reuniones destrozó *desbotone*, *esqueje*, *botón color* y *pompón*. En un invernadero con ruido
   va a pasar lo mismo. Restringir el vocabulario al catálogo no es una optimización: es lo que hace
   que funcione.
3. **Confiabilidad es el atributo #1.** Un asistente que interpreta mal un número de cama pega
   directo contra la prioridad más alta del cliente. **Regla de diseño: el asistente propone, el
   sistema valida, el usuario confirma. Nunca escritura silenciosa.** Las validaciones duras que el
   cliente ya pidió (no se puede registrar corte en una cama sembrada hace una semana) corren sobre
   el resultado interpretado.

### 7.3 El contraargumento — hay que poder responderlo

Son **solo 3 personas** capturando datos (H-29) y los registros son muy repetitivos: misma secuencia
de camas, misma variedad, casi los mismos campos cada día. Un formulario bien diseñado, con valores
por defecto, memoria del último valor y campos grandes, podría dar **buena parte del beneficio de
velocidad a una fracción del costo**.

Si se defiende el asistente, hay que defenderlo **con una medición, no con la intuición**: un
prototipo de formulario optimizado contra un prototipo de captura por voz, cronometrados con una
persona real de campo, resuelven la discusión mejor que cualquier documento.

---

## 8. Estado del levantamiento y artefactos

### 8.1 Sesiones

| Sesión | Fecha | Qué pasó |
|---|---|---|
| S1 | 27-jul-2026, grabada | Propuesta y encuadre. El cliente vende la idea: trabajo offline con sincronización, notificaciones, mapa de calor de camas, control de acceso por roles, capa de IA sobre el PowerBI existente, modelo SaaS (~10 USD/usuario/mes, rentable desde ~20 empresas) |
| S1-Q | No grabada | Cuestionario de 46 preguntas. Resultado: 10 completas, 21 parciales, 3 vagas, 2 mal interpretadas, 4 contradictorias, 6 sin responder |
| S2 | 4-ago-2026, grabada | Recorrido de las brechas del archivo 3 + taller de atributos de calidad + definición de roles. Cerró la mayoría de las brechas de alcance y roles, destrabó parcialmente el motor |

### 8.2 Calificación del contexto

| Momento | Ponderada | Simple |
|---|---|---|
| Tras S1-Q | 4,6 / 10 | 5,5 / 10 |
| Tras S2 | **6,7 / 10** | — |

**6,7 significa:** suficiente para una propuesta de solución y un estimado con rango amplio;
**todavía no alcanza para construir.**

El propio cliente calificó el contexto en **5/10** y dijo: *«está muy mal, por eso hay que volverla
a hacer, hasta conseguir un 9 con algo.»*

### 8.3 Inventario de archivos

Raíz de documentación: `OneDrive - UCO/FlorLogic/`

| Ruta relativa | Qué contiene |
|---|---|
| `0_CONTEXTO_v3.md` | Este archivo |
| `0_CONTEXTO_para_nuevo_chat.md` | Versión 2.0, conservada como histórico |
| `REVISION_CONTEXTO_v2.md` | Revisión que originó esta versión |
| `Documentacion/Levantamiento de requisitos/5_RF_CRITICOS_v1.xlsx` | **Lo más reciente (10-ago).** 7 hojas: cómo leer, 20 RF críticos, trazabilidad cita→requisito, matriz de criticidad ponderada, mapa RF→atributos de calidad, esqueleto de 8 escenarios, brechas que bloquean |
| `.../Entrevistas/Formatos de entrevista/1_PLANTILLA_Levantamiento_Requisitos.xlsx` | Maestro reutilizable sin datos. 8 hojas: instrucciones, guion de 46 preguntas con sondeos, ejemplo de trazabilidad, catálogo de requisitos, tablero, reglas y excepciones, glosario, pendientes y riesgos. `[!]` Los ejemplos usan un caso de **rosa que no corresponde a esta finca** |
| `.../Entrevistas/Formatos de entrevista/2_ENTREVISTA_S1_Diligenciada_y_Vacios.xlsx` | Las 46 preguntas con respuesta textual sin editar, estado, nota /5, qué falta, y el texto listo para volver a preguntar |
| `.../Entrevistas/Formatos de entrevista/3_DIAGNOSTICO_Brechas_y_Plan_de_Accion.xlsx` | Diagnóstico, 24 brechas priorizadas, plan de acercamiento y mensajes listos. Incluye hoja `Estado tras Sesion 2` |
| `.../Entrevistas/Formatos de entrevista/4_DOCUMENTOS_Requeridos_al_Cliente.docx` | 38 documentos a pedir, en 4 bloques de prioridad |
| `.../Entrevistas/Formatos de entrevista/4b_Lista_para_WhatsApp.txt` | La misma lista en texto plano |
| `.../Entrevistas/Formatos de entrevista/MINI QAW PLANTILLA NO TERMINADA.xlsx` | Matriz de trade-off y mapa de empatía. **Sin terminar:** falta cargar las prioridades de la sección 6 y los 3 roles |
| `.../Propuesta de Idea/FlorLogic_Elevator_Pitch (2).pptx` | Presentación de la idea. No revisado |
| `.../Propuesta de Idea/FlorLogic_Mapa_de_Impacto.xlsx` | Mapa de impacto. No revisado |

**Grabaciones y transcripciones:**
`Documentacion/Levantamiento de requisitos/Entrevistas/Grabaciones  y Transcripciones por sesión/`
`[!]` El nombre de esa carpeta contiene **dos espacios seguidos** entre «Grabaciones» y «y».

Hay un `.mp4` y un `.vtt` por sesión. **Las transcripciones `.vtt` de Teams atribuyen todo a un
solo hablante**, así que hay que inferir quién dice qué por el contenido.

### 8.4 Estado del repositorio de código

`github.com/JuanPabloAvendano/FlorLogic`. Ramas: `main`, `documentacion-desarrollo`,
`documentacion-lista`, `codigo-desarrollo`, `codigo-testing`, `codigo-listo`. Flujo: el trabajo
nuevo se hace en `*-desarrollo`, pasa por testing o revisión, luego a `*-listo`, y de ahí a `main`.

El repo contiene un esqueleto `docs/` con `01-vision-general`, `02-requerimientos`,
`03-arquitectura`, `04-manual-usuario` y `contenido-pendiente`.

`[!]` **Tres problemas abiertos en el repo, señalados y no corregidos:**

1. **Dos árboles de documentación en paralelo** — la carpeta de OneDrive y `docs/` en el repo — sin
   ninguna regla de cuál manda. Divergirán.
2. **La copia local tiene `docs/` borrado** (7 archivos en estado `D`, sin commitear) y el `README.md`
   enlaza a `docs/README.md`, que localmente no existe. Causa probable: el repositorio vive dentro de
   OneDrive, que sincroniza mal los archivos ocultos tipo `.gitkeep`.
3. **Finales de línea CRLF sin `.gitattributes`.** `README.md` y `.gitignore` aparecen modificados
   por completo sin que el texto cambie. Cualquiera que clone en Linux o Mac verá diffs falsos.
   Se resuelve con un `.gitattributes` que contenga `* text=auto eol=lf`.

---

## 9. Registro de incógnitas abiertas

Cada fila bloquea algo concreto. `RF-Cnn` remite a `5_RF_CRITICOS_v1.xlsx`.

| ID | Incógnita | Bloquea | A quién se pregunta | Estado |
|---|---|---|---|---|
| `BR-N3` | **Los documentos prometidos no han llegado**: plan de siembra, presupuestos de producción y ventas, formatos en papel llenos, tabla de grados y calidades, histórico de siembra y producción | **Todo** | Director de Producción | Abierta |
| `BR-21` | ¿A qué nivel de agregación se miden el −6% actual y el ±10% objetivo? | `RF-C12`, `ESC-08`. **Define la métrica de éxito del proyecto** | Director de Producción | Abierta |
| `BR-23` | ¿De dónde sale el %productividad esperada por variedad, y cómo se reparten los tallos en los ~7 días de corte? | `RF-C07`, `RF-C08`. **Último hueco del motor** | Director de Producción / ing. agrónomo | Abierta |
| `BR-N2` | El sistema actual sigue sin identificarse: nombre, alcance, qué datos guarda, quién lo administra | `RF-C03`, `RF-C13`. Sin esto no se sabe si FlorLogic reemplaza, alimenta o convive | Director de Producción / ing. de sistemas | Abierta |
| `BR-22` | ¿9 variedades activas o ~300 con subvariedades? | `RF-C13`, `RF-C20`. Cambia el dimensionamiento del modelo de datos y del catálogo del asistente | Director de Producción | Abierta |
| `BR-11` | Catálogo de grados y calidades de la finca | `RF-C02`, `RF-C14`. Sin él no se sabe con qué granularidad se registra el corte | Director de Producción | Abierta |
| `BR-24` | Accesibilidad quedó en el puesto 10 de 14 pese a ser la premisa del asistente | `RF-C20` | Director de Producción | Abierta |
| `BR-N1` | Tiempo máximo aceptable de confirmación de captura en campo | `RF-C01`, `RF-C02`, `RF-C20`, `ESC-01`. Es el número que convierte «que sea fácil» en algo verificable | Director de Producción / medición en campo | Abierta |
| `BR-N4` | ¿Puede una misma cama ser registrada por dos capturadores en la misma fecha? | `RF-C04`. Si nunca ocurre, el requisito baja de Should a Could y la arquitectura se simplifica | Director de Producción | **No preguntada** |
| `BR-N5` | Duración de la ventana de sesión offline y qué pasa si se pierde el dispositivo | `RF-C16`. Es donde chocan seguridad y disponibilidad | Equipo / Director de Producción | **No preguntada** |

### 9.1 Contradicción declarada

**`BR-22` — 9 contra 300.** En S1 el cliente dijo **9 variedades activas**; en S2 dijo **~300
variedades y subvariedades** sumando todo. La explicación probable es *variedades comerciales
activas* contra *catálogo histórico completo*, pero cambia por completo el dimensionamiento.
Confianza: `CONTRAD`.

### 9.2 Riesgo aceptado, no resuelto

**Todo descansa en una sola voz.** El cliente descartó entrevistar a otras áreas: *«me pregunta por
más personas a las que se pueda entrevistar, pero no, eso no.»* **Planeación, que es donde nace la
proyección, nunca se exploró.** Queda escrito como riesgo asumido por el equipo.

---

## 10. Próximos pasos

**El bloqueo principal son los documentos (`BR-N3`).** Un archivo real contesta veinte preguntas que
nunca se hicieron. En orden:

1. **Perseguir el bloque 1 de documentos** (12 ítems de `4b_Lista_para_WhatsApp.txt`), en especial:
   plan de siembra, presupuesto de producción, formatos en papel llenos, tabla de grados, y el
   **detalle completo de una sola cama real**.
2. **Ver el sistema actual** (`BR-N2`). Un pantallazo y un export. Veinte minutos que pueden cambiar
   el proyecto entero.
3. **Cerrar `BR-21`** — define la métrica de éxito.
4. **Cerrar `BR-23`** — último hueco del motor.
5. **Resolver `BR-22`** — 9 variedades o 300.
6. **Preguntar `BR-N1`, `BR-N4` y `BR-N5`**, que nunca se han planteado al cliente.
7. **Terminar el mini QAW** con los 3 roles, renumerar la lista de atributos y reabrir la fila de
   accesibilidad con la definición explícita.
8. **Escribir los escenarios de calidad** sobre el esqueleto `ESC-01…ESC-08`, empezando por los RF
   de índice de criticidad más alto (`RF-C03` = 3,0; `RF-C05` = 2,75; `RF-C02` y `RF-C07` = 2,65).
9. **Prototipo con datos simulados** y sesión de validación de 45 minutos. Recién ahí se puede
   estimar esfuerzo.
10. **Medir antes de comprometer el asistente de IA** (ver 7.3).

---

## 11. Glosario de esta finca

Términos que usa el cliente y que no coinciden con el vocabulario estándar de rosa.

| Término | Significado |
|---|---|
| **Siembra** | Poner el esqueje en la cama. Ahí arranca el ciclo fenológico |
| **Esqueje** | Material vegetal de propagación |
| **Desbotone** | Quitar ciertas partes de la planta para que el producto salga de mejor calidad. Faltan ~3 semanas para el corte |
| **Botón color** | El momento previo al corte. Faltan ~12 días |
| **Baja de producción** | Cuando algo falla y parte del producto ya no sirve para venta, se descuenta un porcentaje de la producción estimada. Se mide en porcentajes o en tallos. Se registra por formato y se ingresa al sistema; la idea es que sea de un día para otro, hoy pasa cada semana |
| **Grado / calidad** | El grado agrupa un conjunto de calidades: longitud de tallo, tamaño de flor, número de flores por tallo, grosor, follaje |
| **Densidad de siembra** | Plantas por m² que define el ingeniero agrónomo según variedad |
| **Cuarto frío / cava** | Nevera grande donde se preserva la flor cortada, 4–5 días |
| **Erradicación** | Eliminar la plantación de una cama antes de terminar el ciclo. Reinicia el ciclo a cero |
| **Pompón** | Una de las flores que producen. Tiene subvariedades y colores (hielo, verde, etc.) |

**Estos términos son también el catálogo cerrado sobre el que tendría que reconocer el asistente de
captura** (ver 7.2, punto 2). Las transcripciones automáticas destrozan *desbotone*, *esqueje*,
*botón color* y *pompón*: cuando un dato de una transcripción parezca raro, hay que verificarlo
contra la grabación, no darlo por bueno.

---

# Anexo A — Método de trabajo (reutilizable en otros proyectos)

Este anexo **no contiene nada de FlorLogic**. Se copia tal cual a otro levantamiento de requisitos.

## A.1 Trazabilidad

Cadena obligatoria, en este orden:

```
cita textual del cliente → hallazgo → regla de negocio → requisito → criterio de aceptación
```

El criterio de aceptación se escribe en formato **Dado / Cuando / Entonces**.

**La cita y la interpretación nunca comparten columna.** Si una fila no tiene cita textual, se marca
como inferida o como propuesta del equipo. No se inventan citas.

## A.2 Redacción de requisitos

- Una sola capacidad por requisito.
- Verbo observable: *registrar, calcular, mostrar, notificar, impedir*. **Nunca** *gestionar* ni
  *optimizar*.
- **Sin solución técnica en el enunciado.** «Registrar en menos de N segundos sin conexión» es
  requisito; «con un asistente de voz local» es decisión de arquitectura y va aparte.
- Origen explícito: qué pregunta, qué sesión, qué persona.
- Funcionales y no funcionales **separados**.
- Prioridad **MoSCoW**.

## A.3 Índice de criticidad

Cuatro ejes, cada uno de 0 a 3, con pesos editables que deben sumar 100%:

| Eje | Peso usado en FlorLogic |
|---|---|
| Sin esto no hay producto | 0,40 |
| Riesgo arquitectónico | 0,20 |
| Alto impacto de negocio | 0,25 |
| Alto riesgo de fallo | 0,15 |

## A.4 Escenarios de calidad

Seis campos: fuente del estímulo, estímulo, artefacto, entorno, respuesta esperada, **medida de
respuesta**. Un escenario sin medida numérica **no es un escenario**: se marca `PENDIENTE` con lo
que falta preguntar, no se completa con un número inventado.

## A.5 Estilo de los Excel

- Fuente **Arial**.
- **Fórmulas reales, nunca valores quemados.**
- Código de color: **verde** = confirmado o cerrado · **amarillo** = para llenar o parcial ·
  **naranja/rojo** = alerta o crítico.
- Primera hoja siempre `Como leer`, con qué es, qué no es y de dónde salió.

## A.6 Texto destinado a WhatsApp

WhatsApp reformatea y renumera. Prohibido:

- asteriscos, guiones bajos, virgulillas y acentos graves;
- líneas que empiecen con guion, asterisco, `>` o número seguido de punto.

Se usa `1)` en lugar de `1.`. Las tildes y las mayúsculas sí son seguras.

## A.7 Regla de honestidad

Se señalan los errores propios y los del cliente **sin suavizarlos**. Un hueco declarado vale más
que un hueco tapado con un supuesto plausible. Cuando el equipo propone algo que el cliente no
pidió, se marca como propuesta del equipo y se escribe **el contraargumento más fuerte que exista
en contra**.

---

# Anexo B — Capacidades que el asistente necesita

Escrito como capacidades requeridas, no como limitaciones de una herramienta concreta. Comprobar
cuáles están disponibles antes de asignar trabajo.

| Capacidad | Para qué hace falta | Si no está |
|---|---|---|
| Leer y escribir archivos en disco | Todo el trabajo son `.xlsx`, `.docx` y `.md` reales | El asistente solo puede redactar contenido para que alguien lo pegue a mano |
| Manipular `.xlsx` con fórmulas | Los entregables llevan fórmulas vivas (A.5) | No se puede mantener el catálogo de requisitos ni la matriz de criticidad |
| Procesar texto largo | Una entrevista de 90 minutos son 12–15 mil palabras; los `.vtt` pesan 76 KB y 137 KB | Hay que trocear las transcripciones a mano |
| **Procesar audio o video** | Hay dos `.mp4` (28 MB y 66 MB) sin transcripción fiable | Se depende de los `.vtt` de Teams, que **ya destrozaron el vocabulario técnico** y traen fragmentos en inglés sin sentido donde se perdió el audio |

**Nota sobre la última fila.** En la herramienta usada hasta la v2.0 esta capacidad no existía, y esa
limitación se había escrito como si fuera permanente. **No lo es.** Si el asistente que retome el
proyecto procesa audio nativo, retranscribir los dos `.mp4` con el glosario de la sección 11 como
referencia resuelve el problema de vocabulario de raíz y es de lo más rentable que se puede hacer
sin depender del cliente.

## B.1 Preferencias de estilo de trabajo

Aplican a cómo responde el asistente, no al contenido del proyecto:

- Español.
- Respuestas concisas y directas, sin relleno.
- Entregar archivos reales, no volcar el contenido en la conversación.
- Señalar los errores del usuario sin suavizarlos.
