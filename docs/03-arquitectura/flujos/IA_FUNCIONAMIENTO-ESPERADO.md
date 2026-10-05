REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · orquestador de la revisión del modelo C4 · 4-oct-2026
Estado de revisión: SIN REVISAR
Manda por encima: el modelo (Arquitectura FlorLogic.drawio, sus 7 páginas «(copia con cambios)»), ADR.xlsx (hoja ADR) y DRIVERS_ARQUITECTONICOS.md con sus cuatro .xlsx
Zona IA ampliada por Juan el 4-oct-2026: docs/03-arquitectura/flujos/ es zona IA.

# Funcionamiento esperado de FlorLogic, según el modelo C4

**Para qué sirve.** Es la base para dibujar diagramas de flujo precisos (Mermaid) de lo que debe hacer
el sistema.

**De dónde sale.** Solo de dos cosas:
- los textos de las 7 páginas «(copia con cambios)» de `Arquitectura FlorLogic.drawio` (commit
  `3d9d57f`, con el mismo texto que `0db8dd8`, del 22-sep);
- `decisiones-de-juan.md`, que hoy está vacío.

Aquí no entra ningún ADR, requisito ni escenario. Por eso A1, A2, A3 y A4 no lo usan para juzgar el
modelo: sería circular.

**Reglas.**
- Lo que el modelo no fija queda **PENDIENTE**, con la pregunta que lo resolvería.
- Cuando el modelo no da el orden entre dos pasos, se escribe en el orden de lectura y se marca
  PENDIENTE.
- Todo lo que dice «PENDIENTE» es una pregunta, no una propuesta.

**Cómo se cita la fuente.** Página y id de drawio. Todas las páginas son «(copia con cambios)»:
- N1: Nivel 1 — Contexto
- N2: Nivel 2 — Contenedores
- N3-cap: Nivel 3 — App de captura
- N3-back: Nivel 3 — Backend de la finca
- N3-web: Nivel 3 — Aplicación web del sistema
- N3-nube: Nivel 3 — Servicios en línea
- Dep: Despliegue

**Fronteras.**
- {dentro del celular}
- {red local de la finca}: también lo que corre dentro del servidor de la finca o en el navegador del
  computador de oficina.
- {internet}: también lo que corre dentro de la nube.

**Equivalencia con Mermaid.**
- Cada paso es una flecha de un `sequenceDiagram`, y cada condición es un bloque `alt` u `opt`.
- Cada PENDIENTE va como `Note`.
- Cada transición es una línea de un `stateDiagram-v2`.
- Un flujo es un diagrama; no se juntan dos flujos en uno.
- En esta fase no se dibuja nada.

---

## 1 · Contenedores

### Aplicación de captura
- **Qué hace:** Registra y corrige la producción en el invernadero. Valida antes de guardar y funciona sin red. — N2 {c4n2-c-cap-cc}
- **Qué recibe:**
  - del Operario de campo, la captura y la corrección, en el invernadero, sin red y con guantes — N2 {c4n2-rel3-cc}, N3-cap {3QFZ-2BZZ4g53YUmzgdM-1-cc};
  - del API Gateway de la finca, al sincronizar, el resultado de las capturas, la credencial, la configuración vigente y la versión nueva de la aplicación — N3-cap {c4n3a-rel15-cc}.
- **Qué entrega:**
  - al API Gateway de la finca, las capturas pendientes, al sincronizar al volver a la oficina — N3-cap {c4n3a-rel15-cc}, {c4n3a-ttl1-cc};
  - al Almacén del dispositivo, lo aceptado — N3-cap {c4n3a-rel8-cc}.
- **Qué guarda:** nada propio. Guarda en el Almacén del dispositivo — N2 {c4n2-rel8-cc}.
- **Dónde corre:** Celular de campo — Dep {c4dep-c-cap-cc}.

### Almacén del dispositivo
- **Qué hace:** Guarda en el celular las capturas pendientes y la configuración vigente. — N2 {c4n2-c-loc-cc}
- **Qué recibe:** de la aplicación de captura, las capturas aceptadas y la respuesta del servidor — N3-cap {c4n3a-rel8-cc}, {c4n3a-rel14-cc}.
- **Qué entrega:**
  - el formato activo y las capturas del día, al Orquestador local — N3-cap {c4n3a-rel8-cc};
  - las reglas de la configuración vigente, al Motor de reglas · instancia local — {c4n3a-rel11-cc};
  - la credencial vigente, a Verificación de permisos — {c4n3a-rel-per-loc-cc};
  - las capturas pendientes, al Servicio de sincronización · cliente — {c4n3a-rel14-cc}.
- **Qué guarda:** las capturas pendientes, la configuración vigente (reglas y formato de captura) y la credencial vigente — N2 {c4n2-c-loc-cc}, N3-cap {c4n3a-rel11-cc}, {c4n3a-rel-per-loc-cc}. Está cifrado y se abre con la llave del sistema al iniciar la aplicación — N3-cap {c4n3a-rel13-cc}, {c4n3a-k-cap-cif-cc}.
- **Dónde corre:** Celular de campo — Dep {c4dep-c-loc-cc}.
- PENDIENTE: ¿guarda las camas asignadas al celular? El modelo no lo dice.

### API Gateway de la finca
- **Qué hace:** Único punto de entrada a la finca. Sirve la aplicación web y enruta el resto al backend. — N2 {c4n2-c-gw-cc}
- **Qué recibe:**
  - del celular, las sincronizaciones — N2 {c4n2-rel9-cc};
  - de la aplicación web, las consultas y la administración — N2 {c4n2-rel17-cc};
  - de la herramienta de análisis, las lecturas — N2 {c4n2-rel14-cc};
  - el inicio y el cierre de sesión — N3-web {c4n3c-rel-ses-gw-cc}.
- **Qué entrega:**
  - la aplicación, al navegador — N2 {c4n2-rel13-cc};
  - cada petición a Identidad y permisos, para verificarla — N3-back {c4n3b-rel19-cc};
  - cada petición al componente del backend que le toca — N3-back {c4n3b-rel33-cc}, {c4n3b-rel34-cc}, {c4n3b-rel-gw-sal-cc}, {c4n3b-rel-gw-cfg-cc}, {c4n3b-rel-gw-ciclo-cc}, {c4n3b-rel-gw-dis-cc}, {c4n3b-rel-gw-iam2-cc}.
- **Qué guarda:** nada, según el modelo.
- **Dónde corre:** Servidor de la finca — Dep {c4dep-c-gw-cc}.

### Backend de la finca
- **Qué hace:** Autoridad del dato operativo: ingesta, reglas, correcciones, proyección, desviación, disponibilidad y consulta. — N2 {c4n2-c-back-cc}
- **Qué recibe:**
  - del API Gateway, las peticiones enrutadas — N2 {c4n2-rel10-cc};
  - del sistema heredado, el histórico, en la carga inicial — N2 {5mauoWmsDpcP75MK4mOA-2-cc};
  - de los servicios en línea, bajo demanda, la versión nueva y el respaldo para restaurar — N2 {c4n2-rel18-cc}, {c4n2-rel23-cc}.
- **Qué entrega:**
  - a la Base de datos de la finca, lectura y escritura del dato operativo — N2 {c4n2-rel11-cc};
  - a los servicios en línea, el respaldo cifrado y la telemetría — N2 {c4n2-rel15-cc}.
- **Qué guarda:** nada en disco propio. Mantiene en memoria el Caché de producciones activas — N3-back {c4n3b-c-cache-cc}.
- **Dónde corre:** Servidor de la finca — Dep {c4dep-c-back-cc}.

### Base de datos de la finca
- **Qué hace y qué guarda:** guarda la producción, la proyección, la configuración, la bitácora y el histórico de la finca — N2 {c4n2-c-db-cc}. Según las flechas de N3-back, además:
  - usuarios, roles y permisos — {c4n3b-rel-iam-db-cc};
  - los celulares, sus camas y su versión — {c4n3b-rel-dis-db-cc};
  - los avisos pendientes — {c4n3b-rel-not-db-cc};
  - las correcciones, los cierres y los cambios confirmados — {c4n3b-rel-ciclo-db-cc};
  - cada versión de la configuración — {y2uk7SS4gnAXLxvLYaCL-1-cc};
  - la cola de trabajos — {c4n3b-rel10-cc}, {c4n3b-rel11-cc}, {c4n3b-rel13-cc} («Cola de trabajos en la base de datos»).
- **Qué recibe y qué entrega:** solo con el backend — N2 {c4n2-rel11-cc}.
- **Dónde corre:** Servidor de la finca, que tiene el disco cifrado — Dep {c4dep-c-db-cc}, {c4dep-d-n2-cc}.

