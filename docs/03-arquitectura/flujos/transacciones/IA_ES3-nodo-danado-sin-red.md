REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 8-oct-2026, a pedido de Juan
Estado de revisión: POR ACEPTAR — nada de este archivo vale para programar hasta que Juan lo marque ACEPTADO en `IA_00-LEEME-transacciones.md`
Manda por encima: `ADR.xlsx` (hoja ADR) · `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx` · `docs/03-arquitectura/decisiones/`

# ES3 · Event storming: se daña el nodo de la finca y no hay red

**Juan, 8-oct:** «no los podemos dejar tirados en medio de operación sin solución inmediata, uno de
los peores escenarios sería que sin red ni wifi ni conexión a internet se les dañe el sistema o algún
componente y no tengan cómo volver a trabajar».

## 0 · Lo primero que hay que tener claro

**Cuando cae el nodo, la captura no se pierde: ya vive en los celulares.** `ADR-002` dice que nada se
borra del celular hasta que el servidor confirma. Lo que **sí se pierde**, y en este orden de urgencia, es:

1. **La credencial, a las 24 horas.** `ADR-007` la renueva solo al sincronizar. Si el nodo sigue caído
   pasado ese plazo, **los operarios dejan de poder capturar**. Es la consecuencia que el propio ADR
   escribe: «un usuario pueda perder acceso de manera completa al sistema».
2. **El recordatorio escalado de `ADR-025`.** Con días sin sincronizar, termina **impidiendo trabajar**,
   aunque la culpa no sea del operario sino del nodo.
3. **El espacio del celular**, después de varios días sin descargar (`ESC-35`).
4. **Lo que entró al nodo y todavía no estaba respaldado**, si el disco murió.
5. **La consulta, la proyección y los tableros.** Molesta, pero no detiene el campo.

Así que el plan de continuidad no empieza por «levantar otro servidor». Empieza por **evitar que los
puntos 1 y 2 detengan la captura**, que hoy los detendrían. Levantar otro servidor viene después.

---

## 1 · Event storming

Formato de Juan: título y descripción, **estímulo**, **entorno**, **qué pasó** y **cómo lo abordamos**.
Los casos largos tienen su diagrama en el mazo.

### ES-N01 · Se va la luz en la oficina a mitad de la jornada
*Caen el nodo y el router. Los celulares están en el campo.*
- **Estímulo:** corte de energía de 3 horas.
- **Entorno:** jornada en curso y dos celulares sincronizando en la oficina.
- **Qué pasó:** las sesiones de sincronización en curso quedaron abiertas y la base se apagó sin avisar.
- **Cómo lo abordamos:**
  - La captura en el campo no lo nota.
  - Al volver la luz, la base se recupera sola de su registro de escritura, y el backend cierra como parciales las sesiones que quedaron abiertas.
  - Los celulares reenvían lo que no tenía acuse (ES-S03).
  - **Propuesta:** una UPS pequeña para el nodo y el router, que apague el nodo de forma ordenada antes de que se agote. Es hardware de la instalación y mueve el precio.

### ES-N02 · El backend no arranca después de una actualización
*La base está bien; el programa no.*
- **Estímulo:** la versión nueva falla al arrancar.
- **Entorno:** noche de actualización.
- **Qué pasó:** sin backend no hay sincronización ni consulta.
- **Cómo lo abordamos:**
  - **Volver a la versión anterior.** `SPK-15` midió 0,334 s solo para el código.
  - **El esquema no siempre vuelve:** 19 de 20 migraciones fueron reversibles en `SPK-15`, y la que no devolvió una columna vacía.
  - Por eso el modelo pide **respaldar antes de migrar**, y las migraciones se escriben en dos tiempos: primero se añade y después se quita.
  - El ciclo es **CN-15: fallo ≤1 hora**.

