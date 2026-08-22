#!/usr/bin/env python3
"""
Pruebas de compartir.py — el túnel de Cloudflare.

    python pruebas/compartir.py

No sale a internet: usa un `cloudflared` de mentiras que imita la salida real. Lo que se comprueba
es lo nuestro —encontrar la dirección, poner la clave, servir detrás del túnel y cerrar limpio—,
no la red de Cloudflare.
"""
from __future__ import annotations

import base64
import os
import subprocess
import sys
import tempfile
import time
import urllib.error
import urllib.request
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
fallos: list[str] = []

FALSO = '''#!/usr/bin/env python3
import sys, time
print("INF Thank you for trying Cloudflare Tunnel.", file=sys.stderr)
print("INF Requesting new quick Tunnel on trycloudflare.com...", file=sys.stderr)
time.sleep(0.2)
print("INF |  https://cama-37-flor-demo.trycloudflare.com |", file=sys.stderr)
sys.stderr.flush()
time.sleep(30)
'''

MUDO = '''#!/usr/bin/env python3
import sys, time
print("INF arrancando y sin decir nada util", file=sys.stderr)
sys.stderr.flush()
time.sleep(30)
'''


def comprobar(nombre: str, ok: bool, detalle: str = "") -> None:
    print(f"{'  ok  ' if ok else ' FALLA'} {nombre}" + (f"\n         {detalle}" if not ok and detalle else ""))
    if not ok:
        fallos.append(nombre)


def carpeta_con(programa: str, contenido: str) -> str:
    carpeta = tempfile.mkdtemp()
    destino = Path(carpeta) / programa
    destino.write_text(contenido, encoding="utf-8")
    destino.chmod(0o755)
    return carpeta


def correr(entorno_extra: dict[str, str], argumentos: list[str], segundos: float = 6.0):
    entorno = {**os.environ, **entorno_extra}
    p = subprocess.Popen([sys.executable, "-u", "compartir.py", *argumentos],
                         cwd=RAIZ, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                         text=True, env=entorno)
    time.sleep(segundos)
    return p


def leer(p: subprocess.Popen) -> str:
    p.terminate()
    try:
        salida, _ = p.communicate(timeout=8)
    except subprocess.TimeoutExpired:
        p.kill()
        salida, _ = p.communicate()
    return salida or ""


