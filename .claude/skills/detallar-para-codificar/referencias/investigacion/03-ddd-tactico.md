# 03 · DDD táctico y modelado del dominio

> Frente de investigación para la skill `DetallarParaCodificar`. Ejemplos en Java 21+ (records,
> sealed interfaces). Dominio de ejemplo: despacho de cajas de flores. Lo marcado **[opinión]** es
> interpretación del autor de este documento, no cita de las fuentes.

## 1. Qué es

El DDD táctico es el conjunto de patrones de Eric Evans (2003) para expresar en código un modelo
del dominio, siempre dentro de un *bounded context* con su *lenguaje ubicuo*. Los bloques son:

- **Entidad**: se distingue por su identidad y su ciclo de vida, no por sus atributos.
- **Objeto de valor**: se distingue solo por sus atributos. Es inmutable y sus operaciones no
  tienen efectos secundarios (dinero, rango de fechas, código de finca).
- **Agregado**: grupo de entidades y valores con una raíz y una frontera de consistencia. Fuera
  de él solo se referencia la raíz. "Transactions should not cross aggregate boundaries" (Fowler).
- **Evento de dominio**: un hecho del negocio ya ocurrido, en pasado (`CajaDespachada`).
- **Servicio de dominio**: operación significativa que no le pertenece a ninguna entidad ni valor.
- **Repositorio**: la ilusión de una colección en memoria de raíces de agregado.
- **Fábrica**: encapsula la construcción compleja y deja el objeto válido desde que nace.
- **Especificación**: regla de negocio con nombre (`isSatisfiedBy`) que se puede combinar.

Vernon aterriza los agregados en cuatro reglas: modelar invariantes verdaderas, diseñar agregados
pequeños, referenciar otros agregados por identidad y usar consistencia eventual fuera de la
frontera. Wlaschin aporta "make illegal states unrepresentable": codificar las reglas en los
tipos. Brandolini aporta Event Storming, que descubre el modelo en orden: eventos, comandos,
políticas, agregados y read models.

## 2. Reglas operativas para un agente

Cada regla es imperativa y se puede comprobar contra el modelo o el código propuesto.

1. **Nombra en el lenguaje ubicuo.** Cada clase, método y evento usa un término del glosario
   acordado. Si un nombre no está en el glosario, pregunta antes de inventarlo.
2. **Eventos en pasado, comandos en imperativo.** Ejemplos: `DespacharCaja` produce
   `CajaDespachada`. Rechaza nombres CRUD (`ActualizarCaja`) si el negocio tiene un verbo propio.
3. **Toda invariante tiene un dueño.** Cada regla "siempre debe cumplirse" se asigna a un solo
   agregado o tipo de valor. Si no tiene dueño, es una política o un hotspot.
4. **Distingue invariante de política.** Si la regla puede incumplirse unos segundos y luego
   corregirse, no es invariante transaccional: es una política o consistencia eventual. Aplica la
   pregunta de Vernon, "¿de quién es el trabajo?": si es del mismo usuario en el mismo acto,
   consistencia transaccional; si es de otro usuario o del sistema, consistencia eventual.
5. **Un comando modifica un solo agregado por transacción.** Si un caso de uso toca dos, pide
   justificación explícita. Vernon da cuatro razones para romper la regla: conveniencia de la UI,
   falta de mecanismo técnico, transacciones globales impuestas y rendimiento de consultas.
6. **Referencia otros agregados solo por id tipado** (`FincaId`), nunca por objeto.
7. **Agregado pequeño por defecto.** Una raíz con sus valores. Agrega entidades internas solo si
   una invariante lo exige. Señal de alerta: colecciones sin límite dentro del agregado.
8. **Primitivos con significado son objetos de valor.** Dinero, cantidades con unidad, códigos,
   correos y rangos se modelan como `record` que se valida en el constructor compacto.
9. **Haz ilegales los estados inválidos.** Modela cada estado con datos distintos como una
   variante de una `sealed interface`. No uses `boolean` sueltos ni campos `null` según el estado.
10. **Cada transición es un método con nombre de negocio.** Recibe el comando o sus valores y
    devuelve el nuevo estado o los eventos. No expongas setters públicos.