### ES-N03 · La base de datos se corrompe
*El disco empieza a fallar por sectores.*
- **Estímulo:** errores de lectura en tablas de la base.
- **Entorno:** nodo con disco envejecido.
- **Qué pasó:** no se puede confiar en lo que hay.
- **Cómo lo abordamos:**
  - Se restaura el último respaldo bueno: el local si existe, si no el de la nube.
  - **Los celulares reenvían lo retenido** (P-S03) y la idempotencia rellena el hueco desde el último respaldo.
  - La observabilidad debería haberlo anunciado antes, con la salud del disco.

### ES-N04 · El disco del nodo muere y hay internet
*Pérdida total del nodo, con la nube disponible.*
- **Estímulo:** el nodo no enciende.
- **Entorno:** oficina con internet.
- **Qué pasó:** no hay servidor, pero sí respaldo en la nube.
- **Cómo lo abordamos:**
  1. Hardware de reemplazo: el **nodo de repuesto** (OC-5) o un portátil (OC-4).
  2. La misma imagen (`ADR-016`).
  3. Bajar el respaldo y descifrarlo con la llave del cliente.
  4. Restaurar.
  5. Los celulares reenvían lo retenido.
  - Meta: **CN-15, ≤1 día**. `SPK-13` midió 2,01 min de restauración sobre la acumulación de cinco años; lo que falta medir es bajar el archivo por el enlace real.

### ES-N05 · El disco del nodo muere y NO hay internet, ni Wi-Fi, ni red
*El peor caso que pidió Juan. Tiene su árbol de decisión en el mazo.*
- **Estímulo:** el nodo no enciende, el router está dañado y no hay internet en la zona.
- **Entorno:** media jornada con los operarios en el campo.
- **Qué pasó:**
  - No hay servidor, ni red, ni respaldo alcanzable en la nube.
  - Los celulares tienen sus capturas, pero las credenciales vencen en ≤24 h.
- **Cómo lo abordamos.** Por capas, de la más barata a la más cara:
  1. **Que la captura no se detenga** (OC-1). Gracia de contingencia en la credencial y recordatorio que no castiga al operario por un nodo caído.
  2. **Que lo capturado tenga dos copias** (OC-2). Buzón en el celular del gerente, que recibe por su punto de acceso las capturas de los demás. Ningún celular borra por eso.
  3. **Volver a tener un sistema** (OC-4, OC-5 u OC-3):
     - portátil o nodo de repuesto con la misma imagen;
     - restaurar desde la **copia local** (OC-7) con la llave del cliente;
     - la red la pone el router de repuesto o el punto de acceso del celular del gerente.
  4. **Si nada de eso existe:** papel (OC-8) y captura retroactiva cuando vuelva el sistema (`RF-003` la permite).
- **Lo que no tiene arreglo sin preparar antes:** sin **copia local** y sin internet, lo que había
  en el nodo y no estaba en ningún celular **no se puede recuperar hasta que vuelva internet**. Por eso
  la retención del celular (P-S03) importa tanto.

### ES-N06 · Se daña el router o el Wi-Fi; el nodo está bien
*La finca tiene servidor pero no tiene red.*
- **Estímulo:** el router no enciende.
- **Entorno:** cierre de jornada.
- **Qué pasó:** los celulares no alcanzan el gateway.
- **Cómo lo abordamos:**
  - **Propuesta:** un **router de repuesto ya configurado** con la misma red y la misma clave. Cuesta poco y tarda minutos en cambiarse.
  - Mientras tanto, canal C3 por archivo y cable (ES-S15).
  - El punto de acceso del celular del gerente solo sirve si el nodo tiene Wi-Fi, y un servidor suele ir por cable.

### ES-N07 · Se roban el nodo
*Pérdida y además riesgo de exposición.*
- **Estímulo:** el nodo desaparece de la oficina.
- **Entorno:** fin de semana.
- **Qué pasó:**
  - Se fue el servidor.
  - Se fueron también las llaves de firma: con ellas alguien podría firmar credenciales y paquetes falsos.
