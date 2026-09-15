# `SPK-13` · Cifrado y respaldo — resultado

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 15-sep-2026
> Estado: **SIN REVISAR** · Chat 3 de 3
> Manda sobre esto: `ADR-012`, `ADR-003`, `ADR-022` · `CN-15`, `CN-28`, `CN-03` · `ESC-19`, `ESC-50`

**Banco:** PostgreSQL 16.13 · 2 núcleos Xeon 2,8 GHz **con AES-NI** · 8 GB. **Conjunto: 24.552.600
eventos, 5.339 MB** — la acumulación de cinco años de `ADR-022` construida entera, no extrapolada.
Esquema fiel a `ADR-024`/`ADR-035`/`ADR-027`/`ADR-031`. **Ningún número depende del lenguaje del
backend.** Guiones, datos y salidas completas en `labs/spk-13/`; el detalle largo, en
`labs/spk-13/salidas/EVIDENCIA-detallada.md`.

---

## Medición

**1 · Cifrado en reposo.** `[!]` **La cota de volumen es cota, no medición** — el entorno no tiene
`dm_mod`: no se abrió ningún LUKS (ver «Qué NO probé»).

| | |
|---|---|
| AES-256-XTS a 8 KiB (lo que usa LUKS) | **6.310 MB/s** un núcleo |
| Pico de ingesta medido | **84.618 ev/s** = 41,0 MB/s de WAL |
| **Cota del cifrado de volumen** | **0,65 % de un núcleo** · a 10× el pico, 6,5 % |

Dentro de la base (`pgcrypto` sobre el valor), **contra un testigo por el mismo camino de dos pasos**:
testigo 83.367 ev/s · `pgp_sym_encrypt` **3.263 ev/s (25,5× más lento)** · con `s2k-count=1024`
42.820 · `encrypt()` AES con la llave ya derivada **68.136 (solo 1,22×)**. **El 25× no es AES: es
derivar la llave en cada fila.** En **lectura**, sobre las mismas 13.150 filas: claro **3,4 ms**,
cifrado **48,5 ms** — **14×, y sin índice posible**, así que crece con las filas recorridas (a 24 M,
~89 s contra ~6,3 s).

**2 · Respaldo con la llave de la empresa (`age` X25519) y restauración.**

| | |
|---|---|
| `pg_dump -Fc -Z6` | 64,0 s → **478 MB** |
| **Cifrarlo con la llave de la empresa** | **0,68 s · +117 KB = +0,02 %** |
| `pg_basebackup -Ft -z -Xs` (habilita PITR) | 421,8 s → 1.879 MB |
| **RESTAURACIÓN COMPLETA** (descifrar 2,8 s + `pg_restore -j2` 118,1 s) | **120,9 s = 2,01 min** |

**Suma de control idéntica sobre las 24.552.600 filas. `CN-15` pide 1 día: margen 714×.**
En tránsito, TLS 1.3 da 1.923 MB/s (15,4 Gbps): nunca es el cuello. Envío a `N4`: la siembra de
390 MB tarda 52 min a 1 Mbps; **el día a día son 0,7 MB — 5,6 s a 1 Mbps.**

**3 · `ESC-19` — el respaldo se verifica solo.** Guion de tres niveles con código de salida
(`bin/05_verificar.sh`): N1 integridad del cifrado **0,5 s** · N2 estructura 1,0 s · N3 **restauración
real + suma de control 120,2 s**. Total **121,8 s**, veredicto `APTO_PARA_RESTAURAR`. **Y falla cuando
debe:** 64 bytes corrompidos al azar → **N1 los detecta en menos de 0,5 s**; el cifrado autenticado
hace de suma de control. Sobre el repositorio deduplicado, `restic check --read-data`: 4,5 s.

**4 · `CN-28` — quien guarda no puede leer. Demostrado contra testigo.**

