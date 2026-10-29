REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 20-oct-2026, a pedido de Juan, al cerrar la sesión (reemplaza la versión del 19-oct)
Estado de revisión: SIN REVISAR
Manda por encima: `codigo/contrato/espec/ESPEC-contrato-ingesta.md` · `IA_00-LEEME-transacciones.md` (rama `documentacion-desarrollo`) · la palabra de Juan

> **21-oct-2026 · pausa:** antes de esto, lee y muestra `.claude/contexto/IA_LEER-PRIMERO.md` (explicación del
> Segmento 1 pendiente y el avance conectado como siguiente paso).
>
> **28-oct-2026 · pausa (en paralelo, decisión de Juan):** foco en la Guía de IS II
> (`.claude/contexto/IA_GUIA-IS2-checklist.md`) y en el esqueleto de componentes de TX-1 con tecnología ya construida:
> `codigo/despliegue/espec/ESPEC-esqueleto-tx1.md`, Segmento 1 (backend en contenedor + OpenBao), ronda 1 cerrada al
> 52 %. Retomar desde su «Dónde retomar».

# Retomar aquí: TX-1, del canal al dominio de ingesta

Para seguir en un chat nuevo basta decir: **«lee .claude/contexto/IA_RETOMAR-AQUI.md y sigamos»**.

## Trabajo del 21 y 22-oct-2026: el WAF del gateway (Jeronimo, con visto de Juan)
- **Dónde:** `codigo/despliegue/ESPEC-waf-finca.md`. Segmento 1 escrito y verificado: coherencia pasa, completitud
  100 %, 11 mutaciones, las 11 detectadas. **La explicación (paso 6) se omitió por decisión de Jeronimo el 22-oct**
  (alcanzó a dar el archivo 1: 50 %), así que el segmento no está aceptado; su Ficha dice cómo retomarla.
- **Integrado con el refactor del hexágono por módulo** (22-oct, sin commit): `./mvnw clean verify` 122/122 con
  Docker, `CanalPorGatewayTest` 25/25. Los commits y el push los hace Jeronimo.
- **Qué cambió:** el servicio `gateway` de Compose pasa a la imagen `owasp/modsecurity-crs:4.30.0-nginx-alpine-202610180210`
  (TEC-31, la misma Nginx 1.30.6), escucha en el 8080 dentro del contenedor, con `FLORLOGIC_WAF_MODO` (On) y un
  `healthcheck` propio; `gateway/modsecurity-override.conf.template` es nuevo.
- **Pendiente de Juan:** TEC-31 → `ACEPTADO` (LEEME de transacciones e `IA_TECNOLOGIAS-vigentes.md`) y ajustar `BB-08`.
  Si no la acepta, el segmento se revierte (COH-1 de esa ESPEC). También: UTF-8 sin BOM en el contrato y qué hace el
  celular ante `gateway/bloqueado` (§7 de esa ESPEC).
- Lo que sigue abajo (el dominio de ingesta) no cambia.

## ⟶ Actualización 20-oct-2026 (tarde): el dominio de ingesta, LISTA PARA CÓDIGO

**Dónde está el trabajo:** en la copia visible `~/Work/florlogic-codigo` (rama `codigo-desarrollo`), **sin commit**
(PR-05: los commits los hace Juan). El 21-oct-2026 todo lo del worktree `ia/dominio-tx1` se copió aquí, a pedido de
Juan, y el diccionario y la skill se unieron con los cambios del gateway. **Nunca se vuelve a escribir en un
worktree:** se trabaja siempre en la copia visible, sin tocar git. Para retomar: `/detallar-para-codificar`.

