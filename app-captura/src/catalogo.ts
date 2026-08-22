import type { Anomalia, Bloque, Cama, Semilla, Siembra, Variedad } from './tipos'

/**
 * Índice en memoria de la semilla.
 *
 * D1 solo lee. La persistencia local (Dexie) y la captura entran en el D2 del plan;
 * por eso aquí no hay ninguna escritura y el índice se puede botar y reconstruir.
 */
export class Catalogo {
  readonly semilla: Semilla
  private readonly camasPorBloque = new Map<string, Cama[]>()
  private readonly siembrasPorCama = new Map<string, Siembra[]>()
  private readonly variedadPorId = new Map<string, Variedad>()
  private readonly anomaliasPorSiembra = new Map<string, Anomalia[]>()

  constructor(semilla: Semilla) {
    this.semilla = semilla
    for (const c of semilla.camas) {
      const lista = this.camasPorBloque.get(c.bloqueId) ?? []
      lista.push(c)
      this.camasPorBloque.set(c.bloqueId, lista)
    }
    for (const s of semilla.siembras) {
      const lista = this.siembrasPorCama.get(s.camaId) ?? []
      lista.push(s)
      this.siembrasPorCama.set(s.camaId, lista)
    }
    for (const v of semilla.variedades) this.variedadPorId.set(v.id, v)
    for (const a of semilla.anomalias) {
      if (!a.siembraId) continue
      const lista = this.anomaliasPorSiembra.get(a.siembraId) ?? []
      lista.push(a)
      this.anomaliasPorSiembra.set(a.siembraId, lista)
    }
  }

  bloques(): Bloque[] { return this.semilla.bloques }
  camas(bloqueId: string): Cama[] { return this.camasPorBloque.get(bloqueId) ?? [] }
  siembras(camaId: string): Siembra[] { return this.siembrasPorCama.get(camaId) ?? [] }
  variedad(id: string): Variedad | undefined { return this.variedadPorId.get(id) }
  anomalias(siembraId: string): Anomalia[] { return this.anomaliasPorSiembra.get(siembraId) ?? [] }

  bloque(id: string): Bloque | undefined { return this.semilla.bloques.find((b) => b.id === id) }
  cama(id: string): Cama | undefined { return this.semilla.camas.find((c) => c.id === id) }

  /** Una cama está dividida cuando el mismo día tiene más de una sección sembrada (DEC-14). */
  secciones(camaId: string, fecha: string): Siembra[] {
    return this.siembras(camaId).filter((s) => s.fecha === fecha)
  }

  estaDividida(camaId: string): boolean {
    const porFecha = new Map<string, number>()
    for (const s of this.siembras(camaId)) {
      porFecha.set(s.fecha, (porFecha.get(s.fecha) ?? 0) + 1)
    }
    return [...porFecha.values()].some((n) => n > 1)
  }

  plantasDeLaCama(camaId: string): number {
    return this.siembras(camaId).reduce((total, s) => total + (s.cantidad ?? 0), 0)
  }

  resumen() {
    const revisar = this.semilla.anomalias.filter((a) => a.severidad === 'revisar').length
    const divididas = this.semilla.camas.filter((c) => this.estaDividida(c.id)).length
    return {
      bloques: this.semilla.bloques.length,
      camas: this.semilla.camas.length,
      variedades: this.semilla.variedades.length,
      siembras: this.semilla.siembras.length,
      revisar,
      divididas,
      porcentajeDivididas: this.semilla.camas.length
        ? Math.round((divididas / this.semilla.camas.length) * 100)
        : 0,
    }
  }
}

export async function cargarSemilla(): Promise<Semilla> {
  // `base: './'` en vite.config.ts hace que esto funcione igual en la raíz y en un subdirectorio.
  const url = new URL('seed.json', document.baseURI).toString()
  const res = await fetch(url, { cache: 'no-cache' })
  if (!res.ok) throw new Error(`No se pudo leer seed.json (${res.status})`)
  return (await res.json()) as Semilla
}
