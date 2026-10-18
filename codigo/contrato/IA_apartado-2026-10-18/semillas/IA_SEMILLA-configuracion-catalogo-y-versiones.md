# SEMILLA · configuración · catálogo, mínimos, umbrales y ejes de versión
Qué es: lo que se aclaró sobre la configuración de la finca mientras se detallaba el contrato, apartado para la ronda de `configuracion`.
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md`, su HISTORIA y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `ADR.xlsx` (ADR-025, ADR-029) · `docs/03-arquitectura/decisiones/2026-09-15-reglas-en-json-y-captura-incompleta.md` · la palabra de Juan.

## Ficha
| Campo | Valor |
|---|---|
| Elemento | Paquete de configuración (catálogo + reglas, ADR-029) y los parámetros de negocio de la sincronización |
| Plantilla | `configuracion` |
| Definición propuesta | Lo que la finca ajusta sin publicar una versión de la app: qué se captura en cada actividad, cuál es su mínimo, sus motivos, sus reglas y los plazos. *Sin convergencia con Juan* |

## Lo que ya se sabe
| Origen | Regla | Fuente |
|---|---|---|
| A-50 | **El nodo de finca crea todo lo que llega al celular.** Cuatro ejes de versión independientes: despliegue/app, motor, configuración (catálogo y reglas en un solo paquete) y contrato. Un celular nunca tiene una versión que el nodo de finca no tenga | ronda 1g P7 · Juan, 16-oct 22:10 y 22:52 |
| A-46 | Mínimo por captura de cama: «pocos elementos a llenar por captura de cama, **personalizables**» (Juan, 16-oct 19:35) | no aparece en ningún documento |
| A-35 | Los motivos de ausencia y de desviación viven en el catálogo versionado (CN-36) | rondas 1b, 1c |
| A-3 | Cada campo declara en el catálogo su clave del hecho | ronda 1d P2 |
| §3 | `edad_vieja` = 3 días, `ventana_retencion` = 7 días (provisionales; Juan: lo viejo «es más fácil volverlo a capturar», conservar 7 «para que el gerente lo revise», 16-oct 19:26) | ronda 1d P3 · ADR-025 |

## Abierto
- **¿Quién configura el mínimo requerido y dónde?** «Personalizables» no tiene dueño: ¿el administrador de la empresa, por actividad, en el catálogo? (RF-013, RF-020)
- **¿3 y 7 días por tipo de campo?** ADR-025: «un corte no se recaptura».
- Umbral de `hora_dudosa` (IA_TX1 P-S09 propone 60 s) y `umbral_reloj_movido`: pendientes de Juan.
- Formatos de los ids de negocio (`produccion_id`, `seccion_id`, `campo_id`, `persona_id`): supuestos.
- **COH-40** · el paquete de configuración tiene que sobrevivir a una restauración (va con TX-2).
