# Pendientes · contrato de ingesta (TX-1.K)
Qué es: preguntas abiertas, rondas sin cerrar y el índice de precisión en que quedó el detallamiento.
Quién: IA (Claude), 2026-10-17, destilado de las sesiones b7e68fbd / d4fb0ea1.
Estado: SIN REVISAR
Manda por encima: `ESPEC-contrato-ingesta.md`, `~/.claude/skills/detallar-para-codificar/SKILL.md` y la palabra de Juan.

## Dónde quedó
- Etapa 3 (Preguntas). Se cerró la **ronda 1h**. La **1i quedó sin empezar**: Juan aceptó empezar con K-75 (17-oct 05:17 UTC), pero se agotó el límite de uso y su «continúa» de las 12:06 tampoco corrió.
- Índice oficial **≈64,5 %** (umbral 85 %). La coherencia (D-04) **no pasa**: COH-61 es bloqueante. Proyección del verificador: ≈88 %.
- Historia del índice: 52 · 59 · 68 · 60 · 60 · 62 · 62 · 63 · 64,5 (rondas 1 a 1h, con la consolidación entre 1f y 1g).
- Lectura del informe de agilidad (`mejora/02-informe-agilidad.md`): contando solo los temas del contrato, la ESPEC ya estaría en **≈86 %**. El resto son problemas de otros elementos.

## Ronda 1i prevista (orden propuesto por el agente)
1. **K-75** · P-N01 (24 h + 7 días de gracia) frente a A-51 ⟦2⟧ («nunca se pierde»). **Decide Juan.** Pista: su frase dice «no totalmente» y «si es necesario» (ver `IA_decisiones-de-juan.md` §A).
2. **COH-61** · ¿manda la persona o el dispositivo? (celular compartido con reasignación). **Bloqueante.**
3. COH-62 (los bytes en toda constancia) · 4. COH-63 (sesión no declarada = defecto del sobre, no se borra)
5. **COH-64** · `fuera_de_asignacion` frente a RF-022. **Decide Juan.**
6. COH-66 (cambio de cuenta con una partición en vuelo) · 7. COH-67 (cuarentena y restauración)
8. K-80 / K-81 (desconocidos y fronteras) · 9. K-82 (de quién son las anotaciones reenviadas) · 10. Los pares cumple/viola de A-2, A-13, A-19, A-20, A-22, A-44 y A-49 ⟦4⟧.

## Abiertos que no están en la 1i (detalle en ESPEC §6)
- Para la ronda 2 (abrir y cerrar la sesión, `/archivos`): COH-5, COH-43, K-2 y los otros 3 POST. La ficha dice «4 POST», pero solo se detalla `particiones`.
- Menores: COH-26, COH-27, COH-44, COH-52.
- **Solo Juan:** K-4 (texto de ADR-035), K-28 (ADR-028 §3), K-40 (ADR-027 §2, ESC-34, ADR-024 §1, ESC-58), K-41 (marcar ADR-027 como deprecado en `ADR.xlsx`), K-59 (ADR-007, BR-N5).
- Pendientes de Juan en ESPEC §3: el umbral de `reloj_movido` y el de `hora_dudosa`, la granularidad (sesión D1) y la zona horaria de `jornada` (P-S04).

## Abiertos que no figuran en la ESPEC
- Quién configura el **mínimo requerido** por actividad («personalizables»). No tiene dueño.
- Las circunstancias en que falta un dato (cama, bloque, lote, celular entero): solo en la HISTORIA.
- Origen de «Decisión de Juan: ajustar ADR-007 y BR-N5»: no aparece en b7e68fbd. Confirmar.
- `C-62`/A-50, «la ventana no corre»: Juan no lo dijo. Confirmar o quitar.
- Códigos `desfase_desconocido` y `capturada_sin_credencial`: los infirió el agente. Confirmar.
- Huecos de cobertura de `endpoint` según el informe: JSON Schema, `400`/`415`, el mismo `particion_uuid` con otro cuerpo, el generador de UUID v7.

## Antes de programar
- TX-1.K, P-S04 y P-S05 siguen `POR ACEPTAR` (CLAUDE.md: parar y preguntar).
- Ningún código del contrato se escribió. Docker: no aplica todavía.
