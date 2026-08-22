/**
 * Verificación del motor de reglas, sin navegador.
 *
 *     node --experimental-strip-types pruebas/reglas.prueba.ts
 *
 * Comprueba que las reglas del archivo se cumplen y —lo que más importa— que la separación entre
 * dura y blanda es la que dice el catálogo: una regla dura impide cerrar la cama, una blanda no.
 */
import { readFileSync } from 'node:fs'
import { MotorReglas } from '../src/reglas.ts'
import type { LineaCaptura } from '../src/modelo.ts'
import type { CatalogoReglas, Variedad } from '../src/tipos.ts'

const catalogo = JSON.parse(
  readFileSync(new URL('../configuracion/reglas.v1.json', import.meta.url), 'utf8'),
) as CatalogoReglas
const motor = new MotorReglas(catalogo)

const LINETH: Variedad = {
  id: 'var-lineth', nombre: 'Lineth', tipoFlor: 'Cremón',
  plantasPorLinea: 19, confianza: 'alta', razonesObservadas: [19],
}
const ASTROI: Variedad = {
  id: 'var-astroi', nombre: 'Astroi', tipoFlor: 'Cremón',
  plantasPorLinea: 11, confianza: 'en-disputa', razonesObservadas: [11.19, 12.46, 19],
}

const linea = (p: Partial<LineaCaptura> = {}): LineaCaptura => ({
  id: 'l1', capturaId: 'c1', variedadId: 'var-lineth',
  lineas: 148, cantidad: 2812, obse: '',
  creadaEn: '', actualizadaEn: '', ...p,
})

let fallos = 0
function comprobar(nombre: string, real: unknown, esperado: unknown) {
  const ok = JSON.stringify(real) === JSON.stringify(esperado)
  if (!ok) fallos++
  console.log(`${ok ? '  ok  ' : ' FALLA'} ${nombre}` +
    (ok ? '' : `\n         esperado ${JSON.stringify(esperado)}, obtuvo ${JSON.stringify(real)}`))
}

console.log('\nLa fila buena del formato real pasa limpia')
comprobar('Lineth 148 × 19 = 2812', motor.revisarLinea(linea(), LINETH).length, 0)

console.log('\nReglas duras: impiden cerrar')
comprobar('sin líneas', motor.revisarLinea(linea({ lineas: null }), LINETH).map((h) => h.reglaId), ['RG-02'])
comprobar('sin cantidad', motor.revisarLinea(linea({ cantidad: null }), LINETH).map((h) => h.reglaId), ['RG-03'])
comprobar('líneas imposibles',
  motor.revisarLinea(linea({ lineas: 900, cantidad: 17100 }), LINETH).map((h) => h.reglaId), ['RG-04'])
comprobar('todas las duras bloquean',
  MotorReglas.bloquean(motor.revisarLinea(linea({ lineas: null }), LINETH)).length, 1)

console.log('\nRegla blanda: la razón del histórico')
const desviada = motor.revisarLinea(linea({ cantidad: 2000 }), LINETH)
comprobar('marca la fila', desviada.map((h) => h.reglaId), ['RG-06'])
comprobar('pero no bloquea', MotorReglas.bloquean(desviada).length, 0)
comprobar('y dice cuánto se desvió', desviada[0].mensaje.includes('13.51'), true)

console.log('\nVariedad sin valor confiable: lo dice en vez de callarlo')
const enDisputa = motor.revisarLinea(linea({ variedadId: 'var-astroi', lineas: 153, cantidad: 1907 }), ASTROI)
comprobar('avisa', enDisputa.length, 1)
comprobar('y advierte que el valor no es confiable',
  enDisputa[0].queHacer.includes('no tiene un valor confiable'), true)

console.log('\nÁmbito de la cama entera')
const ctx = (lineas: LineaCaptura[], otras = 0) => ({
  lineas, otrasCapturasHoy: otras,
  variedadPorId: (id: string) => (id === 'var-astroi' ? ASTROI : LINETH),
})
comprobar('una cama sin secciones no se puede cerrar',
  MotorReglas.bloquean(motor.revisarCaptura(ctx([]))).map((h) => h.reglaId), ['RG-08'])
comprobar('la misma variedad dos veces avisa, no bloquea',
  motor.revisarCaptura(ctx([linea(), linea({ id: 'l2' })])).map((h) => h.reglaId), ['RG-07'])
comprobar('capturar la cama dos veces el mismo día avisa',
  motor.revisarCaptura(ctx([linea()], 1)).map((h) => h.reglaId), ['RG-09'])

console.log('\nEl catálogo es datos, no código')
comprobar('la versión sale del archivo', motor.version(), 'reglas.v1')
comprobar('la tolerancia también', motor.tolerancia(), 0.02)
comprobar('nueve reglas cargadas', motor.reglas().length, 9)

console.log(fallos === 0 ? '\nTodo en orden.\n' : `\n${fallos} comprobación(es) fallaron.\n`)
process.exit(fallos === 0 ? 0 : 1)
