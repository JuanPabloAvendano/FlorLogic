# La captura tardía de una orden ya cumplida no se guarda · queda una constancia
Qué es: recreación, con el formato de `decisiones/`, de una decisión que Juan tomó al detallar el contrato de ingesta.
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md` (A-45 ⟦1⟧, K-40, C-37) y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `ADR-PoC-Spikes.xlsx` · `DRIVERS_ARQUITECTONICOS.md` · la palabra de Juan.

> **DECISIÓN DE JUAN · 16-oct-2026 (ronda 1e P3; turnos dichos a las 20:31 UTC)**
> Estado: **DECIDIDA · PENDIENTE DE ADR**
> Va al ADR: ______________
> Toca: `ADR-027` §2 · `ADR-024` §1 · `ADR-026` · `ESC-34` · `ESC-58` · `CN-24` · `RF-022`

## 1 · Lo decidido
Si una cama se reasignó al turno siguiente y ese turno ya la capturó y sincronizó, la captura que
llega tarde de la primera asignación **no se guarda como dato**: queda una **constancia mínima** con
quién la capturó, qué cama, qué jornada y qué captura la superó. Juan eligió «constancia mínima» y
no «descartar totalmente».

## 2 · Los motivos
Unas 30 personas trabajan en dos turnos; lo que no sincronizó la mañana se reasigna a la tarde, y la
misma orden puede aplicarse dos veces el mismo día. Guardar las dos convertiría una reasignación
normal en un conflicto.

## 3 · Lo que se precisó después (rondas 1f a 1h)
- Solo aplica cuando la orden se **reasignó**; en cualquier otro caso decide RF-022.
- La posterior tiene que ser **verificada** (sin ninguna marca); si no, entran las dos marcadas con `orden_duplicada`.
- Si la vieja ya estaba guardada pasa a **superada**, y su constancia conserva los bytes.

## 4 · Lo que choca
Según K-40: `ESC-34` («conserva ambas versiones», «0 registros descartados automáticamente»), `ADR-027` §2 y `CN-24`; y para la superada, `ADR-024` §1 y `ESC-58`. Lo ajusta Juan.