11. **Los métodos que fallan por reglas de negocio lo declaran.** El fallo va en el tipo de retorno
    (`Resultado`/sealed) o en una excepción de dominio con nombre. Nunca un `IllegalStateException`
    genérico.
12. **Identidad:** genera el id en la aplicación con UUIDv7 (RFC 9562) para ordenar índices B-tree.
    Usa UUIDv4 cuando el id sea secreto o de seguridad, porque v7 revela el instante de creación.
13. **Concurrencia optimista:** cada raíz lleva `version` (`@Version` en JPA). Un conflicto se
    traduce a un error de dominio o a un reintento, nunca a "gana el último".
14. **Idempotencia:** todo comando que llegue por red o por mensaje lleva una clave de idempotencia
    derivada de la identidad de negocio. Los consumidores de eventos deduplican por id de evento,
    porque el outbox (como el *Event Publication Registry* de Spring Modulith) entrega
    al menos una vez.
15. **Repositorio por agregado, no por tabla.** `save(raiz)` y `porId(id)`. Las consultas de lectura
    complejas van en un read model aparte.
16. **Servicio de dominio solo si la operación cruza conceptos** y no tiene dueño natural. Debe ser
    sin estado. Si un servicio cambia campos de una entidad, la lógica pertenece a la entidad.
17. **Todo invariante tiene al menos un caso dado/cuando/entonces** que lo viola y espera el
    rechazo (Evans: *assertions*, "write automated unit tests for them").
18. **Las preguntas abiertas se registran como hotspots.** No inventes reglas, cifras ni nombres
    para completar el modelo.

## 3. Elementos prioritarios al modelar un dominio

Están ordenados por dependencia: cada uno necesita los anteriores. En cada pregunta de la skill,
la "plantilla de ejemplo" toma el elemento en curso.

| # | Elemento | Por qué va en este orden |
|---|----------|--------------------------|
| 1 | Propósito y bounded context | Sin frontera, los términos cambian de significado y el modelo se contamina. |
| 2 | Lenguaje ubicuo (5-15 términos) | Da los nombres de tipos y métodos. Es lo más barato de corregir hoy. |
| 3 | Eventos de dominio | Son hechos observables por el negocio y fijan el "qué pasó" antes del "cómo". |
| 4 | Comandos y actores | Cada evento necesita un disparador: humano, sistema externo, tiempo o política. |
| 5 | Invariantes | Definen la frontera del agregado (Vernon, regla 1). |
| 6 | Agregados y su frontera | Agrupan los comandos y eventos que comparten invariantes. |
| 7 | Estados y transiciones | Muestran qué comandos son válidos en cada momento y alimentan la sealed interface. |
| 8 | Objetos de valor | Sacan validaciones de los agregados y eliminan la obsesión por los primitivos. |
| 9 | Políticas y consistencia eventual | Cubren lo que no cabe en una transacción ("cuando X, entonces Y"). |
| 10 | Identidad, versión e idempotencia | Hacen el modelo seguro ante reintentos y concurrencia. |
| 11 | Firmas de métodos y errores | El punto de entrada al código y a las pruebas. |
| 12 | Read models y repositorios | Indican qué se consulta y qué se carga, sin deformar el agregado. |

### Aggregate Design Canvas (DDD Crew, Nick Tune) como plantilla de preguntas

| Sección | Pregunta para el humano | Señal de alerta |
|---------|-------------------------|-----------------|
| 1. Nombre | ¿Cómo lo llama el negocio? ¿Su ciclo de vida se refleja en el nombre? | Nombres técnicos (`Manager`, `Data`). |
| 2. Descripción | ¿Qué responsabilidad tiene y por qué esta frontera y no otra? | No se puede decir en dos frases. |
| 3. Transiciones de estado | ¿Qué estados tiene y qué lo mueve entre ellos? | Muchas: partir el proceso. Ninguna: agregado anémico. |
| 4. Invariantes que impone | ¿Qué reglas nunca pueden romperse, ni un instante? | Muchas: complejidad local alta. |
| 5. Políticas correctivas | Si relajamos una invariante, ¿quién corrige y cómo? | Muchas: la lógica se fue fuera del agregado. |
| 6. Comandos que maneja | ¿Qué órdenes acepta? | Un comando sin evento resultante. |
| 7. Eventos que crea | ¿Qué hechos emite cada comando? | Un evento que no sale de ningún comando. |
| 8. Throughput | ¿Cuántos comandos por instancia (promedio y pico)? ¿Cuántos clientes concurrentes? | Picos altos: conflictos de versión, conviene partir. |
| 9. Tamaño | ¿Cuántos eventos o hijos por instancia? ¿Cuánto vive? | Crecimiento sin límite: acotar por periodo o snapshot. |

