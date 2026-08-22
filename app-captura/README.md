# FlorLogic — demo de captura

Prototipo **desechable** de la aplicación de captura en campo.
Contexto, estrategia y plan completo: `../PLAN_DEMO_CAPTURA.md`.

Esto **no** es el producto. Se construye para botarlo. Lo que sobrevive a la sesión con el cliente
es el modelo de datos, el catálogo de reglas, el contrato de sincronización y —sobre todo— **los
números que la demo mida en la mesa**.

---

## Ponerla a andar

```bash
python servir.py
```

Y ya. `dist/` viene construido en el repositorio, así que no hay nada que instalar: ni `npm
install`, ni dependencias, ni esperar. El servidor imprime dos direcciones:

```
  En este equipo   http://localhost:8787
  En el celular    http://192.168.x.x:8787   (misma red wifi)
```

El servidor atiende con un hilo por petición: la aplicación se sirve como módulos ES sin
empaquetar, así que el navegador abre del orden de quince conexiones a la vez al cargar, y un
servidor de un solo hilo devuelve `ERR_CONNECTION_REFUSED` a las que se salen de la cola.
Si el puerto está ocupado prueba los nueve siguientes en vez de fallar. El puerto por defecto es
**8787 y no 8000 a propósito**: 8000 lo usa medio mundo, y para el navegador **cada puerto es un
sitio distinto**, con sus propios service workers, cachés y base de datos. Compartir puerto con otro
proyecto mezcla las dos cosas.

La segunda es la que se abre en el teléfono del supervisor. En Android conviene «Agregar a
pantalla de inicio»: así arranca a pantalla completa y **sigue funcionando sin conexión**.

> El escaneo de la marca de la cama solo funciona en `localhost` o sobre HTTPS — es un límite de
> los navegadores, no de la demo. Con la dirección de red local todo lo demás funciona igual.

### Si un sitio «sigue vivo» después de apagar su servidor

Pasa, y no es cosa del puerto: un **service worker** registrado por otro proyecto en ese mismo
puerto sigue respondiendo aunque su servidor ya no exista —y sobrevive a desinstalar el proyecto y
a reiniciar el computador, porque vive dentro del navegador—.

1. Comprobá si de verdad hay algo escuchando (PowerShell):
   `Get-NetTCPConnection -LocalPort 8000 -State Listen | Select-Object OwningProcess`
   y `Get-Process -Id <PID>`. Si no sale nada, no hay ningún servidor: es el navegador.
2. Abrí **`http://localhost:PUERTO/limpiar.html`** y borrá los service workers y cachés de ese
   origen. La página dice cuántos encontró.
3. O desde el navegador: F12 → Application → Storage → *Clear site data*, y
   `chrome://serviceworker-internals` para ver todos los registrados.
4. Lo más rápido de todo: usar otro puerto. `python servir.py --puerto 9500`.

### Compartirla por internet

```bash
python compartir.py
```

Abre un **túnel de Cloudflare** y devuelve una dirección `https://algo.trycloudflare.com` que
funciona desde cualquier parte, sin cuenta de Cloudflare y sin tocar el router. Necesita
`cloudflared` instalado una vez: `winget install --id Cloudflare.cloudflared` en Windows,
`brew install cloudflared` en macOS.

Dos cosas que gana la demo al salir por el túnel:

- **HTTPS de verdad.** El navegador solo entrega la cámara en `localhost` o sobre HTTPS, así que
  **el escaneo de la marca de la cama recién funciona por acá** — con la IP de la red local no.
  Lo mismo para instalarla como aplicación en el celular.
- Se puede enseñar a distancia, con el cliente en otra ciudad.

> **La dirección es pública**: cualquiera con el enlace entra, y lo que hay del otro lado son datos
> reales de la finca. Por eso `compartir.py` **pone clave por defecto** (usuario `florlogic`, clave
> generada al arrancar) y la imprime en pantalla. `--sin-clave` la quita si de verdad hace falta,
> y `--clave loquesea` la fija. El túnel muere al cerrar la ventana y la dirección deja de existir.

`servir.py --clave loquesea` también acepta clave, por si querés protegerla en la red local.

### Cuando se toque algo de `src/`

```bash
python construir.py       # compila a dist/
python construir.py --servir
```

