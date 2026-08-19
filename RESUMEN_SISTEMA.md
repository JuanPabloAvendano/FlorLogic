# FlorLogic — Resumen del sistema

**Estado al 15-ago-2026, después de las tandas 1 y 2 de depuración.**
Este documento resume **solo lo que está vigente**. Lo que se descartó, se contradijo o quedó
histórico no aparece aquí — vive en `DECISIONES.md` y en el modelo ArchiMate.

---

## 1. Qué es FlorLogic, en una frase

Un sistema que **proyecta cuánta flor va a haber, de qué variedad, de qué grado y en qué fecha**,
a partir de datos capturados en el campo por los supervisores, sin depender de que haya internet.

Lo que **no** es: no maneja dinero, no cruza con pedidos ni clientes, y no reemplaza los sistemas
que la finca ya tiene.

---

## 2. Qué se decidió y ya no se discute

### El producto

| Decisión | Qué significa |
|---|---|
| **SaaS multi-tenant** | Un solo producto que sirve a varias empresas. Lo opera el equipo FlorLogic |
| **Piloto primero** | La primera entrega hace funcionar el sistema en **una** finca. El multi-tenant se construye desde el diseño, pero se valida con un cliente |
| **Empresa es la raíz** | Empresa → Fincas → Bloques → Naves → Camas → Secciones. Todo dato cuelga de una empresa |
| **Aislamiento total** | Ningún usuario puede ver datos de otra empresa. Los clientes son fincas que compiten entre sí |
| **Una base de datos por empresa** | Mismo esquema para todas, base independiente para cada una |

### El dominio

**Cantidades, unidades y calidades. Nada de dinero.** La proyección es del **estado resultante de la
producción del cultivo**, no del rendimiento económico. Precios, unidades comerciales y todo lo que
sea información de ventas queda fuera.

Consecuencia práctica: **dentro de una empresa no hay ningún dato restringido por rol.** El control de
acceso decide *quién puede hacer qué* (capturar, corregir, parametrizar), no *quién puede ver qué*.
La única frontera de visibilidad real es la de empresa.

### La unidad de medida

**La cama es la unidad de inventario. Pero el dato de siembra vive en la SECCIÓN.**

Una cama se divide en secciones, y cada sección tiene su propia área en m², su propia variedad y su
propia densidad de siembra. Ejemplo real: una cama con 30% pompón morado y 70% violeta azulada,
porque 6 m² se cultivaron de violeta azulada a 6 tallos/m².

**Nada se cuenta por esqueje.** Nadie cuenta esquejes en el campo, así que el sistema no lo pide:

```
plantas_sección = área_sección (m²) × densidad de siembra de esa variedad
plantas_cama    = suma de todas sus secciones
tallos_proyectados = plantas × productividad esperada de la variedad
```

Cota dura que dio el cliente: **tallos ≤ plantas sembradas, siempre.** Si el sistema arroja más, el
dato es malo y se rechaza.

### Cómo se muestra la producción

Dos lecturas de lo mismo, al mismo tiempo:

1. **Tallos aproximados** — el resultado del motor.
2. **Porcentaje de plantas reales estimadas** sobre el 100% de lo que se sembró en esa cama.

La segunda existe porque el número de plantas es un aproximado: el porcentaje no obliga a confiar en
un conteo que nadie hizo. Sobre ese porcentaje se dibuja una **vista geométrica**: las camas como
rectángulos que muestran cuánta producción sigue viva.

### La IA, partida en dos

| Pieza | Dónde corre | Qué hace |
|---|---|---|
| **Asistente de captura** | En el celular, **sin conexión** | Ayuda a llenar el formulario y avisa qué campos faltan |
| **IA analítica** | En la nube | Consultas, análisis y apoyo a los tableros |

Regla que no se rompe: **el asistente propone, el sistema valida, el usuario confirma.** Nunca escribe
en silencio.

### Los tableros