| Qué | Dónde |
|---|---|
| ESPEC del dominio (estado, decisiones A-60…A-82, casos C-90…C-103, plan de 3 segmentos) | `codigo/backend-finca/espec/ESPEC-dominio-ingesta.md` · leer la **Ficha** y la «Ficha del Segmento 1/3» (§8) |
| Diagrama del Segmento 1 (valores y avance) | `codigo/backend-finca/espec/diagramas/IA_segmento-1.drawio` y `IA_segmento-1.png` |
| Diccionario (términos nuevos: agregado, avance del dispositivo, los dos motivos, `IdYaRecibido`, `Veredicto`…) | `.claude/contexto/IA_DICCIONARIO.md` |

**Estado en una línea:** la ESPEC del modelo de dominio de la ingesta está `LISTA PARA CÓDIGO` (≈88 %, 0
bloqueantes, tres rondas de preguntas). Juan **aceptó el alcance del dominio mínimo de TX-1** el 20-oct en la
sesión. Etapa 4, Segmento 1: plan y diagrama presentados; **falta el «sí» de Juan para escribir el código**.

**Qué decide Juan antes o durante el código:**
1. El «sí» al Segmento 1 (14 archivos, ≈750 líneas, solo Java puro y pruebas unitarias).
2. K-115: forma canónica de la huella (propia sobre Jackson 3, que TEC-10 acepta, o biblioteca RFC 8785). Solo
   bloquea el Segmento 3.
3. Reflejar su aceptación en la fila TX-1 de `IA_00-LEEME-transacciones.md` (rama `documentacion-desarrollo`;
   documento humano, lo edita Juan). `CLAUDE.md` aún dice «POR ACEPTAR»: también conviene corregirlo.
4. Confirmar la precisión agregada a «Juicio de la anotación» en el diccionario (`sin_efecto` es un resultado de
   llegada, no un juicio guardado).

**Decisiones de Juan en este detallamiento que mandan:** solo el modelo de dominio (sin servicio, notificación ni
observabilidad); dos raíces pequeñas (`Anotacion`, `AvanceDelDispositivoAgregado`); `enum Resultado` con guardas;
un consecutivo o uuid inválido = `422` de toda la partición «porque si un consecutivo falla, toda la partición
peligra»; el hueco se guarda marcado y el celular reanuda; una cama no capturada no es hueco; subpaquetes
`comun`, `eventos`, `anotacion`, `avance`, `reglas`; reconstituir `Anotacion` fuera del borde.

Lo de abajo es el estado anterior (canal y contrato); sigue vigente para el contrato.

## Estado en una línea
El canal de TX-1 funciona de punta a punta y está **verificado** (Segmento 2: 65/65 pruebas con Docker, coherencia
pasa, completitud 100 % sobre el camino mínimo). Su aceptación espera la explicación de Juan. El canal se **traspasó**
a sus semillas (D-19): el siguiente trabajo es el **dominio de ingesta**, que arranca desde ese código.

## Comprobar que todo sigue bien (2-3 minutos, necesita Docker)
```
cd ~/Work/florlogic-codigo/codigo
./mvnw clean verify          # 65 pruebas en verde; sin Docker se saltan las de PostgreSQL (dilo, no lo des por probado)
scripts/IA_probar-canal.sh   # «2 nuevas», «2 ya estaban», «Rechazada» (código 1), «Canal OK»; usa su propio compose
```

