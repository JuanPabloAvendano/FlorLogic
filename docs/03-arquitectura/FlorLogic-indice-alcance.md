# FlorLogic — Índice de alcance del modelo ArchiMate

Generado el 15-ago-2026 (tras las tandas 1 y 2 de depuración) desde `FlorLogic-modelo.archimate` — 236 elementos.

**Para qué sirve este archivo.** El `.archimate` es un XML incómodo de revisar. Este índice es su espejo en texto: la misma lista de elementos, ordenada por alcance, para que puedas marcar segmento por segmento qué entra y qué no. Cuando corrijas algo aquí, se corrige en el modelo, no al revés.

**Cómo usarlo.** Recorre primero la sección `EN DUDA` (48 elementos): es donde el alcance no está decidido. Después `FUERA-F1`, para confirmar que lo excluido sigue excluido. `DENTRO` y `CONTEXTO` son revisión de rutina.

## Resumen

| Alcance | Elementos | Qué significa |
|---|---:|---|
| `EN DUDA` | 48 | Depende de una brecha abierta o de una decisión sin cerrar. **Esta es la lista que hay que resolver.** |
| `CANDIDATO` | 13 | Propuesto por el equipo a partir de una decisión, **sin validar con el cliente** y fuera del catálogo vigente. |
| `FUERA-CATALOGO` | 0 | Requisito real que NO está en el catálogo de trabajo vigente. Hay que decidir si vuelve. |
| `DENTRO` | 120 | El sistema lo maneja en la fase 1. |
| `CERRADO` | 4 | Era una brecha o una contradicción y ya se resolvió. Se conserva con la decisión escrita. |
| `FUERA-F1` | 11 | Decisión explícita de dejarlo fuera de la fase 1. |
| `CONTEXTO` | 30 | Existe en el mundo real pero el sistema no lo gestiona. |
| `METODO` | 10 | Regla de cómo se trabaja, no del producto. No va a la entrega de arquitectura. |
| **Total** | **236** | |

## Elementos con inconsistencia declarada `[!]`

Son 85. Cada uno tiene una contradicción o un hueco escrito y NO resuelto en su documentación dentro de Archi. Buscar `[!]` en Archi los lista todos.

| Elemento | Capa | Alcance |
|---|---|---|
| Accesibilidad | Motivation | `EN DUDA` |
| Aislamiento entre empresas | Application | `EN DUDA` |
| Auditar calidad de datos | Business | `EN DUDA` |
| Banda de exactitud (±10%) | Business | `EN DUDA` |
| CN-05 · Modelo de suscripción y mantenimiento sin definir | Motivation | `EN DUDA` |
| CN-11 · Restricción técnica impuesta: PayU | Motivation | `EN DUDA` |
| Control de exactitud | Business | `EN DUDA` |
| Control de exactitud | Application | `EN DUDA` |
| Cálculo de proyección | Application | `EN DUDA` |
| Desviación proyección vs. corte real | Business | `EN DUDA` |
| Escalabilidad | Motivation | `EN DUDA` |
| Escenarios de calidad | Implementation & Migration | `EN DUDA` |
| Experiencia de usuario | Motivation | `EN DUDA` |
| Fase 2 · Lanzamiento SaaS multi-tenant | Implementation & Migration | `EN DUDA` |
| Hueco lógico: el clima corre el ciclo pero no se usa | Motivation | `EN DUDA` |
| Interoperatividad | Motivation | `EN DUDA` |
| Los escenarios de calidad no están escritos | Motivation | `EN DUDA` |
| Motor de proyección | Application | `EN DUDA` |
| Portabilidad | Motivation | `EN DUDA` |
| Registrar baja de producción | Business | `EN DUDA` |
| Registro marcado como erróneo | Business | `EN DUDA` |
| Repositorio central | Application | `EN DUDA` |
| Riesgo §9.2 · Todo descansa en una sola voz | Motivation | `EN DUDA` |
| Seguridad | Motivation | `EN DUDA` |
| Seguridad de funcionamiento (safety) | Motivation | `EN DUDA` |
| Trazabilidad | Motivation | `EN DUDA` |
| Asistente de captura offline (IA local) | Application | `CANDIDATO` |
| IA analítica en la nube | Application | `CANDIDATO` |
| RFP-01 · Registrar la siembra por SECCIÓN de cama | Motivation | `CANDIDATO` |
| RFP-02 · Registrar el motivo de la disminución de producción | Motivation | `CANDIDATO` |
| RFP-06 · Consultas y análisis asistidos por IA en la nube | Motivation | `CANDIDATO` |
| RFP-07 · Plantillas de captura configurables por la empresa | Motivation | `CANDIDATO` |
| Administrador de la empresa (tenant) | Business | `DENTRO` |
| Autenticación y permisos offline | Application | `DENTRO` |
| Automatizar primero, permitir mediación humana como opción | Motivation | `DENTRO` |
| Base de datos por empresa | Technology & Physical | `DENTRO` |
| CN-12 · Restricción técnica adoptada: RBAC con partición por empresa | Motivation | `DENTRO` |
| CN-16 · Una base de datos por empresa, con esquema común | Motivation | `DENTRO` |
| Capturar dato en campo | Business | `DENTRO` |
| Catálogo de la empresa descargado | Application | `DENTRO` |
| Conflicto de sincronización | Business | `DENTRO` |
| Corregir dato erróneo | Business | `DENTRO` |
| Exportación (Excel / PDF) | Business | `DENTRO` |
| Finca | Business | `DENTRO` |
| Gestión de empresas y suscripciones | Application | `DENTRO` |
| Motivo de disminución de producción | Business | `DENTRO` |
| Módulo de BI y tableros propios | Application | `DENTRO` |
| Módulo de auditoría y trazabilidad | Application | `DENTRO` |
| Operador de la plataforma (equipo FlorLogic) | Business | `DENTRO` |
| Plataforma SaaS multi-tenant | Technology & Physical | `DENTRO` |
| Proyección de producción y ventas | Business | `DENTRO` |
| RF-001 · Registrar siembra de una cama sin conexión | Motivation | `DENTRO` |
| RF-002 · Registrar corte por variedad, grado y fecha sin conexión | Motivation | `DENTRO` |
| RF-006 · Calcular tallos proyectados de una cama | Motivation | `DENTRO` |
| RF-007 · Distribuir los tallos sobre los días de corte | Motivation | `DENTRO` |
| RF-009 · Registrar erradicación y descontar hacia adelante | Motivation | `DENTRO` |
| RF-011 · Mostrar la desviación entre corte real y proyectado | Motivation | `DENTRO` |
| RF-012 · Aislamiento total entre empresas (multi-tenant) | Motivation | `DENTRO` |
| RF-014 · Autenticar y aplicar permisos de rol sin conexión | Motivation | `DENTRO` |
| RF-017 · Solo el administrador modifica un registro sincronizado | Motivation | `DENTRO` |
| RF-020 · Descargar la parametrización más reciente al dispositivo | Motivation | `DENTRO` |
| RF-022 · Resolver conflictos de sincronización: automático por defecto, mediación opcional | Motivation | `DENTRO` |
| RF-023 · Congelar los parámetros de cálculo de una proyección | Motivation | `DENTRO` |
| Reducir la brecha entre proyección y corte real | Motivation | `DENTRO` |
| Respaldo por empresa | Technology & Physical | `DENTRO` |
| Sección de cama | Business | `DENTRO` |
| Servicio de sincronización | Application | `DENTRO` |
| Variedad | Business | `DENTRO` |
| Viabilidad comercial del producto SaaS | Motivation | `DENTRO` |
| DEC-07 · El dominio excluye precios y rendimiento económico | Motivation | `CERRADO` |
| DEC-13 · Alcance del BI resuelto: seis reportes, sin integrar la app de plagas | Motivation | `CERRADO` |
| DEC-16 · La IA vuelve, partida en dos | Motivation | `CERRADO` |
| Datos climáticos (pluviometría, temperatura) | Business | `FUERA-F1` |
| Botón color alcanzado | Business | `CONTEXTO` |
| Desbotonar | Business | `CONTEXTO` |
| Desviación entre proyección y corte real | Motivation | `CONTEXTO` |
| Director de Producción | Business | `CONTEXTO` |
| Equipo FlorLogic | Business | `CONTEXTO` |
| Error de captura: 2% | Motivation | `CONTEXTO` |
| Estación meteorológica | Technology & Physical | `CONTEXTO` |
| Ingeniero de sistemas | Business | `CONTEXTO` |
| FuncionalidadesSignificativas.xlsx | Implementation & Migration | `METODO` |
| Los identificadores no se reutilizan | Motivation | `METODO` |
| MINI QAW PLANTILLA NO TERMINADA.xlsx | Implementation & Migration | `METODO` |
| RestriccionesNegocio.xlsx | Implementation & Migration | `METODO` |

