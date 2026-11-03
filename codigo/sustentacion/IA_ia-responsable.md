REGISTRO DE IA · NO ES FUENTE
Generado por: Claude (subagente) · 31-oct-2026, construcción completa de TX-1
Estado de revisión: SIN REVISAR
Manda por encima: README de documentacion-desarrollo · Guía oficial IS II · la palabra de Juan

# Uso responsable de IA y validación humana en FlorLogic

## 1 · Marco

NIST organiza la gestión de riesgos de IA en cuatro funciones: «GOVERN, MAP, MEASURE, and MANAGE» (NIST AI 100-1 ·
AI Risk Management Framework 1.0 · p. 8, https://nvlpubs.nist.gov/nistpubs/ai/NIST.AI.100-1.pdf#page=8). FlorLogic
usa IA (Claude) como **asistente de especificación, código y revisión**, nunca como autoridad. El README §9 lo dice
así: «se logra un flujo de trabajo que considera completamente el concepto **human in the loop**».

## 2 · Política en una página

| # | Regla | Cómo se cumple | Dónde se ve |
|---|---|---|---|
| 1 | **Lo humano manda** | Juan, 28-oct-2026: «todo lo que está en GitHub es humano y manda». El README de `documentacion-desarrollo` (ESC, ADR, C4) lo escribieron los integrantes; lo IA es antecedente | `.claude/contexto/IA_GUIA-IS2-checklist.md` §0 |
| 2 | **Todo lo generado se marca** | Prefijo `IA_` en el nombre y cabecera de cuatro líneas: «REGISTRO DE IA · NO ES FUENTE», quién lo generó, estado de revisión, qué manda por encima | Todos los `IA_*.md`, `default.conf.template`, `politica-backend-finca.hcl` |
| 3 | **Nada se da por revisado sin una persona** | Estados `SIN REVISAR` → `REVISADO POR JUAN <fecha>` / `APLAZADO` / `DESCARTADO` | Ejemplo: `IA_LEER-PRIMERO.md` quedó `DESCARTADO 30-oct` |
| 4 | **Cada decisión la acepta una persona** | Las ESPEC registran cada decisión con quién la tomó: «(Juan, ronda 30-oct P2)»; las tecnologías pasan de `PROPUESTA` a `ACEPTADO` solo por Juan | `codigo/backend-finca/espec/ESPEC-dominio-ingesta.md` (A-n), `IA_TECNOLOGIAS-vigentes.md` §4 |
| 5 | **Se verifica con agentes independientes** | Subagentes verificadores comparan la ESPEC con la documentación humana y el código (VER-n, COH-n); sus hallazgos son observaciones (OBS-n) que Juan acepta o no | §10 de cada ESPEC |
| 6 | **Los commits los hace una persona** | Regla PR-05: «La IA solo hace commit o push cuando Juan lo pide de forma explícita en esa sesión». Esta construcción se hace en una copia **sin git** (CT-5: «Claude no hace commits») | `.claude/CLAUDE.md`, `codigo/IA_CONSTRUCCION-TX1.md` |
| 7 | **Ningún secreto en el repo ni en el contexto de la IA** | Regla PR-06; los secretos entran por variables del anfitrión y OpenBao; la IA trabaja con valores de desarrollo | `compose.yaml` (secretos sin defecto), `application.yaml` |
| 8 | **Tecnología nueva con revisión de CVE** | La IA busca CVE en OSV y en los avisos del repositorio antes de proponer; Juan acepta | `IA_TECNOLOGIAS-vigentes.md` TEC-33…37 |
| 9 | **Fuentes citadas, no inventadas** | Biblioteca local consultable (libros y documentación oficial); se cita «Título · p. N» | Javadoc de `CapasTest` («Clean Architecture p. 198»), este documento |
| 10 | **El equipo debe poder explicar y reconstruir sin IA** | Ruta de estudio por tramos y matriz de dominio por integrante; modo docente: tras la explicación se daña el código y la IA orienta sin dar la solución | `IA_RUTA-ESTUDIO-TX1.md`, `IA_GUIA-IS2-checklist.md` §6 |

## 3 · Las cuatro funciones del NIST en el proceso

| Función | Qué significa | En FlorLogic |
|---|---|---|
| **Govern** | Responsabilidades y reglas | `.claude/CLAUDE.md` (reglas del repo), las reglas de §2, Juan como arquitecto y dueño de las decisiones |
| **Map** | Saber dónde puede hacer daño la IA | Riesgos: código que nadie entiende (R-30 en `IA_riesgos.md`), decisiones IA que contradicen al README (R-31), tecnología sin aceptar en uso (R-32) |
| **Measure** | Medir si la salida es correcta | Pruebas automáticas (sin prueba no está terminado, PR-04); verificadores; umbral de calidad del detallamiento (85 %) |
| **Manage** | Actuar sobre lo medido | Estados `DESCARTADO`/`APLAZADO`; tabla de choques README ↔ IA (CH-1…CH-7) con quién lo resuelve |

## 4 · Ejemplos concretos de validación humana que cambió lo que propuso la IA

| Qué propuso o hizo la IA | Qué decidió una persona | Fuente |
|---|---|---|
| Identidad propia con credencial JWS (ADR-007, TEC-13) | «should be a service, not something we build from scratch» → Keycloak (TEC-32) | Juan, 28-oct-2026 |
| Imagen del backend con Jib | Dockerfile multietapa escrito por el equipo (TEC-28 ajustado) | Juan, 28-oct-2026 |
| Nombres en inglés y «cable» en los ensambladores | «no me gusta que se llame cable … cambia assembler a ensamblador» (A-105) | Juan, 31-oct-2026 |
| OpenBao 2.7.1 | 2.7.2 tras la revisión de CVE (GHSA-736f-82w7-hj22) | Juan, 29-oct-2026 |
| La huella calculada en el ensamblador de entrada | «not calculate, just adapt it» → puerto `CalculadorDeHuella` (A-106) | Juan, 31-oct-2026 |

## 5 · Límites declarados

- La IA puede equivocarse con seguridad aparente: por eso cada afirmación de este paquete de sustentación cita un
  archivo, una prueba o una fuente, y lo que no se pudo comprobar dice «Pendiente (Fx)».
- **Esta pasada no ejecutó `./mvnw verify`.** Los conteos de pruebas que aparecen vienen de los documentos
  (`codigo/IA_LEEME.md`, `IA_CONSTRUCCION-TX1.md`: «171/171 con Docker» al tomar la copia) y deben volver a medirse.
- Todos los documentos de `codigo/sustentacion/` están `SIN REVISAR` hasta que Juan los revise.

## 6 · Frase de defensa

> Usamos la IA como un colega rápido que propone, escribe y revisa, pero nunca firma: todo lo que produce lleva su
> marca y su estado, cada decisión la toma una persona con nombre y fecha, los commits los hacemos nosotros, y la meta
> es que cada uno pueda explicar y reconstruir cada pieza sin ella.
