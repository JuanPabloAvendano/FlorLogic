# FlorLogic — Guía de lectura del modelo ArchiMate

**Versión 1.1 · 15-ago-2026 · acompaña a `FlorLogic-modelo.archimate`**

*Actualizada tras las tandas de depuración y la limpieza del contexto.*

Esta guía explica cómo interpretar el modelo. No repite su contenido: para eso está el modelo
y su espejo en texto, `FlorLogic-indice-alcance.md`.

Está escrita para alguien que abre Archi por primera vez y para alguien que ya lo conoce pero
no sabe qué decisiones se tomaron en *este* modelo concreto.

---

## 0. Lo primero: el modelo no es un dibujo

El error más común con ArchiMate es tratarlo como una herramienta de diagramas. No lo es.

- El **modelo** es el árbol de elementos del panel izquierdo de Archi. Ahí vive la información.
- Las **vistas** (los diagramas) son *ventanas* sobre ese árbol. Un mismo elemento puede aparecer
  en cinco vistas: sigue siendo **un solo elemento**. Si le cambias el nombre o la documentación,
  cambia en las cinco.
- Borrar un elemento de una vista lo quita **solo de esa vista**. Para borrarlo del modelo hay que
  borrarlo del árbol (Archi lo pregunta explícitamente).

De ahí sale la regla que sostiene todo el modelo:

> **Las vistas se construyen arrastrando elementos que ya existen en el árbol. Nunca creando
> elementos nuevos desde la paleta.**

Crear desde la paleta produce dos «Cama», dos «Variedad», dos «Confiabilidad» — que es exactamente
la deriva conceptual entre `.md`, `.xlsx` y transcripciones que este modelo existe para eliminar.

---

## 1. Convenciones de este modelo

Estas tres cosas no vienen de ArchiMate: son acuerdos del proyecto. Están en la documentación de
cada elemento (pestaña **Properties → Main → Documentation** en Archi).

### 1.1 Primera línea: el alcance

```
ALCANCE: DENTRO
```

| Etiqueta | Significa | Cuántos |
|---|---|---:|
| `DENTRO` | El sistema lo maneja en la fase 1 | 120 |
| `EN DUDA` | Depende de una brecha abierta o de una decisión sin cerrar | 48 |
| `CONTEXTO` | Existe en el mundo real pero el sistema no lo gestiona | 30 |
| `CANDIDATO` | Propuesto por el equipo, **sin validar con el cliente** | 13 |
| `FUERA-F1` | Decisión explícita de dejarlo fuera de la fase 1 | 11 |
| `METODO` | Regla de cómo se trabaja, no del producto | 10 |
| `CERRADO` | Era una brecha o contradicción y ya se resolvió | 4 |

`Ctrl+F` en Archi sobre `EN DUDA` te da los 48 elementos cuyo alcance no está decidido. Esa es la
lista de trabajo. `CONTEXTO` es la categoría que suele faltar en los modelos y la que más discusiones
evita: el cuarto frío existe, el ciclo fenológico existe, la estación meteorológica existe — pero
FlorLogic no los gestiona, solo los registra o los ignora.

### 1.2 Segunda línea: la trazabilidad

```
ID: H-03, H-09 · Origen: S2 · Confianza: CONF
```

- **ID** — de dónde sale el hecho: `H-nn` hecho del dominio, `D-nn` decisión de alcance cerrada,
  `BR-nn` brecha abierta, `RF-nnn` requisito funcional, `CN-nn` restricción de negocio.
- **Origen** — `S1` sesión 1 (27-jul, grabada) · `S1-Q` cuestionario de 46 preguntas (no grabado) ·
  `S2` sesión 2 (4-ago, grabada) · `EQ` propuesta o inferencia del equipo · `DOC` documento del cliente.
- **Confianza** — `CONF` el cliente lo afirmó explícitamente · `INF` derivado por el equipo, falta
  validar · `PROP` propuesta del equipo, el cliente no la ha visto · `CONTRAD` hay dos versiones
  incompatibles en las fuentes.

Origen y confianza son **dos ejes distintos**. Algo puede venir de S2 y aun así ser `INF`.

Los atributos de calidad no llevan esta línea: llevan su estado de prioridad (§3.4).

Los requisitos llevan además: `Módulo · Antes: <id anterior> · Aparece en: <archivos> · Significancia`.

