/**
 * Lectura de la marca física de la cama.
 *
 * UXP: «identificar la cama escaneando una marca física en lugar de escribir el código» = SÍ.
 *
 * Usa `BarcodeDetector`, que hoy existe en Chrome sobre Android y no en Safari sobre iOS. No se
 * añade ninguna biblioteca para tapar ese hueco: **dónde funciona el escaneo y dónde no es
 * justamente uno de los datos que la sesión con el cliente tiene que producir**, porque decide si
 * el producto puede seguir siendo una PWA (ver PLAN_DEMO_CAPTURA.md §4.4).
 */

interface DetectorCodigos {
  detect(fuente: CanvasImageSource): Promise<{ rawValue: string }[]>
}

interface VentanaConDetector {
  BarcodeDetector?: new (opciones?: { formats?: string[] }) => DetectorCodigos
}

export function hayEscaner(): boolean {
  return typeof window !== 'undefined'
    && 'BarcodeDetector' in window
    && Boolean(navigator.mediaDevices?.getUserMedia)
}

/**
 * Abre la cámara dentro del contenedor y devuelve el primer código que reconozca.
 * Devuelve null si el usuario cancela o si el dispositivo no puede.
 */
export async function escanearCodigo(contenedor: HTMLElement): Promise<string | null> {
  const Detector = (window as unknown as VentanaConDetector).BarcodeDetector
  if (!Detector || !navigator.mediaDevices?.getUserMedia) return null

  const video = document.createElement('video')
  video.setAttribute('playsinline', 'true')
  video.className = 'escaner__video'
  const caja = document.createElement('div')
  caja.className = 'escaner'
  const cancelar = document.createElement('button')
  cancelar.type = 'button'
  cancelar.className = 'boton boton--tenue'
  cancelar.textContent = 'Cancelar'
  caja.append(video, cancelar)
  contenedor.append(caja)

  let flujo: MediaStream | null = null
  const limpiar = () => {
    flujo?.getTracks().forEach((t) => t.stop())
    caja.remove()
  }

  try {
    flujo = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } })
    video.srcObject = flujo
    await video.play()
    const detector = new Detector({ formats: ['qr_code', 'code_128', 'ean_13'] })

    return await new Promise<string | null>((resolver) => {
      cancelar.addEventListener('click', () => { limpiar(); resolver(null) })
      const tic = window.setInterval(async () => {
        try {
          const codigos = await detector.detect(video)
          if (codigos.length > 0) {
            window.clearInterval(tic)
            limpiar()
            resolver(codigos[0].rawValue)
          }
        } catch {
          /* un cuadro ilegible no es un error: se intenta con el siguiente */
        }
      }, 300)
      // Sin lectura en 30 segundos se devuelve el control: nadie se queda atrapado en la cámara.
      window.setTimeout(() => { window.clearInterval(tic); limpiar(); resolver(null) }, 30_000)
    })
  } catch {
    limpiar()
    return null
  }
}
