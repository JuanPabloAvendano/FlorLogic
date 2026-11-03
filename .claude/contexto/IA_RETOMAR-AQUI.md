REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 20-oct-2026, a pedido de Juan, al cerrar la sesión (reemplaza la versión del 19-oct)
Estado de revisión: SIN REVISAR
Manda por encima: `codigo/contrato/espec/ESPEC-contrato-ingesta.md` · `IA_00-LEEME-transacciones.md` (rama `documentacion-desarrollo`) · la palabra de Juan

> ~~**21-oct-2026 · pausa:** antes de esto, lee y muestra `.claude/contexto/IA_LEER-PRIMERO.md`~~ · **descartado el
> 30-oct-2026**: superado por los bloques de abajo; el avance conectado es el Segmento 1b (§8 de la ESPEC de ingesta).
>
> **3-nov-2026 (ÚLTIMO) · se trajo TODO de `~/Work/florlogic-tx1-completa`** (Juan: «bring everything to this
> project»). `codigo/` es ahora espejo de la copia (V4…V9, identidad con Keycloak, dispositivos, bitácora, ciclo,
> consulta y web del gerente, observabilidad, SonarQube, JaCoCo, Trivy, `codigo/sustentacion/`), más `.github/workflows/ci.yml`.
> Se reemplazó la `V4__sesion_sincronizacion.sql` de P1(a) por la `V4__sesion_huella_y_rechazadas.sql` de la copia.
> El plan paso a paso P1…P7 queda superado. TEC-37…41 siguen `PROPUESTA`: falta el `ACEPTADO` de Juan. Respaldo del
> estado anterior: `~/Work/respaldos/florlogic-codigo-antes-de-traer-tx1-completa-20261103-1125.tar.gz`. El manual
> `manual-is2/` se probó con el código de antes (sin Keycloak): repasarlo. Sin commit (PR-05).
>
> **2-nov-2026 · pasando TX-1 de `~/Work/florlogic-tx1-completa` a este repo, paso por paso.** El plan de
> pasos (P0…P7) vive en el `IA_RETOMAR-AQUI.md` de la copia. **P0 ✅** (Spring Boot 4.1.2, commit de Juan `559375d`).
> **P1(a) ✅ sin commit:** la sesión de sincronización (abrir, particiones en ella, cerrar; CT-1, CT-7, CT-8),
> `./mvnw verify` 194/194 con Docker y «Canal OK» con 3 sesiones (2 cerradas, 1 parcial). Siguen **P1(b)** (huella con
> `CalculadorDeHuella`; necesita TEC-37 `ACEPTADO`) y **P1(c)** (rechazadas, `excepciones[]`). Para seguir: **«lee
> .claude/contexto/IA_RETOMAR-AQUI.md y sigamos con P1(b)»**.
>
> **Estudio de P1(a) con Juan (2-nov, antes del commit):** tramo 1 (dominio) cerrado salvo la pregunta 3 (por qué
> `abrir` recibe `ahora` y no llama a `Instant.now()`); tramo 2 (casos de uso) cerrado salvo la pregunta 3 (por qué el
> dominio no cruza el puerto de salida: `SesionDto` frente a `SesionDeSincronizacion`), las dos aplazadas por Juan;
> tramo 3 (puertos y adaptadores de salida, V4) cerrado; tramo 4 (entrada) cerrado salvo la pregunta 3 (qué hace el
> compilador si un `switch` sin `default` sobre un `sealed` no cubre un caso nuevo), aplazada; sigue el tramo 5 (lo que
> se retiró: `DispositivoDelNodo`). Pendientes que salieron:
> **P1a-1** una sesión guardada que `desde` no puede rehacer sale `404` sin una línea de log (el avance roto sí la deja,
> COH-131): agregar el log. **P1a-2** el simulador arma el JSON de abrir y cerrar con cadenas: si el contrato cambia un
> nombre, compila y el nodo responde `422`; usar los DTO del contrato o una prueba que lo amarre. **P1a-3**
> `SesionesControlador` tiene dos casos de uso (plantilla T6: uno por controlador): Juan decide si se parte.
> **P1a-4** `AbrirSesionCasoUsoImpl` y `CerrarSesionCasoUsoImpl` no tienen prueba unitaria (solo `CanalSesionesTest`
> por HTTP, con el reloj real y un rango de desfase): escribirla con `Reloj` y `GeneradorDeIds` fijos y dobles en
> memoria, como `RecibirParticionCasoUsoImplTest`.
>
> **28-oct-2026 · pausa (en paralelo, decisión de Juan):** foco en la Guía de IS II
> (`.claude/contexto/IA_GUIA-IS2-checklist.md`) y en el esqueleto de componentes de TX-1 con tecnología ya construida:
> `codigo/despliegue/espec/ESPEC-esqueleto-tx1.md`, Segmento 1 (backend en contenedor + OpenBao), ronda 1 cerrada al
> 52 %. Retomar desde su «Dónde retomar».
>
> **29-oct-2026 · pausa:** `ESPEC-esqueleto-tx1.md` va a mitad de la ronda 2 (≈66 %): arreglos de redacción aplicados,
> y decididos A-10 reformulada (Compose secrets solo para el secreto cero), A-22 (llave del vault en un token HSM USB,
> SoftHSM en dev/CI), A-23 (`fail-fast`) y A-24 (partes de recuperación a personas, raíz revocada). Para seguir:
> «lee .claude/contexto/IA_RETOMAR-AQUI.md y sigamos con la ESPEC del esqueleto».
>
> **29-oct-2026 (tarde):** rondas 2, 3 y 4 cerradas (A-10…A-43; ≈82 %, ≈89 % con K-7). Coherencia pasa. **Solo falta K-7:**
> que Juan acepte TEC-33…TEC-36 (OpenBao, Spring Cloud Vault 5.0.3, complemento `pkcs11`, paquetes Alpine). Después,
> confirmar `LISTA PARA CÓDIGO` y la etapa 4 del Segmento 1. Ver «Dónde retomar» de la ESPEC.

