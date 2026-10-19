# Informe · Por qué el detallamiento de TX-1.K no pasa del 60 %

> REGISTRO DE IA · NO ES FUENTE. Lo generó Claude el 16-oct-2026 a pedido de Juan, con 4 agentes en paralelo: conversación en vivo, documento, métrica y proceso, y fidelidad de la captura.
> Estado: SIN REVISAR. Por encima mandan `DECISIONES.md` y las decisiones de Juan.
> Material revisado: las 4 transcripciones de la sesión, `ESPEC-contrato-ingesta.md`, `-HISTORIA.md`, `SKILL.md` y `referencias/`.

---

## 0 · En una frase

El contrato no está corto de detalle. Lo que pasa es que el contrato está absorbiendo problemas que son de otros elementos, y la métrica los cobra todos en un solo componente. Sin ese componente, la ESPEC ya estaría en **≈ 86 %**.

## 1 · ¿Se está documentando todo lo que sale en las preguntas?

**Casi todo, y las fugas tienen patrón.** Se muestrearon 30 respuestas, sobre todo las de texto libre:

| Resultado | Cuántas |
|---|---|
| Fieles | 24 (80 %) |
| Alteradas o inferidas | 4 |
| Solo en la HISTORIA | 1 |
| Perdidas | 1 |

Dónde se fuga:

1. **Una nota sin opción elegida se vuelve una aprobación que Juan no dio.**
   - K-27: la nota explicaba para qué sirve una marca, pero A-23 agregó dos códigos nuevos.
   - COH-31: A-50 agregó «la ventana no corre», que Juan no dijo, y eso es justo lo que hoy choca (K-54, COH-40).
   - A-51: «si es necesario… podría darse» quedó escrito como obligatorio.
2. **La consolidación cambió sentidos.** Cambió el sentido de 7 frases (K-61..K-66), que después se restituyeron. También dejó fuera del §7 vigente «las circunstancias en que falta un dato» y el aviso «parte 1 de 2».
3. **Hay temas sin hogar.** Ningún documento con dueño recoge:
   - el mínimo de captura **personalizable** por actividad (perdido);
   - la orden de captura y la reasignación por turnos;
   - la reinstalación del dispositivo (`registrar_de_nuevo`);
   - las circunstancias en que falta un dato.
4. **No hay rastro de pregunta → respuesta.** `SKILL.md` prohíbe guardar el historial dentro de la ESPEC, así que Juan no tiene cómo comprobar que lo que dijo quedó escrito.

**Lo que falta del propio contrato:**
- JSON Schema de la respuesta: tipos de `conteo` y enums de `resultado` y `accion`.
- Los errores `400` y `415`.
- El catálogo de `errores[].regla`.
- Qué pasa con el mismo `particion_uuid` y otro cuerpo.
- Los **otros 3 POST**: la ficha promete 4 y solo se detalla `/particiones`.

## 2 · Por qué el índice no sube

Índice = 0,35·Cobertura + 0,25·Contradicciones + 0,25·Prácticas + 0,15·Trazabilidad.

| Ronda | Cob | Contr | Práct | Traz | Índice |
|---|---|---|---|---|---|
| 1c | 76 | 35 | 86 | 72 | 68 |
| 1d | 77 | **0** | 86 | 76 | 60 |
| 1e | 77 | **0** | 86 | 80 | 60 |
| 1f | 78 | **5** | 86 | 82 | 62 |

- **Todo el atasco está en Contradicciones.** Ese componente resta en valor absoluto, sin denominador ni piso. Una sola contradicción directa lo deja en 0.
- **Hay un techo de ≈ 75 %.** K-40, K-41, K-53, K-28 y K-59 restan unos 45 puntos que **solo Juan puede quitar**, editando ADR. Ninguna ronda de preguntas los cierra.
- **Lo que está fuera del borde se cobra.** Una decisión fuera del borde cuenta como trazada gratis, pero sus choques sí restan. D-07 lo corrigió en Trazabilidad, pero no en Contradicciones.

## 3 · Las causas, ordenadas por impacto

| # | Causa | Evidencia |
|---|---|---|
| 1 | **El alcance se infló** | 22 de 50 decisiones son comportamiento del nodo o del celular, y 2 son de TX-2. Entre ≈ 85 y 90 % de los K/COH abiertos no son del contrato. Temas que dominaron desde la 1c: borrado y lápidas (33 preguntas), respaldo (23), retención (18), restauración (14), obsoleta (10) |
| 2 | **El agente de coherencia no tiene presupuesto ni borde** | Abre 8–11 COH por ronda, de 1 a 3 bloqueantes, y siempre con los mismos 3 recorridos, uno de ellos la restauración del nodo. Sus «mejoras» se cobran como contradicciones. COH-36 bloquea el contrato por un tema que la propia ESPEC manda a caso-de-uso |
| 3 | **Cerrar un hallazgo abre otros en cascada** | Unos 1,5 problemas nuevos por cada pregunta respondida. K pasó de 12 a 60 y COH de 9 a 44. COH-18 generó K-32, K-33 y K-45. Las retiradas (A-4, A-25, A-28) dejaron referencias huérfanas (K-44, K-45) |
| 4 | **Las decisiones son compuestas** | A-31 mezcla 7 reglas, A-32 unas 6 y A-49 unas 5. A-45 se reescribió en 8 preguntas y A-31 en 7. Un cambio dispara 4 o 5 K/COH |
| 5 | **Las preguntas son largas y con jerga** | Pasaron de ~12 a ~164 palabras de media; la más larga tiene 202. Hubo 12 pedidos de aclaración y 8 preguntas solo para fijar un término. Juan dijo «No entiendo esta pregunta» |
| 6 | **Las rondas no tienen tope** | 6 rondas y ~117 preguntas sobre una plantilla pensada para 2 rondas de 10. Desde la 1b, los cupos se van en reparar cascadas |
| 7 | **El verificador no tiene una rúbrica estable** | Empieza en frío cada ronda, sin tabla COH → resta, sin deduplicar (COH-39 = K-60) y sin línea base: pasó de 68 a 60 sin que la ESPEC empeorara. El estimado de P5 no ve las cascadas, por eso sale optimista |
| 8 | **La skill cambió durante el proceso** | 4 recargas de la skill mientras se respondía; el agente de coherencia entró después de la 1b |

