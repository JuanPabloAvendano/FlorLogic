REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 2-nov-2026, a pedido de Juan (manual de la demostración IS II) · actualizado el 2-nov-2026 por el orquestador (iteración 2)
Estado de revisión: SIN REVISAR
Manda por encima: `codigo/IA_LEEME.md` · `codigo/despliegue/compose.yaml` · la palabra de Juan

# 07 · git, solo para mirar

Los commits y empujes los hace Juan (regla PR-05 del proyecto: la IA no hace commit). Estas órdenes solo leen.
Se corren desde cualquier carpeta del repo. Las de git no se probaron (las pruebas corren en una copia sin git).

| Orden | Qué hace | Necesidad |
|---|---|---|
| `git status -s` | qué cambió sin commit (`M` modificado, `D` borrado, `??` nuevo) | ÚTIL |
| `git log --oneline -15` | últimos 15 commits, uno por línea | ÚTIL |
| `git log --format='%h %an %ad %s' --date=short -10` | quién hizo cada commit: evidencia de que los commits son humanos (IA responsable, área 1.8) | ÚTIL |
| `git diff -- <archivo>` | qué cambió en un archivo y aún no tiene commit | ÚTIL |
| `git restore <archivo>` | **escribe**: descarta los cambios de un archivo (deshacer un daño de la hoja 09) | RARA · Motivo de necesidad: reparar un ejercicio de la hoja 09 sin copia previa; lo decide Juan, porque pisa el archivo de trabajo |
| `git show <commit> --stat` | qué archivos tocó un commit | ÚTIL |
| `git fetch origin documentacion-desarrollo` y `git show origin/documentacion-desarrollo:README.md \| less` | leer el README de la otra rama (ADR-001…037 en su §4, C4, secuencias) sin cambiar de rama: evidencia del área 1.1 | ESENCIAL para gobierno arquitectónico |
| `git log -p --follow -- <archivo>` | historia completa de un archivo, aunque se haya renombrado | RARA · Motivo de necesidad: rastrear por qué cambió una decisión en un archivo |
| `git blame <archivo>` | quién y cuándo cambió cada línea | RARA · Motivo de necesidad: igual que la anterior, línea por línea |
| `head -4 <archivo IA_….md>` · en scripts `head -5 <archivo IA_….sh>` | la cabecera de cuatro líneas de todo lo que generó la IA (qué es, quién, estado de revisión, qué manda). En un `.sh` la línea 1 es el `#!/usr/bin/env bash` y la cabecera va de la 2 a la 5: `head -4` la corta | ÚTIL (IA responsable y validación humana, área 1.8) |

- Riesgo: ninguno, salvo `git restore` (pisa cambios sin commit)
- Probado: las de git, sin probar · `head -4` en `scripts/IA_probar-canal.sh`: 2-nov-2026 · iteración 2 · falló: cortó la cabecera porque la línea 1 es el `#!` (fila 132); `head -5` sin probar