> **29-oct-2026 (noche) · siguiente paso:** `ESPEC-esqueleto-tx1.md` está **`LISTA PARA CÓDIGO`** (≈87 %, coherencia pasa;
> TEC-33…36 aceptadas tras revisar CVE, OpenBao 2.7.2). Juan dio el **«sí» al Segmento 1a**: escribirlo en esta sesión nueva.
> Para seguir: **«lee .claude/contexto/IA_RETOMAR-AQUI.md y escribe el Segmento 1a del esqueleto»** → «Dónde retomar» de la ESPEC.

> **29-oct-2026 (cierre) · Segmento 1a escrito:** el nodo completo corre en Compose (base, OpenBao con SoftHSM,
> `openbao-init`, backend con su Dockerfile, gateway por nombre); `NodoCompletoTest` 2/2 con Docker. Falta la etapa 5:
> revisar con Juan OBS-1a-1…5 (§10 de `ESPEC-esqueleto-tx1.md`) y después el 1b. **`scripts/IA_probar-canal.sh` queda
> roto hasta el 1b** y el Compose de desarrollo pide un `.env` con el secreto cero y un `down -v` una vez (OBS-1a-3).
> Para seguir: **«lee .claude/contexto/IA_RETOMAR-AQUI.md y revisemos el Segmento 1a del esqueleto»**.

> **29-oct-2026 (revisión) · etapa 5 del Segmento 1a cerrada:** Juan, «de acuerdo» con OBS-1a-1…5 (se queda
> `KV_APPLICATION_NAME`; hueco de `IA_probar-canal.sh` aceptado hasta el 1b; `softhsm-so-pin` aplazado a K-22; `e2e/`
> borrado). Para seguir: **«lee .claude/contexto/IA_RETOMAR-AQUI.md y presenta el Segmento 1b del esqueleto»**.

> **29-oct-2026 (cierre, 2) · Segmento 1b escrito:** `init.sh` con marcas, reanudación y comprobaciones;
> `NodoArranqueSeguroTest` 6/6 con Docker; `IA_probar-canal.sh` levanta el nodo entero («Canal OK»); K-3 del gateway
> cerrado. Falta la etapa 5 del 1b (OBS-1b-1…6, §10 de `ESPEC-esqueleto-tx1.md`) y la etapa 6 del Segmento 1. Para
> seguir: **«lee .claude/contexto/IA_RETOMAR-AQUI.md y revisemos el Segmento 1b del esqueleto»**.

> **29-oct-2026 (noche) · dos cambios de Juan:** (1) **todo el nodo con HTTPS, ningún enlace en claro**
> (`ESPEC-esqueleto-tx1.md` A-44; lo que falta decidir en K-46; nuevo segmento 1c antes de cerrar el Segmento 1).
> (2) **Se estudia construyendo, por tramos cortos:** `IA_RUTA-ESTUDIO-TX1.md`, tramo 1 abierto (Juan separa
> `ParticionesControlador`, OBS-1-8). Para seguir: **«lee .claude/contexto/IA_RETOMAR-AQUI.md y sigamos con el
> tramo 1»** o **«…y detallemos el TLS (K-46)»**.