- **Cómo lo abordamos:**
  - **Disco cifrado** (`CN-28`; la medición real está abierta, `AB-03`).
  - Restaurar en hardware nuevo (ES-N04).
  - **Rotar las llaves de firma.** Los celulares tienen que **volver a confiar** en la llave nueva al sincronizar, con un nuevo registro de celulares.
  - Avisar al administrador.

### ES-N08 · El nodo lleva más de 24 horas caído: vencen las credenciales
*El fallo silencioso que detiene el campo.*
- **Estímulo:** pasan 24 h sin que nadie sincronice.
- **Entorno:** nodo dañado y a la espera de repuesto.
- **Qué pasó:** con `ADR-007` literal, **los operarios no pueden desbloquear la aplicación al día siguiente**.
- **Cómo lo abordamos:**
  - **Propuesta:** la credencial lleva **dos vigencias**: la normal de 24 h, y una **gracia de contingencia** de N días que **solo permite capturar**, no corregir lo cerrado ni administrar. Se usa sola cuando el celular no ha logrado sincronizar.
  - `ADR-007` ya deja escrito en su observación que se puede subir la validez a 7 o 10 días.
  - Es la decisión de seguridad contra disponibilidad que `RF-014` anuncia, y va al cliente con `BR-N5`. P-N01.

### ES-N09 · El recordatorio escalado bloquea a los operarios
*`ADR-025` castiga al que no sincroniza; aquí no es su culpa.*
- **Estímulo:** lo pendiente pasa el umbral de bloqueo.
- **Entorno:** el nodo lleva días caído.
- **Qué pasó:** la aplicación impediría seguir trabajando.
- **Cómo lo abordamos:**
  - **Propuesta:** el recordatorio distingue entre **«no lo intentaste»** y **«lo intentaste y el nodo no respondió»**. El celular sabe si estaba en el Wi-Fi de la finca y el gateway no contestó.
  - En el segundo caso el aviso sigue, pero **no escala a bloqueo**.

### ES-N10 · Los celulares se llenan después de días sin descargar
*`ESC-35`.*
- **Estímulo:** el espacio libre baja del umbral.
- **Entorno:** contingencia larga y gama de entrada (`CN-21`).
- **Qué pasó:** se arriesga lo que todavía no se ha capturado.
- **Cómo lo abordamos:**
  - **Buzón** (OC-2). El celular del gerente recibe copias.
  - **Solo en emergencia de espacio**, el operario puede liberar lo que el buzón ya acusó.
  - Fuera de esa emergencia, nadie borra.

### ES-N11 · Se levanta el sistema completo en el celular del gerente
*La opción que pidió Juan. El análisis de factibilidad está en el §3.*
- **Estímulo:** el gerente declara la contingencia y arranca el «modo nodo» en su celular.
- **Entorno:** sin nodo, sin router y sin internet.
- **Qué pasó:**
  - El celular del gerente hace de punto de acceso y de servidor a la vez.
  - Los demás celulares se conectan y sincronizan contra él.
- **Cómo lo abordamos:**
  - Sirve como contingencia **si se preparó antes**: el sistema instalado, la copia local cargada, la llave del cliente y el certificado de contingencia ya conocido por los celulares.
  - **Durante la contingencia no se publica configuración ni se cierran producciones.** Solo se recibe y se consulta.
  - Ver el §3 para lo que de verdad se puede hacer en Android.

### ES-N12 · Vuelve el nodo real: juntar lo que se recibió en contingencia
*Dos lugares recibieron capturas.*
- **Estímulo:** el nodo restaurado arranca y el de contingencia todavía tiene datos.
- **Entorno:** después de la contingencia.
- **Qué pasó:** hay anotaciones en el buzón o en el nodo de contingencia que el real no tiene.
- **Cómo lo abordamos:**
  - El nodo de contingencia **reenvía por el mismo ingreso** de TX-1, conservando los ids, las sesiones de captura y los sellos originales.
  - La **idempotencia hace que juntar sea sumar**: lo repetido no hace nada y lo nuevo entra.
  - Gracias a `ADR-024` y `ADR-027` **no hay que resolver nada a mano**. Como en contingencia no se publicó configuración, no hay dos versiones que reconciliar.

