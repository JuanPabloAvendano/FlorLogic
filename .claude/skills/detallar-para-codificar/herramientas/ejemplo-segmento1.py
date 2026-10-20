import sys
sys.path.insert(0, sys.argv[1])
from drawio import Pagina, guardar

CABECERA = ("IA_ · Diagramas del Segmento 1 del contrato de ingesta (TX-1.K)\n"
            "Quién: IA (Claude), 19-oct-2026, a pedido de Juan · Estado: SIN REVISAR\n"
            "Manda por encima: ESPEC-contrato-ingesta.md (§2, §5, §8) y la palabra de Juan")

# ───────────── Página 1: por qué contrato es un módulo aparte ─────────────
m = Pagina("1 · Por qué contrato está aparte", "mod")
m.nota(CABECERA, 20, 20, 620, 60)
cel = m.paquete("simulador  (el celular)\nPicocli · SQLite", 20, 120, 300, 230, "#e1d5e7")
m.caja("almacén de salida\n(anotaciones capturadas)", 40, 175, 260, 50, "fillColor=#ffffff;")
cli = m.caja("cliente HTTP\narma ParticionRequest\nlee ResultadoParticion (tolerante)", 40, 250, 260, 80, "fillColor=#ffffff;")
fin = m.paquete("backend-finca  (nodo de finca)\nSpring Boot · Modulith", 720, 120, 420, 330, "#d5e8d4")
ctl = m.caja("infraestructura: controlador de ingesta\nlee ParticionRequest (estricto)\narma ResultadoParticion", 740, 175, 380, 70, "fillColor=#ffffff;")
srv = m.caja("servicios: caso de uso de ingreso\n(recibe un comando propio, no el DTO)", 740, 270, 380, 55, "fillColor=#ffffff;")
dom = m.caja("dominio: Anotacion, reglas\nJava puro · NO importa contrato (A-44)", 740, 350, 380, 55, "fillColor=#ffffff;")
con = m.caja("contrato.jar\nPublished Language: la forma del cable\ndto · codigos · lectura · Cabeceras\n(sin Spring, sin base de datos)\nlo usan los dos lados por Maven", 370, 470, 330, 90, "fillColor=#dae8fc;strokeColor=#6c8ebf;fontStyle=1;")
m.flecha(cli, ctl, "doble", "HTTP · JSON\nPOST /ingesta/v1/sesiones/{id}/particiones", salida=(1, 0.3), entrada=(0, 0.5))
m.flecha(ctl, srv, "dependencia", "mapea DTO → comando")
m.flecha(srv, dom, "dependencia", "usa")
m.flecha(cli, con, "dependencia", "depende de", salida=(0.5, 1), entrada=(0, 0.5))
m.flecha(fin, con, "dependencia", "depende de", salida=(0.15, 1), entrada=(1, 0.5))
m.nota("Por qué aparte:\n"
       "• Lo usan DOS programas que se despliegan por separado: el celular y el nodo de finca.\n"
       "• Si viviera dentro de backend-finca, el simulador tendría que depender de todo el backend\n"
       "  (Spring, base de datos) o copiar las clases; una copia se desincroniza y el cable se rompe.\n"
       "• Así, un cambio de forma rompe la compilación de los dos lados a la vez, no en producción.\n"
       "• El dominio no lo ve: el controlador traduce el DTO a un comando propio (frontera hexagonal).\n"
       "Fuentes: PoEAA secc. 100 (las clases del DTO «present on both sides»);\n"
       "DDD Reference p. 43 (Published Language).", 20, 590, 680, 140)

# ───────────── Página 2: clases del Segmento 1 ─────────────
p = Pagina("2 · Segmento 1 · clases", "s1")
p.nota(CABECERA, 20, 20, 620, 60)
p.paquete("dto · IDA  (celular → nodo de finca)", 20, 110, 1080, 780)
p.paquete("dto · VUELTA  (nodo de finca → celular)", 1120, 110, 640, 560, "#eef6e8")
p.paquete("lectura  (barrera: leer nunca lanza, A-57)", 1120, 690, 640, 300, "#fdf3e6")
p.paquete("codigos", 1780, 110, 320, 420)
p.paquete("raíz", 1780, 550, 320, 260)

