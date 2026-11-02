package co.florlogic.simulador.infraestructura.salida.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * El cliente HTTP del simulador confía en la raíz de la CA de la finca (A-44, A-51 b): el nodo solo habla HTTPS y su
 * certificado lo firma esa raíz, que no está en el almacén de Java. La raíz llega por la variable
 * {@code FLORLOGIC_CA_RAIZ} (la ruta del {@code raiz.crt} de la ceremonia). Nunca se desactiva la verificación: sin la
 * variable, se usa el almacén de Java y un nodo de la finca no se deja hablar.
 */
final class ConfianzaDeLaFinca {

    static final String VARIABLE = "FLORLOGIC_CA_RAIZ";

    private ConfianzaDeLaFinca() {
    }

    /** El cliente del simulador: con la raíz de la variable, si está. */
    static HttpClient cliente() {
        String raiz = System.getenv(VARIABLE);
        return raiz == null || raiz.isBlank() ? HttpClient.newHttpClient() : cliente(Path.of(raiz));
    }

    /** Un cliente que solo confía en esta raíz. Falla al arrancar si el archivo no es un certificado: no hay más que hacer. */
    static HttpClient cliente(Path raiz) {
        try (InputStream entrada = Files.newInputStream(raiz)) {
            KeyStore confianza = KeyStore.getInstance(KeyStore.getDefaultType());
            confianza.load(null, null);
            int i = 0;
            for (Certificate certificado : CertificateFactory.getInstance("X.509").generateCertificates(entrada)) {
                confianza.setCertificateEntry("finca-" + i++, certificado);
            }
            if (i == 0) {
                throw new IllegalStateException(VARIABLE + " no trae ningún certificado: " + raiz);
            }
            TrustManagerFactory gestores = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            gestores.init(confianza);
            SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, gestores.getTrustManagers(), null);
            return HttpClient.newBuilder().sslContext(tls).build();
        } catch (IOException e) {
            throw new UncheckedIOException("no se pudo leer " + VARIABLE + " = " + raiz, e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(VARIABLE + " no es un certificado X.509: " + raiz, e);
        }
    }
}
