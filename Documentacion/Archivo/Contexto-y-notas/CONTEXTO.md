# FlorLogic — Contexto único del proyecto

**Versión 4.0 · 25-ago-2026 · archivo único de contexto para asistentes de IA.**

Este archivo **refunde y reemplaza** a `0_CONTEXTO_v3.md`, `DECISIONES.md`, `RESUMEN_SISTEMA.md`,
`PUNTOS_CLAVE.md`, `LIMPIEZA.md`, `PLAN_DEMO_CAPTURA.md`, `CITAS_TEXTUALES_CLIENTE.md`,
`FlorLogic-indice-alcance.md` y `FlorLogic-guia-de-lectura-archimate.md`. Todos ellos están
archivados en `_to_delete/`. **No hay ningún otro documento de contexto: este es el único.**

Lo que **no** está aquí y sigue siendo fuente primaria: las grabaciones y transcripciones `.vtt`,
los `.xlsx` de trabajo, el `.pptx` del pitch y el modelo `.archimate`. Este archivo los resume y
dice dónde están; cuando haya duda sobre un dato, se verifica contra ellos.

---

## 0 · Cómo leer y mantener este archivo

### 0.1 Identificadores

Los IDs son estables y **no se reutilizan**. Si algo se invalida, se marca `OBSOLETO` y se conserva.

| Prefijo | Qué identifica |
|---|---|
| `H-nn` | Hecho del dominio (§3) |
| `DEC-nn` | Decisión cerrada del equipo (§6) |
| `RF-nnn` | Requisito funcional del catálogo vigente (§7.1) |
| `RFP-nn` | Requisito candidato, sin validar (§7.2) |
| `RF-Cnn` | Numeración **obsoleta** del 10-ago; solo aparece en las citas (§14) |
| `RN-nn` | Regla de negocio derivada de una cita (§14) |
| `CN-nn` | Restricción de negocio o técnica — **una sola serie continua** (§8) |
| `RG-nn` | Regla de validación de la demo de captura (§11.4) |
| `BR-nn` | Brecha abierta (§13) |
| `ESC-nnn` | Escenario de calidad — **ninguno existe todavía** |
| `PR-nn` | Principio de diseño |
| `D-nn` | Decisión de alcance de la fase de levantamiento (§7.4) |

### 0.2 Origen y confianza — dos ejes distintos

`ORIGEN` dice de dónde salió. `CONFIANZA` dice cuánto pesa. No se mezclan.

| Origen | Significado | | Confianza | Significado |
|---|---|---|---|---|
| `S1` | Sesión 1, 27-jul-2026, grabada | | `CONF` | El cliente lo afirmó explícitamente |
| `S1-Q` | Cuestionario de 46 preguntas, no grabado | | `INF` | Derivado por el equipo; falta validar |
| `S2` | Sesión 2, 4-ago-2026, grabada | | `PROP` | Propuesta del equipo; el cliente no la ha visto |
| `S3` | Sesión 3, 11-ago-2026, **solo el equipo** | | `CONTRAD` | Dos versiones incompatibles en las fuentes |
| `S4` | Sesión 4, 17-ago-2026, **solo el equipo** | | | |
| `CAR` | Cuestionario de caracterización, 262 preguntas | | | |
| `EQ` | Propuesta o inferencia del equipo | | | |
| `DOC` | Documento entregado por el cliente | | | |

### 0.3 Marcas

- `[!]` — inconsistencia detectada y **deliberadamente no resuelta**. Verificar contra la fuente.
- Un requisito **nunca** se da por bueno sin cita textual. Sin cita, es `INF` o `PROP`.
- **No se rellenan huecos con supuestos plausibles.** Un hueco declarado vale más que uno tapado.

---

## 1 · Qué es FlorLogic

Un sistema que **proyecta cuánta flor va a haber, de qué variedad, de qué grado y en qué fecha**,
a partir de datos capturados en el campo por los supervisores, **sin depender de que haya internet**.

Lo que **no** es: no maneja dinero (`DEC-07`), no cruza con pedidos ni clientes, y no reemplaza los
sistemas que la finca ya tiene.

### 1.1 Quiénes

| Papel | Quién |
|---|---|
| Equipo — **dos personas** | Juan Pablo Avendaño y Jerónimo Montoya |
| Naturaleza | Proyecto de semestre de Ingeniería de Software II. Profesor: Farid |
| Cliente entrevistado | **Gustavo Montoya**, ingeniero de sistemas y gerente/director de producción hasta marzo de 2025 en **Buena Vista Flowers S.A.S** y **Flores Rionegro S.A.** (fuente: `FlorLogic_Elevator_Pitch.pptx`, diapositiva 9) |
| Repositorio | `github.com/JuanPabloAvendano/FlorLogic` — privado, propiedad de Juan Pablo |

`[!]` **No incluir a «Ruben».** Las versiones v1 y v2 del contexto lo listaban como miembro del
equipo. Es un error de origen: el proyecto se trabajó un tiempo desde esa cuenta, pero esa persona
**nunca formó parte del equipo**.

**Fuente única.** Todo el conocimiento del negocio viene de **una sola persona**, con 30 años de
experiencia en exactamente el proceso que el sistema va a apoyar, y con conocimiento directo de
**dos empresas** cuyas prácticas son las que sigue el resto del sector. Es más sólida de lo que
suena «una sola voz», pero sigue siendo un solo punto de vista (§13.3).

### 1.2 Carpeta que manda

**`C:\Users\juanp\FlorLogic`** — repo git, rama activa `documentacion-desarrollo`.
`OneDrive - UCO\FlorLogic` está **deprecada** y su único contenido exclusivo son los dos `.mp4` de
S1 y S2 (§17.3).

---

## 2 · Cronología de las fuentes

| Sesión | Fecha | Quién | Qué pasó |
|---|---|---|---|
| **S1** | 27-jul-2026, grabada | Cliente + equipo | Propuesta y encuadre. El cliente vende la idea: offline con sincronización, notificaciones, mapa de calor de camas, roles, capa de IA sobre PowerBI, modelo SaaS (~10 USD/usuario/mes, rentable desde ~20 empresas) |
| **S1-Q** | no grabada | Cliente | Cuestionario de 46 preguntas. Resultado: 10 completas, 21 parciales, 3 vagas, 2 mal interpretadas, 4 contradictorias, 6 sin responder |
| **S2** | 4-ago-2026, grabada | Cliente + equipo | Recorrido de brechas + taller de atributos de calidad + definición de roles. Cerró la mayoría de brechas de alcance, destrabó parcialmente el motor |
| **S3** | 11-ago-2026, grabada | **Solo el equipo** | Roles validados, mini QAW, se descartó la IA embebida como requisito del cliente. Confianza `EQ`, nunca `CONF` |
| **Q&A** | 12-ago-2026 | Equipo | Formato de escenarios de calidad; se inclinó a PaaS (después revertido por `DEC-01`) |
| **Tandas 1, 1-bis y 2** | 15-ago-2026 | Equipo | `DEC-01` a `DEC-16`. Limpieza del contexto |
| **S4** | 17-ago-2026, grabada | **Solo el equipo** | Lectura de la plantilla real de captura, adopción de ArchiMate, dudas sobre el 90% y el reparto del corte |
| **Mini QAW nuevo** | 21-ago-2026 | Equipo | 13 atributos, ranking por actor, 262 preguntas de caracterización |
| **Demo de captura** | 21-ago-2026 | Equipo | `app-captura/` construida y verificada |
| **Caracterización diligenciada** | ~23-ago-2026 | Cliente + Jerónimo | **Las tres columnas quedaron respondidas** (§10). Es el cambio de estado más importante y reciente |

**Calificación del contexto:** tras S1-Q 4,6/10 ponderada · tras S2 **6,7/10**. Suficiente para una
propuesta y un estimado con rango amplio; **todavía no alcanza para construir**. El propio cliente
calificó el contexto en 5/10: *«está muy mal, por eso hay que volverla a hacer, hasta conseguir un 9
con algo.»*

`[!]` Las transcripciones `.vtt` de Teams **atribuyen todo a un solo hablante** y destrozan el
vocabulario técnico (*desbotone*, *esqueje*, *botón color*, *pompón*). Cuando un dato de una
transcripción parezca raro, se verifica contra la grabación.

---

## 3 · El dominio — hechos

### 3.1 Escala y estructura física

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-01 | Área en producción: 15 hectáreas | S1 | CONF |
| H-02 | 25 bloques | S2 | CONF |
| H-03 | ~1.525 camas | S2 | CONF |
| H-04 | Jerarquía: Finca → Bloques → Naves → Camas → Variedades. **Modificada por `DEC-01`:** Empresa → Finca(s) → Bloques → Naves → Camas → **Secciones** → Variedades | S1 | CONF |
| H-05 | Los bloques tienen área distinta entre sí | S1 | CONF |
| H-06 | Normalmente una variedad por cama; puede haber dos mezcladas, y dentro de una cama subvariedades y varios colores. **Refinado por `DEC-14`** | S1 | CONF |
| H-07 | 156 operarios en campo + ~11 administrativos | S2 | CONF |
| H-08 | 57 personas en poscosecha + 4 administrativos | S2 | CONF |

`[!]` H-02 y H-03 dan ~61 camas por bloque, pero H-05 dice que los bloques son de área distinta.
Ese promedio es **derivado, no observado**: no usarlo para dimensionar.

`[!]` Duda abierta de S4: el equipo no tiene claro si **nave y bloque** son lo mismo. En el formato
real de captura **no aparece la nave**: solo bloque y cama.

### 3.2 Capacidad de una cama

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-09 | Cada cama tiene largo × ancho → área en m² | S2 | CONF |
| H-10 | El **ingeniero agrónomo** define la densidad de siembra en plantas/m² según variedad. Ejemplos: 90 y 60 plantas/m² | S2 | CONF |
| H-11 | Planeación debe respetar esa densidad | S2 | CONF |

### 3.3 Ciclo productivo

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-12 | Siembra a corte: 3 a 5 meses según variedad | S1 | CONF |
| H-13 | El ciclo se corre hasta **15 días** de adelanto o atraso — ~6% sobre ~90 días | S2 | CONF |
| H-14 | El corte de una cama dura alrededor de **7 días**. Una cama no corta todo en un día | S2 | CONF |
| H-15 | Desbotone → corte ≈ 3 semanas. Botón color → corte ≈ 12 días. Depende de la variedad | S2 | CONF |
| H-16 | El cliente no quiso añadir más etapas fenológicas por ahora | S2 | CONF |
| H-17 | Lo que corre el ciclo son **actividades**, no variedades ni variables: desbotone, corte, retirada de luces, profundidad de siembra, fumigaciones | S2 | CONF |

**Sobre H-17:** en S1 el cliente dijo *«dentro del ciclo hay una serie de variedades que pueden o no
afectar la producción»*. En S2 confirmó que quiso decir **actividades**. Corregido en origen por el
cliente, no por el equipo.

### 3.4 Erradicación

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-18 | Se puede erradicar una cama a mitad de ciclo por problema fitosanitario, enfermedad o falta de ventas | S1 | CONF |
| H-19 | Decide el ingeniero de producción o el gerente de ventas | S1 | CONF |
| H-20 | Puede ocurrir **semanalmente**; unas erradicaciones son más graves que otras | S2 | CONF |
| H-21 | Se debe digitar para que el sistema **reste** esa producción proyectada | S2 | CONF |
| H-22 | Lo ya vendido de esa cama se cancela o se compra a terceros | S2 | CONF |
| H-23 | El ciclo vuelve a cero. El tiempo hasta resembrar depende de la necesidad; no hay regla fija | S2 | CONF |

### 3.5 Captura de datos hoy — el dolor cuantificado

| ID | Métrica | Valor | Origen |
|---|---|---|---|
| H-24 | Supervisor llenando formatos a lápiz y papel | **1 hora/día** | S1 |
| H-25 | Practicante digitando | **4 horas/semana** | S1 |
| H-26 | Latencia hasta que planeación y gerencia ven el dato | **8 días** | S1 |
| H-27 | Error de captura declarado | **2%** | S1 |
| H-28 | Horas semanales del director revisando siembra | ~4 horas | S2 |
| H-29 | **Solo 3 personas ingresan información:** 1 supervisor + 2 auxiliares | — | S2 |
| H-30 | ~12 personas usarían el sistema; ~20 más solo consultan (las vendedoras) | — | S2 |
| H-31 | El puesto de digitador es **rotativo** (practicante de ing. de sistemas). Al principio tarda más y se equivoca más. El cliente lo señaló como falla del proceso actual | — | S2 |
| H-32 | Un dato mal registrado solo lo corrige el **ingeniero de sistemas**, con aprobación del gerente o del ingeniero de producción | — | S2 |
| H-33 | El 2% de error lo revisa un auditor (normalmente el ing. de sistemas) y debe llevarlo a 0%. **Ese 2% no está visualizado en ninguna parte** | — | S2 |

### 3.6 Conectividad

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-34 | Existe un *formato de monitoreo de plagas y enfermedades* que se llena en una app **totalmente local en el celular** y sube a la nube cuando hay red. La app es propia de la empresa | S2 | CONF |

No es una contradicción con *«no hay internet»*: es **arquitectura offline-first**, con precedente
funcionando en la finca.

### 3.7 Sistemas existentes

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-35 | Hay un sistema en operación que arroja «productividades» en tallos. El cliente **no recordó el nombre**. Nunca se ha visto | S1, S2 | CONF |
| H-36 | PowerBI produce reportes semanales: siembra, producción, plagas y enfermedades, inventario de material vegetal, pérdida de flor, estimados de flor | S1 | CONF |
| H-37 | Sobre PowerBI: *«está y se utiliza, pero no es la forma más óptima. No es que no sirva, se puede mejorar cambiando el enfoque.»* | S2 | CONF |
| H-38 | Existen sistemas de nómina y contabilidad, sin explorar | S1 | CONF |
| H-39 | El modelo de datos actual tiene ~300 tablas, 45 solo de producción | S1 | CONF |
| H-40 | **Motivo real del proyecto, en boca del cliente:** *«mejorar el motor de base de datos, llevar los datos que tiene actualmente a una base robusta, y tener una aplicación enfocada a celulares y web.»* | S2 | CONF |

