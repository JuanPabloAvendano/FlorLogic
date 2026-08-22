import { useCallback, useEffect, useMemo, useState } from 'react'
import { Catalogo, cargarSemilla } from './catalogo'
import { Boton, Etiqueta, Tarjeta, Vacio } from './componentes'
import { PantallaCaptura } from './PantallaCaptura'
import { Repositorio } from './repositorio'
import type { Semilla, Siembra } from './tipos'

type Vista =
  | { nombre: 'bloques' }
  | { nombre: 'camas'; bloqueId: string }
  | { nombre: 'cama'; camaId: string }
  | { nombre: 'captura'; camaId: string }
  | { nombre: 'datos' }

interface ResumenLocal {
  capturas: number
  borradores: number
  lineas: number
  pendientes: number
}

/** Sin autenticación todavía: el login sin conexión (SEG, CN-23) no es parte del D2. */
const USUARIO = 'demo'

export default function App() {
  const [repo, setRepo] = useState<Repositorio | null>(null)
  const [semilla, setSemilla] = useState<Semilla | null>(null)
  const [resumen, setResumen] = useState<ResumenLocal | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [vista, setVista] = useState<Vista>({ nombre: 'bloques' })
  const [enLinea, setEnLinea] = useState(navigator.onLine)

  useEffect(() => {
    void (async () => {
      try {
        // CN-26: primero se instala y versiona el catálogo; después se captura.
        const r = await Repositorio.abrir()
        await r.sembrarCatalogo(await cargarSemilla())
        setSemilla(await r.semillaLocal())
        setResumen(await r.resumen())
        setRepo(r)
      } catch (e) {
        setError(e instanceof Error ? e.message : String(e))
      }
    })()
  }, [])

  useEffect(() => {
    const cambio = () => setEnLinea(navigator.onLine)
    window.addEventListener('online', cambio)
    window.addEventListener('offline', cambio)
    return () => {
      window.removeEventListener('online', cambio)
      window.removeEventListener('offline', cambio)
    }
  }, [])

  const refrescarResumen = useCallback(() => {
    if (repo) void repo.resumen().then(setResumen)
  }, [repo])

  const catalogo = useMemo(() => (semilla ? new Catalogo(semilla) : null), [semilla])

  if (error) {
    return (
      <Vacio
        titulo="No se pudo abrir la base local"
        detalle={`${error}. Si es la primera vez, corré "npm run seed" para regenerar public/seed.json.`}
      />
    )
  }
  if (!catalogo || !repo) return <Vacio titulo="Abriendo…" detalle="Instalando el catálogo en el dispositivo." />

  return (
    <div className="app">
      <Cabecera
        enLinea={enLinea}
        pendientes={resumen?.pendientes ?? 0}
        finca={catalogo.semilla.fincas[0]?.nombre ?? '—'}
        onDatos={() => setVista({ nombre: 'datos' })}
        enDatos={vista.nombre === 'datos'}
        onInicio={() => setVista({ nombre: 'bloques' })}
      />

      <main className="contenido">
        {vista.nombre === 'bloques' && (
          <VistaBloques catalogo={catalogo} ir={(bloqueId) => setVista({ nombre: 'camas', bloqueId })} />
        )}
        {vista.nombre === 'camas' && (
          <VistaCamas
            catalogo={catalogo}
            bloqueId={vista.bloqueId}
            volver={() => setVista({ nombre: 'bloques' })}
            ir={(camaId) => setVista({ nombre: 'cama', camaId })}
          />
        )}
        {vista.nombre === 'cama' && (
          <VistaCama
            catalogo={catalogo}
            camaId={vista.camaId}
            capturar={() => setVista({ nombre: 'captura', camaId: vista.camaId })}
            volver={() => {
              const cama = catalogo.cama(vista.camaId)
              setVista(cama ? { nombre: 'camas', bloqueId: cama.bloqueId } : { nombre: 'bloques' })
            }}
          />
        )}
        {vista.nombre === 'captura' && (
          <PantallaCaptura
            repo={repo}
            catalogo={catalogo}
            camaId={vista.camaId}
            usuario={USUARIO}
            volver={() => setVista({ nombre: 'cama', camaId: vista.camaId })}
            alCambiar={refrescarResumen}
          />
        )}
        {vista.nombre === 'datos' && (
          <VistaDatos
            catalogo={catalogo}
            resumen={resumen}
            onBorrar={async () => {
              await repo.borrarCapturas()
              refrescarResumen()
            }}
          />
        )}
      </main>

      <footer className="pie">
        <span>{catalogo.semilla.version}</span>
        <span>·</span>
        <span>{catalogo.semilla.origen}</span>
      </footer>
    </div>
  )
}