> **29-oct-2026 (cierre de la verificación) · explicación del Segmento 1 del dominio ≈79 % (hace falta 80 %; ver la corrección de cuenta en §10 «Paso 6»). Falta el `while` de `registrar` (prueba propia + depurador):** completas
> o casi (1) `ConsecutivoValor`, (2) el agregado, (3) los ids, (4) la huella, (6) los enums, (7) el flujo a medias.
> **Falta (5) `CapturaValor`:** una frase de Juan (qué guarda, por qué el dominio no lee el `json`, quién llena `campos`).
> Detalle en `ESPEC-dominio-ingesta.md` §10 «Paso 6». Después: aceptación, modo docente (dañar el código) y la revisión del 1b.

> **29-oct-2026 (cierre de sesión, ÚLTIMO) · el Segmento 1 del dominio, a un paso de aceptarse.** Explicación ≈82 %
> (≥80 %). Juan pidió verificarlo antes con subagentes (documentación aceptada y arquitectura limpia/guía IS II): 14
> hallazgos VER-1…VER-14 en `ESPEC-dominio-ingesta.md` §10. Juan eligió «arreglar lo del segmento y aceptar»; Claude
> aplicó VER-2…VER-7 y VER-9 (ventana de `porEncima` con `LlegadaAlAvance`, `CapasTest` con ArchUnit, llaves en las
> excepciones, diccionario, A-74/A-76 según el README). **Para seguir: «lee .claude/contexto/IA_RETOMAR-AQUI.md y
> hazme las preguntas de entendimiento de los cambios del Segmento 1»** → con su «sí», aceptar el Segmento 1, cerrar
> `IA_LEER-PRIMERO.md` y quitar su línea de `.claude/CLAUDE.md`. Después, en este orden propuesto: práctica de código
> dañado (modo docente) · revisión del 1b del esqueleto (OBS-1b-1…6) · ronda TLS 2 (COH-43 bloqueante, COH-45, COH-50,
> ¿PostgreSQL con TLS?) · tramo 1 de `IA_RUTA-ESTUDIO-TX1.md` · segmento «avance conectado» (VER-1).

> **29-oct-2026 (noche, pausa) · preguntas de entendimiento de los cambios del Segmento 1, empezadas.** Claude hizo 13
> preguntas (A ventana y `LlegadaAlAvance`, B `long` interno, C `CapasTest`, D llaves, E empaquetado). Juan respondió
> A1 (¾: falta el porqué frente a excepción y `void`, A-57). Detalle y pista en `ESPEC-dominio-ingesta.md` §10, tras
> «Decisión de Juan (29-oct)». **Para seguir: «lee .claude/contexto/IA_RETOMAR-AQUI.md y sigamos con las preguntas de
> entendimiento del Segmento 1»** → Claude repite la pista de A1 y sigue con A2…E13; con ≥80 % y el «sí», aceptar.
> Aparte (Eclipse, fuera del repo): SonarLint no analizaba los archivos abiertos como `/codigo/backend-finca/...` (por
> el proyecto padre); se filtraron las carpetas de los módulos en `codigo/.project` (ignorado por git). Abrirlos
> siempre desde el proyecto del módulo.

> **30-oct-2026 (cierre, punto quieto para el commit de Juan) · dominio de ingesta hasta anotaciones, detallado.**
> Alcance de Juan: «el código no usa el dominio ni las reglas; terminar TX-1 de manera completa» → dominio mínimo
> conectado hasta anotaciones, con el hexágono de ucotestapi (`~/eclipse-workspace/ucotestapi`) en los 14 módulos de
> negocio, adaptador ≠ ensamblador, catálogo de reglas en el módulo `reglas` y formas de cada tipo en `configuracion`,
> por puertos. Rondas 4, 5 y 6 cerradas en `codigo/backend-finca/espec/ESPEC-dominio-ingesta.md` (A-83…A-102,
> C-104…C-117, §5 «Firmas, puertos y flujo», §8 «Reordenado el 30-oct»: segmentos R → 1b → H → CR → 2). Coherencia
> pasa; el 4.º verificador midió ≈73 % y dio **el Segmento R listo para código (≈87 %)**; el 5.º no corrió (límite
> semanal de agentes). Contrato ajustado: tipo desconocido ya no es `422`, sale `rechazada` `tipo_desconocido` (A-102).
> TEC-37 (Titanium JCS + tree-io-jakarta + Parsson, solo pruebas) **PROPUESTA**, falta el `ACEPTADO` de Juan antes del
> Segmento H. Escribe Claude, revisa Juan; las preguntas A2…E13 del Segmento 1 se responden durante el trabajo. Nada de
> código todavía. **Para seguir: «lee .claude/contexto/IA_RETOMAR-AQUI.md, corre la 5.ª verificación de la ESPEC de
> ingesta y presenta el plan del Segmento R con su draw.io y su árbol de archivos»**.

