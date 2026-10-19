---
name: information-pairment
description: Separa de una ESPEC (y de su HISTORIA y destilados) todo lo que no pertenece a su contexto definido. Cada pieza ajena va primero a la plantilla de detallar-para-codificar que le corresponda, como semilla de otra ESPEC; si no encaja en ninguna, se recrea como el tipo de documentación humana que le toca (requisito funcional, escenario de calidad, event storming, diagrama de flujo, decisión, pregunta abierta) en un directorio espejo en revisión, nunca sobre la documentación humana. Úsala cuando el usuario la invoque, o cuando diga «saca lo que no es del contexto», «aparta lo que no es del contrato», «poda la ESPEC» o «information pairment».
---

# Information pairment

Una ESPEC crece con lo que se aclara en las preguntas, y parte de eso pertenece a otros elementos:
dudas de negocio, comportamiento de otro componente, ajustes de ADR. Esta skill **empareja cada
pieza con el lugar que le corresponde** y la deja en ese lugar, sin perderla. No decide nada nuevo.

## Entradas
1. La ESPEC y su **Ficha**: la «Definición acordada» y el campo «Dominio» marcan el borde.
2. Lo que la acompaña: la HISTORIA, los destilados y la lista de decisiones de la persona usuaria.
3. Las plantillas de `~/.claude/skills/detallar-para-codificar/referencias/plantillas/`.
4. La estructura de la documentación humana del proyecto, que se lee y no se toca.

## Procedimiento
1. **Fijar el borde.** Copia la definición acordada y el dominio en el índice. Una pieza es del
   contexto solo si cambia lo que el elemento recibe, devuelve o garantiza. Lo demás es ajeno,
   aunque se haya aclarado en el mismo detallamiento.
2. **Inventariar.** Recorre cada decisión, caso, variable, pendiente y nota de «Fuera del dominio»,
   y también lo que la persona dijo y no quedó escrito (destilados). A cada pieza le pones su id de
   origen (`A-n`, `C-n`, `K-n`, `COH-n`, cita con fecha).
3. **Partir las piezas mixtas.** Si una fila mezcla contrato y otra cosa, la parte del contrato se
   queda y la otra se aparta. Se anota qué frase sale.
4. **Emparejar, en este orden:**
   1. ¿Encaja en otra **plantilla** (caso-de-uso, endpoint, entidad-agregado, adaptador-persistencia,
      evento-mensaje, trabajo-programado, configuracion, pruebas)? → **semilla de ESPEC** de ese
      elemento (ver `referencias/destinos.md`).
   2. Si no, ¿qué **documento humano** le corresponde? → recréalo con el formato de ese documento
      en el espejo (ver `referencias/destinos.md`).
   3. Si no tiene dueño ni lugar → **pregunta abierta** para la persona usuaria.
5. **Escribir en el espejo.** El directorio de salida es `<carpeta de la ESPEC>/IA_apartado-<fecha>/`.
   Lleva `semillas/` y `documentacion/`, y dentro de `documentacion/` reproduce las rutas de la
   documentación humana, para que pasar un archivo a su sitio sea moverlo. Los ids nuevos llevan
   `P` (`RF-P01`, `ESC-P01`, `ES-P01`, `UC-P01`) para no chocar con los vigentes.
6. **Proponer la poda.** En `IA_00-INDICE.md` va la lista de lo que saldría de la ESPEC: la fila,
   la frase y el destino. **La ESPEC no se toca** hasta que la persona usuaria apruebe la poda.
7. **Aplicar la poda** (solo con el «sí»):
   1. **Copia íntegra antes de tocar nada:** `<ESPEC>-HISTORIA-<rondas>.md`, con una cabecera que diga
      que no es la versión vigente. Compara la copia con la original y no sigas si no son idénticas. Sin
      esa copia, las rondas que solo vivían en la ESPEC se pierden.
   2. **Los casos de cable se quedan:** un caso o un par que muestra una respuesta del elemento, o la
      reacción de un cliente a esa respuesta, se queda aunque cite lógica apartada. Sale solo el caso
      que describe por dentro otro elemento.
   3. Lo que se arma a mano (Ficha, decisiones partidas, pendientes) se escribe; lo que se conserva
      tal cual (tablas de tipos, ejemplos dorados, forma técnica) se copia por rango de líneas con un
      script, para no transcribir mal.
   4. **Busca las citas rotas:** casos, pares, variables y filas de §5 que todavía citan un id apartado,
      o una regla que ya no dice eso. Haz una lista para el verificador. Se reparan como redacción, con
      permiso (detallar-para-codificar D-11): se vuelve a citar la regla que queda, o se marca
      «(semilla X, no se prueba aquí)».
   5. Actualiza en cada semilla la lista de casos: cuáles se apartaron (con la ruta de la copia) y cuáles
      siguieron en la ESPEC.
   6. En la Ficha: «Historia», «Apartado», «Dónde retomar» y la precisión como «por recalcular».
      Después, la verificación la hace detallar-para-codificar (D-10).

## Reglas
- **Nada se inventa.** Cada pieza lleva su origen. Lo que infirió un agente se marca
  «inferido, confirmar»; las palabras de la persona usuaria se citan tal cual, con fecha.
- **Nada se pierde.** Toda pieza del inventario aparece en el índice con un destino, o con «se queda».
- **La documentación humana es de solo lectura**, igual que los ADR: se lee, se cita y se imita su
  formato, pero nunca se escribe.
- Cada archivo lleva prefijo `IA_` y la cabecera de cuatro líneas del proyecto (qué es, quién,
  estado `SIN REVISAR`, qué manda por encima).
- **Todo id nuevo** (`RF-P01`, `ES-P03`, `UC-P02`…) se registra en la Nomenclatura del diccionario
  del proyecto en el mismo turno, con su significado en 5 a 10 palabras (detallar-para-codificar D-09).
- Una pieza que la poda deja a medias (una semilla que se adelanta, como la sesión que pasa a ser la
  parte 2 del mismo elemento) se corrige en la semilla y en el índice; no se duplica.
- Una semilla no es una ESPEC: no tiene índice de precisión ni rondas. Es el punto de partida que
  detallar-para-codificar lee en su etapa 2 (consulta).
- Archivos cortos: una semilla en 40-120 líneas; si pasa de 200, se parte por elemento.

## Cierre
Resume en el chat cuántas piezas salieron, a qué destinos y qué queda para la persona usuaria:
aprobar la poda, mover archivos del espejo a la documentación humana y responder las preguntas.
