REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 16-oct-2026, a pedido de Juan (skill detallar-para-codificar)
Estado de revisión: SIN REVISAR
Manda por encima: los ADR (`ADR.xlsx`) y la documentación de `documentacion-desarrollo`; si chocan, manda la fuente y esta entrada se corrige

# Diccionario de FlorLogic

Palabras con un significado específico en FlorLogic, de **negocio** y **técnicas**. Lo mantiene la skill
`detallar-para-codificar`: cada explicación copia de aquí las definiciones de las palabras que usa. Si
una palabra tiene dos lecturas, son dos entradas y cada una nombra a la otra en «No es».

Estados: `PROPUESTO` (escrito por la IA, sin confirmar) · `ACORDADO <fecha>` (Juan dijo «Es lo mismo»)
· `REEMPLAZADO <fecha> · ver <término>`.

## Pares que se confunden

| Término | Término | La diferencia |
|---|---|---|
| Lote | Partición | El lote es una plantación; la partición es un trozo de lo que sube un celular |
| Sesión de captura | Sesión de sincronización | La primera es el trabajo de una persona en campo; la segunda, una subida al nodo de finca |
| `confirmado_hasta` | `respaldado_hasta` | Lo que el nodo de finca recibió frente a lo que ya está respaldado en dos copias |
| Nodo de finca (nodo principal) | Nube | El nodo de finca tiene el dato vivo y opera sin internet; la nube solo guarda respaldos y versiones |
| Acuse | Marca de agua | El acuse es por anotación; la marca de agua es un número para todo el celular |
| Anotación vieja | Anotación obsoleta | La vieja se mide por edad; la obsoleta, porque otra captura ya cumplió su orden de captura |
| Divergencia | Fallo técnico | La divergencia es un fallo del motor y entra marcada; el fallo técnico se descarta y se recaptura |
| Captura incompleta | Fallo técnico | La incompleta es un hecho del campo y entra; el fallo técnico es un error y se descarta |
| Anotación marcada | Marca de agua | La marcada resalta una anotación para el gerente; la marca de agua es un número de avance |
| `corrige_a` | Corrección del gerente | `corrige_a` es una anotación que corrige otra; la del gerente es una revisión en el tablero |
| Corrección | Edición local | La corrección la hace el gerente sobre lo registrado; la edición local, el operario antes de sincronizar |
| Catálogo de configuración | Catálogo de códigos | El de configuración lo versiona el nodo y cambia sin tocar código; el de códigos es una lista fija del contrato |
| Desviación | Divergencia | La desviación es un cambio brusco del producto; la divergencia, un desacuerdo entre motores |
| Hueco | Dato ausente | El hueco es un consecutivo que no llegó; el dato ausente llegó sin cifra |
| `ingesta/rechazado` | `gateway/rechazado` | El primero es el `422` del backend (la partición llegó y su forma falla); el segundo lo genera Nginx (`404`, `405`, `413`…) y la petición nunca llegó al backend |
| Gateway de la finca | Ingreso de sincronización | El gateway es la puerta de red (Nginx); el ingreso es el módulo del backend que decide qué entra |
| `motivo_ausencia` | `motivo_desviacion` | El primero explica que no hay cifra; el segundo, una cifra rara que sí vino |
| WAF de la finca | Gateway de la finca | El gateway es la puerta que enruta; el WAF es el filtro de ataques que corre dentro de esa misma puerta |
| WAF de la finca | WAF de la nube (`BB-08`) | El de la finca protege el nodo en la red local (C4); el de `BB-08` son reglas gestionadas del proveedor de CDN para los servicios en línea, y el C4 vigente ya lo quitó (observación O-11 de `IA_TECNOLOGIAS-C4.md`) |
| `gateway/bloqueado` | `sin-permiso` | Los dos son `403`: el primero lo da el WAF del gateway (parecía ataque, nunca llegó al backend); el segundo lo da el backend por la credencial (A-22 del contrato) |
| Falso positivo | `ingesta/rechazado` | El falso positivo es una partición válida que el WAF toma por ataque; el `422` es una partición con forma inválida que el backend rechaza |

## Nomenclatura

Todo código que aparece en una ESPEC, una semilla, un destilado o una explicación tiene aquí su
significado en 5 a 10 palabras y su uso (skill `detallar-para-codificar`, `diccionario.md` §5 y D-09).
Los de ejemplo también. Antes de crear un prefijo o un código, se comprueba que no esté en «Prefijos
que chocan» y se registra aquí en el mismo turno. Todas las filas nacen `PROPUESTO` (18-oct-2026).

**Los ids ya usados se mantienen**: no se renombran aunque choquen; el choque queda en «Prefijos que
chocan». Los códigos de una familia se explican con el mismo patrón (misma etiqueta inicial: «ADR:»,
«Escenario:», «Ejemplo:»…), aunque el contenido no sea siempre igual. Un código que se aparta de su
familia tiene su propia fila.

### Familias que crea la skill (ids de la ESPEC)

Su significado suelto es la primera frase de su fila en la ESPEC. Se conservan iguales en la ESPEC,
las HISTORIAS, los destilados y las semillas.

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `A-n` | Afirmación: una decisión de cómo funciona el elemento | ESPEC §2 · §5 le asigna un archivo Java destino |
| `A-n ⟦k⟧` | Parte k de una decisión que tiene varias | Dentro de una `A-n`; viene del hueco k del esqueleto |
| `C-n` | Comportamiento: escenario «Dado · cuando · entonces» que cubre decisiones | ESPEC §4 · cada uno se vuelve una prueba (§9) |
| `K-n` | Contradicción o ambigüedad hallada en las rondas de preguntas | ESPEC §6 · resta al índice; bloquea el código |
| `COH-n` | Hallazgo del agente de revisión de coherencia y calidad | ESPEC §6 · igual que `K-n`; la puerta es `D-04` |
| `OBS-s-n` | Observación de revisión n del segmento de código s | ESPEC §10 (etapa 5) · se corrige o se descarta |
| `DI1-n` | Decisión implícita n del Segmento 1 del dominio de ingesta | ESPEC-dominio-ingesta, ficha del Segmento 1 · la cita el código · **no es `D-nn` ni `D-0nn`** |
| `VS2-n` | Decisión implícita n de la verificación del Segmento 2 | ESPEC §11 (tabla VS2) · la cita el código · **no es `Dn` de ejemplo ni `D-nn`** |
| Segmento n | Grupo de unas 600 líneas de código para revisar | ESPEC §8 (etapa 4) · se escribe, revisa y acepta |
| Ronda 1, 1b…1j | Vuelta de 10 preguntas, de lo general a lo detallado | Columna Fuente de las `A-n`; «Dónde retomar» |
| `Pn` (P1…P20) | Pregunta n dentro de una ronda | «ronda 1c P9» = cuándo se decidió algo |
| `⟦n⟧` | Hueco n del esqueleto que rellena una pregunta | Esqueleto de §1 de cada plantilla |
| `★` | Opción recomendada de una pregunta | «(★)»: Juan aceptó la recomendada |
| `K-110`… | Contradicción de ESPEC-dominio-ingesta (desde 110, sin chocar con el contrato) | ESPEC-dominio-ingesta §6 · también `A-60`…, `C-90`… |
| `INV-n` | Invariante n de un agregado: regla que nunca se rompe | ESPEC-dominio-ingesta §2 · cada una es una prueba unitaria del dominio |
| `semilla` | Arranque de otra ESPEC con lo apartado | `codigo/contrato/espec/IA_apartado-2026-10-18/semillas/` · conserva los ids |

### Reglas de la skill y prácticas de sus plantillas

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `D-nn` (`D-01`…`D-13`) | Regla o cambio de la skill, con su porqué | `~/.claude/skills/detallar-para-codificar/DECISIONES.md` |
| `D-04` | Regla de la skill: revisión de coherencia obligatoria | Sin pasarla no hay «LISTA PARA CÓDIGO» |
| `D-06` | Regla de la skill: diccionario y bloque «Términos» | Este archivo |
| `D-07` | Regla de la skill: lo fuera del borde cuenta trazado | ESPEC §5 «Fuera del borde» |
| `D-08` | Regla de la skill: rondas de 10 preguntas | Etapa 3 |
| `D-09` | Regla de la skill: todo código tiene significado aquí | Esta sección |
| `D-10` | Regla de la skill: verificar otra vez después de podar | Etapa 3, tras `information-pairment` |
| `D-11` | Regla de la skill: redacción con permiso, sin pregunta | Arreglos que no cambian reglas |
| `D-12` | Regla de la skill: efecto en cascada y estimado honesto | Un `K-n` nace en el mismo turno |
| `D-13` | Regla de la skill: etiquetas, reabrir decisiones, una por pregunta | Contexto de cada pregunta |
| `D-14` | Regla de la skill: lo mínimo viable primero, generalizar | Filtros de cada pregunta (etapa 3) |
| `D-15` | Regla de la skill: consultar fuentes antes de escribir código | Línea «Consultado» de la ficha de cada segmento |
| `D-16` | Regla de la skill: diagrama draw.io antes del código | `diagramas/IA_segmento-<n>.drawio` junto a la ESPEC |
| `D-17` | Regla de la skill: camino mínimo funcional primero | Encuadre, preguntas, segmentos y revisión; lo demás va a «después» |
| `D-18` | Regla de la skill: el camino mínimo lleva empaquetado correcto | Etapas 4 a 6 · un paquete mal ubicado es bloqueante |
| `D-19` | Regla de la skill: el canal se traspasa a sus elementos | Al aceptar el canal · nota de traspaso a las semillas |
| `EP-nn` | Práctica sana de la plantilla endpoint | `plantillas/endpoint.md` §7 · la verifica el verificador |
| `EP-02` | Práctica: ningún DTO web cruza al núcleo | Cita de `A-44` |
| `EP-06` | Práctica: errores como `problem+json` con `type` estable | Cita de `A-22` |
| `EP-14` | Práctica: límites explícitos de tamaño, cantidad y tasa | Citada por el verificador (413 frente a 422) |
| `EP-18` | Práctica: el contrato existe en JSON Schema y se prueba | Cita de `COH-80` (errores dorados) |
| `CF-nn` · `EV-nn` · `PB-nn` · `TP-nn` | Práctica de las plantillas configuración, evento, pruebas o trabajo programado | `plantillas/<plantilla>.md` |
| `PR-nn` (plantilla) | Práctica de la plantilla adaptador de persistencia | `plantillas/adaptador-persistencia.md` · **choca con `PR-nn` del repo** |

