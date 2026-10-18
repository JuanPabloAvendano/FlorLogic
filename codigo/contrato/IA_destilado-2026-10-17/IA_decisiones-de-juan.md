# Decisiones de Juan · contrato de ingesta (TX-1.K)
Qué es: lo que Juan decidió o corrigió en la conversación, contrastado con lo que quedó escrito en la ESPEC.
Quién: IA (Claude), 2026-10-17, destilado de las sesiones b7e68fbd / d4fb0ea1.
Estado: SIN REVISAR
Manda por encima: `ESPEC-contrato-ingesta.md`, `~/.claude/skills/detallar-para-codificar/SKILL.md` y la palabra de Juan.

Horas en UTC (las del registro de la sesión; Bogotá = UTC−5). «ESPEC» = `codigo/contrato/ESPEC-contrato-ingesta.md`.
Solo se listan las respuestas que traen texto de Juan, que van contra la recomendada o que corrigen un malentendido.
Las respuestas que solo eligieron la opción recomendada ya están en la ESPEC §2 con su ronda: no se repiten aquí.
Antecedente: `~/.claude/skills/detallar-para-codificar/mejora/02-informe-agilidad.md` auditó 30 respuestas (24 fieles).

## A · No están en la ESPEC, o la ESPEC dice otra cosa
| Fecha | Cita breve de Juan | Afecta a | En la ESPEC |
|---|---|---|---|
| 16-oct 19:35 | «pocos elementos a llenar por captura de cama, **personalizables**… si no se completa la información mínima necesaria… no se puede ingresar» | A-46 (mínimo requerido): quién configura el mínimo, por actividad y dónde (catálogo) | **no**: «personalizables» no aparece en ningún documento |
| 16-oct 17:21 | «se perdió la información de una cama, un bloque, un lote, un celular completo que… nunca sincronizó? hay muchas alternativas» | caso de uso del ingreso y tablero ADR-026 (consulta «asignado − recibido») | **solo en la HISTORIA**: la consolidación lo sacó del §7 vigente |
| 16-oct 22:22 | «**si es necesario** verificación nuevamente para sincronizar… no se debería perder el acceso **totalmente** si se está en el campo… **podría darse** actualizar la credencial antes» | A-51 ⟦2⟧ ⟦3⟧ y K-75 (P-N01) | **la endurece**: la ESPEC dice «nunca se pierde por vencimiento» y renovar es obligatorio. Las palabras de Juan caben en una gracia acotada como la de P-N01 (24 h + 7 días) |
| 16-oct 22:10 | (COH-31) «versiones independientes… el nodo de finca crea cualquier cosa que llega al celular» | A-50 y C-62 | **agregado**: «la ventana de retención no corre y se alerta» no lo dijo Juan (origen de K-54 y COH-40) |
| 16-oct 19:45 | (K-27, sin opción) «la marca es para resaltarla a la hora de que el gerente de producción realice las correcciones» | A-23: códigos `desfase_desconocido` y `capturada_sin_credencial` | **inferida**: explicó para qué sirve una marca, no aprobó esos dos códigos |
| 17-oct 03:17 | (K-72) «La deduce del dispositivo al que se la asignó… **el dispositivo o el usuario** al que se le asignó» | A-45 ⟦1⟧, orden de captura, COH-61 | **sin decidir**: dijo las dos cosas. COH-61 (bloqueante) es la consecuencia |
| 16-oct 20:31 | «la confiabilidad se basa en poder asegurar la llegada del dato… encontrar problemas de variación de producción e identificar quiénes hicieron qué cosa» | propósito de TX-1 y atributo de calidad | **no**: el criterio de confiabilidad no figura en §1 |
| — | «Decisión de Juan: ajustar ADR-007 y BR-N5» (K-59, COH-42) | A-51 frente a ADR-007 | **sin fuente en b7e68fbd**: no aparece esa frase de Juan. Puede venir de una sesión anterior o ser inferida. Confirmar |

