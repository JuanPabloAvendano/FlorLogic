# FlorLogic — Modelo C4 en Archi 4.7

> **v1.0 · PROPUESTA DEL EQUIPO, sin validar con el cliente.**
> Archivo: `docs/03-arquitectura/FlorLogic-C4.archimate` — **independiente** de
> `FlorLogic-modelo.archimate` a propósito: fundir dos modelos ArchiMate escritos por separado obliga
> a resolver identificadores a mano y es donde se pierde trabajo.

## Qué contiene

**64 elementos · 110 relaciones · 7 vistas.** Verificado: XML bien formado, y las 124 conexiones y
95 objetos de vista resuelven todas sus referencias.

| Vista | Qué es |
|---|---|
| `v-1` **Nivel 1 · Contexto** | Los 5 actores y los 3 sistemas externos. FlorLogic es una sola caja |
| `v-2` **Nivel 2 · Contenedores** | Los 8 contenedores con su tecnología propuesta. Los contratos `CT-nn` viven aquí |
| `v-3a` **Componentes — Aplicación de captura** | Tu N1, reencuadrado |
| `v-3b` **Componentes — Backend de la finca** | Tu N2 |
| `v-3c` **Componentes — Aplicación web de consulta** | Tu N3 |
| `v-3d` **Componentes — Servicios en línea** | Tu N4 |
| `v-4` **Despliegue** | Los cuatro nodos y qué contenedor corre en cada uno |

## El mapeo que se usó

El del artículo oficial de Archi 4.7, que fue el que elegiste:

| C4 | ArchiMate |
|---|---|
| Person | Business Actor |
| Software System | Application Component |
| Container | Application Component |
| Component | **Application Function** |
| Deployment Node | Node |
| Relación C4 | **Triggering Relationship** (Assignment en la vista de despliegue) |

## Cómo leer cada elemento

La documentación de todos empieza con tres líneas fijas, siguiendo la misma convención que
`ALCANCE:` del modelo principal:

```
C4:               qué es en el modelo C4
TECNOLOGÍA:       la candidata propuesta, o SIN DECIDIR
RESPONSABILIDAD:  qué hace, con el requisito o restricción que lo justifica
```

Una cuarta línea que empieza con **`[!]`** marca una contradicción o un bloqueo. Hay siete:
`T1` sin decidir en la app de captura · `A5` decidido contra el cliente en el cifrado local ·
`CT-02` (mismo evaluador en los dos lados) · `D3` bloqueando el motor de proyección ·
la credencial dentro del paquete versionado · `ESC-06` contra `RF-017` en los servicios de escritura ·
y la IA analítica en conflicto con el Key Vault.

## `[!]` Lo único que no pude verificar

**Este modelo no usa `<property>`.** `FlorLogic-modelo.archimate` no tiene ni una sola, así que no
hubo de dónde copiar esa serialización, y las propiedades escritas a mano sin un ejemplo del que
partir ya han dado problemas antes. Por eso el estereotipo C4 va en la documentación y no como
propiedad.

**La consecuencia:** las **Label Expressions** de Archi 4.7 —lo que el artículo usa para mostrar
`nombre / [Container: tecnología] / descripción` dentro de la caja— no pueden apuntar a
`${property:Stereotype}`. Sí funcionan con `${documentation}`.

**Prueba de 10 segundos:** abre `FlorLogic-C4-PRUEBA-property.archimate`. Si carga sin errores y el
elemento muestra dos propiedades en la pestaña *Properties*, dímelo y regenero el modelo con las
propiedades puestas, para que las Label Expressions funcionen como en el artículo.

## Lo que falta y es tuyo

**La disposición.** Los elementos están en una rejilla automática. Archi no reordena solo: hay que
arrastrarlos. La estructura y las conexiones ya están; mover cajas no rompe nada.

**Los colores.** C4 distingue por color el sistema en foco, los sistemas externos y las personas. En
Archi se hace a mano sobre cada objeto de la vista.

**La leyenda.** C4 la exige siempre. Se añade como un elemento de nota en cada vista.

---

*Modelo C4 v1.0 · propuesta del equipo, sin validar con el cliente.*