> **30-oct-2026 (noche) · Segmento R escrito.** `IA_LEER-PRIMERO.md` quedó `DESCARTADO` (Juan: «es viejo»). R, a pedido de
> Juan («do repackaging»): nombres de ucotestapi en los 14 módulos de negocio, ingesta movida (tabla de §5 de
> `ESPEC-dominio-ingesta.md`), `ManejadorDeErrores` (500 genérico, C-116), reglas (1) y (2) de `CapasTest` (C-115).
> `./mvnw clean verify` 141/141 con Docker. `codigo/IA_LEEME.md` actualizado. **Para seguir: «lee
> .claude/contexto/IA_RETOMAR-AQUI.md y revisemos el Segmento R»** → después, 5.ª verificación y plan del 1b.

> **31-oct-2026 · revisión del Segmento R hecha (etapa 5).** Juan decidió OBS-R-1…3 (A-103): `contrato` se queda y solo
> lo leen adaptadores; `ParticionCableAssembler` en `controladores/cable` traduce en los dos sentidos y el controlador queda
> en HTTP; regla ArchUnit (1) angostada + regla del cable (mutación detectada); `dominio/recepcion`. COH-191 (simulador)
> abierto para después de 1b. `./mvnw verify` 145/145 con Docker. Checklist de la guía IS II actualizado. **Para seguir:
> «lee .claude/contexto/IA_RETOMAR-AQUI.md y hazme las preguntas de entendimiento del Segmento R»** → con ≥80 % y el «sí»,
> aceptar R; después, 5.ª verificación y plan del 1b.

> **31-oct-2026 (tarde) · Segmento R en la etapa 6, solo falta la explicación de Juan.** Coherencia pasa (agente); OBS-R-5…12
> aplicadas: regla «fuera del cable nadie ve contrato ni Jackson», reglas (1) y (2) ampliadas, las tres con mutación
> detectada; el cable solo atrapa `JacksonException` y registra cada rechazo; 4 casos unitarios; ESPEC §5, A-103 y
> diccionario al día. `./mvnw verify` 150/150 con Docker. **Para seguir: «lee .claude/contexto/IA_RETOMAR-AQUI.md y
> sigamos con la explicación del Segmento R»** (ronda 1: controlador, cable, puerto de entrada).
> **Después (31-oct):** Juan ubicó los `type` de los problemas de ingesta en el catálogo de parámetros (A-104, COH-200,
> OBS-R-13); se construyen en el Segmento CR, sin cambiar el orden 1b → CR. La explicación de R se estudia más tarde.

> **31-oct-2026 (noche) · A-105 escrita y explicación de R empezada.** Ensambladores y DTO en español en los 14 módulos
> (`…EnsambladorEntrada`, `…EnsambladorSalida`, `…SolicitudDto`, `…RespuestaDto`, `…Dto`; el del controlador es
> `controladores/ensamblador/ParticionEnsamblador`). `./mvnw clean verify` 150/150 con Docker. Explicación, ronda 1 ≈60 %
> (§10 de `ESPEC-dominio-ingesta.md`, con pistas por clase). **Para seguir: «lee .claude/contexto/IA_RETOMAR-AQUI.md y
> sigamos con la explicación del Segmento R»** → corregir las clases 2-5 y explicar las 6 que faltan; con ≥80 % y el «sí»,
> aceptar R.