---

## EN DUDA — 48 elementos

Depende de una brecha abierta o de una decisión sin cerrar. **Esta es la lista que hay que resolver.**

### Business · 03 Proyección, presupuesto y venta

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Curva de reparto del corte | BusinessObject | ID: BR-23, H-14 · Origen: EQ · Confianza: PROP |
| ☐ | Desviación proyección vs. corte real `[!]` | BusinessObject | ID: RF-011, BR-21 · Origen: S2 · Confianza: CONTRAD |

### Business · 04 Parámetros del motor

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Productividad esperada | BusinessObject | ID: BR-23 · Origen: S2 · Confianza: INF |
| ☐ | Banda de exactitud (±10%) `[!]` | BusinessObject | ID: BR-21, §3.3 · Origen: S2 · Confianza: CONTRAD |

### Business · 05 Procesos y eventos

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Registrar baja de producción `[!]` | BusinessProcess | ID: RF-010 · Origen: S2 · Confianza: CONF |
| ☐ | Auditar calidad de datos `[!]` | BusinessProcess | ID: H-33, RF-C19 · Origen: S2 · Confianza: CONF |

### Business · 08 Auditoría y calidad del dato

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Registro marcado como erróneo `[!]` | BusinessObject | ID: RF-C19, H-33 · Origen: S2 · Confianza: CONF |

### Business · 09 Servicios de negocio

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Control de exactitud `[!]` | BusinessService | ID: RF-011, BR-21 · Origen: S2 · Confianza: CONTRAD |

### Application · 01 FlorLogic

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Motor de proyección `[!]` | ApplicationComponent | ID: RF-006, RF-007, RF-008, RF-023 · Origen: EQ · Confianza: PROP |
| ☐ | Repositorio central `[!]` | ApplicationComponent | ID: H-40, RF-012 · Origen: S2 · Confianza: CONF |

### Application · 02 Servicios de aplicación

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Cálculo de proyección `[!]` | ApplicationService | ID: RF-006, RF-007 · Origen: S2 · Confianza: INF |
| ☐ | Control de exactitud `[!]` | ApplicationService | ID: RF-011 · Origen: S2 · Confianza: CONTRAD |
| ☐ | Aislamiento entre empresas `[!]` | ApplicationService | ID: RF-012 · Origen: EQ · Confianza: PROP |