**[opinión]** Para la skill, las secciones 3, 4, 6 y 7 son obligatorias antes de escribir código.
Las 8 y 9 pueden quedar como `PENDIENTE` con una estimación de orden de magnitud.

## 4. Banco de preguntas cortas

Van de lo general a lo detallado. Cada una se hace sola, con opciones y un ejemplo que muestra en
qué se convierte la respuesta.

### 4.1 Contexto y lenguaje

**P1. ¿En qué contexto vive este elemento y qué problema del negocio resuelve?**
Opciones: núcleo (diferenciador) · soporte · genérico (comprar o reutilizar).
```java
// package co.florlogic.despacho  -> bounded context "Despacho"
// Propósito: asignar cajas a camiones respetando la cadena de frío.
```

**P2. ¿Qué palabra usa el negocio para esto? ¿Hay sinónimos que deban unificarse?**
Opciones: un término único · dos términos iguales (elegir uno) · dos conceptos distintos (separar).
```java
public record Caja(CajaId id, CodigoFinca finca, Tallos tallos) {}
// "bonche" ≠ "caja": la caja contiene N bonches -> dos tipos
```

**P3. ¿Qué término significa algo distinto en otro equipo o contexto?**
Opciones: ninguno · sí, traducir en el borde (capa anticorrupción) · sí, compartir núcleo.
```java
// Ventas.Pedido != Despacho.OrdenDeCarga; se traduce en el adaptador
record OrdenDeCarga(OrdenId id, List<CajaId> cajas) {}
```

### 4.2 Eventos y comandos

**P4. ¿Qué hechos importantes ocurren? Dilos en pasado.**
Opciones: creación · cambio de estado · cumplimiento de plazo · fallo o rechazo.
```java
sealed interface EventoCaja permits CajaRegistrada, CajaDespachada, CajaRechazada {}
record CajaDespachada(CajaId caja, CamionId camion, Instant en) implements EventoCaja {}
```

**P5. ¿Quién o qué provoca cada hecho?**
Opciones: usuario con rol · sistema externo · reloj o plazo · política automática.
```java
record DespacharCaja(CajaId caja, CamionId camion, UsuarioId operario) {} // comando
// Política: "cuando CamionCerrado, entonces GenerarManifiesto"
```

**P6. ¿Qué datos necesita el comando para decidir sin consultar nada más?**
Opciones: solo ids · ids + valores del formulario · además un read model consultado antes.
```java
record RegistrarCaja(CodigoFinca finca, Tallos tallos, TemperaturaC temp,
                     ClaveIdempotencia clave) {}
```

**P7. ¿Qué información debe llevar el evento para quien lo escuche?**
Opciones: solo ids (consultar luego) · estado completo relevante · id + campos clave.
```java
record CajaRechazada(UUID eventoId, CajaId caja, MotivoRechazo motivo,
                     Instant ocurrioEn) implements EventoCaja {}
```

### 4.3 Invariantes y frontera

**P8. ¿Qué regla nunca puede romperse, ni siquiera un segundo?**
Opciones: límite numérico · unicidad · orden obligatorio de pasos · coherencia entre campos.
```java
// Invariante: peso total del camión <= capacidad
if (pesoActual.mas(caja.peso()).excede(capacidad))
    throw new CapacidadExcedida(id, capacidad);
```

**P9. Si esa regla se rompe un momento y se corrige luego, ¿hay daño real?**
Opciones: sí, daño real (invariante transaccional) · no, basta corregirla (política) · no sé (hotspot).
```java
// Política correctiva en vez de invariante:
@ApplicationModuleListener void on(CajaDespachada e) { inventario.descontar(e.caja()); }
```