### Fuentes que se citan (viven en `documentacion-desarrollo` o en el repo)

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `PR-nn` (repo) | Principio de trabajo del repositorio FlorLogic | CLAUDE.md · `PR-05`: commits y empujes los hace Juan |
| `TX-n` | Transacción completa que se programa de punta a punta | `IA_00-LEEME-transacciones.md` |
| `TX-1` | Transacción: sincronizar una jornada ya capturada | `IA_TX1-sincronizacion.md` |
| `TX-1.K` | Trozo de TX-1: la forma del contrato | **Esta ESPEC** (`codigo/contrato/espec/ESPEC-contrato-ingesta.md`) |
| `TX-2` | Transacción: respaldar en la nube con observabilidad | `IA_TX2-respaldo-y-observabilidad.md` |
| `TEC-nn` | Tecnología propuesta o aceptada, con su versión | `IA_TECNOLOGIAS-vigentes.md` |
| `TEC-10` | Tecnología: Jackson y forma canónica para la huella | `HuellaCanonica.java`, `interno/Jcs.java` |
| `TEC-11` | Tecnología: records y JSON Schema versionado | Módulo `contrato/` |
| `TEC-12` | Tecnología: UUID v7 generado en el cliente | `interno/ReglasUuid.java` · generador pendiente |
| `TEC-15` | Tecnología: Nginx como gateway de la finca | `despliegue/` · `ESPEC-gateway-finca.md` |
| `TEC-16` | Tecnología: autoridad certificadora propia para el TLS local | Pendiente; lo pide el TLS del gateway |
| `TEC-28` | Tecnología: imagen OCI con Jib y Docker Compose | `despliegue/compose.yaml` |
| `TEC-31` | Tecnología propuesta: WAF ModSecurity v3 con OWASP CRS 4 | `IA_TECNOLOGIAS-vigentes.md` §2 · `ESPEC-waf-finca.md` · **PROPUESTA** con visto de Juan: entra al código antes de `ACEPTADO`, como excepción anotada (`ESPEC-waf-finca` COH-1) |
| `TEC-32` | Tecnología propuesta: Keycloak como proveedor de identidad | `IA_TECNOLOGIAS-vigentes.md` §2 · **PROPUESTA** 28-oct-2026, decidida por Juan |
| `CMP-n` | Componente n del esqueleto de TX-1 (C4 nivel 3) | `codigo/despliegue/espec/ESPEC-esqueleto-tx1.md` §5 · uno por caja del C4 |
| `MSG-n` | Mensaje n entre componentes: quién, a quién, cómo | `ESPEC-esqueleto-tx1.md` §5, catálogo de mensajes · sale de las flechas del C4 |
| `CH-n` | Choque entre el README de GitHub y otro documento | `IA_GUIA-IS2-checklist.md` §9 · lo resuelve Juan |
| `BB-nn` | Bloque de construcción de la arquitectura (piloto y madurez) | `FlorLogic-alternativa-de-solucion-y-ADR.md` (rama `documentacion-desarrollo`) |
| `BB-06` | Bloque: pasarela, proxy inverso con TLS y tasa en la finca | Fuente de TEC-15 y del gateway de la finca |
| `BB-08` | Bloque: WAF con reglas gestionadas del proveedor de CDN | Zona C (nube) · choca con el WAF de la finca del C4 |
| `BB-13` | Bloque: observabilidad sin datos de negocio en la telemetría | Restricción de los registros (`CN-34`, `ESC-30`) |
| `PR-04` | Principio del repo: terminado es prueba automática en verde | CLAUDE.md · cierre de cada segmento |
| `PR-06` | Principio del repo: ninguna llave ni secreto en git | CLAUDE.md · certificados y claves del gateway |
| `ADR-nnn` | Decisión de arquitectura registrada | `ADR.xlsx` (fuera de git) · resumen en `FlorLogic-alternativa-de-solucion-y-ADR.md` |
| `ADR-002` | ADR: el celular es el registro durante la jornada | Retención y outbox |
| `ADR-007` | ADR: identidad propia con credencial offline acotada | Credencial 24 h · `K-59`, `K-75` |
| `ADR-014` | ADR: sello de tiempo confiable, marcar sin bloquear | `hora_dudosa` |
| `ADR-015` | ADR: actualizar app y catálogo sin recoger celulares | Lectura de respuestas nuevas (`A-43`) |
| `ADR-016` | ADR: misma imagen desplegada en nube o finca | Despliegue |
| `ADR-024` | ADR: campo capturado es evento; cama, unidad de captura | `corrige_a` · `K-40` |
| `ADR-025` | ADR: sincronización diaria; celular perdido se recaptura | Umbrales de negocio |
| `ADR-026` | ADR: la asignación es el denominador de lo faltante | Tablero «asignado − recibido» |
| `ADR-027` | ADR: identidad UUID v7; clave del hecho aparte | **Deprecado por Juan** (`K-41`), falta marcarlo |
| `ADR-028` | ADR: la sesión de sincronización es del transporte | Sesión ≠ captura · `K-2`, `K-28` |
| `ADR-029` | ADR: un solo paquete versionado de configuración | Versiones (`A-50`) |
| `ADR-031` | ADR: sello crudo, sello normalizado y orden determinista | `dto/TiempoDto.java` (`A-16`) |
| `ADR-035` | ADR: la traza va por sesión de captura | `K-4` |
| `RF-nnn` | Requisito funcional de los drivers arquitectónicos | `DRIVERS_ARQUITECTONICOS.md` |
| `RF-001` | Requisito: registrar siembra por cama sin conexión | Cita |
| `RF-011` | Requisito: mostrar desviación real contra proyectado | Cita (`R-DESV-03`) |
| `RF-013` | Requisito: el administrador parametriza la empresa | Cita |
| `RF-016` | Requisito: último valor por campo y traza por sesión | Cita |
| `RF-017` | Requisito: solo el administrador corrige lo sincronizado | Choca con `RF-P03` |
| `RF-020` | Requisito: catálogo descargado antes de capturar | Cita |
| `RF-022` | Requisito: conflictos automáticos, gana el más reciente | Semilla de ingreso |
| `ESC-nn` | Escenario de calidad del Mini-QAW | `EscenariosCalidad.xlsx` |
| `ESC-02` | Escenario: valor fuera de rango en el motor | Cita |
| `ESC-03` | Escenario: el respaldo se dispara solo al intervalo | Cita (`respaldado_hasta`) |
| `ESC-07` | Escenario: el administrador cambia un rango o regla | Cita |
| `ESC-08` | Escenario: corregir un registro aún en cola local | Duda abierta tras la poda |
| `ESC-19` | Escenario: verificar respaldos al cumplir su periodo | Cita |
| `ESC-34` | Escenario: dos supervisores capturan la misma cama | Choca con obsoleta (`K-40`) |
| `ESC-35` | Escenario: poco almacenamiento libre en el celular | Cita |
| `ESC-38` | Escenario: todos los celulares sincronizan a la vez | Prueba de carga · fija `max_anotaciones_particion` |
| `ESC-54` | Escenario: celular dañado o perdido con pendientes | TX-2 |
| `ESC-57` | Escenario: mismo dato con y sin conexión | Motores de validación |
| `ESC-58` | Escenario: corregir un registro ya en el servidor | `K-40`, `K-41` |
| `ESC-59` | Escenario: la nube cae durante la jornada | Cita |
| `CN-nn` | Restricción del proyecto, de negocio o técnica | `RestriccionesNegocio.xlsx`, `RestriccionesTecnicas.xlsx` |
| `CN-15` | Restricción: cero pérdida, fallo máximo una hora | Confiabilidad del contrato |
| `CN-17` | Restricción: sin conectividad en el área de cultivo | Cita |
| `CN-24` | Restricción: sincronización idempotente, gana el más reciente | Captura incompleta entra (`A-35`) |
| `CN-36` | Restricción: campos capturados son datos, no columnas | Catálogo versionado |
| `CN-37` | Restricción: local-first, opera sin internet en la finca | Cita |
| `SPK-nn` | Spike: investigación medida antes de decidir | `docs/03-arquitectura/spikes/SPK-nn-*/` |
| `SPK-04` | Spike: carga de la sincronización simultánea | Ajusta topes de `ESC-38` |
| `SPK-09` | Spike: motor de reglas | Diferencia del 1,95 % (`ESC-57`) |
| `SPK-12` | Spike: ingreso y sincronización | Relojes `crudo_ms` y `mono_ms` |
| `SPK-17` | Spike: Java frente a C# | Forma canónica (`A-10`) |
| `M2b` | Medición M2b dentro del spike SPK-12 (por confirmar) | Fuente de `A-17` |
| `ES-S/R/N nn` | Tarjeta de event storming: sincronización, respaldo o nodo dañado | `IA_TX1` §7, `IA_TX2` §7, `IA_ES3` |
| `ES-S01` | Tarjeta: sincronización completa al volver a la oficina | Cita |
| `ES-S02` | Tarjeta: se corta el Wi-Fi a mitad de entrega | Cita |
| `ES-S03` | Tarjeta: se va la luz a mitad de una partición | Partición y acuse en una transacción · ESPEC-dominio-ingesta |
| `ES-S04` | Tarjeta: se pierde la respuesta y el celular reenvía | Idempotencia |
| `ES-S05` | Tarjeta: mismo identificador llega con otro contenido | Anotación corrupta |
| `ES-S06` | Tarjeta: dos operarios anotan la misma sección | Cita |
| `ES-S09` | Tarjeta: la captura llega incompleta (falta un obligatorio) | Entra marcada (`P-S01`) · ESPEC-dominio-ingesta |
| `ES-S10` | Tarjeta: el celular trae una configuración vieja | Versiones |
| `ES-S11` | Tarjeta: el servidor rechaza lo que aceptó el celular | Divergencia |
| `ES-S12` | Tarjeta: la credencial venció antes de llegar | Credencial |
| `ES-S13` | Tarjeta: llega un celular que no se conoce | Dispositivo no registrado |
| `ES-S14` | Tarjeta: temporada alta, todos sincronizan a la vez | Carga |
| `ES-S17` | Tarjeta: se pierde un celular con la jornada | Cita |
| `ES-R14` | Tarjeta: restaurar la finca entera en nodo nuevo | `confirmado_hasta` baja (`C-27`) |
| `ES-N03` | Tarjeta: la base de datos se corrompe | Cita |
| `ES-N05` | Tarjeta: muere el disco del nodo sin red | Cita |
| `ES-N09` | Tarjeta: el recordatorio escalado bloquea operarios | Cita |
| `ES-N10` | Tarjeta: celulares llenos tras días sin descargar | Retención |
| `ES-N12` | Tarjeta: vuelve el nodo y se junta la contingencia | Reenvío de bytes (`A-33`) |
| `UC-Sn` | Caso de uso de la sincronización (TX-1) | `IA_TX1` |
| `UC-S1` | Caso de uso: sincronizar la jornada | Cita |
| `UC-S4` | Caso de uso: ver avance esperado, recibido, falta | Cita |
| `UC-S5` | Caso de uso: pedir camas a rehacer de un celular | Cita |
| `UC-S6` | Caso de uso: revisar lo marcado por el gerente | Cita |
| `UC-S8` | Caso de uso: registrar un celular y su asignación | Precondición |
| `OC-n` | Opción de continuidad cuando cae el nodo de finca | `IA_ES3` |
| `OC-2` | Opción: buzón en el celular del gerente | Reenvío de bytes (`A-33`, `COH-7`) |
| `BR-N5` | Regla de negocio de vigencia de sesión (por confirmar) | `K-59`; frase de Juan sin fuente |
| `P-S0n` · `P-R0n` · `P-N0n` | Pregunta abierta a Juan: sincronización, respaldo o nodo dañado | `IA_00-LEEME-transacciones.md` |
| `P-S01` | Pregunta: captura incompleta, cuál decisión vale (ACEPTADO) | Entra; trabajo del gerente |
| `P-S02` | Pregunta: choque de dos capturas (ACEPTADO) | Gana `RF-022`, sin mediación |
| `P-S03` | Pregunta: qué borra el celular y cuándo (ACEPTADO) | Retiene hasta el respaldo |
| `P-S04` | Pregunta: jornada provisional = día natural (POR ACEPTAR) | `datos.jornada` · **bloquea el código** |
| `P-S05` | Pregunta: nombres del contrato (POR ACEPTAR) | `K-87` · **bloquea el código** |
| `P-S09` | Pregunta: umbral provisional de hora dudosa, 60 s | Semilla de ingreso |
| `P-R01` | Pregunta: cada cuánto se respalda | `respaldado_hasta` (`A-31`) |
| `P-N01` | Pregunta: credencial con gracia de contingencia (ACEPTADO) | Choca con `A-51` (`K-75`) |

### Propuestas que creó la poda (sufijo `P`, en revisión)

Viven en `codigo/contrato/espec/IA_apartado-2026-10-18/documentacion/`, con la ruta que tendrían en
`documentacion-desarrollo`. La `P` evita que choquen con los vigentes.

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `RF-P01` | Requisito propuesto: ver qué falta de la jornada | `IA_RF-propuestos-desde-TX1K.md` |
| `RF-P02` | Requisito propuesto: decidir sobre lo no entregado | Mismo archivo |
| `RF-P03` | Requisito propuesto: solo el gerente corrige lo registrado | Choca con `RF-017` |
| `RF-P04` | Requisito propuesto: reasignar lo que no sincronizó | Mismo archivo |
| `ESC-P01` | Escenario propuesto: gerente revisa variaciones de la jornada | `IA_ESC-propuestos-desde-TX1K.md` |
| `ESC-P02` | Escenario propuesto: restaurar por debajo de lo anunciado | Mismo archivo |
| `ESC-P03` | Escenario propuesto: respaldo nocturno pasa su prueba | Mismo archivo |
| `ES-P01` | Tarjeta propuesta: la cama no sincronizada pasa a la tarde | `IA_TX1-jornada-event-storming.md` |
| `ES-P02` | Tarjeta propuesta: dos operarios comparten un celular | Mismo archivo |
| `ES-P03` | Tarjeta propuesta: falta un dato y su motivo | Mismo archivo |
| `ES-P04` | Tarjeta propuesta: pasan 7 días y la cama no llega | Mismo archivo |
| `ES-P05` | Tarjeta propuesta: restauración por debajo de lo anunciado | Mismo archivo |
| `UC-P01` | Caso de uso propuesto: decidir lo no entregado | Mismo archivo |
| `UC-P02` | Caso de uso propuesto: corregir lo ya registrado | Mismo archivo |
| `UC-P03` | Caso de uso propuesto: cerrar un hueco que no llegará | Mismo archivo |
| `UC-P04` | Caso de uso propuesto: cambiar de cuenta en celular compartido | Mismo archivo |
| `UC-P05` | Caso de uso propuesto: reasignar cama al turno siguiente | Mismo archivo |

### Códigos de negocio que viajan en el contrato

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `R-…` | Id de regla del catálogo de validación | Viaja en `codigos[]` de la respuesta |
| `R-DESV-03` | Regla: el producto se desvió bruscamente de lo esperado | `C-53`, `C-54` · `motivo_desviacion` |
| `R-RANGO-01` | Ejemplo: regla de rango permitido de un campo | `C-29` y par de `A-32` (ronda 1k P9) · inventada |

### Reglas de error (`regla`) y alertas que viajan en el contrato

