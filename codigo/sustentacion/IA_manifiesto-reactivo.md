REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# Manifiesto Reactivo aplicado a TX-1

Fuente: «we want systems that are Responsive, Resilient, Elastic and Message Driven. We call these Reactive Systems»
(The Reactive Manifesto 2.0 y su glosario · secc. 1, https://www.reactivemanifesto.org/). La relación entre las cuatro:
«Value: Responsive … Form: Elastic and resilient … Means: Message-driven» (*Principles and Patterns for Distributed
Application Architecture* · p. 20).

**Aclaración para la defensa.** «Reactivo» como arquitectura **no** obliga a programar con WebFlux o Reactor.
FlorLogic usa Spring Web MVC con **hilos virtuales** (TEC-04): código bloqueante sencillo que escala en E/S. Las
propiedades del manifiesto se consiguen por diseño del sistema, no por el estilo de programación.

## 1 · Message Driven (el medio)

| Qué pide | En FlorLogic | Evidencia | Estado |
|---|---|---|---|
| Comunicación asíncrona por mensajes, con límites claros | **La partición es un mensaje**: el celular la guarda en su almacén de salida y la envía cuando hay red (ADR-002: «entra a un almacén de salida persistente y no se borra hasta que el servidor confirma»). El nodo responde con un acuse | Contrato en `codigo/contrato/` (`ParticionDto`, `ResultadoParticionDto`); `DtoJsonTest` | HAY (en el canal) |
| Entrega al menos una vez sin efectos dobles | Idempotencia por identificador del teléfono (ADR-027) | `CanalParticionesTest.reenviar_la_misma_particion_no_duplica` | HAY |
| Contrapresión (back-pressure) | El nodo responde `409` con `Retry-After` cuando dos particiones del mismo celular chocan; `413` hace que el celular parta la partición | `CanalParticionesTest.dos_particiones_…_409_…`, `CanalPorGatewayTest.mas_de_10000_campos_es_413_rechazado` | HAY |
| Mensajes internos entre módulos | Eventos de Spring Modulith con su tabla `event_publication`; cola de trabajos en PostgreSQL con db-scheduler (ADR-009) | Pendiente (F5) | PENDIENTE-F5 |

## 2 · Resilient (la forma)

| Qué pide | En FlorLogic | Evidencia | Estado |
|---|---|---|---|
| Fallos contenidos y aislados | Cada contenedor falla solo: sin backend, el gateway responde `502` en `problem+json`; sin vault, el backend no arranca y Docker lo reintenta | `CanalPorGatewayTest.con_el_backend_apagado_es_502_no_disponible`, `NodoArranqueSeguroTest.c5_sin_vault_el_backend_reiniciado_no_queda_escuchando` | HAY |
| Recuperación sin perder datos | Transacción por partición; el celular conserva hasta el acuse | `CanalParticionesTest.si_la_base_falla_a_mitad_es_500_generico_y_no_queda_nada_a_medias` | HAY |
| Replicación / delegación | La nube no está en el camino crítico (README §5); el respaldo cifrado sale a la nube (TX-2) | Diseño | PARCIAL |
| Corte de Wi-Fi o de luz a mitad (ES-S02, ES-S03) | `restart: unless-stopped`; reintento del celular | Toxiproxy (TEC-26) pendiente | PENDIENTE-F6 |

## 3 · Elastic (la forma)

| Qué pide | En FlorLogic | Evidencia | Estado |
|---|---|---|---|
| Responder igual bajo carga variable | Temporada alta: +60 % de registros, +30–40 % de personal (ESC-61). Hilos virtuales absorben la E/S concurrente sin un hilo del sistema por petición | `application.yaml` (`spring.threads.virtual.enabled: true`) | PARCIAL |
| Escalar sin intervención | En un nodo de finca la elasticidad es la del hardware; el disparador para separar el trabajador o subir la máquina es SPK-04 («Si la sincronización de jornada supera 30 min …») | Sin medir | PENDIENTE-F5/F6: 13 simuladores a la vez (TX-1.T 6) |

## 4 · Responsive (el valor)

| Qué pide | En FlorLogic | Evidencia | Estado |
|---|---|---|---|
| Tiempos de respuesta acotados y consistentes | El gateway tiene un presupuesto: 2 s de DNS + 2 s de conexión + 25 s de espera < 30 s del celular (comentario de `default.conf.template`) | `default.conf.template` | HAY (presupuesto) |
| Respuesta inmediata al usuario | En el celular: confirmación ≤ 200 ms en el 95 % (ESC-36), sin red (ESC-37) | PWA | PENDIENTE-F8 |
| Medir | Percentiles de `/ingesta` con Micrometer | — | PENDIENTE-F6 |

## 5 · Frase de defensa

> TX-1 es reactiva en su forma más útil para una finca: el celular trabaja sin red y entrega mensajes idempotentes
> cuando puede; el nodo acusa recibo, frena con `409` y `413` cuando hace falta, y cada pieza falla sola sin arrastrar
> a las demás. La elasticidad está acotada por diseño a una finca, con un disparador medible para crecer.
