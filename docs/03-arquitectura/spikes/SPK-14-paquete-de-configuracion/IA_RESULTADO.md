# `SPK-14` · Paquete de configuración versionado y su distribución — resultado

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 15-sep-2026
> Estado: **SIN REVISAR** · Chat 4
> Manda sobre esto: `ADR-029`, `ADR-015`, `ADR-006`, `ADR-024`, `ADR-027` · `CT-01`, `CT-04` · `ESC-22`

Código y datos: `labs/spk-14/`. Se repite entero con `bash correr.sh` (~2 min) más
`python3 bin/11_sensibilidad.py datos/sens` (~4 min). Formato del paquete: `tar` + `zstd -19`,
manifiesto con `sha256` por archivo y firma `ed25519` desprendida. **No hay nada en este formato
que ate un lenguaje**; la compuerta y el veredicto se corrieron en Python y en Node con la misma
firma y los mismos bytes.

---

## Medición

Finca de porte medio: 2.400 camas, 9.600 secciones, 121 reglas, 991 KB de contenido sin comprimir.
**12 usuarios, 3 dispositivos** (36 pares persona-aparato). Tres formas del paquete:

`A` partido (un común firmado + credenciales aparte) · `B` personalizado (36 paquetes completos) ·
`C` partido + **lista de revocación firmada**.

### 1 · Tamaño, artefactos y bajada — `salidas/01_formas.json`, `07_red.json`

| | artefactos | publicado | **baja cada aparato** | peticiones | construir |
|---|---:|---:|---:|---:|---:|
| `A` partido | 37 | 77,1 KB | **66,3 KB** | 13 | 0,40 s |
| `C` partido + lista | 38 | 77,5 KB | **66,5 KB** | 14 | 0,39 s |
| `B` personalizado | 36 | 2,18 MB | **728,5 KB** | 12 | **14,9 s** |

Medido sirviendo por HTTP en la misma máquina: 11–12 ms en las tres, o sea **el protocolo no
distingue**. Modelado sobre los bytes (aritmética, **no medición**): a 10 Mbps, `A` 0,05 s y
`B` 0,58 s. **Ninguna de las dos es un problema de red.**

### 2 · La compuerta de `ADR-015` — `salidas/02_compuerta.json`, `05_integridad.txt`

Reloj de pared del proceso entero, arranque del intérprete incluido, mejor de 7 vueltas:

| forma | Python | Node | sin parsear el catálogo |
|---|---:|---:|---|
| `A` / `C` | 85 / 83 ms | 78 / 77 ms | 69–71 ms |
| `B` | 271 ms | 263 ms | 150–168 ms |

Desglose de una verificación: descomprimir **5,8 ms** · verificar firma `ed25519` **0,10 ms** ·
`sha256` de todo el contenido **0,97 ms** · parsear el JSON **9,3 ms**. **La firma no cuesta nada;
lo caro es leer el catálogo.**

**Seis ataques, seis rebotes** (código de salida 3, `NO DEJA CAPTURAR`): un byte cambiado dentro
del catálogo · manifiesto recalculado a mano con la firma vieja · credencial con un rol añadido sin
refirmar · paquete ausente · paquete de la versión anterior · archivo ilegible.

### 3 · Reproducibilidad — `salidas/03_reproducibilidad-py.json` y `-node.json`

Anotación de marzo, sellada con la versión 1 del paquete y su veredicto. Recuperado el paquete v1
de seis publicados, **el veredicto se reproduce** (huella `36dea84eb83198c1`, igual en Python y en
Node). Las versiones 2 a 6 dan **otro** veredicto (`1fdc4a79dea175cb`): sin el sello no hay forma de
volver al original. El versionado sirve exactamente para esto.

### 4 · Retirar el permiso a una persona (`ESC-22`) — `salidas/04_revocacion.json`

| | rehacer | **baja cada aparato** | tiempo | ¿verificable sin conexión? |
|---|---:|---:|---:|---|
| `A` (credencial fuera del número de versión) | 33 arch., 14,9 KB | **0 B** | 0,52 s | **No** — es la *ausencia* de un archivo |
| `C` (+ lista firmada) | 1 arch., **232 B** | **232 B** | 0,015 s | **Sí** — es una afirmación firmada |
| `B` estricto (un solo número de versión) | 33 paq., 1,99 MB | **665 KB** | **13,9 s** | Sí |
| `B` laxo (solo los del revocado) | 0 | 0 B | 0 s | No, y **las versiones se desparejan** |

### 5 · Completa contra incremental — `salidas/06_incremental.json`

`zstd --patch-from` sobre el `.tar`, reconstruyendo **byte a byte**: cambiar **una regla** = parche
de **257 B** (99,6 % menos); **crecer el catálogo** en 30 camas = **1.050 B** (98,3 % menos).

### 6 · ¿Depende del tamaño de la finca? — `salidas/08_sensibilidad.json`

| finca | camas | `A` baja | `B` baja | **veces** | compuerta `A` | compuerta `B` |
|---|---:|---:|---:|---:|---:|---:|
| chica | 360 | 25,0 KB | 239,8 KB | 9,6× | 87 ms | 202 ms |
| media | 2.400 | 66,3 KB | 728,5 KB | 11,0× | 103 ms | 381 ms |
| grande | 9.600 | 191,2 KB | 2,24 MB | 11,7× | 162 ms | **1.099 ms** |

Construir la forma `B` en la finca grande: **97 s**. La proporción no se mueve: es estructural.

---

## Lectura

*(Se puede tirar entera sin perder nada de lo de arriba.)*

**El tamaño no es el eje que decide.** El paquete completo de una finca media pesa 66 KB y baja en
centésimas de segundo. Ni la forma cara llega a un segundo. Quien elija por tamaño estará eligiendo
por una cifra que no aprieta.