### Aplicación web del sistema
- **Qué hace:** Consulta de proyección, desviación, disponibilidad y tableros; resolución de correcciones y administración del sistema. — N2 {c4n2-c-web-cc}. No guarda datos propios. — N3-web {c4n3c-ttl1-cc}
- **Qué recibe:**
  - del API Gateway, la aplicación y las respuestas — N2 {c4n2-rel13-cc};
  - del gerente, de ventas y del administrador, sus acciones — N2 {c4n2-rel4-cc}, {c4n2-rel5-cc}, {c4n2-rel6-cc}.
- **Qué entrega:** al API Gateway, consultas y cambios, con la cookie de sesión — N3-web {c4n3c-rel16-cc}, {c4n3c-rel17-cc}.
- **Qué guarda:** nada propio. Usa un caché de consultas y la cookie de sesión en el navegador — N3-web {c4n3c-k-w-lec-cc}, {c4n3c-k-w-ses-cc}.
- **Dónde corre:** Computador de oficina — Dep {c4dep-c-web-cc}.

### Servicios en línea
- **Qué hace:** Respaldo, distribución de versiones y diagnóstico sobre el estado que la finca reporta. Si se cae, la finca sigue operando. — N2 {c4n2-c-nube-cc}
- **Qué recibe:**
  - del backend, el respaldo cifrado y la telemetría, y las peticiones de versión y de respaldo para restaurar — N2 {c4n2-rel15-cc}, {c4n2-rel18-cc}, {c4n2-rel23-cc};
  - del Operador de la plataforma, mantenimiento y diagnóstico — N2 {c4n2-rel7-cc};
  - de la integración continua del equipo, cada versión firmada — N3-nube {c4n3d-rel14-cc}.
- **Qué entrega:**
  - al backend, la versión nueva si la suscripción de la finca está vigente — N3-nube {c4n3d-k-n-ver-cc};
  - al backend, el respaldo intacto para restaurar — N3-nube {c4n3d-k-n-res-cc};
  - a la Custodia de respaldos, el respaldo cifrado — N2 {c4n2-rel19-cc}.
- **Qué guarda:** en su base de datos, el estado que reporta la finca, las cuentas de operador y la suscripción — N2 {c4n2-rel-nubedb-cc}.
- **Dónde corre:** Infraestructura en la nube — Dep {c4dep-c-nube-cc}.

### Custodia de respaldos
- **Qué hace y qué guarda:** guarda los respaldos ya cifrados con la llave de la finca. No puede descifrarlos. — N2 {c4n2-c-obj-cc}
- **Qué recibe y qué entrega:** solo con Servicios en línea — N2 {c4n2-rel19-cc}.
- **Dónde corre:** Infraestructura en la nube — Dep {c4dep-c-obj-cc}.

### Base de datos de los servicios en línea
- **Qué hace y qué guarda:** guarda el estado que reporta la finca, las cuentas de operador y la suscripción. — N2 {c4n2-c-nubedb-cc}
- **Qué recibe y qué entrega:** solo con Servicios en línea — N2 {c4n2-rel-nubedb-cc}.
- **Dónde corre:** Infraestructura en la nube — Dep {c4dep-c-nubedb-cc}.

### Sistemas externos
- **Herramienta de análisis de la empresa (Power BI):** lee los datos de producción por el API Gateway, en solo lectura y en la red local — N1 {c4n1-s-bi-cc}, N2 {c4n2-rel14-cc}. Corre en el Computador de oficina — Dep {c4dep-s-bi-cc}.
- **Sistema heredado de producción (Microsoft Access):** entrega el histórico para la carga inicial — N2 {5mauoWmsDpcP75MK4mOA-2-cc}. Está en un computador con Windows que comparte el archivo en una carpeta — Dep {c4dep-d-leg-cc}.
- **Integración continua del equipo:** construye, firma y publica cada versión del sistema — N3-nube {c4n3d-k-n-cicd-cc}.

---

## 2 · Los once flujos

### Flujo 1 · Preparar la jornada

**Disparador:** PENDIENTE. El modelo no dice qué inicia la preparación de una jornada ni cuándo.

**Participantes, en orden de aparición:** Administrador del sistema en la finca · Panel de administración · Servicios de escritura · API Gateway de la finca · Identidad y permisos · Datos maestros y parametrización versionada · Custodia de llaves · Base de datos de la finca · (quien asigna, PENDIENTE) · Gestión de dispositivos · Servicio de sincronización · cliente · Interfaz de captura · Verificación de permisos · Almacén del dispositivo

**Precondición:** el celular está registrado. PENDIENTE: el modelo no dice quién lo registra.

**Pasos, uno por mensaje:**

*opt: hay un formato de captura nuevo*
1. Administrador del sistema en la finca → Panel de administración: el formato de captura [cuando cambia] {red local de la finca} — N3-web {c4n3c-rel5-cc}
2. Panel de administración → Servicios de escritura: usuarios, permisos y formato de captura {red local de la finca} — N3-web {c4n3c-rel15-cc}
3. Servicios de escritura → API Gateway de la finca: los cambios, con la cookie de sesión {red local de la finca} — N3-web {c4n3c-rel17-cc}
4. API Gateway de la finca → Identidad y permisos: verifica la petición {red local de la finca} — N3-back {c4n3b-rel19-cc}
5. API Gateway de la finca → Datos maestros y parametrización versionada: la administración de la configuración {red local de la finca} — N3-back {c4n3b-rel-gw-cfg-cc}
6. Datos maestros y parametrización versionada → Custodia de llaves: firma la configuración vigente {red local de la finca} — N3-back {c4n3b-rel-cfg-kv-cc}
7. Datos maestros y parametrización versionada → Base de datos de la finca: la versión nueva de la configuración {red local de la finca} — N3-back {y2uk7SS4gnAXLxvLYaCL-1-cc}

*asignar camas a un celular*

8. (quien asigna) → API Gateway de la finca: registro de un celular y asignación de sus camas {red local de la finca} — N3-back {c4n3b-rel-gw-dis-cc}. PENDIENTE: ninguna persona ni pantalla de la aplicación web origina esta petición.
9. API Gateway de la finca → Identidad y permisos: verifica la petición {red local de la finca} — N3-back {c4n3b-rel19-cc}
10. API Gateway de la finca → Gestión de dispositivos: registro del celular y asignación de camas {red local de la finca} — N3-back {c4n3b-rel-gw-dis-cc}
11. Gestión de dispositivos → Base de datos de la finca: el celular, sus camas y su versión {red local de la finca} — N3-back {c4n3b-rel-dis-db-cc}

*el celular recibe lo que necesita (dentro de una sincronización, Flujo 3)*

12. Servicio de sincronización · cliente → API Gateway de la finca: sincroniza [al volver a la oficina] {red local de la finca} — N3-cap {c4n3a-rel15-cc}
13. API Gateway de la finca → Servicio de sincronización · cliente: la credencial, la configuración vigente y la versión nueva de la aplicación {red local de la finca} — N3-cap {c4n3a-rel15-cc}. Cómo se arman, en el Flujo 3, pasos 19 a 23.
14. Servicio de sincronización · cliente → Almacén del dispositivo: guarda la respuesta del servidor {dentro del celular} — N3-cap {c4n3a-rel14-cc}

*al iniciar la jornada*

15. Interfaz de captura → Verificación de permisos: verifica la credencial del operario [al iniciar la jornada] {dentro del celular} — N3-cap {c4n3a-rel3-cc}
16. Verificación de permisos → Almacén del dispositivo: lee la credencial vigente {dentro del celular} — N3-cap {c4n3a-rel-per-loc-cc}

**Fin (qué queda distinto después):**
- la configuración nueva quedó firmada y guardada con su versión;
- las camas del celular quedaron registradas en la finca;
- el celular tiene credencial y configuración vigentes, y el operario puede capturar sin red durante toda la jornada — N3-cap {c4n3a-k-cap-per-cc}.

**PENDIENTE:**
- ¿Quién asigna las camas a cada celular, y desde qué pantalla? La aplicación web no tiene componente para eso (N3-web).
- ¿Las camas asignadas bajan al celular? La flecha de la sincronización (N3-cap {c4n3a-rel15-cc}) no las nombra, y el almacén solo guarda «capturas pendientes y configuración vigente» (N2 {c4n2-c-loc-cc}).
- ¿El celular recibe su jornada antes de salir al invernadero, o en la sincronización con la que vuelve de la jornada anterior? El único intercambio que tiene el modelo es «al volver a la oficina» (N3-cap {c4n3a-ttl1-cc}).
- ¿Quién registra un celular nuevo?
- Paso 1: Datos maestros publica «el catálogo, las reglas y el formato de captura» (N3-back {c4n3b-k-b-cfg-cc}), pero el panel solo envía «formato de captura» (N3-web {c4n3c-rel15-cc}). ¿Desde dónde se cambian el catálogo y las reglas?
- ¿Qué es «la jornada»: el día, el turno o la salida al invernadero?

### Flujo 2 · Capturar

**Disparador:** el operario de campo empieza a capturar una cama en el invernadero, sin red — N3-cap {3QFZ-2BZZ4g53YUmzgdM-1-cc}

