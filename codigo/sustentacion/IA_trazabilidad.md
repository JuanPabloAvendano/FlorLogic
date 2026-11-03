REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# Matriz de trazabilidad de TX-1: escenario → decisión → tecnología → código → prueba

La guía pide «trazabilidad» dentro de Gobierno arquitectónico. La cadena es:

`ESC-nn` (README §3, humano) → `ADR-nnn` (README §4) → `TEC-nn` (`.claude/contexto/IA_TECNOLOGIAS-vigentes.md`) →
módulo / clase (`codigo/`) → prueba automática.

Rutas de clases relativas a `codigo/backend-finca/src/main/java/co/florlogic/finca/` salvo que digan otra cosa.
Las pruebas, relativas a la carpeta `src/test/java` de su módulo. «Pendiente (Fx)» = la fase de
`codigo/IA_CONSTRUCCION-TX1.md` que la entrega.

## 1 · Lo que ya corre

| ESC (resumen) | ADR (lo que dice) | TEC | Módulo / clase | Prueba |
|---|---|---|---|---|
| ESC-01 · 0 registros perdidos y 0 duplicados tras sincronizar | ADR-027: «La identidad técnica de cada anotación es un código único que genera el teléfono al confirmar la captura, sin red» | TEC-07 `JdbcClient`, TEC-06 PostgreSQL | `ingesta/infraestructura/output/repositorios/estructurados/AnotacionRepositorioAdapter` (llave única `anotacion_uuid`; en F1 el puerto `AnotacionRepositorio` pasa a `guardadasCon` + `guardarAceptada` / `guardarRechazada`) | `CanalParticionesTest.reenviar_la_misma_particion_no_duplica`, `AnotacionRepositorioAdapterTest` (hereda `AnotacionRepositorioContratoTest`) |
| ESC-38 · 0 registros rechazados o perdidos con todos sincronizando a la vez | ADR-002: «entra a un almacén de salida persistente y no se borra hasta que el servidor confirma» | TEC-04 hilos virtuales, TEC-06 | `ingesta/servicios/casosuso/impl/RecibirParticionCasoUsoImpl` (una transacción), `AvanceDelDispositivoAgregado` (versión optimista) | `CanalParticionesTest.dos_particiones_del_mismo_celular_a_la_vez_la_que_pierde_es_409_y_al_reintentar_entra`, `AvanceRepositorioAdapterTest`, `AvanceDelDispositivoAgregadoTest` |
| ESC-38 · la partición entra entera o no entra | README §8: «o entran todas las anotaciones de la partición o no entra ninguna, y reenviar siempre es seguro» | TEC-06 | `RecibirParticionCasoUsoImpl` | `CanalParticionesTest.si_la_base_falla_a_mitad_es_500_generico_y_no_queda_nada_a_medias`, `…una_anotacion_que_el_dominio_rechaza_es_422_y_no_guarda_ni_mueve_el_avance` |
| ESC-47 · el celular sabe qué quedó confirmado | ADR-028: «El servidor abre el intercambio y deja registrado qué dispositivo lo trajo» | TEC-11 contrato | `contrato/…/dto/ResultadoParticionDto`, `PrimerAcuseDto`; `AvanceRepositorioAdapter` | `CanalParticionesTest.confirmado_hasta_es_el_avance_real_del_celular`, `contrato` · `DtoJsonTest` (dorados) |
| ESC-56 · rechazo sin códigos técnicos para el usuario | ADR-006: «incluye información variable del motivo de rechazo, no código de error únicamente» | TEC-10 Jackson 3 | `infraestructura/input/controladores/ensamblador/ParticionEnsamblador`, `ManejadorDeErrores` (`problem+json`) | `CanalParticionesTest.cualquier_fallo_de_forma_es_422_rechazado_y_no_guarda_nada`, `ParticionEnsambladorTest`, `LectorDtoTest` |
| ESC-50 · el contenido de negocio no se expone; acceso controlado | ADR-012: «La llave es de cada empresa» | TEC-33…36 OpenBao + `pkcs11`, TEC-34 Spring Cloud Vault | `despliegue/openbao/` (`init.sh`, `politica-backend-finca.hcl`), `compose.yaml` | `pruebas-extremo-a-extremo` · `NodoCompletoTest`, `NodoArranqueSeguroTest` (6) |
| ESC-50, ESC-61 · la única puerta del nodo filtra ataques | BB-06 (gateway), BB-08 (WAF; TEC-31 la ajusta al nodo) | TEC-15 Nginx 1.30.6, TEC-31 ModSecurity + CRS 4.30.0 | `despliegue/gateway/default.conf.template`, `modsecurity-override.conf.template` | `CanalPorGatewayTest` (25), `NodoCompletoTest.el_gateway_es_la_unica_puerta_del_nodo` |
| ESC-16 · mismo paquete en nube y en sitio, 0 cambios de código | ADR-016: «Todo lo propio de cada entorno vive en configuración, nunca en el código» | TEC-28 Dockerfile + Compose | `backend-finca/Dockerfile`, `despliegue/compose.yaml`, `application.yaml` con `FLORLOGIC_*` | `NodoCompletoTest` (construye las imágenes y levanta el `compose.yaml` real) |
| ESC-21 · esquema migrado sin intervención | BB-15: «Herramienta de migración … desde el andamiaje inicial» | TEC-08 Flyway 12.4.0 | `src/main/resources/db/migration/V1…V3` | `ArranqueBackendFincaTest` |
| (Arquitectura) dependencias hacia el dominio | ADR-017 y la regla de capas de `.claude/CLAUDE.md` | TEC-05 Spring Modulith 2.1.1 + ArchUnit 1.5.1 | los 17 paquetes de módulo | `CapasTest` (7 reglas), `ModulosTest` (2) |