### 3.8 Negocio

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-41 | Temporada pico: +60% en tallos, +60% en registros, +30–40% en personal. Es proporcional | S2 | CONF |
| H-42 | ~**8% de las ventas** se tuvo que conseguir comprando a terceros o cancelando órdenes. Falta el valor en dinero | S2 | CONF |
| H-43 | La compra a terceros ocurre **mensualmente** | S2 | CONF |
| H-44 | Certificación Florverde: **excluida del alcance** por decisión del cliente | S2 | CONF |
| H-45 | Registran pluviometría y temperatura y tienen estación meteorológica, **pero no está en uso** en producción | S2 | CONF |
| H-46 | Clasificación comercial por **grados**, que agrupan calidades: longitud de tallo, tamaño de flor, número de flores por tallo, grosor, follaje. **Cada finca define las suyas** | S1 | CONF |

`[!]` **Hueco lógico en H-45:** el cliente dice que el ciclo se corre por clima (H-13) pero no usa la
variable climática. O el clima no es la causa, o hay una fuente de datos sin explotar.

`[!]` **H-44 quedó contradicho** por la caracterización: el cliente respondió **SÍ** a *«¿Se necesita
responder auditorías de certificación (por ejemplo Florverde, Rainforest Alliance o GLOBALG.A.P.) con
información sacada del sistema?»* Ver §10.4.

### 3.9 Horizonte y granularidad

| ID | Hecho | Origen | Conf. |
|---|---|---|---|
| H-47 | Horizonte de proyección: 1 a 1,5 años. Unidad de tiempo base: el **día** | S1 | CONF |
| H-48 | El presupuesto se arma según quién lo consume: gerencia general → **mensual**; gerente de ventas → **semanal**; gerente de producción → **diario** | S2 | CONF |
| H-49 | Hoy la proyección se ajusta **mensualmente** o cuando es necesario. **Se quiere semanal.** Es requisito, no aspiración | S2 | CONF |

### 3.10 Glosario de esta finca

Términos que usa el cliente y que no coinciden con el vocabulario estándar de rosa. **Es también el
catálogo cerrado** contra el que tendría que reconocer un asistente de captura.

| Término | Significado |
|---|---|
| **Siembra** | Poner el esqueje en la cama. Ahí arranca el ciclo fenológico |
| **Esqueje** | Material vegetal de propagación |
| **Desbotone** | Quitar partes de la planta para que el producto salga de mejor calidad. Faltan ~3 semanas para el corte |
| **Botón color** | Momento previo al corte. Faltan ~12 días |
| **Baja de producción** | Cuando algo falla y parte del producto ya no sirve para venta, se descuenta un porcentaje de la producción estimada. Se mide en porcentajes o en tallos. Se registra por formato; la idea es que sea de un día para otro, hoy pasa cada semana |
| **Grado / calidad** | El grado agrupa un conjunto de calidades: longitud de tallo, tamaño de flor, número de flores por tallo, grosor, follaje |
| **Densidad de siembra** | Plantas por m² que define el ingeniero agrónomo según variedad |
| **Cuarto frío / cava** | Nevera grande donde se preserva la flor cortada, 4–5 días |
| **Erradicación** | Eliminar la plantación de una cama antes de terminar el ciclo. Reinicia el ciclo a cero |
| **Pompón** | Una de las flores que producen. Tiene subvariedades y colores (hielo, verde, etc.). Es una *instancia* de Variedad, no un concepto del modelo |
| **Matsomoto / Cremón** | Tipos de flor que aparecen como encabezado de las hojas del formato real |
| **Línea** | Unidad real de conteo en el formato de papel. `cantidad = #líneas × plantas por línea`. **Su definición exacta sigue sin confirmarse con el cliente** |

---

## 4 · El formato real de captura — la única evidencia documental

Archivo: `Documentacion/Levantamiento de requisitos/PLANTILLAS DOCUMENTOS DE EMPRESA/Plantilla
digitalizada de excel de información capturada.xlsx`. **Es el único documento del proyecto con datos
reales de captura.** Tres hojas: `Cremón`, `Matsomoto` y `Programa Siembras`.

### 4.1 Estructura física (descrita en S4)

Hoja de oficina, escrita a mano. Arriba, en lapicero: **el tipo de flor** (Matsomoto, Cremón) y la
**semana**. Debajo, la tabla titulada *«Buenavista — Novedades de siembra»*, con **7 columnas**:

`FECHA · BLOQUE · CAMA · VARIEDAD · #LÍNEAS · CANTIDAD · OBSE`

Son varias hojas engrapadas por flor. **El segmento principal de organización es la flor, no la
cama** — hallazgo explícito de S4: *«no es como que vaya a ir a una cama y a buscar la información,
sino que voy a buscar la flor»*.

### 4.2 Los datos reales

**Hoja `Cremón`** — fecha 23-06-26, bloque 12, camas 28 a 41, variedades Petroska, Cooper, Lineth,
Astroi. **Hoja `Matsomoto`** — fecha 24-06-26, bloques 5 y 9, variedades Rose, Blue, Scarlett, Pink.

### 4.3 Cuatro hallazgos que ya mueven el modelo

**1 · Las camas divididas son reales y frecuentes.** En el bloque 12, tres camas aparecen dos veces
el mismo día con variedades distintas:

| Cama | Fila 1 | Fila 2 |
|---|---|---|
| 37 | Lineth · 148 líneas · 2812 | Cooper · 21 líneas · 399 |
| 39 | Astroi · 153 líneas · 1907 | Astroi · 97 líneas · 1843 |
| 40 | Lineth · 49 líneas · 931 | Petroska · 5 líneas · 95 |

Tres de dieciocho filas, **17%**. `DEC-14` y `DEC-15` dejan de ser una apuesta.

**2 · La unidad real de captura no es el área — son las líneas.** El formato **no tiene ninguna
columna de área ni de densidad**. Nadie mide metros cuadrados en el campo: cuentan líneas.

```
cantidad de plantas (sección) = #líneas × plantas por línea (de esa variedad)
```

`área × densidad` queda como **derivación de oficina**, no como dato de campo. Esto abarata la app:
se captura un entero, no dos decimales. **Al reescribir `RF-001` y `RF-002` hay que usar esta
fórmula.**

**3 · La regla de validación ya encuentra errores en el histórico.** `plantas por línea` es
prácticamente constante por hoja: **19 en Cremón, 15 en Matsomoto**.

| Hoja | Variedad | Razones observadas | Lectura |
|---|---|---|---|
| Cremón | Petroska | 19,0 · 18,93 · 19,0 · 19,0 · 19,0 · 19,0 | 18,93 = redondeo |
| Cremón | Cooper | 19,0 ×5 · **17,78** | una fila fuera |
| Cremón | Lineth | 19,0 · 19,0 | limpia |
| Cremón | **Astroi** | **11,19 · 12,46 · 19,0** | tres razones distintas, misma variedad, mismo día |
| Matsomoto | Rose | **13,8** · 15,0 · **16,0** · 15,0 · 15,0 | dos fuera |
| Matsomoto | Blue | 15,0 ×3 · **20,56** | una fuera |
| Matsomoto | Scarlett | **14,76** · **24,68** · 15,0 | dos fuera |
| Matsomoto | Pink | 15,0 · 15,0 · **13,96** | una fuera |

Con tolerancia del **2%**, la demo marca **9 filas**: 8 por razón fuera de rango más
`Cortona`/`Cartona` (dos nombres a una letra de distancia, en `Programa Siembras`). `Astroi` y `Rose`
quedan **`en-disputa`**: tres razones distintas cada una. **El umbral del 2% es una decisión nuestra,
no un hallazgo** — hay que preguntárselo al cliente.

En S4 el cliente había señalado que lo resaltado en su formato eran **errores del que tomó los
datos**, y que la observación *«repetido»* indica filas duplicadas porque hay varias plantillas de la
misma flor y la misma fecha con información repetida.

**4 · Hay una columna ilegible y son dos formatos, no uno.**
- La columna **`OBSE`** alterna entre `325` y `425` sin patrón evidente, incluso dentro de la misma
  variedad. No es texto libre: es un código. **Pregunta directa de la sesión.**
- `Programa de siembras` **no es el mismo formato**: trae `fecha salida cf`, `fecha de siembra`,
  `long prebrotado`, `bloque`, `cama`, `variedad`, `lote`, `calibre`, `proveedor`, `contenedor`,
  `observaciones`. La app necesita **dos plantillas de captura**.
  `[!]` En S4 el equipo especuló que `calibre` («12 14») podría ser el grosor del tallo en mm y que
  se relaciona con la tabla de grados. **Especulación sin confirmar.**

---

## 5 · El motor de proyección

### 5.1 La fórmula

Vigente tras `DEC-14` — la fórmula corre **por sección de cama**, no por cama:

```
plantas_sección    = área_sección_m2 × densidad_siembra(variedad de esa sección)
plantas_cama       = suma de las plantas de todas sus secciones
tallos_proyectados = plantas × productividad_esperada(variedad)
```

Y, según el formato real (§4.3), el dato **de campo** es:

```
plantas_sección = #líneas × plantas_por_línea(variedad)
```

| Variable | Unidad | Dominio | De dónde sale |
|---|---|---|---|
| `área_sección_m2` | m² | > 0 | Largo × ancho (H-09) — derivación de oficina |
| `densidad_siembra` | plantas/m² | > 0; ejemplos 60 y 90 | La define el ingeniero agrónomo por variedad (H-10) |
| `#líneas` | entero | 1..~231 observado | **Lo que se cuenta en campo** |
| `productividad_esperada` | fracción | (0 , 1] — orden de 0,80–0,90 | **`BR-23` — no se sabe de dónde sale** |
| `tallos_proyectados` | tallos | entero ≥ 0 | Calculado |

**Cota dura, verificable, dada por el cliente:** `tallos ≤ plantas_sembradas` **siempre**. Si el
sistema arroja más, el dato es malo y se rechaza. Ejemplo textual: 1.000 plantas × 90% → 900 tallos.

### 5.2 Lo que la fórmula todavía no da

La fórmula produce un **total por cama por ciclo**. El gerente de producción consume **proyección
diaria** (H-48). Falta la **curva de reparto** de los tallos sobre los ~7 días de corte (H-14). Es
`BR-23` y es el último hueco del motor.

> Pregunta exacta para la sesión: *«si de una cama salen 900 tallos en 7 días, ¿salen 130 cada día o
> el tercer día salen 300?»*

### 5.3 La meta de exactitud

**El corte real debe quedar dentro del ±10% de lo proyectado.** Sobre 900 tallos, la banda es
810–990. La desviación puede ser por encima o por debajo. Ese ±10% es el **umbral de aceptación, no
la meta**: el objetivo es reducir la brecha lo más posible.

> **`BR-21` — abierta y crítica.** Hoy el presupuesto contra el corte real está en **−6%**. Si la
> proyección se pone como meta un ±10%, la meta es **más floja que lo que ya logran**. La explicación
> probable es el nivel de agregación: el −6% sería finca/año y el ±10% cama o variedad por semana.
> **Hasta confirmarlo, el proyecto no tiene métrica de éxito.**

### 5.4 La interpretación del 90% (aclarada en S4)

Se siembran ~1.000 plantas para asegurar ~900 tallos. De las 1.000 se espera cierta pérdida, y esa
pérdida debe quedar dentro del ±10% de las 900 (es decir, entre 810 y 990). **El equipo no busca
reducir el 10% al 5%**: no puede garantizar que la planta crezca. Ese es un proceso propio de la
empresa; FlorLogic lo facilita, lo hace visible y lleva la contabilidad.

---

## 6 · Decisiones cerradas `DEC-01` … `DEC-16`

Todas del **15-ago-2026**, tomadas por Juan Pablo y Jerónimo. Confianza `EQ`:
**ninguna está validada con el cliente**, salvo donde se indique.

### DEC-01 · FlorLogic es un SaaS multi-tenant
Se descartan on-premise, PaaS y desarrollo a medida. Motivos: accesibilidad para el cliente y
flexibilidad para otras fincas. La plataforma la **opera el equipo FlorLogic**.
**Consecuencias:** `Empresa` pasa a ser la raíz de la jerarquía · `RF-012` (aislamiento) se vuelve
estructural, base legal `CN-03` · `RF-013` (parametrización) es el corazón del modelo de datos ·
`CN-11` (PayU) cobra sentido · `CN-12` (RBAC) se evalúa contra el par (rol, empresa).
**El rol de administrador se parte en dos:** *Administrador de la empresa (tenant)*, dentro de la
finca cliente, y *Operador de la plataforma*, el equipo FlorLogic.
> `[!]` `RF-017` dice que solo el administrador puede modificar un registro sincronizado. Si eso
> incluyera al operador, el equipo FlorLogic estaría tocando datos productivos de un cliente.
> Problema contractual, no técnico.
> `[!]` Agrava el riesgo de fuente única: construir para varias empresas sobre esta evidencia es un
> salto mayor que construir a medida.

### DEC-02 · Primero una finca piloto, después el producto multi-tenant
La primera entrega debe funcionar en **una** empresa concreta; el multi-tenant se construye desde el
diseño pero se valida con una finca. `CN-01` (mayo 2027) y `CN-07` aplican al **piloto**.
`RF-012` hay que construirlo con pruebas sintéticas. El módulo de gestión de empresas y
suscripciones es de fase 2.
**Presupuesto:** `CN-02` (~20.000 USD ≈ 61 M COP) sale de la partida que la empresa destina a
producción y recolección de datos antes de iniciar un cultivo. Es **construcción y puesta en
marcha**; el pitch de ~10 USD/usuario/mes es el **ingreso recurrente posterior**. No compiten: son
dos momentos distintos. `CN-05` (qué se cobra y por qué unidad) sigue sin definir.

