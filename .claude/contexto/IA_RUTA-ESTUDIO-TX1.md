REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 29-oct-2026, a pedido de Juan («separar en archivos la parte del funcionamiento individual; usar la forma de estudio de ucotestapi, mezclarla con la especificación de TX-01 y el checklist de IS II; tramos más cortos, que yo escriba el código más importante»)
Estado de revisión: SIN REVISAR
Manda por encima: la palabra de Juan · `codigo/backend-finca/espec/ESPEC-dominio-ingesta.md` · `IA_GUIA-IS2-checklist.md` · método de `~/Work/orient/Orient/README.md` («Cómo explica el agente», «Prácticas de reconstrucción»)

# Ruta de estudio de TX-1 · el hexágono de `ingesta`, tramo por tramo

## 0 · Cómo funciona esta ruta

Es la forma de estudio de ucotestapi (PRA-1) aplicada a TX-1: **tú escribes el código más importante**, en tramos
cortos que caben en una respuesta, y en cada tramo explicas lo que hiciste antes de abrir el siguiente.

1. **Un tramo a la vez, en orden fijo.** No se abre el siguiente hasta cerrar el anterior (prueba en verde y tus
   respuestas).
2. **Mapa antes de código.** Cada tramo dice dónde está en el recorrido del dato.
3. **Primero el estado:** qué está bien (no tocar), qué falla y por qué.
4. **Un concepto por bloque:** qué es → ejemplo completo de otra cosa con la misma forma → qué cambia en tu caso →
   tareas numeradas, cada una con su comprobación.
5. **Nunca la respuesta.** El agente muestra el patrón con otro tema, contesta conceptos y lee tus errores contigo; no
   escribe la línea que falta. Lo mecánico (mover un método tal cual, un `import`) lo puedes pedir.
6. **Entendimiento constante:** cada tramo tiene 2 o 3 preguntas cortas; se responden al cerrarlo.
7. **Las pruebas son el examen:** no se tocan. Si una no compila, se lee solo el mensaje del compilador.
8. **Para la presentación:** cada tramo dice qué ítem de la guía de IS II deja demostrado y qué mostrarías.

El código se escribe en la copia visible `~/Work/florlogic-codigo` (Eclipse: proyecto `backend-finca`), sin commit
(los commits los haces tú).

## 1 · El recorrido del dato (mapa de toda la ruta)

```
celular ──HTTP──▶ gateway (WAF) ──▶ ParticionesControlador ──▶ [lector de la partición] ──▶ ensamblador de entrada
                                    infraestructura/input       (tramo 1)                    servicios/input/adapter
                                                                                                     │
                                                                                                     ▼
PostgreSQL ◀── adaptador JDBC ◀── ensamblador de salida ◀── puerto de salida ◀── caso de uso ──▶ dominio
               infraestructura/     servicios/output/         servicios/output/   servicios/       (valores,
               output/adapter       adapter                   port                casosuso         avance)
```

**Todas las flechas de red van por HTTPS** (Juan, 29-oct: «todo debe funcionar con https»): hoy van en claro y se
corrige en un segmento propio del esqueleto (1c). El tramo 1 no cambia: el controlador sigue recibiendo HTTP, ya
descifrado por el gateway.

La regla que no se rompe: las flechas de dependencia apuntan **hacia adentro** (infraestructura → servicios →
dominio). El dominio no conoce a nadie. Cada adaptador ignora a los demás.

## 2 · Los tramos

| # | Tramo | Lo que escribes tú | Prueba que lo cierra | Guía IS II (qué demuestra) | Estado |
|---|---|---|---|---|---|
| 1 | **Separar el controlador**: que solo adapte HTTP ↔ puerto (OBS-1-8) | El resultado sellado de la lectura y el método que lee; el `switch` del controlador | `CanalParticionesTest` (5) y `CanalPorGatewayTest` (25) | Clean Code y SOLID (responsabilidad única); Clean Architecture (adaptador que solo traduce); OWASP (validar la entrada en la frontera) | **abierto** |
| 2 | **Valores del dominio**: `ConsecutivoValor` (constructor frente a `de`) | El método `de` y `siguienteDe`, tras borrarlos | pruebas unitarias de `ConsecutivoValor` | DDD: objeto de valor; fronteras que no lanzan | pendiente |
| 3 | **El agregado**: `AvanceDelDispositivoAgregado.registrar` | `registrar` (el avance con huecos) | pruebas unitarias del agregado; el flujo 1, 2, 4, 3 a mano | DDD: raíz de agregado e invariante | pendiente |
| 4 | **El puerto de salida del avance** | La interfaz del puerto y su DTO | compila; prueba de contrato del puerto con un doble en memoria | Hexagonal: puerto de salida (inversión de dependencias) | pendiente · necesita la ronda corta de preguntas (`IA_LEER-PRIMERO.md` §2) |
| 5 | **El adaptador JDBC y la migración** | El SQL de la migración y el `guardar` | prueba del adaptador contra PostgreSQL (Testcontainers, con Docker) | Repository / Adapter; persistencia fuera del núcleo | pendiente · igual que el 4 |
| 6 | **El caso de uso conectado** | La llamada al avance y el `confirmado_hasta` real | `CanalParticionesTest` con el caso 1, 2, 4, 3 | Caso de uso que orquesta; trazabilidad regla → código → prueba | pendiente · igual que el 4 |
| 7 | **El nodo entero** | — (lo lees y lo explicas) | `NodoCompletoTest`, `NodoArranqueSeguroTest` | Docker, Compose, gestor de secretos, Twelve-Factor (configuración) | pendiente |
| 8 | **La presentación** | La matriz requisito → código → prueba de lo que construiste | — | Gobierno: trazabilidad (fila «PARCIAL» de la guía, §1.1) | pendiente |