Necesita `tsc` una sola vez: `npm i -g typescript`. **No hay `package.json` a propósito**: en una
finca con mala conexión, `npm install` es exactamente el paso que falla.

### Si cambia la plantilla del cliente

```bash
python construir.py --semilla
```

Vuelve a leer `../Documentacion/Levantamiento de requisitos/PLANTILLAS DOCUMENTOS DE EMPRESA/`
y regenera el catálogo antes de compilar. Necesita `pip install openpyxl`.

---

## Qué se puede enseñar en la sesión

| Pantalla | Qué demuestra |
|---|---|
| **Bloques → Camas → Cama** | El catálogo real de Buenavista, con las camas divididas marcadas |
| **Capturar · rejilla** | La estructura del formato de papel, tal como está hoy |
| **Capturar · guiada** | Una sección a la vez, botones grandes, escaneo de la marca |
| **Confirmá antes de guardar** | Lo capturado completo antes de darlo por bueno, con los avisos |
| **Papel contra app** | Lo que dice el formato al lado de lo que quedó capturado |
| **Bandeja de salida** | Sin señal no se pierde nada · reintentar no duplica · el choque lo decide una persona |
| **Medidas** | Segundos y toques por cama, **rejilla contra guiada** |
| **Datos** | De dónde salió cada dato y qué encontró la regla en el histórico real |

**El recorrido que vale la pena ensayar antes:** cama 37 del bloque 12 (la dividida) → capturar en
rejilla → cerrar → bandeja → sincronizar → volver a capturar la misma cama en guiada → sincronizar
→ resolver el conflicto → medidas.

---

## Lo que hay que sacar de la sesión

1. **El CSV de medidas.** Pantalla Medidas → *Descargar medidas*. Ahí están los segundos y los
   toques por cama, separados por variante. Eso es la **medida de respuesta** que hoy bloquea todos
   los escenarios `ESC-nnn`, y no se saca de una encuesta.
2. **Cuál de las dos variantes ganó.** La contradicción 2 del plan —rejilla tipo papel contra
   captura de una sola mano— se resuelve con cronómetro, no discutiendo.
3. **La lista de sorpresas.** Todo lo que el cliente hizo o dijo que el prototipo no previó.

---

## Lo que la demo ya sabe de los datos reales

| | |
|---|---|
| Bloques / camas / registros | 4 · 38 · 41 |
| Camas divididas | 3 (bloque 12: camas 37, 39 y 40) — **confirma `DEC-14`** |
| Filas que la regla marca | **9** |
| Columnas de significado desconocido | `OBSE` (325 / 425) |

Las nueve son ocho donde `cantidad ÷ #líneas` no da la razón de la variedad, más `Cortona` /
`Cartona`, dos nombres a una letra de distancia. **Ninguna es un error nuestro: son del formato del
cliente.** Esa lista es material de sesión.

La tolerancia está en 2% y vive en `configuracion/reglas.v1.json`. Por eso Scarlett
`1860 ÷ 126 = 14,76` pasa y Pink `3225 ÷ 231 = 13,96` no. **Ese umbral es una decisión nuestra, no
un hallazgo** — hay que preguntárselo al cliente.

`Astroi` y `Rose` quedan marcadas **`en-disputa`**: tienen tres razones distintas cada una y el
generador no elige por su cuenta. Astroi llega a 11,19 · 12,46 · 19,0 el mismo día.

---

## Decisiones y de dónde salen

Todas vienen de las respuestas de caracterización de Juan (`Mini QAW FlorLogic.xlsx`, hoja
`3. Caracterización`), que hoy son **hipótesis del arquitecto, no requisitos**.

| En la demo | Respuesta que la origina |
|---|---|
| Rejilla con la estructura del papel | `CNF` plantilla como el papel **SÍ** · guiada pantalla por pantalla **NO** |
| Las dos variantes conviviendo | `UXP` una mano, guantes, sol · `RND` menos de 30 s por cama |
| Blanco de toque de 56 px, contraste alto | `UXP` con guantes, bajo el sol directo |
| Todo estado con símbolo **y** palabra | `ACC` nunca depender solo del color |
| Reglas en un archivo, no en el código | `CNF` cambiar rangos sin versión nueva (`CN-22`, `CN-26`) |
| Rechazo que dice **qué hacer** | `UXP` los mensajes explican qué hacer, no solo qué pasó |
| Confirmación final antes de guardar | `CNF` **SÍ** |
| Guardado inmediato, sin botón de guardar | `CNF` cada dato guardado de inmediato |
| Sugerencia que no se escribe sola | `CN-31` nunca escritura silenciosa |
| El conflicto lo decide una persona | `DEC-05` y `CNF` **NO** a que gane la más reciente |
| Catálogo versionado antes de capturar | `CN-26` |
| Identificadores UUID v7 del dispositivo | `CN-24` sincronización idempotente y cronológica |

