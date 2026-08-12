# Revisión del documento de contexto de FlorLogic

**Objeto revisado:** `0_CONTEXTO_para_nuevo_chat.md`, versión 2.0, 5-ago-2026, 315 líneas.
**Criterio de revisión:** ¿sirve para traspasar el proyecto a otro modelo de IA, a otra herramienta/entorno, y para reutilizar el método en otro proyecto?
**Fecha de la revisión:** 11-ago-2026.
**Alcance de la intervención:** solo se señalan las inconsistencias. No se corrigió ningún dato del documento original.

---

## Veredicto en una línea

Como documento de traspaso *dentro de un chat con Claude* está por encima del promedio: tiene trazabilidad, distingue lo confirmado de lo ambiguo y no esconde los huecos. Como documento **portable** falla en tres puntos concretos: está **desactualizado en seis días de trabajo real**, **codifica el entorno actual** (Claude, chat, rutas de Windows) dentro del contenido, y **no tiene identificadores estables**, así que nada de lo que dice se puede citar ni actualizar sin reescribir el párrafo.

---

## 1. Hallazgo principal: el documento ya no describe el proyecto

`Documentacion/Levantamiento de requisitos/5_RF_CRITICOS_v1.xlsx` se creó el **10-ago-2026**, cinco días después de la versión 2.0 del contexto. **No aparece mencionado en ninguna parte del documento.** Contiene siete hojas:

| Hoja | Contenido |
|---|---|
| `Como leer` | Criterio de criticidad, convenciones, advertencia de fuente única |
| `RF Criticos` | 20 requisitos funcionales (RF-C01…RF-C20) con actor, regla de negocio, criterio Dado/Cuando/Entonces, origen, MoSCoW e índice |
| `Trazabilidad` | Cita textual → hallazgo → regla → requisito, para los 20 |
| `Criticidad` | Matriz de 4 ejes con pesos editables (0.4 / 0.2 / 0.25 / 0.15) |
| `Mapa de calidad` | Cada RF contra los atributos de calidad que tensiona |
| `Escenarios - esqueleto` | ESC-01…ESC-08, con las medidas de respuesta marcadas como pendientes |
| `Brechas que bloquean` | BR-21, BR-22, BR-23, BR-24, BR-11 **más cinco brechas nuevas: BR-N1 a BR-N5** |

Consecuencias directas:

- La sección 12 «Qué sigue» dice en el punto 7 *«empezar el catálogo de requisitos»*. Ya está empezado y bastante avanzado.
- Las brechas **BR-N1** (tiempo máximo de confirmación de captura), **BR-N2** (identificar el sistema actual), **BR-N3** (documentos no entregados), **BR-N4** (¿dos capturadores sobre la misma cama?) y **BR-N5** (ventana de sesión offline) no existen en el contexto. Dos de ellas están marcadas «No preguntada», es decir, son deuda de entrevista que un modelo nuevo no vería.
- El escalón de confianza `Confirmado / Inferido / Propuesta del equipo` que usa el Excel **no existe en el contexto**, que trata todo con el mismo peso salvo donde el texto lo aclara en prosa.

Un modelo nuevo alimentado con este contexto **rehará los 20 requisitos desde cero**. Ese es el fallo más caro de todos los listados aquí.

---

## 2. Lo que ata el documento a Claude y al chat (bloquea otro modelo / otra herramienta)

**2.1 — La sección 11 es falsa fuera de Claude.**
Dice *«Claude no puede escuchar audio ni ver video; no puede transcribir»*. Eso es una propiedad de una herramienta, no del proyecto. Otros modelos sí procesan audio nativo. Y hay **dos `.mp4` en la carpeta** (28 MB y 66 MB). La sección debería expresarse como una **capacidad requerida** —«si el asistente procesa audio, retranscribir estos dos archivos resuelve el problema de vocabulario»— en lugar de un impedimento permanente. Tal como está, un modelo capaz de resolver el problema no se enterará de que puede.

**2.2 — La línea 3 asume una interfaz de chat.**
*«Pega este archivo completo al inicio de un chat nuevo.»* El mismo archivo no funciona como `AGENTS.md` en el repo, ni como system prompt, ni como documento de entrega al profesor.