### 1.3 La marca `[!]`

`[!]` significa **inconsistencia detectada y deliberadamente no corregida**. Hay **85** en el modelo.
Buscar `[!]` en Archi las lista todas. No son errores de transcripción: son puntos donde dos fuentes
del proyecto dicen cosas distintas y nadie ha decidido cuál manda.

Un hueco declarado vale más que un hueco tapado con un supuesto plausible. Esa es la razón de que
existan.

---

## 2. Las capas de ArchiMate y qué significa cada una aquí

ArchiMate organiza todo en capas. En Archi son las carpetas de primer nivel del árbol.

| Capa | Pregunta que responde | Qué hay en FlorLogic |
|---|---|---|
| **Motivation** | ¿Por qué? | Dolores medidos, metas, atributos de calidad, requisitos, restricciones, brechas |
| **Business** | ¿Qué pasa en la finca? | Camas, variedades, ciclo, procesos, actores, roles, servicios |
| **Application** | ¿Qué software? | FlorLogic y sus módulos, los sistemas que ya existen, los datos |
| **Technology & Physical** | ¿Sobre qué corre y dónde? | Dispositivo móvil, red, invernadero, cuarto frío, estación meteorológica |
| **Implementation & Migration** | ¿Con qué se documenta? | Los archivos del proyecto y su vigencia |
| **Strategy** | ¿Con qué capacidades? | **Vacía a propósito.** No hay material para llenarla |

**La capa de tecnología ya se puede modelar** desde que `DEC-01` cerró el modelo de entrega:
plataforma SaaS multi-tenant, una base de datos por empresa, respaldos por empresa. Lo que sigue sin
decidir es el proveedor, la estrategia fina de aislamiento y el cifrado de los respaldos.

---

## 3. Los tipos de elemento que usa este modelo

Archi tiene ~60 tipos. Este modelo usa 20. Aquí está qué significa cada uno **en el contexto de
FlorLogic**, que es lo que la documentación oficial de ArchiMate no te va a decir.

### 3.1 Negocio

| Tipo | Forma en el diagrama | Qué es aquí | Ejemplos |
|---|---|---|---|
| **Business Object** | Rectángulo con línea arriba | Una **cosa** de la que el negocio guarda información | Cama, Variedad, Registro de siembra, Proyección |
| **Business Process** | Flecha ancha | Una **actividad** que alguien ejecuta. Se nombra en **infinitivo** | Sembrar, Cortar, Capturar dato en campo |
| **Business Event** | Flecha con muesca | Un **estado alcanzado**, no una actividad. Por eso «Botón color» es evento y no proceso | Botón color alcanzado, Erradicación registrada |
| **Business Actor** | Muñeco | Una **persona o grupo real** de la finca | Gustavo (Director de Producción), Supervisor de siembra |
| **Business Role** | Cilindro con línea | Un **rol del sistema**, no una persona | Supervisor de campo, Administrador de la empresa, Operador de la plataforma |
| **Business Service** | Óvalo | Lo que el sistema **le ofrece** a un rol, visto desde fuera | Captura de datos en campo, Control de exactitud |

> **Actor ≠ Rol, y aquí importa.** Nueve actores reales se reparten en **cuatro** roles del sistema
> —tres dentro de la empresa cliente, más el Operador de la plataforma, que es el equipo FlorLogic.
> Gustavo, el gerente de ventas, gerencia general y las vendedoras comparten el rol
> *Administrador de producción y ventas*. Confundirlos es lo que produce las peticiones de
> «un rol por tablero» que el equipo ya descartó en la sesión 3.

### 3.2 Aplicación y tecnología

| Tipo | Qué es aquí | Ejemplos |
|---|---|---|
| **Application Component** | Una pieza de software identificable | App móvil de campo, Motor de proyección, PowerBI |
| **Application Service** | Una capacidad que el software expone | Captura offline, Sincronización exactamente-una-vez |
| **Data Object** | Un dato que vive dentro del software | Registro pendiente de sincronizar, Catálogo descargado |
| **Device / Node** | Hardware o plataforma de ejecución | Dispositivo móvil de campo, Plataforma SaaS multi-tenant, Base de datos por empresa |
| **Facility / Equipment** | Instalación o máquina física, no informática | Invernadero, Cuarto frío, Estación meteorológica |
| **Communication Network** | El medio por el que viajan los datos | Conectividad intermitente en campo |

