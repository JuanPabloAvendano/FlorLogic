# Tecnologías del modelo C4 de FlorLogic

> **REGISTRO DE IA · NO ES FUENTE**
> Generado por: Claude · 23-sep-2026, a pedido de Jerónimo
> Estado: **SIN REVISAR**
> Manda sobre esto: `app-captura/` (todo lo de la captura) · `Documentacion/Drivers-Arquitectonicos/ADR-PoC-Spikes.xlsx` · `DRIVERS_ARQUITECTONICOS.md` y sus cuatro `.xlsx`

Este registro es **el único lugar** donde las tecnologías del diagrama se cruzan con ADR, restricciones y
spikes. El diagrama no cita ningún documento.

## 0 · Qué se hizo

**Archivo editado:** `Documentacion/Drivers-Arquitectonicos/Arquitectura FlorLogic.drawio` (11 páginas).
**Respaldo previo:** `Documentacion/Drivers-Arquitectonicos/Arquitectura FlorLogic.drawio.bak-tecnologias`,
idéntico byte a byte al archivo de las 15:39 del 23-sep (sha256 `0828b4f7…`). draw.io estaba cerrado al escribir.

**Qué se tocó, y nada más:**

- `c4Technology` de los 98 contenedores y componentes, los 5 nodos de despliegue y las 157 relaciones.
- `c4Application` de 8 límites de contenedor (el subtítulo `[Container: …]`). El de nivel 2 dice
  `Software System`: es el tipo, no una tecnología, y no se tocó.
- `fillColor` y `strokeColor` de 28 estilos: 24 pasan a verde (servicio existente) y 4 son el borde del
  contenedor «Aplicación web del sistema», que era `#118ACD` y se igualó al `#0E7DAD` de la leyenda.
- Una leyenda nueva por página, «Clasificación», con tres filas: Desarrollo propio, Servicio existente y
  Sistema externo. Son 11 leyendas de 7 celdas cada una (77 celdas).

**Qué no se tocó:** ninguna geometría (posición, tamaño, puntos de las flechas o de las etiquetas),
ningún `c4Name`, `c4Type`, `c4Description` ni `label`, ninguna dirección de flecha, ninguna persona, ningún
sistema externo, ningún título de página. No se agregó, eliminó ni movió ningún elemento salvo las leyendas.

**Dónde quedó cada leyenda.** En la esquina superior derecha, a 20 px a la derecha de la leyenda que ya
tenía cada página y a su misma altura. La página del API Gateway no tenía leyenda: allí va arriba a la
derecha, encima del contenedor «Backend de la finca». En ningún caso pisa un elemento ni una flecha.

**Las cifras:**

| | |
|---|---|
| Elementos distintos (contenedores y componentes) | **55**: **48 desarrollo propio** y **7 servicio existente** |
| Lo mismo, contando cada aparición en cada página | 98: 74 propios y 24 existentes |
| Tecnologías de elementos que salen de las fuentes | 32 de 55 elementos, y 4 de 5 nodos |
| Tecnologías de elementos que salen de consulta rápida | 23 de 55 elementos, y 1 de 5 nodos |
| Relaciones cuya tecnología sale de las fuentes | 122 de 157 |
| Relaciones cuya tecnología sale de consulta rápida | 35 de 157 |
| Códigos de documento retirados del diagrama | **0** (ver §3) |

Una tecnología cuenta como «consulta rápida» cuando el producto concreto salió de una de las cuatro
consultas de §4, aunque el tipo de pieza ya estuviera en el modelo o en un bloque de construcción.

**Abreviaturas de página:** `N1` contexto · `N2` contenedores · `N3-App` app de captura · `N3-GW` API Gateway
(WAF) · `N3-Ingreso`, `N3-Ciclo`, `N3-Identidad`, `N3-Operación` las cuatro páginas del backend ·
`N3-Web` aplicación web · `N3-Nube` servicios en línea · `Despliegue`.

## 1 · Elementos

La columna «Antes» es el `c4Technology` que tenía el diagrama. Un mismo elemento lleva la misma tecnología
en todas las páginas donde aparece.

**Cómo se clasificó.** *Desarrollo propio* es lo que escribe el equipo, aunque use bibliotecas: por eso
Quartz, Caffeine, Flyway, Apache POI o TanStack aparecen dentro de componentes azules. *Servicio existente*
es un producto que se instala y se configura: PostgreSQL, NGINX con ModSecurity, Backblaze B2 e IndexedDB
del navegador.

### Contenedores y componentes

