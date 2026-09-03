# FlorLogic — Modelo de componentes

> **v1.0 · 31-ago-2026 · PROPUESTA DEL EQUIPO, sin validar con el cliente.**
>
> Las conexiones entre los componentes de `FlorLogic_Bloques_Arquitectonicos.xlsx`, en cinco vistas.
> **Un solo grafo con los 31 bloques y sus aristas no se puede leer** — se parte por nodo, más una
> vista de secuencia que enseña el camino de un dato de punta a punta.
>
> **Qué manda:** `PLAN_DE_CONSTRUCCION.md` para las reglas de partición y los contratos ·
> `DRIVERS_ARQUITECTONICOS.md` para requisitos, restricciones y medidas.

## Los seis contratos

Un contrato es lo que un bloque promete a otro y no puede romper sin avisar. Son lo único que Juan y
Jerónimo tienen que acordar antes de escribir código por separado.

| ID | Entre | Qué fija |
|---|---|---|
| `CT-01` | Datos maestros → catálogo local | Baja **completo** y versionado. Sin catálogo vigente no se captura |
| `CT-02` | Motor de reglas dispositivo ≡ servidor | **El mismo artefacto**, evaluado por la misma implementación. 0% de divergencia (`ESC-57`) |
| `CT-03` | Sincronización cliente → ingesta | Idempotente, reanudable, ventana de 15 días o más, gana el más reciente |
| `CT-04` | Datos maestros → Job Scheduler | El motor lee un **snapshot inmutable**, nunca la tabla viva |
| `CT-05` | Interfaz de salida → exterior | Autenticada y limitada a la empresa. **0 accesos directos a la base** |
| `CT-06` | Distribución de versiones → nodo | **El mismo paquete en nube y en sitio**, 0 cambios de código |

## Convenciones de los diagramas

| Marca | Significado |
|---|---|
| Flecha gruesa `==>` | Cruza la frontera de un nodo. Es donde vive un contrato |
| Flecha punteada `-.->` | Relación de apoyo: cifra, observa, despliega, propone |
| Cilindro verde | Guarda estado |
| Caja amarilla | Bloque de seguridad |
| Caja naranja | **Contradicción o bloqueo sin resolver** |
| Nodo de borde redondo | Frontera: algo que vive en otro diagrama |

---

## D1 · Vista general — los cuatro nodos y los seis contratos

Tres nodos guardan estado; el puesto de consulta no. La regla que ordena todo: **ningún camino
crítico pasa por internet** (`CN-17`, `CN-37`). Si un bloque necesita la nube para funcionar,
está mal ubicado.

```mermaid
flowchart LR
  subgraph N1["N1 · DISPOSITIVO DE CAMPO — sin red · 3 capturadores"]
    D["Aplicación de captura<br/>+ reglas + almacén local"]
  end
  subgraph N2["N2 · NODO DE FINCA — la instalación · es el producto"]
    F["Servicios, datos,<br/>seguridad y operación"]
  end
  subgraph N3["N3 · PUESTO DE CONSULTA — sin estado propio"]
    W["Aplicación web<br/>de consulta"]
  end
  subgraph N4["N4 · SERVICIOS EN LÍNEA — la mensualidad"]
    C["Respaldo · versiones<br/>soporte · IA analítica"]
  end
  BI["Herramienta de BI<br/>del cliente"]
  LEG[("Sistema heredado<br/>BLOQUEANTE CN-20")]

  D ==>|"CT-03 · sincronización idempotente"| F
  F ==>|"CT-01 · catálogo completo versionado"| D
  D <-.->|"CT-02 · mismo motor de reglas"| F
  W -->|"consulta y administración"| F
  F ==>|"CT-05 · salida limitada por empresa"| BI
  F ==>|"respaldo cifrado con llave del cliente"| C
  C ==>|"CT-06 · mismo paquete, 0 cambios de código"| F
  LEG -.->|"carga inicial · BLOQUEADO"| F

  classDef alerta fill:#FCE4D6,stroke:#C55A11,stroke-width:2px,color:#000
  classDef ext fill:#EDEDED,stroke:#808080,color:#000
  class LEG alerta
  class BI ext
```

---

## D2 · N1 · Dispositivo de campo

El camino del dato hasta la cola de salida. **Valida antes de guardar**, y ese orden es el driver #1:
validar al sincronizar convierte un error de diez segundos en un error de ocho días (`RF-004`, `CN-22`).

`[!]` El cifrado en reposo va en naranja porque es `A5`, una decisión tomada **en contra de una
respuesta escrita del cliente**, y porque con PWA no se puede demostrar documentalmente.

