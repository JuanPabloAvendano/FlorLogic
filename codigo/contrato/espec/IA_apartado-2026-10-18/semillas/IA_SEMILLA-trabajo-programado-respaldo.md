# SEMILLA · trabajo programado · respaldo, prueba de restauración y `respaldado_hasta` (TX-2)
Qué es: lo que el contrato de ingesta le exige a TX-2, apartado porque Juan lo marcó «fuera de contexto» (16-oct 22:47).
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md` (A-31, §7), su HISTORIA y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `IA_TX2-respaldo-y-observabilidad.md` · `IA_00-LEEME-transacciones.md` (P-R01, P-S03) · la palabra de Juan.

## Ficha
| Campo | Valor |
|---|---|
| Elemento | Trabajo de respaldo nocturno y su prueba de restauración, en `backend-finca` |
| Plantilla | `trabajo-programado` |
| Definición propuesta | Cada noche respalda la base, la verifica, la prueba restaurándola y publica hasta qué consecutivo de cada celular quedó a salvo en las dos copias. *Sin convergencia con Juan* |

## Lo que ya se sabe
| Origen | Regla | Fuente |
|---|---|---|
| A-31 | `respaldado_hasta` = mayor consecutivo de cada dispositivo dentro de un respaldo verificado, que pasó la prueba de restauración y está en la copia local **y** en la nube (el mínimo de las dos). Invariante `respaldado_hasta ≤ confirmado_hasta` | ronda 1c P6, 1g P4 |
| A-31 | El celular borra solo lo que está respaldado «de forma automática o constante, física o en la nube» (Juan, 16-oct 17:52) | ronda 1b P5 |
| A-31 | Si `respaldado_hasta` baja tras una restauración, se alerta (`respaldo_retrocedio`) y se calcula la posible pérdida por celular | rondas 1d P4, 1g P4 |
| A-31 | `respaldado_hasta` avanza tarde: al menos tras el respaldo nocturno (P-R01) | HISTORIA A-31 |

## Abierto
- **K-67 · frecuencia de la prueba de restauración.** Juan: «fuera de contexto, esto es respaldos… estamos en sincronización». Propuesta del agente, sin decidir: probar cada respaldo nocturno; con la prueba mensual de ESC-19 el celular retendría hasta un mes.
- **COH-40** · respaldar también los paquetes de configuración.
- **COH-67** · respaldar la cuarentena.
- **COH-52** · qué estado del ingreso sobrevive a una restauración.