| Elemento | Tipo | Página(s) | Categoría | Tecnología | Antes | Fuente |
|---|---|---|---|---|---|---|
| Aplicación de captura | Contenedor | N2, N3-GW, N3-Ingreso, Despliegue | Desarrollo propio | PWA · TypeScript sin framework · Service Worker | Aplicación móvil Android | app-captura/ (README, tsconfig, src/main.ts, dist/sw.js) · ADR-008 · BB-01 |
| Almacén del dispositivo | Contenedor | N2, N3-App, Despliegue | Servicio existente | IndexedDB de Chrome · cifrado AES-GCM (Web Crypto) | Base de datos local cifrada en el dispositivo | app-captura/src/almacen.ts (IndexedDB) · cifrado: plataforma web de la PWA, ver observación O-04 |
| API Gateway de la finca | Contenedor | N2, N3-App, N3-Ingreso, N3-Ciclo, N3-Identidad, N3-Web, Despliegue | Servicio existente | NGINX + ModSecurity v3 · OWASP CRS 4 | Proxy inverso | BB-06 (proxy inverso con TLS y límite de tasa en la finca) · producto: consulta rápida Q1 |
| Backend de la finca | Contenedor | N2, N3-App, N3-GW, N3-Nube, Despliegue | Desarrollo propio | Spring Boot 4.1 · Java 21 (OpenJDK) | Servicio de aplicación en Java | ADR-037 (Java) · SPK-18 (Spring Boot 4.1, OpenJDK 21) · criterio del encargo (Spring Boot preferente) |
| Base de datos de la finca | Contenedor | N2, N3-Ingreso, N3-Ciclo, N3-Identidad, N3-Operación, Despliegue | Servicio existente | PostgreSQL 16 | Base de datos relacional | BB-03 · CN-16 · SPK-13/15/17 (PostgreSQL 16.13) · SPK-18 |
| Aplicación web del sistema | Contenedor | N2, N3-GW, N3-Ciclo, N3-Identidad, Despliegue | Desarrollo propio | SPA en Vue 3 y TypeScript · Vite | Aplicación de una sola página, se ejecuta en el navegador | Consulta rápida Q2 · CN-18 (compartir TypeScript con la PWA) |
| Servicios en línea | Contenedor | N2, N3-Operación, Despliegue | Desarrollo propio | Spring Boot 4.1 · Java 21 (OpenJDK) | Servicio web en la nube | Consulta rápida Q3 (misma pila que el backend) · ADR-037 · SPK-18 |
| Custodia de respaldos | Contenedor | N2, N3-Nube, Despliegue | Servicio existente | Backblaze B2 · API S3 con Object Lock | Almacenamiento de objetos | BB-04 (bucket cifrado en línea) · producto: consulta rápida Q3 |
| Base de datos de los servicios en línea | Contenedor | N2, N3-Nube, Despliegue | Servicio existente | PostgreSQL 16 | Base de datos relacional | Consulta rápida Q3 (autoalojada en la misma VM) · misma versión que BB-03 |
| Verificación de permisos | Componente | N3-App | Desarrollo propio | Web Crypto API · credencial JWS Ed25519 | Credencial firmada | ADR-007 (credencial firmada offline) · SPK-14 (ed25519) · plataforma web de app-captura |
| Orquestador local | Componente | N3-App | Desarrollo propio | TypeScript · módulo ES de la PWA | Modulo PWA | app-captura/src/repositorio.ts y src/main.ts |
| Captura de identificador físico | Componente | N3-App | Desarrollo propio | BarcodeDetector + cámara (getUserMedia) | Lectura de códigos con la cámara | app-captura/src/escaner.ts (QR, Code 128, EAN-13) |
| Motor de reglas · instancia local | Componente | N3-App | Desarrollo propio | Evaluador propio en TypeScript · reglas JSON | Reglas en JSON con evaluador embebido | app-captura/src/reglas.ts + configuracion/reglas.v1.json · decisiones/2026-09-15 (JSON) |
| Servicio de cifrado del dispositivo | Componente | N3-App | Desarrollo propio | Web Crypto API · llave AES-GCM no exportable | Adaptador del almacén de llaves de Android | Plataforma web de app-captura · CN-28 · ver observación O-04 |
| Servicio de sincronización · cliente | Componente | N3-App | Desarrollo propio | Fetch API · outbox UUID v7 | Cliente HTTPS idempotente | app-captura/src/sincronizacion.ts e id.ts · ADR-002 · ADR-027 |
| Interfaz de captura | Componente | N3-App | Desarrollo propio | TypeScript · DOM propio sin framework | Pantallas de la aplicación móvil | app-captura/src/vista.ts y src/ui/ |
| WAF de la finca | Componente | N3-GW | Servicio existente | ModSecurity v3 · OWASP CRS 4 | Firewall de aplicaciones web · reglas OWASP (propuesta) | Modelo (reglas OWASP) · producto: consulta rápida Q1 |
| Servicio de API Gateway | Componente | N3-GW | Servicio existente | NGINX · proxy inverso y limit_req | Proxy inverso · enrutamiento | BB-06 · producto: consulta rápida Q1 |
| Ingreso de sincronización | Componente | N3-Ingreso | Desarrollo propio | Spring Web MVC · REST/JSON | API REST en Java | SPK-18 (esqueleto: Spring MVC) · ADR-002 · ADR-028 |
| Motor de reglas · instancia servidor | Componente | N3-Ingreso | Desarrollo propio | Evaluador propio en Java · reglas JSON (Jackson) | Reglas en JSON con evaluador en Java | ADR-006 · decisiones/2026-09-15 (JSON) · SPK-17 (evaluador Java + Jackson) · SPK-18 |
| Motor de proyección | Componente | N3-Ingreso, N3-Ciclo | Desarrollo propio | Servicio Spring · cálculo en Java 21 | Cálculo en Java | ADR-005 · ADR-037 · SPK-18 (código Java puro reutilizado) |
| Servicio de notificación | Componente | N3-Ingreso, N3-Ciclo | Desarrollo propio | Servicio Spring · Spring Data JPA | Módulo Java | SPK-18 (Spring Data JPA) · criterio del encargo |
| Gestión de dispositivos | Componente | N3-Ingreso, N3-Identidad, N3-Operación | Desarrollo propio | Servicio Spring · Spring Data JPA | Módulo Java | SPK-18 (Spring Data JPA) · criterio del encargo |
| Emisión de credenciales | Componente | N3-Ingreso, N3-Identidad | Desarrollo propio | Nimbus JOSE+JWT · JWS firmado con Ed25519 | Firma digital en Java | ADR-007 · SPK-18 (Spring Security + Nimbus JOSE) · SPK-14 (ed25519) |
| Datos maestros y parametrización versionada | Componente | N3-Ingreso, N3-Ciclo, N3-Identidad | Desarrollo propio | Spring Data JPA · paquete tar.zst firmado Ed25519 | Módulo Java · configuración versionada | ADR-029 · SPK-14 (tar + zstd, manifiesto sha256, firma ed25519) · SPK-18 |
| Bitácora de auditoría | Componente | N3-Ingreso, N3-Ciclo | Desarrollo propio | Spring Data JPA · registro de solo inserción | Módulo Java · registro de solo inserción | ADR-004 · ADR-035 · SPK-18 |
| Caché de producciones activas | Componente | N3-Ingreso, N3-Ciclo, N3-Operación | Desarrollo propio | Spring Cache + Caffeine · JSON en volumen LUKS | JSON cifrado dentro del backend | ADR-034 · SPK-18 (Spring Cache + Caffeine) · SPK-13 (cifrado en el volumen) |
| Ciclo de producción | Componente | N3-Ingreso, N3-Ciclo | Desarrollo propio | Servicio Spring · Spring Data JPA | Módulo Java | SPK-18 (Spring Data JPA para ciclo de producción) · ADR-020 |
| Planificador de tareas | Componente | N3-Ciclo, N3-Operación | Desarrollo propio | Quartz Scheduler · JobStore JDBC en PostgreSQL | Planificador en Java · trabajos guardados en la base | ADR-009 · BB-09 · SPK-18 (Quartz con almacén JDBC) |
| Generador de documentos | Componente | N3-Ciclo | Desarrollo propio | Apache POI · OpenPDF | Apache POI · biblioteca de PDF de Java | Modelo (Apache POI) · SPK-18 (Apache POI · OpenPDF) · ADR-013 |
| Interfaz de salida de datos | Componente | N3-Ciclo | Desarrollo propio | Spring Web MVC · OData v4 con Apache Olingo | API REST en Java · solo lectura | SPK-16 (caminos B/C, OData v4 con conector nativo de Power BI) · CN-10 · biblioteca: consulta rápida Q4 |
| Consulta y tableros | Componente | N3-Ciclo | Desarrollo propio | Spring Web MVC · REST/JSON | API REST en Java | SPK-18 (Spring MVC) · ADR-010 · ADR-023 |
| Identidad y permisos | Componente | N3-Identidad | Desarrollo propio | Spring Security · usuarios y roles en PostgreSQL | Módulo Java de identidad | BB-05 (proveedor de identidad propio en fase 1) · ADR-007 · CN-12 · SPK-18 |
| Custodia de llaves | Componente | N3-Identidad, N3-Operación | Desarrollo propio | Java KeyStore PKCS#12 (JCA) | Almacén de llaves en Java | Modelo vigente (almacén de llaves en Java, llamadas en proceso) · ADR-012 · ver observación O-01 |
| Observabilidad | Componente | N3-Operación | Desarrollo propio | Spring Boot Actuator · Micrometer · logs JSON | Registros estructurados y métricas | SPK-18 (Actuator + Micrometer) · BB-13 |
| Actualización y migraciones | Componente | N3-Operación | Desarrollo propio | Flyway · verifica la firma Ed25519 del artefacto | Migraciones versionadas en Java | CN-29 · BB-15 · SPK-18 (Flyway) · firma: mismo esquema de SPK-14 |
| Servicio de respaldo | Componente | N3-Operación | Desarrollo propio | pg_dump · restic (deduplica y cifra) | Volcado de la base y cifrado en Java | SPK-13 (pg_dump -Fc, restic deduplicado y cifrado, restic check) · ADR-012 |
| Lectura del sistema heredado | Componente | N3-Operación | Desarrollo propio | Spring Batch · UCanAccess (JDBC para Access) | UCanAccess · conector JDBC para Access | Modelo (UCanAccess) · SPK-18 (Spring Batch para la carga reanudable) |
| Sesión de usuario, roles y permisos | Componente | N3-Web | Desarrollo propio | Cookie de sesión HttpOnly · store Pinia | Cookie de sesión del navegador | Modelo (cookie de sesión) · Vue 3: consulta rápida Q2 |
| Tableros de operaciones | Componente | N3-Web | Desarrollo propio | Apache ECharts (vue-echarts) | Biblioteca de gráficos | Consulta rápida Q2 |
| Vista de camas | Componente | N3-Web | Desarrollo propio | SVG con componentes Vue 3 | Dibujo SVG en el navegador | Modelo (dibujo SVG en el navegador) · consulta rápida Q2 |
| Vista de proyección y desviación | Componente | N3-Web | Desarrollo propio | Apache ECharts (vue-echarts) | Biblioteca de gráficos | Consulta rápida Q2 |
| Panel de administración | Componente | N3-Web | Desarrollo propio | Formularios Vue 3 · TypeScript | Formularios web | Modelo (formularios web) · consulta rápida Q2 |
| Exportación de archivos | Componente | N3-Web | Desarrollo propio | Fetch API · descarga con Blob | Descarga de archivos | Modelo (descarga de archivos) · plataforma web |
| Navegación detallada | Componente | N3-Web | Desarrollo propio | TanStack Table (Vue) · carga progresiva | Biblioteca de tablas | Consulta rápida Q2 · ADR-023 (carga progresiva) |
| Servicios de escritura | Componente | N3-Web | Desarrollo propio | Fetch API + capa propia en TypeScript | fetch + capa propia | Modelo (fetch + capa propia) |
| Servicios de lectura | Componente | N3-Web | Desarrollo propio | Fetch API + TanStack Query | fetch + caché de consultas | Modelo (fetch + caché de consultas) · consulta rápida Q2 |
| Bandeja de correcciones | Componente | N3-Web | Desarrollo propio | Formularios Vue 3 · TypeScript | Formularios web | Modelo (formularios web) · consulta rápida Q2 |
| Vista de disponibilidad | Componente | N3-Web | Desarrollo propio | TanStack Table (Vue) | Biblioteca de tablas | Consulta rápida Q2 |
| Control de borde | Componente | N3-Nube | Desarrollo propio | Spring Security X.509 · TLS mutuo · Bucket4j | Filtro HTTP con TLS mutuo | Modelo (filtro HTTP con TLS mutuo, llamadas en proceso) · Spring: consulta rápida Q3 |
| Distribución de versiones | Componente | N3-Nube | Desarrollo propio | Spring Web MVC · REST/JSON | API REST | Modelo (API REST) · consulta rápida Q3 |
| Identidad y suscripción | Componente | N3-Nube | Desarrollo propio | Spring Security · Spring Data JPA | Módulo de identidad | Modelo (módulo de identidad) · consulta rápida Q3 |
| Recepción y entrega de respaldos | Componente | N3-Nube | Desarrollo propio | Spring Web MVC · API REST de restic | API REST | Modelo (API REST) · SPK-13 (restic) · consulta rápida Q3 |
| Recepción de telemetría | Componente | N3-Nube | Desarrollo propio | Spring Web MVC · REST/JSON | API REST | Modelo (API REST) · BB-13 · consulta rápida Q3 |
| Panel del superadministrador | Componente | N3-Nube | Desarrollo propio | Spring MVC · Thymeleaf | Vistas web generadas en el servidor | Modelo (vistas web generadas en el servidor) · consulta rápida Q3 |

