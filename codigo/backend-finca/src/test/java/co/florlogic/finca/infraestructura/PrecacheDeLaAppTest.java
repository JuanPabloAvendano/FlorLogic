package co.florlogic.finca.infraestructura;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

/**
 * El service worker de la PWA (F8) se mantiene a mano desde que la app vive en el nodo (sin {@code construir.py}):
 * esta prueba hace de {@code construir.py}. Sin Spring y sin Docker.
 *
 * <ul>
 * <li>Todo archivo de {@code static/app} está en el precaché, salvo el propio {@code sw.js} y la página de limpieza,
 * que tiene que cargar justo cuando el service worker es el problema.</li>
 * <li>El nombre del caché es la huella de lo precacheado, como la calculaba {@code construir.py}: si un archivo cambia
 * y el nombre no, los celulares se quedan con la versión vieja. El mensaje de la falla dice el nombre correcto.</li>
 * <li>Ningún HTML trae script ni estilo en línea: la CSP de {@link AppDelNodo} los bloquearía.</li>
 * </ul>
 */
class PrecacheDeLaAppTest {

    private static final Path APP = Path.of("src", "main", "resources", "static", "app");
    private static final Set<String> FUERA_DEL_PRECACHE = Set.of("sw.js", "limpiar.html", "limpiar.js");

    @Test
    void todo_archivo_de_la_app_esta_en_el_precache_y_nada_mas() throws IOException {
        Set<String> enDisco = new TreeSet<>(archivosDeLaApp());
        enDisco.removeAll(FUERA_DEL_PRECACHE);

        Set<String> enElPrecache = new TreeSet<>(precache());
        assertThat(enElPrecache).contains("./");
        enElPrecache.remove("./");

        assertThat(enElPrecache).as("ARCHIVOS de sw.js").isEqualTo(enDisco);
    }

    @Test
    void el_nombre_del_cache_es_la_huella_de_lo_precacheado() throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        for (String archivo : new TreeSet<>(precache())) {
            Path ruta = APP.resolve(archivo);
            if (Files.isRegularFile(ruta)) {
                sha256.update(Files.readAllBytes(ruta));
            }
        }
        String esperado = "florlogic-" + HexFormat.of().formatHex(sha256.digest()).substring(0, 12);

        Matcher cache = Pattern.compile("const CACHE = '([^']+)'").matcher(sw());
        assertThat(cache.find()).isTrue();
        assertThat(cache.group(1)).as("cambió un archivo de la app: en sw.js va const CACHE = '%s'", esperado)
                .isEqualTo(esperado);
    }

    @Test
    void ningun_html_de_la_app_trae_script_ni_estilo_en_linea() throws IOException {
        for (String archivo : archivosDeLaApp()) {
            if (!archivo.endsWith(".html")) {
                continue;
            }
            String html = Files.readString(APP.resolve(archivo));
            assertThat(Pattern.compile("<script(?![^>]*\\ssrc=)[^>]*>").matcher(html).find()).as(archivo).isFalse();
            assertThat(html).as(archivo).doesNotContain("<style", " style=", " onclick=", " onload=");
        }
    }

    private static List<String> archivosDeLaApp() throws IOException {
        try (Stream<Path> todos = Files.walk(APP)) {
            return todos.filter(Files::isRegularFile)
                    .map(p -> APP.relativize(p).toString().replace('\\', '/'))
                    .toList();
        }
    }

    private static List<String> precache() throws IOException {
        Matcher lista = Pattern.compile("const ARCHIVOS = \\[([^\\]]*)\\]").matcher(sw());
        assertThat(lista.find()).as("sw.js declara const ARCHIVOS = [...]").isTrue();
        return Pattern.compile("\"([^\"]+)\"").matcher(lista.group(1)).results().map(m -> m.group(1)).toList();
    }

    private static String sw() throws IOException {
        return Files.readString(APP.resolve("sw.js"));
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Jeronimo · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - Path.of("src", "main", …): Maven corre las pruebas con el directorio del módulo (backend-finca) como actual, así
 *   que la ruta es la de los fuentes, no la del .jar.
 * - Files.walk(APP): recorre la carpeta y sus subcarpetas; filter(Files::isRegularFile) deja solo archivos;
 *   relativize + replace('\\', '/'): la ruta relativa con barras normales, como la escribe sw.js, también en Windows.
 * - MessageDigest.getInstance("SHA-256"), update(bytes), digest(): la huella de todos los archivos en orden
 *   alfabético; HexFormat.of().formatHex(…).substring(0, 12): sus 12 primeros dígitos hexadecimales, igual que
 *   hashlib.sha256().hexdigest()[:12] en construir.py.
 * - Pattern.compile(…).matcher(texto): expresiones regulares. "const ARCHIVOS = \\[([^\\]]*)\\]" saca la lista del
 *   service worker; "\"([^\"]+)\"" cada nombre entre comillas; "<script(?![^>]*\\ssrc=)[^>]*>" una etiqueta script
 *   SIN atributo src (eso es un script en línea; (?!…) es «no seguido de»).
 * - TreeSet: conjunto ordenado; isEqualTo compara los dos conjuntos y AssertJ muestra lo que sobra y lo que falta.
 * - .as("…%s…", esperado): el mensaje que sale si la comprobación falla.
 */
