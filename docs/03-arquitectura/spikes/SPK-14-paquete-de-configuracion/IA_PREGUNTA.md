# `SPK-14` · Paquete de configuración versionado y su distribución

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 15-sep-2026
> Estado: **SIN REVISAR** · Chat 4
> Manda sobre esto: `ADR-029`, `ADR-015`, `ADR-006`, `ADR-024`, `ADR-027` · `CT-01`, `CT-04` · `ESC-22`

## Qué se explora

Cómo se construye, se firma, se versiona y se distribuye **el paquete único de configuración de la
empresa** —catálogo, reglas y credenciales— y cómo se verifica antes de dejar capturar.

Es **el eje «dato»** de `ADR-029`, que fijó *dos ejes y no tres*: configuración (dato) y aplicación
(código). El eje «código» es `SPK-15`.

## Lo que manda

- **`ADR-029`** (Aceptada) — catálogo, reglas y parámetros se publican **juntos, como un solo paquete
  inmutable, con un único número de versión**, distribuido como un solo archivo.
- **`CT-01`** — entrega del paquete versionado al dispositivo. **`CT-04`** — el snapshot inmutable de
  parámetros que alimenta la proyección.
- **`ADR-015`** (Aceptada) — las reglas y el catálogo **se descargan y se verifican antes de dejar
  capturar**: si falta algo **se avisa antes de salir al cultivo, nunca en medio del campo**.
- **`ADR-006`** — cambiar una regla **no puede exigir publicar una versión nueva de la aplicación**.
  Es requisito escrito del cliente.
- **`ADR-024`/`ADR-027`** — cada anotación guarda **con qué versión del catálogo se validó**. De ahí
  sale la reproducibilidad.
- **`ESC-22`** — un permiso retirado surte efecto **en la primera sincronización** del desconectado.

## `[!]` El nudo, escrito en el propio `ADR-029`

**La credencial es por persona y por aparato**, así que el ADR dejó abierto **si el paquete se parte
o va personalizado**. Ese es el centro de este spike: medir las dos formas, no elegir.

## Qué se mide

1. **Tamaño y tiempo de bajada** del paquete completo y de una actualización incremental, sobre la
   red de la oficina (en el cultivo no hay red, `CN-17`).
2. **La compuerta de `ADR-015`.** Detectar paquete faltante, corrupto o desactualizado **antes** de la
   jornada, y avisar. Mide cuánto tarda esa verificación: si es lenta, la gente sale sin ella.
3. **Reproducibilidad.** Tomar una anotación de hace meses, recuperar la versión del paquete con que
   se validó y **reproducir el mismo veredicto**. Es la prueba de que el versionado sirve para algo.
4. **Partido frente a personalizado.** Para una finca real —del orden de 12 usuarios y 3 dispositivos
   de captura— medir número de artefactos a generar, tamaño total, y **qué pasa al revocar a una
   persona**: ¿se regenera todo el paquete?
5. **Integridad.** Firma y verificación: que un paquete alterado se rechace, y a qué costo.

## Qué NO se decide aquí

El contenido del catálogo ni de las reglas (es del cliente, `D1`, y el proceso de captura nunca se
trabajó) · el motor de reglas (`SPK-09`, que está explorando sin elegir) · el proveedor de identidad
(`ADR-007`, bloqueado hasta fijar la ventana de la credencial offline) · el lenguaje del backend.