**P10. ¿Esa regla es trabajo del mismo usuario en el mismo acto, o de otro?**
Opciones: mismo usuario y acto (misma transacción) · otro usuario · el sistema (eventual).
```java
// Mismo acto: Camion.cargar(caja) valida capacidad dentro del agregado Camion
// Otro actor: Facturacion reacciona a CamionCerrado de forma asíncrona
```

**P11. ¿Qué objetos deben cambiar juntos para que la regla se cumpla?**
Opciones: solo la raíz · raíz + colección acotada · dos agregados (rediseñar o usar política).
```java
public final class Camion {           // raíz
  private final List<CajaId> cajas;   // por id, no objetos Caja completos
  private Peso pesoCargado;           // lo mínimo para la invariante
}
```

**P12. ¿Cuántos hijos puede tener una instancia como máximo?**
Opciones: pocos y acotados (<50) · cientos · sin límite (sacar a otro agregado).
```java
// Límite en el modelo: un camión admite como máximo 400 cajas
if (cajas.size() == MAX_CAJAS) throw new CamionLleno(id);
```

### 4.4 Valores e identidad

**P13. ¿Qué datos son "valores" con reglas propias, no texto ni números sueltos?**
Opciones: dinero/moneda · cantidad + unidad · código con formato · rango de fechas o temperaturas.
```java
public record TemperaturaC(BigDecimal valor) {
  public TemperaturaC { if (valor.compareTo(new BigDecimal("-2")) < 0)
      throw new TemperaturaInvalida(valor); }
}
```

**P14. ¿Cómo se identifica una instancia y quién genera el id?**
Opciones: UUIDv7 generado en la app · id natural del negocio · secuencia de la BD.
```java
public record CajaId(UUID valor) {
  public static CajaId nuevo() { return new CajaId(UuidV7.generar()); } // RFC 9562
}
```

**P15. ¿Dos instancias con los mismos datos son la misma cosa?**
Opciones: sí (objeto de valor) · no, importa su historia (entidad).
```java
record Direccion(String calle, String ciudad) {}  // valor: igualdad por atributos
final class Camion { final CamionId id; /* entidad: igualdad por id */ }
```

### 4.5 Estados y transiciones

**P16. ¿En qué estados puede estar y qué datos existen solo en cada uno?**
Opciones: lineal (A→B→C) · con rama de rechazo · con vuelta atrás · con estado terminal.
```java
sealed interface EstadoCaja permits Registrada, Despachada, Rechazada {}
record Registrada(TemperaturaC temp) implements EstadoCaja {}
record Despachada(CamionId camion, Instant en) implements EstadoCaja {}
record Rechazada(MotivoRechazo motivo) implements EstadoCaja {}
```

**P17. ¿Qué comando es válido en cada estado y qué pasa si llega en otro?**
Opciones: se rechaza con error · se ignora (idempotente) · se encola.
```java
return switch (estado) {
  case Registrada r -> despachar(r, cmd);
  case Despachada d when d.camion().equals(cmd.camion()) -> sinCambios(); // repetido
  case Despachada d -> throw new TransicionInvalida(estado, cmd);
  case Rechazada x  -> throw new TransicionInvalida(estado, cmd);
};
```

**P18. ¿Hay estados terminales o algo que nunca pueda deshacerse?**
Opciones: sí, terminal · se compensa con otro evento · se puede reabrir con permiso.
```java
// Rechazada es terminal: se corrige con un evento de compensación, no se edita
record RechazoAnulado(CajaId caja, UsuarioId supervisor) implements EventoCaja {}
```

### 4.6 Concurrencia e idempotencia

**P19. ¿Pueden dos personas o procesos cambiar la misma instancia a la vez?**
Opciones: casi nunca · a veces (versión optimista) · a menudo (partir el agregado).
```java
@Version private long version;   // JPA: OptimisticLockException -> ConflictoDeVersion
```

**P20. Si el mismo comando llega dos veces, ¿qué debe ocurrir?**
Opciones: devolver el mismo resultado · rechazar como duplicado · aplicar otra vez (raro).
```java
record ClaveIdempotencia(String valor) {}   // p. ej. finca + guía + número de caja
if (procesados.existe(cmd.clave())) return procesados.resultado(cmd.clave());
```