**2.3 — Las preferencias personales están mezcladas con los hechos.**
La sección 1 mete *«respuestas concisas y directas, sin relleno»* junto a quién es el usuario y qué hace. Son dos cosas de vida distinta: los hechos del proyecto cambian cada sesión, el estilo no. En un entorno de código esas preferencias son ruido.

**2.4 — Rutas no portables.**
`OneDrive - UCO\FlorLogic\Documentacion\` con backslashes, espacios, tildes y un espacio doble en `Grabaciones  y Transcripciones`. Se rompe en cualquier shell POSIX y en cualquier URL.

**2.5 — No hay contrato de salida.**
El documento cuenta el estado, pero nunca dice **qué debe producir el asistente ni con qué formato**. Sin eso, cada modelo improvisa: uno responde en prosa, otro genera Excel, otro pide permiso. Las convenciones de la sección 10 son de forma (fuentes, colores), no de entregable.

---

## 3. Referencias que no coinciden con el disco

| # | Dice el documento | Realidad verificada |
|---|---|---|
| 1 | Los archivos aparecen en una tabla plana bajo `Documentacion\` | Están en `Documentacion/Levantamiento de requisitos/`, y dentro en `Entrevistas/Formatos de entrevista/` y `Propuesta de Idea/` |
| 2 | `FlorLogic_Elevator_Pitch.pptx` | El archivo se llama `FlorLogic_Elevator_Pitch (2).pptx` |
| 3 | `Grabaciones y Transcripciones por sesión` | La carpeta real tiene **dos espacios**: `Grabaciones  y Transcripciones por sesión` |
| 4 | «Carpeta de documentación (sin git)» | Correcto para esa carpeta, pero **el repo sí tiene un árbol `docs/` propio** con 4 secciones y dos README. Hay **dos árboles de documentación en paralelo y ninguna regla de cuál manda** |
| 5 | El repo «solo tiene README con la estrategia de ramas» | Falso. Todas las ramas remotas incluyen `docs/README.md`, `docs/contenido-pendiente/README.md` y las carpetas `01-vision-general`, `02-requerimientos`, `03-arquitectura`, `04-manual-usuario` |
| 6 | (no lo dice) | El repo es `github.com/JuanPabloAvendano/FlorLogic`. **El dueño del repositorio es Juan Pablo, no Ruben.** Dato relevante para cualquiera que retome el proyecto |
| 7 | (no lo dice) | La copia local está en la rama `codigo-desarrollo` |

---

## 4. Higiene del repositorio (bloquea el traspaso a un entorno de desarrollo)

Verificado con `git status` sobre la copia local:

```
 M .gitignore
 M README.md
 D docs/01-vision-general/.gitkeep
 D docs/02-requerimientos/.gitkeep
 D docs/03-arquitectura/.gitkeep
 D docs/04-manual-usuario/.gitkeep
 D docs/README.md
 D docs/contenido-pendiente/README.md
