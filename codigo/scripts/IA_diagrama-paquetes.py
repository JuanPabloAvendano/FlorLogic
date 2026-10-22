"""REGISTRO DE IA · NO ES FUENTE
Generado por: Claude · 21-oct-2026, a pedido de Juan · arma IA_DIAGRAMA-paquetes.drawio desde los paquetes del commit
Estado de revisión: SIN REVISAR
Manda por encima: el código en el commit congelado · la palabra de Juan

Paquetes UML anidados (carpetas en blanco y negro), una página por parte: general, finca.ingesta, módulos esqueleto,
contrato y simulador. Aplica las reglas de la skill archify (modo `tree`, `references/authoring-defaults.md`,
`repository-authoring.md` y `authoring-contract.md` «Tree») sin su renderizador:

  A1 Solo contención: una carpeta dentro de otra es «está contenido en», nunca llamada ni dependencia.
  A2 Una raíz y un solo padre por nodo; los hijos en orden estable (capas: dominio → servicios → infraestructura).
  A3 Cada paquete lleva una subetiqueta corta con su responsabilidad: la primera frase de su package-info.
  A4 Evidencia congelada: se lee el commit (git), no la copia de trabajo; origen sin credenciales y SHA de 40
     caracteres en la cabecera; cada paquete y cada clase enlaza a su archivo y línea en ese commit.
  A5 Nombrar lo incierto: un paquete sin clases lo dice («solo package-info»), no se inventa.
  A6 Plegar no borra: una carpeta cerrada dice en qué página se abre; entre todas las páginas está el árbol entero.
  A7 Título con el alcance y subtítulo que dice cómo leer.
  A8 Legible en escritorio: ancho ≤ 1240 px, o ≤ 1790 px si el ancho es ≥ 1,55 × el alto; texto ≥ 11 px.
  A9 Se verifica antes de entregar: si una regla falla, no se escribe el archivo y se dice cuál.

Uso (desde codigo/):  python3 -I scripts/IA_diagrama-paquetes.py . IA_DIAGRAMA-paquetes.drawio [commit]
"""
import copy, math, os, re, subprocess, sys
from xml.sax.saxutils import quoteattr

CODIGO, SALIDA = sys.argv[1], sys.argv[2]
REPO = subprocess.run(["git", "-C", CODIGO, "rev-parse", "--show-toplevel"], capture_output=True, text=True).stdout.strip()
COMMIT = sys.argv[3] if len(sys.argv) > 3 else subprocess.run(
    ["git", "-C", REPO, "rev-parse", "HEAD"], capture_output=True, text=True).stdout.strip()
ORIGEN = re.sub(r"(https?://)[^@/]+@", r"\1", subprocess.run(
    ["git", "-C", REPO, "remote", "get-url", "origin"], capture_output=True, text=True).stdout.strip())
WEB = ORIGEN[:-4] if ORIGEN.endswith(".git") else ORIGEN
MODULOS = ["backend-finca", "contrato", "simulador", "servicios-en-linea"]
ORDEN = ["dominio", "servicios", "infraestructura", "casosuso", "puertos", "entrada", "salida"]

TAB_H, PAD, GAP, FUENTE, SUB, LINEA_CLASE = 28, 22, 20, 16, 11, 17
SUB_ANCHO = 230          # ancho máximo de una subetiqueta antes de partirla en líneas
ANCHO_MAX, ANCHO_ANCHO, PROPORCION = 1240, 1790, 1.55   # A8


# ── Lectura del commit (A4) ────────────────────────────────────────────────────────────────────────────────────────
def git(*args):
    return subprocess.run(["git", "-C", REPO, *args], capture_output=True, text=True, check=True).stdout


def archivo(ruta):
    return git("show", f"{COMMIT}:{ruta}")


def primera_frase(texto):
    m = re.search(r"/\*\*(.*?)\*/", texto, re.S)
    if not m:
        return ""
    t = re.sub(r"\s*\*\s?", " ", m.group(1))
    t = re.sub(r"\{@\w+\s+([^}]*)\}", r"\1", t)
    t = re.sub(r"<[^>]+>", "", t)
    t = re.sub(r"\s+", " ", t).strip()
    return re.split(r"(?<=[.:;])\s", t)[0].rstrip(".:;")


