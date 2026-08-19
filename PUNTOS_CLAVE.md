# FlorLogic — Los puntos que hay que abordar

**15-ago-2026 · Juan Pablo y Jerónimo**

---

## Dónde estamos

El proyecto **ya sabe qué es**: un SaaS que proyecta cuánta flor va a haber, por cama, variedad,
grado y fecha, con captura en campo sin internet.

Lo que falta no es idea. Es **cerrar el proceso real con el cliente** y **completar el motor**.

---

## Ya está decidido — no se rediscute

| Decisión | En una línea |
|---|---|
| **SaaS multi-tenant** | Un producto para varias fincas, operado por nosotros |
| **Piloto primero** | La primera entrega funciona en **una** finca. Mayo 2027 |
| **Sin dinero** | Cantidades, unidades y calidades. Nada de precios ni ventas |
| **La cama se divide en secciones** | Cada sección con su área, variedad y densidad |
| **Nada se cuenta por esqueje** | Plantas = área × densidad |
| **Una base de datos por empresa** | Mismo esquema, base independiente |
| **BI propio** | No nos integramos con PowerBI ni con la app de plagas |
| **La IA vuelve, partida en dos** | Asistente de captura offline + analítica en la nube |

---

## Los 6 puntos a abordar

### 1 · El proceso real nunca se trabajó con el cliente

**Es el punto más importante de todos.** Nunca nos sentamos a definir cómo funciona el sistema a
detalle: qué se captura, en qué orden, qué es obligatorio, cómo se identifica una cama.

Por eso `RF-001` y `RF-002` **siguen escritos sobre un modelo que ya invalidamos** — dicen
"cantidad de esquejes por cama" y eso ya no existe.

> **Qué hacer:** una sesión siguiendo **una cama real** de principio a fin, con los formatos de
> papel encima de la mesa. Ya está el guion: 12 preguntas, una por etapa.

---

### 2 · El motor de proyección está incompleto

Nos faltan **dos números** y sin ellos el producto no proyecta:

- **De dónde sale el % de productividad** de cada variedad. Usamos 90% en los ejemplos, pero nadie
  sabe de dónde salió ese número.
- **Cómo se reparten los tallos** en los ~7 días que dura el corte. Hoy la fórmula da un total por
  ciclo; el gerente de producción necesita el dato **por día**.

> **Qué hacer:** preguntarlo en la sesión. La pregunta clave es *"si de una cama salen 900 tallos en
> 7 días, ¿salen 130 cada día o el tercer día salen 300?"*

---

### 3 · No sabemos cómo demostrar que el sistema sirve

El cliente dijo dos cosas que no encajan: que hoy están en **−6%** de desviación, y que la
tolerancia es **±10%**.

Si el ±10% fuera nuestra meta, estaríamos prometiendo **algo peor de lo que ya logran**.

Lo más probable es que sean niveles distintos —el −6% de toda la finca al año, el ±10% de una cama
por semana— pero **hasta confirmarlo no tenemos métrica de éxito**.

> **Qué hacer:** preguntar a qué nivel se mide cada uno. Y decirlo en voz alta en la reunión.

---

### 4 · No hay atributos de calidad válidos

Hicimos cuatro rankings distintos y **no coinciden entre sí**. Interoperatividad salía 3ª en una
lista y 13ª en otra. Los descartamos todos.

Consecuencia: **no podemos escribir los escenarios de calidad** hasta rehacer el ejercicio, ahora
enfocado a SaaS.

> **Qué hacer:** terminar el ranking nuevo. Ya tenemos los primeros números medidos para apoyarlo:
> pérdida de datos cero, 1 hora de tolerancia a fallo, 1 día para restaurar.

---

### 5 · El cambio de modelo de datos que todavía no bajamos a requisitos

Decidimos que la **sección de cama** es donde vive el dato de siembra, no la cama. Una cama puede
ser 30% pompón morado y 70% violeta azulada.

Eso cambia la fórmula de nivel:

```
plantas por sección = área de la sección × densidad de esa variedad
plantas por cama    = suma de sus secciones
```

Hay **8 requisitos candidatos** escritos para cubrir esto, pero ninguno está validado con el cliente.

> **Qué hacer:** confirmar en la sesión que las camas divididas son algo real y frecuente. Si son
> raras, el modelo se simplifica mucho.

---

### 6 · Dos decisiones técnicas que dependen solo de nosotros

**El cifrado de los respaldos.** Si la clave es única nuestra, podemos restaurarle al cliente al
instante — pero "no accedemos a sus datos" queda como promesa, no como hecho. Si la clave es del
cliente, el aislamiento es real pero él tiene que participar en cada restauración. **No hay opción
gratis**, y esto va también al contrato.

**El alcance de la IA analítica.** El cliente quiere que "proponga estrategias". Eso puede ser
cualquier cosa y hoy no se puede estimar. Hay que acotarlo antes de prometerlo.

---

## El bloqueo que detiene todo

**Los documentos que el cliente prometió y no ha entregado:** plan de siembra, presupuestos,
formatos en papel llenos, tabla de grados, histórico de producción.

Un formato lleno contesta veinte preguntas que nunca hicimos. Sin ellos, la mitad de lo anterior
sigue en suposiciones.

---

## El riesgo que asumimos a conciencia

Todo el conocimiento viene de **una sola persona**. Tiene 30 años en exactamente este proceso y
conoce dos empresas cuyas prácticas sigue el sector, así que la fuente es sólida.

Pero un experto de treinta años **omite lo que para él es evidente**. Por eso la mitigación no es
preguntarle más, sino **contrastar contra documentos reales y validar con un prototipo**.

---

## Próximo paso

**Una sesión con el cliente**, con dos cosas encima de la mesa: los formatos de papel de una cama
real, y las 12 preguntas de `HOJA_SESION_CLIENTE.md`.

De ahí salen los puntos 1, 2, 3 y 5. Los puntos 4 y 6 los cerramos nosotros.
