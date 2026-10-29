REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 20-oct-2026, a pedido de Juan (limpieza del repositorio)
Estado de revisión: SIN REVISAR
Manda por encima: `.claude/CLAUDE.md` · la documentación de `documentacion-desarrollo` · la palabra de Juan

# Contexto para la IA · índice

Documentos de apoyo. Nadie los revisa como entregable, pero se consultan para tener contexto. Lo que
manda está en la rama `documentacion-desarrollo` y en `ADR.xlsx`.

| Archivo | Para qué sirve | Quién lo mantiene |
|---|---|---|
| `IA_LEER-PRIMERO.md` | **Urgente (21-oct):** lo que el siguiente agente le muestra a Juan antes de nada: explicación pendiente del Segmento 1 y el avance conectado como siguiente paso. Se descarta al cumplirse | La IA, al cumplirse |
| `IA_GUIA-IS2-checklist.md` | **Foco desde el 28-oct:** cada ítem de la Guía Oficial de Evaluación de IS II emparejado con FlorLogic y TX-1, su evidencia, su fuente en la biblioteca y la matriz de dominio por integrante | La IA cuando cambia la evidencia; los integrantes marcan la matriz |
| `IA_RETOMAR-AQUI.md` | Punto de reentrada: en qué va el trabajo y qué sigue. Se reemplaza al cerrar cada sesión | La IA al cerrar sesión |
| `IA_TECNOLOGIAS-vigentes.md` | Qué tecnología está aceptada, con qué versión, y cómo entra una nueva | La IA cuando cambia una versión o entra una pieza |
| `IA_PRIORIDAD-componentes-TX1.md` | Orden de importancia de los componentes de TX-1 y cómo entran al nodo el gateway, el WAF, Grafana y demás junto al monolito modular (20-oct, propuesta) | La IA cuando Juan decide sobre el orden o las tecnologías |
| `IA_DICCIONARIO.md` | Palabras de negocio y técnicas, y la Nomenclatura de los códigos (A-n, K-n, COH-n…) | La skill `detallar-para-codificar` |
| `IA_TX1-DEFINICION.md` | TX-1 consolidada: definición, estado de aceptación, recorrido, contrato, P-S, componentes, ES-S, código hecho y tensiones abiertas | La IA cuando cambia el estado de TX-1 |
| `referencias/empaquetado-hexagonal-por-modulo.png` | **Imagen de Juan (22-oct-2026): la estructura principal de paquetes y su comunicación en la arquitectura hexagonal.** Cada módulo del backend la sigue como hexágono independiente. Se cita cada vez que se hable de empaquetado | Juan (es suya, no de la IA) |
| `referencias/arquitectura-readme/` | **Manda (28-oct-2026):** copia de lectura de las 9 imágenes del README de `documentacion-desarrollo` (C4 N2, despliegue, C4 N3, paquetes de `ingesta`, secuencias). Índice y comando para volver a copiarlas en su `IA_LEEME.md` | Se vuelven a copiar cuando cambien en GitHub |
| `levantamiento/0_CONTEXTO_v3.md` | Contexto del levantamiento de requisitos (11-ago-2026), vigente | Histórico; no se actualiza |
| `levantamiento/historico/` | La versión 2.0 del contexto y la revisión que dio pie a la 3.0. Solo para rastrear de dónde viene algo | Congelado |

Las ESPEC de cada detallamiento no están aquí: van junto al código, en `codigo/<módulo>/espec/`.