Los tramos 2 y 3 son la explicación pendiente del Segmento 1 del dominio, hecha construyendo: al cerrarlos, la
explicación cuenta para su aceptación.

---

## Tramo 1 · Separar el controlador

### Dónde estás
```
celular ─▶ gateway ─▶ [ParticionesControlador] ─▶ ensamblador ─▶ caso de uso ─▶ …
                       ▲ aquí: que solo adapte
```

### Estado
- **Bien, no tocar:** la dirección de dependencias. El controlador solo conoce el puerto de entrada
  (`RecibirParticionPuertoEntrada`) y sus DTO; nunca el dominio. Las pruebas del canal pasan.
- **Lo que falla (tu observación):** en 183 líneas el controlador hace tres trabajos: (1) traducir HTTP ↔ puerto, que
  es lo suyo; (2) revisar la forma del JSON (`formaMinima`, `soloObjetos`); (3) un límite de PostgreSQL (`guardable`:
  `jsonb` no guarda el carácter U+0000). El 3 hace que el adaptador de entrada sepa algo del adaptador de salida.
- **Además:** `uuidONull` y `arbolONull` devuelven `null`. No son Null Object: son lo contrario. La regla del
  proyecto en la frontera es devolver un resultado con nombre, nunca `null` ni una excepción.

### Concepto 1 · Responsabilidad única y el adaptador que solo traduce
- **Qué es:** una clase tiene una sola razón para cambiar (Martin, *Clean Architecture*, cap. 7). Un adaptador de
  entrada traduce un protocolo (HTTP) a una llamada del puerto; no decide reglas ni sabe de la base.
- **Tu modelo dentro del proyecto** (como el docente en PRA-1): `contrato/.../lectura/LecturaResultado.java`. Es un
  resultado sellado con dos casos, `Leida` y `Fallida`: quien lee nunca recibe `null`, recibe uno de los dos casos.

### Concepto 2 · Resultado sellado en vez de `null` (Special Case)
- **Qué es:** una `sealed interface` con un `record` por caso. El compilador sabe que solo existen esos casos, y un
  `switch` sobre ella obliga a tratar todos (si falta uno, no compila).

### Ejemplo completo, otro tema con la misma forma
Una estación de clima recibe una lectura de temperatura por HTTP. Compila tal cual (Spring 7, Java 21 o más).

```java
// 1 · El resultado de leer: solo dos casos, nunca null.
sealed interface LecturaLeida permits LecturaLeida.Valida, LecturaLeida.Rechazada {
    record Valida(String estacion, double celsius) implements LecturaLeida {}
    record Rechazada() implements LecturaLeida {}
}

// 2 · El lector: toda la revisión de forma vive aquí; no lanza y no devuelve null.
final class LectorDeLectura {

    private LectorDeLectura() {}

    static LecturaLeida leer(String estacion, String cuerpo) {
        Optional<Double> celsius = numero(cuerpo);
        if (estacion == null || estacion.isBlank() || celsius.isEmpty() || !posible(celsius.get())) {
            return new LecturaLeida.Rechazada();
        }
        return new LecturaLeida.Valida(estacion, celsius.get());
    }

    private static Optional<Double> numero(String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Double.parseDouble(texto.strip()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    private static boolean posible(double celsius) {
        return celsius > -273.15;
    }
}

// 3 · El puerto y su DTO (del lado de servicios).
interface RegistrarLecturaPuertoEntrada {
    String ejecutar(RegistrarLecturaEntradaDto entrada);
}

record RegistrarLecturaEntradaDto(String estacion, double celsius) {}

// 4 · El controlador: solo adapta. Lee, decide con un switch y llama al puerto.
@RestController
class LecturasControlador {

    private final RegistrarLecturaPuertoEntrada registrar;

    LecturasControlador(RegistrarLecturaPuertoEntrada registrar) {
        this.registrar = registrar;
    }

    @PostMapping("/clima/v1/estaciones/{id}/lecturas")
    ResponseEntity<?> recibir(@PathVariable("id") String id, @RequestBody(required = false) String cuerpo) {
        return switch (LectorDeLectura.leer(id, cuerpo)) {
            case LecturaLeida.Valida v ->
                    ResponseEntity.ok(registrar.ejecutar(new RegistrarLecturaEntradaDto(v.estacion(), v.celsius())));
            case LecturaLeida.Rechazada r -> ResponseEntity.unprocessableContent().build();
        };
    }
}
```