function Cabecera({ enLinea, pendientes, finca, onDatos, onInicio, enDatos }: {
  enLinea: boolean
  pendientes: number
  finca: string
  onDatos: () => void
  onInicio: () => void
  enDatos: boolean
}) {
  return (
    <header className="cabecera">
      <button type="button" className="cabecera__marca" onClick={onInicio}>
        <strong>FlorLogic</strong>
        <span className="cabecera__sub">Captura · {finca}</span>
      </button>
      <div className="cabecera__estado">
        {/* DSP: la app tiene que decir siempre si está trabajando sin conexión. */}
        <Etiqueta tono={enLinea ? 'ok' : 'aviso'}>{enLinea ? 'En línea' : 'Sin conexión'}</Etiqueta>
        {/* UXP: qué se sincronizó y qué no, siempre a la vista. */}
        {pendientes > 0 && <Etiqueta tono="aviso">{pendientes} sin enviar</Etiqueta>}
        <Boton variante="tenue" onClick={onDatos}>{enDatos ? 'Cerrar' : 'Datos'}</Boton>
      </div>
    </header>
  )
}

function VistaBloques({ catalogo, ir }: { catalogo: Catalogo; ir: (id: string) => void }) {
  const r = catalogo.resumen()
  return (
    <>
      <h1 className="titulo">Bloques</h1>
      <p className="subtitulo">
        {r.bloques} bloques · {r.camas} camas · {r.siembras} registros del formato real
      </p>
      <div className="lista">
        {catalogo.bloques().map((b) => {
          const camas = catalogo.camas(b.id)
          const divididas = camas.filter((c) => catalogo.estaDividida(c.id)).length
          return (
            <Tarjeta key={b.id} onClick={() => ir(b.id)}>
              <div className="tarjeta__fila">
                <span className="tarjeta__codigo">Bloque {b.codigo}</span>
                <span className="tarjeta__flecha" aria-hidden="true">›</span>
              </div>
              <div className="tarjeta__meta">
                <span>{camas.length} camas</span>
                {b.tiposFlor.filter(Boolean).map((t) => <span key={t}>{t}</span>)}
                {divididas > 0 && <Etiqueta tono="neutro">{divididas} divididas</Etiqueta>}
              </div>
            </Tarjeta>
          )
        })}
      </div>
    </>
  )
}