> **31-oct-2026 (CIERRE, ÚLTIMO) · Segmento R: código terminado, falta la explicación de Juan (rondas 2 y 3).**
> Hoy: A-103 (el JSON del contrato lo traduce `controladores/ensamblador/ParticionEnsamblador`; `contrato` solo para
> adaptadores), A-104 (los `type` de problema van al catálogo de parámetros, en el Segmento CR), A-105 (ensambladores y DTO
> en español, todo DTO con `Dto`), A-106 (la huella la calcula un puerto `CalculadorDeHuella` que pide el caso de uso).
> Coherencia pasa; OBS-R-1…13 cerradas o ubicadas; `./mvnw verify` 150/150 con Docker. Explicación ronda 1 (clases 1-5) =
> **85 %**. **Siguiente paso: «lee .claude/contexto/IA_RETOMAR-AQUI.md y sigamos con la ronda 2 de la explicación del
> Segmento R»** → (6) caso de uso, (7) puerto + ensamblador de salida + DTO, (8) adaptador JDBC; luego ronda 3
> (`ManejadorDeErrores`, `CapasTest`). Con ≥80 % sobre las 11 clases y el «sí», aceptar R → 5.ª verificación → plan del 1b.
> Eclipse: si algo sale rojo tras cambios desde la terminal, F5 en **todos** los proyectos (también `contrato`) y Project ›
> Clean; el refresco automático está apagado. Las pistas a Juan muestran el texto, nunca solo un código (A-n).

> **31-oct-2026 (tarde, ÚLTIMO) · Segmento R aceptado (por decisión de Juan, explicación aplazada) y Segmento 1b.1
> escrito.** El nodo guarda el avance de cada celular (`avance_dispositivo`, versión optimista) y responde el
> `confirmado_hasta` real (C-104: 1,2 → 2 · 4 → 2 · 3 → 4 · 4 → 4). Celular configurado con
> `FLORLOGIC_INGESTA_DISPOSITIVO_ID` (A-107). `./mvnw verify` 166/166 con Docker. **Siguiente: «lee
> .claude/contexto/IA_RETOMAR-AQUI.md y escribe el Segmento 1b.2»** → `422` por uuid no v7 o consecutivo 0 desde el
> dominio (A-108), `409 particion-en-curso` con `Retry-After: 1` (C-112, dos hilos), casos fijos y
> `scripts/IA_probar-avance.sh`. Sin `clean` con Eclipse abierto.

> **31-oct-2026 (cierre) · LA CONSTRUCCIÓN COMPLETA DE TX-1 SIGUE EN `~/Work/florlogic-tx1-completa`** (copia sin git). Su
> `.claude/contexto/IA_RETOMAR-AQUI.md` tiene el estado completo (F1, F2, F3 parcial, F6, F7 hechos; 194/194) y el siguiente
> paso (identidad con Keycloak). Este repo quedó en el Segmento 1b (171/171). **Para seguir: abre Claude en
> `~/Work/florlogic-tx1-completa` y di «lee .claude/contexto/IA_RETOMAR-AQUI.md y sigue con la identidad (Keycloak)»**.

> **31-oct-2026 (tarde, 2) · Segmento 1b completo (171/171 con Docker).** 422 desde el dominio (A-108), 409
> `particion-en-curso` con `Retry-After: 1` (C-112). Juan pidió después **construir toda la TX-1 de una vez en otra copia
> del repo, cumpliendo la Guía de IS II** (`~/Downloads/Guia_Oficial_Evaluacion_Ingeniería Software II.docx`); ver el
> bloque siguiente cuando exista.

# Retomar aquí: TX-1, del canal al dominio de ingesta

Para seguir en un chat nuevo basta decir: **«lee .claude/contexto/IA_RETOMAR-AQUI.md y sigamos»**.

## Trabajo del 21 y 22-oct-2026: el WAF del gateway (Jeronimo, con visto de Juan)
- **Dónde:** `codigo/despliegue/ESPEC-waf-finca.md`. Segmento 1 escrito y verificado: coherencia pasa, completitud
  100 %, 11 mutaciones, las 11 detectadas. **La explicación (paso 6) se omitió por decisión de Jeronimo el 22-oct**
  (alcanzó a dar el archivo 1: 50 %), así que el segmento no está aceptado; su Ficha dice cómo retomarla.
- **Integrado con el refactor del hexágono por módulo** (22-oct, sin commit): `./mvnw clean verify` 122/122 con
  Docker, `CanalPorGatewayTest` 25/25. Los commits y el push los hace Jeronimo.
- **Qué cambió:** el servicio `gateway` de Compose pasa a la imagen `owasp/modsecurity-crs:4.30.0-nginx-alpine-202610180210`
  (TEC-31, la misma Nginx 1.30.6), escucha en el 8080 dentro del contenedor, con `FLORLOGIC_WAF_MODO` (On) y un
  `healthcheck` propio; `gateway/modsecurity-override.conf.template` es nuevo.