### Application · —

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Sistema actual (SIN IDENTIFICAR) | ApplicationComponent | ID: H-35, H-39, BR-N2 · Origen: S1, S2 · Confianza: CONF |

### Motivation · 03 Atributos de calidad

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Confiabilidad | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Experiencia de usuario `[!]` | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Disponibilidad | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Interoperatividad `[!]` | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Seguridad `[!]` | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Rendimiento | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Capacidad para ser soportado | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Capacidad para ser administrado | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Trazabilidad `[!]` | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Capacidad | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Accesibilidad `[!]` | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Portabilidad `[!]` | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Escalabilidad `[!]` | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |
| ☐ | Seguridad de funcionamiento (safety) `[!]` | Goal | Prioridad: SIN DEFINIR. Se re-elabora bajo modelo SaaS (DEC-03). Los cuatro rankings previos quedan como histórico en el mini Q… |

### Motivation · 04 Brechas, contradicciones y riesgos

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | BR-N3 · Documentos prometidos no entregados | Assessment | Estado: Abierta · Bloquea: TODO |
| ☐ | BR-21 · Nivel de agregación del -6% / ±10% | Assessment | Estado: Abierta · Bloquea: RF-011 |
| ☐ | BR-23 · Origen de la productividad y curva de reparto | Assessment | Estado: Abierta · Bloquea: RF-006, RF-007 |
| ☐ | BR-N2 · El sistema actual sigue sin identificarse | Assessment | Estado: Abierta · Bloquea: RF-003, RF-013 |
| ☐ | BR-22 · ¿9 variedades o ~300? | Assessment | Estado: Abierta · Bloquea: RF-013, RF-020 |
| ☐ | BR-11 · Catálogo de grados y calidades | Assessment | Estado: Abierta · Bloquea: RF-002, RF-013 |
| ☐ | BR-24 · Accesibilidad en el puesto 10 de 14 | Assessment | Estado: Abierta · Bloquea: RF-024, asistente |
| ☐ | BR-N1 · Tiempo máximo de confirmación de captura | Assessment | Estado: Abierta · Bloquea: RF-001, RF-002 |
| ☐ | BR-N4 · ¿Dos capturadores en la misma cama y fecha? | Assessment | Estado: No preguntada · Bloquea: RF-022 |
| ☐ | BR-N5 · Ventana de sesión offline y pérdida del dispositivo | Assessment | Estado: No preguntada · Bloquea: RF-014 |
| ☐ | Riesgo §9.2 · Todo descansa en una sola voz `[!]` | Assessment | Estado: Asumido · Bloquea: Validez general del levantamiento |
| ☐ | Atributos de calidad pendientes de re-elaborar bajo SaaS | Assessment | Estado: Abierta · Bloquea: Escenarios de calidad, decisiones de arquitectura, trade-offs |
| ☐ | BR-N6 · El proceso de captura a detalle no se ha definido con el cliente | Assessment | Estado: Abierta · Bloquea: RFP-01 a RFP-05, redacción de RF-001 y RF-002 |
| ☐ | Cifrado de los respaldos: promesa o propiedad | Assessment | Estado: Abierta · Bloquea: DEC-09, RFP-08, contrato |
| ☐ | Hueco lógico: el clima corre el ciclo pero no se usa `[!]` | Assessment | Estado: Abierta · Bloquea: Motor de proyección |
| ☐ | Los escenarios de calidad no están escritos `[!]` | Assessment | Estado: Abierta · Bloquea: Entrega académica |

### Motivation · 06 Restricciones

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | CN-05 · Modelo de suscripción y mantenimiento sin definir `[!]` | Constraint | Tipo: Presupuesto |
| ☐ | CN-11 · Restricción técnica impuesta: PayU `[!]` | Constraint | Tipo: Técnica impuesta |

### Implementation & Migration · —

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Escenarios de calidad `[!]` | Deliverable | PENDIENTE · formato acordado 12-ago-2026 |
| ☐ | Fase 2 · Lanzamiento SaaS multi-tenant `[!]` | WorkPackage | ID: DEC-01 · Origen: EQ · Confianza: PROP |

---

## CANDIDATO — 13 elementos

Propuesto por el equipo a partir de una decisión, **sin validar con el cliente** y fuera del catálogo vigente.

### Application · 01 FlorLogic

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Asistente de captura offline (IA local) `[!]` | ApplicationComponent | ID: DEC-16, RFP-05 · Origen: EQ, S? · Confianza: PROP |
| ☐ | IA analítica en la nube `[!]` | ApplicationComponent | ID: DEC-16, RFP-06 · Origen: EQ · Confianza: PROP |
| ☐ | Vista geométrica de camas | ApplicationComponent | ID: DEC-15, RFP-03 · Origen: S1, EQ · Confianza: PROP |

### Application · 02 Servicios de aplicación

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Asistencia de captura sin conexión | ApplicationService | ID: DEC-16, RFP-05 · Origen: EQ · Confianza: PROP |
| ☐ | Consultas y análisis asistidos | ApplicationService | ID: DEC-16, RFP-06 · Origen: EQ · Confianza: PROP |

