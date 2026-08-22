/**
 * Repositorio de la demo: catálogo + capturas, todo en el dispositivo.
 *
 * Reglas que ya se ven aquí y que vienen de decisiones cerradas:
 *  - CN-26 · El catálogo se descarga y se versiona ANTES de capturar. Resembrar el catálogo
 *    nunca toca lo capturado: son almacenes distintos y se vacían por separado.
 *  - CNF   · Cada dato queda guardado de inmediato. No hay botón de guardar.
 *  - CNF   · Se puede dejar una cama incompleta y marcarla pendiente  ->  estado 'borrador'.
 *  - CN-24 · Al cerrar, la captura entra a la bandeja de salida con su propio id idempotente.
 *
 * Después de la primera carga, la aplicación lee de aquí y no de `seed.json`: el archivo es el
 * instalador del catálogo, no la fuente en tiempo de ejecución.
 */

import { Almacen, type Esquema } from './almacen'
import { idDispositivo, uuidv7 } from './id'
import type {
  Captura, CapturaCompleta, Conflicto, Evento, ItemOutbox, LineaCaptura, Meta,
  RegistroServidor, TipoEvento,
} from './modelo'
import type { Bloque, Cama, Plantilla, Semilla, Siembra, Variedad } from './tipos'

export const ESQUEMA: Esquema = {
  nombre: 'florlogic-captura',
  version: 2,
  almacenes: [
    { nombre: 'meta', clave: 'clave' },
    { nombre: 'bloques', clave: 'id' },
    { nombre: 'camas', clave: 'id', indices: [{ nombre: 'porBloque', campo: 'bloqueId' }] },
    { nombre: 'variedades', clave: 'id' },
    { nombre: 'plantillas', clave: 'id' },
    { nombre: 'siembras', clave: 'id', indices: [{ nombre: 'porCama', campo: 'camaId' }] },
    {
      nombre: 'capturas',
      clave: 'id',
      indices: [
        { nombre: 'porCama', campo: 'camaId' },
        { nombre: 'porEstado', campo: 'estado' },
      ],
    },
    { nombre: 'lineas', clave: 'id', indices: [{ nombre: 'porCaptura', campo: 'capturaId' }] },
    { nombre: 'outbox', clave: 'id', indices: [{ nombre: 'porEstado', campo: 'estado' }] },
    // D8 · instrumentación: de aquí salen las medidas de respuesta de los escenarios.
    { nombre: 'eventos', clave: 'id', indices: [{ nombre: 'porTipo', campo: 'tipo' }] },
    // D6 · el «servidor» de la demo vive en el mismo dispositivo, pero del otro lado del sync.
    { nombre: 'servidor', clave: 'id', indices: [{ nombre: 'porCamaFecha', campo: ['camaId', 'fecha'] }] },
    { nombre: 'conflictos', clave: 'id', indices: [{ nombre: 'porCama', campo: 'camaId' }] },
  ],
}

/** Almacenes del catálogo: se reemplazan al resembrar. Los de captura NUNCA se tocan. */
const ALMACENES_CATALOGO = ['bloques', 'camas', 'variedades', 'plantillas', 'siembras']
const CLAVE_VERSION = 'versionCatalogo'
const CLAVE_DIAGNOSTICO = 'diagnostico'

/** Lo de la semilla que no es catálogo: procedencia y hallazgos. Se guarda entero en `meta`. */
type Diagnostico = Pick<
  Semilla,
  'version' | 'generado' | 'origen' | 'advertencia' | 'empresa' | 'fincas' | 'anomalias' | 'avisos'
>

const ahora = () => new Date().toISOString()

export class Repositorio {
  private constructor(readonly almacen: Almacen, private versionCatalogo: string) {}

  static async abrir(): Promise<Repositorio> {
    const almacen = await Almacen.abrir(ESQUEMA)
    const meta = await almacen.obtener<Meta>('meta', CLAVE_VERSION)
    return new Repositorio(almacen, meta?.valor ?? '')
  }

  version(): string {
    return this.versionCatalogo
  }

  // -- catálogo ------------------------------------------------------------------

