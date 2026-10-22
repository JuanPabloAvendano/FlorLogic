REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 20-oct-2026, a pedido de Juan
Estado de revisión: SIN REVISAR
Manda por encima: `IA_00-LEEME-transacciones.md` (aceptación) · `IA_TECNOLOGIAS-y-estructura.md` · `IA_TX1-sincronizacion.md` (rama `documentacion-desarrollo`) · `IA_TECNOLOGIAS-vigentes.md` · la palabra de Juan

# Prioridad de los componentes de TX-1 y cómo conviven otras tecnologías con el monolito modular

Es una propuesta para revisar y **no autoriza código**. TX-1 sigue `AJUSTAR 19-oct`: solo el canal está aceptado.

## 1 · Componentes de TX-1 ordenados por importancia para el producto

**Criterio:** FlorLogic sirve si **lo que se anota en la cama llega al nodo sin perderse, queda clasificado y el
gerente lo ve**. Cada nivel se apoya en el anterior. El orden sigue el principio de que el dominio tira de la
infraestructura: un componente entra cuando una regla lo pide.

### Nivel 0 · Sin esto no hay producto

| # | Módulo | Por qué va aquí | Estado (20-oct) |
|---|---|---|---|
| 1 | **`ingesta`**: ingreso de sincronización | Es la puerta del dato. Le faltan la sesión `{id}`, la huella con los tres casos de ADR-027 y guardar el acuse y los datos en la misma transacción | Canal hecho y verificado |
| 2 | **`configuracion`**: catálogo y versiones | Sin producción, sección, cama y campo una anotación es un JSON sin significado. También guarda la zona horaria de la jornada (P-S04) | Solo `package-info` |
| 3 | **`reglas`**: motor del servidor | Clasifica cada anotación como aplicada, marcada o rechazada. En el camino mínimo bastan reglas en Java; JSON Logic (TEC-27) llega después | Solo `package-info` |
| 4 | **`ciclo`**: ciclo de producción | Convierte la anotación en un hecho de producción. Lo incompleto y lo marcado quedan como trabajo del gerente (P-S01) | Solo `package-info` |
| 5 | **`consulta`**: lo esperado, lo recibido y lo que falta | Es el primer valor que ve el gerente (ESC-33, ESC-60). Sin esto el dato está guardado pero no sirve a nadie | Solo `package-info` |

### Nivel 1 · Confianza: que el dato sea de quien dice ser

| # | Módulo | Por qué va aquí |
|---|---|---|
| 6 | **`identidad`**: verificar y renovar la credencial | Cada registro necesita un autor identificado (ESC-49) y el permiso retirado tiene que contar (ESC-22). Usa TEC-13 y TEC-14 |
| 7 | **`llaves`** | Firma la credencial. Se construye junto con el 6 |
| 8 | **`dispositivos`** | Distingue celular conocido de desconocido; si no lo conoce, la sesión entra marcada. Cuesta poco |
| 9 | **`bitacora`** | Registro de solo inserción (ESC-40). Es la defensa ante una disputa sobre los datos |
| 10 | **Gateway Nginx + TLS con la CA de la finca** (TEC-15, TEC-16) | Hace falta en cuanto viaja una credencial: no puede ir en claro por el Wi-Fi |

### Nivel 2 · Operación sostenida

| # | Módulo | Por qué va aquí |
|---|---|---|
| 11 | **`notificacion`** | Avisa al gerente. Está bloqueado por una decisión de Juan: correo y push necesitan internet y el nodo funciona sin red |
| 12 | **`trabajos`** (db-scheduler, TEC-09) | Lo pide el 11 para guardar el aviso y reenviarlo cuando vuelva la red. No va antes |
| 13 | **WAF** (ModSecurity v3 + OWASP CRS 4) | En una red local cerrada el riesgo es menor que en internet, y en APIs JSON produce falsos positivos. Va después del gateway |

### Nivel 3 · Al final

14. **Caché de producciones activas**: es una optimización. No hace falta mientras PostgreSQL responda bien.
15. **`proyeccion`**: está bloqueada por BR-23 y en TX-1 sería simulada.
16. **Migraciones**: ya están con Flyway (TEC-08).

**Transversal, desde ya: `observabilidad`** (TEC-17). No está en la lista porque no compite con los demás: cada
regla que se escribe trae su métrica (Micrometer) y su log estructurado, sin datos de negocio (BB-13, ESC-30, CN-34).

---

## 2 · Cómo conviven otras tecnologías con un monolito modular

La idea central: **el monolito modular limita cómo se corta el código de negocio, no cuántos procesos corren en el
nodo.** ADR-017 prohíbe partir la *lógica* en microservicios. El gateway, el WAF, Grafana o la base no son lógica de
negocio: son procesos de infraestructura que corren junto al monolito.

Hay dos tipos de pieza.

### A · Bibliotecas dentro del proceso del monolito

Ejemplos: db-scheduler, Micrometer, Nimbus, Spring Security, JSON Logic y los eventos de Spring Modulith.