**Participantes, en orden de aparición:** Servicio de cifrado del dispositivo · Almacén del dispositivo · Operario de campo · Interfaz de captura · Verificación de permisos · Orquestador local · Captura de identificador físico · Motor de reglas · instancia local

**Precondición:** el celular tiene credencial y configuración vigentes (Flujo 1).

**Pasos, uno por mensaje** (todos {dentro del celular}):
1. Servicio de cifrado del dispositivo → Almacén del dispositivo: abre el almacén cifrado con la llave del sistema [al iniciar la aplicación] {dentro del celular} — N3-cap {c4n3a-rel13-cc}, {c4n3a-k-cap-cif-cc}
2. Operario de campo → Interfaz de captura: empieza la jornada {dentro del celular} — N3-cap {3QFZ-2BZZ4g53YUmzgdM-1-cc}
3. Interfaz de captura → Verificación de permisos: verifica la credencial del operario [al iniciar la jornada] {dentro del celular} — N3-cap {c4n3a-rel3-cc}
4. Verificación de permisos → Almacén del dispositivo: lee la credencial vigente {dentro del celular} — N3-cap {c4n3a-rel-per-loc-cc}
5. Interfaz de captura → Orquestador local: pide el formato activo {dentro del celular} — N3-cap {c4n3a-rel4-cc}
6. Orquestador local → Almacén del dispositivo: lee el formato activo y las capturas del día {dentro del celular} — N3-cap {c4n3a-rel8-cc}
7. Orquestador local → Captura de identificador físico: pide el código de la cama {dentro del celular} — N3-cap {c4n3a-rel5-cc}
8. Captura de identificador físico → Orquestador local: el código de la cama, leído con la cámara {dentro del celular} — N3-cap {c4n3a-k-cap-qr-cc}
9. Operario de campo → Interfaz de captura: captura o corrige los datos de la cama, con guantes {dentro del celular} — N3-cap {3QFZ-2BZZ4g53YUmzgdM-1-cc}
10. Interfaz de captura → Orquestador local: envía lo capturado {dentro del celular} — N3-cap {c4n3a-rel4-cc}
11. Orquestador local → Verificación de permisos: ¿el rol puede registrar la captura? {dentro del celular} — N3-cap {c4n3a-rel9-cc}
12. Orquestador local → Motor de reglas · instancia local: pide evaluar la captura {dentro del celular} — N3-cap {c4n3a-rel6-cc}
13. Motor de reglas · instancia local → Almacén del dispositivo: lee las reglas de la configuración vigente {dentro del celular} — N3-cap {c4n3a-rel11-cc}
14. Motor de reglas · instancia local → Orquestador local: el veredicto, con el motivo en lenguaje del negocio {dentro del celular} — N3-cap {c4n3a-rel6-cc}, {c4n3a-k-cap-reg-cc}

*alt: el veredicto acepta*

15. Orquestador local → Almacén del dispositivo: guarda lo aceptado [si el veredicto acepta] {dentro del celular} — N3-cap {c4n3a-rel8-cc}
16. Orquestador local → Interfaz de captura: la aceptación {dentro del celular} — N3-cap {c4n3a-rel4-cc}

*alt: el veredicto rechaza*

17. Orquestador local → Interfaz de captura: el motivo del rechazo [si el veredicto rechaza] {dentro del celular} — N3-cap {c4n3a-rel4-cc}
18. Interfaz de captura → Operario de campo: el motivo del rechazo {dentro del celular} — N3-cap {L_2uR0sUDPapYQsvCX1m-1-cc}

**Fin (qué queda distinto después):** la captura aceptada queda en el almacén del dispositivo como captura pendiente — N2 {c4n2-c-loc-cc}. La rechazada no se guarda (solo se guarda «lo aceptado», N3-cap {c4n3a-rel8-cc}).

**PENDIENTE:**
- Paso 11: si el rol no puede registrar, ¿qué pasa? El modelo no lo dice.
- Pasos 11 y 12: el modelo no fija el orden entre verificar el rol y evaluar las reglas.
- Paso 17: ¿lo rechazado se pierde, o el operario lo corrige en el momento y lo vuelve a enviar?
- ¿Se puede capturar una cama que no está asignada a ese celular? (Flujo 1)
- El operario «captura y corrige» (N1 {c4n1-rel2-cc}). En el celular, ¿una corrección es una captura nueva de la misma cama, o edita la anterior antes de sincronizar?
- ¿Cuándo termina la jornada en el celular?

### Flujo 3 · Sincronizar

**Disparador:** el celular vuelve a la red de la oficina — Dep {c4dep-rel2-cc}, N3-cap {c4n3a-ttl1-cc}. PENDIENTE: ¿la inicia el operario o la aplicación sola?

**Participantes, en orden de aparición:** Celular de campo · Servidor de la finca · Servicio de sincronización · cliente · Almacén del dispositivo · API Gateway de la finca · Identidad y permisos · Ingreso de sincronización · Gestión de dispositivos · Motor de reglas · instancia servidor · Datos maestros y parametrización versionada · Base de datos de la finca · Ciclo de producción · Caché de producciones activas · Bitácora de auditoría · Servicio de notificación · Motor de proyección · Emisión de credenciales · Custodia de llaves

**Precondición:** hay capturas pendientes en el almacén del dispositivo. PENDIENTE: ¿se sincroniza también sin capturas pendientes, solo para traer credencial, configuración y versión?

**Pasos, uno por mensaje:**
1. Celular de campo → Servidor de la finca: se conecta a la red local [al volver a la oficina, por el Wi-Fi de la oficina] {red local de la finca} — Dep {c4dep-rel2-cc}
2. Servicio de sincronización · cliente → Almacén del dispositivo: lee las capturas pendientes {dentro del celular} — N3-cap {c4n3a-rel14-cc}
3. Servicio de sincronización · cliente → API Gateway de la finca: las capturas pendientes [reanudable e idempotente] {red local de la finca} — N3-cap {c4n3a-rel15-cc}
4. API Gateway de la finca → Identidad y permisos: verifica la petición {red local de la finca} — N3-back {c4n3b-rel19-cc}
5. API Gateway de la finca → Ingreso de sincronización: la sesión de sincronización {red local de la finca} — N3-back {c4n3b-rel33-cc}
6. Ingreso de sincronización → Gestión de dispositivos: consulta el celular, sus camas asignadas y su versión {red local de la finca} — N3-back {c4n3b-rel-ing-dis-cc}
7. Ingreso de sincronización → Motor de reglas · instancia servidor: cada captura, para validarla {red local de la finca} — N3-back {c4n3b-rel3-cc}
8. Motor de reglas · instancia servidor → Datos maestros y parametrización versionada: lee las reglas de la versión con que se capturó {red local de la finca} — N3-back {c4n3b-rel8-cc}, {c4n3b-k-b-reg-cc}
9. Motor de reglas · instancia servidor → Ingreso de sincronización: el veredicto con su motivo {red local de la finca} — N3-back {c4n3b-rel3-cc}
10. Ingreso de sincronización → Base de datos de la finca: guarda cada captura [inserción idempotente por identificador; descarta duplicados; gana la más reciente según la hora corregida del celular] {red local de la finca} — N3-back {c4n3b-rel4-cc}, {c4n3b-k-b-ing-cc}
11. Ingreso de sincronización → Ciclo de producción: la registra como corrección abierta [si la captura cambia un dato ya registrado] {red local de la finca} — N3-back {c4n3b-rel-ing-ciclo-cc}
12. Ingreso de sincronización → Caché de producciones activas: actualiza la producción activa {red local de la finca} — N3-back {c4n3b-rel37-cc}
13. Ingreso de sincronización → Bitácora de auditoría: registra la sesión de captura recibida {red local de la finca} — N3-back {c4n3b-rel5-cc}
14. Bitácora de auditoría → Base de datos de la finca: guarda la bitácora [solo inserción] {red local de la finca} — N3-back {c4n3b-rel27-cc}
15. Ingreso de sincronización → Servicio de notificación: registra los descartes [si hubo] {red local de la finca} — N3-back {c4n3b-rel6-cc}
16. Servicio de notificación → Base de datos de la finca: guarda los avisos pendientes {red local de la finca} — N3-back {c4n3b-rel-not-db-cc}
17. Ingreso de sincronización → Servicio de notificación: recoge los avisos del operario {red local de la finca} — N3-back {c4n3b-rel6-cc}, {c4n3b-k-b-not-cc}
18. Ingreso de sincronización → Motor de proyección: recalcula la proyección con lo recién capturado {red local de la finca} — N3-back {c4n3b-rel-ing-pro-cc} (sigue en el Flujo 6)
19. Ingreso de sincronización → Emisión de credenciales: pide la credencial del operario y del celular {red local de la finca} — N3-back {c4n3b-rel21-cc}
20. Emisión de credenciales → Identidad y permisos: lee el rol y los permisos del operario {red local de la finca} — N3-back {c4n3b-rel20-cc}
21. Emisión de credenciales → Custodia de llaves: firma la credencial {red local de la finca} — N3-back {c4n3b-rel-cre-kv-cc}
22. Ingreso de sincronización → Datos maestros y parametrización versionada: obtiene la configuración vigente para el celular {red local de la finca} — N3-back {c4n3b-rel-ing-cfg-cc}
23. API Gateway de la finca → Servicio de sincronización · cliente: el resultado de las capturas, la credencial, la configuración vigente y la versión nueva de la aplicación {red local de la finca} — N3-cap {c4n3a-rel15-cc}
24. Servicio de sincronización · cliente → Almacén del dispositivo: guarda la respuesta del servidor {dentro del celular} — N3-cap {c4n3a-rel14-cc}

