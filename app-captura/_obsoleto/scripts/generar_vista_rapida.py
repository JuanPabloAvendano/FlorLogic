#!/usr/bin/env python3
"""
FlorLogic — vista rápida.

Empaqueta la semilla y los estilos en UN solo archivo HTML que se abre con doble clic:
sin npm, sin servidor, sin conexión. Sirve para enseñar el D1 hoy mismo y para mandarlo
por WhatsApp.

Es desechable a propósito: cuando el proyecto de Vite esté instalado (npm run dev),
esta vista sobra y se borra. No agregarle funcionalidad.

Uso:
    python scripts/generar_vista_rapida.py
"""
from __future__ import annotations

import json
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent

CUERPO = """
<div class="app">
  <header class="cabecera">
    <button type="button" class="cabecera__marca" onclick="ir({v:'bloques'})">
      <strong>FlorLogic</strong><span class="cabecera__sub" id="sub"></span>
    </button>
    <div class="cabecera__estado">
      <span class="etiqueta etiqueta--neutro">vista rápida</span>
      <button type="button" class="boton boton--tenue" onclick="ir({v:'datos'})">Datos</button>
    </div>
  </header>
  <main class="contenido" id="contenido"></main>
  <footer class="pie"><span id="pie"></span></footer>
</div>
<script>
const S = window.__SEMILLA__;
const camasDe = b => S.camas.filter(c => c.bloqueId === b);
const siembrasDe = c => S.siembras.filter(s => s.camaId === c);
const variedad = id => S.variedades.find(v => v.id === id) || {nombre: '—'};
const bloque = id => S.bloques.find(b => b.id === id);
const cama = id => S.camas.find(c => c.id === id);
const avisosDe = id => S.anomalias.filter(a => a.siembraId === id);
const dividida = id => {
  const porFecha = {};
  siembrasDe(id).forEach(s => porFecha[s.fecha] = (porFecha[s.fecha] || 0) + 1);
  return Object.values(porFecha).some(n => n > 1);
};
const plantas = id => siembrasDe(id).reduce((t, s) => t + (s.cantidad || 0), 0);
const n = x => (x == null ? '—' : x.toLocaleString('es-CO'));
const esc = t => String(t == null ? '' : t).replace(/[&<>"]/g, m => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[m]));
const et = (tono, txt) => `<span class="etiqueta etiqueta--${tono}"><span aria-hidden="true">${{ok:'✓',aviso:'!',alerta:'✕',neutro:'·'}[tono]}</span> ${esc(txt)}</span>`;

function vistaBloques() {
  return `<h1 class="titulo">Bloques</h1>
    <p class="subtitulo">${S.bloques.length} bloques · ${S.camas.length} camas · ${S.siembras.length} registros del formato real</p>
    <div class="lista">` + S.bloques.map(b => {
      const cs = camasDe(b.id), dv = cs.filter(c => dividida(c.id)).length;
      return `<button class="tarjeta tarjeta--pulsable" onclick="ir({v:'camas',id:'${b.id}'})">
        <div class="tarjeta__fila"><span class="tarjeta__codigo">Bloque ${esc(b.codigo)}</span><span class="tarjeta__flecha">›</span></div>
        <div class="tarjeta__meta"><span>${cs.length} camas</span>${b.tiposFlor.filter(Boolean).map(t => `<span>${esc(t)}</span>`).join('')}
        ${dv ? et('neutro', dv + ' divididas') : ''}</div></button>`;
    }).join('') + `</div>`;
}

function vistaCamas(id) {
  const b = bloque(id);
  return `<button class="boton boton--tenue" onclick="ir({v:'bloques'})">‹ Bloques</button>
    <h1 class="titulo">Bloque ${esc(b.codigo)}</h1>
    <p class="subtitulo">${camasDe(id).length} camas con registro en el formato</p>
    <div class="lista lista--densa">` + camasDe(id).map(c => {
      const ss = siembrasDe(c.id);
      const aviso = ss.some(s => avisosDe(s.id).length);
      return `<button class="tarjeta tarjeta--pulsable" onclick="ir({v:'cama',id:'${c.id}'})">
        <div class="tarjeta__fila"><span class="tarjeta__codigo">Cama ${esc(c.codigo)}</span><span class="tarjeta__flecha">›</span></div>
        <div class="tarjeta__meta"><span>${esc(ss.map(s => variedad(s.variedadId).nombre).join(' + ') || 'sin siembra')}</span>
        ${plantas(c.id) ? `<span>${n(plantas(c.id))} plantas</span>` : ''}
        ${dividida(c.id) ? et('neutro', 'dividida') : ''}${aviso ? et('aviso', 'revisar') : ''}</div></button>`;
    }).join('') + `</div>`;
}

function vistaCama(id) {
  const c = cama(id), b = bloque(c.bloqueId), ss = siembrasDe(id);
  const fechas = [...new Set(ss.map(s => s.fecha))];
  return `<button class="boton boton--tenue" onclick="ir({v:'camas',id:'${b.id}'})">‹ Bloque ${esc(b.codigo)}</button>
    <h1 class="titulo">Cama ${esc(c.codigo)}</h1>
    <p class="subtitulo">Bloque ${esc(b.codigo)} · lo que dice el formato en papel</p>` +
    fechas.map(f => {
      const items = ss.filter(s => s.fecha === f);
      return `<section class="jornada"><h2 class="jornada__fecha">${esc(f)}
        ${items.length > 1 ? et('neutro', items.length + ' secciones') : ''}</h2>` +
        items.map(s => {
          const campos = [
            ['# líneas', s.lineas], ['Cantidad', s.cantidad === null ? null : n(s.cantidad)],
            ['Plantas por línea', s.razonPlantasPorLinea], ['Lote', s.lote], ['Calibre', s.calibre],
            ['Proveedor', s.proveedor], ['Contenedor', s.contenedor],
            ['OBSE', s.obse ? s.obse + ' (significado desconocido)' : null],
            ['Observaciones', s.observaciones],
          ].filter(([, v]) => v !== null && v !== undefined && v !== '');
          return `<div class="seccion">
            <div class="seccion__cabeza"><span class="seccion__variedad">${esc(variedad(s.variedadId).nombre)}</span>
            <span class="seccion__hoja">${esc(s.hojaOrigen)} · fila ${s.filaOrigen}</span></div>
            <dl class="datos">${campos.map(([k, v]) => `<dt>${esc(k)}</dt><dd>${esc(v)}</dd>`).join('')}</dl>
            ${avisosDe(s.id).map(a => `<p class="aviso">${et('aviso', 'revisar')} ${esc(a.detalle)}</p>`).join('')}
          </div>`;
        }).join('') + `</section>`;
    }).join('') +
    `<p class="nota">La captura entra en el D2 del plan. Hoy esta pantalla solo muestra lo que el formato de papel ya dice, para poder contrastarlo con el cliente.</p>`;
}

function vistaDatos() {
  const rev = S.anomalias.filter(a => a.severidad === 'revisar');
  const inf = S.anomalias.filter(a => a.severidad === 'informativo');
  const dv = S.camas.filter(c => dividida(c.id)).length;
  const cifra = (x, t) => `<div class="cifra"><span class="cifra__n">${x}</span><span class="cifra__etiqueta">${esc(t)}</span></div>`;
  return `<h1 class="titulo">Datos de la semilla</h1><p class="subtitulo">${esc(S.advertencia)}</p>
    <div class="cifras">${cifra(S.bloques.length, 'bloques')}${cifra(S.camas.length, 'camas')}
    ${cifra(S.variedades.length, 'variedades')}${cifra(S.siembras.length, 'registros')}
    ${cifra(dv, `camas divididas (${Math.round(dv / S.camas.length * 100)}%)`)}${cifra(rev.length, 'filas a revisar')}</div>
    <h2 class="seccion__titulo">Lo que la regla ya encuentra en el histórico real</h2>
    ${rev.map(a => `<p class="aviso">${et('aviso', a.tipo)} ${esc(a.detalle)}${a.hoja ? `<span class="aviso__origen"> — ${esc(a.hoja)}, fila ${a.fila}</span>` : ''}</p>`).join('')}
    <h2 class="seccion__titulo">Camas divididas (confirma DEC-14)</h2>
    ${inf.map(a => `<p class="aviso aviso--neutro">${et('neutro', a.tipo)} ${esc(a.detalle)}</p>`).join('')}
    <h2 class="seccion__titulo">Preguntas abiertas que dejó el archivo</h2>
    ${S.avisos.map(a => `<p class="aviso aviso--neutro">${esc(a)}</p>`).join('')}
    <h2 class="seccion__titulo">Plantas por línea derivadas</h2>
    <table class="tabla"><thead><tr><th>Variedad</th><th>Tipo</th><th>P/línea</th><th>Confianza</th></tr></thead><tbody>
    ${S.variedades.map(v => `<tr><td>${esc(v.nombre)}</td><td>${esc(v.tipoFlor || '—')}</td><td>${v.plantasPorLinea ?? '—'}</td>
    <td>${et(v.confianza === 'alta' ? 'ok' : v.confianza === 'en-disputa' ? 'alerta' : 'aviso', v.confianza)}</td></tr>`).join('')}
    </tbody></table>
    <p class="nota">Semilla generada el ${esc(S.generado)} desde ${esc(S.origen)}.</p>`;
}

function ir(estado) {
  const c = document.getElementById('contenido');
  c.innerHTML = estado.v === 'camas' ? vistaCamas(estado.id)
    : estado.v === 'cama' ? vistaCama(estado.id)
    : estado.v === 'datos' ? vistaDatos()
    : vistaBloques();
  window.scrollTo(0, 0);
}

document.getElementById('sub').textContent = 'Captura · ' + (S.fincas[0] ? S.fincas[0].nombre : '—');
document.getElementById('pie').textContent = S.version + ' · ' + S.origen;
ir({v: 'bloques'});
</script>
"""


def main() -> int:
    semilla = (RAIZ / "public" / "seed.json").read_text(encoding="utf-8")
    estilos = (RAIZ / "src" / "estilos.css").read_text(encoding="utf-8")
    datos = json.dumps(json.loads(semilla), ensure_ascii=False).replace("</", "<\\/")

    html = (
        "<!doctype html>\n<html lang=\"es-CO\">\n<head>\n"
        "<meta charset=\"utf-8\">\n"
        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, viewport-fit=cover\">\n"
        "<title>FlorLogic — Captura (vista rápida)</title>\n"
        f"<style>\n{estilos}\n</style>\n"
        f"<script>window.__SEMILLA__ = {datos};<\/script>\n".replace("<\\/script>", "</script>")
        + "</head>\n<body>\n" + CUERPO + "\n</body>\n</html>\n"
    )
    salida = RAIZ / "vista-rapida.html"
    salida.write_text(html, encoding="utf-8")
    print(f"OK  {salida}  ({len(html) / 1024:.0f} KB)  — se abre con doble clic, sin servidor")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
