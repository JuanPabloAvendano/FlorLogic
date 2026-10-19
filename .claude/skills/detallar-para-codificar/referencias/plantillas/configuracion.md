---
plantilla: configuracion
aplica_a: parámetro de configuración técnica (application.yml, variables de entorno, secretos), configuración de negocio versionada (paquete inmutable, publicado y firmado, con JSON Schema) y bandera de funcionalidad
entra_desde: caso-de-uso, entidad-agregado, endpoint, adaptador-persistencia, evento-mensaje (sus variables «configurable» o «pendiente»)
sale_hacia: caso-de-uso (puerto de configuración que consume), adaptador-persistencia (si el paquete de negocio se guarda), evento-mensaje (si la publicación de un paquete se avisa)
---
# Plantilla · Parámetro de configuración

## 0 · Instrucciones para el agente

1. Usa esta plantilla cuando otra plantilla dejó una variable `V-n` marcada **configurable** o
   **pendiente** (P11 de `caso-de-uso` y `endpoint`), o cuando el humano pide detallar un valor
   configurable, un paquete de reglas de negocio o una bandera.
2. Antes de la P1, muestra el esqueleto de §1 con los huecos ⟦1⟧…⟦12⟧ y la opción ★ como valor
   provisional (`investigacion/04` §2.3).
3. Detalla **un parámetro o un grupo cohesionado por ronda** (un record `@ConfigurationProperties`,
   un paquete de negocio o una bandera). Si aparecen más, regístralos como `Q-n` y sigue.
4. Haz las preguntas en orden y respeta la poda de §6. La P2 y la P3 deciden qué rama sigue.
5. Cada respuesta produce una variable con la forma de `investigacion/04` §4:
   `V-n: clave = valor unidad (rango, defecto) [fuente · dueño · momento de lectura] [Pk]`.
6. No inventes rangos ni valores por defecto. Si el humano no sabe, deja `Q-n` con el supuesto ★ y
   anota quién lo decide (`investigacion/04` regla 7).
7. Quedan fuera de esta plantilla, y se pasan a otra:
   - qué hace el caso de uso con el valor queda en `caso-de-uso`;
   - las tablas donde se guarda un paquete de negocio o su historial van a `adaptador-persistencia`;
   - el evento «paquete publicado» y su sobre van a `evento-mensaje`.
   De aquí salen el record de propiedades, el value object de política, el puerto de configuración y
   sus pruebas de arranque.
8. Si la P2 resulta **fija**, cierra en tres preguntas (P1, P2, P5) con una constante con nombre y una
   línea de ADR. No hagas la ronda para algo que no es configuración (12-factor, *Config*).

## 1 · Qué se define primero

El orden sigue la dependencia: sin saber qué decide (⟦1⟧) no se puede clasificar (⟦2⟧), y sin la
clase no se sabe la fuente ni el ciclo de vida. Los tres primeros son esenciales (regla dura de
cobertura, `investigacion/04` §5.1).

| ⟦k⟧ | Concepto | Por qué va aquí | Fuente |
|---|---|---|---|
| 1 | **Decisión que controla** y nombre por su función | Un parámetro sin propósito claro no se puede validar ni retirar. Los nombres de propiedades forman parte de la interfaz de usuario del sistema. | SRE Workbook cap. 14; *Release It!* 1.ª ed., «Configuration Files» |
| 2 | **Clase: fija, configurable o pendiente** | Es config solo lo que varía entre despliegues; el resto es código. | 12-factor, *Config* |
| 3 | **Tipo de configurable**: técnica, de negocio versionada o bandera | Cada tipo tiene otra fuente, otro dueño y otro ciclo de vida. | Hodgson, *Feature Toggles*; SRE Workbook cap. 14 [opinión: la división en tres] |
| 4 | **Tipo de dato y unidad** | `48` sin unidad es ambiguo; `Duration` y `DataSize` lo resuelven. | Spring Boot ref., *Externalized Configuration* (conversión) |
| 5 | **Valor por defecto u obligatorio** | Los valores obligatorios deben ser pocos; los opcionales necesitan un defecto seguro. | SRE Workbook cap. 14 |
| 6 | **Rango válido y validación al arrancar** | Un valor inválido debe tumbar el arranque, no la primera petición. | *Release It!* «Fail Fast»; Spring Boot `@Validated` |
| 7 | **Secreto o no** | Si el repo no se puede publicar sin filtrar credenciales, la config está mal separada. | 12-factor; OWASP *Secrets Management* |
| 8 | **Fuente y precedencia** | Decide quién gana cuando el valor aparece en varios sitios. | Spring Boot ref., orden de `PropertySource` |
| 9 | **Momento de lectura**: al arrancar o en caliente | El cambio en caliente necesita instantánea por operación y trazabilidad. | Hodgson (configuración estática o dinámica) |
| 10 | **Entrada al dominio** | El dominio recibe valores o un puerto, nunca `Environment` ni anotaciones. | Cockburn, *configurable dependency*; Seemann, *Primitive Dependencies* |
| 11 | **Versión, inmutabilidad y compatibilidad** (configuración de negocio) | Versionar la configuración permite volver atrás; sin una versión no se sabe qué regla se aplicó. | SRE Workbook cap. 14; TUF spec; Confluent, *Schema Evolution* |
| 12 | **Dueño, documentación y retiro** | Cada fragmento de configuración necesita un dueño claro, y las banderas tienen un costo mientras viven. | SRE Workbook cap. 14; Hodgson (*carrying cost*) |

Esqueleto que se muestra antes de la P1:

```
Parámetro:     ⟦1 decisión + clave⟧     → pedidos.vencimiento.plazo: cancela pedidos sin confirmar
Clase:         ⟦2⟧                      → configurable (varía entre la finca piloto y el resto)
Tipo:          ⟦3⟧                      → técnica (application.yml + variable de entorno)
Dato:          ⟦4 tipo + unidad⟧        → java.time.Duration, en horas («48h»)
Defecto:       ⟦5⟧                      → 48h · obligatorio: no
Rango:         ⟦6⟧                      → 1h..720h, validado al arrancar (@Validated)
Secreto:       ⟦7⟧                      → no
Fuente:        ⟦8⟧                      → yml empaquetado < PEDIDOS_VENCIMIENTO_PLAZO
Lectura:       ⟦9⟧                      → al arrancar; cambiarlo exige reiniciar
Dominio:       ⟦10⟧                     → value object PlazoDeVencimiento pasado al caso de uso
Versión:       ⟦11⟧                     → N/A (técnica) · negocio: politica-envios v3, firmada
Dueño/retiro:  ⟦12⟧                     → equipo de pedidos · Javadoc → metadatos del IDE
```

## 2 · Funcionamiento apropiado

**Responsabilidad.** Un parámetro bien hecho nace en un único lugar tipado, se valida una vez al
cargarse, entra al núcleo como un valor del dominio y deja rastro de qué versión se aplicó. La
lectura del entorno vive en el borde (la raíz de composición). El dominio no sabe si el valor vino de
YAML, de una variable o de un paquete firmado (Seemann: «This is still part of the Composition Root»).

**Tres tipos, tres ciclos de vida** [opinión: síntesis de 12-factor, Hodgson y SRE Workbook]:

| Tipo | Ejemplo | Fuente | Cambia | Dueño | Se valida |
|---|---|---|---|---|---|
| Técnica | URL de la BD, *timeouts*, tamaño de pool | `application.yml` + variables de entorno + gestor de secretos | con un redespliegue | operación | al arrancar (`@Validated`) |
| Negocio versionada | tarifas, umbrales, reglas de elegibilidad | paquete JSON inmutable, publicado y firmado, con JSON Schema | al publicar una versión nueva | negocio | al publicar (esquema y semántica) y al cargar (firma, versión) |
| Bandera | activar el despacho por lotes | proveedor de banderas (OpenFeature, Unleash) o archivo | en caliente | producto u operación | al registrarla (categoría, fecha de retiro) |

**Flujo de la configuración técnica:**

```
arranque (borde)                                    núcleo
────────────────                                    ──────
1 Spring une las fuentes por precedencia ─▶ yml < env < línea de comandos
2 enlaza el record @ConfigurationProperties ─▶ PedidosProperties(plazo=48h)
3 @Validated ── inválido → BindValidationException → «APPLICATION FAILED TO START»
4 @Configuration (raíz de composición) ─────▶ new PlazoDeVencimiento(props.plazo())
5 ────────────────────────────────────────▶ CancelarVencidos(plazo, reloj, pedidos)
```

**Flujo de la configuración de negocio versionada:**

```
publicación (fuera del proceso)              arranque o recarga (adaptador)        núcleo
──────────────────────────────               ──────────────────────────────        ──────
redacta politica-envios v4.json               1 lee el paquete y su firma
valida con politica-envios.schema.json        2 verifica la firma (Ed25519)  ─✗─▶ rechaza; sigue la v3
revisión + firma (JWS) + publica inmutable    3 versión ≥ vigente, no caducada ─✗─▶ rechaza (anti-rollback)
                                              4 valida esquema + reglas semánticas
                                              5 mapea a PoliticaDeEnvios(v4) ─────▶ puerto PoliticasVigentes
                                              6 cada decisión guarda «version=4» ──▶ auditoría
```

**Dónde vive cada pieza** (`investigacion/02` §2):