```

Dos problemas distintos:

**4.1 — El esqueleto `docs/` está borrado localmente y sin commitear.** Siete archivos en estado `D`. Existen en el remoto pero no en disco. La causa más probable es que el repositorio vive **dentro de OneDrive**, que sincroniza mal los archivos ocultos tipo `.gitkeep`. El `README.md` enlaza a `docs/README.md`, que en la copia local no existe: enlace roto.

**4.2 — Finales de línea.** El diff de `README.md` y `.gitignore` muestra **todas las líneas cambiadas** sin que el texto cambie: es CRLF contra LF. Falta un `.gitattributes` con `* text=auto eol=lf`. Sin él, cualquier compañero en Linux o Mac verá un diff completo falso en cada archivo que toque, y las revisiones de código serán ilegibles.

---

## 5. Problemas de forma que dañan la trazabilidad

**5.1 — No hay identificadores estables.** Las brechas tienen ID (`BR-21`). Los hechos no. No se puede decir «el dato de 1.525 camas cambió» sin reescribir la tabla, ni un modelo puede citar de dónde sacó algo. Todo hecho duro debería tener ID.

**5.2 — La marca `[S2]` mezcla dos ejes.** Significa a la vez «lo dijo el cliente en la sesión 2» y «esto cambió respecto de la v1». Cuando llegue la sesión 3 el esquema se rompe: habrá datos de S2 que ya no son novedad y datos de S3 que sí. Origen y novedad necesitan columnas separadas.

**5.3 — La lista de atributos de calidad está mal numerada y el documento lo sabe.** Va 1, 2, 3, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14. Hay un 3 repetido y no hay 13. El texto lo admite —«hay números repetidos y saltos que hay que limpiar»— pero deja la tabla igual. Un modelo nuevo la leerá como dato bueno y calculará trade-offs sobre ella.

**5.4 — La fórmula del motor no tiene unidades ni dominios.**

```
plantas_sembradas  = área_cama_m² × densidad_siembra(variedad)
tallos_proyectados = plantas_sembradas × %productividad_esperada(variedad)
```

Falta: unidad de cada variable, que `%productividad ∈ (0, 1]`, la cota dura `tallos ≤ plantas`, y —lo más importante— que **de esta fórmula no sale la proyección diaria**, que es lo que consume el gerente de producción. Falta la curva de reparto sobre los ~7 días de corte (BR-23).

**5.5 — Un dato derivado presentado como dato observado.** 25 bloques y ~1.525 camas dan ~61 camas por bloque, pero el propio documento dice que los bloques son de área distinta entre sí. El promedio no es un hecho de la finca; conviene marcarlo como derivado para que nadie lo use al dimensionar.

---

## 6. Lo que impide reutilizar el método en otro proyecto

El documento entrelaza tres cosas que tienen vida independiente:

1. **El método** — convenciones de trazabilidad, reglas de redacción de requisitos, formato Dado/Cuando/Entonces, MoSCoW, reglas de texto para WhatsApp, estilo de los Excel. Todo esto sirve para cualquier levantamiento de requisitos.
2. **Los hechos de FlorLogic** — la finca, el motor, las brechas.
3. **El estado de avance** — qué falta, qué sigue, cuándo se hizo cada sesión.

Para llevar el método a otro proyecto hoy hay que desenredarlo a mano. Separados, el bloque 1 se convierte en una plantilla que se copia y ya.

---

## 7. Lo que el documento hace bien (no tocarlo al reescribir)

- **Separa cita textual de interpretación.** Es la decisión de diseño más valiosa del documento y hay que conservarla.
- **Nombra sus propias contradicciones en vez de resolverlas por conveniencia.** BR-21 (¿−6% contra ±10% a qué nivel de agregación?), BR-22 (9 variedades contra 300) y BR-24 (accesibilidad en el puesto 10) están planteadas con la explicación de por qué importan.
- **Incluye el contraargumento al agente de IA.** La sección 6 argumenta en contra de la propuesta del propio equipo —tres personas capturando, registros repetitivos, un formulario bien diseñado podría bastar— y pide medición en vez de intuición. Eso es poco común y hay que conservarlo íntegro.
- **Cuantifica el dolor.** 1 h/día, 4 h/semana, 8 días de latencia, 2% de error, 8% de las ventas compradas a terceros.
- **Advierte del riesgo de fuente única** y lo declara riesgo aceptado, no resuelto.

---

## 8. Prioridad de corrección

| Orden | Qué | Por qué |
|---|---|---|
| 1 | Incorporar `5_RF_CRITICOS_v1.xlsx` y las brechas BR-N1…BR-N5 | Sin esto se repite una semana de trabajo |
| 2 | Sacar del contenido lo específico de Claude y del chat (§11, §1, línea 3) | Es lo que impide usarlo con otro modelo |
| 3 | Poner IDs estables a los hechos y separar origen de novedad | Sin esto no hay forma de actualizar el documento sin reescribirlo |
| 4 | Corregir rutas y nombres de archivo | Hoy apuntan a archivos que no existen con ese nombre |
| 5 | Decidir si la documentación vive en OneDrive o en el repo `docs/` | Dos árboles paralelos garantizan divergencia |
| 6 | Añadir `.gitattributes` y recuperar el árbol `docs/` borrado | Bloquea el trabajo en equipo multiplataforma |
| 7 | Limpiar la numeración del mini QAW antes de usar la matriz de trade-off | Se van a calcular trade-offs sobre una lista mal numerada |
| 8 | Separar método de hechos en archivos distintos | Habilita reutilizar el método en otro proyecto |

---

*Los puntos 1 a 4 y 8 están aplicados en `0_CONTEXTO_v3.md`. Los puntos 5, 6 y 7 son decisiones que no corresponde tomar en una revisión: quedan señalados y sin ejecutar.*
