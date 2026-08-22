/**
 * Verificación del D2: que un dato capturado sobreviva a cerrar la aplicación.
 *
 * Corre dentro de un navegador de verdad, contra IndexedDB de verdad, y entre la fase 1 y la
 * fase 2 la página se RECARGA. Es la única forma honesta de comprobar la promesa del D2.
 *
 * Se ejecuta con pruebas/correr_persistencia.py
 */
import { uuidv7, msDelUuid } from '../src/id'
import { Repositorio } from '../src/repositorio'
import type { Semilla } from '../src/tipos'

const CAMA = 'blq-12-cma-37' // la cama dividida del formato real: Lineth + Cooper

interface Resultado { nombre: string; ok: boolean; detalle: string }

function comprobar(lista: Resultado[], nombre: string, real: unknown, esperado: unknown): void {
  const ok = JSON.stringify(real) === JSON.stringify(esperado)
  lista.push({ nombre, ok, detalle: ok ? '' : `esperado ${JSON.stringify(esperado)}, obtuvo ${JSON.stringify(real)}` })
}

/** Antes de recargar: instalar catálogo, capturar y cerrar. */
export async function fase1(): Promise<Resultado[]> {
  const r: Resultado[] = []

  const semilla = (await (await fetch('seed.json')).json()) as Semilla
  const repo = await Repositorio.abrir()

  comprobar(r, 'la primera siembra escribe', await repo.sembrarCatalogo(semilla), true)
  comprobar(r, 'la segunda siembra no hace nada', await repo.sembrarCatalogo(semilla), false)

  const local = await repo.semillaLocal()
  comprobar(r, 'bloques en la base local', local.bloques.length, 4)
  comprobar(r, 'camas en la base local', local.camas.length, 38)
  comprobar(r, 'variedades en la base local', local.variedades.length, 12)
  comprobar(r, 'siembras en la base local', local.siembras.length, 41)
  comprobar(r, 'el diagnóstico también se guardó', local.anomalias.length, semilla.anomalias.length)

  const captura = await repo.crearCaptura({
    camaId: CAMA, bloqueId: 'blq-12', plantillaId: 'novedad_siembra',
    fecha: '2026-08-21', usuario: 'prueba',
  })
  comprobar(r, 'la captura nace en borrador', captura.estado, 'borrador')
  comprobar(r, 'la captura recuerda el catálogo', captura.versionCatalogo.startsWith('seed.v1'), true)

  await repo.agregarLinea(captura.id, { variedadId: 'var-lineth', lineas: 148, cantidad: 2812 })
  await repo.agregarLinea(captura.id, { variedadId: 'var-cooper', lineas: 21, cantidad: 399 })

  const antes = await repo.capturasDeCama(CAMA)
  comprobar(r, 'una captura en la cama', antes.length, 1)
  comprobar(r, 'con dos secciones (DEC-14)', antes[0].lineas.length, 2)

  await repo.cerrarCaptura(captura.id)
  await repo.cerrarCaptura(captura.id) // CN-24: cerrar dos veces no puede encolar dos veces
  const res = await repo.resumen()
  comprobar(r, 'un solo item en la bandeja de salida', res.pendientes, 1)
  comprobar(r, 'sin borradores abiertos', res.borradores, 0)

  // UUID v7: únicos y ordenados aunque se generen todos en el mismo milisegundo
  const ids = Array.from({ length: 2000 }, () => uuidv7(1_755_000_000_000))
  comprobar(r, '2000 identificadores distintos en el mismo ms', new Set(ids).size, 2000)
  comprobar(r, 'y en orden de generación', [...ids].sort().join() === ids.join(), true)
  comprobar(r, 'la marca de tiempo va dentro del id', msDelUuid(ids[0]), 1_755_000_000_000)

  localStorage.setItem('prueba.captura', captura.id)
  repo.almacen.cerrar()
  return r
}

/** Después de recargar: nada se perdió, y las correcciones se comportan. */
export async function fase2(): Promise<Resultado[]> {
  const r: Resultado[] = []
  const repo = await Repositorio.abrir()
  const capturaId = localStorage.getItem('prueba.captura')!

  const local = await repo.semillaLocal()
  comprobar(r, 'el catálogo sigue ahí tras recargar', local.camas.length, 38)

  const capturas = await repo.capturasDeCama(CAMA)
  comprobar(r, 'la captura sobrevivió a la recarga', capturas.length, 1)
  comprobar(r, 'y sus dos secciones también', capturas[0].lineas.length, 2)
  comprobar(r, 'sigue cerrada', capturas[0].captura.estado, 'cerrada')
  comprobar(r, 'los datos son los mismos',
    capturas[0].lineas.map((l) => l.cantidad).sort((a, b) => (a ?? 0) - (b ?? 0)), [399, 2812])
  comprobar(r, 'el orden de las secciones es el de captura',
    capturas[0].lineas.map((l) => l.variedadId), ['var-lineth', 'var-cooper'])

  // CNF: corregir antes de que se sincronice tiene que ser posible
  await repo.reabrirCaptura(capturaId)
  const trasReabrir = await repo.resumen()
  comprobar(r, 'al corregir sale de la bandeja de salida', trasReabrir.pendientes, 0)
  comprobar(r, 'y vuelve a ser borrador', trasReabrir.borradores, 1)

  // Resembrar el catálogo NUNCA puede borrar lo capturado (CN-26)
  const semilla = (await (await fetch('seed.json')).json()) as Semilla
  const forzada: Semilla = { ...semilla, generado: '2099-01-01T00:00:00+00:00' }
  await repo.sembrarCatalogo(forzada)
  const trasResembrar = await repo.resumen()
  comprobar(r, 'resembrar el catálogo no toca lo capturado', trasResembrar.capturas, 1)
  comprobar(r, 'ni las secciones', trasResembrar.lineas, 2)

  await repo.borrarCapturas()
  const limpio = await repo.resumen()
  comprobar(r, 'borrar la demo deja cero capturas', limpio.capturas, 0)
  comprobar(r, 'pero el catálogo queda intacto', (await repo.semillaLocal()).camas.length, 38)

  repo.almacen.cerrar()
  return r
}

declare global {
  interface Window { fase1: typeof fase1; fase2: typeof fase2 }
}
window.fase1 = fase1
window.fase2 = fase2