### 4.7 Servicios, fábricas, especificaciones, repositorios

**P21. ¿Esta operación le pertenece a un solo concepto o cruza varios?**
Opciones: a uno (método del agregado) · cruza varios sin estado (servicio de dominio) · es orquestación (servicio de aplicación).
```java
interface AsignadorDeRuta {           // servicio de dominio: cruza Camion y Ruta
  RutaId asignar(Camion camion, List<Ruta> disponibles);
}
```

**P22. ¿Crear una instancia exige reglas o datos de varias fuentes?**
Opciones: constructor simple · método fábrica estático · fábrica aparte (varias fuentes).
```java
public static Caja registrar(RegistrarCaja cmd, Reloj reloj) {
  return new Caja(CajaId.nuevo(), cmd.finca(), new Registrada(cmd.temp()), reloj.ahora());
}
```

**P23. ¿Hay una regla de selección o elegibilidad que se repita en varios lugares?**
Opciones: no · sí, para validar · sí, para filtrar consultas · ambas.
```java
interface Especificacion<T> { boolean cumple(T t);
  default Especificacion<T> y(Especificacion<T> o) { return t -> cumple(t) && o.cumple(t); } }
Especificacion<Caja> aptaParaExportar = c -> c.temperatura().enRango(RANGO_FRIO);
```

**P24. ¿Cómo se busca y se guarda esto?**
Opciones: solo por id · por id + una búsqueda de negocio · listados (read model aparte).
```java
interface Camiones { Optional<Camion> porId(CamionId id); void guardar(Camion c); }
```

### 4.8 Firma final

**P25. ¿Cuál es la firma del método: entra comando, salen eventos o error?**
Opciones: devuelve eventos · devuelve el nuevo estado · lanza excepción de dominio · `Resultado` sellado.
```java
sealed interface Resultado permits Ok, Rechazo {}
record Ok(List<EventoCaja> eventos) implements Resultado {}
record Rechazo(ErrorDominio error) implements Resultado {}
Resultado despachar(DespacharCaja cmd);
```

**[opinión]** Para Java con Spring Modulith, la opción por defecto es "el método muta el agregado y
registra eventos, el servicio de aplicación guarda y publica". El estilo funcional
(comando → eventos) de Wlaschin conviene cuando el agregado es una máquina de estados rica.

## 5. De tarjetas de Event Storming a modelo del dominio y a casos dado/cuando/entonces

La skill recibe tarjetas con cuatro campos: **estímulo, entorno, qué pasó, cómo lo abordamos**.
**[opinión]** Corresponden a la gramática del nivel de diseño de Event Storming (actor →
comando → agregado → evento → política → comando, más read models y sistemas externos):

| Campo de la tarjeta | Elemento de Event Storming | Elemento del modelo | Parte del caso de prueba |
|---------------------|----------------------------|---------------------|--------------------------|
| Estímulo | Actor + comando (azul) o política (lila) | `record` comando + método de la raíz | **Cuando** |
| Entorno | Estado previo del agregado + read model (verde) + condiciones | Estado de la sealed interface + datos consultados | **Dado** |
| Qué pasó | Evento(s) (naranja) o rechazo | `record` evento o error de dominio | **Entonces** |
| Cómo lo abordamos | Agregado (amarillo) que decide + políticas siguientes | Invariante en el agregado, política correctiva, listener | Reglas y casos adicionales |

**Procedimiento (verificable):**

1. Del **qué pasó** extrae un evento en pasado por hecho distinto. Si describe un fallo, conviértelo
   en error de dominio con nombre (`CapacidadExcedida`), no en evento, salvo que el negocio
   necesite registrarlo (`CajaRechazada`).
2. Del **estímulo** extrae el comando en imperativo y su emisor (actor, sistema, reloj o política).
   Lista los datos que trae el comando.
3. Del **entorno** separa tres cosas: (a) el estado del agregado, que será el `Dado`; (b) los datos
   externos que se consultan, que serán un read model o un puerto; (c) las condiciones técnicas
   (carga, red), que van al frente de calidad y no al dominio.
4. Del **cómo lo abordamos** extrae: la regla que decide (invariante o política, usando P9 y P10) y
   el agregado que la posee (el que tiene todos los datos para decidir sin consultar fuera).