- **Pendiente de Juan:** TEC-31 → `ACEPTADO` (LEEME de transacciones e `IA_TECNOLOGIAS-vigentes.md`) y ajustar `BB-08`.
  Si no la acepta, el segmento se revierte (COH-1 de esa ESPEC). También: UTF-8 sin BOM en el contrato y qué hace el
  celular ante `gateway/bloqueado` (§7 de esa ESPEC).
- Lo que sigue abajo (el dominio de ingesta) no cambia.

## ⟶ Actualización 20-oct-2026 (tarde): el dominio de ingesta, LISTA PARA CÓDIGO

**Dónde está el trabajo:** en la copia visible `~/Work/florlogic-codigo` (rama `codigo-desarrollo`), **sin commit**
(PR-05: los commits los hace Juan). El 21-oct-2026 todo lo del worktree `ia/dominio-tx1` se copió aquí, a pedido de
Juan, y el diccionario y la skill se unieron con los cambios del gateway. **Nunca se vuelve a escribir en un
worktree:** se trabaja siempre en la copia visible, sin tocar git. Para retomar: `/detallar-para-codificar`.

| Qué | Dónde |
|---|---|
| ESPEC del dominio (estado, decisiones A-60…A-82, casos C-90…C-103, plan de 3 segmentos) | `codigo/backend-finca/espec/ESPEC-dominio-ingesta.md` · leer la **Ficha** y la «Ficha del Segmento 1/3» (§8) |
| Diagrama del Segmento 1 (valores y avance) | `codigo/backend-finca/espec/diagramas/IA_segmento-1.drawio` y `IA_segmento-1.png` |
| Diccionario (términos nuevos: agregado, avance del dispositivo, los dos motivos, `IdYaRecibido`, `Veredicto`…) | `.claude/contexto/IA_DICCIONARIO.md` |

**Estado en una línea:** la ESPEC del modelo de dominio de la ingesta está `LISTA PARA CÓDIGO` (≈88 %, 0
bloqueantes, tres rondas de preguntas). Juan **aceptó el alcance del dominio mínimo de TX-1** el 20-oct en la
sesión. Etapa 4, Segmento 1: plan y diagrama presentados; **falta el «sí» de Juan para escribir el código**.

**Qué decide Juan antes o durante el código:**
1. El «sí» al Segmento 1 (14 archivos, ≈750 líneas, solo Java puro y pruebas unitarias).
2. K-115: forma canónica de la huella (propia sobre Jackson 3, que TEC-10 acepta, o biblioteca RFC 8785). Solo
   bloquea el Segmento 3.
3. Reflejar su aceptación en la fila TX-1 de `IA_00-LEEME-transacciones.md` (rama `documentacion-desarrollo`;
   documento humano, lo edita Juan). `CLAUDE.md` aún dice «POR ACEPTAR»: también conviene corregirlo.
4. Confirmar la precisión agregada a «Juicio de la anotación» en el diccionario (`sin_efecto` es un resultado de
   llegada, no un juicio guardado).

**Decisiones de Juan en este detallamiento que mandan:** solo el modelo de dominio (sin servicio, notificación ni
observabilidad); dos raíces pequeñas (`Anotacion`, `AvanceDelDispositivoAgregado`); `enum Resultado` con guardas;
un consecutivo o uuid inválido = `422` de toda la partición «porque si un consecutivo falla, toda la partición
peligra»; el hueco se guarda marcado y el celular reanuda; una cama no capturada no es hueco; subpaquetes
`comun`, `eventos`, `anotacion`, `avance`, `reglas`; reconstituir `Anotacion` fuera del borde.

Lo de abajo es el estado anterior (canal y contrato); sigue vigente para el contrato.

## Estado en una línea
El canal de TX-1 funciona de punta a punta y está **verificado** (Segmento 2: 65/65 pruebas con Docker, coherencia
pasa, completitud 100 % sobre el camino mínimo). Su aceptación espera la explicación de Juan. El canal se **traspasó**
a sus semillas (D-19): el siguiente trabajo es el **dominio de ingesta**, que arranca desde ese código.

