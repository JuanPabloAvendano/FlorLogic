/**
 * Motor de reglas.
 *
 * Interpreta `configuracion/reglas.v1.json`; no hay ninguna regla escrita en el código.
 * CNF: se pueden cambiar los rangos sin publicar una versión nueva de la aplicación (CN-22),
 * y funcionan igual con y sin conexión — aquí no hay ninguna llamada a la red (CN-13).
 *
 * Dos severidades:
 *   dura   -> impide cerrar la cama. CNF: «rechace un dato fuera del rango posible».
 *   blanda -> avisa y queda anotada. CNF: «muestre siempre el motivo por el cual rechazó».
 *
 * [!] Tensión conocida, para la sesión con el cliente: Juan respondió **NO** a «advertir sin
 * bloquear cuando un dato se aleja de lo histórico», pero la regla de la razón (RG-06) no puede
 * ser otra cosa que un aviso mientras no sepamos si Astroi es un error de digitación o una
 * densidad distinta. Es una de las preguntas que la demo va a poner sobre la mesa.
 */

import type { CatalogoReglas, Regla, Variedad } from './tipos'
import type { LineaCaptura } from './modelo'

export interface Hallazgo {
  reglaId: string
  severidad: 'dura' | 'blanda'
  campo: string | null
  lineaId: string | null
  mensaje: string
  queHacer: string
}

export interface ContextoCaptura {
  lineas: LineaCaptura[]
  variedadPorId: (id: string) => Variedad | undefined
  /** Otras capturas de la misma cama y el mismo día, sin contar esta. */
  otrasCapturasHoy: number
}

const valorDe = (linea: LineaCaptura, campo: string): unknown =>
  (linea as unknown as Record<string, unknown>)[campo]

function vacio(v: unknown): boolean {
  return v === null || v === undefined || v === '' || (typeof v === 'number' && Number.isNaN(v))
}

function hallazgo(r: Regla, extra: Partial<Hallazgo> = {}): Hallazgo {
  return {
    reglaId: r.id,
    severidad: r.severidad,
    campo: r.campo ?? null,
    lineaId: null,
    mensaje: r.mensaje,
    queHacer: r.queHacer,
    ...extra,
  }
}

export class MotorReglas {
  // Campo explícito en vez de propiedad de parámetro: así el motor se puede probar con el
  // borrado de tipos de Node, sin navegador y sin compilar.  (node --experimental-strip-types)
  private readonly catalogo: CatalogoReglas

  constructor(catalogo: CatalogoReglas) {
    this.catalogo = catalogo
  }

  version(): string {
    return this.catalogo.version
  }

  tolerancia(): number {
    return this.catalogo.toleranciaRazon
  }

  reglas(): Regla[] {
    return this.catalogo.reglas
  }

  /** Revisa una sección suelta. Es lo que se ejecuta mientras se escribe. */
  revisarLinea(linea: LineaCaptura, variedad: Variedad | undefined): Hallazgo[] {
    const salida: Hallazgo[] = []
    for (const r of this.catalogo.reglas) {
      if (r.ambito !== 'linea') continue

      if (r.tipo === 'obligatorio' && r.campo && vacio(valorDe(linea, r.campo))) {
        salida.push(hallazgo(r, { lineaId: linea.id }))
      }

      if (r.tipo === 'rango' && r.campo) {
        const v = valorDe(linea, r.campo)
        if (typeof v === 'number' && ((r.min !== undefined && v < r.min) || (r.max !== undefined && v > r.max))) {
          salida.push(hallazgo(r, {
            lineaId: linea.id,
            mensaje: `${r.mensaje} (${v}; se espera entre ${r.min} y ${r.max})`,
          }))
        }
      }

      if (r.tipo === 'razonPlantasPorLinea') {
        const esperado = variedad?.plantasPorLinea ?? null
        if (esperado && linea.lineas && linea.cantidad) {
          const razon = linea.cantidad / linea.lineas
          const desvio = Math.abs(razon - esperado) / esperado
          if (desvio > this.catalogo.toleranciaRazon) {
            const disputa = variedad?.confianza === 'en-disputa'
            salida.push(hallazgo(r, {
              lineaId: linea.id,
              mensaje:
                `${linea.cantidad} ÷ ${linea.lineas} = ${Math.round(razon * 100) / 100} plantas por línea, ` +
                `pero ${variedad?.nombre} va en ${esperado}.`,
              queHacer: disputa
                ? `Ojo: ${variedad?.nombre} no tiene un valor confiable en el histórico. Confirmá con el supervisor.`
                : r.queHacer,
            }))
          }
        }
      }
    }
    return salida
  }

  /** Revisa la cama entera. Es lo que se ejecuta al intentar cerrar. */
  revisarCaptura(ctx: ContextoCaptura): Hallazgo[] {
    const salida: Hallazgo[] = []

    for (const linea of ctx.lineas) {
      salida.push(...this.revisarLinea(linea, ctx.variedadPorId(linea.variedadId)))
    }

    for (const r of this.catalogo.reglas) {
      if (r.ambito !== 'captura') continue

      if (r.tipo === 'sinSecciones' && ctx.lineas.length === 0) {
        salida.push(hallazgo(r))
      }

      if (r.tipo === 'variedadRepetida') {
        const vistas = new Set<string>()
        for (const l of ctx.lineas) {
          if (l.variedadId && vistas.has(l.variedadId)) {
            salida.push(hallazgo(r, {
              lineaId: l.id,
              mensaje: `${r.mensaje} (${ctx.variedadPorId(l.variedadId)?.nombre ?? l.variedadId})`,
            }))
          }
          if (l.variedadId) vistas.add(l.variedadId)
        }
      }

      if (r.tipo === 'camaRepetidaMismoDia' && ctx.otrasCapturasHoy > 0) {
        salida.push(hallazgo(r))
      }
    }
    return salida
  }

  static bloquean(hallazgos: Hallazgo[]): Hallazgo[] {
    return hallazgos.filter((h) => h.severidad === 'dura')
  }
}

export async function cargarReglas(url = 'reglas.v1.json'): Promise<MotorReglas> {
  const res = await fetch(new URL(url, document.baseURI).toString(), { cache: 'no-cache' })
  if (!res.ok) throw new Error(`No se pudo leer ${url} (${res.status})`)
  return new MotorReglas((await res.json()) as CatalogoReglas)
}
