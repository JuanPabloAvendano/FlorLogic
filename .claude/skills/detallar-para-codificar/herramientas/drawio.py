"""Mini generador de diagramas draw.io (UML de clases y cajas), sin HTML en las etiquetas."""
from xml.sax.saxutils import quoteattr

class Pagina:
    def __init__(self, nombre, id_):
        self.nombre, self.id, self.celdas, self.n = nombre, id_, [], 0
    def _id(self):
        self.n += 1
        return f"{self.id}-{self.n}"
    def caja(self, texto, x, y, w, h, estilo=""):
        i = self._id()
        e = "rounded=0;whiteSpace=wrap;html=0;" + estilo
        self.celdas.append(f'<mxCell id="{i}" value={quoteattr(texto)} style="{e}" vertex="1" parent="1"><mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry"/></mxCell>')
        return i
    def paquete(self, texto, x, y, w, h, color="#f5f5f5"):
        return self.caja(texto, x, y, w, h, f"fillColor={color};strokeColor=#999999;dashed=1;verticalAlign=top;align=left;spacingLeft=8;fontStyle=1;fontSize=13;")
    def nota(self, texto, x, y, w, h):
        return self.caja(texto, x, y, w, h, "shape=note;size=14;fillColor=#fff2cc;strokeColor=#d6b656;align=left;verticalAlign=top;spacingLeft=6;fontSize=11;")
    def clase(self, titulo, filas, x, y, w, color="#dae8fc", borde="#6c8ebf"):
        i = self._id()
        h = 30 + 20 * len(filas)
        st = (f"swimlane;fontStyle=1;childLayout=stackLayout;horizontal=1;startSize=30;horizontalStack=0;"
              f"resizeParent=1;collapsible=0;marginBottom=0;html=0;whiteSpace=wrap;fillColor={color};strokeColor={borde};fontSize=12;")
        self.celdas.append(f'<mxCell id="{i}" value={quoteattr(titulo)} style="{st}" vertex="1" parent="1"><mxGeometry x="{x}" y="{y}" width="{w}" height="{h}" as="geometry"/></mxCell>')
        for k, f in enumerate(filas):
            j = self._id()
            sep = f == "---"
            if sep:
                s = "line;strokeWidth=1;fillColor=none;align=left;verticalAlign=middle;spacingTop=-1;spacingLeft=3;spacingRight=3;rotatable=0;labelPosition=right;points=[];portConstraint=eastwest;"
                f = ""
            else:
                s = "text;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;spacingLeft=6;overflow=hidden;rotatable=0;html=0;fontFamily=Courier New;fontSize=11;"
            self.celdas.append(f'<mxCell id="{j}" value={quoteattr(f)} style="{s}" vertex="1" parent="{i}"><mxGeometry y="{30 + 20 * k}" width="{w}" height="20" as="geometry"/></mxCell>')
        return i
    def flecha(self, a, b, tipo="composicion", texto="", salida=None, entrada=None):
        estilos = {
            "composicion": "endArrow=open;endFill=0;startArrow=diamondThin;startFill=1;startSize=14;",
            "dependencia": "endArrow=open;endFill=0;dashed=1;",
            "realiza": "endArrow=block;endFill=0;dashed=1;endSize=12;",
            "flujo": "endArrow=block;endFill=1;strokeWidth=2;",
            "doble": "endArrow=block;endFill=1;startArrow=block;startFill=1;strokeWidth=2;",
        }
        e = "edgeStyle=orthogonalEdgeStyle;rounded=1;html=0;fontSize=11;labelBackgroundColor=#ffffff;" + estilos[tipo]
        if salida: e += f"exitX={salida[0]};exitY={salida[1]};exitDx=0;exitDy=0;"
        if entrada: e += f"entryX={entrada[0]};entryY={entrada[1]};entryDx=0;entryDy=0;"
        i = self._id()
        self.celdas.append(f'<mxCell id="{i}" value={quoteattr(texto)} style="{e}" edge="1" parent="1" source="{a}" target="{b}"><mxGeometry relative="1" as="geometry"/></mxCell>')
    def xml(self):
        cuerpo = "".join(self.celdas)
        return (f'<diagram name={quoteattr(self.nombre)} id="{self.id}"><mxGraphModel grid="1" gridSize="10" page="0" '
                f'math="0" shadow="0"><root><mxCell id="0"/><mxCell id="1" parent="0"/>{cuerpo}</root></mxGraphModel></diagram>')

def guardar(ruta, paginas):
    with open(ruta, "w", encoding="utf-8") as f:
        f.write('<mxfile host="drawio">' + "".join(p.xml() for p in paginas) + "</mxfile>\n")
