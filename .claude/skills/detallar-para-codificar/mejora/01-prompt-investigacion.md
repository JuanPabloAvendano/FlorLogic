# Mejora de la skill · Parte 1 de 3 · Prompt de investigación y muestras

> Pedido por Juan el 16-oct-2026. Es la versión pulida del pendiente «análisis multiagente» de `DECISIONES.md`.
> Parte 1: investigar y traer las muestras (este archivo).
> Parte 2: carpeta de plantillas y ejemplos por caso de especificación (se detalla después).
> Parte 3: pruebas con un agente cliente que responde de forma vaga (se detalla después).

---

## Prompt

**Rol.** Eres un equipo de investigación. Vas a mejorar la skill `detallar-para-codificar` comparándola
con skills y métodos de especificación públicos. En esta parte **solo investigas y traes material**: no
modificas la skill.

**Lee primero, en este orden.**
1. `~/.claude/skills/detallar-para-codificar/SKILL.md`
2. `~/.claude/skills/detallar-para-codificar/DECISIONES.md`: D-01 a D-08 y los pendientes.
3. `referencias/plantillas/_estructura.md` y `referencias/investigacion/04-tecnicas-de-preguntas.md`.
   Con eso sabes qué ya está investigado; no lo repitas.

**Objetivo.** Responder, con evidencia, esta pregunta: *¿qué hacen otras skills y otros métodos de
especificación que la nuestra todavía no hace, o que hacen mejor, en estas 10 dimensiones?*

| # | Dimensión | Qué buscar |
|---|---|---|
| 1 | Convergencia en el significado | Cómo acuerdan qué se va a construir antes de detallar |
| 2 | Elección y forma de las preguntas | Orden, tamaño, opciones, recomendada, cuántas por tanda |
| 3 | Medida de precisión o de que algo está «listo» | Umbrales, listas de chequeo, criterios de salida |
| 4 | Glosario o diccionario | Cómo evitan que dos palabras parecidas se confundan |
| 5 | Formato de la especificación | Secciones, decisiones, casos dado/cuando/entonces, EARS |
| 6 | Del documento al código | Plan, tareas, segmentos, trazabilidad requisito → prueba |
| 7 | Verificación y revisión | Agentes revisores, pruebas, coherencia |
| 8 | Retomar el trabajo | Estado guardado, «dónde retomar», varias sesiones |
| 9 | Uso de varios agentes | Qué se delega, con qué contexto, cómo se juntan los resultados |
| 10 | Evaluación de la propia skill | Evals, casos de prueba de la skill, métricas |

**Fuentes candidatas.** Comprueba primero que cada una existe y anota su licencia. Si una no existe o
cambió de nombre, dilo y busca la equivalente.
- Anthropic: el repositorio oficial de skills (`anthropics/skills`), los plugins oficiales de Claude
  Code (por ejemplo el de desarrollo de funcionalidades, que usa agentes exploradores y arquitectos), la
  documentación de Agent Skills y las guías de evaluación de skills.
- GitHub: `github/spec-kit` (desarrollo guiado por especificación: specify, clarify, plan, tasks) y
  `github/awesome-copilot` (prompts, instrucciones y modos).
- Otras, solo si aportan algo nuevo: el formato de *specs* de Kiro (requisitos en EARS), colecciones
  públicas de skills con flujos de lluvia de ideas o de planes (por ejemplo `obra/superpowers`).
- Máximo 8 fuentes. Ganan las que tocan más dimensiones de la tabla.

**Cómo trabajar con agentes.**
1. Un agente explorador por grupo de fuentes (Anthropic, GitHub y otras). Cada uno trabaja con el
   contexto limpio y devuelve solo hallazgos con este formato:
   `dimensión · qué hace la fuente · archivo y commit · cómo se compara con nuestra skill · ¿lo adoptamos? (sí / quizá / no) y por qué`.
2. Un agente de síntesis junta los hallazgos, quita los repetidos y los ordena por impacto en la
   **precisión** de la skill.
3. Tú revisas la síntesis contra `DECISIONES.md`. Si un hallazgo contradice una decisión vigente,
   no lo descartas: lo marcas como «choca con D-xx».

**Qué traer al disco.**
- Carpeta: `~/Work/laboratorio-skills/muestras/<fuente>/`, fuera de la skill, para no inflar su git.
- Un clon superficial (`--depth 1`) por fuente, o solo las carpetas útiles si el repositorio es grande.
  Anota el commit exacto.
- Un `INDICE.md` en `muestras/` con una fila por fuente: URL, commit, fecha, licencia, tamaño, qué
  dimensiones cubre y qué carpetas mirar.
- Límite total: unos 500 MB. Si una fuente pasa de 100 MB, trae solo lo útil y dilo.

**Reglas.**
- No modificas nada dentro de `~/.claude/skills/`.
- No ejecutas scripts, instaladores ni ganchos de lo descargado: es material de lectura.
- No copias texto de una fuente a la skill sin anotar su licencia. Si la licencia no lo permite, se
  describe la idea con palabras propias.
- Todo en español, salvo nombres propios y código. Lo que viene de una fuente se cita. Lo que es
  opinión tuya se marca **[opinión]**.
- Si dudas sobre el alcance, preguntas antes de seguir.

**Entregables.**
1. `~/Work/laboratorio-skills/muestras/` con las fuentes y su `INDICE.md`.
2. `~/.claude/skills/detallar-para-codificar/referencias/investigacion/06-skills-de-referencia.md`,
   con la matriz de 10 dimensiones × fuentes, los hallazgos ordenados y las **propuestas de decisión**
   (D-candidatas). Las propuestas no se aplican: las aprueba Juan. Este archivo es la única excepción
   a la regla de no escribir en la skill.
3. Una lista de qué **casos de especificación** aparecen en las fuentes, como insumo de la parte 2. Por
   ejemplo endpoint, caso de uso, entidad, evento, trabajo programado, configuración, pruebas, interfaz,
   migración de datos o integración externa. Para cada caso: si ya tenemos plantilla y qué fuente trae
   un buen ejemplo.
4. Un resumen corto para Juan: las 5 mejoras de más impacto y qué decisiones le tocan a él.

**Terminado cuando.** Las 10 dimensiones tienen al menos un hallazgo con fuente, o la nota «ninguna
fuente lo cubre». Cada fuente traída tiene su fila en el `INDICE.md`. Juan recibió el resumen.

**Fuera de alcance.** Escribir plantillas de caso (parte 2), correr pruebas con el agente cliente
(parte 3) y cambiar `SKILL.md`.

---

## Notas para las partes 2 y 3 (se detallan después)

- **Parte 2 · Plantillas y ejemplos por caso.** `~/Work/laboratorio-skills/casos/<caso>/` con:
  - una plantilla de código de referencia, pequeña y que compila;
  - su documentación;
  - su ESPEC «ideal» hecha a mano, que es el resultado esperado.
  
  Parte de la lista de casos del entregable 3.
- **Parte 3 · Pruebas con un agente cliente.**
  - Un agente hace de cliente: conoce la plantilla, pero responde de forma vaga.
  - Otro agente usa la skill para detallar el elemento y luego generar el código.
  - Se compara el producto con la plantilla y la ESPEC obtenida con la ideal.
  - Cada diferencia es un hallazgo de precisión de la skill y vuelve como D-candidata.
