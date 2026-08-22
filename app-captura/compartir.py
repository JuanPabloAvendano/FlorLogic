#!/usr/bin/env python3
"""
FlorLogic — compartir la demo por internet.

    python compartir.py                 servidor + túnel, con clave generada
    python compartir.py --sin-clave     sin clave (solo si la vas a mandar y borrar enseguida)
    python compartir.py --clave florlog
    python compartir.py --puerto 9000

Levanta el servidor local y abre un túnel de Cloudflare, que devuelve una dirección
`https://algo.trycloudflare.com` accesible desde cualquier parte. No hace falta cuenta de
Cloudflare ni tocar el router.

Dos cosas que gana la demo al salir por el túnel, y que no son menores:

  · **HTTPS de verdad.** El navegador solo da la cámara en `localhost` o sobre HTTPS, así que
    **el escaneo de la marca de la cama recién funciona por acá** — con la IP de la red local, no.
    Lo mismo vale para instalarla como aplicación en el celular.
  · Se puede enseñar a distancia, sin que el cliente esté en la misma wifi.

Y una que hay que tener presente: **la dirección es pública**. Cualquiera con el enlace entra, y lo
que hay del otro lado son datos reales de la finca. Por eso se pone clave por defecto. El túnel se
cierra cuando cerrás este programa, y la dirección deja de existir.
"""
from __future__ import annotations

import argparse
import os
import queue
import re
import shutil
import signal
import subprocess
import sys
import threading
import time
from pathlib import Path

RAIZ = Path(__file__).resolve().parent
DIRECCION = re.compile(r"https://[a-z0-9-]+\.trycloudflare\.com")
ESPERA_SEGUNDOS = 45

CANDIDATOS_WINDOWS = [
    r"C:\Program Files (x86)\cloudflared\cloudflared.exe",
    r"C:\Program Files\cloudflared\cloudflared.exe",
    os.path.expandvars(r"%LOCALAPPDATA%\Microsoft\WinGet\Links\cloudflared.exe"),
]

COMO_INSTALAR = """
No encuentro `cloudflared`, que es lo que abre el túnel.

  Windows   winget install --id Cloudflare.cloudflared
  macOS     brew install cloudflared
  Linux     https://developers.cloudflare.com/cloudflare-one/connections/connect-networks/downloads/

Después volvé a correr:  python compartir.py

Mientras tanto, para la red local no hace falta nada:  python servir.py
"""


def encontrar_cloudflared() -> str | None:
    ruta = shutil.which("cloudflared")
    if ruta:
        return ruta
    for candidato in CANDIDATOS_WINDOWS:
        if candidato and Path(candidato).exists():
            return candidato
    return None


def main() -> int:
    p = argparse.ArgumentParser(description="Comparte la demo por un túnel de Cloudflare")
    p.add_argument("--puerto", type=int, default=8787)
    p.add_argument("--clave", help="Clave para entrar (si no, se genera una)")
    p.add_argument("--sin-clave", action="store_true", help="Dejar la demo abierta a cualquiera")
    p.add_argument("--espera", type=float, default=ESPERA_SEGUNDOS,
                   help="Segundos a esperar la dirección del túnel antes de rendirse")
    args = p.parse_args()

    # Que un `taskkill` o un cierre de ventana también cierre el túnel, no solo Ctrl+C.
    try:
        signal.signal(signal.SIGTERM, lambda *_: (_ for _ in ()).throw(KeyboardInterrupt()))
    except (ValueError, AttributeError, OSError):
        pass  # algunos Windows y los hilos secundarios no lo permiten

    if not (RAIZ / "dist").exists():
        raise SystemExit("No hay dist/. Corré primero:  python construir.py")

    cloudflared = encontrar_cloudflared()
    if not cloudflared:
        raise SystemExit(COMO_INSTALAR)

    sys.path.insert(0, str(RAIZ))
    import servir  # noqa: PLC0415

    clave = None if args.sin_clave else (args.clave or servir.clave_nueva())
    servir.CLAVE = clave

    servidor, puerto = servir.abrir(args.puerto)
    threading.Thread(target=servidor.serve_forever, daemon=True).start()
    print(f"\nServidor local en http://localhost:{puerto}")
    print("Abriendo el túnel de Cloudflare…")

    proceso = subprocess.Popen(
        [cloudflared, "tunnel", "--url", f"http://localhost:{puerto}", "--no-autoupdate"],
        stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, bufsize=1,
    )

    # Las líneas se leen en otro hilo para poder rendirse por TIEMPO. Contando líneas no sirve:
    # si cloudflared se cuelga sin escribir nada, un bucle sobre su salida espera para siempre.
    assert proceso.stdout is not None
    cola: queue.Queue[str | None] = queue.Queue()

    def bombear() -> None:
        for linea in proceso.stdout:  # type: ignore[union-attr]
            cola.put(linea)
        cola.put(None)

    threading.Thread(target=bombear, daemon=True).start()

    direccion = None
    murio = False
    try:
        limite = time.monotonic() + args.espera
        while time.monotonic() < limite:
            try:
                linea = cola.get(timeout=0.4)
            except queue.Empty:
                continue
            if linea is None:
                murio = True
                break
            hallazgo = DIRECCION.search(linea)
            if hallazgo:
                direccion = hallazgo.group(0)
                break

        if not direccion:
            proceso.terminate()
            servidor.shutdown()
            servidor.server_close()
            razon = "cloudflared se cerró solo" if murio else f"pasaron {args.espera:.0f} s sin respuesta"
            raise SystemExit(
                f"cloudflared no devolvió ninguna dirección ({razon}).\n"
                "  · Revisá que haya internet y que no lo esté bloqueando el firewall.\n"
                "  · Para la red local no hace falta el túnel:  python servir.py"
            )

        print("\n" + "─" * 62)
        print("  FlorLogic — demo de captura, accesible desde cualquier parte")
        print("─" * 62)
        print(f"\n  {direccion}\n")
        if clave:
            print(f"  Usuario   {servir.USUARIO}")
            print(f"  Clave     {clave}\n")
        else:
            print("  SIN CLAVE: cualquiera con el enlace entra. Son datos reales de la finca.\n")
        print("  Al ser HTTPS, acá sí funcionan el escaneo de la cama y la instalación")
        print("  como aplicación en el celular.")
        print("\n  La dirección vive mientras esta ventana esté abierta.")
        print("  Ctrl+C para cerrar el túnel.\n")
        print("─" * 62 + "\n")

        # A partir de acá solo hacemos de eco de lo que diga cloudflared si algo se rompe.
        while True:
            linea = cola.get()
            if linea is None:
                print("  cloudflared se cerró. El enlace dejó de funcionar.")
                break
            if any(x in linea for x in ("ERR", "error", "failed")):
                print("  cloudflared:", linea.rstrip())
    except KeyboardInterrupt:
        print("\nCerrando el túnel…")
    finally:
        proceso.terminate()
        try:
            proceso.wait(timeout=5)
        except subprocess.TimeoutExpired:
            proceso.kill()
        servidor.shutdown()
        servidor.server_close()
    print("Listo. La dirección ya no existe.\n")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
