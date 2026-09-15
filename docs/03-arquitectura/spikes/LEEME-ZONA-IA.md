# `spikes/` — ZONA DE REGISTROS DE IA

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 14-sep-2026, ampliado 15-sep-2026, consolidado 15-sep-2026
> Estado: **SIN REVISAR**
> Manda sobre esto: `Documentacion/Drivers-Arquitectonicos/ADR-PoC-Spikes.xlsx` ·
> `Documentacion/Drivers-Arquitectonicos/DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx`

## `[!]` Lo primero: los seis spikes medidos están consolidados

**`IA_SPIKES-resumen.xlsx`** (en esta carpeta) tiene los seis en una fila cada uno: qué se
preguntó, la cifra que decide, la consecuencia, el hueco y qué ADR toca. **Es el punto de entrada.**
Sus cinco hojas: `Leeme` · `Spikes` · `Abiertos` (rojo intenso = imprescindible) · `Para-ADR` ·
`Cifras`.

Los seis `IA_RESULTADO.md` y sus `IA_PREGUNTA.md` **están pendientes de erradicar a `_to_delete/`**
una vez Juan revise el `.xlsx`. Hasta entonces conviven, y **manda el `.xlsx`**.
El resumen de lectura vive en `_to_delete/resumenes-de-lectura/2026-09-15-resumen-6-spikes.md`.

**`labs/` no se toca.** La evidencia ejecutable de `spk-09`, `spk-12`, `spk-13`, `spk-14`, `spk-15`
y `spk-16` sigue en `OneDrive - UCO/FlorLogic/Claude outputs/labs/`, y el `.xlsx` no la sustituye.

## La regla

**Nada de esta carpeta opaca la información humana.** Lo que vive aquí son registros de pruebas
puntuales, escritos por IA, para que Juan decida. No son documentación de arquitectura, no son
requisitos y **no son ADR**: el ADR lo escribe Juan en el `.xlsx` de drivers, que es el libro maestro.

Tres marcas, para que no haya forma de confundirse:

1. **La carpeta.** Todo lo generado por IA para las pruebas de tecnología está bajo `spikes/`.
   Fuera de aquí, la IA no escribe documentación.
2. **El nombre del archivo.** Prefijo `IA_`. Se ve en el explorador sin abrir nada, y sobrevive a
   que alguien copie el archivo a otro sitio.
3. **La cabecera.** Las cuatro primeras líneas de cada archivo dicen qué es, quién lo generó,
   su estado de revisión y qué documento manda por encima.

**Ningún documento humano cita un archivo de esta carpeta mientras esté `SIN REVISAR`.**

## Estados de revisión

| Estado | Qué significa |
|---|---|
| `SIN REVISAR` | Lo escribió la IA y nadie lo ha mirado. **No se cita, no se usa para decidir.** |
| `REVISADO POR JUAN <fecha>` | Juan lo leyó. Sirve de insumo para un ADR. Sigue sin ser fuente. |
| `APLAZADO <fecha> · <motivo>` | Fuera de la ronda actual. Se conserva intacto para retomarlo. |
| `DESCARTADO <fecha> · <motivo>` | Se probó y no sirve. Se conserva para no repetir el camino. |

## Cómo se revisa

Cada `IA_RESULTADO.md` está partido en bloques que se leen por separado:

- **Medición** — lo que salió de correr algo, con el comando exacto para repetirlo y dónde quedaron
  los datos. Es lo verificable.
- **Lectura** — lo que la IA interpretó. **Se puede tirar entera sin perder la medición.**
- **Dependencias** — lo que se asumió de otro componente, siempre como contrato.
- **Qué NO probé** — el hueco de una medición importa tanto como su resultado.

**Límite de tamaño:** si un registro pasa de dos páginas, se parte — la evidencia larga
(salidas completas, tablas, capturas) va a `labs/`, nunca a este repo.

## Los seis medidos — 14 y 15 de sep de 2026

| Spike | Tecnología | Estado |
|---|---|---|
| `SPK-09` | **Motor de reglas** — se exploró sin fijar lenguaje | MEDIDO · sin ganador a propósito · el formato ya se decidió (JSON, 15-sep) |
| `SPK-12` | **Ingreso y sincronización** | **`APLAZADO 15-sep-2026`** · medido, no decidible · ver `Aparcados` |
| `SPK-13` | **Cifrado y respaldo**, como conjunto | MEDIDO · SIN REVISAR · listo para ADR salvo `AB-03` (LUKS real) |
| `SPK-14` | **Paquete de configuración** versionado y su distribución | MEDIDO · SIN REVISAR · listo para ADR salvo `AB-01` y `AB-04` |
| `SPK-15` | **Empaquetado, instalación y actualización** del nodo | MEDIDO · SIN REVISAR · listo para ADR salvo `AM-01` |
| `SPK-16` | **Salida de datos** hacia el BI del cliente | MEDIDO · SIN REVISAR · listo para ADR salvo `AB-02` |