def recortar(t, n=100):
    """El recorte va después de quitar lo repetido, para no cortar justo la parte propia del paquete."""
    return t if len(t) <= n else t[:n - 3].rsplit(" ", 1)[0] + "…"


def declaracion(texto):
    for n, l in enumerate(texto.split("\n"), 1):
        m = re.match(r"\s*public\s+(?:sealed\s+|final\s+|abstract\s+)*(record|enum|interface|class)\s+\w+", l)
        if m:
            return m.group(1), n
    return "class", 1


class Nodo:
    def __init__(self, nombre, ruta):
        self.nombre, self.ruta, self.hijos, self.clases = nombre, ruta, {}, []
        self.sub, self.fuente, self.cerrado, self.pagina = "", None, False, None


def arbol():
    raiz = Nodo("", "")
    rutas = git("ls-tree", "-r", "--name-only", COMMIT, *[f"codigo/{m}/src/main/java" for m in MODULOS]).split()
    for ruta in sorted(rutas):
        if not ruta.endswith(".java"):
            continue
        base = ruta.split("/src/main/java/")[0] + "/src/main/java/"
        partes = ruta[len(base):].split("/")
        n = raiz
        for k, p in enumerate(partes[:-1]):
            if p not in n.hijos:
                n.hijos[p] = Nodo(p, base + "/".join(partes[:k + 1]))
            n = n.hijos[p]
        texto = archivo(ruta)
        if partes[-1] == "package-info.java":
            n.sub = primera_frase(texto)
            n.fuente = (ruta, 1, texto.count("\n"))
        else:
            tipo, linea = declaracion(texto)
            n.clases.append((partes[-1][:-5], tipo, (ruta, linea, linea)))
    def completar(n, nivel=0):
        if n.fuente is None and n.clases:
            n.fuente = n.clases[0][2]
        if n.fuente is None and nivel in (1, 2):   # co y florlogic abarcan varios módulos: los declara el pom padre
            n.fuente = ("codigo/pom.xml", 22, 28)
        elif n.fuente is None and n.ruta:          # carpeta sin package-info ni clases: su propio directorio
            n.fuente = (n.ruta, 0, 0)
        if not n.clases and not any(True for _ in n.hijos) and not n.sub:
            n.sub = "Vacío"
        for h in n.hijos.values():
            completar(h, nivel + 1)
    completar(raiz)
    return raiz


def enlace(fuente):
    ruta, desde, hasta = fuente
    if desde == 0:
        return f"{WEB}/tree/{COMMIT}/{ruta}"
    return f"{WEB}/blob/{COMMIT}/{ruta}#L{desde}" + (f"-L{hasta}" if hasta > desde else "")


# ── Medida y dibujo ───────────────────────────────────────────────────────────────────────────────────────────────
def tab_w(t):
    return max(40, int(len(t) * FUENTE * 0.58) + 26)


def lineas_sub(t):
    return max(1, math.ceil(len(t) * SUB * 0.55 / SUB_ANCHO)) if t else 0


def sin_repetir(sub, del_padre):
    """Quita el tramo que repite la subetiqueta de un ancestro (archify: no repetir lo que ya se lee arriba)."""
    if not sub or not del_padre:
        return sub
    partes, previas = sub.split(" · "), set(del_padre.split(" · "))
    return " · ".join(x for x in partes if x not in previas)


def subtexto(n, marcado=False):
    if n.cerrado:
        aviso = f"→ se abre en la pág. {n.pagina}" if n.pagina else "→ cerrado en esta página"
        return f"{n.sub} · {aviso}" if n.sub else aviso
    if not marcado and not n.clases and not any(h.clases for h in hojas(n)):   # A5, una sola vez por rama
        return (n.sub + " · " if n.sub else "") + "solo package-info, sin clases todavía (todo lo de adentro)"
    return n.sub


def hojas(n):
    out = []
    for h in n.hijos.values():
        out.append(h)
        out += hojas(h)
    return out


