/**
 * Verificación del índice del catálogo, sin navegador y sin dependencias.
 *
 *     node --experimental-strip-types pruebas/catalogo.prueba.ts
 *
 * Comprueba que lo que la app muestra en pantalla coincide con lo que dice el formato
 * de papel del cliente. Los números esperados salen de PLAN_DEMO_CAPTURA.md §2.
 */
import { readFileSync } from 'node:fs'
import { Catalogo } from '../src/catalogo.ts'
import type { Semilla } from '../src/tipos.ts'

const semilla = JSON.parse(readFileSync(new URL('../public/seed.json', import.meta.url), 'utf8')) as Semilla
const cat = new Catalogo(semilla)

let fallos = 0
function comprobar(nombre: string, real: unknown, esperado: unknown) {
  const ok = JSON.stringify(real) === JSON.stringify(esperado)
  if (!ok) fallos++
  console.log(`${ok ? '  ok  ' : ' FALLA'} ${nombre}${ok ? '' : `\n         esperado ${JSON.stringify(esperado)}, obtuvo ${JSON.stringify(real)}`}`)
}

const r = cat.resumen()
console.log('\nResumen del catálogo')
comprobar('bloques leídos', r.bloques, 4)
comprobar('camas leídas', r.camas, 38)
comprobar('registros del formato', r.siembras, 41)

console.log('\nCamas divididas — DEC-14 sobre datos reales')
const bloque12 = cat.bloques().find((b) => b.codigo === '12')!
const divididas = cat.camas(bloque12.id).filter((c) => cat.estaDividida(c.id)).map((c) => c.codigo)
comprobar('camas divididas del bloque 12', divididas.sort(), ['37', '39', '40'])

console.log('\nSecciones de la cama 37 del bloque 12')
const cama37 = cat.camas(bloque12.id).find((c) => c.codigo === '37')!
const secciones = cat.siembras(cama37.id)
comprobar('número de secciones', secciones.length, 2)
comprobar('variedades', secciones.map((s) => cat.variedad(s.variedadId)!.nombre).sort(), ['Cooper', 'Lineth'])
comprobar('plantas = suma de las secciones', cat.plantasDeLaCama(cama37.id), 2812 + 399)
comprobar('líneas capturadas', secciones.map((s) => s.lineas).sort((a, b) => a! - b!), [21, 148])

console.log('\nRegla dura: cantidad ≈ líneas × plantas por línea')
const aRevisar = semilla.anomalias.filter((a) => a.severidad === 'revisar')
comprobar('filas marcadas', aRevisar.length, 9)
const conAnomalia = semilla.siembras.filter((s) => cat.anomalias(s.id).length > 0).length
comprobar('registros con aviso adjunto', conAnomalia, 8)

console.log('\nVariedades sin valor confiable de plantas por línea')
const enDisputa = semilla.variedades.filter((v) => v.confianza === 'en-disputa').map((v) => v.nombre)
comprobar('variedades en disputa', enDisputa.sort(), ['Astroi', 'Rose'])

console.log('\nColumnas cuyo significado no conocemos')
comprobar('valores distintos de OBSE',
  [...new Set(semilla.siembras.map((s) => s.obse).filter(Boolean))].sort(), ['325', '425'])

console.log(fallos === 0 ? '\nTodo en orden.\n' : `\n${fallos} comprobación(es) fallaron.\n`)
process.exit(fallos === 0 ? 0 : 1)