### ES-N13 · Cae también el celular del gerente
*La contingencia de la contingencia.*
- **Estímulo:** el celular del gerente se apaga o se pierde.
- **Entorno:** contingencia en curso.
- **Qué pasó:** no queda dónde juntar copias.
- **Cómo lo abordamos:**
  - Cada operario sigue con su celular (OC-1).
  - **Si tampoco hay celulares, papel:** las plantillas que la finca ya usa hoy.
  - Después se digitan con la captura retroactiva.
  - **Es honesto decirlo:** el papel es el último recurso, y el cliente ya sabe usarlo.

### ES-N14 · El daño ocurre en plena temporada alta
*El peor momento: marzo y abril.*
- **Estímulo:** cualquiera de los anteriores.
- **Entorno:** +60 % de registros y +30–40 % de personal (`CN-30`).
- **Qué pasó:** cada hora sin sistema cuesta más.
- **Cómo lo abordamos:**
  - La prioridad es **la captura primero**: credencial con gracia y recordatorio sin bloqueo. Después **las dos copias**, y después el sistema.
  - **Propuesta:** antes de cada temporada, simulacro de 30 minutos con el nodo de repuesto.

---

## 2 · Opciones de continuidad

| ID | Opción | Qué cubre | Costo | Riesgo | Lectura de la IA |
|---|---|---|---|---|---|
| **OC-1** | **Captura que no se detiene:** credencial con gracia de contingencia más recordatorio que distingue la culpa | Credenciales y bloqueo (puntos 1 y 2 del §0) | Bajo: es lógica en la credencial y en la app | Seguridad: una credencial robada dura más. Va con `BR-N5` | **Imprescindible.** Sin esto, todo lo demás llega tarde |
| **OC-2** | **Buzón en el celular del gerente:** recibe las capturas de los demás por su punto de acceso, las guarda cifradas y las reenvía al volver el nodo | Segunda copia y espacio | Medio: un modo más en la app de captura, con el contrato de TX-1 recortado | La app de captura no está decidida (`ADR-008`) | **Recomendada.** Es pequeña y reutiliza TX-1 |
| **OC-3** | **Sistema completo en el celular del gerente** | Todo, incluida la consulta | Alto | Alto: ver §3 | **Experimento**, no garantía. Necesita un spike sobre el celular real |
| **OC-4** | **Portátil de contingencia** con la misma imagen | Todo | Bajo si la finca ya tiene un portátil | El portátil tiene que estar preparado y probado | **Recomendada** si no hay nodo de repuesto |
| **OC-5** | **Nodo de repuesto en frío:** un mini PC con la imagen instalada, guardado en un cajón | Todo, en <1 h (`CN-15`) | Medio: un equipo más en la instalación | Hay que mantenerlo actualizado | **Recomendada** para cumplir el «fallo ≤1 h» de `CN-15` |
| OC-6 | Réplica en caliente: un segundo servidor que copia en vivo | Todo, en minutos | Alto | **Reabre `ADR-017`**, que excluye «la duplicación para alta disponibilidad» | Solo si el cliente exige minutos y no horas |
| **OC-7** | **Copia local del respaldo** (disco o USB en la finca) más la llave del cliente en la caja fuerte | Restaurar sin internet | Bajo | Que la copia esté vieja; la observabilidad lo vigila | **Imprescindible.** Sin ella, ES-N05 no tiene salida |
| **OC-8** | **Papel** y captura retroactiva | El último recurso | Nulo | Vuelven las 4 h de traslado | Siempre presente; hay que decirlo en el contrato |
| **OC-9** | **Router de repuesto** ya configurado | La red local | Muy bajo | — | **Recomendada** |

