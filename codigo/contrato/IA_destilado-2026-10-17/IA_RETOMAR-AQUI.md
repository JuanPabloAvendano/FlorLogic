# RETOMAR AQUÍ · detallamiento del contrato de ingesta
Qué es: lo mínimo para seguir el detallamiento de TX-1.K en una sesión limpia, sin reanudar la conversación b7e68fbd (unos 650 mil tokens).
Quién: IA (Claude), 2026-10-17, destilado de las sesiones b7e68fbd / d4fb0ea1.
Estado: SIN REVISAR
Manda por encima: `ESPEC-contrato-ingesta.md`, `~/.claude/skills/detallar-para-codificar/SKILL.md` y la palabra de Juan.

## Estado en una línea
Ronda 1h cerrada · ≈64,5 % (umbral 85 %) · coherencia no pasa (COH-61) · la 1i nunca empezó · no hay código.

## Qué leer, en este orden
1. `codigo/contrato/ESPEC-contrato-ingesta.md`: la Ficha (sobre todo «Dónde retomar») y §2, §6 y §7. **No** leer la HISTORIA salvo para un id concreto.
2. `codigo/contrato/IA_destilado-2026-10-17/IA_pendientes.md`: el orden de la ronda 1i.
3. `codigo/contrato/IA_destilado-2026-10-17/IA_decisiones-de-juan.md` §A: lo que Juan dijo y no quedó, o quedó endurecido.
4. `codigo/IA_DICCIONARIO.md`: solo los términos que vayan a salir en la pregunta.
5. Lo carga la skill: `~/.claude/skills/detallar-para-codificar/SKILL.md`.

## Decisión previa (de Juan, antes de la 1i)
El 17-oct (03:41 UTC) Juan pidió delimitar el contexto: lo que no es del contrato va a una hoja de su contexto. Eso **solo existe en el laboratorio** (`~/Work/laboratorio-skills/versiones/v5`), no en la skill real.
- **Opción A:** seguir la 1i con la skill actual. Es lo que pide la Ficha.
- **Opción B:** pasar antes la v5 a la skill real (ver `~/Work/laboratorio-skills/IA_destilado-2026-10-17/IA_RETOMAR-AQUI.md`) y sacar de la ESPEC lo que no es del contrato a hojas (`IA_fuera-de-contexto.md` es la semilla). Según el informe de agilidad, eso solo deja el índice cerca del 86 %.

## Por dónde seguir (opción A)
1. Invocar `/detallar-para-codificar` con «sigamos con el detallamiento del contrato de ingesta».
2. Ronda 1i, P1 = **K-75**: la gracia de la credencial. Ofrecer como opción la lectura literal de Juan: no perder el acceso «totalmente», que es compatible con una gracia acotada (P-N01).
3. P2 = **COH-61**: persona o dispositivo. Juan dijo «el dispositivo o el usuario al que se le asignó».
4. Seguir con COH-62, COH-63, COH-64 (de Juan), COH-66, COH-67, K-80/K-81, K-82 y los pares que faltan.
5. Aprovechar la ronda para confirmar tres agregados del agente: la ventana que no corre (C-62), los códigos de K-27 y «Juan decidió ajustar ADR-007».
6. Al cerrar: verificador + revisión de coherencia (D-04), como siempre.

## Reglas que no hay que olvidar
- Decir «nodo de finca» o «nodo principal», nunca «nodo» a secas. Al paquete técnico se le dice «partición»; «lote» es solo el de cultivo.
- Corrige solo el gerente de producción; el operario recaptura.
- Nada desconocido entra a la finca (fallo técnico). Lo que va hacia el celular se resuelve en despliegue y actualización.
- Si Juan dice «fuera de contexto», no se decide: va a §7 o a una hoja.
- Commits: solo Juan (PR-05). TX-1.K, P-S04 y P-S05 siguen `POR ACEPTAR`: no se programa.