### DEC-03 · Los rankings de atributos previos quedan descartados
Los cuatro ejercicios anteriores no se usan como fuente. Los 14 atributos pasaron a `EN DUDA`.
**Escenarios de calidad: no existen. Están bloqueados en cascada.**
Insumos que la re-elaboración debía incorporar: aislamiento entre competidoras (`RF-012`), pico
simultáneo entre tenants (`H-41`), operación por el propio equipo, BI propio que baja
Interoperatividad. **Ya ejecutada** — ver §9.

### DEC-04 · El catálogo vigente es `FuncionalidadesSignificativas.xlsx`
«Funcionalidades significativas» = las críticas para el funcionamiento mínimo del negocio, acotadas
al tiempo de desarrollo. **No existen más requisitos validados.** `5_RF_CRITICOS_v1.xlsx` y
`6_FUNCIONALIDADES_CRITICAS_v1.xlsx` quedan como histórico.
**Pendiente de forma:** `FR-023` y `FR-024` deberían escribirse `RF-`.

### DEC-05 · Conflictos de sincronización: automático por defecto, mediación como opción
Orden cronológico estricto por defecto, con registro de cambios consultable. **La mediación humana
existe como opción configurable por empresa.** Cierra la contradicción `RF-C04` vs. `RF-022`.
**Principio `PR-09`:** *automatizar primero, permitir mediación humana como opción.*
> `[!]` No confundir con `PR-01` («el asistente propone, el sistema valida, el usuario confirma»):
> `PR-01` manda sobre la interpretación de un dato que se captura; `PR-09` sobre la resolución de
> conflictos entre datos ya capturados.
> `[!]` **Contradicho por la caracterización** — ver §10.4.

### DEC-06 · BI propio y cerrado, antes que integración con terceros
Modelo de BI específico y cerrado sobre la información que el negocio considera importante.
**Efecto:** nuevo componente *Módulo de BI y tableros propios* · `CN-10` se cierra (PowerBI **no**
es restricción impuesta) · nueva restricción `CN-14` · **Interoperatividad baja de prioridad**.
> `[!]` El alcance del BI no está acotado. «Lo que el negocio considere importante» no es una lista.
> `[!]` **Contradicho por S4 y por la caracterización** — ver §10.4 y §13.2.

### DEC-07 · El dominio excluye precios y rendimiento económico
**La decisión de alcance más grande.** El dominio son **cantidades, unidades y calidades**.

| Elemento | Antes | Ahora |
|---|---|---|
| `Proyectar ventas` | proyectaba «ventas» | **`Proyectar disponibilidad de flor`** |
| `Proyección de producción y ventas` | — | **`Proyección de producción y disponibilidad`** |
| `Precio de venta` | DENTRO, único dato restringido | **FUERA-F1**, fuera del dominio |
| `RF-015` ocultar precio | fuera del catálogo | **sin objeto** |
| `CN-12` RBAC | controlaba visibilidad de campos | controla **capacidades**. La única frontera de visibilidad es la de EMPRESA |
| `RF-019` exportar | punto de fuga de precios | ya no lo es |

**Consecuencia práctica: dentro de una empresa no hay ningún dato restringido por rol.**
> `[!]` El **costo de producción** no es información de ventas y los supervisores ya lo conocen. Si
> algún día entra al sistema, esta decisión hay que revisarla.

### DEC-08 · Destino de los cuatro requisitos huérfanos

| Requisito | Destino | Por qué |
|---|---|---|
| **RF-007** repartir tallos sobre los días de corte | **Entra, pero NO como funcionalidad significativa** | Dependencia del negocio, no indispensable para que el producto exista |
| **RF-010** baja de producción | **Absorbido por RF-009** | «Erradicación parcial o total» ya cubre el descuento parcial |
| **RF-015** ocultar precio de venta | **Sin objeto** | Ver `DEC-07` |
| **RF-C19** vista de calidad de datos | **Absorbido por RF-016** | RF-016 pasa de «conservar» a **«conservar y mostrar»** |

`RF-009` pasa a cubrir el descuento parcial en porcentaje o en tallos, conservando la diferencia
operativa: la baja parcial se registra en campo y es frecuente; la erradicación total la decide el
ingeniero de producción o el gerente de ventas (H-19).
`RF-016` debe exponer al auditor los registros marcados como erróneos, en conflicto o pendientes,
con su antigüedad y filtrables por bloque y por capturador.

### DEC-09 · El operador de la plataforma tiene acceso de infraestructura, no funcional
Puede operar sobre los datos en la nube para dar soporte —copias, restauración, disponibilidad,
integridad— pero **no como usuario funcional del negocio de un tenant**. `RF-017` se refiere siempre
al administrador de la **empresa**.
> `[!]` Una copia de seguridad *contiene* los datos del tenant. «Acceso indirecto» solo es real si
> los respaldos van cifrados con una clave que el operador no pueda usar y si toda operación queda
> auditada. Contra `CN-03` esto es también una cláusula de contrato.

### DEC-10 · Alcance inicial del BI: los seis reportes actuales
Siembra · Producción · Plagas y enfermedades · Inventario de material vegetal · Pérdida de flor ·
Estimados de flor. Es lo que el negocio ya usa, no una hipótesis del equipo.

### DEC-11 · Una base de datos por empresa, con esquema común
Mismo esquema para todas, base independiente para cada una. **Ubicación indiferente**: el criterio
es la opción más barata y segura. Fallback aceptable: esquema por empresa. **No aceptable: tabla
compartida con columna discriminadora.**
**Gana:** restaurar el respaldo de un solo cliente es trivial. **Cuesta:** cada migración de esquema
se aplica a N bases; sin automatización los esquemas divergen (`CN-29`).

### DEC-12 · Objetivos de continuidad, diferenciados por tipo de fallo

| Situación | Tolerancia |
|---|---|
| **Pérdida de información** | **CERO.** Objetivo duro, sin excepción |
| **Fallo de funcionamiento** (no se puede operar, no sincroniza) | **1 hora** |
| **Reparación o carga de respaldo** | **1 día** |
| **Desajuste de datos** (las proyecciones no cuadran con lo real) | Mayor, pero sin pérdida |

La hora coincide con S2: aguantan ~1 hora caídos, 4 horas es demasiado. **El offline-first (`CN-13`)
es el mitigante natural**: convierte una caída de plataforma en un retraso, no en una parada.
Son **los primeros números medidos y diferenciados del proyecto**. Queda como `CN-15`.

### DEC-13 · La app de plagas no se toca; la enfermedad entra como motivo
FlorLogic **no reemplaza ni consume** la app de plagas. La usan roles no contemplados y el alcance no
da. Lo que sí entra: **enfermedad como motivo** de disminución de producción.
> `[!]` **Residuo honesto.** El reporte de FlorLogic responderá *«cuánta producción se perdió por
> enfermedad»*, no *«qué plagas hay y dónde»*. Decirlo así al cliente.

### DEC-14 · La cama es la unidad de inventario, y se divide en secciones
**Nada se cuenta por esqueje.** Se usa un aproximado calculado desde el área cultivada y la densidad
por m². **Una cama se divide en SECCIONES**, cada una con su área, su variedad y su densidad.
> Ejemplo del equipo: una cama con **30% pompón morado y 70% violeta azulada**, porque 6 m² se
> cultivaron de violeta azulada a 6 tallos/m².

La **sección** es donde vive el dato de siembra; la **cama** es el agregado y la unidad de
inventario. Es el cambio de modelo de datos más grande hasta ahora. **Confirmado documentalmente**
en §4.3.
`[!]` Duda abierta de S4: no está claro si una cama puede dividirse entre **flores distintas** (no
solo variedades de la misma flor). Dos flores distintas tendrían procesos y fechas de desbotone
distintos. **Sin preguntar al cliente.**

### DEC-15 · Dos lecturas de la producción, y una vista geométrica
1. **Tallos aproximados** — el resultado del motor.
2. **Porcentaje de plantas estimadas reales** frente al 100% de lo cultivado en la cama.

La segunda existe porque el número de plantas es un aproximado: **el porcentaje no exige confiar en
un conteo que nadie hizo**. Sobre ella se construye una **vista geométrica**: las camas como
rectángulos que muestran cuánta producción sigue viva. Recupera con justificación la idea de «mapa
de calor de camas» del pitch de S1.

### DEC-16 · La IA vuelve, partida en dos

| Pieza | Dónde corre | Alcance |
|---|---|---|
| **Asistente de captura** | Local, **sin conexión** | Ayudar a llenar el formulario y recordar qué falta |
| **IA analítica** | **En la nube** | Consultas, análisis, apoyo al BI. No participa en la captura |

**No es el asistente abierto que se descartó en S3.** Rige `PR-01`: propone, el sistema valida, el
usuario confirma. Nunca escritura silenciosa.
**Plantillas configurables (`RFP-07`): explícitamente fuera de la primera entrega.**
> `[!]` **Confianza en disputa.** S3 registró que *«la IA embebida es decisión de desarrolladores, no
> requisito del cliente»* y que *«fue idea de venta»*. Hasta que haya **cita textual** del cliente
> pidiéndola, esto es `PROP`, no `CONF`.
> `[!]` «Proponer estrategias» no es estimable.
> `[!]` **Choque con `RF-012`:** una IA que consulta datos de una empresa no puede arrastrar contexto
> de otra. El aislamiento aplica al modelo, a sus prompts y a cualquier índice o caché.

---

## 7 · Requisitos

### 7.1 Catálogo vigente — `Documentacion/FuncionalidadesSignificativas.xlsx`

Columnas: `Identificador · Especificación · Tipo de funcionalidad significativa (Valor de negocio /
Reto técnico / Ambos) · Justificación · Observación`.

| ID | Enunciado (resumido) | Tipo |
|---|---|---|
| **RF-001** | Registrar la siembra de una cama (bloque, nave, cama, variedad, fecha, cantidad de esquejes) **sin conexión**, confirmando en pantalla que quedó guardado `[!] escrito sobre el modelo de esquejes que DEC-14 invalidó` | Valor de negocio |
| **RF-002** | Registrar el corte de una cama por variedad, grado y fecha **sin conexión**, aceptando varios registros para la misma cama en días distintos `[!] mismo problema` | Valor de negocio |
| **RF-003** | Sincronizar con el repositorio central cuando haya conectividad, sin perder ningún registro y sin aplicar ninguno más de una vez | Ambos |
| **RF-004** | Impedir el registro de un evento imposible dentro del ciclo fenológico y **explicar el motivo del rechazo**, aun sin conexión | Valor de negocio |
| **RF-005** | Rechazar todo dato que implique más tallos que plantas sembradas en esa cama | Valor de negocio |
| **RF-006** | Calcular los tallos proyectados a partir del área, la densidad de siembra y el % de productividad esperada de la variedad | Valor de negocio |
| **RF-007** | Repartir los tallos sobre los días de corte — **entra, pero NO como significativa** (`DEC-08`) | — |
| **RF-008** | Regenerar la proyección **al menos una vez por semana**, conservando la versión anterior | Valor de negocio |
| **RF-009** | Registrar la erradicación **parcial o total** de una cama, descontándola de la proyección | Valor de negocio |
| **RF-011** | Mostrar la desviación entre corte real y proyectado por cama, variedad y periodo, señalando lo que sale de la banda | Valor de negocio |
| **RF-012** | Garantizar que ningún usuario acceda, **por ningún canal**, a datos de otra empresa | Valor de negocio |
| **RF-013** | Permitir al administrador de cada empresa parametrizar variedades, densidades, % de productividad, grados, días a corte por etapa, duración del corte y bandas de tolerancia, **sin intervención del equipo de desarrollo** | Valor de negocio |
| **RF-014** | Autenticar al usuario y aplicar los permisos de su rol en el móvil **aun sin conexión** | Valor de negocio |
| **RF-016** | Conservar **y mostrar** (`DEC-08`) para cada registro: empresa, autor, dispositivo, fecha y hora de captura, fecha de sincronización y **valor anterior en caso de corrección** | Valor de negocio |
| **RF-017** | Impedir que un registro sincronizado sea modificado o eliminado por un rol distinto del administrador de la empresa, exigiendo que quede registrada la aprobación | Valor de negocio |
| **RF-018** | Presentar producción proyectada y real agregadas por día, semana y mes, sobre el mismo conjunto de datos | Valor de negocio |
| **RF-019** | Exportar a Excel y PDF la información autorizada, con las mismas restricciones de rol que en pantalla | Valor de negocio |
| **RF-020** | Exigir y garantizar la descarga de la parametrización más reciente (variedades, camas, reglas) antes de permitir capturar | Valor de negocio |
| **RF-021** | Registrar una marca de tiempo inmutable en cada evento capturado offline, bloqueando si se detecta alteración del reloj | Valor de negocio |
| **RF-022** | Resolver conflictos de sincronización por **orden cronológico estricto**, con registro de cambios consultable | Ambos |
| **FR-023** | Captura estática en el tiempo de los parámetros de cálculo y proyección (versionado inmutable) `[!] debería ser RF-023` | Ambos |
| **FR-024** | Identificar e informar visualmente **la causa** de una caída en la producción proyectada `[!] debería ser RF-024` | Valor de negocio |

`[!]` La hoja `HU` de ese mismo archivo contiene un ejemplo de **otro proyecto (HortalSoft)**, no de
FlorLogic.

### 7.2 Requisitos candidatos `RFP-nn` — sin validar

Abiertos por la tanda 2. Casi todos dependen de `BR-N6`.

