# Apartado del contrato de ingesta · índice y propuesta de poda
Qué es: el inventario de lo que se sacó de la ESPEC de TX-1.K porque no es del contrato, a dónde fue cada pieza y qué saldría de la ESPEC si Juan aprueba la poda.
Quién: IA (Claude), 2026-10-18, skill information-pairment (`~/.claude/skills/information-pairment/`), desde `ESPEC-contrato-ingesta.md`, su HISTORIA y `IA_destilado-2026-10-17/`.
Estado: SIN REVISAR
Manda por encima: `ESPEC-contrato-ingesta.md` (podada el 18-oct) · la documentación humana (no se tocó) · la palabra de Juan.

## 1 · El borde
**Definición acordada (16-oct, 2.ª vuelta):** «Acuerdo entre el celular y el servidor de la finca para entregar lo capturado en una jornada: qué lleva cada mensaje, qué responde el servidor, cómo se reconoce un envío repetido y cómo cambia de versión sin romper a nadie. No es la lógica que decide si una anotación vale, ni la base de datos, ni el código que recibe la petición.»

Criterio: una pieza se queda solo si cambia lo que el contrato recibe, devuelve o garantiza.

## 2 · Qué hay aquí
| Archivo | Tipo | Piezas |
|---|---|---|
| `semillas/IA_SEMILLA-caso-de-uso-ingreso.md` | plantilla `caso-de-uso` | idempotencia, obsoleta/superada, orden de captura, marcas, `hora_dudosa`, `vieja`, juicio con el catálogo |
| `semillas/IA_SEMILLA-caso-de-uso-celular.md` | plantilla `caso-de-uso` (simulador) | mínimo, edición local, retención, borrado y lápidas, reintentos |
| `semillas/IA_SEMILLA-endpoint-sesion-y-credencial.md` | plantilla `endpoint` (ronda 2) | sesión, credencial, celular compartido, registro de dispositivo |
| `semillas/IA_SEMILLA-adaptador-persistencia-ingreso.md` | plantilla `adaptador-persistencia` | inserción atómica, índice único, cuarentena, `statement_timeout` |
| `semillas/IA_SEMILLA-configuracion-catalogo-y-versiones.md` | plantilla `configuracion` | cuatro ejes de versión, mínimo personalizable, plazos, umbrales |
| `semillas/IA_SEMILLA-entidad-agregado-produccion-y-asignacion.md` | plantilla `entidad-agregado` | lote, sección, cama, turnos, orden de captura, D1 |
| `semillas/IA_SEMILLA-trabajo-programado-respaldo.md` | plantilla `trabajo-programado` (TX-2) | `respaldado_hasta`, prueba de restauración |
| `documentacion/Documentacion/Drivers-Arquitectonicos/IA_RF-propuestos-desde-TX1K.md` | requisitos funcionales | `RF-P01`..`RF-P04` |
| `documentacion/Documentacion/Drivers-Arquitectonicos/IA_ESC-propuestos-desde-TX1K.md` | escenarios de calidad | `ESC-P01`..`ESC-P03` y las dudas sobre `ESC-08`, `ESC-34` y `ESC-58` |
| `documentacion/docs/03-arquitectura/decisiones/IA_2026-10-16-*.md` (4) | decisiones de Juan sin ADR | ADR-027 deprecado · corrige solo el gerente · captura tardía · nada desconocido entra |
| `documentacion/docs/03-arquitectura/flujos/transacciones/IA_TX1-jornada-event-storming.md` | event storming + flujo (Mermaid) + casos de uso | `ES-P01`..`ES-P05`, `UC-P01`..`UC-P05` |
| `documentacion/docs/contenido-pendiente/IA_preguntas-para-juan-desde-TX1K.md` | preguntas abiertas | decidir · ajustar ADR · confirmar agregados del agente |

Las rutas bajo `documentacion/` son las mismas de la rama `documentacion-desarrollo`: aceptar un archivo es moverlo a esa ruta, quitarle `IA_` si Juan lo hace suyo y cambiar su estado.

## 3 · Inventario de la ESPEC
**Se queda** = es del contrato. **Parte** = la fila se parte: se queda la forma y sale la lógica.