Las crea la ESPEC del contrato (rondas 1d a 1j). Van en `regla` de un `problem+json`: en la raíz en los
`403`, dentro de `errores[]` en los `422` (§4b, catálogo de `regla`).

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `ingesta_entregar` | `403`: el rol no tiene permiso de entregar | `A-22` · se detiene esa persona con su credencial (1k P7) |
| `empresa_distinta` | `403`: la credencial es de otra empresa | `A-22`, `C-46`, `C-68` · se detiene esa persona con su credencial (1k P7) |
| `sesion_ajena` | `403`: la sesión la abrió otra persona | `A-22`, `C-74` · abrir otra sesión; el dispositivo es de la parte 2 |
| `contrato_obligatorio` | `422`: la partición no trae el campo `contrato` | `A-22` fase ⟦1⟧ · `pointer: /contrato` |
| `contrato_mal_escrito` | `422`: `contrato` no cumple el formato de versión | `A-22` fase ⟦1⟧, `A-24` · p. ej. `"1.x"` |
| `anotacion_uuid_obligatorio` | `422`: a una anotación le falta su id | `C-14` · error dorado de §4b |
| `anotacion_uuid_invalido` | `422`: el id de una anotación no es UUID v7 en minúsculas | `A-38`, `C-25` · fase ⟦3⟧ |
| `consecutivo_obligatorio` | `422`: a una anotación le falta su consecutivo | `A-22` fase ⟦3⟧ |
| `consecutivo_invalido` | `422`: el consecutivo no es un entero válido | `A-22` fase ⟦3⟧, `C-63` · p. ej. `"13x"` |
| `sesion_no_declarada` | `422`: la anotación apunta a una sesión no declarada | `A-38`, `C-79` · trae `faltan[]`; el celular rearma |
| `cuerpo_incompleto` | `422`: el cuerpo llegó cortado (falla de transporte) | `A-54`, `C-86` · fase ⟦0⟧, sin `pointer`; se reintenta como un `5xx` |
| `json_ilegible` | `422`: un documento completo que no se lee | `A-32`, `A-41` · con `pointer` si cae en una anotación, sin él si cae en el sobre; anomalía (A-53): rearmar, nunca borrar |
| `profundidad_excedida` | `422`: anidamiento mayor que `max_profundidad_particion` | `A-32`, `C-82` · el `pointer` es fiable |
| `tipo_desconocido` | `422 contrato-incompatible`: `tipo` bien escrito pero desconocido | `A-32`, `C-18` · ya no es código de anotación |
| `sobre_invalido_repetido` | Alerta: mismo `particion_uuid` recibe un segundo `422` con otro cuerpo | `A-41`, `C-73` · memoria del ingreso por huella del cuerpo, una vez (1k P4-P5) |

### Códigos del ingreso que viajan en `codigos[]`

Los pone el ingreso del nodo de finca en `excepciones[].codigos` (A-23, §3b: `snake_case`). Viven en
`codigos/CodigoIngreso.java` (ESPEC §5). Los que tienen entrada propia en «Entradas» (`incompleta`,
`vieja`, `obsoleta`, `divergencia`) no se repiten aquí.

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `hora_dudosa` | Código: el sello de tiempo de la captura no es confiable | `A-17` · lo calcula el ingreso (semilla ingreso) |
| `hueco_consecutivo` | Código: llegó después de un consecutivo que falta | `A-27`, `C-8` |
| `datos_invalidos` | Código: fallo técnico, la anotación se rechaza por su forma | `A-32`, `C-10`, `C-19` · resultado `rechazada` |
| `motivo_ausente` | Código: falta el valor y falta su `motivo_ausencia` | `A-35`, `C-20` · junto a `incompleta` · **no es** el campo `motivo_ausencia` |
| `consecutivo_repetido` | Código: mismo dispositivo y consecutivo con otro id | `A-39`, `C-28` · trae `accion: registrar_de_nuevo` |
| `consecutivo_resuelto` | Código: el dispositivo nuevo reenvió la anotación repetida | `A-39`, `C-28` |
| `version_desconocida` | Código: `version_config`, `version_app` o `version_motor` desconocida | `A-32`, `A-50`, `C-30` · es anomalía |
| `desfase_desconocido` | Código: el celular nunca sincronizó, no declara desfase | `A-16`, `C-7` |
| `capturada_sin_credencial` | Código: capturada después de vencer la credencial de captura | `A-23` · ES-S12 |
| `desviacion_sin_motivo` | Código: una regla detectó desviación y faltó su `motivo_desviacion` | `A-49 ⟦4⟧`, `C-54` |

**Los dos motivos no se mezclan** (aclaración de Juan, 20-oct, ESPEC-dominio-ingesta ronda 1 P10). Nunca se escribe «motivo» a secas:

| Campo de `datos` | Explica | Cuándo viene | Si falta | Si sobra |
|---|---|---|---|---|
| `motivo_ausencia` | por qué **no hay cifra** | solo con `valor: null` | `incompleta` + `motivo_ausente` | con `valor` presente se contradice → `rechazada` `datos_invalidos` (A-35) |
| `motivo_desviacion` | por qué la cifra **es rara** | con `valor` presente, cuando una regla lo pide | `marcada` con el id de la regla + `desviacion_sin_motivo` | nunca sobra: un valor con desviación explicada se recibe `marcada` (A-49 ⟦4⟧) |
| `sin_minimo` | Código: la captura no trae el mínimo de su actividad | `A-46`, `C-55` · junto a `divergencia` |
| `orden_duplicada` | Código: dos capturas cumplen la misma orden sin poder superarse | `A-45 ⟦1⟧` · regla en la semilla ingreso |
| `fuera_de_asignacion` | Código: la cama no estaba asignada a ese dispositivo ni persona | `A-45 ⟦1⟧` · regla en la semilla ingreso |
| `dispositivo_no_registrado` | Código: entregó un celular sin registro en el nodo | `A-23`, `C-64` · ES-S13 |
| `reloj_movido` | Código: el desfase declarado y el medido difieren demasiado | `A-23`, `C-65` · ADR-031 §3 |

### Tipos de problema (`type`) de los errores de la partición

Van en el `type` de un `application/problem+json` (A-22), con el prefijo
`https://florlogic.co/problemas/ingesta/` (supuesto, lo fija Juan). Viven en `problemas/TipoProblema.java`.

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `credencial-invalida` | `401`: la credencial falta, es inválida o venció | `A-22` · el celular renueva y reintenta |
| `sin-permiso` | `403`: la persona no puede entregar esta partición | `A-22` · su `regla` dice por qué |
| `sesion-desconocida` | `404`: la sesión de sincronización de la ruta no existe | `A-22`, `C-12` · abrir otra |
| `sesion-cerrada` | `409`: la sesión de sincronización ya se cerró | `A-22`, `C-11` · abrir otra |
| `particion-en-curso` | `409`: otra partición del mismo dispositivo está procesándose | `A-22`, `C-22` · trae `Retry-After` |
| `particion-muy-grande` | `413`: la partición pasa un tope de tamaño | `A-22`, `C-13` · el celular la parte |
| `sobre-invalido` | `422`: la partición no se puede leer o identificar | `A-22`, `A-32` · su `regla` va en `errores[]` |
| `contrato-incompatible` | `422`: versión del contrato o tipo que no se conoce | `A-24`, `A-32` · es anomalía |
| `demasiadas-peticiones` | `429`: el dispositivo pasó `tasa_por_dispositivo` | `A-22`, `C-48` · trae `Retry-After` |
| `rechazado` | `422` provisional del canal: fallo de forma, sin motivo | Segmento 2, `ParticionesAdaptadorWeb` · regla por defecto de `D-17`; OBS-2-7 |
| `error-interno` | `500`: fallo del nodo de finca, sin detalles internos | `A-36`, `C-49` · reintentar |
| `nodo-ocupado` | `503`: nodo saturado o plazo de procesamiento vencido | `A-40`, `C-23` · trae `Retry-After` |
| `rechazado` (ingesta) | `422` del camino mínimo: cualquier fallo de forma, sin motivo | `ParticionesAdaptadorWeb.RECHAZADO` · Segmento 2 del contrato |

**Los del gateway** llevan el prefijo `https://florlogic.co/problemas/gateway/` y los genera Nginx, nunca el
backend (`ESPEC-gateway-finca` A-3):

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `gateway/rechazado` | Cualquier 4xx propio del gateway (y `501`, `505`), salvo el `403` del WAF | `despliegue/gateway/` · `ESPEC-gateway-finca` A-9, C-3 · `ESPEC-waf-finca` A-3 · no se reintenta igual |
| `gateway/no-disponible` | Cualquier 5xx propio de Nginx: el backend no está | `despliegue/gateway/` · `ESPEC-gateway-finca` A-9, C-4 · el celular reintenta |
| `gateway/bloqueado` | Cualquier `403` del WAF de la finca: bloqueó la petición | `despliegue/gateway/` · `ESPEC-waf-finca` A-3 (decidida 21-oct, ronda 1 P3) · también un `deny` de la base sin `status` · el celular retiene, no cambia de credencial |

### Reglas del WAF de la finca (ids de ModSecurity y de OWASP CRS)

Números que salen en el registro del WAF y en las exclusiones. Los `2000nn` son de la configuración base de la imagen
`owasp/modsecurity-crs`; los `9nnnnn`, de las reglas OWASP CRS 4.

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `2000nn` · `9nnnnn` | Regla del WAF: base de la imagen o de CRS | Registro del WAF · exclusiones de `ESPEC-waf-finca` |
| `200002` | Regla del WAF: cuerpo que no se puede leer → `400` | **Quitada** (`ESPEC-waf-finca` A-4, K-3); la reemplazan `10002` y `10003` para lo que no es UTF-8 simple |
| `200007` | Regla del WAF: más de 1000 campos en el cuerpo → `400` | **Quitada** (`ESPEC-waf-finca` A-5, K-4); la reemplaza `10001` |
| `949110` | Regla del WAF: la suma de anomalías pasó el umbral | La que bloquea un ataque con `403` |
| `959100` | Regla del WAF: la suma de anomalías de la respuesta | Vaciaba los errores del gateway (`ESPEC-waf-finca` K-7) |
| `100nn` | Regla propia del WAF de la finca (rango local 1–99.999) | `gateway/default.conf.template` · `ESPEC-waf-finca` |
| `10001` | Regla propia: 10.000 campos o más → `413` | Reemplaza a `200007`; el celular parte la partición (`ESPEC-waf-finca` A-5, COH-5) |
| `10002` | Regla propia: cuerpo ilegible con BOM o byte nulo → `400` | UTF-16 o UTF-32 (`ESPEC-waf-finca` A-14, K-8) |
| `10003` | Regla propia: cuerpo ilegible que no es UTF-8 → `400` | `@validateUtf8Encoding` (`ESPEC-waf-finca` A-14, K-8) |
| `920350` | Regla del WAF: `Host` con una IP numérica, 3 puntos | Quitada: salta en toda partición de la finca (`ESPEC-waf-finca` A-11, COH-6) |
| `920340` | Regla del WAF: cuerpo sin cabecera `Content-Type` | `403` en el gateway (`ESPEC-waf-finca` OBS-1-1) |
| `920420` | Regla del WAF: `Content-Type` fuera de los permitidos | `text/plain` → `403` (`ESPEC-waf-finca` OBS-1-1) |
| `941110` | Regla del WAF: XSS en cualquier cabecera, también `Authorization` | La que A-15 deja sin efecto (`ESPEC-waf-finca` OBS-1-3) |
| `942100` | Regla del WAF: inyección SQL detectada por libinjection; no revisa `Authorization` | La que detecta `' OR 1=1 --` (`ESPEC-waf-finca` §4b) |
| `950000-959999` | Reglas del WAF que revisan las respuestas | Quitadas: el WAF solo revisa lo que entra (`ESPEC-waf-finca` A-9) |

### Valores fijos que viajan en el contrato

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `fallo_tecnico` | Motivo de descarte: el celular borró una anotación inservible | `descartes[].motivo` (`A-47`) |
| `descartada_por_gerente` | Motivo de descarte: el gerente ordenó borrarla sin entregar | `descartes[].motivo` (`A-47`, `C-47`) |
| `registrar_de_nuevo` | Acción: el celular debe registrarse como dispositivo nuevo | `accion` de la respuesta (`A-39`) |
| `h1` | Versión 1 del algoritmo de huella (JCS + SHA-256) | `{"alg": "h1", …}` (`A-33`) |
| `D1` | Granularidad de la jornada: un día natural (por confirmar) | Apartada a la semilla configuración · P-S04 |

### Datos de ejemplo (no existen en producción)

| Código | Significa (5-10 palabras) | Uso: dónde vive y a qué llega |
|---|---|---|
| `D-0nn` (`D-014`, `D-022`, `D-031`, `D-099`) | Ejemplo: celular inventado para los casos C-n | ESPEC §4 · **choca con `D-0n` de la skill** |
| `S1`…`S9` | Ejemplo: sesión inventada, de sincronización o de captura | De sincronización en `C-74` y en el par de `A-52`; de captura en `C-79` y en el error dorado de `sesion_no_declarada` |
| `H1`, `H2` | Ejemplo: dos huellas distintas de una anotación | `C-75` |
| `d001`, `d002` | Ejemplo: `anotacion_uuid` abreviado de una anotación | Pares cumple/viola (§4b) |
| `b001` | Ejemplo: `particion_uuid` abreviado de la partición dorada | JSON dorado (§4b) |
| `b006`, `b007` | Ejemplo: `particion_uuid` abreviado de una partición nueva | Pares de `A-41` y de `A-29` (mitades de un `413`) |
| `C1`, `C2` | Ejemplo: dos cuerpos distintos de un `422` | Par de la alerta (`A-41`, rondas 1k P4-P5) · **no es `C-n`** |
| `b003`, `b004`, `b005` | Ejemplo: `particion_uuid` abreviado de una partición | Par de `A-41` (ronda 1j P3) |
| `c001`, `c009` | Ejemplo: uuid abreviado de una sesión de captura | JSON dorado y error dorado de `sesion_no_declarada` (§4b) |
| `a1` | Ejemplo: `anotacion_uuid` abreviado repetido en una partición | Par de `A-42` |
| P1…P4 (partición) | Ejemplo: partición inventada de un celular | `C-22` y pares de `A-41` · **choca con `Pn` de ronda** |
| Celulares A, B, C | Ejemplo: celulares inventados sin código de dispositivo | `C-22` |
| `op-07`, `op-12` | Ejemplo: operarios de campo inventados | Celular compartido (`C-74`) |
| `jti-5b2e`, `jti-9a1c`, `jti-77f0` | Ejemplo: identificador (`jti`) de una credencial inventada | JSON dorado (§4b) y `C-68` (credencial renovada) |
| `B3-C17-S1` | Ejemplo: sección, en formato bloque-cama-sección | JSON dorado (§4b) |
| `P-2026-031` | Ejemplo: id de una producción | JSON dorado · **choca con `P-S0n`** |

