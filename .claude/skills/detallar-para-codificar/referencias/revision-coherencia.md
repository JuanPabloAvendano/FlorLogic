# Revisión de coherencia, cohesión y atributos de calidad

Un segundo agente, aparte del verificador del índice. **Es obligatorio y no se salta**: corre al cerrar
cada ronda de preguntas (etapa 3) y en cada verificación de un segmento (etapa 6). Lo decidió Juan
(`DECISIONES.md` D-04).

El verificador del índice mide **cuánto** está especificado. Esta revisión mide **si las decisiones
forman un todo que funciona**, y si ese todo es confiable.

## 1 · Cómo se lanza

- Herramienta Agent, **contexto limpio**, en paralelo con el verificador del índice cuando los dos
  aplican. El agente es de solo lectura: no modifica ningún archivo.
- Le das: la ruta de la ESPEC, la plantilla de la ronda, este archivo, la documentación del dominio
  (ADR, escenarios, requisitos) y, en la etapa 6, los archivos del segmento.
- Le pides que siga §2 a §5 y que devuelva el informe de §6 en menos de 700 palabras.
- **Camino mínimo funcional** (D-17): le das el CMF de la ficha y le pides que marque cada hallazgo como
  **«rompe el CMF»** o **«después»** (con su segmento o §7). Solo los primeros deciden «pasa / no pasa».

## 2 · Coherencia: cada decisión con su fuente y con las demás

1. **Con la fuente.** Cada decisión que cita un ADR, un escenario o un requisito dice lo mismo que ese
   texto. Se relee la fuente; nunca se supone.
2. **Entre secciones.** Las decisiones (§2), las variables (§3), los casos (§4), los ejemplos (§4b) y el
   destino técnico (§5) dicen lo mismo. Un caso o un ejemplo que contradice una decisión es una
   incoherencia, aunque cada uno esté bien redactado por separado.
3. **Vocabulario.** Cada término central significa una sola cosa en toda la ESPEC y coincide con el del
   dominio. Busca restos de nombres viejos, sinónimos que se cruzan y redacciones que admiten dos
   lecturas (por ejemplo, «X es de un solo Y» leído como «X es Y»).
   Cruza cada término con su entrada en el diccionario del proyecto (`diccionario.md`): un uso con otro
   sentido, un término sin entrada o una pareja parecida sin «No es» es un hallazgo.
4. **Bordes.** Nada se sale de la definición acordada de la ficha. Lo que se sale se propone como nota
   para otra ronda, no como decisión.

## 3 · Cohesión entre decisiones (pasada extra, obligatoria)

La coherencia mira cada decisión por separado; la cohesión mira **cómo se combinan**.

1. **Mapa de conceptos.** Agrupa las decisiones por el concepto que tocan: identidad, tiempo, orden,
   idempotencia, retención, errores, versiones, seguridad, límites y lo que sea propio del elemento.
2. **Pares e interacciones.** Dentro de cada grupo, y entre grupos que se tocan, pregúntate: «si se
   cumplen A y B a la vez, ¿qué pasa en el caso límite?». Busca:
   - combinaciones que producen un comportamiento que nadie decidió;
   - decisiones que dependen de algo que no está definido;
   - dos decisiones que resuelven lo mismo de formas distintas (redundancia que puede divergir);
   - decisiones huérfanas, que no sirven a ningún propósito ni caso de la ESPEC;
   - efectos en cascada: si una decisión cambió en esta ronda, ¿quedó alguna otra apoyada en la versión
     anterior?
3. **Recorrido de punta a punta.** Sigue al menos tres recorridos completos: el feliz, uno con fallo a
   mitad y uno de reintento o recuperación. En cada paso, comprueba que las decisiones que aplican se
   encadenan sin huecos.

## 4 · Atributos de calidad, en este orden de prioridad

1. **Confiabilidad** (la principal): no se pierde ni se corrompe un dato, el resultado es determinista,
   el reintento es seguro, cada fallo tiene un camino de recuperación, y el sistema detecta y dice lo que
   no pudo hacer. Pregunta por cada fallo posible: ¿dónde queda el dato?, ¿quién se entera?, ¿cómo se
   recupera?
2. **Disponibilidad:** qué deja de funcionar cuando falla una pieza, si se degrada de forma controlada,
   si hay un punto único de fallo o un bloqueo que deja a otros esperando, y si los tiempos de espera y
   los reintentos evitan las cascadas.
3. **Rendimiento:** latencia y capacidad frente a las metas de los escenarios, la concurrencia, la
   contención y los cuellos de botella.
4. **Eficiencia:** el uso de CPU, memoria, red, almacenamiento y batería, y el trabajo repetido o
   innecesario.

Reglas de esta sección:
- Cada hallazgo cita el escenario o la meta que afecta. Sin una meta, se marca como inferencia.
- Toda propuesta de optimización trae su **costo**: qué atributo empeora y cuánto.
- Si dos atributos chocan, **gana la confiabilidad**, salvo que el usuario ya haya decidido otra cosa en
  la ESPEC. En ese caso no se reabre la decisión: se anota el riesgo.
- No se proponen piezas ni tecnologías nuevas sin decir que necesitan la aprobación del proyecto.

## 5 · En la etapa 6, además

- El código hace lo que dice la ESPEC y nada más. Cada decisión implícita del código está en la ESPEC.
- Las decisiones de confiabilidad tienen su prueba: idempotencia, reintentos, límites, tiempos de espera,
  transacciones y orden.
- Revisa recursos sin cerrar, esperas sin tiempo límite, reintentos sin tope, estados compartidos sin
  protección y errores que se tragan en silencio.

## 6 · Informe

```
COH-<n> · <coherencia | cohesión | confiabilidad | disponibilidad | rendimiento | eficiencia>
          (<blocking | mayor | menor>)
  Decisiones: <ids>
  Escenario: <dado / cuando / entonces que lo muestra>
  Fuente o meta: <id | inferencia>
  Propuesta: <qué cambiar> · Costo: <qué empeora>
```

Al final del informe:
- un veredicto: **pasa** solo si no queda ningún `blocking` abierto;
- los tres hallazgos de confiabilidad más importantes, aunque no sean bloqueantes.

## 7 · Qué hace el agente principal con el informe

- Copia los hallazgos a la ESPEC: en la etapa 3, a §6 (contradicciones y pendientes) con id `COH-n`; en
  la etapa 6, a §10 (observaciones) con la etiqueta y la severidad de `verificacion.md` §3.
- **Un `blocking` abierto impide pasar**: en la etapa 3 no hay `LISTA PARA CÓDIGO`, y en la etapa 6 no
  hay aceptación. Solo el usuario puede descartar un hallazgo bloqueante.
- Los hallazgos que piden decidir algo se convierten en preguntas de la ronda siguiente, con la forma
  normal de título y contexto.