class Pagina:
    def __init__(self, nombre, pid, holgura):
        self.nombre, self.id, self.celdas, self.n, self.holgura = nombre, pid, [], 0, holgura
        self.revision = []   # (id, x, y, w, h, padre) para la verificación A8/A9

    def nid(self):
        self.n += 1
        return f"{self.id}-{self.n}"

    def medir(self, nodo, sub_ancestros="", marcado=False):
        hijos = [] if nodo.cerrado else sorted(
            nodo.hijos.values(), key=lambda h: (ORDEN.index(h.nombre) if h.nombre in ORDEN else len(ORDEN), h.nombre))
        if not nodo.sub and not nodo.cerrado and len(nodo.hijos) != 1:   # sin package-info: sube lo común (al más bajo)
            subs = [h.sub.split(" · ") for h in hojas(nodo) if h.sub]
            comun = [x for x in subs[0] if all(x in o for o in subs[1:])] if len(subs) > 1 else []
            nodo.sub = " · ".join(comun)
        propio = copy.copy(nodo)
        propio.sub = recortar(sin_repetir(nodo.sub, sub_ancestros))
        nodo.texto_sub = subtexto(propio, marcado)
        marca_aqui = marcado or "solo package-info" in nodo.texto_sub
        for h in hijos:
            self.medir(h, " · ".join(x for x in (sub_ancestros, nodo.sub) if x), marca_aqui)
        sub_h = 15 * lineas_sub(nodo.texto_sub)
        sub_w = min(SUB_ANCHO, len(nodo.texto_sub) * SUB * 0.55 + 8) if nodo.texto_sub else 0
        items = []
        if nodo.clases and not nodo.cerrado:
            ancho = max(len(c[0]) + len(c[1]) + 3 for c in nodo.clases) * 7.3 + 20
            items.append(("clases", ancho, LINEA_CLASE * len(nodo.clases) + 10))
        items += [(h, h.w, h.h) for h in hijos]
        arriba = TAB_H + 10 + (sub_h + 8 if sub_h else 0)
        tw = tab_w(nodo.nombre)
        nodo.pos = []
        if not items:
            nodo.w, nodo.h = max(tw + 50, sub_w + 2 * PAD, 150), arriba + PAD
            return
        area = sum((w + GAP) * (h + GAP) for _, w, h in items)
        tope = max(max(w for _, w, _ in items), math.sqrt(area) * self.holgura)
        filas, fila, ancho = [], [], 0
        for it in items:
            if fila and ancho + GAP + it[1] > tope:
                filas.append(fila)
                fila, ancho = [], 0
            ancho += (GAP if fila else 0) + it[1]
            fila.append(it)
        filas.append(fila)
        y, ancho_max = arriba + 6, 0
        for fila in filas:
            x, alto = PAD, 0
            for it, w, h in fila:
                nodo.pos.append((it, x, y, w, h))
                x += w + GAP
                alto = max(alto, h)
            ancho_max = max(ancho_max, x - GAP)
            y += alto + GAP
        nodo.w = max(ancho_max + PAD, tw + 40, sub_w + 2 * PAD)
        nodo.h = y - GAP + PAD

    def celda(self, valor, estilo, x, y, w, h, padre, tooltip=None, link=None):
        i = self.nid()
        geo = f'<mxGeometry x="{x:.0f}" y="{y:.0f}" width="{w:.0f}" height="{h:.0f}" as="geometry"/>'
        if tooltip or link:
            extra = (f" tooltip={quoteattr(tooltip)}" if tooltip else "") + (f" link={quoteattr(link)}" if link else "")
            self.celdas.append(f'<UserObject label={quoteattr(valor)}{extra} id="{i}"><mxCell style="{estilo}" '
                               f'vertex="1" parent="{padre}">{geo}</mxCell></UserObject>')
        else:
            self.celdas.append(f'<mxCell id="{i}" value={quoteattr(valor)} style="{estilo}" vertex="1" '
                               f'parent="{padre}">{geo}</mxCell>')
        self.revision.append((i, x, y, w, h, padre))
        return i

    def dibujar(self, nodo, x, y, padre="1"):
        estilo = (f"shape=folder;tabWidth={tab_w(nodo.nombre)};tabHeight={TAB_H};tabPosition=left;html=0;"
                  "whiteSpace=wrap;fillColor=#FFFFFF;strokeColor=#000000;strokeWidth=2;fontColor=#000000;"
                  f"fontFamily=Helvetica;fontSize={FUENTE};align=left;verticalAlign=top;spacingLeft=10;spacingTop=-1;"
                  "container=1;collapsible=0;recursiveResize=0;")
        tip = f"{nodo.ruta.split('/src/main/java/')[-1].replace('/', '.')}"
        if nodo.fuente:
            tip += f"\nFuente: {nodo.fuente[0]}" + (f":{nodo.fuente[1]}" if nodo.fuente[1] else "/") + f" @ {COMMIT[:7]}"
        i = self.celda(nodo.nombre, estilo, x, y, nodo.w, nodo.h, padre, tip, enlace(nodo.fuente) if nodo.fuente else None)
        if nodo.texto_sub:
            st = (f"text;html=0;whiteSpace=wrap;align=left;verticalAlign=top;fontFamily=Helvetica;fontSize={SUB};"
                  "fontStyle=2;fontColor=#000000;spacing=0;")
            self.celda(nodo.texto_sub, st, PAD, TAB_H + 8, nodo.w - 2 * PAD, 15 * lineas_sub(nodo.texto_sub), i)
        for it, cx, cy, w, h in nodo.pos:
            if it == "clases":
                st = "rounded=0;html=0;fillColor=none;strokeColor=#000000;dashed=1;container=1;collapsible=0;"
                caja = self.celda("", st, cx, cy, w, h, i)
                for k, (nombre, tipo, fuente) in enumerate(nodo.clases):
                    stc = ("text;html=0;align=left;verticalAlign=middle;fontFamily=Courier New;fontSize=12;"
                           "fontColor=#000000;spacingLeft=6;")
                    self.celda(f"{nombre} «{tipo}»", stc, 0, 5 + k * LINEA_CLASE, w, LINEA_CLASE, caja,
                               f"Fuente: {fuente[0]}:{fuente[1]} @ {COMMIT[:7]}", enlace(fuente))
            else:
                self.dibujar(it, cx, cy, i)
        return i

    def texto(self, valor, x, y, w, h, tam, negrita=False):
        st = (f"text;html=0;whiteSpace=wrap;align=left;verticalAlign=top;fontFamily=Helvetica;fontSize={tam};"
              f"fontColor=#000000;fontStyle={1 if negrita else 0};spacing=0;")
        return self.celda(valor, st, x, y, w, h, "1")

    def nota(self, valor, x, y, w, h):
        st = ("shape=note;size=14;html=0;whiteSpace=wrap;align=left;verticalAlign=top;spacingLeft=8;spacingTop=4;"
              "fontSize=11;fontFamily=Helvetica;fillColor=#FFFFFF;strokeColor=#000000;fontColor=#000000;")
        return self.celda(valor, st, x, y, w, h, "1")

    def xml(self):
        return (f'<diagram name={quoteattr(self.nombre)} id="{self.id}"><mxGraphModel grid="0" page="0" '
                f'background="#F5F5F5" math="0" shadow="0"><root><mxCell id="0"/><mxCell id="1" parent="0"/>'
                f'{"".join(self.celdas)}</root></mxGraphModel></diagram>')