### Prefijos que chocan

| Prefijo | Sentidos | Cómo se distinguen hoy |
|---|---|---|
| `P` | Pregunta de ronda (`P9`) · pregunta a Juan (`P-S04`) · producción de ejemplo (`P-2026-031`) · partición de ejemplo (`P3` en `C-22` y en los pares de `A-41`) | Sin guion = ronda si va tras «ronda 1x», partición si va tras «partición»; letra de flujo = Juan; año = ejemplo |
| `D` | Regla de la skill (`D-04`) · celular de ejemplo (`D-014`) | Dos cifras = skill; tres cifras = celular |
| `PR` | Principio del repo (`PR-05`) · práctica de la plantilla de persistencia | Por el contexto de la cita |
| `S` | Sesión de ejemplo (`S1`), que puede ser de sincronización o de captura · sección dentro de `B3-C17-S1` | Por la posición; el texto del caso dice qué sesión es |

## Entradas

### Acuse · técnico
- **Es:** lo que el celular recuerda de cada anotación según la respuesta del nodo de finca: su resultado (`aplicada`, `marcada`, `rechazada` o `sin_efecto`) y sus códigos.
- **No es / se confunde con:** `confirmado_hasta`; el acuse es por anotación. Para borrar no basta cualquier acuse: la regla está en la semilla del celular (A-48, apartada del contrato el 18-oct). Tras restaurar el nodo de finca, un acuse viejo puede ser falso.
- **Fuente:** ESPEC-contrato-ingesta A-39, A-48, COH-13
- **Estado:** PROPUESTO

### Adaptador (hexagonal) · técnico
- **Es:** pieza de `infraestructura` que traduce un protocolo de afuera (HTTP, SQL, archivos) a un puerto: el controlador en `infraestructura/input/controlador` llama a un puerto de entrada; el adaptador de `infraestructura/output/adapter` implementa un puerto de salida. Ejemplo: `AnotacionesRecibidasAdaptadorJdbc`.
- **No es / se confunde con:** el **ensamblador**, que vive en `servicios/*/adapter` y solo convierte DTO ↔ dominio; comparten el nombre del paquete (`adapter`) pero no el papel ni el sufijo de clase.
- **Fuente:** diagrama de paquetes de Juan 22-oct-2026 · `codigo/IA_LEEME.md` «Dentro de cada módulo del backend» · Vernon · Implementing DDD p. 170
- **Estado:** ACORDADO 22-oct-2026

### Anfitrión · técnico
- **Es:** la máquina donde corre Docker; un contenedor la alcanza por el nombre `host.docker.internal`, y en las pruebas por `host.testcontainers.internal`.
- **No es / se confunde con:** el contenedor. En desarrollo el anfitrión es el computador de quien programa; en producción es el propio nodo de finca, así que «anfitrión» nombra el papel (donde corre Docker), no una máquina.
- **Fuente:** documentación de Docker Desktop · `ESPEC-gateway-finca` ronda 1
- **Estado:** PROPUESTO

### Agregado · técnico
- **Es:** un grupo de objetos del dominio que cambia junto, en una sola transacción, con una raíz que es la única puerta de entrada y que protege sus invariantes.
- **No es / se confunde con:** una tabla ni una entidad JPA; ni la partición, que es un trozo de lo que sube un celular.
- **Fuente:** Evans, *DDD Reference* (Aggregates) · plantilla entidad-agregado §1
- **Estado:** ACORDADO 20-oct-2026

### Anomalía · técnico
- **Es:** una situación que por diseño no debería ocurrir (por ejemplo, un celular con una versión que el nodo de finca no creó); si aparece, se alerta al administrador; con `contrato-incompatible` el celular no parte ni borra: por el `contrato` se detiene todo; por `tipo_desconocido`, retiene esa partición y sigue (A-41).
- **No es / se confunde con:** fallo técnico, que sí se espera y se resuelve borrando y recapturando.
- **Fuente:** ESPEC-contrato-ingesta A-50
- **Estado:** PROPUESTO

### Anotación · técnico
- **Es:** un registro que el celular guarda y entrega al nodo de finca, con identidad propia (UUID v7), un número consecutivo y un tipo; hoy solo existe `captura.campo`.
- **No es / se confunde con:** el dato de negocio que se captura; la anotación es el sobre que lo lleva.
- **Fuente:** ESPEC-contrato-ingesta A-1, A-15
- **Estado:** PROPUESTO

### Anotación aislada · técnico
- **Es:** la anotación que el celular deja fuera de la partición porque su lectura previa falla (validar al armar, A-53); nunca viaja ni entra al nodo de finca. Un `422` ya no aísla nada (ronda 1k P3), y un `413` tampoco: parte la partición en dos.
- **No es / se confunde con:** anotación rechazada, que sí entró y quedó registrada.
- **Fuente:** ESPEC-contrato-ingesta A-41
- **Estado:** PROPUESTO

### Anotación corrupta · técnico
- **Es:** una anotación que llega con el mismo `anotacion_uuid` que otra ya guardada o que una constancia, pero con otro contenido (otra huella); es fallo técnico: se rechaza, se alerta, y el celular la borra con lápida y pide recapturar.
- **No es / se confunde con:** un reenvío, que trae el mismo contenido y cuenta `sin_efecto`.
- **Fuente:** ESPEC-contrato-ingesta A-2, C-4 (antes ADR-027 §2, deprecado, K-41)
- **Estado:** PROPUESTO

### Anotación marcada · técnico
- **Es:** una anotación que entra y cuenta para el estado, pero lleva uno o más códigos que la **resaltan para que el gerente de producción haga las correcciones**.
- **No es / se confunde con:** anotación rechazada, que no cuenta; ni la marca de agua.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · ESPEC-contrato-ingesta A-23
- **Estado:** ACORDADO 16-oct-2026

### Anotación obsoleta · técnico
- **Es:** una captura que llega cuando su orden de captura **fue reasignada a otro dispositivo o persona** y ésa ya la cumplió con una captura posterior y verificada, en el mismo (sección, campo); no entra al estado ni se guarda el dato, solo una constancia. Sin reasignación, decide RF-022 y se guardan las dos.
- **Variante:** «superada»: la vieja que llegó antes y ya estaba guardada; al llegar la que cumple la orden, sale del estado y de la historia y queda su constancia.
- **No es / se confunde con:** anotación vieja, que se mide por su edad; ni el registro que pierde en RF-022, que sí se guarda.
- **Ejemplo:** la cama 12 la capturó primero el turno de la tarde; la del turno de la mañana llega después y queda obsoleta.
- **Fuente:** aclaración de Juan 16-oct (ronda 1e P3) · ADR-026
- **Estado:** ACORDADO 16-oct-2026

### Anotación rechazada · técnico
- **Es:** una anotación que llegó al nodo de finca, quedó registrada, pero no cuenta para el estado porque su forma no cumple el contrato o es corrupta.
- **No es / se confunde con:** anotación marcada, que sí cuenta; ni anotación aislada, que nunca entró.
- **Fuente:** ESPEC-contrato-ingesta A-32, A-31
- **Estado:** PROPUESTO

### Anotación vieja · técnico
- **Es:** una anotación que lleva más de 3 días capturada sin llegar al nodo de finca; todavía se entrega, pero queda marcada para que el gerente decida si se recaptura.
- **No es / se confunde con:** anotación obsoleta.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · ADR-025 §2 (el umbral es decisión de negocio)
- **Estado:** ACORDADO 16-oct-2026

### Asignación (orden de captura) · negocio
- **Es:** la orden de capturar una cama, dada a un dispositivo o a un usuario para una jornada; si nadie la sincronizó, se puede volver a dar ese mismo día a otro trabajador de otro turno.
- **No es / se confunde con:** la captura misma; la asignación es lo esperado, la captura es lo recibido.
- **Ejemplo:** la cama 12 le toca a D-014 (turno de 6 a 2) el 16-oct; como no llegó, se reasigna a D-022 (turno de 2 a 10) el mismo día.
- **Fuente:** ADR-026 («la asignación va acotada en el tiempo») · aclaración de Juan 16-oct (ronda 1e)
- **Estado:** ACORDADO 16-oct-2026

### Aviso al gerente · técnico
- **Es:** el mensaje que el nodo de finca deja para el gerente de producción cuando una anotación entra marcada o queda rechazada, para que la revise; se da una vez por causa y su falla nunca detiene el ingreso.
- **No es / se confunde con:** la alerta al administrador (fallos técnicos del sistema); ni el patrón Notification (lista de reglas rotas); ni el aviso de descarte, que manda el celular.
- **Fuente:** encargo de Juan 20-oct (IA_RETOMAR-AQUI) · ESPEC-dominio-ingesta
- **Estado:** PROPUESTO

### Aviso de descarte · técnico
- **Es:** la línea que el celular envía en su siguiente partición para decir qué consecutivo borró sin entregar y por qué; el nodo de finca cierra ese hueco y lo deja en la bitácora.
- **No es / se confunde con:** una anotación: no lleva datos del campo.
- **Ejemplo:** `{ "consecutivo": 137, "motivo": "fallo_tecnico" }`.
- **Fuente:** ESPEC-contrato-ingesta A-47
- **Estado:** ACORDADO 16-oct-2026

### Avance del dispositivo · técnico
- **Es:** lo que el nodo de finca sabe de la entrega de un celular: qué consecutivos ya recibió y, por tanto, su `confirmado_hasta`. Es un agregado propio (A-60); en el código, `AvanceDelDispositivoAgregado`.
- **No es / se confunde con:** la marca de agua, que es solo el número; ni la sesión de sincronización, que es una subida concreta.
- **Fuente:** ESPEC-dominio-ingesta ronda 1 P1, A-60 · A-31 (semilla ingreso)
- **Estado:** ACORDADO 20-oct-2026

### Barrera de lectura · técnico
- **Es:** el único punto (`LectorDto`) donde un árbol JSON se convierte en DTO; nunca lanza: devuelve `LecturaResultado` = `Leida` o `Fallida` con `pointer` y clase del fallo, sin datos de negocio.
- **No es / se confunde con:** el validador (Segmento 5), que decide si una entrada se acepta; la barrera solo evita que leer pare el proceso.
- **Fuente:** ESPEC-contrato-ingesta A-57
- **Estado:** PROPUESTO

### Blindaje · técnico
- **Es:** guardar un campo desconocido como texto inerte, con topes, sin convertirlo en objetos ni evaluarlo nunca.
- **No es / se confunde con:** aceptar el campo; ya no se usa: ningún campo desconocido entra (A-25 retirada).
- **Fuente:** ESPEC-contrato-ingesta A-25
- **Estado:** REEMPLAZADO 16-oct-2026 · ver Fallo técnico (ronda 1e P8)

### Camino mínimo funcional · técnico
- **Es:** la ruta más corta que hace funcionar una transacción de punta a punta, con reglas por defecto simples; lo que no lo rompe queda para «después».
- **No es / se confunde con:** el producto completo ni los 18 pasos de TX-1; tampoco el MVP de negocio.
- **Ejemplo:** el simulador manda una partición, el backend la guarda y responde `200`; todo fallo de forma es `422` sin motivo.
- **Fuente:** skill `detallar-para-codificar` D-17 · LEEME de transacciones, TX-1 `AJUSTAR 19-oct`
- **Estado:** ACORDADO 20-oct-2026

### Ciclo de paquetes · técnico
- **Es:** dos paquetes que se usan entre sí, de modo que ninguno se entiende, compila ni prueba sin el otro; en el dominio de ingesta lo prohíbe una regla ArchUnit (A-76).
- **No es / se confunde con:** una dependencia en un solo sentido, que sí está permitida (`anotacion` → `comun`).
- **Fuente:** ESPEC-dominio-ingesta K-116, A-76
- **Estado:** PROPUESTO

### DTO de entrada y de salida · técnico
- **Es:** `record` simple que cruza un puerto, sin dominio adentro: el de entrada (`servicios/input/dto`, sufijos `EntradaDto` y `RespuestaDto`) va y vuelve por el puerto de entrada; el de salida (`servicios/output/dto`, sufijo `SalidaDto`) cruza el puerto de salida y es distinto al de entrada.
- **No es / se confunde con:** el `Dto` del módulo `contrato`, que es el JSON del cable entre el celular y el nodo; ni un objeto de valor (`Valor`), que sí tiene reglas.
- **Fuente:** diagrama de paquetes de Juan 22-oct-2026 · `codigo/IA_LEEME.md` «Dentro de cada módulo del backend» · Martin · Clean Architecture p. 185 y p. 195
- **Estado:** ACORDADO 22-oct-2026

### Ensamblador · técnico
- **Es:** objeto aparte que convierte DTO ↔ dominio para que ninguno dependa del otro (Assembler de Fowler, un Mapper). El de entrada implementa el puerto de entrada y llama al caso de uso; el de salida convierte el dominio en DTO de salida. Viven en `servicios/input/adapter` y `servicios/output/adapter`.
- **No es / se confunde con:** el **adaptador** de infraestructura (HTTP, JDBC); ni el caso de uso, que decide: el ensamblador solo traduce.
- **Fuente:** diagrama de paquetes de Juan 22-oct-2026 · `codigo/IA_LEEME.md` «Dentro de cada módulo del backend» · Fowler · PoEAA secc. 100
- **Estado:** ACORDADO 22-oct-2026