function VistaCamas({ catalogo, bloqueId, volver, ir }: {
  catalogo: Catalogo
  bloqueId: string
  volver: () => void
  ir: (id: string) => void
}) {
  const bloque = catalogo.bloque(bloqueId)
  const camas = catalogo.camas(bloqueId)
  return (
    <>
      <Boton variante="tenue" onClick={volver}>‹ Bloques</Boton>
      <h1 className="titulo">Bloque {bloque?.codigo}</h1>
      <p className="subtitulo">{camas.length} camas con registro en el formato</p>
      <div className="lista lista--densa">
        {camas.map((c) => {
          const siembras = catalogo.siembras(c.id)
          const dividida = catalogo.estaDividida(c.id)
          const plantas = catalogo.plantasDeLaCama(c.id)
          const conAviso = siembras.some((s) => catalogo.anomalias(s.id).length > 0)
          return (
            <Tarjeta key={c.id} onClick={() => ir(c.id)}>
              <div className="tarjeta__fila">
                <span className="tarjeta__codigo">Cama {c.codigo}</span>
                <span className="tarjeta__flecha" aria-hidden="true">›</span>
              </div>
              <div className="tarjeta__meta">
                <span>{siembras.map((s) => catalogo.variedad(s.variedadId)?.nombre).join(' + ') || 'sin siembra'}</span>
                {plantas > 0 && <span>{plantas.toLocaleString('es-CO')} plantas</span>}
                {dividida && <Etiqueta tono="neutro">dividida</Etiqueta>}
                {conAviso && <Etiqueta tono="aviso">revisar</Etiqueta>}
              </div>
            </Tarjeta>
          )
        })}
      </div>
    </>
  )
}