Lo que **no** es el problema: el tiempo de Claude entre preguntas (0,3–0,5 min) y la captura de las decisiones explícitas (80 % fieles). Se pierde tiempo en aclaraciones (2–14 min cada una) y en los cierres de ronda (4–10 min).

## 4 · Qué hacer

### 4.1 En la ESPEC de ahora (lo decide Juan)

1. **Partir la ESPEC en 4.** Contrato estimado en **≈ 83–85 % recortado**; una ronda corta de 4–5 puntos lo deja en ≥ 85.

   | ESPEC | Plantilla | Qué lleva |
   |---|---|---|
   | `ESPEC-contrato-ingesta` | endpoint | Solo la forma: las 26 decisiones de contrato, tipos, ejemplos, catálogo de códigos como lista de nombres, huella y versión |
   | `ESPEC-ingreso-tx1` | caso-de-uso | Idempotencia, juicio, marcas, obsoleta, superada y vieja, candados y plazos |
   | `ESPEC-celular-sincronizacion` | simulador | Reintentos, lápidas y borrado, edición local, retención y avisos, credencial offline |
   | `ESPEC-marcas-y-restauracion` | con TX-2 | A-31, A-50, COH-40 y K-28 |

2. **Una sesión de ADR antes de la próxima ronda.** Sirve para resolver o aplazar K-40 (ADR-027 §2 / ESC-34), K-41 (ADR-027), K-28 (ADR-028 §3), K-53 (ADR-029) y K-59 (ADR-007). Con la partición, la mayoría sale del contrato sin más.
3. **Confirmar las 3 inferencias:** A-23 (los dos códigos), A-50 («la ventana no corre») y A-51 (renovación obligatoria).
4. **Dar hogar a lo huérfano:** mínimo personalizable por actividad, orden de captura y turnos, reinstalación del dispositivo y circunstancias de falta de dato.
5. **Ficha honesta:** «parte 1 de 4 POST».

### 4.2 En la skill (D-candidatas, sin aplicar)

| Candidata | Cambio | Ataca la causa |
|---|---|---|
| D-09 | **Contradicciones solo dentro del borde.** Lo que depende de un ADR o de Juan va a una lista «Pendiente de Juan» que no resta. Se deduplica por causa raíz y hay un piso cuando no queda ninguna directa. La «puerta» (ninguna directa abierta) va separada del índice | 1, 7 |
| D-10 | **Coherencia con presupuesto:** máximo 5 hallazgos por ronda, solo bloqueantes y mayores dentro del borde. Las mejoras van a «Mejoras propuestas», que no puntúa. Lo de fuera del borde va a §7 | 2 |
| D-11 | **Rúbrica fija del verificador:** tabla COH → resta y el informe anterior como línea base; si cambia más de 5 puntos, lo justifica. El estimado de P5 descuenta la cascada esperada | 7 |
| D-12 | **Tope de rondas:** 2 por plantilla y 1 de cierre. Después Juan elige: partir, aceptar pendientes con dueño o seguir | 6 |
| D-13 | **Corte vertical:** se puede codificar un corte delgado con el núcleo ⟦1⟧-⟦6⟧ cubierto y sin contradicción directa dentro del borde. Las pruebas cierran K/COH. El 85 % pasa a ser para aceptar la ESPEC entera | 3, 6 |
| D-14 | **Registro de respuestas:** `ESPEC-…-RESPUESTAS.md`, una fila por pregunta con la cita de Juan, la opción, dónde quedó y si es fiel, inferida o pendiente | captura |
| D-15 | **Nota sin opción = no aprobada:** lo deducido de una nota entra como `supuesto` y se confirma en la ronda siguiente | captura |
| D-16 | **Consolidación con diferencia obligatoria:** comparar cada A-n antes y después con un agente; el §7 nunca pierde viñetas sin marcarlas como movidas | captura |
| D-17 | **Decisiones atómicas:** una regla verificable por fila con su caso C-n. Tras retirar o cambiar una decisión, limpiar sus referencias antes del verificador | 3, 4 |
| D-18 | **Preguntas cortas:** contexto de ~50–90 palabras, máximo un término nuevo por pregunta y escenarios de la finca en vez de cadenas de A-n/K-n/COH-n | 5 |
| D-19 | **Congelar la skill durante un detallamiento:** los cambios se aplican al empezar la ronda siguiente | 8 |

## 5 · Orden sugerido

1. Juan decide si parte la ESPEC (§4.1 punto 1). Es lo que más rinde.
2. Sesión de ADR con los 5 K de Juan.
3. Aplicar a la skill D-09, D-10, D-14 y D-15 antes de la siguiente ronda: son las de más impacto con menos cambio.
4. Una ronda corta del contrato recortado para llegar a ≥ 85 %.
5. Después, D-11, D-12, D-13 y las demás.
