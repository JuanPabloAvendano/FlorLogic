#!/usr/bin/env python3
"""
FlorLogic — servidor de la demo.

    python servir.py                 http://localhost:8787
    python servir.py --puerto 9000
    python servir.py --clave secreta  pide usuario y clave antes de dejar entrar

Hace falta un servidor (no basta abrir el archivo con doble clic) porque la base de datos del
navegador y el service worker no existen para un archivo `file://`. Sirve en toda la red local,
así que el celular puede abrir la demo con la dirección que se imprime al arrancar.

Aviso para la prueba en el campo: los navegadores solo dejan usar la cámara —el escaneo de la
marca de la cama— en `localhost` o sobre HTTPS. Con la dirección de red local el resto de la demo
funciona completo, pero el escáner no se va a poder abrir.

Sobre el puerto: si el que se pide está ocupado, se prueban los nueve siguientes en vez de fallar.
El puerto por defecto es 8787 y no 8000 a propósito — 8000 lo usa medio mundo, y cada puerto es un
origen distinto para el navegador: si otro proyecto dejó un service worker registrado en
`localhost:8000`, ese service worker sigue respondiendo ahí aunque su servidor ya no exista.
Para limpiar un origen sucio está `http://localhost:PUERTO/limpiar.html`.
"""
from __future__ import annotations

import argparse
import base64
import errno
import hmac
import http.server
import os
import secrets
import socket
import socketserver
from pathlib import Path

DIST = Path(__file__).resolve().parent / "dist"
INTENTOS = 10
USUARIO = "florlogic"

# Cuando la demo sale a internet por el túnel, la URL es pública: cualquiera con el enlace entra.
# Lo que hay del otro lado son datos reales del cliente, así que compartir.py pone clave por
# defecto. En la red local no hace falta y queda en None.
CLAVE: str | None = None


def clave_nueva() -> str:
    """Corta y sin caracteres que se confundan al dictarla o teclearla en un celular."""
    alfabeto = "abcdefghjkmnpqrstuvwxyz23456789"
    return "".join(secrets.choice(alfabeto) for _ in range(8))


def ip_local() -> str | None:
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(("8.8.8.8", 80))
        return s.getsockname()[0]
    except OSError:
        return None
    finally:
        s.close()


class Servidor(socketserver.ThreadingTCPServer):
    """
    Servidor con dos diferencias que importan, y las dos aparecieron en Windows.

    **Un hilo por petición.** `TCPServer` atiende de a una y deja el resto en la cola de escucha,
    que por defecto es de 5. La aplicación se sirve como módulos ES sin empaquetar, así que el
    navegador abre del orden de quince conexiones a la vez al cargar: la cola se desborda y Windows
    responde `ERR_CONNECTION_REFUSED` a las que sobran. En Linux la misma situación aguanta, y por
    eso no se ve en las pruebas del contenedor.

    **Atarse en exclusiva.** `allow_reuse_address` activa SO_REUSEADDR, que en Linux solo sirve para reusar un puerto en
    TIME_WAIT. En **Windows** hace algo distinto y peligroso: deja que un segundo proceso se ate a
    un puerto que YA está escuchando, y a partir de ahí las peticiones caen en uno o en otro sin
    ningún orden. El síntoma es exactamente el que parece imposible de diagnosticar: unos archivos
    cargan, otros fallan, y el puerto «sigue ocupado» por algo que ya cerraste.

    Por eso en Windows se apaga y se pide SO_EXCLUSIVEADDRUSE: si el puerto está tomado, el bind
    falla de una vez —que es lo que queremos, porque entonces saltamos al siguiente puerto.
    """

    daemon_threads = True          # cerrar con Ctrl+C no espera a las conexiones abiertas
    request_queue_size = 128       # cola de escucha holgada, no la de 5 por defecto
    allow_reuse_address = os.name != "nt"

    def server_bind(self) -> None:
        if os.name == "nt":
            try:
                self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_EXCLUSIVEADDRUSE, 1)
            except OSError:
                pass  # versión de Windows que no lo soporta: seguimos con el comportamiento normal
        super().server_bind()