| ID | Qué |
|---|---|
| `RFP-01` | Registrar la siembra por **sección** de cama (área, variedad, densidad) |
| `RFP-02` | Registrar el **motivo** de la disminución de producción, incluida enfermedad |
| `RFP-03` | Vista **geométrica** de camas con porcentaje de producción |
| `RFP-04` | Doble lectura: tallos aproximados y % de plantas reales |
| `RFP-05` | Asistente de captura **offline** |
| `RFP-06` | Consultas y análisis asistidos por IA **en la nube** |
| `RFP-07` | Plantillas de captura configurables — **fuera de la primera entrega** |
| `RFP-08` | Restaurar los datos de una empresa sin afectar a las demás |

### 7.3 Las tres numeraciones que convivieron

| Archivo | Fecha | Numeración | Estado |
|---|---|---|---|
| `5_RF_CRITICOS_v1.xlsx` | 10-ago | `RF-C01..RF-C20` | Histórico. Archivado |
| `6_FUNCIONALIDADES_CRITICAS_v1.xlsx` | 12-ago | `RF-001..RF-019`, en clave SaaS | Histórico. Archivado |
| `FuncionalidadesSignificativas.xlsx` | 13-ago | `RF-001..FR-024` | **Vigente** |

**Mapeo viejo → nuevo:** C01→001, C02→002, C03→003, C05→004, C06→005, C07→006, C08→007, C11→008,
C09→009, C10→010, C12→011, C13→013, C16→014, C15→015, C18→016, C17→017, C14→018,
C04→022 (reformulado). Sin antecedente: 012 (aislamiento), 019 (exportación), 020..024.

`[!]` El catálogo vigente **omite RF-007, RF-010 y RF-015** sin explicación escrita en el archivo;
`DEC-08` les dio destino después. `RF-C19` desapareció sin equivalente y fue absorbido por `RF-016`.

### 7.4 Decisiones de alcance `D-nn` (fase de levantamiento)

**Dentro:** D-01 captura en campo de siembra y producción por cama, offline · D-02 proyección por
cama, variedad y fecha · D-03 erradicaciones y bajas con recálculo hacia adelante · D-04 reportes y
proyección a partir de la producción proyectada · D-05 trazabilidad hasta la cama de origen ·
D-06 parametrización por el administrador.

**Fuera de la fase 1:** D-07 cruce con pedidos y clientes — *«solamente va a mostrar qué flor se va a
producir»* · D-08 Florverde — *«No nos metamos en Florverde, es otra cuestión adicional»* ·
D-09 gestión de personal e insumos — *«No nos metamos con personal, en algún momento va a tocar»* ·
D-10 registro obligatorio de actividades culturales.

### 7.5 Alcance de la fase 1, en una lista

**Entra:** captura offline de siembra, corte, bajas y erradicaciones · sincronización sin perder ni
duplicar · validación de reglas en el dispositivo · motor de proyección por cama, variedad y fecha ·
regeneración semanal conservando versiones · control de desviación proyectado vs. cortado ·
parametrización por empresa · trazabilidad hasta la cama · aislamiento total entre empresas ·
exportación a Excel y PDF · tableros propios.

**No entra:** precios, ventas y rendimiento económico · cruce con pedidos y clientes · Florverde ·
gestión de personal e insumos · poscosecha (el alcance termina en el corte) · registro obligatorio de
actividades culturales · datos climáticos · integración con PowerBI o con la app de plagas ·
plantillas de captura configurables.

---

## 8 · Restricciones `CN-nn`

**Una sola serie continua y compartida** entre negocio y técnica (decisión del 19-ago-2026). No
existe serie `RT-nn`. Los IDs no se reciclan.

### 8.1 De negocio — `Documentacion/RestriccionesNegocio.xlsx` · `CN-01..CN-09`

9 filas. Columnas: `Tipo · Restricción · Justificación · Plan acción`.
`[!]` **El archivo no lleva los identificadores CN escritos**: la numeración vive solo en el modelo y
en este documento. `[!]` La columna «Plan acción» está **vacía en las 9 filas**.

| ID | Tipo | Restricción |
|---|---|---|
| **CN-01** | Tiempo | Entrega e implementación en **mayo de 2027**, después de la temporada alta de marzo–abril |
| **CN-02** | Presupuesto | ~**20.000 USD** (~61 M COP) para el desarrollo |
| **CN-03** | Legal | **Secreto empresarial** — art. 260, Decisión 486 de la CAN. Base legal de `RF-012` |
| **CN-04** | Proceso | El proceso a apalancar **apenas se está definiendo**; se implementa lo que se vaya identificando |
| **CN-05** | Presupuesto | **Se desconoce el presupuesto de mantenimiento** del sistema |
| **CN-06** | Humano | Decisiones de arquitectura tomadas por **ingenieros sin experiencia medible en el sector** |
| **CN-07** | Proceso | El despliegue e instrucción **no puede retrasar la operación más de 7 días** |
| **CN-08** | Humano | **Resistencia al cambio** de los supervisores. Crítica para el éxito |
| **CN-09** | Tiempo | Disponibilidad limitada antes de la implementación para pruebas y entregas previas |

`[!]` `CN-02` choca con el pitch SaaS de ~10 USD/usuario/mes. `DEC-02` lo explica como dos momentos
distintos, pero conviene decirlo cada vez que salga.

### 8.2 Técnicas — `Documentacion/RestriccionesTecnicas-IA.xlsx` · 26 filas

Columnas: `Identificador · Tipo · Restricción · Justificación · Origen · Estado · Plan acción`.
Tipos: **impuesta** (7) y **adoptada** (19). Estado: DENTRO 21 · EN DUDA 4 · CERRADO 1.

**Impuestas**

| ID | Qué dice | Estado |
|---|---|---|
| **CN-10** | PowerBI **no** es restricción impuesta: el sistema no debe integrarse con el BI actual | CERRADO `[!] revertido de hecho, ver §13.2` |
| **CN-11** | El cobro de suscripción exige pasarela de pago; PayU es la opción mencionada | EN DUDA |
| **CN-17** | **No hay conectividad de datos confiable** en el área de cultivo (H-34) | DENTRO |
| **CN-18** | Doble canal obligatorio: **app móvil de captura + web de consulta** (H-40) | DENTRO |
| **CN-19** | La app de plagas sigue viva: no se reemplaza, no se consume, no se integra (`DEC-13`) | DENTRO |
| **CN-20** | Modelo heredado de **~300 tablas** y sistema de productividades sin identificar (H-35, H-39) | EN DUDA `[!] BLOQUEANTE` |
| **CN-21** | Dispositivo de captura sin especificar y **sin partida de hardware** | EN DUDA `[!]` |

**Adoptadas**

| ID | Qué dice | Estado |
|---|---|---|
| **CN-12** | RBAC con partición por empresa: cada permiso se evalúa contra el par (rol, empresa) | DENTRO |
| **CN-13** | **Offline-first obligatorio**: captura, validación, autenticación y marca de tiempo íntegramente en el dispositivo. *Es la restricción rectora* | DENTRO |
| **CN-14** | BI propio y cerrado antes que integración con terceros | DENTRO `[!] ver §13.2` |
| **CN-15** | Objetivos de continuidad de `DEC-12` | DENTRO |
| **CN-16** | Una base de datos independiente por empresa, con esquema común | DENTRO |
| **CN-22** | Reglas duras evaluables **en el dispositivo**, con motivo de rechazo visible | DENTRO |
| **CN-23** | Autenticación y permisos **offline** durante toda la jornada | DENTRO `[!] BR-N5` |
| **CN-24** | Sincronización **idempotente**, cronológica, con mediación opcional y bitácora | DENTRO |
| **CN-25** | Marca de tiempo confiable; bloqueo ante reloj alterado | DENTRO |
| **CN-26** | Catálogo/parametrización descargado y **versionado** antes de capturar | DENTRO |
| **CN-27** | **Versionado inmutable** de proyecciones y de los parámetros con que se calcularon | DENTRO |
| **CN-28** | Cifrado en tránsito y en reposo, incluidos respaldos. **Custodia de la clave SIN DECIDIR** | EN DUDA `[!]` |
| **CN-29** | Migraciones de esquema **automatizadas** sobre N bases desde el día uno | DENTRO |
| **CN-30** | Pico de calendario **simultáneo** entre tenants (+60%) — perfil de carga hostil | DENTRO |
| **CN-31** | Asistente de captura local, **vocabulario restringido**, nunca escritura silenciosa | DENTRO |
| **CN-32** | IA analítica en la nube, **jamás dependencia de la captura** | DENTRO |
| **CN-33** | Exportar a Excel/PDF es la **única interoperabilidad de fase 1**. Sin API pública | DENTRO `[!] ver §13.2` |
| **CN-34** | Operador con acceso de infraestructura, no funcional, y auditado | DENTRO |
| **CN-35** | Costo operativo por tenant acotado: sin licencias que escalen por empresa o usuario | DENTRO `[!]` |

**Las cuatro `EN DUDA` son `CN-11`, `CN-20`, `CN-21` y `CN-28`.** `CN-20` la responde el cliente;
`CN-28` la decide el equipo y va también al contrato. Son las dos que hay que cerrar primero.

`[!]` **Pendiente:** sincronizar `CN-17..CN-35` al `.archimate` como elementos Constraint. Hoy solo
viven en el xlsx.

---

## 9 · Atributos de calidad

**Son 13, no 14.** Dos redefiniciones que hay que respetar en todo documento nuevo:

- **Seguridad de funcionamiento ya no existe como atributo propio.** Queda **absorbida por
  Confiabilidad**, que ahora cubre también la pérdida y el daño de información ante fallos.
- **Trazabilidad ya no existe con ese nombre.** Se reemplaza por **Capacidad para ser Auditado**,
  definida como **trazabilidad + cumplimiento** (certificaciones, autoridad fitosanitaria, auditor
  externo, retención normativa, datos personales, secreto empresarial `CN-03`).

### 9.1 Ranking por actor — hoja `2. Priorización-QA`

Cada actor reparte 1..13 (menor = más prioritario). Total 91 por actor, 273 global.

| # | Atributo | Sup. campo | Ger. producción | Adm. sistema | Total | % |
|---|---|:--:|:--:|:--:|:--:|:--:|
| 1 | **Confiabilidad** | 1 | 1 | 1 | **3** | 1,10% |
| 2 | Disponibilidad | 2 | 2 | 7 | 11 | 4,03% |
| 3 | Rendimiento | 3 | 5 | 9 | 17 | 6,23% |
| 4 | Capacidad para ser Auditado | 9 | 4 | 4 | 17 | 6,23% |
| 5 | Capacidad | 11 | 3 | 5 | 19 | 6,96% |
| 6 | Capacidad para ser Administrado | 13 | 7 | 3 | 23 | 8,42% |
| 7 | Experiencia de Usuario | 6 | 6 | 12 | 24 | 8,79% |
| 8 | Seguridad | 10 | 8 | 6 | 24 | 8,79% |
| 9 | Interoperatividad | 5 | 10 | 10 | 25 | 9,16% |
| 10 | Escalabilidad | 8 | 9 | 8 | 25 | 9,16% |
| 11 | Capacidad para ser Soportado | 12 | 13 | 2 | 27 | 9,89% |
| 12 | Portabilidad | 4 | 11 | 13 | 28 | 10,26% |
| 13 | Accesibilidad | 7 | 12 | 11 | 30 | 10,99% |

**Confiabilidad es 1 para los tres actores: único consenso total del proyecto.**
**Tres empates sin romper:** Rendimiento/Auditado (17) · UX/Seguridad (24) ·
Interoperatividad/Escalabilidad (25). Se rompen con el patrón de respuestas de la caracterización,
no con otra encuesta de ordenamiento.

### 9.2 Trade-off — hoja `1. Trade-Off-QA`

Tres listas, y la tercera **no es la media aritmética de las dos primeras**: `[!]` la aritmética de
la columna «promediado» no está justificada en ninguna parte y hay que verificarla antes de usarla.

| # | Propuesto por el **usuario/cliente** | Propuesto por los **arquitectos** | **Promediado** |
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

`[!]` **Tensión histórica sin resolver.** El equipo bajó Accesibilidad al final y UX al 8, mientras el
cliente puso UX en 2. El argumento fue *«son usuarios que ya conocen el negocio y solo pasan de papel
a celular»*. Choca con `BR-24`. Y accesibilidad se definió en sesión como *«capacidad de ser
interpretado por personas con falta de digitalización o analfabetismo»*, que es exactamente la
premisa del asistente de captura: es probable que el cliente no entendiera el término.

`[!]` En la hoja de caracterización hay un bloque **`Votación por rol`** empezado y sin terminar
(«65 votos por rol, 65 atributos seleccionados (25%)», columnas *Tomador de datos / Producción /
Administrador*, con **una sola celda diligenciada**). Decidir si se completa o se descarta.

### 9.3 Escenarios de calidad — formato acordado

**Dos columnas y nada más: `ID | Escenario`.** El escenario se escribe como **párrafo narrativo
continuo** que contiene los seis elementos de Bass/Clements/Kazman: entorno, fuente del estímulo,
estímulo, artefacto, respuesta, **medida de respuesta**. IDs `ESC-001`, `ESC-002`, …, **agrupados por
atributo de calidad**, no por RF.

Son **ejemplos de uso de la vida real** y del comportamiento esperado, no el contrato final de
aceptación. El esqueleto `ESC-01…ESC-08` de `5_RF_CRITICOS_v1.xlsx` **queda descartado**: no usar ni
su contenido ni su numeración.

**Un escenario sin medida numérica no es un escenario:** se marca `PENDIENTE` con lo que falta
preguntar, no se completa con un número inventado.

**Estado: no existe ninguno.** Están bloqueados por la falta de medidas de respuesta (§11).

---

## 10 · Las 262 preguntas de caracterización

