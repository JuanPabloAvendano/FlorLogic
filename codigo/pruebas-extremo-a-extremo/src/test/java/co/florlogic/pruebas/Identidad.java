package co.florlogic.pruebas;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Un token de operador del Keycloak del nodo (CT-2), pedido por el gateway como lo pediría el simulador: credenciales
 * de cliente del cliente {@code simulador}. Uno nuevo en cada entrega: los tokens duran cinco minutos y algunas pruebas
 * duran más.
 */
final class Identidad {

    static final String TOKEN = "/auth/realms/florlogic/protocol/openid-connect/token";
    private static final Pattern ACCESO = Pattern.compile("\"access_token\"\\s*:\\s*\"([^\"]+)\"");

    private Identidad() {
    }

    static String tokenDeOperador(HttpClient http, String base, String secreto) throws IOException, InterruptedException {
        HttpResponse<String> r = http.send(HttpRequest.newBuilder(URI.create(base + TOKEN))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&client_id=simulador&client_secret="
                        + URLEncoder.encode(secreto, StandardCharsets.UTF_8)))
                .build(), HttpResponse.BodyHandlers.ofString());
        Matcher acceso = ACCESO.matcher(r.body());
        if (r.statusCode() != 200 || !acceso.find()) {
            throw new IllegalStateException("Keycloak no dio token por el gateway (" + r.statusCode() + "): " + r.body());
        }
        return acceso.group(1);
    }
}