### Motivation · 08 Requisitos candidatos (sin validar)

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | RFP-01 · Registrar la siembra por SECCIÓN de cama `[!]` | Requirement | Módulo: Captura en campo · Origen: DEC-14 · Propuesto 15-ago-2026 · Reemplazaría el enunciado de RF-001 |
| ☐ | RFP-02 · Registrar el motivo de la disminución de producción `[!]` | Requirement | Módulo: Erradicaciones y bajas · Origen: DEC-13 · Propuesto 15-ago-2026 · Complementa RF-009 |
| ☐ | RFP-03 · Mostrar el estado de cada cama en porcentaje sobre una vista geométrica | Requirement | Módulo: Consulta y reportes · Origen: DEC-15 · Propuesto 15-ago-2026 · Recupera la idea del mapa de calor de S1 |
| ☐ | RFP-04 · Presentar la producción en dos lecturas: tallos aproximados y % de plantas reales | Requirement | Módulo: Consulta y reportes · Origen: DEC-15 · Propuesto 15-ago-2026 · Complementa RF-006 y RF-018 |
| ☐ | RFP-05 · Asistente de captura offline que ayuda a llenar el formulario | Requirement | Módulo: Captura asistida · Origen: DEC-16 · Propuesto 15-ago-2026 · Sustituye a RF-C20 con alcance acotado |
| ☐ | RFP-06 · Consultas y análisis asistidos por IA en la nube `[!]` | Requirement | Módulo: BI y analítica · Origen: DEC-16 · Propuesto 15-ago-2026 · Nuevo |
| ☐ | RFP-07 · Plantillas de captura configurables por la empresa `[!]` | Requirement | Módulo: Parametrización · Origen: DEC-16 · Propuesto 15-ago-2026 · NO en la primera entrega · Fase posterior |
| ☐ | RFP-08 · Restaurar los datos de una empresa sin afectar a las demás | Requirement | Módulo: Plataforma multi-tenant · Origen: DEC-11, DEC-12 · Propuesto 15-ago-2026 · Nuevo |

---

## DENTRO — 120 elementos

El sistema lo maneja en la fase 1.

### Business · 01 Estructura física y catálogo

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Finca `[!]` | BusinessObject | ID: H-01, H-04 · Origen: S1 · Confianza: CONF |
| ☐ | Bloque | BusinessObject | ID: H-02, H-05 · Origen: S2 · Confianza: CONF |
| ☐ | Nave | BusinessObject | ID: H-04 · Origen: S1 · Confianza: INF |
| ☐ | Cama | BusinessObject | ID: H-03, H-09 · Origen: S2 · Confianza: CONF |
| ☐ | Variedad `[!]` | BusinessObject | ID: H-06, BR-22 · Origen: S1, S2 · Confianza: CONTRAD |
| ☐ | Subvariedad | BusinessObject | ID: H-06 · Origen: S1 · Confianza: CONF |
| ☐ | Color | BusinessObject | ID: H-06 · Origen: S1 · Confianza: CONF |
| ☐ | Esqueje | BusinessObject | ID: Glosario §11 · Origen: S1 · Confianza: CONF |
| ☐ | Empresa / Tenant | BusinessObject | ID: DEC-01, H-46, RF-012 · Origen: EQ · Confianza: CONF |
| ☐ | Parametrización de la empresa | BusinessObject | ID: RF-013, D-06 · Origen: S2 · Confianza: CONF |
| ☐ | Sección de cama `[!]` | BusinessObject | ID: DEC-14, H-06 · Origen: EQ · Confianza: PROP |

### Business · 02 Ciclo y producción

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Ciclo fenológico | BusinessObject | ID: H-12, H-13, H-17 · Origen: S1, S2 · Confianza: CONF |
| ☐ | Registro de siembra | BusinessObject | ID: D-01 · Origen: S2 · Confianza: CONF |
| ☐ | Registro de producción | BusinessObject | ID: D-01 · Origen: S2 · Confianza: CONF |
| ☐ | Tallo | BusinessObject | ID: §3.1 · Origen: S1, S2 · Confianza: CONF |
| ☐ | Grado | BusinessObject | ID: H-46, BR-11 · Origen: S1 · Confianza: CONF |
| ☐ | Calidad | BusinessObject | ID: H-46 · Origen: S1 · Confianza: CONF |
| ☐ | Catálogo de grados y calidades | BusinessObject | ID: BR-11 · Origen: EQ · Confianza: PROP |
| ☐ | Baja de producción | BusinessObject | ID: D-03, Glosario §11 · Origen: S2 · Confianza: CONF |
| ☐ | Registro de erradicación | BusinessObject | ID: H-20, H-21, H-22, D-03 · Origen: S2 · Confianza: CONF |
| ☐ | Motivo de disminución de producción `[!]` | BusinessObject | ID: DEC-13, RFP-02 · Origen: EQ · Confianza: PROP |
| ☐ | Estado de producción de la cama (%) | BusinessObject | ID: DEC-15, RFP-03, RFP-04 · Origen: EQ · Confianza: PROP |
| ☐ | Marca de tiempo de captura | BusinessObject | ID: RF-021 · Origen: EQ · Confianza: PROP |

### Business · 03 Proyección, presupuesto y venta

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Proyección de producción | BusinessObject | ID: D-02, H-47, H-49 · Origen: S1, S2 · Confianza: CONF |
| ☐ | Versión de proyección | BusinessObject | ID: RF-008, RF-023 · Origen: S2, EQ · Confianza: INF |
| ☐ | Exportación (Excel / PDF) `[!]` | BusinessObject | ID: RF-019 · Origen: EQ · Confianza: PROP |

### Business · 04 Parámetros del motor

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Área de cama (m²) | BusinessObject | ID: H-09 · Origen: S2 · Confianza: CONF |
| ☐ | Densidad de siembra | BusinessObject | ID: H-10, H-11 · Origen: S2 · Confianza: CONF |
| ☐ | Plantas sembradas | BusinessObject | ID: §3.1 · Origen: S2 · Confianza: CONF |
| ☐ | Días a corte | BusinessObject | ID: H-15, D-06 · Origen: S2 · Confianza: CONF |

