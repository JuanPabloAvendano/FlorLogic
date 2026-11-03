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

/*
 * ── Guía de lectura · IA (Claude), 3-nov-2026, a pedido de Juan · SIN REVISAR ──
 * Lo que no está en español en este archivo:
 * - /auth/realms/florlogic/protocol/openid-connect/token: el punto de Keycloak que entrega tokens, aquí por el
 *   gateway (prefijo /auth).
 * - grant_type=client_credentials&client_id=simulador&client_secret=…: flujo «credenciales de cliente» de OAuth
 *   2.0; el cliente simulador se identifica con su secreto.
 * - "Content-Type", "application/x-www-form-urlencoded": el cuerpo va como formulario, lo que exige ese punto.
 * - URLEncoder.encode(secreto, UTF_8): escapa el secreto para el formulario.
 * - Pattern.compile("\"access_token\"\\s*:\\s*\"([^\"]+)\""): expresión regular que saca el token de la
 *   respuesta; [^\"]+ es «todo hasta la siguiente comilla».
 */
