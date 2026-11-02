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
