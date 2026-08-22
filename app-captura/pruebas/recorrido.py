#!/usr/bin/env python3
"""
Recorrido completo de la demo, en un navegador de verdad.

    python pruebas/recorrido.py            comprueba
    python pruebas/recorrido.py --fotos    además guarda capturas de pantalla en pruebas/fotos/

Hace lo que va a hacer el supervisor en la sesión: entra, captura una cama dividida, la cierra,
sincroniza, provoca un conflicto, lo resuelve, mira las medidas y recarga la página para
comprobar que nada se perdió. Falla si aparece un error en consola o si alguna pantalla se sale
de ancho en un celular.
"""
from __future__ import annotations

import argparse
import http.server
import socketserver
import subprocess
import sys
import threading
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent
DIST = RAIZ / "dist"
FOTOS = RAIZ / "pruebas" / "fotos"
PUERTO = 8901
ANCHO = 412

fallos: list[str] = []
errores: list[str] = []


def comprobar(nombre: str, condicion: bool, detalle: str = "") -> None:
    if condicion:
        print(f"  ok   {nombre}")
    else:
        fallos.append(nombre)
        print(f" FALLA {nombre}" + (f"\n         {detalle}" if detalle else ""))


def servir() -> socketserver.TCPServer:
    class Silencioso(http.server.SimpleHTTPRequestHandler):
        def __init__(self, *a, **k):
            super().__init__(*a, directory=str(DIST), **k)

        def log_message(self, *_):
            pass

    socketserver.TCPServer.allow_reuse_address = True
    s = socketserver.TCPServer(("127.0.0.1", PUERTO), Silencioso)
    threading.Thread(target=s.serve_forever, daemon=True).start()
    return s


def main() -> int:
    p = argparse.ArgumentParser()
    p.add_argument("--fotos", action="store_true")
    args = p.parse_args()

    if not DIST.exists():
        subprocess.run([sys.executable, str(RAIZ / "construir.py")], check=True)
    if args.fotos:
        FOTOS.mkdir(parents=True, exist_ok=True)

    from playwright.sync_api import sync_playwright

    servidor = servir()
    try:
        with sync_playwright() as pw:
            nav = pw.chromium.launch()
            pag = nav.new_page(viewport={"width": ANCHO, "height": 900}, device_scale_factor=2)
            pag.on("console", lambda m: errores.append(f"consola: {m.text}") if m.type == "error" else None)
            pag.on("pageerror", lambda e: errores.append(f"página: {e}"))

            def foto(nombre: str, completa: bool = False) -> None:
                if args.fotos:
                    pag.screenshot(path=str(FOTOS / f"{nombre}.png"), full_page=completa)

            def sin_desborde(donde: str) -> None:
                ancho = pag.evaluate("document.documentElement.scrollWidth")
                comprobar(f"sin desborde horizontal · {donde}", ancho <= ANCHO, f"la página mide {ancho}px")

            try:
                recorrer(pag, comprobar, sin_desborde, foto)
            except Exception as e:  # noqa: BLE001
                FOTOS.mkdir(parents=True, exist_ok=True)
                pag.screenshot(path=str(FOTOS / "fallo.png"), full_page=True)
                print("\n  Estado al fallar:", pag.locator(".titulo").first.inner_text())
                print("  Botones:", pag.locator("main button").all_inner_texts()[:20])
                fallos.append(f"excepción: {e}")
            nav.close()

    finally:
        servidor.shutdown()

    if errores:
        print("\nErrores del navegador:")
        for e in errores:
            print("   ", e)
    print()
    if fallos or errores:
        print(f"{len(fallos)} comprobación(es) fallaron, {len(errores)} error(es).\n")
        return 1
    print("Recorrido completo, todo en orden.\n")
    return 0


