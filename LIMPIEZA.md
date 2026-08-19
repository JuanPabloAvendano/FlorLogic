# FlorLogic — Limpieza del contexto

**15-ago-2026.** Depuración de información generada por IA que quedó redundante, sin desarrollar o
superada por decisiones posteriores.

**Regla que gobernó toda la limpieza:** no se tocó ni una transcripción, ni una grabación, ni un
documento escrito por el equipo. Solo se movió material que era **andamiaje de IA** — útil en su
momento, sustituido después.

**Nada se borró.** Todo lo retirado está en `_to_delete/`, organizado por carpeta de origen. Se
puede recuperar cualquier cosa, o borrar la carpeta entera cuando tengas confianza.

---

## 1. Primero: qué es humano y qué es IA

La clasificación fue el paso más importante, porque hay archivos que **parecen** de IA por su formato
pero contienen evidencia humana dentro.

### Evidencia primaria — intocable

| Archivo | Por qué |
|---|---|
| Las 3 grabaciones `.mp4` y las 3 transcripciones `.vtt` | Fuente última de todo |
| `2_ENTREVISTA_S1_Diligenciada_y_Vacios.xlsx` | **Contiene las respuestas del cliente sin editar**, pregunta por pregunta. El archivo lo armó la IA; el contenido de la columna D es del cliente |
| `MINI QAW PLANTILLA NO TERMINADA.xlsx` | Los rankings los llenaron el cliente y el equipo. `DEC-03` los descartó como *fuente*, pero siguen siendo evidencia de lo que se dijo |
| `FuncionalidadesSignificativas.xlsx` | Escrito por el equipo. Es el catálogo vigente |
| `RestriccionesNegocio.xlsx` | Escrito por el equipo |
| `FlorLogic_Elevator_Pitch (2).pptx` | Propuesta del equipo |
| `FlorLogic_Mapa_de_Impacto.xlsx` | Del equipo. Ver el hallazgo de la sección 5 |

### Andamiaje de IA — retirado

Lo que se movió a `_to_delete/`, y por qué:

| Archivo | Peso | Por qué se retira |
|---|---:|---|
| `0_CONTEXTO_para_nuevo_chat.md` | 25 KB | Versión 2.0 del contexto. La v3 la reemplazó completa y corrigió sus errores (entre ellos, listar a «Ruben» como miembro del equipo) |
| `REVISION_CONTEXTO_v2.md` | 12 KB | Era la revisión **que originó** la v3. Ya hizo su trabajo |
| `docs/03-arquitectura/FlorLogic-glosario.archimate` | 34 KB | Sus 88 elementos están dentro del modelo actual, con los mismos identificadores |
| `docs/03-arquitectura/FlorLogic-core-negocio.archimate` | 6 KB | Ejemplo mínimo desechable, declarado así desde el principio |
| `docs/03-arquitectura/FlorLogic-modelo.archimate.bak` | 251 KB | Copia automática que hace Archi al guardar. Basura del editor |
| `5_RF_CRITICOS_v1.xlsx` | 35 KB | Numeración `RF-Cnn` obsoleta desde `DEC-04`. **Sus citas se rescataron antes** — ver sección 3 |
| `6_FUNCIONALIDADES_CRITICAS_v1.xlsx` | 17 KB | Superado por `FuncionalidadesSignificativas.xlsx` (`DEC-04`) |
| `1_PLANTILLA_Levantamiento_Requisitos.xlsx` | 44 KB | Plantilla reutilizable sin datos de FlorLogic. Además sus ejemplos usan un caso de rosa que no corresponde a esta finca |
| `3_DIAGNOSTICO_Brechas_y_Plan_de_Accion.xlsx` | 35 KB | Las brechas vivas están en el modelo; las cerradas, en `DECISIONES.md` |
| `4_DOCUMENTOS_Requeridos_al_Cliente.docx` | 18 KB | Lista de 38 documentos. Sustituida por la lista de 7 de `HOJA_SESION_CLIENTE.md` |
| `4b_Lista_para_WhatsApp.txt` | 4 KB | Igual que el anterior, en texto plano |
| `GUION_SESION_CLIENTE.md` | 22 KB | Las 84 preguntas quedaron condensadas en las 12 de `HOJA_SESION_CLIENTE.md` |

**Total retirado: 12 archivos, ~503 KB de contexto activo.**

---

## 2. El modelo ArchiMate, podado

De **253 a 236 elementos**. Se quitaron 17 que eran inferencia de IA sin desarrollo, sin confianza,
o pura duplicación de `DECISIONES.md`.

### Propuestas que nunca se desarrollaron

- **Notificaciones** — apareció en el pitch de S1 y no se volvió a mencionar. Nunca entró a un
  catálogo, nunca se discutió, nadie la pidió.
- **Pompón** — es un ejemplo, no un concepto. Es *una instancia* de Variedad, y estaba en el modelo
  solo porque las transcripciones destrozan la palabra.

### Elementos reemplazados por otros

- **Asistente de lenguaje natural abierto** (componente y requisito) — `DEC-16` lo sustituyó por el
  asistente de captura offline, con alcance cerrado.
- **Administrador del sistema (rol histórico)** — `DEC-01` lo partió en Administrador de la empresa
  y Operador de la plataforma. Mantener los tres era ruido en todas las vistas.

### Fichas de requisitos absorbidos

- `RF-010`, `RF-015` y `RF-C19`. `DEC-08` decidió su destino y la tabla de esa decisión conserva la
  trazabilidad. Tener tres fichas vacías diciendo «esto ya no existe» no aporta.

