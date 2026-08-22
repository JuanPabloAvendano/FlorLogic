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

export type EstadoCaptura = 'borrador' | 'cerrada'
export type EstadoEnvio = 'pendiente' | 'enviado' | 'conflicto'

export interface Captura {
  id: string
  camaId: string
  bloqueId: string
  plantillaId: string
  fecha: string
  estado: EstadoCaptura
  creadaEn: string
  actualizadaEn: string
  cerradaEn: string | null
  dispositivoId: string
  usuario: string
  /** Con qué versión del catálogo se capturó. Sin esto no se puede reconstruir nada (CN-26). */
  versionCatalogo: string
}

export interface LineaCaptura {
  id: string
  capturaId: string
  variedadId: string
  lineas: number | null
  cantidad: number | null
  obse: string
  creadaEn: string
  actualizadaEn: string
}

export interface ItemOutbox {
  id: string
  capturaId: string
  estado: EstadoEnvio
  intentos: number
  creadoEn: string
  ultimoError: string | null
}

export interface Meta {
  clave: string
  valor: string
}

/** Una captura con sus secciones, que es como se muestra y como se va a sincronizar. */
export interface CapturaCompleta {
  captura: Captura
  lineas: LineaCaptura[]
}

// --- instrumentación (D8) --------------------------------------------------------
// R3 del plan: la demo tiene que producir las MEDIDAS DE RESPUESTA que hoy no existen.
// Sin esto la sesión con el cliente deja opiniones, no números.

export type TipoEvento =
  | 'captura.inicio'
  | 'captura.cierre'
  | 'captura.reapertura'
  | 'seccion.agregada'
  | 'seccion.quitada'
  | 'regla.rechazo'
  | 'sugerencia.usada'
  | 'toque'
  | 'sincronizacion'

export interface Evento {
  id: string
  tipo: TipoEvento
  ts: string
  capturaId: string | null
  camaId: string | null
  variante: string | null
  detalle: string
}

// --- sincronización (D6) ---------------------------------------------------------

/** Lo que el servidor simulado guarda. En el producto esto vive del otro lado de la red. */
export interface RegistroServidor {
  id: string
  camaId: string
  fecha: string
  dispositivoId: string
  recibidoEn: string
  lineas: LineaCaptura[]
}

/**
 * DEC-05 y CNF: cuando dos capturas de la misma cama chocan, NO gana la más reciente.
 * Se dejan las dos y una persona decide.
 */
export interface Conflicto {
  id: string
  camaId: string
  fecha: string
  capturaLocalId: string
  capturaServidorId: string
  detectadoEn: string
  resueltoEn: string | null
  ganadora: string | null
}
