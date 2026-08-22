#!/usr/bin/env python3
"""
FlorLogic — construir la demo de captura.

    python construir.py                 compila a dist/
    python construir.py --semilla RUTA  regenera antes el catálogo desde la plantilla .xlsx
    python construir.py --servir        compila y levanta el servidor

Necesita: Python 3.9+ y `tsc` (TypeScript). Nada más — el proyecto no tiene dependencias de npm,
así que no hay `npm install` que pueda fallar en una finca con mala conexión.

Si no hay `tsc` a mano, `dist/` ya viene construido en el repositorio: `python servir.py` funciona
igual. Solo hace falta compilar cuando se toca algo dentro de `src/`.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import re
import shutil
import subprocess
import sys
from pathlib import Path

RAIZ = Path(__file__).resolve().parent
DIST = RAIZ / "dist"

PLANTILLA_POR_DEFECTO = (
    RAIZ.parent / "Documentacion" / "Levantamiento de requisitos" / "PLANTILLAS DOCUMENTOS DE EMPRESA"
    / "Plantilla digitalizada de excel de información capturada.xlsx"
)

INDEX = """<!doctype html>
<html lang="es-CO">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1, viewport-fit=cover">
<meta name="theme-color" content="#0f5132">
<meta name="description" content="Prototipo de captura en campo sin conexión — FlorLogic">
<title>FlorLogic — Captura</title>
<link rel="manifest" href="manifest.webmanifest">
<link rel="icon" href="icono-192.png">
<link rel="apple-touch-icon" href="icono-192.png">
<link rel="stylesheet" href="estilos.css">
</head>
<body>
<div id="raiz"></div>
<script type="module" src="js/main.js"></script>
<noscript>Esta demo necesita JavaScript.</noscript>
</body>
</html>
"""

MANIFIESTO = {
    "name": "FlorLogic — Captura",
    "short_name": "Captura",
    "description": "Prototipo de captura en campo sin conexión",
    "start_url": "./",
    "scope": "./",
    "display": "standalone",
    "orientation": "portrait",
    "lang": "es-CO",
    "theme_color": "#0f5132",
    "background_color": "#0b0f0d",
    "icons": [
        {"src": "icono-192.png", "sizes": "192x192", "type": "image/png"},
        {"src": "icono-512.png", "sizes": "512x512", "type": "image/png"},
        {"src": "icono-512.png", "sizes": "512x512", "type": "image/png", "purpose": "maskable"},
    ],
}

LIMPIAR = """<!doctype html>
<html lang="es-CO">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>FlorLogic — limpiar este origen</title>
<link rel="stylesheet" href="estilos.css">
</head>
<body>
<div class="app"><main class="contenido">
<h1 class="titulo">Limpiar este origen</h1>
<p class="subtitulo" id="origen"></p>

<p class="aviso aviso--neutro">Para el navegador, cada puerto es un sitio distinto. Si otro
proyecto dej&oacute; un <em>service worker</em> registrado en este mismo puerto, ese service worker
sigue respondiendo aunque su servidor ya no exista: por eso una p&aacute;gina puede &laquo;seguir
viva&raquo; despu&eacute;s de desinstalar el proyecto y reiniciar el computador. Esta p&aacute;gina
lo borra.</p>

<div id="estado"></div>

<div class="acciones">
  <button class="boton" id="limpiar">Borrar service workers y cach&eacute;s</button>
  <button class="boton boton--peligro" id="todo">Borrar tambi&eacute;n lo capturado en la demo</button>
</div>

<p class="nota">Lo primero no toca nada de lo capturado. Lo segundo s&iacute;: borra la base local
de la demo en este dispositivo.</p>
</main></div>

<script>
const estado = document.getElementById('estado');
document.getElementById('origen').textContent = location.origin;

function linea(tono, texto) {
  const p = document.createElement('p');
  p.className = tono === 'neutro' ? 'aviso aviso--neutro' : 'aviso';
  p.textContent = texto;
  estado.append(p);
}

