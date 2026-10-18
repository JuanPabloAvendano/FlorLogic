# SEMILLA · endpoint · abrir y cerrar la sesión de sincronización (ronda 2)
Qué es: lo que se aclaró sobre la sesión de sincronización, la credencial y el celular compartido mientras se detallaba `POST …/particiones`, apartado para la ronda 2.
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md`, su HISTORIA y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `ADR.xlsx` (ADR-007, ADR-028, ADR-035) · `IA_00-LEEME-transacciones.md` (P-N01) · la palabra de Juan.

## Ficha
| Campo | Valor |
|---|---|
| Elemento | `POST /ingesta/v1/sesiones` (abrir), `…/cierre`, `…/archivos`; y el registro de un dispositivo (`registrar_de_nuevo`) |
| Plantilla | `endpoint`. **La sesión es la parte 2 de `ESPEC-contrato-ingesta.md`** (ronda 1i P8, 18-oct); el registro de dispositivos sigue como detallamiento aparte |
| Definición propuesta | La sesión de sincronización es la visita de un celular a la oficina, a nombre de una persona: la abre, recibe lo que el nodo de finca anuncia, entrega particiones y la cierra. *Sin convergencia con Juan* |

## Lo que ya se sabe
| Origen | Regla | Fuente |
|---|---|---|
| A-5 | La crea el nodo de finca; lleva dispositivo (`uuid`, `registro_id`), usuario, versiones de configuración, conteos y resultado; ninguna queda abierta | ADR-028 (supuesto por fuente) |
| A-40 ⟦1⟧ | Una sesión activa por celular | supuesto |
| A-51 ⟦1⟧ | Login local en el celular: la identidad está presente al capturar sin red | Juan, 16-oct 22:22 |
| A-51 ⟦3⟧ | En la oficina se renueva la credencial antes de sincronizar; la persona verificada con ella es la dueña de la sincronización. La sesión es de la persona, no de la credencial: renovar no la rompe | ronda 1g P9 (COH-41) |
| A-51 | **Celular compartido:** «cada operario ingresa con su cuenta única, esté o no sincronizando» (Juan, 17-oct 03:14). Entrar con otra cuenta cierra la sesión anterior como `parcial`; la nueva sube todo lo pendiente del celular (los autores no cambian) y después baja el catálogo y la asignación de esa persona | ronda 1h P6 (K-69, COH-55) |
| A-31, COH-54 | Al abrir, el celular declara el último `respaldado_hasta` que recibió | ronda 1h P9 |
| A-31, COH-5 | Las marcas de agua viajan también al abrir la sesión | propuesta |
| §3 | El servidor anuncia al abrir los topes (`max_anotaciones_particion`, `max_bytes_*`, `max_profundidad_anotacion`, `max_largo_cadena`) | rondas 1, 1c, 1h |
| A-39 | Registro de un dispositivo nuevo tras `registrar_de_nuevo`: nuevo `registro_id`, marcas desde 0 | ADR-027 §5 (deprecado) · ADR-007 |

## Casos que ya existen
- Apartados aquí (texto en `ESPEC-contrato-ingesta-HISTORIA-1g-1h.md` §4): C-67, C-80.
- Siguen en la ESPEC del contrato: C-11, C-12, C-74. En `IA_TX1`: ES-S02 y ES-S12.

## Abierto
- **K-75 (decide Juan)** · P-N01 (aceptado el 13-oct) da 24 h + 7 días de gracia **solo para capturar**; la ESPEC dice que el acceso en campo «nunca se pierde». Juan dijo «no se debería perder el acceso **totalmente**» y «**si es necesario** verificación nuevamente»: cabe en una gracia acotada.
- **K-59 / COH-42** · ADR-007 descarta la sesión offline sin caducidad. La frase «Decisión de Juan: ajustar ADR-007 y BR-N5» no tiene fuente en la conversación: confirmar.
- **Atar la sesión al celular** (de la parte 1, ronda 1j P4, COH-77/K-89): cómo sabe el nodo de finca desde qué celular llega cada partición (cabecera, claim del JWS u otra forma) y si otro dispositivo puede usar la sesión (`403 sesion_ajena`). Toca ADR-007 si va en la credencial.
- **COH-66** · si otra persona entra mientras una partición está en proceso, ¿el cierre espera al candado (≤ 25 s)?
- **COH-43** · campo con la edad que declara el celular para el tablero.
- **K-2** · resultados de la sesión: ADR-028 dice `completada · parcial · fallida`, `IA_TX1` dice `completa · parcial`.
- **K-4** · el texto de ADR-035 (sesión de captura) no está en los `.md`.
- La ficha del contrato dice «4 POST» pero solo detalla `particiones`.
