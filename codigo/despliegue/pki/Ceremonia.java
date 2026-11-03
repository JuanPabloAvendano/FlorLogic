// La ceremonia de la CA de la finca (TEC-16): crea la raíz fuera de línea, firma el certificado del listener de
//      OpenBao (paso 1) y firma el CSR de la intermedia que deja openbao-init (paso 2). Solo el JDK y su keytool.
// Quién: IA (Claude), 1-nov-2026, a pedido de Juan (HTTPS en todo el nodo, A-44) · Estado: REVISADO POR JUAN 1-nov-2026
// Manda por encima: despliegue/espec/ESPEC-esqueleto-tx1.md A-44…A-51, A-52…A-60 · la palabra de Juan
//
// Uso (desde codigo/, con la clave de la raíz en FLORLOGIC_RAIZ_CLAVE, nunca en la línea de órdenes):
//   java despliegue/pki/Ceremonia.java raiz   <custodia> <nodo>    paso 1: raíz + certificado de OpenBao
//   java despliegue/pki/Ceremonia.java firmar <custodia> <nodo>    paso 2: <nodo>/intermedia.csr → intermedia.crt
//   java despliegue/pki/Ceremonia.java hoja   <custodia> <destino> <nombre> <nombres> <días>   solo pruebas
// <custodia> guarda la llave de la raíz: en la finca es la máquina fuera de línea y nunca llega al nodo (A-45).
// <nodo> es la carpeta que Compose lee como FLORLOGIC_PKI: raiz.crt, openbao.crt, openbao.key, el CSR y la intermedia.
// <nombres> separados por comas; los que parecen una IPv4 van como IP y el resto como DNS (A-50).

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Ceremonia {

    static final String VARIABLE_CLAVE = "FLORLOGIC_RAIZ_CLAVE";
    static final int DIAS_RAIZ = 3650;        // 10 años (COH-50)
    static final int DIAS_INTERMEDIA = 1825;  // 5 años (COH-50)
    static final int DIAS_OPENBAO = 730;      // 2 años (COH-50)

    public static void main(String[] argumentos) throws Exception {
        if (argumentos.length < 3) {
            uso();
        }
        if (System.getenv(VARIABLE_CLAVE) == null || System.getenv(VARIABLE_CLAVE).isBlank()) {
            falla("falta " + VARIABLE_CLAVE + " (la clave del almacén de la raíz)");
        }
        Path custodia = Path.of(argumentos[1]);
        switch (argumentos[0]) {
            case "raiz" -> raiz(custodia, Path.of(argumentos[2]));
            case "firmar" -> firmar(custodia, Path.of(argumentos[2]));
            case "hoja" -> {
                if (argumentos.length != 6) {
                    uso();
                }
                hoja(custodia, Path.of(argumentos[2]), argumentos[3], argumentos[4], Integer.parseInt(argumentos[5]),
                        "serverAuth,clientAuth");
            }
            default -> uso();
        }
    }

    /** Paso 1: la raíz (una vez por finca) y el certificado del listener de OpenBao, firmado por ella (A-46). */
    static void raiz(Path custodia, Path nodo) throws Exception {
        Path almacen = custodia.resolve("raiz.p12");
        if (Files.exists(almacen)) {
            falla(almacen + " ya existe: la raíz se crea una sola vez por finca");
        }
        Files.createDirectories(custodia);
        Files.createDirectories(nodo);
        keytool("-genkeypair", "-alias", "raiz", "-keyalg", "EC", "-groupname", "secp256r1",
                "-dname", "CN=FlorLogic raiz de la finca", "-validity", String.valueOf(DIAS_RAIZ),
                "-ext", "bc:c=ca:true", "-ext", "ku:c=keyCertSign,cRLSign",
                "-keystore", almacen.toString(), "-storetype", "PKCS12", "-storepass:env", VARIABLE_CLAVE);
        privado(almacen);
        keytool("-exportcert", "-rfc", "-alias", "raiz", "-keystore", almacen.toString(), "-storepass:env",
                VARIABLE_CLAVE, "-file", nodo.resolve("raiz.crt").toString());
        // Solo servidor: backend y openbao-init se autentican ante OpenBao con AppRole, no con certificado (A-48).
        hoja(custodia, nodo, "openbao", "openbao,localhost,127.0.0.1", DIAS_OPENBAO, "serverAuth");
        System.out.println("raíz creada en " + almacen + "; raiz.crt, openbao.crt y openbao.key en " + nodo);
    }

    /** Paso 2: firma el CSR de la intermedia; su llave nació en OpenBao y no sale de ahí (A-45, A-46). */
    static void firmar(Path custodia, Path nodo) throws Exception {
        Path csr = nodo.resolve("intermedia.csr");
        if (!Files.exists(csr)) {
            falla("no hay " + csr + ": primero «docker compose up» para que openbao-init lo deje");
        }
        keytool("-gencert", "-rfc", "-alias", "raiz", "-keystore", custodia.resolve("raiz.p12").toString(),
                "-storepass:env", VARIABLE_CLAVE, "-infile", csr.toString(),
                "-outfile", nodo.resolve("intermedia.crt").toString(), "-validity", String.valueOf(DIAS_INTERMEDIA),
                "-ext", "bc:c=ca:true,pathlen:0", "-ext", "ku:c=keyCertSign,cRLSign");
        System.out.println("intermedia firmada: " + nodo.resolve("intermedia.crt"));
    }

    /** Un certificado de hoja firmado por la raíz, con su llave en PEM (PKCS#8): <nombre>.crt y <nombre>.key. */
    static void hoja(Path custodia, Path destino, String nombre, String nombres, int dias, String usos) throws Exception {
        Files.createDirectories(destino);
        Path temporal = Files.createTempDirectory("ceremonia");
        try {
            Path almacen = temporal.resolve("hoja.p12");
            Path pedido = temporal.resolve("hoja.csr");
            keytool("-genkeypair", "-alias", "hoja", "-keyalg", "EC", "-groupname", "secp256r1", "-dname", "CN=" + nombre,
                    "-keystore", almacen.toString(), "-storetype", "PKCS12", "-storepass:env", VARIABLE_CLAVE);
            keytool("-certreq", "-alias", "hoja", "-keystore", almacen.toString(), "-storepass:env", VARIABLE_CLAVE,
                    "-file", pedido.toString());
            keytool("-gencert", "-rfc", "-alias", "raiz", "-keystore", custodia.resolve("raiz.p12").toString(),
                    "-storepass:env", VARIABLE_CLAVE, "-infile", pedido.toString(),
                    "-outfile", destino.resolve(nombre + ".crt").toString(), "-validity", String.valueOf(dias),
                    "-ext", "san=" + alternativos(nombres), "-ext", "eku=" + usos, "-ext", "ku:c=digitalSignature",
                    "-ext", "bc:c=ca:false");
            KeyStore llaves = KeyStore.getInstance("PKCS12");
            char[] clave = System.getenv(VARIABLE_CLAVE).toCharArray();
            try (InputStream entrada = Files.newInputStream(almacen)) {
                llaves.load(entrada, clave);
            }
            PrivateKey llave = (PrivateKey) llaves.getKey("hoja", clave);
            Path archivo = destino.resolve(nombre + ".key");
            Files.writeString(archivo, "-----BEGIN PRIVATE KEY-----\n"
                    + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(llave.getEncoded())
                    + "\n-----END PRIVATE KEY-----\n");
            privado(archivo);
        } finally {
            try (var archivos = Files.list(temporal)) {
                for (Path archivo : archivos.toList()) {
                    Files.delete(archivo);
                }
            }
            Files.delete(temporal);
        }
    }

    /** «openbao,localhost,127.0.0.1» → «dns:openbao,dns:localhost,ip:127.0.0.1». */
    static String alternativos(String nombres) {
        List<String> san = new ArrayList<>();
        for (String nombre : nombres.split(",")) {
            String limpio = nombre.strip();
            if (!limpio.isEmpty()) {
                san.add((limpio.matches("\\d{1,3}(\\.\\d{1,3}){3}") ? "ip:" : "dns:") + limpio);
            }
        }
        return String.join(",", san);
    }

    static void keytool(String... argumentos) throws IOException, InterruptedException {
        List<String> orden = new ArrayList<>();
        orden.add(Path.of(System.getProperty("java.home"), "bin", "keytool").toString());
        orden.addAll(List.of(argumentos));
        Process proceso = new ProcessBuilder(orden).redirectErrorStream(true).start();
        String salida = new String(proceso.getInputStream().readAllBytes());
        if (proceso.waitFor() != 0) {
            falla("keytool " + argumentos[0] + " falló: " + salida.strip());
        }
    }

    /** Solo el dueño lee la llave (en la finca, la de la raíz no sale de la custodia). */
    static void privado(Path archivo) throws IOException {
        try {
            Files.setPosixFilePermissions(archivo, PosixFilePermissions.fromString("rw-------"));
        } catch (UnsupportedOperationException sinPosix) {
            // Windows: sin permisos POSIX; la custodia la cuida el operador.
        }
    }

    static void uso() {
        falla("uso: raiz <custodia> <nodo> · firmar <custodia> <nodo> · hoja <custodia> <destino> <nombre> <nombres> <días>");
    }

    static void falla(String mensaje) {
        System.err.println("ceremonia: " + mensaje);
        System.exit(1);
    }
}

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Propósito: la ceremonia de la autoridad certificadora (CA) de la finca: crea la raíz, firma el certificado de
 * OpenBao y firma la intermedia que generó openbao-init. Sin bibliotecas: solo el JDK y su keytool.
 * Uso: lo corre el operador a mano, desde codigo/, con la clave en la variable FLORLOGIC_RAIZ_CLAVE (ver la cabecera):
 * «raiz» una vez por finca antes del primer «docker compose up»; «firmar» cuando openbao-init deja intermedia.csr;
 * «hoja» solo lo usan las pruebas. Se ejecuta como archivo suelto: «java Ceremonia.java» compila en memoria y corre
 * (programa de un solo archivo, sin Maven), por eso no tiene package.
 * Origen: la cabecera de arriba (ESPEC-esqueleto-tx1, HTTPS en todo el nodo; TEC-16 es la PKI de la finca).
 * Lo que no está en español en este archivo:
 * - public static void main(String[] argumentos) throws Exception: punto de entrada; throws Exception deja que
 *   cualquier error corte el programa con su traza (es una herramienta de operador, no un servicio).
 * - System.getenv(...): lee una variable de entorno; la clave nunca va como argumento, que se vería en ps o en el
 *   historial del shell.
 * - switch (...) { case "raiz" -> ...; default -> ... }: switch con flecha (Java 14+): sin break y sin caer al
 *   siguiente caso.
 * - Path.of / resolve: rutas del sistema de archivos; resolve("raiz.p12") es «la carpeta más ese nombre».
 * - Files.exists / createDirectories / createTempDirectory / writeString / list / delete: operaciones de archivos de
 *   java.nio; createDirectories no falla si la carpeta ya existe.
 * - keytool -genkeypair -alias raiz -keyalg EC -groupname secp256r1: genera un par de llaves de curva elíptica P-256
 *   y un certificado autofirmado. -dname "CN=...": el nombre del sujeto. -validity: días de vigencia.
 * - -ext bc:c=ca:true: extensión Basic Constraints, crítica (c), que marca el certificado como CA; pathlen:0 en la
 *   intermedia: puede firmar hojas pero no otras CA. -ext ku:c=keyCertSign,cRLSign: Key Usage, solo firmar
 *   certificados y listas de revocación. En las hojas: ku digitalSignature, bc ca:false, eku serverAuth o
 *   serverAuth,clientAuth (Extended Key Usage: para qué lado de TLS sirve) y san (nombres alternativos dns: e ip:).
 * - -keystore ... -storetype PKCS12: el almacén donde queda la llave, en el formato estándar .p12.
 * - -storepass:env FLORLOGIC_RAIZ_CLAVE: keytool lee la clave del almacén de esa variable de entorno, no de la orden.
 * - keytool -exportcert -rfc: saca el certificado público en texto PEM (Base64 con BEGIN/END).
 * - keytool -certreq: crea el CSR (solicitud de firma) de la llave de hoja. keytool -gencert -infile -outfile: la
 *   raíz firma ese CSR y escribe el certificado.
 * - KeyStore.getInstance("PKCS12") + load(entrada, clave) + getKey("hoja", clave): abre el .p12 temporal y saca la
 *   llave privada, porque keytool no sabe exportarla.
 * - PrivateKey.getEncoded(): la llave en PKCS#8 binario; Base64.getMimeEncoder(64, salto) la parte en líneas de 64
 *   caracteres para armar el PEM «BEGIN PRIVATE KEY» que leen Nginx y OpenBao.
 * - try (InputStream entrada = ...) { }: try con recursos; cierra el archivo al salir aunque haya error.
 * - try { ... } finally { ... }: el bloque finally borra la carpeta temporal (con la llave de hoja) pase lo que pase.
 * - var archivos = Files.list(temporal): var deja que el compilador deduzca el tipo (un Stream de Path); toList() lo
 *   vuelve lista.
 * - String... argumentos: parámetro variable; keytool("-a", "-b") llega como arreglo.
 * - limpio.matches("...\\d{1,3}..."): expresión regular de una IPv4 (cuatro grupos de 1 a 3 dígitos); decide ip: o dns:.
 * - System.getProperty("java.home"): la carpeta del JDK que está corriendo; así se usa su keytool y no otro del PATH.
 * - ProcessBuilder(orden).redirectErrorStream(true).start(): lanza keytool juntando su salida de error con la normal;
 *   readAllBytes() la lee completa y waitFor() espera y da el código de salida (distinto de 0 = falló).
 * - Files.setPosixFilePermissions(..., PosixFilePermissions.fromString("rw-------")): permisos 0600, solo el dueño
 *   lee y escribe. UnsupportedOperationException: el sistema de archivos no es POSIX (Windows).
 * - System.err.println + System.exit(1): mensaje a la salida de errores y fin del programa con código 1.
 */
