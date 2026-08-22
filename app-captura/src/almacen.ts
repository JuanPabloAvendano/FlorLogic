/**
 * Envoltura mínima de IndexedDB.
 *
 * Por qué a mano y no Dexie, como decía el plan: el prototipo hace consultas por clave y por un
 * índice, nada más. Una dependencia menos es un `npm install` que no falla en el campo y un archivo
 * que se lee entero en cinco minutos. Todo el acceso a disco pasa por aquí, así que si el D6
 * (sincronización) pide más, cambiar a Dexie toca un solo archivo.
 *
 * IndexedDB es la única persistencia del navegador que sirve para esto: localStorage es síncrono,
 * pequeño y de solo texto. Sus límites en iOS están anotados en PLAN_DEMO_CAPTURA.md §4.3 y son la
 * razón por la que el producto podría no terminar siendo una PWA.
 */

export interface DefinicionIndice {
  nombre: string
  campo: string | string[]
  unico?: boolean
}

export interface DefinicionAlmacen {
  nombre: string
  clave: string
  indices?: DefinicionIndice[]
}

export interface Esquema {
  nombre: string
  version: number
  almacenes: DefinicionAlmacen[]
}

function esperar<T>(solicitud: IDBRequest<T>): Promise<T> {
  return new Promise((resolver, rechazar) => {
    solicitud.onsuccess = () => resolver(solicitud.result)
    solicitud.onerror = () => rechazar(solicitud.error ?? new Error('Error de IndexedDB'))
  })
}

export class Almacen {
  private constructor(private readonly bd: IDBDatabase) {}

  static abrir(esquema: Esquema): Promise<Almacen> {
    return new Promise((resolver, rechazar) => {
      const sol = indexedDB.open(esquema.nombre, esquema.version)

      sol.onupgradeneeded = () => {
        const bd = sol.result
        for (const def of esquema.almacenes) {
          const almacen = bd.objectStoreNames.contains(def.nombre)
            ? sol.transaction!.objectStore(def.nombre)
            : bd.createObjectStore(def.nombre, { keyPath: def.clave })
          for (const idx of def.indices ?? []) {
            if (!almacen.indexNames.contains(idx.nombre)) {
              almacen.createIndex(idx.nombre, idx.campo, { unique: idx.unico ?? false })
            }
          }
        }
      }

      sol.onsuccess = () => resolver(new Almacen(sol.result))
      sol.onerror = () => rechazar(sol.error ?? new Error('No se pudo abrir la base local'))
      sol.onblocked = () => rechazar(new Error('Hay otra pestaña abierta con una versión anterior'))
    })
  }

  private tx(almacenes: string[], modo: IDBTransactionMode): IDBTransaction {
    return this.bd.transaction(almacenes, modo)
  }

  /** Una transacción de escritura por operación: CNF pide que cada dato quede guardado de inmediato. */
  async poner<T>(almacen: string, valor: T): Promise<void> {
    const tx = this.tx([almacen], 'readwrite')
    await esperar(tx.objectStore(almacen).put(valor as unknown as IDBValidKey & T))
    await this.fin(tx)
  }

  async ponerVarios<T>(almacen: string, valores: T[]): Promise<void> {
    if (valores.length === 0) return
    const tx = this.tx([almacen], 'readwrite')
    const objeto = tx.objectStore(almacen)
    for (const v of valores) objeto.put(v as unknown as IDBValidKey & T)
    await this.fin(tx)
  }

  async obtener<T>(almacen: string, clave: IDBValidKey): Promise<T | undefined> {
    const tx = this.tx([almacen], 'readonly')
    const r = await esperar<T | undefined>(tx.objectStore(almacen).get(clave))
    return r
  }

  async todos<T>(almacen: string): Promise<T[]> {
    const tx = this.tx([almacen], 'readonly')
    return esperar<T[]>(tx.objectStore(almacen).getAll())
  }

  async porIndice<T>(almacen: string, indice: string, valor: IDBValidKey): Promise<T[]> {
    const tx = this.tx([almacen], 'readonly')
    return esperar<T[]>(tx.objectStore(almacen).index(indice).getAll(valor))
  }

  async contar(almacen: string): Promise<number> {
    const tx = this.tx([almacen], 'readonly')
    return esperar<number>(tx.objectStore(almacen).count())
  }

  async borrar(almacen: string, clave: IDBValidKey): Promise<void> {
    const tx = this.tx([almacen], 'readwrite')
    await esperar(tx.objectStore(almacen).delete(clave))
    await this.fin(tx)
  }

  async vaciar(almacenes: string[]): Promise<void> {
    if (almacenes.length === 0) return
    const tx = this.tx(almacenes, 'readwrite')
    for (const a of almacenes) tx.objectStore(a).clear()
    await this.fin(tx)
  }

  private fin(tx: IDBTransaction): Promise<void> {
    return new Promise((resolver, rechazar) => {
      tx.oncomplete = () => resolver()
      tx.onerror = () => rechazar(tx.error ?? new Error('Transacción fallida'))
      tx.onabort = () => rechazar(tx.error ?? new Error('Transacción abortada'))
    })
  }

  cerrar(): void {
    this.bd.close()
  }
}
