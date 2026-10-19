# Destinos · a dónde va cada pieza y con qué forma

## 1 · Plantillas de detallar-para-codificar (primero)
| Si la pieza habla de… | Plantilla | Semilla |
|---|---|---|
| Lógica que decide con lo recibido (reglas, orden, estados, conflictos) | `caso-de-uso` | `semillas/IA_SEMILLA-caso-de-uso-<nombre>.md` |
| Otra ruta o mensaje distinto del que se detalla | `endpoint` | `semillas/IA_SEMILLA-endpoint-<nombre>.md` |
| Qué es una cosa del negocio, sus partes e invariantes | `entidad-agregado` | `semillas/IA_SEMILLA-entidad-agregado-<nombre>.md` |
| Cómo se guarda, índices, transacciones, tiempos de la base | `adaptador-persistencia` | `semillas/IA_SEMILLA-adaptador-persistencia-<nombre>.md` |
| Alertas o avisos que otro componente consume | `evento-mensaje` | `semillas/IA_SEMILLA-evento-mensaje-<nombre>.md` |
| Algo que corre por calendario (respaldo, prueba, limpieza) | `trabajo-programado` | `semillas/IA_SEMILLA-trabajo-programado-<nombre>.md` |
| Umbrales, catálogos, parámetros por actividad | `configuracion` | `semillas/IA_SEMILLA-configuracion-<nombre>.md` |
| Cómo se prueba algo que no es del elemento | `pruebas` | dentro de la semilla del elemento que se prueba |

**Forma de una semilla:** cabecera · Ficha corta (elemento, plantilla, definición *propuesta*,
dominio) · «Lo que ya se sabe» (tabla id de origen → regla → fuente) · «Casos que ya existen» ·
«Abierto» · «Qué no es».

## 2 · Documentación humana (si no encaja en una plantilla)
Se imita el formato del documento humano que corresponda en el proyecto. En FlorLogic:

| Tipo | Documento humano que se imita | Formato |
|---|---|---|
| Requisito funcional | `DRIVERS_ARQUITECTONICOS.md` §2 | ID · Título · Estado · Requisito («El sistema debe…») · Necesidad · Nota |
| Escenario de calidad | `DRIVERS_ARQUITECTONICOS.md` §9 | ID · atributo · un párrafo con fuente, estímulo, artefacto, entorno, respuesta y medida |
| Restricción | `DRIVERS_ARQUITECTONICOS.md` §3-§4 | ID · enunciado · origen |
| Decisión ya tomada, sin ADR | `docs/03-arquitectura/decisiones/` | `aaaa-mm-dd-tema.md`, «Va al ADR:» en blanco, lo decidido, los motivos, lo que toca |
| Lo que puede pasar | `IA_TX1-sincronizacion.md` §7 (event storming) | título · estímulo · entorno · qué pasó · cómo lo abordamos · prueba |
| Recorrido entre actores | diagrama de flujo | Mermaid `flowchart` en el mismo archivo del event storming |
| Caso de uso de alto nivel | `IA_TX1-sincronizacion.md` §8 | ID · caso de uso · actor · incluye |
| Sin dueño o solo de la persona usuaria | `docs/contenido-pendiente/` | pregunta · por qué importa · de dónde sale |
| Término | diccionario del proyecto | no se apartan: el diccionario ya es compartido |