## B · Correcciones de Juan a malentendidos del agente (ya corregidas en la ESPEC)
- 16-oct 17:43-17:45 · el **lote** de cultivo: «una variedad para producción, en un periodo fenológico», «no se va a trabajar por semanas con número, sino con periodos y fechas reales», «no se captura el lote, se capturan las camas» → A-30.
- 16-oct 17:49 · el paquete técnico se llama **partición**: «llámalos particiones» → A-30, `/particiones`.
- 16-oct 18:31 · «una partición era un **segmento** de lo que se va a sincronizar del celular, no todo el celular… sincronizar **todos los celulares al tiempo**» → A-30 corregida.
- 16-oct 19:02 · K-20: el lote es «una plantación definida de una variedad, que puede estar en varias secciones»; no contradice a ADR-024 → A-30.
- 16-oct 19:23 · «sigue llamándolo **nodo principal o nodo de finca**» → `codigo/IA_DICCIONARIO.md` (ACORDADO, nunca «nodo» a secas).
- 16-oct 20:38 · las **correcciones** las hace solo el gerente de producción; el operario no corrige, edita en su celular lo no sincronizado; el único motivo obligatorio es la desviación brusca → A-49.

## C · Juan eligió contra la opción recomendada (en la ESPEC: sí)
- 17:39 K-7 «Nunca repetir, procesar siempre» → A-26 (reenvío = `sin_efecto`, sin guardar respuestas).
- 18:25 «Exigir versión 7» en `anotacion_uuid` → A-38.
- 20:43 COH-21 «Fallo técnico, un campo desconocido es peligroso»; K-42 «el sistema se construye para tener datos definidos y esperados» → A-32 (A-25 retirada). Queda la tensión K-80.
- 22:50 COH-50 «Fallo técnico como hoy» (mismo id, otra huella) → A-2 (+ cuarentena, COH-53..60).

## D · Notas de Juan que sí quedaron (referencia rápida)
| Fecha | Cita breve | En la ESPEC |
|---|---|---|
| 16-oct 17:52 | borrar del celular solo cuando esté respaldado «físicas o digitales en la nube» y «inaccesible por usuarios operarios» | sí · A-31, A-48 |
| 16-oct 17:58 / 19:47 | «Usar el Null Object Pattern» · «nivel backend» | sí · A-33, A-34 |
| 16-oct 18:00 | faltar puede ser «menor a lo esperado» | sí · A-35 (dato ausente / producto faltante) |
| 16-oct 19:07 | duplicados «no deberían aparecer nunca, pero si pasa, deduplicar con orden del arreglo» | sí · A-42 |
| 16-oct 19:26 | lo viejo «es más fácil volverlo a capturar»; 3 días; conservar 7 «para que el gerente lo revise» | sí · A-45 ⟦2⟧ ⟦3⟧ |
| 16-oct 19:35 | flujo de la jornada: asignación → campo sin red → oficina; fallo técnico se borra y pide recaptura | sí · A-46, A-48 (eligió «suelta al respaldar», aunque su texto dice «cuando se termina la jornada») |
| 16-oct 20:31 | 30 trabajadores, turno de 6 a 2-3 y de 2-4 a 10-11; la orden se reasigna al turno de la tarde; la tardía se descarta | sí · A-45 ⟦1⟧, C-37 (eligió «constancia mínima», no «descartar totalmente») |
| 16-oct 20:39 | «Deprecia ADR-027 bajo el motivo de mal conceptualizado» | sí · K-41 (falta marcarlo en `ADR.xlsx`) |
| 16-oct 20:46 | K-43 «Nada entra a la finca; si es hacia el celular, va en despliegue y actualización» | sí · §7 (A-43 retirada) |
| 16-oct 22:02 | «Inmutable desde que sale, eso es otra captura nueva de la cama» | sí · A-49 ⟦3⟧ |
| 16-oct 22:47 | K-67 «fuera de contexto, esto es respaldos… estamos en sincronización» | sí · §7 TX-2, solo como propuesta |
| 16-oct 22:52 | K-54 «la configuración nace, se crea y se distribuye desde el backend de la finca» | sí · A-50 |
| 17-oct 03:14 | celular compartido: «cuenta única de cada operario… si no se completó la sincronización para eso está en partes… también tenga que sincronizar su catálogo propio» | sí · A-51, K-69, §7 ronda 2 |
