# Contexto 3 · Nivel 3 — Backend de la finca

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 14-sep-2026
> Estado: **SIN REVISAR**
> Manda sobre esto: `Documentacion/Drivers-Arquitectonicos/ADR-PoC-Spikes.xlsx` (36 ADR) ·
> `Documentacion/Drivers-Arquitectonicos/DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx`

**Este es «el contexto 3».** Lo lee entero cada chat de esta ronda antes de hacer nada. Si algo de
aquí contradice al `.xlsx` de ADR o a los drivers, **manda el documento humano** y este archivo está
desactualizado: dilo en tu resultado.

---

## 1 · El sistema, en corto

FlorLogic proyecta producción de flor de corte a partir de captura en campo **sin conexión**. No
maneja dinero; la métrica es **tallos cortados**.

**Entrega local-first** (`ADR-001`): una instalación por empresa, en un nodo dentro de la finca. No
es SaaS. La nube es una capa aparte que **puede caerse una semana sin parar la finca**.

**El dolor que paga el cliente no es la velocidad de captura.** Es el traslado de papel a digital
(~4 h por tanda) y la proyección manual (hasta 8 días, por el ciclo de corrección). **Toda prioridad
y todo criterio de «esto vale la pena» se juzga contra esas dos cifras.**

Equipo: dos personas.

## 2 · Qué es el Backend de la finca

El contenedor que es **la autoridad de todo el dato operativo**: ingesta, reglas de servidor,
proyección, consulta, documentos y salida de datos. Corre en el nodo de la finca (`N2`), junto a la
base de datos de la empresa y detrás del API Gateway de la finca. Tiene 18 componentes.

A su lado, fuera de él: `API Gateway de la finca` · `Base de datos de la empresa` (PostgreSQL,
propuesta) · `Aplicación de captura` · `Aplicación web de consulta` · `Servicios en línea` (`N4`).

## 3 · Esta ronda: 3 chats, 3 tecnologías

| Chat | Spike | Tecnología | Carpeta |
|---|---|---|---|
| **1** | `SPK-09` | **Motor de reglas** — explorar lenguajes **sin fijar ninguno** | `SPK-09-motor-de-reglas/` |
| **2** | `SPK-12` | **Ingreso y sincronización** | `SPK-12-ingreso-y-sincronizacion/` |
| **3** | `SPK-13` | **Cifrado y respaldo**, como conjunto | `SPK-13-cifrado-y-respaldo/` |

### Las reglas para que los tres no se pisen

1. **Trabajas únicamente tu componente.** No opinas sobre los otros dos.
2. **Lo que necesites de otro componente lo tratas como contrato, no como implementación** — la forma
   de la API o del esquema, nunca una librería concreta de otro. Lo anotas en «Dependencias».
3. **`[!]` Ningún chat fija el lenguaje del backend.** `SPK-09` explora caminos **sin elegir**, así
   que `SPK-12` y `SPK-13` **no pueden asumir un lenguaje**: si una medición depende del lenguaje, se
   hace en dos lenguajes distintos o se declara como dependiente y se dice de qué.
4. **Ningún chat escribe ADR.** Entregas el insumo; el ADR lo redacta Juan en el `.xlsx`.

## 4 · Decidido, y no se reabre en esta ronda

| | |
|---|---|
| `ADR-001` | Local-first, una instalación por empresa. **No SaaS** |
| `ADR-024` | Cada campo capturado es un hecho; la cama es la unidad de captura. Relacional + `JSONB` **solo para el valor**. Sigue en `Propuesta` a propósito |
| `ADR-006` | Motor de reglas **guiado por datos**: reglas en artefacto versionable, no en código de aplicación |
| `ADR-029` | **Un solo paquete de configuración** por empresa —catálogo + reglas + credenciales— inmutable, con un único número de versión |
| `ADR-027` | Identidad técnica del dato (UUID del dispositivo, opaco) y **clave de hecho aparte** |
| `ADR-031` | **Tres tiempos** que no se mezclan: sello crudo, sello normalizado por el desvío, tiempo de sesión. El normalizado ordena `RF-022` |
| `ADR-028` | La sesión de sincronización es **del transporte**; la captura se identifica al llegar a la base de la finca |
| `ADR-035` | **La sesión de captura es la unidad de trazabilidad.** Cambia `ADR-024` |
| `ADR-009` | La cola de trabajos vive **dentro de la misma base**. Pieza aparte solo si la medición obliga |
| `ADR-016` | Un solo artefacto empaquetado; lo del entorno vive en configuración, nunca en código |
| `ADR-012` | Llave de cifrado **por empresa** + copia de custodia **fuera de línea**, en soporte físico, dos ejemplares en sitios separados |

## 5 · `[!]` Lo que NO se toca

- **La tecnología del dispositivo de captura.** `ADR-008` la **aplaza deliberadamente**: el prototipo
  y las mediciones **siguen sobre la aplicación web**, y solo se pasa a aplicación instalada si se
  dispara uno de tres disparadores escritos. Abrir esa discusión es gastar la ronda.
- **El modelo C4.** El `.drawio` de la carpeta está congelado: manda la versión en la nube de Juan,
  todavía no compartida. **No lo edites y no lo cites como verdad** (ver §6).
- **Los cuatro `.xlsx` de drivers y el libro de ADR.** La IA no los edita. Si algo hay que cambiar,
  se lista el cambio exacto —texto de hoy, texto propuesto, dónde— y lo aplica una persona.

## 6 · `[!]` El modelo C4 está desfasado: no lo uses como fuente

Auditoría del 14-sep contra el libro de ADR. **Si tu componente aparece aquí, el modelo miente:**

| Dónde | Dice | Manda |
|---|---|---|
| `Bitácora de auditoría` | «registra **por sesión de sincronización**» | **`ADR-035`**: la unidad es la **sesión de captura** |
| `Servicio de IA analítica` | en la nube, «ubicación sin decidir» | `ADR-030` Depreciada; **`ADR-036`** la pone ligera en la finca, y es **opcional** |
| Captura y nodo `N1` | «Android · Flutter o Kotlin nativo» | **`ADR-008`**: aplazado, sigue la aplicación web |
| `Servicios de escritura` | `[!]` «`ESC-06` y `RF-017` se contradicen» | **`ADR-032`** ya lo resolvió: manda la **magnitud** del cambio, no el rol |
| `Planificador de tareas` | «regeneración **semanal**» | **`ADR-005`**: cálculo **vivo con cada sincronización** + versión publicada congelada |

Y **no están representados** en el modelo: el desvío del reloj (`ADR-031`), la asignación (`ADR-026`),
las tablas de consulta derivadas (`ADR-010`), la copia de custodia fuera de línea (`ADR-012`), la
confirmación del segundo administrador (`ADR-032`), ni **el cierre de producción**, que no tiene ADR.

## 7 · Cómo cierras tu chat

1. Escribe `IA_RESULTADO.md` en la carpeta de tu spike, con esta estructura fija:
   - **Medición** — lo que salió de correr algo, **con el comando exacto para repetirlo** y dónde
     quedaron los datos. Es lo verificable.
   - **Lectura** — tu interpretación. Va aparte porque **se puede tirar entera sin perder la medición**.
   - **Dependencias** — lo que asumiste de otro componente, como contrato.
   - **Qué NO probé** — el hueco importa tanto como el resultado.
2. Añade **una línea** a `IA_BITACORA.md`. No edites lo anterior.
3. El código y los datos van a `labs/spk-nn/`, **fuera de este repo**. Si el resultado pasa de dos
   páginas, pártelo: la evidencia larga no entra aquí.
4. **No escribas ADR.**
