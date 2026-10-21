# SEMILLA · adaptador de persistencia · guardar lo que entra por la ingesta
Qué es: lo que se dijo sobre la base de datos mientras se detallaba el contrato, apartado para su propio detallamiento.
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md` y su HISTORIA.
Estado: SIN REVISAR
Manda por encima: `ADR.xlsx` (ADR-024, ADR-028) · `IA_TECNOLOGIAS-vigentes.md` · la palabra de Juan.

## Ficha
| Campo | Valor |
|---|---|
| Elemento | Adaptador de PostgreSQL del ingreso en `backend-finca` (capa `infraestructura`) |
| Plantilla | `adaptador-persistencia` |
| Definición propuesta | Guarda anotaciones, constancias, cuarentena y bitácora, de forma idempotente y atómica, y cumple el plazo de procesamiento. *Sin convergencia con Juan* |


## Punto de partida: el canal ya implementado (traspaso D-19, 20-oct-2026)

Esta semilla **no arranca desde cero**. El Segmento 2 de `ESPEC-contrato-ingesta.md` (el canal de TX-1, camino mínimo
D-17) ya escribió, probó y verificó una versión mínima de este elemento. Su ESPEC parte de ese código y lo hace crecer.
Detalle: ESPEC-contrato-ingesta §12 (nota de traspaso) y §10-§11 (observaciones y verificación del Segmento 2).

| Archivo ya escrito | Qué hace hoy |
|---|---|
| `backend-finca/src/main/resources/db/migration/V2__anotacion_recibida.sql` | Tabla `anotacion_recibida` (llave `anotacion_uuid`, `contenido jsonb`). Migración aplicada: no se edita (PR-14) |
| `…/ingesta/servicios/puertos/salida/AnotacionesRecibidasPuertoSalida` | Puerto de salida `guardarSiNueva` |
| `…/ingesta/infraestructura/salida/persistencia/AnotacionesRecibidasAdaptadorJdbc` | `INSERT … ON CONFLICT DO NOTHING` con `JdbcClient` |
| Pruebas: `AnotacionesRecibidasPuertoSalidaContratoTest` (abstracta), `…EnMemoria` (doble), `…EnMemoriaTest`, `…JdbcTest` | Contrato del puerto heredado por la memoria y por PostgreSQL real |

Viajan con el traspaso: **A-59**, **COH-104** resto (clave repetida y decimal largo), **COH-105** (cumplida: U+0000 se
rechaza antes de la base), **COH-106** (el contrato del puerto solo compara el boolean; falta «guardar y recuperar
igual» y un doble con rollback), OBS-2-9 (paquete `salida/persistencia` + regla ArchUnit, Segmento 7).

## Lo que ya se sabe
| Origen | Regla | Fuente |
|---|---|---|
| A-40 ⟦5⟧ | Inserción idempotente y atómica: `INSERT … ON CONFLICT (anotacion_uuid) DO NOTHING` y después comparar la huella | ronda 1c P8 · ADR-028 §4 |
| A-2 | El `anotacion_uuid` es único **entre anotaciones y constancias** (un índice que vea las dos) | ronda 1g P10 (COH-37) |
| A-2 | Cuarentena: los bytes de la versión corrupta se guardan fuera del estado | ronda 1h P9 (COH-57) |
| A-45 ⟦1⟧ | Una superada sale del estado y de la historia; su constancia conserva los bytes. Falta decir cómo se retira su dato | ronda 1h P9 (COH-58) |
| A-40 ⟦2⟧ | Procesar ≤ 20 s < candado por dispositivo 25 s < espera del celular 30 s | ronda 1d P8 |

## Abierto
- **COH-27** · el aborto a los 20 s solo es real si se cancela la transacción. Propuesta: `statement_timeout` menor que el plazo, con prueba.
- **COH-67** · ¿la cuarentena entra en el respaldo de TX-2?
- **COH-62** · ¿toda constancia guarda los bytes?
- **Docker:** nada de esto se puede probar contra PostgreSQL sin Docker (Testcontainers).