### §2 Decisiones
| Id | Veredicto | Lo que sale y a dónde |
|---|---|---|
| A-1, A-9, A-10, A-12, A-13, A-14, A-15, A-16, A-19, A-21, A-22, A-24, A-27 (forma), A-29, A-33, A-34, A-36, A-37, A-38, A-42 (forma), A-44, A-47 | se queda | — |
| A-2 | parte | se queda: los tres casos y el resultado (`sin_efecto`, `rechazada`). Sale el orden de juicio, la cuarentena y RF-022 → semilla ingreso |
| A-3, A-11 | parte | se queda: «el contrato garantiza que viajan los datos». Sale el cálculo de la clave → semilla ingreso |
| A-5 | sale | → semilla sesión |
| A-6, A-7, A-8 | sale | → semilla ingreso (A-7 → semilla celular) |
| A-17 | parte | se queda `arranque` y `mono_ms`. Sale el cálculo de `hora_dudosa` → semilla ingreso |
| A-18 | se queda | — |
| A-20 | parte | se queda la forma de `datos`. Sale «cama, bloque o lote cuando D1 decida» → semilla entidad |
| A-23 | se queda | — (los códigos inferidos van a preguntas §C) |
| A-26 | parte | se queda la forma de la respuesta. Sale el sello de la primera sesión → semilla ingreso |
| A-30 | parte | se queda *partición*. Sale *lote* → semilla entidad (y ya está en el diccionario) |
| A-31 | parte | se queda el significado de las dos marcas y el invariante. Sale el cálculo, la restauración, la alerta y la retención → semillas ingreso, celular y respaldo |
| A-32, A-35 | parte | se queda la frontera `422` / anotación y los códigos. Sale el juicio con el catálogo → semilla ingreso |
| A-39 | parte | se queda `consecutivo_repetido` y `accion`. Sale el registro del dispositivo → semilla sesión |
| A-40 | parte | se quedan ⟦2⟧ plazos, ⟦3⟧ y la fórmula de ⟦4⟧ (A-41 la usa). Salen ⟦1⟧ → sesión, el comportamiento de ⟦4⟧ → celular, ⟦5⟧ → persistencia |
| A-41 | se queda | es la otra mitad del error; copia en la semilla celular |
| A-45 ⟦1⟧ | parte | se queda `obsoleta` como resultado y la constancia en `excepciones[]`. Sale cómo se deduce la orden, la reasignación y la superada → semilla ingreso, entidad, decisión «captura tardía» |
| A-45 ⟦2⟧, ⟦2⟧b | parte | se queda `primeros_acuses[]` y `recibido_ms`. Sale la regla de `vieja` → semilla ingreso |
| A-45 ⟦3⟧ | sale | → semilla celular, `RF-P02`, `ES-P04` (se queda el motivo `descartada_por_gerente` en A-47) |
| A-46 | parte | se queda `sin_minimo`. Sale el flujo de la jornada → semilla celular, event storming |
| A-48 | sale | → semilla celular (las lápidas viajan por A-47, que se queda) |
| A-49 | parte | se quedan ⟦3⟧ (inmutable → huella) y ⟦4⟧ (`motivo_desviacion`). Salen ⟦1⟧ ⟦2⟧ ⟦5⟧ ⟦6⟧ → decisiones «corrige solo el gerente» y «ADR-027», semilla celular |
| A-50 | parte | se queda «`contrato-incompatible` y `version_desconocida` son anomalía». Sale el principio de los cuatro ejes → decisión «nada desconocido», semilla configuración |
| A-51 | parte | se queda «la credencial de la petición es quien entrega» y la respuesta a `401`/`403 sesion_ajena`. Sale login local, renovación y celular compartido → semilla sesión |

### §3 Variables
Se quedan los topes del mensaje, los plazos y el p95. Salen `edad_vieja`, `ventana_retencion` y el umbral de `hora_dudosa` → semilla configuración (y su uso → semilla ingreso); la granularidad D1 → semilla entidad.

### §4 Casos
Se apartaron 18 de 80. A la semilla ingreso: C-5, C-33, C-37, C-38, C-60, C-61, C-70, C-71, C-72, C-81. A la semilla celular: C-39, C-39b, C-41, C-42, C-58, C-62. A la semilla sesión: C-67, C-80. Se quedaron los que muestran una respuesta o la reacción del celular a una respuesta (por ejemplo C-29, C-30, C-36, C-55, C-59, C-74, C-75), aunque citen lógica de otro elemento.

### §6 Pendientes
| Sale a | Ids |
|---|---|
| semilla ingreso | COH-26, COH-52, COH-61, COH-62, COH-64, K-82 |
| semilla sesión | COH-5, COH-42, COH-43, COH-66, K-2, K-4, K-59, K-75 |
| semilla persistencia | COH-27, COH-67 |
| semilla celular | COH-44 |
| preguntas para Juan | K-28, K-40, K-41 (más copias de K-75, COH-61, COH-64) |
| **se quedan** | COH-63, K-80, K-81 |

### §7 Fuera del dominio
Se vacía entero: cada viñeta tiene ya su semilla o su documento.

### Lo que no estaba en la ESPEC (destilado, HISTORIA)
| Pieza | Destino |
|---|---|
| «Personalizables» (mínimo por actividad) | semilla configuración · preguntas §A |
| Circunstancias de un dato faltante (solo en la HISTORIA §7) | `RF-P01` · `ES-P03` |
| Criterio de confiabilidad de Juan (16-oct 20:31) | `ESC-P01` |
| Turnos y reasignación | semilla entidad · `RF-P04` · `ES-P01` |
| Agregados del agente sin fuente (ADR-007, C-62, códigos de K-27) | preguntas §C |
| Enlace al mazo del contrato | preguntas §E |

## 4 · Poda · **aplicada el 18-oct-2026 por pedido de Juan**
La ESPEC pasó de 517 a 452 líneas. La copia íntegra anterior está en `ESPEC-contrato-ingesta-HISTORIA-1g-1h.md`. Además del inventario de §3, la poda quitó de A-41 «la ventana de retención no corre» (agregado del agente, ver preguntas §C) y la fila del par cumple/viola de A-43.

La ESPEC quedó con: Ficha, §1, §2 con las filas que se quedan y las partes de forma de las filas partidas, §3 sin los plazos de negocio, §3b entero, §4 con los casos de cable, §4b entero, §5 sin la tabla «Fuera del borde» (pasa a ser una línea por semilla), §6 con COH-63, K-80 y K-81, y §7 con un enlace a este índice.

Efecto esperado: el informe de agilidad (`mejora/02-informe-agilidad.md`) estimó **≈86 %** contando solo los temas del contrato. Es una estimación; el índice oficial lo vuelve a calcular el verificador de detallar-para-codificar después de la poda. La HISTORIA no se poda: es registro.

**Siguiente:** Juan invoca detallar-para-codificar; su verificador recalcula el índice sobre la ESPEC podada.
