// Tipos de la semilla. Espejo exacto de lo que produce scripts/generar_seed.py.
// Ojo: esto NO es todavía el modelo de datos de captura (eso es el D2 del plan).

export type Confianza = 'alta' | 'media' | 'en-disputa' | 'heredada-del-tipo-de-flor' | 'sin-datos'
export type Severidad = 'revisar' | 'informativo'

export interface Finca { id: string; empresaId: string; nombre: string }
export interface Bloque { id: string; fincaId: string; codigo: string; tiposFlor: string[] }
export interface Cama { id: string; bloqueId: string; codigo: string }

export interface Variedad {
  id: string
  nombre: string
  tipoFlor: string
  plantasPorLinea: number | null
  confianza: Confianza
  razonesObservadas: number[]
}

export interface CampoPlantilla {
  id: string
  etiqueta: string
  tipo: 'fecha' | 'entero' | 'texto' | 'catalogo' | 'desconocido'
  obligatorio: boolean
}

export interface Plantilla { id: string; nombre: string; campos: CampoPlantilla[] }

export interface Siembra {
  id: string
  plantillaId: string
  hojaOrigen: string
  filaOrigen: number
  fecha: string
  fechaCruda: string
  bloqueId: string
  camaId: string
  variedadId: string
  lineas: number | null
  cantidad: number | null
  razonPlantasPorLinea: number | null
  obse?: string
  lote?: string
  calibre?: string
  proveedor?: string
  contenedor?: string
  observaciones?: string
}

export interface Anomalia {
  tipo: string
  severidad: Severidad
  detalle: string
  siembraId?: string
  hoja?: string
  fila?: number
}

export interface Semilla {
  version: string
  generado: string
  origen: string
  advertencia: string
  empresa: { id: string; nombre: string }
  fincas: Finca[]
  bloques: Bloque[]
  camas: Cama[]
  variedades: Variedad[]
  plantillas: Plantilla[]
  siembras: Siembra[]
  anomalias: Anomalia[]
  avisos: string[]
}

// --- catálogo de reglas (configuracion/reglas.v1.json) ---------------------------
// CNF: «los rangos y las reglas se pueden cambiar sin depender de una nueva versión de la
// aplicación». Por eso las reglas son datos que se interpretan, no código (CN-22, CN-26).

export type Severidad2 = 'dura' | 'blanda'
export type AmbitoRegla = 'linea' | 'captura'

export interface Regla {
  id: string
  tipo:
    | 'obligatorio'
    | 'rango'
    | 'razonPlantasPorLinea'
    | 'variedadRepetida'
    | 'sinSecciones'
    | 'camaRepetidaMismoDia'
  ambito: AmbitoRegla
  severidad: Severidad2
  mensaje: string
  /** UXP: «los mensajes explican qué hacer y no solo qué salió mal». */
  queHacer: string
  campo?: string
  min?: number
  max?: number
}

export interface CatalogoReglas {
  version: string
  actualizado: string
  nota: string
  toleranciaRazon: number
  reglas: Regla[]
}