### Nodos de despliegue

| Nodo | Página | Tecnología | Antes | Fuente |
|---|---|---|---|---|
| Celular de campo | Despliegue | Android · Google Chrome (PWA instalada) | Celular Android | app-captura/README.md (Agregar a pantalla de inicio) · src/escaner.ts (BarcodeDetector solo en Chrome para Android) |
| Servidor de la finca | Despliegue | Ubuntu Server 24.04 LTS · LUKS2 · Docker Compose | Servidor con disco cifrado | SPK-13 (LUKS, AES-256-XTS, exige Linux y AES-NI) · BB-14 (imagen única + Compose) · SPK-17 (banco Ubuntu 24.04) |
| Computador de oficina | Despliegue | Windows 11 · Microsoft Edge | Computador de escritorio | CN-10 (Power BI Desktop, solo corre en Windows) · navegador por defecto de Windows |
| Infraestructura en la nube | Despliegue | Hetzner Cloud · Ubuntu 24.04 · Docker Compose | Servidor virtual en la nube | Consulta rápida Q3 · BB-14 |
| Computador con el sistema heredado | Despliegue | Windows · carpeta compartida SMB | Computador con Windows | Modelo (computador con Windows, carpeta compartida) · SPK-18 §4a |

### Límites de contenedor (subtítulo `c4Application`)

| Límite | Página | Subtítulo nuevo | Antes |
|---|---|---|---|
| Aplicación de captura | N3-App | Container: PWA · TypeScript · Chrome para Android | Container: Aplicación móvil Dispositivo Celular Finca. |
| API Gateway de la finca | N3-GW | Container: NGINX con WAF | Container: Proxy inverso con WAF |
| Backend de la finca | N3-Ingreso | Container: Spring Boot 4.1 · Java 21 (OpenJDK) | Container: Computador Finca Servicio Java SpringBoot |
| Backend de la finca | N3-Ciclo | Container: Spring Boot 4.1 · Java 21 (OpenJDK) | Container: Computador Finca Servicio Java SpringBoot |
| Backend de la finca | N3-Identidad | Container: Spring Boot 4.1 · Java 21 (OpenJDK) | Container: Servicio de aplicación en Java |
| Backend de la finca | N3-Operación | Container: Spring Boot 4.1 · Java 21 (OpenJDK) | Container: Servicio de aplicación en Java |
| Aplicación web del sistema | N3-Web | Container: SPA en Vue 3 y TypeScript · Vite | Container: Aplicación de una sola página |
| Servicios en línea | N3-Nube | Container: Spring Boot 4.1 · Java 21 (OpenJDK) | Container: Servicio web en la nube |

### Lo que no lleva tecnología nueva

- **Personas** (15 apariciones) y **títulos de página** (11): no llevan tecnología en C4.
- **Sistemas externos de terceros** (12 apariciones, gris `#8C8496`): no se tocan, por encargo. Conservan lo
  que tenían: en `N1`, «Herramienta de análisis de la empresa» dice `Power BI` y «Sistema heredado de
  producción» dice `Microsoft Access`; en las demás páginas no tienen tecnología (ver O-07).
- **FlorLogic** (sistema de software de `N1`) y el límite de `N2`, que dice `Software System`.


## 2 · Relaciones

157 relaciones en 137 filas: una fila agrupa la misma relación con la misma tecnología en varias páginas.