5. Agrupa las tarjetas por agregado y llena el Aggregate Design Canvas (sección 3). Si dos tarjetas
   exigen que dos agregados cambien juntos, vuelve a P10 o márcalo como hotspot.
6. Escribe un caso por camino: el feliz, uno por invariante violada, uno por comando repetido
   (idempotencia) y uno por conflicto de versión si el throughput lo amerita.

**Ejemplo.** La tarjeta dice: estímulo "operario escanea caja para cargarla al camión"; entorno
"camión abierto con 3.980 kg de 4.000 kg"; qué pasó "la caja de 25 kg no se carga y se avisa";
cómo lo abordamos "el camión no supera su capacidad; se sugiere otro camión".

```gherkin
Escenario: Rechazar caja que excede la capacidad del camión
  Dado un Camion "C-12" en estado Abierto con capacidad 4000 kg y 3980 kg cargados
  Cuando el operario ejecuta CargarCaja(caja "K-7" de 25 kg, camion "C-12")
  Entonces se rechaza con CapacidadExcedida(camion "C-12", disponible 20 kg)
  Y no se emite ningún evento CajaCargada
  Y el Camion conserva 3980 kg y la misma versión
```
```java
@Test void rechazaCajaQueExcedeCapacidad() {
  var camion = CamionMadre.abierto(kg(4000), kg(3980));             // Dado
  var r = camion.cargar(new CargarCaja(cajaId("K-7"), kg(25)));     // Cuando
  assertThat(r).isEqualTo(new Rechazo(new CapacidadExcedida(camion.id(), kg(20)))); // Entonces
}
```
"Se sugiere otro camión" no es una invariante: es una **política** o un read model ("camiones con
capacidad disponible"). Se modela aparte con su propia tarjeta.

## 6. Antipatrones y señales detectables

| Antipatrón | Señales detectables | Corrección |
|------------|---------------------|------------|
| **Modelo anémico** (Fowler) | Entidades con solo getters/setters. Servicios `XxxService` con `if` sobre el estado de la entidad. El canvas no muestra transiciones. | Mueve la regla al método de la entidad. Quita los setters públicos. |
| **Agregado gigante** (Vernon) | Colecciones sin límite. Cargar la raíz trae cientos de filas. Conflictos de versión frecuentes. Un comando toca muchos hijos que no comparten invariante. | Parte por invariante verdadera y referencia por id. |
| **Referencias por objeto entre agregados** | Campos `Finca finca` dentro de `Caja`. Relaciones JPA `@ManyToOne` hacia otra raíz. Cascadas entre agregados. | Usa `FincaId`. Si hay que navegar, que sea vía repositorio en la capa de aplicación. |
| **Transacción multiagregado por costumbre** | Un `@Transactional` guarda dos raíces distintas sin justificar una de las cuatro razones de Vernon. | Emite un evento y usa una política con `@ApplicationModuleListener`. |
| **Obsesión por primitivos** | `String codigoFinca`, `double precio`, `int temperatura`. Validaciones repetidas en varios sitios. | Crea un objeto de valor (`record`) con validación en el constructor. |
| **Estado por banderas** | `boolean despachada, rechazada` y campos que valen `null` según el estado. Combinaciones imposibles representables. | Usa una sealed interface por estado (Wlaschin). |
| **Eventos CRUD o técnicos** | `CajaActualizada`, `RegistroGuardado`, eventos sin significado para el negocio. | Nombra el hecho de negocio (`TemperaturaFueraDeRango`). |
| **Servicio de dominio "dios"** | Un servicio con decenas de métodos que muta varias entidades. | Reparte la lógica a los agregados. El servicio solo coordina cálculos sin estado. |
| **Repositorio por tabla** | `CajaLineaRepository` para hijos internos del agregado. Consultas de pantalla dentro del agregado. | Un repositorio por raíz. Read models para listados. |
| **Invariantes implícitas** | Reglas que solo están en la UI o en SQL. Ningún test que viole la regla. | Llévala al agregado o al valor y agrega su caso dado/cuando/entonces. |
| **Ignorar duplicados** | Listeners sin deduplicación por id de evento. Comandos remotos sin clave de idempotencia. | Usa una tabla de procesados con restricción única en la misma transacción. |
| **"Gana el último"** | Raíz sin `@Version`. Actualizaciones `UPDATE ... SET` sin condición de versión. | Concurrencia optimista y un error de dominio ante el conflicto. |
| **Modelo atado a la persistencia** | Anotaciones JPA que dictan la forma del dominio. Constructores vacíos públicos. Ids `Long` autoincrementales visibles en la API. | **[opinión]** Separa el mapeo o acepta JPA con constructores protegidos e ids generados en la app. |

## 7. Fuentes

- Eric Evans, *DDD Reference* (2015), resumen oficial de todos los patrones con su "Therefore":
  https://www.domainlanguage.com/ddd/reference/
- Eric Evans, *Domain-Driven Design* (Blue Book, 2003), origen de los bloques tácticos, el lenguaje
  ubicuo y el diseño flexible (assertions, funciones sin efectos secundarios):
  https://www.domainlanguage.com/ddd/
- Vaughn Vernon, *Effective Aggregate Design* I-III, con las cuatro reglas, "¿de quién es el
  trabajo?" y las razones para romperlas: https://www.dddcommunity.org/library/vernon_2011/
- Vaughn Vernon, *Implementing Domain-Driven Design* (2013), la implementación de agregados,
  eventos, repositorios y la identidad generada por la app:
  https://www.oreilly.com/library/view/implementing-domain-driven-design/9780133039900/
- Martin Fowler, *DDD Aggregate*, con la raíz, las referencias externas solo a la raíz y las
  transacciones dentro del agregado: https://martinfowler.com/bliki/DDD_Aggregate.html
- Martin Fowler, *Value Object*, sobre igualdad por valor, inmutabilidad y aliasing:
  https://martinfowler.com/bliki/ValueObject.html
- Martin Fowler, *Anemic Domain Model*, con los síntomas del antipatrón:
  https://martinfowler.com/bliki/AnemicDomainModel.html
- Evans y Fowler, *Specifications*, sobre la validación, la selección y la construcción a pedido:
  https://martinfowler.com/apsupp/spec.pdf
- Scott Wlaschin, *Designing with types: making illegal states unrepresentable*:
  https://fsharpforfunandprofit.com/posts/designing-with-types-making-illegal-states-unrepresentable/
- Scott Wlaschin, *Domain Modeling Made Functional* (2018), con los flujos como funciones
  (comando → eventos) y los estados como tipos:
  https://pragprog.com/titles/swdddf/domain-modeling-made-functional/
- Alberto Brandolini, *Introducing EventStorming* y los formatos Big Picture, Process y Design:
  https://www.eventstorming.com/ y https://leanpub.com/introducing_eventstorming
- Three Dots Academy, *Process Level Event Storming*, con la gramática actor → comando →
  agregado → evento → política y los colores: https://academy.threedots.tech/knowledge/process-level-event-storming/
- DDD Crew, *Aggregate Design Canvas*, con sus nueve secciones y las heurísticas de throughput y
  tamaño: https://github.com/ddd-crew/aggregate-design-canvas
- DDD Crew, *Bounded Context Canvas*, con el propósito, la clasificación estratégica, el lenguaje,
  las decisiones de negocio y las preguntas abiertas: https://github.com/ddd-crew/bounded-context-canvas
- IETF RFC 9562 (UUIDv7), sobre la estructura, la monotonía, la localidad en índices y cuándo
  preferir v4: https://www.rfc-editor.org/rfc/rfc9562.html
- Spring Modulith, *Events*, con `@ApplicationModuleListener` y el Event Publication Registry
  (outbox, reintentos): https://docs.spring.io/spring-modulith/reference/events.html
- jMolecules, con los tipos y anotaciones DDD para Java (`AggregateRoot`, `Identifier`) e
  integración con Spring: https://github.com/xmolecules/jmolecules
- Práctica comunitaria sobre idempotencia en CQRS (marca de comando y eventos en la misma
  transacción, idempotencia frente a versión, inbox/outbox):
  https://dev.to/ohugonnot/idempotency-in-cqrs-and-event-sourcing-part-2-commands-projections-and-outbox-4ei
