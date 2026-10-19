# Plantilla del ESPEC

Cada detallamiento tiene un archivo `ESPEC-<elemento>.md` **junto al código** que define, en la carpeta
del módulo. Si el código vive en otro worktree, se ubica con `git worktree list`.

Reglas:
- Es el **estado actual**, no un acta. Cada ronda reescribe las secciones; no se guarda el historial de
  preguntas.
- Es lo único que la skill relee para retomar.
- Para listar los detallamientos abiertos se buscan los `ESPEC-*.md` cuyo estado no sea `IMPLEMENTADA`.

```markdown
# ESPEC · <elemento>

## Ficha
| Campo | Valor |
|---|---|
| Elemento | <qué es, p. ej. POST /ingesta/v1/sesiones/{id}/lotes> |
| **Definición acordada** | <2-4 líneas en lenguaje sencillo: qué es, qué hace, para quién y qué no es; confirmada por el usuario con «Es lo mismo»> |
| Diccionario | <ruta al diccionario del proyecto, p. ej. codigo/IA_DICCIONARIO.md> |
| Plantilla | <endpoint · caso-de-uso · entidad-agregado · …> |
| Dominio | Entra: <…> · Sale: <…> · Fuera: <…> |
| Generalidad | <conceptual · flujo · contrato · casi línea por línea> |
| Para qué | <entender · especificar para programar> |
| Estado | BORRADOR · LISTA PARA CÓDIGO · ACEPTADA · IMPLEMENTADA |
| **Etapa actual** | 1 Tamaño · 2 Consulta · 3 Preguntas · 4 Generación · 5 Revisión · 6 Verificación · 7 Aceptación |
| **Dónde retomar** | <p. ej. «ronda 2, pregunta 7» o «segmento 2/3, pasada 1» o «verificación, OBS-2-04»> |
| Precisión | <n> % (cobertura · contradicciones · prácticas · trazabilidad), calculada por el verificador el <fecha> |
| Completitud | <n> % · Explicación <n> % |
| Avance por etapa | Tamaño ✔ · Consulta ✔ · Preguntas 85 % ✔ · Generación 1/3 · Revisión 60 % · Verificación — · Aceptación — |

## 1 · Propósito
<2-3 líneas con su fuente>

## 2 · Decisiones
| Id | Afirmación | Fuente | Estado |
|---|---|---|---|
| A-1 | <«Un lote se rechaza entero solo si falla su sobre»> | <ADR-027 · ronda 1 P8> | decidida · supuesto · pendiente |

## 3 · Variables
| Nombre | Valor | Tipo | Dueño |
|---|---|---|---|
| <max_anotaciones_lote> | <200> | fija · configurable · pendiente | <quién decide> |

## 4 · Comportamiento
- **C-1** Dado <…> · cuando <…> · entonces <…>  (cubre A-n)

## 5 · Forma técnica
<De la entrada a la salida, según la plantilla: contrato, DTO, puerto, caso de uso, dominio, puertos de
salida, adaptador, errores. Cada pieza con su archivo destino.>

**Fuera del borde.** <Decisiones cuya lógica vive fuera del elemento: id → ronda y plantilla donde se
detallará su archivo. Cuentan como trazadas (`indice-precision.md`, Trazabilidad).>

## 6 · Contradicciones y pendientes
| Id | Qué | Tipo | Dueño | Estado |
|---|---|---|---|---|

## 7 · Fuera del dominio (para otro canal)
- <…>

## 8 · Plan de segmentos
| Seg. | Archivos (líneas estimadas) | Cubre | Estado |
|---|---|---|---|
| 1 | <Comando.java (40), Puerto.java (20), Servicio.java (160), ServicioTest.java (220)> | <A-1..A-5, C-1..C-4> | por escribir · escrito · revisado · verificado · aceptado |

## 9 · Matriz requisito → código → prueba
| Requisito | Archivo:símbolo | Prueba | Estado |
|---|---|---|---|

## 10 · Observaciones de revisión
<Formato OBS de verificacion.md §3>

## 11 · Informe de verificación
<Tabla de verificacion.md §4 y los cálculos>
```
