REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 8-oct-2026, a pedido de Juan
Estado de revisión: POR ACEPTAR — esta carpeta no autoriza a programar nada hasta que Juan marque cada fila
Manda por encima: `ADR.xlsx` (hoja ADR) · `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx` · `docs/03-arquitectura/decisiones/`

# Las dos transacciones de FlorLogic — índice y aceptación

## Qué es esta carpeta

El 8-oct-2026 Juan fijó que el proyecto pasa a programarse alrededor de **dos transacciones completas**,
**diferentes entre sí**, que juntas ocupen la mayor cantidad de componentes. Aquí está todo lo que hay
que aceptar antes de escribir la primera línea:

| Archivo | Qué contiene |
|---|---|
| `IA_TX1-sincronizacion.md` | **TX-1**: celular simulado → gateway → backend → base, ida y vuelta. Recorrido, canales, contrato, cobertura, escenarios, event storming ES-S y casos de uso |
| `IA_TX2-respaldo-y-observabilidad.md` | **TX-2**: respaldo cifrado en la nube con sus dos planos de observabilidad. Lo mismo, con el event storming ES-R |
| `IA_ES3-nodo-danado-sin-red.md` | **Event storming ES-N**: qué pasa si se daña el nodo de la finca sin red. Opciones de continuidad y la factibilidad del sistema completo en el celular del gerente |
| `IA_TECNOLOGIAS-y-estructura.md` | TEC-01..TEC-30 sobre Java, Spring Boot y Maven · módulos Maven · paquetes · patrones · prácticas |
| `IA_TRANSACCIONES-mazo.html` | **El mazo con los diagramas:** cobertura C4, secuencias, casos de uso, árbol de contingencia y tarjetas. Se abre en el navegador |

## Lo que dijo Juan el 8-oct-2026, tal cual

1. «Necesito que sean 2 transacciones diferentes completas que ocupen la mayor cantidad de componentes.»
2. «Simulador de contrato para acciones básicas pero el foco sigue siendo una transacción completa
   especifica, considero que estos añadidos pueden crecer más adelante sin cambiar la transacción
   tanto tanto.»
3. «me gustaría que respaldo fuera acompañado de observabilidad.»
4. «una de ellas me gustaría que se pudiera correr a veces si es necesario el sistema completo en un
   contenedor en el dispositivo movil del gerente de producción».
5. «La documentación vive en el espacio IA con etiqueta IA, se van a requerir modelos y feedback
   completo para ir refinando los modelos y los conceptos, pero tendrán que ser aceptados para poder
   considerarlos válidos para empezar la programación.»
6. «vamos con lo rápido para nosotros, java spring boot maven para los principales servicios.»

`[!]` **El punto 6 es una decisión de Juan pendiente de ADR**, igual que las del 15-sep. Si quieres
que quede en `decisiones/`, tienes que escribirlo tú: esa carpeta es humana.

## Cómo se acepta

Cambia la columna **Estado** de cada fila:
- `POR ACEPTAR` → `ACEPTADO <fecha>`, o
- `AJUSTAR <fecha> · <qué>`, o
- `RECHAZADO <fecha> · <por qué>`.

Una transacción **se puede empezar a programar** cuando su fila TX, todas sus preguntas P y las
TEC que usa están en `ACEPTADO`.

## Tabla de aceptación

### Las transacciones y sus piezas

