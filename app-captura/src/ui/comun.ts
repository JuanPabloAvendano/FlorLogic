import type { Catalogo } from '../catalogo'
import type { MotorReglas } from '../reglas'
import type { Repositorio } from '../repositorio'

export type Variante = 'rejilla' | 'guiada'

export type Vista =
  | { n: 'inicio' }
  | { n: 'camas'; bloqueId: string }
  | { n: 'cama'; camaId: string }
  | { n: 'captura'; camaId: string; variante: Variante }
  | { n: 'papel'; camaId: string }
  | { n: 'bandeja' }
  | { n: 'medidas' }
  | { n: 'datos' }

export interface Ctx {
  repo: Repositorio
  catalogo: Catalogo
  motor: MotorReglas
  usuario: string
  ir: (v: Vista) => void
  /** Vuelve a pintar la cabecera (contadores de la bandeja de salida). */
  refrescarShell: () => void
  /** La vista que se está mostrando. La usa el contador de toques. */
  vistaActual: () => Vista
  /** Identificador de la captura abierta, para poder atribuirle los toques. */
  capturaActiva: { id: string | null; camaId: string | null; variante: Variante | null }
}

export const hoy = (): string => new Date().toISOString().slice(0, 10)

export const numero = (n: number | null | undefined): string =>
  n === null || n === undefined ? '—' : n.toLocaleString('es-CO')

export const NOMBRE_VARIANTE: Record<Variante, string> = {
  rejilla: 'Rejilla como el papel',
  guiada: 'Guiada, una a la vez',
}