```mermaid
flowchart TB
  CAP(["Supervisor de campo"])
  QR["Captura de identificador<br/>físico QR"]
  IAMD["IAM · verificación de<br/>permisos offline"]
  IAD["Runtime de inferencia local<br/>asistente de captura"]
  APP["Framework de aplicación<br/>móvil de captura"]
  REGD["Motor de reglas<br/>instancia dispositivo"]
  CATD[("Catálogo local<br/>versionado")]
  ALMD[("Almacén embebido<br/>en el dispositivo")]
  CIFD["Cifrado en reposo<br/>del dispositivo"]
  SYNCD["Motor de sincronización<br/>cliente"]
  NODO(["Al nodo de finca<br/>CT-03"])
  CATN(["Del nodo de finca<br/>CT-01"])

  CAP --> APP
  QR -->|"identifica la cama"| APP
  IAMD -->|"quién es y qué puede hacer"| APP
  IAD -.->|"propone · el usuario confirma"| APP
  APP -->|"1 · valida antes de guardar"| REGD
  REGD -->|"reglas y rangos vigentes"| CATD
  REGD -->|"rechaza con el motivo<br/>en lenguaje del negocio"| APP
  APP -->|"2 · confirma y guarda"| ALMD
  APP -->|"nombres de la finca"| CATD
  CIFD -.->|"cifra en reposo · decisión A5"| ALMD
  ALMD -->|"3 · cola de salida"| SYNCD
  SYNCD ==> NODO
  CATN ==> CATD

  classDef alerta fill:#FCE4D6,stroke:#C55A11,stroke-width:2px,color:#000
  classDef datos fill:#E2EFDA,stroke:#548235,color:#000
  classDef seg fill:#FFF2CC,stroke:#BF8F00,color:#000
  classDef borde fill:#FFFFFF,stroke:#404040,stroke-dasharray:4 3,color:#000
  class CIFD alerta
  class CATD,ALMD datos
  class IAMD seg
  class NODO,CATN,CAP borde
```

---

## D3 · N2 · Nodo de finca

La instalación: es el producto. Autoridad de todo el dato operativo y opera sin internet.

Dos cosas que el diagrama hace explícitas: el **snapshot inmutable** de `CT-04` —el motor no lee la
tabla viva de parámetros, porque una proyección publicada no puede cambiar por debajo (`CN-27`)—
y que **el Key Vault es lo que hace posible que la nube custodie sin poder leer**.

```mermaid
flowchart TB
  DISP(["Dispositivos de campo<br/>CT-03"])
  WEB(["Puesto de consulta"])
  GW["API Gateway"]

  subgraph LOG["Framework de servicios · backend"]
    direction LR
    SYNCS["Motor de sincronización<br/>ingesta"]
    REGS["Motor de reglas<br/>instancia servidor"]
    JOB["Planificador de tareas<br/>Job Scheduler"]
    DOC["Generación de documentos<br/>Excel y PDF"]
    OUT["Interfaz de salida<br/>de datos"]
    NOT["Notification<br/>Gateway"]
    MDMD["Gestión de<br/>dispositivos"]
  end

  subgraph DAT["Datos y persistencia"]
    direction LR
    CAT[("Datos maestros y<br/>parametrización versionada")]
    AUD[("Registro de auditoría<br/>inmutable")]
    DB[("Base de datos<br/>operacional")]
    COLD[("Archivo frío")]
  end

  subgraph SEG["Seguridad"]
    direction LR
    IAMS["Identity and Access<br/>Management · RBAC"]
    SES["Emisión de<br/>credenciales"]
    KV["Key Vault<br/>llave del cliente"]
    CIF["Cifrado en tránsito<br/>y en reposo"]
  end

  MIG["Migraciones<br/>de esquema"]
  BKP["Respaldo y<br/>recuperación"]

  BI(["Herramienta de BI<br/>del cliente · CT-05"])
  ADISP(["A los dispositivos"])
  ANUBE(["A la nube"])

  DISP ==> GW
  WEB --> GW
  GW --> LOG

  SYNCS -->|"gana el más reciente"| DB
  SYNCS -->|"una entrada por sesión"| AUD
  REGS -->|"reglas vigentes"| CAT
  CAT ==>|"CT-04 · snapshot inmutable"| JOB
  JOB -->|"proyección al menos semanal"| DB
  DOC --> DB
  OUT --> DB
  AUD --> DB
  CAT --> DB
  DB --> COLD

  IAMS --> SES
  IAMS -.->|"rol + empresa"| GW
  KV --> CIF
  CIF -.->|"cifra en reposo"| DB

  MIG -.->|"versionadas y verificables"| DB
  BKP --> DB
  CIF -.->|"cifra el respaldo"| BKP
  JOB -->|"respaldo automático"| BKP

  OUT ==> BI
  SES --> ADISP
  NOT --> ADISP
  MDMD --> ADISP
  CAT ==>|"CT-01"| ADISP
  BKP ==> ANUBE

  classDef datos fill:#E2EFDA,stroke:#548235,color:#000
  classDef seg fill:#FFF2CC,stroke:#BF8F00,color:#000
  classDef borde fill:#FFFFFF,stroke:#404040,stroke-dasharray:4 3,color:#000
  class CAT,DB,AUD,COLD datos
  class IAMS,SES,KV,CIF seg
  class DISP,WEB,BI,ADISP,ANUBE borde
```