> **Por qué el cuarto frío está en la capa física y no entre los objetos de negocio.** Porque es
> una nevera, no un dato. Los objetos de negocio son cosas de las que se guarda información; los
> elementos físicos son cosas que existen. Es la distinción que evita que «Cama» y «Cuarto frío»
> terminen tratados igual.

### 3.3 Motivación

| Tipo | Qué es aquí | Ejemplos |
|---|---|---|
| **Driver** | Un **dolor con número**. Si no tiene cifra, no es driver | Latencia de 8 días, error del 2%, 8% de compra a terceros |
| **Goal** | Lo que el proyecto quiere lograr, y los 14 atributos de calidad | Llevar el error a 0%, Confiabilidad, Disponibilidad |
| **Assessment** | Una **pregunta sin responder** o una contradicción viva | BR-23, BR-N6, «Cifrado de los respaldos» |
| **Requirement** | Una capacidad exigible del sistema | RF-001 a RF-024 (catálogo vigente) y RFP-01 a RFP-08 (candidatos, sin validar) |
| **Constraint** | Un límite impuesto que no se negocia | CN-01 mayo 2027, CN-02 ~20.000 USD, CN-03 secreto empresarial |
| **Principle** | Una regla que el equipo se impone | «El asistente propone, el sistema valida, el usuario confirma» |

> **Requirement vs. Constraint.** El requisito dice *qué debe hacer el sistema*; la restricción dice
> *bajo qué límite hay que construirlo*. «Registrar la siembra sin conexión» es requisito.
> «Entregar en mayo de 2027 con 20.000 USD» es restricción. Mezclarlos es la razón por la que
> muchos documentos de requisitos son inservibles para estimar.

> **Assessment es el tipo más valioso de este modelo.** Cada brecha y cada contradicción está
> modelada como Assessment con una flecha de influencia hacia lo que bloquea. Eso convierte
> «faltan cosas» en «este requisito concreto no se puede escribir por esta razón concreta».
> Los marcados `CERRADO` ya se resolvieron pero conservan una advertencia viva.

### 3.4 Los atributos de calidad no tienen prioridad

Los catorce atributos existen en el modelo, pero **ninguno tiene prioridad válida hoy**. Su
documentación dice:

```
Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03).
```

Hubo cuatro rankings —del cliente en `0_CONTEXTO_v3` §6, del cliente en el mini QAW, del equipo de
arquitectos, y del mapa de empatía— y **no coincidían entre sí**. Interoperatividad aparecía en el
puesto 3 en una lista y en el 13 en otra. El equipo los descartó todos y está rehaciendo el
ejercicio enfocado al modelo SaaS.

**Consecuencia práctica:** no se pueden calcular trade-offs ni elegir los atributos para los
escenarios de calidad hasta que exista el ranking nuevo. Está registrado como el Assessment
*«Atributos de calidad pendientes de re-elaborar bajo SaaS»*.

Los rankings viejos siguen consultables en el mini QAW y en `0_CONTEXTO_v3` §6. No se reproducen en
el modelo a propósito: reproducir cuatro cifras que nadie considera válidas era ruido.

**Lo único que sobrevive de aquel ejercicio:** Confiabilidad salía primera en las cuatro listas. Es
el único consenso total que ha tenido el proyecto.

---

## 4. Cómo se leen las flechas

El tipo de flecha dice tanto como el elemento. Estas son las nueve que usa el modelo, de la más
fuerte a la más débil:

| Relación | Aspecto | Se lee | Ejemplo en el modelo |
|---|---|---|---|
| **Composición** | Rombo relleno | «está hecho de» — la parte no existe sin el todo | Bloque ◆— Nave ◆— Cama |
| **Agregación** | Rombo vacío | «agrupa» — la parte existe por su cuenta | Grado ◇— Calidad |
| **Asignación** | Línea con bola y punta | «lo ejecuta / está desplegado en» | Supervisor de campo ●→ Capturar dato en campo |
| **Realización** | Punteada, punta triangular vacía | «cumple / materializa» | RF-006 ⇢ Reducir la brecha |
| **Serving** | Continua, punta abierta | «le presta servicio a» | Captura offline → Capturar dato en campo |
| **Acceso** | Punteada, punta fina | «lee o escribe» | Cortar ⇢ Registro de producción |
| **Influencia** | Punteada, con `+/-` o etiqueta | «afecta a» — **solo en motivación** | BR-23 ⇢ *bloquea* ⇢ RF-006 |
| **Disparo** | Continua, punta rellena | «pasa antes que» | Desbotonar → Botón color alcanzado |
| **Asociación** | Línea simple | «tiene que ver con» — la más débil | Estación meteorológica — Datos climáticos |

