/**
 * Pantallas de consulta: bloques, camas, el detalle de una cama y la comparación con el papel.
 */

import { boton, cifra, etiqueta, h, tabla, tarjeta, titulo, vacio } from '../vista'
import type { Ctx } from './comun'
import { numero } from './comun'

export function pantallaInicio(ctx: Ctx): HTMLElement {
  const r = ctx.catalogo.resumen()
  const raiz = h('div', {})
  raiz.append(titulo('Bloques', `${r.bloques} bloques · ${r.camas} camas · ${r.siembras} registros del formato real`))

  const lista = h('div', { clase: 'lista' })
  for (const b of ctx.catalogo.bloques()) {
    const camas = ctx.catalogo.camas(b.id)
    const divididas = camas.filter((c) => ctx.catalogo.estaDividida(c.id)).length
    lista.append(
      tarjeta(() => ctx.ir({ n: 'camas', bloqueId: b.id }),
        h('div', { clase: 'tarjeta__fila' },
          h('span', { clase: 'tarjeta__codigo', texto: `Bloque ${b.codigo}` }),
          h('span', { clase: 'tarjeta__flecha', texto: '›', atributos: { 'aria-hidden': 'true' } }),
        ),
        h('div', { clase: 'tarjeta__meta' },
          h('span', { texto: `${camas.length} camas` }),
          ...b.tiposFlor.filter(Boolean).map((t) => h('span', { texto: t })),
          divididas > 0 ? etiqueta('neutro', `${divididas} divididas`) : null,
        ),
      ),
    )
  }
  raiz.append(lista)
  return raiz
}

export function pantallaCamas(ctx: Ctx, bloqueId: string): HTMLElement {
  const bloque = ctx.catalogo.bloque(bloqueId)
  const camas = ctx.catalogo.camas(bloqueId)
  const raiz = h('div', {})
  raiz.append(boton('‹ Bloques', () => ctx.ir({ n: 'inicio' }), 'tenue'))
  raiz.append(titulo(`Bloque ${bloque?.codigo ?? '—'}`, `${camas.length} camas con registro en el formato`))

  const lista = h('div', { clase: 'lista lista--densa' })
  for (const c of camas) {
    const siembras = ctx.catalogo.siembras(c.id)
    const conAviso = siembras.some((s) => ctx.catalogo.anomalias(s.id).length > 0)
    lista.append(
      tarjeta(() => ctx.ir({ n: 'cama', camaId: c.id }),
        h('div', { clase: 'tarjeta__fila' },
          h('span', { clase: 'tarjeta__codigo', texto: `Cama ${c.codigo}` }),
          h('span', { clase: 'tarjeta__flecha', texto: '›', atributos: { 'aria-hidden': 'true' } }),
        ),
        h('div', { clase: 'tarjeta__meta' },
          h('span', {
            texto: siembras.map((s) => ctx.catalogo.variedad(s.variedadId)?.nombre).join(' + ') || 'sin siembra',
          }),
          ctx.catalogo.plantasDeLaCama(c.id) > 0
            ? h('span', { texto: `${numero(ctx.catalogo.plantasDeLaCama(c.id))} plantas` })
            : null,
          ctx.catalogo.estaDividida(c.id) ? etiqueta('neutro', 'dividida') : null,
          conAviso ? etiqueta('aviso', 'revisar') : null,
        ),
      ),
    )
  }
  raiz.append(lista)
  return raiz
}