| | Cifrado | Testigo: el mismo respaldo **sin** cifrar |
|---|---|---|
| `grep` de `finca`/`evento`/`produccion`/`campo_`/`PGDMP` | **0 · 0 · 0 · 0 · 0** | 56 · 20 · 15 · 8 · 1 |
| Abrir con otra llave | `no identity matched any of the recipients` | — |
| `pg_restore -l` | `not a valid archive` | **lista el esquema entero y saca filas reales** |

`[!]` **Dos matices.** La **entropía no prueba nada**: el volcado comprimido da 7,945 contra 8,000 del
cifrado — **comprimir no es cifrar**, y lo que prueba es el `grep`. Y **sí se filtra** el tamaño
exacto, la fecha y la cadencia: quien guarda sabe cuánto crece la finca y qué días no hubo respaldo.

**5 · Crecimiento, con retención de cinco años.** Repositorio deduplicado y cifrado: siembra
**389,8 MB**; **delta diario ~712 KB** (cuatro días: 650·844·596·758 KB); **un ciclo de producción**
(1,2 M eventos) suma **268,7 MB a la base pero solo 18,0 MB al repositorio**. Restauración desde él:
126,6 s, suma de control idéntica.

| A cinco años, una instantánea diaria | |
|---|---|
| Repositorio deduplicado | **1,62 GB** |
| Copias completas cifradas, una por día | **813 GB** — **502×** |

`[!]` **El orden importa: deduplicar y luego cifrar.** Al revés, cada volcado es un archivo distinto,
no engancha nada, y se vuelve a los 456 MB diarios.

**6 · Custodia (`ADR-012`, dos ejemplares en sitios separados).** Verificado en los tres pares y con
un sitio destruido:

| | Un sitio robado | Un sitio perdido |
|---|---|---|
| Copia simple (llave entera en cada ejemplar) | **lee todo el dato del cliente** | se sobrevive |
| 2-de-2 | no sirve | **llave perdida para siempre, y los respaldos con ella** |
| **2-de-3** | **no sirve** | **se sobrevive** (cuesta un tercer sitio) |

El ejemplar físico son **75 bytes** (la llave) o 151 (una parte): cabe en un papel o un QR.

---

## Lectura

*Se puede tirar entera sin perder la medición.*

**Cifrar el respaldo es gratis; cifrar el valor dentro de la base es carísimo.** 0,68 s sobre 64 s de
volcado y +0,02 % de tamaño, contra 14× en lectura y la pérdida del índice — justo donde el proyecto
tiene apretado (`ESC-12` ≤5 s, `ESC-41` ≤10 s). **Cifrar en el volumen y dejar el dato en claro dentro
de la base sostiene las tres promesas a la vez**, por un 0,65 % de un núcleo. **Con una condición
dura: el nodo necesita AES-NI.** Sin él, esto cambia de orden de magnitud y hay que volver a medir.

**`CN-15` no aprieta por ningún lado, y eso libera la decisión.** Dos minutos contra un día son 714× de
margen: se puede elegir el camino más simple sin pagar nada. Lo que **no** queda demostrado es la otra
mitad de `CN-15`: **«pérdida cero» no se midió**, y con volcado nocturno se pierde hasta un día. Puede
que el dominio lo resuelva solo —`ADR-026` recaptura en vez de recuperar— pero **eso es una decisión**.

**El cifrado no es lo caro; la decisión cara es la del cliente.** En `B4` respondió **NO** a respaldos
cifrados, **NO** a llave por empresa y **NO** a registrar todo acceso técnico; el equipo decidió que
sí, y es una de las siete decisiones tomadas en su contra. **Ahora hay una cifra que poner sobre esa
mesa: cifrar cuesta 0,68 s y 117 KB.** El argumento ya no puede ser el costo — es una cláusula que
alguien tiene que firmar.

**Lo que se cae si `N4` se difiere.** El cifrado de volumen: **igual** (`ESC-50` pide 100 % pase lo que
pase). El respaldo cifrado con la llave de la empresa: **sigue haciendo falta** — el disco externo o la
nube del propio cliente también se pierden o se roban. La deduplicación: gana menos, porque ahorra
disco y el disco no era la restricción. La custodia `ADR-012`: **igual**, es del cliente y no de la
nube. **La única pieza que se cae entera es el Key Vault autoalojado:** sin un tercero que custodie, no
hay nada de quien aislar la llave.