## Comprobar que todo sigue bien (2-3 minutos, necesita Docker)
```
cd ~/Work/florlogic-codigo/codigo
./mvnw clean verify          # 65 pruebas en verde; sin Docker se saltan las de PostgreSQL (dilo, no lo des por probado)
scripts/IA_probar-canal.sh   # «2 nuevas», «2 ya estaban», «Rechazada» (código 1), «Canal OK»; usa su propio compose
```

## Lo que se decidió el 20-oct-2026 (Juan)
| Qué | Dónde quedó |
|---|---|
| **D-18:** el camino mínimo recorta clases, nunca el empaquetado; un paquete mal ubicado es bloqueante | Skill `DECISIONES.md` · `codigo/IA_LEEME.md` (convención de paquetes) |
| Paquetes «plantilla en español»: `servicios/puertos/entrada` · `puertos/salida` · `casosuso` · `infraestructura/entrada/web` (o `cli`) · `infraestructura/salida/persistencia` (o `http`) | `codigo/IA_LEEME.md` · ESPEC §10 (OBS-2-9, OBS-2-12) |
| Simulador hexagonal completo | `codigo/IA_LEEME.md` «Dentro del simulador» |
| Pirámide de pruebas en `ingesta`: contrato del puerto + doble en memoria + prueba JDBC + unitaria del servicio | `codigo/IA_LEEME.md` «Pruebas» · ESPEC (OBS-2-11) |
| **A-59:** el nodo guarda el JSON equivalente de cada anotación, no sus bytes | ESPEC §2 |
| COH-105: un U+0000 en una anotación es 422 (antes daba 500 para siempre) | ESPEC §11 |
| **D-19:** el canal se traspasa a los elementos que tocó | Skill `DECISIONES.md` · ESPEC §12 · las tres semillas («Punto de partida») |
| Orden: aceptar el Segmento 2 → traspaso → contrato (Segmentos 3-7 y ronda 2) → `IMPLEMENTADA` | ESPEC, Ficha |
| La skill vive **solo en el proyecto** (`.claude/skills/detallar-para-codificar`); la copia vieja de usuario, con su git, está en `~/.claude/skills-respaldo/` | — |
| El C4 y `IA_FUNCIONAMIENTO-ESPERADO.md` son arquitectura de **referencia**, no se corrigen por diferencias de forma | Memoria de Claude |

## Encargo para el chat nuevo (palabras de Juan, 20-oct)
> Detallar y codificar **el dominio y las reglas de negocio**, identificando puntualmente **solo las totalmente
> necesarias** para el funcionamiento directo y básico de la **ingesta de información, su clasificación** y el uso de
> los **componentes mínimos** que necesite. Por ejemplo: en el dominio de ingesta hacen falta **notificaciones** al
> celular del gerente de producción y a su **correo**. Ir utilizando e implementando los componentes a través de esas
> reglas, **usando lo que ya esté construido**. Y **los servicios de observabilidad**: que las cosas vayan apareciendo
> **en paralelo** a medida que avanza el código.
>
> Aclaración (mismo día): «no sólo la observabilidad: las necesidades de componentes y tecnologías **van creciendo
> cuando se van definiendo comportamientos y reglas** a través de lo que se escriba en el dominio».

**El principio que manda en el chat nuevo: el dominio tira de la infraestructura.** No se diseñan ni se instalan
componentes por adelantado. Se escribe una regla del dominio; esa regla dice qué necesita (avisar a alguien, esperar a
que haya red, medir algo, leer un umbral, programar algo para más tarde); y **en ese momento** entra el componente
mínimo que la cumple, con su puerto en `servicios/puertos/salida`, su adaptador en `infraestructura`, su prueba y, si
trae tecnología nueva, su fila propuesta en `IA_TECNOLOGIAS-vigentes.md` para que Juan la acepte. Ejemplos de lo que
puede ir apareciendo así: notificación (módulo `notificacion`), cola y reintento (TEC-09 db-scheduler), métricas y logs
(TEC-17, módulo `observabilidad`), configuración de umbrales (semilla de configuración), eventos entre módulos (Spring
Modulith). Cada componente nace con la regla que lo pidió y queda trazado a ella.

Cómo se lee con la skill (`/detallar-para-codificar`):
- **Elemento de partida:** semilla `codigo/contrato/espec/IA_apartado-2026-10-18/semillas/IA_SEMILLA-caso-de-uso-ingreso.md`
  (plantilla `caso-de-uso`). Arranca desde el canal ya escrito (su sección «Punto de partida»). Le siguen, en rondas
  propias: `entidad-agregado` (qué es el agregado: anotación o sesión, OBS-2-17), `evento-mensaje` (ingesta →
  `notificacion`, con registro de publicación de Spring Modulith, según `IA_TECNOLOGIAS-y-estructura.md` §4) y
  `configuracion` (catálogo y umbrales, semilla `IA_SEMILLA-configuracion-catalogo-y-versiones.md`).