### Business · 05 Procesos y eventos

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Erradicar cama | BusinessProcess | Termino cliente: erradicación · ID: H-18, H-19, H-23 · Origen: S1, S2 · Confianza: CONF |
| ☐ | Capturar dato en campo `[!]` | BusinessProcess | ID: H-24, H-29, D-01, BR-N1 · Origen: S1, S2 · Confianza: CONF |
| ☐ | Corregir dato erróneo `[!]` | BusinessProcess | ID: H-27, H-32, H-33 · Origen: S2 · Confianza: CONF |
| ☐ | Proyectar producción | BusinessProcess | ID: D-02, §3.1 · Origen: S2 · Confianza: CONF |
| ☐ | Ajustar proyección | BusinessProcess | ID: D-03, H-49 · Origen: S2 · Confianza: CONF |
| ☐ | Proyectar disponibilidad de flor | BusinessProcess | ID: D-04, D-07 · Origen: S2 · Confianza: CONF |
| ☐ | Parametrizar variedades y márgenes | BusinessProcess | ID: RF-013, D-06 · Origen: S2 · Confianza: CONF |
| ☐ | Exportar información | BusinessProcess | ID: RF-019 · Origen: EQ · Confianza: PROP |
| ☐ | Erradicación registrada | BusinessEvent | ID: H-20, RF-009 · Origen: S2 · Confianza: CONF |

### Business · 07 Roles del sistema (los 3 validados)

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Supervisor de campo | BusinessRole | ID: §5 · Origen: S2 · Confianza: CONF |
| ☐ | Administrador de producción y ventas | BusinessRole | ID: §5 · Origen: S2 · Confianza: CONF |
| ☐ | Administrador de la empresa (tenant) `[!]` | BusinessRole | ID: DEC-01, RF-013, RF-017 · Origen: EQ · Confianza: PROP |
| ☐ | Operador de la plataforma (equipo FlorLogic) `[!]` | BusinessRole | ID: DEC-01 · Origen: EQ · Confianza: PROP |

### Business · 08 Auditoría y calidad del dato

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Registro de auditoría | BusinessObject | ID: RF-016, RF-017 · Origen: S2 · Confianza: CONF |
| ☐ | Conflicto de sincronización `[!]` | BusinessObject | ID: DEC-05, RF-022, BR-N4 · Origen: EQ · Confianza: PROP |

### Business · 09 Servicios de negocio

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Captura de datos en campo | BusinessService | ID: D-01 · Origen: S2 · Confianza: CONF |
| ☐ | Proyección de producción y ventas `[!]` | BusinessService | ID: D-02, D-04 · Origen: S2 · Confianza: CONF |
| ☐ | Trazabilidad y auditoría | BusinessService | ID: D-05, RF-016 · Origen: S2 · Confianza: CONF |

### Application · 01 FlorLogic

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | App móvil de campo (offline-first) | ApplicationComponent | ID: RF-001..005, RF-014, RF-020, RF-021 · Origen: EQ · Confianza: PROP |
| ☐ | Aplicación web | ApplicationComponent | ID: H-40, RF-011, RF-018, RF-019 · Origen: S2 · Confianza: CONF |
| ☐ | Servicio de sincronización `[!]` | ApplicationComponent | ID: RF-003, RF-020, RF-022 · Origen: EQ · Confianza: PROP |
| ☐ | Módulo de parametrización | ApplicationComponent | ID: RF-013 · Origen: S2 · Confianza: CONF |
| ☐ | Módulo de auditoría y trazabilidad `[!]` | ApplicationComponent | ID: RF-016, RF-017, RF-021 · Origen: S2 · Confianza: CONF |
| ☐ | Módulo de BI y tableros propios `[!]` | ApplicationComponent | ID: DEC-06, CN-14, RF-018, RF-024 · Origen: EQ · Confianza: PROP |
| ☐ | Gestión de empresas y suscripciones `[!]` | ApplicationComponent | ID: DEC-01, RF-012, CN-11 · Origen: EQ · Confianza: PROP |
| ☐ | Módulo de exportación | ApplicationComponent | ID: RF-019 · Origen: EQ · Confianza: PROP |

### Application · 02 Servicios de aplicación

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Captura offline | ApplicationService | ID: RF-001, RF-002 · Origen: S2 · Confianza: CONF |
| ☐ | Validación de reglas en el dispositivo | ApplicationService | ID: RF-004, RF-005 · Origen: S2 · Confianza: CONF |
| ☐ | Sincronización exactamente-una-vez | ApplicationService | ID: RF-003 · Origen: S2 · Confianza: CONF |
| ☐ | Autenticación y permisos offline `[!]` | ApplicationService | ID: RF-014, RF-015 · Origen: S2 · Confianza: INF |
| ☐ | Tableros y analítica embebida | ApplicationService | ID: DEC-06, RF-018, RF-024 · Origen: EQ · Confianza: PROP |
| ☐ | Exportación con restricciones de rol | ApplicationService | ID: RF-019 · Origen: EQ · Confianza: PROP |

### Application · 04 Datos

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Registro pendiente de sincronizar | DataObject | ID: RF-003 · Origen: S2 · Confianza: CONF |
| ☐ | Catálogo de la empresa descargado `[!]` | DataObject | ID: RF-020 · Origen: EQ · Confianza: PROP |
| ☐ | Parámetros congelados de una proyección | DataObject | ID: RF-023 · Origen: EQ · Confianza: PROP |

### Application · —

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | FlorLogic | ApplicationComponent | ID: H-40 · Origen: S2 · Confianza: CONF |

