/**
 * Medidas de respuesta.
 *
 * Esta es la razón de ser de la demo (regla R3 del plan): los escenarios de calidad `ESC-nnn`
 * están bloqueados porque nadie sabe cuánto tarda de verdad capturar una cama. Aquí no se estima:
 * se mide lo que pasó en la mesa.
 *
 * Lo que sale de aquí se pega directo en el escenario:
 *   «... el supervisor registra la cama y el sistema la deja guardada
 *    en menos de N segundos y con M toques.»
 */

import type { Evento } from './modelo'

export interface MedidaCaptura {
  capturaId: string
  camaId: string | null
  variante: string | null
  segundos: number | null
  toques: number
  secciones: number
  rechazos: number
  cerrada: boolean
}

export interface Resumen {
  camas: number
  segundosMediana: number | null
  segundosPeor: number | null
  toquesMediana: number | null
  rechazos: number
  porVariante: Record<string, { camas: number; segundosMediana: number | null }>
}

function mediana(valores: number[]): number | null {
  if (valores.length === 0) return null
  const orden = [...valores].sort((a, b) => a - b)
  const m = Math.floor(orden.length / 2)
  const v = orden.length % 2 ? orden[m] : (orden[m - 1] + orden[m]) / 2
  return Math.round(v * 10) / 10
}

export function medidasPorCaptura(eventos: Evento[]): MedidaCaptura[] {
  const porCaptura = new Map<string, Evento[]>()
  for (const e of eventos) {
    if (!e.capturaId) continue
    porCaptura.set(e.capturaId, [...(porCaptura.get(e.capturaId) ?? []), e])
  }

  const salida: MedidaCaptura[] = []
  for (const [capturaId, lista] of porCaptura) {
    const inicio = lista.find((e) => e.tipo === 'captura.inicio')
    const cierre = lista.filter((e) => e.tipo === 'captura.cierre').pop()
    salida.push({
      capturaId,
      camaId: inicio?.camaId ?? lista[0]?.camaId ?? null,
      variante: inicio?.variante ?? null,
      segundos:
        inicio && cierre
          ? Math.round(((new Date(cierre.ts).getTime() - new Date(inicio.ts).getTime()) / 1000) * 10) / 10
          : null,
      toques: lista.filter((e) => e.tipo === 'toque').length,
      secciones: lista.filter((e) => e.tipo === 'seccion.agregada').length,
      rechazos: lista.filter((e) => e.tipo === 'regla.rechazo').length,
      cerrada: Boolean(cierre),
    })
  }
  return salida.sort((a, b) => a.capturaId.localeCompare(b.capturaId))
}

export function resumir(medidas: MedidaCaptura[]): Resumen {
  const cerradas = medidas.filter((m) => m.cerrada && m.segundos !== null)
  const segundos = cerradas.map((m) => m.segundos as number)
  const porVariante: Resumen['porVariante'] = {}
  for (const m of cerradas) {
    const clave = m.variante ?? 'sin variante'
    porVariante[clave] ??= { camas: 0, segundosMediana: null }
    porVariante[clave].camas += 1
  }
  for (const clave of Object.keys(porVariante)) {
    porVariante[clave].segundosMediana = mediana(
      cerradas.filter((m) => (m.variante ?? 'sin variante') === clave).map((m) => m.segundos as number),
    )
  }
  return {
    camas: cerradas.length,
    segundosMediana: mediana(segundos),
    segundosPeor: segundos.length ? Math.max(...segundos) : null,
    toquesMediana: mediana(cerradas.map((m) => m.toques)),
    rechazos: medidas.reduce((t, m) => t + m.rechazos, 0),
    porVariante,
  }
}

const escapar = (v: unknown) => `"${String(v ?? '').replace(/"/g, '""')}"`

/** CSV con punto y coma: es lo que Excel en español abre sin preguntar nada. */
export function csvDeEventos(eventos: Evento[]): string {
  const cabecera = ['ts', 'tipo', 'capturaId', 'camaId', 'variante', 'detalle']
  const filas = eventos
    .slice()
    .sort((a, b) => a.id.localeCompare(b.id))
    .map((e) => [e.ts, e.tipo, e.capturaId, e.camaId, e.variante, e.detalle].map(escapar).join(';'))
  return [cabecera.join(';'), ...filas].join('\r\n')
}

export function csvDeMedidas(medidas: MedidaCaptura[]): string {
  const cabecera = ['capturaId', 'camaId', 'variante', 'segundos', 'toques', 'secciones', 'rechazos', 'cerrada']
  const filas = medidas.map((m) =>
    [m.capturaId, m.camaId, m.variante, m.segundos, m.toques, m.secciones, m.rechazos, m.cerrada ? 'sí' : 'no']
      .map(escapar)
      .join(';'),
  )
  return [cabecera.join(';'), ...filas].join('\r\n')
}

export function descargar(nombre: string, contenido: string): void {
  // BOM para que Excel reconozca los acentos.
  const blob = new Blob(['﻿' + contenido], { type: 'text/csv;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = nombre
  a.click()
  setTimeout(() => URL.revokeObjectURL(url), 1000)
}
