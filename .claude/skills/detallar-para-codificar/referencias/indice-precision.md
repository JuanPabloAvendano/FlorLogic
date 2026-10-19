# Índices de DetallarParaCodificar

Hay tres umbrales:

| Índice | Umbral | Qué habilita |
|---|---|---|
| Precisión | ≥85 % | Escribir código |
| Completitud | ≥95 % | Verificar |
| Explicación | ≥80 % | Aceptar |

Los índices oficiales los calcula **un agente verificador aparte**, con contexto limpio: lee el ESPEC y la
documentación. El agente que pregunta muestra un **estimado a mitad de cada ronda de 10 (pregunta 5)**, en una línea de la ficha
(«precisión ≈ 62 %, falta: errores e idempotencia»).

## 1 · Precisión

**Precisión = 0,35·Cobertura + 0,25·Contradicciones + 0,25·Prácticas + 0,15·Trazabilidad**, cada
componente de 0 a 100. Fuentes: `investigacion/04` §5 y `investigacion/02` §3.

**Cobertura (35).** Se mide sobre los elementos prioritarios (§1 de la plantilla). Con *n* elementos, el
de la posición *k* pesa `n − k + 1`. Valor de cada elemento:

| Estado | Valor |
|---|---|
| Escrito o ajustado por el usuario; si es una regla, con su par cumple/viola | 1,0 |
| Eligió la opción ★ · respondido sin casos · confirmado contra el código o un ADR | 0,8 |
| Aceptado en bloque (esqueleto entero sin discutir) | 0,5 |
| Pendiente con supuesto provisional | 0,25 |
| Sin tocar | 0 |
| No aplica, con justificación | 1,0 |

`Cobertura = 100·Σ(w·valor)/Σw`. Cada regla sin su par de ejemplos resta 3, hasta −15.
**Tope:** si alguno de los 3 primeros elementos queda en 0 o 0,25, la cobertura no pasa de 70.

**Contradicciones (25).** Empieza en 100 y resta por cada choque abierto:

| Tipo de choque | Resta |
|---|---|
| Directo, entre dos afirmaciones incompatibles | −25 y **bloquea** el umbral |
| Un caso que viola su propia regla | −20 |
| Con un elemento vecino | −15 |
| Con el código o un ADR | −15 |
| Variable fuera de rango o sin unidad | −10 |
| Ambigüedad | −5 |

Un caso de segunda vuelta que no bloquea ningún escenario del MVP no es un choque abierto: va a §7 como riesgo y no resta (D-14).

**Prácticas (25).** Se evalúan las 26 prácticas de `investigacion/02` §3 más las de la §4 de la
plantilla. Peso: crítica 3, mayor 2, menor 1. Valor: cumple 1, parcial 0,5, no cumple 0. Las que no
aplican, con justificación, se excluyen.
- **Tope:** una crítica en 0 deja el máximo en 60; dos o más, en 40.
- Al detallar basta con que la práctica esté decidida y escrita. Al verificar tiene que comprobarla
  ArchUnit o una prueba; si solo se leyó, vale 0,5.

**Trazabilidad (15).** Porcentaje de afirmaciones con las tres cosas: pregunta de origen, caso o
criterio medible, y destino en el código. Antes del código, el destino es el archivo del plan.
**Decisiones fuera del borde.** Si una decisión pide lógica que vive fuera del elemento acordado (por
ejemplo, la idempotencia del caso de uso cuando se detalla un contrato), su archivo destino **existe,
pero no se define en este detallamiento**. Su destino es la **ronda y la plantilla** donde se detallará
(«fuera del borde → ronda `caso-de-uso`»), anotado en la ESPEC §5 en la tabla «Fuera del borde». Con
ese destino, la decisión **cuenta como trazada**: no se le inventa un archivo ni se mete su lógica en el
elemento para completar la matriz.

## 2 · Completitud de la verificación

Peso de cada observación de la revisión: bloqueante 3, mayor 2, menor 1, detalle 0,5. Cuenta como
resuelta si quedó cumplida o descartada con justificación.

`Completitud = 100·Σ(peso·resuelta)/Σpeso ≥ 95`

Además:
- Ninguna bloqueante abierta. Una bloqueante solo se descarta con aprobación del usuario.
- Si los descartes pasan del 30 % del peso, se revisan con el usuario en bloque.
- El build y las pruebas pasan.

## 3 · Explicación (cierre de la verificación)

El usuario tiene que poder explicar, con sus palabras, el código del segmento: **clases completas**, qué
hace cada una en la práctica y qué significa en teoría el patrón que usa (para qué sirve en general).

- El agente pide la explicación clase por clase y la compara con la especificación y con el código.
- Puntúa por clase: correcta 1, incompleta 0,5, errónea 0.
- Puntúa en dos partes, la práctica (qué hace) y la teoría (patrón y propósito), cada una con el 50 %.
- **≥80 % → el segmento se puede aceptar.**
- Por debajo de 80 %, el agente explica lo que faltó, en el modo revisión, y se vuelve a pedir la
  explicación de esas clases.