export function pantallaCama(ctx: Ctx, camaId: string): HTMLElement {
  const cama = ctx.catalogo.cama(camaId)
  if (!cama) return vacio('Cama no encontrada', 'El catálogo local no la tiene.')
  const bloque = ctx.catalogo.bloque(cama.bloqueId)
  const raiz = h('div', {})

  raiz.append(boton(`‹ Bloque ${bloque?.codigo ?? ''}`, () => ctx.ir({ n: 'camas', bloqueId: cama.bloqueId }), 'tenue'))
  raiz.append(titulo(`Cama ${cama.codigo}`, `Bloque ${bloque?.codigo ?? '—'} · lo que dice el formato en papel`))

  // D3 y D7: las dos variantes conviven a propósito. El cliente escoge capturando, no opinando.
  raiz.append(
    h('div', { clase: 'acciones' },
      boton('Capturar · rejilla', () => ctx.ir({ n: 'captura', camaId, variante: 'rejilla' })),
      boton('Capturar · guiada', () => ctx.ir({ n: 'captura', camaId, variante: 'guiada' })),
      boton('Papel contra app', () => ctx.ir({ n: 'papel', camaId }), 'tenue'),
    ),
  )

  const porFecha = new Map<string, ReturnType<typeof ctx.catalogo.siembras>>()
  for (const s of ctx.catalogo.siembras(camaId)) porFecha.set(s.fecha, [...(porFecha.get(s.fecha) ?? []), s])

  for (const [fecha, items] of porFecha) {
    const seccion = h('section', { clase: 'jornada' })
    seccion.append(
      h('h2', { clase: 'jornada__fecha' },
        fecha,
        items.length > 1 ? etiqueta('neutro', `${items.length} secciones`) : null,
      ),
    )
    for (const s of items) {
      const v = ctx.catalogo.variedad(s.variedadId)
      const datos = h('dl', { clase: 'datos' })
      const par = (k: string, valor: string) => { datos.append(h('dt', { texto: k }), h('dd', { texto: valor })) }
      if (s.lineas !== null) par('# líneas', String(s.lineas))
      if (s.cantidad !== null) par('Cantidad', numero(s.cantidad))
      if (s.razonPlantasPorLinea !== null) par('Plantas por línea', String(s.razonPlantasPorLinea))
      if (s.lote) par('Lote', s.lote)
      if (s.calibre) par('Calibre', s.calibre)
      if (s.proveedor) par('Proveedor', s.proveedor)
      if (s.contenedor) par('Contenedor', s.contenedor)
      if (s.obse) par('OBSE', `${s.obse} (significado desconocido)`)
      if (s.observaciones) par('Observaciones', s.observaciones)

      const caja = h('div', { clase: 'seccion' },
        h('div', { clase: 'seccion__cabeza' },
          h('span', { clase: 'seccion__variedad', texto: v?.nombre ?? '—' }),
          h('span', { clase: 'seccion__hoja', texto: `${s.hojaOrigen} · fila ${s.filaOrigen}` }),
        ),
        datos,
      )
      for (const a of ctx.catalogo.anomalias(s.id)) {
        caja.append(h('p', { clase: 'aviso' }, etiqueta('aviso', 'revisar'), ' ' + a.detalle))
      }
      seccion.append(caja)
    }
    raiz.append(seccion)
  }
  return raiz
}

/**
 * D9 · papel contra app.
 *
 * CNF: «seguir llenando el formato en papel en paralelo los primeros meses» y «comparar lo
 * capturado en la aplicación contra el formato del mismo día». No es un parche de transición:
 * es la funcionalidad que hace visible si la app está capturando bien.
 */
export async function pantallaPapel(ctx: Ctx, camaId: string): Promise<HTMLElement> {
  const cama = ctx.catalogo.cama(camaId)
  if (!cama) return vacio('Cama no encontrada', 'El catálogo local no la tiene.')
  const raiz = h('div', {})
  raiz.append(boton('‹ Cama ' + cama.codigo, () => ctx.ir({ n: 'cama', camaId }), 'tenue'))
  raiz.append(titulo(`Papel contra app · cama ${cama.codigo}`, 'Lo que dice el formato y lo que quedó capturado'))

  const papel = ctx.catalogo.siembras(camaId)
  const capturas = await ctx.repo.capturasDeCama(camaId)
  const app = capturas.flatMap((c) => c.lineas.map((l) => ({ ...l, fecha: c.captura.fecha })))

  const totalPapel = papel.reduce((t, s) => t + (s.cantidad ?? 0), 0)
  const totalApp = app.reduce((t, l) => t + (l.cantidad ?? 0), 0)
  const diferencia = totalApp - totalPapel
  const pct = totalPapel ? Math.round((diferencia / totalPapel) * 1000) / 10 : null

  raiz.append(
    h('div', { clase: 'cifras' },
      cifra(numero(totalPapel), 'plantas en el papel'),
      cifra(numero(totalApp), 'plantas en la app'),
      cifra(pct === null ? '—' : `${pct > 0 ? '+' : ''}${pct}%`, 'diferencia'),
    ),
  )

  raiz.append(h('h2', { clase: 'seccion__titulo', texto: 'Formato en papel' }))
  raiz.append(tabla(['Fecha', 'Variedad', '# líneas', 'Cantidad'],
    papel.map((s) => [s.fecha, ctx.catalogo.variedad(s.variedadId)?.nombre ?? '—', String(s.lineas ?? '—'), numero(s.cantidad)]),
  ))

  raiz.append(h('h2', { clase: 'seccion__titulo', texto: 'Capturado en la aplicación' }))
  if (app.length === 0) {
    raiz.append(vacio('Todavía nada', 'Capturá esta cama y volvé para comparar.'))
  } else {
    raiz.append(tabla(['Fecha', 'Variedad', '# líneas', 'Cantidad'],
      app.map((l) => [l.fecha, ctx.catalogo.variedad(l.variedadId)?.nombre ?? '—', String(l.lineas ?? '—'), numero(l.cantidad)]),
    ))
  }
  return raiz
}