**Fuente vigente:** `Documentacion/MINI QAW PLANTILLA NO TERMINADA.xlsx`, hoja `3. Caracterización`.
Columnas: `Atributo · Pregunta · Respuesta Juan · Respuesta Jerónimo · Respuesta Cliente · Nota`.

### 10.1 Estado real — actualizado

| Columna | Diligenciadas |
|---|---|
| **Respuesta Juan** | 260 / 262 |
| **Respuesta Jerónimo** | 260 / 262 |
| **Respuesta Cliente** | **262 / 262** |
| Nota | 29 |

**Este es el cambio de estado más importante y más reciente del proyecto.** Toda la documentación
anterior a esta fecha dice «Jerónimo 0 · Cliente 0»; **ya no es cierto**.

`[!]` **Dos fuentes para lo mismo, y hay que matar una.**
`Documentacion/Levantamiento de requisitos/Entrevistas/PREGUNTAS_CARACTERIZACION.xlsx` y su gemelo
`.md` siguen en **0% respondido**, y `Documentacion/Mini QAW FlorLogic.xlsx` solo tiene la columna de
Juan. La **única** con las tres columnas es `Documentacion/MINI QAW PLANTILLA NO TERMINADA.xlsx`.
El nombre miente: es la vigente.
`[!]` Solo `PREGUNTAS_CARACTERIZACION` conserva los **identificadores** (`CNF-01`, `DSP-02`, …) y la
columna `Bloque`; la hoja vigente **no los tiene**. Antes de archivarla, portar los IDs.

### 10.2 Distribución y convención

262 preguntas sobre 13 atributos, más 3 de cierre. Prefijos y conteo:
`CNF` 57 · `AUD` 32 · `RND` 21 · `DSP` 20 · `SEG` 19 · `UXP` 18 · `ADM` 16 · `CAP` 15 · `INT` 13 ·
`SOP` 13 · `POR` 13 · `ESL` 11 · `ACC` 11.

`[!]` **Escalabilidad usa `ESL-`, no `ESC-`**: `ESC-nnn` está reservado para los escenarios.
La v1 (248 preguntas, 14 atributos, prefijos `SFN`/`TRZ`/`ESC`) quedó **reemplazada**, no coexiste.

**Reglas fijadas el 21-ago-2026:**
- **Se responden sí/no.** Ninguna admite respuesta abierta. El matiz va en `Nota`, nunca dentro de la
  pregunta.
- **No mencionan roles.** Se pregunta por la necesidad, no por quién la tiene.
- Van **escalonadas** dentro de cada bloque de menor a mayor exigencia (dato → sección → cama →
  bloque → finca; en el momento → al sincronizar → después; una jornada → tres días → una semana →
  quince días).
- **El punto donde el cliente pasa de «Sí» a «No» dentro de un bloque ES la medida de respuesta** del
  escenario que se va a escribir. Por eso **el orden de las filas no se altera**: para revisar por
  atributo se filtra, nunca se reordena.
- **Un «No» también sirve:** cierra alcance y deja por escrito el respaldo del cliente para descartar.

**Cambio del 27-ago-2026 — la v3 escribe cada pregunta como escenario, no como funcionalidad.**
`PREGUNTAS_CARACTERIZACION.md`/`.xlsx` pasaron a **v3**: las 262 preguntas conservan identificador,
orden, bloque, escalonamiento e intención, pero cada una se redacta como una sola frase interrogativa
que contiene los **seis elementos** de Bass/Clements/Kazman (entorno, fuente del estímulo, estímulo,
artefacto, respuesta, medida de respuesta), de modo que el `ESC-nnn` se pueda escribir directamente
desde la fila. La medida sale del **propio escalón** de cada fila; **no se inventó ningún número**.
La regla de «no mencionan roles» sigue viva para la *necesidad*, pero la **fuente del estímulo** se
nombra de forma genérica (*quien captura*, *quien consulta*, *un auditor externo*, *el dispositivo*,
*el servicio en la nube*), nunca con los tres roles del mini QAW. `FIN-01..03` **no son escenarios**
y quedaron intactas. La v2 está en `_to_delete/caracterizacion-v2/`.
`[!]` La hoja vigente `MINI QAW PLANTILLA NO TERMINADA.xlsx` **sigue con la redacción vieja**: al
portar los IDs (§10.1) hay que decidir si se porta también el texto v3 sobre las 262 respuestas ya
diligenciadas.

### 10.3 Estatus epistémico de cada columna

- **Juan y Jerónimo:** hipótesis de ingenieros **sin experiencia en el sector** (`CN-06`). **No son
  requisitos.** Sirven para fijar el alcance de la demo, para ser contradichas, y para medir la
  distancia entre lo que el equipo asume y lo que el negocio necesita.
- **Cliente:** es la voz del negocio, pero sigue siendo **la misma fuente única** de siempre.
- **Divergencias medidas:** Juan vs. Jerónimo difieren en **15** preguntas. **Juan vs. Cliente
  difieren en 94.** Ese 36% de desacuerdo es el mapa de dónde el equipo estaba flojo.

`[!]` **Marcar por escrito el estatus de las columnas del equipo.** Ya pasó una vez que un supuesto
del equipo se leyera como dicho del cliente (`DEC-16 [!]`).

**Triage por responsable:** del cliente son `CNF DSP RND AUD CAP UXP ACC POR`; del equipo son
`SEG ESL SOP ADM INT`.

### 10.4 Lo que el cliente respondió y contradice decisiones ya cerradas

Esta es la sección más importante del archivo hoy. **Nada de esto está todavía propagado a
`DEC-nn`, al catálogo de requisitos, a las restricciones ni al modelo.**

**A · La ventana offline es larga → cae la premisa técnica de la PWA.**
El cliente respondió **SÍ** a capturar una jornada completa, **tres días**, **una semana** y **más de
quince días** sin sincronizar. `PLAN_DEMO_CAPTURA §4.4` decía que si la ventana offline es de una
jornada o más, **el producto deja de poder ser una PWA**. Con esta respuesta, ese punto de decisión
está alcanzado.

**B · El cliente NO pide velocidad de captura.**
Respondió **NO** a: que registrar una cama tome menos que en papel · menos de un minuto · menos de
treinta segundos · capturar un bloque completo en una jornada (nota: *«sobra»*) · capturar la finca
completa en una jornada (*«sobra»*) · que buscar una cama dé resultado inmediato (*«ligero, no de
inmediato»*). **Toda la premisa de rapidez de la demo y del asistente de captura era del equipo, no
del cliente.**

**C · El cliente NO pide ergonomía de campo.**
**NO** a: capturar con una sola mano · con guantes puestos · dictando por voz · sugerencias mientras
se captura · ver el avance de la jornada · usar la app sin capacitación formal · vista gráfica del
bloque en vez de lista. **Sí** pidió que la mayoría de los datos se escojan de una lista y no se
escriban. Esto desarma buena parte de `RFP-05`, de `CN-31` y de la variante «guiada» de la demo.

**D · El cliente NO pide cifrado ni aislamiento demostrable.**
**NO** a: que la información del dispositivo quede ilegible si se pierde · demostrar documentalmente
el aislamiento · registrar todo acceso técnico · que la información viaje cifrada · que los respaldos
estén cifrados · que la llave sea distinta por empresa · registrar cada exportación. Y **NO** a que
dentro de una empresa toda la información sea visible para todos.
`[!]` **Esto no cierra `CN-28`:** el cifrado también responde a `CN-03` (secreto empresarial) y al
contrato SaaS, no solo a lo que el cliente pida. Pero cambia la conversación por completo.
`[!]` Sí respondió **SÍ** a **borrar de manera remota** un dispositivo perdido (nota: *«opcional»*),
que es justo lo contrario del NO al cifrado local.

**E · El cliente SÍ quiere interoperar — cae `DEC-06`/`CN-10`/`CN-33`.**
**SÍ** a que la información se pueda leer desde una herramienta de análisis externa — **nota literal:
«POWER BI»**. **SÍ** a tomar información del sistema administrativo que la empresa ya tiene. **SÍ** a
convivir con la app de plagas sin reemplazarla. Y en la pregunta sobre entregar información a nómina
o productividades respondió NO con la nota **«PRODUCTIVIDADES SÍ»**.
Esto coincide con lo dicho en S4: *«un BI propio, pero con la necesidad obligatoria de hacer
integraciones con otras herramientas de BI»* y *«al parecer sí vamos a integrar, pero a la vez
ofrecer»*.

**F · El cliente SÍ quiere on-premise como opción — tensiona `DEC-01`.**
**SÍ** a *«¿Se necesita que el sistema pueda instalarse en servidores de la propia empresa y no solo
en la nube?»* y **SÍ** a llevarse toda la información si se termina la relación con el proveedor
(que Juan había respondido NO). Pero **NO** a operar en una finca de otro país.

**G · El cliente SÍ quiere cumplimiento y certificaciones — tensiona `H-44`/`D-08`.**
**SÍ** a: conservar trazabilidad **de manera indefinida** (nota: *«DE POR VIDA»*) · demostrar ante
auditoría externa el origen de cualquier cifra · **responder auditorías de certificación (Florverde,
Rainforest Alliance, GLOBALG.A.P.)** · conservar la evidencia de labores que exige la autoridad
fitosanitaria · demostrar que la información no fue alterada tras cerrar el periodo.
Pero **NO** a: saber fecha y hora exactas de captura · distinguir hora de captura de hora de
sincronización · registrar quién consultó o exportó · exportar el registro de auditoría · constancia
de quién vio información protegida.
`[!]` **Esas dos mitades no encajan.** No se puede demostrar ante un auditor el origen de una cifra
sin la marca de tiempo que se acaba de descartar. Es la contradicción más grave del conjunto.

**H · El cliente NO quiere mediación humana de conflictos — cae `DEC-05`.**
**NO** a dejar ambas capturas y que decida una persona. Nota: **«NO DEJA INGRESAR EL ÚLTIMO
REGISTRO»** — es decir, el segundo registro se rechaza en el momento. Pero **SÍ** a que quien capturó
se entere cuando su captura fue descartada o modificada.

**I · El cliente SÍ quiere el aviso sin bloqueo — se resuelve `RG-06`.**
**SÍ** a *«advertir, sin bloquear, cuando un dato se aleja mucho de lo capturado históricamente en esa
misma cama»*, donde Juan había dicho NO. **La regla de la razón de la demo queda respaldada.**

**J · Otras reversiones puntuales.**
- **NO** a confirmación final antes de guardar, y **NO** a impedir cerrar con campos vacíos
  (nota: *«hay campos que no son concordantes con otros»*, *«pueden haber datos en blanco»*).
- **NO** a que la estructura de captura cambie según la variedad; **SÍ** a que sea siempre la misma
  para todos los bloques. `[!]` Contradice el hallazgo de §4.3 punto 4 (son dos formatos).
- **NO** a excluir de la proyección lo no verificado, **NO** a avisar cuando una proyección se calculó
  con información incompleta, **NO** a impedir publicarla con bloques faltantes
  (nota: *«todo debe estar ingresado»*).
- **NO** a conservar el valor original junto al corregido (nota: **«SOLO LA CORREGIDA»**).
  `[!]` Choca de frente con `RF-016` y con la mitad de «Capacidad para ser Auditado» que sí pidió.
- **NO** a fijar una meta de porcentaje máximo de error (nota: **«NO PUEDE HABER ERRORES»**).
- **SÍ** a que una proyección recalculada meses después dé el mismo resultado (nota: *«si no se
  modifica sí»*) — respalda `CN-27`.
- **SÍ** a verificar dato por dato y por bloque, donde Juan había dicho NO; **NO** a agrupar por
  sección de cama, donde Juan había dicho SÍ.
- **NO** a almacenar fotografías ni documentos escaneados. **SÍ** a conservar en línea todo desde el
  primer día sin límite y con la misma rapidez.
- **Escalabilidad: 11 SÍ de 11.** Es el único bloque sin un solo NO — costo que no crece por finca,
  cambios aplicados a todas las empresas sin intervención manual, años de historia sin lentitud,
  añadir un tipo de labor sin rehacer la captura.
- **NO** a mantenimiento sin sacar el sistema de servicio, pero **SÍ** a disponibilidad 24×7 y los
  7 días. `[!]` Incompatibles entre sí.
- **NO** a que el dispositivo lo ponga cada persona (nota: **«IDEAL DE LA EMPRESA»**) — contradice a
  Juan y afecta a `CN-21`.

### 10.5 Las tres preguntas de cierre

| Pregunta | Respuesta del cliente |
|---|---|
| De todo lo que respondió «Sí», ¿hay algo que, si el sistema no lo cumple, haría que no valga la pena usarlo? | **«CERTEZA DE LOS DATOS, QUE SE INGRESEN LOS DATOS CORRECTAMENTE»** |
| ¿Hay alguna de estas exigencias que esté dispuesto a sacrificar con tal de que la captura en campo sea más rápida? | **SÍ** (sin detallar cuál) |
| ¿Hay algo que necesite el sistema y que ninguna de estas preguntas haya tocado? | **NO** |

La primera respuesta es la frase más importante que ha dado el cliente sobre prioridades: **confirma
Confiabilidad como atributo #1 y la reduce a una sola cosa — que el dato entre correcto.**

---

## 11 · La demo de captura

Código en `app-captura/`. Rama prevista: `codigo-desarrollo` (existe y está vacía).
`[!]` **Sin commitear todavía.**

### 11.1 Estrategia — esto no es un adelanto del producto

La demo **es la mitigación ya acordada** del riesgo de fuente única: *«contrastar contra documentos
reales y validar con un prototipo»*. Cierra `BR-N6`. No se construye para enseñar avance: se
construye para que el cliente capture una cama real delante del equipo **y falle**. Cada punto donde
el prototipo no le sirve es un requisito que hoy no existe.

