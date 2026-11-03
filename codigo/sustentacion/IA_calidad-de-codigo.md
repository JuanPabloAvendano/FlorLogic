REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# Calidad de código: Clean Code, Clean Architecture, SOLID, KISS, YAGNI, DRY

Cada principio con **un ejemplo real** del código. Rutas relativas a
`codigo/backend-finca/src/main/java/co/florlogic/finca/ingesta/` salvo que digan otra cosa.

> **Ojo:** la fase F1 (sesión, huella, rechazadas) está cambiando `ingesta` mientras se escribe esto. Los nombres de
> abajo se comprobaron el 31-oct-2026 por la tarde; una pasada posterior debe volver a mirarlos.

## 1 · Clean Architecture

**La regla:** «Source code dependencies always point inward» (*Clean Architecture* · p. 199).

**Cómo se ve en FlorLogic.** Un hexágono por módulo, con tres anillos:

| Anillo | Paquete | Qué contiene | Ejemplo |
|---|---|---|---|
| Afuera | `infraestructura/input/controladores` | HTTP | `ParticionesControlador`, `ManejadorDeErrores` |
| Afuera | `infraestructura/output/repositorios/estructurados` | SQL | `AnotacionRepositorioAdapter`, `AvanceRepositorioAdapter` |
| Medio | `servicios/input/{ports,adapter,ensamblador,dto}` | Puertos de entrada | `RecibirParticionInputPort`, `RecibirParticionInputAdapter` |
| Medio | `servicios/casosuso` + `impl` | Casos de uso | `RecibirParticionCasoUsoImpl`, `AbrirSesionCasoUsoImpl` |
| Medio | `servicios/output/ports` | Puertos de salida | `AnotacionRepositorio`, `CalculadorDeHuella`, `Reloj` |
| Centro | `dominio` | Java puro | `AvanceDelDispositivoAgregado`, `ConsecutivoValor`, `SesionDeSincronizacion` |

**Evidencia automática:** `codigo/backend-finca/src/test/java/co/florlogic/finca/CapasTest.java`, 7 reglas ArchUnit:

1. `el_dominio_solo_depende_de_java_y_de_otro_dominio` — sin Spring, Jackson, JDBC ni el contrato.
2. `los_servicios_no_dependen_de_la_infraestructura` — las flechas apuntan hacia adentro.
3. `el_controlador_solo_conoce_el_puerto_de_entrada_y_sus_dto`.
4. `el_manejador_de_errores_ademas_solo_ve_las_excepciones_de_los_puertos_de_salida`.
5. `solo_el_ensamblador_del_controlador_conoce_el_contrato_y_jackson`.
6. `fuera_del_ensamblador_del_controlador_nadie_conoce_el_contrato_ni_jackson`.
7. `los_adaptadores_de_servicios_no_construyen_dominio`.

Y `ModulosTest` (Spring Modulith): un módulo solo usa la API pública de otro, y hay 17 módulos.

## 2 · SOLID, un ejemplo por principio

### S · Responsabilidad única

«A module should be responsible to one, and only one, actor» (*Clean Architecture* · p. 76).

- `servicios/input/adapter/RecibirParticionInputAdapter` **solo orquesta**: pide el dominio al ensamblador, llama al
  caso de uso y pide la respuesta.
- `servicios/input/ensamblador/RecibirParticionEnsambladorEntrada` **solo traduce** DTO ↔ dominio.

Si cambia la forma del DTO, cambia el ensamblador; si cambia el flujo, cambia el adaptador. Decisión A-84: «Adaptador
y ensamblador, clases y paquetes distintos … `…InputAdapter` implementa el puerto de entrada y **solo orquesta** …
`…InputAssembler` **solo traduce**». La regla 7 de `CapasTest` falla si el adaptador construye un valor del dominio.

### O · Abierto/cerrado

El caso de uso depende de `servicios/output/ports/repositorios/AnotacionRepositorio`. Para guardar en otro lugar se
**añade** un adaptador nuevo sin tocar el caso de uso. Ya pasa: en las pruebas el mismo caso de uso corre con
`AnotacionRepositorioDobleEnMemoria` (en `src/test/…/servicios/output/ports/repositorios/`) y en producción con
`AnotacionRepositorioAdapter`. Otro ejemplo: `ManejadorDeErrores` **extiende** `ResponseEntityExceptionHandler` de
Spring y añade solo sus dos casos (`ChoqueConcurrente` → `409`, `Exception` → `500`) sin modificar la clase base.

### L · Sustitución de Liskov

