# Escenarios de calidad propuestos · salidos del detallamiento del contrato de ingesta
Qué es: escenarios de calidad que se desprenden de lo que Juan dijo al detallar TX-1.K, con el formato de `DRIVERS_ARQUITECTONICOS.md` §9 (un párrafo con fuente, estímulo, artefacto, entorno, respuesta y medida).
Quién: IA (Claude), 2026-10-18, skill information-pairment, desde `ESPEC-contrato-ingesta.md`, su HISTORIA y el destilado del 17-oct.
Estado: SIN REVISAR
Manda por encima: `EscenariosCalidad.xlsx` · `DRIVERS_ARQUITECTONICOS.md` · la palabra de Juan.

**Las medidas son propuesta del agente**, salvo donde se cita a Juan. Ninguna está validada con el cliente.

## Confiabilidad

| ID | Escenario |
|---|---|
| `ESC-P01` | el gerente de producción revisa la jornada ya sincronizada para encontrar variaciones de producción, sobre las anotaciones registradas, sus sesiones de captura y sus sesiones de sincronización, en operación normal, después de que todos los celulares entregaron. El sistema asegura que cada dato capturado llegó o que su falta es visible, y dice quién capturó, quién entregó y desde qué celular, con 0 anotaciones perdidas sin constancia, 100 % de las anotaciones con autor y dispositivo, y 100 % de las faltas explicadas en la consulta asignado − recibido. |
| `ESC-P02` | el nodo de finca se restaura a un respaldo anterior a lo que ya había anunciado como respaldado, sobre las marcas de agua por celular y la lista de camas a rehacer, en recuperación tras una falla, con celulares que ya borraron lo que creían a salvo. El sistema baja las marcas, pide a cada celular reenviar lo que retiene y calcula por celular la posible pérdida, con alerta al administrador al terminar la restauración, 100 % de las camas afectadas en la lista y 0 huecos que reaparezcan por lápidas perdidas. |
| `ESC-P03` | el trabajo de respaldo nocturno termina y pasa su prueba de restauración, sobre la marca `respaldado_hasta` de cada celular, en operación normal, con celulares que retienen lo confirmado y aún no respaldado. El sistema publica la marca nueva en la siguiente respuesta a cada celular, que entonces borra lo cubierto, con retención máxima en el celular de 1 día si la prueba es nocturna. **Sin decidir:** Juan marcó la frecuencia de la prueba como «fuera de contexto» (K-67), y `ESC-19` hoy pide una prueba al mes, con la que el celular retendría hasta un mes. |

**Fuente de `ESC-P01`.** Juan, 16-oct 20:31: «la confiabilidad se basa en poder asegurar la llegada del dato… encontrar problemas de variación de producción e identificar quiénes hicieron qué cosa». Hoy no figura en ningún documento.

## Redacciones vigentes que esto deja en duda
`DRIVERS_ARQUITECTONICOS.md` ya marca `ESC-08`, `ESC-34` y `ESC-58` como «redacciones anteriores a las decisiones». El detallamiento añade motivos concretos:
- `ESC-08` (el supervisor corrige en la cola local y se conserva el original y el motivo) frente a A-49 ⟦2⟧: la edición local reemplaza sin dejar rastro.
- `ESC-34` («conserva ambas versiones», «0 registros descartados automáticamente») frente a la captura obsoleta de A-45 ⟦1⟧ (K-40).
- `ESC-58` (corrección como evento nuevo que conserva el original) frente a la superada, que sale de la historia (K-40, ronda 1h P2).