**Recomendación por capas:** OC-1 + OC-7 + OC-9 como base, OC-2 encima, OC-5 u OC-4 para volver a
operar, y OC-3 como experimento medido. OC-8 siempre.

---

## 3 · ¿Se puede correr el sistema completo en un contenedor en el celular del gerente?

**Lo que pidió Juan:** «que se pudiera correr a veces si es necesario el sistema completo en un
contenedor en el dispositivo móvil del gerente de producción».

**La respuesta corta: un contenedor tipo Docker, en la mayoría de los celulares, no. El sistema
completo sin contenedor, sí parece posible, con condiciones que hay que medir.**

| Camino | Qué es | ¿Funciona? | Fuente |
|---|---|---|---|
| Docker o Podman sobre Android normal | Contenedores de verdad | **No.** En Termux con proot no hay espacios de nombres, cgroups ni sistemas de archivos superpuestos, que son lo que Docker necesita | [web] Cosyra, guía de contenedores Linux en Android (2026) |
| **Terminal de Linux de Android 16** (una máquina virtual con Debian) | Linux completo dentro del celular | **Solo en equipos que soportan la virtualización de Android: Pixel y algunos Android One. Los Samsung no lo soportaban al 1-jul-2026.** Si Docker corre dentro, no está confirmado | [web] misma guía · Android Authority (2024) |
| **Termux con Java y PostgreSQL nativos** | El mismo JAR del backend y PostgreSQL para ARM, sin contenedor | **Probablemente sí.** Termux instala OpenJDK y PostgreSQL | [web] · **sin medir** |

**Las condiciones del camino Termux, que son las que hay que medir:**
1. **El «asesino de procesos fantasma» de Android 12 y posteriores** mata procesos de fondo cuando
   pasan de 32 o cuando usan mucha CPU. En **Android 14 y posteriores** se desactiva en las opciones de
   desarrollador, con «Desactivar restricciones de procesos secundarios». En 12 y 13 hace falta un
   comando por cable. **Una actualización del sistema puede volver a activarlo.** [web] Cosyra, guía de
   «signal 9».
2. **Memoria.** `SPK-17` midió el backend Java en unos 60 MB en reposo; `SPK-15`, entre 66 y 116 MB.
   Spring Boot sumará algo. Más PostgreSQL. **Cabe en un celular de 6–8 GB**, pero hay que medirlo con
   carga.
3. **Arquitectura.** El nodo será x86 y el celular ARM. El JAR es el mismo, así que `ADR-016` se
   cumple en el código. Lo que cambia es el empaque: Java y PostgreSQL para ARM. **La imagen tiene que
   construirse para las dos arquitecturas.**
4. **Red.** El celular del gerente tiene que ser punto de acceso y servidor a la vez. Android lo
   permite, pero la dirección del punto de acceso cambia según el fabricante. Los demás celulares
   tendrían que encontrarlo por un nombre fijo o una dirección conocida. **Sin medir.**
5. **Certificado.** Los celulares confían en la autoridad propia de la finca. El modo nodo necesita un
   certificado que ellos ya conozcan. **Propuesta:** un «ancla de contingencia» que se instala en
   cada celular al registrarlo, para no copiar la llave de la autoridad al celular del gerente.
6. **`[!]` El dato entero de la finca dentro de un celular que sale de la finca.** Choca con el
   principio de la ronda 4: «la información de la finca no vive completa en el dispositivo».
   **Decisión tuya** (P-N03). Mitigación: almacén cifrado, borrado al terminar la contingencia y solo
   la producción activa, no los cinco años.

**Lectura de la IA:** el sistema completo en el celular del gerente es posible como **experimento**
y peligroso como **promesa**. Depende del modelo exacto del celular, de su versión de Android y de un
ajuste que una actualización puede deshacer. **Lo que sí da garantía con poco costo es OC-2** —el
buzón, que no necesita base ni backend—, **más OC-5 u OC-4**.

