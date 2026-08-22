/**
 * Sincronización de la demo.
 *
 * El «servidor» vive en el mismo dispositivo, en su propio almacén. No es una simplificación
 * perezosa: lo que hay que probar antes de la sesión con el cliente no es la red, es el
 * COMPORTAMIENTO — idempotencia, reintento y qué pasa cuando dos capturas de la misma cama chocan.
 * Cuando exista el backend (FastAPI + PostgreSQL), esta es la única pieza que se cambia.
 *
 * Decisiones que implementa:
 *  - CN-24 · idempotente: el id del envío es el de la captura, reenviar no duplica.
 *  - CN-24 · cronológica: la bandeja se procesa en orden de UUID v7, que es orden de creación.
 *  - DEC-05 y CNF · ante un choque **se dejan las dos capturas y decide una persona**.
 *    Nunca gana la más reciente sola.
 */

import type { Conflicto, ItemOutbox, RegistroServidor } from './modelo'
import { uuidv7 } from './id'
import type { Repositorio } from './repositorio'

export interface ResultadoSync {
  enviados: number
  conflictos: number
  fallidos: number
  detalle: string[]
}

export interface OpcionesSync {
  /** Simula que la red se cae, para poder enseñar el reintento en la sesión. */
  fallar?: boolean
  /** Milisegundos de espera por envío, para que el estado «enviando» se alcance a ver. */
  demoraMs?: number
}

const dormir = (ms: number) => new Promise<void>((r) => setTimeout(r, ms))

export async function sincronizar(repo: Repositorio, opciones: OpcionesSync = {}): Promise<ResultadoSync> {
  const salida: ResultadoSync = { enviados: 0, conflictos: 0, fallidos: 0, detalle: [] }
  const pendientes = (await repo.outbox())
    .filter((i) => i.estado === 'pendiente')
    .sort((a, b) => a.id.localeCompare(b.id)) // UUID v7 = orden cronológico

  for (const item of pendientes) {
    const captura = await repo.capturaPorId(item.capturaId)
    if (!captura) {
      await repo.guardarItemOutbox({ ...item, estado: 'enviado', ultimoError: 'la captura ya no existe' })
      continue
    }

    if (opciones.demoraMs) await dormir(opciones.demoraMs)

    if (opciones.fallar) {
      await repo.guardarItemOutbox({
        ...item,
        intentos: item.intentos + 1,
        ultimoError: 'no hay conexión con el servidor',
      })
      salida.fallidos += 1
      salida.detalle.push(`Cama ${captura.camaId}: sin conexión, queda en la bandeja (intento ${item.intentos + 1}).`)
      continue
    }

    const enServidor = await repo.registroServidorDe(captura.camaId, captura.fecha)

    // Idempotencia: si ya está el MISMO envío, no es conflicto, es un reintento.
    if (enServidor && enServidor.id !== captura.id) {
      const conflicto: Conflicto = {
        id: uuidv7(),
        camaId: captura.camaId,
        fecha: captura.fecha,
        capturaLocalId: captura.id,
        capturaServidorId: enServidor.id,
        detectadoEn: new Date().toISOString(),
        resueltoEn: null,
        ganadora: null,
      }
      await repo.guardarConflicto(conflicto)
      await repo.guardarItemOutbox({ ...item, estado: 'conflicto', intentos: item.intentos + 1 })
      salida.conflictos += 1
      salida.detalle.push('Esa cama ya tenía una captura de la misma fecha. Se dejaron las dos: decide una persona.')
      await repo.anotar('sincronizacion', { capturaId: captura.id, camaId: captura.camaId, detalle: 'conflicto' })
      continue
    }

    const registro: RegistroServidor = {
      id: captura.id,
      camaId: captura.camaId,
      fecha: captura.fecha,
      dispositivoId: captura.dispositivoId,
      recibidoEn: new Date().toISOString(),
      lineas: await repo.lineasDe(captura.id),
    }
    await repo.guardarEnServidor(registro)
    await repo.guardarItemOutbox({ ...item, estado: 'enviado', intentos: item.intentos + 1, ultimoError: null })
    salida.enviados += 1
    await repo.anotar('sincronizacion', { capturaId: captura.id, camaId: captura.camaId, detalle: 'enviado' })
  }

  return salida
}

/** Mediación humana (DEC-05): alguien mira las dos y escoge. Ninguna se borra. */
export async function resolverConflicto(
  repo: Repositorio,
  conflicto: Conflicto,
  ganadora: 'local' | 'servidor',
): Promise<void> {
  if (ganadora === 'local') {
    const captura = await repo.capturaPorId(conflicto.capturaLocalId)
    if (captura) {
      await repo.guardarEnServidor({
        id: captura.id,
        camaId: captura.camaId,
        fecha: captura.fecha,
        dispositivoId: captura.dispositivoId,
        recibidoEn: new Date().toISOString(),
        lineas: await repo.lineasDe(captura.id),
      })
    }
  }

  const item = (await repo.outbox()).find((i) => i.capturaId === conflicto.capturaLocalId)
  if (item) await repo.guardarItemOutbox({ ...item, estado: 'enviado', ultimoError: null })

  await repo.guardarConflicto({
    ...conflicto,
    resueltoEn: new Date().toISOString(),
    ganadora: ganadora === 'local' ? conflicto.capturaLocalId : conflicto.capturaServidorId,
  })
}

export function resumenOutbox(items: ItemOutbox[]) {
  return {
    pendientes: items.filter((i) => i.estado === 'pendiente').length,
    enviados: items.filter((i) => i.estado === 'enviado').length,
    conflictos: items.filter((i) => i.estado === 'conflicto').length,
  }
}