### Technology & Physical · —

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Dispositivo móvil de campo | Device | ID: D-01, §7.2 · Origen: EQ · Confianza: INF |
| ☐ | Plataforma SaaS multi-tenant `[!]` | Node | ID: DEC-01 · Origen: EQ · Confianza: CONF |
| ☐ | Base de datos por empresa `[!]` | Node | ID: DEC-11, CN-16, RFP-08 · Origen: EQ · Confianza: PROP |
| ☐ | Respaldo por empresa `[!]` | Artifact | ID: DEC-09, DEC-12, CN-15 · Origen: EQ · Confianza: PROP |

### Motivation · 01 Drivers (dolores medidos)

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Viabilidad comercial del producto SaaS `[!]` | Driver | ID: §8.1, DEC-01 · Origen: S1, EQ · Confianza: PROP |

### Motivation · 02 Metas de negocio

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Reducir la brecha entre proyección y corte real `[!]` | Goal | ID: §3.3 · Origen: S2 · Confianza: CONF |
| ☐ | Eliminar el paso de digitación y la latencia de 8 días | Goal | ID: H-25, H-26, H-31 · Origen: S1, S2 · Confianza: INF |
| ☐ | Llevar el error de captura del 2% al 0% | Goal | ID: H-33 · Origen: S2 · Confianza: CONF |
| ☐ | Ajustar la proyección al menos semanalmente | Goal | ID: H-49 · Origen: S2 · Confianza: CONF |
| ☐ | Producto SaaS vendible a varias fincas | Goal | ID: DEC-01 · Origen: EQ · Confianza: CONF (decisión del equipo) |
| ☐ | Poner el sistema en producción en una finca antes que en varias | Goal | ID: DEC-02, CN-01 · Origen: EQ · Confianza: CONF (decisión del equipo) |
| ☐ | Que el personal de campo adopte el sistema y no vuelva al papel | Goal | ID: CN-08, §7 · Origen: EQ · Confianza: INF |

### Motivation · 05 Requisitos funcionales

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | RF-001 · Registrar siembra de una cama sin conexión `[!]` | Requirement | Módulo: Captura en campo · Antes: RF-C01 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio y reto técnico |
| ☐ | RF-002 · Registrar corte por variedad, grado y fecha sin conexión `[!]` | Requirement | Módulo: Captura en campo · Antes: RF-C02 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-003 · Sincronizar sin perder ni duplicar registros | Requirement | Módulo: Sincronización · Antes: RF-C03 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio y reto técnico |
| ☐ | RF-004 · Impedir eventos imposibles en el ciclo, aun sin conexión | Requirement | Módulo: Validación · Antes: RF-C05 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio y reto técnico |
| ☐ | RF-005 · Rechazar producción superior a las plantas sembradas | Requirement | Módulo: Validación · Antes: RF-C06 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-006 · Calcular tallos proyectados de una cama `[!]` | Requirement | Módulo: Motor de proyección · Antes: RF-C07 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-007 · Distribuir los tallos sobre los días de corte `[!]` | Requirement | Módulo: Motor de proyección · Antes: RF-C08 · Aparece en: Reincorporado el 15-ago (DEC-08) · Significancia: NO significativa · … |
| ☐ | RF-008 · Regenerar la proyección semanalmente conservando versiones | Requirement | Módulo: Motor de proyección · Antes: RF-C11 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-009 · Registrar erradicación y descontar hacia adelante `[!]` | Requirement | Módulo: Erradicaciones y bajas · Antes: RF-C09 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-011 · Mostrar la desviación entre corte real y proyectado `[!]` | Requirement | Módulo: Control de exactitud · Antes: RF-C12 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-012 · Aislamiento total entre empresas (multi-tenant) `[!]` | Requirement | Módulo: Plataforma multi-tenant · Antes: — (nuevo, 12-ago) · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio… |
| ☐ | RF-013 · Parametrización por empresa sin intervención de desarrollo | Requirement | Módulo: Parametrización · Antes: RF-C13 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio y reto técnico |
| ☐ | RF-014 · Autenticar y aplicar permisos de rol sin conexión `[!]` | Requirement | Módulo: Seguridad y roles · Antes: RF-C16 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio y reto técnico |
| ☐ | RF-016 · Conservar autoría, dispositivo y valor anterior de cada registro | Requirement | Módulo: Auditoría · Antes: RF-C18 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-017 · Solo el administrador modifica un registro sincronizado `[!]` | Requirement | Módulo: Auditoría · Antes: RF-C17 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-018 · Agregar producción por día, semana y mes sobre el mismo conjunto | Requirement | Módulo: Consulta y reportes · Antes: RF-C14 · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-019 · Exportar a Excel y PDF respetando las restricciones de rol | Requirement | Módulo: Consulta y reportes · Antes: — (nuevo, 12-ago) · Aparece en: 12-ago y 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-020 · Descargar la parametrización más reciente al dispositivo `[!]` | Requirement | Módulo: Sincronización · Antes: — (nuevo, 13-ago) · Aparece en: 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-021 · Marca de tiempo inmutable y detección de reloj alterado | Requirement | Módulo: Auditoría · Antes: — (nuevo, 13-ago) · Aparece en: 13-ago · Significancia: Impacto en el negocio |
| ☐ | RF-022 · Resolver conflictos de sincronización: automático por defecto, mediación opcional `[!]` | Requirement | Módulo: Sincronización · Antes: RF-C04 (reformulado) · Aparece en: 13-ago · criterio cerrado 15-ago · Significancia: Impacto en… |
| ☐ | RF-023 · Congelar los parámetros de cálculo de una proyección `[!]` | Requirement | Módulo: Motor de proyección · Antes: — (nuevo, 13-ago) · Aparece en: 13-ago · Significancia: Impacto en el negocio y reto técnico |
| ☐ | RF-024 · Explicar visualmente la causa de una caída en la proyección | Requirement | Módulo: Consulta y reportes · Antes: — (nuevo, 13-ago) · Aparece en: 13-ago · Significancia: Impacto en el negocio |

