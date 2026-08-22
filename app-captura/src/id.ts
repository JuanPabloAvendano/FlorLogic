/**
 * Identificadores del dispositivo.
 *
 * UUID v7: 48 bits de milisegundos + versión + azar. Dos propiedades que la demo necesita:
 *  - se generan **en el dispositivo**, sin pedirle nada al servidor (CN-13, captura sin conexión);
 *  - ordenan por tiempo, así que la sincronización cronológica de CN-24 no necesita otro campo.
 *
 * El contador monótono evita colisiones cuando se generan varios dentro del mismo milisegundo,
 * que es exactamente lo que pasa al guardar una cama completa de un tirón.
 */

let ultimoMs = 0
let contador = 0

function azar(bytes: number): number[] {
  const b = new Uint8Array(bytes)
  crypto.getRandomValues(b)
  return [...b]
}

export function uuidv7(ahora: number = Date.now()): string {
  if (ahora === ultimoMs) {
    contador += 1
  } else {
    ultimoMs = ahora
    contador = 0
  }
  // El contador entra en los 12 bits de rand_a: mantiene el orden dentro del mismo milisegundo.
  const secuencia = contador & 0x0fff
  const ms = BigInt(ahora)

  const b: number[] = []
  for (let i = 5; i >= 0; i--) b.push(Number((ms >> BigInt(i * 8)) & 0xffn))
  b.push(0x70 | (secuencia >> 8))          // versión 7 + 4 bits altos de la secuencia
  b.push(secuencia & 0xff)
  const resto = azar(8)
  resto[0] = 0x80 | (resto[0] & 0x3f)      // variante RFC 4122
  b.push(...resto)

  const hex = b.map((x) => x.toString(16).padStart(2, '0')).join('')
  return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20)}`
}

/** Marca de tiempo embebida en el identificador. Sirve para ordenar sin confiar en otro campo. */
export function msDelUuid(id: string): number {
  return Number(BigInt('0x' + id.replace(/-/g, '').slice(0, 12)))
}

const CLAVE_DISPOSITIVO = 'florlogic.dispositivo'

/**
 * Identidad del dispositivo. Vive en localStorage a propósito: si el navegador la borra,
 * queremos que se note — es justamente el riesgo que PLAN_DEMO_CAPTURA.md §4.3 pone sobre la mesa.
 */
export function idDispositivo(): string {
  try {
    const guardado = localStorage.getItem(CLAVE_DISPOSITIVO)
    if (guardado) return guardado
    const nuevo = uuidv7()
    localStorage.setItem(CLAVE_DISPOSITIVO, nuevo)
    return nuevo
  } catch {
    return uuidv7() // modo privado o almacenamiento bloqueado: la demo sigue, sin identidad estable
  }
}
