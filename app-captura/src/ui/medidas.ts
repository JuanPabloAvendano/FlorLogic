/**
 * D8 · medidas de respuesta.
 *
 * Esta pantalla es el producto real de la demo. Los escenarios `ESC-nnn` están bloqueados porque
 * nadie sabe cuánto tarda capturar una cama de verdad; de aquí sale ese número, medido con el
 * cliente y no estimado por nosotros.
 *
 * El objetivo que Juan puso en RND es «menos de 30 segundos por cama». Aquí se ve si se cumple,
 * con cuál de las dos variantes, y a costa de cuántos toques.
 */

import { csvDeEventos, csvDeMedidas, descargar, medidasPorCaptura, resumir } from '../metricas'
import { aviso, boton, cifra, etiqueta, h, tabla, titulo, vacio } from '../vista'
import type { Ctx } from './comun'

const META_SEGUNDOS = 30

export async function pantallaMedidas(ctx: Ctx): Promise<HTMLElement> {
  const eventos = await ctx.repo.eventos()
  const medidas = medidasPorCaptura(eventos)
  const r = resumir(medidas)

  const raiz = h('div', {})
  raiz.append(titulo('Medidas', 'Lo que la demo midió, no lo que nosotros calculamos'))

  if (r.camas === 0) {
    raiz.append(vacio('Todavía no hay camas cerradas', 'Capturá una cama completa y volvé acá.'))
    return raiz
  }

  raiz.append(
    h('div', { clase: 'cifras' },
      cifra(r.camas, 'camas cerradas'),
      cifra(r.segundosMediana ?? '—', 'segundos (mediana)'),
      cifra(r.segundosPeor ?? '—', 'segundos (la peor)'),
      cifra(r.toquesMediana ?? '—', 'toques (mediana)'),
      cifra(r.rechazos, 'rechazos de regla'),
    ),
  )

  if (r.segundosMediana !== null) {
    const cumple = r.segundosMediana <= META_SEGUNDOS
    raiz.append(aviso(cumple ? 'ok' : 'aviso',
      cumple
        ? `La mediana está en ${r.segundosMediana} s, por debajo de los ${META_SEGUNDOS} s que pide RND.`
        : `La mediana está en ${r.segundosMediana} s, por encima de los ${META_SEGUNDOS} s que pide RND.`,
      'Ese número es el que va en la medida de respuesta del escenario.'))
  }

  const variantes = Object.entries(r.porVariante)
  if (variantes.length > 1) {
    raiz.append(h('h2', { clase: 'seccion__titulo', texto: 'Rejilla contra guiada' }))
    raiz.append(h('p', { clase: 'nota', texto: 'La contradicción 2 del plan, resuelta con cronómetro en vez de con opiniones.' }))
    raiz.append(tabla(['Variante', 'Camas', 'Segundos (mediana)'],
      variantes.map(([nombre, v]) => [nombre, String(v.camas), String(v.segundosMediana ?? '—')])))
  }

  raiz.append(h('h2', { clase: 'seccion__titulo', texto: 'Cama por cama' }))
  raiz.append(tabla(['Cama', 'Variante', 'Segundos', 'Toques', 'Secciones', 'Rechazos'],
    medidas.map((m) => [
      ctx.catalogo.cama(m.camaId ?? '')?.codigo ?? '—',
      m.variante ?? '—',
      m.segundos === null ? etiqueta('aviso', 'sin cerrar') : String(m.segundos),
      String(m.toques),
      String(m.secciones),
      String(m.rechazos),
    ])))

  const sello = new Date().toISOString().slice(0, 10)
  raiz.append(
    h('div', { clase: 'acciones' },
      boton('Descargar medidas (CSV)', () => descargar(`florlogic-medidas-${sello}.csv`, csvDeMedidas(medidas))),
      boton('Descargar eventos (CSV)', () => descargar(`florlogic-eventos-${sello}.csv`, csvDeEventos(eventos)), 'tenue'),
    ),
  )
  raiz.append(h('p', { clase: 'nota', texto: 'Los CSV salen con punto y coma y BOM: Excel en español los abre sin preguntar.' }))
  return raiz
}