| Origen → destino | Página(s) | Tecnología de comunicación | Antes | Fuente |
|---|---|---|---|---|
| Administrador del Sistema de Finca → FlorLogic | N1 | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| FlorLogic → Sistema heredado de producción | N1 | JDBC (UCanAccess) sobre la base Microsoft Access | Consulta sobre la base Microsoft Access | Modelo · SPK-18 §4a |
| Gerente de producción → Asistente de IA de un tercero | N1, N2 | Navegador Microsoft Edge por HTTPS, con internet | Navegador, con internet | Nodo «Computador de oficina» |
| Gerente de producción → FlorLogic | N1 | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Herramienta de análisis de la empresa → FlorLogic | N1 | OData v4 sobre HTTPS · vistas de solo lectura y exportación | Vistas de solo lectura y exportación | SPK-16 · ADR-013 · CN-10 |
| Operador de la plataforma → FlorLogic | N1 | Navegador web por HTTPS (TLS 1.3), desde fuera de la finca | Navegador, desde fuera de la finca | CN-28 (cifrado en tránsito) |
| Operario de campo → FlorLogic | N1 | Pantalla táctil del celular · PWA en Chrome para Android | Pantalla táctil del celular | app-captura/README.md |
| Personal de ventas → Asistente de IA de un tercero | N1, N2 | Navegador Microsoft Edge por HTTPS, con internet | Navegador, con internet | Nodo «Computador de oficina» |
| Personal de ventas → FlorLogic | N1 | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| API Gateway de la finca → Aplicación web del sistema | N2, Despliegue | HTTPS (TLS 1.3) · contenido estático servido por NGINX | HTTPS · contenido estático | Consulta rápida Q1 |
| API Gateway de la finca → Backend de la finca | N2, N3-App, Despliegue | HTTP/1.1 · REST/JSON sobre TCP/IP en red interna de Docker | HTTP interno de la finca | BB-06 · BB-14 (Compose) · consulta rápida Q1 |
| Administrador del sistema en la finca → Aplicación web del sistema | N2 | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Aplicación de captura → API Gateway de la finca | N2, Despliegue | HTTPS (TLS 1.3) · REST/JSON en red local · reanudable | HTTPS en red local · reanudable | app-captura/src/sincronizacion.ts · ADR-002 · CN-28 |
| Aplicación de captura → Almacén del dispositivo | N2, Despliegue | API de IndexedDB · lectura y escritura local, transaccional | Lectura y escritura local · transaccional | app-captura/src/almacen.ts |
| Aplicación web del sistema → API Gateway de la finca | N2, N3-Ciclo, N3-Identidad, Despliegue | HTTPS (TLS 1.3) · REST/JSON en red local | HTTPS/JSON en red local | CN-28 · SPK-18 (REST) |
| Backend de la finca → Base de datos de la finca | N2, Despliegue | JDBC (driver PostgreSQL) sobre TCP/IP local · puerto 5432 | SQL sobre conexión local | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Backend de la finca → Servicios en línea | N2, Despliegue | HTTPS con TLS mutuo por internet · REST · asíncrono, reintenta si la nube no responde | HTTPS por internet · asíncrono, reintenta si la nube no responde | Modelo (nivel 3) · SPK-13 · BB-13 |
| Backend de la finca → Servicios en línea | N2, Despliegue | HTTPS con TLS mutuo por internet · REST · artefacto firmado Ed25519, bajo demanda | HTTPS por internet · artefacto firmado, bajo demanda | Modelo (nivel 3) · SPK-14 (ed25519) |
| Backend de la finca → Servicios en línea | N2, Despliegue | HTTPS con TLS mutuo por internet · restic · bajo demanda, el archivo vuelve cifrado | HTTPS por internet · bajo demanda, el archivo vuelve cifrado | Modelo (nivel 3) · SPK-13 (restic) |
| Backend de la finca → Sistema heredado de producción | N2, Despliegue | JDBC (UCanAccess) sobre el archivo de Access, por carpeta compartida SMB | JDBC sobre el archivo de Access, por carpeta compartida | Modelo · SPK-18 §4a |
| Gerente de producción → Aplicación web del sistema | N2 | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Herramienta de análisis de la empresa → API Gateway de la finca | N2, N3-Ciclo, Despliegue | HTTPS (TLS 1.3) · OData v4 JSON en red local · solo lectura | HTTPS/JSON en red local · solo lectura | SPK-16 · CN-10 · consulta rápida Q4 |
| Operador de la plataforma → Servicios en línea | N2 | Navegador web por HTTPS (TLS 1.3), desde fuera de la finca | Navegador, desde fuera de la finca | CN-28 (cifrado en tránsito) |
| Operario de campo → Aplicación de captura | N2 | Pantalla táctil del celular · PWA en Chrome para Android | Pantalla táctil del celular | app-captura/README.md |
| Personal de ventas → Aplicación web del sistema | N2 | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Servicios en línea → Base de datos de los servicios en línea | N2, Despliegue | JDBC (driver PostgreSQL) sobre TCP/IP · puerto 5432 | SQL | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Servicios en línea → Custodia de respaldos | N2, Despliegue | HTTPS · API S3 (AWS SDK for Java) · entra y sale cifrado | API de almacenamiento de objetos · entra y sale cifrado | Consulta rápida Q3 · modelo |
| Interfaz de captura → Orquestador local | N3-App | Llamada en proceso · TypeScript | Llamada en proceso | app-captura/src/ (módulos ES) |
| Interfaz de captura → Verificación de permisos | N3-App | Llamada en proceso · TypeScript | Llamada en proceso | app-captura/src/ (módulos ES) |
| Motor de reglas · instancia local → Almacén del dispositivo | N3-App | API de IndexedDB · lectura local | TCP/IP | app-captura/src/almacen.ts |
| Operario de campo → Interfaz de captura | N3-App | Pantalla táctil del celular · PWA en Chrome para Android | Pantalla táctil del celular | app-captura/README.md |
| Orquestador local → Almacén del dispositivo | N3-App | API de IndexedDB · lectura y escritura local | TCP/IP | app-captura/src/almacen.ts y repositorio.ts |
| Orquestador local → Captura de identificador físico | N3-App | Llamada en proceso · TypeScript | Llamada en proceso | app-captura/src/ (módulos ES) |
| Orquestador local → Motor de reglas · instancia local | N3-App | Llamada en proceso · TypeScript | Llamada en proceso | app-captura/src/ (módulos ES) |
| Orquestador local → Verificación de permisos | N3-App | Llamada en proceso · TypeScript | Llamada en proceso | app-captura/src/ (módulos ES) |
| Servicio de cifrado del dispositivo → Almacén del dispositivo | N3-App | API de IndexedDB · llave AES-GCM de Web Crypto | Conexión local | app-captura/src/almacen.ts · plataforma web (Web Crypto) |
| Servicio de sincronización · cliente → API Gateway de la finca | N3-App | HTTPS (TLS 1.3) · REST/JSON en red local · reanudable e idempotente | HTTPS en red local · reanudable e idempotente | app-captura/src/sincronizacion.ts · ADR-002 · ADR-027 |
| Servicio de sincronización · cliente → Almacén del dispositivo | N3-App | API de IndexedDB · lectura y escritura local | Lectura y escritura local | app-captura/src/sincronizacion.ts y almacen.ts |
| Verificación de permisos → Almacén del dispositivo | N3-App | API de IndexedDB · lectura local | TCP/IP | app-captura/src/almacen.ts |
| Aplicación de captura → WAF de la finca | N3-GW | HTTPS (TLS 1.3) · REST/JSON en red local · reanudable | HTTPS en red local · reanudable | app-captura/src/sincronizacion.ts · ADR-002 · CN-28 |
| Aplicación web del sistema → WAF de la finca | N3-GW | HTTPS (TLS 1.3) · REST/JSON en red local | HTTPS/JSON en red local | CN-28 · SPK-18 (REST) |
| Herramienta de análisis de la empresa → WAF de la finca | N3-GW | HTTPS (TLS 1.3) · OData v4 JSON en red local · solo lectura | HTTPS/JSON en red local · solo lectura | SPK-16 · CN-10 · consulta rápida Q4 |
| Servicio de API Gateway → Aplicación web del sistema | N3-GW | HTTPS (TLS 1.3) · contenido estático servido por NGINX | HTTPS · contenido estático | Consulta rápida Q1 |
| Servicio de API Gateway → Backend de la finca | N3-GW | HTTP/1.1 interno · REST/JSON | HTTP interno de la finca | BB-06 · BB-14 (Compose) · consulta rápida Q1 |
| WAF de la finca → Servicio de API Gateway | N3-GW | Llamada en proceso · módulo ModSecurity de NGINX | Llamada en proceso | Consulta rápida Q1 |
| API Gateway de la finca → Ingreso de sincronización | N3-Ingreso | HTTP/1.1 interno · REST/JSON | HTTP interno de la finca | BB-06 · BB-14 (Compose) · consulta rápida Q1 |
| Aplicación de captura → API Gateway de la finca | N3-Ingreso | HTTPS (TLS 1.3) · REST/JSON en red local · reanudable e idempotente | HTTPS en red local · reanudable e idempotente | app-captura/src/sincronizacion.ts · ADR-002 · ADR-027 |
| Ingreso de sincronización → Base de datos de la finca | N3-Ingreso | SQL por JDBC · inserción idempotente por UUID v7 | SQL · inserción idempotente por identificador | BB-03 · SPK-17 (pgjdbc) · SPK-18 · ADR-027 |
| Ingreso de sincronización → Bitácora de auditoría | N3-Ingreso | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Ingreso de sincronización → Caché de producciones activas | N3-Ingreso | Llamada en proceso · Caffeine | Llamada en proceso | SPK-18 |
| Ingreso de sincronización → Ciclo de producción | N3-Ingreso | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Ingreso de sincronización → Datos maestros y parametrización versionada | N3-Ingreso | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Ingreso de sincronización → Emisión de credenciales | N3-Ingreso | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Ingreso de sincronización → Gestión de dispositivos | N3-Ingreso | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Ingreso de sincronización → Motor de proyección | N3-Ingreso | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Ingreso de sincronización → Motor de reglas · instancia servidor | N3-Ingreso | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Ingreso de sincronización → Servicio de notificación | N3-Ingreso | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Motor de reglas · instancia servidor → Datos maestros y parametrización versionada | N3-Ingreso | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| API Gateway de la finca → Ciclo de producción | N3-Ciclo | HTTP/1.1 interno · REST/JSON | HTTP interno de la finca | BB-06 · BB-14 (Compose) · consulta rápida Q1 |
| API Gateway de la finca → Consulta y tableros | N3-Ciclo | HTTP/1.1 interno · REST/JSON | HTTP interno de la finca | BB-06 · BB-14 (Compose) · consulta rápida Q1 |
| API Gateway de la finca → Interfaz de salida de datos | N3-Ciclo | HTTP/1.1 interno · OData v4 | HTTP interno de la finca | SPK-16 · consulta rápida Q1 y Q4 |
| Bitácora de auditoría → Base de datos de la finca | N3-Ciclo | SQL por JDBC · solo inserción | SQL · solo inserción | BB-03 · SPK-17 (pgjdbc) · SPK-18 · ADR-004 |
| Ciclo de producción → Base de datos de la finca | N3-Ciclo | SQL por JDBC | SQL | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Ciclo de producción → Bitácora de auditoría | N3-Ciclo | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Ciclo de producción → Caché de producciones activas | N3-Ciclo | Llamada en proceso · Caffeine | Llamada en proceso | SPK-18 |
| Ciclo de producción → Motor de proyección | N3-Ciclo | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Ciclo de producción → Servicio de notificación | N3-Ciclo | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Consulta y tableros → Base de datos de la finca | N3-Ciclo | SQL por JDBC · solo lectura | SQL · solo lectura | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Consulta y tableros → Caché de producciones activas | N3-Ciclo | Llamada en proceso · Caffeine | Llamada en proceso | SPK-18 |
| Consulta y tableros → Generador de documentos | N3-Ciclo | Cola Quartz en PostgreSQL | Cola de trabajos en la base de datos | ADR-009 · ADR-013 (en segundo plano) · SPK-18 |
| Generador de documentos → Base de datos de la finca | N3-Ciclo | SQL por JDBC · solo lectura | SQL · solo lectura | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Generador de documentos → Bitácora de auditoría | N3-Ciclo | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Interfaz de salida de datos → Base de datos de la finca | N3-Ciclo | SQL por JDBC · solo lectura | SQL · solo lectura | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Motor de proyección → Base de datos de la finca | N3-Ciclo | SQL por JDBC | SQL | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Motor de proyección → Datos maestros y parametrización versionada | N3-Ciclo | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Planificador de tareas → Motor de proyección | N3-Ciclo | Cola Quartz en PostgreSQL | Cola de trabajos en la base de datos | ADR-009 · SPK-18 (Quartz JDBC) |
| Servicio de notificación → Base de datos de la finca | N3-Ciclo | SQL por JDBC | SQL | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| API Gateway de la finca → Datos maestros y parametrización versionada | N3-Identidad | HTTP/1.1 interno · REST/JSON | HTTP interno de la finca | BB-06 · BB-14 (Compose) · consulta rápida Q1 |
| API Gateway de la finca → Gestión de dispositivos | N3-Identidad | HTTP/1.1 interno · REST/JSON | HTTP interno de la finca | BB-06 · BB-14 (Compose) · consulta rápida Q1 |
| API Gateway de la finca → Identidad y permisos | N3-Identidad | HTTP/1.1 interno · auth_request | HTTP interno de la finca | Consulta rápida Q1 · modelo |
| API Gateway de la finca → Identidad y permisos | N3-Identidad | HTTP/1.1 interno · REST/JSON | HTTP interno de la finca | BB-06 · BB-14 (Compose) · consulta rápida Q1 |
| Datos maestros y parametrización versionada → Base de datos de la finca | N3-Identidad | SQL por JDBC | SQL | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Datos maestros y parametrización versionada → Custodia de llaves | N3-Identidad | Llamada en proceso · JCA | Llamada en proceso | SPK-14 (ed25519) · modelo vigente |
| Emisión de credenciales → Custodia de llaves | N3-Identidad | Llamada en proceso · JCA | Llamada en proceso | SPK-14 (ed25519) · modelo vigente |
| Emisión de credenciales → Identidad y permisos | N3-Identidad | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Gestión de dispositivos → Base de datos de la finca | N3-Identidad | SQL por JDBC | SQL | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Identidad y permisos → Base de datos de la finca | N3-Identidad | SQL por JDBC | SQL | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Actualización y migraciones → Base de datos de la finca | N3-Operación | Flyway por JDBC | SQL | CN-29 · SPK-18 (Flyway) |
| Actualización y migraciones → Gestión de dispositivos | N3-Operación | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Actualización y migraciones → Servicio de respaldo | N3-Operación | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Actualización y migraciones → Servicios en línea | N3-Operación | HTTPS con TLS mutuo · artefacto firmado Ed25519, bajo demanda | HTTPS con TLS mutuo · artefacto firmado, bajo demanda | Modelo · SPK-14 (ed25519) · SPK-15 |
| Lectura del sistema heredado → Base de datos de la finca | N3-Operación | SQL por JDBC · carga inicial, una vez | SQL · carga inicial, una sola vez | SPK-18 (Spring Batch) |
| Lectura del sistema heredado → Sistema heredado de producción | N3-Operación | JDBC (UCanAccess) sobre el archivo de Access, por carpeta compartida SMB | JDBC sobre el archivo de Access, por carpeta compartida | Modelo · SPK-18 §4a |
| Observabilidad → Servicios en línea | N3-Operación | HTTPS con TLS mutuo · REST/JSON · métricas y estado | HTTPS con TLS mutuo · métricas y estado | BB-13 · SPK-18 (Micrometer) · modelo |
| Planificador de tareas → Servicio de respaldo | N3-Operación | Cola Quartz en PostgreSQL | Cola de trabajos en la base de datos | ADR-009 · SPK-18 (Quartz JDBC) |
| Servicio de respaldo → Base de datos de la finca | N3-Operación | Volcado con pg_dump | Volcado de la base | SPK-13 (pg_dump -Fc) |
| Servicio de respaldo → Caché de producciones activas | N3-Operación | Lectura del JSON cifrado · Jackson | Lectura del JSON cifrado | SPK-18 · SPK-13 |
| Servicio de respaldo → Custodia de llaves | N3-Operación | Llamada en proceso · JCA | Llamada en proceso | ADR-012 · modelo vigente |
| Servicio de respaldo → Servicios en línea | N3-Operación | HTTPS con TLS mutuo · restic · el archivo viaja cifrado | HTTPS con TLS mutuo · el archivo viaja cifrado | SPK-13 (restic) · modelo |
| Administrador del sistema en la finca → Bandeja de correcciones | N3-Web | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Administrador del sistema en la finca → Panel de administración | N3-Web | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Bandeja de correcciones → API Gateway de la finca | N3-Web | HTTPS (TLS 1.3) · REST/JSON en red local · con la cookie de sesión | HTTPS/JSON en red local · con la cookie de sesión | CN-28 · modelo |
| Bandeja de correcciones → Servicios de escritura | N3-Web | Llamada en el navegador · módulo TypeScript | Llamada en el navegador | Modelo (capa propia) |
| Bandeja de correcciones → Servicios de lectura | N3-Web | Llamada en el navegador · TanStack Query | Llamada en el navegador | Consulta rápida Q2 |
| Bandeja de correcciones → Sesión de usuario, roles y permisos | N3-Web | Llamada en el navegador · store Pinia | Llamada en el navegador | Consulta rápida Q2 |
| Exportación de archivos → Servicios de lectura | N3-Web | Llamada en el navegador · TanStack Query | Llamada en el navegador | Consulta rápida Q2 |
| Gerente de producción → Bandeja de correcciones | N3-Web | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Gerente de producción → Tableros de operaciones | N3-Web | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Gerente de producción → Vista de camas | N3-Web | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Gerente de producción → Vista de proyección y desviación | N3-Web | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Navegación detallada → Servicios de lectura | N3-Web | Llamada en el navegador · TanStack Query, carga progresiva | Llamada en el navegador · carga progresiva | Consulta rápida Q2 · ADR-023 |
| Panel de administración → Servicios de escritura | N3-Web | Llamada en el navegador · módulo TypeScript | Llamada en el navegador | Modelo (capa propia) |
| Panel de administración → Servicios de lectura | N3-Web | Llamada en el navegador · TanStack Query | Llamada en el navegador | Consulta rápida Q2 |
| Panel de administración → Sesión de usuario, roles y permisos | N3-Web | Llamada en el navegador · store Pinia | Llamada en el navegador | Consulta rápida Q2 |
| Personal de ventas → Vista de disponibilidad | N3-Web | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Personal de ventas → Vista de proyección y desviación | N3-Web | Navegador Microsoft Edge, en la oficina | Navegador, en la oficina | Nodo «Computador de oficina» (Windows 11 · Microsoft Edge) |
| Servicios de lectura → API Gateway de la finca | N3-Web | HTTPS (TLS 1.3) · REST/JSON en red local · con la cookie de sesión | HTTPS/JSON en red local · con la cookie de sesión | CN-28 · modelo |
| Sesión de usuario, roles y permisos → API Gateway de la finca | N3-Web | HTTPS (TLS 1.3) · REST/JSON en red local | HTTPS/JSON en red local | CN-28 · SPK-18 (REST) |
| Tableros de operaciones → Servicios de lectura | N3-Web | Llamada en el navegador · TanStack Query | Llamada en el navegador | Consulta rápida Q2 |
| Vista de camas → Servicios de lectura | N3-Web | Llamada en el navegador · TanStack Query | Llamada en el navegador | Consulta rápida Q2 |
| Vista de disponibilidad → Servicios de lectura | N3-Web | Llamada en el navegador · TanStack Query | Llamada en el navegador | Consulta rápida Q2 |
| Vista de proyección y desviación → Exportación de archivos | N3-Web | Llamada en el navegador · componente Vue | Llamada en el navegador | Consulta rápida Q2 |
| Vista de proyección y desviación → Navegación detallada | N3-Web | Vue Router · navegación dentro de la aplicación | Navegación dentro de la aplicación | Consulta rápida Q2 |
| Backend de la finca → Control de borde | N3-Nube | HTTPS con TLS mutuo por internet · REST · reintenta si la nube no responde | HTTPS con TLS mutuo, por internet · reintenta si la nube no responde | Modelo · SPK-13 (restic) · BB-13 |
| Control de borde → Distribución de versiones | N3-Nube | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Control de borde → Identidad y suscripción | N3-Nube | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Control de borde → Recepción de telemetría | N3-Nube | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Control de borde → Recepción y entrega de respaldos | N3-Nube | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Distribución de versiones → Identidad y suscripción | N3-Nube | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Identidad y suscripción → Base de datos de los servicios en línea | N3-Nube | SQL por JDBC | SQL | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Integración continua del equipo → Control de borde | N3-Nube | HTTPS (TLS 1.3) · REST · carga del artefacto firmado | HTTPS | Modelo · SPK-15 |
| Operador de la plataforma → Panel del superadministrador | N3-Nube | Navegador web por HTTPS (TLS 1.3), desde fuera de la finca | Navegador, desde fuera de la finca | CN-28 (cifrado en tránsito) |
| Panel del superadministrador → Identidad y suscripción | N3-Nube | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Panel del superadministrador → Recepción de telemetría | N3-Nube | Llamada en proceso · Spring | Llamada en proceso | Criterio del encargo (componentes Spring en un mismo proceso) · SPK-18 |
| Recepción de telemetría → Base de datos de los servicios en línea | N3-Nube | SQL por JDBC | SQL | BB-03 · SPK-17 (pgjdbc) · SPK-18 |
| Recepción y entrega de respaldos → Custodia de respaldos | N3-Nube | HTTPS · API S3 (AWS SDK for Java) · entra y sale cifrado | API de almacenamiento de objetos · entra y sale cifrado | Consulta rápida Q3 · modelo |
| Celular de campo → Servidor de la finca | Despliegue | Wi-Fi de la oficina (IEEE 802.11) · TCP/IP | Wi-Fi de la oficina | CN-17 (hay red en la oficina) |

