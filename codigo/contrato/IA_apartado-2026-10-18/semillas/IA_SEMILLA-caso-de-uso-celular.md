# SEMILLA · caso de uso · el celular (simulador) guarda, retiene, borra y reintenta
Qué es: el comportamiento del celular que se decidió mientras se detallaba el contrato, apartado para la ronda del simulador.
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md`, su HISTORIA y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `ADR.xlsx` (ADR-024, ADR-025) · `IA_00-LEEME-transacciones.md` (P-S01, P-S03) · la palabra de Juan.

## Ficha
| Campo | Valor |
|---|---|
| Elemento | Módulo `simulador` (en producción, la app del celular): almacén de salida, borrado y reintentos |
| Plantilla | `caso-de-uso` (del lado del celular) |
| Definición propuesta | Qué hace el celular con lo capturado desde que el operario lo guarda hasta que lo borra: no dejar guardar sin el mínimo, editar antes de enviar, retener, borrar solo lo respaldado y reintentar según el error. *Sin convergencia con Juan* |

## Lo que ya se sabe
| Origen | Regla | Fuente |
|---|---|---|
| A-46 ⟦1⟧ | Sin el mínimo requerido de su actividad, el celular no deja guardar la captura | ronda 1d P5 · ADR-024 |
| A-49 ⟦2⟧ | Edición local: el operario cambia en su lugar lo que todavía no salió (mismo id; solo viaja la versión final) | ronda 1e P4 |
| A-49 ⟦3⟧ | Desde que sale es inmutable: «eso es otra captura nueva de la cama» (Juan, 16-oct 22:02) → id nuevo | ronda 1f P1 |
| A-48 | **Única regla de borrado:** ⟦1⟧ lo acusado se borra cuando su consecutivo ≤ `respaldado_hasta`; ⟦2⟧ un fallo técnico o un descarte del gerente borra ya y deja lápida `{consecutivo, motivo}`; ⟦3⟧ si una marca baja, reenvía lo retenido y las lápidas; ⟦4⟧ nada más se borra | ronda 1e P2 |
| A-31 | Lo confirmado y aún no respaldado se retiene y queda «inaccesible por usuarios operarios de campo» (Juan, 16-oct 17:52) | ronda 1b P5 |
| A-45 ⟦3⟧ | Lo no entregado nunca se borra solo: a los 3 días escala el aviso; a los 7 la cama sale en el tablero del gerente. Los días los cuenta el celular tras cada respuesta normal | rondas 1e P1, 1f P10 |
| A-40 ⟦4⟧ | Espera `min(2^n · 1 s, 5 min) + azar(0..1 s)`; tras 8 intentos avisa al operario y sigue cada 5 min; la espera es la mayor entre esa y `Retry-After` | ronda 1c P8 |
| A-41 | Qué hace según el error (413 parte en dos, todo `422 sobre-invalido` rearma una vez y nunca quita por el `pointer` — ronda 1k P3 —, 403 no reintenta, etc.). **Se queda también en el contrato**, porque es la otra mitad del error | rondas 1c-1h |
| A-41 (1j P2) | ~~Ante `json_ilegible`, leer la señalada antes de quitarla~~ **Reemplazada** por A-53 (ronda 1k P1): nunca se quita por el `pointer` | ronda 1j P2 → 1k P1 |
| A-53 | **Validar al armar:** sobre con `JsonGenerator`; cada anotación leída con `readTree`, los topes del nodo de finca y `FAIL_ON_TRAILING_TOKENS`; la partición entera validada con `particion.schema.json` y sus sesiones declaradas antes del POST. La anotación que no se lee es fallo técnico local (lápida y aviso en `descartes`) | ronda 1k P1-P2 |
| A-53 (contrato) · A-56 | Usa `ValidadorContrato` del módulo `contrato` (no una copia), con los topes de la sesión vigente; el aviso en pantalla es para lo que solo el celular ve, una vez | segunda poda 18-oct (Juan) |
| A-41 (1k P3-P6) | Recordar en el almacén que ya rearmó una partición; al segundo `422 sobre-invalido` retenerla, seguir con las demás y **avisar en pantalla** («partición retenida: avisa al administrador»). Es el **único** aviso cuando el rearmado sale idéntico (el nodo de finca no alerta: 1k P5; el contrato no lo informa: 1k P6) | ronda 1k P3-P6 (Juan) |
| A-51 ⟦2⟧ | Con la credencial vencida en campo sigue capturando con el login local (ver K-75) | ronda 1f P9 |

## Casos que ya existen
- Apartados aquí (texto en `ESPEC-contrato-ingesta-HISTORIA-1g-1h.md` §4): C-39, C-39b, C-41, C-42, C-58, C-62.
- Apartados en la segunda poda (texto en `ESPEC-contrato-ingesta-HISTORIA-1i-1k.md` §4): **C-83** (la 38 con `…}}` no viaja: `readTree` con `FAIL_ON_TRAILING_TOKENS` falla, sale en `descartes`) y **C-85** (sesión S9 no declarada: el celular la agrega antes de enviar).
- Siguen en la ESPEC del contrato, porque son la reacción a una respuesta: C-36, C-43, C-47, C-52, C-63, C-68, C-69, C-84, C-86.

## Abierto
- **K-107** (segunda poda) · con A-53 ⟦1⟧, una anotación de forma inválida (C-10, C-19) no viaja: se trata como fallo técnico local. Confirmar en el código del simulador.
- **K-108** (segunda poda) · validar con los topes que anunció la sesión vigente (A-52), no con los del esquema estático.
- **K-110** (segunda poda) · tras `403 ingesta_entregar` (permiso del rol), renovar la credencial no lo arregla. Regla general A-56: un intento por credencial nueva, sin bucles. ¿Tope por jornada?
- **COH-94** (segunda poda) · ¿el rearmado antes de enviar gasta el «una vez»? Tras actualizar la app, ¿se olvida «ya rearmé» y se reintenta con el mismo `particion_uuid`? La retención antes de enviar también es invisible para la oficina (riesgo de la 1k P6).
- **COH-44** · al partir una partición, cada mitad lleva las `sesiones_captura[]` y `primeros_acuses[]` de sus anotaciones; ¿a qué mitad van los `descartes`?
- **C-62 / A-50** · «la ventana de retención no corre» con `contrato-incompatible`: **inferido por el agente, Juan no lo dijo**. Confirmar o quitar.
- El momento de soltar: Juan eligió «suelta al respaldar», aunque su texto decía «cuando se termina la jornada» (16-oct 19:35). Confirmar.