AZUL, VERDE, NAR, GRIS = ("#dae8fc", "#6c8ebf"), ("#d5e8d4", "#82b366"), ("#ffe6cc", "#d79b00"), ("#f5f5f5", "#666666")

pr = p.clase("«record» ParticionRequest", ["contrato: String", "particionUuid: UUID", "sesionesCaptura: List<SesionCaptura>",
            "anotaciones: List<Anotacion>", "descartes: List<Descarte>", "primerosAcuses: List<PrimerAcuse>"], 40, 150, 320, *AZUL)
sc = p.clase("«record» SesionCaptura", ["uuid: UUID", "personaId: String", "credencialJti: String", "inicio: Instant",
            "versionApp: String", "versionMotor: String"], 40, 400, 250, *AZUL)
de = p.clase("«record» Descarte", ["consecutivo: long", "motivo: Motivo"], 40, 640, 250, *AZUL)
dm = p.clase("«enum · IdDeCatalogo» Descarte.Motivo", ["FALLO_TECNICO", "DESCARTADA_POR_GERENTE", "DESCONOCIDO"], 40, 760, 250, *GRIS)
pa = p.clase("«record» PrimerAcuse", ["anotacionUuid: UUID", "primerAcuseMs: long"], 320, 640, 230, *AZUL)
ls = p.clase("«interna» Listas", ["copia(List): List", "sin null · nunca lanza"], 320, 770, 230, *GRIS)
an = p.clase("«record» Anotacion  (Envelope Wrapper)", ["anotacionUuid: UUID", "consecutivo: long", "tipo: String", "versionTipo: int",
            "sesionCapturaUuid: UUID", "tiempo: Tiempo", "versionConfig: int", "veredictoLocal: VeredictoLocal", "datos: JsonNode"], 420, 150, 320, *AZUL)
vl = p.clase("«enum · IdDeCatalogo» VeredictoLocal", ["ACEPTADA", "MARCADA", "DESCONOCIDO"], 420, 400, 220, *GRIS)
ti = p.clase("«record» Tiempo", ["crudoMs: long", "monoMs: long", "arranque: long", "desfaseDeclarado: Desfase"], 790, 150, 290, *AZUL)
df = p.clase("«sealed» Desfase  (Null Object)", ["Conocido(ms: long)", "Desconocido()", "---", "DESCONOCIDO: Desfase  (static)"], 790, 300, 290, *NAR)
cc = p.clase("«record» tipos.CapturaCampo", ["produccionId, seccionId, campoId: String", "jornada: LocalDate", "valor: JsonNode",
            "motivoAusencia: String", "motivoDesviacion: String", "---", "dato(): Valor"], 600, 560, 300, *AZUL)
va = p.clase("«sealed» tipos.Valor", ["Hay(valor)", "Falta(motivo: Motivo)", "Contradictorio(valor, motivo)  (Special Case)",
            "Motivo = Declarado(id) | SinMotivo"], 600, 770, 300, *NAR)

rp = p.clase("«record» ResultadoParticion", ["particionUuid: UUID", "confirmadoHasta: long", "respaldadoHasta: long", "recibidoMs: long",
            "conteo: Conteo", "excepciones: List<Excepcion>", "descartesAcusados: List<Long>", "accion: Accion", "---",
            "incoherencias(): List<String>  (Notification)"], 1140, 150, 340, *VERDE)
co = p.clase("«record» Conteo", ["recibidas, aplicadas: int", "marcadas, rechazadas: int", "sinEfecto: int", "---", "incoherencias(): List<String>"], 1520, 150, 220, *VERDE)
ac = p.clase("«enum · IdDeCatalogo» Accion", ["REGISTRAR_DE_NUEVO", "DESCONOCIDA"], 1520, 340, 220, *GRIS)
ex = p.clase("«record» Excepcion", ["anotacionUuid: UUID", "resultado: Resultado", "codigos: List<String>"], 1140, 420, 300, *VERDE)
re = p.clase("«enum · IdDeCatalogo» Excepcion.Resultado", ["MARCADA", "RECHAZADA", "SIN_EFECTO", "DESCONOCIDO"], 1140, 540, 300, *GRIS)

