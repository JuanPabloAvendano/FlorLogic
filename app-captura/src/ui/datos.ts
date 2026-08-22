/**
 * Procedencia y hallazgos: de dónde salió cada dato de la demo y qué encontró la regla en el
 * histórico real del cliente. Es la pantalla que se abre cuando alguien pregunta «¿y esto de
 * dónde lo sacaron?».
 */

import { aviso, boton, cifra, etiqueta, h, tabla, titulo } from '../vista'
import type { Ctx } from './comun'

export async function pantallaDatos(ctx: Ctx, alBorrar: () => Promise<void>): Promise<HTMLElement> {
  const s = ctx.catalogo.semilla
  const r = ctx.catalogo.resumen()
  const local = await ctx.repo.resumen()

  const raiz = h('div', {})
  raiz.append(titulo('Datos', s.advertencia))

  raiz.append(
    h('div', { clase: 'cifras' },
      cifra(r.bloques, 'bloques'),
      cifra(r.camas, 'camas'),
      cifra(r.variedades, 'variedades'),
      cifra(r.siembras, 'registros'),
      cifra(r.divididas, `camas divididas (${r.porcentajeDivididas}%)`),
      cifra(r.revisar, 'filas a revisar'),
    ),
  )

  raiz.append(h('h2', { clase: 'seccion__titulo', texto: 'En este dispositivo' }))
  raiz.append(
    h('div', { clase: 'cifras' },
      cifra(local.capturas, 'capturas'),
      cifra(local.borradores, 'borradores'),
      cifra(local.lineas, 'secciones'),
      cifra(local.pendientes, 'sin enviar'),
    ),
  )
  raiz.append(h('div', { clase: 'acciones' },
    boton('Borrar lo capturado en la demo', alBorrar, 'peligro')))

  raiz.append(h('h2', { clase: 'seccion__titulo', texto: 'Lo que la regla encuentra en el histórico real' }))
  for (const a of s.anomalias.filter((x) => x.severidad === 'revisar')) {
    raiz.append(h('p', { clase: 'aviso' }, etiqueta('aviso', a.tipo), ' ' + a.detalle,
      a.hoja ? h('span', { clase: 'aviso__origen', texto: ` — ${a.hoja}, fila ${a.fila}` }) : null))
  }

  raiz.append(h('h2', { clase: 'seccion__titulo', texto: 'Camas divididas (confirma DEC-14)' }))
  for (const a of s.anomalias.filter((x) => x.severidad === 'informativo')) {
    raiz.append(h('p', { clase: 'aviso aviso--neutro' }, etiqueta('neutro', a.tipo), ' ' + a.detalle))
  }

  raiz.append(h('h2', { clase: 'seccion__titulo', texto: 'Preguntas abiertas que dejó el archivo' }))
  for (const a of s.avisos) raiz.append(h('p', { clase: 'aviso aviso--neutro', texto: a }))

  raiz.append(h('h2', { clase: 'seccion__titulo', texto: 'Plantas por línea derivadas' }))
  raiz.append(tabla(['Variedad', 'Tipo', 'P/línea', 'Confianza'],
    s.variedades.map((v) => [
      v.nombre,
      v.tipoFlor || '—',
      String(v.plantasPorLinea ?? '—'),
      etiqueta(v.confianza === 'alta' ? 'ok' : v.confianza === 'en-disputa' ? 'alerta' : 'aviso', v.confianza),
    ])))

  raiz.append(h('h2', { clase: 'seccion__titulo', texto: 'Reglas cargadas' }))
  raiz.append(aviso('neutro', `${ctx.motor.version()} · tolerancia de la razón: ${Math.round(ctx.motor.tolerancia() * 100)}%.`,
    'Se cambian en configuracion/reglas.v1.json, sin tocar la aplicación.'))
  raiz.append(tabla(['Id', 'Severidad', 'Qué revisa'],
    ctx.motor.reglas().map((rg) => [
      rg.id,
      etiqueta(rg.severidad === 'dura' ? 'alerta' : 'aviso', rg.severidad),
      rg.mensaje,
    ])))

  raiz.append(h('p', { clase: 'nota', texto: `Semilla ${s.version} generada el ${s.generado} desde ${s.origen}.` }))
  return raiz
}