**Fin (qué queda distinto después):**
- las capturas están en la base de la finca;
- la bitácora tiene la sesión;
- el caché y la proyección se actualizaron;
- el celular tiene la respuesta, la credencial y la configuración nuevas.

**PENDIENTE:**
- Después de la respuesta, ¿qué pasa con las capturas en el celular? El cliente «las conserva hasta que el servidor responde» (N3-cap {c4n3a-k-cap-syn-cc}); el modelo no dice si después se borran.
- Paso 9: una captura que el motor del servidor rechaza, ¿se guarda marcada o se descarta? ¿Es eso un «descarte» del paso 15?
- Pasos 17 y 23: ¿los avisos bajan al celular? La flecha del celular no los nombra, y el Servicio de notificación dice que entrega el aviso «en su siguiente sincronización» (N3-back {c4n3b-k-b-not-cc}).
- Paso 10: ¿quién calcula la «hora corregida del celular» y con qué dato? Ninguna flecha la lleva.
- El modelo no fija el orden de los pasos 6 a 22 dentro del backend: ¿se valida antes de guardar?, ¿se registra la sesión al principio o al final?
- Paso 5 dice «sesión de sincronización» y paso 13 «sesión de captura». ¿Son la misma sesión?
- Si la sincronización se corta a la mitad, ¿qué queda guardado? La flecha dice «reanudable», pero no qué se conserva.
- Paso 6: ¿qué pasa si el celular no está registrado o tiene una versión vieja?

### Flujo 4 · Corregir

**Disparador:** pueden ser dos:
- una sincronización trae una captura que cambia un dato ya registrado (Flujo 3, paso 11) — N3-back {c4n3b-rel-ing-ciclo-cc};
- hay cambios sobre producciones cerradas por confirmar — N3-web {c4n3c-rel-adm-corr-cc}.

**Participantes, en orden de aparición:** Operario de campo · Interfaz de captura · Ingreso de sincronización · Ciclo de producción · Base de datos de la finca · Gerente de producción · Gestión de correcciones · Sesión de usuario, roles y permisos · Servicios de lectura · API Gateway de la finca · Identidad y permisos · (quien sirve la lista, PENDIENTE) · Servicios de escritura · Bitácora de auditoría · Motor de proyección · Servicio de notificación · Administrador del sistema en la finca

**Precondición:** el dato ya estaba registrado en la base de la finca.

**Pasos, uno por mensaje:**

*abrir la corrección*

1. Operario de campo → Interfaz de captura: corrige un dato ya capturado [en el invernadero, sin red] {dentro del celular} — N3-cap {3QFZ-2BZZ4g53YUmzgdM-1-cc} (sigue como el Flujo 2)
2. Ingreso de sincronización → Ciclo de producción: registra la corrección abierta [en la sincronización] {red local de la finca} — N3-back {c4n3b-rel-ing-ciclo-cc}
3. Ciclo de producción → Base de datos de la finca: guarda la corrección {red local de la finca} — N3-back {c4n3b-rel-ciclo-db-cc}

*resolverla*

4. Gerente de producción → Gestión de correcciones: abre las correcciones abiertas {red local de la finca} — N3-web {c4n3c-rel-ger-corr-cc}
5. Gestión de correcciones → Sesión de usuario, roles y permisos: consulta el rol para mostrar las acciones permitidas {red local de la finca} — N3-web {c4n3c-rel-corr-ses-cc}
6. Gestión de correcciones → Servicios de lectura: pide las correcciones abiertas y los cambios por confirmar {red local de la finca} — N3-web {c4n3c-rel-corr-lec-cc}
7. Servicios de lectura → API Gateway de la finca: la consulta, con la cookie de sesión {red local de la finca} — N3-web {c4n3c-rel16-cc}
8. API Gateway de la finca → Identidad y permisos: verifica la petición {red local de la finca} — N3-back {c4n3b-rel19-cc}
9. API Gateway de la finca → (quien sirve la lista): la consulta de las correcciones abiertas {red local de la finca}. PENDIENTE: ningún componente del backend dice que sirva la lista de correcciones.
10. Gerente de producción → Gestión de correcciones: resuelve una corrección abierta {red local de la finca} — N3-web {c4n3c-rel-ger-corr-cc}
11. Gestión de correcciones → Servicios de escritura: la resolución {red local de la finca} — N3-web {c4n3c-rel-corr-esc-cc}
12. Servicios de escritura → API Gateway de la finca: el cambio, con la cookie de sesión {red local de la finca} — N3-web {c4n3c-rel17-cc}
13. API Gateway de la finca → Ciclo de producción: la resolución de la corrección {red local de la finca} — N3-back {c4n3b-rel-gw-ciclo-cc}
14. Ciclo de producción → Base de datos de la finca: guarda la resolución {red local de la finca} — N3-back {c4n3b-rel-ciclo-db-cc}
15. Ciclo de producción → Bitácora de auditoría: registra la resolución {red local de la finca} — N3-back {c4n3b-rel-ciclo-aud-cc}
16. Ciclo de producción → Motor de proyección: recalcula la proyección con lo corregido {red local de la finca} — N3-back {c4n3b-rel-ciclo-pro-cc}
17. Ciclo de producción → Servicio de notificación: avisa al operario cuya captura se descartó [si la resolución descarta su captura] {red local de la finca} — N3-back {c4n3b-rel-ciclo-not-cc}
18. Servicio de notificación → Base de datos de la finca: guarda el aviso pendiente {red local de la finca} — N3-back {c4n3b-rel-not-db-cc}
19. Ingreso de sincronización → Servicio de notificación: recoge los avisos del operario [en su siguiente sincronización] {red local de la finca} — N3-back {c4n3b-rel6-cc}, {c4n3b-k-b-not-cc}

*opt: un cambio sobre una producción cerrada*

20. Administrador del sistema en la finca → Gestión de correcciones: confirma un cambio sobre una producción cerrada {red local de la finca} — N3-web {c4n3c-rel-adm-corr-cc}
21. Gestión de correcciones → Servicios de escritura: la confirmación {red local de la finca} — N3-web {c4n3c-rel-corr-esc-cc}
22. Servicios de escritura → API Gateway de la finca: el cambio, con la cookie de sesión {red local de la finca} — N3-web {c4n3c-rel17-cc}
23. API Gateway de la finca → Ciclo de producción: el cambio sobre lo cerrado {red local de la finca} — N3-back {c4n3b-rel-gw-ciclo-cc}
24. Ciclo de producción → Base de datos de la finca: guarda el cambio confirmado {red local de la finca} — N3-back {c4n3b-rel-ciclo-db-cc}
25. Ciclo de producción → Bitácora de auditoría: registra el cambio confirmado {red local de la finca} — N3-back {c4n3b-rel-ciclo-aud-cc}

**Fin (qué queda distinto después):**
- la corrección queda resuelta y registrada en la bitácora;
- la proyección se recalcula;
- si la captura del operario se descartó, hay un aviso pendiente para él;
- el cambio sobre lo cerrado queda confirmado y registrado.

**PENDIENTE:**
- Al llegar, la captura ya «gana la más reciente» (Flujo 3, paso 10). ¿Qué decide entonces el gerente al resolver: confirmar el valor nuevo, volver al anterior u otra cosa?
- ¿Quién propone un cambio sobre una producción cerrada, y por dónde entra? El modelo solo tiene la confirmación.
- ¿Qué pasa si nadie resuelve o nadie confirma?
- ¿Las correcciones abiertas se le avisan al gerente, o solo las ve cuando abre la pantalla?
- ¿Los avisos llegan al celular? (Flujo 3)

### Flujo 5 · Cerrar la producción

**Disparador:** PENDIENTE. Ciclo de producción «lleva cada producción de activa a cerrada» (N3-back {c4n3b-k-b-ciclo-cc}), pero el modelo no dice quién cierra, cuándo ni con qué regla.

**Participantes, en orden de aparición:** (quien cierra, PENDIENTE) · Aplicación web del sistema · API Gateway de la finca · Identidad y permisos · Ciclo de producción · Base de datos de la finca · Caché de producciones activas