---

## D4 · N4 · Servicios en línea y consumidores externos

Es la mensualidad. Todo lo de aquí puede caerse una semana sin que la finca deje de operar.

`[!]` El servicio de IA analítica va en naranja y con un enlace tachado hacia el Key Vault: **un
servicio de nube que no puede leer lo que custodia no puede analizarlo.** O corre en el nodo de
finca —y deja de ser servicio de nube, y encarece la instalación— o los datos salen en claro —y
`CN-28` y `CN-03` se debilitan—. Esta contradicción no está registrada en ningún otro archivo
del proyecto.

```mermaid
flowchart LR
  subgraph FIN["N2 · Nodo de finca"]
    BKP["Respaldo y recuperación"]
    OBS["Observabilidad"]
    PKG["Empaquetado y orquestación"]
    MDMD["Gestión de dispositivos"]
    OUT["Interfaz de salida de datos"]
    KV["Key Vault · llave del cliente"]
  end

  subgraph NUBE["N4 · Servicios en línea sobre Infraestructura en la nube — puede caerse una semana sin parar la finca"]
    S3BK[("Custodia de respaldos<br/>cifrados y opacos")]
    REL["Distribución de versiones<br/>y actualización remota"]
    CICD["Integración y despliegue<br/>continuos"]
    SOP["Panel de soporte remoto"]
    IAN["Servicio de IA analítica<br/>EN CONFLICTO"]
  end

  BI["Herramienta de BI del cliente"]

  BKP ==>|"cifrado con la llave del cliente"| S3BK
  OBS -->|"telemetría sin datos de negocio"| SOP
  CICD --> REL
  REL ==>|"CT-06 · mismo paquete en nube y en sitio"| PKG
  REL -->|"versión de la app de captura"| MDMD
  OUT ==>|"CT-05 · autenticada y limitada a la empresa"| BI
  IAN x-.-x|"no puede leer lo que custodia"| KV

  classDef alerta fill:#FCE4D6,stroke:#C55A11,stroke-width:2px,color:#000
  classDef datos fill:#E2EFDA,stroke:#548235,color:#000
  classDef seg fill:#FFF2CC,stroke:#BF8F00,color:#000
  classDef ext fill:#EDEDED,stroke:#808080,color:#000
  class IAN alerta
  class S3BK datos
  class KV seg
  class BI ext
```

---

## D5 · El camino de una captura, de punta a punta

La vista que más sirve para discutir con el cliente, porque no habla de componentes sino de lo que
le pasa a un dato suyo. Enseña las tres cosas que el sistema promete: **se corrige en el momento y
no en ocho días**, **puede pasar quince días en la cola sin perderse**, y **la proyección se publica
sin tocar las anteriores**.

```mermaid
sequenceDiagram
  autonumber
  actor SUP as Supervisor de campo
  participant APP as App de captura
  participant REG as Motor de reglas<br/>(dispositivo)
  participant ALM as Almacén embebido
  participant SYN as Sincronización<br/>(cliente)
  participant GW as API Gateway
  participant ING as Ingesta<br/>(servidor)
  participant AUD as Auditoría
  participant DB as Base de datos
  participant JOB as Job Scheduler
  participant WEB as Consulta web

  Note over SUP,ALM: EN EL INVERNADERO — sin conexión de datos (CN-17)
  SUP->>APP: captura la cama 37, secciones y líneas
  APP->>REG: valida contra el catálogo local
  alt regla dura violada
    REG-->>APP: rechaza y explica el motivo (RF-004)
    APP-->>SUP: corrige en el momento, no en 8 días
  else válido
    REG-->>APP: acepta, con avisos blandos si los hay
  end
  APP->>ALM: guarda y confirma en pantalla
  ALM->>SYN: entra en la cola de salida
  Note over SYN: puede pasar hasta 15 días aquí (B1, A6)

  Note over SYN,DB: AL LLEGAR A LA OFICINA — hay red
  SYN->>GW: CT-03 · sesión idempotente y reanudable
  GW->>ING: entrega los registros de la sesión
  ING->>AUD: quién, desde qué dispositivo, qué entró
  ING->>DB: aplica · gana el más reciente (RF-022)
  ING-->>SYN: confirma lo aplicado
  SYN-->>APP: marca los registros como sincronizados
  opt su registro fue descartado
    ING-->>APP: aviso a quien capturó
  end

  Note over JOB,WEB: PROYECCIÓN Y CONSULTA
  JOB->>DB: lee con el snapshot de parámetros (CT-04)
  JOB->>DB: publica una versión nueva, sin tocar las anteriores
  WEB->>DB: proyectado contra real por día, semana y mes
  Note over WEB: objetivo: 1 hora desde la sincronización<br/>línea base actual: 8 días (B2)
```

---

*Modelo de componentes v1.0 · propuesta del equipo, sin validar con el cliente · 31-ago-2026.*