### Historia duplicada

Siete Assessments ya cerrados cuya única función era contar algo que `DECISIONES.md` cuenta mejor:
las dos numeraciones de rankings, los requisitos huérfanos, las tres numeraciones de requisitos, el
mapa de empatía, los dos árboles de documentación, las preguntas sí/no pendientes, y el acceso del
operador a los datos.

> **Los Assessments cerrados que SÍ se quedaron** son los cuatro que todavía llevan una advertencia
> viva: el modelo de entrega (agrava el riesgo de fuente única), el dominio sin precios (vigilar el
> costo de producción), el alcance del BI (el residuo fitosanitario) y la IA (tres avisos abiertos,
> incluido el choque con el aislamiento entre empresas).

### Los atributos de calidad, comprimidos

Los catorce llevaban **cuatro rankings cada uno** en su documentación. `DEC-03` los descartó todos
como fuente, así que reproducirlos en el modelo era exactamente el tipo de información poco confiable
que esta limpieza busca soltar.

Ahora dicen una sola cosa: **prioridad sin definir, se re-elabora bajo SaaS**. Los números viejos
siguen consultables en el mini QAW y en `0_CONTEXTO_v3` §6, que es donde corresponde.

---

## 3. Lo que se rescató antes de archivar

**`CITAS_TEXTUALES_CLIENTE.md`** — nuevo, en `Documentacion/Levantamiento de requisitos/Entrevistas/`.

`5_RF_CRITICOS_v1.xlsx` tenía siete hojas obsoletas y **una que no lo era**: la hoja `Trazabilidad`,
con **20 citas textuales del cliente** y lo que se dedujo de cada una. Eso es evidencia, no
andamiaje, y era el único sitio donde el mapeo cita → regla de negocio estaba escrito.

Se extrajo a un archivo propio de 11 KB antes de archivar el xlsx de 35 KB. La numeración `RF-Cnn`
va marcada como obsoleta en cada entrada, para que nadie la confunda con el catálogo vigente.

---

## 4. `0_CONTEXTO_v3.md` — conservado, pero con etiqueta

**No se archivó, y a propósito.** Sigue siendo la fuente de los 49 hechos del dominio (`H-01` a
`H-49`) y del glosario de la finca, y el modelo lo cita por ID en decenas de elementos. Borrarlo
rompería la trazabilidad de todo.

Lo que sí se hizo: **añadirle una cabecera de vigencia** que dice, sección por sección, qué sigue
válido y qué está superado. Cinco de sus once secciones lo están.

Vigentes: los hechos del dominio (§2), el glosario de la finca (§11) y los anexos de método.
Superadas: alcance, roles, atributos de calidad, el asistente, el inventario de archivos y los
próximos pasos.

Eso reduce el contexto que hay que leer sin destruir la trazabilidad. Es el mismo criterio que se
usó con todo lo demás: **soltar el andamiaje, conservar la evidencia.**

---

## 5. Un hallazgo de la revisión

`FlorLogic_Mapa_de_Impacto.xlsx` estaba marcado como «no revisado» desde el primer inventario. Al
abrirlo para clasificarlo apareció algo que contradice al modelo.

La hoja `Plantilla Visión` contiene **análisis de competencia**: menciona Tend, FlorNet.co y Mprise
Agriware, y señala que FlorNet.co es la única con función offline, limitada a empaque y ventas.
También define el público objetivo —cultivos pequeños, medianos y grandes de Colombia con
conectividad irregular— y la diferenciación del producto.

El modelo dice hoy, en el driver de viabilidad comercial, que *«sigue sin investigación de mercado
que lo respalde»*. **Eso ya no es del todo cierto.** No es un estudio de mercado, pero es más de lo
que el contexto reconocía.

Vale la pena revisar ese archivo con calma: es el único sitio donde hay competidores nombrados, y
bajo la decisión `DEC-01` de construir un SaaS para varias fincas, esa información pasó de ser un
adorno del pitch a ser un insumo de negocio.

---

## 6. Estado del contexto activo

| | Antes | Después |
|---|---:|---:|
| Archivos de trabajo en el repo | 24 | 12 |
| Elementos en el modelo | 253 | 236 |
| Peso del modelo | 301 KB | 273 KB |
| Contexto retirado | — | ~503 KB |

### Lo que queda activo

**En la raíz:** `RESUMEN_SISTEMA.md` (el estado actual), `DECISIONES.md` (el porqué de cada cambio),
`0_CONTEXTO_v3.md` (los hechos del dominio, con su etiqueta de vigencia), `LIMPIEZA.md` (esto).

**En `docs/03-arquitectura/`:** el modelo, el índice de alcance y la guía de lectura.

**En `Documentacion/`:** `FuncionalidadesSignificativas.xlsx` y `RestriccionesNegocio.xlsx`, que son
el catálogo vigente y las restricciones.

**En `Entrevistas/`:** las grabaciones, las transcripciones, la entrevista S1 diligenciada, el mini
QAW, las citas textuales rescatadas y `HOJA_SESION_CLIENTE.md`.

---

## 7. Cómo deshacer esto

Todo está en `_to_delete/`, con la estructura de carpetas de origen. Para recuperar cualquier archivo
basta con moverlo de vuelta a su ruta original.

Cuando estés seguro, borra `_to_delete/` a mano. **No lo borré yo a propósito**: una limpieza que no
se puede revisar antes de confirmar no es una limpieza, es una pérdida.

Y como el repositorio está en git, incluso después de borrar la carpeta todo sigue recuperable desde
el historial mientras no se purgue.