function VistaCama({ catalogo, camaId, volver, capturar }: {
  catalogo: Catalogo
  camaId: string
  volver: () => void
  capturar: () => void
}) {
  const cama = catalogo.cama(camaId)
  const bloque = cama ? catalogo.bloque(cama.bloqueId) : undefined
  const siembras = catalogo.siembras(camaId)
  const porFecha = new Map<string, Siembra[]>()
  for (const s of siembras) porFecha.set(s.fecha, [...(porFecha.get(s.fecha) ?? []), s])

  return (
    <>
      <Boton variante="tenue" onClick={volver}>‹ Bloque {bloque?.codigo}</Boton>
      <h1 className="titulo">Cama {cama?.codigo}</h1>
      <p className="subtitulo">Bloque {bloque?.codigo} · lo que dice el formato en papel</p>

      <div className="acciones">
        <Boton onClick={capturar}>Capturar esta cama</Boton>
      </div>

      {[...porFecha.entries()].map(([fecha, items]) => (
        <section key={fecha} className="jornada">
          <h2 className="jornada__fecha">
            {fecha}
            {items.length > 1 && <Etiqueta tono="neutro">{items.length} secciones</Etiqueta>}
          </h2>
          {items.map((s) => {
            const v = catalogo.variedad(s.variedadId)
            const avisos = catalogo.anomalias(s.id)
            return (
              <div key={s.id} className="seccion">
                <div className="seccion__cabeza">
                  <span className="seccion__variedad">{v?.nombre}</span>
                  <span className="seccion__hoja">{s.hojaOrigen} · fila {s.filaOrigen}</span>
                </div>
                <dl className="datos">
                  {s.lineas !== null && <><dt># líneas</dt><dd>{s.lineas}</dd></>}
                  {s.cantidad !== null && <><dt>Cantidad</dt><dd>{s.cantidad.toLocaleString('es-CO')}</dd></>}
                  {s.razonPlantasPorLinea !== null && (
                    <><dt>Plantas por línea</dt><dd>{s.razonPlantasPorLinea}</dd></>
                  )}
                  {s.lote && <><dt>Lote</dt><dd>{s.lote}</dd></>}
                  {s.calibre && <><dt>Calibre</dt><dd>{s.calibre}</dd></>}
                  {s.proveedor && <><dt>Proveedor</dt><dd>{s.proveedor}</dd></>}
                  {s.contenedor && <><dt>Contenedor</dt><dd>{s.contenedor}</dd></>}
                  {s.obse && <><dt>OBSE</dt><dd>{s.obse} <em>(significado desconocido)</em></dd></>}
                  {s.observaciones && <><dt>Observaciones</dt><dd>{s.observaciones}</dd></>}
                </dl>
                {avisos.map((a, i) => (
                  <p key={i} className="aviso">
                    <Etiqueta tono="aviso">revisar</Etiqueta> {a.detalle}
                  </p>
                ))}
              </div>
            )
          })}
        </section>
      ))}
    </>
  )
}

function VistaDatos({ catalogo, resumen, onBorrar }: {
  catalogo: Catalogo
  resumen: ResumenLocal | null
  onBorrar: () => Promise<void>
}) {
  const s = catalogo.semilla
  const r = catalogo.resumen()
  const revisar = s.anomalias.filter((a) => a.severidad === 'revisar')
  const informativas = s.anomalias.filter((a) => a.severidad === 'informativo')

  return (
    <>
      <h1 className="titulo">Datos de la semilla</h1>
      <p className="subtitulo">{s.advertencia}</p>

      <div className="cifras">
        <Cifra n={r.bloques} etiqueta="bloques" />
        <Cifra n={r.camas} etiqueta="camas" />
        <Cifra n={r.variedades} etiqueta="variedades" />
        <Cifra n={r.siembras} etiqueta="registros" />
        <Cifra n={r.divididas} etiqueta={`camas divididas (${r.porcentajeDivididas}%)`} />
        <Cifra n={r.revisar} etiqueta="filas a revisar" />
      </div>

      <h2 className="seccion__titulo">Capturado en este dispositivo</h2>
      <div className="cifras">
        <Cifra n={resumen?.capturas ?? 0} etiqueta="capturas" />
        <Cifra n={resumen?.borradores ?? 0} etiqueta="borradores" />
        <Cifra n={resumen?.lineas ?? 0} etiqueta="secciones" />
        <Cifra n={resumen?.pendientes ?? 0} etiqueta="sin enviar" />
      </div>
      <p className="nota">
        Todo esto vive en la base local del navegador y sobrevive a cerrar la aplicación. Todavía no
        sale del dispositivo: la bandeja de salida se vacía en el D6.
      </p>
      <div className="acciones">
        <Boton variante="tenue" onClick={() => void onBorrar()}>Borrar lo capturado en la demo</Boton>
      </div>

      <h2 className="seccion__titulo">Lo que la regla ya encuentra en el histórico real</h2>
      {revisar.length === 0 && <Vacio titulo="Nada marcado" detalle="La regla no encontró filas fuera de rango." />}
      {revisar.map((a, i) => (
        <p key={i} className="aviso">
          <Etiqueta tono="aviso">{a.tipo}</Etiqueta> {a.detalle}
          {a.hoja && <span className="aviso__origen"> — {a.hoja}, fila {a.fila}</span>}
        </p>
      ))}

      <h2 className="seccion__titulo">Camas divididas (confirma DEC-14)</h2>
      {informativas.map((a, i) => (
        <p key={i} className="aviso aviso--neutro">
          <Etiqueta tono="neutro">{a.tipo}</Etiqueta> {a.detalle}
        </p>
      ))}

      <h2 className="seccion__titulo">Preguntas abiertas que dejó el archivo</h2>
      {s.avisos.map((a, i) => <p key={i} className="aviso aviso--neutro">{a}</p>)}

      <h2 className="seccion__titulo">Plantas por línea derivadas</h2>
      <table className="tabla">
        <thead>
          <tr><th>Variedad</th><th>Tipo</th><th>P/línea</th><th>Confianza</th></tr>
        </thead>
        <tbody>
          {s.variedades.map((v) => (
            <tr key={v.id}>
              <td>{v.nombre}</td>
              <td>{v.tipoFlor || '—'}</td>
              <td>{v.plantasPorLinea ?? '—'}</td>
              <td>
                <Etiqueta tono={v.confianza === 'alta' ? 'ok' : v.confianza === 'en-disputa' ? 'alerta' : 'aviso'}>
                  {v.confianza}
                </Etiqueta>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <p className="nota">Semilla generada el {new Date(s.generado).toLocaleString('es-CO')} desde {s.origen}.</p>
    </>
  )
}

function Cifra({ n, etiqueta }: { n: number; etiqueta: string }) {
  return (
    <div className="cifra">
      <span className="cifra__n">{n}</span>
      <span className="cifra__etiqueta">{etiqueta}</span>
    </div>
  )
}