### impl (paquete) · técnico
- **Es:** el subpaquete `servicios/casosuso/impl`, donde viven las clases que implementan las interfaces de caso de uso (sufijo `CasoUsoImpl`).
- **No es / se confunde con:** `casosuso`, donde viven solo las interfaces.
- **Fuente:** diagrama de paquetes de Juan 22-oct-2026 · `codigo/IA_LEEME.md` «Dentro de cada módulo del backend»
- **Estado:** ACORDADO 22-oct-2026

### Núcleo compartido · técnico
- **Es:** el `dominio` de la raíz del backend (`co.florlogic.finca.dominio`): el subconjunto pequeño y acordado del dominio que usan todos los módulos (Shared Kernel). Hoy vacío.
- **No es / se confunde con:** el `dominio` de cada módulo, que es solo suyo.
- **Fuente:** diagrama de paquetes de Juan 22-oct-2026 · `codigo/IA_LEEME.md` «Dentro de cada módulo del backend» · Evans · Domain Driven Design secc. 112
- **Estado:** ACORDADO 22-oct-2026

### Package-private · técnico
- **Es:** en Java, lo que se declara sin `public`: solo lo ve el mismo paquete. Sirve para esconder lo interno de un agregado.
- **No es / se confunde con:** `private`, que es solo de la clase.
- **Fuente:** ESPEC-dominio-ingesta ronda 2 P6
- **Estado:** PROPUESTO

### Captura incompleta · negocio
- **Es:** una captura válida a la que le falta producto o un dato no obligatorio, con motivo o sin él; sí llega al sistema y queda para el gerente.
- **No es / se confunde con:** captura sin el mínimo requerido, que nunca llega; ni fallo técnico.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · P-S01 · ESPEC-contrato-ingesta A-35
- **Estado:** ACORDADO 16-oct-2026

### Captura válida · negocio
- **Es:** la captura de una cama que tiene completo el mínimo requerido de su actividad; solo así se vuelve anotación y puede llegar al sistema.
- **No es / se confunde con:** captura incompleta, que tiene el mínimo pero le falta producto o algún dato no obligatorio.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · ADR-024 (la validación va antes del evento)
- **Estado:** ACORDADO 16-oct-2026

### Catálogo de configuración · negocio
- **Es:** lo que el nodo de finca define y versiona (actividades, campos, motivos, variedades, rangos, reglas `R-…`) y entrega al celular dentro del paquete de configuración (CT-01).
- **No es / se confunde con:** el catálogo de códigos, que es una lista fija dentro del contrato; ni el catálogo de mensajes, que guarda textos para personas.
- **«Catálogo» sin apellido no se usa** (Juan, 21-oct): siempre «de configuración», «de códigos» o «de mensajes».
- **Fuente:** C4 `k-b-cfg` · ADR-029 · Juan, 21-oct-2026
- **Estado:** ACORDADO 21-oct-2026

### Catálogo de códigos · técnico
- **Es:** una lista fija de ids que viajan como texto en el JSON (`CodigoIngreso`, los enums del contrato que implementan `IdDeCatalogoInterfaz`); cambia solo con una versión nueva del contrato.
- **No es / se confunde con:** el catálogo de configuración, que cambia sin cambiar el código; ni el catálogo de mensajes.
- **Fuente:** `contrato/codigos/CodigoIngreso.java` · CT-03 · Juan, 21-oct-2026
- **Estado:** ACORDADO 21-oct-2026

### Catálogo de mensajes · técnico
- **Es:** el servicio que guarda, fuera del código, el texto para una persona de cada mensaje, buscado por una llave (un código); el código nombra la llave y nunca escribe el texto (Juan, 21-oct: «los mensajes no deben vivir en código pelado o crudo»).
- **No es / se confunde con:** el catálogo de códigos, que solo da el id; ni el catálogo de configuración, aunque el texto de las reglas pueda viajar en él (C4 `k-b-cfg`, CN-22).
- **Fuente:** Juan, 21-oct-2026 · OBS-1-6 de ESPEC-dominio-ingesta
- **Alcance (Juan, 21-oct):** todo texto; una excepción lleva solo su llave y el valor.
- **Dónde vive (Juan, 21-oct):** módulo propio `mensajes` del backend; API `MensajesPuerto`, sobre Spring `MessageSource`.
- **Estado:** ACORDADO 21-oct-2026

### Clave del hecho · técnico
- **Es:** la tupla que identifica un hecho, declarada por campo en el catálogo; por defecto producción · sección · campo · jornada. Dos anotaciones con la misma clave hablan del mismo hecho.
- **No es / se confunde con:** el `anotacion_uuid`, que identifica la anotación, no el hecho.
- **Fuente:** ESPEC-contrato-ingesta A-3 (antes ADR-027 §3, deprecado, K-41)
- **Estado:** PROPUESTO

### `confirmado_hasta` · técnico
- **Es:** el mayor consecutivo que el nodo de finca ya recibió sin huecos por debajo; le dice al celular desde dónde seguir enviando.
- **No es / se confunde con:** `respaldado_hasta`. Recibido no es respaldado: el celular no borra hasta aquí.
- **Ejemplo:** recibidas 1-200 y 401-600 → `confirmado_hasta = 200`.
- **Fuente:** ESPEC-contrato-ingesta A-26, A-27
- **Estado:** PROPUESTO

### Consecutivo · técnico
- **Es:** el número que cada celular pone a sus anotaciones, 1, 2, 3…, en el orden en que las guarda; solo tiene sentido dentro de un mismo celular.
- **No es / se confunde con:** el `anotacion_uuid`, que identifica la anotación. El 812 del celular A y el 812 del celular B son anotaciones distintas.
- **Fuente:** ESPEC-contrato-ingesta A-27 · A-7 (apartada a la semilla celular el 18-oct) (antes ADR-027 §5, deprecado, K-41)
- **Estado:** PROPUESTO

### Consolidación · técnico
- **Es:** reescribir la ESPEC con las reglas vigentes, sin la historia de rondas, para que no se contradigan entre sí; la historia queda en un anexo.
- **No es / se confunde con:** una ronda de preguntas: no decide nada nuevo.
- **Fuente:** decisión de Juan 16-oct (tras la ronda 1f)
- **Estado:** ACORDADO 16-oct-2026

### Constancia · técnico
- **Es:** la línea que el nodo de finca deja en la bitácora de una captura obsoleta o superada: quién, qué cama, qué jornada y qué la superó. Guarda el `anotacion_uuid` y la huella (el uuid es único entre anotaciones y constancias) y cuenta como recibida para las marcas de agua. La de una obsoleta tardía no guarda el dato; la de una **superada** conserva sus bytes. Una superada vuelve con el código `obsoleta`: no existe un código `superada`.
- **No es / se confunde con:** la lápida, que guarda el celular.
- **Fuente:** ESPEC-contrato-ingesta A-45 ⟦1⟧
- **Estado:** PROPUESTO

### Corrección · negocio
- **Es:** el cambio que hace el gerente de producción sobre información ya registrada, al revisar lo marcado y las desviaciones; no la hace el operario de campo.
- **No es / se confunde con:** la edición local del operario antes de sincronizar, ni la recaptura de una cama; tampoco `corrige_a`.
- **Fuente:** aclaración de Juan 16-oct (ronda 1e P4)
- **Estado:** ACORDADO 16-oct-2026

### `corrige_a` · técnico
- **Es:** el campo de una anotación que apunta al id de otra anotación del mismo hecho para corregirla; puede apuntar a una de otro celular o que todavía no ha llegado.
- **No es / se confunde con:** la corrección que hace el gerente de producción en el tablero, que es una tarea de revisión, no una anotación.
- **Ejemplo:** el operario anotó 40 tallos por error y luego anota 45 con `corrige_a` apuntando a la de 40.
- **Fuente:** ADR-027 §4 · ESPEC-contrato-ingesta A-4, A-28
- **Estado:** REEMPLAZADO 16-oct-2026 · ver Corrección, Edición local (sale del contrato, ronda 1e P4)

### Cuarentena · técnico
- **Es:** guardar aparte, fuera del estado, los bytes de una anotación rechazada por corrupta, para poder revisar cuál de las dos copias era la buena.
- **No es / se confunde con:** la constancia, que es de obsoletas y superadas.
- **Fuente:** ESPEC-contrato-ingesta A-2 (ronda 1h P9, COH-57)
- **Estado:** ACORDADO 16-oct-2026

### Dato ausente · técnico
- **Es:** no hay cifra; el celular lo declara con `valor: null` y, si lo sabe, su `motivo_ausencia`; entra como captura incompleta.
- **No es / se confunde con:** desviación, donde sí hay cifra pero rara; ni hueco, que es un consecutivo que no llegó.
- **Fuente:** ESPEC-contrato-ingesta A-20, A-35
- **Estado:** PROPUESTO

### `desfase_declarado_ms` · técnico
- **Es:** la diferencia entre el reloj del celular y el del nodo de finca, medida en la última sincronización; `null` o ausente si el celular nunca sincronizó, que valen lo mismo (A-33).
- **No es / se confunde con:** el sello normalizado, que el nodo de finca calcula con este valor.
- **Fuente:** ADR-031 §2 · ESPEC-contrato-ingesta A-16
- **Estado:** PROPUESTO

### Desviación · negocio
- **Es:** un cambio brusco o raro en la cantidad de producto, según las reglas del catálogo; es el único caso en que el operario debe dar un motivo, en la primera captura o en la siguiente.
- **No es / se confunde con:** divergencia (el celular y el nodo de finca juzgan distinto) ni dato ausente.
- **Fuente:** aclaración de Juan 16-oct (ronda 1e P4) · RF-011
- **Estado:** ACORDADO 16-oct-2026

### Detener · técnico
- **Es:** que el celular deje de enviar con una credencial o del todo, sin borrar nada, hasta que algo cambie: una credencial nueva, otra persona o actualizar el lado incompatible.
- **No es / se confunde con:** retener, que guarda solo una partición mientras las demás siguen.
- **Ejemplo:** tras `403 empresa_distinta` se detiene op-12 con su credencial; con un `contrato` desconocido se detiene todo el celular.
- **Fuente:** ESPEC-contrato-ingesta A-41 (rondas 1j P5 y 1k P7)
- **Estado:** PROPUESTO

### Divergencia · técnico
- **Es:** el nodo de finca y el celular juzgan distinto la misma anotación con la misma versión de reglas; es un fallo del motor, no del operario, y entra marcada.
- **No es / se confunde con:** fallo técnico, que se descarta y se recaptura; ni producto faltante.
- **Fuente:** ADR-014 · IA_TX1 ES-S11 · ESC-57 · ESPEC-contrato-ingesta A-32
- **Estado:** ACORDADO 16-oct-2026

### Edición local · técnico
- **Es:** el cambio que el operario hace en su celular a una captura que todavía no salió en ninguna partición; reemplaza el valor sin dejar rastro y solo viaja la versión final.
- **No es / se confunde con:** corrección (la hace el gerente) ni recaptura (una captura nueva de la misma cama).
- **Ejemplo:** captura cama 2 → cama 4 → cambia los tallos de la cama 2; viaja una sola captura de la cama 2, con el valor final.
- **Fuente:** aclaración de Juan 16-oct (ronda 1e P4)
- **Estado:** ACORDADO 16-oct-2026

### Entidad · técnico
- **Es:** un objeto del dominio que se reconoce por su identidad y no por sus datos: aunque cambie de estado, sigue siendo el mismo (por ejemplo, una anotación con su `anotacion_uuid`).
- **No es / se confunde con:** el objeto de valor, que no tiene identidad; ni la entidad JPA, que es una clase de persistencia.
- **Fuente:** Evans, *DDD Reference* (Entities) · *Domain-Driven Design Distilled · p. 85*
- **Estado:** PROPUESTO

### Espera creciente · técnico
- **Es:** el tiempo que el celular espera antes de reintentar, que se duplica en cada intento fallido hasta un tope, con un poco de azar para que no reintenten todos a la vez.
- **No es / se confunde con:** `Retry-After`, que es la espera que pide el nodo de finca en su respuesta.
- **Ejemplo:** 1 s, 2 s, 4 s, … hasta 5 min.
- **Fuente:** ESPEC-contrato-ingesta A-40
- **Estado:** PROPUESTO

### Evento de dominio · técnico
- **Es:** un hecho del negocio ya ocurrido, en pasado e inmutable («AnotacionMarcada»), que el agregado anuncia para que otros módulos reaccionen después, fuera de su transacción.
- **No es / se confunde con:** el aviso al gerente, que es una de las reacciones posibles; ni un mensaje del contrato.
- **Fuente:** Evans, *DDD Reference* (Domain Events) · plantilla entidad-agregado §1 ⟦10⟧
- **Estado:** ACORDADO 20-oct-2026

### Fallo técnico · técnico
- **Es:** una anotación que no se puede usar por cómo está construida: JSON mal formado, datos sin sanear o cualquier error de software; se descarta y se pide recapturar.
- **No es / se confunde con:** captura incompleta, que es un hecho del campo y no un error.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d)
- **Estado:** ACORDADO 16-oct-2026

