# Fuera de contexto · lo valioso que no es del contrato de ingesta
Qué es: lo que salió al detallar el contrato pero pertenece a otro elemento, agrupado por el contexto al que debería ir.
Quién: IA (Claude), 2026-10-17, destilado de las sesiones b7e68fbd / d4fb0ea1.
Estado: SIN REVISAR
Manda por encima: `ESPEC-contrato-ingesta.md`, `~/.claude/skills/detallar-para-codificar/SKILL.md` y la palabra de Juan.

ESPEC §7 ya tiene la lista corta por destino. Aquí está **lo que Juan dijo** para cada contexto, para sembrar su hoja o ESPEC propia.
Pedido de Juan (17-oct 03:41): «lo que se clarifica de otro protocolo… tendría que entrar en una hoja destinada al caso de ese contexto».

## Caso de uso de sincronización (ingreso en el nodo de finca)
- La jornada: al operario «le mandan lo que tiene que ir a mirar», captura sin internet y sincroniza en la oficina (16-oct 19:35).
- Turnos: unas 30 personas en dos turnos (6 a 2-3, y 2-4 a 10-11). Lo que no sincronizó el turno de la mañana se reasigna al de la tarde, y la misma orden puede aplicarse dos veces el mismo día (20:31) → obsoleta y superada (A-45 ⟦1⟧, K-52, COH-36).
- La orden de captura se deduce «del dispositivo… o el usuario al que se le asignó» (03:17): falta elegir (COH-61, COH-64).
- Mínimo por captura de cama: «pocos elementos, **personalizables**» → el mínimo lo define la configuración por actividad. Sin dueño.
- Cuándo falta un dato: se perdió una cama, un bloque, un lote o «un celular completo que nunca sincronizó» → consulta «asignado − recibido» (ADR-026, K-6). Solo está en la HISTORIA.
- Criterio de confiabilidad: asegurar la llegada del dato, ver variaciones de producción y «quién hizo qué cosa».
- Divergencia, `hora_dudosa`, `vieja`, sello tras restaurar, RF-022 frente a `orden_duplicada` (K-71): ya en ESPEC §5 «Fuera del borde».

## Identidad, login y sesión
- Login local en el celular para que la identidad esté presente al capturar; el acceso en campo no se pierde «totalmente»; la credencial se renueva en la oficina antes de sincronizar y esa persona es dueña de la sincronización (22:22).
- Celular compartido: cada operario entra con **su cuenta única**, esté o no sincronizando; quien entra baja **su propio catálogo y su asignación** (03:14).
- Chocan con fuentes aceptadas: P-N01 (24 h + 7 días de gracia, K-75) y ADR-007 (K-59, COH-42). Decide Juan.
- Los casos del laboratorio (`~/Work/laboratorio-skills/casos/login`, `casos/contrato-login`) son ejercicios: no son la ESPEC del login de FlorLogic.

## Respaldo y restauración (TX-2)
- El celular borra solo lo que está respaldado «de forma automática o constante, física o en la nube» (17:52) → `respaldado_hasta` = mínimo entre la copia local y la nube, solo con la prueba de restauración aprobada (A-31, COH-49).
- La frecuencia de la prueba de restauración: Juan dijo «fuera de contexto» (K-67). La propuesta de probar cada noche no está decidida.
- Que la cuarentena y los paquetes de configuración entren en el respaldo (COH-67, COH-40).
- Qué estado sobrevive a una restauración (alertas, `registrar_de_nuevo`): COH-52.

## Dominio de lote y niveles de captura
- Lote = plantación definida de una variedad, en un periodo con **fechas reales** (no semanas numeradas), repartida en varias secciones. No se captura el lote, se capturan las camas (A-30).
- Granularidad: la v1 anota solo por sección; cama, bloque y lote quedan para la sesión **D1** de Juan (A-20).

## Celular (simulador) y tablero del gerente
- Edición local de lo no enviado; inmutable desde que sale; la recaptura lleva id nuevo (A-49).
- Lo respaldado se oculta al operario («inaccesible por usuarios operarios de campo»).
- Tablero ADR-026: correcciones del gerente de producción, camas a rehacer, decidir sobre lo no entregado a los 7 días, cierre administrativo de huecos.

## Despliegue y versiones
- Cuatro ejes independientes: app/despliegue, motor, configuración (catálogo + reglas en un paquete) y contrato. «El nodo de finca crea cualquier cosa que llega al celular» (A-50).
- Lo que va hacia el celular se resuelve en despliegue y actualización, no en el contrato (K-43).

## ADR y fuentes que solo puede tocar Juan
- ADR-027: deprecado por Juan («mal conceptualizado», K-41). Falta marcarlo en `ADR.xlsx` y decidir qué reemplaza §1-§5.
- ADR-024 §1 (`corrige_a` en la tupla) y ESC-58 (K-40, K-41) · ADR-027 §2 y ESC-34 (K-40) · ADR-028 §3 (K-28) · ADR-007 y BR-N5 (K-59) · texto de ADR-035 (K-4).

## Material visual (claude.ai, privado)
- Contrato, comportamiento esperado: https://claude.ai/artifact/6FFzNX26NYEfGDrwM8DJKR (17 diapositivas, hecho a partir de la ESPEC del 16-oct; puede estar desactualizado).
- Expansión de conceptos de la sesión (pedido 16-oct 22:57): se armó en `scratchpad/deck-expansion` de la sesión d4fb0ea1. No se encontró el enlace en la conversación.