**R1 · Solo se codifica lo que ya tiene evidencia dura.** Lo que dependa de una respuesta pendiente
entra como **configuración en un archivo**, nunca como código.
**R2 · Se construye para botarlo.** Sobreviven el **modelo de datos**, el **catálogo de reglas**, el
**contrato de sincronización** y el registro de lo que pasó en la mesa.
**R3 · La demo tiene que producir un dato que hoy no existe:** las **medidas de respuesta** que
bloquean todos los `ESC-nnn`. Segundos por cama, toques por cama, rechazos, correcciones.

### 11.2 Tecnología — decidida al construir

**PWA offline-first con cero dependencias de npm.** Se descartaron **React, Vite y Dexie** por la
misma razón, y no es el peso: cada dependencia es un `npm install` que puede fallar donde la demo
tiene que funcionar. Reemplazos propios: `src/vista.ts` (~150 líneas, sin framework), `construir.py`
(tsc + estáticos + service worker) y `src/almacen.ts` (IndexedDB a mano).

```
python servir.py        # red local; dist/ se versiona ya construido
python compartir.py     # túnel de Cloudflare: https://algo.trycloudflare.com
python construir.py     # tras tocar src/. Necesita tsc global. --semilla regenera el catálogo
python pruebas/todas.py # seis suites
```

`cloudflared` se instala una vez: `winget install --id Cloudflare.cloudflared`.

`[!]` **El escáner de la cama y la instalación como app solo funcionan en `localhost` o HTTPS.** Con
la IP de la red local no. **Por el túnel sí** — esa es la razón fuerte de usarlo con el cliente.
`[!]` **La URL del túnel es pública y del otro lado hay datos reales de la finca.** Por eso
`compartir.py` pone **clave por defecto** (usuario `florlogic`, clave generada de 8 caracteres);
`--sin-clave` la quita, `--clave x` la fija. `servir.py --clave x` también acepta clave. Verificado
que el service worker se instala igual detrás de Basic auth y sigue arrancando offline.
El puerto por defecto es **8787 y no 8000 a propósito**: para el navegador cada puerto es un sitio
distinto, con sus propios service workers, cachés y base de datos.

**El producto deja de poder ser PWA si:** la ventana offline es de una jornada o más · el cifrado en
reposo debe ser demostrable · iOS es real. En cualquiera de esos casos → **Flutter** (explorado por
Juan pero no dominado). Lo que se llevaría: `modelo.ts`, `reglas.v1.json` y el contrato de
`sincronizacion.ts`.
`[!]` **La primera condición ya se cumplió** con las respuestas del cliente (§10.4-A).

**Límites conocidos de la PWA:** iOS/Safari desaloja IndexedDB tras ~7 días sin abrir la app si no
está instalada (contra `DEC-12`, pérdida CERO) · `Background Sync` no existe en iOS · el cifrado en
reposo con WebCrypto deja la clave en IndexedDB y **no se puede demostrar documentalmente** ·
la detección de reloj alterado (`CN-25`) es una heurística, no una garantía.

### 11.3 Estado del código

Pantallas: bloques → camas → cama · captura en **rejilla** (como el papel) y **guiada** (una sección a
la vez, botones grandes, escaneo QR) · confirmación final · papel contra app · bandeja de salida con
sincronización, reintento y mediación del conflicto · medidas con export CSV · datos.
`dist/limpiar.html` borra service workers y cachés de un origen. Lo viejo de React quedó en
`app-captura/_obsoleto/`.

**Estructura:** `src/tipos.ts` · `modelo.ts` (Captura, LineaCaptura, ItemOutbox, Evento, Conflicto) ·
`almacen.ts` (IndexedDB) · `id.ts` (UUID v7 e identidad del dispositivo) · `repositorio.ts` ·
`reglas.ts` · `sincronizacion.ts` · `metricas.ts` · `escaner.ts` · `vista.ts` · `ui/`.

**Modelo de datos de la demo** — derivado de la plantilla real, no del modelo teórico:

```
Empresa ─< Finca ─< Bloque ─< Cama ─< SeccionCama
                                        ├─ variedad
                                        ├─ lineas          ← lo que se cuenta en campo
                                        └─ plantas         ← lineas × plantasPorLinea(variedad)

Captura        id(uuidv7) · camaId · fecha · tipoPlantilla · estado(pendiente|cerrada)
               capturadoPor · dispositivoId · selloTiempo · versionReglas
LineaCaptura   capturaId · seccionId · variedad · lineas · cantidad · obse · motivoRechazo?
Outbox         capturaId · intentos · ultimoError · estado(pendiente|enviado|conflicto)
Catalogo       variedades · plantasPorLinea · bloques · camas   ← de seed.json, versionado
Reglas         reglas.v1.json                                   ← duras y blandas, versionado
```

Dos tipos de plantilla desde el principio: `novedad_siembra` y `programa_siembra`.

**Datos reales cargados:** 4 bloques · 38 camas · 41 registros · 3 camas divididas · 9 filas marcadas
por la regla · `OBSE` de significado desconocido.

### 11.4 Catálogo de reglas — `app-captura/configuracion/reglas.v1.json`

`toleranciaRazon: 0.02`. Nueve reglas; las **duras** impiden cerrar la cama, las **blandas** avisan y
dejan constancia. Cada una lleva `mensaje` y `queHacer`.

| ID | Tipo | Ámbito | Severidad | Qué comprueba |
|---|---|---|---|---|
| `RG-01` | obligatorio | línea | **dura** | La sección tiene variedad |
| `RG-02` | obligatorio | línea | **dura** | Hay número de líneas |
| `RG-03` | obligatorio | línea | **dura** | Hay cantidad |
| `RG-04` | rango 1–400 | línea | **dura** | Líneas dentro de lo posible (la cama más larga real tiene 231) |
| `RG-05` | rango 1–20000 | línea | **dura** | Cantidad dentro de lo posible (la más alta real es 3.225) |
| `RG-06` | razón plantas/línea | línea | blanda | `cantidad ÷ #líneas` cuadra con la razón de la variedad |
| `RG-07` | variedad repetida | captura | blanda | La misma variedad en dos secciones de la cama |
| `RG-08` | sin secciones | captura | **dura** | La cama tiene al menos una sección |
| `RG-09` | cama repetida mismo día | captura | blanda | Esta cama ya se capturó hoy |

### 11.5 Verificación — `python pruebas/todas.py`, seis suites en verde

`catalogo.prueba.ts` (lo que muestra la app coincide con el formato de papel) · `reglas.prueba.ts` ·
`servidor.py` (carga en paralelo y puerto ocupado) · `compartir.py` (túnel con un `cloudflared` de
mentiras, clave, y la demo entera detrás de la clave) · `correr_persistencia.py` (sobrevive a
recargar) · `recorrido.py` (sesión completa: captura la cama dividida, choca contra una regla dura,
cierra, sincroniza, provoca y resuelve un conflicto, mira las medidas, recarga y corre con la red
apagada). El recorrido **falla si hay error de consola o si una pantalla se sale de 412 px**.
`python pruebas/recorrido.py --fotos` deja capturas en `pruebas/fotos/`.

### 11.6 Bugs propios encontrados y corregidos — vale recordarlos

1. **Service worker que se tragaba toda navegación.** El manejador de `navigate` iba a caché primero
   y le contestaba a CUALQUIER URL con la página de inicio guardada. Ahora es red primero con caída a
   la copia guardada, y `limpiar.html` nunca se intercepta.
2. **`allow_reuse_address = True` en Windows.** Allí SO_REUSEADDR deja que un segundo proceso se ate a
   un puerto YA escuchando y las peticiones caen en uno o en otro sin orden. Ahora se apaga en Windows
   y se pide `SO_EXCLUSIVEADDRUSE`; además se comprueba el puerto antes de atarse y se salta al
   siguiente. `servir.py` imprime su PID.
3. **`socketserver.TCPServer` es de un solo hilo**, con cola de escucha de 5. La app se sirve como
   módulos ES sin empaquetar → el navegador abre ~15 conexiones al cargar → Windows respondía
   `ERR_CONNECTION_REFUSED`. En Linux no se reproduce. Ahora es `ThreadingTCPServer` con cola de 128.
4. **Cabeceras HTTP son latin-1:** una raya larga en el realm de Basic auth reventaba la respuesta 401.

`[!]` Si un sitio «sigue vivo» después de apagar su servidor, es un **service worker** de otro
proyecto en ese mismo puerto: abrir `http://localhost:PUERTO/limpiar.html`, o usar otro puerto.

### 11.7 Qué NO se construye ahora, y por qué

| No se construye | Depende de |
|---|---|
| Autenticación real, roles, RBAC | `BR-N5`, `CN-12`, `CN-23` — sin resolver |
| Multi-tenant, una BD por empresa | `DEC-11` decidido, pero no toca la demo |
| Backend, migraciones, respaldos | `CN-29`, `CN-28` — el cifrado sin decidir |
| Motor de proyección | Faltan el % de productividad y el reparto diario de tallos |
| BI y reportes | `DEC-10` — seis reportes de línea base, ninguno especificado |
| IA de captura o analítica | `DEC-16` sigue marcada `[!]` |
| Vista geométrica del bloque | `RFP-03`, candidato sin validar |

`[!]` **La demo se diseñó contra las hipótesis de Juan, y el cliente ya las contradijo en 94 de 262
preguntas (§10.4).** Antes de llevarla a la mesa hay que releer §11.1–11.4 contra §10.4 y decidir qué
partes siguen teniendo sentido. En particular: la variante «guiada», el cronómetro y el escaneo QR
respondían a exigencias que el cliente no pidió.

---

## 12 · El modelo ArchiMate

Herramienta **Archi** (gratis, open source). Elegida como fuente única del vocabulario y de la
trazabilidad tras revisar LikeC4, Structurizr, IcePanel, diagramas-as-code y suites EA: las otras no
tienen capas de negocio ni de motivación.

**Archivo vigente:** `docs/03-arquitectura/FlorLogic-modelo.archimate`.
Reemplaza a `FlorLogic-glosario.archimate` y `FlorLogic-core-negocio.archimate` (archivados; los IDs
`g-*` del glosario se mantuvieron intactos).

### 12.1 Convenciones del modelo

- **Primera línea de cada documentación = `ALCANCE:`**, con uno de estos valores:
  `DENTRO` · `EN DUDA` · `CANDIDATO` · `CERRADO` · `FUERA-F1` · `CONTEXTO` · `METODO`.
  Ctrl+F sobre `EN DUDA` da la lista de trabajo.
- **Segunda línea = trazabilidad:** `ID: H-nn/D-nn/BR-nn/RF-nnn · Origen: S1|S1-Q|S2|EQ|DOC ·
  Confianza: CONF|INF|PROP|CONTRAD`.
- **`[!]` = inconsistencia detectada y no resuelta.**
- Objetos de negocio **en singular**. Procesos **en infinitivo**, con el término del cliente en la
  documentación.
- Las vistas se construyen **arrastrando elementos del árbol**, nunca creando desde la paleta.
- Los atributos de calidad dicen hoy **«prioridad sin definir, se re-elabora bajo SaaS»**;
  `[!]` hay que actualizarlos con el ranking de §9.

### 12.2 Estado del modelo (último recuento conocido: 15-ago-2026)

236 elementos, 16 vistas. `EN DUDA` 48 · `CANDIDATO` 13 · `DENTRO` 120 · `CERRADO` 4 · `FUERA-F1` 11 ·
`CONTEXTO` 30 · `METODO` 10. **85 elementos con `[!]`.**
`[!]` Estas cifras son del índice de alcance generado el 15-ago y **no incluyen nada posterior**:
ni las restricciones `CN-17..CN-35`, ni el ranking nuevo, ni las respuestas del cliente.

**Los `[!]` más citados:** Accesibilidad · Aislamiento entre empresas · Auditar calidad de datos ·
Banda de exactitud (±10%) · `CN-05` · `CN-11` · Control de exactitud · Cálculo de proyección ·
Desviación proyección vs. corte real · Escalabilidad · Escenarios de calidad · Experiencia de usuario ·
Hueco lógico del clima · Interoperatividad · Motor de proyección · Portabilidad · Registrar baja de
producción · Registro marcado como erróneo · Repositorio central · Riesgo §9.2 · Seguridad ·
Trazabilidad · Asistente de captura offline · IA analítica en la nube · `RFP-01`, `RFP-02`, `RFP-06`,
`RFP-07`.

### 12.3 Trampas del formato `.archimate` — dos errores ya cometidos

**1 · `documentation` es un elemento hijo, no un atributo.** `<element ... documentation="...">`
produce `Feature 'documentation' not found` al abrir. Correcto:
`<element ...><documentation>texto</documentation></element>`.

**2 · En las vistas el tag es `<child>`, NO `<children>`.** Usar `<children>` produce cientos de
`Feature 'children' not found`. Serialización correcta:

```xml
<folder name="Views" id="f-views" type="diagrams">
  <element xsi:type="archimate:ArchimateDiagramModel" name="..." id="v-01">
    <documentation>...</documentation>
    <child xsi:type="archimate:DiagramObject" id="do-1" archimateElement="g-cama">
      <bounds x="20" y="20" width="200" height="85"/>
      <sourceConnection xsi:type="archimate:Connection" id="c-1" source="do-1"
                        target="do-2" archimateRelationship="rel-01"/>
    </child>
    <child xsi:type="archimate:DiagramObject" id="do-2" targetConnections="c-1"
           archimateElement="g-variedad">
      <bounds x="250" y="20" width="200" height="85"/>
    </child>
  </element>
</folder>
```

`sourceConnection` se anida en el hijo **origen**; `targetConnections` es la lista de ids de conexión,
separados por espacio, en el hijo **destino**.
Los `<property key= value=/>` escritos a mano son dudosos: los metadatos van plegados en la
documentación, no como propiedades.

### 12.4 Riesgo operativo