BI **propio y cerrado**, no integración con herramientas de terceros. Como línea base, los seis
reportes que la finca ya consume hoy: siembra, producción, plagas y enfermedades, inventario de
material vegetal, pérdida de flor y estimados de flor.

La app de plagas que la finca ya tiene **no se reemplaza ni se consume**. Lo único que entra es poder
indicar **enfermedad como motivo** de que la producción bajó.

---

## 3. Quién usa el sistema

| Rol | Qué hace |
|---|---|
| **Supervisor de campo** | El supervisor y los 2 auxiliares. Capturan siembra, corte, bajas y erradicaciones en el celular, sin conexión |
| **Administrador de producción** | Gerencia, planeación, ventas y producción. Consultan proyecciones, informes y tableros de todos los bloques |
| **Administrador de la empresa** | El ingeniero de sistemas de la finca. Parametriza, otorga permisos, autoriza correcciones |
| **Operador de la plataforma** | El equipo FlorLogic. Opera la infraestructura: respaldos, disponibilidad, despliegues. **No accede a los datos de negocio de un cliente** |

Los dos últimos salieron de partir en dos el antiguo «Administrador del sistema» cuando se decidió
el modelo SaaS.

Solo **3 personas** capturan datos. Unas 12 usarían el sistema activamente y ~20 más solo consultan.

---

## 4. Los números que ya están medidos

Son pocos, y por eso valen. Todo lo demás es cualitativo.

### El dolor actual

| Qué | Cuánto |
|---|---|
| Supervisor llenando formatos a mano | 1 hora/día |
| Practicante digitando | 4 horas/semana |
| Desde que pasa el hecho hasta que gerencia lo ve | **8 días** |
| Error de captura declarado | **2%**, y no está visualizado en ninguna parte |
| Ventas cubiertas comprando a terceros o cancelando | **8%** |
| Frecuencia de ajuste de la proyección | Mensual. Se quiere **semanal** |
| Pico de temporada | +60% en tallos y registros, +30-40% en personal |

### Continuidad — lo que el sistema debe garantizar

| Situación | Tolerancia |
|---|---|
| **Pérdida de información** | **Cero.** Sin excepción |
| **Fallo de funcionamiento** (no se puede operar, no sincroniza) | **1 hora** |
| **Reparación o carga de respaldo** | **1 día** |
| **Desajuste de datos** (las proyecciones no cuadran con lo real) | Mayor, pero sin pérdida |

El trabajo sin conexión es lo que hace realista la hora: si la plataforma se cae, la captura en campo
sigue funcionando en el celular y sincroniza después. Una caída se convierte en retraso, no en parada.

### Restricciones del proyecto

| Tipo | Cuál |
|---|---|
| **Tiempo** | Implementación desde **mayo de 2027**, después de la temporada alta de marzo-abril |
| **Tiempo** | El despliegue no puede retrasar la operación más de **7 días** |
| **Presupuesto** | ~**20.000 USD** (~61 M COP) para construir y poner en marcha |
| **Legal** | Secreto empresarial — art. 260, Decisión 486 de la CAN |
| **Humano** | Resistencia al cambio de los supervisores. Crítica para el éxito |
| **Humano** | Decisiones de arquitectura tomadas por ingenieros sin experiencia en el sector |
| **Proceso** | El proceso a apalancar apenas se está definiendo |

---

## 5. Qué entra y qué no

### Entra en la fase 1

- Captura en campo de siembra, corte, bajas y erradicaciones, **sin conexión**
- Sincronización sin perder ni duplicar registros
- Validación de reglas en el propio dispositivo
- Motor de proyección por cama, variedad y fecha
- Regeneración semanal de la proyección, conservando versiones
- Control de desviación entre lo proyectado y lo cortado
- Parametrización por empresa: variedades, densidades, grados, días a corte, bandas
- Trazabilidad completa hasta la cama de origen
- Aislamiento total entre empresas
- Exportación a Excel y PDF
- Tableros propios

### No entra

