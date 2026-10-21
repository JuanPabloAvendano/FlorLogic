# Preguntas para Juan · lo que salió del contrato de ingesta y solo él puede cerrar
Qué es: las decisiones y confirmaciones pendientes que no tienen lugar en ninguna plantilla ni documento, apartadas de la ESPEC de TX-1.K.
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md` §6, su HISTORIA y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `ADR.xlsx` · `IA_00-LEEME-transacciones.md` · la palabra de Juan.

## A · Decidir
| Pregunta | Por qué importa | De dónde sale |
|---|---|---|
| ¿La gracia de 7 días de P-N01 es el límite del acceso en campo, o se reabre P-N01? | La ESPEC dice «nunca se pierde»; tus palabras («no totalmente», «si es necesario») caben en la gracia | K-75 · A-51 ⟦2⟧ |
| ¿Manda la persona o el celular en la orden de captura? | Bloquea la coherencia del contrato | COH-61 |
| ¿Una captura asignada gana a una no asignada aunque sea más vieja? | Sería una excepción a RF-022 | COH-64 |
| ¿«Corrige solo el gerente de producción» reemplaza a `RF-017` (administrador de la empresa)? | Son roles distintos en DRIVERS | A-49 · `RF-P03` |
| ¿Quién configura el mínimo requerido por actividad («personalizables»)? | No tiene dueño | A-46, 16-oct 19:35 |
| ¿Cada cuánto se prueba la restauración? | De eso depende cuánto retiene el celular | K-67 · `ESC-P03` |

## B · Ajustar en el libro de ADR (solo Juan)
- Marcar ADR-027 como deprecado y decidir qué reemplaza §1-§5 (K-41).
- ADR-027 §2, ESC-34, ADR-024 §1 y ESC-58 frente a la obsoleta y la superada (K-40).
- ADR-028 §3: sello tras restaurar (K-28).
- ADR-007 y BR-N5 frente al acceso en campo (K-59, COH-42).
- Pasar a los `.md` el texto de ADR-035 (K-4).

## C · Confirmar lo que el agente agregó
- «Decisión de Juan: ajustar ADR-007 y BR-N5»: no aparece en la conversación b7e68fbd.
- «La ventana de retención no corre» con `contrato-incompatible` (C-62, A-50): no lo dijiste.
- Los códigos `desfase_desconocido` y `capturada_sin_credencial` (K-27): explicaste para qué sirve una marca, no aprobaste esos dos.
- «Suelta al respaldar» frente a tu texto «cuando se termina la jornada» (A-46).

## D · Pendientes de antes
- Umbrales de `hora_dudosa` y `reloj_movido`; granularidad D1; zona horaria de `jornada` (P-S04).
- TX-1.K, P-S04 y P-S05 siguen `POR ACEPTAR`.

## E · Material visual (privado en claude.ai)
- Comportamiento esperado del contrato: https://claude.ai/artifact/6FFzNX26NYEfGDrwM8DJKR (hecho con la ESPEC del 16-oct; puede estar desactualizado).