| Pieza | Paquete | Depende de |
|---|---|---|
| Record `@ConfigurationProperties` | `pedidos.config` (borde) | Spring Boot, Jakarta Validation |
| Value object de política (`PlazoDeVencimiento`, `PoliticaDeEnvios`) | `pedidos.domain` | solo el JDK |
| Puerto de configuración (`PoliticasVigentes`) | `pedidos.application.port.out` | tipos del dominio |
| Adaptador que carga, verifica y valida el paquete | `pedidos.adapter.out.politicas` | Jackson, validador de JSON Schema, JOSE |
| Decisión de bandera (`DecisionesDeDespacho`) | `pedidos.application.port.out` (interfaz) + adaptador OpenFeature | OpenFeature SDK solo en el adaptador |
| Raíz de composición (`@Configuration`) | `pedidos.config` | todo |

## 3 · Patrones de diseño

| Patrón | Problema que resuelve | Úsalo cuando | No lo uses cuando | Fuente |
|---|---|---|---|---|
| Configuración externalizada | El mismo artefacto debe correr en varios entornos | algo varía entre despliegues | el valor nunca cambia (es código) | 12-factor *Config*; microservices.io *Externalized configuration* |
| Objeto de configuración tipado (record `@ConfigurationProperties`) | Claves sueltas con `@Value`, sin tipo ni validación | siempre que haya más de una clave o haga falta validarlas | un único valor trivial en un adaptador [opinión] | Spring Boot ref. (comparativa con `@Value`) |
| Validación al arrancar (*Fail Fast*) | El error aparece horas después, en la primera petición | todo valor con rango o formato | el valor se resuelve en caliente (se valida al recargar) | Nygard, *Release It!* «Fail Fast» |
| Defecto seguro | Configuración obligatoria excesiva | casi siempre; el defecto debe ser el comportamiento más seguro | secretos y URLs de producción (sin defecto) | SRE Workbook cap. 14 |
| *Configurable dependency* / raíz de composición | El núcleo leería el entorno directamente | siempre | — | Cockburn; Seemann, *Primitive Dependencies* |
| Value object de política | Un `int` o `Duration` suelto pierde reglas y significado | el valor tiene rango, unidad o reglas | es solo técnico y vive en un adaptador | Evans (Value Objects); `entidad-agregado` T2 |
| Puerto de configuración | El valor cambia en caliente o viene versionado | configuración de negocio o recargable | valor estático de arranque (basta pasar el VO) [opinión] | Cockburn (puerto secundario) |
| Configuración como código | Cambios sin revisión, sin historial ni pruebas | configuración de negocio y banderas estáticas | — | Tang et al. 2015 (Configerator); Hodgson |
| Paquete inmutable versionado | No se sabe qué regla se aplicó ni se puede volver atrás | reglas de negocio auditables | parámetros técnicos de despliegue | SRE Workbook cap. 14 (versionado, hermético) |
| Metadatos firmados con versión y caducidad | Paquetes alterados, viejos (rollback) o congelados | el paquete cruza una red o lo distribuye otro sistema | el paquete viaja dentro del artefacto firmado | TUF spec; sigstore-java `Updater` |
| Evolución compatible del esquema (*parallel change*) | Renombrar una clave rompe a quien aún lee la vieja | cambiar claves o campos del paquete | despliegue atómico de productor y consumidor | Fowler, *ParallelChange*; Confluent |
| Enrutador de banderas / objeto de decisiones | Cadenas mágicas y lógica de bandera repartida por el código | siempre que haya banderas | — | Hodgson («decoupling decision points from decision logic») |
| Inversión de decisión + Strategy | `if (flag)` en el núcleo | banderas de vida larga o con varios puntos de decisión | bandera de lanzamiento de días | Hodgson («inversion of decision», «avoiding conditionals») |
| Interruptor de apagado (*kill switch*) | Degradar una función bajo carga sin redesplegar | toggles de operación | la decisión no se toma en caliente | Hodgson (Ops Toggles) |
| Última versión buena conocida | El proveedor remoto no responde al arrancar | banderas y paquetes remotos | el valor es crítico para la seguridad (falla cerrado) | Unleash `unleash-repo.json` y *bootstrap* |

**Decisiones de patrón que cierra la ronda:**
- clase y tipo de configurable: P2 y P3;
- defecto seguro u obligatorio que falla al arrancar: P6;
- entrada al dominio (VO pasado al construir ★, puerto o lectura directa rechazada): P14;
- falla cerrado o sigue con la última buena: P18;
- bandera con objeto de decisión ★ o `if` disperso: P19.

## 4 · Prácticas profesionales verificables

Severidad: C = crítica (3), M = mayor (2), m = menor (1). Verificación: A = ArchUnit, P = prueba,
L = lectura.

| id | Práctica | Sev. | Al detallar | Al verificar | Fuente |
|---|---|---|---|---|---|
| CF-01 | Ningún secreto en el repositorio: ni en `application.yml`, ni en claves `.pem`, ni en pruebas | C | P9 | L + escáner de secretos en *pre-commit* y CI (p. ej. gitleaks) | 12-factor; OWASP *Secrets Management* |
| CF-02 | El dominio no importa `org.springframework.core.env`, `@Value` ni `@ConfigurationProperties` | C | P14 | A: `noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat().resideInAnyPackage("org.springframework..")` | Cockburn; `investigacion/02` #1 |
| CF-03 | Toda clave se enlaza a un record `@ConfigurationProperties`; no hay `@Value` disperso | M | P8 | A: ninguna clase fuera de `..config..` usa `@Value`; L | Spring Boot ref. (`@Value` frente a `@ConfigurationProperties`) |
| CF-04 | Valores con rango se validan al arrancar con `@Validated` y restricciones de Jakarta; los anidados llevan `@Valid` | C | P7 | P: `ApplicationContextRunner` con un valor fuera de rango espera el fallo del contexto | Spring Boot ref. (validación); *Release It!* «Fail Fast» |
| CF-05 | Tiempos con `Duration` y tamaños con `DataSize`; nada de `int segundos` sin unidad | M | P5 | L: no hay `long`/`int` con sufijo `Ms`/`Seconds` en propiedades | Spring Boot ref. (conversión) |
| CF-06 | Defecto seguro declarado con `@DefaultValue`; lo obligatorio no tiene defecto y falla al arrancar | M | P6 | P: arranque sin la clave obligatoria falla; sin la opcional toma el defecto | SRE Workbook cap. 14; Spring Boot `@DefaultValue` |
| CF-07 | Cada variable de entorno sobrescribe una sola clave; no hay combinatoria de perfiles por entorno | M | P11 | L: perfiles solo para modos (`test`, `local`), no `prod-eu-v2` | 12-factor («never grouped together as environments») |
| CF-08 | Las propiedades se documentan con Javadoc (`@param` en records) y se genera la metadata | m | P8 | L: existe `META-INF/spring-configuration-metadata.json` con `description` | Spring Boot, *Configuration Metadata* |
| CF-09 | El núcleo recibe value objects o un puerto; la lectura del entorno vive en la raíz de composición | C | P14 | A: `..application..` y `..domain..` no dependen de `..config..` | Seemann; Cockburn |
| CF-10 | El paquete de negocio es inmutable: la versión publicada no se edita, se publica otra | C | P16 | P: publicar dos veces la misma versión con otro contenido se rechaza | SRE Workbook cap. 14 (versionado, hermético) |
| CF-11 | El paquete se valida contra su JSON Schema y contra reglas semánticas antes de publicarse y al cargarse | C | P15 | P: un paquete inválido no llega al puerto; prueba del esquema con casos dorados | SRE Workbook (validación semántica); JSON Schema 2020-12 |
| CF-12 | Se verifica la firma y se rechaza una versión menor que la vigente o una caducada | C | P16 | P: firma alterada, versión menor y caducidad → rechazo | TUF spec §5; sigstore-java `Updater` |
| CF-13 | Cada decisión de negocio registra la versión de la política que aplicó | M | P13 | P: el registro de la decisión lleva `politicaVersion` | [opinión] derivada de SRE Workbook («go back in time») |
| CF-14 | Una operación usa una sola instantánea de la configuración, aunque cambie a mitad | M | P13 | P: recarga concurrente no mezcla versiones en una operación | [opinión] |
| CF-15 | Los cambios de esquema o de clave son compatibles hacia atrás o siguen *parallel change* con deprecación | M | P17 | L: `@DeprecatedConfigurationProperty(replacement=…)`; P: el lector acepta la versión anterior | Fowler, *ParallelChange*; Spring Boot `spring-boot-properties-migrator` |
| CF-16 | Las banderas se consultan por un objeto de decisión con nombre de negocio, no con cadenas sueltas | M | P19 | A o L: `OpenFeatureAPI`/`Unleash` solo en `..adapter..` | Hodgson |
| CF-17 | Cada bandera tiene categoría, dueño, valor por omisión seguro y fecha de retiro | M | P19 | P: prueba «bomba de tiempo» que falla tras la fecha de retiro | Hodgson (*expiration dates*, *time bombs*) |
| CF-18 | Se prueban los dos estados de cada bandera y la configuración de producción | M | P20 | P: pruebas con la bandera encendida y apagada (`FakeUnleash`, `InMemoryProvider`) | Hodgson (*Testing*) |
| CF-19 | La configuración vigente se puede consultar sin exponer secretos (`/actuator/configprops` con saneado, versión de la política) | m | P12 | P: el endpoint muestra la versión y oculta `password`/`token` | Spring Boot Actuator; Hodgson («exposing current configuration») |
| CF-20 | Cada parámetro de negocio y cada bandera tiene un dueño nombrado | m | P4 | L: tabla de variables con dueño | SRE Workbook cap. 14 («clear owner») |

## 5 · Árbol de preguntas

Formato: título de una línea + contexto pequeño o mediano con el **por qué** (SKILL.md, «Cómo preguntas») · opciones con la ★ primero y un ejemplo cada una ·
**cierra**.

**A · Propósito y clasificación**

