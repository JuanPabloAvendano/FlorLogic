import { useEffect, useState } from 'react'
import type { Catalogo } from './catalogo'
import { Boton, Etiqueta, Vacio } from './componentes'
import type { CapturaCompleta, LineaCaptura } from './modelo'
import type { Repositorio } from './repositorio'

/**
 * Captura de una cama.
 *
 * Es el D2 del plan: el mínimo que demuestra que un dato sobrevive a cerrar la aplicación.
 * La rejilla completa tipo papel es el D3 y el motor de reglas el D4 — aquí todavía no se valida
 * nada, solo se guarda.
 *
 * Dos cosas que ya se respetan y que no son negociables más adelante:
 *  - CNF: cada dato se guarda de inmediato. No hay botón de guardar en ninguna parte.
 *  - CN-31: la sugerencia de cantidad se muestra, nunca se escribe sola.
 */

const hoy = () => new Date().toISOString().slice(0, 10)

export function PantallaCaptura({ repo, catalogo, camaId, usuario, volver, alCambiar }: {
  repo: Repositorio
  catalogo: Catalogo
  camaId: string
  usuario: string
  volver: () => void
  alCambiar: () => void
}) {
  const [capturas, setCapturas] = useState<CapturaCompleta[] | null>(null)
  const cama = catalogo.cama(camaId)
  const bloque = cama ? catalogo.bloque(cama.bloqueId) : undefined

  async function recargar() {
    setCapturas(await repo.capturasDeCama(camaId))
    alCambiar()
  }

  useEffect(() => {
    void recargar()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [camaId])

  if (!cama || !bloque) return <Vacio titulo="Cama no encontrada" detalle="El catálogo local no la tiene." />
  if (!capturas) return <Vacio titulo="Abriendo la base local…" detalle="Un momento." />

  const abierta = capturas.find((c) => c.captura.estado === 'borrador')

  async function empezar() {
    await repo.crearCaptura({
      camaId,
      bloqueId: cama!.bloqueId,
      plantillaId: 'novedad_siembra',
      fecha: hoy(),
      usuario,
    })
    await recargar()
  }

  return (
    <>
      <Boton variante="tenue" onClick={volver}>‹ Cama {cama.codigo}</Boton>
      <h1 className="titulo">Capturar cama {cama.codigo}</h1>
      <p className="subtitulo">Bloque {bloque.codigo} · {hoy()}</p>

      {!abierta && (
        <div className="acciones">
          <Boton onClick={() => void empezar()}>Capturar esta cama</Boton>
        </div>
      )}

      {abierta && (
        <FormularioCaptura
          repo={repo}
          catalogo={catalogo}
          completa={abierta}
          recargar={recargar}
        />
      )}

      {capturas.filter((c) => c.captura.estado === 'cerrada').length > 0 && (
        <>
          <h2 className="seccion__titulo">Ya capturadas en esta cama</h2>
          {capturas
            .filter((c) => c.captura.estado === 'cerrada')
            .map((c) => (
              <CapturaCerrada key={c.captura.id} repo={repo} catalogo={catalogo} completa={c} recargar={recargar} />
            ))}
        </>
      )}
    </>
  )
}

function FormularioCaptura({ repo, catalogo, completa, recargar }: {
  repo: Repositorio
  catalogo: Catalogo
  completa: CapturaCompleta
  recargar: () => Promise<void>
}) {
  const { captura, lineas } = completa
  const bloque = catalogo.bloque(captura.bloqueId)
  const tiposFlor = bloque?.tiposFlor.filter(Boolean) ?? []
  const variedades = catalogo.semilla.variedades.filter(
    (v) => tiposFlor.length === 0 || tiposFlor.includes(v.tipoFlor),
  )

  const [variedadId, setVariedadId] = useState(variedades[0]?.id ?? '')
  const [texto, setTexto] = useState({ lineas: '', cantidad: '' })

  const variedad = catalogo.variedad(variedadId)
  const nLineas = texto.lineas === '' ? null : Number(texto.lineas)
  const sugerencia = nLineas && variedad?.plantasPorLinea ? nLineas * variedad.plantasPorLinea : null

  async function agregar() {
    if (!variedadId) return
    await repo.agregarLinea(captura.id, {
      variedadId,
      lineas: nLineas,
      cantidad: texto.cantidad === '' ? null : Number(texto.cantidad),
    })
    setTexto({ lineas: '', cantidad: '' })
    await recargar()
  }

  return (
    <section className="captura">
      <div className="captura__cabeza">
        <Etiqueta tono="aviso">borrador</Etiqueta>
        <span className="captura__guardado">Se guarda solo, dato por dato</span>
      </div>

      {lineas.map((l) => (
        <LineaGuardada key={l.id} repo={repo} catalogo={catalogo} linea={l} recargar={recargar} />
      ))}

      {lineas.length === 0 && (
        <p className="nota">
          Todavía no hay secciones. Una cama puede tener varias: el bloque 12 del formato real tiene
          tres camas divididas en dos variedades.
        </p>
      )}

      <div className="formulario">
        <label className="campo">
          <span className="campo__etiqueta">Variedad</span>
          <select
            className="campo__control"
            value={variedadId}
            onChange={(e) => setVariedadId(e.target.value)}
          >
            {variedades.map((v) => (
              <option key={v.id} value={v.id}>{v.nombre}</option>
            ))}
          </select>
        </label>

        <label className="campo">
          <span className="campo__etiqueta"># Líneas</span>
          <input
            className="campo__control"
            inputMode="numeric"
            value={texto.lineas}
            onChange={(e) => setTexto({ ...texto, lineas: e.target.value.replace(/\D/g, '') })}
          />
        </label>

        <label className="campo">
          <span className="campo__etiqueta">Cantidad</span>
          <input
            className="campo__control"
            inputMode="numeric"
            value={texto.cantidad}
            onChange={(e) => setTexto({ ...texto, cantidad: e.target.value.replace(/\D/g, '') })}
          />
        </label>
      </div>

      {/* CN-31: la sugerencia se ve, nunca se escribe sola. */}
      {sugerencia !== null && texto.cantidad === '' && (
        <p className="aviso aviso--neutro">
          Con {variedad?.plantasPorLinea} plantas por línea serían <strong>{sugerencia.toLocaleString('es-CO')}</strong>.
          {variedad?.confianza === 'en-disputa' && ' Ojo: esta variedad no tiene un valor confiable.'}
          <Boton variante="tenue" onClick={() => setTexto({ ...texto, cantidad: String(sugerencia) })}>
            Usar
          </Boton>
        </p>
      )}

      <div className="acciones">
        <Boton onClick={() => void agregar()}>Agregar sección</Boton>
        <Boton
          variante="tenue"
          onClick={async () => {
            await repo.cerrarCaptura(captura.id)
            await recargar()
          }}
        >
          Cerrar captura
        </Boton>
      </div>
    </section>
  )
}

function LineaGuardada({ repo, catalogo, linea, recargar }: {
  repo: Repositorio
  catalogo: Catalogo
  linea: LineaCaptura
  recargar: () => Promise<void>
}) {
  const v = catalogo.variedad(linea.variedadId)
  const razon = linea.lineas && linea.cantidad ? Math.round((linea.cantidad / linea.lineas) * 100) / 100 : null
  return (
    <div className="seccion">
      <div className="seccion__cabeza">
        <span className="seccion__variedad">{v?.nombre ?? '—'}</span>
        <Boton
          variante="tenue"
          onClick={async () => {
            await repo.borrarLinea(linea)
            await recargar()
          }}
        >
          Quitar
        </Boton>
      </div>
      <dl className="datos">
        <dt># líneas</dt><dd>{linea.lineas ?? '—'}</dd>
        <dt>Cantidad</dt><dd>{linea.cantidad?.toLocaleString('es-CO') ?? '—'}</dd>
        {razon !== null && <><dt>Plantas por línea</dt><dd>{razon}</dd></>}
      </dl>
    </div>
  )
}

function CapturaCerrada({ repo, catalogo, completa, recargar }: {
  repo: Repositorio
  catalogo: Catalogo
  completa: CapturaCompleta
  recargar: () => Promise<void>
}) {
  const { captura, lineas } = completa
  const total = lineas.reduce((t, l) => t + (l.cantidad ?? 0), 0)
  return (
    <div className="seccion">
      <div className="seccion__cabeza">
        <span className="seccion__variedad">{captura.fecha}</span>
        <Etiqueta tono="aviso">sin sincronizar</Etiqueta>
      </div>
      <dl className="datos">
        <dt>Secciones</dt><dd>{lineas.length}</dd>
        <dt>Plantas</dt><dd>{total.toLocaleString('es-CO')}</dd>
        <dt>Variedades</dt>
        <dd>{lineas.map((l) => catalogo.variedad(l.variedadId)?.nombre).join(' + ') || '—'}</dd>
      </dl>
      <div className="acciones">
        {/* CNF: corregir antes de sincronizar tiene que ser posible. */}
        <Boton
          variante="tenue"
          onClick={async () => {
            await repo.reabrirCaptura(captura.id)
            await recargar()
          }}
        >
          Corregir
        </Boton>
      </div>
    </div>
  )
}
