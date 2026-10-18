# SEMILLA · entidad y agregado · lote, sección, cama y orden de captura
Qué es: las aclaraciones de negocio de Juan sobre qué se captura y a quién se le asigna, apartadas de la ESPEC del contrato.
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md` (A-20, A-30, A-45), su HISTORIA y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `ADR.xlsx` (ADR-024, ADR-026) · `DRIVERS_ARQUITECTONICOS.md` · `codigo/IA_DICCIONARIO.md` · la palabra de Juan.

## Ficha
| Campo | Valor |
|---|---|
| Elemento | Dominio de producción: `Lote`, `Seccion`, `Cama`, `Produccion` y la orden de captura (asignación ADR-026) |
| Plantilla | `entidad-agregado` |
| Definición propuesta | Qué es cada unidad de la finca, cuál se captura y cómo nace la orden que dice qué cama revisa quién y qué día. *Sin convergencia con Juan* |

## Lo que ya se sabe (palabras de Juan)
| Fecha | Aclaración | Dónde quedó |
|---|---|---|
| 16-oct 17:43-17:45 | El lote es «una variedad para producción, en un periodo fenológico»; «no se va a trabajar por semanas con número, sino con periodos y fechas reales»; «no se captura el lote, se capturan las camas» | A-30 |
| 16-oct 19:02 | El lote es «una plantación definida de una variedad, que puede estar en varias secciones»; no contradice ADR-024 §2 (agrupación transversal) | A-30 (K-20) |
| 16-oct 19:35 | La jornada: al operario «le mandan lo que tiene que ir a mirar», captura sin internet y sincroniza en la oficina | A-46 |
| 16-oct 20:31 | Unas 30 personas en dos turnos (6 a 2-3 y 2-4 a 10-11). Lo que no sincronizó el turno de la mañana se reasigna al de la tarde; la misma orden puede aplicarse dos veces el mismo día | A-45 ⟦1⟧, C-37 |
| 17-oct 03:17 | La orden se deduce «del dispositivo… o el usuario al que se le asignó» | A-45 ⟦1⟧ (sin decidir: COH-61) |

## Lo que dice el contrato y depende de esto
- A-20: en la v1 solo se anota a nivel de **sección**; cama, bloque y lote llegarán en una versión mayor cuando la sesión **D1** con el cliente decida la granularidad.
- A-45 ⟦1⟧: la orden de captura = cama + jornada + a quién se asignó; sección → cama por el catálogo.

## Abierto
- **Granularidad (D1):** sección, cama, bloque o lote. La decide Juan con el cliente.
- **COH-61:** la orden pertenece a la persona o al dispositivo.
- Zona horaria y corte de la `jornada` (P-S04), con turnos que terminan a las 10-11 p. m.
