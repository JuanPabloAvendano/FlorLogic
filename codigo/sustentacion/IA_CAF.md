REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# Cloud Adoption Framework (CAF) en un producto local-first

Fuente: «AWS CAF groups its capabilities in six perspectives: Business, People, Governance, Platform, Security, and
Operations» (AWS Cloud Adoption Framework · Overview · p. 10,
https://docs.aws.amazon.com/pdfs/whitepapers/latest/overview-aws-cloud-adoption-framework/overview-aws-cloud-adoption-framework.pdf#page=10).
El Microsoft CAF para Azure tiene una estructura equivalente (estrategia, plan, preparar, adoptar, gobernar, gestionar).

## 1 · La postura: adopción **selectiva** de la nube

FlorLogic **no migra a la nube**. ADR-001 lo dice: «Evitar la tendencia de desarrollo que presenta al modelo de
negocio SaaS como la tendencia necesaria para el éxito, se utilizará una forma de entrega que permita el Offline-First
por restricciones técnicas del lugar». La finca captura en invernaderos sin cobertura y su dato no puede depender de
internet.

La nube entra **solo** donde aporta algo que el nodo no puede dar:

| Uso de la nube | Por qué allá y no en la finca | Transacción |
|---|---|---|
| Custodia de respaldos cifrados (S3 con bloqueo de retención, TEC-21) | Un respaldo en la misma oficina se quema con la oficina | TX-2 |
| Distribución de versiones y del paquete de configuración (BB-07, BB-16) | Actualizar sin recoger dispositivos (ESC-25) | futura |
| Soporte remoto sin ver datos de negocio (ESC-30) | La finca está a horas del equipo | futura |
| Aviso push al administrador (ntfy, CT-3) | **No es nube:** ntfy corre en el nodo («Aviso al administrador por ntfy en el nodo»). Solo necesita salida a internet si el teléfono está fuera del Wi-Fi de la finca; si no hay red, el aviso se guarda y se reintenta | TX-1 (F5) |

README §5, nodo N4: los servicios en línea «reciben los respaldos ya cifrados con la llave del cliente, sin poder
leerlos, y entregan las versiones nuevas del sistema. Nunca están en el camino crítico de la finca».

## 2 · Las seis perspectivas

| Perspectiva | Pregunta del CAF | Respuesta de FlorLogic | Evidencia / estado |
|---|---|---|---|
| **Business** | ¿Qué valor de negocio da la nube? | Proteger el dato ante desastre (respaldo) y bajar costo de soporte. El valor principal (capturar y ver) se da sin nube | README (problema: 8 días de retraso, 6–10 % de pérdidas); ADR-001 · HAY (decisión) |
| **People** | ¿El equipo sabe operar lo que adopta? | Dos personas: se adopta lo que se puede operar. Por eso no hay Kubernetes ni broker. Matriz de dominio por integrante | `IA_GUIA-IS2-checklist.md` §6 · PARCIAL (matriz vacía) |
| **Governance** | ¿Quién decide y cómo se controla el riesgo y el costo? | ADR-001…037 en el README; registro de tecnologías con estados y revisión de CVE antes de aceptar; riesgos en `IA_riesgos.md` | README §4, `IA_TECNOLOGIAS-vigentes.md` · HAY |
| **Platform** | ¿Qué plataforma y cómo se despliega? | Contenedores con Compose (BB-14), el mismo artefacto en finca y en nube (ADR-016); proveedor de nube **sin elegir** (P-R10), MinIO en pruebas | `compose.yaml`, `Dockerfile` · PARCIAL: P-R10 abierto, sin pipeline (F6) |
| **Security** | ¿Cómo se protege en la nube? | El respaldo sale **ya cifrado** con la llave de la empresa (ADR-012: «La llave es de cada empresa»); TLS mutuo finca → nube (TEC-23); bloqueo de retención contra borrado | Pendiente (TX-2) |
| **Operations** | ¿Cómo se opera y observa? | Telemetría de operación con esquema cerrado y sin datos de negocio (TEC-18, BB-13) | Pendiente (F6, TX-2) |

## 3 · Por qué no «lift and shift»

| Alternativa | Por qué no ahora | Disparador para reconsiderar |
|---|---|---|
| SaaS multiempresa en la nube | No hay red en el invernadero; los clientes compiten entre sí y exigen su dato en su infraestructura (ESC-16) | Una finca con conectividad garantizada que pida no tener servidor; ESC-16 ya lo permite: «El sistema se instala en la infraestructura indicada usando el mismo paquete» |
| Servicios gestionados (IdP, cola, base) | Cobro por usuario o por mensaje rompe el margen (CN-35) y no funcionan sin internet | Más instalaciones que justifiquen una plataforma compartida |

## 4 · Frase de defensa

> Aplicamos el CAF al revés de lo habitual: la pregunta no fue «cómo llevamos todo a la nube», sino «qué parte, y
> sólo esa, gana algo estando en la nube». La respuesta es el respaldo cifrado, las versiones y el soporte; la
> captura y la consulta se quedan en la finca porque ahí está el trabajo y no hay red.