  /**
   * Deja el catálogo local igual al de la semilla. Idempotente: si la versión coincide no escribe.
   * Devuelve true cuando hubo que sembrar.
   */
  async sembrarCatalogo(semilla: Semilla): Promise<boolean> {
    const version = `${semilla.version}·${semilla.generado}`
    if (this.versionCatalogo === version) return false

    await this.almacen.vaciar(ALMACENES_CATALOGO)
    await this.almacen.ponerVarios('bloques', semilla.bloques)
    await this.almacen.ponerVarios('camas', semilla.camas)
    await this.almacen.ponerVarios('variedades', semilla.variedades)
    await this.almacen.ponerVarios('plantillas', semilla.plantillas)
    await this.almacen.ponerVarios('siembras', semilla.siembras)

    const diagnostico: Diagnostico = {
      version: semilla.version,
      generado: semilla.generado,
      origen: semilla.origen,
      advertencia: semilla.advertencia,
      empresa: semilla.empresa,
      fincas: semilla.fincas,
      anomalias: semilla.anomalias,
      avisos: semilla.avisos,
    }
    await this.almacen.poner<Meta>('meta', { clave: CLAVE_DIAGNOSTICO, valor: JSON.stringify(diagnostico) })
    await this.almacen.poner<Meta>('meta', { clave: CLAVE_VERSION, valor: version })
    this.versionCatalogo = version
    return true
  }

  /** Reconstruye la semilla desde la base local. Es lo que la aplicación usa para pintar. */
  async semillaLocal(): Promise<Semilla> {
    const meta = await this.almacen.obtener<Meta>('meta', CLAVE_DIAGNOSTICO)
    if (!meta) throw new Error('No hay catálogo sembrado en este dispositivo')
    const diagnostico = JSON.parse(meta.valor) as Diagnostico
    const [bloques, camas, variedades, plantillas, siembras] = await Promise.all([
      this.almacen.todos<Bloque>('bloques'),
      this.almacen.todos<Cama>('camas'),
      this.almacen.todos<Variedad>('variedades'),
      this.almacen.todos<Plantilla>('plantillas'),
      this.almacen.todos<Siembra>('siembras'),
    ])
    return { ...diagnostico, bloques, camas, variedades, plantillas, siembras }
  }

  bloques = () => this.almacen.todos<Bloque>('bloques')
  camas = (bloqueId: string) => this.almacen.porIndice<Cama>('camas', 'porBloque', bloqueId)
  variedades = () => this.almacen.todos<Variedad>('variedades')
  plantillas = () => this.almacen.todos<Plantilla>('plantillas')
  siembras = (camaId: string) => this.almacen.porIndice<Siembra>('siembras', 'porCama', camaId)

  // -- captura -------------------------------------------------------------------

  async crearCaptura(datos: {
    camaId: string
    bloqueId: string
    plantillaId: string
    fecha: string
    usuario: string
  }): Promise<Captura> {
    const t = ahora()
    const captura: Captura = {
      id: uuidv7(),
      camaId: datos.camaId,
      bloqueId: datos.bloqueId,
      plantillaId: datos.plantillaId,
      fecha: datos.fecha,
      estado: 'borrador',
      creadaEn: t,
      actualizadaEn: t,
      cerradaEn: null,
      dispositivoId: idDispositivo(),
      usuario: datos.usuario,
      versionCatalogo: this.versionCatalogo,
    }
    await this.almacen.poner('capturas', captura)
    return captura
  }

  async agregarLinea(capturaId: string, datos: {
    variedadId: string
    lineas: number | null
    cantidad: number | null
    obse?: string
  }): Promise<LineaCaptura> {
    const t = ahora()
    const linea: LineaCaptura = {
      id: uuidv7(),
      capturaId,
      variedadId: datos.variedadId,
      lineas: datos.lineas,
      cantidad: datos.cantidad,
      obse: datos.obse ?? '',
      creadaEn: t,
      actualizadaEn: t,
    }
    await this.almacen.poner('lineas', linea)
    await this.tocar(capturaId)
    return linea
  }

  async actualizarLinea(linea: LineaCaptura): Promise<void> {
    await this.almacen.poner('lineas', { ...linea, actualizadaEn: ahora() })
    await this.tocar(linea.capturaId)
  }

  async borrarLinea(linea: LineaCaptura): Promise<void> {
    await this.almacen.borrar('lineas', linea.id)
    await this.tocar(linea.capturaId)
  }

  private async tocar(capturaId: string): Promise<void> {
    const c = await this.almacen.obtener<Captura>('capturas', capturaId)
    if (c) await this.almacen.poner('capturas', { ...c, actualizadaEn: ahora() })
  }

  /**
   * Cierra la cama y la encola. El identificador del item de la bandeja es el de la captura:
   * reintentar dos veces no puede crear dos envíos (CN-24, idempotencia).
   */
  async cerrarCaptura(capturaId: string): Promise<Captura | undefined> {
    const c = await this.almacen.obtener<Captura>('capturas', capturaId)
    if (!c) return undefined
    const t = ahora()
    const cerrada: Captura = { ...c, estado: 'cerrada', cerradaEn: t, actualizadaEn: t }
    await this.almacen.poner('capturas', cerrada)

    const yaEncolado = await this.almacen.obtener<ItemOutbox>('outbox', capturaId)
    if (!yaEncolado) {
      const item: ItemOutbox = {
        id: capturaId,
        capturaId,
        estado: 'pendiente',
        intentos: 0,
        creadoEn: t,
        ultimoError: null,
      }
      await this.almacen.poner('outbox', item)
    }
    return cerrada
  }