def main() -> int:
    print("\nSin cloudflared instalado: explica cómo instalarlo en vez de reventar")
    vacia = tempfile.mkdtemp()
    p = correr({"PATH": vacia}, ["--puerto", "8901"], segundos=3)
    salida = leer(p)
    comprobar("dice que falta cloudflared", "No encuentro `cloudflared`" in salida, salida[-300:])
    comprobar("y ofrece la alternativa local", "python servir.py" in salida)

    print("\nCon cloudflared: encuentra la dirección y sirve detrás de ella")
    ruta = carpeta_con("cloudflared", FALSO)
    p = correr({"PATH": ruta + os.pathsep + os.environ["PATH"]}, ["--puerto", "8902"])
    try:
        clave = None
        # El servidor local tiene que estar arriba y pidiendo clave.
        try:
            urllib.request.urlopen("http://127.0.0.1:8902/", timeout=4)
            comprobar("pone clave por defecto", False, "entró sin clave")
        except urllib.error.HTTPError as e:
            comprobar("pone clave por defecto", e.code == 401, f"devolvió {e.code}")
        except Exception as e:  # noqa: BLE001
            comprobar("pone clave por defecto", False, str(e))
    finally:
        salida = leer(p)

    comprobar("muestra la dirección del túnel",
              "https://cama-37-flor-demo.trycloudflare.com" in salida, salida[-400:])
    comprobar("muestra usuario y clave", "Usuario   florlogic" in salida and "Clave" in salida)
    comprobar("avisa que con HTTPS funciona el escaneo", "escaneo de la cama" in salida)
    comprobar("cierra limpio", "La dirección ya no existe" in salida)

    # La clave que imprimió tiene que ser la que abre de verdad.
    for linea in salida.splitlines():
        if linea.strip().startswith("Clave"):
            clave = linea.split()[-1]
    comprobar("la clave impresa es de 8 caracteres", bool(clave) and len(clave) == 8, str(clave))

    print("\nCon clave elegida a mano: el servidor la respeta")
    p = correr({"PATH": ruta + os.pathsep + os.environ["PATH"]},
               ["--puerto", "8903", "--clave", "camadividida"])
    try:
        pedido = urllib.request.Request("http://127.0.0.1:8903/js/main.js")
        pedido.add_header("Authorization", "Basic " +
                          base64.b64encode(b"florlogic:camadividida").decode())
        comprobar("entra con la clave dada", urllib.request.urlopen(pedido, timeout=4).status == 200)
    except Exception as e:  # noqa: BLE001
        comprobar("entra con la clave dada", False, str(e))
    finally:
        leer(p)

    print("\nCon --sin-clave: abierto, pero lo dice")
    p = correr({"PATH": ruta + os.pathsep + os.environ["PATH"]}, ["--puerto", "8904", "--sin-clave"])
    try:
        comprobar("entra sin clave", urllib.request.urlopen("http://127.0.0.1:8904/", timeout=4).status == 200)
    except Exception as e:  # noqa: BLE001
        comprobar("entra sin clave", False, str(e))
    finally:
        salida = leer(p)
    comprobar("avisa que queda abierto", "SIN CLAVE" in salida)

    print("\nSi cloudflared no devuelve dirección, no se queda colgado")
    mudo = carpeta_con("cloudflared", MUDO)
    p = correr({"PATH": mudo + os.pathsep + os.environ["PATH"]},
               ["--puerto", "8905", "--espera", "2"], segundos=5)
    salida = leer(p)
    comprobar("lo dice en vez de esperar para siempre",
              "no devolvió ninguna dirección" in salida or "Cerrando" in salida, salida[-300:])

    print("\nLa demo completa detrás de la clave (es lo que va a ver el cliente por el túnel)")
    detras_de_la_clave()

    print()
    if fallos:
        print(f"{len(fallos)} comprobación(es) fallaron.\n")
        return 1
    print("Túnel en orden.\n")
    return 0


def detras_de_la_clave() -> None:
    """
    Con clave de por medio, lo que suele romperse es el service worker: precachea con
    `cache.addAll`, y si esas peticiones no llevan credenciales la instalación falla y la demo
    deja de arrancar sin conexión. Por eso se prueba con el navegador, no con urllib.
    """
    try:
        from playwright.sync_api import sync_playwright  # noqa: PLC0415
    except ImportError:
        print("  (saltada: falta playwright)")
        return

    p = subprocess.Popen([sys.executable, "-u", "servir.py", "--puerto", "8906", "--clave", "camadividida"],
                         cwd=RAIZ, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    time.sleep(1.5)
    try:
        with sync_playwright() as pw:
            nav = pw.chromium.launch()
            ctx = nav.new_context(http_credentials={"username": "florlogic", "password": "camadividida"})
            pag = ctx.new_page()
            errores: list[str] = []
            pag.on("pageerror", lambda e: errores.append(str(e)))
            pag.goto("http://127.0.0.1:8906/")
            pag.wait_for_selector("text=Bloques", timeout=15000)
            comprobar("la demo carga entera detrás de la clave", pag.locator(".tarjeta__codigo").count() == 4)
            pag.wait_for_function("navigator.serviceWorker.controller !== null", timeout=15000)
            comprobar("el service worker se instala igual", True)
            ctx.set_offline(True)
            pag.reload()
            pag.wait_for_selector("text=Bloques", timeout=15000)
            comprobar("y sigue arrancando sin conexión", pag.locator(".tarjeta__codigo").count() == 4)
            ctx.set_offline(False)
            comprobar("sin errores de página", not errores, "; ".join(errores[:3]))
            nav.close()
    except Exception as e:  # noqa: BLE001
        comprobar("la demo carga entera detrás de la clave", False, str(e))
    finally:
        p.terminate()
        p.wait()


if __name__ == "__main__":
    raise SystemExit(main())
