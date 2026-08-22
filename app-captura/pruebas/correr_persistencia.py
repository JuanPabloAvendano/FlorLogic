#!/usr/bin/env python3
"""
Corre pruebas/persistencia.ts en un navegador de verdad, con una recarga en la mitad.

    python pruebas/correr_persistencia.py

Compila los módulos con tsc, los sirve por http (IndexedDB no funciona desde file://)
y ejecuta las dos fases en Chromium. No necesita npm install.
"""
from __future__ import annotations

import http.server
import re
import shutil
import socketserver
import subprocess
import sys
import threading
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
SALIDA = RAIZ / ".prueba-navegador"
PUERTO = 8899

HARNESS = """<!doctype html>
<html lang="es"><head><meta charset="utf-8"><title>prueba</title></head>
<body><script type="module" src="./pruebas/persistencia.js"></script></body></html>
"""


def compilar() -> None:
    if SALIDA.exists():
        shutil.rmtree(SALIDA)
    orden = [
        "--ignoreConfig", "--strict", "--target", "ES2020",
        "--lib", "ES2020,DOM,DOM.Iterable", "--module", "esnext",
        "--moduleResolution", "bundler", "--outDir", str(SALIDA),
        str(RAIZ / "pruebas" / "persistencia.ts"),
    ]
    r = subprocess.run(["tsc", *orden], capture_output=True, text=True)
    if r.returncode != 0:
        sys.exit(r.stdout + r.stderr)

    # El navegador necesita la extensión en los import relativos; tsc no la pone.
    for js in SALIDA.rglob("*.js"):
        texto = js.read_text(encoding="utf-8")
        texto = re.sub(r"(from\s+['\"]\.{1,2}/[^'\"]+)(['\"])", r"\1.js\2", texto)
        js.write_text(texto, encoding="utf-8")

    shutil.copy(RAIZ / "public" / "seed.json", SALIDA / "seed.json")
    (SALIDA / "index.html").write_text(HARNESS, encoding="utf-8")


def servir() -> socketserver.TCPServer:
    class Silencioso(http.server.SimpleHTTPRequestHandler):
        def __init__(self, *a, **k):
            super().__init__(*a, directory=str(SALIDA), **k)

        def log_message(self, *_):
            pass

    socketserver.TCPServer.allow_reuse_address = True
    servidor = socketserver.TCPServer(("127.0.0.1", PUERTO), Silencioso)
    threading.Thread(target=servidor.serve_forever, daemon=True).start()
    return servidor


def main() -> int:
    try:
        from playwright.sync_api import sync_playwright
    except ImportError:
        sys.exit("Falta playwright.  pip install playwright && playwright install chromium")

    compilar()
    servidor = servir()
    fallos, errores = 0, []
    try:
        with sync_playwright() as p:
            nav = p.chromium.launch()
            pagina = nav.new_page()
            pagina.on("pageerror", lambda e: errores.append(str(e)))
            pagina.goto(f"http://127.0.0.1:{PUERTO}/")

            for fase, titulo in (("fase1", "Fase 1 — capturar y cerrar"),
                                 ("fase2", "Fase 2 — DESPUÉS DE RECARGAR LA PÁGINA")):
                if fase == "fase2":
                    pagina.reload()
                pagina.wait_for_function(f"typeof window.{fase} === 'function'")
                print(f"\n{titulo}")
                for r in pagina.evaluate(f"window.{fase}()"):
                    marca = "  ok  " if r["ok"] else " FALLA"
                    if not r["ok"]:
                        fallos += 1
                    print(f"{marca} {r['nombre']}" + (f"\n         {r['detalle']}" if r["detalle"] else ""))
            nav.close()
    finally:
        servidor.shutdown()

    if errores:
        print("\nErrores de la página:", *errores, sep="\n  ")
    print("\nTodo en orden.\n" if fallos == 0 and not errores else f"\n{fallos} comprobación(es) fallaron.\n")
    return 0 if fallos == 0 and not errores else 1


if __name__ == "__main__":
    raise SystemExit(main())