**P1. ¿Qué decisión concreta controla este valor, en una frase?**
Por qué: un parámetro sin propósito claro no se puede validar, documentar ni retirar.
1. ★ Una sola decisión con nombre de negocio — `pedidos.vencimiento.plazo`: «cancela pedidos sin confirmar pasado el plazo»
2. Varias decisiones a la vez (se parte) — `pedidos.modo=estricto` cambia el plazo, el cupo y el aviso → tres claves
3. No se sabe todavía — `Q-1: ¿qué decide el «umbral»? supuesto: plazo de vencimiento`
Cierra: ⟦1⟧.

**P2. ¿Este valor es fijo, configurable o pendiente?**
Por qué: es config solo lo que varía entre despliegues; lo demás es código (12-factor).
1. ★ Configurable: cambia entre despliegues, fincas o con el tiempo — `PEDIDOS_VENCIMIENTO_PLAZO=24h` solo en el piloto
2. Fijo: igual en todo despliegue → constante con nombre — `static final int MAX_LINEAS = 50;` + línea de ADR
3. Pendiente: se decide luego → defecto provisional marcado — `Q-2: plazo; supuesto 48h; decide: negocio`
Cierra: ⟦2⟧.

**P3. ¿Qué tipo de configurable es?**
Por qué: cada tipo tiene otra fuente, otro dueño y otro ciclo de vida (§2).
1. ★ Técnica de despliegue — `spring.datasource.url`, `envios.cliente.timeout=2s`
2. De negocio versionada — `politica-envios` v3: tarifas por zona en un paquete firmado
3. Bandera de funcionalidad — `despacho-por-lotes` encendida para la finca piloto
Cierra: ⟦3⟧.

**P4. ¿Quién es el dueño y quién puede cambiarlo?**
Por qué: cada fragmento de configuración necesita un dueño claro (SRE Workbook).
1. ★ Un rol nombrado y el mismo flujo de revisión que el código — «negocio publica; revisa pedidos»
2. Operación, mediante el entorno de despliegue — `kubectl set env` o el `.env` del servidor
3. Cualquiera desde una pantalla, sin revisión (se rechaza) — tabla `parametros` editable sin auditoría
Cierra: ⟦12⟧, CF-20.

**B · Forma y validación**

**P5. ¿De qué tipo es y en qué unidad se expresa?**
Por qué: `48` sin unidad es ambiguo; el binder de Spring convierte `48h` a `Duration`.
1. ★ Tipo con unidad — `Duration plazo` con `48h`; `DataSize` con `10MB`
2. Tipo de dominio enlazable — `Set<CodigoZona>`, `enum ModoDespacho { LOTE, UNITARIO }`
3. Cadena o número sueltos (se rechaza) — `String plazo = "48"`, `int timeoutMs`
Cierra: ⟦4⟧, CF-05.

**P6. ¿Tiene un valor por defecto seguro o es obligatorio?**
Por qué: lo obligatorio debe ser poco y, si falta, fallar al arrancar (SRE Workbook; *Fail Fast*).
1. ★ Defecto seguro, el de producción razonable — `@DefaultValue("48h") Duration plazo`
2. Obligatorio, sin defecto; el arranque falla si falta — `@NotNull URI urlInventario`
3. Defecto «de desarrollo» que sirve en producción por descuido (se rechaza) — `password=postgres`
Cierra: ⟦5⟧, CF-06.

**P7. ¿Qué rango es válido y cuándo se valida?**
Por qué: un valor fuera de rango debe tumbar el arranque, no la primera petición.
1. ★ Al arrancar, con `@Validated` + restricciones — `@DurationMin(hours = 1) @DurationMax(days = 30)`
2. En el constructor compacto del record o del VO — `if (plazo.isNegative()) throw …`
3. Al usarse, en tiempo de ejecución (se desaconseja) — el error aparece a las 3 a. m.
Cierra: ⟦6⟧, CF-04.

**P8. ¿Cómo se llama la clave y dónde se documenta?**
Por qué: las propiedades forman parte de la interfaz de usuario del sistema (*Release It!*).
1. ★ Prefijo del módulo en kebab-case + Javadoc `@param` → metadata — `pedidos.vencimiento.plazo`
2. Nombre por su naturaleza técnica (se desaconseja) — `pedidos.timeout1`, `app.valor-x`
3. Documentación aparte, en una wiki — se desactualiza [opinión]
Cierra: ⟦1⟧, ⟦12⟧, CF-03, CF-08.

**C · Origen**

**P9. ¿Es un secreto?**
Por qué: prueba de 12-factor: ¿podría publicarse el repo ya mismo sin filtrar credenciales?
1. ★ Sí → gestor de secretos (Vault, `configtree:` de Kubernetes o Docker), nunca en el repo — `spring.config.import=vault://`
2. Sí, pero solo hay variables de entorno → variable inyectada por el despliegue — `DB_PASSWORD` desde el orquestador
3. No — `pedidos.vencimiento.plazo`
Cierra: ⟦7⟧, CF-01.

**P10. ¿De qué fuente sale y cuál gana si aparece en varias?**
Por qué: Spring une las fuentes por precedencia; la última gana (Spring Boot ref.).
1. ★ Defecto en el record < `application.yml` empaquetado < variable de entorno — `PEDIDOS_VENCIMIENTO_PLAZO`
2. Paquete de negocio cargado por un adaptador propio — `politicas/politica-envios-v3.json` + `.jws`
3. Proveedor remoto de banderas o de config — OpenFeature + flagd, Unleash, Spring Cloud Config
Cierra: ⟦8⟧.

**P11. ¿Cómo cambia entre entornos?**
Por qué: 12-factor desaconseja agrupar la configuración en «entornos» con nombre.
1. ★ Cada variable por separado, en cada despliegue — `PEDIDOS_VENCIMIENTO_PLAZO=24h` solo en el piloto
2. Perfil para un modo de ejecución, no para un entorno — `application-local.yml` con Testcontainers
3. Un archivo por entorno con todo duplicado (se desaconseja) — `application-prod-finca2.yml`
Cierra: ⟦8⟧, CF-07.

**D · Ciclo de vida y entrada al núcleo**

**P12. ¿Se lee al arrancar o puede cambiar sin reiniciar?**
Por qué: el cambio en caliente exige instantánea, trazabilidad y otra forma de validación (Hodgson).
1. ★ Al arrancar; cambiarlo exige redesplegar — record inmutable enlazado una vez
2. Recarga controlada de una versión completa — el adaptador cambia de la v3 a la v4 de forma atómica
3. En caliente, por clave suelta — `@RefreshScope` o un proveedor de banderas
Cierra: ⟦9⟧, CF-19.

**P13. Si cambia mientras corre una operación, ¿qué versión usa esa operación?**
Por qué: mezclar versiones en una operación produce decisiones imposibles de auditar.
1. ★ Toma una instantánea al empezar y guarda su versión — `var p = politicas.vigente(); envio.tarifar(p); registro.version(p.version())`
2. Siempre la última, sin registrar versión — aceptable solo para técnicos (un *timeout*)
3. Relee la clave en cada paso (se rechaza) — dos lecturas, dos valores
Cierra: ⟦9⟧, ⟦11⟧, CF-13, CF-14.

**P14. ¿Cómo entra el valor al caso de uso o al dominio?**
Por qué: el núcleo no debe acoplarse a Spring ni al origen del valor (Cockburn; Seemann).
1. ★ Como value object pasado al construir, en la raíz de composición — `new CancelarVencidos(new PlazoDeVencimiento(props.plazo()), reloj, pedidos)`
2. Mediante un puerto de configuración, si cambia o está versionado — `interface PoliticasVigentes { PoliticaDeEnvios vigente(); }`
3. El caso de uso inyecta el record de Spring o `Environment` (se rechaza) — `@Autowired Environment env` en `application`
Cierra: ⟦10⟧, CF-02, CF-09.

**E · Configuración de negocio versionada**

**P15. ¿Qué forma tiene el paquete y con qué se valida?**
Por qué: la validación sintáctica no basta; también hay que validar el significado (SRE Workbook).
1. ★ JSON con `$schema` 2020-12 + reglas semánticas en código — `"tarifas": [...]`, «zonas sin solapes»
2. YAML o *properties* sin esquema — solo validan tipos al enlazar
3. Filas en una tabla editable — sin versión ni firma (pasa a `adaptador-persistencia` si se elige)
Cierra: ⟦11⟧, CF-11.

**P16. ¿Cómo se publica y cómo se protege contra alteración y retroceso?**
Por qué: un paquete sin firma ni versión monótona se puede alterar o sustituir por uno viejo (TUF).
1. ★ Inmutable, versión creciente, firmado (JWS Ed25519) y con caducidad — `politica-envios` v4, `expira 2027-01-01`
2. Inmutable y versionado, sin firma, dentro del artefacto firmado — `classpath:politicas/v4.json`
3. Se edita en sitio (se rechaza) — «se cambió la tarifa de la v3»
Cierra: ⟦11⟧, CF-10, CF-12.

**P17. ¿Cómo evoluciona el esquema sin romper a quien lo lee?**
Por qué: productor y lector no se despliegan a la vez; el cambio debe ser compatible (Confluent).
1. ★ Solo cambios aditivos con defecto; quitar un campo exige versión mayor y *parallel change* — `schemaVersion: 2.1`
2. Versión mayor nueva leída en paralelo con la anterior — lector v1 y v2 durante una ventana
3. Sin regla — se renombra el campo y falla la carga en las fincas sin actualizar
Cierra: ⟦11⟧, CF-15.