**Tensión que la demo lleva a la mesa a propósito:** Juan respondió **NO** a «advertir sin bloquear
cuando un dato se aleja de lo histórico», pero la regla de la razón (`RG-06`) no puede ser otra cosa
que un aviso mientras no sepamos si Astroi es un error de digitación o una densidad distinta.

---

## Pruebas

```bash
python pruebas/todas.py
```

| | Qué comprueba | Necesita |
|---|---|---|
| `catalogo.prueba.ts` | Lo que muestra la app coincide con el formato de papel | Node 22 |
| `reglas.prueba.ts` | Duras bloquean, blandas avisan, y salen del archivo | Node 22 |
| `servidor.py` | Aguanta que el navegador pida los ~15 módulos a la vez | Python |
| `compartir.py` | El túnel y la clave, con un `cloudflared` de mentiras. Y que **el service worker se instale igual detrás de la clave** | Python (+ playwright) |
| `correr_persistencia.py` | Lo capturado sobrevive a **recargar la página** | playwright + tsc |
| `recorrido.py` | La sesión completa de punta a punta | playwright + tsc |

El recorrido hace lo que va a hacer el supervisor: captura la cama dividida, la cierra contra las
reglas, sincroniza, provoca un conflicto, lo resuelve, mira las medidas, recarga y **corre la
aplicación con la red apagada**. Falla si aparece un error de consola o si alguna pantalla se sale
de ancho en un celular de 412 px.

`python pruebas/recorrido.py --fotos` deja capturas de pantalla en `pruebas/fotos/`.

---

## Estructura

```
construir.py                     todo el build. tsc + estáticos + service worker
servir.py                        servidor local; salta de puerto si está ocupado
compartir.py                     túnel de Cloudflare con clave, para enseñarla a distancia
dist/limpiar.html                borra service workers y cachés de este origen
configuracion/reglas.v1.json     las reglas de validación — datos, no código
scripts/generar_seed.py          plantilla .xlsx  ->  public/seed.json
dist/                            lo construido; se versiona a propósito

src/tipos.ts                     forma de la semilla y del catálogo de reglas
src/modelo.ts                    Captura, LineaCaptura, ItemOutbox, Evento, Conflicto
src/almacen.ts                   IndexedDB a mano, sin dependencias
src/id.ts                        UUID v7 e identidad del dispositivo
src/repositorio.ts               catálogo versionado + capturas + bandeja + eventos
src/reglas.ts                    motor que interpreta reglas.v1.json
src/sincronizacion.ts            envío idempotente, reintento y mediación del conflicto
src/metricas.ts                  medidas de respuesta y exportación a CSV
src/escaner.ts                   lectura de la marca física de la cama
src/vista.ts                     capa de vista mínima, sin framework
src/ui/                          las pantallas
```

### Por qué no hay React, ni Vite, ni Dexie

Los tres se descartaron por la misma razón, y no es el peso: cada dependencia es un `npm install`
que puede fallar donde la demo tiene que funcionar. Sin ellas el proyecto se compila con `tsc` y se
sirve como archivos estáticos, y `dist/` puede venir listo en el repositorio.

- **React → `src/vista.ts`**, unas 150 líneas. Cada pantalla arma su DOM y las partes que cambian se
  vuelven a pintar a mano; ningún campo pierde el foco mientras se escribe.
- **Vite → `construir.py`.** Compila, copia y genera el service worker con el precaché.
- **Dexie → `src/almacen.ts`.** El prototipo consulta por clave y por un índice, nada más. Todo el
  acceso a disco pasa por ahí: volver a Dexie toca un solo archivo.

Cuando el producto deje de poder ser una PWA —ver `PLAN_DEMO_CAPTURA.md` §4.4— lo que se lleva a
Flutter es `modelo.ts`, `reglas.v1.json` y el contrato de `sincronizacion.ts`. El resto se bota,
que era el plan desde el principio.