- **Precios, ventas y rendimiento económico**
- **Cruce con pedidos y clientes concretos** — el sistema muestra qué flor va a haber, no a quién está prometida
- **Certificación Florverde** — excluida por el cliente
- **Gestión de personal e insumos** — excluida por el cliente
- **Poscosecha** — el alcance termina en el corte
- **Registro obligatorio de actividades culturales** — se puede habilitar, hoy no se registran
- **Datos climáticos** — se registran en la finca pero no se usan
- **Integración con PowerBI o con la app de plagas**
- **Plantillas de captura configurables** — es la visión, no la primera entrega

---

## 6. Lo que todavía no se sabe

### Depende del cliente

| Qué falta | Qué bloquea |
|---|---|
| **Los documentos prometidos** — plan de siembra, presupuestos, formatos llenos, tabla de grados, histórico | Todo |
| **A qué nivel se miden el −6% actual y el ±10% objetivo** | La métrica de éxito del proyecto |
| **De dónde sale el % de productividad** y cómo se reparten los tallos en los ~7 días de corte | El motor de proyección |
| **Qué sistema usan hoy** — nombre, alcance, qué guarda | Si FlorLogic reemplaza, alimenta o convive |
| **¿9 variedades o ~300?** | El dimensionamiento del catálogo |
| **La tabla de grados y calidades de la finca** | Con qué detalle se registra el corte |
| **Cuánto puede tardar la confirmación de un registro** | Que «que sea fácil» sea verificable |
| **Si dos personas pueden capturar la misma cama el mismo día** | Cuánta complejidad hace falta en la sincronización |

**Y sobre todo: el proceso de captura a detalle nunca se ha trabajado con el cliente.** No es un dato
que falte, es una sesión de trabajo que falta. Mientras no exista, los requisitos de captura siguen
escritos sobre un modelo que ya se invalidó.

### Depende del equipo

- El ranking de atributos de calidad, ahora enfocado a SaaS. Sin él no hay escenarios de calidad.
- El cifrado de los respaldos, que decide si el aislamiento es una promesa o una propiedad.
- El alcance de la IA analítica: «proponer estrategias» hoy no es estimable.
- El modelo de suscripción: precio, unidad de cobro, qué incluye.

---

## 7. El riesgo que se asume a conciencia

Todo el conocimiento del negocio viene de **una sola persona**. Tiene 30 años de experiencia en
exactamente el proceso que el sistema va a apoyar, y conoce a fondo dos empresas cuyas prácticas son
las que sigue el resto del sector. Eso hace la fuente mucho más sólida de lo que suena «una sola voz».

Pero sigue siendo un solo punto de vista. **Planeación —que es donde nace la proyección— nunca se
exploró**, y el cliente descartó entrevistar a otras áreas. Y hay un efecto que no se arregla con más
horas de entrevista: un experto de treinta años omite lo que para él es evidente.

Por eso las dos mitigaciones reales no son «preguntar más», sino:

1. **Contrastar contra documentos físicos reales** — un formato lleno responde veinte preguntas que
   nunca se hicieron.
2. **Validar con un prototipo** sobre datos simulados, antes de comprometer esfuerzo.

---

## 8. Dónde está cada cosa

| Archivo | Qué contiene |
|---|---|
| `DECISIONES.md` | Las 16 decisiones cerradas, qué desbloqueó cada una y qué dejó abierto |
| `docs/03-arquitectura/FlorLogic-modelo.archimate` | El modelo completo: 253 elementos, 16 vistas |
| `docs/03-arquitectura/FlorLogic-indice-alcance.md` | Todos los elementos ordenados por alcance, para revisar |
| `docs/03-arquitectura/FlorLogic-guia-de-lectura-archimate.md` | Cómo interpretar el modelo |
| `Documentacion/FuncionalidadesSignificativas.xlsx` | El catálogo de requisitos vigente |
| `0_CONTEXTO_v3.md` | El levantamiento previo. Sigue siendo la fuente de los hechos H-nn |