async function inventario() {
  estado.innerHTML = '';
  const sws = navigator.serviceWorker ? await navigator.serviceWorker.getRegistrations() : [];
  const caches_ = window.caches ? await caches.keys() : [];
  linea('neutro', sws.length + ' service worker(s) registrados en ' + location.origin);
  for (const r of sws) linea('neutro', '   \u00b7 ' + (r.active && r.active.scriptURL || r.scope));
  linea('neutro', caches_.length + ' cach\u00e9(s): ' + (caches_.join(', ') || 'ninguno'));
}

async function limpiar(tambienDatos) {
  estado.innerHTML = '';
  let n = 0;
  if (navigator.serviceWorker) {
    for (const r of await navigator.serviceWorker.getRegistrations()) { await r.unregister(); n++; }
  }
  let c = 0;
  if (window.caches) {
    for (const k of await caches.keys()) { await caches.delete(k); c++; }
  }
  linea('ok', 'Listo: ' + n + ' service worker(s) y ' + c + ' cach\u00e9(s) borrados.');

  if (tambienDatos && window.indexedDB) {
    await new Promise((r) => {
      const s = indexedDB.deleteDatabase('florlogic-captura');
      s.onsuccess = s.onerror = s.onblocked = () => r();
    });
    try { localStorage.removeItem('florlogic.dispositivo'); } catch (e) { /* sin almacenamiento */ }
    linea('ok', 'Tambi\u00e9n se borr\u00f3 la base local de la demo.');
  }
  linea('neutro', 'Cerr\u00e1 TODAS las pesta\u00f1as de este sitio y volv\u00e9 a abrirlo. ' +
    'Si segu\u00eds viendo la p\u00e1gina de otro proyecto, us\u00e1 otro puerto: python servir.py --puerto 9500');
}

document.getElementById('limpiar').addEventListener('click', () => limpiar(false));
document.getElementById('todo').addEventListener('click', () => limpiar(true));
inventario();
</script>
</body>
</html>
"""

SW = """/* Generado por construir.py — no editar a mano. */
const CACHE = 'florlogic-%(version)s';
const ARCHIVOS = %(archivos)s;

// El catálogo y las reglas entran al precaché a propósito: sin ellos la aplicación no arranca
// en el campo, y CN-13 dice que tiene que arrancar sin conexión.
self.addEventListener('install', (e) => {
  e.waitUntil(caches.open(CACHE).then((c) => c.addAll(ARCHIVOS)).then(() => self.skipWaiting()));
});

self.addEventListener('activate', (e) => {
  e.waitUntil(
    caches.keys()
      .then((claves) => Promise.all(claves.filter((k) => k !== CACHE).map((k) => caches.delete(k))))
      .then(() => self.clients.claim()),
  );
});