**P18. ¿Qué pasa si el paquete o el proveedor no cargan?**
Por qué: decide si el sistema falla cerrado o sigue con la última versión buena conocida.
1. ★ Valor crítico: falla al arrancar; en recarga, conserva la versión vigente y alerta — log `politica v4 rechazada: firma`
2. Sigue con la última copia buena en disco — como el `unleash-repo.json` de Unleash
3. Sigue con defectos en código sin avisar (se rechaza) — tarifa 0 por error
Cierra: ⟦6⟧, ⟦11⟧.

**F · Banderas y verificación**

**P19. Si es una bandera: ¿de qué categoría es, qué devuelve si falla y cuándo se retira?**
Por qué: cada categoría tiene otra vida y otra dinámica; las banderas tienen un costo (Hodgson).
1. ★ De lanzamiento, apagada por omisión y con fecha de retiro, tras un objeto de decisión — `decisiones.despachoPorLotes(finca)`
2. De operación (*kill switch*), de vida larga y encendida por omisión — `envios.notificar-cliente`
3. De permiso o de experimento, por usuario o por segmento — contexto `fincaId` en `EvaluationContext`
Cierra: ⟦12⟧, CF-16, CF-17.

**P20. ¿Cómo se verifica?**
Por qué: la configuración se prueba como el código; un valor inválido debe fallar en CI, no en producción.
1. ★ `ApplicationContextRunner` (válido, inválido, ausente) + prueba de esquema + ArchUnit + ambos estados de cada bandera — §10 T8
2. Solo la prueba de contexto con valores por defecto — `@SpringBootTest` vacío
3. Ninguna (se rechaza)
Cierra: CF-04, CF-11, CF-12, CF-18.

## 6 · Poda y ramas nuevas

- **P2 = fija:** se cierra con P1, P5 y una línea de ADR. Las demás quedan N/A. Si sobran cupos, se
  pregunta «¿qué haría falta para que variara entre despliegues?».
- **P2 = pendiente:** se sigue la rama de P3 con supuestos ★ marcados `Q-n` y se anota quién decide.
- **P3 = técnica:** se saltan P15-P17 y P19. Los cupos van a P9 (secretos derivados: certificados,
  claves) y a contraejemplos de rango («¿y con `0s`?»).
- **P3 = negocio versionada:** se salta P11 (no cambia por entorno sino por versión) y P19. Los cupos
  van a las reglas semánticas del paquete (solapes, huecos y monedas) y a quién firma.
- **P3 = bandera:** se saltan P15-P17. P5 se reduce a `boolean` o a variante. Los cupos van al
  contexto de evaluación (qué atributos se envían) y a quién la apaga.
- **P9 = secreto:** P6 queda forzada a «obligatorio, sin defecto», y P8 añade el saneado en
  `/actuator/configprops`. Se pregunta la rotación («¿cada cuánto se rota y quién?», OWASP).
- **P12 = al arrancar:** P13 se confirma en una línea (una sola versión por proceso).
- **Lo que el código o un ADR ya fija** (p. ej. TEC-11 «Records + JSON Schema versionado») se confirma
  en una línea y libera su cupo (`investigacion/04` regla 7).
- **Cupos libres:** primero los contraejemplos («¿qué pasa con `PEDIDOS_VENCIMIENTO_PLAZO=0`?»), y al
  final la metapregunta «¿hay algo que debí preguntarte sobre este parámetro?»
  (`investigacion/04` regla 18).

## 7 · Cobertura

Pesos en el orden de §1 (⟦1⟧ pesa 12, ⟦12⟧ pesa 1). Cada casilla cuenta cuando tiene respuesta o
queda `[PENDIENTE]` con dueño.

- [ ] ⟦1⟧ Decisión que controla y clave con prefijo del módulo
- [ ] ⟦2⟧ Clase: fija, configurable o pendiente, con su justificación
- [ ] ⟦3⟧ Tipo: técnica, negocio versionada o bandera
- [ ] ⟦4⟧ Tipo de dato y unidad
- [ ] ⟦5⟧ Defecto seguro u obligatoriedad
- [ ] ⟦6⟧ Rango válido, con un caso que lo cumple y otro que lo viola
- [ ] ⟦7⟧ Secreto o no; si lo es, gestor y rotación
- [ ] ⟦8⟧ Fuente, precedencia y variable de entorno equivalente
- [ ] ⟦9⟧ Momento de lectura e instantánea por operación
- [ ] ⟦10⟧ Entrada al núcleo (VO o puerto)
- [ ] ⟦11⟧ Versión, firma, esquema y compatibilidad (N/A si es técnica)
- [ ] ⟦12⟧ Dueño, documentación y, si es bandera, fecha de retiro

Cada rango sale con su par de casos (`investigacion/04` §4.1 regla 2):

```gherkin
# V-1: pedidos.vencimiento.plazo = 48h (1h..720h, defecto 48h) [yml < env · equipo pedidos · arranque] [P5-P7]
Rule: El plazo de vencimiento se valida al arrancar
  Example: Plazo dentro de rango
    Given la variable PEDIDOS_VENCIMIENTO_PLAZO vale "24h"
    When arranca el servicio de pedidos
    Then el servicio queda listo y cancela pedidos sin confirmar tras 24 horas
  Example: Plazo fuera de rango
    Given la variable PEDIDOS_VENCIMIENTO_PLAZO vale "0s"
    When arranca el servicio de pedidos
    Then el arranque falla con un error que nombra "pedidos.vencimiento.plazo"
```

## 8 · Antipatrones

| Antipatrón | Señal detectable | Fuente |
|---|---|---|
| Secreto en el repositorio | `password=` con valor, `*.pem` privados en `src/main/resources`, tokens en pruebas | 12-factor; OWASP; §9.1 |
| `@Value` disperso | `@Value("${...}")` en servicios y casos de uso; la misma clave leída en tres sitios | Spring Boot ref. (comparativa) |
| Número mágico sin unidad | `int timeout = 30` (¿segundos?); `Duration.ofMinutes(60)` fijo en un adaptador que debería ser configurable | Spring Boot (conversión); §9.2 |
| Validación perezosa | La primera petición lanza `IllegalArgumentException` por un valor de configuración | *Release It!* «Fail Fast» |
| Defecto peligroso | Defecto que solo sirve en desarrollo (`allowed-origins=*`, `localhost`) y llega a producción | SRE Workbook (defectos seguros) |
| Explosión de perfiles | `application-prod-finca2-v3.yml`; valores copiados entre perfiles | 12-factor |
| Dominio que lee el entorno | `Environment`, `System.getenv` o `@Value` dentro de `..domain..` o `..application..` | Seemann; Cockburn |
| Configuración editada en sitio | Una tabla `parametros` con `UPDATE` sin versión; no se sabe qué tarifa aplicó una factura | SRE Workbook (versionado) |
| Paquete sin firma o sin anti-retroceso | Se acepta cualquier JSON del directorio; una versión vieja sustituye a la nueva | TUF spec |
| Bandera eterna | Banderas de lanzamiento con meses de vida; nadie sabe si se pueden borrar | Hodgson (*carrying cost*) |
| Cadena mágica de bandera | `client.getBooleanValue("v2_enabled", false)` repetido en varios controladores | Hodgson (*decision points*) |
| Bandera que lanza excepción | El proveedor caído rompe la petición | OpenFeature spec 1.4.10 |
| Configuración que nadie posee | Claves sin dueño ni descripción en la metadata | SRE Workbook (*clear owner*) |

## 9 · Código de referencia real

Todos los archivos se leyeron el 15-oct-2026 en el commit indicado, descargados de
`raw.githubusercontent.com`. Los fragmentos son literales (indentación normalizada a espacios) y
`// …` marca lo omitido.

### 9.1 Record `@ConfigurationProperties` con `@Validated`, `@DefaultValue` y records anidados (Spring Boot 4.1.1)

- **Muestra:** el objeto de configuración tipado (P5-P8) en un monolito modular real con Spring Boot
  4.1.1 y Spring Modulith; cómo se consume en una `@Configuration` del borde (P14). También muestra el
  antipatrón CF-01.