### Falso positivo · técnico
- **Es:** una petición legítima que el WAF de la finca toma por ataque, por ejemplo una partición válida cuyo texto se parece a una inyección; si el WAF bloquea, la partición no llega al backend.
- **No es / se confunde con:** el `422` «rechazado» del backend, que es una partición con forma inválida; ni un ataque real detectado.
- **Ejemplo:** una nota de campo que dice «cama 3 -- revisar» y salta una regla de inyección SQL.
- **Fuente:** `IA_PRIORIDAD-componentes-TX1.md` (WAF, «en APIs JSON produce falsos positivos») · ESPEC-waf-finca
- **Estado:** ACORDADO 21-oct-2026 (Jeronimo, encuadre del WAF)

### Gateway de la finca · técnico
- **Es:** la única puerta de red del nodo de finca: recibe lo que llega del celular o del navegador, aplica las protecciones de borde y lo reenvía al backend sin cambiarlo.
- **No es / se confunde con:** el backend, que decide si una partición vale; el ingreso de sincronización, que es un módulo del backend. Tampoco verifica permisos por sí solo.
- **Ejemplo:** Nginx en el puerto del nodo de finca reenvía `POST /ingesta/v1/sesiones/{id}/particiones` al backend.
- **Fuente:** TEC-15 · `IA_FUNCIONAMIENTO-ESPERADO.md` (API Gateway de la finca) · `IA_TX1` paso 4
- **Estado:** ACORDADO 20-oct-2026

### Gerente de producción · negocio
- **Es:** la persona de la finca que revisa lo capturado en la jornada y corrige lo que quedó marcado; recibe los avisos del ingreso en su celular y en su correo.
- **No es / se confunde con:** el administrador del nodo de finca, que atiende fallos técnicos; ni el operario de campo, que captura.
- **Fuente:** aclaración de Juan 16-oct (anotación marcada) · encargo de Juan 20-oct
- **Estado:** PROPUESTO

### Hueco · técnico
- **Es:** un consecutivo que falta entre dos que sí llegaron al nodo de finca, por ejemplo cuando una partición falló y la siguiente entró.
- **No es / se confunde con:** un dato ausente (`valor: null`), que sí llegó pero sin cifra; ni una **cama asignada que no se capturó**: sin captura no hay anotación ni consecutivo, así que no hay número que falte (aclaración de Juan, 20-oct).
- **Ejemplo:** llegaron 1-200 y 401-600 → el hueco es 201-400. La cama 25 asignada y no visitada no abre ningún hueco.
- **Fuente:** ESPEC-contrato-ingesta A-27 · ESPEC-dominio-ingesta A-71
- **Estado:** ACORDADO 20-oct-2026

### Huella · técnico
- **Es:** el SHA-256 de la anotación recibida en forma canónica (RFC 8785, sin miembros nulos), con la versión de su algoritmo; detecta si el mismo id llega con otro contenido.
- **No es / se confunde con:** el `anotacion_uuid`, que identifica la anotación pero no su contenido.
- **Fuente:** ESPEC-contrato-ingesta A-19, A-33
- **Estado:** PROPUESTO

### `IdYaRecibido` · técnico
- **Es:** lo que devuelve `IdYaRecibido.ante(id, dispositivo, huella guardada, huella llegada)` (estático, A-81) cuando llega un id que ya estaba guardado: su `Resultado` (`SIN_EFECTO` si es un reenvío, `RECHAZADA` si es una corrupta) y el evento opcional de la corrupta.
- **No es / se confunde con:** un reenvío, que es solo uno de sus dos casos.
- **Fuente:** ESPEC-dominio-ingesta A-77
- **Estado:** PROPUESTO

### Invariante · técnico
- **Es:** una regla que el sistema cumple siempre, en cualquier estado, y que una prueba puede comprobar.
- **No es / se confunde con:** una validación de entrada, que se revisa una vez al recibir un dato.
- **Ejemplo:** `respaldado_hasta ≤ confirmado_hasta`.
- **Fuente:** COH-13
- **Estado:** PROPUESTO

### Jib · técnico
- **Es:** el plugin de Maven que construye la imagen de contenedor de una aplicación Java sin Dockerfile y de forma reproducible (TEC-28).
- **No es / se confunde con:** Docker Compose, que arranca los contenedores; Jib solo arma la imagen.
- **Fuente:** TEC-28 · `IA_TECNOLOGIAS-vigentes.md` §3.2
- **Estado:** PROPUESTO

### Jornada de captura · negocio
- **Es:** la salida de un operario a revisar lo que le asignaron: va sin internet, captura las camas y vuelve a la oficina a sincronizar.
- **No es / se confunde con:** la jornada de la clave del hecho (el día al que pertenece un dato); ni la sesión de sincronización.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · ADR-026 (asignación)
- **Estado:** ACORDADO 16-oct-2026

### Juicio de la anotación · técnico
- **Es:** la decisión que toma el nodo de finca con cada anotación recibida: `aplicada` (entra limpia), `marcada` (entra con códigos para el gerente), `rechazada` (queda registrada, no cuenta) o `sin_efecto` (ya estaba).
- **No es / se confunde con:** la validación de forma del contrato, que decide el `422` de la partición entera antes del juicio; ni `veredicto_local`, que es el juicio del celular.
- **Precisión (COH-125, por confirmar con Juan):** en el modelo, solo `aplicada`, `marcada` y `rechazada` quedan guardadas como juicio de una `Anotacion`; `sin_efecto` es el `Resultado` de una llegada cuyo id ya existía (A-66, A-77), no un juicio guardado.
- **Fuente:** ESPEC-contrato-ingesta A-2, A-23, A-32 · semilla caso de uso ingreso
- **Estado:** ACORDADO 20-oct-2026

### Lector tolerante · técnico
- **Es:** leer un mensaje sin romperse cuando trae algo nuevo: un texto de catálogo que no se conoce se lee como `DESCONOCIDO` y quien lo usa decide (Fowler, TolerantReader).
- **No es / se confunde con:** aceptar lo desconocido. En el nodo de finca lo desconocido se sigue rechazando (A-50); solo que se rechaza lo suyo, sin tumbar lo demás (A-55). Tampoco es el lector estricto. Tampoco es el «blindaje» retirado (A-25), que guardaba lo desconocido.
- **En el código:** `LectorDto.tolerante()`, que usa el celular para leer respuestas (A-9).
- **Fuente:** ESPEC-contrato-ingesta A-9, A-57 · plantilla endpoint EP-16 · antes A-43, K-42
- **Estado:** PROPUESTO

### Lector estricto · técnico
- **Es:** `LectorDto.estricto()`, con que el nodo de finca lee lo que entra: un campo que el contrato no conoce, o un `null` en un número, es `Fallida` con su `pointer` (A-9). Se usa pieza por pieza (A-58).
- **No es / se confunde con:** el validador del Segmento 5, que además aplica el esquema y los topes; ni el lector tolerante.
- **Fuente:** ESPEC-contrato-ingesta A-9, A-58
- **Estado:** PROPUESTO

### Lápida · técnico
- **Es:** el registro mínimo (consecutivo y motivo) que el celular guarda de algo que borró sin respaldo, para volver a avisarlo si una marca de agua baja.
- **No es / se confunde con:** el aviso de descarte, que es el envío; la lápida es lo que el celular conserva.
- **Fuente:** revisión de coherencia ronda 1d, COH-19 · ESPEC-contrato-ingesta A-48 (apartada a la semilla celular el 18-oct); viaja como aviso de descarte (A-47)
- **Estado:** ACORDADO 16-oct-2026

### Login local · técnico
- **Es:** la entrada del operario en su celular, sin red, que mantiene su identidad presente al capturar; sigue funcionando aunque la credencial haya vencido.
- **No es / se confunde con:** la renovación de la credencial, que se hace en la oficina contra el nodo de finca.
- **Fuente:** aclaración de Juan 16-oct (ronda 1f P9) · ADR-007
- **Estado:** ACORDADO 16-oct-2026

### Lote · negocio
- **Es:** una plantación de una variedad, en un periodo con fechas reales, que puede ocupar varias secciones (y por tanto varios bloques y camas).
- **No es / se confunde con:** partición. No se captura el lote; se capturan las camas.
- **Fuente:** ADR-024 §2 · ESPEC-contrato-ingesta A-30
- **Estado:** ACORDADO 16-oct-2026

### `motivo_ausencia` · técnico
- **Es:** el id del catálogo que explica por qué una captura **no trae cifra** (`valor: null`); con él la anotación entra marcada `incompleta`.
- **No es / se confunde con:** `motivo_desviacion`, que explica una cifra rara que sí vino. Un valor **con** `motivo_ausencia` se contradice y es fallo técnico (A-35).
- **Fuente:** ESPEC-contrato-ingesta A-20, A-35 · aclaración en ESPEC-dominio-ingesta ronda 1 P10 (20-oct)
- **Estado:** PROPUESTO

### `motivo_desviacion` · técnico
- **Es:** el id del catálogo que explica por qué una cifra que sí vino es más baja o distinta de lo esperado (por ejemplo, `helada`); la anotación se recibe y entra marcada con el id de la regla.
- **No es / se confunde con:** `motivo_ausencia`, que explica que no hay cifra.
- **Fuente:** ESPEC-contrato-ingesta A-20, A-49 ⟦4⟧, C-53, C-54
- **Estado:** PROPUESTO

### Marca de agua · técnico
- **Es:** un número que dice hasta dónde se llegó, sin huecos, en una secuencia; aquí son `confirmado_hasta` y `respaldado_hasta`, una pareja por celular.
- **No es / se confunde con:** el acuse, que es por anotación.
- **Fuente:** ESPEC-contrato-ingesta A-26, A-31
- **Estado:** PROPUESTO

### Modo del WAF · técnico
- **Es:** cómo actúa el WAF de la finca cuando una petición parece ataque: **bloquear** (`On`: responde `403` y no la deja llegar al backend) o **solo detectar** (`DetectionOnly`: la anota en el registro y la deja pasar).
- **No es / se confunde con:** el nivel de paranoia, que decide cuántas reglas se aplican, no qué se hace cuando una salta.
- **Ejemplo:** en modo bloquear, `' OR 1=1 --` en `persona_id` da `403`; en solo detectar, llega al backend y queda en el registro.
- **Fuente:** directiva `SecRuleEngine` de ModSecurity · ESPEC-waf-finca §5 (comprobado 21-oct)
- **Estado:** PROPUESTO

### Nodo de finca (nodo principal) · técnico
- **Es:** la máquina en las oficinas de cada empresa donde corre FlorLogic con su base de datos; los celulares le entregan las anotaciones por la red local, sin internet.
- **No es / se confunde con:** la nube (servicios en línea), que solo guarda respaldos y distribuye versiones; el celular, que es el registro durante la jornada; ni el nodo de contingencia (OC-2). **Se dice «nodo de finca» o «nodo principal», nunca «nodo» a secas.**
- **Ejemplo:** el computador de la oficina de la finca al que se sincronizan los celulares al volver del campo.
- **Fuente:** documento de alternativa de solución (§ «Un solo producto, instalado N veces») · CN-37 · ADR-016
- **Estado:** ACORDADO 16-oct-2026

### Notification · técnico
- **Es:** patrón en que una comprobación no lanza al primer fallo, sino que devuelve la lista de reglas rotas (Fowler). En el contrato: `incoherencias()` de `ConteoDto` y `ResultadoParticionDto`.
- **No es / se confunde con:** el aviso al gerente ni la alerta al administrador.
- **Fuente:** ESPEC-contrato-ingesta A-57
- **Estado:** PROPUESTO

### Nube (servicios en línea) · técnico
- **Es:** la capa compartida fuera de la finca que guarda respaldos cifrados, distribuye versiones y ofrece IA; la finca no depende de ella para operar.
- **No es / se confunde con:** el nodo de finca, donde vive el dato vivo.
- **Fuente:** documento de alternativa de solución · CN-17 · ESC-59
- **Estado:** PROPUESTO

### Null Object · técnico
- **Es:** un patrón para que «no hay dato» sea un valor con nombre y no un `null`. En el contrato son interfaces selladas con dos variantes: `DesfaseDto` (conocido o desconocido) y `ValorDto` (hay valor o falta, con su `Motivo`).
- **No es / se confunde con:** el `null` del JSON. En el JSON, un miembro `null` vale lo mismo que uno ausente (A-33); en Java los dos se leen como la variante «no hay».
- **Fuente:** ESPEC-contrato-ingesta A-34, A-35
- **Estado:** PROPUESTO

### Objeto de valor · técnico
- **Es:** un dato del dominio sin identidad propia, inmutable y que se valida al crearse (por ejemplo, la huella o el consecutivo); dos con el mismo contenido son el mismo.
- **No es / se confunde con:** una entidad, que conserva su identidad aunque cambie; ni un DTO del cable.
- **Sin setters:** no cambia nunca; «cambiarlo» es crear otro. Un setter dejaría que cualquiera lo pusiera inválido (un consecutivo en 0) después de crearlo, y la regla tendría que revisarse en cada uso (*Implementing Domain-Driven Design · p. 271*; *DDD Reference · p. 19*). En Java se escribe como `record`.
- **Fuente:** Evans, *DDD Reference* (Value Objects) · Bloch, ítem 17
- **Estado:** ACORDADO 20-oct-2026

### Paquete de configuración · técnico
- **Es:** el conjunto versionado de catálogo y reglas, en un solo paquete, que el nodo de finca crea y entrega al celular; cada anotación dice con cuál se capturó (`version_config`).
- **No es / se confunde con:** la versión de la app, la del motor o la del contrato, que son ejes aparte.
- **Fuente:** ADR-029 · ESPEC-contrato-ingesta A-50 (ronda 1g P7)
- **Estado:** ACORDADO 16-oct-2026