self.addEventListener('fetch', (e) => {
  const url = new URL(e.request.url);
  if (e.request.method !== 'GET' || url.origin !== self.location.origin) return;

  // limpiar.html nunca se intercepta: es la herramienta para reparar justamente esto.
  if (url.pathname.endsWith('/limpiar.html')) return;

  // Navegaciones: primero la red, y solo si no hay se cae al arranque guardado. Al revés
  // —caché primero— el service worker le contesta a CUALQUIER direcci\u00f3n con la misma p\u00e1gina,
  // y entonces sigue sirviendo el sitio despu\u00e9s de que su servidor ya no existe.
  if (e.request.mode === 'navigate') {
    e.respondWith(fetch(e.request).catch(() => caches.match('./').then((r) => r || Response.error())));
    return;
  }
  e.respondWith(
    caches.match(e.request).then((r) => r || fetch(e.request).then((res) => {
      const copia = res.clone();
      caches.open(CACHE).then((c) => c.put(e.request, copia));
      return res;
    })),
  );
});
"""


def paso(texto: str) -> None:
    print(f"  · {texto}")


def generar_semilla(plantilla: Path) -> None:
    guion = RAIZ / "scripts" / "generar_seed.py"
    r = subprocess.run([sys.executable, str(guion), str(plantilla)], cwd=RAIZ, text=True)
    if r.returncode != 0:
        sys.exit("No se pudo regenerar el catálogo.")


def compilar_typescript() -> None:
    if shutil.which("tsc") is None:
        sys.exit(
            "No encuentro `tsc`.\n"
            "  · Instalalo una sola vez:  npm i -g typescript\n"
            "  · O usá el dist/ que ya viene construido:  python servir.py"
        )
    r = subprocess.run(["tsc", "--project", str(RAIZ / "tsconfig.json")], cwd=RAIZ, capture_output=True, text=True)
    if r.returncode != 0:
        sys.exit(r.stdout + r.stderr)

    # Los navegadores exigen la extensión en los import relativos; TypeScript no la escribe.
    for js in (DIST / "js").rglob("*.js"):
        texto = js.read_text(encoding="utf-8")
        nuevo = re.sub(r"(from\s+['\"]\.{1,2}/[^'\"]+?)(?<!\.js)(['\"])", r"\1.js\2", texto)
        if nuevo != texto:
            js.write_text(nuevo, encoding="utf-8")


def copiar_estaticos() -> list[str]:
    archivos: list[str] = []

    def copiar(origen: Path, destino_rel: str) -> None:
        destino = DIST / destino_rel
        destino.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy(origen, destino)
        archivos.append(destino_rel)

    copiar(RAIZ / "src" / "estilos.css", "estilos.css")
    copiar(RAIZ / "public" / "seed.json", "seed.json")
    copiar(RAIZ / "configuracion" / "reglas.v1.json", "reglas.v1.json")
    copiar(RAIZ / "public" / "icono-192.png", "icono-192.png")
    copiar(RAIZ / "public" / "icono-512.png", "icono-512.png")

    (DIST / "index.html").write_text(INDEX, encoding="utf-8")
    # Herramienta de reparación: a propósito NO entra al precaché del service worker — tiene que
    # poder cargar cuando el service worker es justamente el problema.
    (DIST / "limpiar.html").write_text(LIMPIAR, encoding="utf-8")
    (DIST / "manifest.webmanifest").write_text(json.dumps(MANIFIESTO, ensure_ascii=False, indent=2), encoding="utf-8")
    archivos += ["./", "index.html", "manifest.webmanifest"]
    archivos += sorted(
        str(p.relative_to(DIST)).replace("\\", "/") for p in (DIST / "js").rglob("*.js")
    )
    return archivos


def escribir_service_worker(archivos: list[str]) -> None:
    huella = hashlib.sha256()
    for rel in sorted(archivos):
        ruta = DIST / rel
        if ruta.is_file():
            huella.update(ruta.read_bytes())
    version = huella.hexdigest()[:12]
    (DIST / "sw.js").write_text(
        SW % {"version": version, "archivos": json.dumps(sorted(set(archivos)), ensure_ascii=False)},
        encoding="utf-8",
    )
    return None


def main() -> int:
    p = argparse.ArgumentParser(description="Construye la demo de captura en dist/")
    p.add_argument("--semilla", nargs="?", const=str(PLANTILLA_POR_DEFECTO),
                   help="Regenera public/seed.json desde la plantilla del cliente antes de construir")
    p.add_argument("--servir", action="store_true", help="Levanta el servidor al terminar")
    args = p.parse_args()

    print("FlorLogic — construyendo la demo de captura")

    if args.semilla:
        plantilla = Path(args.semilla)
        if not plantilla.exists():
            sys.exit(f"No existe la plantilla: {plantilla}")
        paso(f"catálogo desde {plantilla.name}")
        generar_semilla(plantilla)

    if DIST.exists():
        shutil.rmtree(DIST)
    DIST.mkdir(parents=True)

    paso("compilando TypeScript")
    compilar_typescript()
    paso("copiando catálogo, reglas, estilos e iconos")
    archivos = copiar_estaticos()
    paso("página de limpieza (limpiar.html)")
    paso("service worker (arranque sin conexión)")
    escribir_service_worker(archivos)

    total = sum(f.stat().st_size for f in DIST.rglob("*") if f.is_file())
    print(f"\nListo: dist/ — {len(list(DIST.rglob('*')))} archivos, {total / 1024:.0f} KB")
    print("  python servir.py     abre la demo y da la dirección para el celular")

    if args.servir:
        sys.path.insert(0, str(RAIZ))
        import servir  # noqa: PLC0415
        servir.main()
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