**Precondición:** la producción está activa.

**Pasos, uno por mensaje:**
1. (quien cierra) → Aplicación web del sistema: cierra una producción {red local de la finca}. PENDIENTE: ninguna persona ni pantalla de la aplicación web tiene esta acción.
2. Aplicación web del sistema → API Gateway de la finca: el cierre [consulta y administra] {red local de la finca} — N2 {c4n2-rel17-cc}
3. API Gateway de la finca → Identidad y permisos: verifica la petición {red local de la finca} — N3-back {c4n3b-rel19-cc}
4. API Gateway de la finca → Ciclo de producción: el cierre de la producción {red local de la finca} — N3-back {c4n3b-rel-gw-ciclo-cc}
5. Ciclo de producción → Base de datos de la finca: guarda el cierre {red local de la finca} — N3-back {c4n3b-rel-ciclo-db-cc}
6. Ciclo de producción → Caché de producciones activas: retira la producción que se cierra {red local de la finca} — N3-back {c4n3b-rel-ciclo-cache-cc}

**Fin (qué queda distinto después):** la producción está cerrada y fuera del caché. Desde ese momento, los cambios sobre ella los confirma el Administrador del sistema en la finca — N1 {c4n1-p-adm-cc}, N3-web {c4n3c-rel-adm-corr-cc}.

**PENDIENTE:**
- ¿Qué se consolida o se calcula al cerrar? El modelo no lo dice.
- ¿El cierre queda en la bitácora? Ciclo → Bitácora solo nombra «cada resolución y cada cambio confirmado» (N3-back {c4n3b-rel-ciclo-aud-cc}).
- ¿Qué pasa con las correcciones abiertas de una producción que se cierra?
- ¿Cerrar una producción cambia su proyección o su desviación?
- ¿Cómo nace una producción? (ver §3)

### Flujo 6 · Proyectar

**Disparador:** pueden ser tres:
- cada sincronización — N3-back {c4n3b-k-b-pro-cc}, {c4n3b-rel-ing-pro-cc};
- cada corrección resuelta — N3-back {c4n3b-rel-ciclo-pro-cc};
- cada semana, el Planificador de tareas — N3-back {c4n3b-rel10-cc}, {c4n3b-k-b-job-cc}.

**Participantes, en orden de aparición:** Ingreso de sincronización · Ciclo de producción · Motor de proyección · Base de datos de la finca · Datos maestros y parametrización versionada · Planificador de tareas

**Precondición:** hay producción registrada en la base de la finca.

**Pasos, uno por mensaje:**

*alt: recálculo con cada sincronización o corrección*

1. Ingreso de sincronización → Motor de proyección: recalcula con lo recién capturado [en cada sincronización] {red local de la finca} — N3-back {c4n3b-rel-ing-pro-cc}
2. Ciclo de producción → Motor de proyección: recalcula con lo corregido [al resolver una corrección] {red local de la finca} — N3-back {c4n3b-rel-ciclo-pro-cc}
3. Motor de proyección → Base de datos de la finca: lee la producción {red local de la finca} — N3-back {c4n3b-rel9-cc}
4. Motor de proyección → Datos maestros y parametrización versionada: toma los parámetros vigentes {red local de la finca} — N3-back {c4n3b-rel25-cc}
5. Motor de proyección → (dónde, PENDIENTE): la proyección recalculada {red local de la finca}. PENDIENTE: el modelo no dice dónde se guarda la proyección recalculada.

*alt: publicación semanal*

6. Planificador de tareas → Motor de proyección: dispara la publicación semanal [cada semana, por la cola de trabajos en la base] {red local de la finca} — N3-back {c4n3b-rel10-cc}
7. Motor de proyección → Datos maestros y parametrización versionada: toma los parámetros vigentes y los congela en la versión {red local de la finca} — N3-back {c4n3b-rel25-cc}
8. Motor de proyección → Base de datos de la finca: lee la producción y publica la versión semanal sin tocar las anteriores {red local de la finca} — N3-back {c4n3b-rel9-cc}

**Fin (qué queda distinto después):** hay una proyección recalculada (pasos 1 a 5) o una versión publicada que no se modifica (pasos 6 a 8) — N3-back {c4n3b-k-b-pro-cc}.

**PENDIENTE:**
- ¿Dónde vive la proyección recalculada, y quién la lee? Consulta y tableros lee «la proyección publicada» (N3-back {c4n3b-rel12-cc}).
- ¿Qué día y a qué hora se publica la versión semanal? ¿Se puede publicar a mano?
- La Vista de proyección y desviación compara lo proyectado contra lo real (N3-web {c4n3c-k-w-pro-cc}). ¿Contra cuál: la versión publicada o la recalculada?
- ¿Recalcular rehace toda la finca o solo lo que cambió?

### Flujo 7 · Consultar

**Disparador:** el gerente o el personal de ventas abren una vista en el navegador de la oficina — N3-web {c4n3c-rel3-cc}, {c4n3c-rel4-cc}.

**Participantes, en orden de aparición:** API Gateway de la finca · Aplicación web del sistema · Sesión de usuario, roles y permisos · Identidad y permisos · Gerente de producción · Vista de proyección y desviación · Tableros de operaciones · Vista de camas · Personal de ventas · Vista de disponibilidad · Servicios de lectura · Consulta y tableros · Caché de producciones activas · Base de datos de la finca · Navegación detallada · Exportación de archivos · Generador de documentos · Bitácora de auditoría

**Precondición:** ninguna más allá de estar en la red de la oficina — Dep {c4dep-d-n3-cc}.

**Pasos, uno por mensaje:**
1. API Gateway de la finca → Aplicación web del sistema: entrega la aplicación al navegador {red local de la finca} — N2 {c4n2-rel13-cc}
2. Sesión de usuario, roles y permisos → API Gateway de la finca: inicia la sesión {red local de la finca} — N3-web {c4n3c-rel-ses-gw-cc}
3. API Gateway de la finca → Identidad y permisos: verifica la petición {red local de la finca} — N3-back {c4n3b-rel19-cc}

*alt: el gerente*

4. Gerente de producción → Vista de proyección y desviación: sigue la proyección y la desviación {red local de la finca} — N3-web {c4n3c-rel3-cc}
5. Gerente de producción → Tableros de operaciones: sigue el avance del día {red local de la finca} — N3-web {c4n3c-rel-ger-tab-cc}
6. Gerente de producción → Vista de camas: consulta la producción viva por cama {red local de la finca} — N3-web {c4n3c-rel-ger-cam-cc}

*alt: ventas*

7. Personal de ventas → Vista de disponibilidad: consulta la flor disponible; solo lee {red local de la finca} — N3-web {c4n3c-rel4-cc}
8. Personal de ventas → Vista de proyección y desviación: consulta la proyección de corte {red local de la finca} — N3-web {c4n3c-rel-ven-pro-cc}

*cada vista pide sus datos*

9. Vista de proyección y desviación → Servicios de lectura: pide lo proyectado y lo real {red local de la finca} — N3-web {c4n3c-rel12-cc}
10. Tableros de operaciones → Servicios de lectura: pide el avance del día y las camas sin datos {red local de la finca} — N3-web {c4n3c-rel9-cc}
11. Vista de camas → Servicios de lectura: pide el plano de la finca y el porcentaje por cama {red local de la finca} — N3-web {c4n3c-rel10-cc}
12. Vista de disponibilidad → Servicios de lectura: pide la flor disponible {red local de la finca} — N3-web {c4n3c-rel-dispo-lec-cc}
13. Servicios de lectura → API Gateway de la finca: la consulta, con la cookie de sesión {red local de la finca} — N3-web {c4n3c-rel16-cc}
14. API Gateway de la finca → Identidad y permisos: verifica la petición {red local de la finca} — N3-back {c4n3b-rel19-cc}
15. API Gateway de la finca → Consulta y tableros: la consulta {red local de la finca} — N3-back {c4n3b-rel34-cc}
16. Consulta y tableros → Caché de producciones activas: lee las producciones activas antes de ir a la base {red local de la finca} — N3-back {c4n3b-rel38-cc}
17. Caché de producciones activas → Base de datos de la finca: carga las producciones activas [si el caché no las tiene; si difiere de la base, prevalece la base] {red local de la finca} — N3-back {c4n3b-rel39-cc}, {c4n3b-c-cache-cc}
18. Consulta y tableros → Base de datos de la finca: lee la producción y la proyección publicada [solo lectura] {red local de la finca} — N3-back {c4n3b-rel12-cc}

*opt: el detalle de una cifra*

19. Vista de proyección y desviación → Navegación detallada: abre el detalle de una cifra {red local de la finca} — N3-web {c4n3c-rel11-cc}
20. Navegación detallada → Servicios de lectura: pide los registros que forman la cifra [carga progresiva] {red local de la finca} — N3-web {c4n3c-rel13-cc}

*opt: exportar lo que se está viendo*