### Par cumple/viola · técnico
- **Es:** para una decisión de la ESPEC, un ejemplo que la cumple y otro que no; después se convierte en prueba.
- **No es / se confunde con:** un caso *dado / cuando / entonces*, que describe un comportamiento completo.
- **Fuente:** skill detallar-para-codificar (`indice-precision.md`)
- **Estado:** PROPUESTO

### Partición · técnico
- **Es:** un trozo, de hasta `max_anotaciones_particion`, de lo que un celular tiene pendiente por sincronizar; sube en un solo POST.
- **No es / se confunde con:** lote. Puede traer anotaciones de varias camas y varios lotes, pero nunca de dos celulares.
- **Ejemplo:** 1.000 anotaciones pendientes salen en 5 particiones de 200.
- **Fuente:** ESPEC-contrato-ingesta A-30
- **Estado:** ACORDADO 16-oct-2026

### `pointer` · técnico
- **Es:** la ruta JSON (RFC 6901) dentro de la partición donde el nodo de finca encontró el error, contando las anotaciones desde 0; va en `errores[]` de un `422`.
- **No es / se confunde con:** el `consecutivo` de la anotación; `/anotaciones/37` es la n.º 38 del arreglo, no el consecutivo 37.
- **Ejemplo:** `/anotaciones/37/anotacion_uuid`, `/sesiones_captura`, `/contrato`.
- **Fuente:** ESPEC-contrato-ingesta A-22, A-32, C-14
- **Estado:** PROPUESTO

### Presencia · técnico
- **Es:** en el dominio de ingesta, cómo llegó cada campo de una captura: con valor, sin valor y con `motivo_ausencia`, sin valor ni motivo, o con valor y `motivo_ausencia` (contradictorio). La llena la infraestructura.
- **No es / se confunde con:** el Null Object `ValorDto` del contrato, del que se traduce; ni la desviación, que se juzga aparte.
- **Fuente:** ESPEC-dominio-ingesta A-69
- **Estado:** PROPUESTO

### Puerto de entrada · técnico
- **Es:** la interfaz en `servicios/input/port` que recibe y devuelve DTO de entrada; es lo único que el controlador conoce del núcleo, con sus DTO. La implementa un ensamblador de entrada (22-oct-2026; antes la implementaba el caso de uso, en `servicios/puertos/entrada`).
- **No es / se confunde con:** el puerto de salida (`servicios/output/port`), que el núcleo usa para guardar o avisar; ni la interfaz del caso de uso (`servicios/casosuso`), que habla en dominio.
- **Fuente:** `codigo/IA_LEEME.md` (convención de paquetes) · Martin · Clean Architecture p. 185
- **Estado:** ACORDADO 22-oct-2026

### `AnotacionEntrante` · técnico
- **Es:** la anotación tal como entra al caso de uso: ya traducida del cable a valores del dominio (id, consecutivo, huella, captura) y todavía sin juicio. Vive en `servicios/puertos/entrada`.
- **No es / se confunde con:** `Anotacion`, la raíz del dominio que ya tiene su juicio; ni el DTO `Anotacion` del contrato.
- **Fuente:** ESPEC-dominio-ingesta A-74 (antes `AnotacionRecibida`, en `dominio`)
- **Estado:** PROPUESTO

### Primer acuse · técnico
- **Es:** el momento, medido con el reloj del nodo de finca (`recibido_ms`), en que el celular recibió por primera vez la respuesta para una anotación; el celular lo devuelve tal cual tras una restauración para medir su edad.
- **No es / se confunde con:** el acuse en general, que puede repetirse en cada reenvío.
- **Fuente:** ESPEC-contrato-ingesta A-45 ⟦2⟧
- **Estado:** PROPUESTO

### `problem+json` · técnico
- **Es:** el formato estándar de error HTTP (RFC 9457): un JSON con `type`, `title`, `status` y campos propios como `traceId` y `regla`; lo usan todos los errores de toda la partición y todos los errores que genera el gateway de la finca.
- **No es / se confunde con:** una excepción de `excepciones[]`, que es el resultado de una sola anotación dentro de una respuesta `200`.
- **Fuente:** ESPEC-contrato-ingesta A-22, A-36
- **Estado:** PROPUESTO

### Prueba de restauración · técnico
- **Es:** restaurar un respaldo en una base aparte y compararlo, para saber que de verdad sirve; `respaldado_hasta` solo cuenta respaldos que la pasaron.
- **No es / se confunde con:** la verificación del respaldo (suma de control y lista del contenido), que no prueba que restaure.
- **Fuente:** TX-2 paso 17 · ESPEC-contrato-ingesta A-31 (frecuencia: TX-2, K-67)
- **Estado:** PROPUESTO

### Puerto de salida · técnico
- **Es:** interfaz en `servicios/output/port` que el caso de uso necesita de afuera (guardar, avisar, el reloj); recibe DTO de salida y la implementa un adaptador de `infraestructura/output/adapter`.
- **No es / se confunde con:** el puerto de entrada (`servicios/input/port`), que el controlador llama.
- **Fuente:** diagrama de paquetes de Juan 22-oct-2026 · `codigo/IA_LEEME.md` «Dentro de cada módulo del backend»
- **Estado:** ACORDADO 22-oct-2026

### Puntuación de anomalía · técnico
- **Es:** la suma de puntos que las reglas de OWASP CRS le van poniendo a una petición mientras la revisan; el WAF de la finca la bloquea solo cuando la suma llega al umbral (5, regla `949110`), no cuando salta una regla suelta.
- **No es / se confunde con:** el nivel de paranoia, que decide cuántas reglas participan; ni el modo del WAF, que decide si al llegar al umbral se bloquea o solo se anota.
- **Ejemplo:** `' OR 1=1 --` suma 5 por la `942100` y se bloquea; un `Host` por IP sumaba 3 por la `920350` y pasaba, a 2 del umbral.
- **Fuente:** OWASP CRS 4 (modo de puntuación de anomalías) · ESPEC-waf-finca §5 (comprobado 21-oct)
- **Estado:** PROPUESTO

### Raíz de agregado · técnico
- **Es:** la entidad que es la única puerta de entrada de su agregado: nadie de fuera cambia lo de dentro sin pasar por sus métodos, y es lo único que se busca por su id.
- **No es / se confunde con:** cualquier entidad; una entidad interna no se busca ni se cambia desde fuera.
- **Sin setters:** cambia solo por verbos de negocio que protegen sus invariantes (`registrar`, no `setConfirmadoHasta`). Una clase con solo getters y setters, con las reglas en los servicios, es un «modelo anémico» (*Domain-Driven Design Distilled · p. 83, 86*; *Implementing Domain-Driven Design · p. 56*). Es `class`, no `record`, porque tiene identidad y cambia con el tiempo.
- **Fuente:** Evans, *DDD Reference* (Aggregates) · ESPEC-dominio-ingesta A-60
- **Estado:** PROPUESTO

### Reconstituir · técnico
- **Es:** rehacer un agregado desde lo guardado sin ejecutar otra vez sus reglas de creación; el juicio guardado se respeta tal cual.
- **No es / se confunde con:** `recibir`, que crea una anotación nueva y la juzga; ni la restauración de un respaldo.
- **Fuente:** plantilla entidad-agregado §1 ⟦11⟧ (Evans, Factories) · ESPEC-dominio-ingesta A-79 (salió del borde del dominio: es del adaptador de persistencia)
- **Estado:** PROPUESTO

### Reenvío · técnico
- **Es:** la misma anotación, con el mismo id y el mismo contenido (misma huella), que llega otra vez al nodo de finca; cuenta `sin_efecto` y no cambia nada.
- **No es / se confunde con:** la anotación corrupta, que trae el mismo id con otra huella; ni `IdYaRecibido`, que cubre los dos casos.
- **Fuente:** ESPEC-contrato-ingesta A-2 · ES-S04 · ESPEC-dominio-ingesta A-77
- **Estado:** PROPUESTO

### `ReglasDeIngreso` · técnico
- **Es:** la lista de reglas del ingreso (`ReglaDeIngreso`) que recibe `Anotacion.recibir`; hoy solo trae `ReglaDatoAusente`, y cada regla nueva se agrega a ella sin tocar la raíz.
- **No es / se confunde con:** el catálogo de configuración, que da los valores esperados que algunas reglas usarán después.
- **Fuente:** ESPEC-dominio-ingesta A-69, A-76
- **Estado:** PROPUESTO

### Recaptura · negocio
- **Es:** una captura nueva de la misma cama, con su propio id, para cambiar algo que ya salió del celular o que se perdió; la más reciente decide el estado.
- **No es / se confunde con:** edición local, que cambia en su lugar lo que todavía no salió; ni corrección, que la hace el gerente.
- **Fuente:** aclaración de Juan 16-oct (ronda 1e P4, ronda 1f P1) · ESPEC-contrato-ingesta A-49
- **Estado:** ACORDADO 16-oct-2026

### Record · técnico
- **Es:** una forma de declarar una clase de Java inmutable cuya igualdad es su contenido: se escribe en una línea con sus campos, y el compilador genera el constructor, los accesores, `equals`, `hashCode` y `toString`. Puede llevar reglas: un constructor que valida, métodos y fábricas. En el proyecto tiene dos usos: **DTO** en `contrato`, que solo viaja y no decide nada (`ParticionDto`), y **objeto de valor o id tipado** en `dominio`, con reglas (`ConsecutivoValor`, `HuellaValor`, `AnotacionId`). También se usa para comandos, resultados, eventos y las variantes de un tipo sellado.
- **No es / se confunde con:** un DTO (ese es solo uno de sus usos); ni una entidad, que cambia y conserva su identidad (`AvanceDelDispositivoAgregado` es una clase, no un record).
- **Ejemplo:** `record ConsecutivoValor(long valor)` valida que valga 1 o más y ofrece `siguiente()`; `new ConsecutivoValor(3).equals(new ConsecutivoValor(3))` es `true`.
- **Fuente:** ESPEC-contrato-ingesta A-9 · TEC-11 · ESPEC-dominio-ingesta A-64, A-65, OBS-1-1 · *Implementing Domain-Driven Design · p. 264*
- **Estado:** PROPUESTO (corregido el 21-oct-2026, OBS-1-1)

### Renovación de credencial · técnico
- **Es:** volver a verificar al operario contra el nodo de finca, en la oficina y antes de sincronizar; la persona verificada así es la dueña de la sincronización (la sesión es de la persona, no de la credencial).
- **No es / se confunde con:** el login local, que no necesita red.
- **Fuente:** aclaración de Juan 16-oct (ronda 1f P9) · ES-S12
- **Estado:** ACORDADO 16-oct-2026

### Rearmar el sobre · técnico
- **Es:** volver a construir el sobre de la partición desde el almacén del celular, con los bytes originales de cada anotación y sin regenerar ids, conservando el `particion_uuid` (sin marca: el celular recuerda que ya rearmó, ronda 1k P4); se hace una vez ante cualquier `422 sobre-invalido`, con o sin `pointer` (A-41, ronda 1k P3).
- **No es / se confunde con:** partir la partición (`413`), ni editar una anotación, que es inmutable desde que sale.
- **Fuente:** ESPEC-contrato-ingesta A-41, ronda 1i P5 y P7
- **Estado:** PROPUESTO

### Retener · técnico
- **Es:** que el celular conserve una partición en su almacén sin borrarla ni darla por entregada, hasta que algo cambie (actualizar la app o el nodo de finca, o una orden del gerente).
- **No es / se confunde con:** reintentar, que vuelve a enviar lo mismo; ni la retención hasta `respaldado_hasta`, que guarda lo ya entregado.
- **Fuente:** ESPEC-contrato-ingesta A-41 (alcance según la causa, ronda 1j P5)
- **Estado:** PROPUESTO

### `rearmado` · técnico
- **Estado actual:** REEMPLAZADO 18-oct-2026 · ver «Rearmar el sobre» y `sobre_invalido_repetido` (ronda 1k P4: el campo salió del contrato; la alerta la decide la memoria del ingreso).
- **Era:** la marca booleana del sobre de una partición rearmada; si esa partición vuelve a fallar con `422 sobre-invalido` (con o sin `pointer`, ronda 1k P3), el nodo de finca alerta `sobre_invalido_repetido`.
- **No es / se confunde con:** un reenvío normal (reintento), que no la lleva y no cuenta para la alerta.
- **Fuente:** ESPEC-contrato-ingesta A-41, §3b (ronda 1j P10)

### `respaldado_hasta` · técnico
- **Es:** el mayor consecutivo que ya está dentro de un respaldo verificado, en la copia local y en la nube; es el límite de borrado de lo acusado (la regla de borrado vive en la semilla celular).
- **No es / se confunde con:** `confirmado_hasta`, que solo dice qué recibió el nodo de finca.
- **Fuente:** ESPEC-contrato-ingesta A-31
- **Estado:** PROPUESTO

### Respaldo · técnico
- **Es:** una copia cifrada y verificada de la base del nodo de finca que sale de la máquina: una copia local física y otra en la nube.
- **No es / se confunde con:** la retención en el celular, que guarda las anotaciones hasta que un respaldo las cubra.
- **Fuente:** TX-2 · ESC-03 · ESPEC-contrato-ingesta A-31
- **Estado:** PROPUESTO

### Respuesta normal · técnico
- **Es:** una respuesta del nodo de finca que demuestra que la sincronización funciona; solo con ella corre la ventana de retención. Su alcance exacto está pendiente en la semilla celular.
- **No es / se confunde con:** cualquier respuesta: un `422 contrato-incompatible` es una anomalía.
- **Fuente:** semilla celular (`IA_SEMILLA-caso-de-uso-celular.md`): A-45 ⟦3⟧ y COH-43, apartados de la ESPEC del contrato el 18-oct
- **Estado:** PROPUESTO

