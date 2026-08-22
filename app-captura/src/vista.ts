/**
 * Capa de vista mínima. Sin framework y sin dependencias.
 *
 * Por qué no React: el proyecto se construye para botarse (regla R2 del plan) y el precio de
 * React aquí no es el peso, es el `npm install` — que en el campo, con la conexión de una finca,
 * es exactamente el paso que falla. Con esto el proyecto entero se compila con `tsc` y se sirve
 * como archivos estáticos.
 *
 * No hay reconciliación: cada pantalla arma su DOM una vez y las partes que cambian se vuelven a
 * pintar a mano. Es más código en las listas, pero ningún campo pierde el foco mientras se escribe
 * — que con guantes y bajo el sol no es un detalle.
 */

export type Hijo = Node | string | number | null | undefined | false

export interface Props {
  clase?: string
  texto?: string
  onClick?: (e: MouseEvent) => void
  onInput?: (e: Event) => void
  onChange?: (e: Event) => void
  valor?: string
  tipo?: string
  modoEntrada?: string
  marcador?: string
  deshabilitado?: boolean
  atributos?: Record<string, string>
}

export function h<K extends keyof HTMLElementTagNameMap>(
  etiqueta: K,
  props: Props = {},
  ...hijos: Hijo[]
): HTMLElementTagNameMap[K] {
  const el = document.createElement(etiqueta)
  if (props.clase) el.className = props.clase
  if (props.texto !== undefined) el.textContent = props.texto
  if (props.onClick) el.addEventListener('click', props.onClick as EventListener)
  if (props.onInput) el.addEventListener('input', props.onInput)
  if (props.onChange) el.addEventListener('change', props.onChange)
  if (props.tipo && 'type' in el) (el as unknown as HTMLInputElement).type = props.tipo
  if (props.valor !== undefined && 'value' in el) (el as unknown as HTMLInputElement).value = props.valor
  if (props.modoEntrada) el.setAttribute('inputmode', props.modoEntrada)
  if (props.marcador) el.setAttribute('placeholder', props.marcador)
  if (props.deshabilitado) el.setAttribute('disabled', 'true')
  for (const [k, v] of Object.entries(props.atributos ?? {})) el.setAttribute(k, v)

  for (const hijo of hijos) {
    if (hijo === null || hijo === undefined || hijo === false) continue
    el.append(hijo instanceof Node ? hijo : document.createTextNode(String(hijo)))
  }
  return el
}

export function vaciar(el: Element): void {
  while (el.firstChild) el.removeChild(el.firstChild)
}

// --- piezas comunes --------------------------------------------------------------
// ACC: ningún estado se entiende solo por el color; todos llevan símbolo y palabra.

export type Tono = 'ok' | 'aviso' | 'alerta' | 'neutro'

const SIMBOLO: Record<Tono, string> = { ok: '✓', aviso: '!', alerta: '✕', neutro: '·' }

export function etiqueta(tono: Tono, texto: string): HTMLElement {
  return h('span', { clase: `etiqueta etiqueta--${tono}` },
    h('span', { texto: SIMBOLO[tono], atributos: { 'aria-hidden': 'true' } }),
    ' ' + texto,
  )
}

export function boton(
  texto: string,
  onClick: () => void,
  variante: 'normal' | 'tenue' | 'peligro' = 'normal',
): HTMLButtonElement {
  return h('button', { clase: `boton boton--${variante}`, texto, tipo: 'button', onClick })
}

export function tarjeta(onClick: (() => void) | null, ...hijos: Hijo[]): HTMLElement {
  return onClick
    ? h('button', { clase: 'tarjeta tarjeta--pulsable', tipo: 'button', onClick }, ...hijos)
    : h('div', { clase: 'tarjeta' }, ...hijos)
}

export function titulo(texto: string, sub?: string): DocumentFragment {
  const f = document.createDocumentFragment()
  f.append(h('h1', { clase: 'titulo', texto }))
  if (sub) f.append(h('p', { clase: 'subtitulo', texto: sub }))
  return f
}

export function aviso(tono: Tono, texto: string, queHacer?: string): HTMLElement {
  return h('p', { clase: tono === 'neutro' ? 'aviso aviso--neutro' : 'aviso' },
    etiqueta(tono, tono === 'alerta' ? 'no se puede' : tono === 'aviso' ? 'revisar' : 'nota'),
    ' ' + texto,
    queHacer ? h('span', { clase: 'aviso__quehacer', texto: ' ' + queHacer }) : null,
  )
}

export function vacio(tituloTexto: string, detalle: string): HTMLElement {
  return h('div', { clase: 'vacio' },
    h('p', { clase: 'vacio__titulo', texto: tituloTexto }),
    h('p', { clase: 'vacio__detalle', texto: detalle }),
  )
}

export function campo(etiquetaTexto: string, control: HTMLElement): HTMLElement {
  return h('label', { clase: 'campo' },
    h('span', { clase: 'campo__etiqueta', texto: etiquetaTexto }),
    control,
  )
}

export function entradaNumero(valor: string, alCambiar: (v: string) => void): HTMLInputElement {
  const input = h('input', {
    clase: 'campo__control',
    modoEntrada: 'numeric',
    valor,
    // CNF: cada dato queda guardado de inmediato. No hay botón de guardar.
    onInput: (e) => {
      const el = e.target as HTMLInputElement
      el.value = el.value.replace(/[^\d]/g, '')
      alCambiar(el.value)
    },
  })
  return input
}

export function selector(
  opciones: { valor: string; texto: string }[],
  seleccionado: string,
  alCambiar: (v: string) => void,
): HTMLSelectElement {
  const sel = h('select', {
    clase: 'campo__control',
    onChange: (e) => alCambiar((e.target as HTMLSelectElement).value),
  })
  for (const o of opciones) {
    const op = h('option', { valor: o.valor, texto: o.texto })
    if (o.valor === seleccionado) op.selected = true
    sel.append(op)
  }
  return sel
}

export function cifra(n: number | string, etiquetaTexto: string): HTMLElement {
  return h('div', { clase: 'cifra' },
    h('span', { clase: 'cifra__n', texto: String(n) }),
    h('span', { clase: 'cifra__etiqueta', texto: etiquetaTexto }),
  )
}

export function tabla(cabeceras: string[], filas: Hijo[][]): HTMLElement {
  return h('div', { clase: 'tabla__caja' },
    h('table', { clase: 'tabla' },
      h('thead', {}, h('tr', {}, ...cabeceras.map((c) => h('th', { texto: c })))),
      h('tbody', {}, ...filas.map((f) => h('tr', {}, ...f.map((c) => h('td', {}, c))))),
    ),
  )
}