- **Repositorio:** `sivaprasadreddy/spring-modular-monolith` · licencia Apache-2.0.
- **Archivos:**
  [`ApplicationProperties.java`](https://github.com/sivaprasadreddy/spring-modular-monolith/blob/09fcf920b09712cbb420dd56f722c0a310c4df2c/src/main/java/com/sivalabs/bookstore/ApplicationProperties.java#L10-L44),
  [`WebMvcConfig.java`](https://github.com/sivaprasadreddy/spring-modular-monolith/blob/09fcf920b09712cbb420dd56f722c0a310c4df2c/src/main/java/com/sivalabs/bookstore/config/WebMvcConfig.java#L9-L30)
  y [`application.properties`](https://github.com/sivaprasadreddy/spring-modular-monolith/blob/09fcf920b09712cbb420dd56f722c0a310c4df2c/src/main/resources/application.properties#L8-L40)

```java
@ConfigurationProperties(prefix = "app")
@Validated
public record ApplicationProperties(
        String supportEmail,
        String newsletterJobCron,
        @DefaultValue("10") int postsPerPage,
        @Valid JwtProperties jwt,
        @Valid CorsProperties cors,
        @Valid OpenAPIProperties openApi) {
    public record JwtProperties(
            @DefaultValue("SivaLabs") String issuer,
            @DefaultValue("604800") Long expiresInSeconds,
            RSAPublicKey publicKey,
            RSAPrivateKey privateKey) {}

    public record CorsProperties(
            @DefaultValue("/api/**") String pathPattern,
            @DefaultValue("*") String allowedOrigins,
            // …
// … WebMvcConfig.java
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        var corsProperties = properties.cors();
        registry.addMapping(corsProperties.pathPattern())
                .allowedOriginPatterns(corsProperties.allowedOrigins())
                // …
// … application.properties
app.jwt.private-key=classpath:certs/private.pem
// …
spring.datasource.password=postgres
```

- **Qué copiar:** un record por módulo con prefijo, records anidados con `@Valid` para que la
  validación baje en cascada, `@DefaultValue` en el componente y la activación con
  `@ConfigurationPropertiesScan` en la clase de arranque. El record se consume solo en la
  `@Configuration` (borde).
- **Qué no copiar:** `@Validated` sin una sola restricción (no valida nada); `expiresInSeconds` como
  `Long` en lugar de `Duration` (CF-05); `allowedOrigins="*"` como defecto (defecto peligroso);
  `private.pem` y la contraseña de la BD dentro del repo (`src/main/resources/certs/private.pem`
  existe en ese commit). Es aceptable en una demo local, pero va contra CF-01 en un servicio real.

### 9.2 Record mínimo con restricciones y su uso en un adaptador

- **Muestra:** claves obligatorias sin defecto con `@NotEmpty` (P6-2), que hacen fallar el arranque si
  faltan, y el consumo del record en un adaptador de S3 y en la fábrica de un cliente HTTP.
- **Repositorio:** `testcontainers/java-local-development-workshop` · licencia MIT.
- **Archivos:**
  [`ApplicationProperties.java`](https://github.com/testcontainers/java-local-development-workshop/blob/e69d050a9eb5be79bb3fbce0f4ba29eb82fa8bb4/src/main/java/com/testcontainers/catalog/ApplicationProperties.java#L7-L12),
  [`RestClientConfig.java`](https://github.com/testcontainers/java-local-development-workshop/blob/e69d050a9eb5be79bb3fbce0f4ba29eb82fa8bb4/src/main/java/com/testcontainers/catalog/clients/inventory/RestClientConfig.java#L10-L20)
  y [`S3FileStorageService.java`](https://github.com/testcontainers/java-local-development-workshop/blob/e69d050a9eb5be79bb3fbce0f4ba29eb82fa8bb4/src/main/java/com/testcontainers/catalog/domain/internal/S3FileStorageService.java#L12-L42)

```java
@ConfigurationProperties(prefix = "application")
@Validated
public record ApplicationProperties(
        @NotEmpty String productImagesBucketName,
        @NotEmpty String productImageUpdatesTopic,
        @NotEmpty String inventoryServiceUrl) {}
// … RestClientConfig.java
@Configuration
class RestClientConfig {

    @Bean
    InventoryServiceClient inventoryServiceProxy(ApplicationProperties properties) {
        RestClient restClient = RestClient.create(properties.inventoryServiceUrl());
        // …
// … S3FileStorageService.java
    public String getPreSignedURL(String filename) {
        return s3Template
                .createSignedGetURL(properties.productImagesBucketName(), filename, Duration.ofMinutes(60))
                .toString();
    }
```

- **Qué copiar:** restricciones en el componente del record; el valor se usa al construir el cliente en
  la raíz de composición (`@Bean`), no en cada llamada.
- **Qué no copiar:** `String inventoryServiceUrl` en lugar de `URI` (P5); `Duration.ofMinutes(60)`
  fijo en el adaptador, que es un candidato a «configurable» (P2); el adaptador S3 vive en
  `domain.internal` e inyecta el record de Spring (CF-09).

### 9.3 Conversión de `Duration`, `@DurationUnit` y anidado no nulo con `@DefaultValue` vacío

- **Muestra:** la documentación ejecutable de Spring Boot: unidad por defecto con `@DurationUnit`
  (P5), defecto con unidad (`"30s"`) y `@DefaultValue` sin valor para que el objeto anidado nunca sea
  `null` (P6).
- **Repositorio:** `spring-projects/spring-boot` (módulo `documentation/spring-boot-docs`) · licencia
  Apache-2.0.
- **Archivos:**
  [`durations/constructorbinding/MyProperties.java`](https://github.com/spring-projects/spring-boot/blob/e9644b19f559db8ad4825526c245ab5a5cb40909/documentation/spring-boot-docs/src/main/java/org/springframework/boot/docs/features/externalconfig/typesafeconfigurationproperties/conversion/durations/constructorbinding/MyProperties.java#L14-L27),
  [`defaultvalues/nonnull/MyProperties.java`](https://github.com/spring-projects/spring-boot/blob/e9644b19f559db8ad4825526c245ab5a5cb40909/documentation/spring-boot-docs/src/main/java/org/springframework/boot/docs/features/externalconfig/typesafeconfigurationproperties/constructorbinding/defaultvalues/nonnull/MyProperties.java#L22-L28)
  y [`validation/nested/MyProperties.java`](https://github.com/spring-projects/spring-boot/blob/e9644b19f559db8ad4825526c245ab5a5cb40909/documentation/spring-boot-docs/src/main/java/org/springframework/boot/docs/features/externalconfig/typesafeconfigurationproperties/validation/nested/MyProperties.java#L16-L24)

```java
@ConfigurationProperties("my")
public class MyProperties {
    // …
    private final Duration sessionTimeout;

    private final Duration readTimeout;
    // …
    public MyProperties(@DurationUnit(ChronoUnit.SECONDS) @DefaultValue("30s") Duration sessionTimeout,
            @DefaultValue("1000ms") Duration readTimeout) {
        this.sessionTimeout = sessionTimeout;
        this.readTimeout = readTimeout;
    }
// … defaultvalues/nonnull/MyProperties.java
    public MyProperties(boolean enabled, InetAddress remoteAddress, @DefaultValue Security security) {
// … validation/nested/MyProperties.java
@ConfigurationProperties("my.service")
@Validated
public class MyProperties {

    @NotNull
    private InetAddress remoteAddress;

    @Valid
    private final Security security = new Security();
```

- **Qué copiar:** las mismas anotaciones valen en los componentes de un record (la guía: «Constructor
  binding can be used with records»); `@Valid` para validar en cascada; `Duration` con unidad
  explícita.
- **Qué no copiar:** las clases con constructor y getters escritos a mano. En Java 21 se escribe un
  `record`. Los marcadores `// @fold:on` son de la herramienta de documentación.

### 9.4 Validación en el constructor compacto del record

- **Muestra:** `@Validated` con mensajes propios y una regla de rango en el constructor compacto
  (P7-2).
- **Repositorio:** `danvega/dvaas`. **No declara licencia** (la API de GitHub devuelve
  `license: null`). Se cita solo como referencia breve; no copies el código literal.
- **Archivo:** [`BlogProperties.java`](https://github.com/danvega/dvaas/blob/27d1dc2cf475aaa7518e8dd672460e096a1526b6/src/main/java/dev/danvega/dvaas/config/BlogProperties.java#L14-L47)

```java
@ConfigurationProperties(prefix = "dvaas.blog")
@Validated
public record BlogProperties(
        // …
        @NotBlank(message = "Blog RSS URL must not be blank")
        @Pattern(regexp = "^https?://.*", message = "Blog RSS URL must start with http:// or https://")
        String rssUrl,
        // …
        @NotNull(message = "Blog cache duration must not be null")
        Duration cacheDuration

) {
    // …
    public BlogProperties {
        if (cacheDuration == null) {
            cacheDuration = Duration.ofMinutes(30);
        }

        // Custom validation: cache duration must be at least 1 minute
        if (cacheDuration.toMinutes() < 1) {
            throw new IllegalArgumentException("Blog cache duration must be at least 1 minute, got: " + cacheDuration);
        }
    }
```

- **Qué copiar:** la regla de rango junto al dato y los mensajes de error que nombran la propiedad.
- **Qué no copiar:** el defecto en el constructor compacto choca con `@NotNull` (el binder entrega
  `null` y el constructor lo sustituye) y no aparece en la metadata; usa `@DefaultValue("30m")` y
  `@DurationMin(minutes = 1)` de Hibernate Validator. Tampoco los comentarios Javadoc en los
  componentes: los records se documentan con `@param` en la clase (Spring Boot, *Configuration
  Metadata*).

### 9.5 Bandera con OpenFeature: API neutral, valor por omisión obligatorio y proveedor en memoria

- **Muestra:** cada evaluación exige un valor por omisión (P19), y la especificación prohíbe que la
  evaluación lance excepciones (req. 1.4.10). `InMemoryProvider` sirve para pruebas y arranques
  locales (P20).
- **Repositorio:** `open-feature/java-sdk` · licencia Apache-2.0.
- **Archivos:**
  [`Features.java`](https://github.com/open-feature/java-sdk/blob/c74d7beaece173a592affd9a02086f778246b8ce/src/main/java/dev/openfeature/sdk/Features.java#L9-L17)
  y [`README.md`](https://github.com/open-feature/java-sdk/blob/c74d7beaece173a592affd9a02086f778246b8ce/README.md?plain=1#L95-L119)

```java
public interface Features {

    Boolean getBooleanValue(String key, Boolean defaultValue);

    Boolean getBooleanValue(String key, Boolean defaultValue, EvaluationContext ctx);

    Boolean getBooleanValue(String key, Boolean defaultValue, EvaluationContext ctx, FlagEvaluationOptions options);

    FlagEvaluationDetails<Boolean> getBooleanDetails(String key, Boolean defaultValue);
    // …
// … README.md
    // flags defined in memory
    Map<String, Flag<?>> myFlags = new HashMap<>();
    myFlags.put("v2_enabled", Flag.builder()
        .variant("on", true)
        .variant("off", false)
        .defaultVariant("on")
        .build());

    // configure a provider
    OpenFeatureAPI api = OpenFeatureAPI.getInstance();
    try {
        api.setProviderAndWait(new InMemoryProvider(myFlags));
    } catch (Exception e) {
        // handle initialization failure
        e.printStackTrace();
    }
    // …
    boolean flagValue = client.getBooleanValue("v2_enabled", false);
```

- **Qué copiar:** el valor por omisión explícito en cada llamada; `getBooleanDetails` cuando hay que
  registrar el motivo (`reason`, `errorCode`); `setProviderAndWait` para no evaluar antes de que el
  proveedor esté listo (req. 1.1.2.2); el SDK es neutral respecto al proveedor (flagd, Unleash y
  otros).
- **Qué no copiar:** la cadena `"v2_enabled"` en el punto de uso (va dentro del objeto de decisión,
  CF-16); `e.printStackTrace()`; el singleton `OpenFeatureAPI.getInstance()` usado desde el núcleo.

### 9.6 Bandera con Unleash: alternativa por omisión, copia local y doble para pruebas

- **Muestra:** `isEnabled` con un valor o una función de reserva (P18-P19), y `FakeUnleash` para
  probar los dos estados (CF-18).
- **Repositorio:** `Unleash/unleash-java-sdk` · licencia Apache-2.0.
- **Archivos:**
  [`Unleash.java`](https://github.com/Unleash/unleash-java-sdk/blob/20ff5c098a82e7f88482b05e9a88635706b45c91/src/main/java/io/getunleash/Unleash.java#L7-L32),
  [`FakeUnleash.java`](https://github.com/Unleash/unleash-java-sdk/blob/20ff5c098a82e7f88482b05e9a88635706b45c91/src/main/java/io/getunleash/FakeUnleash.java#L187-L199)
  y [`README.md`](https://github.com/Unleash/unleash-java-sdk/blob/20ff5c098a82e7f88482b05e9a88635706b45c91/README.md?plain=1#L376-L402)

```java
public interface Unleash {
    default boolean isEnabled(String toggleName) {
        return isEnabled(toggleName, false);
    }

    default boolean isEnabled(String toggleName, boolean defaultSetting) {
        return isEnabled(toggleName, UnleashContext.builder().build(), defaultSetting);
    }
    // …
    boolean isEnabled(
            String toggleName,
            UnleashContext context,
            BiPredicate<String, UnleashContext> fallbackAction);
// … FakeUnleash.java
    public void enable(String... features) {
        for (String name : features) {
            this.conditionalFeatures.remove(name);
            this.features.put(name, true);
        }
    }

    public void disable(String... features) {
        // …
```

- **Caso de uso real (README):** el cliente consulta el servidor cada 10 s y guarda el resultado en
  `unleash-repo.json`; si el servidor no responde, decide con esa copia. El valor por omisión de
  `isEnabled` solo se usa si no hay copia o si la bandera no existe. Con `ToggleBootstrapProvider` y la
  variable `UNLEASH_BOOTSTRAP_FILE` se arranca desde un JSON sin red. Es la «última versión buena
  conocida» de P18-2.
- **Qué copiar:** reserva explícita, copia local y el doble de pruebas del propio proveedor.
- **Qué no copiar:** `isEnabled(toggleName)` sin reserva (oculta el `false` por omisión); el token
  `apiKey` escrito en el builder: va a un secreto (P9).

### 9.7 Metadatos firmados con versión monótona y caducidad (anti-retroceso)

- **Muestra:** el cliente TUF de Sigstore en Java. Verifica la firma con un umbral de claves, rechaza
  una versión menor que la vigente y rechaza metadatos caducados. Es el modelo para cargar un paquete
  de negocio firmado (P16, P18).
- **Repositorio:** `sigstore/sigstore-java` · licencia Apache-2.0.
- **Archivo:** [`Updater.java`](https://github.com/sigstore/sigstore-java/blob/f18d4248fa2c88aad769926e4cbcf6c8c4faae51/sigstore-java/src/main/java/dev/sigstore/tuf/Updater.java#L330-L372)

```java
  void updateTimestamp()
      throws IOException,
          FileNotFoundException,
          SignatureVerificationException,
          JsonParseException {
    // 1) download the timestamp.json bytes.
    var timestamp =
        metaFetcher
            .getMeta(RootRole.TIMESTAMP, Timestamp.class)
            .orElseThrow(() -> new FileNotFoundException("timestamp.json", metaFetcher.getSource()))
            .getMetaResource();

    // 2) verify against threshold of keys as specified in trusted root.json
    verifyDelegate(trustedMetaStore.getRoot(), timestamp);

    // 3) If the new timestamp file has a lesser version than our current trusted timestamp file
    // report a rollback attack.  If it is equal, just return the original timestamp there should
    // be no changes. If it is higher than continue update.
    Optional<Timestamp> localTimestampMaybe = trustedMetaStore.findTimestamp();
    if (localTimestampMaybe.isPresent()) {
      Timestamp localTimestamp = localTimestampMaybe.get();
      if (localTimestamp.getSignedMeta().getVersion() > timestamp.getSignedMeta().getVersion()) {
        throw new RollbackVersionException(
            localTimestamp.getSignedMeta().getVersion(), timestamp.getSignedMeta().getVersion());
      }
      // …
      if (localTimestamp.getSignedMeta().getVersion() == timestamp.getSignedMeta().getVersion()) {
        return;
      }
    }
    // 4) check expiration timestamp is after tuf update start time, else fail.
    throwIfExpired(timestamp.getSignedMeta().getExpiresAsDate());
    // 5) persist timestamp.json
    trustedMetaStore.setTimestamp(timestamp);
  }
```

- **Qué copiar:** el orden de las comprobaciones (firma → versión → caducidad → persistir); la misma
  versión no hace nada (idempotente); cada rechazo es una excepción con nombre.
- **Qué no copiar:** la jerarquía completa de roles de TUF (root, snapshot, targets, timestamp), que
  sobra para un paquete de reglas con un único firmante [opinión].

### 9.8 Validar un documento contra JSON Schema 2020-12 en Java

- **Muestra:** cargar un esquema desde el classpath, inicializarlo una vez (cachearlo) y obtener la
  lista de errores. Es el paso 4 del flujo de negocio de §2 (P15).
- **Repositorio:** `networknt/json-schema-validator` · licencia Apache-2.0. El fragmento está en la
  guía del propio repositorio, no en `src/`.
- **Archivo:** [`doc/quickstart.md`](https://github.com/networknt/json-schema-validator/blob/47d2fe638f39227b6e6cbee2b5c3461da8f161d8/doc/quickstart.md?plain=1#L81-L100)

```java
    @Test
    void schemaFromClasspath() {
        SchemaRegistry schemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        /*
         * This should be cached for performance.
         * 
         * Loading from using the retrieval IRI is not recommended as it may cause
         * confusing when resolving relative $ref when $id is also used.
         */
        Schema schemaFromClasspath = schemaRegistry.getSchema(SchemaLocation.of("classpath:schema/example-ref.json"));
        /*
         * By default all schemas are preloaded eagerly but ref resolve failures are not
         * thrown. You check if there are issues with ref resolving using
         * initializeValidators()
         */
        schemaFromClasspath.initializeValidators();
        List<Error> errors = schemaFromClasspath.validate("{\"id\": \"2\"}", InputFormat.JSON,
                executionContext -> executionContext
                        .executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));
        assertEquals(1, errors.size());
    }
```

- **Qué copiar:** el registro con el dialecto fijo (2020-12), el esquema cacheado e
  `initializeValidators()` al arrancar para fallar rápido si un `$ref` no resuelve.
- **Qué no copiar:** cargar el esquema desde una cadena (la guía lo desaconseja por los `$ref`
  relativos). La línea 2.x usa Jackson 2 y la 3.x usa Jackson 3 y Java 17+: elige según el proyecto.

### 9.9 Casos de uso reales: qué configuran estos proyectos y por qué

| Proyecto | Qué configura | Por qué (según su fuente) |
|---|---|---|
| Facebook, Configerator (Tang et al., SOSP 2015) | Configuración como código compilada desde fuentes de alto nivel, con validadores, revisión y canario por fases | Miles de cambios diarios hechos por miles de personas; los errores de configuración son «a major source of site outages» (resumen de Acolyer) |
| Facebook, Gatekeeper (mismo artículo) | Lanzamiento de funciones por cambio de configuración en línea, con restricciones y muestreo por porcentaje | Desacoplar el lanzamiento del despliegue de código |
| Google SRE (Workbook cap. 14) | Configuración versionada, hermética, con dueño, validación semántica en el *commit* y despliegue gradual | Volver atrás es más rápido que corregir durante un incidente |
| Sigstore (`sigstore-java`) | Metadatos TUF firmados con versión y caducidad para la raíz de confianza | Evitar alteración, retroceso y congelación del repositorio de claves |
| Unleash (`unleash-java-sdk`) | Banderas consultadas cada 10 s con copia local y *bootstrap* desde archivo | Seguir decidiendo si el servidor de banderas cae, y arrancar en contenedores efímeros |
| spring-modular-monolith (§9.1) | Un record por aplicación: JWT, CORS y OpenAPI con defectos | Agrupar la configuración propia bajo `app.*`, con validación y metadatos |

## 10 · Plantillas de código

Son esqueletos en Java 21 y Spring Boot 4 (también valen en Boot 3.x). Cada `TODO Pn` lo resuelve la
pregunta n. Paquete de ejemplo: `com.ejemplo.pedidos`.

**T1 · Record de propiedades técnicas** (basado en 9.1 y 9.3; CF-03 a CF-06 y CF-08)

```java
package com.ejemplo.pedidos.config;

/**
 * Configuración del módulo de pedidos.
 * @param vencimiento reglas para cancelar pedidos sin confirmar
 * @param inventario cliente del servicio de inventario
 */
@ConfigurationProperties("pedidos")                                   // TODO P8: prefijo del módulo
@Validated
public record PedidosProperties(@Valid @DefaultValue Vencimiento vencimiento,
                                @Valid @NotNull Inventario inventario) {

  /** @param plazo tiempo sin confirmar antes de cancelar el pedido */
  public record Vencimiento(
      @DefaultValue("48h")                                             // TODO P6: defecto seguro
      @DurationMin(hours = 1) @DurationMax(days = 30) Duration plazo) {} // TODO P7: rango (Hibernate Validator)

  /** @param url URL base del servicio · @param timeout tiempo máximo por llamada */
  public record Inventario(
      @NotNull URI url,                                                // obligatorio: sin defecto (P6-2)
      @DefaultValue("2s") @DurationMax(seconds = 30) Duration timeout) {}
}
// application.yml (sin secretos):  pedidos.vencimiento.plazo: 48h
// Variable de entorno equivalente: PEDIDOS_VENCIMIENTO_PLAZO=24h  · PEDIDOS_INVENTARIO_URL=https://…
```

**T2 · Secretos fuera del repositorio** (P9; 12-factor, Spring Boot `configtree`, Spring Cloud Vault)

```yaml
# application.yml — solo referencias, nunca valores
spring:
  config:
    import:
      - "optional:configtree:/run/secrets/"     # Docker/Kubernetes: archivo = clave, contenido = valor
      - "vault://"                              # TODO P9: obligatorio (sin optional:) → falla si no hay Vault
  datasource:
    url: ${DB_URL}                              # sin defecto: si falta, el arranque falla (P6-2)
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
management:
  endpoint:
    configprops:
      show-values: when-authorized              # CF-19: no exponer valores a anónimos
```

**T3 · Value object de política en el dominio** (basado en `entidad-agregado` T2; CF-02)

```java
package com.ejemplo.pedidos.domain;

public record PlazoDeVencimiento(Duration valor) {                    // sin Spring ni anotaciones
  public PlazoDeVencimiento {
    Objects.requireNonNull(valor, "valor");
    if (valor.compareTo(Duration.ofHours(1)) < 0) throw new PlazoInvalido(valor); // TODO P7: mismo rango que T1
  }
  public boolean vencio(Instant creado, Instant ahora) { return creado.plus(valor).isBefore(ahora); }
}
```

**T4 · Raíz de composición: el valor entra al construir** (P14-1; Seemann; Cockburn)

```java
package com.ejemplo.pedidos.config;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(PedidosProperties.class)
class PedidosConfig {
  @Bean
  CancelarPedidosVencidos cancelarVencidos(PedidosProperties props, Pedidos pedidos, Clock reloj) {
    var plazo = new PlazoDeVencimiento(props.vencimiento().plazo());  // el núcleo no ve PedidosProperties
    return new CancelarPedidosVencidos(plazo, pedidos, reloj);        // TODO P14: ¿VO o puerto?
  }
}
```

**T5 · Paquete de negocio versionado: VO, puerto y esquema** (P14-2, P15-P17)

```java
package com.ejemplo.envios.domain;

public record VersionDePolitica(int mayor, int menor) implements Comparable<VersionDePolitica> {
  public VersionDePolitica { if (mayor < 1 || menor < 0) throw new IllegalArgumentException("versión"); }
  public int compareTo(VersionDePolitica o) {
    return Comparator.comparingInt(VersionDePolitica::mayor).thenComparingInt(VersionDePolitica::menor).compare(this, o);
  }
}
public record PoliticaDeEnvios(String id, VersionDePolitica version, Instant vigenteDesde,
                               Map<CodigoZona, Dinero> tarifaPorZona) {  // TODO P15: campos del paquete
  public PoliticaDeEnvios {
    tarifaPorZona = Map.copyOf(tarifaPorZona);                       // inmutable (CF-10)
    if (tarifaPorZona.isEmpty()) throw new PoliticaSinTarifas(id, version); // regla semántica (CF-11)
  }
  public Dinero tarifa(CodigoZona zona) {
    var t = tarifaPorZona.get(zona);
    if (t == null) throw new ZonaSinTarifa(zona, version);
    return t;
  }
}
// application.port.out
public interface PoliticasVigentes {                                  // puerto de configuración
  PoliticaDeEnvios vigente();                                         // instantánea inmutable (CF-14)
}
```

```json
{
  "$schema": "https://json-schema.org/draft/2020-12/schema",
  "$id": "https://ejemplo.com/esquemas/politica-envios/2.json",
  "type": "object",
  "required": ["id", "version", "vigenteDesde", "expira", "tarifas"],
  "properties": {
    "id": { "const": "politica-envios" },
    "version": { "type": "string", "pattern": "^[1-9][0-9]*\\.[0-9]+$" },
    "vigenteDesde": { "type": "string", "format": "date-time" },
    "expira": { "type": "string", "format": "date-time" },
    "tarifas": {
      "type": "array", "minItems": 1,
      "items": {
        "type": "object", "required": ["zona", "monto", "moneda"],
        "properties": {
          "zona": { "type": "string", "pattern": "^[A-Z]{2,5}$" },
          "monto": { "type": "string", "pattern": "^[0-9]+(\\.[0-9]{1,2})?$" },
          "moneda": { "type": "string", "pattern": "^[A-Z]{3}$" }
        },
        "additionalProperties": false
      }
    }
  },
  "additionalProperties": true
}
```

`additionalProperties: true` en la raíz deja que un lector viejo acepte campos nuevos (Tolerant
Reader, cambio aditivo de P17-1). Dentro de cada tarifa va `false` para detectar errores de escritura.
[opinión]

**T6 · Adaptador que verifica, valida y carga el paquete** (basado en 9.7 y 9.8; CF-11, CF-12, P18)

```java
package com.ejemplo.envios.adapter.out.politicas;

final class CargadorDePoliticas implements PoliticasVigentes {
  private final AtomicReference<PoliticaDeEnvios> vigente = new AtomicReference<>();
  private final VerificadorDeFirma firma;            // TODO P16: JWS Ed25519 (RFC 8037), p. ej. Nimbus JOSE+JWT
  private final Schema esquema;                      // networknt, cacheado e inicializado (9.8)
  private final MapeadorDePolitica mapeador;         // JSON → record PaqueteLeido(PoliticaDeEnvios politica, Instant expira)
  private final Clock reloj;

  CargadorDePoliticas(VerificadorDeFirma firma, Schema esquema, MapeadorDePolitica mapeador, Clock reloj, byte[] inicial, byte[] firmaInicial) {
    this.firma = firma; this.esquema = esquema; this.mapeador = mapeador; this.reloj = reloj;
    vigente.set(verificarYMapear(inicial, firmaInicial));   // falla al arrancar si no es válido (P18-1)
  }

  @Override public PoliticaDeEnvios vigente() { return vigente.get(); }

  /** Recarga: si falla, conserva la vigente y propaga el motivo para alertar. */
  void publicar(byte[] json, byte[] jws) {
    var nueva = verificarYMapear(json, jws);
    vigente.updateAndGet(actual -> {
      if (nueva.version().compareTo(actual.version()) < 0) throw new RetrocesoDeVersion(actual.version(), nueva.version()); // anti-rollback
      return nueva.version().equals(actual.version()) ? actual : nueva;                  // idempotente, como 9.7
    });
  }

  private PoliticaDeEnvios verificarYMapear(byte[] json, byte[] jws) {
    firma.verificar(json, jws);                                       // 1 firma → FirmaInvalida
    var errores = esquema.validate(new String(json, UTF_8), InputFormat.JSON); // 2 esquema
    if (!errores.isEmpty()) throw new PaqueteInvalido(errores.toString());
    var paquete = mapeador.leer(json);                                // 3 semántica (constructores del VO)
    if (paquete.expira().isBefore(reloj.instant())) throw new PaqueteCaducado(paquete.expira()); // 4 caducidad
    return paquete.politica();
  }
}
```

**T7 · Bandera tras un objeto de decisión** (basado en 9.5 y 9.6; Hodgson; CF-16, CF-17)

```java
// application.port.out — el núcleo no conoce OpenFeature
public interface DecisionesDeDespacho {
  boolean despachoPorLotes(FincaId finca);           // TODO P19: categoría «release», retiro 2027-03-31, dueño: envíos
}
// adapter.out.banderas
final class DecisionesDeDespachoOpenFeature implements DecisionesDeDespacho {
  static final String DESPACHO_POR_LOTES = "envios.despacho-por-lotes";
  private final Client banderas;                     // dev.openfeature.sdk.Client
  DecisionesDeDespachoOpenFeature(Client banderas) { this.banderas = banderas; }

  @Override public boolean despachoPorLotes(FincaId finca) {
    var ctx = new ImmutableContext(finca.valor().toString());        // targeting key
    return banderas.getBooleanValue(DESPACHO_POR_LOTES, false, ctx);  // false = comportamiento anterior (Hodgson)
  }
}
// En el caso de uso, inversión de decisión + Strategy (sin if disperso):
var estrategia = decisiones.despachoPorLotes(finca) ? despachoPorLotes : despachoUnitario;
```

**T8 · Pruebas de arranque, de paquete, de banderas y de arquitectura** (CF-04, CF-12, CF-17, CF-18, CF-02)

```java
class PedidosPropertiesTest {
  private final ApplicationContextRunner contexto = new ApplicationContextRunner()
      .withUserConfiguration(PedidosConfigDePrueba.class);           // @EnableConfigurationProperties(PedidosProperties.class)

  @Test void tomaElDefectoSiFalta() {
    contexto.withPropertyValues("pedidos.inventario.url=https://inventario.local")
        .run(ctx -> assertThat(ctx.getBean(PedidosProperties.class).vencimiento().plazo()).isEqualTo(Duration.ofHours(48)));
  }
  @Test void fallaAlArrancarFueraDeRango() {                        // V-1 · viola
    contexto.withPropertyValues("pedidos.inventario.url=https://inventario.local", "pedidos.vencimiento.plazo=0s")
        .run(ctx -> assertThat(ctx).hasFailed()
            .getFailure().rootCause().hasMessageContaining("plazo"));
  }
  @Test void fallaSiFaltaLaObligatoria() {
    contexto.run(ctx -> assertThat(ctx).hasFailed());
  }
}

class CargadorDePoliticasTest {
  @Test void rechazaFirmaAlterada() { /* TODO P16: bytes alterados → FirmaInvalida; la vigente no cambia */ }
  @Test void rechazaVersionMenor()  { /* v4 vigente, llega v3 → RetrocesoDeVersion */ }
  @Test void rechazaCaducado()      { /* expira < reloj fijo → PaqueteCaducado */ }
}

class BanderasTest {
  static final LocalDate RETIRO_DESPACHO_POR_LOTES = LocalDate.of(2027, 3, 31);   // TODO P19
  @Test void bombaDeTiempo() {                                       // Hodgson: «time bombs»
    assertThat(LocalDate.now()).as("retirar envios.despacho-por-lotes").isBefore(RETIRO_DESPACHO_POR_LOTES);
  }
  @ParameterizedTest @ValueSource(booleans = {true, false})          // ambos estados (CF-18)
  void despachaConYSinLotes(boolean encendida) {
    var api = OpenFeatureAPI.getInstance();
    api.setProviderAndWait(new InMemoryProvider(Map.of(DecisionesDeDespachoOpenFeature.DESPACHO_POR_LOTES,
        Flag.builder().variant("on", true).variant("off", false).defaultVariant(encendida ? "on" : "off").build())));
    // … ejecutar el caso de uso y comprobar la estrategia elegida
  }
}

@AnalyzeClasses(packages = "com.ejemplo")
class ReglasDeConfiguracionTest {
  @ArchTest static final ArchRule nucleoSinSpring = noClasses().that().resideInAnyPackage("..domain..", "..application..")
      .should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "dev.openfeature..", "io.getunleash.."); // CF-02, CF-16
  @ArchTest static final ArchRule sinValueDisperso = noFields().that().areDeclaredInClassesThat().resideOutsideOfPackage("..config..")
      .should().beAnnotatedWith("org.springframework.beans.factory.annotation.Value");                 // CF-03
}
```

## 11 · Qué se genera

Orden dentro del segmento (cada paso compila solo):

1. Value objects del dominio: `PlazoDeVencimiento`, `VersionDePolitica`, `PoliticaDeEnvios` (T3, T5),
   con sus pruebas de construcción inválida.
2. Puertos: `PoliticasVigentes`, `DecisionesDeDespacho` (T5, T7).
3. Record de propiedades `PedidosProperties` con Javadoc `@param` (T1) y `application.yml` sin
   secretos (T2).
4. Raíz de composición `PedidosConfig` (T4).
5. Si hay paquete de negocio: esquema `politica-envios/2.json`, un paquete de ejemplo firmado para
   pruebas y `CargadorDePoliticas` (T5, T6).
6. Si hay bandera: `DecisionesDeDespachoOpenFeature` y su registro (categoría, dueño, retiro) (T7).
7. Pruebas: `PedidosPropertiesTest`, `CargadorDePoliticasTest`, `BanderasTest` y
   `ReglasDeConfiguracionTest` (T8).

Salen hacia otras plantillas: el puerto `PoliticasVigentes` y el VO hacia `caso-de-uso`; la tabla del
historial de versiones publicadas, si se guarda, hacia `adaptador-persistencia`; el evento
`PoliticaPublicada` hacia `evento-mensaje`.
Dependencias: `spring-boot-starter-validation` (Hibernate Validator, que trae `@DurationMin` y
`@DurationMax`), `spring-boot-configuration-processor` (metadatos), un validador de JSON Schema
(networknt u otro, según el ADR), una biblioteca JOSE para JWS Ed25519 y `dev.openfeature:sdk` o
`io.getunleash:unleash-client-java` si hay banderas.

## 12 · Referencias

- The Twelve-Factor App, *III. Config*. Config es lo que varía entre despliegues; propone la prueba del
  «repo publicable» y desaconseja los entornos con nombre. https://12factor.net/config
- Spring Boot 4.1 Reference, *Externalized Configuration*. Orden de `PropertySource`, enlace por
  constructor con records, `@DefaultValue`, `@Validated` y `@Valid`, `Duration` y `DataSize`, relaxed
  binding y variables de entorno, `configtree:`, `optional:` y `/actuator/configprops`.
  https://docs.spring.io/spring-boot/reference/features/external-config.html
- Spring Boot 4.1, *Configuration Metadata · Annotation Processor*. Javadoc `@param` en records,
  detección de defectos y `@ConfigurationPropertiesSource`.
  https://docs.spring.io/spring-boot/specification/configuration-metadata/annotation-processor.html
- Spring Cloud Vault, *Config Data API*. `spring.config.import=vault://` y `optional:vault://`.
  https://docs.spring.io/spring-cloud-vault/reference/config-data.html
- Pete Hodgson, *Feature Toggles (aka Feature Flags)*, martinfowler.com (2017). Categorías,
  enrutador y objeto de decisiones, inversión de decisión, configuración estática o dinámica, pruebas,
  costo, fechas de caducidad y *time bombs*. https://martinfowler.com/articles/feature-toggles.html
- OpenFeature Specification, *Flag Evaluation API*. Req. 1.4.10 (no lanzar; devolver el defecto),
  1.4.7-1.4.9 (motivo y código de error) y 1.1.2.2 (inicializar el proveedor).
  https://openfeature.dev/specification/sections/flag-evaluation
- Unleash Java SDK, README. Sondeo, copia local `unleash-repo.json`, *bootstrap* y `FakeUnleash`.
  https://github.com/Unleash/unleash-java-sdk
- M. Nygard, *Release It!* 2.ª ed. (2018). «Fail Fast» (p. 106), «Configuration» (p. 160),
  «Configuration Services» (p. 206), «Configured Passwords» (p. 232); índice verificado en
  https://www.gbv.de/dms/ilmenau/toc/898405874.PDF. Las ideas de la 1.ª ed. (las propiedades son parte
  de la interfaz de usuario; separar la configuración interna de la de producción; arrancar completo
  antes de aceptar trabajo) salen de notas de lectura, no del libro:
  https://github.com/csabapalfi/release-it
- Google, *The Site Reliability Workbook*, cap. 14 *Configuration Design and Best Practices*.
  Configuración como interfaz, defectos seguros, validación semántica, dueño, versionado, hermeticidad
  y despliegue gradual. https://sre.google/workbook/configuration-design/
- C. Tang et al., *Holistic Configuration Management at Facebook*, SOSP 2015. Configerator
  (configuración como código, validadores, revisión y canario) y Gatekeeper (banderas). Resumen
  verificado en https://blog.acolyer.org/2015/10/16/holistic-configuration-management-at-facebook/
- OWASP, *Secrets Management Cheat Sheet*. Centralizar, rotar, mínimo privilegio, detectar en
  *pre-commit*, riesgos de las variables de entorno y auditoría.
  https://cheatsheetseries.owasp.org/cheatsheets/Secrets_Management_Cheat_Sheet.html
- A. Cockburn, *Hexagonal Architecture*. *Configurable dependency* y el configurador como pieza
  obligatoria. https://alistair.cockburn.us/hexagonal-architecture/
- M. Seemann, *Primitive Dependencies* (2012). Los valores de configuración son dependencias y se
  leen en la raíz de composición. https://blog.ploeh.dk/2012/07/02/PrimitiveDependencies/
- C. Richardson, *Pattern: Externalized configuration*.
  https://microservices.io/patterns/externalized-configuration.html
- M. Fowler, *ParallelChange* (expandir, migrar, contraer) para renombrar claves y campos.
  https://martinfowler.com/bliki/ParallelChange.html
- The Update Framework, *Specification*. Metadatos firmados con versión, caducidad, umbral de firmas
  y protección contra retroceso y congelación. https://theupdateframework.github.io/specification/latest/
- JSON Schema 2020-12, *Core* y *Validation*. https://json-schema.org/draft/2020-12
- IETF RFC 7515 (JWS) y RFC 8037 (Ed25519 en JOSE). https://www.rfc-editor.org/rfc/rfc7515 ·
  https://www.rfc-editor.org/rfc/rfc8037
- Confluent, *Schema Evolution and Compatibility*. Compatibilidad BACKWARD y FORWARD, y campos
  nuevos con defecto.
  https://docs.confluent.io/platform/current/schema-registry/fundamentals/schema-evolution.html
- M. Fowler, *TolerantReader*. https://martinfowler.com/bliki/TolerantReader.html
- Repositorios de §9: `sivaprasadreddy/spring-modular-monolith` (Apache-2.0),
  `testcontainers/java-local-development-workshop` (MIT), `spring-projects/spring-boot` (Apache-2.0),
  `danvega/dvaas` (sin licencia), `open-feature/java-sdk` (Apache-2.0), `Unleash/unleash-java-sdk`
  (Apache-2.0), `sigstore/sigstore-java` (Apache-2.0) y `networknt/json-schema-validator`
  (Apache-2.0).