- **Camino mínimo (D-17):** el juicio más simple de cada anotación (aplicada / marcada / rechazada) y **un** aviso al
  gerente, de punta a punta, antes que las reglas finas.
- **Componentes en paralelo** (principio de arriba): cada regla trae lo que necesita. La observabilidad es uno más:
  su métrica (Micrometer) y su log estructurado, sin datos de negocio en la telemetría (BB-13, `ESC-30`, `CN-34`).
- **«Lo que ya está construido»:** hoy los módulos `notificacion/` y `observabilidad/` son **solo `package-info`**; lo
  único con código es `ingesta/` (el canal) y, de TEC-17, Actuator. No hay que inventar que existe más.

## Antes de escribir código en el chat nuevo: lo que decide Juan
1. **TX-1 está `AJUSTAR`**: solo el canal está aceptado en `IA_00-LEEME-transacciones.md`. CLAUDE.md: las reglas de una
   transacción se programan cuando su fila, sus P y sus TEC están `ACEPTADO`. Hay que aceptar el alcance del dominio
   mínimo (o la skill para y pregunta).
2. **Correo y push no tienen tecnología aceptada**: ninguna fila en `IA_TECNOLOGIAS-vigentes.md` (BB-10 dice «correo
   transaccional + notificación push», sin herramienta). Se proponen con su fila y Juan las acepta antes de usarlas.
3. **Local-first:** el nodo de finca opera **sin internet**; un correo o un push necesitan salir a internet. Hay que
   decidir qué pasa cuando no hay red (cola en la base, TEC-09 db-scheduler, y reenvío al volver) y si el aviso al
   celular del gerente es push (internet) o por la red de la finca.
4. (Menor) **Telemetría** (TEC-18, servicio propio) vive en `servicios-en-linea`: decidir si en el camino mínimo basta
   con métricas y logs locales (TEC-17).

## Pendiente del detallamiento del contrato (no se pierde)
- **Explicación de Juan del Segmento 2** (paso 6, ≥80 %, clase por clase: práctica y patrón). Sin ella, el Segmento 2
  no está aceptado. Puede darse en el chat nuevo.
- Contrato: Segmentos 3 (huella), 4 (errores y fronteras), 5 (esquemas; bloqueado por la biblioteca de TEC-11),
  6 (validador único, A-58), 7 (dorados y arquitectura) y la **ronda 2** (`/sesiones`, `/cierre`, `/archivos`).
- `application.yaml` trae una clave de desarrollo por defecto (`florlogic`): decidir con PR-06 (ESPEC §7).
- `information-pairment` sigue duplicada (usuario y proyecto).
- Apareció `build/generated-spring-modulith/` en la raíz del repo (lo genera Spring Modulith al correr desde ahí): sin versionar; decidir si va al `.gitignore`.

## Qué leer al volver, en orden
1. Este archivo.
2. `codigo/contrato/espec/ESPEC-contrato-ingesta.md`: la Ficha y §12 (traspaso). No hace falta leer las HISTORIAS.
3. La semilla `IA_SEMILLA-caso-de-uso-ingreso.md` («Punto de partida» y «Abierto»).
4. `codigo/IA_LEEME.md` (paquetes y pruebas) e `IA_TECNOLOGIAS-vigentes.md`.

## Herramientas
- **Biblioteca**: MCP `biblioteca` (61 libros; citar «Título · p. N»).
- **draw.io**: generador en `.claude/skills/detallar-para-codificar/herramientas/drawio.py`; exportar con
  `drawio -x -f png -p <página, desde 1> -o <png> <archivo>`.
- **Eclipse**: workspace `~/eclipse-workspace` (6 proyectos; vista jerárquica). Tras mover paquetes: F5 o *Maven › Update Project*.

## Reglas que no hay que olvidar
- Camino mínimo primero (D-17), pero **con el empaquetado correcto** (D-18).
- El canal se traspasa a sus elementos (D-19).
- Ningún punto de entrada lanza ante una entrada externa (A-57).
- Ninguna dependencia sin su fila en `IA_TECNOLOGIAS-vigentes.md`. Commits y empujes los hace Juan (PR-05).
