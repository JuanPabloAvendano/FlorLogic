/**
 * D6 · bandeja de salida.
 *
 * Lo que hay que poder enseñarle al cliente aquí es el comportamiento, no la tecnología:
 *  - lo capturado se queda en el dispositivo y se ve que se queda;
 *  - reintentar no duplica (CN-24);
 *  - cuando dos capturas de la misma cama chocan **se quedan las dos** y decide una persona
 *    (DEC-05 y la respuesta de Juan en CNF).
 */

import type { Conflicto } from '../modelo'
import { resolverConflicto, resumenOutbox, sincronizar } from '../sincronizacion'
import { aviso, boton, cifra, etiqueta, h, tabla, titulo, vaciar, vacio } from '../vista'
import type { Ctx } from './comun'
import { numero } from './comun'

export async function pantallaBandeja(ctx: Ctx): Promise<HTMLElement> {
  const raiz = h('div', {})
  raiz.append(titulo('Bandeja de salida', 'Lo capturado que todavía no está del otro lado'))

  const cuerpo = h('div', {})
  raiz.append(cuerpo)

  const pintar = async () => {
    vaciar(cuerpo)
    const items = await ctx.repo.outbox()
    const r = resumenOutbox(items)
    const conflictos = (await ctx.repo.conflictos()).filter((c) => !c.resueltoEn)
    const enServidor = await ctx.repo.registrosServidor()

    cuerpo.append(
      h('div', { clase: 'cifras' },
        cifra(r.pendientes, 'sin enviar'),
        cifra(r.enviados, 'enviados'),
        cifra(r.conflictos, 'en conflicto'),
        cifra(enServidor.length, 'camas en el servidor'),
      ),
    )

    cuerpo.append(
      h('div', { clase: 'acciones' },
        boton('Sincronizar', async () => {
          const res = await sincronizar(ctx.repo, { demoraMs: 120 })
          await pintar()
          ctx.refrescarShell()
          cuerpo.prepend(aviso(res.conflictos ? 'aviso' : 'ok',
            `${res.enviados} enviadas, ${res.conflictos} en conflicto.`,
            res.detalle[0] ?? ''))
        }),
        // Para la sesión: enseñar que sin señal no se pierde nada y el reintento no duplica.
        boton('Sincronizar sin señal', async () => {
          const res = await sincronizar(ctx.repo, { fallar: true })
          await pintar()
          cuerpo.prepend(aviso('aviso', `${res.fallidos} quedaron en la bandeja.`,
            'Nada se perdió: se reintenta cuando haya conexión.'))
        }, 'tenue'),
      ),
    )

    if (conflictos.length > 0) {
      cuerpo.append(h('h2', { clase: 'seccion__titulo', texto: 'Decide una persona' }))
      for (const c of conflictos) cuerpo.append(await tarjetaConflicto(ctx, c, pintar))
    }

    cuerpo.append(h('h2', { clase: 'seccion__titulo', texto: 'Envíos' }))
    if (items.length === 0) {
      cuerpo.append(vacio('La bandeja está vacía', 'Cerrá una cama y aparece acá.'))
      return
    }
    const filas = await Promise.all(items
      .sort((a, b) => b.id.localeCompare(a.id))
      .map(async (i) => {
        const captura = await ctx.repo.capturaPorId(i.capturaId)
        const cama = captura ? ctx.catalogo.cama(captura.camaId) : undefined
        const tono = i.estado === 'enviado' ? 'ok' : i.estado === 'conflicto' ? 'alerta' : 'aviso'
        return [
          `Cama ${cama?.codigo ?? '—'}`,
          captura?.fecha ?? '—',
          etiqueta(tono, i.estado),
          String(i.intentos),
          i.ultimoError ?? '',
        ]
      }))
    cuerpo.append(tabla(['Cama', 'Fecha', 'Estado', 'Intentos', 'Último error'], filas))
  }

  await pintar()
  return raiz
}

async function tarjetaConflicto(ctx: Ctx, c: Conflicto, pintar: () => Promise<void>): Promise<HTMLElement> {
  const cama = ctx.catalogo.cama(c.camaId)
  const local = await ctx.repo.lineasDe(c.capturaLocalId)
  const servidor = (await ctx.repo.registrosServidor()).find((r) => r.id === c.capturaServidorId)
  const suma = (ls: { cantidad: number | null }[]) => ls.reduce((t, l) => t + (l.cantidad ?? 0), 0)

  return h('div', { clase: 'seccion seccion--conflicto' },
    h('div', { clase: 'seccion__cabeza' },
      h('span', { clase: 'seccion__variedad', texto: `Cama ${cama?.codigo ?? '—'} · ${c.fecha}` }),
      etiqueta('alerta', 'conflicto'),
    ),
    h('p', { clase: 'nota', texto: 'Las dos capturas se conservan. Ninguna se sobreescribió sola.' }),
    tabla(['Versión', 'Secciones', 'Plantas'], [
      ['La de este dispositivo', String(local.length), numero(suma(local))],
      ['La que ya estaba', String(servidor?.lineas.length ?? 0), numero(suma(servidor?.lineas ?? []))],
    ]),
    h('div', { clase: 'acciones' },
      boton('Queda la de este dispositivo', async () => {
        await resolverConflicto(ctx.repo, c, 'local'); await pintar(); ctx.refrescarShell()
      }),
      boton('Queda la que ya estaba', async () => {
        await resolverConflicto(ctx.repo, c, 'servidor'); await pintar(); ctx.refrescarShell()
      }, 'tenue'),
    ),
  )
}
