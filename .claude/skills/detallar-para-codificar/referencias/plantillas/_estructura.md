# Estructura común de una plantilla

Una plantilla es un **subprompt**. Cuando la etapa de consulta identifica qué se va a detallar, el agente
carga la plantilla más adecuada y la sigue como sus instrucciones para guiar las preguntas. Por eso cada
plantilla es, a la vez:

- **un lugar de consulta**: los conceptos, los patrones y las prácticas del tipo de elemento, con sus
  referencias;
- **un modelo de funcionamiento apropiado**: cómo se ve uno bien hecho en arquitectura hexagonal y Clean;
- **una guía de preguntas**: qué se define primero y qué preguntas cierran cada decisión.

Todo lo que una plantilla afirma tiene que venir de una fuente (libro, guía o artículo) o ir marcado
`[opinión]`.

## Secciones obligatorias, en este orden

```
---
plantilla: <id>
aplica_a: <qué elementos cubre>
entra_desde: <plantilla anterior en el flujo, o «ninguna»>
sale_hacia: <plantillas que siguen>
---
# Plantilla · <Nombre>

## 0 · Instrucciones para el agente
   Frases imperativas: cómo usar esta plantilla dentro de la ronda de preguntas, qué no salirse,
   cuándo pasar el detalle a otra plantilla.

## 1 · Qué se define primero
   Los conceptos principales del tipo de elemento, en orden de prioridad, con el porqué y la fuente
   de cada uno. Es la «plantilla de ejemplo con elementos prioritarios» que se ofrece en las preguntas.

## 2 · Funcionamiento apropiado
   Cómo funciona un elemento de este tipo bien hecho: responsabilidades, flujo, dónde vive cada pieza
   en el hexágono y un diagrama en texto.

## 3 · Patrones de diseño
   Tabla: patrón · problema que resuelve · cuándo usarlo · cuándo no · fuente.
   Y qué decisión de patrón tiene que tomar la ronda.

## 4 · Prácticas profesionales verificables
   Tabla: id · práctica · severidad (crítica 3 / mayor 2 / menor 1) · cómo se verifica al detallar ·
   cómo se verifica al verificar (ArchUnit, prueba o lectura) · fuente.
   Alimenta el 25 % de adherencia del índice.

## 5 · Árbol de preguntas
   Son 20 preguntas fijas, de general a detallado, que se hacen 10 por ronda: P1–P10 en la primera, P11–P20 en la siguiente (SKILL.md, etapa 3). Cada una:
   título de una línea · contexto pequeño o mediano con el «por qué» (SKILL.md, «Cómo preguntas») · 2-4 opciones con la recomendada (★) primero ·
   un ejemplo por opción, en el lenguaje del proyecto (por defecto Java 21 y Spring) ·
   qué concepto de §1 o práctica de §4 cierra.

## 6 · Poda y ramas nuevas
   Qué respuestas eliminan preguntas, y en qué se usan los cupos liberados.

## 7 · Cobertura
   Lista de casillas que mide el 35 % del índice para este tipo de elemento.

## 8 · Antipatrones
   Tabla: antipatrón · señal detectable en el código o en la especificación · fuente.

## 9 · Código de referencia real
   Ejemplos **sacados de repositorios públicos reales** en Java, verificados (que el repositorio y
   el archivo existan). Cada ejemplo lleva:
   - qué patrón o pieza del hexágono muestra;
   - repositorio, ruta del archivo y URL (mejor si es permanente, con commit);
   - licencia del repositorio;
   - un fragmento corto (≤40 líneas), recortado con `// …` donde se omite algo, sin inventar código;
   - qué copiar de él y qué no.
   Mínimo 3 ejemplos, de repositorios distintos cuando se pueda. Candidatos conocidos, que hay que
   verificar: thombergs/buckpal, ddd-by-examples/library, citerus/dddsample-core,
   xmolecules/jmolecules-examples, spring-projects/spring-modulith (ejemplos),
   odrotbohm/spring-restbucks, eventuate-tutorials/ftgo-application, ttulka/ddd-example-ecommerce.

## 10 · Plantillas de código
   Esqueletos en Java 21 + Spring, listos para adaptar, uno por patrón o pieza principal de la
   plantilla. Se basan en los ejemplos reales de §9 y lo dicen. Llevan `TODO` donde la ronda de
   preguntas decide algo, con el número de la pregunta que lo decide.

## 11 · Qué se genera
   Archivos típicos que salen de esta plantilla, su orden dentro de un segmento y las pruebas que
   los acompañan.

## 12 · Referencias
   Libros, guías y artículos con su URL cuando exista, y una línea de qué aporta cada uno.
```

## Reglas de redacción

- Español claro, frases cortas, voz activa.
- Los ejemplos usan un dominio genérico (pedidos, inventario, envíos), no el de un proyecto concreto.
- Extensión: sin tope. La plantilla es para el agente; el usuario la revisa una sola vez. Que sea completa antes que corta.
- Las interpretaciones propias van marcadas `[opinión]`.