### Motivation · 06 Restricciones

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | CN-01 · Entrega e implementación en mayo de 2027 | Constraint | Tipo: Tiempo |
| ☐ | CN-02 · Presupuesto de desarrollo: ~20.000 USD | Constraint | Tipo: Presupuesto |
| ☐ | CN-03 · Trato de la información como secreto empresarial | Constraint | Tipo: Legal |
| ☐ | CN-04 · El proceso a apalancar apenas se está definiendo | Constraint | Tipo: Proceso |
| ☐ | CN-06 · Arquitectos sin experiencia medible en el sector | Constraint | Tipo: Humano |
| ☐ | CN-07 · El despliegue no puede retrasar la operación más de 7 días | Constraint | Tipo: Proceso |
| ☐ | CN-08 · Resistencia al cambio de los supervisores de campo | Constraint | Tipo: Humano |
| ☐ | CN-09 · Disponibilidad limitada para pruebas previas | Constraint | Tipo: Tiempo |
| ☐ | CN-10 · PowerBI NO es una restricción impuesta en fase 1 | Constraint | Tipo: Técnica impuesta |
| ☐ | CN-12 · Restricción técnica adoptada: RBAC con partición por empresa `[!]` | Constraint | Tipo: Técnica adoptada |
| ☐ | CN-15 · Objetivos de continuidad: sin pérdida de datos, 1 hora de operación, 1 día de restauración | Constraint | Tipo: Técnica adoptada |
| ☐ | CN-16 · Una base de datos por empresa, con esquema común `[!]` | Constraint | Tipo: Técnica adoptada |
| ☐ | CN-14 · BI propio y cerrado antes que integración con terceros | Constraint | Tipo: Técnica adoptada |
| ☐ | CN-13 · Offline-first obligatorio | Constraint | Tipo: Técnica adoptada |

### Motivation · 07 Principios

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | El asistente propone, el sistema valida, el usuario confirma | Principle | Tipo: Arquitectura |
| ☐ | Los parámetros del motor son datos, no código | Principle | Tipo: Arquitectura |
| ☐ | Ningún dato se sobrescribe sin dejar rastro | Principle | Tipo: Arquitectura |
| ☐ | El día es la unidad de tiempo base | Principle | Tipo: Arquitectura |
| ☐ | Automatizar primero, permitir mediación humana como opción `[!]` | Principle | Tipo: Arquitectura |

### Implementation & Migration · —

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Fase 1 · Piloto en una finca | WorkPackage | ID: DEC-02, CN-01, CN-07 · Origen: EQ · Confianza: CONF |

---

## CERRADO — 4 elementos

Era una brecha o una contradicción y ya se resolvió. Se conserva con la decisión escrita.

### Motivation · 04 Brechas, contradicciones y riesgos

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | DEC-01 · Modelo de entrega CERRADO: SaaS multi-tenant | Assessment | Estado: CERRADA 15-ago-2026 · Bloquea: Ya no bloquea nada |
| ☐ | DEC-07 · El dominio excluye precios y rendimiento económico `[!]` | Assessment | Estado: CERRADA 15-ago-2026 · Bloquea: Ya no bloquea |
| ☐ | DEC-13 · Alcance del BI resuelto: seis reportes, sin integrar la app de plagas `[!]` | Assessment | Estado: CERRADA 15-ago-2026 · Bloquea: Queda un residuo menor |
| ☐ | DEC-16 · La IA vuelve, partida en dos `[!]` | Assessment | Estado: CERRADA 15-ago-2026 · Bloquea: Deja abierto el alcance de la mitad analítica |

---

## FUERA-F1 — 11 elementos

Decisión explícita de dejarlo fuera de la fase 1.

### Business · 02 Ciclo y producción

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Actividad de cultivo | BusinessObject | ID: H-17, D-10 · Origen: S2 · Confianza: CONF |

### Business · 05 Procesos y eventos

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Digitar registros | BusinessProcess | ID: H-25, H-26, H-31 · Origen: S1, S2 · Confianza: CONF |

### Business · 06 Actores (personas reales)

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Practicante digitador | BusinessActor | ID: H-25, H-31 · Origen: S2 · Confianza: CONF |

### Business · 99 Fuera del alcance de la fase 1

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Precio de venta (FUERA DEL DOMINIO) | BusinessObject | ID: §5 · Origen: S2 · Confianza: CONF |
| ☐ | Pedido de cliente | BusinessObject | ID: D-07 · Origen: S2 · Confianza: CONF |
| ☐ | Cliente comercial (comprador de flor) | BusinessObject | ID: D-07 · Origen: S2 · Confianza: CONF |
| ☐ | Certificación Florverde | BusinessObject | ID: D-08, H-44 · Origen: S2 · Confianza: CONF |
| ☐ | Gestión de personal e insumos | BusinessObject | ID: D-09 · Origen: S2 · Confianza: CONF |
| ☐ | Datos climáticos (pluviometría, temperatura) `[!]` | BusinessObject | ID: H-45 · Origen: S2 · Confianza: CONF |
| ☐ | Poscosecha | BusinessProcess | ID: H-08 · Origen: S2 · Confianza: CONF |

### Application · —

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Sistemas de nómina y contabilidad | ApplicationComponent | ID: H-38, D-09 · Origen: S1 · Confianza: CONF |

---

## CONTEXTO — 30 elementos

Existe en el mundo real pero el sistema no lo gestiona.