## 2 · Lo que entra con las fases de la construcción

| ESC | ADR | TEC | Dónde irá | Prueba | Fase |
|---|---|---|---|---|---|
| ESC-49 · cada registro con autor identificado | ADR-007 (ajustado por TEC-32: Keycloak) | TEC-32 Keycloak 26, TEC-14 Spring Security | `identidad/`, configuración de servidor de recursos | Pendiente (F3): JWT inválido → 401; rol `gerente` en `/ingesta` → 403 | F3 |
| ESC-22 · permiso retirado no se acepta | ADR-007 | TEC-32 | `identidad/` | Pendiente (F3) | F3 |
| ESC-28 · cierre por inactividad | — | TEC-32 (tiempos de sesión del realm) | realm `florlogic` | Pendiente (F3) | F3 |
| ESC-17 · reloj desviado marcado | ADR-014, ADR-031 | — | `ingesta` (sesión, `desfase_ms`) | Pendiente (F1) | F1 |
| ESC-34 · duplicado con huella distinta detectado | ADR-027 | TEC-10 forma canónica, TEC-37 (solo pruebas) | `CalculadorDeHuella` (CT-6), `contrato/…/HuellaCanonica` | Pendiente (F1): vectores RFC 8785 | F1 |
| ESC-57 · 0 divergencias cliente/servidor | ADR-006, ADR-029 | TEC-27 JSON Logic | `reglas/`, `configuracion/` | Pendiente (F2) | F2 |
| ESC-33, ESC-60 · consolidado visible en ≤ 1 h | ADR-010, ADR-026 | TEC-24 Thymeleaf + htmx | `consulta/` + web | Pendiente (F4) | F4 |
| ESC-20 · aviso de dispositivo vencido | BB-10, ADR-009 | TEC-09 db-scheduler, ntfy (fila TEC por abrir) | `notificacion/`, `trabajos/` | Pendiente (F5) | F5 |
| ESC-40 · bitácora inmodificable | ADR-022 | TEC-06 (permisos de solo inserción) | `bitacora/` | Pendiente (F3) | F3 |
| ESC-30 · diagnóstico sin datos de negocio | BB-13 | TEC-17 Actuator, Micrometer, logs JSON | `observabilidad/`, `application.yaml` | Pendiente (F6) | F6 |
| ESC-04, ESC-36 · captura sin red, confirmación ≤ 200 ms | ADR-002, ADR-008 | BB-02 IndexedDB | PWA (CT-4) | Pendiente (F8) | F8 |
| ESC-03, ESC-19 · respaldo cifrado y verificado | ADR-012 | TEC-19, TEC-20, TEC-21 | `respaldo/` | Fuera de TX-1 (TX-2) | — |

## 3 · Cómo se mantiene

1. Una clase nueva lleva en su Javadoc el ESC o la decisión que la pide (ya se hace con `TEC-nn` y `A-n`).
2. Al cerrar una fase, la fila pasa de §2 a §1 con el nombre real de la prueba.
3. Una fila sin prueba **no** está terminada (regla PR-04 de `.claude/CLAUDE.md`: «Sin prueba, no está terminado»).

## 4 · Huecos de trazabilidad detectados

- El README no lista de qué ESC deriva cada ADR; la columna «Deriva de» está en `ADR.xlsx`, que no está en git.
- Los ESC no tienen todavía una prueba de carga que mida sus números (≤ 30 min, ≤ 200 ms): solo se prueban los
  invariantes (0 perdidos, 0 duplicados).
