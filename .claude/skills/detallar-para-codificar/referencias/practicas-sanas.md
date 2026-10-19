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