| ID | Qué | Dónde | Estado |
|---|---|---|---|
| TX-1 | Sincronizar una jornada ya capturada (18 pasos, 6 de 9 contenedores, 16 de 20 componentes del backend) | `IA_TX1` §2, §5 | POR ACEPTAR |
| TX-1.C | Los canales C1, C2 y C3; C4 en contingencia; C5 descartada | `IA_TX1` §3 | POR ACEPTAR |
| TX-1.K | La forma del contrato | `IA_TX1` §4 | POR ACEPTAR |
| TX-1.T | Criterio de terminado (7 pruebas) | `IA_TX1` §10 | POR ACEPTAR |
| TX-2 | Respaldar con observabilidad (17 pasos, 7 de 9 contenedores, 5 de 6 componentes de la nube) | `IA_TX2` §2, §3 | POR ACEPTAR |
| TX-2.O | Los dos planos de observabilidad y sus métricas | `IA_TX2` §4 | POR ACEPTAR |
| ES-S | 17 tarjetas de la sincronización | `IA_TX1` §7 | POR ACEPTAR |
| ES-R | 14 tarjetas del respaldo | `IA_TX2` §7 | POR ACEPTAR |
| ES-N | 14 tarjetas del nodo dañado | `IA_ES3` §1 | POR ACEPTAR |
| OC | Opciones de continuidad OC-1..OC-9 y su orden por capas | `IA_ES3` §2 | POR ACEPTAR |
| UC-S | Casos de uso de TX-1 (UC-S1..UC-S8) | `IA_TX1` §8 · mazo | POR ACEPTAR |
| UC-R | Casos de uso de TX-2 (UC-R1..UC-R9) | `IA_TX2` §8 · mazo | POR ACEPTAR |
| UC-C | Casos de uso de la contingencia (UC-C1..UC-C5) | mazo | POR ACEPTAR |

### Las preguntas que hay que contestar (la recomendada en cada archivo)

| ID | Pregunta corta | Archivo | Estado |
|---|---|---|---|
| P-S01 | Captura incompleta: ¿la decisión del 15-sep o la respuesta del 4-oct? | TX1 | POR ACEPTAR |
| P-S02 | Choque de dos capturas: ¿`RF-022` o el gerente, como dice el modelo? | TX1 | POR ACEPTAR |
| P-S03 | ¿Qué borra el celular y cuándo? (retención hasta respaldo) | TX1 | POR ACEPTAR |
| P-S04 | «Jornada» provisional = día natural de la finca | TX1 | POR ACEPTAR |
| P-S05 | Nombres del contrato | TX1 | POR ACEPTAR |
| P-S06 | ¿El canal por archivo entra en la primera versión? | TX1 | POR ACEPTAR |
| P-S07 | Permiso retirado: ¿qué pasa con lo capturado antes? | TX1 | POR ACEPTAR |
| P-S08 | ¿Entra el tablero en TX-1? | TX1 | POR ACEPTAR |
| P-S09 | Umbral provisional de la hora dudosa: 60 s | TX1 | POR ACEPTAR |
| P-S10 | Sesión parcial a los 10 min sin actividad | TX1 | POR ACEPTAR |
| P-R01 | Cada cuánto se respalda | TX2 | POR ACEPTAR |
| P-R02 | Volcado deduplicado o archivado continuo | TX2 | POR ACEPTAR |
| P-R03 | Copia local además de la nube | TX2 | POR ACEPTAR |
| P-R04 | Suscripción vencida: ¿se sigue aceptando el respaldo? | TX2 | POR ACEPTAR |
| P-R05 | Retención en la nube: 30 diarias, 12 mensuales y 5 anuales | TX2 | POR ACEPTAR |
| P-R06 | La prueba de restauración corre en la finca | TX2 | POR ACEPTAR |
| P-R07 | La copia de la llave del cliente la guarda el cliente | TX2 | POR ACEPTAR |
| P-R08 | Custodia 2-de-3 contra los dos ejemplares de `ADR-012` | TX2 | POR ACEPTAR |
| P-R09 | Canal de las alertas | TX2 | POR ACEPTAR |
| P-R10 | Proveedor de nube | TX2 | POR ACEPTAR |
| P-N01 | Credencial con gracia de contingencia | ES3 | POR ACEPTAR |
| P-N02 | Recordatorio que distingue la culpa | ES3 | POR ACEPTAR |
| P-N03 | El dato de la finca en el celular del gerente | ES3 | POR ACEPTAR |
| P-N04 | Hardware de contingencia en la instalación | ES3 | POR ACEPTAR |
| P-N05 | Spike del sistema completo en el celular del gerente | ES3 | POR ACEPTAR |
| P-N06 | Escenario nuevo «cae el nodo de la finca» | ES3 | POR ACEPTAR |
| P-T01..P-T07 | Repo, paquetes, Java, restic, web, Nginx, Compose | TEC | POR ACEPTAR |

### Las tecnologías

