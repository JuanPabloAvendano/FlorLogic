/**
 * Arranque de la demo de captura de FlorLogic.
 *
 * Orden de encendido, que no es casual:
 *   1. se abre la base local del dispositivo;
 *   2. se instala y versiona el catálogo (CN-26: primero el catálogo, después capturar);
 *   3. se cargan las reglas desde su archivo (CN-22);
 *   4. recién ahí se pinta.
 *
 * A partir de la segunda vez todo sale de IndexedDB: `seed.json` es el instalador, no la fuente.
 */

import { Catalogo, cargarSemilla } from './catalogo'
import { cargarReglas } from './reglas'
import { Repositorio } from './repositorio'
import { boton, etiqueta, h, vaciar, vacio } from './vista'
import { pantallaBandeja } from './ui/bandeja'
import { pantallaCamas, pantallaCama, pantallaInicio, pantallaPapel } from './ui/catalogoPantallas'
import { pantallaCaptura } from './ui/captura'
import { pantallaDatos } from './ui/datos'
import { pantallaMedidas } from './ui/medidas'
import type { Ctx, Vista } from './ui/comun'

/** Sin autenticación: el ingreso sin conexión (SEG, CN-23) no es parte de la demo. */
const USUARIO = 'demo'

async function arrancar(): Promise<void> {
  const raiz = document.getElementById('raiz')
  if (!raiz) return

  raiz.append(vacio('Abriendo…', 'Instalando el catálogo en el dispositivo.'))

  let repo: Repositorio
  let catalogo: Catalogo
  let motor: Awaited<ReturnType<typeof cargarReglas>>
  try {
    repo = await Repositorio.abrir()
    await repo.sembrarCatalogo(await cargarSemilla())
    catalogo = new Catalogo(await repo.semillaLocal())
    motor = await cargarReglas()
  } catch (e) {
    vaciar(raiz)
    raiz.append(vacio('No se pudo abrir la demo',
      `${e instanceof Error ? e.message : String(e)}. Regenerá el catálogo con "python construir.py".`))
    return
  }

  let vista: Vista = { n: 'inicio' }
  const capturaActiva = { id: null as string | null, camaId: null as string | null, variante: null as Ctx['capturaActiva']['variante'] }

  const cabecera = h('header', { clase: 'cabecera' })
  const contenido = h('main', { clase: 'contenido' })
  const navegacion = h('nav', { clase: 'navegacion' })
  const pie = h('footer', { clase: 'pie' })

  const ctx: Ctx = {
    repo, catalogo, motor, usuario: USUARIO,
    ir: (v) => { vista = v; void pintar() },
    refrescarShell: () => { void pintarCabecera() },
    vistaActual: () => vista,
    capturaActiva,
  }

  async function pintarCabecera(): Promise<void> {
    const r = await repo.resumen()
    vaciar(cabecera)
    cabecera.append(
      h('button', { clase: 'cabecera__marca', tipo: 'button', onClick: () => ctx.ir({ n: 'inicio' }) },
        h('strong', { texto: 'FlorLogic' }),
        h('span', { clase: 'cabecera__sub', texto: `Captura · ${catalogo.semilla.fincas[0]?.nombre ?? '—'}` }),
      ),
      h('div', { clase: 'cabecera__estado' },
        // DSP: la aplicación dice siempre si está trabajando sin conexión.
        etiqueta(navigator.onLine ? 'ok' : 'aviso', navigator.onLine ? 'En línea' : 'Sin conexión'),
        // UXP: qué se sincronizó y qué no, siempre a la vista.
        r.pendientes > 0 ? etiqueta('aviso', `${r.pendientes} sin enviar`) : null,
      ),
    )
  }

  function pintarNavegacion(): void {
    vaciar(navegacion)
    const items: { texto: string; destino: Vista; activo: boolean }[] = [
      { texto: 'Bloques', destino: { n: 'inicio' }, activo: ['inicio', 'camas', 'cama', 'captura', 'papel'].includes(vista.n) },
      { texto: 'Bandeja', destino: { n: 'bandeja' }, activo: vista.n === 'bandeja' },
      { texto: 'Medidas', destino: { n: 'medidas' }, activo: vista.n === 'medidas' },
      { texto: 'Datos', destino: { n: 'datos' }, activo: vista.n === 'datos' },
    ]
    for (const i of items) {
      navegacion.append(boton(i.texto, () => ctx.ir(i.destino), i.activo ? 'normal' : 'tenue'))
    }
  }

  async function pintar(): Promise<void> {
    if (vista.n !== 'captura') capturaActiva.id = null
    pintarNavegacion()
    void pintarCabecera()
    vaciar(contenido)
    contenido.append(await construirPantalla())
    window.scrollTo(0, 0)
  }

  async function construirPantalla(): Promise<HTMLElement> {
    switch (vista.n) {
      case 'inicio': return pantallaInicio(ctx)
      case 'camas': return pantallaCamas(ctx, vista.bloqueId)
      case 'cama': return pantallaCama(ctx, vista.camaId)
      case 'captura': return pantallaCaptura(ctx, vista.camaId, vista.variante)
      case 'papel': return pantallaPapel(ctx, vista.camaId)
      case 'bandeja': return pantallaBandeja(ctx)
      case 'medidas': return pantallaMedidas(ctx)
      case 'datos': return pantallaDatos(ctx, async () => {
        await repo.borrarCapturas()
        await pintar()
      })
    }
  }

  vaciar(raiz)
  raiz.append(h('div', { clase: 'app' }, cabecera, contenido, navegacion, pie))
  pie.append(h('span', { texto: `${catalogo.semilla.version} · ${motor.version()} · ${catalogo.semilla.origen}` }))

  // D8 · contador de toques. Solo cuenta mientras hay una cama abierta, y nunca puede
  // interrumpir la captura: si falla, falla en silencio.
  contenido.addEventListener('pointerdown', () => {
    if (vista.n === 'captura' && capturaActiva.id) {
      void repo.anotar('toque', {
        capturaId: capturaActiva.id, camaId: capturaActiva.camaId, variante: capturaActiva.variante,
      })
    }
  })

  window.addEventListener('online', () => void pintarCabecera())
  window.addEventListener('offline', () => void pintarCabecera())

  await pintar()
}

if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register(new URL('sw.js', document.baseURI).toString()).catch(() => {
      /* sin service worker la demo funciona igual, solo pierde el arranque sin conexión */
    })
  })
}

void arrancar()