| En el ejemplo | En tu caso |
|---|---|
| `LecturaLeida` (`Valida`, `Rechazada`) | el resultado de leer una partición: qué datos necesita el controlador del caso válido para armar `RecibirParticionEntradaDto` y la respuesta |
| `LectorDeLectura.leer(id, cuerpo)` | el lector de la partición: recibe el id de la ruta y los bytes del cuerpo |
| `numero(...)` con `Optional` | `uuidONull` y `arbolONull`, que hoy devuelven `null` |
| `posible(...)` | `formaMinima`, `soloObjetos` |
| `LecturasControlador.recibir` con `switch` | `ParticionesControlador.recibir`: deja de revisar la forma |
| (no tiene par) | `guardable` (U+0000): **decides tú** dónde vive, ver la pregunta 3 |

### Qué es igual y qué es distinto del ejemplo
- **Igual:** la forma (resultado sellado → lector que no lanza → `switch` en el controlador) y que las pruebas no
  cambian: siguen esperando 422 `ingesta/rechazado` para todo fallo de forma.
- **Distinto:** tu lector usa `LectorDto.estricto()` (el de `contrato`) para convertir el árbol en `ParticionDto`, y
  su resultado ya es sellado (`LecturaResultado`): úsalo, no lo dupliques. Tu caso válido lleva más datos que el
  ejemplo (la sesión, el árbol de cada anotación y el DTO).

### Tareas
Todo en `codigo/backend-finca/src/main/java/co/florlogic/finca/ingesta/infraestructura/input/controlador/`.

1. **Crea el resultado sellado** de leer una partición (un archivo nuevo, dos casos).
   Comprueba: `cd codigo && ./mvnw -q -pl backend-finca -am compile` sin errores.
2. **Crea el lector** (un archivo nuevo) con un método `static` que reciba el id de la ruta y los bytes, y devuelva
   tu resultado sellado. Mueve ahí `formaMinima` y `soloObjetos` tal cual; cambia `uuidONull` y `arbolONull` para que
   devuelvan `Optional` en vez de `null`.
   Comprueba: compila, y `grep -n "return null" ParticionesControlador.java <tu-lector>.java` no muestra nada.
3. **Cambia `ParticionesControlador.recibir`** para que lea con tu lector y decida con un `switch`. En el
   controlador solo quedan la traducción al DTO de entrada, la llamada al puerto y las respuestas.
   Comprueba: `ParticionesControlador.java` baja de 183 líneas a menos de la mitad.
4. **Corre el examen:**
   `./mvnw -pl backend-finca -am verify -Dtest='Canal*' -Dsurefire.failIfNoSpecifiedTests=false`
   Debe salir `CanalParticionesTest` 5/5 y `CanalPorGatewayTest` 25/25 (necesita Docker; sin Docker se saltan y no
   prueban nada).

### Preguntas de entendimiento (al cerrar el tramo)
1. ¿Por qué tu lector devuelve un resultado sellado y no `null`, y qué pasa en el `switch` del controlador si mañana
   alguien agrega un tercer caso?
2. ¿El controlador **usa** o **implementa** el puerto de entrada? ¿Y tu lector, de qué depende?
3. **Decisión:** `guardable` (U+0000) ¿es una regla del contrato («ningún texto lleva U+0000», la revisa el lector y
   vale para cualquier base) o un límite de la persistencia (lo revisa el adaptador JDBC)? Elige y di qué cambia en
   el dibujo del hexágono.

### Para la presentación (guía IS II)
- **Clean Code / SOLID (S):** el antes y el después de `ParticionesControlador` (183 líneas, tres trabajos → un trabajo).
- **Clean Architecture:** el adaptador de entrada solo traduce; muestra que no importa nada del dominio.
- **OWASP (validación de la entrada):** toda entrada externa pasa por un lector que nunca lanza; las 10 entradas malas
  de `CanalParticionesTest` dan 422.

---

## 3 · Registro de la ruta

| Tramo | Cerrado | Prueba | Respuestas |
|---|---|---|---|
| 1 | abierto: tarea 1 sin empezar | — | 29-oct: **ensamblador de entrada** explicado (práctica ✓, teoría ✓: «Adapter = flujo de llamadas, Assembler = flujo de datos»); **controlador**: sesión que no es UUID, parcial (vio la primera conversión, no vio `uuidONull` + línea 64); **adaptador JDBC** y **dónde se juzga la anotación**: explicados por Claude, falta su respuesta (pregunta pendiente: 1, 2 y 4 → veredicto del 4 y `confirmado_hasta`) |