### Business · 03 Proyección, presupuesto y venta

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Presupuesto de producción | BusinessObject | ID: H-48, BR-21 · Origen: S2 · Confianza: CONF |
| ☐ | Compra a terceros | BusinessObject | ID: H-42, H-43 · Origen: S2 · Confianza: CONF |

### Business · 05 Procesos y eventos

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Sembrar | BusinessProcess | Termino cliente: siembra · Origen: S1 · Confianza: CONF |
| ☐ | Desbotonar `[!]` | BusinessProcess | Termino cliente: desbotone · ID: H-15 · Origen: S2 · Confianza: CONF |
| ☐ | Botón color alcanzado `[!]` | BusinessEvent | Termino cliente: botón color · ID: H-15 · Origen: S2 · Confianza: CONF |
| ☐ | Cortar | BusinessProcess | Termino cliente: corte · ID: H-14 · Origen: S2 · Confianza: CONF |
| ☐ | Inicio de temporada pico | BusinessEvent | ID: H-41 · Origen: S2 · Confianza: CONF |

### Business · 06 Actores (personas reales)

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Director de Producción `[!]` | BusinessActor | ID: H-28, §9.2 · Origen: S1, S2 · Confianza: CONF |
| ☐ | Supervisor de siembra | BusinessActor | ID: H-24, H-29 · Origen: S2 · Confianza: CONF |
| ☐ | Auxiliar de siembra | BusinessActor | ID: H-29 · Origen: S2 · Confianza: CONF |
| ☐ | Ingeniero agrónomo | BusinessActor | ID: H-10, BR-23 · Origen: S2 · Confianza: CONF |
| ☐ | Ingeniero de sistemas `[!]` | BusinessActor | ID: H-32, H-33 · Origen: S2 · Confianza: CONF |
| ☐ | Gerente de ventas | BusinessActor | ID: H-19, H-48 · Origen: S1, S2 · Confianza: CONF |
| ☐ | Gerencia general | BusinessActor | ID: H-48 · Origen: S2 · Confianza: CONF |
| ☐ | Vendedora | BusinessActor | ID: H-30 · Origen: S2 · Confianza: CONF |
| ☐ | Equipo FlorLogic `[!]` | BusinessActor | ID: §1.1 · Origen: EQ · Confianza: CONF |

### Application · —

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | PowerBI | ApplicationComponent | ID: H-36, H-37 · Origen: S1, S2 · Confianza: CONF |
| ☐ | App de monitoreo de plagas y enfermedades | ApplicationComponent | ID: H-34 · Origen: S2 · Confianza: CONF |

### Technology & Physical · —

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Cuarto frío / cava | Facility | ID: Glosario §11 · Origen: S1 · Confianza: CONF |
| ☐ | Estación meteorológica `[!]` | Equipment | ID: H-45 · Origen: S2 · Confianza: CONF |
| ☐ | Conectividad intermitente en campo | CommunicationNetwork | ID: H-34, D-01 · Origen: S2 · Confianza: CONF |
| ☐ | Invernadero / bloque en campo | Facility | ID: §7.2 · Origen: EQ · Confianza: INF |

### Motivation · 01 Drivers (dolores medidos)

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Latencia del dato: 8 días | Driver | ID: H-26 · Origen: S1 · Confianza: CONF |
| ☐ | Error de captura: 2% `[!]` | Driver | ID: H-27, H-33 · Origen: S1, S2 · Confianza: CONF |
| ☐ | Costo de la captura manual | Driver | ID: H-24, H-25, H-28 · Origen: S1, S2 · Confianza: CONF |
| ☐ | Desviación entre proyección y corte real `[!]` | Driver | ID: §3.3, BR-21 · Origen: S2 · Confianza: CONTRAD |
| ☐ | 8% de las ventas cubiertas con compra a terceros o cancelación | Driver | ID: H-42, H-43 · Origen: S2 · Confianza: CONF |
| ☐ | La proyección se ajusta mensualmente; se quiere semanal | Driver | ID: H-49 · Origen: S2 · Confianza: CONF |
| ☐ | Confidencialidad de los datos de producción | Driver | ID: CN-03, RF-012 · Origen: EQ, S2 · Confianza: INF |
| ☐ | Pico de temporada: +60% | Driver | ID: H-41 · Origen: S2 · Confianza: CONF |

---

## METODO — 10 elementos

Regla de cómo se trabaja, no del producto. No va a la entrega de arquitectura.

### Motivation · 07 Principios

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | Cita textual y interpretación nunca comparten columna | Principle | Tipo: Método |
| ☐ | Un hueco declarado vale más que un hueco tapado | Principle | Tipo: Método |
| ☐ | Los identificadores no se reutilizan `[!]` | Principle | Tipo: Método |
| ☐ | Un requisito = una capacidad, con verbo observable | Principle | Tipo: Método |

### Implementation & Migration · —

| ¿Correcto? | Elemento | Tipo | Trazabilidad |
|:---:|---|---|---|
| ☐ | 0_CONTEXTO_v3.md | Deliverable | Versión 3.0 · 11-ago-2026 |
| ☐ | FuncionalidadesSignificativas.xlsx `[!]` | Deliverable | 13-ago-2026 · numeración RF-001..FR-024 · MÁS RECIENTE |
| ☐ | RestriccionesNegocio.xlsx `[!]` | Deliverable | 13-ago-2026 |
| ☐ | MINI QAW PLANTILLA NO TERMINADA.xlsx `[!]` | Deliverable | 11-ago-2026 |
| ☐ | DECISIONES.md · bitácora de decisiones cerradas | Deliverable | Abierto el 15-ago-2026 |
| ☐ | Modelo ArchiMate de contexto | Deliverable | 15-ago-2026 · este archivo |