class Manejador(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *a, **k):
        super().__init__(*a, directory=str(DIST), **k)

    def autorizado(self) -> bool:
        if CLAVE is None:
            return True
        esperado = "Basic " + base64.b64encode(f"{USUARIO}:{CLAVE}".encode()).decode()
        # compare_digest en vez de == para no filtrar la clave por el tiempo de comparación.
        return hmac.compare_digest(self.headers.get("Authorization", ""), esperado)

    def pedir_clave(self) -> None:
        self.send_response(401)
        # Solo ASCII: las cabeceras HTTP se codifican en latin-1 y una raya larga las revienta.
        self.send_header("WWW-Authenticate", 'Basic realm="FlorLogic - demo de captura"')
        self.send_header("Content-Type", "text/plain; charset=utf-8")
        cuerpo = f"Esta demo pide clave. Usuario: {USUARIO}\n".encode()
        self.send_header("Content-Length", str(len(cuerpo)))
        self.end_headers()
        self.wfile.write(cuerpo)

    def do_GET(self) -> None:
        if not self.autorizado():
            return self.pedir_clave()
        super().do_GET()

    def do_HEAD(self) -> None:
        if not self.autorizado():
            return self.pedir_clave()
        super().do_HEAD()

    def end_headers(self) -> None:
        # Sin caché del navegador: el que manda es el service worker, no una copia vieja.
        self.send_header("Cache-Control", "no-cache")
        super().end_headers()

    def log_message(self, formato: str, *args) -> None:
        if "GET" in (formato % args) and " 200 " not in (formato % args):
            print("   ", formato % args)


def responde_alguien(puerto: int) -> bool:
    """¿Hay algo escuchando ya en ese puerto? Se comprueba antes de atarse, no después."""
    s = socket.socket()
    s.settimeout(0.3)
    try:
        return s.connect_ex(("127.0.0.1", puerto)) == 0
    finally:
        s.close()


def abrir(puerto_pedido: int) -> tuple[socketserver.TCPServer, int]:
    """Devuelve el servidor y el puerto que consiguió. Prueba los siguientes si está ocupado."""
    ultimo: OSError | None = None
    for puerto in range(puerto_pedido, puerto_pedido + INTENTOS):
        if responde_alguien(puerto):
            print(f"  puerto {puerto} ya tiene algo escuchando, probando el siguiente…")
            continue
        try:
            return Servidor(("0.0.0.0", puerto), Manejador), puerto
        except OSError as e:
            if e.errno not in (errno.EADDRINUSE, getattr(errno, "WSAEADDRINUSE", 10048)):
                raise
            print(f"  puerto {puerto} ocupado por otro programa, probando el siguiente…")
            ultimo = e
    raise SystemExit(
        f"Los puertos {puerto_pedido}–{puerto_pedido + INTENTOS - 1} están ocupados.\n"
        f"  Elegí otro:  python servir.py --puerto 9500\n"
        f"  ({ultimo})"
    )


def main(argv: list[str] | None = None) -> int:
    global CLAVE
    p = argparse.ArgumentParser(description="Sirve dist/ para la demo")
    p.add_argument("--puerto", type=int, default=8787)
    p.add_argument("--clave", help="Exige usuario y clave (usuario: florlogic)")
    args = p.parse_args(argv)
    CLAVE = args.clave

    if not DIST.exists():
        raise SystemExit("No hay dist/. Corré primero:  python construir.py")

    servidor, puerto = abrir(args.puerto)
    with servidor:
        ip = ip_local()
        print("\nFlorLogic — demo de captura")
        print(f"  En este equipo   http://localhost:{puerto}")
        if ip:
            print(f"  En el celular    http://{ip}:{puerto}   (misma red wifi)")
        if CLAVE:
            print(f"  Usuario {USUARIO} · clave {CLAVE}")
        print(f"  Si algo quedó raro   http://localhost:{puerto}/limpiar.html")
        # El PID a la vista: si esta ventana se cierra sin Ctrl+C, el servidor sigue vivo y hay
        # que poder matarlo sin salir a buscar quién tiene tomado el puerto.
        print(f"\n  Ctrl+C para parar.  Si se queda colgado:  taskkill /PID {os.getpid()} /F\n")
        try:
            servidor.serve_forever()
        except KeyboardInterrupt:
            print("\nListo.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