**El eje que decide es qué pasa al retirarle el permiso a alguien** — 232 B contra 665 KB, 15 ms
contra 14 s, y en la finca grande 97 s de regeneración por una sola persona. Y no es solo costo:
la forma personalizada obliga a elegir entre rehacer los 36 paquetes o dejar que los aparatos
queden en versiones distintas del mismo paquete, que es justo lo que `ADR-029` quería evitar con su
número único. **Personalizar el paquete pone el «un solo número de versión» y la revocación en
contra el uno del otro.**

**Partir el paquete no rompe `ADR-029` si se dice una cosa:** que la credencial **no es parte del
paquete versionado**, sino un artefacto propio con su ciclo de vida. Así el número único sigue
describiendo catálogo + reglas + parámetros —que es lo que `CT-04` necesita congelar y lo que
`ADR-024`/`ADR-027` sellan en cada anotación— y las personas entran y salen sin tocarlo.

**`A` a secas tiene un hueco, y es el que `ESC-22` señala.** Si el aparato se entera de una
revocación porque *le falta* un archivo, un aparato desconectado no puede distinguir «te
revocaron» de «todavía no he sincronizado», y la credencial vieja sigue firmada y válida hasta su
fecha. Una lista de revocación firmada convierte esa ausencia en una afirmación verificable sin
conexión. **Cuesta 232 bytes.** Esa es la forma `C`, y es la única de las cuatro que cumple
`ESC-22` sin pelearse con `ADR-029`.

**La compuerta no va a ser la excusa para salir sin ella.** 85 ms en la forma barata. Lo caro no es
la criptografía —la firma son 0,10 ms— sino parsear el catálogo; si algún día aprieta, se puede
verificar sin cargarlo y diferir el parseo.

**El incremental todavía no se paga.** Ahorra 99 % de 60 KB: ahorra 60 KB. Vale la pena anotarlo
como camino conocido si el catálogo crece dos órdenes de magnitud, no como trabajo de ahora.

---

## Dependencias

Todo lo de otros componentes se tomó como **contrato**, nunca como implementación.

- **Motor de reglas (`SPK-09`, explorando sin elegir)** — solo se asume que el artefacto de reglas
  es **un archivo de datos versionable** (`ADR-006`) y que el veredicto es función determinista de
  (reglas, anotación). El evaluador de `bin/04_*` es un intérprete mínimo escrito para poder
  comparar un veredicto consigo mismo: **no es un motor y no insinúa ninguno.**
- **Ingreso y sincronización (`SPK-12`)** — se asume que existe una *primera sincronización* en la
  que el aparato puede recibir la lista de revocación (`ESC-22`), y que la anotación viaja con la
  versión del paquete con que se validó (`ADR-024`/`ADR-027`).
- **Empaquetado e instalación (`SPK-15`)** — se asume que la versión del paquete de configuración y
  la de la aplicación son **dos números independientes**, los dos ejes de `ADR-029`.
- **Proveedor de identidad (`ADR-007`, bloqueado)** — la credencial se modeló como afirmación
  firmada con ventana de validez. Si el proveedor emite otra cosa, cambia el tamaño del artefacto,
  no el orden de magnitud ni ninguna conclusión de arriba.
- **Base de datos (`SPK-10`, aparcado)** — ninguna. El paquete es un archivo, no una tabla.
- `[!]` **Forma canónica del JSON.** Python y Node dieron huellas **distintas** del mismo veredicto
  hasta fijar la serialización (claves ordenadas, sin espacios). El veredicto coincidía; la huella
  no. **La reproducibilidad de `ADR-024`/`ADR-027` depende de fijar también la forma canónica, no
  solo la versión del paquete.** Es un contrato que hoy no está escrito en ningún lado.

---

## Qué NO probé

1. **La compuerta sobre el aparato real.** Medida sobre la CPU del nodo, no sobre el navegador de
   un teléfono de campo, que es donde `ADR-008` mantiene la captura. Lo caro —parsear el catálogo—
   es justo lo que peor escala ahí. **Un número de 85 ms aquí no es un número de 85 ms allá.**
2. **Red real de la oficina.** Solo HTTP en la misma máquina más aritmética sobre los bytes.
3. **Rotación de la llave de publicación.** Es el hueco grande: `ADR-022` conserva todo 5 años, y no
   se probó qué pasa con un paquete de hace tres años firmado con una llave que ya se rotó. Sin eso,
   la reproducibilidad medida arriba tiene fecha de vencimiento.
4. **Custodia de la llave de publicación.** `ADR-012` habla de la llave de cifrado por empresa; no
   está dicho si es la misma llave ni quién la guarda.
5. **Qué hace el aparato cuando la compuerta dice que no** a las 5 a.m. y no hay quién publique.
   Se midió que detecta y avisa, no qué pasa después.
6. **Dos publicaciones el mismo día** con el mismo número de versión, y qué gana.
7. **Más de 12 usuarios / 3 dispositivos.** La sensibilidad movió el tamaño de la finca, no el
   número de personas; en la forma personalizada el costo crece con el producto de los dos.
8. **El contenido real del catálogo y de las reglas.** Es inventado con forma plausible: el
   contenido es del cliente (`D1`) y el proceso de captura nunca se trabajó.

---

`[!]` Durante la medición se encontraron y corrigieron **dos defectos del propio banco**, no del
diseño: la compuerta se caía con una excepción en vez de dar veredicto ante una firma inválida
(un lanzador lo habría leído como «error del script», no como «no deja capturar»), y las huellas de
los dos lenguajes no eran comparables por la serialización. Las cifras de arriba son posteriores a
las dos correcciones.
