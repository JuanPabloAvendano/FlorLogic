import type { ReactNode } from 'react'

/**
 * Piezas de interfaz del prototipo.
 *
 * Reglas que vienen de las respuestas de caracterización (ver PLAN_DEMO_CAPTURA.md §3.4):
 *  - UXP: blanco de toque mínimo de 56 px, para acertar con guantes.
 *  - ACC: nada se entiende solo por el color; todo estado lleva además una palabra o un símbolo.
 *  - UXP: los mensajes dicen qué hacer, no solo qué pasó.
 */

export function Etiqueta({ tono, children }: { tono: 'ok' | 'aviso' | 'alerta' | 'neutro'; children: ReactNode }) {
  const simbolo = { ok: '✓', aviso: '!', alerta: '✕', neutro: '·' }[tono]
  return (
    <span className={`etiqueta etiqueta--${tono}`}>
      <span aria-hidden="true">{simbolo}</span> {children}
    </span>
  )
}

export function Boton({ onClick, children, variante = 'normal' }: {
  onClick: () => void
  children: ReactNode
  variante?: 'normal' | 'tenue'
}) {
  return (
    <button type="button" className={`boton boton--${variante}`} onClick={onClick}>
      {children}
    </button>
  )
}

export function Tarjeta({ onClick, children }: { onClick?: () => void; children: ReactNode }) {
  if (!onClick) return <div className="tarjeta">{children}</div>
  return (
    <button type="button" className="tarjeta tarjeta--pulsable" onClick={onClick}>
      {children}
    </button>
  )
}

export function Vacio({ titulo, detalle }: { titulo: string; detalle: string }) {
  return (
    <div className="vacio">
      <p className="vacio__titulo">{titulo}</p>
      <p className="vacio__detalle">{detalle}</p>
    </div>
  )
}