Cualquier implementación del puerto debe poder ponerse en lugar de otra. Se **prueba**:
`AnotacionRepositorioContratoTest` es abstracta y fija el comportamiento del puerto; la heredan
`AnotacionRepositorioDobleEnMemoriaTest` (doble en memoria) y `AnotacionRepositorioAdapterTest` (PostgreSQL real).
Lo mismo con `AvanceRepositorioContratoTest` ← `AvanceRepositorioDobleEnMemoriaTest` y `AvanceRepositorioAdapterTest`.
Si el adaptador real se comportara distinto que el doble, la misma prueba fallaría en uno de los dos.

### I · Segregación de interfaces

Puertos pequeños, uno por necesidad del caso de uso, en vez de un «servicio de infraestructura» grande:
`Reloj` (`Instant ahora()`), `CalculadorDeHuella` (`String de(String contenidoJson)`), `GeneradorDeIds`,
`DispositivoDelNodo` (`UUID dispositivoId()`), `EvaluadorDeAnotaciones`. El caso de uso que solo necesita la hora no
depende de nada más.

### D · Inversión de dependencias

El caso de uso (política, adentro) define la interfaz `AnotacionRepositorio` en `servicios/output/ports`; el detalle
(`infraestructura/output/repositorios/estructurados/AnotacionRepositorioAdapter`, con `JdbcClient`) la implementa. La
dependencia de código va de infraestructura hacia servicios, al revés del flujo de control. La regla 2 de
`CapasTest` lo hace cumplir.

## 3 · Clean Code

| Práctica | Ejemplo |
|---|---|
| Nombres que dicen lo que es, en el idioma del dominio (PR-01) | `AvanceDelDispositivoAgregado`, `SesionDeSincronizacion`, `ChoqueConcurrente`, `CalculadorDeHuella` |
| Pruebas que se leen como especificación | `CanalParticionesTest.una_anotacion_que_el_dominio_rechaza_es_422_y_no_guarda_ni_mueve_el_avance` |
| Valores con significado en vez de primitivos | `dominio/comun/ConsecutivoValor` (`record`; `de(long)` devuelve `Optional` vacío si es menor que 1), `HuellaValor`, `AnotacionId` |
| Funciones cortas con un nivel de abstracción | `RecibirParticionInputAdapter.ejecutar` (una cadena de `map`) |
| Sin `null` como señal | `Optional` en ensambladores y valores (decisión de código defensivo: «ningún punto de entrada lanza») |
| Comentarios que dicen **por qué**, no qué | `despliegue/gateway/default.conf.template`: cada directiva con su motivo y la decisión que la pide |

**Falta:** SonarQube (F6) para medir duplicación, complejidad y olores de forma objetiva; JaCoCo para la cobertura.

## 4 · KISS

- **Cola en PostgreSQL** en vez de broker (ADR-009); **monolito modular** en vez de microservicios (ADR-017).
- **Spring Web MVC con hilos virtuales** en vez de programación reactiva (TEC-04): código bloqueante legible.
- **`JdbcClient` con SQL a mano** en vez de JPA/Hibernate (TEC-07): se ve exactamente qué se ejecuta.
- **Dockerfile multietapa escrito por el equipo** en vez de Jib (TEC-28 ajustado): el equipo lo entiende y lo
  reconstruye.
- La PWA sin framework (CT-4).

## 5 · YAGNI

- ADR-017, «Lo que deliberadamente no se construye»: cada cosa con su condición de reapertura.
- Ejemplo de código: 13 de los 17 módulos solo tienen `package-info.java` y la forma de paquetes (A-97): **existen
  como límite**, pero no tienen clases hasta que una regla las pida («el dominio tira de la infraestructura»).
- ADR-034 (caché) y ADR-011 (histórico por capas, depreciada) esperan a una medición.

## 6 · DRY

- **Un solo contrato** para el simulador y el nodo: `codigo/contrato/` (records y `LectorDto`) lo usan
  `simulador/…/NodoDeFincaAdaptadorHttp` y el ensamblador del controlador; los JSON dorados de
  `contrato/src/test/resources/dorados/` son la única fuente de la forma.
- **Una sola definición de la imagen del WAF:** `CanalPorGatewayTest` **lee** la línea `image:` de `compose.yaml` en
  vez de repetirla.
- **Una sola versión de cada dependencia** en el `pom.xml` padre con comentario `TEC-nn`.
- **Un solo paquete de configuración** por empresa (ADR-029) y el mismo motor de reglas en cliente y servidor (ADR-006).

## 7 · Brechas de calidad a cerrar

| Brecha | Fase |
|---|---|
| Cobertura medida (JaCoCo) con umbral | F6 |
| Análisis estático (SonarQube) con quality gate | F6 |
| `AnotacionRepositorio` crece a 3 métodos en F1: vigilar que siga siendo una sola responsabilidad | F1 |
| Los `type` de los problemas siguen como `static final URI` en el controlador (A-104 los lleva al catálogo) | F2 |