## 3 · Referencias retiradas del diagrama

**En la versión de trabajo no había ningún código de documento.** Se buscó
`\b(ADR|SPK|CN|CT|ESC|BB|RT|PoC|T)-?\d+\b` y las marcas `[!]`, «revisar», «propuesta» y «por definir» en
todos los atributos de texto de las 11 páginas (`c4Name`, `c4Type`, `c4Description`, `c4Technology`,
`c4Application`, `label` y `value`). Ninguna `c4Description` se modificó.

La única marca encontrada no era un código sino una alternativa sin decidir, y desapareció al fijar la
tecnología:

| Página | Elemento | Atributo | Texto original | Texto limpio |
|---|---|---|---|---|
| N3-GW | WAF de la finca | `c4Technology` | Firewall de aplicaciones web · reglas OWASP (propuesta) | ModSecurity v3 · OWASP CRS 4 |

`[!]` Los ejemplos del encargo (`Java ADR-037`, `Identity Provider local CN-022`, `CT-05 · Entrega los
datos`, un «Key Vault»…) corresponden a la versión comprometida en git (`8b4d11b`, 14 páginas), que sí tenía
códigos como `ADR-034`, `CN-28` y `ESC-29`. La versión de trabajo del 23-sep ya había quitado esas páginas y
esos códigos antes de esta edición.

