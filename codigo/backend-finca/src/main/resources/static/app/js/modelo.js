/**
 * Modelo de captura.
 *
 * Esto SÍ sobrevive a la demo: cuando la interfaz se bote, estas formas y el contrato de
 * sincronización son lo que se lleva al producto (PLAN_DEMO_CAPTURA.md, regla R2).
 *
 * Decisiones que ya están tomadas y que se ven aquí:
 *  - DEC-14: la cama se divide en secciones. Una captura tiene N líneas, una por sección.
 *  - La unidad de campo es #líneas, no el área (§2.2 del plan).
 *  - CNF: se puede guardar incompleto y marcarlo pendiente  ->  estado 'borrador'.
 *  - CN-24: la sincronización es idempotente y cronológica  ->  id UUID v7 del dispositivo.
 *  - CN-26: el catálogo va versionado  ->  cada captura recuerda con qué catálogo se hizo.
 */
export {};