21. Vista de proyección y desviación → Exportación de archivos: exporta lo que se está viendo {red local de la finca} — N3-web {c4n3c-rel-pro-exp-cc}
22. Exportación de archivos → Servicios de lectura: pide el archivo elegido (Excel o PDF) {red local de la finca} — N3-web {c4n3c-rel14-cc}
23. Consulta y tableros → Generador de documentos: pide un Excel o un PDF [por la cola de trabajos en la base] {red local de la finca} — N3-back {c4n3b-rel13-cc}
24. Generador de documentos → Base de datos de la finca: lee los datos del reporte [solo lectura] {red local de la finca} — N3-back {c4n3b-rel-doc-db-cc}
25. Generador de documentos → Bitácora de auditoría: lee la bitácora [si es el reporte de auditoría] {red local de la finca} — N3-back {c4n3b-rel-doc-aud-cc}
26. Generador de documentos → Consulta y tableros: el archivo [al terminar] {red local de la finca} — N3-back {c4n3b-rel13-cc}
27. Servicios de lectura → Exportación de archivos: el archivo, que se descarga cuando está listo {red local de la finca} — N3-web {c4n3c-rel14-cc}, {c4n3c-k-w-exp-cc}

**Fin (qué queda distinto después):** nada cambia en la finca. La persona ve lo que pidió y, si exportó, tiene el archivo.

**PENDIENTE:**
- ¿Quién sirve el avance del día, las camas sin datos y el plano de la finca? Consulta y tableros solo declara «la proyección, la desviación y la flor disponible por día, semana y mes» (N3-back {c4n3b-k-b-con-cc}).
- ¿Quién mantiene el plano de la finca, y dónde se guarda?
- La Vista de camas muestra «producción viva» (N3-web {c4n3c-k-w-cam-cc}) y Consulta y tableros lee «la proyección publicada». ¿La consulta muestra lo recalculado o lo publicado?
- Ventas usa la misma Vista de proyección y desviación que el gerente. ¿Qué ve ventas exactamente?
- Paso 2: ¿con qué credencial entra la persona? El modelo no lo dice.
- Pasos 26 y 27: ¿cómo se entera la aplicación web de que el archivo está listo?
- ¿Quién puede pedir el reporte de auditoría?

### Flujo 8 · Exportar al BI

**Disparador:** la herramienta de análisis de la empresa pide datos. PENDIENTE: ¿cada cuánto y quién lo programa?

**Participantes, en orden de aparición:** Herramienta de análisis de la empresa · API Gateway de la finca · Identidad y permisos · Interfaz de salida de datos · Base de datos de la finca

**Precondición:** la herramienta está dada de alta para autenticarse — N3-back {c4n3b-k-b-iam-cc}. PENDIENTE: ¿quién la da de alta?

**Pasos, uno por mensaje:**
1. Herramienta de análisis de la empresa → API Gateway de la finca: lee los datos de producción [solo lectura] {red local de la finca} — N3-back {c4n3b-rel16-cc}
2. API Gateway de la finca → Identidad y permisos: verifica la petición (autentica a la herramienta de análisis) {red local de la finca} — N3-back {c4n3b-rel19-cc}, {c4n3b-k-b-iam-cc}
3. API Gateway de la finca → Interfaz de salida de datos: la consulta de análisis {red local de la finca} — N3-back {c4n3b-rel-gw-sal-cc}
4. Interfaz de salida de datos → Base de datos de la finca: lee la producción y la proyección publicada [solo lectura] {red local de la finca} — N3-back {c4n3b-rel15-cc}
5. Interfaz de salida de datos → Herramienta de análisis de la empresa: los datos de producción, en solo lectura {red local de la finca} — N3-back {c4n3b-k-b-sal-cc}

**Fin (qué queda distinto después):** la herramienta tiene los datos y nada cambió en la finca.

**PENDIENTE:**
- ¿Qué datos exactamente: anotaciones, totales o la proyección publicada? ¿Con qué detalle?
- ¿La herramienta solo lee desde la red local? Despliegue la pone en el computador de oficina (Dep {c4dep-s-bi-cc}).

### Flujo 9 · Respaldar y restaurar

**Disparador:** pueden ser tres:
- el Planificador de tareas dispara el respaldo — N3-back {c4n3b-rel11-cc}, {c4n3b-k-b-job-cc}. PENDIENTE: ¿cada cuánto?;
- antes de migrar — N3-back {y2uk7SS4gnAXLxvLYaCL-6-cc} (Flujo 10);
- restaurar. PENDIENTE: ¿quién lo ordena y en qué caso?

**Participantes, en orden de aparición:** Planificador de tareas · Servicio de respaldo · Base de datos de la finca · Custodia de llaves · Control de borde · Identidad y suscripción · Recepción y entrega de respaldos · Custodia de respaldos · (quien ordena restaurar, PENDIENTE)

**Precondición:** la finca tiene su llave en Custodia de llaves — N3-back {c4n3b-k-b-kv-cc}.

**Pasos, uno por mensaje:**

*respaldar*

1. Planificador de tareas → Servicio de respaldo: dispara el respaldo y su prueba de restauración [por la cola de trabajos en la base] {red local de la finca} — N3-back {c4n3b-rel11-cc}
2. Servicio de respaldo → Base de datos de la finca: copia la base para respaldarla [volcado] {red local de la finca} — N3-back {c4n3b-rel28-cc}
3. Servicio de respaldo → Custodia de llaves: obtiene la llave de la finca para cifrar {red local de la finca} — N3-back {c4n3b-rel22-cc}
4. Servicio de respaldo → Control de borde: el respaldo cifrado [HTTPS con TLS mutuo; reintenta si la nube no responde] {internet} — N3-back {c4n3b-rel29-cc}, N3-nube {c4n3d-rel15-cc}
5. Control de borde → Identidad y suscripción: verifica que quien llama es la finca {internet} — N3-nube {c4n3d-rel8-cc}
6. Control de borde → Recepción y entrega de respaldos: el respaldo {internet} — N3-nube {c4n3d-rel5-cc}
7. Recepción y entrega de respaldos → Custodia de respaldos: guarda el respaldo cifrado {internet} — N3-nube {c4n3d-rel11-cc}
8. Servicio de respaldo → (dónde, PENDIENTE): prueba la restauración {red local de la finca} — N3-back {c4n3b-k-b-bkp-cc}. PENDIENTE: ¿dónde se restaura la prueba sin tocar la base en uso?

*restaurar*

9. (quien ordena) → Servicio de respaldo: restaurar {red local de la finca}. PENDIENTE: ningún elemento del modelo lo ordena.
10. Servicio de respaldo → Control de borde: pide el respaldo para restaurar [bajo demanda] {internet} — N3-back {c4n3b-rel29-cc}, N2 {c4n2-rel23-cc}
11. Control de borde → Identidad y suscripción: verifica que quien llama es la finca {internet} — N3-nube {c4n3d-rel8-cc}
12. Control de borde → Recepción y entrega de respaldos: la petición de restauración {internet} — N3-nube {c4n3d-rel5-cc}
13. Recepción y entrega de respaldos → Custodia de respaldos: recupera el respaldo cifrado {internet} — N3-nube {c4n3d-rel11-cc}
14. Recepción y entrega de respaldos → Servicio de respaldo: el respaldo, intacto y cifrado {internet} — N3-nube {c4n3d-k-n-res-cc}, N2 {c4n2-rel23-cc}
15. Servicio de respaldo → Custodia de llaves: obtiene la llave de la finca para descifrar {red local de la finca} — N3-back {c4n3b-rel22-cc}
16. Servicio de respaldo → Base de datos de la finca: restaura {red local de la finca}. PENDIENTE: el modelo no tiene esta flecha; la única que hay es «Copia la base para respaldarla» (N3-back {c4n3b-rel28-cc}).

**Fin (qué queda distinto después):**
- al respaldar, hay una copia cifrada en la Custodia de respaldos que la nube no puede leer — N2 {c4n2-c-obj-cc};
- al restaurar: PENDIENTE.

**PENDIENTE:**
- ¿Cada cuánto se respalda? El Planificador dispara «la publicación semanal de la proyección y el respaldo» (N3-back {c4n3b-k-b-job-cc}), y no queda claro si «semanal» vale también para el respaldo.
- Si se pierde el servidor de la finca, la Custodia de llaves se pierde con él. ¿Cómo se descifra el respaldo? El modelo dice que su copia de recuperación «se conserva fuera de línea» (N3-back {c4n3b-k-b-kv-cc}), pero no cómo vuelve.
- ¿Restaurar en un servidor nuevo usa el mismo Servicio de respaldo? ¿Quién instala ese servidor?
- ¿Qué pasa con lo que se capturó después del último respaldo?

### Flujo 10 · Actualizar la versión (finca y celulares)

**Disparador:** pueden ser dos:
- la integración continua del equipo publica una versión firmada — N3-nube {c4n3d-rel14-cc};
- el backend pide la versión nueva «bajo demanda» — N3-back {c4n3b-rel36-cc}. PENDIENTE: ¿quién decide pedirla, y cuándo?