`[!]` **Ninguno fijó el lenguaje del backend, y eso se sostuvo:** la diferencia entre lenguajes
quedó entre el 3 % y el 36 % de la instalación, y por debajo del 4 % en todo lo demás.

## `[!]` Los cuatro abiertos imprescindibles — hoja `Abiertos`, en rojo

No son «más spikes»: son los que dejan una conclusión sostenida en el aire o un fallo que
revienta solo. **Lo primordial de aquí en adelante no es sacar más spikes.**

| | Qué falta | Por qué no se salta |
|---|---|---|
| `AB-01` | **Validador del paquete**: forma canónica del JSON + tipo de regla desconocido | Sin forma canónica el veredicto sellado en cada anotación no se reproduce entre lenguajes, y ahí muere la auditabilidad a 5 años de `ADR-022`. Y una regla puede viajar al dispositivo y no aplicarse nunca sin que nada lo diga. ~1 día |
| `AB-02` | **Prueba automática del cero de `CT-05`** sobre el modelo derivado | La RLS no protege las vistas materializadas: 239.200 filas de otra empresa a la vista. El arreglo está probado a mano 12 veces; la prueba que lo comprueba en cada cambio no existe, y el fallo no da error |
| `AB-03` | **LUKS de verdad en el nodo real** | El 0,65 % de un núcleo es cota de CPU, no medición. Sostiene toda la ADR de cifrado y depende de AES-NI. **La única que no se puede hacer desde la nube** |
| `AB-04` | **Rotación de llaves** contra los 5 años de `ADR-022` | Estalla solo: el día que se rote la llave de publicación, la reproducibilidad medida deja de valer. Recifrar 5 años de respaldos no tiene costo conocido |

Cinco más en **ámbar** (conviene, no rompe nada hoy) y **25 decisiones** que ya no se miden sino
que se deciden, en las hojas `Abiertos` y `Para-ADR`.

## En curso — abierto el 15-sep-2026

| Chat | Spike | Tecnología | Estado |
|---|---|---|---|
| **7** | `SPK-17` | **Java y C#** — los dos únicos lenguajes candidatos sin ninguna medición | **PREGUNTA LISTA · sin medir** |

`[!]` **Por qué existe:** de los cinco candidatos, tres están medidos (Node, Python, Go) y estos dos
no tienen ni una cifra, porque el entorno no tenía cadena de herramientas. Hoy se descartarían **por
ausencia de datos, que es la peor razón posible.** El spike cierra ese hueco y **no elige ganador**.
Las ocho mediciones están en su `IA_PREGUNTA.md`, y la primera es una compuerta: si la red vuelve a
bloquear el toolchain, se dice y no se estima.

**Después de este, la campaña de tecnología no necesita más spikes:** necesita los cuatro abiertos
en rojo y las decisiones de la hoja `Para-ADR`.

## Aparcados

| Spike | Tecnología | Por qué |
|---|---|---|
| `SPK-10` | Motor de base de datos | Es un contenedor aparte, y depende de que Juan cierre si `ADR-010`, `ADR-023` y `ADR-034` son tres mecanismos o uno |
| `SPK-11` | Lectura del heredado en Access | Fuera del alcance «solo backend». Se retoma con una copia del archivo del cliente |
| `SPK-12` | Ingreso y sincronización | **Medido, no decidible.** La clave de hecho de `ADR-027` incluye «la jornada», y `D1` no está respondida: sin eso no hay clave de hecho. Se suman `BR-N4`, `ADR-024` todavía en `Propuesta`, y `CT-03`, que no existe. **La medición se conserva entera**; el punto de reentrada es el `§0` de su `IA_RESULTADO.md` |

Se conservan intactos, con su cabecera en `APLAZADO`. `SPK-10` y `SPK-11` nunca se
midieron; **`SPK-12` sí**, así que su laboratorio en `labs/spk-12/` sigue siendo ejecutable
y sus cuatro resultados no se vuelven a medir — están listados en el `§0` de su registro.

## Archivos de coordinación

`IA_SPIKES-resumen.xlsx` (el punto de entrada) · `IA_CONTEXTO-3-BACKEND.md` (lo que lee cada chat
antes de nada) · `IA_BITACORA.md` (append-only).
`IA_CONTEXTO-TECNOLOGIA.md` es el contexto general anterior a esta ronda y **está pendiente de
erradicar**: el contexto 3 lo dejó atrás.