**Las flechas etiquetadas `bloquea` son el núcleo del modelo.** Salen de un Assessment y llegan a un
requisito, una meta o un atributo. Si sigues esas flechas hacia atrás desde cualquier cosa que no
puedas escribir, encuentras la pregunta exacta que hay que hacerle al cliente.

Cuando la relación correcta no era obvia se usó **asociación**, que es válida entre cualquier par de
elementos. Es preferible una asociación honesta a una relación precisa e incorrecta.

---

## 5. Las dieciséis vistas y para qué sirve cada una

En Archi están en la carpeta **Views**. Cada una tiene su propia documentación explicando cómo leerla.
El orden no es decorativo: van de por qué existe el proyecto a qué se construye.

| # | Vista | Para qué la abres |
|---|---|---|
| 01 | **Motivación — de los dolores medidos a las metas** | Justificar el proyecto. Se lee de izquierda a derecha: quién lo dice → qué duele y cuánto → qué meta persigue |
| 02 | **Brechas abiertas y qué bloquean exactamente** | Preparar la próxima sesión con el cliente. Es la vista más accionable del modelo |
| 03 | **Atributos de calidad — todos sin prioridad definida** | Recordar que no hay ranking válido, y qué falta para tenerlo |
| 04 | **Restricciones de negocio y principios** | Estimar. Es lo que limita el diseño sin negociación posible |
| 05 | **Estructura de la información** | Diseñar el modelo de datos. La jerarquía completa |
| 06 | **Ciclo productivo y captura del dato** | Entender el dominio. Arriba lo que hace la planta, abajo lo que hace el sistema |
| 07 | **Actores reales y roles del sistema** | Discutir permisos. Nueve personas, cuatro roles |
| 08 | **Motor de proyección — la fórmula y sus dos huecos** | La conversación técnica más importante del proyecto |
| 09 | **Aplicación y entorno de sistemas** | Ver qué se construye y qué ya existe |
| 10 | **Alcance de la fase 1 — qué entra y qué no** | La vista de control |
| 11 | **Catálogo de requisitos funcionales** | Los requisitos vigentes |
| 12 | **Artefactos del proyecto y su vigencia** | Saber qué archivo manda sobre qué tema |
| 13 | **Decisiones cerradas y qué desbloqueó cada una** | Ver el efecto en cadena de DEC-01 a DEC-16 |
| 14 | **Modelo de siembra — la cama y sus secciones** | El cambio de modelo de datos más grande. Empieza por aquí si vas a diseñar la base |
| 15 | **Requisitos candidatos — lo propuesto y no validado** | Lo que hay que llevar a validar con el cliente |
| 16 | **Plataforma, aislamiento y continuidad** | Cómo se separan los datos de cada empresa y qué pasa cuando algo falla |

**Las vistas 02 y 10 son las que hay que revisar primero.** Una dice qué falta preguntar; la otra
dice qué entra y qué no. Todo lo demás es soporte.

**El layout es automático y feo a propósito.** Las cajas están en cuadrícula porque el objetivo de
esta versión es que el contenido esté completo y correcto, no que el diagrama sea bonito. Mover
cajas en Archi no cambia el modelo: acomódalas como quieras.

---

## 6. Lo que este modelo deja expuesto

Nada de esto es nuevo, pero puesto junto se ve distinto:

1. **La decisión SaaS vs. PaaS bloquea más de lo que parece.** No solo la vista de tecnología:
   también la existencia misma de `Empresa/Tenant` como raíz de la jerarquía, la prioridad de
   Seguridad y Escalabilidad, la partición del rol Administrador del sistema, el sentido de PayU,
   el presupuesto de mantenimiento y la validez de RF-012. **Doce elementos del modelo dependen de
   esa única decisión.**