- Viven en la `infraestructura/` del módulo que las pidió, detrás de un puerto de `servicios/output/port`. El
  dominio nunca las ve.
- No agregan contenedores. db-scheduler, por ejemplo, es solo una tabla más en PostgreSQL.
- Los módulos se hablan dentro del proceso de dos formas: llamando a la API pública del otro módulo, o publicando un
  evento. Ejemplo: `ingesta` publica un evento de anotación clasificada y `notificacion` lo escucha con
  `@ApplicationModuleListener`. La tabla `event_publication` de Modulith garantiza que el evento no se pierde si el
  proceso muere después del commit.

### B · Procesos aparte, junto al monolito

Ejemplos: Nginx con el WAF, PostgreSQL, Prometheus, Loki, Grafana, restic, MinIO.

- Son contenedores en `codigo/despliegue/compose.yaml`.
- El monolito no los conoce como código. Se comunican por **interfaces estándar**: HTTP, stdout, un endpoint de
  métricas o archivos.

```
 celular ──HTTPS──► [ Nginx + ModSecurity/CRS ] ──HTTP──► [ backend-finca (JVM, 15 módulos) ] ──JDBC──► [ PostgreSQL ]
                    TLS · limit_req · WAF               │  /actuator/prometheus ◄── scrape ── [ Prometheus ] ─┐
                                                        │  stdout (logs JSON)  ──► [ Alloy ] ──► [ Loki ]    ├─► [ Grafana ]
                                                        └─ (sin saber que existen)                            ┘
```

### Gateway Nginx (TEC-15, aceptado)

- Termina el TLS con el certificado de la CA de la finca (TEC-16), limita la tasa con `limit_req`, sirve la web
  estática y reenvía con `proxy_pass` a `backend:8080`.
- En el backend se configura `server.forward-headers-strategy=framework` para que Spring confíe en los
  `X-Forwarded-*`.
- **La firma de la credencial la verifica el backend**, en el módulo `identidad`, no Nginx: es una regla de dominio
  (quién puede capturar) y tiene que poder probarse sin el gateway.

### WAF ModSecurity v3 + OWASP CRS 4 (no está aceptado)

- El C4 lo eligió (`docs/03-arquitectura/spikes/IA_TECNOLOGIAS-C4.md`, imagen `owasp/modsecurity-crs` con NGINX),
  pero TEC-15 solo acepta Nginx. **Necesita su propia fila TEC.**
- Corre dentro del mismo proceso de Nginx: no es otro contenedor.
- **Efecto en el contrato:** cuando el WAF bloquea, responde `403` antes de que la petición llegue al backend. El
  contrato hoy solo conoce `200` y `422`. El simulador tiene que tratar ese `403` como «no entregado, conservar» y
  nunca borrar del almacén de salida (ADR-002).
- Conviene arrancarlo en `DetectionOnly`, con nivel de paranoia 1, y ajustar exclusiones para `/ingesta/v1/`. Los
  límites de tamaño (`client_max_body_size`, `SecRequestBodyLimit`) tienen que ser mayores que la partición más
  grande.
- Se prueba con Testcontainers en `pruebas-extremo-a-extremo`: las particiones doradas pasan por el gateway y
  ninguna puede ser bloqueada.

### Grafana para logs: choca con lo aceptado

TEC-17 eligió Actuator, Micrometer y logs JSON, y **descartó a propósito «Prometheus y Grafana en el nodo (dos piezas
más)»**. Además, Grafana no guarda logs: necesita **Loki** para los logs, **Prometheus** para las métricas y un
recolector (Alloy). Son tres o cuatro contenedores en un nodo cuyo hardware aún está abierto (CN-20).

Lo bueno es que **el código casi no cambia**:

- Logs: `logging.structured.format.console=ecs`, que Spring Boot trae sin dependencias.
- Métricas: `micrometer-registry-prometheus`, que es una dependencia nueva y necesita su fila.

Recomendación: un perfil `observabilidad` en Compose solo para desarrollo. Así se ve todo en Grafana sin cargar el
nodo, y la pregunta de si va a producción queda abierta como un ajuste de TEC-17 que decide Juan. No confundirlo con
TEC-18: la telemetría que sale a la nube va con un esquema cerrado, y Grafana u OpenTelemetry no la reemplazan.

### Correo y push

Siguen el mismo molde. El puerto vive en `notificacion/servicios/output/port` y el adaptador habla con un servicio
externo (SMTP o FCM). Por el local-first, el aviso se guarda en la base y db-scheduler lo reenvía cuando vuelve la
red. Ninguno de los dos tiene tecnología aceptada todavía.

---

## 3 · Lo que decide Juan

1. Si acepta este orden, sobre todo si `consulta` (5) va antes que `identidad` (6).
2. **WAF:** abrir una fila TEC nueva para ModSecurity + CRS 4 y aceptar el `403` en el contrato.
3. **Grafana:** solo en desarrollo, o ajustar TEC-17 para llevarlo también al nodo.
4. **Notificación sin red:** si el aviso al gerente va por push (internet) o por la red de la finca.