Si quieres OC-3, la IA propone un **spike pequeño** con el celular real del gerente: instalar, restaurar
la producción activa, sincronizar dos celulares contra él y medir memoria y batería durante 4 horas.
Por la regla del 15-sep entraría marcado en **rojo**, como abierto por riesgo de que estalle.

---

## 4 · Lo que esta contingencia toca de las fuentes

| Fuente | Qué dice | Qué le pasa en contingencia |
|---|---|---|
| `CN-15` | Pérdida cero · fallo ≤1 h · restaurar ≤1 día | ≤1 h solo con OC-5. ≤1 día con OC-4 y OC-7. Pérdida cero solo con la retención del celular (P-S03) |
| `ADR-007` | Credencial de 24 h | **Detiene el campo al segundo día.** OC-1 / P-N01 |
| `ADR-025` | El recordatorio escala hasta impedir trabajar | **Castiga al operario por un nodo caído.** ES-N09 |
| `ADR-016` | El mismo artefacto en cualquier sitio | **Es lo que hace posibles OC-3, OC-4 y OC-5** |
| `ADR-017` | Sin duplicación para alta disponibilidad | OC-6 lo reabriría. OC-5 es un repuesto apagado, no una duplicación: confirmar |
| `ADR-012` | Custodia del equipo solo ante excepción | Sin internet, la custodia del equipo llega tarde. La copia del cliente va primero (ES-R13) |
| `ESC-04` | Jornada completa sin red | Se cumple, también con el nodo caído |
| `ESC-54` | Reposición de un celular en ≤1 h | No cambia |
| `ESC-59` | Si la nube cae, la finca sigue | Aquí cae el nodo, que es peor. Ningún escenario lo cubre. **Hueco: falta un escenario «cae el nodo de la finca»**. Propuesta para `EscenariosCalidad.xlsx`; lo aplicas tú |

---

## 5 · Casos de uso de la contingencia

Los diagramas están en el mazo, lámina «Contingencia · casos de uso».

| ID | Caso de uso | Actor |
|---|---|---|
| UC-C1 | Declarar la contingencia y anotar desde cuándo | Gerente de producción |
| UC-C2 | Seguir capturando con la gracia de la credencial | Operario de campo |
| UC-C3 | Entregar una copia de lo capturado al buzón del gerente | Operario de campo → celular del gerente |
| UC-C4 | Levantar el nodo de reemplazo y restaurar sin internet desde la copia local | Administrador del sistema en la finca |
| UC-C5 | Juntar en el nodo real lo recibido durante la contingencia | Administrador; el reenvío lo hace el sistema |

---

## 6 · Lo que tienes que decidir

| ID | Pregunta | Opciones (la recomendada primero) |
|---|---|---|
| **P-N01** | ¿Credencial con gracia de contingencia? ¿De cuántos días? | (a) **sí: 24 h normales más 7 días solo para capturar** · (b) subir todo a 7–10 días, como dice la observación de `ADR-007` · (c) dejarla en 24 h |
| **P-N02** | ¿El recordatorio distingue «no lo intentaste» de «el nodo no respondió»? | (a) **sí** · (b) no |
| **P-N03** | ¿Se permite el dato de la finca en el celular del gerente durante una contingencia? | (a) **solo en el buzón (OC-2), sin base completa** · (b) sistema completo solo con la producción activa · (c) no |
| **P-N04** | ¿Qué hardware de contingencia se incluye en la instalación? | (a) **router de repuesto, UPS y disco para la copia local; nodo de repuesto como opción de precio** · (b) solo el router · (c) nada |
| **P-N05** | ¿Se hace el spike de OC-3 con el celular real del gerente? | (a) **después de TX-1, si OC-2 no alcanza** · (b) ya · (c) no |
| **P-N06** | ¿Se escribe un escenario «cae el nodo de la finca» en `EscenariosCalidad.xlsx`? | (a) **sí, lo redactas tú** · (b) no |