2. **Hay tres numeraciones de requisitos conviviendo.** `RF-C01..RF-C20` (10-ago),
   `RF-001..RF-019` (12-ago) y `RF-001..FR-024` (13-ago). El archivo más reciente **omite RF-007,
   RF-010 y RF-015**, que sí están en el del 12-ago, y escribe «FR-» en lugar de «RF-» en los dos
   últimos. Además `RF-C19` (la vista de calidad de datos para el auditor) desapareció sin
   equivalente — y es justamente lo que hacía accionable la meta de llevar el error del 2% al 0%.

3. **RF-C04 y RF-022 se contradicen.** El primero exigía resolución **humana** de conflictos de
   sincronización, sin sobrescritura automática. El segundo manda orden cronológico estricto, que
   **sí** es automático. Son criterios opuestos y el cambio no está explicado en ninguna parte.

4. **El ranking de atributos del cliente existe en dos versiones incompatibles.** Y no se pueden
   calcular trade-offs sobre ninguna de las dos hasta decidir cuál manda.

5. **El motor tiene dos huecos, no uno.** La productividad esperada (no se sabe de dónde sale) y la
   curva de reparto sobre los ~7 días de corte (no existe). Ambos son `BR-23`.

6. **La restricción CN-02 (~20.000 USD) y el pitch SaaS (~10 USD/usuario/mes) son dos modelos
   económicos distintos** y nadie ha dicho cuál aplica.

---

## 7. Cómo trabajar con el modelo sin romperlo

### 7.1 Regla de oro

Un solo dueño del modelo a la vez. El `.archimate` es **un XML de un solo archivo**: si dos personas
lo editan en paralelo, el merge en git es inviable. La alternativa es instalar el plugin **coArchi**
(Archi → Help → Install New Software), que parte el modelo por elemento y hace merge real sobre git.
Mientras no esté instalado: avisa antes de tocarlo.

### 7.2 Al corregir algo

1. Cambia la **documentación** del elemento, no solo su nombre.
2. Si cambia el alcance, cambia la primera línea (`ALCANCE: ...`).
3. Si resuelves una inconsistencia, **quita la marca `[!]`** y escribe cómo se resolvió y quién lo
   decidió.
4. Si el cambio viene de una sesión con el cliente, actualiza `Origen` y `Confianza`.
5. Los identificadores **no se reutilizan**: si algo se invalida, se marca `OBSOLETO` y se conserva.

### 7.3 Al añadir algo

Antes de crear un elemento, búscalo en el árbol. Si ya existe con otro nombre, renombra en vez de
duplicar. La pregunta correcta no es «¿qué caja pongo?» sino «¿este concepto ya está en el modelo?».

### 7.4 Lo que sí se puede tocar sin pensarlo

Mover cajas, cambiar colores, reorganizar vistas, crear vistas nuevas arrastrando elementos
existentes. Nada de eso altera la información.

---

## 8. Archivos y qué hace cada uno

| Archivo | Qué es |
|---|---|
| `FlorLogic-modelo.archimate` | **El modelo.** 236 elementos, 348 relaciones, 16 vistas. Se abre con Archi |
| `FlorLogic-indice-alcance.md` | Espejo en texto del modelo, ordenado por alcance, con casillas para revisar |
| `FlorLogic-guia-de-lectura-archimate.md` | Este documento |
| `../../DECISIONES.md` | Las 16 decisiones cerradas y qué desbloqueó cada una |
| `../../RESUMEN_SISTEMA.md` | El estado vigente del sistema, sin histórico |

`FlorLogic-glosario.archimate` y `FlorLogic-core-negocio.archimate` se **archivaron** el 15-ago-2026
en `_to_delete/`. Sus elementos están dentro del modelo actual con los mismos identificadores.
Ver `LIMPIEZA.md`.

---

## 9. Lo mínimo para empezar

1. Descarga Archi de `archimatetool.com` (gratis, Windows/Mac/Linux).
2. `File → Open` → `docs/03-arquitectura/FlorLogic-modelo.archimate`.
3. Abre **Views → 10 · Alcance de la fase 1**. Es la vista que responde tu pregunta original.
4. Haz clic en cualquier caja y mira la pestaña **Properties → Main → Documentation**: ahí está el
   alcance, la trazabilidad y las inconsistencias.
5. `Ctrl+F` sobre `[!]` para ver las 78 cosas que nadie ha decidido todavía.