## Lo que se decidió el 20-oct-2026 (Juan)
| Qué | Dónde quedó |
|---|---|
| **D-18:** el camino mínimo recorta clases, nunca el empaquetado; un paquete mal ubicado es bloqueante | Skill `DECISIONES.md` · `codigo/IA_LEEME.md` (convención de paquetes) |
| Paquetes «plantilla en español»: `servicios/puertos/entrada` · `puertos/salida` · `casosuso` · `infraestructura/entrada/web` (o `cli`) · `infraestructura/salida/persistencia` (o `http`) | `codigo/IA_LEEME.md` · ESPEC §10 (OBS-2-9, OBS-2-12) |
| Simulador hexagonal completo | `codigo/IA_LEEME.md` «Dentro del simulador» |
| Pirámide de pruebas en `ingesta`: contrato del puerto + doble en memoria + prueba JDBC + unitaria del servicio | `codigo/IA_LEEME.md` «Pruebas» · ESPEC (OBS-2-11) |
| **A-59:** el nodo guarda el JSON equivalente de cada anotación, no sus bytes | ESPEC §2 |
| COH-105: un U+0000 en una anotación es 422 (antes daba 500 para siempre) | ESPEC §11 |
| **D-19:** el canal se traspasa a los elementos que tocó | Skill `DECISIONES.md` · ESPEC §12 · las tres semillas («Punto de partida») |
| Orden: aceptar el Segmento 2 → traspaso → contrato (Segmentos 3-7 y ronda 2) → `IMPLEMENTADA` | ESPEC, Ficha |
| La skill vive **solo en el proyecto** (`.claude/skills/detallar-para-codificar`); la copia vieja de usuario, con su git, está en `~/.claude/skills-respaldo/` | — |
| El C4 y `IA_FUNCIONAMIENTO-ESPERADO.md` son arquitectura de **referencia**, no se corrigen por diferencias de forma | Memoria de Claude |

## Encargo para el chat nuevo (palabras de Juan, 20-oct)
> Detallar y codificar **el dominio y las reglas de negocio**, identificando puntualmente **solo las totalmente
> necesarias** para el funcionamiento directo y básico de la **ingesta de información, su clasificación** y el uso de
> los **componentes mínimos** que necesite. Por ejemplo: en el dominio de ingesta hacen falta **notificaciones** al
> celular del gerente de producción y a su **correo**. Ir utilizando e implementando los componentes a través de esas
> reglas, **usando lo que ya esté construido**. Y **los servicios de observabilidad**: que las cosas vayan apareciendo
> **en paralelo** a medida que avanza el código.
>
> Aclaración (mismo día): «no sólo la observabilidad: las necesidades de componentes y tecnologías **van creciendo
> cuando se van definiendo comportamientos y reglas** a través de lo que se escriba en el dominio».

**El principio que manda en el chat nuevo: el dominio tira de la infraestructura.** No se diseñan ni se instalan
componentes por adelantado. Se escribe una regla del dominio; esa regla dice qué necesita (avisar a alguien, esperar a
que haya red, medir algo, leer un umbral, programar algo para más tarde); y **en ese momento** entra el componente
mínimo que la cumple, con su puerto en `servicios/puertos/salida`, su adaptador en `infraestructura`, su prueba y, si
trae tecnología nueva, su fila propuesta en `IA_TECNOLOGIAS-vigentes.md` para que Juan la acepte. Ejemplos de lo que
puede ir apareciendo así: notificación (módulo `notificacion`), cola y reintento (TEC-09 db-scheduler), métricas y logs
(TEC-17, módulo `observabilidad`), configuración de umbrales (semilla de configuración), eventos entre módulos (Spring
Modulith). Cada componente nace con la regla que lo pidió y queda trazado a ella.

Cómo se lee con la skill (`/detallar-para-codificar`):
- **Elemento de partida:** semilla `codigo/contrato/espec/IA_apartado-2026-10-18/semillas/IA_SEMILLA-caso-de-uso-ingreso.md`
  (plantilla `caso-de-uso`). Arranca desde el canal ya escrito (su sección «Punto de partida»). Le siguen, en rondas
  propias: `entidad-agregado` (qué es el agregado: anotación o sesión, OBS-2-17), `evento-mensaje` (ingesta →
  `notificacion`, con registro de publicación de Spring Modulith, según `IA_TECNOLOGIAS-y-estructura.md` §4) y
  `configuracion` (catálogo y umbrales, semilla `IA_SEMILLA-configuracion-catalogo-y-versiones.md`).
- **Camino mínimo (D-17):** el juicio más simple de cada anotación (aplicada / marcada / rechazada) y **un** aviso al
  gerente, de punta a punta, antes que las reglas finas.
