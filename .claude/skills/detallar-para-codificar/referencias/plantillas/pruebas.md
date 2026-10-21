---
plantilla: pruebas
aplica_a: la estrategia de pruebas de un elemento o de un flujo — qué comportamiento se protege, en qué nivel del hexágono se prueba (tamaño y alcance), con qué dobles, con qué herramientas (JUnit Jupiter, AssertJ, Spring Boot Test, Testcontainers, Spring Modulith, ArchUnit, jqwik, PIT, Pact o Spring Cloud Contract), cómo se nombra y estructura cada prueba, cómo se controla el tiempo, la concurrencia y la asincronía, y cómo se mide la calidad de las pruebas
entra_desde: cualquier plantilla (su §11 «Qué se genera» lista las pruebas); en especial caso-de-uso (P20, dobles y contrato), adaptador-persistencia (P20 y §10.6), endpoint (pruebas web y contratos), evento-mensaje (T6, `Scenario`) y trabajo-programado
sale_hacia: ninguna (devuelve el control a la plantilla que la llamó)
---
# Plantilla · Pruebas (formas saludables de probar y pirámide de pruebas)

## 0 · Instrucciones para el agente

1. Usa esta plantilla para decidir **cómo se prueba** un elemento ya detallado (o un flujo de varios):
   qué comportamientos se protegen, en qué nivel va cada prueba, qué se sustituye por un doble, qué
   herramienta se usa y cómo se sabe que las pruebas sirven. No detalla el elemento: eso lo hizo la
   plantilla de origen. Aquí se decide la **cartera de pruebas** (Fowler, *TestPyramid*: «a balanced
   portfolio»).
2. **Empieza donde terminó la plantilla de origen.** Lee su §11 y sus casos dado/cuando/entonces. Cada
   caso de la ESPEC es una prueba candidata (`investigacion/05` §4.1, ítems 1, 5 y 9). No vuelvas a
   preguntar lo que ya quedó fijo; confírmalo en una línea (`investigacion/04` §1.2 regla 7).