  /** Reabre una captura cerrada. CNF: corregir antes de que se sincronice tiene que ser posible. */
  async reabrirCaptura(capturaId: string): Promise<void> {
    const c = await this.almacen.obtener<Captura>('capturas', capturaId)
    if (!c) return
    await this.almacen.poner('capturas', { ...c, estado: 'borrador', cerradaEn: null, actualizadaEn: ahora() })
    const item = await this.almacen.obtener<ItemOutbox>('outbox', capturaId)
    if (item && item.estado === 'pendiente') await this.almacen.borrar('outbox', capturaId)
  }

  async lineasDe(capturaId: string): Promise<LineaCaptura[]> {
    const lineas = await this.almacen.porIndice<LineaCaptura>('lineas', 'porCaptura', capturaId)
    return lineas.sort((a, b) => a.id.localeCompare(b.id)) // uuid v7: orden de captura
  }

  async capturasDeCama(camaId: string): Promise<CapturaCompleta[]> {
    const capturas = await this.almacen.porIndice<Captura>('capturas', 'porCama', camaId)
    capturas.sort((a, b) => b.id.localeCompare(a.id)) // la más reciente primero
    return Promise.all(capturas.map(async (captura) => ({ captura, lineas: await this.lineasDe(captura.id) })))
  }

  async resumen(): Promise<{ capturas: number; borradores: number; lineas: number; pendientes: number }> {
    const [capturas, lineas, borradores, pendientes] = await Promise.all([
      this.almacen.contar('capturas'),
      this.almacen.contar('lineas'),
      this.almacen.porIndice<Captura>('capturas', 'porEstado', 'borrador'),
      this.almacen.porIndice<ItemOutbox>('outbox', 'porEstado', 'pendiente'),
    ])
    return { capturas, lineas, borradores: borradores.length, pendientes: pendientes.length }
  }

  // -- instrumentación (D8) ------------------------------------------------------

  /**
   * Anota un evento. Nunca falla hacia afuera: si la instrumentación se rompe, la captura sigue.
   * Es una medición, no una funcionalidad.
   */
  async anotar(tipo: TipoEvento, datos: Partial<Evento> = {}): Promise<void> {
    try {
      const evento: Evento = {
        id: uuidv7(),
        tipo,
        ts: ahora(),
        capturaId: datos.capturaId ?? null,
        camaId: datos.camaId ?? null,
        variante: datos.variante ?? null,
        detalle: datos.detalle ?? '',
      }
      await this.almacen.poner('eventos', evento)
    } catch {
      /* medir nunca puede tumbar la captura */
    }
  }

  eventos = () => this.almacen.todos<Evento>('eventos')

  // -- servidor simulado y conflictos (D6) ---------------------------------------

  registrosServidor = () => this.almacen.todos<RegistroServidor>('servidor')
  conflictos = () => this.almacen.todos<Conflicto>('conflictos')
  outbox = () => this.almacen.todos<ItemOutbox>('outbox')

  async capturaPorId(id: string): Promise<Captura | undefined> {
    return this.almacen.obtener<Captura>('capturas', id)
  }

  async guardarItemOutbox(item: ItemOutbox): Promise<void> {
    await this.almacen.poner('outbox', item)
  }

  async guardarEnServidor(registro: RegistroServidor): Promise<void> {
    await this.almacen.poner('servidor', registro)
  }

  async guardarConflicto(conflicto: Conflicto): Promise<void> {
    await this.almacen.poner('conflictos', conflicto)
  }

  async registroServidorDe(camaId: string, fecha: string): Promise<RegistroServidor | undefined> {
    const todos = await this.almacen.porIndice<RegistroServidor>('servidor', 'porCamaFecha', [camaId, fecha])
    return todos[0]
  }

  /** Cuántas capturas cerradas hay ya de esa cama ese día, sin contar la que se está mirando. */
  async otrasCapturasDelDia(camaId: string, fecha: string, exceptoId: string): Promise<number> {
    const capturas = await this.almacen.porIndice<Captura>('capturas', 'porCama', camaId)
    return capturas.filter((c) => c.fecha === fecha && c.id !== exceptoId).length
  }

  /** Solo para la demo: deja el dispositivo como recién instalado, sin borrar el catálogo. */
  async borrarCapturas(): Promise<void> {
    await this.almacen.vaciar(['capturas', 'lineas', 'outbox', 'eventos', 'servidor', 'conflictos'])
  }
}