### Restauración · técnico
- **Es:** volver a poner en marcha el nodo de finca desde un respaldo, por ejemplo tras un daño; el nodo de finca queda como estaba en el momento de ese respaldo.
- **No es / se confunde con:** un reenvío del celular. Después de restaurar, el celular reenvía lo que el respaldo no tenía.
- **Fuente:** TX-2 ES-R14 · ES3 · ESPEC-contrato-ingesta A-31, C-27
- **Estado:** PROPUESTO

### Registro del WAF · técnico
- **Es:** lo que el WAF de la finca anota de cada petición que bloquea, que sale con error o que, en solo detectar, habría bloqueado: la fecha, la IP, la traza del gateway y qué regla saltó, con el valor entero del campo que la disparó; nunca cabeceras, cuerpos ni `Authorization`.
- **No es / se confunde con:** el registro de acceso del gateway (una línea por petición, con su traza y su código); tampoco es telemetría: no sale del nodo de finca.
- **Fuente:** ESPEC-waf-finca K-6 · ModSecurity (`SecAuditLog`, partes del registro de auditoría)
- **Estado:** PROPUESTO

### Sección · negocio
- **Es:** la parte de una cama que se usa como unidad de inventario y de captura; en la versión 1 del contrato es el único nivel al que se anota (`datos.seccion_id`).
- **No es / se confunde con:** la cama, que contiene secciones; el lote, que agrupa secciones de una misma variedad y no se captura.
- **Fuente:** DRIVERS RF-001 (sección como unidad de inventario) · ADR-024 · ESPEC-contrato-ingesta A-20
- **Estado:** PROPUESTO

### Sello normalizado · técnico
- **Es:** la hora de captura corregida por el nodo de finca con el desfase que declaró el celular; es la hora con la que se ordenan las capturas.
- **No es / se confunde con:** el sello crudo (`crudo_ms`), que es el reloj del celular sin corregir.
- **Fuente:** ADR-031 §2 · ESPEC-contrato-ingesta A-16 · el cálculo, en la semilla ingreso (A-6, apartada el 18-oct)
- **Estado:** PROPUESTO

### Sobre de la anotación · técnico
- **Es:** los campos fijos de una anotación fuera de `datos` (`anotacion_uuid`, `consecutivo`, `tipo`, `tiempo`…); un defecto ahí afecta solo a esa anotación, salvo `anotacion_uuid` o `consecutivo` ausentes o inválidos: sin ellos no se la puede identificar y dan `422` a toda la partición (A-32, C-14).
- **No es / se confunde con:** sobre de la partición.
- **Fuente:** ESPEC-contrato-ingesta A-15, A-32
- **Estado:** PROPUESTO

### Sobre de la partición · técnico
- **Es:** los campos de la partición fuera de las anotaciones (`contrato`, `particion_uuid`, `sesiones_captura[]`, `descartes[]`, `primeros_acuses[]`); lo arma el celular desde su almacén y un defecto ahí da `422` a toda la partición.
- **No es / se confunde con:** sobre de la anotación.
- **Fuente:** ESPEC-contrato-ingesta A-32, A-38, A-41
- **Estado:** PROPUESTO

### Servicio de dominio · técnico
- **Es:** una operación del dominio, sin estado, que no pertenece a ninguna entidad ni objeto de valor; vive en `dominio` y es Java puro.
- **No es / se confunde con:** el servicio de aplicación (caso de uso, `servicios/casosuso`), que orquesta puertos y transacciones.
- **Fuente:** *Domain Driven Design · secc. 42* · ESPEC-dominio-ingesta ronda 1 P8 (se descartó para el ingreso)
- **Estado:** PROPUESTO

### Servidor · técnico
- **Es:** palabra que no se usa en FlorLogic para la máquina de la finca: se dice **nodo de finca** o **nodo principal**.
- **No es / se confunde con:** nodo de finca, que es el término correcto; la nube.
- **Fuente:** revisión de coherencia del 18-oct, COH-73
- **Estado:** PROPUESTO

### Sesión de captura · negocio
- **Es:** el trabajo de una persona en campo con una misma versión de la app; actualizar la app abre una sesión nueva.
- **No es / se confunde con:** sesión de sincronización. No lleva el dispositivo.
- **Fuente:** ESPEC-contrato-ingesta A-18
- **Estado:** PROPUESTO

### Sesión de sincronización · técnico
- **Es:** una subida de un celular al nodo de finca; la crea el nodo de finca, es **de la persona** que entrega (no de su credencial) y lleva dispositivo, usuario, versiones de configuración, conteos y resultado.
- **No es / se confunde con:** sesión de captura, que es el trabajo en campo.
- **Fuente:** ESPEC-contrato-ingesta A-5 (apartada el 18-oct a la semilla de sesión, ronda 2)
- **Estado:** PROPUESTO

### Special Case · técnico
- **Es:** patrón en que una situación rara tiene su propia clase con nombre en vez de un `null` o una excepción (Fowler, PoEAA). En el contrato: `ValorDto.Contradictorio`.
- **No es / se confunde con:** Null Object, que es el caso especial de «no hay dato».
- **Fuente:** ESPEC-contrato-ingesta A-34, A-57
- **Estado:** PROPUESTO

### Testcontainers · técnico
- **Es:** la biblioteca de pruebas que levanta contenedores reales (PostgreSQL, Nginx) desde una prueba JUnit y los apaga al terminar (TEC-26).
- **No es / se confunde con:** Docker Compose, que arranca el nodo de finca para usarlo; Testcontainers solo vive mientras corre la prueba.
- **Fuente:** TEC-26 · `CanalParticionesTest` · `ESPEC-gateway-finca` A-4
- **Estado:** PROPUESTO

### Strategy · técnico
- **Es:** un patrón en que una interfaz tiene varias implementaciones intercambiables; en el ingreso, cada regla del juicio es una implementación de `ReglaDeIngreso`.
- **No es / se confunde con:** Specification, que solo responde sí o no; una Strategy de ingreso devuelve un veredicto con códigos.
- **Fuente:** plantilla entidad-agregado §9.1 · ESPEC-dominio-ingesta ronda 1 P10
- **Estado:** PROPUESTO

### `porEncima` · técnico
- **Es:** los consecutivos de un celular que ya llegaron pero están más allá de un hueco; esperan dentro de `AvanceDelDispositivoAgregado` a que llegue lo que falta para sumarse a `confirmado_hasta` (A-72).
- **No es / se confunde con:** el hueco, que es lo que falta; `porEncima` es lo que sí llegó. Tampoco cuenta para lo que el celular puede borrar.
- **Ejemplo:** recibidas 1-200 y 401-600 → `confirmado_hasta = 200`, `porEncima = 401-600`.
- **Fuente:** ESPEC-dominio-ingesta A-72, DI1-4
- **Estado:** PROPUESTO

### Sufijo de papel · técnico
- **Es:** la palabra al final del nombre de una clase que dice qué papel cumple en la arquitectura hexagonal: `PuertoEntrada`, `EntradaDto`, `RespuestaDto`, `EnsambladorEntrada`, `CasoUso`, `CasoUsoImpl`, `EnsambladorSalida`, `SalidaDto`, `PuertoSalida`, `Controlador`, `Adaptador<Técnica>`, `Dto`, `Valor`, `Id`, `Agregado`, `Aplicacion` (22-oct-2026; `Comando` y `Resultado` salieron). Ejemplo: `RecibirParticionCasoUso`.
- **No es / se confunde con:** el paquete, que dice dónde vive la clase (D-18); el sufijo lo repite a propósito para leerlo en el IDE. Tampoco es la forma de Java (`record`, `class`, `enum`): un `record` puede ser `Dto`, `EntradaDto`, `SalidaDto`, `Valor` o `Id`.
- **Fuente:** decisión de Juan 21-oct-2026 (OBS-1-2) · tabla completa en `codigo/IA_LEEME.md` «Nombres de las clases»
- **Estado:** ACORDADO 21-oct-2026

### Tipo sellado · técnico
- **Es:** una interfaz `sealed` de Java con una lista cerrada de variantes, cada una con solo sus datos; el compilador impide combinaciones que no existen.
- **No es / se confunde con:** un `enum`, que tiene valores fijos sin datos propios (A-66 eligió `enum` para el resultado del juicio).
- **Fuente:** plantilla entidad-agregado P10 (Wlaschin) · ESPEC-dominio-ingesta ronda 1 P7
- **Estado:** PROPUESTO

### Transversal (paquete) · técnico
- **Es:** el crosscutting del sistema: los helpers y las herramientas que usan varias capas o varios módulos (utilidades, validaciones comunes, excepciones base). Hay `transversal/` en cada módulo y en la raíz del backend. Hoy vacío. Corrección de Juan, 22-oct-2026.
- **No es / se confunde con:** un servicio externo (el catálogo de mensajes, la telemetría, los parámetros), que va en `infraestructura/output/serviciosexternos`; ni `infraestructura`, que traduce hacia afuera.
- **Fuente:** diagrama de paquetes de Juan 22-oct-2026 · `codigo/IA_LEEME.md` «Dentro de cada módulo del backend»
- **Estado:** ACORDADO 22-oct-2026

### Validar al armar · técnico
- **Es:** que el celular, antes de enviar, pase cada anotación y el sobre por el mismo `ValidadorContrato` y los mismos topes del nodo de finca; lo que no valida no viaja (A-53 ⟦1⟧-⟦3⟧).
- **No es / se confunde con:** el motor de validación del celular, que juzga el contenido de la captura (rangos, mínimos), no si el JSON se lee.
- **Ejemplo:** la 38 con `…}}` falla en `readTree` con `FAIL_ON_TRAILING_TOKENS`, se avisa en `descartes` con `fallo_tecnico`.
- **Fuente:** ESPEC-contrato-ingesta A-53 (ronda 1k P1)
- **Estado:** PROPUESTO

### Ventana de retención · técnico
- **Es:** los 7 días, contados solo mientras el nodo de finca responde, tras los cuales lo no entregado pasa al tablero del gerente, que decide recapturar o descartar; el celular nunca lo borra solo.
- **No es / se confunde con:** la retención hasta `respaldado_hasta`, que aplica a lo que sí se entregó.
- **Fuente:** aclaración de Juan 16-oct (ronda 1d) · ronda 1e P1 · P-S03 · ADR-025 §2-4
- **Estado:** ACORDADO 16-oct-2026

### `Veredicto` · técnico
- **Es:** lo que devuelve una regla del ingreso (`ReglaDeIngreso.evaluar`) sobre una captura: sin marca, marca con códigos, o rechazo con su motivo. `Anotacion.recibir` junta los veredictos de todas las reglas para decidir el juicio.
- **No es / se confunde con:** `veredicto_local`, que es el juicio que hizo el celular y viaja en la anotación; ni el `Resultado` final de la anotación.
- **Fuente:** ESPEC-dominio-ingesta A-69, A-76 (forma: ronda 3, COH-122)
- **Estado:** PROPUESTO

### Versión optimista · técnico
- **Es:** un número que lleva el agregado y sube con cada cambio; si dos escrituras simultáneas parten del mismo número, la segunda falla en vez de pisar a la primera.
- **No es / se confunde con:** `version_config` ni las versiones de la app, el motor o el contrato.
- **Fuente:** *Implementing Domain-Driven Design · p. 412* · plantilla entidad-agregado §1 ⟦12⟧
- **Estado:** PROPUESTO

### Verificada · técnico
- **Es:** dicho de una captura: aplicada en el nodo de finca **sin ningún código de marca** (ni de hora, ni de motor, ni incompleta, ni de dispositivo); solo una captura verificada puede dejar obsoleta a otra, y solo en el mismo (sección, campo).
- **No es / se confunde con:** aceptada por el celular (`veredicto_local`).
- **Fuente:** ESPEC-contrato-ingesta A-45 ⟦1⟧ (rondas 1f P3, 1g P2)
- **Estado:** ACORDADO 16-oct-2026

### WAF de la finca · técnico
- **Es:** el filtro de ataques web que corre dentro del mismo Nginx del gateway de la finca (ModSecurity v3 con las reglas OWASP CRS 4): revisa todo lo que entra por el gateway (ruta, cabeceras salvo `Authorization`, y cuerpo) antes de que llegue al backend. «Limpiar» es filtrar: detiene lo que parece ataque (`403`) y lo que no puede revisar entero (`400`, `413`), salvo un JSON roto en UTF-8 simple, que llega al backend y a su `422`; lo limpio pasa sin cambios.
- **No es / se confunde con:** el gateway de la finca, que es la puerta que enruta; el WAF es un filtro dentro de esa puerta. Nunca modifica la petición. Tampoco es el WAF gestionado de la nube (`BB-08`), ni valida el contrato o la credencial (eso es del backend).
- **Ejemplo:** una partición con `' OR 1=1 --` en un campo de texto salta una regla de inyección SQL.
- **Fuente:** `IA_TECNOLOGIAS-C4.md` (consulta Q1, «WAF de la finca») · `IA_PRIORIDAD-componentes-TX1.md` §1 nivel 2 y §2 · ESPEC-waf-finca
- **Estado:** ACORDADO 21-oct-2026 (Jeronimo: encuadre; 2.ª vuelta tras la ronda 1 P1; 3.ª vuelta tras la ronda 2)