| ID | Propuesta | Estado |
|---|---|---|
| TEC-01 | Java 21 LTS | POR ACEPTAR |
| TEC-02 | Spring Boot 4.1.x | POR ACEPTAR |
| TEC-03 | Maven multi-módulo reproducible | POR ACEPTAR |
| TEC-04 | Spring Web MVC con hilos virtuales | POR ACEPTAR |
| TEC-05 | Monolito modular con Spring Modulith | POR ACEPTAR |
| TEC-06 | PostgreSQL 16 | POR ACEPTAR |
| TEC-07 | Spring JDBC (`JdbcClient`) | POR ACEPTAR |
| TEC-08 | Flyway | POR ACEPTAR |
| TEC-09 | db-scheduler | POR ACEPTAR |
| TEC-10 | Jackson y huella canónica | POR ACEPTAR |
| TEC-11 | Módulo `contrato` con JSON Schema versionado | POR ACEPTAR |
| TEC-12 | UUID v7 | POR ACEPTAR |
| TEC-13 | JWS Ed25519 con Nimbus | POR ACEPTAR |
| TEC-14 | Spring Security 7 | POR ACEPTAR |
| TEC-15 | Nginx como gateway | POR ACEPTAR |
| TEC-16 | Autoridad certificadora propia de la finca | POR ACEPTAR |
| TEC-17 | Actuator, Micrometer y logs JSON | POR ACEPTAR |
| TEC-18 | Telemetría propia con esquema cerrado | POR ACEPTAR |
| TEC-19 | `pg_dump` en formato custom | POR ACEPTAR |
| TEC-20 | restic | POR ACEPTAR |
| TEC-21 | Almacén S3 con bloqueo de retención | POR ACEPTAR |
| TEC-22 | Servicios en línea en Spring Boot con Thymeleaf | POR ACEPTAR |
| TEC-23 | TLS mutuo finca → nube | POR ACEPTAR |
| TEC-24 | Web con Thymeleaf y htmx | POR ACEPTAR |
| TEC-25 | Simulador en Java con picocli y SQLite | POR ACEPTAR |
| TEC-26 | Testcontainers y Toxiproxy | POR ACEPTAR |
| TEC-27 | JSON Logic en Java | POR ACEPTAR |
| TEC-28 | Imagen OCI con Jib, multi-arquitectura, y Compose | POR ACEPTAR |
| TEC-29 | Debian 13 o Ubuntu 24.04 en el nodo | POR ACEPTAR |
| TEC-30 | GitHub Actions | POR ACEPTAR |

## La cobertura, de un vistazo

| | TX-1 | TX-2 | Juntas |
|---|---|---|---|
| Contenedores (de 9) | 6 | 7 | **9** |
| Componentes del backend (de 20) | 16 (2 a medias) | 9 (1 a medias) | **17** |
| Componentes de la nube (de 6) | 0 | 5 | **5** |
| Escenarios de calidad que se demuestran enteros o en lo esencial | 11 | 6 | — |

No tocan: Generador de documentos, Interfaz de salida de datos, Lectura del sistema heredado y
Distribución de versiones. Son la tercera y la cuarta transacción natural, para cuando estas dos
estén cerradas.

## Los choques que esta carpeta encontró o confirmó

1. **Captura incompleta:** decisión de Juan del 15-sep contra su respuesta del 4-oct (P-S01).
2. **Choque de dos capturas:** `RF-022` y `ADR-027` contra el modelo, que pone al gerente (P-S02).
3. **`ESC-19` contra `ADR-012`:** el operador no puede restaurar sin usar la llave (P-R06).
4. **La credencial de 24 h de `ADR-007` detiene el campo** si el nodo cae más de un día (P-N01).
5. **El recordatorio de `ADR-025` castiga al operario** por un nodo caído (P-N02).
6. **La pérdida cero de `CN-15`** no se cumple con volcados periódicos (P-S03 y P-R02).
7. **Ningún escenario cubre «cae el nodo de la finca»**: `ESC-59` solo habla de la nube (P-N06).
8. **El modelo dibuja una aplicación de una sola página**; la propuesta es Thymeleaf y htmx (P-T05).