**La custodia simple es la que contradice la promesa.** `ESC-50` pide 0 accesos al dato en operación
normal; con dos ejemplares enteros, **cualquiera de los dos sitios es un acceso completo**. **2-de-3 es
la única forma medida que cumple las dos cosas**, y cuesta un sobre más.

**Un desfase que conviene mirar:** la acumulación de cinco años pesó **5,3 GB**, no los 20–25 GB
estimados; la diferencia son los modelos de lectura de `ADR-010`/`ADR-023`, que aquí no se
construyeron. No cambia ninguna conclusión —sobra margen— pero **esa cifra y estos 5,3 GB miden cosas
distintas** y conviene no citarlas como si fueran la misma.

---

## Dependencias

*Asumido como contrato, nunca como implementación.*

- **`SPK-12`, pico de ingesta.** Se usó el techo de este banco (84.618 ev/s, 41 MB/s de WAL) y la
  cadencia diaria de `ADR-025`. **Si `SPK-12` mide un pico mayor, la cota escala lineal**: seguiría
  bajo el 7 % de un núcleo incluso a diez veces este pico.
- **`SPK-09`, lenguaje.** No se asumió ninguno. El contrato que este spike necesita es mínimo: *«una
  tarea programada que ejecuta un comando y lee su código de salida»*. Cualquier lenguaje lo cumple.
- **Motor de base de datos.** Se asumió PostgreSQL porque `ADR-024` lo propone y `SPK-10` está
  aparcado. Si cambia, cambian §1 y §2; **no cambian §4, §5 ni §6** — esos son del archivo, no del motor.
- **`ADR-009`:** la cola vive en la misma base, así que **el respaldo ya la cubre**.
- **`ADR-003`:** una base por empresa → una llave, un repositorio y una restauración por empresa.
- **Alcance de `N4`:** tratado como las dos hipótesis. **No se decidió.**

---

## Qué NO probé

1. **`[!]` LUKS de verdad.** Sin `dm_mod` ni `algif_skcipher` no se abrió ningún volumen cifrado. Lo
   de §1 es **cota superior de CPU**, no medición: no cubre la latencia de `dm-crypt`, sus colas ni la
   amplificación de escritura. **Hay que medirlo en el nodo real antes de escribir la ADR.**
2. **`[!]` «Pérdida cero» de `CN-15`.** No se montó archivado continuo de WAL ni se hizo recuperación a
   un punto en el tiempo. Solo está demostrado «restauración en 1 día o menos».
3. **`[!]` Vault / OpenBao.** La salida a internet los bloqueó (403): la comparación «Key Vault
   autoalojado frente a custodia simple» quedó medida **solo del lado simple**.
4. **pgBackRest y Barman.** No empaquetados aquí y no descargables. Solo se midió `restic`; `borg`
   quedó instalado sin comparar.
5. **Respaldo *durante* el pico de ingesta.** Todo se midió con la base en reposo.
6. **El nodo real de la finca** (2 núcleos aquí) y **el enlace real**: la tabla de envío a `N4` es
   aritmética sobre tamaños medidos, no una red probada.
7. **TDE por tablespace** (PostgreSQL comunitario no lo trae) ni `fscrypt`/`gocryptfs`.
8. **`[!]` El aviso de `ADR-012`.** **No se inventó ningún mecanismo**, como pedía la pregunta. Sigue
   siendo hueco: cada uso de la copia de custodia debe avisarse al administrador y **ninguna decisión
   dice por qué canal ni con qué garantía**. Mientras siga así, el «100 % notificados» de `ESC-50`
   **no se puede demostrar** — no porque falle, sino porque no hay qué medir.
9. **Rotación de la llave.** Qué pasa con cinco años de respaldos cuando la llave cambia, y qué cuesta
   recifrarlos. Ni se planteó.