XML de un solo archivo: **si Juan y Jerónimo lo editan en paralelo, el merge en git es inviable.**
O **un solo dueño a la vez**, o instalar **coArchi**.

---

## 13 · Lo abierto

### 13.1 Brechas `BR-nn`

| ID | Incógnita | Bloquea | Estado |
|---|---|---|---|
| **`BR-N6`** | **El proceso de captura a detalle nunca se trabajó con el cliente.** Pide una SESIÓN, no un dato | `RFP-01..05`; deja `RF-001`/`RF-002` sobre un modelo que `DEC-14` invalidó | **Abierta. La más importante** |
| **`BR-N3`** | **Los documentos prometidos no han llegado:** plan de siembra, presupuestos, formatos en papel llenos, tabla de grados, histórico | **Todo** | Abierta |
| `BR-21` | ¿A qué nivel de agregación se miden el −6% actual y el ±10% objetivo? | **La métrica de éxito del proyecto** | Abierta |
| `BR-23` | ¿De dónde sale el % de productividad por variedad, y cómo se reparten los tallos en los ~7 días de corte? | **El último hueco del motor** | Abierta |
| `BR-N2` | El sistema actual sigue sin identificarse: nombre, alcance, qué guarda, quién lo administra | Si FlorLogic reemplaza, alimenta o convive. `CN-20` | Abierta |
| `BR-22` | **¿9 variedades o ~300?** En S1 dijo 9 activas; en S2 ~300 con subvariedades | Dimensionamiento del catálogo. `CN-26` | Abierta · `CONTRAD` |
| `BR-11` | Catálogo de grados y calidades de la finca | Con qué granularidad se registra el corte | Abierta |
| `BR-24` | Accesibilidad quedó al final del ranking pese a ser la premisa del asistente | `RFP-05` | Abierta |
| `BR-N1` | Tiempo máximo aceptable de confirmación de captura en campo | Convierte «que sea fácil» en verificable | Abierta `[!] el cliente respondió NO a todos los umbrales, §10.4-B` |
| `BR-N4` | ¿Puede una misma cama ser registrada por dos capturadores la misma fecha? | Cuánta complejidad hace falta en la sincronización | **No preguntada** |
| `BR-N5` | Duración de la ventana de sesión offline y qué pasa si se pierde el dispositivo | `CN-23`. Donde chocan Seguridad y Disponibilidad | **Parcialmente respondida**, §10.4-A y D |

**Nuevas, abiertas por §4 y §10:** qué significa la columna `OBSE` (325/425) · qué es exactamente una
«línea» · qué significan `lote`, `calibre`, `proveedor`, `contenedor` · si el umbral del 2% de
tolerancia es correcto · si `Astroi` y `Rose` son errores de digitación o densidades distintas · si
una cama puede dividirse entre **flores** distintas y no solo variedades · si nave y bloque son lo
mismo.

**Del equipo, sin depender del cliente:** el cifrado de los respaldos y su cláusula contractual ·
el alcance de la IA analítica · el modelo de suscripción (`CN-05`) · PayU (`CN-11`) · romper los tres
empates del ranking · completar o descartar la votación por rol.

### 13.2 Contradicciones vivas — el inventario

1. **`DEC-06`/`CN-10`/`CN-14`/`CN-33` contra el cliente y contra S4.** Se decidió BI propio y cerrado
   sin integración; el cliente pidió que se pueda leer desde Power BI y desde el sistema
   administrativo, y en S4 el propio equipo dijo *«sí vamos a integrar, pero a la vez ofrecer»*.
   **Estas cuatro entradas hay que reescribirlas.**
2. **`DEC-05`/`CN-24` contra el cliente.** Mediación humana opcional contra *«no deja ingresar el
   último registro»*.
3. **`RF-016`/`DEC-08` contra el cliente.** Conservar el valor anterior contra *«solo la corregida»*.
4. **Auditoría partida por la mitad (§10.4-G).** Demostrar el origen de una cifra sin marca de tiempo
   no es posible.
5. **`H-44`/`D-08` contra el cliente.** Florverde excluido contra responder auditorías de
   certificación desde el sistema.
6. **`DEC-01` contra el cliente.** SaaS puro contra la opción de instalar en servidores propios.
7. **Disponibilidad 24×7 contra mantenimiento con parada.**
8. **`CN-21` contra el cliente.** El equipo asumía BYOD de gama baja; el cliente dice que el
   dispositivo lo pone la empresa.
9. **§4.3-4 contra el cliente.** Son dos formatos distintos, pero el cliente pide una estructura de
   captura única para todos los bloques.
10. **La premisa de velocidad y ergonomía de la demo no la pidió el cliente (§10.4-B y C).**
11. **`BR-22`:** 9 variedades contra ~300.
12. **`DEC-16`:** la IA atribuida al cliente sin cita, contra el registro de S3.
13. **El pitch (`CN-02`) contra el modelo de ingreso recurrente.**
14. **El −6% actual contra el ±10% objetivo** (`BR-21`).

### 13.3 El riesgo que se asume a conciencia

**Todo descansa en una sola voz.** El cliente descartó entrevistar a otras áreas: *«me pregunta por
más personas a las que se pueda entrevistar, pero no, eso no.»* **Planeación —que es donde nace la
proyección— nunca se exploró.**

Lo que mitiga: la profundidad y la representatividad del informante son mucho mayores de lo que
sugiere «una sola voz»; las prácticas descritas no son idiosincrásicas de una finca, lo que sostiene
mejor la apuesta multi-tenant.
Lo que no mitiga: sigue siendo un solo punto de vista, y **un experto de treinta años omite lo que
para él es evidente**.

**Las dos mitigaciones reales no son «preguntar más»:** contrastar contra **documentos físicos
reales** (`BR-N3`) y validar con un **prototipo** (§11).

`[!]` Bajo `DEC-01` (SaaS para varias fincas), este riesgo sube de grado: construir un producto
multi-tenant para ~20 empresas sobre la evidencia de una sola voz de una sola finca es un salto mayor
que construir a medida.

---

## 14 · Citas textuales del cliente

Rescatadas de la hoja `Trazabilidad` de `5_RF_CRITICOS_v1.xlsx`. **La numeración `RF-Cnn` es
obsoleta**; lo que vale es la cita y la regla derivada. La fuente última siguen siendo los `.vtt` y
`2_ENTREVISTA_S1_Diligenciada_y_Vacios.xlsx`.

| # | Cita | Regla derivada |
|---|---|---|
| C01 | *«El formato de plagas y enfermedades se llena en una app totalmente local en el celular y sube a la nube después.»* / *«Que sea fácil, que a veces las aplicaciones ponemos tanta arandela que es muy complicado.»* | **RN-01:** la captura en campo no puede depender de conectividad |
| C02 | *«El supervisor llena formatos a lápiz y papel una hora al día»* y el practicante digita 4 h/semana | **RN-02:** el corte de una cama se registra por día; dura ~7 días, no es un solo evento |
| C03 | *«Sube a la nube después.»* / *«La información tarda 8 días en llegar a planeación y gerencia.»* | **RN-03:** cada registro se entrega al repositorio central **exactamente una vez**; lo no confirmado queda pendiente en el dispositivo |
| C04 | *«Solo 3 personas ingresan información: 1 supervisor y 2 auxiliares.»* | **RN-04:** ante dos registros en conflicto, ninguno se descarta automáticamente `[!] contradicho, §10.4-H` |
| C05 | *«No se puede registrar corte en una cama sembrada hace una semana.»* | **RN-05:** entre siembra y primer corte deben pasar al menos los días a corte mínimos de la variedad, menos el margen de corrimiento (hasta 15 días) |
| C06 | *«Nunca puede superar el 100%: si el sistema arroja más tallos que plantas sembradas, hay un dato malo.»* | **RN-06:** `tallos ≤ plantas sembradas`. **Sin excepción declarada.** Única cota dura del motor |
| C07 | *«1.000 plantas sembradas, 90% de productividad, da 900 tallos proyectados.»* | **RN-07:** la fórmula nuclear del producto, y la única que el cliente verbalizó completa |
| C08 | *«Una cama no corta todo en un día. El corte dura alrededor de 7 días, según variedad.»* | **RN-08:** la producción se reparte sobre la duración de corte según una **curva parametrizable** |
| C09 | *«Se debe digitar para que el sistema reste esa producción proyectada.»* / *«Lo que ya estaba vendido de ahí se cancela o se compra a terceros.»* | **RN-09:** al erradicar, el ciclo vuelve a cero y la producción pendiente se resta |
| C10 | *«Baja de producción: cuando algo falla y parte del producto ya no sirve para venta, se descuenta un porcentaje de la producción estimada. La idea es que sea de un día para otro, hoy pasa cada semana.»* | **RN-10:** la baja se expresa en porcentaje o en tallos sobre una cama y una fecha |
| C11 | *«Hoy se ajusta mensualmente o cuando es necesario. Se quiere que sea semanal.»* | **RN-11:** recálculo semanal como mínimo y bajo demanda; cada versión fechada e identificable |
| C12 | *«El corte real debe quedar dentro del ±10% de lo que el sistema proyectó.»* / *«Hoy el presupuesto contra el corte real está en −6%.»* | **RN-12:** banda de aceptación ±10% parametrizable. **El objetivo es reducir la brecha** |
| C13 | *«Parametrización por parte del administrador: ciclos, días a corte, densidades, márgenes.»* / *«El ingeniero agrónomo define la densidad de siembra según la variedad.»* | **RN-13:** los parámetros del motor son **datos, no código** |
| C14 | *«Gerencia general mensual, gerente de ventas semanal, gerente de producción diario.»* | **RN-14:** la unidad de tiempo base es el **día**; semana y mes son agregaciones que deben conciliar |
| C15 | *«Hay información muy compartida en todo el sistema, no hay restricción.»* Única excepción: supervisores y auxiliares no ven precio de venta | **RN-15** `[!] sin objeto tras DEC-07` |
| C16 | *«App totalmente local en el celular»* + los tres roles + la restricción de precio | **RN-16:** la sesión en campo sigue válida sin conectividad durante una ventana definida |
| C17 | *«Un dato mal registrado solo lo puede corregir el ingeniero de sistemas, con aprobación del gerente o ingeniero de producción.»* | **RN-17:** toda corrección requiere rol administrador **y** referencia a la aprobación |
| C18 | *«Trazabilidad completa hasta la cama de origen.»* / *«El 2% de error lo revisa un auditor y debe llevarlo a 0%.»* | **RN-18:** ningún dato se sobrescribe sin dejar rastro del valor anterior y de quién lo cambió `[!] contradicho, §10.4-J` |
| C19 | *«Ese 2% no está visualizado en ninguna parte.»* | **RN-19:** la meta es llevar el error del 2% al 0%. **Lo que no se ve no se corrige** |
| C20 | *«Que sea fácil…»* El requisito de facilidad es del cliente; **la captura por lenguaje natural NO lo es** | **RN-20:** el asistente propone, el sistema valida, el usuario confirma. **Nunca escritura silenciosa** |

**Otras citas de alcance, de S1-Q y S2:**
*«solamente va a mostrar qué flor se va a producir»* (descarta el cruce con pedidos) ·
*«No nos metamos en Florverde, es otra cuestión adicional»* ·
*«No nos metamos con personal, en algún momento va a tocar»* ·
*«hay que seguir trabajando sí o sí»* (sobre la caída del sistema) ·
*«El sistema perdería todo su valor si en fechas importantes falla»* ·
*«Toda la información puede ser observada por todos siempre y cuando sea bajo el contexto de
producción»* · *«Todo está sistematizado»* (sobre lo que se hace fuera del sistema).

---

## 15 · Qué sigue

1. **Propagar las respuestas del cliente (§10.4) a todo lo demás.** Es lo primero y no es opcional:
   hoy `DEC-05`, `DEC-06`, `RF-016`, `CN-10`, `CN-14`, `CN-21`, `CN-28`, `CN-33` y `H-44` dicen cosas
   que el cliente ya contradijo. Cada reversión necesita su propia `DEC-nn`.
2. **Decidir la tecnología del producto.** La condición de `PLAN §4.4` se cumplió: la ventana offline
   es de días. **La PWA deja de ser defendible como producto** (sigue sirviendo como demo).
3. **Unificar la fuente de caracterización.** Portar los identificadores `CNF-01`… a la hoja vigente
   y archivar `PREGUNTAS_CARACTERIZACION.xlsx` y `Mini QAW FlorLogic.xlsx`.
4. **Sesión con el cliente sobre el proceso de captura (`BR-N6`).** Técnica acordada: **seguir UNA
   cama real de principio a fin**, con los formatos físicos sobre la mesa, en línea de tiempo,
   preguntando en cada hecho: quién se enteró y cómo · dónde quedó anotado · quién necesita saberlo y
   cuándo. Repetir en 10 minutos con una cama que se erradicó — *el camino roto muestra lo que el
   camino feliz esconde*. Event Storming ligero solo si hay 2+ personas del cliente.
5. **Pedir los documentos (`BR-N3`).** Bloquea todo. En especial el detalle completo de **una sola
   cama real** y un pantallazo del sistema actual (`BR-N2`): veinte minutos que pueden cambiar el
   proyecto entero.
6. **Reescribir `RF-001` y `RF-002`** sobre `#líneas × plantas por línea`, no sobre esquejes.
7. **Romper los tres empates del ranking** con el patrón de respuestas, y decidir qué atributos
   llevan escenario.
8. **Escribir los escenarios de calidad** con las medidas de respuesta que salgan de la sesión y de
   la instrumentación de la demo.
9. **Actualizar el modelo ArchiMate:** `CN-17..CN-35` como Constraints, el ranking de §9 en los
   atributos, y las reversiones del punto 1.
10. **Decidir el cifrado de los respaldos** y redactar su cláusula contractual.
11. **Commitear `app-captura/`** en `codigo-desarrollo`.

