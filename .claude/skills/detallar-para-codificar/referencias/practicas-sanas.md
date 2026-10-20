# Prácticas sanas (25 % del índice de precisión)

## De dónde salen

1. **Lista general:** las 26 prácticas de `investigacion/02` §3. Aplican a todo elemento: regla de
   dependencias, dominio sin framework, puertos explícitos, errores explícitos, pruebas sin
   infraestructura y demás. Cada una dice cómo se comprueba (lectura, ArchUnit, prueba) y trae su
   severidad.
2. **Lista de la plantilla:** la §4 de la plantilla cargada, con las prácticas propias del tipo de
   elemento (Problem Details en un endpoint, outbox en un evento, backoff con jitter en un trabajo, etc.).

Se usa la unión de las dos. Si una práctica de la plantilla repite una general, cuenta una sola vez, con
la severidad más alta de las dos.

## Cómo se puntúa

- Peso: crítica 3, mayor 2, menor 1.
- Valor: cumple 1, parcial 0,5, no cumple 0.
- No aplica: se excluye del cálculo, y la justificación queda en la ESPEC.
- `Adherencia = 100·Σ(peso·valor)/Σpeso`.
- **Tope:** una crítica en 0 deja el máximo en 60; dos o más críticas en 0, en 40.

**Según la etapa:**
- **Al detallar:** cumple = la práctica está decidida y escrita en la ESPEC.
- **Al verificar:** cumple = la comprueba ArchUnit, jMolecules, Spring Modulith o una prueba. Si solo se
  revisó leyendo, vale 0,5.

## Caso sin invariantes

Si el módulo no tiene invariantes de negocio y sus puertos de salida solo guardan y leen, se sugieren
capas simples. En ese caso, los ítems 4, 8, 10 y 11 de la lista general se marcan N/A sin penalizar
(`investigacion/02` §6).

## Cómo pregunta el agente por una práctica

Nunca pregunta «¿cumples la práctica X?». La convierte en una decisión concreta con opciones. Por ejemplo,
en lugar de «¿el dominio es independiente del framework?», pregunta «¿Dónde va la anotación `@Entity`?»
con dos opciones: ★ en un modelo de persistencia aparte, o en la entidad de dominio.

## Lo imprevisto (D-15)

Práctica general, **mayor (2)**, para todo elemento con entradas que vienen de fuera de su módulo (red,
archivo, otro proceso, otra versión del mismo programa):

- **Fronteras totales:** ningún punto de entrada lanza ante una entrada. Devuelve un resultado sellado
  (`Leida`/`Fallida`, `Ok`/`Rechazo`) con el lugar y la clase del fallo, sin datos sensibles.
- **Lector tolerante y catálogos extensibles:** un valor de catálogo nuevo se lee como `DESCONOCIDO`;
  tolerar al leer no es aceptar: la regla de negocio sigue rechazando, pero solo lo suyo.
- **Invariantes que se nombran, no que lanzan** donde la entrada viene de fuera (patrón Notification);
  los objetos de dominio propios sí pueden exigirlas en el constructor (`investigacion/03` regla 8).
- **Casos especiales con nombre** en vez de `null` o de una excepción (Special Case, Null Object).
- **Barrera de fallos:** el borde atrapa también los defectos de programación de lo que envuelve, pero
  nunca los `Error` de la JVM.

Se comprueba con pruebas que alimentan entradas rotas (tipo incorrecto, valor nuevo, `null`, lista con
`null`, documento vacío) y esperan un resultado, no una excepción. Fuentes: Fowler, *TolerantReader*,
*Notification* y *Special Case* (PoEAA); plantilla `endpoint` EP-15 y EP-16; Nygard, *Release It!*
(bulkheads y fallar rápido en el borde correcto).

Cómo se pregunta: nunca «¿quieres código defensivo?». Por ejemplo: «Si llega `"resultado": "aplazada"`,
que este lado no conoce, ¿qué pasa?», con opciones: ★ se lee como `DESCONOCIDO` y se trata solo esa
entrada, o falla la lectura de todo el mensaje.
