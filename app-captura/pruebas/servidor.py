#!/usr/bin/env python3
"""
Pruebas del servidor de la demo.

    python pruebas/servidor.py

La aplicación se sirve como módulos ES sin empaquetar: al cargar, el navegador abre del orden de
quince conexiones a la vez. Un servidor de un solo hilo con la cola de escucha por defecto (5)
responde `ERR_CONNECTION_REFUSED` a las que sobran — en Windows se ve, en Linux normalmente no.
Estas dos pruebas cubren ese caso y el del puerto ocupado.
"""
from __future__ import annotations

import concurrent.futures as cf
import socket
import subprocess
import sys
import time
import urllib.request
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
RUTAS = [
    "/", "/estilos.css", "/seed.json", "/reglas.v1.json", "/manifest.webmanifest",
    "/icono-192.png", "/icono-512.png", "/sw.js", "/limpiar.html",
    "/js/main.js", "/js/catalogo.js", "/js/reglas.js", "/js/repositorio.js", "/js/vista.js",
    "/js/almacen.js", "/js/id.js", "/js/modelo.js", "/js/tipos.js", "/js/metricas.js",
    "/js/escaner.js", "/js/sincronizacion.js", "/js/ui/comun.js", "/js/ui/bandeja.js",
    "/js/ui/captura.js", "/js/ui/catalogoPantallas.js", "/js/ui/datos.js", "/js/ui/medidas.js",
]

fallos: list[str] = []


def comprobar(nombre: str, ok: bool, detalle: str = "") -> None:
    print(f"{'  ok  ' if ok else ' FALLA'} {nombre}" + (f"\n         {detalle}" if not ok and detalle else ""))
    if not ok:
        fallos.append(nombre)


def arrancar(puerto: int) -> subprocess.Popen:
    p = subprocess.Popen([sys.executable, "-u", "servir.py", "--puerto", str(puerto)],
                         cwd=RAIZ, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
    time.sleep(1.5)
    return p


def main() -> int:
    print("\nTodo el grafo de módulos pedido de golpe, como hace el navegador")
    p = arrancar(8893)
    try:
        def pedir(ruta: str):
            try:
                with urllib.request.urlopen(f"http://127.0.0.1:8893{ruta}", timeout=8) as x:
                    return ruta, x.status, None
            except Exception as e:  # noqa: BLE001
                return ruta, None, f"{type(e).__name__}: {e}"

        malas: list[str] = []
        for _ in range(3):
            with cf.ThreadPoolExecutor(max_workers=len(RUTAS)) as ex:
                for ruta, estado, error in ex.map(pedir, RUTAS):
                    if error or estado != 200:
                        malas.append(f"{ruta} → {error or estado}")
        comprobar(f"{len(RUTAS) * 3} peticiones en paralelo, ninguna rechazada",
                  not malas, "; ".join(malas[:5]))

        print("\nUna conexión a medio hablar no puede bloquear a las demás")
        colgada = socket.create_connection(("127.0.0.1", 8893))
        colgada.sendall(b"GET /seed.json HTTP/1.1\r\nHost: x\r\n")  # falta la línea en blanco
        time.sleep(0.4)
        try:
            with urllib.request.urlopen("http://127.0.0.1:8893/js/main.js", timeout=4) as x:
                comprobar("el servidor atiende en paralelo", x.status == 200)
        except Exception as e:  # noqa: BLE001
            comprobar("el servidor atiende en paralelo", False, f"quedó bloqueado: {e}")
        colgada.close()
    finally:
        p.terminate()
        p.wait()

    print("\nPuerto ocupado: salta al siguiente en vez de atarse encima")
    ocupado = arrancar(8894)
    segundo = arrancar(8894)
    try:
        with urllib.request.urlopen("http://127.0.0.1:8895/", timeout=4) as x:
            comprobar("el segundo servidor se fue al 8895", x.status == 200)
    except Exception as e:  # noqa: BLE001
        comprobar("el segundo servidor se fue al 8895", False, str(e))
    finally:
        for proc in (ocupado, segundo):
            proc.terminate()
            proc.wait()

    print()
    if fallos:
        print(f"{len(fallos)} comprobación(es) fallaron.\n")
        return 1
    print("Servidor en orden.\n")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