SUBTITULO = ("Una carpeta dentro de otra significa «está contenido en», no «llama a» ni «depende de». "
             "Clic en una carpeta o clase: abre su archivo en el commit. Pasa el ratón para ver la fuente.")


def pagina(nombre, pid, raiz, titulo, cabecera=False):
    tope = list(raiz.hijos.values())[0]
    for holgura in (3.0, 2.6, 2.2, 1.9, 1.6, 1.4, 1.2, 1.0, 0.85, 0.7, 0.55, 0.45, 0.35, 0.25):   # A8: se compacta hasta caber
        p = Pagina(nombre, pid, holgura)
        p.medir(tope)
        alto_total = tope.h + 150 + (100 if cabecera else 0)
        limite = ANCHO_ANCHO if tope.w >= PROPORCION * alto_total else ANCHO_MAX
        if tope.w + 40 <= limite:
            break
    p.limite = limite
    y = 20
    p.texto(titulo, 20, y, min(tope.w, 1100), 26, 20, True)
    p.texto(SUBTITULO, 20, y + 32, min(tope.w, 1100), 34, 12)
    y += 80
    if cabecera:
        p.nota("REGISTRO DE IA · NO ES FUENTE\n"
               "Generado por: Claude · 21-oct-2026, a pedido de Juan · paquetes de codigo/ (src/main/java), reglas de archify\n"
               "Estado de revisión: SIN REVISAR\n"
               f"Manda por encima: el código en {ORIGEN} @ {COMMIT} · la palabra de Juan",
               20, y, min(max(tope.w, 760), 1100), 76)
        y += 92
    p.dibujar(tope, 20, y)
    p.ancho_total, p.alto_total = tope.w + 40, y + tope.h + 20
    return p


