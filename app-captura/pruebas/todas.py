#!/usr/bin/env python3
"""
Corre todas las pruebas de la demo.

    python pruebas/todas.py

  1. catálogo     — lo que muestra la app coincide con el formato de papel del cliente
  2. reglas       — duras y blandas se comportan como dice configuracion/reglas.v1.json
  3. servidor     — aguanta que el navegador pida todos los módulos a la vez
  4. compartir    — el túnel de Cloudflare y la clave, con un cloudflared de mentiras
  5. persistencia — lo capturado sobrevive a recargar la página (navegador real)
  6. recorrido    — la sesión completa: capturar, cerrar, sincronizar, conflicto, medidas

Las dos primeras necesitan Node 22; la tercera solo Python; las dos últimas playwright y tsc.
"""
from __future__ import annotations

import shutil
import subprocess
import sys
from pathlib import Path

RAIZ = Path(__file__).resolve().parent.parent


def hay(programa: str) -> bool:
    return shutil.which(programa) is not None


def hay_playwright() -> bool:
    return subprocess.run([sys.executable, "-c", "import playwright"], capture_output=True).returncode == 0


def correr(titulo: str, orden: list[str]) -> bool:
    print(f"\n{'=' * 70}\n{titulo}\n{'=' * 70}")
    return subprocess.run(orden, cwd=RAIZ).returncode == 0


def main() -> int:
    resultados: dict[str, bool | None] = {}

    if hay("node"):
        resultados["catálogo"] = correr(
            "1 · Catálogo contra el formato de papel",
            ["node", "--experimental-strip-types", "pruebas/catalogo.prueba.ts"])
        resultados["reglas"] = correr(
            "2 · Motor de reglas",
            ["node", "--experimental-strip-types", "pruebas/reglas.prueba.ts"])
    else:
        resultados["catálogo"] = resultados["reglas"] = None
        print("\nSin Node 22: se saltan las pruebas 1 y 2.")

    resultados["servidor"] = correr(
        "3 · Servidor: carga en paralelo y puerto ocupado",
        [sys.executable, "pruebas/servidor.py"])

    resultados["compartir"] = correr(
        "4 · Túnel de Cloudflare y clave de acceso",
        [sys.executable, "pruebas/compartir.py"])

    if hay_playwright() and hay("tsc"):
        resultados["persistencia"] = correr(
            "5 · Persistencia con recarga de página",
            [sys.executable, "pruebas/correr_persistencia.py"])
        resultados["recorrido"] = correr(
            "6 · Recorrido completo de la demo",
            [sys.executable, "pruebas/recorrido.py"])
    else:
        resultados["persistencia"] = resultados["recorrido"] = None
        print("\nSin playwright o sin tsc: se saltan las pruebas 3 y 4.")
        print("  pip install playwright && playwright install chromium")
        print("  npm i -g typescript")

    print(f"\n{'=' * 70}\nResumen")
    for nombre, ok in resultados.items():
        print(f"  {'pasa ' if ok else 'SALTADA' if ok is None else 'FALLA'}  {nombre}")
    fallaron = [n for n, ok in resultados.items() if ok is False]
    print()
    return 1 if fallaron else 0


if __name__ == "__main__":
    raise SystemExit(main())