**Participantes, en orden de aparición:** Integración continua del equipo · Control de borde · Identidad y suscripción · Distribución de versiones · Actualización y migraciones · Servicio de respaldo · Base de datos de la finca · Gestión de dispositivos · Ingreso de sincronización · API Gateway de la finca · Servicio de sincronización · cliente · Aplicación de captura

**Precondición:** la suscripción de la finca está vigente — N3-nube {c4n3d-k-n-ver-cc}.

**Pasos, uno por mensaje:**

*publicar la versión*

1. Integración continua del equipo → Control de borde: publica cada versión firmada [HTTPS] {internet} — N3-nube {c4n3d-rel14-cc}
2. Control de borde → Identidad y suscripción: verifica que quien llama es la integración continua {internet} — N3-nube {c4n3d-rel8-cc}
3. Control de borde → Distribución de versiones: la publicación {internet} — N3-nube {c4n3d-rel7-cc}

*actualizar la finca*

4. Actualización y migraciones → Control de borde: pide la versión nueva del sistema y de la aplicación [HTTPS con TLS mutuo; artefacto firmado; bajo demanda] {internet} — N3-back {c4n3b-rel36-cc}, N3-nube {c4n3d-rel15-cc}
5. Control de borde → Identidad y suscripción: verifica que quien llama es la finca {internet} — N3-nube {c4n3d-rel8-cc}
6. Control de borde → Distribución de versiones: la petición {internet} — N3-nube {c4n3d-rel7-cc}
7. Distribución de versiones → Identidad y suscripción: ¿la suscripción de la finca está vigente? {internet} — N3-nube {c4n3d-rel9-cc}
8. Identidad y suscripción → Base de datos de los servicios en línea: lee las cuentas y la suscripción {internet} — N3-nube {c4n3d-rel-iam-db-cc}
9. Distribución de versiones → Actualización y migraciones: la versión nueva [si la suscripción está vigente] {internet} — N3-nube {c4n3d-k-n-ver-cc}
10. Actualización y migraciones → Servicio de respaldo: pide un respaldo antes de migrar {red local de la finca} — N3-back {y2uk7SS4gnAXLxvLYaCL-6-cc} (sigue en el Flujo 9)
11. Actualización y migraciones → Base de datos de la finca: aplica las migraciones versionadas {red local de la finca} — N3-back {c4n3b-rel31-cc}

*actualizar los celulares*

12. Actualización y migraciones → Gestión de dispositivos: entrega la versión nueva de la aplicación de captura {red local de la finca} — N3-back {c4n3b-rel32-cc}
13. Gestión de dispositivos → Base de datos de la finca: guarda la versión de cada celular {red local de la finca} — N3-back {c4n3b-rel-dis-db-cc}
14. Ingreso de sincronización → Gestión de dispositivos: consulta la versión del celular [en su siguiente sincronización] {red local de la finca} — N3-back {c4n3b-rel-ing-dis-cc}
15. API Gateway de la finca → Servicio de sincronización · cliente: la versión nueva de la aplicación {red local de la finca} — N3-cap {c4n3a-rel15-cc}
16. (quién) → Aplicación de captura: instala la versión nueva {dentro del celular}. PENDIENTE: el modelo no lo dice.

**Fin (qué queda distinto después):** la finca corre la versión nueva con la base migrada, y cada celular la recibe al sincronizar.

**PENDIENTE:**
- ¿Quién verifica la firma de la versión, en la finca y en el celular?
- ¿Cómo se instala la versión nueva en un celular Android, y qué pasa con sus capturas pendientes?
- ¿Cómo se revierte una actualización que falla?
- ¿Qué pasa si la suscripción no está vigente: la finca sigue con la versión que tiene?
- N2 dice «Pide la versión nueva del sistema» (N2 {c4n2-rel18-cc}) y N3-back «del sistema y de la aplicación» (N3-back {c4n3b-rel36-cc}). ¿Es la misma petición?

### Flujo 11 · Carga inicial desde Access

**Disparador:** PENDIENTE. ¿Quién ordena la carga y cuándo? El modelo dice «una sola vez» (N3-back {c4n3b-rel41-cc}).

**Participantes, en orden de aparición:** (quien ordena, PENDIENTE) · Lectura del sistema heredado · Sistema heredado de producción · Base de datos de la finca

**Precondición:** el archivo de Access está compartido en una carpeta del computador con Windows — Dep {c4dep-d-leg-cc}.

**Pasos, uno por mensaje:**
1. (quien ordena) → Lectura del sistema heredado: inicia la carga {red local de la finca}. PENDIENTE: el modelo no lo dice.
2. Lectura del sistema heredado → Sistema heredado de producción: consulta el histórico del sistema anterior [JDBC sobre el archivo de Access, por la carpeta compartida] {red local de la finca} — N3-back {c4n3b-rel40-cc}
3. Lectura del sistema heredado → Lectura del sistema heredado: filtra el histórico y lo organiza para la carga inicial {red local de la finca} — N3-back {c4n3b-k-b-leg-cc}
4. Lectura del sistema heredado → Base de datos de la finca: escribe el histórico ya organizado [carga inicial, una sola vez] {red local de la finca} — N3-back {c4n3b-rel41-cc}

**Fin (qué queda distinto después):** el histórico de la finca está en la base de la finca.

**PENDIENTE:**
- ¿Se lee el Access mientras está en uso, o una copia congelada?
- ¿Qué se filtra, y con qué criterio?
- ¿El histórico pasa por el motor de reglas, o entra tal cual?
- ¿La carga queda en la bitácora?
- ¿Qué pasa si la carga falla a la mitad: se repite entera?

---

## 3 · Entidades con ciclo de vida

### Producción, como entidad con ciclo de vida

**Estados:** activa · cerrada

**Transiciones, una por línea:**
- [*] → activa: nace una producción [PENDIENTE: quién la crea] — el modelo no lo dice.
- activa → cerrada: cierre de la producción [PENDIENTE: quién lo provoca] — N3-back {c4n3b-k-b-ciclo-cc}, {c4n3b-rel-gw-ciclo-cc}
- cerrada → cerrada: cambio confirmado sobre lo cerrado [Administrador del sistema en la finca] — N3-back {c4n3b-k-b-ciclo-cc}, N3-web {c4n3c-rel-adm-corr-cc}

**Estados finales:** cerrada. Al pasar a cerrada, la producción sale del caché — N3-back {c4n3b-rel-ciclo-cache-cc}.

### Producción, como dato capturado

**No tiene un ciclo propio en el modelo.** Es lo que lleva cada captura de una cama:
- «Registra la producción de cada cama» — N1 {c4n1-s-flor-cc};
- «Captura y corrige la producción» — N1 {c4n1-rel2-cc}.

Su ciclo es el de la Captura (abajo).

PENDIENTE: el modelo usa «producción» para las dos cosas. ¿Cómo se llama cada una? ¿A qué producción pertenece una captura, y cómo se sabe?

### Jornada, con su asignación

**Estados:** PENDIENTE. El modelo nombra la jornada y la asignación de camas, pero no sus estados:
- «al iniciar la jornada» — N3-cap {c4n3a-rel3-cc};
- «durante toda la jornada, sin red» — N3-cap {c4n3a-k-cap-per-cc};
- «le asigna sus camas» — N3-back {c4n3b-k-b-dis-cc}.

**Transiciones que el modelo sí deja ver, una por línea:**
- [*] → camas asignadas: se registran las camas de un celular [PENDIENTE: quién] — N3-back {c4n3b-rel-gw-dis-cc}, {c4n3b-rel-dis-db-cc}
- camas asignadas → jornada iniciada: el operario verifica su credencial [Operario de campo] — N3-cap {c4n3a-rel3-cc}

**Estados finales:** PENDIENTE.
- ¿Cuándo termina una jornada?
- ¿Una cama asignada que al final del día no tiene datos (N3-web {c4n3c-k-w-tab-cc}) cambia de estado?

### Captura

**Estados:** en captura · rechazada en el celular · pendiente · en sincronización · duplicada · guardada en la finca · descartada

**Transiciones, una por línea:**
- [*] → en captura: el operario empieza a capturar una cama [Operario de campo] — N3-cap {3QFZ-2BZZ4g53YUmzgdM-1-cc}
- en captura → rechazada en el celular: el motor de reglas local la rechaza [Motor de reglas · instancia local] — N3-cap {c4n3a-rel6-cc}, {L_2uR0sUDPapYQsvCX1m-1-cc}
- en captura → pendiente: el motor local la acepta y se guarda [Orquestador local] — N3-cap {c4n3a-rel8-cc}
- pendiente → en sincronización: se envía al servidor [Servicio de sincronización · cliente] — N3-cap {c4n3a-rel15-cc}
- en sincronización → pendiente: el servidor no responde y se conserva [Servicio de sincronización · cliente] — N3-cap {c4n3a-k-cap-syn-cc}
- en sincronización → duplicada: el servidor ya la tenía [Ingreso de sincronización] — N3-back {c4n3b-k-b-ing-cc}, {c4n3b-rel4-cc}
- en sincronización → guardada en la finca: se guarda; gana la más reciente [Ingreso de sincronización] — N3-back {c4n3b-rel4-cc}
- en sincronización → descartada: el servidor la descarta [Ingreso de sincronización] — N3-back {c4n3b-rel6-cc}
- guardada en la finca → descartada: al resolver la corrección se descarta [Gerente de producción] — N3-back {c4n3b-rel-ciclo-not-cc}