### 15.1 La hoja de sesión — 12 preguntas sobre una cama real

Para imprimir y llevar. El cliente narra; el equipo solo pincha con estas doce. Cada una tiene lo que
**tiene que haber quedado dicho** para poder pasar a la siguiente.

> **Cómo abrir:** *«Escoja una cama que ya terminó su ciclo completo hace poco. Con número y bloque.
> Traiga los formatos de esa cama, llenos. Y cuéntenos todo lo que le pasó, desde que decidieron
> sembrarla hasta el último día de corte.»* Dibujar una línea de tiempo mientras habla.

1. **Decisión** — ¿Cómo supo el supervisor que tenía que sembrar esta cama, ese día, con esa
   variedad? *(documento de por medio, quién lo hizo, cómo llegó la orden)*
2. **Siembra** — ¿Se sembró completa de una sola variedad? Si no, ¿cómo anotaron cuánto de cada una?
   *(si se divide, con qué frecuencia, en qué unidad — metros, porcentaje, surcos, «media cama» — y
   la densidad de cada parte)* → **cierra `BR-N6`**
3. **Ciclo** — ¿Qué quedó anotado entre la siembra y el corte, y qué de eso movió la fecha?
4. **Pérdidas** — ¿Se le descontó producción? ¿Quién lo decidió, cómo lo expresó y dónde quedó?
5. **Corte** — De los días que duró el corte, ¿cuánto salió cada día? *«¿sale parejo, o hay días de
   mucho y días de poco?»* → **la pregunta que hoy vale más de toda la sesión** (`BR-23`)
6. **Grados** — ¿Cómo clasificaron la flor y dónde? *(pedir la lista en papel)* → `BR-11`
7. **Viaje del dato** — Del papel del supervisor a sus ojos: ¿por dónde pasó y cuánto tardó?
   *(nombre del sistema, quién lo administra, si seguiría existiendo)* → `BR-N2`
8. **Proyección** — ¿Qué porcentaje de productividad usaron y de dónde salió? → `BR-23`
9. **Exactitud** — ¿A qué nivel comparan lo proyectado con lo real, y en qué periodo? *Si son niveles
   distintos, decirlo en voz alta: «entonces no se pueden comparar, ¿cuál usamos?»* → `BR-21`
10. **Consumo** — ¿Quién miró el dato de esta cama y qué decidió con él?
11. **Errores** — ¿Hubo algún dato mal? ¿Cómo se dieron cuenta y quién lo corrigió?
12. **Velocidad y adopción** — ¿Cuánto tomó llenar el formato, y cuál sería el máximo aceptable en
    celular? *Y reabrir accesibilidad con la definición explícita.* → `BR-N1`, `BR-24`

**Cierre — confirmaciones de alcance, buscando un sí o un no:** ¿no maneja precios ni dinero? · ¿no
cruza con pedidos ni clientes? · ¿el alcance termina en el corte? · ¿no entra Florverde, ni personal,
ni insumos? · ¿no reemplaza la app de plagas? · ¿pueden dos personas capturar la misma cama el mismo
día? · si el celular se pierde con datos sin sincronizar, ¿qué debería pasar? · ¿cuántas variedades
activas y cuántas en el histórico?

**Documentos a pedir:** formato de siembra lleno de la cama elegida · formato de corte lleno, los días
completos · formato de bajas/erradicaciones · tabla de grados y calidades · plan de siembra vigente ·
un presupuesto de producción · pantallazo y export del sistema actual.

**Después:** dibujar el proceso tal como lo narró y **devolvérselo para que lo corrija** — un diagrama
mal dibujado que el cliente corrige vale más que diez páginas que aprueba sin leer. Y marcar `CONF`
**solo** lo que tenga cita textual.

---

## 16 · Método de trabajo

### 16.1 Trazabilidad

Cadena obligatoria, en este orden:

```
cita textual del cliente → hallazgo → regla de negocio → requisito → criterio de aceptación
```

El criterio de aceptación se escribe **Dado / Cuando / Entonces**.
**La cita y la interpretación nunca comparten columna.** Si una fila no tiene cita textual, se marca
inferida o propuesta del equipo. **No se inventan citas.**

### 16.2 Redacción de requisitos

- Una sola capacidad por requisito.
- Verbo observable: *registrar, calcular, mostrar, notificar, impedir*. **Nunca** *gestionar* ni
  *optimizar*.
- **Sin solución técnica en el enunciado.** «Registrar en menos de N segundos sin conexión» es
  requisito; «con un asistente de voz local» es decisión de arquitectura y va aparte.
- Origen explícito: qué pregunta, qué sesión, qué persona.
- Funcionales y no funcionales **separados**. Prioridad **MoSCoW**.

### 16.3 Índice de criticidad

Cuatro ejes de 0 a 3, con pesos que suman 100%: sin esto no hay producto **0,40** · riesgo
arquitectónico **0,20** · alto impacto de negocio **0,25** · alto riesgo de fallo **0,15**.

### 16.4 Estilo de los Excel

Fuente **Arial** · **fórmulas reales, nunca valores quemados** · color: verde = confirmado o cerrado,
amarillo = para llenar o parcial, naranja/rojo = alerta o crítico · primera hoja siempre `Como leer`,
con qué es, qué no es y de dónde salió.

### 16.5 Texto para WhatsApp

WhatsApp reformatea y renumera. **Prohibido:** asteriscos, guiones bajos, virgulillas, acentos graves;
líneas que empiecen con guion, asterisco, `>` o número seguido de punto. Se usa `1)` en vez de `1.`.
Las tildes y las mayúsculas sí son seguras.

### 16.6 Regla de honestidad

Se señalan los errores propios y los del cliente **sin suavizarlos**. Un hueco declarado vale más que
uno tapado con un supuesto plausible. Cuando el equipo propone algo que el cliente no pidió, se marca
como propuesta del equipo y se escribe **el contraargumento más fuerte que exista en contra**.

### 16.7 Preferencias de trabajo con el asistente

Español · respuestas concisas y directas, sin relleno · **entregar archivos reales**, no volcar el
contenido en la conversación · señalar los errores del usuario sin suavizarlos.

### 16.8 Operar git desde una sesión en la nube

En sesiones de Cowork **en la nube**, la carpeta montada **no permite `unlink`** y no tiene red (el
push lo hace Juan). Recetas verificadas:

- Cada comando git deja `.git/*.lock` huérfanos. Antes de cada comando:
  `for f in $(find .git -name '*.lock'); do mv "$f" _to_delete/locks/$(basename $f).$(date +%s%N); done`
  (nanosegundos: `$$` siempre vale 5 y colisiona).
- **Dejar el repo sin locks al terminar la sesión**, o el git de Windows falla.
- `git checkout -- .`, `git rm`, `git branch -D` fallan. Para arreglar CRLF:
  `t=$(mktemp); tr -d '\r' < f > $t; cat $t > f` (trunca en sitio, no borra).
- `git commit` necesita identidad explícita:
  `git -c user.name="Juan Avendaño Duque" -c user.email="avendanoduquejuanpablo@gmail.com" commit`.
- `tar` no sobrescribe en la carpeta montada sin `--overwrite`.
- El contenedor en la nube **no llega a registry.npmjs.org ni a github/trycloudflare**. `tsc` sí está.

### 16.9 Estado de git

Repo privado, seis ramas: `main`, `documentacion-desarrollo`, `documentacion-lista`,
`codigo-desarrollo`, `codigo-testing`, `codigo-listo`. Flujo: desarrollo → testing/revisión → listo →
main. **Jerónimo no ha hecho ningún commit.**

Limpieza del 13-ago-2026, **hecha pero sin push**: deshecho un `git rm -r --cached .` disfrazado ·
quitado el CRLF de 11 archivos y añadido `.gitattributes` con `* text=auto` · `filter-branch` purgó
los dos `.mp4` (27 y 63 MB) del historial. Queda `_push_florlogic.sh` en la raíz: borra locks, hace
force-with-lease a las dos ramas, verifica, corre `gc` y borra `_to_delete/`. **Lo ejecuta Juan en Git
Bash.** Después del push el `.git` debe bajar de ~312 MB a unos pocos MB. Refs originales en
`_to_delete/REFS_ANTES.txt`.

---

## 17 · Inventario de archivos

### 17.1 Vigentes en `C:\Users\juanp\FlorLogic`

| Ruta | Qué es |
|---|---|
| **`CONTEXTO.md`** | **Este archivo. El único documento de contexto.** |
| `README.md` · `docs/README.md` | Estructura del repo y de la documentación |
| `docs/03-arquitectura/FlorLogic-modelo.archimate` | **El modelo.** Se abre con Archi |
| `Documentacion/FuncionalidadesSignificativas.xlsx` | **Catálogo de requisitos vigente** (`DEC-04`) |
| `Documentacion/RestriccionesNegocio.xlsx` | `CN-01..CN-09` |
| `Documentacion/RestriccionesTecnicas-IA.xlsx` | `CN-10..CN-35` |
| `Documentacion/MINI QAW PLANTILLA NO TERMINADA.xlsx` | **Mini QAW vigente**: ranking, trade-off y las 262 preguntas con las **tres columnas respondidas** |
| `Documentacion/Mini QAW FlorLogic.xlsx` | Versión anterior, solo con la columna de Juan `[!] duplicado` |
| `Documentacion/.../PLANTILLAS DOCUMENTOS DE EMPRESA/Plantilla digitalizada...xlsx` | **La única evidencia documental de captura real** |
| `Documentacion/.../Entrevistas/Formatos de entrevista/2_ENTREVISTA_S1_Diligenciada_y_Vacios.xlsx` | **Respuestas del cliente sin editar**, pregunta por pregunta |
| `Documentacion/.../Entrevistas/PREGUNTAS_CARACTERIZACION.md` y `.xlsx` | **v3, 27-ago-2026:** banco de preguntas **redactadas como escenarios** (6 elementos), con **los identificadores `CNF-01`…**; `[!]` 0% respondido |
| `Documentacion/.../Entrevistas/Grabaciones  y Transcripciones por sesión/` | Los cuatro `.vtt`. `[!]` La carpeta tiene **dos espacios** entre «Grabaciones» y «y» |
| `Documentacion/.../Propuesta de Idea/FlorLogic_Elevator_Pitch (2).pptx` | El pitch. Nombra al cliente y a las dos empresas |
| `Documentacion/.../Propuesta de Idea/FlorLogic_Mapa_de_Impacto.xlsx` | Mapa de impacto y **análisis de competencia** (§17.4) |
| `app-captura/` | La demo, con su propio `README.md` |
| `_push_florlogic.sh` | Script de push que ejecuta Juan |

### 17.2 Archivado en `_to_delete/`

**Nada se borró.** Todo lo retirado se puede recuperar moviéndolo de vuelta, y mientras no se purgue
el historial de git también sigue ahí.

Contexto refundido en este archivo: `0_CONTEXTO_v3.md` · `DECISIONES.md` · `RESUMEN_SISTEMA.md` ·
`PUNTOS_CLAVE.md` · `LIMPIEZA.md` · `PLAN_DEMO_CAPTURA.md` · `CITAS_TEXTUALES_CLIENTE.md` ·
`HOJA_SESION_CLIENTE.md` · `FlorLogic-indice-alcance.md` · `FlorLogic-guia-de-lectura-archimate.md`.

De limpiezas anteriores: `0_CONTEXTO_para_nuevo_chat.md` (v2) · `REVISION_CONTEXTO_v2.md` ·
`GUION_SESION_CLIENTE.md` (84 preguntas) · `FlorLogic-glosario.archimate` ·
`FlorLogic-core-negocio.archimate` · `FlorLogic-modelo.archimate.bak` · `5_RF_CRITICOS_v1.xlsx` ·
`6_FUNCIONALIDADES_CRITICAS_v1.xlsx` · `1_PLANTILLA_Levantamiento_Requisitos.xlsx` ·
`3_DIAGNOSTICO_Brechas_y_Plan_de_Accion.xlsx` · `4_DOCUMENTOS_Requeridos_al_Cliente.docx` ·
`4b_Lista_para_WhatsApp.txt` · los `.tgz` de la demo.

### 17.3 `OneDrive - UCO\FlorLogic` — deprecada

Todo su contenido está duplicado en el repo **salvo dos cosas**: las **grabaciones `.mp4` de S1 y
S2** (27 MB y 66 MB), que se purgaron del historial de git y ya no existen en la carpeta local, y una
versión intermedia del mini QAW. Los `.vtt` de OneDrive son los mismos con CRLF.
**Antes de dejar de usar OneDrive hay que decidir qué se hace con esos dos `.mp4`.**

### 17.4 Hallazgo pendiente de explotar

`FlorLogic_Mapa_de_Impacto.xlsx`, hoja `Plantilla Visión`, contiene **análisis de competencia**:
**Tend**, **FlorNet.co** y **Mprise Agriware** (ERP internacional sobre Microsoft Dynamics 365).
FlorNet.co es la única con función offline, y **limitada a empaque y ventas**, no a la captura en el
cultivo. Define además el público objetivo (cultivos pequeños, medianos y grandes de Colombia con
conectividad irregular) y la diferenciación del producto.

El modelo dice todavía, en el driver de viabilidad comercial, que *«sigue sin investigación de mercado
que lo respalde»*. **Eso ya no es del todo cierto.** Bajo `DEC-01` esa información pasó de adorno del
pitch a insumo de negocio.

`[!]` **El pitch y el mapa de impacto describen un producto distinto del que el proyecto está
construyendo hoy:** captura conversacional por voz con un agente de IA local como eje, público de
PyMEs y floristerías, alertas administrativas. `DEC-07`, `DEC-16` y las respuestas del cliente
(§10.4-B y C) desmontan buena parte de eso. **Se conservan como testimonio del origen del proyecto,
no como descripción del producto.**