ld = p.clase("LectorDto", ["estricto(): LectorDto   (nodo de finca)", "  lee ParticionRequest y CapturaCampo", "tolerante(): LectorDto  (celular)",
            "  lee ResultadoParticion", "---", "leer(JsonNode, Class<T>): Lectura<T>"], 1140, 720, 320, *NAR)
le = p.clase("«sealed» Lectura<T>", ["Leida(valor: T)", "Fallida(pointer, clase)"], 1490, 870, 250, *NAR)
ic = p.clase("«interface» IdDeCatalogo", ["id(): String", "buscar(valores, id, siNo): E  (static)"], 1490, 720, 250, *NAR)

ci = p.clase("«enum · IdDeCatalogo» CodigoIngreso", ["DATOS_INVALIDOS", "INCOMPLETA", "MOTIVO_AUSENTE", "DIVERGENCIA", "HORA_DUDOSA",
            "OBSOLETA", "…  (19 en total, ESPEC §5)", "---", "id(): String", "de(String): Optional"], 1800, 150, 280, *GRIS)
ca = p.clase("Cabeceras  (constantes)", ["AUTHORIZATION, ESQUEMA_BEARER", "TRACEPARENT", "CACHE_CONTROL, NO_STORE", "RETRY_AFTER",
            "TIPO_JSON, TIPO_PROBLEMA"], 1800, 590, 280, *GRIS)

# Composición (rombo = «contiene»)
p.flecha(pr, sc, texto="0..*", salida=(0.3, 1), entrada=(0.5, 0))
p.flecha(pr, an, texto="0..*", salida=(1, 0.5), entrada=(0, 0.3))
p.flecha(pr, de, texto="0..*", salida=(0, 0.8), entrada=(0, 0.3))
p.flecha(pr, pa, texto="0..*", salida=(0.85, 1), entrada=(0.15, 0))
p.flecha(de, dm, texto="1")
p.flecha(an, ti, texto="1", salida=(1, 0.3), entrada=(0, 0.3))
p.flecha(an, vl, texto="1", salida=(0.3, 1), entrada=(0.3, 0))
p.flecha(ti, df, texto="1")
p.flecha(rp, co, texto="1", salida=(1, 0.3), entrada=(0, 0.3))
p.flecha(rp, ex, texto="0..*", salida=(0.3, 1), entrada=(0.3, 0))
p.flecha(rp, ac, texto="0..1", salida=(1, 0.8), entrada=(0, 0.5))
p.flecha(ex, re, texto="1")
# Dependencias (punteada)
p.flecha(an, cc, "dependencia", "datos se lee como", salida=(0.9, 1), entrada=(0.6, 0))
p.flecha(cc, va, "dependencia", "dato()")
p.flecha(an, sc, "dependencia", "sesionCapturaUuid", salida=(0, 0.75), entrada=(1, 0.3))
p.flecha(ex, ci, "dependencia", "codigos[] usa ids de", salida=(1, 0.8), entrada=(0.5, 1))
p.flecha(ld, le, "dependencia", "devuelve")
p.flecha(re, ic, "realiza", "implementa", salida=(1, 0.5), entrada=(0.5, 0))
p.nota("Leyenda\n"
       "◆—▷  contiene (composición), con su multiplicidad\n"
       "- - ▷  depende de / usa\n"
       "- - ▷ (triángulo vacío)  implementa\n"
       "«enum · IdDeCatalogo»: implementan IdDeCatalogo. Un texto desconocido\n"
       "se lee como DESCONOCIDO; CodigoIngreso.de() devuelve Optional vacío (A-57).\n"
       "Azul = ida · verde = vuelta · naranja = lectura y Null Objects · gris = catálogos",
       1800, 830, 300, 160)

guardar(sys.argv[2], [m, p])
print("ok")