def recorrer(pag, comprobar, sin_desborde, foto) -> None:
            pag.goto(f"http://127.0.0.1:{PUERTO}/")
            pag.wait_for_selector("text=Bloques")

            print("\nArranque")
            comprobar("carga el catálogo real", pag.locator("text=41 registros del formato real").count() == 1)
            comprobar("lista los cuatro bloques", pag.locator(".tarjeta__codigo").count() == 4)
            sin_desborde("bloques")
            foto("01-bloques")

            print("\nCaptura en rejilla — la cama dividida del formato real")
            pag.get_by_text("Bloque 12", exact=True).click()
            pag.wait_for_selector("text=Cama 37")
            pag.get_by_text("Cama 37", exact=True).click()
            pag.wait_for_selector("text=Papel contra app")
            sin_desborde("detalle de la cama")
            foto("02-cama")

            pag.get_by_role("button", name="Capturar · rejilla").click()
            pag.wait_for_selector("text=borrador")
            sin_desborde("captura vacía")

            pag.get_by_role("button", name="+ Sección").click()
            pag.wait_for_selector(".rejilla__fila")
            filas = pag.locator(".rejilla__fila")
            filas.nth(0).locator("select").select_option(label="Lineth")
            filas.nth(0).locator("input").nth(0).fill("148")
            pag.wait_for_selector("text=serían 2.812")
            comprobar("sugiere la cantidad sin escribirla sola",
                      pag.locator(".rejilla__fila").nth(0).locator("input").nth(1).input_value() == "")
            filas.nth(0).locator("input").nth(1).fill("2812")

            pag.get_by_role("button", name="+ Sección").click()
            filas = pag.locator(".rejilla__fila")
            filas.nth(1).locator("select").select_option(label="Cooper")
            filas.nth(1).locator("input").nth(0).fill("21")
            filas.nth(1).locator("input").nth(1).fill("399")
            pag.wait_for_selector("text=3.211 plantas")
            comprobar("suma las dos secciones en vivo (DEC-14)",
                      pag.locator(".pie-captura__total strong").inner_text() == "3.211 plantas")
            sin_desborde("rejilla con dos secciones")
            foto("03-rejilla")

            print("\nReglas duras")
            pag.get_by_role("button", name="+ Sección").click()
            pag.get_by_role("button", name="Cerrar cama").click()
            pag.wait_for_selector("text=Confirmá antes de guardar")
            comprobar("una sección vacía impide cerrar",
                      pag.get_by_role("button", name="Guardar y cerrar").is_disabled())
            comprobar("y dice qué hacer", pag.locator("text=Contá las líneas").count() >= 1)
            foto("04-reglas")
            pag.get_by_role("button", name="Seguir capturando").click()
            pag.wait_for_selector(".rejilla__fila")
            pag.locator(".rejilla__fila").nth(2).get_by_role("button", name="×").click()

            print("\nCierre")
            pag.get_by_role("button", name="Cerrar cama").click()
            pag.wait_for_selector("text=Confirmá antes de guardar")
            comprobar("el resumen muestra el total", pag.locator("text=3.211").count() >= 1)
            sin_desborde("confirmación")
            foto("05-confirmar")
            pag.get_by_role("button", name="Guardar y cerrar").click()
            pag.wait_for_selector("text=Papel contra app")
            comprobar("queda una cama sin enviar", pag.locator("text=1 sin enviar").count() == 1)

            print("\nPapel contra app")
            pag.get_by_role("button", name="Papel contra app").click()
            pag.wait_for_selector("text=Formato en papel")
            comprobar("la app coincide con el papel", pag.locator("text=0%").count() >= 1)
            sin_desborde("papel contra app")
            foto("06-papel", completa=True)

            print("\nSincronización")
            pag.get_by_role("button", name="Bandeja").click()
            pag.wait_for_selector("text=Bandeja de salida")
            pag.get_by_role("button", name="Sincronizar sin señal").click()
            pag.wait_for_selector("text=Nada se perdió")
            comprobar("sin señal no se pierde nada", pag.locator("text=1 sin enviar").count() >= 1)
            pag.get_by_role("button", name="Sincronizar", exact=True).click()
            pag.wait_for_selector("text=1 enviadas")
            comprobar("con señal se envía", pag.locator(".cifra__n").nth(1).inner_text() == "1")
            sin_desborde("bandeja")
            foto("07-bandeja")

            print("\nConflicto: la misma cama capturada dos veces")
            pag.get_by_role("button", name="Bloques").click()
            pag.get_by_text("Bloque 12", exact=True).click()
            pag.get_by_text("Cama 37", exact=True).click()
            pag.get_by_role("button", name="Capturar · guiada").click()
            pag.wait_for_selector("text=Guiada")
            pag.locator(".botonera .boton").filter(has_text="Astroi").first.click()
            pag.locator(".campo__control").nth(0).fill("100")
            pag.locator(".campo__control").nth(1).fill("1900")
            pag.get_by_role("button", name="Agregar sección").click()
            pag.wait_for_selector("text=lista")
            sin_desborde("captura guiada")
            foto("08-guiada")
            pag.get_by_role("button", name="Cerrar cama").click()
            pag.wait_for_selector("text=Confirmá antes de guardar")
            comprobar("avisa que la cama ya se capturó hoy",
                      pag.locator("text=ya se capturó hoy").count() == 1)
            comprobar("y exige que alguien lo mire antes de cerrar",
                      pag.get_by_role("button", name="Guardar y cerrar").is_disabled())
            pag.locator(".confirmar input").check()
            pag.get_by_role("button", name="Guardar y cerrar").click()
            pag.wait_for_selector("text=Papel contra app")

            pag.get_by_role("button", name="Bandeja").click()
            pag.get_by_role("button", name="Sincronizar", exact=True).click()
            pag.wait_for_selector("text=Decide una persona")
            comprobar("el choque no se resuelve solo (DEC-05)",
                      pag.locator("text=Las dos capturas se conservan").count() == 1)
            sin_desborde("conflicto")
            foto("09-conflicto")
            pag.get_by_role("button", name="Queda la de este dispositivo").click()
            pag.wait_for_timeout(300)
            comprobar("resuelto por una persona", pag.locator("text=Decide una persona").count() == 0)

            print("\nMedidas")
            pag.get_by_role("button", name="Medidas").click()
            pag.wait_for_selector("text=camas cerradas")
            comprobar("midió las dos camas", pag.locator(".cifra__n").nth(0).inner_text() == "2")
            comprobar("compara las dos variantes", pag.locator("text=Rejilla contra guiada").count() == 1)
            comprobar("con una fila por variante",
                      pag.locator("table").first.locator("tbody tr").count() == 2)
            comprobar("cuenta los rechazos de regla",
                      int(pag.locator(".cifra__n").nth(4).inner_text()) >= 1)
            sin_desborde("medidas")
            foto("10-medidas", completa=True)

            print("\nDespués de recargar")
            pag.reload()
            pag.wait_for_selector("text=Bloques")
            pag.get_by_role("button", name="Medidas").click()
            pag.wait_for_selector("text=camas cerradas")
            comprobar("las medidas sobreviven a recargar",
                      pag.locator(".cifra__n").nth(0).inner_text() == "2")
            pag.get_by_role("button", name="Datos").click()
            pag.wait_for_selector("text=Reglas cargadas")
            comprobar("las nueve reglas están cargadas", pag.locator("table").last.locator("tbody tr").count() == 9)
            sin_desborde("datos")
            foto("11-datos", completa=True)

            print("\nSin conexión — CN-13")
            pag.evaluate("navigator.serviceWorker.ready.then(() => true)")
            pag.wait_for_function("navigator.serviceWorker.controller !== null")
            pag.context.set_offline(True)
            pag.reload()
            pag.wait_for_selector("text=Bloques", timeout=15000)
            comprobar("la demo arranca sin conexión", pag.locator(".tarjeta__codigo").count() == 4)
            comprobar("y lo dice en la cabecera", pag.locator("text=Sin conexión").count() >= 1)
            pag.get_by_role("button", name="Medidas").click()
            pag.wait_for_selector("text=camas cerradas")
            comprobar("con todo lo capturado intacto",
                      pag.locator(".cifra__n").nth(0).inner_text() == "2")
            foto("13-sin-conexion")
            pag.context.set_offline(False)

            print("\nModo oscuro")
            pag.emulate_media(color_scheme="dark")
            pag.get_by_role("button", name="Bloques").click()
            pag.wait_for_selector("text=Bloques")
            foto("12-oscuro")


if __name__ == "__main__":
    raise SystemExit(main())