## 4 · Elegidas por consulta rápida

Cuatro consultas, una por grupo de componentes que las fuentes no resolvían. Cada una pidió dos o tres
opciones con el criterio del encargo: funciona en la finca sin internet, licencia libre o gratuita, madura y
mantenida, y lo que mejor encaja con Java y Spring.

### Q1 · API Gateway de la finca

Toca el contenedor, «WAF de la finca», «Servicio de API Gateway» y las relaciones que dependen del proxy.
`BB-06` pedía un proxy inverso con TLS y límite de tasa dentro de la finca, sin nombrar producto.

| Opción | Por qué sí / por qué no |
|---|---|
| **NGINX + ModSecurity v3 + OWASP CRS 4** | Imagen oficial `owasp/modsecurity-crs` con NGINX. ModSecurity pasó a OWASP en 2024. NGINX trae TLS 1.3, `limit_req`, `auth_request` y archivos estáticos sin plugins de terceros |
| NGINX + Coraza + OWASP CRS 4 | Coraza es el WAF de OWASP en Go y soporta CRS 4, pero su conector para NGINX sigue marcado como experimental |
| Caddy + coraza-caddy + OWASP CRS | Configuración más simple, pero el límite de tasa exige el plugin de terceros `caddy-ratelimit` |

**Elegida: NGINX + ModSecurity v3 + OWASP CRS 4.** Cubre los seis requisitos del gateway sin plugins de
terceros y tiene más años en producción. Además, deja el WAF en el mismo proceso que el proxy, que es lo
que dice la relación «WAF → Servicio de API Gateway: llamada en proceso». La verificación de cada petición
contra el backend se hace con `auth_request`.