- **Componentes en paralelo** (principio de arriba): cada regla trae lo que necesita. La observabilidad es uno más:
  su métrica (Micrometer) y su log estructurado, sin datos de negocio en la telemetría (BB-13, `ESC-30`, `CN-34`).
- **«Lo que ya está construido»:** hoy los módulos `notificacion/` y `observabilidad/` son **solo `package-info`**; lo
  único con código es `ingesta/` (el canal) y, de TEC-17, Actuator. No hay que inventar que existe más.

## Antes de escribir código en el chat nuevo: lo que decide Juan
1. **TX-1 está `AJUSTAR`**: solo el canal está aceptado en `IA_00-LEEME-transacciones.md`. CLAUDE.md: las reglas de una
   transacción se programan cuando su fila, sus P y sus TEC están `ACEPTADO`. Hay que aceptar el alcance del dominio
   mínimo (o la skill para y pregunta).
2. **Correo y push no tienen tecnología aceptada**: ninguna fila en `IA_TECNOLOGIAS-vigentes.md` (BB-10 dice «correo
   transaccional + notificación push», sin herramienta). Se proponen con su fila y Juan las acepta antes de usarlas.
3. **Local-first:** el nodo de finca opera **sin internet**; un correo o un push necesitan salir a internet. Hay que
   decidir qué pasa cuando no hay red (cola en la base, TEC-09 db-scheduler, y reenvío al volver) y si el aviso al
   celular del gerente es push (internet) o por la red de la finca.
4. (Menor) **Telemetría** (TEC-18, servicio propio) vive en `servicios-en-linea`: decidir si en el camino mínimo basta
   con métricas y logs locales (TEC-17).

## Pendiente del detallamiento del contrato (no se pierde)
- **Explicación de Juan del Segmento 2** (paso 6, ≥80 %, clase por clase: práctica y patrón). Sin ella, el Segmento 2
  no está aceptado. Puede darse en el chat nuevo.
- Contrato: Segmentos 3 (huella), 4 (errores y fronteras), 5 (esquemas; bloqueado por la biblioteca de TEC-11),
  6 (validador único, A-58), 7 (dorados y arquitectura) y la **ronda 2** (`/sesiones`, `/cierre`, `/archivos`).
- `application.yaml` trae una clave de desarrollo por defecto (`florlogic`): decidir con PR-06 (ESPEC §7).
- `information-pairment` sigue duplicada (usuario y proyecto).
- Apareció `build/generated-spring-modulith/` en la raíz del repo (lo genera Spring Modulith al correr desde ahí): sin versionar; decidir si va al `.gitignore`.

## Qué leer al volver, en orden
1. Este archivo.
2. `codigo/contrato/espec/ESPEC-contrato-ingesta.md`: la Ficha y §12 (traspaso). No hace falta leer las HISTORIAS.
3. La semilla `IA_SEMILLA-caso-de-uso-ingreso.md` («Punto de partida» y «Abierto»).
4. `codigo/IA_LEEME.md` (paquetes y pruebas) e `IA_TECNOLOGIAS-vigentes.md`.

## Herramientas
- **Biblioteca**: MCP `biblioteca` (61 libros; citar «Título · p. N»).
- **draw.io**: generador en `.claude/skills/detallar-para-codificar/herramientas/drawio.py`; exportar con
  `drawio -x -f png -p <página, desde 1> -o <png> <archivo>`.
- **Eclipse**: workspace `~/eclipse-workspace` (6 proyectos; vista jerárquica). Tras mover paquetes: F5 o *Maven › Update Project*.

## Reglas que no hay que olvidar
- Camino mínimo primero (D-17), pero **con el empaquetado correcto** (D-18).
- El canal se traspasa a sus elementos (D-19).
- Ningún punto de entrada lanza ante una entrada externa (A-57).
- Ninguna dependencia sin su fila en `IA_TECNOLOGIAS-vigentes.md`. Commits y empujes los hace Juan (PR-05).