**Estados finales:** rechazada en el celular · duplicada · guardada en la finca · descartada

Si una captura guardada cambia un dato ya registrado, abre una Corrección (abajo) — N3-back {c4n3b-rel-ing-ciclo-cc}.

**PENDIENTE:**
- ¿Qué es exactamente «descartada»: rechazada por las reglas del servidor, perdedora frente a la más reciente, o descartada al resolver una corrección? El modelo usa «descarte» en N3-back {c4n3b-rel6-cc} y {c4n3b-rel-ciclo-not-cc}.
- La captura que pierde frente a la más reciente, ¿queda guardada en la historia?
- ¿Qué pasa con la captura en el celular una vez guardada en la finca? (Flujo 3)

### Corrección

**Estados:** abierta · resuelta · por confirmar · confirmada

**Transiciones, una por línea:**
- [*] → abierta: llega una captura que cambia un dato ya registrado [Ingreso de sincronización] — N3-back {c4n3b-rel-ing-ciclo-cc}
- abierta → resuelta: el gerente la resuelve [Gerente de producción] — N3-web {c4n3c-rel-ger-corr-cc}, N3-back {c4n3b-rel-gw-ciclo-cc}
- [*] → por confirmar: alguien propone un cambio sobre una producción cerrada [PENDIENTE: quién] — N3-web {c4n3c-rel-corr-lec-cc} («los cambios por confirmar»)
- por confirmar → confirmada: el administrador lo confirma [Administrador del sistema en la finca] — N3-web {c4n3c-rel-adm-corr-cc}

**Estados finales:** resuelta · confirmada

**PENDIENTE:**
- ¿Existe «no confirmada» o «rechazada»?
- ¿Una corrección abierta caduca?
- ¿Qué pasa con las abiertas cuando se cierra la producción?

### Proyección

**Estados:** recalculada · versión publicada

**Transiciones, una por línea:**
- recalculada → recalculada: llega una sincronización o se resuelve una corrección [Ingreso de sincronización; Ciclo de producción] — N3-back {c4n3b-rel-ing-pro-cc}, {c4n3b-rel-ciclo-pro-cc}
- recalculada → versión publicada: publicación semanal [Planificador de tareas] — N3-back {c4n3b-rel10-cc}, {c4n3b-k-b-pro-cc}

**Estados finales:** versión publicada, «una versión que no se modifica» — N3-back {c4n3b-k-b-pro-cc}.

---

## 4 · Lo que el modelo tiene y no cae en ninguno de los once flujos

Va aquí para que no se pierda. No se dibuja como flujo propio, porque la lista de once es fija.

- **Telemetría y diagnóstico remoto:**
  - Observabilidad → Servicios en línea — N3-back {c4n3b-rel30-cc};
  - Control de borde → Recepción de telemetría → Base de datos de los servicios en línea — N3-nube {c4n3d-rel6-cc}, {c4n3d-rel-tel-db-cc};
  - Operador de la plataforma → Panel de soporte remoto → Recepción de telemetría — N3-nube {c4n3d-rel3-cc}, {c4n3d-rel12-cc};
  - Panel de soporte remoto → Identidad y suscripción — N3-nube {c4n3d-rel-panel-iam-cc}.
- **Gestión de usuarios y permisos:**
  - Administrador del sistema en la finca → Panel de administración → Servicios de escritura → API Gateway de la finca → Identidad y permisos («Enruta la gestión de usuarios») → Base de datos de la finca — N3-web {c4n3c-rel5-cc}, {c4n3c-rel15-cc}, {c4n3c-rel17-cc}, N3-back {c4n3b-rel-gw-iam2-cc}, {c4n3b-rel-iam-db-cc};
  - el panel lee los usuarios y el formato vigentes — N3-web {c4n3c-rel-adm-lec-cc};
  - y solo se le muestra al administrador — N3-web {c4n3c-rel6-cc}.
- **Inicio y cierre de sesión en la aplicación web:** N3-web {c4n3c-rel-ses-gw-cc}.

## 5 · La misma conversación en otros niveles

Estas flechas dicen, en N1, N2, Despliegue o en otra página de Nivel 3, lo mismo que pasos ya escritos
arriba. Se listan para que ninguna flecha del modelo quede fuera y para seguir el dato entre páginas.
No son pasos nuevos.

| Flecha | Es la misma conversación que |
|---|---|
| N1 {c4n1-rel2-cc} Operario de campo → FlorLogic | Flujo 2 (y 4, paso 1) |
| N1 {c4n1-rel3-cc} Gerente de producción → FlorLogic | Flujo 4 (pasos 4 a 10) y Flujo 7 (pasos 4 a 6) |
| N1 {c4n1-rel4-cc} Personal de ventas → FlorLogic | Flujo 7 (pasos 7 y 8) |
| N1 {c4n1-rel5-cc} Administrador del sistema en la finca → FlorLogic | Flujo 1 (paso 1), Flujo 4 (paso 20) y §4 (usuarios) |
| N1 {c4n1-rel6-cc} Operador de la plataforma → FlorLogic | §4 (telemetría y diagnóstico) |
| N1 {c4n1-rel7-cc} Herramienta de análisis → FlorLogic | Flujo 8 |
| N1 {c4n1-rel8-cc} FlorLogic → Sistema heredado de producción | Flujo 11 |
| N2 {c4n2-rel3-cc}, N2 {c4n2-rel8-cc}, Dep {c4dep-rel3-cc} Aplicación de captura ↔ Almacén del dispositivo | Flujo 2 y Flujo 3 (pasos 2 y 24) |
| N2 {c4n2-rel9-cc}, Dep {c4dep-rel4-cc}, N3-back {c4n3b-rel35-cc} Aplicación de captura → API Gateway | Flujo 3 (pasos 3 y 23) |
| N2 {c4n2-rel10-cc}, Dep {c4dep-rel5-cc}, N3-cap {c4n3a-rel16-cc} API Gateway → Backend | todo paso que va del API Gateway a un componente del backend |
| N2 {c4n2-rel11-cc}, Dep {c4dep-rel6-cc} Backend → Base de datos de la finca | todo paso de un componente del backend a la base |
| N2 {c4n2-rel13-cc}, Dep {c4dep-rel8-cc} API Gateway → Aplicación web | Flujo 7 (paso 1) |
| N2 {c4n2-rel14-cc}, Dep {c4dep-rel-bi-cc} Herramienta de análisis → API Gateway | Flujo 8 (paso 1) |
| N2 {c4n2-rel15-cc}, Dep {c4dep-rel9-cc} Backend → Servicios en línea: respaldo y telemetría | Flujo 9 (paso 4) y §4 (telemetría) |
| N2 {c4n2-rel17-cc}, Dep {c4dep-rel11-cc}, N3-back {c4n3b-rel-web-cc} Aplicación web → API Gateway | Flujos 1, 4, 5 y 7 (pasos que salen de Servicios de lectura o de escritura) |
| N2 {c4n2-rel18-cc}, Dep {c4dep-rel12-cc} Backend → Servicios en línea: versión nueva | Flujo 10 (paso 4) |
| N2 {c4n2-rel19-cc}, Dep {c4dep-rel13-cc} Servicios en línea → Custodia de respaldos | Flujo 9 (pasos 7 y 13) |
| N2 {c4n2-rel23-cc}, Dep {c4dep-rel-res-cc} Backend → Servicios en línea: respaldo para restaurar | Flujo 9 (paso 10) |
| N2 {5mauoWmsDpcP75MK4mOA-2-cc}, Dep {c4dep-rel-leg-cc} Backend → Sistema heredado | Flujo 11 (paso 2) |
| N2 {c4n2-rel-nubedb-cc}, Dep {c4dep-rel-nubedb-cc} Servicios en línea → su base de datos | Flujo 10 (paso 8) y §4 (telemetría) |
| Dep {c4dep-rel2-cc} Celular de campo → Servidor de la finca | Flujo 3 (paso 1) |

---

*Fin del documento. Construido solo con las 7 páginas «(copia con cambios)» y con decisiones-de-juan.md (vacío
al escribirlo). Pasa a «REVISADO POR JUAN <fecha>» cuando Juan lo apruebe; entonces se copia a la entrada de la
revisión.*