3. **Defectos fijados por el usuario** (se confirman, no se preguntan como abiertos):
   - **siempre** hay dobles en memoria de cada puerto de salida y una **prueba de contrato abstracta**
     que heredan la implementación en memoria y la real. Están definidos en `caso-de-uso` T8 y T9 y en
     `adaptador-persistencia` §10.6 y §9.7. **Enlázalos; no los dupliques**;
   - Java 21 y Spring Boot por defecto; JUnit Jupiter (la API `org.junit.jupiter` es la misma en JUnit 5 y
     en JUnit 6, que es la que trae Spring Boot 4.1) y AssertJ;
   - el tiempo entra por un `java.time.Clock` inyectado y los identificadores por un generador inyectable
     (`investigacion/02` #21).
4. **Las pruebas se derivan de la especificación, no del código.** Si el código ya existe, no leas la
   implementación para decidir qué afirmar; parte de los casos de la ESPEC (`investigacion/05` §6, fila
   «Pruebas que confirman la implementación»).
5. Haz las preguntas de §5 en orden, 10 por ronda: P1–P10 en la primera, P11–P20 en la siguiente (SKILL.md, etapa 3), **una decisión por pregunta**, cada una con su línea de «por
   qué», sus opciones (la ★ primero) y un ejemplo en Java 21 por opción. Aplica la poda de §6 en cuanto
   una respuesta la active.
6. **No te salgas de las pruebas.** Si una pregunta revela un hueco del elemento (una regla sin dueño, un
   error sin nombre, un puerto que falta), anótalo como pendiente `Q-n` para la plantilla de origen y
   sigue.
7. **Reparte por responsabilidades, no por porcentajes.** La proporción de la pirámide es una
   consecuencia: cada regla se prueba en el nivel más bajo que pueda detectarla (Vocke, «push your tests
   as far down the test pyramid as you can»; Hombergs, cap. «Testing Architecture Elements»). Si la ronda
   sale con más pruebas de punta a punta que de dominio, señálalo como antipatrón (§8, cono de helado).
8. Ofrece código solo desde §10. Si copias de §9, respeta la licencia indicada; buckpal y
   `sttp-book/code-examples` no declaran licencia: solo se leen.
9. Al terminar, marca §7, calcula la cobertura y lista los pendientes. Cierra con la metapregunta «¿hay
   algún fallo que te preocupe y que ninguna de estas pruebas detectaría?» si queda cupo.

## 1 · Qué se define primero

El orden sigue la dependencia: no se elige un doble sin saber qué es la unidad, ni una herramienta sin
saber el nivel. Los tres primeros son esenciales: si alguno queda sin responder, la cobertura se limita a
70 (`investigacion/04` §5.1, regla dura).

| ⟦k⟧ | Concepto | Por qué va aquí | Fuente |
|---|---|---|---|
| 1 | **Comportamientos a proteger** (los casos dado/cuando/entonces de la ESPEC, con su id) | Una prueba protege un comportamiento observable; sin la lista no hay criterio para decidir qué falta ni qué sobra. | Beck, *TDD by Example* («test list»); Khorikov, cap. 4 y 5 (comportamiento observable frente a detalle de implementación); `investigacion/05` §2.4 |
| 2 | **Qué es la unidad** (escuela clásica o de Londres; prueba sociable o solitaria) | Decide qué se aísla y, con eso, cuántos dobles hay y qué tan frágiles son las pruebas. | Khorikov, cap. 2; Fowler, *UnitTest* (solitary / sociable); Fowler, *Mocks Aren't Stubs* |
| 3 | **Nivel de cada prueba en el hexágono** (tamaño y alcance) | Cada pieza del hexágono tiene su prueba natural; el nivel fija la velocidad, la fidelidad y la herramienta. | Cohn, *Succeeding with Agile*; Fowler, *TestPyramid*; Vocke; *Software Engineering at Google* (SWE), cap. 11; Hombergs, cap. 7 |
| 4 | **Dobles por frontera** (qué se sustituye y con qué tipo de doble) | Sustituir de más produce pruebas frágiles; de menos, pruebas lentas e intermitentes. | Meszaros, *xUnit Test Patterns*; SWE cap. 13; Khorikov, cap. 5 y 8 |
| 5 | **Infraestructura real en las pruebas de integración** (BD, mensajería) | Lo que solo garantiza el motor real (unicidad, versión, bloqueos, SQL) solo lo detecta el motor real. | Testcontainers; Khorikov, cap. 8 (dependencias gestionadas reales); `adaptador-persistencia` §9.6-§9.7 |
| 6 | **Contratos entre procesos** (consumidor y proveedor) | Un cambio del proveedor rompe al consumidor sin que ninguna prueba local lo vea. | Fowler, *ContractTest*; Robinson, *Consumer-Driven Contracts*; Pact; Spring Cloud Contract |
| 7 | **Reglas de arquitectura automatizadas** | La dependencia prohibida que nadie revisa se cuela; una prueba la detiene en el build. | ArchUnit; Spring Modulith, *Verifying Application Module Structure*; Ford et al., *Building Evolutionary Architectures* (funciones de aptitud) |
| 8 | **Determinismo**: reloj, aleatoriedad, ids, asincronía, concurrencia | Una prueba que a veces falla destruye la confianza en todas. | Fowler, *Eradicating Non-Determinism in Tests*; SWE cap. 11 (flaky); Beck, *Test Desiderata* («deterministic») |
| 9 | **Datos de prueba** (Object Mother, Test Data Builder, datos únicos por prueba) | Los datos opacos o compartidos producen pruebas oscuras e interdependientes. | Freeman y Pryce, *GOOS* cap. 22; Fowler, *ObjectMother*; Meszaros (*Fresh Fixture*, *Mystery Guest*) |
| 10 | **Nombres y estructura** (AAA / dado-cuando-entonces, una conducta por prueba) | La prueba es la documentación ejecutable del comportamiento; si no se lee, no se mantiene. | Wake, *3A*; North, *Introducing BDD*; Fowler, *GivenWhenThen*; SWE cap. 12; Khorikov, cap. 3 |
| 11 | **Medida de calidad de las pruebas** (mutación frente a cobertura de líneas) | La cobertura dice qué se ejecutó, no qué se comprobó. | PIT; Fowler, *TestCoverage*; Petrović et al. (Google), *Practical Mutation Testing at Scale* |
| 12 | **Ejecución y retroalimentación** (fases de Maven, etiquetas, tiempos, CI) | Las pruebas lentas se dejan de correr; las que no corren en CI no protegen nada. | SWE cap. 11 (tamaños y tiempos); Beck, *Test Desiderata* («fast»); Maven Surefire y Failsafe |

Esqueleto que se muestra antes de la P1 (los valores son la ★ provisional; dominio genérico de pedidos):

```
Elemento:       ConfirmarPedido (caso de uso) + Pedido (agregado) + PedidosJdbc + endpoint POST /pedidos/{id}:confirmar
Protege:  ⟦1⟧   CA-01 confirma con crédito · CA-02 rechaza sin crédito · CA-03 pedido vacío · CA-04 dos veces = un efecto
Unidad:   ⟦2⟧   clásica: la unidad es un comportamiento; sociable dentro del núcleo, dobles solo en los puertos
Niveles:  ⟦3⟧   dominio (pequeñas) · caso de uso con fakes (pequeñas) · contrato de puertos (memoria pequeña / real mediana)
                · web slice (mediana) · persistencia con Testcontainers (mediana) · 1-2 de sistema (mediana, alcance amplio)
Dobles:   ⟦4⟧   fakes en memoria + contrato (caso-de-uso T8-T9) · Clock.fixed · mocks solo para lo que no es nuestro
Infra:    ⟦5⟧   PostgreSQL de la misma versión que producción, en contenedor (Testcontainers 2 + @ServiceConnection)
Contrato: ⟦6⟧   ninguno (monolito modular) · si otro servicio consume: Pact del consumidor + verificación del proveedor
Arquitec: ⟦7⟧   ApplicationModules.verify() + reglas ArchUnit del hexágono (caso-de-uso T10)
Determin: ⟦8⟧   Clock.fixed · ids fijos en las pruebas · Awaitility en vez de sleep · carrera con 2 hilos y latch
Datos:    ⟦9⟧   PedidoMadre + builder con valores por defecto; ids aleatorios en las pruebas que comparten BD
Nombres:  ⟦10⟧  clase <Sujeto>Test / <Sujeto>IT · método en frase de negocio · bloques // dado // cuando // entonces
Calidad:  ⟦11⟧  PIT en dominio y aplicación: ≥ 80 % de mutantes muertos [opinión]; la cobertura de líneas solo se mira
Ejecución:⟦12⟧  Surefire: pequeñas (< 1 s c/u) · Failsafe: *IT con Docker · PIT nocturno o incremental en CI
```

## 2 · Funcionamiento apropiado

### 2.1 · Qué es una buena prueba

**Las cuatro cualidades de Khorikov.** Una buena prueba unitaria tiene: (1) protección contra
regresiones, (2) resistencia a la refactorización, (3) retroalimentación rápida y (4) facilidad de
mantenimiento. Las tres primeras compiten entre sí: no se pueden maximizar a la vez. La resistencia a la
refactorización **no es negociable**, porque una prueba que se rompe cuando el comportamiento no cambió
(falso positivo) enseña al equipo a ignorar las pruebas. Lo que se negocia es el equilibrio entre
protección y velocidad, y ese equilibrio es justamente la pirámide (Khorikov, *Unit Testing Principles,
Practices, and Patterns*, cap. 4).

**Los doce deseos de Beck.** Kent Beck enumera las propiedades deseables de una prueba (*Test
Desiderata*, 2019): aislada, componible, determinista, rápida, fácil de escribir, legible, de
comportamiento (falla si el comportamiento cambia), insensible a la estructura (no falla si solo cambia
la estructura), automática, específica (cuando falla, la causa es obvia), predictiva (si todas pasan, el
código sirve en producción) e inspiradora (pasar da confianza). Beck aclara que ninguna propiedad es
absoluta: son compensaciones entre ellas.

**Tres reglas de Google** (SWE, cap. 12): prueba **por la API pública**, no por los detalles; prueba
**estado, no interacciones**; prueba **comportamientos, no métodos** (una prueba por conducta, no una por
método). Y una aspiración: las pruebas no deberían tener que cambiar salvo cuando cambia el
comportamiento («strive for unchanging tests»).

**Dos escuelas** (Fowler, *Mocks Aren't Stubs*; Khorikov, cap. 2):

| | Escuela clásica (Detroit, Chicago) | Escuela de Londres (mockista) |
|---|---|---|
| La unidad es | un comportamiento (una o varias clases) | una clase |
| Se aísla | la prueba de las demás pruebas (estado compartido, E/S) | la clase de sus colaboradores |
| Dobles para | dependencias compartidas o fuera del proceso | todo colaborador mutable |
| Verificación | estado y resultado | interacciones (qué se llamó) |
| Riesgo | una falla rompe varias pruebas | sobreespecificación: refactorizar rompe pruebas |
| Libro de referencia | Beck, *TDD by Example*; Khorikov | Freeman y Pryce, *GOOS* |

★ En esta skill: **escuela clásica en el núcleo** (sociable: el caso de uso real con el agregado real y
fakes en los puertos) y **dobles de interacción solo en la frontera con lo que no es nuestro**
(Khorikov, cap. 8-9: los mocks solo para las dependencias fuera del proceso **no gestionadas**, cuya comunicación es visible desde afuera). Es lo que permite el
defecto fijado por el usuario (fake + contrato). [opinión, alineada con Khorikov y con SWE cap. 13: «A real implementation should be preferred over a
test double»]

### 2.2 · Formas de la cartera: pirámide, trofeo, panal

| Forma | Autor | Capas (de abajo arriba) | Qué afirma | Crítica o matiz |
|---|---|---|---|---|
| **Pirámide** | Mike Cohn, *Succeeding with Agile* (2009); popularizada por Fowler, *TestPyramid* (2012) | unidad · servicio · interfaz de usuario | muchas pruebas pequeñas y rápidas, menos de servicio, muy pocas de UI | los nombres de las capas son ambiguos (Vocke); «unidad» significa cosas distintas (Fowler 2021) |
| **Pirámide práctica** | Ham Vocke, *The Practical Test Pyramid* (2018) | unidad · integración · contrato · UI · punta a punta | escribe muchas pruebas pequeñas y rápidas, algunas más gruesas y muy pocas de alto nivel; empuja cada prueba lo más abajo posible; evita duplicar | — |
| **Cono de helado** (antipatrón) | Alister Scott (2012) | pocas unitarias, muchas de UI y manuales | — | lento, frágil, caro; es lo que hay que evitar |
| **Trofeo** | Kent C. Dodds (2018; *The Testing Trophy and Testing Classifications*, 2021) | estáticas · unitarias · **integración (la más ancha)** · punta a punta | «Write tests. Not too many. Mostly integration.» (cita de Guillermo Rauch); la confianza crece al parecerse al uso real | pensado para *frontend* en JavaScript; su «integración» se parece a la «unitaria sociable» de la escuela clásica (Fowler 2021) |
| **Panal** | Spotify, André Schaffer y Rickard Dybeck, *Testing of Microservices* (2018) | detalles de implementación (pocas) · **integración (la mayoría)** · integradas (pocas) | en microservicios casi sin lógica, la complejidad está en la interacción; probar el servicio por sus contratos | aplica a servicios pequeños con poca lógica de dominio |
| **Tamaño y alcance** | Google, SWE cap. 11 | pequeñas · medianas · grandes (por recursos) × estrecho · medio · amplio (por código) | proporción orientativa de ~80 % estrecho, ~15 % medio y ~5 % amplio | separar «cuánto cuesta correrla» de «cuánto código valida» resuelve buena parte del debate |

**La síntesis de Fowler** (*On the Diverse And Fantastical Shapes of Testing*, 2021): la mayor parte del
desacuerdo entre la pirámide y el trofeo o el panal viene de **qué llama cada uno «prueba unitaria»**.
Quien dice «trofeo» suele llamar «integración» a lo que un clasicista llama «unitaria sociable». Lo
importante es lo que Fowler toma de Justin Searls: casi ningún equipo escribe pruebas expresivas, con
fronteras claras, que corran rápido y de forma fiable; «Focus on that instead».

**[opinión] Para un núcleo hexagonal con dominio rico** (el caso de esta skill), la pirámide clásica con
pruebas sociables es la forma natural: las reglas viven en el dominio y en los casos de uso, que se
prueban sin infraestructura. El panal aplica a un servicio sin dominio (un adaptador que solo traduce),
donde casi todo el riesgo está en la integración.

### 2.3 · La pirámide mapeada al hexágono

Tamaño según Google (SWE cap. 11): **pequeña** = un proceso y un hilo, sin red, disco, `sleep` ni
reloj real; **mediana** = una máquina, puede usar `localhost`, varios procesos y una BD en contenedor;
**grande** = varias máquinas o servicios remotos. Alcance = cuánto código ejecuta la prueba.

```
                                  ▲ pocas · lentas · alta fidelidad
                    ┌──────────────────────────┐
                    │  SISTEMA / PUNTA A PUNTA │ @SpringBootTest(RANDOM_PORT) + Testcontainers
                    │  1-3 caminos críticos    │ mediana · alcance amplio            (§9.2, T13)
                ┌───┴──────────────────────────┴───┐
                │  CONTRATOS ENTRE PROCESOS        │ Pact consumidor / verificación proveedor,
                │  (solo si hay otro servicio)     │ Spring Cloud Contract   (§9.10, endpoint §9.9-9.10)
            ┌───┴──────────────────────────────────┴───┐
            │  ADAPTADORES (integración enfocada)      │ web: @WebMvcTest · BD: @JdbcTest/@DataJpaTest
            │  + MÓDULO (@ApplicationModuleTest)       │ + Testcontainers · HTTP: servidor simulado
            │  + subclase REAL del contrato de puertos │ mediana · alcance medio  (§9.1, §9.5, §9.6, T6-T9)
        ┌───┴──────────────────────────────────────────┴───┐
        │  APLICACIÓN: caso de uso con fakes + Clock.fixed │ sin Spring, < 1 s, sociable
        │  + subclase EN MEMORIA del contrato de puertos   │ pequeña · alcance estrecho (caso-de-uso T8-T10)
    ┌───┴──────────────────────────────────────────────────┴───┐
    │  DOMINIO: agregados, valores, políticas                   │ JUnit puro, sin dobles; ejemplos +
    │  (ejemplos y propiedades)                                 │ propiedades (jqwik)  (§9.1, §9.3, §9.7, T1-T2)
    └───────────────────────────────────────────────────────────┘
      + REGLAS DE ARQUITECTURA (ArchUnit, Modulith verify): pequeñas, transversales   (§9.4, §9.5, T11)
      + CALIDAD DE LAS PRUEBAS (PIT, mutación): mide las capas de dominio y aplicación   (§9.8, T14)
                                  ▼ muchas · rápidas · aisladas
```

| Pieza del hexágono | Qué se prueba | Prueba y herramienta | Dobles | Tamaño · alcance | Detalle en |
|---|---|---|---|---|---|
| Valor, entidad, agregado | invariantes, transiciones, cálculos, eventos registrados | JUnit + AssertJ; `@ParameterizedTest`; jqwik para invariantes | ninguno | pequeña · estrecho | `entidad-agregado` §11; aquí T1-T2 |
| Caso de uso (servicio de aplicación) | orquestación, reglas que necesitan datos externos, errores con nombre, idempotencia | JUnit, el servicio construido con `new` | fakes en memoria de cada puerto de salida + `Clock.fixed` | pequeña · estrecho | `caso-de-uso` T10 |
| Puerto de salida | que la memoria y la real se comporten igual | prueba de contrato abstracta + 2 subclases | — (la subclase real usa infraestructura) | memoria: pequeña · real: mediana | `caso-de-uso` T9; `adaptador-persistencia` §10.6 |
| Adaptador de entrada web | ruta, deserialización, validación, códigos HTTP, `ProblemDetail` | `@WebMvcTest` + `MockMvc` (o `MockMvcTester`) | el puerto de entrada: `@MockitoBean` o una lambda | mediana · medio | `endpoint` §9.2; aquí T6 |
| Adaptador de salida BD | mapeo, SQL, versión, unicidad, migraciones | `@JdbcTest` o `@DataJpaTest` + Testcontainers | ninguno: motor real | mediana · medio | `adaptador-persistencia` §10.6; aquí T7 |
| Adaptador de salida HTTP | traducción de la respuesta, errores, tiempo de espera | servidor simulado (WireMock, `MockRestServiceServer`) + Pact del consumidor | el servidor remoto | mediana · medio | aquí T8-T9 |
| Adaptador de mensajería, listener | publicación tras el commit, consumo, deduplicación | `@ApplicationModuleTest` + `Scenario`; Testcontainers del broker | — | mediana · medio | `evento-mensaje` T6 |
| Módulo de Modulith | el módulo arranca solo y cumple su API de eventos | `@ApplicationModuleTest` | `@MockitoBean` de lo que viene de otros módulos | mediana · medio | aquí §9.5, T10 |
| Arquitectura | dependencias permitidas, ciclos, estereotipos | ArchUnit; `ApplicationModules.verify()` | — | pequeña · amplio (estático) | `caso-de-uso` T10; aquí T11 |
| Sistema | 1-3 caminos críticos de punta a punta y un fallo crítico | `@SpringBootTest(webEnvironment = RANDOM_PORT)` + Testcontainers | nada del sistema; sí lo externo que no es nuestro | mediana · amplio | aquí §9.2, T13 |

### 2.4 · Flujo de trabajo: de afuera hacia adentro, en doble lazo

Freeman y Pryce (*GOOS*, cap. 1 y 4) proponen un **doble lazo**: primero una prueba de aceptación que
falla (lazo externo, el comportamiento visible); luego, dentro, ciclos rojo-verde-refactorizar de
pruebas unitarias (lazo interno, Beck) hasta que la de aceptación pasa. Empiezan por un **esqueleto
andante** (*walking skeleton*): la rebanada mínima que atraviesa todas las capas y se despliega, para
que la infraestructura de pruebas exista desde el día uno.

```
ESPEC (dado/cuando/entonces) ──▶ prueba de aceptación ROJA (caso de uso con fakes, o sistema)
                                   │
                                   ├─▶ rojo: prueba de dominio ──▶ verde ──▶ refactorizar ─┐
                                   │   (repetir por regla, Beck: lista de pruebas)         │
                                   ◀───────────────────────────────────────────────────────┘
                                   ▼
                                 aceptación VERDE ──▶ adaptadores con su prueba de integración
                                   ──▶ subclase real del contrato ──▶ PIT sobre lo nuevo
```

[opinión] En esta skill la **prueba de aceptación por defecto es la del caso de uso con fakes**
(`caso-de-uso` T10): ejecuta el comportamiento completo del núcleo, es pequeña y no depende de la web.
La de sistema se reserva para los 1-3 caminos críticos (Hombergs, cap. 7: «system tests» para los
caminos más importantes).

### 2.5 · Dónde viven las pruebas

| Tipo | Ubicación | Sufijo | Fase de Maven |
|---|---|---|---|
| Pequeñas (dominio, caso de uso, contrato en memoria, arquitectura) | `src/test/java/<paquete del sujeto>` | `…Test` | `test` (Surefire) |
| Medianas con Docker (adaptadores, contrato real, módulo, sistema) | `src/test/java/<paquete del sujeto>` | `…IT` | `integration-test` y `verify` (Failsafe) |
| Dobles en memoria y contratos abstractos | `src/test/java/<módulo>/application/port/out` | `…EnMemoria`, `…ContratoTest` | — |
| Madres y builders de datos | `src/test/java/<módulo>/…/testdata` o junto al sujeto | `…Madre`, `…Builder` | — |
| Propiedades (jqwik) | junto a la prueba de ejemplos del sujeto | `…Propiedades` | `test` |

Convención de sufijos: Surefire incluye `**/*Test.java` y Failsafe `**/*IT.java` por defecto
(documentación de los plugins de Maven). [opinión] Las medianas sin Docker (`@WebMvcTest`) pueden ir en
Surefire.

## 3 · Patrones de diseño

### 3.1 · Dobles de prueba (Meszaros, *xUnit Test Patterns*; Fowler, *Mocks Aren't Stubs*)

| Doble | Qué es | Úsalo cuando | No lo uses cuando | Ejemplo |
|---|---|---|---|---|
| **Dummy** | se pasa para llenar un parámetro; nunca se usa | la firma exige un argumento irrelevante para la conducta | el argumento sí influye | `new ConfirmarPedidoServicio(pedidos, credito, eventos, null /* sin uso en esta rama */)` [opinión: mejor un valor real] |
| **Stub** | devuelve respuestas preparadas; no verifica | hay que forzar una entrada indirecta (un dato, un fallo) | se quiere comprobar qué se envió | `FindAvailableBook willNotFindBook = { id -> Option.none() }` (library §9.3) |
| **Spy** | stub que además registra cómo lo llamaron | se quiere ver una salida indirecta sin un framework | — | `EventosCapturados.publicados()` (`caso-de-uso` T8); `OutputTracker` (yacht-tdd §9.9) |
| **Mock** | se programa con expectativas y falla si no se cumplen | la conducta **es** la interacción con algo que no es nuestro (enviar un correo, una API de pago) | el colaborador es nuestro o tiene estado consultable: sobreespecifica | `then(notificador).should().enviar(...)`; `1 * cancelingHold.cancelHold(cmd)` (library §9.12) |
| **Fake** | implementación funcional con un atajo (memoria en vez de BD) | ★ cada puerto de salida (decisión del usuario) | sin prueba de contrato: el fake miente | `PedidosEnMemoria` (`adaptador-persistencia` §10.6) |

Orden de preferencia de Google (SWE cap. 13): **implementación real → fake → stub → prueba de
interacción**. Las pruebas de interacción solo cuando el resultado no se puede observar de otro modo o
la interacción misma es el comportamiento. Los fakes **deben tener sus propias pruebas** para asegurar
fidelidad con la real; eso es exactamente la prueba de contrato del usuario.

### 3.2 · Catálogo de patrones de prueba

| Patrón | Problema que resuelve | Úsalo cuando | No lo uses cuando | Fuente |
|---|---|---|---|---|
| Prueba en cuatro fases | estructurar preparación, ejercicio, verificación y limpieza | siempre (la limpieza suele ser implícita) | — | Meszaros, *Four-Phase Test* |
| Arrange-Act-Assert / Dado-Cuando-Entonces | leer la prueba como una especificación | ★ siempre; un solo «cuando» por prueba | varias acciones encadenadas: parte en pruebas | Wake, *3A* (2001); North, *Introducing BDD* (2006); Fowler, *GivenWhenThen* |
| Fixture fresca | que cada prueba cree lo que necesita y no dependa de otras | ★ por defecto | el costo de crear es enorme (contenedor): compártelo, pero no sus datos | Meszaros, *Fresh Fixture* / *Shared Fixture* |
| Object Mother | dar nombre de negocio a objetos de prueba típicos | ★ estados con nombre (`PedidoMadre.confirmado()`) | muchas variantes pequeñas: crece sin control | Fowler, *ObjectMother*; library `PatronFixture` (§9.3) |
| Test Data Builder | construir variantes cambiando solo lo relevante | ★ junto a la madre, para las variantes | objetos triviales (un `record` de 2 campos) | Freeman y Pryce, *GOOS* cap. 22; buckpal `AccountTestData` (§9.1) |
| Prueba parametrizada | la misma regla con varias filas de datos | tablas de ejemplos de la ESPEC (`Scenario Outline`) | cada fila necesita otra preparación | JUnit `@ParameterizedTest`; Spock `where:` |
| Prueba de propiedades | encontrar el contraejemplo que nadie imaginó | invariantes, ida y vuelta (serializar/deserializar), modelos de referencia | la regla solo se define por ejemplos puntuales | jqwik User Guide; Claessen y Hughes, *QuickCheck* (2000) |
| Prueba con estado (basada en modelo) | secuencias de operaciones que rompen el agregado | máquinas de estados con muchas transiciones | 2-3 transiciones: bastan ejemplos | jqwik `ActionChain` (§9.7) |
| Prueba de contrato de un doble | que el fake se porte como la real | ★ siempre que hay un fake (decisión del usuario) | — | Rainsberger, *Contract tests*; SWE cap. 13 |
| Contrato dirigido por el consumidor | que el proveedor no rompa a sus consumidores | hay otro proceso que consume la API o el mensaje | monolito modular sin consumidores externos | Robinson (2006); Pact; Spring Cloud Contract |
| Objeto humilde | sacar la lógica de lo difícil de probar (controlador, hilo, temporizador) | adaptadores con lógica de traducción | el adaptador no tiene lógica | Meszaros, *Humble Object*; Khorikov, cap. 7 |
| Nullables (infraestructura anulable) | probar sociable sin mocks: la clase de infraestructura trae su versión «apagada» | se quiere evitar fakes y mocks y se acepta código de prueba en producción | se prefiere núcleo puro con puertos (★ aquí) | Shore, *Testing Without Mocks* (2018-2023); yacht-tdd (§9.9) |
| Rebanada de Spring (*test slice*) | levantar solo la capa que se prueba | adaptadores web y de persistencia | el sistema completo | Spring Boot, *Testing* (`@WebMvcTest`, `@JdbcTest`, `@DataJpaTest`) |
| Contenedor compartido | no pagar el arranque de Docker en cada clase | varias clases `…IT` con la misma BD | la prueba altera el esquema | Testcontainers, *Singleton containers*; Spring Boot `@ServiceConnection` |
| Reloj controlable | fijar o adelantar el tiempo | toda regla con fechas, vencimientos o plazos | — | `java.time.Clock`; Khorikov, cap. 11 («working with time»); Modulith `TimeMachine` (§9.12) |
| Espera por condición | esperar algo asíncrono sin `sleep` fijo | eventos, listeners, trabajos | el código es síncrono | Fowler, *Eradicating Non-Determinism*; Awaitility; Modulith `Scenario` |
| Esqueleto andante | tener la cadena de pruebas desde el primer día | arranque de un módulo nuevo | el módulo ya tiene infraestructura de pruebas | Freeman y Pryce, *GOOS* cap. 4 |
| Prueba de mutación | medir si las pruebas detectan cambios del código | dominio y aplicación | adaptadores triviales, código generado | PIT; Petrović et al. (Google, 2021) |
| Función de aptitud de arquitectura | volver ejecutable una regla de diseño | dependencias entre capas y módulos | reglas de estilo (para eso el *linter*) | Ford, Parsons y Kua, *Building Evolutionary Architectures*; ArchUnit |

**Decisiones de patrón que cierra la ronda:**
- escuela y unidad (clásica sociable ★ o Londres): P2;
- forma de la cartera (pirámide ★, trofeo o panal): P3;
- propiedades además de ejemplos en el dominio: P4;
- tipo de doble por frontera (fake + contrato ★ fijo; mock solo para lo ajeno): P5-P6;
- madre + builder ★ o fixtures compartidas: P7;
- base de datos real en contenedor ★ o en memoria (H2): P10;
- contratos entre procesos (ninguno, Pact o Spring Cloud Contract): P12;
- reglas de arquitectura (Modulith ★ + ArchUnit): P15;
- medida de calidad (mutación ★ o solo cobertura): P18.

## 4 · Prácticas profesionales verificables

Severidad: C = crítica (3), M = mayor (2), m = menor (1). Verificación: A = ArchUnit, P = prueba o
ejecución (incluye PIT y el build), L = lectura. Alimentan el 25 % de adherencia (`investigacion/02` §3).

| id | Práctica | Sev. | Al detallar | Al verificar | Fuente |
|---|---|---|---|---|---|
| PB-01 | Cada caso dado/cuando/entonces de la ESPEC tiene al menos una prueba con su id, y cada prueba lleva a un caso o a una decisión declarada | C | P1 | L: matriz requisito → prueba (`investigacion/05` §2.4) | `investigacion/05` §4.1 ítems 1-2; CMMI REQM SP1.4 |
| PB-02 | Cada regla tiene un par de pruebas: una que la cumple y otra que la viola | C | P1 | L; P: PIT no deja mutantes vivos en la condición | Example Mapping; `caso-de-uso` U-06 |
| PB-03 | La prueba falla si se rompe el comportamiento: aserciones sobre resultado o estado observable, nunca una prueba sin aserción | C | P6 | P: PIT mata los mutantes de la regla | Khorikov cap. 4; `investigacion/05` §4.1 ítem 9 |
| PB-04 | No hay mock del sujeto bajo prueba ni del agregado que el caso de uso usa | C | P5-P6 | L: buscar `mock(Pedido.class)`, `spy(servicio)` | Khorikov cap. 5; `caso-de-uso` §9.2 «qué no copiar» |
| PB-05 | Cada puerto de salida tiene fake en memoria + prueba de contrato heredada por la memoria y la real | C | P5 | P: existe `XContratoTest` con 2 subclases | [decisión del usuario]; SWE cap. 13; `caso-de-uso` U-20 |
| PB-06 | Las pruebas de dominio y de caso de uso corren sin Spring, sin BD y sin red, en < 1 s cada clase | C | P3-P5 | P: sin `@SpringBootTest` ni `@Testcontainers` en `domain` y `application`; tiempo en el informe de Surefire | SWE cap. 11 (pequeñas); `caso-de-uso` U-21 |
| PB-07 | Los mocks solo se usan para dependencias fuera del proceso que no son nuestras (correo, pasarela, API de terceros) | M | P6 | L: contar `mock(`/`@MockitoBean` y su tipo | Khorikov cap. 8; Freeman y Pryce («only mock types that you own») |
| PB-08 | Se verifica estado o resultado, no el orden de las llamadas; `verify` solo para la salida que es el comportamiento | M | P6 | L: pocas `verify`/`then(...).should()` | Fowler, *Mocks Aren't Stubs*; SWE cap. 12 |
| PB-09 | Nada en la prueba ni en el código usa el reloj del sistema, `Random` sin semilla ni `UUID.randomUUID()` sin inyectar | C | P8 | A: `application`/`domain` no llaman `Instant.now()`, `LocalDateTime.now()`, `UUID.randomUUID()`; L en pruebas | Fowler, *Eradicating Non-Determinism*; Khorikov cap. 11; `investigacion/02` #21 |
| PB-10 | Ninguna prueba espera con `Thread.sleep` fijo: espera por condición con tiempo máximo | M | P13 | L/A: buscar `Thread.sleep` en `src/test` | Fowler, *Eradicating Non-Determinism*; Awaitility |
| PB-11 | La BD de las pruebas de integración es el mismo motor y versión mayor que producción (contenedor) | M | P10 | L: imagen `postgres:<versión de prod>` | Testcontainers; Vocke (advierte que con H2 las pruebas corren «against a different type of database»); Khorikov cap. 10 |
| PB-12 | Cada adaptador tiene su prueba de integración enfocada (rebanada) y la subclase real del contrato | M | P9-P11 | P: existe `…IT` por adaptador | Hombergs cap. 7; `caso-de-uso` U-16 |
| PB-13 | Las pruebas de sistema son pocas (1-3 caminos críticos y un fallo crítico) y no repiten las reglas ya probadas abajo | M | P14 | L: contar `@SpringBootTest(RANDOM_PORT)` | Vocke («avoid test duplication»); Fowler, *TestPyramid* |
| PB-14 | Las reglas de arquitectura corren en el build: `ApplicationModules.verify()` y reglas ArchUnit | M | P15 | P: la prueba existe y corre en `mvn verify` | Spring Modulith; ArchUnit; `caso-de-uso` U-24 |
| PB-15 | Las pruebas son independientes: ninguna depende del orden ni de los datos de otra | C | P7, P19 | P: correr en orden aleatorio (`junit.jupiter.testmethod.order.default=…Random`) y en paralelo | Meszaros (*Interacting Tests*); Beck, *Test Desiderata* («isolated») |
| PB-16 | Los datos se crean con madres y builders de nombre de negocio; no hay «invitados misteriosos» (archivos SQL o JSON que la prueba no muestra) para la conducta que se afirma | m | P7 | L | Meszaros (*Mystery Guest*); Freeman y Pryce cap. 22 |
| PB-17 | Nombre en frase de negocio que dice la conducta y el resultado; estructura dado/cuando/entonces visible; un solo «cuando» | m | P17 | L | SWE cap. 12; Khorikov cap. 3; North |
| PB-18 | Sin lógica en las pruebas (`if`, bucles que calculan el esperado); el valor esperado es literal | m | P17 | L | SWE cap. 12 («don't put logic in tests»); Meszaros (*Conditional Test Logic*) |
| PB-19 | Las pruebas no se debilitan para pasar: sin `@Disabled` sin pendiente, sin aserciones borradas, sin `catch` vacío | C | P20 | L: buscar `@Disabled`, `assumeTrue(false)` | `investigacion/05` §4.1 ítem 10 |
| PB-20 | La calidad se mide con mutación (PIT) en dominio y aplicación; la cobertura de líneas no es meta | M | P18 | P: `pitest:mutationCoverage` con umbral | PIT; Fowler, *TestCoverage*; Petrović et al. (2021) |
| PB-21 | Las carreras que la ESPEC menciona tienen una prueba con hilos reales que fuerza el entrelazado (latch o barrera) y afirma el invariante | C si aplica | P16 | P: `…ConcurrenciaIT` | Goetz, *JCIP* cap. 12; `investigacion/05` §3.3 (OBS de ejemplo) |
| PB-22 | Las pruebas intermitentes se corrigen o se ponen en cuarentena con pendiente y dueño; no se reintentan hasta que pasen | M | P20 | L: no hay `rerunFailingTestsCount` sin pendiente | Fowler, *Eradicating Non-Determinism* (cuarentena); SWE cap. 11 |
| PB-23 | Contratos entre procesos versionados y verificados en el build del proveedor | M si aplica | P12 | P: la verificación del proveedor corre en CI | Pact; Spring Cloud Contract; Fowler, *ContractTest* |
| PB-24 | Las pruebas pequeñas van en Surefire y las de Docker en Failsafe; el build local corre las pequeñas en segundos | m | P19 | P: tiempos del informe | Maven Surefire/Failsafe; SWE cap. 11 |
| PB-25 | No hay código solo para pruebas en producción (getters, `setX` «para el test») salvo Nullables declarados | m | P5 | L | Khorikov cap. 11 (*code pollution*); Meszaros (*Test Logic in Production*) |
| PB-26 | No se prueban métodos privados; se prueban por la API pública del sujeto | m | P2 | L: sin reflexión ni `@VisibleForTesting` | Khorikov cap. 11; SWE cap. 12 |
| PB-27 | El contexto de Spring se reutiliza: misma configuración entre clases, sin `@DirtiesContext` salvo motivo escrito | m | P19 | L: contar `@DirtiesContext` | Spring Framework, *Context Caching* |

## 5 · Árbol de preguntas

Formato (`investigacion/04` §2.2): título de una línea + contexto pequeño o mediano con el **por qué** (SKILL.md, «Cómo preguntas») · opciones con la ★ primero y
un ejemplo cada una · **cierra** (concepto de §1 o práctica de §4). Las 20 son fijas.

### A · Qué se protege y con qué forma

**P1. ¿Qué comportamientos de la ESPEC protegen estas pruebas, con su id?**
Por qué: sin lista no se sabe qué falta ni qué sobra (Beck: lista de pruebas).
1. ★ Un caso por regla cumple/viola + uno por error + idempotencia — `CA-01 confirmaConCredito · CA-02 rechazaSinCredito · CA-05 dosVecesUnEfecto`
2. Solo el camino feliz — se acepta con un pendiente `Q-n` por regla sin su par
3. Lista desde el código existente (se rechaza) — «probar cada método público de `PedidoServicio`»
Cierra: ⟦1⟧, PB-01, PB-02.

**P2. ¿Qué es la «unidad» en estas pruebas?**
Por qué: decide cuánto se aísla y qué tan frágiles serán las pruebas (Khorikov, cap. 2).
1. ★ Un comportamiento (clásica, sociable) — `new ConfirmarPedidoServicio(new PedidosEnMemoria(), …)` con el `Pedido` real
2. Una clase (Londres, solitaria) — `mock(Pedido.class)`, `mock(Pedidos.class)` y `verify` de cada llamada
3. Mixta por capa — sociable en el núcleo; solitaria solo en un adaptador con lógica (objeto humilde)
Cierra: ⟦2⟧, PB-04, PB-26.

**P3. ¿Qué forma tendrá la cartera de pruebas?**
Por qué: la forma es consecuencia de dónde está el riesgo (Fowler 2021; Spotify).
1. ★ Pirámide con pruebas sociables — dominio y casos de uso pequeños; 1 IT por adaptador; 1-3 de sistema
2. Panal (servicio casi sin dominio) — la mayoría `@SpringBootTest` + Testcontainers del servicio por su API
3. Trofeo — integración la más ancha; aquí equivale a la ★ si «integración» = caso de uso con fakes
Cierra: ⟦3⟧, PB-13.

### B · Núcleo: dominio y casos de uso

**P4. ¿El dominio se prueba solo con ejemplos o también con propiedades?**
Por qué: los ejemplos prueban lo que imaginaste; las propiedades buscan el contraejemplo (jqwik).
1. ★ Ejemplos + propiedades para invariantes — `@Property void totalNuncaNegativo(@ForAll("lineas") List<Linea> l)`
2. Solo ejemplos, parametrizados — `@ParameterizedTest @CsvSource({"480000,500000,CONFIRMA", "400000,500000,RECHAZA"})`
3. Con estado (secuencias de operaciones) — `ActionChain.startWith(PedidoMadre::borrador).withAction(agregarLinea())…`
Cierra: ⟦3⟧, PB-02.

**P5. Confirmo: cada puerto de salida tiene fake en memoria y contrato heredado. ¿Algún puerto distinto?**
Por qué: es la decisión fija del usuario; solo cambia la subclase real (`caso-de-uso` T8-T9).
1. ★ Todos con fake + contrato — `PedidosContratoTest` ← `PedidosEnMemoriaContratoTest`, `PedidosJdbcContratoIT`
2. Puerto que envuelve algo ajeno sin estado (correo) — fake espía + contrato mínimo con servidor simulado
3. Nullable del adaptador en vez de fake (Shore) — `NotificadorCorreo.createNull()`; se registra como decisión
Cierra: ⟦4⟧, PB-05, PB-25.

**P6. ¿Qué afirma la prueba del caso de uso?**
Por qué: verificar interacciones sobreespecifica; el estado sobrevive a la refactorización (Khorikov cap. 4).
1. ★ Resultado + estado en los fakes + eventos capturados — `assertThat(r).isEqualTo(...)`; `assertThat(eventos.publicados()).singleElement()…`
2. Interacción solo con lo ajeno — `assertThat(correo.enviados()).containsExactly(...)` o `then(pasarela).should().cobrar(...)`
3. Orden de llamadas a cada colaborador (se rechaza) — `InOrder o = inOrder(repo, credito); o.verify(repo).porId(...)`
Cierra: ⟦4⟧, PB-03, PB-07, PB-08.

**P7. ¿Cómo se crean los datos de prueba?**
Por qué: datos opacos o compartidos producen pruebas oscuras e interdependientes (Meszaros).
1. ★ Madre con estados de negocio + builder para variantes — `PedidoMadre.confirmado()`, `unPedido().conTotal(cop(480_000)).build()`
2. Constructores literales en cada prueba — `new Pedido(new PedidoId(...), List.of(new Linea(...)), ...)`
3. Archivo SQL o JSON compartido — `@Sql("datos.sql")`; solo para lecturas complejas del adaptador
Cierra: ⟦9⟧, PB-15, PB-16.

**P8. ¿Cómo se controlan el tiempo, los ids y lo aleatorio?**
Por qué: el reloj real y lo aleatorio vuelven intermitentes las pruebas (Fowler; Khorikov cap. 11).
1. ★ `Clock` y generador inyectados; fijos en la prueba — `Clock.fixed(Instant.parse("2026-01-10T12:00:00Z"), UTC)`; `() -> PedidoId.de("…c1")`
2. Reloj que avanza (plazos, vencimientos) — `RelojMovible reloj; reloj.avanzar(Duration.ofDays(3))` (T5) o Modulith `TimeMachine.shiftBy`
3. Tiempo pasado como valor al método — `pedido.vencer(ahora)` sin reloj en el agregado
Cierra: ⟦8⟧, PB-09.

### C · Adaptadores e integración

**P9. ¿Cómo se prueba el adaptador de entrada web?**
Por qué: ruta, JSON, validación y errores solo se ven con la capa web real (`endpoint` §9.2).
1. ★ `@WebMvcTest` + puerto como `@MockitoBean` o lambda — `given(confirmar.ejecutar(any())).willReturn(resultado)`; `status().isOk()`
2. Standalone sin Spring (`MockMvcBuilders.standaloneSetup`) — más rápido; no prueba la configuración de Spring
3. Solo desde la prueba de sistema — pierde los casos de error; se acepta si el adaptador no tiene lógica
Cierra: ⟦3⟧, PB-12.

**P10. ¿Contra qué base de datos corren las pruebas del adaptador de persistencia?**
Por qué: unicidad, versión, bloqueos y SQL propio solo los reproduce el motor real.
1. ★ PostgreSQL en contenedor, versión de producción — `@Container @ServiceConnection static PostgreSQLContainer pg = new PostgreSQLContainer("postgres:16")`
2. URL JDBC de Testcontainers — `spring.datasource.url=jdbc:tc:postgresql:16-alpine:///pedidos` (§9.6)
3. H2 en memoria (se desaconseja) — dialecto distinto: `ON CONFLICT`, `FOR UPDATE SKIP LOCKED` difieren
Cierra: ⟦5⟧, PB-11, PB-12.

**P11. ¿Cómo se prueba el adaptador que llama a un HTTP externo?**
Por qué: la traducción de errores y los tiempos de espera fallan en producción, no en el fake.
1. ★ Subclase real del contrato contra un servidor simulado — `WireMock` responde `200`, `404`, `503` y un retraso mayor que el `timeout`
2. `MockRestServiceServer` sobre el `RestClient` — `server.expect(requestTo("/creditos/c1")).andRespond(withSuccess(json, APPLICATION_JSON))`
3. Contra el servicio real de pruebas del tercero — prueba grande; solo manual o nocturna
Cierra: ⟦4⟧ ⟦5⟧, PB-05, PB-12.

**P12. ¿Hay otro proceso que consuma o provea esta API o este mensaje?**
Por qué: sin contrato, el cambio del proveedor rompe al consumidor en producción (Fowler, *ContractTest*).
1. ★ No: monolito modular — los módulos se protegen con `@ApplicationModuleTest` y `verify()`; sin Pact
2. Sí, nosotros consumimos — Pact del consumidor: `@PactTestFor(providerName = "Creditos")` + archivo de contrato publicado
3. Sí, nosotros proveemos — Spring Cloud Contract (contratos → pruebas generadas) o verificación Pact del proveedor
Cierra: ⟦6⟧, PB-23.

**P13. ¿Hay comportamiento asíncrono (eventos tras el commit, listeners, trabajos)?**
Por qué: lo asíncrono sin espera por condición es la primera causa de pruebas intermitentes (Fowler).
1. ★ Sí, eventos de Modulith — `scenario.stimulate(() -> pedidos.confirmar(id)).andWaitForEventOfType(PedidoConfirmado.class).toArrive()`
2. Sí, otro asíncrono — `await().atMost(Duration.ofSeconds(5)).until(() -> repo.porId(id).map(Pedido::estado), hasValue(ENVIADO))`
3. No — se poda (§6)
Cierra: ⟦8⟧, PB-10.

### D · Sistema, arquitectura y concurrencia

**P14. ¿Qué caminos se prueban de punta a punta?**
Por qué: son caras y lentas; duplicarlas con las reglas de abajo no agrega protección (Vocke).
1. ★ 1-3 caminos críticos + 1 fallo crítico — `POST /pedidos/{id}:confirmar` → 200 y el saldo cambió; sin crédito → 422
2. Uno por caso de uso — aceptable si hay pocos; vigilar el tiempo total
3. Uno por regla (se rechaza) — cono de helado
Cierra: ⟦3⟧, PB-13.

**P15. ¿Qué reglas de arquitectura se automatizan?**
Por qué: la dependencia prohibida que nadie revisa se cuela; la prueba la detiene (Ford et al.).
1. ★ `ApplicationModules.verify()` + reglas del hexágono — `ensureHexagonal()`, `application` no depende de `adapter` (`caso-de-uso` T10)
2. Solo ArchUnit — `onionArchitecture().domainModels("..domain..").applicationServices("..application..")…` (§9.4)
3. Solo jMolecules — `JMoleculesArchitectureRules.ensureHexagonal()` + `JMoleculesDddRules.all()`
Cierra: ⟦7⟧, PB-14.

**P16. ¿Qué carrera de la ESPEC se prueba con hilos reales?**
Por qué: la concurrencia no se ve en pruebas de un hilo; hay que forzar el entrelazado (Goetz, *JCIP*).
1. ★ Dos hilos que arrancan juntos (latch) sobre la BD real; se afirma el invariante — «2 confirmaciones → 1 éxito + 1 `ConflictoDeVersion`»
2. Entrelazado forzado por un bloqueo observable — tomar `FOR UPDATE` y esperar en `pg_locks` (spring-ai §9.11)
3. Ninguna: un solo escritor garantizado por diseño — se declara la razón (ADR corto)
Cierra: ⟦8⟧, PB-21.

### E · Calidad, estilo y ejecución

**P17. ¿Cómo se nombran y estructuran las pruebas?**
Por qué: la prueba es documentación ejecutable; el nombre debe decir la conducta (SWE cap. 12).
1. ★ Frase de negocio + bloques dado/cuando/entonces — `void pedidoSinCreditoSeRechaza()` con `// dado // cuando // entonces` (un «y» en el nombre sugiere dos conductas, SWE cap. 12)
2. `@DisplayName` y `@Nested` por situación — `@Nested class ConCreditoInsuficiente { @Test void rechaza() }`
3. Patrón `metodo_estado_resultado` — `confirmar_sinCredito_lanzaCreditoInsuficiente()`
Cierra: ⟦10⟧, PB-17, PB-18.

**P18. ¿Cómo se mide si las pruebas sirven?**
Por qué: la cobertura dice qué se ejecutó; la mutación, qué se comprobó (PIT).
1. ★ PIT en dominio y aplicación con umbral — `<mutationThreshold>80</mutationThreshold>` [opinión: valor]; mutantes vivos se revisan
2. PIT incremental sobre lo cambiado — `withHistory` o `scmMutationCoverage` en cada PR
3. Solo cobertura de líneas (JaCoCo) — sirve para ver lo no probado; no como meta (Fowler, *TestCoverage*)
Cierra: ⟦11⟧, PB-20.

**P19. ¿Cómo y cuándo se ejecutan?**
Por qué: lo lento no se corre y lo que no corre en CI no protege (Beck: «fast»).
1. ★ `*Test` en Surefire (segundos), `*IT` en Failsafe con Docker, PIT nocturno — `mvn verify` en cada PR
2. Etiquetas JUnit — `@Tag("lenta")`; `mvn test -Dgroups=!lenta` en local
3. Todo en `mvn test` — solo si el total cabe en < 1-2 min [opinión]
Cierra: ⟦12⟧, PB-15, PB-24, PB-27.

**P20. ¿Qué se hace con una prueba intermitente?**
Por qué: cerca del 1 % de intermitencia las pruebas pierden valor (SWE cap. 11); Fowler: cuarentena.
1. ★ Se corrige la causa o se pone en cuarentena con pendiente, dueño y plazo — `@Tag("cuarentena")` + `Q-n` (Fowler sugiere un tope: «only allow 8 tests», «no longer than a week»)
2. Reintento automático solo informativo — `rerunFailingTestsCount=2` que además reporta las intermitentes
3. Se desactiva y se olvida (se rechaza) — `@Disabled` sin pendiente
Cierra: ⟦8⟧, PB-19, PB-22.

## 6 · Poda y ramas nuevas

- **El elemento no tiene E/S propia (solo dominio):** se saltan P9-P13 y P16. Los cupos van a más
  contraejemplos de las reglas y a propiedades (P4-3).
- **La plantilla de origen ya fijó los dobles (`caso-de-uso` P20 o `adaptador-persistencia` P20):** P5 y
  P6 se responden con una línea de confirmación.
- **No hay web (el disparador es un evento o un trabajo):** P9 cambia a «¿cómo se prueba el listener o el
  trabajo?» → `@ApplicationModuleTest` + `Scenario.publish(...)` (`evento-mensaje` T6) o la prueba del
  objeto humilde que el `@Scheduled` invoca (`trabajo-programado`).
- **P12 = no hay otro proceso:** se salta el resto de contratos; el cupo va a «¿qué módulo de Modulith
  consume los eventos y cómo se prueba esa API?».
- **P13 = no hay asincronía:** se salta; el cupo va a P16 (concurrencia) o a fallos técnicos de cada
  puerto («¿y si la BD no responde?»).
- **P16 = un solo escritor garantizado:** se registra el ADR; el cupo va a idempotencia («¿y si el mismo
  comando llega dos veces?»).
- **P3 = panal (servicio casi sin dominio):** P4 se reduce a una línea; P14 crece a «¿qué casos por la
  API del servicio?».
- **Módulo nuevo sin infraestructura de pruebas:** se agrega una rama «esqueleto andante» (Freeman y
  Pryce): la primera prueba de sistema mínima que arranca el contexto con Testcontainers.
- **Lo que el código o la documentación ya responde** (por ejemplo, las versiones de la plantilla de
  `IA_TECNOLOGIAS-vigentes.md`): confirmación de una línea; libera cupo (`investigacion/04` regla 7).
- **Cupos libres, en este orden:** mutantes que probablemente sobrevivan («¿qué pasa si el `>=` fuera
  `>`?» → prueba en el borde), fallos técnicos de cada puerto, entradas límite (vacío, cero, máximo) y la
  metapregunta.

## 7 · Cobertura

Pesos en el orden de §1 (`w_k = 12 − k + 1`: ⟦1⟧ pesa 12 y ⟦12⟧ pesa 1). Cada casilla cuenta cuando tiene
respuesta o queda `[PENDIENTE]` con dueño (`investigacion/04` §5.1).

- [ ] ⟦1⟧ Lista de comportamientos con id, cada regla con su par cumple/viola
- [ ] ⟦2⟧ Qué es la unidad (escuela) y por qué
- [ ] ⟦3⟧ Nivel de cada prueba en el hexágono, con su tamaño y alcance
- [ ] ⟦4⟧ Dobles por frontera (fake + contrato confirmado; mocks solo para lo ajeno)
- [ ] ⟦5⟧ Infraestructura real de las pruebas de integración (motor y versión)
- [ ] ⟦6⟧ Contratos entre procesos (o «no aplica» con motivo)
- [ ] ⟦7⟧ Reglas de arquitectura automatizadas
- [ ] ⟦8⟧ Determinismo: reloj, ids, asincronía, concurrencia y política de intermitentes
- [ ] ⟦9⟧ Datos de prueba (madres, builders, ids únicos)
- [ ] ⟦10⟧ Nombres y estructura
- [ ] ⟦11⟧ Medida de calidad (mutación) y umbral
- [ ] ⟦12⟧ Ejecución: fases, etiquetas, tiempos y CI

## 8 · Antipatrones

| Antipatrón | Señal detectable | Fuente |
|---|---|---|
| Cono de helado | más `@SpringBootTest` o pruebas de UI que pruebas de dominio y de caso de uso; build de decenas de minutos | Alister Scott (2012); Fowler, *TestPyramid*; SWE cap. 11 |
| Reloj de arena | muchas de punta a punta y muchas unitarias, casi ninguna de integración de adaptadores | SWE cap. 11 |
| Magdalena (*cupcake*) | cada equipo o capa repite las mismas verificaciones en todos los niveles | Fabio Pereira, *Testing cupcake* (ThoughtWorks, 2014) |
| Pruebas frágiles por sobreespecificación | `verify` de cada llamada y su orden; un refactor sin cambio de comportamiento rompe pruebas | Meszaros (*Fragile Test*, *Overspecified Software*); Khorikov cap. 4 |
| Mock del sujeto o del agregado | `Mockito.mock(Account.class)` en la prueba del servicio; `spy(servicio)` | Khorikov cap. 5; buckpal (`caso-de-uso` §9.2) |
| Mockear lo que no es tuyo | `mock(JdbcTemplate.class)`, `mock(RestClient.class)`, `mock(KafkaTemplate.class)` | Freeman y Pryce («only mock types that you own»); SWE cap. 13 |
| Fake sin contrato | `InMemoryBookRepo` que acepta lo que la BD rechazaría | Rainsberger; SWE cap. 13; `caso-de-uso` §8 |
| Pruebas intermitentes toleradas | `@RepeatedTest` o `rerunFailingTestsCount` para «que pase»; «vuelve a correr el CI» | Fowler, *Eradicating Non-Determinism*; SWE cap. 11; Micco (Google, 2016) |
| `Thread.sleep` como sincronización | `Thread.sleep(2000)` antes de la aserción | Fowler, *Eradicating Non-Determinism* |
| Reloj oculto | `LocalDateTime.now()` en el código o en la prueba; fallan a medianoche o en otra zona | Khorikov cap. 11; buckpal `SendMoneySystemTest` (§9.2) |
| Invitado misterioso | la prueba depende de `datos.sql` o de un JSON que no muestra; el esperado sale de ahí | Meszaros (*Mystery Guest*) |
| Ruleta de aserciones | muchas aserciones sin mensaje sobre cosas distintas; no se sabe cuál falló ni por qué | Meszaros (*Assertion Roulette*) |
| Prueba ansiosa | un método de prueba con varios «cuando» | Meszaros (*Eager Test*); Khorikov cap. 3 |
| Lógica en la prueba | `for` o `if` que calculan el esperado; la prueba replica el algoritmo | SWE cap. 12; Khorikov cap. 11 (*leaking domain knowledge*) |
| Probar métodos privados | reflexión, `@VisibleForTesting`, subir la visibilidad «para el test» | Khorikov cap. 11; SWE cap. 12 |
| Prueba sin aserción o que no puede fallar | `assertThat(r).isNotNull()` como única aserción; PIT deja vivos los mutantes | PIT; `investigacion/05` §4.1 ítem 9 |
| Cobertura como meta | «80 % de líneas obligatorio»; pruebas que ejecutan sin afirmar | Fowler, *TestCoverage*; Goodhart |
| H2 en lugar del motor real | `@AutoConfigureTestDatabase` con H2 y SQL propio de PostgreSQL | Vocke (usa H2 por comodidad y reconoce el riesgo); Testcontainers |
| `@SpringBootTest` para todo | cada prueba levanta el contexto completo; nada es pequeño | Spring Boot, *Testing* (rebanadas); SWE cap. 11 |
| Contextos que no se reutilizan | `@DirtiesContext` en muchas clases; `@MockitoBean` distintos que crean un contexto por clase | Spring Framework, *Context Caching* |
| Pruebas interdependientes | una prueba usa lo que dejó otra; fallan al cambiar el orden o en paralelo | Meszaros (*Interacting Tests*); Beck, *Test Desiderata* |
| Pruebas escritas a la medida del código | se leyó la implementación y se afirmó lo que hace, no lo que la ESPEC pide | `investigacion/05` §6; Khorikov cap. 4 |
| Daño de diseño inducido por las pruebas | interfaces y capas solo para poder mockear, sin otro motivo | DHH, *Test-induced design damage* (2014); Fowler, Beck y DHH, *Is TDD Dead?* (2014) |

## 9 · Código de referencia real

Todos los repositorios se clonaron y leyeron el **15-oct-2026** en el commit enlazado, y cada URL
`raw.githubusercontent.com/<repo>/<commit>/<ruta>` respondió 200 ese día. Los fragmentos son literales
(la indentación se normalizó a espacios) y `// …` marca lo omitido. Las licencias se leyeron del archivo
`LICENSE` o de la API de GitHub. Los ejemplos que ya están en las plantillas vecinas no se repiten; se
enlazan al final de la sección.

### 9.1 · La base de la pirámide en buckpal: dominio sin dobles y adaptador de persistencia con rebanada

- **Muestra:** la prueba de una entidad de dominio sin Spring ni dobles (pequeña), y la prueba del
  adaptador de salida con `@DataJpaTest` que importa solo el adaptador y su mapeador (mediana).
- **Caso de uso real:** *Send Money* de Hombergs. `AccountTest` comprueba que un retiro mayor que el
  saldo falla y no deja actividad nueva; `AccountPersistenceAdapterTest` comprueba que el adaptador carga
  la cuenta con su ventana de actividades y que guarda la actividad nueva.
- **Repositorio:** `thombergs/buckpal` (acompaña a *Get Your Hands Dirty on Clean Architecture*, 2.ª
  ed., cap. 7 «Testing Architecture Elements»). **Sin licencia declarada** (la API de GitHub devuelve
  `license: null`): solo lectura.
- **Archivos:**
  [`AccountTest.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/test/java/io/reflectoring/buckpal/application/domain/AccountTest.java#L57-L77) ·
  [`AccountPersistenceAdapterTest.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/test/java/io/reflectoring/buckpal/adapter/out/persistence/AccountPersistenceAdapterTest.java#L18-L53)

```java
@Test
void withdrawalFailure() {
    AccountId accountId = new AccountId(1L);
    Account account = defaultAccount()
            .withAccountId(accountId)
            .withBaselineBalance(Money.of(555L))
            .withActivityWindow(new ActivityWindow(
                    defaultActivity()
                            .withTargetAccount(accountId)
                            .withMoney(Money.of(999L)).build(),
                    defaultActivity()
                            .withTargetAccount(accountId)
                            .withMoney(Money.of(1L)).build()))
            .build();

    boolean success = account.withdraw(Money.of(1556L), new AccountId(99L));

    assertThat(success).isFalse();
    assertThat(account.getActivityWindow().getActivities()).hasSize(2);
    assertThat(account.calculateBalance()).isEqualTo(Money.of(1555L));
}
// … AccountPersistenceAdapterTest.java
@DataJpaTest
@Import({AccountPersistenceAdapter.class, AccountMapper.class})
class AccountPersistenceAdapterTest {

    @Autowired
    private AccountPersistenceAdapter adapterUnderTest;
    // …
    @Test
    @Sql("AccountPersistenceAdapterTest.sql")
    void loadsAccount() {
        Account account = adapterUnderTest.loadAccount(new AccountId(1L), LocalDateTime.of(2018, 8, 10, 0, 0));

        assertThat(account.getActivityWindow().getActivities()).hasSize(2);
        assertThat(account.calculateBalance()).isEqualTo(Money.of(500));
    }
```

- **Qué copiar:** la prueba de dominio construida con un **Test Data Builder** (`defaultAccount().with…`,
  en `AccountTestData`) que solo cambia lo relevante (PB-16); la de dominio afirma resultado **y**
  estado (el saldo no cambió, no hay actividad nueva) en la rama que viola la regla (PB-02, PB-03); la
  rebanada que importa solo el adaptador y el mapeador (PB-12); el reloj pasado como valor
  (`LocalDateTime.of(2018, 8, 10, 0, 0)`).
- **Qué no copiar:** `@DataJpaTest` sin Testcontainers usa la BD embebida (H2), con otro dialecto que
  producción (PB-11; aquí T7); el `.sql` del primer caso es un **invitado misterioso**: el `500` esperado
  sale de un archivo que la prueba no muestra (PB-16); el resultado `boolean` de `withdraw`.

### 9.2 · La punta de la pirámide en buckpal: adaptador web con el puerto simulado y prueba de sistema

- **Muestra:** el adaptador de entrada con `@WebMvcTest`, donde el puerto de entrada es un mock y se
  verifica el comando que recibió; y la prueba de sistema que arranca todo con `RANDOM_PORT`, llama por
  HTTP y comprueba el efecto leyendo por el puerto de salida.
- **Caso de uso real:** el mismo *Send Money*: la web traduce la ruta a `SendMoneyCommand`; el sistema
  completo mueve 500 de la cuenta 1 a la 2.
- **Repositorio:** `thombergs/buckpal` · sin licencia (solo lectura).
- **Archivos:**
  [`SendMoneyControllerTest.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/test/java/io/reflectoring/buckpal/adapter/in/web/SendMoneyControllerTest.java#L16-L39) ·
  [`SendMoneySystemTest.java`](https://github.com/thombergs/buckpal/blob/dc819c66640be4f42100a622b9e97b1e82ad75a7/src/test/java/io/reflectoring/buckpal/SendMoneySystemTest.java#L22-L66)

```java
@WebMvcTest(controllers = SendMoneyController.class)
class SendMoneyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SendMoneyUseCase sendMoneyUseCase;

    @Test
    void testSendMoney() throws Exception {

        mockMvc.perform(post("/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
                41L, 42L, 500)
                .header("Content-Type", "application/json"))
                .andExpect(status().isOk());

        then(sendMoneyUseCase).should()
                .sendMoney(eq(new SendMoneyCommand(
                        new AccountId(41L),
                        new AccountId(42L),
                        Money.of(500L))));
    }
// … SendMoneySystemTest.java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SendMoneySystemTest {
    // …
    @Test
    @Sql("SendMoneySystemTest.sql")
    void sendMoney() {

        Money initialSourceBalance = sourceAccount().calculateBalance();
        Money initialTargetBalance = targetAccount().calculateBalance();

        ResponseEntity response = whenSendMoney(
                sourceAccountId(),
                targetAccountId(),
                transferredAmount());

        then(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        then(sourceAccount().calculateBalance())
                .isEqualTo(initialSourceBalance.minus(transferredAmount()));
    // …
    private Account loadAccount(AccountId accountId) {
        return loadAccountPort.loadAccount(
                accountId,
                LocalDateTime.now());
    }
```

- **Qué copiar:** en la web, el puerto de entrada es el único doble y la aserción es **qué comando
  recibió** (es la salida observable del adaptador: Khorikov, mock solo en la frontera); una sola prueba
  de sistema por el camino crítico, con el esperado expresado como diferencia de saldos (PB-13); el
  efecto se comprueba por un puerto, no con SQL a mano.
- **Qué no copiar:** `@MockBean` ya no existe en Spring Boot 4: es `@MockitoBean` (Spring Boot 4.0
  Migration Guide); `TestRestTemplate` en Boot 4 vive en `org.springframework.boot.resttestclient` y
  exige `@AutoConfigureTestRestTemplate`, o mejor `RestTestClient` con `@AutoConfigureRestTestClient`
  (mismo documento); `LocalDateTime.now()` en la prueba (PB-09); la web no prueba ningún caso de error
  (falta un `422` y un `400`, `endpoint` §9.2); nombre `testSendMoney` que no dice la conducta (PB-17).

### 9.3 · Dominio y aplicación en Spock, y bloqueo optimista contra la BD (ddd-by-examples/library)

- **Muestra:** pruebas de dominio con `given / when / then` del lenguaje Spock; la prueba del caso de uso
  con **stubs como lambdas** del puerto de lectura y un `Stub()` del repositorio; y una prueba de
  integración que reproduce una escritura concurrente con versión vieja.
- **Caso de uso real:** préstamo de biblioteca. Un lector regular puede reservar hasta 5 libros: la 5.ª
  reserva tiene éxito y además anuncia `MaximumNumberOhHoldsReached`. Reservar falla si el libro o el
  lector no existen o si guardar falla. Si otro proceso modificó el libro, guardar con la versión leída
  lanza `AggregateRootIsStale`.
- **Repositorio:** `ddd-by-examples/library` · licencia **MIT**.
- **Archivos:**
  [`PatronRequestingLastPossibleHoldTest.groovy`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/test/groovy/io/pillopl/library/lending/patron/model/PatronRequestingLastPossibleHoldTest.groovy#L12-L28) ·
  [`PlacingBookOnHoldTest.groovy`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/test/groovy/io/pillopl/library/lending/patron/application/hold/PlacingBookOnHoldTest.groovy#L20-L99) ·
  [`OptimisticLockingBookAggregateIT.groovy`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/integration-test/groovy/io/pillopl/library/lending/book/infrastructure/OptimisticLockingBookAggregateIT.groovy#L26-L50)

```groovy
class PatronRequestingLastPossibleHoldTest extends Specification {

    def 'should announce that a regular patron places his last possible hold (4th)'() {
        given:
            AvailableBook book = circulatingBook()
        when:
            Either<BookHoldFailed, BookPlacedOnHoldEvents> hold = regularPatronWithHolds(4).placeOnHold(book, closeEnded(3))
        then:
            hold.isRight()
            hold.get().with {
                assert it.maximumNumberOhHoldsReached.isDefined()
                MaximumNumberOhHoldsReached maximumNumberOhHoldsReached = it.maximumNumberOhHoldsReached.get()
                assert maximumNumberOhHoldsReached.numberOfHolds == 5
            }
    }
// … PlacingBookOnHoldTest.groovy
class PlacingBookOnHoldTest extends Specification {

    FindAvailableBook willFindBook = { id -> Option.of(circulatingBook()) }
    FindAvailableBook willNotFindBook = { id -> Option.none() }
    Patrons repository = Stub()
    // …
    def 'should reject placing on hold book if one of the domain rules is broken (but should not fail!)'() {
        given:
            PlacingOnHold holding = new PlacingOnHold(willFindBook, repository)
        and:
            PatronId patron = persistedRegularPatronWithManyHolds()
        when:
            Try<Result> result = holding.placeOnHold(for3days(patron))
        then:
            result.isSuccess()
            result.get() == Result.Rejection
    }
    // …
    PatronId persistedRegularPatron() {
        PatronId patronId = anyPatronId()
        Patron patron = regularPatron(patronId)
        repository.findBy(patronId) >> Option.of(patron)
        repository.publish(_ as PatronEvent) >> patron
        return patronId
    }
// … OptimisticLockingBookAggregateIT.groovy
    def 'persistence in real database should work'() {
        given:
            AvailableBook availableBook = circulatingAvailableBookAt(bookId, libraryBranchId)
        and:
            bookEntityRepository.save(availableBook)
        and:
            Book loaded = loadPersistedBook(bookId)
        and:
            someoneModifiedBookInTheMeantime(availableBook)
        when:
            bookEntityRepository.save(loaded)
        then:
            thrown(AggregateRootIsStale)
            loadPersistedBook(bookId).version == new Version(1)
    }
```

- **Qué copiar:** el nombre en frase de negocio (`'should reject placing on hold book if one of the
  domain rules is broken'`); un **Object Mother** con estados de negocio (`regularPatronWithHolds(4)`,
  `circulatingBook()`, en `PatronFixture`/`BookFixture`); el puerto de un método sustituido por una
  lambda, que es el stub más simple posible; la distinción entre **rechazo de negocio** (resultado) y
  **fallo** (excepción) con un caso para cada uno; la prueba de concurrencia optimista que afirma la
  excepción **y** que el estado guardado no cambió (PB-21, `adaptador-persistencia` PR-06); las pruebas
  de integración en su propio conjunto de fuentes (`src/integration-test`).
- **Qué no copiar:** Spock en un proyecto Spring Boot 4: la integración con Spock se retiró porque Spock
  aún no soporta Groovy 5 (Spring Boot 4.0 Migration Guide); en Java, el mismo estilo se logra con
  JUnit + AssertJ (T1, T3). `Stub()` con `>>` dentro de métodos auxiliares esconde la preparación
  (*Mystery Guest* leve); el nombre «(4th)» contradice la aserción `== 5`; la IT de bloqueo optimista
  usa la BD embebida del contexto de prueba, no el motor de producción (PB-11).

### 9.4 · Reglas de arquitectura con ArchUnit (TNG/ArchUnit-Examples y ddd-by-examples/library)

- **Muestra:** la regla `onionArchitecture()` de la biblioteca de ArchUnit con capas por paquete o por
  anotación, y reglas propias que impiden que el dominio y la aplicación dependan de Spring.
- **Caso de uso real:** en la biblioteca, el dominio de préstamos (`lending..model`) y su aplicación no
  pueden importar nada de `org.springframework`; en los ejemplos de TNG, un sistema de pedidos con
  adaptadores `cli`, `persistence` y `rest`.
- **Repositorios:** `TNG/ArchUnit-Examples` · **Apache-2.0** · `ddd-by-examples/library` · **MIT**.
- **Archivos:**
  [`OnionArchitectureTest.java`](https://github.com/TNG/ArchUnit-Examples/blob/67b4d63ae2d2d4c6109fbe3970853da12fc2dc44/example-junit5/src/test/java/com/tngtech/archunit/exampletest/junit5/OnionArchitectureTest.java#L24-L48) ·
  [`NoSpringInDomainLogicTest.java`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/test/groovy/io/pillopl/library/lending/architecture/NoSpringInDomainLogicTest.java#L11-L33)

```java
@AnalyzeClasses(packages = {
        "com.tngtech.archunit.example.onionarchitecture",
        "com.tngtech.archunit.example.onionarchitecture_by_annotations"
})
public class OnionArchitectureTest {

    @ArchTest
    static final ArchRule onion_architecture_is_respected = onionArchitecture()
            .domainModels("..domain.model..")
            .domainServices("..domain.service..")
            .applicationServices("..application..")
            .adapter("cli", "..adapter.cli..")
            .adapter("persistence", "..adapter.persistence..")
            .adapter("rest", "..adapter.rest..");
    // …
            .ignoreDependency(OrderItem.class, OrderQuantity.class);
// … NoSpringInDomainLogicTest.java (library)
@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = {"io.pillopl.library", "org.springframework"})
public class NoSpringInDomainLogicTest {

    @ArchTest
    public static final ArchRule model_should_not_depend_on_spring =
            noClasses()
                    .that()
                    .resideInAPackage(
                            "..io.pillopl.library.lending..model..")
                    .should()
                    .dependOnClassesThat()
                    .resideInAPackage("org.springframework..");
```

- **Qué copiar:** las reglas como campos `static final ArchRule` con `@ArchTest` (JUnit Jupiter); la
  regla de capas lista para usar (`onionArchitecture()`), que hace cumplir la regla de dependencias de
  Clean; la excepción explícita y con nombre (`ignoreDependency`) en vez de desactivar la regla; la regla
  «el dominio no conoce Spring» (PB-14, `caso-de-uso` U-02).
- **Qué no copiar:** `@RunWith(ArchUnitRunner.class)` es JUnit 4; con JUnit Jupiter basta
  `@AnalyzeClasses` y el motor `archunit-junit5`. Importar `org.springframework` entero en
  `@AnalyzeClasses` es lento e innecesario: ArchUnit resuelve las dependencias sin analizarlas.

### 9.5 · Estructura de módulos y registro de publicaciones en Spring Modulith (spring-projects/spring-modulith)

- **Muestra:** la prueba que verifica la estructura de módulos (ciclos y accesos a paquetes internos) y
  genera la documentación; y una prueba de módulo que estimula el caso de uso, espera un cambio de estado
  asíncrono y verifica que la publicación del evento quedó **incompleta** cuando el listener falla.
- **Caso de uso real:** el ejemplo completo de Modulith (pedidos e inventario). Al completar un pedido se
  publica `OrderCompleted`; si un listener transaccional asíncrono falla, el registro de publicaciones
  guarda la publicación pendiente para reintentarla.
- **Repositorio:** `spring-projects/spring-modulith` · **Apache-2.0**.
- **Archivos:**
  [`ModularityTests.java`](https://github.com/spring-projects/spring-modulith/blob/1fe549999aaa2401b675eba4a562ae374f4f96ba/spring-modulith-examples/spring-modulith-example-full/src/test/java/example/ModularityTests.java#L27-L40) ·
  [`EventPublicationRegistryTests.java`](https://github.com/spring-projects/spring-modulith/blob/1fe549999aaa2401b675eba4a562ae374f4f96ba/spring-modulith-examples/spring-modulith-example-full/src/test/java/example/order/EventPublicationRegistryTests.java#L38-L77)

```java
class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(Application.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }

    @Test
    void createModuleDocumentation() {
        new Documenter(modules).writeDocumentation();
    }
}
// … EventPublicationRegistryTests.java
@ApplicationModuleTest
@Import(FailingAsyncTransactionalEventListener.class)
@DirtiesContext
@RequiredArgsConstructor
class EventPublicationRegistryTests {

    private final OrderManagement orders;
    private final EventPublicationRegistry registry;
    private final FailingAsyncTransactionalEventListener listener;

    @Test
    void leavesPublicationIncompleteForFailingListener(Scenario scenario) throws Exception {

        var order = new Order();

        scenario.stimulate(() -> orders.complete(order))
                .andWaitForStateChange(listener::getEx)
                .andVerify(__ -> {
                    assertThat(registry.findIncompletePublications()).hasSize(1);
                });
    }
```

- **Qué copiar:** `ApplicationModules.of(...).verify()` como prueba pequeña y estática del build
  (PB-14); `@ApplicationModuleTest`, que por defecto arranca **solo el módulo** (`STANDALONE`; también
  hay `DIRECT_DEPENDENCIES` y `ALL_DEPENDENCIES`, Spring Modulith, *Integration Testing Application
  Modules*); `Scenario` para esperar por condición sin `sleep` (PB-10): `andWaitForStateChange` da por
  cumplida la espera con un valor no nulo; el fallo del listener simulado con una clase de prueba
  importada.
- **Qué no copiar:** `@DirtiesContext` (aquí se justifica porque el listener que falla contamina el
  contexto, pero en general rompe la caché de contextos, PB-27); la inyección por constructor de Lombok
  en la prueba es opcional. La prueba hermana `OrderIntegrationTests` con `andWaitForEventOfType` ya está
  en `caso-de-uso` §9.9.

### 9.6 · PostgreSQL real con Testcontainers en Spring Boot (testcontainers/testcontainers-java-spring-boot-quickstart)

- **Muestra:** las tres formas de conectar un PostgreSQL en contenedor: la URL `jdbc:tc:` en una rebanada
  `@DataJpaTest`; un contenedor estático con `@Container @ServiceConnection` en una prueba de sistema; y
  un bean `@ServiceConnection` en una `@TestConfiguration` reutilizable (también para levantar la app en
  local con `SpringApplication.from(...)`).
- **Caso de uso real:** una API de tareas (*Todo-Backend*). El repositorio devuelve solo las pendientes;
  la API REST lista, consulta y crea tareas contra la BD real.
- **Repositorio:** `testcontainers/testcontainers-java-spring-boot-quickstart` · licencia **MIT**.
- **Archivos:**
  [`TodoRepositoryTest.java`](https://github.com/testcontainers/testcontainers-java-spring-boot-quickstart/blob/c2fa0b2287a97026e153c9d9ee3aab5b7e96ba02/src/test/java/com/atomicjar/todos/repository/TodoRepositoryTest.java#L11-L31) ·
  [`TodoControllerTests.java`](https://github.com/testcontainers/testcontainers-java-spring-boot-quickstart/blob/c2fa0b2287a97026e153c9d9ee3aab5b7e96ba02/src/test/java/com/atomicjar/todos/web/TodoControllerTests.java#L24-L41) ·
  [`ContainersConfig.java`](https://github.com/testcontainers/testcontainers-java-spring-boot-quickstart/blob/c2fa0b2287a97026e153c9d9ee3aab5b7e96ba02/src/test/java/com/atomicjar/todos/ContainersConfig.java#L9-L19)

```java
@DataJpaTest(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:tc:postgresql:15-alpine:///todos"
})
class TodoRepositoryTest {

    @Autowired
    TodoRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        repository.save(new Todo(null, "Todo Item 1", true, 1));
        repository.save(new Todo(null, "Todo Item 2", false, 2));
        repository.save(new Todo(null, "Todo Item 3", false, 3));
    }

    @Test
    void shouldGetPendingTodos() {
        assertThat(repository.getPendingTodos()).hasSize(2);
    }
}
// … TodoControllerTests.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TodoControllerTests {
    @LocalServerPort
    private Integer port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    // …
// … ContainersConfig.java
@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    @RestartScope
    PostgreSQLContainer<?> postgreSQLContainer(){
        return new PostgreSQLContainer<>("postgres:15-alpine");
    }
}
```

- **Qué copiar:** `spring.test.database.replace=none` para que la rebanada **no** cambie la BD por una
  embebida (PB-11); el contenedor `static` (uno por clase) y `@ServiceConnection` en vez de
  `@DynamicPropertySource`; la `@TestConfiguration` compartida, que además permite que el contexto de
  Spring se reutilice entre clases (PB-27); la prueba del repositorio afirma una consulta propia, no el
  CRUD de Spring Data.
- **Qué no copiar:** con Testcontainers 2 la clase es `org.testcontainers.postgresql.PostgreSQLContainer`
  sin genérico y el artefacto `testcontainers-postgresql` (documentación de Testcontainers; así está en el
  código de este proyecto, `ArranqueBackendFincaTest`); `deleteAll()` en `@BeforeEach` para aislar sirve,
  pero con contrato compartido son mejores los ids únicos por prueba (`adaptador-persistencia` §9.7);
  `postgres:15-alpine` debe ser la versión de producción.

### 9.7 · Pruebas de propiedades con jqwik: rangos válidos e inválidos, y secuencias con estado

- **Muestra:** (a) propiedades que dividen la entrada en particiones (aprueba, reprueba, inválida) con
  generadores acotados y un `@Provide` propio; (b) una prueba con estado (`ActionChain`): jqwik genera
  secuencias aleatorias de operaciones y verifica las invariantes después de cada una, y si falla reduce
  (*shrinking*) la secuencia al contraejemplo mínimo.
- **Caso de uso real:** (a) el libro *Software Testing: From Theory to Practice* (Aniche y van Deursen,
  TU Delft) prueba la regla «una nota de 1 a 10 aprueba desde 5,0; fuera de rango es inválida»;
  (b) la guía de jqwik prueba una pila con `push`, `pop` y `clear`.
- **Repositorios:** `sttp-book/code-examples` · **sin licencia declarada** (solo lectura) ·
  `jqwik-team/jqwik` · **EPL-2.0**.
- **Archivos:**
  [`PassingGradesPBTest.java`](https://github.com/sttp-book/code-examples/blob/4e816d5af9ee2a7c7cd7cbcb277e92bbeafe7a8a/src/test/java/tudelft/pbt/PassingGradesPBTest.java#L10-L38) ·
  [`MyStringStackExamples.java`](https://github.com/jqwik-team/jqwik/blob/da7d36fe693e340c71f508f3b26960f2681f3eb4/documentation/src/test/java/net/jqwik/docs/state/mystack/MyStringStackExamples.java#L10-L54)

```java
public class PassingGradesPBTest {

    private final PassingGrade pg = new PassingGrade();

    @Property
    void fail(@ForAll @FloatRange(min = 1f, max = 5.0f, maxIncluded = false) float grade) {
        assertThat(pg.passed(grade)).isFalse();
    }

    @Property
    void pass(@ForAll @FloatRange(min = 5.0f, max = 10.0f, maxIncluded = false) float grade) {
        assertThat(pg.passed(grade)).isTrue();
    }

    @Property
    void invalid(@ForAll("invalidGrades") float grade) {
        assertThatThrownBy(() -> {
            pg.passed(grade);
        })
        .isInstanceOf(IllegalArgumentException.class);
    }

    @Provide
    private Arbitrary<Float> invalidGrades() {
        return Arbitraries.oneOf(
                Arbitraries.floats().lessOrEqual(0.9f),
                Arbitraries.floats().greaterOrEqual(10.1f));
    }
}
// … MyStringStackExamples.java (jqwik)
    @Property
    void checkMyStack(@ForAll("myStackActions") ActionChain<MyStringStack> chain) {
        chain.run();
    }

    @Provide
    Arbitrary<ActionChain<MyStringStack>> myStackActions() {
        return ActionChain.startWith(MyStringStack::new)
                          .withAction(new PushAction())
                          .withAction(pop())
                          .withAction(new ClearAction());
    }
    // …
    static class PushAction implements Action.Independent<MyStringStack> {

        @Override
        public Arbitrary<Transformer<MyStringStack>> transformer() {
            Arbitrary<String> pushElements = Arbitraries.strings().alpha().ofLength(5);
            return pushElements.map(element -> Transformer.mutate(
                String.format("push(%s)", element),
                stack -> {
                    int sizeBefore = stack.size();
                    stack.push(element);
                    assertThat(stack.isEmpty()).isFalse();
                    assertThat(stack.size()).isEqualTo(sizeBefore + 1);
                }
            ));
        }
    }
```

- **Qué copiar:** una propiedad por partición de la regla, con el borde exacto (`maxIncluded = false`)
  que es justo donde sobreviven los mutantes (`>=` frente a `>`); los inválidos generados por un
  `@Provide` con nombre; en la prueba con estado, cada acción afirma la invariante después de mutar y la
  acción condicionada (`Action.when(stack -> !stack.isEmpty())`) solo se aplica cuando tiene sentido. Para
  un agregado con máquina de estados (P4-3) es la misma forma: el agregado en lugar de la pila.
- **Qué no copiar:** los rangos inválidos dejan huecos sin probar (entre 0,9 y 1 y entre 10 y 10,1), que
  es exactamente lo que una propiedad debía cubrir; `float` para notas (en el dominio, un valor propio).

### 9.8 · Pruebas de mutación con PIT en Maven (jitterted/yacht-tdd y sttp-book/code-examples)

- **Muestra:** la configuración del plugin `pitest-maven` con el complemento de JUnit 5; en yacht-tdd con
  el motor **Descartes** (mutación extrema: vacía el cuerpo de cada método), en sttp-book con clases
  objetivo y exclusiones explícitas.
- **Caso de uso real:** yacht-tdd es el juego de dados Yacht escrito con TDD en arquitectura hexagonal
  (Ted M. Young); sttp-book corre `mvn clean compile test-compile pitest:mutationCoverage` desde su
  `mutation.sh` para los ejercicios del capítulo de mutación.
- **Repositorios:** `jitterted/yacht-tdd` · **Apache-2.0** · `sttp-book/code-examples` · sin licencia
  (solo lectura).
- **Archivos:**
  [`yacht-tdd/pom.xml`](https://github.com/jitterted/yacht-tdd/blob/d634055ca98aaeaa1c98badb13c2141098700104/pom.xml#L61-L82) ·
  [`sttp-book/pom.xml`](https://github.com/sttp-book/code-examples/blob/4e816d5af9ee2a7c7cd7cbcb277e92bbeafe7a8a/pom.xml#L120-L153)

```xml
<plugin>
  <groupId>org.pitest</groupId>
  <artifactId>pitest-maven</artifactId>
  <version>1.6.3</version>
  <configuration>
    <mutationEngine>descartes</mutationEngine>
    <mutators>
    </mutators>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>org.pitest</groupId>
      <artifactId>pitest-junit5-plugin</artifactId>
      <version>0.12</version>
    </dependency>
    <dependency>
      <groupId>eu.stamp-project</groupId>
      <artifactId>descartes</artifactId>
      <version>1.3</version>
    </dependency>
  </dependencies>
</plugin>
<!-- … sttp-book/pom.xml -->
<configuration>
    <jvmArgs>
        <arg>-ea</arg>
    </jvmArgs>
    <verbose>false</verbose>
    <targetClasses>
        <param>tudelft.*</param>
    </targetClasses>
    <targetTests>
        <param>tudelft.*</param>
    </targetTests>
    <excludedTestClasses>
        <excludedTestClass>tudelft.mocks.invoice.BadlyDesignedInvoiceFilterTest</excludedTestClass>
        <!-- … -->
    </excludedTestClasses>
```

- **Qué copiar:** el complemento de JUnit 5 como dependencia **del plugin**, no del proyecto;
  `targetClasses` acotado a lo que importa (dominio y aplicación); Descartes como primera pasada barata
  en proyectos grandes (detecta métodos cuyo cuerpo se puede borrar sin que ninguna prueba falle).
- **Qué no copiar:** las versiones (en Maven Central, el 15-oct-2026: `pitest-maven` 1.30.1 y
  `pitest-junit5-plugin` 1.2.3); ninguno fija `mutationThreshold`, así que el build nunca falla por
  pruebas débiles (PB-20; T14 lo fija).

### 9.9 · Nullables: infraestructura con interruptor de apagado en vez de mocks (jitterted/yacht-tdd)

- **Muestra:** el patrón *Nullables* de James Shore: la clase de infraestructura (`DieRoller`) tiene
  `create()` para producción y `createNull(...)` con **respuestas configurables**; el servicio se prueba
  sociable con todas sus dependencias anuladas, y la salida hacia afuera se observa con **seguimiento de
  salida** (`trackAssignments()` devuelve un `OutputTracker`).
- **Caso de uso real:** en Yacht, tirar los dados con valores conocidos, volver a tirar conservando
  algunos y notificar a un servicio externo la categoría a la que el jugador asignó su tirada.
- **Repositorio:** `jitterted/yacht-tdd` · **Apache-2.0**.
- **Archivos:**
  [`DieRoller.java`](https://github.com/jitterted/yacht-tdd/blob/d634055ca98aaeaa1c98badb13c2141098700104/src/main/java/com/jitterted/yacht/adapter/out/dieroller/DieRoller.java#L9-L80) ·
  [`GameServiceNotificationTest.java`](https://github.com/jitterted/yacht-tdd/blob/d634055ca98aaeaa1c98badb13c2141098700104/src/test/java/com/jitterted/yacht/application/GameServiceNotificationTest.java#L59-L89)

```java
public class DieRoller {

    private final RandomInt random;

    private DieRoller(RandomInt randomInt) {
        this.random = randomInt;
    }

    public static DieRoller create() {
        return new DieRoller(new RandomWrapper());
    }

    public static DieRoller createNull(Integer... dieRolls) {
        return createNull(Arrays.asList(dieRolls));
    }
    // …
    // ---- Nullable stuff below
    // …
    private static class RandomStub implements RandomInt {
        private final List<Integer> values;
        private int current = 0;
        // …
        @Override
        public int nextInt(int bound) {
            if (values.isEmpty()) {
                return 0;
            } else {
                requireHaveMoreConfiguredRolls();
                int value = values.get(current++);
                requireWithinRange(value);
                return value - 1;
            }
        }
// … GameServiceNotificationTest.java
    @Test
    void whenRollAssignedToCategoryNotificationIsSent() throws Exception {
        // GIVEN a started game and dice rolled
        ScoreCategoryNotifier scoreCategoryNotifier =
                ScoreCategoryNotifier.createNull();
        OutputTracker<RollAssignment> tracker =
                scoreCategoryNotifier.trackAssignments();
        // …
        // WHEN
        gameService.assignCurrentHandTo(ScoreCategory.SIXES);

        // THEN
        assertThat(tracker.output())
                .containsExactly(
                        new RollAssignment(HandOfDice.of(6, 6, 6, 6, 6),
                                           30,
                                           ScoreCategory.SIXES));
    }
```

- **Qué copiar:** la idea de fondo, que coincide con la decisión del usuario: **probar por estado y por
  la salida observada, no con mocks** (Shore: *State-Based Tests*, *Output Tracking*); los bloques
  `// GIVEN // WHEN // THEN` visibles (PB-17); el stub que **falla con un mensaje claro** si la prueba
  pide más tiradas de las configuradas, en vez de devolver basura.
- **Qué no copiar:** en esta skill el núcleo depende de **puertos** y el doble vive en `src/test` (fake +
  contrato); los Nullables meten código de prueba en producción (Khorikov: *code pollution*), y aquí el
  servicio de aplicación importa clases de `adapter.out` (viola la regla de dependencias, `caso-de-uso`
  U-02). Úsalo solo si la P5 elige la opción 3 y se registra como decisión.

### 9.10 · Contrato dirigido por el consumidor con Pact y JUnit 5 (pact-foundation/pact-workshop-Maven-Springboot-JUnit5)

- **Muestra:** el consumidor declara la interacción esperada (`given` → estado del proveedor,
  `uponReceiving`, `willRespondWith` con *matchers* de tipo, no valores fijos) y prueba su cliente real
  contra el servidor simulado de Pact; el proveedor verifica todas las interacciones del archivo de pacto
  contra su aplicación arrancada en un puerto aleatorio.
- **Caso de uso real:** un catálogo de productos (consumidor `ProductCatalogue`) lee la lista y el detalle
  de productos de `ProductService`; el contrato fija los campos que el catálogo usa.
- **Repositorio:** `pact-foundation/pact-workshop-Maven-Springboot-JUnit5`, rama `step4` · licencia
  **MIT**.
- **Archivos:**
  [`ProductServiceClientPactTest.java`](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/blob/94778376183514bcf4ce2450e63252d160c397a1/consumer/src/test/java/io/pact/workshop/product_catalogue/clients/ProductServiceClientPactTest.java#L24-L58) ·
  [`PactVerificationTest.java`](https://github.com/pact-foundation/pact-workshop-Maven-Springboot-JUnit5/blob/94778376183514bcf4ce2450e63252d160c397a1/provider/src/test/java/io/pact/workshop/product_service/PactVerificationTest.java#L17-L38)

```java
@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "ProductService")
class ProductServiceClientPactTest {
  @Autowired
  private ProductServiceClient productServiceClient;

  @Pact(consumer = "ProductCatalogue")
  public RequestResponsePact allProducts(PactDslWithProvider builder) {
    return builder
      .given("products exists")
        .uponReceiving("get all products")
        .path("/products")
      .willRespondWith()
        .status(200)
        .body(
          new PactDslJsonBody()
            .minArrayLike("products", 1, 2)
              .integerType("id", 9L)
              .stringType("name", "Gem Visa")
              .stringType("type", "CREDIT_CARD")
              .closeObject()
            .closeArray()
        )
      .toPact();
  }

  @Test
  @PactTestFor(pactMethod = "allProducts", pactVersion = PactSpecVersion.V3)
  void testAllProducts(MockServer mockServer) {
    productServiceClient.setBaseUrl(mockServer.getUrl());
    List<Product> products = productServiceClient.fetchProducts().getProducts();
    assertThat(products, hasSize(2));
    // …
// … PactVerificationTest.java (proveedor)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("ProductService")
@PactFolder("pacts")
@IgnoreMissingStateChange
public class PactVerificationTest {
  @LocalServerPort
  private int port;
  // …
  @BeforeEach
  void setup(PactVerificationContext context) {
    context.setTarget(new HttpTestTarget("localhost", port));
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void pactVerificationTestTemplate(PactVerificationContext context) {
    context.verifyInteraction();
  }
}
```

- **Qué copiar:** *matchers* por tipo (`integerType`, `stringType`, `minArrayLike`) para que el contrato
  fije la **forma**, no los datos; el estado del proveedor con nombre (`given("products exists")`); la
  verificación del proveedor como una plantilla de prueba que recorre todas las interacciones (PB-23).
  En el hexágono, el consumidor Pact es la prueba del **adaptador de salida HTTP** y puede ser además su
  subclase real del contrato del puerto (T9).
- **Qué no copiar:** `@PactFolder("pacts")` (copiar el archivo a mano) en lugar de un Pact Broker; 
  `@IgnoreMissingStateChange` esconde estados que el proveedor no prepara (en un paso posterior del
  taller se agregan `@State`); `@SpringBootTest` completo en el consumidor cuando basta construir el
  cliente. El Pact con JUnit 4 (GOV.UK Pay) y Spring Cloud Contract ya están en `endpoint` §9.9 y §9.10.

### 9.11 · Concurrencia determinista contra PostgreSQL: forzar el entrelazado con un bloqueo observable (spring-projects/spring-ai)

- **Muestra:** una prueba de concurrencia que **no depende de la suerte**: una transacción toma el
  bloqueo de la fila (`SELECT … FOR UPDATE`), lanza la operación concurrente en otro hilo, **espera a que
  el hilo esté realmente bloqueado** consultando `pg_locks` (con plazo máximo) y solo entonces sigue; al
  final afirma el orden resultante.
- **Caso de uso real:** en el repositorio de sesiones de chat de Spring AI, una compactación (reemplazar
  mensajes viejos por un resumen) y un mensaje nuevo llegan a la vez; el nuevo debe quedar **después**
  del resumen.
- **Repositorio:** `spring-projects/spring-ai` · **Apache-2.0**.
- **Archivo:**
  [`JdbcSessionRepositoryPostgresqlIT.java`](https://github.com/spring-projects/spring-ai/blob/d8ee22fc43c878e046d5a5471fa8c87c808e6dac/session-repositories/spring-ai-session-jdbc/src/test/java/org/springframework/ai/session/jdbc/JdbcSessionRepositoryPostgresqlIT.java#L104-L176)

```java
    @Test
    void appendConcurrentWithCompactionIsOrderedAfterRetainedEvents() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        // …
        // The compacting transaction takes the session row lock first, then lets the
        // concurrent append run into it before the compaction rewrites the active window.
        TransactionTemplate outerTx = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        CompletableFuture<Void> append = new CompletableFuture<>();
        Boolean compacted = outerTx.execute(status -> {
            this.jdbcTemplate.queryForList("SELECT id FROM AI_SESSION WHERE id = ? FOR UPDATE", sessionId);
            CompletableFuture.runAsync(() -> this.repository.appendEvent(newest)).whenComplete((r, ex) -> {
                // …
            });
            awaitBlockedOnLock();
            // …
            return this.repository.appendEvents(sessionId, List.of(boundaryEvent, summary), version);
        });

        assertThat(compacted).isTrue();
        append.get(30, TimeUnit.SECONDS);
        // …
        assertThat(active).containsExactly("summary", "old answer", "newest question");
    }

    private void awaitBlockedOnLock() {
        JdbcTemplate monitor = new JdbcTemplate(dataSource);
        long deadline = System.nanoTime() + Duration.ofSeconds(20).toNanos();
        while (System.nanoTime() < deadline) {
            // pg_locks is read live; pg_stat_activity would be snapshotted for the
            // duration of the enclosing transaction.
            Integer waiting = monitor.queryForObject("SELECT COUNT(*) FROM pg_locks WHERE NOT granted", Integer.class);
            if (waiting != null && waiting > 0) {
                return;
            }
            sleep(20);
        }
        throw new AssertionError("concurrent append never blocked on the session row lock");
    }
```

- **Qué copiar:** forzar el entrelazado con una **condición observable** en vez de esperar a que «a
  veces» coincidan (PB-21, Goetz *JCIP* cap. 12); plazos máximos en toda espera (`get(30, SECONDS)`,
  `deadline`), de modo que la prueba falla en vez de colgarse; ids aleatorios para compartir la BD con
  otras pruebas; transacciones reales (`TransactionTemplate`), no el rollback automático de la rebanada
  (`adaptador-persistencia` §10.6).
- **Qué no copiar:** el sondeo a mano con `sleep(20)` es aceptable dentro de un bucle con plazo, pero
  Awaitility lo expresa mejor (T12); `pg_locks WHERE NOT granted` cuenta **cualquier** espera del
  servidor: si las pruebas corren en paralelo sobre el mismo contenedor, conviene filtrar por la
  relación.

### 9.12 · Reloj inyectable: `Clock.fixed` en la prueba y un reloj que avanza (ddd-by-examples/library y Spring Modulith)

- **Muestra:** (a) la política recibe un `Clock` por constructor y la prueba lo fija; el comando
  esperado se construye con el mismo reloj; (b) `TimeMachine` de Spring Modulith *Moments*: un reloj de
  pruebas que **se adelanta** con `shiftBy(Duration)` y publica los eventos de paso del tiempo (día,
  semana, mes) que correspondan.
- **Caso de uso real:** (a) en la biblioteca, si otro lector ya reservó el libro (reserva duplicada), se
  cancela la segunda reserva con la hora actual; (b) en Modulith, probar reglas que dependen del paso de
  los días sin esperar.
- **Repositorios:** `ddd-by-examples/library` · MIT · `spring-projects/spring-modulith` · Apache-2.0.
- **Archivos:**
  [`HandleDuplicateHoldTest.groovy`](https://github.com/ddd-by-examples/library/blob/5225ff7a0f0c1e0751b91cdd64f925cd001d7555/src/test/groovy/io/pillopl/library/lending/patron/application/hold/HandleDuplicateHoldTest.groovy#L17-L34) ·
  [`TimeMachine.java`](https://github.com/spring-projects/spring-modulith/blob/1fe549999aaa2401b675eba4a562ae374f4f96ba/spring-modulith-moments/src/main/java/org/springframework/modulith/moments/support/TimeMachine.java#L31-L61)

```groovy
class HandleDuplicateHoldTest extends Specification {

    CancelingHold cancelingHold = Mock()

    def "should start cancelling hold if book was already hold by other patron"() {
        given:
            Clock clock = Clock.fixed(Instant.parse('2020-02-27T12:21:00Z'), ZoneOffset.UTC)
        and:
            HandleDuplicateHold duplicateHold = new HandleDuplicateHold(cancelingHold, clock)
        and:
            BookDuplicateHoldFound bookDuplicateHoldFound = duplicateHoldFoundBy()
        and:
            CancelHoldCommand cancelHoldCommand = cancelHoldCommandFrom(bookDuplicateHoldFound, clock)
        when:
            duplicateHold.handle(bookDuplicateHoldFound)
        then:
            1 * cancelingHold.cancelHold(cancelHoldCommand)
    }
```

```java
public class TimeMachine extends Moments {
    // …
    public TimeMachine(Clock clock, ApplicationEventPublisher events, MomentsProperties properties) {
        super(clock, events, properties);
    }
    // …
    @Override
    public Moments shiftBy(Duration duration) {
        return super.shiftBy(duration);
    }
```

- **Qué copiar:** `Clock.fixed(Instant.parse(...), ZoneOffset.UTC)` con un instante literal (PB-09); el
  constructor de producción que usa `Clock.systemUTC()` y el de prueba que recibe el reloj (`caso-de-uso`
  §9.4); para plazos (vencimientos, reservas que caducan), un reloj que avanza en vez de varios relojes
  fijos (T5).
- **Qué no copiar:** el mock con `1 * cancelingHold.cancelHold(...)`: el puerto de entrada de otro caso
  de uso **es nuestro**; con un espía (`CancelacionesCapturadas`) se afirma lo mismo por estado (PB-07);
  `Instant.now()` en el evento de entrada (`duplicateHoldFoundBy()`), que mezcla el reloj real con el
  fijo.

### 9.13 · Ejemplos que ya están en las plantillas vecinas (no se repiten)

| Qué | Dónde |
|---|---|
| Prueba del servicio con Mockito y regla hexagonal propia (buckpal `SendMoneyServiceTest`, `DependencyRuleTests`) | `caso-de-uso` §9.2 |
| Casos de uso probados con repositorios en memoria (citerus/dddsample-core) | `caso-de-uso` §9.6 |
| Prueba de módulo con `Scenario` y `andWaitForEventOfType` (spring-modulith `OrderIntegrationTests`) | `caso-de-uso` §9.9 |
| Estereotipos y `ensureHexagonal()` (jMolecules) | `caso-de-uso` §9.10 |
| Prueba de contrato compartida memoria + PostgreSQL (spring-ai `AbstractSessionRepositoryContractTests`) | `adaptador-persistencia` §9.7 |
| jOOQ con Testcontainers y `@ServiceConnection` (tc-guide) | `adaptador-persistencia` §9.6 |
| Prueba del adaptador web con el puerto simulado | `endpoint` §9.2 |
| Pact del consumidor (GOV.UK Pay) y Spring Cloud Contract del proveedor | `endpoint` §9.9-§9.10 |

**Límite de lo verificado:** no apareció en estos repositorios una prueba de concurrencia de **dos hilos
que arrancan juntos con `CountDownLatch`** sobre un agregado con versión (la variante ★ de P16); T12 la
escribe a partir de §9.3 (conflicto de versión) y §9.11 (entrelazado observable), y lo dice.

## 10 · Plantillas de código

Esqueletos en Java 21 + Spring Boot 4.1, JUnit Jupiter y AssertJ, listos para adaptar al dominio
genérico de pedidos. Cada uno dice en qué ejemplo real de §9 se basa. Los `TODO Pn` marcan lo que decide
la pregunta `n` de §5. **No se repiten** los dobles en memoria (`caso-de-uso` T8), las pruebas de
contrato (`caso-de-uso` T9, `adaptador-persistencia` §10.6) ni la prueba del caso de uso con fakes
(`caso-de-uso` T10): aquí se enlazan y se completan.

### T1 · Prueba de dominio: ejemplos, par cumple/viola y tablas (basado en §9.1 y §9.3)

```java
// src/test/java/com/ejemplo/pedidos/domain/PedidoTest.java
class PedidoTest {                                                    // pequeña: sin Spring, sin dobles

  static final Instant T0 = Instant.parse("2026-01-10T12:00:00Z");

  @Nested class Confirmar {

    @Test void pedidoConLineasQuedaConfirmadoYRegistraElEvento() {     // R1 · cumple
      // dado
      var pedido = PedidoMadre.borradorConLineas(2);
      // cuando
      pedido.confirmar(T0);
      // entonces
      assertThat(pedido.estado()).isEqualTo(EstadoPedido.CONFIRMADO);
      assertThat(pedido.eventosRegistrados()).singleElement().isInstanceOf(PedidoConfirmado.class);
    }

    @Test void pedidoSinLineasSeRechazaYNoCambia() {                    // R1 · viola
      var pedido = PedidoMadre.borradorVacio();
      assertThatThrownBy(() -> pedido.confirmar(T0)).isInstanceOf(PedidoVacio.class);
      assertThat(pedido.estado()).isEqualTo(EstadoPedido.BORRADOR);   // el estado no cambió (PB-03)
      assertThat(pedido.eventosRegistrados()).isEmpty();
    }

    @Test void confirmarDosVecesNoRegistraOtroEvento() {               // idempotencia (TODO P1: ¿o es error?)
      var pedido = PedidoMadre.confirmado();
      pedido.confirmar(T0);
      assertThat(pedido.eventosRegistrados()).isEmpty();
    }
  }

  // Tabla de la ESPEC (Scenario Outline). Los bordes exactos matan mutantes de `>=` frente a `>` (P18).
  @ParameterizedTest(name = "cantidad {0} → válida: {1}")
  @CsvSource({ "0, false", "1, true", "999, true", "1000, false" })    // TODO P1: límites de la ESPEC
  void cantidadDeLinea(int cantidad, boolean valida) {
    if (valida) assertThatCode(() -> new Cantidad(cantidad)).doesNotThrowAnyException();
    else assertThatThrownBy(() -> new Cantidad(cantidad)).isInstanceOf(CantidadInvalida.class);
  }
}
```

[opinión] El `if` del último método es la excepción aceptable a PB-18 (separa dos ramas de la tabla, no
calcula el esperado). Si molesta, parte en dos `@ParameterizedTest`.

### T2 · Propiedades del dominio con jqwik (basado en §9.7)

```java
// src/test/java/com/ejemplo/pedidos/domain/PedidoPropiedades.java
class PedidoPropiedades {                                             // corre en Surefire con el motor de jqwik

  @Property                                                          // por defecto 1000 intentos
  void elTotalEsLaSumaDeLasLineasYNuncaEsNegativo(@ForAll("lineas") List<Linea> lineas) {
    var pedido = PedidoMadre.borradorCon(lineas);
    assertThat(pedido.total().esNegativo()).isFalse();
    assertThat(pedido.total()).isEqualTo(lineas.stream().map(Linea::subtotal).reduce(Dinero.CERO, Dinero::mas));
  }

  @Property
  void cantidadFueraDeRangoSiempreSeRechaza(@ForAll("cantidadesInvalidas") int c) {
    assertThatThrownBy(() -> new Cantidad(c)).isInstanceOf(CantidadInvalida.class);
  }

  @Provide Arbitrary<List<Linea>> lineas() {
    Arbitrary<Linea> linea = Combinators.combine(
        Arbitraries.integers().between(1, 999),                       // TODO P4: rango de la ESPEC
        Arbitraries.longs().between(0, 10_000_000))
      .as((cant, precio) -> new Linea(ProductoId.de("P-1"), new Cantidad(cant), Dinero.cop(precio)));
    return linea.list().ofMinSize(1).ofMaxSize(20);
  }

  @Provide Arbitrary<Integer> cantidadesInvalidas() {                 // cubre todo el complemento (§9.7, «qué no copiar»)
    return Arbitraries.oneOf(Arbitraries.integers().lessOrEqual(0), Arbitraries.integers().greaterOrEqual(1000));
  }

  // P4-3: máquina de estados con ActionChain (forma de §9.7 b)
  @Property
  void ningunaSecuenciaDejaUnPedidoConfirmadoSinLineas(@ForAll("acciones") ActionChain<Pedido> cadena) {
    cadena.run();
  }

  @Provide Arbitrary<ActionChain<Pedido>> acciones() {
    return ActionChain.startWith(PedidoMadre::borradorVacio)
        .withAction(Action.<Pedido>when(p -> p.estado() == EstadoPedido.BORRADOR)
            .describeAs("agregarLinea").justMutate(p -> p.agregar(LineaMadre.cualquiera())))
        .withAction(Action.<Pedido>when(p -> !p.lineas().isEmpty() && p.estado() == EstadoPedido.BORRADOR)
            .describeAs("confirmar").justMutate(p -> {
              p.confirmar(Instant.parse("2026-01-10T12:00:00Z"));
              assertThat(p.lineas()).isNotEmpty();                    // invariante tras cada paso
            }));                                                     // TODO P4: resto de transiciones
  }
}
```

### T3 · Prueba del caso de uso: lo que agrega esta plantilla a `caso-de-uso` T10

La prueba con fakes, `Clock.fixed` y el par cumple/viola está en `caso-de-uso` T10. Esta plantilla
solo añade el caso de un puerto que envuelve **algo ajeno sin estado consultable** (un correo, una
pasarela): se sigue usando un fake que **captura** la salida (espía), no un mock (PB-07, PB-08).

```java
// src/test/java/com/ejemplo/pedidos/application/port/out/AvisosCapturados.java
public final class AvisosCapturados implements AvisosAlCliente {      // espía (Meszaros) + fake
  private final List<Aviso> enviados = new CopyOnWriteArrayList<>();
  private final AtomicBoolean caido = new AtomicBoolean();
  public AvisosCapturados caido() { caido.set(true); return this; }
  @Override public void enviar(Aviso a) {
    if (caido.get()) throw new AvisoNoEnviado(a.cliente(), new IllegalStateException("simulado"));
    enviados.add(a);
  }
  public List<Aviso> enviados() { return List.copyOf(enviados); }
}

// en ConfirmarPedidoServicioTest (caso-de-uso T10):
@Test void alConfirmarSeAvisaAlClienteUnaSolaVez() {
  var p = PedidoMadre.borradorConTotal(Dinero.cop(480_000));
  pedidos.guardar(p);
  credito.con(p.cliente(), Dinero.cop(500_000));
  confirmar.ejecutar(new ConfirmarPedidoComando(p.getId(), SolicitanteMadre.vendedorDe(p)));
  assertThat(avisos.enviados()).containsExactly(Aviso.pedidoConfirmado(p.cliente(), p.getId()));  // estado, no verify()
}
```

### T4 · Datos de prueba: madre + builder (basado en §9.1 `AccountTestData` y §9.3 `PatronFixture`)

```java
// src/test/java/com/ejemplo/pedidos/domain/testdata/PedidoMadre.java
public final class PedidoMadre {                                      // Object Mother: estados con nombre de negocio
  private PedidoMadre() {}
  public static Pedido borradorVacio()            { return unPedido().sinLineas().build(); }
  public static Pedido borradorConLineas(int n)   { return unPedido().conLineas(n).build(); }
  public static Pedido borradorConTotal(Dinero t) { return unPedido().conUnaLineaDe(t).build(); }
  public static Pedido confirmado()               { return unPedido().conLineas(1).confirmadoEn(Instant.parse("2026-01-01T00:00:00Z")).build(); }
  public static Pedido borradorCon(List<Linea> l) { return unPedido().con(l).build(); }

  public static Builder unPedido() { return new Builder(); }

  public static final class Builder {                                 // Test Data Builder (GOOS cap. 22)
    private PedidoId id = PedidoId.de("01928c1e-0000-7000-8000-0000000000a1");   // TODO P7: ids fijos o aleatorios
    private ClienteId cliente = ClienteId.de("01928c1e-0000-7000-8000-0000000000c1");
    private List<Linea> lineas = new ArrayList<>(List.of(LineaMadre.cualquiera()));
    private Instant confirmadoEn;

    public Builder conId(PedidoId v)          { id = v; return this; }
    public Builder deCliente(ClienteId v)     { cliente = v; return this; }
    public Builder sinLineas()                { lineas = new ArrayList<>(); return this; }
    public Builder con(List<Linea> v)         { lineas = new ArrayList<>(v); return this; }
    public Builder conLineas(int n)           { lineas = new ArrayList<>(Collections.nCopies(n, LineaMadre.cualquiera())); return this; }
    public Builder conUnaLineaDe(Dinero t)    { lineas = new ArrayList<>(List.of(LineaMadre.deSubtotal(t))); return this; }
    public Builder confirmadoEn(Instant v)    { confirmadoEn = v; return this; }

    public Pedido build() {
      var p = Pedido.nuevo(id, cliente);
      lineas.forEach(p::agregar);
      if (confirmadoEn != null) { p.confirmar(confirmadoEn); p.limpiarEventos(); }   // TODO: según la API del agregado
      return p;
    }
  }
}
```

Regla: la madre construye **por la API pública del agregado** (`nuevo`, `agregar`, `confirmar`), no con
`reconstituir` ni reflexión; así una invariante nueva rompe la madre y no cientos de pruebas (PB-25).
Para pruebas que comparten BD (contrato real, sistema), usa `PedidoId.nuevo()` en vez del id fijo
(§9.11, `adaptador-persistencia` §9.7).

### T5 · Reloj que avanza (basado en §9.12; alternativa propia a `TimeMachine`)

```java
// src/test/java/com/ejemplo/comun/RelojMovible.java
public final class RelojMovible extends Clock {                       // fake de java.time.Clock
  private final AtomicReference<Instant> ahora;
  private final ZoneId zona;

  public RelojMovible(Instant inicio) { this(new AtomicReference<>(inicio), ZoneOffset.UTC); }
  private RelojMovible(AtomicReference<Instant> ahora, ZoneId zona) { this.ahora = ahora; this.zona = zona; }

  public void avanzar(Duration d) { ahora.updateAndGet(i -> i.plus(d)); }
  @Override public Instant instant() { return ahora.get(); }
  @Override public ZoneId getZone() { return zona; }
  @Override public Clock withZone(ZoneId z) { return new RelojMovible(ahora, z); }
}

// uso
@Test void laReservaVenceALas48Horas() {                              // TODO P8: plazo de la ESPEC
  var reloj = new RelojMovible(Instant.parse("2026-01-10T12:00:00Z"));
  var servicio = new VencerReservasServicio(reservas, eventos, reloj);
  reservas.guardar(ReservaMadre.creadaEn(reloj.instant()));
  reloj.avanzar(Duration.ofHours(47).plusMinutes(59));
  servicio.ejecutar();
  assertThat(eventos.publicados()).isEmpty();                         // 1 minuto antes: no vence
  reloj.avanzar(Duration.ofMinutes(1));
  servicio.ejecutar();
  assertThat(eventos.publicados()).singleElement().isInstanceOf(ReservaVencida.class);
}
```

En producción, un solo bean: `@Bean Clock reloj() { return Clock.systemUTC(); }`. En las pruebas de
Spring, sustitúyelo en una `@TestConfiguration` (no con `@MockitoBean`: un `Clock` es un valor, no un
colaborador). Si el módulo usa Spring Modulith *Moments*, `TimeMachine.shiftBy(...)` además publica los
eventos de paso del tiempo (§9.12).

### T6 · Adaptador web con `@WebMvcTest` en Spring Boot 4 (basado en §9.2; detalle en `endpoint` §9.2)

```java
// src/test/java/com/ejemplo/pedidos/adapter/in/web/ConfirmarPedidoControllerTest.java
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;              // Boot 4: paquete nuevo
import org.springframework.test.context.bean.override.mockito.MockitoBean;        // reemplaza a @MockBean

@WebMvcTest(ConfirmarPedidoController.class)
class ConfirmarPedidoControllerTest {                                 // mediana sin Docker: puede ir en Surefire

  @Autowired MockMvc mvc;
  @MockitoBean ConfirmarPedido confirmar;                             // el puerto de entrada: única frontera simulada

  static final PedidoId ID = PedidoId.de("01928c1e-0000-7000-8000-0000000000a1");

  @Test void confirmaYDevuelve200ConElResultado() throws Exception {
    given(confirmar.ejecutar(any())).willReturn(
        new ResultadoConfirmacion(ID, Dinero.cop(480_000), Instant.parse("2026-01-10T12:00:00Z")));
    mvc.perform(post("/pedidos/{id}:confirmar", ID.valor()).header("X-Usuario", "vendedor-1"))  // TODO P9: autenticación real
       .andExpect(status().isOk())
       .andExpect(jsonPath("$.total").value(480000));
    then(confirmar).should().ejecutar(new ConfirmarPedidoComando(ID, new Solicitante("vendedor-1")));  // la traducción ES la conducta
  }

  @Test void creditoInsuficienteEs422ConProblemDetail() throws Exception {
    given(confirmar.ejecutar(any())).willThrow(new CreditoInsuficiente(ID));
    mvc.perform(post("/pedidos/{id}:confirmar", ID.valor()).header("X-Usuario", "vendedor-1"))
       .andExpect(status().isUnprocessableEntity())
       .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
       .andExpect(jsonPath("$.type").value("https://ejemplo.com/problemas/credito-insuficiente"));  // TODO: catálogo de endpoint P16
  }

  @Test void idMalFormadoEs400SinLlamarAlCasoDeUso() throws Exception {
    mvc.perform(post("/pedidos/{id}:confirmar", "no-es-uuid").header("X-Usuario", "vendedor-1"))
       .andExpect(status().isBadRequest());
    then(confirmar).shouldHaveNoInteractions();
  }
}
```

Aquí el `verify` sí es correcto: el adaptador **es** un traductor y su salida observable es el comando
que entrega al puerto (Khorikov, mocks en la frontera; §9.2). Dependencia: `spring-boot-starter-webmvc-test`
(Spring Boot 4.0 Migration Guide).

### T7 · Contenedor de PostgreSQL compartido para todas las `…IT` (basado en §9.6)

```java
// src/test/java/com/ejemplo/ContenedoresDePrueba.java
import org.testcontainers.postgresql.PostgreSQLContainer;             // Testcontainers 2: sin genérico

@TestConfiguration(proxyBeanMethods = false)
public class ContenedoresDePrueba {
  @Bean @ServiceConnection
  PostgreSQLContainer postgres() {
    return new PostgreSQLContainer("postgres:16");                    // TODO P10: la imagen y versión de producción
  }
}

// una rebanada de persistencia: la subclase real del contrato (adaptador-persistencia §10.6) la usa así
@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)       // no cambiar por H2 (PB-11)
@Import({ContenedoresDePrueba.class, PedidosJdbc.class})
class PedidosJdbcContratoIT extends PedidosContratoTest {
  @Autowired PedidosJdbc real;
  @Override protected Pedidos crear() { return real; }
}

// una prueba de sistema o de módulo
@SpringBootTest @Import(ContenedoresDePrueba.class)
class AlgoIT { /* … */ }
```

Una sola `@TestConfiguration` importada igual en todas las clases deja que Spring **reutilice** el
contexto y el contenedor entre clases (PB-27). Si no hay Docker en la máquina, usa
`@Testcontainers(disabledWithoutDocker = true)` en las clases que declaran `@Container` (así lo hace
`ArranqueBackendFincaTest` en este proyecto); en CI, Docker es obligatorio [opinión].

### T8 · Subclase real del contrato de un puerto HTTP con WireMock (completa el `TODO P20` de `caso-de-uso` T9)

```java
// src/test/java/com/ejemplo/pedidos/adapter/out/credito/CreditoDeClientesHttpContratoIT.java
class CreditoDeClientesHttpContratoIT extends CreditoDeClientesContratoTest {

  @RegisterExtension
  static WireMockExtension servidor = WireMockExtension.newInstance()
      .options(wireMockConfig().dynamicPort()).build();               // org.wiremock:wiremock-standalone 3.13.2; baseUrl() verificado en el jar

  @Override protected CreditoDeClientes crear() {
    var rest = RestClient.builder().baseUrl(servidor.baseUrl())
        .requestFactory(fabricaConTimeout(Duration.ofSeconds(2)))     // TODO P11: el timeout del adaptador
        .build();
    return new CreditoDeClientesHttp(rest);
  }

  @Override protected void dado(ClienteId c, Dinero d) {
    servidor.stubFor(get(urlEqualTo("/creditos/" + c.valor()))
        .willReturn(okJson("{\"disponible\": %d, \"moneda\": \"COP\"}".formatted(d.enPesos()))));
  }

  @Override protected void dadoCaido(ClienteId c) {                   // dos fallos técnicos del contrato
    servidor.stubFor(get(urlEqualTo("/creditos/" + c.valor()))
        .willReturn(aResponse().withStatus(503)));
    // variante de lentitud: .willReturn(okJson("{}").withFixedDelay(3_000))
  }
  // «sin línea → vacío» del contrato abstracto: WireMock responde 404 por defecto a lo no preparado.
  // TODO P11: confirmar que el adaptador traduce 404 a Optional.empty()
}
```

### T9 · Pact del consumidor para el puerto HTTP de salida (basado en §9.10; solo si P12 = «nosotros consumimos»)

```java
// src/test/java/com/ejemplo/pedidos/adapter/out/credito/CreditoDeClientesPactTest.java
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "creditos")                              // TODO P12: nombre acordado con el proveedor
class CreditoDeClientesPactTest {

  @Pact(consumer = "pedidos")
  RequestResponsePact clienteConLinea(PactDslWithProvider builder) {
    return builder
        .given("el cliente c1 tiene una línea de crédito")
        .uponReceiving("consulta del disponible de c1")
          .path("/creditos/01928c1e-0000-7000-8000-0000000000c1").method("GET")
        .willRespondWith()
          .status(200)
          .body(new PactDslJsonBody().integerType("disponible", 500000).stringValue("moneda", "COP"))
        .toPact();
  }

  @Test @PactTestFor(pactMethod = "clienteConLinea")
  void leeElDisponible(MockServer servidor) {
    var adaptador = new CreditoDeClientesHttp(RestClient.builder().baseUrl(servidor.getUrl()).build());  // el adaptador real
    assertThat(adaptador.disponible(ClienteId.de("01928c1e-0000-7000-8000-0000000000c1")))
        .contains(Dinero.cop(500_000));
  }
  // TODO P12: un @Pact por respuesta que el adaptador traduce (404 → vacío, 503 → CreditoNoDisponible)
}
```

El archivo de pacto que se genera (`target/pacts/pedidos-creditos.json`) se publica en un Pact Broker y
el proveedor lo verifica en su build (lado proveedor: §9.10 `PactVerificationTest`; con Spring Cloud
Contract: `endpoint` §9.10). Dependencias: `au.com.dius.pact.consumer:junit5` (4.7.5 en Maven Central el
15-oct-2026).

### T10 · Prueba de módulo y estructura en Spring Modulith (basado en §9.5; eventos en `evento-mensaje` T6)

```java
// src/test/java/com/ejemplo/ModularidadTest.java
class ModularidadTest {                                               // pequeña: análisis estático
  static final ApplicationModules MODULOS = ApplicationModules.of(Aplicacion.class);
  @Test void respetaLaEstructuraDeModulos() { MODULOS.verify(); }
  @Test void documentaLosModulos() { new Documenter(MODULOS).writeDocumentation(); }  // opcional
}

// src/test/java/com/ejemplo/pedidos/PedidosModuloIT.java
@ApplicationModuleTest                                                // STANDALONE por defecto
@Import(ContenedoresDePrueba.class)                                   // T7
class PedidosModuloIT {

  @Autowired ConfirmarPedido confirmar;
  @MockitoBean CatalogoDeProductos catalogo;                          // TODO P12: API de otro módulo que pedidos usa

  @Test void confirmarPublicaPedidoConfirmado(Scenario escenario) {
    var id = /* TODO: preparar un pedido en borrador por el puerto de entrada de creación */ PedidoId.nuevo();
    escenario.stimulate(() -> confirmar.ejecutar(new ConfirmarPedidoComando(id, SolicitanteMadre.supervisor())))
        .andWaitForEventOfType(PedidoConfirmado.class)
        .matching(e -> e.pedidoId().equals(id))
        .toArrive();
  }
}
```

### T11 · Reglas de arquitectura de determinismo e higiene de pruebas (complementa `caso-de-uso` T10)

```java
// src/test/java/com/ejemplo/ReglasDePruebasTest.java
@AnalyzeClasses(packages = "com.ejemplo")                             // incluye las clases de prueba
class ReglasDePruebasTest {

  @ArchTest static final ArchRule nucleoSinRelojNiAzar = noClasses()  // PB-09
      .that().resideInAnyPackage("..domain..", "..application..")
      .should().callMethod(Instant.class, "now")
      .orShould().callMethod(LocalDateTime.class, "now")
      .orShould().callMethod(UUID.class, "randomUUID")
      .because("el tiempo y los ids entran por Clock y por un generador inyectado");

  @ArchTest static final ArchRule sinSleepEnPruebas = noClasses()     // PB-10
      .should().callMethod(Thread.class, "sleep", long.class)
      .because("se espera por condición (Awaitility o Scenario), no por tiempo fijo");

  @ArchTest static final ArchRule nucleoSinSpringTest = noClasses()   // PB-06
      .that().resideInAnyPackage("..domain..", "..application..")
      .should().beAnnotatedWith(SpringBootTest.class)
      .orShould().beAnnotatedWith(Testcontainers.class);
}
```

TODO P15: si alguna prueba de concurrencia necesita `sleep` dentro de un bucle con plazo (§9.11),
exclúyela con `.that().doNotHaveSimpleName(...)` y deja el motivo en `because(...)`.

### T12 · Concurrencia: dos hilos que arrancan juntos contra la BD real (escrito a partir de §9.3 y §9.11)

```java
// src/test/java/com/ejemplo/pedidos/ConfirmarPedidoConcurrenciaIT.java
@SpringBootTest @Import(ContenedoresDePrueba.class)
class ConfirmarPedidoConcurrenciaIT {                                 // sin @Transactional: commits reales

  @Autowired ConfirmarPedido confirmar;
  @Autowired CrearPedido crear;                                       // TODO P16: cómo se prepara el pedido

  @Test void dosConfirmacionesSimultaneasDejanUnSoloEfecto() throws Exception {
    var id = crear.ejecutar(/* … */).pedidoId();
    var largada = new CountDownLatch(1);
    try (var hilos = Executors.newFixedThreadPool(2)) {               // Java 21: ExecutorService es AutoCloseable
      Callable<Object> intento = () -> {
        largada.await();                                              // los dos salen a la vez
        try { return confirmar.ejecutar(new ConfirmarPedidoComando(id, SolicitanteMadre.supervisor())); }
        catch (ConflictoDeVersion e) { return e; }
      };
      var a = hilos.submit(intento);
      var b = hilos.submit(intento);
      largada.countDown();
      var resultados = List.of(a.get(10, SECONDS), b.get(10, SECONDS));   // plazo: falla en vez de colgarse
      assertThat(resultados).filteredOn(ResultadoConfirmacion.class::isInstance).hasSize(1);
      assertThat(resultados).filteredOn(ConflictoDeVersion.class::isInstance).hasSize(1);  // TODO P16: ¿o idempotente?
    }
    // invariante en la BD: un solo PedidoConfirmado en el registro de publicaciones
  }
}
```

Que los dos hilos «salgan a la vez» no garantiza que se entrelacen en el punto crítico: con versión
optimista basta (los dos leen la versión 0 antes de escribir casi siempre), pero si la prueba sale
intermitente, fuerza el entrelazado con un bloqueo observable como §9.11 o con un puerto de prueba que
se detiene en un `CountDownLatch` entre la lectura y la escritura [opinión]. Para estructuras en memoria
de bajo nivel (no agregados), la herramienta es jcstress, no JUnit (Goetz, *JCIP* cap. 12, sobre la
dificultad de probar concurrencia) [opinión sobre la herramienta].

### T13 · Prueba de sistema de punta a punta con `RestTestClient` (basado en §9.2 y §9.6)

```java
// src/test/java/com/ejemplo/ConfirmarPedidoSistemaIT.java
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient                                          // Boot 4: ya no viene con @SpringBootTest
@Import(ContenedoresDePrueba.class)
class ConfirmarPedidoSistemaIT {                                      // 1-3 por sistema (PB-13)

  @Autowired RestTestClient cliente;

  @Test void elVendedorConfirmaUnPedidoConCredito() {                 // camino crítico CA-01
    var id = crearPedidoPorLaApi();                                   // por la API, no con SQL (PB-16)
    cliente.post().uri("/pedidos/{id}:confirmar", id)
        .header("X-Usuario", "vendedor-1")                            // TODO P14: autenticación real de la prueba
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.pedidoId").isEqualTo(id);
    cliente.get().uri("/pedidos/{id}", id).exchange()
        .expectStatus().isOk()
        .expectBody().jsonPath("$.estado").isEqualTo("CONFIRMADO");   // el efecto, leído por la API
  }

  @Test void sinCreditoSeRechazaCon422() { /* TODO P14: el fallo crítico; un caso, no todas las reglas */ }

  private String crearPedidoPorLaApi() { /* TODO: POST /pedidos con un cliente con crédito */ return null; }
}
```

El crédito del cliente viene de otro servicio: en la prueba de sistema se simula con el servidor de T8
(o con los *stubs* del proveedor, si usa Spring Cloud Contract), nunca con el servicio real: llamarlo la
volvería una prueba grande, que «run wherever they want» (SWE cap. 11).

### T14 · Ejecución: Maven, JUnit, jqwik y PIT

```xml
<!-- pom.xml del módulo (versiones de Spring Boot 4.1.1 salvo donde se indica) -->
<dependencies>
  <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-webmvc-test</artifactId><scope>test</scope></dependency>
  <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-jdbc-test</artifactId><scope>test</scope></dependency>
  <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-testcontainers</artifactId><scope>test</scope></dependency>
  <dependency><groupId>org.testcontainers</groupId><artifactId>testcontainers-junit-jupiter</artifactId><scope>test</scope></dependency>
  <dependency><groupId>org.testcontainers</groupId><artifactId>testcontainers-postgresql</artifactId><scope>test</scope></dependency>
  <dependency><groupId>org.springframework.modulith</groupId><artifactId>spring-modulith-starter-test</artifactId><scope>test</scope></dependency>
  <dependency><groupId>org.awaitility</groupId><artifactId>awaitility</artifactId><scope>test</scope></dependency>
  <dependency><groupId>com.tngtech.archunit</groupId><artifactId>archunit-junit5</artifactId><version>${archunit.version}</version><scope>test</scope></dependency>
  <dependency><groupId>net.jqwik</groupId><artifactId>jqwik</artifactId><version>1.10.2</version><scope>test</scope></dependency>  <!-- TODO P4 -->
</dependencies>

<build><plugins>
  <plugin>                                                            <!-- *Test: pequeñas, en `mvn test` -->
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration><excludedGroups>cuarentena</excludedGroups></configuration>   <!-- P20 -->
  </plugin>
  <plugin>                                                            <!-- *IT: con Docker, en `mvn verify` -->
    <artifactId>maven-failsafe-plugin</artifactId>
    <executions><execution><goals><goal>integration-test</goal><goal>verify</goal></goals></execution></executions>
  </plugin>
  <plugin>                                                            <!-- P18: `mvn test-compile pitest:mutationCoverage` -->
    <groupId>org.pitest</groupId>
    <artifactId>pitest-maven</artifactId>
    <version>1.30.1</version>                                         <!-- Maven Central, 15-oct-2026 -->
    <dependencies>
      <dependency><groupId>org.pitest</groupId><artifactId>pitest-junit5-plugin</artifactId><version>1.2.3</version></dependency>
    </dependencies>
    <configuration>
      <targetClasses>
        <param>com.ejemplo.*.domain.*</param>
        <param>com.ejemplo.*.application.*</param>
      </targetClasses>
      <excludedTestClasses><param>*IT</param></excludedTestClasses>   <!-- solo las pequeñas: rápido -->
      <mutationThreshold>80</mutationThreshold>                       <!-- TODO P18 [opinión: valor] -->
      <threads>4</threads>
    </configuration>
  </plugin>
</plugins></build>
```

```properties
# src/test/resources/junit-platform.properties — PB-15: el orden no debe importar
junit.jupiter.testmethod.order.default=org.junit.jupiter.api.MethodOrderer$Random
junit.jupiter.testclass.order.default=org.junit.jupiter.api.ClassOrderer$Random
```

Los nombres `junit.jupiter.testmethod.order.default` y `junit.jupiter.testclass.order.default` se
verificaron en las clases `MethodOrderer` y `ClassOrderer` de `junit-jupiter-api` 6.0.3 (la versión de
Spring Boot 4.1.1). jqwik se registra como
motor de la plataforma JUnit y corre en Surefire sin más configuración (jqwik User Guide).

## 11 · Qué se genera

Orden dentro de un segmento: primero lo que fija la intención (la lista de casos y las pruebas que
fallan), luego el soporte, luego la integración, de modo que cada paso compile y las pruebas se lean
antes que el código (`investigacion/05` §2.2, «pruebas de aceptación primero»).

1. **Matriz requisito → prueba** en la ficha del segmento (P1; `investigacion/05` §2.4): cada `CA-n`
   con el nombre de su prueba.
2. Datos de prueba: `PedidoMadre` y su builder (T4), `RelojMovible` si hay plazos (T5).
3. Pruebas de dominio (T1) y, si P4 lo pide, propiedades (T2).
4. Dobles en memoria y contratos: **los genera la plantilla de origen** (`caso-de-uso` T8-T9,
   `adaptador-persistencia` §10.6); aquí solo se confirma que existen y se agrega el espía de T3 si hay
   un puerto hacia algo ajeno.
5. Prueba del caso de uso con fakes (`caso-de-uso` T10, más T3).
6. Reglas de arquitectura: `ModularidadTest` (T10) y `ReglasDePruebasTest` (T11).
7. Pruebas de adaptadores: web (T6), contenedor compartido (T7) y subclase real de cada contrato (T7,
   T8); Pact del consumidor si P12 lo pide (T9).
8. Prueba de módulo con `Scenario` (T10; eventos en `evento-mensaje` T6).
9. Concurrencia si P16 lo pide (T12).
10. 1-3 pruebas de sistema (T13).
11. Configuración de ejecución: Surefire, Failsafe, PIT y orden aleatorio (T14).

Al cerrar el segmento: corre `mvn verify` y PIT sobre lo nuevo; cada mutante vivo en dominio o
aplicación es una observación `todo (test)` o un `question` si la ESPEC no decide el borde
(`investigacion/05` §3.3).

## 12 · Referencias

**Pirámide y formas de la cartera**
- Mike Cohn, *Succeeding with Agile: Software Development Using Scrum* (Addison-Wesley, 2009). Origen
  de la «Test Automation Pyramid» (unidad, servicio, UI). Libro; se verificó la atribución en Fowler.
- Martin Fowler, *TestPyramid* (1-may-2012). Verificado: «a way of thinking about how different kinds of
  automated tests should be used to create a balanced portfolio»; el cono de helado; las de UI son
  «brittle, expensive to write, and time consuming to run». https://martinfowler.com/bliki/TestPyramid.html
- Ham Vocke, *The Practical Test Pyramid* (martinfowler.com, 2018). Verificado: «Write lots of small and
  fast unit tests»; «Push your tests as far down the test pyramid as you can»; pruebas sociables;
  contratos con Pact; el riesgo de H2. https://martinfowler.com/articles/practical-test-pyramid.html
- Martin Fowler, *On the Diverse And Fantastical Shapes of Testing* (2021). Verificado: el debate
  pirámide/panal es en gran parte semántico (sociable/solitaria); cita a Justin Searls («Focus on that
  instead»). https://martinfowler.com/articles/2021-test-shapes.html
- Martin Fowler, *UnitTest* (pruebas sociables y solitarias). https://martinfowler.com/bliki/UnitTest.html
- Kent C. Dodds, *The Testing Trophy and Testing Classifications* (3-jun-2021). Verificado: capas
  estática, unitaria, integración, punta a punta; «The more your tests resemble the way your software is
  used, the more confidence they can give you»; «Write tests. Not too many. Mostly integration.» es de
  Guillermo Rauch (2016). https://kentcdodds.com/blog/the-testing-trophy-and-testing-classifications
- André Schaffer y Rickard Dybeck, *Testing of Microservices* (Spotify Engineering, ene-2018).
  Verificado: el panal; «focus on Integration Tests, have a few Implementation Detail Tests and even
  fewer Integrated Tests (ideally none)». https://engineering.atspotify.com/2018/01/testing-of-microservices
- Alister Scott, *Testing Pyramids & Ice-Cream Cones* (watirmelon, 2012). Origen del cono de helado. No
  se releyó; la idea se verificó en Fowler y en SWE cap. 11.
- Fabio Pereira, *Introducing the Software Testing Cupcake (Anti-Pattern)* (ThoughtWorks, 2014). No se
  releyó para esta plantilla.

**Libros**
- Titus Winters, Tom Manshreck y Hyrum Wright (eds.), *Software Engineering at Google* (O'Reilly, 2020),
  caps. 11-14. Verificado en la versión abierta: tamaños (un proceso / una máquina / donde sea), alcance
  y la mezcla ~80/15/5; intermitencia (0,15 % en Google; cerca del 1 % las pruebas pierden valor); cono de
  helado y reloj de arena (cap. 11); API pública, estado y no interacciones, comportamientos y no
  métodos, nombres, sin lógica, DAMP (cap. 12); preferir la real, luego el fake; el fake necesita sus
  propias pruebas y lo mantiene el dueño de la real; cuándo sí interacción (cap. 13).
  https://abseil.io/resources/swe-book/html/ch11.html · ch12 · ch13 · ch14
- Kent Beck, *Test-Driven Development: By Example* (Addison-Wesley, 2002). Rojo-verde-refactorizar,
  lista de pruebas, *Fake It*, triangulación. Libro; no se releyó.
- Kent Beck, *Test Desiderata* (2019). Verificado: las 12 propiedades y que unas se refuerzan y otras se
  estorban. https://testdesiderata.com/
- Steve Freeman y Nat Pryce, *Growing Object-Oriented Software, Guided by Tests* (Addison-Wesley, 2009).
  Doble lazo, esqueleto andante, «only mock types that you own», *Test Data Builders* (cap. 22). Libro;
  no se releyó.
- Gerard Meszaros, *xUnit Test Patterns* (Addison-Wesley, 2007). Dobles (Dummy, Stub, Spy, Mock, Fake),
  *Four-Phase Test*, *Fresh/Shared Fixture*, *Humble Object* y olores (*Fragile Test*, *Mystery Guest*,
  *Assertion Roulette*, *Eager Test*, *Conditional Test Logic*, *Test Logic in Production*,
  *Interacting Tests*). http://xunitpatterns.com/ (no respondió el 15-oct-2026, como en `caso-de-uso`).
- Vladimir Khorikov, *Unit Testing Principles, Practices, and Patterns* (Manning, 2020). Escuelas clásica
  y de Londres (cap. 2), AAA (cap. 3), los cuatro pilares (cap. 4), mocks y fragilidad (cap. 5),
  dependencias gestionadas y no gestionadas (cap. 8-9), BD en pruebas (cap. 10), antipatrones (cap. 11).
  Libro; los pilares y la regla de los mocks se confirmaron en resúmenes secundarios, no en el texto.
  https://www.manning.com/books/unit-testing
- Martin Fowler, *Mocks Aren't Stubs* (2007). Verificación de estado frente a comportamiento, clasicistas
  y mockistas, vocabulario de Meszaros. https://martinfowler.com/articles/mocksArentStubs.html
- Brian Goetz et al., *Java Concurrency in Practice* (2006), cap. 12 «Testing Concurrent Programs».
  Libro; no se releyó.
- Neal Ford, Rebecca Parsons y Patrick Kua, *Building Evolutionary Architectures* (O'Reilly, 2017).
  Funciones de aptitud. Libro; no se releyó.
- Tom Hombergs, *Get Your Hands Dirty on Clean Architecture*, 2.ª ed. (2023), cap. 7 «Testing
  Architecture Elements». Una prueba por tipo de pieza del hexágono; buckpal es su código.

**Artículos y estilo**
- Martin Fowler, *Eradicating Non-Determinism in Tests* (14-abr-2011). Verificado: cuarentena con tope
  («only allow 8 tests», «no longer than a week»), aislamiento, nada de *sleeps* desnudos, dobles +
  contratos para lo remoto, «Always wrap the system clock». https://martinfowler.com/articles/nonDeterminism.html
- John Micco, *Flaky Tests at Google and How We Mitigate Them* (Google Testing Blog, 2016). No se releyó.
- Martin Fowler, *TestCoverage* (2012). Verificado: útil para hallar lo no probado, «of little use as a
  numeric statement»; sospecha de cifras cercanas a 100 %. https://martinfowler.com/bliki/TestCoverage.html
- Martin Fowler, *ContractTest* y *ObjectMother*; Ian Robinson, *Consumer-Driven Contracts* (2006).
  https://martinfowler.com/bliki/ContractTest.html · https://martinfowler.com/bliki/ObjectMother.html ·
  https://martinfowler.com/articles/consumerDrivenContracts.html
- J. B. Rainsberger, *Getting Started with Contract Tests* (verificado en `caso-de-uso`) e *Integrated
  Tests Are A Scam* (2009, citado por Spotify).
- James Shore, *Testing Without Mocks: A Pattern Language* (16-feb-2023). Verificado: Nullables
  («production code with an "off" switch»), *Configurable Responses*, *Output Tracking*, *State-Based
  Tests*, *Overlapping Sociable Tests*. https://www.jamesshore.com/v2/projects/nullables/testing-without-mocks
- Bill Wake, *3A – Arrange, Act, Assert* (2001); Dan North, *Introducing BDD* (2006); Martin Fowler,
  *GivenWhenThen* (2013). https://xp123.com/3a-arrange-act-assert/ · https://dannorth.net/introducing-bdd/ ·
  https://martinfowler.com/bliki/GivenWhenThen.html
- DHH, *TDD is dead. Long live testing.* y *Test-induced design damage* (2014); Fowler, Beck y DHH, *Is
  TDD Dead?* (conversaciones, 2014). https://martinfowler.com/articles/is-tdd-dead/
- Goran Petrović, Marko Ivanković, Gordon Fraser y René Just, *Practical Mutation Testing at Scale: A
  view from Google* (IEEE TSE, 2021). Verificado el resumen: mutación solo sobre el código cambiado en
  revisión, filtrado de mutantes poco útiles, más de 24 000 desarrolladores.
  https://research.google/pubs/pub50658/
- Koen Claessen y John Hughes, *QuickCheck: A Lightweight Tool for Random Testing of Haskell Programs*
  (ICFP 2000). Origen de las pruebas de propiedades.

**Herramientas (documentación oficial)**
- PIT. Verificado: definición de mutante muerto o vivo; la cobertura «measures only which code is
  executed»; parámetros `mutationThreshold`, `coverageThreshold`, `withHistory`, `targetClasses`,
  `targetTests`, `threads`. https://pitest.org/ · https://pitest.org/quickstart/maven/ · Descartes:
  https://github.com/STAMP-project/pitest-descartes
- jqwik User Guide: `@Property`, `@ForAll`, `@Provide`, `Arbitraries`, *shrinking*, `ActionChain`.
  https://jqwik.net/docs/current/user-guide.html
- Testcontainers for Java, módulo PostgreSQL (artefacto `testcontainers-postgresql` 2.0.5).
  https://java.testcontainers.org/modules/databases/postgres/
- Spring Boot 4.0 Migration Guide. Verificado: se retiró `@MockBean`/`@SpyBean` (usar `@MockitoBean`,
  `@MockitoSpyBean`); *starters* de prueba por tecnología; `@SpringBootTest` ya no trae MockMvc
  (`@AutoConfigureMockMvc`) ni `TestRestTemplate` (`@AutoConfigureTestRestTemplate`, paquete
  `org.springframework.boot.resttestclient`) y se sugiere `RestTestClient`; se retiró la integración con
  Spock. https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Migration-Guide
- Spring Boot 4.1 API: `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`,
  `org.springframework.boot.jdbc.test.autoconfigure.JdbcTest` y `AutoConfigureTestDatabase`,
  `org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient` (todas desde 4.0;
  verificadas). https://docs.spring.io/spring-boot/api/java/
- Spring Framework 7, *RestTestClient*. Verificado: `post().uri(...).body(...).exchange().expectStatus()`,
  `expectBody().jsonPath(...)`. https://docs.spring.io/spring-framework/reference/testing/resttestclient.html
- Spring Framework, *Context Caching* (TestContext). https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/ctx-management/caching.html
- Spring Modulith, *Integration Testing Application Modules*. Verificado: modos `STANDALONE`,
  `DIRECT_DEPENDENCIES`, `ALL_DEPENDENCIES`; `@MockitoBean` para beans de otros módulos; `Scenario`
  (`stimulate`, `publish`, `andWaitForEventOfType`, `matching`, `andWaitForStateChange`, `andVerify`,
  `toArrive`, `toArriveAndVerify`). https://docs.spring.io/spring-modulith/reference/testing.html
- ArchUnit User Guide: `onionArchitecture()`, `layeredArchitecture()`, `GeneralCodingRules`,
  `@AnalyzeClasses`, `@ArchTest`. https://www.archunit.org/userguide/html/000_Index.html
- Pact JVM (JUnit 5 consumidor y proveedor) https://docs.pact.io/implementation_guides/jvm · Spring Cloud
  Contract https://spring.io/projects/spring-cloud-contract
- WireMock 3 (extensión JUnit Jupiter `WireMockExtension`). https://wiremock.org/docs/junit-jupiter/
- Awaitility. https://github.com/awaitility/awaitility/wiki/Usage
- Maven Surefire y Failsafe (inclusiones por defecto `*Test` e `*IT`).
  https://maven.apache.org/surefire/maven-failsafe-plugin/

**Repositorios de §9:** thombergs/buckpal (sin licencia), ddd-by-examples/library (MIT),
TNG/ArchUnit-Examples (Apache-2.0), spring-projects/spring-modulith (Apache-2.0),
testcontainers/testcontainers-java-spring-boot-quickstart (MIT), sttp-book/code-examples (sin licencia),
jqwik-team/jqwik (EPL-2.0), jitterted/yacht-tdd (Apache-2.0),
pact-foundation/pact-workshop-Maven-Springboot-JUnit5 (MIT), spring-projects/spring-ai (Apache-2.0).

**Internas:** `investigacion/02` (#15, #21, #25), `investigacion/04` (§1.2, §2.2, §5.1),
`investigacion/05` (§2.2, §2.4, §3.3, §4.1, §6); plantillas `caso-de-uso` (T8-T10, U-20, U-21, U-24),
`adaptador-persistencia` (§9.6, §9.7, §10.6), `endpoint` (§9.2, §9.9, §9.10), `evento-mensaje` (T6),
`entidad-agregado` y `trabajo-programado`; en este proyecto, `.claude/contexto/IA_TECNOLOGIAS-vigentes.md`
(TEC-26: JUnit 6.0.3, AssertJ 3.27.7, Testcontainers 2.0.5).