def podar(raiz, ruta, solo=None):
    r = copy.deepcopy(raiz)
    n = r
    for p in ruta.split("/"):
        n.hijos = {p: n.hijos[p]}
        n = n.hijos[p]
    if solo is not None:
        n.hijos = {k: v for k, v in n.hijos.items() if k in solo}
    return r, n


# ── Páginas ────────────────────────────────────────────────────────────────────────────────────────────────────────
raiz = arbol()
florlogic = raiz.hijos["co"].hijos["florlogic"]
finca_mods = sorted(florlogic.hijos["finca"].hijos)
esqueleto = [m for m in finca_mods if m != "ingesta"]
PAG = {"ingesta": 2, "esqueleto": 3, "contrato": 4, "simulador": 5}
paginas = []

g, fl = podar(raiz, "co/florlogic")
for m in fl.hijos.values():
    for h in m.hijos.values():
        h.cerrado = True
        h.pagina = PAG.get(h.nombre) if m.nombre != "finca" else (PAG["ingesta"] if h.nombre == "ingesta" else PAG["esqueleto"])
    if m.nombre in PAG:
        for h in m.hijos.values():
            h.pagina = PAG[m.nombre]
paginas.append(pagina("1 · General", "general", g, "FlorLogic · paquetes Java · vista general", cabecera=True))

f, finca = podar(raiz, "co/florlogic/finca")
for k, h in finca.hijos.items():
    if k != "ingesta":
        h.cerrado, h.pagina = True, PAG["esqueleto"]
paginas.append(pagina("2 · finca.ingesta", "ingesta", f, "backend-finca · módulo ingesta, completo"))

f, _ = podar(raiz, "co/florlogic/finca", solo=set(esqueleto))
paginas.append(pagina("3 · finca · módulos esqueleto", "esqueleto", f,
                      f"backend-finca · los {len(esqueleto)} módulos que aún no tienen clases"))

for nombre in ("contrato", "simulador"):
    r, _ = podar(raiz, f"co/florlogic/{nombre}")
    paginas.append(pagina(f"{PAG[nombre]} · {nombre}", nombre, r, f"{nombre} · paquetes y clases"))


# ── Verificación antes de entregar (A2, A4, A8, A9) ───────────────────────────────────────────────────────────────
fallos = []
def revisar_nodo(n, camino):
    if n.nombre and n.fuente is None:
        fallos.append(f"A4 sin fuente: {camino}")
    for h in n.hijos.values():
        revisar_nodo(h, camino + "." + h.nombre)
revisar_nodo(florlogic, "co.florlogic")
for p in paginas:
    if p.ancho_total > p.limite:
        fallos.append(f"A8 {p.nombre}: {p.ancho_total:.0f} px > {p.limite} px")
    hijos = {}
    for i, x, y, w, h, padre in p.revision:
        hijos.setdefault(padre, []).append((i, x, y, w, h))
    for padre, cajas in hijos.items():
        for a in range(len(cajas)):
            for b in range(a + 1, len(cajas)):
                _, x1, y1, w1, h1 = cajas[a]
                _, x2, y2, w2, h2 = cajas[b]
                if x1 < x2 + w2 - 1 and x2 < x1 + w1 - 1 and y1 < y2 + h2 - 1 and y2 < y1 + h1 - 1:
                    fallos.append(f"A9 {p.nombre}: se solapan {cajas[a][0]} y {cajas[b][0]}")
if fallos:
    print("NO SE ESCRIBIÓ: falla la verificación\n  " + "\n  ".join(fallos[:20]))
    sys.exit(1)

with open(SALIDA, "w", encoding="utf-8") as fh:
    fh.write('<mxfile host="drawio">' + "".join(p.xml() for p in paginas) + "</mxfile>\n")
print(f"ok {SALIDA} · {len(paginas)} páginas · commit {COMMIT[:7]}")
for p in paginas:
    print(f"  {p.nombre}: {p.ancho_total:.0f}×{p.alto_total:.0f} px (límite {p.limite})")