### Q2 · Aplicación web del sistema

| Grupo | Opciones |
|---|---|
| Framework y construcción | **Vue 3 + Vite** (curva más baja, TypeScript de primera) · React + Vite (ecosistema mayor, más aprendizaje) · Svelte 5 + Vite (menos bibliotecas de gráficos y tablas) |
| Gráficos | **Apache ECharts** (series temporales y bandas de tolerancia nativas) · Chart.js (bandas con plugin) · uPlot (rápido pero de bajo nivel) |
| Tablas | **TanStack Table** (sin interfaz propia, con carga progresiva) · AG Grid Community · PrimeVue DataTable (ata la tabla al framework) |
| Caché de consultas | **TanStack Query** (agnóstica, adaptador para Vue) · SWR (solo React de forma oficial) |

**Elegido: Vue 3 + Vite, Apache ECharts (vía `vue-echarts`), TanStack Table y TanStack Query.** Es la curva
más baja para dos personas. ECharts y TanStack tienen un núcleo que no depende del framework, así que la
lógica de consulta y los tipos quedan en TypeScript puro, reutilizable con la PWA, como pide `CN-18`. Para
el rol en sesión y la navegación se usan las piezas oficiales de Vue: Pinia y Vue Router.

### Q3 · Servicios en línea

| Grupo | Opciones |
|---|---|
| Servidor virtual | **Hetzner Cloud** (el más barato, maduro, tráfico incluido) · DigitalOcean (dos o tres veces más caro, consola más amigable) |
| Almacenamiento de objetos | **Backblaze B2** (barato, Object Lock real por la API S3) · Wasabi (mínimo facturable de 1 TB). Cloudflare R2 quedó fuera: no expone Object Lock por la API S3 |
| Base de datos | **PostgreSQL 16 en la misma VM** (costo cero, misma pila que la finca) · DigitalOcean Managed Postgres (un costo fijo nuevo) |

**Elegidos: Hetzner Cloud, Backblaze B2 y PostgreSQL 16 en la misma VM**, con el servicio en **Spring Boot
4.1** para que el equipo use la misma pila que en la finca. Ninguno cobra por finca ni por usuario (`CN-35`).
Dentro de Spring, el control de borde usa Spring Security con certificados X.509 (TLS mutuo) y Bucket4j
para el límite de tasa, y el panel usa Spring MVC con Thymeleaf. La nube habla con B2 por la API S3 con el
AWS SDK for Java.

### Q4 · Interfaz de salida de datos

`SPK-16` dejó abiertos dos caminos que rinden igual y cierran la base: B (REST propio) y C (OData v4, con
conector nativo en Power BI). `CN-10` lleva la nota literal «POWER BI». Se tomó C. OData v4 también es REST
sobre JSON, así que no contradice el «API REST · solo lectura» que ya tenía el componente. Solo faltaba la
biblioteca:

| Opción | Por qué sí / por qué no |
|---|---|
| **Apache Olingo 5.0 (OData v4)** | La única biblioteca OData v4 madura en Java; ya migrada a Jakarta; se monta como servlet dentro de Spring Boot. La paginación `@odata.nextLink` se programa a mano |
| SAP `olingo-jpa-processor-v4` sobre Olingo | Genera metadatos y consultas desde entidades JPA de solo lectura (las vistas); mantenido por SAP |
| OData4j · Olingo OData v2 | No viables: solo OData v2 y sin mantenimiento |

**Elegida: Apache Olingo**, con el procesador JPA de SAP como forma de implementarla. `[!]` Según la
consulta, la última versión estable de Olingo es de diciembre de 2023. Si al construir no convence, el
camino B de `SPK-16` (REST propio con Spring Web MVC) rinde igual y solo cambia la etiqueta del componente
y de las relaciones que llevan OData.

## 5 · Decisiones de forma

**Formas cortas en las relaciones que tocan un componente.** Las cuatro páginas del backend usan letra de
18 px en las flechas, y el ancho de cada etiqueta es fijo (142 a 180 px). Con el texto largo del ejemplo del
encargo («Llamada en proceso · inyección de dependencias Spring»), cada etiqueta crecía dos líneas y tapaba
cajas vecinas. Se comprobó exportando cada página a PNG antes y después. Para no mover nada, esas
relaciones usan una forma corta del mismo largo que el texto original:

| Familia | Forma corta (relaciones con un componente) | Forma completa (entre contenedores) |
|---|---|---|
| Llamada dentro del backend | `Llamada en proceso · Spring` (llamada directa entre beans de Spring, por inyección de dependencias) | — |
| Llamada dentro de la PWA | `Llamada en proceso · TypeScript` | — |
| Gateway → backend | `HTTP/1.1 interno · REST/JSON` | `HTTP/1.1 · REST/JSON sobre TCP/IP en red interna de Docker` |
| Backend → base de datos | `SQL por JDBC …` | `JDBC (driver PostgreSQL) sobre TCP/IP · puerto 5432` |

Con el mismo criterio se acortaron estas otras formas: `Cola Quartz en PostgreSQL`, `Volcado con pg_dump`,
`Flyway por JDBC`, `Llamada en proceso · Caffeine` y `Llamada en proceso · JCA`. La forma corta se usa en
toda relación que toca un componente, en cualquier página, para que se lea igual en todas. Las relaciones
entre dos contenedores (`N2`, `N3-App` y `Despliegue`) llevan la forma completa.

**Subtítulo del límite del gateway.** En `N3-GW` dice `Container: NGINX con WAF`, no la tecnología completa
del contenedor, porque la etiqueta «Consulta y administra» tapaba el texto largo. El detalle (ModSecurity v3
y OWASP CRS 4) está en el componente «WAF de la finca» de esa misma página y en el contenedor en las demás.

**«Fetch API · outbox UUID v7».** Es la forma de una línea de la tecnología del cliente de sincronización.
La versión de dos líneas desbordaba la caja por 5 px.

**Solapes que ya estaban.** Algunas etiquetas ya tapaban parte de una caja antes de esta edición, por
ejemplo:

- «Obtiene la configuración vigente para el celular» sobre la base de datos (`N3-Ingreso`);
- «Lee la bitácora para el reporte de auditoría» sobre la bitácora (`N3-Ciclo`);
- «Dispara el respaldo y su prueba de restauración» sobre el servicio de respaldo (`N3-Operación`);
- «Revisa a diario la bandeja de correcciones», «Pide los usuarios y el formato vigentes» y «Pide la flor
  disponible» sobre cajas vecinas (`N3-Web`).

Siguen igual. Corregirlos exige mover etiquetas, y el encargo pide no mover nada.

## 6 · Observaciones sin tocar

- **O-01 · Custodia de llaves.** El encargo pone «Key Vault → HashiCorp Vault» como ejemplo de servicio
  existente. En la versión vigente del modelo, «Custodia de llaves» es un componente dentro del backend, con
  tecnología «Almacén de llaves en Java» y tres relaciones «llamada en proceso». El «Key Vault» con
  «HashiCorp Vault u OpenBao autoalojado (propuesta)» solo existe en la versión comprometida en git. Se
  respetó la definición vigente (Java KeyStore PKCS#12, desarrollo propio). Para pasar a Vault u OpenBao
  habría que convertir el componente en contenedor y cambiar las tres relaciones a
  `HTTPS · API REST de Vault (Spring Cloud Vault)`. Relacionado: `BB-12` ubica la custodia de claves en la
  nube, y `SPK-13` no pudo medir Vault ni OpenBao.
- **O-02 · Salida al BI.** `ADR-013` (Aceptada) dice «conexión de solo lectura contra las tablas de consulta,
  con un usuario de base dedicado». El modelo lleva el BI por el API Gateway a una API de solo lectura, que
  es lo que pide `ESC-29` (0 accesos directos a la base) y lo que `SPK-16` recomienda. Son dos definiciones
  distintas.
- **O-03 · REST u OData.** `SPK-16` dejó la elección entre REST propio y OData como decisión pendiente
  (hoja `Para-ADR`, fila 19). Aquí se tomó OData v4 (§4, Q4); falta el ADR.
- **O-04 · Cifrado en el celular.** El modelo dice «base de datos local cifrada» y «obtiene del sistema la
  llave». En una PWA no hay acceso al almacén de llaves de Android: la llave es una `CryptoKey` AES-GCM no
  exportable de Web Crypto, guardada por el navegador. `app-captura/` todavía no cifra su IndexedDB, y
  `ADR-008` pone «que el cliente exija demostrar el cifrado» como disparador para pasar a una aplicación
  instalada.
- **O-05 · El prototipo no sincroniza por red.** En `app-captura/src/sincronizacion.ts` el «servidor» vive
  en el mismo dispositivo, y el comentario habla de un backend FastAPI + PostgreSQL que `ADR-037` ya
  reemplazó por Java. El choque lo resuelve una persona (`DEC-05`), mientras `CN-24`, `ADR-027` y `ADR-031`
  dicen que se resuelve solo. `ADR-027` ya anota que el prototipo lo contradice.
- **O-06 · El armazón no está medido.** `SPK-18` (Spring Boot 4.1 contra Quarkus 3.33) solo tiene la
  pregunta. Se usó Spring Boot por el criterio del encargo. Si el spike recomendara Quarkus, cambian todas las
  piezas «Spring …»: Spring MVC por Quarkus REST, Spring Data JPA por Panache, Quartz por `quarkus-quartz`,
  Spring Security + Nimbus por `smallrye-jwt`, Caffeine por Quarkus Cache, Actuator por SmallRye Health y
  Spring Batch por `quarkus-jberet`. `SPK-18` también podría sacar la carga del heredado del backend (su
  «forma B»), lo que movería el componente «Lectura del sistema heredado».
- **O-07 · Sistemas externos con tecnología dispar.** En `N1`, la herramienta de análisis dice `Power BI` y el
  heredado `Microsoft Access`; en las demás páginas no tienen tecnología. No se tocó, por encargo.
- **O-08 · El heredado pesa 250 GB.** Un `.accdb` no pasa de 2 GB (`SPK-18`, compuerta 0). UCanAccess copia
  el archivo entero a memoria o a disco al conectarse, y `SPK-18` evalúa Jackcess directo. Se dejó UCanAccess
  porque es lo que dice el modelo.
- **O-09 · Llave de restic.** `restic` cifra el repositorio con una contraseña simétrica. `SPK-13` midió
  además `age` con X25519 para la llave de la empresa. No está decidido cuál es «la llave de la finca» ni cómo
  se rota (`AB-04`).
- **O-10 · Caché en propuesta.** `ADR-034` sigue en «Propuesta» y no decide qué entra al caché. La tecnología
  del componente (Caffeine, con volcado JSON en el volumen LUKS) supone ese caché dentro del backend.
- **O-11 · Nube sin CDN ni WAF.** El bloque `BB-07`/`BB-08` proponía un CDN con WAF gestionado para la nube.
  El modelo vigente resuelve el borde con «Control de borde» dentro del proceso, sin CDN.
- **O-12 · El .drawio estaba congelado para la IA.** `IA_CONTEXTO-3-BACKEND.md` §5 y la fila 12 de
  `Para-ADR` dicen que la IA no toca el `.drawio`. Esta edición la pidió Jerónimo de forma explícita el
  23-sep-2026.

## 7 · Validación

Script en el scratchpad de la sesión, que compara el archivo nuevo contra el respaldo:

| Comprobación | Resultado |
|---|---|
| El XML es válido y draw.io lo abre y exporta | Sí: las 11 páginas se exportaron a PNG con el CLI de draw.io |
| Mismo número de páginas | 11 y 11 |
| Mismos objetos C4 por tipo | 11 títulos · 15 personas · 1 sistema · 12 externos · 9 límites · 39 contenedores · 59 componentes · 5 nodos · 157 relaciones |
| Celdas nuevas | 77, todas de las 11 leyendas |
| Geometría | Ninguna `mxGeometry` existente cambió |
| Estilos | Solo cambian `fillColor` y `strokeColor`, en 28 contenedores y componentes |
| `c4Name`, `c4Type`, `label`, origen y destino de cada flecha | Sin cambios |
| `c4Description` modificadas | 0 |
| `c4Technology` o `c4Application` con «propuesta», « o » o «por definir» | 0 |
| Textos que coinciden con el patrón de códigos | 0 |
| Texto de cajas que desborda por la tecnología nueva (métricas de Arial) | 0 |
| Formato del archivo | Mismo formato de draw.io: sangría, comillas y `" />` intactos; la diferencia con el respaldo son solo las líneas cambiadas y las leyendas |
